package model.client;

import oracle.jbo.client.remote.RowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Acc_SoInterventoPrecViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Acc_SoInterventoPrecViewRowClient()
  {
  }


    public Integer getIdintervento() {
        return (Integer)getAttribute("Idintervento");
    }

    public void setIdintervento(Integer value) {
        setAttribute("Idintervento", value);
    }

    public Integer getIdacc1liv() {
        return (Integer)getAttribute("Idacc1liv");
    }

    public void setIdacc1liv(Integer value) {
        setAttribute("Idacc1liv", value);
    }

    public String getTpscr() {
        return (String)getAttribute("Tpscr");
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }

    public Integer getAnnointervento() {
        return (Integer)getAttribute("Annointervento");
    }

    public void setAnnointervento(Integer value) {
        setAttribute("Annointervento", value);
    }
}
