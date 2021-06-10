package view.round;

import insiel.dataHandling.DateUtils;

import model.common.Round_AppModule;

import model.commons.AccessManager;

import model.commons.ViewHelper;

import model.datacontrol.Round_invitiBean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

public class ToRoundAltriInvitiAction extends Parent_DataForwardAction {
    private RichForm frmInvito;
    private Round_altriInvitiAction round_altriInvitiAction;

    public ToRoundAltriInvitiAction() {
    }

    public void setFrmInvito(RichForm frmInvito) {
        this.frmInvito = frmInvito;
    }

    public RichForm getFrmInvito() {
        if (frmInvito == null)
            findForward();

        return frmInvito;
    }

    protected void findForward() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoCentriPrelView1");
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String elenco_centri = (String) ADFContext.getCurrent().getSessionScope().get("elenco_centri");
        ViewObjectFilters.filterCentri(vo, ulss, tpscr, DateUtils.getToday(), elenco_centri);

        Round_invitiBean invitiBean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
        if(invitiBean.getCentro()!=null){
            RowSetIterator _iter = ViewHelper.getRowSetIterator(vo, "centro_iter");
            Row[] _r = _iter.getFilteredRows("Idcentro", invitiBean.getCentro());
            
            if(_r==null || _r.length==0){
                invitiBean.setCentro(null);
                invitiBean.setLivello(null);
            }
            
            _iter.closeRowSetIterator();
        }
        // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE
        try {
            AccessManager.checkPermission("SOGenerazioneInviti");
        } catch (Exception ex) {
            handleException(ex.getMessage());
        }

        round_altriInvitiAction.processUpdateModel();
    }

    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }

    public void setRound_altriInvitiAction(Round_altriInvitiAction round_altriInvitiAction) {
        this.round_altriInvitiAction = round_altriInvitiAction;
    }

    public Round_altriInvitiAction getRound_altriInvitiAction() {
        return round_altriInvitiAction;
    }
}
