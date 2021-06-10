package view.print;

import insiel.dataHandling.DateUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.util.Date;
import java.util.HashMap;

import javax.faces.context.FacesContext;

import model.Cnf_AppModuleImpl;

import model.commons.ParametriSistema;
import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

import view.reports.SystemReport;

@SuppressWarnings({ "deprecation", "unchecked", "oracle.jdeveloper.java.insufficient-catch-block" })
public class Print_storicoAction extends Parent_DataForwardAction {
    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }

    private void generatePDF(OutputStream pdfOut) {
        System.out.println("Storia scr, INIZIO: " + DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
        Cnf_AppModuleImpl am =
            (Cnf_AppModuleImpl) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        String codts = (String) ADFContext.getCurrent().getRequestScope().get("storico_codts");
        String ulss = (String) ADFContext.getCurrent().getRequestScope().get("storico_ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        HashMap h = null;
        File pdf = null;
        try {
            int report_n =
                ((Integer) ViewHelper.decodeByTpscr(tpscr, new Integer(SystemReport.STORICO_CITO),
                                                    new Integer(SystemReport.STORICO_COLON),
                                                    new Integer(SystemReport.STORICO_MAMMO),
                                                    new Integer(SystemReport.STORICO_CARDIO),
                                                    new Integer(SystemReport.STORICO_PFAS))).intValue();

            SystemReport report = new SystemReport(report_n);
            h = new HashMap();
            h.put("cod_ts", codts);
            h.put("ulss", ulss);

            String ordine = "asc";
            ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
            try {
                ordine = ParametriSistema.getParamValue(voParam, ulss, tpscr, "ordStorico");
            } catch (Exception e) {
                // parametro non trovato, non faccio niente
            }

            h.put("ordStorico", ordine);
            //report.getPDFReport(am.getDBConnection(), h, pdfOut);
            pdf = report.getPDFReport(am.getDBConnection(), h);
            //download del file
            FileInputStream fis;
            byte[] b;
            try {
                fis = new FileInputStream(pdf);

                int n;
                while ((n = fis.available()) > 0) {
                    b = new byte[n];
                    int res = fis.read(b);
                    pdfOut.write(b, 0, b.length);
                    if (res == -1)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Storia scr, FINE: " + DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
        } catch (Exception ex) {
            //   ex.printStackTrace();
            this.handleException(ex, null);
        } finally {
            ParametriSistema.releaseLogo(h);
            try {
                pdfOut.flush();
                pdfOut.close();
                if (pdf != null)
                    pdf.delete();
            } catch (IOException e) {
            }
        }
    }

    public void stampaPDF(FacesContext facesContext, OutputStream outputStream) {
        generatePDF(outputStream);
    }
}
