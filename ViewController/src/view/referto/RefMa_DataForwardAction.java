package view.referto;

import insiel.dataHandling.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.AccMa_AppModule;
import model.common.Cnf_AppModule;
import model.common.Parent_AppModule;
import model.common.RefMa_AppModule;
import model.common.Ref_SoInterventomammoViewRow;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.ViewHelper;

import model.datacontrol.AccMa_RicParam;
import model.datacontrol.Ref_2livBean;
import model.datacontrol.Ref_SearchBean;
import model.datacontrol.Sogg_RicParam;

import model.referto.Ref_SoRefertomammo2livViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.context.RequestContext;

import view.commons.AccUtils;
import view.commons.action.Parent_DataForwardAction;

import view.reports.SystemReport;

import view.util.Utility;

public abstract class RefMa_DataForwardAction extends Parent_DataForwardAction {
    private ADFLogger logger = ADFLogger.createADFLogger(this.getClass().getName());

    protected void setAppModule() {
        this.amName = "RefMa_AppModule";
    }


    public void onApply(ActionEvent actionEvent) {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        try {
            if (livello == null)
                throw new Exception("Nessun referto da salvare");

            // Lavoro peculiare alla singola form.
            this.beforeSavingReferto();
            //09-01-2013 sara: aggiunto journaling
            //journaling
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) sess.get("user");
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine sara
            am.getTransaction().commit();

            afterSave();

            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiMAView1");
            String voName;
            if (livello.intValue() == 2)
                // Da sistemare.
                voName = "Ref_SoRefertomammo2livView1";
            else
                voName = "Ref_SoRefertomammo1livView1";

            ViewObject ref = am.findViewObject(voName);
            Row r = ref.getCurrentRow();

            // mauro 30/11/2010, modifica per assicurare aggiornamento esito invito
            Integer idInvito = (Integer) r.getAttribute("Idinvito");
            GestoreReferti.updateEsito(am, idInvito);
            //

            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);

            /*MOD 19/06/2007
			r=vo.getCurrentRow();
			if(r!=null)
			ViewHelper.restoreCurrentRow(vo,"Idinvito",r.getAttribute("Idinvito"));
			*/

            ViewHelper.queryAndRestoreCurrentRow(vo);
            this.loadFromDB();
        } catch (Exception ex) {
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
        }
    }

    /**
     * Salva e torna alla pagina di inizio
     * @param ctx
     */
    public String onConfirm() {
        this.onApply(null);
        return "to_refMa_ricerca";
    }


    /**
     * Prima di un eventuale salvataggio automatico salvo io a modo mio
     * (per mantenere la consistenza dei dati tra interfaccia e DB)
     * @param ctx
     */
    protected boolean beforeSave() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        try {

            if (livello == null)
                throw new Exception("Nessun referto da salvare");

            String voName;
            if (livello.intValue() == 2)
                voName = "Ref_SoRefertomammo2livView1";
            else
                voName = "Ref_SoRefertomammo1livView1";

            ViewObject ref = am.findViewObject(voName);
            Row r = ref.getCurrentRow();
            if (r == null)
                return true;

            if (r != null && r.getAttribute("Completo") != null &&
                ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
                throw new Exception("Referto gia' chiuso, le modifiche sono state annullate");

            //trattamento peculiare alla singola pagina
            this.beforeSavingReferto();
            return true;
        } catch (Exception ex) {
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
            return false;
        }
    }

    /**
     * Creazione di un nuovo referto
     * @param ctx
     */
    public void onCreateReferto(ActionEvent actionEvent) {

        try {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            //viewObject dipendente in base al valore di idinvito (che viene impostato in automatico)
            Integer livello = (Integer) session.get("ref_livello");


            if (livello == null)
                throw new Exception("nessun livello impostato per il referto da creare");

            String voName;
            if (livello.intValue() == 1) {
                //PRIMO LIVELLO
                voName = "Ref_SoRefertomammo1livView1";

            } else {
                //SECONDO LIVELLO
                voName = "Ref_SoRefertomammo2livView1";
            }

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding(voName + "Iterator");
            ViewObject ref = voIter.getViewObject();
            //se il referto c'e' gia' non posso crearlo
            if (ref.getRowCount() > 0)
                throw new Exception("Esiste gia' un referto per l'invito selezionato)");

            //altrimenti lo devo creare
            RefMa_AppModule am = (RefMa_AppModule) ref.getApplicationModule();
            try {
                ViewObject invito = am.findViewObject("Ref_SoInvitoView2");
                Row inv = invito.first();
                if (inv == null)
                    throw new Exception("Invito non trovato");
                GestoreReferti gr = new GestoreReferti(am);

                if (inv.getAttribute("Dtapp") == null)
                    throw new Exception("La data dell'invito non risulta valorizzata");
                if (inv.getAttribute("Idcentroprelievo") == null)
                    throw new Exception("Il centro di prelievo dell'invito non risulta valorizzato");

                if (livello.intValue() == 1) {
                    //PRIMO LIVELLO
                    Row nuovoRef = gr.nuovoRefertoMA(invito, ref, (String) session.get("user"));
                    //09032012 gaion: doppio cieco
                    Boolean authDoppioCieco = (Boolean) session.get("SODoppioCieco");
                    if (authDoppioCieco != null && authDoppioCieco.booleanValue()) {
                        //utente autorizzato al doppio cieco
                        //cerco il radiologo associato all'utente
                        Integer radiologo = getOpeFromUser();
                        if (radiologo != null) {
                            nuovoRef.setAttribute("L1Radiologo", radiologo);
                        }
                    }
                    //fine 09032012

                } else {
                    //SECONDO LIVELLO
                    gr.nuovoReferto2livMA(invito, ref, (String) session.get("user"));
                    am.getTransaction().postChanges();
                }

            } catch (Exception ex) {
                //  ex.printStackTrace();
                this.whenException("Impossibile inserire un nuovo referto: " + ex.getMessage(), am,
                                   new String[] { "Ref_SoInvitiPerRefertiMAView1" });
            }

        } catch (Exception e) {
            this.handleException("Impossibile inserire un nuovo referto: " + e.getMessage(), null);
        }

    }


    /**
     * Metodo che si occup adi sbolccare un referto per ulteriori modifiche
     * @param ctx
     */
    public void onReopen() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        Row r;
        try {

            if (livello == null)
                throw new Exception("Nessun referto da riaprire");

            AccessManager.checkPermission("SORiaperturaReferti" + livello + "Liv");

            ViewObject ref = am.findViewObject("Ref_SoRefertomammo" + livello + "livView1");
            r = ref.first();

            if (r == null)
                return;

            //////////////////////////////////////////
            //imposto il richiamo
            GestoreReferti gr = new GestoreReferti(am);
            //annullo il richiamo (se e' un secondo livello non devono esserci intreventi chiusi non ci sono interventi chusi)
            if (livello.intValue() == 1 ||
                (livello.intValue() == 2 && !ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi")))) {

                gr.updateInvito((String) r.getAttribute("Ulss"), (String) r.getAttribute("Tpscr"),
                                (Integer) r.getAttribute("Idinvito"), null, //sugg=null
                                am.findViewObject("Ref_SoInvitoView2"), 1, //non importa il livello, tanto il sugg e' null
                                (String) ADFContext.getCurrent().getSessionScope().get("user"));

            }

            //se la lettera non risulta spedita la cancello
            if (r.getAttribute("Dtspedizione") ==
                null) {
                //per il secondo livello non devo avere interventi chiusi
                if (livello.intValue() == 1 ||
                    (livello.intValue() > 1 && !ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi"))))
                    gr.deleteLettera(r);
            }
            /////////////////////////////////

            try {
                r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
                //09-01-2013 sara: aggiunto journaling
                //journaling
                Map sess = ADFContext.getCurrent().getSessionScope();
                Boolean flag = (Boolean) sess.get("flagAbilJournal");
                if (flag != null && flag.booleanValue()) {
                    String user = (String) sess.get("user");
                    String tpscr = (String) sess.get("scr");
                    String ulss = (String) sess.get("ulss");
                    am.preapareJournaling(user, ulss, tpscr);
                }
                //fine sara
                am.getTransaction().commit();
            } catch (Exception ex) {
                this.whenException("Impossibile riaprire il referto: " + ex.getMessage(), am,
                                   new String[] { "Ref_SoInvitiPerRefertiMAView1" });
            }

        } catch (Exception ex) {

            this.handleException("Impossibile riaprire il referto: " + ex.getMessage(), null);
            this.loadFromDB();
        }

    }

    protected void beforeSavingReferto() throws Exception {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");

        if (livello == null)
            throw new Exception("Nessun referto da salvare");

        ViewObject ref;
        if (livello.intValue() == 2)
            ref = am.findViewObject("Ref_SoRefertomammo2livView1");
        else
            ref = am.findViewObject("Ref_SoRefertomammo1livView1");

        Row r = ref.getCurrentRow();
        if (r == null)
            return;

        ref = am.findViewObject("Ref_SoInvitoView2");
        // Row invito = ref.first();

        // Impostazione dati ultima modifica
        r.setAttribute("Dtultmodifica", DateUtils.getOracleDateNow());
        r.setAttribute("Opultmodifica", ADFContext.getCurrent().getSessionScope().get("user"));

        this.updateDB();
    }

    /**
     * Invocato al momento della chiusura, controlla che i
     * dati necessari siano tutti presenti
     * @throws java.lang.Exception
     * @param endo
     * @param ref
     */
    protected void checkMandatoryData(Row ref) throws Exception {
        if (ref == null)
            return;

        Ref_SoRefertomammo2livViewRowImpl r = (Ref_SoRefertomammo2livViewRowImpl) ref;

        //per ogni esame eseguito deve esserci l'esito
        if (ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue() < r.getCitologiaDx().intValue() &&
            (r.getCDxEsito() == null || ConfigurationConstants.DB_FALSE.equals(r.getCDxEsito())))
            throw new Exception("La citologia al seno destro risulta eseguita; e' pertanto necessario inserirne l'esito");

        if (ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue() < r.getCitologiaSx().intValue() &&
            (r.getCSxEsito() == null || ConfigurationConstants.DB_FALSE.equals(r.getCSxEsito())))
            throw new Exception("La citologia al seno sinistro risulta eseguita; e' pertanto necessario inserirne l'esito");


        if (ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue() < r.getAgobDx().intValue() &&
            (r.getADxEsito() == null || ConfigurationConstants.DB_FALSE.equals(r.getADxEsito())))
            throw new Exception("L'agobiopsia al seno destro risulta eseguita; e' pertanto necessario inserirne l'esito");

        if (ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue() < r.getAgobSx().intValue() &&
            (r.getASxEsito() == null || ConfigurationConstants.DB_FALSE.equals(r.getASxEsito())))
            throw new Exception("L'agobiopsia al seno sinistro risulta eseguita; e' pertanto necessario inserirne l'esito");


    }


    /**
     * Metodo che si occupa della chiusura di un referto di 2 livello,
     * da sovrascrivere localmente per il primo livello.
     *
     * @param ctx
     */
    public void onClose(ClientEvent clientEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        RefMa_AppModule am =
            (RefMa_AppModule) refVoIter.getViewObject().getApplicationModule();
        String to_do = (String)clientEvent.getParameters().get("ref_lettera");
        ViewObject invito = am.findViewObject("Ref_SoInvitoView2");
        Ref_SoRefertomammo2livViewRowImpl r = null;

        try {
            r = (Ref_SoRefertomammo2livViewRowImpl) refVoIter.getCurrentRow();
            // Controllo di conformita'
            if (r.getIdsugg2l() == null || ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getIdsugg2l()))
                throw new Exception("Non e' stata segnalata nessuna raccomandazione conclusiva");
            if (r.getIdmedconcl1() == null)
                throw new Exception("Medico responsabile delle raccomandazioni conclusive non inserito");
            if (r.getDtconcl() == null)
                throw new Exception("Non e' stata inserita la data della raccomandazione conclusiva");

            // Aggiorno il db in conseguenza alle selezioni di interfaccia
            this.beforeSavingReferto();

            this.checkMandatoryData(r);
            this.checkDateOrder(r, invito.first());

            // Applico le consguenze della chiusura
            this.conseguenzeChiusura(r, am, to_do);

            // Imposto la diagnosi peggiore
            this.setDiagnosiPeggiore(r);

            // Il referto e' completo
            r.setCompleto(ConfigurationConstants.DB_TRUE);

            // Salvo il tutto
            // 09-01-2013 sara: aggiunto journaling
            // Journaling
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) sess.get("user");
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine sara

            am.getTransaction().commit();

            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);

        } catch (Exception ex) {
            //ex.printStackTrace();
            if (r != null) {

                //il referto non e' completo
                r.setCompleto(ConfigurationConstants.DB_FALSE);
            }
            this.handleException("Impossibile chiudere il referto: " + ex.getMessage(), null);
        }
    }

    /**
     * Applica le conseguenze della chiusura di un referto di 2 livello
     * @throws java.lang.Exception
     * @param ctx contesto
     * @param am application module
     * @param r record con il referto da chiudere
     */
    protected void conseguenzeChiusura(Row r, RefMa_AppModule am, String to_do) throws Exception {

        if (r == null)
            throw new Exception("Nessun referto da chiudere");
        ViewObject primol = am.findViewObject("Ref_SoInvito1livMAView1");
        
        //se ho degli interventi chiusi non vado a modifcare il richiamo
        if (ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi")))
            return;

        //creo la lettera di refero
        GestoreReferti gr = new GestoreReferti(am);
        //se devo ricreare la lettera la cancello e ricreo
        if ("ricrea".equals(to_do)) {
            gr.deleteLettera(r);
            gr.creaLetteraDiReferto(r, 2, primol.first());
        }
        //se la lettera non c'e' la creo semplicemente
        else if ("crea".equals(to_do)) {
            gr.creaLetteraDiReferto(r, 2, primol.first());
        }

        //imposto il richiamo
        gr.updateInvito(r, am.findViewObject("Ref_SoInvitoView2"), 2,
                        (String) ADFContext.getCurrent().getSessionScope().get("user"));

    }


    public String onRollback() {

        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();

        am.doRollback(new String[] { "Ref_SoInvitiPerRefertiMAView1" });

        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean.setDirty(false);
        return "to_refMa_ricerca";
    }

    /**
     * Fa la rollback, visualizza il messaggio d'errore e ricarica i dati di
     * interfaccia d aDB
     * @param livello
     * @param voNames
     * @param am
     * @param msg
     * @param ctx
     */
    protected void whenException(String msg, Parent_AppModule am, String[] voNames) {
        this.handleException(msg, am, voNames);
        this.loadFromDB();
    }

    /**
     * Metdodo invocato dopo l'update del model per mantenere l'interfaccia coerente
     * @param ctx
     */
    protected abstract void afterUpdateModel();

    /**
     * Metodo che riporta su DB le modifiche di interfaccia
     * @param ctx
     */
    protected abstract void updateDB() throws Exception;

    /**
     * Metodod che adatta l'interfaccaia ai dati letti dal db
     * @param ctx
     */
    protected abstract void loadFromDB();

    protected void beforeNavigate(String dest) throws Exception

    {
        //le modifiche sono gia' state salvate o annullate
        Ref_2livBean bean2 =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean2.setDirty(false);
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        //@codecoach:disable nextline
        //      Ref_SearchBean bean=(Ref_SearchBean)BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiMAView1");
        Row r = vo.getCurrentRow();
        if (r != null) {

            //impostare i parametri di ricerca della gestione
            //soggetto e accettazione

            if (dest.equals("iniSogg")) {
                //reset dei parametri di ricerca
                Sogg_RicParam beanSogg =
                    (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                beanSogg.resetCampi();
                beanSogg.setTessSan((String) r.getAttribute("Codts"));
                beanSogg.setCognome((String) r.getAttribute("Cognome"));
                beanSogg.setNome((String) r.getAttribute("Nome"));
                
                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    Map session = ADFContext.getCurrent().getSessionScope();
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanSogg.setChiave(chiave);
                    }
                }
                
                beanSogg.setInEta(0);
                beanSogg.setNavigazione(Boolean.TRUE);
            }
            //navigo verso l'accettazione oppure l'anamnesi
            else if (dest.startsWith("acc_to")) {
                Integer livello = (Integer) r.getAttribute("Livello");
                if (livello.intValue() == 2) {
                    Map session = ADFContext.getCurrent().getSessionScope();
                    try {
                        AccessManager.checkPermission("SOAcc2Liv");
                    } catch (IllegalAccessException ex) {
                        // non ha il permesso, permetto di navigare ma non
                        // imposto nulla
                        return;
                    }
                }
                AccMa_RicParam beanAcc =
                    (AccMa_RicParam) BindingContext.getCurrent().findDataControl("AccMa_RicParamDataControl").getDataProvider();
                beanAcc.resetCampi();
                beanAcc.setCognome((String) r.getAttribute("Cognome"));
                beanAcc.setNome((String) r.getAttribute("Nome"));
                
                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    Map session = ADFContext.getCurrent().getSessionScope();
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanAcc.setChiave(chiave);
                    }
                }
                
                beanAcc.setTessSan((String) r.getAttribute("Codts"));
                Integer idCprel = (Integer) r.getAttribute("Idcentroprelievo");
                if (idCprel == null)
                    beanAcc.setIdCprel(null);
                else
                    beanAcc.setIdCprel(new Integer(idCprel.intValue()));
                beanAcc.setLivello(livello.toString());
                beanAcc.setNavigazione(Boolean.TRUE);

                //se sto navigando verso l'anamnesi devo anche eseguire la ricerca
                //ed impostare la riga corrente
                if (dest.equals("acc_toMA_Anamnesi")) {
                    beanAcc.queryInviti();
                    AccMa_AppModule am2 =
                        (AccMa_AppModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
                    vo = am2.findViewObject("AccMa_RicInvitiView1");
                    //mi posiziono sul primo ed unico record
                    vo.first();
                    //faccio in modo che anche il vo dipenente sia posizionato sulla stessa riga
                    AccUtils.procInvCorrenteMammo(vo);
                }

            }
        } 

        //se sto uscendo dalla refertazione restto il bean
        if (!dest.startsWith("to_refMa_") && !dest.equals("livello1Ma")) {
            Ref_SearchBean bean =
                (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
            bean.reset();
        } else {
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean authDoppioCieco = (Boolean) sess.get("SODoppioCieco");
            Boolean revisione = (Boolean) sess.get("revisore");

            if ((authDoppioCieco != null && authDoppioCieco.booleanValue()) && (revisione == null)) {
                int lettura = GestoreRefertiUI.loadFromDB1livMA();
                if (lettura < 0)
                    throw new Exception("Non sono presenti o previste letture per l'utente connesso");
                else {
                    switch (lettura) {
                    case 1:
                        break;
                    case 2:
                        throw new Exception("Non sono presenti o previste letture per l'utente connesso");
                    case 3:
                        throw new Exception("Non sono presenti o previste letture per l'utente connesso");
                    }
                }
            }
        }
    }


    public String onRichnav(String dest, HashMap params) {
        Map req = ADFContext.getCurrent().getRequestScope();
        req.putAll(params);
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();

        boolean modP = am.getTransaction().isDirty();

        // Controllo anche eventuali variazioni all'interfaccia, registrate tramite il bean.
        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        boolean modP2 = bean.isDirty();


        // Se ci sono modifiche non salvate...
        if (modP || modP2) {
            //imposto l'attributo destinazione e lascio al metodo onLoad la gestione
            req.put("destNav", dest);
            ADFContext.getCurrent().getViewScope().put("destNav", dest);
            if(getFvDest()!=null)
                RequestContext.getCurrentInstance().addPartialTarget(getFvDest());
            Utility.addScriptOnPartialRequest("wantToSaveOnLoad()");
        }

        // altrimenti navigo verso la destinzione.
        else {
            try {
                this.beforeNavigate(dest);

                // ADFContext.getCurrent().getRequestScope().getRequestDispatcher(destination).forward(ADFContext.getCurrent().getRequestScope(), ctx.getHttpServletResponse());
                if (dest == null)
                    throw new Exception("destinazione non specificata");

                String suffix = "";
                if (dest.indexOf("?") >= 0)
                    suffix = "_p";
                int index = dest.indexOf(".");
                if (index >= 0)
                    dest = dest.substring(0, index);
                return dest + suffix;
            } catch (Exception ex) {
                this.handleException("Impossibile navigare verso la destinazione " + dest + ": " +
                                     ex.getMessage(), null);
            }
        }
        return "error";
    }

    protected void doRollback() {
        Parent_AppModule am =
            (Parent_AppModule) BindingContext.getCurrent().findDataControl(amName +
                                                                           "DataControl").getApplicationModule();

        am.doRollback(new String[] { "Ref_SoInvitiPerRefertiMAView1" });

    }


    /**
     * Cancella un referto di secondo livello con tutti i dati correlati
     * @param ctx
     */
    public String onDeleteReferto() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        //

        try {


            ViewObject ref = am.findViewObject("Ref_SoRefertomammo" + livello + "livView1");

            Row r = ref.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto selezionato per la cancellazione");

            //12032012 gaion: doppio cieco
            Boolean authDoppioCieco = (Boolean) ADFContext.getCurrent().getSessionScope().get("SODoppioCieco");
            if (livello.intValue() == 1 && (authDoppioCieco != null && authDoppioCieco.booleanValue())) {
                //si puo' cancellare solo se c'e' solo la 1 lettura e se l'utente e' l'autore
                if (r.getAttribute("L2Radiologo") == null && r.getAttribute("RRadiologo") == null) {
                    Integer operatore = getOpeFromUser();
                    if (operatore.intValue() != ((Integer) r.getAttribute("L1Radiologo")).intValue()) {
                        throw new Exception("L'utente non coincide con l'autore del referto");
                    }
                } else
                    throw new Exception("Sono presenti altre letture");
            }
            //fine 12032012

            String ulss = (String) r.getAttribute("Ulss");
            String tpscr = (String) r.getAttribute("Tpscr");
            Integer idinvito = (Integer) r.getAttribute("Idinvito");


            ViewObject interv = am.findViewObject("Ref_SoInterventomammoView1");
            ViewObject allegati = am.findViewObject("Ref_SoAllegatoView1");
            ViewObject inviti = am.findViewObject("Ref_SoInvitoView2");
            Row[] result1 = inviti.getFilteredRows("Idinvito", r.getAttribute("Idinvito"));
            //cancellazione referto e annessi
            GestoreReferti gr = new GestoreReferti(am);
            //vanno passati gli interventi
            //////////////////////////////////////////////////////

            String user = (String) ADFContext.getCurrent().getSessionScope().get("user");
            gr.deleteRefertoMA(r, interv, allegati, inviti, am, user);
            //update invito relativo

            if (result1.length == 0)
                throw new Exception("Invito cui si riferisce il referto non trovato");
            // Row invito=result1[0];
            gr.updateInvito(ulss, tpscr, idinvito, null, //sugg=null
                            inviti, 2, user);


            //6. salvo
            //09-01-2013 sara: aggiunto journaling
            //journaling
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine sara
            am.getTransaction().commit();

            //7. comunico all'utente che l'operazione e' avvenuta con successo e che il soggetto risulta
            //counque presentato
            //this.handleMessages(FacesMessage.SEVERITY_INFO,
            //                    "Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");
            this.saveMessages("Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");

            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiMAView1");

            ViewHelper.queryAndRestoreCurrentRow(vo);

            return "to_refMa_ricerca";
        } catch (Exception ex) {
            // ex.printStackTrace();
            this.whenException("Impossibile cancellare il referto: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiMAView1", "Ref_SoEndoscopiaView1" });
        }
        
        return "error";
    }

    /**
     * Rimuove (senza salvare) l'allegato di un referto, se esiste
     * @throws java.lang.Exception
     * @param am application module
     * @param r record del referto
     */
    protected void deleteAllegato(Row r, ApplicationModule am) throws Exception {
        if (r == null)
            throw new Exception("Nessun referto");

        if (r.getAttribute("Idallegato") != null) {
            Integer id = (Integer) r.getAttribute("Idallegato");
            r.setAttribute("Idallegato", null);
            ViewObject allegati = am.findViewObject("Ref_SoAllegatoView1");
            allegati.setWhereClause("IDALLEGATO=" + id);
            allegati.executeQuery();
            if (allegati.first() == null)
                throw new Exception("Lettera con identificativo " + id + " non trovata");
            allegati.first().remove();

        }
    }


    public void onSetDirty(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean.setDirty(true);
    }

    protected void checkDateOrder(Ref_SoRefertomammo2livViewRowImpl ref, Row invito) throws Exception {
        oracle.jbo.domain.Date d1 = (oracle.jbo.domain.Date) invito.getAttribute("Dtapp");
        oracle.jbo.domain.Date d2 = ref.getMData();
        oracle.jbo.domain.Date d3 = ref.getEData();
        oracle.jbo.domain.Date d4 = ref.getClData();
        oracle.jbo.domain.Date d5 = ref.getCDxData();
        oracle.jbo.domain.Date d6 = ref.getCSxData();
        oracle.jbo.domain.Date d7 = ref.getADxData();
        oracle.jbo.domain.Date d8 = ref.getASxData();
        oracle.jbo.domain.Date d9 = ref.getDtconcl();
        Date dd1 = null;
        Date dd2 = null;
        Date dd3 = null;
        Date dd4 = null;
        Date dd5 = null;
        Date dd6 = null;
        Date dd7 = null;
        Date dd8 = null;
        Date dd9 = null;
        if (d1 == null)
            throw new Exception("La data dell'invito non risulta valorizzata");
        if (d9 == null)
            throw new Exception("La data delle conclusioni di 2 livello non risulta valorizzata");


        if (d1 != null) {
            Calendar c = DateUtils.getCalendar(d1.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd1 = c.getTime();
        }
        if (d2 != null) {
            Calendar c = DateUtils.getCalendar(d2.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd2 = c.getTime();
        }
        if (d3 != null) {
            Calendar c = DateUtils.getCalendar(d3.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd3 = c.getTime();
        }

        if (d4 != null) {
            Calendar c = DateUtils.getCalendar(d4.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd4 = c.getTime();
        }
        if (d5 != null) {
            Calendar c = DateUtils.getCalendar(d5.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd5 = c.getTime();
        }
        if (d6 != null) {
            Calendar c = DateUtils.getCalendar(d6.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd6 = c.getTime();
        }
        if (d7 != null) {
            Calendar c = DateUtils.getCalendar(d7.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd7 = c.getTime();
        }
        if (d8 != null) {
            Calendar c = DateUtils.getCalendar(d8.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd8 = c.getTime();
        }
        if (d9 != null) {
            Calendar c = DateUtils.getCalendar(d9.getValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd9 = c.getTime();
        }

        //controllo sulla data della mammografia
        if (dd2 != null) {

            if (dd1.after(dd2))
                throw new Exception("La data della mammografia (" + DateUtils.dateToString(dd2) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");

            if (dd2.after(dd9))
                throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd9) +
                                    ") non puo' essere precedente alla data della mammografia(" +
                                    DateUtils.dateToString(dd2) + ")");
        }

        //controllo sulla data dell'ecografia
        if (dd3 != null) {

            if (dd1.after(dd3))
                throw new Exception("La data dell'ecografia (" + DateUtils.dateToString(dd3) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");

            if (dd3.after(dd9))
                throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd9) +
                                    ") non puo' essere precedente alla data dell'ecografia(" +
                                    DateUtils.dateToString(dd3) + ")");
        }

        //controllo sulla data dell'esame clinico
        if (dd4 != null) {

            if (dd1.after(dd4))
                throw new Exception("La data dell'esame clinico (" + DateUtils.dateToString(dd4) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");

            if (dd4.after(dd9))
                throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd9) +
                                    ") non puo' essere precedente alla data dell'esame clinico (" +
                                    DateUtils.dateToString(dd4) + ")");
        }

        //controllo sulla data della citologia destra
        if (dd5 != null) {

            if (dd1.after(dd5))
                throw new Exception("La data della citologia al seno destro (" + DateUtils.dateToString(dd5) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");

            if (dd5.after(dd9))
                throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd9) +
                                    ") non puo' essere precedente alla data della citologia al seno destro(" +
                                    DateUtils.dateToString(dd5) + ")");
        }

        //controllo sulla data della citologia sinistra
        if (dd6 != null) {

            if (dd1.after(dd6))
                throw new Exception("La data della citologia al seno sinistro (" + DateUtils.dateToString(dd6) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");

            if (dd6.after(dd9))
                throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd9) +
                                    ") non puo' essere precedente alla data della citologia al seno sinistro (" +
                                    DateUtils.dateToString(dd6) + ")");
        }


        //controllo sulla data dell'agobiopsia destra
        if (dd7 != null) {

            if (dd1.after(dd7))
                throw new Exception("La data dell'agobiopsia al seno destro (" + DateUtils.dateToString(dd7) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");

            if (dd7.after(dd9))
                throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd9) +
                                    ") non puo' essere precedente alla data dell'agobiopsia al seno destro(" +
                                    DateUtils.dateToString(dd7) + ")");
        }

        //controllo sulla data dell'agobiopsia sinistra
        if (dd8 != null) {

            if (dd1.after(dd8))
                throw new Exception("La data dell'agobiopsia al seno sinistro (" + DateUtils.dateToString(dd8) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");

            if (dd8.after(dd9))
                throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd9) +
                                    ") non puo' essere precedente alla data dell'agobiopsia al seno sinistro (" +
                                    DateUtils.dateToString(dd8) + ")");
        }


    }

    /**
     * Metodo che analizza tutte le diagnosi istologiche del referto in questione
     * ed imposta la diagnosi peggiore utilizzando la piu' grave (l'ordine di gravita'
     * e' dato dall'ordine di visualizzazione)
     * @throws java.lang.Exception
     * @param ctx
     * @param r record con il referto di 2 livello (che deve essere current row)
     */
    protected void setDiagnosiPeggiore(Ref_SoRefertomammo2livViewRowImpl r) throws Exception {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        if (livello == null || livello.intValue() == 1)
            return;

        RowSetIterator iter = null;

        try {
            Integer diagnosi = ConfigurationConstants.DB_FALSE.intValue();
            int ordine = -1;
            Integer ord = null;

            //maping degli esiti di citologie e agobiopsie sulle diagnosi peggiori
            //si accede con l'indice corrispondente all'esito e si ottiene la diagnosi
            Integer[] mapping = {
                ConfigurationConstants.DB_FALSE.intValue(), ConfigurationConstants.CODICE_DIAGNOSI_NORMALE,
                ConfigurationConstants.CODICE_DIAGNOSI_BENIGNA, ConfigurationConstants.CODICE_DIAGNOSI_BENIGNA,
                ConfigurationConstants.CODICE_DIAGNOSI_SOSPETTA, ConfigurationConstants.CODICE_DIAGNOSI_MALIGNA
            };


            //controllo sulla citologia destra
            if (r.getCitologiaDx() != null &&
                ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue() < r.getCitologiaDx().intValue()) {
                ord =
                    this.getOrdine(r.getUlss(), r.getTpscr(), ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,
                                   mapping[r.getCDxEsito().intValue()], am);
                if (ord != null) {
                    //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                    //la diagnosi peggiore
                    if (ordine < ord.intValue()) {
                        diagnosi = mapping[r.getCDxEsito().intValue()];
                        ordine = ord.intValue();
                    }
                }
            }

            //controllo sulla citologia sinistra
            if (r.getCitologiaSx() != null &&
                ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue() < r.getCitologiaSx().intValue()) {
                ord =
                    this.getOrdine(r.getUlss(), r.getTpscr(), ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,
                                   mapping[r.getCSxEsito().intValue()], am);
                if (ord != null) {
                    //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                    //la diagnosi peggiore
                    if (ordine < ord.intValue()) {
                        diagnosi = mapping[r.getCSxEsito().intValue()];
                        ordine = ord.intValue();
                    }
                }
            }

            //controllo sull'agobiospia destra
            if (r.getAgobDx() != null &&
                ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue() < r.getAgobDx().intValue()) {
                ord =
                    this.getOrdine(r.getUlss(), r.getTpscr(), ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,
                                   mapping[r.getADxEsito().intValue()], am);
                if (ord != null) {
                    //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                    //la diagnosi peggiore
                    if (ordine < ord.intValue()) {
                        diagnosi = mapping[r.getADxEsito().intValue()];
                        ordine = ord.intValue();
                    }
                }
            }

            //controllo sull'agobiopsia sinistra
            if (r.getAgobSx() != null &&
                ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue() < r.getAgobSx().intValue()) {
                ord =
                    this.getOrdine(r.getUlss(), r.getTpscr(), ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,
                                   mapping[r.getASxEsito().intValue()], am);
                if (ord != null) {
                    //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                    //la diagnosi peggiore
                    if (ordine < ord.intValue()) {
                        diagnosi = mapping[r.getASxEsito().intValue()];
                        ordine = ord.intValue();
                    }
                }
            }

            //controllo sulle istologie chirurgiche, solo se gli interventi sono chiusi
            if (ConfigurationConstants.DB_TRUE.equals(r.getIntchiusi())) {
                ViewObject interv = am.findViewObject("Ref_SoInterventomammoView1");
                iter = ViewHelper.getRowSetIterator(interv, "interventi_iter");
                while (iter.hasNext()) {
                    Ref_SoInterventomammoViewRow e = (Ref_SoInterventomammoViewRow) iter.next();
                    ord = null;
                    //
                    if (ConfigurationConstants.DB_TRUE.equals(e.getIstologia()) && e.getDiagnosi() != null) {
                        ord =
                            this.getOrdine(e.getUlss(), e.getTpscr(), ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,
                                           e.getDiagnosi(), am);
                        if (ord != null) {
                            //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                            //la diagnosi peggiore
                            if (ordine < ord.intValue()) {
                                diagnosi = e.getDiagnosi();
                                ordine = ord.intValue();
                            } 
                        }
                    }
                }
            } //se gli intreventi sono chiusi
            //aggiorno l adiagnosi peggiore
            r.setDiagnosiPeggiore(diagnosi);
            r.setGrDiagnosi(ConfigurationConstants.NOME_GRUPPO_RACDIA_2L);


        } finally {
            if (iter != null)
                iter.closeRowSetIterator();

        }
    }


    /**
     * Dato un dato di configurazione ne restituisce l'ordine
     * @throws java.lang.Exception
     * @return
     * @param am
     * @param idcnfref
     * @param gruppo
     * @param tpscrgetorcine
     * @param ulss
     */
    protected Integer getOrdine(String ulss, String tpscr, String gruppo, Integer idcnfref,
                               ApplicationModule am) throws Exception {
        ViewObject cnf = am.findViewObject("Ref_SoCnfRef2livView1");
        String where =
            "ULSS='" + ulss + "' AND " + "TPSCR='" + tpscr + "' AND " + "GRUPPO='" + gruppo + "' AND " + "IDCNFREF2L=" +
            idcnfref;
        cnf.setWhereClause(where);
        cnf.executeQuery();
        Row cnfdata = cnf.first();
        if (cnfdata == null)
            throw new Exception("Dati di configurazione non trovati: ULSS=" + ulss + " TPSCR=" + tpscr + " GRUPPO=" +
                                gruppo + " IDCNFREF2L=" + idcnfref);
        return (Integer) cnfdata.getAttribute("Ordine");
    }

    /**
     * Restituisce l'operatore associato all'utente
     * Inserito per la refertazione in modalita' doppio cieco
     * @return
     * @param ctx
     */
    public Integer getOpeFromUser() {
        Cnf_AppModule amCnf =
            (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        ViewObject cnf = amCnf.findViewObject("Cnf_SoCnfUtentiOperatoriView1");
        String username = (String) ADFContext.getCurrent().getSessionScope().get("user");
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String where =
            "Cnf_SoCnfUtentiOperatori.ULSS='" + ulss + "' AND Cnf_SoCnfUtentiOperatori.TPSCR='" + tpscr +
            "' AND Cnf_SoCnfUtentiOperatori.USERNAME = '" + username + "'";
        cnf.setWhereClause(where);
        cnf.executeQuery();
        Row user = cnf.first();
        if (user != null) {
            return (Integer) user.getAttribute("Operatore");
        } else
            return null;
    }

    public String onNavigate() {
        this.onApply(null);
        String destination = (String) ADFContext.getCurrent().getRequestScope().get("destination");
        int index = destination.indexOf(".");
        if (index >= 0) {
            destination = destination.substring(0, index);
        }

        try{
            this.beforeNavigate(destination);
        } catch (Exception ex) {
            this.handleException("Impossibile navigare verso la destinazione " + destination + ": " +
                                 ex.getMessage(), null);
        }
        return destination;
    }
    
    public void downloadRiassunto2liv(FacesContext fc, OutputStream os) throws Exception {
        int report_n = SystemReport.REFERTO_RIASSUNTIVO_2L_MA;

        File pdf = null;
        HashMap h = null;
        try {

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
            ViewObject ref = refVoIter.getViewObject();
            Row r = ref.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto selezionato per la visualizzazione");
            SystemReport report = new SystemReport(report_n);
            h = new HashMap();
            h.put("id_referto", new Double(((Integer) r.getAttribute("Idreferto")).doubleValue()));
            RefMa_AppModule am = (RefMa_AppModule) ref.getApplicationModule();
            pdf = report.getPDFReport( am.getDBConnection(), h);
            
            //download del file
            FileInputStream fis;
            byte[] b;
            try {
                fis = new FileInputStream(pdf);

                int n;
                while ((n = fis.available()) > 0) {
                    b = new byte[n];
                    int res = fis.read(b);
                    os.write(b, 0, b.length);
                    if (res == -1)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            os.flush();
            os.close();
        } catch (Exception ex) {
            //  ex.printStackTrace();
            throw new Exception("Impossibile visualizzare il referto riassuntivo: " + ex.getMessage());
            
        } finally {
            if (pdf != null)
                pdf.delete();

            ParametriSistema.releaseLogo(h);
        }
    }
    
}
