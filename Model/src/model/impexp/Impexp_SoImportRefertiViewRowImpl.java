package model.impexp;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Impexp_SoImportRefertiViewRowImpl extends ViewRowImpl implements model.common.Impexp_SoImportRefertiViewRow 
{


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codts {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getCodts();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setCodts((String)value);
            }
        }
        ,
        CognomeNome {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getCognomeNome();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setCognomeNome((String)value);
            }
        }
        ,
        Datanas {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getDatanas();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setDatanas((String)value);
            }
        }
        ,
        TipoAcc {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getTipoAcc();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setTipoAcc((String)value);
            }
        }
        ,
        DataReferto {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getDataReferto();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setDataReferto((String)value);
            }
        }
        ,
        AnnoAcc {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getAnnoAcc();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setAnnoAcc((String)value);
            }
        }
        ,
        Snotop {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getSnotop();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setSnotop((String)value);
            }
        }
        ,
        Snomo1 {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getSnomo1();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setSnomo1((String)value);
            }
        }
        ,
        Snomo2 {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getSnomo2();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setSnomo2((String)value);
            }
        }
        ,
        Lettore {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getLettore();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setLettore((String)value);
            }
        }
        ,
        Firma {
            public Object get(Impexp_SoImportRefertiViewRowImpl obj) {
                return obj.getFirma();
            }

            public void put(Impexp_SoImportRefertiViewRowImpl obj, Object value) {
                obj.setFirma((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Impexp_SoImportRefertiViewRowImpl object);

        public abstract void put(Impexp_SoImportRefertiViewRowImpl object, Object value);

        public int index() {
            return Impexp_SoImportRefertiViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Impexp_SoImportRefertiViewRowImpl.AttributesEnum.firstIndex() + Impexp_SoImportRefertiViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Impexp_SoImportRefertiViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int COGNOMENOME = AttributesEnum.CognomeNome.index();
    public static final int DATANAS = AttributesEnum.Datanas.index();
    public static final int TIPOACC = AttributesEnum.TipoAcc.index();
    public static final int DATAREFERTO = AttributesEnum.DataReferto.index();
    public static final int ANNOACC = AttributesEnum.AnnoAcc.index();
    public static final int SNOTOP = AttributesEnum.Snotop.index();
    public static final int SNOMO1 = AttributesEnum.Snomo1.index();
    public static final int SNOMO2 = AttributesEnum.Snomo2.index();
    public static final int LETTORE = AttributesEnum.Lettore.index();
    public static final int FIRMA = AttributesEnum.Firma.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Impexp_SoImportRefertiViewRowImpl()
  {
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
   *  Gets the attribute value for the calculated attribute CognomeNome
   */
  public String getCognomeNome()
  {
    return (String)getAttributeInternal(COGNOMENOME);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute CognomeNome
   */
  public void setCognomeNome(String value)
  {
    setAttributeInternal(COGNOMENOME, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Datanas
   */
  public String getDatanas()
  {
    return (String)getAttributeInternal(DATANAS);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Datanas
   */
  public void setDatanas(String value)
  {
    setAttributeInternal(DATANAS, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute TipoAcc
   */
  public String getTipoAcc()
  {
    return (String)getAttributeInternal(TIPOACC);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute TipoAcc
   */
  public void setTipoAcc(String value)
  {
    setAttributeInternal(TIPOACC, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute DataReferto
   */
  public String getDataReferto()
  {
    return (String)getAttributeInternal(DATAREFERTO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute DataReferto
   */
  public void setDataReferto(String value)
  {
    setAttributeInternal(DATAREFERTO, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute AnnoAcc
   */
  public String getAnnoAcc()
  {
    return (String)getAttributeInternal(ANNOACC);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute AnnoAcc
   */
  public void setAnnoAcc(String value)
  {
    setAttributeInternal(ANNOACC, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Snotop
   */
  public String getSnotop()
  {
    return (String)getAttributeInternal(SNOTOP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Snotop
   */
  public void setSnotop(String value)
  {
    setAttributeInternal(SNOTOP, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Snomo1
   */
  public String getSnomo1()
  {
    return (String)getAttributeInternal(SNOMO1);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Snomo1
   */
  public void setSnomo1(String value)
  {
    setAttributeInternal(SNOMO1, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Snomo2
   */
  public String getSnomo2()
  {
    return (String)getAttributeInternal(SNOMO2);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Snomo2
   */
  public void setSnomo2(String value)
  {
    setAttributeInternal(SNOMO2, value);
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Lettore
   */
  public String getLettore()
  {
    return (String)getAttributeInternal(LETTORE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Lettore
   */
  public void setLettore(String value)
  {
    setAttributeInternal(LETTORE, value);
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
   *  Gets the attribute value for the calculated attribute Firma
   */
  public String getFirma()
  {
    return (String)getAttributeInternal(FIRMA);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Firma
   */
  public void setFirma(String value)
  {
    setAttributeInternal(FIRMA, value);
  }
}