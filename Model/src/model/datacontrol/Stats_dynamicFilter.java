package model.datacontrol;

import java.util.Date;

public class Stats_dynamicFilter {
    //filtri globali
    int eta_da;
    int eta_a;
    String comune;
    Integer zona;
    Date periodo_dal;
    Date periodo_al;
    String round_indiv;
    String round_comune;
    String accesso = "Tutti";

    String comune_desc;
    String zona_desc;

    int filtro_1 = 0;
    int filtro_2 = 0;

    /*20080709 MOD: aggiunta round inviti ai filtri*/
    private String round_inviti;
    /*20080709 FINE MOD*/
    /*20080710 MOD: aggiungi filtri CCR*/
    Integer alto_rischio;
    /*20080710 FINE MOD*/


    public Stats_dynamicFilter() {
    }


    public void resetAll() {
        comune = null;
        zona = null;
        periodo_dal = null;
        periodo_al = null;
        round_indiv = null;
        round_comune = null;
        accesso = "Tutti";

        comune_desc = null;
        zona_desc = null;

        filtro_1 = 0;
        this.resetFilter1();
        filtro_2 = 0;
        this.resetFilter2();

        /*20080709 MOD: aggiunta round inviti ai filtri*/
        round_inviti = null;
        /*20080709 FINE MOD*/
        /*20080710 MOD: aggiungi filtri CCR*/
        alto_rischio = null;
        /*20080710 FINE MOD*/


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

    public String getRound_indiv() {
        return round_indiv;
    }

    public void setRound_indiv(String round_indiv) {
        this.round_indiv = round_indiv;
    }

    public String getRound_comune() {
        return round_comune;
    }

    public void setRound_comune(String round_comune) {
        this.round_comune = round_comune;
    }

    public String getComune_desc() {
        return comune_desc;
    }

    public void setComune_desc(String comune) {
        this.comune_desc = comune;
    }

    public String getZona_desc() {
        return zona_desc;
    }

    public void setZona_desc(String zona) {
        this.zona_desc = zona;
    }


    public int getFiltro_1() {
        return filtro_1;
    }

    public void setFiltro_1(int filtro_1) {
        this.filtro_1 = filtro_1;
    }

    public int getFiltro_2() {
        return filtro_2;
    }

    public void setFiltro_2(int filtro_2) {
        this.filtro_2 = filtro_2;
    }

    public String getAccesso() {
        return accesso;
    }

    public void setAccesso(String accesso) {
        this.accesso = accesso;
    }

    /*20080709 MOD: aggiunta round inviti ai filtri*/

    public String getRound_inviti() {
        return round_inviti;
    }

    public void setRound_inviti(String round_inviti) {
        this.round_inviti = round_inviti;
    }
    /*20080709 FINE MOD*/

    /*20080710 MOD: aggiungi filtri CCR*/

    public Integer getAlto_rischio() {
        return alto_rischio;
    }

    public void setAlto_rischio(Integer alto_rischio) {
        this.alto_rischio = alto_rischio;
    }

    /*20080710 FINE MOD*/


    /*************** FILTRO 1************************/
    String tipo_invito;
    Integer centro_invito;
    String tipo_richiamo;
    int livello;
    String completo;
    Integer adepre;
    Integer giudia;
    Integer positi;
    Integer idsugg1l;
    Integer colpvl;
    Integer colpes;
    Integer racdia;
    Integer idsugg2l;
    String intchiusi;
    Integer tipo_int;
    Integer idsugg3l;
    String esito;
    Integer intval;

    String tipo_invito_desc;
    String centro_invito_desc;
    String tipo_richiamo_desc;
    String adepre_desc;
    String giudia_desc;
    String positi_desc;
    String idsugg1l_desc;
    String colpvl_desc;
    String colpes_desc;
    String racdia_desc;
    String idsugg2l_desc;
    String tipo_int_desc;
    String idsugg3l_desc;
    String esito_desc;
    String intval_desc;


    String rxcolon;
    String endo_estensione;
    Integer endo_complicanze;
    String endo_complicanze_desc;
    Integer conclusioni2l;
    String conclusioni2l_desc;
    Integer rx_conclusioni;
    String rx_conclusioni_desc;
    Integer diagnosi_co;
    String diagnosi_co_desc;
    boolean invitati;
    boolean esclusi;
    String tpescl;
    Integer procedura;
    Integer motivo;
    String motivo_desc;
    Integer pn;
    Integer pt;
    Integer pm;
    String pn_desc;
    String pt_desc;
    String pm_desc;
    Integer mx_esito;
    String mx_esito_desc;
    Integer mammo;
    Integer eco;
    Integer clinico;
    Integer citologia;
    Integer agobiopsia;
    String citologia_desc;
    String agobiopsia_desc;
    Integer ascella;
    String ascella_desc;
    Integer esito_cito;
    String esito_cito_desc;
    Integer esito_agob;
    String esito_agob_desc;

    /*20080710 MOD: aggiunta categoria inviti*/
    Integer cat_invito;
    String cat_invito_desc;
    /*20080710 FINE MOD*/
    /*20080710 MOD: aggiungi filtri CCR*/
    Integer quantita;
    /*20080710 FINE MOD*/
    /*20081210 MOD VERSIONE 1.2*/
    Integer ist_bio;
    Integer ist_bio_diagnosi;
    String ist_bio_diagnosi_desc;
    Integer ist_chir;
    Integer ist_chir_diagnosi;
    String ist_chir_diagnosi_desc;
    Integer lesione_hpv;
    Integer inalim;
    String inalim_desc;
    /* FINE MOD 20081210*/
    //20110714
    boolean referto;

    public void resetFilter1() {
        tipo_invito = null;
        centro_invito = null;
        tipo_richiamo = null;
        livello = -1;
        completo = null;
        adepre = null;
        giudia = null;
        positi = null;
        idsugg1l = null;
        colpvl = null;
        colpes = null;
        racdia = null;
        idsugg2l = null;
        intchiusi = null;
        tipo_int = null;
        idsugg3l = null;
        esito = null;
        intval = null;
        rxcolon = null;
        endo_estensione = null;
        endo_complicanze = null;
        conclusioni2l = null;
        invitati = true;
        esclusi = false;
        tpescl = null;
        procedura = null;
        motivo = null;
        pn = null;
        pt = null;
        pm = null;

        tipo_invito_desc = null;
        centro_invito_desc = null;
        tipo_richiamo_desc = null;
        adepre_desc = null;
        giudia_desc = null;
        positi_desc = null;
        idsugg1l_desc = null;
        colpvl_desc = null;
        colpes_desc = null;
        racdia_desc = null;
        idsugg2l_desc = null;
        tipo_int_desc = null;
        idsugg3l_desc = null;
        esito_desc = null;
        intval_desc = null;
        endo_complicanze_desc = null;
        conclusioni2l_desc = null;
        rx_conclusioni = null;
        rx_conclusioni_desc = null;
        diagnosi_co = null;
        diagnosi_co_desc = null;
        motivo_desc = null;
        pn_desc = null;
        pt_desc = null;
        pm_desc = null;

        mx_esito = null;
        mx_esito_desc = null;
        mammo = null;
        eco = null;
        clinico = null;
        citologia = null;
        agobiopsia = null;
        citologia_desc = null;
        agobiopsia_desc = null;
        ascella = null;
        ascella_desc = null;

        esito_cito = null;
        esito_cito_desc = null;
        esito_agob = null;
        esito_agob_desc = null;

        /*20080710 MOD: aggiunta categoria inviti*/
        cat_invito = null;
        cat_invito_desc = null;
        /*20080710 FINE MOD*/
        /*20080710 MOD: aggiungi filtri CCR*/
        quantita = null;
        /*20080710 FINE MOD*/
        //20110714
        referto = false;

        /*20081210 MOD VERSIONE 1.2*/
        ist_bio = null;
        ist_bio_diagnosi = null;
        ist_chir = null;
        ist_chir_diagnosi = null;
        lesione_hpv = null;
        ist_bio_diagnosi_desc = null;
        ist_chir_diagnosi_desc = null;
        inalim = null;
        inalim_desc = null;
        /* FINE MOD 20081210*/
    }

    public Integer getPn() {
        return pn;
    }

    public void setPn(Integer pn) {
        this.pn = pn;
    }

    public Integer getPt() {
        return pt;
    }

    public void setPt(Integer pt) {
        this.pt = pt;
    }

    public Integer getPm() {
        return pm;
    }

    public void setPm(Integer pm) {
        this.pm = pm;
    }

    public String getPn_desc() {
        return pn_desc;
    }

    public void setPn_desc(String pn_desc) {
        this.pn_desc = pn_desc;
    }

    public String getPt_desc() {
        return pt_desc;
    }

    public void setPt_desc(String pt_desc) {
        this.pt_desc = pt_desc;
    }

    public String getPm_desc() {
        return pm_desc;
    }

    public void setPm_desc(String pm_desc) {
        this.pm_desc = pm_desc;
    }


    public Integer getMotivo() {
        return motivo;
    }

    public void setMotivo(Integer motivo) {
        this.motivo = motivo;
    }


    public String getMotivo_desc() {
        return motivo_desc;
    }

    public void setMotivo_desc(String motivo_desc) {
        this.motivo_desc = motivo_desc;
    }


    public Integer getProcedura() {
        return procedura;
    }

    public void setProcedura(Integer procedura) {
        this.procedura = procedura;
    }

    public String getTpescl() {
        return tpescl;
    }

    public void setTpescl(String tpescl) {
        this.tpescl = tpescl;
    }

    public String getTipo_invito() {
        return tipo_invito;
    }

    public void setTipo_invito(String tipo_invito) {
        this.tipo_invito = tipo_invito;
    }
    /*20080710 MOD: aggiunta categoria invito*/

    public Integer getCat_invito() {
        return cat_invito;
    }

    public void setCat_invito(Integer cat_invito) {
        this.cat_invito = cat_invito;
    }

    public String getCat_invito_desc() {
        return cat_invito_desc;
    }

    public void setCat_invito_desc(String cat_invito_desc) {
        this.cat_invito_desc = cat_invito_desc;
    }
    /*20080710 FINE MOD*/

    public Integer getCentro_invito() {
        return centro_invito;
    }

    public void setCentro_invito(Integer centro_invito) {
        this.centro_invito = centro_invito;
    }


    public String getTipo_richiamo() {
        return tipo_richiamo;
    }

    public void setTipo_richiamo(String tipo_richiamo) {
        this.tipo_richiamo = tipo_richiamo;
    }

    public int getLivello() {
        return livello;
    }

    public void setLivello(int livello) {
        this.livello = livello;
    }

    public String getCompleto() {
        return completo;
    }

    public void setCompleto(String completo) {
        this.completo = completo;
    }

    public Integer getAdepre() {
        return adepre;
    }

    public void setAdepre(Integer adepre) {
        this.adepre = adepre;
    }

    public Integer getGiudia() {
        return giudia;
    }

    public void setGiudia(Integer giudia) {
        this.giudia = giudia;
    }

    public Integer getPositi() {
        return positi;
    }

    public void setPositi(Integer positi) {
        this.positi = positi;
    }

    public Integer getIdsugg1l() {
        return idsugg1l;
    }

    public void setIdsugg1l(Integer idsugg1l) {
        this.idsugg1l = idsugg1l;
    }

    public Integer getColpvl() {
        return colpvl;
    }

    public void setColpvl(Integer colpvl) {
        this.colpvl = colpvl;
    }

    public Integer getColpes() {
        return colpes;
    }

    public void setColpes(Integer colpes) {
        this.colpes = colpes;
    }

    public Integer getRacdia() {
        return racdia;
    }

    public void setRacdia(Integer racdia) {
        this.racdia = racdia;
    }

    public Integer getIdsugg2l() {
        return idsugg2l;
    }

    public void setIdsugg2l(Integer idsugg2l) {
        this.idsugg2l = idsugg2l;
    }

    public String getIntchiusi() {
        return intchiusi;
    }

    public void setIntchiusi(String intchiusi) {
        this.intchiusi = intchiusi;
    }

    public Integer getTipo_int() {
        return tipo_int;
    }

    public void setTipo_int(Integer tipo_int) {
        this.tipo_int = tipo_int;
    }

    public Integer getIdsugg3l() {
        return idsugg3l;
    }

    public void setIdsugg3l(Integer idsugg3l) {
        this.idsugg3l = idsugg3l;
    }

    public String getEsito() {
        return esito;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }


    public Integer getIntval() {
        return intval;
    }

    public void setIntval(Integer intval) {
        this.intval = intval;
    }


    public String getTipo_invito_desc() {
        return tipo_invito_desc;
    }

    public void setTipo_invito_desc(String tipo_invito) {
        this.tipo_invito_desc = tipo_invito;
    }

    public String getCentro_invito_desc() {
        return centro_invito_desc;
    }

    public void setCentro_invito_desc(String centro_invito) {
        this.centro_invito_desc = centro_invito;
    }


    public String getTipo_richiamo_desc() {
        return tipo_richiamo_desc;
    }

    public void setTipo_richiamo_desc(String tipo_richiamo) {
        this.tipo_richiamo_desc = tipo_richiamo;
    }


    public String getAdepre_desc() {
        return adepre_desc;
    }

    public void setAdepre_desc(String adepre) {
        this.adepre_desc = adepre;
    }

    public String getGiudia_desc() {
        return giudia_desc;
    }

    public void setGiudia_desc(String giudia) {
        this.giudia_desc = giudia;
    }

    public String getPositi_desc() {
        return positi_desc;
    }

    public void setPositi_desc(String positi) {
        this.positi_desc = positi;
    }

    public String getIdsugg1l_desc() {
        return idsugg1l_desc;
    }

    public void setIdsugg1l_desc(String idsugg1l) {
        this.idsugg1l_desc = idsugg1l;
    }

    public String getColpvl_desc() {
        return colpvl_desc;
    }

    public void setColpvl_desc(String colpvl) {
        this.colpvl_desc = colpvl;
    }

    public String getColpes_desc() {
        return colpes_desc;
    }

    public void setColpes_desc(String colpes) {
        this.colpes_desc = colpes;
    }

    public String getRacdia_desc() {
        return racdia_desc;
    }

    public void setRacdia_desc(String racdia) {
        this.racdia_desc = racdia;
    }

    public String getIdsugg2l_desc() {
        return idsugg2l_desc;
    }

    public void setIdsugg2l_desc(String idsugg2l) {
        this.idsugg2l_desc = idsugg2l;
    }


    public String getTipo_int_desc() {
        return tipo_int_desc;
    }

    public void setTipo_int_desc(String tipo_int) {
        this.tipo_int_desc = tipo_int;
    }

    public String getIdsugg3l_desc() {
        return idsugg3l_desc;
    }

    public void setIdsugg3l_desc(String idsugg3l) {
        this.idsugg3l_desc = idsugg3l;
    }

    public String getEsito_desc() {
        return esito_desc;
    }

    public void setEsito_desc(String esito) {
        this.esito_desc = esito;
    }


    public String getIntval_desc() {
        return intval_desc;
    }

    public void setIntval_desc(String intval) {
        this.intval_desc = intval;
    }

    public String getRxcolon() {
        return rxcolon;
    }

    public void setRxcolon(String rxcolon) {
        this.rxcolon = rxcolon;
    }

    public String getEndo_estensione() {
        return endo_estensione;
    }

    public void setEndo_estensione(String endo_estensione) {
        this.endo_estensione = endo_estensione;
    }


    public Integer getEndo_complicanze() {
        return endo_complicanze;
    }

    public void setEndo_complicanze(Integer endo_complicanze) {
        this.endo_complicanze = endo_complicanze;
    }

    public String getEndo_complicanze_desc() {
        return endo_complicanze_desc;
    }

    public void setEndo_complicanze_desc(String endo_complicanze_desc) {
        this.endo_complicanze_desc = endo_complicanze_desc;
    }

    public Integer getConclusioni2l() {
        return conclusioni2l;
    }

    public void setConclusioni2l(Integer conclusioni2l) {
        this.conclusioni2l = conclusioni2l;
    }

    public String getConclusioni2l_desc() {
        return conclusioni2l_desc;
    }

    public void setConclusioni2l_desc(String conclusioni2l_desc) {
        this.conclusioni2l_desc = conclusioni2l_desc;
    }

    public Integer getRx_conclusioni() {
        return rx_conclusioni;
    }

    public void setRx_conclusioni(Integer rx_conclusioni) {
        this.rx_conclusioni = rx_conclusioni;
    }

    public String getRx_conclusioni_desc() {
        return rx_conclusioni_desc;
    }

    public void setRx_conclusioni_desc(String rx_conclusioni_desc) {
        this.rx_conclusioni_desc = rx_conclusioni_desc;
    }

    public Integer getDiagnosi_co() {
        return diagnosi_co;
    }

    public void setDiagnosi_co(Integer diagnosi_co) {
        this.diagnosi_co = diagnosi_co;
    }

    public String getDiagnosi_co_desc() {
        return diagnosi_co_desc;
    }

    public void setDiagnosi_co_desc(String diagnosi_co_desc) {
        this.diagnosi_co_desc = diagnosi_co_desc;
    }

    public boolean isInvitati() {
        return invitati;
    }

    public void setInvitati(boolean invitati) {
        this.invitati = invitati;
    }

    public boolean isEsclusi() {
        return esclusi;
    }

    public void setEsclusi(boolean esclusi) {
        this.esclusi = esclusi;
    }

    public Integer getMx_esito() {
        return mx_esito;
    }

    public void setMx_esito(Integer mx_esito) {
        this.mx_esito = mx_esito;
    }

    public String getMx_esito_desc() {
        return mx_esito_desc;
    }

    public void setMx_esito_desc(String mx_esito_desc) {
        this.mx_esito_desc = mx_esito_desc;
    }


    public Integer getMammo() {
        return mammo;
    }

    public void setMammo(Integer mammo) {
        this.mammo = mammo;
    }

    public Integer getEco() {
        return eco;
    }

    public void setEco(Integer eco) {
        this.eco = eco;
    }

    public Integer getClinico() {
        return clinico;
    }

    public void setClinico(Integer clinico) {
        this.clinico = clinico;
    }


    public Integer getCitologia() {
        return citologia;
    }

    public void setCitologia(Integer citologia) {
        this.citologia = citologia;
    }


    public Integer getAgobiopsia() {
        return agobiopsia;
    }

    public void setAgobiopsia(Integer agobiopsia) {
        this.agobiopsia = agobiopsia;
    }


    public String getCitologia_desc() {
        return citologia_desc;
    }

    public void setCitologia_desc(String citologia_desc) {
        this.citologia_desc = citologia_desc;
    }


    public String getAgobiopsia_desc() {
        return agobiopsia_desc;
    }

    public void setAgobiopsia_desc(String agobiopsia_desc) {
        this.agobiopsia_desc = agobiopsia_desc;
    }

    public Integer getAscella() {
        return ascella;
    }

    public void setAscella(Integer ascella) {
        this.ascella = ascella;
    }


    public String getAscella_desc() {
        return ascella_desc;
    }

    public void setAscella_desc(String ascella_desc) {
        this.ascella_desc = ascella_desc;
    }


    public Integer getEsito_agob() {
        return esito_agob;
    }

    public void setEsito_agob(Integer esito_agob) {
        this.esito_agob = esito_agob;
    }

    public String getEsito_agob_desc() {
        return esito_agob_desc;
    }

    public void setEsito_agob_desc(String esito_agob_desc) {
        this.esito_agob_desc = esito_agob_desc;
    }


    public void setEsito_cito(Integer esito_cito) {
        this.esito_cito = esito_cito;
    }

    public String getEsito_cito_desc() {
        return esito_cito_desc;
    }

    public void setEsito_cito_desc(String esito_cito_desc) {
        this.esito_cito_desc = esito_cito_desc;
    }

    /*20080710 MOD: aggiungi filtri CCR*/

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }
    /*20080710 FINE MOD*/

    /*20081210 MOD VERSIONE 1.2*/
    public Integer getIst_bio() {
        return ist_bio;
    }

    public void setIst_bio(Integer ist_bio) {
        this.ist_bio = ist_bio;
    }

    public Integer getIst_bio_diagnosi() {
        return ist_bio_diagnosi;
    }

    public void setIst_bio_diagnosi(Integer ist_bio_diagnosi) {
        this.ist_bio_diagnosi = ist_bio_diagnosi;
    }

    public String getIst_bio_diagnosi_desc() {
        return ist_bio_diagnosi_desc;
    }

    public void setIst_bio_diagnosi_desc(String ist_bio_diagnosi_desc) {
        this.ist_bio_diagnosi_desc = ist_bio_diagnosi_desc;
    }

    public Integer getIst_chir() {
        return ist_chir;
    }

    public void setIst_chir(Integer ist_chir) {
        this.ist_chir = ist_chir;
    }

    public Integer getIst_chir_diagnosi() {
        return ist_chir_diagnosi;
    }

    public void setIst_chir_diagnosi(Integer ist_chir_diagnosi) {
        this.ist_chir_diagnosi = ist_chir_diagnosi;
    }

    public String getIst_chir_diagnosi_desc() {
        return ist_chir_diagnosi_desc;
    }

    public void setIst_chir_diagnosi_desc(String ist_chir_diagnosi_desc) {
        this.ist_chir_diagnosi_desc = ist_chir_diagnosi_desc;
    }

    public Integer getLesione_hpv() {
        return lesione_hpv;
    }

    public void setLesione_hpv(Integer lesione_hpv) {
        this.lesione_hpv = lesione_hpv;
    }

    public Integer getInalim() {
        return inalim;
    }

    public void setInalim(Integer inalim) {
        this.inalim = inalim;
    }

    public String getInalim_desc() {
        return inalim_desc;
    }

    public void setInalim_desc(String inalim_desc) {
        this.inalim_desc = inalim_desc;
    }
    /* FINE MOD 20081210*/


    /*************** FILTRO 2************************/
    String tipo_invito_2;
    Integer centro_invito_2;
    String tipo_richiamo_2;
    int livello_2;
    String completo_2;
    Integer adepre_2;
    Integer giudia_2;
    Integer positi_2;
    Integer idsugg1l_2;
    Integer colpvl_2;
    Integer colpes_2;
    Integer racdia_2;
    Integer idsugg2l_2;
    String intchiusi_2;
    Integer tipo_int_2;
    Integer idsugg3l_2;
    String esito_2;
    Integer intval_2;
    Integer procedura_2;
    Integer motivo_2;
    String motivo_desc_2;
    Integer pn_2;
    Integer pt_2;
    Integer pm_2;
    String pn_desc_2;
    String pt_desc_2;
    String pm_desc_2;

    String tipo_invito_desc_2;
    String centro_invito_desc_2;
    String tipo_richiamo_desc_2;
    String adepre_desc_2;
    String giudia_desc_2;
    String positi_desc_2;
    String idsugg1l_desc_2;
    String colpvl_desc_2;
    String colpes_desc_2;
    String racdia_desc_2;
    String idsugg2l_desc_2;
    String tipo_int_desc_2;
    String idsugg3l_desc_2;
    String esito_desc_2;
    String intval_desc_2;
    String rxcolon_2;
    String endo_estensione_2;
    Integer endo_complicanze_2;
    String endo_complicanze_desc_2;
    Integer conclusioni2l_2;
    String conclusioni2l_desc_2;
    Integer rx_conclusioni_2;
    String rx_conclusioni_desc_2;
    Integer diagnosi_co_2;
    String diagnosi_co_desc_2;

    Integer mx_esito_2;
    String mx_esito_desc_2;
    Integer mammo_2;
    Integer eco_2;
    Integer clinico_2;
    Integer citologia_2;
    Integer agobiopsia_2;
    String citologia_desc_2;
    String agobiopsia_desc_2;
    Integer ascella_2;
    String ascella_desc_2;
    Integer esito_cito_2;
    String esito_cito_desc_2;
    Integer esito_agob_2;
    String esito_agob_desc_2;

    /*20080710 MOD: aggiunta categoria inviti*/
    Integer cat_invito_2;
    String cat_invito_desc_2;

    /*20080710 FINE MOD*/
    /*20080710 MOD: aggiungi filtri CCR*/
    Integer quantita_2;
    /*20080710 FINE MOD*/
    /*20081210 MOD VERSIONE 1.2*/
    Integer ist_bio_2;
    Integer ist_bio_diagnosi_2;
    String ist_bio_diagnosi_desc_2;
    Integer ist_chir_2;
    Integer ist_chir_diagnosi_2;
    String ist_chir_diagnosi_desc_2;
    Integer lesione_hpv_2;
    Integer inalim_2;
    String inalim_desc_2;

    /* FINE MOD 20081210*/
    //20110714
    boolean referto2;


    public void resetFilter2() {
        tipo_invito_2 = null;
        centro_invito_2 = null;
        tipo_richiamo_2 = null;
        livello_2 = -1;
        completo_2 = null;
        adepre_2 = null;
        giudia_2 = null;
        positi_2 = null;
        idsugg1l_2 = null;
        colpvl_2 = null;
        colpes_2 = null;
        racdia_2 = null;
        idsugg2l_2 = null;
        intchiusi_2 = null;
        tipo_int_2 = null;
        idsugg3l_2 = null;
        esito_2 = null;
        intval_2 = null;
        procedura_2 = null;
        motivo_2 = null;
        pn_2 = null;
        pt_2 = null;
        pm_2 = null;

        /*20080710 MOD: aggiunta categoria inviti*/
        cat_invito_2 = null;
        cat_invito_desc_2 = null;
        /*20080710 FINE MOD*/
        //20110714
        referto2 = false;


        tipo_invito_desc_2 = null;
        centro_invito_desc_2 = null;
        tipo_richiamo_desc_2 = null;
        adepre_desc_2 = null;
        giudia_desc_2 = null;
        positi_desc_2 = null;
        idsugg1l_desc_2 = null;
        colpvl_desc_2 = null;
        colpes_desc_2 = null;
        racdia_desc_2 = null;
        idsugg2l_desc_2 = null;
        tipo_int_desc_2 = null;
        idsugg3l_desc_2 = null;
        esito_desc_2 = null;
        intval_desc_2 = null;

        rxcolon_2 = null;
        endo_estensione_2 = null;
        endo_complicanze_2 = null;
        endo_complicanze_desc_2 = null;
        conclusioni2l_2 = null;
        conclusioni2l_desc_2 = null;
        rx_conclusioni_2 = null;
        rx_conclusioni_desc_2 = null;
        diagnosi_co_2 = null;
        diagnosi_co_desc_2 = null;
        motivo_desc_2 = null;
        pn_desc_2 = null;
        pt_desc_2 = null;
        pm_desc_2 = null;


        mx_esito_2 = null;
        mx_esito_desc_2 = null;
        mammo_2 = null;
        eco_2 = null;
        clinico_2 = null;
        citologia_2 = null;
        agobiopsia_2 = null;
        citologia_desc_2 = null;
        agobiopsia_desc_2 = null;
        ascella_2 = null;
        ascella_desc_2 = null;
        esito_cito_2 = null;
        esito_cito_desc_2 = null;
        esito_agob_2 = null;
        esito_agob_desc_2 = null;

        /*20080710 MOD: aggiungi filtri CCR*/
        quantita_2 = null;
        /*20080710 FINE MOD*/

        /*20081210 MOD VERSIONE 1.2*/
        ist_bio_2 = null;
        ist_bio_diagnosi_2 = null;
        ist_bio_diagnosi_desc_2 = null;
        ist_chir_2 = null;
        ist_chir_diagnosi_2 = null;
        ist_chir_diagnosi_desc_2 = null;
        lesione_hpv_2 = null;
        inalim_2 = null;
        inalim_desc_2 = null;
        /* FINE MOD 20081210*/
    }

    public Integer getPn_2() {
        return pn_2;
    }

    public void setPn_2(Integer pn_2) {
        this.pn_2 = pn_2;
    }

    public Integer getPt_2() {
        return pt_2;
    }

    public void setPt_2(Integer pt_2) {
        this.pt_2 = pt_2;
    }

    public Integer getPm_2() {
        return pm_2;
    }

    public void setPm_2(Integer pm_2) {
        this.pm_2 = pm_2;
    }

    public String getPn_desc_2() {
        return pn_desc_2;
    }

    public void setPn_desc_2(String pn_desc_2) {
        this.pn_desc_2 = pn_desc_2;
    }

    public String getPt_desc_2() {
        return pt_desc_2;
    }

    public void setPt_desc_2(String pt_desc_2) {
        this.pt_desc_2 = pt_desc_2;
    }

    public String getPm_desc_2() {
        return pm_desc_2;
    }

    public void setPm_desc_2(String pm_desc_2) {
        this.pm_desc_2 = pm_desc_2;
    }


    public Integer getMotivo_2() {
        return motivo_2;
    }

    public void setMotivo_2(Integer motivo_2) {
        this.motivo_2 = motivo_2;
    }


    public String getMotivo_desc_2() {
        return motivo_desc_2;
    }

    public void setMotivo_desc_2(String motivo_desc_2) {
        this.motivo_desc_2 = motivo_desc_2;
    }


    public Integer getProcedura_2() {
        return procedura_2;
    }

    public void setProcedura_2(Integer procedura_2) {
        this.procedura_2 = procedura_2;
    }

    public String getTipo_invito_2() {
        return tipo_invito_2;
    }

    public void setTipo_invito_2(String tipo_invito) {
        this.tipo_invito_2 = tipo_invito;
    }

    /*20080710 MOD: aggiunta categoria invito*/

    public Integer getCat_invito_2() {
        return cat_invito_2;
    }

    public void setCat_invito_2(Integer cat_invito_2) {
        this.cat_invito_2 = cat_invito_2;
    }

    public String getCat_invito_desc_2() {
        return cat_invito_desc_2;
    }

    public void setCat_invito_desc_2(String cat_invito_desc) {
        this.cat_invito_desc_2 = cat_invito_desc;
    }
    /*20080710 FINE MOD*/

    public Integer getCentro_invito_2() {
        return centro_invito_2;
    }

    public void setCentro_invito_2(Integer centro_invito) {
        this.centro_invito_2 = centro_invito;
    }


    public String getTipo_richiamo_2() {
        return tipo_richiamo_2;
    }

    public void setTipo_richiamo_2(String tipo_richiamo) {
        this.tipo_richiamo_2 = tipo_richiamo;
    }

    public int getLivello_2() {
        return livello_2;
    }

    public void setLivello_2(int livello) {
        this.livello_2 = livello;
    }

    public String getCompleto_2() {
        return completo_2;
    }

    public void setCompleto_2(String completo) {
        this.completo_2 = completo;
    }

    public Integer getAdepre_2() {
        return adepre_2;
    }

    public void setAdepre_2(Integer adepre) {
        this.adepre_2 = adepre;
    }

    public Integer getGiudia_2() {
        return giudia_2;
    }

    public void setGiudia_2(Integer giudia) {
        this.giudia_2 = giudia;
    }

    public Integer getPositi_2() {
        return positi_2;
    }

    public void setPositi_2(Integer positi) {
        this.positi_2 = positi;
    }

    public Integer getIdsugg1l_2() {
        return idsugg1l_2;
    }

    public void setIdsugg1l_2(Integer idsugg1l) {
        this.idsugg1l_2 = idsugg1l;
    }

    public Integer getColpvl_2() {
        return colpvl_2;
    }

    public void setColpvl_2(Integer colpvl) {
        this.colpvl_2 = colpvl;
    }

    public Integer getColpes_2() {
        return colpes_2;
    }

    public void setColpes_2(Integer colpes) {
        this.colpes_2 = colpes;
    }

    public Integer getRacdia_2() {
        return racdia_2;
    }

    public void setRacdia_2(Integer racdia) {
        this.racdia_2 = racdia;
    }

    public Integer getIdsugg2l_2() {
        return idsugg2l_2;
    }

    public void setIdsugg2l_2(Integer idsugg2l) {
        this.idsugg2l_2 = idsugg2l;
    }

    public String getIntchiusi_2() {
        return intchiusi_2;
    }

    public void setIntchiusi_2(String intchiusi) {
        this.intchiusi_2 = intchiusi;
    }

    public Integer getTipo_int_2() {
        return tipo_int_2;
    }

    public void setTipo_int_2(Integer tipo_int) {
        this.tipo_int_2 = tipo_int;
    }

    public Integer getIdsugg3l_2() {
        return idsugg3l_2;
    }

    public void setIdsugg3l_2(Integer idsugg3l) {
        this.idsugg3l_2 = idsugg3l;
    }

    public String getEsito_2() {
        return esito_2;
    }

    public void setEsito_2(String esito) {
        this.esito_2 = esito;
    }


    public Integer getIntval_2() {
        return intval_2;
    }

    public void setIntval_2(Integer intval) {
        this.intval_2 = intval;
    }


    public String getTipo_invito_desc_2() {
        return tipo_invito_desc_2;
    }

    public void setTipo_invito_desc_2(String tipo_invito) {
        this.tipo_invito_desc_2 = tipo_invito;
    }

    public String getCentro_invito_desc_2() {
        return centro_invito_desc_2;
    }

    public void setCentro_invito_desc_2(String centro_invito) {
        this.centro_invito_desc_2 = centro_invito;
    }


    public String getTipo_richiamo_desc_2() {
        return tipo_richiamo_desc_2;
    }

    public void setTipo_richiamo_desc_2(String tipo_richiamo) {
        this.tipo_richiamo_desc_2 = tipo_richiamo;
    }


    public String getAdepre_desc_2() {
        return adepre_desc_2;
    }

    public void setAdepre_desc_2(String adepre) {
        this.adepre_desc_2 = adepre;
    }

    public String getGiudia_desc_2() {
        return giudia_desc_2;
    }

    public void setGiudia_desc_2(String giudia) {
        this.giudia_desc_2 = giudia;
    }

    public String getPositi_desc_2() {
        return positi_desc_2;
    }

    public void setPositi_desc_2(String positi) {
        this.positi_desc_2 = positi;
    }

    public String getIdsugg1l_desc_2() {
        return idsugg1l_desc_2;
    }

    public void setIdsugg1l_desc_2(String idsugg1l) {
        this.idsugg1l_desc_2 = idsugg1l;
    }

    public String getColpvl_desc_2() {
        return colpvl_desc_2;
    }

    public void setColpvl_desc_2(String colpvl) {
        this.colpvl_desc_2 = colpvl;
    }

    public String getColpes_desc_2() {
        return colpes_desc_2;
    }

    public void setColpes_desc_2(String colpes) {
        this.colpes_desc_2 = colpes;
    }

    public String getRacdia_desc_2() {
        return racdia_desc_2;
    }

    public void setRacdia_desc_2(String racdia) {
        this.racdia_desc_2 = racdia;
    }

    public String getIdsugg2l_desc_2() {
        return idsugg2l_desc_2;
    }

    public void setIdsugg2l_desc_2(String idsugg2l) {
        this.idsugg2l_desc_2 = idsugg2l;
    }


    public String getTipo_int_desc_2() {
        return tipo_int_desc_2;
    }

    public void setTipo_int_desc_2(String tipo_int) {
        this.tipo_int_desc_2 = tipo_int;
    }

    public String getIdsugg3l_desc_2() {
        return idsugg3l_desc_2;
    }

    public void setIdsugg3l_desc_2(String idsugg3l) {
        this.idsugg3l_desc_2 = idsugg3l;
    }

    public String getEsito_desc_2() {
        return esito_desc_2;
    }

    public void setEsito_desc_2(String esito) {
        this.esito_desc_2 = esito;
    }


    public String getIntval_desc_2() {
        return intval_desc_2;
    }

    public void setIntval_desc_2(String intval) {
        this.intval_desc_2 = intval;
    }

    public String getRxcolon_2() {
        return rxcolon_2;
    }

    public void setRxcolon_2(String rxcolon_2) {
        this.rxcolon_2 = rxcolon_2;
    }

    public String getEndo_estensione_2() {
        return endo_estensione_2;
    }

    public void setEndo_estensione_2(String endo_estensione_2) {
        this.endo_estensione_2 = endo_estensione_2;
    }


    public Integer getEndo_complicanze_2() {
        return endo_complicanze_2;
    }

    public void setEndo_complicanze_2(Integer endo_complicanze) {
        this.endo_complicanze_2 = endo_complicanze;
    }

    public String getEndo_complicanze_desc_2() {
        return endo_complicanze_desc_2;
    }

    public void setEndo_complicanze_desc_2(String endo_complicanze_desc) {
        this.endo_complicanze_desc_2 = endo_complicanze_desc;
    }

    public Integer getConclusioni2l_2() {
        return conclusioni2l_2;
    }

    public void setConclusioni2l_2(Integer conclusioni2l) {
        this.conclusioni2l_2 = conclusioni2l;
    }

    public String getConclusioni2l_desc_2() {
        return conclusioni2l_desc_2;
    }

    public void setConclusioni2l_desc_2(String conclusioni2l_desc) {
        this.conclusioni2l_desc_2 = conclusioni2l_desc;
    }

    public Integer getRx_conclusioni_2() {
        return rx_conclusioni_2;
    }

    public void setRx_conclusioni_2(Integer rx_conclusioni) {
        this.rx_conclusioni_2 = rx_conclusioni;
    }

    public String getRx_conclusioni_desc_2() {
        return rx_conclusioni_desc_2;
    }

    public void setRx_conclusioni_desc_2(String rx_conclusioni_desc) {
        this.rx_conclusioni_desc_2 = rx_conclusioni_desc;
    }

    public Integer getDiagnosi_co_2() {
        return diagnosi_co_2;
    }

    public void setDiagnosi_co_2(Integer diagnosi_co) {
        this.diagnosi_co_2 = diagnosi_co;
    }

    public String getDiagnosi_co_desc_2() {
        return diagnosi_co_desc_2;
    }

    public void setDiagnosi_co_desc_2(String diagnosi_co_desc) {
        this.diagnosi_co_desc_2 = diagnosi_co_desc;
    }


    public Integer getMx_esito_2() {
        return mx_esito_2;
    }

    public void setMx_esito_2(Integer mx_esito_2) {
        this.mx_esito_2 = mx_esito_2;
    }

    public String getMx_esito_desc_2() {
        return mx_esito_desc_2;
    }

    public void setMx_esito_desc_2(String mx_esito_desc_2) {
        this.mx_esito_desc_2 = mx_esito_desc_2;
    }


    public Integer getMammo_2() {
        return mammo_2;
    }

    public void setMammo_2(Integer mammo_2) {
        this.mammo_2 = mammo_2;
    }


    public Integer getEco_2() {
        return eco_2;
    }

    public void setEco_2(Integer eco_2) {
        this.eco_2 = eco_2;
    }


    public Integer getClinico_2() {
        return clinico_2;
    }

    public void setClinico_2(Integer clinico_2) {
        this.clinico_2 = clinico_2;
    }


    public Integer getCitologia_2() {
        return citologia_2;
    }

    public void setCitologia_2(Integer citologia_2) {
        this.citologia_2 = citologia_2;
    }


    public Integer getAgobiopsia_2() {
        return agobiopsia_2;
    }

    public void setAgobiopsia_2(Integer agobiopsia_2) {
        this.agobiopsia_2 = agobiopsia_2;
    }


    public String getCitologia_desc_2() {
        return citologia_desc_2;
    }

    public void setCitologia_desc_2(String citologia_desc_2) {
        this.citologia_desc_2 = citologia_desc_2;
    }


    public String getAgobiopsia_desc_2() {
        return agobiopsia_desc_2;
    }

    public void setAgobiopsia_desc_2(String agobiopsia_desc_2) {
        this.agobiopsia_desc_2 = agobiopsia_desc_2;
    }


    public Integer getAscella_2() {
        return ascella_2;
    }

    public void setAscella_2(Integer ascella_2) {
        this.ascella_2 = ascella_2;
    }


    public String getAscella_desc_2() {
        return ascella_desc_2;
    }

    public void setAscella_desc_2(String ascella_desc_2) {
        this.ascella_desc_2 = ascella_desc_2;
    }

    public Integer getEsito_cito() {
        return esito_cito;
    }


    public Integer getEsito_cito_2() {
        return esito_cito_2;
    }

    public void setEsito_cito_2(Integer esito_cito_2) {
        this.esito_cito_2 = esito_cito_2;
    }

    public String getEsito_cito_desc_2() {
        return esito_cito_desc_2;
    }

    public void setEsito_cito_desc_2(String esito_cito_desc_2) {
        this.esito_cito_desc_2 = esito_cito_desc_2;
    }


    public Integer getEsito_agob_2() {
        return esito_agob_2;
    }

    public void setEsito_agob_2(Integer esito_agob_2) {
        this.esito_agob_2 = esito_agob_2;
    }

    public String getEsito_agob_desc_2() {
        return esito_agob_desc_2;
    }

    public void setEsito_agob_desc_2(String esito_agob_desc_2) {
        this.esito_agob_desc_2 = esito_agob_desc_2;
    }

    /*20080710 MOD: aggiungi filtri CCR*/

    public Integer getQuantita_2() {
        return quantita_2;
    }

    public void setQuantita_2(Integer quantita_2) {
        this.quantita_2 = quantita_2;
    }


    /*20080710 FINE MOD*/

    /*20081210 MOD VERSIONE 1.2*/
    public Integer getIst_bio_2() {
        return ist_bio_2;
    }

    public void setIst_bio_2(Integer ist_bio_2) {
        this.ist_bio = ist_bio_2;
    }

    public Integer getIst_bio_diagnosi_2() {
        return ist_bio_diagnosi_2;
    }

    public void setIst_bio_diagnosi_2(Integer ist_bio_diagnosi_2) {
        this.ist_bio_diagnosi_2 = ist_bio_diagnosi_2;
    }

    public Integer getIst_chir_2() {
        return ist_chir_2;
    }

    public void setIst_chir_2(Integer ist_chir_2) {
        this.ist_chir = ist_chir_2;
    }

    public Integer getIst_chir_diagnosi_2() {
        return ist_chir_diagnosi_2;
    }

    public void setIst_chir_diagnosi_2(Integer ist_chir_diagnosi_2) {
        this.ist_chir_diagnosi_2 = ist_chir_diagnosi_2;
    }

    public Integer getLesione_hpv_2() {
        return lesione_hpv_2;
    }

    public void setLesione_hpv_2(Integer lesione_hpv_2) {
        this.lesione_hpv_2 = lesione_hpv_2;
    }

    public String getIst_bio_diagnosi_desc_2() {
        return ist_bio_diagnosi_desc_2;
    }

    public void setIst_bio_diagnosi_desc_2(String ist_bio_diagnosi_desc_2) {
        this.ist_bio_diagnosi_desc = ist_bio_diagnosi_desc_2;
    }


    public String getIst_chir_diagnosi_desc_2() {
        return ist_chir_diagnosi_desc_2;
    }

    public void setIst_chir_diagnosi_desc_2(String ist_chir_diagnosi_desc_2) {
        this.ist_chir_diagnosi_desc_2 = ist_chir_diagnosi_desc_2;
    }

    public Integer getInalim_2() {
        return inalim_2;
    }

    public void setInalim_2(Integer inalim_2) {
        this.inalim_2 = inalim_2;
    }

    public String getInalim_desc_2() {
        return inalim_desc_2;
    }

    public void setInalim_desc_2(String inalim_desc_2) {
        this.inalim_desc_2 = inalim_desc_2;
    }

    public boolean isReferto() {
        return referto;
    }

    public void setReferto(boolean referto) {
        this.referto = referto;
    }

    public boolean isReferto2() {
        return referto2;
    }

    public void setReferto2(boolean referto2) {
        this.referto2 = referto2;
    }

    /* FINE MOD 20081210*/


}
