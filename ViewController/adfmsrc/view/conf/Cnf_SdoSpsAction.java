package view.conf;

import java.util.Map;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.ReturnEvent;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Cnf_SdoSpsAction extends Parent_DataForwardAction {
    private RichForm form;
    private RichInputText codSdoSpsAss;
    
    @Override
    protected void setAppModule() {
        this.amName = "Cnf_AppModule";
    }

    public Cnf_SdoSpsAction() {
    }

    public void setForm(RichForm form) {
        this.form = form;
    }

    public RichForm getForm() {
        return form;
    }

    public void setCodSdoSpsAss(RichInputText codSdoSpsAss) {
        this.codSdoSpsAss = codSdoSpsAss;
        
        String st = "";
        
        if (this.codSdoSpsAss!=null && this.codSdoSpsAss.getValue()!=null)
            st = this.codSdoSpsAss.getValue().toString();
        
        if (!"".equals(st)){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter1 = bindings.findIteratorBinding("Cnf_SoCnfSdosps_SelCodView1Iterator");
                
            if (voIter1!=null){
                ViewObject voObj1 = voIter1.getViewObject();
                String whcl = "CODSDOSPS like '" + st + "%'"; 
                voObj1.setWhereClause(whcl);
                voObj1.setOrderByClause("TIPO_FLUSSO, CODSDOSPS");
                
                voObj1.executeQuery();
            }
        }
    }

    public RichInputText getCodSdoSpsAss() {
        return codSdoSpsAss;
    }
    
    @SuppressWarnings("unused")
    public void lovSdoSpsReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(codSdoSpsAss);
        
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + codSdoSpsAss.getClientId() + "', false)");
    }
    
    /*@SuppressWarnings("unchecked")
    public String onChCod() {
        AGSTART ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        @SuppressWarnings("deprecation")
        Sogg_RicParam bean =
            (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
        String idComRes = bean.getIdComRes();
        session.put("codCom", idComRes);
        SoggUtils.filtraDistretti(idComRes, "Sogg_DistrettiView1");
        
        String desCom = bean.getDesComRes();
        Map req = adfCtx.getRequestScope();
        if (desCom == null || desCom.equals("")) {
            bean.setCodComRes(null);
            
            boolean onClick = false;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : false;
                }catch(Throwable th){
                    onClick = false;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                //Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', false)");
                return null;
            }
        }
        
        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                Map page = adfCtx.getPageFlowScope();
                page.put("iteratorBinding", req.get("iteratorBinding"));
                page.put("source", req.get("source"));
                
                LovComuneAction.checkOnLovFilter();
                
                page.remove("iteratorBinding");
                page.remove("itName");
                page.remove("source");                
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                //Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', true)");
                return null;
            }
        }AGSTOP 
        
        return "lovSdoSps";
        //return "lovComune";
    }*/
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    public String onLovSelect() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        
        //Riga selezionata
        DCIteratorBinding voIter1 = bindings.findIteratorBinding("Cnf_SoCnfSdosps_SelCodView1Iterator");
        ViewObject voObj1 = voIter1.getViewObject();
        Row code = voObj1.getCurrentRow();
        
        DCIteratorBinding voIter2 = bindings.findIteratorBinding("Cnf_SoCnfSdospsEsclView1Iterator");
        ViewObject voObj2 = voIter2.getViewObject();
        Row target = voObj2.getCurrentRow();
        
        String cod = (String) code.getAttribute("Codsdosps");
        String des = (String) code.getAttribute("Descrizione");
        
        target.setAttribute("Codsdosps",cod);
        target.setAttribute("Descrizione",des);
        
        //this.codSdoSpsAss.setValue(cod);
        
        return "selected";
    }
    
    @SuppressWarnings({ "deprecation", "unchecked" })
    public String onLovFilter() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        String st = req.get("searchText") != null ? req.get("searchText").toString() : "";
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        DCIteratorBinding voIter1 = bindings.findIteratorBinding("Cnf_SoCnfSdosps_SelCodView1Iterator");
        ViewObject voObj1 = voIter1.getViewObject();
        
        String whcl = "CODSDOSPS like '" + st + "%'";
        
        voObj1.setWhereClause(whcl);
        voObj1.setOrderByClause("TIPO_FLUSSO, CODSDOSPS");
        
        voObj1.executeQuery();   

        return "searchcompleted";
    }
    
}
