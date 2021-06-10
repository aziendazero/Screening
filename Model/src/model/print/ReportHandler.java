package model.print;

import insiel.dataHandling.BlobUtils;

import java.io.BufferedOutputStream;
import java.io.File;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashMap;

import model.commons.ConfigurationConstants;
import model.commons.ConfigurationReader;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;
import oracle.jbo.domain.BlobDomain;


public class ReportHandler 
{
  ApplicationModule am;
  JRFileVirtualizer virtualizer;
  public ReportHandler()
  {
    // mauro 09/02/2010, parametrizzazione directory virtualizer
    String virtualizerDir = ConfigurationReader.readProperty("virtualizerDirectory");
    // mauro 21/05/2010, parametrizzazione directory virtualizer
    String virtualizerSize = ConfigurationReader.readProperty("virtualizerSize");
    
    int size = 1000;
    
    if (virtualizerSize != null && !virtualizerSize.equals(""))
    {
      size = Integer.parseInt(virtualizerSize);
      
    }
    
    
    boolean exists = false;
    
    if (virtualizerDir != null && !virtualizerDir.equals(""))
    {
      exists = (new File(virtualizerDir)).exists(); 
    }    
    
    if (exists)
    {
      virtualizer = new JRFileVirtualizer(size, virtualizerDir);      
    }
    else
    {  
      virtualizer = new JRFileVirtualizer(size, System.getProperty("java.io.tmpdir"));
    }
    // mauro 21/05/2010, fine
    // mauro 09/02/2010, fine
    
    //20110429 Serra
    virtualizer.setReadOnly(true);
    //fine
   
  }
  
  // mauro 24/05/2010, nuovo metodo per cleanup virtualizer
  public void cleanup()
  {
    if (virtualizer != null) // 07/06 aggiunto test 
    {
        virtualizer.cleanup();
    }
  }
  
  protected void finalize() throws Throwable
  {
    if(am!=null)
      Configuration.releaseRootApplicationModule(am,true);
     //per evitare sovraccarico di connessioni al db (7/12/2006)
     //Configuration.releaseRootApplicationModule(am,false);
    super.finalize();
  }

/*  public ReportHandler(File f) throws JRException
  {
    JasperDesign design=JRXmlLoader.load(f);
    try
    {
      JasperReport jasper=JasperCompileManager.compileReport(design);
    }
    catch(JRException ex)
    {
      
    }
  }*/
  
  /**
   * Metodo che crae un report prelevando il template dal record indicato e
   * compilandolo oppure provando ad utilizzare direttamente il compilato
   * @throws java.lang.Exception
   * @return file con il pdf
   * @param parameters mappa dei parametri
   * @param ds sorgente dati
   * @param file_jasper nome del campo del viewobject contenete il compilato
   * (se null la tabella non mantiene il compilato)
   * @param file_xml nome del campo del viewobject contenete il template
   * @param original record contenete il template
   * @param vo_name nome del viewobject da cui si prende il template
   */
  public File createReport(Row original,
                      String vo_name,
                      String file_xml,
                      String file_jasper,
                      JRDataSource ds,
                      HashMap parameters) throws Exception
  {
      
    JasperReport jasper=this.getCompiled(original,vo_name, file_xml,file_jasper); 
    JasperPrint print=null;

     
     parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
    try{
    
      //riempio il report con i dati
      print=JasperFillManager.fillReport(jasper,parameters,ds);
    }
    catch(JRException ex)
    { //se è stat generata un'eccezione forse le librerie sono state rinnovate 
      //e il report va ricompilato
      jasper=this.recompileReport(original,vo_name,file_xml,file_jasper);
      print=JasperFillManager.fillReport(jasper,parameters,ds);
    }
    
    File pdf=File.createTempFile("report",".pdf");
   // JasperExportManager.exportReportToPdfFile(print,pdf.getAbsolutePath());
   JRPdfExporter exp=new JRPdfExporter();
   exp.setParameter(JRExporterParameter.JASPER_PRINT,print);
   exp.setParameter(JRExporterParameter.OUTPUT_FILE,pdf);
   exp.setParameter(JRPdfExporterParameter.PDF_VERSION,
                    ConfigurationConstants.PDF_VERSION);
   exp.exportReport();
    print=null;
    return pdf;
    
  }
  
  public File createReport(Row original,String vo_name,
                      String file_xml,
                      String file_jasper,
                      Connection conn,
                      HashMap parameters) throws Exception
  {
    
    
    JasperReport jasper=this.getCompiled(original,vo_name, file_xml,file_jasper); 
    JasperPrint print=null;

       parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
    try{
      //riempio il report con i dati
      print=JasperFillManager.fillReport(jasper,parameters,conn);
    }
    catch(JRException ex)
    { //se è stat generata un'eccezione forse le librerie sono state rinnovate 
      //e il report va ricompilato
      jasper=this.recompileReport(original,vo_name, file_xml,file_jasper);
      print=JasperFillManager.fillReport(jasper,parameters,conn);
    }
    
    File pdf=File.createTempFile("report",".pdf");
   // JasperExportManager.exportReportToPdfFile(print,pdf.getAbsolutePath());
  
   JRPdfExporter exp=new JRPdfExporter();
   exp.setParameter(JRExporterParameter.JASPER_PRINT,print);
   exp.setParameter(JRExporterParameter.OUTPUT_FILE,pdf);
   exp.setParameter(JRPdfExporterParameter.PDF_VERSION,
                    ConfigurationConstants.PDF_VERSION);
   exp.exportReport();
    print=null;
    return pdf;
    
  }
  
  public File createCsvReport(Row original,String vo_name,
                      String file_xml,
                      String file_jasper,
                      Connection conn,
                      HashMap parameters) throws Exception
  {
    
    
    JasperReport jasper=this.getCompiled(original,vo_name, file_xml,file_jasper); 
    JasperPrint print=null;

       parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
    try{
      //riempio il report con i dati
      print=JasperFillManager.fillReport(jasper,parameters,conn);
    }
    catch(JRException ex)
    { //se è stat generata un'eccezione forse le librerie sono state rinnovate 
      //e il report va ricompilato
      jasper=this.recompileReport(original,vo_name, file_xml,file_jasper);
      print=JasperFillManager.fillReport(jasper,parameters,conn);
    }
    
    File csv=File.createTempFile("report",".csv");
   
    //esportazione in csv
     JRCsvExporter exp=new JRCsvExporter();
     exp.setParameter(JRCsvExporterParameter.FIELD_DELIMITER,";");
     exp.setParameter(JRCsvExporterParameter.JASPER_PRINT,print);
     exp.setParameter(JRCsvExporterParameter.OUTPUT_FILE,csv);
     exp.exportReport();
   
    print=null;
    return csv;
    
  }
  
  /**
   * Metodo che restituisce il compilato di un template, prendendolo da DB se c'è,
   * oppure ricompilando il template
   * @throws java.lang.Exception
   * @return 
   * @param file_jasper nome del campo del viewobject contenete il compilato
   * (se null la tabella non mantiene il compilato)
   * @param file_xml nome del campo del viewobject contenete il template
   * @param r record contenente template e/o compilato
   */
  public JasperReport getCompiled(Row r,String vo_name,
                      String file_xml,
                      String file_jasper) throws Exception
  {
    
    BlobDomain blob=null;
    File f=null;
    try
    {
      //se nella tabella c'è anche il compilato provo ad usarlo
      if(file_jasper!=null)
        blob=(BlobDomain)r.getAttribute(file_jasper);
      JasperReport jasper=null;
      //se il compilato è null
      if(blob==null)
      {
        //ottengo il compilato
        jasper=this.recompileReport(r,vo_name,file_xml, file_jasper);
      }
      else{
      //estraggo il compilato già pronto
        f=BlobUtils.getFileFromBlob(blob,"compiled");
  
        jasper=(JasperReport)JRLoader.loadObject(f);
      }
       return jasper;
    }
    finally
    {
      if(f!=null)
       f.delete();
    }
  }
  
   /**
   * Metodo che commpila un template, memorizza un compilato nel database e
   * restituisce il nuovo compilato
   * @throws java.lang.Exception
   * @return 
   * @param file_jasper nome del campo del viewobject contenete il compilato
   * (se null la tabella non mantiene il compilato)
   * @param file_xml nome del campo del viewobject contenete il template
   * @param r record originale contenente template e/o compilato
   */
  public JasperReport recompileReport(Row r,String vo_name,
                      String file_xml,
                      String file_jasper) throws Exception
  {
    File xml=null;
    try
    {
      am =Configuration.createRootApplicationModule("model.Reports_AppModule","Reports_AppModuleLocal");
      ViewObject vo=am.findViewObject(vo_name);
      Row[] result=vo.findByKey(r.getKey(),1);
      if(result.length==0)
        throw new Exception("Record con chiave "+r.getKey()+" non trovato");
      BlobDomain b=(BlobDomain)result[0].getAttribute(file_xml);
      JasperReport jasper=null;
        if(b==null)
          throw new Exception("Template non inserito");
        xml=BlobUtils.getFileFromBlob(b,"template");
        //lo carico come jasperdesign e lo compilo
        JasperDesign design=JRXmlLoader.load(xml);
        jasper=JasperCompileManager.compileReport(design);
        
        //se esiste un campo per il compilato, lo memorizzo
        if(file_jasper!=null)
        {
          BlobDomain output=new BlobDomain();
          BufferedOutputStream out=new BufferedOutputStream(output.getBinaryOutputStream());
          JasperCompileManager.compileReportToStream(design,out);
          try{
            result[0].setAttribute(file_jasper,output);
            am.getTransaction().commit();
          }
          catch(Exception ex)
          {
            am.getTransaction().rollback();
            throw ex;
          }
          
        }
        return jasper;
	}
    finally
    {
	  if (am != null)
        Configuration.releaseRootApplicationModule(am,true);
	
     //per evitare sovraccarico di connessioni al db (7/12/2006)
     //Configuration.releaseRootApplicationModule(am,false);
      if(xml!=null)
        xml.delete();
    }
      
      
  }
  
  
  public static JasperDesign getDesign(File f) throws JRException
  {
    JasperDesign design=JRXmlLoader.load(f);
 //   JasperReport jasper=JasperCompileManager.compileReport(design);
    return design;
  }
  
   public static JasperDesign getDesign(Row r,String file_xml) throws Exception
  {
     File xml=BlobUtils.getFileFromBlob((BlobDomain)r.getAttribute(file_xml),"template");
      //lo carico come jasperdesign e lo compilo
      JasperDesign design=JRXmlLoader.load(xml);
      xml.delete();
    return design;
  }
  
  public static BlobDomain getBlobJasper(File f) throws JRException,SQLException
  {
    JasperDesign design=JRXmlLoader.load(f);
    BlobDomain output=new BlobDomain();
    BufferedOutputStream out=new BufferedOutputStream(output.getBinaryOutputStream());
    JasperCompileManager.compileReportToStream(design,out);
    return output;
  }
}