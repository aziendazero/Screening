package view.conf;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;


import model.commons.AccessManager;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.backing.ParentBackingBean;

public class Cnf_esitiBean extends ParentBackingBean  {
    private RichSelectOneChoice selectedLiv;

    public Cnf_esitiBean() {
    }

    public void filterEsitiRegionali() {
        
    }

    public void filterEsitiRegionali(ActionEvent actionEvent) {
        this.onDett(actionEvent);
        
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfEsitoinvitoView1Iterator");
        ViewObject vo = voIter.getViewObject();
        DCIteratorBinding regionaliIter = bindings.findIteratorBinding("Cnf_SoCnfEsitoinvitoRegionaleView1Iterator");
        ViewObject regionali = regionaliIter.getViewObject();
        try {
            Row r = vo.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun esito selezionato");

            String tpscr = (String) session.get("scr");
            
            Integer liv = new Integer(1);
            if (r.getAttribute("Livesito")!=null){
                String livesito = r.getAttribute("Livesito").toString();
                liv = Integer.parseInt(livesito);
            }
            
            regionali.setWhereClause("ULSS='" + AccessManager.CODREGIONALE + "' AND TPSCR = '" + tpscr + "' AND LIVESITO = " + liv);
            regionali.executeQuery();

        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void changeLivEsito(ValueChangeEvent valueChangeEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding regionaliIter = bindings.findIteratorBinding("Cnf_SoCnfEsitoinvitoRegionaleView1Iterator");
        ViewObject regionali = regionaliIter.getViewObject();
        try {
            String tpscr = (String) session.get("scr");
            String liv =  valueChangeEvent.getNewValue().toString();
            
            if (liv == null)
                liv = "1";
            
            regionali.setWhereClause("ULSS='" + AccessManager.CODREGIONALE + "' AND TPSCR='" + tpscr + "' AND LIVESITO=" + liv);
            regionali.executeQuery();

        } catch (Exception ex) {

            this.handleMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }
    
    public void onDett(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfEsitoinvitoView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("navEsitoInvito", rowKey);
    }

    public void setSelectedLiv(RichSelectOneChoice selectedLiv) {
        this.selectedLiv = selectedLiv;
    }

    public RichSelectOneChoice getSelectedLiv() {
        return selectedLiv;
    }
    
}
