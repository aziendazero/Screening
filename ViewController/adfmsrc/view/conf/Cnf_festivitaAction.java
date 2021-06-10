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

public class Cnf_festivitaAction extends Parent_DataForwardAction {
    private RichForm festivitaForm;
    private RichTable tabFestivita;
    private RichSelectOneChoice selectedUlss;

    public Cnf_festivitaAction() {
    }
    
    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }

    public void setFestivitaForm(RichForm festivitaForm) {
        this.festivitaForm = festivitaForm;
    }

    public RichForm getFestivitaForm() {
        if (festivitaForm == null){
            findForward();
        }
        return festivitaForm;
    }

    public void setTabFestivita(RichTable tabFestivita) {
        this.tabFestivita = tabFestivita;
    }

    public RichTable getTabFestivita() {
        return tabFestivita;
    }
    
    protected void findForward() {       
        Map session = ADFContext.getCurrent().getSessionScope();
        Key navigDaDett = (Key) session.get("festivitaDett");
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfFestivitaView1Iterator");
        if (navigDaDett != null ) {         
            if (!navigDaDett.isAnyNull()){
                ViewObject vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                if(r!=null)                
                    vo.setCurrentRow(r[0]);
                
                ViewHelper.queryAndRestoreCurrentRow(vo);
                
                if (this.tabFestivita == null)
                    this.tabFestivita = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabFestivita.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabFestivita);
            }
        } else
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", "Tutte");   
    }
    
    public void onDett(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfFestivitaView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("festivitaDett", rowKey);
    }
    
    public void onChange(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlss();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", ulss);   
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfFestivitaView1Iterator");
            ViewObject vo = voIter.getViewObject();

            if ("Tutte".equals(ulss))
                vo.setWhereClause("");
            else 
                vo.setWhereClause("ULSS = '" + ulss + "'");
            
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
