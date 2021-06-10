package model.datacontrol;

import java.util.Date;

public class PrintBean {
    public PrintBean() {
        super();
    }

    private String tpinvito;
    private String tpStampa = "locale";
    private String cosaStampare = "lettere";
    private String nuovi;
    private Integer centro;
    private Date stampate_dal;
    private Date stampate_al;
    private String inviti_dal;
    private String inviti_al;
    private String cognome;
    private String nome;
    private String comune;
    private Date creato;
    private Integer etichetta;
    private Integer tpsugg;
    private String livello;
    private String livello_ref;
    private Date creato_al;
    private Date data_app;
    private Integer centro_ref;
    private String da_spedire = null;
    private int raccomandata = -1;
    private Integer inserto;
    /*20080704 MOD: filtro su intervallo data appuntamento*/
    private Date data_app_al;
    /*20080704 FINE MOD*/
    private Integer idStatoTrial;
    private Integer idBraccioTrial;
    private String testProposto;
    private String prelievoHpv;
    private String consensi;
    private String codClassePop;

    /**
     * reset dei parametri di ricerca, ma non delle impostazioni di stampa
     */
    public void reset() {
        tpinvito = null;
        nuovi = "tutti";
        centro = null;
        centro_ref = null;
        stampate_dal = null;
        stampate_al = null;
        inviti_dal = null;
        inviti_al = null;
        cognome = null;
        nome = null;
        comune = null;
        creato = null;
        creato_al = null;
        livello = "tutti";
        livello_ref = null;
        data_app = null;
        da_spedire = null;
        raccomandata = -1;
        /*20080704 MOD: filtro su intervallo data appuntamento*/
        data_app_al = null;
        /*20080704 FINE MOD*/
        idStatoTrial = null;
        idBraccioTrial = null;
        testProposto = "--";
        prelievoHpv = null;
        consensi = "qualsiasi valore";
        codClassePop = null;
    }

    public static Integer getInitialSugg() {
        return new Integer(-9999999);
    }

    public String getTpinvito() {
        return tpinvito;
    }

    public void setTpinvito(String tpinvito) {
        this.tpinvito = tpinvito;
    }

    public String getTpStampa() {
        return tpStampa;
    }

    public void setTpStampa(String tpStampa) {
        this.tpStampa = tpStampa;
    }

    public String getCosaStampare() {
        return cosaStampare;
    }

    public void setCosaStampare(String cosaStampare) {
        this.cosaStampare = cosaStampare;
    }

    public String getNuovi() {
        return nuovi;
    }

    public void setNuovi(String nuovi) {
        this.nuovi = nuovi;
    }

    public Integer getCentro() {
        return centro;
    }

    public void setCentro(Integer centro) {
        this.centro = centro;
    }

    public Date getStampate_dal() {
        return stampate_dal;
    }

    public void setStampate_dal(Date stampate_dal) {
        this.stampate_dal = stampate_dal;
    }

    public Date getStampate_al() {
        return stampate_al;
    }

    public void setStampate_al(Date stampate_al) {
        this.stampate_al = stampate_al;
    }

    public String getInviti_dal() {
        return inviti_dal;
    }

    public void setInviti_dal(String inviti_dal) {
        this.inviti_dal = inviti_dal;
    }

    public String getInviti_al() {
        return inviti_al;
    }

    public void setInviti_al(String inviti_al) {
        this.inviti_al = inviti_al;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getComune() {
        return comune;
    }

    public void setComune(String comune) {
        this.comune = comune;
    }

    public Date getCreato() {
        return creato;
    }

    public void setCreato(Date creato) {
        this.creato = creato;
    }

    public Integer getEtichetta() {
        return etichetta;
    }

    public void setEtichetta(Integer etichetta) {
        this.etichetta = etichetta;
    }

    public Integer getTpsugg() {
        return tpsugg;
    }

    public void setTpsugg(Integer tpsugg) {
        this.tpsugg = tpsugg;
    }

    public String getLivello() {
        return livello;
    }

    public void setLivello(String livello) {
        this.livello = livello;
    }

    public String getLivello_ref() {
        return livello_ref;
    }

    public void setLivello_ref(String livello_ref) {
        this.livello_ref = livello_ref;
    }

    public Date getCreato_al() {
        return creato_al;
    }

    public void setCreato_al(Date creato_al) {
        this.creato_al = creato_al;
    }

    public Date getData_app() {
        return data_app;
    }

    public void setData_app(Date data_app) {
        this.data_app = data_app;
    }

    public Integer getCentro_ref() {
        return centro_ref;
    }

    public void setCentro_ref(Integer centro_ref) {
        this.centro_ref = centro_ref;
    }

    public String getDa_spedire() {
        return da_spedire;
    }

    public void setDa_spedire(String da_spedire) {
        this.da_spedire = da_spedire;
    }

    public int getRaccomandata() {
        return raccomandata;
    }

    public void setRaccomandata(int raccomadata) {
        this.raccomandata = raccomadata;
    }

    public Integer getInserto() {
        return inserto;
    }

    public void setInserto(Integer inserto) {
        this.inserto = inserto;
    }

    /*20080704 MOD: filtro su intervallo data appuntamento*/
    public Date getData_app_al() {
        return data_app_al;
    }

    public void setData_app_al(Date data_app_al) {
        this.data_app_al = data_app_al;
    }
    /*20080704 FINE MOD*/

    public void setIdStatoTrial(Integer value) {
        idStatoTrial = value;
    }

    public Integer getIdStatoTrial() {
        return idStatoTrial;
    }

    public void setIdBraccioTrial(Integer value) {
        idBraccioTrial = value;
    }

    public Integer getIdBraccioTrial() {
        return idBraccioTrial;
    }


    public void setTestProposto(String testProposto) {
        this.testProposto = testProposto;
    }


    public String getTestProposto() {
        return testProposto;
    }


    public void setPrelievoHpv(String prelievoHpv) {
        this.prelievoHpv = prelievoHpv;
    }


    public String getPrelievoHpv() {
        return prelievoHpv;
    }


    public void setConsensi(String consensi) {
        this.consensi = consensi;
    }


    public String getConsensi() {
        return consensi;
    }

    public void setCodClassePop(String codClassePop) {
        this.codClassePop = codClassePop;
    }

    public String getCodClassePop() {
        return codClassePop;
    }
}
