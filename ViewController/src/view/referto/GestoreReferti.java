package view.referto;

import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.ComparisonUtils;

import java.io.File;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;

import java.util.Iterator;

import model.common.Parent_AppModule;
import model.common.RefCo_AppModule;
import model.common.RefMa_AppModule;
import model.common.RefPf_AppModule;
import model.common.Ref_AppModule;
import model.common.Ref_SoEndoscopiaViewRow;

import model.commons.ConfigurationConstants;
import model.commons.DomandaQuestionario;
import model.commons.LetteraRefertoCitoBean;
import model.commons.LetteraRefertoPFBean;
import model.commons.ParametriSistema;
import model.commons.Questionario;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import insiel.dataHandling.DateUtils;

import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import model.commons.Lettera;

import model.global.A_SoSoggettoViewRowImpl;

import model.global.common.A_SoSoggScrViewRow;

import model.global.common.A_SoSoggettoViewRow;

import model.inviti.GeneratoreInviti;

import model.referto.Ref_SoAllegatoViewRowImpl;
import model.referto.Ref_SoSoggScrViewRowImpl;
import model.referto.common.Ref_SoRefertomammo1livViewRow;

import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.ApplicationModule;
import oracle.jbo.JboException;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

public class GestoreReferti 
{
	private final Logger logger = Logger.getLogger(getClass().getName());
  private ApplicationModule am;

  private HashMap ulssParams;
  private HashMap templates=new HashMap();
  
  public GestoreReferti(Ref_AppModule _am)
  {
    this.am=_am;
  }
  
   public GestoreReferti(RefCo_AppModule _am)
  {
    this.am=_am;
  }
  
  public GestoreReferti(RefMa_AppModule _am)
  {
    this.am=_am;
  }
  
  public GestoreReferti(RefPf_AppModule _am)
  {
    this.am=_am;
  }
  /**
   * Metodo che crea ad inserisce un nuovo referto ed i rispettivi 
   * dati di adeguatezza e giudizio diagnostico
   * @throws java.lang.Exception
   * @param hpv se true significa che l'azienda adotta il test per HPV
   * @param conf_giudia viewobject in cui inserire il giudizio diagnostico
   * @param conf_adepre viewobject in cui inserire l'adeguatezza
   * @param user operatore connesso
   * @param referti viewobject in cui creare il nuovo referto
   * @param inviti viewobject la cui current row e' l'invito cui fara' riferimento 
   * anche il referto
   */
   public Row nuovoReferto(ViewObject inviti, 
                            ViewObject referti, 
                            String user, 
                            ViewObject conf_adepre,
                            ViewObject conf_giudia,
                            boolean hpv) throws Exception
  {
    Row r=inviti.getCurrentRow();
    if(r==null)
      throw new Exception("Nessun invito da refertare");
    
    BigDecimal vetrino=this.getVetrino((Integer)r.getAttribute("Idinvito"),
                                  (String)r.getAttribute("Tpscr"),
                                  user,hpv);
    
    Row nuovoRef=referti.createRow();
            referti.insertRow(nuovoRef);
            //imposto subito l'id perche' potrei doverlo usare per creare 
            //dati nella tabella di cross
            nuovoRef.setAttribute("Idreferto",((Parent_AppModule)am).getNextIdReferto1liv());
            this.setDatiStandardReferto(nuovoRef,r,user);
            //data del prelievo=data dell'invito
            nuovoRef.setAttribute("Dtprelievo",r.getAttribute("Dtapp"));
             //centro di prelievo=centro dell'invito
            nuovoRef.setAttribute("Idcentroprelievo",r.getAttribute("Idcentroprelievo"));
            //centro di refertazione: derivato dall'invito
            //nuovoRef.setAttribute("Idcentroref",r.getAttribute("Idcentroref1liv"));
            //numero di vetrino preso dall'accettazione
            nuovoRef.setAttribute("Numaccap",vetrino);       
            
            
            //creo anche i campi per l'adeguatezza del preparato e per 
            //il giudizio diagnostico
            
            Row adepre=conf_adepre.createRow();
            conf_adepre.insertRow(adepre);
            adepre.setAttribute("Id",((Parent_AppModule)am).getNextIdConfReferto1liv());
            adepre.setAttribute("Gruppo",ConfigurationConstants.NOME_GRUPPO_ADEPRE);
            adepre.setAttribute("Idreferto",nuovoRef.getAttribute("Idreferto"));
            adepre.setAttribute("Idcnfref",ConfigurationConstants.CODICE_ADEPRE_DEFAULT);
            adepre.setAttribute("Ulss",nuovoRef.getAttribute("Ulss"));
            adepre.setAttribute("Tpscr",nuovoRef.getAttribute("Tpscr"));
            
           
            Row giudia=conf_giudia.createRow();
            conf_giudia.insertRow(giudia);
            giudia.setAttribute("Id",((Parent_AppModule)am).getNextIdConfReferto1liv());
            giudia.setAttribute("Gruppo",ConfigurationConstants.NOME_GRUPPO_GIUDIA);
            giudia.setAttribute("Idreferto",nuovoRef.getAttribute("Idreferto"));
            giudia.setAttribute("Idcnfref",ConfigurationConstants.CODICE_GIUDIA_DEFAULT);
            giudia.setAttribute("Ulss",nuovoRef.getAttribute("Ulss"));
            giudia.setAttribute("Tpscr",nuovoRef.getAttribute("Tpscr"));
            
            //esito hpv: imposto comunque il default
              nuovoRef.setAttribute("EsitoHpv",ConfigurationConstants.DB_FALSE);
              nuovoRef.setAttribute("GrEsitoHpv",ConfigurationConstants.NOME_GRUPPO_ESITO_HPV);
            //vado a lvorare in modo diverso a seconda che sia attivo o meno il test HPV
            if(hpv)
            {
              //data del prelievo=data dell'invito
              nuovoRef.setAttribute("DataPrelievoHpv",r.getAttribute("Dtapp"));
              
              
            }
            else
            {
              //data del referto: per default la imposto ad oggi
              nuovoRef.setAttribute("DataRefertoPap",DateUtils.getOracleDateNow());
            }
            
            
            this.updateEsito(r,am);
            
      return nuovoRef;
  }
  
  public Row creaLetteraDiRefertoCI(Row ref,ViewObject conf_adepre,
                            ViewObject conf_giudia,int livello, Row primol) throws Exception
  {
    return this.creaLetteraDiRefertoCI(ref,conf_adepre, conf_giudia, livello, primol, null);
  }
  
  
  public Row creaLetteraDiRefertoCI(Row ref,ViewObject conf_adepre,
                            ViewObject conf_giudia,int livello, Row primol, String test_proposto) throws Exception
  {
    /*
     * mauro 09/09/2010, inseriti messaggi di logging per problema mancata produzione
     * allegati
     */

    String ulss = (String) ref.getAttribute("Ulss");
    String tpscr = (String) ref.getAttribute("Tpscr");
    System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr +  "/" + getServerTime() + "/creazione singola lettera/inizio metodo" );

    ViewObject lettere=am.findViewObject("Ref_SoCnfLetteracentroView1");
    String sugg;
    Integer idsugg;
    switch(livello)
    {
      case 2:
      {
        sugg="Idsugg2l";
        idsugg=(Integer)ref.getAttribute("Idsugg");
        break;
      }
      case 3:
      {
        sugg="Idsugg3l";
        idsugg=(Integer)ref.getAttribute("Idsugg");
        //devo sostituire l'intervento con il relativo referto
        ViewObject referti=am.findViewObject("Ref_SoRefertocito2livView1");
        referti.setWhereClause("IDREFERTO="+ref.getAttribute("Idreferto"));
        referti.executeQuery();
        ref=referti.first();
        /*Row[] result=referti.getFilteredRows("Idreferto",ref.getAttribute("Idreferto"));
        ref=result[0];*/
        break;
      }
      default://primo livello
      {
        sugg="Idsugg";
        idsugg=(Integer)ref.getAttribute("Idsugg");
        break;
      }
    }
   // System.out.println(sugg+","+ref.getAttribute("Idsugg"));
  /*20080721 MOD: lettere per eta e centro
       lettere.setWhereClause("TPSCR='"+ref.getAttribute("Tpscr")+"' AND "+
    " ULSS='"+ref.getAttribute("Ulss")+"' AND "+sugg+"="+idsugg);
      */
      //soggetto
    ViewObject soggetti=am.findViewObject("A_SoSoggettoView1");
    soggetti.setWhereClause("CODTS='"+ref.getAttribute("Codts")+"' AND A_SoSoggetto.ULSS='"+ref.getAttribute("Ulss")+"'");
    soggetti.executeQuery();
    A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)soggetti.first();
    if(soggetto==null)
      throw new Exception("Il soggetto con tessera sanitaria="+ref.getAttribute("Codts")+" non e' stato trovato nell'azienda sanitaria "+ref.getAttribute("Ulss"));    
 
   //clacolo l'eta' del soggetto al momento della chiusura del referto, cioe' adesso
   int eta=ViewHelper.etaCompiuta(new java.util.Date(),soggetto.getDataNascita().getValue());
   Integer cc= null;///////slot==null? null:slot.getIdcentro();
 
      String whereLettere = "TPSCR='"+ref.getAttribute("Tpscr")+"' AND "+
    " ULSS='"+ref.getAttribute("Ulss")+"' AND "+sugg+"="+idsugg+
      " and   nvl(eta_da,0)<="+eta+
      " and   nvl(eta_a,200)>="+eta;
      
      if (test_proposto != null)
      {
        whereLettere += " and (test_proposto = '"+test_proposto+"' or test_proposto is null) ";
      }
      
     lettere.setWhereClause(whereLettere);
      
      //ordino in base al criterio dell'eta' 8prendo il range piu' piccolo che la contiene)
      //e poi, eventualmente, per centro
      lettere.setOrderByClause("("+eta+"-nvl(eta_da,0))+(nvl(eta_a,200)-"+eta+")");//,centro");
      
        /*20080721 FINE MOD*/
    lettere.executeQuery();
    if(lettere.getRowCount()==0)
       return null;
    Row lettera=lettere.first();
    
    //controllo se ho gia' memorizzato il template
    Lettera l=(Lettera)this.templates.get(lettera.getAttribute("Codtempl"));
    if(l==null){
      ViewObject templates=am.findViewObject("Ref_SoTemplateView1");
      templates.setWhereClause("CODTEMPL="+lettera.getAttribute("Codtempl"));
      templates.executeQuery();
      if(templates.first()==null)
          throw new Exception("Richiesto e non trovato il template con codice "+lettera.getAttribute("Codtempl"));
          
     //creo l'oggetto lettera che mi servira' per creare il pdf
    //  l=new Lettera((BlobDomain)templates.first().getAttribute("Filexml"),"Referto"+System.currentTimeMillis());
    l=new Lettera(templates.first(),"Ref_SoTemplateView1","Filexml","Compiled","Referto"+System.currentTimeMillis(), ConfigurationConstants.FORMATO_PDF);
    this.templates.put(lettera.getAttribute("Codtempl"),l);
    }

    l.setParametersMap((String)ref.getAttribute("Ulss"),(String)ref.getAttribute("Tpscr"),
        am.findViewObject("A_SoAziendaView1"),
        am.findViewObject("A_SoCnfPartemplateView1"));
    
    
    //trovo i record per adeguatezza e giudizio diagnostico
    Row adepre;
    if(conf_adepre==null)
    {
      adepre=null;
    }
    else{
      conf_adepre.setWhereClause("Ref_SoCodref1livc.TPSCR='"+(String)ref.getAttribute("Tpscr")+"' AND Ref_SoCodref1livc.ULSS='"+(String)ref.getAttribute("Ulss")+
      "' AND Ref_SoCodref1livc.GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ADEPRE+"'");
      conf_adepre.executeQuery();
      Row[] result=conf_adepre.getFilteredRows("Idreferto",ref.getAttribute("Idreferto"));
      
      if(result.length>0)
        adepre=result[0];
      else
        adepre=null;
    }
    Row giudia;
    if(conf_giudia==null)
    {
      giudia=null;
    }
    else{
      conf_giudia.setWhereClause("Ref_SoCodref1livc.TPSCR='"+(String)ref.getAttribute("Tpscr")+"' AND Ref_SoCodref1livc.ULSS='"+(String)ref.getAttribute("Ulss")+
      "' AND Ref_SoCodref1livc.GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_GIUDIA+"'");
      conf_giudia.executeQuery();
      Row[] result=conf_giudia.getFilteredRows("Idreferto",ref.getAttribute("Idreferto"));
      
      if(result.length>0)
        giudia=result[0];
      else
        giudia=null;
    }
    Integer id_primol;
    if(primol==null)
      id_primol=null;
    else
      id_primol=(Integer)primol.getAttribute("Idinvito");
    
    //creo il bean
    LetteraRefertoCitoBean lb;
    switch(livello)
    {

      case 2:
      case 3:
      {
        lb=this.createLetteraBean2L((String)ref.getAttribute("Codts"),
                                              (String)ref.getAttribute("Ulss"),
                                              (String)ref.getAttribute("Tpscr"),
                                              ref,
                                              livello,
                                              id_primol);
        break;
      }
      
      default: //primo livello
      {
        lb=this.createLetteraBean1L((String)ref.getAttribute("Codts"),
                                              (String)ref.getAttribute("Ulss"),
                                              (String)ref.getAttribute("Tpscr"),
                                              ref,
                                              adepre,
                                              giudia);
        break;
      }
    }
    
    
                                              
    //creo l'allegato
    File pdf=l.createLetter(lb);

    System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr +  "/" + getServerTime() + "/creazione singola lettera/pdf creato" );
    
    //creo il record per salvare l'allegato
    ViewObject all=am.findViewObject("Ref_SoAllegatoView1");
    Row allegato=all.createRow();
    all.insertRow(allegato);
    Integer id=((Parent_AppModule)am).getNextIdAllegato();
    allegato.setAttribute("Idallegato",id);
    allegato.setAttribute("Codts",ref.getAttribute("Codts"));
    allegato.setAttribute("Dtcreazione",DateUtils.getOracleDateNow());
  // allegato.setAttribute("Idinvito",null);
   allegato.setAttribute("Tpscr",ref.getAttribute("Tpscr"));
   allegato.setAttribute("Ulss",ref.getAttribute("Ulss"));
   allegato.setAttribute("Lettera",BlobUtils.getBlobFromFile(pdf));
   
   pdf.delete();
   
   // mauro 10/02/2010
   l.release();
   //
   
   //così obbligo la transazione a scrivere prima l'allegato cui il referto 
   //fara' poi riferimento (per evitare problemi con le chiavi esterne)
   am.getTransaction().postChanges();

    System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr +  "/" + getServerTime() + "/creazione singola lettera/effettuato postchanges" );
   
   ref.setAttribute("Idallegato",allegato.getAttribute("Idallegato"));
   ref.setAttribute("Dtcreazione",allegato.getAttribute("Dtcreazione"));
  
     return allegato;
                                              
    
    
  }
  
  public Row creaLetteraDiRefertoPF(Row ref, Questionario questionario, Row invito, ViewObject esamiBio, ViewObject esamiPfas) throws Exception
  {
		Integer idAllegato = null;

			Integer idRilevatore = (Integer)ref.getAttribute("Oprilevazione");
			ViewObject medicoRilevatoreView = am.findViewObject("Ref_SoOpmedicoRilevatoreView1");
			medicoRilevatoreView.setWhereClause("IDOP = " + idRilevatore);
			medicoRilevatoreView.executeQuery();
			Row medicoRilevatoreRow = medicoRilevatoreView.first();
			ViewObject allegatoView = am.findViewObject("Ref_SoAllegatoView1");
			
			Integer idSugg = (Integer)ref.getAttribute("Idsugg");
      String codts = (String) ref.getAttribute("Codts");
      String ulss = (String) ref.getAttribute("Ulss");
      String tpscr = (String) ref.getAttribute("Tpscr");
			
			// Memorizzo l'id dell'allegato vecchio, perche' dovro' cancellarlo alla fine
			Integer idAllegatoOld = (Integer)ref.getAttribute("Idallegato");
			
			// Recupero il soggetto
      ViewObject soggetti=am.findViewObject("A_SoSoggettoView1");
      soggetti.setWhereClause("CODTS='"+ codts +"' AND A_SoSoggetto.ULSS='"+ulss+"'");
      soggetti.executeQuery();
			A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)soggetti.first();
			
			java.util.Date dataNascita = soggetto.getDataNascita().dateValue();
			
			// Calcolo l'eta' del soggetto
      int eta=ViewHelper.etaCompiuta(new java.util.Date(),dataNascita);

			// Recupero il template
			ViewObject letteraCentroView = am.findViewObject("Ref_SoCnfLetteracentroView1");
      String whereLettere = "TPSCR='"+tpscr+"' AND "+
      " ULSS='"+ref.getAttribute("Ulss")+"' AND IDSUGG="+idSugg+
      " and   nvl(eta_da,0)<="+eta+
      " and   nvl(eta_a,200)>="+eta;
      letteraCentroView.setWhereClause(whereLettere);
      letteraCentroView.setOrderByClause("("+eta+"-nvl(eta_da,0))+(nvl(eta_a,200)-"+eta+")");
			letteraCentroView.executeQuery();
			Row letteraCentroRow = letteraCentroView.first();
      if (letteraCentroRow != null)
        System.out.println(letteraCentroRow.getAttribute("Codtempl"));

			Lettera l=(Lettera)this.templates.get(letteraCentroRow.getAttribute("Codtempl"));
        
        if(l==null){
          ViewObject templates=am.findViewObject("Ref_SoTemplateView1");
          templates.setWhereClause("CODTEMPL="+letteraCentroRow.getAttribute("Codtempl"));
          templates.executeQuery();
          if(templates.first()==null)
              throw new Exception("Richiesto e non trovato il template con codice "+letteraCentroRow.getAttribute("Codtempl"));
              
         //creo l'oggetto lettera che mi servira' per creare il pdf
         l=new Lettera(templates.first(),"Ref_SoTemplateView1","Filexml","Compiled","Referto"+System.currentTimeMillis(),ConfigurationConstants.FORMATO_PDF);
         
          this.templates.put(letteraCentroRow.getAttribute("Codtempl"),l);
        }
        
        l.setParametersMap((String)ref.getAttribute("Ulss"),(String)ref.getAttribute("Tpscr"),
        am.findViewObject("A_SoAziendaView1"),
        am.findViewObject("A_SoCnfPartemplateView1"));
		
				// Recupero la modalita' di spedizione
				boolean spedizioneStandard = true;
				ViewObject paramView = am.findViewObject("A_SoCnfParametriSistemaView1");
				paramView.setWhereClause("ULSS = '" + ulss + "' AND TPSCR = '" + tpscr +
					"' AND UPPER(NOME_PARAM) = 'MOD_SPEDIZIONE'");
				paramView.executeQuery();
				Row paramRow = paramView.first();
				if (paramRow != null) {
					spedizioneStandard = "standard".equals((String)paramRow.getAttribute("ValoreParam"));
				}
				
				// Valorizzo il bean per la stampa
				LetteraRefertoPFBean letteraBean = new LetteraRefertoPFBean();
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				
				// Determino l'indirizzo del soggetto
				String indirizzoSoggetto;
				String capSoggetto;
				String comuneSoggetto;
				String provinciaSoggetto;
				if (soggetto.getIndirizzoScr() != null) {
					indirizzoSoggetto = soggetto.getIndirizzoScr();
					capSoggetto = soggetto.getCap();
					comuneSoggetto = soggetto.getDescrizione();
					provinciaSoggetto = soggetto.getCodpr();
				} else if (soggetto.getIndirizzoDom() != null && spedizioneStandard) {
					indirizzoSoggetto = soggetto.getIndirizzoDom();
					capSoggetto = soggetto.getCap1();
					comuneSoggetto = soggetto.getDescrizione1();
					provinciaSoggetto = soggetto.getCodpr1();
				} else {
					indirizzoSoggetto = soggetto.getIndirizzoRes();
					capSoggetto = soggetto.getCap2();
					comuneSoggetto = soggetto.getDescrizione2();
					provinciaSoggetto = soggetto.getCodpr2();
				}
        
        //trovo la tessera sanitaria
        String tesseraSanitaria = SoggUtils.trovaTessera(am,soggetto.getCodts(),soggetto.getUlss());
				
				// Recupero la descrizione del suggerimento
				ViewObject suggView=am.findViewObject("Ref_SoCnfSugg1livView3");
				Row[] suggRows = suggView.findByKey(new Key(new Object[]{new Integer(idSugg), ulss, tpscr}), 1);
				Row suggRow = suggRows[0];
        
        //recupero la descrizione delle classi
        String stileDiVita = ((Integer)ref.getAttribute("StileVita")).toString();
        String alimentazione = ((Integer)ref.getAttribute("Alimentazione")).toString();
        String classeBmi = ((Integer)ref.getAttribute("Bmi")).toString();
        
        ViewObject centri=am.findViewObject("A_SoCentroPrelRefView1");
        Integer centroPrelievo = (Integer)invito.getAttribute("Idcentroprelievo");
        Row[] result=centri.getFilteredRows("Idcentro",centroPrelievo);
        String centro_p=null;
        String indirizzo_centro_p=null;
        String sede_centro_p=null;
        String orari_centro_p=null;
        String tel_centro_p=null;
        if(result.length>0)
        {
          Row cc=result[0];
          centro_p=(String)cc.getAttribute("Descrizione");
          indirizzo_centro_p=(String)cc.getAttribute("IndirizzoRes");
          sede_centro_p=(String)cc.getAttribute("Sede");
          orari_centro_p=(String)cc.getAttribute("Oraritel");
          tel_centro_p=(String)cc.getAttribute("Tel");
        }
				
				letteraBean.setData_stampa(dateFormat.format(new java.util.Date()));
				letteraBean.setData_esame(dateFormat.format(((Date)ref.getAttribute("DataRilevazione")).dateValue()));
				letteraBean.setCognome(soggetto.getCognome());
				letteraBean.setNome(soggetto.getNome());
				letteraBean.setIndirizzo(indirizzoSoggetto);
				letteraBean.setCap(capSoggetto);
				letteraBean.setComune(comuneSoggetto);
				letteraBean.setProvincia(provinciaSoggetto);
				letteraBean.setData_nascita(dateFormat.format(soggetto.getDataNascita().dateValue()));
				letteraBean.setTessera_sanitaria(tesseraSanitaria);
				letteraBean.setCodice_fiscale(soggetto.getCf());
				letteraBean.setSesso(soggetto.getSesso());
				letteraBean.setEmail(soggetto.getEmail());
				letteraBean.setCellulare(soggetto.getCellulare());
				letteraBean.setTelefono1(soggetto.getTel1());
				letteraBean.setTelefono2(soggetto.getTel2());
				letteraBean.setCentro_prelievo(centro_p);
				letteraBean.setConsiglio((String)suggRow.getAttribute("Descrizione"));
				letteraBean.setNote((String)ref.getAttribute("Note"));
				letteraBean.setIndirizzo_centro(indirizzo_centro_p);
				letteraBean.setSede_centro(sede_centro_p);
				letteraBean.setOrari_centro(orari_centro_p);
				letteraBean.setTel_centro(tel_centro_p);
				letteraBean.setData_app(dateFormat.format(((Date)invito.getAttribute("Dtapp")).dateValue()));
				letteraBean.setData_creazione(dateFormat.format(new java.util.Date()));
				letteraBean.setSupervisore(medicoRilevatoreRow.getAttribute("Titolo") + " " + medicoRilevatoreRow.getAttribute("Nome") + " " + medicoRilevatoreRow.getAttribute("Cognome"));
        letteraBean.setStile_di_vita(stileDiVita);
        letteraBean.setAlimentazione(alimentazione);
        letteraBean.setBmi_eval(classeBmi);
				
				// Medico, solo se aderisce allo screening
				if (soggetto.getDtadesCa() != null) {
					letteraBean.setMedico(soggetto.getNome1() + " " + soggetto.getCognome1());
				}
				
				for (Iterator i = questionario.getDomandeTuttiLiv().iterator(); i.hasNext();) {
					DomandaQuestionario domanda = (DomandaQuestionario)i.next();
					if ("SYS_USED".equals(domanda.getCodice())) {
						letteraBean.setSys(domanda.getValore());
					} else if ("DIA_USED".equals(domanda.getCodice())) {
						letteraBean.setDia(domanda.getValore());
					} else if ("GLICEM_NUM".equals(domanda.getCodice())) {
						letteraBean.setGlicemia(domanda.getValore());
					} else if ("COLESTEROL".equals(domanda.getCodice())) {
						letteraBean.setColesterolo_tot(domanda.getValore());
					} else if ("COL_HDL".equals(domanda.getCodice())) {
						letteraBean.setColesterolo_hdl(domanda.getValore());
					} else if ("CIRCONF".equals(domanda.getCodice())) {
						letteraBean.setCirconf_vita(domanda.getValore());
					} else if ("BMI".equals(domanda.getCodice())) {
						letteraBean.setBmi(domanda.getValore());
					} else if ("AF_TOT".equals(domanda.getCodice())) {
						letteraBean.setAttivita_fisica(domanda.getValore());
					} else if ("FUMO".equals(domanda.getCodice())) {
            letteraBean.setFumo(domanda.getValore());
          }
				}
        
        RowSetIterator iter=null;
        Row r = null;
        try
        {
          iter=ViewHelper.getRowSetIterator(esamiBio, "esami_iter");
          while(iter.hasNext())
          {
            r=iter.next();
            Integer id = (Integer)r.getAttribute("Idcnfref");
            Integer anormalita = (Integer)r.getAttribute("Anormalita");
            if (anormalita == null)
            {
              anormalita = new Integer(0);
            }
            switch (id.intValue()) {
              case 1:  letteraBean.setEsa1li_1(((Integer)r.getAttribute("Valore")).toString());
                       letteraBean.setEsa_abn_1(anormalita.toString());
                      break;
              case 2:  letteraBean.setEsa1li_2(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_2(anormalita.toString());
                      break;
              case 3:  letteraBean.setEsa1li_3(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_3(anormalita.toString());
                    break;
              case 4:  letteraBean.setEsa1li_4(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_4(anormalita.toString());
                    break;
              case 5:  letteraBean.setEsa1li_5(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_5(anormalita.toString());
                    break;
              case 6:  letteraBean.setEsa1li_6(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_6(anormalita.toString());
                    break;
              case 7:  letteraBean.setEsa1li_7(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_7(anormalita.toString());
                    break;
              case 8:  letteraBean.setEsa1li_8(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_8(anormalita.toString());
                    break;
              case 9:  letteraBean.setEsa1li_9(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_9(anormalita.toString());
                    break;
              case 10:  letteraBean.setEsa1li_10(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_10(anormalita.toString());
                    break;
              case 11:  letteraBean.setEsa1li_11(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_11(anormalita.toString());
                    break;
              case 12:  letteraBean.setEsa1li_12(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_12(anormalita.toString());
                    break;
              case 13:  letteraBean.setEsa1li_13(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setEsa_abn_13(anormalita.toString());
                    break;
            }         
          }
          
          iter=ViewHelper.getRowSetIterator(esamiPfas, "esami_iter");
          while(iter.hasNext())
          {
            r=iter.next();
            Integer id = (Integer)r.getAttribute("Idcnfref");
            Integer anormalita = (Integer)r.getAttribute("Anormalita");
            if (anormalita == null)
            {
              anormalita = new Integer(0);
            }
            switch (id.intValue()) {
              case 1:  letteraBean.setPfa1li_1(((Integer)r.getAttribute("Valore")).toString());       
                      letteraBean.setPfas_abn_1(anormalita.toString());
                      break;
              case 2:  letteraBean.setPfa1li_2(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_2(anormalita.toString());
                      break;
              case 3:  letteraBean.setPfa1li_3(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_3(anormalita.toString());
                    break;
              case 4:  letteraBean.setPfa1li_4(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_4(anormalita.toString());
                    break;
              case 5:  letteraBean.setPfa1li_5(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_5(anormalita.toString());
                    break;
              case 6:  letteraBean.setPfa1li_6(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_6(anormalita.toString());
                    break;
              case 7:  letteraBean.setPfa1li_7(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_7(anormalita.toString());
                    break;
              case 8:  letteraBean.setPfa1li_8(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_8(anormalita.toString());
                    break;
              case 9:  letteraBean.setPfa1li_9(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_9(anormalita.toString());
                    break;
              case 10:  letteraBean.setPfa1li_10(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_10(anormalita.toString());
                    break;
              case 11:  letteraBean.setPfa1li_11(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_11(anormalita.toString());
                    break;
              case 12:  letteraBean.setPfa1li_12(((Integer)r.getAttribute("Valore")).toString());
                      letteraBean.setPfas_abn_12(anormalita.toString());
                    break;
            }         
          }
        } catch (Exception e)
        {
          
        }finally
        {
          if(iter!=null)
            iter.closeRowSetIterator();
        }
				
				// Scrivo il pdf nel blob
				//l.writePdf(letteraBean, pdfOutputStream);
        File pdf=l.createLetter(letteraBean);
				
				// Salvo il PDF come allegato
				Ref_SoAllegatoViewRowImpl allegatoRow = (Ref_SoAllegatoViewRowImpl)allegatoView.createRow();
				allegatoView.insertRow(allegatoRow);
				
				idAllegato = ((Parent_AppModule)am).getNextIdAllegato();
				allegatoRow.setIdallegato(idAllegato);
				allegatoRow.setCodts(soggetto.getCodts());
				allegatoRow.setDtcreazione(DateUtils.getOracleDateNow());
				allegatoRow.setTpscr(tpscr);
				allegatoRow.setUlss(ulss);
				allegatoRow.setLettera(BlobUtils.getBlobFromFile(pdf));
				
				am.getTransaction().postChanges();
			
        ref.setAttribute("Idallegato",idAllegato);
        ref.setAttribute("Dtcreazione",allegatoRow.getDtcreazione());        
				ref.setAttribute("Dtspedizione",null);
			
				
		return allegatoRow;
	}
  
  
    private LetteraRefertoCitoBean createLetteraBean1L(String soggettoId, String ulss, String tpscr,
    Row ref,Row adepreR,Row giudiaR) throws Exception
    {
      return createLetteraBean1L(soggettoId, ulss, tpscr, ref, adepreR, giudiaR, null);
    }
    
  
  
    private LetteraRefertoCitoBean createLetteraBean1L(String soggettoId, String ulss, String tpscr,
    Row ref,Row adepreR,Row giudiaR, Row invito) throws Exception
    {
      //soggetto
      ViewObject soggetti=am.findViewObject("A_SoSoggettoView1");
      soggetti.setWhereClause("CODTS='"+soggettoId+"' AND A_SoSoggetto.ULSS='"+ulss+"'");
      soggetti.executeQuery();
  
  
  
      A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)soggetti.first();
  
  
      if(soggetto==null)
        throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda sanitaria "+ulss);    
    
      ViewObject centri=am.findViewObject("A_SoCentroPrelRefView1");
    
      String centro_p=null;
      String centro_r=null;
      Row[]   result=centri.getFilteredRows("Idcentro",ref.getAttribute("Idcentroref"));
      if(result.length>0){    

      centro_r=(String)result[0].getAttribute("Descrizione");
      }

      /* modifica per il colon*/
      Integer centroPrelievo = (Integer)ref.getAttribute("Idcentroprelievo");
      if (centroPrelievo == null)
      {
        //lo prendo dall'invito
        centroPrelievo = (Integer)invito.getAttribute("Idcentroprelievo");
      }
      result=centri.getFilteredRows("Idcentro",centroPrelievo);

      String indirizzo_centro_p=null;
      String sede_centro_p=null;
      String orari_centro_p=null;
      String tel_centro_p=null;
      if(result.length>0)
      {
        Row cc=result[0];
        centro_p=(String)cc.getAttribute("Descrizione");
        indirizzo_centro_p=(String)cc.getAttribute("IndirizzoRes");
        sede_centro_p=(String)cc.getAttribute("Sede");
        orari_centro_p=(String)cc.getAttribute("Oraritel");
        tel_centro_p=(String)cc.getAttribute("Tel");
      }
      
       
  /*20080718 FINE MOD*/
 
    String adepre=null;
    String giudia=null;
    
    String supervisore=null;
    String citoscreener=null;
    String revisore=null;
    String dtprelievo=null;
    String note=null;
    String braccio45mx = null;
    String codice45mx = null;
  //dati per il citologico
     if ("CI".equals(tpscr))
     {
     
    
       if(adepreR!=null){
          ViewObject conf=am.findViewObject("Ref_SoCnfRef1livADEPREView1");
          result=conf.getFilteredRows("Idcnfref1l",adepreR.getAttribute("Idcnfref"));
         
          if(result.length>0)
          {
            adepre=(String)result[0].getAttribute("Descrizione");
          }
      }
      
      
       if(giudiaR!=null){
            ViewObject conf=am.findViewObject("Ref_SoCnfRef1livGIUDIAView1");
            result=conf.getFilteredRows("Idcnfref1l",giudiaR.getAttribute("Idcnfref"));
            
            if(result.length>0)
            {
              giudia=(String)result[0].getAttribute("Descrizione");
            }
       }
      ViewObject opmedici=am.findViewObject("Ref_SoOpmedicoView2");
      result=opmedici.getFilteredRows("Idop",ref.getAttribute("Idmedref"));
      
      if(result.length>0)
      {
        supervisore=result[0].getAttribute("Titolo")+" "+
        result[0].getAttribute("Cognome")+" "+result[0].getAttribute("Nome");
      }
      
      
       result=opmedici.getFilteredRows("Idop",ref.getAttribute("Idcitoscreener"));
       if(result.length>0)
      {
        citoscreener=result[0].getAttribute("Titolo")+" "+
        result[0].getAttribute("Cognome")+" "+result[0].getAttribute("Nome");
      }
      
      if(ref.getAttribute("Dtprelievo")!=null){
       dtprelievo=DateUtils.dateToString((((oracle.jbo.domain.Date)ref.getAttribute("Dtprelievo")).dateValue()));
      } else 
      {
        if(ref.getAttribute("DataPrelievoHpv")!=null){
          dtprelievo=DateUtils.dateToString(((oracle.jbo.domain.Date)ref.getAttribute("DataPrelievoHpv")).dateValue());
        }
      }
      
      
      note=(String)ref.getAttribute("Notereferto");
      
     }
     //dati per il colon
     else if ("CO".equals(tpscr))
     {
      ViewObject opmedici=am.findViewObject("Ref_SoOpmedicoView2");
       result=opmedici.getFilteredRows("Idop",ref.getAttribute("Idlettore"));
      
      if(result.length>0)
      { //piu' che il supervisore e' il lettore della provetta
        supervisore=result[0].getAttribute("Titolo")+" "+
        result[0].getAttribute("Cognome")+" "+result[0].getAttribute("Nome");
      }
      
      if(ref.getAttribute("Dtreferto")!=null)
       dtprelievo=DateUtils.dateToString(((oracle.jbo.domain.Date)ref.getAttribute("Dtreferto")).dateValue());
     
      note=(String)ref.getAttribute("Note");
     }
     //dati per il mammo
     else if ("MA".equals(tpscr))
     {
      ViewObject opmedici=am.findViewObject("Ref_SoOpmedicoView2");
      //leggo il primo radiologo
      result=opmedici.getFilteredRows("Idop",ref.getAttribute("L1Radiologo"));
       if(result.length>0)
      {
        citoscreener=result[0].getAttribute("Titolo")+" "+
        result[0].getAttribute("Cognome")+" "+result[0].getAttribute("Nome");
      }
        
       if(ref.getAttribute("L2Radiologo")!=null){
        result=opmedici.getFilteredRows("Idop",ref.getAttribute("L2Radiologo"));
       }
       else
         result=new Row[0];
      
      if(result.length>0)
      { //piu' che il supervisore e' il lettore della provetta
        supervisore=result[0].getAttribute("Titolo")+" "+
        result[0].getAttribute("Cognome")+" "+result[0].getAttribute("Nome");
      }
      
      if(ref.getAttribute("RRadiologo")!=null){
        result=opmedici.getFilteredRows("Idop",ref.getAttribute("RRadiologo"));
       }else
         result=new Row[0];
      
      if(result.length>0)
      { //revisore
        revisore=result[0].getAttribute("Titolo")+" "+
        result[0].getAttribute("Cognome")+" "+result[0].getAttribute("Nome");
      }
      
      if(ref.getAttribute("Dtmammo")!=null)
       dtprelievo=DateUtils.dateToString(((oracle.jbo.domain.Date)ref.getAttribute("Dtmammo")).dateValue());
     
      
      note="";
      if(ref.getAttribute("L1Note")!=null)
         note+=ref.getAttribute("L1Note")+" ";
      if(ref.getAttribute("L2Note")!=null)
         note+=ref.getAttribute("L2Note")+" ";
      if(ref.getAttribute("RNote")!=null)
         note+=ref.getAttribute("RNote")+" ";
         
      ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
      /*02052013 GAION:progetto 45 mx*/
      try{
        String studio45mx = ParametriSistema.getParamValue(voParam,ulss,tpscr,ConfigurationConstants.PARAMETRO_STUDIO_45_MX);     
        if (studio45mx != null && "S".equals(studio45mx)) {
          ViewObject sogg_scr_vo = am.findViewObject("A_SoSoggScrView1"); 
          sogg_scr_vo.setWhereClause("TPSCR='"+tpscr+"' AND ULSS='"+ulss+"' AND CODTS='"+soggetto.getCodts()+"'");
          sogg_scr_vo.executeQuery();
          A_SoSoggScrViewRow soggscr=(A_SoSoggScrViewRow)sogg_scr_vo.first();
          if (soggscr != null)
          {
            braccio45mx = soggscr.getMx45Braccio();
            codice45mx = soggscr.getMx45Codice();
          }
        }
      } catch(Exception ex)
      {      
      }
      /* FINE 02052013*/

     }
   
    int fine=2;
   if ("CO".equals(tpscr) || "MA".equals(tpscr))
     fine=3;
    ViewObject suggerimenti=am.findViewObject("Ref_SoCnfSugg1livView"+fine);
   
    suggerimenti.setWhereClause("TPSCR='"+tpscr+"' AND ULSS='"+ulss+"' AND IDSUGG="+ref.getAttribute("Idsugg"));
    suggerimenti.executeQuery();
    String sugg_d=null;
    Row s=suggerimenti.first();
    if(s!=null)
    {
      sugg_d=(String)s.getAttribute("Descrizione");
    }
    
    String codcom=null;
    String indirizzo=null;
    String comune=null;
    String pv=null;
    String cap=null;
    //24112011 Gaion: nuovo parametro per il calcolo dell'indirizzo
    ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
    String mod_spedizione =null;
     try{
    
      mod_spedizione =ParametriSistema.getParamValue(voParam,ulss,tpscr,"mod_spedizione");
    }
      catch (Exception ex)
      {
        mod_spedizione="standard"; //se il parametro non e' confiogurato si lavora in modo standard
      }
    
    //24112011 fine
    if(soggetto.getIndirizzoScr()!=null && soggetto.getIndirizzoScr().trim().length()>0)
    {
      codcom=soggetto.getCodcomscr();
      indirizzo=soggetto.getIndirizzoScr();
      cap=soggetto.getCap();
      comune=soggetto.getDescrizione();
      pv=soggetto.getCodpr();
    }
    //24112011 GAion: verifico il parametro per il calcolo dell'indirizzo
    else if(mod_spedizione.equalsIgnoreCase("standard")&& soggetto.getIndirizzoDom()!=null && soggetto.getIndirizzoDom().trim().length()>0)
    {
      codcom=soggetto.getCodcomdom();
      indirizzo=soggetto.getIndirizzoDom();
      cap=soggetto.getCap1();
      comune=soggetto.getDescrizione1();
      pv=soggetto.getCodpr1();
    }
      
    else
    {
      codcom=soggetto.getCodcomres();
      indirizzo=soggetto.getIndirizzoRes();
      cap=soggetto.getCap2();
      comune=soggetto.getDescrizione2();
      pv=soggetto.getCodpr2();
    }

    ViewObject zone=am.findViewObject("Cnf_SoDistrettoZonaView1");
    String zona=null;
    if(soggetto.getCoddistrzona()!=null){
        zone.setWhereClause("CODDISTRZONA="+soggetto.getCoddistrzona()+
        " AND ulss='"+soggetto.getUlss()+"'");
        zone.executeQuery();
        Row z=zone.first();
        if(z!=null && z.getAttribute("Descrizione")!=null)
          zona=(String)z.getAttribute("Descrizione");

    }
    
    String medico=null;
    if(soggetto.getCodiceregmedico()!=null)
    {
      String att=(String)ViewHelper.decodeByTpscr(tpscr,"Dtadesione","DtadesCo","DtadesMa","DtadesCa","DtadesPf");
      if(soggetto.getAttribute(att)!=null)
        medico=soggetto.getNome1().trim()+" "+soggetto.getCognome1().trim();
    }
      
  
  /* 20110207 mod Serra*/
    ViewObject inviti=am.findViewObject("Ref_SoInvitoView1");
      inviti.setWhereClause("IDINVITO="+ref.getAttribute("Idinvito"));
      inviti.executeQuery();
      Row inv=inviti.first();
      if(inv==null)
        throw new Exception("Invito cui si riferisce il referto non trovato");
      oracle.jbo.domain.Date data_app=(oracle.jbo.domain.Date)inv.getAttribute("Dtapp");
 /* 20110207 fine mod Serra*/
      String test_proposto = (String)inv.getAttribute("TestProposto");
      
      //prelievo hpv
      String prelievo_hpv = null;
      if ("CI".equals(tpscr)){
        ViewObject voAcc=am.findViewObject("Ref_SoAccCito1livView1");
        voAcc.setWhereClause("CODTS='"+soggetto.getCodts()+"' AND ULSS='"+soggetto.getUlss()+"'");
        voAcc.executeQuery();
        Row acc=this.getAcc1liv((Integer)ref.getAttribute("Idinvito"),tpscr,false,null,false);
        if (acc != null){
          Integer prel_hpv = (Integer)acc.getAttribute("PrelievoHpv");
          if (prel_hpv != null){
            if (prel_hpv.intValue() == ConfigurationConstants.DB_SINOND_SI)
            {
              prelievo_hpv = "Si";  
            } else if (prel_hpv.intValue() == ConfigurationConstants.DB_SINOND_NO)
            {
              prelievo_hpv = "No";  
            } else if (prel_hpv.intValue() == ConfigurationConstants.DB_SINOND_ND)
            {
              prelievo_hpv = "Dato non disponibile";  
            }
          }
        }
      }
      
    //trovo la tessera sanitaria
    String tesseraSanitaria = SoggUtils.trovaTessera(am,soggetto.getCodts(),soggetto.getUlss());
      
    LetteraRefertoCitoBean lb=new LetteraRefertoCitoBean(
                  soggetto.getCognome(),
                  soggetto.getNome(),
                  indirizzo,
                  cap,
                  comune,
                  pv,
                  DateUtils.getToday(),
                  tesseraSanitaria,
                  soggetto.getCf(),
                  soggetto.getSesso(),
                  soggetto.getDataNascita()!=null?DateUtils.dateToString(soggetto.getDataNascita().dateValue()):"",
                  soggetto.getCognomeConiuge(),
                  soggetto.getTel1(),
                  soggetto.getTel2(),
                  dtprelievo,
                  centro_p,
                  centro_r,
                  adepre,
                  null,
                  supervisore,
                  citoscreener,
                  revisore,
                  sugg_d,
                  giudia,
                  note,
                  null,
                  zona,
                  medico,
                  /*20080718 MOD: dati centro nelle stampe*/
                  indirizzo_centro_p,
                  sede_centro_p,//sede
                  orari_centro_p,//orari
                  tel_centro_p,//tel centro
                  /*20080718 FINE MOD*/
                   /* 20110207 mod Serra*/
                   data_app!=null?DateUtils.dateToString(data_app.dateValue()):"",
                    /* 20110207 fine mod Serra*/
                    null,
                    null,
                    null,
                    braccio45mx,
                    codice45mx,
                    soggetto.getEmail(),
                    soggetto.getCellulare(),
                    test_proposto,
                    prelievo_hpv, null);
                  
        return lb;
  }
   private LetteraRefertoCitoBean createLetteraBean2L(String soggettoId, String ulss, String tpscr,
  Row ref,int livello,Integer idinvitoprec) throws Exception
  {
    String sugg=(String)ViewHelper.decodeByTpscr(tpscr,"Idsugg","Idsugg2l","Idsugg2l",null,null);
  
    Integer idsugg=(Integer)ref.getAttribute(sugg);
    
    return this.createLetteraBean2L(soggettoId,ulss,tpscr,ref,livello,idinvitoprec,idsugg);
  }
  
	private LetteraRefertoCitoBean createLetteraBean2L(String soggettoId, String ulss, String tpscr,
		Row ref, int livello, Integer idinvitoprec, Integer idsugg) throws Exception
	{
		//soggetto
		ViewObject soggetti = am.findViewObject("A_SoSoggettoView1");
		soggetti.setWhereClause("CODTS='" + soggettoId + "' AND A_SoSoggetto.ULSS='" + ulss + "'");
		soggetti.executeQuery();
		/*MOD20071116
		if(soggetti.getRowCount()==0)
		throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda sanitaria "+ulss);    
		A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)soggetti.first();
		*/
		A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow)soggetti.first();
		if(soggetto == null)
			throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId + " non e' stato trovato nell'azienda sanitaria " + ulss);
		
		// Dati di screening del soggetto
		String mx45Braccio = null;
		String mx45Codice = null;
		ViewObject soggettoScrView = am.findViewObject("Ref_SoSoggScrView1");
		if (soggettoScrView != null) {
			soggettoScrView.setWhereClause("Ref_SoSoggScr.ulss = '" + ulss + "' AND Ref_SoSoggScr.tpscr = '" + tpscr + "' AND Ref_SoSoggScr.codts = '" + soggettoId + "'");
			soggettoScrView.executeQuery();
			Ref_SoSoggScrViewRowImpl soggettoScrRow = (Ref_SoSoggScrViewRowImpl)soggettoScrView.first();
			if (soggettoScrRow != null) {
				mx45Braccio = soggettoScrRow.getMx45Braccio();
				mx45Codice = soggettoScrRow.getMx45Codice();
			}
		}
		
		ViewObject centri=am.findViewObject("A_SoCentroPrelRefView1");
		Integer id_centro_p, id_centro_r;
		String centro_p = null;
		String centro_r = null;
		//caso del secondo livello, devo cercare i centri nell'invito correlato
		ViewObject inviti = am.findViewObject("Ref_SoInvitoView1");
		inviti.setWhereClause("IDINVITO=" + ref.getAttribute("Idinvito"));
		inviti.executeQuery();
		Row inv = inviti.first();
		if(inv == null)
			throw new Exception("Invito cui si riferisce il referto non trovato");
			
		id_centro_p = (Integer)inv.getAttribute("Idcentroprelievo");
		id_centro_r = (Integer)inv.getAttribute("Idcentroref1liv");
		oracle.jbo.domain.Date data_app = (oracle.jbo.domain.Date)inv.getAttribute("Dtapp");

		Row[] result = centri.getFilteredRows("Idcentro", id_centro_p);
		
		/*20080718 MOD: dati centro nelle stampe
		if (result.length > 0)
			centro_p = (String)result[0].getAttribute("Descrizione");
		*/
		String indirizzo_centro_p = null;
		String sede_centro_p = null;
		String orari_centro_p = null;
		String tel_centro_p = null;
		if (result.length > 0) {
			Row cc = result[0];
			centro_p = (String)cc.getAttribute("Descrizione");
			indirizzo_centro_p = (String)cc.getAttribute("IndirizzoRes");
			sede_centro_p = (String)cc.getAttribute("Sede");
			orari_centro_p = (String)cc.getAttribute("Oraritel");
			tel_centro_p = (String)cc.getAttribute("Tel");
		}
		/*20080718 FINE MOD*/  
      
		result = centri.getFilteredRows("Idcentro", id_centro_r);
		if (result.length > 0)
		//throw new Exception("Centro di refertazione con codice="+ref.getAttribute("Idcentroprelievo")+" non trovato");    
			centro_r = (String)result[0].getAttribute("Descrizione");
		
		ViewObject opmedici = am.findViewObject("Ref_SoOpmedicoView2");
		result = opmedici.getFilteredRows("Idop", ref.getAttribute((String)ViewHelper.decodeByTpscr(tpscr, "Idmedconcl", "IdmedConcl", "Idmedconcl1",null,null)));
		String supervisore = null;
		if(result.length > 0) {
			supervisore = result[0].getAttribute("Titolo") + " " +
				result[0].getAttribute("Cognome") + " " + result[0].getAttribute("Nome");
		}
		
		int fine = 2;
		if ("CO".equals(tpscr))
			fine = 3;
			
		ViewObject suggerimenti = am.findViewObject("Ref_SoCnfSugg" + livello + "livView" + fine);
		String attname = "IDSUGG";
		
		if (livello == 2)
			attname += "2L";
		else if (livello == 3)
			attname += "3L";
		
		suggerimenti.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' AND " + attname + "=" + idsugg);
		suggerimenti.executeQuery();
		String sugg_d = null;
		Row s = suggerimenti.first();
		if(s != null) {
			sugg_d = (String)s.getAttribute("Descrizione");
		}
                
	    //data intervento
            String data_intervento = null;
            if (livello == 3)
            {
              String interventiVoName = (String)ViewHelper.decodeByTpscr(tpscr, "Ref_SoInterventocitoView1", "Ref_SoInterventocolonView4", "Ref_SoInterventomammoView4",null,null);
              ViewObject interventi = am.findViewObject(interventiVoName);
              interventi.setWhereClause("IDREFERTO = "+ ref.getAttribute("Idreferto"));
              interventi.executeQuery();
              Row interv = interventi.first();
              if (interv != null && interv.getAttribute("Dtintervento") != null)
              {
                data_intervento = DateUtils.dateToString(((oracle.jbo.domain.Date)interv.getAttribute("Dtintervento")).dateValue());
              }
            }
    
		////////////
		String codcom = null;
		String indirizzo = null;
		String comune = null;
		String pv = null;
		String cap = null;
		//24112011 Gaion: nuovo parametro per il calcolo dell'indirizzo
		String mod_spedizione = null;
                ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
		try {
			mod_spedizione = ParametriSistema.getParamValue(voParam, ulss, tpscr, "mod_spedizione");
		} catch (Exception ex) {
			mod_spedizione = "standard"; //se il parametro non e' configurato si lavora in modo standard
		}    //24112011 fine

		if (soggetto.getIndirizzoScr() != null && soggetto.getIndirizzoScr().trim().length() > 0) {
			codcom = soggetto.getCodcomscr();
			indirizzo = soggetto.getIndirizzoScr();
			cap = soggetto.getCap();
			comune = soggetto.getDescrizione();
			pv = soggetto.getCodpr();
		}
		
		//24112011 Gaion: verifico il parametro per il calcolo dell'indirizzo
		else if (mod_spedizione.equalsIgnoreCase("standard") && soggetto.getIndirizzoDom() != null && soggetto.getIndirizzoDom().trim().length() > 0) {
			codcom = soggetto.getCodcomdom();
			indirizzo = soggetto.getIndirizzoDom();
			cap = soggetto.getCap1();
			comune = soggetto.getDescrizione1();
			pv = soggetto.getCodpr1();
		} else {
			codcom = soggetto.getCodcomres();
			indirizzo = soggetto.getIndirizzoRes();
			cap = soggetto.getCap2();
			comune = soggetto.getDescrizione2();
			pv = soggetto.getCodpr2();
		}
		//////////////////

		ViewObject zone = am.findViewObject("Cnf_SoDistrettoZonaView1");
		String zona = null;
		if (soggetto.getCoddistrzona() != null) {
			zone.setWhereClause("CODDISTRZONA=" + soggetto.getCoddistrzona());
			zone.executeQuery();
			Row z = zone.first();
			if (z != null && z.getAttribute("Descrizione") != null)
				zona = (String)z.getAttribute("Descrizione");
		}

		ViewObject invito1l = am.findViewObject("Ref_SoInvitoView1");

		String data_esame_prec;
		if (idinvitoprec == null)
			data_esame_prec = null;
		else {
			invito1l.setWhereClause("IDINVITO=" + idinvitoprec);
			invito1l.executeQuery();
			Row inv1 = invito1l.first();
			if (inv1 == null)
				throw new Exception("Invito con codice "+idinvitoprec+" non trovato");
			
			if (inv1.getAttribute("Dtapp") == null)
				data_esame_prec=null;
			else
				data_esame_prec=DateUtils.dateToString(((oracle.jbo.domain.Date)inv1.getAttribute("Dtapp")).dateValue());
		}
    
		String medico = null;
		if (soggetto.getCodiceregmedico() != null) {
			String att = (String)ViewHelper.decodeByTpscr(tpscr, "Dtadesione", "DtadesCo", "DtadesMa",null,null);
			if (soggetto.getAttribute(att) != null)
				medico = soggetto.getNome1().trim() + " " + soggetto.getCognome1().trim();
			/*
			ViewObject medici = am.findViewObject("A_SoMedicoView1");
			medici.setWhereClause("CODICEREGMEDICO=" + soggetto.getCodiceregmedico());
			medici.executeQuery();
			Row m = medici.first();
			//manca il controllo sull'adesione!!!!!!!
			if (m != null)
				medico = "Dr." + m.getAttribute("Cognome") + " " + m.getAttribute("Nome");
			*/
		}
    
    //trovo la tessera sanitaria
    String tesseraSanitaria = SoggUtils.trovaTessera(am,soggetto.getCodts(),soggetto.getUlss());
		LetteraRefertoCitoBean lb = new LetteraRefertoCitoBean(
			soggetto.getCognome(),
			soggetto.getNome(),
			indirizzo,
			cap,
			comune,
			pv,
			DateUtils.getToday(),
			tesseraSanitaria,
			soggetto.getCf(),
			soggetto.getSesso(),
			soggetto.getDataNascita() != null ? DateUtils.dateToString(soggetto.getDataNascita().dateValue()) : "",
			soggetto.getCognomeConiuge(),
			soggetto.getTel1(),
			soggetto.getTel2(),
			data_app != null ? DateUtils.dateToString(data_app.dateValue()) : "",
			centro_p,
			centro_r,
			null,
			null,
			supervisore,
			null,
			null,
			sugg_d,
			null,
			(String)ref.getAttribute((String)ViewHelper.decodeByTpscr(tpscr, "Noteconcl", "Note", "Note",null,null)),
			data_esame_prec,
			zona,
			medico,
			
			/*20080718 MOD: dati centro nelle stampe*/
			indirizzo_centro_p,
			sede_centro_p,
			orari_centro_p,
			tel_centro_p,
			/*20080718 FINE MOD*/
			
			/* 20110207 mod Serra*/
			data_app != null ? DateUtils.dateToString(data_app.dateValue()) : "",
			/* 20110207 fine mod Serra*/
			
			null,  // codice richiesta
			null,  // codice campione
			null,  // data creazione
			mx45Braccio,
			mx45Codice,
      soggetto.getEmail(),
      soggetto.getCellulare(),
      null,
      null,data_intervento);

		return lb;
	}
  
  public void updateRoundIndivHPV(Row ref,ViewObject inviti) throws Exception
  {
    Row[] result = inviti.getFilteredRows("Idinvito", (Integer)ref.getAttribute("Idinvito"));
		if (result.length == 0)
			throw new Exception("Invito cui si riferisce il referto non trovato");
		
		Row invito = result[0];
    
    GeneratoreInviti gi=new GeneratoreInviti(am,true);
    gi.updateRoundIndivHPV(invito);
  }
  
  /**
   * Va ad aggiornare l'invito cui il referto si riferisce
   * @throws java.lang.Exception
   * @param livello
   * @param inviti
   * @param ref
   */
  public void updateInvito(Row ref,ViewObject inviti,int livello,String user) throws Exception
  {
    
    String sugg="Idsugg";
    if(livello>1)
      sugg=(String)ViewHelper.decodeByTpscr((String)ref.getAttribute("Tpscr"),"Idsugg","Idsugg"+livello+"l","Idsugg"+livello+"l",null,"Idsugg");
      this.updateInvito((String)ref.getAttribute("Ulss"),
                        (String)ref.getAttribute("Tpscr"),
                        (Integer)ref.getAttribute("Idinvito"),
                        (Integer)ref.getAttribute(sugg),
                        inviti,
                        livello,
                        user);
  }
  
	/**
	 * Va ad aggiornare l'invito cui si riferisce l'intervento
	 * @throws java.lang.Exception
	 * @param livello
	 * @param inviti
	 * @param ref
	 */
	public void updateInvito(Row intervento, Row ref, ViewObject inviti, int livello, String user) throws Exception {
		String sugg = (String)ViewHelper.decodeByTpscr((String)ref.getAttribute("Tpscr"), "Idsugg", "Idsugg3l", "Idsugg3l",null,null);
		
		this.updateInvito((String)intervento.getAttribute("Ulss"),
			(String)intervento.getAttribute("Tpscr"),
			(Integer)ref.getAttribute("Idinvito"),
			(Integer)intervento.getAttribute(sugg),
			inviti,
			livello,
			user);
	}
  
	public void updateInvito(String ulss, String tpscr, Integer idinvito,
		Integer idsugg, ViewObject inviti, int livello, String user) throws Exception
	{
		Row[] result = inviti.getFilteredRows("Idinvito", idinvito);
		if (result.length == 0)
			throw new Exception("Invito cui si riferisce il referto non trovato");
		
		Row invito = result[0];

		// mauro 01/04/2010, modifiche per sviluppo 51, data richiamo calcolata
		// sulla base della data referto
		/* modifiche rimosse il 14/06/2010
		if (tpscr.equals("CO")) {
			String dataRichRef = "N";
		
			try {
				dataRichRef = ParametriSistema.getParamValue(am,ulss,tpscr,ConfigurationConstants.PARAMETRO_DATA_RICH_REF);
			} catch (Exception ex) {} // do nothing
		
			if (dataRichRef.equals("S")) {
				// ottenere data referto
				Integer idInvito = (Integer) invito.getAttribute("Idinvito");
				String queryRef = "select dtreferto from so_refertocolon1liv where idinvito = " + idInvito.toString() ;      
				Date dtRef = (Date) ViewHelper.getQueryResult(am,queryRef);
				
				// ottenere centro prelievo
				Integer idCprel = (Integer) invito.getAttribute("Idcentroprelievo");
				
				// cercare su agenda se esiste slot per data e centro
				String dtLett = DateUtils.dateToString(dtRef);
				
				// vedo se esiste lo slot in agenda e eventualmente lo creo
				String queryAgenda = "select idapp from so_appuntamento where " + 
					"idcentro = " + idCprel.toString() + 
					" and dtapp = to_date('" + dtLett + "','dd/mm/yyyy')" +
					" and oraapp = 3 and minapp = 3 and tpsrc = 'CO'";
				
				Object oidApp = ViewHelper.getQueryResult(am,queryAgenda);
				Integer idApp;
		
				if (oidApp == null) {
					// inserimento slot
					
					idApp = ((Parent_AppModule) am).getNextIdAppuntamento();
					
					String insApp = "insert into so_appuntamento values (" +
						idApp.toString() + "," +
						idCprel.toString() + "," +
						"to_date('" + dtLett + "','dd/mm/yyyy')," +
						"3,3,1,'CO')";
		
					am.getTransaction().executeCommand(insApp);
				} else {
					idApp = (Integer) oidApp;
				}
		
				// aggiornamento invito
				invito.setAttribute("Idapp",idApp);
				invito.setAttribute("Dtapp",dtRef);
				invito.setAttribute("Oraapp",new Integer(3));
				invito.setAttribute("Minapp",new Integer(3));
			}
		}
		*/
		
		//se non c'e' nessun suggerimento azzero ogni possibile richiamo
		
		if (idsugg != null) {
			String attname = "IDSUGG";
			if (livello == 2)
				attname += "2L";
			else if (livello == 3)
				attname += "3L";
    
	        //andiamo a leggere il tipo di richiamo e quanto tempo deve passare
		    int fine = 2;

			if ("CO".equals(tpscr) || "MA".equals(tpscr) || "PF".equals(tpscr))
				fine = 3;

			ViewObject suggerimenti = am.findViewObject("Ref_SoCnfSugg" + livello + "livView" + fine);
			suggerimenti.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' AND " + attname + "=" + idsugg);
			suggerimenti.executeQuery();
			/* MOD20071116
			if (suggerimenti.getRowCount() == 0)
				throw new Exception("Suggerimento di " + livello + " livello con codice " + idsugg + " non trovato");
			Row s = suggerimenti.first();
			*/
			
			Row s = suggerimenti.first();
			if (s == null)
				throw new Exception("Suggerimento di " + livello + " livello con codice " + idsugg + " non trovato");

			//se non va inserito nessun richiamo non faccio nulla
			if (s.getAttribute("Idtpinvito") != null) {
				ViewObject tpinviti = am.findViewObject("Ref_SoCnfTpinvitoView1");
				result=tpinviti.getFilteredRows("Idtpinvito", s.getAttribute("Idtpinvito"));
				if (result.length == 0)
					throw new Exception("Tipo di invito cui si riferisce il richiamo non trovato");

				int livello_richiamo = ((Integer)result[0].getAttribute("Livello")).intValue();
			
				if (livello != 3)
					this.insertRichiamo(invito,
						(String)s.getAttribute("Idtpinvito"),
						(Integer)result[0].getAttribute("Idcateg"),
						livello_richiamo,
						(Integer)s.getAttribute("Ggrichiamo"),
						am.findViewObject("A_SoCentroPrelRefView1"),
						am);
				else {
					//gestione dell'impostazione del richiamo a partire dalla data dell'ultimo intervento
					this.insertRichiamo(invito,
						(String)s.getAttribute("Idtpinvito"),
						(Integer)result[0].getAttribute("Idcateg"),
						livello_richiamo,
						this.getUltimoInterventoDate(invito, tpscr),
						(Integer)s.getAttribute("Ggrichiamo"),
						am.findViewObject("A_SoCentroPrelRefView1"),
						am);
				}
			} else {
				//nessun richiamo
				invito.setAttribute("Tprichiamo",null);
				invito.setAttribute("Dtrichiamo",null);
				invito.setAttribute("Idcentrorichiamo",null);
			}
		} else {
			//nessun richiamo perche' non c'e' nessun suggeriemnto
			invito.setAttribute("Tprichiamo",null);
			invito.setAttribute("Dtrichiamo",null);
			invito.setAttribute("Idcentrorichiamo",null);
		}
		invito.setAttribute("Dtultimamod",DateUtils.getOracleDateNow());
		invito.setAttribute("Opmodifica",user);
	}

  private Date getUltimoInterventoDate(Row invito, String tpscr) throws Exception
  {
    String refertiName, intervName, dateAttName;
    //il vo dei referti e' indipendente, mentre gli interventi dipendono da questo
    if("CI".equals(tpscr))
    {
      refertiName="Ref_SoRefertocito2livView1";
      intervName="Ref_SoInterventocitoView5";
      dateAttName="Dtraccomandazione";
    }
    else if("CO".equals(tpscr))
    {
      refertiName="Ref_SoRefertocolon2livView2";
      intervName="Ref_SoInterventocolonView3";
      dateAttName="Dtconcl";
    }
    else if("MA".equals(tpscr))
    {
      refertiName="Ref_SoRefertomammo2livView2";
      intervName="Ref_SoInterventomammoView3";
      dateAttName="Dtconcl";
    }
    else
      throw new Exception("Tipo di screening "+tpscr+" non riconosciuto");
  
    ViewObject ref=am.findViewObject(refertiName);
    ref.setWhereClause("IDINVITO="+invito.getAttribute("Idinvito"));
    ref.executeQuery();
    Row r=ref.first();
    if(r==null)
      throw new Exception("Referto di secondo livello non trovato");
    
    //interventi dipendenti dal referto sopr atrovato
    ref=am.findViewObject(intervName);
    r=ref.first();
    if(r==null)
      throw new Exception("Nessun intervento trovato");
    
    return (Date)r.getAttribute(dateAttName);
    
  }
  
  /**
   * Inserisce i dati del richiamo nell'invito (se si tratta di un richiamo del tipo
   * primo invito, allora l'invito e' quello che chiude il round individuale)
   * Non considera i casi in cui c'e' un intervento
   * @throws java.lang.Exception
   * @param codcom codice del comune di riferimento
   * @param conf_comune viewobject delle configurazioni del comune
   * @param centri viewobject dei centri di prelievo
   * @param ggrichiamo gironi dopo cui far scattare il richiamo
   * @param livello_richiamo livello del richiamo
   * @param categ categoria del richiamo
   * @param tprichiamo tipo di invito del richiamo
   * @param invito record contenet l'invito da aggiornare col richiamo
   */
   public static void insertRichiamo(Row invito, 
                                    String tprichiamo,
                                    Integer categ,
                                    int livello_richiamo,
                                    Integer ggrichiamo,
                                    ViewObject centri,
                                    ApplicationModule _am) throws Exception {

        //sara 24/05/2011
        Date dateFrom = (Date) invito.getAttribute("Dtapp");
        String metodo = (String) invito.getAttribute("MetodoCalcolo");
        if (metodo != null &&
            metodo.equals("D")) //se il metodo del calcolo della data di richiamo per il tipo di esito e' impostato a D
        {
            if (invito.getAttribute("Dtesamerecente") != null)
                dateFrom = (Date) invito.getAttribute("Dtesamerecente"); //uso la data dell'esame recente
        }

        //uso la data dell'invito come
        insertRichiamo(invito, tprichiamo, categ, livello_richiamo, dateFrom, ggrichiamo, centri, _am);
    }
                                    
  public static void insertRichiamo(Row invito, String tprichiamo, Integer categ, int livello_richiamo, Date dateFrom,
                                      Integer ggrichiamo, ViewObject centri, ApplicationModule _am) throws Exception {

        if (tprichiamo == null) {
            invito.setAttribute("Tprichiamo", null);
            invito.setAttribute("Dtrichiamo", null);
            invito.setAttribute("Idcentrorichiamo", null);
            return;
        }

        invito.setAttribute("Tprichiamo", tprichiamo);

        //parte dalla data specificata
        Calendar c = DateUtils.getCalendar(dateFrom.getValue());

        // mauro 10.5.06, gestito caso null
        c.add(Calendar.DAY_OF_MONTH, ggrichiamo == null ? 0 : ggrichiamo.intValue());
        invito.setAttribute("Dtrichiamo", DateUtils.getOracleDate(c));

        //controllo se devo chiudere il round individuale
        if (ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.equals(categ))
            invito.setAttribute("Chiusuraroundindiv", ConfigurationConstants.DB_TRUE);
        else
            invito.setAttribute("Chiusuraroundindiv", ConfigurationConstants.DB_FALSE);

        //MODIFICA 07/03/2007: il calcolo del centro di richiamo avviene a livello PL/SQL
        HashMap result =
            ((Parent_AppModule) _am).callGetCentroRichiamo((String) invito.getAttribute("Codts"),
                                                           (String) invito.getAttribute("Ulss"),
                                                           (String) invito.getAttribute("Tpscr"),
                                                           new BigDecimal(((Integer) invito.getAttribute("Idinvito")).doubleValue()),
                                                           new BigDecimal(livello_richiamo));
        if (result.get("error") != null)
            throw new Exception((String) result.get("error"));
        else
            invito.setAttribute("Idcentrorichiamo", new Integer(((BigDecimal) result.get("centro")).intValue()));
        //FINE MODIFICA

    }
  
  /**
   * Cancella una lettera di referto ed aggiorna il refrto relativo
   * @param ref referto in uso (modificabile)
   */
  public void deleteLettera(Row ref)
  {

    Integer id=(Integer)ref.getAttribute("Idallegato");
    if(id==null)
     return;
    ref.setAttribute("Idallegato", null);
    ref.setAttribute("Dtcreazione", null);
    ref.setAttribute("Dtspedizione", null);
     

    ViewObject allegati=am.findViewObject("Ref_SoAllegatoView1");
    allegati.setWhereClause("IDALLEGATO="+id);
    allegati.executeQuery();
    if(allegati.first()==null)
       return;
    else {
        Row allegato = allegati.first();
        if (allegato.getAttribute("Dtstampa") != null)
        {
          // BACK UP della vecchia lettera
          String sql = "insert into so_allegato_bck " + 
            "select * from so_allegato where idallegato = " + id;     
          am.getTransaction().executeCommand(sql);      
        }
        
        allegati.first().remove();
          
    }

    
  }
  
  /**
   * Crea ed inserisce un referto di secondo livello
   * @throws java.lang.Exception
   * @return record con il nuovo referto
   * @param user operatore connesso
   * @param referti viewobject in cui inserire il referto
   * @param inviti viewobject con l'invito in uso come riga corrente
   */
  public Row nuovoReferto2liv(ViewObject inviti, 
                            ViewObject referti, 
                            String user) throws Exception
  {
    Row r=inviti.getCurrentRow();
    if(r==null)
      throw new Exception("Nessun invito da refertare");
    
    Row nuovoRef=referti.createRow();
            referti.insertRow(nuovoRef);
            //imposto subito l'id perche' potrei doverlo usare per creare 
            //dati nella tabella di cross
            nuovoRef.setAttribute("Idreferto",((Parent_AppModule)am).getNextIdReferto2liv());
            this.setDatiStandardReferto(nuovoRef,r,user);
            
            //dati peculiari del secondo livello
            //inizio con nessun esame eseguito
            nuovoRef.setAttribute("Colposcopia",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("Precito",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("Prebio",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("Citologia",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("Istbioptica",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("Biohiv",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("Biohpv",ConfigurationConstants.DB_FALSE);
          //  nuovoRef.setAttribute("Istchirurgica",ConfigurationConstants.DB_FALSE);
            //gli interventi non devono risultare chiusi
            nuovoRef.setAttribute("Intchiusi",ConfigurationConstants.DB_FALSE);
            
            // nuovi campi del refereto di 2 livello
            nuovoRef.setAttribute("GiudizioPap",ConfigurationConstants.CODICE_GIUDIA_DEFAULT);
            nuovoRef.setAttribute("GrGiudizioPap",ConfigurationConstants.NOME_GRUPPO_GIUDIA_2L);

            this.updateEsito(r,am);
            
      return nuovoRef;
  }
  
  
  private void setDatiStandardReferto(Row nuovoRef,Row r,String user)
  {
            //id invito
            nuovoRef.setAttribute("Idinvito",r.getAttribute("Idinvito"));
              //non e' completo
            nuovoRef.setAttribute("Completo",ConfigurationConstants.DB_FALSE);
            //suggerimento non disponbile
            nuovoRef.setAttribute("Idsugg",ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
             //soggetto
            nuovoRef.setAttribute("Codts",r.getAttribute("Codts"));
            //data inserimento
            nuovoRef.setAttribute("Dtinserimento",DateUtils.getOracleDateNow());
             //operatore inserimento
            nuovoRef.setAttribute("Opinserimento",user);
            //data modifica
            nuovoRef.setAttribute("Dtultimamodifica",nuovoRef.getAttribute("Dtinserimento"));
             //operatore modifica
            nuovoRef.setAttribute("Opultmodifica",user);
              //ulss
            nuovoRef.setAttribute("Ulss",r.getAttribute("Ulss"));
               //tpscr
            nuovoRef.setAttribute("Tpscr",r.getAttribute("Tpscr"));
            //referto di screening, perche' parte da un invito
            nuovoRef.setAttribute("Tpreferto",new Integer(0));
  }
  
  private void setDatiStandardRefertoCO(Row nuovoRef,Row r,String user)
  {
            //id invito
            nuovoRef.setAttribute("Idinvito",r.getAttribute("Idinvito"));
              //non e' completo
            nuovoRef.setAttribute("Completo",ConfigurationConstants.DB_FALSE);
            //soggetto
            nuovoRef.setAttribute("Codts",r.getAttribute("Codts"));
            //data inserimento
            nuovoRef.setAttribute("Dtinserimento",DateUtils.getOracleDateNow());
             //operatore inserimento
            nuovoRef.setAttribute("Opinserimento",user);
            //data modifica
            nuovoRef.setAttribute("Dtultmodifica",nuovoRef.getAttribute("Dtinserimento"));
             //operatore modifica
            nuovoRef.setAttribute("Opultmodifica",user);
              //ulss
            nuovoRef.setAttribute("Ulss",r.getAttribute("Ulss"));
               //tpscr
            nuovoRef.setAttribute("Tpscr",r.getAttribute("Tpscr"));
            //referto di screening, perche' parte da un invito
            nuovoRef.setAttribute("Tpreferto",new Integer(0));
  }
  
  
  private void updateEsito(Row r, ApplicationModule am) throws Exception
  {
    //se l'invito non ha esito
    if(r.getAttribute("Codesitoinvito")==null || !ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO.equals(r.getAttribute("Codesitoinvito")))
        {
            r.setAttribute("Codesitoinvito",ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO);
            //se faccio un amodifica devo matenere la consistenza nel numero di round individuali
            GeneratoreInviti gi=new GeneratoreInviti(am,true);
            gi.updateRoundIndiv(r);
        }
  }
  
  /*
   * mauro 30/11/2010
   * metodo da invocare dopo l'aggiornamento del referto per assicurare
   * che l'esito dell'invito sia 'S'
   * 
   */
  public static void updateEsito(ApplicationModule am, Integer idInvito) 
  {
      ADFContext adfCtx = ADFContext.getCurrent();
      Map session = adfCtx.getSessionScope();
    
    String user = (String) session.get("user");
  
    String sql = "update so_invito" + 
    " set codesitoinvito = 'S', opmodifica = '" + user + "', dtultimamod = sysdate" +
    " where idinvito = " + idInvito.toString();
    
    am.getTransaction().executeCommand(sql);
    
    am.getTransaction().commit();
    
  }

  
  /**
   * Metodo che restituisce il numero di vetrino dell'accettazione
   * di primo livello associata all'invito. Se tale accettazione non esiste 
   * viene inserita
   * @return 
   * @param tpscr
   * @param idinvito
   */
  private BigDecimal getVetrino(Integer idinvito,String tpscr,String user,boolean hpv)
  {
    
    Row acc=this.getAcc1liv(idinvito,tpscr,true,user,hpv);
   /* if (tpscr.equals("CI"))
    {
      if (hpv)
          {
            acc.setAttribute("PrelievoHpv",new Integer(2));
          } else 
          {
            acc.setAttribute("PrelievoHpv",new Integer(1));
          }
    }*/
     return (BigDecimal)acc.getAttribute((String)ViewHelper.decodeByTpscr(tpscr,
                                                        "Numvetrino",
                                                        "CodCampione",
                                                        null,
                                                        null,
                                                        null));
  }
  
  /**
   * Cerca l'accettazione associata al referto per aggiornarne il numero di
   * vetrino. Se l'accettazione non c'e' viene creata solo il parametro create 
   * e' true
   * @param create
   * @param tpscr
   * @param numvetrino
   * @param idinvito
   */
  public void updateVetrinoInAcc(Integer idinvito,BigDecimal numvetrino,String tpscr,
  boolean create,String user,boolean hpv)
  {
    Row acc=this.getAcc1liv(idinvito,tpscr,create,user, hpv);
    String attName=(String)ViewHelper.decodeByTpscr(tpscr,"Numvetrino","CodCampione",null,null,null);
    if(!ComparisonUtils.compare(acc.getAttribute(attName),numvetrino)){
        acc.setAttribute(attName,numvetrino);
        if("CO".equals(tpscr))
        {
          acc.setAttribute("Dtultmod",DateUtils.getOracleDateNow());
          acc.setAttribute("Opultmod",user);
        }
    }
   /* if("CI".equals(tpscr))
        {
          if (hpv)
          {
            acc.setAttribute("PrelievoHpv",new Integer(2));
          } else 
          {
            acc.setAttribute("PrelievoHpv",new Integer(1));
          }
        }*/
  }
  
  /**
   * Restituisce l'accettazione dell'invito. Se non c'e', il flag create indica
   * se crearla o meno
   * @return 
   * @param create
   * @param tpscr
   * @param idinvito
   */
  private Row getAcc1liv(Integer idinvito,String tpscr,boolean create,String user, boolean hpv)
  {
  //il vo e' scremato per codts entrando nei ref di 1 livello
  String voName=(String)ViewHelper.decodeByTpscr(tpscr,
                                                "Ref_SoAccCito1livView1",
                                                "Ref_SoAccColon1livView1",
                                                null,null,null);

 
    ViewObject vo=am.findViewObject(voName);
    Row[] result=vo.getFilteredRows("Idinvito",idinvito);
    Row acc;
    if(result.length==0)
    {
      if(create){
      
    
        acc=vo.createRow();
        vo.insertRow(acc);
        acc.setAttribute("Idinvito",idinvito);
        
        if("CI".equals(tpscr))
        {
           acc.setAttribute("Idacc1liv",((Parent_AppModule)am).getNextIdAccettazione());
           acc.setAttribute("Idmot",ConfigurationConstants.DB_FALSE);
           acc.setAttribute("Idtpprelievo",ConfigurationConstants.DB_FALSE);
           acc.setAttribute("Tpscr",tpscr);
           //20110719 Serra: imposto il valore del prelievo HPV in base al default aziendale
           if(hpv)
            acc.setAttribute("PrelievoHpv",ConfigurationConstants.CODICE_PRELIEVO_HPV_ESEGUITO);
          else 
            acc.setAttribute("PrelievoHpv",ConfigurationConstants.CODICE_PRELIEVO_HPV_NON_ESEGUITO);
        }
        else if("CO".equals(tpscr))
        {
         acc.setAttribute("Idaccco1liv",((Parent_AppModule)am).getNextIdAccColon1());
         acc.setAttribute("Dtcreazione",DateUtils.getOracleDateNow());
         acc.setAttribute("Opcreazione",user);
         acc.setAttribute("Dtultmod",DateUtils.getOracleDateNow());
         acc.setAttribute("Opultmod",user);
         
        }
        
        
       
         }
      else
        return null;
      
    }
    else
    {
      acc=result[0];
    //  acc=vo.first();
    }
    
    return acc;
  }
  
  /**
   * Cerca il referto associato all'accettzione e se esiste mantiene consistente
   * il numero di vetrino con quello dell'accettazione
   * @param ref1liv
   * @param acc
   */
  public static void updateVetrinoInRef(Row acc,ViewObject ref1liv)
  {
    //non c'e' nessuna accettazione
    if(acc==null)
      return;
    Row[] result=ref1liv.getFilteredRows("Idinvito",acc.getAttribute("Idinvito"));
    //non c'e' ancora nessun referto da aggiornare
    if(result.length==0)
      return;
    Row ref=result[0];
    String tpscr=(String)ref.getAttribute("Tpscr");
    String refAtt=(String)ViewHelper.decodeByTpscr(tpscr,"Numaccap","CodProvetta",null,null,null);
    String accAtt=(String)ViewHelper.decodeByTpscr(tpscr,"Numvetrino","CodCampione",null,null,null);
    
    //se i due dati non sono uguali, aggiorno il referto
    if(!ComparisonUtils.compare(ref.getAttribute(refAtt),acc.getAttribute(accAtt)))
      ref.setAttribute(refAtt,acc.getAttribute(accAtt));

  }
  
  public static void deleteReferto(Row ref,
                                   ViewObject cnf,
                                   ViewObject interventi,
                                   ViewObject allegati,
                                   ViewObject inviti,
                                   ApplicationModule am,
                                   String user) throws Exception
  {
    RowSetIterator iter=null;
    RowSetIterator cnf_iter=null;
  try{
  //controllo che sia possibile cancellare il rfeerto
  if(ref==null)
        throw new Exception("Nessun referto selezionato per la cancellazione");
      //se il referto e' gia' chiuso non si puo' cancellare
      if(ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Completo")))
        throw new Exception("Il referto risulta gia' chiuso");
        
      Integer idReferto = (Integer) ref.getAttribute("Idreferto");
      
      int livello=1;
      try{
      //se il referto e' di secondo livello e ha intreventi chiusi non si puo' cancellare
      if(ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Intchiusi")))
        throw new Exception("Il referto presenta degli interventi chiusi");
        
      //CANCELLAZIONE:
      //1. rimuovo tutti gli eventuali intreventi
      
      // mauro 21/10/2010, prima salvo su tabella backup
      // 16/12/2010, prima di salvare devo pulire la tabella      
      String sqlDel = "delete from so_interventocito_bck where idreferto = " + idReferto.toString();
      am.getTransaction().executeCommand(sqlDel);       
      // 16/12/2010, fine modifica


      String sql = "insert into so_interventocito_bck " + 
        "select * from so_interventocito where idreferto = " + idReferto.toString();
      
      am.getTransaction().executeCommand(sql);      
      //
      
      iter=ViewHelper.getRowSetIterator(interventi,"iter");

      Row r1=null;
      while(iter.hasNext())
      {
          r1=iter.next();
          if(r1==null)
            continue; 
          else
            r1.remove();
      }
      livello=2;
      
      }catch(JboException jbex)
      {
        //non e' un referto di secondo livello,non ha il campo intchiusi, non faccio nulla
      }

      // mauro 22/10/2010, prima di rimuovere id allegato salvo referto su tabella backup
      if (livello == 1)
      {
        String sql = "insert into so_refertocito1liv_bck " + 
          "select so_refertocito1liv.*, sysdate, '" + user + "'" +
          " from so_refertocito1liv where idreferto = " + idReferto.toString();
        
        am.getTransaction().executeCommand(sql);
        
      }
      else
      {
        String sql = "insert into so_refertocito2liv_bck " + 
          "select so_refertocito2liv.*, sysdate, '" + user + "'" +
          " from so_refertocito2liv where idreferto = " + idReferto.toString();
        
        am.getTransaction().executeCommand(sql);
        
      }
      //
      
      //2. rimuovo l'eventuale allegato
      if(ref.getAttribute("Idallegato")!=null)
      {
        Integer id=(Integer)ref.getAttribute("Idallegato");
        ref.setAttribute("Idallegato", null);
        
        // mauro 21/10/2010, prima salvo su tabella backup
        String sql = "insert into so_allegato_bck " + 
          "select * from so_allegato where idallegato = " + id.toString();
        
        am.getTransaction().executeCommand(sql);              
        //
        
        allegati.setWhereClause("IDALLEGATO="+id);
        allegati.executeQuery();
        if(allegati.first()==null)
            throw new Exception("Lettera con identificativo "+id+" non trovata");
        allegati.first().remove();
      }
      
      //3. rimuovo i dati di configurazione (vo dipendenti dal referto)
      
      // mauro 21/10/2010, prima salvo su tabella backup
      if (livello == 1)
      {
        String sql = "insert into so_codref1livc_bck " + 
          "select * from so_codref1livc where idreferto = " + idReferto.toString();
        
        am.getTransaction().executeCommand(sql);
      }
      else
      {
        String sql = "insert into so_codref2livc2_bck " + 
          "select * from so_codref2livc2 where idreferto = " + idReferto.toString();
        
        am.getTransaction().executeCommand(sql);
        
      }
      //

      cnf_iter=ViewHelper.getRowSetIterator(cnf, "cnf_iter");
      Row codref=cnf_iter.last();
      while(codref!=null)
      {
        codref.remove();
        codref=cnf_iter.last();
      }
      am.getTransaction().postChanges();
      
      //4. resetto il richiamo
  /*    Row[] result1=inviti.getFilteredRows("Idinvito",ref.getAttribute("Idinvito"));
      if(result1.length==0)
        throw new Exception("Invito cui si riferisce il referto non trovato");
      Row invito=result1[0];
      updateInvito((String)ref.getAttribute("Ulss"),
                      (String)ref.getAttribute("Tpscr"),
                      (Integer)ref.getAttribute("Idinvito"),
                      null, //sugg=null
                      inviti,
                      livello,
                      user);*/
         
       //5. rimuovo il referto

      ref.remove();
  }catch(Exception ex)
  {
    throw ex;
  }
    finally
    {
      if(iter!=null)
        iter.closeRowSetIterator();
      if(cnf_iter!=null)
        cnf_iter.closeRowSetIterator();
    }
  }
  
  
  /**
   * Metodo che crea ad inserisce un nuovo referto ed i rispettivi 
   * dati di adeguatezza e giudizio diagnostico
   * @throws java.lang.Exception
   * @param conf_giudia viewobject in cui inserire il giudizio diagnostico
   * @param conf_adepre viewobject in cui inserire l'adeguatezza
   * @param user operatore connesso
   * @param referti viewobject in cui creare il nuovo referto
   * @param inviti viewobject la cui current row e' l'invito cui fara' riferimento 
   * anche il referto
   */
  public Row nuovoRefertoCO(ViewObject inviti, 
                            ViewObject referti, 
                            String user,
                            Integer provetta) throws Exception
  {
    Row r=inviti.getCurrentRow();
    if(r==null)
      throw new Exception("Nessun invito da refertare");
    
  //cerco il numero della provetta, se e' in accettazione
   BigDecimal codcampione=this.getVetrino((Integer)r.getAttribute("Idinvito"),
                                  (String)r.getAttribute("Tpscr"),
                                  user,false);
   
    
    Row nuovoRef=referti.createRow();
            referti.insertRow(nuovoRef);
            //imposto subito l'id perche' potrei doverlo usare per creare 
            //dati nella tabella di cross
            nuovoRef.setAttribute("Idreferto",((Parent_AppModule)am).getNextIdReferto1livCO());
            this.setDatiStandardRefertoCO(nuovoRef,r,user);
           
             //suggerimento non disponbile
            nuovoRef.setAttribute("Idsugg",new Integer(ConfigurationConstants.CODICE_SUGG1L_DEFAULT));
            
            //data del referto: per default la imposto ad oggi
            nuovoRef.setAttribute("Dtreferto",DateUtils.getOracleDateNow());

            //centro di refertazione: derivato dall'invito
            nuovoRef.setAttribute("Idcentroref",r.getAttribute("Idcentroref1liv"));
            
            //codice del campione
            nuovoRef.setAttribute("CodProvetta",codcampione);
            
            //numero di provetta, se ce n'e' piu' d'uno
           if(provetta!=null)
             nuovoRef.setAttribute("NProvetta",provetta);
            
           
            
            this.updateEsito(r,am);
            
      return nuovoRef;
  }
  
  /**
   * Crea ed inserisce un referto di secondo livello
   * @throws java.lang.Exception
   * @return record con il nuovo referto
   * @param user operatore connesso
   * @param referti viewobject in cui inserire il referto
   * @param inviti viewobject con l'invito in uso come riga corrente
   */
  public Row nuovoReferto2livCO(ViewObject inviti, 
                            ViewObject referti, 
                            String user) throws Exception
  {
    Row r=inviti.getCurrentRow();
    if(r==null)
      throw new Exception("Nessun invito da refertare");
    
    Row nuovoRef=referti.createRow();
            referti.insertRow(nuovoRef);
            //imposto subito l'id perche' potrei doverlo usare per creare 
            //dati nella tabella di cross
            nuovoRef.setAttribute("Idreferto",((Parent_AppModule)am).getNextIdReferto2livCO());
            this.setDatiStandardRefertoCO(nuovoRef,r,user);
            
             //suggerimento non disponbile
            nuovoRef.setAttribute("Idsugg2l",ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
            
            //dati peculiari del secondo livello
            //inizio con nessun esame eseguito
            nuovoRef.setAttribute("RxColon",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("ColonTac",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("DiagnosiPeggiore",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("GrDiagnosi",ConfigurationConstants.NOME_GRUPPO_DIAGNOSI);
            nuovoRef.setAttribute("Conclusioni",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("GrConclusioni",ConfigurationConstants.NOME_GRUPPO_CONCL_CO);
  
          //  nuovoRef.setAttribute("Istchirurgica",ConfigurationConstants.DB_FALSE);
            //gli interventi non devono risultare chiusi
            nuovoRef.setAttribute("Intchiusi",ConfigurationConstants.DB_FALSE);
           
            this.updateEsito(r,am);
            
      return nuovoRef;
  }
  
  public static String getServerTime()
  {
    SimpleDateFormat DATE_TIME_FORMATTER= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    return DATE_TIME_FORMATTER.format(new java.util.Date());
  }

  public Row creaLetteraDiReferto(Row ref,int livello, Row primol) throws Exception
  {
    return creaLetteraDiReferto(ref,livello,primol,null);
  }

   public Row creaLetteraDiReferto(Row ref,int livello, Row primol, Row invito) throws Exception
  {
    /*
     * mauro 09/09/2010, inseriti messaggi di logging per problema mancata produzione
     * allegati
     */

    String ulss = (String) ref.getAttribute("Ulss");
    String tpscr = (String) ref.getAttribute("Tpscr");
    System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr +  "/" + getServerTime() + "/creazione singola lettera/inizio metodo" );
    
    ViewObject lettere=am.findViewObject("Ref_SoCnfLetteracentroView1");
    String sugg;
    Integer idsugg;
    switch(livello)
    {
      case 2:
      {
        sugg="Idsugg2l";
        idsugg=(Integer)ref.getAttribute("Idsugg2l");
        break;
      }
      case 3:
      {
        sugg="Idsugg3l";
        idsugg=(Integer)ref.getAttribute("Idsugg3l");
        //devo sostituire l'intervento con il relativo referto
        ViewObject referti=am.findViewObject((String)ViewHelper.decodeByTpscr(tpscr,null,"Ref_SoRefertocolon2livView2","Ref_SoRefertomammo2livView2",null,null));
        referti.setWhereClause("IDREFERTO="+ref.getAttribute("Idreferto"));
        referti.executeQuery();
        ref=referti.first();
   
        break;
      }
      default://primo livello
      {
        sugg="Idsugg";
        idsugg=(Integer)ref.getAttribute("Idsugg");
        break;
      }
    }
     /*20080721 MOD: lettere per eta e centro
       lettere.setWhereClause("TPSCR='"+ref.getAttribute("Tpscr")+"' AND "+
    " ULSS='"+ref.getAttribute("Ulss")+"' AND "+sugg+"="+idsugg);
      */
      //soggetto
    ViewObject soggetti=am.findViewObject("A_SoSoggettoView1");
    soggetti.setWhereClause("CODTS='"+ref.getAttribute("Codts")+"' AND A_SoSoggetto.ULSS='"+ref.getAttribute("Ulss")+"'");
    soggetti.executeQuery();
    /*MOD20071116
    if(soggetti.getRowCount()==0)
      throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda sanitaria "+ulss);    
    A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)soggetti.first();
    */
    A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)soggetti.first();
    if(soggetto==null)
      throw new Exception("Il soggetto con tessera sanitaria="+ref.getAttribute("Codts")+" non e' stato trovato nell'azienda sanitaria "+ref.getAttribute("Ulss"));    
 
   //clacolo l'eta' del soggetto al momento della chiusura del referto, cioe' adesso
    int eta=ViewHelper.etaCompiuta(new java.util.Date(),soggetto.getDataNascita().getValue());
   Integer cc= null;///////slot==null? null:slot.getIdcentro();
 
 
     lettere.setWhereClause("TPSCR='"+ref.getAttribute("Tpscr")+"' AND "+
    " ULSS='"+ref.getAttribute("Ulss")+"' AND "+sugg+"="+idsugg+
      " and   nvl(eta_da,0)<="+eta+
      " and   nvl(eta_a,200)>="+eta);/*+
      " and nvl(centro,"+centro+")="+centro);*/
      //ordino in base al criterio dell'eta' 8prendo il range piu' piccolo che la contiene)
      //e poi, eventualmente, per centro
      lettere.setOrderByClause("("+eta+"-nvl(eta_da,0))+(nvl(eta_a,200)-"+eta+")");//,centro");
      //System.out.println(lettere.getQuery());     
        /*20080721 FINE MOD*/
    
   // System.out.println(sugg+","+ref.getAttribute("Idsugg"));
   
    lettere.executeQuery();
    if(lettere.getRowCount()==0)
       return null;
    Row lettera=lettere.first();
    
    //controllo se ho gia' memorizzato il template
    Lettera l=(Lettera)this.templates.get(lettera.getAttribute("Codtempl"));
    if(l==null){
      ViewObject templates=am.findViewObject("Ref_SoTemplateView1");
      templates.setWhereClause("CODTEMPL="+lettera.getAttribute("Codtempl"));
      templates.executeQuery();
      if(templates.first()==null)
          throw new Exception("Richiesto e non trovato il template con codice "+lettera.getAttribute("Codtempl"));
          
     //creo l'oggetto lettera che mi servira' per creare il pdf
    //  l=new Lettera((BlobDomain)templates.first().getAttribute("Filexml"),"Referto"+System.currentTimeMillis());
     l=new Lettera(templates.first(),"Ref_SoTemplateView1","Filexml","Compiled","Referto"+System.currentTimeMillis(),ConfigurationConstants.FORMATO_PDF);
     
      this.templates.put(lettera.getAttribute("Codtempl"),l);
    }

      l.setParametersMap((String)ref.getAttribute("Ulss"),(String)ref.getAttribute("Tpscr"),
        am.findViewObject("A_SoAziendaView1"),
        am.findViewObject("A_SoCnfPartemplateView1"));
    
    Integer id_primol;
    if(primol==null)
      id_primol=null;
    else
      id_primol=(Integer)primol.getAttribute("Idinvito");
    
    //creo il bean
    LetteraRefertoCitoBean lb;
    switch(livello)
    {

      case 2:
      {
        lb=this.createLetteraBean2L((String)ref.getAttribute("Codts"),
                                              (String)ref.getAttribute("Ulss"),
                                              (String)ref.getAttribute("Tpscr"),
                                              ref,
                                              livello,
                                              id_primol);
        break;
      }
      case 3:
      {
        lb=this.createLetteraBean2L((String)ref.getAttribute("Codts"),
                                              (String)ref.getAttribute("Ulss"),
                                              (String)ref.getAttribute("Tpscr"),
                                              ref,
                                              livello,
                                              id_primol,
                                              idsugg);
        break;
      }
      
      default: //primo livello
      {
        lb=this.createLetteraBean1L((String)ref.getAttribute("Codts"),
                                              (String)ref.getAttribute("Ulss"),
                                              (String)ref.getAttribute("Tpscr"),
                                              ref,
                                              null,
                                              null,
                                              invito);
        break;
      }
    }
    
    
                                              
    //creo l'allegato
    File pdf=l.createLetter(lb);

    System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr +  "/" + getServerTime() + "/creazione singola lettera/pdf creato" );
    
    //creo il record per salvare l'allegato
    ViewObject all=am.findViewObject("Ref_SoAllegatoView1");
    Row allegato=all.createRow();
    all.insertRow(allegato);
    Integer id=((Parent_AppModule)am).getNextIdAllegato();
    allegato.setAttribute("Idallegato",id);
    allegato.setAttribute("Codts",ref.getAttribute("Codts"));
    allegato.setAttribute("Dtcreazione",DateUtils.getOracleDateNow());
  // allegato.setAttribute("Idinvito",null);
   allegato.setAttribute("Tpscr",ref.getAttribute("Tpscr"));
   allegato.setAttribute("Ulss",ref.getAttribute("Ulss"));
   allegato.setAttribute("Lettera",BlobUtils.getBlobFromFile(pdf));
   
   pdf.delete();
   
   // mauro 10/02/2010
   l.release();
   //   
   
   //così obbligo la transazione a scrivere prima l'allegato cui il referto 
   //fara' poi riferimento (per evitare problemi con le chiavi esterne)
   am.getTransaction().postChanges();

    System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr +  "/" + getServerTime() + "/creazione singola lettera/effettuato postchanges" );
   
   ref.setAttribute("Idallegato",allegato.getAttribute("Idallegato"));
   ref.setAttribute("Dtcreazione",allegato.getAttribute("Dtcreazione"));
  
     return allegato;
                                              
    
    
  }
  
  /**
   * Cancella un referto del colo-retto (senza fare rollback)
   * @throws java.lang.Exception
   * @param am application module
   * @param inviti vo con l'invito correlato al referto
   * @param allegati vo con gli allegati
   * @param interventi
   * @param endo
   * @param ref
   */
  public static void deleteRefertoCO(Row ref,
                                   ViewObject endo,
                                   ViewObject interventi,
                                   ViewObject allegati,
                                   int livello,
                                   ApplicationModule am,
                                   String user) throws Exception
  {
      RowSetIterator iter=null;
          RowSetIterator endo_iter=null;
        try{
        //controllo che sia possibile cancellare il rfeerto
        if(ref==null)
              throw new Exception("Nessun referto selezionato per la cancellazione");
            //se il referto e' gia' chiuso non si puo' cancellare
            if(ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Completo")))
              throw new Exception("Il referto risulta gia' chiuso");
            
            Integer idReferto = (Integer) ref.getAttribute("Idreferto");
            
            try{
          
            //se il referto e' di secondo livello e ha intreventi chiusi non si puo' cancellare
            if(ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Intchiusi")))
              throw new Exception("Il referto presenta degli interventi chiusi");
              
            //CANCELLAZIONE:
            //1. rimuovo tutti gli eventuali intreventi

            // mauro 09/11/2010, prima salvo su tabella backup (interventi e endoscopie)
            // 16/12/2010, prima di salvare devo pulire la tabella      
            String sqlDel = "delete from so_interventocolon_bck where idreferto = " + idReferto.toString();
            am.getTransaction().executeCommand(sqlDel);       
            // 16/12/2010, fine modifica

            String sql = "insert into so_interventocolon_bck " + 
              "select * from so_interventocolon where idreferto = " + idReferto.toString();
            
            am.getTransaction().executeCommand(sql);      

            sql = "insert into so_endoscopia_bck " + 
              "select * from so_endoscopia where idreferto = " + idReferto.toString();
            
            am.getTransaction().executeCommand(sql);      

            //

            iter=ViewHelper.getRowSetIterator(interventi,"iter");

            Row r1=null;
            while(iter.hasNext())
            {
                r1=iter.next();
                if(r1==null)
                  continue; 
                else
                  r1.remove();
            }
       
            //cancellazione di ogni endoscopia e dei relativi dati di configuraione
             
              Ref_SoEndoscopiaViewRow r=(Ref_SoEndoscopiaViewRow)endo.last();
              while(r!=null)
              {
                endo.setCurrentRow(r);
                r.remove();
                
                r=(Ref_SoEndoscopiaViewRow)endo.last();
              }
              
            
            }catch(JboException jbex)
            {
              //non e' un referto di secondo livello,non ha il campo intchiusi, non faccio nulla
            }

            // mauro 09/11/2010, prima di rimuovere id allegato salvo referto su tabella backup
            if (livello == 1)
            {
              String sql = "insert into so_refertocolon1liv_bck " + 
                "select so_refertocolon1liv.*, sysdate, '" + user + "'" +
                " from so_refertocolon1liv where idreferto = " + idReferto.toString();
              
              am.getTransaction().executeCommand(sql);
              
            }
            else
            {
              String sql = "INSERT INTO SO_REFERTOCOLON2LIV_BCK (" + 
                "IDREFERTO, " +
                "IDINVITO, " +
                "CODTS, " +
                "ULSS, " +
                "TPSCR, " +
                "RX_COLON, " +
                "DT_RX_COLON, " +
                "QUALITA, " +
                "GR_QUALITA, " +
                "RX_CONCL, " +
                "GR_RX_CONCL, " +
                "IDMEDICO_RX, " +
                "NOTE_RX, " +
                "DTCONCL, " +
                "IDMED_CONCL, " +
                "IDSUGG2L, " +
                "CONCLUSIONI, " +
                "GR_CONCLUSIONI, " +
                "DIAGNOSI_PEGGIORE, " +
                "GR_DIAGNOSI, " +
                "NOTE, " +
                "COMPLETO, " +
                "INTCHIUSI, " +
                "DTINSERIMENTO, " +
                "OPINSERIMENTO, " +
                "DTULTMODIFICA, " +
                "OPULTMODIFICA, " +
                "TPREFERTO, " +
                "IDALLEGATO, " +
                "DTCREAZIONE, " +
                "DTSPEDIZIONE, " +
                "COLON_TAC, " +
                "DT_COLON_TAC, " +
                "QUALITA_TAC, " +
                "GR_QUALITA_TAC, " +
                "TAC_CONCL, " +
                "GR_TAC_CONCL, " +
                "IDMED_TAC, " +
                "NOTE_TAC, " +
                "DTCANC, " +
                "OPCANC" +
                ") SELECT " +
                "IDREFERTO, " +
                "IDINVITO, " +
                "CODTS, " +
                "ULSS, " +
                "TPSCR, " +
                "RX_COLON, " +
                "DT_RX_COLON, " +
                "QUALITA, " +
                "GR_QUALITA, " +
                "RX_CONCL, " +
                "GR_RX_CONCL, " +
                "IDMEDICO_RX, " +
                "NOTE_RX, " +
                "DTCONCL, " +
                "IDMED_CONCL, " +
                "IDSUGG2L, " +
                "CONCLUSIONI, " +
                "GR_CONCLUSIONI, " +
                "DIAGNOSI_PEGGIORE, " +
                "GR_DIAGNOSI, " +
                "NOTE, " +
                "COMPLETO, " +
                "INTCHIUSI, " +
                "DTINSERIMENTO, " +
                "OPINSERIMENTO, " +
                "DTULTMODIFICA, " +
                "OPULTMODIFICA, " +
                "TPREFERTO, " +
                "IDALLEGATO, " +
                "DTCREAZIONE, " +
                "DTSPEDIZIONE, " +
                "COLON_TAC, " +
                "DT_COLON_TAC, " +
                "QUALITA_TAC, " +
                "GR_QUALITA_TAC, " +
                "TAC_CONCL, " +
                "GR_TAC_CONCL, " +
                "IDMED_TAC, " +
                "NOTE_TAC, " +
                "SYSDATE, '" + user + "' " +
                "FROM SO_REFERTOCOLON2LIV WHERE IDREFERTO = " + idReferto.toString();
              
              am.getTransaction().executeCommand(sql);
              
            }
            //
            
            //2. rimuovo l'eventuale allegato
            if(ref.getAttribute("Idallegato")!=null)
            {
              Integer id=(Integer)ref.getAttribute("Idallegato");
              ref.setAttribute("Idallegato", null);

              // mauro 09/11/2010, prima salvo su tabella backup
              String sql = "insert into so_allegato_bck " + 
                "select * from so_allegato where idallegato = " + id.toString();
              
              am.getTransaction().executeCommand(sql);              
              //
              
              allegati.setWhereClause("IDALLEGATO="+id);
              allegati.executeQuery();
              if(allegati.first()==null)
                  throw new Exception("Lettera con identificativo "+id+" non trovata");
              allegati.first().remove();
            }     
           
            am.getTransaction().postChanges();
            
               
             //5. rimuovo il referto
            ref.remove();
        }catch(Exception ex)
        {
          throw ex;
        }
          finally
          {
            if(iter!=null)
              iter.closeRowSetIterator();
            
          }
  }
  
  
	/**
	* Metodo che crea ad inserisce un nuovo referto 
	* @throws java.lang.Exception
	* @param user operatore connesso
	* @param referti viewobject in cui creare il nuovo referto
	* @param inviti viewobject la cui current row e' l'invito cui fara' riferimento 
	* anche il referto
	*/
	public Row nuovoRefertoMA(ViewObject inviti, 
	                          ViewObject referti, 
	                          String user) throws Exception
	{
		Row r = inviti.getCurrentRow();
		if(r == null)
			throw new Exception("Nessun invito da refertare");

		Ref_SoRefertomammo1livViewRow nuovoRef = (Ref_SoRefertomammo1livViewRow)referti.createRow();
		referti.insertRow(nuovoRef);

		// Imposto subito l'id perche' potrei doverlo usare per creare 
		// dati nella tabella di cross.
		nuovoRef.setAttribute("Idreferto", ((Parent_AppModule)am).getNextIdRef1livMA());
		this.setDatiStandardRefertoCO(nuovoRef, r, user);
		
		//suggerimento non disponbile
		nuovoRef.setIdsugg(ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
		nuovoRef.setL1Sugg(ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
		nuovoRef.setL2Sugg(ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
		nuovoRef.setRSugg(ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
		nuovoRef.setL1Esito(ConfigurationConstants.CODICE_SUGG1L_DEFAULT.intValue());
		nuovoRef.setL2Esito(ConfigurationConstants.CODICE_SUGG1L_DEFAULT.intValue());
		nuovoRef.setREsito(ConfigurationConstants.CODICE_SUGG1L_DEFAULT.intValue());
		nuovoRef.setEsito(ConfigurationConstants.CODICE_SUGG1L_DEFAULT.intValue());
		nuovoRef.setGrEsito(ConfigurationConstants.NOME_GRUPPO_ESITO_MA);
		
		//data del referto: per default la imposto ad oggi
		nuovoRef.setDtreferto(DateUtils.getOracleDateNow());

		//centro di refertazione: derivato dall'invito
		nuovoRef.setIdcentroref((Integer)r.getAttribute("Idcentroref1liv"));

		//data mammografia=data invito
		nuovoRef.setDtmammo((oracle.jbo.domain.Date)r.getAttribute("Dtapp"));

		this.updateEsito(r, am);
    
		return nuovoRef;
	}
  
  /**
   * Crea ed inserisce un referto di secondo livello
   * @throws java.lang.Exception
   * @return record con il nuovo referto
   * @param user operatore connesso
   * @param referti viewobject in cui inserire il referto
   * @param inviti viewobject con l'invito in uso come riga corrente
   */
  public Row nuovoReferto2livMA(ViewObject inviti, 
                            ViewObject referti, 
                            String user) throws Exception
  {
    Row r=inviti.getCurrentRow();
    if(r==null)
      throw new Exception("Nessun invito da refertare");
    
    Row nuovoRef=referti.createRow();
            referti.insertRow(nuovoRef);
            //imposto subito l'id perche' potrei doverlo usare per creare 
            //dati nella tabella di cross
            nuovoRef.setAttribute("Idreferto",((Parent_AppModule)am).getNextIdRef2livMA());
            this.setDatiStandardRefertoCO(nuovoRef,r,user);
            
             //suggerimento non disponbile
            nuovoRef.setAttribute("Idsugg2l",ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
            
            //dati peculiari del secondo livello
            //inizio con nessun esame eseguito
            nuovoRef.setAttribute("Mammo",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("Eco",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("Clinico",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("CitologiaDx",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("CitologiaSx",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("AgobDx",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("AgobSx",ConfigurationConstants.DB_FALSE);
            
            nuovoRef.setAttribute("ConsiglioRadio",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("GrConsiglioRadio",ConfigurationConstants.NOME_GRUPPO_ESITO_RADIO);
            nuovoRef.setAttribute("GrExec",ConfigurationConstants.NOME_GRUPPO_EXEC2L);
            nuovoRef.setAttribute("GrLocalizz",ConfigurationConstants.NOME_GRUPPO_LOCALIZZ);
            nuovoRef.setAttribute("GrEsitoCito",ConfigurationConstants.NOME_GRUPPO_ESITO_CITO);
            nuovoRef.setAttribute("GrEsitoAgob",ConfigurationConstants.NOME_GRUPPO_ESITO_AGOB);
            nuovoRef.setAttribute("GrTecnicaExec",ConfigurationConstants.NOME_GRUPPO_TECNICA_EXEC);

           
            nuovoRef.setAttribute("DiagnosiPeggiore",ConfigurationConstants.DB_FALSE);
            nuovoRef.setAttribute("GrDiagnosi",ConfigurationConstants.NOME_GRUPPO_RACDIA_2L);
           
         
            //gli interventi non devono risultare chiusi
            nuovoRef.setAttribute("Intchiusi",ConfigurationConstants.DB_FALSE);
           
            this.updateEsito(r,am);
            
      return nuovoRef;
  }
  
	/**
	 * Cancella un referto del mammografico (senza fare rollback)
	 * @throws java.lang.Exception
	 * @param am application module
	 * @param inviti vo con l'invito correlato al referto
	 * @param allegati vo con gli allegati
	 * @param ref
	 */
	public static void deleteRefertoMA(Row ref,
			ViewObject interventi,
			ViewObject allegati,
			ViewObject inviti,
			ApplicationModule am,
			String user
		) throws Exception
	{
		RowSetIterator iter=null;
		String update=null;

		try {
			//controllo che sia possibile cancellare il referto
			if (ref == null)
				throw new Exception("Nessun referto selezionato per la cancellazione");

			//se il referto e' gia' chiuso non si puo' cancellare
			if(ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Completo")))
				throw new Exception("Il referto risulta gia' chiuso");

			int livello=1;
			
			Integer idReferto = (Integer) ref.getAttribute("Idreferto");
			
			try {
				//se il referto e' di secondo livello e ha intreventi chiusi non si puo' cancellare
				if(ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Intchiusi")))
					throw new Exception("Il referto presenta degli interventi chiusi");

				//CANCELLAZIONE:
				//1. rimuovo tutti gli eventuali interventi
				// mauro 21/10/2010, prima salvo su tabelle bck
				// 16/12/2010, prima di salvare devo pulire le tabelle
				String sqlDel = "delete from SO_CODREF3LIVMA_BCK where idint in " +
					"(select idint from so_interventomammo_bck where idreferto = " + 
					idReferto.toString() + ")";
				am.getTransaction().executeCommand(sqlDel); 
      
				sqlDel = "delete from so_interventomammo_bck where idreferto = " + idReferto.toString();
				am.getTransaction().executeCommand(sqlDel);       
				// 16/12/2010, fine modifica
				
				String sql = "insert into so_interventomammo_bck " + 
					"select * from so_interventomammo where idreferto = " + idReferto.toString();
				
				am.getTransaction().executeCommand(sql);
				
				sql = "insert into SO_CODREF3LIVMA_BCK " + 
					"select * from SO_CODREF3LIVMA c where c.idint in " + 
					"(SELECT a.idint FROM so_interventomammo a where a.idreferto = " +
					idReferto.toString() + ")";
				
				am.getTransaction().executeCommand(sql);
				
				iter=ViewHelper.getRowSetIterator(interventi,"iter");
				
				Row r1=null;
				while(iter.hasNext()) {
					r1=iter.next();
					if(r1==null)
						continue; 
					else {
						update="DELETE FROM SO_CODREF3LIVMA WHERE IDINT=" + r1.getAttribute("Idint");
						am.getTransaction().executeCommand(update);
						r1.remove();
					}
				}
				livello=2;
			} catch (JboException jbex) {
				//non e' un referto di secondo livello, non ha il campo intchiusi,
				//cancello tutte le eventuali indicazioni del primo livello
				
				// mauro 21/10/2010, prima salvo su bck
				String sql = "insert into SO_CODREF1LIVMA_BCK " +
					"select * from SO_CODREF1LIVMA where idreferto = " + idReferto.toString();
				
				am.getTransaction().executeCommand(sql);
				//
				
				update="DELETE FROM SO_CODREF1LIVMA WHERE ULSS='"+
					ref.getAttribute("Ulss")+"' AND TPSCR='"+ref.getAttribute("Tpscr")+"' AND IDREFERTO="+
					ref.getAttribute("Idreferto");
				am.getTransaction().executeCommand(update);
			}

			// mauro 22/10/2010, devo backuppare il referto prima di rimuovere il riferimento
			// all'allegato
			if (livello == 1) {
				String sql = "INSERT INTO SO_REFERTOMAMMO1LIV_BCK (" +
					"IDREFERTO, " +
					"IDINVITO, " +
					"CODTS, " +
					"ULSS, " +
					"TPSCR, " +
					"GR_ESITO, " +
					"L1_RADIOLOGO, " +
					"L1_ESITO, " +
					"L1_SUGG, " +
					"L1_ALTRE_INDICAZIONI, " +
					"L1_NOTE, " +
					"L2_RADIOLOGO, " +
					"L2_ESITO, " +
					"L2_SUGG, " +
					"L2_ALTRE_INDICAZIONI, " +
					"L2_NOTE, " +
					"REVISIONE, " +
					"R_RADIOLOGO, " +
					"R_ESITO, " +
					"R_SUGG, " +
					"R_ALTRE_INDICAZIONI, " +
					"R_NOTE, " +
					"IDSUGG, " +
					"ESITO, " +
					"DTMAMMO, " +
					"DTREFERTO, " +
					"CODICE_REFERTO, " +
					"IDCENTROREF, " +
					"COMPLETO, " +
					"DTINSERIMENTO, " +
					"OPINSERIMENTO, " +
					"DTULTMODIFICA, " +
					"OPULTMODIFICA, " +
					"TPREFERTO, " +
					"IDALLEGATO, " +
					"DTCREAZIONE, " +
					"DTSPEDIZIONE, " +
					"L1_IDCENTROREF, " +
					"L2_IDCENTROREF, " +
					"R_IDCENTROREF, " +
					"DENSITA, " +
					"DTCANC, " +
					"OPCANC" +
					") SELECT " +
					"IDREFERTO, " +
					"IDINVITO, " +
					"CODTS, " +
					"ULSS, " +
					"TPSCR, " +
					"GR_ESITO, " +
					"L1_RADIOLOGO, " +
					"L1_ESITO, " +
					"L1_SUGG, " +
					"L1_ALTRE_INDICAZIONI, " +
					"L1_NOTE, " +
					"L2_RADIOLOGO, " +
					"L2_ESITO, " +
					"L2_SUGG, " +
					"L2_ALTRE_INDICAZIONI, " +
					"L2_NOTE, " +
					"REVISIONE, " +
					"R_RADIOLOGO, " +
					"R_ESITO, " +
					"R_SUGG, " +
					"R_ALTRE_INDICAZIONI, " +
					"R_NOTE, " +
					"IDSUGG, " +
					"ESITO, " +
					"DTMAMMO, " +
					"DTREFERTO, " +
					"CODICE_REFERTO, " +
					"IDCENTROREF, " +
					"COMPLETO, " +
					"DTINSERIMENTO, " +
					"OPINSERIMENTO, " +
					"DTULTMODIFICA, " +
					"OPULTMODIFICA, " +
					"TPREFERTO, " +
					"IDALLEGATO, " +
					"DTCREAZIONE, " +
					"DTSPEDIZIONE, " +
					"L1_IDCENTROREF, " +
					"L2_IDCENTROREF, " +
					"R_IDCENTROREF, " +
					"DENSITA, " +
					"SYSDATE, '" + user + "' " +
					"FROM SO_REFERTOMAMMO1LIV WHERE IDREFERTO = " + idReferto.toString();

				am.getTransaction().executeCommand(sql);
			} else {
				String sql = "insert into so_refertomammo2liv_bck " + 
					"select so_refertomammo2liv.*, sysdate, '" + user + "'" +
					" from so_refertomammo2liv where idreferto = " + idReferto.toString();
	
				am.getTransaction().executeCommand(sql);
			}
			//

      //2. rimuovo l'eventuale allegato
      if(ref.getAttribute("Idallegato")!=null)
      {
        Integer id=(Integer)ref.getAttribute("Idallegato");
        ref.setAttribute("Idallegato", null);
 
        // mauro 21/10/2010, prima salvo su tabella backup
        String sql = "insert into so_allegato_bck " + 
          "select * from so_allegato where idallegato = " + id.toString();
        
        am.getTransaction().executeCommand(sql);              
        //
        
        allegati.setWhereClause("IDALLEGATO="+id);
        allegati.executeQuery();
        if(allegati.first()==null)
            throw new Exception("Lettera con identificativo "+id+" non trovata");
        allegati.first().remove();
      }
      
      
     
      am.getTransaction().postChanges();
      
      //4. resetto il richiamo
  /*    Row[] result1=inviti.getFilteredRows("Idinvito",ref.getAttribute("Idinvito"));
      if(result1.length==0)
        throw new Exception("Invito cui si riferisce il referto non trovato");
      Row invito=result1[0];
      updateInvito((String)ref.getAttribute("Ulss"),
                      (String)ref.getAttribute("Tpscr"),
                      (Integer)ref.getAttribute("Idinvito"),
                      null, //sugg=null
                      inviti,
                      livello,
                      user);*/
       
       //5. rimuovo i dati di configurzione della mamografia
        // mauro 21/10/2010, prima salvo su bck
        String sql = "insert into SO_CODREF2LIVMA_BCK " + 
          "select * from SO_CODREF2LIVMA where idreferto = " + idReferto.toString();
        
        am.getTransaction().executeCommand(sql);
        //

       update="DELETE FROM SO_CODREF2LIVMA WHERE IDREFERTO="+ref.getAttribute("Idreferto");
        am.getTransaction().executeCommand(update);
         
       //6. rimuovo il referto


      ref.remove();
  }catch(Exception ex)
  {
    throw ex;
  }
    finally
    {
      if(iter!=null)
        iter.closeRowSetIterator();
      
    }
  }
  
}