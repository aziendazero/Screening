package model.pianolavoro.common;

import java.math.BigDecimal;

import oracle.jbo.Row;
import oracle.jbo.RowIterator;
import oracle.jbo.domain.Date;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Oct 30 16:16:51 CET 2018
// ---------------------------------------------------------------------
public interface PL_SoAppuntamentoExtendedViewRow extends Row {
    RowIterator getPL_SoEsclusioneDEFExtendedView();

    RowIterator getPL_SoEsclusioneTEMPExtendedView();

    Integer getIdapp();

    void setIdapp(Integer param);

    Integer getIdcentro();

    void setIdcentro(Integer param);

    Date getDtapp();

    void setDtapp(Date param);

    Integer getOraapp();

    void setOraapp(Integer param);

    Integer getMinapp();

    void setMinapp(Integer param);

    Integer getDispslot();

    void setDispslot(Integer param);

    String getTpsrc();

    void setTpsrc(String param);

    String getCentro();

    void setCentro(String param);

    Integer getIdcentro1();

    void setIdcentro1(Integer param);

    String getIndirizzoRes();

    void setIndirizzoRes(String param);

    String getTel();

    void setTel(String param);

    String getCodts();

    void setCodts(String param);

    Integer getIdinvito();

    void setIdinvito(Integer param);

    String getIdtpinvito();

    void setIdtpinvito(String param);

    String getCodesitoinvito();

    void setCodesitoinvito(String param);

    Integer getLivesito();

    void setLivesito(Integer param);

    Integer getIdapp1();

    void setIdapp1(Integer param);

    String getTipoInvito();

    void setTipoInvito(String param);

    String getIdtpinvito1();

    void setIdtpinvito1(String param);

    String getUlss();

    void setUlss(String param);

    String getTpscr();

    void setTpscr(String param);

    String getEsitoinvito();

    void setEsitoinvito(String param);

    String getCodesitoinvito1();

    void setCodesitoinvito1(String param);

    Integer getLivesito1();

    void setLivesito1(Integer param);

    String getUlss1();

    void setUlss1(String param);

    String getTpscr1();

    void setTpscr1(String param);

    String getCodts1();

    void setCodts1(String param);

    String getUlss2();

    void setUlss2(String param);

    String getCodcomscr();

    void setCodcomscr(String param);

    String getCognome();

    void setCognome(String param);

    String getNome();

    void setNome(String param);

    Date getDataNascita();

    void setDataNascita(Date param);

    String getIndirizzoScr();

    void setIndirizzoScr(String param);

    String getTel1();

    void setTel1(String param);

    String getTel2();

    void setTel2(String param);

    String getCodcom();

    void setCodcom(String param);

    String getComune();

    void setComune(String param);

    String getUlss3();

    void setUlss3(String param);

    String getCodpr();

    void setCodpr(String param);

    Integer getIdinvito1();

    void setIdinvito1(Integer param);

    Integer getIdacc1liv();

    void setIdacc1liv(Integer param);


    String getCf();

    void setCf(String param);

    String getCodcom1();

    String getCodcom2();

    String getCodpr1();

    String getCodpr2();

    String getDescrizione();

    String getDescrizione1();

    void setCodcom1(String param);

    void setCodcom2(String param);

    void setCodpr1(String param);

    void setCodpr2(String param);

    void setDescrizione(String param);

    void setDescrizione1(String param);

    String getIndirizzoDom();

    void setIndirizzoDom(String param);

    String getIndirizzoRes1();

    void setIndirizzoRes1(String param);


    Integer getIdaccco1liv();


    void setIdaccco1liv(Integer param);

    String getCodRichiesta();

    Integer getIdaccma1liv();

    String getCap();

    String getCap1();

    String getCap2();

    BigDecimal getCodCampione();

    BigDecimal getNumvetrino();

    void setCodCampione(BigDecimal value);

    void setIdaccma1liv(Integer value);

    void setNumvetrino(BigDecimal value);
}

