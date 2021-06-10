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

public class Cnf_anagraficheAction extends Parent_DataForwardAction {
    private RichForm sdoCodiciLocaliForm;
    private RichForm sdoEsclusioniForm;
    private RichForm sdoCodiciRegionaliForm;
    
    private RichTable tabCodLoc;
    private RichTable tabEsclusioni;
    private RichTable tabCodReg;
    
    private RichSelectOneChoice selectedUlssCodLoc;
    private RichSelectOneChoice selectedFilters;
    private RichSelectOneChoice selectedEsclusioni;
    
    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }

    public void setSdoEsclusioniForm(RichForm sdoEsclusioniForm) {
        this.sdoEsclusioniForm = sdoEsclusioniForm;
    }

    public RichForm getSdoEsclusioniForm() {
        if (sdoEsclusioniForm == null){
            findForward("esclusioni");
        }
        return sdoEsclusioniForm;
    }

    public void setSdoCodiciRegionaliForm(RichForm sdoCodiciRegionaliForm) {
        this.sdoCodiciRegionaliForm = sdoCodiciRegionaliForm;
    }

    public RichForm getSdoCodiciRegionaliForm() {
        if (sdoCodiciRegionaliForm == null){
            findForward("codicireg");
        }
        return sdoCodiciRegionaliForm;
    }

    public void setTabCodLoc(RichTable tabCodLoc) {
        this.tabCodLoc = tabCodLoc;
    }

    public RichTable getTabCodLoc() {
        return tabCodLoc;
    }

    public void setTabEsclusioni(RichTable tabEsclusioni) {
        this.tabEsclusioni = tabEsclusioni;
    }

    public RichTable getTabEsclusioni() {
        return tabEsclusioni;
    }

    public void setTabCodReg(RichTable tabCodReg) {
        this.tabCodReg = tabCodReg;
    }

    public RichTable getTabCodReg() {
        return tabCodReg;
    }
  
    public Cnf_anagraficheAction() {
        Object filters = ADFContext.getCurrent().getPageFlowScope().get("ulssSelectedFilters");
        
        if (filters==null || "".equals(filters.toString()))
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedFilters", "Tutte");
    }

    public void setSdoCodiciLocaliForm(RichForm sdoCodiciLocaliForm) {
        this.sdoCodiciLocaliForm = sdoCodiciLocaliForm;
    }

    public RichForm getSdoCodiciLocaliForm() {
        if (sdoCodiciLocaliForm == null){
            findForward("codiciloc");
        }
        return sdoCodiciLocaliForm;
    }
    
    protected void findForward(String type) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
        if ("codiciloc".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfAnagUlssView1Iterator");
        } else if ("esclusioni".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfEsclAnagView1Iterator");
        } else if ("codicireg".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfAnagRegView1Iterator");
        } else 
            return;
        
        if (navigDaDett != null ) {
            ViewObject vo = voIter.getViewObject();
            Row[] r = vo.findByKey(navigDaDett, 1);
            if (r != null && r.length>0)
                vo.setCurrentRow(r[0]);
                
            ViewHelper.queryAndRestoreCurrentRow(vo);
                
            if ("codiciloc".equals(type)){
                if (this.tabCodLoc == null)
                    this.tabCodLoc = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabCodLoc.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabCodLoc);
            } else if ("esclusioni".equals(type)){
                if (this.tabEsclusioni == null)
                    this.tabEsclusioni = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabEsclusioni.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabEsclusioni);
            } else if ("codicireg".equals(type)){
                if (this.tabCodReg == null)
                    this.tabCodReg = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabCodReg.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabCodReg);
            }
        } else {
            if ("codiciloc".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedCodLoc", "Tutte");

            else if ("esclusioni".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedEscl", "Tutte");

            else if ("codicireg".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedCodReg", "Tutte");               
        }
    }
    
    public void onDettCodLoc(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfAnagUlssView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("codiciloc", rowKey);
    }
    
    public void onDettEsclusioni(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfEsclAnagView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("esclusioni", rowKey);
    }
    
    public void onDettCodReg(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfAnagRegView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("codicireg", rowKey);
    }
    
    public void onChangeCodLoc(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlssCodLoc();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedCodLoc", selected.getValue());
        
        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfAnagUlssView1Iterator");
            ViewObject vo = voIter.getViewObject();            

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfAnagUlss.ULSS is not null");
            else 
                vo.setWhereClause("Cnf_SoCnfAnagUlss.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }
    
    public void onChangeFilters(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedFilters();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedFilters", "Tutte");

        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfAnagFilters1Iterator");
            ViewObject vo = voIter.getViewObject();

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfAnagScr.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCnfAnagScr.TPSCR='" + tpscr + "' AND Cnf_SoCnfAnagScr.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }
    
    public void onChangeEsclusioni(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedEsclusioni();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedEscl", "Tutte");

        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfEsclAnagView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfEsclAnag.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCnfEsclAnag.TPSCR='" + tpscr + "' AND Cnf_SoCnfEsclAnag.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }

    public void setSelectedUlssCodLoc(RichSelectOneChoice selectedUlssCodLoc) {
        this.selectedUlssCodLoc = selectedUlssCodLoc;
    }

    public RichSelectOneChoice getSelectedUlssCodLoc() {
        return selectedUlssCodLoc;
    }

    public void setSelectedFilters(RichSelectOneChoice selectedFilters) {
        this.selectedFilters = selectedFilters;
    }

    public RichSelectOneChoice getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedEsclusioni(RichSelectOneChoice selectedEsclusioni) {
        this.selectedEsclusioni = selectedEsclusioni;
    }

    public RichSelectOneChoice getSelectedEsclusioni() {
        return selectedEsclusioni;
    }
}
