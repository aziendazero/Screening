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

public class ToRoundSollecitiAction extends Parent_DataForwardAction {
    private RichForm frmInvito;
    private Round_sollecitiAction round_sollecitiAction;

    public void setRound_sollecitiAction(Round_sollecitiAction round_sollecitiAction) {
        this.round_sollecitiAction = round_sollecitiAction;
    }

    public Round_sollecitiAction getRound_sollecitiAction() {
        return round_sollecitiAction;
    }

    public ToRoundSollecitiAction() {
    }

    public void setFrmInvito(RichForm frmInvito) {
        this.frmInvito = frmInvito;
    }

    public RichForm getFrmInvito() {
        if (frmInvito == null)
            findForward();

        return frmInvito;
    }

    @Override
    protected void setAppModule() {
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
        
        vo = am.findViewObject("Round_SoCentriPrelView2");
        ViewObjectFilters.filterCentri(vo, ulss, tpscr, null, elenco_centri);
      
        // se centro_2 e' null vuol dire che o non era stato selezionato oppure non l'ho trovato nel set dei valori possibili
        // --> verifico se posso valorizzarlo con il valore di centro
        if(invitiBean.getCentro_2()==null && invitiBean.getCentro()!=null){
            Key _k = new Key(new Object[]{invitiBean.getCentro()});
            RowSetIterator _iter = ViewHelper.getRowSetIterator(vo, "centro_iter");
            Row[] _r = _iter.findByKey(_k, 1);
            
            if(_r==null || _r.length==0){
                invitiBean.setCentro_2(null);
                invitiBean.setLivello(null);
            } else {
                invitiBean.setCentro_2((Integer)_r[0].getAttribute("Idcentro"));
                invitiBean.setLivello((Integer)_r[0].getAttribute("Tipocentro"));
            }
            
            _iter.closeRowSetIterator();
        }

        // se centro_2 e' ancora null --> lo valorizzo con il primo elemento tra quelli possibili
        if(invitiBean.getCentro_2()==null && vo.getEstimatedRowCount()>0){
            invitiBean.setCentro_2((Integer)vo.first().getAttribute("Idcentro"));
            invitiBean.setLivello((Integer)vo.first().getAttribute("Tipocentro"));
        }
                   
        // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE
        try {
            AccessManager.checkPermission("SOGenerazioneInviti");
        } catch (Exception ex) {
            ex.printStackTrace();
            handleException(ex.getMessage());
        }

        round_sollecitiAction.processUpdateModel();
    }
}
