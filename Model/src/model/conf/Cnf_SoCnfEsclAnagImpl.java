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

public class Cnf_SoCnfEsclAnagImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codanagreg {
            public Object get(Cnf_SoCnfEsclAnagImpl obj) {
                return obj.getCodanagreg();
            }

            public void put(Cnf_SoCnfEsclAnagImpl obj, Object value) {
                obj.setCodanagreg((Integer)value);
            }
        }
        ,
        Ulss {
            public Object get(Cnf_SoCnfEsclAnagImpl obj) {
                return obj.getUlss();
            }

            public void put(Cnf_SoCnfEsclAnagImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Cnf_SoCnfEsclAnagImpl obj) {
                return obj.getTpscr();
            }

            public void put(Cnf_SoCnfEsclAnagImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Idcnfescl {
            public Object get(Cnf_SoCnfEsclAnagImpl obj) {
                return obj.getIdcnfescl();
            }

            public void put(Cnf_SoCnfEsclAnagImpl obj, Object value) {
                obj.setIdcnfescl((Integer)value);
            }
        }
        ,
        Cnf_SoCnfAnagReg {
            public Object get(Cnf_SoCnfEsclAnagImpl obj) {
                return obj.getCnf_SoCnfAnagReg();
            }

            public void put(Cnf_SoCnfEsclAnagImpl obj, Object value) {
                obj.setCnf_SoCnfAnagReg((Cnf_SoCnfAnagRegImpl)value);
            }
        }
        ,
        Cnf_SoCnfEsclusione {
            public Object get(Cnf_SoCnfEsclAnagImpl obj) {
                return obj.getCnf_SoCnfEsclusione();
            }

            public void put(Cnf_SoCnfEsclAnagImpl obj, Object value) {
                obj.setCnf_SoCnfEsclusione((Cnf_SoCnfEsclusioneImpl)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfEsclAnagImpl object);

        public abstract void put(Cnf_SoCnfEsclAnagImpl object, Object value);

        public int index() {
            return Cnf_SoCnfEsclAnagImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoCnfEsclAnagImpl.AttributesEnum.firstIndex() + Cnf_SoCnfEsclAnagImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoCnfEsclAnagImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int CODANAGREG = AttributesEnum.Codanagreg.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int IDCNFESCL = AttributesEnum.Idcnfescl.index();
    public static final int CNF_SOCNFANAGREG = AttributesEnum.Cnf_SoCnfAnagReg.index();
    public static final int CNF_SOCNFESCLUSIONE = AttributesEnum.Cnf_SoCnfEsclusione.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoCnfEsclAnagImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.Cnf_SoCnfEsclAnag");
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
   *  Gets the attribute value for Idcnfescl, using the alias name Idcnfescl
   */
  public Integer getIdcnfescl()
  {
    return (Integer)getAttributeInternal(IDCNFESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idcnfescl
   */
  public void setIdcnfescl(Integer value)
  {
    setAttributeInternal(IDCNFESCL, value);
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
   * 
   *  Gets the associated entity Cnf_SoCnfEsclusioneImpl
   */
  public Cnf_SoCnfEsclusioneImpl getCnf_SoCnfEsclusione()
  {
    return (Cnf_SoCnfEsclusioneImpl)getAttributeInternal(CNF_SOCNFESCLUSIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the associated entity Cnf_SoCnfEsclusioneImpl
   */
  public void setCnf_SoCnfEsclusione(Cnf_SoCnfEsclusioneImpl value)
  {
    setAttributeInternal(CNF_SOCNFESCLUSIONE, value);
  }

    /**
     * @param codanagreg key constituent
     * @param ulss key constituent
     * @param tpscr key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer codanagreg, String ulss, String tpscr) {
        return new Key(new Object[]{codanagreg, ulss, tpscr});
    }


}
