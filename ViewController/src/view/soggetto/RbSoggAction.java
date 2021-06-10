package view.soggetto;

import java.util.Map;

import model.common.Sogg_AppModule;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.jbo.ViewObject;

import view.commons.AppConstants;
import view.commons.PageDescriptor;

public class RbSoggAction {
    @SuppressWarnings({ "unchecked", "deprecation" })
    public String findForward() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        //TODO: patch per la perdita delle bind variables VERIFICARE!!!!
        ViewObject vo = am.findViewObject("Sogg_RicercaView1");
        vo.setWhereClauseParams(new Object[] { ulss, tpscr, ulss, tpscr, tpscr });
        am.doRollback("Sogg_RicercaView1");

        // Se provengo da una pagina diversa dalla ricerca, torno ad essa
        PageDescriptor page = (PageDescriptor) ADFContext.getCurrent().getSessionScope().get(AppConstants.FROM_PAGE);
        System.out.println("------------------------> " + page);
        if (page != null) {
            return page.getAction();
        }

        Map req = adfCtx.getPageFlowScope();
        req.put("initSearch", false);

        System.out.print("---------------> rb" + req.get("initSearch"));
        return "iniSogg";
    }
}
