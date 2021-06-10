package view.soggetto;

import insiel.dataHandling.DateUtils;

import java.util.Map;

import model.common.Sogg_AppModule;

import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import oracle.jbo.domain.Date;

import view.commons.action.Parent_DataForwardAction;

public class Sogg_documentoAction extends Parent_DataForwardAction {
    protected void setAppModule() {
        this.amName = "Sogg_AppModule";
    }

    public Sogg_documentoAction() {
    }

    @SuppressWarnings("deprecation")
    public String onConfirm() {
        if (this.beforeSave()) {

            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            am.getTransaction().commit();
            ViewObject vo = am.findViewObject("Sogg_SoDocumentiSoggView1");

            ViewHelper.queryAndRestoreCurrentRow(vo);

            ADFContext.getCurrent().getSessionScope().remove("sogg_documento_mode");
            
            return "back";
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public String onRollback() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        am.doRollback("Sogg_SoDocumentiSoggView1");

        ADFContext.getCurrent().getSessionScope().remove("sogg_documento_mode");

        return "back";
    }

    @SuppressWarnings("deprecation")
    public String onApply() {
        try {

            this.beforeSave();

            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            am.getTransaction().commit();

        } catch (Exception ex) {
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage());
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public String onDelete() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ViewObject vo = am.findViewObject("Sogg_SoDocumentiSoggView1");
        Row r = vo.getCurrentRow();

        //copio e storicizzo
        ViewObject docStorico = am.findViewObject("Sogg_SoDocumentiSoggStoricoView1");
        Row doc = docStorico.createRow();
        doc.setAttribute("IdTipoDoc", r.getAttribute("IdTipoDoc"));
        doc.setAttribute("DocIdent", r.getAttribute("DocIdent"));
        doc.setAttribute("Codts", r.getAttribute("Codts"));
        doc.setAttribute("Ulss", r.getAttribute("Ulss"));
        doc.setAttribute("Dtrilascio", r.getAttribute("Dtrilascio"));
        doc.setAttribute("Dtfinevalidita", r.getAttribute("Dtfinevalidita"));
        doc.setAttribute("DocIdentAnag", r.getAttribute("DocIdentAnag"));
        doc.setAttribute("IdIstComp", r.getAttribute("IdIstComp"));
        doc.setAttribute("Dtinserimento", r.getAttribute("Dtinserimento"));
        doc.setAttribute("Opinserimento", r.getAttribute("Opinserimento"));
        doc.setAttribute("Dtultmodifica", r.getAttribute("Dtultmodifica"));
        doc.setAttribute("Opmodifica", r.getAttribute("Opmodifica"));
        doc.setAttribute("Dtstoriciz", DateUtils.getOracleDateNow());
        doc.setAttribute("Opstoriciz", sess.get("user"));

        // Rimuovo il docuemnto valido
        String sql =
            "DELETE FROM SO_DOCUMENTI_SOGG WHERE ULSS = '" + r.getAttribute("Ulss") + "' AND ID_TIPO_DOC = '" +
            r.getAttribute("IdTipoDoc") + "' AND DOC_IDENT = '" + r.getAttribute("DocIdent") + "' AND CODTS = '" +
            r.getAttribute("Codts") + "' ";
        am.getTransaction().executeCommand(sql);

        am.getTransaction().commit();

        vo.executeQuery();

        sess.remove("sogg_documento_mode");

        return "back";
    }

    @SuppressWarnings("deprecation")
    protected boolean beforeSave() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ViewObject vo = am.findViewObject("Sogg_SoDocumentiSoggView1");
        Row r = vo.getCurrentRow();

        try {

            Map sess = ADFContext.getCurrent().getSessionScope();
            String user = (String) sess.get("user");
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }

            String mode = (String) sess.get("sogg_documento_mode");

            // Impostazione dati ultima modifica
            r.setAttribute("Dtultmodifica", DateUtils.getOracleDateNow());
            r.setAttribute("Opmodifica", sess.get("user"));

            if ("edit".equals(mode)) {

            } else {
                // dati inserimento
                r.setAttribute("Dtinserimento", DateUtils.getOracleDateNow());
                r.setAttribute("Opinserimento", sess.get("user"));

                String tipoDoc = (String) r.getAttribute("IdTipoDoc");
                if ("STP".equals(tipoDoc) || "ENI".equals(tipoDoc) || "TEAM".equals(tipoDoc) || "PSU".equals(tipoDoc)) {
                    //la data di fine validia' deve essere compilata e deve essere nel futuro
                    if (r.getAttribute("Dtfinevalidita") == null) {
                        throw new Exception("Deve essere popolato il campo data fine validita'.");
                    } else {
                        Date dtFine = (Date) r.getAttribute("Dtfinevalidita");
                        if (dtFine.compareTo(DateUtils.getOracleDateNow()) < 0) {
                            throw new Exception("La data fine validita' deve essere futura rispetto la data corrente.");
                        }
                    }

                }

                //controllo di congruenza
                String errorMsg = am.callFunDocCongruenza(tipoDoc, (String) r.getAttribute("DocIdent"));
                if (errorMsg != null) {
                    throw new Exception(errorMsg);
                }
            }

            return true;

        } catch (Exception ex) {
            this.handleException("Impossibile salvare le modifiche: " + ex.getMessage(), null);
            return false;
        }
    }

}
