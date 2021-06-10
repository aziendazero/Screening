package model.stats;

import model.pianolavoro.PL_SoCnfEsitoinvitoImpl;

import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Stats_SoCnfEsitoinvitoViewRowImpl extends ViewRowImpl implements model.common.Stats_SoCnfEsitoinvitoViewRow 
{


    public static final int ENTITY_PL_SOCNFESITOINVITO = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codesitoinvito {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getCodesitoinvito();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setCodesitoinvito((String)value);
            }
        }
        ,
        Livesito {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getLivesito();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setLivesito((Integer)value);
            }
        }
        ,
        Esitoinvito {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getEsitoinvito();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setEsitoinvito((String)value);
            }
        }
        ,
        Descrbreve {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getDescrbreve();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setDescrbreve((String)value);
            }
        }
        ,
        Descrizione {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Descrsugg {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getDescrsugg();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setDescrsugg((String)value);
            }
        }
        ,
        Catesito {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getCatesito();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setCatesito((String)value);
            }
        }
        ,
        Tipoesito {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getTipoesito();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setTipoesito((String)value);
            }
        }
        ,
        Idtpinvito {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setIdtpinvito((String)value);
            }
        }
        ,
        Ggrichiamo {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getGgrichiamo();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setGgrichiamo((Integer)value);
            }
        }
        ,
        Codregionale {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getCodregionale();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setCodregionale((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Ordine {
            public Object get(Stats_SoCnfEsitoinvitoViewRowImpl obj) {
                return obj.getOrdine();
            }

            public void put(Stats_SoCnfEsitoinvitoViewRowImpl obj, Object value) {
                obj.setOrdine((Integer)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Stats_SoCnfEsitoinvitoViewRowImpl object);

        public abstract void put(Stats_SoCnfEsitoinvitoViewRowImpl object, Object value);

        public int index() {
            return Stats_SoCnfEsitoinvitoViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Stats_SoCnfEsitoinvitoViewRowImpl.AttributesEnum.firstIndex() + Stats_SoCnfEsitoinvitoViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Stats_SoCnfEsitoinvitoViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int CODESITOINVITO = AttributesEnum.Codesitoinvito.index();
    public static final int LIVESITO = AttributesEnum.Livesito.index();
    public static final int ESITOINVITO = AttributesEnum.Esitoinvito.index();
    public static final int DESCRBREVE = AttributesEnum.Descrbreve.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int DESCRSUGG = AttributesEnum.Descrsugg.index();
    public static final int CATESITO = AttributesEnum.Catesito.index();
    public static final int TIPOESITO = AttributesEnum.Tipoesito.index();
    public static final int IDTPINVITO = AttributesEnum.Idtpinvito.index();
    public static final int GGRICHIAMO = AttributesEnum.Ggrichiamo.index();
    public static final int CODREGIONALE = AttributesEnum.Codregionale.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int ORDINE = AttributesEnum.Ordine.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Stats_SoCnfEsitoinvitoViewRowImpl()
  {
  }

  /**
     *
     *  Gets PL_SoCnfEsitoinvito entity object.
     */
    public PL_SoCnfEsitoinvitoImpl getPL_SoCnfEsitoinvito()
  {
    return (PL_SoCnfEsitoinvitoImpl)getEntity(0);
  }

  /**
   * 
   *  Gets the attribute value for CODESITOINVITO using the alias name Codesitoinvito
   */
  public String getCodesitoinvito()
  {
    return (String)getAttributeInternal(CODESITOINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODESITOINVITO using the alias name Codesitoinvito
   */
  public void setCodesitoinvito(String value)
  {
    setAttributeInternal(CODESITOINVITO, value);
  }

  /**
   * 
   *  Gets the attribute value for LIVESITO using the alias name Livesito
   */
  public Integer getLivesito()
  {
    return (Integer)getAttributeInternal(LIVESITO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for LIVESITO using the alias name Livesito
   */
  public void setLivesito(Integer value)
  {
    setAttributeInternal(LIVESITO, value);
  }

  /**
   * 
   *  Gets the attribute value for ESITOINVITO using the alias name Esitoinvito
   */
  public String getEsitoinvito()
  {
    return (String)getAttributeInternal(ESITOINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ESITOINVITO using the alias name Esitoinvito
   */
  public void setEsitoinvito(String value)
  {
    setAttributeInternal(ESITOINVITO, value);
  }

  /**
   * 
   *  Gets the attribute value for DESCRBREVE using the alias name Descrbreve
   */
  public String getDescrbreve()
  {
    return (String)getAttributeInternal(DESCRBREVE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DESCRBREVE using the alias name Descrbreve
   */
  public void setDescrbreve(String value)
  {
    setAttributeInternal(DESCRBREVE, value);
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
   *  Gets the attribute value for DESCRSUGG using the alias name Descrsugg
   */
  public String getDescrsugg()
  {
    return (String)getAttributeInternal(DESCRSUGG);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DESCRSUGG using the alias name Descrsugg
   */
  public void setDescrsugg(String value)
  {
    setAttributeInternal(DESCRSUGG, value);
  }

  /**
   * 
   *  Gets the attribute value for CATESITO using the alias name Catesito
   */
  public String getCatesito()
  {
    return (String)getAttributeInternal(CATESITO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CATESITO using the alias name Catesito
   */
  public void setCatesito(String value)
  {
    setAttributeInternal(CATESITO, value);
  }

  /**
   * 
   *  Gets the attribute value for TIPOESITO using the alias name Tipoesito
   */
  public String getTipoesito()
  {
    return (String)getAttributeInternal(TIPOESITO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TIPOESITO using the alias name Tipoesito
   */
  public void setTipoesito(String value)
  {
    setAttributeInternal(TIPOESITO, value);
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
   *  Gets the attribute value for CODREGIONALE using the alias name Codregionale
   */
  public String getCodregionale()
  {
    return (String)getAttributeInternal(CODREGIONALE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODREGIONALE using the alias name Codregionale
   */
  public void setCodregionale(String value)
  {
    setAttributeInternal(CODREGIONALE, value);
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
}