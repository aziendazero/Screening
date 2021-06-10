package view.referto;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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

public class RefMa_lettura1Action extends RefMa1l_DataForwardAction {
    private RichForm lettura1Form;

    protected int getNLettura() {
        return 1;
    }

    /**
     * Calcolo del suggerimento associato all'esito
     * @param ctx
     */
    public void onSelectEsito(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
        RefMa_AppModule am =
            (RefMa_AppModule) refVoIter.getViewObject().getApplicationModule();
        ViewObject sugg = am.findViewObject("Ref_SoEsitoSuggView1");
        Ref_SoRefertomammo1livViewRow r = null;

        try {
            r = (Ref_SoRefertomammo1livViewRow) refVoIter.getCurrentRow();
            Integer esito = (Integer)valueChangeEvent.getNewValue();
            String where = "ULSS = '" + r.getUlss() + "' AND TPSCR = '" + r.getTpscr() + "' AND IDCNFREF1L = " + esito;
            sugg.setWhereClause(where);
            sugg.executeQuery();
            Row s = sugg.first();
            if (s != null) {
                Integer idsugg = (Integer) s.getAttribute("Idsugg");
                r.setL1Sugg(idsugg);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setLettura1Form(RichForm lettura1Form) {
        this.lettura1Form = lettura1Form;
    }

    public RichForm getLettura1Form() {
        if (lettura1Form == null) {
            
            this.filter();
            
            //leggo i dati per l aprima lettura
            GestoreRefertiUI.loadFromDB1livMA(1);
                
        }
        return lettura1Form;
    }

    /**
     * Creazione di un nuovo referto
     * @param ctx
     */
    public void onCreateReferto(ActionEvent actionEvent) {
        super.onCreateReferto(actionEvent);
        
        this.filter();
        GestoreRefertiUI.loadFromDB1livMA(1);
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
    }
    
    
    /**
     * Filtra i dati per i referti di 1 livello
     */
    private void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo1livView1Iterator");
        RefMa_AppModule am =
            (RefMa_AppModule) refVoIter.getViewObject().getApplicationModule();
        Row r = refVoIter.getCurrentRow();
        if (r == null)
            return;
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");

        // Determino la data referto (oggi se null)
        oracle.jbo.domain.Date dataReferto = (oracle.jbo.domain.Date) r.getAttribute("Dtreferto");
        if (dataReferto == null) {
            dataReferto = new oracle.jbo.domain.Date(new java.sql.Date(new java.util.Date().getTime()));
        }

        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = insiel.utilities.dataformats.DateUtils.dateToString(d.getValue());

        //LETTORE
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");

        // Gaion 17062013 : se e' completo non filtro per ulss: per la rete mammografica
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

        //suggerimenti
        vo = am.findViewObject("Ref_SoCnfSugg1livView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, dataReferto);
    }
}
