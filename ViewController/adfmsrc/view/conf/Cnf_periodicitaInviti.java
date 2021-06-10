package view.conf;

import insiel.dataHandling.DateUtils;

import java.sql.SQLException;

import java.util.Arrays;
import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.common.Cnf_AppModule;
import model.common.Ref_AppModule;

import model.commons.ViewHelper;

import model.conf.Cnf_SoCnfPeriodicitaInvitiViewRowImpl;

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

import oracle.jbo.domain.Number;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Cnf_periodicitaInviti extends Parent_DataForwardAction {

    private RichForm tabPeriodicitaInviti;
    private RichTable tabPer;
    private RichSelectOneChoice selectedUlss;

    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }
    
    public Cnf_periodicitaInviti() {
    }

    public void onDettPer(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfPeriodicitaInvitiView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row c = vo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = c.getKey();
        session.put("perinv", rowKey);
    }

    public void setTabPeriodicitaInviti(RichForm tabPeriodicitaInviti) {
        this.tabPeriodicitaInviti = tabPeriodicitaInviti;
    }

    public RichForm getTabPeriodicitaInviti() {
        if (tabPeriodicitaInviti == null){
            findForward();
        }
        return tabPeriodicitaInviti;
    }
    
    protected void findForward() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        
        String selectedUlss = session.get("ulssPeriodicitaInviti")!=null?session.get("ulssPeriodicitaInviti").toString():"";        
        if (!"".equals(selectedUlss))
            ulss = selectedUlss;
        
        /* Preparazione ViewObject coinvolti */
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
        
        /* Riselezione record */
        Key navigDaDett = (Key) session.get("perinv");
        
        //DCBindingContainer bindings = (DCBindingContainer) getBindings();
        voIter = bindings.findIteratorBinding("Cnf_SoCnfPeriodicitaInvitiView1Iterator");
        if (navigDaDett != null ) {         
            vo = voIter.getViewObject();
            Row[] r = vo.findByKey(navigDaDett, 1);
            
            if (r != null && r.length>0)
                vo.setCurrentRow(r[0]);
                
            ViewHelper.queryAndRestoreCurrentRow(vo);
                
            if (this.tabPer == null)
                this.tabPer = new RichTable();
            
            Row _row = vo.getCurrentRow();

            if (_row != null) {
                RowKeySet selected = new RowKeySetImpl();
                selected.add(Arrays.asList(_row.getKey()));
                this.tabPer.setSelectedRowKeys(selected);
            }
                
            Utility.gotoTablePageOfSelectedRow(voIter, this.tabPer);
        } else 
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", "Tutte"); 
    }

    public void setTabPer(RichTable tabPer) {
        this.tabPer = tabPer;
    }

    public RichTable getTabPer() {
        return tabPer;
    }

    public void onConfirm(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String)session.get("user");
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfPeriodicitaInvitiView1Iterator");
        
        ViewObject vo = voIter.getViewObject();
        Cnf_SoCnfPeriodicitaInvitiViewRowImpl r = (Cnf_SoCnfPeriodicitaInvitiViewRowImpl)vo.getCurrentRow();
        
        //Siamo in modifica
        if (r!=null && r.getOpins()!=null){
            r.setOpultmod(user);
            r.setDtultmod(DateUtils.getOracleDateNow());
        } else {//Siamo in creazione
            if (!"050000".equals(ulss))
                r.setUlss(ulss);
            
            r.setTpscr(tpscr);
            r.setOpins(user);
            r.setDtins(DateUtils.getOracleDateNow());
        }
        Cnf_AppModule am =
            (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();

        try {
            am.getTransaction().commit();
        } catch (Exception ex) {
            //am.getTransaction().rollback();
            //this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
            handleException("Oggetto già presente in DB: " + ex.getMessage());

            
            return;
        }
               
        Key rowKey = r.getKey();
        session.put("perinv", rowKey);
        
    }
    
    public void onChange(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedUlss();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelected", selected.getValue());
    
        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            session.put("ulssPeriodicitaInviti", ulss);
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfPeriodicitaInvitiView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCnfPeriodicitaInviti.TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCnfPeriodicitaInviti.TPSCR='" + tpscr + "' AND Cnf_SoCnfPeriodicitaInviti.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }

    public void setSelectedUlss(RichSelectOneChoice selectedUlss) {
        this.selectedUlss = selectedUlss;
    }

    public RichSelectOneChoice getSelectedUlss() {
        return selectedUlss;
    }

    public String onConf() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String)session.get("user");
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        
        Cnf_AppModule am =
            (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfPeriodicitaInvitiView1Iterator");
        
        ViewObject vo = voIter.getViewObject();
        Cnf_SoCnfPeriodicitaInvitiViewRowImpl r = (Cnf_SoCnfPeriodicitaInvitiViewRowImpl)vo.getCurrentRow();
 
        //Siamo in modifica
        if (r!=null && r.getOpins()!=null){
            r.setOpultmod(user);
            r.setDtultmod(DateUtils.getOracleDateNow());
        } else {//Siamo in creazione
            
            String query = "select count(*) from SO_CNF_PERIODISMO where CODCLASSEPOP = '" + r.getCodclassepop() + 
                           "' and IDTPINVITO = '" + r.getIdtpinvito() + "' and TPSCR = '" + tpscr +
                           "' and ulss = '" + ulss + "'";
                   
            ViewObject voCheck = am.createViewObjectFromQueryStmt("", query);
            voCheck.setRangeSize(-1);
            voCheck.executeQuery();
    
            Number cnt = (Number) voCheck.first().getAttribute(0);
    
            if (cnt.intValue() > 0) {
                this.handleException("Oggetto già presente in DB", null);
                return null;
            }
                
            if (!"050000".equals(ulss))
                r.setUlss(ulss);
            
            r.setTpscr(tpscr);
            r.setOpins(user);
            r.setDtins(DateUtils.getOracleDateNow());
        }
        
        try {
            am.getTransaction().commit();
        } catch (Exception ex) {
            this.handleException("Oggetto già presente in DB", null);
            r.setOpultmod(null);
            //am.getTransaction().rollback();
            //handleException("Oggetto già presente in DB: " + ex.getMessage());

            return null;
        }
               
        Key rowKey = r.getKey();
        session.put("perinv", rowKey);
        
        return "confirm";
    }

    public String onAppl() {
        String ret = this.onConf();
        
        if (ret==null)
            return ret;

        return "apply";
    }
}
