package model.client;

import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Stats_SoCnfRef2livRACDIAViewRowClient extends Stats_SoCnfRef2livCOLPESViewRowClient {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Stats_SoCnfRef2livRACDIAViewRowClient()
  {
  }


    public Integer getIdcnfref2l() {
        return (Integer)getAttribute("Idcnfref2l");
    }

    public void setIdcnfref2l(Integer value) {
        setAttribute("Idcnfref2l", value);
    }

    public String getDescrizione() {
        return (String)getAttribute("Descrizione");
    }

    public void setDescrizione(String value) {
        setAttribute("Descrizione", value);
    }

    public String getGruppo() {
        return (String)getAttribute("Gruppo");
    }

    public void setGruppo(String value) {
        setAttribute("Gruppo", value);
    }

    public Integer getCodreg() {
        return (Integer)getAttribute("Codreg");
    }

    public void setCodreg(Integer value) {
        setAttribute("Codreg", value);
    }

    public Integer getOrdine() {
        return (Integer)getAttribute("Ordine");
    }

    public void setOrdine(Integer value) {
        setAttribute("Ordine", value);
    }

    public Date getDtfinevalidita() {
        return (Date)getAttribute("Dtfinevalidita");
    }

    public void setDtfinevalidita(Date value) {
        setAttribute("Dtfinevalidita", value);
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
