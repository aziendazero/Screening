package view.conf;

import insiel.dataHandling.DateUtils;


import java.sql.SQLException;

import java.util.Date;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;

import javax.servlet.http.HttpSession;

import model.datacontrol.Cnf_selectionBean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.jbo.ViewObject;

import view.backing.ParentBackingBean;

public class Cnf_operatoriMediciBean extends ParentBackingBean {
    

    public void selectTpOp(ValueChangeEvent valueChangeEvent) {
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String)session.get("scr");
        boolean regionale = session.get("regionale") != null ? ((Boolean)session.get("regionale")).booleanValue() : false;
        Cnf_selectionBean bean = (Cnf_selectionBean)BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();   
        String ulss;
        if (regionale) {
                ulss = (bean.getUlss2() != null && !bean.getUlss2().equals("-1")) ? bean.getUlss2() : null;
        } else{
                ulss = (String)session.get("ulss");
        }
        Integer tipoOp = null;
        try {
            tipoOp = (Integer)valueChangeEvent.getNewValue();
        } catch (Throwable e) {
        }

        // Aggiorno la lista dei medici in base alla Ulss e al tipo operatore
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoOpmedicoOperatoreView1Iterator");
        ViewObject operatoriView = voIter.getViewObject();      
        String data = DateUtils.dateToString(new Date());
        ViewObjectFilters.filterOpMedici(operatoriView, data, null, tipoOp.intValue(), ulss, tpscr);
    }

    public void selectUlss(ValueChangeEvent valueChangeEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String)session.get("scr");
        boolean regionale = session.get("regionale") != null ? ((Boolean)session.get("regionale")).booleanValue() : false;
        Cnf_selectionBean bean = (Cnf_selectionBean)BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();   
        String ulss;
        if (regionale) {
                ulss = (String)valueChangeEvent.getNewValue();
        } else{
            ulss = (String)session.get("ulss");
        }
        Integer tipoOp = bean.getTipoOperatore();
        
        // Aggiorno la lista dei medici in base alla Ulss e al tipo operatore
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoOpmedicoOperatoreView1Iterator");
        ViewObject operatoriView = voIter.getViewObject();      
        String data = DateUtils.dateToString(new Date());
        ViewObjectFilters.filterOpMedici(operatoriView, data, null, tipoOp.intValue(), ulss, tpscr);
    }
}
