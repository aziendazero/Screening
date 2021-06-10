package view.soggetto;

import java.util.Map;

import model.common.Sogg_AppModule;

import model.datacontrol.Sogg_NuovoParam;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

public class Sogg_preInvCorrAction {
    private RichForm frminvcorr;

    public Sogg_preInvCorrAction() {
    }

    public void setFrminvcorr(RichForm frminvcorr) {
        this.frminvcorr = frminvcorr;
    }

    public RichForm getFrminvcorr() {
        if (frminvcorr == null){
            findForward();
            Sogg_invCorrAction.findForward();
        }
        
        return frminvcorr;
    }

    @SuppressWarnings("unchecked")
    public void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("chAgenda", "sogg_preInvCorr.do");
        session.remove("testPropostoOrig");

        // filtro tipi invito
        @SuppressWarnings("deprecation")
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        ViewObject invitoView = am.findViewObject("Sogg_SoInvitoView1");
        Row invitoRow = invitoView.getCurrentRow();
        Integer idInvito = (Integer) invitoRow.getAttribute("Idinvito");
        
        invitoView.setWhereClause("IDINVITO="+idInvito);
        invitoView.executeQuery();
        invitoRow = invitoView.first();
        
        ViewObject voTI = am.findViewObject("Sogg_TipoInvitoView1");
        String whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";

        // Filtro per data fine validita
        Date dataApp = (Date) invitoRow.getAttribute("Dtapp");
        String strData;
        if (dataApp != null) {
            strData = dataApp.dateValue().toString();
        } else {
            strData = new java.sql.Date(new java.util.Date().getTime()).toString();
        }
        whcl += " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" + strData + "', 'YYYY-MM-DD'))";

        voTI.setWhereClause(whcl);
        voTI.executeQuery();

        session.put("invPf2l", Boolean.FALSE);
        Integer livello = (Integer) invitoRow.getAttribute("Livello");
        String esito = (String) invitoRow.getAttribute("Codesitoinvito");
        if (tpscr.equals("PF") && livello.intValue() == 2 && (esito.equals("?") || esito.equals("S"))) {
            //negli inviti pfas di 2 livello, anche se c'e' un referto aperto
            //e' possibile spostare un appuntamento
            session.put("invPf2l", Boolean.TRUE);
            String qry =
                "SELECT completo FROM SO_REFERTOPFAS2LIV where IDINVITO=" + idInvito + " and ulss = '" + ulss +
                "' and tpscr = '" + tpscr + "'";
            ViewObject voQry = am.createViewObjectFromQueryStmt("", qry);
            voQry.setRangeSize(-1);
            voQry.executeQuery();
            Row rRow = voQry.first();
            if (rRow != null) {
                Integer completo = rRow.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) rRow.getAttribute(0)).intValue() : null;
                if (completo.intValue() > 0) {
                    session.put("invPf2l", Boolean.FALSE);
                }
            }
        }
        
        //categoria cittadino
        ViewObject soggscrView = am.findViewObject("Sogg_SoSoggScrView1");
        Row soggScr = soggscrView.getCurrentRow();
        Sogg_NuovoParam bean =
            (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
        bean.setCodClassePop((String)soggScr.getAttribute("Codclassepop"));
        
        Date dataCreaInv = (Date) invitoRow.getAttribute("Dtcreazione");
        String strDataInsInv;
        if (dataCreaInv != null) {
            strDataInsInv = dataCreaInv.dateValue().toString();
        } else {
            strDataInsInv = new java.sql.Date(new java.util.Date().getTime()).toString();
        }
        
        if (tpscr.equals("CO") && (Boolean)session.get("covid19")){
            ViewObject motView = am.findViewObject("SoCnfRef1LivMTEXECView1");
            motView.setWhereClause(" (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" + strDataInsInv + "', 'YYYY-MM-DD'))");
            motView.executeQuery();            
        }
    }
}
