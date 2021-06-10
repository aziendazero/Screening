package view.print;

import insiel.utilities.dataformats.DateUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URLDecoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.*;
import javax.servlet.http.*;

import model.common.PL_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.Lettera;
import model.commons.LetteraBean;
import model.commons.LetteraEtichetta;
import model.commons.PianoLavoroBean;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.PL_Bean;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

public class Print_PDFServlet extends HttpServlet {
    @SuppressWarnings("compatibility:-5750725392394241395")
    private static final long serialVersionUID = 4777381464927981499L;
    private static final String CONTENT_TYPE = "application/pdf";
    private static final String CONTENT_DISPOSITION = "inline; filename=\"Document.pdf\"";
    private static final int DEFAULT_BUFFER_SIZE = 10240;
    private String filePath;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //sString requestedFile = request.getParameter("filename");
        // Get requested file by path info.
        String requestedFile = request.getParameter("name");
        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        // Check if file is actually supplied to the request URI.
        if (requestedFile == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }


        // I want to invoke a pdf that is located on the machine where the application is running
        this.filePath = requestedFile;

        // Decode the file name (might contain spaces and on) and prepare file object.
        File file = new File(filePath);

        try {

            response.reset();
            response.setContentType(CONTENT_TYPE);
            response.setHeader("Content-Disposition", CONTENT_DISPOSITION);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Content-Disposition", "filename=\"" + file.getName() + "\"");
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

        } catch (Exception ex) {

            ex.printStackTrace();

        } finally {
            
            if (output != null) {
                output.close();
            }
        }
    }
}
