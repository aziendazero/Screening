package view.accettazione.cito;

import java.util.Map;

import javax.faces.event.ActionEvent;

import model.accettazione.common.Acc_RicInvitiViewRow;
import model.accettazione.common.Acc_SoInvitoViewRow;

import model.common.Acc_AppModule;
import model.common.Acc_SoAnamnesiCitoViewRow;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.commons.AccUtils;
import view.commons.action.Parent_DataForwardAction;

import view.referto.GestoreReferti;

public class Acc_prelievoAction extends Parent_DataForwardAction {
    public Acc_prelievoAction() {
        super();
    }
    
    @Override
    protected void setAppModule() {
        this.amName = "Acc_AppModule";
    }

    private RichForm frm;

    public void setFrm(RichForm frm) {
        this.frm = frm;
    }

    public RichForm getFrm() {
        if (frm == null)
            findForward();
        return frm;
    }

    protected void findForward() {
    }    
    
    protected boolean beforeSave() {
        boolean esitoSuper = super.beforeSave();
        if (!esitoSuper)
            return false;

        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        boolean escl = ((Boolean) sess.get("soggEscluso")).booleanValue();
        if (escl) {
            String msg = "La persona risulta esclusa. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject voRic = voInvIter.getViewObject();
        Acc_AppModule am = (Acc_AppModule) voRic.getApplicationModule();

        ViewObject voAnam = am.findViewObject("Acc_SoAnamnesiCitoView1");
        Acc_SoAnamnesiCitoViewRow cAnam = (Acc_SoAnamnesiCitoViewRow) voAnam.getCurrentRow();

        if (cAnam != null) {
            Acc_RicInvitiViewRow cInvRic = (Acc_RicInvitiViewRow) voRic.getCurrentRow();
            Integer idInv = cInvRic.getIdinvito();
            ViewObject voInvito = am.findViewObject("Acc_SoInvitoView1");
            voInvito.setWhereClauseParams(new Object[] { idInv });
            voInvito.executeQuery();

            Acc_SoInvitoViewRow fInv = (Acc_SoInvitoViewRow) voInvito.first();
            voInvito.setCurrentRow(fInv);
            Date dataApp = fInv.getDtapp();

            cAnam.setDtanamnesi(dataApp);
        }

        ViewObject voAcc = am.findViewObject("Acc_SoAccCito1livView1");
        Row cRow = voAcc.getCurrentRow();
        Integer idInvito = (Integer) cRow.getAttribute("Idinvito");
        
        ViewObject voRef = am.findViewObject("Acc_SoRefertocito1livView1");
        voRef.setWhereClause("IDINVITO = " + idInvito.toString());
        voRef.executeQuery();
        
        GestoreReferti.updateVetrinoInRef(cRow,voRef);
        return true;
    }    
    
    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        if (dest.equals("acc_to")) {
            session.put("fromDett", Boolean.TRUE);
        } else {
            AccUtils.beforeNavAcc(dest, true);
        }
    }  
    
    protected void doRollback() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject vo = voInvIter.getViewObject();

        Acc_AppModule am = (Acc_AppModule) vo.getApplicationModule();
        String[] views = { "Acc_RicInvitiView1" };
        am.doRollback(views);
    }

    protected boolean pendingUpdatesOnSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject vo = voInvIter.getViewObject();

        Acc_AppModule am = (Acc_AppModule) vo.getApplicationModule();
        boolean dirty = am.getTransaction().isDirty();
        return dirty;
    }
    
    public void onAppl(@SuppressWarnings("unused") ActionEvent actionEvent) {
        @SuppressWarnings("unused")
        boolean saveOK = this.save();
    }

    public void onPrev(@SuppressWarnings("unused") ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.previous();
            AccUtils.processaInvitoCorrente(voRic);
        }
    }

    public void onNext(@SuppressWarnings("unused") ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.next();
            AccUtils.processaInvitoCorrente(voRic);
        }
    }    

    public String onRollback() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        Acc_AppModule am = (Acc_AppModule) voIter.getViewObject().getApplicationModule();
        am.doRollback("Acc_RicInvitiView1");

        ViewObject voRic = voIter.getViewObject();
        Row cInv = voRic.getCurrentRow();
        if (cInv != null)
            cInv.setAttribute("Selezionato", Boolean.TRUE);

        return "acc_to";
    }

    @SuppressWarnings("unchecked")
    public String onConf() {
        boolean saveOK = this.save();
        if (saveOK) {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            session.put("fromDett", Boolean.TRUE);
            return "acc_to";
        }
        return null;
    }
}
