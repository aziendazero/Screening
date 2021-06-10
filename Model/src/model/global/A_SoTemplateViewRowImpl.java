package model.global;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoTemplateViewRowImpl extends ViewRowImpl implements model.common.A_SoTemplateViewRow  
{


    public static final int ENTITY__SOTEMPLATE = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codtempl {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getCodtempl();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setCodtempl((Integer)value);
            }
        }
        ,
        Idtplettera {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getIdtplettera();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setIdtplettera((Integer)value);
            }
        }
        ,
        Codtemplreg {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getCodtemplreg();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setCodtemplreg((Integer)value);
            }
        }
        ,
        Nomefile {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getNomefile();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setNomefile((String)value);
            }
        }
        ,
        Filexml {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getFilexml();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setFilexml((BlobDomain)value);
            }
        }
        ,
        Descrizione {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getDescrizione();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setDescrizione((String)value);
            }
        }
        ,
        Dtcreazione {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setDtcreazione((Date)value);
            }
        }
        ,
        Autore {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getAutore();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setAutore((String)value);
            }
        }
        ,
        Dtultmod {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getDtultmod();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setDtultmod((Date)value);
            }
        }
        ,
        Opultmod {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getOpultmod();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setOpultmod((String)value);
            }
        }
        ,
        Dtfinevaltempl {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getDtfinevaltempl();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setDtfinevaltempl((Date)value);
            }
        }
        ,
        Ulss {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Compiled {
            public Object get(A_SoTemplateViewRowImpl obj) {
                return obj.getCompiled();
            }

            public void put(A_SoTemplateViewRowImpl obj, Object value) {
                obj.setCompiled((BlobDomain)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(A_SoTemplateViewRowImpl object);

        public abstract void put(A_SoTemplateViewRowImpl object, Object value);

        public int index() {
            return A_SoTemplateViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return A_SoTemplateViewRowImpl.AttributesEnum.firstIndex() + A_SoTemplateViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = A_SoTemplateViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int CODTEMPL = AttributesEnum.Codtempl.index();
    public static final int IDTPLETTERA = AttributesEnum.Idtplettera.index();
    public static final int CODTEMPLREG = AttributesEnum.Codtemplreg.index();
    public static final int NOMEFILE = AttributesEnum.Nomefile.index();
    public static final int FILEXML = AttributesEnum.Filexml.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int AUTORE = AttributesEnum.Autore.index();
    public static final int DTULTMOD = AttributesEnum.Dtultmod.index();
    public static final int OPULTMOD = AttributesEnum.Opultmod.index();
    public static final int DTFINEVALTEMPL = AttributesEnum.Dtfinevaltempl.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int COMPILED = AttributesEnum.Compiled.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public A_SoTemplateViewRowImpl()
  {
  }

  /**
   * 
   *  Gets _SoTemplate entity object.
   */
  public A_SoTemplateImpl get_SoTemplate()
  {
    return (A_SoTemplateImpl)getEntity(0);
  }

  /**
   * 
   *  Gets the attribute value for CODTEMPL using the alias name Codtempl
   */
  public Integer getCodtempl()
  {
    return (Integer)getAttributeInternal(CODTEMPL);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODTEMPL using the alias name Codtempl
   */
  public void setCodtempl(Integer value)
  {
    setAttributeInternal(CODTEMPL, value);
  }

  /**
   * 
   *  Gets the attribute value for IDTPLETTERA using the alias name Idtplettera
   */
  public Integer getIdtplettera()
  {
    return (Integer)getAttributeInternal(IDTPLETTERA);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDTPLETTERA using the alias name Idtplettera
   */
  public void setIdtplettera(Integer value)
  {
    setAttributeInternal(IDTPLETTERA, value);
  }

  /**
   * 
   *  Gets the attribute value for CODTEMPLREG using the alias name Codtemplreg
   */
  public Integer getCodtemplreg()
  {
    return (Integer)getAttributeInternal(CODTEMPLREG);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODTEMPLREG using the alias name Codtemplreg
   */
  public void setCodtemplreg(Integer value)
  {
    setAttributeInternal(CODTEMPLREG, value);
  }

  /**
   * 
   *  Gets the attribute value for NOMEFILE using the alias name Nomefile
   */
  public String getNomefile()
  {
    return (String)getAttributeInternal(NOMEFILE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for NOMEFILE using the alias name Nomefile
   */
  public void setNomefile(String value)
  {
    setAttributeInternal(NOMEFILE, value);
  }

  /**
   * 
   *  Gets the attribute value for FILEXML using the alias name Filexml
   */
  public BlobDomain getFilexml()
  {
    return (BlobDomain)getAttributeInternal(FILEXML);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for FILEXML using the alias name Filexml
   */
  public void setFilexml(BlobDomain value)
  {
    setAttributeInternal(FILEXML, value);
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
   *  Gets the attribute value for DTCREAZIONE using the alias name Dtcreazione
   */
  public Date getDtcreazione()
  {
    return (Date)getAttributeInternal(DTCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTCREAZIONE using the alias name Dtcreazione
   */
  public void setDtcreazione(Date value)
  {
    setAttributeInternal(DTCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for AUTORE using the alias name Autore
   */
  public String getAutore()
  {
    return (String)getAttributeInternal(AUTORE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for AUTORE using the alias name Autore
   */
  public void setAutore(String value)
  {
    setAttributeInternal(AUTORE, value);
  }

  /**
   * 
   *  Gets the attribute value for DTULTMOD using the alias name Dtultmod
   */
  public Date getDtultmod()
  {
    return (Date)getAttributeInternal(DTULTMOD);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTULTMOD using the alias name Dtultmod
   */
  public void setDtultmod(Date value)
  {
    setAttributeInternal(DTULTMOD, value);
  }

  /**
   * 
   *  Gets the attribute value for OPULTMOD using the alias name Opultmod
   */
  public String getOpultmod()
  {
    return (String)getAttributeInternal(OPULTMOD);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for OPULTMOD using the alias name Opultmod
   */
  public void setOpultmod(String value)
  {
    setAttributeInternal(OPULTMOD, value);
  }

  /**
   * 
   *  Gets the attribute value for DTFINEVALTEMPL using the alias name Dtfinevaltempl
   */
  public Date getDtfinevaltempl()
  {
    return (Date)getAttributeInternal(DTFINEVALTEMPL);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTFINEVALTEMPL using the alias name Dtfinevaltempl
   */
  public void setDtfinevaltempl(Date value)
  {
    setAttributeInternal(DTFINEVALTEMPL, value);
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
   *  Gets the attribute value for COMPILED using the alias name Compiled
   */
  public BlobDomain getCompiled()
  {
    return (BlobDomain)getAttributeInternal(COMPILED);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for COMPILED using the alias name Compiled
   */
  public void setCompiled(BlobDomain value)
  {
    setAttributeInternal(COMPILED, value);
  }
}