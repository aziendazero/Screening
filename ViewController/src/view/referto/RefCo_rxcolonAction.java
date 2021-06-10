package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefCo_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import model.referto.common.Ref_SoRefertocolon2livViewRow;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.util.Utility;

public class RefCo_rxcolonAction extends RefCo_DataForwardAction {
    private RichForm rxForm;

    public RefCo_rxcolonAction() {
        super();
    }

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

    public void setRxForm(RichForm rxForm) {
        this.rxForm = rxForm;
    }

    public RichForm getRxForm() {
        if (rxForm == null){
            //filtro i dati
            this.filter();  
        }
        return rxForm;
    }
    
    protected void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        //consiero comunque la data di inserimento del referto, poiche' le'ndoscopia non ne ha una sua
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
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

        //radiologo
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_RADIOLOGO, ulss, tpscr);
        //QUALITA
        vo = am.findViewObject("Ref_SoCnfRef2livENDQLTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //CONCLUSIONI
        vo = am.findViewObject("Ref_SoCnfRef2livRXCONCView1_1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
    }
    
    protected void beforeSavingReferto() throws Exception {
        //annullo i campi che non devono avere un valore
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        Ref_SoRefertocolon2livViewRow r = (Ref_SoRefertocolon2livViewRow) voIter.getCurrentRow();

        if (r != null && ConfigurationConstants.DB_FALSE.equals(r.getRxColon())) {
            //se l'esame non risulta eseguito, tutti i dati vengono azzerati
            r.setDtRxColon(null);
            r.setIdmedicoRx(null);
            r.setQualita(ConfigurationConstants.DB_FALSE.intValue());
            r.setConclusioni(ConfigurationConstants.DB_FALSE.intValue());
            r.setNoteRx(null);
        }

        if (r != null && ConfigurationConstants.DB_FALSE.equals(r.getColonTac())) {
            //se l'esame non risulta eseguito, tutti i dati vengono azzerati
            r.setDtColonTac(null);
            r.setIdmedTac(null);
            r.setQualitaTac(ConfigurationConstants.DB_FALSE.intValue());
            r.setTacConcl(ConfigurationConstants.DB_FALSE.intValue());
            r.setNoteTac(null);
        }

        super.beforeSavingReferto();
    }

    public void onSetRxColon(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        Ref_SoRefertocolon2livViewRow r = (Ref_SoRefertocolon2livViewRow) voIter.getCurrentRow();
        if (r == null)
            return;

        //se dichiaro che rx colon e' eseguito, preimposto la data con quella dell'appuntamento
        if (ConfigurationConstants.DB_TRUE.equals(r.getRxColon())) {
            RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
            Row r1 = vo.getCurrentRow();
            r.setDtRxColon((Date) r1.getAttribute("Dtapp"));
        }
        
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:Qualita', 'pt1:RxConcl'])");        
    }

    public void onSetColonTac(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon2livView1Iterator");
        Ref_SoRefertocolon2livViewRow r = (Ref_SoRefertocolon2livViewRow) voIter.getCurrentRow();
        if (r == null)
            return;

        //se dichiaro che colon TAC e' eseguito, preimposto la data con quella dell'appuntamento
        if (ConfigurationConstants.DB_TRUE.equals(r.getColonTac())) {
            RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
            Row r1 = vo.getCurrentRow();
            r.setDtColonTac((Date) r1.getAttribute("Dtapp"));
        }
        
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:QualitaTac', 'pt1:TacConcl'])");           
    }
}
