package model.client;

import oracle.jbo.client.remote.RowImpl;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Timestamp;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Agenda_AppRiassViewRowClient extends RowImpl {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Agenda_AppRiassViewRowClient()
  {
  }


    public Integer getIdinvito() {
        return (Integer)getAttribute("Idinvito");
    }

    public void setIdinvito(Integer value) {
        setAttribute("Idinvito", value);
    }

    public Date getDtapp() {
        return (Date)getAttribute("Dtapp");
    }

    public void setDtapp(Date value) {
        setAttribute("Dtapp", value);
    }

    public String getCtapp() {
        return (String)getAttribute("Ctapp");
    }

    public void setCtapp(String value) {
        setAttribute("Ctapp", value);
    }

    public Integer getIdcentroprelievo() {
        return (Integer)getAttribute("Idcentroprelievo");
    }

    public void setIdcentroprelievo(Integer value) {
        setAttribute("Idcentroprelievo", value);
    }

    public String getNome() {
        return (String)getAttribute("Nome");
    }

    public void setNome(String value) {
        setAttribute("Nome", value);
    }

    public String getCognome() {
        return (String)getAttribute("Cognome");
    }

    public void setCognome(String value) {
        setAttribute("Cognome", value);
    }

    public Date getDataNascita() {
        return (Date)getAttribute("DataNascita");
    }

    public void setDataNascita(Date value) {
        setAttribute("DataNascita", value);
    }

    public String getIdtpinvito() {
        return (String)getAttribute("Idtpinvito");
    }

    public void setIdtpinvito(String value) {
        setAttribute("Idtpinvito", value);
    }

    public String getCodts() {
        return (String)getAttribute("Codts");
    }

    public void setCodts(String value) {
        setAttribute("Codts", value);
    }

    public String getTel1() {
        return (String)getAttribute("Tel1");
    }

    public void setTel1(String value) {
        setAttribute("Tel1", value);
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

    public Timestamp getDtoraapp() {
        return (Timestamp)getAttribute("Dtoraapp");
    }

    public void setDtoraapp(Timestamp value) {
        setAttribute("Dtoraapp", value);
    }

    public String getCodesitoinvito() {
        return (String)getAttribute("Codesitoinvito");
    }

    public void setCodesitoinvito(String value) {
        setAttribute("Codesitoinvito", value);
    }
}
