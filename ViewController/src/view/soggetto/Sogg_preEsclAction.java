package view.soggetto;

import java.util.Map;

import model.common.Sogg_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.ViewObject;

public class Sogg_preEsclAction {
    private RichForm frmescl;

    public Sogg_preEsclAction() {
    }

    public void setFrmescl(RichForm frmescl) {
        this.frmescl = frmescl;
    }

    public RichForm getFrmescl() {
        if (frmescl == null)
            findForward();

        return frmescl;
    }

    @SuppressWarnings("unchecked")
    public void findForward() {
        @SuppressWarnings("deprecation")
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        // filtro tipo invito
        ViewObject voTI = am.findViewObject("Sogg_TipoInvitoView1");
        voTI.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'");
        voTI.executeQuery();

        session.put("esclModAnnullo", Boolean.FALSE);

    }
}
