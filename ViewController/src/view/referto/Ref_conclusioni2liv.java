package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.Ref_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class Ref_conclusioni2liv extends Ref_DataForwardAction {
    private RichForm frmReferto;

    public Ref_conclusioni2liv() {
    }

    public void setFrmReferto(RichForm frmReferto) {
        this.frmReferto = frmReferto;
    }

    public RichForm getFrmReferto() {
        if (frmReferto == null) {
            //filtro i dati di interfaccia in base alla data della colposcopia
            this.filter();
        }

        return frmReferto;
    }

    protected void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocito2livView4Iterator");
        Ref_AppModule am = (Ref_AppModule) voIter.getViewObject().getApplicationModule();
        Row r = voIter.getCurrentRow();
        if (r == null)
            return;

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        oracle.jbo.domain.Date dataConcl = (oracle.jbo.domain.Date) r.getAttribute("Dtconcl");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.dateValue());
        try {
            //patologo o ginecologo
            ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
            ViewObjectFilters.filterOpMedici(vo, data, null,
                                             new Integer[] { ConfigurationConstants.CODICE_GINECOLOGO,
                                                             ConfigurationConstants.CODICE_PATOLOGO }, ulss, tpscr);

            //suggerimenti
            ViewObject voSugg = am.findViewObject("Ref_SoCnfSugg2livView1");
            ViewObjectFilters.filterCnfReferti(voSugg, ulss, tpscr, dataConcl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void updateDB() {
    }

    @Override
    protected void loadFromDB() {
    }

    public void onChangeDataConcl(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocito2livView4Iterator");
        ViewObject refertoVo = voIter.getViewObject();
        Row refertoRow = refertoVo.getCurrentRow();
        oracle.jbo.domain.Date dataConcl = (oracle.jbo.domain.Date) refertoRow.getAttribute("Dtconcl");

        // Filtro i suggerimenti
        ViewObject suggVo = refertoVo.getApplicationModule().findViewObject("Ref_SoCnfSugg2livView1");
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        ViewObjectFilters.filterCnfReferti(suggVo, ulss, tpscr, dataConcl);
    }
}
