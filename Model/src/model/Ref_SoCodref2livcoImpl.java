package model;

import oracle.jbo.Key;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.EntityImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Ref_SoCodref2livcoImpl extends EntityImpl 
{
  public static final int ID = 0;
  public static final int IDENDO = 1;
  public static final int ULSS = 2;
  public static final int TPSCR = 3;
  public static final int IDCFREF2L = 4;
  public static final int GRUPPO = 5;






  private static EntityDefImpl mDefinitionObject;

  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Ref_SoCodref2livcoImpl()
  {
  }

  /**
   * 
   *  Retrieves the definition object for this instance class.
   */
  public static synchronized EntityDefImpl getDefinitionObject()
  {
    if (mDefinitionObject == null)
    {
      mDefinitionObject = (EntityDefImpl)EntityDefImpl.findDefObject("model.Ref_SoCodref2livco");
    }
    return mDefinitionObject;
  }







  /**
   * 
   *  Gets the attribute value for Id, using the alias name Id
   */
  public Integer getId()
  {
    return (Integer)getAttributeInternal(ID);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Id
   */
  public void setId(Integer value)
  {
    setAttributeInternal(ID, value);
  }

  /**
   * 
   *  Gets the attribute value for Idendo, using the alias name Idendo
   */
  public Integer getIdendo()
  {
    return (Integer)getAttributeInternal(IDENDO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idendo
   */
  public void setIdendo(Integer value)
  {
    setAttributeInternal(IDENDO, value);
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
   *  Gets the attribute value for Idcfref2l, using the alias name Idcfref2l
   */
  public Integer getIdcfref2l()
  {
    return (Integer)getAttributeInternal(IDCFREF2L);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idcfref2l
   */
  public void setIdcfref2l(Integer value)
  {
    setAttributeInternal(IDCFREF2L, value);
  }

  /**
   * 
   *  Gets the attribute value for Gruppo, using the alias name Gruppo
   */
  public String getGruppo()
  {
    return (String)getAttributeInternal(GRUPPO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Gruppo
   */
  public void setGruppo(String value)
  {
    setAttributeInternal(GRUPPO, value);
  }

  /**
   * 
   *  getAttrInvokeAccessor: generated method. Do not modify.
   */
  protected Object getAttrInvokeAccessor(int index, AttributeDefImpl attrDef) throws Exception
  {
    switch (index)
      {
      case ID:
        return getId();
      case IDENDO:
        return getIdendo();
      case ULSS:
        return getUlss();
      case TPSCR:
        return getTpscr();
      case IDCFREF2L:
        return getIdcfref2l();
      case GRUPPO:
        return getGruppo();
      default:
        return super.getAttrInvokeAccessor(index, attrDef);
      }
  }

  /**
   * 
   *  setAttrInvokeAccessor: generated method. Do not modify.
   */
  protected void setAttrInvokeAccessor(int index, Object value, AttributeDefImpl attrDef) throws Exception
  {
    switch (index)
      {
      case ID:
        setId((Integer)value);
        return;
      case IDENDO:
        setIdendo((Integer)value);
        return;
      case ULSS:
        setUlss((String)value);
        return;
      case TPSCR:
        setTpscr((String)value);
        return;
      case IDCFREF2L:
        setIdcfref2l((Integer)value);
        return;
      case GRUPPO:
        setGruppo((String)value);
        return;
      default:
        super.setAttrInvokeAccessor(index, value, attrDef);
        return;
      }
  }

  /**
   * 
   *  Creates a Key object based on given key constituents
   */
  public static Key createPrimaryKey(Integer id)
  {
    return new Key(new Object[] {id});
  }












}