package model.stats.client;

import oracle.jbo.client.remote.RowImpl;

public class Stats_SoCnfCategTpinvitoViewRowClient extends RowImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public Stats_SoCnfCategTpinvitoViewRowClient() {
    }

    public String getDescrizione() {
        return (String) getAttribute("Descrizione");
    }

    public Integer getIdcateg() {
        return (Integer) getAttribute("Idcateg");
    }

    public Integer getLivello() {
        return (Integer) getAttribute("Livello");
    }

    public String getTpscr() {
        return (String) getAttribute("Tpscr");
    }

    public String getUlss() {
        return (String) getAttribute("Ulss");
    }

    public void setDescrizione(String value) {
        setAttribute("Descrizione", value);
    }

    public void setIdcateg(Integer value) {
        setAttribute("Idcateg", value);
    }

    public void setLivello(Integer value) {
        setAttribute("Livello", value);
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }

    public void setUlss(String value) {
        setAttribute("Ulss", value);
    }
}

