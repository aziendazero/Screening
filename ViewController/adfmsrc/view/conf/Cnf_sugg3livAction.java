package view.conf;

import java.util.Arrays;
import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.commons.ViewHelper;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichDocument;

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

public class Cnf_sugg3livAction extends Parent_DataForwardAction {
    private RichForm suggForm;
    private RichTable tabSugg3;
    
    private RichSelectOneChoice selectedUlss;

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }
    
    public Cnf_sugg3livAction() {
    }

    public void setSuggForm(RichForm suggForm) {
        this.suggForm = suggForm;
    }

    public RichForm getSuggForm() {
        if (suggForm == null){
            findForward();
        }
        return suggForm;
    }
    
    public void setTabSugg3(RichTable tabSugg3) {
        this.tabSugg3 = tabSugg3;
    }

    public RichTable getTabSugg3() {
        return tabSugg3;
    }
    
    protected void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        Key navigDaDett = (Key) session.get("navSugg3Dett");
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSugg3livView1Iterator");
        if (navigDaDett != null ) {         
            //if (!navigDaDett.isAnyNull()){
                ViewObject vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                if (r != null && r.length>0)
                    vo.setCurrentRow(r[0]);
                
                ViewHelper.queryAndRestoreCurrentRow(vo);
                
                if (this.tabSugg3 == null)
                    this.tabSugg3 = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabSugg3.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabSugg3);
            //}
        } else
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedSugg3", "Tutte"); 
    }
    
    public void onDett(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSugg3livView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("navSugg3Dett", rowKey);
    }
    
    public void onChange(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlss();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedSugg3", selected.getValue());

        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSugg3livView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfSugg3liv.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCnfSugg3liv.TPSCR='" + tpscr + "' AND Cnf_SoCnfSugg3liv.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }

    public void setSelectedUlss(RichSelectOneChoice selectedUlss) {
        this.selectedUlss = selectedUlss;
    }

    public RichSelectOneChoice getSelectedUlss() {
        return selectedUlss;
    }

}
