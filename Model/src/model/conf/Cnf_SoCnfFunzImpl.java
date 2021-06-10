package model.conf;

import model.global.A_SoAziendaImpl;

import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.Key;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoCnfFunzImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idcnf {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getIdcnf();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setIdcnf((Integer)value);
            }
        }
        ,
        Impanag {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getImpanag();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setImpanag((Integer)value);
            }
        }
        ,
        Impref {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getImpref();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setImpref((Integer)value);
            }
        }
        ,
        Expapp {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getExpapp();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setExpapp((Integer)value);
            }
        }
        ,
        Expacc {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getExpacc();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setExpacc((Integer)value);
            }
        }
        ,
        Postel {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getPostel();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setPostel((Integer)value);
            }
        }
        ,
        Tpscr {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getTpscr();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getUlss();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        NReferti1liv {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getNReferti1liv();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setNReferti1liv((Integer)value);
            }
        }
        ,
        Expcanc {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getExpcanc();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setExpcanc((Integer)value);
            }
        }
        ,
        Impescl {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getImpescl();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setImpescl((Integer)value);
            }
        }
        ,
        Impacc {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getImpacc();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setImpacc((Integer)value);
            }
        }
        ,
        A_SoAzienda {
            public Object get(Cnf_SoCnfFunzImpl obj) {
                return obj.getA_SoAzienda();
            }

            public void put(Cnf_SoCnfFunzImpl obj, Object value) {
                obj.setA_SoAzienda((A_SoAziendaImpl)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfFunzImpl object);

        public abstract void put(Cnf_SoCnfFunzImpl object, Object value);

        public int index() {
            return Cnf_SoCnfFunzImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoCnfFunzImpl.AttributesEnum.firstIndex() + Cnf_SoCnfFunzImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoCnfFunzImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDCNF = AttributesEnum.Idcnf.index();
    public static final int IMPANAG = AttributesEnum.Impanag.index();
    public static final int IMPREF = AttributesEnum.Impref.index();
    public static final int EXPAPP = AttributesEnum.Expapp.index();
    public static final int EXPACC = AttributesEnum.Expacc.index();
    public static final int POSTEL = AttributesEnum.Postel.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int NREFERTI1LIV = AttributesEnum.NReferti1liv.index();
    public static final int EXPCANC = AttributesEnum.Expcanc.index();
    public static final int IMPESCL = AttributesEnum.Impescl.index();
    public static final int IMPACC = AttributesEnum.Impacc.index();
    public static final int A_SOAZIENDA = AttributesEnum.A_SoAzienda.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoCnfFunzImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.Cnf_SoCnfFunz");
    }

    /**
     *
     *  Gets the attribute value for Idcnf, using the alias name Idcnf
     */
    public Integer getIdcnf()
  {
    return (Integer)getAttributeInternal(IDCNF);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idcnf
   */
  public void setIdcnf(Integer value)
  {
    setAttributeInternal(IDCNF, value);
  }

  /**
   * 
   *  Gets the attribute value for Impanag, using the alias name Impanag
   */
  public Integer getImpanag()
  {
    return (Integer)getAttributeInternal(IMPANAG);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Impanag
   */
  public void setImpanag(Integer value)
  {
    setAttributeInternal(IMPANAG, value);
  }

  /**
   * 
   *  Gets the attribute value for Impref, using the alias name Impref
   */
  public Integer getImpref()
  {
    return (Integer)getAttributeInternal(IMPREF);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Impref
   */
  public void setImpref(Integer value)
  {
    setAttributeInternal(IMPREF, value);
  }

  /**
   * 
   *  Gets the attribute value for Expapp, using the alias name Expapp
   */
  public Integer getExpapp()
  {
    return (Integer)getAttributeInternal(EXPAPP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Expapp
   */
  public void setExpapp(Integer value)
  {
    setAttributeInternal(EXPAPP, value);
  }

  /**
   * 
   *  Gets the attribute value for Expacc, using the alias name Expacc
   */
  public Integer getExpacc()
  {
    return (Integer)getAttributeInternal(EXPACC);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Expacc
   */
  public void setExpacc(Integer value)
  {
    setAttributeInternal(EXPACC, value);
  }



  /**
   * 
   *  Gets the attribute value for Postel, using the alias name Postel
   */
  public Integer getPostel()
  {
    return (Integer)getAttributeInternal(POSTEL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Postel
   */
  public void setPostel(Integer value)
  {
    setAttributeInternal(POSTEL, value);
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
   *  Gets the attribute value for NReferti1liv, using the alias name NReferti1liv
   */
  public Integer getNReferti1liv()
  {
    return (Integer)getAttributeInternal(NREFERTI1LIV);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for NReferti1liv
   */
  public void setNReferti1liv(Integer value)
  {
    setAttributeInternal(NREFERTI1LIV, value);
  }

  /**
   * 
   *  Gets the associated entity A_SoAziendaImpl
   */
  public A_SoAziendaImpl getA_SoAzienda()
  {
    return (A_SoAziendaImpl)getAttributeInternal(A_SOAZIENDA);
  }

  /**
   * 
   *  Sets <code>value</code> as the associated entity A_SoAziendaImpl
   */
  public void setA_SoAzienda(A_SoAziendaImpl value)
  {
    setAttributeInternal(A_SOAZIENDA, value);
  }


    /**
     * @param idcnf key constituent
     * @param tpscr key constituent
     * @param ulss key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idcnf, String tpscr, String ulss) {
        return new Key(new Object[]{idcnf, tpscr, ulss});
    }

    /**
     *
     *  Gets the attribute value for Expcanc, using the alias name Expcanc
     */
  public Integer getExpcanc()
  {
    return (Integer)getAttributeInternal(EXPCANC);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Expcanc
   */
  public void setExpcanc(Integer value)
  {
    setAttributeInternal(EXPCANC, value);
  }


  /**
   * 
   *  Gets the attribute value for Impescl, using the alias name Impescl
   */
  public Integer getImpescl()
  {
    return (Integer)getAttributeInternal(IMPESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Impescl
   */
  public void setImpescl(Integer value)
  {
    setAttributeInternal(IMPESCL, value);
  }


  /**
   * 
   *  Gets the attribute value for Impacc, using the alias name Impacc
   */
  public Integer getImpacc()
  {
    return (Integer)getAttributeInternal(IMPACC);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Impacc
   */
  public void setImpacc(Integer value)
  {
    setAttributeInternal(IMPACC, value);
  }


}
