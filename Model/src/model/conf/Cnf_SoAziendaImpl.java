package model.conf;
import oracle.jbo.JboException;
import oracle.jbo.server.EntityImpl;
import oracle.jbo.server.EntityDefImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.Key;
import oracle.jbo.server.TransactionEvent;
import oracle.jbo.RowIterator;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Timestamp;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Cnf_SoAziendaImpl extends EntityImpl {


    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Codaz {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getCodaz();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setCodaz((String) value);
            }
        }
        ,
        Codipa {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getCodipa();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setCodipa((String) value);
            }
        }
        ,
        Logo {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getLogo();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setLogo((BlobDomain) value);
            }
        }
        ,
        Etacitomin {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtacitomin();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtacitomin((Integer) value);
            }
        }
        ,
        Etacitomax {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtacitomax();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtacitomax((Integer) value);
            }
        }
        ,
        Etamammomin {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtamammomin();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtamammomin((Integer) value);
            }
        }
        ,
        Etamammomax {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtamammomax();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtamammomax((Integer) value);
            }
        }
        ,
        Etacolonmin {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtacolonmin();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtacolonmin((Integer) value);
            }
        }
        ,
        Etacolonmax {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtacolonmax();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtacolonmax((Integer) value);
            }
        }
        ,
        Descrizione {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getDescrizione();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setDescrizione((String) value);
            }
        }
        ,
        Dtultagganag {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getDtultagganag();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setDtultagganag((Timestamp) value);
            }
        }
        ,
        GgChiusuraRound {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getGgChiusuraRound();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setGgChiusuraRound((Integer) value);
            }
        }
        ,
        DurataMsgChiusuraRound {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getDurataMsgChiusuraRound();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setDurataMsgChiusuraRound((Integer) value);
            }
        }
        ,
        DataHpv {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getDataHpv();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setDataHpv((Date) value);
            }
        }
        ,
        Etacardiomin {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtacardiomin();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtacardiomin((Date) value);
            }
        }
        ,
        Etacardiomax {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtacardiomax();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtacardiomax((Date) value);
            }
        }
        ,
        Etahpvmin {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtahpvmin();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtahpvmin((Integer) value);
            }
        }
        ,
        Etahpvmax {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtahpvmax();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtahpvmax((Integer) value);
            }
        }
        ,
        GestioneCodts {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getGestioneCodts();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setGestioneCodts((String) value);
            }
        }
        ,
        Etapfasmin {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtapfasmin();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtapfasmin((Date) value);
            }
        }
        ,
        Etapfasmax {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getEtapfasmax();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setEtapfasmax((Date) value);
            }
        }
        ,
        Cnf_SoCnfPartemplate {
            public Object get(Cnf_SoAziendaImpl obj) {
                return obj.getCnf_SoCnfPartemplate();
            }

            public void put(Cnf_SoAziendaImpl obj, Object value) {
                obj.setAttributeInternal(index(), value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(Cnf_SoAziendaImpl object);

        public abstract void put(Cnf_SoAziendaImpl object, Object value);

        public int index() {
            return Cnf_SoAziendaImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return Cnf_SoAziendaImpl.AttributesEnum.firstIndex() + Cnf_SoAziendaImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = Cnf_SoAziendaImpl.AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int CODAZ = AttributesEnum.Codaz.index();
    public static final int CODIPA = AttributesEnum.Codipa.index();
    public static final int LOGO = AttributesEnum.Logo.index();
    public static final int ETACITOMIN = AttributesEnum.Etacitomin.index();
    public static final int ETACITOMAX = AttributesEnum.Etacitomax.index();
    public static final int ETAMAMMOMIN = AttributesEnum.Etamammomin.index();
    public static final int ETAMAMMOMAX = AttributesEnum.Etamammomax.index();
    public static final int ETACOLONMIN = AttributesEnum.Etacolonmin.index();
    public static final int ETACOLONMAX = AttributesEnum.Etacolonmax.index();
    public static final int DESCRIZIONE = AttributesEnum.Descrizione.index();
    public static final int DTULTAGGANAG = AttributesEnum.Dtultagganag.index();
    public static final int GGCHIUSURAROUND = AttributesEnum.GgChiusuraRound.index();
    public static final int DURATAMSGCHIUSURAROUND = AttributesEnum.DurataMsgChiusuraRound.index();
    public static final int DATAHPV = AttributesEnum.DataHpv.index();
    public static final int ETACARDIOMIN = AttributesEnum.Etacardiomin.index();
    public static final int ETACARDIOMAX = AttributesEnum.Etacardiomax.index();
    public static final int ETAHPVMIN = AttributesEnum.Etahpvmin.index();
    public static final int ETAHPVMAX = AttributesEnum.Etahpvmax.index();
    public static final int GESTIONECODTS = AttributesEnum.GestioneCodts.index();
    public static final int ETAPFASMIN = AttributesEnum.Etapfasmin.index();
    public static final int ETAPFASMAX = AttributesEnum.Etapfasmax.index();
    public static final int CNF_SOCNFPARTEMPLATE = AttributesEnum.Cnf_SoCnfPartemplate.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public Cnf_SoAziendaImpl()
  {
  }

    /**
     * @return the definition object for this instance class.
     */
    public static synchronized EntityDefImpl getDefinitionObject() {
        return EntityDefImpl.findDefObject("model.conf.Cnf_SoAzienda");
    }


    /**
     *
     *  Gets the attribute value for Codaz, using the alias name Codaz
     */
    public String getCodaz()
  {
    return (String)getAttributeInternal(CODAZ);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codaz
   */
  public void setCodaz(String value)
  {
    setAttributeInternal(CODAZ, value);
  }

  /**
   * 
   *  Gets the attribute value for Codipa, using the alias name Codipa
   */
  public String getCodipa()
  {
    return (String)getAttributeInternal(CODIPA);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Codipa
   */
  public void setCodipa(String value)
  {
    setAttributeInternal(CODIPA, value);
  }

  /**
   * 
   *  Gets the attribute value for Logo, using the alias name Logo
   */
  public BlobDomain getLogo()
  {
    return (BlobDomain)getAttributeInternal(LOGO);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Logo
   */
  public void setLogo(BlobDomain value)
  {
    setAttributeInternal(LOGO, value);
  }

  /**
   * 
   *  Gets the attribute value for Etacitomin, using the alias name Etacitomin
   */
  public Integer getEtacitomin()
  {
    return (Integer)getAttributeInternal(ETACITOMIN);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Etacitomin
   */
  public void setEtacitomin(Integer value)
  {
    setAttributeInternal(ETACITOMIN, value);
  }

  /**
   * 
   *  Gets the attribute value for Etacitomax, using the alias name Etacitomax
   */
  public Integer getEtacitomax()
  {
    return (Integer)getAttributeInternal(ETACITOMAX);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Etacitomax
   */
  public void setEtacitomax(Integer value)
  {
    setAttributeInternal(ETACITOMAX, value);
  }

  /**
   * 
   *  Gets the attribute value for Etamammomin, using the alias name Etamammomin
   */
  public Integer getEtamammomin()
  {
    return (Integer)getAttributeInternal(ETAMAMMOMIN);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Etamammomin
   */
  public void setEtamammomin(Integer value)
  {
    setAttributeInternal(ETAMAMMOMIN, value);
  }

  /**
   * 
   *  Gets the attribute value for Etamammomax, using the alias name Etamammomax
   */
  public Integer getEtamammomax()
  {
    return (Integer)getAttributeInternal(ETAMAMMOMAX);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Etamammomax
   */
  public void setEtamammomax(Integer value)
  {
    setAttributeInternal(ETAMAMMOMAX, value);
  }

  /**
   * 
   *  Gets the attribute value for Etacolonmin, using the alias name Etacolonmin
   */
  public Integer getEtacolonmin()
  {
    return (Integer)getAttributeInternal(ETACOLONMIN);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Etacolonmin
   */
  public void setEtacolonmin(Integer value)
  {
    setAttributeInternal(ETACOLONMIN, value);
  }

  /**
   * 
   *  Gets the attribute value for Etacolonmax, using the alias name Etacolonmax
   */
  public Integer getEtacolonmax()
  {
    return (Integer)getAttributeInternal(ETACOLONMAX);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Etacolonmax
   */
  public void setEtacolonmax(Integer value)
  {
    setAttributeInternal(ETACOLONMAX, value);
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
   *  Add locking logic here.
   */
  public void lock()
  {
    super.lock();
  }

  /**
   * 
   *  Custom DML update/insert/delete logic here.
   */
  protected void doDML(int operation, TransactionEvent e)
  {
    super.doDML(operation, e);
  }


/*  protected void validateEntity()
  {
    if(this.getLogo()==null)
      throw new JboException("Inserire il logo prima di salvare");
    super.validateEntity();
    if(!this.getCodaz().startsWith("050"))
      throw new JboException("Il codice deve iniziare con 050");
  }*/

  /**
   * 
   *  Gets the attribute value for Descrizione, using the alias name Descrizione
   */
  public String getDescrizione()
  {
    return (String)getAttributeInternal(DESCRIZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Descrizione
   */
  public void setDescrizione(String value)
  {
    setAttributeInternal(DESCRIZIONE, value);
  }


  /**
   *
   *  Gets the associated entity oracle.jbo.RowIterator
   */
    public RowIterator getCnf_SoCnfPartemplate()
  {
        return (RowIterator)getAttributeInternal(CNF_SOCNFPARTEMPLATE);
    }


    /**
     * @param codaz key constituent

     * @return a Key object based on given key constituents.
     */
    public static Key createPrimaryKey(String codaz) {
        return new Key(new Object[] { codaz });
    }

    /**
     *
     *  Gets the attribute value for Dtultagganag, using the alias name Dtultagganag
     */
  public Timestamp getDtultagganag()
  {
    return (Timestamp)getAttributeInternal(DTULTAGGANAG);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for Dtultagganag
   */
  public void setDtultagganag(Timestamp value)
  {
    setAttributeInternal(DTULTAGGANAG, value);
  }


  /**
   * 
   *  Gets the attribute value for GgChiusuraRound, using the alias name GgChiusuraRound
   */
  public Integer getGgChiusuraRound()
  {
    return (Integer)getAttributeInternal(GGCHIUSURAROUND);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for GgChiusuraRound
   */
  public void setGgChiusuraRound(Integer value)
  {
    setAttributeInternal(GGCHIUSURAROUND, value);
  }

  /**
   * 
   *  Gets the attribute value for DurataMsgChiusuraRound, using the alias name DurataMsgChiusuraRound
   */
  public Integer getDurataMsgChiusuraRound()
  {
    return (Integer)getAttributeInternal(DURATAMSGCHIUSURAROUND);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for DurataMsgChiusuraRound
   */
  public void setDurataMsgChiusuraRound(Integer value)
  {
    setAttributeInternal(DURATAMSGCHIUSURAROUND, value);
  }


  /**
   * 
   *  Gets the attribute value for DataHpv, using the alias name DataHpv
   */
  public Date getDataHpv()
  {
    return (Date)getAttributeInternal(DATAHPV);
  }

  /**
   * 
   *  Sets <code>value</code> as the attribute value for DataHpv
   */
  public void setDataHpv(Date value)
  {
    setAttributeInternal(DATAHPV, value);
  }

    /**
     * Gets the attribute value for Etacardiomin, using the alias name Etacardiomin.
     * @return the value of Etacardiomin
     */
    public Date getEtacardiomin() {
        return (Date) getAttributeInternal(ETACARDIOMIN);
    }

    /**
     * Sets <code>value</code> as the attribute value for Etacardiomin.
     * @param value value to set the Etacardiomin
     */
    public void setEtacardiomin(Date value) {
        setAttributeInternal(ETACARDIOMIN, value);
    }

    /**
     * Gets the attribute value for Etacardiomax, using the alias name Etacardiomax.
     * @return the value of Etacardiomax
     */
    public Date getEtacardiomax() {
        return (Date) getAttributeInternal(ETACARDIOMAX);
    }

    /**
     * Sets <code>value</code> as the attribute value for Etacardiomax.
     * @param value value to set the Etacardiomax
     */
    public void setEtacardiomax(Date value) {
        setAttributeInternal(ETACARDIOMAX, value);
    }

    /**
     * Gets the attribute value for Etahpvmin, using the alias name Etahpvmin.
     * @return the value of Etahpvmin
     */
    public Integer getEtahpvmin() {
        return (Integer) getAttributeInternal(ETAHPVMIN);
    }

    /**
     * Sets <code>value</code> as the attribute value for Etahpvmin.
     * @param value value to set the Etahpvmin
     */
    public void setEtahpvmin(Integer value) {
        setAttributeInternal(ETAHPVMIN, value);
    }

    /**
     * Gets the attribute value for Etahpvmax, using the alias name Etahpvmax.
     * @return the value of Etahpvmax
     */
    public Integer getEtahpvmax() {
        return (Integer) getAttributeInternal(ETAHPVMAX);
    }

    /**
     * Sets <code>value</code> as the attribute value for Etahpvmax.
     * @param value value to set the Etahpvmax
     */
    public void setEtahpvmax(Integer value) {
        setAttributeInternal(ETAHPVMAX, value);
    }

    /**
     * Gets the attribute value for GestioneCodts, using the alias name GestioneCodts.
     * @return the value of GestioneCodts
     */
    public String getGestioneCodts() {
        return (String) getAttributeInternal(GESTIONECODTS);
    }

    /**
     * Sets <code>value</code> as the attribute value for GestioneCodts.
     * @param value value to set the GestioneCodts
     */
    public void setGestioneCodts(String value) {
        setAttributeInternal(GESTIONECODTS, value);
    }

    /**
     * Gets the attribute value for Etapfasmin, using the alias name Etapfasmin.
     * @return the value of Etapfasmin
     */
    public Date getEtapfasmin() {
        return (Date) getAttributeInternal(ETAPFASMIN);
    }

    /**
     * Sets <code>value</code> as the attribute value for Etapfasmin.
     * @param value value to set the Etapfasmin
     */
    public void setEtapfasmin(Date value) {
        setAttributeInternal(ETAPFASMIN, value);
    }

    /**
     * Gets the attribute value for Etapfasmax, using the alias name Etapfasmax.
     * @return the value of Etapfasmax
     */
    public Date getEtapfasmax() {
        return (Date) getAttributeInternal(ETAPFASMAX);
    }

    /**
     * Sets <code>value</code> as the attribute value for Etapfasmax.
     * @param value value to set the Etapfasmax
     */
    public void setEtapfasmax(Date value) {
        setAttributeInternal(ETAPFASMAX, value);
    }


}
