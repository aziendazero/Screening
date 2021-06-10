package model.stats;

import model.global.A_SoCnfTpinvitoImpl;
import model.conf.Cnf_SoCnfCategTpinvitoImpl;

import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Stats_SoCnfTpinvitoViewRowImpl extends ViewRowImpl implements model.common.Stats_SoCnfTpinvitoViewRow 
{


    public static final int ENTITY_A_SOCNFTPINVITO = 0;
    public static final int ENTITY_CNF_SOCNFCATEGTPINVITO = 1;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idtpinvito {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setIdtpinvito((String)value);
            }
        }
        ,
        Idcateg {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getIdcateg();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setIdcateg((Integer)value);
            }
        }
        ,
        Descrizione {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Descrbreve {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getDescrbreve();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setDescrbreve((String)value);
            }
        }
        ,
        Appuntamento {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getAppuntamento();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setAppuntamento((Integer)value);
            }
        }
        ,
        Invio2liv {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getInvio2liv();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setInvio2liv((Integer)value);
            }
        }
        ,
        Codregionale {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getCodregionale();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setCodregionale((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Ggsollecito {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getGgsollecito();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setGgsollecito((Integer)value);
            }
        }
        ,
        IdtpinvSoll {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getIdtpinvSoll();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setIdtpinvSoll((String)value);
            }
        }
        ,
        Livello {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getLivello();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setLivello((Integer)value);
            }
        }
        ,
        Ordine {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getOrdine();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setOrdine((Integer)value);
            }
        }
        ,
        Idcateg1 {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getIdcateg1();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setIdcateg1((Integer)value);
            }
        }
        ,
        Descrizione1 {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getDescrizione1();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setDescrizione1((String)value);
            }
        }
        ,
        Tpscr1 {
            public Object get(Stats_SoCnfTpinvitoViewRowImpl obj) {
                return obj.getTpscr1();
            }

            public void put(Stats_SoCnfTpinvitoViewRowImpl obj, Object value) {
                obj.setTpscr1((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Stats_SoCnfTpinvitoViewRowImpl object);

        public abstract void put(Stats_SoCnfTpinvitoViewRowImpl object, Object value);

        public int index() {
            return Stats_SoCnfTpinvitoViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Stats_SoCnfTpinvitoViewRowImpl.AttributesEnum.firstIndex() + Stats_SoCnfTpinvitoViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Stats_SoCnfTpinvitoViewRowImpl.AttributesEnum.values();
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
    public static final int LIVELLO = AttributesEnum.Livello.index();
    public static final int ORDINE = AttributesEnum.Ordine.index();
    public static final int IDCATEG1 = AttributesEnum.Idcateg1.index();
    public static final int DESCRIZIONE1 = AttributesEnum.Descrizione1.index();
    public static final int TPSCR1 = AttributesEnum.Tpscr1.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Stats_SoCnfTpinvitoViewRowImpl()
  {
  }

  /**
     *
     *  Gets A_SoCnfTpinvito entity object.
     */
    public A_SoCnfTpinvitoImpl getA_SoCnfTpinvito()
  {
    return (A_SoCnfTpinvitoImpl)getEntity(0);
  }

  /**
     *
     *  Gets Cnf_SoCnfCategTpinvito entity object.
     */
    public Cnf_SoCnfCategTpinvitoImpl getCnf_SoCnfCategTpinvito()
  {
    return (Cnf_SoCnfCategTpinvitoImpl)getEntity(1);
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
   *  Gets the attribute value for IDCATEG using the alias name Idcateg
   */
  public Integer getIdcateg()
  {
    return (Integer)getAttributeInternal(IDCATEG);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDCATEG using the alias name Idcateg
   */
  public void setIdcateg(Integer value)
  {
    setAttributeInternal(IDCATEG, value);
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
   *  Gets the attribute value for APPUNTAMENTO using the alias name Appuntamento
   */
  public Integer getAppuntamento()
  {
    return (Integer)getAttributeInternal(APPUNTAMENTO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for APPUNTAMENTO using the alias name Appuntamento
   */
  public void setAppuntamento(Integer value)
  {
    setAttributeInternal(APPUNTAMENTO, value);
  }

  /**
   * 
   *  Gets the attribute value for INVIO2LIV using the alias name Invio2liv
   */
  public Integer getInvio2liv()
  {
    return (Integer)getAttributeInternal(INVIO2LIV);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for INVIO2LIV using the alias name Invio2liv
   */
  public void setInvio2liv(Integer value)
  {
    setAttributeInternal(INVIO2LIV, value);
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
   *  Gets the attribute value for GGSOLLECITO using the alias name Ggsollecito
   */
  public Integer getGgsollecito()
  {
    return (Integer)getAttributeInternal(GGSOLLECITO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for GGSOLLECITO using the alias name Ggsollecito
   */
  public void setGgsollecito(Integer value)
  {
    setAttributeInternal(GGSOLLECITO, value);
  }

  /**
   * 
   *  Gets the attribute value for IDTPINV_SOLL using the alias name IdtpinvSoll
   */
  public String getIdtpinvSoll()
  {
    return (String)getAttributeInternal(IDTPINVSOLL);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDTPINV_SOLL using the alias name IdtpinvSoll
   */
  public void setIdtpinvSoll(String value)
  {
    setAttributeInternal(IDTPINVSOLL, value);
  }

  /**
   * 
   *  Gets the attribute value for LIVELLO using the alias name Livello
   */
  public Integer getLivello()
  {
    return (Integer)getAttributeInternal(LIVELLO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for LIVELLO using the alias name Livello
   */
  public void setLivello(Integer value)
  {
    setAttributeInternal(LIVELLO, value);
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
   *  Gets the attribute value for IDCATEG using the alias name Idcateg1
   */
  public Integer getIdcateg1()
  {
    return (Integer)getAttributeInternal(IDCATEG1);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDCATEG using the alias name Idcateg1
   */
  public void setIdcateg1(Integer value)
  {
    setAttributeInternal(IDCATEG1, value);
  }

  /**
   * 
   *  Gets the attribute value for DESCRIZIONE using the alias name Descrizione1
   */
  public String getDescrizione1()
  {
    return (String)getAttributeInternal(DESCRIZIONE1);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DESCRIZIONE using the alias name Descrizione1
   */
  public void setDescrizione1(String value)
  {
    setAttributeInternal(DESCRIZIONE1, value);
  }

  /**
   * 
   *  Gets the attribute value for TPSCR using the alias name Tpscr1
   */
  public String getTpscr1()
  {
    return (String)getAttributeInternal(TPSCR1);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TPSCR using the alias name Tpscr1
   */
  public void setTpscr1(String value)
  {
    setAttributeInternal(TPSCR1, value);
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