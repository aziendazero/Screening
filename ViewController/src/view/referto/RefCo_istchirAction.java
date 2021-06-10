package view.referto;


import insiel.dataHandling.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefCo_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import model.referto.common.Ref_SoInterventocolonViewRow;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class RefCo_istchirAction extends RefCo_InterventiDataForwardAction {

    private RichForm istchirForm;

    public void setIstchirForm(RichForm istchirForm) {
        this.istchirForm = istchirForm;
    }

    public RichForm getIstchirForm() {
        if (istchirForm == null){
            this.filter(); 
        }
        return istchirForm;
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
        try {
            //patologo
            ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_PATOLOGO, ulss, tpscr);

            //diagnosi istologica
            vo = am.findViewObject("Ref_SoCnfRef2livISTOCOView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
            //stadio m
            vo = am.findViewObject("Ref_SoCnfRef2livISTOLMView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
            //stadio pn
            vo = am.findViewObject("Ref_SoCnfRef2livISTOPNView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
            //stadio pt
            vo = am.findViewObject("Ref_SoCnfRef2livISTOPTView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
            //astler coller
            vo = am.findViewObject("Ref_SoCnfRef2livISTOACView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
            //dukes
            vo = am.findViewObject("Ref_SoCnfRef2livISTODKView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
            //stadiazione
            vo = am.findViewObject("Ref_SoCnfRef2livSTADIOView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);
            //resezione
            vo = am.findViewObject("Ref_SoCnfRef2livGENRCOView1");
            ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
    public void onEseguito(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");

        Row r = voIter.getCurrentRow();
        Integer eseguito = (Integer) valueChangeEvent.getNewValue();
        //se non e' stato eseguito
        if (ConfigurationConstants.DB_FALSE.equals(eseguito)) {
            //reset di tutti gli oggetti

            r.setAttribute("Dtistologia", null);
            r.setAttribute("NoteIstologia", null);
            r.setAttribute("Idpatologo", null);
            r.setAttribute("Diagnosi", null);
            r.setAttribute("StadioM", null);
            r.setAttribute("StadioPn", null);
            r.setAttribute("StadioPt", null);
            r.setAttribute("AstlerColler", null);
            r.setAttribute("Dukes", null);
            r.setAttribute("Stadio", null);
            r.setAttribute("Resezione", ConfigurationConstants.DB_FALSE);
            r.setAttribute("NLinfonodi", null);
            r.setAttribute("NLinfonodiPositivi", null);
        } else {
            r.setAttribute("Dtistologia", DateUtils.getOracleDateNow());
        }

    }
    
    public String onCreateIntervento()
      {

        super.onCreateIntervento(null);
        
        return "to_refCo_intervento";
      }

    private void onUpdateStadiazione(Integer stadioM, Integer stadioPn, Integer stadioPt) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");
        Ref_SoInterventocolonViewRow r;
        try {
            r = (Ref_SoInterventocolonViewRow) voIter.getCurrentRow();
            if (r == null)
                return;

            //se ho M1, per ogni PN e PT ho un solo risultato
            if (ConfigurationConstants.CODICE_CO_ISTOM_M1.equals(stadioM)) {
                r.setAstlerColler(ConfigurationConstants.CODICE_ASTLERC_D);
                r.setDukes(ConfigurationConstants.CODICE_DUKES_D);
                r.setStadio(ConfigurationConstants.CODICE_STADIO_IV);
                return;
            }
            //se ho M0 devo controllare gli altri due campi
            if (ConfigurationConstants.CODICE_CO_ISTOM_M0.equals(stadioM)) {

                //se ho N1, N2 o N3
                if (stadioPn != null &&
                    ((ConfigurationConstants.CODICE_CO_ISTOPN_N1 <= stadioPn &&
                      ConfigurationConstants.CODICE_CO_ISTOPN_N1b >= stadioPn) ||
                     ConfigurationConstants.CODICE_CO_ISTOPN_N2.equals(stadioPn) ||
                     ConfigurationConstants.CODICE_CO_ISTOPN_N3.equals(stadioPn))) {

                    //imposto il dukes e lo stadio
                    r.setDukes(ConfigurationConstants.CODICE_DUKES_C);
                    r.setStadio(ConfigurationConstants.CODICE_STADIO_III);

                    //T3 o T4
                    if (ConfigurationConstants.CODICE_CO_ISTOPT_T3.equals(stadioPt) ||
                        ConfigurationConstants.CODICE_CO_ISTOPT_T4.equals(stadioPt)) {
                        r.setAstlerColler(ConfigurationConstants.CODICE_ASTLERC_C2);
                        return;
                    }
                    //T1 o T2
                    if (ConfigurationConstants.CODICE_CO_ISTOPT_T1.equals(stadioPt) ||
                        ConfigurationConstants.CODICE_CO_ISTOPT_T1MICR.equals(stadioPt) ||
                        ConfigurationConstants.CODICE_CO_ISTOPT_T2.equals(stadioPt)) {
                        r.setAstlerColler(ConfigurationConstants.CODICE_ASTLERC_C1);
                        return;
                    } else {
                        //altro (non impostabile)
                        r.setAstlerColler(null);
                        return;
                    }

                } //fine N1, N2, N3

                //se ho N0
                if (ConfigurationConstants.CODICE_CO_ISTOPN_N0.equals(stadioPn)) {

                    //imposto il dukes e lo stadio
                    //T3 o T4
                    if (ConfigurationConstants.CODICE_CO_ISTOPT_T3.equals(stadioPt) ||
                        ConfigurationConstants.CODICE_CO_ISTOPT_T4.equals(stadioPt)) {
                        r.setAstlerColler(ConfigurationConstants.CODICE_ASTLERC_B2);
                        r.setDukes(ConfigurationConstants.CODICE_DUKES_B);
                        r.setStadio(ConfigurationConstants.CODICE_STADIO_II);
                        return;
                    }
                    //T2
                    if (ConfigurationConstants.CODICE_CO_ISTOPT_T2.equals(stadioPt)) {
                        r.setAstlerColler(ConfigurationConstants.CODICE_ASTLERC_B1);
                        r.setDukes(ConfigurationConstants.CODICE_DUKES_A);
                        r.setStadio(ConfigurationConstants.CODICE_STADIO_I);
                        return;
                    }
                    //T1
                    if (ConfigurationConstants.CODICE_CO_ISTOPT_T1.equals(stadioPt) ||
                        ConfigurationConstants.CODICE_CO_ISTOPT_T1MICR.equals(stadioPt)) {
                        r.setAstlerColler(ConfigurationConstants.CODICE_ASTLERC_A);
                        r.setDukes(ConfigurationConstants.CODICE_DUKES_A);
                        r.setStadio(ConfigurationConstants.CODICE_STADIO_I);
                        return;
                    } else {
                        //altro (non impostabile)
                        r.setAstlerColler(null);
                        r.setDukes(null);
                        r.setStadio(ConfigurationConstants.DB_FALSE.intValue());
                        return;
                    }

                } //fine N0


            } else {
                //vado a resettare i dati impostati
                r.setAstlerColler(null);
                r.setDukes(null);
                r.setStadio(null);
                return;
            }

        } catch (Exception ex) {
            this.handleException("Impossibile aggiornare automaticamente la stadiazione: " + ex.getMessage(),
                                 null);
        }
    }

    public void onUpdateStadioPt(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        Integer stadioPt = (Integer) valueChangeEvent.getNewValue();

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");
        Ref_SoInterventocolonViewRow r = (Ref_SoInterventocolonViewRow) voIter.getCurrentRow();
        if (r == null)
            return;
        
        this.onUpdateStadiazione(r.getStadioM(), r.getStadioPn(), stadioPt);
    }

    public void onUpdateStadioPn(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        Integer stadioPn = (Integer) valueChangeEvent.getNewValue();
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");
        Ref_SoInterventocolonViewRow r = (Ref_SoInterventocolonViewRow) voIter.getCurrentRow();
        if (r == null)
            return;
        
        this.onUpdateStadiazione(r.getStadioM(), stadioPn, r.getStadioPt());
    }

    public void onUpdateStadioM(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        Integer stadioM = (Integer) valueChangeEvent.getNewValue();
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocolonView1Iterator");
        Ref_SoInterventocolonViewRow r = (Ref_SoInterventocolonViewRow) voIter.getCurrentRow();
        if (r == null)
            return;
        
        this.onUpdateStadiazione(stadioM, r.getStadioPn(), r.getStadioPt());
    }
}
