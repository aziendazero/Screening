package model.client;

import oracle.jbo.client.remote.RowImpl;

//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Stats_SoCnfCategTpinvitoViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Stats_SoCnfCategTpinvitoViewRowClient()
  {
  }


    public Integer getIdcateg() {
        return (Integer)getAttribute("Idcateg");
    }

    public void setIdcateg(Integer value) {
        setAttribute("Idcateg", value);
    }

    public String getDescrizione() {
        return (String)getAttribute("Descrizione");
    }

    public void setDescrizione(String value) {
        setAttribute("Descrizione", value);
    }

    public String getTpscr() {
        return (String)getAttribute("Tpscr");
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }

    public Integer getLivello() {
        return (Integer)getAttribute("Livello");
    }

    public void setLivello(Integer value) {
        setAttribute("Livello", value);
    }

    public String getUlss() {
        return (String)getAttribute("Ulss");
    }

    public void setUlss(String value) {
        setAttribute("Ulss", value);
    }
}
