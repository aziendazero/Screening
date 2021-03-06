package model.accettazione.common;

import java.math.BigDecimal;

import oracle.jbo.Row;
import oracle.jbo.domain.Date;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Jun 19 10:33:50 CEST 2018
// ---------------------------------------------------------------------
public interface AccPf_SoAccPfas2livViewRow extends Row {
    BigDecimal getCodCampione();

    BigDecimal getCodRichiesta();

    Date getDtcreazione();

    Date getDtultmod();

    Integer getIdaccpf2liv();

    Integer getIdinvito();

    String getNote();

    String getOpcreazione();

    String getOpultmod();

    void setCodCampione(BigDecimal value);

    void setCodRichiesta(BigDecimal value);

    void setDtcreazione(Date value);

    void setDtultmod(Date value);

    void setIdaccpf2liv(Integer value);

    void setIdinvito(Integer value);

    void setNote(String value);

    void setOpcreazione(String value);

    void setOpultmod(String value);
}

