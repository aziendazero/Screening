package view.postel;

import insiel.dataHandling.DateUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.DateFormat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;

import model.common.A_SoCnfComuniZoneParamViewRow;
import model.common.Print_AppModule;

import model.common.RefMa_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.LetteraBean;
import model.commons.LetteraRefertoCABean;
import model.commons.LetteraRefertoCitoBean;
import model.commons.LetteraRefertoPFBean;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.PrintBean;

import model.inviti.LetteraInvitoBean;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Timestamp;

import org.apache.commons.beanutils.BeanUtils;

public class TXTPostelManager {
    private static final DateFormat dataOraFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private PrintBean bean = null;
    private String ulss;
    private String tpscr;
    private OutputStream os = null;
    private File filetmp = null;
    private Print_AppModule am = null;
    private String mod_spedizione;
    //ViewObject vo=null;

    /**
     * COSTRUTTORE:
     * @throws java.lang.Exception
     * @param tpscr tipo di screening
     * @param ulss azienda snaitaria
     */
    public TXTPostelManager(String ulss, String tpscr) throws Exception {
        this.ulss = ulss;
        this.tpscr = tpscr;
    }

    public Map<String, Object> getPostelTxtFile() throws Exception {
        this.bean = (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();
        this.am =
            (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        //24112011 Gaion: aggiunto parametro per il calcolo dell'indirizzo
        this.mod_spedizione = (String) ADFContext.getCurrent().getSessionScope().get("mod_spedizione");
        //24112011 fine

        String voName = (String)ADFContext.getCurrent().getRequestScope().get("vo");
        ViewObject vo = am.findViewObject(voName);
        //this.vo=am.findViewObject(voName);

        //File sc=null;
        File filetmp = null;
        if (voName.indexOf("Inviti") >= 0)
            return stampaLettere(vo, am); //stampa degli inviti
        else
            return stampaReferti(vo, am); //stampa dei referti
    }

    private void stampaRiga(Row r, ViewObject voPostel, OutputStream os, String voNameUpdate) throws Exception {
        String parametro = null;
        Row postel = null;
        String ref1l_name =
            (String) ViewHelper.decodeByTpscr(tpscr, "Print_SoRefertocito1livView2", "Print_SoRefertocolon1livView2",
                                              "Print_SoRefertomammo1livView2", "Print_SoRefertocardioView2",
                                              "Print_SoRefertopfasView2");
        String ref2l_name =
            (String) ViewHelper.decodeByTpscr(tpscr, "Print_SoRefertocito2livView2", "Print_SoRefertocolon2livView2",
                                              "Print_SoRefertomammo2livView2", "Print_SoRefertocardioView2",
                                              "Print_SoRefertopfasView2");
        String att =
            (String) ViewHelper.decodeByTpscr(tpscr, "Dtultimamodifica", "Dtultmodifica", "Dtultmodifica",
                                              "Dtultimamodifica", "Dtultmodifica");

        ViewObject ref1l = am.findViewObject(ref1l_name);
        ViewObject ref2l = am.findViewObject(ref2l_name);

        //aggiorno eventualmente data e tipo di stampa
        //prima di stampare perche' mi serve la data di stampa
        ViewObject vvo = am.findViewObject(voNameUpdate);
        Row rr = vvo.getCurrentRow();
        if (rr == null)
            rr = vvo.first();

        if (rr.getAttribute("Dtstampa") == null) {
            rr.setAttribute("Dtstampa", DateUtils.getOracleDate(new java.util.Date()));
            rr.setAttribute("Stampapostel", ConfigurationConstants.DB_TRUE);

            //vado ad aggiornare anche il referto (Se sto stampando un referto)
            if (!voNameUpdate.equals("Print_SoAllegatoInvitiView1")) {
                Row ref;
                //se e' di primo livello
                if (ref1l.getRowCount() > 0) {
                    ref = ref1l.first();
                }

                //se e' di secondo livello
                else {
                    ref = ref2l.first();
                }

                ref.setAttribute("Dtspedizione", rr.getAttribute("Dtstampa"));
                ref.setAttribute(att, DateUtils.getOracleDateNow());
                ref.setAttribute("Opultmodifica", ADFContext.getCurrent().getSessionScope().get("user"));

                //20110415 Serra per evitare di apire troppi cursori
                am.getTransaction().postChanges();
                //fine 20110415
            }
        }

        String tesseraSanitaria =
            SoggUtils.trovaTessera(am, (String) r.getAttribute("Codts"), (String) r.getAttribute("Ulss"));

        //15092011 Gaion : gestione postel txt da bean
        LetteraBean letterabean = null;
        //inizializzo il bean per gli inviti
        if (voNameUpdate.equals("Print_SoAllegatoInvitiView1")) {
            String codCamp = null;
            if (r.getAttribute("CodCampione") != null)
                codCamp = (r.getAttribute("CodCampione")).toString();
            else if (r.getAttribute("Numvetrino") != null)
                codCamp = (r.getAttribute("Numvetrino")).toString();
            else if (r.getAttribute("CodCampione1") != null) //pfas
                codCamp = (r.getAttribute("CodCampione1")).toString();

            String codRichiesta = null;
            if (r.getAttribute("CodRichiesta") != null) {
                codRichiesta = (String) r.getAttribute("CodRichiesta");
            }

            String altro = null;
            ViewObject comzonepar_vo = am.findViewObject("A_SoCnfComuniZoneParamView1");
            if (ConfigurationConstants.CODICE_DOMICILIATO.equals(r.getAttribute("Codanagreg"))) {
                comzonepar_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND RELEASE_CODE_COM='" +
                                             r.getAttribute("ReleaseCodeComDom") + "'");
            } else {
                comzonepar_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND RELEASE_CODE_COM='" +
                                             r.getAttribute("ReleaseCodeComRes") + "'");
            }
            comzonepar_vo.executeQuery();
            if (comzonepar_vo.getRowCount() > 1)
                throw new Exception("Troppi dati di configurazione associati al comune di riferimento del soggetto");
            A_SoCnfComuniZoneParamViewRow cr = (A_SoCnfComuniZoneParamViewRow) comzonepar_vo.first();
            if (cr != null)
                altro = cr.getAltro();
            else { //provo con la zona
                comzonepar_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND CODDISTRZONA=" +
                                             r.getAttribute("Coddistrzona") + "");
                comzonepar_vo.executeQuery();
                if (comzonepar_vo.getRowCount() > 1)
                    throw new Exception("Troppi dati di configurazione associati alla zona di riferimento del soggetto");
                cr = (A_SoCnfComuniZoneParamViewRow) comzonepar_vo.first();

                if (cr != null)
                    altro = cr.getAltro();
            }

            letterabean =
                new LetteraInvitoBean((String) r.getAttribute("Cognome"), (String) r.getAttribute("Nome"),
                                      this.getIndirizzo(r), (String) r.getAttribute("Cap"),
                                      (String) r.getAttribute("ComuneDesc"), (String) r.getAttribute("Codpr"),
                                      DateUtils.getToday(), (String) r.getAttribute("CentroPrel"),
                                      (String) r.getAttribute("IndirizzoCentro"),
                                      DateUtils.dateToString(((Date) r.getAttribute("Dtapp")).dateValue()),
                                      this.getOraappuntamento(r),
                                      (String) r.getAttribute("CognomeMedico") + ";" +
                                      (String) r.getAttribute("NomeMedico"), (String) r.getAttribute("CognomeMedico"),
                                      (String) r.getAttribute("NomeMedico"), null, codCamp, codRichiesta, altro,
                                      DateUtils.dateToString(((Date) r.getAttribute("DataNascita")).dateValue()), tesseraSanitaria,
                                      (String) r.getAttribute("Cf"), (String) r.getAttribute("Sesso"), getTel(r),
                                      (String) r.getAttribute("Sede"), (String) r.getAttribute("Oraritel"),
                                      (String) r.getAttribute("Tel"), getTimestamp(r, "Dtcreazione"),
                                      (String) r.getAttribute("Mx45Braccio"), (String) r.getAttribute("Mx45Codice"),
                                      (String) r.getAttribute("Email"), (String) r.getAttribute("Cellulare"),
                                      (String) r.getAttribute("Otp"));
        }

        // Inizializzo il bean per i referti
        else {
            String codCamp = null;
            String data_esame = null;
            String prelievo_hpv = null;
            String data_intervento = null;
            if (tpscr.equals("CO")) {
                if (r.getAttribute("CodCampione") != null)
                    codCamp = (r.getAttribute("CodCampione")).toString();

                //19012012 S.Gaion: valorizzo la data_esamecon la data del referto
                if (r.getAttribute("Dtreferto") != null)
                    data_esame = DateUtils.dateToString(((oracle.jbo.domain.Date) r.getAttribute("Dtreferto")).dateValue());
            } else if (tpscr.equals("CI")) {
                if (r.getAttribute("Numvetrino") != null)
                    codCamp = (r.getAttribute("Numvetrino")).toString();

                if (r.getAttribute("Dtprelievo") != null)
                    data_esame = DateUtils.dateToString(((oracle.jbo.domain.Date) r.getAttribute("Dtprelievo")).dateValue());

                //prelievo hpv
                if (r.getAttribute("PrelievoHpv") != null) {
                    Integer prel_hpv = (Integer) r.getAttribute("PrelievoHpv");
                    if (prel_hpv.intValue() == ConfigurationConstants.DB_SINOND_SI) {
                        prelievo_hpv = "Si";
                    } else if (prel_hpv.intValue() == ConfigurationConstants.DB_SINOND_NO) {
                        prelievo_hpv = "No";
                    } else if (prel_hpv.intValue() == ConfigurationConstants.DB_SINOND_ND) {
                        prelievo_hpv = "Dato non disponibile";
                    }
                }
            }
            String codRichiesta = null;

            // Il cardiovascolare non ha codice richiesta
            if (!tpscr.equals("CA")) {
                if (r.getAttribute("CodRichiesta") != null) {
                    codRichiesta = (r.getAttribute("CodRichiesta")).toString();
                }
            }

            String centro_ref = null;
            if (r.getAttribute("Idcentroref") != null) {
                ViewObject centri = am.findViewObject("Print_SoCentroPrelRefView2");
                Row[] result = centri.getFilteredRows("Idcentro", r.getAttribute("Idcentroref"));
                if (result.length > 0)
                    centro_ref = (String) result[0].getAttribute("Descrizione");
            }
            
            //data intervento 
              String livelloRef = (String)r.getAttribute("LivelloRef");
              if ("3".equals(livelloRef))
              {
                String interventiVoName = (String)ViewHelper.decodeByTpscr(tpscr, "Print_SoInterventocitoView1", "Print_SoInterventocolonView1", "Print_SoInterventomammo1",null,null);
                ViewObject interventi = am.findViewObject(interventiVoName);
                interventi.setWhereClause("IDREFERTO = "+ r.getAttribute("Idreferto"));
                interventi.executeQuery();
                Row interv = interventi.first();
                if (interv != null && interv.getAttribute("Dtintervento") != null)
                {
                  data_intervento = DateUtils.dateToString(((oracle.jbo.domain.Date)interv.getAttribute("Dtintervento")).dateValue());
                }
              }

            String supervisore = null;
            String citoscreener = null;
            String revisore = null;
            String braccio45mx = null;
            String codice45mx = null;

            if (tpscr.equals("MA")) {
                RefMa_AppModule refam =
                    (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
                ViewObject opmedici = refam.findViewObject("Ref_SoOpmedicoView2");

                //leggo il primo radiologo
                Row[] result = new Row[0];
                if (r.getAttribute("L1Radiologo") != null) {
                    result = opmedici.getFilteredRows("Idop", r.getAttribute("L1Radiologo"));
                }
                if (result.length > 0) {
                    citoscreener =
                        result[0].getAttribute("Titolo") + " " + result[0].getAttribute("Cognome") + " " +
                        result[0].getAttribute("Nome");
                }

                //secondo radiologo
                if (r.getAttribute("L2Radiologo") != null) {
                    result = opmedici.getFilteredRows("Idop", r.getAttribute("L2Radiologo"));
                } else
                    result = new Row[0];

                if (result.length > 0) {
                    supervisore =
                        result[0].getAttribute("Titolo") + " " + result[0].getAttribute("Cognome") + " " +
                        result[0].getAttribute("Nome");
                }

                if (r.getAttribute("RRadiologo") != null) {
                    result = opmedici.getFilteredRows("Idop", r.getAttribute("RRadiologo"));
                } else
                    result = new Row[0];

                if (result.length >
                    0) {
                    //revisore
                    revisore =
        result[0].getAttribute("Titolo") + " " + result[0].getAttribute("Cognome") + " " +
        result[0].getAttribute("Nome");
                }

                if (r.getAttribute("Dtmammo") != null)
                    data_esame = DateUtils.dateToString(((oracle.jbo.domain.Date) r.getAttribute("Dtmammo")).dateValue());

                braccio45mx = (String) r.getAttribute("Mx45Braccio");
                codice45mx = (String) r.getAttribute("Mx45Codice");
            }

            if (tpscr.equals("CA")) {
                letterabean = new LetteraRefertoCABean();
                letterabean.setCap((String) r.getAttribute("Cap"));
                letterabean.setCodice_fiscale((String) r.getAttribute("Cf"));
                letterabean.setCognome((String) r.getAttribute("Cognome"));
                letterabean.setComune((String) r.getAttribute("ComuneDesc"));
                letterabean.setData_nascita(DateUtils.dateToString(((oracle.jbo.domain.Date) r.getAttribute("DataNascita")).dateValue()));
                letterabean.setData_stampa(DateUtils.getToday());
                letterabean.setIndirizzo(getIndirizzo(r));
                letterabean.setNome((String) r.getAttribute("Nome"));
                letterabean.setProvincia((String) r.getAttribute("Codpr"));
                letterabean.setSesso((String) r.getAttribute("Sesso"));
                letterabean.setTessera_sanitaria(tesseraSanitaria);
                letterabean.setEmail((String) r.getAttribute("Email"));
                letterabean.setCellulare((String) r.getAttribute("Cellulare"));
                ((LetteraRefertoCABean) letterabean).setData_esame(DateUtils.dateToString(((Date) r.getAttribute("DataRilevazione")).dateValue()));
                ((LetteraRefertoCABean) letterabean).setTelefono1((String) r.getAttribute("Tel1"));
                ((LetteraRefertoCABean) letterabean).setTelefono2((String) r.getAttribute("Tel2"));
                ((LetteraRefertoCABean) letterabean).setCentro_prelievo((String) r.getAttribute("CentroPrel"));
                ((LetteraRefertoCABean) letterabean).setConsiglio((String) r.getAttribute("DescSugg"));
                ((LetteraRefertoCABean) letterabean).setNote((String) r.getAttribute("Note"));
                ((LetteraRefertoCABean) letterabean).setMedico((String) r.getAttribute("NomeMedico") + " " +
                                                               (String) r.getAttribute("CognomeMedico"));
                ((LetteraRefertoCABean) letterabean).setIndirizzo_centro((String) r.getAttribute("IndirizzoPrel"));
                ((LetteraRefertoCABean) letterabean).setSede_centro((String) r.getAttribute("SedePrel"));
                ((LetteraRefertoCABean) letterabean).setOrari_centro((String) r.getAttribute("OrariPrel"));
                ((LetteraRefertoCABean) letterabean).setTel_centro((String) r.getAttribute("TelPrel"));
                ((LetteraRefertoCABean) letterabean).setData_app(DateUtils.dateToString(((Date) r.getAttribute("Dtapp")).dateValue()));
                ((LetteraRefertoCABean) letterabean).setData_creazione(dataOraFormat.format(((Timestamp) r.getAttribute("Dtcreazione")).timestampValue()));
                ((LetteraRefertoCABean) letterabean).setZona((String) r.getAttribute("Zona"));
                String medicoRilevatore =
                    (String) r.getAttribute("TitoloRilevatore") + " " + r.getAttribute("NomeRilevatore") + " " +
                    r.getAttribute("CognomeRilevatore");
                ((LetteraRefertoCABean) letterabean).setSupervisore(medicoRilevatore);

                // Leggo i campi dinamici del questionario
                ViewObject datiQuestionarioView = am.findViewObject("Print_DatiQuestCAView1");
                datiQuestionarioView.setWhereClause("idcnfquest = :1");
                datiQuestionarioView.setWhereClauseParam(0, r.getAttribute("Idcnfquest"));
                datiQuestionarioView.executeQuery();
                while (datiQuestionarioView.hasNext()) {
                    Row datiQuestionarioRow = datiQuestionarioView.next();
                    String codiceCampo = (String) datiQuestionarioRow.getAttribute("CodiceDom");
                    String valoreCampo = (String) datiQuestionarioRow.getAttribute("Valore");
                    if ("SYS_USED".equals(codiceCampo)) {
                        ((LetteraRefertoCABean) letterabean).setSys(valoreCampo);
                    } else if ("DIA_USED".equals(codiceCampo)) {
                        ((LetteraRefertoCABean) letterabean).setDia(valoreCampo);
                    } else if ("GLICEM_NUM".equals(codiceCampo)) {
                        ((LetteraRefertoCABean) letterabean).setGlicemia(valoreCampo);
                    } else if ("COLESTEROL".equals(codiceCampo)) {
                        ((LetteraRefertoCABean) letterabean).setColesterolo_tot(valoreCampo);
                    } else if ("COL_HDL".equals(codiceCampo)) {
                        ((LetteraRefertoCABean) letterabean).setColesterolo_hdl(valoreCampo);
                    } else if ("CIRCONF".equals(codiceCampo)) {
                        ((LetteraRefertoCABean) letterabean).setCirconf_vita(valoreCampo);
                    } else if ("BMI".equals(codiceCampo)) {
                        ((LetteraRefertoCABean) letterabean).setBmi(valoreCampo);
                    } else if ("AF_TOT".equals(codiceCampo)) {
                        ((LetteraRefertoCABean) letterabean).setAttivita_fisica(valoreCampo);
                    }
                }
            } else if (tpscr.equals("PF")) {
                letterabean = new LetteraRefertoPFBean();
                letterabean.setCap((String) r.getAttribute("Cap"));
                letterabean.setCodice_fiscale((String) r.getAttribute("Cf"));
                letterabean.setCognome((String) r.getAttribute("Cognome"));
                letterabean.setComune((String) r.getAttribute("ComuneDesc"));
                letterabean.setData_nascita(DateUtils.dateToString(((Date) r.getAttribute("DataNascita")).dateValue()));
                letterabean.setData_stampa(DateUtils.getToday());
                letterabean.setIndirizzo(getIndirizzo(r));
                letterabean.setNome((String) r.getAttribute("Nome"));
                letterabean.setProvincia((String) r.getAttribute("Codpr"));
                letterabean.setSesso((String) r.getAttribute("Sesso"));
                letterabean.setTessera_sanitaria(tesseraSanitaria);
                letterabean.setEmail((String) r.getAttribute("Email"));
                letterabean.setCellulare((String) r.getAttribute("Cellulare"));
                ((LetteraRefertoPFBean) letterabean).setData_app(DateUtils.dateToString(((Date) r.getAttribute("Dtapp")).dateValue()));
                ((LetteraRefertoPFBean) letterabean).setData_esame(DateUtils.dateToString(((Date) r.getAttribute("DataRilevazione")).dateValue()));
                ((LetteraRefertoPFBean) letterabean).setOrari_centro((String) r.getAttribute("OrariPrel"));
                ((LetteraRefertoPFBean) letterabean).setSede_centro((String) r.getAttribute("SedePrel"));
                ((LetteraRefertoPFBean) letterabean).setConsiglio((String) r.getAttribute("DescSugg"));
                ((LetteraRefertoPFBean) letterabean).setMedico((String) r.getAttribute("NomeMedico") + " " +
                                                               (String) r.getAttribute("CognomeMedico"));
                ((LetteraRefertoPFBean) letterabean).setStile_di_vita(((Integer) r.getAttribute("StileVita")).toString());
                ((LetteraRefertoPFBean) letterabean).setAlimentazione(((Integer) r.getAttribute("Alimentazione")).toString());
                ((LetteraRefertoPFBean) letterabean).setBmi_eval(((Integer) r.getAttribute("Bmi")).toString());

                // Leggo i campi dinamici del questionario
                ViewObject datiQuestionarioView = am.findViewObject("Print_DatiQuestPFView1");
                datiQuestionarioView.setWhereClause("idcnfquest = " + r.getAttribute("Idcnfquest"));
                datiQuestionarioView.executeQuery();
                while (datiQuestionarioView.hasNext()) {
                    Row datiQuestionarioRow = datiQuestionarioView.next();
                    String codiceCampo = (String) datiQuestionarioRow.getAttribute("CodiceDom");
                    String valoreCampo = (String) datiQuestionarioRow.getAttribute("Valore");
                    if ("SYS_USED".equals(codiceCampo)) {
                        ((LetteraRefertoPFBean) letterabean).setSys(valoreCampo);
                    } else if ("DIA_USED".equals(codiceCampo)) {
                        ((LetteraRefertoPFBean) letterabean).setDia(valoreCampo);
                    } else if ("BMI".equals(codiceCampo)) {
                        ((LetteraRefertoPFBean) letterabean).setBmi(valoreCampo);
                    } else if ("AF_TOT".equals(codiceCampo)) {
                        ((LetteraRefertoPFBean) letterabean).setAttivita_fisica(valoreCampo);
                    } else if ("FUMO".equals(codiceCampo)) {
                        ((LetteraRefertoPFBean) letterabean).setFumo(valoreCampo);
                    }
                }

                // LEggo i dati dinamici degli esami
                ViewObject datiEsamiView = am.findViewObject("Print_EsamiPfasView1");
                datiEsamiView.setWhereClause("ULSS = '" + r.getAttribute("Ulss") + "' AND IDREFERTO = " +
                                             r.getAttribute("Idreferto"));
                datiEsamiView.executeQuery();
                Row[] esami = datiEsamiView.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_ESAMI);
                for (int i = 0; i < esami.length; i++) {
                    Row esame = esami[i];
                    Integer id = (Integer) esame.getAttribute("Idcnfref");
                    switch (id.intValue()) {
                    case 1:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_1(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 2:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_2(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 3:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_3(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 4:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_4(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 5:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_5(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 6:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_6(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 7:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_7(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 8:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_8(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 9:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_9(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 10:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_10(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 11:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_11(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 12:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_12(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 13:
                        ((LetteraRefertoPFBean) letterabean).setEsa1li_13(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    }
                }

                Row[] esamiPfas =
                    datiEsamiView.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS);
                for (int i = 0; i < esamiPfas.length; i++) {
                    Row esame = esamiPfas[i];
                    Integer id = (Integer) esame.getAttribute("Idcnfref");
                    switch (id.intValue()) {
                    case 1:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_1(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 2:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_2(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 3:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_3(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 4:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_4(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 5:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_5(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 6:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_6(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 7:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_7(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 8:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_8(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 9:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_9(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 10:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_10(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 11:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_11(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    case 12:
                        ((LetteraRefertoPFBean) letterabean).setPfa1li_12(((Integer) esame.getAttribute("Valore")).toString());
                        break;
                    }
                }

            } else {
                letterabean =
                    new LetteraRefertoCitoBean((String) r.getAttribute("Cognome"), (String) r.getAttribute("Nome"),
                                               this.getIndirizzo(r), (String) r.getAttribute("Cap"),
                                               (String) r.getAttribute("ComuneDesc"), (String) r.getAttribute("Codpr"),
                                               DateUtils.getToday(), tesseraSanitaria, (String) r.getAttribute("Cf"),
                                               (String) r.getAttribute("Sesso"),
                                               DateUtils.dateToString(((Date) r.getAttribute("DataNascita")).dateValue()), null,
                                               getTel(r), null, data_esame, (String) r.getAttribute("CentroPrel"),
                                               centro_ref, null, null, supervisore, citoscreener, revisore,
                                               (String) r.getAttribute("DescSugg"), null,
                                               (String) r.getAttribute("Note"), null, null,
                                               (String) r.getAttribute("NomeMedico") + " " +
                                               (String) r.getAttribute("CognomeMedico"),
                                               (String) r.getAttribute("IndirizzoPrel"),
                                               (String) r.getAttribute("SedePrel"),
                                               (String) r.getAttribute("OrariPrel"), (String) r.getAttribute("TelPrel"),
                                               DateUtils.dateToString(((Date) r.getAttribute("Dtapp")).dateValue()), codRichiesta,
                                               codCamp, getTimestamp(r, "Dtcreazione"), braccio45mx, codice45mx,
                                               (String) r.getAttribute("Email"), (String) r.getAttribute("Cellulare"),
                                               (String) r.getAttribute("TestProposto"), prelievo_hpv, data_intervento);
            }
        }

        //itero sulla tabella dei parametri e in base a cio' che voglio
        //stampo sul file finale chiamando le funzioni apposite che trattano
        //i dati nel modo corretto in base se sono date, timestamp, ecc.
        voPostel.first();
        voPostel.previous();

        String s;
        while (voPostel.hasNext()) {
            postel = voPostel.next();
            s = null;
            parametro = (String) postel.getAttribute("Nomeparametro");

            if ("Stringa".equals(parametro))
                s = getStringa(postel);
            else if ("Ulss".equals(parametro))
                s = ulss;
            else if ("Tpscr".equals(parametro))
                s = tpscr;
            else
                s = BeanUtils.getProperty(letterabean, parametro);

            if (s != null) {
                //Verifico se devo aggiungere un prefisso o un suffisso
                parametro = (String) postel.getAttribute("Prefisso");
                if (parametro != null)
                    s = parametro + s;

                parametro = (String) postel.getAttribute("Suffisso");
                if (parametro != null)
                    s = s + parametro;

                //stampo
                os.write(s.getBytes());
            }

            //se c'e' un altro campo da stampare dopo allora stampo il ";" altrimenti
            //non serve perche' sono a fine riga
            if (voPostel.hasNext())
                os.write(";".getBytes());
            else
                os.write("\r\n".getBytes()); //altrimenti vado a capo
        }
    }

    /**
     * Ogni riga deve essere così composta:
     * primi 7 campi con i dati anagrafici separati da ";"
     * e poi posso aggiungere i dati che servono
     * come ora appuntamento, luogo, ecc
     *
     * esempio di anagrafica:
     * Galeotti Domenico;;Via Tirreno 50;15100;Alessandria;AL;;
     */
    public Map<String, Object> stampaLettere(ViewObject vo, Print_AppModule am) throws Exception {
        return this.stampa(vo, am, true);
    }

    public Map<String, Object> stampaReferti(ViewObject vo, Print_AppModule am) throws Exception {
        return this.stampa(vo, am, false);
    }

    private  Map<String, Object> stampa(ViewObject vo, Print_AppModule am, boolean inviti) throws Exception {
        Map<String, Object> _ret = new HashMap<String, Object>();
        
        ViewObject voPostel = am.findViewObject("Print_SoPosteltxtView1");
        //ViewObject voPostel = am.findViewObject("Print_SoAllegatoInvitiView1");
        RowSetIterator iter = null;
        int invito = inviti ? 1 : 0;
        String nomefile = inviti ? "InvitiPostel.txt" : "RefertiPostel.txt";
        String voNameUpdate =
            inviti ? "Print_SoAllegatoInvitiView1" :
            (String) ViewHelper.decodeByTpscr(tpscr, "Print_SoAllegatoView2", "Print_SoAllegatoView3",
                                              "Print_SoAllegatoView4", "Print_SoAllegatoView5",
                                              "Print_SoAllegatoView6");

        String where = null;
        try {
            //in base se voglio solo lettere o lettere+etichette imposto la query adatta
            if ("lettere".equals(bean.getCosaStampare()))
                where =
                    "Print_SoPosteltxt.ULSS = '" + ulss + "' AND Print_SoPosteltxt.TPSCR = '" + tpscr +
                    "' AND Print_SoPosteltxt.INVITO = " + invito + " AND Print_SoPosteltxt.ETICHETTA = 0";
            else if ("lettere + etichette".equals(bean.getCosaStampare()))
                where =
                    "Print_SoPosteltxt.ULSS = '" + ulss + "' AND Print_SoPosteltxt.TPSCR = '" + tpscr +
                    "' AND Print_SoPosteltxt.INVITO = " + invito;

            voPostel.setWhereClause(where);
            voPostel.executeQuery();

            if (voPostel.first() == null)
                throw new Exception("Impossibile generare il file per Postel, parametri PostelTxt non inseriti.");

            try {
                filetmp = File.createTempFile("TXT", ".txt");
                os = new ByteArrayOutputStream();
                this.outputPrintServlet(os, filetmp);
                
            } catch (IOException e) {
            }

            // Formato necessario per la stampa dei minuti, altrimenti non ho lo zero davanti
            // DecimalFormat df =new DecimalFormat ("00");
            // int temp=0;//var temporanea necessaria nella stampa dei minuti nel file

            //se ho selezionato solo un invito da stampare:
            if ("selection".equals(ADFContext.getCurrent().getRequestScope().get("quanti"))) {
                //stampo la singola riga
                Row r = vo.getCurrentRow();
                stampaRiga(r, voPostel, os, voNameUpdate);
            }

            //piu' lettere da stampare:
            else {
                iter = ViewHelper.getRowSetIterator(vo, "stampa");
                while (iter.hasNext()) //ciclo su ogni riga e la aggiungo allo stream
                {
                    Row r = iter.next();
                    vo.setCurrentRow(r);

                    stampaRiga(r, voPostel, os, voNameUpdate);

                    //devo resettare la riga di Postel altrimenti mi stampa solo la prima
                    voPostel.reset();
                }
            }

            this.am.getTransaction().commit();

            //sepdisco il file
            _ret.put("fileName", nomefile);
            _ret.put("contentType", "text/plain; charset=utf-8");
            _ret.put("outputStream", os);

            //try {
               // ServletUtils.sendFile(ctx.getHttpServletResponse(), filetmp, nomefile, "text", true);
            //} catch (IOException e) {
            //}

            //e chiudo lo stream
            try {
                os.close();
            } catch (IOException e) {
            }
        } finally {
            //chiudo l'iteratore
            if (iter != null)
                iter.closeRowSetIterator();

            //elimino il file temporaneo
            if (filetmp != null)
                filetmp.delete();
        }
        
        return _ret;
    }

    protected void outputPrintServlet(OutputStream output, File f) throws IOException {
        //17102013 S.Gaion : tolto spoolx per incompatibilita con il CAS
        // lo zip viene scaricato direttamente
        InputStream input = new FileInputStream(f);

        try {
            while (input.available() > 0) {
                output.write(input.read());
            }

        } finally {
            input.close();
            output.flush();
            output.close();
            if (f != null)
                f.delete();
        }
    }

    private String getIndirizzo(Row r) {
        String s = null;

        //Nome e Cognome
        s = r.getAttribute("Cognome") + " ";
        s += r.getAttribute("Nome") + ";";
        //c'e' un indirizzo di screening
        if ((String) r.getAttribute("IndirizzoScr") != null && ((String) r.getAttribute("IndirizzoScr")).length() > 0) {
            s += r.getAttribute("IndirizzoScr") + ";";
            s += r.getAttribute("Cap") + ";";
            s += r.getAttribute("ComuneDesc") + ";";
            s += "(" + (String) r.getAttribute("Codpr") + ");";
        }
        //c'e' un indirizzo di domicilio
        //24112011 GAIOn: aggiunto parametro per il calcolo dell'indirizzo
        else if (this.mod_spedizione.equalsIgnoreCase("standard") && (String) r.getAttribute("IndirizzoDom") != null &&
                 ((String) r.getAttribute("IndirizzoDom")).length() > 0) {
            s += r.getAttribute("IndirizzoDom") + ";";
            s += r.getAttribute("Cap1") + ";";
            s += r.getAttribute("Descrizione1") + ";";
            s += "(" + (String) r.getAttribute("Codpr1") + ");";
        }
        //c'e' un indirizzo di residenza
        else if ((String) r.getAttribute("IndirizzoRes") != null &&
                 ((String) r.getAttribute("IndirizzoRes")).length() > 0) {
            s += r.getAttribute("IndirizzoRes") + ";";
            s += r.getAttribute("Cap2") + ";";
            s += r.getAttribute("Descrizione2") + ";";
            s += "(" + (String) r.getAttribute("Codpr2") + ");";
        }

        return s;

    }

    private String getParametro(Row r, String param) {
        Object o = r.getAttribute(param);
        if (o != null)
            return o.toString();
        else
            return null;

    }


    private String getTimestamp(Row r, String parametro) {
        if (r.getAttribute(parametro) != null)
            return ((DateUtils.dateToString(DateUtils.DATE_PATTERN,
                                            ((oracle.jbo.domain.Timestamp) r.getAttribute(parametro)))));
        else
            return null;
    }

    private String getStringa(Row postel) {
        return (String) postel.getAttribute("Valore");

    }


    private String getOraappuntamento(Row r) {
        //necessario per avere i minuti con lo zero davanti
        DecimalFormat df = new DecimalFormat("00");
        String s = null;
        if (r.getAttribute("Oraapp") != null && r.getAttribute("Minapp") != null) {
            s = r.getAttribute("Oraapp") + ".";
            int temp = (Integer)(r.getAttribute("Minapp"));
            s += df.format(temp);
        }

        return s;
    }


    private String getData(Row r, String parametro) {

        if (r.getAttribute(parametro) != null)
            return (DateUtils.dateToString(((Date) r.getAttribute(parametro)).dateValue()));
        else
            return null;
    }


    private String getTel(Row r) {

        String tel = null;
        if (r.getAttribute("Tel1") == null)
            tel = (String) r.getAttribute("Tel2");
        else if (r.getAttribute("Tel2") == null)
            tel = (String) r.getAttribute("Tel1");
        else
            tel = (String) r.getAttribute("Tel1") + " " + (String) r.getAttribute("Tel2");

        return tel;
    }


    /* private String getParametroNumber(Row r,String param){
    oracle.jbo.domain.Number nv=null;
    nv=((oracle.jbo.domain.Number)r.getAttribute(param));
    if (nv!=null)
    return nv.toString();
    else
    return null;
    }*/


}//chiusura classe
    
    