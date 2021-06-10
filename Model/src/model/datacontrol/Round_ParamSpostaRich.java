package model.datacontrol;

import java.util.Date;

public class Round_ParamSpostaRich 
{
  public Round_ParamSpostaRich()
  {
  }
  
  Integer ctPart, ctArr;
  Date dtRichDa, dtRichA;
  String codCom,desCom;

  public void reset()
  {
    this.setCodCom(null);
    this.setDesCom(null);
    this.setCtArr(null);
    this.setCtPart(null);
    this.setDtRichA(null);
    this.setDtRichDa(null);
  }

  public void setCtPart(Integer ctPart)
  {
    this.ctPart = ctPart;
  }


  public Integer getCtPart()
  {
    return ctPart;
  }


  public void setCtArr(Integer ctArr)
  {
    this.ctArr = ctArr;
  }


  public Integer getCtArr()
  {
    return ctArr;
  }


  public void setDtRichDa(Date dtRichDa)
  {
    this.dtRichDa = dtRichDa;
  }


  public Date getDtRichDa()
  {
    return dtRichDa;
  }


  public void setDtRichA(Date dtRichA)
  {
    this.dtRichA = dtRichA;
  }


  public Date getDtRichA()
  {
    return dtRichA;
  }


  public void setCodCom(String codCom)
  {
    this.codCom = codCom;
  }


  public String getCodCom()
  {
    return codCom;
  }


  public void setDesCom(String desCom)
  {
    this.desCom = desCom;
  }


  public String getDesCom()
  {
    return desCom;
  }
  
}
