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

public class Print_SoCnfSugg1livImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idsugg {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getIdsugg();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setIdsugg((Integer)value);
            }
        }
        ,
        Idtpinvito {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setIdtpinvito((String)value);
            }
        }
        ,
        Esitosugg {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getEsitosugg();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setEsitosugg((String)value);
            }
        }
        ,
        Descrizione {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Ggrichiamo {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getGgrichiamo();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setGgrichiamo((Integer)value);
            }
        }
        ,
        Inviaintervento {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getInviaintervento();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setInviaintervento((Integer)value);
            }
        }
        ,
        Tipoesame {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getTipoesame();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setTipoesame((String)value);
            }
        }
        ,
        Invia2liv {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getInvia2liv();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setInvia2liv((Integer)value);
            }
        }
        ,
        Giuddiagnostico {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getGiuddiagnostico();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setGiuddiagnostico((Integer)value);
            }
        }
        ,
        Coddip {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getCoddip();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setCoddip((String)value);
            }
        }
        ,
        Codregionale {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getCodregionale();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setCodregionale((Integer)value);
            }
        }
        ,
        Ulss {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getUlss();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Print_SoCnfSugg1livImpl obj) {
                return obj.getTpscr();
            }

            public void put(Print_SoCnfSugg1livImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Print_SoCnfSugg1livImpl object);

        public abstract void put(Print_SoCnfSugg1livImpl object, Object value);

        public int index() {
            return Print_SoCnfSugg1livImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Print_SoCnfSugg1livImpl.AttributesEnum.firstIndex() + Print_SoCnfSugg1livImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Print_SoCnfSugg1livImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDSUGG = AttributesEnum.Idsugg.index();
    public static final int IDTPINVITO = AttributesEnum.Idtpinvito.index();
    public static final int ESITOSUGG = AttributesEnum.Esitosugg.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int GGRICHIAMO = AttributesEnum.Ggrichiamo.index();
    public static final int INVIAINTERVENTO = AttributesEnum.Inviaintervento.index();
    public static final int TIPOESAME = AttributesEnum.Tipoesame.index();
    public static final int INVIA2LIV = AttributesEnum.Invia2liv.index();
    public static final int GIUDDIAGNOSTICO = AttributesEnum.Giuddiagnostico.index();
    public static final int CODDIP = AttributesEnum.Coddip.index();
    public static final int CODREGIONALE = AttributesEnum.Codregionale.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Print_SoCnfSugg1livImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.print.Print_SoCnfSugg1liv");
    }

    /**
     *
     *  Gets the attribute value for Idsugg, using the alias name Idsugg
     */
    public Integer getIdsugg()
  {
    return (Integer)getAttributeInternal(IDSUGG);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idsugg
   */
  public void setIdsugg(Integer value)
  {
    setAttributeInternal(IDSUGG, value);
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
   *  Gets the attribute value for Esitosugg, using the alias name Esitosugg
   */
  public String getEsitosugg()
  {
    return (String)getAttributeInternal(ESITOSUGG);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Esitosugg
   */
  public void setEsitosugg(String value)
  {
    setAttributeInternal(ESITOSUGG, value);
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
   *  Gets the attribute value for Ggrichiamo, using the alias name Ggrichiamo
   */
  public Integer getGgrichiamo()
  {
    return (Integer)getAttributeInternal(GGRICHIAMO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Ggrichiamo
   */
  public void setGgrichiamo(Integer value)
  {
    setAttributeInternal(GGRICHIAMO, value);
  }

  /**
   * 
   *  Gets the attribute value for Inviaintervento, using the alias name Inviaintervento
   */
  public Integer getInviaintervento()
  {
    return (Integer)getAttributeInternal(INVIAINTERVENTO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Inviaintervento
   */
  public void setInviaintervento(Integer value)
  {
    setAttributeInternal(INVIAINTERVENTO, value);
  }

  /**
   * 
   *  Gets the attribute value for Tipoesame, using the alias name Tipoesame
   */
  public String getTipoesame()
  {
    return (String)getAttributeInternal(TIPOESAME);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Tipoesame
   */
  public void setTipoesame(String value)
  {
    setAttributeInternal(TIPOESAME, value);
  }

  /**
   * 
   *  Gets the attribute value for Invia2liv, using the alias name Invia2liv
   */
  public Integer getInvia2liv()
  {
    return (Integer)getAttributeInternal(INVIA2LIV);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Invia2liv
   */
  public void setInvia2liv(Integer value)
  {
    setAttributeInternal(INVIA2LIV, value);
  }

  /**
   * 
   *  Gets the attribute value for Giuddiagnostico, using the alias name Giuddiagnostico
   */
  public Integer getGiuddiagnostico()
  {
    return (Integer)getAttributeInternal(GIUDDIAGNOSTICO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Giuddiagnostico
   */
  public void setGiuddiagnostico(Integer value)
  {
    setAttributeInternal(GIUDDIAGNOSTICO, value);
  }

  /**
   * 
   *  Gets the attribute value for Coddip, using the alias name Coddip
   */
  public String getCoddip()
  {
    return (String)getAttributeInternal(CODDIP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Coddip
   */
  public void setCoddip(String value)
  {
    setAttributeInternal(CODDIP, value);
  }

  /**
   * 
   *  Gets the attribute value for Codregionale, using the alias name Codregionale
   */
  public Integer getCodregionale()
  {
    return (Integer)getAttributeInternal(CODREGIONALE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codregionale
   */
  public void setCodregionale(Integer value)
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
     * @param idsugg key constituent
     * @param ulss key constituent
     * @param tpscr key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idsugg, String ulss, String tpscr) {
        return new Key(new Object[]{idsugg, ulss, tpscr});
    }


}
