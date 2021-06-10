package view.conf;

import java.util.Calendar;

import java.util.Map;

import javax.faces.application.FacesMessage;

import javax.faces.event.ActionEvent;

import model.common.Parent_AppModule;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.event.ReturnEvent;

import view.backing.ParentBackingBean;

import model.commons.SchedUtils;

public class Cnf_confImpexpBean extends ParentBackingBean {
    public Cnf_confImpexpBean() {
    }

    public void beforeSave(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfImpexpView1Iterator");
        ViewObject vo = voIter.getViewObject();

        Row r = vo.getCurrentRow();
        String tpdip = (String) r.getAttribute("Tpdip");
        Integer orasched = (Integer) r.getAttribute("Orasched");

        if ("AC".equals(tpdip) && orasched != null) {

            String ulss = (String) r.getAttribute("Ulss");
            String tpscr = (String) r.getAttribute("Tpscr");

            String user = (String) session.get("user");

            // Recupero ore e minuti di schedulazione
            int orario = orasched.intValue();
            int ore = orario / 100;
            int minuti = orario - (ore * 100);

            // Calcolo la data di esecuzione aggiungendo un giorno ad oggi ed impostando ore e minuti
            // Lascio la frazione di minuto attuale, per una partenza quasi casuale nell'arco di un minuto
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, ore);
            cal.set(Calendar.MINUTE, minuti);

            // Creo una nuova schedulazione
            SchedUtils.scheduleTimeProcess((Parent_AppModule) vo.getApplicationModule(), "ACC", ulss, tpscr, user, cal.getTime());
        }
    }
}
