package view.merge;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import model.common.Sogg_AppModule;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

public class Mrg_mergeAction extends Parent_DataForwardAction {

    private String idInterno_val;
    private String tessera_val;
    private String codiceFiscale_val;
    private RichForm mergeForm;
    private String idInterno_depr;
    private String tessera_depr;
    private String codiceFiscale_depr;
    private RichPopup mergeMPIWarnigPopup;

    @Override
    protected void setAppModule() {
        this.amName="Sogg_AppModule";
    }

    public void setIdInterno_val(String idInterno_val) {
        this.idInterno_val = idInterno_val;
    }

    public String getIdInterno_val() {
        return idInterno_val;
    }

    public void setTessera_val(String tessera_val) {
        this.tessera_val = tessera_val;
    }

    public String getTessera_val() {
        return tessera_val;
    }

    public void setCodiceFiscale_val(String codiceFiscale_val) {
        this.codiceFiscale_val = codiceFiscale_val;
    }

    public String getCodiceFiscale_val() {
        return codiceFiscale_val;
    }

    public void cercaVal(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String)session.get("ulss");   
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoggValidoViewIterator");
        ViewObject vo = voIter.getViewObject();

        //parametri minimi
        boolean paramOk = false;
        if (this.getTessera_val() != null) {
            if (!("null").equals(this.getTessera_val())) {
                if (this.getTessera_val().length() > 1) {
                    paramOk = true;
                }
            }
        }
        if (this.getCodiceFiscale_val() != null) {
            if (!("null").equals(this.getCodiceFiscale_val())) {
                if (this.getCodiceFiscale_val().length() > 1) {
                    paramOk = true;
                }
            }
        }
        if (this.getIdInterno_val() != null) {
            if (!("null").equals(this.getIdInterno_val())) {
                if (this.getIdInterno_val().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (!paramOk) {
            this.handleException("Parametri di ricerca insufficienti", null);
        } else {
            // Eseguo la query
            this.cercaSoggetti(vo, this.getTessera_val(),this.getCodiceFiscale_val(),this.getIdInterno_val(), ulss);
        }
    }
    
    private void cercaSoggetti(ViewObject soggetti, String tessera, String cf, String mpi, String ulss) {

        String whcl = " ULSS = '" + ulss + "'";

        if (cf != null && !(cf.equals(""))) {
            whcl += " and CF = '" + cf + "'";
        }

        if (tessera != null && !(tessera.equals(""))) {
            whcl += " and CODTS = FUN_DOC_TROVA_CODTS('TS','" + tessera + "','" + ulss + "','N') ";
        }

        if (mpi != null && !(mpi.equals(""))) {
            whcl += " and IDINTERNO = '" + mpi + "'";
        }

        soggetti.setWhereClause(whcl);
        soggetti.setOrderByClause("COGNOME, NOME, DATA_NASCITA");
        soggetti.executeQuery();

    }

    public void setMergeForm(RichForm mergeForm) {
        this.mergeForm = mergeForm;
    }

    public RichForm getMergeForm() {
        if(mergeForm==null)
            try {
                findForward();
            } catch (Exception e) {
                this.handleException(e.getMessage(), null);
            }
        return mergeForm;
    }
    
    protected void findForward() throws Exception {
        resetCampi();
      
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoggValidoViewIterator");
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        
        Map request = ADFContext.getCurrent().getPageFlowScope();
        String codts = (String)request.get("merge_codts");
        String codtsVal = (String) request.get("merge_codts_val");
        String codtsDepr = (String) request.get("merge_codts_depr");

        ViewObject vo = voIter.getViewObject();
        if (codtsVal != null && codtsVal.length() > 0) {
            this.setTessera_val(codtsVal);
            String whcl = " ULSS = '" + ulss + "' AND CODTS = '" + codtsVal + "'";
            vo.setWhereClause(whcl);
        } else {
            vo.setWhereClause("1=2");
        }
        vo.executeQuery();

        voIter = bindings.findIteratorBinding("Mrg_SoggDeprecatoViewIterator");
        vo = voIter.getViewObject();
        if (codts != null && codts.length() > 0) {
            this.setTessera_depr(codts);
            String whcl = " ULSS = '" + ulss + "' AND CODTS = '" + codts + "'";
            vo.setWhereClause(whcl);
        } else if (codtsDepr != null && codtsDepr.length() > 0) {
            this.setTessera_depr(codtsDepr);
            String whcl = " ULSS = '" + ulss + "' AND CODTS = '" + codtsDepr + "'";
            vo.setWhereClause(whcl);
        } else {
            vo.setWhereClause("1=2");
        }
        vo.executeQuery();

    }
    
    private void resetCampi() {
        this.setCodiceFiscale_depr(null);
        this.setCodiceFiscale_val(null);
        this.setIdInterno_depr(null);
        this.setIdInterno_val(null);
        this.setTessera_depr(null);
        this.setTessera_val(null);
      }

    public void setIdInterno_depr(String idInterno_depr) {
        this.idInterno_depr = idInterno_depr;
    }

    public String getIdInterno_depr() {
        return idInterno_depr;
    }

    public void setTessera_depr(String tessera_depr) {
        this.tessera_depr = tessera_depr;
    }

    public String getTessera_depr() {
        return tessera_depr;
    }

    public void setCodiceFiscale_depr(String codiceFiscale_depr) {
        this.codiceFiscale_depr = codiceFiscale_depr;
    }

    public String getCodiceFiscale_depr() {
        return codiceFiscale_depr;
    }

    public void cercaDepr(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoggDeprecatoViewIterator");
        ViewObject vo = voIter.getViewObject();

        //parametri minimi
        boolean paramOk = false;
        if (this.getTessera_depr() != null) {
            if (!("null").equals(this.getTessera_depr())) {
                if (this.getTessera_depr().length() > 1) {
                    paramOk = true;
                }
            }
        }
        if (this.getCodiceFiscale_depr() != null) {
            if (!("null").equals(this.getCodiceFiscale_depr())) {
                if (this.getCodiceFiscale_depr().length() > 1) {
                    paramOk = true;
                }
            }
        }
        if (this.getIdInterno_depr() != null) {
            if (!("null").equals(this.getIdInterno_depr())) {
                if (this.getIdInterno_depr().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (!paramOk) {
            this.handleException("Parametri di ricerca insufficienti", null);
        } else {
            // Eseguo la query
            this.cercaSoggetti(vo, this.getTessera_depr(), this.getCodiceFiscale_depr(), this.getIdInterno_depr(), ulss);

        }
    }

    public void scambiaAnag(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");

        //scambio i filtri
        String temp_tess_val = this.getTessera_val();
        String temp_cf_val = this.getCodiceFiscale_val();
        String temp_mpi_val = this.getIdInterno_val();

        this.setTessera_val(this.getTessera_depr());
        this.setCodiceFiscale_val(this.getCodiceFiscale_depr());
        this.setIdInterno_val(this.getIdInterno_depr());
        this.setTessera_depr(temp_tess_val);
        this.setCodiceFiscale_depr(temp_cf_val);
        this.setIdInterno_depr(temp_mpi_val);

        //eseguo le ricerche
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoggValidoViewIterator");
        ViewObject vo = voIter.getViewObject();

        //parametri minimi
        boolean paramOk = false;
        if (this.getTessera_val() != null) {
            if (!("null").equals(this.getTessera_val())) {
                if (this.getTessera_val().length() > 1) {
                    paramOk = true;
                }
            }
        }
        if (this.getCodiceFiscale_val() != null) {
            if (!("null").equals(this.getCodiceFiscale_val())) {
                if (this.getCodiceFiscale_val().length() > 1) {
                    paramOk = true;
                }
            }
        }
        if (this.getIdInterno_val() != null) {
            if (!("null").equals(this.getIdInterno_val())) {
                if (this.getIdInterno_val().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (!paramOk) {
            //svuoto il vo
            vo.setWhereClause("1=2");
            vo.executeQuery();
        } else {
            // Eseguo la query
            this.cercaSoggetti(vo, this.getTessera_val(), this.getCodiceFiscale_val(), this.getIdInterno_val(), ulss);
        }

        voIter = bindings.findIteratorBinding("Mrg_SoggDeprecatoViewIterator");
        vo = voIter.getViewObject();
        //parametri minimi
        paramOk = false;
        if (this.getTessera_depr() != null) {
            if (!("null").equals(this.getTessera_depr())) {
                if (this.getTessera_depr().length() > 1) {
                    paramOk = true;
                }
            }
        }
        if (this.getCodiceFiscale_depr() != null) {
            if (!("null").equals(this.getCodiceFiscale_depr())) {
                if (this.getCodiceFiscale_depr().length() > 1) {
                    paramOk = true;
                }
            }
        }
        if (this.getIdInterno_depr() != null) {
            if (!("null").equals(this.getIdInterno_depr())) {
                if (this.getIdInterno_depr().length() > 1) {
                    paramOk = true;
                }
            }
        }

        if (!paramOk) {
            //svuoto il vo
            vo.setWhereClause("1=2");
            vo.executeQuery();
        } else {
            // Eseguo la query
            this.cercaSoggetti(vo, this.getTessera_depr(), this.getCodiceFiscale_depr(), this.getIdInterno_depr(),
                               ulss);
        }
    }

    public void merge(DialogEvent dialogEvent) {
        //se l'anagrafica valida non ha MPI mentre quella da deprecare si
        //segnala all'utente che deve scambiare le posizioni
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Mrg_SoggValidoViewIterator");
        ViewObject voValido = voIter.getViewObject();
        Row soggValido = voValido.getCurrentRow();
        String mpiValido = (String) soggValido.getAttribute("Idinterno");

        voIter = bindings.findIteratorBinding("Mrg_SoggDeprecatoViewIterator");
        ViewObject voDeprecato = voIter.getViewObject();
        Row soggDepr = voDeprecato.getCurrentRow();
        String mpiDepr = (String) soggDepr.getAttribute("Idinterno");
        
        boolean bypassControlMPI = false;
        try{
           bypassControlMPI = (Boolean)ADFContext.getCurrent().getRequestScope().get("bypassControlMPI");
        }catch(Throwable th){
            bypassControlMPI = false;
        }

        if (mpiValido == null && mpiDepr != null) {
            this.handleException("Scambiare le posizioni anagrafiche per effettuare il merge", null);
            // I00095789
        } else if (mpiValido != null && mpiDepr != null && !mpiValido.equalsIgnoreCase(mpiDepr) && !bypassControlMPI){
            getMergeMPIWarnigPopup().show(new RichPopup.PopupHints());
            // I00095789
        } else {
            //chiamo la procedura di merge
            Map session = ADFContext.getCurrent().getSessionScope();
            String ulss = (String) session.get("ulss");
            String user = (String) session.get("user");
            Sogg_AppModule am = (Sogg_AppModule)voDeprecato.getApplicationModule();
            String message =
                am.callMergeAnagrafico(ulss, (String) soggValido.getAttribute("Codts"),
                                       (String) soggDepr.getAttribute("Codts"), user);

            if (message != null) {
                this.handleException( message, null);
            } else {
                this.handleMessages(FacesMessage.SEVERITY_INFO, "Merge anagrafico eseguito con successo. Modificata la situazione di screening del soggetto a seguito di merge/unmerge, si consiglia verifica.");
                // rifaccio la ricerca dei soggetti deprecati per evidenziare il soggetto deprecato
                voDeprecato.executeQuery();
            }
        }
    }

    // I00095789
    public void mergeAnag(ActionEvent actionEvent) {
        this.merge(null);
    }
    // I00095789
    public void setMergeMPIWarnigPopup(RichPopup mergeMPIWarnigPopup) {
        this.mergeMPIWarnigPopup = mergeMPIWarnigPopup;
    }

    public RichPopup getMergeMPIWarnigPopup() {
        return mergeMPIWarnigPopup;
    }
}
