package view.conf;

import java.util.Arrays;
import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.commons.ViewHelper;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Cnf_impExpAction extends Parent_DataForwardAction {
    private RichForm confForm;
    private RichForm errorForm;
    private RichTable tabConf;
    private RichTable tabError;
    private RichSelectOneChoice selectedUlssConf;
    private RichSelectOneChoice selectedUlssError;

    public Cnf_impExpAction() {
    }

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }
    
    public void setConfForm(RichForm confForm) {
        this.confForm = confForm;
    }

    public RichForm getConfForm() {
        if (confForm == null){
            findForward("conf");
        }
        return confForm;
    }

    public void setErrorForm(RichForm errorForm) {
        this.errorForm = errorForm;
    }

    public RichForm getErrorForm() {
        if (errorForm == null){
            findForward("error");
        }
        return errorForm;
    }

    public void setTabConf(RichTable tabConf) {
        this.tabConf = tabConf;
    }

    public RichTable getTabConf() {
        return tabConf;
    }

    public void setTabError(RichTable tabError) {
        this.tabError = tabError;
    }

    public RichTable getTabError() {
        return tabError;
    }
    
    protected void findForward(String type) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
        if ("conf".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfImpexpView1Iterator");
        } else if ("error".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoErroreView1Iterator");
        } else 
            return;
        
        if (navigDaDett != null ) {         
                ViewObject vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                if(r!=null)                
                    vo.setCurrentRow(r[0]);
                
                ViewHelper.queryAndRestoreCurrentRow(vo);
                
            if ("conf".equals(type)){
                if (this.tabConf == null)
                    this.tabConf = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabConf.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabConf);
            } else if ("error".equals(type)){
                if (this.tabError == null)
                    this.tabError = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabError.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabError);
            } 
        }  else if ("conf".equals(type)){
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedConf", "Tutte");
        } else  if ("error".equals(type)){
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedError", "Tutte");
        }
    }
    
    public void onDettConf(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfImpexpView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("conf", rowKey);
    }
    
    public void onDettError(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoErroreView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("error", rowKey);
    }
    
    public void onChangeConf(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlssConf();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedConf", ulss); 
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfImpexpView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfImpexp.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCnfImpexp.TPSCR='" + tpscr + "' AND Cnf_SoCnfImpexp.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }
    
    public void onChangeError(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlssError();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedError", ulss); 
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoErroreView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoErrore.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoErrore.TPSCR='" + tpscr + "' AND Cnf_SoErrore.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }

    public void setSelectedUlssConf(RichSelectOneChoice selectedUlssConf) {
        this.selectedUlssConf = selectedUlssConf;
    }

    public RichSelectOneChoice getSelectedUlssConf() {
        return selectedUlssConf;
    }

    public void setSelectedUlssError(RichSelectOneChoice selectedUlssError) {
        this.selectedUlssError = selectedUlssError;
    }

    public RichSelectOneChoice getSelectedUlssError() {
        return selectedUlssError;
    }
}
