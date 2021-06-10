package view.backing;

import java.util.ArrayList;
import java.util.List;

import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.model.BindingContext;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

public class ParentBackingBean {
	private List listOfDays;
        private String ulss;

	public ParentBackingBean() {
	}
        
        public BindingContainer getBindings() {
            return BindingContext.getCurrent().getCurrentBindingsEntry();
        }

	public String deleteAction() {
		BindingContext bc = BindingContext.getCurrent();
		BindingContainer bindings = bc.getCurrentBindingsEntry();
		OperationBinding deleteBinding = bindings.getOperationBinding("Delete");
		OperationBinding commitBinding = bindings.getOperationBinding("Commit");
		Object result = deleteBinding.execute();
		if (!deleteBinding.getErrors().isEmpty()) {
			//add error handling here
			return null;
		}
		Object commitResult = commitBinding.execute();
		if (!commitBinding.getErrors().isEmpty()) {
			//add error handling here
			return null;
		}
		return null;
	}

	public void setListOfDays(List listOfItems) {
		this.listOfDays = listOfItems;
	}

	public List getListOfDays() {
		listOfDays = new ArrayList();
		SelectItem si1 = new SelectItem();
		si1.setValue(new Integer(1));
		si1.setLabel("DOM");
		listOfDays.add(si1);
		SelectItem si2 = new SelectItem();
		si2.setValue(new Integer(2));
		si2.setLabel("LUN");
		listOfDays.add(si2);
		SelectItem si3 = new SelectItem();
		si3.setValue(new Integer(3));
		si3.setLabel("MAR");
		listOfDays.add(si3);
		SelectItem si4 = new SelectItem();
		si4.setValue(new Integer(4));
		si4.setLabel("MER");
		listOfDays.add(si4);
		SelectItem si5 = new SelectItem();
		si5.setValue(new Integer(5));
		si5.setLabel("GIO");
		listOfDays.add(si5);
		SelectItem si6 = new SelectItem();
		si6.setValue(new Integer(6));
		si6.setLabel("VEN");
		listOfDays.add(si6);
		SelectItem si7 = new SelectItem();
		si7.setValue(new Integer(7));
		si7.setLabel("SAB");
		listOfDays.add(si7);
		return listOfDays;
	}

	public void deleteAction(DialogEvent dialogEvent) {
		deleteAction((ActionEvent)null);
	}

	public void deleteAction(ActionEvent actionEvent) {
		BindingContext bc = BindingContext.getCurrent();
		BindingContainer bindings = bc.getCurrentBindingsEntry();
		OperationBinding deleteBinding = bindings.getOperationBinding("Delete");
		OperationBinding commitBinding = bindings.getOperationBinding("Commit");
		Object result = deleteBinding.execute();
		if (!deleteBinding.getErrors().isEmpty()) {
			//add error handling here
		}
		Object commitResult = commitBinding.execute();
		if (!commitBinding.getErrors().isEmpty()) {
			OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
			rollbackBinding.execute();
			this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile cancellare il record: restrizione di integrità violata, chiave figlia trovata.");
		}
	}

	public void handleMessage(FacesMessage.Severity severity, String msg) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		FacesMessage fm = new FacesMessage(severity, msg, "");
		ctx.addMessage(null, fm);
	}

    public void setUlss(String ulss) {
        this.ulss = ulss;
    }

    public String getUlss() {
        return ulss;
    }
}
