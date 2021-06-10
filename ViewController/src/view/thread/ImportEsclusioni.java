package view.thread;


import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.DateUtils;
import insiel.dataHandling.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;

import model.common.ImpExp_AppModule;

import model.commons.ConfigurationReader;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import model.print.ReportHandler;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;

import view.impexp.ImpexpUtils;

public class ImportEsclusioni extends Thread {
    String ulss;
    String tpscr;
    String tipoFlusso;
    String eroganti;
    String user;
    ImpExp_AppModule am;
    String tpdip;
    String suffisso;
    int annoFlusso;
    int faseFlusso;
    String modalita;
    String dwnDir;
    String fName;
    int pid;

    public ImportEsclusioni(String a_ulss, String a_tpscr, String a_tipoFlusso, int a_annoFlusso, int a_faseFlusso,
                            String a_eroganti, String a_user, String a_suffisso, String a_modalita, String a_dwnDir,
                            String a_fName) {
        ulss = a_ulss;
        tpscr = a_tpscr;
        tipoFlusso = a_tipoFlusso;
        annoFlusso = a_annoFlusso;
        faseFlusso = a_faseFlusso;
        eroganti = a_eroganti;
        user = a_user;
        suffisso = a_suffisso;
        modalita = a_modalita;
        tpdip = tipoFlusso.substring(0, 2);
        dwnDir = a_dwnDir;
        fName = a_fName;
        am =
            (ImpExp_AppModule) Configuration.createRootApplicationModule("model.ImpExp_AppModule",
                                                                         "ImpExp_AppModuleLocal");
    }

    public void run() {

        // Imposto il lock
        try {
            ImpexpUtils.getRowAndLock(am, ulss, tpscr, tpdip);
        }

        // Puo' andare in eccezione anche quando il record e' gia' bloccato
        catch (Exception e) {
            am.log(ulss, tpdip, "IMP", e.getMessage(), tpscr);
            Configuration.releaseRootApplicationModule(am, true);
            return;
        }

        try {
            String mess;

            if (modalita.equals("E"))
                mess = "IMPORT FLUSSO " + tipoFlusso + " per l'azienda sanitaria " + ulss + " iniziato";
            else
                mess = "VERIFICA IMPORT FLUSSO " + tipoFlusso + " per l'azienda sanitaria " + ulss + " iniziata";

            this.startElaborazione();

            am.log(ulss, tpdip, "IMP", mess, tpscr);

            String msg =
                am.callImportEsclusioni(ulss, tpscr, tipoFlusso, eroganti, user, modalita, annoFlusso, faseFlusso);
            if (msg != null)
                throw new Exception("Impossibile eseguire l'import (o verifica import) delle esclusioni: " + msg);

            if (modalita.equals("E"))
                mess =
                    "IMPORT FLUSSO " + tipoFlusso + " per l'azienda sanitaria " + ulss + " completato. " +
                    "Si procede ora all'aggiornamento delle date di import...";
            else
                mess = "VERIFICA IMPORT FLUSSO " + tipoFlusso + " per l'azienda sanitaria " + ulss + " completata.";

            am.log(ulss, tpdip, "IMP", mess, tpscr);

            // Aggiornamento data import per tutti gli screening (non serve in modalita' controllo)
            // + rinomina file aggiungendo timestamp
            if (modalita.equals("E")) {
                this.updateConfig();
                mess =
                    "Date di import aggiornate con successo. " + "Si procede ora alla creazione dei report di log...";
            } else {
                mess = "Si procede ora alla creazione dei report di log...";
            }

            am.log(ulss, tpdip, "IMP", mess, tpscr);

            // Report di log
            this.createLog();

            if (modalita.equals("E")) {
                mess = "IMPORT FLUSSO " + tipoFlusso + " COMPLETATO CON SUCCESSO. Operazione chiusa.";
            } else {
                mess = "VERIFICA IMPORT FLUSSO " + tipoFlusso + " COMPLETATA CON SUCCESSO. Operazione chiusa.";
            }

            am.log(ulss, tpdip, "IMP", mess, tpscr);
        } catch (Exception ex) {
            ex.printStackTrace();
            am.getTransaction().rollback();
            am.log(ulss, tpdip, "IMP", "Errore: " + ex.getMessage(), tpscr);
            String mess;
            if (modalita.equals("E")) {
                mess = "IMPORT FLUSSO " + tipoFlusso + "INTERROTTO PER ERRORI. Operazione chiusa.";
            } else {
                mess = "VERIFICA IMPORT FLUSSO " + tipoFlusso + "INTERROTTA PER ERRORI. Operazione chiusa.";
            }

            am.log(ulss, tpdip, "IMP", mess, tpscr);
        } finally {
            try {
                ImpexpUtils.unlock(am, tpdip, "IMP", ulss, tpscr);
            } catch (Exception ex) {
            }

            Configuration.releaseRootApplicationModule(am, true);
        }
    }

    private void updateConfig() throws Exception {
        String update =
            "UPDATE SO_CNF_IMPEXP SET DTIMPORT=sysdate " + "WHERE TPDIP='" + tpdip + "' AND " + "IMPEXP='IMP' AND " +
            "ULSS='" + ulss + "' ";

        am.getTransaction().executeCommand(update);

        String updateFlusso =
            "update SO_SDOSPS_FLUSSO set STATO = 'E', DATA_ELABORAZIONE = sysdate, " + "USER_MODIFICA = '" + user +
            "', DATA_MODIFICA = sysdate " + "where ULSS = '" + ulss + "' and TIPO_FLUSSO = '" + tipoFlusso +
            "' and ANNO = " + annoFlusso + " and FASE = " + faseFlusso;

        am.getTransaction().executeCommand(updateFlusso);

        am.getTransaction().commit();

        // rinomina file
        File dwnFile = new File(dwnDir, fName + ".dat");

        SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyyMMddkkmm");
        String tms = DATE_TIME_FORMATTER.format(new Date());

        String fNameNew = fName + "_" + tms + ".dat";

        File dest = new File(dwnDir, fNameNew);
        dwnFile.renameTo(dest);
    }

    private void createLog() throws Exception {
        File pdf = null;
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView2");

        // Filtro i record di configurazione sull'import dell'anagrafe
        vo.setWhereClause("ULSS='" + ulss + "' AND IMPEXP='IMP' AND TPDIP='" + tpdip + "'");
        vo.executeQuery();

        HashMap map = new HashMap();
        map.put("ulss", ulss);
        // map.put("tpscr", r.getTpscr());
        map.put("gruppo", tpdip);
        map.put("impexp", "IMP");
        map.put("data_log", DateUtils.getNow());
        map.put("luogo", suffisso);
        map.put("titolo", "IMPORT FLUSSO " + tipoFlusso);
        map.put("eroganti", eroganti);
        map.put("anno", String.valueOf(annoFlusso));
        map.put("fase", String.valueOf(faseFlusso));

        //creo un report unico per tutti gli screening
        ReportHandler rh = new ReportHandler();
        Row[] result = null;
        Impexp_SoCnfImpexpViewRow r1 = null;

        try {
            result = vo.getFilteredRows("Tpscr", tpscr);
            r1 = (Impexp_SoCnfImpexpViewRow) result[0];
        } catch (Exception ee) {

        }
        pdf =
            rh.createCsvReport(r1, "Impexp_SoCnfImpexpView2", "Template", "TemplateCompilato", am.getDBConnection(),
                               map);
        rh.cleanup();

        RowSetIterator iter = vo.createRowSetIterator("iter");

        //salvo il log su tutti gli screening
        while (iter.hasNext()) {
            r1 = (Impexp_SoCnfImpexpViewRow) iter.next();
            if (r1 == null)
                continue;

            System.out.println("Screening " + r1.getTpscr());

            r1.setLogFile(BlobUtils.getBlobFromFile(pdf));

        }

        am.getTransaction().commit();

        String mess;

        if (modalita.equals("E")) {
            mess = "Report di log per l'import SDO/SPS creato con successo.";
        } else {
            mess = "Report di log per verifica import SDO/SPS creato con successo.";
        }

        am.log(ulss, tpdip, "IMP", mess, tpscr);

        this.logElaborazione(pdf);

        if (pdf != null)
            pdf.delete();

    }

    private void logElaborazione(File report) throws Exception {
        //legge la dir dove vanno memorizzati i file di log
        String dir = ConfigurationReader.readProperty("scheduledLogPath");

        File directory = new File(dir);
        if (!directory.exists())
            throw new Exception("Directory " + dir + " per la memorizzazione dei log non trovata");

        //copia il report di log nella dir
        String filename = directory.getAbsolutePath() + "/" + pid + "_logImportEsclusioni.csv";
        StreamUtils.copyInputToOutput(new FileInputStream(report), new FileOutputStream(filename));

        SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date d = new Date();
        String endTime = DATE_TIME_FORMATTER.format(d);

        //aggiorna il record del processo con il nome del file
        String update =
            "update SO_ELABORAZIONE_ESCLUSIONI " + "set link_log='" + pid + "_logImportEsclusioni.csv', " +
            "FINE_ESECUZIONE = to_date('" + endTime + "','dd/mm/yyyy hh24:mi:ss')" + "where ID_ELAB = " + pid;

        am.getTransaction().executeCommand(update);

        am.getTransaction().commit();
    }

    private void startElaborazione() //sistemare
    {
        SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date d = new Date();
        String startTime = DATE_TIME_FORMATTER.format(d);

        pid = am.getNextPID().intValue();

        String insert =
            "insert into SO_ELABORAZIONE_ESCLUSIONI " +
            "(ID_ELAB, ULSS, TPSCR, TIPO_FLUSSO, ANNO, FASE, INIZIO_ESECUZIONE, UTENTE_ESECUZIONE, MODALITA)" +
            "values (" + pid + ", " + "'" + ulss + "', " + "'" + tpscr + "', " + "'" + tipoFlusso + "', " + annoFlusso +
            ", " + faseFlusso + ", " + "to_date('" + startTime + "','dd/mm/yyyy hh24:mi:ss'), " + "'" + user + "', " +
            "'" + modalita + "')";

        ApplicationModule am1 = (ApplicationModule) am;

        am1.getTransaction().executeCommand(insert);

        am1.getTransaction().commit();

    }
}
