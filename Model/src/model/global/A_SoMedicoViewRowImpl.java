package model.global;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoMedicoViewRowImpl extends ViewRowImpl implements model.common.A_SoMedicoViewRow  
{


    public static final int ENTITY__SOMEDICO = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codiceregmedico {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getCodiceregmedico();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setCodiceregmedico((Integer) value);
            }
        }
        ,
        Codcom {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getCodcom();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setCodcom((String)value);
            }
        }
        ,
        Cognome {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getCognome();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setCognome((String)value);
            }
        }
        ,
        Nome {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getNome();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setNome((String)value);
            }
        }
        ,
        IndirizzoRes {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getIndirizzoRes();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setIndirizzoRes((String)value);
            }
        }
        ,
        Tel {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getTel();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setTel((String)value);
            }
        }
        ,
        Cell {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getCell();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setCell((Integer) value);
            }
        }
        ,
        Dtadesione {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getDtadesione();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setDtadesione((Date)value);
            }
        }
        ,
        Dtfinevalop {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getDtfinevalop();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setDtfinevalop((Date)value);
            }
        }
        ,
        Ulss {
            public Object get(A_SoMedicoViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(A_SoMedicoViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(A_SoMedicoViewRowImpl object);

        public abstract void put(A_SoMedicoViewRowImpl object, Object value);

        public int index() {
            return A_SoMedicoViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return A_SoMedicoViewRowImpl.AttributesEnum.firstIndex() + A_SoMedicoViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = A_SoMedicoViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int CODICEREGMEDICO = AttributesEnum.Codiceregmedico.index();
    public static final int CODCOM = AttributesEnum.Codcom.index();
    public static final int COGNOME = AttributesEnum.Cognome.index();
    public static final int NOME = AttributesEnum.Nome.index();
    public static final int INDIRIZZORES = AttributesEnum.IndirizzoRes.index();
    public static final int TEL = AttributesEnum.Tel.index();
    public static final int CELL = AttributesEnum.Cell.index();
    public static final int DTADESIONE = AttributesEnum.Dtadesione.index();
    public static final int DTFINEVALOP = AttributesEnum.Dtfinevalop.index();
    public static final int ULSS = AttributesEnum.Ulss.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public A_SoMedicoViewRowImpl()
  {
  }

  /**
   * 
   *  Gets _SoMedico entity object.
   */
  public A_SoMedicoImpl get_SoMedico()
  {
    return (A_SoMedicoImpl)getEntity(0);
  }

  /**
     *
     *  Gets the attribute value for CODICEREGMEDICO using the alias name Codiceregmedico
     */
    public Integer getCodiceregmedico()
  {
        return (Integer) getAttributeInternal(CODICEREGMEDICO);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODICEREGMEDICO using the alias name Codiceregmedico
   */
  public void setCodiceregmedico(Integer value)
  {
    setAttributeInternal(CODICEREGMEDICO, value);
  }

  /**
   * 
   *  Gets the attribute value for CODCOM using the alias name Codcom
   */
  public String getCodcom()
  {
    return (String)getAttributeInternal(CODCOM);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODCOM using the alias name Codcom
   */
  public void setCodcom(String value)
  {
    setAttributeInternal(CODCOM, value);
  }

  /**
   * 
   *  Gets the attribute value for COGNOME using the alias name Cognome
   */
  public String getCognome()
  {
    return (String)getAttributeInternal(COGNOME);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for COGNOME using the alias name Cognome
   */
  public void setCognome(String value)
  {
    setAttributeInternal(COGNOME, value);
  }

  /**
   * 
   *  Gets the attribute value for NOME using the alias name Nome
   */
  public String getNome()
  {
    return (String)getAttributeInternal(NOME);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for NOME using the alias name Nome
   */
  public void setNome(String value)
  {
    setAttributeInternal(NOME, value);
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

  /**
     *
     *  Gets the attribute value for CELL using the alias name Cell
     */
    public Integer getCell()
  {
        return (Integer) getAttributeInternal(CELL);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CELL using the alias name Cell
   */
  public void setCell(Integer value)
  {
    setAttributeInternal(CELL, value);
  }

  /**
   * 
   *  Gets the attribute value for DTADESIONE using the alias name Dtadesione
   */
  public Date getDtadesione()
  {
    return (Date)getAttributeInternal(DTADESIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTADESIONE using the alias name Dtadesione
   */
  public void setDtadesione(Date value)
  {
    setAttributeInternal(DTADESIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for DTFINEVALOP using the alias name Dtfinevalop
   */
  public Date getDtfinevalop()
  {
    return (Date)getAttributeInternal(DTFINEVALOP);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTFINEVALOP using the alias name Dtfinevalop
   */
  public void setDtfinevalop(Date value)
  {
    setAttributeInternal(DTFINEVALOP, value);
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