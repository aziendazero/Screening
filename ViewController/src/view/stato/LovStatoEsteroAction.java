package view.stato;

import java.util.Map;

import model.common.Sogg_AppModule;

import model.soggetto.common.Sogg_SoSoggettoViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;


@SuppressWarnings("deprecation")
public class LovStatoEsteroAction {
    public LovStatoEsteroAction() {
    }

    public static String checkOnLovFilter() {
        LovStatoEsteroAction l = new LovStatoEsteroAction();
        return l.onLovFilter();
    }

    @SuppressWarnings("unchecked")
    public String onLovFilter() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();

        String st = req.get("searchText") != null ? req.get("searchText").toString() : "";

        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voSt = am.findViewObject("Sogg_StatoView1");

        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        int cittadinanza = soggettoRow.getReleaseCodeCit() != null ? soggettoRow.getReleaseCodeCit().intValue() : 0;

        String whcl = "DESCRIZIONE like '" + st + "%'";

        // Se cittadinanza sconosciuta o italiana
        if (cittadinanza == 0 || cittadinanza == 1) {
            Date dataNascita = soggettoRow.getDataNascita();
            whcl +=
                " AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= :1) AND (dtfinevalidita IS NULL OR dtfinevalidita > :2)";
            voSt.setWhereClauseParam(0, dataNascita);
            voSt.setWhereClauseParam(1, dataNascita);
        } else {
            whcl +=
                " AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE)";
        }

        // TODO: Aggiungere lo stato attuale, se non gia' presente nella select

        voSt.setWhereClause(whcl);
        voSt.executeQuery();
        
        boolean onOneReturn = false;
        
        try{
            onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
        }catch(Throwable th){
            onOneReturn = false;
        }
        
        if(onOneReturn && voSt.getEstimatedRowCount()==1){
            voSt.setCurrentRow(voSt.first());
            onLovSelect();
        } else 
            req.put("onOneReturn", false);
        
        return "searchcompleted";
    }

    public String  onLovSelect() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voSt = am.findViewObject("Sogg_StatoView1");
        Row cSt = voSt.getCurrentRow();
        String codSt = (String) cSt.getAttribute("Codst");
        String desSt = (String) cSt.getAttribute("Descrizione");
        Integer releaseCode = (Integer) cSt.getAttribute("ReleaseCode");
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        soggettoRow.setCodst(codSt);
        soggettoRow.setDesstato(desSt);
        soggettoRow.setReleaseCodeStNas(releaseCode);
        
        return "selected";
    }
}

