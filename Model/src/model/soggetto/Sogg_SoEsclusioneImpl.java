package model.soggetto;

import model.conf.Cnf_SoCnfEsclusioneImpl;

import model.referto.Ref_SoInvitoImpl;

import model.round.Round_SoSoggettoImpl;

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

public class Sogg_SoEsclusioneImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idescl {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getIdescl();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setIdescl((Integer)value);
            }
        }
        ,
        Idcnfescl {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getIdcnfescl();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setIdcnfescl((Integer)value);
            }
        }
        ,
        Idinvito {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getIdinvito();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setIdinvito((Integer)value);
            }
        }
        ,
        Codts {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getCodts();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setCodts((String)value);
            }
        }
        ,
        Dtescl {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getDtescl();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setDtescl((Date)value);
            }
        }
        ,
        Ultima {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getUltima();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setUltima((Integer)value);
            }
        }
        ,
        Dtrichiamo {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getDtrichiamo();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setDtrichiamo((Date)value);
            }
        }
        ,
        Lettrichiamo {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getLettrichiamo();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setLettrichiamo((Integer)value);
            }
        }
        ,
        Roundorgescl {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getRoundorgescl();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setRoundorgescl((Integer)value);
            }
        }
        ,
        Roundindiv {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getRoundindiv();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setRoundindiv((Integer)value);
            }
        }
        ,
        Dtannull {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getDtannull();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setDtannull((Date)value);
            }
        }
        ,
        Dtmodescl {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getDtmodescl();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setDtmodescl((Date)value);
            }
        }
        ,
        Noteescl {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getNoteescl();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setNoteescl((String)value);
            }
        }
        ,
        Ulss {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getUlss();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getTpscr();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Dtcreazione {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getDtcreazione();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setDtcreazione((Date)value);
            }
        }
        ,
        Opcreazione {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getOpcreazione();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setOpcreazione((String)value);
            }
        }
        ,
        Opmodifica {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getOpmodifica();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setOpmodifica((String)value);
            }
        }
        ,
        IdesclPadre {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getIdesclPadre();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setIdesclPadre((Integer)value);
            }
        }
        ,
        Round_SoSoggetto {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getRound_SoSoggetto();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setRound_SoSoggetto((Round_SoSoggettoImpl)value);
            }
        }
        ,
        Ref_SoInvito {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getRef_SoInvito();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setRef_SoInvito((Ref_SoInvitoImpl)value);
            }
        }
        ,
        Cnf_SoCnfEsclusione {
            public Object get(Sogg_SoEsclusioneImpl obj) {
                return obj.getCnf_SoCnfEsclusione();
            }

            public void put(Sogg_SoEsclusioneImpl obj, Object value) {
                obj.setCnf_SoCnfEsclusione((Cnf_SoCnfEsclusioneImpl)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Sogg_SoEsclusioneImpl object);

        public abstract void put(Sogg_SoEsclusioneImpl object, Object value);

        public int index() {
            return Sogg_SoEsclusioneImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Sogg_SoEsclusioneImpl.AttributesEnum.firstIndex() + Sogg_SoEsclusioneImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Sogg_SoEsclusioneImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDESCL = AttributesEnum.Idescl.index();
    public static final int IDCNFESCL = AttributesEnum.Idcnfescl.index();
    public static final int IDINVITO = AttributesEnum.Idinvito.index();
    public static final int CODTS = AttributesEnum.Codts.index();
    public static final int DTESCL = AttributesEnum.Dtescl.index();
    public static final int ULTIMA = AttributesEnum.Ultima.index();
    public static final int DTRICHIAMO = AttributesEnum.Dtrichiamo.index();
    public static final int LETTRICHIAMO = AttributesEnum.Lettrichiamo.index();
    public static final int ROUNDORGESCL = AttributesEnum.Roundorgescl.index();
    public static final int ROUNDINDIV = AttributesEnum.Roundindiv.index();
    public static final int DTANNULL = AttributesEnum.Dtannull.index();
    public static final int DTMODESCL = AttributesEnum.Dtmodescl.index();
    public static final int NOTEESCL = AttributesEnum.Noteescl.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int DTCREAZIONE = AttributesEnum.Dtcreazione.index();
    public static final int OPCREAZIONE = AttributesEnum.Opcreazione.index();
    public static final int OPMODIFICA = AttributesEnum.Opmodifica.index();
    public static final int IDESCLPADRE = AttributesEnum.IdesclPadre.index();
    public static final int ROUND_SOSOGGETTO = AttributesEnum.Round_SoSoggetto.index();
    public static final int REF_SOINVITO = AttributesEnum.Ref_SoInvito.index();
    public static final int CNF_SOCNFESCLUSIONE = AttributesEnum.Cnf_SoCnfEsclusione.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Sogg_SoEsclusioneImpl()
  {
  }


    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.soggetto.Sogg_SoEsclusione");
    }

    /**
     *
     *  Gets the attribute value for Idescl, using the alias name Idescl
     */
    public Integer getIdescl()
  {
    return (Integer)getAttributeInternal(IDESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idescl
   */
  public void setIdescl(Integer value)
  {
    setAttributeInternal(IDESCL, value);
  }

  /**
   * 
   *  Gets the attribute value for Idcnfescl, using the alias name Idcnfescl
   */
  public Integer getIdcnfescl()
  {
    return (Integer)getAttributeInternal(IDCNFESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idcnfescl
   */
  public void setIdcnfescl(Integer value)
  {
    setAttributeInternal(IDCNFESCL, value);
  }

  /**
   * 
   *  Gets the attribute value for Idinvito, using the alias name Idinvito
   */
  public Integer getIdinvito()
  {
    return (Integer)getAttributeInternal(IDINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Idinvito
   */
  public void setIdinvito(Integer value)
  {
    setAttributeInternal(IDINVITO, value);
  }

  /**
   * 
   *  Gets the attribute value for Codts, using the alias name Codts
   */
  public String getCodts()
  {
    return (String)getAttributeInternal(CODTS);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codts
   */
  public void setCodts(String value)
  {
    setAttributeInternal(CODTS, value);
  }

  /**
   * 
   *  Gets the attribute value for Dtescl, using the alias name Dtescl
   */
  public Date getDtescl()
  {
    return (Date)getAttributeInternal(DTESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtescl
   */
  public void setDtescl(Date value)
  {
    setAttributeInternal(DTESCL, value);
  }

  /**
   * 
   *  Gets the attribute value for Ultima, using the alias name Ultima
   */
  public Integer getUltima()
  {
    return (Integer)getAttributeInternal(ULTIMA);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Ultima
   */
  public void setUltima(Integer value)
  {
    setAttributeInternal(ULTIMA, value);
  }

  /**
   * 
   *  Gets the attribute value for Dtrichiamo, using the alias name Dtrichiamo
   */
  public Date getDtrichiamo()
  {
    return (Date)getAttributeInternal(DTRICHIAMO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtrichiamo
   */
  public void setDtrichiamo(Date value)
  {
    setAttributeInternal(DTRICHIAMO, value);
  }

  /**
   * 
   *  Gets the attribute value for Lettrichiamo, using the alias name Lettrichiamo
   */
  public Integer getLettrichiamo()
  {
    return (Integer)getAttributeInternal(LETTRICHIAMO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Lettrichiamo
   */
  public void setLettrichiamo(Integer value)
  {
    setAttributeInternal(LETTRICHIAMO, value);
  }

  /**
   * 
   *  Gets the attribute value for Roundorgescl, using the alias name Roundorgescl
   */
  public Integer getRoundorgescl()
  {
    return (Integer)getAttributeInternal(ROUNDORGESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Roundorgescl
   */
  public void setRoundorgescl(Integer value)
  {
    setAttributeInternal(ROUNDORGESCL, value);
  }

  /**
   * 
   *  Gets the attribute value for Roundindiv, using the alias name Roundindiv
   */
  public Integer getRoundindiv()
  {
    return (Integer)getAttributeInternal(ROUNDINDIV);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Roundindiv
   */
  public void setRoundindiv(Integer value)
  {
    setAttributeInternal(ROUNDINDIV, value);
  }

  /**
   * 
   *  Gets the attribute value for Dtannull, using the alias name Dtannull
   */
  public Date getDtannull()
  {
    return (Date)getAttributeInternal(DTANNULL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtannull
   */
  public void setDtannull(Date value)
  {
    setAttributeInternal(DTANNULL, value);
  }

  /**
   * 
   *  Gets the attribute value for Dtmodescl, using the alias name Dtmodescl
   */
  public Date getDtmodescl()
  {
    return (Date)getAttributeInternal(DTMODESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtmodescl
   */
  public void setDtmodescl(Date value)
  {
    setAttributeInternal(DTMODESCL, value);
  }

  /**
   * 
   *  Gets the attribute value for Noteescl, using the alias name Noteescl
   */
  public String getNoteescl()
  {
    return (String)getAttributeInternal(NOTEESCL);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Noteescl
   */
  public void setNoteescl(String value)
  {
    setAttributeInternal(NOTEESCL, value);
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
   *  Gets the associated entity Ref_SoInvitoImpl
   */
  public Ref_SoInvitoImpl getRef_SoInvito()
  {
    return (Ref_SoInvitoImpl)getAttributeInternal(REF_SOINVITO);
  }

  /**
   * 
   *  Sets <code>value</code> as the associated entity Ref_SoInvitoImpl
   */
  public void setRef_SoInvito(Ref_SoInvitoImpl value)
  {
    setAttributeInternal(REF_SOINVITO, value);
  }

  /**
   * 
   *  Gets the associated entity Round_SoSoggettoImpl
   */
  public Round_SoSoggettoImpl getRound_SoSoggetto()
  {
    return (Round_SoSoggettoImpl)getAttributeInternal(ROUND_SOSOGGETTO);
  }

  /**
   * 
   *  Sets <code>value</code> as the associated entity Round_SoSoggettoImpl
   */
  public void setRound_SoSoggetto(Round_SoSoggettoImpl value)
  {
    setAttributeInternal(ROUND_SOSOGGETTO, value);
  }

  /**
     *
     *  Gets the associated entity Cnf_SoCnfEsclusioneImpl
     */
    public Cnf_SoCnfEsclusioneImpl getCnf_SoCnfEsclusione()
  {
    return (Cnf_SoCnfEsclusioneImpl)getAttributeInternal(CNF_SOCNFESCLUSIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the associated entity Cnf_SoCnfEsclusioneImpl
   */
  public void setCnf_SoCnfEsclusione(Cnf_SoCnfEsclusioneImpl value)
  {
    setAttributeInternal(CNF_SOCNFESCLUSIONE, value);
  }


    /**
     * @param idescl key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(Integer idescl) {
        return new Key(new Object[]{idescl});
    }

    /**
     *
     *  Gets the attribute value for Dtcreazione, using the alias name Dtcreazione
     */
  public Date getDtcreazione()
  {
    return (Date)getAttributeInternal(DTCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtcreazione
   */
  public void setDtcreazione(Date value)
  {
    setAttributeInternal(DTCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for Opcreazione, using the alias name Opcreazione
   */
  public String getOpcreazione()
  {
    return (String)getAttributeInternal(OPCREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Opcreazione
   */
  public void setOpcreazione(String value)
  {
    setAttributeInternal(OPCREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for Opmodifica, using the alias name Opmodifica
   */
  public String getOpmodifica()
  {
    return (String)getAttributeInternal(OPMODIFICA);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Opmodifica
   */
  public void setOpmodifica(String value)
  {
    setAttributeInternal(OPMODIFICA, value);
  }


  /**
   * 
   *  Gets the attribute value for IdesclPadre, using the alias name IdesclPadre
   */
  public Integer getIdesclPadre()
  {
    return (Integer)getAttributeInternal(IDESCLPADRE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for IdesclPadre
   */
  public void setIdesclPadre(Integer value)
  {
    setAttributeInternal(IDESCLPADRE, value);
  }


}