package model.agenda;

import oracle.jbo.domain.Date;
import oracle.jbo.domain.Timestamp;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Agenda_AppRiassViewRowImpl extends ViewRowImpl implements model.common.Agenda_AppRiassViewRow 
{


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idinvito {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getIdinvito();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setIdinvito((Integer) value);
            }
        }
        ,
        Dtapp {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getDtapp();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setDtapp((Date) value);
            }
        }
        ,
        Ctapp {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getCtapp();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setCtapp((String) value);
            }
        }
        ,
        Idcentroprelievo {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getIdcentroprelievo();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setIdcentroprelievo((Integer) value);
            }
        }
        ,
        Nome {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getNome();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setNome((String) value);
            }
        }
        ,
        Cognome {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getCognome();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setCognome((String) value);
            }
        }
        ,
        DataNascita {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getDataNascita();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setDataNascita((Date) value);
            }
        }
        ,
        Idtpinvito {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getIdtpinvito();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setIdtpinvito((String) value);
            }
        }
        ,
        Codts {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getCodts();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setCodts((String) value);
            }
        }
        ,
        Tel1 {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getTel1();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setTel1((String) value);
            }
        }
        ,
        Ulss {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        Dtoraapp {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getDtoraapp();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setDtoraapp((Timestamp) value);
            }
        }
        ,
        Codesitoinvito {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getCodesitoinvito();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setCodesitoinvito((String) value);
            }
        }
        ,
        Dtcreazione {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setDtcreazione((Date) value);
            }
        }
        ,
        DocIdent {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getDocIdent();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setDocIdent((String) value);
            }
        }
        ,
        Oraapp {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getOraapp();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setOraapp((Integer) value);
            }
        }
        ,
        Minapp {
            public Object get(Agenda_AppRiassViewRowImpl obj) {
                return obj.getMinapp();
            }

            public void put(Agenda_AppRiassViewRowImpl obj, Object value) {
                obj.setMinapp((Integer) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Agenda_AppRiassViewRowImpl object);

        public abstract void put(Agenda_AppRiassViewRowImpl object, Object value);

        public int index() {
            return Agenda_AppRiassViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Agenda_AppRiassViewRowImpl.AttributesEnum.firstIndex() + Agenda_AppRiassViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Agenda_AppRiassViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int DTAPP = AttributesEnum.Dtapp.index();
    public static final int CTAPP = AttributesEnum.Ctapp.index();
    public static final int IDCENTROPRELIEVO = AttributesEnum.Idcentroprelievo.index();
    public static final int NOME = AttributesEnum.Nome.index();
    public static final int COGNOME = AttributesEnum.Cognome.index();
    public static final int DATANASCITA = AttributesEnum.DataNascita.index();
    public static final int IDTPINVITO = AttributesEnum.Idtpinvito.index();
    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int TEL1 = AttributesEnum.Tel1.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int DTORAAPP = AttributesEnum.Dtoraapp.index();
    public static final int CODESITOINVITO = AttributesEnum.Codesitoinvito.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int DOCIDENT = AttributesEnum.DocIdent.index();
    public static final int ORAAPP = AttributesEnum.Oraapp.index();
    public static final int MINAPP = AttributesEnum.Minapp.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Agenda_AppRiassViewRowImpl()
  {
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Idinvito
   */
  public Integer getIdinvito()
  {
    return (Integer)getAttributeInternal(IDINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Idinvito
   */
  public void setIdinvito(Integer value)
  {
    setAttributeInternal(IDINVITO, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Dtapp
   */
  public Date getDtapp()
  {
    return (Date)getAttributeInternal(DTAPP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Dtapp
   */
  public void setDtapp(Date value)
  {
    setAttributeInternal(DTAPP, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Ctapp
   */
  public String getCtapp()
  {
    return (String)getAttributeInternal(CTAPP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Ctapp
   */
  public void setCtapp(String value)
  {
    setAttributeInternal(CTAPP, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Idcentroprelievo
   */
  public Integer getIdcentroprelievo()
  {
    return (Integer)getAttributeInternal(IDCENTROPRELIEVO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Idcentroprelievo
   */
  public void setIdcentroprelievo(Integer value)
  {
    setAttributeInternal(IDCENTROPRELIEVO, value);
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
   *  Gets the attribute value for the calculated attribute DataNascita
   */
  public Date getDataNascita()
  {
    return (Date)getAttributeInternal(DATANASCITA);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute DataNascita
   */
  public void setDataNascita(Date value)
  {
    setAttributeInternal(DATANASCITA, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Idtpinvito
   */
  public String getIdtpinvito()
  {
    return (String)getAttributeInternal(IDTPINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Idtpinvito
   */
  public void setIdtpinvito(String value)
  {
    setAttributeInternal(IDTPINVITO, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Codts
   */
  public String getCodts()
  {
    return (String)getAttributeInternal(CODTS);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Codts
   */
  public void setCodts(String value)
  {
    setAttributeInternal(CODTS, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Tel1
   */
  public String getTel1()
  {
    return (String)getAttributeInternal(TEL1);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Tel1
   */
  public void setTel1(String value)
  {
    setAttributeInternal(TEL1, value);
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

  /**
   * 
   *  Gets the attribute value for the calculated attribute Dtoraapp
   */
  public Timestamp getDtoraapp()
  {
    return (Timestamp)getAttributeInternal(DTORAAPP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Dtoraapp
   */
  public void setDtoraapp(Timestamp value)
  {
    setAttributeInternal(DTORAAPP, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Codesitoinvito
   */
  public String getCodesitoinvito()
  {
    return (String)getAttributeInternal(CODESITOINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Codesitoinvito
   */
  public void setCodesitoinvito(String value)
  {
    setAttributeInternal(CODESITOINVITO, value);
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
   *  Gets the attribute value for the calculated attribute Dtcreazione
   */
  public Date getDtcreazione()
  {
    return (Date)getAttributeInternal(DTCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Dtcreazione
   */
  public void setDtcreazione(Date value)
  {
    setAttributeInternal(DTCREAZIONE, value);
  }

    /**
     * Gets the attribute value for the calculated attribute DocIdent.
     * @return the DocIdent
     */
    public String getDocIdent() {
        return (String) getAttributeInternal(DOCIDENT);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DocIdent.
     * @param value value to set the  DocIdent
     */
    public void setDocIdent(String value) {
        setAttributeInternal(DOCIDENT, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Oraapp.
     * @return the Oraapp
     */
    public Integer getOraapp() {
        return (Integer) getAttributeInternal(ORAAPP);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Oraapp.
     * @param value value to set the  Oraapp
     */
    public void setOraapp(Integer value) {
        setAttributeInternal(ORAAPP, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Minapp.
     * @return the Minapp
     */
    public Integer getMinapp() {
        return (Integer) getAttributeInternal(MINAPP);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Minapp.
     * @param value value to set the  Minapp
     */
    public void setMinapp(Integer value) {
        setAttributeInternal(MINAPP, value);
    }


}
