package model.referto.common;
import oracle.jbo.Row;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public interface Ref_SoRefertomammo1livViewRow extends Row {
    Integer getIdreferto();

  void setIdreferto(Integer value);

    Integer getIdinvito();

  void setIdinvito(Integer value);

  String getCodts();

  void setCodts(String value);

  String getUlss();

  void setUlss(String value);

  String getTpscr();

  void setTpscr(String value);

    Integer getL1Radiologo();

  void setL1Radiologo(Integer value);

    Integer getL1Esito();

  void setL1Esito(Integer value);

  String getL1AltreIndicazioni();

  void setL1AltreIndicazioni(String value);

  String getL1Note();

  void setL1Note(String value);

    Integer getL2Radiologo();

  void setL2Radiologo(Integer value);

    Integer getL2Esito();

  void setL2Esito(Integer value);

  String getL2AltreIndicazioni();

  void setL2AltreIndicazioni(String value);

  String getL2Note();

  void setL2Note(String value);

    Integer getRRadiologo();

  void setRRadiologo(Integer value);

    Integer getREsito();

  void setREsito(Integer value);

  String getRAltreIndicazioni();

  void setRAltreIndicazioni(String value);

  String getRNote();

  void setRNote(String value);

    Integer getIdsugg();

  void setIdsugg(Integer value);

  oracle.jbo.domain.Date getDtreferto();

  void setDtreferto(oracle.jbo.domain.Date value);


    Integer getCompleto();

  void setCompleto(Integer value);

  oracle.jbo.domain.Date getDtinserimento();

  void setDtinserimento(oracle.jbo.domain.Date value);

  String getOpinserimento();

  void setOpinserimento(String value);

  oracle.jbo.domain.Date getDtultmodifica();

  void setDtultmodifica(oracle.jbo.domain.Date value);

  String getOpultmodifica();

  void setOpultmodifica(String value);

    Integer getTpreferto();

  void setTpreferto(Integer value);

    Integer getIdallegato();

  void setIdallegato(Integer value);

  oracle.jbo.domain.Date getDtcreazione();

  void setDtcreazione(oracle.jbo.domain.Date value);

  oracle.jbo.domain.Date getDtspedizione();

  void setDtspedizione(oracle.jbo.domain.Date value);

  String getCodiceReferto();

  void setCodiceReferto(String value);

    Integer getIdcentroref();

  void setIdcentroref(Integer value);

  oracle.jbo.RowIterator getRef_SoCnfSugg1livView();

  oracle.jbo.RowIterator getRef_SoCodref1livmaView();

  oracle.jbo.RowIterator getRef_SoCodref1livmaView1();

  oracle.jbo.RowIterator getRef_SoCodref1livmaView2();

  oracle.jbo.RowIterator getRef_SoCodref1livmaView3();

  oracle.jbo.domain.Date getDtmammo();

    Integer getEsito();

  String getGrEsito();

    Integer getL1Sugg();

    Integer getL2Sugg();

    Integer getRSugg();

    Integer getRevisione();

  void setDtmammo(oracle.jbo.domain.Date value);

  void setEsito(Integer value);

  void setGrEsito(String value);

  void setL1Sugg(Integer value);

  void setL2Sugg(Integer value);

  void setRSugg(Integer value);

  void setRevisione(Integer value);

    Integer getDensita();

    void setDensita(Integer value);
}
