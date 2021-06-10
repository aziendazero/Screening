package model.commons;

import insiel.dataHandling.DateUtils;

import java.math.BigDecimal;

import java.sql.CallableStatement;
import java.sql.SQLException;

import java.util.Calendar;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.DBTransaction;

public class Progressivi 
{


  public Progressivi()
  {
  }
  
  
  private static IstanzaProgressivo getIstanzaProg
                        (ApplicationModule am, 
                        String codProg,
                        String ulss,
                        String tpscr,
                        Integer ct
                        )
                        throws Exception
  {
    String qCodice = "SELECT a.codprog, a.ulss, a.tpscr, a.dip_ct " +
      "FROM so_prog_codice a WHERE a.codprog = '" + codProg + "'" + 
      " AND a.ulss = '" + ulss + "' AND a.tpscr = '" + tpscr + "'";
    ViewObject voCod = am.createViewObjectFromQueryStmt("",qCodice);
    voCod.setRangeSize(-1);
    voCod.executeQuery();
    
    int cntCod = voCod.getRowCount();
    
    if (cntCod <= 0)
    {
      String msg = "Non è stato trovato alcun codice progressivo di nome " +
        codProg + " nella tabella SO_PROG_CODICE.";
      throw new Exception(msg);
    }
    else if (cntCod >= 2)
    {
      String msg = "E' stato trovato più di un codice progressivo di nome " +
        codProg + " nella tabella SO_PROG_CODICE.";
      throw new Exception(msg);      
    }
    
    Row codRow = voCod.first();
    Integer dipCt = codRow.getAttribute(3) !=null ? ((oracle.jbo.domain.Number) codRow.getAttribute(3)).intValue() : null;
    
    String qIst = "SELECT a.idistanza, a.uso_fascia, a.fascia_da, " +
                 "a.fascia_a, a.dip_temporale FROM so_prog_istanza a " +
                 "where a.codprog = '" + codProg + "'" +
                 " and a.ulss = '" + ulss + "'" + 
                 " and a.tpscr = '" + tpscr + "'";
    if (dipCt.intValue() == 1)
    {
      qIst += " and a.idcentro = " + ct.toString();
    }

    ViewObject voIst = am.createViewObjectFromQueryStmt("",qIst);
    voIst.setRangeSize(-1);
    voIst.executeQuery();

    int count = voIst.getRowCount();
    if (count <= 0)
    {
      String msg = "Non è stata trovata l'istanza di progressivo con i " +
                   "seguenti parametri: " +
                   "codice progressivo = " + codProg + ", " +
                   "ulss = " + ulss + ", " +
                   "tpscr = " + tpscr;
      if (dipCt.intValue() == 1)
      {
        msg += ", centro = " + ct.toString();
      }
      throw new Exception(msg);
    }

    if (count >= 2)
    {
      String msg = "Sono state trovate più istanze di progressivo con i " +
                   "seguenti parametri: " +
                   "codice progressivo = " + codProg + ", " +
                   "ulss = " + ulss + ", " +
                   "tpscr = " + tpscr;
      
      if (dipCt.intValue() == 1)
      {
        msg += ", centro = " + ct.toString();
      }
      throw new Exception(msg);
    }

    Row ist = voIst.first();
    Integer idIst = ist.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) ist.getAttribute(0)).intValue() : null;
    Integer usoF = ist.getAttribute(1) !=null ? ((oracle.jbo.domain.Number) ist.getAttribute(1)).intValue() : null;
    BigDecimal fDa = ist.getAttribute(2) !=null ? ((oracle.jbo.domain.Number) ist.getAttribute(2)).bigDecimalValue() : null;
    BigDecimal fA = ist.getAttribute(3) !=null ? ((oracle.jbo.domain.Number) ist.getAttribute(3)).bigDecimalValue() : null;
    String dpTem = (String) ist.getAttribute(4);
    
    IstanzaProgressivo istProg = new IstanzaProgressivo(idIst,usoF,fDa,fA,dpTem);
        
    return istProg;
  }
  
  
  
  
  private static BigDecimal getProgressivo
                        (ApplicationModule am,
                        IstanzaProgressivo ist,
                        Date dtRif
                        )
                        throws Exception
  {
    Integer anno = null;
    Integer mese = null;
    Integer giorno = null;
    
    if (ist.getDipTemp().equals("N"))
    {
      anno = new Integer(0);
      mese = new Integer(0);
      giorno = new Integer(0);
    }
    else
    {  
      
      if (ist.getDipTemp().equals("A"))
      {
        anno = getYear(dtRif);
        mese = new Integer(0);
        giorno = new Integer(0);
        
      }
      else if (ist.getDipTemp().equals("M"))
      {
        anno = getYear(dtRif);
        mese = getMonth(dtRif);
        giorno = new Integer(0);
        
      }
      else //if (ist.getDipTemp().equals("G"))
      {
        anno = getYear(dtRif);
        mese = getMonth(dtRif);
        giorno = getDay(dtRif);
        
      }      
    }
  
    String qUpd = "SELECT a.idistanza, a.giorno, a.mese, a.anno, a.contatore " +
                  "FROM so_prog_contatore a " + 
                  "where a.idistanza = " + ist.getIdIstanza().toString() + 
                  " and a.anno = " + anno.toString() +
                  " and a.mese = " + mese.toString() +
                  " and a.giorno = " + giorno.toString() +
                  " for update";
    ViewObject voCnt = am.createViewObjectFromQueryStmt("",qUpd);
    voCnt.setRangeSize(-1);
    voCnt.executeQuery();
    
    int numCnt = voCnt.getRowCount();
    
    BigDecimal progressivo;
    if (numCnt == 0)
    {
      if (ist.getUsoFascia().intValue() == 0)
      {
        progressivo = new BigDecimal(1);
      }
      else
      {
        progressivo = ist.getFasciaDa();
      }
      
      String ins = "insert into so_prog_contatore values (" +
                   ist.getIdIstanza().toString() + "," +
                   giorno.toString() + "," +
                   mese.toString() + "," +
                   anno.toString() + "," +
                   progressivo.toString() + "); COMMIT;";
        // am.getTransaction().executeCommand(ins);
      
        ApplicationModuleImpl amProgr = (ApplicationModuleImpl)am;
      
        CallableStatement st = null;

        try {
            String stmt = "BEGIN " + ins + " END;";

            DBTransaction tr = amProgr.getDBTransaction();
            st = tr.createCallableStatement(stmt, 1);
            st.executeUpdate();
        }finally {
            try {
                if (st != null)
                    st.close();
            } catch (SQLException s) { /* ignore */
            }
        }
      return progressivo;
                   
      
    }    
    else if (numCnt == 1)
    {
      Row rCnt = voCnt.first();
      progressivo = rCnt.getAttribute(4) !=null ? ((oracle.jbo.domain.Number) rCnt.getAttribute(4)).bigDecimalValue() : null;
      BigDecimal prog=null;
      //se non uso la fascia, parto sempre dal progressivo
      if (ist.getUsoFascia().intValue() == 0)
      {
        prog = progressivo.add(new BigDecimal(1));
      }
      else
      {//se uso la fascia verifico di non aver sfondato la soglia
       //MOD 20090320: int value non va bene per range di 10 cifre
       //   if (progressivo.intValue() < ist.getFasciaA().intValue())
        if(progressivo.compareTo(ist.getFasciaA())<0)
        {
          prog = progressivo.add(new BigDecimal(1));
        }
        else
        {
          prog = ist.getFasciaDa();
        }
      }
            
      String upd = "update so_prog_contatore set CONTATORE = " + 
                   prog.toString() + 
                   " where IDISTANZA = " + ist.getIdIstanza().toString() +
                   " and anno = " + anno.toString() +
                   " and mese = " + mese.toString() +
                   " and giorno = " + giorno.toString() + "; COMMIT;";
      //am.getTransaction().executeCommand(upd);
      
      ApplicationModuleImpl amProgr = (ApplicationModuleImpl)am;
      
        CallableStatement st = null;

        try {
            String stmt = "BEGIN " + upd + " END;";

            DBTransaction tr = amProgr.getDBTransaction();
            st = tr.createCallableStatement(stmt, 1);
            st.executeUpdate();
        }finally {
            try {
                if (st != null)
                    st.close();
            } catch (SQLException s) { /* ignore */
            }
        }
        
      return prog;
      
    }
    else
    {
      String msg = "Sono state trovate più righe di contatore per l'istanza " +
                  "di progressivo " + ist.getIdIstanza().toString() + ".";
      throw new Exception(msg);
    }
    
    
  }

  public static BigDecimal getProgressivo
                        (ApplicationModule am, 
                        String codProg,
                        String ulss,
                        String tpscr,
                        Integer ct,
                        Date dtRif
                        )
                        throws Exception
  {
    IstanzaProgressivo istProg = getIstanzaProg(am,codProg,ulss,tpscr,ct);
    BigDecimal prog = getProgressivo(am,istProg,dtRif);
    return prog;    
  }
                        
  
  private static Integer getYear(Date dtrif)
  {
    java.util.Date data = dtrif.dateValue();
    Calendar iCal = Calendar.getInstance();
    iCal.setTime(data); 
    int y = iCal.get(Calendar.YEAR);
    return new Integer(y);    
  }

  private static Integer getMonth(Date dtrif)
  {
    java.util.Date data = dtrif.dateValue();
    Calendar iCal = Calendar.getInstance();
    iCal.setTime(data); 
    int y = iCal.get(Calendar.MONTH) + 1;
    return new Integer(y);    
  }

  private static Integer getDay(Date dtrif)
  {
    java.util.Date data = dtrif.dateValue();
    Calendar iCal = Calendar.getInstance();
    iCal.setTime(data); 
    int y = iCal.get(Calendar.DAY_OF_MONTH);
    return new Integer(y);    
  }
  
  
  public static String getCodiceIdMx(
                        ApplicationModule am,
                        Date dataNasc, 
                        String cogn, 
                        String nome
                        )
                        throws Exception
  {
  
    String prefData = DateUtils.dateToString("ddMMyy",dataNasc.dateValue());
    String prefisso = prefData + cogn.substring(0,1) + nome.substring(0,1);
    
    String qCnt = "SELECT a.prefisso, a.contatore " + 
                  "FROM so_prog_cntidmx a " +
                  "WHERE a.prefisso = '" + prefisso + "' " +
                  "FOR UPDATE";
    
    ViewObject voCnt = am.createViewObjectFromQueryStmt("",qCnt);
    voCnt.setRangeSize(-1);
    voCnt.executeQuery();
    
    int numCnt = voCnt.getRowCount();
    
    BigDecimal progressivo;
    if (numCnt == 0)
    {
      progressivo = new BigDecimal(1);
      String ins = "insert into so_prog_cntidmx values (" + 
                    "'" + prefisso + "'," +
                    progressivo.toString() + ")";
      am.getTransaction().executeCommand(ins);
      return prefisso + progressivo.toString();
      
    }
    else if (numCnt == 1)
    {
      Row rCnt = voCnt.first();
      progressivo = rCnt.getAttribute(1) !=null ? ((oracle.jbo.domain.Number) rCnt.getAttribute(1)).bigDecimalValue() : null;
      BigDecimal prog = progressivo.add(new BigDecimal(1));
      
      String upd = "update so_prog_cntidmx set CONTATORE = " + prog.toString() +
                    "where PREFISSO = '" + prefisso + "'";
      am.getTransaction().executeCommand(upd);
      return prefisso + prog.toString();
      
    }
    else
    {
      String msg = "Nella tabella SO_PROG_CNTIDMX sono state trovate " +
                    "più righe corrispondenti al prefisso " + prefisso;
      throw new Exception(msg);
    }

  }
    
}