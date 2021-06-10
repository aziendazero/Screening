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

public class Cnf_aziendAction extends Parent_DataForwardAction{
    private RichForm aziendeForm;    
    private RichTable tabAziende;
    
    private RichSelectOneChoice selectedUlss;

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }

    public void setTabAziende(RichTable tabAziende) {
        this.tabAziende = tabAziende;
    }

    public RichTable getTabAziende() {
        return tabAziende;
    }
    
    public Cnf_aziendAction() {
    }

    public void setAziendeForm(RichForm aziendeForm) {
        this.aziendeForm = aziendeForm;
    }

    public RichForm getAziendeForm() {
        if (aziendeForm == null){
            findForward();
        }
        return aziendeForm;
    }
    
    protected void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
       
       navigDaDett = (Key) session.get("azienda");
       voIter = bindings.findIteratorBinding("Cnf_SoAziendaView1Iterator");
        
        if (navigDaDett != null ) {         
            ViewObject vo = voIter.getViewObject();
            Row[] r = vo.findByKey(navigDaDett, 1);
            if (r != null && r.length>0)
                 vo.setCurrentRow(r[0]);
                
            ViewHelper.queryAndRestoreCurrentRow(vo);
                if (this.tabAziende == null)
                    this.tabAziende = new RichTable();
            Row _row = vo.getCurrentRow();

            if (_row != null) {
                RowKeySet selected = new RowKeySetImpl();
                selected.add(Arrays.asList(_row.getKey()));
                this.tabAziende.setSelectedRowKeys(selected);
            }
            
            Utility.gotoTablePageOfSelectedRow(voIter, this.tabAziende);
        } else 
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", "Tutte");  
    }
    
    public void onDett(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoAziendaView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("azienda", rowKey);
    }
    
    public void onChange(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlss();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", selected.getValue());
        
        if (ulss != ""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoAziendaView1Iterator");
            ViewObject vo = voIter.getViewObject();

            if ("Tutte".equals(ulss))
                vo.setWhereClause("");
            else 
                vo.setWhereClause("Cnf_SoAzienda.CODAZ = '" + ulss + "'");
            
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
