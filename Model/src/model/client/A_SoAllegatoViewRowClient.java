package model.client;

import oracle.jbo.client.remote.RowImpl;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoAllegatoViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public A_SoAllegatoViewRowClient()
  {
  }


    public Integer getIdallegato() {
        return (Integer) getAttribute("Idallegato");
    }

    public void setIdallegato(Integer value) {
        setAttribute("Idallegato", value);
    }

    public Integer getIdinvito() {
        return (Integer) getAttribute("Idinvito");
    }

    public void setIdinvito(Integer value) {
        setAttribute("Idinvito", value);
    }

    public String getCodts() {
        return (String)getAttribute("Codts");
    }

    public void setCodts(String value) {
        setAttribute("Codts", value);
    }

    public BlobDomain getLettera() {
        return (BlobDomain)getAttribute("Lettera");
    }

    public void setLettera(BlobDomain value) {
        setAttribute("Lettera", value);
    }

    public Date getDtcreazione() {
        return (Date)getAttribute("Dtcreazione");
    }

    public void setDtcreazione(Date value) {
        setAttribute("Dtcreazione", value);
    }

    public Date getDtstampa() {
        return (Date)getAttribute("Dtstampa");
    }

    public void setDtstampa(Date value) {
        setAttribute("Dtstampa", value);
    }

    public Integer getStampapostel() {
        return (Integer) getAttribute("Stampapostel");
    }

    public void setStampapostel(Integer value) {
        setAttribute("Stampapostel", value);
    }

    public String getUlss() {
        return (String)getAttribute("Ulss");
    }

    public void setUlss(String value) {
        setAttribute("Ulss", value);
    }

    public String getTpscr() {
        return (String)getAttribute("Tpscr");
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }
}