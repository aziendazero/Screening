package model.datacontrol;

import java.util.Date;

import model.commons.ConfigurationConstants;



public class PL_Bean 
{
    private Integer template;
    private Date data;
    private String ordine="temporale";
    private Integer centro;
    private Date data_al;
    private String codts;
    private String type="more";
    private int export_type=ConfigurationConstants.FORMATO_PDF;
    private Integer idproc;
    private Date data_proc;
    private String nome_file;
    
    private String dalle_ore;
    private String alle_ore;
    private String codClassePop;
    private String idtpinvito;

    public void setAlle_ore(String alle_ore) {
        this.alle_ore = alle_ore;
    }

    public String getAlle_ore() {
        return alle_ore;
    }

  public PL_Bean()
  {
  }

  public Integer getTemplate()
  {
    return template;
  }

  public void setTemplate(Integer template)
  {
    this.template = template;
  }

  public Date getData()
  {
    return data;
  }

  public void setData(Date data)
  {
    this.data = data;
  }

  public String getOrdine()
  {
    return ordine;
  }

  public void setOrdine(String ordine)
  {
    this.ordine = ordine;
  }

  public Integer getCentro()
  {
    return centro;
  }

  public void setCentro(Integer centro)
  {
    this.centro = centro;
  }

  public Date getData_al()
  {
    return data_al;
  }

  public void setData_al(Date data_al)
  {
    this.data_al = data_al;
  }

  public String getCodts()
  {
    return codts;
  }

  public void setCodts(String codts)
  {
    this.codts = codts;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public int getExport_type()
  {
    return export_type;
  }

  public void setExport_type(int export_type)
  {
    this.export_type = export_type;
  }


  public void setIdproc(Integer idproc)
  {
    this.idproc = idproc;
  }


  public Integer getIdproc()
  {
    return idproc;
  }


  public void setData_proc(Date data_proc)
  {
    this.data_proc = data_proc;
  }


  public Date getData_proc()
  {
    return data_proc;
  }

    public void setNome_file(String nome_file) {
        this.nome_file = nome_file;
    }

    public String getNome_file() {
        return nome_file;
    }

    public void setDalle_ore(String dalle_ore) {
        this.dalle_ore = dalle_ore;
    }

    public String getDalle_ore() {
        return dalle_ore;
    }

    public void setCodClassePop(String codClassePop) {
        this.codClassePop = codClassePop;
    }

    public String getCodClassePop() {
        return codClassePop;
    }

    public void setIdtpinvito(String idtpinvito) {
        this.idtpinvito = idtpinvito;
    }

    public String getIdtpinvito() {
        return idtpinvito;
    }
}
