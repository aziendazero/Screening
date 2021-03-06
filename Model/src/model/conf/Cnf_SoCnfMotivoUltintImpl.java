package model.conf;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.Key;
import oracle.jbo.RowIterator;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoCnfMotivoUltintImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idmot {
            public Object get(Cnf_SoCnfMotivoUltintImpl obj) {
                return obj.getIdmot();
            }

            public void put(Cnf_SoCnfMotivoUltintImpl obj, Object value) {
                obj.setIdmot((Integer)value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoCnfMotivoUltintImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoCnfMotivoUltintImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Cnf_SoCnfMotivoUltintImpl obj) {
                return obj.getTpscr();
            }

            public void put(Cnf_SoCnfMotivoUltintImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Acc_SoAccCito2liv {
            public Object get(Cnf_SoCnfMotivoUltintImpl obj) {
                return obj.getAcc_SoAccCito2liv();
            }

            public void put(Cnf_SoCnfMotivoUltintImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        PL_SoAccCito1liv {
            public Object get(Cnf_SoCnfMotivoUltintImpl obj) {
                return obj.getPL_SoAccCito1liv();
            }

            public void put(Cnf_SoCnfMotivoUltintImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfMotivoUltintImpl object);

        public abstract void put(Cnf_SoCnfMotivoUltintImpl object, Object value);

        public int index() {
            return Cnf_SoCnfMotivoUltintImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoCnfMotivoUltintImpl.AttributesEnum.firstIndex() + Cnf_SoCnfMotivoUltintImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoCnfMotivoUltintImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDMOT = AttributesEnum.Idmot.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int ACC_SOACCCITO2LIV = AttributesEnum.Acc_SoAccCito2liv.index();
    public static final int PL_SOACCCITO1LIV = AttributesEnum.PL_SoAccCito1liv.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoCnfMotivoUltintImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.Cnf_SoCnfMotivoUltint");
    }

    /**
     *
     *  Gets the attribute value for Idmot, using the alias name Idmot
     */
    public Integer getIdmot()
  {
    return (Integer)getAttributeInternal(IDMOT);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idmot
   */
  public void setIdmot(Integer value)
  {
    setAttributeInternal(IDMOT, value);
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
   *
   *  Gets the associated entity oracle.jbo.RowIterator
   */
    public RowIterator getPL_SoAccCito1liv()
  {
        return (RowIterator)getAttributeInternal(PL_SOACCCITO1LIV);
    }

    /**
     * @param idmot key constituent
     * @param tpscr key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idmot, String tpscr) {
        return new Key(new Object[]{idmot, tpscr});
    }

    /**
     *
     *  Gets the associated entity oracle.jbo.RowIterator
     */
    public RowIterator getAcc_SoAccCito2liv()
  {
        return (RowIterator)getAttributeInternal(ACC_SOACCCITO2LIV);
    }


}
