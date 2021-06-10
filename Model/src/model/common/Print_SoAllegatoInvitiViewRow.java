package model.common;

import java.math.BigDecimal;

import oracle.jbo.Row;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public interface Print_SoAllegatoInvitiViewRow extends Row {
  Integer getIdallegato();

  void setIdallegato(Integer value);

  Integer getIdinvito();

  void setIdinvito(Integer value);

  String getCodts();

  void setCodts(String value);

  oracle.jbo.domain.BlobDomain getLettera();

  void setLettera(oracle.jbo.domain.BlobDomain value);





  Integer getStampapostel();

  void setStampapostel(Integer value);

  String getUlss();

  void setUlss(String value);

  String getTpscr();

  void setTpscr(String value);

  Integer getIdinvito1();

  void setIdinvito1(Integer value);

  String getIdtpinvito();

  void setIdtpinvito(String value);

  Integer getIdcentroprelievo();

  void setIdcentroprelievo(Integer value);

  oracle.jbo.domain.Date getDtapp();

  void setDtapp(oracle.jbo.domain.Date value);

  Integer getOraapp();

  void setOraapp(Integer value);

  Integer getMinapp();

  void setMinapp(Integer value);

  String getCodts1();

  void setCodts1(String value);

  String getUlss1();

  void setUlss1(String value);

  String getCognome();

  void setCognome(String value);

  String getNome();

  void setNome(String value);

  oracle.jbo.domain.Date getDataNascita();

  void setDataNascita(oracle.jbo.domain.Date value);

  String getSesso();

  void setSesso(String value);

  String getTel1();

  void setTel1(String value);

  String getTel2();

  void setTel2(String value);

  String getIndirizzoScr();

  void setIndirizzoScr(String value);

  String getCodcomscr();

  void setCodcomscr(String value);


  String getCodpr();



  String getCentroPrel();

  String getCf();

  BigDecimal getCodCampione();

  String getCodcom();

  String getCodcomdom();

  String getCodcomres();

  String getCodpr1();

  String getCodpr2();

  String getComuneDesc();

  oracle.jbo.domain.Timestamp getData_ora_order();

  String getDescrizione1();

  String getDescrizione2();

  oracle.jbo.domain.Timestamp getDtcreazione();

  oracle.jbo.domain.Timestamp getDtstampa();

  Integer getIdacc1liv();

  Integer getIdaccco1liv();

  Integer getIdcentro();

  Integer getIdinvito2();

  String getIndirizzoDom();

  String getIndirizzoRes();

  BigDecimal getNumvetrino();




  void setCentroPrel(String value);

  void setCf(String value);

  void setCodCampione(BigDecimal value);

  void setCodcom(String value);

  void setCodcomdom(String value);

  void setCodcomres(String value);

  void setCodpr(String value);

  void setCodpr1(String value);

  void setCodpr2(String value);

  void setComuneDesc(String value);

  void setData_ora_order(oracle.jbo.domain.Timestamp value);

  void setDescrizione1(String value);

  void setDescrizione2(String value);

  void setDtcreazione(oracle.jbo.domain.Timestamp value);

  void setDtstampa(oracle.jbo.domain.Timestamp value);

  void setIdacc1liv(Integer value);

  void setIdaccco1liv(Integer value);

  void setIdcentro(Integer value);

  void setIdinvito2(Integer value);

  void setIndirizzoDom(String value);

  void setIndirizzoRes(String value);

  void setNumvetrino(BigDecimal value);




  String getCodRichiesta();

  Integer getIdaccma1liv();

  Integer getNonSpedireReferto();

  Integer getRaccLettInvito();

  Integer getRaccLettReferto();




  void setCodRichiesta(String value);

  void setIdaccma1liv(Integer value);

  void setNonSpedireReferto(Integer value);

  void setRaccLettInvito(Integer value);

  void setRaccLettReferto(Integer value);

  String getCap();

  String getCap1();

  String getCap2();

  void setCap(String value);

  void setCap1(String value);

  void setCap2(String value);




}