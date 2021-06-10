package view.conf;

import java.util.Arrays;
import java.util.Map;

import oracle.jbo.domain.Date;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.common.Cnf_AppModule;

import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Cnf_flussiRegionaliAction extends Parent_DataForwardAction {
    private RichForm sdoFiltriForm;
    private RichForm sdoAssociazioniForm;
    private RichForm sdoCentriForm;
    
    private RichTable tabFiltri;
    private RichTable tabAssociazioni;
    private RichTable tabCentri;
    
    private RichSelectOneChoice selectedCentri;
    private RichSelectOneChoice selectedSdoSps;

    private String export = "";
    
    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }

    public void setSdoAssociazioniForm(RichForm sdoAssociazioniForm) {
        this.sdoAssociazioniForm = sdoAssociazioniForm;
    }

    public RichForm getSdoAssociazioniForm() {
        if (sdoAssociazioniForm == null){
            findForward("associazioni");
        }
        return sdoAssociazioniForm;
    }

    public void setSdoCentriForm(RichForm sdoCentriForm) {
        this.sdoCentriForm = sdoCentriForm;
    }

    public RichForm getSdoCentriForm() {
        if (sdoCentriForm == null){
            findForward("centri");
        }
        return sdoCentriForm;
    }

    public void setTabFiltri(RichTable tabFiltri) {
        this.tabFiltri = tabFiltri;
    }

    public RichTable getTabFiltri() {
        return tabFiltri;
    }

    public void setTabAssociazioni(RichTable tabAssociazioni) {
        this.tabAssociazioni = tabAssociazioni;
    }

    public RichTable getTabAssociazioni() {
        return tabAssociazioni;
    }

    public void setTabCentri(RichTable tabCentri) {
        this.tabCentri = tabCentri;
    }

    public RichTable getTabCentri() {
        return tabCentri;
    }
    
    public Cnf_flussiRegionaliAction() {
    }

    public void setSdoFiltriForm(RichForm sdoFiltriForm) {
        this.sdoFiltriForm = sdoFiltriForm;
    }

    public RichForm getSdoFiltriForm() {
        if (sdoFiltriForm == null){
            findForward("filtri");
        }
        return sdoFiltriForm;
    }
    
    protected void findForward(String type) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key navigDaDett = null;
        DCIteratorBinding voIter=null;
        if ("filtri".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfSdoSpsElencoView1Iterator");
        } else if ("associazioni".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfSdospsEsclElencoView1Iterator");
        } else if ("centri".equals(type)){
            navigDaDett = (Key) session.get(type);
            voIter = bindings.findIteratorBinding("Cnf_SoCnfDwhCentriView1Iterator");
        } else 
            return;
        
        if (navigDaDett != null ) {         
                ViewObject vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                if (r != null && r.length>0)
                    vo.setCurrentRow(r[0]);
                
                ViewHelper.queryAndRestoreCurrentRow(vo);
                
            if ("filtri".equals(type)){
                if (this.tabFiltri == null)
                    this.tabFiltri = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabFiltri.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabFiltri);
            } else if ("associazioni".equals(type)){
                if (this.tabAssociazioni == null)
                    this.tabAssociazioni = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabAssociazioni.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabAssociazioni);
                
            } else if ("centri".equals(type)){
                if (this.tabCentri == null)
                    this.tabCentri = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabCentri.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabCentri);
            }
        } else {
            if ("associazioni".equals(type)){
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedAssoc", "Tutte");
                
                //Se e' stato creato un nuovo record -> refresh ViewObject
                Object editSdo = session.get("editSdo");
                if (editSdo!=null && "false".equals(editSdo)){
                    voIter = bindings.findIteratorBinding("Cnf_SoCnfSdospsEsclElencoView1Iterator");
                    ViewObject vo = voIter.getViewObject();
                    vo.executeQuery();
                }
            }

            else if ("centri".equals(type))
                ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedCentri", "Tutte");    
        }  
    }
    
    public void onDettFiltri(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSdoSpsElencoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("filtri", rowKey);
    }
    
    public void onDettAssociazioni(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSdospsEsclElencoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
 
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("associazioni", rowKey);
        
        //Recuper l'object selezionato
        String tipoF = (String) cSdo.getAttribute("TipoFlusso");
        String codice = (String) cSdo.getAttribute("Codsdosps");
        String ulss = (String) cSdo.getAttribute("Ulss");
        String tpscr = (String) cSdo.getAttribute("Tpscr");
        
        DCIteratorBinding escl = bindings.findIteratorBinding("Cnf_SoCnfSdospsEsclView1Iterator");
        ViewObject esclDet = escl.getViewObject();
        
        String where = "codsdosps = '" + codice + "' and tipo_flusso = '" + tipoF + "' and ulss = '" + ulss + "' and tpscr = '" + tpscr + "'" ;
          
        esclDet.setWhereClause(where);
        esclDet.executeQuery();
    }
    
    public void onDeleteAssociazione(DialogEvent dialogEvent) { 
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSdospsEsclElencoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        //Recuper l'object partendo sall'elemento selezionato della lista
        String tipoF = (String) cSdo.getAttribute("TipoFlusso");
        String codice = (String) cSdo.getAttribute("Codsdosps");
        String ulss = (String) cSdo.getAttribute("Ulss");
        String tpscr = (String) cSdo.getAttribute("Tpscr");
        
        DCIteratorBinding escl = bindings.findIteratorBinding("Cnf_SoCnfSdospsEsclView1Iterator");
        ViewObject esclDet = escl.getViewObject();
        
        String where = "codsdosps = '" + codice + "' and tipo_flusso = '" + tipoF + "' and ulss = '" + ulss + "' and tpscr = '" + tpscr + "'" ;
          
        esclDet.setWhereClause(where);
        esclDet.executeQuery();
        
        if (esclDet!=null){
            Row fInv = esclDet.first();
            if (fInv!=null){
                fInv.remove();
                  
                Cnf_AppModule am2 = (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
                am2.getTransaction().commit();
            }
        }
        
        //Refresh lista
        voSdo.executeQuery();
    }
    
    public void onDettCentri(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfDwhCentriView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("centri", rowKey);
    }
    
    public void onChangeCentri(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedCentri();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedCentri", selected.getValue());
        
        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfDwhCentriView1Iterator");
            ViewObject vo = voIter.getViewObject();

            if ("Tutte".equals(ulss))
                vo.setWhereClause("TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("TPSCR='" + tpscr + "' AND ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }

    public void onChangeSdoSps(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedSdoSps();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedAssoc", selected.getValue());
        
        if (ulss!=""){
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSdospsEsclElencoView1Iterator");
            ViewObject vo = voIter.getViewObject();

            if ("Tutte".equals(ulss))
                vo.setWhereClause("TPSCR='" + tpscr + "'");
            else 
                vo.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + ulss + "'");
            
            vo.executeQuery();
        }
    }
    
    public void setSelectedCentri(RichSelectOneChoice selectedCentri) {
        this.selectedCentri = selectedCentri;
    }

    public RichSelectOneChoice getSelectedCentri() {
        return selectedCentri;
    }

    public void setSelectedSdoSps(RichSelectOneChoice selectedSdoSps) {
        this.selectedSdoSps = selectedSdoSps;
    }

    public RichSelectOneChoice getSelectedSdoSps() {
        return selectedSdoSps;
    }

    public void onNewAssociazione(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSdospsMotEsclView1Iterator");
        
        if (voIter!=null){
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");
            String ulss = (String) session.get("ulss");
            
            String where = "ulss = '" + ulss + "' and tpscr = '" + tpscr + "'";
            
            vo.setWhereClause(where);
            vo.setOrderByClause("idcnfescl");
            vo.executeQuery();
        }
    }

    public void setExport(String export) {
        this.export = export;
    }

    public String getExport() {
        String str = "";
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfSdoSpsElencoView1Iterator");
        
        if (voIter!=null){
            ViewObject voElenco = voIter.getViewObject();
            RowSetIterator rsi =ViewHelper.getRowSetIterator(voElenco, "iter_elenco");
            
            java.text.SimpleDateFormat displayDateFormat = new java.text.SimpleDateFormat ("dd/MM/yyyy");
            
            while (rsi.hasNext()) {
                Row cRow = rsi.next();
    
                String tipoFl = (String) cRow.getAttribute("TipoFlusso");
                str += tipoFl + ";";
                
                String codice = (String) cRow.getAttribute("Codsdosps");
                str += codice + ";";     
                
                String descr = (String) cRow.getAttribute("Descrizione");      
                str += descr + ";";      
                
                Date dtIn = (Date) cRow.getAttribute("DataInizioValidita");
                String dtInval = "";
                
                if (dtIn != null)
                    dtInval = displayDateFormat.format(dtIn.dateValue());
                
                str += dtInval + ";"; 
                
                Date dtFin = (Date) cRow.getAttribute("DataFineValidita");      
                String dtFinval = "";      
                
                if (dtFin != null)
                    dtFinval = displayDateFormat.format(dtFin.dateValue());
                
                str += dtFinval + ";<br>";
            }
        }
        
        return str;
    }
}
