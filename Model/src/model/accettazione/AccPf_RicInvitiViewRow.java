package model.accettazione;

import java.math.BigDecimal;

import oracle.jbo.Row;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public interface AccPf_RicInvitiViewRow extends Row {
  Integer getAltorischio();

  String getCentroPrel();

  String getCf();


    String getCodesitoinvito();

  String getCodidsoggMx();

  String getCodts();

  String getCognome();

  Integer getConfermato();

  Integer getConsenso();

  oracle.jbo.domain.Date getDataNascita();

  String getDescEsito();

  String getDescTpinv();

  oracle.jbo.domain.Date getDtapp();

  oracle.jbo.domain.Timestamp getDtoraapp();

  String getEscl();

  Integer getEta();

  Integer getIdcentro1liv();

  Integer getIdcentro2liv();

  Integer getIdcentroprelievo();

  Integer getIdcref();

  Integer getIdinvito();

  Integer getLivello();

  String getNome();

  Boolean getSelezionato();

  Integer getSollecitare();

  String getTpscr();

  String getUlss();

  void setAltorischio(Integer value);

  void setCentroPrel(String value);

  void setCf(String value);


    void setCodesitoinvito(String value);

  void setCodidsoggMx(String value);

  void setCodts(String value);

  void setCognome(String value);

  void setConfermato(Integer value);

  void setConsenso(Integer value);

  void setDataNascita(oracle.jbo.domain.Date value);

  void setDescEsito(String value);

  void setDescTpinv(String value);

  void setDtapp(oracle.jbo.domain.Date value);

  void setDtoraapp(oracle.jbo.domain.Timestamp value);

  void setEscl(String value);

  void setEta(Integer value);

  void setIdcentro1liv(Integer value);

  void setIdcentro2liv(Integer value);

  void setIdcentroprelievo(Integer value);

  void setIdcref(Integer value);

  void setIdinvito(Integer value);

  void setLivello(Integer value);

  void setNome(String value);

  void setSelezionato(Boolean value);

  void setSollecitare(Integer value);

  void setTpscr(String value);

  void setUlss(String value);

    void setCodCampione(BigDecimal value);
}
