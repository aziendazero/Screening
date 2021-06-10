package view.statistiche;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.Stats_AppModule;

import model.commons.AccessManager;

import model.datacontrol.Stats_dynamicFilter;
import model.datacontrol.Stats_paramBean;

import model.datasources.Stats_datasource;

import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;

import oracle.jbo.RowSetIterator;

import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

import view.reports.SystemReport;

import view.util.Utility;

public class Stats_invitiEsitiAction extends Parent_DataForwardAction {
    private Stats_toInvitiEsitiAction Stats_toInvitiEsitiAction;
    private RichPanelGroupLayout panelDynamicSearch;

    protected void setAppModule() {
        this.amName = "Stats_AppModule";
    }

    public void onChangeIndicator(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        Stats_paramBean bean =
            (Stats_paramBean) BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        switch (bean.getStats_type()) {
        case Stats_paramBean.INVITI_ESITI:
        case Stats_paramBean.ESCLUSIONI:
            {
                bean.setCoorte("INVITATI");
                break;
            }
        case Stats_paramBean.PERIODISMO:
            {
                bean.setCoorte("INVITATI");
                if ("CI".equals(tpscr)) {
                    bean.setInizio_int(36);
                    bean.setFine_int(54);
                } else { //screening biennali
                    bean.setInizio_int(24);
                    bean.setFine_int(48);
                }

                bean.setDurata_int(6);
                break;
            }
        case Stats_paramBean.TEMPI:
            {
                bean.setCoorte("SCREENATI");
                bean.setInizio_int(15);
                bean.setFine_int(45);
                bean.setDurata_int(15);
                break;
            }
        case Stats_paramBean.SCREENATI:
        case Stats_paramBean.DIAGNOSI1LIV:
        case Stats_paramBean.OPMEDICI:
        case Stats_paramBean.DIAGNOSI_PEGGIORE:
        case Stats_paramBean.ESITI_COLPO:
        case Stats_paramBean.RAC2LIV:
        case Stats_paramBean.INDICAZIONI2LIV:
        case Stats_paramBean.CONCLUSIONI2L:
        case Stats_paramBean.APPROFONDIMENTO_CO:
        case Stats_paramBean.DETECTION_RATE:
        case Stats_paramBean.CHIRURGIE_CO:
            {
                bean.setCoorte("SCREENATI");
                break;
            }
        case Stats_paramBean.PN_PT:
            {
                bean.setCoorte("SCREENATI");
                bean.setLesione("carcinoma");
                break;
            }
        }

        bean.setLivello(new Integer(1));

        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:comune', 'pt1:zona', 'pt1:centroPrel', 'pt1:sex', 'pt1:roundInd', 'pt1:accesso', 'pt1:giudia', 'pt1:tpescl', 'pt1:lesione', 'pt1:lesione1', 'pt1:listUlss'])");
    }

    public void onChangeUlss(ValueChangeEvent valueChangeEvent) {
        try {
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
            Stats_helper.onChangeUlss();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException("Errore durante la selzionae dei dati per la ulss: " + ex.getMessage(), null);
        }

        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:comune', 'pt1:zona', 'pt1:centroPrel', 'pt1:sex', 'pt1:roundInd', 'pt1:accesso', 'pt1:giudia', 'pt1:tpescl', 'pt1:lesione', 'pt1:lesione1', 'pt1:listUlss'])");
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:r1:0:comune', 'pt1:r1:0:zona', 'pt1:r1:0:centroPrel', 'pt1:r1:0:sex', 'pt1:r1:0:roundInd', 'pt1:r1:0:accesso', 'pt1:r1:0:giudia', 'pt1:r1:0:tpescl', 'pt1:r1:0:lesione', 'pt1:r1:0:lesione1', 'pt1:r1:0:listUlss', 'pt1:r1:0:medico', 'pt1:r1:0:centroRef', 'pt1:r1:0:catInv', 'pt1:r1:0:tpInvito', 'pt1:r1:0:esito', 'pt1:r1:0:tprichiamo', 'pt1:r1:0:livello', 'pt1:r1:0:completo', 'pt1:r1:0:adepre', 'pt1:r1:0:inalim', 'pt1:r1:0:giudia', 'pt1:r1:0:positi', 'pt1:r1:0:mx_esito', 'pt1:r1:0:quantita', 'pt1:r1:0:idsugg1l', 'pt1:r1:0:completo1', 'pt1:r1:0:colpvl', 'pt1:r1:0:colpes', 'pt1:r1:0:lesione_hpv', 'pt1:r1:0:ist_bio', 'pt1:r1:0:ist_bio_diagnosi', 'pt1:r1:0:racdia', 'pt1:r1:0:rxcolon', 'pt1:r1:0:rx_conclusioni', 'pt1:r1:0:endo_estensione', 'pt1:r1:0:motivo', 'pt1:r1:0:procedura', 'pt1:r1:0:endo_complicanze', 'pt1:r1:0:conclusioni2l', 'pt1:r1:0:diagnosi_co', 'pt1:r1:0:mammo', 'pt1:r1:0:eco', 'pt1:r1:0:clinico', 'pt1:r1:0:citologia', 'pt1:r1:0:esito_cito', 'pt1:r1:0:agobiopsia', 'pt1:r1:0:esito_agob', 'pt1:r1:0:racdia1', 'pt1:r1:0:idsugg2l', 'pt1:r1:0:intval', 'pt1:r1:0:intchiusi', 'pt1:r1:0:tipo_int', 'pt1:r1:0:ist_chir', 'pt1:r1:0:ist_chir_diagnosi', 'pt1:r1:0:pn', 'pt1:r1:0:pt', 'pt1:r1:0:pm', 'pt1:r1:0:ascella', 'pt1:r1:0:idsugg3l'])");
    }

    public String onCall_proc() {
        try {
            Stats_helper.onCall_proc();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException("Impossibile calcolare l'indicatore richiesto: " + ex.getMessage(), null);
        }
        return "calculated";
    }

    public String onReimp() {
        @SuppressWarnings("deprecation")
        Stats_paramBean bean =
            (Stats_paramBean) BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
        bean.reset();

        this.Stats_toInvitiEsitiAction.findForward();

        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:comune', 'pt1:zona', 'pt1:centroPrel', 'pt1:sex', 'pt1:roundInd', 'pt1:accesso', 'pt1:giudia', 'pt1:tpescl', 'pt1:lesione', 'pt1:lesione1', 'pt1:listUlss'])");
        return "reimp";
    }

    public void setStats_toInvitiEsitiAction(Stats_toInvitiEsitiAction Stats_toInvitiEsitiAction) {
        this.Stats_toInvitiEsitiAction = Stats_toInvitiEsitiAction;
    }

    public Stats_toInvitiEsitiAction getStats_toInvitiEsitiAction() {
        return Stats_toInvitiEsitiAction;
    }
    
    public String onReimpDynamicFilter() {
        @SuppressWarnings("deprecation")
        Stats_dynamicFilter bean =
            (Stats_dynamicFilter) BindingContext.getCurrent().findDataControl("Stats_dynamicFilterDataControl").getDataProvider();
        bean.resetAll();

        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:r1:0:comune', 'pt1:r1:0:zona', 'pt1:r1:0:centroPrel', 'pt1:r1:0:sex', 'pt1:r1:0:roundInd', 'pt1:r1:0:accesso', 'pt1:r1:0:giudia', 'pt1:r1:0:tpescl', 'pt1:r1:0:lesione', 'pt1:r1:0:lesione1', 'pt1:r1:0:listUlss', 'pt1:r1:0:medico', 'pt1:r1:0:centroRef', 'pt1:r1:0:catInv', 'pt1:r1:0:tpInvito', 'pt1:r1:0:esito', 'pt1:r1:0:tprichiamo', 'pt1:r1:0:livello', 'pt1:r1:0:completo', 'pt1:r1:0:adepre', 'pt1:r1:0:inalim', 'pt1:r1:0:giudia', 'pt1:r1:0:positi', 'pt1:r1:0:mx_esito', 'pt1:r1:0:quantita', 'pt1:r1:0:idsugg1l', 'pt1:r1:0:completo1', 'pt1:r1:0:colpvl', 'pt1:r1:0:colpes', 'pt1:r1:0:lesione_hpv', 'pt1:r1:0:ist_bio', 'pt1:r1:0:ist_bio_diagnosi', 'pt1:r1:0:racdia', 'pt1:r1:0:rxcolon', 'pt1:r1:0:rx_conclusioni', 'pt1:r1:0:endo_estensione', 'pt1:r1:0:motivo', 'pt1:r1:0:procedura', 'pt1:r1:0:endo_complicanze', 'pt1:r1:0:conclusioni2l', 'pt1:r1:0:diagnosi_co', 'pt1:r1:0:mammo', 'pt1:r1:0:eco', 'pt1:r1:0:clinico', 'pt1:r1:0:citologia', 'pt1:r1:0:esito_cito', 'pt1:r1:0:agobiopsia', 'pt1:r1:0:esito_agob', 'pt1:r1:0:racdia1', 'pt1:r1:0:idsugg2l', 'pt1:r1:0:intval', 'pt1:r1:0:intchiusi', 'pt1:r1:0:tipo_int', 'pt1:r1:0:ist_chir', 'pt1:r1:0:ist_chir_diagnosi', 'pt1:r1:0:pn', 'pt1:r1:0:pt', 'pt1:r1:0:pm', 'pt1:r1:0:ascella', 'pt1:r1:0:idsugg3l'])");
        return "reimp";
    }

    public void setPanelDynamicSearch(RichPanelGroupLayout panelDynamicSearch) {
        this.panelDynamicSearch = panelDynamicSearch;
    }

    public RichPanelGroupLayout getPanelDynamicSearch() {
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:r1:0:comune', 'pt1:r1:0:zona', 'pt1:r1:0:centroPrel', 'pt1:r1:0:sex', 'pt1:r1:0:roundInd', 'pt1:r1:0:accesso', 'pt1:r1:0:giudia', 'pt1:r1:0:tpescl', 'pt1:r1:0:lesione', 'pt1:r1:0:lesione1', 'pt1:r1:0:listUlss', 'pt1:r1:0:medico', 'pt1:r1:0:centroRef', 'pt1:r1:0:catInv', 'pt1:r1:0:tpInvito', 'pt1:r1:0:esito', 'pt1:r1:0:tprichiamo', 'pt1:r1:0:livello', 'pt1:r1:0:completo', 'pt1:r1:0:adepre', 'pt1:r1:0:inalim', 'pt1:r1:0:giudia', 'pt1:r1:0:positi', 'pt1:r1:0:mx_esito', 'pt1:r1:0:quantita', 'pt1:r1:0:idsugg1l', 'pt1:r1:0:completo1', 'pt1:r1:0:colpvl', 'pt1:r1:0:colpes', 'pt1:r1:0:lesione_hpv', 'pt1:r1:0:ist_bio', 'pt1:r1:0:ist_bio_diagnosi', 'pt1:r1:0:racdia', 'pt1:r1:0:rxcolon', 'pt1:r1:0:rx_conclusioni', 'pt1:r1:0:endo_estensione', 'pt1:r1:0:motivo', 'pt1:r1:0:procedura', 'pt1:r1:0:endo_complicanze', 'pt1:r1:0:conclusioni2l', 'pt1:r1:0:diagnosi_co', 'pt1:r1:0:mammo', 'pt1:r1:0:eco', 'pt1:r1:0:clinico', 'pt1:r1:0:citologia', 'pt1:r1:0:esito_cito', 'pt1:r1:0:agobiopsia', 'pt1:r1:0:esito_agob', 'pt1:r1:0:racdia1', 'pt1:r1:0:idsugg2l', 'pt1:r1:0:intval', 'pt1:r1:0:intchiusi', 'pt1:r1:0:tipo_int', 'pt1:r1:0:ist_chir', 'pt1:r1:0:ist_chir_diagnosi', 'pt1:r1:0:pn', 'pt1:r1:0:pt', 'pt1:r1:0:pm', 'pt1:r1:0:ascella', 'pt1:r1:0:idsugg3l'])");
        return panelDynamicSearch;
    }

    public void removeEmptyOptions(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:r1:0:comune', 'pt1:r1:0:zona', 'pt1:r1:0:centroPrel', 'pt1:r1:0:sex', 'pt1:r1:0:roundInd', 'pt1:r1:0:accesso', 'pt1:r1:0:giudia', 'pt1:r1:0:tpescl', 'pt1:r1:0:lesione', 'pt1:r1:0:lesione1', 'pt1:r1:0:listUlss', 'pt1:r1:0:medico', 'pt1:r1:0:centroRef', 'pt1:r1:0:catInv', 'pt1:r1:0:tpInvito', 'pt1:r1:0:esito', 'pt1:r1:0:tprichiamo', 'pt1:r1:0:livello', 'pt1:r1:0:completo', 'pt1:r1:0:adepre', 'pt1:r1:0:inalim', 'pt1:r1:0:giudia', 'pt1:r1:0:positi', 'pt1:r1:0:mx_esito', 'pt1:r1:0:quantita', 'pt1:r1:0:idsugg1l', 'pt1:r1:0:completo1', 'pt1:r1:0:colpvl', 'pt1:r1:0:colpes', 'pt1:r1:0:lesione_hpv', 'pt1:r1:0:ist_bio', 'pt1:r1:0:ist_bio_diagnosi', 'pt1:r1:0:racdia', 'pt1:r1:0:rxcolon', 'pt1:r1:0:rx_conclusioni', 'pt1:r1:0:endo_estensione', 'pt1:r1:0:motivo', 'pt1:r1:0:procedura', 'pt1:r1:0:endo_complicanze', 'pt1:r1:0:conclusioni2l', 'pt1:r1:0:diagnosi_co', 'pt1:r1:0:mammo', 'pt1:r1:0:eco', 'pt1:r1:0:clinico', 'pt1:r1:0:citologia', 'pt1:r1:0:esito_cito', 'pt1:r1:0:agobiopsia', 'pt1:r1:0:esito_agob', 'pt1:r1:0:racdia1', 'pt1:r1:0:idsugg2l', 'pt1:r1:0:intval', 'pt1:r1:0:intchiusi', 'pt1:r1:0:tipo_int', 'pt1:r1:0:ist_chir', 'pt1:r1:0:ist_chir_diagnosi', 'pt1:r1:0:pn', 'pt1:r1:0:pt', 'pt1:r1:0:pm', 'pt1:r1:0:ascella', 'pt1:r1:0:idsugg3l'])");
    }

    public void printDownload(FacesContext facesContext, OutputStream outputStream) {
        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        File pdf = null;
        FileInputStream fis = null;
        try {
            AccessManager.checkPermission("SOStatULSS");

            //leggo il bean con i dati salvati
            Stats_datasource ds = (Stats_datasource) am.returnPrintBean().elementAt(0);

            //controllo di quante colonne ho bisogno
            int max_cols =
                ((String[]) ADFContext.getCurrent().getSessionScope().get("stats_inviti_esiti_headers")).length;
            ds.setMax_column_used(max_cols);

            SystemReport sr = new SystemReport(SystemReport.STATISTICHE_MASTER);
            pdf = sr.getPDFReport(new JRBeanArrayDataSource(new Object[] { ds }), new HashMap());

            fis = new FileInputStream(pdf);
            byte[] b;
            int n;
            while ((n = fis.available()) > 0) {
                b = new byte[n];
                int result = fis.read(b);
                outputStream.write(b, 0, b.length);
                if (result == -1)
                    break;
            }

            outputStream.flush();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                }

            if (pdf != null)
                pdf.delete();
        }

    }
}
