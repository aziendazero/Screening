package view.conf;

import java.util.Map;

import javax.faces.event.ActionEvent;

import model.commons.AccessManager;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.backing.ParentBackingBean;

public class Cnf_esclusioniBean  extends ParentBackingBean {
    public Cnf_esclusioniBean() {
        super();
    }
    
    public void tpEsclFilter(ActionEvent actionEvent) {
        this.onDett(actionEvent);
        
        Map sess = ADFContext.getCurrent().getSessionScope();
        Map req  = ADFContext.getCurrent().getRequestScope();
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject escl = bindings.findIteratorBinding("Cnf_SoCnfEsclusioneRegionaleView1Iterator").getViewObject();
        
        String where ="ULSS='050000'";
        if (req.get("Tpescl")!=null)
            where += " AND TPESCL = '" + req.get("Tpescl") + "'";
        
        if (sess.get("scr")!=null)
            where += " AND TPSCR = '" + sess.get("scr") + "'";
            
        escl.setWhereClause(where);
        escl.executeQuery();
    }
    
    public void onDett(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfEsclusioneView1Iterator");
        ViewObject voSdo = voIter.getViewObject();
        Row cSdo = voSdo.getCurrentRow();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        Key rowKey = cSdo.getKey();
        session.put("navEsclusione", rowKey);
    }
}
