package model.agenda.client;

import oracle.jbo.client.remote.RowImpl;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Timestamp;

public class Agenda_AppRiassViewRowClient extends RowImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public Agenda_AppRiassViewRowClient() {
    }

    public String getCodesitoinvito() {
        return (String) getAttribute("Codesitoinvito");
    }

    public String getCodts() {
        return (String) getAttribute("Codts");
    }

    public String getCognome() {
        return (String) getAttribute("Cognome");
    }

    public String getCtapp() {
        return (String) getAttribute("Ctapp");
    }

    public Date getDataNascita() {
        return (Date) getAttribute("DataNascita");
    }

    public Date getDtapp() {
        return (Date) getAttribute("Dtapp");
    }

    public Timestamp getDtoraapp() {
        return (Timestamp) getAttribute("Dtoraapp");
    }

    public Integer getIdcentroprelievo() {
        return (Integer) getAttribute("Idcentroprelievo");
    }

    public Integer getIdinvito() {
        return (Integer) getAttribute("Idinvito");
    }

    public String getIdtpinvito() {
        return (String) getAttribute("Idtpinvito");
    }

    public Integer getMinapp() {
        return (Integer) getAttribute("Minapp");
    }

    public String getNome() {
        return (String) getAttribute("Nome");
    }

    public Integer getOraapp() {
        return (Integer) getAttribute("Oraapp");
    }

    public String getTel1() {
        return (String) getAttribute("Tel1");
    }

    public String getTpscr() {
        return (String) getAttribute("Tpscr");
    }

    public String getUlss() {
        return (String) getAttribute("Ulss");
    }

    public void setCodesitoinvito(String value) {
        setAttribute("Codesitoinvito", value);
    }

    public void setCodts(String value) {
        setAttribute("Codts", value);
    }

    public void setCognome(String value) {
        setAttribute("Cognome", value);
    }

    public void setCtapp(String value) {
        setAttribute("Ctapp", value);
    }

    public void setDataNascita(Date value) {
        setAttribute("DataNascita", value);
    }

    public void setDtapp(Date value) {
        setAttribute("Dtapp", value);
    }

    public void setDtoraapp(Timestamp value) {
        setAttribute("Dtoraapp", value);
    }

    public void setIdcentroprelievo(Integer value) {
        setAttribute("Idcentroprelievo", value);
    }

    public void setIdinvito(Integer value) {
        setAttribute("Idinvito", value);
    }

    public void setIdtpinvito(String value) {
        setAttribute("Idtpinvito", value);
    }

    public void setMinapp(Integer value) {
        setAttribute("Minapp", value);
    }

    public void setNome(String value) {
        setAttribute("Nome", value);
    }

    public void setOraapp(Integer value) {
        setAttribute("Oraapp", value);
    }

    public void setTel1(String value) {
        setAttribute("Tel1", value);
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }

    public void setUlss(String value) {
        setAttribute("Ulss", value);
    }
}

