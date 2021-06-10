package model.datasources;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashMap;

import model.commons.ViewHelper;

import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;

public class Stats_datasource {
    private Date periodo_dal;
    private Date periodo_al;
    private BigDecimal cod_distretto;
    private String nome_distretto;
    private String cod_comune;
    private String nome_comune;
    private BigDecimal cod_centro;
    private String nome_centro;
    private Integer eta_da;
    private Integer eta_a;
    private String round_indiv;
    private String round_org;
    private String accesso;
    private String ulss;
    private String tpscr;
    private String indicatore;
    private String tipo_escl;
    private String formato;
    private String tipo_operatore;
    private Integer livello;

    private Object[] risultati;
    private final Long id = new Long(System.currentTimeMillis());
    private Integer max_column_used;
    private HashMap headers;
    private HashMap render;
    private Boolean computeTotals;
    private String coorte;
    private String sesso;
    /*20080704 MOD: aggiunta round inviti ai filtri*/
    private String round_inviti;
    /*20080704 FINE MOD*/


    public Stats_datasource() {
    }

    public Date getPeriodo_dal() {
        return periodo_dal;
    }

    public void setPeriodo_dal(Date periodo_dal) {
        this.periodo_dal = periodo_dal;
    }

    public Date getPeriodo_al() {
        return periodo_al;
    }

    public void setPeriodo_al(Date periodo_al) {
        this.periodo_al = periodo_al;
    }

    public BigDecimal getCod_distretto() {
        return cod_distretto;
    }

    public void setCod_distretto(BigDecimal cod_distretto) {
        this.cod_distretto = cod_distretto;
    }

    public String getNome_distretto() {
        return nome_distretto;
    }

    public void setNome_distretto(String nome_distretto) {
        this.nome_distretto = nome_distretto;
    }

    public String getCod_comune() {
        return cod_comune;
    }

    public void setCod_comune(String cod_comune) {
        this.cod_comune = cod_comune;
    }

    public String getNome_comune() {
        return nome_comune;
    }

    public void setNome_comune(String nome_comune) {
        this.nome_comune = nome_comune;
    }

    public BigDecimal getCod_centro() {
        return cod_centro;
    }

    public void setCod_centro(BigDecimal cod_centro) {
        this.cod_centro = cod_centro;
    }

    public String getNome_centro() {
        return nome_centro;
    }

    public void setNome_centro(String nome_centro) {
        this.nome_centro = nome_centro;
    }

    public Integer getEta_da() {
        return eta_da;
    }

    public void setEta_da(Integer eta_da) {
        this.eta_da = eta_da;
    }

    public void setEta_da(BigDecimal eta_da) {
        this.eta_da = new Integer(eta_da.intValue());
    }

    public void setEta_da(int eta_da) {
        this.eta_da = new Integer(eta_da);
    }

    public Integer getEta_a() {
        return eta_a;
    }

    public void setEta_a(Integer eta_a) {
        this.eta_a = eta_a;
    }

    public void setEta_a(BigDecimal eta_a) {
        this.eta_a = new Integer(eta_a.intValue());
    }

    public void setEta_a(int eta_a) {
        this.eta_a = new Integer(eta_a);
    }

    public String getRound_indiv() {
        return round_indiv;
    }

    public void setRound_indiv(String round_indiv) {
        this.round_indiv = round_indiv;
    }

    public String getRound_org() {
        return round_org;
    }

    public void setRound_org(String round_org) {
        this.round_org = round_org;
    }

    public String getAccesso() {
        return accesso;
    }

    public void setAccesso(String accesso) {
        this.accesso = accesso;
    }

    public String getUlss() {
        return ulss;
    }

    public void setUlss(String ulss) {
        this.ulss = ulss;
    }

    public String getTpscr() {
        return (String) ViewHelper.decodeByTpscr(tpscr, "Cervicale", "Colon-retto", "Mammografico", "Cardiovascolare",
                                                 "PFAS");
    }

    public void setTpscr(String tpscr) {
        this.tpscr = tpscr;
    }

    public String getIndicatore() {
        return indicatore;
    }

    public void setIndicatore(String indicatore) {
        this.indicatore = indicatore;
    }

    public String getTipo_escl() {
        return tipo_escl;
    }

    public void setTipo_escl(String tipo_escl) {
        this.tipo_escl = tipo_escl;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getTipo_operatore() {
        return tipo_operatore;
    }

    public void setTipo_operatore(String tipo_operatore) {
        this.tipo_operatore = tipo_operatore;
    }

    public Integer getLivello() {
        return livello;
    }

    public void setLivello(Integer livello) {
        this.livello = livello;
    }

    public void setLivello(int livello) {
        this.livello = new Integer(livello);
    }

    /* public JRBeanArrayDataSource getRisultati()
    {
      return new JRBeanArrayDataSource(risultati);
    }*/


    public void setRisultati(Object[] risultati) {
        this.risultati = risultati;
    }

    public Long getId() {
        return id;
    }

    public Integer getMax_column_used() {
        return max_column_used;
    }

    public void setMax_column_used(Integer max_column_used) {
        this.max_column_used = max_column_used;
    }

    public void setMax_column_used(int max_column_used) {
        this.max_column_used = new Integer(max_column_used);
    }

    public HashMap getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap headers) {
        this.headers = headers;
    }

    public HashMap getRender() {
        return render;
    }

    public void setRender(HashMap render) {
        this.render = render;
    }

    public Boolean isComputeTotals() {
        return computeTotals;
    }

    public Boolean getComputeTotals() {
        return computeTotals;
    }

    public void setComputeTotals(Boolean computeTotals) {
        this.computeTotals = computeTotals;
    }

    public JRBeanArrayDataSource getRisultati_1() {
        return new JRBeanArrayDataSource(risultati);
    }

    public JRBeanArrayDataSource getRisultati_2() {
        return new JRBeanArrayDataSource(risultati);
    }

    public JRBeanArrayDataSource getRisultati_3() {
        return new JRBeanArrayDataSource(risultati);
    }

    public JRBeanArrayDataSource getRisultati_4() {
        return new JRBeanArrayDataSource(risultati);
    }

    public String getCoorte() {
        return coorte;
    }

    public void setCoorte(String coorte) {
        this.coorte = coorte;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }
    /*20080704 MOD: aggiunta round inviti ai filtri*/
    public String getRound_inviti() {
        return round_inviti;
    }

    public void setRound_inviti(String round_inviti) {
        this.round_inviti = round_inviti;
    }
    /*20080704 FINE MOD*/
}
