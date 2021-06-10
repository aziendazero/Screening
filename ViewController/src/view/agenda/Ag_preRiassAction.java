package view.agenda;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import model.commons.AgendaUtils;

import oracle.adf.view.rich.component.rich.RichForm;

import view.commons.action.Parent_DataForwardAction;

public class Ag_preRiassAction {
    private RichForm frmagenda;

    public Ag_preRiassAction() {
    }

    public void setFrmagenda(RichForm frmagenda) {
        this.frmagenda = frmagenda;
    }

    public RichForm getFrmagenda() {
        if (frmagenda == null) {
            findForward();
        }

        return frmagenda;
    }

    public void findForward() {
        try {
            AgendaUtils.queryElencoRiass();
        } catch (Exception ex) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, Parent_DataForwardAction.printStackTrace(ex), "");
            ctx.addMessage(null, fm);

        }
    }
}
