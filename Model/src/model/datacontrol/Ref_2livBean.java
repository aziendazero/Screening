package model.datacontrol;
import insiel.dataHandling.ComparisonUtils;

public final class Ref_2livBean 
{
  private Integer cito_giudia;
  private Integer cito_positivo;
  private Integer colpo_esito;
  private Integer istobio_grading;
  private Integer istobio_diagnosi;
  private boolean dirty=false;
  private Integer diagnosi_peggiore;
  private Integer istochir_diagnosi;
  private Integer istochir_M;
  private Integer istochir_PN;
  private Integer istochir_PT;
  private Integer limitatezza_1l;
  private String listener;
  private Integer hpv_esito;
 
  
  //parte del colon
   private Integer co_esito1liv=new Integer(0);
  

  public Ref_2livBean()
  {
  }

  public Integer getCito_giudia()
  {
    return cito_giudia;
  }

  public void setCito_giudia(Integer cito_giudia)
  {
    setDirty(!ComparisonUtils.compare(this.cito_giudia,cito_giudia));
    this.cito_giudia = cito_giudia;
  }

  public Integer getCito_positivo()
  {
    return cito_positivo;
  }

  public void setCito_positivo(Integer cito_positivo)
  {
   setDirty(!ComparisonUtils.compare(this.cito_positivo,cito_positivo));
    this.cito_positivo = cito_positivo;
  }

  public Integer getColpo_esito()
  {
    return colpo_esito;
  }

  public void setColpo_esito(Integer colpo_esito)
  {
    setDirty(!ComparisonUtils.compare(this.colpo_esito,colpo_esito));
    this.colpo_esito = colpo_esito;
  }

  public Integer getIstobio_grading()
  {
    return istobio_grading;
  }

  public void setIstobio_grading(Integer istobio_grading)
  {
    setDirty(!ComparisonUtils.compare(this.istobio_grading,istobio_grading));
    this.istobio_grading = istobio_grading;
  }

  public Integer getIstobio_diagnosi()
  {
    return istobio_diagnosi;
  }

  public void setIstobio_diagnosi(Integer istobio_diagnosi)
  {
    setDirty(!ComparisonUtils.compare(this.istobio_diagnosi,istobio_diagnosi));
    this.istobio_diagnosi = istobio_diagnosi;
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
  
  

  public Integer getDiagnosi_peggiore()
  {
    return diagnosi_peggiore;
  }

  public void setDiagnosi_peggiore(Integer diagnosi_peggiore)
  {
    setDirty(!ComparisonUtils.compare(this.diagnosi_peggiore,diagnosi_peggiore));
    this.diagnosi_peggiore = diagnosi_peggiore;
  }

  public Integer getIstochir_diagnosi()
  {
    return istochir_diagnosi;
  }

  public void setIstochir_diagnosi(Integer istochir_diagnosi)
  {
    setDirty(!ComparisonUtils.compare(this.istochir_diagnosi,istochir_diagnosi));
    this.istochir_diagnosi = istochir_diagnosi;
  }

  public Integer getIstochir_M()
  {
    return istochir_M;
  }

  public void setIstochir_M(Integer istochir_M)
  {
    setDirty(!ComparisonUtils.compare(this.istochir_M,istochir_M));
    this.istochir_M = istochir_M;
  }

  public Integer getIstochir_PN()
  {
    return istochir_PN;
  }

  public void setIstochir_PN(Integer istochir_PN)
  {
    setDirty(!ComparisonUtils.compare(this.istochir_PN,istochir_PN));
    this.istochir_PN = istochir_PN;
  }

  public Integer getIstochir_PT()
  {
    return istochir_PT;
  }

  public void setIstochir_PT(Integer istochir_PT)
  {
    setDirty(!ComparisonUtils.compare(this.istochir_PT,istochir_PT));
    this.istochir_PT = istochir_PT;
  }

  public Integer getLimitatezza_1l()
  {
    return limitatezza_1l;
  }

  public void setLimitatezza_1l(Integer limitatezza_1l)
  {
   setDirty(!ComparisonUtils.compare(this.limitatezza_1l,limitatezza_1l));
    this.limitatezza_1l = limitatezza_1l;
  }

  public String getListener()
  {
    return listener;
  }

  public void setListener(String listener)
  {
    setDirty(true);
    this.listener = listener;
  }

  public Integer getCo_esito1liv()
  {
    return co_esito1liv;
  }

  public void setCo_esito1liv(Integer co_esito1liv)
  {
    setDirty(!ComparisonUtils.compare(this.co_esito1liv,co_esito1liv));
    this.co_esito1liv = co_esito1liv;
  }


  public void setHpv_esito(Integer hpv_esito)
  {
    setDirty(!ComparisonUtils.compare(this.hpv_esito,hpv_esito));
    this.hpv_esito = hpv_esito;
  }


  public Integer getHpv_esito()
  {
    return hpv_esito;
  }
}