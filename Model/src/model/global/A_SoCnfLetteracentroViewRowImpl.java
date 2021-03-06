package model.global;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoCnfLetteracentroViewRowImpl extends ViewRowImpl implements model.common.A_SoCnfLetteracentroViewRow  
{


    public static final int ENTITY__SOCNFLETTERACENTRO = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idtpinvito {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setIdtpinvito((String) value);
            }
        }
        ,
        Codtempl {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getCodtempl();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setCodtempl((Integer) value);
            }
        }
        ,
        Idsugg {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getIdsugg();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setIdsugg((Integer) value);
            }
        }
        ,
        Idsugg2l {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getIdsugg2l();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setIdsugg2l((Integer) value);
            }
        }
        ,
        Idsugg3l {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getIdsugg3l();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setIdsugg3l((Integer) value);
            }
        }
        ,
        Ulss {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        Idrow {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getIdrow();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setIdrow((Integer) value);
            }
        }
        ,
        EtaDa {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getEtaDa();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setEtaDa((Integer) value);
            }
        }
        ,
        EtaA {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getEtaA();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setEtaA((Integer) value);
            }
        }
        ,
        Centro {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getCentro();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setCentro((Integer) value);
            }
        }
        ,
        TestProposto {
            public Object get(A_SoCnfLetteracentroViewRowImpl obj) {
                return obj.getTestProposto();
            }

            public void put(A_SoCnfLetteracentroViewRowImpl obj, Object value) {
                obj.setTestProposto((String) value);
            }
        }
        ;
        static AttributesEnum[] vals = null;
        ;
        private static int firstIndex = 0;

        public abstract Object get(A_SoCnfLetteracentroViewRowImpl object);

        public abstract void put(A_SoCnfLetteracentroViewRowImpl object, Object value);

        public int index() {
            return A_SoCnfLetteracentroViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return A_SoCnfLetteracentroViewRowImpl.AttributesEnum.firstIndex() + A_SoCnfLetteracentroViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = A_SoCnfLetteracentroViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }

    public static final int IDTPINVITO = AttributesEnum.Idtpinvito.index();
    public static final int CODTEMPL = AttributesEnum.Codtempl.index();
    public static final int IDSUGG = AttributesEnum.Idsugg.index();
    public static final int IDSUGG2L = AttributesEnum.Idsugg2l.index();
    public static final int IDSUGG3L = AttributesEnum.Idsugg3l.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int IDROW = AttributesEnum.Idrow.index();
    public static final int ETADA = AttributesEnum.EtaDa.index();
    public static final int ETAA = AttributesEnum.EtaA.index();
    public static final int CENTRO = AttributesEnum.Centro.index();
    public static final int TESTPROPOSTO = AttributesEnum.TestProposto.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public A_SoCnfLetteracentroViewRowImpl()
  {
  }

  /**
   * 
   *  Gets _SoCnfLetteracentro entity object.
   */
  public A_SoCnfLetteracentroImpl get_SoCnfLetteracentro()
  {
    return (A_SoCnfLetteracentroImpl)getEntity(0);
  }

  /**
   * 
   *  Gets the attribute value for IDTPINVITO using the alias name Idtpinvito
   */
  public String getIdtpinvito()
  {
    return (String)getAttributeInternal(IDTPINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDTPINVITO using the alias name Idtpinvito
   */
  public void setIdtpinvito(String value)
  {
    setAttributeInternal(IDTPINVITO, value);
  }

  /**
     *
     *  Gets the attribute value for CODTEMPL using the alias name Codtempl
     */
    public Integer getCodtempl()
  {
        return (Integer) getAttributeInternal(CODTEMPL);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODTEMPL using the alias name Codtempl
   */
  public void setCodtempl(Integer value)
  {
    setAttributeInternal(CODTEMPL, value);
  }

  /**
     *
     *  Gets the attribute value for IDSUGG using the alias name Idsugg
     */
    public Integer getIdsugg()
  {
        return (Integer) getAttributeInternal(IDSUGG);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDSUGG using the alias name Idsugg
   */
  public void setIdsugg(Integer value)
  {
    setAttributeInternal(IDSUGG, value);
  }

  /**
     *
     *  Gets the attribute value for IDSUGG2L using the alias name Idsugg2l
     */
    public Integer getIdsugg2l()
  {
        return (Integer) getAttributeInternal(IDSUGG2L);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDSUGG2L using the alias name Idsugg2l
   */
  public void setIdsugg2l(Integer value)
  {
    setAttributeInternal(IDSUGG2L, value);
  }

  /**
     *
     *  Gets the attribute value for IDSUGG3L using the alias name Idsugg3l
     */
    public Integer getIdsugg3l()
  {
        return (Integer) getAttributeInternal(IDSUGG3L);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDSUGG3L using the alias name Idsugg3l
   */
  public void setIdsugg3l(Integer value)
  {
    setAttributeInternal(IDSUGG3L, value);
  }

  /**
   * 
   *  Gets the attribute value for ULSS using the alias name Ulss
   */
  public String getUlss()
  {
    return (String)getAttributeInternal(ULSS);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ULSS using the alias name Ulss
   */
  public void setUlss(String value)
  {
    setAttributeInternal(ULSS, value);
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
     *  Gets the attribute value for IDROW using the alias name Idrow
     */
    public Integer getIdrow()
  {
        return (Integer) getAttributeInternal(IDROW);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDROW using the alias name Idrow
   */
  public void setIdrow(Integer value)
  {
    setAttributeInternal(IDROW, value);
  }

  /**
     *
     *  Gets the attribute value for ETA_DA using the alias name EtaDa
     */
    public Integer getEtaDa()
  {
        return (Integer) getAttributeInternal(ETADA);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ETA_DA using the alias name EtaDa
   */
  public void setEtaDa(Integer value)
  {
    setAttributeInternal(ETADA, value);
  }

  /**
     *
     *  Gets the attribute value for ETA_A using the alias name EtaA
     */
    public Integer getEtaA()
  {
        return (Integer) getAttributeInternal(ETAA);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ETA_A using the alias name EtaA
   */
  public void setEtaA(Integer value)
  {
    setAttributeInternal(ETAA, value);
  }

  /**
     *
     *  Gets the attribute value for CENTRO using the alias name Centro
     */
    public Integer getCentro()
  {
        return (Integer) getAttributeInternal(CENTRO);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CENTRO using the alias name Centro
   */
  public void setCentro(Integer value)
  {
    setAttributeInternal(CENTRO, value);
  }

    /**
     * Gets the attribute value for TEST_PROPOSTO using the alias name TestProposto.
     * @return the TEST_PROPOSTO
     */
    public String getTestProposto() {
        return (String) getAttributeInternal(TESTPROPOSTO);
    }

    /**
     * Sets <code>value</code> as attribute value for TEST_PROPOSTO using the alias name TestProposto.
     * @param value value to set the TEST_PROPOSTO
     */
    public void setTestProposto(String value) {
        setAttributeInternal(TESTPROPOSTO, value);
    }
}
