package model.accettazione;


import oracle.jbo.domain.Date;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Acc_SoAnamnesiCitoViewRowImpl extends ViewRowImpl implements model.common.Acc_SoAnamnesiCitoViewRow 
{


    public static final int ENTITY_ACC_SOANAMNESICITO = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        IdAnamci {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getIdAnamci();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setIdAnamci((Integer) value);
            }
        }
        ,
        Idacc1liv {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getIdacc1liv();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setIdacc1liv((Integer) value);
            }
        }
        ,
        Idacc2liv {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getIdacc2liv();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setIdacc2liv((Integer) value);
            }
        }
        ,
        Codts {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getCodts();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setCodts((String)value);
            }
        }
        ,
        Dtcreazione {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setDtcreazione((Date)value);
            }
        }
        ,
        Opcreazione {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getOpcreazione();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setOpcreazione((String)value);
            }
        }
        ,
        Dtultmod {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getDtultmod();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setDtultmod((Date)value);
            }
        }
        ,
        Opultmod {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getOpultmod();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setOpultmod((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Note {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getNote();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setNote((String)value);
            }
        }
        ,
        Idopanamnesi {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getIdopanamnesi();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setIdopanamnesi((Integer) value);
            }
        }
        ,
        Dtanamnesi {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getDtanamnesi();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setDtanamnesi((Date)value);
            }
        }
        ,
        Gravidanza {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getGravidanza();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setGravidanza((Integer) value);
            }
        }
        ,
        MeseGravidanza {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getMeseGravidanza();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setMeseGravidanza((Integer) value);
            }
        }
        ,
        Allattamento {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getAllattamento();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setAllattamento((Integer) value);
            }
        }
        ,
        PresenzaIud {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getPresenzaIud();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setPresenzaIud((Integer) value);
            }
        }
        ,
        ContraccTorm {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getContraccTorm();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setContraccTorm((Integer) value);
            }
        }
        ,
        DtUltMestr {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getDtUltMestr();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setDtUltMestr((Date)value);
            }
        }
        ,
        Menopausa {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getMenopausa();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setMenopausa((Integer) value);
            }
        }
        ,
        ChemioUlt1 {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getChemioUlt1();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setChemioUlt1((Integer) value);
            }
        }
        ,
        RadioUlt3 {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getRadioUlt3();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setRadioUlt3((Integer) value);
            }
        }
        ,
        GravTermine {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getGravTermine();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setGravTermine((Integer) value);
            }
        }
        ,
        Aborti {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getAborti();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setAborti((Integer) value);
            }
        }
        ,
        PartiPrematuri {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getPartiPrematuri();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setPartiPrematuri((Integer) value);
            }
        }
        ,
        FigliViventi {
            public Object get(Acc_SoAnamnesiCitoViewRowImpl obj) {
                return obj.getFigliViventi();
            }

            public void put(Acc_SoAnamnesiCitoViewRowImpl obj, Object value) {
                obj.setFigliViventi((Integer) value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Acc_SoAnamnesiCitoViewRowImpl object);

        public abstract void put(Acc_SoAnamnesiCitoViewRowImpl object, Object value);

        public int index() {
            return Acc_SoAnamnesiCitoViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Acc_SoAnamnesiCitoViewRowImpl.AttributesEnum.firstIndex() + Acc_SoAnamnesiCitoViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Acc_SoAnamnesiCitoViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDANAMCI = AttributesEnum.IdAnamci.index();
    public static final int IDACC1LIV = AttributesEnum.Idacc1liv.index();
    public static final int IDACC2LIV = AttributesEnum.Idacc2liv.index();
    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int OPCREAZIONE = AttributesEnum.Opcreazione.index();
    public static final int DTULTMOD = AttributesEnum.Dtultmod.index();
    public static final int OPULTMOD = AttributesEnum.Opultmod.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int NOTE = AttributesEnum.Note.index();
    public static final int IDOPANAMNESI = AttributesEnum.Idopanamnesi.index();
    public static final int DTANAMNESI = AttributesEnum.Dtanamnesi.index();
    public static final int GRAVIDANZA = AttributesEnum.Gravidanza.index();
    public static final int MESEGRAVIDANZA = AttributesEnum.MeseGravidanza.index();
    public static final int ALLATTAMENTO = AttributesEnum.Allattamento.index();
    public static final int PRESENZAIUD = AttributesEnum.PresenzaIud.index();
    public static final int CONTRACCTORM = AttributesEnum.ContraccTorm.index();
    public static final int DTULTMESTR = AttributesEnum.DtUltMestr.index();
    public static final int MENOPAUSA = AttributesEnum.Menopausa.index();
    public static final int CHEMIOULT1 = AttributesEnum.ChemioUlt1.index();
    public static final int RADIOULT3 = AttributesEnum.RadioUlt3.index();
    public static final int GRAVTERMINE = AttributesEnum.GravTermine.index();
    public static final int ABORTI = AttributesEnum.Aborti.index();
    public static final int PARTIPREMATURI = AttributesEnum.PartiPrematuri.index();
    public static final int FIGLIVIVENTI = AttributesEnum.FigliViventi.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Acc_SoAnamnesiCitoViewRowImpl()
  {
  }

  /**
   * 
   *  Gets Acc_SoAnamnesiCito entity object.
   */
  public Acc_SoAnamnesiCitoImpl getAcc_SoAnamnesiCito()
  {
    return (Acc_SoAnamnesiCitoImpl)getEntity(0);
  }

  /**
     *
     *  Gets the attribute value for ID_ANAMCI using the alias name IdAnamci
     */
    public Integer getIdAnamci()
  {
        return (Integer) getAttributeInternal(IDANAMCI);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ID_ANAMCI using the alias name IdAnamci
   */
  public void setIdAnamci(Integer value)
  {
    setAttributeInternal(IDANAMCI, value);
  }

  /**
     *
     *  Gets the attribute value for IDACC1LIV using the alias name Idacc1liv
     */
    public Integer getIdacc1liv()
  {
        return (Integer) getAttributeInternal(IDACC1LIV);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDACC1LIV using the alias name Idacc1liv
   */
  public void setIdacc1liv(Integer value)
  {
    setAttributeInternal(IDACC1LIV, value);
  }

  /**
     *
     *  Gets the attribute value for IDACC2LIV using the alias name Idacc2liv
     */
    public Integer getIdacc2liv()
  {
        return (Integer) getAttributeInternal(IDACC2LIV);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDACC2LIV using the alias name Idacc2liv
   */
  public void setIdacc2liv(Integer value)
  {
    setAttributeInternal(IDACC2LIV, value);
  }

  /**
   * 
   *  Gets the attribute value for CODTS using the alias name Codts
   */
  public String getCodts()
  {
    return (String)getAttributeInternal(CODTS);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODTS using the alias name Codts
   */
  public void setCodts(String value)
  {
    setAttributeInternal(CODTS, value);
  }

  /**
   * 
   *  Gets the attribute value for DTCREAZIONE using the alias name Dtcreazione
   */
  public Date getDtcreazione()
  {
    return (Date)getAttributeInternal(DTCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTCREAZIONE using the alias name Dtcreazione
   */
  public void setDtcreazione(Date value)
  {
    setAttributeInternal(DTCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for OPCREAZIONE using the alias name Opcreazione
   */
  public String getOpcreazione()
  {
    return (String)getAttributeInternal(OPCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for OPCREAZIONE using the alias name Opcreazione
   */
  public void setOpcreazione(String value)
  {
    setAttributeInternal(OPCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for DTULTMOD using the alias name Dtultmod
   */
  public Date getDtultmod()
  {
    return (Date)getAttributeInternal(DTULTMOD);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTULTMOD using the alias name Dtultmod
   */
  public void setDtultmod(Date value)
  {
    setAttributeInternal(DTULTMOD, value);
  }

  /**
   * 
   *  Gets the attribute value for OPULTMOD using the alias name Opultmod
   */
  public String getOpultmod()
  {
    return (String)getAttributeInternal(OPULTMOD);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for OPULTMOD using the alias name Opultmod
   */
  public void setOpultmod(String value)
  {
    setAttributeInternal(OPULTMOD, value);
  }

  /**
   * 
   *  Gets the attribute value for ULSS using the alias name Ulss
   */
  public String getUlss()
  {
    return (String)getAttributeInternal(ULSS);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ULSS using the alias name Ulss
   */
  public void setUlss(String value)
  {
    setAttributeInternal(ULSS, value);
  }

  /**
   * 
   *  Gets the attribute value for TPSCR using the alias name Tpscr
   */
  public String getTpscr()
  {
    return (String)getAttributeInternal(TPSCR);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TPSCR using the alias name Tpscr
   */
  public void setTpscr(String value)
  {
    setAttributeInternal(TPSCR, value);
  }

  /**
   * 
   *  Gets the attribute value for NOTE using the alias name Note
   */
  public String getNote()
  {
    return (String)getAttributeInternal(NOTE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for NOTE using the alias name Note
   */
  public void setNote(String value)
  {
    setAttributeInternal(NOTE, value);
  }

  /**
     *
     *  Gets the attribute value for IDOPANAMNESI using the alias name Idopanamnesi
     */
    public Integer getIdopanamnesi()
  {
        return (Integer) getAttributeInternal(IDOPANAMNESI);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDOPANAMNESI using the alias name Idopanamnesi
   */
  public void setIdopanamnesi(Integer value)
  {
    setAttributeInternal(IDOPANAMNESI, value);
  }

  /**
   * 
   *  Gets the attribute value for DTANAMNESI using the alias name Dtanamnesi
   */
  public Date getDtanamnesi()
  {
    return (Date)getAttributeInternal(DTANAMNESI);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DTANAMNESI using the alias name Dtanamnesi
   */
  public void setDtanamnesi(Date value)
  {
    setAttributeInternal(DTANAMNESI, value);
  }

  /**
     *
     *  Gets the attribute value for GRAVIDANZA using the alias name Gravidanza
     */
    public Integer getGravidanza()
  {
        return (Integer) getAttributeInternal(GRAVIDANZA);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for GRAVIDANZA using the alias name Gravidanza
   */
  public void setGravidanza(Integer value)
  {
    setAttributeInternal(GRAVIDANZA, value);
  }

  /**
     *
     *  Gets the attribute value for MESE_GRAVIDANZA using the alias name MeseGravidanza
     */
    public Integer getMeseGravidanza()
  {
        return (Integer) getAttributeInternal(MESEGRAVIDANZA);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for MESE_GRAVIDANZA using the alias name MeseGravidanza
   */
  public void setMeseGravidanza(Integer value)
  {
    setAttributeInternal(MESEGRAVIDANZA, value);
  }

  /**
     *
     *  Gets the attribute value for ALLATTAMENTO using the alias name Allattamento
     */
    public Integer getAllattamento()
  {
        return (Integer) getAttributeInternal(ALLATTAMENTO);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ALLATTAMENTO using the alias name Allattamento
   */
  public void setAllattamento(Integer value)
  {
    setAttributeInternal(ALLATTAMENTO, value);
  }

  /**
     *
     *  Gets the attribute value for PRESENZA_IUD using the alias name PresenzaIud
     */
    public Integer getPresenzaIud()
  {
        return (Integer) getAttributeInternal(PRESENZAIUD);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for PRESENZA_IUD using the alias name PresenzaIud
   */
  public void setPresenzaIud(Integer value)
  {
    setAttributeInternal(PRESENZAIUD, value);
  }

  /**
     *
     *  Gets the attribute value for CONTRACC_TORM using the alias name ContraccTorm
     */
    public Integer getContraccTorm()
  {
        return (Integer) getAttributeInternal(CONTRACCTORM);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CONTRACC_TORM using the alias name ContraccTorm
   */
  public void setContraccTorm(Integer value)
  {
    setAttributeInternal(CONTRACCTORM, value);
  }

  /**
   * 
   *  Gets the attribute value for DT_ULT_MESTR using the alias name DtUltMestr
   */
  public Date getDtUltMestr()
  {
    return (Date)getAttributeInternal(DTULTMESTR);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DT_ULT_MESTR using the alias name DtUltMestr
   */
  public void setDtUltMestr(Date value)
  {
    setAttributeInternal(DTULTMESTR, value);
  }

  /**
     *
     *  Gets the attribute value for MENOPAUSA using the alias name Menopausa
     */
    public Integer getMenopausa()
  {
        return (Integer) getAttributeInternal(MENOPAUSA);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for MENOPAUSA using the alias name Menopausa
   */
  public void setMenopausa(Integer value)
  {
    setAttributeInternal(MENOPAUSA, value);
  }

  /**
     *
     *  Gets the attribute value for CHEMIO_ULT1 using the alias name ChemioUlt1
     */
    public Integer getChemioUlt1()
  {
        return (Integer) getAttributeInternal(CHEMIOULT1);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CHEMIO_ULT1 using the alias name ChemioUlt1
   */
  public void setChemioUlt1(Integer value)
  {
    setAttributeInternal(CHEMIOULT1, value);
  }

  /**
     *
     *  Gets the attribute value for RADIO_ULT3 using the alias name RadioUlt3
     */
    public Integer getRadioUlt3()
  {
        return (Integer) getAttributeInternal(RADIOULT3);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for RADIO_ULT3 using the alias name RadioUlt3
   */
  public void setRadioUlt3(Integer value)
  {
    setAttributeInternal(RADIOULT3, value);
  }

  /**
     *
     *  Gets the attribute value for GRAV_TERMINE using the alias name GravTermine
     */
    public Integer getGravTermine()
  {
        return (Integer) getAttributeInternal(GRAVTERMINE);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for GRAV_TERMINE using the alias name GravTermine
   */
  public void setGravTermine(Integer value)
  {
    setAttributeInternal(GRAVTERMINE, value);
  }

  /**
     *
     *  Gets the attribute value for ABORTI using the alias name Aborti
     */
    public Integer getAborti()
  {
        return (Integer) getAttributeInternal(ABORTI);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ABORTI using the alias name Aborti
   */
  public void setAborti(Integer value)
  {
    setAttributeInternal(ABORTI, value);
  }

  /**
     *
     *  Gets the attribute value for PARTI_PREMATURI using the alias name PartiPrematuri
     */
    public Integer getPartiPrematuri()
  {
        return (Integer) getAttributeInternal(PARTIPREMATURI);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for PARTI_PREMATURI using the alias name PartiPrematuri
   */
  public void setPartiPrematuri(Integer value)
  {
    setAttributeInternal(PARTIPREMATURI, value);
  }

  /**
     *
     *  Gets the attribute value for FIGLI_VIVENTI using the alias name FigliViventi
     */
    public Integer getFigliViventi()
  {
        return (Integer) getAttributeInternal(FIGLIVIVENTI);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for FIGLI_VIVENTI using the alias name FigliViventi
   */
  public void setFigliViventi(Integer value)
  {
    setAttributeInternal(FIGLIVIVENTI, value);
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
}