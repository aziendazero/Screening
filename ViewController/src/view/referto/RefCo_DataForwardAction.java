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

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import model.common.Parent_AppModule;
import model.common.RefCo_AppModule;
import model.common.Ref_SoEndoscopiaViewRow;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.ViewHelper;

import model.datacontrol.AccCo_RicParam;
import model.datacontrol.Ref_2livBean;
import model.datacontrol.Ref_SearchBean;
import model.datacontrol.Sogg_RicParam;

import model.filters.ViewObjectFilters;

import model.referto.common.Ref_SoInterventocolonViewRow;
import model.referto.common.Ref_SoRefertocolon2livViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.context.RequestContext;

import view.commons.action.Parent_DataForwardAction;

import view.reports.SystemReport;

import view.util.Utility;

public abstract class RefCo_DataForwardAction extends Parent_DataForwardAction {
    /**
     * Metdodo invocato dopo l'update del model per mantenere l'interfaccia coerente
     *
     * @param
     */
    protected abstract void afterUpdateModel();

    protected void beforeNavigate(String dest) throws Exception {
        //le modifiche sono gia' state salvate o annullate
        Ref_2livBean bean2 =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean2.setDirty(false);
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        //@codecoach:disable nextline
        //      Ref_SearchBean bean=(Ref_SearchBean)BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
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
                if (livello == 2) {
                    try {
                        AccessManager.checkPermission("SOAcc2Liv");
                    } catch (IllegalAccessException ex) {
                        // non ha il permesso, permetto di navigare ma non
                        // imposto nulla
                        return;
                    }
                }
                AccCo_RicParam beanAcc =
                    (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();
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
                    beanAcc.setIdCprel(idCprel);
                beanAcc.setLivello(livello.toString());
                beanAcc.setNavigazione(Boolean.TRUE);
            }
        } //if r!=null


        //tutte le destinazioni all'interno della refertazione
        /* HashMap internal_dest=new HashMap();
        internal_dest.put("refCo_rice",Boolean.TRUE);
        internal_dest.put("refCo_toRe",Boolean.TRUE);
        internal_dest.put("refCo_toEn",Boolean.TRUE);
        internal_dest.put("refCo_toIs",Boolean.TRUE);
        internal_dest.put("refCo_toCo",Boolean.TRUE);
        internal_dest.put("refCo_toIn",Boolean.TRUE);
        internal_dest.put("refCo_toDi",Boolean.TRUE);
        internal_dest.put("refCo_toRx",Boolean.TRUE);

        //se sto uscendo dalla refertazione restto il bean
        Object b=internal_dest.get(dst);*/
        if (!dest.startsWith("to_refCo_")) {
            Ref_SearchBean bean =
                (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
            bean.reset();
        }
    }

    /**
     * Prima di un eventuale salvataggio automatico salvo io a modo mio
     * (per mantenere la consistenza dei dati tra interfaccia e DB)
     * @param
     */
    protected boolean beforeSave() {
        // TODO:  Override this view.commons.Parent_DataForwardAction method

        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        try {

            if (livello == null)
                throw new Exception("Nessun referto da salvare");

            String voName;
            if (livello == 2)
                voName = "Ref_SoRefertocolon2livView1";
            else
                voName = "Ref_SoRefertocolon1livView1";

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

    protected void beforeSavingReferto() throws Exception {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");


        if (livello == null)
            throw new Exception("Nessun referto da salvare");


        ViewObject ref = am.findViewObject("Ref_SoRefertocolon2livView1");
        Row r = ref.getCurrentRow();
        if (r == null)
            return;


        ref = am.findViewObject("Ref_SoInvitoView2");
        Row invito = ref.first();

        if (livello == 2)
            this.checkDateOrder(r, invito, am.findViewObject("Ref_SoEndoscopiaView1"));

        if (r != null && r.getAttribute("Completo") != null &&
            ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
            throw new Exception("Referto gia' chiuso, le modifiche sono state annullate");


        //impostazione dati ultima modifica
        r.setAttribute("Dtultmodifica", DateUtils.getOracleDateNow());
        r.setAttribute("Opultmodifica", ADFContext.getCurrent().getSessionScope().get("user"));


        this.updateDB();

    }

    protected void checkDateOrder(Row ref, Row invito, ViewObject endoscopie) throws Exception {
        oracle.jbo.domain.Date d1 = (oracle.jbo.domain.Date) invito.getAttribute("Dtapp");
        oracle.jbo.domain.Date d2 = (oracle.jbo.domain.Date) ref.getAttribute("DtRxColon");
        oracle.jbo.domain.Date d4 = (oracle.jbo.domain.Date) ref.getAttribute("Dtconcl");
        //01042013 Gaion: aggiunta parte per la colon tac
        oracle.jbo.domain.Date d5 = (oracle.jbo.domain.Date) ref.getAttribute("DtColonTac");
        Date dd1 = null;
        Date dd2 = null;
        Date dd4 = null;
        Date dd5 = null;

        if (d1 == null)
            throw new Exception("La data dell'invito non risulta valorizzata");

        if (d1 != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(d1.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd1 = c.getTime();
        }
        if (d2 != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(d2.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd2 = c.getTime();
        }

        if (d4 != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(d4.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd4 = c.getTime();
        }

        if (d5 != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(d5.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd5 = c.getTime();
        }


        if (dd2 != null) {

            if (dd1.after(dd2))
                throw new Exception("La data del clisma opaco (" + DateUtils.dateToString(dd2) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");
        }

        if (dd5 != null) {

            if (dd1.after(dd5))
                throw new Exception("La data della colon TAC (" + DateUtils.dateToString(dd5) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");
        }

        if (dd4 != null) {
            if (dd1.after(dd4))
                throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd4) +
                                    ") non puo' essere precedente alla data dell'invito (" +
                                    DateUtils.dateToString(dd1) + ")");
            if (dd2 != null) {
                if (dd2.after(dd4))
                    throw new Exception("La data delle conclusioni del 2 livello (" + DateUtils.dateToString(dd4) +
                                        ") non puo' essere precedente alla data del clisma opaco(" +
                                        DateUtils.dateToString(dd2) + ")");
            }
        }


        RowSetIterator iter = null;
        try {
            iter = ViewHelper.getRowSetIterator(endoscopie, "iter_endo");
            Date dd3 = null;
            oracle.jbo.domain.Date d3 = null;
            while (iter.hasNext()) {
                Row r = iter.next();
                d3 = (oracle.jbo.domain.Date) r.getAttribute("Dtendo");
                if (d3 != null) {
                    Calendar c = DateUtils.getCalendar(d3.getValue());
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    dd3 = c.getTime();
                }

                if (dd3 != null) {

                    if (dd1.after(dd3))
                        throw new Exception("La data di un'endoscopia (" + DateUtils.dateToString(dd3) +
                                            ") non puo' essere precedente alla data dell'invito (" +
                                            DateUtils.dateToString(dd1) + ")");

                    if (dd4 != null) {
                        if (dd3.after(dd4))
                            throw new Exception("La data delle conclusioni del 2 livello (" +
                                                DateUtils.dateToString(dd4) +
                                                ") non puo' essere precedente alla data di un'endoscopia (" +
                                                DateUtils.dateToString(dd3) + ")");
                    }


                }


            }
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

    }

    /**
     * Invocato al momento della chiusura, controlla che i
     * dati necessari siano tutti presenti
     * @throws java.lang.Exception
     * @param endo
     * @param ref
     */
    protected void checkMandatoryData(Row ref, ViewObject endo) throws Exception {
        if (ref == null)
            return;

        //controllo sull'rxcolon: data, medico e conclusioni
        Ref_SoRefertocolon2livViewRow r = (Ref_SoRefertocolon2livViewRow) ref;
        if (ConfigurationConstants.DB_TRUE.equals(r.getRxColon())) {
            if (r.getDtRxColon() == null)
                throw new Exception("Inserire la data del rx colon");
            if (r.getIdmedicoRx() == null)
                throw new Exception("Inserire il medico che ha eseguito il rx colon");
            if (r.getRxConcl() == null || r.getRxConcl() <= 0)
                throw new Exception("Inserire le conclusioni del rx colon");

        }

        //1504200123 Gaion: aggiunta sezione per la colon tac
        //controllo sull'colon TAC: data, medico e conclusioni
        if (ConfigurationConstants.DB_TRUE.equals(r.getColonTac())) {
            if (r.getDtColonTac() == null)
                throw new Exception("Inserire la data della colon TAC");
            if (r.getIdmedTac() == null)
                throw new Exception("Inserire il medico che ha eseguito la colon TAC");
            if (r.getTacConcl() == null || r.getTacConcl() <= 0)
                throw new Exception("Inserire le conclusioni della colon TAC");
        }
        //fine 15042013

        RowSetIterator iter = null;
        try {
            iter = ViewHelper.getRowSetIterator(endo, "endo_iter2");
            while (iter.hasNext()) {
                Ref_SoEndoscopiaViewRow e = (Ref_SoEndoscopiaViewRow) iter.next();
                Integer n = e.getNEndo();
                if (e.getDtendo() == null)
                    throw new Exception("Inserire la data dell'endoscopia n." + n);
                if (e.getIdmedico() == null)
                    throw new Exception("Inserire il medico responsabile dell'endoscopia n." + n);
                if (e.getRegione() == null || e.getRegione() <= 0)
                    throw new Exception("Inserire la regione raggiunta dall'endoscopia n." + n);

            }
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }


    }

    /**
     * Applica le conseguenze della chiusura di un referto di 2 livello
     * @throws java.lang.Exception
     * @param  contesto
     * @param am application module
     * @param r record con il referto da chiudere
     */
    protected void conseguenzeChiusura(Row r, RefCo_AppModule am, String to_do) throws Exception {

        if (r == null)
            throw new Exception("Nessun referto da chiudere");
        ViewObject primol = am.findViewObject("Ref_SoInvito1LivCOView1");
        //String to_do = (String) ADFContext.getCurrent().getRequestScope().get("ref_lettera");        //se ho degli interventi chiusi non vado a modifcare il richiamo
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
     * Metodod che adatta l'interfaccaia ai dati letti dal db
     * @param
     */
    protected abstract void loadFromDB();

    protected void setAppModule() {
        this.amName = "RefCo_AppModule";
    }

    public void onApply(ActionEvent actionEvent) {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        boolean successful = true;

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
        } catch (Exception ex) {
            //  ex.printStackTrace();
            successful = false;
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
        }

        afterSave();

        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
        String voName;
        if (livello == 2)
            //da sistemare
            voName = "Ref_SoRefertocolon2livView1";
        else
            voName = "Ref_SoRefertocolon1livView1";

        ViewObject ref = am.findViewObject(voName);
        Row r = ref.getCurrentRow();

        // mauro 02/12/2010, modifica per assicurare aggiornamento esito invito
        if (successful) {
            Integer idInvito = (Integer) r.getAttribute("Idinvito");
            GestoreReferti.updateEsito(am, idInvito);
        }
        //

        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean.setDirty(false);

        ViewObject vo2 = am.findViewObject("Ref_SoEndoscopiaView1");
        Row r2 = vo2.getCurrentRow();
        //ripristino la riga corrente nei referti
        ViewHelper.queryAndRestoreCurrentRow(vo);

        //ed eventualemente dell'endoscopia (basandomi sul dato vecchio)
        if (r2 != null)
            ViewHelper.restoreCurrentRow(vo2, "Idendo", r2.getAttribute("Idendo"));


        this.loadFromDB();

    }

    /**
     * Metodo che si pccupa della chiusura di un refrto di 2 livello,
     * da sovrascrivere localmete per il primo livello
     * @param
     */
    public void onClose(ClientEvent clientEvent) {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertocolon2livView1");
        Ref_SoRefertocolon2livViewRow r = null;
        String to_do = (String)clientEvent.getParameters().get("ref_lettera");

        try {
            r = (Ref_SoRefertocolon2livViewRow) ref.getCurrentRow();

            //controllo di conformita'
            if (r.getIdsugg2l() == null || ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getIdsugg2l()))
                throw new Exception("Non e' stata segnalata nessuna raccomandazione conclusiva");

            if (r.getConclusioni() == null || ConfigurationConstants.DB_FALSE.equals(r.getConclusioni()))
                throw new Exception("Le conclusioni non sono state compilate");

            if (r.getIdmedConcl() == null)
                throw new Exception("Medico responsabile delle raccomandazioni conclusive non inserito");
            if (r.getDtconcl() == null)
                throw new Exception("Non e' stata inserita la data della raccomandazione conclusiva");


            //aggiorno il db in conseguenza alle selezioni di interfaccia
            this.beforeSavingReferto();

            this.checkMandatoryData(r, am.findViewObject("Ref_SoEndoscopiaView1"));

            //applico le consguenze della chiusura
            this.conseguenzeChiusura(r, am, to_do);

            //imposto la diagnosi peggiore
            this.setDiagnosiPeggiore(r);


            //il referto e' completo
            r.setCompleto(ConfigurationConstants.DB_TRUE);
            //salvo il tutto
            //09-01-2013 sara: aggiunto journaling
            //journalig
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

            FacesContext fc = FacesContext.getCurrentInstance();
            String refreshpage = fc.getViewRoot().getViewId();
            ViewHandler ViewH = fc.getApplication().getViewHandler();
            UIViewRoot UIV = ViewH.createView(fc,refreshpage);
            UIV.setViewId(refreshpage);
            fc.setViewRoot(UIV);
            
        } catch (Exception ex) {
            // ex.printStackTrace();
            if (r != null) {

                //il referto non e' completo
                r.setCompleto(ConfigurationConstants.DB_FALSE);
            }
            this.handleException("Impossibile chiudere il referto: " + ex.getMessage(), null);

        }
    }

    public String onConfirm() {
        this.onApply(null);
        return "to_refCo_ricerca";
    }

    public void onCreateEndo(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        ViewObject ref = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) ref.getApplicationModule();
        try {
        
            ViewObject invito = am.findViewObject("Ref_SoInvitoView2");
            //se il referto non c'e' segnalo errore
            Row r = ref.first();
            if (r == null)
                throw new Exception("Nessun referto inserito");

            Row inv = invito.first();
            if (inv == null)
                throw new Exception("Invito cui si riferisce il referto non trovato");

            //endoscopie di questo referto
            ViewObject endo = am.findViewObject("Ref_SoEndoscopiaView1");
            int n = endo.getRowCount();
            Ref_SoEndoscopiaViewRow e = (Ref_SoEndoscopiaViewRow) endo.createRow();
            endo.insertRow(e);
            e.setIdendo(am.getNextEndoscopia());
            e.setUlss((String) r.getAttribute("Ulss"));
            e.setTpscr((String) r.getAttribute("Tpscr"));
            e.setNEndo(new Integer(n + 1));
            e.setDtendo((oracle.jbo.domain.Date) inv.getAttribute("Dtapp"));
            e.setGrAspetto(ConfigurationConstants.NOME_GRUPPO_ASPETTO);
            e.setGrCIstologia(ConfigurationConstants.NOME_GRUPPO_ISTO_CANCRO);
            e.setGrComplicanze(ConfigurationConstants.NOME_GRUPPO_COMPLICANZE);

            e.setGrDiametro(ConfigurationConstants.NOME_GRUPPO_DIAMETRO);
            e.setGrDisplasia(ConfigurationConstants.NOME_GRUPPO_DISPLASIA);
            e.setGrIndicazioni(ConfigurationConstants.NOME_GRUPPO_INDICAZIONI);
            e.setGrIstologia(ConfigurationConstants.NOME_GRUPPO_ISTO_POLIPI);
            e.setGrMotivo(ConfigurationConstants.NOME_GRUPPO_MOTIVO);
            e.setGrQualita(ConfigurationConstants.NOME_GRUPPO_QUALITA);
            e.setGrRegione(ConfigurationConstants.NOME_GRUPPO_REGIONE);
            e.setGrAltreLesioni(ConfigurationConstants.NOME_GRUPPO_LESIONI);

            e.setEstensione(ConfigurationConstants.DB_FALSE);
            e.setSedazione(ConfigurationConstants.DB_FALSE);
            e.setProcedura(ConfigurationConstants.DB_FALSE);

            e.setNPolipi(ConfigurationConstants.DB_FALSE);
            e.setDiametroMax(ConfigurationConstants.DB_FALSE);
            e.setPolipo1(ConfigurationConstants.DB_FALSE);
            e.setPolipo2(ConfigurationConstants.DB_FALSE);
            e.setPolipo3(ConfigurationConstants.DB_FALSE);
            e.setCancro(ConfigurationConstants.DB_FALSE);
            e.setAltreLesioni(ConfigurationConstants.DB_FALSE);
            this.resetPolipoCancro(e, "C");
            this.resetPolipoCancro(e, "P1");
            this.resetPolipoCancro(e, "P2");
            this.resetPolipoCancro(e, "P3");

            this.filter();
            
            Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:Qualita', 'pt1:Regione', 'pt1:Motivo', 'pt1:Complicanze', 'pt1:Indicazioni'])");
        } catch (Exception ex) {
            // ex.printStackTrace();
            this.whenException("Impossibile aggiungere una nuova endoscopia: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoEndoscopiaView1" });
        }
    }
    
    protected void  filter(){ }

    /**
     * Creazione di un nuovo referto
     * @param
     */
    public void onCreateReferto(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();

        try {
            ViewObject ref = voIter.getViewObject();
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
            if (inv.getAttribute("Idcentroprelievo") == null)
                throw new Exception("Il centro di prelievo dell'invito non risulta valorizzato");

            gr.nuovoReferto2livCO(invito, ref, (String) ADFContext.getCurrent().getSessionScope().get("user"));

        } catch (Exception ex) {
            //  ex.printStackTrace();
            this.whenException("Impossibile inserire un nuovo referto: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiCOView1" });
        }

    }

    /**
     * Cancella l'endoscopia correntemente visualizzata e rende la numerazione
     * delle endo nuovamete consistente
     * @param
     */
    public void onDeleteEndo(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        ViewObject referti = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) referti.getApplicationModule();
        
        ViewObject vo = am.findViewObject("Ref_SoEndoscopiaView1");
        Ref_SoEndoscopiaViewRow r = (Ref_SoEndoscopiaViewRow) vo.getCurrentRow();

        Row ref = referti.getCurrentRow();
        try {
            if (ref == null || r == null)
                throw new Exception("Nessuna endoscopia da cancellare");

            if (ConfigurationConstants.DB_TRUE.equals(ref.getAttribute("Completo")))
                throw new Exception("Il referto risulta chiuso");

            //numero dell'endoscopia
            int n = r.getNEndo() == null ? 0 : r.getNEndo();
            //numero di endoscopie prima della cancellazione
            int c = vo.getRowCount();

            //e cancello l'endoscopia
            r.remove();

            //sto cancellando l'unica o l'ultima endoscopia,
            //nessun problem di numerazione,altrimenti
            if (c > 1 &&  c != n) {
                //devo rinumerare le endocopie successive
                String update =
                    "UPDATE SO_ENDOSCOPIA SET N_ENDO=N_ENDO-1 " + "WHERE IDREFERTO=" + ref.getAttribute("Idreferto") +
                    " AND N_ENDO>=" + n;
                am.getTransaction().executeCommand(update);
            }
            //09-01-2013 sara: aggiunto journaling
            //journalig
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
            vo.executeQuery();

        } catch (Exception ex) {
            this.whenException("Impossibile cancellare l'endoscopia: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiCOView1" });
        }

        try {
            this.updateConclusioni();
            am.getTransaction().commit();
        } catch (Exception ex) {

            this.handleException("Endoscopia cancellata, ma impossibile aggiornare automaticamente le conclusioni del referto: " +
                                 ex.getMessage(), am,
                                 new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoEndoscopiaView1" });
        }
    }

    public String onDeleteIntervento() {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInterventocolonView1");
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
                ViewObject ref = am.findViewObject("Ref_SoRefertocolon2livView1");
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
                n = (Integer) r.getAttribute("NInt");

            Integer idref = (Integer) r.getAttribute("Idreferto");

            // mauro 15/12/2010, prima di cancellare l'intervento faccio il backup
            Integer idInt = (Integer) r.getAttribute("Idint");
            String sql =
                "insert into so_interventocolon_bck " + "select * from so_interventocolon where idint = " +
                idInt.toString();
            am.getTransaction().executeCommand(sql);
            // mauro 15/12/2010, fine modifica

            //comunque cancello l'intervento
            r.remove();

            //aggiorno la numerazione degli interventi
            String update =
                "UPDATE SO_INTERVENTOCOLON SET N_INT=N_INT-1 " + "WHERE IDREFERTO=" + idref + " AND N_INT > " + n;
            am.getTransaction().executeCommand(update);

            //09-01-2013 sara: aggiunto journaling
            //journalig
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
            vo.executeQuery();


        } catch (Exception ex) {
            this.whenException("Impossibile cancellare l'intervento: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoEndoscopiaView1",
                                              "Ref_SoInterventocolonView1" });
        }
        
        return null;
    }

    /**
     * Cancella un referto di secondo livello con tutti i dati correlati
     * @param
     */
    public String onDeleteReferto() {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();

        //

        try {


            ViewObject ref = am.findViewObject("Ref_SoRefertocolon2livView1");

            Row r = ref.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto selezionato per la cancellazione");

            String ulss = (String) r.getAttribute("Ulss");
            String tpscr = (String) r.getAttribute("Tpscr");
            Integer idinvito = (Integer) r.getAttribute("Idinvito");

            ViewObject endo = am.findViewObject("Ref_SoEndoscopiaView1");
            ViewObject interv = am.findViewObject("Ref_SoInterventocolonView1");
            ViewObject allegati = am.findViewObject("Ref_SoAllegatoView1");
            ViewObject inviti = am.findViewObject("Ref_SoInvitoView2");
            //MOD 20/06/2007
            // Row[] result1=inviti.getFilteredRows("Idinvito",r.getAttribute("Idinvito"));

            //cancellazione referto e annessi
            GestoreReferti gr = new GestoreReferti(am);
            String user = (String) ADFContext.getCurrent().getSessionScope().get("user");

            //vanno passati gli interventi
            //////////////////////////////////////////////////////
            gr.deleteRefertoCO(r, endo, interv, allegati, 2, am, user);
            //update invito relativo

            //MOD 20/06/2007
            // if(result1.length==0)
            if (inviti.first() == null)
                throw new Exception("Invito cui si riferisce il referto non trovato");
            // Row invito=result1[0];
            gr.updateInvito(ulss, tpscr, idinvito, null, //sugg=null
                            inviti, 2, (String) ADFContext.getCurrent().getSessionScope().get("user"));


            //6. salvo
            //09-01-2013 sara: aggiunto journaling
            //journalig
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine sara
            am.getTransaction().commit();

            //7. comunico all'utente che l'operazione e' avvenuta con successo e che il soggetto risulta
            //counque presentato
           // this.handleMessages(FacesMessage.SEVERITY_INFO, "Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");
           this.saveMessages("Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");

            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
            /* MOD 20/06/2007
         r=vo.getCurrentRow();
        if(r!=null)
          ViewHelper.restoreCurrentRow(vo,"Idinvito",r.getAttribute("Idinvito"));
          */
            ViewHelper.queryAndRestoreCurrentRow(vo);

        } catch (Exception ex) {
            // ex.printStackTrace();
            this.whenException("Impossibile cancellare il referto: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoEndoscopiaView1" });
        }

        return "to_refCo_ricerca";

    }

    public String onInterm_dett() {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoEndoscopiaIntermView1");

        ViewObject invitiVo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
        Row r = invitiVo.getCurrentRow();
        Integer idInvito = (Integer) r.getAttribute("Idinvito");
        String codts = (String) r.getAttribute("Codts");

        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        //rieseguo il vo in modo che abbia solo una riga
        String whereStr =
            "ULSS = '" + ulss + "' AND " + "TPSCR = '" + tpscr +
            "' AND " + "IDINVITO = " + idInvito + " AND " + "CODTS = '" +
            codts + "' ";
        vo.setWhereClause(whereStr); 
        vo.executeQuery();

        //filtro i dati per la compilazione
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtapp");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.dateValue());
        else {
            data = DateUtils.dateToString(new Date());
        }

        //LETTORE
        vo = am.findViewObject("Ref_SoOpmedicoView1");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_GASTROENTEROLOGO, ulss, tpscr);
        //COMPLICAZIONI
        vo = am.findViewObject("Ref_SoCnfRef2livENDCOMView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //INDICAZIONI
        vo = am.findViewObject("Ref_SoCnfRef2livENDINDView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //MOTIVO DI INCOMPLETEZZA
        vo = am.findViewObject("Ref_SoCnfRef2livENDMOTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //QUALITA
        vo = am.findViewObject("Ref_SoCnfRef2livENDQLTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //REGIONE RAGGIUNTA 8meno le sedi multiple)
        vo = am.findViewObject("Ref_SoCnfRef2livENDREGView1");
        String where = "TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' ";
        if (data != null)
            where +=
                " AND (DTFINEVALIDITA IS NULL OR  DTFINEVALIDITA>TO_DATE('" + data + "','" + DateUtils.DATE_PATTERN +
                "'))";
        where += " AND IDCNFREF2L<>" + ConfigurationConstants.CODICE_REGIONE_MULTIPLE;
        vo.setWhereClause(where);
        vo.executeQuery();
        //DIMENSIONI MASSIME
        vo = am.findViewObject("Ref_SoCnfRef2livENDDIMView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ASPETTO
        vo = am.findViewObject("Ref_SoCnfRef2livPOLASPView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ISTOLOGIA DEI POLIPI
        vo = am.findViewObject("Ref_SoCnfRef2livISTOCOView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ISTOLOGIA DEL CANCRO
        vo = am.findViewObject("Ref_SoCnfRef2livCARISTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //DISPLASIA
        vo = am.findViewObject("Ref_SoCnfRef2livDISPCOView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //altre lesioni
        vo = am.findViewObject("Ref_SoCnfRef2livENDLESView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //PATOLOGO
        vo = am.findViewObject("Ref_SoOpmedicoView2");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_PATOLOGO, ulss, tpscr);

        ADFContext.getCurrent().getSessionScope().put("LINK_DETT", Boolean.TRUE);
        ADFContext.getCurrent().getSessionScope().put("CODTS", codts);
        return "to_refCo_intermedio_dett";
    }

    public String onRollback() {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();

        am.doRollback(new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoEndoscopiaView1" });

        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean.setDirty(false);
        return "to_refCo_ricerca";
    }

    public String onRef_back() {
        return "to_refCo_ricerca";
    }

    /**
     * Metodo che si occup adi sbolccare un referto per ulteriori modifiche
     * @param
     */
    public void onReopen() {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        Row r;
        try {

            if (livello == null)
                throw new Exception("Nessun referto da riaprire");

            AccessManager.checkPermission("SORiaperturaReferti" + livello + "Liv");

            String voName;
            if (livello == 2)
                voName = "Ref_SoRefertocolon2livView1";
            else
                voName = "Ref_SoRefertocolon1livView1";

            ViewObject ref = am.findViewObject(voName);
            // r=ref.getCurrentRow();
            r = ref.first();

            if (r == null)
                return;

            //09-01-2013 sara: aggiunto journaling
            //journalig
            Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) sess.get("user");
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }
            //fine sara
            //////////////////////////////////////////
            //imposto il richiamo
            GestoreReferti gr = new GestoreReferti(am);
            //annullo il richiamo (se e' un secondo livello non devono esserci intreventi chiusi non ci sono interventi chusi)
            if (livello == 1 ||
                (livello == 2 && !ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi")))) {


                gr.updateInvito((String) r.getAttribute("Ulss"), (String) r.getAttribute("Tpscr"),
                                (Integer) r.getAttribute("Idinvito"), null, //sugg=null
                                am.findViewObject("Ref_SoInvitoView2"), 2, //non importa il livello, tanto il sugg e' null
                                (String) ADFContext.getCurrent().getSessionScope().get("user"));

            }

            //se la lettera non risulta spedita la cancello
            if (r.getAttribute("Dtspedizione") ==
                null) {
                //per il secondo livello non devo avere interventi chiusi
                if (livello == 1 ||
                    (livello > 1 && !ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi"))))
                    gr.deleteLettera(r);
            }
            /////////////////////////////////
            if (livello == 2) {
                try {
                    r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
                    am.getTransaction().commit();
                } catch (Exception ex) {
                    this.whenException("Impossibile riaprire il referto: " + ex.getMessage(), am,
                                       new String[] { "Ref_SoInvitiPerRefertiCOView1" });
                }
            } else {
                RowSetIterator iter = ViewHelper.getRowSetIterator(ref, "provette_iter3");
                try {
                    Row r1 = null;
                    while ((r1 = iter.next()) != null) {
                        r1.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
                    }

                    am.getTransaction().commit();
                } catch (Exception ex) {
                    this.whenException("Impossibile riaprire il referto: " + ex.getMessage(), am,
                                       new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoEndoscopiaView1" });
                } finally {
                    if (iter != null)
                        iter.closeRowSetIterator();
                }
            }
                     
            FacesContext fc = FacesContext.getCurrentInstance();
            String refreshpage = fc.getViewRoot().getViewId();
            ViewHandler ViewH = fc.getApplication().getViewHandler();
            UIViewRoot UIV = ViewH.createView(fc,refreshpage);
            UIV.setViewId(refreshpage);
            fc.setViewRoot(UIV);

        } catch (Exception ex) {

            this.handleException("Impossibile riaprire il referto: " + ex.getMessage(), null);
            this.loadFromDB();
        }

    }

    @Override
    public String onRichnav(String dest, HashMap params) {
        //prima si deve impostare il nome dell'application module
        this.setAppModule();

        Map req = ADFContext.getCurrent().getRequestScope();
        req.putAll(params);

        //lettura della destinzione
        String destination = dest;
        ApplicationModule am =
            BindingContext.getCurrent().findDataControl(amName + "DataControl").getApplicationModule();
        boolean modP = am.getTransaction().isDirty();

        //controllo anche eventuali variazioni all'interfaccia, registrate tramite il bean
        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        boolean modP2 = bean.isDirty();


        //se ci sono modifiche non salvate...
        if (modP || modP2) { //imposto l'attributo destinazione e lascio al metodo onLoad la gestione
            req.put("destNav", destination);
            ADFContext.getCurrent().getViewScope().put("destNav", dest);
            RequestContext.getCurrentInstance().addPartialTarget(getFvDest());
            Utility.addScriptOnPartialRequest("wantToSaveOnLoad()");
            return "error";
        } else { //altriemnti navigo verso la destinzione

            try {
                this.beforeNavigate(destination);
                //e navigo verso la destinazione
                // .getHttpServletRequest().getRequestDispatcher(destination).forward(.getHttpServletRequest(), .getHttpServletResponse());
                if (destination == null)
                    throw new Exception("destinazione non specificata");
                /* String suffix = "";
                if (destination.indexOf("?") >= 0)
                    suffix = "_p";
                int index = destination.indexOf(".");
                if (index >= 0)
                    destination = destination.substring(0, index);
                .setActionForward("to_" + destination + suffix);*/
            } catch (Exception ex) {
                this.handleException("Impossibile navigare verso la destinazione " + destination + ": " +
                                     ex.getMessage(), null);
            }
        }

        return dest;
    }

    protected void doRollback() {
        Parent_AppModule am =
            (Parent_AppModule) BindingContext.getCurrent().findDataControl(amName +
                                                                           "DataControl").getApplicationModule();

        am.doRollback(new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoEndoscopiaView1" });

    }

    public void onSetDirty() {
        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean.setDirty(true);
    }

    protected void processUpdateModel() {
        // TODO:  Override this oracle.adf.controller.struts.actions.DataAction method
       // super.processUpdateModel();
        this.afterUpdateModel();
    }

    protected void resetPolipoCancro(Row r, String prefix) {
        if (!ConfigurationConstants.DB_FALSE.equals(r.getAttribute(prefix + "Sede")))
            r.setAttribute(prefix + "Sede", ConfigurationConstants.DB_FALSE);
        if (r.getAttribute(prefix + "Aspetto") != null &&
            !ConfigurationConstants.DB_FALSE.equals(r.getAttribute(prefix + "Aspetto")))
            r.setAttribute(prefix + "Aspetto", ConfigurationConstants.DB_FALSE);
        if ("C".equals(prefix)) {
            if (r.getAttribute(prefix + "Dimensioni") != null)
                r.setAttribute(prefix + "Dimensioni", null);
        } else {
            if (r.getAttribute(prefix + "Diametro") != null)
                r.setAttribute(prefix + "Diametro", null);
            if (!ConfigurationConstants.DB_FALSE.equals(r.getAttribute(prefix + "Displasia")))
                r.setAttribute(prefix + "Displasia", ConfigurationConstants.DB_FALSE);
        }

        if (!ConfigurationConstants.DB_FALSE.equals(r.getAttribute(prefix + "Istologia")))
            r.setAttribute(prefix + "Istologia", ConfigurationConstants.DB_FALSE);
        if (r.getAttribute(prefix + "Note") != null && ((String) r.getAttribute(prefix + "Note")).length() > 0)
            r.setAttribute(prefix + "Note", null);
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
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");
        if (livello == null || livello == 1)
            return;

        RowSetIterator iter = null;
        RowSetIterator iter2 = null;
        try {
            //endoscopie
            ViewObject endo = am.findViewObject("Ref_SoEndoscopiaView1");
            iter = ViewHelper.getRowSetIterator(endo, "endo_iter");
            Integer diagnosi = null;
            int ordine = -1;
            while (iter.hasNext()) {
                Ref_SoEndoscopiaViewRow e = (Ref_SoEndoscopiaViewRow) iter.next();
                Integer ord = null;
                //polipo1
                {
                    ord =
                        this.getOrdine(e.getUlss(), e.getTpscr(), ConfigurationConstants.NOME_GRUPPO_ISTO_POLIPI,
                                       e.getP1Istologia(), am);
                    if (ord != null) {
                        //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                        //la diagnosi peggiore
                        if (ordine < ord.intValue()) {
                            diagnosi = new Integer(e.getP1Istologia());
                            ordine = ord.intValue();
                        }
                    }
                }
                //polipo2
                if (e.getP2Istologia() != null && e.getP2Istologia() > 0) {
                    ord =
                        this.getOrdine(e.getUlss(), e.getTpscr(), ConfigurationConstants.NOME_GRUPPO_ISTO_POLIPI,
                                       e.getP2Istologia(), am);
                    if (ord != null) {
                        //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                        //la diagnosi peggiore
                        if (ordine < ord.intValue()) {
                            diagnosi = e.getP2Istologia();
                            ordine = ord.intValue();
                        }
                    }
                }
                //polipo 3
                if (e.getP3Istologia() != null && e.getP3Istologia() > 0) {
                    ord =
                        this.getOrdine(e.getUlss(), e.getTpscr(), ConfigurationConstants.NOME_GRUPPO_ISTO_POLIPI,
                                       e.getP3Istologia(), am);
                    if (ord != null) {
                        //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                        //la diagnosi peggiore
                        if (ordine < ord.intValue()) {
                            diagnosi = e.getP3Istologia();
                            ordine = ord.intValue();
                        }
                    }
                }
                //carcinoma
                if (e.getCIstologia() != null && e.getCIstologia() > 0) {
                    ord =
                        this.getOrdine(e.getUlss(), e.getTpscr(), ConfigurationConstants.NOME_GRUPPO_ISTO_CANCRO,
                                       e.getCIstologia(), am);
                    if (ord != null) {
                        //la nuova diagnosi trovata e' piu' grave, aggiorno quella che sara'
                        //la diagnosi peggiore
                        if (ordine < ord.intValue()) {
                            diagnosi = e.getCIstologia();
                            ordine = ord.intValue();
                        }
                    }
                }

            } //fine del ciclo sulle endoscopie

            //controllo sulle istologie chirurgiche
            endo = am.findViewObject("Ref_SoInterventocolonView1");
            iter2 = ViewHelper.getRowSetIterator(endo, "interventi_iter");
            while (iter2.hasNext()) {
                Ref_SoInterventocolonViewRow e = (Ref_SoInterventocolonViewRow) iter2.next();
                Integer ord = null;
                //polipo1
                if (ConfigurationConstants.DB_TRUE.equals(e.getIstologia()) && e.getDiagnosi() != null) {
                    ord =
                        this.getOrdine(e.getUlss(), e.getTpscr(), ConfigurationConstants.NOME_GRUPPO_DIAGNOSI,
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

            //aggiorno la diagnosi peggiore
            //20090120: correzione delal situizone in cui non c'e' alcuna diagnosi peggiore
            //(deve rimanere il default inziale)
            if (diagnosi == null)
                diagnosi = ConfigurationConstants.DB_FALSE;
            ((Ref_SoRefertocolon2livViewRow) r).setDiagnosiPeggiore(diagnosi);
            ((Ref_SoRefertocolon2livViewRow) r).setGrDiagnosi(ConfigurationConstants.NOME_GRUPPO_DIAGNOSI);


        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter2 != null)
                iter2.closeRowSetIterator();
        }


    }

    /**
     * Imposta automaticamnet ele conclusioni a partire dai dati delle istologie
     * post-endoscopiche (non fa commit ne' rollback)
     * @param
     */
    protected void updateConclusioni() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        Ref_SoRefertocolon2livViewRow ref = (Ref_SoRefertocolon2livViewRow) voIter.getCurrentRow();
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
        if (ref == null)
            return;
        ViewObject vo = am.findViewObject("Ref_SoEndoscopiaView1");
        RowSetIterator iter = null;
        //  ViewObject cnf=am.findViewObject("Ref_SoCodref2livcoView2");
        try {
            ref.setGrConclusioni(ConfigurationConstants.NOME_GRUPPO_CONCL_CO);
            //se non ci sono endoscopie, non c'e' nemmeno automatismo
            if (vo.getRowCount() == 0) {
                ref.setConclusioni(ConfigurationConstants.DB_FALSE);
                return;
            }

            //1. se tra le endoscopie e' presente almneo un carcinoma --> CARCINOMA
            //lavoro su un set di dati che e' gia' tutto in memoria perche' in menu', quindi il getFilteredRows va bene
            Row[] result = vo.getFilteredRows("Cancro", ConfigurationConstants.DB_TRUE);
            if (result.length > 0) {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_CARCINOMA);
                return;
            }


            //raccogliamo dati da tutte le endoscopie
            iter = ViewHelper.getRowSetIterator(vo, "endo_iter");
            String lista = "";
            int n_polipi = 0;
            boolean max_dim = false;
            boolean med_dim = false;
            Ref_SoEndoscopiaViewRow r = null;
            int isto = 0;
            boolean poliposi = false;
            boolean ibd = false;
            while ((r = (Ref_SoEndoscopiaViewRow) iter.next()) != null) {
                //elenco con gli id delle endoscopie
                lista += r.getIdendo() + ",";

                //sommo il numero di polipi
                n_polipi += r.getNPolipi() == null ? 0 : r.getNPolipi();

                //controllo se il diametro massimo e' >=2 (cioe' ha codice >=3)
                if (r.getDiametroMax() != null &&
                    ConfigurationConstants.CODICE_DIM_2 <= r.getDiametroMax())
                    max_dim = true;
                //controllo se il diametro e' tra 1 e 2 cm (cioe' ha codice 2)
                if (r.getDiametroMax() != null &&
                    ConfigurationConstants.CODICE_DIM_1_2 == r.getDiametroMax())
                    med_dim = true;

                //prendo il valore di istologia piu' grave sui 3 polipi di tutte le endo
                if (r.getP1Istologia() != null && r.getP1Istologia() > isto)
                    isto = r.getP1Istologia();
                if (r.getP2Istologia() != null && r.getP2Istologia() > isto)
                    isto = r.getP2Istologia();
                if (r.getP3Istologia() != null && r.getP3Istologia() > isto)
                    isto = r.getP3Istologia();

                //leggo se c'e' una poliposi o un ibd
                if (ConfigurationConstants.CODICE_LESIONI_POLIPOSI.equals(r.getAltreLesioni()))
                    poliposi = true;
                if (ConfigurationConstants.CODICE_LESIONI_IBD.equals(r.getAltreLesioni()))
                    ibd = true;
            }
            if (lista.endsWith(","))
                lista = lista.substring(0, lista.length() - 1);


            //2. se c'e' almeno un apoliposi --> POLIPOSI
            if (poliposi) {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_POLIPOSI);
                return;
            }


            //3. CASI PER L'ADENOMA AD ALTO RISCHIO
            //se la somma dei polipi e' >=5
            //oppure, se il diametro massimo>=2cm almeno in un polipo di una endo
            if (n_polipi >= 5 || max_dim) //|| ConfigurationConstants.CODICE_ISTO_AT_DG.intValue()<=isto)
            {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_HR);
                return;
            }

            //4. se num polipi = 3,4 o dimensioni >1 e <2
            //o displasia alto grado o polipo villoso --> RISCHIO INTERMEDIO
            //uso il try catch perche' getFilteredRows da' errore se non trova niente
            //30042013 GAION: aggiunto automatismo per ADENOMA RISCHIO INTERMEDIO
            if (n_polipi > 2 && n_polipi < 5) //|| ConfigurationConstants.CODICE_ISTO_AT_DG.intValue()<=isto)
            {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_MR);
                return;
            }
            if (n_polipi >= 1 && med_dim) {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_MR);
                return;
            }
            if (ConfigurationConstants.CODICE_ISTO_TUBVIL <= isto &&
                ConfigurationConstants.CODICE_ISTO_VILL_DG >= isto) {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_MR);
                return;
            }
            try { //oppure se c'e' un'endoscopia con displasia alto grado in uno dei 3 polipi
                //lavoro su un set di dati che e' gia' tutto in memoria perche' in menu', quindi il getFilteredRows va bene
                result = vo.getFilteredRows("P1Displasia", ConfigurationConstants.CODICE_DISP_HG);
                if (result.length > 0) {
                    ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_MR);
                    return;
                }
            } catch (NullPointerException nex) {
            }
            try { //lavoro su un set di dati che e' gia' tutto in memoria perche' in menu', quindi il getFilteredRows va bene
                result = vo.getFilteredRows("P2Displasia", ConfigurationConstants.CODICE_DISP_HG);
                if (result.length > 0) {
                    ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_MR);
                    return;
                }

            } catch (NullPointerException nex) {
            }
            try { //lavoro su un set di dati che e' gia' tutto in memoria perche' in menu', quindi il getFilteredRows va bene
                result = vo.getFilteredRows("P3Displasia", ConfigurationConstants.CODICE_DISP_HG);
                if (result.length > 0) {
                    ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_MR);
                    return;
                }
            } catch (NullPointerException nex) {
            }


            //5.      se almeno in un polipo di una endoscopia
            //--> BASSO RISCHIO
            if (n_polipi >= 1 && n_polipi <= 2) {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_LR);
                return;
            }

            //6.      Se almeno in una endoscopia e' segnalato IBD -->IBD
            if (ibd) {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_IBD);
                return;
            }
            /*  cnf.setWhereClause("ULSS='"+ulss+"' AND TPSCR='"+tpscr+"' AND "+
            "GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_LESIONI+"' AND "+
            "IDCFREF2L="+ConfigurationConstants.CODICE_LESIONI_IBD+
            " AND IDENDO IN ("+lista+")");

            cnf.executeQuery();
            if(cnf.getRowCount()>0){
               ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_IBD);
               return;
            }*/

            //7.      se almeno una endoscopia ha  n°polipi>0 E
            //altro<=istologia<=leiomioma --> ALTRO
            if (ConfigurationConstants.CODICE_ISTO_ALTRO <= isto &&
                ConfigurationConstants.CODICE_ISTO_LEIOMIOMA >= isto) {
                ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_ALTRO);
                am.getTransaction().commit();
                return;
            }

            //8. in tutti i casi rimanenti: NEGATIVO
            ref.setConclusioni(ConfigurationConstants.CODICE_CO2LIV_NEGATIVO);


        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

    }

    /**
     * Metodo che riporta su DB le modifiche di interfaccia
     * @param
     */
    protected abstract void updateDB();

    /**
     * Fa la rollback, visualizza il messaggio d'errore e ricarica i dati di
     * interfaccia d aDB
     * @param livello
     * @param voNames
     * @param am
     * @param msg
     * @param
     */
    protected void whenException(String msg, Parent_AppModule am, String[] voNames) {
        this.handleException(msg, am, voNames);
        this.loadFromDB();
    }
    
    public void downloadRiassunto2liv(FacesContext fc, OutputStream os) throws Exception {
        int report_n = SystemReport.REFERTO_RIASSUNTIVO_2L_CO;

        File pdf = null;
        HashMap h = null;
        try {

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
            ViewObject ref = refVoIter.getViewObject();
            Row r = ref.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto selezionato per la visualizzazione");
            SystemReport report = new SystemReport(report_n);
            h = new HashMap();
            h.put("id_referto", new Double(((Integer) r.getAttribute("Idreferto")).doubleValue()));
            RefCo_AppModule am = (RefCo_AppModule) ref.getApplicationModule();
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

