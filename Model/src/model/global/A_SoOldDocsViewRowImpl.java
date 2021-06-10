package model.global;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.domain.Date;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class A_SoOldDocsViewRowImpl extends ViewRowImpl implements model.common.A_SoOldDocsViewRow 
{


    public static final int ENTITY_A_SOOLDDOCS = 0;

    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Idallegato {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getIdallegato();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setIdallegato((Integer) value);
            }
        }
        ,
        Iddocumento {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getIddocumento();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setIddocumento((Integer) value);
            }
        }
        ,
        Tipolettera {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getTipolettera();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setTipolettera((Integer) value);
            }
        }
        ,
        Codtempl {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getCodtempl();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setCodtempl((Integer) value);
            }
        }
        ,
        Cognome {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getCognome();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setCognome((String)value);
            }
        }
        ,
        Nome {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getNome();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setNome((String)value);
            }
        }
        ,
        Indirizzo {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getIndirizzo();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setIndirizzo((String)value);
            }
        }
        ,
        Cap {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getCap();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setCap((String)value);
            }
        }
        ,
        Comune {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getComune();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setComune((String)value);
            }
        }
        ,
        Provincia {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getProvincia();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setProvincia((String)value);
            }
        }
        ,
        Centro {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getCentro();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setCentro((String)value);
            }
        }
        ,
        IndirizzoCentro {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getIndirizzoCentro();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setIndirizzoCentro((String)value);
            }
        }
        ,
        DataApp {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getDataApp();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setDataApp((String)value);
            }
        }
        ,
        OraApp {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getOraApp();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setOraApp((String)value);
            }
        }
        ,
        Medico {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getMedico();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setMedico((String)value);
            }
        }
        ,
        DataEsame {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getDataEsame();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setDataEsame((String)value);
            }
        }
        ,
        CodiceCampione {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getCodiceCampione();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setCodiceCampione((String)value);
            }
        }
        ,
        TesseraSanitaria {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getTesseraSanitaria();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setTesseraSanitaria((String)value);
            }
        }
        ,
        CodiceFiscale {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getCodiceFiscale();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setCodiceFiscale((String)value);
            }
        }
        ,
        DataNascita {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getDataNascita();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setDataNascita((String)value);
            }
        }
        ,
        CognomeMarito {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getCognomeMarito();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setCognomeMarito((String)value);
            }
        }
        ,
        Telefono1 {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getTelefono1();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setTelefono1((String)value);
            }
        }
        ,
        Telefono2 {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getTelefono2();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setTelefono2((String)value);
            }
        }
        ,
        CentroReferto {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getCentroReferto();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setCentroReferto((String)value);
            }
        }
        ,
        DataCreazione {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getDataCreazione();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setDataCreazione((Date)value);
            }
        }
        ,
        DataStampa {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getDataStampa();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setDataStampa((Date)value);
            }
        }
        ,
        Postel {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getPostel();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setPostel((Integer) value);
            }
        }
        ,
        Ulss {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getUlss();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setUlss((String)value);
            }
        }
        ,
        Tpscr {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getTpscr();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setTpscr((String)value);
            }
        }
        ,
        Importato {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getImportato();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setImportato((Integer) value);
            }
        }
        ,
        GiorniOrari {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getGiorniOrari();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setGiorniOrari((String)value);
            }
        }
        ,
        Telefono {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getTelefono();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setTelefono((String)value);
            }
        }
        ,
        IntFirmaInviti {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getIntFirmaInviti();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setIntFirmaInviti((String)value);
            }
        }
        ,
        FirmaInviti {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getFirmaInviti();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setFirmaInviti((String)value);
            }
        }
        ,
        FirmaRef1 {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getFirmaRef1();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setFirmaRef1((String)value);
            }
        }
        ,
        FirmaRef2 {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getFirmaRef2();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setFirmaRef2((String)value);
            }
        }
        ,
        Luogo {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getLuogo();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setLuogo((String)value);
            }
        }
        ,
        Altro {
            public Object get(A_SoOldDocsViewRowImpl obj) {
                return obj.getAltro();
            }

            public void put(A_SoOldDocsViewRowImpl obj, Object value) {
                obj.setAltro((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(A_SoOldDocsViewRowImpl object);

        public abstract void put(A_SoOldDocsViewRowImpl object, Object value);

        public int index() {
            return A_SoOldDocsViewRowImpl.AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return A_SoOldDocsViewRowImpl.AttributesEnum.firstIndex() + A_SoOldDocsViewRowImpl.AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = A_SoOldDocsViewRowImpl.AttributesEnum.values();
            }
            return vals;
        }
    }
    public static final int IDALLEGATO = AttributesEnum.Idallegato.index();
    public static final int IDDOCUMENTO = AttributesEnum.Iddocumento.index();
    public static final int TIPOLETTERA = AttributesEnum.Tipolettera.index();
    public static final int CODTEMPL = AttributesEnum.Codtempl.index();
    public static final int COGNOME = AttributesEnum.Cognome.index();
    public static final int NOME = AttributesEnum.Nome.index();
    public static final int INDIRIZZO = AttributesEnum.Indirizzo.index();
    public static final int CAP = AttributesEnum.Cap.index();
    public static final int COMUNE = AttributesEnum.Comune.index();
    public static final int PROVINCIA = AttributesEnum.Provincia.index();
    public static final int CENTRO = AttributesEnum.Centro.index();
    public static final int INDIRIZZOCENTRO = AttributesEnum.IndirizzoCentro.index();
    public static final int DATAAPP = AttributesEnum.DataApp.index();
    public static final int ORAAPP = AttributesEnum.OraApp.index();
    public static final int MEDICO = AttributesEnum.Medico.index();
    public static final int DATAESAME = AttributesEnum.DataEsame.index();
    public static final int CODICECAMPIONE = AttributesEnum.CodiceCampione.index();
    public static final int TESSERASANITARIA = AttributesEnum.TesseraSanitaria.index();
    public static final int CODICEFISCALE = AttributesEnum.CodiceFiscale.index();
    public static final int DATANASCITA = AttributesEnum.DataNascita.index();
    public static final int COGNOMEMARITO = AttributesEnum.CognomeMarito.index();
    public static final int TELEFONO1 = AttributesEnum.Telefono1.index();
    public static final int TELEFONO2 = AttributesEnum.Telefono2.index();
    public static final int CENTROREFERTO = AttributesEnum.CentroReferto.index();
    public static final int DATACREAZIONE = AttributesEnum.DataCreazione.index();
    public static final int DATASTAMPA = AttributesEnum.DataStampa.index();
    public static final int POSTEL = AttributesEnum.Postel.index();
    public static final int ULSS = AttributesEnum.Ulss.index();
    public static final int TPSCR = AttributesEnum.Tpscr.index();
    public static final int IMPORTATO = AttributesEnum.Importato.index();
    public static final int GIORNIORARI = AttributesEnum.GiorniOrari.index();
    public static final int TELEFONO = AttributesEnum.Telefono.index();
    public static final int INTFIRMAINVITI = AttributesEnum.IntFirmaInviti.index();
    public static final int FIRMAINVITI = AttributesEnum.FirmaInviti.index();
    public static final int FIRMAREF1 = AttributesEnum.FirmaRef1.index();
    public static final int FIRMAREF2 = AttributesEnum.FirmaRef2.index();
    public static final int LUOGO = AttributesEnum.Luogo.index();
    public static final int ALTRO = AttributesEnum.Altro.index();

    /**
     *
     *  This is the default constructor (do not remove)
     */
  public A_SoOldDocsViewRowImpl()
  {
  }

  /**
   * 
   *  Gets A_SoOldDocs entity object.
   */
  public A_SoOldDocsImpl getA_SoOldDocs()
  {
    return (A_SoOldDocsImpl)getEntity(0);
  }

  /**
     *
     *  Gets the attribute value for IDALLEGATO using the alias name Idallegato
     */
    public Integer getIdallegato()
  {
        return (Integer) getAttributeInternal(IDALLEGATO);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDALLEGATO using the alias name Idallegato
   */
  public void setIdallegato(Integer value)
  {
    setAttributeInternal(IDALLEGATO, value);
  }

  /**
     *
     *  Gets the attribute value for IDDOCUMENTO using the alias name Iddocumento
     */
    public Integer getIddocumento()
  {
        return (Integer) getAttributeInternal(IDDOCUMENTO);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IDDOCUMENTO using the alias name Iddocumento
   */
  public void setIddocumento(Integer value)
  {
    setAttributeInternal(IDDOCUMENTO, value);
  }

  /**
     *
     *  Gets the attribute value for TIPOLETTERA using the alias name Tipolettera
     */
    public Integer getTipolettera()
  {
        return (Integer) getAttributeInternal(TIPOLETTERA);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TIPOLETTERA using the alias name Tipolettera
   */
  public void setTipolettera(Integer value)
  {
    setAttributeInternal(TIPOLETTERA, value);
  }

  /**
     *
     *  Gets the attribute value for CODTEMPL using the alias name Codtempl
     */
    public Integer getCodtempl()
  {
        return (Integer) getAttributeInternal(CODTEMPL);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODTEMPL using the alias name Codtempl
   */
  public void setCodtempl(Integer value)
  {
    setAttributeInternal(CODTEMPL, value);
  }

  /**
   * 
   *  Gets the attribute value for COGNOME using the alias name Cognome
   */
  public String getCognome()
  {
    return (String)getAttributeInternal(COGNOME);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for COGNOME using the alias name Cognome
   */
  public void setCognome(String value)
  {
    setAttributeInternal(COGNOME, value);
  }

  /**
   * 
   *  Gets the attribute value for NOME using the alias name Nome
   */
  public String getNome()
  {
    return (String)getAttributeInternal(NOME);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for NOME using the alias name Nome
   */
  public void setNome(String value)
  {
    setAttributeInternal(NOME, value);
  }

  /**
   * 
   *  Gets the attribute value for INDIRIZZO using the alias name Indirizzo
   */
  public String getIndirizzo()
  {
    return (String)getAttributeInternal(INDIRIZZO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for INDIRIZZO using the alias name Indirizzo
   */
  public void setIndirizzo(String value)
  {
    setAttributeInternal(INDIRIZZO, value);
  }

  /**
   * 
   *  Gets the attribute value for CAP using the alias name Cap
   */
  public String getCap()
  {
    return (String)getAttributeInternal(CAP);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CAP using the alias name Cap
   */
  public void setCap(String value)
  {
    setAttributeInternal(CAP, value);
  }

  /**
   * 
   *  Gets the attribute value for COMUNE using the alias name Comune
   */
  public String getComune()
  {
    return (String)getAttributeInternal(COMUNE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for COMUNE using the alias name Comune
   */
  public void setComune(String value)
  {
    setAttributeInternal(COMUNE, value);
  }

  /**
   * 
   *  Gets the attribute value for PROVINCIA using the alias name Provincia
   */
  public String getProvincia()
  {
    return (String)getAttributeInternal(PROVINCIA);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for PROVINCIA using the alias name Provincia
   */
  public void setProvincia(String value)
  {
    setAttributeInternal(PROVINCIA, value);
  }

  /**
   * 
   *  Gets the attribute value for CENTRO using the alias name Centro
   */
  public String getCentro()
  {
    return (String)getAttributeInternal(CENTRO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CENTRO using the alias name Centro
   */
  public void setCentro(String value)
  {
    setAttributeInternal(CENTRO, value);
  }

  /**
   * 
   *  Gets the attribute value for INDIRIZZO_CENTRO using the alias name IndirizzoCentro
   */
  public String getIndirizzoCentro()
  {
    return (String)getAttributeInternal(INDIRIZZOCENTRO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for INDIRIZZO_CENTRO using the alias name IndirizzoCentro
   */
  public void setIndirizzoCentro(String value)
  {
    setAttributeInternal(INDIRIZZOCENTRO, value);
  }

  /**
   * 
   *  Gets the attribute value for DATA_APP using the alias name DataApp
   */
  public String getDataApp()
  {
    return (String)getAttributeInternal(DATAAPP);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DATA_APP using the alias name DataApp
   */
  public void setDataApp(String value)
  {
    setAttributeInternal(DATAAPP, value);
  }

  /**
   * 
   *  Gets the attribute value for ORA_APP using the alias name OraApp
   */
  public String getOraApp()
  {
    return (String)getAttributeInternal(ORAAPP);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ORA_APP using the alias name OraApp
   */
  public void setOraApp(String value)
  {
    setAttributeInternal(ORAAPP, value);
  }

  /**
   * 
   *  Gets the attribute value for MEDICO using the alias name Medico
   */
  public String getMedico()
  {
    return (String)getAttributeInternal(MEDICO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for MEDICO using the alias name Medico
   */
  public void setMedico(String value)
  {
    setAttributeInternal(MEDICO, value);
  }

  /**
   * 
   *  Gets the attribute value for DATA_ESAME using the alias name DataEsame
   */
  public String getDataEsame()
  {
    return (String)getAttributeInternal(DATAESAME);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DATA_ESAME using the alias name DataEsame
   */
  public void setDataEsame(String value)
  {
    setAttributeInternal(DATAESAME, value);
  }

  /**
   * 
   *  Gets the attribute value for CODICE_CAMPIONE using the alias name CodiceCampione
   */
  public String getCodiceCampione()
  {
    return (String)getAttributeInternal(CODICECAMPIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODICE_CAMPIONE using the alias name CodiceCampione
   */
  public void setCodiceCampione(String value)
  {
    setAttributeInternal(CODICECAMPIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for TESSERA_SANITARIA using the alias name TesseraSanitaria
   */
  public String getTesseraSanitaria()
  {
    return (String)getAttributeInternal(TESSERASANITARIA);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TESSERA_SANITARIA using the alias name TesseraSanitaria
   */
  public void setTesseraSanitaria(String value)
  {
    setAttributeInternal(TESSERASANITARIA, value);
  }

  /**
   * 
   *  Gets the attribute value for CODICE_FISCALE using the alias name CodiceFiscale
   */
  public String getCodiceFiscale()
  {
    return (String)getAttributeInternal(CODICEFISCALE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CODICE_FISCALE using the alias name CodiceFiscale
   */
  public void setCodiceFiscale(String value)
  {
    setAttributeInternal(CODICEFISCALE, value);
  }

  /**
   * 
   *  Gets the attribute value for DATA_NASCITA using the alias name DataNascita
   */
  public String getDataNascita()
  {
    return (String)getAttributeInternal(DATANASCITA);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DATA_NASCITA using the alias name DataNascita
   */
  public void setDataNascita(String value)
  {
    setAttributeInternal(DATANASCITA, value);
  }

  /**
   * 
   *  Gets the attribute value for COGNOME_MARITO using the alias name CognomeMarito
   */
  public String getCognomeMarito()
  {
    return (String)getAttributeInternal(COGNOMEMARITO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for COGNOME_MARITO using the alias name CognomeMarito
   */
  public void setCognomeMarito(String value)
  {
    setAttributeInternal(COGNOMEMARITO, value);
  }

  /**
   * 
   *  Gets the attribute value for TELEFONO1 using the alias name Telefono1
   */
  public String getTelefono1()
  {
    return (String)getAttributeInternal(TELEFONO1);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TELEFONO1 using the alias name Telefono1
   */
  public void setTelefono1(String value)
  {
    setAttributeInternal(TELEFONO1, value);
  }

  /**
   * 
   *  Gets the attribute value for TELEFONO2 using the alias name Telefono2
   */
  public String getTelefono2()
  {
    return (String)getAttributeInternal(TELEFONO2);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TELEFONO2 using the alias name Telefono2
   */
  public void setTelefono2(String value)
  {
    setAttributeInternal(TELEFONO2, value);
  }

  /**
   * 
   *  Gets the attribute value for CENTRO_REFERTO using the alias name CentroReferto
   */
  public String getCentroReferto()
  {
    return (String)getAttributeInternal(CENTROREFERTO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for CENTRO_REFERTO using the alias name CentroReferto
   */
  public void setCentroReferto(String value)
  {
    setAttributeInternal(CENTROREFERTO, value);
  }

  /**
   * 
   *  Gets the attribute value for DATA_CREAZIONE using the alias name DataCreazione
   */
  public Date getDataCreazione()
  {
    return (Date)getAttributeInternal(DATACREAZIONE);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DATA_CREAZIONE using the alias name DataCreazione
   */
  public void setDataCreazione(Date value)
  {
    setAttributeInternal(DATACREAZIONE, value);
  }

  /**
   * 
   *  Gets the attribute value for DATA_STAMPA using the alias name DataStampa
   */
  public Date getDataStampa()
  {
    return (Date)getAttributeInternal(DATASTAMPA);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for DATA_STAMPA using the alias name DataStampa
   */
  public void setDataStampa(Date value)
  {
    setAttributeInternal(DATASTAMPA, value);
  }

  /**
     *
     *  Gets the attribute value for POSTEL using the alias name Postel
     */
    public Integer getPostel()
  {
        return (Integer) getAttributeInternal(POSTEL);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for POSTEL using the alias name Postel
   */
  public void setPostel(Integer value)
  {
    setAttributeInternal(POSTEL, value);
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
     *  Gets the attribute value for IMPORTATO using the alias name Importato
     */
    public Integer getImportato()
  {
        return (Integer) getAttributeInternal(IMPORTATO);
    }

  /**
   * 
   *  Sets <code>value</code> as attribute value for IMPORTATO using the alias name Importato
   */
  public void setImportato(Integer value)
  {
    setAttributeInternal(IMPORTATO, value);
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
   *  Gets the attribute value for GIORNI_ORARI using the alias name GiorniOrari
   */
  public String getGiorniOrari()
  {
    return (String)getAttributeInternal(GIORNIORARI);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for GIORNI_ORARI using the alias name GiorniOrari
   */
  public void setGiorniOrari(String value)
  {
    setAttributeInternal(GIORNIORARI, value);
  }

  /**
   * 
   *  Gets the attribute value for TELEFONO using the alias name Telefono
   */
  public String getTelefono()
  {
    return (String)getAttributeInternal(TELEFONO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for TELEFONO using the alias name Telefono
   */
  public void setTelefono(String value)
  {
    setAttributeInternal(TELEFONO, value);
  }

  /**
   * 
   *  Gets the attribute value for INT_FIRMA_INVITI using the alias name IntFirmaInviti
   */
  public String getIntFirmaInviti()
  {
    return (String)getAttributeInternal(INTFIRMAINVITI);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for INT_FIRMA_INVITI using the alias name IntFirmaInviti
   */
  public void setIntFirmaInviti(String value)
  {
    setAttributeInternal(INTFIRMAINVITI, value);
  }

  /**
   * 
   *  Gets the attribute value for FIRMA_INVITI using the alias name FirmaInviti
   */
  public String getFirmaInviti()
  {
    return (String)getAttributeInternal(FIRMAINVITI);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for FIRMA_INVITI using the alias name FirmaInviti
   */
  public void setFirmaInviti(String value)
  {
    setAttributeInternal(FIRMAINVITI, value);
  }

  /**
   * 
   *  Gets the attribute value for FIRMA_REF1 using the alias name FirmaRef1
   */
  public String getFirmaRef1()
  {
    return (String)getAttributeInternal(FIRMAREF1);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for FIRMA_REF1 using the alias name FirmaRef1
   */
  public void setFirmaRef1(String value)
  {
    setAttributeInternal(FIRMAREF1, value);
  }

  /**
   * 
   *  Gets the attribute value for FIRMA_REF2 using the alias name FirmaRef2
   */
  public String getFirmaRef2()
  {
    return (String)getAttributeInternal(FIRMAREF2);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for FIRMA_REF2 using the alias name FirmaRef2
   */
  public void setFirmaRef2(String value)
  {
    setAttributeInternal(FIRMAREF2, value);
  }

  /**
   * 
   *  Gets the attribute value for LUOGO using the alias name Luogo
   */
  public String getLuogo()
  {
    return (String)getAttributeInternal(LUOGO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for LUOGO using the alias name Luogo
   */
  public void setLuogo(String value)
  {
    setAttributeInternal(LUOGO, value);
  }

  /**
   * 
   *  Gets the attribute value for ALTRO using the alias name Altro
   */
  public String getAltro()
  {
    return (String)getAttributeInternal(ALTRO);
  }

  /**
   * 
   *  Sets <code>value</code> as attribute value for ALTRO using the alias name Altro
   */
  public void setAltro(String value)
  {
    setAttributeInternal(ALTRO, value);
  }


}