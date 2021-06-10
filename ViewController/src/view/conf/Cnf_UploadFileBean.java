package view.conf;

import insiel.dataHandling.BlobUtils;

import java.awt.image.BufferedImage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.io.OutputStream;

import java.util.Base64;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import javax.imageio.ImageIO;

import javax.servlet.ServletContext;

import javax.servlet.http.Cookie;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import model.common.Cnf_SoAziendaViewRow;

import model.common.Cnf_SoTemplateViewRow;

import model.print.ReportHandler;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import net.sf.jasperreports.engine.design.JasperDesign;

import net.sf.jasperreports.engine.xml.JRXmlLoader;

import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

import org.apache.myfaces.trinidad.model.UploadedFile;


import view.backing.ParentBackingBean;

import view.commons.CookiesManager;

public class Cnf_UploadFileBean extends ParentBackingBean {

    private UploadedFile _file;

    public Cnf_UploadFileBean() {
    }

    /*private boolean render = true;
    
    public Boolean getRender() {
        if (this._file==null)
            return false;
        
        return true;
    }
    
    public void setRender(boolean render) {
        this.render=render;
    }*/
        
    public String insertFile() {
        try {
            UploadedFile file = this.getFile();
            FacesContext fctx = FacesContext.getCurrentInstance();
            ServletContext servletCtx = (ServletContext) fctx.getExternalContext().getContext();
            AdfFacesContext afc = AdfFacesContext.getCurrentInstance();
            Map _pfS = afc.getPageFlowScope();
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            String action = (String) _pfS.get("action");
            
            if ("logo".equals(action)) {
    
                // UPLOAD DEL LOGO AZIENDALE
                String imageDirPath = servletCtx.getRealPath("/");
    
                DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoAziendaView1Iterator");
                ViewObject vo = voIter.getViewObject();
                Cnf_SoAziendaViewRow r = (Cnf_SoAziendaViewRow) vo.getCurrentRow();
    
                if (file == null)
                    throw new Exception("Non e' stato caricato nessun file");
                
                InputStream inputStream = file.getInputStream();
                BufferedImage input = ImageIO.read(inputStream);
                File outputFile = new File(imageDirPath + file.getFilename());
                ImageIO.write(input, "gif", outputFile);

                r.setLogo(BlobUtils.getBlobFromFile(outputFile));

                //cancello il vecchio file, se c'e'

                Cookie c =
                    CookiesManager.getCookie("logoUlss" + r.getCodaz().trim(),
                                             (HttpServletRequest) fctx.getExternalContext().getRequest());
                if (c != null) {
                    File old = new File(c.getValue());
                    old.delete();
                }
                //creo il nuovo cookie e lo invio alla response
                CookiesManager.updateFileCookie("logoUlss" + r.getCodaz().trim(), outputFile.getPath(),
                                                (HttpServletResponse) fctx.getExternalContext().getResponse(),
                                                (HttpServletRequest) fctx.getExternalContext().getRequest());

                outputFile.delete();
                inputStream.close();
    
            } else if ("template".equals(action)) {
    
                //UPLOAD TEMPLATE JASPER
                String templateDirPath = servletCtx.getRealPath("/");
                DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoTemplateView1Iterator");
                ViewObject vo = voIter.getViewObject();
                Cnf_SoTemplateViewRow r = (Cnf_SoTemplateViewRow) vo.getCurrentRow();

                if (r == null)
                    throw new Exception("Nessun record selezionato per l'inserimento del file template");

                if (file == null)
                    throw new Exception("Non e' stato caricato nessun file");
                
                String fileName = file.getFilename();
                if (fileName != null && !fileName.endsWith(".xml") && !fileName.endsWith(".jrxml"))
                    throw new Exception("Il file selezionato non e' del tipo atteso (.xml .jrxml)");

                InputStream inputStream = file.getInputStream();
                BlobDomain blobDomain = new BlobDomain();
                OutputStream out = blobDomain.getBinaryOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead = 0;

                while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                r.setFilexml(blobDomain);

                //imposto il nome del file, eventualmente troncato
                int length = vo.getAttributeDef(vo.getAttributeIndexOf("Nomefile")).getPrecision();

                if (fileName.length() > length) { //accorcio il nome prima dell'estensione
                    String end = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                    fileName = fileName.substring(0, length - end.length()) + end;
                }
                r.setNomefile(fileName);

                //inserisco il file compilato              
                JasperDesign design = JRXmlLoader.load(blobDomain.getBinaryStream());
                
                BlobDomain bd2 = new BlobDomain();
                BufferedOutputStream out2 = new BufferedOutputStream(bd2.getBinaryOutputStream());
                try {
                    JasperCompileManager.compileReportToStream(design, out2);
                } catch (JRException jex) {
                    throw new Exception("Errore di compilazione del template: " + jex.getMessage());
                }

                r.setCompiled(bd2);
                
                //cancello il file
                inputStream.close();
                this.setFile(null);
                return null;
    
            } else if ("inserto".equals(action)){
                DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoInsertoView1Iterator");
                ViewObject vo = voIter.getViewObject();
                Row r = vo.getCurrentRow();
    
                if (r == null)
                    throw new Exception("Nessun record selezionato per l'inserimento dell'inserto");

                if (file == null)
                    throw new Exception("Non e' stato caricato nessun file");
                    
                String fileName = file.getFilename();
                if (fileName != null && !fileName.endsWith(".pdf"))
                    throw new Exception("Il file selezionato non e' del tipo atteso (.pdf)");

                InputStream inputStream = file.getInputStream();
                BlobDomain blobDomain = new BlobDomain();
                OutputStream out = blobDomain.getBinaryOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead = 0;

                while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                r.setAttribute("Inserto", blobDomain);

                //imposto il nome del file, eventualmente troncato
                int length = vo.getAttributeDef(vo.getAttributeIndexOf("Nomefile")).getPrecision();

                if (fileName.length() > length) { //accorcio il nome prima dell'estensione
                    String end = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                    fileName = fileName.substring(0, length - end.length()) + end;
                }
                r.setAttribute("Nomefile", fileName);
                
                vo.clearCache();
                
                //cancello il file
                inputStream.close();
                
                this.setFile(null);
                return null;
                
            }  else if ("impexplog".equals(action)){
                DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfImpexpView1Iterator");
                ViewObject vo = voIter.getViewObject();
    
                File f = null;
    
                if (file == null)
                    throw new Exception("Non e' stato caricato nessun file");
                String fileName = file.getFilename();
                if (fileName != null && !fileName.endsWith(".xml") && !fileName.endsWith(".jrxml"))
                    throw new Exception("Il file selezionato non e' del tipo atteso (.xml .jrxml)");

                InputStream inputStream = file.getInputStream();
                BlobDomain blobDomain = new BlobDomain();
                OutputStream out = blobDomain.getBinaryOutputStream();
                byte[] buffer = new byte[8192];
                int bytesRead = 0;

                while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                Row r = vo.getCurrentRow();
                r.setAttribute("Template", blobDomain);

                //inserisco il file compilato
                JasperDesign design = JRXmlLoader.load(blobDomain.getBinaryStream());

                BlobDomain bd2 = new BlobDomain();
                BufferedOutputStream out2 = new BufferedOutputStream(bd2.getBinaryOutputStream());
                try {
                    JasperCompileManager.compileReportToStream(design, out2);
                } catch (JRException jex) {
                    throw new Exception("Errore di compilazione del template: " + jex.getMessage());
                }
                
                r.setAttribute("TemplateCompilato", bd2);

                //elimino le tracce dell'upoad
                inputStream.close();
                
                this.setFile(null);
                return null;
    
            }

            
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
            ctx.addMessage(null, fm);
        }
        
        return null;
    }

    public void setFile(UploadedFile _file) {
        this._file = _file;
    }

    public UploadedFile getFile() {
        return _file;
    }

    public static void main(String[] args){
       File f =  new File("C:\\Users\\Utente\\DesktopCardio50_B.jrxml.xml");
    }
}
