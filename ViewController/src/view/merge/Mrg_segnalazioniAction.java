package view.merge;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import model.common.Sogg_AppModule;

import model.commons.SchedUtils;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

public class Mrg_segnalazioniAction extends Parent_DataForwardAction {
    private boolean sgMerge = true;
    private boolean sgUnmerge = true;
    private String sgMpiVal;
    private String sgMpiDepr;
    private String sgEvase;
    private String sgDal;
    private String sgCodtsVal;
    private String sgCodtsDepr;
    private boolean sgArchiviate;
    private String sgAl;
    private String sgCfVal;
    private String sgCfDepr;
    private RichForm segnalazioniForm;
    private String currentDate;

    public Mrg_segnalazioniAction() {
        super();
    }

    @Override
    protected void setAppModule() {
        this.amName="Sogg_AppModule";
    }

    public void setSgMerge(boolean sgMerge) {
        this.sgMerge = sgMerge;
    }

    public boolean getSgMerge() {
        return sgMerge;
    }

    public void setSgUnmerge(boolean sgUnmerge) {
        this.sgUnmerge = sgUnmerge;
    }

    public boolean getSgUnmerge() {
        return sgUnmerge;
    }

    public void setSgMpiVal(String sgMpiVal) {
        this.sgMpiVal = sgMpiVal;
    }

    public String getSgMpiVal() {
        return sgMpiVal;
    }

    public void setSgMpiDepr(String sgMpiDepr) {
        this.sgMpiDepr = sgMpiDepr;
    }

    public String getSgMpiDepr() {
        return sgMpiDepr;
    }

    public void setSgEvase(String sgEvase) {
        this.sgEvase = sgEvase;
    }

    public String getSgEvase() {
        return sgEvase;
    }

    public void setSgDal(String sgDal) {
        this.sgDal = sgDal;
    }

    public String getSgDal() {
        if (sgDal == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -10);
            sgDal = DateUtils.dateToString(cal);
        }
        return sgDal;
    }

    public void setSgCodtsVal(String sgCodtsVal) {
        this.sgCodtsVal = sgCodtsVal;
    }

    public String getSgCodtsVal() {
        return sgCodtsVal;
    }

    public void setSgCodtsDepr(String sgCodtsDepr) {
        this.sgCodtsDepr = sgCodtsDepr;
    }

    public String getSgCodtsDepr() {
        return sgCodtsDepr;
    }

    public void setSgArchiviate(boolean sgArchiviate) {
        this.sgArchiviate = sgArchiviate;
    }

    public boolean getSgArchiviate() {
        return sgArchiviate;
    }

    public void setSgAl(String sgAl) {
        this.sgAl = sgAl;
    }

    public String getSgAl() {
        if (sgAl == null){
            sgAl = DateUtils.dateToString(new Date());
        }
        return sgAl;
    }

    public void setSgCfVal(String sgCfVal) {
        this.sgCfVal = sgCfVal;
    }

    public String getSgCfVal() {
        return sgCfVal;
    }

    public void setSgCfDepr(String sgCfDepr) {
        this.sgCfDepr = sgCfDepr;
    }

    public String getSgCfDepr() {
        return sgCfDepr;
    }

    public void cerca(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoMergeProposteView1Iterator");
        ViewObject vo = voIter.getViewObject();

        // Eseguo la query
        String whcl = " Mrg_SoMergeProposte.ULSS = '" + ulss + "'";

        if (this.getSgAl() != null && !(this.getSgAl().equals(""))) {
            whcl +=
                " and TRUNC(Mrg_SoMergeProposte.DTINSERT) <= TO_DATE('" + this.getSgAl() + "','" +
                DateUtils.DATE_PATTERN + "') ";
        }
        if (this.getSgArchiviate()) {
            whcl += " and Mrg_SoMergeProposte.ARCHIVIATO = 1";
        } else {
            whcl += " and Mrg_SoMergeProposte.ARCHIVIATO = 0";
        }
        if (this.getSgCfDepr() != null && !(this.getSgCfDepr().equals(""))) {
            whcl += " and Sogg_SoSoggetto1.CF = '" + this.getSgCfDepr() + "'";
        }
        if (this.getSgCfVal() != null && !(this.getSgCfVal().equals(""))) {
            whcl += " and Sogg_SoSoggetto.CF = '" + this.getSgCfVal() + "'";
        }
        if (this.getSgCodtsDepr() != null &&
            !(this.getSgCodtsDepr().equals(""))) { //tessera
            whcl +=
                " and Sogg_SoSoggetto1.CODTS = FUN_DOC_TROVA_CODTS('TS','" + this.getSgCodtsDepr() + "','" + ulss +
                "') ";
        }
        if (this.getSgCodtsVal() != null &&
            !(this.getSgCodtsVal().equals(""))) { //tessera
            whcl +=
                " and Sogg_SoSoggetto.CODTS = FUN_DOC_TROVA_CODTS('TS','" + this.getSgCodtsVal() + "','" + ulss + "') ";
        }
        if (this.getSgDal() != null && !(this.getSgDal().equals(""))) {
            whcl +=
                " and TRUNC(Mrg_SoMergeProposte.DTINSERT) >= TO_DATE('" + this.getSgDal() + "','" +
                DateUtils.DATE_PATTERN + "') ";
        }
        if (this.getSgMpiDepr() != null && !(this.getSgMpiDepr().equals(""))) {
            whcl += " and Sogg_SoSoggetto1.IDINTERNO = '" + this.getSgMpiDepr() + "'";
        }
        if (this.getSgMpiVal() != null && !(this.getSgMpiVal().equals(""))) {
            whcl += " and Sogg_SoSoggetto.IDINTERNO = '" + this.getSgMpiVal() + "'";
        }
        if (this.getSgMerge() && !this.getSgUnmerge()) {
            whcl += " and Mrg_SoMergeProposte.TIPO = 'M'";
        } else if (!this.getSgMerge() && this.getSgUnmerge()) {
            whcl += " and Mrg_SoMergeProposte.TIPO = 'U'";
        }
        if (this.getSgEvase() != null && !this.getSgEvase().isEmpty()) {
            if ("SI".equals(this.getSgEvase())) {
                whcl += " and Mrg_SoMergeProposte.EVASIONE = '1'";
            } else {
                whcl += " and Mrg_SoMergeProposte.EVASIONE = '0'";
            }
        }

        vo.setWhereClause(whcl);
        vo.executeQuery();
    }

    public void setSegnalazioniForm(RichForm segnalazioniForm) {
        this.segnalazioniForm = segnalazioniForm;
    }

    public RichForm getSegnalazioniForm() {
        if (segnalazioniForm == null)
            try {
                findForward();
            } catch (Exception e) {
                this.handleException(e.getMessage(), null);
            }
        return segnalazioniForm;
    }
    
    protected void findForward() throws Exception {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoMergeProposteView1Iterator");
        ViewObject vo = voIter.getViewObject();
        vo.setWhereClause("1=2");
        vo.executeQuery();

    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentDate() {
        return DateUtils.dateToString(new Date());
    }

    public void archivia(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String) session.get("user");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoMergeProposteView1Iterator");
        ViewObject vo = voIter.getViewObject();

        Row proposta = vo.getCurrentRow();
        proposta.setAttribute("Oparchiv", user);
        proposta.setAttribute("Dtarchiv", DateUtils.getOracleDateNow());
        proposta.setAttribute("Archiviato", new Integer(1));

        OperationBinding commit = bindings.getOperationBinding("Commit");
        commit.execute();
        
        AdfFacesContext adfFC = AdfFacesContext.getCurrentInstance();
        adfFC.returnFromDialog(null, null);
    }

    public void rielabora(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoMergeProposteView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Sogg_AppModule am = (Sogg_AppModule)vo.getApplicationModule();
        Row proposta = vo.getCurrentRow();

        String tipo = (String) proposta.getAttribute("Tipo");
        if ("M".equals(tipo)) {

            String updProposta =
                "UPDATE SO_ELABORAZIONE_ANAG SET END_ELAB = null " + "WHERE ID_MSG_FK = " +
                proposta.getAttribute("IdMsgFk");

            am.getTransaction().executeCommand(updProposta);

            am.getTransaction().commit();
            Map session = ADFContext.getCurrent().getSessionScope();
            String user = (String) session.get("user");
            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");

            int PID = SchedUtils.scheduleProcess(am, "ANA", ulss, tpscr, user);

            this.handleMessages(FacesMessage.SEVERITY_INFO,
                                "Rielaborazione correttamente schedulata. L'operazione di merge verra' tentata al prossimo import anagrafico.");
        }
    }

    public String goScheda() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoMergeProposteView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row proposta = vo.getCurrentRow();
        try {

            String tipoProposta = (String) proposta.getAttribute("Tipo");
            if ("M".equals(tipoProposta)) {
                return "mrg_toMerge";
            } else {
                return "mrg_toUnmerge";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
