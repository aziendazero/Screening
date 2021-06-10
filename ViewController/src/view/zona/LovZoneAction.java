package view.zona;

import java.util.Map;

import model.common.Cnf_AppModule;


import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;


import oracle.adf.share.ADFContext;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;



import view.backing.ParentBackingBean;

public class LovZoneAction extends ParentBackingBean {
    
    
    public String onLovFilter() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        Map sessionMap = adfCtx.getSessionScope();
        @SuppressWarnings("deprecation")
        Cnf_AppModule am =
            (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        String st = req.get("searchText") != null ? req.get("searchText").toString() : "";

        ViewObject vo = am.findViewObject("Cnf_SoDistrettoZonaView2");
        
        String ulss = (String) sessionMap.get("ulss");
        String whcl = "ULSS='"+ulss+"' AND UPPER(DESCRIZIONE) like UPPER('" + st + "%') ";
        vo.setWhereClause(whcl);
        vo.executeQuery();
        
        boolean onOneReturn = false;
        
        try{
            onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
        }catch(Throwable th){
            onOneReturn = false;
        }
        
        if(onOneReturn && vo.getEstimatedRowCount()==1){
            vo.setCurrentRow(vo.first());
            onLovSelect();
        } else 
            req.put("onOneReturn", false);
        
        return "searchcompleted";
    }

    /**
     * Crea ed inserisce una nuova associazione tra i comune da cui e' stata
     * scatenata la LOV e la zona selezionata
     * @param ctx
     */
    public String onLovSelect() {
        @SuppressWarnings("deprecation")
        Cnf_AppModule am =
            (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject vo1 = am.findViewObject("Cnf_SoDistrettoZonaView2").getViewObject();
        Row zona = vo1.getCurrentRow();
        ViewObject comzone = am.findViewObject("Cnf_SoComDistrettiZoneView2");

        try {

            if (zona == null)
                throw new Exception("Nessuna zona selezionata");

            Row r = comzone.createRow();
            comzone.insertRow(r);

            r.setAttribute("Coddistrzona", zona.getAttribute("Coddistrzona"));
            r.setAttribute("Ulss", zona.getAttribute("Ulss"));

            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            commitBinding.execute();
            
            comzone.executeQuery();
            
        } catch (Exception ex) {
            //am.doRollback("Cnf_SoComuneView1");


        }
        
        return "selected";
    }
}
