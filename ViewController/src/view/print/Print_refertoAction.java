package view.print;

import java.io.OutputStream;

import java.util.Map;

import javax.faces.context.FacesContext;

import model.RefCa_AppModule;

import model.common.RefCo_AppModule;
import model.common.RefMa_AppModule;
import model.common.RefPf_AppModule;
import model.common.Ref_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ApplicationModule;

import java.io.File;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.reports.Documento;

import insiel.dataHandling.DateUtils;

import java.io.FileInputStream;

import java.io.IOException;

import model.common.Parent_AppModule;

import model.commons.ConfigurationConstants;

public class Print_refertoAction {

    public void stampaPDF(FacesContext facesContext, OutputStream outputStream) throws Exception {
        generatePDF(outputStream);
    }

    private void generatePDF(OutputStream pdfOut) throws Exception {
        Integer idref = (Integer) ADFContext.getCurrent().getRequestScope().get("print_referto_idref");
        Integer livello = null;
        try {
            livello = (Integer) ADFContext.getCurrent().getRequestScope().get("print_referto_livello");
        } catch (Exception e) {
            String livString = (String) ADFContext.getCurrent().getRequestScope().get("print_referto_livello");
            livello = new Integer(livString);
        }

        ApplicationModule am = null;
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String vo_name1 = null;
        String vo_name2 = null;
        String rollback_vo = null;
        String attname = null;

        if ("CI".equals(tpscr)) {
            am =
                (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
            vo_name2 = "Ref_SoRefertocito2livView1";
            vo_name1 = "Ref_SoRefertocito1livView1";
            rollback_vo = "Ref_SoInvitiPerRefertiView1";
            attname = "Dtultimamodifica";
        }

        else if ("CO".equals(tpscr)) {
            am =
                (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
            vo_name2 = "Ref_SoRefertocolon2livView2";
            vo_name1 = "Ref_SoRefertocolon1livView2";
            rollback_vo = "Ref_SoInvitiPerRefertiCOView1";
            attname = "Dtultmodifica";
        } else if ("MA".equals(tpscr)) {
            am =
                (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
            vo_name2 = "Ref_SoRefertomammo2livView2";
            vo_name1 = "Ref_SoRefertomammo1livView2";
            rollback_vo = "Ref_SoInvitiPerRefertiMAView1";
            attname = "Dtultmodifica";
        } else if ("CA".equals(tpscr)) {
            am =
                (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
            vo_name2 = "RefCa_SoRefertocardioView2";
            vo_name1 = "RefCa_SoRefertocardioView2";
            rollback_vo = "Ref_SoInvitiPerRefertiCAView1";
            attname = "Dtultimamodifica";
        } else if ("PF".equals(tpscr)) {
            am =
                (RefPf_AppModule) BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
            vo_name2 = "Ref_SoRefertopfas2livView2";
            vo_name1 = "Ref_SoRefertopfasView2";
            rollback_vo = "Ref_SoInvitiPerRefertiPFView1";
            attname = "Dtultmodifica";
        } else
            throw new Exception("Tipo di screening non identificato");

        File stampa = null;
        try {

            if (livello == null)
                throw new Exception("Nessun referto selezionato");
            //lavoro peculiare alla singola form

            String voName;
            if (livello.intValue() == 2)
                voName = vo_name2;
            else
                voName = vo_name1;

            ViewObject ref = am.findViewObject(voName);
            ref.setWhereClauseParams(null);
            ref.setWhereClause("IDREFERTO=" + idref);
            ref.executeQuery();
            Row r = ref.first();
            if (r == null)
                throw new Exception("Nessun referto selezionato per la stampa");


            Integer id = (Integer) r.getAttribute("Idallegato");
            if (id == null)
                throw new Exception("Nessuna lettera da stampare");

            ViewObject allegati = am.findViewObject("Ref_SoAllegatoView1");
            allegati.setWhereClause("IDALLEGATO=" + id);
            allegati.executeQuery();
            Row a = allegati.first();
            if (a == null)
                throw new Exception("Lettera con identificativo " + id + " non trovata");

            stampa = Documento.getDocumento(a, File.createTempFile("Referto", ".pdf"));
            String user = (String) session.get("user");
            if (a.getAttribute("Dtstampa") == null) {
                a.setAttribute("Dtstampa", DateUtils.getOracleDateNow());
                a.setAttribute("Stampapostel", ConfigurationConstants.DB_FALSE);
                r.setAttribute("Dtspedizione", a.getAttribute("Dtstampa"));
                r.setAttribute(attname, DateUtils.getOracleDateNow());
                r.setAttribute("Opultmodifica", user);

            }

            FileInputStream is = new FileInputStream(stampa);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                pdfOut.write(buffer, 0, bytesRead);
            }
            is.close();

            am.getTransaction().commit();


        } catch (Exception ex) {
            am.getTransaction().rollback();
            throw new Exception("Impossibile recuperare la stampa: " + ex.getMessage());
        } finally {
            if (stampa != null)
                stampa.delete();

            try {
                pdfOut.flush();
                pdfOut.close();
            } catch (IOException e) {
            }
        }
    }
}
