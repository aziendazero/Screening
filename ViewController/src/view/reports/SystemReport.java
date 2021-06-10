package view.reports;

import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.DateUtils;

import insiel.dataHandling.ObjectTransformationUtils;

import java.io.File;

import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import model.commons.ConfigurationConstants;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import oracle.jbo.domain.BlobDomain;

@SuppressWarnings({ "unchecked" })
public class SystemReport {
    public static final int REFERTO_RIASSUNTIVO_2L = 0;
    public static final int STORICO_CITO = 1;
    private static final int STORICO_DETAIL_SOGGETTO = 2;
    private static final int STORICO_DETAIL_INVITI = 3;
    private static final int STORICO_DETAIL_REFERTI1LIV_CITO = 4;
    private static final int STORICO_DETAIL_REFERTI2LIV_CITO = 5;
    private static final int STORICO_DETAIL_ESCLUSIONI = 6;
    private static final int STORICO_DETAIL_INTERVENTI = 7;
    private static final int STATISTICHE_SUB_1 = 8;
    private static final int STATISTICHE_SUB_2 = 9;
    private static final int STATISTICHE_SUB_3 = 10;
    private static final int STATISTICHE_SUB_4 = 11;
    public static final int STATISTICHE_MASTER = 12;
    public static final int SCHEDA_ANAM_COLON = 13;
    public static final int SCHEDA_ANAM_COLON_VUOTA = 14;
    public static final int REFERTO_RIASSUNTIVO_2L_CO = 15;
    private static final int REFERTO_RIASSUNTIVO_2L_CO_ENDO = 16;
    public static final int STORICO_COLON = 17;
    private static final int STORICO_DETAIL_INVITI_COLON = 18;
    private static final int STORICO_DETAIL_REFERTI1LIV_COLON = 19;
    private static final int STORICO_DETAIL_REFERTI2LIV_COLON = 20;
    private static final int STORICO_DETAIL_INTERVENTI_COLON = 21;
    private static final int STORICO_DETAIL_ENDOSCOPIE = 22;
    public static final int REFERTO_RIASSUNTIVO_2L_MA = 23;
    public static final int STORICO_MAMMO = 24;
    private static final int STORICO_DETAIL_INVITI_MAMMO = 25;
    private static final int STORICO_DETAIL_REFERTI1LIV_MAMMO = 26;
    private static final int STORICO_DETAIL_REFERTI2LIV_MAMMO = 27;
    private static final int STORICO_DETAIL_INTERVENTI_MAMMO = 28;
    public static final int SCHEDA_ANAM_MX = 29;
    public static final int SCHEDA_ANAMMX_SINTOMI = 30;
    public static final int SCHEDA_ANAM_MX_VUOTA = 31;
    public static final int SCHEDA_ANAMMX_VUSINT = 32;
    public static final int SCHEDA_ANAM_CITO = 33;
    public static final int SCHEDA_ANAMCI_INTERVENTI = 34;
    public static final int SCHEDA_ANAM_CITO_VUOTA = 35;
    public static final int SCHEDA_ANAMCI_VUOTA_INTERVENTI = 36;
    public static final int STORICO_CARDIO = 37;
    private static final int STORICO_DETAIL_INVITI_CARDIO = 38;
    private static final int STORICO_DETAIL_REFERTI1LIV_CARDIO = 39;
    private static final int STORICO_DETAIL_DOCUMENTI = 40;
    public static final int STORICO_PFAS = 41;
    private static final int STORICO_DETAIL_INVITI_PFAS = 42;
    private static final int STORICO_DETAIL_REFERTI1LIV_PFAS = 43;

    private JasperReport report;
    private HashMap subreport_params;

    /**
     * Il primo elemento di ogni coppia e' il nome del template da caricare,
     * il secondo e' l'array degli indici dei sottotemplate con il
     * relativo nome di parametro.
     */
    private static final Object[] REPORTS_DEF = {
        new Object[] { "CReferto_Conclusivo2LIV.jrxml", null },
        // 0
                                                  new Object[] { "StoricoCito.jrxml",
                                                                 new Object[] { new Object[] { new Integer(STORICO_DETAIL_SOGGETTO),
                                                                                               "SOGGETTO_DETAIL" },
                                                                                new Object[] { new Integer(STORICO_DETAIL_DOCUMENTI),
                                                                                               "DOCUMENTI_DETAIL" },
                                                                                new Object[] { new Integer(STORICO_DETAIL_INVITI),
                                                                                               "INVITI_DETAIL" },
                                                                                new Object[] { new Integer(STORICO_DETAIL_ESCLUSIONI),
                                                                                               "ESCLUSIONI_DETAIL" },
                                                                                new Object[] { new Integer(STORICO_DETAIL_REFERTI1LIV_CITO),
                                                                                               "REFERTO1LIV_DETAIL" },
                                                                                new Object[] { new Integer(STORICO_DETAIL_REFERTI2LIV_CITO),
                                                                                               "REFERTO2LIV_DETAIL" },
                                                                                new Object[] { new Integer(STORICO_DETAIL_INTERVENTI),
                                                                                               "INTERVENTO_DETAIL" } } },
        // 1
        new Object[] { "StoricoSoggettoDetail.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_DOCUMENTI), "DOCUMENTI_DETAIL" } } },
        // 2
        new Object[] { "StoricoInvitoDetail.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_REFERTI1LIV_CITO),
                                                     "REFERTO1LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI2LIV_CITO),
                                                     "REFERTO2LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INTERVENTI), "INTERVENTO_DETAIL" } } },
        // 3
        new Object[] { "StoricoReferto1livDetail.jrxml", null }, // 4
        new Object[] { "StoricoReferto2livDetail.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_INTERVENTI), "INTERVENTO_DETAIL" } } },
        // 5
        new Object[] { "StoricoEsclusioniDetail.jrxml", null }, // 6
        new Object[] { "StoricoInterventoDetail.jrxml", null }, // 7
        new Object[] { "Matrix00_07cols.jrxml", null }, // 8
        new Object[] { "Matrix08_15cols.jrxml", null }, // 9
        new Object[] { "Matrix16_23cols.jrxml", null }, // 10
        new Object[] { "Matrix24_29cols.jrxml", null }, // 11
        new Object[] { "Stats_report.jrxml",
                       new Object[] { new Object[] { new Integer(STATISTICHE_SUB_1), "stats_sub_1" },
                                      new Object[] { new Integer(STATISTICHE_SUB_2), "stats_sub_2" },
                                      new Object[] { new Integer(STATISTICHE_SUB_3), "stats_sub_3" },
                                      new Object[] { new Integer(STATISTICHE_SUB_4), "stats_sub_4" } } }, // 12
        new Object[] { "SchedaAnamnesiColon.jrxml", null }, // 13
        new Object[] { "SchedaAnamnesiColonVuota.jrxml", null }, // 14
        new Object[] { "OReferto_Conclusivo2LIV.jrxml",
                       new Object[] { new Object[] { new Integer(REFERTO_RIASSUNTIVO_2L_CO_ENDO), "sub_endo" } } },
        // 15
        new Object[] { "OReferto_Conclusivo2LIV_subendo.jrxml", null }, // 16
        new Object[] { "StoricoColon.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_SOGGETTO), "SOGGETTO_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_DOCUMENTI), "DOCUMENTI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INVITI_COLON), "INVITI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_ESCLUSIONI), "ESCLUSIONI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI1LIV_COLON),
                                                     "REFERTO1LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI2LIV_COLON),
                                                     "REFERTO2LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INTERVENTI_COLON),
                                                     "INTERVENTO_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_ENDOSCOPIE), "ENDO_DETAIL" } } }, // 17
        new Object[] { "StoricoInvitoDetailCO.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_REFERTI1LIV_COLON),
                                                     "REFERTO1LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI2LIV_COLON),
                                                     "REFERTO2LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INTERVENTI_COLON),
                                                     "INTERVENTO_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_ENDOSCOPIE), "ENDO_DETAIL" } } }, // 18
        new Object[] { "StoricoReferto1livDetailCO.jrxml", null }, // 19
        new Object[] { "StoricoReferto2livDetailCO.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_INTERVENTI_COLON),
                                                     "INTERVENTO_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_ENDOSCOPIE), "ENDO_DETAIL" } } }, // 20
        new Object[] { "StoricoInterventoDetailCO.jrxml", null }, // 21
        new Object[] { "StoricoEndoDetail.jrxml", null }, // 22
        new Object[] { "MReferto_Conclusivo2LIV.jrxml", null },
        // 23

        new Object[] { "StoricoMammo.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_SOGGETTO), "SOGGETTO_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_DOCUMENTI), "DOCUMENTI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INVITI_MAMMO), "INVITI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_ESCLUSIONI), "ESCLUSIONI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI1LIV_MAMMO),
                                                     "REFERTO1LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI2LIV_MAMMO),
                                                     "REFERTO2LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INTERVENTI_MAMMO),
                                                     "INTERVENTO_DETAIL" } } }, // 24
        new Object[] { "StoricoInvitoDetailMA.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_REFERTI1LIV_MAMMO),
                                                     "REFERTO1LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI2LIV_MAMMO),
                                                     "REFERTO2LIV_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INTERVENTI_MAMMO),
                                                     "INTERVENTO_DETAIL" } } }, // 25
        new Object[] { "StoricoReferto1livDetailMA.jrxml", null }, // 26
        new Object[] { "StoricoReferto2livDetailMA.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_INTERVENTI_MAMMO),
                                                     "INTERVENTO_DETAIL" } } }, // 27
        new Object[] { "StoricoInterventoDetailMA.jrxml", null }, // 28
        new Object[] { "SchedaAnamnesiMx.jrxml",
                       new Object[] { new Object[] { new Integer(SCHEDA_ANAMMX_SINTOMI), "sintomi" } } }, // 29
        new Object[] { "SchedaAnamMxSint.jrxml", null }, // 30
        new Object[] { "SchedaAnamnesiMxVuota.jrxml",
                       new Object[] { new Object[] { new Integer(SCHEDA_ANAMMX_VUSINT), "sintomi" } } }, // 31
        new Object[] { "SchedaAnamMxVuSint.jrxml", null }, // 32
        new Object[] { "SchedaAnamnesiCito.jrxml",
                       new Object[] { new Object[] { new Integer(SCHEDA_ANAMCI_INTERVENTI), "interventi" } } }, // 33
        new Object[] { "SchedaAnamCitoSint.jrxml", null }, // 34
        new Object[] { "SchedaAnamCitoVuota.jrxml",
                       new Object[] { new Object[] { new Integer(SCHEDA_ANAMCI_VUOTA_INTERVENTI), "interventi" } } },
        // 35
        new Object[] { "SchedaAnamCitoVuSint.jrxml", null }, // 36
        new Object[] { "StoricoCardio.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_SOGGETTO), "SOGGETTO_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_DOCUMENTI), "DOCUMENTI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INVITI_CARDIO), "INVITI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_ESCLUSIONI), "ESCLUSIONI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI1LIV_CARDIO),
                                                     "REFERTO1LIV_DETAIL" }, } }, // 37
        new Object[] { "StoricoInvitoDetailCA.jrxml", null }, // 38
        new Object[] { "StoricoReferto1livDetailCA.jrxml", null }, // 39
        new Object[] { "StoricoDocumentoDetail.jrxml", null }, // 40
        new Object[] { "StoricoPfas.jrxml",
                       new Object[] { new Object[] { new Integer(STORICO_DETAIL_SOGGETTO), "SOGGETTO_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_DOCUMENTI), "DOCUMENTI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_INVITI_PFAS), "INVITI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_ESCLUSIONI), "ESCLUSIONI_DETAIL" },
                                      new Object[] { new Integer(STORICO_DETAIL_REFERTI1LIV_PFAS),
                                                     "REFERTO1LIV_DETAIL" } } }, //41
        new Object[] { "StoricoInvitoDetailPF.jrxml", null }, // 42
        new Object[] { "StoricoReferto1livDetailPF.jrxml", null } // 43
    };

    private static HashMap compiled;

    public SystemReport(int resourceIndex) throws JRException {
        System.out.println("Storia scr, inizio compilazione:" +
                           DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
        Object[] obj = compileReport(resourceIndex, true);
        System.out.println("Storia scr, fine compilazione:" +
                           DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
        this.report = (JasperReport) obj[0];
        this.subreport_params = (HashMap) obj[1];
    }

    public SystemReport(File f) throws JRException {
        JasperDesign design1 = JRXmlLoader.load(f);
        this.report = JasperCompileManager.compileReport(design1);
        this.subreport_params = new HashMap();
    }

    public SystemReport(BlobDomain blob) throws JRException, IOException {
        File f = BlobUtils.getFileFromBlob(blob, "Report");
        JasperDesign design1 = JRXmlLoader.load(f);
        this.report = JasperCompileManager.compileReport(design1);
        this.subreport_params = new HashMap();
    }


    public File getPDFReport(Connection conn, HashMap hash) throws Exception {
        Map m = ObjectTransformationUtils.merge(this.subreport_params, hash);
        System.out.println("Storia scr, inizio riempimento:" +
                           DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
        JasperPrint print = JasperFillManager.fillReport(this.report, m, conn);
        System.out.println("Storia scr, fine riempimento:" + DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
        File pdf = File.createTempFile("system_report", ".pdf");
        //JasperExportManager.exportReportToPdfFile(print,pdf.getAbsolutePath());

        JRPdfExporter exp = new JRPdfExporter();
        exp.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exp.setParameter(JRExporterParameter.OUTPUT_FILE, pdf);
        exp.setParameter(JRPdfExporterParameter.PDF_VERSION, ConfigurationConstants.PDF_VERSION);
        exp.exportReport();
        System.out.println(DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
        return pdf;
    }
    
    public void getPDFReport(Connection conn, HashMap hash, OutputStream out) throws Exception {
        Map m = ObjectTransformationUtils.merge(this.subreport_params, hash);
        System.out.println("Storia scr, inizio riempimento:" +
                           DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
        JasperPrint print = JasperFillManager.fillReport(this.report, m, conn);
        System.out.println("Storia scr, fine riempimento:" + DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));

        JRPdfExporter exp = new JRPdfExporter();
        exp.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exp.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
        exp.setParameter(JRPdfExporterParameter.PDF_VERSION, ConfigurationConstants.PDF_VERSION);
        exp.exportReport();
        System.out.println(DateUtils.dateToString("dd/MM/yyyy HH:mm:ss", new Date()));
    }

    public File getPDFReport(JRDataSource ds, HashMap hash) throws Exception {
        Map m = ObjectTransformationUtils.merge(this.subreport_params, hash);
        JasperPrint print = JasperFillManager.fillReport(this.report, m, ds);
        File pdf = File.createTempFile("system_report", ".pdf");
        // JasperExportManager.exportReportToPdfFile(print,pdf.getAbsolutePath());
        JRPdfExporter exp = new JRPdfExporter();
        exp.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exp.setParameter(JRExporterParameter.OUTPUT_FILE, pdf);
        exp.setParameter(JRPdfExporterParameter.PDF_VERSION, ConfigurationConstants.PDF_VERSION);
        exp.exportReport();
        return pdf;
    }

    /**
     * Metodo per creare un elenco di stampe di anamnesi in un
     * unico file pdf. (gaion 07/06/2011)
     * @throws java.lang.Exception
     * @return
     * @param ids  lista degli id delle anamnesi
     * @param hash
     * @param conn
     */
    public File getBatchPDFReport(Connection conn, HashMap hash, List ids, String paramname) throws Exception {
        List jasperPrintList = new ArrayList();
        for (int i = 0; i < ids.size(); i++) {
            String id = (String) ids.get(i);
            Double idanam = new Double(id);

            hash.put(paramname, idanam);

            Map m = ObjectTransformationUtils.merge(this.subreport_params, hash);
            JasperPrint print = JasperFillManager.fillReport(this.report, m, conn);
            jasperPrintList.add(print);
        }
        File pdf = File.createTempFile("system_report", ".pdf");
        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, pdf);
        exporter.setParameter(JRPdfExporterParameter.PDF_VERSION, ConfigurationConstants.PDF_VERSION);
        exporter.exportReport();
        return pdf;
    }


    /**
     * Metodo ricorsivo che riceve in input l'indice del referto di sistema da
     * utilizzare e produce in output il compilato e la mappa con i subreport.
     * @throws net.sf.jasperreports.engine.JRException
     * @return [JasperReport][HashMap]
     * @param index
     */
    public static Object[] compileReport(int index, boolean withsubs) throws JRException {
        if (compiled == null)
            compiled = new HashMap();

        //controllo se il compilato e' gia' nella hashMap
        JasperReport report1 = (JasperReport) compiled.get(new Integer(index));

        //se non c'e' lo compilo e lo inserisco nella hashmap
        Object[] resource = (Object[]) REPORTS_DEF[index];

        if (report1 == null) { 
            //il primo elemento e' la stringa col nome del file jrxml
            InputStream inputJrxml = SystemReport.class.getResourceAsStream((String) resource[0]);
            JasperDesign design1 = JRXmlLoader.load(inputJrxml);
            report1 = JasperCompileManager.compileReport(design1);
            compiled.put(new Integer(index), report1);
        }

        //imposto il compilato
        Object[] output = new Object[2];
        output[0] = report1;

        //vedo se il report ha dei sottoreport che devo mettere nella mappa parametri
        if (resource[1] == null || !withsubs) {
            output[1] = new HashMap();
            return output;
        }

        HashMap map = new HashMap();
        Object[] subs = (Object[]) resource[1];
        for (int i = 0; i < subs.length; i++) {
            Object[] ob = (Object[]) subs[i];
            Object[] sub_all = compileReport(((Integer) ob[0]).intValue(), false);
            map.put((String) ob[1], (JasperReport) sub_all[0]);

        }
        output[1] = map;
        return output;
    }

    public static void massiveCompilation() throws JRException {
        for (int i = 0; i < REPORTS_DEF.length; i++) {
            compileReport(i, false);
        }
    }

    /**
     * Metodo che sostituisce tutte le occorrenze di old_value con new_value
     * al'interno della query del'oggetto jasperdesign
     * @param new_value
     * @param old_value
     * @param design
     */
    private JasperDesign changeStringValue(JasperDesign design, String old_value, String new_value) {
        String query = design.getQuery().getText();
        query = query.replaceAll(old_value, new_value);
        JRDesignQuery newQuery = new JRDesignQuery();
        newQuery.setText(query);
        design.setQuery(newQuery);

        return design;
    }


}
