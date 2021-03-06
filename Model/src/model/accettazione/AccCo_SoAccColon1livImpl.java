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

public class AccCo_SoAccColon1livImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idaccco1liv {
            public Object get(AccCo_SoAccColon1livImpl obj) {
                return obj.getIdaccco1liv();
            }

            public void put(AccCo_SoAccColon1livImpl obj, Object value) {
                obj.setIdaccco1liv((Integer) value);
            }
        }
        ,
        Idinvito {
            public Object get(AccCo_SoAccColon1livImpl obj) {
                return obj.getIdinvito();
            }

            public void put(AccCo_SoAccColon1livImpl obj, Object value) {
                obj.setIdinvito((Integer) value);
            }
        }
        ,
        Dtcreazione {
            public Object get(AccCo_SoAccColon1livImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(AccCo_SoAccColon1livImpl obj, Object value) {
                obj.setDtcreazione((Date) value);
            }
        }
        ,
        Opcreazione {
            public Object get(AccCo_SoAccColon1livImpl obj) {
                return obj.getOpcreazione();
            }

            public void put(AccCo_SoAccColon1livImpl obj, Object value) {
                obj.setOpcreazione((String) value);
            }
        }
        ,
        Dtultmod {
            public Object get(AccCo_SoAccColon1livImpl obj) {
                return obj.getDtultmod();
            }

            public void put(AccCo_SoAccColon1livImpl obj, Object value) {
                obj.setDtultmod((Date) value);
            }
        }
        ,
        Opultmod {
            public Object get(AccCo_SoAccColon1livImpl obj) {
                return obj.getOpultmod();
            }

            public void put(AccCo_SoAccColon1livImpl obj, Object value) {
                obj.setOpultmod((String) value);
            }
        }
        ,
        CodCampione {
            public Object get(AccCo_SoAccColon1livImpl obj) {
                return obj.getCodCampione();
            }

            public void put(AccCo_SoAccColon1livImpl obj, Object value) {
                obj.setCodCampione((BigDecimal) value);
            }
        }
        ,
        CodRichiesta {
            public Object get(AccCo_SoAccColon1livImpl obj) {
                return obj.getCodRichiesta();
            }

            public void put(AccCo_SoAccColon1livImpl obj, Object value) {
                obj.setCodRichiesta((BigDecimal) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(AccCo_SoAccColon1livImpl object);

        public abstract void put(AccCo_SoAccColon1livImpl object, Object value);

        public int index() {
            return AccCo_SoAccColon1livImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AccCo_SoAccColon1livImpl.AttributesEnum.firstIndex() + AccCo_SoAccColon1livImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AccCo_SoAccColon1livImpl.AttributesEnum.values();
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
     *
     *  This is the default constructor (do not remove)
     */
  public AccCo_SoAccColon1livImpl()
  {
  }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.accettazione.AccCo_SoAccColon1liv");
    }


    /**
     *
     *  Gets the attribute value for Idaccco1liv, using the alias name Idaccco1liv
     */
    public Integer getIdaccco1liv()
  {
        return (Integer) getAttributeInternal(IDACCCO1LIV);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idaccco1liv
   */
  public void setIdaccco1liv(Integer value)
  {
    setAttributeInternal(IDACCCO1LIV, value);
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
     * @param idaccco1liv key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idaccco1liv) {
        return new Key(new Object[] { idaccco1liv });
    }

    /**
     *
     *  Gets the attribute value for CodCampione, using the alias name CodCampione
     */
    public BigDecimal getCodCampione()
  {
        return (BigDecimal) getAttributeInternal(CODCAMPIONE);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for CodCampione
   */
  public void setCodCampione(BigDecimal value)
  {
    setAttributeInternal(CODCAMPIONE, value);
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

}
