package model.common;

import oracle.jbo.Row;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public interface Cnf_SoAziendaViewRow extends Row {
  String getCodaz();

  void setCodaz(String value);

  String getCodipa();

  void setCodipa(String value);

  oracle.jbo.domain.BlobDomain getLogo();

  void setLogo(oracle.jbo.domain.BlobDomain value);

  Integer getEtacitomin();

  void setEtacitomin(Integer value);

  Integer getEtacitomax();

  void setEtacitomax(Integer value);

  Integer getEtamammomin();

  void setEtamammomin(Integer value);

  Integer getEtamammomax();

  void setEtamammomax(Integer value);

  Integer getEtacolonmin();

  void setEtacolonmin(Integer value);

  Integer getEtacolonmax();

  void setEtacolonmax(Integer value);

  oracle.jbo.RowIterator getCnf_SoCnfPartemplateView();

  String getDescrizione();

  oracle.jbo.domain.Timestamp getDtultagganag();

  Integer getDurataMsgChiusuraRound();

  Integer getGgChiusuraRound();

  void setDescrizione(String value);

  void setDtultagganag(oracle.jbo.domain.Timestamp value);

  void setDurataMsgChiusuraRound(Integer value);

  void setGgChiusuraRound(Integer value);
}