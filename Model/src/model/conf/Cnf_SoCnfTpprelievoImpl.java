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

public class Cnf_SoCnfTpprelievoImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idtpprelievo {
            public Object get(Cnf_SoCnfTpprelievoImpl obj) {
                return obj.getIdtpprelievo();
            }

            public void put(Cnf_SoCnfTpprelievoImpl obj, Object value) {
                obj.setIdtpprelievo((Integer)value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoCnfTpprelievoImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoCnfTpprelievoImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Cnf_SoCnfTpprelievoImpl obj) {
                return obj.getTpscr();
            }

            public void put(Cnf_SoCnfTpprelievoImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        PL_SoAccCito1liv {
            public Object get(Cnf_SoCnfTpprelievoImpl obj) {
                return obj.getPL_SoAccCito1liv();
            }

            public void put(Cnf_SoCnfTpprelievoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfTpprelievoImpl object);

        public abstract void put(Cnf_SoCnfTpprelievoImpl object, Object value);

        public int index() {
            return Cnf_SoCnfTpprelievoImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoCnfTpprelievoImpl.AttributesEnum.firstIndex() + Cnf_SoCnfTpprelievoImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoCnfTpprelievoImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDTPPRELIEVO = AttributesEnum.Idtpprelievo.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int PL_SOACCCITO1LIV = AttributesEnum.PL_SoAccCito1liv.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoCnfTpprelievoImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.Cnf_SoCnfTpprelievo");
    }

    /**
     *
     *  Gets the attribute value for Idtpprelievo, using the alias name Idtpprelievo
     */
    public Integer getIdtpprelievo()
  {
    return (Integer)getAttributeInternal(IDTPPRELIEVO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idtpprelievo
   */
  public void setIdtpprelievo(Integer value)
  {
    setAttributeInternal(IDTPPRELIEVO, value);
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
     * @param idtpprelievo key constituent
     * @param tpscr key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idtpprelievo, String tpscr) {
        return new Key(new Object[]{idtpprelievo, tpscr});
    }


}
