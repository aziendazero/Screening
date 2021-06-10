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

import view.commons.ViewHelper;
import view.impexp.ImpexpUtils;

public class DWHGenerator extends Thread
{
  ImpExp_AppModule am;
  Impexp_SoCnfImpexpViewRow r;
  String periodo;
  String anno;
  File report,
        anagrafica,
        fileEpisodi,
        fileTest;
  Vector tozip=new Vector();
  Integer test;

  public DWHGenerator(String _periodo,String _anno,
                            Integer _test_exp,  Key k,
                              HashMap errors)
  {
    try
    {
      periodo = _periodo;
      anno = _anno;
      test = _test_exp;
     
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
     // e.printStackTrace();
      errors.put("error",e.getMessage());
    }
    
  }
  
  public void run()
  {
    String ulss=r.getUlss();
    String tpscr=r.getTpscr();
    String tpdip=r.getTpdip();
    
    try
    {
       
        am.logDWH(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "ATTIVATO il processo di generazione del tracciato DWH per il periodo  "+periodo+" del "+anno,
              tpscr);
        
        
       this.createDWHfiles();
        
        am.logDWH(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "Generazione dei file del tracciato DWH completata senza errori per il periodo "+periodo+" del "+anno,
              tpscr);
        
  
        //genero lo zip ed il file dei sospesi
        File f=this.zipDWH();
        
        am.logDWH(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "File  pronto per l'uso",
              tpscr);
              
        //report di log
        this.createLog();
        
        am.logDWH(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "COMPLETATO il processo di generazione del tracciato DWH per il periodo "+periodo+" del "+anno,
              tpscr);
        
    }
    catch (Exception e)
    {
      e.printStackTrace();
      am.logDWH(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "Errore: "+e.getMessage(),
              tpscr);
              
      am.logDWH(//this.log_ulss,
              ulss,
              tpdip,
              ConfigurationConstants.IMPEXP_LOC,
              "INTERROTTO il processo di generazione del tracciato DWH per l'azienda sanitaria "+
              ulss+" del periodo "+periodo+" del "+anno,
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
  
  private void createDWHfiles() throws Exception
  {
    //data e ora di inizio dell'estrazione
    Date init=new Date();
    int esaminati=-1;
    File virtual=new File(r.getPosizdirvirtuale());
    /*report=new File(virtual,"report.txt");
    tozip.addElement(report);*/
    anagrafica=new File(virtual,r.getNomefilepart()+anno+periodo+(String)ViewHelper.decodeByTpscr(r.getTpscr(),"A","C","B",null,null)+"1.txt");
    tozip.addElement(anagrafica);
    fileEpisodi=new File(virtual,r.getNomefilepart()+anno+periodo+(String)ViewHelper.decodeByTpscr(r.getTpscr(),"A","C","B",null,null)+"2.txt");
    tozip.addElement(fileEpisodi);
    if (r.getTpscr().equals("CI"))
    {
      fileTest = new File(virtual,r.getNomefilepart()+anno+periodo+"A3.txt");
      tozip.addElement(fileTest);
    }
    try
  {
   //chiamo il package
   HashMap map1=am.callDWHwrite(r.getUlss(),r.getTpscr(),anno,periodo,test.toString());
    if(map1.get("error")!=null)
      throw new Exception((String)map1.get("error"));
    
    esaminati=((BigDecimal)map1.get("howmany")).intValue();
    if(esaminati==0) throw new Exception("Nessun dato estratto nel periodo segnalato");
    am.logDWH(//this.log_ulss,
              r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              "Completata l'estrazione dei tracciati DWH per il periodo "+periodo+" del "+anno,
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
  
  private File zipDWH() throws Exception
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
      
      String nome= r.getNomefilepart()+anno+periodo+(String)ViewHelper.decodeByTpscr(r.getTpscr(),"A","C","B",null,null)+".zip";
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
      map.put("titolo","TRACCIATO DWH"); 
      map.put("tpscr",r.getTpscr());
    try{
    
    //creazione e memorizzazione del log
      ReportHandler rh=new ReportHandler();

      pdf=rh.createReport(r,"Impexp_SoCnfImpexpView1","Template","TemplateCompilato",am.getDBConnection(),map);
      
      rh.cleanup();
      
      //r.setLogFile(BlobUtils.getBlobFromFile(pdf));
      //am.getTransaction().commit();
      
      //legge la dir dove vanno memorizzati i file di log
     String dir=ConfigurationReader.readProperty("scheduledLogPath");
     
     File directory=new File(dir);
      if(!directory.exists())
        throw new Exception("Directory "+dir+" per la memorizzazione dei log non trovata");
      
      //copia il report di log nella dir
      String filename= directory.getAbsolutePath()+"/LOG_"+r.getNomefilepart()+anno+periodo+(String)ViewHelper.decodeByTpscr(r.getTpscr(),"A","C","B",null,null)+".pdf";
      StreamUtils.copyInputToOutput(new FileInputStream(pdf), new FileOutputStream(filename));
      
      am.log(r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              "Report di log per la creazione del tracciato DWH per lo screening di tipo "+r.getTpscr()+" creato con successo. ",
              r.getTpscr());
    }   
    catch(Exception ex)
    {
      ex.printStackTrace();
      am.log(r.getUlss(),
              r.getTpdip(),
              ConfigurationConstants.IMPEXP_LOC,
              limitSize("Impossibile creare il report di log per lo screening di tipo " + r.getTpscr() + ": " + ex.getMessage()),
              r.getTpscr());
    }
    finally
    {
      if(pdf!=null)
         pdf.delete();
    }
   
  }

	private String limitSize(String msg) {
		if (msg != null) {
			if (msg.length() > 200) {
				return msg.substring(0, 199);
			}
			return msg;
		}
		return "";
	}
}