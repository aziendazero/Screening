package view.print;

import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.DateUtils;
import insiel.dataHandling.FileUtils;
import insiel.dataHandling.ObjectTransformationUtils;
import insiel.dataHandling.PdfUtils;

import insiel.postel.mpx.Envelope;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ActionListener;

import model.common.Print_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.Lettera;
import model.commons.LetteraEtichetta;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.PrintBean;

import model.inviti.GeneratoreInviti;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.nav.RichButton;
import oracle.adf.view.rich.event.BasePolytypeListener;
import oracle.adf.view.rich.event.SetPropertyListener;

import oracle.adfinternal.view.faces.event.rich.FileDownloadActionListener;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

import view.commons.action.Parent_DataForwardAction;

import view.postel.MPXManager;
import view.postel.PostelBean;
import view.postel.TXTPostelManager;

import view.reports.Documento;

import view.util.Utility;

public class Print_DataAction extends Parent_DataForwardAction {
    private RichButton btnStampaHidden;

    public Print_DataAction() {
    }

    public void print(FacesContext facesContext, OutputStream outputStream) {
        try {
            ByteArrayOutputStream by =
                (ByteArrayOutputStream) ADFContext.getCurrent().getRequestScope().get("outputStream");
            by.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    private Map<String, Object> print() {
        Print_AppModule am =
            (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        PrintBean bean =
            (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();
        File stampa = null;
        File end = null;

        OutputStream outputStream = new ByteArrayOutputStream();
        Map<String, Object> _ret = new HashMap<String, Object>();

        MPXManager m = null;
        File ins = null;
        TXTPostelManager tpmng = null;
        try {
            //controllo il permesso di stampare
            AccessManager.checkPermission("SOGestioneStampe");

            boolean postel = false;
            boolean posteltxt = true;
            /* if(!"locale".equals(bean.getTpStampa())){
                if("Postel".equals(bean.getTpStampa()))*/
            if (!"Posteltxt".equals(bean.getTpStampa())) {
                posteltxt = false;
                if ("Postel".equals(bean.getTpStampa())) {
                    //STAMPA CON POSTEL
                    //AccessManager.checkPermission("SOPostel",ctx.getHttpServletRequest().getSession());
                    postel = true;
                    m = new MPXManager(am.findViewObject("A_SoCnfParametriSistemaView1"),
                                       (String) ADFContext.getCurrent().getSessionScope().get("ulss"),
                                       (String) ADFContext.getCurrent().getSessionScope().get("scr"));
                }
                /*
                        } else
                          //stampa con POSTEL TXT
                          {
                          posteltxt=true;
                          tpmng=new TXTPostelManager((String)ADFContext.getCurrent().getSessionScope().get("ulss"),
                                  (String)ADFContext.getCurrent().getSessionScope().get("scr"));
                          }
                        }
                        */

                Vector envelopes = null;
                if ("lettere".equals(bean.getCosaStampare())) {

                    System.out.println("stampe massive - prima dell'esecuzione di 'stampaLettere'");

                    Object[] out = this.stampaLettere(postel, m);

                    System.out.println("stampe massive - dopo l'esecuzione di 'stampaLettere'");

                    stampa = (File) out[0];
                    envelopes = (Vector) out[1];

                    //e aggiorno correttamente i range delle lettere postel
                    if (postel) {
                        int base = 0;
                        for (int i = 0; i < envelopes.size(); i++) {
                            Envelope en = (Envelope) envelopes.elementAt(i);
                            en.setPageStart(base + 1);
                            base = base + en.getPageEnd();
                            en.setPageEnd(base);
                        }
                    }
                } else if ("etichette".equals(bean.getCosaStampare())) {
                    if (postel || posteltxt)
                        throw new Exception("Impossibile inviare a Postel la stampa di sole etichette");

                    //  stampo tutt ele letichette in un file unico
                    stampa = this.stampaEtichette(false);
                }

                //lettere + etichette (separate una dall'altra)
                else {
                    //preparo le lettere
                    Vector v1 = this.getLettere();
                    Vector v2 = null;
                    Vector v = null;
                    Vector v3 = null;
                    int l_ins = -1;
                    //se serve preparo le etichette
                    if (bean.getCosaStampare().indexOf("etichette") > 0) {
                        v2 = this.getEtichette(true);
                        v = ObjectTransformationUtils.merge(v1, v2);
                    }

                    //se serve preparo l'inserto
                    if (bean.getCosaStampare().endsWith("inserti")) {
                        if (bean.getInserto() == null || bean.getInserto().intValue() <= 0)
                            throw new Exception("Selezionare un inserto da utilizzare per la stampa");

                        ViewObject inserti = am.findViewObject("Cnf_SoInsertoView1");
                        Row[] insertiRows = inserti.getFilteredRows("Idinserto", bean.getInserto());
                        Row inn = insertiRows[0];
                        if (inn == null)
                            throw new Exception("Nessun inserto trovato con codice " + bean.getInserto());

                        ins =
                            BlobUtils.getFileFromBlob((BlobDomain) inn.getAttribute("Inserto"),
                                                      File.createTempFile("inserto", ".pdf").getAbsolutePath());
                        l_ins = PdfUtils.getPdfPages(ins);
                        v3 = new Vector();
                        //ogni due file (lettera + etichetta) aggiungo l'inserto
                        if (v != null) {
                            for (int i = 0; i < v.size(); i++) {
                                v3.addElement(v.elementAt(i));
                                if (i > 0 && i % 2 > 0)
                                    v3.addElement(ins);
                            }
                        }

                        //ho solo lettere
                        else {
                            for (int i = 0; i < v1.size(); i++) {
                                v3.addElement(v1.elementAt(i));
                                v3.addElement(ins);
                            }
                        }
                        v = v3;
                    }

                    if (postel) {
                        //creo i dati per Postel
                        PostelBean p_bean = m.getBean();
                        envelopes =
                            this.getPostelData(v1, p_bean.getWorkprocessID(), p_bean.getWorkprocessIDraccomandata(),
                                               p_bean.getEUserZCode(), p_bean.getNazionemittente());

                        //e aggiorno correttamente i range
                        int base = 0;
                        for (int i = 0; i < envelopes.size(); i++) {
                            Envelope en = (Envelope) envelopes.elementAt(i);
                            en.setPageStart(base + 1);
                            int add = 0;
                            //aggiungo le pagine delle etichette
                            if (v2 != null) {
                                File f2 = (File) v2.elementAt(i);
                                add += PdfUtils.getPdfPages(f2);
                            }
                            //aggiungo le pgaine degli inserti
                            if (ins != null)
                                add += l_ins;

                            base = base + en.getPageEnd() + add;
                            en.setPageEnd(base);
                        }
                    }

                    stampa = File.createTempFile("stampaTemp", ".pdf");
                    PdfUtils.concatenatePdf(v, stampa.getAbsolutePath());

                    for (int i = 0; i < v.size(); i++) {
                        File f = (File) v.elementAt(i);
                        f.delete();
                    }
                }

                //09-01-2013 sara: aggiunto journaling
                Map sess = ADFContext.getCurrent().getSessionScope();
                String user = (String) sess.get("user");
                Boolean flag = (Boolean) sess.get("flagAbilJournal");
                if (flag != null && flag.booleanValue()) {
                    String tpscr = (String) sess.get("scr");
                    String ulss = (String) sess.get("ulss");
                    am.preapareJournaling(user, ulss, tpscr);
                }
                //fine sara

                am.getTransaction().commit();

                //se si tratta di un file di grosse dimensioni lo zippo

                System.out.println("stampe massive - prima dell'operazione di compressione");

                if (!"selection".equals(ADFContext.getCurrent().getRequestScope().get("quanti")) && !postel &&
                    !posteltxt) {
                    //  end = File.createTempFile("Stampe", ".zip", temp);
                    end = File.createTempFile("Stampe", ".zip");
                    end = FileUtils.zipFiles(new File[] { stampa }, null, end);
                } else {
                    end = stampa;
                }

                System.out.println("stampe massive - eseguita operazione di compressione");

                if ("locale".equals(bean.getTpStampa())) {
                    if (end.getName().indexOf(".pdf") > 0) {
                        this.outputPrintServlet(outputStream, end);
                        _ret.put("fileName", "DocumentoSingolo.pdf");
                        _ret.put("contentType", "application/pdf");
                        _ret.put("outputStream", outputStream);
                    } else if (end.getName().indexOf(".zip") > 0) {
                        this.outputPrintServlet(outputStream, end);
                        _ret.put("fileName", "documenti.zip");
                        _ret.put("contentType", "application/zip");
                        _ret.put("outputStream", outputStream);
                    }
                } else {
                    //STAMPA CON POSTEL
                    //  if (postel){
                    //AccessManager.checkPermission("SOPostel",ctx.getHttpServletRequest().getSession());
                    end = m.getPostelFile(stampa, envelopes, true);

                    if ("Postel".equals(bean.getTpStampa())) {
                        this.outputPrintServlet(outputStream, end);
                        if (end != null)
                            end.delete();

                        _ret.put("fileName", "FileToPostel.xml");
                        _ret.put("contentType", "text/xml");
                        _ret.put("outputStream", outputStream);
                    }
                }
                /*   } else //sono con stampa postel TXT
                                {
                                        tpmng.getPostelTxtFile(ctx);
                                } */
            } //fine stampa locale o postel
            else //postel txt
            {
                posteltxt = true;
                if ("etichette".equals(bean.getCosaStampare()))
                    throw new Exception("Impossibile inviare a Postel la stampa di sole etichette");
                if (bean.getCosaStampare().indexOf("inserti") > 0)
                    throw new Exception("Impossibile inviare a PostelTxt la stampa di inserti");

                tpmng =
                    new TXTPostelManager((String) ADFContext.getCurrent().getSessionScope().get("ulss"),
                                         (String) ADFContext.getCurrent().getSessionScope().get("scr"));
                _ret = tpmng.getPostelTxtFile();
            }
        } catch (Exception ex) {
            am.getTransaction().rollback();
            System.out.println("stampe massive - eccezione in Print_DataAction:findForward");
            ex.printStackTrace();

            //throw new RuntimeException("Impossibile recuperare la stampa: " + ex.getMessage());
            handleException("Impossibile recuperare la stampa: " + ex.getMessage());
        } finally {
            if (stampa != null)
                stampa.delete();
            if (ins != null)
                ins.delete();
        }

        return _ret;
    }

    @Override
    protected void setAppModule() {
        this.amName = "Print_AppModule";
    }

    /**
     * Genera il pdf contenente la/le lettere
     * @throws java.lang.Exception
     * @return
     * @param ctx
     */
    //@codecoach:disable nextline (final)
    protected Object[] stampaLettere(boolean postel, MPXManager m) throws Exception {
        Object[] out = new Object[2];

        System.out.println("stampe massive - prima dell'esecuzione di 'getLettere'");
        Vector toPrint = this.getLettere();
        System.out.println("stampe massive - dopo l'esecuzione di 'getLettere'");

        //nessuna lettera
        if (toPrint.size() == 0)
            return out;

        if (postel) {
            if (m == null)
                throw new Exception("Impossibile inviare a postel senza i dati necessari");
            PostelBean b = m.getBean();
            out[1] =
                this.getPostelData(toPrint, b.getWorkprocessID(), b.getWorkprocessIDraccomandata(), b.getEUserZCode(),
                                   b.getNazionemittente());
        }

        //una sola lettera
        if (toPrint.size() == 1) {
            out[0] = (File) toPrint.elementAt(0);
            return out;
        }

        //piu' lettere da concatenare e zippare
        File f = null;

        System.out.println("stampe massive - prima dell'operazione di concatenazione");

        //genero il file pdf unico da mandare in output
        if (!toPrint.isEmpty()) {
            f = File.createTempFile("Stampe", ".pdf");

            if (!PdfUtils.concatenatePdf(toPrint, f.getAbsolutePath()))

                throw new Exception("Creazione file pdf unico non riuscita");

            for (int i = 0; i < toPrint.size(); i++) {
                File f2 = (File) toPrint.elementAt(i);
                f2.delete();
            }
        }

        System.out.println("stampe massive - eseguita operazione di concatenazione");

        //return f;
        out[0] = f;
        return out;
    }

    protected File stampaEtichette(boolean separated) throws Exception {
        Vector toPrint = this.getEtichette(separated);

        //nessuna lettera
        if (toPrint.size() == 0)
            return null;
        //una sola lettera
        if (toPrint.size() == 1)
            return (File) toPrint.elementAt(0);

        //piu' lettere da concatenare e zippare
        File f = null;

        //genero il file pdf unico da mandare in output
        if (!toPrint.isEmpty()) {
            f = File.createTempFile("Etichette", ".pdf");

            if (!PdfUtils.concatenatePdf(toPrint, f.getAbsolutePath()))

                throw new Exception("Creazione file pdf unico non riuscita");

            for (int i = 0; i < toPrint.size(); i++) {
                File f2 = (File) toPrint.elementAt(i);
                f2.delete();
            }
        }

        return f;

    }

    void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    protected void outputPrintServlet(OutputStream output, File f) throws IOException {
        //17102013 S.Gaion : tolto spoolx per incompatibilita con il CAS
        // lo zip viene scaricato direttamente
        InputStream input = new FileInputStream(f);

        try {
            while (input.available() > 0) {
                output.write(input.read());
            }

        } finally {
            input.close();
            output.flush();
            output.close();
            if (f != null)
                f.delete();
        }
    }

    private Envelope getEnvelope(Row r, String tpscr, File f, int workProcessID, String EUserZCode,
                                 String nazioneDefault, String mod_spedizione, ApplicationModule am) throws Exception {
        //nazioneDefault="Italia";
        //workProcessID=1;

        LetteraEtichetta l = this.getLetteraEtichettaBean(r, tpscr, mod_spedizione, am);
        int npages = PdfUtils.getPdfPages(f);
        //customer id: codts_timestamp
        String customerID =
            r.getAttribute("Codts") + "_" +
            DateUtils.dateToString("yyyyMMddHHmmss", (oracle.jbo.domain.Timestamp) r.getAttribute("Dtcreazione"));

        //in questa prima fase memorizzo solo il numero di pagine, non
        //il range definitivo (che dipendera' da etichette, insertie e altro)
        Envelope env = new Envelope(1, //pagestart
                                    npages, //pageend
                                    workProcessID, //"workprocessid",
                                    customerID, //"customerenevelopeid",
                                    (String) null, //ddressline1
                                    l.getCognome() + " " + l.getNome(), //addressline2
                                    l.getIndirizzo(), //addressline3
                                    l.getCap(), l.getComune(), l.getProvincia(), nazioneDefault);

        return env;
    }

    protected Vector getEtichette(boolean separated) throws Exception {
        PrintBean bean =
            (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();
        Print_AppModule am =
            (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String mod_spedizione = (String) ADFContext.getCurrent().getSessionScope().get("mod_spedizione");

        String voName = (String) ADFContext.getCurrent().getRequestScope().get("vo");
        ViewObject vo = am.findViewObject(voName);
        RowSetIterator iter = ViewHelper.getRowSetIterator(vo, "etichette_iter");

        //leggo il template da usare
        ViewObject templates = am.findViewObject("Print_SoTemplateEtichetteView1");

        try {
            Row[] result = templates.getFilteredRows("Codtempl", bean.getEtichetta());
            if (result.length == 0)
                throw new Exception("Nessun modello di etichetta selezionato");
            // BlobDomain blob=(BlobDomain)result[0].getAttribute("Filexml");

            Vector v = new Vector();
            // Lettera l=new Lettera(blob,"Etichette"+System.currentTimeMillis());


            //se ho selezionato solo un invito da stampare
            if ("selection".equals(ADFContext.getCurrent().getRequestScope().get("quanti"))) {

                Row r = vo.getCurrentRow();

                Lettera l =
                    new Lettera(result[0], "Print_SoTemplateEtichetteView1", "Filexml", "Compiled",
                                "Etichette" + System.currentTimeMillis(), ConfigurationConstants.FORMATO_PDF);

                l.setParametersMap(ulss, tpscr, am.findViewObject("A_SoAziendaView1"),
                                   am.findViewObject("A_SoCnfPartemplateView1"));

                File f = l.createLetter(this.getLetteraEtichettaBean(r, tpscr, mod_spedizione, am));
                v.addElement(f);

            } else { //ho tutti i record da stampare


                //creo un array di bean da usare come datasource
                LetteraEtichetta[] le = new LetteraEtichetta[vo.getRowCount()];
                int i = 0;
                while (iter.hasNext()) {
                    Row r = iter.next();

                    le[i] = this.getLetteraEtichettaBean(r, tpscr, mod_spedizione, am);
                    i++;

                }


                //se si tratta di etichette separate...
                if (separated) {
                    Lettera l =
                        new Lettera(result[0], "Print_SoTemplateEtichetteView1", "Filexml", "Compiled",
                                    "Etichette" + System.currentTimeMillis(), ConfigurationConstants.FORMATO_PDF);

                    l.setParametersMap(ulss, tpscr, am.findViewObject("A_SoAziendaView1"),
                                       am.findViewObject("A_SoCnfPartemplateView1"));
                    //creo un pdf con ogni etichetta
                    for (int j = 0; j < le.length; j++) {
                        l.changeDestination("Etichetta");
                        File f = l.createLetter(le[j]);
                        v.addElement(f);
                    }
                } else {
                    //creo un pdf unico
                    Lettera l =
                        new Lettera(result[0], "Print_SoTemplateEtichetteView1", "Filexml", "Compiled",
                                    "Etichette" + System.currentTimeMillis(), ConfigurationConstants.FORMATO_PDF);

                    l.setParametersMap(ulss, tpscr, am.findViewObject("A_SoAziendaView1"),
                                       am.findViewObject("A_SoCnfPartemplateView1"));

                    File f = l.createLetter(le);

                    v.addElement(f);
                }
            }

            return v;
        } catch (Exception ex) {

            // ex.printStackTrace();
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

    }

    private LetteraEtichetta getLetteraEtichettaBean(Row r, String tpscr, String mod_spedizione, ApplicationModule am) {
        if (r == null)
            return null;

        //ricerca tessera sanitaria
        String tesseraSanitaria =
            SoggUtils.trovaTessera(am, (String) r.getAttribute("Codts"), (String) r.getAttribute("Ulss"));

        String tel = null;
        if (r.getAttribute("Tel1") == null)
            tel = (String) r.getAttribute("Tel2");
        else if (r.getAttribute("Tel2") == null)
            tel = (String) r.getAttribute("Tel1");
        else
            tel = (String) r.getAttribute("Tel1") + ", " + (String) r.getAttribute("Tel2");

        String attname =
            (String) ViewHelper.decodeByTpscr(tpscr, "Numvetrino", "CodCampione", null, null, "CodCampione");
        BigDecimal numvetrino = null;
        if (attname != null)
            numvetrino = (BigDecimal) r.getAttribute(attname);

        attname =
            (String) ViewHelper.decodeByTpscr(tpscr, "CodRichiesta", "CodRichiesta", "CodRichiesta", null,
                                              "CodRichiesta");
        String codrichiesta = null;
        if (attname != null)
            codrichiesta = r.getAttribute(attname) == null ? null : r.getAttribute(attname).toString();
        //              String codrichiesta = r.getAttribute("CodRichiesta") == null ? null : r.getAttribute("CodRichiesta").toString();

        String indirizzo, comune, cap, provincia;

        //c'e' un indirizzo di screening
        if ((String) r.getAttribute("IndirizzoScr") != null && ((String) r.getAttribute("IndirizzoScr")).length() > 0) {
            indirizzo = (String) r.getAttribute("IndirizzoScr");
            comune = (String) r.getAttribute("ComuneDesc");
            cap = (String) r.getAttribute("Cap");
            provincia = (String) r.getAttribute("Codpr");
        }

        //c'e' un indirizzo di domicilio
        //24112011 gaion: parametro per il calcolo dell'indirizzo
        else if (mod_spedizione.equalsIgnoreCase("standard") && (String) r.getAttribute("IndirizzoDom") != null &&
                 ((String) r.getAttribute("IndirizzoDom")).length() > 0) {
            indirizzo = (String) r.getAttribute("IndirizzoDom");
            comune = (String) r.getAttribute("Descrizione1");
            cap = (String) r.getAttribute("Cap1");
            provincia = (String) r.getAttribute("Codpr1");
        }

        //c'e' un indirizzo di residenza
        else if ((String) r.getAttribute("IndirizzoRes") != null &&
                 ((String) r.getAttribute("IndirizzoRes")).length() > 0) {
            indirizzo = (String) r.getAttribute("IndirizzoRes");
            comune = (String) r.getAttribute("Descrizione2");
            cap = (String) r.getAttribute("Cap2");
            provincia = (String) r.getAttribute("Codpr2");
        } else {
            indirizzo = null;
            comune = null;
            cap = null;
            provincia = null;
        }

        LetteraEtichetta record =
            new LetteraEtichetta((String) r.getAttribute("Cognome"), (String) r.getAttribute("Nome"), indirizzo, cap,
                                 comune, provincia, DateUtils.getToday(), tesseraSanitaria,
                                 (String) r.getAttribute("Cf"),
                                 DateUtils.dateToString(((oracle.jbo.domain.Date) r.getAttribute("DataNascita")).dateValue()), tel,
                                 (String) r.getAttribute("CentroPrel"),
                                 numvetrino == null ? null : numvetrino.toString(), codrichiesta,
                                 r.getAttribute("Dtapp") == null ? null :
                                 DateUtils.dateToString(((oracle.jbo.domain.Date) r.getAttribute("Dtapp")).dateValue()),
                                 (String) r.getAttribute("CodidsoggMx"), (String) r.getAttribute("NomeMedico"),
                                 (String) r.getAttribute("CognomeMedico"), (String) r.getAttribute("IndirizzoRes1"),
                                 (String) r.getAttribute("Cap3"), (String) r.getAttribute("Descrizione"),
                                 (String) r.getAttribute("Codpr3"), (String) r.getAttribute("Mx45Braccio"),
                                 (String) r.getAttribute("Mx45Codice"), "", "", "", (String) r.getAttribute("Email"), //05112014 Gaion
                                 (String) r.getAttribute("Cellulare"), null, 
                                 (String) r.getAttribute("Descrizione3"),(String) r.getAttribute("Descrizione4"),null);

        return record;
    }

    protected Vector getLettere() throws Exception {
        Print_AppModule am =
            (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        String voName = (String) ADFContext.getCurrent().getRequestScope().get("vo");
        ViewObject vo = am.findViewObject(voName);
        PrintBean bean =
            (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();


        File f = null;
        Vector printList = new Vector();

        if (vo == null)
            throw new Exception("ViewObject " + voName + " not found");
        //@codecoach:disable nextline
        //  HttpServletResponse resp=ctx.getHttpServletResponse();

        //se ho selezionato solo un invito da stampare
        if ("selection".equals(ADFContext.getCurrent().getRequestScope().get("quanti"))) {

            f = File.createTempFile("SingolaLettera", ".pdf");
            Row r = vo.getCurrentRow();
            if (r == null)
                throw new Exception("Nessuna lettera selezionata per la stampa");

            // mauro 26/11/2010, modifiche x gestione postel
            String tipoInvito = (String) r.getAttribute("Idtpinvito");
            Integer idApp = (Integer) r.getAttribute("Idapp");
            String testProposto = (String) r.getAttribute("TestProposto");
            f = Documento.getDocumento(r, f, tipoInvito, idApp, testProposto);
            //

            printList.addElement(f);

            //aggiorno i dati solo se si tratta della prima stampa
            if (r.getAttribute("Dtstampa") == null) {
                r.setAttribute("Dtstampa", DateUtils.getOracleDate(new Date()));
                if ("locale".equals(bean.getTpStampa()))
                    r.setAttribute("Stampapostel", ConfigurationConstants.DB_FALSE);
                else
                    r.setAttribute("Stampapostel", ConfigurationConstants.DB_TRUE);
            }

            /* mauro 14/12/2010 codice da utilizzare se sia vuole fare aggiornamneto da sql */
            /*
            int stampaPostel = 0;
            if(!"locale".equals(bean.getTpStampa()))
              stampaPostel=1;
            Integer idInvito = (Integer) r.getAttribute("Idinvito");
            Documento.aggiornaInfoStampa(am,idInvito,stampaPostel);
            */


        } else { //..se invece stampo tutti gli inviti di quel tipo

            RowSetIterator iter = ViewHelper.getRowSetIterator(vo, "stampa");

            // mauro 14/12/2010, modifiche x gestione postel
            GeneratoreInviti gen = new GeneratoreInviti(am);
            //

            try {

                //aggiungo tutte le lettere
                while (iter.hasNext()) {

                    Row r = iter.next();
                    f = File.createTempFile("SingolaLettera", ".pdf");

                    // mauro 14/12/2010, modifiche x gestione postel
                    String tipoInvito = (String) r.getAttribute("Idtpinvito");
                    Integer idApp = (Integer) r.getAttribute("Idapp");
                    String testProposto = (String) r.getAttribute("TestProposto");
                    f = Documento.getDocumento(r, f, tipoInvito, idApp, gen, am, testProposto);
                    //

                    /*BlobDomain blob=(BlobDomain)r.getAttribute("Lettera");
             f=BlobUtils.getFileFromBlob(blob,"SingolaLettera"); */
                    if (f != null) {
                        String name = f.getAbsolutePath();
                        if (name.indexOf(".") >= 0)
                            name = name.substring(0, name.lastIndexOf("."));
                        File f2 = new File(name + ".pdf");
                        f.renameTo(f2);
                        printList.addElement(f2);

                    }


                    if (r.getAttribute("Dtstampa") == null) {
                        r.setAttribute("Dtstampa", DateUtils.getOracleDate(new Date()));
                        if ("locale".equals(bean.getTpStampa()))
                            r.setAttribute("Stampapostel", ConfigurationConstants.DB_FALSE);
                        else
                            r.setAttribute("Stampapostel", ConfigurationConstants.DB_TRUE);
                    }

                }


            } finally {
                gen.releaseLogo();
                if (iter != null)
                    iter.closeRowSetIterator();
            }
        } //end if

        return printList;
    }

    protected Vector getPostelData(Vector toPrint, int workProcessID, int workProcessIDRaccomandata, String EUserZCode,
                                   String nazioneDefault) throws Exception {
        Print_AppModule am =
            (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        String voName = (String) ADFContext.getCurrent().getRequestScope().get("vo");

        //il nome dell'attributo da controllare per vedere se e' una raccomandata
        //cambia se considero inviti o referti
        String raccName = voName.indexOf("Inviti") >= 0 ? "RaccLettInvito" : "RaccLettReferto";
        ViewObject vo = am.findViewObject(voName);
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        //24112011 gaion: nuovo parametro per il calcolo dell'indirizzo
        String mod_spedizione = (String) ADFContext.getCurrent().getSessionScope().get("mod_spedizione");
        //fine 24112011

        File f = null;
        Vector envelopes = new Vector();

        if (vo == null)
            throw new Exception("ViewObject " + voName + " not found");

        //se ho selezionato solo un invito/referto da stampare
        if ("selection".equals(ADFContext.getCurrent().getRequestScope().get("quanti"))) {
            Row r = vo.getCurrentRow();
            f = (File) toPrint.elementAt(0);
            Integer n = (Integer) r.getAttribute(raccName);
            if (n == null || n.intValue() == 0)
                //spedizione normale
                envelopes.addElement(this.getEnvelope(r, tpscr, f, workProcessID, EUserZCode, nazioneDefault,
                                                      mod_spedizione, am));
            else
                //spedizione tramite raccomandata
                envelopes.addElement(this.getEnvelope(r, tpscr, f, workProcessIDRaccomandata, EUserZCode,
                                                      nazioneDefault, mod_spedizione, am));
        } else {
            RowSetIterator iter = ViewHelper.getRowSetIterator(vo, "stampaPostel");
            Boolean racc = null;

            try {
                //aggiungo tutte le lettere
                int i = 0;

                while (iter.hasNext()) {
                    Row r = iter.next();
                    f = (File) toPrint.elementAt(i);
                    Integer n = (Integer) r.getAttribute(raccName);
                    //inizializzo il valore del flag raccomandata la prima volta
                    if (racc == null) {
                        racc = Boolean.valueOf(n != null && n.intValue() == 1);
                    } else {
                        if (!racc.equals(Boolean.valueOf(n != null && n.intValue() == 1)))
                            throw new Exception("Impossibile inviare a Postel un file che contenga " +
                                                "sia lettere normali che raccomandate (la lettera per " +
                                                r.getAttribute("Cognome") + " " + r.getAttribute("Nome") + " " +
                                                new String(racc.booleanValue() ? "NON" : "") +
                                                " e' una raccomandata e le precedenti " +
                                                new String(racc.booleanValue() ? "SI" : "NO") + ")");
                    }

                    if (!racc.booleanValue()) {

                        //spedizione normale
                        envelopes.addElement(this.getEnvelope(r, tpscr, f, workProcessID, EUserZCode, nazioneDefault,
                                                              mod_spedizione, am));
                    } else {
                        //spedizione tramite raccomandata
                        envelopes.addElement(this.getEnvelope(r, tpscr, f, workProcessIDRaccomandata, EUserZCode,
                                                              nazioneDefault, mod_spedizione, am));
                    }
                    i++;
                }
            } finally {
                if (iter != null)
                    iter.closeRowSetIterator();
            }
        }
        return envelopes;
    }

    public void setBtnStampaHidden(RichButton btnStampaHidden) {
        this.btnStampaHidden = btnStampaHidden;
    }

    public RichButton getBtnStampaHidden() {
        return btnStampaHidden;
    }

    public void downloadListener(ActionEvent actionEvent) {
        Map<String, Object> resp = print();

        if (resp.isEmpty() || !resp.containsKey("outputStream"))
            handleException("Errore nel recupero del file!");
        else {
            FileDownloadActionListener fileDownloadActionListener = new FileDownloadActionListener();
            FacesContext fctx = FacesContext.getCurrentInstance();
            ELContext elctx = fctx.getELContext();
            Application application = fctx.getApplication();
            ExpressionFactory exprFactory = application.getExpressionFactory();
            MethodExpression methodExpr = null;
            methodExpr =
                exprFactory.createMethodExpression(elctx, "#{Print_DataAction.print}", null,
                                                   new Class[] { FacesContext.class, OutputStream.class });
            fileDownloadActionListener.setMethod(methodExpr);
            fileDownloadActionListener.setFilename((String)resp.get("fileName"));
            fileDownloadActionListener.setContentType((String)resp.get("contentType"));

            SetPropertyListener setPropertyListener = new SetPropertyListener(BasePolytypeListener.EventType.ACTION);
            ValueExpression valueExpression =
                exprFactory.createValueExpression(elctx, "#{requestScope.outputStream}", OutputStream.class);
            setPropertyListener.setValueExpression("to", valueExpression);

            setPropertyListener.setFrom(resp.get("outputStream"));
            
            for(ActionListener list: btnStampaHidden.getActionListeners()){
                btnStampaHidden.removeActionListener(list);
            }
            btnStampaHidden.addActionListener(setPropertyListener);
            btnStampaHidden.addActionListener(fileDownloadActionListener);

            String id = (String)ADFContext.getCurrent().getRequestScope().get("btnHiddenId");
            Utility.addScriptOnPartialRequest("customHandler('" + id + "')");
        }
    }
}
