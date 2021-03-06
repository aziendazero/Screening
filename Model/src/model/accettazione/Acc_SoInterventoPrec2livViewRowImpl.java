package model.accettazione;


import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Acc_SoInterventoPrec2livViewRowImpl extends ViewRowImpl implements model.common.Acc_SoInterventoPrec2livViewRow 
{


    public static final int ENTITY_ACC_SOINTERVENTOPREC2LIV = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idintervento {
            public Object get(Acc_SoInterventoPrec2livViewRowImpl obj) {
                return obj.getIdintervento();
            }

            public void put(Acc_SoInterventoPrec2livViewRowImpl obj, Object value) {
                obj.setIdintervento((Integer)value);
            }
        }
        ,
        Idacc2liv {
            public Object get(Acc_SoInterventoPrec2livViewRowImpl obj) {
                return obj.getIdacc2liv();
            }

            public void put(Acc_SoInterventoPrec2livViewRowImpl obj, Object value) {
                obj.setIdacc2liv((Integer)value);
            }
        }
        ,
        Tpscr {
            public Object get(Acc_SoInterventoPrec2livViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Acc_SoInterventoPrec2livViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Annointervento {
            public Object get(Acc_SoInterventoPrec2livViewRowImpl obj) {
                return obj.getAnnointervento();
            }

            public void put(Acc_SoInterventoPrec2livViewRowImpl obj, Object value) {
                obj.setAnnointervento((Integer)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Acc_SoInterventoPrec2livViewRowImpl object);

        public abstract void put(Acc_SoInterventoPrec2livViewRowImpl object, Object value);

        public int index() {
            return Acc_SoInterventoPrec2livViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Acc_SoInterventoPrec2livViewRowImpl.AttributesEnum.firstIndex() + Acc_SoInterventoPrec2livViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Acc_SoInterventoPrec2livViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDINTERVENTO = AttributesEnum.Idintervento.index();
    public static final int IDACC2LIV = AttributesEnum.Idacc2liv.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int ANNOINTERVENTO = AttributesEnum.Annointervento.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Acc_SoInterventoPrec2livViewRowImpl()
  {
  }

  /**
   * 
   *  Gets Acc_SoInterventoPrec2liv entity object.
   */
  public Acc_SoInterventoPrec2livImpl getAcc_SoInterventoPrec2liv()
  {
    return (Acc_SoInterventoPrec2livImpl)getEntity(0);
  }

  /**
   * 
   *  Gets the attribute value for IDINTERVENTO using the alias name Idintervento
   */
  public Integer getIdintervento()
  {
    return (Integer)getAttributeInternal(IDINTERVENTO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDINTERVENTO using the alias name Idintervento
   */
  public void setIdintervento(Integer value)
  {
    setAttributeInternal(IDINTERVENTO, value);
  }

  /**
   * 
   *  Gets the attribute value for IDACC2LIV using the alias name Idacc2liv
   */
  public Integer getIdacc2liv()
  {
    return (Integer)getAttributeInternal(IDACC2LIV);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDACC2LIV using the alias name Idacc2liv
   */
  public void setIdacc2liv(Integer value)
  {
    setAttributeInternal(IDACC2LIV, value);
  }

  /**
   * 
   *  Gets the attribute value for TPSCR using the alias name Tpscr
   */
  public String getTpscr()
  {
    return (String)getAttributeInternal(TPSCR);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TPSCR using the alias name Tpscr
   */
  public void setTpscr(String value)
  {
    setAttributeInternal(TPSCR, value);
  }

  /**
   * 
   *  Gets the attribute value for ANNOINTERVENTO using the alias name Annointervento
   */
  public Integer getAnnointervento()
  {
    return (Integer)getAttributeInternal(ANNOINTERVENTO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ANNOINTERVENTO using the alias name Annointervento
   */
  public void setAnnointervento(Integer value)
  {
    setAttributeInternal(ANNOINTERVENTO, value);
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