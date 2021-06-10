package view.referto;

import insiel.dataHandling.DateUtils;

import java.util.Map;

import model.common.RefCo_AppModule;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class RefCo_diagnosiAction extends RefCo_DataForwardAction {
    private RichForm diagnosiForm;

    public RefCo_diagnosiAction() {
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

    public void setDiagnosiForm(RichForm diagnosiForm) {
        this.diagnosiForm = diagnosiForm;
    }

    public RichForm getDiagnosiForm() {
        if (diagnosiForm == null){
            this.filter();
        }
        return diagnosiForm;
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
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //Diagnois peggiore
        ViewObject vo = am.findViewObject("Ref_SoCnfRef2livISTOCOView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

    }
}
