
package view.backing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import model.Print_AppModuleImpl;
import model.RefCa_AppModule;
import model.Sec_AppModuleImpl;

import model.common.Cnf_AppModule;
import model.common.Cnf_SoCnfFunzViewRow;
import model.common.ImpExp_AppModule;
import model.common.RefCo_AppModule;
import model.common.RefMa_AppModule;
import model.common.Ref_AppModule;
import model.common.Round_AppModule;
import model.common.Sogg_AppModule;
import model.common.Stats_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ConfigurationReader;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;

import model.datacontrol.ImpexpBean;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import model.sicurezze.Sec_SecCnfUsersRolesViewRowImpl;
import model.sicurezze.Sec_SecCnfUsersViewImpl;
import model.sicurezze.Sec_SecCnfUsersViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.jbo.Row;
import oracle.jbo.RowIterator;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.uicli.binding.JUCtrlListBinding;

import view.commons.AccUtils;

public class InitializeActionBean {

    private String username;
    private boolean citoOK = false;
    private boolean mammoOK = false;
    private boolean colonOK = false;
    private boolean cardioOK = false;
    private boolean pfasOK = false;
    private Sec_SecCnfUsersViewRowImpl currentUser;
    private String selectedUlss = null;
    private boolean accessoRegionale = false;
    private static final ADFLogger logger = ADFLogger.createADFLogger(InitializeActionBean.class);
    
    private String recapitoHd = "";
    private RichPopup invitoFastPopUp;

    public InitializeActionBean() {
    }

    public String init() {
        //INIZIALIZZAZIONE DELL'APPLICAZIONE
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sessionMap = adfCtx.getSessionScope();
        //utente
        sessionMap.put("user", this.username);

        String ulss = this.selectedUlss;
        String tpscr = null;
        boolean go_on = false;
        try {
            if (selectedUlss != null) {
                sessionMap.put("ulss", ulss);
                if (AccessManager.CODREGIONALE.equals(selectedUlss)) {
                    this.setAccessoRegionale(true);
                } else {
                    this.setAccessoRegionale(false);
                }
            } else {
                this.handleMessage(FacesMessage.SEVERITY_ERROR, "Scegliere un'azienda su cui lavorare!");
                return "error";
            }

            Integer accessi = (Integer) sessionMap.get("accessi");
            if (accessi == null) {
                sessionMap.put("accessi", new Integer(1));
            } else {
                this.handleMessage(FacesMessage.SEVERITY_ERROR,
                                   "Attenzione, il programma ha invalidato la sessione perche' gia' in uso su altra pagina del browser. " +
                                   "Per continuare ad operare e' NECESSARIO chiudere tutte le pagine del browser e rilanciare l'applicazione.");
                return "error";
            }

            //lettura del tipo di screening
            tpscr = (String) sessionMap.get("tpscreening");

            if (tpscr == null)
                throw new Exception("L'utente non è abilitato ad accedere ai programmi di screening");
            sessionMap.put("scr", tpscr);

            //ripulisco i dati di sessione non usati
            sessionMap.remove("tpscreening");

            //lettura della ulss ed impostazione della regionalità
            sessionMap.put("regionale", Boolean.valueOf(this.isAccessoRegionale()));

            //lettura parametro di abilitazione per il journaling
            String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
            if (flagAbilJournal == null || flagAbilJournal.equals("") || flagAbilJournal.equals("0")) {
                sessionMap.put("flagAbilJournal", Boolean.FALSE);
            } else {
                sessionMap.put("flagAbilJournal", Boolean.TRUE);
            }

            //ottengo il vettore dei permessi per lo specifico screening
            this.analyzePermissions(currentUser, tpscr, ulss);

            go_on = true;

            ParametriSistema.init();

            String impAnagManuale = ConfigurationReader.readProperty("impAnagManuale");
            if (impAnagManuale == null || impAnagManuale.equals("") || impAnagManuale.equals("0")) {
                sessionMap.put("impAnagManuale", Boolean.FALSE);
            } else {
                sessionMap.put("impAnagManuale", Boolean.TRUE);
            }

            // se l'azienda non ha attivato il doppio cieco tolgo l'autorizzazione all'utente
            if (!((Boolean) sessionMap.get("doppio_cieco")).booleanValue()) {
                sessionMap.put("SODoppioCieco", Boolean.valueOf(false));
            }

            //se l'azienda lavora per centri devo cerificare
            /*if (((Boolean)sessionMap.get("modalita_centri")).booleanValue()) {
				String user = (String)sessionMap.get("user");
				
				//verifico se l'utente è legato ad un centro oppure no
				Integer centro_f = chooseCenter(ulss, tpscr, user);
				
				//se lo è ma deve ancora scegliere quale centro rimando i filtri
				if (centro_f != null && centro_f.intValue() < 0)
					go_on = false;
			}*/
        } catch (Exception e) {
            logger.severe("Eccezione in InitializeActionBean.init()", e);
        }

        try {
            //solo se l'assegnazione risorse ha avuto successo proseguo
            if (go_on) {
                logger.fine("L'assegnazione risorse ha avuto successo");
                
                //controllo se l'utente ha delle limitazioni sui centri
                try{
                    AccessManager.checkPermission("SOLimiteCentri");
                    filterUserCenter(ulss, tpscr, currentUser);
                } catch (IllegalAccessException e){                              
                    //non deve fare niente
                }
                
                this.initialFiltering(ulss, tpscr, this.accessoRegionale);

                //controllo delle funzionalità non abilitate
                this.checkDisabledFunctions();

                //leggo se ci sono comunicazioni sui round in scadenza (solo se sono utente di una ulss particolare)
                if (!this.accessoRegionale)
                    adfCtx.getPageFlowScope().put("info_round", this.getInfoRound());

                // Consento allo scheduler di utilizzare gli application module di ADF
                FacesContext ctx = FacesContext.getCurrentInstance();
                ServletContext servletContext = (ServletContext) ctx.getExternalContext().getContext();
                servletContext.setAttribute("adf_ready", Boolean.TRUE);
                
                //I00094671
                sessionMap.put("recapitoHd", this.recapitoHd);
                
                Sogg_AppModule am =
                    (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
                ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
                String linkNoteOperative = null;
                
                try {
                    linkNoteOperative = ParametriSistema.getParamValue(voParam, ulss, tpscr, "link_note_operative");
                } catch (Exception e) {
                    logger.info("Parametro link_note_operative non definito");
                }

                if (linkNoteOperative != null) 
                    sessionMap.put("linkNoteOperative", linkNoteOperative);
                
                //BindingContext ctx = BindingContext.getCurrent();
                Sec_AppModuleImpl secam = 
                    (Sec_AppModuleImpl) BindingContext.getCurrent().findDataControl("Sec_AppModuleDataControl").getDataProvider();
                
                ViewObject voWarning = secam.findViewObject("Sec_SoWarningView1");
                
                String where = "Sec_SoWarning.APPLICATIVO = 'SCREENING' " +
                    "AND Sec_SoWarning.ULSS = '" + ulss + "' AND Sec_SoWarning.TPSCR = '"+tpscr+"' " +
                    //"AND (sec_sowarning.iniziovalidita is null or TRUNC(sec_sowarning.iniziovalidita) <=  TRUNC(sysdate)) " +
                    //"AND (sec_sowarning.finevalidita   is null or TRUNC(sec_sowarning.finevalidita)   > TRUNC(sysdate))";
                    "AND (sec_sowarning.iniziovalidita is null or sec_sowarning.iniziovalidita < sysdate) " +
                    "AND (sec_sowarning.finevalidita   is null or sec_sowarning.finevalidita   > sysdate)";
                
                voWarning.setWhereClause(where);
                voWarning.executeQuery();
                //I00094671

                //I00102504 - Inviti Fast (apertura popup)
                Boolean sOInvitoFast = false;
                if (sessionMap.get("SOInvitoFast")!=null)
                    sOInvitoFast = (Boolean) sessionMap.get("SOInvitoFast");
                
                if (sOInvitoFast){    
                    if(this.invitoFastPopUp==null)
                        this.invitoFastPopUp = new RichPopup();
                    
                    this.invitoFastPopUp.show(new RichPopup.PopupHints());
                    logger.info("Scelta configurazione inviti fast");
                    
                    return "";
                }

                logger.finest("init() returns \"home\"");
                return "home";

            } else {
                logger.info("L'assegnazione risorse non ha avuto successo");
                return "to_centerchoice";
            }
        } catch (Exception e) {
            logger.severe("Eccezione in InitializeActionBean.init()", e);
        }
        
        return null;
    }

    public static void analyzePermissions(Sec_SecCnfUsersViewRowImpl user, String tpscr, String ulss) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sessionMap = adfCtx.getSessionScope();
        RowIterator roles = user.getSec_SecCnfUsersRolesView();
        logger.info("Utente: " + user.getCognome());
        while (roles.hasNext()) {
            Sec_SecCnfUsersRolesViewRowImpl role = (Sec_SecCnfUsersRolesViewRowImpl) roles.next();
            if (tpscr.equals(role.getTpscr()) && ulss.equals(role.getUlss())) {
                logger.info("Ruolo: " + role.getAttribute("RoleKey"));
                RowIterator auths = role.getSec_SecCnfRolesAuthView();
                while (auths.hasNext()) {
                    Row r = auths.next();
                    sessionMap.put(r.getAttribute("SoAuthorizationId"), Boolean.TRUE);
                }
            }
        }
    }

    public static Integer chooseCenter(String ulss, String tpscr, String user) {
        BindingContext ctx = BindingContext.getCurrent();
        DCBindingContainer bindings = (DCBindingContainer) ctx.getCurrentBindingsEntry();
        JUCtrlListBinding utentiCentriList = (JUCtrlListBinding) bindings.get("Cnf_SoUtentiCentriView1");
        ViewObject vo = utentiCentriList.getViewObject();
        vo.setWhereClause("Cnf_SoUtentiCentri.TPSCR='" + tpscr + "' AND Cnf_SoUtentiCentri.ULSS='" + ulss +
                          "' AND Cnf_SoUtentiCentri.UTENTE='" + user.replaceAll("'", "''") + "'");
        vo.executeQuery();

        int n = vo.getRowCount();

        if (n == 1) { //l'utente è associato ad un solo centro fisico, lo imposto automaticamnete
            ADFContext adfCtx = ADFContext.getCurrent();
            Map ss = adfCtx.getSessionScope();
            Integer id = (Integer) vo.first().getAttribute("Idcentro");
            ss.put("centro_f", id);

            Integer c1 = (Integer) vo.first().getAttribute("Idcentro1liv");
            Integer c2 = (Integer) vo.first().getAttribute("Idcentro2liv");
            String in = null;   

            if (c1 != null || c2 != null) {
                in = "(";
                if (c1 != null)
                    in += c1;
                if (c1 != null && c2 != null)
                    in += ",";
                if (c2 != null)
                    in += c2;

                in += ")";

                ss.put("elenco_centri", in);
                ss.put("centro1liv", c1);
                ss.put("centro2liv", c2);
            }

            return id;

        } else if (n > 1) {
            //l'utente è associato a più centri, deve scegliere con quale entrare
            //lo redirigo all apagina di scelta
            return new Integer(-1);
        }

        //l'utente non è associato ad alcun centro, quindi ha diritto ad accedere senza restrizioni
        return null;

    }
    
    public static Integer filterUserCenter(String ulss, String tpscr, Sec_SecCnfUsersViewRowImpl user) {
        BindingContext ctx = BindingContext.getCurrent();
        Sec_AppModuleImpl secam = (Sec_AppModuleImpl) ctx.findDataControl("Sec_AppModuleDataControl").getDataProvider();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        ViewObject userCentriVo = secam.getSec_SoCnfUsersCentriView1();
        userCentriVo.setWhereClause("SO_USER_ID = "+user.getSoUserId()+" AND ULSS = '"+ulss+"' AND TPSCR = '"+tpscr+"' ");
        userCentriVo.executeQuery();
        RowSetIterator rsI = userCentriVo.createRowSetIterator(null); 
        List<Integer> centriList = new ArrayList();
        while(rsI.hasNext()){
            Row r = rsI.next();
            Integer id = ((Number)r.getAttribute("Idcentro")).intValue();
            centriList.add(id);
        }
        rsI.closeRowSetIterator();
        session.put("centriautorizzati",centriList);
        return null;

    }

    public static void initialFiltering(String ulss, String tpscr, boolean accessoRegionale) throws Exception {

        ADFContext adfCtx = ADFContext.getCurrent();
        Map ss = adfCtx.getSessionScope();
        String elenco_in = (String) ss.get("elenco_centri");
        Integer centro_f = (Integer) ss.get("centro_f");
        Integer idTrialAttr = (Integer) ss.get("trial");
        Integer idTrial = null;
        try {
            idTrial = new Integer(idTrialAttr);
        } catch (Exception e) {
            idTrial = new Integer(0);
        }
        
        //filtro i viewObject in base ai permessi dell'utente
        BindingContext ctx = BindingContext.getCurrent();
        
        Cnf_AppModule am = (Cnf_AppModule) ctx.findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        am.filterConfiguration(ulss, tpscr, AccessManager.CODREGIONALE, accessoRegionale, elenco_in, centro_f!=null?centro_f.intValue():null);

        Round_AppModule ram = (Round_AppModule) ctx.findDataControl("Round_AppModuleDataControl").getDataProvider();
        ram.filterConfiguration(ulss, tpscr, AccessManager.CODREGIONALE, accessoRegionale);

        Print_AppModuleImpl pam = (Print_AppModuleImpl)ctx.findDataControl("Print_AppModuleDataControl").getDataProvider();
        pam.filterConfiguration(ulss, tpscr, AccessManager.CODREGIONALE, accessoRegionale,elenco_in, centro_f, idTrial);

        ImpExp_AppModule ieam = (ImpExp_AppModule) ctx.findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ieam.filterConfiguration(ulss, tpscr, accessoRegionale);

        if ("CI".equals(tpscr)) {
            Ref_AppModule ream = (Ref_AppModule)ctx.findDataControl("Ref_AppModuleDataControl").getDataProvider();
            ream.filterConfiguration(ulss, tpscr, AccessManager.CODREGIONALE, accessoRegionale, elenco_in, centro_f, idTrial);
        } else if ("CO".equals(tpscr)) {
            RefCo_AppModule ream = (RefCo_AppModule)ctx.findDataControl("RefCo_AppModuleDataControl").getDataProvider();
            ream.filterConfiguration(ulss, tpscr, AccessManager.CODREGIONALE, accessoRegionale);
        } else if ("MA".equals(tpscr)) {
            RefMa_AppModule ream = (RefMa_AppModule)ctx.findDataControl("RefMa_AppModuleDataControl").getDataProvider();
            ream.filterConfiguration(ulss, tpscr, AccessManager.CODREGIONALE, accessoRegionale);
        } else if ("CA".equals(tpscr)) {
            RefCa_AppModule ream =
                (RefCa_AppModule) ctx.findDataControl("RefCa_AppModuleDataControl").getDataProvider();
            ream.filterConfiguration(ulss, tpscr, AccessManager.CODREGIONALE, accessoRegionale);
        } else if ("PF".equals(tpscr)) {

        } else
            throw new Exception("Tipo di screening non riconosciuto");

        //18122013 Gaion: controllo se l'import referti colon 2 liv e' via hl7
        if ("CO".equals(tpscr)) {
                ViewObject cnfVo = ieam.findViewObject("Impexp_SoCnfImpexpView1");
                String wh = "IMPEXP='" + ConfigurationConstants.IMPEXP_IMP +
                        "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'" +
                        " AND TPDIP='" + ConfigurationConstants.TPDIP_ENDO2 + "' ";
                cnfVo.setWhereClause(wh);
                cnfVo.executeQuery();
                Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow)cnfVo.first();
                if (r != null && r.getModalita().equals(ConfigurationConstants.MODALITA_PDD)) {
                        ss.put("interm2liv",Boolean.valueOf(true)); 
                }
        }
        
        Stats_AppModule stam=(Stats_AppModule)ctx.findDataControl("Stats_AppModuleDataControl").getDataProvider();
        stam.filterConfiguration(ulss, tpscr, AccessManager.CODREGIONALE, accessoRegionale, elenco_in, centro_f);
        
        Sec_AppModuleImpl secam = (Sec_AppModuleImpl) ctx.findDataControl("Sec_AppModuleDataControl").getDataProvider();
        secam.filterConfiguration(ulss, accessoRegionale);



        //mod20071115
        SoggUtils.filterSogg(ulss, tpscr);

        //mod20081117
        AccUtils.filterAcc(ulss, tpscr);
    }

    /**
     * Metodo che controlla quali funzionalità di import-export sono
     * disabilitate e disabilita la relativa autorizzzione come
     * conseguenza
     */
    public static void checkDisabledFunctions() {
        BindingContext ctx = BindingContext.getCurrent();
        Cnf_AppModule am = (Cnf_AppModule) ctx.findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Cnf_SoCnfFunzView1");
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        //se non ho inserito i dati per la ulss disabilito tutto
        if (vo.getRowCount() == 0) {
            session.put("SOImportAnagrafe", Boolean.FALSE);
            session.put("SOImportReferti", Boolean.FALSE);
            session.put("SOExportAppuntamenti", Boolean.FALSE);
            session.put("SOExportAccettazioni", Boolean.FALSE);
            session.put("SOImportEsclusioni", Boolean.FALSE);
            session.put("SOImportPresenze", Boolean.FALSE);
            //non è una vera autorizzazione, sevre solo per abilitare/disabilitare
            //in base alle funzionalità della ulss
            session.put("SOExportCancellazioni", Boolean.FALSE);
            session.put("SOPostel", Boolean.FALSE);
            session.put("n_referti_1liv", new Integer(1));
        } else //altrimenti l'unica riga selezioata è quella da considerare
        {
            Cnf_SoCnfFunzViewRow r = (Cnf_SoCnfFunzViewRow) vo.first();

            //se una funzionalità non risulta implementata l'autoizzazione diventa FALSE
            //altrimenti dipende dai permesi dell'utente, quindi  non viene toccata
            if (!ConfigurationConstants.DB_TRUE.equals(r.getImpanag()))
                    session.put("SOImportAnagrafe", Boolean.FALSE);
            if (!ConfigurationConstants.DB_TRUE.equals(r.getImpref()))
                    session.put("SOImportReferti", Boolean.FALSE);
            if (!ConfigurationConstants.DB_TRUE.equals(r.getImpacc()))
                    session.put("SOImportPresenze", Boolean.FALSE);
            if (!ConfigurationConstants.DB_TRUE.equals(r.getImpescl()))
                    session.put("SOImportEsclusioni", Boolean.FALSE);
            
            boolean setacc = false;
            if (!ConfigurationConstants.DB_TRUE.equals(r.getExpapp())) {
                session.put("SOExportAppuntamenti", Boolean.FALSE);
                //non ho appuntamenti, venetulamnete posso esportare slo accettazioni
                setacc = true;
            } else //le cancellazioni sono una sotto-autorizzazione degli appuntamenti
            {
                //non è una vera autorizzazione, sevre solo per abilitare/disabilitare
                //in base alle funzionalità della ulss
                if (!ConfigurationConstants.DB_TRUE.equals(r.getExpcanc()))
                    session.put("SOExportCancellazioni", Boolean.FALSE);
                else
                    session.put("SOExportCancellazioni", Boolean.TRUE);
            }
            if (!ConfigurationConstants.DB_TRUE.equals(r.getPostel()))
                session.put("SOPostel", Boolean.FALSE);
            if (!ConfigurationConstants.DB_TRUE.equals(r.getExpacc()))
                session.put("SOExportAccettazioni", Boolean.FALSE);
            else
            //se posso esportare solo accettazioni...
            if (setacc) {
                ImpexpBean bean=(ImpexpBean)BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();
                bean.setOp_type(1);
            }

            //non sono autorizzzioni, quindi serve impostarli da 0

            //imposto il nuemro di referti
            if (r.getNReferti1liv() != null) {
                session.put("n_referti_1liv", r.getNReferti1liv());
            } else {
                session.put("n_referti_1liv", new Integer(1));
            }

        }
    }

    public static Boolean getInfoRound() {
        BindingContext ctx = BindingContext.getCurrent();
        Round_AppModule ram = (Round_AppModule) ctx.findDataControl("Round_AppModuleDataControl").getDataProvider();
        //controllo l'esistenza di comuni col round in scadenza
        ViewObject vo = ram.findViewObject("Round_SoRoundInScadenzaView1");
        if (vo.first() != null)
            return Boolean.TRUE;

        //controllo l'esistenza di comuni privi di round attivi
        vo = ram.findViewObject("Round_SoComuniSenzaRound1");
        if (vo.first() != null)
            return Boolean.TRUE;

        return Boolean.FALSE;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        if ((username == null) || ("".equals(username))) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();

            if (request.getUserPrincipal() == null) {
                username = request.getParameter("username");
            } else {
                username = request.getUserPrincipal().getName();
            }

            if (username != null) {

                this.loadAuthorizations();
            } else {
                this.handleMessage(FacesMessage.SEVERITY_ERROR, "Utente non riconosciuto.");
            }
        }

        return username;
    }

    private boolean loadAuthorizations() {
        
        //parametro screening covid
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sessionMap = adfCtx.getSessionScope();
        String screeningCovid = ConfigurationReader.readProperty("screenCovid19");
        
        if (screeningCovid != null && screeningCovid.equals("1")) {
            sessionMap.put("covid19", Boolean.TRUE);
        } else {
            sessionMap.put("covid19", Boolean.FALSE);
        }

        BindingContext ctx = BindingContext.getCurrent();
        Sec_AppModuleImpl secam = (Sec_AppModuleImpl) ctx.findDataControl("Sec_AppModuleDataControl").getDataProvider();
        
        //ViewObject voWarning = secam.findViewObject("Sec_SoWarningView1");
        
        //String where = "Sec_SoWarning.APPLICATIVO = 'SCREENING'";
        //voWarning.setWhereClause(where);
        //voWarning.executeQuery();
        
        Sec_SecCnfUsersViewImpl userVo = secam.getUserInfo();
        userVo.setNamedWhereClauseParam("UsernameVar", username);
        userVo.executeQuery();
        if (userVo.getEstimatedRowCount() == 1) {
            Sec_SecCnfUsersViewRowImpl userRow = (Sec_SecCnfUsersViewRowImpl) userVo.first();
            if (userRow.getIsEnabled().booleanValue()) {
                Date dtValid = userRow.getDtfineValidita();
                if (dtValid == null || dtValid.compareTo(new Date()) > 0) {
                    this.currentUser = userRow;
                    RowIterator ulssIter = userRow.getSec_SecCnfUsersUlssView();
                    if (ulssIter.getRowCount() > 0) {

                    } else {
                        //nessuna ulss abilitata
                        this.handleMessage(FacesMessage.SEVERITY_ERROR,
                                           "Impossibile accedere. L'utente non è' abilitato su nessuna azienda.");
                        return false;
                    }
                    RowIterator tpscrIter = userRow.getSec_SecCnfUsersTpscrView();
                    while (tpscrIter.hasNext()) {
                        String tpscrI = (String) tpscrIter.next().getAttribute("Tpscr");
                        if (("CI").equals(tpscrI)) {
                            citoOK = true;
                            continue;
                        }
                        if (("MA").equals(tpscrI)) {
                            mammoOK = true;
                            continue;
                        }
                        if (("CO").equals(tpscrI)) {
                            colonOK = true;
                            continue;
                        }
                        if (("CA").equals(tpscrI)) {
                            cardioOK = true;
                            continue;
                        }
                        if (("PF").equals(tpscrI)) {
                            pfasOK = true;
                            continue;
                        }
                    }
                    return true;
                } else {
                    //utente scaduto
                    this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile accedere. L'account è scaduto.");
                    return false;
                }
            } else {
                //utente non abilitato
                this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile accedere. L'utente non e' abilitato.");
                return false;
            }
        } else { //utente non trovato o username non univoco
            this.handleMessage(FacesMessage.SEVERITY_ERROR,
                               "Impossibile accedere. L'utente non ha nessun account per l'applicativo.");
            return false;
        }
    }

    public void setSelectedUlss(String selectedUlss) {
        this.selectedUlss = selectedUlss;
    }

    public String getSelectedUlss() {
        if (this.currentUser != null && this.currentUser.getSec_SecCnfUsersUlssView() != null &&
            this.currentUser.getSec_SecCnfUsersUlssView().getRowCount() == 1) {
            try {
                selectedUlss = (String) this.currentUser.getSec_SecCnfUsersUlssView().first().getAttribute("Ulss");
            } catch (Exception e) {
                this.handleMessage(FacesMessage.SEVERITY_ERROR,
                                   "Si è verificato un errore. Impossibile recuperare l'azienda su cui e' abilitato l'utente.");
                return null;
            }
        }
        return selectedUlss;
    }

    private void handleMessage(FacesMessage.Severity severity, String msg) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        FacesMessage fm = new FacesMessage(severity, msg, "");
        ctx.addMessage(null, fm);
    }

    public void setMammoOK(boolean mammoOK) {
        this.mammoOK = mammoOK;
    }

    public boolean isMammoOK() {
        return mammoOK;
    }

    public void setCurrentUser(Sec_SecCnfUsersViewRowImpl currentUser) {
        this.currentUser = currentUser;
    }

    public Sec_SecCnfUsersViewRowImpl getCurrentUser() {
        return currentUser;
    }

    public void setAccessoRegionale(boolean accessoRegionale) {
        this.accessoRegionale = accessoRegionale;
    }

    public boolean isAccessoRegionale() {
        return accessoRegionale;
    }

    public void setCitoOK(boolean citoOK) {
        this.citoOK = citoOK;
    }

    public boolean isCitoOK() {
        return citoOK;
    }

    public void setColonOK(boolean colonOK) {
        this.colonOK = colonOK;
    }

    public boolean isColonOK() {
        return colonOK;
    }

    public void setCardioOK(boolean cardioOK) {
        this.cardioOK = cardioOK;
    }

    public boolean isCardioOK() {
        return cardioOK;
    }

    public void setPfasOK(boolean pfasOK) {
        this.pfasOK = pfasOK;
    }

    public boolean isPfasOK() {
        return pfasOK;
    }
    
    //I00094671
    public void setRecapitoHd(String recapitoHd) {
        this.recapitoHd = recapitoHd;
    }
    
    //I00094671
    public String getRecapitoHd() {
        if ("".equals(recapitoHd)) {
            
            String recapito_dh = ConfigurationReader.readProperty("recapito_hd");
            
            if (recapito_dh != null) {
                this.recapitoHd = recapito_dh;
            }
        }

        return this.recapitoHd;
    }

    public void setInvitoFastPopUp(RichPopup invitoFastPopUp) {
        this.invitoFastPopUp = invitoFastPopUp;
    }

    public RichPopup getInvitoFastPopUp() {
        return invitoFastPopUp;
    }

}
