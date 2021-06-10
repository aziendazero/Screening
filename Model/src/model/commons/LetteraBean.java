package model.commons;


/**
 * Classe da cui ereditano i bean per tutti i tipi di lettera da spedire.
 * Contiene i dati del destinatario e la data di stampa, viene
 * utilizzata anche per la stampa delle etichette
 */
public class LetteraBean 
{
  private String cognome;
  private String nome;
  private String indirizzo;
  private String cap;
  private String comune;
  private String provincia;
  private String data_stampa;
  private String data_nascita;
  private String tessera_sanitaria;
  private String codice_fiscale;
  private String sesso;
  private String email;  //05112014 Gaion
  private String cellulare;  //05112014 Gaion

  public LetteraBean()
  {
  }
  
  public LetteraBean(String _cognome,
                      String _nome,
                      String _indirizzo,
                      String _cap,
                      String _comune,
                      String _provincia,
                      String _data_stampa,
                      String _data_nascita,
                      String _tessera_sanitaria,
                      String _codice_fiscale,
                      String _sesso,
                      String _email,
                      String _cellulare)
  {
    this.cognome=_cognome;
    this.nome=_nome;
    this.indirizzo=_indirizzo;
    this.cap=_cap;
    this.comune=_comune;
    this.provincia=_provincia;
    this.data_stampa=_data_stampa;
    this.data_nascita=_data_nascita;
    this.tessera_sanitaria=_tessera_sanitaria;
    this.codice_fiscale=_codice_fiscale; //05102011 Gaion
    this.sesso = _sesso;    //50102011 Gaion
    this.email = _email;  //05112014 Gaion
    this.cellulare = _cellulare;  //05112014 Gaion
  }

  public String getCognome()
  {
    return cognome;
  }

  public void setCognome(String cognome)
  {
    this.cognome = cognome;
  }

  public String getNome()
  {
    return nome;
  }

  public void setNome(String nome)
  {
    this.nome = nome;
  }

  public String getIndirizzo()
  {
    return indirizzo;
  }

  public void setIndirizzo(String indirizzo)
  {
    this.indirizzo = indirizzo;
  }

  public String getCap()
  {
    return cap;
  }

  public void setCap(String cap)
  {
    this.cap = cap;
  }

  public String getComune()
  {
    return comune;
  }

  public void setComune(String comune)
  {
    this.comune = comune;
  }

  public String getProvincia()
  {
    return provincia;
  }

  public void setProvincia(String provincia)
  {
    this.provincia = provincia;
  }

  public String getData_stampa()
  {
    return data_stampa;
  }

  public void setData_stampa(String data_stampa)
  {
    this.data_stampa = data_stampa;
  }

  public String getData_nascita()
  {
    return data_nascita;
  }

  public void setData_nascita(String data_nascita)
  {
    this.data_nascita = data_nascita;
  }

  public String getTessera_sanitaria()
  {
    return tessera_sanitaria;
  }

  public void setTessera_sanitaria(String tessera_sanitaria)
  {
    this.tessera_sanitaria = tessera_sanitaria;
  }
  
  // 30092011 GAION spostato qui da letterarefertocitobean
  public String getCodice_fiscale()
  {
    return codice_fiscale;
  }

  public void setCodice_fiscale(String codice_fiscale)
  {
    this.codice_fiscale = codice_fiscale;
  }


  public void setSesso(String sesso)
  {
    this.sesso = sesso;
  }


  public String getSesso()
  {
    return sesso;
  }
  // fine 30092011

  public void setEmail(String email)
  {
    this.email = email;
  }


  public String getEmail()
  {
    return email;
  }


  public void setCellulare(String cellulare)
  {
    this.cellulare = cellulare;
  }


  public String getCellulare()
  {
    return cellulare;
  }
  
}