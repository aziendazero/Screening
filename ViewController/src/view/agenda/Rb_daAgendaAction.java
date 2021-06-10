package view.agenda;

import java.util.Map;

import model.common.Agenda_AppModule;
import model.common.Sogg_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import view.commons.AppConstants;
import view.commons.PageDescriptor;

public class Rb_daAgendaAction {
    @SuppressWarnings({ "unchecked", "deprecation" })
    public String findForward() {
        // TODO:  Override this oracle.adf.controller.struts.actions.DataAction method
        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        am.getTransaction().rollback();
        Map session = ADFContext.getCurrent().getSessionScope();
        String chiamante = (String) session.get("chAgenda");

        if (chiamante.equals("menuApp.do")) {
            return "adfMenu_home";

        } else if (chiamante.equals("sogg_preInvCorr.do")) {
            return "goInvCorr";

        } else if (chiamante.equals("sogg_preNuovo.do")) {
            return "goNuovoInv";

        } else if (chiamante.equals("acc_preEsito.do")) {
            return "goEsitoCI";

        } else if (chiamante.equals("accCo_preEsito.do")) {
            return "to_accCo_preEsito";

        } else if (chiamante.equals("accMa_preEsito.do")) {
            return "to_accMa_preEsito";

        } else if (chiamante.equals("accPf_preEsito.do")) {
            return "to_accPf_preEsito";
        } else if (chiamante.equals("cnf_agendaCentro2.do")) {
            return "to_cnf_agendaCentro2_p";

        }

        return null;
    }
}
