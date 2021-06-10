package model.datacontrol;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;
import java.util.Date;

import java.util.List;

import model.commons.ConfigurationConstants;

public final class Round_invitiBean {
    private Integer centro;
    private String comune;
    private Integer zona;
    private Integer medico;
    private int eta_da;
    private int eta_a;
    private Date max_data_richiamo;
    //invito per comune
    private String invita_per = "Comune";
    private Date generate_dal;
    private Date generate_al;
    private String chiInvitare = "tutti";
    private String esito = ConfigurationConstants.CODICE_ESITO_MANCATA_PRESENZA; //mancata presenza
    private String tpInvito = "-1";
    private String testProposto;
    private Integer livelloInvito;
    private String tpSollecito = "-1";
    private Date data_riferimento_eta;
    private Integer livesito;
    private String sesso = "entrambi";
    private String via;
    Date max_data_invito;
    private Integer centro_2;
    private Integer livello;
    private Integer per_indirizzo = new Integer(0);
    private String generate_daore;
    private String generate_aore;
    private String ordine;
    private Integer virtuale = new Integer(0);
    private String mod_chiamata = "Territoriale";
    private Integer rc_comune;
    private Date data_nascita_da;
    private Date data_nascita_a;
    private String cod_classe_pop;

    public void setData_nascita_da(Date data_nascita_da) {
        this.data_nascita_da = data_nascita_da;
    }

    public Date getData_nascita_da() {
        return data_nascita_da;
    }

    public void setData_nascita_a(Date data_nascita_a) {
        this.data_nascita_a = data_nascita_a;
    }

    public Date getData_nascita_a() {
        return data_nascita_a;
    }

    public void setCod_classe_pop(String cod_classe_pop) {
        this.cod_classe_pop = cod_classe_pop;
    }

    public String getCod_classe_pop() {
        return cod_classe_pop;
    }

    public Round_invitiBean() {
        //massima data di richiamo tra un mese
        Calendar c = DateUtils.getCalendar(new Date());
        c.add(Calendar.MONTH, 1);
        this.max_data_richiamo = c.getTime();
        //this.data_riferimento_eta=DateUtils.dateToString(c);
    }

    public Integer getCentro() {
        return centro;
    }

    public void setCentro(Integer centro) {
        this.centro = centro;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public Integer getZona() {
        return zona;
    }

    public void setZona(Integer zona) {
        this.zona = zona;
    }

    public Integer getMedico() {
        return medico;
    }

    public void setMedico(Integer medico) {
        this.medico = medico;
    }

    public int getEta_da() {
        return eta_da;
    }

    public void setEta_da(int eta_da) {
        this.eta_da = eta_da;
    }

    public int getEta_a() {
        return eta_a;
    }

    public void setEta_a(int eta_a) {
        this.eta_a = eta_a;
    }

    public Date getMax_data_richiamo() {
        return max_data_richiamo;
    }

    public void setMax_data_richiamo(Date max_data_richiamo) {
        this.max_data_richiamo = max_data_richiamo;
    }

    public String getInvita_per() {
        return invita_per;
    }

    public void setInvita_per(String invita_per) {
        this.invita_per = invita_per;
    }

    public Date getGenerate_dal() {
        return generate_dal;
    }

    public void setGenerate_dal(Date generate_dal) {
        this.generate_dal = generate_dal;
    }

    public Date getGenerate_al() {
        return generate_al;
    }

    public void setGenerate_al(Date generate_al) {
        this.generate_al = generate_al;
    }

    public String getChiInvitare() {
        return chiInvitare;
    }

    public void setChiInvitare(String chiInvitare) {
        this.chiInvitare = chiInvitare;
    }

    public String getEsito() {
        return esito;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    public String getTpInvito() {
        return tpInvito;
    }

    public void setTpInvito(String tpInvito) {
        this.tpInvito = tpInvito;
    }

    public Date getData_riferimento_eta() {
        return data_riferimento_eta;
    }

    public void setData_riferimento_eta(Date data_riferimento_eta) {
        this.data_riferimento_eta = data_riferimento_eta;
    }

    public Integer getLivesito() {
        return livesito;
    }

    public void setLivesito(Integer livesito) {
        this.livesito = livesito;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public Date getMax_data_invito() {
        return max_data_invito;
    }

    public void setMax_data_invito(Date max_data_invito) {
        this.max_data_invito = max_data_invito;
    }

    public Integer getCentro_2() {
        return centro_2;
    }

    public void setCentro_2(Integer centro_2) {
        this.centro_2 = centro_2;
    }

    public Integer getLivello() {
        return livello;
    }

    public void setLivello(Integer livello) {
        this.livello = livello;
    }

    public Integer getPer_indirizzo() {
        return per_indirizzo;
    }

    public void setPer_indirizzo(Integer per_indirizzo) {
        this.per_indirizzo = per_indirizzo;
    }


    public void setGenerate_daore(String generate_daore) {
        this.generate_daore = generate_daore;
    }


    public String getGenerate_daore() {
        return generate_daore;
    }


    public void setGenerate_aore(String generate_aore) {
        this.generate_aore = generate_aore;
    }


    public String getGenerate_aore() {
        return generate_aore;
    }


    public void setOrdine(String ordine) {
        this.ordine = ordine;
    }


    public String getOrdine() {
        return ordine;
    }


    public void setVirtuale(Integer virtuale) {
        this.virtuale = virtuale;
    }


    public Integer getVirtuale() {
        return virtuale;
    }


    public void setMod_chiamata(String mod_chiamata) {
        this.mod_chiamata = mod_chiamata;
    }


    public String getMod_chiamata() {
        return mod_chiamata;
    }


    public void setRc_comune(Integer rc_comune) {
        this.rc_comune = rc_comune;
    }


    public Integer getRc_comune() {
        return rc_comune;
    }


    public void setTestProposto(String testProposto) {
        this.testProposto = testProposto;
    }


    public String getTestProposto() {
        return testProposto;
    }


    public void setLivelloInvito(Integer livelloInvito) {
        this.livelloInvito = livelloInvito;
    }


    public Integer getLivelloInvito() {
        return livelloInvito;
    }


    public void setTpSollecito(String tpSollecito) {
        this.tpSollecito = tpSollecito;
    }


    public String getTpSollecito() {
        return tpSollecito;
    }

}
