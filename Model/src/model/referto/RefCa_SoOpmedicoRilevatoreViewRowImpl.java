package model.referto;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class RefCa_SoOpmedicoRilevatoreViewRowImpl extends ViewRowImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    protected enum AttributesEnum {
        Idop,
        Codop,
        Idcentro,
        Cognome,
        Nome,
        Titolo,
        Ulss,
        Tpscr,
        Cf,
        Dtfinevalopmedico;
        private static AttributesEnum[] vals = null;
        private static final int        firstIndex = 0;

        protected int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        protected static final int firstIndex() {
            return firstIndex;
        }

        protected static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        protected static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
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
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int CF = AttributesEnum.Cf.index();
    public static final int DTFINEVALOPMEDICO = AttributesEnum.Dtfinevalopmedico.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
	public RefCa_SoOpmedicoRilevatoreViewRowImpl() {
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Idop
	 */
	public Integer getIdop() {
		return (Integer)getAttributeInternal(IDOP);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Idop
	 */
	public void setIdop(Integer value) {
		setAttributeInternal(IDOP, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Codop
	 */
	public Integer getCodop() {
		return (Integer)getAttributeInternal(CODOP);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Codop
	 */
	public void setCodop(Integer value) {
		setAttributeInternal(CODOP, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Idcentro
	 */
	public Integer getIdcentro() {
		return (Integer)getAttributeInternal(IDCENTRO);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Idcentro
	 */
	public void setIdcentro(Integer value) {
		setAttributeInternal(IDCENTRO, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Cognome
	 */
	public String getCognome() {
		return (String)getAttributeInternal(COGNOME);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Cognome
	 */
	public void setCognome(String value) {
		setAttributeInternal(COGNOME, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Nome
	 */
	public String getNome() {
		return (String)getAttributeInternal(NOME);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Nome
	 */
	public void setNome(String value) {
		setAttributeInternal(NOME, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Titolo
	 */
	public String getTitolo() {
		return (String)getAttributeInternal(TITOLO);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Titolo
	 */
	public void setTitolo(String value) {
		setAttributeInternal(TITOLO, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Ulss
	 */
	public String getUlss() {
		return (String)getAttributeInternal(ULSS);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Ulss
	 */
	public void setUlss(String value) {
		setAttributeInternal(ULSS, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Tpscr
	 */
	public String getTpscr() {
		return (String)getAttributeInternal(TPSCR);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Tpscr
	 */
	public void setTpscr(String value) {
		setAttributeInternal(TPSCR, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Cf
	 */
	public String getCf() {
		return (String)getAttributeInternal(CF);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Cf
	 */
	public void setCf(String value) {
		setAttributeInternal(CF, value);
	}

	/**
	 * 
	 *  Gets the attribute value for the calculated attribute Dtfinevalopmedico
	 */
	public Date getDtfinevalopmedico() {
		return (Date)getAttributeInternal(DTFINEVALOPMEDICO);
	}

	/**
	 * 
	 *  Sets <code>value</code> as the attribute value for the calculated attribute Dtfinevalopmedico
	 */
	public void setDtfinevalopmedico(Date value) {
		setAttributeInternal(DTFINEVALOPMEDICO, value);
	}


}
