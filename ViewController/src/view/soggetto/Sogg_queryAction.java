package view.soggetto;

import java.util.Map;

import model.common.Sogg_AppModule;

import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ViewObject;

@SuppressWarnings({ "deprecation", "unchecked" })
public class Sogg_queryAction {
    public Sogg_queryAction() {
    }

    public String findForward() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Sogg_RicercaView1");
        //TODO: patch per la perdita delle bind variables VERIFICARE!!!!
        vo.setWhereClauseParams(new Object[] { ulss, tpscr, ulss, tpscr, tpscr });
        ViewHelper.queryAndRestoreCurrentRow(vo);

        Map req = adfCtx.getPageFlowScope();
        req.put("initSearch", false);

        System.out.print("---------------> rb" + req.get("initSearch"));
        return "iniSogg";
    }
}
