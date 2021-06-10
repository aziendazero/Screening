package view.soggetto;

import java.util.Map;

import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.SoggUtils;

import model.datacontrol.Sogg_NuovoParam;

import model.inviti.InvitoUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

public class Sogg_preNuovoAction {
    private RichForm bndfrm;

    public Sogg_preNuovoAction() {
    }

    public void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("chAgenda", "sogg_preNuovo.do");
        session.put("nuovoInvitoCheckDate", Boolean.FALSE);

        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        boolean creatoInv = ((Boolean) session.get("invCreato")).booleanValue();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        
        String strDataInsInv = new java.sql.Date(new java.util.Date().getTime()).toString();

        if (creatoInv) {
            SoggUtils.filtraOrari(true);

            // filtro tipi invito
            ViewObject invitoView = am.findViewObject("Sogg_SoInvitoView1");
            Row invitoRow = invitoView.getCurrentRow();
            String codts = (String) invitoRow.getAttribute("Codts");
            Integer idInvito = (Integer) invitoRow.getAttribute("Idinvito");
            Integer attivoCorrente = (Integer) invitoRow.getAttribute("Attivo");
            
            //invitoView.setWhereClause("IDINVITO="+idInvito);
            //invitoView.executeQuery();
            //invitoRow = invitoView.first();
            
            boolean esisteAttivo = InvitoUtils.getInvitoAttivo(am, codts, idInvito.intValue(), ulss, tpscr) != null;
            ViewObject voTI = am.findViewObject("Sogg_TipoInvitoView1");
            String whcl;
            if (esisteAttivo || (attivoCorrente != null && attivoCorrente.intValue() == 1)) {
                whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
            } else {
                /* mauro 30-11-06: modifiche per includere anche accesso spontaneo */
                Integer codCatI = ConfigurationConstants.CODICE_CAT_PRIMO_INVITO;
                Integer codCatV = ConfigurationConstants.CODICE_CAT_VOLONTARIO;
                whcl =
                    "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" + " and IDCATEG in (" + codCatI.toString() +
                    "," + codCatV.toString() + ")";
            }

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

            Date dataCreaInv = (Date) invitoRow.getAttribute("Dtcreazione");
            if (dataCreaInv != null) {
                strDataInsInv = dataCreaInv.dateValue().toString();
            } else {
                strDataInsInv = new java.sql.Date(new java.util.Date().getTime()).toString();
            }
        }
        
        //categoria cittadino
        ViewObject soggscrView = am.findViewObject("Sogg_SoSoggScrView1");
        Row soggScr = soggscrView.getCurrentRow();
        Sogg_NuovoParam bean =
            (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
        bean.setCodClassePop((String)soggScr.getAttribute("Codclassepop"));
        
        if (tpscr.equals("CO") && (Boolean)session.get("covid19")){
            ViewObject motView = am.findViewObject("SoCnfRef1LivMTEXECView1");
            motView.setWhereClause(" (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" + strDataInsInv + "', 'YYYY-MM-DD'))");
            motView.executeQuery();
        }
    }

    public void setBndfrm(RichForm bndfrm) {
        this.bndfrm = bndfrm;
    }

    public RichForm getBndfrm() {
        if(bndfrm==null)
            findForward();
        
        return bndfrm;
    }
}
