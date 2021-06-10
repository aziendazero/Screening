package view.medico;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.ReturnEvent;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Med_dettAction extends Parent_DataForwardAction {

    private RichInputText descom;
    private RichInputText codcom;

    @Override
    protected void setAppModule() {
        this.amName="Med_AppModule";
    }

    public String onConf() {
        boolean saveOK = this.save();
        if (saveOK) {
            return "med_ini";
        }
        return null;
    }

    public String onAppl() {
        this.save();
        return null;
    }

    public String onCancel() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
        rollbackBinding.execute();
        return "med_ini";
    }

    public void onChProv(ValueChangeEvent valueChangeEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Med_SoMedicoView1Iterator");
        ViewObject voMed = voIter.getViewObject();
        Row cMed = voMed.getCurrentRow();
        cMed.setAttribute("Descom", null);
        cMed.setAttribute("Codcom", null);
    }

    public void setDescom(RichInputText descom) {
        this.descom = descom;
    }

    public RichInputText getDescom() {
        return descom;
    }

    public String onChCom() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Med_SoMedicoView1Iterator");
        ViewObject voMed = voIter.getViewObject();
        Row cMed = voMed.getCurrentRow();
        String desCom = (String) cMed.getAttribute("Descom");
        if (desCom == null || desCom.equals("")) {
            cMed.setAttribute("Codcom", null);
        }
        return "lovComune";
    }
    
    public void lovComuneReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(descom);
        RequestContext.getCurrentInstance().addPartialTarget(codcom);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + descom.getClientId() + "', false)");
    }

    public void setCodcom(RichInputText codcom) {
        this.codcom = codcom;
    }

    public RichInputText getCodcom() {
        return codcom;
    }
}
