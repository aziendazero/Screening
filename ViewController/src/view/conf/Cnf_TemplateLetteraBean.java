package view.conf;

import insiel.dataHandling.BlobUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import model.common.Cnf_SoTemplateViewRow;

import model.commons.ConfigurationConstants;
import model.commons.Lettera;
import model.commons.ParametriSistema;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

import org.apache.myfaces.trinidad.event.ReturnEvent;

import view.backing.ParentBackingBean;


public class Cnf_TemplateLetteraBean extends ParentBackingBean {

    
    public void handlePopupReturn(ReturnEvent event)
    {
      if (event.getReturnValue() != null)
      {
          this.handleMessage(FacesMessage.SEVERITY_ERROR, (String)event.getReturnValue() );
      }
    }
    
    /**Method to download file from actual path
         * @param facesContext
         * @param outputStream
         */
        public void downloadFileListener(FacesContext facesContext, OutputStream outputStream) throws IOException {

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoTemplateView1Iterator");
            ViewObject vo = voIter.getViewObject();
            Cnf_SoTemplateViewRow curRow = (Cnf_SoTemplateViewRow) vo.getCurrentRow();

            BlobDomain blob = curRow.getFilexml();
            BufferedInputStream in = null;

            in = new BufferedInputStream(blob.getBinaryStream());

            int b;
            byte[] buffer = new byte[10240];
            while ((b = in.read(buffer, 0, 10240)) != -1) {
                outputStream.write(buffer, 0, b);
            }
            outputStream.close();
        }

    public void downloadPreviewListener(FacesContext facesContext, OutputStream outputStream) {
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCnfLetteracentroExtendedView1Iterator");
        ViewObject vo1 = voIter.getViewObject();
        Row current = vo1.getCurrentRow();
        Integer codtm = (Integer) current.getAttribute("Codtempl");
        File preview = null;
        HashMap parameters = null;
        try {
            //controllare che non sia null
            if (codtm == null)
                throw new Exception("Nessun template selezionato per il download");
            DCIteratorBinding voTemplateIter = bindings.findIteratorBinding("Cnf_SoTemplateView2Iterator");
            ViewObject vo = voTemplateIter.getViewObject();
            Row[] result = vo.getFilteredRows("Codtempl", codtm);
            //controllare che non sia vuoto
            if (result.length == 0)
                throw new Exception("Nessun template selezionato per il download");

            Cnf_SoTemplateViewRow r = (Cnf_SoTemplateViewRow) result[0];
            // GeneratoreInviti gi=new GeneratoreInviti();
            String nome = r.getNomefile();
            if (nome.indexOf(".") >= 0)
                nome = nome.substring(0, nome.lastIndexOf("."));
            //leggo i parametri per l aulss ed il tipo di screening del template selezionato
            parameters = ParametriSistema.readUlssParams((String) current.getAttribute("Ulss"),
                                                (String) current.getAttribute("Tpscr"));

            Lettera l = new Lettera(r, "Cnf_SoTemplateView1", "Filexml", "Compiled", "Anteprima_" + nome,
                            ConfigurationConstants.FORMATO_PDF);

            l.setParametersMap(parameters);
            preview = l.createLetter();
            
            //download del file
            FileInputStream fis;
            byte[] b;
            try {
                fis = new FileInputStream(preview);

                int n;
                while ((n = fis.available()) > 0) {
                    b = new byte[n];
                    int res = fis.read(b);
                    outputStream.write(b, 0, b.length);
                    if (res == -1)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream.flush();
            outputStream.close();
            
        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile visualizzare l'anteprima: " + ex.getMessage() );
            
        } finally {
            if (preview != null)
                preview.delete();
            ParametriSistema.releaseLogo(parameters);
        }
    }

    public void downloadInsertoListener(FacesContext facesContext, OutputStream outputStream) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoInsertoView1Iterator");
        ViewObject vo = voIter.getViewObject();

        Row r = vo.getCurrentRow();
        File t = null;
        try {
            if (r == null)
                throw new Exception("Nessun inserto selezionato per il download");
            //invio il file alla response della servlet
            t = BlobUtils.getFileFromBlob((BlobDomain) r.getAttribute("Inserto"), (String) r.getAttribute("Nomefile"));
            
            //download del file
            FileInputStream fis;
            byte[] b;
            try {
                fis = new FileInputStream(t);

                int n;
                while ((n = fis.available()) > 0) {
                    b = new byte[n];
                    int res = fis.read(b);
                    outputStream.write(b, 0, b.length);
                    if (res == -1)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream.flush();

        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile recuperare l'inserto: " + ex.getMessage());

        } finally {
            if (t != null)
                t.delete();
        }

    }
}
