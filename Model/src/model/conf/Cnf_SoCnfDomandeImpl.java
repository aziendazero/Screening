package model.conf;

import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.SequenceImpl;
import oracle.jbo.server.TransactionEvent;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Feb 08 13:30:28 CET 2018
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Cnf_SoCnfDomandeImpl extends EntityImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Idcnfdom {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getIdcnfdom();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setIdcnfdom((Integer) value);
            }
        }
        ,
        Domanda {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getDomanda();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setDomanda((String) value);
            }
        }
        ,
        Tipo {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getTipo();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setTipo((String) value);
            }
        }
        ,
        Lunghezza {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getLunghezza();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setLunghezza((Integer) value);
            }
        }
        ,
        Modificabile {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getModificabile();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setModificabile((Boolean) value);
            }
        }
        ,
        Gruppo {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getGruppo();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setGruppo((String) value);
            }
        }
        ,
        DtFineVal {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getDtFineVal();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setDtFineVal((Date) value);
            }
        }
        ,
        Note {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getNote();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setNote((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getTpscr();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        Dtinserimento {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getDtinserimento();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Opinserimento {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getOpinserimento();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Dtultimamodifica {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getDtultimamodifica();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Opultmodifica {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getOpultmodifica();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Minval {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getMinval();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setMinval((String) value);
            }
        }
        ,
        Maxval {
            public Object get(Cnf_SoCnfDomandeImpl obj) {
                return obj.getMaxval();
            }

            public void put(Cnf_SoCnfDomandeImpl obj, Object value) {
                obj.setMaxval((String) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfDomandeImpl object);

        public abstract void put(Cnf_SoCnfDomandeImpl object, Object value);

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


    public static final int IDCNFDOM = AttributesEnum.Idcnfdom.index();
    public static final int DOMANDA = AttributesEnum.Domanda.index();
    public static final int TIPO = AttributesEnum.Tipo.index();
    public static final int LUNGHEZZA = AttributesEnum.Lunghezza.index();
    public static final int MODIFICABILE = AttributesEnum.Modificabile.index();
    public static final int GRUPPO = AttributesEnum.Gruppo.index();
    public static final int DTFINEVAL = AttributesEnum.DtFineVal.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int DTINSERIMENTO = AttributesEnum.Dtinserimento.index();
    public static final int OPINSERIMENTO = AttributesEnum.Opinserimento.index();
    public static final int DTULTIMAMODIFICA = AttributesEnum.Dtultimamodifica.index();
    public static final int OPULTMODIFICA = AttributesEnum.Opultmodifica.index();
    public static final int MINVAL = AttributesEnum.Minval.index();
    public static final int MAXVAL = AttributesEnum.Maxval.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Cnf_SoCnfDomandeImpl() {
    }

    /**
     * Gets the attribute value for Idcnfdom, using the alias name Idcnfdom.
     * @return the value of Idcnfdom
     */
    public Integer getIdcnfdom() {
        return (Integer) getAttributeInternal(IDCNFDOM);
    }

    /**
     * Sets <code>value</code> as the attribute value for Idcnfdom.
     * @param value value to set the Idcnfdom
     */
    public void setIdcnfdom(Integer value) {
        setAttributeInternal(IDCNFDOM, value);
    }

    /**
     * Gets the attribute value for Domanda, using the alias name Domanda.
     * @return the value of Domanda
     */
    public String getDomanda() {
        return (String) getAttributeInternal(DOMANDA);
    }

    /**
     * Sets <code>value</code> as the attribute value for Domanda.
     * @param value value to set the Domanda
     */
    public void setDomanda(String value) {
        setAttributeInternal(DOMANDA, value);
    }

    /**
     * Gets the attribute value for Tipo, using the alias name Tipo.
     * @return the value of Tipo
     */
    public String getTipo() {
        return (String) getAttributeInternal(TIPO);
    }

    /**
     * Sets <code>value</code> as the attribute value for Tipo.
     * @param value value to set the Tipo
     */
    public void setTipo(String value) {
        setAttributeInternal(TIPO, value);
    }

    /**
     * Gets the attribute value for Lunghezza, using the alias name Lunghezza.
     * @return the value of Lunghezza
     */
    public Integer getLunghezza() {
        return (Integer) getAttributeInternal(LUNGHEZZA);
    }

    /**
     * Sets <code>value</code> as the attribute value for Lunghezza.
     * @param value value to set the Lunghezza
     */
    public void setLunghezza(Integer value) {
        setAttributeInternal(LUNGHEZZA, value);
    }

    /**
     * Gets the attribute value for Modificabile, using the alias name Modificabile.
     * @return the value of Modificabile
     */
    public Boolean getModificabile() {
        return (Boolean) getAttributeInternal(MODIFICABILE);
    }

    /**
     * Sets <code>value</code> as the attribute value for Modificabile.
     * @param value value to set the Modificabile
     */
    public void setModificabile(Boolean value) {
        setAttributeInternal(MODIFICABILE, value);
    }

    /**
     * Gets the attribute value for Gruppo, using the alias name Gruppo.
     * @return the value of Gruppo
     */
    public String getGruppo() {
        return (String) getAttributeInternal(GRUPPO);
    }

    /**
     * Sets <code>value</code> as the attribute value for Gruppo.
     * @param value value to set the Gruppo
     */
    public void setGruppo(String value) {
        setAttributeInternal(GRUPPO, value);
    }

    /**
     * Gets the attribute value for DtFineVal, using the alias name DtFineVal.
     * @return the value of DtFineVal
     */
    public Date getDtFineVal() {
        return (Date) getAttributeInternal(DTFINEVAL);
    }

    /**
     * Sets <code>value</code> as the attribute value for DtFineVal.
     * @param value value to set the DtFineVal
     */
    public void setDtFineVal(Date value) {
        setAttributeInternal(DTFINEVAL, value);
    }

    /**
     * Gets the attribute value for Note, using the alias name Note.
     * @return the value of Note
     */
    public String getNote() {
        return (String) getAttributeInternal(NOTE);
    }

    /**
     * Sets <code>value</code> as the attribute value for Note.
     * @param value value to set the Note
     */
    public void setNote(String value) {
        setAttributeInternal(NOTE, value);
    }

    /**
     * Gets the attribute value for Tpscr, using the alias name Tpscr.
     * @return the value of Tpscr
     */
    public String getTpscr() {
        return (String) getAttributeInternal(TPSCR);
    }

    /**
     * Sets <code>value</code> as the attribute value for Tpscr.
     * @param value value to set the Tpscr
     */
    public void setTpscr(String value) {
        setAttributeInternal(TPSCR, value);
    }

    /**
     * Gets the attribute value for Dtinserimento, using the alias name Dtinserimento.
     * @return the value of Dtinserimento
     */
    public Date getDtinserimento() {
        return (Date) getAttributeInternal(DTINSERIMENTO);
    }

    /**
     * Gets the attribute value for Opinserimento, using the alias name Opinserimento.
     * @return the value of Opinserimento
     */
    public String getOpinserimento() {
        return (String) getAttributeInternal(OPINSERIMENTO);
    }

    /**
     * Gets the attribute value for Dtultimamodifica, using the alias name Dtultimamodifica.
     * @return the value of Dtultimamodifica
     */
    public Date getDtultimamodifica() {
        return (Date) getAttributeInternal(DTULTIMAMODIFICA);
    }

    /**
     * Gets the attribute value for Opultmodifica, using the alias name Opultmodifica.
     * @return the value of Opultmodifica
     */
    public String getOpultmodifica() {
        return (String) getAttributeInternal(OPULTMODIFICA);
    }

    /**
     * Gets the attribute value for Minval, using the alias name Minval.
     * @return the value of Minval
     */
    public String getMinval() {
        return (String) getAttributeInternal(MINVAL);
    }

    /**
     * Sets <code>value</code> as the attribute value for Minval.
     * @param value value to set the Minval
     */
    public void setMinval(String value) {
        setAttributeInternal(MINVAL, value);
    }

    /**
     * Gets the attribute value for Maxval, using the alias name Maxval.
     * @return the value of Maxval
     */
    public String getMaxval() {
        return (String) getAttributeInternal(MAXVAL);
    }

    /**
     * Sets <code>value</code> as the attribute value for Maxval.
     * @param value value to set the Maxval
     */
    public void setMaxval(String value) {
        setAttributeInternal(MAXVAL, value);
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

    /**
     * @param idcnfdom key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idcnfdom) {
        return new Key(new Object[] { idcnfdom });
    }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.conf.Cnf_SoCnfDomande");
    }


    @Override
    protected Object getHistoryContextForAttribute(AttributeDefImpl attr) {
        byte historyKind = attr.getHistoryKind();

        if ((historyKind == AttributeDefImpl.HISTORY_CREATE_USER) ||
            (historyKind == AttributeDefImpl.HISTORY_MODIFY_USER)) {
            return ADFContext.getCurrent().getSessionScope().get("user");
        }
        return super.getHistoryContextForAttribute(attr);
    }
    
    protected void doDML(int operation, TransactionEvent e) {
        if (operation == this.DML_INSERT) { //inserisco il codice da sequenza
            SequenceImpl s = new SequenceImpl("SO_CNF_DOMANDE_SEQ", getDBTransaction());
            this.setIdcnfdom(s.getSequenceNumber().intValue());
        }

        super.doDML(operation, e);
    }
    
}

