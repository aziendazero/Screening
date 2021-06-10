package view.statistiche;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.bean.ImpexpFile;
import model.bean.ImpexpProvider;

import model.common.ImpExp_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ConfigurationReader;

import model.datacontrol.Stats_paramBean;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.adf.view.rich.component.rich.output.RichOutputText;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.quartz.Job;

import view.commons.action.Parent_DataForwardAction;

import view.impexp.ImpexpUtils;

import view.thread.DWHGenerator;

public class Stats_tracciatoDWHAction extends Parent_DataForwardAction {

    private RichForm dwHFrm;
    private RichTable zipTable;
    private RichOutputText zipFileName;

    @Override
    protected void setAppModule() {
        this.amName = "ImpExp_AppModule";
    }
    
    List<ImpexpFile> zipFiles = new ArrayList();
    
    /**
     * Cancella il file selezionato
     * @param ctx
     */
    public String onFileDelete() {

        try {
                Map session = ADFContext.getCurrent().getSessionScope();
                String directory = (String) session.get("impexp_localdir");
                File fileZip = new File(directory + "/" + zipFileName.getValue().toString() + ".zip");
                fileZip.delete();
            
                //cancellazione file di log   
                String filename=zipFileName.getValue().toString();
                filename = "LOG_"+filename+".pdf";
                filename=ConfigurationReader.readProperty("scheduledLogPath")+"/"+filename.trim();
                File pdf=new File(filename);
                if (pdf != null)
                    pdf.delete();
            
            this.onRefeshFiles();

        } catch (Exception e) {
            this.handleException(e.getMessage(), null);
        }

        return null;
    }
      
      /**
       * 
       * @throws java.lang.Exception
       * @param ctx
       */
      public void onGenerate(ActionEvent actionEvent) {
        
         ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
         ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
         Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
         Stats_paramBean bean=(Stats_paramBean)BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
         
         try
         {
               
           if(r==null)
             throw new Exception("Non e' stata selezionata nessuna ulss oppure la ulss selezionata non presenta i dati di configurazione per eseguire tale operazione.");
         
         
          if (bean.getDWH_anno() == null || bean.getDWH_anno().length()==0)
          {
            throw new Exception("Inserire gli estremi del periodo temporale");
          }
          String pattern ="(19|20)\\d\\d";
          if (bean.getDWH_anno().length()>4 || !bean.getDWH_anno().matches(pattern))
          {
            throw new Exception("L'anno deve rispettare il formato previsto (yyyy)");
          }
          
          String periodo;
          if (bean.getDWH_periodo().startsWith("S1"))
            periodo = "01";
          else
            periodo = "02";
            
          Integer test;
          if (bean.getDWH_test()!=null)
            test = bean.getDWH_test();
          else
            test = 0;
                
            //creo la hashmap per gli eventauli errori
           HashMap errors=new HashMap();
             //invoco il DWHGenerator...
           DWHGenerator t = null;
             
           t = new DWHGenerator(periodo,bean.getDWH_anno(),test,r.getKey(),errors);
             
             //se l'inizializzaizone ha dato errori non eseguo la thread
            if(!errors.isEmpty())
            {
              t=null;
               throw new Exception((String)errors.get("error"));
            }
            
            //altrimenti faccio partire la thread
            t.start();
             
            //e comunico il fatto all'utente
            this.handleMessages(FacesMessage.SEVERITY_INFO,"Il processo di estrazione del tracciato DWH e' stato avviato");
             
            
            //LOCK DELL'OPERAZIONE
            }
         catch (Exception e)
         {
           this.handleException("Impossibile generare il tracciato DWH: "+e.getMessage(),null);
         }
         finally
         {
           vo=am.findViewObject("Impexp_SoLogTransferDWHView1");  
          vo.setWhereClause("GRUPPO='"+ConfigurationConstants.TPDIP_DWH+"' AND VERSO='"+ConfigurationConstants.IMPEXP_LOC+"' AND ULSS='"+
          r.getUlss()+"' AND TPSCR='"+r.getTpscr()+"'");
          vo.setOrderByClause("IDLOG DESC");
          vo.executeQuery();
         }
         
      }
      
      /**
       * Unlock del rcord di so_cnf_impexp per indicare che l'operazione e' conclusa
       * @param ctx
       */
      public void onClose()
      {
       
        ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        try{
         
         ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
         Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
        if(r!=null)
          ImpexpUtils.getRowAndUNLock(am,r,true,false);
          
          
        }catch(Exception ex)
        {
        //  ex.printStackTrace();
          this.handleException(ex.getMessage(),am.getTransaction());
        }
      }
      
      public String onCleanLog()
      {
         ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
         ViewObject vo=am.findViewObject("Impexp_SoLogTransferDWHView1");
         Row r;
         while( (r=vo.last())!=null)
         {
           r.remove();
         }
         
         am.getTransaction().commit();
         
         return null;
      }
      
    public void onSelectUlss(ValueChangeEvent valueChangeEvent) {
        ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
        Map session = ADFContext.getCurrent().getSessionScope();
        String newUlss = (String)valueChangeEvent.getNewValue();
        try
        {
          if(newUlss==null)
          {           
            session.remove("impexp_localdir");
            session.remove("impexp_localfile");
            session.remove("impexp_virtualdir");
            //voglio che la current row sia null
            vo.setWhereClause("1=2");
            vo.executeQuery();
            
            return;
          }
            
        
            String tpscr=(String)session.get("scr");
          
            vo.setWhereClause("IMPEXP='"+ConfigurationConstants.IMPEXP_LOC+
               "' AND TPDIP='"+ConfigurationConstants.TPDIP_DWH+
               "' AND ULSS='"+newUlss+"' AND TPSCR='"+tpscr+"'");
               vo.executeQuery();
              if(vo.getRowCount()==0)
                throw new Exception("Configurazione per la creazione del tracciato DWH non trovata per la ulss "+newUlss+". Impossibile utilizzare la funzionalita'");
              vo.setCurrentRow(vo.first());
              
              Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
             
              File dir=new File(r.getPosizfilepart());
              if(!dir.exists())
                throw new Exception("Directory "+dir.getAbsolutePath()+" non trovata");
              
              session.put("impexp_localdir", dir.getAbsolutePath());
              session.put("impexp_localfile", r.getNomefilepart());
              
              dir=new File(r.getPosizdirvirtuale());
              if(!dir.exists())
                throw new Exception("Directory virtuale "+dir.getAbsolutePath()+" non trovata");
              session.put("impexp_virtualdir", dir.getAbsolutePath());
            
            ImpexpProvider provider = new ImpexpProvider();
            this.zipFiles = provider.findDwhFileList(r.getPosizfilearr(), r.getNomefilepart());
              
              vo=am.findViewObject("Impexp_SoLogTransferDWHView1");
           
              r.refresh(Row.REFRESH_CONTAINEES);
              vo.setWhereClause("GRUPPO='"+r.getTpdip()+"' AND VERSO='"+ConfigurationConstants.IMPEXP_LOC+"' AND ULSS='"+
              r.getUlss()+"' AND TPSCR='"+r.getTpscr()+"'");
              vo.setOrderByClause("IDLOG DESC");
              
        }
        catch (Exception e)
        {
          this.handleException(e.getMessage(),null);
          session.put("impexp_localdir", null);
          session.put("impexp_localfile", null);
             
        }
      }

    public void setZipFiles(List<ImpexpFile> zipFiles) {
        this.zipFiles = zipFiles;
    }

    public List<ImpexpFile> getZipFiles() {
        return zipFiles;
    }

    public void setDwHFrm(RichForm dwHFrm) {
        this.dwHFrm = dwHFrm;
    }

    public RichForm getDwHFrm() {
        if (dwHFrm == null)
            findForward();
        return dwHFrm;
    }
    
    protected void findForward() 
      {
         Map session = ADFContext.getCurrent().getSessionScope();
         String ulss=(String)session.get("ulss");
         String tpscr=(String)session.get("scr");
         Boolean regionale=(Boolean)session.get("regionale");
         ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
         
         try{
           if(regionale!=null && regionale.booleanValue())
           {
             //se e' un utente regionale il controllo viene fatto dopo        
             ViewObject vo=am.findViewObject("ImpExp_SoAziendaView1");
             vo.setWhereClause(" CODAZ != '"+ConfigurationConstants.CODICE_ULSS_REGIONALE+"' ");
             vo.setOrderByClause("Codaz");
             vo.executeQuery();
           } else 
           {
             
             ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
             Stats_paramBean bean=(Stats_paramBean)BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
             bean.setDWH_ulss(ulss);
             vo.setWhereClause("IMPEXP='"+ConfigurationConstants.IMPEXP_LOC+
               "' AND TPDIP='"+ConfigurationConstants.TPDIP_DWH+
               "' AND ULSS='"+ulss+"' AND TPSCR='"+tpscr+"'");
               vo.executeQuery();
              if(vo.getRowCount()==0)
                throw new Exception("Configurazione per la creazione del tracciato record DWH non trovata. Impossibile utilizzare la funzionalita'");
              vo.setCurrentRow(vo.first());
              Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
              File dir=new File(r.getPosizfilearr());
              if(!dir.exists())
                throw new Exception("Directory "+dir.getAbsolutePath()+" non trovata");
              
              session.put("impexp_localdir", dir.getAbsolutePath());
              session.put("impexp_localfile", r.getNomefilepart());
              
              dir=new File(r.getPosizdirvirtuale());
              if(!dir.exists())
                throw new Exception("Directory virtuale "+dir.getAbsolutePath()+" non trovata");
              session.put("impexp_virtualdir", dir.getAbsolutePath());
               
               ImpexpProvider provider = new ImpexpProvider();
               this.zipFiles = provider.findDwhFileList(r.getPosizfilearr(), r.getNomefilepart());
           }
           
            ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
            Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
          
            vo=am.findViewObject("Impexp_SoLogTransferDWHView1");
          
            if(r!=null){
              r.refresh(Row.REFRESH_CONTAINEES);
              vo.setWhereClause("GRUPPO='"+ConfigurationConstants.TPDIP_DWH+"' AND VERSO='"+ConfigurationConstants.IMPEXP_LOC+"' AND ULSS='"+
              r.getUlss()+"' AND TPSCR='"+r.getTpscr()+"'");
              vo.setOrderByClause("IDLOG DESC");
            }
            else
            {
              vo.setWhereClause("1=2");
            }
            vo.executeQuery();
             
            
           
         } 
         catch(Exception ex)
         {
             this.handleException(ex.getMessage(),null);
       
         }
      }

    public void setZipTable(RichTable zipTable) {
        this.zipTable = zipTable;
    }

    public RichTable getZipTable() {
        return zipTable;
    }

    public void downloadZip(FacesContext facesContext, OutputStream outputStream) {

            Map session = ADFContext.getCurrent().getSessionScope();
            String directory = (String) session.get("impexp_localdir");
            File fileZip = new File(directory + "/" + zipFileName.getValue().toString() + ".zip");
            FileInputStream fis;
            byte[] b;
            try {
                fis = new FileInputStream(fileZip);

                int n;
                while ((n = fis.available()) > 0) {
                    b = new byte[n];
                    int result = fis.read(b);
                    outputStream.write(b, 0, b.length);
                    if (result == -1)
                        break;
                }
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
    
    public void downloadLog(FacesContext facesContext, OutputStream outputStream) {

            Map session = ADFContext.getCurrent().getSessionScope();
            String directory = (String) session.get("impexp_localdir");
            //download file di log
            String filename= zipFileName.getValue().toString();
            filename = "LOG_"+filename+".pdf";
            filename=ConfigurationReader.readProperty("scheduledLogPath")+"/"+filename.trim();
            File pdf = new File(filename);
            FileInputStream fis;
            byte[] b;
            try {
                fis = new FileInputStream(pdf);

                int n;
                while ((n = fis.available()) > 0) {
                    b = new byte[n];
                    int result = fis.read(b);
                    outputStream.write(b, 0, b.length);
                    if (result == -1)
                        break;
                }
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void setZipFileName(RichOutputText zipFileName) {
        this.zipFileName = zipFileName;
    }

    public RichOutputText getZipFileName() {
        return zipFileName;
    }

    public String onRefeshFiles() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String directory = (String) session.get("impexp_localdir");
        String nomefile = (String) session.get("impexp_localfile");
        ImpexpProvider provider = new ImpexpProvider();
        this.zipFiles = provider.findDwhFileList(directory, nomefile);
        return null;
    }
}
