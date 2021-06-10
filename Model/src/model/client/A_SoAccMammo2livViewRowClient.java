package model.client;

import java.math.BigDecimal;

import oracle.jbo.client.remote.RowImpl;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoAccMammo2livViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public A_SoAccMammo2livViewRowClient()
  {
  }


    public Integer getIdaccma2liv() {
        return (Integer) getAttribute("Idaccma2liv");
    }

    public void setIdaccma2liv(Integer value) {
        setAttribute("Idaccma2liv", value);
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

    public String getNote() {
        return (String)getAttribute("Note");
    }

    public void setNote(String value) {
        setAttribute("Note", value);
    }

    public BigDecimal getCodRichiesta() {
        return (BigDecimal) getAttribute("CodRichiesta");
    }

    public void setCodRichiesta(BigDecimal value) {
        setAttribute("CodRichiesta", value);
    }
}
