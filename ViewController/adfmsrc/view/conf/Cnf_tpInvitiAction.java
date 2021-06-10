package view.conf;

import insiel.dataHandling.DateUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.commons.ViewHelper;

import model.datacontrol.Agenda_Servizio;
import model.datacontrol.Cnf_selectionBean;

import oracle.adf.model.BindingContext;
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

public class Cnf_tpInvitiAction extends Parent_DataForwardAction {
    private RichForm catTpInvForm;
    private RichForm tpInvForm;
    
    private RichTable tabCatTpinv;
    private RichTable tabTpinv;
    
    private RichSelectOneChoice selectedUlss;

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }
    
    public void setTabCatTpinv(RichTable tabCatTpinv) {
        this.tabCatTpinv = tabCatTpinv;
    }

    public RichTable getTabCatTpinv() {
        return tabCatTpinv;
    }

    public void setTabTpinv(RichTable tabTpinv) {
        this.tabTpinv = tabTpinv;
    }

    public RichTable getTabTpinv() {
        return tabTpinv;
    }
       
    public Cnf_tpInvitiAction() {
    }

    protected void findForward(String type) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
        
        if ("cattpinv".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfCategTpinvitoView1Iterator");
        } else if ("tpinv".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfTpinvitoView1Iterator");
        } else 
            return;
        
        if (navigDaDett != null ) {         
            ViewObject vo = voIter.getViewObject();
            Row[] r = vo.findByKey(navigDaDett, 1);
            if (r != null && r.length>0)
                vo.setCurrentRow(r[0]);
                
            ViewHelper.queryAndRestoreCurrentRow(vo);
                
            if ("cattpinv".equals(type)){
                if (this.tabCatTpinv == null)
                    this.tabCatTpinv = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabCatTpinv.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabCatTpinv);
            } else if ("tpinv".equals(type)){
                if (this.tabTpinv == null)
                    this.tabTpinv = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabTpinv.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabTpinv);
            }
        } else if ("tpinv".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", "Tutte");               
    }

    public void onDettCatTpinv(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfCategTpinvitoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("cattpinv", rowKey);
    }
    
    public void onDettTpinv(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfTpinvitoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("tpinv", rowKey);
    }

    public void setCatTpInvForm(RichForm catTpInvForm) {
        this.catTpInvForm = catTpInvForm;
    }

    public RichForm getCatTpInvForm() {
        if (catTpInvForm == null){
            findForward("cattpinv");
        }
        return catTpInvForm;
    }

    public void setTpInvForm(RichForm tpInvForm) {
        this.tpInvForm = tpInvForm;
    }

    public RichForm getTpInvForm() {
        if (tpInvForm == null){
            findForward("tpinv");
        }
        return tpInvForm;
    }
    
    public void onChange(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlss();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", selected.getValue());
 
        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            session.put("ulssTpInviti", ulss);
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfTpinvitoView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfTpinvito.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCnfTpinvito.TPSCR='" + tpscr + "' AND Cnf_SoCnfTpinvito.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }

    public void setSelectedUlss(RichSelectOneChoice selectedUlss) {
        this.selectedUlss = selectedUlss;
    }

    public RichSelectOneChoice getSelectedUlss() {
        return selectedUlss;
    }
    
    /*public String onPeriodicitaInviti() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfPeriodicitaInvitiView1Iterator");
        ViewObject vo = voIter.getViewObject();
        
        if (vo!=null){
            String where = "Cnf_SoCnfPeriodicitaInviti.TPSCR='" + tpscr + "'";
            
            if (!"050000".equals(ulss))
                where += " AND Cnf_SoCnfPeriodicitaInviti.ULSS = '" + ulss + "'";
            
            vo.setWhereClause(where);
            vo.executeQuery();           
        }
        
        voIter = bindings.findIteratorBinding("Cnf_SoCnfPrimoInvito1Iterator");
        vo = voIter.getViewObject();
            
        if (vo!=null){
          
            String where = "Cnf_SoCnfTpinvito.TPSCR='" + tpscr + "'";
            
            if (!"050000".equals(ulss))
                where += " AND Cnf_SoCnfTpinvito.ULSS = '" + ulss + "'";
            
            vo.setWhereClause(where);
            vo.executeQuery();           
        }
        
        voIter = bindings.findIteratorBinding("Cnf_SoCnfClassePopView1Iterator");
        vo = voIter.getViewObject();
            
        if (vo!=null){
            String where = "Cnf_SoCnfClassePop.TPSCR='" + tpscr + "'";
            vo.setWhereClause(where);
            vo.executeQuery();           
        }

        return "cnf_periodicitaInviti";
    }*/
}
