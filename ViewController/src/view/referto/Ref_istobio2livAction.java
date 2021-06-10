package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Calendar;
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

public class Ref_istobio2livAction extends Ref_DataForwardAction {
    private RichForm frmReferto;

    public Ref_istobio2livAction() {
    }

    public void setFrmReferto(RichForm frmReferto) {
        this.frmReferto = frmReferto;
    }

    public RichForm getFrmReferto() {
        if (frmReferto == null) {
            //filtro i dati di interfaccia in base alla data della colposcopia
            this.filter();
            GestoreRefertiUI.loadFromDB2livIstobio();
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
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_PATOLOGO, ulss, tpscr);
            //grading
            vo = am.findViewObject("Ref_SoCnfRef2livISTGRAView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
            //diagnosi
            vo = am.findViewObject("Ref_SoCnfRef2livBIODIAView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void updateDB() {
        GestoreRefertiUI.updateDB2livIstobio();
    }

    @Override
    protected void loadFromDB() {
        GestoreRefertiUI.loadFromDB2livIstobio();
    }

    public void onEseguito(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocito2livView4Iterator");
        ViewObject ref = voIter.getViewObject();
        Row r = ref.getCurrentRow();
        
        Ref_2livBean bean=(Ref_2livBean)bindings.getBindingContext().findDataControl("Ref_2livBeanDataControl").getDataProvider();

        Number eseguito = (Number) r.getAttribute("Istbioptica");
        //se non e' stato eseguito
        if (ConfigurationConstants.DB_FALSE.equals(eseguito)) {
            //reset di tutti gli oggetti
            bean.setIstobio_diagnosi(ConfigurationConstants.DB_FALSE);
            bean.setIstobio_grading(ConfigurationConstants.DB_FALSE);
            r.setAttribute("Dtbiopsia", null);
            r.setAttribute("Notebiopsia", null);
            r.setAttribute("Idmedbiopato", null);
            r.setAttribute("Idmedbiopato2", null);
            r.setAttribute("Biohiv", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Biohpv", ConfigurationConstants.DB_FALSE);
        } else {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            r.setAttribute("Dtbiopsia", new java.sql.Date(cal.getTimeInMillis()));
        }
    }
}
