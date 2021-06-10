package model.commons;

import insiel.dataHandling.BlobUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.Connection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.LetteraAppModuleImpl;

import model.global.A_SoAziendaViewRowImpl;
import model.global.A_SoCnfPartemplateViewRowImpl;
import model.global.A_SoTemplateViewRowImpl;

import model.print.ReportHandler;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

public class Lettera  {
	private static final Logger logger = Logger.getLogger("model.commons.Lettera");
	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private String ulss;
	private String tpscr;
	private long idTemplate;
	private LetteraAppModuleImpl am;
	private Map parametri = new HashMap();
	private File logoFile = null;
	private File pdfFile = null;
	
	public Lettera(String ulss, String tpscr, long idTemplate, LetteraAppModuleImpl am) {
		this.ulss = ulss;
		this.tpscr = tpscr;
		this.idTemplate = idTemplate;
		this.am = am;
		
		init();
	}
	
	/**
	 * Ripristina i parametri ai valori iniziali.
	 */
	public void init() {
		release();
		
		// Recupero il logo aziendale dal db e lo salvo su un file temporaneo
		ViewObject aziendaView = am.getA_SoAziendaView1();
		aziendaView.setWhereClause("codaz = :1");
		aziendaView.setWhereClauseParam(0, getUlss());
		aziendaView.executeQuery();
		A_SoAziendaViewRowImpl aziendaRow = (A_SoAziendaViewRowImpl)aziendaView.first();
		if (aziendaRow != null) {
		
			// Creo un file temporaneo contenente il logo dell'azienda
			InputStream logoInputStream = null;
			OutputStream logoOutputStream = null;
			try {
				logoFile = File.createTempFile("logo", null);
				BlobDomain logo = aziendaRow.getLogo();
				logoInputStream = logo.getInputStream();
				logoOutputStream = new FileOutputStream(logoFile);
				byte[] buffer = new byte[1024];
				int count;
				while ((count = logoInputStream.read(buffer)) > 0) {
					logoOutputStream.write(buffer, 0, count);
				}
			} catch(IOException e) {
				logger.log(Level.SEVERE, "Eccezione nella creazione del logo aziendale", e);
			} finally {
				if (logoInputStream != null) {
					try {
						logoInputStream.close();
					} catch (IOException e) {
						logger.log(Level.SEVERE, "Eccezione nella chiusura dell'InputStream del Blob", e);
					}
				}
				if (logoOutputStream != null) {
					try {
						logoOutputStream.close();
					} catch (IOException e) {
						logger.log(Level.SEVERE, "Eccezione nella chiusura del file del logo aziendale", e);
					}
				}
			}
		}
		
		// Valorizzo i parametri comuni a tutte le lettere
		parametri.clear();
		ViewObject paramsView = am.getA_SoCnfPartemplateView1();
		paramsView.setWhereClause("codaz = :1 AND tpscr = :2");
		paramsView.setWhereClauseParam(0, getUlss());
		paramsView.setWhereClauseParam(1, getTpscr());
		paramsView.executeQuery();
		while (paramsView.hasNext()) {
			A_SoCnfPartemplateViewRowImpl paramsRow = (A_SoCnfPartemplateViewRowImpl)paramsView.next();
			parametri.put(paramsRow.getNomepar(), paramsRow.getDescrpar());
		}
		parametri.put("logo", logoFile);
	}
	
	public void writePdf(LetteraBean bean, OutputStream outputStream) throws Exception {
		ViewObject templateView = am.getA_SoTemplateView1();
		templateView.setWhereClause("CODTEMPL = :1");
		templateView.setWhereClauseParam(0, new Integer(new Long(getIdTemplate()).intValue()));
		templateView.executeQuery();
		A_SoTemplateViewRowImpl templateRow = (A_SoTemplateViewRowImpl)templateView.first();
		if (templateRow != null) {
		
			// Se non c'è il template compilato, compilo il sorgente
			if (templateRow.getCompiled() == null) {
				if (templateRow.getFilexml() == null) {
					String msg = "Manca il modello sorgente per il template " + getIdTemplate();
					logger.severe(msg);
					throw new Exception(msg);
				}
				InputStream srcInputStream = templateRow.getFilexml().getBinaryStream();
				// Scrivo il template compilato su DB
				try {
					BlobDomain blob = new BlobDomain();
					OutputStream jasperOutputStream = blob.getBinaryOutputStream();
					JasperCompileManager.compileReportToStream(srcInputStream, jasperOutputStream);
					jasperOutputStream.close();
					templateRow.setCompiled(blob);
				} catch (Exception e) {
					String msg = "Errore nella compilazione del report Jasper per il template " + getIdTemplate();
					logger.severe(msg);
					throw new Exception(msg);
				}
			}
                        InputStream templateInputStream = null;
			try {
				// Creo il Jasper
				// Valorizzo la data di stampa, se non è già impostata
				if (bean.getData_stampa() == null) {
					bean.setData_stampa(dateFormat.format(new java.util.Date()));
				}
				templateInputStream = templateRow.getCompiled().getInputStream();
                                try {
                                    templateInputStream.reset();
                                } catch (IOException e) {
                                    //non serve fare niente
                                }
				JRDataSource ds = new JRBeanArrayDataSource(new Object[]{bean});
				JasperPrint jasperPrint = JasperFillManager.fillReport(templateInputStream, parametri, ds);

				// Creo il PDF
				JRPdfExporter pdfExporter = new JRPdfExporter();
				pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				pdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
				pdfExporter.exportReport();
			} catch (JRException e) {
				String msg = "Errore nella creazione del report";
				logger.log(Level.SEVERE, msg, e);
				throw new Exception(msg + " - " + e.getMessage(), e);                               
			} catch (Exception e) {
				String msg = "Eccezione nella creazione del report";
				logger.log(Level.SEVERE, msg, e);
				throw new Exception(msg, e);
                        } finally {
                            if (templateInputStream != null)
                                templateInputStream.close();
                        }
                           
                              
		}
	}
	
	public File getPdfFile(LetteraBean bean) throws Exception {
		pdfFile = File.createTempFile("pdf", null);
		writePdf(bean, new FileOutputStream(pdfFile));
		return pdfFile;
	}
	
	public void release() {
		if (logoFile != null) {
			if (!logoFile.delete()) {
				logger.warning("Impossibile cancellare il file temporaneo del logo aziendale " + logoFile.toString());
			}
			logoFile = null;
		}
		if (pdfFile != null) {
			if (!pdfFile.delete()) {
				logger.warning("Impossibile cancellare il file temporaneo del pdf " + pdfFile.toString());
			}
			pdfFile = null;
		}
                if(this.template!=null){
                            this.template.delete();
                            ParametriSistema.releaseLogo(this.map);
                }
	}
	
	public String getUlss() {
		return ulss;
	}
	
	public String getTpscr() {
		return tpscr;
	}
	
	public long getIdTemplate() {
		return idTemplate;
	}
	
	public File getLogoFile() {
		return logoFile;
	}
	
	public Map getParametri() {
		return parametri;
	}
	
	
        
        
        
///////////////////////////////////////////
  /**
   * File xml o jrxml con il template
   */
  private File template;
  /**
   * Nome che avrà il file di finale privo di estensione
   */
  //private String fName;
  /**
   * Template caricato dal motore jasper
   */
  private JasperDesign design;
  /**
   * Elenco dei parametri del report
   */
  private JRParameter[] params;
  /**
   * Template compilato
   */
  private JasperReport report;
  /**
   * Mappa dei paraemtri da passare al report
   */
  private HashMap map;

  /**
   * File di destinazione
   */
   private File dest;
   
   /**
    * Formato di export del file
    */
    private int formato;

 /**
   * COSTRUTTORE: carica il template, lo compila e ne legge i parametri 
   * @throws net.sf.jasperreports.engine.JRException
   * @param _fName nome che avrà il file finale, privo di estensione
   * @param _template file xml o jrxml con il template
   */
  public Lettera(File _template,String _fName, int _formato) throws Exception
  {
    this.template=_template;
    this.formato=_formato;
    dest=new File(_fName);
    if(!dest.exists()){
      String suffix=ConfigurationConstants.FORMATO_CSV==formato?".csv":".pdf";
      dest=File.createTempFile(_fName,suffix);
    }
    
    initialize();
  }
  
  /**
   * COSTRUTTORE: carica il template, lo compila e ne legge i parametri
   * @throws net.sf.jasperreports.engine.JRException
   * @throws java.io.IOException
   * @param _fName nome che avrà il file finale, privo di estensione
   * @param template ogetto BlobDomain contenente il file xml o jrxml con il template
   */
  public Lettera(BlobDomain template,String _fName,int _formato) throws IOException,JRException
  {
    this.template=BlobUtils.getFileFromBlob(template,_fName);
    this.formato=_formato;
    dest=new File(_fName);
    if(!dest.exists()){
      String suffix=ConfigurationConstants.FORMATO_CSV==formato?".csv":".pdf";
      dest=File.createTempFile(_fName,suffix);
    }
    initialize();
  }
  

   /**
   * COSTRUTTORE: usa ReportHandler per ottenere il file compilato o,
   * all'occorrenza, ricompilarlo
   * @throws java.lang.Exception
   * @param _fName nome che avrà il file finale, privo di estensione
   * @param file_jasper nome del campo che contiene il compilato
   * @param file_xml nome del campo ch econtiene il template
   * @param vo_name nome del viewobject da usare per ottenere il template
   * @param r record della tabella dei template
   */
  public Lettera(Row r,String vo_name,
                      String file_xml,
                      String file_jasper,
                      String _fName,
                      int _formato) throws Exception
  {
    ReportHandler rh=new ReportHandler();
    this.design=rh.getDesign(r,file_xml);
    params=design.getParameters();
    this.report=rh.getCompiled(r,vo_name,file_xml,file_jasper);
    this.formato=_formato;
    dest=new File(_fName);
    if(!dest.exists()){
      String suffix=ConfigurationConstants.FORMATO_CSV==formato?".csv":".pdf";
      dest=File.createTempFile(_fName,suffix);
    }
  }
  
  
  private void initialize() throws JRException
  {
    design=JRXmlLoader.load(this.template);
    params=design.getParameters();
    report=JasperCompileManager.compileReport(design);
  }
  
  /**
   * Crea una nuova mappa parametri e vi inserisce i
   * parametri di input
   * @param _map
   */
  public void setParametersMap(HashMap _map)
  {
    map=new HashMap();
    if(_map==null)
      return;
    for(int i=0;i<params.length;i++)
    {
      //non faccio nulla con i parametri di sistema
      if(params[i].isSystemDefined())
        continue;
      String pName=params[i].getName();
      Object o=_map.get(pName);
      //non c'è valore per questo parametro
      if(o==null)
        continue;
      //aggiungo il parametro
      map.put(pName,o);
    }
  }
  
  /**
   * Aggiunge un  nuovo parametro alla mappa esistente
   * (se non esiste, la crea)
   * @param value
   * @param name
   */
  public void addParameter(String name, Object value)
  {
    if(map==null)
      map=new HashMap();
    
    map.put(name,value);
  }
  
  /**
   * Crea la lettera in formato pdf usando il report e i parametri memorizzati
   * ed il bean di input come data source
   * @throws java.io.IOException
   * @throws net.sf.jasperreports.engine.JRException
   * @return file pdf
   * @param lb bean da usare come sorgente dati
   */
  public File createLetter(LetteraBean lb) throws JRException,IOException
  {
    JRDataSource ds=new JRBeanArrayDataSource(new Object[]{lb});
    return this.createLetter(ds);
  }
  
  public File createLetter(LetteraBean[] lb) throws JRException,IOException
  {
    JRDataSource ds=new JRBeanArrayDataSource(lb);
    return this.createLetter(ds);
  }
  
  /**
   * Crea la lettera in formato pdf usando il report e i parametri memorizzati
   * ed una empty data source
   * @throws java.io.IOException
   * @throws net.sf.jasperreports.engine.JRException
   * @return file pdf
   */
  public File createLetter() throws JRException,IOException
  {
    JRDataSource ds=new JREmptyDataSource();
    return this.createLetter(ds);
  }
  
  /**
   * Crea la lettera in formato pdf usando il report e i parametri memorizzati
   * ed la datasource di input come data source
   * @throws java.io.IOException
   * @throws net.sf.jasperreports.engine.JRException
   * @return file pdf
   * @param ds data source da usare come sorgente dati
   */
  public File createLetter(JRDataSource ds) throws JRException,IOException
  {
    JasperPrint print=JasperFillManager.fillReport(report,map,ds);
    this.createExportedFile(print, dest);
    return dest;
  }
  
  /**
   * Crea la lettera in formato pdf usando il report e i parametri memorizzati
   * ed la connessione di input come data source
   * @throws java.io.IOException
   * @throws net.sf.jasperreports.engine.JRException
   * @return 
   * @param conn connessione al db su cui si basa la query
   */
   public File createLetter(Connection conn) throws JRException,IOException
  {
    JasperPrint print=JasperFillManager.fillReport(report,map,conn);
    this.createExportedFile(print, dest);
    return dest;
  }
  
  private void createExportedFile(JasperPrint print, File f) throws JRException
  {
    if(ConfigurationConstants.FORMATO_CSV==this.formato)
    {
      //esportazione in csv
     JRCsvExporter exp=new JRCsvExporter();
     exp.setParameter(JRCsvExporterParameter.FIELD_DELIMITER,";");
     exp.setParameter(JRCsvExporterParameter.JASPER_PRINT,print);
     exp.setParameter(JRCsvExporterParameter.OUTPUT_FILE,f);
     exp.exportReport();
    }
    else
    {
      //JasperExportManager.exportReportToPdfFile(print,path);
      JRPdfExporter exp=new JRPdfExporter();
       exp.setParameter(JRExporterParameter.JASPER_PRINT,print);
       exp.setParameter(JRExporterParameter.OUTPUT_FILE,f);
       exp.setParameter(JRPdfExporterParameter.PDF_VERSION,
                        ConfigurationConstants.PDF_VERSION);
       exp.exportReport();
    }
  }
  
 
  
  public void setParametersMap(String ulss,String tpscr, 
  ViewObject ulss_vo, ViewObject params_vo) throws Exception
  {
    this.setParametersMap(ParametriSistema.getParamTemplate(ulss,tpscr,ulss_vo,params_vo));
    
  }

  protected void finalize() throws Throwable
  {
    this.template.delete();
    ParametriSistema.releaseLogo(this.map);
	release();
    super.finalize();
  }
  public void changeDestination(String _fName) throws IOException
  {
    String suffix=ConfigurationConstants.FORMATO_CSV==formato?".csv":".pdf";
    if(_fName==null)
    {
      dest=File.createTempFile("change",suffix);
    }
    else
    {
      dest=new File(_fName);
      if(!dest.exists())
        dest=File.createTempFile(_fName,suffix);
    }
  }
///////////////////////////////////////////        
}