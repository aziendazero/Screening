package view.round;

import model.common.Round_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.ViewObject;

public class Round_dettagliSollecitiAction extends Round_DataForwardAction {
    private RichForm frmDetails;

    public Round_dettagliSollecitiAction() {
    }

    public void setFrmDetails(RichForm frmDetails) {
        this.frmDetails = frmDetails;
    }

    public RichForm getFrmDetails() {
        if (frmDetails == null)
            findForward();

        return frmDetails;
    }

    protected String getElenco_voName() {
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("listaHPV");
        if (hpvAttivo.booleanValue()) {
            return "Round_SoInvitoSollecitiView3";
        }
        return "Round_SoInvitoSollecitiView2";
    }

    protected void findForward() {
        super.onSetInClause();
    }

    protected void beforeNavigate(String dest) throws Exception {

        if (dest != null &&
            !dest.startsWith("goDetails")) {
            //poiche' ho ripopolato la cache, se esco dall apagina devo ripulirla
            Round_AppModule am =
(Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            ViewObject vo = am.findViewObject("Round_SoInvitoSollecitiView2");
            vo.clearCache();
            vo = am.findViewObject("Round_SoInvitoSollecitiView3");
            vo.clearCache();
        }
    }

    public String onBack() {
        //poiche' ho ripopolato la cache, se esco dall apagina devo ripulirla
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoInvitoSollecitiView2");
        vo.clearCache();
        vo = am.findViewObject("Round_SoInvitoSollecitiView3");
        vo.clearCache();
        return "back";
    }
}
