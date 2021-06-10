package model.commons;

import java.math.BigDecimal;


public class IstanzaProgressivo 
{
    private Integer idIstanza;
    private Integer usoFascia;
    private BigDecimal fasciaDa;
    private BigDecimal fasciaA;
    private String dipTemp;

  public IstanzaProgressivo(Integer idIst, Integer usoF, BigDecimal fDa, BigDecimal fA, 
    String dpTem)
  {
    this.setIdIstanza(idIst);
    this.setUsoFascia(usoF);
    this.setFasciaDa(fDa);
    this.setFasciaA(fA);
    this.setDipTemp(dpTem);
  }


  public void setIdIstanza(Integer idIstanza)
  {
    this.idIstanza = idIstanza;
  }


  public Integer getIdIstanza()
  {
    return idIstanza;
  }


  public void setUsoFascia(Integer usoFascia)
  {
    this.usoFascia = usoFascia;
  }


  public Integer getUsoFascia()
  {
    return usoFascia;
  }


  public void setFasciaDa(BigDecimal fasciaDa)
  {
    this.fasciaDa = fasciaDa;
  }


  public BigDecimal getFasciaDa()
  {
    return fasciaDa;
  }


  public void setFasciaA(BigDecimal fasciaA)
  {
    this.fasciaA = fasciaA;
  }


  public BigDecimal getFasciaA()
  {
    return fasciaA;
  }


  public void setDipTemp(String dipTemp)
  {
    this.dipTemp = dipTemp;
  }


  public String getDipTemp()
  {
    return dipTemp;
  }

}