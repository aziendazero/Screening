package model.accettazione;

import java.math.BigDecimal;

import model.accettazione.common.A_SoAccColon1livViewRow;

import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Oct 30 15:38:33 CET 2018
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class A_SoAccColon1livViewRowImpl extends ViewRowImpl implements A_SoAccColon1livViewRow {

    public static final int ENTITY_A_SOACCCOLON1LIV = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Idaccco1liv {
            public Object get(A_SoAccColon1livViewRowImpl obj) {
                return obj.getIdaccco1liv();
            }

            public void put(A_SoAccColon1livViewRowImpl obj, Object value) {
                obj.setIdaccco1liv((Integer) value);
            }
        }
        ,
        Idinvito {
            public Object get(A_SoAccColon1livViewRowImpl obj) {
                return obj.getIdinvito();
            }

            public void put(A_SoAccColon1livViewRowImpl obj, Object value) {
                obj.setIdinvito((Integer) value);
            }
        }
        ,
        Dtcreazione {
            public Object get(A_SoAccColon1livViewRowImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(A_SoAccColon1livViewRowImpl obj, Object value) {
                obj.setDtcreazione((Date) value);
            }
        }
        ,
        Opcreazione {
            public Object get(A_SoAccColon1livViewRowImpl obj) {
                return obj.getOpcreazione();
            }

            public void put(A_SoAccColon1livViewRowImpl obj, Object value) {
                obj.setOpcreazione((String) value);
            }
        }
        ,
        Dtultmod {
            public Object get(A_SoAccColon1livViewRowImpl obj) {
                return obj.getDtultmod();
            }

            public void put(A_SoAccColon1livViewRowImpl obj, Object value) {
                obj.setDtultmod((Date) value);
            }
        }
        ,
        Opultmod {
            public Object get(A_SoAccColon1livViewRowImpl obj) {
                return obj.getOpultmod();
            }

            public void put(A_SoAccColon1livViewRowImpl obj, Object value) {
                obj.setOpultmod((String) value);
            }
        }
        ,
        CodCampione {
            public Object get(A_SoAccColon1livViewRowImpl obj) {
                return obj.getCodCampione();
            }

            public void put(A_SoAccColon1livViewRowImpl obj, Object value) {
                obj.setCodCampione((BigDecimal) value);
            }
        }
        ,
        CodRichiesta {
            public Object get(A_SoAccColon1livViewRowImpl obj) {
                return obj.getCodRichiesta();
            }

            public void put(A_SoAccColon1livViewRowImpl obj, Object value) {
                obj.setCodRichiesta((BigDecimal) value);
            }
        }
        ;
        static AttributesEnum[] vals = null;
        ;
        private static final int firstIndex = 0;

        public abstract Object get(A_SoAccColon1livViewRowImpl object);

        public abstract void put(A_SoAccColon1livViewRowImpl object, Object value);

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

    public static final int IDACCCO1LIV = AttributesEnum.Idaccco1liv.index();
    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int OPCREAZIONE = AttributesEnum.Opcreazione.index();
    public static final int DTULTMOD = AttributesEnum.Dtultmod.index();
    public static final int OPULTMOD = AttributesEnum.Opultmod.index();
    public static final int CODCAMPIONE = AttributesEnum.CodCampione.index();
    public static final int CODRICHIESTA = AttributesEnum.CodRichiesta.index();

    /**
     * This is the default constructor (do not remove).
     */
    public A_SoAccColon1livViewRowImpl() {
    }

    /**
     * Gets A_SoAccColon1liv entity object.
     * @return the A_SoAccColon1liv
     */
    public A_SoAccColon1livImpl getA_SoAccColon1liv() {
        return (A_SoAccColon1livImpl) getEntity(ENTITY_A_SOACCCOLON1LIV);
    }

    /**
     * Gets the attribute value for IDACCCO_1LIV using the alias name Idaccco1liv.
     * @return the IDACCCO_1LIV
     */
    public Integer getIdaccco1liv() {
        return (Integer) getAttributeInternal(IDACCCO1LIV);
    }

    /**
     * Sets <code>value</code> as attribute value for IDACCCO_1LIV using the alias name Idaccco1liv.
     * @param value value to set the IDACCCO_1LIV
     */
    public void setIdaccco1liv(Integer value) {
        setAttributeInternal(IDACCCO1LIV, value);
    }

    /**
     * Gets the attribute value for IDINVITO using the alias name Idinvito.
     * @return the IDINVITO
     */
    public Integer getIdinvito() {
        return (Integer) getAttributeInternal(IDINVITO);
    }

    /**
     * Sets <code>value</code> as attribute value for IDINVITO using the alias name Idinvito.
     * @param value value to set the IDINVITO
     */
    public void setIdinvito(Integer value) {
        setAttributeInternal(IDINVITO, value);
    }

    /**
     * Gets the attribute value for DTCREAZIONE using the alias name Dtcreazione.
     * @return the DTCREAZIONE
     */
    public Date getDtcreazione() {
        return (Date) getAttributeInternal(DTCREAZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for DTCREAZIONE using the alias name Dtcreazione.
     * @param value value to set the DTCREAZIONE
     */
    public void setDtcreazione(Date value) {
        setAttributeInternal(DTCREAZIONE, value);
    }

    /**
     * Gets the attribute value for OPCREAZIONE using the alias name Opcreazione.
     * @return the OPCREAZIONE
     */
    public String getOpcreazione() {
        return (String) getAttributeInternal(OPCREAZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for OPCREAZIONE using the alias name Opcreazione.
     * @param value value to set the OPCREAZIONE
     */
    public void setOpcreazione(String value) {
        setAttributeInternal(OPCREAZIONE, value);
    }

    /**
     * Gets the attribute value for DTULTMOD using the alias name Dtultmod.
     * @return the DTULTMOD
     */
    public Date getDtultmod() {
        return (Date) getAttributeInternal(DTULTMOD);
    }

    /**
     * Sets <code>value</code> as attribute value for DTULTMOD using the alias name Dtultmod.
     * @param value value to set the DTULTMOD
     */
    public void setDtultmod(Date value) {
        setAttributeInternal(DTULTMOD, value);
    }

    /**
     * Gets the attribute value for OPULTMOD using the alias name Opultmod.
     * @return the OPULTMOD
     */
    public String getOpultmod() {
        return (String) getAttributeInternal(OPULTMOD);
    }

    /**
     * Sets <code>value</code> as attribute value for OPULTMOD using the alias name Opultmod.
     * @param value value to set the OPULTMOD
     */
    public void setOpultmod(String value) {
        setAttributeInternal(OPULTMOD, value);
    }

    /**
     * Gets the attribute value for COD_CAMPIONE using the alias name CodCampione.
     * @return the COD_CAMPIONE
     */
    public BigDecimal getCodCampione() {
        return (BigDecimal) getAttributeInternal(CODCAMPIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for COD_CAMPIONE using the alias name CodCampione.
     * @param value value to set the COD_CAMPIONE
     */
    public void setCodCampione(BigDecimal value) {
        setAttributeInternal(CODCAMPIONE, value);
    }

    /**
     * Gets the attribute value for COD_RICHIESTA using the alias name CodRichiesta.
     * @return the COD_RICHIESTA
     */
    public BigDecimal getCodRichiesta() {
        return (BigDecimal) getAttributeInternal(CODRICHIESTA);
    }

    /**
     * Sets <code>value</code> as attribute value for COD_RICHIESTA using the alias name CodRichiesta.
     * @param value value to set the COD_RICHIESTA
     */
    public void setCodRichiesta(BigDecimal value) {
        setAttributeInternal(CODRICHIESTA, value);
    }

    /**
     * getAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param attrDef the attribute

     * @return the attribute value
     * @throws Exception
     */
    protected Object getAttrInvokeAccessor(int index, AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

    /**
     * setAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param value the value to assign to the attribute
     * @param attrDef the attribute

     * @throws Exception
     */
    protected void setAttrInvokeAccessor(int index, Object value, AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }
}

