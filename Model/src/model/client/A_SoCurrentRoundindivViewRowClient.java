package model.client;

import oracle.jbo.client.remote.RowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoCurrentRoundindivViewRowClient extends RowImpl 
{
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public A_SoCurrentRoundindivViewRowClient()
  {
  }

  public String getCodts()
  {
    return (String)getAttribute("Codts");
  }

  public void setCodts(String value)
  {
    setAttribute("Codts", value);
  }

  public Integer getCurrentPresence()
  {
    return (Integer)getAttribute("CurrentPresence");
  }

  public void setCurrentPresence(Integer value)
  {
    setAttribute("CurrentPresence", value);
  }
}