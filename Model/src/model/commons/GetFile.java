package model.commons;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPTransferType;

import insiel.dataHandling.DateUtils;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;

import model.common.Transfer_AppModule;
import model.common.Transfer_SoCnfImpexpViewRow;

import oracle.jbo.ApplicationModule;
import oracle.jbo.JboException;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

/* GetFile
 * La thread GetFile scarica un file da un sistema remoto
 * in locale, non appena il file stesso si rende disponibile
 * Se il sistema remoto viene chiuso o disconnesso la thread
 * tenta di riconnettersi dopo un certo tempo. Una volta che il file remoto
 * � stato scaricato lo stesso viene cancellato dal sistema remoto
 */

public class GetFile extends Thread 
{
FTPClient ftpc;

String host=null;
int port;
String user=null;
String psw=null;
String tipoDip = null;
String localDir=null;
String localFile=null;
String remoteDir=null;
String remoteFile=null;
long timeout = 60*1000;
Transfer_AppModule am = null;
String azienda;
String tpscr;
String lastMsg = null;
Long timeSched;
boolean parziale;
boolean cancella;
Key key;
String suffisso;
Integer lunghezza;

 public GetFile(ApplicationModule am, Transfer_SoCnfImpexpViewRow row)
 {
   
   this.am = (Transfer_AppModule)am;
   this.azienda = row.getUlss(); 
   this.host = row.getIppart();
   Integer ftp_port=row.getFtpport();
   int port =21;
   if(ftp_port!=null)
        this.port=ftp_port.intValue();
    this.user = row.getUsrftp();
    this.psw = row.getPwdftp();
    this.tipoDip = row.getTpdip();
    this.localDir = row.getPosizfilearr();
    if(localDir.endsWith("\\") == false && localDir.endsWith("/") == false)
      localDir += File.separator;
    this.localFile = row.getNomefilearr();
    this.remoteDir = row.getPosizfilepart();
    this.remoteFile = row.getNomefilepart();
    Integer partial=row.getFileparziale();
    this.parziale = partial==null?false:Boolean.valueOf(partial.toString()).booleanValue();
    Integer oraSched = row.getOrasched();   // espresso in hhmm!
    if(oraSched != null)
        this.timeSched = new Long(oraSched.longValue());
    Integer delete=row.getCancellazioneDip();
    this.cancella=delete==null?false:Boolean.valueOf(delete.toString()).booleanValue();
    this.tpscr=row.getTpscr();
    this.key=row.getKey();
    this.suffisso=row.getSuffisso();
    this.lunghezza=row.getLunghRecord();
    
    long timeout = 60*1000;  // default 1 minuto
    Integer interv = row.getIntervallo();   // espresso in secondi!
    if(interv != null)
       this.timeout = interv.longValue() * 1000; 

    
    this.ftpc = new FTPClient();    
   
 }


 /* 
  * macano:
  * lungh_record
  * 
  * public GetFile(ApplicationModule am, String azienda, String host, int port, 
  String user, String psw, String tipoDip, String localDir, String localFile, 
  String remoteDir, String remoteFile, boolean parziale, Long timeSched, 
  boolean cancella, String tpscr, Key key, String suffisso)
  {
    istanzia(am, azienda, host, port, user, psw, tipoDip, localDir, localFile, remoteDir, remoteFile, parziale, timeSched, cancella, tpscr, key, suffisso);
  }

  public GetFile(ApplicationModule am, String azienda, String host, 
  int port, String user, String psw, String tipoDip, String localDir, 
  String localFile, String remoteDir, String remoteFile, boolean parziale, 
  Long timeSched, long timeout, boolean cancella, String tpscr, Key key, String suffisso)
  {
    istanzia(am, azienda, host, port, user, psw, tipoDip, localDir, localFile, remoteDir, remoteFile, parziale, timeSched, cancella, tpscr, key, suffisso);
    this.timeout = timeout;
      
  }*/
  
  void istanzia(ApplicationModule am, String azienda, String host, int port,
  String user, String psw, String tipoDip, String localDir, 
  String localFile, String remoteDir, String remoteFile, boolean parziale, 
  Long timeSched, boolean cancella, String tpscr, Key key, String suffisso)
  {
  
    
    this.am = (Transfer_AppModule)am;
    this.azienda = azienda;
    this.host = host;
    this.port = port;
    this.user = user;
    this.psw = psw;
    this.tipoDip = tipoDip;
    if(localDir.endsWith("\\") == false && localDir.endsWith("/") == false)
      localDir += "\\";
    this.localDir = localDir;
    this.localFile = localFile;
//    if(remoteDir.endsWith("\\") == false && remoteDir.endsWith("/") == false)
//      remoteDir += "/";
    this.remoteDir = remoteDir;
    this.remoteFile = remoteFile;
    this.parziale = parziale;
    this.timeSched = timeSched;
    this.cancella=cancella;
    this.tpscr=tpscr;
    this.key=key;
    this.suffisso=suffisso;
    
    this.ftpc = new FTPClient();    
  }

  public void run()
  {
      log(azienda, "Partenza thread per scarico da " + host);
          
      // per attivare il logging dei msg ftp :
      // FTPMessageCollector msg = new FTPMessageCollector();
      // ftpc.setMessageListener(msg);
      // usare quindi msg.getlog() per recuperare i messaggi
          
      while(true)
      {
        //se non � il momento di eseguire aspetto
        if(isToBeSched() == false)
          continue;
      
        try
        {
          // connessione
          
          ftpc.setRemoteHost(host);
      
          ftpc.setRemotePort(port);
      
          ftpc.connect();
        
          // account
          
          ftpc.login(user, psw);
        
          // controlla se � disponibile il file remoto
          
          ftpc.chdir(remoteDir);
        
          ftpc.setType(FTPTransferType.ASCII);
                  
          while(true)          
          {
            //se non � il momento di eseguire aspetto
            if(isToBeSched() == false)
              continue;          

            try
            {            

              String s = "";
              if(timeSched != null)
              {
                NumberFormat formatter = new DecimalFormat("0000");
                String stime = formatter.format(timeSched);
                s += " (schedulata per le ore " + stime.substring(0,2) + ":" + stime.substring(2) + ")";
              }

              log(azienda, "Richiesta di scarico del file " + remoteFile + (parziale ? "*" : "") + " da " + host + " in corso ..." + s);   
                    
              FTPFile[] files = null;      
                           
              try 
              {
                files = ftpc.dirDetails(remoteDir);      
              }
              catch(ParseException e)
              {
                System.out.println(e);
              }
                
              int lfile = remoteFile.length();  
                           
              for (int k = 0 ;k < files.length ;k++ ) 
              {
                String sfile = files[k].getName();
                
                if(sfile.length() < lfile)
                  continue;
                
                boolean bTrasferibile = false;
                
                //se il mnome che ho � solo l'inizio del nome del file...
                if(parziale)
                {
                  bTrasferibile = sfile.substring(0, lfile).equals(remoteFile);
                  
                  localFile = sfile;
                }
                else //se invece � il nome intero
                  bTrasferibile = sfile.equals(remoteFile);
                  
                if(bTrasferibile)                
                {
// trasferisco file remoto in locale
                
        
                  ftpc.get(localDir + localFile, sfile);
                  //nome del file dsu cui fare eventualmente il padding
                  String endFile=localDir + localFile;
                  
                  // cancello il file remoto, se il flag di cancellazione da
                  //dipartimentale � true
                 String s2="File remoto " + sfile + " scaricato in locale su " + localDir + localFile;
                 if(cancella){
                    ftpc.delete(sfile);
                    s2+=" e cancellato dal sistema remoto " + host;
                 }
        
                    log(azienda, s2);            
                    
//padding del file per arrivare alla lunghezza definitiva                  
                  if(this.lunghezza!=null && this.lunghezza.intValue()>0)
                  {
                    try{
                      Pad.doPad(endFile,lunghezza.intValue());
                      
                    }
                    catch(Exception ex)
                    {
                      ex.printStackTrace();
                      log(azienda, "Errore nell'esecuzione del padding del file " + endFile); 
                    }
                  }
        
                  lastMsg = null;
                  
                  if(parziale == false)
                  {
// Rinomino il file (ultima cosa, cos� � visibile solo quando � completo)
                    
                    File file = new File(localDir + localFile);
                   
                    DateFormat df = new SimpleDateFormat(ConfigurationConstants.IMPEXP_DATE_PATTERN);
                    String newFile = localFile + df.format(Calendar.getInstance().getTime());
                    
                    //aggiungo l'eventuale suffisso
                    if(suffisso!=null && suffisso.length()>0)
                      newFile+="_"+suffisso;
          
                    File filenew = new File(localDir + newFile);
                    
                    if (file.renameTo(filenew)){
                      log(azienda, "File locale " + localDir + localFile + " rinominato in " + localDir + newFile);            
                      endFile=filenew.getAbsolutePath();
                    }
                    else
                      log(azienda, "Errore rinominando il file locale " + localDir + localFile + " in " + localDir + newFile); 
                  }
                  
                 
                  

                  
  //devo impostare la data ora dell'ultimo trasfer
                  ViewObject vo=am.findViewObject("Transfer_SoCnfImpexpView1");
                  Row[] rows=vo.findByKey(this.key,1);
                  if(rows.length>0)
                  {
                     
                     boolean timeset=false;
                     while (!timeset){
                      try{
                        rows[0].refresh(Row.REFRESH_CONTAINEES);
                        ((Transfer_SoCnfImpexpViewRow)rows[0]).setDttransfer((oracle.jbo.domain.Date)DateUtils.getOracleDateNow());
                        am.getTransaction().commit();
                        timeset=true;
                      }
                      catch(JboException jboex)
                      {//errore di accesso, qualcun altro sta modificando la riga
                       jboex.printStackTrace();
                        try
                        { //aspetto che la risorsa siliberi
                          sleep(10000);
                        }
                        catch(InterruptedException ie)
                        {
                        }
                      }
                     }//while(!timeset)
                  }//if(rows.length>0)
                  
                }
              }             
              
              try
              {
                sleep(timeout);
              }
              catch(InterruptedException ie)
              {
              }

            }
            catch(FTPException e)
            {
            
              int code = e.getReplyCode();
            
              switch(code)
              {
                case 550: // no such file or directory
                
                  try
                  {
                    sleep(timeout);
                  }
                  catch(InterruptedException ie)
                  {
                  }
              }
            }
          }  
        }
        catch(FTPException e)
        {
          int code = e.getReplyCode();
            
          log(azienda, "Errore ftp su " + host + " .Codice: " + code);
          
          try
          {
            sleep(timeout);
          }
          catch(InterruptedException ie)
          {
          }          
          
          try
          {
            ftpc.quit();
          }
          catch(FTPException e1)
          {
          }
          catch(IOException e1)
          {
          }
        }
        catch(IOException e)
        {
          log(azienda, "Errore sul sistema remoto " + host + ": " + e.getMessage() + " Tentativo di riconnessione al sistema " + host + " in corso ...");            

          try
          {
            sleep(timeout);
          }
          catch(InterruptedException ie)
          {
          }
          try
          {
            ftpc.quit();
          }
          catch(FTPException e1)
          {
          }
          catch(IOException e1)
          {
          }
          
        }  
      }  
  }
  
  void log(String azienda, String msg)
  {
    
    if(msg.equals(lastMsg) == false)
    {
      lastMsg = msg;

      am.log(azienda, tipoDip, msg, tpscr);   
    }
    
  }
  
  /**
   * Restituisce true se la thread deve eseguire il suo lavoro adesso,
   * altrimenti apsetta un timeout e restituisce false
   * @return 
   */
  boolean isToBeSched()
  {
    if(timeSched == null)
      return true;
    
    
    DateFormat df = new SimpleDateFormat("HHmm");
    String hhmm = df.format(Calendar.getInstance().getTime()); 
    Long now = new Long(hhmm);

    boolean bsched =  (now.longValue() >= timeSched.longValue());
    
    if(bsched == false)
    {
      try
      {
        sleep(timeout);
      }
      catch(InterruptedException ie)
      {
      }
    }
    
    return bsched;  
  }
}