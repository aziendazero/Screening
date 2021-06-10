package model.accettazione.common;

import java.math.BigDecimal;

import oracle.jbo.Row;
import oracle.jbo.domain.Date;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Jun 19 10:30:53 CEST 2018
// ---------------------------------------------------------------------
public interface AccPf_SoAccPfas1livViewRow extends Row {
    BigDecimal getCodCampione();

    BigDecimal getCodRichiesta();

    Integer getDonatore();

    Date getDtcreazione();

    Date getDtultmod();

    Integer getIdaccpf1liv();

    Integer getIdinvito();

    Integer getMiteni();

    String getOpcreazione();

    String getOpultmod();

    void setCodCampione(BigDecimal value);

    void setCodRichiesta(BigDecimal value);

    void setDonatore(Integer value);

    void setDtcreazione(Date value);

    void setDtultmod(Date value);

    void setIdaccpf1liv(Integer value);

    void setIdinvito(Integer value);

    void setMiteni(Integer value);

    void setOpcreazione(String value);

    void setOpultmod(String value);
}

