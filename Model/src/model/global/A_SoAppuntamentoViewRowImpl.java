package model.global;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoAppuntamentoViewRowImpl extends ViewRowImpl implements model.common.A_SoAppuntamentoViewRow  
{


    public static final int ENTITY__SOAPPUNTAMENTO = 0;
    public static final int ENTITY__SOCENTROPRELREF = 1;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idapp {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getIdapp();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setIdapp((Integer) value);
            }
        }
        ,
        Idcentro {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getIdcentro();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setIdcentro((Integer) value);
            }
        }
        ,
        Dtapp {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getDtapp();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setDtapp((Date)value);
            }
        }
        ,
        Oraapp {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getOraapp();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setOraapp((Integer) value);
            }
        }
        ,
        Minapp {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getMinapp();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setMinapp((Integer) value);
            }
        }
        ,
        Dispslot {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getDispslot();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setDispslot((Integer) value);
            }
        }
        ,
        Tpsrc {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getTpsrc();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setTpsrc((String)value);
            }
        }
        ,
        Idcentro1 {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getIdcentro1();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setIdcentro1((Integer) value);
            }
        }
        ,
        Idcentroref {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getIdcentroref();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setIdcentroref((Integer) value);
            }
        }
        ,
        Descrizione {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getDescrizione();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        IndirizzoRes {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getIndirizzoRes();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setIndirizzoRes((String)value);
            }
        }
        ,
        Sede {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getSede();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setSede((String)value);
            }
        }
        ,
        Oraritel {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getOraritel();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setOraritel((String)value);
            }
        }
        ,
        Tel {
            public Object get(A_SoAppuntamentoViewRowImpl obj) {
                return obj.getTel();
            }

            public void put(A_SoAppuntamentoViewRowImpl obj, Object value) {
                obj.setTel((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(A_SoAppuntamentoViewRowImpl object);

        public abstract void put(A_SoAppuntamentoViewRowImpl object, Object value);

        public int index() {
            return A_SoAppuntamentoViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return A_SoAppuntamentoViewRowImpl.AttributesEnum.firstIndex() + A_SoAppuntamentoViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = A_SoAppuntamentoViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDAPP = AttributesEnum.Idapp.index();
    public static final int IDCENTRO = AttributesEnum.Idcentro.index();
    public static final int DTAPP = AttributesEnum.Dtapp.index();
    public static final int ORAAPP = AttributesEnum.Oraapp.index();
    public static final int MINAPP = AttributesEnum.Minapp.index();
    public static final int DISPSLOT = AttributesEnum.Dispslot.index();
    public static final int TPSRC = AttributesEnum.Tpsrc.index();
    public static final int IDCENTRO1 = AttributesEnum.Idcentro1.index();
    public static final int IDCENTROREF = AttributesEnum.Idcentroref.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int INDIRIZZORES = AttributesEnum.IndirizzoRes.index();
    public static final int SEDE = AttributesEnum.Sede.index();
    public static final int ORARITEL = AttributesEnum.Oraritel.index();
    public static final int TEL = AttributesEnum.Tel.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public A_SoAppuntamentoViewRowImpl()
  {
  }

  /**
   * 
   *  Gets _SoAppuntamento entity object.
   */
  public A_SoAppuntamentoImpl get_SoAppuntamento()
  {
    return (A_SoAppuntamentoImpl)getEntity(0);
  }

  /**
     *
     *  Gets the attribute value for IDAPP using the alias name Idapp
     */
    public Integer getIdapp()
  {
        return (Integer) getAttributeInternal(IDAPP);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDAPP using the alias name Idapp
   */
  public void setIdapp(Integer value)
  {
    setAttributeInternal(IDAPP, value);
  }

  /**
     *
     *  Gets the attribute value for IDCENTRO using the alias name Idcentro
     */
    public Integer getIdcentro()
  {
        return (Integer) getAttributeInternal(IDCENTRO);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDCENTRO using the alias name Idcentro
   */
  public void setIdcentro(Integer value)
  {
    setAttributeInternal(IDCENTRO, value);
  }

  /**
   * 
   *  Gets the attribute value for DTAPP using the alias name Dtapp
   */
  public Date getDtapp()
  {
    return (Date)getAttributeInternal(DTAPP);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTAPP using the alias name Dtapp
   */
  public void setDtapp(Date value)
  {
    setAttributeInternal(DTAPP, value);
  }

  /**
     *
     *  Gets the attribute value for ORAAPP using the alias name Oraapp
     */
    public Integer getOraapp()
  {
        return (Integer) getAttributeInternal(ORAAPP);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ORAAPP using the alias name Oraapp
   */
  public void setOraapp(Integer value)
  {
    setAttributeInternal(ORAAPP, value);
  }

  /**
     *
     *  Gets the attribute value for MINAPP using the alias name Minapp
     */
    public Integer getMinapp()
  {
        return (Integer) getAttributeInternal(MINAPP);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for MINAPP using the alias name Minapp
   */
  public void setMinapp(Integer value)
  {
    setAttributeInternal(MINAPP, value);
  }

  /**
     *
     *  Gets the attribute value for DISPSLOT using the alias name Dispslot
     */
    public Integer getDispslot()
  {
        return (Integer) getAttributeInternal(DISPSLOT);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DISPSLOT using the alias name Dispslot
   */
  public void setDispslot(Integer value)
  {
    setAttributeInternal(DISPSLOT, value);
  }

  /**
   * 
   *  Gets the attribute value for TPSRC using the alias name Tpsrc
   */
  public String getTpsrc()
  {
    return (String)getAttributeInternal(TPSRC);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TPSRC using the alias name Tpsrc
   */
  public void setTpsrc(String value)
  {
    setAttributeInternal(TPSRC, value);
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
   *  Gets _SoCentroPrelRef entity object.
   */
  public A_SoCentroPrelRefImpl get_SoCentroPrelRef()
  {
    return (A_SoCentroPrelRefImpl)getEntity(1);
  }

  /**
     *
     *  Gets the attribute value for IDCENTRO using the alias name Idcentro1
     */
    public Integer getIdcentro1()
  {
        return (Integer) getAttributeInternal(IDCENTRO1);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDCENTRO using the alias name Idcentro1
   */
  public void setIdcentro1(Integer value)
  {
    setAttributeInternal(IDCENTRO1, value);
  }

  /**
     *
     *  Gets the attribute value for IDCENTROREF using the alias name Idcentroref
     */
    public Integer getIdcentroref()
  {
        return (Integer) getAttributeInternal(IDCENTROREF);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDCENTROREF using the alias name Idcentroref
   */
  public void setIdcentroref(Integer value)
  {
    setAttributeInternal(IDCENTROREF, value);
  }

  /**
   * 
   *  Gets the attribute value for DESCRIZIONE using the alias name Descrizione
   */
  public String getDescrizione()
  {
    return (String)getAttributeInternal(DESCRIZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DESCRIZIONE using the alias name Descrizione
   */
  public void setDescrizione(String value)
  {
    setAttributeInternal(DESCRIZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for INDIRIZZO_RES using the alias name IndirizzoRes
   */
  public String getIndirizzoRes()
  {
    return (String)getAttributeInternal(INDIRIZZORES);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for INDIRIZZO_RES using the alias name IndirizzoRes
   */
  public void setIndirizzoRes(String value)
  {
    setAttributeInternal(INDIRIZZORES, value);
  }

  /**
   * 
   *  Gets the attribute value for SEDE using the alias name Sede
   */
  public String getSede()
  {
    return (String)getAttributeInternal(SEDE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for SEDE using the alias name Sede
   */
  public void setSede(String value)
  {
    setAttributeInternal(SEDE, value);
  }

  /**
   * 
   *  Gets the attribute value for ORARITEL using the alias name Oraritel
   */
  public String getOraritel()
  {
    return (String)getAttributeInternal(ORARITEL);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ORARITEL using the alias name Oraritel
   */
  public void setOraritel(String value)
  {
    setAttributeInternal(ORARITEL, value);
  }

  /**
   * 
   *  Gets the attribute value for TEL using the alias name Tel
   */
  public String getTel()
  {
    return (String)getAttributeInternal(TEL);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TEL using the alias name Tel
   */
  public void setTel(String value)
  {
    setAttributeInternal(TEL, value);
  }



}