package view.postel;

public class PostelBean {
    private String zcode;
    private String username;
    private String password;
    private String eUserZCode;
    private String csdg;
    private String ragionesocialemittente;
    private String indirizzomittente;
    private String capmittente;
    private String cittamittente;
    private String provinciamittente;
    private String nazionemittente;
    private int workprocessID;
    private int workprocessIDraccomandata;
    private String type;

    public PostelBean() {
    }

    public String getZcode() {
        return zcode;
    }

    public void setZcode(String zcode) {
        this.zcode = zcode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEUserZCode() {
        return eUserZCode;
    }

    public void setEUserZCode(String eUserZCode) {
        this.eUserZCode = eUserZCode;
    }

    public String getCsdg() {
        return csdg;
    }

    public void setCsdg(String csdg) {
        this.csdg = csdg;
    }

    public String getRagionesocialemittente() {
        return ragionesocialemittente;
    }

    public void setRagionesocialemittente(String ragionesocialemittente) {
        this.ragionesocialemittente = ragionesocialemittente;
    }

    public String getIndirizzomittente() {
        return indirizzomittente;
    }

    public void setIndirizzomittente(String indirizzomittente) {
        this.indirizzomittente = indirizzomittente;
    }

    public String getCapmittente() {
        return capmittente;
    }

    public void setCapmittente(String capmittente) {
        this.capmittente = capmittente;
    }

    public String getCittamittente() {
        return cittamittente;
    }

    public void setCittamittente(String cittamittente) {
        this.cittamittente = cittamittente;
    }

    public String getProvinciamittente() {
        return provinciamittente;
    }

    public void setProvinciamittente(String provinciamittente) {
        this.provinciamittente = provinciamittente;
    }

    public String getNazionemittente() {
        return nazionemittente;
    }

    public void setNazionemittente(String nazionemittente) {
        this.nazionemittente = nazionemittente;
    }

    public int getWorkprocessID() {
        return workprocessID;
    }

    public void setWorkprocessID(int workprocessID) {
        this.workprocessID = workprocessID;
    }

    public void setWorkprocessID(String workprocessID) {
        this.workprocessID = Integer.parseInt(workprocessID);
    }

    public int getWorkprocessIDraccomandata() {
        return workprocessIDraccomandata;
    }

    public void setWorkprocessIDraccomandata(int workprocessIDraccomandata) {
        this.workprocessIDraccomandata = workprocessIDraccomandata;
    }

    public void setWorkprocessIDraccomandata(String workprocessIDraccomandata) {
        this.workprocessIDraccomandata = Integer.parseInt(workprocessIDraccomandata);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
