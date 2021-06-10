package view.conf;

import java.util.Arrays;
import java.util.Map;

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

public class Cnf_esclusioniAction extends Parent_DataForwardAction {

    private RichForm esclForm;
    private RichTable tabEscl;

    private RichSelectOneChoice selectedUlss;

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }
    
    public Cnf_esclusioniAction() {
    }
    
    protected void findForward() {
         Map session = ADFContext.getCurrent().getSessionScope();
         Key navigDaDett = (Key) session.get("navEsclusione");
         
         DCBindingContainer bindings = (DCBindingContainer) getBindings();
         DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfEsclusioneView1Iterator");
         if (navigDaDett != null ) {         
             //if (!navigDaDett.isAnyNull()){
            ViewObject vo = voIter.getViewObject();
            Row[] r = vo.findByKey(navigDaDett, 1);
            if (r != null && r.length>0)
                vo.setCurrentRow(r[0]);
                 
            ViewHelper.queryAndRestoreCurrentRow(vo);
                 
            if (this.tabEscl == null)
                this.tabEscl = new RichTable();
                
             Row _row = vo.getCurrentRow();

             if (_row != null) {
                 RowKeySet selected = new RowKeySetImpl();
                 selected.add(Arrays.asList(_row.getKey()));
                 this.tabEscl.setSelectedRowKeys(selected);
             }
                 
            Utility.gotoTablePageOfSelectedRow(voIter, this.tabEscl);
             //}
         } else 
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", "Tutte");       
     }
     
    public void setEsclForm(RichForm esclForm) {
        this.esclForm = esclForm;
    }

    public RichForm getEsclForm() {
        if (esclForm == null){
            findForward();
        }
        return esclForm;
    }

    public void setTabEscl(RichTable tabEscl) {
        this.tabEscl = tabEscl;
    }

    public RichTable getTabEscl() {
        return tabEscl;
    }
    
    public void setSelectedUlss(RichSelectOneChoice selectedUlss) {
        this.selectedUlss = selectedUlss;
    }

    public RichSelectOneChoice getSelectedUlss() {
        return selectedUlss;
    }
    
    public void onChange(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlss();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", selected.getValue());
        
        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            session.put("ulssEsclusioni", ulss);
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfEsclusioneView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfEsclusione.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCnfEsclusione.TPSCR='" + tpscr + "' AND Cnf_SoCnfEsclusione.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }
}
