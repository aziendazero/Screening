package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.text.SimpleDateFormat;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefMa_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import model.referto.common.Ref_SoRefertomammo1livViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

public class RefMa_refertoCAction extends RefMa1l_DataForwardAction {
    private RichForm conclusivoForm;


    public void setConclusivoForm(RichForm conclusivoForm) {
        this.conclusivoForm = conclusivoForm;
    }

    public RichForm getConclusivoForm() {
        if (conclusivoForm == null) {
            //filtro i dati
            this.filter();
            //leggo i dati per tutto
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
            Row r = refVoIter.getCurrentRow();
            Map sess = ADFContext.getCurrent().getSessionScope();
            Integer n_letture = (Integer) sess.get("n_referti_1liv");
            //se ho una revisione leggo solo le indiczioni del revisore...
            if (n_letture.intValue() == 3 && r.getAttribute("REsito") != null &&
                ((Integer) r.getAttribute("REsito")).intValue() > 0)
                GestoreRefertiUI.loadFromDB1livMA(3);
            else
                //... altrimenti le leggo tutte assieme
                GestoreRefertiUI.loadFromDB1livMA(0);
        }
        return conclusivoForm;
    }

    private void filter() {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
        RefMa_AppModule am = (RefMa_AppModule) refVoIter.getViewObject().getApplicationModule();
        Row r = refVoIter.getCurrentRow();
        if (r == null)
            return;
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //LETTORE
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
        // Gaion 17062013 : se e' completo non filtro per ulss:  per la rete mammografica
        if (((Integer) r.getAttribute("Completo")).intValue() == 1) {
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_RADIOLOGO, null, tpscr);
        } else {
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_RADIOLOGO, ulss, tpscr);
        }

        //indicazioni
        vo = am.findViewObject("Ref_SoCnfRef1livMXINDIView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //centro di refertazione
        vo = am.findViewObject("Ref_SoCentroRefertoView2");
        ViewObjectFilters.filterCentri(vo, ulss, tpscr, data);

        //esiti
        vo = am.findViewObject("Ref_SoCnfRef1livMXEST1View1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //DENSITA
        vo = am.findViewObject("Ref_SoCnfRef1livMXDENSView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
    }

    public void onChangeDataReferto(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
        RefMa_AppModule am = (RefMa_AppModule) refVoIter.getViewObject().getApplicationModule();
        try {
            Map sess = ADFContext.getCurrent().getSessionScope();
            String ulss = (String) sess.get("ulss");
            String tpscr = (String) sess.get("scr");

            // Determino la data referto
            Ref_SoRefertomammo1livViewRow refertoRow =
                (Ref_SoRefertomammo1livViewRow) refVoIter.getCurrentRow();
            String dateStr = (String)valueChangeEvent.getNewValue();         
            java.util.Date dataref = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
            Date dataReferto = DateUtils.getOracleDate(dataref);

            // Filtro i suggerimenti
            ViewObject suggVo = am.findViewObject("Ref_SoCnfSugg1livView1");
            ViewObjectFilters.filterCnfReferti(suggVo, ulss, tpscr, dataReferto);

            // Verifico che i suggerimenti del referto siano ancora validi
            if (refertoRow.getL1Sugg() != null) {
                if (suggVo.getFilteredRows("Idsugg", refertoRow.getL1Sugg()).length == 0) {
                    throw new Exception("Il suggerimento della prima lettura non e' valido alla data del referto");
                }
            }
            if (refertoRow.getL2Sugg() != null && refertoRow.getL2Sugg().intValue() != 0) {
                if (suggVo.getFilteredRows("Idsugg", refertoRow.getL2Sugg()).length == 0) {
                    throw new Exception("Il suggerimento della seconda lettura non e' valido alla data del referto");
                }
            }
            if (refertoRow.getRevisione() != null && refertoRow.getRevisione().intValue() != 0) {
                if (refertoRow.getRSugg() != null && refertoRow.getRSugg().intValue() != 0) {
                    if (suggVo.getFilteredRows("Idsugg", refertoRow.getRSugg()).length == 0) {
                        throw new Exception("Il suggerimento della revisione non e' valido alla data del referto");
                    }
                }
            }
        } catch (Exception e) {
            this.handleException(e.getMessage(), am.getTransaction());
        }
    }
    
    protected int getNLettura() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        
        ViewObject refVoIter = am.findViewObject("Ref_SoRefertomammo1livView1");
        Row r = refVoIter.getCurrentRow();
        if (r == null)
            return 0;
        Map sess = ADFContext.getCurrent().getSessionScope();
        Integer n_letture = (Integer) sess.get("n_referti_1liv");

        //se ho una revisione leggo solo le indicazioni del revisore
        if (n_letture.intValue() == 3 && r.getAttribute("Idsugg") != null &&
            ((Integer) r.getAttribute("Idsugg")).intValue() > 0)
            return 3;
        else
            return 0;
    }
}
