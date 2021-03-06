package model.soggetto;

import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Oct 16 14:50:00 CEST 2020
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Sogg_StInvitiAllegBckViewRowImpl extends ViewRowImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Idallegato {
            public Object get(Sogg_StInvitiAllegBckViewRowImpl obj) {
                return obj.getIdallegato();
            }

            public void put(Sogg_StInvitiAllegBckViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Idinvito {
            public Object get(Sogg_StInvitiAllegBckViewRowImpl obj) {
                return obj.getIdinvito();
            }

            public void put(Sogg_StInvitiAllegBckViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Codts {
            public Object get(Sogg_StInvitiAllegBckViewRowImpl obj) {
                return obj.getCodts();
            }

            public void put(Sogg_StInvitiAllegBckViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Dtcreazione {
            public Object get(Sogg_StInvitiAllegBckViewRowImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(Sogg_StInvitiAllegBckViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Dtstampa {
            public Object get(Sogg_StInvitiAllegBckViewRowImpl obj) {
                return obj.getDtstampa();
            }

            public void put(Sogg_StInvitiAllegBckViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Stampapostel {
            public Object get(Sogg_StInvitiAllegBckViewRowImpl obj) {
                return obj.getStampapostel();
            }

            public void put(Sogg_StInvitiAllegBckViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ulss {
            public Object get(Sogg_StInvitiAllegBckViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Sogg_StInvitiAllegBckViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Tpscr {
            public Object get(Sogg_StInvitiAllegBckViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Sogg_StInvitiAllegBckViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(Sogg_StInvitiAllegBckViewRowImpl object);

        public abstract void put(Sogg_StInvitiAllegBckViewRowImpl object, Object value);

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


    public static final int IDALLEGATO = AttributesEnum.Idallegato.index();
    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int DTSTAMPA = AttributesEnum.Dtstampa.index();
    public static final int STAMPAPOSTEL = AttributesEnum.Stampapostel.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Sogg_StInvitiAllegBckViewRowImpl() {
    }

    /**
     * Gets the attribute value for the calculated attribute Idallegato.
     * @return the Idallegato
     */
    public Integer getIdallegato() {
        return (Integer) getAttributeInternal(IDALLEGATO);
    }

    /**
     * Gets the attribute value for the calculated attribute Idinvito.
     * @return the Idinvito
     */
    public Integer getIdinvito() {
        return (Integer) getAttributeInternal(IDINVITO);
    }

    /**
     * Gets the attribute value for the calculated attribute Codts.
     * @return the Codts
     */
    public String getCodts() {
        return (String) getAttributeInternal(CODTS);
    }

    /**
     * Gets the attribute value for the calculated attribute Dtcreazione.
     * @return the Dtcreazione
     */
    public Date getDtcreazione() {
        return (Date) getAttributeInternal(DTCREAZIONE);
    }

    /**
     * Gets the attribute value for the calculated attribute Dtstampa.
     * @return the Dtstampa
     */
    public Date getDtstampa() {
        return (Date) getAttributeInternal(DTSTAMPA);
    }

    /**
     * Gets the attribute value for the calculated attribute Stampapostel.
     * @return the Stampapostel
     */
    public Integer getStampapostel() {
        return (Integer) getAttributeInternal(STAMPAPOSTEL);
    }

    /**
     * Gets the attribute value for the calculated attribute Ulss.
     * @return the Ulss
     */
    public String getUlss() {
        return (String) getAttributeInternal(ULSS);
    }

    /**
     * Gets the attribute value for the calculated attribute Tpscr.
     * @return the Tpscr
     */
    public String getTpscr() {
        return (String) getAttributeInternal(TPSCR);
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

