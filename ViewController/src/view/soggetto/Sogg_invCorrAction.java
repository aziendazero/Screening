package view.soggetto;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.accettazione.A_SoAccCito1livView1RowImpl;

import model.common.ImpExp_AppModule;
import model.common.Sogg_AppModule;

import model.commons.AgendaUtils;
import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.ImpexpBean;
import model.datacontrol.Sogg_NuovoParam;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import model.inviti.GeneratoreInviti;
import model.inviti.InvitoUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import org.apache.myfaces.trinidad.context.RequestContext;

import view.commons.action.Parent_DataForwardAction;

import view.impexp.Impexp_dataForwardAction;

import view.referto.GestoreReferti;

import view.util.Utility;

@SuppressWarnings({ "deprecation", "unchecked" })
public class Sogg_invCorrAction extends Parent_DataForwardAction {
    private RichPopup classePopWarnPopup;
    
    public Sogg_invCorrAction() {
    }

    // To handle an event named "yourname" add a method:
    // public void onYourname()
    public String onPgl() {
        ApplicationModule am =
            (ApplicationModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        Row cInv = voInvito.getCurrentRow();
        Integer idCt = (Integer) cInv.getAttribute("Idcentroprelievo");
        Date dtApp = (Date) cInv.getAttribute("Dtapp");
        Map sess = ADFContext.getCurrent().getSessionScope();
        String scr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");

        if (idCt == null) {
            String msg = "Per poter calcolare il primo giorno libero e' necessario " + "impostare il centro.";
            this.handleException(msg, null);
            return null;
        }

        if (dtApp == null) {
            dtApp = DateUtils.getOracleDateNow();
        }

        Date dtMin = AgendaUtils.primoGLib(am, idCt, dtApp, scr, ulss);

        if (dtMin == null) {
            String msg = "Non sono stati trovati giorni liberi per i parametri impostati.";
            this.handleException(msg, null);
            return null;
        }

        cInv.setAttribute("Dtapp", dtMin);
        SoggUtils.filtraOrari(false);
        Integer livello = (Integer) cInv.getAttribute("Livello");
        SoggUtils.filtraEsiti(dtMin, livello.intValue());
        
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:Idapp'])");
        
        return null;
    }


    public String onCanc() {
        
        //prima salvo
        // String saveOK = this.onAppl();
        
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        Row cInv = voInvito.getCurrentRow();
        Integer idInv = (Integer) cInv.getAttribute("Idinvito");
        String codTs = (String) cInv.getAttribute("Codts");
        String ulss = (String) cInv.getAttribute("Ulss");
        String tpscr = (String) cInv.getAttribute("Tpscr");
        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String) session.get("user");
        try {
            //prima di cancellare salvo eventuali modifiche all'invito
            am.getTransaction().commit();
            
            InvitoUtils.cancellaInvito(am, idInv.intValue(), codTs, ulss, tpscr, user);
        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = ex.getMessage();
            this.handleException(msg, null);
            this.doRollback();
            return null;
        }
        return "goRic";
    }


    public void onAggOrari(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        SoggUtils.filtraOrari(false);

        // Filtro i tipi invito in base alla data appuntamento
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        Map sess = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");

        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        Row cRow = voInvito.getCurrentRow();
        Date dtApp = (Date) cRow.getAttribute("Dtapp");

        ViewObject voTipoInvito = am.findViewObject("Sogg_TipoInvitoView1");
        if (dtApp != null) {
            voTipoInvito.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss +
                                        "' AND DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                                        dtApp.dateValue().toString() + "', 'YYYY-MM-DD')");
        } else {
            voTipoInvito.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss +
                                        "' AND DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TRUNC(SYSDATE)");
        }
        voTipoInvito.executeQuery();

        // filtro esiti invito
        Integer livello = (Integer) cRow.getAttribute("Livello");
        Date dataApp = (Date) cRow.getAttribute("Dtapp");
        SoggUtils.filtraEsiti(dataApp, livello.intValue());
        
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:Idapp'])");
    }

    protected void setAppModule() {
        this.amName = "Sogg_AppModule";
    }

    public void onChTipo(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject invitoView = am.findViewObject("Sogg_SoInvitoView1");
        Row invitoRow = invitoView.getCurrentRow();
        Integer livello = (Integer) invitoRow.getAttribute("Livello");
        String ulss = (String) invitoRow.getAttribute("Ulss");
        String tpscr = (String) invitoRow.getAttribute("Tpscr");
        Date dataApp = (Date) invitoRow.getAttribute("Dtapp");

        invitoRow.setAttribute("Codesitoinvito", "?");
        invitoRow.setAttribute("Livesito", livello);

        SoggUtils.filtraEsiti(dataApp, livello.intValue());

        Boolean gestioneHPV = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");

        // Valorizzo test proposto (solo se e' gestito HPV)
        if (Boolean.TRUE.equals(gestioneHPV)) {
            String testProposto = null;
            if (livello.intValue() == 2) {
                //testProposto = null;

            } else {
                String codts = (String) invitoRow.getAttribute("Codts");

                // Recupero l'azienda dell'invito
                ViewObject aziendaView = am.findViewObject("A_SoAziendaView1");
                Row aziendaRow = aziendaView.getCurrentRow();
                if (aziendaRow == null) {
                    aziendaView.setWhereClause("CODAZ = '" + ulss + "'");
                    aziendaView.executeQuery();
                    aziendaRow = aziendaView.first();
                }

                // Recupero la configurazione del tipo invito selezionato
                String tipoInvito = (String) invitoRow.getAttribute("Idtpinvito");
                ViewObject tipoInvitoView = am.findViewObject("Sogg_TipoInvitoView1");
                Row tipoInvitoRow = null;
                for (tipoInvitoRow = tipoInvitoView.first(); tipoInvitoRow != null;
                     tipoInvitoRow = tipoInvitoView.next()) {
                    if (tipoInvitoRow.getAttribute("Idtpinvito").equals(tipoInvito)) {
                        break;
                    }
                }

                Integer hpv = (Integer) tipoInvitoRow.getAttribute("Hpv");

                if (hpv != null && hpv.intValue() == 1) {
                    testProposto = "HPV";
                } else if (hpv != null && hpv.intValue() == 0) {
                    testProposto = "PAP";
                } else if (hpv != null && hpv.intValue() == 2) {

                    // Recupero il round individuale HPV
                    ViewObject soggScrView = am.findViewObject("Sogg_SoSoggScrView1");
                    soggScrView.setWhereClause("Sogg_SoSoggScr.ULSS = '" + ulss + "' AND Sogg_SoSoggScr.TPSCR = '" +
                                               tpscr + "' AND Sogg_SoSoggScr.CODTS = '" + codts + "'");
                    soggScrView.executeQuery();
                    Row soggScrRow = soggScrView.first();
                    int roundIndivHpv = 0;
                    if (soggScrRow != null) {
                        Integer n = (Integer) soggScrRow.getAttribute("RoundindivHpv");
                        if (n != null) {
                            roundIndivHpv = n.intValue();
                        }
                    }
                    if (roundIndivHpv > 0) {
                        testProposto = "PAP";
                    } else {
                        // Calcolo l'eta'
                        Row soggettoRow = am.findViewObject("Sogg_SoSoggettoView1").getCurrentRow();
                        int eta = calcolaEta(((Date) soggettoRow.getAttribute("DataNascita")).dateValue());

                        int etaMin =
                            aziendaRow.getAttribute("Etahpvmin") != null ?
                            ((Integer) aziendaRow.getAttribute("Etahpvmin")).intValue() : 0;
                        int etaMax =
                            aziendaRow.getAttribute("Etahpvmax") != null ?
                            ((Integer) aziendaRow.getAttribute("Etahpvmax")).intValue() : 999;
                        if (eta >= etaMin && eta <= etaMax) {
                            testProposto = "HPV";
                        } else {
                            testProposto = "PAP";
                        }
                    }
                }
            }
            invitoRow.setAttribute("TestProposto", testProposto);
        }
    }

    private int calcolaEta(java.util.Date dataNascita) {
        Calendar calNascita = Calendar.getInstance();
        calNascita.setTime(dataNascita);
        Calendar calAdesso = Calendar.getInstance();
        int eta = calAdesso.get(Calendar.YEAR) - calNascita.get(Calendar.YEAR);
        if (calNascita.get(Calendar.MONTH) > calAdesso.get(Calendar.MONTH) ||
            (calNascita.get(Calendar.MONTH) == calAdesso.get(Calendar.MONTH) &&
             calNascita.get(Calendar.DATE) > calAdesso.get(Calendar.DATE))) {
            eta--;
        }
        return eta;
    }

    public String onAppl() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();

        Map session = adfCtx.getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        
        boolean checkClasseOk = true;
        
        //I00101237 - SCADENZA CATEGORIA POPOLAZIONE
        if ("CO".equals(tpscr)){
            boolean bypassCheck = adfCtx.getViewScope().containsKey("bypassCheckPop");
            
            if (!bypassCheck)
                checkClasseOk = this.checkClassePop(am, adfCtx);
            
            adfCtx.getViewScope().remove("bypassCheckPop");
        }
        
        if (checkClasseOk && this.save()) {
            // rifaccio la query per aggiornare i dati
            ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
            voInvito.executeQuery();


            // 4-11-2009 filtro centro richiamo
            Row fInv = voInvito.first();
            Date dtApp = (Date) fInv.getAttribute("Dtapp");
            Date dtCreazInvito = (Date) fInv.getAttribute("Dtcreazione");
            String dtCr;
            //se la data dell'invito e' valorizzata, confronto..
            if (dtApp != null) {
                dtCr = DateUtils.dateToString(dtApp.dateValue());
                if (dtApp.compareTo(dtCreazInvito) > 0)
                    dtCr = DateUtils.dateToString(dtCreazInvito.dateValue());
            } else //altrimenti uso per forza la data di creazione dell'invito
            {
                dtCr = DateUtils.dateToString(dtCreazInvito.dateValue());
            }

            ViewObject voCtRich = am.findViewObject("Sogg_CRichInvito1");

            String tipoRich = (String) fInv.getAttribute("Tprichiamo");

            String wr = "";

            if (tipoRich == null || tipoRich.equals("")) {
                wr = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' and REALE = 0";
            } else {

                int livRich = SoggUtils.getLivelloTipoInvito(am, tipoRich, ulss, tpscr);

                if (livRich == 1)
                    wr =
                        "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' and TIPOCENTRO = 1" +
                        " and (DTCHIUSURACENTRO is null or " + " DTCHIUSURACENTRO >= to_date('" + dtCr +
                        "','dd/mm/yyyy'))";

                else
                    wr =
                        "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' and TIPOCENTRO = 2" +
                        " and (DTCHIUSURACENTRO is null or " + " DTCHIUSURACENTRO >= to_date('" + dtCr +
                        "','dd/mm/yyyy'))";

            }

            voCtRich.setWhereClause(wr);
            voCtRich.executeQuery();
            //  4-11-2009, fine
        }

        return null;
    }
    
    public String onConf() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();

        Map session = adfCtx.getSessionScope();
        String tpscr = (String) session.get("scr");
        
        boolean checkClasseOk = true;
        
        //I00101237 - SCADENZA CATEGORIA POPOLAZIONE
        if ("CO".equals(tpscr)){
            boolean bypassCheck = adfCtx.getViewScope().containsKey("bypassCheckPop");
            
            if (!bypassCheck)
                checkClasseOk = this.checkClassePop(am, adfCtx);
            
            adfCtx.getViewScope().remove("bypassCheckPop");
        }
        
        if (checkClasseOk && this.save()) {
            return "goRic";
        }

        return null;
    }
    
    @SuppressWarnings("unchecked")
    private boolean checkClassePop(Sogg_AppModule am, ADFContext adfCtx) {

        ViewObject soggscrView = am.findViewObject("Sogg_SoSoggScrView1");
        Row soggScr = soggscrView.getCurrentRow();
        
        if (soggScr!=null){
            String tpscr = (String) soggScr.getAttribute("Tpscr");
            
            Sogg_NuovoParam bean =
                (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
            String classePop = bean.getCodClassePop();
            
            if (classePop!=null && tpscr!=null){
                String query = "SELECT sccp.scadenza, sccp.descrizione FROM so_cnf_classe_pop sccp " + 
                "WHERE sccp.codclasse = '" + classePop + "' AND sccp.tpscr='" + tpscr + "'";     
                
                ViewObject vo = am.createViewObjectFromQueryStmt("", query);
                vo.setRangeSize(-1);
                vo.executeQuery();
                
                Row res = vo.first();
                
                if (res != null){
                    Date scad = (Date)res.getAttribute(0);
                    String descClassePop = res.getAttribute(1).toString();
                    
                    if(scad!=null){
                        java.util.Date scadenza = new java.util.Date(scad.dateValue().getTime());

                        if (scadenza!=null && scadenza.before(new java.util.Date())){
                            
                            adfCtx.getViewScope().put("descClassePop", descClassePop);
                            
                            if(this.classePopWarnPopup==null)
                                this.classePopWarnPopup = new RichPopup();
                            
                            this.classePopWarnPopup.show(new RichPopup.PopupHints());
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }

    @SuppressWarnings("oracle.jdeveloper.java.insufficient-catch-block")
    protected boolean beforeSave() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String user = (String) sess.get("user");
        String tpscr = (String) sess.get("scr");

        // controllo che il soggetto non sia escluso
        boolean escl = ((Boolean) sess.get("esclSalvata")).booleanValue();
        if (escl) {
            String msg = "La persona risulta esclusa. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }

        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        Row cInv = voInvito.getCurrentRow();

        Integer idCt = (Integer) cInv.getAttribute("Idcentroprelievo");
        // mauro 24/08/2010: il centro prelievo deve essere valorizzato, per forza
        if (idCt == null) {
            String msg = "Il centro prelievo e' un dato obbligatorio. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }
        //

        // controllo di consistenza livello tra centro e tipo invito
        String idTipo = (String) cInv.getAttribute("Idtpinvito");
        String ulssTipo = (String) cInv.getAttribute("Ulss");
        String tpscrTipo = (String) cInv.getAttribute("Tpscr");
        Date dtApp = (Date) cInv.getAttribute("Dtapp");

        // Controllo la coerenza tra data appuntamento e tipo invito
        if (dtApp != null) {
            // Recupero la configurazione del tipo invito
            ViewObject voTipoInvito = am.findViewObject("A_SoCnfTpinvitoView1");
            voTipoInvito.setWhereClause("TPSCR = '" + tpscrTipo + "' AND ULSS = '" + ulssTipo + "' AND IDTPINVITO = '" +
                                        idTipo + "'");
            voTipoInvito.executeQuery();
            voTipoInvito.next();
            Row rowTipoInvito = voTipoInvito.getCurrentRow();
            Date dtFineValidita = (Date) rowTipoInvito.getAttribute("Dtfinevalidita");
            if (dtFineValidita != null && dtApp.compareTo(dtFineValidita) > 0) {
                String msg = "Il tipo invito non e' valido alla data dell'appuntamento";
                this.handleException(msg, null);
                return false;
            }
        }

        boolean compatibili = SoggUtils.ctVStipo(idCt.intValue(), idTipo, ulssTipo, tpscrTipo, am);

        if (!compatibili) {
            String msg =
                "Il centro prelievo e il tipo di invito impostati " + "hanno livello diverso. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }

        //I00101171 - CONGRUENZA TRA CENTRO DI REFERTAZIONE E TIPO DI INVITO
        if (tpscr.equals("CO")){
            String sql = "SELECT ct.idtpinvito FROM SO_CENTRO_PREL_REF cpr " +
                "JOIN SO_CNF_CENTROREF_TPINVITO ct ON cpr.idcentroref = ct.idcentroref " +
                "where cpr.idcentro = " + idCt + " AND cpr.tipocentro = 1";
            
            ViewObject vo = am.createViewObjectFromQueryStmt("", sql);
            vo.setRangeSize(-1);
            vo.executeQuery();
            
            if (vo.getEstimatedRowCount()>0){
                boolean found = false;
                
                RowSetIterator it = vo.createRowSetIterator(null);
                while (it.hasNext()) {
                    Row r = it.next();
                    String idTipoOk = (String) r.getAttribute(0);
                    
                    if (idTipoOk!=null && idTipoOk.equals(idTipo))
                        found = true; 
                }
                
                if (!found){
                    String msg = "Impossibile salvare: tipo invito e centro refertazione sono incompatibili.";
                    this.handleException(msg, null);
                    return false;
                }
            }
        }

        // se previsto la data esame deve essere valorizzata, altrimenti
        // viene svuotata
        tpscr = (String) cInv.getAttribute("Tpscr");
        Integer liv = (Integer) cInv.getAttribute("Livello");
        String codEsit = (String) cInv.getAttribute("Codesitoinvito");
        // gaion 10/06/2011
        String metodo = (String) cInv.getAttribute("MetodoCalcolo");
        Integer dtObblig = (Integer) cInv.getAttribute("DataObbligatoria");
        if (metodo != null && metodo.equals("D")) {
            Date dtEsame = (Date) cInv.getAttribute("Dtesamerecente");
            if (dtEsame == null && dtObblig != null && dtObblig.intValue() == 1) {
                String msg = "Dato l'esito inserito e' necessario impostare la data dell'esame. Impossibile aggiornare.";
                this.handleException(msg, null);
                return false;
            }
        } else {
            cInv.setAttribute("Dtesamerecente", null);
        }
        
        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
         if (tpscr.equals("CO") && (Boolean)sess.get("covid19")){
             try {
                Integer IdMotivo = (Integer) cInv.getAttribute("MotivoIdcnfref");
                String motivo_obbl =
                    ParametriSistema.getParamValue(voParam, (String) sess.get("ulss"), tpscr,
                                                   ConfigurationConstants.motivo_obbl);
                
                if(motivo_obbl!=null && "S".equalsIgnoreCase(motivo_obbl) && IdMotivo==null){
                    String msg =
                        "Il motivo di esecuzione � obbligatorio.";
                    this.handleException(msg, null);
                    return false;
                }
                
            } catch (Exception e) {
            }
         }       

        if (tpscr.equals("CO")) {
            if (!codEsit.equals(ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO)) {
                cInv.setAttribute("Dtconscont", null);
                if (!codEsit.equals(ConfigurationConstants.CODICE_ESITO_CONTENITORE_RITIRATO)) {
                    cInv.setAttribute("Dtritcont", null);
                }
            }
        }

        Integer idApp = (Integer) cInv.getAttribute("Idapp");

        if (idApp != null) {
            // allineo orario
            ViewObject voAg = am.findViewObject("Sogg_QueryAgendaView1");
            voAg.setWhereClause("IDAPP = " + idApp.toString());
            voAg.executeQuery();
            Row slot = voAg.first();
            Integer ora = (Integer) slot.getAttribute("Oraapp");
            Integer min = (Integer) slot.getAttribute("Minapp");
            cInv.setAttribute("Oraapp", ora);
            cInv.setAttribute("Minapp", min);
        }

        Date dtAppIni = (Date) cInv.getAttribute("Dtappiniziale");
        if (dtAppIni == null) {
            try {
                Integer oraApp = (Integer) cInv.getAttribute("Oraapp");
                Integer minApp = (Integer) cInv.getAttribute("Minapp");

                if (dtApp != null && oraApp != null && minApp != null) {
                    Calendar c = Calendar.getInstance(Locale.ITALY);
                    c.clear();
                    c.setTime(dtApp.dateValue());
                    c.set(11, oraApp.intValue());
                    c.set(12, minApp.intValue());
                    
                    oracle.jbo.domain.Date dtIni = new oracle.jbo.domain.Date(new java.sql.Timestamp(c.getTime().getTime()));

                    cInv.setAttribute("Dtappiniziale", dtIni);
                }
            } catch (Exception ex) {
            }
        }

        GeneratoreInviti gen =
            new GeneratoreInviti(am, (String) cInv.getAttribute("Codts"), (String) cInv.getAttribute("Ulss"),
                                 (String) cInv.getAttribute("Tpscr"));
        try {
            // mauro 20-06, aggiornamenteo centro ref.
            Integer idCtRef = SoggUtils.getCtRef(am, idCt.intValue());
            cInv.setAttribute("Idcentroref1liv", idCtRef);
            //

            gen.updateRoundIndiv(cInv);
            /*Integer x=*/gen.updateAndGetRoundInviti(cInv);

            String ulss = (String) cInv.getAttribute("Ulss");
            /*MOD 20071212 il richiamo va ricalcolato solo se non c'e' gia' un referto*/
            boolean up_richiamo = true;
            String s = "select * from ";
            if (liv.intValue() == 1) {
                s = s +
                    (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO1LIV", "SO_REFERTOCOLON1LIV",
                                                      "SO_REFERTOMAMMO1LIV", "SO_REFERTOCARDIO", "SO_REFERTOPFAS");
            } else {
                s = s +
                    (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO2LIV", "SO_REFERTOCOLON2LIV",
                                                      "SO_REFERTOMAMMO2LIV", null, null);
            }

            s = s + " R WHERE R.IDINVITO=" + cInv.getAttribute("Idinvito");
            ViewObject ref_vo = null;
            try {
                ref_vo = am.createViewObjectFromQueryStmt("ref", s);
                ref_vo.executeQuery();
                up_richiamo = (ref_vo.first() == null);
            } catch (Exception rex) {
                up_richiamo = true;
            } finally {
                if (ref_vo != null)
                    ref_vo.remove();
            }

            if (up_richiamo) {
                Integer idInvito = (Integer) cInv.getAttribute("Idinvito");
                String codEsInvNew = (String) cInv.getAttribute("Codesitoinvito");
                Integer idCtPrelNew = (Integer) cInv.getAttribute("Idcentroprelievo");
                Date dtAppNew = (Date) cInv.getAttribute("Dtapp");
                //20110404 Serra
                Date dtEsameRecNew = (Date) cInv.getAttribute("Dtesamerecente");

                boolean modifiche =
                    SoggUtils.datiRichiamoModificati(am, idInvito.intValue(), codEsInvNew, idCtPrelNew.intValue(), dtAppNew, dtEsameRecNew);
                //fine

                if (!modifiche)
                    up_richiamo = false;
            }

            if (up_richiamo) {
                ViewObject voCnfEsito = am.findViewObject("Sogg_CnfEsito1");
                String codEsito = (String) cInv.getAttribute("Codesitoinvito");
                Integer livEsito = (Integer) cInv.getAttribute("Livesito");

                String whcl =
                    "CODESITOINVITO = '" + codEsito + "' and LIVESITO = " + livEsito.toString() + " and ULSS = '" +
                    ulss + "' and TPSCR = '" + tpscr + "'";
                voCnfEsito.setWhereClause(whcl);
                voCnfEsito.executeQuery();

                Row cnfEsito = voCnfEsito.first();

                String tpRich = (String) cnfEsito.getAttribute(2);
                Integer categ = (Integer) cnfEsito.getAttribute(6);
                Integer gg = (Integer) cnfEsito.getAttribute(3);
                Integer livRich = (Integer) cnfEsito.getAttribute(7);

                ViewObject voCt = am.findViewObject("A_SoCentroPrelRefView1");

                GestoreReferti.insertRichiamo(cInv, tpRich, categ, livRich == null ? 0 : livRich.intValue(), gg, voCt,
                                              am);
            }

            // gestione dei codici progressivi
            Integer idInvito = (Integer) cInv.getAttribute("Idinvito");
            if (tpscr.equals("CO")) {
                InvitoUtils.gesAccColon(am, idInvito, dtApp, liv.intValue(), idCtRef, user, ulss, tpscr);               
            }

            if (tpscr.equals("MA")) {
                int livello = liv.intValue();
                String prodNrich = "";
                String prodNrich2 = "";

                //ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
                if (livello == 1)
                    prodNrich =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
                if (livello == 2)
                    prodNrich2 =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA2);

                if ((livello == 1 && "S".equals(prodNrich)) || (livello == 2 && "S".equals(prodNrich2))) {
                    InvitoUtils.gesAccMammo(am, idInvito, dtApp, livello, ulss, idCtRef, user, tpscr);
                }
            }

            // mauro 7-10-2008, trattato anche cito per hpv
            // 27122013 Gaion: cod_richiesta per 2 liv
            if (tpscr.equals("CI")) {
                //gaion 07/06/2011
                Boolean hpv = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
                String testProposto = null;
                if (hpv != null && hpv.booleanValue()) {
                    String testPropostoOrig = (String) sess.get("testPropostoOrig");
                    testProposto = (String) cInv.getAttribute("TestProposto");

                    // Verifico se il test proposto e' stato modificato
                    if ((testProposto != null && !testProposto.equals(testPropostoOrig)) ||
                        (testProposto == null && testPropostoOrig != null)) {
                        if (!"HPV".equals(testProposto)) {
                            hpv = Boolean.FALSE;
                        }

                        // Se il test proposto non e' stato modificato, lascio il campo HPV uguale a prima
                        // null = null, 1 = false, 2 = true
                    } else {
                        ViewObject accCitoView = am.findViewObject("A_SoAccCito1livView1_1");
                        accCitoView.setWhereClause("IDINVITO = " + idInvito.toString());
                        accCitoView.executeQuery();
                        A_SoAccCito1livView1RowImpl accCitoRow = (A_SoAccCito1livView1RowImpl) accCitoView.first();
                        if (accCitoRow != null) {
                            Integer prelievoHpv = accCitoRow.getPrelievoHpv();
                            if (prelievoHpv == null) {
                                hpv = null;
                            } else if (prelievoHpv.intValue() == 1) {
                                hpv = Boolean.FALSE;
                            } else if (prelievoHpv.intValue() == 2) {
                                hpv = Boolean.TRUE;
                            }
                        }
                    }
                }
                InvitoUtils.gesAccCito(am, idInvito.intValue(), dtApp, liv.intValue(), idCtRef, user, ulss, tpscr, hpv, false,
                                       testProposto);
            }
            //
            //20170126 Serra: cod richiesta e campione per il PFAS
            if (tpscr.equals("PF")) {

                InvitoUtils.gesAccPfas(am, idInvito.intValue(), dtApp, liv.intValue(), idCtRef, user, ulss, tpscr);
            }

            // 06/11/2009 rigenerazione lettera
            boolean modApp = SoggUtils.appModificato(am, idInvito, idApp);

            if (modApp) {
                String codTs = (String) cInv.getAttribute("Codts");
                String testProposto = (String) cInv.getAttribute("TestProposto");

                InvitoUtils.gestioneRigLettera(am, ulssTipo, tpscrTipo, idTipo, codTs, idApp, idInvito, idCt, dtApp,
                                               testProposto);
            }
            // 06/11/2009, fine
        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = ex.getMessage();
            this.handleMessages(FacesMessage.SEVERITY_ERROR, msg);
            this.doRollback();
            return false;
        }

        // dati aggiornamento
        cInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
        cInv.setAttribute("Opmodifica", user);

        return true;
    }
    
    protected String afterSave() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();    
        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        Row cInv = voInvito.getCurrentRow();
        String codTs = (String) cInv.getAttribute("Codts");
        am.getTransaction().postChanges();
        
        Sogg_NuovoParam bean =
            (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
        String classePop = bean.getCodClassePop();
        if (classePop != null){
            String upStmt = " UPDATE SO_SOGG_SCR SET CODCLASSEPOP = '"+classePop+ "' "+
                " WHERE CODTS = '"+codTs+"' AND ULSS = '"+ulss+"' AND TPSCR = '"+tpscr+"' ";
        
            am.getTransaction().executeCommand(upStmt);
        }
        
        if (tpscr.equals("CO") && (Boolean)sess.get("covid19")){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoInvitoView1Iterator");
            
            if(voIter!=null){
                Row _r = voIter.getCurrentRow();
                if(_r!=null){
                    Integer MotivoidCnfRef = (Integer) _r.getAttribute("MotivoIdcnfref");
                    String MotivoGruppo = (String) _r.getAttribute("MotivoGruppo");
                    Integer IdMotivo = (Integer) _r.getAttribute("MotivoId");
                    
                    // se IdMotivo == null --> che non � presente il dato sulla tabella SO_INV_MOTIVO (INSERT SE MotivoidCnfRef <> null)
                    // se IdMotivo <> null --> che � presente il dato sulla tabella SO_INV_MOTIVO (DELETE SE MotivoidCnfRef == null UPDATE MotivoidCnfRef <> null)
                    String stm = null;
                    if(IdMotivo==null && MotivoidCnfRef!=null)
                        stm = " INSERT INTO SO_INV_MOTIVO (ID , IDCNFREF , IDINVITO , DTCREAZIONE, DTULTIMAMOD , GRUPPO , NOTE , OPINSERIMENTO , OPMODIFICA , TPSCR , ULSS) VALUES" +
                              "(so_inv_motivo_seq.nextVal, " + MotivoidCnfRef + ", " + cInv.getAttribute("Idinvito") + ", sysdate, sysdate, '" + MotivoGruppo + "', null, '" 
                              + sess.get("user") + "', '" + sess.get("user") + "', '" + tpscr + "', '" + ulss + "')";
                    else if(IdMotivo!=null && MotivoidCnfRef!=null && IdMotivo.intValue()!=MotivoidCnfRef.intValue())
                        stm = " UPDATE SO_INV_MOTIVO SET IDCNFREF = " + MotivoidCnfRef +
                              ", GRUPPO = '" + MotivoGruppo + "' " +
                              ", OPMODIFICA = '" + sess.get("user") + "' " +
                              ", DTULTIMAMOD = sysdate" +
                              " WHERE ID = " + IdMotivo;
                    else if(IdMotivo!=null && MotivoidCnfRef==null)
                        stm = " DELETE FROM SO_INV_MOTIVO " +
                              " WHERE ID = " + IdMotivo;
                    
                    if(stm!=null)
                        am.getTransaction().executeCommand(stm);
                }
            }
        }
        
        am.getTransaction().commit();
        
        ViewObject voSoggScr = am.findViewObject("Sogg_SoSoggScrView1");
        voSoggScr.executeQuery();
        voSoggScr.setCurrentRow(voSoggScr.first());
        
        bean.setDirty(false);
        
        return null;
    }

    protected void beforeNavigate(String dest) throws Exception {
        SoggUtils.beforeNavSogg(dest, false);
        if (dest.equals("goNuovoInv")) {
            Map sess = ADFContext.getCurrent().getSessionScope();
            sess.put("creaInv", Boolean.valueOf(true));
            sess.put("invCreato", Boolean.valueOf(true));
            try {
                String msg = InvitoUtils.creaInvito(true);
                if (msg != null)
                    this.saveMessages(msg);
            } catch (Exception ex) {
                throw new Exception("Impossibile controllare l'eventuale presenza di primi inviti/" +
                                     "accessi spontanei nello stesso round: " + ex.getMessage(), null);
            }
        }
    }

    protected void doRollback() {
        // TODO:  Override this view.commons.Parent_DataForwardAction method
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        String[] views = { "Sogg_RicercaView1" };
        am.doRollback(views);
        SoggUtils.loadVoDet();
    }


    public String onCreateLetter() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        try {
            //prima salvo
            this.onAppl();

            ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
            voInvito.executeQuery();
            ViewObject allegati = am.findViewObject("Sogg_StInvitiAllegView2");

            Row all = allegati.first();
               
            //il metodo beforesave aggiorna gia' la lettera se esisteva (ma non stampata)
            //altrimenti va creata (04062019 Gaion)
            if (all == null) {
                    Row cInv = voInvito.getCurrentRow();
                                    
                    GeneratoreInviti gen = new GeneratoreInviti(am,
                                (String) cInv.getAttribute("Codts"),
                                (String) cInv.getAttribute("Ulss"),
                                (String) cInv.getAttribute("Tpscr")); 
                    
                    //trovo la tessera sanitaria
                    String tesseraSanitaria = SoggUtils.trovaTessera(am,(String) cInv.getAttribute("Codts"),(String) cInv.getAttribute("Ulss"));
                    
                    gen.creaLettera((String) cInv.getAttribute("Ulss"),
                                    (String) cInv.getAttribute("Tpscr"),
                                    (String) cInv.getAttribute("Idtpinvito"),
                                    (String) cInv.getAttribute("Codts"),
                                    (Integer) cInv.getAttribute("Idapp"),
                                    (Integer) cInv.getAttribute("Idinvito"),
                                    (String) cInv.getAttribute("TestProposto"),
                                    tesseraSanitaria,
                                    (String) cInv.getAttribute("Otp"));                     
                  } else if (all.getAttribute("Dtstampa") != null)
                  {
                    //aggiorno la lettera visto che il metodo beforeSave non lo fa
                    Row cInv = voInvito.getCurrentRow();
                                    
                    GeneratoreInviti gen = new GeneratoreInviti(am,
                            (String) cInv.getAttribute("Codts"),
                            (String) cInv.getAttribute("Ulss"),
                            (String) cInv.getAttribute("Tpscr"));
                    gen.deleteLettera((Integer)all.getAttribute("Idallegato"));  
                    
                    //trovo la tessera sanitaria
                    String tesseraSanitaria = SoggUtils.trovaTessera(am,(String) cInv.getAttribute("Codts"),(String) cInv.getAttribute("Ulss"));
                    
                    gen.creaLettera((String) cInv.getAttribute("Ulss"),
                                    (String) cInv.getAttribute("Tpscr"),
                                    (String) cInv.getAttribute("Idtpinvito"),
                                    (String) cInv.getAttribute("Codts"),
                                    (Integer) cInv.getAttribute("Idapp"),
                                    (Integer) cInv.getAttribute("Idinvito"),
                                    (String) cInv.getAttribute("TestProposto"),
                                    tesseraSanitaria,
                                    (String) cInv.getAttribute("Otp"));  
                  }
            
            am.getTransaction().commit();
            
                allegati.executeQuery();
        } catch (Exception e) {
            this.handleException("Impossible generare/aggiornare la lettera: " + e.getMessage(), am,
                                 new String[] { "Sogg_RicercaView1" });
        }
        
        return null;
    }

    public static void findForward() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject allegati = am.findViewObject("Sogg_StInvitiAllegView2");
        allegati.executeQuery();

        // Memorizzo il valore attuale del test proposto
        // perche' all'atto del salvataggio devo vedere se e' cambiato
        Map session = ADFContext.getCurrent().getSessionScope();
        String testPropostoOrig = (String) session.get("testPropostoOrig");
        if (testPropostoOrig == null) {
            Row invitoRow = am.findViewObject("Sogg_SoInvitoView1").getCurrentRow();
            session.put("testPropostoOrig", invitoRow.getAttribute("TestProposto"));
        }
        
        String tpscr = (String) session.get("scr");
        String ulss  = (String) session.get("ulss");
        
        //I00102494
        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
        try {
            String blocco_tpinvito =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, "blocco_tpinvito");
            
            if("S".equalsIgnoreCase(blocco_tpinvito))
                session.put("blocco_tpinvito", "true");
            else 
                session.put("blocco_tpinvito", "false");
        } catch (Exception e) {
            session.put("blocco_tpinvito", "false");
        }
            
        try {    
            /* I00102494 - Il sistema deve inoltre verificare l'eventuale presenza, sempre per ulss e 
             * tipo screening, del parametro sposta_invito in so_cnf_parametri_sistema:
             * 
             * Se assente, il comportamento del sistema resta invariato, ovvero non sono previsti blocchi
             * Se presente e valorizzato con un numero intero N, gli inviti possono essere spostati solo se 
             * l'appuntamento è in data D tale che D+N > trunc(sysdate) */
            String sposta_invito =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, "sposta_invito");
            
            if(sposta_invito.matches("-?\\d+")){
                int n = Integer.parseInt(sposta_invito);
                
                Row invitoRow = am.findViewObject("Sogg_SoInvitoView1").getCurrentRow();
                if (invitoRow!=null && invitoRow.getAttribute("Dtapp")!=null){
                    java.util.Date dataApp = ((Date)invitoRow.getAttribute("Dtapp")).getValue();
    
                    Calendar c = Calendar.getInstance();
                    c.setTime(dataApp);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    c.add(Calendar.DATE, n);
                    dataApp = c.getTime();
                    
                    c.setTime(new java.util.Date());
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    
                    java.util.Date oggi = c.getTime();
                    
                    if (dataApp.after(oggi))
                        session.put("sposta_invito", "true");
                    else 
                        session.put("sposta_invito", "false");
                }
            
            } else
                session.put("sposta_invito", "true");
            
        } catch (Exception e) {
            session.put("sposta_invito", "true");
        }
               
        //I00101237 Filtro le classi di popolazione per data attuale      
        ViewObject classePopView = am.findViewObject("Sogg_SoCnfClassePopView1");
        classePopView.setWhereClause("tpscr ='" + tpscr + "' AND scadenza IS NULL OR TRUNC(scadenza) >= TRUNC(SYSDATE)");
        classePopView.executeQuery();
    }

    //01/12/2011 gaion: export puntuale degli appuntamenti
    public String onExportApp() {

        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        Sogg_AppModule sogg_am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = sogg_am.findViewObject("Sogg_SoInvitoView1");
        ViewObject voSoggetto = sogg_am.findViewObject("Sogg_SoSoggettoView1");
        try {

            sess.put("renderChiudiOp", Boolean.TRUE);

            ImpexpBean bean =
                (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();

            String wh =
                "IMPEXP='" + ConfigurationConstants.IMPEXP_EXP + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'";

            Row cInv = voInvito.getCurrentRow();

            Integer livello = (Integer) cInv.getAttribute("Livello");

            if (livello == 2) { //invito di 2 livello
                if (tpscr.equals("MA")) {
                    //dipartimentale di default
                    bean.setTpdip("R2");
                } else if (tpscr.equals("CI")) {
                    bean.setTpdip("G2");
                } else if (tpscr.equals("CO")) {
                    bean.setTpdip("E2");
                } else if (tpscr.equals("PF")) {
                    bean.setTpdip("LA");
                }
                //livello di default
                bean.setLivello("2");
                bean.setIdcentro(null);
                bean.setCentro_prel((Integer) cInv.getAttribute("Idcentroprelievo"));

            } else //invito di 1 livello
            {
                bean.setLivello("1");
                if (tpscr.equals("MA")) {
                    bean.setTpdip("RA");
                } else if (tpscr.equals("CI")) {
                    //bean.setTpdip("AP");
                } else if (tpscr.equals("CO") || tpscr.equals("PF")) {
                    bean.setTpdip("LA");
                }
                bean.setIdcentro((Integer) cInv.getAttribute("Idcentroref1liv"));
                bean.setCentro_prel((Integer) cInv.getAttribute("Idcentroprelievo"));
            }

            Date dtinvito = (Date) cInv.getAttribute("Dtapp");

            bean.setId_invito((Integer) cInv.getAttribute("Idinvito"));
            bean.setData(dtinvito.dateValue());
            bean.setData_max(dtinvito.dateValue());

            Row cSogg = voSoggetto.getCurrentRow();
            bean.setNome((String) cSogg.getAttribute("Nome"));
            bean.setCognome((String) cSogg.getAttribute("Cognome"));
            bean.setCodts((String) cSogg.getAttribute("Codts"));

            ViewObject tp = am.findViewObject("Impexp_SoCnfTpdipartimentaleView1");

            tp.setWhereClause(wh);
            tp.executeQuery();

            //filtro anche i centri utilizzati nelle configurazioni PER QUEL TIPO DIP
            ViewObject vo = am.findViewObject("Impexp_SoCnfCentriImpexpView1");
            if (bean.getIdcentro() != null) {
                vo.setWhereClause(wh + " AND IDCENTROREF='" + bean.getIdcentro() + "'");

            } else {
                if (bean.getTpdip() != null) {
                    vo.setWhereClause(wh + " AND TPDIP='" + bean.getTpdip() + "'");
                } else {
                    vo.setWhereClause(wh);
                }
            }
            vo.executeQuery();
            Row centroImpExp = vo.first();
            if (centroImpExp != null) {
                //cambio TPDIP in modo che sia coerente con il centro di ref dell'invito
                bean.setTpdip((String) centroImpExp.getAttribute("Tpdip"));
            }

            //filtro sulla configurazione dell'import che mi interessa
            vo = am.findViewObject("Impexp_SoCnfImpexpView1");
            if (bean.getTpdip() != null) {
                vo.setWhereClause(wh + " AND TPDIP='" + bean.getTpdip() + "'");
            } else {
                vo.setWhereClause(wh + " AND IDCENTROREF IS NOT NULL"); //per evitare nullpointer dopo in selectCentro
            }
            vo.executeQuery();

            //OTTENGO TUTTE LE RIGHE, UNA PER OGNI CENTRO DI REFERTAZIONE
            Impexp_dataForwardAction.selectCentro(am, bean);

            Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();


            //memorizzo la modalita' nella sessione
            sess.put("impexp_modalita", r.getModalita());

            sess.put("LINK_INV", Boolean.TRUE);

            return "to_impexp_app_single";


        } catch (Exception ex) {
            this.handleException("Funzionalita' non utilizzabile: " + ex.getMessage());
        } finally {

        }
        
        return null;
    }

    public void onSetDirty(ValueChangeEvent valueChangeEvent) {
        Sogg_NuovoParam bean =
            (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
        bean.setDirty(true);
    }
    
    @Override
    public String onRichnav(String dest, HashMap params) {
        //prima si deve impostare il nome dell'application module
        this.setAppModule();
        Map req = ADFContext.getCurrent().getRequestScope();
        req.putAll(params);

        boolean modP = this.pendingUpdatesOnRichnav();
        
        //controllo anche eventuali variazioni all'interfaccia, registrate tramite il bean
        Sogg_NuovoParam bean =
            (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
        boolean modP2 = bean.isDirty();
                
        //se ci sono modifiche non salvate...
        if (modP || modP2) { //imposto l'attributo destinazione e lascio al metodo onLoad la gestione
            req.put("destNav", dest);
            ADFContext.getCurrent().getViewScope().put("destNav", dest);
            RequestContext.getCurrentInstance().addPartialTarget(getFvDest());
            Utility.addScriptOnPartialRequest("wantToSaveOnLoad()");
            return "error";
        } else { //altriemnti navigo verso la destinzione

            try {
                this.beforeNavigate(dest);
                //e navigo verso la destinazione
                if (dest == null)
                    throw new Exception("destinazione non specificata");
            } catch (Exception ex)

            {
                ex.printStackTrace();
                this.handleException("Impossibile navigare verso la destinazione " + dest + ": " + ex.getMessage(),
                                     null);
                return null;
            }
        }
        return dest;
    }
    
    public void setClassePopWarnPopup(RichPopup classePopWarnPopup) {
        this.classePopWarnPopup = classePopWarnPopup;
    }

    public RichPopup getClassePopWarnPopup() {
        return classePopWarnPopup;
    }
}
