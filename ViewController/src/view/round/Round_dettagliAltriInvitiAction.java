package view.round;

import javax.faces.event.ActionEvent;

import model.common.Round_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.ViewObject;

public class Round_dettagliAltriInvitiAction extends Round_DataForwardAction {
    private RichForm frmDetails;

    public Round_dettagliAltriInvitiAction() {
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
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        if (hpvAttivo.booleanValue()) {
            return "Round_SoConteggiInvitiView1";
        }
        return "Round_SoSoggettiFilteredView2";
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
            ViewObject vo = am.findViewObject(this.getElenco_voName());
            vo.clearCache();
        }
    }

    public String onBack() {
        //poiche' ho ripopolato la cache, se esco dall apagina devo ripulirla
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject(this.getElenco_voName());
        vo.clearCache();
        return "back";
    }

    public void onSetInClause(ActionEvent actionEvent) {
        super.onSetInClause();
    }
}
