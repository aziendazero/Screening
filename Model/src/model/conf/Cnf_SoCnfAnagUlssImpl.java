package model.conf;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.Key;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoCnfAnagUlssImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codanagulss {
            public Object get(Cnf_SoCnfAnagUlssImpl obj) {
                return obj.getCodanagulss();
            }

            public void put(Cnf_SoCnfAnagUlssImpl obj, Object value) {
                obj.setCodanagulss((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Cnf_SoCnfAnagUlssImpl obj) {
                return obj.getUlss();
            }

            public void put(Cnf_SoCnfAnagUlssImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoCnfAnagUlssImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoCnfAnagUlssImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Codanagreg {
            public Object get(Cnf_SoCnfAnagUlssImpl obj) {
                return obj.getCodanagreg();
            }

            public void put(Cnf_SoCnfAnagUlssImpl obj, Object value) {
                obj.setCodanagreg((Integer)value);
            }
        }
        ,
        Cnf_SoCnfAnagReg {
            public Object get(Cnf_SoCnfAnagUlssImpl obj) {
                return obj.getCnf_SoCnfAnagReg();
            }

            public void put(Cnf_SoCnfAnagUlssImpl obj, Object value) {
                obj.setCnf_SoCnfAnagReg((Cnf_SoCnfAnagRegImpl)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfAnagUlssImpl object);

        public abstract void put(Cnf_SoCnfAnagUlssImpl object, Object value);

        public int index() {
            return Cnf_SoCnfAnagUlssImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoCnfAnagUlssImpl.AttributesEnum.firstIndex() + Cnf_SoCnfAnagUlssImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoCnfAnagUlssImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int CODANAGULSS = AttributesEnum.Codanagulss.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int CODANAGREG = AttributesEnum.Codanagreg.index();
    public static final int CNF_SOCNFANAGREG = AttributesEnum.Cnf_SoCnfAnagReg.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoCnfAnagUlssImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.Cnf_SoCnfAnagUlss");
    }

    /**
     *
     *  Gets the attribute value for Codanagulss, using the alias name Codanagulss
     */
    public String getCodanagulss()
  {
    return (String)getAttributeInternal(CODANAGULSS);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codanagulss
   */
  public void setCodanagulss(String value)
  {
    setAttributeInternal(CODANAGULSS, value);
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
   *  Gets the attribute value for Codanagreg, using the alias name Codanagreg
   */
  public Integer getCodanagreg()
  {
    return (Integer)getAttributeInternal(CODANAGREG);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codanagreg
   */
  public void setCodanagreg(Integer value)
  {
    setAttributeInternal(CODANAGREG, value);
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
   *  Gets the associated entity Cnf_SoCnfAnagRegImpl
   */
  public Cnf_SoCnfAnagRegImpl getCnf_SoCnfAnagReg()
  {
    return (Cnf_SoCnfAnagRegImpl)getAttributeInternal(CNF_SOCNFANAGREG);
  }

  /**
   * 
   *  Sets <code>value</code> as the associated entity Cnf_SoCnfAnagRegImpl
   */
  public void setCnf_SoCnfAnagReg(Cnf_SoCnfAnagRegImpl value)
  {
    setAttributeInternal(CNF_SOCNFANAGREG, value);
  }

    /**
     * @param codanagulss key constituent
     * @param ulss key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(String codanagulss, String ulss) {
        return new Key(new Object[]{codanagulss, ulss});
    }


}