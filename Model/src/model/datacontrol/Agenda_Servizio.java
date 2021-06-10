package model.datacontrol;

public class Agenda_Servizio 
{
  public Agenda_Servizio()
  {
  }
  
  Integer idCentro;
  Integer anno;
  Integer mese;
  String sint;
  Integer idCtRiass;


  public void setIdCentro(Integer idCentro)
  {
    this.idCentro = idCentro;
  }


  public Integer getIdCentro()
  {
    return idCentro;
  }


  public void setAnno(Integer anno)
  {
    this.anno = anno;
  }


  public Integer getAnno()
  {
    return anno;
  }


  public void setMese(Integer mese)
  {
    this.mese = mese;
  }


  public Integer getMese()
  {
    return mese;
  }


  public void setSint(String sint)
  {
    this.sint = sint;
  }


  public String getSint()
  {
    return sint;
  }


  public void setIdCtRiass(Integer idCtRiass)
  {
    this.idCtRiass = idCtRiass;
  }


  public Integer getIdCtRiass()
  {
    return idCtRiass;
  }
}
