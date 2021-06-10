package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class RefMa_diagnosiAction extends RefMa_DataForwardAction {
    private RichForm diagnosiForm;

    public RefMa_diagnosiAction() {
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

    public void setDiagnosiForm(RichForm diagnosiForm) {
        this.diagnosiForm = diagnosiForm;
    }

    public RichForm getDiagnosiForm() {
        if (diagnosiForm == null){
            //filtro i dati
            this.filter();
        }
        return diagnosiForm;
    }
    
    private void filter()
      {
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
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //esito di citologia
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livMXCESTView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, data);

        //esito di agobiopsia
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livMXAESTView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
        
        //diagnois peggiore
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livRACDIAView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
        
      }
}
