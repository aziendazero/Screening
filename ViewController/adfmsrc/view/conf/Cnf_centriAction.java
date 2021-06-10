package view.conf;

import insiel.dataHandling.DateUtils;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.Cnf_AppModule;

import model.commons.ViewHelper;

import model.datacontrol.Agenda_Servizio;
import model.datacontrol.Cnf_selectionBean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Cnf_centriAction extends Parent_DataForwardAction {
    private RichForm centroForm;
    private RichForm agendaForm;
    private RichForm chiusuraForm;
    private RichForm centriFisiciForm;
    private RichForm utentiCentriForm;
    private RichForm userCentriForm;
    private RichForm agendaGenForm;
    
    private RichTable tabCentro;
    private RichTable tabAgenda;
    private RichTable tabChiusura;
    private RichTable tabCentriFisici;
    private RichTable tabUtentiCentri;

    private RichSelectOneChoice selectedCentri;
    private RichSelectBooleanCheckbox centro;
    
    private List<Integer> selectedUserCentri;


    public Cnf_centriAction() {
    }
    
    @Override
    protected void setAppModule() {
        this.amName="Cnf_AppModule";
    }

    public void setCentroForm(RichForm centroForm) {
        this.centroForm = centroForm;
    }

    public RichForm getCentroForm() {
        if (centroForm == null){
            findForward("centro");
        }
        return centroForm;
    }
    
    public void setAgendaForm(RichForm agendaForm) {
        this.agendaForm = agendaForm;
    }

    public RichForm getAgendaForm() {
        if (agendaForm == null){
            findForward("agenda");
        }
        return agendaForm;
    }

    public void setChiusuraForm(RichForm chiusuraForm) {
        this.chiusuraForm = chiusuraForm;
    }

    public RichForm getChiusuraForm() {
        if (chiusuraForm == null){
            findForward("chiusura");
        }
        return chiusuraForm;
    }
    
    public void setUserCentriForm(RichForm userCentriForm) {
        this.userCentriForm = userCentriForm;
    }

    public RichForm getUserCentriForm() {
        if (userCentriForm == null){
            findForward("userCentri");
        }
        return userCentriForm;
    }


    public void setAgendaGenForm(RichForm agendaGenForm) {
        this.agendaGenForm = agendaGenForm;
    }

    public RichForm getAgendaGenForm() {
        if (agendaGenForm == null) {
            findForward(null);
        }
        return agendaGenForm;
    }

    public void setTabCentro(RichTable tabCentro) {
        this.tabCentro = tabCentro;
    }

    public RichTable getTabCentro() {
        return tabCentro;
    }
    public void setTabAgenda(RichTable tabAgenda) {
        this.tabAgenda = tabAgenda;
    }

    public RichTable getTabAgenda() {
        return tabAgenda;
    }

    public void setTabChiusura(RichTable tabChiusura) {
        this.tabChiusura = tabChiusura;
    }

    public RichTable getTabChiusura() {
        return tabChiusura;
    }
    
    public void setCentro(RichSelectBooleanCheckbox centro) {
        this.centro = centro;
    }

    public RichSelectBooleanCheckbox getCentro() {
        return centro;
    }
    
    protected void findForward(String type) {     
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Key currentRowKey = null;
        DCIteratorBinding voIter=null;

        // H00282493
        DCIteratorBinding voIterCentro = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator");

        if(voIterCentro!=null && voIterCentro.getViewObject().getCurrentRow()!=null){
            Row r = voIterCentro.getViewObject().getCurrentRow();
            session.put("centro", r.getKey());
        }
        
        DCIteratorBinding voIterCentri = bindings.findIteratorBinding("Cnf_SoCnfCentroPrelView1Iterator");
        if (voIterCentri != null) {
            ViewObject voCentri = voIterCentri.getViewObject();
            voCentri.executeQuery();
        }
        // H00282493

        if ("centro".equals(type)){
            voIter = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator");
            currentRowKey = extractCurrentRowKey(session, type, voIter);
        } else if ("agenda".equals(type)){
            voIter = bindings.findIteratorBinding("Cnf_SoAgendacentroView2Iterator");
            currentRowKey = extractCurrentRowKey(session, type, voIter);
        } else if ("chiusura".equals(type)){
            voIter = bindings.findIteratorBinding("Cnf_SoChiusuracentroView2Iterator");
            currentRowKey = extractCurrentRowKey(session, type, voIter);
        } else if ("centriFisici".equals(type)){
            voIter = bindings.findIteratorBinding("Cnf_SoCentroFisicoExtendedView1Iterator");
            currentRowKey = extractCurrentRowKey(session, type, voIter);
        } else if ("utentiCentri".equals(type)){
            voIter = bindings.findIteratorBinding("Cnf_SoUtentiCentriView1Iterator");
            currentRowKey = extractCurrentRowKey(session, type, voIter);
        } else if ("userCentri".equals(type)){
            voIter = bindings.findIteratorBinding("Cnf_SecCnfUsersView1Iterator");
            currentRowKey = extractCurrentRowKey(session, type, voIter);
        } else {
            return;
        }
        
        // H00282493
        if(session.get("centro")!=null && voIterCentro!=null) {
            ViewObject vo = voIterCentro.getViewObject();
            Row[] r = null;
            try {
                r = vo.findByKey((Key) session.get("centro"), 1);
            } catch (Throwable th) {
            }

            if (r != null && r.length > 0 && r[0] != null && r[0].getAttribute(0) != null) {
                vo.setCurrentRow(r[0]);
                ViewHelper.queryAndRestoreCurrentRow(vo);
            }
        }
        // H00282493

        if (currentRowKey != null ) {         
            ViewObject vo = voIter.getViewObject();
            Row[] r = null;
            try {
                r = vo.findByKey(currentRowKey, 1);
            } catch (Throwable th) {
            }

            if (r != null && r.length > 0 && r[0] != null && r[0].getAttribute(0) != null) {
                vo.setCurrentRow(r[0]);
                ViewHelper.queryAndRestoreCurrentRow(vo);
            }

            if ("centro".equals(type)){
                if (this.tabCentro == null)
                    this.tabCentro = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabCentro.setSelectedRowKeys(selected);
                }
                            
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabCentro);
            /*} else if ("agenda".equals(type)){
                if (this.tabAgenda == null)
                    this.tabAgenda = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabAgenda.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabAgenda);*/
            } else if ("chiusura".equals(type)){
                if (this.tabChiusura == null)
                    this.tabChiusura = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabChiusura.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabChiusura);
            } else if ("centriFisici".equals(type)){
                if (this.tabCentriFisici == null)
                    this.tabCentriFisici = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabCentriFisici.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabCentriFisici);
                
            } else if ("utentiCentri".equals(type)){
                if (this.tabUtentiCentri == null)
                    this.tabUtentiCentri = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.tabUtentiCentri.setSelectedRowKeys(selected);
                }
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabUtentiCentri);
            }
        } else if ("centro".equals(type))
            ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedCentro", "Tutte");
        else if ("centriFisici".equals(type)){
            //Refresh tabella dopo creazione nuovo record
            voIter = bindings.findIteratorBinding("Cnf_SoCentroFisicoExtendedView1Iterator");
            ViewObject vo = voIter.getViewObject();
            vo.executeQuery();
        }  
    }
    
    public void onChangeCentri(ValueChangeEvent valueChangeEvent) {
        RichSelectOneChoice selected = this.getSelectedCentri();
        String ulss = "";
        
        if (selected!=null && selected.getValue()!=null)
            ulss = selected.getValue().toString();
        
        ADFContext.getCurrent().getPageFlowScope().put("ulssSelectedCentro", selected.getValue());
        
        if (ulss!=""){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator");
            ViewObject vo = voIter.getViewObject();
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String tpscr = (String) session.get("scr");

            if ("Tutte".equals(ulss))
                vo.setWhereClause("Cnf_SoCentroPrelRef.TPSCR = '" + tpscr + "'");
            else 
                vo.setWhereClause("Cnf_SoCentroPrelRef.TPSCR = '" + tpscr + "' AND Cnf_SoCentroPrelRef.ULSS = '" + ulss + "'");
            
            vo.executeQuery();
        }
    }
    
    public void onDettCentro(ActionEvent actionEvent) {
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("centro", rowKey);
        
        Object port = cSdo.getAttribute("Portale");
        if (port!=null){
            Integer portale = (Integer)port;
            
            if (portale==1)
                session.put("portale", true);
            else 
                session.put("portale", false);
        } else
            session.put("portale", false);
    }
    
    public void onDettAgenda(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoAgendacentroView2Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("agenda", rowKey);
    }
    
    public void onDettChiusura(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoChiusuracentroView2Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("chiusura", rowKey);
    }

    public void setSelectedCentri(RichSelectOneChoice selectedCentri) {
        this.selectedCentri = selectedCentri;
    }

    public RichSelectOneChoice getSelectedCentri() {
        return selectedCentri;
    }

    public void setCentriFisiciForm(RichForm centriFisiciForm) {
        this.centriFisiciForm = centriFisiciForm;
    }

    public RichForm getCentriFisiciForm() {
        if (centriFisiciForm == null){
            findForward("centriFisici");
        }
        return centriFisiciForm;
    }

    public void setTabCentriFisici(RichTable tabCentriFisici) {
        this.tabCentriFisici = tabCentriFisici;
    }

    public RichTable getTabCentriFisici() {
        return tabCentriFisici;
    }

    public void setUtentiCentriForm(RichForm utentiCentriForm) {
        this.utentiCentriForm = utentiCentriForm;
    }

    public RichForm getUtentiCentriForm() {
        if (utentiCentriForm == null){
            findForward("utentiCentri");
        }
        return utentiCentriForm;
    }

    public void setTabUtentiCentri(RichTable tabUtentiCentri) {
        this.tabUtentiCentri = tabUtentiCentri;
    }

    public RichTable getTabUtentiCentri() {
        return tabUtentiCentri;
    }

    public void onDettCentriFisici(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCentroFisicoExtendedView1Iterator");
        ViewObject voExt = voIter.getViewObject();
        Row cExt = voExt.getCurrentRow();
       
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cExt.getKey();
        session.put("centriFisici", rowKey);

        //Recuper l'object selezionato
        String idcentro = cExt.getAttribute("Idcentro").toString();
        
        DCIteratorBinding centriFisici = bindings.findIteratorBinding("Cnf_SoCnfCentroFisicoView1Iterator");
        ViewObject centroFisico = centriFisici.getViewObject();
        
        String where = "SoCnfCentroFisico.IDCENTRO = '" + idcentro + "'" ;
          
        centroFisico.setWhereClause(where);
        centroFisico.executeQuery();
    }
    
    public void onDettUtentiCentri(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoUtentiCentriView1Iterator");
        ViewObject voUc = voIter.getViewObject();
        Row cExt = voUc.getCurrentRow();
       
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cExt.getKey();
        session.put("utentiCentri", rowKey);

        //Filtra combo dei centri fisici con ulss
        String ulss = (String) session.get("ulss");
        if (ulss!=null && !"".equals(ulss)){
            DCIteratorBinding centriFisici = bindings.findIteratorBinding("Cnf_SoCnfCentroFisicoView1Iterator");
            ViewObject centroFisico = centriFisici.getViewObject();
            
            String where = "SoCnfCentroFisico.ULSS = '" + ulss + "'" ;
              
            centroFisico.setWhereClause(where);
            centroFisico.executeQuery();
        }
    }
    
    public void onDettUserCentri(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfUsersCentriView1Iterator");
        Cnf_selectionBean bean=(Cnf_selectionBean)BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        ViewObject voCentri = voIter.getViewObject();
        selectedUserCentri = new ArrayList<>();
        RowSetIterator iter =  voCentri.createRowSetIterator(null);
        while(iter.hasNext()){
            Row r = iter.next();
            selectedUserCentri.add(((Number)r.getAttribute("Idcentro")).intValue());
        }
        iter.closeRowSetIterator();
        
        DCIteratorBinding voUserIter = bindings.findIteratorBinding("Cnf_SecCnfUsersView1Iterator");
        Row userRow = voUserIter.getCurrentRow();
        bean.setUserId((Integer)userRow.getAttribute("SoUserId"));
        
        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("listacentri", selectedUserCentri);
        
        Key rowKey = userRow.getKey();
        session.put("userCentri", rowKey);
        
        String ulss = (String)session.get("ulss");
        String tpscr = (String)session.get("scr");
        DCIteratorBinding voLOVIter = bindings.findIteratorBinding("Cnf_SecCnfUsersViewLOV1Iterator");
        ViewObject vo = voLOVIter.getViewObject();
        vo.setWhereClause("ULSS = '"+ulss+"' AND TPSCR = '"+tpscr+"'");
        vo.executeQuery();
    }
    
    public void onDelete(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCentroFisicoExtendedView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row c = vo.getCurrentRow();
        
        //Recuper l'object partendo sall'elemento selezionato della lista
        String idcentro = c.getAttribute("Idcentro").toString();

        
        DCIteratorBinding centriFisici = bindings.findIteratorBinding("Cnf_SoCnfCentroFisicoView1Iterator");
        ViewObject centroFisico = centriFisici.getViewObject();
        
        String where = "SoCnfCentroFisico.IDCENTRO = '" + idcentro + "'" ;
          
        centroFisico.setWhereClause(where);
        centroFisico.executeQuery();
        
        if (centroFisico!=null){
            Row first = centroFisico.first();
            if (first!=null){
                first.remove();
                  
                Cnf_AppModule am2 = (Cnf_AppModule)BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
                am2.getTransaction().commit();
            }
        }
        
        //Refresh lista
        vo.executeQuery();
    }

    public void onDeleteUtentiCentri(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoUtentiCentriView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row c = vo.getCurrentRow();
        
        c.remove();
        
        Cnf_AppModule am = (Cnf_AppModule)BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        am.getTransaction().commit();
        
        vo.executeQuery();
    }
    
    public void onDeleteUserCentri(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfUsersCentriView1Iterator");
        ViewObject vo = voIter.getViewObject();

        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        
        RowSetIterator rsI = vo.createRowSetIterator(null);        
        try {
            String user = (String)ADFContext.getCurrent().getSessionScope().get("user");
            while (rsI.hasNext()) {
                Row r = rsI.next();
                r.setAttribute("Opmod", user);
                r.setAttribute("Dtmod", DateUtils.getOracleDate(new Date()));
            }
            commitBinding.execute();
        } catch (Exception e) {
            OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
            rollbackBinding.execute();
            this.handleException("Impossibile aggiornare la lista centri: " + e.getMessage());
            return;
        } finally {
            rsI.closeRowSetIterator();
        }

        rsI = vo.createRowSetIterator(null);        
        while(rsI.hasNext()){
            rsI.next().remove();
        }
        rsI.closeRowSetIterator();
        
        /*
        Cnf_AppModule am = (Cnf_AppModule)BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        am.getTransaction().commit();
        */
        commitBinding.execute();
        
        DCIteratorBinding voUtentiIter = bindings.findIteratorBinding("Cnf_SecCnfUsersView1Iterator");
        voUtentiIter.executeQuery();
    }
    
    public void onSelectCentro(SelectionEvent selectionEvent) {
        Parent_DataForwardAction.makeCurrent(selectionEvent);
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator");
        Cnf_selectionBean bean=(Cnf_selectionBean)BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            
          //se c'e' una current row allineo il bean
          if(voIter.getCurrentRow()!=null)
          {
            bean.setCentro((Integer)voIter.getCurrentRow().getAttribute("Idcentro"));
            
            //Memorizzo in sessione la chiave della riga selezionata
            ViewObject vo = voIter.getViewObject();
            Row c = vo.getCurrentRow();
              
            Map session = ADFContext.getCurrent().getSessionScope();
            Key rowKey = c.getKey();
            session.put("centro", rowKey);
          }
          else
          {//altrimenti imposto la prima riga come current e allineo il bean       
            Row f=voIter.getViewObject().first();
            if(f!=null)
            {
              voIter.getViewObject().setCurrentRow(f);  
              bean.setCentro((Integer)f.getAttribute("Idcentro"));
            }
          }          
    }

    public String onAgendaGiorno() {
        Cnf_selectionBean beanCFG = (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        
        Integer idCt = beanCFG.getCentro();
        
        if (idCt != null){
            Agenda_Servizio bean =
                (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
            
            bean.setIdCentro(idCt);
        }
            
        Map session = ADFContext.getCurrent().getSessionScope();

        String dtAg = session.get("dtAgenda")==null ? null : DateUtils.dateToString((Date) session.get("dtAgenda"));

        if(dtAg==null)
            session.put("dtAgenda", new java.util.Date());
            
        if (session.get("oraDaInvito")==null)    
            session.put("oraDaInvito", Boolean.valueOf(false));

        if (session.get("agDaMenu")==null)    
            session.put("agDaMenu", Boolean.valueOf(true));
        
        if (session.get("appSel")==null)    
            session.put("appSel", Boolean.valueOf(false));
        
        if (session.get("chAgenda")==null)    
            session.put("chAgenda", "menuApp.do");
        
        return "ag_preGiorn";
    }

    public void onChangePortale(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        Boolean checked = false;
        RichSelectBooleanCheckbox checkbox = this.getCentro();
        Map session = ADFContext.getCurrent().getSessionScope();
        
        if (this.getCentro()!=null && this.getCentro().getValue()!=null)
            checked = ((Boolean)checkbox.getValue())!=null?(Boolean)checkbox.getValue():false;
        
        session.put("portale", checked);
    }

    public void onNewCentro(ActionEvent actionEvent) {
        ADFContext.getCurrent().getSessionScope().put("portale", false);    
    }

    public void updateCentriUser(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfUsersCentriView1Iterator");
        ViewObject voCentri = voIter.getViewObject();
        Map session = ADFContext.getCurrent().getSessionScope();
        selectedUserCentri = (List<Integer>) session.get("listacentri");
        String action = (String)session.get("action");
        String ulss = (String)session.get("ulss");
        String tpscr = (String)session.get("scr");
        String user = (String)session.get("user");
        
        Cnf_selectionBean bean = (Cnf_selectionBean)BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        if ("edit".equals(action)){
            // aggiorno
            RowSetIterator rsI = voCentri.createRowSetIterator(null);
            try {
                OperationBinding commitBinding = bindings.getOperationBinding("Commit");
                boolean needCommit = false;
                while (rsI.hasNext()) {
                    Row r = rsI.next();
                    Integer id = ((Number) r.getAttribute("Idcentro")).intValue();
                    if (!selectedUserCentri.contains(id)) {
                        r.setAttribute("Opmod", user);
                        r.setAttribute("Dtmod", DateUtils.getOracleDate(new Date()));
                        needCommit = true;
                    }
                }
                if (needCommit) {
                    commitBinding.execute();
                }
            } catch (Exception e) {
                OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
                rollbackBinding.execute();
                this.handleException("Impossibile aggiornare la lista centri: " + e.getMessage());
                return;
            } finally {
                rsI.closeRowSetIterator();
            }
            
            //svuoto
            rsI = voCentri.createRowSetIterator(null);        
            while(rsI.hasNext()){
                Row r =rsI.next();
                Integer id = ((Number)r.getAttribute("Idcentro")).intValue();
                if (!selectedUserCentri.contains(id)) {
                    r.remove();
                }
            }
            rsI.closeRowSetIterator();
            
            try {
                //ricreo
                for (Integer centro : selectedUserCentri){
                    if (voCentri.getFilteredRows("Idcentro", new Number(centro)).length == 0){
                        Row newRow = voCentri.createRow();
                        newRow.setAttribute("SoUserId", bean.getUserId());   
                        newRow.setAttribute("Idcentro", new Number(centro));                
                        newRow.setAttribute("Ulss", ulss);
                        newRow.setAttribute("Tpscr", tpscr);
                        
                        /* I00093700 Punto 3 - registro sempre operatore e data inserimento perchè l'aggiornamento 
                         * (update) non viene mai eseguito. La deselezione del check in relazione utente - centro 
                         * equivale all'eliminazione del record */
                        newRow.setAttribute("Opins", user);
                        newRow.setAttribute("Dtins", DateUtils.getOracleDate(new Date()));

                        voCentri.insertRow(newRow);
                    }
                }
            } catch (SQLException e) {
            }
        } else {
            //nuovo
            try {
                for (Integer centro : selectedUserCentri){                   
                        Row newRow = voCentri.createRow();
                        newRow.setAttribute("SoUserId", bean.getUserId());   
                        newRow.setAttribute("Idcentro", new Number(centro));                
                        newRow.setAttribute("Ulss", ulss);
                        newRow.setAttribute("Tpscr", tpscr);
                    
                        //I00093700 Punto 3 
                        newRow.setAttribute("Opins", user);
                        newRow.setAttribute("Dtins", DateUtils.getOracleDate(new Date()));

                        voCentri.insertRow(newRow);
                }
            } catch (SQLException e) {
            }
        }
        
        try{             
            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            commitBinding.execute();
            
            DCIteratorBinding voUtentiIter = bindings.findIteratorBinding("Cnf_SecCnfUsersView1Iterator");
            voUtentiIter.executeQuery();
                      
            if ("create".equals(action)){
                Key k = new Key(new Object[]{bean.getUserId()});
                session.put("userCentri", k);
            }
            
        } catch (Exception e){
            this.handleException("Impossibile salvare: l'utente selezionato ha già associazioni: " + e.getMessage());
        }
        
    }

    public void setSelectedUserCentri(List<Integer> selectedUserCentri) {
        this.selectedUserCentri = selectedUserCentri;
    }

    public List<Integer> getSelectedUserCentri() {
        return selectedUserCentri;
    }

    public void onNewUserCentri(ActionEvent actionEvent) {
        //lista vuota
        selectedUserCentri = new ArrayList<>();
        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("listacentri", selectedUserCentri);
        
        Cnf_selectionBean bean=(Cnf_selectionBean)BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        bean.setUserId(null);
        
        String ulss = (String)session.get("ulss");
        String tpscr = (String)session.get("scr");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SecCnfUsersViewLOV1Iterator");
        ViewObject vo = voIter.getViewObject();
        vo.setWhereClause("ULSS = '"+ulss+"' AND TPSCR = '"+tpscr+"' AND SO_USER_ID NOT IN (SELECT SO_USER_ID FROM SO_CNF_USERS_CENTRI "+
                          " WHERE SO_CNF_USERS_CENTRI.ULSS = '"+ulss+"' AND SO_CNF_USERS_CENTRI.TPSCR='"+tpscr+"') ");
        vo.executeQuery();
    }


    private Key extractCurrentRowKey(Map session, String type, DCIteratorBinding voIter) {
        Key currentRowKey = (Key) session.get(type);
        if (currentRowKey == null) {
            Row currentRow = voIter.getCurrentRow();
            if (currentRow != null) {
                currentRowKey = currentRow.getKey();
            }
        }
        session.remove("type");
        return currentRowKey;
    }
}
