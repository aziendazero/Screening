package model.round.client;
import oracle.jbo.client.remote.ViewUsageImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Round_SoSoggettiFilteredViewClient extends ViewUsageImpl implements model.round.common.Round_SoSoggettiFilteredView 
{
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Round_SoSoggettiFilteredViewClient()
  {
  }


    public String getInClause() {
        Object _ret = 
            getApplicationModuleProxy().riInvokeExportedMethod(this,"getInClause",null,null);
        return (String)_ret;
    }
}
