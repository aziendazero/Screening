package model.impexp;

import java.math.BigDecimal;

import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
import oracle.jbo.Key;
import oracle.jbo.domain.Timestamp;
import oracle.jbo.server.SequenceImpl;
import oracle.jbo.server.TransactionEvent;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Impexp_SoLogTransferImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idlog {
            public Object get(Impexp_SoLogTransferImpl obj) {
                return obj.getIdlog();
            }

            public void put(Impexp_SoLogTransferImpl obj, Object value) {
                obj.setIdlog((BigDecimal) value);
            }
        }
        ,
        Gruppo {
            public Object get(Impexp_SoLogTransferImpl obj) {
                return obj.getGruppo();
            }

            public void put(Impexp_SoLogTransferImpl obj, Object value) {
                obj.setGruppo((String) value);
            }
        }
        ,
        Verso {
            public Object get(Impexp_SoLogTransferImpl obj) {
                return obj.getVerso();
            }

            public void put(Impexp_SoLogTransferImpl obj, Object value) {
                obj.setVerso((String) value);
            }
        }
        ,
        Data {
            public Object get(Impexp_SoLogTransferImpl obj) {
                return obj.getData();
            }

            public void put(Impexp_SoLogTransferImpl obj, Object value) {
                obj.setData((Timestamp) value);
            }
        }
        ,
        Ora {
            public Object get(Impexp_SoLogTransferImpl obj) {
                return obj.getOra();
            }

            public void put(Impexp_SoLogTransferImpl obj, Object value) {
                obj.setOra((Integer) value);
            }
        }
        ,
        Descrizione {
            public Object get(Impexp_SoLogTransferImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Impexp_SoLogTransferImpl obj, Object value) {
                obj.setDescrizione((String) value);
            }
        }
        ,
        Ulss {
            public Object get(Impexp_SoLogTransferImpl obj) {
                return obj.getUlss();
            }

            public void put(Impexp_SoLogTransferImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(Impexp_SoLogTransferImpl obj) {
                return obj.getTpscr();
            }

            public void put(Impexp_SoLogTransferImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Impexp_SoLogTransferImpl object);

        public abstract void put(Impexp_SoLogTransferImpl object, Object value);

        public int index() {
            return Impexp_SoLogTransferImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Impexp_SoLogTransferImpl.AttributesEnum.firstIndex() + Impexp_SoLogTransferImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Impexp_SoLogTransferImpl.AttributesEnum.values();
            }
            return vals;
        }
    }

    public static final int IDLOG = AttributesEnum.Idlog.index();
    public static final int GRUPPO = AttributesEnum.Gruppo.index();
    public static final int VERSO = AttributesEnum.Verso.index();
    public static final int DATA = AttributesEnum.Data.index();
    public static final int ORA = AttributesEnum.Ora.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Impexp_SoLogTransferImpl()
  {
  }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.impexp.Impexp_SoLogTransfer");
    }


    /**
     *
     *  Gets the attribute value for Idlog, using the alias name Idlog
     */
    public BigDecimal getIdlog()
  {
        return (BigDecimal) getAttributeInternal(IDLOG);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idlog
   */
  public void setIdlog(BigDecimal value)
  {
    setAttributeInternal(IDLOG, value);
  }

  /**
   * 
   *  Gets the attribute value for Gruppo, using the alias name Gruppo
   */
  public String getGruppo()
  {
    return (String)getAttributeInternal(GRUPPO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Gruppo
   */
  public void setGruppo(String value)
  {
    setAttributeInternal(GRUPPO, value);
  }

  /**
   * 
   *  Gets the attribute value for Verso, using the alias name Verso
   */
  public String getVerso()
  {
    return (String)getAttributeInternal(VERSO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Verso
   */
  public void setVerso(String value)
  {
    setAttributeInternal(VERSO, value);
  }

  /**
   * 
   *  Gets the attribute value for Data, using the alias name Data
   */
  public Timestamp getData()
  {
    return (Timestamp)getAttributeInternal(DATA);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Data
   */
  public void setData(Timestamp value)
  {
    setAttributeInternal(DATA, value);
  }

  /**
   * 
   *  Gets the attribute value for Ora, using the alias name Ora
   */
  public Integer getOra()
  {
    return (Integer)getAttributeInternal(ORA);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Ora
   */
  public void setOra(Integer value)
  {
    setAttributeInternal(ORA, value);
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
   * 
   *  Gets the attribute value for Ulss, using the alias name Ulss
   */
  public String getUlss()
  {
    return (String)getAttributeInternal(ULSS);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Ulss
   */
  public void setUlss(String value)
  {
    setAttributeInternal(ULSS, value);
  }

  /**
   * 
   *  Gets the attribute value for Tpscr, using the alias name Tpscr
   */
  public String getTpscr()
  {
    return (String)getAttributeInternal(TPSCR);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Tpscr
   */
  public void setTpscr(String value)
  {
    setAttributeInternal(TPSCR, value);
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
     * @param idlog key constituent
     * @param gruppo key constituent
     * @param verso key constituent
     * @param ulss key constituent
     * @param tpscr key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(BigDecimal idlog, String gruppo, String verso, String ulss, String tpscr) {
        return new Key(new Object[] { idlog, gruppo, verso, ulss, tpscr });
    }

    protected void doDML(int operation, TransactionEvent e)
    {
        if(operation==this.DML_INSERT)
        {//inserisco il codice da sequenza
          SequenceImpl s = new SequenceImpl("SO_LOG_TRANSFER_SEQ", getDBTransaction());     
          this.setIdlog(s.getSequenceNumber().bigDecimalValue()); 
        }
          
        super.doDML(operation, e);
    }
}