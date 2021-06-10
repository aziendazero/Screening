package view.agenda;

import insiel.dataHandling.DateUtils;

import java.util.Date;
import java.util.Map;

import model.commons.AgendaUtils;

import model.datacontrol.Agenda_Servizio;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

@SuppressWarnings({ "unchecked", "deprecation" })
public class Ag_daMenuAction extends Agenda{
    @Override
    public void findForward() {
        System.out.println("-----------------> Ag_daMenuAction - findForward");

        Map session = ADFContext.getCurrent().getSessionScope();
        Map page = ADFContext.getCurrent().getPageFlowScope();
        page.remove("agendaObject");
        
        Date today = DateUtils.getOracleDateNow().dateValue();
        session.put("dtAgenda", AgendaUtils.getDateWithoutTime(today));

        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
        bean.setIdCentro(null);

        session.put("appSel", Boolean.valueOf(false));
        session.put("agDaMenu", Boolean.valueOf(true));
        session.put("oraDaInvito", Boolean.valueOf(false));
        session.put("chAgenda", "menuApp.do");

        // inzializzo elenco per riassegnazione
        AgendaUtils.initElencoRiass();
    }
}
