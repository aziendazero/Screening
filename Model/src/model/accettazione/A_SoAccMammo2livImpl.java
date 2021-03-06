package model.accettazione;

import java.math.BigDecimal;

import oracle.jbo.Key;
import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoAccMammo2livImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idaccma2liv {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getIdaccma2liv();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setIdaccma2liv((Integer) value);
            }
        }
        ,
        Idinvito {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getIdinvito();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setIdinvito((Integer) value);
            }
        }
        ,
        Dtcreazione {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setDtcreazione((Date) value);
            }
        }
        ,
        Opcreazione {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getOpcreazione();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setOpcreazione((String) value);
            }
        }
        ,
        Dtultmod {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getDtultmod();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setDtultmod((Date) value);
            }
        }
        ,
        Opultmod {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getOpultmod();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setOpultmod((String) value);
            }
        }
        ,
        Note {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getNote();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setNote((String) value);
            }
        }
        ,
        CodRichiesta {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getCodRichiesta();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setCodRichiesta((BigDecimal) value);
            }
        }
        ,
        ConsensoB {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getConsensoB();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setConsensoB((String) value);
            }
        }
        ,
        ConsensoC {
            public Object get(A_SoAccMammo2livImpl obj) {
                return obj.getConsensoC();
            }

            public void put(A_SoAccMammo2livImpl obj, Object value) {
                obj.setConsensoC((String) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(A_SoAccMammo2livImpl object);

        public abstract void put(A_SoAccMammo2livImpl object, Object value);

        public int index() {
            return A_SoAccMammo2livImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return A_SoAccMammo2livImpl.AttributesEnum.firstIndex() + A_SoAccMammo2livImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = A_SoAccMammo2livImpl.AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int IDACCMA2LIV = AttributesEnum.Idaccma2liv.index();
    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int OPCREAZIONE = AttributesEnum.Opcreazione.index();
    public static final int DTULTMOD = AttributesEnum.Dtultmod.index();
    public static final int OPULTMOD = AttributesEnum.Opultmod.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int CODRICHIESTA = AttributesEnum.CodRichiesta.index();
    public static final int CONSENSOB = AttributesEnum.ConsensoB.index();
    public static final int CONSENSOC = AttributesEnum.ConsensoC.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public A_SoAccMammo2livImpl()
  {
  }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.accettazione.A_SoAccMammo2liv");
    }


    /**
     *
     *  Gets the attribute value for Idaccma2liv, using the alias name Idaccma2liv
     */
    public Integer getIdaccma2liv()
  {
        return (Integer) getAttributeInternal(IDACCMA2LIV);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idaccma2liv
   */
  public void setIdaccma2liv(Integer value)
  {
    setAttributeInternal(IDACCMA2LIV, value);
  }

  /**
     *
     *  Gets the attribute value for Idinvito, using the alias name Idinvito
     */
    public Integer getIdinvito()
  {
        return (Integer) getAttributeInternal(IDINVITO);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idinvito
   */
  public void setIdinvito(Integer value)
  {
    setAttributeInternal(IDINVITO, value);
  }

  /**
   * 
   *  Gets the attribute value for Dtcreazione, using the alias name Dtcreazione
   */
  public Date getDtcreazione()
  {
    return (Date)getAttributeInternal(DTCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtcreazione
   */
  public void setDtcreazione(Date value)
  {
    setAttributeInternal(DTCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for Opcreazione, using the alias name Opcreazione
   */
  public String getOpcreazione()
  {
    return (String)getAttributeInternal(OPCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Opcreazione
   */
  public void setOpcreazione(String value)
  {
    setAttributeInternal(OPCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for Dtultmod, using the alias name Dtultmod
   */
  public Date getDtultmod()
  {
    return (Date)getAttributeInternal(DTULTMOD);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtultmod
   */
  public void setDtultmod(Date value)
  {
    setAttributeInternal(DTULTMOD, value);
  }

  /**
   * 
   *  Gets the attribute value for Opultmod, using the alias name Opultmod
   */
  public String getOpultmod()
  {
    return (String)getAttributeInternal(OPULTMOD);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Opultmod
   */
  public void setOpultmod(String value)
  {
    setAttributeInternal(OPULTMOD, value);
  }

  /**
   * 
   *  Gets the attribute value for Note, using the alias name Note
   */
  public String getNote()
  {
    return (String)getAttributeInternal(NOTE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Note
   */
  public void setNote(String value)
  {
    setAttributeInternal(NOTE, value);
  }

  /**
     *
     *  Gets the attribute value for CodRichiesta, using the alias name CodRichiesta
     */
    public BigDecimal getCodRichiesta()
  {
        return (BigDecimal) getAttributeInternal(CODRICHIESTA);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for CodRichiesta
   */
  public void setCodRichiesta(BigDecimal value)
  {
    setAttributeInternal(CODRICHIESTA, value);
  }

    /**
     * Gets the attribute value for ConsensoB, using the alias name ConsensoB.
     * @return the value of ConsensoB
     */
    public String getConsensoB() {
        return (String) getAttributeInternal(CONSENSOB);
    }

    /**
     * Sets <code>value</code> as the attribute value for ConsensoB.
     * @param value value to set the ConsensoB
     */
    public void setConsensoB(String value) {
        setAttributeInternal(CONSENSOB, value);
    }

    /**
     * Gets the attribute value for ConsensoC, using the alias name ConsensoC.
     * @return the value of ConsensoC
     */
    public String getConsensoC() {
        return (String) getAttributeInternal(CONSENSOC);
    }

    /**
     * Sets <code>value</code> as the attribute value for ConsensoC.
     * @param value value to set the ConsensoC
     */
    public void setConsensoC(String value) {
        setAttributeInternal(CONSENSOC, value);
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
     * @param idaccma2liv key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idaccma2liv) {
        return new Key(new Object[] { idaccma2liv });
    }


}
