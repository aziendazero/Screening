package view.accettazione.colon;

import insiel.dataHandling.DateUtils;

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

import model.common.AccCo_AppModule;
import model.common.AccCo_SoAnamnesicolonViewRow;

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

import view.util.Utility;

public class AccCo_anamAction extends Parent_DataForwardAction {
    private RichForm frm;
    private AccCo_ricercaAction accCo_ricercaAction;

    public void setAccCo_ricercaAction(AccCo_ricercaAction accCo_ricercaAction) {
        this.accCo_ricercaAction = accCo_ricercaAction;
    }

    public AccCo_ricercaAction getAccCo_ricercaAction() {
        return accCo_ricercaAction;
    }

    @Override
    protected void setAppModule() {
        this.amName = "AccCo_AppModule";
    }

    protected boolean beforeSave() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        boolean escl = ((Boolean) sess.get("soggEscluso")).booleanValue();
        if (escl) {
            String msg = "La persona risulta esclusa. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }
        return true;
    }

    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        if (dest.equals("acc_toColon")) {
            session.put("fromDett", Boolean.TRUE);
        } else {
            AccUtils.beforeNavAccCo(dest, true);
        }
    }

    public void prevInvitoCo(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.previous();
            AccUtils.procInvCorrenteColon(voRic);
            procCurrentAnam((AccCo_AppModule) voRic.getApplicationModule());
        }
    }

    public void nextInvitoCo(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.next();
            AccUtils.procInvCorrenteColon(voRic);
            procCurrentAnam((AccCo_AppModule) voRic.getApplicationModule());
        }
    }

    public void stampaScheda(FacesContext facesContext, OutputStream outputStream) throws IOException, Exception,
                                                                                          FileNotFoundException {

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
            id = (Integer) ADFContext.getCurrent().getRequestScope().get("IdAnamco");
        else
            id = (Integer) ADFContext.getCurrent().getRequestScope().get("Idinvito");


        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
        AccCo_AppModule am = (AccCo_AppModule) voIter.getViewObject().getApplicationModule();
        ViewObject ulss_vo = am.findViewObject("AccCo_SoAziendaView1");
        ViewObject param_vo = am.findViewObject("AccCo_SoCnfPartemplateView1");
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        File pdf = null;
        HashMap h = null;
        try {
            SystemReport report = null;
            if (!_empty)
                report = new SystemReport(SystemReport.SCHEDA_ANAM_COLON);
            else
                report = new SystemReport(SystemReport.SCHEDA_ANAM_COLON_VUOTA);

            h = ParametriSistema.getParamTemplate(ulss, tpscr, ulss_vo, param_vo);
            h.put(((!_empty) ? "idanamco" : "idinvito"), Double.valueOf(String.valueOf(id)));

            File f1 = ImageLoader.getImage("flagsi.jpg");
            h.put("flagsi", f1);
            File f2 = ImageLoader.getImage("flagno.jpg");
            h.put("flagno", f2);

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

    public String onConf() {
        boolean saveOK = this.save();
        if (saveOK) {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            session.put("fromDett", Boolean.TRUE);
            return "acc_toColon";
        }
        return null;
    }

    public String onAnnulla() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
        ViewObject voRic = voIter.getViewObject();
        AccCo_AppModule am = (AccCo_AppModule) voRic.getApplicationModule();
        am.doRollback("AccCo_RicInvitiView1");

        Row cInv = voRic.getCurrentRow();
        if (cInv != null)
            cInv.setAttribute("Selezionato", Boolean.TRUE);

        return "acc_toColon";
    }

    public void onAppl(ActionEvent actionEvent) {
        boolean saveOK = this.save();
    }

    private void procCurrentAnam(AccCo_AppModule am) {
        findForward();
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:Idopanamnesi'])");
    }

    public void onChanamfam(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_SoAnamnesicolonView1Iterator");
        ViewObject voAnam = voIter.getViewObject();
        AccCo_AppModule am = (AccCo_AppModule) voAnam.getApplicationModule();
        ViewObject voSoggScr = am.findViewObject("AccCo_SoSoggScrView1");
        Row cSogg = voSoggScr.getCurrentRow();

        AccCo_SoAnamnesicolonViewRow cAnam = (AccCo_SoAnamnesicolonViewRow) voAnam.getCurrentRow();
        Integer esitoAnam = cAnam.getIdesitoaf();
        String ulss = cAnam.getUlss();
        String tpscr = cAnam.getTpscr();
        String qry =
            "SELECT altorischio FROM so_cnf_esitoanamfam where IDESITOAF=" + esitoAnam.toString() + " and ulss = '" +
            ulss + "' and tpscr = '" + tpscr + "'";
        ViewObject voQry = am.createViewObjectFromQueryStmt("", qry);
        voQry.setRangeSize(-1);
        voQry.executeQuery();
        Row rRow = voQry.first();
        oracle.jbo.domain.Number altoR = (oracle.jbo.domain.Number) rRow.getAttribute(0);

        if (altoR.intValue() > 0) {
            cSogg.setAttribute("Altorischio", 1);
        } else {
            cSogg.setAttribute("Altorischio", 0);
        }
    }

    public void onChpc(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_SoAnamnesicolonView1Iterator");
            ViewObject voAnam = voIter.getViewObject();
            AccCo_SoAnamnesicolonViewRow cAnam = (AccCo_SoAnamnesicolonViewRow) voAnam.getCurrentRow();

            cAnam.setPcDtintervento(null);
            cAnam.setPcIntnoncod(null);
            cAnam.setPcNota(null);
        }
    }

    public void onChac(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_SoAnamnesicolonView1Iterator");
            ViewObject voAnam = voIter.getViewObject();
            AccCo_SoAnamnesicolonViewRow cAnam = (AccCo_SoAnamnesicolonViewRow) voAnam.getCurrentRow();

            cAnam.setAcDtsosp(null);
            cAnam.setAcNote(null);
        }
    }

    public void onChpa(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_SoAnamnesicolonView1Iterator");
            ViewObject voAnam = voIter.getViewObject();
            AccCo_SoAnamnesicolonViewRow cAnam = (AccCo_SoAnamnesicolonViewRow) voAnam.getCurrentRow();

            cAnam.setPaDatainizio(null);
            cAnam.setPaNote(null);
        }
    }

    public void onChrc(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_SoAnamnesicolonView1Iterator");
            ViewObject voAnam = voIter.getViewObject();
            AccCo_SoAnamnesicolonViewRow cAnam = (AccCo_SoAnamnesicolonViewRow) voAnam.getCurrentRow();

            cAnam.setRcNote(null);
        }
    }


    public void onChal(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_SoAnamnesicolonView1Iterator");
            ViewObject voAnam = voIter.getViewObject();
            AccCo_SoAnamnesicolonViewRow cAnam = (AccCo_SoAnamnesicolonViewRow) voAnam.getCurrentRow();

            cAnam.setAlNote(null);
        }
    }

    public void setFrm(RichForm frm) {
        this.frm = frm;
    }

    public RichForm getFrm() {
        if (frm == null)
            findForward();
        return frm;
    }

    protected void findForward() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        accCo_ricercaAction.initAnam(bindings);
    }

    protected boolean pendingUpdatesOnSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_SoAnamnesicolonView1Iterator");
        ViewObject voAnam = voIter.getViewObject();
        AccCo_AppModule am = (AccCo_AppModule) voAnam.getApplicationModule();

        boolean dirty = am.getTransaction().isDirty();
        return dirty;
    }
}
