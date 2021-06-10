package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefMa_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import model.referto.common.Ref_SoRefertomammo1livViewRow;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class RefMa_revisioneAction extends RefMa1l_DataForwardAction {
    private RichForm revisioneForm;

    public RefMa_revisioneAction() {
        super();
    }

    public void setRevisioneForm(RichForm revisioneForm) {
        this.revisioneForm = revisioneForm;
    }

    public RichForm getRevisioneForm() {
        if (revisioneForm == null) {
            //filtro i dati
            this.filter();
            //leggo i dati per l aprima lettura
            GestoreRefertiUI.loadFromDB1livMA(3);
        }
        return revisioneForm;
    }

    private void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
        RefMa_AppModule am = (RefMa_AppModule) refVoIter.getViewObject().getApplicationModule();
        Row r = refVoIter.getCurrentRow();
        if (r == null)
            return;
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //LETTORE
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
        // Gaion 17062013 : se e' completo non filtro per ulss:  per la rete mammografica
        if (((Integer) r.getAttribute("Completo")).intValue() == 1) {
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_RADIOLOGO, null, tpscr);
        } else {
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_RADIOLOGO, ulss, tpscr);
        }
        //indicazioni
        vo = am.findViewObject("Ref_SoCnfRef1livMXINDIView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //esiti
        vo = am.findViewObject("Ref_SoCnfRef1livMXEST1View1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //DENSITA
        vo = am.findViewObject("Ref_SoCnfRef1livMXDENSView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
    }

    protected int getNLettura() {
        return 3;
    }

    public void onSelectEsito(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
        RefMa_AppModule am = (RefMa_AppModule) refVoIter.getViewObject().getApplicationModule();
        ViewObject sugg = am.findViewObject("Ref_SoEsitoSuggView1");
        Ref_SoRefertomammo1livViewRow r = null;

        try {
            r = (Ref_SoRefertomammo1livViewRow) refVoIter.getCurrentRow();
            Integer esito = (Integer) valueChangeEvent.getNewValue();
            String where = "ULSS = '" + r.getUlss() + "' AND TPSCR = '" + r.getTpscr() + "' AND IDCNFREF1L = " + esito;
            sugg.setWhereClause(where);
            sugg.executeQuery();
            Row s = sugg.first();
            if (s != null) {
                Integer idsugg = (Integer) s.getAttribute("Idsugg");
                r.setRSugg(idsugg);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
