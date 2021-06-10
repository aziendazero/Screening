package view.soggetto;

import java.util.Map;

import model.common.Sogg_AppModule;

import model.commons.ViewHelper;

import model.inviti.InvitoUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

@SuppressWarnings({ "deprecation", "unchecked" })
public class RbNuovoInvitoAction {
    public RbNuovoInvitoAction() {
    }

    public String findForward() {
        // TODO:  Override this oracle.adf.controller.struts.actions.DataAction method
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        Row cRow = voInvito.getCurrentRow();

        Date dtApp = (Date) cRow.getAttribute("Dtapp");
        Integer idApp = (Integer) cRow.getAttribute("Idapp");
        Integer idInvito = (Integer) cRow.getAttribute("Idinvito");
        String codTs = (String) cRow.getAttribute("Codts");
        String ulss = (String) cRow.getAttribute("Ulss");
        String tpscr = (String) cRow.getAttribute("Tpscr");

        // salvo key della riga corrente
        ViewObject vo = am.findViewObject("Sogg_RicercaView1");
        Row r = vo.getCurrentRow();
        Key k = null;
        if (r != null) {
            k = r.getKey();
        }
        //

        am.getTransaction().rollback();

        if (dtApp == null && idApp == null) {
            try {
                String sql = "select count(*) from so_invito i where i.idinvito = " + idInvito.toString();
                ViewObject voCount = am.createViewObjectFromQueryStmt("", sql);
                voCount.setRangeSize(-1);
                voCount.executeQuery();

                Row fRow = voCount.first();
                Number cnt = (Number) fRow.getAttribute(0);

                if (cnt.intValue() == 1){
                    Map session = ADFContext.getCurrent().getSessionScope();
                    String user = (String) session.get("user");
                    InvitoUtils.cancellaInvito(am, idInvito.intValue(), codTs, ulss, tpscr, user);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // ripristino riga corrente
        vo.executeQuery();
        if (k != null) {
            ViewHelper.setCurrent(vo, k);
        }
        //

        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getPageFlowScope();
        req.put("initSearch", false);

        System.out.print("---------------> rb" + req.get("initSearch"));
        return "iniSogg";
    }
}
