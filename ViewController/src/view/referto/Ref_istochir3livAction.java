package view.referto;

import insiel.dataHandling.DateUtils;

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

public class Ref_istochir3livAction extends Ref_InterventiDataForwadAction {
    private RichForm frmReferto;

    public Ref_istochir3livAction() {
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
        if(r==null)
          return;
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        oracle.jbo.domain.Date d=(oracle.jbo.domain.Date)r.getAttribute("Dtinserimento");
        String data=null;
        if(d!=null)
           data=DateUtils.dateToString(d.getValue());
        try{
            //supervisore
            ViewObject vo=am.findViewObject("Ref_SoOpmedicoView1");
            ViewObjectFilters.filterOpMedici(vo,data,null,ConfigurationConstants.CODICE_PATOLOGO,ulss, tpscr);
            //diagnosi
            vo=am.findViewObject("Ref_SoCnfRef2livISTDIAView1");
            ViewObjectFilters.filterCnfReferti(vo,ulss,tpscr,data);
            //stadio M
            vo=am.findViewObject("Ref_SoCnfRef2livISTOLMView1");
            ViewObjectFilters.filterCnfReferti(vo,ulss,tpscr,data);
            //satdio PN
            vo=am.findViewObject("Ref_SoCnfRef2livISTOPNView1");
            ViewObjectFilters.filterCnfReferti(vo,ulss,tpscr,data);
            //stadio PT
            vo=am.findViewObject("Ref_SoCnfRef2livISTOPTView1");
            ViewObjectFilters.filterCnfReferti(vo,ulss,tpscr,data);
             //stadiazione
            vo=am.findViewObject("Ref_SoCnfRef2livSTADIOView1");
            ViewObjectFilters.filterCnfReferti(vo,ulss,tpscr,data);         
            
        }catch(Exception ex)
        {
          ex.printStackTrace();
        }
    }

    public void onEseguito(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocitoView6Iterator");

        Row r = voIter.getCurrentRow();
        Integer eseguito = (Integer) r.getAttribute("Istchirurgica");
        //se non e' stato eseguito
        if (ConfigurationConstants.DB_FALSE.equals(eseguito)) {
            //reset di tutti gli oggetti

            r.setAttribute("Dtistchir", null);
            r.setAttribute("Noteistologia", null);
            r.setAttribute("Idmedistchi", null);
            r.setAttribute("Idmedistchi2", null);
            r.setAttribute("Idcentroistchi", null);
            r.setAttribute("Istdia", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Istolm", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Istopn", ConfigurationConstants.DB_FALSE);
            r.setAttribute("Istopt", ConfigurationConstants.DB_FALSE);
            /*20080714 MOD: aggiunta stadiazione*/
            r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO__CI);
        } else {
            r.setAttribute("Dtistchir", DateUtils.getOracleDateNow());
        }
    }

    public String onCreateIntervento() {
        super.onCreateIntervento(null);
        return "toRefIntervento3liv";
    }

    /*20080714 MOD: aggiunta stadiazione*/
    public void onUpdateStadiazione(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventocitoView6Iterator");
        ViewObject ref = voIter.getViewObject();
        Row r;
        try {
            r = ref.getCurrentRow();
            if (r == null)
                return;

            //se ho M1, per ogni PN e PT ho un solo risultato
            if (ConfigurationConstants.CODICE_CI_ISTOM_M1.equals(r.getAttribute("Istolm"))) {
                r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO_IVb_CI);
                return;
            }
            //se ho PTis --> stadio 0
            if (ConfigurationConstants.CODICE_CI_ISTOPT_TIS.equals(r.getAttribute("Istopt"))) {
                r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO_0_CI);
                return;
            }
            //se ho PT1 --> stadio 1
            if (ConfigurationConstants.CODICE_CI_ISTOPT_T1.equals(r.getAttribute("Istopt"))) {
                r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO_I_CI);
                return;
            }
            //se ho PT2 --> stadio 2
            if (ConfigurationConstants.CODICE_CI_ISTOPT_T2.equals(r.getAttribute("Istopt"))) {
                r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO_II_CI);
                return;
            }
            //se ho PT3 --> stadio 3
            if (ConfigurationConstants.CODICE_CI_ISTOPT_T3.equals(r.getAttribute("Istopt"))) {
                r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO_III_CI);
                return;
            }
            //se ho PT4 --> stadio 4a
            if (ConfigurationConstants.CODICE_CI_ISTOPT_T4.equals(r.getAttribute("Istopt"))) {
                r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO_IVa_CI);
                return;
            }
            //se ho PTx o 0 --> stadio -
            if (ConfigurationConstants.CODICE_CI_ISTOPT_TX.equals(r.getAttribute("Istopt")) ||
                ConfigurationConstants.CODICE_CI_ISTOPT_T0.equals(r.getAttribute("Istopt"))) {
                r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO__CI);
                return;
            } else
                r.setAttribute("Stadiazione", ConfigurationConstants.CODICE_STADIO__CI);


        } catch (Exception ex) {
            this.handleException("Impossibile aggiornare automaticamente la stadiazione: " + ex.getMessage(), null);
        }
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
