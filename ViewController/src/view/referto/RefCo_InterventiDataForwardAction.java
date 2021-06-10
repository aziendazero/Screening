package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.RefCo_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Ref_2livBean;

import model.referto.common.Ref_SoInterventocolonViewRow;
import model.referto.common.Ref_SoRefertocolon2livViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class RefCo_InterventiDataForwardAction extends RefCo_DataForwardAction {
    public RefCo_InterventiDataForwardAction() {
        super();
    }

    @Override
    protected void afterUpdateModel() {
        // TODO Implement this method
    }

    @Override
    protected void loadFromDB() {
        // TODO Implement this method
    }

    @Override
    protected void updateDB() {
        // TODO Implement this method
    }

    /**
     * Metodo che chiude tutti gli interventi associati ad un referto
     * di secondo livello
     * @param ctx
     */
    public void onClose(ClientEvent clientEvent) {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInterventocolonView1");
        String to_do = (String)clientEvent.getParameters().get("ref_lettera");
        RowSetIterator iter = ViewHelper.getRowSetIterator(vo, "iter_close");
        ViewObject ref1 = am.findViewObject("Ref_SoRefertocolon2livView1");
        Row r1 = ref1.getCurrentRow();

        Row r = null;
        Row last = null;
        try {

            //controlli prima di qualsiasi commit
            this.beforeSavingReferto();
            int idint = -1;
            
            //controllo ch ei dati siano corretti sull'ultimo intervento, cioe' sul piu' recente           
            while (iter.hasNext()) {
                r = iter.next();
                if (((Integer) r.getAttribute("NInt")).intValue() > idint) {
                    last = r;
                    idint = ((Integer) r.getAttribute("NInt")).intValue();
                }
            }

            if (last == null)
                throw new Exception("Nessun intervento da chiudere");
            Integer eseguito = (Integer) last.getAttribute("Opzioneesec");
            //se l'intervento risulta eseguito, cntrollo che ci siano le raccomandzioni post-intervento
            if (!ConfigurationConstants.DB_FALSE.equals(eseguito)
                // && !ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito)
                ) {
                if (last.getAttribute("Idsugg3l") == null ||
                    ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(last.getAttribute("Idsugg3l")))
                    throw new Exception("Non e' stata segnalata nessuna raccomandazione conclusiva");
                if (last.getAttribute("Idmedconcl") == null)
                    throw new Exception("Medico responsabile delle raccomandazioni conclusive non inserito");
                if (last.getAttribute("Dtconcl") == null)
                    throw new Exception("Data delle raccomandazioni conclusive non inserita");
            }
            //chiudo tutti gli interventi
            iter.reset();
            while (iter.hasNext()) {

                r = iter.next();
                this.checkDateOrder(r, r1);
                this.checkMandatoryData((Ref_SoInterventocolonViewRow) r);
                r.setAttribute("Completo", ConfigurationConstants.DB_TRUE);

            }

            GestoreReferti gr = new GestoreReferti(am);
            ViewObject primol = am.findViewObject("Ref_SoInvito1LivCOView1");
            ViewObject ref = am.findViewObject("Ref_SoRefertocolon2livView1");
            Row refRow = ref.getCurrentRow();
            if (refRow == null)
                throw new Exception("Non e' stato trovato il referto cui associare gli interventi");

            //se devo ricreare la lettera la cancello e ricreo
            if ("ricrea".equals(to_do)) {
                gr.deleteLettera(refRow);
                gr.creaLetteraDiReferto(last, 3, primol.first());
            }
            //se la lettera non c'e' la creo semplicemente
            else if ("crea".equals(to_do)) {
                gr.creaLetteraDiReferto(last, 3, primol.first());
            }

            //faccio l'update del referto
            refRow.setAttribute("Intchiusi", ConfigurationConstants.DB_TRUE);

            //faccio l'update dell'invito in base all'ultimo suggerimento
            Map session = ADFContext.getCurrent().getSessionScope();
            gr.updateInvito(last, refRow, am.findViewObject("Ref_SoInvitoView2"), 3,
                            (String) session.get("user"));

            //eventuale update diagnois peggiore
            this.setDiagnosiPeggiore(refRow);

            //salvo il tutto
            am.getTransaction().commit();
            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);
            
        } catch (Exception ex) {
            // ex.printStackTrace();
            if (r != null) {
                vo.setCurrentRow(r);
                //il referto non e' completo
                r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
            }
            this.handleException("Impossibile chiudere il/gli intervento/i: " + ex.getMessage(),
                                 null); //am,new String[]{"Ref_SoInvitiPerRefertiView1","Ref_SoInterventocitoView6"});

        } finally {
            iter.closeRowSetIterator();
        }
    }


    public void onCreateIntervento(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();

        try {
            Ref_SoRefertocolon2livViewRow r = (Ref_SoRefertocolon2livViewRow) voIter.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto cui associare l'intervento");
            if (!ConfigurationConstants.DB_TRUE.equals(r.getCompleto()))
                throw new Exception("Il referto di 2 livello associato all'intervento non e' stato chiuso");
            if (ConfigurationConstants.DB_TRUE.equals(r.getIntchiusi()))
                throw new Exception("Gli interventi di questo referto risultano chiusi");


            ViewObject vo = am.findViewObject("Ref_SoInterventocolonView1");
            int n = vo.getRowCount();
            Ref_SoInterventocolonViewRow interv = (Ref_SoInterventocolonViewRow) vo.createRow();
            vo.insertRow(interv);
            interv.setIdint(am.getNextIdInterventoCO());
            interv.setCompleto(ConfigurationConstants.DB_FALSE);
            interv.setCodts(r.getCodts());
            //dato non disponibile corrisponde allo zero
            interv.setOpzioneesec(0);
            interv.setAttribute("GrOpzione", ConfigurationConstants.NOME_GRUPPO_INTVAL_2L);
            interv.setMotivoesec(0);
            interv.setAttribute("GrMotivo", ConfigurationConstants.NOME_GRUPPO_INTMIE_2L);
            interv.setTpintervento(0);
            interv.setAttribute("GrTpintervento", ConfigurationConstants.NOME_GRUPPO_INTTIP_2L);
            interv.setComplicanze(0);
            interv.setGrComplicanze(ConfigurationConstants.NOME_GRUPPO_COMPLICANZE_INT);
            interv.setIstologia(ConfigurationConstants.DB_FALSE);
            interv.setDiagnosi(0);
            interv.setAttribute("GrDiagnosi", ConfigurationConstants.NOME_GRUPPO_DIAGNOSI);
            interv.setAttribute("GruppoM", ConfigurationConstants.NOME_GRUPPO_ISTOLM_2L);
            interv.setAttribute("GruppoPn", ConfigurationConstants.NOME_GRUPPO_ISTOPN_2L);
            interv.setAttribute("GruppoPt", ConfigurationConstants.NOME_GRUPPO_ISTOPT_2L);
            interv.setAttribute("Idsugg3l", ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
            Map session = ADFContext.getCurrent().getSessionScope();
            interv.setAttribute("Ulss", session.get("ulss"));
            interv.setAttribute("Tpscr", session.get("scr"));

            interv.setRadio(ConfigurationConstants.DB_FALSE);
            interv.setChemio(ConfigurationConstants.DB_FALSE);
            interv.setGrGenerico(ConfigurationConstants.NOME_GRUPPO_GENERICO);
            interv.setResezione(0);
            interv.setNInt(new Integer(n + 1));
            interv.setGrAstlerColler(ConfigurationConstants.NOME_GRUPPO_ASTLER_COLLER);
            interv.setGrDukes(ConfigurationConstants.NOME_GRUPPO_DUKES);
            interv.setGrStadio(ConfigurationConstants.NOME_GRUPPO_STADIO);
        } catch (Exception ex) {
            this.handleException("Impossibile creare un nuovo intervento: " + ex.getMessage(), am,
                                 new String[] { "Ref_SoInterventocolonView1" });
        }

    }

    /**
     * Metodo che riapre tutti gli interventi associati ad un referto
     * di secondo livello
     * @param ctx
     */
    public void onReopen() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();

        RowSetIterator iter = ViewHelper.getRowSetIterator(voIter.getViewObject(), "iter_close");
        Row r = null;
        try {

            AccessManager.checkPermission("SORiaperturaReferti2Liv");
            
            while (iter.hasNext()) {
                r = iter.next();
                //il referto e' completo
                r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
            }

            //faccio l'update del referto
            ViewObject ref = am.findViewObject("Ref_SoRefertocolon2livView1");
            Row refRow = ref.getCurrentRow();
            if (refRow == null)
                throw new Exception("Non e' stato trovato il referto cui associare gli interventi");
            refRow.setAttribute("Intchiusi", ConfigurationConstants.DB_FALSE);

            /////////////////////////////////////////
            //annullo il richiamo
            Map session = ADFContext.getCurrent().getSessionScope();
            GestoreReferti gr = new GestoreReferti(am);
            gr.updateInvito((String) r.getAttribute("Ulss"), (String) r.getAttribute("Tpscr"),
                            (Integer) refRow.getAttribute("Idinvito"), null, //sugg=null
                            am.findViewObject("Ref_SoInvitoView2"), 3,
                            (String) session.get("user"));
            //////////////////////


            //salvo il tutto
            am.getTransaction().commit();

        } catch (Exception ex) {
            // ex.printStackTrace();
            if (r != null)
                voIter.getViewObject().setCurrentRow(r);
            this.handleException("Impossibile riaprire il/gli intervento/i: " + ex.getMessage(), am,
                                 new String[] { "Ref_SoInterventocolonView1" });

        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

    }

    public void onApply(ActionEvent actionEvent) {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();

        try {
            this.beforeSavingReferto();
            am.getTransaction().commit();
            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);

        } catch (Exception ex) {
            // ex.printStackTrace();
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

        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        try {

            ViewObject ref = am.findViewObject("Ref_SoInterventocolonView1");
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
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");
        //eseguo il controllo per tutti gli interventi di quel referto
        RowSetIterator iter = ViewHelper.getRowSetIterator(voIter.getViewObject(), "iter");
        Ref_SoInterventocolonViewRow r = null;
        try {

            while (iter.hasNext()) {
                r = (Ref_SoInterventocolonViewRow) iter.next();
                if (r == null)
                    continue;

                //se l'intervento non e' eseguito non ci possono essere dati ne' istologia
                Integer eseguito = (Integer) r.getAttribute("Opzioneesec");
                //se non e' stato eseguito
                if (ConfigurationConstants.DB_FALSE.intValue() == eseguito ||
                    ConfigurationConstants.CODICE_INTVAL_NON_EX == eseguito) {
                    //reset di tutti gli oggetti

                    r.setAttribute("Dtintervento", null);
                    r.setAttribute("Idchirurgo", null);
                    r.setAttribute("Reparto", null);
                    r.setTpintervento(0);
                    r.setComplicanze(0);
                    r.setAttribute("AltroComplicanze", null);
                    r.setIstologia(ConfigurationConstants.DB_FALSE);
                    r.setAttribute("Dtistologia", null);
                    r.setAttribute("NoteIstologia", null);
                    r.setAttribute("Idpatologo", null);
                    r.setDiagnosi(0);
                    r.setStadioM(0);
                    r.setStadioPn(0);
                    r.setStadioPt(0);
                    r.setAstlerColler(0);
                    r.setDukes(0);
                    r.setStadio(0);
                    r.setResezione(0);
                    r.setAttribute("Grading", null);
                    r.setAttribute("NLinfonodi", null);
                    r.setAttribute("NLinfonodiPositivi", null);

                    if (ConfigurationConstants.DB_FALSE.intValue() == eseguito) {
                        r.setAttribute("Dtconcl", null);
                        r.setAttribute("Idmedconcl", null);
                        r.setAttribute("Idsugg3l", ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
                    }

                }

                if (ConfigurationConstants.CODICE_INTVAL_NON_EX != eseguito)
                    r.setAttribute("Motivoesec", ConfigurationConstants.DB_FALSE);

                //se le complicanze non contemplano "Altro", tale campo non puo' essere valorizzato
                if (ConfigurationConstants.CODICE_COMPLIC_INT_ALTRO != r.getComplicanze() &&
                    r.getAltroComplicanze() != null && r.getAltroComplicanze().trim().length() > 0)
                    r.setAltroComplicanze(null);

                //idem per il morivo di inesecuzione
                if (ConfigurationConstants.CODICE_INTMIE_ALTRO != r.getMotivoesec() &&
                    r.getAltroMotivo() != null && r.getAltroMotivo().trim().length() > 0)
                    r.setAltroMotivo(null);

                if (r != null && r.getAttribute("Completo") != null &&
                    ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
                    throw new Exception("Intervento/i gia' chiuso/i, le modifiche sono state annullate");

            }
            iter.closeRowSetIterator();

        } catch (Exception ex) {
            iter.closeRowSetIterator();

            throw ex;
        }

        this.updateDB();

    }

    protected void checkMandatoryData(Ref_SoInterventocolonViewRow ref) throws Exception {
        if (ref == null)
            return;
        Integer eseguito = ref.getOpzioneesec();
        //se l'intervento risulta eseguito, data e medico sono obbligatori
        if ( eseguito.intValue() != ConfigurationConstants.DB_FALSE.intValue() 
                && eseguito.intValue() != ConfigurationConstants.CODICE_INTVAL_NON_EX.intValue()) {
            if (ref.getDtintervento() == null)
                throw new Exception("Inserire la data dell'intervento");

            //eseguito nel programma, servono ulteriori dati
            if (ConfigurationConstants.CODICE_INTVAL_EX_QUI.intValue() == eseguito.intValue()) {
                if (ref.getIdchirurgo() == null)
                    throw new Exception("Inserire il medico responsabile dell'intervento");
                /*if(ref.getTpintervento()==null || ref.getTpintervento().intValue()<=0)
                throw new Exception("Inserire il tipo di intervento eseguito");*/
            }
        }

        eseguito = ref.getIstologia();
        if (ConfigurationConstants.DB_TRUE.equals(eseguito)) {
            if (ref.getDtistologia() == null)
                throw new Exception("Inserire la data dell'istologia chirurgica");
            if (ref.getIdpatologo() == null)
                throw new Exception("Inserire il medico responsabile dell'istologia chirurgica");

            if (ref.getDiagnosi() == null || ref.getDiagnosi().intValue() <= 0)
                throw new Exception("Inserire il un dato valido per la diagnosi dell'istologia chirurgica");

        }

    }


    public void onEseguito(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");

        Row r = voIter.getCurrentRow();
        Integer eseguito = (Integer) valueChangeEvent.getNewValue();

        if (ConfigurationConstants.CODICE_INTVAL_NON_EX != eseguito)
            r.setAttribute("Motivoesec", ConfigurationConstants.DB_FALSE);

    }


    protected void checkDateOrder(Row interv, Row ref) throws Exception {

        oracle.jbo.domain.Date d2 = (oracle.jbo.domain.Date) ref.getAttribute("Dtconcl");
        oracle.jbo.domain.Date d3 = (oracle.jbo.domain.Date) interv.getAttribute("Dtintervento");
        oracle.jbo.domain.Date d4 = (oracle.jbo.domain.Date) interv.getAttribute("Dtistologia");
        oracle.jbo.domain.Date d5 = (oracle.jbo.domain.Date) interv.getAttribute("Dtconcl");

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
