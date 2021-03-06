package model.conf;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoCnfTpscrViewRowImpl extends ViewRowImpl implements model.common.Cnf_SoCnfTpscrViewRow 
{


    public static final int ENTITY_CNF_SOCNFTPSCR = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codscr {
            public Object get(Cnf_SoCnfTpscrViewRowImpl obj) {
                return obj.getCodscr();
            }

            public void put(Cnf_SoCnfTpscrViewRowImpl obj, Object value) {
                obj.setCodscr((String)value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoCnfTpscrViewRowImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoCnfTpscrViewRowImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Etada {
            public Object get(Cnf_SoCnfTpscrViewRowImpl obj) {
                return obj.getEtada();
            }

            public void put(Cnf_SoCnfTpscrViewRowImpl obj, Object value) {
                obj.setEtada((Integer)value);
            }
        }
        ,
        Etaa {
            public Object get(Cnf_SoCnfTpscrViewRowImpl obj) {
                return obj.getEtaa();
            }

            public void put(Cnf_SoCnfTpscrViewRowImpl obj, Object value) {
                obj.setEtaa((Integer)value);
            }
        }
        ,
        Durata {
            public Object get(Cnf_SoCnfTpscrViewRowImpl obj) {
                return obj.getDurata();
            }

            public void put(Cnf_SoCnfTpscrViewRowImpl obj, Object value) {
                obj.setDurata((Integer)value);
            }
        }
        ,
        Sesso {
            public Object get(Cnf_SoCnfTpscrViewRowImpl obj) {
                return obj.getSesso();
            }

            public void put(Cnf_SoCnfTpscrViewRowImpl obj, Object value) {
                obj.setSesso((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfTpscrViewRowImpl object);

        public abstract void put(Cnf_SoCnfTpscrViewRowImpl object, Object value);

        public int index() {
            return Cnf_SoCnfTpscrViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoCnfTpscrViewRowImpl.AttributesEnum.firstIndex() + Cnf_SoCnfTpscrViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoCnfTpscrViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int CODSCR = AttributesEnum.Codscr.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int ETADA = AttributesEnum.Etada.index();
    public static final int ETAA = AttributesEnum.Etaa.index();
    public static final int DURATA = AttributesEnum.Durata.index();
    public static final int SESSO = AttributesEnum.Sesso.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoCnfTpscrViewRowImpl()
  {
  }

  /**
   * 
   *  Gets Cnf_SoCnfTpscr entity object.
   */
  public Cnf_SoCnfTpscrImpl getCnf_SoCnfTpscr()
  {
    return (Cnf_SoCnfTpscrImpl)getEntity(0);
  }

  /**
   * 
   *  Gets the attribute value for CODSCR using the alias name Codscr
   */
  public String getCodscr()
  {
    return (String)getAttributeInternal(CODSCR);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODSCR using the alias name Codscr
   */
  public void setCodscr(String value)
  {
    setAttributeInternal(CODSCR, value);
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
   *  Gets the attribute value for ETADA using the alias name Etada
   */
  public Integer getEtada()
  {
    return (Integer)getAttributeInternal(ETADA);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ETADA using the alias name Etada
   */
  public void setEtada(Integer value)
  {
    setAttributeInternal(ETADA, value);
  }

  /**
   * 
   *  Gets the attribute value for ETAA using the alias name Etaa
   */
  public Integer getEtaa()
  {
    return (Integer)getAttributeInternal(ETAA);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ETAA using the alias name Etaa
   */
  public void setEtaa(Integer value)
  {
    setAttributeInternal(ETAA, value);
  }

  /**
   * 
   *  Gets the attribute value for DURATA using the alias name Durata
   */
  public Integer getDurata()
  {
    return (Integer)getAttributeInternal(DURATA);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DURATA using the alias name Durata
   */
  public void setDurata(Integer value)
  {
    setAttributeInternal(DURATA, value);
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
   *  Gets the attribute value for SESSO using the alias name Sesso
   */
  public String getSesso()
  {
    return (String)getAttributeInternal(SESSO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for SESSO using the alias name Sesso
   */
  public void setSesso(String value)
  {
    setAttributeInternal(SESSO, value);
  }
}