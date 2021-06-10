package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.Ref_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class Ref_intervento3liv extends Ref_InterventiDataForwadAction {
    private RichForm frmReferto;

    public Ref_intervento3liv() {
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
        // Recupero la data intervento
        oracle.jbo.domain.Date dataRaccomandazione = null;
        ViewObject interventoVo = am.findViewObject("Ref_SoInterventocitoView6");
        Row interventoRow = interventoVo.getCurrentRow();
        if (interventoRow != null) {
            dataRaccomandazione = (oracle.jbo.domain.Date) interventoRow.getAttribute("Dtraccomandazione");
        }

        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.dateValue());
        try {
            //medici intervento
            ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_GINECOLOGO, ulss, tpscr);
            vo = am.findViewObject("Ref_SoOpmedicoSupervisoreView1");

            // mauro 21/12/2009,aggiunto oncologo
            ViewObjectFilters.filterOpMedici(vo, data, null,
                                             new Integer[] { ConfigurationConstants.CODICE_GINECOLOGO,
                                                             ConfigurationConstants.CODICE_PATOLOGO,
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

            //suggerimenti
            vo = am.findViewObject("Ref_SoCnfSugg3livView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, dataRaccomandazione);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onChangeDataRacc(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocito2livView4Iterator");
        Ref_AppModule am = (Ref_AppModule) voIter.getViewObject().getApplicationModule();

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        // Recupero la data intervento
        ViewObject interventoVo = am.findViewObject("Ref_SoInterventocitoView6");
        Row interventoRow = interventoVo.getCurrentRow();
        oracle.jbo.domain.Date dataRaccomandazione =
            (oracle.jbo.domain.Date) interventoRow.getAttribute("Dtraccomandazione");

        //filtro i suggerimenti
        ViewObject suggerimentiVo = am.findViewObject("Ref_SoCnfSugg3livView1");
        ViewObjectFilters.filterCnfReferti(suggerimentiVo, ulss, tpscr, dataRaccomandazione);
    }
    
    public void onNextIntervento(ActionEvent actionEvent) {
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocitoView6Iterator");
        Row currInt = voIter.getCurrentRow();
        if (currInt != null && currInt.getAttribute("Completo") != null &&
            ConfigurationConstants.DB_FALSE.equals((Integer) currInt.getAttribute("Completo"))){     
            this.onApply();
        }

        OperationBinding nextBinding = bindings.getOperationBinding("Next");
        nextBinding.execute();
        
        loadFromDB();
    }

    public void onPreviousIntervento(ActionEvent actionEvent) {
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocitoView6Iterator");
        Row currInt = voIter.getCurrentRow();
        if (currInt != null && currInt.getAttribute("Completo") != null &&
            ConfigurationConstants.DB_FALSE.equals((Integer) currInt.getAttribute("Completo"))){     
            this.onApply();
        }
        
        
        OperationBinding prevBinding = bindings.getOperationBinding("Previous");
        prevBinding.execute();
        
        loadFromDB();
    }    

    public void onFirstIntervento(ActionEvent actionEvent) {
        
        this.onApply();
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        OperationBinding nextBinding = bindings.getOperationBinding("First");
        nextBinding.execute();
        
        loadFromDB();
    }

    public void onLastIntervento(ActionEvent actionEvent) {
        
        this.onApply();
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        OperationBinding prevBinding = bindings.getOperationBinding("Last");
        prevBinding.execute();
        
        loadFromDB();
    }    
}
