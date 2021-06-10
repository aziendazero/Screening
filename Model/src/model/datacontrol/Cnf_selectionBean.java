package model.datacontrol;

import java.util.List;


public class Cnf_selectionBean {
    private Integer centro;
    private String inClause;
    private String note;
    private String ulss = null;
    private Integer centro_f;
    private Integer centro1liv;
    private Integer centro2liv;
    private Integer tipoOperatore;
    private String ulss2;
    private String tipo_esame;
    private Integer anni_passati;
    private Integer userId;
    private Integer rowNum;

    public Cnf_selectionBean() {
    }

    public Integer getCentro() {
        return centro;
    }

    public void setCentro(Integer centro) {
        this.centro = centro;
    }

    public String getInClause() {
        return inClause;
    }

    public void setInClause(String inClause) {
        this.inClause = inClause;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUlss() {
        return ulss;
    }

    public void setUlss(String ulss) {
        this.ulss = ulss;
    }

    public Integer getCentro_f() {
        return centro_f;
    }

    public void setCentro_f(Integer centro_f) {
        this.centro_f = centro_f;
    }

    public Integer getCentro1liv() {
        return centro1liv;
    }

    public void setCentro1liv(Integer centro1liv) {
        this.centro1liv = centro1liv;
    }

    public Integer getCentro2liv() {
        return centro2liv;
    }

    public void setCentro2liv(Integer centro2liv) {
        this.centro2liv = centro2liv;
    }


    public void setTipoOperatore(Integer tipoOperatore) {
        this.tipoOperatore = tipoOperatore;
    }


    public Integer getTipoOperatore() {
        return tipoOperatore;
    }


    public void setUlss2(String ulss2) {
        this.ulss2 = ulss2;
    }


    public String getUlss2() {
        return ulss2;
    }


    public void setTipo_esame(String tipo_esame) {
        this.tipo_esame = tipo_esame;
    }


    public String getTipo_esame() {
        return tipo_esame;
    }


    public void setAnni_passati(Integer anni_passati) {
        this.anni_passati = anni_passati;
    }


    public Integer getAnni_passati() {
        return anni_passati;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getRowNum() {
        return rowNum;
    }
    
}
