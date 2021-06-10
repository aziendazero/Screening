package view.thread;

import insiel.dataHandling.DateUtils;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import model.common.ImpExp_AppModule;

import model.commons.ConfigurationConstants;

import model.commons.SchedUtils;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;

import view.impexp.ImpexpUtils;

public class ImportPresenzeSched extends Thread
{
  ImpExp_AppModule am;
  Impexp_SoCnfImpexpViewRow r;
  String msg_;
  int pid;

  /**
   * Costruttore: cerco il record di configurazione
   * @param aPID
   * @param errors
   * @param k
   */
  public ImportPresenzeSched(Key k, HashMap errors, int aPID)
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
    }catch(Exception e)
    {
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
    Integer modalita=r.getModalita();
    try
    {
      List files = new ArrayList();     
      if(modalita == null || ConfigurationConstants.MODALITA_FTP.equals(modalita)){        
        //cerca il file su cui lavorare 
        //(se ci sono problemi viene generata un'eccezione)
        files=this.searchForFile();
        
        if(files!=null){
          for (int i = 0; i < files.size(); i++) {
          {
            File f = (File)files.get(i);
            String path = f.getAbsolutePath();
            
            //copia e rinomina nella dir virtuale
            f=ImpexpUtils.copyToDirVirtuale(r,f,am);
            
            //import di db  
            this.doImport(ulss, tpscr, tpdip);
                          
            //aggiornamento data import  
            this.updateConfig();
                                     
            //cancellazione, se serve, del file importato        
            if(r.getCancellazione()!=null && r.getCancellazione().intValue()==1 && f!=null)
            {
              f.delete();
            } else 
            {
              //rinomino il file con il suffisso _IMPORTED
              if(path!=null)
              f=new File(path);
              
              if (path.indexOf("_IMPORTED")<0){
                if (path.indexOf("_OLD")>0){
                  path = path.substring(0, path.indexOf("_OLD"));
                }
               
                path = path + "_IMPORTED";
                File old=new File(path);
                f.renameTo(old);
              }
            }
            
          }
          
          }
                  
        } 
      }
                            
       // aggiorno stato processo
       SchedUtils.setProcessCompleted(pid,am);           
        
    }
    catch (Exception e)
    {
      e.printStackTrace();
      am.getTransaction().rollback();
      SchedUtils.setProcessAborted(pid,am,e.getMessage());     
    }
    finally{
    
      try {sleep(5000);} catch (Exception ex) {};
      
      if (am != null)
        Configuration.releaseRootApplicationModule(am,true);
 
    }
  }
  
  /**
   * Metodo che cerca il file con timestamp piu' recente 
   * @throws java.lang.Exception
   * @return 
   */
   private List searchForFile() throws Exception
  {
    //cerco il file da importare nella dir locale 
    //(sempre il piu' recente, anche se dovrebbe essere unico)
      
      String localDir=r.getPosizfilearr(); 
      
      File dir=new File(localDir);
      if(!dir.exists())
        throw new Exception("Directory "+localDir+" non trovata");
      String nome=r.getNomefilearr();
      List f=new ArrayList();
      
      f=ImpexpUtils.getFileList(dir, nome,null);
      return f;
      
  }
  
  /**
   * Metodo che invoca la procedura che esegue l'import
   * @throws java.lang.Exception
   * @param ulss
   */
  private void doImport(String ulss, String tpscr, String tpdip) throws Exception
  {

      String msg=am.callImportPresenze(ulss,tpscr,null,tpdip,null);
     if(msg!=null)
          throw new Exception("Impossibile eseguire l'import dei dati anagrafici: "+msg);
    
  }
  
  /**
   * Metodo che aggiorna la data dell'ultimo import ed il report di log
   * nei dati di configurzione 
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
           "ULSS='"+r.getUlss()+"' AND "+
           "TPSCR='"+r.getTpscr()+"' ";
           
           am.getTransaction().executeCommand(update);
              
           am.getTransaction().commit();

    }
    catch(Exception ex)
    {
      am.getTransaction().rollback();
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
     //per evitare sovraccarico di connessioni al db 
    super.finalize();
  }
  
}