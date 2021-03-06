package model.accettazione;

import java.math.BigDecimal;

import oracle.jbo.Row;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public interface AccPf_SoAccPfas1livViewRow extends Row {
  BigDecimal getCodCampione();

  BigDecimal getCodRichiesta();

  Integer getDonatore();

  oracle.jbo.domain.Date getDtcreazione();

  oracle.jbo.domain.Date getDtultmod();

  Integer getIdaccpf1liv();

  Integer getIdinvito();

  Integer getMiteni();

  String getOpcreazione();

  String getOpultmod();

  void setCodCampione(BigDecimal value);

  void setCodRichiesta(BigDecimal value);

  void setDonatore(Integer value);

  void setDtcreazione(oracle.jbo.domain.Date value);

  void setDtultmod(oracle.jbo.domain.Date value);

  void setIdaccpf1liv(Integer value);

  void setIdinvito(Integer value);

  void setMiteni(Integer value);

  void setOpcreazione(String value);

  void setOpultmod(String value);
}