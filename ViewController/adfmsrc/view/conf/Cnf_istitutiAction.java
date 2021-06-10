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

public class Cnf_istitutiAction extends Parent_DataForwardAction{
    private RichForm istitutiForm;    
    private RichTable tabIstituti;
    
    private RichSelectOneChoice selectedUlss;

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }

    public void setTabIstituti(RichTable tabIstituti) {
        this.tabIstituti = tabIstituti;
    }

    public RichTable getTabIstituti() {
        return tabIstituti;
    }
    
    public Cnf_istitutiAction() {
    }

    public void setIstitutiForm(RichForm istitutiForm) {
        this.istitutiForm = istitutiForm;
    }

    public RichForm getIstitutiForm() {
        if (istitutiForm == null){
            findForward();
        }
        return istitutiForm;
    }
    
    protected void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
       
        navigDaDett = (Key) session.get("istituto");
        voIter = bindings.findIteratorBinding("Cnf_SoAmbIstricView1Iterator");
        
        if (navigDaDett != null ) {         
            ViewObject vo = voIter.getViewObject();
            Row[] r = vo.findByKey(navigDaDett, 1);
            if (r != null && r.length>0)
                 vo.setCurrentRow(r[0]);
                
            ViewHelper.queryAndRestoreCurrentRow(vo);
            
            if (this.tabIstituti == null)
                this.tabIstituti = new RichTable();
            
            Row _row = vo.getCurrentRow();

            if (_row != null) {
                RowKeySet selected = new RowKeySetImpl();
                selected.add(Arrays.asList(_row.getKey()));
                this.tabIstituti.setSelectedRowKeys(selected);
            }
                
            Utility.gotoTablePageOfSelectedRow(voIter, this.tabIstituti);
        } else
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", "Tutte");  
    }
    
    public void onDett(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoAmbIstricView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("istituto", rowKey);
    }
    
    public void onChange(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlss();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", selected.getValue());
        
        if (ulss != ""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoAmbIstricView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            
            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoAmbIstric.TPSCR='" + tpscr + "'");
            else
                vo.setWhereClause("Cnf_SoAmbIstric.TPSCR='" + tpscr + "' AND Cnf_SoAmbIstric.ULSS = '" + ulss + "'");
            
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
