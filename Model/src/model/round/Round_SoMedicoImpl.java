package model.round;

import model.conf.Cnf_SoComuneImpl;

import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
import oracle.jbo.Key;
import oracle.jbo.RowIterator;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Round_SoMedicoImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codiceregmedico {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getCodiceregmedico();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setCodiceregmedico((Integer) value);
            }
        }
        ,
        Codcom {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getCodcom();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setCodcom((String) value);
            }
        }
        ,
        Cognome {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getCognome();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setCognome((String) value);
            }
        }
        ,
        Nome {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getNome();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setNome((String) value);
            }
        }
        ,
        IndirizzoRes {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getIndirizzoRes();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setIndirizzoRes((String) value);
            }
        }
        ,
        Tel {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getTel();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setTel((String) value);
            }
        }
        ,
        Cell {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getCell();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setCell((Integer) value);
            }
        }
        ,
        Dtadesione {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getDtadesione();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setDtadesione((Date) value);
            }
        }
        ,
        Dtfinevalop {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getDtfinevalop();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setDtfinevalop((Date) value);
            }
        }
        ,
        Ulss {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getUlss();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setUlss((String) value);
            }
        }
        ,
        Dtspedlettera {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getDtspedlettera();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setDtspedlettera((Date) value);
            }
        }
        ,
        DtadesCo {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getDtadesCo();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setDtadesCo((Date) value);
            }
        }
        ,
        DtadesMa {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getDtadesMa();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setDtadesMa((Date) value);
            }
        }
        ,
        Codicefiscale {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getCodicefiscale();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setCodicefiscale((String) value);
            }
        }
        ,
        Dtultmodifica {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getDtultmodifica();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setDtultmodifica((Date) value);
            }
        }
        ,
        Opultmodifica {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getOpultmodifica();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setOpultmodifica((String) value);
            }
        }
        ,
        DtadesCa {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getDtadesCa();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setDtadesCa((Date) value);
            }
        }
        ,
        ReleaseCodeCom {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getReleaseCodeCom();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setReleaseCodeCom((Integer) value);
            }
        }
        ,
        DtadesPf {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getDtadesPf();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setDtadesPf((Date) value);
            }
        }
        ,
        Cnf_SoComune {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getCnf_SoComune();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setCnf_SoComune((Cnf_SoComuneImpl) value);
            }
        }
        ,
        Round_SoSoggetto {
            public Object get(Round_SoMedicoImpl obj) {
                return obj.getRound_SoSoggetto();
            }

            public void put(Round_SoMedicoImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Round_SoMedicoImpl object);

        public abstract void put(Round_SoMedicoImpl object, Object value);

        public int index() {
            return Round_SoMedicoImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Round_SoMedicoImpl.AttributesEnum.firstIndex() + Round_SoMedicoImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Round_SoMedicoImpl.AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int CODICEREGMEDICO = AttributesEnum.Codiceregmedico.index();
    public static final int CODCOM = AttributesEnum.Codcom.index();
    public static final int COGNOME = AttributesEnum.Cognome.index();
    public static final int NOME = AttributesEnum.Nome.index();
    public static final int INDIRIZZORES = AttributesEnum.IndirizzoRes.index();
    public static final int TEL = AttributesEnum.Tel.index();
    public static final int CELL = AttributesEnum.Cell.index();
    public static final int DTADESIONE = AttributesEnum.Dtadesione.index();
    public static final int DTFINEVALOP = AttributesEnum.Dtfinevalop.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int DTSPEDLETTERA = AttributesEnum.Dtspedlettera.index();
    public static final int DTADESCO = AttributesEnum.DtadesCo.index();
    public static final int DTADESMA = AttributesEnum.DtadesMa.index();
    public static final int CODICEFISCALE = AttributesEnum.Codicefiscale.index();
    public static final int DTULTMODIFICA = AttributesEnum.Dtultmodifica.index();
    public static final int OPULTMODIFICA = AttributesEnum.Opultmodifica.index();
    public static final int DTADESCA = AttributesEnum.DtadesCa.index();
    public static final int RELEASECODECOM = AttributesEnum.ReleaseCodeCom.index();
    public static final int DTADESPF = AttributesEnum.DtadesPf.index();
    public static final int CNF_SOCOMUNE = AttributesEnum.Cnf_SoComune.index();
    public static final int ROUND_SOSOGGETTO = AttributesEnum.Round_SoSoggetto.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Round_SoMedicoImpl()
  {
  }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.round.Round_SoMedico");
    }


    /**
     *
     *  Gets the attribute value for Codiceregmedico, using the alias name Codiceregmedico
     */
    public Integer getCodiceregmedico()
  {
        return (Integer) getAttributeInternal(CODICEREGMEDICO);
    }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codiceregmedico
   */
  public void setCodiceregmedico(Integer value)
  {
    setAttributeInternal(CODICEREGMEDICO, value);
  }

  /**
   * 
   *  Gets the attribute value for Codcom, using the alias name Codcom
   */
  public String getCodcom()
  {
    return (String)getAttributeInternal(CODCOM);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codcom
   */
  public void setCodcom(String value)
  {
    setAttributeInternal(CODCOM, value);
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
   *  Gets the attribute value for IndirizzoRes, using the alias name IndirizzoRes
   */
  public String getIndirizzoRes()
  {
    return (String)getAttributeInternal(INDIRIZZORES);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for IndirizzoRes
   */
  public void setIndirizzoRes(String value)
  {
    setAttributeInternal(INDIRIZZORES, value);
  }

  /**
   * 
   *  Gets the attribute value for Tel, using the alias name Tel
   */
  public String getTel()
  {
    return (String)getAttributeInternal(TEL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Tel
   */
  public void setTel(String value)
  {
    setAttributeInternal(TEL, value);
  }

  /**
   * 
   *  Gets the attribute value for Cell, using the alias name Cell
   */
  public Integer getCell()
  {
    return (Integer)getAttributeInternal(CELL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Cell
   */
  public void setCell(Integer value)
  {
    setAttributeInternal(CELL, value);
  }

  /**
   * 
   *  Gets the attribute value for Dtadesione, using the alias name Dtadesione
   */
  public Date getDtadesione()
  {
    return (Date)getAttributeInternal(DTADESIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtadesione
   */
  public void setDtadesione(Date value)
  {
    setAttributeInternal(DTADESIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for Dtfinevalop, using the alias name Dtfinevalop
   */
  public Date getDtfinevalop()
  {
    return (Date)getAttributeInternal(DTFINEVALOP);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtfinevalop
   */
  public void setDtfinevalop(Date value)
  {
    setAttributeInternal(DTFINEVALOP, value);
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
     * Gets the attribute value for Dtspedlettera, using the alias name Dtspedlettera.
     * @return the value of Dtspedlettera
     */
    public Date getDtspedlettera() {
        return (Date) getAttributeInternal(DTSPEDLETTERA);
    }

    /**
     * Sets <code>value</code> as the attribute value for Dtspedlettera.
     * @param value value to set the Dtspedlettera
     */
    public void setDtspedlettera(Date value) {
        setAttributeInternal(DTSPEDLETTERA, value);
    }

    /**
     * Gets the attribute value for DtadesCo, using the alias name DtadesCo.
     * @return the value of DtadesCo
     */
    public Date getDtadesCo() {
        return (Date) getAttributeInternal(DTADESCO);
    }

    /**
     * Sets <code>value</code> as the attribute value for DtadesCo.
     * @param value value to set the DtadesCo
     */
    public void setDtadesCo(Date value) {
        setAttributeInternal(DTADESCO, value);
    }

    /**
     * Gets the attribute value for DtadesMa, using the alias name DtadesMa.
     * @return the value of DtadesMa
     */
    public Date getDtadesMa() {
        return (Date) getAttributeInternal(DTADESMA);
    }

    /**
     * Sets <code>value</code> as the attribute value for DtadesMa.
     * @param value value to set the DtadesMa
     */
    public void setDtadesMa(Date value) {
        setAttributeInternal(DTADESMA, value);
    }

    /**
     * Gets the attribute value for Codicefiscale, using the alias name Codicefiscale.
     * @return the value of Codicefiscale
     */
    public String getCodicefiscale() {
        return (String) getAttributeInternal(CODICEFISCALE);
    }

    /**
     * Sets <code>value</code> as the attribute value for Codicefiscale.
     * @param value value to set the Codicefiscale
     */
    public void setCodicefiscale(String value) {
        setAttributeInternal(CODICEFISCALE, value);
    }

    /**
     * Gets the attribute value for Dtultmodifica, using the alias name Dtultmodifica.
     * @return the value of Dtultmodifica
     */
    public Date getDtultmodifica() {
        return (Date) getAttributeInternal(DTULTMODIFICA);
    }

    /**
     * Sets <code>value</code> as the attribute value for Dtultmodifica.
     * @param value value to set the Dtultmodifica
     */
    public void setDtultmodifica(Date value) {
        setAttributeInternal(DTULTMODIFICA, value);
    }

    /**
     * Gets the attribute value for Opultmodifica, using the alias name Opultmodifica.
     * @return the value of Opultmodifica
     */
    public String getOpultmodifica() {
        return (String) getAttributeInternal(OPULTMODIFICA);
    }

    /**
     * Sets <code>value</code> as the attribute value for Opultmodifica.
     * @param value value to set the Opultmodifica
     */
    public void setOpultmodifica(String value) {
        setAttributeInternal(OPULTMODIFICA, value);
    }

    /**
     * Gets the attribute value for DtadesCa, using the alias name DtadesCa.
     * @return the value of DtadesCa
     */
    public Date getDtadesCa() {
        return (Date) getAttributeInternal(DTADESCA);
    }

    /**
     * Sets <code>value</code> as the attribute value for DtadesCa.
     * @param value value to set the DtadesCa
     */
    public void setDtadesCa(Date value) {
        setAttributeInternal(DTADESCA, value);
    }

    /**
     * Gets the attribute value for ReleaseCodeCom, using the alias name ReleaseCodeCom.
     * @return the value of ReleaseCodeCom
     */
    public Integer getReleaseCodeCom() {
        return (Integer) getAttributeInternal(RELEASECODECOM);
    }

    /**
     * Sets <code>value</code> as the attribute value for ReleaseCodeCom.
     * @param value value to set the ReleaseCodeCom
     */
    public void setReleaseCodeCom(Integer value) {
        setAttributeInternal(RELEASECODECOM, value);
    }

    /**
     * Gets the attribute value for DtadesPf, using the alias name DtadesPf.
     * @return the value of DtadesPf
     */
    public Date getDtadesPf() {
        return (Date) getAttributeInternal(DTADESPF);
    }

    /**
     * Sets <code>value</code> as the attribute value for DtadesPf.
     * @param value value to set the DtadesPf
     */
    public void setDtadesPf(Date value) {
        setAttributeInternal(DTADESPF, value);
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
     *  Gets the associated entity Cnf_SoComuneImpl
     */
    public Cnf_SoComuneImpl getCnf_SoComune()
  {
    return (Cnf_SoComuneImpl)getAttributeInternal(CNF_SOCOMUNE);
  }

  /**
   * 
   *  Sets <code>value</code> as the associated entity Cnf_SoComuneImpl
   */
  public void setCnf_SoComune(Cnf_SoComuneImpl value)
  {
    setAttributeInternal(CNF_SOCOMUNE, value);
  }

  /**
   *
   *  Gets the associated entity oracle.jbo.RowIterator
   */
    public RowIterator getRound_SoSoggetto()
  {
        return (RowIterator)getAttributeInternal(ROUND_SOSOGGETTO);
    }

    /**
     * @param codiceregmedico key constituent
     * @param ulss key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer codiceregmedico, String ulss) {
        return new Key(new Object[] { codiceregmedico, ulss });
    }


}
