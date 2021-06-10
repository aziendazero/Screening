package model.bean;

import insiel.dataHandling.ComparisonUtils;

public class Cnf_ulssParameters 
{
  private String intestazione;
  private String pieDiPagina;
  private String denominazione;
  private String indirizzo;
  private String telefono;
  private String giorni_orari;
  private String int_firma_inviti;
  private String firma_inviti;
  private String int_firma_ref1;
  private String firma_ref1;
  private String int_firma_ref2;
  private String firma_ref2;
  private String int_firma_lmed;
  private String firma_lmed;
  private String luogo;
  private String modified;
  private boolean dirty=false;
  private String fax;
  private String email;

  public Cnf_ulssParameters()
  {
  }
  
  public void reset()
  {
     intestazione=null;
     pieDiPagina=null;
     denominazione=null;
     indirizzo=null;
     telefono=null;
     giorni_orari=null;
     int_firma_inviti=null;
     firma_inviti=null;
     int_firma_ref1=null;
     firma_ref1=null;
     int_firma_ref2=null;
     firma_ref2=null;
     int_firma_lmed=null;
     firma_lmed=null;
     luogo=null;
     modified=null;
     fax=null;
     email=null;
     dirty=false;

  }

  public String getIntestazione()
  {
    return intestazione;
  }

  public void setIntestazione(String Intestazione)
  {
    if(!ComparisonUtils.compare(this.intestazione, Intestazione))
       this.dirty=true;
    this.intestazione = Intestazione;
  }

  public String getPieDiPagina()
  {
    return pieDiPagina;
  }

  public void setPieDiPagina(String PieDiPagina)
  {
    if(!ComparisonUtils.compare(this.pieDiPagina, PieDiPagina))
       this.dirty=true;
    this.pieDiPagina = PieDiPagina;
  }

  public String getDenominazione()
  {
    return denominazione;
  }

  public void setDenominazione(String denominazione)
  {
    if(!ComparisonUtils.compare(this.denominazione, denominazione))
       this.dirty=true;
    this.denominazione = denominazione;
  }

  public String getIndirizzo()
  {
    return indirizzo;
  }

  public void setIndirizzo(String indirizzo)
  {
    if(!ComparisonUtils.compare(this.indirizzo, indirizzo))
       this.dirty=true;
    this.indirizzo = indirizzo;
  }

  public String getTelefono()
  {
    return telefono;
  }

  public void setTelefono(String telefono)
  {
    if(!ComparisonUtils.compare(this.telefono, telefono))
       this.dirty=true;
    this.telefono = telefono;
  }

  public String getGiorni_orari()
  {
    return giorni_orari;
  }

  public void setGiorni_orari(String giorni_orari)
  {
    if(!ComparisonUtils.compare(this.giorni_orari, giorni_orari))
       this.dirty=true;
    this.giorni_orari = giorni_orari;
  }

  public String getInt_firma_inviti()
  {
    return int_firma_inviti;
  }

  public void setInt_firma_inviti(String int_firma_inviti)
  {
    if(!ComparisonUtils.compare(this.int_firma_inviti, int_firma_inviti))
       this.dirty=true;
    this.int_firma_inviti = int_firma_inviti;
  }

  public String getFirma_inviti()
  {
    return firma_inviti;
  }

  public void setFirma_inviti(String firma_inviti)
  {
    if(!ComparisonUtils.compare(this.firma_inviti, firma_inviti))
       this.dirty=true;
    this.firma_inviti = firma_inviti;
  }

  public String getInt_firma_ref1()
  {
    return int_firma_ref1;
  }

  public void setInt_firma_ref1(String int_firma_ref1)
  {
    if(!ComparisonUtils.compare(this.int_firma_ref1, int_firma_ref1))
       this.dirty=true;
    this.int_firma_ref1 = int_firma_ref1;
  }

  public String getFirma_ref1()
  {
    return firma_ref1;
  }

  public void setFirma_ref1(String firma_ref1)
  {
    if(!ComparisonUtils.compare(this.firma_ref1, firma_ref1))
       this.dirty=true;
    this.firma_ref1 = firma_ref1;
  }

  public String getInt_firma_ref2()
  {
    return int_firma_ref2;
  }

  public void setInt_firma_ref2(String int_firma_ref2)
  {
    if(!ComparisonUtils.compare(this.int_firma_ref2, int_firma_ref2))
       this.dirty=true;
    this.int_firma_ref2 = int_firma_ref2;
  }

  public String getFirma_ref2()
  {
    return firma_ref2;
  }

  public void setFirma_ref2(String firma_ref2)
  {
  if(!ComparisonUtils.compare(this.firma_ref2, firma_ref2))
       this.dirty=true;
    this.firma_ref2 = firma_ref2;
  }

  public String getInt_firma_lmed()
  {
    return int_firma_lmed;
  }

  public void setInt_firma_lmed(String int_firma_lmed)
  {
    if(!ComparisonUtils.compare(this.int_firma_lmed, int_firma_lmed))
       this.dirty=true;
    this.int_firma_lmed = int_firma_lmed;
  }

  public String getFirma_lmed()
  {
    return firma_lmed;
  }

  public void setFirma_lmed(String firma_lmed)
  {
    if(!ComparisonUtils.compare(this.firma_lmed, firma_lmed))
       this.dirty=true;
    this.firma_lmed = firma_lmed;
  }

  public String getLuogo()
  {
    return luogo;
  }

  public void setLuogo(String luogo)
  {
   if(!ComparisonUtils.compare(this.luogo, luogo))
       this.dirty=true;
    this.luogo = luogo;
  }



  public boolean isDirty()
  {
    return dirty;
  }

  public void setDirty(boolean dirty)
  {
    this.dirty = dirty;
  }
  
  public void resetDirty()
  {
    this.dirty=false;
  }

  public String getFax()
  {
    return fax;
  }

  public void setFax(String fax)
  {
     if(!ComparisonUtils.compare(this.fax, fax))
       this.dirty=true;
    this.fax = fax;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
     if(!ComparisonUtils.compare(this.email, email))
       this.dirty=true;
    this.email = email;
  }
}