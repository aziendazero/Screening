package model.common;

import oracle.jbo.Row;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public interface A_SoMedicoViewRow extends Row {
    Integer getCodiceregmedico();

  void setCodiceregmedico(Integer value);

  String getCodcom();

  void setCodcom(String value);

  String getCognome();

  void setCognome(String value);

  String getNome();

  void setNome(String value);

  String getIndirizzoRes();

  void setIndirizzoRes(String value);

  String getTel();

  void setTel(String value);

    Integer getCell();

  void setCell(Integer value);

  oracle.jbo.domain.Date getDtadesione();

  void setDtadesione(oracle.jbo.domain.Date value);

  oracle.jbo.domain.Date getDtfinevalop();

  void setDtfinevalop(oracle.jbo.domain.Date value);

  String getUlss();

  void setUlss(String value);
}