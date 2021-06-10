package view.medico;

import java.util.Map;

import javax.faces.event.ActionEvent;

import model.common.Med_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;

import model.commons.ViewHelper;

import model.datacontrol.Med_RicParam;

import model.datacontrol.PL_Bean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;
import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.ReturnEvent;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Med_gestioneAction extends Parent_DataForwardAction {

    private RichForm mediciForm;
    
    private RichInputText codiceComune;
    private RichInputText descComune;
    private RichTable tabMedici;

    @Override
    protected void setAppModule() {
        this.amName="Med_AppModule";
    }

    public void setMediciForm(RichForm mediciForm) {
        this.mediciForm = mediciForm;
    }

    public RichForm getMediciForm() {
        if (mediciForm == null){
            findForward();
        }
        return mediciForm;
    }
    
    protected void findForward() {
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Boolean navigDaSogg = (Boolean) session.get("navDaSogg");
        Key navigDaDett = (Key) session.get("navMedDett");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Med_ElencoMediciView1Iterator");
        if (navigDaDett != null ) {  
            if (!navigDaDett.isAnyNull()){
                ViewObject vo = voIter.getViewObject();
                Row[] r = vo.findByKey(navigDaDett, 1);
                if(r!=null)                
                    vo.setCurrentRow(r[0]);
                
                ViewHelper.queryAndRestoreCurrentRow(vo);
                
                if (this.tabMedici == null)
                    this.tabMedici = new RichTable();
                
                Utility.gotoTablePageOfSelectedRow(voIter, this.tabMedici);
            }
        }  else {
            BindingContext ctx = BindingContext.getCurrent();
            Med_RicParam bean = (Med_RicParam) ctx.findDataControl("Med_RicParamDataControl").getDataProvider();
            bean.reset();

            ViewObject voMed = voIter.getViewObject();
            voMed.setWhereClause("1=2");
            voMed.executeQuery();
        }
    }
    
    public void cerca(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Med_ElencoMediciView1Iterator");
        ViewObject voMed = voIter.getViewObject();

        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        String whcl = "CODSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
        Med_RicParam bean =
            (Med_RicParam) BindingContext.getCurrent().findDataControl("Med_RicParamDataControl").getDataProvider();
        String cogn = bean.getCognome();
        String nome = bean.getNome();
        Integer cod = bean.getCodMedico();
        String codCom = bean.getCodCom();
        Integer rcComune = bean.getRcCom();

        cogn = ViewHelper.replaceApostrophe(cogn);
        if (cogn != null && !(cogn.equals(""))) {
            whcl += " and COGNOME LIKE '" + cogn + "%'";
        }

        nome = ViewHelper.replaceApostrophe(nome);
        if (nome != null && !(nome.equals(""))) {
            whcl += " and NOME LIKE '" + nome + "%'";
        }

        if (cod != null) {
            whcl += " and CODICEREGMEDICO = " + cod.toString();
        }

        if (codCom != null && !(codCom.equals(""))) {
            whcl += " and CODCOM = '" + codCom + "' and RELEASE_CODE_COM =" + rcComune;
        }

        voMed.setWhereClause(whcl);
        voMed.executeQuery();
    }
    
    public String chDescom() {
        Med_RicParam bean =
            (Med_RicParam) BindingContext.getCurrent().findDataControl("Med_RicParamDataControl").getDataProvider();
        String desCom = bean.getDesCom();

        if (desCom == null || desCom.equals("")) {
            bean.setCodCom(null);
            bean.setRcCom(null);
        }
        return "lovComune";
    }
    
    protected void beforeNavigate(String dest) throws Exception {
        Map session = ADFContext.getCurrent().getSessionScope();
        session.remove("navMedDett");
        session.remove("navDaSogg");
    }

    public void lovComuneReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(codiceComune);
        RequestContext.getCurrentInstance().addPartialTarget(descComune);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', false)");
    }

    public void setCodiceComune(RichInputText codiceComune) {
        this.codiceComune = codiceComune;
    }

    public RichInputText getCodiceComune() {
        return codiceComune;
    }

    public void setDescComune(RichInputText descComune) {
        this.descComune = descComune;
    }

    public RichInputText getDescComune() {
        return descComune;
    }

    public void onDett(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Med_ElencoMediciView1Iterator");
        ViewObject voMed = voIter.getViewObject();
        Row cMed = voMed.getCurrentRow();
        Integer codMed = (Integer) cMed.getAttribute("Codiceregmedico");
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        Key rowKey = cMed.getKey();
        session.put("navMedDett", rowKey);
        DCIteratorBinding voDetIter = bindings.findIteratorBinding("Med_SoMedicoView1Iterator");
        ViewObject voDetMed = voDetIter.getViewObject();

        voDetMed.setWhereClause("CODICEREGMEDICO = " + codMed.toString() + " and ulss='" +
                               ulss + "'");

        voDetMed.executeQuery();
    }

    public String onNuovo() {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voDetIter = bindings.findIteratorBinding("Med_SoMedicoView1Iterator");
        ViewObject voDetMed = voDetIter.getViewObject();
        voDetMed.setWhereClause(null);
        OperationBinding createBinding = bindings.getOperationBinding("CreateInsert");
        createBinding.execute();   
        Row r = voDetIter.getCurrentRow();     
        String ulss = (String) session.get("ulss");
        r.setAttribute("Ulss", ulss); 
        String desAz = this.getDesAzienda(ulss, (Med_AppModule)voDetMed.getApplicationModule());
        r.setAttribute("Desaz",desAz);
        session.put("navMedDett", r.getKey());
        
        return "toDettMed";
    }
    
    private String getDesAzienda(String codAz, Med_AppModule am)
      {       
        String qry = "select descrizione from so_azienda where codaz = '" + codAz + "'";
            
        ViewObject voDec = am.createViewObjectFromQueryStmt("",qry);
        voDec.setRangeSize(-1);
        voDec.executeQuery();
        
        Row fAz = voDec.first();
        String desAz = (String) fAz.getAttribute(0);
        return desAz;    
       
      }

    public void reimposta(ActionEvent actionEvent) {
        BindingContext ctx = BindingContext.getCurrent();
        Med_RicParam bean = (Med_RicParam) ctx.findDataControl("Med_RicParamDataControl").getDataProvider();
        bean.reset();
    }

    public void cancella(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Med_ElencoMediciView1Iterator");
        ViewObject voMed = voIter.getViewObject();
        Row cMed = voMed.getCurrentRow();
        Integer codMed = (Integer) cMed.getAttribute("Codiceregmedico");
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        
        String qryAssist =
            "select count(*) from so_soggetto where codiceregmedico = " + codMed.toString() + " AND ULSS='" + ulss +
            "'";
        Med_AppModule am = (Med_AppModule)voMed.getApplicationModule();
        ViewObject voAss = am.createViewObjectFromQueryStmt("", qryAssist);
        voAss.setRangeSize(-1);
        voAss.executeQuery();
        Row cntAss = voAss.first();

        Number countAss = (Number) cntAss.getAttribute(0);

        if (countAss.intValue() > 0) {
            String msg = "Il medico selezionato ha assistiti. " + "Per questo motivo non e' possibile cancellarlo.";
            this.handleException(msg, null);
            return;
        }

        DCIteratorBinding voDetIter = bindings.findIteratorBinding("Med_SoMedicoView1Iterator");
        ViewObject voDetMed = voDetIter.getViewObject();

        voDetMed.setWhereClause("CODICEREGMEDICO = " + codMed.toString() + " and ulss='" + ulss + "'");

        voDetMed.executeQuery();
        Row r = voDetMed.first();
        
        if (r != null){       
            r.remove();           
            am.getTransaction().commit();
            
            ViewHelper.queryAndRestoreCurrentRow(voMed);
        }

    }

    public void resetParamStampa(ActionEvent actionEvent) {
        BindingContext ctx = BindingContext.getCurrent();
        Med_RicParam bean = (Med_RicParam) ctx.findDataControl("Med_RicParamDataControl").getDataProvider();
        bean.resetCampiStampa();
        
        PL_Bean templateBean = (PL_Bean) ctx.findDataControl("PL_BeanDataControl").getDataProvider();
        templateBean.setTemplate(null);
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Med_TemplateView1Iterator");
        ViewObject voTempl = voIter.getViewObject();
        String whcl = "IDTPLETTERA = " + 
              ConfigurationConstants.CODICE_ELENCHI_SOGG.toString() + 
              " and ULSS in ('" + ulss + "','"+AccessManager.CODREGIONALE+"') and TPSCR = '" + tpscr + "'";
        voTempl.setWhereClause(whcl);
        voTempl.executeQuery();
        
        DCIteratorBinding voMediciIter = bindings.findIteratorBinding("Med_ElencoMediciView1Iterator");
        session.put("navMedDett", voMediciIter.getCurrentRow().getKey());
    }

    public void setTabMedici(RichTable tabMedici) {
        this.tabMedici = tabMedici;
    }

    public RichTable getTabMedici() {
        return tabMedici;
    }
}
