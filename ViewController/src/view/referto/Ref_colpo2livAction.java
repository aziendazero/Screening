package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.Ref_AppModule;

import model.commons.ConfigurationConstants;

import model.datacontrol.Ref_2livBean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class Ref_colpo2livAction extends Ref_DataForwardAction {
    private RichForm frmReferto;

    public Ref_colpo2livAction() {
    }

    public void setFrmReferto(RichForm frmReferto) {
        this.frmReferto = frmReferto;
    }

    public RichForm getFrmReferto() {
        if (frmReferto == null) {
            //filtro i dati di interfaccia in base alla data della colposcopia
            this.filter();
            GestoreRefertiUI.loadFromDB2livColpo();
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
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.dateValue());
        try {
            //supervisore
            ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_GINECOLOGO, ulss, tpscr);

            //ESITO DELLA COLPOSCOPIA
            vo = am.findViewObject("Ref_SoCnfRef2livCOLPESView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //validita' della colposcopia
            vo = am.findViewObject("Ref_SoCnfRef2livCOLPVLView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //reperti colposcopici miscellanei
            vo = am.findViewObject("Ref_SoCnfRef2livCOLPREView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //prelievo biotpico
            vo = am.findViewObject("Ref_SoCnfRef2livCOLPPBView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //prelievo citologico
            vo = am.findViewObject("Ref_SoCnfRef2livCOLPPCView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //esito del pap test
            vo = am.findViewObject("Ref_SoCnfRef2livGIUDIAView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //positivita' del pap test
            vo = am.findViewObject("Ref_SoCnfRef2livCITOPOSView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

            //esito HPV
            vo = am.findViewObject("Ref_SoCnfRef2livHPVES2View1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void updateDB() {
        GestoreRefertiUI.updateDB2livColpo();
    }

    @Override
    protected void loadFromDB() {
        GestoreRefertiUI.loadFromDB2livColpo();
    }

    public void onEseguito(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        try {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocito2livView4Iterator");
            ViewObject ref = voIter.getViewObject();
            Ref_AppModule am = (Ref_AppModule) ref.getApplicationModule();

            ViewObject inviti = am.findViewObject("Ref_SoInvitoView2");
            Ref_2livBean bean =
                (Ref_2livBean) bindings.getBindingContext().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            Row r = ref.getCurrentRow();
            Row invito = inviti.first();
            Number eseguito = (Number) r.getAttribute("Colposcopia");
            //se non e' stato eseguito
            //DA SISTEMARE IN BASE ALLE DECISIONI
            if (ConfigurationConstants.DB_FALSE.equals(eseguito)) {
                //reset di tutti gli oggetti
                bean.setColpo_esito(null);

                r.setAttribute("Dtcolposcopia", null);
                r.setAttribute("Notecolposcopia", null);
                r.setAttribute("Idmedcolpo", null);
                r.setAttribute("Idmedcolpo2", null);

            } else if (ConfigurationConstants.CODICE_COLPVL_NON_ES_FLOGISTICO.equals(eseguito) ||
                       ConfigurationConstants.CODICE_COLPVL_NON_ES_TECNICO.equals(eseguito)) {
                bean.setColpo_esito(null);
                r.setAttribute("Notecolposcopia", null);
            } else if (ConfigurationConstants.DB_TRUE.equals(eseguito)) {
                r.setAttribute("Dtcolposcopia", (oracle.jbo.domain.Date) invito.getAttribute("Dtapp"));
            } else {
                r.setAttribute("Dtcolposcopia", (oracle.jbo.domain.Date) invito.getAttribute("Dtapp"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
