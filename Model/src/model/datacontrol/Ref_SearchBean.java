package model.datacontrol;

import java.util.Date;

public final class Ref_SearchBean {
    private Integer centro_ref;
    private Date prelievo_dal;
    private String prelievo_al;
    private Integer anno_nascita;
    private String cognome;
    private String nome;
    private Integer centro_prel;
    private boolean inviti_attivi;
    private String referti_chiusi;
    private String livello;
    private Integer vetrino_dal;
    private Integer vetrino_al;
    private Integer anno_vetrino;
    private boolean pendente;
    private Integer giorni_pendente;
    private Integer follow_up;
    private Integer id_invito;
    private String codts;
    private Integer giorno_nascita;
    private Integer mese_nascita;
    private Integer cat_richiamo;
    private boolean emptyQuery = false;
    private String sugg_esito;
    private String sugg_descrizione;
    private boolean da_revisionare;
    private Integer hpv;
    private boolean pres_no_referto;
    private Date interm_dal;
    private boolean interm_completi;
    private boolean iterm_errore;
    private Integer idStatoTrial;
    private Integer idBraccioTrial;
    private String test_proposto;
    private String prelievo_hpv;
    private String tipoDocumento;
    private String codiceDocumento;
    private boolean soloStorico;
    private String codClassePop;
    private String chiave;

    public Ref_SearchBean() {
        reset();
    }

    public void reset() {
        this.setCentro_ref(null);
        prelievo_dal = null;
        prelievo_al = "il";
        anno_nascita = null;
        giorno_nascita = null;
        mese_nascita = null;
        cognome = null;
        nome = null;
        centro_prel = null;
        inviti_attivi = false;
        referti_chiusi = "tutti";
        livello = "tutti";
        vetrino_dal = null;
        vetrino_al = null;
        anno_vetrino = null;
        pendente = false;
        giorni_pendente = new Integer(15);
        follow_up = null;
        id_invito = null;
        codts = null;
        emptyQuery = true;
        cat_richiamo = null;
        this.setCat_richiamo(null);
        this.setSugg_esito(null);
        sugg_descrizione = null;
        da_revisionare = false;
        hpv = null;
        setHpv(null);
        pres_no_referto = false;
        idStatoTrial = null;
        idBraccioTrial = null;
        test_proposto = null;
        prelievo_hpv = null;
        codiceDocumento = null;
        soloStorico = false;
        codClassePop = null;
        chiave = null;
    }

    public void resetIntermedi() {
        interm_dal = null;
        interm_completi = false;
        iterm_errore = false;
    }

    public Integer getCentro_ref() {
        return centro_ref;
    }

    public void setCentro_ref(Integer centro_ref) {
        this.centro_ref = centro_ref;
    }

    public Date getPrelievo_dal() {
        return prelievo_dal;
    }

    public void setPrelievo_dal(Date prelievo_dal) {
        this.prelievo_dal = prelievo_dal;
    }

    public String getPrelievo_al() {
        return prelievo_al;
    }

    public void setPrelievo_al(String prelievo_al) {
        this.prelievo_al = prelievo_al;
    }

    public Integer getAnno_nascita() {
        return anno_nascita;
    }

    public void setAnno_nascita(Integer anno_nascita) {
        this.anno_nascita = anno_nascita;
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

    public Integer getCentro_prel() {
        return centro_prel;
    }

    public void setCentro_prel(Integer centro_prel) {
        this.centro_prel = centro_prel;
    }

    public boolean isInviti_attivi() {
        return inviti_attivi;
    }

    public void setInviti_attivi(boolean inviti_attivi) {
        this.inviti_attivi = inviti_attivi;
    }

    public String getReferti_chiusi() {
        return referti_chiusi;
    }

    public void setReferti_chiusi(String referti_chiusi) {
        this.referti_chiusi = referti_chiusi;
    }

    public String getLivello() {
        return livello;
    }

    public void setLivello(String livello) {
        this.livello = livello;
    }

    public Integer getVetrino_dal() {
        return vetrino_dal;
    }

    public void setVetrino_dal(Integer vetrino_dal) {
        this.vetrino_dal = vetrino_dal;
    }

    public Integer getVetrino_al() {
        return vetrino_al;
    }

    public void setVetrino_al(Integer vetrino_al) {
        this.vetrino_al = vetrino_al;
    }

    public Integer getAnno_vetrino() {
        return anno_vetrino;
    }

    public void setAnno_vetrino(Integer anno_vetrino) {
        this.anno_vetrino = anno_vetrino;
    }

    public boolean getPendente() {
        return pendente;
    }

    public void setPendente(boolean pendente) {
        this.pendente = pendente;
    }

    public Integer getGiorni_pendente() {
        return giorni_pendente;
    }

    public void setGiorni_pendente(Integer giorni_pendente) {
        this.giorni_pendente = giorni_pendente;
    }

    public Integer getFollow_up() {
        return follow_up;
    }

    public void setFollow_up(Integer follow_up) {
        this.follow_up = follow_up;
    }


    public Integer getId_invito() {
        return id_invito;
    }

    public void setId_invito(Integer id_invito) {
        this.id_invito = id_invito;
    }

    public String getCodts() {
        return codts;
    }

    public void setCodts(String codts) {
        this.codts = codts;
    }

    public Integer getGiorno_nascita() {
        return giorno_nascita;
    }

    public void setGiorno_nascita(Integer giorno_nascita) {
        this.giorno_nascita = giorno_nascita;
    }

    public Integer getMese_nascita() {
        return mese_nascita;
    }

    public void setMese_nascita(Integer mese_nascita) {
        this.mese_nascita = mese_nascita;
    }

    public Integer getCat_richiamo() {
        return cat_richiamo;
    }

    public void setCat_richiamo(Integer cat_richiamo) {
        this.cat_richiamo = cat_richiamo;
    }

    public boolean isEmptyQuery() {
        return emptyQuery;
    }

    public void setEmptyQuery(boolean emptyQuery) {
        this.emptyQuery = emptyQuery;
    }

    public String getSugg_esito() {
        return sugg_esito;
    }

    public void setSugg_esito(String sugg_esito) {
        this.sugg_esito = sugg_esito;
    }

    public String getSugg_descrizione() {
        return sugg_descrizione;
    }

    public void setSugg_descrizione(String sugg_descrizione) {
        this.sugg_descrizione = sugg_descrizione;
    }

    public boolean isDa_revisionare() {
        return da_revisionare;
    }

    public void setDa_revisionare(boolean da_revisionare) {
        this.da_revisionare = da_revisionare;
    }

    public Integer getHpv() {
        return hpv;
    }

    public void setHpv(Integer hpv) {
        this.hpv = hpv;
    }


    public void setPres_no_referto(boolean pres_no_referto) {
        this.pres_no_referto = pres_no_referto;
    }


    public boolean isPres_no_referto() {
        return pres_no_referto;
    }

    /**
     * Metodo che verifica quanti parametri sono stati impostati per la query
     * @return numero di parametri valorizzati dall'utente
     */
    public int getParamNumber() {
        int n = 0;
        if (this.centro_ref != null && this.centro_ref.intValue() > 0)
            n = n + 1;
        if (this.prelievo_dal != null)
            n = n + 1;
        if (this.anno_nascita != null)
            n = n + 1;
        if (this.cognome != null && this.cognome.length() > 0)
            n = n + 1;
        if (this.nome != null && this.nome.length() > 0)
            n = n + 1;
        if (this.centro_prel != null && this.centro_prel.intValue() > 0)
            n = n + 1;
        if (this.inviti_attivi == true)
            n = n + 1;
        if (this.vetrino_dal != null)
            n = n + 1;
        if (this.vetrino_al != null)
            n = n + 1;
        if (this.anno_vetrino != null)
            n = n + 1;
        if (this.pendente == true && this.giorni_pendente != null)
            n = n + 1;
        if (this.follow_up != null && this.follow_up.intValue() > 0)
            n = n + 1;
        if (this.id_invito != null)
            n = n + 1;
        if (this.giorno_nascita != null || this.mese_nascita != null || this.anno_nascita != null)
            n = n + 1;
        if (this.cat_richiamo != null && this.cat_richiamo.intValue() >= 0)
            n = n + 1;
        if (this.hpv != null && this.hpv.intValue() >= 0)
            n = n + 1;
        if (this.da_revisionare == true)
            n = n + 1;
        if (this.pres_no_referto == true)
            n = n + 1;
        if (this.sugg_esito != null && this.sugg_esito.length() > 0 && this.sugg_descrizione != null &&
            this.sugg_descrizione.length() > 0)
            n = n + 1;

        if (this.referti_chiusi != null && !"tutti".equals(this.referti_chiusi))
            n = n + 1;
        if (this.livello != null && !"tutti".equals(this.livello))
            n = n + 1;
        if (this.codts != null && this.codts.length() > 0)
            n = n + 1;
        if (this.idStatoTrial != null && this.idStatoTrial.intValue() >= 0)
            n = n + 1;
        if (this.idBraccioTrial != null && this.idBraccioTrial.intValue() >= 0)
            n = n + 1;
        if (this.codClassePop != null && this.codClassePop.length() > 0)
            n = n + 1;
        if (this.chiave != null && this.chiave.length() > 0)
            n = n + 1;
        
        return n;

    }


    public void setInterm_dal(Date interm_dal) {
        this.interm_dal = interm_dal;
    }


    public Date getInterm_dal() {
        return interm_dal;
    }


    public void setInterm_completi(boolean interm_completi) {
        this.interm_completi = interm_completi;
    }


    public boolean isInterm_completi() {
        return interm_completi;
    }


    public void setIterm_errore(boolean iterm_errore) {
        this.iterm_errore = iterm_errore;
    }


    public boolean isIterm_errore() {
        return iterm_errore;
    }

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


    public void setTest_proposto(String test_proposto) {
        this.test_proposto = test_proposto;
    }


    public String getTest_proposto() {
        return test_proposto;
    }


    public void setPrelievo_hpv(String prelievo_hpv) {
        this.prelievo_hpv = prelievo_hpv;
    }


    public String getPrelievo_hpv() {
        return prelievo_hpv;
    }


    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }


    public String getTipoDocumento() {
        return tipoDocumento;
    }


    public void setCodiceDocumento(String codiceDocumento) {
        this.codiceDocumento = codiceDocumento;
    }


    public String getCodiceDocumento() {
        return codiceDocumento;
    }


    public void setSoloStorico(boolean soloStorico) {
        this.soloStorico = soloStorico;
    }


    public boolean getSoloStorico() {
        return soloStorico;
    }

    public void setCodClassePop(String codClassePop) {
        this.codClassePop = codClassePop;
    }

    public String getCodClassePop() {
        return codClassePop;
    }

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }

    public String getChiave() {
        return chiave;
    }
}
