package model.commons;


/**
 * Clsse che contiene i dati per un report del tipo piano di lavoro (a priori e
 * a posteriori)
 */
public class PianoLavoroBean extends LetteraBean {
    private String centro;
    private String data;
    private String tipo_invito;
    private String ora;
    private String esito;
    private String escl_def;
    private String escl_temp;
    private String vetrino;
    private String telefono;
    private String roundindiv;
    private String codrichiesta;
    private String codarchsogg;
    /* 02042013 Gaion: aggiunti campi per progetto studio 45 mx*/
    private String mx45_braccio;
    private String mx45_codice;
    /* 02042013 FINE*/
    /* 30092013 Gaion */
    private String codidsogg_mx;
    /* 30092013 FINE */
    private String trialStatoDescr;
    private String trialBraccioDescr;
    private String test_proposto;
    private String prelievo_hpv;
    private String cognome_medico;
    private String nome_medico;
    private String consenso;
    private String consenso_cond;
    private String confermato;

    private String cellConfermato;
    private String otp; // I00098760

    public PianoLavoroBean(String _centro, String _data, String _cognome, String _nome, String _tipo_invito,
                           String _data_nascita, String _indirizzo, String _tessera_sanitaria, String _ora,
                           String _esito, String _escl_def, String _escl_temp, String _vetrino, String _comune,
                           String _provincia, String _telefono, String _cap, String _data_stampa,
                           String _codice_fiscale, String _roundindiv, String _codrichiesta, String _codarchsogg,
                           String _mx45_braccio, String _mx45_codice, String _codidsogg_mx, String _trialStatoDescr,
                           String _trialBraccioDescr, String _email, String _cellulare, String _cognome_medico,
                           String _nome_medico, String _consenso, String _consenso_cond, String _confermato, String _otp) {
        super(_cognome, _nome, _indirizzo, _cap, _comune, _provincia, _data_stampa, _data_nascita, _tessera_sanitaria,
              _codice_fiscale, null, _email, _cellulare);
        this.centro = _centro;
        this.data = _data;
        this.tipo_invito = _tipo_invito;
        this.ora = _ora;
        this.esito = _esito;
        this.escl_def = _escl_def;
        this.escl_temp = _escl_temp;
        this.vetrino = _vetrino;
        this.telefono = _telefono;
        this.roundindiv = _roundindiv;
        this.codrichiesta = _codrichiesta;
        this.codarchsogg = _codarchsogg;
        this.mx45_braccio = _mx45_braccio;
        this.mx45_codice = _mx45_codice;
        this.codidsogg_mx = _codidsogg_mx;
        this.trialStatoDescr = _trialStatoDescr;
        this.trialBraccioDescr = _trialBraccioDescr;
        this.cognome_medico = _cognome_medico;
        this.nome_medico = _nome_medico;
        this.consenso = _consenso;
        this.consenso_cond = _consenso_cond;
        this.confermato = _confermato;
        this.otp = _otp;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTipo_invito() {
        return tipo_invito;
    }

    public void setTipo_invito(String tipo_invito) {
        this.tipo_invito = tipo_invito;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getEsito() {
        return esito;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    public String getEscl_def() {
        return escl_def;
    }

    public void setEscl_def(String escl_def) {
        this.escl_def = escl_def;
    }

    public String getEscl_temp() {
        return escl_temp;
    }

    public void setEscl_temp(String escl_temp) {
        this.escl_temp = escl_temp;
    }

    public String getVetrino() {
        return vetrino;
    }

    public void setVetrino(String vetrino) {
        this.vetrino = vetrino;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRoundindiv() {
        return roundindiv;
    }

    public void setRoundindiv(String roundindiv) {
        this.roundindiv = roundindiv;
    }

    public void setCodrichiesta(String codrichiesta) {
        this.codrichiesta = codrichiesta;
    }

    public String getCodrichiesta() {
        return codrichiesta;
    }

    public void setCodarchsogg(String codarchsogg) {
        this.codarchsogg = codarchsogg;
    }

    public String getCodarchsogg() {
        return codarchsogg;
    }

    public void setMx45_braccio(String mx45_braccio) {
        this.mx45_braccio = mx45_braccio;
    }

    public String getMx45_braccio() {
        return mx45_braccio;
    }

    public void setMx45_codice(String mx45_codice) {
        this.mx45_codice = mx45_codice;
    }

    public String getMx45_codice() {
        return mx45_codice;
    }

    public void setCodidsogg_mx(String codidsogg_mx) {
        this.codidsogg_mx = codidsogg_mx;
    }

    public String getCodidsogg_mx() {
        return codidsogg_mx;
    }

    public String getTrialStatoDescr() {
        return trialStatoDescr;
    }

    public void setTrialStatoDescr(String descr) {
        trialStatoDescr = descr;
    }

    public String getTrialBraccioDescr() {
        return trialBraccioDescr;
    }

    public void setTrialBraccioDescr(String descr) {
        trialBraccioDescr = descr;
    }

    public void setTest_proposto(String testProposto) {
        this.test_proposto = testProposto;
    }

    public String getTest_proposto() {
        return test_proposto;
    }

    public void setPrelievo_hpv(String prelievoHpv) {
        this.prelievo_hpv = prelievoHpv;
    }

    public String getPrelievo_hpv() {
        return prelievo_hpv;
    }

    public void setCognome_medico(String cognome_medico) {
        this.cognome_medico = cognome_medico;
    }

    public String getCognome_medico() {
        return cognome_medico;
    }

    public void setNome_medico(String nome_medico) {
        this.nome_medico = nome_medico;
    }

    public String getNome_medico() {
        return nome_medico;
    }

    public void setConsenso(String consenso) {
        this.consenso = consenso;
    }

    public String getConsenso() {
        return consenso;
    }

    public void setConsenso_cond(String consenso_cond) {
        this.consenso_cond = consenso_cond;
    }

    public String getConsenso_cond() {
        return consenso_cond;
    }

    public void setConfermato(String confermato) {
        this.confermato = confermato;
    }

    public String getConfermato() {
        return confermato;
    }

    public void setCellConfermato(String cellConfermato) {
        this.cellConfermato = cellConfermato;
    }

    public String getCellConfermato() {
        return cellConfermato;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }
}
