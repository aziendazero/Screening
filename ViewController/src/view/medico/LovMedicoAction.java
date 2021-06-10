package view.medico;

import java.util.Map;

import model.common.Sogg_AppModule;

import model.datacontrol.Sogg_RicParam;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

public class LovMedicoAction {
    public LovMedicoAction() {
    }
    
    public static String checkOnLovFilter() {
        LovMedicoAction l = new LovMedicoAction();
        return l.onLovFilter();
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    public String onLovFilter() {
        BindingContext ctx = BindingContext.getCurrent();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        Map sess = adfCtx.getSessionScope();
        Map page = adfCtx.getPageFlowScope();

        String st = req.get("searchText") != null ? req.get("searchText").toString() : "";
        String src = page.get("source").toString();
        sess.put("lovSource", src);

        st = st.toUpperCase();

        Sogg_AppModule am = (Sogg_AppModule) ctx.findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voCom = am.findViewObject("Sogg_MedicoView1");
        String whcl = "COGNOME like '" + st + "%'";
        /* MOD 20090316
           if (src.equals("VB_desMedico"))
            {
              String ulss = (String) sess.getAttribute("ulss");
              whcl += " AND ULSS = '" + ulss + "'";

            }
            else*/

        String ulss = (String) sess.get("ulss");
        whcl += " AND ULSS = '" + ulss + "'";
        if (!src.equals("VB_desMedico"))
        /* fine mod */
        {
            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            Row cAnag = voAnag.getCurrentRow();
            Date dtCrea = (Date) cAnag.getAttribute("Dtcreazione");

            whcl +=
                " and (DTFINEVALOP is null or DTFINEVALOP >= to_date('" + (dtCrea == null ? "SYSDATE" : dtCrea) +
                "','dd/MM/yyyy'))";
        }

        voCom.setWhereClause(whcl);
        voCom.executeQuery();

        return "searchcompleted";
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    public String onLovSelect() {
        BindingContext ctx = BindingContext.getCurrent();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        Sogg_AppModule am = (Sogg_AppModule) ctx.findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ViewObject voMed = am.findViewObject("Sogg_MedicoView1");
        Row cMed = voMed.getCurrentRow();
        String cogn = (String) cMed.getAttribute("Cognome");
        String nome = (String) cMed.getAttribute("Nome");
        Integer codMed = (Integer) cMed.getAttribute("Codiceregmedico");

        String src = (String) sess.get("lovSource");
        if (src.equals("VB_desMedico")) {
            Sogg_RicParam bean = (Sogg_RicParam) ctx.findDataControl("Sogg_RicParamDataControl").getDataProvider();
            bean.setDesMedico(cogn);
            bean.setIdMedico(codMed);
            sess.put("codMed", codMed);
            // ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            // Row cRow = voAnag.getCurrentRow();

        } else if (src.equals("VB_Cognmed")) {
            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            Row cRow = voAnag.getCurrentRow();
            Integer codiMed = new Integer(codMed.intValue());
            cRow.setAttribute("Codiceregmedico", codiMed);
            cRow.setAttribute("Cognmed", cogn);
            cRow.setAttribute("Nomemed", nome);

        }

        return "selected";
    }
}
