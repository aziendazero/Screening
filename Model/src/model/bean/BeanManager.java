package model.bean;

import java.util.Iterator;
import java.util.Map;

import model.Cnf_AppModuleImpl;

import model.common.Cnf_SoAziendaViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.commons.beanutils.BeanUtils;

public class BeanManager {
    public BeanManager() {
        super();
    }
    
    Cnf_ulssParameters ulssParams = null;
    

    public Cnf_ulssParameters prepareParams (){
        
        BindingContext ctx = BindingContext.getCurrent();
        Cnf_AppModuleImpl am = (Cnf_AppModuleImpl) ctx.findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        
        ViewObject vo = am.getCnf_SoCnfPartemplateView2();
        ulssParams = new Cnf_ulssParameters();

        try {

            ulssParams.reset();

            Row r = vo.first();
            if (r != null) {
                do {
                    String name = (String) r.getAttribute("Nomepar");
                    String value = (String) r.getAttribute("Descrpar");
                    //imposto la bean property con quel nome al valore memorizzato
                    BeanUtils.setProperty(ulssParams, name, value);

                    r = vo.next();
                } while (r != null);


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return ulssParams;
    }
    
    public Cnf_ulssParameters saveParams(){
        
        BindingContext ctx = BindingContext.getCurrent();
        Cnf_AppModuleImpl am = (Cnf_AppModuleImpl) ctx.findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        ViewObject vo=am.findViewObject("Cnf_SoCnfPartemplateView2");
        ViewObject ulss = am.findViewObject("Cnf_SoAziendaView1");
        ADFContext adfCtx = ADFContext.getCurrent();
        String tpscr = (String) adfCtx.getSessionScope().get("scr");
        try {

            Cnf_SoAziendaViewRow u = (Cnf_SoAziendaViewRow) ulss.getCurrentRow();
            if (u == null)
                throw new Exception("Nessuna azienda sanitaria selezionata");


            Map map = BeanUtils.describe(ulssParams);
            Iterator iter = map.keySet().iterator();
            //per ogni proprietà del bean
            while (iter.hasNext()) {
                String property = (String) iter.next();
                String value = (String) map.get(property);

                if (property.equals("class") || property.equals("dirty"))
                    continue;
                //cerco la rig acon tale proprietà
                Row[] result = vo.getFilteredRows("Nomepar", property);
                if (result.length > 1)
                    throw new Exception("Ci sono troppi valori per il parametro " + property);

                if (result.length == 0) {
                    //se la riga non c'è e la proprietà ha un valore,
                    //creo un nuovo record per memorizzare questo valore
                    if (value != null && value.length() > 0) {
                        Row r = vo.createRow();
                        vo.insertRow(r);
                        r.setAttribute("Tpscr", tpscr);
                        r.setAttribute("Nomepar", property);
                        r.setAttribute("Descrpar", value);
                        r.setAttribute("Codaz", u.getCodaz());

                    }
                    continue;
                } else //se la riga c'è già..
                {
                    //controllo se il valore è nullo e va cancellata
                    if (value == null || value.length() == 0)
                        result[0].remove();
                    else { //altrimenti è sufficiente aggiornare il valore nel DB
                        result[0].setAttribute("Descrpar", value);
                    }
                }

            }
            iter.remove();


        } catch (Exception ex) {
            //   ex.printStackTrace();
            am.doRollback("Cnf_SoAziendaView1");

        }
        
        return ulssParams;
    }
    
}
