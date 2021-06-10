package model.print;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.Key;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Print_SoCnfTpinvitoImpl extends EntityImpl {

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idtpinvito {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setIdtpinvito((String)value);
            }
        }
        ,
        Idcateg {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getIdcateg();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setIdcateg((Integer)value);
            }
        }
        ,
        Descrizione {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Descrbreve {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getDescrbreve();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setDescrbreve((String)value);
            }
        }
        ,
        Appuntamento {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getAppuntamento();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setAppuntamento((Integer)value);
            }
        }
        ,
        Invio2liv {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getInvio2liv();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setInvio2liv((Integer)value);
            }
        }
        ,
        Codregionale {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getCodregionale();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setCodregionale((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getUlss();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getTpscr();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Ggsollecito {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getGgsollecito();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setGgsollecito((Integer)value);
            }
        }
        ,
        IdtpinvSoll {
            public Object get(Print_SoCnfTpinvitoImpl obj) {
                return obj.getIdtpinvSoll();
            }

            public void put(Print_SoCnfTpinvitoImpl obj, Object value) {
                obj.setIdtpinvSoll((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Print_SoCnfTpinvitoImpl object);

        public abstract void put(Print_SoCnfTpinvitoImpl object, Object value);

        public int index() {
            return Print_SoCnfTpinvitoImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Print_SoCnfTpinvitoImpl.AttributesEnum.firstIndex() + Print_SoCnfTpinvitoImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Print_SoCnfTpinvitoImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDTPINVITO = AttributesEnum.Idtpinvito.index();
    public static final int IDCATEG = AttributesEnum.Idcateg.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int DESCRBREVE = AttributesEnum.Descrbreve.index();
    public static final int APPUNTAMENTO = AttributesEnum.Appuntamento.index();
    public static final int INVIO2LIV = AttributesEnum.Invio2liv.index();
    public static final int CODREGIONALE = AttributesEnum.Codregionale.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int GGSOLLECITO = AttributesEnum.Ggsollecito.index();
    public static final int IDTPINVSOLL = AttributesEnum.IdtpinvSoll.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Print_SoCnfTpinvitoImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.print.Print_SoCnfTpinvito");
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
   *  Gets the attribute value for Idcateg, using the alias name Idcateg
   */
  public Integer getIdcateg()
  {
    return (Integer)getAttributeInternal(IDCATEG);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idcateg
   */
  public void setIdcateg(Integer value)
  {
    setAttributeInternal(IDCATEG, value);
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
   *  Gets the attribute value for Descrbreve, using the alias name Descrbreve
   */
  public String getDescrbreve()
  {
    return (String)getAttributeInternal(DESCRBREVE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Descrbreve
   */
  public void setDescrbreve(String value)
  {
    setAttributeInternal(DESCRBREVE, value);
  }

  /**
   * 
   *  Gets the attribute value for Appuntamento, using the alias name Appuntamento
   */
  public Integer getAppuntamento()
  {
    return (Integer)getAttributeInternal(APPUNTAMENTO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Appuntamento
   */
  public void setAppuntamento(Integer value)
  {
    setAttributeInternal(APPUNTAMENTO, value);
  }

  /**
   * 
   *  Gets the attribute value for Invio2liv, using the alias name Invio2liv
   */
  public Integer getInvio2liv()
  {
    return (Integer)getAttributeInternal(INVIO2LIV);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Invio2liv
   */
  public void setInvio2liv(Integer value)
  {
    setAttributeInternal(INVIO2LIV, value);
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
   *  Gets the attribute value for Ggsollecito, using the alias name Ggsollecito
   */
  public Integer getGgsollecito()
  {
    return (Integer)getAttributeInternal(GGSOLLECITO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Ggsollecito
   */
  public void setGgsollecito(Integer value)
  {
    setAttributeInternal(GGSOLLECITO, value);
  }

  /**
   * 
   *  Gets the attribute value for IdtpinvSoll, using the alias name IdtpinvSoll
   */
  public String getIdtpinvSoll()
  {
    return (String)getAttributeInternal(IDTPINVSOLL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for IdtpinvSoll
   */
  public void setIdtpinvSoll(String value)
  {
    setAttributeInternal(IDTPINVSOLL, value);
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
     * @param idtpinvito key constituent
     * @param ulss key constituent
     * @param tpscr key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(String idtpinvito, String ulss, String tpscr) {
        return new Key(new Object[]{idtpinvito, ulss, tpscr});
    }

}
