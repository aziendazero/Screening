package view.conf;

import java.text.DateFormat;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.faces.event.ActionEvent;

import model.common.Cnf_AppModule;

import model.commons.AccessManager;

import model.commons.ViewHelper;

import model.conf.Cnf_SoCnfInvitiFastViewRowImpl;

import model.datacontrol.Sogg_NuovoParam;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import oracle.jbo.server.SequenceImpl;

import org.apache.myfaces.trinidad.context.RequestContext;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;


public class Cnf_invitiFastAction extends Parent_DataForwardAction {
    private RichForm invitiFastForm;
    private RichTable fastTab;

    public Cnf_invitiFastAction() {
        super();
    }
    
    @Override
    protected void setAppModule() {
        this.amName = "Cnf_AppModule";
    }
    public String onNew() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");

        Cnf_AppModule am =
            (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfInvitiFastView1Iterator");

        ViewObject vo = voIter.getViewObject();
        Cnf_SoCnfInvitiFastViewRowImpl invFast = (Cnf_SoCnfInvitiFastViewRowImpl)vo.createRow();
        
        SequenceImpl s = new SequenceImpl("SO_INVITI_FAST_SEQ", am);
        invFast.setId(s.getSequenceNumber());
        
        invFast.setNomeConf("New");
        invFast.setUlss(ulss);
        invFast.setTpscr(tpscr);
        invFast.setIdtpinvito("0 ");
        invFast.setIdcentro(0);
        invFast.setCodesitoinvito("?");

        vo.insertRow(invFast);
        
        am.getTransaction().commit();

        return null;
    }

    public void setInvitiFastForm(RichForm invitiFastForm) {
        this.invitiFastForm = invitiFastForm;
    }

    public RichForm getInvitiFastForm() {
        if (invitiFastForm == null){
            findForward();
        }
        
        return invitiFastForm;
    }
    
    protected void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        String user = (String) session.get("user");
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        
        DCIteratorBinding voIter=bindings.findIteratorBinding("Cnf_SoCnfInvitiFastView1Iterator");
        if (voIter!=null && voIter.getViewObject()!=null){
            ViewObject vo = voIter.getViewObject();
            
            String where = "ULSS = '" + ulss + "' AND TPSCR = '" + tpscr + "' AND OPINS = '" + user + "'";
            
            vo.setWhereClause(where);
            vo.executeQuery();
        }
        
        voIter=bindings.findIteratorBinding("Sogg_TipoInvitoView1Iterator");
        if (voIter!=null && voIter.getViewObject()!=null){
            ViewObject vo = voIter.getViewObject();
            
            String where = "ULSS = '" + ulss + "' AND TPSCR = '" + tpscr + "'";

            vo.setWhereClause(where);
            vo.executeQuery();
        }
        
        voIter=bindings.findIteratorBinding("Sogg_EsitoView1Iterator");
        
        if (voIter!=null && voIter.getViewObject()!=null){
            ViewObject vo = voIter.getViewObject();
            
            String where = "ULSS = '" + ulss + "' AND TPSCR = '" + tpscr + "'";

            vo.setWhereClause(where);
            vo.executeQuery();
        } 
        
        voIter=bindings.findIteratorBinding("Sogg_CprelInvitoView1Iterator");
        
        if (voIter!=null && voIter.getViewObject()!=null){
            ViewObject vo = voIter.getViewObject();
            
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String oggi = df.format(new java.util.Date());
            
            String where =
                "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" + " and (DTCHIUSURACENTRO is null or " +
                " DTCHIUSURACENTRO >= to_date('" + oggi + "','dd/mm/yyyy'))";

            //se sono un utente legato ad un centro devo poter vedere solo i miei centri
            //piu', al massimo, quello dell'invito
            String in = (String) session.get("elenco_centri");
            if (in != null)
                where += " and (IDCENTRO in " + in + ")";
            
            try{
                AccessManager.checkPermission("SOLimiteCentri");
                List<Integer> centriAutorizzati = (List<Integer>) session.get("centriautorizzati");
                String inCentri = "";
                if (centriAutorizzati != null && !centriAutorizzati.isEmpty()){
                    for (int i = 0; i < centriAutorizzati.size(); i++) {
                        inCentri += "" + centriAutorizzati.get(i) + ",";
                    }
                    inCentri = inCentri.substring(0, inCentri.length() - 1);
                    where += " AND IDCENTRO in (" + inCentri + ") ";
                } else {
                    where += " AND 1=2 ";
                }           
            } catch (IllegalAccessException e){                                      
               // non faccio niente
            }

            vo.setWhereClause(where);
            vo.executeQuery();
            
            /* Riselezione record */
            Key navigDaDett = (Key) session.get("invfast");
            
            //DCBindingContainer bindings = (DCBindingContainer) getBindings();
            voIter = bindings.findIteratorBinding("Cnf_SoCnfInvitiFastView1Iterator");
            if (navigDaDett != null ) {         
                vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                
                if (r != null && r.length>0)
                    vo.setCurrentRow(r[0]);
                    
                ViewHelper.queryAndRestoreCurrentRow(vo);
                    
                if (this.fastTab == null)
                    this.fastTab = new RichTable();
                
                Row _row = vo.getCurrentRow();

                if (_row != null) {
                    RowKeySet selected = new RowKeySetImpl();
                    selected.add(Arrays.asList(_row.getKey()));
                    this.fastTab.setSelectedRowKeys(selected);
                }
                    
                Utility.gotoTablePageOfSelectedRow(voIter, this.fastTab);
            }
        }        
    }
    
    public void onDettInvFast(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfInvitiFastView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row c = vo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = c.getKey();
        session.put("invfast", rowKey);
    }

    public void setFastTab(RichTable fastTab) {
        this.fastTab = fastTab;
    }

    public RichTable getFastTab() {
        return fastTab;
    }

}
