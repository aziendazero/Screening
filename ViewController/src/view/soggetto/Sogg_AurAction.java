package view.soggetto;

import java.util.Map;

import model.commons.SoggUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.binding.OperationBinding;

import view.commons.action.Parent_DataForwardAction;

public class Sogg_AurAction extends Parent_DataForwardAction {
    private RichForm frmSearch;

    public Sogg_AurAction() {
        super();
    }
    
    @Override
    protected void setAppModule() {
        this.amName="Sogg_AppModule";
    }
    
    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.remove("FROM_AUR");

        SoggUtils.beforeNavSogg(dest, true);
    }

    public void setFrmSearch(RichForm frmSearch) {
        this.frmSearch = frmSearch;
    }

    public RichForm getFrmSearch() {
        if (frmSearch == null)
            findForward();
        
        return frmSearch;
    }
    
    protected void findForward() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        
        if (this.frmSearch == null)
            this.frmSearch = new RichForm();

        if(session.containsKey("FROM_AUR") && (Boolean)session.get("FROM_AUR"))
            return;
        
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding voIterAUR = bindings.findIteratorBinding("personsIterator");

        voIterAUR.getViewObject().executeEmptyRowSet();
    }    
}
