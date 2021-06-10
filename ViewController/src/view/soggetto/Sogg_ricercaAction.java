package view.soggetto;

import insiel.dataHandling.DateUtils;

import java.security.MessageDigest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.Sogg_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ConfigurationReader;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.conf.Cnf_SoCnfInvitiFastViewRowImpl;

import model.datacontrol.PL_Bean;
import model.datacontrol.Sogg_RicParam;

import model.inviti.InvitoUtils;

import model.soggetto.Sogg_SoChiaviViewRowImpl;
import model.soggetto.Sogg_SoInvitoViewRowImpl;
import model.soggetto.common.Sogg_SoSoggettoViewRow;

import model.util.MessageBundle;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.component.rich.layout.RichPanelHeader;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.server.SequenceImpl;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.ReturnEvent;
import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.comune.LovComuneAction;

import view.util.Utility;

public class Sogg_ricercaAction extends Parent_DataForwardAction {
    private RichPanelHeader panelSearch;
    private RichInputText codiceComune;
    private RichInputText descComune;
    private RichSelectOneChoice idDistr;
    private RichInputText codMed;
    private RichInputText desMedico;
    private RichTable soggTable;
    private RichPopup invFast1;
    private RichPopup invFast2;

    public void setSoggTable(RichTable soggTable) {
        this.soggTable = soggTable;
    }

    public RichTable getSoggTable() {
        return soggTable;
    }

    public Sogg_ricercaAction() {
        super();
    }

    public String onCerca() {
        @SuppressWarnings("deprecation")
        Sogg_RicParam bean =
            (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        //03102011 Gaion parametri di ricerca minimi
        boolean paramOk = false;
        if (bean.getCognome() != null) {
            if (!("null").equals(bean.getCognome())) {
                if (bean.getCognome().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (bean.getNome() != null) {
            if (!("null").equals(bean.getNome())) {
                if (bean.getNome().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (session.get("dtNasc") != null) {
            paramOk = true;
        }

        if (bean.getTessSan() != null) {
            if (!("null").equals(bean.getTessSan())) {
                if (bean.getTessSan().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (bean.getCodFisc() != null) {
            if (!("null").equals(bean.getCodFisc())) {
                if (bean.getCodFisc().length() > 1) {
                    paramOk = true;
                }
            }
        }
        
        if (bean.getChiave() != null) {
            if (!("null").equals(bean.getChiave())) {
                if (bean.getChiave().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (bean.getMpiRegionale() != null) {
            if (!("null").equals(bean.getMpiRegionale())) {
                if (bean.getMpiRegionale().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (bean.getCodiceDocumento() != null) {
            if (!("null").equals(bean.getCodiceDocumento())) {
                if (bean.getCodiceDocumento().length() > 1) {
                    paramOk = true;
                }
            }
        }
        
        if (bean.getCodRichiesta() != null) {
            if (!("null").equals(bean.getCodRichiesta())) {
                if (bean.getCodRichiesta().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (bean.getCodCampione() != null) {
            if (!("null").equals(bean.getCodCampione())) {
                if (bean.getCodCampione().length() > 1) {
                    paramOk = true;
                }
            }
        } 

        if (session.get("dtInvito") != null) {
            paramOk = true;
        }

        if (!paramOk) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, MessageBundle.getMessageFromApplicationMessageBundle(adfCtx.getLocale(), "SEARCH_SOGG_CHK_PARAM_MSG", true), "");
            ctx.addMessage(null, fm);
        } else {
            // Eseguo la query
            bean.querySogg();
        }
        bean = null;
        
        checkSelected();  //TODO: perche' qua?????
        return "searched";
    }

    @SuppressWarnings("unchecked")
    public void onChineta() {
        @SuppressWarnings("deprecation")
        Sogg_RicParam bean =
            (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
        int inEta = bean.getInEta().intValue();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        boolean isIneta = (inEta == 1);
        session.put("inEta", Boolean.valueOf(isIneta));

    }

    @SuppressWarnings("unchecked")
    public String onChCodCom() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        @SuppressWarnings("deprecation")
        Sogg_RicParam bean =
            (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
        String idComRes = bean.getIdComRes();
        session.put("codCom", idComRes);
        SoggUtils.filtraDistretti(idComRes, "Sogg_DistrettiView1");
        
        String desCom = bean.getDesComRes();
        Map req = adfCtx.getRequestScope();
        if (desCom == null || desCom.equals("")) {
            bean.setCodComRes(null);
            
            boolean onClick = false;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : false;
                }catch(Throwable th){
                    onClick = false;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', false)");
                return null;
            }
        }
        
        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                Map page = adfCtx.getPageFlowScope();
                page.put("iteratorBinding", req.get("iteratorBinding"));
                page.put("source", req.get("source"));
                
                LovComuneAction.checkOnLovFilter();
                
                page.remove("iteratorBinding");
                page.remove("itName");
                page.remove("source");                
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovComune";
    }
    
    public boolean isAurEnabled(){
        String enabledStr = ConfigurationReader.readProperty("AUR_SERVICE_ENABLED");
        Boolean enabled = Boolean.FALSE;
        if (enabledStr != null && enabledStr.equals("1")) {
            enabled = Boolean.TRUE;
        }

        return enabled;
    }
    
    private String fncsha(String inputVal) throws Exception {
        try {
            MessageDigest myDigest = MessageDigest.getInstance("SHA-256");
            myDigest.update(inputVal.getBytes());
            byte[] dataBytes = myDigest.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < dataBytes.length; i++) {
                sb.append(Integer.toString((dataBytes[i])).substring(1));
            }
    
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < dataBytes.length; i++) {
                String hex = Integer.toHexString(0xff & dataBytes[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
    
            String retParam = hexString.toString();
            return retParam;
            
        } catch (Exception e){                                      
           return null;
        }
    }

    @SuppressWarnings("unchecked")
    public String onNuova() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.put("showTabs", Boolean.FALSE);
        session.put("LINK_ACC", Boolean.FALSE);
        session.put("LINK_REF", Boolean.FALSE);
        session.put("anagEsiste", Boolean.FALSE);
        session.put("invitoPresente", Boolean.FALSE);
        @SuppressWarnings("deprecation")
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        // creo nuova anag
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        // forzo query vuota altrimenti poi non visualizzo la nuova anag
        // (perche' ?)
        voAnag.setWhereClause("1=2");
        voAnag.executeQuery();

        Sogg_SoSoggettoViewRow nAnag = (Sogg_SoSoggettoViewRow) voAnag.createRow();

        //creo un codts
        SequenceImpl s = new SequenceImpl("SO_CODTS_SEQ", am);
        Integer idCodts = s.getSequenceNumber().intValue();
        String codtsString = idCodts.toString();
        codtsString = "S" + "0000000000".substring(codtsString.length()) + codtsString;
        nAnag.setCodts(codtsString);

        String ulss = (String) session.get("ulss");
        String user = (String) session.get("user");
        String tpscr = (String) session.get("scr");
        Date dtoraCorr = DateUtils.getOracleDateNow();
        nAnag.setUlss(ulss);
        nAnag.setDtcreazione(dtoraCorr);
        nAnag.setOpcreazione(user);
        nAnag.setDtultmodifica(dtoraCorr);
        nAnag.setOpultmodifica(user);
        nAnag.setCodanagreg(new Integer(0));

        voAnag.insertRow(nAnag);

        voAnag.setCurrentRow(nAnag);
        
        //Inserisco il recordo in SO_CHIAVI
        ViewObject voChiavi = am.findViewObject("Sogg_SoChiaviView1");

        voChiavi.setWhereClause("1=2");
        voChiavi.executeQuery();

        Sogg_SoChiaviViewRowImpl chiave = (Sogg_SoChiaviViewRowImpl) voChiavi.createRow();

        String concat = codtsString + ulss;
        String hash = "";
       
        try {
            hash= this.fncsha(concat);
        } catch (Exception e){                                      
           hash = null;
        }
            
        chiave.setChiave(hash);
        chiave.setCodts(codtsString);
        chiave.setUlss(ulss);
        chiave.setDataCreazione(dtoraCorr);
        chiave.setOpCreazione(user);
        
        voChiavi.insertRow(chiave);
        voChiavi.setCurrentRow(chiave);
       
        // inserisco record anche su SO_SOGG_SCR
        ViewObject voSoggScr = am.findViewObject("Sogg_SoSoggScrView1");
        Row r = voSoggScr.createRow();
        voSoggScr.insertRow(r);

        r.setAttribute("Codts", codtsString);
        r.setAttribute("Roundindiv", new Integer(0));
        r.setAttribute("Roundinviti", new Integer(0));
        r.setAttribute("Altorischio", ViewHelper.decodeByTpscr(tpscr, null, ConfigurationConstants.DB_FALSE, //altorischio e' valorizzato solo per il colon
                                                               null, null, null));
        r.setAttribute("VaccinatoHpv", new Integer(0));
        r.setAttribute("Consenso", new Integer(0));
        r.setAttribute("ConsensoCond", new Integer(0));
        r.setAttribute("Tpscr", tpscr);
        r.setAttribute("Ulss", ulss);

        voSoggScr.setCurrentRow(r);
        //

        return "goAnag";
    }

    @SuppressWarnings("unchecked")
    public static void navDett() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.put("showTabs", Boolean.TRUE);
        session.put("LINK_ACC", Boolean.FALSE);
        session.put("LINK_REF", Boolean.FALSE);
        session.put("anagEsiste", Boolean.TRUE);
        @SuppressWarnings("deprecation")
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Sogg_RicercaView1");
        Row cAnag = vo.getCurrentRow();
        String codTs = (String) cAnag.getAttribute("Codts");
        String ulss2 = (String) cAnag.getAttribute("Ulss");
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // setto anagrafica corrente
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        voAnag.setWhereClause("CODTS = '" + codTs + "' AND ULSS='"+ulss2+"'");
        voAnag.executeQuery();
        Row fRow = voAnag.first();
        voAnag.setCurrentRow(fRow);

        // 31072013 Gaion: fix su column ambiguously defined
        ViewObject voSoggScr = am.findViewObject("Sogg_SoSoggScrView1");
        String whScr =
            "Sogg_SoSoggScr.CODTS = '" + codTs + "' and Sogg_SoSoggScr.ULSS = '" + ulss2 +
            "' and Sogg_SoSoggScr.TPSCR = '" + tpscr + "'";
        voSoggScr.setWhereClause(whScr);
        voSoggScr.executeQuery();
        
        Row r = voSoggScr.first();
        if (r == null) {
            
            r = voSoggScr.createRow();
            voSoggScr.insertRow(r);
    
            r.setAttribute("Codts", codTs);
            r.setAttribute("Roundindiv", new Integer(0));
            r.setAttribute("Roundinviti", new Integer(0));
            r.setAttribute("Altorischio", ViewHelper.decodeByTpscr(tpscr, null, ConfigurationConstants.DB_FALSE, //altorischio e' valorizzato solo per il colon
                                                                   null, null, null));
            r.setAttribute("VaccinatoHpv", new Integer(0));
            r.setAttribute("Consenso", new Integer(0));
            r.setAttribute("ConsensoCond", new Integer(0));
            r.setAttribute("Tpscr", tpscr);
            r.setAttribute("Ulss", ulss);
            
        }
        voSoggScr.setCurrentRow(r);
        //am.getTransaction().postChanges();
        
        // setto var per dire se ci apprestiamo a creare nuovo inv
        Integer invPres = cAnag.getAttribute("Invpres")!=null ? (Integer) cAnag.getAttribute("Invpres") : null;
        boolean eInv = (invPres!=null && invPres.intValue() == 1);
        session.put("creaInv", Boolean.valueOf(!eInv));

        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");

        // filtro invito corrente
        // inserimento dell'invito viene richiesto esplicitamente
        if (eInv) {
            Integer idInv = (Integer) cAnag.getAttribute("Idinvito");
            voInvito.setWhereClause("IDINVITO = " + idInv.toString());
            voInvito.executeQuery();
            Row fInv = voInvito.first();
            voInvito.setCurrentRow(fInv);

            Integer idApp = (Integer) fInv.getAttribute("Idapp");
            boolean sceltaOrario = (idApp != null);
            session.put("sceltaOrario", Boolean.valueOf(sceltaOrario));
            SoggUtils.filtraOrari(true);

            // filtro esiti invito
            Integer livello = (Integer) fInv.getAttribute("Livello");
            Date dtApp = (Date) fInv.getAttribute("Dtapp");
            SoggUtils.filtraEsiti(dtApp, livello);

            // filtro centro prelievo
            ViewObject voCt = am.findViewObject("Sogg_CprelInvitoView1");

            //non mostro i centri chiusi in data superiore alla data di craezione dell'invito
            //o alla data dell'appuntamento (cioe' alla data piu' vecchia delle due)
            Date dtCreazInvito = (Date) fInv.getAttribute("Dtcreazione");
            String dtCr;
            //se la data dell'invito e' valorizzata, confronto..
            if (dtApp != null) {
                dtCr = DateUtils.dateToString(dtApp.dateValue());
                if (dtApp.compareTo(dtCreazInvito) > 0)
                    dtCr = DateUtils.dateToString(dtCreazInvito.dateValue());
            } else { //altrimenti uso per forza la data di creazione dell'invito
                dtCr = DateUtils.dateToString(dtCreazInvito.dateValue());
            }

            String ww =
                "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" + " and (DTCHIUSURACENTRO is null or " +
                " DTCHIUSURACENTRO >= to_date('" + dtCr + "','dd/mm/yyyy'))";

            //se sono un utente legato ad un centro devo poter vedere solo i miei centri
            //piu', al massimo, quello dell'invito
            String in = (String) session.get("elenco_centri");
            if (in != null)
                ww += " and (IDCENTRO in " + in + " OR IDCENTRO=" + fInv.getAttribute("Idcentroprelievo") + ")";
            
            try{
                AccessManager.checkPermission("SOLimiteCentri");
                List<Integer> centriAutorizzati = (List<Integer>) session.get("centriautorizzati");
                String inCentri = "";
                if (centriAutorizzati != null && !centriAutorizzati.isEmpty()){
                    for (int i = 0; i < centriAutorizzati.size(); i++) {
                        inCentri += "" + centriAutorizzati.get(i) + ",";
                    }
                    inCentri = inCentri.substring(0, inCentri.length() - 1);
                    ww += " AND IDCENTRO in (" + inCentri + ") ";
                } else {
                    ww += " AND 1=2 ";
                }           
            } catch (IllegalAccessException e){                                      
               // non faccio niente
            }
            
            voCt.setWhereClause(ww);
            voCt.executeQuery();


            // 4-11-2009 filtro centro richiamo
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

            // filtro tipo invito
            ViewObject voTI = am.findViewObject("Sogg_TipoInvitoView1");
            String strDataApp = null;
            if (dtApp == null) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                strDataApp = df.format(new java.util.Date());
            } else {
                strDataApp = DateUtils.dateToString(dtApp.dateValue());
            }
            voTI.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" +
                                " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" + strDataApp +
                                "', 'DD/MM/YYYY'))");
            voTI.executeQuery();

            // determino se invito ha referto
            boolean refPresente = InvitoUtils.invitoHaReferto(am, idInv, tpscr);
            //  System.out.println("navdett 9");
            session.put("refPresente", Boolean.valueOf(refPresente));

            session.put("invitoPresente", Boolean.valueOf(true));
        } else {
            session.put("invCreato", Boolean.valueOf(false));
            session.put("refPresente", Boolean.valueOf(false));
            session.put("invitoPresente", Boolean.valueOf(false));
        }

        // gestione esclusioni
        ViewObject voEscl = am.findViewObject("Sogg_SoEsclusioneView1");
        String whcl =
            "CODTS = '" + codTs + "' and ULTIMA = 1 and " +
            "(DTANNULL is null) and (TPESCL = 'D' or DTFINE >= trunc(sysdate)) and " + "TPSCR = '" + tpscr +
            "' and ULSS = '" + ulss + "'";
        voEscl.setWhereClause(whcl);
        voEscl.executeQuery();

        Row cEscl = voEscl.first();
        boolean eEscl = (cEscl != null);

        // mauro 15/06/2010 propag escl
        ViewObject voEsclColleg = am.findViewObject("Sogg_EsclusioniCollegateView1");
        voEsclColleg.setWhereClause("1=2");
        voEsclColleg.executeQuery();
        //

        session.put("esisteEscl", Boolean.valueOf(eEscl));
        session.put("esclSalvata", Boolean.valueOf(eEscl));

        if (eEscl) {
            //MOD-fine

            //Integer idEscl = (Integer) cEscl.getAttribute("Idescl");
            String tipo = (String) cEscl.getAttribute("Tpescl");

            SoggUtils.filtraMotEscl(tipo, false);

            session.put("esclAppesa", Boolean.valueOf(false));

            SoggUtils.svuotaInterfacciaPropagazione();
        }

        // per storico
        session.put("stIndex", new Integer(0));

        // carico vo da programma
        //SoggUtils.loadVoDet(ctx);
    }

    public String onEscl() {
        Sogg_ricercaAction.navDett();
        return "goEscl";
    }


    public String onAnag() {
        Sogg_ricercaAction.navDett();
        return "goAnag";
    }

    public String onInvito() {
        try {
            Sogg_ricercaAction.navDett();
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            boolean creaInv = ((Boolean) session.get("creaInv")).booleanValue();

            if (creaInv) {
                return "goNuovoInv";
            } else {
                return "goInvCorr";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossibile accedere all'invito: ", "");
            ctx.addMessage(null, fm);
            return "error";
        }
    }

    /* I00102504 - Inviti fast 
     * Nel caso in cui abbia trovato una configurazione unica, si procede alla creazione dell'invito */
    public String onInvitoFast() {
        try {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            
            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

            Sogg_ricercaAction.navDett();
            
            try {
                String msg = InvitoUtils.creaInvito(false);
                if (msg != null)
                    this.handleMessages(FacesMessage.SEVERITY_INFO, msg);
                
                boolean invitoFastDirect = ((Boolean) session.get("invitoFastDirect")).booleanValue();
                if (invitoFastDirect){
                    Cnf_SoCnfInvitiFastViewRowImpl fastRow = null;
                    Sogg_SoInvitoViewRowImpl nInv = null;
                    
                    DCBindingContainer bindings = (DCBindingContainer) getBindings();
                    DCIteratorBinding voIterFast=bindings.findIteratorBinding("Cnf_SoCnfInvitiFastView1Iterator");
                    
                    //Accedo alla configurazione
                    if (voIterFast!=null && voIterFast.getViewObject()!=null){
                        ViewObject voFast = voIterFast.getViewObject();
                        fastRow = (Cnf_SoCnfInvitiFastViewRowImpl)voFast.first();
                    }
                    
                    //Accedo all'nvito appena creato
                    ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
            
                    if (voInvito!=null)
                        nInv = (Sogg_SoInvitoViewRowImpl)voInvito.getCurrentRow();
                    
                    if (fastRow!=null && nInv!=null){
                                                
                        //Setta la data odierna (se flag dataCorrente = true) o la data impostata nella configurazione
                        oracle.jbo.domain.Number dataCorrente = fastRow.getDataCorrente();
                        if (dataCorrente!=null && dataCorrente.intValue()==1){
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            
                            nInv.setDtapp(DateUtils.getOracleDate(cal));
                        } else
                            nInv.setDtapp(fastRow.getData());
                        
                        //Setto i dati disponibili nella configurazione
                        nInv.setCodesitoinvito(fastRow.getCodesitoinvito());
                        nInv.setLivesito(1);

                        nInv.setIdcentroprelievo(fastRow.getIdcentro());
                        nInv.setIdtpinvito(fastRow.getIdtpinvito());
                        
                        //Setto la variabile per renderizzare correttamente il tasto di conferma
                        oracle.jbo.domain.Number stampaEtichetta = fastRow.getStampaEtichetta();
                        if (stampaEtichetta!=null && stampaEtichetta.intValue()==1)
                            session.put("stampaEtichetta", Boolean.valueOf(true));
                        else 
                            session.put("stampaEtichetta", Boolean.valueOf(false));
                        
                    }
                }
                
            } catch (Exception ex) {
                this.handleException("Impossibile controllare l'eventuale presenza di primi inviti/" +
                                     "accessi spontanei nello stesso round: " + ex.getMessage(), null);
            }
            
            return "goNuovoInv";
            
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossibile accedere all'invito: ", "");
            ctx.addMessage(null, fm);
            return "error";
        }
    }
    
    /* I00102504 - Inviti fast 
     * Nel caso in cui abbia trovato n configurazioni unica, si procede alla creazione dell'invito */
    public String onInvitoFastn() {
        try {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            
            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

            Sogg_ricercaAction.navDett();
            
            try {
                String msg = InvitoUtils.creaInvito(false);
                if (msg != null)
                    this.handleMessages(FacesMessage.SEVERITY_INFO, msg);
                
                Cnf_SoCnfInvitiFastViewRowImpl fastRow = null;
                Sogg_SoInvitoViewRowImpl nInv = null;
                
                DCBindingContainer bindings = (DCBindingContainer) getBindings();
                DCIteratorBinding voIterFast=bindings.findIteratorBinding("Cnf_SoCnfInvitiFastView1Iterator");
                
                //Accedo alla configurazione
                if (voIterFast!=null && voIterFast.getViewObject()!=null){
                    ViewObject voFast = voIterFast.getViewObject();
                    fastRow = (Cnf_SoCnfInvitiFastViewRowImpl)voFast.getCurrentRow();
                }
                
                //Accedo all'invito appena creato
                ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        
                if (voInvito!=null)
                    nInv = (Sogg_SoInvitoViewRowImpl)voInvito.getCurrentRow();
                
                if (fastRow!=null && nInv!=null){
                    
                    //Setta la data odierna (se flag dataCorrente = true) o la data impostata nella configurazione
                    oracle.jbo.domain.Number dataCorrente = fastRow.getDataCorrente();
                    if (dataCorrente!=null && dataCorrente.intValue()==1){
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        
                        nInv.setDtapp(DateUtils.getOracleDate(cal));
                    } else
                        nInv.setDtapp(fastRow.getData());
                    
                    //Setto i dati disponibili nella configurazione
                    nInv.setCodesitoinvito(fastRow.getCodesitoinvito());
                    nInv.setLivesito(1);

                    nInv.setIdcentroprelievo(fastRow.getIdcentro());
                    nInv.setIdtpinvito(fastRow.getIdtpinvito());
                    
                    //Setto la variabile per renderizzare correttamente il tasto di conferma
                    oracle.jbo.domain.Number stampaEtichetta = fastRow.getStampaEtichetta();
                    if (stampaEtichetta!=null && stampaEtichetta.intValue()==1)
                        session.put("stampaEtichetta", Boolean.valueOf(true));
                    else 
                        session.put("stampaEtichetta", Boolean.valueOf(false));
                }
                
            } catch (Exception ex) {
                this.handleException("Impossibile controllare l'eventuale presenza di primi inviti/" +
                                     "accessi spontanei nello stesso round: " + ex.getMessage(), null);
            }
            
            return "goNuovoInv";
            
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossibile accedere all'invito: ", "");
            ctx.addMessage(null, fm);
            return "error";
        }
    }
    
    /* I00102504 - Inviti fast 
     * Apertura dei due pop-up nel caso in cui non trovi configurazioni o ne trovi più di una.
     * Nel primo caso il pop-up consentirà di annullare o di procedere alla creazione standard.
     * Nel secondo caso si potrà scegliere quale configurazione utilizzare */
    public String onInvitoFast(ActionEvent actionEvent) {
        try {

            Map page = ADFContext.getCurrent().getPageFlowScope();
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter=bindings.findIteratorBinding("Cnf_SoCnfInvitiFastView1Iterator");
            
            if (voIter!=null && voIter.getViewObject()!=null){
                ViewObject vo = voIter.getViewObject();
                
                long rowCoun = vo.getEstimatedRowCount();

                /* Se non trova configurazioni, segnala all'utente "nessuna configurazione trovata". 
                 * Se l'utente clicca su Procedi, si apre il Nuovo invito classico, in caso contrario Annulla */
                if (rowCoun<1){
                    page.put("showEsito", "false");
                    
                    if(this.invFast1==null)
                        this.invFast1 = new RichPopup();
                    
                    this.invFast1.show(new RichPopup.PopupHints());
                
                /* Se trova più configurazioni, presenta la lista valori delle configurazioni per l'utente, 
                 * mostrandone la descrizione, il centro, il tipo di invito e la data, nell'ordine indicato in configurazione. 
                 * 
                 * L'utente può proseguire con la configurazione scelta oppure annullare l'operazione. */
                } else  if (rowCoun>1){
                    page.put("showEsito", "true");

                    ADFContext adfCtx = ADFContext.getCurrent();
                    Map session = adfCtx.getSessionScope();
                    
                    voIter=bindings.findIteratorBinding("Cnf_SoCnfInvitiFastView1Iterator");

                    //Filtro i vo
                    String ulss = (String) session.get("ulss");
                    String tpscr = (String) session.get("scr");
                    
                    String where = "ulss = '"+ ulss +"' and tpscr='" + tpscr + "'";
                    
                    voIter = bindings.findIteratorBinding("Sogg_CprelInvitoView1Iterator");
                    vo = voIter.getViewObject();
                    if (vo != null) {
                        vo.setWhereClause(where);
                        vo.executeQuery();
                    }
                    
                    voIter = bindings.findIteratorBinding("Sogg_TipoInvitoView1Iterator");
                    vo = voIter.getViewObject();
                    if (vo != null) {
                        vo.setWhereClause(where);
                        vo.executeQuery();
                    }
                    
                    if(this.invFast2==null)
                        this.invFast2 = new RichPopup();
                    
                    this.invFast2.show(new RichPopup.PopupHints());                
                }
            }
            
            return "";
            
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossibile proseguire nella generazione dell'invito: ", "");
            ctx.addMessage(null, fm);
            return "";
        }
    }

    public String onStoria() {
        Sogg_ricercaAction.navDett();
        return "goStoria";
    }


    public String onReimp() {
        @SuppressWarnings("deprecation")
        Sogg_RicParam bean =
            (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
        bean.resetCampi();
        
        return "reimp";
    }
    
    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.remove("FROM_AUR");

        SoggUtils.beforeNavSogg(dest, true);
    }

    public void setPanelSearch(RichPanelHeader panelSearch) {
        this.panelSearch = panelSearch;
    }

    public RichPanelHeader getPanelSearch() {
        if (panelSearch == null)
            findForward();
        return panelSearch;
    }
    
    protected void findForward() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_RicercaView1Iterator");
        ViewObject vo = voIter.getViewObject();
        if (vo.getEstimatedRowCount() > 0 && vo.getCurrentRow() != null) {
            if (this.soggTable == null)
                this.soggTable = new RichTable();

            Row _row = vo.getCurrentRow();
            RowKeySet selected = new RowKeySetImpl();
            selected.add(Arrays.asList(_row.getKey()));
            this.soggTable.setSelectedRowKeys(selected);
            Utility.gotoTablePageOfSelectedRow(voIter, this.soggTable);
        }
        
    }

    @SuppressWarnings("unused")
    public void lovComuneReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(codiceComune);
        RequestContext.getCurrentInstance().addPartialTarget(descComune);
        RequestContext.getCurrentInstance().addPartialTarget(idDistr);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', false)");
    }

    public void setCodiceComune(RichInputText codiceComune) {
        this.codiceComune = codiceComune;
    }

    public RichInputText getCodiceComune() {
        return codiceComune;
    }

    public void setDescComune(RichInputText descComune) {
        this.descComune = descComune;
    }

    public RichInputText getDescComune() {
        return descComune;
    }

    public void setIdDistr(RichSelectOneChoice idDistr) {
        this.idDistr = idDistr;
    }

    public RichSelectOneChoice getIdDistr() {
        return idDistr;
    }

    public void setCodMed(RichInputText codMed) {
        this.codMed = codMed;
    }

    public RichInputText getCodMed() {
        return codMed;
    }

    public void setDesMedico(RichInputText desMedico) {
        this.desMedico = desMedico;
    }

    public RichInputText getDesMedico() {
        return desMedico;
    }

    @SuppressWarnings("unused")
    public void lovMedicoReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(codMed);
        RequestContext.getCurrentInstance().addPartialTarget(desMedico);
    }

    @SuppressWarnings("unchecked")
    public void onChineta(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        boolean isIneta = (Boolean)valueChangeEvent.getNewValue();
        session.put("inEta", Boolean.valueOf(isIneta));
    }

    @Override
    protected void setAppModule()
      {
        this.amName="Sogg_AppModule";
      }

    @SuppressWarnings("unchecked")
    public void selectSoggListener(SelectionEvent selectionEvent) {
        Utility.invokeEL("#{bindings.Sogg_RicercaView1.collectionModel.makeCurrent}", new Class[] {SelectionEvent.class},
                                 new Object[] { selectionEvent });
    
        checkSelected();
    }

    @SuppressWarnings("unchecked")
    public static void checkSelected(){
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding voSoggRicerca = bindings.findIteratorBinding("Sogg_RicercaView1Iterator");
        Row selectedRow = voSoggRicerca.getCurrentRow();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        if (selectedRow != null){
            Integer invPres = (Integer)selectedRow.getAttribute("Invpres");
            boolean eInv = (invPres != null && invPres.equals(1));
            session.put("creaInv", !eInv);
            
            if (selectedRow.getAttribute("Escl")==null || "".equals(selectedRow.getAttribute("Escl")))
                session.put("creaInvFast", true);
            else 
                session.put("creaInvFast", false);
            
        } else {
            session.put("creaInv", false);
            session.put("creaInvFast", false);
        }
    }

    @SuppressWarnings("unchecked")
    public void setParameterAndCallback(ClientEvent ce){
        ADFContext adfCtx = ADFContext.getCurrent();
        Map _m = ce.getParameters();
        System.out.println("---------------------> " + _m);
        adfCtx.getViewScope().putAll(_m);

        String _cb = null;
        if(_m.containsKey("callbackFunction")){
            try{
                _cb = _m.get("callbackFunction") != null ? _m.get("callbackFunction").toString() : null;
            }catch(Throwable th){
                _cb = null;
            }
        }
        
        if(_cb!=null)
            Utility.addScriptOnPartialRequest(_cb);
    }

    public void onChangeCodClassePop(ValueChangeEvent valueChangeEvent) {
        String codClassePop = "";
        
        if (valueChangeEvent!=null)
            codClassePop = (String) valueChangeEvent.getNewValue(); 
        
        if (codClassePop!=null && !"".equals(codClassePop)){
            PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
            bean.setCodClassePop(codClassePop);
        }
    }

    public void setInvFast1(RichPopup invFast1) {
        this.invFast1 = invFast1;
    }

    public RichPopup getInvFast1() {
        return invFast1;
    }

    public void setInvFast2(RichPopup invFast2) {
        this.invFast2 = invFast2;
    }

    public RichPopup getInvFast2() {
        return invFast2;
    }
}
