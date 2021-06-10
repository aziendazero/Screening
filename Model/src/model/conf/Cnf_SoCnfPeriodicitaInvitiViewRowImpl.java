package model.conf;

import model.conf.common.Cnf_SoCnfPeriodicitaInvitiViewRow;

import oracle.jbo.RowSet;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Aug 04 10:45:01 CEST 2020
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Cnf_SoCnfPeriodicitaInvitiViewRowImpl extends ViewRowImpl implements Cnf_SoCnfPeriodicitaInvitiViewRow {


    public static final int ENTITY_CNF_SOCNFPERIODICITAINVITI = 0;
    public static final int ENTITY_CNF_SOCNFCLASSEPOP = 1;
    public static final int ENTITY_CNF_SOCNFTPINVITO = 2;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Codclassepop {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getCodclassepop();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setCodclassepop((String) value);
            }
        }
        ,
        Idtpinvito {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setIdtpinvito((String) value);
            }
        }
        ,
        Note {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getNote();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setNote((String) value);
            }
        }
        ,
        Dtultmod {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getDtultmod();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setDtultmod((Date) value);
            }
        }
        ,
        Opultmod {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getOpultmod();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setOpultmod((String) value);
            }
        }
        ,
        GgPeriodismo {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getGgPeriodismo();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setGgPeriodismo((Number) value);
            }
        }
        ,
        Tpscr {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        Ulss {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Dtins {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getDtins();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setDtins((Date) value);
            }
        }
        ,
        Opins {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getOpins();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setOpins((String) value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setDescrizione((String) value);
            }
        }
        ,
        Codclasse {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getCodclasse();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Tpscr1 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getTpscr1();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setTpscr1((String) value);
            }
        }
        ,
        Descrizione1 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getDescrizione1();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setDescrizione1((String) value);
            }
        }
        ,
        Idtpinvito1 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getIdtpinvito1();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setIdtpinvito1((String) value);
            }
        }
        ,
        Ulss1 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getUlss1();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setUlss1((String) value);
            }
        }
        ,
        Tpscr2 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getTpscr2();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setTpscr2((String) value);
            }
        }
        ,
        Cnf_SoCnfTpinvitoView1 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getCnf_SoCnfTpinvitoView1();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Cnf_SoCnfClassePopView1 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getCnf_SoCnfClassePopView1();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Cnf_SoCnfTpscrView1 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getCnf_SoCnfTpscrView1();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Cnf_SoCnfPrimoInvito1 {
            public Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj) {
                return obj.getCnf_SoCnfPrimoInvito1();
            }

            public void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        static AttributesEnum[] vals = null;
        ;
        private static final int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfPeriodicitaInvitiViewRowImpl object);

        public abstract void put(Cnf_SoCnfPeriodicitaInvitiViewRowImpl object, Object value);

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


    public static final int CODCLASSEPOP = AttributesEnum.Codclassepop.index();
    public static final int IDTPINVITO = AttributesEnum.Idtpinvito.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int DTULTMOD = AttributesEnum.Dtultmod.index();
    public static final int OPULTMOD = AttributesEnum.Opultmod.index();
    public static final int GGPERIODISMO = AttributesEnum.GgPeriodismo.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int DTINS = AttributesEnum.Dtins.index();
    public static final int OPINS = AttributesEnum.Opins.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int CODCLASSE = AttributesEnum.Codclasse.index();
    public static final int TPSCR1 = AttributesEnum.Tpscr1.index();
    public static final int DESCRIZIONE1 = AttributesEnum.Descrizione1.index();
    public static final int IDTPINVITO1 = AttributesEnum.Idtpinvito1.index();
    public static final int ULSS1 = AttributesEnum.Ulss1.index();
    public static final int TPSCR2 = AttributesEnum.Tpscr2.index();
    public static final int CNF_SOCNFTPINVITOVIEW1 = AttributesEnum.Cnf_SoCnfTpinvitoView1.index();
    public static final int CNF_SOCNFCLASSEPOPVIEW1 = AttributesEnum.Cnf_SoCnfClassePopView1.index();
    public static final int CNF_SOCNFTPSCRVIEW1 = AttributesEnum.Cnf_SoCnfTpscrView1.index();
    public static final int CNF_SOCNFPRIMOINVITO1 = AttributesEnum.Cnf_SoCnfPrimoInvito1.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Cnf_SoCnfPeriodicitaInvitiViewRowImpl() {
    }

    /**
     * Gets Cnf_SoCnfPeriodicitaInviti entity object.
     * @return the Cnf_SoCnfPeriodicitaInviti
     */
    public Cnf_SoCnfPeriodicitaInvitiImpl getCnf_SoCnfPeriodicitaInviti() {
        return (Cnf_SoCnfPeriodicitaInvitiImpl) getEntity(ENTITY_CNF_SOCNFPERIODICITAINVITI);
    }

    /**
     * Gets Cnf_SoCnfClassePop entity object.
     * @return the Cnf_SoCnfClassePop
     */
    public Cnf_SoCnfClassePopImpl getCnf_SoCnfClassePop() {
        return (Cnf_SoCnfClassePopImpl) getEntity(ENTITY_CNF_SOCNFCLASSEPOP);
    }


    /**
     * Gets Cnf_SoCnfTpinvito entity object.
     * @return the Cnf_SoCnfTpinvito
     */
    public Cnf_SoCnfTpinvitoImpl getCnf_SoCnfTpinvito() {
        return (Cnf_SoCnfTpinvitoImpl) getEntity(ENTITY_CNF_SOCNFTPINVITO);
    }

    /**
     * Gets the attribute value for ULSS using the alias name Ulss.
     * @return the ULSS
     */
    public String getUlss() {
        return (String) getAttributeInternal(ULSS);
    }

    /**
     * Sets <code>value</code> as attribute value for ULSS using the alias name Ulss.
     * @param value value to set the ULSS
     */
    public void setUlss(String value) {
        setAttributeInternal(ULSS, value);
    }

    /**
     * Gets the attribute value for TPSCR using the alias name Tpscr.
     * @return the TPSCR
     */
    public String getTpscr() {
        return (String) getAttributeInternal(TPSCR);
    }

    /**
     * Sets <code>value</code> as attribute value for TPSCR using the alias name Tpscr.
     * @param value value to set the TPSCR
     */
    public void setTpscr(String value) {
        setAttributeInternal(TPSCR, value);
    }

    /**
     * Gets the attribute value for CODCLASSEPOP using the alias name Codclassepop.
     * @return the CODCLASSEPOP
     */
    public String getCodclassepop() {
        return (String) getAttributeInternal(CODCLASSEPOP);
    }

    /**
     * Sets <code>value</code> as attribute value for CODCLASSEPOP using the alias name Codclassepop.
     * @param value value to set the CODCLASSEPOP
     */
    public void setCodclassepop(String value) {
        setAttributeInternal(CODCLASSEPOP, value);
    }

    /**
     * Gets the attribute value for IDTPINVITO using the alias name Idtpinvito.
     * @return the IDTPINVITO
     */
    public String getIdtpinvito() {
        return (String) getAttributeInternal(IDTPINVITO);
    }

    /**
     * Sets <code>value</code> as attribute value for IDTPINVITO using the alias name Idtpinvito.
     * @param value value to set the IDTPINVITO
     */
    public void setIdtpinvito(String value) {
        setAttributeInternal(IDTPINVITO, value);
    }

    /**
     * Gets the attribute value for GG_PERIODISMO using the alias name GgPeriodismo.
     * @return the GG_PERIODISMO
     */
    public Number getGgPeriodismo() {
        return (Number) getAttributeInternal(GGPERIODISMO);
    }

    /**
     * Sets <code>value</code> as attribute value for GG_PERIODISMO using the alias name GgPeriodismo.
     * @param value value to set the GG_PERIODISMO
     */
    public void setGgPeriodismo(Number value) {
        setAttributeInternal(GGPERIODISMO, value);
    }

    /**
     * Gets the attribute value for NOTE using the alias name Note.
     * @return the NOTE
     */
    public String getNote() {
        return (String) getAttributeInternal(NOTE);
    }

    /**
     * Sets <code>value</code> as attribute value for NOTE using the alias name Note.
     * @param value value to set the NOTE
     */
    public void setNote(String value) {
        setAttributeInternal(NOTE, value);
    }

    /**
     * Gets the attribute value for DTINS using the alias name Dtins.
     * @return the DTINS
     */
    public Date getDtins() {
        return (Date) getAttributeInternal(DTINS);
    }

    /**
     * Sets <code>value</code> as attribute value for DTINS using the alias name Dtins.
     * @param value value to set the DTINS
     */
    public void setDtins(Date value) {
        setAttributeInternal(DTINS, value);
    }

    /**
     * Gets the attribute value for OPINS using the alias name Opins.
     * @return the OPINS
     */
    public String getOpins() {
        return (String) getAttributeInternal(OPINS);
    }

    /**
     * Sets <code>value</code> as attribute value for OPINS using the alias name Opins.
     * @param value value to set the OPINS
     */
    public void setOpins(String value) {
        setAttributeInternal(OPINS, value);
    }

    /**
     * Gets the attribute value for DESCRIZIONE using the alias name Descrizione.
     * @return the DESCRIZIONE
     */
    public String getDescrizione() {
        return (String) getAttributeInternal(DESCRIZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for DESCRIZIONE using the alias name Descrizione.
     * @param value value to set the DESCRIZIONE
     */
    public void setDescrizione(String value) {
        setAttributeInternal(DESCRIZIONE, value);
    }

    /**
     * Gets the attribute value for CODCLASSE using the alias name Codclasse.
     * @return the CODCLASSE
     */
    public String getCodclasse() {
        return (String) getAttributeInternal(CODCLASSE);
    }

    /**
     * Gets the attribute value for TPSCR using the alias name Tpscr1.
     * @return the TPSCR
     */
    public String getTpscr1() {
        return (String) getAttributeInternal(TPSCR1);
    }

    /**
     * Sets <code>value</code> as attribute value for TPSCR using the alias name Tpscr1.
     * @param value value to set the TPSCR
     */
    public void setTpscr1(String value) {
        setAttributeInternal(TPSCR1, value);
    }

    /**
     * Gets the attribute value for DESCRIZIONE using the alias name Descrizione1.
     * @return the DESCRIZIONE
     */
    public String getDescrizione1() {
        return (String) getAttributeInternal(DESCRIZIONE1);
    }

    /**
     * Sets <code>value</code> as attribute value for DESCRIZIONE using the alias name Descrizione1.
     * @param value value to set the DESCRIZIONE
     */
    public void setDescrizione1(String value) {
        setAttributeInternal(DESCRIZIONE1, value);
    }

    /**
     * Gets the attribute value for IDTPINVITO using the alias name Idtpinvito1.
     * @return the IDTPINVITO
     */
    public String getIdtpinvito1() {
        return (String) getAttributeInternal(IDTPINVITO1);
    }

    /**
     * Sets <code>value</code> as attribute value for IDTPINVITO using the alias name Idtpinvito1.
     * @param value value to set the IDTPINVITO
     */
    public void setIdtpinvito1(String value) {
        setAttributeInternal(IDTPINVITO1, value);
    }

    /**
     * Gets the attribute value for ULSS using the alias name Ulss1.
     * @return the ULSS
     */
    public String getUlss1() {
        return (String) getAttributeInternal(ULSS1);
    }

    /**
     * Sets <code>value</code> as attribute value for ULSS using the alias name Ulss1.
     * @param value value to set the ULSS
     */
    public void setUlss1(String value) {
        setAttributeInternal(ULSS1, value);
    }

    /**
     * Gets the attribute value for TPSCR using the alias name Tpscr2.
     * @return the TPSCR
     */
    public String getTpscr2() {
        return (String) getAttributeInternal(TPSCR2);
    }

    /**
     * Sets <code>value</code> as attribute value for TPSCR using the alias name Tpscr2.
     * @param value value to set the TPSCR
     */
    public void setTpscr2(String value) {
        setAttributeInternal(TPSCR2, value);
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
     * Gets the view accessor <code>RowSet</code> Cnf_SoCnfTpinvitoView1.
     */
    public RowSet getCnf_SoCnfTpinvitoView1() {
        return (RowSet) getAttributeInternal(CNF_SOCNFTPINVITOVIEW1);
    }

    /**
     * Gets the view accessor <code>RowSet</code> Cnf_SoCnfClassePopView1.
     */
    public RowSet getCnf_SoCnfClassePopView1() {
        return (RowSet) getAttributeInternal(CNF_SOCNFCLASSEPOPVIEW1);
    }

    /**
     * Gets the view accessor <code>RowSet</code> Cnf_SoCnfTpscrView1.
     */
    public RowSet getCnf_SoCnfTpscrView1() {
        return (RowSet) getAttributeInternal(CNF_SOCNFTPSCRVIEW1);
    }

    /**
     * Gets the view accessor <code>RowSet</code> Cnf_SoCnfPrimoInvito1.
     */
    public RowSet getCnf_SoCnfPrimoInvito1() {
        return (RowSet) getAttributeInternal(CNF_SOCNFPRIMOINVITO1);
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
