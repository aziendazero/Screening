package model.client;

import oracle.jbo.client.remote.RowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Acc_SoAccCito2livViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Acc_SoAccCito2livViewRowClient()
  {
  }


    public Integer getIdacc2liv() {
        return (Integer) getAttribute("Idacc2liv");
    }

    public void setIdacc2liv(Integer value) {
        setAttribute("Idacc2liv", value);
    }

    public Integer getIdmot() {
        return (Integer) getAttribute("Idmot");
    }

    public void setIdmot(Integer value) {
        setAttribute("Idmot", value);
    }

    public String getDescrizione() {
        return (String)getAttribute("Descrizione");
    }

    public void setDescrizione(String value) {
        setAttribute("Descrizione", value);
    }

    public Integer getIdinvito() {
        return (Integer) getAttribute("Idinvito");
    }

    public void setIdinvito(Integer value) {
        setAttribute("Idinvito", value);
    }

    public String getNoteacc2liv() {
        return (String)getAttribute("Noteacc2liv");
    }

    public void setNoteacc2liv(String value) {
        setAttribute("Noteacc2liv", value);
    }

    public String getAltromotultint() {
        return (String)getAttribute("Altromotultint");
    }

    public void setAltromotultint(String value) {
        setAttribute("Altromotultint", value);
    }

    public String getTpscr() {
        return (String)getAttribute("Tpscr");
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }
}
