package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.RefMa_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.filters.ViewObjectFilters;

import model.referto.Ref_SoRefertomammo2livViewRowImpl;
import model.referto.common.Ref_SoCnfIndicazioniViewRow;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class RefMa_radiologiaAction extends RefMa_DataForwardAction {


    private RichForm radiologiaForm;

    @Override
    protected void afterUpdateModel() {
        try {
            //se il referto e' chiuso i dati vanno pescati sempre da database
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
            ViewObject ref = refVoIter.getViewObject();
            Row r = ref.getCurrentRow();
            if (r != null && ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Completo"))) {
                GestoreRefertiUI.loadFromDB2livMA();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void updateDB() throws Exception {
        GestoreRefertiUI.updateDB2livMA();
    }

    @Override
    protected void loadFromDB() {
        GestoreRefertiUI.loadFromDB2livMA();
    }

    public void setRadiologiaForm(RichForm radiologiaForm) {
        this.radiologiaForm = radiologiaForm;
    }

    public RichForm getRadiologiaForm() {
        if (radiologiaForm == null) {
            //filtro i dati
            this.filter();

            //leggo i dati per il tipo esame/sede lesione
            GestoreRefertiUI.loadFromDB2livMA();
        }
        return radiologiaForm;
    }
    
    private void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        Row r = refVoIter.getCurrentRow();
        if (r == null)
            return;
        
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //medico per mammo ed eco
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView1Iterator");
        ViewObject vo = voIter.getViewObject();
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_RADIOLOGO, ulss, tpscr);

        //medico per esame clinico
        voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView4Iterator");
        vo = voIter.getViewObject();
        ViewObjectFilters.filterOpMedici(vo, data, null,
                                         new Integer[] { ConfigurationConstants.CODICE_RADIOLOGO,
                                                        ConfigurationConstants.CODICE_SENOLOGO }, ulss, tpscr);
        //consigli
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livMXCONSView1Iterator");
        vo = voIter.getViewObject();
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        
        //TIPI DI ESAME
        RefMa_AppModule am = (RefMa_AppModule) vo.getApplicationModule();
        vo = am.findViewObject("Ref_SoCnfRef2livMXESTPView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //primo liv relativo
        //filtro sui radiologi della ulss
        vo = am.findViewObject("Ref_SoOpmedicoView3");
        ViewObjectFilters.filterOpMedici(vo, null, null, ConfigurationConstants.CODICE_RADIOLOGO, ulss, tpscr);

        vo = am.findViewObject("Ref_SoCnfRef1livMXEST1View2");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, (String)null);
    }
    
    protected void beforeSavingReferto() throws Exception {

        //per ogni esame, se non e' eseguito allora ne cabcello i dati
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        ViewObject ref = refVoIter.getViewObject();
        Ref_SoRefertomammo2livViewRowImpl r = (Ref_SoRefertomammo2livViewRowImpl) ref.getCurrentRow();

        //mammografia
        if (!ConfigurationConstants.DB_TRUE.equals(r.getMammo())) {
            if (r.getMData() != null)
                r.setMData(null);
            if (r.getMMedico() != null)
                r.setMMedico(null);
            if (r.getMNote() != null && r.getMNote().trim().length() > 0)
                r.setMNote(null);
            
            refVoIter = bindings.findIteratorBinding("Ref_SoCnfIndicazioniView1Iterator");   
            ref = refVoIter.getViewObject();
            RowSetIterator iter = null;
            try {
                iter = ViewHelper.getRowSetIterator(ref, "iter_tipo_esame");
                while (iter.hasNext()) {
                    Ref_SoCnfIndicazioniViewRow in = (Ref_SoCnfIndicazioniViewRow) iter.next();
                    in.setDx(null);
                    in.setSx(null);
                }
            } finally {
                if (iter != null)
                    iter.closeRowSetIterator();
            }
        }

        //ecografia
        if (!ConfigurationConstants.DB_TRUE.equals(r.getEco())) {
            if (r.getEData() != null)
                r.setEData(null);
            if (r.getEMedico() != null)
                r.setEMedico(null);
            if (r.getENote() != null && r.getENote().trim().length() > 0)
                r.setENote(null);
        }

        //esame clinico
        if (!ConfigurationConstants.DB_TRUE.equals(r.getClinico())) {
            if (r.getClData() != null)
                r.setClData(null);
            if (r.getClMedico() != null)
                r.setClMedico(null);
            if (r.getClNote() != null && r.getClNote().trim().length() > 0)
                r.setClNote(null);
        }

        super.beforeSavingReferto();

    }

    public void onSetMDate(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        ViewObject ref = refVoIter.getViewObject();
        Row r = ref.getCurrentRow();
        
        RefMa_AppModule am = (RefMa_AppModule)ref.getApplicationModule();
        ViewObject invito = am.findViewObject("Ref_SoInvitoView2");

        Integer eseguito = (Integer) valueChangeEvent.getNewValue();
        if (r==null && !ConfigurationConstants.DB_TRUE.equals(eseguito))
            return;
        Row inv = invito.first();

        r.setAttribute("MData", inv.getAttribute("Dtapp"));
    }

    public void onSetEDate(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        ViewObject ref = refVoIter.getViewObject();
        Row r = ref.getCurrentRow();
        
        RefMa_AppModule am = (RefMa_AppModule)ref.getApplicationModule();
        ViewObject invito = am.findViewObject("Ref_SoInvitoView2");

        Integer eseguito = (Integer) valueChangeEvent.getNewValue();
        if (r==null && !ConfigurationConstants.DB_TRUE.equals(eseguito))
            return;
        Row inv = invito.first();

        r.setAttribute("EData", inv.getAttribute("Dtapp"));
    }

    public void onSetClDate(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        ViewObject ref = refVoIter.getViewObject();
        Row r = ref.getCurrentRow();
        
        RefMa_AppModule am = (RefMa_AppModule)ref.getApplicationModule();
        ViewObject invito = am.findViewObject("Ref_SoInvitoView2");

        Integer eseguito = (Integer) valueChangeEvent.getNewValue();
        if (r==null && !ConfigurationConstants.DB_TRUE.equals(eseguito))
            return;
        Row inv = invito.first();

        r.setAttribute("ClData", inv.getAttribute("Dtapp"));
    }
    
    /**
     * Creazione di un nuovo referto
     * @param ctx
     */
    public void onCreateReferto(ActionEvent actionEvent) {
        super.onCreateReferto(actionEvent);
        
        //filtro i dati
        this.filter();

        //leggo i dati per il tipo esame/sede lesione
        GestoreRefertiUI.loadFromDB2livMA();
    }
}
