package model;

import model.commons.Parent_AppModuleImpl;

import model.medico.Med_ComuneViewImpl;
import model.medico.Med_ElencoMediciViewImpl;
import model.medico.Med_ElencoSoggViewImpl;
import model.medico.Med_ProvinceViewImpl;
import model.medico.Med_SoMedicoViewImpl;
import model.medico.Med_TemplateViewImpl;

import oracle.jbo.server.ViewLinkImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Med_AppModuleImpl extends Parent_AppModuleImpl implements model.common.Med_AppModule 
{
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Med_AppModuleImpl()
  {
  }

  /**
   * 
   *  Sample main for debugging Business Components code using the tester.
   */
  public static void main(String[] args)
  {
    launchTester("model", "Med_AppModuleLocal");
  }

  /**
     *
     *  Container's getter for Med_ElencoMediciView1
     */
    public Med_ElencoMediciViewImpl getMed_ElencoMediciView1()
  {
    return (Med_ElencoMediciViewImpl)findViewObject("Med_ElencoMediciView1");
  }

  /**
     *
     *  Container's getter for Med_ElencoSoggView1
     */
    public Med_ElencoSoggViewImpl getMed_ElencoSoggView1()
  {
    return (Med_ElencoSoggViewImpl)findViewObject("Med_ElencoSoggView1");
  }

  /**
     *
     *  Container's getter for Med_ElencoSoggView2
     */
    public Med_ElencoSoggViewImpl getMed_ElencoSoggView2()
  {
    return (Med_ElencoSoggViewImpl)findViewObject("Med_ElencoSoggView2");
  }

  /**
   *
   *  Container's getter for Med_SoggMedicoViewLink1
   */
    public ViewLinkImpl getMed_SoggMedicoViewLink1()
  {
        return (ViewLinkImpl)findViewLink("Med_SoggMedicoViewLink1");
    }

  protected void afterConnect()
  {
    // TODO:  Override this oracle.jbo.server.ApplicationModuleImpl method
    super.afterConnect();
    //getDBTransaction().executeCommand("ALTER SESSION SET SQL_TRACE TRUE");
  }

  /**
     *
     *  Container's getter for Med_SoMedicoView1
     */
    public Med_SoMedicoViewImpl getMed_SoMedicoView1()
  {
    return (Med_SoMedicoViewImpl)findViewObject("Med_SoMedicoView1");
  }

  /**
     *
     *  Container's getter for Med_ProvinceView1
     */
    public Med_ProvinceViewImpl getMed_ProvinceView1()
  {
    return (Med_ProvinceViewImpl)findViewObject("Med_ProvinceView1");
  }

  /**
     *
     *  Container's getter for Med_ComuneView1
     */
    public Med_ComuneViewImpl getMed_ComuneView1()
  {
    return (Med_ComuneViewImpl)findViewObject("Med_ComuneView1");
  }

  /**
     *
     *  Container's getter for Med_TemplateView1
     */
    public Med_TemplateViewImpl getMed_TemplateView1()
  {
    return (Med_TemplateViewImpl)findViewObject("Med_TemplateView1");
  }

}