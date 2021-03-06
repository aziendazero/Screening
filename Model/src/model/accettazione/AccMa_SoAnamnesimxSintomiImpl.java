package model.accettazione;

import oracle.jbo.Key;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class AccMa_SoAnamnesimxSintomiImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        IdAnammxSint {
            public Object get(AccMa_SoAnamnesimxSintomiImpl obj) {
                return obj.getIdAnammxSint();
            }

            public void put(AccMa_SoAnamnesimxSintomiImpl obj, Object value) {
                obj.setIdAnammxSint((Integer) value);
            }
        }
        ,
        IdAnamma {
            public Object get(AccMa_SoAnamnesimxSintomiImpl obj) {
                return obj.getIdAnamma();
            }

            public void put(AccMa_SoAnamnesimxSintomiImpl obj, Object value) {
                obj.setIdAnamma((Integer) value);
            }
        }
        ,
        Idsintomo {
            public Object get(AccMa_SoAnamnesimxSintomiImpl obj) {
                return obj.getIdsintomo();
            }

            public void put(AccMa_SoAnamnesimxSintomiImpl obj, Object value) {
                obj.setIdsintomo((Integer) value);
            }
        }
        ,
        Ulss {
            public Object get(AccMa_SoAnamnesimxSintomiImpl obj) {
                return obj.getUlss();
            }

            public void put(AccMa_SoAnamnesimxSintomiImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(AccMa_SoAnamnesimxSintomiImpl obj) {
                return obj.getTpscr();
            }

            public void put(AccMa_SoAnamnesimxSintomiImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        DxIdsede {
            public Object get(AccMa_SoAnamnesimxSintomiImpl obj) {
                return obj.getDxIdsede();
            }

            public void put(AccMa_SoAnamnesimxSintomiImpl obj, Object value) {
                obj.setDxIdsede((Integer) value);
            }
        }
        ,
        SxIdsede {
            public Object get(AccMa_SoAnamnesimxSintomiImpl obj) {
                return obj.getSxIdsede();
            }

            public void put(AccMa_SoAnamnesimxSintomiImpl obj, Object value) {
                obj.setSxIdsede((Integer) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(AccMa_SoAnamnesimxSintomiImpl object);

        public abstract void put(AccMa_SoAnamnesimxSintomiImpl object, Object value);

        public int index() {
            return AccMa_SoAnamnesimxSintomiImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AccMa_SoAnamnesimxSintomiImpl.AttributesEnum.firstIndex() + AccMa_SoAnamnesimxSintomiImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AccMa_SoAnamnesimxSintomiImpl.AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int IDANAMMXSINT = AttributesEnum.IdAnammxSint.index();
    public static final int IDANAMMA = AttributesEnum.IdAnamma.index();
    public static final int IDSINTOMO = AttributesEnum.Idsintomo.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int DXIDSEDE = AttributesEnum.DxIdsede.index();
    public static final int SXIDSEDE = AttributesEnum.SxIdsede.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public AccMa_SoAnamnesimxSintomiImpl()
  {
  }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.accettazione.AccMa_SoAnamnesimxSintomi");
    }


    /**
     *
     *  Gets the attribute value for IdAnammxSint, using the alias name IdAnammxSint
     */
    public Integer getIdAnammxSint()
  {
        return (Integer) getAttributeInternal(IDANAMMXSINT);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for IdAnammxSint
   */
  public void setIdAnammxSint(Integer value)
  {
    setAttributeInternal(IDANAMMXSINT, value);
  }

  /**
     *
     *  Gets the attribute value for IdAnamma, using the alias name IdAnamma
     */
    public Integer getIdAnamma()
  {
        return (Integer) getAttributeInternal(IDANAMMA);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for IdAnamma
   */
  public void setIdAnamma(Integer value)
  {
    setAttributeInternal(IDANAMMA, value);
  }

  /**
     *
     *  Gets the attribute value for Idsintomo, using the alias name Idsintomo
     */
    public Integer getIdsintomo()
  {
        return (Integer) getAttributeInternal(IDSINTOMO);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idsintomo
   */
  public void setIdsintomo(Integer value)
  {
    setAttributeInternal(IDSINTOMO, value);
  }

  /**
   * 
   *  Gets the attribute value for Ulss, using the alias name Ulss
   */
  public String getUlss()
  {
    return (String)getAttributeInternal(ULSS);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Ulss
   */
  public void setUlss(String value)
  {
    setAttributeInternal(ULSS, value);
  }

  /**
   * 
   *  Gets the attribute value for Tpscr, using the alias name Tpscr
   */
  public String getTpscr()
  {
    return (String)getAttributeInternal(TPSCR);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Tpscr
   */
  public void setTpscr(String value)
  {
    setAttributeInternal(TPSCR, value);
  }

  /**
     *
     *  Gets the attribute value for DxIdsede, using the alias name DxIdsede
     */
    public Integer getDxIdsede()
  {
        return (Integer) getAttributeInternal(DXIDSEDE);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for DxIdsede
   */
  public void setDxIdsede(Integer value)
  {
    setAttributeInternal(DXIDSEDE, value);
  }

  /**
     *
     *  Gets the attribute value for SxIdsede, using the alias name SxIdsede
     */
    public Integer getSxIdsede()
  {
        return (Integer) getAttributeInternal(SXIDSEDE);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for SxIdsede
   */
  public void setSxIdsede(Integer value)
  {
    setAttributeInternal(SXIDSEDE, value);
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
     * @param idAnammxSint key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idAnammxSint) {
        return new Key(new Object[] { idAnammxSint });
    }


}
