package model.client;

import oracle.jbo.client.remote.RowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoAgendacentroViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Cnf_SoAgendacentroViewRowClient()
  {
  }


    public Integer getIdagenda() {
        return (Integer)getAttribute("Idagenda");
    }

    public void setIdagenda(Integer value) {
        setAttribute("Idagenda", value);
    }

    public Integer getIdcentro() {
        return (Integer)getAttribute("Idcentro");
    }

    public void setIdcentro(Integer value) {
        setAttribute("Idcentro", value);
    }

    public Integer getGgsettimana() {
        return (Integer)getAttribute("Ggsettimana");
    }

    public void setGgsettimana(Integer value) {
        setAttribute("Ggsettimana", value);
    }

    public Integer getOraapp() {
        return (Integer)getAttribute("Oraapp");
    }

    public void setOraapp(Integer value) {
        setAttribute("Oraapp", value);
    }

    public Integer getMinapp() {
        return (Integer)getAttribute("Minapp");
    }

    public void setMinapp(Integer value) {
        setAttribute("Minapp", value);
    }

    public Integer getDisporaria() {
        return (Integer)getAttribute("Disporaria");
    }

    public void setDisporaria(Integer value) {
        setAttribute("Disporaria", value);
    }
}
