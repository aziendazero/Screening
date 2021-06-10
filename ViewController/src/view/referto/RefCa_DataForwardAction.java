package view.referto;

import java.util.Map;

import model.RefCa_AppModule;

import model.commons.ViewHelper;

import model.datacontrol.AccCa_RicParam;
import model.datacontrol.Ref_SearchBean;
import model.datacontrol.Sogg_RicParam;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

public abstract class RefCa_DataForwardAction extends Parent_DataForwardAction {
    protected void beforeNavigate(String dest) throws Exception {
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
        Row r = vo.getCurrentRow();
        if (r != null) {

            // Navigazione verso ricerca soggetto
            if (dest.equals("iniSogg")) {
                Sogg_RicParam beanSogg =
                    (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                beanSogg.resetCampi();
                beanSogg.setTessSan((String) r.getAttribute("Codts"));
                beanSogg.setCognome((String) r.getAttribute("Cognome"));
                beanSogg.setNome((String) r.getAttribute("Nome"));
                
                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    Map session = ADFContext.getCurrent().getSessionScope();
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanSogg.setChiave(chiave);
                    }
                }
                
                beanSogg.setInEta(0);
                beanSogg.setNavigazione(Boolean.TRUE);
            }

            // Navigazione verso l'accettazione oppure l'anamnesi
            else if (dest.startsWith("acc_to")) {
                Integer livello = (Integer) r.getAttribute("Livello");
                AccCa_RicParam beanAcc =
                    (AccCa_RicParam) BindingContext.getCurrent().findDataControl("AccCa_RicParamDataControl").getDataProvider();
                beanAcc.resetCampi();
                beanAcc.setCognome((String) r.getAttribute("Cognome"));
                beanAcc.setNome((String) r.getAttribute("Nome"));
                
                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    Map session = ADFContext.getCurrent().getSessionScope();
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanAcc.setChiave(chiave);
                    }
                }
                
                beanAcc.setTessSan((String) r.getAttribute("Codts"));
                Integer idCprel = (Integer) r.getAttribute("Idcentroprelievo");
                if (idCprel == null)
                    beanAcc.setIdCprel(null);
                else
                    beanAcc.setIdCprel(new Integer(idCprel.intValue()));
                beanAcc.setLivello(livello.toString());
                beanAcc.setNavigazione(Boolean.TRUE);
            }
        }

        // Se sto uscendo dalla refertazione resetto il bean
        if (!dest.startsWith("to_refCa_")) {
            Ref_SearchBean bean =
                (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
            bean.reset();
        }
    }

    protected void setAppModule() {
        this.amName = "RefCa_AppModule";
    }

    public String onApply() {
        return null;
    }

    public String onConfirm() {
        onApply();
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
        ViewHelper.queryAndRestoreCurrentRow(vo);
        return "to_refCa_ricerca";
    }

    public String onRollback() {
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        am.doRollback(new String[] { "Ref_SoInvitiPerRefertiCAView1" });
        return "to_refCa_ricerca";
    }

    public String onRef_back() {
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        am.refreshInviti();
        return "to_refCa_ricerca";
    }
}
