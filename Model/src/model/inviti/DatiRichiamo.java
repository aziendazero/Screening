package model.inviti;

public class DatiRichiamo 
{
  public DatiRichiamo(Integer detCt,Integer idCt,String tp,Integer gg)
  {
    this.setDetCtRich(detCt);
    this.setIdCentroRichiamo(idCt);
    this.setTpRichiamo(tp);
    this.setGgRichiamo(gg);
  }
  
  Integer detCtRich;
  Integer idCentroRichiamo;
  String tpRichiamo;
  Integer ggRichiamo;


  public void setIdCentroRichiamo(Integer idCentroRichiamo)
  {
    this.idCentroRichiamo = idCentroRichiamo;
  }


  public Integer getIdCentroRichiamo()
  {
    return idCentroRichiamo;
  }


  public void setTpRichiamo(String tpRichiamo)
  {
    this.tpRichiamo = tpRichiamo;
  }


  public String getTpRichiamo()
  {
    return tpRichiamo;
  }


  public void setGgRichiamo(Integer ggRichiamo)
  {
    this.ggRichiamo = ggRichiamo;
  }


  public Integer getGgRichiamo()
  {
    return ggRichiamo;
  }


  public void setDetCtRich(Integer detCtRich)
  {
    this.detCtRich = detCtRich;
  }


  public Integer getDetCtRich()
  {
    return detCtRich;
  }
  
  
}