package model.accettazione.client;

import java.math.BigDecimal;

import oracle.jbo.client.remote.RowImpl;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Timestamp;

public class Acc_RicInvitiViewRowClient extends RowImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public Acc_RicInvitiViewRowClient() {
    }


    public String getCentroPrel() {
        return (String) getAttribute("CentroPrel");
    }

    public String getCf() {
        return (String) getAttribute("Cf");
    }

    public BigDecimal getCodRichiesta() {
        return (BigDecimal) getAttribute("CodRichiesta");
    }

    public String getCodesitoinvito() {
        return (String) getAttribute("Codesitoinvito");
    }

    public String getCodidsoggMx() {
        return (String) getAttribute("CodidsoggMx");
    }

    public String getCodts() {
        return (String) getAttribute("Codts");
    }

    public String getCognome() {
        return (String) getAttribute("Cognome");
    }

    public Integer getConfermato() {
        return (Integer) getAttribute("Confermato");
    }

    public Integer getConsenso() {
        return (Integer) getAttribute("Consenso");
    }

    public Integer getConsensoCond() {
        return (Integer) getAttribute("ConsensoCond");
    }

    public Date getDataNascita() {
        return (Date) getAttribute("DataNascita");
    }

    public String getDescEsito() {
        return (String) getAttribute("DescEsito");
    }

    public String getDescTpinv() {
        return (String) getAttribute("DescTpinv");
    }

    public Date getDtapp() {
        return (Date) getAttribute("Dtapp");
    }

    public Timestamp getDtoraapp() {
        return (Timestamp) getAttribute("Dtoraapp");
    }

    public String getEscl() {
        return (String) getAttribute("Escl");
    }

    public Integer getEta() {
        return (Integer) getAttribute("Eta");
    }

    public Integer getIdbraccioTrial() {
        return (Integer) getAttribute("IdbraccioTrial");
    }

    public Integer getIdcentro1liv() {
        return (Integer) getAttribute("Idcentro1liv");
    }

    public Integer getIdcentro2liv() {
        return (Integer) getAttribute("Idcentro2liv");
    }

    public Integer getIdcentroprelievo() {
        return (Integer) getAttribute("Idcentroprelievo");
    }

    public Integer getIdcref() {
        return (Integer) getAttribute("Idcref");
    }

    public Integer getIdinvito() {
        return (Integer) getAttribute("Idinvito");
    }

    public Integer getIdstatoTrial() {
        return (Integer) getAttribute("IdstatoTrial");
    }

    public Integer getLivello() {
        return (Integer) getAttribute("Livello");
    }

    public String getNome() {
        return (String) getAttribute("Nome");
    }

    public BigDecimal getNumvetrino() {
        return (BigDecimal) getAttribute("Numvetrino");
    }

    public Integer getPrelievoHpv() {
        return (Integer) getAttribute("PrelievoHpv");
    }

    public Boolean getSelezionato() {
        return (Boolean) getAttribute("Selezionato");
    }

    public Integer getSollecitare() {
        return (Integer) getAttribute("Sollecitare");
    }

    public String getTestProposto() {
        return (String) getAttribute("TestProposto");
    }

    public String getTpscr() {
        return (String) getAttribute("Tpscr");
    }

    public String getTrialBraccioDescr() {
        return (String) getAttribute("TrialBraccioDescr");
    }

    public String getTrialBraccioDescrbreve() {
        return (String) getAttribute("TrialBraccioDescrbreve");
    }

    public String getTrialStatoDescr() {
        return (String) getAttribute("TrialStatoDescr");
    }

    public String getTrialStatoDescrbreve() {
        return (String) getAttribute("TrialStatoDescrbreve");
    }

    public String getUlss() {
        return (String) getAttribute("Ulss");
    }

    public Integer getVaccHpv() {
        return (Integer) getAttribute("VaccHpv");
    }

    public String getVaccinatoHpv() {
        return (String) getAttribute("VaccinatoHpv");
    }

    public void setCentroPrel(String value) {
        setAttribute("CentroPrel", value);
    }

    public void setCf(String value) {
        setAttribute("Cf", value);
    }

    public void setCodRichiesta(BigDecimal value) {
        setAttribute("CodRichiesta", value);
    }

    public void setCodesitoinvito(String value) {
        setAttribute("Codesitoinvito", value);
    }

    public void setCodidsoggMx(String value) {
        setAttribute("CodidsoggMx", value);
    }

    public void setCodts(String value) {
        setAttribute("Codts", value);
    }

    public void setCognome(String value) {
        setAttribute("Cognome", value);
    }

    public void setConsenso(Integer value) {
        setAttribute("Consenso", value);
    }

    public void setDataNascita(Date value) {
        setAttribute("DataNascita", value);
    }

    public void setDescEsito(String value) {
        setAttribute("DescEsito", value);
    }

    public void setDescTpinv(String value) {
        setAttribute("DescTpinv", value);
    }

    public void setDtapp(Date value) {
        setAttribute("Dtapp", value);
    }

    public void setDtoraapp(Timestamp value) {
        setAttribute("Dtoraapp", value);
    }

    public void setEscl(String value) {
        setAttribute("Escl", value);
    }

    public void setEta(Integer value) {
        setAttribute("Eta", value);
    }

    public void setIdcentro1liv(Integer value) {
        setAttribute("Idcentro1liv", value);
    }

    public void setIdcentro2liv(Integer value) {
        setAttribute("Idcentro2liv", value);
    }

    public void setIdcentroprelievo(Integer value) {
        setAttribute("Idcentroprelievo", value);
    }

    public void setIdcref(Integer value) {
        setAttribute("Idcref", value);
    }

    public void setIdinvito(Integer value) {
        setAttribute("Idinvito", value);
    }

    public void setLivello(Integer value) {
        setAttribute("Livello", value);
    }

    public void setNome(String value) {
        setAttribute("Nome", value);
    }

    public void setNumvetrino(BigDecimal value) {
        setAttribute("Numvetrino", value);
    }

    public void setSelezionato(Boolean value) {
        setAttribute("Selezionato", value);
    }

    public void setSollecitare(Integer value) {
        setAttribute("Sollecitare", value);
    }

    public void setTpscr(String value) {
        setAttribute("Tpscr", value);
    }

    public void setUlss(String value) {
        setAttribute("Ulss", value);
    }

    public void setVaccHpv(Integer value) {
        setAttribute("VaccHpv", value);
    }

    public void setVaccinatoHpv(String value) {
        setAttribute("VaccinatoHpv", value);
    }
}

