package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefMa_AppModule;

import model.commons.ConfigurationConstants;

import model.datacontrol.Ref_2livBean;

import model.referto.common.Ref_SoRefertomammo1livViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class RefMa1l_DataForwardAction extends RefMa_DataForwardAction {

    @Override
    protected void afterUpdateModel() {
        // TODO Implement this method
    }

    @Override
    protected void updateDB() throws Exception {
        // Aggiornamento dati di configurazione positivi

        //12032012 gaion: doppio cieco
        Map sess = ADFContext.getCurrent().getSessionScope();
        Boolean authDoppioCieco = (Boolean) sess.get("SODoppioCieco");
        if (authDoppioCieco != null && authDoppioCieco.booleanValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
            Row r = voIter.getCurrentRow();

            //controllo che l'utente sia l'autore della lettura
            Integer operatore = getOpeFromUser();
            Integer autore = null;
            switch (getNLettura()) {
            case 1:
                autore = (Integer) r.getAttribute("L1Radiologo");
                break;
            case 2:
                autore = (Integer) r.getAttribute("L2Radiologo");
                break;
            case 3:
                autore = (Integer) r.getAttribute("RRadiologo");
                break;
            }
            if (operatore != null && operatore.intValue() != autore.intValue())
                throw new Exception(" l'utente non coincide con l'autore del referto");
        }
        //fine 12032012

        GestoreRefertiUI.updateDB1livMA(getNLettura());
    }

    @Override
    protected void loadFromDB() {
        GestoreRefertiUI.loadFromDB1livMA(getNLettura());
    }

    protected void beforeSavingReferto() throws Exception {
        super.beforeSavingReferto();
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertomammo1livView1");
        Ref_SoRefertomammo1livViewRow r = (Ref_SoRefertomammo1livViewRow) ref.getCurrentRow();
        if (r == null)
            return;

        // Riporto il centro di refertazione
        ref = am.findViewObject("Ref_SoInvitoView2");
        System.out.println(ref.getQuery());
        Row inv = ref.first();

        if ((r.getIdcentroref() == null && inv.getAttribute("Idcentroref1liv") != null) ||
            (inv.getAttribute("Idcentroref1liv") != null &&
             !inv.getAttribute("Idcentroref1liv").equals(r.getAttribute("Idcentroref")))) {
            r.setIdcentroref((Integer) inv.getAttribute("Idcentroref1liv"));
        }

        //06052013 GAION : progetto studio 45 mx
        //ricavo le densita'
        ViewObject codRef = am.findViewObject("Ref_SoCodref1livmaDENSView1");
        Map sess = ADFContext.getCurrent().getSessionScope();
        Integer n_letture = (Integer) sess.get("n_referti_1liv");
        switch (n_letture.intValue()) {
        case 1:
            { // Una sola lettura, ne riporto l'esito
                r.setEsito(r.getL1Esito());
                r.setIdsugg(r.getL1Sugg());
                Row[] rCods = codRef.getFilteredRows("Lettura", new Integer(1));
                if (rCods.length > 0)
                    r.setDensita(((Integer) rCods[0].getAttribute("Idcnfref")).intValue());
                break;
            }
        case 3:
            {
                // Se c'e' la revisione riporto quella
                if (ConfigurationConstants.DB_TRUE.equals(r.getRevisione())) {
                    r.setEsito(r.getREsito());
                    r.setIdsugg(r.getRSugg());
                    Row[] rCods = codRef.getFilteredRows("Lettura", new Integer(3));
                    if (rCods.length > 0)
                        r.setDensita(((Integer) rCods[0].getAttribute("Idcnfref")).intValue());
                    break;
                }
            }

            // altrimenti lavoro come se avessi due sole letture
        default: // case 2
            {
                // Due letture, ne riporto la piu' grave (il criterio di gravita' e' dato dall'ordine)
                ViewObject sugg = am.findViewObject("Ref_SoCnfRef1livMXEST1View1");
                ViewObject sugg2 = am.findViewObject("Ref_SoCnfSugg1livView1");
                int ordine_1 = -1;
                int ordine_2 = -1;
                int ordine_sugg_1 = -1;
                int ordine_sugg_2 = -1;
                if (r.getL1Esito() != null) {
                    Row[] result = sugg.getFilteredRows("Idcnfref1l", r.getL1Esito());
                    if (result.length == 0)
                        throw new Exception("Esito di primo livello con codice " + r.getL1Esito() + " non trovato");

                    if (result[0].getAttribute("Ordine") != null)
                        ordine_1 = ((Integer) result[0].getAttribute("Ordine")).intValue();

                    result = sugg2.getFilteredRows("Idsugg", r.getL1Sugg());
                    if (result.length == 0)
                        throw new Exception("Suggerimento di primo livello con codice " + r.getL1Sugg() +
                                            " non trovato");

                    if (result[0].getAttribute("Ordine") != null)
                        ordine_sugg_1 = ((Integer) result[0].getAttribute("Ordine")).intValue();
                }

                if (r.getL2Esito() != null) {
                    Row[] result = sugg.getFilteredRows("Idcnfref1l", r.getL2Esito());
                    if (result.length == 0)
                        throw new Exception("Suggerimento di primo livello con codice " + r.getL2Esito() +
                                            " non trovato");
                    if (result[0].getAttribute("Ordine") != null)
                        ordine_2 = ((Integer) result[0].getAttribute("Ordine")).intValue();

                    result = sugg2.getFilteredRows("Idsugg", r.getL2Sugg());
                    if (result.length == 0)
                        throw new Exception("Suggerimento di primo livello con codice " + r.getL2Sugg() +
                                            " non trovato");
                    if (result[0].getAttribute("Ordine") != null)
                        ordine_sugg_2 = ((Integer) result[0].getAttribute("Ordine")).intValue();
                }

                // mauro 17/12/2009, nuova logica per determinazione referto conclusivo
                // (valuto prima ordine dei suggerimenti e poi ordine esiti)
                if (ordine_sugg_1 > ordine_sugg_2) {
                    r.setIdsugg(r.getL1Sugg());
                    r.setEsito(r.getL1Esito());
                } else if (ordine_sugg_1 < ordine_sugg_2) {
                    r.setIdsugg(r.getL2Sugg());
                    r.setEsito(r.getL2Esito());
                } else if (ordine_1 >= ordine_2) {
                    r.setIdsugg(r.getL1Sugg());
                    r.setEsito(r.getL1Esito());
                } else {
                    r.setIdsugg(r.getL2Sugg());
                    r.setEsito(r.getL2Esito());
                }

                //densita
                //se sono concordanti le 2 letture la riporto altrimenti lascio da completare
                Row[] rCods1 = codRef.getFilteredRows("Lettura", new Integer(1));
                if (rCods1.length > 0) {
                    Integer dens1 = (Integer) rCods1[0].getAttribute("Idcnfref");
                    Row[] rCods2 = codRef.getFilteredRows("Lettura", new Integer(2));
                    if (rCods2.length > 0) {
                        Integer dens2 = (Integer) rCods1[0].getAttribute("Idcnfref");
                        if (dens1.intValue() == dens2.intValue()) {
                            r.setDensita(dens1.intValue());
                        }
                    }
                }

                //13032012 gaion: doppio cieco
                Boolean authDoppioCieco =
                    (Boolean) sess.get("SODoppioCieco");
                if (authDoppioCieco != null && authDoppioCieco.booleanValue()) {
                    if (getNLettura() == 2) {
                        Boolean askForClose =
                            (Boolean) sess.get("ask_close");
                        if (askForClose == null) {
                            sess.put("ask_close", Boolean.TRUE);
                        } else {
                            if (!askForClose.booleanValue()) //se e' gia' stato chiesto
                            {
                                sess.remove("ask_close");
                            }
                        }
                    }
                }
                //fine 13032012
                break;
            }
        }
    }

    /**
     * Metodo che si occupa della chiusura di un referto di 1 livello,
     * e sovrascrive il metodo per il 2 livello.
     *
     * @param ctx
     */
    public void onClose(ClientEvent clientEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
        RefMa_AppModule am = (RefMa_AppModule) refVoIter.getViewObject().getApplicationModule();
        Ref_SoRefertomammo1livViewRow r = null;

        try {
            r = (Ref_SoRefertomammo1livViewRow) refVoIter.getCurrentRow();

            // Aggiorno il db in conseguenza alle selezioni di interfaccia
            this.beforeSavingReferto();

            // Controllo di conformita'
            if (r.getEsito() == null || ConfigurationConstants.DB_FALSE.equals(r.getEsito()))
                throw new Exception("Non e' stato segnalato nessun esito conclusivo");
            if (r.getIdsugg() == null || ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getIdsugg()))
                throw new Exception("Non e' stata segnalata nessuna raccomandazione conclusiva");
            if (r.getDtreferto() == null)
                throw new Exception("Non e' stata inserita la data del referto");
            if (r.getDtmammo() == null)
                throw new Exception("Non e' stata inserita la data dell'esame");
            Map sess = ADFContext.getCurrent().getSessionScope();
            Integer n_letture = (Integer) sess.get("n_referti_1liv");
            this.checkMandatoryData(r, n_letture.intValue());
            this.checkDateOrder(r);

            // Valorizzazioni specifiche per doppio cieco
            Boolean authDoppioCieco = (Boolean) sess.get("SODoppioCieco");
            if (authDoppioCieco != null && authDoppioCieco.booleanValue()) {
                // La chiusura del referto deve impostare la data referto uguale alla data ultima modifica,
                // cioe' adesso (la data ultima modifica e' impostata piu' sopra in beforeSavingReferto())
                r.setDtreferto(r.getDtultmodifica());
            }

            // 13032012 gaion: controllo su autori letture
            this.checkRadiologiLetture(r);

            // Applico le consguenze della chiusura
            this.conseguenzeChiusura(r, am, (String)clientEvent.getParameters().get("ref_lettera"));

            // Il referto e' completo
            r.setCompleto(ConfigurationConstants.DB_TRUE);

            // Salvo il tutto
            am.getTransaction().commit();

            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);

            // 13032012 gaion: rimuovo parametro per doppio cieco
            sess.remove("ask_close");
            // fine 13032012

            FacesContext fc = FacesContext.getCurrentInstance();
            String refreshpage = fc.getViewRoot().getViewId();
            ViewHandler ViewH = fc.getApplication().getViewHandler();
            UIViewRoot UIV = ViewH.createView(fc,refreshpage);
            UIV.setViewId(refreshpage);
            fc.setViewRoot(UIV);
            
        } catch (Exception ex) {
            if (r != null) {

                // Il referto non e' completo
                r.setCompleto(ConfigurationConstants.DB_FALSE);
            }
            this.handleException("Impossibile chiudere il referto: " + ex.getMessage(), null);
        }
    }

    public void onCancelClose() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.put("ask_close", Boolean.FALSE);
    }

    /**
     * Metodo ch e controlla se ci sono tutti i dati necessari per la chiusura,
     * considerando la configurazione dell aulss
     * @throws java.lang.Exception
     * @param ref
     */
    protected void checkMandatoryData(Row ref, int letture) throws Exception {
        if (ref == null)
            return;

        Ref_SoRefertomammo1livViewRow r = (Ref_SoRefertomammo1livViewRow) ref;
        switch (letture) {

        case 3:
            {
                //se la revisione risulta inserita controllo anche la revisione
                if (ConfigurationConstants.DB_TRUE.equals(r.getRevisione())) {
                    if (r.getREsito() == null || r.getREsito().intValue() == 0)
                        throw new Exception("L'esito della revisione non e' stato inserito");

                    if (r.getRRadiologo() == null)
                        throw new Exception("Non e' stato inserito il revisore");

                    if (r.getRSugg() == null || ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getRSugg()))
                        throw new Exception("L'azione conseguente della revisione non e' stata inserita");

                }

            }
        case 2:
            {
                //controllo che ci siano i dati della seconda lettura..
                if (r.getL2Esito() == null || r.getL2Esito().intValue() == 0)
                    throw new Exception("L'esito della seconda lettura non e' stato inserito");

                if (r.getL2Radiologo() == null)
                    throw new Exception("Non e' stato inserito il secondo lettore");

                if (r.getL2Sugg() == null || ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getL2Sugg()))
                    throw new Exception("L'azione conseguente della seconda lettura non e' stata inserita");

                //e proseguo con la prima lettura
            }
        default: //case 1
            { //controllo che ci siano i dati della prima lettura
                if (r.getL1Esito() == null || r.getL1Esito().intValue() == 0)
                    throw new Exception("L'esito della prima lettura non e' stato inserito");

                if (r.getL1Radiologo() == null)
                    throw new Exception("Non e' stato inserito il primo lettore");

                if (r.getL1Sugg() == null || ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getL1Sugg()))
                    throw new Exception("L'azione conseguente della prima lettura non e' stata inserita");

                break;
            }
        }
    }

    /**
     *
     * Se esistono piu' letture dello stesso radiologo non e' possibile chiudere il referto
     * @throws java.lang.Exception
     * @param ctx
     * @param ref
     */
    protected void checkRadiologiLetture(Row ref) throws Exception {
        Integer radiologo1 = (Integer) ref.getAttribute("L1Radiologo");
        Integer radiologo2 = (Integer) ref.getAttribute("L2Radiologo");
        Integer radiologo3 = (Integer) ref.getAttribute("RRadiologo");

        if (radiologo1 != null && radiologo2 != null && radiologo1.intValue() == radiologo2.intValue())
            throw new Exception("Piu' letture risultano effettuate dallo stesso radiologo");

        if (radiologo1 != null && radiologo3 != null && radiologo1.intValue() == radiologo3.intValue())
            throw new Exception("Piu' letture risultano effettuate dallo stesso radiologo");

        if (radiologo2 != null && radiologo3 != null && radiologo2.intValue() == radiologo3.intValue())
            throw new Exception("Piu' letture risultano effettuate dallo stesso radiologo");
    }


    /**
     * Applica le conseguenze della chiusura di un referto di 1 livello
     * @throws java.lang.Exception
     * @param ctx contesto
     * @param am application module
     * @param r record con il referto da chiudere
     */
    protected void conseguenzeChiusura(Row r, RefMa_AppModule am, String to_do) throws Exception {

        if (r == null)
            throw new Exception("Nessun referto da chiudere");

        //String to_do = (String)ADFContext.getCurrent().getRequestScope().get("ref_lettera");

        //creo la lettera di refero
        GestoreReferti gr = new GestoreReferti(am);
        //se devo ricreare la lettera la cancello e ricreo
        if ("ricrea".equals(to_do)) {
            gr.deleteLettera(r);
            gr.creaLetteraDiReferto(r, 1, null);
        }
        //se la lettera non c'e' la creo semplicemente
        else if ("crea".equals(to_do)) {
            gr.creaLetteraDiReferto(r, 1, null);
        }

        //imposto il richiamo
        Map sess = ADFContext.getCurrent().getSessionScope();
        gr.updateInvito(r, am.findViewObject("Ref_SoInvitoView2"), 1,
                        (String) sess.get("user"));

    }

    protected void checkDateOrder(Ref_SoRefertomammo1livViewRow ref) throws Exception {
        oracle.jbo.domain.Date d1 = ref.getDtmammo();
        oracle.jbo.domain.Date d2 = ref.getDtreferto();
        Date dd1 = null;
        Date dd2 = null;

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

        //controllo sulla data della mammografia
        if (dd2 != null) {

            if (dd1.after(dd2))
                throw new Exception("La data del referto (" + DateUtils.dateToString(dd2) +
                                    ") non puo' essere precedente alla data della mammografia (" +
                                    DateUtils.dateToString(dd1) + ")");
        }
    }

    protected int getNLettura() {
        // TODO Implement this method
        return 0;
    }
    
    public void onChangeDirty(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean.setDirty(true);
    }
}
