package model.client;

import oracle.jbo.client.remote.RowImpl;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class AccCo_SoAccColon1livViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public AccCo_SoAccColon1livViewRowClient()
  {
  }


    public Integer getIdaccco1liv() {
        return (Integer) getAttribute("Idaccco1liv");
    }

    public void setIdaccco1liv(Integer value) {
        setAttribute("Idaccco1liv", value);
    }

    public Integer getIdinvito() {
        return (Integer) getAttribute("Idinvito");
    }

    public void setIdinvito(Integer value) {
        setAttribute("Idinvito", value);
    }

    public Date getDtcreazione() {
        return (Date)getAttribute("Dtcreazione");
    }

    public void setDtcreazione(Date value) {
        setAttribute("Dtcreazione", value);
    }

    public String getOpcreazione() {
        return (String)getAttribute("Opcreazione");
    }

    public void setOpcreazione(String value) {
        setAttribute("Opcreazione", value);
    }

    public Date getDtultmod() {
        return (Date)getAttribute("Dtultmod");
    }

    public void setDtultmod(Date value) {
        setAttribute("Dtultmod", value);
    }

    public String getOpultmod() {
        return (String)getAttribute("Opultmod");
    }

    public void setOpultmod(String value) {
        setAttribute("Opultmod", value);
    }
}
