package view.print;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.charset.Charset;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;


import javax.faces.event.ActionEvent;

import javax.faces.event.ActionListener;

import model.common.Parent_AppModule;

import model.common.RefMa_AppModule;

import model.commons.ParametriSistema;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.nav.RichButton;

import oracle.adf.view.rich.event.BasePolytypeListener;
import oracle.adf.view.rich.event.SetPropertyListener;

import oracle.adfinternal.view.faces.event.rich.FileDownloadActionListener;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

import view.reports.ImageLoader;
import view.reports.SystemReport;

import view.util.Utility;

public class Print_anamnesiAction extends Parent_DataForwardAction {

    private RichButton btnStampaHidden;

    public Map<String, Object> stampaPDF() {

        Map requestScope = ADFContext.getCurrent().getRequestScope();
        Integer idInvito = (Integer) requestScope.get("id_invito");
        Integer livello = (Integer) requestScope.get("livello_invito");
        String codts = (String) requestScope.get("codts_invito");
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        File pdf = null;
        HashMap h = null;
        ViewObject tempVo = null;
        List idanam = new ArrayList();
        ViewObject ulss_vo = null;
        ViewObject param_vo = null;
        int reportType = SystemReport.SCHEDA_ANAM_MX;
        String paramname = "";
        SystemReport report = null;
        ApplicationModule am = null;

        OutputStream outputStream = new ByteArrayOutputStream();
        Map<String, Object> _ret = new HashMap<String, Object>();

        try {

            if (tpscr.equals("MA")) {
                DCBindingContainer bindings =
                    (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
                DCIteratorBinding ulss_voIter = bindings.findIteratorBinding("A_SoAziendaView1Iterator");
                ulss_vo = ulss_voIter.getViewObject();
                DCIteratorBinding param_voIter = bindings.findIteratorBinding("A_SoCnfPartemplateView1Iterator");
                param_vo = param_voIter.getViewObject();
                paramname = "idanamma";
                am = (RefMa_AppModule) param_vo.getApplicationModule();
                //la query cambia a seconda del livello dell'invito
                String query =
                    "SELECT C.id_anamma FROM SO_ACC_mammo" + livello + "LIV A, SO_ANAMNESImx C " + "WHERE A.idaccma_" +
                    livello + "liv=C.idaccma_" + livello + "liv " + "AND C.ulss = " + ulss + " AND A.idinvito=" +
                    idInvito;
                tempVo = am.createViewObjectFromQueryStmt("tempVo", query);
                tempVo.executeQuery();
                Row r = tempVo.first();
                if (r != null)
                    idanam.add(r.getAttribute(0).toString());
                else // cerco l'anamnesi piu' recente
                {
                    tempVo.remove();
                    String query_old =
                        "SELECT c.id_anamma FROM  SO_ANAMNESImx C " + "WHERE c.ulss = " + ulss + " and c.tpscr = '" +
                        tpscr + "' " + " and c.codts = " + codts + " order by c.dtanamnesi desc ";
                    tempVo = am.createViewObjectFromQueryStmt("tempVo", query_old);
                    tempVo.executeQuery();
                    Row r2 = tempVo.first();
                    if (r2 != null)
                        idanam.add(r2.getAttribute(0).toString());
                }
                tempVo.remove();

            }
            if (idanam != null && idanam.size() > 0) {
                report = new SystemReport(reportType);
                h = ParametriSistema.getParamTemplate(ulss, tpscr, ulss_vo, param_vo);

                File f1 = ImageLoader.getImage("flagsi.jpg");
                h.put("flagsi", f1);
                File f2 = ImageLoader.getImage("flagno.jpg");
                h.put("flagno", f2);

                if (tpscr.equals("MA")) {
                    File fQ = ImageLoader.getImage("quadrantiseno.GIF");
                    h.put("qseno", fQ);
                }


                Connection conn = ((Parent_AppModule) am).getDBConnection();
                pdf = report.getBatchPDFReport(conn, h, idanam, paramname);

                this.outputPrintServlet(outputStream, pdf);
                _ret.put("fileName", "SchedaAnam_.pdf");
                _ret.put("contentType", "application/pdf");
                _ret.put("outputStream", outputStream);


            } else {
                throw new Exception("Per il paziente in oggetto l'anamnesi non e' MAI stata rilevata");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.saveMessages("Impossibile stampare l' anamnesi: " + e.getMessage());

        } finally {
            if (pdf != null)
                pdf.delete();
            try {
                if (tempVo != null)
                    tempVo.remove();
            } catch (Exception e) {
                //non serve fare niente
            }
            ParametriSistema.releaseLogo(h);
        }

        return _ret;
    }

    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }

    public void setBtnStampaHidden(RichButton btnStampaHidden) {
        this.btnStampaHidden = btnStampaHidden;
    }

    public RichButton getBtnStampaHidden() {
        return btnStampaHidden;
    }

    public void downloadListener(ActionEvent actionEvent) {
        Map<String, Object> resp = stampaPDF();

        if (resp.isEmpty() || !resp.containsKey("outputStream"))
            handleException("Errore nel recupero del file! Impossibile stampare l' anamnesi.");
        else {
            FileDownloadActionListener fileDownloadActionListener = new FileDownloadActionListener();
            FacesContext fctx = FacesContext.getCurrentInstance();
            ELContext elctx = fctx.getELContext();
            Application application = fctx.getApplication();
            ExpressionFactory exprFactory = application.getExpressionFactory();
            MethodExpression methodExpr = null;
            methodExpr =
                exprFactory.createMethodExpression(elctx, "#{Print_anamnesiAction.print}", null,
                                                   new Class[] { FacesContext.class, OutputStream.class });
            fileDownloadActionListener.setMethod(methodExpr);
            fileDownloadActionListener.setFilename((String) resp.get("fileName"));
            fileDownloadActionListener.setContentType((String) resp.get("contentType"));

            SetPropertyListener setPropertyListener = new SetPropertyListener(BasePolytypeListener.EventType.ACTION);
            ValueExpression valueExpression =
                exprFactory.createValueExpression(elctx, "#{requestScope.outputStream}", OutputStream.class);
            setPropertyListener.setValueExpression("to", valueExpression);

            setPropertyListener.setFrom(resp.get("outputStream"));

            for (ActionListener list : btnStampaHidden.getActionListeners()) {
                btnStampaHidden.removeActionListener(list);
            }
            btnStampaHidden.addActionListener(setPropertyListener);
            btnStampaHidden.addActionListener(fileDownloadActionListener);

            String id = (String) ADFContext.getCurrent().getRequestScope().get("btnHiddenId");
            Utility.addScriptOnPartialRequest("customHandler('" + id + "')");
        }
    }

    public void print(FacesContext facesContext, OutputStream outputStream) {
        try {
            ByteArrayOutputStream by =
                (ByteArrayOutputStream) ADFContext.getCurrent().getRequestScope().get("outputStream");
            by.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected void outputPrintServlet(OutputStream output, File f) throws IOException {
        //17102013 S.Gaion : tolto spoolx per incompatibilita con il CAS
        // lo zip viene scaricato direttamente
        InputStream input = new FileInputStream(f);

        try {
            while (input.available() > 0) {
                output.write(input.read());
            }

        } finally {
            input.close();
            output.flush();
            output.close();
            if (f != null)
                f.delete();
        }
    }
}
