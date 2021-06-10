package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefCo_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

public class RefCo_conclusioniAction extends RefCo_DataForwardAction {
    private RichForm conclForm;

    public RefCo_conclusioniAction() {
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

    public void onChangeDataConcl(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        ViewObject refertoVo = voIter.getViewObject();
        Row refertoRow = refertoVo.getCurrentRow();

        Date dataConcl = (Date) refertoRow.getAttribute("Dtconcl");

        voIter = bindings.findIteratorBinding("Ref_SoCnfSugg2livView1Iterator");

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // Filtro i suggerimenti
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, dataConcl);
    }

    public void setConclForm(RichForm conclForm) {
        this.conclForm = conclForm;
    }

    public RichForm getConclForm() {
        if (conclForm == null){
            //filtro i dati
            this.filter();
        }
        return conclForm;
    }
    
    protected void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
        Row r = voIter.getCurrentRow();
        if (r == null)
            return;

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        Date dataConcl = (Date) r.getAttribute("Dtconcl");
        Date d = (Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //radiologo o gastroenterologo
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
        ViewObjectFilters.filterOpMedici(vo, data, null,
                                         new Integer[] { ConfigurationConstants.CODICE_GASTROENTEROLOGO,
                                                        ConfigurationConstants.CODICE_RADIOLOGO }, ulss, tpscr);

        //QUALITA
        vo = am.findViewObject("Ref_SoCnfRef2livCO2LIVView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //CONCLUSIONI
        vo = am.findViewObject("Ref_SoCnfRef2livRXCONCView1_1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //suggerimenti
        vo = am.findViewObject("Ref_SoCnfSugg2livView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, dataConcl);
    }
}
