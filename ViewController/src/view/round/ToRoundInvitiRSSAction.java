package view.round;

import insiel.dataHandling.DateUtils;

import model.common.Round_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;

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

public class ToRoundInvitiRSSAction extends Parent_DataForwardAction {
    private Round_invitiRSSAction round_invitiRSSAction;
    private RichForm frmInvito;

    public void setRound_invitiRSSAction(Round_invitiRSSAction round_invitiRSSAction) {
        this.round_invitiRSSAction = round_invitiRSSAction;
    }

    public Round_invitiRSSAction getRound_invitiRSSAction() {
        return round_invitiRSSAction;
    }

    public void setFrmInvito(RichForm frmInvito) {
        this.frmInvito = frmInvito;
    }

    public RichForm getFrmInvito() {
        if(frmInvito==null)
            findForward();
        
        return frmInvito;
    }    
    
      protected void findForward()
      {
        
        Round_AppModule am=(Round_AppModule)BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo=am.findViewObject("Round_SoCentriPrelView1");
        String ulss=(String)ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr=(String)ADFContext.getCurrent().getSessionScope().get("scr");
        String elenco_centri=(String)ADFContext.getCurrent().getSessionScope().get("elenco_centri");
        ViewObjectFilters.filterCentri(vo,ulss,tpscr,DateUtils.getToday(), elenco_centri, "2");

          Round_invitiBean bean=(Round_invitiBean)BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
          // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
          if(bean.getCentro()!=null){
              Key _k = new Key(new Object[]{bean.getCentro()});
              RowSetIterator _iter = ViewHelper.getRowSetIterator(vo, "centro_iter");
              Row[] _r = _iter.findByKey(_k, 1);
              
              if(_r==null || _r.length==0){
                  bean.setCentro(null);
                  bean.setLivello(null);
              }
              
              _iter.closeRowSetIterator();
          }
          // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE


        vo=am.findViewObject("Round_SoMedicoView1");
        ViewObjectFilters.filterMedici(vo,ulss,DateUtils.getToday());
        
          // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
          /*if(bean.getMedico()!=null){
              Key _k = new Key(new Object[]{bean.getMedico()});
              RowSetIterator _iter = ViewHelper.getRowSetIterator(vo, "medici_iter");
              Row[] _r = _iter.findByKey(_k, 1);
              
              if(_r==null || _r.length==0){
                  bean.setMedico(null);
              }
              
              _iter.closeRowSetIterator();
          }*/
          // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE

        //per la RSS l'eta' e' fissa a 60   
        bean.setEta_da(ConfigurationConstants.ETA_PRIMO_INV_RSS);
        bean.setEta_a(ConfigurationConstants.ETA_PRIMO_INV_RSS);
                              
        try
        {
          AccessManager.checkPermission("SOGenerazioneInviti");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            handleException(ex.getMessage());
        }
        
          round_invitiRSSAction.prepareModel();
          round_invitiRSSAction.processUpdateModel();
      }

    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }
}
