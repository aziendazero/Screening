package view.conf;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.share.ADFContext;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.backing.ParentBackingBean;

import view.commons.ViewHelper;

public class Cnf_confAnagBean extends ParentBackingBean {
    public Cnf_confAnagBean() {
    }

    public void includeConfAnag(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Map session = ADFContext.getCurrent().getSessionScope();
        ViewObject vo = bindings.findIteratorBinding("Cnf_SoCnfAnagFilters1Iterator").getViewObject();

        Row filter = vo.getCurrentRow();

        try {

            if (filter == null)
                throw new Exception("Nessun codice anagrafico selezionato per l'inclusione");
            //leggo il vo dipendente dal filtro selezionato
            ViewObject inclusions = bindings.findIteratorBinding("Cnf_SoCnfAnagScrView3Iterator").getViewObject();
            Row inc = inclusions.first();
            //se il codice non e' ancora stato inserito nella tabella cross, lo inserisco
            if (inc == null) {
                inc = inclusions.createRow();
                inclusions.insertRow(inc);
                inc.setAttribute("Codanagreg", filter.getAttribute("Codanagreg"));
                inc.setAttribute("Ulss", (String) session.get("ulss"));
                inc.setAttribute("Tpscr", (String) session.get("scr"));
            }
            //comunque imposto incluso a true (1)
            inc.setAttribute("Incluso", new Integer(1));

            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            Object commitResult = commitBinding.execute();

            ViewHelper.queryAndRestoreCurrentRow(vo);


        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, "Codice non incluso: " + ex.getMessage());
            
        }
    }

    public void excludeConfAnag(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject vo = bindings.findIteratorBinding("Cnf_SoCnfAnagFilters1Iterator").getViewObject();

        Row filter = vo.getCurrentRow();

        try {

            if (filter == null)
                throw new Exception("Nessun codice anagrafico selezionato per l'esclusione");
            //leggo il vo dipendente dal filtro selezionato
            ViewObject inclusions = bindings.findIteratorBinding("Cnf_SoCnfAnagScrView3Iterator").getViewObject();
            Row inc = inclusions.first();
            //se il codice non e' ancora stato inserito nella tabella cross, non
            //mi interessa inserirlo, tanto ne e' escluso
            if (inc != null) {
                inc.setAttribute("Incluso", new Integer(0));
            }


            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            Object commitResult = commitBinding.execute();

            ViewHelper.queryAndRestoreCurrentRow(vo);


        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, "Codice non escluso: " + ex.getMessage());

        }
    }
}
