package view.referto;


import insiel.utilities.dataformats.DateUtils;

import java.util.HashMap;

import java.util.Map;

import javax.faces.event.ActionEvent;

import model.common.RefMa_AppModule;

import model.common.Ref_SoInterventomammoViewRow;

import model.commons.ConfigurationConstants;

import model.commons.ViewHelper;

import model.datacontrol.Ref_2livBean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;


import oracle.adf.view.rich.component.rich.RichForm;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import org.apache.myfaces.trinidad.context.RequestContext;

import view.util.Utility;

public class RefMa_interventoAction extends RefMa_InterventiDataForwardAction {

    private RichForm interventoForm;

    protected void updateDB() { //aggiornamento dati di configurazione positivi

        GestoreRefertiUI.updateDB3livMA();

    }

    protected void loadFromDB() {
        GestoreRefertiUI.loadFromDB3livMA();
    }


    public String onRichnav(String destination, HashMap params) {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        boolean modP = am.getTransaction().isDirty();

        Map req = ADFContext.getCurrent().getRequestScope();
        req.putAll(params);

        //controllo anche eventuali variazioni all'interfaccia, registrate tramite il bean
        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        boolean modP2 = bean.isDirty();

        //se ci sono modifiche non salvate...
        if (modP || modP2) { //imposto l'attributo destinazione e lascio al metodo onLoad la gestione
            //non faccio niente
            req.put("destNav", destination);
            ADFContext.getCurrent().getViewScope().put("destNav", destination);
            RequestContext.getCurrentInstance().addPartialTarget(getFvDest());
            Utility.addScriptOnPartialRequest("wantToSaveOnLoad()");
            return "error";
        } else { //altriemnti navigo verso la destinzione

            try {
                this.beforeNavigate(destination);

                String suffix = "";
                if (destination.indexOf("?") >= 0)
                    suffix = "_p";
                int index = destination.indexOf(".");
                if (index >= 0)
                    destination = destination.substring(0, index);
                return (destination + suffix);
            } catch (Exception ex) {
                this.handleException("Impossibile navigare verso la destinazione " + destination + ": " +
                                     ex.getMessage(), null);
            }
        }
        return "error";
    }


    public void onChangeDataConcl() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");

        // Determino la data suggerimento
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        Row refertoRow = refVoIter.getCurrentRow();
        Date dataConcl = (Date) refertoRow.getAttribute("Dtconcl");

        // Filtro i suggerimenti
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoCnfSugg3livView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, dataConcl);
    }

    public void onCreateIntervento(ActionEvent actionEvent) {
        super.onCreateIntervento(actionEvent);

        GestoreRefertiUI.loadFromDB3livMA();
    }

    public void setInterventoForm(RichForm interventoForm) {
        this.interventoForm = interventoForm;
    }

    public RichForm getInterventoForm() {
        if (interventoForm == null) {
            //filtro i dati
            this.filter();
            GestoreRefertiUI.loadFromDB3livMA();
        }
        return interventoForm;
    }

    private void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        Row r = refVoIter.getCurrentRow();
        if (r == null)
            return;
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        // Recupero la data suggerimento
        Date dataSugg = null;
        DCIteratorBinding intVoIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        ViewObject interventoVo = intVoIter.getViewObject();
        Row interventoRow = interventoVo.getCurrentRow();
        if (interventoRow != null) {
            dataSugg = (Date) interventoRow.getAttribute("Dtconcl");
        }

        //medico CHIRURGO
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView4Iterator");
        ViewObjectFilters.filterOpMedici(voIter.getViewObject(), data, null, ConfigurationConstants.CODICE_CHIRURGO,
                                         ulss, tpscr);

        //medico CHIRURGO O SENOLOGO (VISITA PRE-OPERATORIA)
        voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView1Iterator");
        ViewObjectFilters.filterOpMedici(voIter.getViewObject(), data, null,
                                         new Integer[] { ConfigurationConstants.CODICE_SENOLOGO,
                                                         ConfigurationConstants.CODICE_CHIRURGO }, ulss, tpscr);

        //MEDICO CHIRURGO, RADIOLOGO O SENLOGO (CONCLUSIONI)
        voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView5Iterator");
        ViewObjectFilters.filterOpMedici(voIter.getViewObject(), data, null,
                                         new Integer[] { ConfigurationConstants.CODICE_RADIOLOGO,
                                                         ConfigurationConstants.CODICE_SENOLOGO,
                                                         ConfigurationConstants.CODICE_CHIRURGO,
                                                         ConfigurationConstants.CODICE_ONCOLOGO, }, ulss, tpscr);

        //esecuzione dell'intervento
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livINTVALView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, data);

        //motivo di inesecuzione
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livINTMIEView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, data);

        //tipo di intervento
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livINTTIPView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, data);

        //focalita'
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livISTOLSView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, data);

        //interventi su ascella
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livINTASCView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, data);

        //sede dell'intervento
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livINTSEDView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, data);

        //suggerimenti
        voIter = bindings.findIteratorBinding("Ref_SoCnfSugg3livView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, dataSugg);
    }

    public void onNextIntervento(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        Ref_SoInterventomammoViewRow r = (Ref_SoInterventomammoViewRow) voIter.getViewObject().getCurrentRow();
        if (!(r != null && r.getAttribute("Completo") != null &&
              ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo"))))
            this.onApply();

        OperationBinding nextBinding = bindings.getOperationBinding("Next");
        nextBinding.execute();

        loadFromDB();
    }

    public void onPreviousIntervento(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        Ref_SoInterventomammoViewRow r = (Ref_SoInterventomammoViewRow) voIter.getViewObject().getCurrentRow();
        if (!(r != null && r.getAttribute("Completo") != null &&
              ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo"))))
        this.onApply();

        OperationBinding prevBinding = bindings.getOperationBinding("Previous");
        prevBinding.execute();

        loadFromDB();
    }

}
