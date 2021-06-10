package view.print;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import model.common.Acc_AppModule;

import model.common.AccCo_AppModule;
import model.common.AccMa_AppModule;
import model.common.Parent_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.ViewHelper;

import model.datacontrol.ElencoAnamnesi;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.nav.RichButton;
import oracle.adf.view.rich.event.BasePolytypeListener;
import oracle.adf.view.rich.event.SetPropertyListener;

import oracle.adfinternal.view.faces.event.rich.FileDownloadActionListener;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

import view.reports.ImageLoader;
import view.reports.SystemReport;

import view.util.Utility;

public class Print_elencoAnamStampaAction extends Parent_DataForwardAction {
    private RichButton btnStampaHidden;

    public void setBtnStampaHidden(RichButton btnStampaHidden) {
        this.btnStampaHidden = btnStampaHidden;
    }

    public RichButton getBtnStampaHidden() {
        return btnStampaHidden;
    }

    public Print_elencoAnamStampaAction() {
    }

    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }

    @SuppressWarnings({ "unchecked", "oracle.jdeveloper.java.semantic-warning", "deprecation",
                      "oracle.jdeveloper.java.insufficient-catch-block" })
    private Map<String, Object> print() {
        @SuppressWarnings("deprecation")
        ElencoAnamnesi bean =
            (ElencoAnamnesi) BindingContext.getCurrent().findDataControl("ElencoAnamnesiDataControl").getDataProvider();
        String tipo = bean.getExport_type();
        if (tipo == null)
            tipo = "Compilate";

        Map<String, Object> _ret = new HashMap<String, Object>();
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        File pdf = null;
        File zip = null;
        ViewObject tempVo = null;
        RowSetIterator rsiAnam = null;
        SystemReport report = null;
        String paramname = "";
        int reportType = -1;
        ViewObject ulss_vo = null;
        ViewObject param_vo = null;
        ApplicationModule am = null;
        List idanam = null;
        HashMap h = null;
        try {

            if (tpscr.equals("CI")) {
                am =
                    (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
                ulss_vo = am.findViewObject("Acc_SoAziendaView1");
                param_vo = am.findViewObject("Acc_SoCnfPartemplateView1");
                ViewObject voInvito = am.findViewObject("Acc_RicInvitiView1");

                rsiAnam = ViewHelper.getRowSetIterator(voInvito, "iter_accanam");
                idanam = new ArrayList();

                while (rsiAnam.hasNext()) {
                    Row invitoRow = rsiAnam.next();
                    //se l'esito e' S estraggo l'invito sia per le anamnesi vuote che per quelle compilate
                    if (invitoRow.getAttribute("Codesitoinvito").toString().equals(ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO) ||
                        //se invece l'esito e' ? estraggo solo le anamnesi vuote
                        (tipo.equals("Vuote") &&
                         invitoRow.getAttribute("Codesitoinvito").toString().equals(ConfigurationConstants.CODICE_ESITO_NON_DISPONIBILE))) {
                        //se devo stampare anamnesi vuote mi bastano gli id degli inviti
                        if (tipo.equals("Vuote")) {
                            idanam.add(invitoRow.getAttribute("Idinvito").toString());
                        } else {
                            //stamp anamneis compilate, lavoro sull'id ell'anamnesi
                            //la query cambia a seconda del livello dell'invito
                            String livello = invitoRow.getAttribute("Livello").toString();
                            String query =
                                "SELECT C.id_anamci FROM SO_ACC_CITO" + livello + "LIV A, SO_ANAMNESI_CITO C " +
                                "WHERE A.idacc" + livello + "liv=C.idacc" + livello + "liv AND A.idinvito=" +
                                invitoRow.getAttribute("Idinvito").toString();
                            tempVo = am.createViewObjectFromQueryStmt("tempVo", query);
                            tempVo.executeQuery();
                            Row r = tempVo.first();
                            if (r != null)
                                idanam.add(r.getAttribute(0).toString());
                            tempVo.remove();
                        }
                    }
                    //gli inviti con altri esiti non li estraggo mai
                }
                rsiAnam.closeRowSetIterator();


                if (tipo.equals("Vuote")) { //vuote
                    reportType = SystemReport.SCHEDA_ANAM_CITO_VUOTA;
                    paramname = "idinvito";
                } else {
                    //compilate
                    reportType = SystemReport.SCHEDA_ANAM_CITO;
                    paramname = "idanamci";
                }


            }
            if (tpscr.equals("CO")) {
                am =
                    (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
                ulss_vo = am.findViewObject("AccCo_SoAziendaView1");
                param_vo = am.findViewObject("AccCo_SoCnfPartemplateView1");
                ViewObject voInvito = am.findViewObject("AccCo_RicInvitiView1");

                rsiAnam = ViewHelper.getRowSetIterator(voInvito, "iter_accanam");
                idanam = new ArrayList();

                while (rsiAnam.hasNext()) {
                    Row invitoRow = rsiAnam.next();
                    //nel colon l'anamnesi si fa solo al 2 livello, quindi per i primi non c'e'
                    if (invitoRow.getAttribute("Livello").toString().equals("1"))
                        continue;

                    //se l'esito e' S estraggo l'invito sia per le anamnesi vuote che per quelle compilate
                    if (invitoRow.getAttribute("Codesitoinvito").toString().equals(ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO) ||
                        //se invece l'esito e' ? oppure Q estraggo solo le anamnesi vuote
                        (tipo.equals("Vuote") &&
                         (invitoRow.getAttribute("Codesitoinvito").toString().equals(ConfigurationConstants.CODICE_ESITO_NON_DISPONIBILE) ||
                          invitoRow.getAttribute("Codesitoinvito").toString().equals(ConfigurationConstants.CODICE_ESITO_KIT_RITIRATO)))) {
                        System.out.println(invitoRow.getAttribute("Idinvito"));
                        //se devo stampare anamnesi vuote mi bastano gli id degli inviti
                        if (tipo.equals("Vuote")) {
                            idanam.add(invitoRow.getAttribute("Idinvito").toString());
                        } else {
                            //stamp anamneis compilate, lavoro sull'id ell'anamnesi
                            String query =
                                "SELECT C.id_anamco FROM SO_ACC_Colon2LIV A, SO_ANAMNESIcolon C " +
                                "WHERE A.idaccco_2liv=c.idaccco_2liv AND A.idinvito=" +
                                invitoRow.getAttribute("Idinvito").toString();
                            tempVo = am.createViewObjectFromQueryStmt("tempVo", query);
                            tempVo.executeQuery();
                            Row r = tempVo.first();
                            if (r != null)
                                idanam.add(r.getAttribute(0).toString());
                            tempVo.remove();
                        }
                    }
                    //gli inviti con altri esiti non li estraggo mai
                }
                rsiAnam.closeRowSetIterator();


                if (tipo.equals("Vuote")) //vuote
                {
                    reportType = SystemReport.SCHEDA_ANAM_COLON_VUOTA;
                    paramname = "idinvito";

                } else //compilate
                {
                    reportType = SystemReport.SCHEDA_ANAM_COLON;
                    paramname = "idanamco";
                }


            }
            if (tpscr.equals("MA")) {
                am =
                    (AccMa_AppModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
                ulss_vo = am.findViewObject("AccMa_SoAziendaView1");
                param_vo = am.findViewObject("AccMa_SoCnfPartemplateView1");
                ViewObject voInvito = am.findViewObject("AccMa_RicInvitiView1");

                rsiAnam = ViewHelper.getRowSetIterator(voInvito, "iter_accanam");
                idanam = new ArrayList();

                while (rsiAnam.hasNext()) {
                    Row invitoRow = rsiAnam.next();
                    //se l'esito e' S estraggo l'invito sia per le anamnesi vuote che per quelle compilate
                    if (invitoRow.getAttribute("Codesitoinvito").toString().equals(ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO) ||
                        //se invece l'esito e' ? estraggo solo le anamnesi vuote
                        (tipo.equals("Vuote") &&
                         invitoRow.getAttribute("Codesitoinvito").toString().equals(ConfigurationConstants.CODICE_ESITO_NON_DISPONIBILE))) {
                        //se devo stampare anamnesi vuote mi bastano gli id degli inviti
                        if (tipo.equals("Vuote")) {
                            idanam.add(invitoRow.getAttribute("Idinvito").toString());
                        } else {
                            //stamp anamneis compilate, lavoro sull'id ell'anamnesi
                            //la query cambia a seconda del livello dell'invito
                            String livello = invitoRow.getAttribute("Livello").toString();
                            String query =
                                "SELECT C.id_anamma FROM SO_ACC_mammo" + livello + "LIV A, SO_ANAMNESImx C " +
                                "WHERE A.idaccma_" + livello + "liv=C.idaccma_" + livello + "liv AND A.idinvito=" +
                                invitoRow.getAttribute("Idinvito").toString();
                            tempVo = am.createViewObjectFromQueryStmt("tempVo", query);
                            tempVo.executeQuery();
                            Row r = tempVo.first();
                            if (r != null)
                                idanam.add(r.getAttribute(0).toString());
                            tempVo.remove();
                        }
                    }
                    //gli inviti con altri esiti non li estraggo mai
                }
                rsiAnam.closeRowSetIterator();


                if (tipo.equals("Vuote")) //vuote
                {
                    reportType = SystemReport.SCHEDA_ANAM_MX_VUOTA;
                    paramname = "idinvito";

                } else //compilate
                {
                    reportType = SystemReport.SCHEDA_ANAM_MX;
                    paramname = "idanamma";
                }

            }

            if (idanam.size() > 0) {
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

                OutputStream outputStream = new ByteArrayOutputStream();
                if (idanam.size() > 50) {
                    zip = zipFile(pdf);
                    _ret.put("fileName", "zipped_report.zip");
                    _ret.put("contentType", "application/zip");
                    this.outputPrintServlet(outputStream, zip);
                    _ret.put("outputStream", outputStream);
                } else {
                    _ret.put("fileName", "SchedaAnam_all.pdf");
                    _ret.put("contentType", "application/pdf");
                    this.outputPrintServlet(outputStream, pdf);
                    _ret.put("outputStream", outputStream);
                }
            } else {
                this.handleMessages(FacesMessage.SEVERITY_WARN, "Nessuna anamnesi da stampare");
            }


        } catch (Exception e) {
            e.printStackTrace();
            this.saveMessages("Impossibile stampare le anamnesi: " + e.getMessage());
        } finally {
            if (pdf != null)
                pdf.delete();
            if (zip != null)
                zip.delete();
            if (tempVo != null) {
                try {
                    tempVo.remove();
                } catch (Throwable th) {
                }
            }
            if (rsiAnam != null)
                rsiAnam.closeRowSetIterator();
            ParametriSistema.releaseLogo(h);
        }

        return _ret;
    }

    public void downloadListener(@SuppressWarnings("unused") ActionEvent actionEvent) {
        Map<String, Object> resp = print();

        if (resp.isEmpty() || !resp.containsKey("outputStream"))
            handleException("Errore nel recupero del file!");
        else {
            FileDownloadActionListener fileDownloadActionListener = new FileDownloadActionListener();
            FacesContext fctx = FacesContext.getCurrentInstance();
            ELContext elctx = fctx.getELContext();
            Application application = fctx.getApplication();
            ExpressionFactory exprFactory = application.getExpressionFactory();
            MethodExpression methodExpr = null;
            methodExpr =
                exprFactory.createMethodExpression(elctx, "#{Print_elencoAnamStampaAction.print}", null,
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

    protected void outputPrintServlet(OutputStream output, File f) throws IOException {
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

    @SuppressWarnings({ "oracle.jdeveloper.java.nested-assignment", "oracle.jdeveloper.java.insufficient-catch-block" })
    private File zipFile(File pdf) {
        byte[] buf = new byte[1024];
        File zip = new File("zipped_report.zip");

        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
            ZipEntry entry = new ZipEntry("SchedaAnam_all.pdf");
            out.putNextEntry(entry);
            FileInputStream in = new FileInputStream(pdf);
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            // Complete the entry
            out.closeEntry();
            in.close();
            out.close();

            return zip;

        } catch (Exception e) {

        }
        return null;
    }

    public void print(@SuppressWarnings("unused") FacesContext facesContext, OutputStream outputStream) {
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
}
