package view.commons;

import insiel.dataHandling.BlobUtils;

import insiel.web.ServletUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;
import java.sql.ResultSet;

import javax.faces.context.FacesContext;

import javax.servlet.*;
import javax.servlet.http.*;

import oracle.jbo.Row;
import oracle.jbo.domain.BlobDomain;

public class ImageViewServlet extends HttpServlet {
    private static final String CONTENT_TYPE = "image/jpg; charset=utf-8";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType(CONTENT_TYPE);
        response.setContentType(CONTENT_TYPE);
        String ulssId = request.getParameter("id");

        OutputStream os = response.getOutputStream();
        Connection conn = null;

        try {

            //se ho gia' il cookie per il logo non serve estrarlo nuovamente
            Cookie logoC = CookiesManager.getCookie("logoUlss" + ulssId, request);
            File x = null;
            if (logoC != null) {
                //il cookie c'e' gia', controllo ch eil file ci sia ancora
                x = new File(logoC.getValue());
                if (x.exists())
                    ServletUtils.sendFile(response, x, "Logo", "image/JPEG", false);
            } else {

                //il cookie va creato ed il file va estratto dal DB
                Context ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup("jdbc/ScreeningDS");

                conn = ds.getConnection();
                PreparedStatement statement =
                    conn.prepareStatement("SELECT Cnf_SoAzienda.CODAZ, Cnf_SoAzienda.LOGO FROM  SO_AZIENDA Cnf_SoAzienda WHERE Cnf_SoAzienda.CODAZ = ?");

                statement.setInt(1, new Integer(ulssId));
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    Blob blob = rs.getBlob("LOGO");

                    x = File.createTempFile("logoUlss",".gif");
                    x = BlobUtils.getFileFromBlob(new BlobDomain(blob), x.getAbsolutePath());
                    if (x != null) {
                        CookiesManager.updateFileCookie("logoUlss" + ulssId,
                                                        x.getAbsolutePath(), response,
                                                        request);
                    }
                    
                    BufferedInputStream in = new BufferedInputStream(blob.getBinaryStream());
                    int b;
                    byte[] buffer = new byte[10240];

                    while ((b = in.read(buffer, 0, 10240)) != -1) {
                        os.write(buffer, 0, b);
                    }
                    os.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }
    
}
