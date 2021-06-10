package model.inviti;

import insiel.dataHandling.DateUtils;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.accettazione.A_SoAccCito2livViewRowImpl;
import model.accettazione.A_SoAccColon2livViewRowImpl;
import model.accettazione.A_SoAccPfas1livViewRowImpl;
import model.accettazione.common.A_SoAccColon1livViewRow;

import model.common.A_SoAccCito1livView1Row;
import model.common.A_SoAccMammo1livViewRow;
import model.common.A_SoAccMammo2livViewRow;
import model.common.Sogg_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.Progressivi;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.Sogg_NuovoParam;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.SequenceImpl;

public class InvitoUtils 
{

  // Chiave di lunghezza arbitraria, i caratteri non devono essere ripetuti
  // Tolti 0, O, I, 1, che potrebbero essere confusi
  private static final String key = "ABCDEFGHLMNPQRSTUVZ23456789";
  private static final int RESULT_LENGTH = 8;

  public static int getEtaSoggetto(ApplicationModule am, String codTS, String ulss, Date dtRif)
  {
    String qrySogg = "SELECT a.DATA_NASCITA FROM so_soggetto a where a.codts = '" + codTS + 
      "' and a.ulss = '" + ulss + "'";

    ViewObject voSogg = am.createViewObjectFromQueryStmt("",qrySogg);
    voSogg.setRangeSize(-1);
    voSogg.executeQuery();
    
    Row result = voSogg.first();
    
    Date dtNasc = (Date) result.getAttribute(0);
    
    java.util.Date dtBase = (dtRif == null ? new java.util.Date() : dtRif.getValue());
    
    int eta = ViewHelper.etaCompiuta(dtBase, dtNasc.getValue());
    
    return eta;
    
  }
  
  public static boolean gestioneRigLettera
  (
    ApplicationModule am,
    String ulss,
    String tpscr,
    String idTpInvito,
    String codTs,
    Integer idApp,
    Integer idInvito,
    Integer idCtPrel,
    Date dtApp,
    String otp
  )
  throws Exception
  {
    return gestioneRigLettera(am, ulss, tpscr, idTpInvito, codTs, idApp, idInvito, idCtPrel, dtApp, null, otp);
  }

  // 5/11/2009, metodo che rigenera la lettera se non stampata
  public static boolean gestioneRigLettera
  (
    ApplicationModule am,
    String ulss,
    String tpscr,
    String idTpInvito,
    String codTs,
    Integer idApp,
    Integer idInvito,
    Integer idCtPrel,
    Date dtApp,
    String testProposto,
    String otp
  )
  throws Exception
  {
  
    int eta = getEtaSoggetto(am,codTs,ulss,dtApp);
    
    String qLetteraCt = "select 1 from SO_CNF_LETTERACENTRO " +
    "where IDTPINVITO='"+idTpInvito+"' AND ULSS='"+ulss+"' AND TPSCR='"+tpscr+"'"+
      " and nvl(eta_da,0)<="+eta+
      " and nvl(eta_a,200)>="+eta;
    if(idCtPrel!=null)
         qLetteraCt+=" and nvl(centro,"+idCtPrel+")="+idCtPrel;
    
    ViewObject voLettCt = am.createViewObjectFromQueryStmt("",qLetteraCt);
    voLettCt.setRangeSize(-1);
    voLettCt.executeQuery();
    
    int cnt = voLettCt.getRowCount();
    
    if (cnt == 0) return false; // non c'� un template per il tipo invito
    
    GeneratoreInviti gen = new GeneratoreInviti(am,codTs,ulss,tpscr);

    String qry = "SELECT a.idallegato, a.dtstampa FROM so_allegato a " +
                  "where a.idinvito = " + idInvito.toString();
    ViewObject vo = am.createViewObjectFromQueryStmt("",qry);
    vo.setRangeSize(-1);
    vo.executeQuery();
    
    Row allegato = vo.first();
    
    if (allegato != null)     
    {
    
      Date dtStampa = (Date) allegato.getAttribute(1);    
      if (dtStampa != null) return false;
      
      Integer idAllegato = allegato.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) allegato.getAttribute(0)).intValue() : null;        
      gen.deleteLettera(idAllegato);
      
      //trovo la tessera sanitaria
      String tesseraSanitaria = SoggUtils.trovaTessera(am,codTs,ulss);
      
      gen.creaLettera(ulss,tpscr,idTpInvito,codTs,idApp,idInvito,testProposto,tesseraSanitaria,otp); 
      
      return true;
        
    }
    
    // se la lettera non c'� non la devo creare!!
    return false;
  }


  public static boolean invitoSpostatoCi
                                  (
                                  ApplicationModule am,
                                  Integer idInvito,
                                  Date dtInvito,
                                  Integer ctRef
                                  )
  {
    String qry = "SELECT a.idcentroref1liv, a.dtapp FROM so_invito a " +
                  "where a.idinvito = " + idInvito.toString();
    ViewObject vo = am.createViewObjectFromQueryStmt("",qry);
    vo.setRangeSize(-1);
    vo.executeQuery();
    
    Row inv = vo.first();
    Date dtInvOld = (Date) inv.getAttribute(1);
    Integer ctRefOld = inv.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) inv.getAttribute(0)).intValue() : null;    
    
    // questa funzione viene invocata solo se dtInvito e ctRef sono non nulli
    
    if (ctRefOld == null)
    {
      return true;
    }
    
    if (ctRefOld.intValue() != ctRef.intValue())
    {
      return true;
    }
    
    if (dtInvOld == null)
    {
      return true;
    }
    
    String strDt = DateUtils.dateToString(dtInvito.dateValue());
    String strDtOld = DateUtils.dateToString(dtInvOld.dateValue());
    return (!strDt.equals(strDtOld));

  }


  public static boolean invitoSpostatoCo
                                  (
                                  ApplicationModule am,
                                  Integer idInvito,
                                  Date dtInvito,
                                  Integer ctRef
                                  )
  {
    String qry = "SELECT a.idcentroref1liv, a.dtapp FROM so_invito a " +
                  "where a.idinvito = " + idInvito.toString();
    ViewObject vo = am.createViewObjectFromQueryStmt("",qry);
    vo.setRangeSize(-1);
    vo.executeQuery();
    
    Row inv = vo.first();
    Date dtInvOld = (Date) inv.getAttribute(1);
    Integer ctRefOld = inv.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) inv.getAttribute(0)).intValue() : null;        
    
    // questa funzione viene invocata solo se dtInvito e ctRef sono non nulli
    
    if (ctRefOld == null)
    {
      return true;
    }
    
    if (ctRefOld.intValue() != ctRef.intValue())
    {
      return true;
    }
    
    if (dtInvOld == null)
    {
      return true;
    }
    
    String strDt = DateUtils.dateToString(dtInvito.dateValue());
    String strDtOld = DateUtils.dateToString(dtInvOld.dateValue());
    return (!strDt.equals(strDtOld));

  }


  public static boolean invitoSpostatoMx
                                  (
                                  ApplicationModule am,
                                  Integer idInvito,
                                  Date dtInvito,
                                  Integer ctRef
                                  )
  {
      String qry = "SELECT a.idcentroref1liv, a.dtapp FROM so_invito a " +
                  "where a.idinvito = " + idInvito.toString();
    ViewObject vo = am.createViewObjectFromQueryStmt("",qry);
    vo.setRangeSize(-1);
    vo.executeQuery();
    
      Row inv = vo.first();
      Date dtInvOld = (Date) inv.getAttribute(1);
      Number ctRefOld = (Number) inv.getAttribute(0);
      
      if (ctRefOld == null)
      {
        return true;
      }
      
      if (ctRef.intValue() != ctRefOld.intValue())
      {
        return true;
      }
      
      if (dtInvOld == null)
      {
        if (dtInvito == null)
        {
          return false;
        }
        else
        {
          return true;
        }
      }
      else
      {
        if (dtInvito == null)
        {
          return true;
        }
        else
        {
          String strDt = DateUtils.dateToString(dtInvito.getValue());
          String strDtOld = DateUtils.dateToString(dtInvOld.getValue());
          return (!strDt.equals(strDtOld));
        }
      }
      
  }
  
  public static boolean invitoSpostatoPf
                                  (
                                  ApplicationModule am,
                                  Integer idInvito,
                                  Date dtInvito,
                                  Integer ctRef
                                  )
  {
    String qry = "SELECT a.idcentroref1liv, a.dtapp FROM so_invito a " +
                  "where a.idinvito = " + idInvito.toString();
    ViewObject vo = am.createViewObjectFromQueryStmt("",qry);
    vo.setRangeSize(-1);
    vo.executeQuery();
    
    Row inv = vo.first();
    Date dtInvOld = (Date) inv.getAttribute(1);
    Integer ctRefOld = inv.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) inv.getAttribute(0)).intValue() : null;
    
    // questa funzione viene invocata solo se dtInvito e ctRef sono non nulli
    
    if (ctRefOld == null)
    {
      return true;
    }
    
    if (ctRefOld.intValue() != ctRef.intValue())
    {
      return true;
    }
    
    if (dtInvOld == null)
    {
      return true;
    }
    
    String strDt = DateUtils.dateToString(dtInvito.dateValue());
    String strDtOld = DateUtils.dateToString(dtInvOld.dateValue());
    return (!strDt.equals(strDtOld));

  }
  
	public static A_SoAccCito1livView1Row gesAccCito(ApplicationModule am,
		Integer idInvito,
		Date dtInvito,
		int livInvito,
		Integer ctRef,
		String user,
		String ulss, 
		String tpscr,
		Boolean hpv,
		boolean nuovoApp,
    String test_proposto) throws Exception
	{
	    ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");

		// mauro 30/03/2010, metodo ora ritorna riga di accettazione 
		// per permettere di accedere ai codici generati
		
		String prodNcamp = ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_CAMPIONE);
		String prodNrich = ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
		String prodNrich2 = ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_RICHIESTA2);

		if (livInvito == 1 && prodNcamp.equals("N") && prodNrich.equals("N")) {
			return null;
		} else if (livInvito == 2 && prodNrich2.equals("N")) {
			return null;
		}

		if (livInvito == 1 && dtInvito != null && ctRef != null) {
			// controllo se esiste gi� un'accettazione, in tale caso 
			// il codice va rigenerato solo se trattasi di spostamento ed
			// � previsto in cfg di rigenerare
			int count = 0;
			
			ViewObject voAcc = am.findViewObject("A_SoAccCito1livView1_1");
			
			if (!nuovoApp) {      
				voAcc.setWhereClause("IDINVITO = " + idInvito.toString());
				voAcc.executeQuery();
				count = voAcc.getRowCount();
			}
			
			if (count == 0) {
				
				// vado avanti con generazione progressivi
				A_SoAccCito1livView1Row accRow = (A_SoAccCito1livView1Row)voAcc.createRow();
				
				SequenceImpl s = new SequenceImpl("SO_ACCETTAZIONI_SEQ", am);         
				Integer idAcc = s.getSequenceNumber().intValue();
				accRow.setIdacc1liv(idAcc);
				accRow.setIdinvito(idInvito);
				accRow.setIdmot(0);
				accRow.setIdtpprelievo(0);
				accRow.setTpscr("CI");

				//gaion 07/06/2011
				if (hpv != null) {
					if (hpv.booleanValue() && "HPV".equals(test_proposto)) {
						accRow.setPrelievoHpv(ConfigurationConstants.CODICE_PRELIEVO_HPV_ESEGUITO);
					} else {
						accRow.setPrelievoHpv(ConfigurationConstants.CODICE_PRELIEVO_HPV_NON_ESEGUITO);
					}
				} else {
					accRow.setPrelievoHpv(ConfigurationConstants.CODICE_PRELIEVO_HPV_ND);
				}
				// fine gaion 07/06/2011

				// I00093449
				boolean gestioneUnicaCodici = false;
				try{
				    String prodNrichCamp =
				        ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_UNQ_RIC_CAM);
				    
				    gestioneUnicaCodici = "S".equals(prodNrichCamp);
				}catch(Throwable th){
				    gestioneUnicaCodici = false;
				}
				
				if(gestioneUnicaCodici){
				    BigDecimal prog = Progressivi.getProgressivo(am, ConfigurationConstants.PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE, ulss,
				                                                tpscr, ctRef, dtInvito);
				    accRow.setNumvetrino(prog);
				    accRow.setCodRichiesta(prog);
				}else{
				// I00093449
                                    if (prodNcamp.equals("S")) {
                                            BigDecimal codCamp = Progressivi.getProgressivo(am,
                                                    ConfigurationConstants.PROGRESSIVO_CAMPIONE,
                                                    ulss,
                                                    tpscr,
                                                    ctRef,
                                                    dtInvito
                                                    );
                                            accRow.setNumvetrino(codCamp); 
                                    }
    
                                    if (prodNrich.equals("S")) {
                                            BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHIESTA,ulss,
                                                    tpscr,ctRef,dtInvito);
                                            accRow.setCodRichiesta(codRich);
                                    }
                                }
                                
                                voAcc.insertRow(accRow);
				
				return accRow;
			} else { // count > 0
				
				// Aggiorno prelievo HPV
				A_SoAccCito1livView1Row accRow = (A_SoAccCito1livView1Row)voAcc.first();
          
          if (hpv != null) {
            if (hpv.booleanValue() && "HPV".equals(test_proposto)) {
              accRow.setPrelievoHpv(ConfigurationConstants.CODICE_PRELIEVO_HPV_ESEGUITO);
            } else {
              accRow.setPrelievoHpv(ConfigurationConstants.CODICE_PRELIEVO_HPV_NON_ESEGUITO);
            }
          } else {
            accRow.setPrelievoHpv(ConfigurationConstants.CODICE_PRELIEVO_HPV_ND);
          }
        

				// generazione progressivi solo se c'� spostamento
				// e rig. prevista in cfg
				// (da implementare)
				String rigNcamp = ParametriSistema.getParamValue(voParam, ulss, tpscr, "rig_codice_campione");
				String rigNrich = ParametriSistema.getParamValue(voParam, ulss, tpscr, "rig_codice_richiesta");
				
				if (rigNcamp.equals("N") && rigNrich.equals("N")) {
					return accRow;
				}
				
				boolean spostatoInvito = invitoSpostatoCi(am, idInvito, dtInvito, ctRef);
				
				if (spostatoInvito) {
					// ricalcolo progressivo
					A_SoAccCito1livView1Row currAcc = (A_SoAccCito1livView1Row)voAcc.first();
					
                                        // I00093449
                                        boolean gestioneUnicaCodici = false;
                                        try{
                                            String prodNrichCamp =
                                                ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_UNQ_RIC_CAM);
                                            
                                            gestioneUnicaCodici = "S".equals(prodNrichCamp);
                                        }catch(Throwable th){
                                            gestioneUnicaCodici = false;
                                        }
                                        
                                        if(gestioneUnicaCodici){
                                            BigDecimal prog = Progressivi.getProgressivo(am, ConfigurationConstants.PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE, ulss,
                                                                                        tpscr, ctRef, dtInvito);
                                            currAcc.setNumvetrino(prog);
                                            currAcc.setCodRichiesta(prog);
                                        }else{
                                        // I00093449
                                            if (rigNcamp.equals("S")) {
                                                    BigDecimal codCamp = Progressivi.getProgressivo(am, ConfigurationConstants.PROGRESSIVO_CAMPIONE, ulss,
                                                            tpscr,ctRef,dtInvito);
                                                    currAcc.setNumvetrino(codCamp);
                                            }
                                            
                                            if (rigNrich.equals("S")) {
                                                    BigDecimal codRich = Progressivi.getProgressivo(am, ConfigurationConstants.PROGRESSIVO_RICHIESTA, ulss,
                                                            tpscr, ctRef, dtInvito);
                                                    currAcc.setCodRichiesta(codRich);
                                            }
                                        }
					
					return currAcc;
				}
				return accRow;
			}
		}

		//27122013 Gaion: generazione codice richiesta 2 liv
		if (livInvito == 2 && dtInvito != null) {
			// controllo se esiste gi� un'accettazione
			int count = 0;
			
			ViewObject voAcc = am.findViewObject("A_SoAccCito2livView1");
			
			if (!nuovoApp) {      
				voAcc.setWhereClause("IDINVITO = " + idInvito.toString());
				voAcc.executeQuery();
				count = voAcc.getRowCount();
			}
			
			if (count == 0) {
				// vado avanti con generazione progressivi
				A_SoAccCito2livViewRowImpl accRow = (A_SoAccCito2livViewRowImpl) voAcc.createRow();
				
				SequenceImpl s = new SequenceImpl("SO_ACCETTAZIONI2_SEQ", am);         
				Integer idAcc = s.getSequenceNumber().intValue();
				accRow.setIdacc2liv(idAcc);
				accRow.setIdinvito(idInvito);
				accRow.setIdmot(0);
				accRow.setTpscr("CI");
				
				if (prodNrich.equals("S")) {
					BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHIESTA2,ulss,
						tpscr,ctRef,dtInvito);
					accRow.setCodRichiesta(codRich);
				}
				voAcc.insertRow(accRow);
			}
			// se � un 2 liv posso tornare null
			return null;
		}
		return null;
	}
  
  public static A_SoAccColon1livViewRow gesAccColon(ApplicationModule am,
                                  Integer idInvito,
                                  Date dtInvito,
                                  int livInvito,
                                  Integer ctRef,
                                  String user,
                                  String ulss, 
                                  String tpscr) 
      throws Exception
  {
      ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
      
    // metodo invocato per la generazione dei progressivi del colon
    // se non ho data invito e ctref non posso generare nulla quindi
    // non faccio nulla, inoltre controllo anche che siamo al primo
    // livello altrimenti non devo fare nulla
    String prodNcamp = ParametriSistema.getParamValue(voParam,ulss,tpscr,ConfigurationConstants.PARAMETRO_PROD_CAMPIONE);
    String prodNrich = ParametriSistema.getParamValue(voParam,ulss,tpscr,ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
    String prodNrich2 = ParametriSistema.getParamValue(voParam,ulss,tpscr,ConfigurationConstants.PARAMETRO_PROD_RICHIESTA2);
    if (livInvito == 1 && prodNcamp.equals("N") && prodNrich.equals("N"))
    {
      return null;
    } else if (livInvito == 2 && prodNrich2.equals("N"))
    {
      return null;
    }

    if (livInvito == 1 && dtInvito != null && ctRef != null)
    {
      // controllo se esiste gi� un'accettazione, in tale caso 
      // il codice va rigenerato solo se trattasi di spostamento ed
      // � previsto in cfg di rigenerare
      ViewObject voAcc = am.findViewObject("A_SoAccColon1livView1");
      voAcc.setWhereClause("IDINVITO = " + idInvito.toString());
      voAcc.executeQuery();
      int count = voAcc.getRowCount();
      
      if (count == 0)
      {
        // vado avanti con generazione progressivi
        A_SoAccColon1livViewRow nAcc = (A_SoAccColon1livViewRow) voAcc.createRow();
        
        SequenceImpl s = new SequenceImpl("SO_ACCCOLON1LIV_SEQ", am);         
        Integer idAcc = s.getSequenceNumber().intValue();
        nAcc.setIdaccco1liv(idAcc);
        nAcc.setIdinvito(idInvito);
        nAcc.setDtcreazione(DateUtils.getOracleDateNow());
        nAcc.setOpcreazione(user);
        nAcc.setDtultmod(DateUtils.getOracleDateNow());
        nAcc.setOpultmod(user);

          // I00093449
          boolean gestioneUnicaCodici = false;
          try{
              String prodNrichCamp =
                  ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_UNQ_RIC_CAM);
              
              gestioneUnicaCodici = "S".equals(prodNrichCamp);
          }catch(Throwable th){
              gestioneUnicaCodici = false;
          }
          
          if(gestioneUnicaCodici){
              BigDecimal prog = Progressivi.getProgressivo(am, ConfigurationConstants.PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE, ulss,
                                                          tpscr, ctRef, dtInvito);
              nAcc.setCodCampione(prog);
              nAcc.setCodRichiesta(prog);
          }else{
          // I00093449
            if (prodNcamp.equals("S"))
            {
              BigDecimal codCamp = Progressivi.getProgressivo(am,
                                        ConfigurationConstants.PROGRESSIVO_CAMPIONE,
                                        ulss,
                                        tpscr,
                                        ctRef,
                                        dtInvito
                                        );
              nAcc.setCodCampione(codCamp); 
            }
            if (prodNrich.equals("S"))
            {
              BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHIESTA,ulss,
                                                          tpscr,ctRef,dtInvito);
              nAcc.setCodRichiesta(codRich);
            }
            voAcc.insertRow(nAcc); 
          }
        
        return nAcc;
      }
      else
      {
        A_SoAccColon1livViewRow currAcc = (A_SoAccColon1livViewRow) voAcc.first();
        
        // generazione progressivi solo se c'� spostamento
        // e rig. prevista in cfg
        String rigNcamp = ParametriSistema.getParamValue(voParam,ulss,tpscr,"rig_codice_campione");
        String rigNrich = ParametriSistema.getParamValue(voParam,ulss,tpscr,"rig_codice_richiesta");
        
        if (rigNcamp.equals("N") && rigNrich.equals("N"))
        {
          return currAcc;
        }
        
        boolean spostatoInvito = invitoSpostatoCo(am,idInvito,dtInvito,ctRef);
        
        if (spostatoInvito)
        {
          // ricalcolo progressivo
          
          // I00093449
          boolean gestioneUnicaCodici = false;
          try{
              String prodNrichCamp =
                  ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_UNQ_RIC_CAM);
              
              gestioneUnicaCodici = "S".equals(prodNrichCamp);
          }catch(Throwable th){
              gestioneUnicaCodici = false;
          }
          
          if(gestioneUnicaCodici){
              BigDecimal prog = Progressivi.getProgressivo(am, ConfigurationConstants.PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE, ulss,
                                                          tpscr, ctRef, dtInvito);
              currAcc.setCodCampione(prog);
              currAcc.setCodRichiesta(prog);
          }else{
          // I00093449
              if (rigNcamp.equals("S"))
              {
                BigDecimal codCamp = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_CAMPIONE,ulss,
                                                            tpscr,ctRef,dtInvito);
                currAcc.setCodCampione(codCamp);
              }
    
              if (rigNrich.equals("S"))
              {
                BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHIESTA,ulss,
                                                            tpscr,ctRef,dtInvito);
                currAcc.setCodRichiesta(codRich);
              }
          }

          currAcc.setDtultmod(DateUtils.getOracleDateNow());
          currAcc.setOpultmod(user);         

        }
        
        return currAcc;
                             
      }
              
    }
    
    if (livInvito == 2 && dtInvito != null)
    {
      ViewObject voAcc = am.findViewObject("A_SoAccColon2livView1");
      voAcc.setWhereClause("IDINVITO = " + idInvito.toString());
      voAcc.executeQuery();
      int count = voAcc.getRowCount();
      
      if (count == 0)
      {
        // vado avanti con generazione progressivi
        A_SoAccColon2livViewRowImpl nAcc = (A_SoAccColon2livViewRowImpl) voAcc.createRow();
        
        SequenceImpl s = new SequenceImpl("SO_ACCCOLON2LIV_SEQ", am);         
        Integer idAcc = s.getSequenceNumber().intValue();
        nAcc.setIdaccco2liv(idAcc);
        nAcc.setIdinvito(idInvito);
        nAcc.setDtcreazione(DateUtils.getOracleDateNow());
        nAcc.setOpcreazione(user);
        nAcc.setDtultmod(DateUtils.getOracleDateNow());
        nAcc.setOpultmod(user);
        
        if (prodNrich.equals("S"))
        {
          BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHIESTA2,ulss,
                                                      tpscr,ctRef,dtInvito);
          nAcc.setCodRichiesta(codRich);
        }
        voAcc.insertRow(nAcc);
      
      } 
    }
    
    return null;
    
  }


  public static A_SoAccMammo1livViewRow gesAccMammo(ApplicationModule am,
                                  Integer idInvito,
                                  Date dtInvito,
                                  int livInvito,
                                  String ulss,
                                  Integer ctRef,
                                  String user,
                                  String tpscr) 
    throws Exception
  {
      ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");

    if (dtInvito != null && ctRef != null)
    // in realt� il centro di prelievo dovrebbe essere sempre non nullo
    {
      if (livInvito == 1)
      {
        ViewObject voAcc = am.findViewObject("A_SoAccMammo1livView1");
        voAcc.setWhereClause("IDINVITO = " + idInvito.toString());
        voAcc.executeQuery();
        int count = voAcc.getRowCount();
        
        if (count == 0)
        {
  
          A_SoAccMammo1livViewRow nAcc = (A_SoAccMammo1livViewRow) voAcc.createRow();
          SequenceImpl s = new SequenceImpl("SO_ACCMAMMO1LIV_SEQ", am);         
          Integer idAcc = s.getSequenceNumber().intValue();
          nAcc.setIdaccma1liv(idAcc);
          nAcc.setIdinvito(idInvito);
          nAcc.setDtcreazione(DateUtils.getOracleDateNow());
          nAcc.setOpcreazione(user);
          nAcc.setDtultmod(DateUtils.getOracleDateNow());
          nAcc.setOpultmod(user);
          
          BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHMX,ulss,tpscr,
                                                      ctRef,dtInvito);
          nAcc.setCodRichiesta(codRich);
          
          voAcc.insertRow(nAcc); 
          
          return nAcc;
        
        }
        else
        {
          A_SoAccMammo1livViewRow currAcc = 
                (A_SoAccMammo1livViewRow) voAcc.first();
          String rigCod = ParametriSistema.getParamValue(voParam,ulss,tpscr,"rig_codice_richiesta");
          if (rigCod.equals("S"))
          {
            boolean spostatoInvito = invitoSpostatoMx(am,idInvito,dtInvito,ctRef);
            if (spostatoInvito)
            {
              // ricalcolo progressivo  
              BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHMX,ulss,
                                                          tpscr,ctRef,dtInvito);
              currAcc.setCodRichiesta(codRich);
    
              currAcc.setDtultmod(DateUtils.getOracleDateNow());
              currAcc.setOpultmod(user);
            }
          }
          return currAcc;
        }
        
      }
      else   
      {
        // 2 livello
        ViewObject voAcc = am.findViewObject("A_SoAccMammo2livView1");
        voAcc.setWhereClause("IDINVITO = " + idInvito.toString());
        voAcc.executeQuery();
        int count = voAcc.getRowCount();
        
        if (count == 0)
        {
  
          A_SoAccMammo2livViewRow nAcc = (A_SoAccMammo2livViewRow) voAcc.createRow();
          SequenceImpl s = new SequenceImpl("SO_ACCMAMMO2LIV_SEQ", am);         
          Integer idAcc = s.getSequenceNumber().intValue();
          nAcc.setIdaccma2liv(idAcc);
          nAcc.setIdinvito(idInvito);
          nAcc.setDtcreazione(DateUtils.getOracleDateNow());
          nAcc.setOpcreazione(user);
          nAcc.setDtultmod(DateUtils.getOracleDateNow());
          nAcc.setOpultmod(user);
  
          BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHIESTA2,ulss,tpscr,
                                                      ctRef,dtInvito);
          nAcc.setCodRichiesta(codRich);
          voAcc.insertRow(nAcc); 
        
        }
        else
        {
  
          String rigCod = ParametriSistema.getParamValue(voParam,ulss,tpscr,"rig_codice_richiesta2");
          if (rigCod.equals("S"))
          {
          
            boolean spostatoInvito = invitoSpostatoMx(am,idInvito,dtInvito,ctRef);
            if (spostatoInvito)
            {            
              // ricalcolo progressivo
              A_SoAccMammo2livViewRow currAcc = 
                (A_SoAccMammo2livViewRow) voAcc.first();
              BigDecimal codRich = Progressivi.getProgressivo(am,"RICHIESTA2",ulss,tpscr,
                                                          ctRef,dtInvito);
              currAcc.setCodRichiesta(codRich);
              
              currAcc.setDtultmod(DateUtils.getOracleDateNow());
              currAcc.setOpultmod(user);              
            }
          
          }
          
        }
        
      }
    
    }
    
    return null;
    
  }
  
  public static A_SoAccPfas1livViewRowImpl gesAccPfas(ApplicationModule am,
                                  Integer idInvito,
                                  Date dtInvito,
                                  int livInvito,
                                  Integer ctRef,
                                  String user,
                                  String ulss, 
                                  String tpscr) 
      throws Exception
  {
      ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");

    // metodo invocato per la generazione dei progressivi del colon
    // se non ho data invito e ctref non posso generare nulla quindi
    // non faccio nulla, inoltre controllo anche che siamo al primo
    // livello altrimenti non devo fare nulla
    String prodNcamp = ParametriSistema.getParamValue(voParam,ulss,tpscr,ConfigurationConstants.PARAMETRO_PROD_CAMPIONE);
    String prodNrich = ParametriSistema.getParamValue(voParam,ulss,tpscr,ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);

    if (livInvito == 1 && prodNcamp.equals("N") && prodNrich.equals("N"))
    {
      return null;
    } 

    if (livInvito == 1 && dtInvito != null && ctRef != null)
    {
      // controllo se esiste gi� un'accettazione, in tale caso 
      // il codice va rigenerato solo se trattasi di spostamento ed
      // � previsto in cfg di rigenerare
      ViewObject voAcc = am.findViewObject("A_SoAccPfas1livView1");
      voAcc.setWhereClause("IDINVITO = " + idInvito.toString());
      voAcc.executeQuery();
      int count = voAcc.getRowCount();
      
      if (count == 0)
      {
        // vado avanti con generazione progressivi
        A_SoAccPfas1livViewRowImpl nAcc = (A_SoAccPfas1livViewRowImpl) voAcc.createRow();
        
        SequenceImpl s = new SequenceImpl("SO_ACCPFAS1LIV_SEQ", am);         
        Integer idAcc = s.getSequenceNumber().intValue();
        nAcc.setIdaccpf1liv(idAcc);
        nAcc.setIdinvito(idInvito);
        nAcc.setDtcreazione(DateUtils.getOracleDateNow());
        nAcc.setOpcreazione(user);
        nAcc.setDtultmod(DateUtils.getOracleDateNow());
        nAcc.setOpultmod(user);
          // I00093449
          boolean gestioneUnicaCodici = false;
          try{
              String prodNrichCamp =
                  ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_UNQ_RIC_CAM);
              
              gestioneUnicaCodici = "S".equals(prodNrichCamp);
          }catch(Throwable th){
              gestioneUnicaCodici = false;
          }
          
          if(gestioneUnicaCodici){
              BigDecimal prog = Progressivi.getProgressivo(am, ConfigurationConstants.PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE, ulss,
                                                          tpscr, ctRef, dtInvito);
              nAcc.setCodCampione(prog);
              nAcc.setCodRichiesta(prog);
          }else{
          // I00093449
            if (prodNcamp.equals("S"))
            {
              BigDecimal codCamp = Progressivi.getProgressivo(am,
                                        ConfigurationConstants.PROGRESSIVO_CAMPIONE,
                                        ulss,
                                        tpscr,
                                        ctRef,
                                        dtInvito
                                        );
              nAcc.setCodCampione(codCamp); 
            }
            if (prodNrich.equals("S"))
            {
              BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHIESTA,ulss,
                                                          tpscr,ctRef,dtInvito);
              nAcc.setCodRichiesta(codRich);
            }
          }
          
        voAcc.insertRow(nAcc);    
        
        return nAcc;
      }
      else
      {
        A_SoAccPfas1livViewRowImpl currAcc = 
            (A_SoAccPfas1livViewRowImpl) voAcc.first();
        // generazione progressivi solo se c'� spostamento
        // e rig. prevista in cfg
        String rigNcamp = ParametriSistema.getParamValue(voParam,ulss,tpscr,"rig_codice_campione");
        String rigNrich = ParametriSistema.getParamValue(voParam,ulss,tpscr,"rig_codice_richiesta");
        
        if (rigNcamp.equals("N") && rigNrich.equals("N"))
        {
          return currAcc;
        }
        
        boolean spostatoInvito = invitoSpostatoPf(am,idInvito,dtInvito,ctRef);
        
        if (spostatoInvito)
        {
            // I00093449
            boolean gestioneUnicaCodici = false;
            try{
                String prodNrichCamp =
                    ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_UNQ_RIC_CAM);
                
                gestioneUnicaCodici = "S".equals(prodNrichCamp);
            }catch(Throwable th){
                gestioneUnicaCodici = false;
            }
            
            if(gestioneUnicaCodici){
                BigDecimal prog = Progressivi.getProgressivo(am, ConfigurationConstants.PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE, ulss,
                                                            tpscr, ctRef, dtInvito);
                currAcc.setCodCampione(prog);
                currAcc.setCodRichiesta(prog);
            }else{
            // I00093449
              // ricalcolo progressivo     
              if (rigNcamp.equals("S"))
              {
                BigDecimal codCamp = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_CAMPIONE,ulss,
                                                            tpscr,ctRef,dtInvito);
                currAcc.setCodCampione(codCamp);
              }
    
              if (rigNrich.equals("S"))
              {
                BigDecimal codRich = Progressivi.getProgressivo(am,ConfigurationConstants.PROGRESSIVO_RICHIESTA,ulss,
                                                            tpscr,ctRef,dtInvito);
                currAcc.setCodRichiesta(codRich);
              }
            }

          currAcc.setDtultmod(DateUtils.getOracleDateNow());
          currAcc.setOpultmod(user);

        }
           
        return currAcc;                     
      }
           
    } 
    
    return null;
  }

  public static DatiRichiamo getDatiRichiamo(ApplicationModule am, String livello,
    String esito, Integer ctInvito, String ulss, String tpscr)
  {
    String qEsito = "select SO_CNF_ESITOINVITO.IDTPINVITO, SO_CNF_ESITOINVITO.GGRICHIAMO, " +
      "SO_CNF_TPINVITO.LIVELLO from SO_CNF_ESITOINVITO, SO_CNF_TPINVITO where " +
        "SO_CNF_ESITOINVITO.CODESITOINVITO = '" + esito + 
        "' and SO_CNF_ESITOINVITO.LIVESITO = " + livello + 
        " and SO_CNF_ESITOINVITO.ULSS = '" + ulss + 
        "' and SO_CNF_ESITOINVITO.TPSCR = '" + tpscr + 
        "' and SO_CNF_TPINVITO.IDTPINVITO = SO_CNF_ESITOINVITO.IDTPINVITO" +
        " and SO_CNF_TPINVITO.ulss = SO_CNF_ESITOINVITO.ulss" +
        " and SO_CNF_TPINVITO.tpscr = SO_CNF_ESITOINVITO.tpscr";
    ViewObject voEsiti = am.createViewObjectFromQueryStmt("",qEsito);
    voEsiti.setRangeSize(-1);
    voEsiti.executeQuery();
    
    Row fEsito = voEsiti.first();  
    
    if (fEsito != null){
      String tpRichiamo= (String) fEsito.getAttribute(0);
      Integer ggRichiamo= fEsito.getAttribute(1)!=null ? ((oracle.jbo.domain.Number) fEsito.getAttribute(1)).intValue() : null;
      Integer livRich = fEsito.getAttribute(2)!=null ? ((oracle.jbo.domain.Number) fEsito.getAttribute(2)).intValue() : null;
      
      int livInv = Integer.parseInt(livello);
      Integer idCentroRichiamo=null;
      int detCt = 1;
      
      if (livInv == 1)
      {
        if (livRich.intValue() == 1)
        {
          idCentroRichiamo = ctInvito;
        }
        else
        {
          // centro richiamo � il centro di II livello associato
          // al centro di primo livello dell'invito
          String qCt = "SELECT IDCENTRO2LIV FROM SO_CENTRO_PREL_REF " +
            "WHERE IDCENTRO = " + ctInvito.toString();
          ViewObject voCt = am.createViewObjectFromQueryStmt("",qCt);
          voCt.setRangeSize(-1);
          voCt.executeQuery();
          
          Row fCt = voCt.first();
          idCentroRichiamo = fCt.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) fCt.getAttribute(0)).intValue() : null;
          
        }
        
      }
      else
      {
        if (livRich.intValue() == 2)
        {
          idCentroRichiamo = ctInvito;
        }
        else
        {
          // centro richiamo dipende dalla persona
          detCt = 0;
          
        }
        
      }
      
      DatiRichiamo datiR = new DatiRichiamo(detCt, idCentroRichiamo, tpRichiamo, ggRichiamo);
      return datiR;
      
    } else 
    {
      return null;
    }
  }

    public static void cancellaInvito(ApplicationModule am,Integer idInv,
      String codTs,String ulss,String tpscr, String user)
      throws Exception
    {
      if (tpscr.equals("CI"))
      {
              
        String qAcc1 = "SELECT IDACC1LIV FROM SO_ACC_CITO1LIV WHERE IDINVITO = " +
          idInv.toString();
        ViewObject voAcc1 = am.createViewObjectFromQueryStmt("",qAcc1);
        voAcc1.setRangeSize(-1);
        voAcc1.executeQuery();      
        
        int countAcc1 = voAcc1.getRowCount();
        
        if (countAcc1 > 0)
        {
          Row acc1 = voAcc1.first();
          Integer idAcc = acc1.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) acc1.getAttribute(0)).intValue() : null;
          
            //backup
          String bckAcc = "insert into SO_ACC_CITO1LIV_BCK " +
                      "select SO_ACC_CITO1LIV.*, sysdate, '" + user + "'" +
                      "from SO_ACC_CITO1LIV where IDACC1LIV = " + idAcc;
          am.getTransaction().executeCommand(bckAcc);
          
          String delInt = "delete from SO_INTERVENTO_PREC where IDACC1LIV = " +
            idAcc.toString();
          am.getTransaction().executeCommand(delInt);
    
          // mauro 20/07/2010, aggiunta canc. anamnesi
          String delAnamCI = "delete from SO_ANAMNESI_CITO where IDACC1LIV = " +
            idAcc.toString();
          am.getTransaction().executeCommand(delAnamCI);
          
          String delAcc = "delete from SO_ACC_CITO1LIV where IDACC1LIV = " +
            idAcc.toString();              
          am.getTransaction().executeCommand(delAcc);
        }
    
    
        String qAcc2 = "SELECT IDACC2LIV FROM SO_ACC_CITO2LIV WHERE IDINVITO = " +
          idInv.toString();
        ViewObject voAcc2 = am.createViewObjectFromQueryStmt("",qAcc2);
        voAcc2.setRangeSize(-1);
        voAcc2.executeQuery();
        
        int countAcc2 = voAcc2.getRowCount();
        
        if (countAcc2 > 0)
        {
          Row acc2 = voAcc2.first();
          Integer idAcc = acc2.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) acc2.getAttribute(0)).intValue() : null;
          
            //backup
          String bckAcc = "insert into SO_ACC_CITO2LIV_BCK " +
                      "select SO_ACC_CITO2LIV.*, sysdate, '" + user + "'" +
                      "from SO_ACC_CITO2LIV where IDACC2LIV = " + idAcc;
          am.getTransaction().executeCommand(bckAcc);
          
          String delInt = "delete from SO_INTERVENTO_PREC2LIV where IDACC2LIV = " +
            idAcc.toString();
          am.getTransaction().executeCommand(delInt);
          
          // mauro 20/07/2010, aggiunta canc. anamnesi
          String delAnamCI = "delete from SO_ANAMNESI_CITO where IDACC2LIV = " +
            idAcc.toString();
          am.getTransaction().executeCommand(delAnamCI);

          String delAcc = "delete from SO_ACC_CITO2LIV where IDACC2LIV = " +
            idAcc.toString();              
          am.getTransaction().executeCommand(delAcc);
        }
      
      }
      else if (tpscr.equals("CO"))
      {
       //backup
       String bckAcc = "insert into SO_ACC_COLON1LIV_BCK " +
                  "select SO_ACC_COLON1LIV.*, sysdate, '" + user + "'" +
                  "from SO_ACC_COLON1LIV where IDINVITO = " + idInv.toString();
       am.getTransaction().executeCommand(bckAcc); 
          
        String delAcc1Co = "delete from SO_ACC_COLON1LIV where IDINVITO = " +
          idInv.toString();              
        am.getTransaction().executeCommand(delAcc1Co);

          String delInvMot = "delete from SO_INV_MOTIVO where IDINVITO = " +
            idInv.toString();              
          am.getTransaction().executeCommand(delInvMot);

        String qAcc2Co = "SELECT IDACCCO_2LIV FROM SO_ACC_COLON2LIV WHERE IDINVITO = " +
          idInv.toString();
        ViewObject voAcc2Co = am.createViewObjectFromQueryStmt("",qAcc2Co);
        voAcc2Co.setRangeSize(-1);
        voAcc2Co.executeQuery();

        int countAcc2Co = voAcc2Co.getRowCount();
        
        if (countAcc2Co > 0)
        {
          Row acc2Co = voAcc2Co.first();
          Integer idAcc2Co = acc2Co.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) acc2Co.getAttribute(0)).intValue() : null;
          
            //backup
          String bckAcc2 = "insert into SO_ACC_COLON2LIV_BCK " +
                      "select SO_ACC_COLON2LIV.*, sysdate, '" + user + "'" +
                      "from SO_ACC_COLON2LIV where IDACCCO_2LIV = " + idAcc2Co;
          am.getTransaction().executeCommand(bckAcc2);
          
          String delAnam = "delete from SO_ANAMNESICOLON where IDACCCO_2LIV = " +
            idAcc2Co.toString();
          am.getTransaction().executeCommand(delAnam);
          
          String delAcc2Co = "delete from SO_ACC_COLON2LIV where IDACCCO_2LIV = " +
            idAcc2Co.toString();              
          am.getTransaction().executeCommand(delAcc2Co);
        }
        
      }
      else if (tpscr.equals("MA"))
      {
        String qAcc1Ma = "SELECT a.idaccma_1liv FROM so_acc_mammo1liv a " +
          "where a.idinvito = " + idInv.toString();
        ViewObject voAcc1Ma = am.createViewObjectFromQueryStmt("",qAcc1Ma);
        voAcc1Ma.setRangeSize(-1);
        voAcc1Ma.executeQuery();
        
        int countAcc1Ma = voAcc1Ma.getRowCount();
        
        if (countAcc1Ma > 0)
        {
          Row acc1Ma = voAcc1Ma.first();
          Integer idAcc1Ma = acc1Ma.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) acc1Ma.getAttribute(0)).intValue() : null;
          
            //backup
          String bckAcc = "insert into SO_ACC_MAMMO1LIV_BCK " +
                      "select SO_ACC_MAMMO1LIV.*, sysdate, '" + user + "'" +
                      "from SO_ACC_MAMMO1LIV where IDACCMA_1LIV = " + idAcc1Ma;
          am.getTransaction().executeCommand(bckAcc);
          
          String delSintAn = "delete FROM so_anamnesimx_sintomi a " + 
            "where a.id_anamma in (select so_anamnesimx.id_anamma from " + 
            "so_anamnesimx where so_anamnesimx.idaccma_1liv = " + 
            idAcc1Ma.toString() + ")";
          am.getTransaction().executeCommand(delSintAn);
          
          String delAnMx1 = "delete from SO_ANAMNESIMX where IDACCMA_1LIV = " +
            idAcc1Ma.toString();
          am.getTransaction().executeCommand(delAnMx1);
          
          String delAcc1Ma = "delete FROM so_acc_mammo1liv a " +
            "where a.idaccma_1liv = " + idAcc1Ma.toString();
          am.getTransaction().executeCommand(delAcc1Ma);
          
        }
        else
        {
          String qAcc2Ma = "SELECT a.idaccma_2liv FROM so_acc_mammo2liv a " +
            "where a.idinvito = " + idInv.toString();
          ViewObject voAcc2Ma = am.createViewObjectFromQueryStmt("",qAcc2Ma);
          voAcc2Ma.setRangeSize(-1);
          voAcc2Ma.executeQuery();
          
          int countAcc2Ma = voAcc2Ma.getRowCount();
          
          if (countAcc2Ma > 0)
          {
            Row acc2Ma = voAcc2Ma.first();
            Integer idAcc2Ma = acc2Ma.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) acc2Ma.getAttribute(0)).intValue() : null;
            
              //backup
            String bckAcc = "insert into SO_ACC_MAMMO2LIV_BCK " +
                        "select SO_ACC_MAMMO2LIV.*, sysdate, '" + user + "'" +
                        "from SO_ACC_MAMMO2LIV where IDACCMA_2LIV = " + idAcc2Ma;
            am.getTransaction().executeCommand(bckAcc);
            
            String delSintAn = "delete FROM so_anamnesimx_sintomi a " + 
              "where a.id_anamma in (select so_anamnesimx.id_anamma from " + 
              "so_anamnesimx where so_anamnesimx.idaccma_2liv = " + 
              idAcc2Ma.toString() + ")";
            am.getTransaction().executeCommand(delSintAn);

            String delAnMx2 = "delete from SO_ANAMNESIMX where IDACCMA_2LIV = " +
              idAcc2Ma.toString();
            am.getTransaction().executeCommand(delAnMx2);
            
            String delAcc2Ma = "delete FROM so_acc_mammo2liv a " +
              "where a.idaccma_2liv = " + idAcc2Ma.toString();
            am.getTransaction().executeCommand(delAcc2Ma);
            
          }
          
        }
        
      }
       //20170126 Serra: cancellazione screening PFAS
          else if (tpscr.equals("PF"))
      {
        String delAcc1Co = "delete from SO_ACC_PFAS1LIV where IDINVITO = " +
          idInv.toString();              
        am.getTransaction().executeCommand(delAcc1Co);

        
      }
      
      String updEscl = "update SO_ESCLUSIONE set IDINVITO = null where IDINVITO = " +
        idInv.toString();
      am.getTransaction().executeCommand(updEscl);
      
        //backup
      String bckLettera = "insert into SO_ALLEGATO_BCK " +
        "select * " +
        "from SO_ALLEGATO where DTSTAMPA IS NOT NULL AND IDINVITO = " + idInv.toString();
      am.getTransaction().executeCommand(bckLettera);
        
      String delAlleg = "delete from SO_ALLEGATO where IDINVITO = " +
        idInv.toString();  
      am.getTransaction().executeCommand(delAlleg);
      
        //backup
      String bckInvito = "insert into SO_INVITO_BCK " +
            "select SO_INVITO.*, sysdate, '" + user + "' " +
            "from SO_INVITO where IDINVITO = " + idInv.toString();
      am.getTransaction().executeCommand(bckInvito);
      
      String delInvito = "delete from SO_INVITO where IDINVITO = " + idInv.toString(); 
      am.getTransaction().executeCommand(delInvito);
      
      // mettere attivo l'ultimo invito
      ViewObject voSel = am.findViewObject("Sogg_NuovoInvitoAttivoView1");
      String whcl = "CODTS = '" + codTs + "' and ULSS = '" + ulss + 
      "' and TPSCR = '" + tpscr + "'";
      voSel.setWhereClause(whcl);
        voSel.setOrderByClause("DTAPP desc, LIVELLO desc, DTULTIMAMOD DESC, DTCREAZIONE DESC");
          
      voSel.executeQuery();
     
     /*MOD20071116 
      int cSel = voSel.getRowCount();
      
      if (cSel > 0)
      {
        Row fInv = voSel.first();
        */
      Row fInv = voSel.first();
      if(fInv!=null)
      {
            
        Integer invAtt = (Integer) fInv.getAttribute("Idinvito");
        String updInv = "update SO_INVITO set ATTIVO = 1 where IDINVITO = " +
          invAtt.toString();
        am.getTransaction().executeCommand(updInv);
        
        // aggiornamento del round
        GeneratoreInviti gen = new GeneratoreInviti(am,
                                                  (String) fInv.getAttribute("Codts"),
                                                  (String) fInv.getAttribute("Ulss"),
                                                  (String) fInv.getAttribute("Tpscr"));

       /* Integer x=*/gen.updateAndGetRoundInviti(fInv);                                                        
        
      }
    
     else
      {
      /* MOD 20081212 versione 1.2: il record in so_sogg_scr non va cancellato,
     * ma solo aggiornato 
     *
        String delScr = "delete from SO_SOGG_SCR where CODTS = '" + codTs + 
          "' and ULSS = '" + ulss + "' and TPSCR = '" + tpscr + "'" ;
        am.getTransaction().executeCommand(delScr);
        */
        
        String updScr="update SO_SOGG_SCR set roundindiv=0, roundinviti=0, "+
        " TPRICHIAMO=NULL, DTRICHIAMO=NULL, IDCENTRORICHIAMO=NULL "+
        " where CODTS = '" + codTs + 
          "' and ULSS = '" + ulss + "' and TPSCR = '" + tpscr + "'" ;
        am.getTransaction().executeCommand(updScr);
        
      }
      
      am.getTransaction().commit();
            
    }

  public static boolean invitoHaReferto(ApplicationModule am, 
                                        Integer idInvito,
                                        String tpscr)
  {
    String qry = "";
    if (tpscr.equals("CI"))
    {
      qry = "select IDREFERTO from SO_REFERTOCITO1LIV where IDINVITO = " +
      idInvito.toString() + " union all select IDREFERTO from " + 
      "SO_REFERTOCITO2LIV where IDINVITO = " + idInvito.toString();      
    }
    else if (tpscr.equals("CO"))
    {  
      qry = "select IDREFERTO from SO_REFERTOCOLON1LIV where IDINVITO = " +
      idInvito.toString() + " union all select IDREFERTO from " + 
      "SO_REFERTOCOLON2LIV where IDINVITO = " + idInvito.toString();
    }
    else if (tpscr.equals("MA"))
    {
      qry = "select IDREFERTO from SO_REFERTOMAMMO1LIV where IDINVITO = " +
      idInvito.toString() + " union all select IDREFERTO from " + 
      "SO_REFERTOMAMMO2LIV where IDINVITO = " + idInvito.toString();
    }
    else if (tpscr.equals("CA"))
    {
      qry = "select IDREFERTO from SO_REFERTOCARDIO where IDINVITO = " +
      idInvito.toString();
    }
    else if (tpscr.equals("PF"))
    {
      qry = "select IDREFERTO from SO_REFERTOPFAS where IDINVITO = " +
      idInvito.toString();
    }
    
    ViewObject vo = am.createViewObjectFromQueryStmt("",qry);
    vo.setRangeSize(-1);
    vo.executeQuery();
    
    int count = vo.getRowCount();
    
    return (count > 0);
  }

    public static String creaInvito(boolean esisteAttivo) throws Exception {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
       
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Row cAnag = voAnag.getCurrentRow();
        String codTs = (String) cAnag.getAttribute("Codts");
        
        ViewObject soScr = am.findViewObject("Sogg_SoSoggScrView1");
        Row soScrRow = soScr.getCurrentRow();

        //se il soggetto non ha un record in so_sogg_scr lo creo
        if (soScrRow == null) {
            soScrRow = soScr.createRow();
            soScr.insertRow(soScrRow);

            soScrRow.setAttribute("Codts", codTs);
            soScrRow.setAttribute("Roundindiv", new Integer(0));
            soScrRow.setAttribute("Roundinviti", new Integer(0));
            soScrRow.setAttribute("Altorischio", ViewHelper.decodeByTpscr(tpscr, null, ConfigurationConstants.DB_FALSE, //altorischio e' valorizzato solo per il colon
                                                                          null, null, null));
            soScrRow.setAttribute("VaccinatoHpv", new Integer(0));
            soScrRow.setAttribute("Consenso", new Integer(0));
            soScrRow.setAttribute("ConsensoCond", new Integer(0));
            soScrRow.setAttribute("Tpscr", tpscr);
            soScrRow.setAttribute("Ulss", ulss);

            soScr.setCurrentRow(soScrRow);
            //am.getTransaction().postChanges();
        }
        
        Boolean otpMode = (Boolean) sess.get("otp");

        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");

        // mauro 01/02/2010, modifica per evitare sovrapp. inviti
        voInvito.setWhereClause("1=2");
        voInvito.executeQuery();
        // mauro 01/02/2010, fine

        Row nInv = voInvito.createRow();

        Integer codInv = am.getNextIdInvito();

        nInv.setAttribute("Idinvito", codInv);
        nInv.setAttribute("Codts", codTs);
        nInv.setAttribute("Tpscr", tpscr);
        nInv.setAttribute("Ulss", ulss);
        nInv.setAttribute("DaConfermare", 0);
        nInv.setAttribute("Confermato", 0);
                
        if (otpMode != null && otpMode.booleanValue()) {
            String token = getToken(codInv.longValue());
            nInv.setAttribute("Otp", token);
        }

        // sara 24/05/2011
        Boolean centri = (Boolean) sess.get("centri_rich_cnf");

        //azienda con centri per soggetto
        if (centri.booleanValue()) {
            Integer idcentro1 = null;
            if (soScr.getRowCount() == 1) {
                idcentro1 = (Integer) soScrRow.getAttribute("Idcentro1liv");
                nInv.setAttribute("Idcentroprelievo", idcentro1);
            }
        } else {
            Integer centroRich = null;
            if (soScr.getRowCount() == 1) {
                centroRich = (Integer) soScrRow.getAttribute("Idcentrorichiamo");
                Date dataChiusuraCentro = null;

                //controllo se il centro non � stato chiuso
                if (centroRich != null) {
                    ViewObject voCt = am.findViewObject("Sogg_CprelInvitoView1");
                    String ww = "IDCENTRO = " + centroRich;
                    voCt.setWhereClause(ww);
                    voCt.executeQuery();
                    Row ct = voCt.first();
                    dataChiusuraCentro = (Date) ct.getAttribute("Dtchiusuracentro");
                }

                if (centroRich != null && dataChiusuraCentro == null) {
                    nInv.setAttribute("Idcentroprelievo", centroRich);
                    nInv.setAttribute("Idtpinvito", (String) soScrRow.getAttribute("Tprichiamo"));
                    Number livRichiamo = (Number) soScrRow.getAttribute("LivelloRichiamo");
                    nInv.setAttribute("Livello", livRichiamo.intValue());
                } else {
                    HashMap result = am.callGetCentroRichiamo(codTs, ulss, tpscr, null, new BigDecimal(1));
                    if (result.get("error") != null)
                        nInv.setAttribute("Idcentroprelievo", null);
                    else
                        nInv.setAttribute("Idcentroprelievo",
                                          new Integer(((BigDecimal) result.get("centro")).intValue()));
                }
            } else {
                HashMap result = am.callGetCentroRichiamo(codTs, ulss, tpscr, null, new BigDecimal(1));
                if (result.get("error") != null)
                    nInv.setAttribute("Idcentroprelievo", null);
                else
                    nInv.setAttribute("Idcentroprelievo", new Integer(((BigDecimal) result.get("centro")).intValue()));
            }
        }
        //fine sara 24/05/2011

        String idtpinvito = (String) nInv.getAttribute("Idtpinvito");
        if (idtpinvito != null) {
            // Recupero la configurazione del tipo invito
            ViewObject tipoInvitoView = am.findViewObject("A_SoCnfTpinvitoView1");
            tipoInvitoView.setWhereClause("idtpinvito = '" + idtpinvito + "' AND ulss = '" + ulss + "' AND tpscr = '" +
                                          tpscr + "'");
            tipoInvitoView.executeQuery();
            Row tipoInvitoRow = tipoInvitoView.first();

            // Recupero l'azienda dell'invito
            ViewObject aziendaView = am.findViewObject("A_SoAziendaView1");
            Row aziendaRow = aziendaView.getCurrentRow();
            if (aziendaRow == null) {
                aziendaView.setWhereClause("CODAZ = '" + ulss + "'");
                aziendaView.executeQuery();
                aziendaRow = aziendaView.first();
            }

            int etaMin =
                aziendaRow.getAttribute("Etahpvmin") != null ?
                ((Integer) aziendaRow.getAttribute("Etahpvmin")).intValue() : 0;
            int etaMax =
                aziendaRow.getAttribute("Etahpvmax") != null ?
                ((Integer) aziendaRow.getAttribute("Etahpvmax")).intValue() : 999;
            int livello = 0;
            int previstoHPV = -1;
            int roundIndivHPV = 0;
            java.util.Date dataNascita = ((Date) cAnag.getAttribute("DataNascita")).dateValue();

            if (tipoInvitoRow != null) {
                livello = ((Integer) tipoInvitoRow.getAttribute("Livello")).intValue();
                previstoHPV = ((Integer) tipoInvitoRow.getAttribute("Hpv")).intValue();
            }

            if (soScrRow != null) {
                roundIndivHPV =
                    soScrRow.getAttribute("RoundindivHpv") != null ?
                    ((Integer) soScrRow.getAttribute("RoundindivHpv")).intValue() : 0;
            }

            // Valorizzo il test proposto
            boolean gestioneHPV = Boolean.TRUE.equals(sess.get("HPV"));

            String testProposto =
                calcolaTestProposto(gestioneHPV, livello, previstoHPV, roundIndivHPV, dataNascita, etaMin, etaMax);
            nInv.setAttribute("TestProposto", testProposto);
        }
        voInvito.insertRow(nInv);

        // mauro 01/02/2010, modifica per evitare sovrapp. inviti
        voInvito.setCurrentRow(nInv);
        // mauro 01/02/2010, fine

        sess.put("invitoAppeso", Boolean.valueOf(true));
        sess.put("invitoSalvato", Boolean.valueOf(false));

        // filtro tipi invito
        ViewObject voTI = am.findViewObject("Sogg_TipoInvitoView1");
        String whcl;
        if (esisteAttivo) {
            whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
        } else {
            /* mauro 30-11-06: modifiche per includere anche accesso spontaneo */
            Integer codCatI = ConfigurationConstants.CODICE_CAT_PRIMO_INVITO;
            Integer codCatV = ConfigurationConstants.CODICE_CAT_VOLONTARIO;
            whcl =
                "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" + " and IDCATEG in (" + codCatI.toString() + "," +
                codCatV.toString() + ")";
        }

        // Filtro per data fine validita
        String strOggi = new java.sql.Date(new java.util.Date().getTime()).toString();
        whcl += " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" + strOggi + "', 'YYYY-MM-DD'))";

        voTI.setWhereClause(whcl);
        voTI.executeQuery();

        // filtro centro prelievo
        Date dtCorr = DateUtils.getOracleDateNow();
        String dtCor = DateUtils.dateToString(dtCorr.dateValue());
        ViewObject voCt = am.findViewObject("Sogg_CprelInvitoView1");
        /*
		voCt.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" +
		" and (DTCHIUSURACENTRO is null or " +
		" DTCHIUSURACENTRO >= to_date('" + dtCor + "','dd/mm/yyyy'))");
		*/
        String ww =
            "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" + " and (DTCHIUSURACENTRO is null or " +
            " DTCHIUSURACENTRO >= to_date('" + dtCor + "','dd/mm/yyyy'))";

        //se sono un utente legato ad un centro la craezione del nuovo invito pu�
        //avvenire solo sui miei centri
        String in = (String) sess.get("elenco_centri");
        if (in != null)
            ww += " and IDCENTRO in " + in;

        try {
            AccessManager.checkPermission("SOLimiteCentri");
            List<Integer> centriAutorizzati = (List<Integer>) sess.get("centriautorizzati");
            String inCentri = "";
            if (centriAutorizzati != null && !centriAutorizzati.isEmpty()) {
                for (int i = 0; i < centriAutorizzati.size(); i++) {
                    inCentri += "" + centriAutorizzati.get(i) + ",";
                }
                inCentri = inCentri.substring(0, inCentri.length() - 1);
                ww += " AND IDCENTRO in (" + inCentri + ") ";
            } else {
                ww += " AND 1=2 ";
            }
        } catch (IllegalAccessException e) {
            // non faccio niente
        }

        voCt.setWhereClause(ww);
        voCt.executeQuery();

        sess.put("invCreato", Boolean.valueOf(true));
        sess.put("sceltaOrario", Boolean.valueOf(true));
        SoggUtils.filtraOrari(true);

        Sogg_NuovoParam bean =
            (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
        bean.setRegLett(0);
        bean.setStpLett(0);
        sess.put("disStp", Boolean.valueOf(true));
        String msg = null;
        boolean ignoraRound = Boolean.TRUE.equals(sess.get("ignora_round"));
        if (!ignoraRound) {
            //controllo se il soggetto ha gi� un primo invito o accesso spontaneo in quel round
            GeneratoreInviti gi = new GeneratoreInviti(am);

            //eseguo sempre il controllo come se si trattasse di un primo invito,
            //tanto � questa l'informazione che mi interessa
            Integer round =
                gi.getRoundComune(ulss, tpscr, gi.getComuneId(codTs, ulss), ConfigurationConstants.CODICE_PRIMO_INVITO,
                                  null);

            //cerco eventauli inviti di categoria primo invito o voltario e con lo stesso
            //round comune
            String query =
                "SELECT * FROM SO_INVITO WHERE CODTS='" + codTs + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr +
                "' AND ROUNDCOMUNE=" + round + " AND IDTPINVITO IN (" +
                "SELECT IDTPINVITO FROM SO_CNF_TPINVITO WHERE ULSS='" + ulss + "' AND TPSCR='" + tpscr +
                "' AND IDCATEG IN (" + ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.intValue() + "," +
                ConfigurationConstants.CODICE_CAT_VOLONTARIO.intValue() + "))";

            ViewObject search = am.createViewObjectFromQueryStmt("AlreadyInvited", query);
            try {
                search.executeQuery();

                if (search.first() != null)
                    msg =
                        "Il soggetto risulta gi� invitato in questo round.\n" +
                        "Si sconsiglia l'inserimento di un nuovo primo invito o accesso spontaneo";
                else
                    msg = null;
            } finally {
                search.remove();
            }
        }

        Boolean otp = (Boolean) sess.get("otp");
        if (otp.booleanValue()) {
            String cf = (String) cAnag.getAttribute("Cf");
            if (cf == null || cf.length() == 0) {
                if (msg != null) {
                    msg += "\n" + "Il codice fiscale del soggetto non � valorizzato";
                } else {
                    msg = "Il codice fiscale del soggetto non � valorizzato ";
                }
            }
        }
        return msg;
    }

  public static Integer getInvitoAttivo(Sogg_AppModule am, String codTs, 
    Integer idInvCorr, String ulss, String tpscr)
  {
    String qry = "select IDINVITO from SO_INVITO where idinvito <> " + 
      idInvCorr.toString() + " and CODTS = '" + codTs + "' and attivo = 1" +
      " and ulss = '" + ulss + "' and tpscr = '" + tpscr + "'";
    ViewObject voInviti = am.createViewObjectFromQueryStmt("",qry);
    voInviti.setRangeSize(-1);
    voInviti.executeQuery();
    
    Integer invAtt;
    int count = voInviti.getRowCount();
    if (count > 0)
    {
      Row fInv = voInviti.first();
      invAtt = fInv.getAttribute(0)!=null ? ((oracle.jbo.domain.Number) fInv.getAttribute(0)).intValue() : null;
      
    }
    else
    {
      invAtt = null;
      
    }
    
    return invAtt;
    
  }

	public static String calcolaTestProposto(boolean gestioneHPV, int livello, int previstoHPV, int roundIndivHPV, java.util.Date dataNascita, int etaMin, int etaMax) {
		String testProposto = null;
		if (gestioneHPV) {
			if (livello != 2) {
				if (previstoHPV == 1) {
					testProposto = "HPV";
				} else if (previstoHPV == 0) {
					testProposto = "PAP";
				} else if (previstoHPV == 2) {
					if (roundIndivHPV > 0) {
						testProposto = "HPV";
					} else {
						// Calcolo l'et�
						int eta = calcolaEta(dataNascita);
						
						if (eta >= etaMin && eta <= etaMax) {
							testProposto = "HPV";
						} else {
							testProposto = "PAP";
						}
					}
				}
			}
		}
		return testProposto;
	}

	public static int calcolaEta(java.util.Date dataNascita) {	
		Calendar calNascita = Calendar.getInstance();
		calNascita.setTime(dataNascita);
		Calendar calAdesso = Calendar.getInstance();
		int eta = calAdesso.get(Calendar.YEAR) - calNascita.get(Calendar.YEAR);
		if (calNascita.get(Calendar.MONTH) > calAdesso.get(Calendar.MONTH) ||
			(calNascita.get(Calendar.MONTH) == calAdesso.get(Calendar.MONTH) &&
				calNascita.get(Calendar.DATE) > calAdesso.get(Calendar.DATE)))
		{
			eta--;
		}
		return eta;
	}
  
  /**
  * Crea un token corrispondente ad un id.
  * 
  * @param id
  * @return
  */
  public static String getToken(long id) {
    char[] chrKey = key.toCharArray();
    char[] result = new char[RESULT_LENGTH];
    int base = key.length();
  
    long value = id;
    for (int i = 0; i < RESULT_LENGTH; i++) {
     result[i] = chrKey[(int)(value % base)];
     value = value / base;
    }
    return String.valueOf(result);
   }

    public static void ripristinaInvito(ApplicationModule am, Integer idInv, String codTs, String ulss, String tpscr,
                                        String user, String notaRipristino, boolean attivo, Integer idAttivo)
    throws Exception {

        GeneratoreInviti gen = null;
        if (attivo) {
            gen = new GeneratoreInviti(am, codTs, ulss, tpscr);
            if (idAttivo != null) {
                gen.updateInvitoAttivo(idAttivo, user);
            }
        }

        // RIPRISTINO INVITO
        String ripristinaInvito =
            "INSERT INTO SO_INVITO " +
            "SELECT IDINVITO, CODTS, IDTPINVITO, CODESITOINVITO, LIVESITO, ";
        ripristinaInvito += attivo ? "1, " : "0, ";
        ripristinaInvito += "IDCENTROPRELIEVO, IDCENTRORICHIAMO, IDCENTROREF1LIV, IDCENTROREF2LIV, IDAPP, DTAPP, ORAAPP, MINAPP, " +
            "DTAPPINIZIALE, CONSENSO, DTESAMERECENTE, DTRICHIAMO, DTINVIORICHIAMO, OPMEDICO, DTINVIOSOLLECITO, " +
            "ROUNDCOMUNE, ROUNDINDIV, VOLONTARIA, DTBLOCCO, CHIUSURAROUNDINDIV, DTULTIMAMOD, DTCREAZIONE, " +
            "OPINSERIMENTO, DTMODESCLUSIONE, OPMODIFICA, FUORIFASCIA, '" + notaRipristino + "', ULSS, TPSCR, " +
            "ROUNDINVITI, TPRICHIAMO, LIVELLO, IDOSTETRICA, CODRESIDENZA, CODDOMICILIO, VIARESIDENZA, VIADOMICILIO, " +
            "STATOANAG, ZONA, DTRITCONT, DTCONSCONT, DA_CONFERMARE, NON_SPEDIRE_REFERTO, RACC_LETT_INVITO, " +
            "RACC_LETT_REFERTO, TEST_PROPOSTO, ROUNDINDIV_HPV, RELEASE_CODE_COM_RES, RELEASE_CODE_COM_DOM, OTP, " +
            "CONFERMATO, NOTE_CITTADINO, EXULSS, NOTE_ACC, CODICEREGMEDICO, CODCLASSEPOP " +
            "FROM SO_INVITO_BCK WHERE IDINVITO = " + idInv;
        am.getTransaction().executeCommand(ripristinaInvito);

        // CANCELLA BACKUP INVITO
        am.getTransaction().executeCommand("DELETE FROM SO_INVITO_BCK WHERE IDINVITO = " + idInv);

        /*
        String updEscl = "update SO_ESCLUSIONE set IDINVITO = null where IDINVITO = " + idInv.toString();
        am.getTransaction().executeCommand(updEscl);
        */

        // RIPRISTINO LETTERA
        String ripristinaLettera =
            "INSERT INTO SO_ALLEGATO " +
            "SELECT * FROM (" +
            "SELECT * FROM SO_ALLEGATO_BCK WHERE IDINVITO = " + idInv + " ORDER BY DTCREAZIONE DESC" +
            ") ALLBACK WHERE ROWNUM < 2";
        am.getTransaction().executeCommand(ripristinaLettera);

        // CANCELLA BACKUP ALLEGATO
        am.getTransaction().executeCommand(
            "DELETE FROM SO_ALLEGATO_BCK SAB WHERE SAB.IDALLEGATO = (SELECT ALLBACK.IDALLEGATO FROM (" +
            "SELECT IDALLEGATO FROM SO_ALLEGATO_BCK SAB2 WHERE SAB2.IDINVITO = " + idInv +
            " ORDER BY SAB2.DTCREAZIONE DESC" +
            ") ALLBACK WHERE ROWNUM < 2)");

        if (attivo) {

            ViewObject voInvito = am.findViewObject("A_SoInvitoView1");
            voInvito.setWhereClause("IDINVITO = " + idInv);
            voInvito.executeQuery();
            Row rInvito = voInvito.first();

            // aggiornamento del round
            gen.updateRoundIndiv(rInvito);
            gen.updateAndGetRoundInviti(rInvito);

        }

        if (tpscr.equals("CI")) {

            String qAcc1 = "SELECT IDACC1LIV FROM SO_ACC_CITO1LIV_BCK WHERE IDINVITO = " + idInv;
            ViewObject voAcc1 = am.createViewObjectFromQueryStmt("", qAcc1);
            voAcc1.setRangeSize(-1);
            voAcc1.executeQuery();
            Row acc1 = voAcc1.first();

            if (acc1 != null) {
                Integer idAcc = acc1.getAttribute(0) != null ? ((Number) acc1.getAttribute(0)).intValue() : null;

                if (idAcc != null) {
                    // RIPRISTINO ACCOGLIENZA CERVICALE
                    String bckAcc =
                        "INSERT INTO SO_ACC_CITO1LIV " +
                        "SELECT IDACC1LIV, IDMOT, DESCRIZIONE, IDTPPRELIEVO, IDINVITO, NOTEACC1LIV, TOTVETRINI, " +
                        "NUMVETRINO, NOTEPRELIEVO, ALTROMOTULTINT, TPSCR, COD_RICHIESTA, PRELIEVO_HPV " +
                        "FROM SO_ACC_CITO1LIV_BCK WHERE IDACC1LIV = " + idAcc;
                    am.getTransaction().executeCommand(bckAcc);
    
                    String delAcc = "DELETE FROM SO_ACC_CITO1LIV_BCK WHERE IDACC1LIV = " + idAcc;
                    am.getTransaction().executeCommand(delAcc);
                }
            }


            String qAcc2 = "SELECT IDACC2LIV FROM SO_ACC_CITO2LIV_BCK WHERE IDINVITO = " + idInv;
            ViewObject voAcc2 = am.createViewObjectFromQueryStmt("", qAcc2);
            voAcc2.setRangeSize(-1);
            voAcc2.executeQuery();
            Row acc2 = voAcc2.first();

            if (acc2 != null) {
                Integer idAcc = acc2.getAttribute(0) != null ? ((Number) acc2.getAttribute(0)).intValue() : null;

                if (idAcc != null) {
                    // RIPRISTINO ACCOGLIENZA 2L
                    String bckAcc =
                        "INSERT INTO SO_ACC_CITO2LIV " +
                        "SELECT IDACC2LIV, IDMOT, DESCRIZIONE, IDINVITO, NOTEACC2LIV, ALTROMOTULTINT, TPSCR,COD_RICHIESTA " +
                        "FROM SO_ACC_CITO2LIV_BCK WHERE IDACC2LIV = " + idAcc;
                    am.getTransaction().executeCommand(bckAcc);
    
                    String delAcc = "DELETE FROM SO_ACC_CITO2LIV_BCK WHERE IDACC2LIV = " + idAcc;
                    am.getTransaction().executeCommand(delAcc);
                }
            }

        } else if (tpscr.equals("CO")) {
            // RIPRISTINO COLON/COVID
            String bckAcc =
                "INSERT INTO SO_ACC_COLON1LIV " +
                "SELECT IDACCCO_1LIV, IDINVITO, DTCREAZIONE, OPCREAZIONE, DTULTMOD, OPULTMOD, COD_CAMPIONE, COD_RICHIESTA " +
                "FROM SO_ACC_COLON1LIV_BCK WHERE IDINVITO = " + idInv;
            am.getTransaction().executeCommand(bckAcc);

            String delAcc1Co = "DELETE FROM SO_ACC_COLON1LIV_BCK WHERE IDINVITO = " + idInv;
            am.getTransaction().executeCommand(delAcc1Co);

            String qAcc2Co = "SELECT IDACCCO_2LIV FROM SO_ACC_COLON2LIV_BCK WHERE IDINVITO = " + idInv;
            ViewObject voAcc2Co = am.createViewObjectFromQueryStmt("", qAcc2Co);
            voAcc2Co.setRangeSize(-1);
            voAcc2Co.executeQuery();
            Row acc2Co = voAcc2Co.first();

            if (acc2Co != null) {
                Integer idAcc2Co = acc2Co.getAttribute(0) != null ? ((Number) acc2Co.getAttribute(0)).intValue() : null;

                if (idAcc2Co != null) {
                    // RIPRISTINO ACCOGLIENZA 2LIV
                    String bckAcc2 =
                        "INSERT INTO SO_ACC_COLON2LIV " +
                        "SELECT IDACCCO_2LIV, IDINVITO, DTCREAZIONE, OPCREAZIONE, DTULTMOD, OPULTMOD, COD_RICHIESTA " +
                        "FROM SO_ACC_COLON2LIV_BCK WHERE IDACCCO_2LIV = " + idAcc2Co;
                    am.getTransaction().executeCommand(bckAcc2);

                    String delAcc2Co = "DELETE FROM SO_ACC_COLON2LIV_BCK WHERE IDACCCO_2LIV = " + idAcc2Co;
                    am.getTransaction().executeCommand(delAcc2Co);
                }
            }

        } else if (tpscr.equals("MA")) {
            String qAcc1Ma =
                "SELECT A.IDACCMA_1LIV FROM SO_ACC_MAMMO1LIV_BCK A WHERE A.IDINVITO = " + idInv;
            ViewObject voAcc1Ma = am.createViewObjectFromQueryStmt("", qAcc1Ma);
            voAcc1Ma.setRangeSize(-1);
            voAcc1Ma.executeQuery();
            Row acc1Ma = voAcc1Ma.first();

            if (acc1Ma != null) {
                Integer idAcc1Ma = acc1Ma.getAttribute(0) != null ? ((Number) acc1Ma.getAttribute(0)).intValue() : null;

                if (idAcc1Ma != null) {
                    // RIPRISTINO ACCOGLIENZA
                    String bckAcc =
                        "INSERT INTO SO_ACC_MAMMO1LIV " +
                        "SELECT IDACCMA_1LIV, IDINVITO, DTCREAZIONE, OPCREAZIONE, DTULTMOD, OPULTMOD, NOTE, " +
                        "COD_RICHIESTA, IDRICHIESTA, IDSTUDIO, IDPUBBLICAZIONE, TECNICO_ESECUTORE, " +
                        "CONSENSO_B, CONSENSO_C FROM SO_ACC_MAMMO1LIV_BCK WHERE IDACCMA_1LIV = " + idAcc1Ma;
                    am.getTransaction().executeCommand(bckAcc);

                    String delAcc1Ma =
                        "DELETE FROM SO_ACC_MAMMO1LIV_BCK A WHERE A.IDACCMA_1LIV = " + idAcc1Ma;
                    am.getTransaction().executeCommand(delAcc1Ma);
                }

            }
            
            String qAcc2Ma = "SELECT A.IDACCMA_2LIV FROM SO_ACC_MAMMO2LIV_BCK A WHERE A.IDINVITO = " + idInv;
            ViewObject voAcc2Ma = am.createViewObjectFromQueryStmt("", qAcc2Ma);
            voAcc2Ma.setRangeSize(-1);
            voAcc2Ma.executeQuery();
            Row acc2Ma = voAcc2Ma.first();

            if (acc2Ma != null) {
                Integer idAcc2Ma = acc2Ma.getAttribute(0) != null ? ((Number) acc2Ma.getAttribute(0)).intValue() : null;
                
                if (idAcc2Ma != null) {
                    // RIPRISTINO ACCOGLIENZA
                    String ripristinaAcc =
                        "INSERT INTO SO_ACC_MAMMO2LIV " +
                        "SELECT IDACCMA_2LIV, IDINVITO, DTCREAZIONE, OPCREAZIONE, DTULTMOD, OPULTMOD, NOTE, " +
                        "COD_RICHIESTA, CONSENSO_B, CONSENSO_C " +
                        "FROM SO_ACC_MAMMO2LIV_BCK WHERE IDACCMA_2LIV = " + idAcc2Ma;
                    am.getTransaction().executeCommand(ripristinaAcc);
                    
                    String delAcc2Ma = "DELETE FROM SO_ACC_MAMMO2LIV_BCK A WHERE A.IDACCMA_2LIV = " + idAcc2Ma;
                    am.getTransaction().executeCommand(delAcc2Ma);
                }
            }
        } else if (tpscr.equals("PF")) {

        }
 
        am.getTransaction().commit();

    }

}
