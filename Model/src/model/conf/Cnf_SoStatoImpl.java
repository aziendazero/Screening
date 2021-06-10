package model.conf;

import oracle.jbo.domain.Date;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.Key;
import oracle.jbo.RowIterator;
import oracle.jbo.server.SequenceImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoStatoImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codst {
            public Object get(Cnf_SoStatoImpl obj) {
                return obj.getCodst();
            }

            public void put(Cnf_SoStatoImpl obj, Object value) {
                obj.setCodst((String) value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoStatoImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoStatoImpl obj, Object value) {
                obj.setDescrizione((String) value);
            }
        }
        ,
        Iso {
            public Object get(Cnf_SoStatoImpl obj) {
                return obj.getIso();
            }

            public void put(Cnf_SoStatoImpl obj, Object value) {
                obj.setIso((String) value);
            }
        }
        ,
        ReleaseCode {
            public Object get(Cnf_SoStatoImpl obj) {
                return obj.getReleaseCode();
            }

            public void put(Cnf_SoStatoImpl obj, Object value) {
                obj.setReleaseCode((Integer) value);
            }
        }
        ,
        Dtiniziovalidita {
            public Object get(Cnf_SoStatoImpl obj) {
                return obj.getDtiniziovalidita();
            }

            public void put(Cnf_SoStatoImpl obj, Object value) {
                obj.setDtiniziovalidita((Date) value);
            }
        }
        ,
        Dtfinevalidita {
            public Object get(Cnf_SoStatoImpl obj) {
                return obj.getDtfinevalidita();
            }

            public void put(Cnf_SoStatoImpl obj, Object value) {
                obj.setDtfinevalidita((Date) value);
            }
        }
        ,
        Cnf_SoSoggetto {
            public Object get(Cnf_SoStatoImpl obj) {
                return obj.getCnf_SoSoggetto();
            }

            public void put(Cnf_SoStatoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoStatoImpl object);

        public abstract void put(Cnf_SoStatoImpl object, Object value);

        public int index() {
            return Cnf_SoStatoImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoStatoImpl.AttributesEnum.firstIndex() + Cnf_SoStatoImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoStatoImpl.AttributesEnum.values();
            }
            return vals;
        }
    }

    public static final int CODST = AttributesEnum.Codst.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int ISO = AttributesEnum.Iso.index();
    public static final int RELEASECODE = AttributesEnum.ReleaseCode.index();
    public static final int DTINIZIOVALIDITA = AttributesEnum.Dtiniziovalidita.index();
    public static final int DTFINEVALIDITA = AttributesEnum.Dtfinevalidita.index();
    public static final int CNF_SOSOGGETTO = AttributesEnum.Cnf_SoSoggetto.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoStatoImpl()
  {
  }
    
    protected void doDML(int operation, oracle.jbo.server.TransactionEvent event) {
        if (operation == DML_INSERT) {
            setReleaseCode(new SequenceImpl("SO_STATO_SEQ", getDBTransaction()).getSequenceNumber().intValue());
        }
        super.doDML(operation, event);
    }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.conf.Cnf_SoStato");
    }


    /**
     *
     *  Gets the attribute value for Codst, using the alias name Codst
     */
    public String getCodst()
  {
    return (String)getAttributeInternal(CODST);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codst
   */
  public void setCodst(String value)
  {
    setAttributeInternal(CODST, value);
  }

  /**
   * 
   *  Gets the attribute value for Descrizione, using the alias name Descrizione
   */
  public String getDescrizione()
  {
    return (String)getAttributeInternal(DESCRIZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Descrizione
   */
  public void setDescrizione(String value)
  {
    setAttributeInternal(DESCRIZIONE, value);
  }

    /**
     * Gets the attribute value for Iso, using the alias name Iso.
     * @return the value of Iso
     */
    public String getIso() {
        return (String) getAttributeInternal(ISO);
    }

    /**
     * Sets <code>value</code> as the attribute value for Iso.
     * @param value value to set the Iso
     */
    public void setIso(String value) {
        setAttributeInternal(ISO, value);
    }

    /**
     * Gets the attribute value for ReleaseCode, using the alias name ReleaseCode.
     * @return the value of ReleaseCode
     */
    public Integer getReleaseCode() {
        return (Integer) getAttributeInternal(RELEASECODE);
    }

    /**
     * Sets <code>value</code> as the attribute value for ReleaseCode.
     * @param value value to set the ReleaseCode
     */
    public void setReleaseCode(Integer value) {
        setAttributeInternal(RELEASECODE, value);
    }

    /**
     * Gets the attribute value for Dtiniziovalidita, using the alias name Dtiniziovalidita.
     * @return the value of Dtiniziovalidita
     */
    public Date getDtiniziovalidita() {
        return (Date) getAttributeInternal(DTINIZIOVALIDITA);
    }

    /**
     * Sets <code>value</code> as the attribute value for Dtiniziovalidita.
     * @param value value to set the Dtiniziovalidita
     */
    public void setDtiniziovalidita(Date value) {
        setAttributeInternal(DTINIZIOVALIDITA, value);
    }

    /**
     * Gets the attribute value for Dtfinevalidita, using the alias name Dtfinevalidita.
     * @return the value of Dtfinevalidita
     */
    public Date getDtfinevalidita() {
        return (Date) getAttributeInternal(DTFINEVALIDITA);
    }

    /**
     * Sets <code>value</code> as the attribute value for Dtfinevalidita.
     * @param value value to set the Dtfinevalidita
     */
    public void setDtfinevalidita(Date value) {
        setAttributeInternal(DTFINEVALIDITA, value);
    }

    /**
     *
     *  getAttrInvokeAccessor: generated method. Do not modify.
     */
  protected Object getAttrInvokeAccessor(int index, AttributeDefImpl attrDef) throws Exception
  {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

  /**
   * 
   *  setAttrInvokeAccessor: generated method. Do not modify.
   */
  protected void setAttrInvokeAccessor(int index, Object value, AttributeDefImpl attrDef) throws Exception
  {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }


  /**
   *
   *  Gets the associated entity oracle.jbo.RowIterator
   */
    public RowIterator getCnf_SoSoggetto()
  {
        return (RowIterator)getAttributeInternal(CNF_SOSOGGETTO);
    }

    /**
     * @param releaseCode key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer releaseCode) {
        return new Key(new Object[] { releaseCode });
    }


}
