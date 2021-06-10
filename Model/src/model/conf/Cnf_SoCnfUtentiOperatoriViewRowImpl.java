package model.conf;

import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Mon Apr 09 14:52:30 CEST 2018
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Cnf_SoCnfUtentiOperatoriViewRowImpl extends ViewRowImpl {
    public static final int ENTITY_CNF_SOCNFUTENTIOPERATORI = 0;
    public static final int ENTITY_CNF_SOOPMEDICO = 1;
    public static final int ENTITY_CNF_SOCNFTPOPMEDICO = 2;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    public enum AttributesEnum {
        Idassoc {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getIdassoc();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setIdassoc((Integer) value);
            }
        }
        ,
        Username {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getUsername();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setUsername((String) value);
            }
        }
        ,
        Operatore {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getOperatore();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setOperatore((Integer) value);
            }
        }
        ,
        DataIns {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getDataIns();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        DataMod {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getDataMod();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        OpIns {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getOpIns();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        OpMod {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getOpMod();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ulss {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        Idop {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getIdop();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setIdop((Integer) value);
            }
        }
        ,
        Cognome {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getCognome();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setCognome((String) value);
            }
        }
        ,
        Nome {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getNome();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setNome((String) value);
            }
        }
        ,
        Titolo {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getTitolo();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setTitolo((String) value);
            }
        }
        ,
        Dtfinevalopmedico {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getDtfinevalopmedico();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setDtfinevalopmedico((Date) value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setDescrizione((String) value);
            }
        }
        ,
        Codop1 {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getCodop1();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setCodop1((Integer) value);
            }
        }
        ,
        Codop {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getCodop();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setCodop((Integer) value);
            }
        }
        ,
        Idcentro {
            public Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl obj) {
                return obj.getIdcentro();
            }

            public void put(Cnf_SoCnfUtentiOperatoriViewRowImpl obj, Object value) {
                obj.setIdcentro((Integer) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static final int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfUtentiOperatoriViewRowImpl object);

        public abstract void put(Cnf_SoCnfUtentiOperatoriViewRowImpl object, Object value);

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
    public static final int IDASSOC = AttributesEnum.Idassoc.index();
    public static final int USERNAME = AttributesEnum.Username.index();
    public static final int OPERATORE = AttributesEnum.Operatore.index();
    public static final int DATAINS = AttributesEnum.DataIns.index();
    public static final int DATAMOD = AttributesEnum.DataMod.index();
    public static final int OPINS = AttributesEnum.OpIns.index();
    public static final int OPMOD = AttributesEnum.OpMod.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int IDOP = AttributesEnum.Idop.index();
    public static final int COGNOME = AttributesEnum.Cognome.index();
    public static final int NOME = AttributesEnum.Nome.index();
    public static final int TITOLO = AttributesEnum.Titolo.index();
    public static final int DTFINEVALOPMEDICO = AttributesEnum.Dtfinevalopmedico.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int CODOP1 = AttributesEnum.Codop1.index();
    public static final int CODOP = AttributesEnum.Codop.index();
    public static final int IDCENTRO = AttributesEnum.Idcentro.index();

    /**
     * This is the default constructor (do not remove).
     */
    public Cnf_SoCnfUtentiOperatoriViewRowImpl() {
    }

    /**
     * Gets Cnf_SoCnfUtentiOperatori entity object.
     * @return the Cnf_SoCnfUtentiOperatori
     */
    public Cnf_SoCnfUtentiOperatoriImpl getCnf_SoCnfUtentiOperatori() {
        return (Cnf_SoCnfUtentiOperatoriImpl) getEntity(ENTITY_CNF_SOCNFUTENTIOPERATORI);
    }

    /**
     * Gets Cnf_SoOpmedico entity object.
     * @return the Cnf_SoOpmedico
     */
    public Cnf_SoOpmedicoImpl getCnf_SoOpmedico() {
        return (Cnf_SoOpmedicoImpl) getEntity(ENTITY_CNF_SOOPMEDICO);
    }

    /**
     * Gets Cnf_SoCnfTpopmedico entity object.
     * @return the Cnf_SoCnfTpopmedico
     */
    public Cnf_SoCnfTpopmedicoImpl getCnf_SoCnfTpopmedico() {
        return (Cnf_SoCnfTpopmedicoImpl) getEntity(ENTITY_CNF_SOCNFTPOPMEDICO);
    }

    /**
     * Gets the attribute value for IDASSOC using the alias name Idassoc.
     * @return the IDASSOC
     */
    public Integer getIdassoc() {
        return (Integer) getAttributeInternal(IDASSOC);
    }

    /**
     * Sets <code>value</code> as attribute value for IDASSOC using the alias name Idassoc.
     * @param value value to set the IDASSOC
     */
    public void setIdassoc(Integer value) {
        setAttributeInternal(IDASSOC, value);
    }

    /**
     * Gets the attribute value for USERNAME using the alias name Username.
     * @return the USERNAME
     */
    public String getUsername() {
        return (String) getAttributeInternal(USERNAME);
    }

    /**
     * Sets <code>value</code> as attribute value for USERNAME using the alias name Username.
     * @param value value to set the USERNAME
     */
    public void setUsername(String value) {
        setAttributeInternal(USERNAME, value);
    }

    /**
     * Gets the attribute value for OPERATORE using the alias name Operatore.
     * @return the OPERATORE
     */
    public Integer getOperatore() {
        return (Integer) getAttributeInternal(OPERATORE);
    }

    /**
     * Sets <code>value</code> as attribute value for OPERATORE using the alias name Operatore.
     * @param value value to set the OPERATORE
     */
    public void setOperatore(Integer value) {
        setAttributeInternal(OPERATORE, value);
    }

    /**
     * Gets the attribute value for DATA_INS using the alias name DataIns.
     * @return the DATA_INS
     */
    public Date getDataIns() {
        return (Date) getAttributeInternal(DATAINS);
    }

    /**
     * Gets the attribute value for DATA_MOD using the alias name DataMod.
     * @return the DATA_MOD
     */
    public Date getDataMod() {
        return (Date) getAttributeInternal(DATAMOD);
    }

    /**
     * Gets the attribute value for OP_INS using the alias name OpIns.
     * @return the OP_INS
     */
    public String getOpIns() {
        return (String) getAttributeInternal(OPINS);
    }

    /**
     * Gets the attribute value for OP_MOD using the alias name OpMod.
     * @return the OP_MOD
     */
    public String getOpMod() {
        return (String) getAttributeInternal(OPMOD);
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
     * Gets the attribute value for IDOP using the alias name Idop.
     * @return the IDOP
     */
    public Integer getIdop() {
        return (Integer) getAttributeInternal(IDOP);
    }

    /**
     * Sets <code>value</code> as attribute value for IDOP using the alias name Idop.
     * @param value value to set the IDOP
     */
    public void setIdop(Integer value) {
        setAttributeInternal(IDOP, value);
    }

    /**
     * Gets the attribute value for COGNOME using the alias name Cognome.
     * @return the COGNOME
     */
    public String getCognome() {
        return (String) getAttributeInternal(COGNOME);
    }

    /**
     * Sets <code>value</code> as attribute value for COGNOME using the alias name Cognome.
     * @param value value to set the COGNOME
     */
    public void setCognome(String value) {
        setAttributeInternal(COGNOME, value);
    }

    /**
     * Gets the attribute value for NOME using the alias name Nome.
     * @return the NOME
     */
    public String getNome() {
        return (String) getAttributeInternal(NOME);
    }

    /**
     * Sets <code>value</code> as attribute value for NOME using the alias name Nome.
     * @param value value to set the NOME
     */
    public void setNome(String value) {
        setAttributeInternal(NOME, value);
    }

    /**
     * Gets the attribute value for TITOLO using the alias name Titolo.
     * @return the TITOLO
     */
    public String getTitolo() {
        return (String) getAttributeInternal(TITOLO);
    }

    /**
     * Sets <code>value</code> as attribute value for TITOLO using the alias name Titolo.
     * @param value value to set the TITOLO
     */
    public void setTitolo(String value) {
        setAttributeInternal(TITOLO, value);
    }

    /**
     * Gets the attribute value for DTFINEVALOPMEDICO using the alias name Dtfinevalopmedico.
     * @return the DTFINEVALOPMEDICO
     */
    public Date getDtfinevalopmedico() {
        return (Date) getAttributeInternal(DTFINEVALOPMEDICO);
    }

    /**
     * Sets <code>value</code> as attribute value for DTFINEVALOPMEDICO using the alias name Dtfinevalopmedico.
     * @param value value to set the DTFINEVALOPMEDICO
     */
    public void setDtfinevalopmedico(Date value) {
        setAttributeInternal(DTFINEVALOPMEDICO, value);
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
     * Gets the attribute value for CODOP using the alias name Codop1.
     * @return the CODOP
     */
    public Integer getCodop1() {
        return (Integer) getAttributeInternal(CODOP1);
    }

    /**
     * Sets <code>value</code> as attribute value for CODOP using the alias name Codop1.
     * @param value value to set the CODOP
     */
    public void setCodop1(Integer value) {
        setAttributeInternal(CODOP1, value);
    }

    /**
     * Gets the attribute value for CODOP using the alias name Codop.
     * @return the CODOP
     */
    public Integer getCodop() {
        return (Integer) getAttributeInternal(CODOP);
    }

    /**
     * Sets <code>value</code> as attribute value for CODOP using the alias name Codop.
     * @param value value to set the CODOP
     */
    public void setCodop(Integer value) {
        setAttributeInternal(CODOP, value);
    }

    /**
     * Gets the attribute value for IDCENTRO using the alias name Idcentro.
     * @return the IDCENTRO
     */
    public Integer getIdcentro() {
        return (Integer) getAttributeInternal(IDCENTRO);
    }

    /**
     * Sets <code>value</code> as attribute value for IDCENTRO using the alias name Idcentro.
     * @param value value to set the IDCENTRO
     */
    public void setIdcentro(Integer value) {
        setAttributeInternal(IDCENTRO, value);
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

