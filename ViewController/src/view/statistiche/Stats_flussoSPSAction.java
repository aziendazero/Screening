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

import model.common.Stats_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;

import model.commons.ConfigurationReader;

import model.datacontrol.Stats_paramBean;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.output.RichOutputText;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.impexp.ImpexpUtils;


import oracle.jbo.domain.Number;

import view.commons.action.Parent_DataForwardAction;

import view.thread.FlussoSPSGenerator;

public class Stats_flussoSPSAction extends Parent_DataForwardAction
{
    private RichForm spsFrm;
    List<ImpexpFile> zipFiles = new ArrayList();
    private RichOutputText fileZipName;

    protected void setAppModule()
  {
    this.amName="ImpExp_AppModule";
  }
  
    public void onSelectUlss(ValueChangeEvent valueChangeEvent) {
    Map session = ADFContext.getCurrent().getSessionScope();
    ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
    ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
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
     
     Stats_AppModule amStats =(Stats_AppModule)BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
      ViewObject centri_prel = amStats.findViewObject("Cnf_SoCnfCentroPrelView1");
      centri_prel.setWhereClause(" ULSS ='"+newUlss+"' AND TPSCR='"+tpscr+"'");
      centri_prel.executeQuery();
      
      ViewObject centri_ref = amStats.findViewObject("Cnf_SoCnfCentroRefView1");
      centri_ref.setWhereClause(" ULSS ='"+newUlss+"' AND TPSCR='"+tpscr+"'");
      centri_ref.executeQuery();
      
      ViewObject centri_hpv = amStats.findViewObject("Cnf_SoAmbIstricView1");
      centri_hpv.setWhereClause(" ULSS ='"+newUlss+"' AND TPSCR='"+tpscr+"' AND TPIST=0");
      centri_hpv.executeQuery();
      
         vo.setWhereClause("IMPEXP='"+ConfigurationConstants.IMPEXP_LOC+
           "' AND TPDIP='"+ConfigurationConstants.TPDIP_FLUSSOSPA+
           "' AND ULSS='"+newUlss+"' AND TPSCR='"+tpscr+"'");
           vo.executeQuery();
          if(vo.getRowCount()==0)
            throw new Exception("Configurazione per la creazione del flusso SPS non trovata per la ulss "+newUlss+". Impossibile utilizzare la funzionalita'");
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
          this.zipFiles = provider.findSPSFileList(r.getPosizfilearr(), r.getNomefilepart());
          
          vo=am.findViewObject("Impexp_SoLogTransferSPSView1");
       
          r.refresh(Row.REFRESH_CONTAINEES);
          vo.setWhereClause("GRUPPO='"+r.getTpdip()+"' AND VERSO='"+ConfigurationConstants.IMPEXP_LOC+"' AND ULSS='"+
          newUlss+"' AND TPSCR='"+r.getTpscr()+"'");
          vo.setOrderByClause("IDLOG DESC");
          
    }
    catch (Exception e)
    {
      this.handleException(e.getMessage(),null);
      session.put("impexp_localdir", null);
      session.put("impexp_localfile", null);
         
    }
  }
  
    public void onGenerate(ActionEvent actionEvent) {
    
     ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
     ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
     Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
     Stats_paramBean bean=(Stats_paramBean)BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
     String tpscr=(String)ADFContext.getCurrent().getSessionScope().get("scr");
     
     try
     {         
       if(r==null)
         throw new Exception("Non e' stata selezionata nessuna ulss oppure la ulss selezionata non presenta i dati di configurazione per eseguire tale operazione.");    
     
      if (bean.getSPS_dal()== null || bean.getSPS_al() == null )
      {
        throw new Exception("Inserire gli estremi del periodo temporale");
      }
  
      if ((bean.getSPS_prestazione1() == null || bean.getSPS_prestazione1().intValue() == 0)
          && (bean.getSPS_prestazione2() == null || bean.getSPS_prestazione2().intValue() == 0)
          && (bean.getSPS_prestazione3() == null || bean.getSPS_prestazione3().intValue() == 0)
          && (bean.getSPS_prestazione4() == null || bean.getSPS_prestazione4().intValue() == 0))
      {
        throw new Exception("Selezionare almeno un tipo di prestazione");
      }
      
      String array_prestazioni = "";
      if (bean.getSPS_prestazione1() != null && bean.getSPS_prestazione1().intValue() == 1)
      {
        array_prestazioni = array_prestazioni + bean.getSPS_label1().substring(bean.getSPS_label1().indexOf(":")+2)+",";
      }
      if (bean.getSPS_prestazione2() != null && bean.getSPS_prestazione2().intValue() == 1)
      {
        array_prestazioni = array_prestazioni + bean.getSPS_label2().substring(bean.getSPS_label2().indexOf(":")+2)+",";
      }
      if (bean.getSPS_prestazione3() != null && bean.getSPS_prestazione3().intValue() == 1)
      {
        array_prestazioni = array_prestazioni + bean.getSPS_label3().substring(bean.getSPS_label3().indexOf(":")+2)+",";
      }
      if (bean.getSPS_prestazione4() != null && bean.getSPS_prestazione4().intValue() == 1)
      {
        array_prestazioni = array_prestazioni + bean.getSPS_label4().substring(bean.getSPS_label4().indexOf(":")+2)+",";
      }
      
      array_prestazioni = array_prestazioni.substring(0, array_prestazioni.length() - 1);
      
      String array_tipo_inviti = "";
      String array_centri_ref = "";
      String array_lab_hpv = "";
      if (tpscr.equals("CI"))
      {
        Stats_AppModule amStats =(Stats_AppModule)BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject voInviti = amStats.findViewObject("Cnf_SoCnfTpinvitoView1");
        Row[] selected = voInviti.getFilteredRows("Selected", true);       
        if (selected.length > 0)
        {         
            for(Row cInv : selected)
          {
            String idTipoInvito = (String)cInv.getAttribute("Idtpinvito");
            array_tipo_inviti = array_tipo_inviti + idTipoInvito + ",";
          }
          
          array_tipo_inviti = array_tipo_inviti.substring(0, array_tipo_inviti.length() - 1);
          
        } else 
        {
          throw new Exception("Selezionare almeno un tipo di invito");
        }
        
        ViewObject voCentri = amStats.findViewObject("Cnf_SoCnfCentroRefView1");
        Row[] selectedCentri = voCentri.getFilteredRows("Selected", true);         
        if (selectedCentri.length > 0)
        {    
     
            for(Row cCentro : selectedCentri)
          {
            Number idCentroRef = (Number)cCentro.getAttribute("Idcentro");
            array_centri_ref = array_centri_ref + idCentroRef.toString() + ",";
          }
          
          array_centri_ref = array_centri_ref.substring(0, array_centri_ref.length() - 1);
        }
        
        ViewObject voLab = amStats.findViewObject("Cnf_SoAmbIstricView1");
        Row[] selectedLab = voLab.getFilteredRows("Selected", true); 
        if (selectedLab.length > 0)
        {           
            for(Row cLab : selectedLab)
          {
            Number idLab = (Number)cLab.getAttribute("Idambist");
            array_lab_hpv = array_lab_hpv + idLab.toString() + ",";
          }
          
          array_lab_hpv = array_lab_hpv.substring(0, array_lab_hpv.length() - 1);
        }
        
      } else 
      {
        if (bean.getSPS_centro_ref() != null)
          array_centri_ref = bean.getSPS_centro_ref().toString();
      }
      
      if (array_lab_hpv.equals("")) 
        array_lab_hpv = null;
      if (array_centri_ref.equals(""))
        array_centri_ref = null;
      if (array_tipo_inviti.equals(""))
        array_tipo_inviti = null;
               
        //creo la hashmap per gli eventauli errori
       HashMap errors=new HashMap();
       FlussoSPSGenerator t = new FlussoSPSGenerator(bean.getSPS_dal(),bean.getSPS_al(),bean.getSPS_centro_prel(), array_lab_hpv,
                                                        array_centri_ref,array_prestazioni, array_tipo_inviti, r.getKey(),errors);
                                 
         //se l'inizializzaizone ha dato errori non eseguo la thread
        if(!errors.isEmpty())
        {
          t=null;
          throw new Exception((String)errors.get("error"));
        }
        
        //altrimenti faccio partire la thread
        t.start();
         
        //e comunico il fatto all'utente
        this.handleMessages(FacesMessage.SEVERITY_INFO, "Il processo di estrazione del flusso SPS e' stato avviato");
         
        }
     catch (Exception e)
     {
       this.handleException("Impossibile generare il flusso SPS: "+e.getMessage(),null);
     }
     finally
     {
       vo=am.findViewObject("Impexp_SoLogTransferSPSView1");  
      vo.setWhereClause("GRUPPO='"+ConfigurationConstants.TPDIP_FLUSSOSPA+"' AND VERSO='"+ConfigurationConstants.IMPEXP_LOC+"' AND ULSS='"+
      r.getUlss()+"' AND TPSCR='"+r.getTpscr()+"'");
      vo.setOrderByClause("IDLOG DESC");
      vo.executeQuery();
     }
     
  }
  
    public String onFileDelete() {

        try {
                Map session = ADFContext.getCurrent().getSessionScope();
                String directory = (String) session.get("impexp_localdir");
                File fileZip = new File(directory + "/" + fileZipName.getValue().toString() + ".zip");
                fileZip.delete();
            
            ImpexpProvider provider = new ImpexpProvider();
            this.zipFiles = provider.findSPSFileList((String)session.get("impexp_localdir"),  (String)session.get("impexp_localfile"));

        } catch (Exception e) {
            this.handleException(e.getMessage(), null);
        }

        return null;
    }
  
  public String onCleanLog()
  {
     ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
     ViewObject vo=am.findViewObject("Impexp_SoLogTransferSPSView1");
     Row r;
     while( (r=vo.last())!=null)
     {
       r.remove();
     }
     
     am.getTransaction().commit();
     
     return null;
  }
  
  public void onClose()
  {
   
    ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
    try{
      AccessManager.checkPermission("SOStatREG");
      
     ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
     Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
    if(r!=null)
      ImpexpUtils.getRowAndUNLock(am,r,true,false);
      
      
    }catch(Exception ex)
    {
      this.handleException(ex.getMessage(),am.getTransaction());
    }
  }
  
  public void onRefreshLog()
  {
     ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
     ViewObject vo=am.findViewObject("Impexp_SoLogTransferSPSView1");
     vo.executeQuery();
  }

    public void setSpsFrm(RichForm spsFrm) {
        this.spsFrm = spsFrm;
    }

    public RichForm getSpsFrm() {
        if (spsFrm == null)
            findForward();
        return spsFrm;
    }
    
    private void findForward()
      {
        Map session = ADFContext.getCurrent().getSessionScope();
         String ulss=(String)session.get("ulss");
         String tpscr=(String)session.get("scr");
         Boolean regionale=(Boolean)session.get("regionale");
         ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
         Stats_paramBean bean=(Stats_paramBean)BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
         ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
         
         try{
           if(regionale!=null && regionale.booleanValue())
           {
             //se e' un utente regionale il controllo viene fatto dopo
            //voglio che la current row sia null
           vo.setWhereClause("1=2");
            vo.executeQuery();
            
             ViewObject voUlss=am.findViewObject("ImpExp_SoAziendaView1");
             voUlss.setWhereClause(" CODAZ != '"+ConfigurationConstants.CODICE_ULSS_REGIONALE+"' ");
             voUlss.setOrderByClause("Codaz");
             voUlss.executeQuery();
           } else 
           {               
             bean.setDWH_ulss(ulss);
             vo.setWhereClause("IMPEXP='"+ConfigurationConstants.IMPEXP_LOC+
               "' AND TPDIP='"+ConfigurationConstants.TPDIP_FLUSSOSPA+
               "' AND ULSS='"+ulss+"' AND TPSCR='"+tpscr+"'");
               vo.executeQuery();
              if(vo.getRowCount()==0)
                throw new Exception("Configurazione per la creazione del flusso SPA non trovata. Impossibile utilizzare la funzionalita'");
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
               this.zipFiles = provider.findSPSFileList(r.getPosizfilearr(), r.getNomefilepart());
           }
           //imposto i nomi delle prestazioni per i checkbox
           ViewObject voCnf=am.findViewObject("Impexp_SoCnfSpsPrestazioniView1");
           voCnf.setWhereClause(" TPSCR = '"+tpscr+"' ");
           voCnf.executeQuery();
           if (voCnf.getRowCount() == 0) 
           {
             throw new Exception("Configurazione per la creazione del flusso SPA non trovata. Impossibile utilizzare la funzionalita'");
           }
           Row r = voCnf.first();
           bean.setSPS_label1((String) r.getAttribute("PrestazioneScr") + ": " + (String) r.getAttribute("PrestazioneSps"));
           bean.setSPS_prestazione1(1);
           r = voCnf.next();
           if (r != null){
            bean.setSPS_label2((String) r.getAttribute("PrestazioneScr") + ": " + (String) r.getAttribute("PrestazioneSps"));
            bean.setSPS_prestazione2(1);
           } else 
           {
             bean.setSPS_label2(null);
             bean.setSPS_prestazione2(null);
           }
           r = voCnf.next();
           if (r != null){
            bean.setSPS_label3((String) r.getAttribute("PrestazioneScr") +  ": " + (String) r.getAttribute("PrestazioneSps"));
            bean.setSPS_prestazione3(1);
           } else 
           {
             bean.setSPS_label3(null);
             bean.setSPS_prestazione3(null);
           }
           r = voCnf.next();
           if (r != null){
            bean.setSPS_label4((String) r.getAttribute("PrestazioneScr") +  ": " + (String) r.getAttribute("PrestazioneSps"));
            bean.setSPS_prestazione4(1);
           } else 
           {
             bean.setSPS_label4(null);
             bean.setSPS_prestazione4(null);
           }
           
            vo=am.findViewObject("Impexp_SoCnfImpexpView1");
            Impexp_SoCnfImpexpViewRow r1=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
          
            vo=am.findViewObject("Impexp_SoLogTransferSPSView1");
          
            if(r1!=null){
              r1.refresh(Row.REFRESH_CONTAINEES);
              vo.setWhereClause("GRUPPO='"+r1.getTpdip()+"' AND VERSO='"+ConfigurationConstants.IMPEXP_LOC+"' AND ULSS='"+
              r1.getUlss()+"' AND TPSCR='"+r1.getTpscr()+"'");
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
    
    public void downloadZip(FacesContext facesContext, OutputStream outputStream) {

            Map session = ADFContext.getCurrent().getSessionScope();
            String directory = (String) session.get("impexp_localdir");
            File fileZip = new File(directory + "/" + fileZipName.getValue().toString() + ".zip");
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
            String filename= fileZipName.getValue().toString();
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

    public void setZipFiles(List<ImpexpFile> zipFiles) {
        this.zipFiles = zipFiles;
    }

    public List<ImpexpFile> getZipFiles() {
        return zipFiles;
    }

    public void setFileZipName(RichOutputText fileZipName) {
        this.fileZipName = fileZipName;
    }

    public RichOutputText getFileZipName() {
        return fileZipName;
    }
    
    public String onRefeshFiles() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String directory = (String) session.get("impexp_localdir");
        String nomefile = (String) session.get("impexp_localfile");
        ImpexpProvider provider = new ImpexpProvider();
        this.zipFiles = provider.findSPSFileList(directory, nomefile);
        return null;
    }

}
