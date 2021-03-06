package model.pianolavoro;
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

public class PL_SoCnfEsclusioneImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idcnfescl {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getIdcnfescl();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setIdcnfescl((Integer)value);
            }
        }
        ,
        Descrizione {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getDescrizione();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Tpescl {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getTpescl();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setTpescl((String)value);
            }
        }
        ,
        Numgiorni {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getNumgiorni();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setNumgiorni((Integer)value);
            }
        }
        ,
        Esito {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getEsito();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setEsito((String)value);
            }
        }
        ,
        Codregionale {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getCodregionale();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setCodregionale((String)value);
            }
        }
        ,
        Ulss {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getUlss();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getTpscr();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Idtpinvito {
            public Object get(PL_SoCnfEsclusioneImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(PL_SoCnfEsclusioneImpl obj, Object value) {
                obj.setIdtpinvito((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(PL_SoCnfEsclusioneImpl object);

        public abstract void put(PL_SoCnfEsclusioneImpl object, Object value);

        public int index() {
            return PL_SoCnfEsclusioneImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return PL_SoCnfEsclusioneImpl.AttributesEnum.firstIndex() + PL_SoCnfEsclusioneImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = PL_SoCnfEsclusioneImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDCNFESCL = AttributesEnum.Idcnfescl.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int TPESCL = AttributesEnum.Tpescl.index();
    public static final int NUMGIORNI = AttributesEnum.Numgiorni.index();
    public static final int ESITO = AttributesEnum.Esito.index();
    public static final int CODREGIONALE = AttributesEnum.Codregionale.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int IDTPINVITO = AttributesEnum.Idtpinvito.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public PL_SoCnfEsclusioneImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.pianolavoro.PL_SoCnfEsclusione");
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
   *  Gets the attribute value for Tpescl, using the alias name Tpescl
   */
  public String getTpescl()
  {
    return (String)getAttributeInternal(TPESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Tpescl
   */
  public void setTpescl(String value)
  {
    setAttributeInternal(TPESCL, value);
  }

  /**
   * 
   *  Gets the attribute value for Numgiorni, using the alias name Numgiorni
   */
  public Integer getNumgiorni()
  {
    return (Integer)getAttributeInternal(NUMGIORNI);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Numgiorni
   */
  public void setNumgiorni(Integer value)
  {
    setAttributeInternal(NUMGIORNI, value);
  }

  /**
   * 
   *  Gets the attribute value for Esito, using the alias name Esito
   */
  public String getEsito()
  {
    return (String)getAttributeInternal(ESITO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Esito
   */
  public void setEsito(String value)
  {
    setAttributeInternal(ESITO, value);
  }

  /**
   * 
   *  Gets the attribute value for Codregionale, using the alias name Codregionale
   */
  public String getCodregionale()
  {
    return (String)getAttributeInternal(CODREGIONALE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codregionale
   */
  public void setCodregionale(String value)
  {
    setAttributeInternal(CODREGIONALE, value);
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
   *  Gets the attribute value for Idtpinvito, using the alias name Idtpinvito
   */
  public String getIdtpinvito()
  {
    return (String)getAttributeInternal(IDTPINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idtpinvito
   */
  public void setIdtpinvito(String value)
  {
    setAttributeInternal(IDTPINVITO, value);
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
     * @param idcnfescl key constituent
     * @param ulss key constituent
     * @param tpscr key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idcnfescl, String ulss, String tpscr) {
        return new Key(new Object[]{idcnfescl, ulss, tpscr});
    }


}
