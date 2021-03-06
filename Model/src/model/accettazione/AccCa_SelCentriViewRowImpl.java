package model.accettazione;

import oracle.jbo.domain.Date;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Mon Nov 13 16:20:19 CET 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class AccCa_SelCentriViewRowImpl extends ViewRowImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Idcentro,
        Descrbreve,
        Ulss,
        Tpscr,
        Tipocentro,
        Dtchperiodo,
        Chapp;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static final int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDCENTRO = AttributesEnum.Idcentro.index();
    public static final int DESCRBREVE = AttributesEnum.Descrbreve.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int TIPOCENTRO = AttributesEnum.Tipocentro.index();
    public static final int DTCHPERIODO = AttributesEnum.Dtchperiodo.index();
    public static final int CHAPP = AttributesEnum.Chapp.index();

    /**
     * This is the default constructor (do not remove).
     */
    public AccCa_SelCentriViewRowImpl() {
    }

    /**
     * Gets the attribute value for the calculated attribute Idcentro.
     * @return the Idcentro
     */
    public Integer getIdcentro() {
        return (Integer) getAttributeInternal(IDCENTRO);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Idcentro.
     * @param value value to set the  Idcentro
     */
    public void setIdcentro(Integer value) {
        setAttributeInternal(IDCENTRO, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Descrbreve.
     * @return the Descrbreve
     */
    public String getDescrbreve() {
        return (String) getAttributeInternal(DESCRBREVE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Descrbreve.
     * @param value value to set the  Descrbreve
     */
    public void setDescrbreve(String value) {
        setAttributeInternal(DESCRBREVE, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Ulss.
     * @return the Ulss
     */
    public String getUlss() {
        return (String) getAttributeInternal(ULSS);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Ulss.
     * @param value value to set the  Ulss
     */
    public void setUlss(String value) {
        setAttributeInternal(ULSS, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Tpscr.
     * @return the Tpscr
     */
    public String getTpscr() {
        return (String) getAttributeInternal(TPSCR);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Tpscr.
     * @param value value to set the  Tpscr
     */
    public void setTpscr(String value) {
        setAttributeInternal(TPSCR, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Tipocentro.
     * @return the Tipocentro
     */
    public Integer getTipocentro() {
        return (Integer) getAttributeInternal(TIPOCENTRO);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Tipocentro.
     * @param value value to set the  Tipocentro
     */
    public void setTipocentro(Integer value) {
        setAttributeInternal(TIPOCENTRO, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Dtchperiodo.
     * @return the Dtchperiodo
     */
    public Date getDtchperiodo() {
        return (Date) getAttributeInternal(DTCHPERIODO);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Dtchperiodo.
     * @param value value to set the  Dtchperiodo
     */
    public void setDtchperiodo(Date value) {
        setAttributeInternal(DTCHPERIODO, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Chapp.
     * @return the Chapp
     */
    public String getChapp() {
        return (String) getAttributeInternal(CHAPP);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Chapp.
     * @param value value to set the  Chapp
     */
    public void setChapp(String value) {
        setAttributeInternal(CHAPP, value);
    }
}

