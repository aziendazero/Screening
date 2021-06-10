package view.conf;

import java.util.Arrays;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Cnf_geog extends Parent_DataForwardAction {
    private RichForm statiForm;
    private RichForm provinceForm;
    private RichForm comuniForm;
    private RichForm roundComuniForm;
    private RichForm zoneForm;
    
    private RichTable tabStati;
    private RichTable tabProvince;
    private RichTable tabComuni;
    private RichTable tabRoundComuni;
    private RichTable tabZone;
    
    private RichSelectOneChoice selectedUlssComuni;
    private RichSelectOneChoice selectedUlssZone;
    private RichForm comzoneForm;
    private RichTable tabComzone;

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }
    
    public Cnf_geog() {
    }

    public void setStatiForm(RichForm statiForm) {
        this.statiForm = statiForm;
    }

    public RichForm getStatiForm() {
        if (statiForm == null){
            findForward("stati");
        }
        return statiForm;
    }

    public void setProvinceForm(RichForm provinceForm) {
        this.provinceForm = provinceForm;
    }

    public RichForm getProvinceForm() {
        if (provinceForm == null){
            findForward("province");
        }
        return provinceForm;
    }

    public void setComuniForm(RichForm comuniForm) {
        this.comuniForm = comuniForm;
    }

    public RichForm getComuniForm() {
        if (comuniForm == null){
            findForward("comuni");
        }
        return comuniForm;
    }

    public void setRoundComuniForm(RichForm roundComuniForm) {
        this.roundComuniForm = roundComuniForm;
    }

    public RichForm getRoundComuniForm() {
        if (roundComuniForm == null){
            findForward("round");
        }
        return roundComuniForm;
    }

    public void setZoneForm(RichForm zoneForm) {
        this.zoneForm = zoneForm;
    }

    public RichForm getZoneForm() {
        if (zoneForm == null){
            findForward("zone");
        }
        return zoneForm;
    }
    
    public void onDettStati(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoStatoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("stati", rowKey);
    }
    
    public void onDettProvince(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoProvinciaView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("province", rowKey);
    }
    
    public void onDettComuni(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoComuneView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("comuni", rowKey);
    }
    
    public void onDettRoundComuni(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Round_SoRoundAttiviView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("round", rowKey);
    }
    
    public void onDettZone(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoDistrettoZonaView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("zone", rowKey);
    }
    
    protected void findForward(String type) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
        if ("stati".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoStatoView1Iterator");
        } else if ("province".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoProvinciaView1Iterator");
        } else if ("comuni".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoComuneView1Iterator");
        }  else if ("round".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Round_SoRoundAttiviView1Iterator");
        }  else if ("zone".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoDistrettoZonaView1Iterator");
        } else if ("comzone".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfComuniZoneParamView1Iterator");
        } else 
            return;
        
        if (navigDaDett != null ) {  
            ViewObject vo = voIter.getViewObject();
            Row[] r = vo.findByKey(navigDaDett, 1);
            if(r!=null && r.length>0)                
                vo.setCurrentRow(r[0]);
                
            ViewHelper.queryAndRestoreCurrentRow(vo);
                
            if ("stati".equals(type)){
                if (this.tabStati == null)
                    this.tabStati = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabStati.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabStati);
            } else if ("province".equals(type)){
                if (this.tabProvince == null)
                    this.tabProvince = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabProvince.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabProvince);
            } else if ("comuni".equals(type)){
                if (this.tabComuni == null)
                    this.tabComuni = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabComuni.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabComuni);
            } else if ("round".equals(type)){
                if (this.tabRoundComuni == null)
                    this.tabRoundComuni = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabRoundComuni.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabRoundComuni);
            } else if ("zone".equals(type)){
                if (this.tabZone == null)
                    this.tabZone = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabZone.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabZone);
            } else if ("comzone".equals(type)){
                if (this.tabComzone == null)
                    this.tabComzone = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabComzone.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabComzone);
            }  
        } else {
            if ("comuni".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedComuni", "Tutte");

            else if ("round".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedRound", "Tutte");               
            
            else if ("zone".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedZone", "Tutte");  
            
            else if ("province".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedProv", "Tutte");  
            
            else if ("stati".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedStati", "Tutte");  
        }
    }

    public void setTabStati(RichTable tabStati) {
        this.tabStati = tabStati;
    }

    public RichTable getTabStati() {
        return tabStati;
    }

    public void setTabProvince(RichTable tabProvince) {
        this.tabProvince = tabProvince;
    }

    public RichTable getTabProvince() {
        return tabProvince;
    }

    public void setTabComuni(RichTable tabComuni) {
        this.tabComuni = tabComuni;
    }

    public RichTable getTabComuni() {
        return tabComuni;
    }

    public void setTabRoundComuni(RichTable tabRoundComuni) {
        this.tabRoundComuni = tabRoundComuni;
    }

    public RichTable getTabRoundComuni() {
        return tabRoundComuni;
    }

    public void setTabZone(RichTable tabZone) {
        this.tabZone = tabZone;
    }

    public RichTable getTabZone() {
        return tabZone;
    }

    public void setSelectedUlssComuni(RichSelectOneChoice selectedUlssComuni) {
        this.selectedUlssComuni = selectedUlssComuni;
    }

    public RichSelectOneChoice getSelectedUlssComuni() {
        return selectedUlssComuni;
    }
    
    public void onChangeComuni(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlssComuni();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedComuni", selected.getValue());
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoComuneView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("");
            else 
                vo.setWhereClause("Cnf_SoComune.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }

    public void onChangeZone(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlssZone();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedZone", selected.getValue());
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoDistrettoZonaView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("");
            else 
                vo.setWhereClause("Cnf_SoDistrettoZona.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }
    
    public void setSelectedUlssZone(RichSelectOneChoice selectedUlssZone) {
        this.selectedUlssZone = selectedUlssZone;
    }

    public RichSelectOneChoice getSelectedUlssZone() {
        return selectedUlssZone;
    }

    public void onChangeRound(ValueChangeEvent valueChangeEvent) {

        String ulss = (String) valueChangeEvent.getNewValue(); 
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Round_SoRoundAttiviView1Iterator");
            ViewObject vo = voIter.getViewObject();
            String whereClause = "";
            if ("Tutte".equals(ulss))
                whereClause = "CODSCR(+)='" + tpscr + "'";           
            else 
                whereClause = "CODSCR(+)='" + tpscr + "' AND ULSS = '"+ulss+"' ";
            
            vo.setWhereClause(whereClause);
            vo.executeQuery();
        }
    }
    
    public void handleMessage(FacesMessage.Severity severity, String msg) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(severity, msg, "");
            ctx.addMessage(null, fm);
    }

    public void delete1Action(DialogEvent dialogEvent) {
        BindingContext bc = BindingContext.getCurrent();
        BindingContainer bindings = bc.getCurrentBindingsEntry();
        OperationBinding deleteBinding = bindings.getOperationBinding("Delete1");
        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        Object result = deleteBinding.execute();
        if (!deleteBinding.getErrors().isEmpty()) {
                //add error handling here
        }
        Object commitResult = commitBinding.execute();
        if (!commitBinding.getErrors().isEmpty()) {
                OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
                rollbackBinding.execute();
                this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile cancellare il record: restrizione di integrità violata, chiave figlia trovata.");
        }
    }
    
    public void delete2Action(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoComDistrettiZoneView2Iterator");    
        
        if (voIter==null)
            voIter = bindings.findIteratorBinding("Cnf_SoComDistrettiZoneView3Iterator");  
        
        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        
        if (voIter!=null && voIter.getCurrentRow()!=null){
            voIter.getCurrentRow().remove();
            
            Object commitResult = commitBinding.execute();
            if (!commitBinding.getErrors().isEmpty()) {
                    OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
                    rollbackBinding.execute();
                    this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile cancellare il record: restrizione di integrità violata, chiave figlia trovata.");
            }
        }
    }

    public void setComzoneForm(RichForm comzoneForm) {
        this.comzoneForm = comzoneForm;
    }

    public RichForm getComzoneForm() {
        if (comzoneForm == null){
            findForward("comzone");
        }
        
        return comzoneForm;
    }

    public void onDettComzone(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfComuniZoneParamView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("comzone", rowKey);
    }

    public void setTabComzone(RichTable tabComzone) {
        this.tabComzone = tabComzone;
    }

    public RichTable getTabComzone() {
        return tabComzone;
    }

    public void onChangeUlss(ValueChangeEvent valueChangeEvent) {
        String ulss = (String) valueChangeEvent.getNewValue(); 
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfComuniZoneParamView1Iterator");
            ViewObject vo = voIter.getViewObject();
            String whereClause = "";
            if ("Tutte".equals(ulss))
                whereClause = "Cnf_SoCnfComuniZoneParam.TPSCR='" + tpscr + "'";           
            else 
                whereClause = "Cnf_SoCnfComuniZoneParam.TPSCR='" + tpscr + "' AND Cnf_SoCnfComuniZoneParam.ULSS = '"+ulss+"' ";
            
            vo.setWhereClause(whereClause);
            vo.executeQuery();
        }
    }
}
