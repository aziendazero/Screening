package model.conf.common;

import oracle.jbo.Row;
import oracle.jbo.RowIterator;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Aug 25 12:38:16 CEST 2017
// ---------------------------------------------------------------------
public interface Cnf_SoAppuntamentoViewRow extends Row {
    Integer getIdapp();

    void setIdapp(Integer value);

    Integer getIdcentro();

    void setIdcentro(Integer value);

    oracle.jbo.domain.Date getDtapp();

    void setDtapp(oracle.jbo.domain.Date value);

    Integer getOraapp();

    void setOraapp(Integer value);

    Integer getMinapp();

    void setMinapp(Integer value);

    Integer getDispslot();

    void setDispslot(Integer value);

    String getTpsrc();

    void setTpsrc(String value);

    void setSlotoccup(Integer value);

    Integer getNascosto();

    void setNascosto(Integer value);

    RowIterator getCnf_SoInvitoView();

    Integer getSlotoccup();
}

