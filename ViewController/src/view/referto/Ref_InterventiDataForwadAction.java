package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Calendar;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.common.Ref_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Ref_2livBean;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class Ref_InterventiDataForwadAction extends Ref_DataForwardAction {

    @Override
    protected void updateDB() {
    }

    @Override
    protected void loadFromDB() {
    }
    
    @Override
    protected void filter(){
    }

    /**
     * Metodo che chiude tutti gli interventi associati ad un referto
     * di secondo livello
     * @param ctx
     */
    public void onClose(ClientEvent clientEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Ref_AppModule am =
            (Ref_AppModule) bindings.getBindingContext().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInterventocitoView6");
        String to_do = (String) clientEvent.getParameters().get("ref_lettera");
        RowSetIterator iter = ViewHelper.getRowSetIterator(vo, "iter_close");

        Row r = null;
        Row last = null;
        try {

            //controlli prima di qualsiasi commit
            this.beforeSavingReferto();
            int idint = -1;
            
            //controllo ch ei dati siano corretti sull'ultimo intervento, cioe' sul piu' recente          
            last = iter.first();          
            if (last == null)
                throw new Exception("Nessun intervento da chiudere");
            
            idint = ((Integer) last.getAttribute("NInt")).intValue();        
            Integer eseguito = (Integer) last.getAttribute("Opzioneesec");
            //se l'intervento risulta eseguito, cntrollo che ci siano le raccomandzioni post-intervento
            if (!ConfigurationConstants.DB_FALSE.equals(eseguito)
                // && !ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito)
                ) {
                if (last.getAttribute("Idsugg") == null ||
                    ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(last.getAttribute("Idsugg")))
                    throw new Exception("Non e' stata segnalata nessuna raccomandazione conclusiva");
                if (last.getAttribute("Medicoracc") == null)
                    throw new Exception("Medico responsabile delle raccomandazioni conclusive non inserito");
                if (last.getAttribute("Dtraccomandazione") == null)
                    throw new Exception("Data delle raccomandazioni conclusive non inserita");
            }
            //chiudo tutti gli interventi
            last.setAttribute("Completo", ConfigurationConstants.DB_TRUE);
            while (iter.hasNext()) {
                r = iter.next();
                r.setAttribute("Completo", ConfigurationConstants.DB_TRUE);
            }

            GestoreReferti gr = new GestoreReferti(am);
            ViewObject primol = am.findViewObject("Ref_SoInvitoPrimoLivView1");
            ViewObject ref = am.findViewObject("Ref_SoRefertocito2livView4");
            Row refRow = ref.getCurrentRow();
            if (refRow == null)
                throw new Exception("Non e' stato trovato il referto cui associare gli interventi");

            //se devo ricreare la lettera la cancello e ricreo
            if ("ricrea".equals(to_do)) {
                gr.deleteLettera(refRow);
                gr.creaLetteraDiRefertoCI(last, null, null, 3, primol.first());
            }
            //se la lettera non c'e' la creo semplicemente
            else if ("crea".equals(to_do)) {
                gr.creaLetteraDiRefertoCI(last, null, null, 3, primol.first());
            }

            //faccio l'update del referto

            refRow.setAttribute("Intchiusi", ConfigurationConstants.DB_TRUE);

            //faccio l'update dell'invito in base all'ultimo suggerimento
            gr.updateInvito(last, refRow, am.findViewObject("Ref_SoInvitoView2"), 3,
                            (String) ADFContext.getCurrent().getSessionScope().get("user"));

            //eventuale update diagnois peggiore
            this.setDiagnosiPeggiore(refRow);


            //salvo il tutto
            am.getTransaction().commit();
            Ref_2livBean bean =
                (Ref_2livBean) bindings.getBindingContext().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);
            //    this.handleMessages(ctx,"Operazione compiuta, ma momentaneamente incompleta: l'eventuale lettera non e' stata creata");
        } catch (Exception ex) {
            //  ex.printStackTrace();
            if (r != null) {
                vo.setCurrentRow(r);
                //il referto non e' completo
                r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
            }
            this.handleException("Impossibile chiudere il/gli intervento/i: " + ex.getMessage(),
                                 null); //am,new String[]{"Ref_SoInvitiPerRefertiView1","Ref_SoInterventocitoView6"});


            // GestoreRefertiUI.loadFromDB1liv(ctx);
        } finally {
            iter.closeRowSetIterator();
        }
    }


    public void onCreateIntervento(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Ref_AppModule am =
            (Ref_AppModule) bindings.getBindingContext().findDataControl("Ref_AppModuleDataControl").getDataProvider();

        try {
            ViewObject vo1 = am.findViewObject("Ref_SoRefertocito2livView4");
            Row r = vo1.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto cui associare l'intervento");
            if (!ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Completo")))
                throw new Exception("Il referto di 2 livello associato all'intervento non e' stato chiuso");
            if (ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Intchiusi")))
                throw new Exception("Gli interventi di questo referto risultano chiusi");


            ViewObject vo = am.findViewObject("Ref_SoInterventocitoView6");
            /*20080715 MOD: numero intervento*/
            int n = vo.getRowCount();
            /*20080715 FINE MOD*/
            Row interv = vo.createRow();
            vo.insertRow(interv);
            interv.setAttribute("Idint", am.getNextIdIntervento());
            interv.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
            //dato non disponibile corrisponde allo zero
            interv.setAttribute("Opzioneesec", ConfigurationConstants.DB_FALSE);
            interv.setAttribute("Gropzione", ConfigurationConstants.NOME_GRUPPO_INTVAL_2L);
            interv.setAttribute("Motivoesec", ConfigurationConstants.DB_FALSE);
            interv.setAttribute("Grmotivo", ConfigurationConstants.NOME_GRUPPO_INTMIE_2L);
            interv.setAttribute("Tpintervento", ConfigurationConstants.DB_FALSE);
            interv.setAttribute("Grtpintervento", ConfigurationConstants.NOME_GRUPPO_INTTIP_2L);
            interv.setAttribute("Istchirurgica", ConfigurationConstants.DB_FALSE);
            interv.setAttribute("Gristdia", ConfigurationConstants.NOME_GRUPPO_ISTDIA_2L);
            interv.setAttribute("Gristolm", ConfigurationConstants.NOME_GRUPPO_ISTOLM_2L);
            interv.setAttribute("Gristopn", ConfigurationConstants.NOME_GRUPPO_ISTOPN_2L);
            interv.setAttribute("Gristopt", ConfigurationConstants.NOME_GRUPPO_ISTOPT_2L);
            interv.setAttribute("Idsugg", ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
            interv.setAttribute("Ulss", ADFContext.getCurrent().getSessionScope().get("ulss"));
            interv.setAttribute("Tpscr", ADFContext.getCurrent().getSessionScope().get("scr"));
            /*20080710 MOD: aggiunta stadiazione*/
            interv.setAttribute("GrStadiazione", ConfigurationConstants.NOME_GRUPPO_STADIO);
            interv.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO__CI);
            /*20080710 FINE MOD*/
            /*20080715 MOD: numero intervento*/
            interv.setAttribute("NInt", new Integer(n + 1));
            /*20080715 FINE MOD*/
        } catch (Exception ex) {
            this.handleException("Impossibile creare un nuovo intervento: " + ex.getMessage(), am,
                                 new String[] { "Ref_SoInvitiPerRefertiView1", "Ref_SoInterventoView6" });
        }

    }

    /**
     * Metodo che riapre tutti gli interventi associati ad un referto
     * di secondo livello
     * @param ctx
     */
    public void onReopen() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Ref_AppModule am =
            (Ref_AppModule) bindings.getBindingContext().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInterventocitoView6");

        RowSetIterator iter = ViewHelper.getRowSetIterator(vo, "iter_close");

        Row r = null;
        try {
            AccessManager.checkPermission("SORiaperturaReferti2Liv");
            while (iter.hasNext()) {
                r = iter.next();
                //il referto e' completo
                r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);

            }


            //faccio l'update del referto
            ViewObject ref = am.findViewObject("Ref_SoRefertocito2livView4");
            Row refRow = ref.getCurrentRow();
            if (refRow == null)
                throw new Exception("Non e' stato trovato il referto cui associare gli interventi");
            refRow.setAttribute("Intchiusi", ConfigurationConstants.DB_FALSE);

            /////////////////////////////////////////
            //annullo il richiamo
            GestoreReferti gr = new GestoreReferti(am);
            gr.updateInvito((String) r.getAttribute("Ulss"), (String) r.getAttribute("Tpscr"),
                            (Integer) refRow.getAttribute("Idinvito"), null, //sugg=null
                            am.findViewObject("Ref_SoInvitoView2"), 3,
                            (String) ADFContext.getCurrent().getSessionScope().get("user"));
            //////////////////////


            //salvo il tutto
            am.getTransaction().commit();

        } catch (Exception ex) {
            //   ex.printStackTrace();
            if (r != null)
                vo.setCurrentRow(r);
            this.handleException("Impossibile chiudere il/gli intervento/i: " + ex.getMessage(), am,
                                 new String[] { "Ref_SoInvitiPerRefertiView1", "Ref_SoInterventocitoView6" });

        } finally {
            iter.closeRowSetIterator();
        }

    }

    public void onApply() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Ref_AppModule am =
            (Ref_AppModule) bindings.getBindingContext().findDataControl("Ref_AppModuleDataControl").getDataProvider();

        try {
            this.beforeSavingReferto();
            am.getTransaction().commit();
            Ref_2livBean bean =
                (Ref_2livBean) bindings.getBindingContext().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);

        } catch (Exception ex) {
            //  ex.printStackTrace();
            //   this.whenException(ctx,"Impossibile salvare le modifiche: "+ex.getMessage(),am,new String[]{"Ref_SoInvitiPerRefertiView1"});
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
        }
    }

    /**
     * Prima di un eventuale salvataggio automatico salvo io a modo mio
     * (per mantenere la consistenza dei dati tra interfaccia e DB)
     * @param ctx
     */
    protected boolean beforeSave() {
        // TODO:  Override this view.commons.Parent_DataForwardAction method
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Ref_AppModule am =
            (Ref_AppModule) bindings.getBindingContext().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        try {

            ViewObject ref = am.findViewObject("Ref_SoInterventocitoView6");
            Row r = ref.getCurrentRow();
            if (r == null)
                return true;

            if (r != null && r.getAttribute("Completo") != null &&
                ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
                throw new Exception("Intervento/i gia' chiuso/i, le modifiche sono state annullate");

            this.beforeSavingReferto();
            return true;
        } catch (Exception ex) {
            //  this.whenException(ctx,"Impossibile salvare le modifiche: "+ex.getMessage(),am,new String[]{"Ref_SoInvitiPerRefertiView1"});
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
            return true;
        }
    }

    protected void beforeSavingReferto() throws Exception {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Ref_AppModule am =
            (Ref_AppModule) bindings.getBindingContext().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref1 = am.findViewObject("Ref_SoRefertocito2livView4");
        //Row r1=ref1.getCurrentRow();


        ViewObject ref = am.findViewObject("Ref_SoInterventocitoView6");
        //eseguo il controllo per tutti gli interventi di quel referto
        RowSetIterator iter = ViewHelper.getRowSetIterator(ref, "iter");
        Row r = null;
        try {


            while (iter.hasNext()) {
                r = iter.next();
                if (r == null)
                    continue;

                this.checkMandatoryData(r);
                //   this.checkDateOrder(r,r1);


                if (r != null && r.getAttribute("Completo") != null &&
                    ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
                    throw new Exception("Intervento/i gia' chiuso/i, le modifiche sono state annullate");

            }
            iter.closeRowSetIterator();

        } catch (Exception ex) {
            iter.closeRowSetIterator();
            if (r != null)
                ref.setCurrentRow(r);
            throw ex;
        }


        //impostazione dati ultima modifica
        //  r.setAttribute("Dtultimamodifica",DateUtils.getOracleDateNow());
        //  r.setAttribute("Opultmodifica",ADFContext.getCurrent().getSessionScope().get("user"));

        this.updateDB();


    }

    protected void checkMandatoryData(Row ref) throws Exception {
        if (ref == null)
            return;
        Integer eseguito = (Integer) ref.getAttribute("Opzioneesec");
        //se l'intervento risulta eseguito, data e medico sono obbligatori
        if (!ConfigurationConstants.DB_FALSE.equals(eseguito) &&
            !ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito)) {
            if (ref.getAttribute("Dtintervento") == null)
                throw new Exception("Inserire la data dell'intervento");
            if (ref.getAttribute("Medicoint1") == null)
                throw new Exception("Inserire il medico responsabile dell'intervento");

        }

        eseguito = (Integer) ref.getAttribute("Istchirurgica");
        if (ConfigurationConstants.DB_TRUE.equals(eseguito)) {
            if (ref.getAttribute("Dtistchir") == null)
                throw new Exception("Inserire la data dell'istologia chirurgica");
            if (ref.getAttribute("Idmedistchi") == null)
                throw new Exception("Inserire il medico responsabile dell'istologia chirurgica");
        }

    }


    public void onEseguito(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Ref_AppModule am =
            (Ref_AppModule) bindings.getBindingContext().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoInterventocitoView6");
        //  Ref_2livBean bean=(Ref_2livBean)bindings.getBindingContext().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        Row r = ref.getCurrentRow();
        Integer eseguito = (Integer) r.getAttribute("Opzioneesec");
        //se non e' stato eseguito
        if (ConfigurationConstants.DB_FALSE.equals(eseguito) ||
            ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito)) {
            //reset di tutti gli oggetti



            r.setAttribute("Dtintervento", null);
            r.setAttribute("Medicoint1", null);
            r.setAttribute("Medicoint2", null);
            r.setAttribute("Codintervento", null);
            r.setAttribute("Numsdo", null);
            r.setAttribute("Tpintervento", ConfigurationConstants.DB_FALSE);

            r.setAttribute("Centroricovero", null);

            //
            r.setAttribute("Istchirurgica", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Dtistchir", null);
            r.setAttribute("Noteistologia", null);
            r.setAttribute("Idmedistchi", null);
            r.setAttribute("Idmedistchi2", null);
            r.setAttribute("Istdia", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Istolm", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Istopn", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Istopt", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Idcentroistchi", null);
            /*20080710 MOD: aggiunta stadiazione*/
            r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO__CI);
            /*20080710 FINE MOD*/

            if (ConfigurationConstants.DB_FALSE.equals(eseguito)) {
                r.setAttribute("Dtraccomandazione", null);
                r.setAttribute("Medicoracc", null);
                r.setAttribute("Idsugg", ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
            }
        }

        if (!ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito))
            r.setAttribute("Motivoesec", ConfigurationConstants.DB_FALSE);


    }


    protected void checkDateOrder(Row interv, Row ref) throws Exception {

        oracle.jbo.domain.Date d2 = (oracle.jbo.domain.Date) ref.getAttribute("Dtconcl");
        oracle.jbo.domain.Date d3 = (oracle.jbo.domain.Date) interv.getAttribute("Dtintervento");
        oracle.jbo.domain.Date d4 = (oracle.jbo.domain.Date) interv.getAttribute("Dtistchir");
        oracle.jbo.domain.Date d5 = (oracle.jbo.domain.Date) interv.getAttribute("Dtraccomandazione");

        Date dd2 = null;
        Date dd3 = null;
        Date dd4 = null;
        Date dd5 = null;

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
        if (d5 != null) {
            Calendar c = DateUtils.getCalendar(d5.dateValue());
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            dd5 = c.getTime();
        }

        if (dd2 != null && dd3 != null) {
            if (dd2.after(dd3))
                throw new Exception("La data dell'intervento (" + DateUtils.dateToString(dd3) +
                                    ") non puo' essere precedente alla data delle conclusioni del 2 livello (" +
                                    DateUtils.dateToString(dd2) + ")");

        }

        if (dd3 != null && dd4 != null) {
            if (dd2 != null) {
                if (dd2.after(dd4))
                    throw new Exception("La data dell'istologia chirurgica (" + DateUtils.dateToString(dd4) +
                                        ") non puo' essere precedente alla data delle conclusioni del 2 livello (" +
                                        DateUtils.dateToString(dd2) + ")");

            }

            if (dd3.after(dd4))
                throw new Exception("La data dell'istologia chirurgica (" + DateUtils.dateToString(dd4) +
                                    ") non puo' essere precedente alla data dell'intervento (" +
                                    DateUtils.dateToString(dd3) + ")");

        }
        if (dd5 != null) {
            if (dd2 != null) {
                if (dd2.after(dd5))
                    throw new Exception("La data della raccomandazione conclusiva dell'intervento (" +
                                        DateUtils.dateToString(dd5) +
                                        ") non puo' essere precedente alla data delle conclusioni del 2 livello (" +
                                        DateUtils.dateToString(dd2) + ")");

            }
            if (dd3 != null) {
                if (dd3.after(dd5))
                    throw new Exception("La data della raccomandazione conclusiva dell'intervento (" +
                                        DateUtils.dateToString(dd5) +
                                        ") non puo' essere precedente alla data dell'intervento (" +
                                        DateUtils.dateToString(dd3) + ")");

            }
            if (dd4 != null) {
                if (dd4.after(dd5))
                    throw new Exception("La data della raccomandazione conclusiva dell'intervento (" +
                                        DateUtils.dateToString(dd5) +
                                        ") non puo' essere precedente alla data dell'istologia chirurgica (" +
                                        DateUtils.dateToString(dd4) + ")");

            }
        }

    }
}
