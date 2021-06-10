package view.referto;

import insiel.dataHandling.DateUtils;

import java.util.Map;

import model.common.Ref_AppModule;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class Ref_diagnosiPeggioreAction extends Ref_DataForwardAction {
    private RichForm frmReferto;

    public Ref_diagnosiPeggioreAction() {
    }

    public void setFrmReferto(RichForm frmReferto) {
        this.frmReferto = frmReferto;
    }

    public RichForm getFrmReferto() {
        if (frmReferto == null) {
            //filtro i dati di interfaccia in base alla data della colposcopia
            this.filter();

            //refresh per vedere le eventuali nuov ediagnois isologiche
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertocito2livView4Iterator");
            ViewObject dia = refVoIter.getViewObject();
            dia.getApplicationModule().findViewObject("Ref_SoInterventocitoISTDIAView1Iterator");
            dia.executeQuery();
            dia = dia.getApplicationModule().findViewObject("Ref_SoCodref2livc2BIODIAView1");
            dia.executeQuery();

            GestoreRefertiUI.loadFromDB2livDiagnosiPeggiore();
        }

        return frmReferto;
    }

    protected void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertocito2livView4Iterator");
        ViewObject ref = refVoIter.getViewObject();
        Ref_AppModule am = (Ref_AppModule)ref.getApplicationModule();

        Row r = ref.getCurrentRow();
        if (r == null)
            return;
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());
        try {

            //diagnosi peggiore
            ViewObject vo = am.findViewObject("Ref_SoCnfRef2livRACDIAView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void updateDB() {
        GestoreRefertiUI.updateDB2livDiagnosiPeggiore();
    }

    @Override
    protected void loadFromDB() {
        GestoreRefertiUI.loadFromDB2livDiagnosiPeggiore();
    }

    protected void beforeSavingReferto() throws Exception {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        Integer livello = (Integer) ADFContext.getCurrent().getSessionScope().get("ref_livello");

        if (livello == null)
            throw new Exception("Nessun referto da salvare");

        String voName;
        if (livello.intValue() == 2)
            voName = "Ref_SoRefertocito2livView4";
        else
            voName = "Ref_SoRefertocito1livView3";

        ViewObject ref = am.findViewObject(voName);
        Row r = ref.getCurrentRow();
        if (r == null)
            return;

        this.checkMandatoryData(r);

        //qui e' sparito il controllo di chiusura perche' la diagnosi peggiore puo' esser sempre aggiornata

        //impostazione dati ultima modifica
        r.setAttribute("Dtultimamodifica", DateUtils.getOracleDateNow());
        r.setAttribute("Opultmodifica", ADFContext.getCurrent().getSessionScope().get("user"));

        this.updateDB();
    }
}
