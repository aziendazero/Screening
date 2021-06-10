package view.accettazione.cito;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.Connection;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.accettazione.common.Acc_RicInvitiViewRow;
import model.accettazione.common.Acc_SoInvitoViewRow;

import model.common.Acc_AppModule;
import model.common.Acc_SoAnamnesiCitoViewRow;

import model.commons.ParametriSistema;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.commons.AccUtils;
import view.commons.action.Parent_DataForwardAction;

import view.reports.ImageLoader;
import view.reports.SystemReport;

public class Acc_anamAction extends Parent_DataForwardAction {
    public Acc_anamAction() {
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

        return true;

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

    @SuppressWarnings({ "unchecked", "oracle.jdeveloper.java.nested-assignment" })
    public void stampaScheda(@SuppressWarnings("unused") FacesContext facesContext,
                             OutputStream outputStream) throws IOException, Exception, FileNotFoundException {

        boolean _empty = true;

        try {
            _empty = (Boolean) ADFContext.getCurrent().getRequestScope().get("schedaEmpty");
        } catch (Throwable th) {
            th.printStackTrace();
        }

        if (!_empty)
            //18112011 gaion: salvo prima di stampare
            this.save();
        //fine 18112011

        // TODO:  Override this oracle.adf.controller.struts.actions.DataAction method
        Integer id = null;

        if (!_empty)
            id = (Integer) ADFContext.getCurrent().getRequestScope().get("idAnam");
        else
            id = (Integer) ADFContext.getCurrent().getRequestScope().get("Idinvito");


        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        Acc_AppModule am = (Acc_AppModule) voIter.getViewObject().getApplicationModule();
        ViewObject ulss_vo = am.findViewObject("Acc_SoAziendaView1");
        ViewObject param_vo = am.findViewObject("Acc_SoCnfPartemplateView1");
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        File pdf = null;
        HashMap h = null;
        try {
            SystemReport report = null;
            if (!_empty)
                report = new SystemReport(SystemReport.SCHEDA_ANAM_CITO);
            else
                report = new SystemReport(SystemReport.SCHEDA_ANAM_CITO_VUOTA);

            h = ParametriSistema.getParamTemplate(ulss, tpscr, ulss_vo, param_vo);
            h.put(((!_empty) ? "idanamci" : "idinvito"), Double.valueOf(String.valueOf(id)));

            File f1 = ImageLoader.getImage("flagsi.jpg");
            h.put("flagsi", f1);
            File f2 = ImageLoader.getImage("flagno.jpg");
            h.put("flagno", f2);

            File fQ = ImageLoader.getImage("quadrantiseno.GIF");
            h.put("qseno", fQ);

            Connection conn = am.getDBConnection();
            pdf = report.getPDFReport(conn, h);

            FileInputStream is = new FileInputStream(pdf);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (pdf != null)
                pdf.delete();
            ParametriSistema.releaseLogo(h);
        }
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

    public void onAppl(@SuppressWarnings("unused") ActionEvent actionEvent) {
        @SuppressWarnings("unused")
        boolean saveOK = this.save();
    }

    public void onChGrav(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voAnam = bindings.findIteratorBinding("Acc_SoAnamnesiCitoView1Iterator");

        Acc_SoAnamnesiCitoViewRow cAnam = (Acc_SoAnamnesiCitoViewRow) voAnam.getCurrentRow();

        cAnam.setMeseGravidanza(null);
    }

    public void onChMeno(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voAnam = bindings.findIteratorBinding("Acc_SoAnamnesiCitoView1Iterator");

        Acc_SoAnamnesiCitoViewRow cAnam = (Acc_SoAnamnesiCitoViewRow) voAnam.getCurrentRow();

        cAnam.setDtUltMestr(null);
    }
}
