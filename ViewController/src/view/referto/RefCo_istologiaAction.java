package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import model.common.RefCo_AppModule;
import model.common.Ref_SoEndoscopiaViewRow;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class RefCo_istologiaAction extends RefCo_DataForwardAction {
    private RichForm istoForm;

    public RefCo_istologiaAction() {
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

    public void setIstoForm(RichForm istoForm) {
        this.istoForm = istoForm;
    }

    public RichForm getIstoForm() {
        if (istoForm == null){
            //filtro i dati
            this.filter();
        }
        return istoForm;
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
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //DIMEZIONI MASSIME
        ViewObject vo = am.findViewObject("Ref_SoCnfRef2livENDDIMView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ASPETTO
        vo = am.findViewObject("Ref_SoCnfRef2livPOLASPView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ISTOLOGIA DEI POLIPI
        vo = am.findViewObject("Ref_SoCnfRef2livPOLISTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //ISTOLOGIA DEL CANCRO
        vo = am.findViewObject("Ref_SoCnfRef2livCARISTView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //DISPLASIA
        vo = am.findViewObject("Ref_SoCnfRef2livDISPCOView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //altre lesioni
        vo = am.findViewObject("Ref_SoCnfRef2livENDLESView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //regione (colpeta)
        //REGIONE RAGGIUNTA 8meno le sedi multiple)
        vo = am.findViewObject("Ref_SoCnfRef2livENDREGView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
        //14-02-2013 Gaion: aggiunto patologo
        //PATOLOGO
        vo = am.findViewObject("Ref_SoOpmedicoView1");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_PATOLOGO, ulss, tpscr);

    }
    
    protected void beforeSavingReferto() throws Exception {
        //annullo i campi ch enon devono avere un valore
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaView1Iterator");
        RowSetIterator iter = ViewHelper.getRowSetIterator(voIter.getViewObject(), "endo_iter");
        while (iter.hasNext()) {
            Ref_SoEndoscopiaViewRow r = (Ref_SoEndoscopiaViewRow) iter.next();
            //se il cancro non c'e', nessuno de suoi dati deve essere valorizzato
            if (ConfigurationConstants.DB_FALSE.equals(r.getCancro())) {
                this.resetPolipoCancro(r, "C");
            }
            //se il polipo 1 non c'e', nessuno de suoi dati deve essere valorizzato
            if (ConfigurationConstants.DB_FALSE.equals(r.getPolipo1())) {
                this.resetPolipoCancro(r, "P1");
            }
            //se il polipo 2 non c'e', nessuno de suoi dati deve essere valorizzato
            if (ConfigurationConstants.DB_FALSE.equals(r.getPolipo2())) {
                this.resetPolipoCancro(r, "P2");
            }
            //se il polipo 3 non c'e', nessuno de suoi dati deve essere valorizzato
            if (ConfigurationConstants.DB_FALSE.equals(r.getPolipo3())) {
                this.resetPolipoCancro(r, "P3");
            }

            //se altre lesioni non e' altro, il testo e' null
            if (!ConfigurationConstants.CODICE_LESIONI_ALTRO.equals(r.getAltreLesioni()) &&
                r.getAltreLesioniAltro() != null && r.getAltreLesioniAltro().trim().length() > 0)
                r.setAltreLesioniAltro(null);
        }
        super.beforeSavingReferto();
    }
}
