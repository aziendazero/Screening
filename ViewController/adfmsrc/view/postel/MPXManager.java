package view.postel;

import insiel.dataHandling.DateUtils;

import insiel.dataHandling.PdfUtils;

import insiel.postel.mpx.Header;

import insiel.postel.mpx.MPX;

import insiel.postel.mpx.Pdf;

import insiel.postel.mpx.Set;

import java.io.File;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import model.commons.ConfigurationConstants;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.commons.beanutils.BeanUtils;

public class MPXManager {
    private PostelBean bean;
    private Header header;
    private String ulss;
    private String tpscr;

    /**
     * COSTRUTTORE.
     * @throws java.lang.Exception
     * @param tpscr tipo di screening
     * @param ulss azienda snaitaria
     * @param parameters viewObject da cui leggere i parametri di Postel
     */
    public MPXManager(ViewObject parameters, String ulss, String tpscr) throws Exception {
        this.ulss = ulss;
        this.tpscr = tpscr;
        bean = new PostelBean();
        //inizializzo ogni proprieta' del bean con l'equivalente proprieta' memorizata nel db
        Map map = BeanUtils.describe(bean);
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
            String property = (String) iter.next();
            parameters.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND NOME_PARAM='" + property +
                                      "'");
            parameters.executeQuery();
            Row r = parameters.first();
            if (r == null)
                continue;
            BeanUtils.setProperty(bean, property, (String) r.getAttribute("ValoreParam"));
        }
        //inizializzo l'header
        header = new Header(bean.getZcode(), bean.getUsername(), bean.getPassword());
    }

    /**
     * Crea l'oggetto MPX.
     * @throws java.lang.Exception
     * @return
     * @param raccomandata se si tratta di un set di raccomndate oppure no
     * @param envelopes vettore di envelope
     * @param pdf file pdf
     */
    public MPX createMPX(File pdf, Vector envelopes, boolean raccomandata) throws Exception {
        String id = ulss + tpscr + DateUtils.dateToString("yyyyMMddHHmm", new Date());
        Pdf p = new Pdf(envelopes, bean.getType(), id, pdf, PdfUtils.getPdfPages(pdf));
        Vector v = new Vector();
        v.addElement(p);
        Set s =
            new Set(v, ConfigurationConstants.POSTEL_TEST, id,
                    bean.getCsdg() == null ? false : Boolean.valueOf(bean.getCsdg()).booleanValue(), raccomandata,
                    bean.getRagionesocialemittente(), bean.getIndirizzomittente(), bean.getCapmittente(),
                    bean.getCittamittente(), bean.getProvinciamittente(), bean.getNazionemittente());

        MPX mpx = new MPX(header, s);
        return mpx;
    }

    /**
     * Metodo che costruisce l'oggetto MPX e ne ottiene il file xml finale in
     * un file temporaneo.
     * @throws java.lang.Exception
     * @return File temporaneo
     * @param raccomandata
     * @param envelopes
     * @param pdf
     */
    public File getPostelFile(File pdf, Vector envelopes, boolean raccomandata) throws Exception {
        File f = File.createTempFile("MPX", ".xml");
        return this.getPostelFile(pdf, envelopes, raccomandata, f);
    }

    /**
     * Metodo che costruisce l'oggetto MPX e ne ottiene il file xml finale
     * nel file fornito in input.
     * @throws java.lang.Exception
     * @return
     * @param output
     * @param raccomandata
     * @param envelopes
     * @param pdf
     */
    public File getPostelFile(File pdf, Vector envelopes, boolean raccomandata, File output) throws Exception {
        MPX mpx = this.createMPX(pdf, envelopes, raccomandata);
        return mpx.getXMLFile(output);
    }

    public PostelBean getBean() {
        return bean;
    }

    public void setBean(PostelBean bean) {
        this.bean = bean;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
