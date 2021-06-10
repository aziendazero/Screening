package view.round;

import java.util.Map;

import model.common.Round_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.ViewObject;

public class Round_dettagliInvitiAction extends Round_DataForwardAction 
{
    private RichForm frmDetails;

    protected  String getElenco_voName()
  {
      ADFContext adfCtx = ADFContext.getCurrent();
      Map sess = adfCtx.getSessionScope();
    Boolean hpvAttivo = (Boolean)sess.get("HPV");
    if (hpvAttivo.booleanValue())
    {
      return "Round_SoConteggiInvitiView1";
    }
		return "Round_SoSoggettiFilteredView1";
  }

  protected void findForward()
  {

    super.onSetInClause();
    //super.findForward();
  }
  
  protected void beforeNavigate(String dest) throws Exception
  {
    
    if(dest!=null && !dest.startsWith("goDetails"))
    {
      //poiche' ho ripopolato la cache, se esco dall apagina devo ripulirla
     Round_AppModule am=(Round_AppModule)BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
      ViewObject vo=am.findViewObject(this.getElenco_voName());
      vo.clearCache();
    }
  }
  
  public String onBack()
  {
    //poiche' ho ripopolato la cache, se esco dall apagina devo ripulirla
     Round_AppModule am=(Round_AppModule)BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
      ViewObject vo=am.findViewObject(this.getElenco_voName());
      vo.clearCache();
      return "back";
  }


    public void setFrmDetails(RichForm frmDetails) {
        this.frmDetails = frmDetails;
    }

    public RichForm getFrmDetails() {
        if(frmDetails==null)
            findForward();
        
        return frmDetails;
    }
}
