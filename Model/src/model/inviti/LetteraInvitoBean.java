package model.inviti;

import model.commons.LetteraBean;

/**
 * Classe che fornisce i dati ad una lettera di invito. I dati del destinatario
 * vengono ereditati dalla superclasse
 */
public class LetteraInvitoBean extends LetteraBean 
{
  private String centro;
  private String indirizzo_centro;
  private String data_app;
  private String ora_app;
  private String medico;
  private String cognome_medico;
  private String nome_medico;
  private String data_esame;
  private String codice_campione;
  private String telefono;          //05102011 Gaion aggiunto per posteltxt
  private String codice_richiesta;  //05102011 Gaion aggiunto per posteltxt
  private String data_creazione;    //05102011 Gaion aggiunto per posteltxt
  private String altro;
   /*20080718 MOD: dati centro nelle stampe*/
  private String sede_centro;
  private String orari_centro;
  private String tel_centro;
    /*20080718 FINE MOD*/
    /* 02042013 GAIOn: aggiunti campi per progetto studio 45 mx*/
  private String mx45_braccio;
  private String mx45_codice;
  /* 02042013 FINE*/
  private String otp;
   
  public LetteraInvitoBean(String _cognome,
                      String _nome,
                      String _indirizzo,
                      String _cap,
                      String _comune,
                      String _provincia,
                      String _data_stampa,
                      String _centro,
                      String _indirizzo_centro,
                      String _data_app,
                      String _ora_app,
                      String _medico,
                      String _data_esame,
                      String _codice_campione,
                      String _altro,
                      String _data_nascita,
                      String _tessera_sanitaria,
                      String _codice_fiscale,
                      /*20080718 MOD: dati centro nelle stampe*/
                      String _sede_centro,
                      String _orari_centro,
                      String _tel_centro,                      
                        /*20080718 FINE MOD*/
                      /* 02042013 GAIOn: aggiunti campi per progetto studio 45 mx*/
                      String _mx45_braccio,
                      String _mx45_codice,
                      /* 02042013 FINE*/
                      //05112014 Gaion
                      String _email,
                      String _cellulare,
                      String _otp,
                      String _codice_richiesta)
  {
    // 30092011 GAION aggiunto codice ficale //
    super(_cognome,_nome,_indirizzo,_cap,_comune,_provincia,_data_stampa,_data_nascita,_tessera_sanitaria,_codice_fiscale,null,_email,_cellulare);
    this.centro=_centro;
    this.indirizzo_centro=_indirizzo_centro;
    this.data_app=_data_app;
    this.ora_app=_ora_app;
    this.medico=_medico;
    this.data_esame=_data_esame;
    this.codice_campione=_codice_campione;
    this.altro=_altro;
     /*20080718 MOD: dati centro nelle stampe*/
     this.sede_centro=_sede_centro;
     this.orari_centro=_orari_centro;
     this.tel_centro=_tel_centro;
  /*20080718 FINE MOD*/
    this.mx45_braccio = _mx45_braccio;
    this.mx45_codice = _mx45_codice;
    this.otp = _otp;
    this.codice_richiesta = _codice_richiesta;
  }
  
  //15092011 Gaion Aggiunto per posteltxt
  public LetteraInvitoBean(String _cognome,
                      String _nome,
                      String _indirizzo,
                      String _cap,
                      String _comune,
                      String _provincia,
                      String _data_stampa,
                      String _centro,
                      String _indirizzo_centro,
                      String _data_app,
                      String _ora_app,
                      String _medico,
                      String _cognome_medico,
                      String _nome_medico,
                      String _data_esame,
                      String _codice_campione,
                      String _codice_richiesta,
                      String _altro,
                      String _data_nascita,
                      String _tessera_sanitaria,
                      String _codice_fiscale,
                      String _sesso,
                      String _telefono,
                      /*20080718 MOD: dati centro nelle stampe*/
                      String _sede_centro,
                      String _orari_centro,
                      String _tel_centro,                      
                        /*20080718 FINE MOD*/
                        String _data_creazione,
                        /* 02042013 GAIOn: aggiunti campi per progetto studio 45 mx*/
                      String _mx45_braccio,
                      String _mx45_codice,
                      /* 02042013 FINE*/
                      //05112014 Gaion
                      String _email,
                      String _cellulare,
                      String _otp)
  {
    super(_cognome,_nome,_indirizzo,_cap,_comune,_provincia,_data_stampa,_data_nascita,_tessera_sanitaria,_codice_fiscale,_sesso,_email,_cellulare);
    this.centro=_centro;
    this.indirizzo_centro=_indirizzo_centro;
    this.data_app=_data_app;
    this.ora_app=_ora_app;
    this.medico=_medico;
    this.cognome_medico=_cognome_medico;
    this.nome_medico=_nome_medico;
    this.data_esame=_data_esame;
    this.codice_campione=_codice_campione;
    this.data_creazione=_data_creazione;
    this.codice_richiesta=_codice_richiesta;
    this.altro=_altro;
    this.telefono=_telefono;
     /*20080718 MOD: dati centro nelle stampe*/
     this.sede_centro=_sede_centro;
     this.orari_centro=_orari_centro;
     this.tel_centro=_tel_centro;
  /*20080718 FINE MOD*/
    this.mx45_braccio = _mx45_braccio;
    this.mx45_codice = _mx45_codice;
    this.otp = _otp;
  }

  public String getCentro()
  {
    return centro;
  }

  public void setCentro(String centro)
  {
    this.centro = centro;
  }

  public String getIndirizzo_centro()
  {
    return indirizzo_centro;
  }

  public void setIndirizzo_centro(String indirizzo_centro)
  {
    this.indirizzo_centro = indirizzo_centro;
  }

  public String getData_app()
  {
    return data_app;
  }

  public void setData_app(String data_app)
  {
    this.data_app = data_app;
  }

  public String getOra_app()
  {
    return ora_app;
  }

  public void setOra_app(String ora_app)
  {
    this.ora_app = ora_app;
  }

  public String getMedico()
  {
    return medico;
  }

  public void setMedico(String medico)
  {
    this.medico = medico;
  }





  public String getData_esame()
  {
    return data_esame;
  }

  public void setData_esame(String data_esame)
  {
    this.data_esame = data_esame;
  }

  public String getCodice_campione()
  {
    return codice_campione;
  }

  public void setCodice_campione(String codice_campione)
  {
    this.codice_campione = codice_campione;
  }

  public String getAltro()
  {
    return altro;
  }

  public void setAltro(String altro)
  {
    this.altro = altro;
  }
 /*20080718 MOD: dati centro nelle stampe*/

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


  public void setCognome_medico(String cognome_medico)
  {
    this.cognome_medico = cognome_medico;
  }


  public String getCognome_medico()
  {
    return cognome_medico;
  }


  public void setNome_medico(String nome_medico)
  {
    this.nome_medico = nome_medico;
  }


  public String getNome_medico()
  {
    return nome_medico;
  }


  public void setCodice_richiesta(String codice_richiesta)
  {
    this.codice_richiesta = codice_richiesta;
  }


  public String getCodice_richiesta()
  {
    return codice_richiesta;
  }


  public void setData_creazione(String data_creazione)
  {
    this.data_creazione = data_creazione;
  }


  public String getData_creazione()
  {
    return data_creazione;
  }


  public void setTelefono(String telefono)
  {
    this.telefono = telefono;
  }


  public String getTelefono()
  {
    return telefono;
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


  public void setOtp(String otp)
  {
    this.otp = otp;
  }


  public String getOtp()
  {
    return otp;
  }


}