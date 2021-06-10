package model.commons;

import insiel.dataHandling.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import model.common.Agenda_AppModule;

import model.datacontrol.Agenda_Servizio;
import model.datacontrol.Cnf_selectionBean;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import java.util.Locale;

public class AgendaUtils 
{
    public static oracle.jbo.domain.Date primoGLib(ApplicationModule am, 
        Integer idCt, oracle.jbo.domain.Date dtStart, String tpscr, String ulss)
      {  
        String strDate = DateUtils.dateToString(dtStart.dateValue());
        //verifico se c'� il parametro in conf
        String parametro = null;
        String confQry = "select valore_scr from SO_CNF_MAPPING_DIP where PARAMETRO = 'primo_gg_agenda' " +
          " and DIP = 'AG' and VERSO = 'LOC' and " + 
          " ulss = '"+ulss+"' and tpscr = '"+tpscr+"' and IDCENTROREF = '"+idCt+"' " ;
        ViewObject voParam = am.createViewObjectFromQueryStmt("",confQry);
        voParam.setRangeSize(-1);
        voParam.executeQuery();
        Row result = voParam.first();
        if (result != null){
          parametro = (String) result.getAttribute(0);
          oracle.jbo.domain.Date newDate = new oracle.jbo.domain.Date(dtStart);
          newDate = (oracle.jbo.domain.Date) newDate.addJulianDays(Integer.decode(parametro).intValue(),0);
          strDate = DateUtils.dateToString(newDate.dateValue());
        }
             
        String qry = "select min(DTAPP) from SO_APPUNTAMENTO where idcentro = " +
          idCt.toString() + " and DTAPP > to_date('" + strDate + "','dd/mm/yyyy')" +
          " and DISPSLOT > SLOTOCCUP";    
        ViewObject voCerca = am.createViewObjectFromQueryStmt("",qry);
        voCerca.setRangeSize(-1);
        voCerca.executeQuery();
        result = voCerca.first();
        oracle.jbo.domain.Date minDate = (oracle.jbo.domain.Date) result.getAttribute(0);
        return minDate;
      }

  public static void beforeNav(String dest)
  {
      String dst = dest==null ? "" : dest.length()>10 ? dest.substring(0,10) : dest;
    if (dst.equals("cnf_agenda"))
    {
      Agenda_Servizio bean = (Agenda_Servizio) BindingContext.getCurrent().
        findDataControl("Agenda_ServizioDataControl").getDataProvider();
      Integer idCentro = bean.getIdCentro();
      if (idCentro != null)
      {
        Cnf_selectionBean beanCFG = (Cnf_selectionBean) BindingContext.getCurrent().
          findDataControl("Cnf_selectionBean").getDataProvider();
        beanCFG.setCentro(idCentro.intValue()); 
          ADFContext adfCtx = ADFContext.getCurrent();
          Map session = adfCtx.getSessionScope();
        session.put("cnf_setCentro",Boolean.valueOf(true));
        
      }
    }
    
  }

  public static java.util.Date addDaysToDate(java.util.Date date, int days)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE,days);
    java.util.Date newDate = cal.getTime();
    return newDate;    
  }

  public static java.util.Date addMonthsToDate(java.util.Date date, int months)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.MONTH,months);
    java.util.Date newDate = cal.getTime();
    return newDate;    
  }


    public static String formatDate( java.util.Date d, String format)
    {
        DateFormat formatter =new SimpleDateFormat(format, Locale.ITALY);
         return formatter.format(d);
    }

  public static java.util.Date getDataGMA(int gg,int mm, int yy)
  {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR,yy);
    cal.set(Calendar.MONTH,mm);
    cal.set(Calendar.DAY_OF_MONTH,gg);
    return cal.getTime();
  }
  
  public static void loadSint(String tipoSint)
  {
    Agenda_AppModule am = (Agenda_AppModule) BindingContext.getCurrent().
      findDataControl("Agenda_AppModuleDataControl").getDataProvider();

    ViewObject voSint = am.findViewObject("Agenda_SintesiView1");
    ViewHelper.clearVO(voSint);
    voSint.setRangeSize(-1);

    Agenda_Servizio bean = (Agenda_Servizio) BindingContext.getCurrent().
      findDataControl("Agenda_ServizioDataControl").getDataProvider();
    
      ADFContext adfCtx = ADFContext.getCurrent();
      Map session = adfCtx.getSessionScope();
    Boolean hpvAttivo = (Boolean)session.get("HPV");

    Integer idCentro = bean.getIdCentro();
    
    if (idCentro != null)
    {
      String dtIni="",dtFin="",per="";
      
      if (tipoSint.equals("G"))
      {
          dtIni = session.get("dtAgenda")==null ? null : DateUtils.dateToString((Date) session.get("dtAgenda"));
        
        if (dtIni == null || dtIni.equals(""))
        {
          return;
        }
        
        java.util.Date jIni = getJDate(dtIni);
        java.util.Date jFin = addDaysToDate(jIni,1);
        dtFin = DateUtils.dateToString(jFin);  
        per = "giorno";
      }
      else
      {
        // mensile
        int mese = bean.getMese().intValue();
        int anno = bean.getAnno().intValue();
        java.util.Date jIni = getDataGMA(1,mese,anno);
        dtIni = DateUtils.dateToString(jIni);
        java.util.Date jFin = addMonthsToDate(jIni,1);
        dtFin = DateUtils.dateToString(jFin);        
        per = "mese";        
      }
    
        Map sess = adfCtx.getSessionScope();
      String scr = (String) sess.get("scr");
      String qryTot = "select nvl(SUM(DISPSLOT),0),nvl(SUM(SLOTOCCUP),0) ";
      qryTot += "from SO_APPUNTAMENTO ";
      qryTot += "where IDCENTRO = " + idCentro.toString() +
        " and DTAPP >= to_date('" + dtIni + "','dd/mm/yyyy')" +
        " and DTAPP < to_date('" + dtFin + "','dd/mm/yyyy')";
      ViewObject voTotali = am.createViewObjectFromQueryStmt("",qryTot);
      voTotali.setRangeSize(-1);
      voTotali.executeQuery();
      
      Row tRow = voTotali.first();
      Integer dispTot = tRow.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) tRow.getAttribute(0)).intValue() : null;
      Integer occTot = tRow.getAttribute(1) !=null ? ((oracle.jbo.domain.Number) tRow.getAttribute(1)).intValue() : null;
  
      Row nRow = voSint.createRow();
      String mess = "Numero appuntamenti del " + per + ": " + occTot.toString() +
        " su " + dispTot.toString() + " disponibili.";
      nRow.setAttribute("Messaggio",mess);
      voSint.insertRow(nRow);
      
      
     
      String qryGr = "select /*+ INDEX(SO_APPUNTAMENTO I_APPUNTAMENTO_DTAPPCENTRO) INDEX(SO_INVITO RIF_SLOT_FK) */ SO_INVITO.IDTPINVITO,SO_CNF_TPINVITO.DESCRIZIONE," +
        "SO_CNF_TPINVITO.DESCRBREVE,COUNT(*),ROUND(100 * COUNT(*) / " + 
        //20111205 Serra: correzione
   //     occTot.toString() + ",2) CNT from SO_APPUNTAMENTO,SO_INVITO," +
       (new Double(occTot.toString()).doubleValue()+0.2)+") CNT from SO_APPUNTAMENTO,SO_INVITO," +
       //20111205 fine
        "SO_CNF_TPINVITO,SO_CNF_ESITOINVITO " + 
        "WHERE SO_INVITO.IDAPP = SO_APPUNTAMENTO.IDAPP " +
        "and SO_CNF_TPINVITO.IDTPINVITO = SO_INVITO.IDTPINVITO " +
        "and SO_CNF_TPINVITO.ULSS = SO_INVITO.ULSS " +
        "and SO_CNF_TPINVITO.TPSCR = SO_INVITO.TPSCR " +
        "and SO_APPUNTAMENTO.IDCENTRO = " + idCentro.toString() +
        " and SO_APPUNTAMENTO.DTAPP >= to_date('" + dtIni + "','dd/mm/yyyy')" +
        " and SO_APPUNTAMENTO.DTAPP < to_date('" + dtFin + "','dd/mm/yyyy')" +
        " and SO_CNF_ESITOINVITO.CODESITOINVITO = SO_INVITO.CODESITOINVITO" +
        " and SO_CNF_ESITOINVITO.LIVESITO = SO_INVITO.LIVESITO" +
        " and SO_CNF_ESITOINVITO.ULSS = SO_INVITO.ULSS" +
        " and SO_CNF_ESITOINVITO.TPSCR = SO_INVITO.TPSCR" +
        " AND SO_CNF_ESITOINVITO.CODREGIONALE IN ('?','S')" +
        " GROUP BY SO_INVITO.IDTPINVITO,SO_CNF_TPINVITO.DESCRIZIONE," +
        "SO_CNF_TPINVITO.DESCRBREVE ORDER BY 1";
      ViewObject voGr = am.createViewObjectFromQueryStmt("",qryGr);
      voGr.setRangeSize(-1);
      voGr.executeQuery();
      int countGr = voGr.getRowCount();

      for (int i=0 ;i < countGr ;i++ ) 
      {
        Row cRow = voGr.getRowAtRangeIndex(i);
        Integer nApp = cRow.getAttribute(3) !=null ? ((oracle.jbo.domain.Number) cRow.getAttribute(3)).intValue() : null;
        String tipoApp = (String) cRow.getAttribute(1);
        Integer perc = cRow.getAttribute(4) !=null ? ((oracle.jbo.domain.Number) cRow.getAttribute(4)).intValue() : null;
        mess = "- n." + nApp.toString() + " appuntamenti tipo " + tipoApp +
          " (" + perc.toString() + "%)";
        nRow = voSint.createRow();
        nRow.setAttribute("Messaggio",mess);
        voSint.insertRowAtRangeIndex(1+i,nRow);
      }
        
      if (hpvAttivo.booleanValue()){
        String qryHpv = "select count(*), so_invito.test_proposto"+
        " FROM so_invito, so_appuntamento, SO_CNF_ESITOINVITO"+
        " WHERE so_invito.idapp = so_appuntamento.idapp"+
        " and SO_APPUNTAMENTO.DTAPP >= to_date('" + dtIni + "','dd/mm/yyyy')" +
        " and SO_APPUNTAMENTO.DTAPP < to_date('" + dtFin + "','dd/mm/yyyy')" +
        " and SO_APPUNTAMENTO.IDCENTRO = " + idCentro.toString() +
        " and SO_CNF_ESITOINVITO.CODESITOINVITO = SO_INVITO.CODESITOINVITO" +
        " and SO_CNF_ESITOINVITO.LIVESITO = SO_INVITO.LIVESITO" +
        " and SO_CNF_ESITOINVITO.ULSS = SO_INVITO.ULSS" +
        " and SO_CNF_ESITOINVITO.TPSCR = SO_INVITO.TPSCR" +
        " AND SO_CNF_ESITOINVITO.CODREGIONALE IN ('?','S')" +
        " group by so_invito.test_proposto ";
        
        ViewObject voNumHpv = am.createViewObjectFromQueryStmt("",qryHpv);
        voNumHpv.setRangeSize(-1);
        voNumHpv.executeQuery();
        int countHpv = voNumHpv.getRowCount();
        
        nRow = voSint.createRow();
        String messag = "Di cui:";
        nRow.setAttribute("Messaggio",messag);
        voSint.insertRowAtRangeIndex(countGr+1,nRow);
        
        for (int i=0 ;i < countHpv ;i++ ) 
        {
          Row cRow = voNumHpv.getRowAtRangeIndex(i);
          Integer nApp = cRow.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) cRow.getAttribute(0)).intValue() : null;
          String tipoApp = (String) cRow.getAttribute(1);
          mess = "- n." + nApp.toString() + " inviti con test proposto tipo " + tipoApp;
          nRow = voSint.createRow();
          nRow.setAttribute("Messaggio",mess);
          voSint.insertRowAtRangeIndex(countGr+2+i,nRow);
        }
      }
    }
    
    
    
  }

  public static void querySlot()
  {
    Agenda_AppModule am = (Agenda_AppModule) BindingContext.getCurrent().
      findDataControl("Agenda_AppModuleDataControl").getDataProvider();
    Agenda_Servizio bean = (Agenda_Servizio) BindingContext.getCurrent().
      findDataControl("Agenda_ServizioDataControl").getDataProvider();
    Integer idCentro = bean.getIdCentro();
      ADFContext adfCtx = ADFContext.getCurrent();
      Map session = adfCtx.getSessionScope();

    querySlotOnly(am, idCentro, (Date) session.get("dtAgenda"), (String) session.get("scr"));
    session.put("formerCt",idCentro);
    
    loadSint("G");
    
  }

    public static ViewObject querySlotOnly(ApplicationModule am, Integer idCentro, Date dateAgenda, String tpScr) {
        ViewObject voSlot = am.findViewObject("Agenda_SoAppuntamentoView1");

        if (idCentro == null) {
            voSlot.setWhereClause("1=2");
            voSlot.executeQuery();
        } else {
            String dtAgenda = dateAgenda == null ? null : DateUtils.dateToString(dateAgenda);
            String whcl =
                "IDCENTRO = " + idCentro.toString() + " and DTAPP >= to_date('" + dtAgenda + "','dd/mm/yyyy')" +
                " and DTAPP < to_date('" + dtAgenda + "','dd/mm/yyyy') + 1" + " and TPSRC = '" + tpScr + "'";
            voSlot.setWhereClause(whcl);
            voSlot.setOrderByClause("ORAAPP, MINAPP");
            voSlot.executeQuery();
        }
        return voSlot;
    }

    public static void queryMese() throws Exception
  {
    Agenda_AppModule am = (Agenda_AppModule) BindingContext.getCurrent().
      findDataControl("Agenda_AppModuleDataControl").getDataProvider();
    ViewObject voVisMens = am.findViewObject("Agenda_VisMensView1");    
    ViewHelper.clearVO(voVisMens);    
    Agenda_Servizio bean = (Agenda_Servizio) BindingContext.getCurrent().
      findDataControl("Agenda_ServizioDataControl").getDataProvider();
    Integer idCentro = bean.getIdCentro();
    Integer mese = bean.getMese();
    Integer anno = bean.getAnno();
    java.util.Date dtIni = getFirstDayOfMonth(mese.intValue(),anno.intValue());
    java.util.Date dtFin = getLastDayOfMonth(mese.intValue(),anno.intValue());
    java.util.Date dtIniPer = getLastMondayPrev(mese.intValue(),anno.intValue());
    java.util.Date dtFinPer = getFirstMondayNext(mese.intValue(),anno.intValue());
    
    String dtIniStr = DateUtils.dateToString(dtIni);
    String dtFinStr = DateUtils.dateToString(dtFin);
    
    int dim = diffDates(dtIniPer,dtFinPer);
    int nWeeks = dim / 7;
    
    Integer[] giornata = new Integer[dim];
    Integer[] disp = new Integer[dim];
    Integer[] occ = new Integer[dim];
    
      ADFContext adfCtx = ADFContext.getCurrent();
      Map sess = adfCtx.getSessionScope();
    String scr = (String) sess.get("scr");
    
    
    String qry = "select dtapp,giorno,sum(dispslot) disp,sum(occ) occ from (";
    qry += "SELECT idapp,DTAPP,to_number(to_char(DTAPP,'dd')) giorno,";
    qry += "DISPSLOT,SLOTOCCUP occ FROM SO_APPUNTAMENTO WHERE";
    
    if (idCentro == null)
    {
      qry += " 1=2";
    }
    else
    {
      qry += " IDCENTRO = " + idCentro.toString();
      qry += " AND DTAPP >= to_date('" + dtIniStr + "','dd/mm/yyyy') ";
      qry += " AND DTAPP < to_date('" + dtFinStr + "','dd/mm/yyyy') ";
    }
    
    qry += ") group by dtapp,giorno order by 1";

    ViewObject voDin = am.createViewObjectFromQueryStmt("",qry);
    voDin.setRangeSize(-1);
    voDin.executeQuery();
    
    int count = voDin.getRowCount();
    int iQuery = 0;
    
    Calendar calMens = Calendar.getInstance();
    calMens.setTime(dtIniPer);
    int meseCalMens;
    
    for (int iArray = 0 ; (iArray < dim) ; iArray++ ) 
    {
      meseCalMens = calMens.get(Calendar.MONTH);
      
      if (meseCalMens == mese.intValue())
      {
        int gCalMens = calMens.get(Calendar.DAY_OF_MONTH);
        
        if (iQuery < count)
        {
          Row row = voDin.getRowAtRangeIndex(iQuery);
          Integer g = row.getAttribute(1) !=null ? ((oracle.jbo.domain.Number) row.getAttribute(1)).intValue() : null;
          int gQuery = g.intValue();
          
          if (gCalMens == gQuery)
          {
            giornata[iArray] = new Integer(gCalMens);
            disp[iArray] = new Integer((row.getAttribute(2) !=null ? ((oracle.jbo.domain.Number) row.getAttribute(2)).intValue() : null).intValue());
            occ[iArray] = new Integer((row.getAttribute(3) !=null ? ((oracle.jbo.domain.Number) row.getAttribute(3)).intValue() : null).intValue());
            iQuery++;
            
          }
          else
          {
            giornata[iArray] = new Integer(gCalMens);
            disp[iArray] = new Integer(0);
            occ[iArray] = new Integer(0);          
          }
        }
        else
        {
          giornata[iArray] = new Integer(gCalMens);
          disp[iArray] = new Integer(0);
          occ[iArray] = new Integer(0);                    
        }
        
        
      }
      else
      {
        giornata[iArray] = null;
        disp[iArray] = null;
        occ[iArray] = null;
        
      }
      
      calMens.add(Calendar.DATE,1);
      
    }

    for (int iSett = nWeeks -1 ; (iSett >= 0) ; iSett--) 
    {
      Row nRow = voVisMens.createRow();
      for (int i=0 ; i<=6 ;i++ ) 
      {
        nRow.setAttribute(i,giornata[i + iSett*7]);
        nRow.setAttribute(7 + i,disp[i + iSett*7]);
        nRow.setAttribute(14 + i,occ[i + iSett*7]);        
      }
      voVisMens.insertRow(nRow);            
    }

    loadSint("M");
   
  }

  public static java.util.Date getFirstDayOfMonth(int mese, int anno)
  {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR,anno);
    cal.set(Calendar.MONTH,mese);
    cal.set(Calendar.DAY_OF_MONTH,1);
    return cal.getTime();    
  }
  
  public static java.util.Date getLastDayOfMonth(int mese, int anno)
  {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR,anno);
    cal.set(Calendar.MONTH,mese);
    cal.set(Calendar.DAY_OF_MONTH,1);
    cal.add(Calendar.MONTH,1);
    return cal.getTime();    
  }
  
  public static java.util.Date getFirstMondayNext(int mese, int anno)
  {
    Calendar iCal = Calendar.getInstance();
    java.util.Date lDay = getLastDayOfMonth(mese, anno);
    iCal.setTime(lDay);
    int dWeek = iCal.get(Calendar.DAY_OF_WEEK);
    
    while (dWeek != 2)
    {
      iCal.add(Calendar.DATE,1);
      dWeek = iCal.get(Calendar.DAY_OF_WEEK);
    }
    
    return iCal.getTime();
    
  }

  public static java.util.Date getLastMondayPrev(int mese, int anno)
  {
  
    Calendar iCal = Calendar.getInstance();
    java.util.Date fDay = getFirstDayOfMonth(mese, anno);
    iCal.setTime(fDay);
    int dWeek = iCal.get(Calendar.DAY_OF_WEEK);
    
    while (dWeek != 2)
    {
      iCal.add(Calendar.DATE,-1);
      dWeek = iCal.get(Calendar.DAY_OF_WEEK);
    }
    
    return iCal.getTime();
    
  }
  
  public static int numDaysInYear(int anno)
  {
    Calendar iCal = Calendar.getInstance();
    iCal.set(Calendar.YEAR,anno);
    iCal.set(Calendar.MONTH,11);
    iCal.set(Calendar.DAY_OF_MONTH,31);
    int lDay = iCal.get(Calendar.DAY_OF_YEAR);
    return lDay;
  }
  
  public static int remDaysInYear(int anno,java.util.Date data)
  {
    int dy = getDayOfYear(data);
    int rem = numDaysInYear(anno) - dy;
    return rem;
  }
  
  public static int getYear(java.util.Date data)
  {
    Calendar iCal = Calendar.getInstance();
    iCal.setTime(data); 
    int y = iCal.get(Calendar.YEAR);
    return y;    
  }
  
  public static int getDayOfYear(java.util.Date data)
  {
    Calendar iCal = Calendar.getInstance();
    iCal.setTime(data); 
    int dy = iCal.get(Calendar.DAY_OF_YEAR);
    return dy;
  }
  
  
  public static int diffDates(java.util.Date dtIni, java.util.Date dtFin)
  {
    int annoIni = getYear(dtIni);
    int annoFin = getYear(dtFin);
    int dyIni = getDayOfYear(dtIni);
    int dyFin = getDayOfYear(dtFin);
    if (annoIni == annoFin)
    {
      return dyFin - dyIni;
    }
    else
    {
      int diff = dyFin;
      
      for (int anno = annoFin - 1 ; anno > annoIni ; anno-- ) 
      {
        diff += numDaysInYear(anno);        
      }
      
      diff += remDaysInYear(annoIni,dtIni);
      
      return diff;
              
    }
    
  }
  
  

 /* public static void caricaAnni(DataActionContext ctx) throws Exception
  {
    Agenda_AppModule am = (Agenda_AppModule) BindingContext.getCurrent().
      findDataControl("Agenda_AppModuleDataControl").getDataProvider();
    ViewObject voAnni = am.findViewObject("Agenda_AnnoView1");
    int countVO = voAnni.getRowCount();
    if (countVO == 0) 
    {
      Calendar cal = DateUtils.getCalendar(new Date());
      cal.add(Calendar.YEAR,9);
      int annoIni = cal.get(Calendar.YEAR);      
      for (int anno = annoIni ; anno > annoIni - 15; anno--  ) 
      {
        Row nRow = voAnni.createRow();
        nRow.setAttribute("Anno",new Integer(anno));
        voAnni.insertRow(nRow);        
      }
    }
  }*/

 /* public static void caricaMesi(DataActionContext ctx) throws Exception
  {
      Agenda_AppModule am = (Agenda_AppModule) BindingContext.getCurrent().
        findDataControl("Agenda_AppModuleDataControl").getDataProvider();
      ViewObject voMesi = am.findViewObject("Agenda_MeseView1");
      int countVO = voMesi.getRowCount();
      
      if (countVO == 0)
      {
      
        Row nRow;
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(11));
        nRow.setAttribute("DesMese","Dicembre");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(10));
        nRow.setAttribute("DesMese","Novembre");
        voMesi.insertRow(nRow);
  
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(9));
        nRow.setAttribute("DesMese","Ottobre");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(8));
        nRow.setAttribute("DesMese","Settembre");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(7));
        nRow.setAttribute("DesMese","Agosto");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(6));
        nRow.setAttribute("DesMese","Luglio");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(5));
        nRow.setAttribute("DesMese","Giugno");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(4));
        nRow.setAttribute("DesMese","Maggio");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(3));
        nRow.setAttribute("DesMese","Aprile");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(2));
        nRow.setAttribute("DesMese","Marzo");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(1));
        nRow.setAttribute("DesMese","Febbraio");
        voMesi.insertRow(nRow);
        
        nRow = voMesi.createRow();
        nRow.setAttribute("CodMese",new Integer(0));
        nRow.setAttribute("DesMese","Gennaio");
        voMesi.insertRow(nRow);
      }
  }*/

  
  public static java.util.Date getJDate(String strDate)
  {
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    try
    {
      java.util.Date javaDate = formatter.parse(strDate);
      return javaDate;
    }
    catch (Exception ex)
    {
      return null;
    }    
  }

  public static void setMeseAnno() throws Exception
  {
      ADFContext adfCtx = ADFContext.getCurrent();
      Map session = adfCtx.getSessionScope();
    String dtAg = session.get("dtAgenda")==null ? null : DateUtils.dateToString((Date) session.get("dtAgenda"));
   //mod20071113
    java.util.Date jDate;
    if(dtAg==null){
       //in caso di errore imposto la data odierna
       jDate=getDateWithoutTime(new java.util.Date());
       session.put("dtAgenda",jDate);
    }
    else
    { 
    try{
        jDate = getJDate(dtAg);
        if(jDate == null){
          System.out.println("FOUND_ERROR: non convertibile in data");
          //in caso di errore imposto la data odierna
          jDate=getDateWithoutTime(new java.util.Date());
          session.put("dtAgenda",jDate);
        }
      }catch(Exception e){
        e.printStackTrace();
        System.out.println("FOUND_ERROR: dtAgenda non convertibile in data");
        jDate=getDateWithoutTime(new java.util.Date());
        session.put("dtAgenda",jDate);
      }
    }
    //java.util.Date jDate = getJDate(dtAg);
    Calendar cal = DateUtils.getCalendar(jDate);
    int anno = cal.get(Calendar.YEAR);      
    int mese = cal.get(Calendar.MONTH);      
    Agenda_Servizio bean = (Agenda_Servizio) BindingContext.getCurrent().
      findDataControl("Agenda_ServizioDataControl").getDataProvider();
    bean.setAnno(new Integer(anno));
    bean.setMese(new Integer(mese));
    
  }
  
  public static void initElencoRiass()
  {
    Agenda_AppModule am = (Agenda_AppModule) BindingContext.getCurrent().
      findDataControl("Agenda_AppModuleDataControl").getDataProvider();
    ViewObject voElenco = am.findViewObject("Agenda_AppRiassView1");
    voElenco.setWhereClause("1=2");
    
  }

  public static void queryElencoRiass()
  {
    Agenda_AppModule am = (Agenda_AppModule) BindingContext.getCurrent().
      findDataControl("Agenda_AppModuleDataControl").getDataProvider();
    ViewObject voElenco = am.findViewObject("Agenda_AppRiassView1");
    voElenco.executeQuery();
    
  }
  
  public static java.util.Date getDateWithoutTime(Date dirtyDate){
      // Get Calendar object set to the date and time of the given Date object
      Calendar cal = Calendar.getInstance();
      cal.setTime(dirtyDate);
       
      // Set time fields to zero
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
       
      // Put it back in the Date object
      return cal.getTime();
  }

  
    public static Row checkSlot(ApplicationModule am, Integer idCentro, oracle.jbo.domain.Date dataApp,
                                    Integer oraApp, Integer minApp, String tpScr) {
        ViewObject voSlot = am.findViewObject("Agenda_SoAppuntamentoView1");

        if (idCentro == null || oraApp == null || minApp == null || tpScr == null) {
            voSlot.setWhereClause("1=2");
            voSlot.executeQuery();
        } else {
            String dataAppStr = DateUtils.dateToString(dataApp.dateValue());
            String whcl =
                "IDCENTRO = " + idCentro + " " +
                "AND DTAPP = to_date('" + dataAppStr + "','DD/MM/YYYY') " +
                "AND TPSRC = '" + tpScr + "' " +
                "AND ORAAPP = " + oraApp + " " +
                "AND MINAPP = " + minApp;
            voSlot.setWhereClause(whcl);
            voSlot.executeQuery();
        }
        return voSlot.first();
    }
}
