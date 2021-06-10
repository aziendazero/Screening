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

public class Cnf_SoCnfTpopmedicoImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codop {
            public Object get(Cnf_SoCnfTpopmedicoImpl obj) {
                return obj.getCodop();
            }

            public void put(Cnf_SoCnfTpopmedicoImpl obj, Object value) {
                obj.setCodop((Integer) value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoCnfTpopmedicoImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoCnfTpopmedicoImpl obj, Object value) {
                obj.setDescrizione((String) value);
            }
        }
        ,
        Ref_SoOpmedico {
            public Object get(Cnf_SoCnfTpopmedicoImpl obj) {
                return obj.getRef_SoOpmedico();
            }

            public void put(Cnf_SoCnfTpopmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfTpopmedicoImpl object);

        public abstract void put(Cnf_SoCnfTpopmedicoImpl object, Object value);

        public int index() {
            return Cnf_SoCnfTpopmedicoImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoCnfTpopmedicoImpl.AttributesEnum.firstIndex() + Cnf_SoCnfTpopmedicoImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoCnfTpopmedicoImpl.AttributesEnum.values();
            }
            return vals;
        }
    }

    public static final int CODOP = AttributesEnum.Codop.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int REF_SOOPMEDICO = AttributesEnum.Ref_SoOpmedico.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoCnfTpopmedicoImpl()
  {
  }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.conf.Cnf_SoCnfTpopmedico");
    }


    /**
     *
     *  Gets the attribute value for Codop, using the alias name Codop
     */
    public Integer getCodop()
  {
        return (Integer) getAttributeInternal(CODOP);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codop
   */
  public void setCodop(Integer value)
  {
    setAttributeInternal(CODOP, value);
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
    public RowIterator getRef_SoOpmedico()
  {
        return (RowIterator)getAttributeInternal(REF_SOOPMEDICO);
    }

    /**
     * @param codop key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer codop) {
        return new Key(new Object[] { codop });
    }


}