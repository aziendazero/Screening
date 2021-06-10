package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefCo_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

public class RefCo_interventoAction extends RefCo_InterventiDataForwardAction {
    private RichForm interventoForm;

    public RefCo_interventoAction() {
        super();
    }

    public void setInterventoForm(RichForm interventoForm) {
        this.interventoForm = interventoForm;
    }

    public RichForm getInterventoForm() {
        if (interventoForm == null){
            this.filter();
        }
        return interventoForm;
    }
    
    protected void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();

        Row r = voIter.getCurrentRow();
        if (r == null)
            return;

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        Date d = (Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        // Recupero la data conclusioni
        Date dataConcl = null;
        ViewObject interventoVo = am.findViewObject("Ref_SoInterventocolonView1");
        Row interventoRow = interventoVo.getCurrentRow();
        if (interventoRow != null) {
            dataConcl = (Date) interventoRow.getAttribute("Dtconcl");
        }

        try {
            //medici intervento(chirurgo)
            ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
            ViewObjectFilters.filterOpMedici(vo, data, null, new Integer[] { ConfigurationConstants.CODICE_CHIRURGO },
                                             ulss, tpscr);

            // mauro 21/12/2009, modifica gestione medico raccomandazione
            vo = am.findViewObject("Ref_SoOpmedicoViewMedRacc");
            ViewObjectFilters.filterOpMedici(vo, data, null,
                                             new Integer[] { ConfigurationConstants.CODICE_CHIRURGO,
                                                            ConfigurationConstants.CODICE_ONCOLOGO }, ulss, tpscr);
            // mauro 21/12/2009, fine modifica

            //validita' dell'intervento
            vo = am.findViewObject("Ref_SoCnfRef2livINTVALView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //motivo inesecuzione
            vo = am.findViewObject("Ref_SoCnfRef2livINTMIEView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //tipo di intervento
            vo = am.findViewObject("Ref_SoCnfRef2livINTTIPView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //complicanze
            vo = am.findViewObject("Ref_SoCnfRef2livINTCOMView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //suggerimenti
            vo = am.findViewObject("Ref_SoCnfSugg3livView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, dataConcl);

            //gli ambulatori sono filtrati a livello di am

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onChangeDataConcl(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");
        ViewObject refertoVo = voIter.getViewObject();
        Row refertoRow = refertoVo.getCurrentRow();

        Date dataConcl = (Date) refertoRow.getAttribute("Dtconcl");

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // Filtro i suggerimenti
        voIter = bindings.findIteratorBinding("Ref_SoCnfSugg3livView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(), ulss, tpscr, dataConcl);
    }

}
