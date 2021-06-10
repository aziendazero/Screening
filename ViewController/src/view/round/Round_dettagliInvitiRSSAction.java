package view.round;

import model.common.Round_AppModule;

import oracle.adf.model.BindingContext;

import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.ViewObject;

public class Round_dettagliInvitiRSSAction extends Round_DataForwardAction {
    private RichForm frmDetails;

    protected String getElenco_voName() {
        return "Round_SoSoggettiFilteredView4";
    }

    protected void findForward() {

        super.onSetInClause();
        //super.findForward(actionContext);
    }

    protected void beforeNavigate(String dest) throws Exception {

        if (dest != null &&
            !dest.startsWith("goDetails")) {
            //poiche' ho ripopolato la cache, se esco dall apagina devo ripulirla
            Round_AppModule am =
(Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            ViewObject vo = am.findViewObject("Round_SoSoggettiFilteredView4");
            vo.clearCache();
        }
    }

    public String onBack() {
        //poiche' ho ripopolato la cache, se esco dall apagina devo ripulirla
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoSoggettiFilteredView4");
        vo.clearCache();
        return "back";
    }

    public void setFrmDetails(RichForm frmDetails) {
        this.frmDetails = frmDetails;
    }

    public RichForm getFrmDetails() {
        if (frmDetails == null)
            findForward();

        return frmDetails;
    }
}
