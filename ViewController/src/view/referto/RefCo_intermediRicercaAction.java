package view.referto;

import insiel.utilities.dataformats.DateUtils;


import java.util.Date;
import java.util.Map;

import javax.faces.event.ActionEvent;


import model.common.RefCo_AppModule;

import model.commons.AccessManager;

import model.commons.ConfigurationConstants;

import model.commons.ViewHelper;

import model.datacontrol.Ref_SearchBean;

import model.filters.ViewObjectFilters;

import model.referto.Ref_SoEndoscopiaIntermViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.ViewObject;

public class RefCo_intermediRicercaAction extends RefCo_DataForwardAction {
    public RefCo_intermediRicercaAction() {
        super();
    }

    @Override
    protected void afterUpdateModel() {
        // TODO Implement this method
    }

    @Override
    protected void loadFromDB() {
        // TODO Implement this method
    }

    @Override
    protected void updateDB() {
        // TODO Implement this method
    }

    public void onCerca(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        Ref_SearchBean bean =
            (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
        ViewObject vo = voIter.getViewObject();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        String where =
            "IMPORTATA = 0 AND " + "ULSS = '" + ulss + "' AND " +
            "TPSCR = '" + tpscr + "' AND ";

        try {

            AccessManager.checkPermission("SORefertazione");

            //data dal
            if (bean.getInterm_dal() != null) {
                //testo il formato
                String dateFrom = DateUtils.dateToString(bean.getInterm_dal());

                where +=
                    "(NVL(DTENDO, DTREFISTO) >= TO_DATE('" +
                    dateFrom + "','" + DateUtils.DATE_PATTERN + "')" +
                    "OR (DTENDO IS NULL AND DTREFISTO IS NULL)) AND ";
            }

            if (bean.isInterm_completi()) {
                //carico solo quelli completi
                where += "COMPLETA=1 AND ";
            }

            if (bean.isIterm_errore()) {
                //carico solo quelli non importati per errore
                where +=
                    "(DTIMPORT IS NOT NULL AND ERRORE IS NOT NULL) AND ";
            }

            where = where.trim();

            if (where.endsWith("AND"))
                where = where.substring(0, where.length() - 3);

            vo.setWhereClause(where);
            vo.executeQuery();


        } catch (Exception ex) {
            this.handleException("Impossibile eseguire la ricerca: " + ex.getMessage(), null);
        }
    }
    
    public String onIntermedio() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Ref_SoEndoscopiaIntermViewRowImpl interm = (Ref_SoEndoscopiaIntermViewRowImpl) vo.getCurrentRow();
        Integer idInvito = interm.getIdinvito();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        //rieseguo il vo in modo che le endo dell'invito selezionato
        String whereStr = "ULSS = '" + ulss + "' AND " + "TPSCR = '" + tpscr + "' AND " + "IDINVITO = " + idInvito;
        vo.setWhereClause(whereStr);
        vo.executeQuery();
        //ripristino la riga corrente
        Key k = interm.getKey();
        ViewHelper.setCurrent(vo, k);

        //filtro i dati per la compilazione
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) interm.getAttribute("Dtendo");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());
        else {
            data = DateUtils.dateToString(new Date());
        }

        //LETTORE
        RefCo_AppModule am = (RefCo_AppModule) vo.getApplicationModule();
        vo = am.findViewObject("Ref_SoOpmedicoView1");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_GASTROENTEROLOGO, ulss, tpscr);
        //COMPLICAZIONI
        vo = am.findViewObject("Ref_SoCnfRef2livENDCOMView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //INDICAZIONI
        vo = am.findViewObject("Ref_SoCnfRef2livENDINDView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //MOTIVO DI INCOMPLETEZZA
        vo = am.findViewObject("Ref_SoCnfRef2livENDMOTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //QUALITA
        vo = am.findViewObject("Ref_SoCnfRef2livENDQLTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //REGIONE RAGGIUNTA 8meno le sedi multiple)
        vo = am.findViewObject("Ref_SoCnfRef2livENDREGView1");
        String where = "TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' ";
        if (data != null)
            where +=
                " AND (DTFINEVALIDITA IS NULL OR  DTFINEVALIDITA>TO_DATE('" + data + "','" + DateUtils.DATE_PATTERN +
                "'))";
        where += " AND IDCNFREF2L<>" + ConfigurationConstants.CODICE_REGIONE_MULTIPLE;
        vo.setWhereClause(where);
        vo.executeQuery();
        //DIMENSIONI MASSIME
        vo = am.findViewObject("Ref_SoCnfRef2livENDDIMView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ASPETTO
        vo = am.findViewObject("Ref_SoCnfRef2livPOLASPView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ISTOLOGIA DEI POLIPI
        vo = am.findViewObject("Ref_SoCnfRef2livISTOCOView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ISTOLOGIA DEL CANCRO
        vo = am.findViewObject("Ref_SoCnfRef2livCARISTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //DISPLASIA
        vo = am.findViewObject("Ref_SoCnfRef2livDISPCOView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //altre lesioni
        vo = am.findViewObject("Ref_SoCnfRef2livENDLESView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //PATOLOGO
        vo = am.findViewObject("Ref_SoOpmedicoView2");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_PATOLOGO, ulss, tpscr);

        return "goIntermedio";

    }
}
