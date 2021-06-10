package view.conf;

import java.util.Calendar;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.event.AttributeChangeEvent;

import view.backing.ParentBackingBean;

public class Cnf_festivitaBean extends ParentBackingBean {
    public Cnf_festivitaBean() {
        super();
    }


    public void changeYear(AttributeChangeEvent attributeChangeEvent) {
        
    }

    public void changeYearListener(ValueChangeEvent valueChangeEvent) {
        Boolean anniPassati = (Boolean)valueChangeEvent.getNewValue();
        Calendar iCal = Calendar.getInstance();
        iCal.setTime(new java.util.Date());
        int y = iCal.get(Calendar.YEAR);
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfFestivitaView1Iterator");
        ViewObject vo = voIter.getViewObject();
        //se l'utente ha selesionato un'azienda filtro in base a quella
        if (this.getUlss() != null && this.getUlss().length() > 0)
            if (anniPassati != null && anniPassati) {
                vo.setWhereClause("ULSS = '" + this.getUlss() + "' ");
            } else {
                vo.setWhereClause("ULSS = '" + this.getUlss() + "' AND (ANNUALE = 1 OR ANNO >=" + y + ") ");
            }
        else
        //altrimneti impongo il filtro solito
        if (anniPassati != null && anniPassati) {
            vo.setWhereClause(null);
        } else {
            vo.setWhereClause("ANNUALE = 1 OR ANNO >=" + y);
        }

        vo.executeQuery();
    }
}
