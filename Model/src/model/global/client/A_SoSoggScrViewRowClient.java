package model.global.client;

import model.global.common.A_SoSoggScrViewRow;

import oracle.jbo.client.remote.RowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Nov 14 14:42:33 CET 2013
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class A_SoSoggScrViewRowClient extends RowImpl {
	/**
	 * This is the default constructor (do not remove).
	 */
	public A_SoSoggScrViewRowClient() {
	}


    public Integer getAltorischio() {
        return (Integer) getAttribute("Altorischio");
    }

    public String getCodclassepop() {
        return (String) getAttribute("Codclassepop");
    }

    public String getCodidsoggMx() {
        return (String) getAttribute("CodidsoggMx");
    }

    public String getCodts() {
        return (String) getAttribute("Codts");
    }

    public String getMx45Braccio() {
        return (String) getAttribute("Mx45Braccio");
    }

    public String getMx45Codice() {
        return (String) getAttribute("Mx45Codice");
    }

    public Integer getNumanatomia() {
        return (Integer) getAttribute("Numanatomia");
    }

    public Integer getNumlab() {
        return (Integer) getAttribute("Numlab");
    }

    public Integer getNumradiologia() {
        return (Integer) getAttribute("Numradiologia");
    }

    public Integer getRoundindiv() {
        return (Integer) getAttribute("Roundindiv");
    }

    public Integer getRoundindivHpv() {
        return (Integer) getAttribute("RoundindivHpv");
    }

    public Integer getRoundinviti() {
        return (Integer) getAttribute("Roundinviti");
    }

    public String getTpscr() {
        return (String) getAttribute("Tpscr");
    }

    public String getUlss() {
        return (String) getAttribute("Ulss");
    }

    public void setAltorischio(Integer value) {
        setAttribute("Altorischio", value);
    }

    public void setCodclassepop(String value) {
        setAttribute("Codclassepop", value);
    }

    public void setCodidsoggMx(String value) {
        setAttribute("CodidsoggMx", value);
    }

    public void setCodts(String value) {
        setAttribute("Codts", value);
    }

    public void setMx45Braccio(String value) {
        setAttribute("Mx45Braccio", value);
    }

    public void setMx45Codice(String value) {
        setAttribute("Mx45Codice", value);
    }

    public void setNumanatomia(Integer value) {
        setAttribute("Numanatomia", value);
    }

    public void setNumlab(Integer value) {
        setAttribute("Numlab", value);
    }

    public void setNumradiologia(Integer value) {
        setAttribute("Numradiologia", value);
    }

    public void setRoundindiv(Integer value) {
        setAttribute("Roundindiv", value);
    }

    public void setRoundindivHpv(Integer value) {
        setAttribute("RoundindivHpv", value);
    }

    public void setRoundinviti(Integer value) {
        setAttribute("Roundinviti", value);
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }

    public void setUlss(String value) {
        setAttribute("Ulss", value);
    }
}
