package view.thread;

import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.DateUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import model.common.ImpExp_AppModule;


import model.commons.ConfigurationConstants;

import model.commons.SchedUtils;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import model.print.ReportHandler;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;

import view.impexp.ImpexpUtils;


public class ImportAnagrafeSched extends Thread
{
  ImpExp_AppModule am;
  Impexp_SoCnfImpexpViewRow r;
  String msg_;
  int pid;


  /**
   * Costruttore: cerco il record di configurazione 
   * @param errors
   * @param k
   */
  public ImportAnagrafeSched(Key k, HashMap errors, int aPID)
  {
     //istanzio l'application module
    am = (ImpExp_AppModule)Configuration.createRootApplicationModule("model.ImpExp_AppModule","ImpExp_AppModuleLocal");
    pid = aPID;
    ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
    //cerco la riga con la configurazione desiderata
    Row[] result=vo.findByKey(k,1);
    
    try
    {
      if(result.length==0)
        throw new NoSuchElementException("Impossibile trovare la configurazione con chiave "+k);
      r=(Impexp_SoCnfImpexpViewRow)result[0];
            
      try      
      {
           
        if(r.getTemplate()==null)
          throw new Exception("E' necessario inserire il template per il report con il log degli errori");    
      }
        
      catch(Exception ex1)
      {
        ex1.printStackTrace();
      }
      
      
      
      
    }catch(Exception e)
    {
     // e.printStackTrace();
      errors.put("error",e.getMessage());
      if(am!=null)
        Configuration.releaseRootApplicationModule(am,true);
     
    }
   
     
  }
  
  
  
  public void run()
  {
    String ulss=r.getUlss();
    String tpscr=r.getTpscr();
    String tpdip=r.getTpdip();
    //04/08/2011 sara
    Integer modalita=r.getModalita();
    try
    {
       
        am.log(ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "IMPORT DATI ANAGRAFICI per l'azienda sanitaria "+ulss+" iniziato",
              tpscr);
              
      //04/08/2011 sara - integrazione an regionale
      File f = null;     
      if(modalita == null || ConfigurationConstants.MODALITA_FTP.equals(modalita)){        
        //cerca il file su cui lavorare 
        //(se ci sono problemi viene generata un'eccezione)
        f=this.searchForFile();
        
        if(f!=null){
        //copia e rinomina nella dir virtuale
          f=ImpexpUtils.copyToDirVirtuale(r,f,am);
          am.log(ulss,
                tpdip,
                ConfigurationConstants.IMPEXP_LOC,
                "IMPORT DATI ANAGRAFICI "+ulss+", file "+f.getName(),
                tpscr);
        }
        else
        am.log(ulss,
                tpdip,
                ConfigurationConstants.IMPEXP_LOC,
                "IMPORT DATI ANAGRAFICI "+ulss+", riutizzo ultimo file",
                tpscr);
      }        
              
      //import di db  
      this.doImport(ulss, modalita);
        
        am.log(ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "Aggiornamento dell'anagrafica per l'azienda sanitaria "+ulss+" completato. "+
              "Si procede ora all'aggiornamento delle date di import...",
              tpscr);
        
    //aggiornamento data import  per tutti gli screening
      this.updateConfig();
        
        am.log(ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "Date di import aggiornate con successo. "+
              "Si procede ora alla creazione dei report di log...",
              tpscr);
              
      //04/08/2011 sara - integrazione an regionale        
      if(modalita == null || ConfigurationConstants.MODALITA_FTP.equals(modalita)){        
        //cancellazione, se serve, del file importato        
        if(r.getCancellazione()!=null && r.getCancellazione().intValue()==1 && f!=null)
        {
          f.delete();
        }
      }        
              
      //20110407 Serra
      //report di log
     // this.createLog();
       this.createLog(tpscr);
       //fine 20110407
      
      
      
       am.log(ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "IMPORT DATI ANAGRAFICI COMPLETATO CON SUCCESSO. Operazione chiusa.",
              tpscr);
  
       // aggiorno stato processo
       SchedUtils.setProcessCompleted(pid,am);           
        
    }
    catch (Exception e)
    {
      e.printStackTrace();
      am.getTransaction().rollback();
      //20110411 Serra: anticipata la chiusura del rocesso
      SchedUtils.setProcessAborted(pid,am,e.getMessage());
      // fine
      am.log(ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "Errore: "+e.getMessage(),
              tpscr);
    
    
      am.log(ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "IMPORT DATI ANAGRAFICI INTERROTTO PER ERRORI. Operazione chiusa.",
              tpscr);
    
      
    }
    finally{
    
      try {sleep(5000);} catch (Exception ex) {};
      
      if (am != null)
        Configuration.releaseRootApplicationModule(am,true);
     //per evitare sovraccarico di connessioni al db (7/12/2006)
     //Configuration.releaseRootApplicationModule(am,false);
  
    }
  }
  
  /**
   * Metodo che invoca la procedura che esegue l'import
   * @throws java.lang.Exception
   * @param ulss
   */
  private void doImport(String ulss, Integer modalita) throws Exception
  {

      String msg=am.callImportAnagrafe(ulss,modalita.intValue());
     if(msg!=null)
          throw new Exception("Impossibile eseguire l'import dei dati anagrafici: "+msg);
    
  }
  
  /**
   * Metodo che crea e memorizza il report di log, diverso per ogni tipo di 
   * screening attivo
   * @throws java.lang.Exception
   */
   //20110407 Serra
  // private void createLog() throws Exception
  private void createLog(String tpscr) throws Exception
  //fine 20010407
  {
   File pdf=null;
   ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView2");
   //filtro i record di configurazione sul'import del'anagrafe
    vo.setWhereClause("ULSS='"+r.getUlss()+
    "' AND IMPEXP='"+r.getImpexp()+"' AND TPDIP='"+r.getTpdip()+"'");
    vo.executeQuery();
    String[] scr={"CI","CO","MA","CA","PF"};
   HashMap map=new HashMap();
      map.put("ulss",r.getUlss());
     // map.put("tpscr",r.getTpscr());
      map.put("gruppo",r.getTpdip());
      map.put("impexp",r.getImpexp());
      map.put("data_log",DateUtils.getNow());
      map.put("luogo",r.getSuffisso());
      map.put("titolo","IMPORT DATI ANAGRAFICI");
   //creo un report diverso per ognbi tipo di screening
   for(int i=0;i<scr.length;i++){
      map.put("tpscr",scr[i]);
    try{
      Row[] result=null;
      Impexp_SoCnfImpexpViewRow r1=null;
      try{
        result=vo.getFilteredRows("Tpscr",scr[i]);
        r1=(Impexp_SoCnfImpexpViewRow)result[0];
      }catch(Exception ee)
      {/*non faccio nulla, e' come se non avessi trovato nulla*/}
      
      if(r1==null)
        continue;
    
    //creazione e memorizzazione del log

      ReportHandler rh=new ReportHandler();
    //  System.out.println("creo log per "+scr[i]+" "+new Date());
      pdf=rh.createReport(r1,"Impexp_SoCnfImpexpView2","Template","TemplateCompilato",am.getDBConnection(),map);
      
      // mauro 24/05/2010
      rh.cleanup();
      // mauro 24/05/2010, fine
      
    //  System.out.println("log creato, or alo slavo"+ new Date());
      r1.setLogFile(BlobUtils.getBlobFromFile(pdf));
      
     
     
    //  System.out.println("inserito, ora faccio comit"+ new Date());
      am.getTransaction().commit();
    //  System.out.println("salvato"+ new Date());
      
      am.log(r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              "Report di log per l'aggiornamento anagrafico per lo screening di tipo "+scr[i]+" creato con successo. ",
              r.getTpscr());
              
       //20110407 Serra: se il tipo di screening coincide con chi l'ha lanciato, allora salvo anche il report
        if(scr[i].equals(tpscr))
        {
          SchedUtils.setLog(pid,am,pdf);
           am.log(r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              "Report di log per l'aggiornamento anagrafico per lo screening di tipo "+scr[i]+" salvato nella coda processi ",
              r.getTpscr());
        }
       
      //fine 20110407
    }
    //significa che non ho trovato il record, tale screening non e' attivo
    catch(NullPointerException nex){
      continue;
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      am.log(r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              "Impossibile creare il report di log per lo screening di tipo " + scr[i] + ": " + ex.getMessage(),
              r.getTpscr());
     // throw new Exception("Impossibile creare il report di log per lo screening di tipo : "+ex.getMessage());
    }
    finally
    {
      if(pdf!=null)
         pdf.delete();
    }
    
    
   }
  }
  
  /**
   * Metodo che aggiorna la data dell'ultimo import ed il report di log
   * nei dati di configurzione di tuttie tre gli screening
   * @throws java.lang.Exception
   */
  private void updateConfig() throws Exception
  {
    try{
    String pattern=DateUtils.DATE_TIME_PATTERN;
            //per i minuti il db vuole mi invece di mm
            pattern=pattern.replaceFirst("mm","mi");
            //per le 24 ore vuole HH24 invece di HH
            pattern=pattern.replaceFirst("HH","HH24");
             String update="UPDATE SO_CNF_IMPEXP SET DTIMPORT=TO_DATE('"+
             DateUtils.getNow()+"','"+pattern+"') "+
           "WHERE TPDIP='"+r.getTpdip()+"' AND "+
           "IMPEXP='"+r.getImpexp()+"' AND "+
           "ULSS='"+r.getUlss()+"' ";
           
           am.getTransaction().executeCommand(update);
           
           //aggiorno tutti i log con quello appena creato
     /*      update="UPDATE SO_CNF_IMPEXP SET LOG_FILE=("+
           "SELECT LOG_FILE FROM SO_CNF_IMPEXP I "+
           "WHERE I.PROGRULSS="+r.getProgrulss()+
           " AND I.IMPEXP='"+r.getImpexp()+
           "' AND I.TPDIP='"+r.getTpdip()+
           "' AND I.ULSS='"+r.getUlss()+
           "' AND TPSCR='"+r.getTpscr()+
           "') WHERE IMPEXP='"+r.getImpexp()+
           "' AND TPDIP='"+r.getTpdip()+
           "' AND ULSS='"+r.getUlss()+
           "' AND TPSCR<>'"+r.getTpscr()+"'";
           am.getTransaction().executeCommand(update);
       */    
           am.getTransaction().commit();

    }
    catch(Exception ex)
    {
      am.getTransaction().rollback();
      am.log(r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              ("Impossibile aggiornare le date di import: "+ex.getMessage()).substring(0,199),
              r.getTpscr());
    
    //  throw new Exception("Impossibile aggiornare le date di import: "+ex.getMessage());
    }
  }
  
  /**
   * Metodo che cerca il file con timestamp piu' recente 
   * @throws java.lang.Exception
   * @return 
   */
   private File searchForFile() throws Exception
  {
    //cerco il file da importare nella dir locale 
    //(sempre il piu' recente, anche se dovrebbe essere unico)
      
      String localDir=r.getPosizfilearr(); 
      //String localDir="d:/dati_temp/anag"; //**** test ****
      
      File dir=new File(localDir);
      if(!dir.exists())
        throw new Exception("Directory "+localDir+" non trovata");
      String nome=r.getNomefilearr();
      File f=null;
      try{
        f=ImpexpUtils.getMostRecentFile(dir, nome,ConfigurationConstants.IMPEXP_DATE_PATTERN);
        return f;
      }
      catch(FileNotFoundException ex){
      //se non ho trovato il file, non faccio nulla, semplicemente
      //rieseguo l'import sui dati gia' presenti nell'external table
        return null;
      }
  }
  
  /**
   * Questo metodo viene invocato prima della distruzione delle thread: se l'am
   * non e' ancora stato distrutto, viene annullato in questo momento
   * @throws java.lang.Throwable
   */
   protected void finalize() throws Throwable
  {
    if(am!=null)
      Configuration.releaseRootApplicationModule(am,true);
     //per evitare sovraccarico di connessioni al db (7/12/2006)
     //Configuration.releaseRootApplicationModule(am,false);
    super.finalize();
  }
}