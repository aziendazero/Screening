package model.referto;

import oracle.jbo.RowIterator;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Fri Nov 15 16:36:22 CET 2013
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class Ref_SoSoggScrViewRowImpl extends ViewRowImpl {


    public static final int ENTITY_REF_SOSOGGSCR = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
	public enum AttributesEnum {
        Codts {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getCodts();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setCodts((String) value);
            }
        }
        ,
        Ulss {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Tpscr {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setTpscr((String) value);
            }
        }
        ,
        Roundindiv {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getRoundindiv();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setRoundindiv((Integer) value);
            }
        }
        ,
        Numanatomia {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getNumanatomia();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setNumanatomia((Integer) value);
            }
        }
        ,
        Numradiologia {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getNumradiologia();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setNumradiologia((Integer) value);
            }
        }
        ,
        Numlab {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getNumlab();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setNumlab((Integer) value);
            }
        }
        ,
        Roundinviti {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getRoundinviti();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setRoundinviti((Integer) value);
            }
        }
        ,
        Mx45Braccio {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getMx45Braccio();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setMx45Braccio((String) value);
            }
        }
        ,
        Mx45Codice {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getMx45Codice();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setMx45Codice((String) value);
            }
        }
        ,
        IdbraccioTrial {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getIdbraccioTrial();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setIdbraccioTrial((Integer) value);
            }
        }
        ,
        IdstatoTrial {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getIdstatoTrial();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setIdstatoTrial((Integer) value);
            }
        }
        ,
        Idtrial {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getIdtrial();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setIdtrial((Integer) value);
            }
        }
        ,
        ConsensoCond {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getConsensoCond();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setConsensoCond((Integer) value);
            }
        }
        ,
        Consenso {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getConsenso();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setConsenso((Integer) value);
            }
        }
        ,
        ConsensoDesc {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getConsensoDesc();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        ConsensoCondDesc {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getConsensoCondDesc();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Cnf_SoCnfTrialBraccioView {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getCnf_SoCnfTrialBraccioView();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ,
        Cnf_SoCnfTrialStatoView {
            public Object get(Ref_SoSoggScrViewRowImpl obj) {
                return obj.getCnf_SoCnfTrialStatoView();
            }

            public void put(Ref_SoSoggScrViewRowImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        static AttributesEnum[] vals = null;
        ;
        private static int firstIndex = 0;

		public abstract Object get(Ref_SoSoggScrViewRowImpl object);

		public abstract void put(Ref_SoSoggScrViewRowImpl object, Object value);

		public int index() {
			return AttributesEnum.firstIndex() + ordinal();
		}

		public static int firstIndex() {
			return firstIndex;
		}

		public static int count() {
			return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
		}

		public static AttributesEnum[] staticValues() {
			if (vals == null) {
				vals = AttributesEnum.values();
			}
			return vals;
		}
	}


    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int ROUNDINDIV = AttributesEnum.Roundindiv.index();
    public static final int NUMANATOMIA = AttributesEnum.Numanatomia.index();
    public static final int NUMRADIOLOGIA = AttributesEnum.Numradiologia.index();
    public static final int NUMLAB = AttributesEnum.Numlab.index();
    public static final int ROUNDINVITI = AttributesEnum.Roundinviti.index();
    public static final int MX45BRACCIO = AttributesEnum.Mx45Braccio.index();
    public static final int MX45CODICE = AttributesEnum.Mx45Codice.index();
    public static final int IDBRACCIOTRIAL = AttributesEnum.IdbraccioTrial.index();
    public static final int IDSTATOTRIAL = AttributesEnum.IdstatoTrial.index();
    public static final int IDTRIAL = AttributesEnum.Idtrial.index();
    public static final int CONSENSOCOND = AttributesEnum.ConsensoCond.index();
    public static final int CONSENSO = AttributesEnum.Consenso.index();
    public static final int CONSENSODESC = AttributesEnum.ConsensoDesc.index();
    public static final int CONSENSOCONDDESC = AttributesEnum.ConsensoCondDesc.index();
    public static final int CNF_SOCNFTRIALBRACCIOVIEW = AttributesEnum.Cnf_SoCnfTrialBraccioView.index();
    public static final int CNF_SOCNFTRIALSTATOVIEW = AttributesEnum.Cnf_SoCnfTrialStatoView.index();

    /**
     * This is the default constructor (do not remove).
     */
	public Ref_SoSoggScrViewRowImpl() {
	}

	/**
	 * Gets Ref_SoSoggScr entity object.
	 * @return the Ref_SoSoggScr
	 */
	public Ref_SoSoggScrImpl getRef_SoSoggScr() {
		return (Ref_SoSoggScrImpl)getEntity(ENTITY_REF_SOSOGGSCR);
	}

	/**
	 * Gets the attribute value for CODTS using the alias name Codts.
	 * @return the CODTS
	 */
	public String getCodts() {
		return (String) getAttributeInternal(CODTS);
	}

	/**
	 * Sets <code>value</code> as attribute value for CODTS using the alias name Codts.
	 * @param value value to set the CODTS
	 */
	public void setCodts(String value) {
		setAttributeInternal(CODTS, value);
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
	 * Gets the attribute value for ROUNDINDIV using the alias name Roundindiv.
	 * @return the ROUNDINDIV
	 */
	public Integer getRoundindiv() {
		return (Integer) getAttributeInternal(ROUNDINDIV);
	}

	/**
	 * Sets <code>value</code> as attribute value for ROUNDINDIV using the alias name Roundindiv.
	 * @param value value to set the ROUNDINDIV
	 */
	public void setRoundindiv(Integer value) {
		setAttributeInternal(ROUNDINDIV, value);
	}

	/**
	 * Gets the attribute value for NUMANATOMIA using the alias name Numanatomia.
	 * @return the NUMANATOMIA
	 */
	public Integer getNumanatomia() {
		return (Integer) getAttributeInternal(NUMANATOMIA);
	}

	/**
	 * Sets <code>value</code> as attribute value for NUMANATOMIA using the alias name Numanatomia.
	 * @param value value to set the NUMANATOMIA
	 */
	public void setNumanatomia(Integer value) {
		setAttributeInternal(NUMANATOMIA, value);
	}

	/**
	 * Gets the attribute value for NUMRADIOLOGIA using the alias name Numradiologia.
	 * @return the NUMRADIOLOGIA
	 */
	public Integer getNumradiologia() {
		return (Integer) getAttributeInternal(NUMRADIOLOGIA);
	}

	/**
	 * Sets <code>value</code> as attribute value for NUMRADIOLOGIA using the alias name Numradiologia.
	 * @param value value to set the NUMRADIOLOGIA
	 */
	public void setNumradiologia(Integer value) {
		setAttributeInternal(NUMRADIOLOGIA, value);
	}

	/**
	 * Gets the attribute value for NUMLAB using the alias name Numlab.
	 * @return the NUMLAB
	 */
	public Integer getNumlab() {
		return (Integer) getAttributeInternal(NUMLAB);
	}

	/**
	 * Sets <code>value</code> as attribute value for NUMLAB using the alias name Numlab.
	 * @param value value to set the NUMLAB
	 */
	public void setNumlab(Integer value) {
		setAttributeInternal(NUMLAB, value);
	}

	/**
	 * Gets the attribute value for ROUNDINVITI using the alias name Roundinviti.
	 * @return the ROUNDINVITI
	 */
	public Integer getRoundinviti() {
		return (Integer) getAttributeInternal(ROUNDINVITI);
	}

	/**
	 * Sets <code>value</code> as attribute value for ROUNDINVITI using the alias name Roundinviti.
	 * @param value value to set the ROUNDINVITI
	 */
	public void setRoundinviti(Integer value) {
		setAttributeInternal(ROUNDINVITI, value);
	}

	/**
	 * Gets the attribute value for MX45_BRACCIO using the alias name Mx45Braccio.
	 * @return the MX45_BRACCIO
	 */
	public String getMx45Braccio() {
		return (String) getAttributeInternal(MX45BRACCIO);
	}

	/**
	 * Sets <code>value</code> as attribute value for MX45_BRACCIO using the alias name Mx45Braccio.
	 * @param value value to set the MX45_BRACCIO
	 */
	public void setMx45Braccio(String value) {
		setAttributeInternal(MX45BRACCIO, value);
	}

	/**
	 * Gets the attribute value for MX45_CODICE using the alias name Mx45Codice.
	 * @return the MX45_CODICE
	 */
	public String getMx45Codice() {
		return (String) getAttributeInternal(MX45CODICE);
	}

	/**
	 * Sets <code>value</code> as attribute value for MX45_CODICE using the alias name Mx45Codice.
	 * @param value value to set the MX45_CODICE
	 */
	public void setMx45Codice(String value) {
		setAttributeInternal(MX45CODICE, value);
	}

    /**
     * Gets the attribute value for IDBRACCIO_TRIAL using the alias name IdbraccioTrial.
     * @return the IDBRACCIO_TRIAL
     */
    public Integer getIdbraccioTrial() {
        return (Integer) getAttributeInternal(IDBRACCIOTRIAL);
    }

    /**
     * Sets <code>value</code> as attribute value for IDBRACCIO_TRIAL using the alias name IdbraccioTrial.
     * @param value value to set the IDBRACCIO_TRIAL
     */
    public void setIdbraccioTrial(Integer value) {
        setAttributeInternal(IDBRACCIOTRIAL, value);
    }

    /**
     * Gets the attribute value for IDSTATO_TRIAL using the alias name IdstatoTrial.
     * @return the IDSTATO_TRIAL
     */
    public Integer getIdstatoTrial() {
        return (Integer) getAttributeInternal(IDSTATOTRIAL);
    }

    /**
     * Sets <code>value</code> as attribute value for IDSTATO_TRIAL using the alias name IdstatoTrial.
     * @param value value to set the IDSTATO_TRIAL
     */
    public void setIdstatoTrial(Integer value) {
        setAttributeInternal(IDSTATOTRIAL, value);
    }

    /**
     * Gets the attribute value for IDTRIAL using the alias name Idtrial.
     * @return the IDTRIAL
     */
    public Integer getIdtrial() {
        return (Integer) getAttributeInternal(IDTRIAL);
    }

    /**
     * Sets <code>value</code> as attribute value for IDTRIAL using the alias name Idtrial.
     * @param value value to set the IDTRIAL
     */
    public void setIdtrial(Integer value) {
        setAttributeInternal(IDTRIAL, value);
    }

    /**
     * Gets the attribute value for CONSENSO_COND using the alias name ConsensoCond.
     * @return the CONSENSO_COND
     */
    public Integer getConsensoCond() {
        return (Integer) getAttributeInternal(CONSENSOCOND);
    }

    /**
     * Sets <code>value</code> as attribute value for CONSENSO_COND using the alias name ConsensoCond.
     * @param value value to set the CONSENSO_COND
     */
    public void setConsensoCond(Integer value) {
        setAttributeInternal(CONSENSOCOND, value);
    }

    /**
     * Gets the attribute value for CONSENSO using the alias name Consenso.
     * @return the CONSENSO
     */
    public Integer getConsenso() {
        return (Integer) getAttributeInternal(CONSENSO);
    }

    /**
     * Sets <code>value</code> as attribute value for CONSENSO using the alias name Consenso.
     * @param value value to set the CONSENSO
     */
    public void setConsenso(Integer value) {
        setAttributeInternal(CONSENSO, value);
    }


    /**
     * Gets the attribute value for the calculated attribute ConsensoDesc.
     * @return the ConsensoDesc
     */
    public String getConsensoDesc() {
        return (String) getAttributeInternal(CONSENSODESC);
    }

    /**
     * Gets the attribute value for the calculated attribute ConsensoCondDesc.
     * @return the ConsensoCondDesc
     */
    public String getConsensoCondDesc() {
        return (String) getAttributeInternal(CONSENSOCONDDESC);
    }

    /**
     * Gets the associated <code>RowIterator</code> using master-detail link Cnf_SoCnfTrialBraccioView.
     */
    public RowIterator getCnf_SoCnfTrialBraccioView() {
        return (RowIterator) getAttributeInternal(CNF_SOCNFTRIALBRACCIOVIEW);
    }

    /**
     * Gets the associated <code>RowIterator</code> using master-detail link Cnf_SoCnfTrialStatoView.
     */
    public RowIterator getCnf_SoCnfTrialStatoView() {
        return (RowIterator) getAttributeInternal(CNF_SOCNFTRIALSTATOVIEW);
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