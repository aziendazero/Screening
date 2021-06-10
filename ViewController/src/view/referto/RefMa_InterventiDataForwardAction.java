package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;
import java.util.Date;
import java.util.Calendar;

import javax.faces.event.ActionEvent;

import model.common.RefMa_AppModule;

import model.common.Ref_SoInterventomammoViewRow;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Ref_2livBean;

import model.referto.Ref_SoRefertomammo2livViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;


public class RefMa_InterventiDataForwardAction extends RefMa_DataForwardAction {
    public RefMa_InterventiDataForwardAction() {
        super();
    }

    @Override
    protected void afterUpdateModel() {
        // TODO Implement this method
    }

    @Override
    protected void updateDB() throws Exception {
        // TODO Implement this method
    }

    @Override
    protected void loadFromDB() {
        // TODO Implement this method
    }

    /**
     * Metodo che chiude tutti gli interventi associati ad un referto
     * di secondo livello
     * @param ctx
     */
    public void onClose(ClientEvent clientEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        String to_do = (String)clientEvent.getParameters().get("ref_lettera");
        RowSetIterator iter = ViewHelper.getRowSetIterator(voIter.getViewObject(), "iter_close");
        iter.setCurrentRowAtRangeIndex(iter.SLOT_BEYOND_LAST);
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        Ref_SoRefertomammo2livViewRowImpl refRow = (Ref_SoRefertomammo2livViewRowImpl) refVoIter.getCurrentRow();

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
                //20110720 sERRA: CORREZIONE
                //  && !ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito)
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
            //  iter.previous();
            iter.reset();
            while (iter.hasNext()) {
                r = iter.next();
                this.checkDateOrder(r, refRow);
                this.checkMandatoryData((Ref_SoInterventomammoViewRow) r);
                r.setAttribute("Completo", ConfigurationConstants.DB_TRUE);
            }
            RefMa_AppModule am = (RefMa_AppModule)voIter.getViewObject().getApplicationModule();
            GestoreReferti gr = new GestoreReferti(am);
            ViewObject primol = am.findViewObject("Ref_SoInvito1livMAView1");
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
            refRow.setIntchiusi(ConfigurationConstants.DB_TRUE);

            //faccio l'update dell'invito in base all'ultimo suggerimento
            ADFContext adfCtx = ADFContext.getCurrent();
            Map sess = adfCtx.getSessionScope();
            gr.updateInvito(last, refRow, am.findViewObject("Ref_SoInvitoView2"), 3,
                            (String) sess.get("user"));

            //eventuale update diagnosi peggiore
            this.setDiagnosiPeggiore(refRow);

            //salvo il tutto
            am.getTransaction().commit();
            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);
            //    this.handleMessages(ctx,"Operazione compiuta, ma momentaneamente incompleta: l'eventuale lettera non e' stata creata");
        } catch (Exception ex) {
            ex.printStackTrace();
            if (iter != null) {
                iter.first();
                iter.previous();
                while (iter.hasNext()) {
                    r = iter.next();
                    r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
                }
            }

            if (refRow != null)
                refRow.setIntchiusi(ConfigurationConstants.DB_FALSE);
            this.handleException("Impossibile chiudere il/gli intervento/i: " + ex.getMessage(), null);

        } finally {
            iter.closeRowSetIterator();
        }
    }


    public void onCreateIntervento(ActionEvent actionEvent) {
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        RefMa_AppModule am = (RefMa_AppModule)refVoIter.getViewObject().getApplicationModule();

        try {
            Ref_SoRefertomammo2livViewRowImpl r = (Ref_SoRefertomammo2livViewRowImpl) refVoIter.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto cui associare l'intervento");
            if (!ConfigurationConstants.DB_TRUE.equals(r.getCompleto()))
                throw new Exception("Il referto di 2 livello associato all'intervento non e' stato chiuso");
            if (ConfigurationConstants.DB_TRUE.equals(r.getIntchiusi()))
                throw new Exception("Gli interventi di questo referto risultano chiusi");
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();

            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
            ViewObject vo = voIter.getViewObject();
            int n = vo.getRowCount();
            Ref_SoInterventomammoViewRow interv = (Ref_SoInterventomammoViewRow) vo.createRow();
            vo.insertRowAtRangeIndex(0, interv);     
            vo.setCurrentRow(interv);
            interv.setIdint(am.getNextIdInterventoMA());
            interv.setCompleto(ConfigurationConstants.DB_FALSE);
            interv.setCodts(r.getCodts());
            //dato non disponibile corrisponde allo zero
            interv.setAttribute("Opzioneesec", ConfigurationConstants.DB_FALSE);
            interv.setAttribute("GrOpzione", ConfigurationConstants.NOME_GRUPPO_INTVAL_2L);
            interv.setAttribute("Motivoesec", ConfigurationConstants.DB_FALSE);
            interv.setAttribute("GrMotivo", ConfigurationConstants.NOME_GRUPPO_INTMIE_2L);
            interv.setAttribute("Tpintervento", ConfigurationConstants.DB_FALSE);
            interv.setAttribute("GrTpintervento", ConfigurationConstants.NOME_GRUPPO_INTTIP_2L);
            interv.setAttribute("Istologia", ConfigurationConstants.DB_FALSE);
            interv.setDiagnosi(ConfigurationConstants.DB_FALSE.intValue());
            interv.setAttribute("GrDiagnosi", ConfigurationConstants.NOME_GRUPPO_ISTDIA_2L);
            interv.setAttribute("GruppoM", ConfigurationConstants.NOME_GRUPPO_ISTOLM_2L);
            interv.setStadioM(ConfigurationConstants.DB_FALSE.intValue());
            interv.setAttribute("GruppoPn", ConfigurationConstants.NOME_GRUPPO_ISTOPN_2L);
            interv.setStadioPn(ConfigurationConstants.DB_FALSE.intValue());
            interv.setAttribute("GruppoPt", ConfigurationConstants.NOME_GRUPPO_ISTOPT_2L);
            interv.setStadioPt(ConfigurationConstants.DB_FALSE.intValue());
            interv.setAttribute("Idsugg3l", ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
            interv.setAttribute("Ulss", session.get("ulss"));
            interv.setAttribute("Tpscr", session.get("scr"));

            interv.setRadioterapia(ConfigurationConstants.DB_FALSE);
            interv.setChemioterapia(ConfigurationConstants.DB_FALSE);

            interv.setNInt(new Integer(n + 1));

            interv.setGrStadiazione(ConfigurationConstants.NOME_GRUPPO_STADIO);
            interv.setStadiazione(ConfigurationConstants.CODICE_STADIO_ND);
        } catch (Exception ex) {
            
            this.handleException("Impossibile creare un nuovo intervento: " + ex.getMessage(), am,
                                 new String[] { "Ref_SoInvitiPerRefertiMAView1", "Ref_SoInterventomammoView1" });
        }

    }

    /**
     * Metodo che riapre tutti gli interventi associati ad un referto
     * di secondo livello
     * @param ctx
     */
    public void onReopen() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        RefMa_AppModule am = (RefMa_AppModule) voIter.getViewObject().getApplicationModule();
        RowSetIterator iter = ViewHelper.getRowSetIterator(voIter.getViewObject(), "iter_close");
        iter.setCurrentRowAtRangeIndex(iter.SLOT_BEYOND_LAST);

        Row r = null;
        try {

            AccessManager.checkPermission("SORiaperturaReferti2Liv");

            iter.reset();
            while (iter.hasNext()) {
                r = iter.next();
                r.setAttribute("Completo", ConfigurationConstants.DB_FALSE);
            }

            //faccio l'update del referto
            DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
            Row refRow = refVoIter.getCurrentRow();
            if (refRow == null)
                throw new Exception("Non e' stato trovato il referto cui associare gli interventi");
            refRow.setAttribute("Intchiusi", ConfigurationConstants.DB_FALSE);

            /////////////////////////////////////////
            //annullo il richiamo
            GestoreReferti gr = new GestoreReferti(am);
            gr.updateInvito((String) r.getAttribute("Ulss"), (String) r.getAttribute("Tpscr"),
                            (Integer) refRow.getAttribute("Idinvito"), null, //sugg=null
                            am.findViewObject("Ref_SoInvitoView2"), 3,
                            (String)sess.get("user"));
            //////////////////////

            //salvo il tutto
            am.getTransaction().commit();

        } catch (Exception ex) {
            // ex.printStackTrace();
            if (r != null)
                voIter.getViewObject().setCurrentRow(r);
            this.handleException("Impossibile riaprire il/gli intervento/i: " + ex.getMessage(), am,
                                 new String[] { "Ref_SoInvitiPerRefertiMAView1", "Ref_SoInterventomammoView1" });
            this.loadFromDB();
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }
    }

    public void onApply() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
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

        this.loadFromDB();
    }

    /**
     * Prima di un eventuale salvataggio automatico salvo io a modo mio
     * (per mantenere la consistenza dei dati tra interfaccia e DB)
     * @param ctx
     */
    protected boolean beforeSave() {
        try {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
            Row r = voIter.getCurrentRow();
            if (r == null)
                return true;

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
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        //eseguo il controllo per tutti gli interventi di quel referto
        RowSetIterator iter = ViewHelper.getRowSetIterator(voIter.getViewObject(), "iter");
        Ref_SoInterventomammoViewRow r = null;
        try {

            while (iter.hasNext()) {
                r = (Ref_SoInterventomammoViewRow) iter.next();
                if (r == null)
                    continue;

                if (r != null && r.getAttribute("Completo") != null &&
                    ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
                    throw new Exception("Intervento/i gia' chiuso/i, le modifiche sono state annullate");

                Integer eseguito = (Integer) r.getAttribute("Opzioneesec");
                //se non e' stato eseguito
                if (ConfigurationConstants.DB_FALSE.equals(eseguito) ||
                    ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito)) {
                    //reset di tutti gli oggetti
                    r.setAttribute("Dtintervento", null);
                    r.setAttribute("Idchirurgo", null);
                    r.setAttribute("Reparto", null);
                    r.setAttribute("Tpintervento", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("Lesione", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("Istologia", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("Dtistologia", null);
                    r.setAttribute("NoteIstologia", null);
                    r.setAttribute("Idpatologo1", null);
                    r.setAttribute("Idpatologo2", null);
                    r.setAttribute("Diagnosi", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("StadioM", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("StadioPn", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("StadioPt", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("Grading", null);
                    r.setAttribute("Diametro", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO_ND);
                    r.setAttribute("Residui", ConfigurationConstants.DB_FALSE);
                    r.setAttribute("NLinfonodi", null);
                    r.setAttribute("NPositivi", null);

                    if (ConfigurationConstants.DB_FALSE.equals(eseguito)) {
                        r.setAttribute("Dtconcl", null);
                        r.setAttribute("Idmedconcl", null);
                        r.setAttribute("Idsugg3l", ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
                    }
                }

                if (!ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito))
                    r.setAttribute("Motivoesec", ConfigurationConstants.DB_FALSE);

                //se il morivo di inesecuzione non e' altro, il testo associato
                //non puo' essere valorizato
                if (!ConfigurationConstants.CODICE_INTMIE_ALTRO.equals(r.getMotivoesec()) &&
                    r.getAltroMotivo() != null && r.getAltroMotivo().trim().length() > 0)
                    r.setAltroMotivo(null);
            }
            iter.closeRowSetIterator();

        } catch (Exception ex) {
            iter.closeRowSetIterator();
            if (r != null)
                voIter.getViewObject().setCurrentRow(r);
            throw ex;
        }
        this.updateDB();


    }

    public void onEseguito() {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        Row r = voIter.getCurrentRow();
        Integer eseguito = (Integer) r.getAttribute("Opzioneesec");

        if (!ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito))
            r.setAttribute("Motivoesec", ConfigurationConstants.DB_FALSE);

    }

    protected void checkMandatoryData(Ref_SoInterventomammoViewRow ref) throws Exception {
        if (ref == null)
            return;
        Integer eseguito = ref.getOpzioneesec();
        //se l'intervento risulta eseguito, data e medico sono obbligatori
        if (!ConfigurationConstants.DB_FALSE.equals(eseguito) &&
            !ConfigurationConstants.CODICE_INTVAL_NON_EX.equals(eseguito)) {
            if (ref.getDtintervento() == null)
                throw new Exception("Inserire la data dell'intervento");
        }

        eseguito = ref.getIstologia();
        if (ConfigurationConstants.DB_TRUE.equals(eseguito)) {
            if (ref.getDtistologia() == null)
                throw new Exception("Inserire la data dell'istologia chirurgica");
            if (ref.getDiagnosi() == null || ref.getDiagnosi().intValue() <= 0)
                throw new Exception("Inserire il un dato valido per la diagnosi dell'istologia chirurgica");

        }

    }

    /**
     * Metodo che controlla la coerenza delle date, che devono essere nelseguente ordine:
     * data conclusioni 2 livello<=data intervento<=data istologia<=data conclusioni post intervento
     * @throws java.lang.Exception
     * @param ref
     * @param interv
     */
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
    
    public String onDeleteIntervento() {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        ViewObject vo = voIter.getViewObject();
        RefMa_AppModule am = (RefMa_AppModule)vo.getApplicationModule();
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
                DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");        
                Row r2 = refVoIter.getCurrentRow();
                if (r2 == null)
                    throw new Exception("Nessun referto");
                if (ConfigurationConstants.DB_TRUE.equals(r2.getAttribute("Completo"))) {
                    //se il referto e' completo devo reimpostare le sue conseguenze
                    this.conseguenzeChiusura(r2, am, null);
                }


            }
            int n = 0;
            if (r.getAttribute("NInt") != null)
                n = ((Integer) r.getAttribute("NInt")).intValue();

            Integer idref = (Integer) r.getAttribute("Idreferto");
            Integer idint = (Integer) r.getAttribute("Idint");

            // mauro 16/12/2010, prima di cancellare l'intervento faccio il backup
            String sql =
                "insert into so_interventomammo_bck " + "select * from so_interventomammo where idint = " +
                idint.toString();
            am.getTransaction().executeCommand(sql);

            String sql2 =
                "insert into SO_CODREF3LIVMA_BCK " + "select * from SO_CODREF3LIVMA c where c.idint = " +
                idint.toString();
            am.getTransaction().executeCommand(sql2);

            //comunque cancello l'intervento
            r.remove();

            //aggiorno la numerazione degli interventi
            String update =
                "UPDATE SO_INTERVENTOMAMMO SET N_INT=N_INT-1 " + "WHERE IDREFERTO=" + idref + " AND N_INT>" + n;
            am.getTransaction().executeCommand(update);

            update = "DELETE FROM SO_CODREF3LIVMA WHERE IDINT=" + idint;
            am.getTransaction().executeCommand(update);
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
            vo.executeQuery();


        } catch (Exception ex) {
            this.whenException("Impossibile cancellare l'intervento: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiMAView1", "Ref_SoInterventomammoView1" });
        }
        
        return null;
    }

}
