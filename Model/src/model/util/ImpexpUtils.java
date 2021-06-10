package model.util;

import insiel.dataHandling.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import model.common.ImpExp_AppModule;

import model.commons.ConfigurationConstants;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;


public class ImpexpUtils {
    public ImpexpUtils() {
    }

    /**
     * Metodo che cerca il file più recente in una cartella
     * @throws java.io.FileNotFoundException se non trova tale file
     * @return il file
     * @param dateFormat formato del timestamp presente nel nome, utilizzato
     * per ordinare i file e scegliere quelo più recente
     * @param nome parte fissa del nome del file 8cui si aggiunge il timestamp ed,
     * eventualmente, il suffisso
     * @param dir directory in cui cercare il file
     */
    public static File getMostRecentFile(File dir, String nome, String dateFormat) throws FileNotFoundException {

        File[] files = dir.listFiles(new MyFilenameFilter(nome, null));

        //assumiamo che per l'anagrafe ci sia sempre un solo file
        if (files == null || files.length == 0)
            throw new FileNotFoundException("Nessun file trovato in " + dir.getAbsolutePath());
        else
        //20110722 serra: correzione se il file non ha timestamp ce ne sarà sempre solo 1
        if (files.length == 1)
            return files[0];
        // end

        else {
            File f = null;
            Date d = null;
            for (int i = 0; i < files.length; i++) {
                //ho trovato un file che si può usare
                Date d2 = null;
                String s = files[i].getName().substring(nome.length());
                if (s.indexOf("_") >= 0)
                    s = s.substring(0, s.indexOf("_"));
                try {
                    d2 = new SimpleDateFormat(dateFormat).parse(s);
                    if (f == null) {
                        f = files[i];
                        d = d2;
                    } else {
                        //confronto se il file già trovato è più nuovo o più vecchio
                        if (d.before(d2)) {
                            f = files[i];
                            d = d2;
                        }
                    }
                } catch (ParseException pex) {
                    //non succede nulla, semplicemente non imposto file e data
                    //con quelli in esame
                }


            } //end for

            if (f == null)
                throw new FileNotFoundException("Nessun file in " + dir.getAbsolutePath() + " rispetta il formato " +
                                                nome + dateFormat);

            return f;
        }
    }


    /**
     * Metodo che cerca in dir il filecol nome indicato. Se lo trova, lo restituisce,
     * altrimenti genera un'eccezione
     * @throws java.io.FileNotFoundException
     * @return
     * @param nome
     * @param dir
     */
    public static File getNamedFile(File dir, String nome) throws FileNotFoundException {
        File[] files = dir.listFiles(new MyFilenameFilter(nome, null));


        if (files.length == 0)
            throw new FileNotFoundException("Nessun file trovato in " + dir.getAbsolutePath());
        else {

            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(nome))
                    return files[i];

            }

            throw new FileNotFoundException("Nessun file di nome " + nome + " trovato in " + dir.getAbsolutePath());

        }
    }

    /**
     * Restituisce l'elenco dei file non OLD prsenti nella directory
     * @return
     * @param nome inizio del nome dei file
     * @param dir directory in cui cercare
     */
    public static Vector getFileList(File dir, String nome, String suffisso) {
        File[] files = dir.listFiles(new MyFilenameFilter(nome, null));
        Vector v = new Vector();
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getAbsolutePath() + " " + files[i].getName());
            //non includo i file vecchi e quelli già importati
            if (!files[i].getName().endsWith("_OLD") && !files[i].getName().endsWith("_IMPORTED")) {

                if (suffisso != null && suffisso.length() > 0) {
                    //controllo che il file abbia il suffisso richiesto
                    if (files[i].getName().endsWith(suffisso))
                        v.addElement(files[i]);
                } else //non c'è suffisso
                    v.addElement(files[i]);

            }

        }
        Collections.sort(v, Collections.reverseOrder());
        return v;
    }

    public static Vector getFileList(File dir, String nome) {
        return getFileList(dir, nome, null);
    }

    /**
     * Restituisce l'elenco dei file  OLD prsenti nella directory
     * @return
     * @param nome inizio del nome dei file
     * @param dir directory in cui cercare
     */
    public static Vector getOLDFileList(File dir, String nome, String suffisso) {
        File[] files = dir.listFiles(new MyFilenameFilter(nome, "_OLD"));
        Vector v = new Vector();
        for (int i = 0; i < files.length; i++) {
            //scarto i file non vecchi
            if (!files[i].getName().endsWith("_OLD"))
                continue;

            if (suffisso != null) {
                //controllo che il file abbia il suffisso richiesto
                if (files[i].getName().endsWith(suffisso + "_OLD"))
                    v.addElement(files[i]);
            } else //non c'è suffisso
                v.addElement(files[i]);

        }

        //16112011 gaion: aggiungo anche quelli già importati
        File[] files_imp = dir.listFiles(new MyFilenameFilter(nome, "_IMPORTED"));
        for (int i = 0; i < files_imp.length; i++) {
            //scarto i file non vecchi
            if (!files_imp[i].getName().endsWith("_IMPORTED"))
                continue;

            if (suffisso != null) {
                //controllo che il file abbia il suffisso richiesto
                if (files_imp[i].getName().endsWith(suffisso + "_IMPORTED"))
                    v.addElement(files[i]);
            } else //non c'è suffisso
                v.addElement(files[i]);

        }

        Collections.sort(v, Collections.reverseOrder());
        return v;

    }

    public static Vector getOLDFileList(File dir, String nome) {
        return getOLDFileList(dir, nome, null);
    }

    /**
     * Metodo che legge la riga di configurazione da DB. Se risulta già lockata,
     * dà un'eccezione. Altrimenti prova a alockarla. Se non ci riesce, dà comune
     * un'eccezione
     * @throws java.lang.Exception
     * @return
     * @param ctx
     */
    /* public static synchronized Impexp_SoCnfImpexpViewRow getRowAndLock() throws Exception
    {
      ImpExp_AppModule am=(ImpExp_AppModule)BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
      ViewObject vo=am.findViewObject("Impexp_SoCnfImpexpView1");
      Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();

      Boolean myLock=(Boolean)ADFContext.getCurrent().getSessionScope();.getAttribute("impexp_lock");
      //se ho già iniziato l'perazione non faccio nulla
      if(myLock!=null && myLock.booleanValue())
        return r;
      r=getRowAndLock(am,r);
      ADFContext.getCurrent().getSessionScope();.setAttribute("impexp_lock",Boolean.TRUE);
      return r;
    }

    public static synchronized Impexp_SoCnfImpexpViewRow getRowAndLock(ImpExp_AppModule am,Impexp_SoCnfImpexpViewRow r) throws Exception
    {
          r.refresh(Row.REFRESH_CONTAINEES);
       //controllo che non ci sia qualcun altro che sta effettaundo un import
        if(r.getAttivo()!=null && r.getAttivo().booleanValue())
           throw new Exception("Un altro operatore sta effettuando questa operazione. Si consiglia di riprovare più tardi.");
        else{
          //faccio un lock sull'import
          try{
           r.setAttivo(ConfigurationConstants.DB_TRUE);
           am.getTransaction().commit();

          }catch(JboException jex)
          {
            am.getTransaction().rollback();
            throw new Exception("Un altro operatore sta effettuando questa operazione. Si consiglia di riprovare più tardi.");
          }
        }
      return r;
    }*/

    /**
     * Metodo che legge la riga di configurazione da DB. Se risulta già lockata,
     * dà un'eccezione. Altrimenti controlla i tutti i record con gli stessi valori di
     * ulss, tpscr (non pe rl'anagrafe), tpdip e verso. Se almeno uno è lockato dà
     * errore. Altrimenti prova a lockare l ariga corrente. Se non ci riesce, dà comune
     * un'eccezione
     * @throws java.lang.Exception
     * @return
     * @param ctx
     */
    public static synchronized Impexp_SoCnfImpexpViewRow getRowAndLock(boolean extended,
                                                                       boolean acrossTpscr) throws Exception {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
        Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();

        // mauro 22/02/2010 aggiunta per tenere traccia dell'ultimo dipartimentale su
        // cui è stato tentato un lock
        ADFContext.getCurrent().getSessionScope().put("impexp_lock_tpdip", r.getTpdip());

        Boolean myLock = (Boolean) ADFContext.getCurrent().getSessionScope().get("impexp_lock");
        //se ho già iniziato l'perazione non faccio nulla
        if (myLock != null && myLock.booleanValue())
            return r;
        r = getRowAndLock(am, r, extended, acrossTpscr);
        ADFContext.getCurrent().getSessionScope().put("impexp_lock", Boolean.TRUE);
        return r;
    }

    public static synchronized Impexp_SoCnfImpexpViewRow getRowAndLock(ImpExp_AppModule am, Impexp_SoCnfImpexpViewRow r,
                                                                       boolean extended,
                                                                       boolean acrossTpscr) throws Exception {
        //controllo se c'è almeno uno dei flag relativi attivo e din tal caso dò errore
        String query =
            "SELECT PROGRULSS, " + //1
            "IDCENTROREF,  " + //2
            "IMPEXP, " + //3
            "TPDIP, " + //4
            "ULSS, " + //5
            "TPSCR, " + //6
            "ATTIVO, " + //7
            "MODALITA " + //8
            "FROM SO_CNF_IMPEXP \n" + "WHERE TPDIP='" + r.getTpdip() + "' AND " + "IMPEXP='" + r.getImpexp() +
            "' AND " + "ULSS='" + r.getUlss() + "' ";
        //se non uso la versione estesa prendo solo la riga corrente
        if (!extended)
            query += "AND TPSCR='" + r.getTpscr() + "' and PROGRULSS=" + r.getProgrulss();
        //SE non USO LA VERSIONE ACROSSsCREENING, LAVORO SOLO SUL MIO SCRENING
        if (!acrossTpscr)
            query += "AND TPSCR='" + r.getTpscr() + "' ";
        //     System.out.println(query);
        query += " FOR UPDATE";

        ViewObject vo = am.createViewObjectFromQueryStmt("ImpexpFlagView1", query);
        vo.executeQuery();
        Row r1 = null;
        boolean locked = false;
        while ((r1 = vo.next()) != null) {
            //se il record è lockato
            if (ConfigurationConstants.DB_TRUE.equals(r1.getAttribute(6))) {
                locked = true;
                break;
            }
        }
        //se c'è già un lock segnalo l'errore
        if (locked) {
            //per sbloccare i record
            am.getTransaction().rollback();
            vo.remove();
            throw new Exception("Un altro operatore sta effettuando questa operazione. Si consiglia di riprovare più tardi.");
        }

        query =
            "UPDATE SO_CNF_IMPEXP SET ATTIVO=1 " + "WHERE TPDIP='" + r.getTpdip() + "' AND " + "IMPEXP='" +
            r.getImpexp() + "' AND " + "ULSS='" + r.getUlss() + "' " + "AND TPSCR='" + r.getTpscr() +
            "' and PROGRULSS=" + r.getProgrulss();
        am.getTransaction().executeCommand(query);

        am.getTransaction().commit();
        vo.remove();
        r.refresh(Row.REFRESH_CONTAINEES);

        return r;
    }

    public static synchronized Impexp_SoCnfImpexpViewRow getRowAndLock(ImpExp_AppModule am, String ulss, String tpscr,
                                                                       String tpdip) throws Exception {
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
        String where =
            "ulss = '" + ulss + "' AND tpscr = '" + tpscr + "' AND impexp = 'IMP' AND tpdip = '" + tpdip + "'";
        vo.setWhereClause(where);
        vo.executeQuery();
        Impexp_SoCnfImpexpViewRow row = (Impexp_SoCnfImpexpViewRow) vo.first();
        return getRowAndLock(am, row, true, true);
    }

    /**
     * Metodo che legge la riga di configurazione da DB e rimuove il lock sulla stessa
     * @throws java.lang.Exception
     * @return
     * @param ctx
     */
    public static synchronized Impexp_SoCnfImpexpViewRow getRowAndUNLock(boolean extended,
                                                                         boolean acrossTpscr) throws Exception {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
        Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();

        //Boolean myLock=(Boolean)ADFContext.getCurrent().getSessionScope();.getAttribute("impexp_lock");
        r = getRowAndUNLock(am, r, extended, acrossTpscr);
        ADFContext.getCurrent().getSessionScope().remove("impexp_lock");
        return r;
    }

    /*  public  static synchronized Impexp_SoCnfImpexpViewRow getRowAndUNLock(ImpExp_AppModule am,Impexp_SoCnfImpexpViewRow r)
    {
      r.refresh(Row.REFRESH_CONTAINEES);
      //unlock sull'import
      r.setAttivo(ConfigurationConstants.DB_FALSE);
      am.getTransaction().commit();
       return r;
    }*/

    public static synchronized void unlock(ImpExp_AppModule am, String tpdip, String impexp, String ulss,
                                           String tpscr) throws Exception {

        // Lettura della configurazione per tutti i tipi screening
        String query =
            "SELECT PROGRULSS, " + //1
            "IDCENTROREF, " + //2
            "IMPEXP, " + //3
            "TPDIP, " + //4
            "ULSS, " + //5
            "TPSCR, " + //6
            "ATTIVO, " + //7
            "MODALITA " + //8
            "FROM SO_CNF_IMPEXP \n" + "WHERE TPDIP='" + tpdip + "' AND IMPEXP='" + impexp + "' AND ULSS='" + ulss +
 "' FOR UPDATE";

        ViewObject vo = am.createViewObjectFromQueryStmt("ImpexpFlagView1", query);
        vo.executeQuery();

        // L'operazione deve essere bloccata per il tipo screening corrente, sbloccata per gli altri
        // altrimenti segnalo l'anomalia nel log
        while (vo.hasNext()) {
            Row row = vo.next();
            String rowTpscr = (String) row.getAttribute("TPSCR");
            Integer rowAttivo = row.getAttribute("ATTIVO")!=null ? ((oracle.jbo.domain.Number) row.getAttribute("ATTIVO")).intValue() : null;
            if (tpscr.equals(rowTpscr)) {
                if (ConfigurationConstants.DB_FALSE.equals(rowAttivo)) {
                    am.log(ulss, tpdip, impexp, "Un altro processo ha sbloccato l'operazione durante l'elaborazione",
                           tpscr);
                }
            } else {
                if (ConfigurationConstants.DB_TRUE.equals(rowAttivo)) {
                    am.log(ulss, tpdip, impexp,
                           "Un processo di tipo " + rowTpscr + " ha bloccato l'operazione durante l'elaborazione",
                           tpscr);
                }
            }
        }

        query =
            "UPDATE SO_CNF_IMPEXP SET ATTIVO=0 " + "WHERE TPDIP='" + tpdip + "' AND IMPEXP='" + impexp +
            "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'";

        am.getTransaction().executeCommand(query);
        am.getTransaction().commit();
        vo.remove();
    }


    public static synchronized Impexp_SoCnfImpexpViewRow getRowAndUNLock(ImpExp_AppModule am,
                                                                         Impexp_SoCnfImpexpViewRow r, boolean extended,
                                                                         boolean acrossTpscr) throws Exception {
        //controllo se c'è almeno uno dei flag relativi attivo e din tal caso dò errore
        String query =
            "SELECT PROGRULSS, " + //1
            "IDCENTROREF,  " + //2
            "IMPEXP, " + //3
            "TPDIP, " + //4
            "ULSS, " + //5
            "TPSCR, " + //6
            "ATTIVO, " + //7
            "MODALITA " + //8
            "FROM SO_CNF_IMPEXP \n" + "WHERE TPDIP='" + r.getTpdip() + "' AND " + "IMPEXP='" + r.getImpexp() +
            "' AND " + "ULSS='" + r.getUlss() + "' ";
        //se non uso la versione estesa prendo solo la riga corrente
        if (!extended)
            query += "AND TPSCR='" + r.getTpscr() + "' and PROGRULSS=" + r.getProgrulss();
        //SE non USO LA VERSIONE ACROSSsCREENING, LAVORO SOLO SUL MIO SCRENING
        if (!acrossTpscr)
            query += "AND TPSCR='" + r.getTpscr() + "' ";

        query += " FOR UPDATE";

        ViewObject vo = am.createViewObjectFromQueryStmt("ImpexpFlagView1", query);
        vo.executeQuery();

        query =
            "UPDATE SO_CNF_IMPEXP SET ATTIVO=0 " + "WHERE TPDIP='" + r.getTpdip() + "' AND " + "IMPEXP='" +
            r.getImpexp() + "' AND " + "ULSS='" + r.getUlss() + "' ";
        //se non uso la versione estesa prendo solo la riga corrente
        if (!extended)
            query += "AND TPSCR='" + r.getTpscr() + "' and PROGRULSS=" + r.getProgrulss();
        //SE non USO LA VERSIONE ACROSSsCREENING, LAVORO SOLO SUL MIO SCRENING
        if (!acrossTpscr)
            query += "AND TPSCR='" + r.getTpscr() + "' ";
        am.getTransaction().executeCommand(query);
        //   System.out.println(query);
        am.getTransaction().commit();
        vo.remove();
        r.refresh(Row.REFRESH_CONTAINEES);

        return r;
    }


    /**
     Copia il file selzionato nel file della directory virtuale accessibile tramite
     external table. Per il colon deve anche invocare la procedura che, partendo dal
     file ricevuto, lo modifica per ottenere la tabella  desiderata
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @return
     * @param f
     * @param r
     */
    public static File copyToDirVirtuale(Impexp_SoCnfImpexpViewRow r, File f, ImpExp_AppModule am) throws Exception {


        if (f == null)
            return null;
        //adesso copio il file nella dir virtuale
        String virtualDir = r.getPosizdirvirtuale();
        if (!virtualDir.endsWith("\\") && !virtualDir.endsWith("/"))
            virtualDir += "/";
        virtualDir += r.getFilevirtuale();
        File virtuale = new File(virtualDir);
        StreamUtils.copyInputToOutput(new FileInputStream(f), new FileOutputStream(virtuale));

        //se si tratta del colon devo anche spachettare
        if ("CO".equals(r.getTpscr()) && ConfigurationConstants.TPDIP_LAB.equals(r.getTpdip())) {
            String msg = am.callUnpackReferti(r.getUlss(), r.getTpscr());
            if (msg != null)
                throw new Exception(msg);
        }

        /*la cancellzione avviene a livello di thread per l'import dell'anagrafe e
       * quando chiudo l'operazione per i referti
       * if(r.getCancellazione()!=null && r.getCancellazione().intValue()==1)
           f.delete();*/


        return f;
    }

    public static Impexp_SoCnfImpexpViewRow setCurrentCNF(String ulssFlusso, String tipoFlusso) throws Exception {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String tpDip = tipoFlusso.substring(0, 2);
        String whcl = "TPDIP = '" + tpDip + "' and TPSCR = '" + tpscr + "' and ULSS = '" + ulssFlusso + "'";
        vo.setWhereClause(whcl);
        vo.executeQuery();

        int count = vo.getRowCount();

        if (count == 0)
            throw new Exception("Configurazione di import/export non trovata per dipartimentale e tipo screening di interesse");
        else if (count > 1)
            throw new Exception("Trovata più di una configurazione di import/export per dipartimentale e tipo screening di interesse");

        Impexp_SoCnfImpexpViewRow cfg = (Impexp_SoCnfImpexpViewRow) vo.first();

        vo.setCurrentRow(cfg);

        return cfg;

    }

    public static Impexp_SoCnfImpexpViewRow getCurrentCNF() {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");

        Impexp_SoCnfImpexpViewRow cfg = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();

        return cfg;

    }

    public static void initializeLockState() {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
        vo.setCurrentRow(null);

        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.put("impexp_lock_tpdip", null);


    }

    public static void releaseLock() throws Exception {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();

        Map sess = ADFContext.getCurrent().getSessionScope();
        String tpDip = (String) sess.get("impexp_lock_tpdip");

        if (tpDip == null)
            throw new Exception("Configurazione di import/export non identificata");

        String tipoFlusso = tpDip.equals("SD") ? "SDO" : "SPS";
        String ulss = (String) sess.get("ulss");
        Impexp_SoCnfImpexpViewRow cfg = ImpexpUtils.setCurrentCNF(ulss, tipoFlusso);

        ImpexpUtils.getRowAndUNLock(am, cfg, true, true);

    }

    public static void setLock(String ulssFlusso, String tipoFlusso) throws Exception {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();

        Impexp_SoCnfImpexpViewRow cfg = ImpexpUtils.setCurrentCNF(ulssFlusso, tipoFlusso);

        // registro ultimo dipartimentale su cui è stato tentato un lock
        Map sess = ADFContext.getCurrent().getSessionScope();
        ;
        String tpDip = tipoFlusso.substring(0, 2);
        sess.put("impexp_lock_tpdip", tpDip);

        ImpexpUtils.getRowAndLock(am, cfg, true, true);

    }

    public static boolean isLockActive() throws Exception {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        Impexp_SoCnfImpexpViewRow cfg = ImpexpUtils.getCurrentCNF();
        return ImpexpUtils.isRowLocked(am, cfg, true, true);

    }


    public static synchronized boolean isRowLocked(ImpExp_AppModule am, Impexp_SoCnfImpexpViewRow r, boolean extended,
                                                   boolean acrossTpscr) throws Exception {
        //controllo se c'è almeno uno dei flag relativi attivo e din tal caso dò errore
        if (r == null)
            return false;

        String query =
            "SELECT PROGRULSS, " + //1
            "IDCENTROREF,  " + //2
            "IMPEXP, " + //3
            "TPDIP, " + //4
            "ULSS, " + //5
            "TPSCR, " + //6
            "ATTIVO, " + //7
            "MODALITA " + //8
            "FROM SO_CNF_IMPEXP \n" + "WHERE TPDIP='" + r.getTpdip() + "' AND " + "IMPEXP='" + r.getImpexp() +
 "' AND " + "ULSS='" + r.getUlss() + "' ";
        //se non uso la versione estesa prendo solo la riga corrente
        if (!extended)
            query += "AND TPSCR='" + r.getTpscr() + "' and PROGRULSS=" + r.getProgrulss();
        //SE non USO LA VERSIONE ACROSSsCREENING, LAVORO SOLO SUL MIO SCRENING
        if (!acrossTpscr)
            query += "AND TPSCR='" + r.getTpscr() + "' ";
        //     System.out.println(query);

        ViewObject vo = am.createViewObjectFromQueryStmt("ImpexpFlagView1", query);
        vo.executeQuery();
        Row r1 = null;
        boolean locked = false;
        while ((r1 = vo.next()) != null) {
            //se il record è lockato
            if (ConfigurationConstants.DB_TRUE.equals(r1.getAttribute(6))) {
                locked = true;
                break;
            }
        }

        vo.remove();
        return locked;

    }

    public static String getDwnDir(Impexp_SoCnfImpexpViewRow cfg) {
        //return "D:/dati_temp";
        return cfg.getPosizfilearr();
    }

    public static String getVirtDir(Impexp_SoCnfImpexpViewRow cfg) {
        //return "D:/dati_temp/virtuale";
        return cfg.getPosizdirvirtuale();
    }

}



