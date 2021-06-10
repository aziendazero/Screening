package view.conf;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import model.common.Cnf_SoTemplateViewRow;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import oracle.jbo.domain.BlobDomain;

import org.apache.myfaces.trinidad.event.ReturnEvent;

import view.backing.ParentBackingBean;

import view.commons.action.Parent_DataForwardAction;

public class Cnf_DataForwardAction extends ParentBackingBean {

    
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
}
