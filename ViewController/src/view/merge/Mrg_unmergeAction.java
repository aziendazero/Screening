package view.merge;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import model.common.Sogg_AppModule;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import view.commons.ViewHelper;
import view.commons.action.Parent_DataForwardAction;

public class Mrg_unmergeAction extends Parent_DataForwardAction {

    private String unMpiVal;
    private String unCodtsVal;
    private String unCfVal;
    private String unMpiDepr;
    private String unCodtsDepr;
    private String unCfDepr;
    private String unMrgDal;
    private String unMrgAl;
    private boolean unMrgEffettuato;
    private RichForm unmergeForm;

    @Override
    protected void setAppModule() {
        this.amName="Sogg_AppModule";
    }

    public void setUnMpiVal(String unMpiVal) {
        this.unMpiVal = unMpiVal;
    }

    public String getUnMpiVal() {
        return unMpiVal;
    }

    public void setUnCodtsVal(String unCodtsVal) {
        this.unCodtsVal = unCodtsVal;
    }

    public String getUnCodtsVal() {
        return unCodtsVal;
    }

    public void setUnCfVal(String unCfVal) {
        this.unCfVal = unCfVal;
    }

    public String getUnCfVal() {
        return unCfVal;
    }

    public void setUnMpiDepr(String unMpiDepr) {
        this.unMpiDepr = unMpiDepr;
    }

    public String getUnMpiDepr() {
        return unMpiDepr;
    }

    public void setUnCodtsDepr(String unCodtsDepr) {
        this.unCodtsDepr = unCodtsDepr;
    }

    public String getUnCodtsDepr() {
        return unCodtsDepr;
    }

    public void setUnCfDepr(String unCfDepr) {
        this.unCfDepr = unCfDepr;
    }

    public String getUnCfDepr() {
        return unCfDepr;
    }

    public void setUnMrgDal(String unMrgDal) {
        this.unMrgDal = unMrgDal;
    }

    public String getUnMrgDal() {
        return unMrgDal;
    }

    public void setUnMrgAl(String unMrgAl) {
        this.unMrgAl = unMrgAl;
    }

    public String getUnMrgAl() {
        return unMrgAl;
    }

    public void setUnMrgEffettuato(boolean unMrgEffettuato) {
        this.unMrgEffettuato = unMrgEffettuato;
    }

    public boolean getUnMrgEffettuato() {
        return unMrgEffettuato;
    }

    public void cerca(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoMergeView1Iterator");
        ViewObject vo = voIter.getViewObject();

        //parametri minimi
        boolean paramOk = true;

        if (!paramOk) {
            this.handleException("Parametri di ricerca insufficienti", null);
        } else {
            // Eseguo la query
            String whcl = " Mrg_SoMerge.ULSS = '" + ulss + "'";

            if (this.getUnCfDepr() != null && !(this.getUnCfDepr().equals(""))) {
                whcl += " and Sogg_SoSoggetto1.CF = '" + this.getUnCfDepr() + "'";
            }

            if (this.getUnCfVal() != null && !(this.getUnCfVal().equals(""))) {
                whcl += " and Sogg_SoSoggetto.CF = '" + this.getUnCfVal() + "'";
            }

            if (this.getUnCodtsDepr() != null &&
                !(this.getUnCodtsDepr().equals(""))) { //tessera
                whcl +=
                    " and Sogg_SoSoggetto1.CODTS = FUN_DOC_TROVA_CODTS('TS','" + this.getUnCodtsDepr() + "','" + ulss +
                    "') ";
            }

            if (this.getUnCodtsVal() != null &&
                !(this.getUnCodtsVal().equals(""))) { //tessera
                whcl +=
                    " and Sogg_SoSoggetto.CODTS = FUN_DOC_TROVA_CODTS('TS','" + this.getUnCodtsVal() + "','" + ulss +
                    "') ";
            }

            if (this.getUnMpiDepr() != null && !(this.getUnMpiDepr().equals(""))) {
                whcl += " and Sogg_SoSoggetto1.IDINTERNO = '" + this.getUnMpiDepr() + "'";
            }

            if (this.getUnMpiVal() != null && !(this.getUnMpiVal().equals(""))) {
                whcl += " and Sogg_SoSoggetto.IDINTERNO = '" + this.getUnMpiVal() + "'";
            }

            if (this.getUnMrgDal() != null && !(this.getUnMrgDal().equals(""))) {
                whcl +=
                    " and Mrg_SoMerge.DTINSERT >= TO_DATE('" + this.getUnMrgDal() + "','" + DateUtils.DATE_PATTERN +
                    "') ";
            }

            if (this.getUnMrgAl() != null && !(this.getUnMrgAl().equals(""))) {
                whcl +=
                    " and Mrg_SoMerge.DTINSERT <= TO_DATE('" + this.getUnMrgAl() + "','" + DateUtils.DATE_PATTERN +
                    "') + 1 ";
            }

            if (!this.getUnMrgEffettuato()) {
                whcl += " and Mrg_SoMerge.UNMERGE = 0";
            }

            vo.setWhereClause(whcl);
            vo.setOrderByClause("Mrg_SoMerge.DTINSERT DESC");
            vo.executeQuery();

        }
    }

    public void reimposta(ActionEvent actionEvent) {
        resetCampiUnmerge();  
    }

    public void setUnmergeForm(RichForm unmergeForm) {
        this.unmergeForm = unmergeForm;
    }

    public RichForm getUnmergeForm() {
        if (unmergeForm == null)
            try {
                findForward();
            } catch (Exception e) {
                this.handleException(e.getMessage(), null);
            }
        return unmergeForm;
    }

    protected void findForward() throws Exception {
        resetCampiUnmerge();

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        Map request = ADFContext.getCurrent().getPageFlowScope();
        String codtsVal = (String) request.get("merge_codts_val");
        String codtsDepr = (String) request.get("merge_codts_depr");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoMergeView1Iterator");
        ViewObject vo = voIter.getViewObject();
        if (codtsVal != null && codtsDepr != null) {
            this.setUnCodtsDepr(codtsDepr);
            this.setUnCodtsVal(codtsVal);
            String whcl =
                " Mrg_SoMerge.ULSS = '" + ulss + "' AND Mrg_SoMerge.VAL_CODTS = '" + codtsVal +
                "' AND Mrg_SoMerge.DEP_CODTS = '" + codtsDepr + "'";
            vo.setWhereClause(whcl);
        } else {
            vo.setWhereClause("1=2");
        }
        vo.executeQuery();
    }

    private void resetCampiUnmerge() {
        this.setUnCfDepr(null);
        this.setUnCfVal(null);
        this.setUnCodtsDepr(null);
        this.setUnCodtsVal(null);
        this.setUnMpiDepr(null);
        this.setUnMpiVal(null);
        this.setUnMrgAl(null);
        this.setUnMrgDal(null);
        this.setUnMrgEffettuato(false);
    }

    public void unmerge(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoMergeView1Iterator");
        ViewObject mergeVo = voIter.getViewObject();
        Row merge = mergeVo.getCurrentRow();

        //chiamo la procedura di merge
        String codtsValido = (String) merge.getAttribute("ValCodts");
        String codtsDeprecato = (String) merge.getAttribute("DepCodts");
        Integer idMerge = (Integer) merge.getAttribute("Idmrg");

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String user = (String) session.get("user");
        Sogg_AppModule am = (Sogg_AppModule)mergeVo.getApplicationModule();
        String message = am.callUnmergeAnagrafico(ulss, codtsValido, codtsDeprecato, user, idMerge);

        if (message != null) {
            this.handleException(message, null);
        } else {
            this.handleMessages(FacesMessage.SEVERITY_INFO,"Unmerge anagrafico eseguito con successo. Modificata la situazione di screening del soggetto a seguito di merge/unmerge, si consiglia verifica.");
            // rifaccio la ricerca del merge in modo che risulti evidente l'annullamento
            ViewHelper.queryAndRestoreCurrentRow(mergeVo);
        }
    }
}
