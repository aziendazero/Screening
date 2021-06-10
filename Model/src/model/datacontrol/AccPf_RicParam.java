package model.datacontrol;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import model.common.AccPf_AppModule;

import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class AccPf_RicParam 
{
  public AccPf_RicParam()
  {
    this.setNavigazione(new Boolean(false));
  }
  
  String cognome, nome;
  String codFisc, tessSan;
  Integer idCprel; 
  Boolean navigazione;
  String livello;
  BigDecimal codCampione;
  BigDecimal codRichiesta;
  String codIdSogg;
  String esito;
  String tipoDocumento;
  String codiceDocumento;
  Integer soloStorico;
  
  public void resetCampi()
  {
    this.setIdCprel(null);
      ADFContext adfCtx = ADFContext.getCurrent();
      Map session = adfCtx.getSessionScope();
    session.remove("dtInvito");
    this.setCognome(null);
    this.setNome(null);
    this.setTessSan(null);
    this.setCodFisc(null);  
    this.setLivello("1");
    this.setCodCampione(null);
    this.setCodRichiesta(null);
    this.setCodIdSogg(null);
     this.setEsito("N");
     this.setCodiceDocumento(null);
     this.setSoloStorico(null);
  }
  
  public void queryInviti()
  {
      ADFContext adfCtx = ADFContext.getCurrent();
      Map session = adfCtx.getSessionScope();
      Map req = adfCtx.getRequestScope();
    String tpscr = (String) session.get("scr");
    String ulss = (String) session.get("ulss");
    
    String whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
  
    AccPf_AppModule am = (AccPf_AppModule) BindingContext.getCurrent().
      findDataControl("AccPf_AppModuleDataControl").getDataProvider();
    ViewObject vo = am.findViewObject("AccPf_RicInvitiView1");

    Integer idCprel = this.getIdCprel();    
    if (idCprel != null)
    {
      whcl += " and IDCENTROPRELIEVO = " + idCprel.toString();
    }    

    Date dtInvito = session.get("dtInvito")!=null?(Date)session.get("dtInvito"):null;
      if(dtInvito==null)
          session.remove("dtInvito");
      else
          session.put("dtInvito",dtInvito);
    if (dtInvito != null)
    {
      whcl += " and DTAPP = to_date('" +  new SimpleDateFormat("dd/MM/yyyy").format(dtInvito) + "','dd/mm/yyyy')";
    }

    String cogn = this.getCognome();
    cogn = ViewHelper.replaceApostrophe(cogn);
    if (cogn != null && !(cogn.equals("")))
    {
      whcl += " and COGNOME LIKE '" + cogn + "%'";
    }
    
    String nome = this.getNome();
    nome = ViewHelper.replaceApostrophe(nome);
    if (nome != null && !(nome.equals("")))
    {
      whcl += " and NOME LIKE '" + nome + "%'";
    }
    
    String codFisc = this.getCodFisc();
    if (codFisc != null && !(codFisc.equals("")))
    {
      whcl += " and CF = '" + codFisc + "'";
    }
  
    String tess = this.getTessSan();
    if (tess != null && !(tess.equals("")))
    {
      whcl += " and CODTS = '" + tess + "'";
    }
    
    String lev = this.getLivello();
    BigDecimal codCamp = this.getCodCampione();
    if (lev.equals("1") && codCamp != null)
    {
      whcl += " and COD_CAMPIONE = " + codCamp.toString();
    }
    
    BigDecimal codRich = this.getCodRichiesta();
    if (codRich != null)
    {
      whcl += " and COD_RICHIESTA = " + codRich.toString();
    }

      String idmx = this.getCodIdSogg();
    if (idmx != null && !idmx.equals(""))
    {
      whcl += " and codidsogg_mx = '" + idmx + "'";
    }
    
       //filtro sui centri dell'utente
       Integer c1=(Integer)session.get("centro1liv");
        Integer c2=(Integer)session.get("centro2liv");
        String in= (String) session.get("elenco_centri");
        if(c1!=null || c2!=null)
        {//filtro i soggetti che sono associati ad un centro dell'utente oppure hanno 
        //l'ultimo invito in tale centro
          
          whcl+=" and (idcentroprelievo in "+in;
          if(c1!=null)
            whcl+=" or idcentro1liv="+c1;
          if(c2!=null)
            whcl+=" or idcentro2liv="+c2;
            
          whcl+=")";
        }
      
    String numDoc = this.getCodiceDocumento();
    if (numDoc != null && numDoc.length()>0)
    {
      if (this.getSoloStorico() != null && this.getSoloStorico().intValue() == 1)
      {
        whcl += " and CODTS IN (select CODTS AS CODTS_DOC "+
        "from so_soggetto where codts in "+
        "(SELECT COLUMN_VALUE "+
            "FROM TABLE(CAST(FUN_DOC_TROVA_CODTS_ARRAY('"+this.getTipoDocumento()+"','"+numDoc+"','"+ulss+"','S') AS VAR_ARRAY)))  "+
            "and ULSS ='"+ulss+"') ";
      } else {
        whcl += " and CODTS IN (FUN_DOC_TROVA_CODTS('"+this.getTipoDocumento()+"','"+numDoc+"','"+ulss+"')) ";
      }
    }
    
    vo.setWhereClause(whcl);
    vo.setOrderByClause("DTORAAPP,COGNOME,NOME");
    //System.out.println(vo.getQuery());
    vo.executeQuery();
    

    if(this.livello!=null)
    {
      int liv= new Integer(this.livello).intValue();
      session.put("accPrimo",new Boolean(liv == 1)); 
    }

  long count = vo.getEstimatedRowCount();
  if (count == 1)
  {
    Row first = vo.first();
    first.setAttribute("Selezionato",Boolean.TRUE);
    vo.setCurrentRow(first);
    
  }

   String qry = vo.getQuery();
  
  Object[] pars=vo.getWhereClauseParams();
  if(pars!=null)
  {
    for(int i=0;i<pars.length;i++)
    {
      qry=qry.replaceFirst(":"+(i+1),"'"+(String)pars[i]+"'");
     //System.out.println(qry);
    }
    
  }
  
  String newQry = "select codts, ulss from (" + qry + ")";
  Cnf_selectionBean clauseBean = (Cnf_selectionBean) BindingContext.getCurrent().
    findDataControl("Cnf_selectionBean").getDataProvider();      
  clauseBean.setInClause(newQry);
  clauseBean.setNote(null);
  
  }


  public void setCognome(String cognome)
  {
    this.cognome = cognome;
  }


  public String getCognome()
  {
    return cognome;
  }


  public void setNome(String nome)
  {
    this.nome = nome;
  }


  public String getNome()
  {
    return nome;
  }


  public void setCodFisc(String codFisc)
  {
    this.codFisc = codFisc;
  }


  public String getCodFisc()
  {
    return codFisc;
  }


  public void setTessSan(String tessSan)
  {
    this.tessSan = tessSan;
  }


  public String getTessSan()
  {
    return tessSan;
  }


  public void setIdCprel(Integer idCprel)
  {
    this.idCprel = idCprel;
  }


  public Integer getIdCprel()
  {
    return idCprel;
  }


  public void setNavigazione(Boolean navigazione)
  {
    this.navigazione = navigazione;
  }


  public Boolean getNavigazione()
  {
    return navigazione;
  }


  public void setLivello(String livello)
  {
    this.livello = livello;
  }


  public String getLivello()
  {
    return livello;
  }


  public void setCodCampione(BigDecimal codCampione)
  {
    this.codCampione = codCampione;
  }


  public BigDecimal getCodCampione()
  {
    return codCampione;
  }


  public void setCodRichiesta(BigDecimal codRichiesta)
  {
    this.codRichiesta = codRichiesta;
  }


  public BigDecimal getCodRichiesta()
  {
    return codRichiesta;
  }


  public void setCodIdSogg(String codIdSogg)
  {
    this.codIdSogg = codIdSogg;
  }


  public String getCodIdSogg()
  {
    return codIdSogg;
  }


  public void setEsito(String esito)
  {
    this.esito = esito;
  }


  public String getEsito()
  {
    return esito;
  }


  public void setTipoDocumento(String tipoDocumento)
  {
    this.tipoDocumento = tipoDocumento;
  }


  public String getTipoDocumento()
  {
    return tipoDocumento;
  }


  public void setCodiceDocumento(String codiceDocumento)
  {
    this.codiceDocumento = codiceDocumento;
  }


  public String getCodiceDocumento()
  {
    return codiceDocumento;
  }


  public void setSoloStorico(Integer soloStorico)
  {
    this.soloStorico = soloStorico;
  }


  public Integer getSoloStorico()
  {
    return soloStorico;
  }
}