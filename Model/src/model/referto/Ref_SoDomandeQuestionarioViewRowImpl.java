package model.referto;

import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Feb 02 16:35:45 CET 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Ref_SoDomandeQuestionarioViewRowImpl extends ViewRowImpl {
    public static final int ENTITY_CNF_SOCNFQUESTDOMANDE = 0;
    public static final int ENTITY_CNF_SOCNFDOMANDE = 1;
    public static final int ENTITY_REF_SOREFERTOPFASDATI = 2;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Domanda {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getDomanda();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setDomanda((String)value);
            }
        }
        ,
        Idcnfdom {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getIdcnfdom();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setIdcnfdom((Integer)value);
            }
        }
        ,
        Tipo {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getTipo();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setTipo((String)value);
            }
        }
        ,
        Lunghezza {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getLunghezza();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setLunghezza((Integer)value);
            }
        }
        ,
        Modificabile {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getModificabile();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setModificabile((Integer)value);
            }
        }
        ,
        Gruppo {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getGruppo();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setGruppo((String)value);
            }
        }
        ,
        Note {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getNote();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setNote((String)value);
            }
        }
        ,
        Capostipite {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getCapostipite();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setCapostipite((Integer)value);
            }
        }
        ,
        Idcnfquest {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getIdcnfquest();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setIdcnfquest((Integer)value);
            }
        }
        ,
        Idcnfdom1 {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getIdcnfdom1();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setIdcnfdom1((Integer)value);
            }
        }
        ,
        Sezione {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getSezione();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setSezione((String)value);
            }
        }
        ,
        Livello {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getLivello();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setLivello((Integer)value);
            }
        }
        ,
        Ordine {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getOrdine();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setOrdine((Integer)value);
            }
        }
        ,
        Obbligatorio {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getObbligatorio();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setObbligatorio((Integer)value);
            }
        }
        ,
        CodiceDom {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getCodiceDom();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setCodiceDom((String)value);
            }
        }
        ,
        Valore {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getValore();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setValore((String)value);
            }
        }
        ,
        Idrefdati {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getIdrefdati();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setIdrefdati((Integer)value);
            }
        }
        ,
        Maxval {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getMaxval();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setMaxval((String)value);
            }
        }
        ,
        Minval {
            public Object get(Ref_SoDomandeQuestionarioViewRowImpl obj) {
                return obj.getMinval();
            }

            public void put(Ref_SoDomandeQuestionarioViewRowImpl obj, Object value) {
                obj.setMinval((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Ref_SoDomandeQuestionarioViewRowImpl object);

        public abstract void put(Ref_SoDomandeQuestionarioViewRowImpl object, Object value);

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }

    public static final int DOMANDA = AttributesEnum.Domanda.index();
    public static final int IDCNFDOM = AttributesEnum.Idcnfdom.index();
    public static final int TIPO = AttributesEnum.Tipo.index();
    public static final int LUNGHEZZA = AttributesEnum.Lunghezza.index();
    public static final int MODIFICABILE = AttributesEnum.Modificabile.index();
    public static final int GRUPPO = AttributesEnum.Gruppo.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int CAPOSTIPITE = AttributesEnum.Capostipite.index();
    public static final int IDCNFQUEST = AttributesEnum.Idcnfquest.index();
    public static final int IDCNFDOM1 = AttributesEnum.Idcnfdom1.index();
    public static final int SEZIONE = AttributesEnum.Sezione.index();
    public static final int LIVELLO = AttributesEnum.Livello.index();
    public static final int ORDINE = AttributesEnum.Ordine.index();
    public static final int OBBLIGATORIO = AttributesEnum.Obbligatorio.index();
    public static final int CODICEDOM = AttributesEnum.CodiceDom.index();
    public static final int VALORE = AttributesEnum.Valore.index();
    public static final int IDREFDATI = AttributesEnum.Idrefdati.index();
    public static final int MAXVAL = AttributesEnum.Maxval.index();
    public static final int MINVAL = AttributesEnum.Minval.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Ref_SoDomandeQuestionarioViewRowImpl() {
    }

    /**
     * Gets Cnf_SoCnfQuestDomande entity object.
     * @return the Cnf_SoCnfQuestDomande
     */
    public EntityImpl getCnf_SoCnfQuestDomande() {
        return (EntityImpl)getEntity(ENTITY_CNF_SOCNFQUESTDOMANDE);
    }

    /**
     * Gets Cnf_SoCnfDomande entity object.
     * @return the Cnf_SoCnfDomande
     */
    public EntityImpl getCnf_SoCnfDomande() {
        return (EntityImpl)getEntity(ENTITY_CNF_SOCNFDOMANDE);
    }

    /**
     * Gets Ref_SoRefertopfasDati entity object.
     * @return the Ref_SoRefertopfasDati
     */
    public EntityImpl getRef_SoRefertopfasDati() {
        return (EntityImpl)getEntity(ENTITY_REF_SOREFERTOPFASDATI);
    }

    /**
     * Gets the attribute value for DOMANDA using the alias name Domanda.
     * @return the DOMANDA
     */
    public String getDomanda() {
        return (String) getAttributeInternal(DOMANDA);
    }

    /**
     * Sets <code>value</code> as attribute value for DOMANDA using the alias name Domanda.
     * @param value value to set the DOMANDA
     */
    public void setDomanda(String value) {
        setAttributeInternal(DOMANDA, value);
    }

    /**
     * Gets the attribute value for IDCNFDOM using the alias name Idcnfdom.
     * @return the IDCNFDOM
     */
    public Integer getIdcnfdom() {
        return (Integer) getAttributeInternal(IDCNFDOM);
    }

    /**
     * Sets <code>value</code> as attribute value for IDCNFDOM using the alias name Idcnfdom.
     * @param value value to set the IDCNFDOM
     */
    public void setIdcnfdom(Integer value) {
        setAttributeInternal(IDCNFDOM, value);
    }

    /**
     * Gets the attribute value for TIPO using the alias name Tipo.
     * @return the TIPO
     */
    public String getTipo() {
        return (String) getAttributeInternal(TIPO);
    }

    /**
     * Sets <code>value</code> as attribute value for TIPO using the alias name Tipo.
     * @param value value to set the TIPO
     */
    public void setTipo(String value) {
        setAttributeInternal(TIPO, value);
    }

    /**
     * Gets the attribute value for LUNGHEZZA using the alias name Lunghezza.
     * @return the LUNGHEZZA
     */
    public Integer getLunghezza() {
        return (Integer) getAttributeInternal(LUNGHEZZA);
    }

    /**
     * Sets <code>value</code> as attribute value for LUNGHEZZA using the alias name Lunghezza.
     * @param value value to set the LUNGHEZZA
     */
    public void setLunghezza(Integer value) {
        setAttributeInternal(LUNGHEZZA, value);
    }

    /**
     * Gets the attribute value for MODIFICABILE using the alias name Modificabile.
     * @return the MODIFICABILE
     */
    public Integer getModificabile() {
        return (Integer) getAttributeInternal(MODIFICABILE);
    }

    /**
     * Sets <code>value</code> as attribute value for MODIFICABILE using the alias name Modificabile.
     * @param value value to set the MODIFICABILE
     */
    public void setModificabile(Integer value) {
        setAttributeInternal(MODIFICABILE, value);
    }

    /**
     * Gets the attribute value for GRUPPO using the alias name Gruppo.
     * @return the GRUPPO
     */
    public String getGruppo() {
        return (String) getAttributeInternal(GRUPPO);
    }

    /**
     * Sets <code>value</code> as attribute value for GRUPPO using the alias name Gruppo.
     * @param value value to set the GRUPPO
     */
    public void setGruppo(String value) {
        setAttributeInternal(GRUPPO, value);
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
     * Gets the attribute value for CAPOSTIPITE using the alias name Capostipite.
     * @return the CAPOSTIPITE
     */
    public Integer getCapostipite() {
        return (Integer) getAttributeInternal(CAPOSTIPITE);
    }

    /**
     * Sets <code>value</code> as attribute value for CAPOSTIPITE using the alias name Capostipite.
     * @param value value to set the CAPOSTIPITE
     */
    public void setCapostipite(Integer value) {
        setAttributeInternal(CAPOSTIPITE, value);
    }

    /**
     * Gets the attribute value for IDCNFQUEST using the alias name Idcnfquest.
     * @return the IDCNFQUEST
     */
    public Integer getIdcnfquest() {
        return (Integer) getAttributeInternal(IDCNFQUEST);
    }

    /**
     * Sets <code>value</code> as attribute value for IDCNFQUEST using the alias name Idcnfquest.
     * @param value value to set the IDCNFQUEST
     */
    public void setIdcnfquest(Integer value) {
        setAttributeInternal(IDCNFQUEST, value);
    }

    /**
     * Gets the attribute value for IDCNFDOM using the alias name Idcnfdom1.
     * @return the IDCNFDOM
     */
    public Integer getIdcnfdom1() {
        return (Integer) getAttributeInternal(IDCNFDOM1);
    }

    /**
     * Sets <code>value</code> as attribute value for IDCNFDOM using the alias name Idcnfdom1.
     * @param value value to set the IDCNFDOM
     */
    public void setIdcnfdom1(Integer value) {
        setAttributeInternal(IDCNFDOM1, value);
    }

    /**
     * Gets the attribute value for SEZIONE using the alias name Sezione.
     * @return the SEZIONE
     */
    public String getSezione() {
        return (String) getAttributeInternal(SEZIONE);
    }

    /**
     * Sets <code>value</code> as attribute value for SEZIONE using the alias name Sezione.
     * @param value value to set the SEZIONE
     */
    public void setSezione(String value) {
        setAttributeInternal(SEZIONE, value);
    }

    /**
     * Gets the attribute value for LIVELLO using the alias name Livello.
     * @return the LIVELLO
     */
    public Integer getLivello() {
        return (Integer) getAttributeInternal(LIVELLO);
    }

    /**
     * Sets <code>value</code> as attribute value for LIVELLO using the alias name Livello.
     * @param value value to set the LIVELLO
     */
    public void setLivello(Integer value) {
        setAttributeInternal(LIVELLO, value);
    }

    /**
     * Gets the attribute value for ORDINE using the alias name Ordine.
     * @return the ORDINE
     */
    public Integer getOrdine() {
        return (Integer) getAttributeInternal(ORDINE);
    }

    /**
     * Sets <code>value</code> as attribute value for ORDINE using the alias name Ordine.
     * @param value value to set the ORDINE
     */
    public void setOrdine(Integer value) {
        setAttributeInternal(ORDINE, value);
    }

    /**
     * Gets the attribute value for OBBLIGATORIO using the alias name Obbligatorio.
     * @return the OBBLIGATORIO
     */
    public Integer getObbligatorio() {
        return (Integer) getAttributeInternal(OBBLIGATORIO);
    }

    /**
     * Sets <code>value</code> as attribute value for OBBLIGATORIO using the alias name Obbligatorio.
     * @param value value to set the OBBLIGATORIO
     */
    public void setObbligatorio(Integer value) {
        setAttributeInternal(OBBLIGATORIO, value);
    }

    /**
     * Gets the attribute value for CODICE_DOM using the alias name CodiceDom.
     * @return the CODICE_DOM
     */
    public String getCodiceDom() {
        return (String) getAttributeInternal(CODICEDOM);
    }

    /**
     * Sets <code>value</code> as attribute value for CODICE_DOM using the alias name CodiceDom.
     * @param value value to set the CODICE_DOM
     */
    public void setCodiceDom(String value) {
        setAttributeInternal(CODICEDOM, value);
    }

    /**
     * Gets the attribute value for VALORE using the alias name Valore.
     * @return the VALORE
     */
    public String getValore() {
        return (String) getAttributeInternal(VALORE);
    }

    /**
     * Sets <code>value</code> as attribute value for VALORE using the alias name Valore.
     * @param value value to set the VALORE
     */
    public void setValore(String value) {
        setAttributeInternal(VALORE, value);
    }

    /**
     * Gets the attribute value for IDREFDATI using the alias name Idrefdati.
     * @return the IDREFDATI
     */
    public Integer getIdrefdati() {
        return (Integer) getAttributeInternal(IDREFDATI);
    }

    /**
     * Sets <code>value</code> as attribute value for IDREFDATI using the alias name Idrefdati.
     * @param value value to set the IDREFDATI
     */
    public void setIdrefdati(Integer value) {
        setAttributeInternal(IDREFDATI, value);
    }

    /**
     * Gets the attribute value for MAXVAL using the alias name Maxval.
     * @return the MAXVAL
     */
    public String getMaxval() {
        return (String) getAttributeInternal(MAXVAL);
    }

    /**
     * Sets <code>value</code> as attribute value for MAXVAL using the alias name Maxval.
     * @param value value to set the MAXVAL
     */
    public void setMaxval(String value) {
        setAttributeInternal(MAXVAL, value);
    }

    /**
     * Gets the attribute value for MINVAL using the alias name Minval.
     * @return the MINVAL
     */
    public String getMinval() {
        return (String) getAttributeInternal(MINVAL);
    }

    /**
     * Sets <code>value</code> as attribute value for MINVAL using the alias name Minval.
     * @param value value to set the MINVAL
     */
    public void setMinval(String value) {
        setAttributeInternal(MINVAL, value);
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
