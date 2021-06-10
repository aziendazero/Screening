package view.agenda;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.servlet.http.HttpServletRequest;

import model.Cnf_AppModuleImpl;

import model.commons.AgendaUtils;

import model.commons.ViewHelper;

import model.datacontrol.Agenda_Servizio;

import model.datacontrol.Cnf_selectionBean;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

@SuppressWarnings({ "deprecation", "unchecked" })
public class Ag_meseAction extends Parent_DataForwardAction {
    public Ag_meseAction() {
    }

    public void onCerca(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        try {
            AgendaUtils.queryMese();
        } catch (Exception ex) {
            this.handleException(ex, null);
        }
    }

    public void onPrev(ActionEvent actionEvent) {
        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
        int mese = bean.getMese().intValue();
        int anno = bean.getAnno().intValue();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, mese);
        cal.set(Calendar.YEAR, anno);
        cal.set(Calendar.DAY_OF_MONTH, 15);

        cal.add(Calendar.MONTH, -1);

        int meseNew = cal.get(Calendar.MONTH);
        int annoNew = cal.get(Calendar.YEAR);
        bean.setAnno(new Integer(annoNew));
        bean.setMese(new Integer(meseNew));
        this.onCerca(null);
    }

    public void onNext(ActionEvent actionEvent) {
        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
        int mese = bean.getMese().intValue();
        int anno = bean.getAnno().intValue();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, mese);
        cal.set(Calendar.YEAR, anno);
        cal.set(Calendar.DAY_OF_MONTH, 15);

        cal.add(Calendar.MONTH, 1);

        int meseNew = cal.get(Calendar.MONTH);
        int annoNew = cal.get(Calendar.YEAR);
        bean.setAnno(new Integer(annoNew));
        bean.setMese(new Integer(meseNew));
        this.onCerca(null);
    }

    public String onSelgg() {
        try {
            Map req = ADFContext.getCurrent().getRequestScope();
            Integer gg = (Integer) req.get("giorno");
            //mod20071113
            int giorno = 1;
            if (gg == null || gg == 0) {
                System.out.println("FOUND_ERROR: parametro giorno non valorizzato nella request");

            } else
                giorno = gg;
            
            Agenda_Servizio bean =
                (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
            int mese = bean.getMese().intValue();
            int anno = bean.getAnno().intValue();
            java.util.Date dtAg = AgendaUtils.getDataGMA(giorno, mese, anno);
            Map sess = ADFContext.getCurrent().getSessionScope();
            sess.put("dtAgenda", dtAg);

            return "ag_preGiorn";
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException(ex, null);
        }
        return null;
    }
    
    public String onConfigurazione() {
        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
        Integer idCt = bean.getIdCentro();
        
        if (idCt != null){
            Cnf_selectionBean beanCFG = (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            beanCFG.setCentro(idCt); 
            Map session = ADFContext.getCurrent().getSessionScope();
            session.put("cnf_setCentro",Boolean.valueOf(true));
            
            Cnf_AppModuleImpl cnfAm =
                (Cnf_AppModuleImpl) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
            
            Key k = new Key(new Object[]{idCt});
            ViewObject voIter = cnfAm.findViewObject("Cnf_SoCentroPrelRefView1");
            ViewHelper.setCurrent(voIter, k);
        }
        
        return "agenda_centro";
    }

    protected void setAppModule() {
        this.amName = "Agenda_AppModule";
    }

    protected void beforeNavigate(String dest) throws Exception {
        AgendaUtils.beforeNav(dest);
        if (dest.equals("ag_preGiorn")) {
            Agenda_Servizio bean =
                (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
            int mese = bean.getMese().intValue();
            int anno = bean.getAnno().intValue();
            java.util.Date dtAg = AgendaUtils.getDataGMA(1, mese, anno);
            Map sess = ADFContext.getCurrent().getSessionScope();
            sess.put("dtAgenda", dtAg);
        }
    }
}
