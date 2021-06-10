package model;

import model.accettazione.AccCa_EsitoViewImpl;
import model.accettazione.AccCa_RicInvitiViewImpl;
import model.accettazione.AccCa_SelCentriViewImpl;
import model.accettazione.AccCa_SoInvitoViewImpl;

import model.commons.Parent_AppModuleImpl;

import model.global.A_SoCnfTpinvitoViewImpl;
import model.global.A_SoSoggScrViewImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class AccCa_AppModuleImpl extends Parent_AppModuleImpl implements model.AccCa_AppModule  
{
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public AccCa_AppModuleImpl()
  {
  }

  /**
   * 
   *  Sample main for debugging Business Components code using the tester.
   */
  public static void main(String[] args)
  {
    launchTester("model", "AccCa_AppModuleLocal");
  }

  /**
   * 
   *  Container's getter for AccCa_RicInvitiView1
   */
  public AccCa_RicInvitiViewImpl getAccCa_RicInvitiView1()
  {
    return (AccCa_RicInvitiViewImpl)findViewObject("AccCa_RicInvitiView1");
  }

  /**
   * 
   *  Container's getter for AccCa_SelCentriView1
   */
  public AccCa_SelCentriViewImpl getAccCa_SelCentriView1()
  {
    return (AccCa_SelCentriViewImpl)findViewObject("AccCa_SelCentriView1");
  }

  /**
   * 
   *  Container's getter for AccCa_EsitoView1
   */
  public AccCa_EsitoViewImpl getAccCa_EsitoView1()
  {
    return (AccCa_EsitoViewImpl)findViewObject("AccCa_EsitoView1");
  }

  /**
   * 
   *  Container's getter for AccCa_SoInvitoView1
   */
  public AccCa_SoInvitoViewImpl getAccCa_SoInvitoView1()
  {
    return (AccCa_SoInvitoViewImpl)findViewObject("AccCa_SoInvitoView1");
  }

  /**
   * 
   *  Container's getter for A_SoSoggScrView1
   */
  public A_SoSoggScrViewImpl getA_SoSoggScrView1()
  {
    return (A_SoSoggScrViewImpl)findViewObject("A_SoSoggScrView1");
  }

  /**
   * 
   *  Container's getter for A_SoCnfTpinvitoView1
   */
  public A_SoCnfTpinvitoViewImpl getA_SoCnfTpinvitoView1()
  {
    return (A_SoCnfTpinvitoViewImpl)findViewObject("A_SoCnfTpinvitoView1");
  }
}