package model.commons;


import insiel.dataHandling.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import model.common.Parent_AppModule;

import oracle.jbo.ApplicationModule;


public class SchedUtils 
{
  public static int getOre(String strFasciaEPS)
  {
    String ore = strFasciaEPS.substring(0,2);
    return Integer.parseInt(ore);
  }

  public static int getMinuti(String strFasciaEPS)
  {
    String minuti = strFasciaEPS.substring(3,5);
    return Integer.parseInt(minuti);
  }

  public static int getSecondi(String strFasciaEPS)
  {
    String secondi = strFasciaEPS.substring(6,8);
    return Integer.parseInt(secondi);
  }  
  
  public static boolean checkStringaFascia(String strFasciaEPS)
  {
    if (strFasciaEPS == null) return false;
    
    int len = strFasciaEPS.length();
    
    if (len != 8) return false;
    
    if (strFasciaEPS.charAt(2) != ':') return false;
    if (strFasciaEPS.charAt(5) != ':') return false;
    
    String ore = strFasciaEPS.substring(0,2);
    String minuti = strFasciaEPS.substring(3,5);
    String secondi = strFasciaEPS.substring(6,8);
    
    try
    {
      int h = Integer.parseInt(ore);
      if (h < 0) return false;
      if (h > 23) return false;
      
      int min = Integer.parseInt(minuti);
      if (min < 0) return false;
      if (min > 59) return false;

      int sec = Integer.parseInt(secondi);
      if (sec < 0) return false;
      if (sec > 59) return false;
      
    }
    catch (Exception ex)
    {
      return false;
    }
     
    return true; 
 
  }

  public static Date getInizioFascia() throws Exception
  {
    String inizioFasciaEPS = ConfigurationReader.readProperty("inizioFasciaEPS");
    
    if (!checkStringaFascia(inizioFasciaEPS))
    {    
      throw new Exception("errore nel formato della proprietà 'inizioFasciaEPS'");
    }
    
    Calendar cal = Calendar.getInstance();
    
    int h = getOre(inizioFasciaEPS);
    int min = getMinuti(inizioFasciaEPS);
    int sec = getSecondi(inizioFasciaEPS);
    
    cal.set(Calendar.HOUR_OF_DAY,h);
    cal.set(Calendar.MINUTE,min);
    cal.set(Calendar.SECOND,sec);
    
    return cal.getTime();
    
  }

  public static Date getFineFascia() throws Exception
  {
    String fineFasciaEPS = ConfigurationReader.readProperty("fineFasciaEPS");

    if (!checkStringaFascia(fineFasciaEPS))
    {    
      throw new Exception("errore nel formato della proprietà 'fineFasciaEPS'");
    }
    
    Calendar cal = Calendar.getInstance();
    
    int h = getOre(fineFasciaEPS);
    int min = getMinuti(fineFasciaEPS);
    int sec = getSecondi(fineFasciaEPS);
    
    cal.set(Calendar.HOUR_OF_DAY,h);
    cal.set(Calendar.MINUTE,min);
    cal.set(Calendar.SECOND,sec);
    
    return cal.getTime();
    
  }
  
  public static boolean dataInFascia(Date dataCorrente, Date inizioFascia, Date fineFascia)
  {
    int res1 = inizioFascia.compareTo(dataCorrente);
    int res2 = dataCorrente.compareTo(fineFascia);
    
    boolean ret = (res1 <= 0) && (res2 <= 0);
      
    return ret;
    
  }
  
  public static Date addDaysToDate(Date date, int days)
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE,days);
    java.util.Date newDate = cal.getTime();
    return newDate;    
  }
  
  public static boolean epsAbilitata() throws Exception
  {
    
    Date dataCorrente = new Date();

    Date inizioFascia = getInizioFascia();
    Date fineFascia = getFineFascia();
    
    if ((inizioFascia.compareTo(fineFascia)) <= 0)
    {
      return dataInFascia(dataCorrente, inizioFascia, fineFascia);
      
    }
    else
    {
    
      boolean ok1 = dataInFascia(dataCorrente, addDaysToDate(inizioFascia,-1), fineFascia);
      boolean ok2 = dataInFascia(dataCorrente, inizioFascia, addDaysToDate(fineFascia,1));
    
      return ok1 || ok2;
    }
    
    
    
            
  }
  
  public static String getServerTime()
  {
  
    SimpleDateFormat DATE_TIME_FORMATTER= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
    Date d = new Date();
    //System.out.println("date = " + d);
    String s = DATE_TIME_FORMATTER.format(d);
    //System.out.println("string = " + s);
    return s;
  }


  public static int scheduleProcess(Parent_AppModule am, String processType, 
    String ulss, String tpscr, String user)
  {
  
    String time = getServerTime();
    
    int PID = am.getNextPID().intValue();
          
    String insert = "insert into SO_CODA_PROCESSI "+
    //20110407 SErra
    "(PID,TIPO_PROCESSO,STATO,PRIORITA,ULSS,TPSCR,ERROR_MESSAGE,DATA_SCHEDULAZIONE,UTENTE_SCHEDULAZIONE,INIZIO_ESECUZIONE,FINE_ESECUZIONE)"+
    //fine
    "values (" +
      PID + ", " +
      "'" + processType + "', " +
      "'S',5, " +
      "'" + ulss + "', " +
      "'" + tpscr + "', " +
      "null, " +
      "to_date('" + time + "','dd/mm/yyyy hh24:mi:ss'), " +
      "'" + user + "', " +
      "null, null)";

    ApplicationModule am1 = (ApplicationModule) am;
      
    am1.getTransaction().executeCommand(insert);
    
    am1.getTransaction().commit();
    
    return PID;
  }
  
  public static int scheduleTimeProcess(Parent_AppModule am, String processType, 
    String ulss, String tpscr, String user, Date newDate)
  {
    SimpleDateFormat DATE_TIME_FORMATTER= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
    String s = DATE_TIME_FORMATTER.format(newDate);
    
    int PID = am.getNextPID().intValue();
          
    String insert = "insert into SO_CODA_PROCESSI "+
    "(PID,TIPO_PROCESSO,STATO,PRIORITA,ULSS,TPSCR,ERROR_MESSAGE,DATA_SCHEDULAZIONE,UTENTE_SCHEDULAZIONE,INIZIO_ESECUZIONE,FINE_ESECUZIONE)"+
    "values (" +
      PID + ", " +
      "'" + processType + "', " +
      "'S',5, " +
      "'" + ulss + "', " +
      "'" + tpscr + "', " +
      "null, " +
      "to_date('" + s + "','dd/mm/yyyy hh24:mi:ss'), " +
      "'" + user + "', " +
      "null, null)";

    ApplicationModule am1 = (ApplicationModule) am;
      
    am1.getTransaction().executeCommand(insert);
    
    am1.getTransaction().commit();
    
    return PID;
  }
  
  public static void cancelProcess(int pid, ApplicationModule am)
  {
    String delete = "delete SO_CODA_PROCESSI where PID = " + pid ;
    
    am.getTransaction().executeCommand(delete);
    
    am.getTransaction().commit();
  }

  public static void setProcessRunning(int pid, ApplicationModule am)
  {
    //System.out.println("setProcessRunning");
    
    String time = getServerTime();

    String update = "update SO_CODA_PROCESSI " +
      "set STATO = 'R', " +
      "INIZIO_ESECUZIONE = to_date('" + time + "','dd/mm/yyyy hh24:mi:ss') " +
      "where PID = " + pid ;
    
    am.getTransaction().executeCommand(update);
    
    am.getTransaction().commit();
    
  }  
  
  public static void setProcessAborted(int pid, ApplicationModule am, String errMsg)
  {
  
    int len = errMsg.length();
    
    if (len > 4000)
      errMsg = errMsg.substring(0,4000);
    
    errMsg = errMsg.replaceAll("'","''");

    String time = getServerTime();
    
    String update = "update SO_CODA_PROCESSI " +
      "set STATO = 'A', " + 
      "FINE_ESECUZIONE = to_date('" + time + "','dd/mm/yyyy hh24:mi:ss'), " + 
      "ERROR_MESSAGE = '" + errMsg + "' " +
      "where PID = " + pid ;
    
    am.getTransaction().executeCommand(update);
    
    am.getTransaction().commit();
    
  }

  public static void setProcessCompleted(int pid, ApplicationModule am)
  {
    String time = getServerTime();

    String update = "update SO_CODA_PROCESSI " +
      "set STATO = 'C', FINE_ESECUZIONE = to_date('" + time + "','dd/mm/yyyy hh24:mi:ss') " +
      "where PID = " + pid ;
    
    am.getTransaction().executeCommand(update);
    
    am.getTransaction().commit();
    
  }
  
  public static void rescheduleProcess(int pid, ApplicationModule am, Date newDate)
  {
    SimpleDateFormat DATE_TIME_FORMATTER= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");    
    String s = DATE_TIME_FORMATTER.format(newDate);
    
    String update = "update SO_CODA_PROCESSI " +
      "set DATA_SCHEDULAZIONE = to_date('" + s + "','dd/mm/yyyy hh24:mi:ss') " +
      "where PID = " + pid ;
      
    am.getTransaction().executeCommand(update);
    
    am.getTransaction().commit();
    
  }
  
  
  /** 20110407 Serra: metodo che permette di registrare il file di log
   * su fileserver, registrando il link in corrispondenza del processo relativo
   * **/
   public static void setLog(int pid, ApplicationModule am, File report) throws Exception
   {
     //legge la dir dove vanno memorizzati i file di log
     String dir=ConfigurationReader.readProperty("scheduledLogPath");
     
     File directory=new File(dir);
      if(!directory.exists())
        throw new Exception("Directory "+dir+" per la memorizzazione dei log non trovata");
      
      //copia il report di log nella dir
      String filename= directory.getAbsolutePath()+"/"+pid+"_logImportAnagrafe.pdf";
      StreamUtils.copyInputToOutput(new FileInputStream(report), new FileOutputStream(filename));
      
      //aggiorna il record del processo con il nome del file
      String update = "update SO_CODA_PROCESSI " +
      "set link_log='" +pid+"_logImportAnagrafe.pdf' "+
      "where PID = " + pid ;
    
    am.getTransaction().executeCommand(update);
    
    am.getTransaction().commit();
   }


}
