package model.accettazione;


import java.math.BigDecimal;

import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoAccMammo1livViewRowImpl extends ViewRowImpl implements model.common.A_SoAccMammo1livViewRow 
{


    public static final int ENTITY_A_SOACCMAMMO1LIV = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idaccma1liv {
            public Object get(A_SoAccMammo1livViewRowImpl obj) {
                return obj.getIdaccma1liv();
            }

            public void put(A_SoAccMammo1livViewRowImpl obj, Object value) {
                obj.setIdaccma1liv((Integer) value);
            }
        }
        ,
        Idinvito {
            public Object get(A_SoAccMammo1livViewRowImpl obj) {
                return obj.getIdinvito();
            }

            public void put(A_SoAccMammo1livViewRowImpl obj, Object value) {
                obj.setIdinvito((Integer) value);
            }
        }
        ,
        Dtcreazione {
            public Object get(A_SoAccMammo1livViewRowImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(A_SoAccMammo1livViewRowImpl obj, Object value) {
                obj.setDtcreazione((Date)value);
            }
        }
        ,
        Opcreazione {
            public Object get(A_SoAccMammo1livViewRowImpl obj) {
                return obj.getOpcreazione();
            }

            public void put(A_SoAccMammo1livViewRowImpl obj, Object value) {
                obj.setOpcreazione((String)value);
            }
        }
        ,
        Dtultmod {
            public Object get(A_SoAccMammo1livViewRowImpl obj) {
                return obj.getDtultmod();
            }

            public void put(A_SoAccMammo1livViewRowImpl obj, Object value) {
                obj.setDtultmod((Date)value);
            }
        }
        ,
        Opultmod {
            public Object get(A_SoAccMammo1livViewRowImpl obj) {
                return obj.getOpultmod();
            }

            public void put(A_SoAccMammo1livViewRowImpl obj, Object value) {
                obj.setOpultmod((String)value);
            }
        }
        ,
        Note {
            public Object get(A_SoAccMammo1livViewRowImpl obj) {
                return obj.getNote();
            }

            public void put(A_SoAccMammo1livViewRowImpl obj, Object value) {
                obj.setNote((String)value);
            }
        }
        ,
        CodRichiesta {
            public Object get(A_SoAccMammo1livViewRowImpl obj) {
                return obj.getCodRichiesta();
            }

            public void put(A_SoAccMammo1livViewRowImpl obj, Object value) {
                obj.setCodRichiesta((BigDecimal) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(A_SoAccMammo1livViewRowImpl object);

        public abstract void put(A_SoAccMammo1livViewRowImpl object, Object value);

        public int index() {
            return A_SoAccMammo1livViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return A_SoAccMammo1livViewRowImpl.AttributesEnum.firstIndex() + A_SoAccMammo1livViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = A_SoAccMammo1livViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDACCMA1LIV = AttributesEnum.Idaccma1liv.index();
    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int OPCREAZIONE = AttributesEnum.Opcreazione.index();
    public static final int DTULTMOD = AttributesEnum.Dtultmod.index();
    public static final int OPULTMOD = AttributesEnum.Opultmod.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int CODRICHIESTA = AttributesEnum.CodRichiesta.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public A_SoAccMammo1livViewRowImpl()
  {
  }

  /**
   * 
   *  Gets A_SoAccMammo1liv entity object.
   */
  public A_SoAccMammo1livImpl getA_SoAccMammo1liv()
  {
    return (A_SoAccMammo1livImpl)getEntity(0);
  }

  /**
     *
     *  Gets the attribute value for IDACCMA_1LIV using the alias name Idaccma1liv
     */
    public Integer getIdaccma1liv()
  {
        return (Integer) getAttributeInternal(IDACCMA1LIV);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDACCMA_1LIV using the alias name Idaccma1liv
   */
  public void setIdaccma1liv(Integer value)
  {
    setAttributeInternal(IDACCMA1LIV, value);
  }

  /**
     *
     *  Gets the attribute value for IDINVITO using the alias name Idinvito
     */
    public Integer getIdinvito()
  {
        return (Integer) getAttributeInternal(IDINVITO);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDINVITO using the alias name Idinvito
   */
  public void setIdinvito(Integer value)
  {
    setAttributeInternal(IDINVITO, value);
  }

  /**
   * 
   *  Gets the attribute value for DTCREAZIONE using the alias name Dtcreazione
   */
  public Date getDtcreazione()
  {
    return (Date)getAttributeInternal(DTCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTCREAZIONE using the alias name Dtcreazione
   */
  public void setDtcreazione(Date value)
  {
    setAttributeInternal(DTCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for OPCREAZIONE using the alias name Opcreazione
   */
  public String getOpcreazione()
  {
    return (String)getAttributeInternal(OPCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for OPCREAZIONE using the alias name Opcreazione
   */
  public void setOpcreazione(String value)
  {
    setAttributeInternal(OPCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for DTULTMOD using the alias name Dtultmod
   */
  public Date getDtultmod()
  {
    return (Date)getAttributeInternal(DTULTMOD);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTULTMOD using the alias name Dtultmod
   */
  public void setDtultmod(Date value)
  {
    setAttributeInternal(DTULTMOD, value);
  }

  /**
   * 
   *  Gets the attribute value for OPULTMOD using the alias name Opultmod
   */
  public String getOpultmod()
  {
    return (String)getAttributeInternal(OPULTMOD);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for OPULTMOD using the alias name Opultmod
   */
  public void setOpultmod(String value)
  {
    setAttributeInternal(OPULTMOD, value);
  }

  /**
   * 
   *  Gets the attribute value for NOTE using the alias name Note
   */
  public String getNote()
  {
    return (String)getAttributeInternal(NOTE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for NOTE using the alias name Note
   */
  public void setNote(String value)
  {
    setAttributeInternal(NOTE, value);
  }

  /**
     *
     *  Gets the attribute value for COD_RICHIESTA using the alias name CodRichiesta
     */
    public BigDecimal getCodRichiesta()
  {
        return (BigDecimal) getAttributeInternal(CODRICHIESTA);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for COD_RICHIESTA using the alias name CodRichiesta
   */
  public void setCodRichiesta(BigDecimal value)
  {
    setAttributeInternal(CODRICHIESTA, value);
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
}