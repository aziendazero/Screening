package model.pianolavoro;


import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class PL_SoEsclusioneDEFExtendedViewRowImpl extends ViewRowImpl {


    public static final int MAXUSAGECONST = 2;

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public PL_SoEsclusioneDEFExtendedViewRowImpl()
  {
  }

  /**
   * 
   *  Gets PL_SoEsclusione entity object.
   */
  public PL_SoEsclusioneImpl getPL_SoEsclusione()
  {
    return (PL_SoEsclusioneImpl)getEntity(0);
  }

  /**
   * 
   *  Gets PL_SoCnfEsclusione entity object.
   */
  public PL_SoCnfEsclusioneImpl getPL_SoCnfEsclusione()
  {
    return (PL_SoCnfEsclusioneImpl)getEntity(1);
  }


}
