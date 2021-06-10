package view.thread;

import insiel.dataHandling.DateUtils;
import insiel.dataHandling.FileUtils;
import insiel.dataHandling.StreamUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Vector;
import model.common.ImpExp_AppModule;
import model.commons.ConfigurationConstants;

import model.commons.ConfigurationReader;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import model.print.ReportHandler;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;

import view.impexp.ImpexpUtils;

public class FlussoSPSGenerator extends Thread
{
  ImpExp_AppModule am;
  Impexp_SoCnfImpexpViewRow r;
  Date dal;
  Date al;
  Integer centro_prel;
  String centro_ref;
  String prestazioni;
  Vector tozip=new Vector();
  File report,
        anagrafica,
        datiSensibili;
  String nome_file;
  String tipi_invito;
  String labor_hpv;

  public FlussoSPSGenerator(Date _dal, Date _al, Integer _centro_prel, String _lab_hpv, 
    String _centro_ref, String _prestazioni, String _tipi_invito, Key k, HashMap errors) 
  {
    try 
    {
      dal = _dal;
      al = _al;
      centro_prel = _centro_prel;
      centro_ref = _centro_ref;
      prestazioni = _prestazioni;
      tipi_invito = _tipi_invito;
      labor_hpv = _lab_hpv;
      
      //trovo e metto il lock sul record
       am = (ImpExp_AppModule)Configuration.createRootApplicationModule("model.ImpExp_AppModule","ImpExp_AppModuleLocal");
      ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
      //cerco la riga con la configurazione desiderata
      Row[] result=vo.findByKey(k,1);
    
      if(result.length==0)
        throw new NoSuchElementException("Impossibile trovare la configurazione con chiave "+k);
      r=(Impexp_SoCnfImpexpViewRow)result[0];
      
      //eseguo il lock della riga (se genera eccezione fa da solo il rollback)
      ImpexpUtils.getRowAndLock(am,r,true,false);
      
    }catch(Exception e)
    {
      errors.put("error",e.getMessage());
    }
  
  }
  
  public void run()
  {
    String ulss=r.getUlss();
    String tpscr=r.getTpscr();
    String tpdip=r.getTpdip();
    
    String dataDa = DateUtils.dateToString("yyyyMMdd",dal);
    String dataAl = DateUtils.dateToString("yyyyMMdd",al);
    
    try
    {
       
        am.logSPS(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "ATTIVATO il processo di generazione del flusso SPS per il periodo dal "+dataDa+" al "+dataAl,
              tpscr);
        
        
       this.createSPSfiles();
        
        am.logSPS(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "Generazione dei file del flusso SPS completata senza errori per il periodo dal "+dataDa+" al "+dataAl,
              tpscr);
        
  
        //genero lo zip ed il file dei sospesi
        File f=this.zipSPS();
        
        am.logSPS(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "File  pronto per l'uso",
              tpscr);
              
        //report di log
        this.createLog();
        
        am.logSPS(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "COMPLETATO il processo di generazione del flusso SPS per il periodo dal "+dataDa+" al "+dataAl,
              tpscr);
        
    }
    catch (Exception e)
    {
      e.printStackTrace();
      am.logSPS(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "Errore: "+e.getMessage(),
              tpscr);
              
      am.logSPS(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "INTERROTTO il processo di generazione del flusso SPS per l'azienda sanitaria "+
              ulss+" del periodo dal "+dataDa+" al "+dataAl,
              tpscr);
    }
    finally{
    
      for(int i=0;i<tozip.size();i++)
      {
        File g=(File)tozip.elementAt(i);
        if(g!=null)
          g.delete();
      }
      
       //unlock della configurazione
       try{
       if(r!=null)
        ImpexpUtils.getRowAndUNLock(am,r,true,false);
       }catch(Exception ex){}
        
        Configuration.releaseRootApplicationModule(am,true);
       //per evitare sovraccarico di connessioni al db (7/12/2006)
       //Configuration.releaseRootApplicationModule(am,false);
    
    }
  
    }
    
     protected void finalize() throws Throwable
  {
    if(am!=null)
      //Configuration.releaseRootApplicationModule(am,true);
     //per evitare sovraccarico di connessioni al db (7/12/2006)
     Configuration.releaseRootApplicationModule(am,false);
    super.finalize();
  }
  
  private void createSPSfiles() throws Exception
  {
    String dataDa = DateUtils.dateToString("yyyyMMdd",dal);
    String dataAl = DateUtils.dateToString("yyyyMMdd",al);
    nome_file = r.getNomefilepart()+r.getTpscr()+dataDa+"_"+dataAl;
    File virtual=new File(r.getPosizdirvirtuale());
    anagrafica=new File(virtual,nome_file+"SPA.txt");
    tozip.addElement(anagrafica);
    datiSensibili=new File(virtual,nome_file+"SPS.txt");
    tozip.addElement(datiSensibili);
    try
  {
    BigDecimal id_centro_prel = null;
    if(centro_prel!=null && centro_prel.intValue()>=0)
      id_centro_prel=new BigDecimal(centro_prel.doubleValue());
      
   //chiamo il package
   HashMap map1=am.callSPSwrite(r.getUlss(),r.getTpscr(), DateUtils.dateToString(dal), DateUtils.dateToString(al), 
            id_centro_prel, centro_ref, prestazioni, tipi_invito, labor_hpv);
    if(map1.get("error")!=null)
      throw new Exception((String)map1.get("error"));
    
    am.logSPS(//this.log_ulss,
              r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              "Completata l'estrazione del flusso SPS per il periodo dal "+dataDa+" al "+dataAl,
              r.getTpscr());
    
  } catch (Exception e)
  {
    System.out.println(e);
  }
  finally
  {
    ImpexpUtils.getRowAndUNLock(am,r,true,false);
  }
  }
  
  private File zipSPS() throws Exception
  {
   File f=null;
    try
    {
      for(int i=0;i<tozip.size();i++)
        if(!((File)tozip.elementAt(i)).exists())
           throw new Exception("Impossibile trovare il file "+
                            ((File)tozip.elementAt(i)).getAbsolutePath());
                            
      File dir=new File(r.getPosizfilearr());
      if(!dir.exists())
        throw new Exception("Impossibile trovare la directory "+r.getPosizfilearr());
      String dataDa = DateUtils.dateToString("yyyyMMdd",dal);
      String dataAl = DateUtils.dateToString("yyyyMMdd",al);
      String nome= r.getNomefilepart()+r.getTpscr()+dataDa+"_"+dataAl+".zip";
      f=new File(dir,nome );
      return FileUtils.zipFiles(tozip,f);
    }
    catch (Exception e)
    {
      if(f!=null)
        f.delete();
      throw new Exception("Impossibile creare il file zip finale: "+e.getMessage());
    }
    
  }
  
  /**
   * Metodo che crea e memorizza il report di log, diverso per ogni tipo di 
   * screening attivo
   * @throws java.lang.Exception
   */
  private void createLog() throws Exception
  {
   File pdf=null;
   HashMap map=new HashMap();
      map.put("ulss",r.getUlss());
      map.put("gruppo",r.getTpdip());
      map.put("impexp",r.getImpexp());
      map.put("data_log",DateUtils.getNow());
      map.put("luogo",r.getSuffisso());
      map.put("titolo","FLUSSO SPS"); 
      map.put("tpscr",r.getTpscr());
    try{
    
    //creazione e memorizzazione del log
      ReportHandler rh=new ReportHandler();

      pdf=rh.createReport(r,"Impexp_SoCnfImpexpView1","Template","TemplateCompilato",am.getDBConnection(),map);
      
      rh.cleanup();
      
      //legge la dir dove vanno memorizzati i file di log
     String dir=ConfigurationReader.readProperty("scheduledLogPath");
     
     File directory=new File(dir);
      if(!directory.exists())
        throw new Exception("Directory "+dir+" per la memorizzazione dei log non trovata");
      
      //copia il report di log nella dir
      String filename= directory.getAbsolutePath()+"/LOG_"+nome_file+".pdf";
      StreamUtils.copyInputToOutput(new FileInputStream(pdf), new FileOutputStream(filename));
      
      am.logSPS(r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              "Report di log per la creazione del flusso SPS per lo screening di tipo "+r.getTpscr()+" creato con successo. ",
              r.getTpscr());
    }   
    catch(Exception ex)
    {
      ex.printStackTrace();
      am.logSPS(r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              ("Impossibile creare il report di log per lo screening di tipo "+r.getTpscr()+": "+ex.getMessage()).substring(0,199),
              r.getTpscr());
    }
    finally
    {
      if(pdf!=null)
         pdf.delete();
    }
   
  }
  
}
