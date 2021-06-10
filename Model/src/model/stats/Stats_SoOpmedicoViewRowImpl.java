package model.stats;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Stats_SoOpmedicoViewRowImpl extends ViewRowImpl implements model.common.Stats_SoOpmedicoViewRow 
{


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idop {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getIdop();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setIdop((Integer)value);
            }
        }
        ,
        Codop {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getCodop();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setCodop((Integer)value);
            }
        }
        ,
        Idcentro {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getIdcentro();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setIdcentro((Integer)value);
            }
        }
        ,
        Cognome {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getCognome();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setCognome((String)value);
            }
        }
        ,
        Nome {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getNome();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setNome((String)value);
            }
        }
        ,
        Titolo {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getTitolo();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setTitolo((String)value);
            }
        }
        ,
        Dtfinevalopmedico {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getDtfinevalopmedico();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setDtfinevalopmedico((Date)value);
            }
        }
        ,
        Ulss {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Stats_SoOpmedicoViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Stats_SoOpmedicoViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Stats_SoOpmedicoViewRowImpl object);

        public abstract void put(Stats_SoOpmedicoViewRowImpl object, Object value);

        public int index() {
            return Stats_SoOpmedicoViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Stats_SoOpmedicoViewRowImpl.AttributesEnum.firstIndex() + Stats_SoOpmedicoViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Stats_SoOpmedicoViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDOP = AttributesEnum.Idop.index();
    public static final int CODOP = AttributesEnum.Codop.index();
    public static final int IDCENTRO = AttributesEnum.Idcentro.index();
    public static final int COGNOME = AttributesEnum.Cognome.index();
    public static final int NOME = AttributesEnum.Nome.index();
    public static final int TITOLO = AttributesEnum.Titolo.index();
    public static final int DTFINEVALOPMEDICO = AttributesEnum.Dtfinevalopmedico.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Stats_SoOpmedicoViewRowImpl()
  {
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Idop
   */
  public Integer getIdop()
  {
    return (Integer)getAttributeInternal(IDOP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Idop
   */
  public void setIdop(Integer value)
  {
    setAttributeInternal(IDOP, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Codop
   */
  public Integer getCodop()
  {
    return (Integer)getAttributeInternal(CODOP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Codop
   */
  public void setCodop(Integer value)
  {
    setAttributeInternal(CODOP, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Idcentro
   */
  public Integer getIdcentro()
  {
    return (Integer)getAttributeInternal(IDCENTRO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Idcentro
   */
  public void setIdcentro(Integer value)
  {
    setAttributeInternal(IDCENTRO, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Cognome
   */
  public String getCognome()
  {
    return (String)getAttributeInternal(COGNOME);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Cognome
   */
  public void setCognome(String value)
  {
    setAttributeInternal(COGNOME, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Nome
   */
  public String getNome()
  {
    return (String)getAttributeInternal(NOME);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Nome
   */
  public void setNome(String value)
  {
    setAttributeInternal(NOME, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Titolo
   */
  public String getTitolo()
  {
    return (String)getAttributeInternal(TITOLO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Titolo
   */
  public void setTitolo(String value)
  {
    setAttributeInternal(TITOLO, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Dtfinevalopmedico
   */
  public Date getDtfinevalopmedico()
  {
    return (Date)getAttributeInternal(DTFINEVALOPMEDICO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Dtfinevalopmedico
   */
  public void setDtfinevalopmedico(Date value)
  {
    setAttributeInternal(DTFINEVALOPMEDICO, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Ulss
   */
  public String getUlss()
  {
    return (String)getAttributeInternal(ULSS);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Ulss
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
   *  Gets the attribute value for the calculated attribute Tpscr
   */
  public String getTpscr()
  {
    return (String)getAttributeInternal(TPSCR);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Tpscr
   */
  public void setTpscr(String value)
  {
    setAttributeInternal(TPSCR, value);
  }
}