package view.print;

import insiel.dataHandling.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.faces.context.FacesContext;

import model.Print_AppModuleImpl;

import model.commons.ConfigurationConstants;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

import view.reports.Documento;

@SuppressWarnings({ "deprecation", "unchecked", "oracle.jdeveloper.java.insufficient-catch-block" })
public class Print_invitoAction extends Parent_DataForwardAction {
    
    private RichPopup errorPop;
    
    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }

    private void generatePDF(OutputStream pdfOut) throws IOException, Exception, FileNotFoundException {
        generatePDF(pdfOut, false);
    }

    private void generatePDF(OutputStream pdfOut, boolean deleted) throws IOException, Exception, FileNotFoundException {
        Print_AppModuleImpl am =
            (Print_AppModuleImpl) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        ViewObject vo = null;
        if (deleted) {
            vo = am.findViewObject("Print_SoAllegatoBckView1");
        } else {
            vo = am.findViewObject("Print_SoAllegatoView1");
        }
        File stampa = null;
        File f = null;
        FileInputStream is = null;

        try {
            Integer id = (Integer) ADFContext.getCurrent().getRequestScope().get("print_idallegato");

            vo.setWhereClause("IDALLEGATO=" + id);
            vo.executeQuery();
            Row a = vo.first();
            if (a == null)
                throw new Exception("Invito da stampare non trovato");

            // mauro 15/12/2010, modifiche per postel
            Integer idInvito = (Integer) a.getAttribute("Idinvito");
            String sql = "SELECT a.idtpinvito, a.idapp, a.test_proposto FROM ";
            if (deleted) {
                sql += "so_invito_bck a";
            } else {
                sql += "so_invito a";
            }
            sql += " where a.idinvito = " + idInvito.toString();
            ViewObject voApp = am.createViewObjectFromQueryStmt("", sql);
            voApp.setRangeSize(-1);
            voApp.executeQuery();
            Row fRow = voApp.first();

            String tipoInvito = (String) fRow.getAttribute(0);
            Integer idApp =
                fRow.getAttribute(1) != null ? ((oracle.jbo.domain.Number) fRow.getAttribute(1)).intValue() : null;

            String testProposto = (String) fRow.getAttribute(2);
            f = File.createTempFile("Invito", ".pdf");

            if (deleted) {
                stampa = Documento.getDocumentoDeleted(a, f, tipoInvito, idApp, testProposto);
            } else {
                stampa = Documento.getDocumento(a, f, tipoInvito, idApp, testProposto);

                if (a.getAttribute("Dtstampa") == null) {
                    a.setAttribute("Dtstampa", DateUtils.getOracleDateNow());
                    a.setAttribute("Stampapostel", ConfigurationConstants.DB_FALSE);
                }
            }
            // 15/12/2010, fine modifiche

            //  stampa=BlobUtils.getFileFromBlob((BlobDomain)a.getAttribute("Lettera"),"Invito");
            
            if (stampa == null) {
                errorPop.show(new RichPopup.PopupHints());
            } else {
                is = new FileInputStream(stampa);
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    pdfOut.write(buffer, 0, bytesRead);
                }
            }

            if (!deleted) {
                am.getTransaction().commit();
            }
        } catch (Exception ex) {
            am.getTransaction().rollback();
            throw ex;
        } finally {
            if (is != null) {
                is.close();
            }
            try {
                pdfOut.flush();
                pdfOut.close();
            } catch (IOException e) {
            }
            if (stampa != null) {
                stampa.delete();
            }
            if (f != null) {
                f.delete();
            }
        }
    }

    public void stampaPDF(FacesContext facesContext, OutputStream outputStream) throws IOException, Exception, FileNotFoundException {
        generatePDF(outputStream);
    }

    public void stampaPDFDeleted(FacesContext facesContext, OutputStream outputStream) throws IOException, Exception, FileNotFoundException {
        generatePDF(outputStream, true);
    }

    public void setErrorPop(RichPopup errorPop) {
        this.errorPop = errorPop;
    }

    public RichPopup getErrorPop() {
        return errorPop;
    }

}

