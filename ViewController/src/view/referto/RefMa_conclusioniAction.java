package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

public class RefMa_conclusioniAction extends RefMa_DataForwardAction {
    private RichForm conclusioniForm;

    public RefMa_conclusioniAction() {
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

    public void setConclusioniForm(RichForm conclusioniForm) {
        this.conclusioniForm = conclusioniForm;
    }

    public RichForm getConclusioniForm() {
        if (conclusioniForm == null){
            this.filter();
        }
        return conclusioniForm;
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
        Map session = adfCtx.getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        Date dataConcl = (Date) r.getAttribute("Dtconcl");
        Date d = (Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //medico radiologo oppure senologo
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView1Iterator");
        ViewObject vo = voIter.getViewObject();
        ViewObjectFilters.filterOpMedici(vo, data, null,
                                         new Integer[] { ConfigurationConstants.CODICE_RADIOLOGO,
                                                        ConfigurationConstants.CODICE_SENOLOGO }, ulss, tpscr);

        //suggerimenti
        voIter = bindings.findIteratorBinding("Ref_SoCnfSugg2livView1Iterator");
        ViewObject suggVo = voIter.getViewObject();
        ViewObjectFilters.filterCnfReferti(suggVo, ulss, tpscr, dataConcl);
    }

    public void onChangeDataConcl(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        ViewObject refertoVo = voIter.getViewObject();
        Row refertoRow = refertoVo.getCurrentRow();

        Date dataConcl = (Date) refertoRow.getAttribute("Dtconcl");
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // Filtro i suggerimenti
        voIter = bindings.findIteratorBinding("Ref_SoCnfSugg2livView1Iterator");
        ViewObject suggVo = voIter.getViewObject();
        ViewObjectFilters.filterCnfReferti(suggVo, ulss, tpscr, dataConcl);
    }
}
