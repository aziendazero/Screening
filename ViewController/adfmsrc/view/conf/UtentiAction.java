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

public class UtentiAction extends Parent_DataForwardAction{
    private RichForm medForm;
    private RichForm opForm;
    private RichForm utForm;
    
    private RichTable tabMed;
    private RichTable tabOp;
    private RichTable tabUt;

    public void setSelectedUlssMed(RichSelectOneChoice selectedUlssMed) {
        this.selectedUlssMed = selectedUlssMed;
    }

    public RichSelectOneChoice getSelectedUlssMed() {
        return selectedUlssMed;
    }

    public void setSelectedUlssUt(RichSelectOneChoice selectedUlssUt) {
        this.selectedUlssUt = selectedUlssUt;
    }

    public RichSelectOneChoice getSelectedUlssUt() {
        return selectedUlssUt;
    }
    private RichSelectOneChoice selectedUlssMed;
    private RichSelectOneChoice selectedUlssUt;

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }

    public void setMedForm(RichForm medForm) {
        this.medForm = medForm;
    }

    public RichForm getMedForm() {
        if (medForm == null){
            findForward("med");
        }
        return medForm;
    }

    public void setOpForm(RichForm opForm) {
        this.opForm = opForm;
    }

    public RichForm getOpForm() {
        if (opForm == null){
            findForward("op");
        }
        return opForm;
    }

    public void setUtForm(RichForm utForm) {
        this.utForm = utForm;
    }

    public RichForm getUtForm() {
        if (utForm == null){
            findForward("ut");
        }
        return utForm;
    }

    public void setTabMed(RichTable tabMed) {
        this.tabMed = tabMed;
    }

    public RichTable getTabMed() {
        return tabMed;
    }

    public void setTabOp(RichTable tabOp) {
        this.tabOp = tabOp;
    }

    public RichTable getTabOp() {
        return tabOp;
    }

    public void setTabUt(RichTable tabUt) {
        this.tabUt = tabUt;
    }

    public RichTable getTabUt() {
        return tabUt;
    }
        
    public UtentiAction() {
    }

    protected void findForward(String type) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
        if ("med".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoOpmedicoView1Iterator");
        } else if ("op".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfTpopmedicoView1Iterator");
        } else if ("ut".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfUtentiOperatoriView1Iterator");
        } else 
            return;
        
        if (navigDaDett != null ) {         
                ViewObject vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                if(r!=null)                
                    vo.setCurrentRow(r[0]);
                
                ViewHelper.queryAndRestoreCurrentRow(vo);
                
            if ("med".equals(type)){
                if (this.tabMed == null)
                    this.tabMed = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabMed.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabMed);
            } else if ("op".equals(type)){
                if (this.tabOp == null)
                    this.tabOp = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabOp.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabOp);
            } else if ("ut".equals(type)){
                if (this.tabUt == null)
                    this.tabUt = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabUt.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabUt);
            }
        } else {
            if ("med".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedMed", "Tutte");

            else if ("ut".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedUt", "Tutte");               
        }    
    }
    
    public void onDettMed(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoOpmedicoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("med", rowKey);
    }
    
    public void onDettOp(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfTpopmedicoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("op", rowKey);
    }
    
    public void onDettUt(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfUtentiOperatoriView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("ut", rowKey);
    }
    
    public void onChangeMed(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlssMed();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedMed", selected.getValue());
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoOpmedicoView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoOpmedico.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoOpmedico.TPSCR='" + tpscr + "' AND Cnf_SoOpmedico.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }
    

    public void onChangeUt(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlssUt();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedUt", selected.getValue());
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfUtentiOperatoriView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfUtentiOperatori.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCnfUtentiOperatori.TPSCR='" + tpscr + "' AND Cnf_SoCnfUtentiOperatori.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }

}
