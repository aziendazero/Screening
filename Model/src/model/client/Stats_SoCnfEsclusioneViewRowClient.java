package model.client;

import oracle.jbo.client.remote.RowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Stats_SoCnfEsclusioneViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Stats_SoCnfEsclusioneViewRowClient()
  {
  }


    public Integer getIdcnfescl() {
        return (Integer)getAttribute("Idcnfescl");
    }

    public void setIdcnfescl(Integer value) {
        setAttribute("Idcnfescl", value);
    }

    public String getDescrizione() {
        return (String)getAttribute("Descrizione");
    }

    public void setDescrizione(String value) {
        setAttribute("Descrizione", value);
    }

    public String getTpescl() {
        return (String)getAttribute("Tpescl");
    }

    public void setTpescl(String value) {
        setAttribute("Tpescl", value);
    }

    public Integer getNumgiorni() {
        return (Integer)getAttribute("Numgiorni");
    }

    public void setNumgiorni(Integer value) {
        setAttribute("Numgiorni", value);
    }

    public String getEsito() {
        return (String)getAttribute("Esito");
    }

    public void setEsito(String value) {
        setAttribute("Esito", value);
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

    public String getIdtpinvito() {
        return (String)getAttribute("Idtpinvito");
    }

    public void setIdtpinvito(String value) {
        setAttribute("Idtpinvito", value);
    }
}