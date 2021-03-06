package model.stats;

import model.conf.Cnf_SoCnfSugg2livImpl;

import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Stats_SoCnfSugg2livViewRowImpl extends ViewRowImpl implements model.common.Stats_SoCnfSugg2livViewRow 
{


    public static final int ENTITY_CNF_SOCNFSUGG2LIV = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idsugg2l {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getIdsugg2l();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setIdsugg2l((Integer) value);
            }
        }
        ,
        Idtpinvito {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setIdtpinvito((String)value);
            }
        }
        ,
        Esitosugg {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getEsitosugg();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setEsitosugg((String)value);
            }
        }
        ,
        Descrizione {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Ggrichiamo {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getGgrichiamo();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setGgrichiamo((Integer)value);
            }
        }
        ,
        Invia2liv {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getInvia2liv();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setInvia2liv((Integer)value);
            }
        }
        ,
        Inviaintervento {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getInviaintervento();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setInviaintervento((Integer)value);
            }
        }
        ,
        Codregionale {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getCodregionale();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setCodregionale((Integer)value);
            }
        }
        ,
        Ulss {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Ordine {
            public Object get(Stats_SoCnfSugg2livViewRowImpl obj) {
                return obj.getOrdine();
            }

            public void put(Stats_SoCnfSugg2livViewRowImpl obj, Object value) {
                obj.setOrdine((Integer)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Stats_SoCnfSugg2livViewRowImpl object);

        public abstract void put(Stats_SoCnfSugg2livViewRowImpl object, Object value);

        public int index() {
            return Stats_SoCnfSugg2livViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Stats_SoCnfSugg2livViewRowImpl.AttributesEnum.firstIndex() + Stats_SoCnfSugg2livViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Stats_SoCnfSugg2livViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDSUGG2L = AttributesEnum.Idsugg2l.index();
    public static final int IDTPINVITO = AttributesEnum.Idtpinvito.index();
    public static final int ESITOSUGG = AttributesEnum.Esitosugg.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int GGRICHIAMO = AttributesEnum.Ggrichiamo.index();
    public static final int INVIA2LIV = AttributesEnum.Invia2liv.index();
    public static final int INVIAINTERVENTO = AttributesEnum.Inviaintervento.index();
    public static final int CODREGIONALE = AttributesEnum.Codregionale.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int ORDINE = AttributesEnum.Ordine.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Stats_SoCnfSugg2livViewRowImpl()
  {
  }

  /**
     *
     *  Gets Cnf_SoCnfSugg2liv entity object.
     */
    public Cnf_SoCnfSugg2livImpl getCnf_SoCnfSugg2liv()
  {
    return (Cnf_SoCnfSugg2livImpl)getEntity(0);
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
   *  Gets the attribute value for ESITOSUGG using the alias name Esitosugg
   */
  public String getEsitosugg()
  {
    return (String)getAttributeInternal(ESITOSUGG);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ESITOSUGG using the alias name Esitosugg
   */
  public void setEsitosugg(String value)
  {
    setAttributeInternal(ESITOSUGG, value);
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
   *  Gets the attribute value for GGRICHIAMO using the alias name Ggrichiamo
   */
  public Integer getGgrichiamo()
  {
    return (Integer)getAttributeInternal(GGRICHIAMO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for GGRICHIAMO using the alias name Ggrichiamo
   */
  public void setGgrichiamo(Integer value)
  {
    setAttributeInternal(GGRICHIAMO, value);
  }

  /**
   * 
   *  Gets the attribute value for INVIA2LIV using the alias name Invia2liv
   */
  public Integer getInvia2liv()
  {
    return (Integer)getAttributeInternal(INVIA2LIV);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for INVIA2LIV using the alias name Invia2liv
   */
  public void setInvia2liv(Integer value)
  {
    setAttributeInternal(INVIA2LIV, value);
  }

  /**
   * 
   *  Gets the attribute value for INVIAINTERVENTO using the alias name Inviaintervento
   */
  public Integer getInviaintervento()
  {
    return (Integer)getAttributeInternal(INVIAINTERVENTO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for INVIAINTERVENTO using the alias name Inviaintervento
   */
  public void setInviaintervento(Integer value)
  {
    setAttributeInternal(INVIAINTERVENTO, value);
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
   *  Gets the attribute value for CODREGIONALE using the alias name Codregionale
   */
  public Integer getCodregionale()
  {
    return (Integer)getAttributeInternal(CODREGIONALE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODREGIONALE using the alias name Codregionale
   */
  public void setCodregionale(Integer value)
  {
    setAttributeInternal(CODREGIONALE, value);
  }

  /**
   * 
   *  Gets the attribute value for ORDINE using the alias name Ordine
   */
  public Integer getOrdine()
  {
    return (Integer)getAttributeInternal(ORDINE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ORDINE using the alias name Ordine
   */
  public void setOrdine(Integer value)
  {
    setAttributeInternal(ORDINE, value);
  }
}