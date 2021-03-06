package model.conf;

import model.global.A_SoOpmedicoImpl;

import oracle.adf.share.ADFContext;

import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
import oracle.jbo.Key;
import oracle.jbo.server.SequenceImpl;
import oracle.jbo.server.TransactionEvent;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoCnfUtentiOperatoriImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idassoc {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getIdassoc();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setIdassoc((Integer) value);
            }
        }
        ,
        Username {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getUsername();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setUsername((String) value);
            }
        }
        ,
        Operatore {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getOperatore();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setOperatore((Integer) value);
            }
        }
        ,
        DataIns {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getDataIns();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        DataMod {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getDataMod();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        OpIns {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getOpIns();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        OpMod {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getOpMod();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ulss {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getUlss();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getTpscr();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        A_SoOpmedico {
            public Object get(Cnf_SoCnfUtentiOperatoriImpl obj) {
                return obj.getA_SoOpmedico();
            }

            public void put(Cnf_SoCnfUtentiOperatoriImpl obj, Object value) {
                obj.setA_SoOpmedico((A_SoOpmedicoImpl) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoCnfUtentiOperatoriImpl object);

        public abstract void put(Cnf_SoCnfUtentiOperatoriImpl object, Object value);

        public int index() {
            return Cnf_SoCnfUtentiOperatoriImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoCnfUtentiOperatoriImpl.AttributesEnum.firstIndex() + Cnf_SoCnfUtentiOperatoriImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoCnfUtentiOperatoriImpl.AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int IDASSOC = AttributesEnum.Idassoc.index();
    public static final int USERNAME = AttributesEnum.Username.index();
    public static final int OPERATORE = AttributesEnum.Operatore.index();
    public static final int DATAINS = AttributesEnum.DataIns.index();
    public static final int DATAMOD = AttributesEnum.DataMod.index();
    public static final int OPINS = AttributesEnum.OpIns.index();
    public static final int OPMOD = AttributesEnum.OpMod.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int A_SOOPMEDICO = AttributesEnum.A_SoOpmedico.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoCnfUtentiOperatoriImpl()
  {
  }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.conf.Cnf_SoCnfUtentiOperatori");
    }


    @Override
    protected Object getHistoryContextForAttribute(AttributeDefImpl attr) {
            byte historyKind = attr.getHistoryKind();

            if ((historyKind == AttributeDefImpl.HISTORY_CREATE_USER) || (historyKind == AttributeDefImpl.HISTORY_MODIFY_USER)) {
                    return ADFContext.getCurrent().getSessionScope().get("user");
            }
            return super.getHistoryContextForAttribute(attr);
    }
    
    protected void doDML(int operation, TransactionEvent e)
      {
        if(operation==this.DML_INSERT)
        {//inserisco il codice da sequenza
          SequenceImpl s = new SequenceImpl("SO_CNF_UTENTI_OPERATORI_SEQ", getDBTransaction());     
          this.setIdassoc(s.getSequenceNumber().intValue()); 
        }
          
        super.doDML(operation, e);
      }


    /**
     *
     *  Gets the attribute value for Idassoc, using the alias name Idassoc
     */
    public Integer getIdassoc()
  {
        return (Integer) getAttributeInternal(IDASSOC);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idassoc
   */
  public void setIdassoc(Integer value)
  {
    setAttributeInternal(IDASSOC, value);
  }

  /**
   * 
   *  Gets the attribute value for Username, using the alias name Username
   */
  public String getUsername()
  {
    return (String)getAttributeInternal(USERNAME);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Username
   */
  public void setUsername(String value)
  {
    setAttributeInternal(USERNAME, value);
  }

  /**
     *
     *  Gets the attribute value for Operatore, using the alias name Operatore
     */
    public Integer getOperatore()
  {
        return (Integer) getAttributeInternal(OPERATORE);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Operatore
   */
  public void setOperatore(Integer value)
  {
    setAttributeInternal(OPERATORE, value);
  }

  /**
   * 
   *  Gets the attribute value for DataIns, using the alias name DataIns
   */
  public Date getDataIns()
  {
    return (Date)getAttributeInternal(DATAINS);
  }


    /**
     *
     *  Gets the attribute value for DataMod, using the alias name DataMod
     */
  public Date getDataMod()
  {
    return (Date)getAttributeInternal(DATAMOD);
  }


    /**
     *
     *  Gets the attribute value for OpIns, using the alias name OpIns
     */
  public String getOpIns()
  {
    return (String)getAttributeInternal(OPINS);
  }


    /**
     *
     *  Gets the attribute value for OpMod, using the alias name OpMod
     */
  public String getOpMod()
  {
    return (String)getAttributeInternal(OPMOD);
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
	 *  Gets the associated entity A_SoOpmedicoImpl
	 */
	public A_SoOpmedicoImpl getA_SoOpmedico()
	{
		return (A_SoOpmedicoImpl)getAttributeInternal(A_SOOPMEDICO);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the associated entity A_SoOpmedicoImpl
	 */
	public void setA_SoOpmedico(A_SoOpmedicoImpl value)
	{
		setAttributeInternal(A_SOOPMEDICO, value);
	}

    /**
     * @param idassoc key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idassoc) {
        return new Key(new Object[] { idassoc });
    }


}
