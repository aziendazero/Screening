package view.statistiche;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.Locale;

import model.commons.AccessManager;

import model.datacontrol.Stats_paramBean;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import view.commons.action.Parent_DataForwardAction;

public class Stats_toInvitiEsitiAction extends Parent_DataForwardAction {
    private RichForm frmSearch;

    public Stats_toInvitiEsitiAction() {
    }

    public void setFrmSearch(RichForm frmSearch) {
        this.frmSearch = frmSearch;
    }

    public RichForm getFrmSearch() {
        if (frmSearch == null)
            findForward();
        return frmSearch;
    }

    public void findForward() {
        // Stats_AppModule am=(Stats_AppModule)BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        // String ulss=(String) ADFContext.getCurrent().getSessionScope()).getAttribute("ulss");
        //  String tpscr=(String) ADFContext.getCurrent().getSessionScope()).getAttribute("scr");
        Stats_paramBean bean =
            (Stats_paramBean) BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
        //Boolean regionale=(Boolean) ADFContext.getCurrent().getSessionScope()).getAttribute("regionale");

        try {
            //imposto l anavigazione verso la pagina corretta
            String tab = (String) ADFContext.getCurrent().getRequestScope().get("selectedTab");
            if (tab == null)
                bean.setStats_tab(0);

            else
                bean.setStats_tab(Integer.parseInt(tab));

        } catch (Exception e) {
            bean.setStats_tab(0);
        }

        try {
            Stats_helper.setAges();

        } catch (Exception ex) {
            this.handleException("Impostazione automatica dei limiti d'eta' non riuscita: " +
                                                    ex.getMessage());
        }
        try {
            AccessManager.checkPermission("SOStatULSS");

        } catch (Exception ex) {
            this.handleException(ex, null);

        }
        //20110914 Serra: disbailito le statistiche fino alle ore 13

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.ITALY);
        int ore = new Integer(sdf.format(d)).intValue();
        if (ore < 13)
            ADFContext.getCurrent().getSessionScope().put("stats_permission", Boolean.FALSE);
        else
             ADFContext.getCurrent().getSessionScope().put("stats_permission", Boolean.TRUE);
    }

    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }
}
