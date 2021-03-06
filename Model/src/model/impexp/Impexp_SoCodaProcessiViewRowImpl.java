package model.impexp;

import model.sched.Sched_SoCodaProcessiImpl;

import oracle.jbo.domain.DBSequence;
import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Feb 07 15:30:46 CET 2013
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Impexp_SoCodaProcessiViewRowImpl extends ViewRowImpl {
	public static final int ENTITY_IMPEXP_SOCODAPROCESSI = 0;

	/**
	 * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
	 */
	public enum AttributesEnum {
		Pid {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getPid();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setPid((Integer)value);
			}
		}
		,
		TipoProcesso {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getTipoProcesso();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setTipoProcesso((String)value);
			}
		}
		,
		Stato {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getStato();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setStato((String)value);
			}
		}
		,
		Priorita {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getPriorita();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setPriorita((Integer)value);
			}
		}
		,
		Ulss {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getUlss();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setUlss((String)value);
			}
		}
		,
		Tpscr {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getTpscr();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setTpscr((String)value);
			}
		}
		,
		ErrorMessage {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getErrorMessage();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setErrorMessage((String)value);
			}
		}
		,
		DataSchedulazione {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getDataSchedulazione();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setDataSchedulazione((Date)value);
			}
		}
		,
		UtenteSchedulazione {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getUtenteSchedulazione();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setUtenteSchedulazione((String)value);
			}
		}
		,
		InizioEsecuzione {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getInizioEsecuzione();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setInizioEsecuzione((Date)value);
			}
		}
		,
		FineEsecuzione {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getFineEsecuzione();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setFineEsecuzione((Date)value);
			}
		}
		,
		LinkLog {
			public Object get(Impexp_SoCodaProcessiViewRowImpl obj) {
				return obj.getLinkLog();
			}

			public void put(Impexp_SoCodaProcessiViewRowImpl obj, Object value) {
				obj.setLinkLog((String)value);
			}
		}
		;
		private static AttributesEnum[] vals = null;
		private static int firstIndex = 0;

		public abstract Object get(Impexp_SoCodaProcessiViewRowImpl object);

		public abstract void put(Impexp_SoCodaProcessiViewRowImpl object, Object value);

		public int index() {
			return Impexp_SoCodaProcessiViewRowImpl.AttributesEnum.firstIndex() + ordinal();
		}

		public static int firstIndex() {
			return firstIndex;
		}

		public static int count() {
			return Impexp_SoCodaProcessiViewRowImpl.AttributesEnum.firstIndex() + Impexp_SoCodaProcessiViewRowImpl.AttributesEnum.staticValues().length;
		}

		public static AttributesEnum[] staticValues() {
			if (vals == null) {
				vals = Impexp_SoCodaProcessiViewRowImpl.AttributesEnum.values();
			}
			return vals;
		}
	}
	public static final int PID = AttributesEnum.Pid.index();
	public static final int TIPOPROCESSO = AttributesEnum.TipoProcesso.index();
	public static final int STATO = AttributesEnum.Stato.index();
	public static final int PRIORITA = AttributesEnum.Priorita.index();
	public static final int ULSS = AttributesEnum.Ulss.index();
	public static final int TPSCR = AttributesEnum.Tpscr.index();
	public static final int ERRORMESSAGE = AttributesEnum.ErrorMessage.index();
	public static final int DATASCHEDULAZIONE = AttributesEnum.DataSchedulazione.index();
	public static final int UTENTESCHEDULAZIONE = AttributesEnum.UtenteSchedulazione.index();
	public static final int INIZIOESECUZIONE = AttributesEnum.InizioEsecuzione.index();
	public static final int FINEESECUZIONE = AttributesEnum.FineEsecuzione.index();
	public static final int LINKLOG = AttributesEnum.LinkLog.index();

	/**
	 * This is the default constructor (do not remove).
	 */
	public Impexp_SoCodaProcessiViewRowImpl() {
	}

	/**
	 * Gets Impexp_SoCodaProcessi entity object.
	 * @return the Impexp_SoCodaProcessi
	 */
	public Sched_SoCodaProcessiImpl getImpexp_SoCodaProcessi() {
		return (Sched_SoCodaProcessiImpl)getEntity(ENTITY_IMPEXP_SOCODAPROCESSI);
	}

	/**
	 * Gets the attribute value for PID using the alias name Pid.
	 * @return the PID
	 */
	public Integer getPid() {
		return (Integer)getAttributeInternal(PID);
	}

	/**
	 * Sets <code>value</code> as attribute value for PID using the alias name Pid.
	 * @param value value to set the PID
	 */
	public void setPid(Integer value) {
		setAttributeInternal(PID, value);
	}

	/**
	 * Gets the attribute value for TipoProcesso using the alias name TipoProcesso.
	 * @return the TipoProcesso
	 */
	public String getTipoProcesso() {
		return (String) getAttributeInternal(TIPOPROCESSO);
	}

	/**
	 * Sets <code>value</code> as attribute value for TipoProcesso using the alias name TipoProcesso.
	 * @param value value to set the TipoProcesso
	 */
	public void setTipoProcesso(String value) {
		setAttributeInternal(TIPOPROCESSO, value);
	}

	/**
	 * Gets the attribute value for STATO using the alias name Stato.
	 * @return the STATO
	 */
	public String getStato() {
		return (String) getAttributeInternal(STATO);
	}

	/**
	 * Sets <code>value</code> as attribute value for STATO using the alias name Stato.
	 * @param value value to set the STATO
	 */
	public void setStato(String value) {
		setAttributeInternal(STATO, value);
	}

	/**
	 * Gets the attribute value for PRIORITA using the alias name Priorita.
	 * @return the PRIORITA
	 */
	public Integer getPriorita() {
		return (Integer) getAttributeInternal(PRIORITA);
	}

	/**
	 * Sets <code>value</code> as attribute value for PRIORITA using the alias name Priorita.
	 * @param value value to set the PRIORITA
	 */
	public void setPriorita(Integer value) {
		setAttributeInternal(PRIORITA, value);
	}

	/**
	 * Gets the attribute value for ULSS using the alias name Ulss.
	 * @return the ULSS
	 */
	public String getUlss() {
		return (String) getAttributeInternal(ULSS);
	}

	/**
	 * Sets <code>value</code> as attribute value for ULSS using the alias name Ulss.
	 * @param value value to set the ULSS
	 */
	public void setUlss(String value) {
		setAttributeInternal(ULSS, value);
	}

	/**
	 * Gets the attribute value for TPSCR using the alias name Tpscr.
	 * @return the TPSCR
	 */
	public String getTpscr() {
		return (String) getAttributeInternal(TPSCR);
	}

	/**
	 * Sets <code>value</code> as attribute value for TPSCR using the alias name Tpscr.
	 * @param value value to set the TPSCR
	 */
	public void setTpscr(String value) {
		setAttributeInternal(TPSCR, value);
	}

	/**
	 * Gets the attribute value for ERROR_MESSAGE using the alias name ErrorMessage.
	 * @return the ERROR_MESSAGE
	 */
	public String getErrorMessage() {
		return (String) getAttributeInternal(ERRORMESSAGE);
	}

	/**
	 * Sets <code>value</code> as attribute value for ERROR_MESSAGE using the alias name ErrorMessage.
	 * @param value value to set the ERROR_MESSAGE
	 */
	public void setErrorMessage(String value) {
		setAttributeInternal(ERRORMESSAGE, value);
	}

	/**
	 * Gets the attribute value for DATA_SCHEDULAZIONE using the alias name DataSchedulazione.
	 * @return the DATA_SCHEDULAZIONE
	 */
	public Date getDataSchedulazione() {
		return (Date) getAttributeInternal(DATASCHEDULAZIONE);
	}

	/**
	 * Sets <code>value</code> as attribute value for DATA_SCHEDULAZIONE using the alias name DataSchedulazione.
	 * @param value value to set the DATA_SCHEDULAZIONE
	 */
	public void setDataSchedulazione(Date value) {
		setAttributeInternal(DATASCHEDULAZIONE, value);
	}

	/**
	 * Gets the attribute value for UTENTE_SCHEDULAZIONE using the alias name UtenteSchedulazione.
	 * @return the UTENTE_SCHEDULAZIONE
	 */
	public String getUtenteSchedulazione() {
		return (String) getAttributeInternal(UTENTESCHEDULAZIONE);
	}

	/**
	 * Sets <code>value</code> as attribute value for UTENTE_SCHEDULAZIONE using the alias name UtenteSchedulazione.
	 * @param value value to set the UTENTE_SCHEDULAZIONE
	 */
	public void setUtenteSchedulazione(String value) {
		setAttributeInternal(UTENTESCHEDULAZIONE, value);
	}

	/**
	 * Gets the attribute value for INIZIO_ESECUZIONE using the alias name InizioEsecuzione.
	 * @return the INIZIO_ESECUZIONE
	 */
	public Date getInizioEsecuzione() {
		return (Date) getAttributeInternal(INIZIOESECUZIONE);
	}

	/**
	 * Sets <code>value</code> as attribute value for INIZIO_ESECUZIONE using the alias name InizioEsecuzione.
	 * @param value value to set the INIZIO_ESECUZIONE
	 */
	public void setInizioEsecuzione(Date value) {
		setAttributeInternal(INIZIOESECUZIONE, value);
	}

	/**
	 * Gets the attribute value for FINE_ESECUZIONE using the alias name FineEsecuzione.
	 * @return the FINE_ESECUZIONE
	 */
	public Date getFineEsecuzione() {
		return (Date) getAttributeInternal(FINEESECUZIONE);
	}

	/**
	 * Sets <code>value</code> as attribute value for FINE_ESECUZIONE using the alias name FineEsecuzione.
	 * @param value value to set the FINE_ESECUZIONE
	 */
	public void setFineEsecuzione(Date value) {
		setAttributeInternal(FINEESECUZIONE, value);
	}

	/**
	 * Gets the attribute value for LINK_LOG using the alias name LinkLog.
	 * @return the LINK_LOG
	 */
	public String getLinkLog() {
		return (String) getAttributeInternal(LINKLOG);
	}

	/**
	 * Sets <code>value</code> as attribute value for LINK_LOG using the alias name LinkLog.
	 * @param value value to set the LINK_LOG
	 */
	public void setLinkLog(String value) {
		setAttributeInternal(LINKLOG, value);
	}

	/**
	 * getAttrInvokeAccessor: generated method. Do not modify.
	 * @param index the index identifying the attribute
	 * @param attrDef the attribute

	 * @return the attribute value
	 * @throws Exception
	 */
	protected Object getAttrInvokeAccessor(int index, AttributeDefImpl attrDef) throws Exception {
		if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
			return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
		}
		return super.getAttrInvokeAccessor(index, attrDef);
	}

	/**
	 * setAttrInvokeAccessor: generated method. Do not modify.
	 * @param index the index identifying the attribute
	 * @param value the value to assign to the attribute
	 * @param attrDef the attribute

	 * @throws Exception
	 */
	protected void setAttrInvokeAccessor(int index, Object value, AttributeDefImpl attrDef) throws Exception {
		if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
			AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
			return;
		}
		super.setAttrInvokeAccessor(index, value, attrDef);
	}
}
