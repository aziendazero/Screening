package model.accettazione.client;

import java.math.BigDecimal;

import oracle.jbo.client.remote.RowImpl;

public class Acc_SoAccCito2livViewRowClient extends RowImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public Acc_SoAccCito2livViewRowClient() {
    }

    public String getAltromotultint() {
        return (String) getAttribute("Altromotultint");
    }

    public BigDecimal getCodRichiesta() {
        return (BigDecimal) getAttribute("CodRichiesta");
    }

    public String getDescrizione() {
        return (String) getAttribute("Descrizione");
    }

    public Integer getIdacc2liv() {
        return (Integer) getAttribute("Idacc2liv");
    }

    public Integer getIdinvito() {
        return (Integer) getAttribute("Idinvito");
    }

    public Integer getIdmot() {
        return (Integer) getAttribute("Idmot");
    }

    public String getNoteacc2liv() {
        return (String) getAttribute("Noteacc2liv");
    }

    public String getTpscr() {
        return (String) getAttribute("Tpscr");
    }

    public void setAltromotultint(String value) {
        setAttribute("Altromotultint", value);
    }

    public void setCodRichiesta(BigDecimal value) {
        setAttribute("CodRichiesta", value);
    }

    public void setDescrizione(String value) {
        setAttribute("Descrizione", value);
    }

    public void setIdacc2liv(Integer value) {
        setAttribute("Idacc2liv", value);
    }

    public void setIdinvito(Integer value) {
        setAttribute("Idinvito", value);
    }

    public void setIdmot(Integer value) {
        setAttribute("Idmot", value);
    }

    public void setNoteacc2liv(String value) {
        setAttribute("Noteacc2liv", value);
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }
}

