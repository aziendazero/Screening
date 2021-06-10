package model.commons;

import java.io.Serializable;

public class RispostaQuestionario implements Serializable {
    @SuppressWarnings("compatibility:-5219812343482683954")
    private static final long serialVersionUID = -6245017530258195709L;
    private String sezione;
    private long id;
    private String valore;


    public void setSezione(String sezione) {
        this.sezione = sezione;
    }


    public String getSezione() {
        return sezione;
    }


    public void setId(long id) {
        this.id = id;
    }


    public long getId() {
        return id;
    }


    public void setValore(String valore) {
        this.valore = valore;
    }


    public String getValore() {
        return valore;
    }
}
