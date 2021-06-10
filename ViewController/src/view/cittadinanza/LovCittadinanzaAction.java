package view.cittadinanza;

import java.util.Map;

import model.common.Sogg_AppModule;

import model.soggetto.common.Sogg_SoSoggettoViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class LovCittadinanzaAction {
    public LovCittadinanzaAction() {
    }

      public static String checkOnLovFilter() {
          LovCittadinanzaAction l = new LovCittadinanzaAction();
          return l.onLovFilter();
      }

    @SuppressWarnings("unchecked")
    public String onLovFilter() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        String st = req.get("searchText") != null ? req.get("searchText").toString() : "";
        boolean onOneReturn = false;
        
        try{
            onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
        }catch(Throwable th){
            onOneReturn = false;
        }

        @SuppressWarnings("deprecation")
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        String whcl =
            "UPPER(descrizione) LIKE '" + st +
            "%' AND (dtinizioval IS NULL OR dtinizioval < SYSDATE) AND (dtfineval IS NULL OR dtfineval > SYSDATE)";
        ViewObject cittadinanzaView = am.findViewObject("Sogg_SoCittadinanzaView1");
        cittadinanzaView.setWhereClause(whcl);
        cittadinanzaView.executeQuery();
        
        if(onOneReturn && cittadinanzaView.getEstimatedRowCount()==1){
            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
            Row citt = cittadinanzaView.first();
            
            soggettoRow.setCittadinanza((String)citt.getAttribute("Descrizione"));
            soggettoRow.setReleaseCodeCit((Integer) citt.getAttribute("ReleaseCode"));
        } else 
            req.put("onOneReturn", false);

        return "searchcompleted";
    }
}
