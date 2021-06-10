package model.accettazione;

import java.math.BigDecimal;

import oracle.jbo.Row;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public interface AccCa_SoInvitoViewRow extends Row {
  BigDecimal getAttivo();

  BigDecimal getChiusuraroundindiv();

  String getCoddomicilio();

  String getCodesitoinvito();

  String getCodresidenza();

  String getCodts();

  BigDecimal getConsenso();

  Integer getDaConfermare();

  oracle.jbo.domain.Date getDtapp();

  oracle.jbo.domain.Date getDtappiniziale();

  oracle.jbo.domain.Date getDtblocco();

  oracle.jbo.domain.Date getDtconscont();

  oracle.jbo.domain.Date getDtcreazione();

  oracle.jbo.domain.Date getDtesamerecente();

  oracle.jbo.domain.Date getDtinviorichiamo();

  oracle.jbo.domain.Date getDtinviosollecito();

  oracle.jbo.domain.Date getDtmodesclusione();

  oracle.jbo.domain.Date getDtrichiamo();

  oracle.jbo.domain.Date getDtritcont();

  oracle.jbo.domain.Date getDtultimamod();

  BigDecimal getFuorifascia();

  Integer getIdapp();

  Integer getIdcentroprelievo();

  Integer getIdcentroref1liv();

  Integer getIdcentroref2liv();

  Integer getIdcentrorichiamo();

  Integer getIdinvito();

  Integer getIdostetrica();

  String getIdtpinvito();

  Integer getLivello();

  Integer getLivesito();

  Integer getMinapp();


    String getNoteinvito();

  String getOpinserimento();

  String getOpmedico();

  String getOpmodifica();

  Integer getOraapp();


    Integer getRoundcomune();

  Integer getRoundindiv();

  Integer getRoundinviti();

  Integer getStatoanag();

  String getTprichiamo();

  String getTpscr();

  String getUlss();

  String getViadomicilio();

  String getViaresidenza();

  BigDecimal getVolontaria();

  Integer getZona();

  void setAttivo(BigDecimal value);

  void setChiusuraroundindiv(BigDecimal value);

  void setCoddomicilio(String value);

  void setCodesitoinvito(String value);

  void setCodresidenza(String value);

  void setCodts(String value);

  void setConsenso(BigDecimal value);

  void setDaConfermare(Integer value);

  void setDtapp(oracle.jbo.domain.Date value);

  void setDtappiniziale(oracle.jbo.domain.Date value);

  void setDtblocco(oracle.jbo.domain.Date value);

  void setDtconscont(oracle.jbo.domain.Date value);

  void setDtcreazione(oracle.jbo.domain.Date value);

  void setDtesamerecente(oracle.jbo.domain.Date value);

  void setDtinviorichiamo(oracle.jbo.domain.Date value);

  void setDtinviosollecito(oracle.jbo.domain.Date value);

  void setDtmodesclusione(oracle.jbo.domain.Date value);

  void setDtrichiamo(oracle.jbo.domain.Date value);

  void setDtritcont(oracle.jbo.domain.Date value);

  void setDtultimamod(oracle.jbo.domain.Date value);

  void setFuorifascia(BigDecimal value);

  void setIdapp(Integer value);

  void setIdcentroprelievo(Integer value);

  void setIdcentroref1liv(Integer value);

  void setIdcentroref2liv(Integer value);

  void setIdcentrorichiamo(Integer value);

  void setIdinvito(Integer value);

  void setIdostetrica(Integer value);

  void setIdtpinvito(String value);

  void setLivello(Integer value);

  void setLivesito(Integer value);

  void setMinapp(Integer value);


    void setNoteinvito(String value);

  void setOpinserimento(String value);

  void setOpmedico(String value);

  void setOpmodifica(String value);

  void setOraapp(Integer value);


    void setRoundcomune(Integer value);

  void setRoundindiv(Integer value);

  void setRoundinviti(Integer value);

  void setStatoanag(Integer value);

  void setTprichiamo(String value);

  void setTpscr(String value);

  void setUlss(String value);

  void setViadomicilio(String value);

  void setViaresidenza(String value);

  void setVolontaria(BigDecimal value);

  void setZona(Integer value);
}