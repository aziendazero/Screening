package model.global;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
import oracle.jbo.RowIterator;
import oracle.jbo.Key;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoOpmedicoImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idop {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getIdop();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Codop {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getCodop();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setCodop((Integer)value);
            }
        }
        ,
        Idcentro {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getIdcentro();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setIdcentro((Integer)value);
            }
        }
        ,
        Cognome {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getCognome();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setCognome((String)value);
            }
        }
        ,
        Nome {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getNome();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setNome((String)value);
            }
        }
        ,
        Titolo {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getTitolo();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setTitolo((String)value);
            }
        }
        ,
        Dtfinevalopmedico {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getDtfinevalopmedico();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setDtfinevalopmedico((Date)value);
            }
        }
        ,
        Ulss {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getUlss();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getTpscr();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Acc_SoAnamnesiCito {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getAcc_SoAnamnesiCito();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        AccCo_SoAnamnesicolon {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getAccCo_SoAnamnesicolon();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        AccMa_SoAnamnesimx {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getAccMa_SoAnamnesimx();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoInterventomammo {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoInterventomammo();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoInterventomammo1 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoInterventomammo1();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoInterventomammo2 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoInterventomammo2();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoInterventomammo3 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoInterventomammo3();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo1liv {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo1liv();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo1liv1 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo1liv1();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv1 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv1();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv2 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv2();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv3 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv3();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv4 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv4();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv5 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv5();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv6 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv6();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv7 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv7();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv8 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv8();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv9 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv9();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv10 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv10();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv11 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv11();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo2liv12 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo2liv12();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoRefertomammo1liv2 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoRefertomammo1liv2();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoEndoscopia {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoEndoscopia();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoInterventocolon {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoInterventocolon();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoInvito {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoInvito();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoInterventocito {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoInterventocito();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Cnf_SoCnfUtentiOperatori {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getCnf_SoCnfUtentiOperatori();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Ref_SoInterventocito1 {
            public Object get(A_SoOpmedicoImpl obj) {
                return obj.getRef_SoInterventocito1();
            }

            public void put(A_SoOpmedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(A_SoOpmedicoImpl object);

        public abstract void put(A_SoOpmedicoImpl object, Object value);

        public int index() {
            return A_SoOpmedicoImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return A_SoOpmedicoImpl.AttributesEnum.firstIndex() + A_SoOpmedicoImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = A_SoOpmedicoImpl.AttributesEnum.values();
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
    public static final int ACC_SOANAMNESICITO = AttributesEnum.Acc_SoAnamnesiCito.index();
    public static final int ACCCO_SOANAMNESICOLON = AttributesEnum.AccCo_SoAnamnesicolon.index();
    public static final int ACCMA_SOANAMNESIMX = AttributesEnum.AccMa_SoAnamnesimx.index();
    public static final int REF_SOINTERVENTOMAMMO = AttributesEnum.Ref_SoInterventomammo.index();
    public static final int REF_SOINTERVENTOMAMMO1 = AttributesEnum.Ref_SoInterventomammo1.index();
    public static final int REF_SOINTERVENTOMAMMO2 = AttributesEnum.Ref_SoInterventomammo2.index();
    public static final int REF_SOINTERVENTOMAMMO3 = AttributesEnum.Ref_SoInterventomammo3.index();
    public static final int REF_SOREFERTOMAMMO1LIV = AttributesEnum.Ref_SoRefertomammo1liv.index();
    public static final int REF_SOREFERTOMAMMO1LIV1 = AttributesEnum.Ref_SoRefertomammo1liv1.index();
    public static final int REF_SOREFERTOMAMMO2LIV = AttributesEnum.Ref_SoRefertomammo2liv.index();
    public static final int REF_SOREFERTOMAMMO2LIV1 = AttributesEnum.Ref_SoRefertomammo2liv1.index();
    public static final int REF_SOREFERTOMAMMO2LIV2 = AttributesEnum.Ref_SoRefertomammo2liv2.index();
    public static final int REF_SOREFERTOMAMMO2LIV3 = AttributesEnum.Ref_SoRefertomammo2liv3.index();
    public static final int REF_SOREFERTOMAMMO2LIV4 = AttributesEnum.Ref_SoRefertomammo2liv4.index();
    public static final int REF_SOREFERTOMAMMO2LIV5 = AttributesEnum.Ref_SoRefertomammo2liv5.index();
    public static final int REF_SOREFERTOMAMMO2LIV6 = AttributesEnum.Ref_SoRefertomammo2liv6.index();
    public static final int REF_SOREFERTOMAMMO2LIV7 = AttributesEnum.Ref_SoRefertomammo2liv7.index();
    public static final int REF_SOREFERTOMAMMO2LIV8 = AttributesEnum.Ref_SoRefertomammo2liv8.index();
    public static final int REF_SOREFERTOMAMMO2LIV9 = AttributesEnum.Ref_SoRefertomammo2liv9.index();
    public static final int REF_SOREFERTOMAMMO2LIV10 = AttributesEnum.Ref_SoRefertomammo2liv10.index();
    public static final int REF_SOREFERTOMAMMO2LIV11 = AttributesEnum.Ref_SoRefertomammo2liv11.index();
    public static final int REF_SOREFERTOMAMMO2LIV12 = AttributesEnum.Ref_SoRefertomammo2liv12.index();
    public static final int REF_SOREFERTOMAMMO1LIV2 = AttributesEnum.Ref_SoRefertomammo1liv2.index();
    public static final int REF_SOENDOSCOPIA = AttributesEnum.Ref_SoEndoscopia.index();
    public static final int REF_SOINTERVENTOCOLON = AttributesEnum.Ref_SoInterventocolon.index();
    public static final int REF_SOINVITO = AttributesEnum.Ref_SoInvito.index();
    public static final int REF_SOINTERVENTOCITO = AttributesEnum.Ref_SoInterventocito.index();
    public static final int CNF_SOCNFUTENTIOPERATORI = AttributesEnum.Cnf_SoCnfUtentiOperatori.index();
    public static final int REF_SOINTERVENTOCITO1 = AttributesEnum.Ref_SoInterventocito1.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
	public A_SoOpmedicoImpl()
	{
	}


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.A_SoOpmedico");
    }

    /**
     *
     *  Gets the attribute value for Idop, using the alias name Idop
     */
    public Integer getIdop()
	{
		return (Integer)getAttributeInternal(IDOP);
	}

	/**
	 * 
	 *  Gets the attribute value for Codop, using the alias name Codop
	 */
	public Integer getCodop()
	{
		return (Integer)getAttributeInternal(CODOP);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for Codop
	 */
	public void setCodop(Integer value)
	{
		setAttributeInternal(CODOP, value);
	}

	/**
	 * 
	 *  Gets the attribute value for Idcentro, using the alias name Idcentro
	 */
	public Integer getIdcentro()
	{
		return (Integer)getAttributeInternal(IDCENTRO);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for Idcentro
	 */
	public void setIdcentro(Integer value)
	{
		setAttributeInternal(IDCENTRO, value);
	}

	/**
	 * 
	 *  Gets the attribute value for Cognome, using the alias name Cognome
	 */
	public String getCognome()
	{
		return (String)getAttributeInternal(COGNOME);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for Cognome
	 */
	public void setCognome(String value)
	{
		setAttributeInternal(COGNOME, value);
	}

	/**
	 * 
	 *  Gets the attribute value for Nome, using the alias name Nome
	 */
	public String getNome()
	{
		return (String)getAttributeInternal(NOME);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for Nome
	 */
	public void setNome(String value)
	{
		setAttributeInternal(NOME, value);
	}

	/**
	 * 
	 *  Gets the attribute value for Titolo, using the alias name Titolo
	 */
	public String getTitolo()
	{
		return (String)getAttributeInternal(TITOLO);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for Titolo
	 */
	public void setTitolo(String value)
	{
		setAttributeInternal(TITOLO, value);
	}

	/**
	 * 
	 *  Gets the attribute value for Dtfinevalopmedico, using the alias name Dtfinevalopmedico
	 */
	public Date getDtfinevalopmedico()
	{
		return (Date)getAttributeInternal(DTFINEVALOPMEDICO);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for Dtfinevalopmedico
	 */
	public void setDtfinevalopmedico(Date value)
	{
		setAttributeInternal(DTFINEVALOPMEDICO, value);
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
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getCnf_SoCnfUtentiOperatori()
	{
        return (RowIterator)getAttributeInternal(CNF_SOCNFUTENTIOPERATORI);
    }

    /**
     * @param idop key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idop) {
        return new Key(new Object[]{idop});
    }

    /**
     *
     *  Gets the associated entity oracle.jbo.RowIterator
     */
    public RowIterator getRef_SoInvito()
	{
        return (RowIterator)getAttributeInternal(REF_SOINVITO);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getAccMa_SoAnamnesimx()
	{
        return (RowIterator)getAttributeInternal(ACCMA_SOANAMNESIMX);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv1()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV1);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv2()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV2);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv3()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV3);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv4()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV4);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv5()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV5);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv6()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV6);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv7()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV7);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv8()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV8);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv9()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV9);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv10()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV10);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv11()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV11);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo2liv12()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO2LIV12);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoInterventocito()
	{
        return (RowIterator)getAttributeInternal(REF_SOINTERVENTOCITO);
    }

	/**
	 * 
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
	public RowIterator getRef_SoInterventocito1()
	{
		return (RowIterator)getAttributeInternal(REF_SOINTERVENTOCITO1);
	}

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getAcc_SoAnamnesiCito()
	{
        return (RowIterator)getAttributeInternal(ACC_SOANAMNESICITO);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getAccCo_SoAnamnesicolon()
	{
        return (RowIterator)getAttributeInternal(ACCCO_SOANAMNESICOLON);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoInterventomammo()
	{
        return (RowIterator)getAttributeInternal(REF_SOINTERVENTOMAMMO);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoInterventomammo1()
	{
        return (RowIterator)getAttributeInternal(REF_SOINTERVENTOMAMMO1);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoInterventomammo2()
	{
        return (RowIterator)getAttributeInternal(REF_SOINTERVENTOMAMMO2);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoInterventomammo3()
	{
        return (RowIterator)getAttributeInternal(REF_SOINTERVENTOMAMMO3);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo1liv()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO1LIV);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo1liv1()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO1LIV1);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoRefertomammo1liv2()
	{
        return (RowIterator)getAttributeInternal(REF_SOREFERTOMAMMO1LIV2);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoInterventocolon()
	{
        return (RowIterator)getAttributeInternal(REF_SOINTERVENTOCOLON);
    }

	/**
	 *
	 *  Gets the associated entity oracle.jbo.RowIterator
	 */
    public RowIterator getRef_SoEndoscopia()
	{
        return (RowIterator)getAttributeInternal(REF_SOENDOSCOPIA);
    }


}
