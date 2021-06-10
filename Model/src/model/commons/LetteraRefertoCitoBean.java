package model.commons;


/**
 * Classe che contine i dati di una lettera di referto citologico.
 * I dati del destinatario vengono ereditati dalla superclasse
 */
public class LetteraRefertoCitoBean extends LetteraBean 
{
  private String cognome_marito;
  private String telefono1;
  private String telefono2;
  private String data_esame;
  private String centro_prelievo;
  private String centro_referto;
  private String adeguatezza;
  private String infiammazione;
  private String supervisore;
  private String citoscreener;
  //16092011 GAION aggiunto campo revisore
  private String revisore;
  private String consiglio;
  private String giudizio_diagnostico;
  private String note;
  private String data_esame_prec;
  private String zona;
  private String medico;
   /*20080718 MOD: dati centro nelle stampe*/
   private String indirizzo_centro;
  private String sede_centro;
  private String orari_centro;
  private String tel_centro;
    /*20080718 FINE MOD*/
    /*20110207 mod Serra*/
 private String data_app;
   /*20110207 fine mod Serra*/
   private String codice_richiesta;
   private String codice_campione;
   private String data_creazione;
   /*03052013 GAION: progetto studio 45 mx*/
    private String mx45_braccio;
    private String mx45_codice;
    /*FINE 03052013*/
    private String test_proposto;
    private String prelievo_hpv;

    private String data_intervento;
  
   public LetteraRefertoCitoBean(String _cognome,
                      String _nome,
                      String _indirizzo,
                      String _cap,
                      String _comune,
                      String _provincia,
                      String _data_stampa,
                      String _tessera_sanitaria,
                      String _codice_fiscale,
                      String _sesso,
                      String _data_nascita,
                      String _cognome_marito,
                      String _telefono1,
                      String _telefono2,
                      String _data_esame,
                      String _centro_prelievo,
                      String _centro_referto,
                      String _adeguatezza,
                      String _infiammazione,
                      String _supervisore,
                      String _citoscreener,
                      String _revisore,
                      String _consiglio,
                      String _giudizio_diagnostico,
                      String _note,
                      String _data_esame_prec,
                      String _zona,
                      String _medico,
                      /*20080718 MOD: dati centro nelle stampe*/
                      String _indirizzo_centro,
                      String _sede_centro,
                      String _orari_centro,
                      String _tel_centro,                      
                        /*20080718 FINE MOD*/
                          /*20110207 fine mod Serra*/
                          String data_app,
                          /*20110207 fine mod Serra*/
                          //05102011 GAION aggiunti per postel
                          String _codice_richiesta,
                          String _codice_campione,
                          String _data_creazione,
                          //05102011 fine
                          String _mx45_braccio,
                          String _mx45_codice,
                          //05112014 Gaion
                          String _email,
                          String _cellulare,
                          String _test_proposto,
                          String _prelievo_hpv,
                          String _data_intervento
                          )
  {
    super(_cognome,_nome,_indirizzo,_cap,_comune,_provincia,_data_stampa,_data_nascita, _tessera_sanitaria, _codice_fiscale,_sesso,_email,_cellulare);
    this.cognome_marito=_cognome_marito;
    this.telefono1=_telefono1;
    this.telefono2=_telefono2;
    this.data_esame=_data_esame;
    this.centro_prelievo=_centro_prelievo;
    this.centro_referto=_centro_referto;
    this.adeguatezza=_adeguatezza;
    this.infiammazione=_infiammazione;
    this.supervisore=_supervisore;
    this.citoscreener=_citoscreener;
    this.revisore = _revisore;
    this.consiglio=_consiglio;
    this.giudizio_diagnostico=_giudizio_diagnostico;
    this.note=_note;
    this.data_esame_prec=_data_esame_prec;
    this.zona=_zona;
    this.medico=_medico;
     /*20080718 MOD: dati centro nelle stampe*/
      this.indirizzo_centro=_indirizzo_centro;
     this.sede_centro=_sede_centro;
     this.orari_centro=_orari_centro;
     this.tel_centro=_tel_centro;
  /*20080718 FINE MOD*/
   /*20110207 fine mod Serra*/
   this.data_app=data_app;
   /*20110207 fine mod Serra*/
   this.codice_richiesta = _codice_richiesta;
   this.codice_campione = _codice_campione;
   this.data_creazione = _data_creazione;
   this.mx45_braccio = _mx45_braccio;
   this.mx45_codice = _mx45_codice;
   this.test_proposto = _test_proposto;
   this.prelievo_hpv = _prelievo_hpv;
      this.data_intervento = _data_intervento;
  }

  public String getCognome_marito()
  {
    return cognome_marito;
  }

  public void setCognome_marito(String cognome_marito)
  {
    this.cognome_marito = cognome_marito;
  }

  public String getTelefono1()
  {
    return telefono1;
  }

  public void setTelefono1(String telefono1)
  {
    this.telefono1 = telefono1;
  }

  public String getTelefono2()
  {
    return telefono2;
  }

  public void setTelefono2(String telefono2)
  {
    this.telefono2 = telefono2;
  }

  public String getData_esame()
  {
    return data_esame;
  }

  public void setData_esame(String data_esame)
  {
    this.data_esame = data_esame;
  }

  public String getCentro_prelievo()
  {
    return centro_prelievo;
  }

  public void setCentro_prelievo(String centro_prelievo)
  {
    this.centro_prelievo = centro_prelievo;
  }

  public String getCentro_referto()
  {
    return centro_referto;
  }

  public void setCentro_referto(String centro_referto)
  {
    this.centro_referto = centro_referto;
  }

  public String getAdeguatezza()
  {
    return adeguatezza;
  }

  public void setAdeguatezza(String adeguatezza)
  {
    this.adeguatezza = adeguatezza;
  }

  public String getInfiammazione()
  {
    return infiammazione;
  }

  public void setInfiammazione(String infiammazione)
  {
    this.infiammazione = infiammazione;
  }

  public String getSupervisore()
  {
    return supervisore;
  }

  public void setSupervisore(String supervisore)
  {
    this.supervisore = supervisore;
  }

  public String getCitoscreener()
  {
    return citoscreener;
  }

  public void setCitoscreener(String citoscreener)
  {
    this.citoscreener = citoscreener;
  }

  public String getConsiglio()
  {
    return consiglio;
  }

  public void setConsiglio(String consiglio)
  {
    this.consiglio = consiglio;
  }

  public String getGiudizio_diagnostico()
  {
    return giudizio_diagnostico;
  }

  public void setGiudizio_diagnostico(String giudizio_diagnostico)
  {
    this.giudizio_diagnostico = giudizio_diagnostico;
  }

  public String getNote()
  {
    return note;
  }

  public void setNote(String note)
  {
    this.note = note;
  }

  public String getData_esame_prec()
  {
    return data_esame_prec;
  }

  public void setData_esame_prec(String data_esame_prec)
  {
    this.data_esame_prec = data_esame_prec;
  }

  public String getZona()
  {
    return zona;
  }

  public void setZona(String zona)
  {
    this.zona = zona;
  }

  public String getMedico()
  {
    return medico;
  }

  public void setMedico(String medico)
  {
    this.medico = medico;
  }
  
  /*20080718 MOD: dati centro nelle stampe*/
  
    public String getIndirizzo_centro()
  {
    return indirizzo_centro;
  }

  public void setIndirizzo_centro(String indirizzo_centro)
  {
    this.indirizzo_centro = indirizzo_centro;
  }

  public String getSede_centro()
  {
    return sede_centro;
  }

  public void setSede_centro(String sede_centro)
  {
    this.sede_centro = sede_centro;
  }

  public String getOrari_centro()
  {
    return orari_centro;
  }

  public void setOrari_centro(String orari_centro)
  {
    this.orari_centro = orari_centro;
  }

  public String getTel_centro()
  {
    return tel_centro;
  }

  public void setTel_centro(String tel_centro)
  {
    this.tel_centro = tel_centro;
  }
 /*20080718 FINE MOD*/

 /*20110207 mod Serra*/

 
  public String getData_app()
  {
    return data_app;
  }

  public void setData_app(String data_app)
  {
    this.data_app = data_app;
  }
  /*20110207 fine mod Serra*/

  // 16092011 GAION aggiunto campo revisore, codice richiesta
  public void setRevisore(String revisore)
  {
    this.revisore = revisore;
  }


  public String getRevisore()
  {
    return revisore;
  }


  public void setCodice_richiesta(String codice_richiesta)
  {
    this.codice_richiesta = codice_richiesta;
  }


  public String getCodice_richiesta()
  {
    return codice_richiesta;
  }


  public void setCodice_campione(String codice_campione)
  {
    this.codice_campione = codice_campione;
  }


  public String getCodice_campione()
  {
    return codice_campione;
  }


  public void setData_creazione(String data_creazione)
  {
    this.data_creazione = data_creazione;
  }


  public String getData_creazione()
  {
    return data_creazione;
  }


  public void setMx45_braccio(String mx45_braccio)
  {
    this.mx45_braccio = mx45_braccio;
  }


  public String getMx45_braccio()
  {
    return mx45_braccio;
  }


  public void setMx45_codice(String mx45_codice)
  {
    this.mx45_codice = mx45_codice;
  }


  public String getMx45_codice()
  {
    return mx45_codice;
  }
   //16092011  fine 

  public void setTest_proposto(String test_proposto)
  {
    this.test_proposto = test_proposto;
  }


  public String getTest_proposto()
  {
    return test_proposto;
  }


  public void setPrelievo_hpv(String prelievo_hpv)
  {
    this.prelievo_hpv = prelievo_hpv;
  }


  public String getPrelievo_hpv()
  {
    return prelievo_hpv;
  }

    public void setData_intervento(String data_intervento)
      {
        this.data_intervento = data_intervento;
      }


      public String getData_intervento()
      {
        return data_intervento;
      }

}