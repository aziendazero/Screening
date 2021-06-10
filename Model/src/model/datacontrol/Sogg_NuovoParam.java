package model.datacontrol;

public class Sogg_NuovoParam 
{
  public Sogg_NuovoParam()
  {
  }
  
    Integer regLett, stpLett;
    String codPr;
    String codClassePop;
    private boolean dirty=false;


  public void setRegLett(Integer regLett)
  {
    this.regLett = regLett;
  }


  public Integer getRegLett()
  {
    return regLett;
  }


  public void setStpLett(Integer stpLett)
  {
    this.stpLett = stpLett;
  }


  public Integer getStpLett()
  {
    return stpLett;
  }


  public void setCodPr(String codPr)
  {
    this.codPr = codPr;
  }


  public String getCodPr()
  {
    return codPr;
  }

    public void setCodClassePop(String codClassePop) {
        this.codClassePop = codClassePop;
    }

    public String getCodClassePop() {
        return codClassePop;
    }
    
    public boolean isDirty()
    {
      return dirty;
    }

    public void setDirty(boolean dirty)
    {
      this.dirty = dirty;
    }
    
    public void setDirty(String dirty)
    {
      this.dirty = new Boolean(dirty).booleanValue();
    }
}
