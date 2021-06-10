package model.datacontrol;

import insiel.dataHandling.DateUtils;

import java.util.Date;

public class Stats_paramBean {
    public static final int INVITI_ESITI = 1;
    public static final int ESCLUSIONI = 2;
    public static final int SCREENATI = 3;
    public static final int DIAGNOSI1LIV = 4;
    public static final int ESITI_COLPO = 5;
    public static final int DIAGNOSI_PEGGIORE = 6;
    public static final int RAC2LIV = 7;
    public static final int OPMEDICI = 8;
    public static final int TEMPI = 9;
    public static final int PERIODISMO = 10;
    public static final int INDICAZIONI2LIV = 11;
    public static final int CONCLUSIONI2L = 12;
    public static final int APPROFONDIMENTO_CO = 13;
    public static final int DETECTION_RATE = 14;
    public static final int CHIRURGIE_CO = 15;
    public static final int PN_PT = 16;


    private Integer livello;
    private String ulss;
    private String tpscr;
    private Date dal;
    private Date al_escluso;
    private String round_ind;
    private String round_org;
    private Integer centro_prel;
    private String comune;
    private Integer zona;
    private int eta_da;
    private int eta_a;
    private String accesso;
    private int stats_type;
    private String tpescl;
    private String formato;
    private int tipo_operatore;
    private String nome_comune;
    private String nome_zona;
    private String nome_centro;
    /*20080704 MOD: aggiunta round inviti ai filtri*/
    private String round_inviti;
    /*20080704 FINE MOD*/


    //statistiche per il centro tumori
    private String TU_periodo;
    private Integer TU_anno;
    private String TU_ulss;
    private String coorte = "INVITATI";
    int stats_tab = 0;
    private int durata_int = 6;
    private int liv_successivo = 1;
    private int inizio_int;
    private Date data_esclusione;
    private String sesso;
    private String lesione;
    private Integer giudia;
    private Integer medico;
    private Integer centro_ref;
    private String centro_ref_desc;
    private String medico_desc;
    private String medico_desc_2;
    int fine_int;
    String datarif;

    //DWH
    private String DWH_ulss;
    private String DWH_periodo;
    private String DWH_anno;
    private Integer DWH_test;

    //FLUSSO SPS
    private Date SPS_dal;
    private Date SPS_al;
    private Integer SPS_centro_prel;
    private Integer SPS_centro_ref;
    private String SPS_ulss;
    private String SPS_label1;
    private String SPS_label2;
    private String SPS_label3;
    private String SPS_label4;
    private Integer SPS_prestazione1;
    private Integer SPS_prestazione2;
    private Integer SPS_prestazione3;
    private Integer SPS_prestazione4;
    private Integer SPS_lab_hpv;

    public Stats_paramBean() {
        this.reset();
    }

    public void reset() {
        setLivello(1);
        //livello = new Integer(1);
        // ulss=null;
        tpscr = null;
        dal = null;
        al_escluso = null;
        round_ind = null;
        round_org = null;
        /*20080704 MOD: aggiunta round inviti ai filtri*/
        round_inviti = null;
        /*20080704 FINE MOD*/
        //centro_prel = null;
        setCentro_prel(null);
        //comune = null;
        setComune(null);
        zona = null;
        eta_da = 0;
        eta_a = 0;
        accesso = "Tutti";
        stats_type = INVITI_ESITI;
        tpescl = "tutte";
        formato = "aggregato";
        tipo_operatore = 1; //1Prelevatore o Endoscopista, 2 Citoscreener, 3 Supervisore
        data_esclusione = null;
        sesso = null;
        giudia = null;
        centro_ref = null;
        centro_ref_desc = null;
        medico = null;
        medico_desc = null;
        medico_desc_2 = null;
        this.nome_centro = null;
        this.nome_comune = null;
        this.nome_zona = null;
        this.datarif = DateUtils.getToday();
        lesione = null;
    }

    public Integer getLivello() {
        return livello;
    }

    public void setLivello(Integer livello) {
        this.livello = livello;
    }

    public String getUlss() {
        return ulss;
    }

    public void setUlss(String ulss) {
        this.ulss = ulss;
    }

    public String getTpscr() {
        return tpscr;
    }

    public void setTpscr(String tpscr) {
        this.tpscr = tpscr;
    }

    public Date getDal() {
        return dal;
    }


    public Date getAl_escluso() {
        return al_escluso;
    }


    public String getRound_ind() {
        return round_ind;
    }

    public void setRound_ind(String round_ind) {
        this.round_ind = round_ind;
    }

    public String getRound_org() {
        return round_org;
    }

    public void setRound_org(String round_org) {
        this.round_org = round_org;
    }

    public Integer getCentro_prel() {
        return centro_prel;
    }

    public void setCentro_prel(Integer centro_prel) {
        this.centro_prel = centro_prel;
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

    public String getAccesso() {
        return accesso;
    }

    public void setAccesso(String accesso) {
        this.accesso = accesso;
    }

    public void setDal(Date dal) {
        this.dal = dal;
    }

    public void setAl_escluso(Date al_escluso) {
        this.al_escluso = al_escluso;
    }

    public int getStats_type() {
        return stats_type;
    }

    public void setStats_type(int stats_type) {
        this.stats_type = stats_type;
    }

    public String getTpescl() {
        return tpescl;
    }

    public void setTpescl(String tpescl) {
        this.tpescl = tpescl;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public int getTipo_operatore() {
        return tipo_operatore;
    }

    public void setTipo_operatore(int tipo_operatore) {
        this.tipo_operatore = tipo_operatore;
    }

    public int getDIAGNOSI1LIV() {
        return DIAGNOSI1LIV;
    }

    public int getDIAGNOSI_PEGGIORE() {
        return DIAGNOSI_PEGGIORE;
    }

    public int getESCLUSIONI() {
        return ESCLUSIONI;
    }

    public int getESITI_COLPO() {
        return ESITI_COLPO;
    }

    public int getINVITI_ESITI() {
        return INVITI_ESITI;
    }

    public int getOPMEDICI() {
        return OPMEDICI;
    }

    public int getRAC2LIV() {
        return RAC2LIV;
    }

    public int getSCREENATI() {
        return SCREENATI;
    }

    public int getTEMPI() {
        return TEMPI;
    }

    public String getNome_comune() {
        return nome_comune;
    }

    public void setNome_comune(String nome_comune) {
        this.nome_comune = nome_comune;
    }

    public String getNome_zona() {
        return nome_zona;
    }

    public void setNome_zona(String nome_zona) {
        this.nome_zona = nome_zona;
    }

    public String getNome_centro() {
        return nome_centro;
    }

    public void setNome_centro(String nome_centro) {
        this.nome_centro = nome_centro;
    }

    public String getTU_periodo() {
        return TU_periodo;
    }

    public void setTU_periodo(String TU_periodo) {
        this.TU_periodo = TU_periodo;
    }

    public Integer getTU_anno() {
        return TU_anno;
    }

    public void setTU_anno(Integer TU_anno) {
        this.TU_anno = TU_anno;
    }

    public String getTU_ulss() {
        return TU_ulss;
    }

    public void setTU_ulss(String TU_ulss) {
        this.TU_ulss = TU_ulss;
    }

    public String getCoorte() {
        return coorte;
    }

    public void setCoorte(String coorte) {
        this.coorte = coorte;
    }

    public int getStats_tab() {
        return stats_tab;
    }

    public void setStats_tab(int stats_tab) {
        this.stats_tab = stats_tab;
    }

    public int getDurata_int() {
        return durata_int;
    }

    public void setDurata_int(int durata_int) {
        this.durata_int = durata_int;
    }

    public int getPERIODISMO() {
        return PERIODISMO;
    }

    public int getINDICAZIONI2LIV() {
        return INDICAZIONI2LIV;
    }

    public int getLiv_successivo() {
        return liv_successivo;
    }

    public void setLiv_successivo(int liv_successivo) {
        this.liv_successivo = liv_successivo;
    }

    public int getInizio_int() {
        return inizio_int;
    }

    public void setInizio_int(int inizio_int) {
        this.inizio_int = inizio_int;
    }


    public Date getData_esclusione() {
        return data_esclusione;
    }

    public void setData_esclusione(Date data_esclusione) {
        this.data_esclusione = data_esclusione;
    }

    public int getCONCLUSIONI2L() {
        return CONCLUSIONI2L;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public int getAPPROFONDIMENTO_CO() {
        return APPROFONDIMENTO_CO;
    }

    public int getDETECTION_RATE() {
        return DETECTION_RATE;
    }

    public int getCHIRURGIE_CO() {
        return CHIRURGIE_CO;
    }

    public String getLesione() {
        return lesione;
    }

    public void setLesione(String lesione) {
        this.lesione = lesione;
    }

    public int getPN_PT() {
        return PN_PT;
    }

    public Integer getGiudia() {
        return giudia;
    }

    public void setGiudia(Integer giudia) {
        this.giudia = giudia;
    }

    public Integer getMedico() {
        return medico;
    }

    public void setMedico(Integer medico) {
        this.medico = medico;
    }

    public Integer getCentro_ref() {
        return centro_ref;
    }

    public void setCentro_ref(Integer centro_ref) {
        this.centro_ref = centro_ref;
    }

    public String getCentro_ref_desc() {
        return centro_ref_desc;
    }

    public void setCentro_ref_desc(String centro_ref_desc) {
        this.centro_ref_desc = centro_ref_desc;
    }

    public String getMedico_desc() {
        return medico_desc;
    }

    public void setMedico_desc(String medico_desc) {
        this.medico_desc = medico_desc;
    }

    public String getMedico_desc_2() {
        return medico_desc_2;
    }

    public void setMedico_desc_2(String medico_desc_2) {
        this.medico_desc_2 = medico_desc_2;
    }


    public int getFine_int() {
        return fine_int;
    }

    public void setFine_int(int fine_int) {
        this.fine_int = fine_int;
    }

    public String getDatarif() {
        return datarif;
    }

    public void setDatarif(String datarif) {
        this.datarif = datarif;
    }
    /*20080704 MOD: aggiunta round inviti ai filtri*/

    public String getRound_inviti() {
        return round_inviti;
    }

    public void setRound_inviti(String round_inviti) {
        this.round_inviti = round_inviti;
    }

    /*20080704 FINE MOD*/


    //19032012 gaion: tracciato dwh
    public void setDWH_ulss(String DWH_ulss) {
        this.DWH_ulss = DWH_ulss;
    }


    public String getDWH_ulss() {
        return DWH_ulss;
    }


    public void setDWH_periodo(String DWH_periodo) {
        this.DWH_periodo = DWH_periodo;
    }


    public String getDWH_periodo() {
        return DWH_periodo;
    }


    public void setDWH_anno(String DWH_anno) {
        this.DWH_anno = DWH_anno;
    }


    public String getDWH_anno() {
        return DWH_anno;
    }


    public void setDWH_test(Integer DWH_test) {
        this.DWH_test = DWH_test;
    }


    public Integer getDWH_test() {
        return DWH_test;
    }
    //fine 19032012

    public void setSPS_dal(Date SPS_dal) {
        this.SPS_dal = SPS_dal;
    }


    public Date getSPS_dal() {
        return SPS_dal;
    }


    public void setSPS_al(Date SPS_al) {
        this.SPS_al = SPS_al;
    }


    public Date getSPS_al() {
        return SPS_al;
    }


    public void setSPS_centro_prel(Integer SPS_centro_prel) {
        this.SPS_centro_prel = SPS_centro_prel;
    }


    public Integer getSPS_centro_prel() {
        return SPS_centro_prel;
    }


    public void setSPS_centro_ref(Integer SPS_centro_ref) {
        this.SPS_centro_ref = SPS_centro_ref;
    }


    public Integer getSPS_centro_ref() {
        return SPS_centro_ref;
    }


    public void setSPS_ulss(String SPS_ulss) {
        this.SPS_ulss = SPS_ulss;
    }


    public String getSPS_ulss() {
        return SPS_ulss;
    }


    public void setSPS_label1(String SPS_label1) {
        this.SPS_label1 = SPS_label1;
    }


    public String getSPS_label1() {
        return SPS_label1;
    }


    public void setSPS_label2(String SPS_label2) {
        this.SPS_label2 = SPS_label2;
    }


    public String getSPS_label2() {
        return SPS_label2;
    }


    public void setSPS_label3(String SPS_label3) {
        this.SPS_label3 = SPS_label3;
    }


    public String getSPS_label3() {
        return SPS_label3;
    }


    public void setSPS_label4(String SPS_label4) {
        this.SPS_label4 = SPS_label4;
    }


    public String getSPS_label4() {
        return SPS_label4;
    }


    public void setSPS_prestazione1(Integer SPS_prestazione1) {
        this.SPS_prestazione1 = SPS_prestazione1;
    }


    public Integer getSPS_prestazione1() {
        return SPS_prestazione1;
    }


    public void setSPS_prestazione2(Integer SPS_prestazione2) {
        this.SPS_prestazione2 = SPS_prestazione2;
    }


    public Integer getSPS_prestazione2() {
        return SPS_prestazione2;
    }


    public void setSPS_prestazione3(Integer SPS_prestazione3) {
        this.SPS_prestazione3 = SPS_prestazione3;
    }


    public Integer getSPS_prestazione3() {
        return SPS_prestazione3;
    }


    public void setSPS_prestazione4(Integer SPS_prestazione4) {
        this.SPS_prestazione4 = SPS_prestazione4;
    }


    public Integer getSPS_prestazione4() {
        return SPS_prestazione4;
    }


    public void setSPS_lab_hpv(Integer SPS_lab_hpv) {
        this.SPS_lab_hpv = SPS_lab_hpv;
    }


    public Integer getSPS_lab_hpv() {
        return SPS_lab_hpv;
    }
}
