package view.referto;

import insiel.dataHandling.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import model.common.Parent_AppModule;
import model.common.Ref_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.ViewHelper;

import model.datacontrol.Acc_RicParam;
import model.datacontrol.Ref_2livBean;
import model.datacontrol.Ref_SearchBean;
import model.datacontrol.Sogg_RicParam;

import model.referto.Ref_SoSoggScrViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;

import org.apache.myfaces.trinidad.context.RequestContext;

import view.commons.action.Parent_DataForwardAction;

import view.reports.SystemReport;

import view.util.Utility;

public abstract class Ref_DataForwardAction extends Parent_DataForwardAction {
    protected void setAppModule() {
        this.amName = "Ref_AppModule";
    }

    public void onApply(ActionEvent actionEvent) {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        try {

            if (livello == null)
                throw new Exception("Nessun referto da salvare");
            //lavoro peculiare alla singola form
            this.beforeSavingReferto();
            //09-01-2013 sara: aggiunto journaling
            //journalig
            Map sess = ADFContext.getCurrent().getSessionScope();
            String user = (String) sess.get("user");
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine sara
            am.getTransaction().commit();
            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiView1");
            String voName;
            if (livello.intValue() == 2)
                voName = "Ref_SoRefertocito2livView4";
            else
                voName = "Ref_SoRefertocito1livView3";

            ViewObject ref = am.findViewObject(voName);
            Row r = ref.getCurrentRow();

            // mauro 30/11/2010, modifica per assicurare aggiornamento esito invito
            Integer idInvito = (Integer) r.getAttribute("Idinvito");
            //02022012 S.Gaion : messo qui l'update dell'invito per evitare il lock sulla riga
            Row[] result = vo.getFilteredRows("Idinvito", idInvito);
            if (result.length == 0)
                throw new Exception("Invito cui si riferisce il referto non trovato");
            Row invito = result[0];
            invito.setAttribute("Codesitoinvito", "S");
            invito.setAttribute("Opmodifica", user);
            invito.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
            if (livello.intValue() == 1) {
                Integer idCentroRef = (Integer) r.getAttribute("Idcentroref");
                if (idCentroRef != null)
                    invito.setAttribute("Idcentroref1liv",idCentroRef);
            }
            am.getTransaction().commit();
            //GestoreReferti.updateEsito(am,idInvito,);
            // fine 02022012

            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);


            /*MOD 20/06/2007
      r.refresh(r.REFRESH_CONTAINEES);
      r=vo.getCurrentRow();
      if(r!=null)
        ViewHelper.restoreCurrentRow(vo,"Idinvito",r.getAttribute("Idinvito"));
        */

            /* mod 20090608 se cercpo in base all'esito del HPV, se lo cambio perdo il soggetto
        ViewHelper.queryAndRestoreCurrentRow(vo);
        */
            r.refresh(r.REFRESH_CONTAINEES);
            /**/
            this.loadFromDB();
        } catch (Exception ex) {
            // ex.printStackTrace();
            //   this.whenException(,"Impossibile salvare le modifiche: "+ex.getMessage(),am,new String[]{"Ref_SoInvitiPerRefertiView1"});
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
        }
    }

    /**
     * Salva e torna alla pagina di inizio
     * @param
     */
    public String onConfirm() {
        this.onApply(null);
        /*mod 20090608*/
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiView1");
        ViewHelper.queryAndRestoreCurrentRow(vo);
        return "to_ref_ricerca";
    }


    /**
     * Prima di un eventuale salvataggio automatico salvo io a modo mio
     * (per mantenere la consistenza dei dati tra interfaccia e DB)
     * @param
     */
    protected boolean beforeSave() {
        // TODO:  Override this view.commons.Parent_DataForwardAction method

        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        try {

            if (livello == null)
                throw new Exception("Nessun referto da salvare");

            String voName;
            if (livello.intValue() == 2)
                voName = "Ref_SoRefertocito2livView4";
            else
                voName = "Ref_SoRefertocito1livView3";

            ViewObject ref = am.findViewObject(voName);
            Row r = ref.getCurrentRow();
            if (r == null)
                return true;
            //   r.refresh(r.REFRESH_CONTAINEES);

            if (r != null && r.getAttribute("Completo") != null &&
                ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
                throw new Exception("Referto gia' chiuso, le modifiche sono state annullate");

            //trattamento peculiare alla singola pagina
            this.beforeSavingReferto();
            return true;
        } catch (Exception ex) {
            //  this.whenException(,"Impossibile salvare le modifiche: "+ex.getMessage(),am,new String[]{"Ref_SoInvitiPerRefertiView1"});
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
            return false;
        }
    }

    /**
     * Creazione di un nuovo referto
     * @param
     */
    public void onCreateReferto(ActionEvent actionEvent) {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        //viewObject dipendente in base al valore di idinvito (che viene impostato in automatico)
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        Boolean hpv = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        try {

            if (livello == null)
                throw new Exception("Nessun referto da salvare");

            String voName;
            if (livello.intValue() == 2)
                voName = "Ref_SoRefertocito2livView4";
            else
                voName = "Ref_SoRefertocito1livView3";

            ViewObject ref = am.findViewObject(voName);
            //se il referto c'e' gia' non posso crearlo
            if (ref.getRowCount() > 0)
                throw new Exception("Esiste gia' un referto per l'invito selezionato)");

            //altrimenti lo devo creare
            ViewObject invito = am.findViewObject("Ref_SoInvitoView2");
            Row inv = invito.first();
            if (inv == null)
                throw new Exception("Invito non trovato");
            GestoreReferti gr = new GestoreReferti(am);

            if (inv.getAttribute("Dtapp") == null)
                throw new Exception("La data dell'invito non risulta valorizzata");
            if (inv.getAttribute("Idcentroprelievo") == null || inv.getAttribute("Idcentroref1liv") == null)
                throw new Exception("Il centro di prelievo e/o il centro di refertazione dell'invito non risultano valorizzati");


            if (livello.intValue() ==
                2) {
                /*Row nuovoRef=*/gr.nuovoReferto2liv(invito, ref,
                                                     (String) ADFContext.getCurrent().getSessionScope().get("user"));
            } else {
                //20110719 Serra
                ViewObject acc = am.findViewObject("Ref_SoAccCito1livView2");
                Row r_acc = acc.getCurrentRow();
                boolean create_hpv = false;
                if (r_acc !=
                    null) { //se in accettazione ho registrato l'esecuiozne dle test hpv, ne creo i prsupposti per il referto
                    if (ConfigurationConstants.CODICE_PRELIEVO_HPV_ESEGUITO.equals((Integer) r_acc.getAttribute("PrelievoHpv")))
                        create_hpv = true;
                } else {
                    //in caso contrario dipende dalla configurazione dell'azienda (progetto hpv attivo oppure no)
                    create_hpv = hpv == null ? false : hpv.booleanValue();
                }
                //fine serra

                gr.nuovoReferto(invito, ref, (String) ADFContext.getCurrent().getSessionScope().get("user"),
                                am.findViewObject("Ref_SoCodref1livcADEPREView1"),
                                am.findViewObject("Ref_SoCodref1livcGIUDIAView1"), create_hpv);
            }
            
            this.filter();
            
        } catch (Exception ex) {
            //    ex.printStackTrace();
            this.whenException("Impossibile inserire un nuovo referto: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiView1" });
        }
    }


    /**
     * Metodo che si occupa di sbolccare un referto per ulteriori modifiche
     * @param
     */
    public void onReopen() {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        Row r;
        try {
            if (livello == null)
                throw new Exception("Nessun referto da riaprire");

            AccessManager.checkPermission("SORiaperturaReferti" + livello.intValue() + "Liv");

            String voName;
            if (livello.intValue() == 2)
                voName = "Ref_SoRefertocito2livView4";
            else
                voName = "Ref_SoRefertocito1livView3";

            ViewObject ref = am.findViewObject(voName);
            r = ref.getCurrentRow();
            if (r == null)
                return;

            // Trial - Riporto il suggerimento a quello precedente la randomizzazione (22)
            //         solo se l'invito e' attivo
            ViewObject invitoVo = am.findViewObject("Ref_SoInvitiPerRefertiView1");
            Row invitoRow = invitoVo.getCurrentRow();
            boolean isInvitoAttivo = ((Integer) invitoRow.getAttribute("Attivo")).intValue() == 1;
            if (isInvitoAttivo) {
                if (((Integer) r.getAttribute("Idsugg")).intValue() == 90) {
                    r.setAttribute("Idsugg", new Integer(22));
                }

                // Trial - Rimuovo il braccio, che verra' randomizzato nuovamente alla chiusura del referto
                if (livello.intValue() == 1) {
                    ViewObject soggettoScrView = am.findViewObject("Ref_SoSoggScrView1");
                    if (soggettoScrView != null) {
                        soggettoScrView.setWhereClause("Ref_SoSoggScr.ulss = '" + r.getAttribute("Ulss") +
                                                       "' AND Ref_SoSoggScr.tpscr = '" + r.getAttribute("Tpscr") +
                                                       "' AND Ref_SoSoggScr.codts = '" + r.getAttribute("Codts") + "'");
                        soggettoScrView.executeQuery();
                        Ref_SoSoggScrViewRowImpl soggettoScrRow = (Ref_SoSoggScrViewRowImpl) soggettoScrView.first();
                        if (soggettoScrRow != null) {
                            soggettoScrRow.refresh(Row.REFRESH_CONTAINEES);
                            soggettoScrRow.setAttribute("IdbraccioTrial", null);
                        }
                    }
                }
            }

            //annullo il richiamo (se e' un secondo livello non devo avere interventi chiusi)
            GestoreReferti gr = new GestoreReferti(am);
            if (livello.intValue() == 1 ||
                (livello.intValue() == 2 && !ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi")))) {
                gr.updateInvito((String) r.getAttribute("Ulss"), (String) r.getAttribute("Tpscr"),
                                (Integer) r.getAttribute("Idinvito"), null, // se null vuol dire che non deve essere cambiato
                                am.findViewObject("Ref_SoInvitoView2"), 2,
                                (String) ADFContext.getCurrent().getSessionScope().get("user"));
            }

            //se la lettera non risulta spedita la cancello
            if (r.getAttribute("Dtspedizione") == null) {

                //per il secondo livello non devo avere interventi chiusi
                if (livello.intValue() == 1 ||
                    (livello.intValue() > 1 && !ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi")))) {
                    gr.deleteLettera(r);
                }
            }
            /////////////////////////////////

            try {
                r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
                //journaling
                //100120123 sara
                Map sess = ADFContext.getCurrent().getSessionScope();
                Boolean flag = (Boolean) sess.get("flagAbilJournal");
                if (flag != null && flag.booleanValue()) {
                    String user = (String) sess.get("user");
                    String tpscr = (String) sess.get("scr");
                    String ulss = (String) sess.get("ulss");
                    am.preapareJournaling(user, ulss, tpscr);
                }
                //fine 10012013

                am.getTransaction().commit();

            } catch (Exception ex) {
                this.whenException("Impossibile riaprire il referto: " + ex.getMessage(), am,
                                   new String[] { "Ref_SoInvitiPerRefertiView1" });
            }
        } catch (Exception ex) {
            this.handleException("Impossibile riaprire il referto: " + ex.getMessage(), null);
            this.loadFromDB();
        }
    }

    protected void beforeSavingReferto() throws Exception {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        Boolean hpv = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");

        if (livello == null)
            throw new Exception("Nessun referto da salvare");

        String voName;
        if (livello.intValue() == 2)
            voName = "Ref_SoRefertocito2livView4";
        else
            voName = "Ref_SoRefertocito1livView3";

        ViewObject ref = am.findViewObject(voName);
        Row r = ref.getCurrentRow();
        if (r == null)
            return;

        this.checkMandatoryData(r);
        //ref=am.findViewObject("Ref_SoInvitoView2");
        //Row invito=ref.first();
        //  System.out.println(invito.getAttribute("Dtapp"));
        /* if(livello.intValue()==2)
        this.checkDateOrder(r,invito);*/

        if (r != null && r.getAttribute("Completo") != null &&
            ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
            throw new Exception("Referto gia' chiuso, le modifiche sono state annullate");

        String user = (String) ADFContext.getCurrent().getSessionScope().get("user");
        //impostazione dati ultima modifica
        r.setAttribute("Dtultimamodifica", DateUtils.getOracleDateNow());
        r.setAttribute("Opultmodifica", user);

        //allineamento numero vetrino con accettazione
        if (livello.intValue() == 1) {
            GestoreReferti gr = new GestoreReferti(am);
            gr.updateVetrinoInAcc((Integer) r.getAttribute("Idinvito"), (BigDecimal) r.getAttribute("Numaccap"),
                                  (String) r.getAttribute("Tpscr"), true, user, hpv.booleanValue());
        }

        this.updateDB();

    }

    protected void checkMandatoryData(Row ref) throws Exception {
        if (ref == null)
            return;

        if (ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Colposcopia")) ||
            ConfigurationConstants.CODICE_COLPVL_CERVICE.equals(ref.getAttribute("Colposcopia")) ||
            ConfigurationConstants.CODICE_COLPVL_NON_SODDISF.equals(ref.getAttribute("Colposcopia"))) {
            if (ref.getAttribute("Dtcolposcopia") == null)
                throw new Exception("Inserire la data della colposcopia");
            if (ref.getAttribute("Idmedcolpo") == null)
                throw new Exception("Inserire il medico responsabile della colposcopia");
        }

        if (ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Istbioptica"))) {
            if (ref.getAttribute("Dtbiopsia") == null)
                throw new Exception("Inserire la data dell'istologia bioptica");
            if (ref.getAttribute("Idmedbiopato") == null)
                throw new Exception("Inserire il medico responsabile dell'istologia bioptica");
        }


        //se il prelievo citologico non e' eseguito, non posso avere ne' giudizio diagnostico ne' positivita'
        if (!ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Precito"))) {
            ref.setAttribute("GiudizioPap", ConfigurationConstants.CODICE_GIUDIA_DEFAULT);
            ref.setAttribute("PositivoPap", null);
            ref.setAttribute("GrPositivoPap", null);
        } else if (!ConfigurationConstants.CODICE_GIUDIA2L_POSITIVO.equals(ref.getAttribute("GiudizioPap"))) { //se il pap non e' positivo, non ci puo' essere la positivita'
            ref.setAttribute("PositivoPap", null);
            ref.setAttribute("GrPositivoPap", null);
        }

    }

    /**
     * Metodo che si occupa della chiusura di un referto di 2 livello,
     * da sovrascrivere localmente per il primo livello
     * @param
     */
    public void onClose(ClientEvent clientEvent) {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertocito2livView4");
        Row r = ref.getCurrentRow();
        ViewObject inviti = am.findViewObject("Ref_SoInvitoView2");
        Row invito = inviti.first();

        try {
            //controllo di conformita'
            if (r.getAttribute("Idsugg") == null ||
                ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getAttribute("Idsugg")))
                throw new Exception("Non e' stata segnalata nessuna raccomandazione conclusiva");
            if (r.getAttribute("Idmedconcl") == null)
                throw new Exception("Medico responsabile delle raccomandazioni conclusive non inserito");
            if (r.getAttribute("Dtconcl") == null)
                throw new Exception("Non e' stata inserita la data della raccomandazione conclusiva");

            this.checkDateOrder(r, invito);

            //aggiorno il db in conseguenza alle selezioni di interfaccia
            this.beforeSavingReferto();

            //applico le conseguenze della chiusura
            this.conseguenzeChiusura(r, am, (String)clientEvent.getParameters().get("ref_lettera"));

            //imposto la diagnosi peggiore
            this.setDiagnosiPeggiore(r);

            //il referto e' completo
            r.setAttribute("Completo", ConfigurationConstants.DB_TRUE);

            //journaling
            //100120123 sara
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) sess.get("user");
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine 10012013

            //salvo il tutto
            am.getTransaction().commit();

            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);
            //this.handleMessages(, "Operazione compiuta, ma momentaneamente incompleta: l'eventuale lettera non e' stata creata");
        } catch (Exception ex) {
            //ex.printStackTrace();
            if (r != null) {

                //il referto non e' completo
                r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
            }
            this.handleException("Impossibile chiudere il referto: " + ex.getMessage(), null);
        }
    }

    /**
     * Applica le conseguenze della chiusura di un referto di 2 livello
     * @throws java.lang.Exception
     * @param  contesto
     * @param am application module
     * @param r record con il referto da chiudere
     */
    protected void conseguenzeChiusura(Row r, Ref_AppModule am, String to_do) throws Exception {

        if (r == null)
            throw new Exception("Nessun referto da chiudere");
        ViewObject primol = am.findViewObject("Ref_SoInvitoPrimoLivView1");
        //String to_do = (String) ADFContext.getCurrent().getRequestScope().get("ref_lettera");
        //se ho degli interventi chiusi non vado a modifcare il richiamo
        if (ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi")))
            return;

        //creo la lettera di refero
        GestoreReferti gr = new GestoreReferti(am);
        //se devo ricreare la lettera la cancello e ricreo
        if ("ricrea".equals(to_do)) {
            gr.deleteLettera(r);
            gr.creaLetteraDiRefertoCI(r, null, null, 2, primol.first());
        }
        //se la lettera non c'e' la creo semplicemente
        else if ("crea".equals(to_do)) {
            gr.creaLetteraDiRefertoCI(r, null, null, 2, primol.first());
        }

        //imposto il richiamo
        gr.updateInvito(r, am.findViewObject("Ref_SoInvitoView2"), 2,
                        (String) ADFContext.getCurrent().getSessionScope().get("user"));

    }


    public String onRollback() {

        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        am.doRollback("Ref_SoInvitiPerRefertiView1");
        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean.setDirty(false);
        return "to_ref_ricerca";
    }

    /**
     * Fa la rollback, visualizza il messaggio d'errore e ricarica i dati di
     * interfaccia d aDB
     * @param livello
     * @param voNames
     * @param am
     * @param msg
     * @param
     */
    private void whenException(String msg, Parent_AppModule am, String[] voNames) {
        this.handleException(msg, am, voNames);
        this.loadFromDB();
    }

    /**
     * Metodo che riporta su DB le modifiche di interfaccia
     * @param
     */
    protected abstract void updateDB();

    /**
     * Metodod che adatta l'interfaccaia ai dati letti dal db
     * @param
     */
    protected abstract void loadFromDB();
    
    protected abstract void filter();

    protected void beforeNavigate(String dest) throws Exception

    {
        //le modifiche sono gia' state salvate o annullate
        Ref_2livBean bean2 =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean2.setDirty(false);
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        //@codecoach:disable nextline
        //      Ref_SearchBean bean=(Ref_SearchBean)BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiView1");
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
            } else if (dest.startsWith("acc_to")) {
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
                Acc_RicParam beanAcc =
                    (Acc_RicParam) BindingContext.getCurrent().findDataControl("Acc_RicParamDataControl").getDataProvider();
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
            }
        } 

        //se sto uscendo dalla refertazione restto il bean
        //Object b = internal_dest.get(dest);
        if (!dest.startsWith("to_ref") && !dest.startsWith("toRef") && !dest.startsWith("ref_toRef")) {
            Ref_SearchBean bean =
                (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
            bean.reset();
        }


    }


    public String onRichnav(String dest, HashMap params) {
        //prima si deve impostare il nome dell'application module
        this.setAppModule();

        Map req = ADFContext.getCurrent().getRequestScope();
        req.putAll(params);

        ApplicationModule am =
            BindingContext.getCurrent().findDataControl(amName + "DataControl").getApplicationModule();
        boolean modP = am.getTransaction().isDirty();

        //controllo anche eventuali variazioni all'interfaccia, registrate tramite il bean
        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        boolean modP2 = bean.isDirty();


        //se ci sono modifiche non salvate...
        if (modP || modP2) { //imposto l'attributo destinazione e lascio al metodo onLoad la gestione
            req.put("destNav", dest);
            ADFContext.getCurrent().getViewScope().put("destNav", dest);
            if(getFvDest()!=null)
                RequestContext.getCurrentInstance().addPartialTarget(getFvDest());
            Utility.addScriptOnPartialRequest("wantToSaveOnLoad()");
        } else { //altriemnti navigo verso la destinzione

            try {
                this.beforeNavigate(dest);
                //e navigo verso la destinazione
                if (dest == null)
                    throw new Exception("destinazione non specificata");
                return dest;
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
        am.doRollback("Ref_SoInvitiPerRefertiView1");

    }


    public String onDeleteIntervento() {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInterventocitoView6");
        Row r = vo.getCurrentRow();
        try {
            if (r == null)
                throw new Exception("Nessun intervento da cancellare");

            if (ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Completo")))
                throw new Exception("Gli interventi risultano chiusi");

            //sto cancellando l'unico intervento,
            //devo riapplicare le conseguenze del referto di secondo livello,
            //se questo risulta completo
            if (vo.getRowCount() == 1) {
                ViewObject ref = am.findViewObject("Ref_SoRefertocito2livView4");
                Row r2 = ref.getCurrentRow();
                if (r2 == null)
                    throw new Exception("Nessun referto");
                if (ConfigurationConstants.DB_TRUE.equals(r2.getAttribute("Completo"))) {
                    //se il referto e' completo devo reimpostare le sue conseguenze
                    this.conseguenzeChiusura(r2, am, "");
                }
            }

            int n = 0;
            if (r.getAttribute("NInt") != null)
                n = ((Integer) r.getAttribute("NInt")).intValue();

            Integer idref = (Integer) r.getAttribute("Idreferto");

            // mauro 15/12/2010, prima di cancellare l'intervento faccio il backup
            Integer idInt = (Integer) r.getAttribute("Idint");
            String sql =
                "insert into so_interventocito_bck " + "select * from so_interventocito where idint = " +
                idInt.toString();
            am.getTransaction().executeCommand(sql);

            //aggiorno la numerazione degli interventi
            String update =
                "UPDATE SO_INTERVENTOCITO SET N_INT=N_INT-1 " + "WHERE IDREFERTO=" + idref + " AND N_INT>=" + n;
            am.getTransaction().executeCommand(update);

            //journaling
            //100120123 sara
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) sess.get("user");
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine 10012013
            am.getTransaction().postChanges();
            r.refresh(Row.REFRESH_CONTAINEES);
            am.getTransaction().commit();
            
            //comunque cancello l'intervento
            r.remove();
            am.getTransaction().commit();
                       
            vo.executeQuery();
            /*20080715 FINE MOD*/


        } catch (Exception ex) {
            ex.printStackTrace();
            this.whenException("Impossibile cancellare l'intervento: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiView1" });
        }
        
        return "created";
    }


    public String onDeleteReferto() {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        ViewObject interv = am.findViewObject("Ref_SoInterventocitoView6");
        //eseguo il controllo per tutti gli interventi di quel referto
        /*     RowSetIterator iter=ViewHelper.getRowSetIterator(interv,"iter");
      RowSetIterator cnf_iter=null;*/
        try {


            if (livello == null)
                throw new Exception("Nessun referto da salvare");

            String voName;
            if (livello.intValue() == 2)
                voName = "Ref_SoRefertocito2livView4";
            else
                voName = "Ref_SoRefertocito1livView3";

            ViewObject ref = am.findViewObject(voName);

            Row r = ref.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto selezionato per la cancellazione");

            String ulss = (String) r.getAttribute("Ulss");
            String tpscr = (String) r.getAttribute("Tpscr");
            Integer idinvito = (Integer) r.getAttribute("Idinvito");


            if (livello.intValue() == 2)
                voName = "Ref_SoCodref2livc2View4";
            else
                voName = "Ref_SoCodref1livcView4";

            ViewObject cnf = am.findViewObject(voName);
            ViewObject allegati = am.findViewObject("Ref_SoAllegatoView1");
            ViewObject inviti = am.findViewObject("Ref_SoInvitoView2");
            Row[] result1 = inviti.getFilteredRows("Idinvito", r.getAttribute("Idinvito"));
            //cancellazione referto e annessi
            GestoreReferti gr = new GestoreReferti(am);
            String user = (String) ADFContext.getCurrent().getSessionScope().get("user");
            gr.deleteReferto(r, cnf, interv, allegati, inviti, am, user);
            //update invito relativo

            if (result1.length == 0)
                throw new Exception("Invito cui si riferisce il referto non trovato");
            // Row invito=result1[0];
            gr.updateInvito(ulss, tpscr, idinvito, null, //sugg=null
                            inviti, livello.intValue(), (String) ADFContext.getCurrent().getSessionScope().get("user"));


            //6. salvo
            //journaling
            //100120123 sara
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine 10012013
            am.getTransaction().commit();

            //7. comunico all'utente che l'operazione e' avvenuta con successo e che il soggetto risulta
            //counque presentato
            //this.handleMessages(FacesMessage.SEVERITY_INFO,
            //                    "Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");
            this.saveMessages("Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");
            

            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiView1");
            /*MOD 20/06/2007
      r=vo.getCurrentRow();
      if(r!=null)
        ViewHelper.restoreCurrentRow(vo,"Idinvito",r.getAttribute("Idinvito"));
        */
            ViewHelper.queryAndRestoreCurrentRow(vo);

        } catch (Exception ex) {
            //  ex.printStackTrace();
            this.whenException("Impossibile cancellare il referto: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiView1" });
        }
        /*   finally
    {
      iter.closeRowSetIterator();
      if(cnf_iter!=null)
        cnf_iter.closeRowSetIterator();
    }*/
        return "to_ref_ricerca";
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
            /* Row[] result=allegati.getFilteredRows("Idallegato",id);
        if(result.length==0) throw new Exception("Lettera con identificativo "+id+" non trovata");
        result[0].remove();*/
        }
    }


    protected Row insertOrUpdateDiagnosi(ViewObject vo, String gruppo, Integer idcnf, Parent_AppModule am,
                                         Integer referto, String ulss, String tpscr) {
        Row[] result = vo.getFilteredRows("Gruppo", gruppo);
        Row racdia;
        //se tale configuraizone non esiste la creo
        if (result.length == 0) {
            racdia = vo.createRow();
            vo.insertRow(racdia);
            racdia.setAttribute("Id", am.getNextIdConfReferto2liv());
            racdia.setAttribute("Gruppo", gruppo);
            racdia.setAttribute("Idreferto", referto);
            racdia.setAttribute("Ulss", ulss);
            racdia.setAttribute("Tpscr", tpscr);
        } else
            racdia = result[0];

        racdia.setAttribute("Idcnfref", idcnf);
        return racdia;
    }

    public void onSetDirty() {
        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean.setDirty(true);
    }

    protected void checkDateOrder(Row ref, Row invito) throws Exception {
        oracle.jbo.domain.Date d1 = (oracle.jbo.domain.Date) invito.getAttribute("Dtapp");
        oracle.jbo.domain.Date d2 = (oracle.jbo.domain.Date) ref.getAttribute("Dtcolposcopia");
        oracle.jbo.domain.Date d3 = (oracle.jbo.domain.Date) ref.getAttribute("Dtbiopsia");
        oracle.jbo.domain.Date d4 = (oracle.jbo.domain.Date) ref.getAttribute("Dtconcl");
        Date dd1 = null;
        Date dd2 = null;
        Date dd3 = null;
        Date dd4 = null;

        if (d1 == null)
            throw new Exception("La data dell'invito non risulta valorizzata");

        if (d1 != null) {
            Calendar c = DateUtils.getCalendar(d1.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd1 = c.getTime();
        }
        if (d2 != null) {
            Calendar c = DateUtils.getCalendar(d2.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd2 = c.getTime();
        }
        if (d3 != null) {
            Calendar c = DateUtils.getCalendar(d3.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd3 = c.getTime();
        }
        if (d4 != null) {
            Calendar c = DateUtils.getCalendar(d4.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd4 = c.getTime();
        }

        if (dd2 != null) {

            if (dd1.after(dd2))
                throw new Exception("La data della colposcopia (" + DateUtils.dateToString(dd2) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");
        }

        if (dd3 != null) {
            if (dd1.after(dd3))
                throw new Exception("La data dell'istologia bioptica (" + DateUtils.dateToString(dd3) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");
            if (dd2 != null) {
                if (dd2.after(dd3))
                    throw new Exception("La data dell'istologia bioptica (" + DateUtils.dateToString(dd3) +
                                        ") non puo' essere precedente alla data della colposcopia (" +
                                        DateUtils.dateToString(dd2) + ")");
            }
        }

        if (dd4 != null) {
            if (dd1.after(dd4))
                throw new Exception("La data delle conclusioni (" + DateUtils.dateToString(dd4) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");
            if (dd2 != null) {
                if (dd2.after(dd4))
                    throw new Exception("La data delle conclusioni (" + DateUtils.dateToString(dd4) +
                                        ") non puo' essere precedente alla data della colposcopia (" +
                                        DateUtils.dateToString(dd2) + ")");
            }
            if (dd3 != null) {
                if (dd3.after(dd4))
                    throw new Exception("La data delle conclusioni (" + DateUtils.dateToString(dd4) +
                                        ") non puo' essere precedente alla data dell'istologia bioptica (" +
                                        DateUtils.dateToString(dd3) + ")");
            }
        }

    }

    /**
     * Metodo che analizza tutte le diagnosi istologiche del referto in questione
     * ed imposta la diagnosi peggiore utilizzando la piu' grave (l'ordine di gravita'
     * e' dato dall'ordine di visualizzazione)
     * @throws java.lang.Exception
     * @param
     * @param r record con il referto di 2 livello (che deve essere current row)
     */
    protected void setDiagnosiPeggiore(Row r) throws Exception {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        if (livello == null || livello.intValue() == 1)
            return;

        //Istologia bioptica
        ViewObject vo = am.findViewObject("Ref_SoCodref2livc2View4");
        //   ViewObject cnf=am.findViewObject("Ref_SoCnfRef2livView2");
        Row[] result = vo.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_BIODIA_2L);
        Integer diagnosi = null;
        int ordine = -1;
        if (result.length > 0) {
            diagnosi = ((Integer) result[0].getAttribute("Idcnfref"));
            if (diagnosi != null) {

                Integer ord =
                    this.getOrdine((String) r.getAttribute("Ulss"), (String) r.getAttribute("Tpscr"),
                                   ConfigurationConstants.NOME_GRUPPO_BIODIA_2L, diagnosi, am);
                if (ord != null)
                    ordine = ord.intValue();
            }
        }

        //istologie chirurgiche
        ViewObject interv = am.findViewObject("Ref_SoInterventocitoView6");
        RowSetIterator iter = interv.createRowSetIterator("diagnosi_iter");
        try {
            while (iter.hasNext()) {
                Row y = iter.next();
                Integer diagnosi_istochir = (Integer) y.getAttribute("Istdia");
                Integer ord = null;
                if (diagnosi_istochir != null) {
                    ord =
                        this.getOrdine((String) r.getAttribute("Ulss"), (String) r.getAttribute("Tpscr"),
                                       ConfigurationConstants.NOME_GRUPPO_ISTDIA_2L, diagnosi_istochir, am);
                }
                if (ord != null) {
                    //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                    //la diagnosi peggiore
                    if (ordine < ord.intValue()) {
                        diagnosi = diagnosi_istochir;
                        ordine = ord.intValue();
                    }
                }

            }


            //aggiorno l adiagnosi peggiore
            this.insertOrUpdateDiagnosi(vo, ConfigurationConstants.NOME_GRUPPO_RACDIA_2L, (Integer) diagnosi, am,
                                        (Integer) r.getAttribute("Idreferto"), (String) r.getAttribute("Ulss"),
                                        (String) r.getAttribute("Tpscr"));
        } catch (Exception ex) {
            throw ex;
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
        ViewObject cnf = am.findViewObject("Ref_SoCnfRef2livView2");
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

    public String onRef_back() {
        return "to_ref_ricerca";
    }

    public void downloadRiassunto2liv(FacesContext fc, OutputStream os) throws Exception {
        int report_n = SystemReport.REFERTO_RIASSUNTIVO_2L;

        File pdf = null;
        HashMap h = null;
        try {

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertocito2livView4Iterator");
            ViewObject ref = refVoIter.getViewObject();
            Row r = ref.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto selezionato per la visualizzazione");
            SystemReport report = new SystemReport(report_n);
            h = new HashMap();
            h.put("id_referto", new Double(((Integer) r.getAttribute("Idreferto")).doubleValue()));
            Ref_AppModule am = (Ref_AppModule) ref.getApplicationModule();
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
