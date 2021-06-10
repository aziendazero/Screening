package view.conf;

import java.util.Arrays;
import java.util.Map;

import javax.faces.event.ActionEvent;

import model.commons.ViewHelper;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Cnf_accettAction extends Parent_DataForwardAction{
    private RichForm motIntForm;
    private RichForm tpprelForm;
    private RichForm tpintForm;
    
    private RichTable tabMotInt;
    private RichTable tabTpprel;
    private RichTable tabTpint;
    
    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }

    public void setTabMotInt(RichTable tabMotInt) {
        this.tabMotInt = tabMotInt;
    }

    public RichTable getTabMotInt() {
        return tabMotInt;
    }

    public void setTabTpprel(RichTable tabTpprel) {
        this.tabTpprel = tabTpprel;
    }

    public RichTable getTabTpprel() {
        return tabTpprel;
    }

    public void setTabTpint(RichTable tabTpint) {
        this.tabTpint = tabTpint;
    }

    public RichTable getTabTpint() {
        return tabTpint;
    }
    
    public Cnf_accettAction() {
    }

    public void setMotIntForm(RichForm motIntForm) {
        this.motIntForm = motIntForm;
    }

    public RichForm getMotIntForm() {
        if (motIntForm == null){
            findForward("motint");
        }
        return motIntForm;
    }

    public void setTpprelForm(RichForm tpprelForm) {
        this.tpprelForm = tpprelForm;
    }

    public RichForm getTpprelForm() {
        if (tpprelForm == null){
            findForward("tpprel");
        }
        return tpprelForm;
    }

    public void setTpintForm(RichForm tpintForm) {
        this.tpintForm = tpintForm;
    }

    public RichForm getTpintForm() {
        if (tpintForm == null){
            findForward("tpint");
        }
        return tpintForm;
    }
    
    protected void findForward(String type) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
        if ("motint".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfMotivoUltintView1Iterator");
        } else if ("tpprel".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfTpprelievoView1Iterator");
        } else if ("tpint".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfTpinterventoView1Iterator");
        } else 
            return;
        
        if (navigDaDett != null ) {         
                ViewObject vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                if (r != null && r.length>0)
                    vo.setCurrentRow(r[0]);
                
                ViewHelper.queryAndRestoreCurrentRow(vo);
                
            if ("motint".equals(type)){
                if (this.tabMotInt == null)
                    this.tabMotInt = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabMotInt.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabMotInt);
            } else if ("tpprel".equals(type)){
                if (this.tabTpprel == null)
                    this.tabTpprel = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabTpprel.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabTpprel);
            } else if ("tpint".equals(type)){
                if (this.tabTpint == null)
                    this.tabTpint = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabTpint.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabTpint);
            }
        }  
    }
    
    public void onDettMotInt(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfMotivoUltintView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("motint", rowKey);
    }
    
    public void onDettTpPrel(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfTpprelievoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("tpprel", rowKey);
    }
    
    public void onDettTpInt(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfTpinterventoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("tpint", rowKey);
    }

}