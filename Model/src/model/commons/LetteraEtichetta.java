package model.commons;


public class LetteraEtichetta extends LetteraBean {

    private String telefono;
    private String centro;
    private String vetrino;
    private String codrichiesta;
    private String data_invito;
    private String codarchsogg;
    private String nome_medico;
    private String cognome_medico;
    private String indirizzo_medico;
    private String cap_medico;
    private String comune_medico;
    private String provincia_medico;
    /* 02042013 GAIOn: aggiunti campi per progetto studio 45 mx*/
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
    private String confermato;
    private String id_procedura;
    private String data_procedura;
    private String tipo_invito;
    private String centro_referto;

    private String cellConfermato;
    private String otp; // I00098760

    public LetteraEtichetta(String _cognome, String _nome, String _indirizzo, String _cap, String _comune,
                            String _provincia, String _data_stampa, String _tessera_sanitaria, String _codice_fiscale,
                            String _data_nascita, String _telefono, String _centro, String _vetrino,
                            String _codrichiesta, String _data_invito, String _codarchsogg,
                            //05112014
                            String _email, String _cellulare, String _tipo_invito) {
        super(_cognome, _nome, _indirizzo, _cap, _comune, _provincia, _data_stampa, _data_nascita, _tessera_sanitaria,
              _codice_fiscale, null, _email, _cellulare);
        this.telefono = _telefono;
        this.centro = _centro;
        this.vetrino = _vetrino;
        this.codrichiesta = _codrichiesta;
        this.data_invito = _data_invito;
        this.codarchsogg = _codarchsogg;
        this.tipo_invito = _tipo_invito;
    }

    // mauro 20/01/2010, aggiunto costruttore con dati del medico
    public LetteraEtichetta(String _cognome, String _nome, String _indirizzo, String _cap, String _comune,
                            String _provincia, String _data_stampa, String _tessera_sanitaria, String _codice_fiscale,
                            String _data_nascita, String _telefono, String _centro, String _vetrino,
                            String _codrichiesta, String _data_invito, String _codarchsogg, String _nome_medico,
                            String _cognome_medico, String _indirizzo_medico, String _cap_medico, String _comune_medico,
                            String _provincia_medico, String _mx45_braccio, String _mx45_codice, String _codidsogg_mx,
                            String _trialStatoDescr, String _trialBraccioDescr,
                            //05112014
                            String _email, String _cellulare, String _confermato, String _tipo_invito,
                            String _centro_referto, String _otp) {
        /*super(_cognome, _nome, _indirizzo, _cap, _comune, _provincia, _data_stampa, _data_nascita, _tessera_sanitaria,
              _codice_fiscale, null, _email, _cellulare);
        this.telefono = _telefono;
        this.centro = _centro;
        this.vetrino = _vetrino;
        this.codrichiesta = _codrichiesta;
        this.data_invito = _data_invito;
        this.codarchsogg = _codarchsogg;
        this.tipo_invito = _tipo_invito;*/
        this(_cognome, _nome, _indirizzo, _cap, _comune, _provincia, _data_stampa, _tessera_sanitaria, _codice_fiscale,
             _data_nascita, _telefono, _centro, _vetrino, _codrichiesta, _data_invito, _codarchsogg, _email, _cellulare,
             _tipo_invito);
        this.nome_medico = _nome_medico;
        this.cognome_medico = _cognome_medico;
        this.indirizzo_medico = _indirizzo_medico;
        this.cap_medico = _cap_medico;
        this.comune_medico = _comune_medico;
        this.provincia_medico = _provincia_medico;
        this.mx45_braccio = _mx45_braccio;
        this.mx45_codice = _mx45_codice;
        this.codidsogg_mx = _codidsogg_mx;
        this.trialStatoDescr = _trialStatoDescr;
        this.trialBraccioDescr = _trialBraccioDescr;
        this.confermato = _confermato;
        this.centro_referto = _centro_referto;
        this.otp = _otp;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono1) {
        this.telefono = telefono1;
    }

    public String getCentro() {
        return centro;
    }

    public void setCentro(String centro) {
        this.centro = centro;
    }

    public String getVetrino() {
        return vetrino;
    }

    public void setVetrino(String vetrino) {
        this.vetrino = vetrino;
    }

    public String getCodrichiesta() {
        return codrichiesta;
    }

    public void setCodrichiesta(String codrichiesta) {
        this.codrichiesta = codrichiesta;
    }

    public String getData_invito() {
        return data_invito;
    }

    public void setData_invito(String data_invito) {
        this.data_invito = data_invito;
    }

    public void setCodarchsogg(String codarchsogg) {
        this.codarchsogg = codarchsogg;
    }

    public String getCodarchsogg() {
        return codarchsogg;
    }

    public void setNome_medico(String nome_medico) {
        this.nome_medico = nome_medico;
    }

    public String getNome_medico() {
        return nome_medico;
    }

    public void setCognome_medico(String cognome_medico) {
        this.cognome_medico = cognome_medico;
    }

    public String getCognome_medico() {
        return cognome_medico;
    }

    public void setIndirizzo_medico(String indirizzo_medico) {
        this.indirizzo_medico = indirizzo_medico;
    }

    public String getIndirizzo_medico() {
        return indirizzo_medico;
    }

    public void setCap_medico(String cap_medico) {
        this.cap_medico = cap_medico;
    }

    public String getCap_medico() {
        return cap_medico;
    }

    public void setComune_medico(String comune_medico) {
        this.comune_medico = comune_medico;
    }

    public String getComune_medico() {
        return comune_medico;
    }

    public void setProvincia_medico(String provincia_medico) {
        this.provincia_medico = provincia_medico;
    }

    public String getProvincia_medico() {
        return provincia_medico;
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

    public void setConfermato(String confermato) {
        this.confermato = confermato;
    }

    public String getConfermato() {
        return confermato;
    }

    public void setId_procedura(String id_procedura) {
        this.id_procedura = id_procedura;
    }

    public String getId_procedura() {
        return id_procedura;
    }

    public void setData_procedura(String data_procedura) {
        this.data_procedura = data_procedura;
    }

    public String getData_procedura() {
        return data_procedura;
    }

    public void setTipo_invito(String tipo_invito) {
        this.tipo_invito = tipo_invito;
    }

    public String getTipo_invito() {
        return tipo_invito;
    }

    public void setCentro_referto(String centro_referto) {
        this.centro_referto = centro_referto;
    }

    public String getCentro_referto() {
        return centro_referto;
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
