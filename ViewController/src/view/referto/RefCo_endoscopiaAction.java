package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.RefCo_AppModule;
import model.common.Ref_SoEndoscopiaViewRow;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class RefCo_endoscopiaAction extends RefCo_DataForwardAction {

    private RichForm endoForm;

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

    public void setEndoForm(RichForm endoForm) {
        this.endoForm = endoForm;
    }

    public RichForm getEndoForm() {
        if (endoForm == null) {
            //filtro i dati
            this.filter();
        }
        return endoForm;
    }

    protected void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
        //considero comunque la data di inserimento del referto, poiche' l'endoscopia non ne ha una sua
        Row r = voIter.getCurrentRow();
        if (r == null)
            return;
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //LETTORE
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
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

        //suggerimenti 2 liv
        vo = am.findViewObject("Ref_SoCnfSugg2livView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //suggerimenti 3 liv
        vo = am.findViewObject("Ref_SoCnfSugg3livView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //REGIONE RAGGIUNTA (meno le sedi multiple)
        vo = am.findViewObject("Ref_SoCnfRef2livENDREGView1");
        String where = "TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' ";
        if (data != null)
            where +=
                " AND (DTFINEVALIDITA IS NULL OR  DTFINEVALIDITA > TO_DATE('" + data + "','" + DateUtils.DATE_PATTERN +
                "'))";
        where += " AND IDCNFREF2L <> " + ConfigurationConstants.CODICE_REGIONE_MULTIPLE;
        vo.setWhereClause(where);
        vo.executeQuery();
    }

    protected void beforeSavingReferto() throws Exception {
        //annullo i campi ch enon devono avere un valore
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaView1Iterator");
        RowSetIterator iter = ViewHelper.getRowSetIterator(voIter.getViewObject(), "endo_iter");
        while (iter.hasNext()) {
            Ref_SoEndoscopiaViewRow r = (Ref_SoEndoscopiaViewRow) iter.next();
            //se l'estensione e' completa, allora motivo e latro motivo vanno resettati
            if (ConfigurationConstants.DB_TRUE.equals(r.getEstensione())) {
                r.setMotivo(ConfigurationConstants.DB_FALSE);
                r.setAltroMotivo(null);
            }
            //se motivo non e' su altro, nessun dato
            else if (ConfigurationConstants.CODICE_MOTIVO_ALTRO != r.getMotivo())
                r.setAltroMotivo(null);

            //se non c'e' sedazione farmaci/dosi va annullato
            if (ConfigurationConstants.DB_FALSE.equals(r.getSedazione()))
                r.setFarmaciDosi(null);

            //se le complicazne non sono "altro" le altre compl. vanno annullate
            if (ConfigurationConstants.CODICE_COMPLIC_ALTRO != r.getComplicanze())
                r.setAltreComplicanze(null);

        }
        super.beforeSavingReferto();
    }

    /**
     *Scatta dopo una commit, chiama il metodo che imposta automaticamente le
     * conclusioni i 2 livello
     * @param ctx
     */
    protected String afterSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
        
        try {
            this.updateConclusioni();
            am.getTransaction().commit();
        } catch (Exception ex) {
            //    ex.printStackTrace();
            this.handleException("Modifiche salvate, ma impossibile aggiornare automaticamente le conclusioni del referto: " +
                                 ex.getMessage(), am,
                                 new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoEndoscopiaView1" });
        }
        
        return null;
    }

    public void onSetEstensione(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaView1Iterator");
        Ref_SoEndoscopiaViewRow r = (Ref_SoEndoscopiaViewRow) voIter.getCurrentRow();
        if (r == null)
            return;
        Integer regione = (Integer)valueChangeEvent.getNewValue();
        // 14/02/2013 Gaion: l'estensione e' completa anche per ANASTOMOSI
        //se ho raggiunto il cieco l'estensione e' completa, altrimenti incompleta
        if (ConfigurationConstants.CODICE_REGIONE_CIECO==regione ||
            ConfigurationConstants.CODICE_REGIONE_ANASTOMOSI==regione) {
            r.setEstensione(ConfigurationConstants.DB_TRUE);
            //non ci sono motivi di incompletezza
            r.setMotivo(ConfigurationConstants.DB_FALSE);
            r.setAltroMotivo(null);
        }

        else
            r.setEstensione(ConfigurationConstants.DB_FALSE);
    }
}
