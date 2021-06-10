package model.stats;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Stats_ClassiDiagnostiche1livViewRowImpl extends ViewRowImpl implements model.common.Stats_ClassiDiagnostiche1livViewRow 
{


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Tipoesame {
            public Object get(Stats_ClassiDiagnostiche1livViewRowImpl obj) {
                return obj.getTipoesame();
            }

            public void put(Stats_ClassiDiagnostiche1livViewRowImpl obj, Object value) {
                obj.setTipoesame((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Stats_ClassiDiagnostiche1livViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Stats_ClassiDiagnostiche1livViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Stats_ClassiDiagnostiche1livViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Stats_ClassiDiagnostiche1livViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Ordine {
            public Object get(Stats_ClassiDiagnostiche1livViewRowImpl obj) {
                return obj.getOrdine();
            }

            public void put(Stats_ClassiDiagnostiche1livViewRowImpl obj, Object value) {
                obj.setOrdine((Integer)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Stats_ClassiDiagnostiche1livViewRowImpl object);

        public abstract void put(Stats_ClassiDiagnostiche1livViewRowImpl object, Object value);

        public int index() {
            return Stats_ClassiDiagnostiche1livViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Stats_ClassiDiagnostiche1livViewRowImpl.AttributesEnum.firstIndex() + Stats_ClassiDiagnostiche1livViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Stats_ClassiDiagnostiche1livViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int TIPOESAME = AttributesEnum.Tipoesame.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int ORDINE = AttributesEnum.Ordine.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Stats_ClassiDiagnostiche1livViewRowImpl()
  {
  }

  /**
   * 
   *  Gets the attribute value for the calculated attribute Tipoesame
   */
  public String getTipoesame()
  {
    return (String)getAttributeInternal(TIPOESAME);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Tipoesame
   */
  public void setTipoesame(String value)
  {
    setAttributeInternal(TIPOESAME, value);
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
   *  Gets the attribute value for the calculated attribute Ordine
   */
  public Integer getOrdine()
  {
    return (Integer)getAttributeInternal(ORDINE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for the calculated attribute Ordine
   */
  public void setOrdine(Integer value)
  {
    setAttributeInternal(ORDINE, value);
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
}