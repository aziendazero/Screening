package view.impexp;

import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.DateUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.ImpExp_AppModule;

import model.common.Parent_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ConfigurationReader;

import model.datacontrol.ImpexpBean;

import model.impexp.Impexp_SoCnfImpExpLogViewRowImpl;
import model.impexp.common.Impexp_SoCnfImpExpLogViewRow;
import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import model.print.ReportHandler;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

import view.commons.action.Parent_DataForwardAction;


public abstract class Impexp_dataForwardAction extends Parent_DataForwardAction {
    protected void setAppModule() {
        this.amName = "ImpExp_AppModule";
    }
    
    /**
     * Restituisce il nome dell apermission da controllare prima di eseguire
     * qualsiasi operazione
     * @return
     */
    protected abstract String getRelatedPermission();

    /**
     *Restituisce true se il lock deve essere esteso anche ai record simili di
     * altri screening (usato per l'anagrafe), false altrimenti
     * @return
     */
    protected abstract boolean getAcrossTpscr();

    /**
     * Esegue l'operazione di import corretta
     * @return
     */
    protected abstract String doImport(HashMap parameters, Integer[] rc) throws Exception;

    /**
     * @return titolo del report di log
     */
    protected abstract String getLogTitle();


    /**
     * Chiude l'operazione facendo due cose molto importanti:
     * 1. sblocca il record dell'import impstando attivo a false
     * 2. cancella o rinomina il file sulla dir locale a seconda della scelta di configurazione
     * @param ctx
     */
    public String chiudiOperazione(String outcome) {
        try {
            AccessManager.checkPermission(this.getRelatedPermission());
            Map sess = ADFContext.getCurrent().getSessionScope();

            Impexp_SoCnfImpexpViewRow r = ImpexpUtils.getRowAndUNLock(true, this.getAcrossTpscr());

            String path = (String) sess.get("impexp_file");
            File f = null;
            if (path != null)
                f = new File(path);
            if (f != null) {
                //cancellazione file sulla dir fisica locale oppure rinomina del file
                if (r.getCancellazione() != null && Boolean.valueOf(r.getCancellazione().toString()).booleanValue()) {
                    //se il file c'e', lo cancello

                    f.delete();
                } else {
                    //controllo se non sto gia' lavorando su un file vecchio
                    if (!f.getName().endsWith("_OLD") && !f.getName().endsWith("_IMPORTED")) {
                        File old = new File(f.getAbsolutePath() + "_OLD");
                        f.renameTo(old);
                    }
                }
            }

            //per tornare alla selezione nei referti
            sess.remove("impexp_referti_mode");
            sess.remove("impexp_presenze_mode");

            return outcome; 
        } catch (Exception ex) {
            //  ex.printStackTrace();
            this.handleException(ex.getMessage(), null);
            return "error";
        }
    }
    
    public void downloadLogListener(FacesContext facesContext, OutputStream outputStream) throws Exception {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voiter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject vo = voiter.getViewObject();

        try {
            Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();

            if (r == null) {
                this.handleException("Impossibile visualizzare il log: non e' stata selezionata la configurazione di import/export");
            } else {
                r.refresh(r.REFRESH_CONTAINEES);

                if (r.getLogFile() == null)
                    throw new Exception("Nessun file di log memorizzato");

                BlobDomain blob = r.getLogFile();
                BufferedInputStream in = null;

                in = new BufferedInputStream(blob.getBinaryStream());

                int b;
                byte[] buffer = new byte[10240];
                while ((b = in.read(buffer, 0, 10240)) != -1) {
                    outputStream.write(buffer, 0, b);
                }
                outputStream.close();
            }

        } catch (Exception ex) {
            //this.handleException("Impossibile visualizzare il log: " + ex.getMessage());
            throw new Exception("Impossibile visualizzare il log: " + ex.getMessage());
        } finally {

        }

    }
    
    public void downloadLogListenerStory(FacesContext facesContext, OutputStream outputStream) throws Exception {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voiter = bindings.findIteratorBinding("Impexp_SoCnfImpExpLogView1Iterator");
        ViewObject vo = voiter.getViewObject();

        try {
            Impexp_SoCnfImpExpLogViewRow r = (Impexp_SoCnfImpExpLogViewRow) vo.getCurrentRow();

            if (r == null) {
                this.handleException("Impossibile visualizzare il log");
            } else {
                r.refresh(r.REFRESH_CONTAINEES);

                if (r.getLogFile() == null)
                    throw new Exception("Nessun file di log memorizzato");

                BlobDomain blob = r.getLogFile();
                BufferedInputStream in = null;

                in = new BufferedInputStream(blob.getBinaryStream());

                int b;
                byte[] buffer = new byte[10240];
                while ((b = in.read(buffer, 0, 10240)) != -1) {
                    outputStream.write(buffer, 0, b);
                }
                outputStream.close();
            }

        } catch (Exception ex) {
            throw new Exception("Impossibile visualizzare il log: " + ex.getMessage());
        } finally {

        }

    }
    
    public void downloadScheduledLog(FacesContext facesContext, OutputStream outputStream) throws Exception {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voiter = bindings.findIteratorBinding("Impexp_CodaProcessiView1Iterator");
        ViewObject vo = voiter.getViewObject();
        Row r = vo.getCurrentRow();
        String filename = (String) r.getAttribute("LinkLog");
        filename = ConfigurationReader.readProperty("scheduledLogPath") + "/" + filename.trim();

        File pdf = new File(filename);

        FileInputStream fis;
        byte[] b;
        try {
            fis = new FileInputStream(pdf);
            int n;
            while ((n = fis.available()) > 0) {
                b = new byte[n];
                int result = fis.read(b);
                outputStream.write(b, 0, b.length);
                if (result == -1)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        outputStream.flush();
                     
    }

    public void onImport() {
        File pdf = null;
        
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding cnfIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        DCIteratorBinding cnfLogIter = bindings.findIteratorBinding("Impexp_SoCnfImpExpLogView1Iterator");

        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        
        ViewObject vo = cnfIter.getViewObject();
        ViewObject voLog = cnfLogIter.getViewObject();

        ImpExp_AppModule am = (ImpExp_AppModule) vo.getApplicationModule();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String user = (String) session.get("user");
        
        Impexp_SoCnfImpexpViewRow r = null;
        Impexp_SoCnfImpExpLogViewRow newLog =null;

        try {
            AccessManager.checkPermission(this.getRelatedPermission());

            //lock dell'operazione
            r = ImpexpUtils.getRowAndLock(true, this.getAcrossTpscr(), vo);
            
            String filename = (String) session.get("impexp_file_name");
            if (filename != null) {
                if (filename.endsWith("_IMPORTED"))
                    filename = filename.substring(0, filename.indexOf("_IMPORTED"));
                else if (filename.endsWith("_OLD"))
                    filename = filename.substring(0, filename.indexOf("_OLD"));
            }
            
            //I00095792: Creo il record di Log
            Integer id = ((Parent_AppModule) am).getNextIdImpExpLog();

            newLog = (Impexp_SoCnfImpExpLogViewRow)voLog.createRow();
            newLog.setId(new oracle.jbo.domain.Number(id));
            newLog.setUlss(ulss);
            newLog.setTpscr(tpscr);
            newLog.setImpexp("IMP");
            newLog.setTpdip(r.getTpdip());
            newLog.setProgrulss(new oracle.jbo.domain.Number(r.getProgrulss()));
            newLog.setOp(user); 
            newLog.setDtinizioelab(DateUtils.getOracleDate(new Date()));
            newLog.setNomefileimport(filename);

            voLog.insertRow(newLog);
            
            commitBinding.execute();
            //I00095792 END

            if (r.getTemplate() == null)
                throw new Exception("E' necessario inserire il template per il report con il log degli errori");

            //cerca il file su cui lavorare
            //(se ci sono problemi viene generata un'eccezione)
            File f = this.searchForFile(r);

            if (f != null) {
                session.put("impexp_file", f.getAbsolutePath());
                session.put("impexp_file_name", f.getName());
                //copia e rinomina nella dir virtuale
                f = ImpexpUtils.copyToDirVirtuale(r, f, am);
            }

            //esecuzione dell'import
            HashMap params = new HashMap();
            params.put("appmodule", am);
            params.put("ulss", ulss);
            params.put("tpscr", tpscr);
            params.put("centro", r.getIdcentroref());
            params.put("tpdip", r.getTpdip());

            Integer[] rc = new Integer[] { new Integer(0) };

            String msg = this.doImport(params, rc);


            if (rc[0].intValue() == -1)
                throw new Exception(msg);

            this.renameFile(f);

            //creazione e memorizzazione del log
            HashMap map = new HashMap();
            map.put("ulss", ulss);
            map.put("tpscr", tpscr);
            map.put("gruppo", r.getTpdip());
            map.put("impexp", r.getImpexp());
            map.put("data_log", DateUtils.getNow());
            map.put("luogo", r.getSuffisso());
            map.put("titolo", this.getLogTitle());

            map.put("nome_file", filename);
            map.put("utente", session.get("user"));

            ReportHandler rh = new ReportHandler();
            pdf =
                rh.createReport(r, "Impexp_SoCnfImpexpView1", "Template", "TemplateCompilato", am.getDBConnection(),
                                map);

            // mauro 07/06/2010
            rh.cleanup();
            // mauro 07/06/2010, fine

            r.setLogFile(BlobUtils.getBlobFromFile(pdf));

            //I00095792: aggiorno il record di Log
            newLog.setLogFile(BlobUtils.getBlobFromFile(pdf));
            
            String nomeFileLog = "LogPresenze" + id + ".pdf";
            newLog.setNomefilelog(nomeFileLog);
            
            //se il risultato e' null non ci sono stati errori
            if (rc[0].intValue() != -1)
                this.handleMessages(FacesMessage.SEVERITY_INFO,
                                    "Import eseguito con successo. E' ora possibile chiudere l'operazione.");
            else
                throw new Exception(msg);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException("Sono stati riscontrati problemi nell'esecuzione dell'operazione: " + ex.getMessage(),
                                 am.getTransaction());
            return;
        } finally {
            //cancello il pdf, tanto e' stato salvato nel DB
            if (pdf != null)
                pdf.delete();

        }
        try {
            //aggiornamento data import
            //se levoro su piu' screening estendo a tutti
            if (this.getAcrossTpscr()) {
                String pattern = DateUtils.DATE_TIME_PATTERN;
                //per i minuti il db vuole mi invece di mm
                pattern = pattern.replaceFirst("mm", "mi");
                //per le 24 ore vuole HH24 invece di HH
                pattern = pattern.replaceFirst("HH", "HH24");
                String update =
                    "UPDATE SO_CNF_IMPEXP SET DTIMPORT=TO_DATE('" + DateUtils.getNow() + "','" + pattern + "') " +
                    "WHERE TPDIP='" + r.getTpdip() + "' AND " + "IMPEXP='" + r.getImpexp() + "' AND " + "ULSS='" +
                    r.getUlss() + "' ";

                am.getTransaction().executeCommand(update);

                //aggiorno tutti i log con quello appena creato
                update =
                    "UPDATE SO_CNF_IMPEXP SET LOG_FILE=(" + "SELECT LOG_FILE FROM SO_CNF_IMPEXP I " +
                    "WHERE I.PROGRULSS=" + r.getProgrulss() + " AND I.IMPEXP='" + r.getImpexp() + "' AND I.TPDIP='" +
                    r.getTpdip() + "' AND I.ULSS='" + r.getUlss() + "' AND TPSCR='" + r.getTpscr() +
                    "') WHERE IMPEXP='" + r.getImpexp() + "' AND TPDIP='" + r.getTpdip() + "' AND ULSS='" +
                    r.getUlss() + "' AND TPSCR<>'" + r.getTpscr() + "'";
                am.getTransaction().executeCommand(update);

            } else //altrimenti imposto solo quella del record corrente
                r.setDtimport(DateUtils.getTimestampNow());
            
            //I00095792: imposto data fine elaborazione del record di log
            newLog.setDtfineelab(DateUtils.getOracleDate(new Date()));
            
            commitBinding.execute();

        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException("Operazione completata con successo, ma impossibile aggiornare la data dell'ultimo import o il log per gli altri screening: " +
                                 ex.getMessage(), am.getTransaction());
        }

    }

    /**
     * Imposta la rig acorrente in base al centro selezionato (va sempre eseguito
     * dopo aver filtrato le configurzioni in base alla tipologia)
     * @param ctx
     */
    public static void selectCentro(ImpExp_AppModule am, ImpexpBean bean) throws Exception {
      
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
  
        selectCentro(vo, bean.getIdcentro());

    }
    
    /**
     * Imposta la rig acorrente in base al centro selezionato (va sempre eseguito
     * dopo aver filtrato le configurzioni in base alla tipologia)
     * @param ctx
     */
    public static void selectCentro(ViewObject vo, Integer idCentro) throws Exception {
         
        //cerco l ariga di configurazione corretta per il file selezionato
        //e la imposto come riga corrente
        if (idCentro != null) {
            try {

                Row[] result = vo.getFilteredRows("Idcentroref", idCentro);
                if (result.length == 0)
                    throw new Exception("Nessuna configurazione trovata per il centro selezionato");
                /*if(result.length>1)
              throw new Exception("Troppe configurazioni trovate per il centro selezionato");*/
                vo.setCurrentRow(result[0]);
            } catch (Exception e) {
                //significa che non ho trovato nessuna configurazione per quel centro,
                //provo come s eil centro non ci fosse
                if (vo.getRowCount() == 0)
                    throw new Exception("Nessuna configurazione trovata");
                if (vo.getRowCount() > 1)
                    throw new Exception("Troppe configurazioni trovate");
                vo.first();
            }
        } else {
            if (vo.getRowCount() == 0)
                throw new Exception("Nessuna configurazione trovata");
            if (vo.getRowCount() > 1)
                throw new Exception("Troppe configurazioni trovate");
            vo.first();

        }

        Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();
        //setto anche il suffisso
        ImpexpBean bean =
            (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();
        if (r.getAttribute("Suffisso") != null){   
            bean.setCentro((String)r.getAttribute("Suffisso"));
        } else {
            bean.setCentro(null);
        }
        ADFContext.getCurrent().getSessionScope().put("impexp_localdir", r.getPosizfilearr());
        ADFContext.getCurrent().getSessionScope().put("impexp_localfile", r.getNomefilearr());

    }

    /**
     * metdodo che ogni import sovrscrivera' per applicare l apropia politica
     * @throws java.io.FileNotFoundException
     * @return
     * @param r
     * @param ctx
     */
    protected abstract File searchForFile(Impexp_SoCnfImpexpViewRow r) throws Exception;


    /**
     * Metodo che filtra i file dei referti in base a centro e data
     * @throws java.lang.Exception
     * @param ctx
     */
    public static void filterFiles() throws Exception {
        ImpexpBean bean =
            (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();
        Map sess = ADFContext.getCurrent().getSessionScope();

            //imposto il filtro sulla data
            if (bean.getData() != null ) {
                //convertiamo la data nel formato usato dal timestamp
                Date d = bean.getData();
                String s = new SimpleDateFormat(ConfigurationConstants.IMPEXP_DATE_PATTERN).format(d);
                //eliminiamo ora e minuti, cioe' gli ultimi 4 caratteri
                s = s.substring(0, s.length() - 4);
                //impostiamo il filtro nel provider
                //   ImpexpProvider.dateFilter=s;
                sess.put("impexp_dateFilter", s);


            } else
                //ImpexpProvider.dateFilter=null;
                sess.remove("impexp_dateFilter");


            //imposto il filtro sul centro
            if (bean.getCentro() != null && bean.getCentro().length() > 0) {
                // ImpexpProvider.centerFilter=bean.getCentro();
                sess.put("impexp_centerFilter", bean.getCentro());
            } else
                //ImpexpProvider.centerFilter=null;
                sess.remove("impexp_centerFilter");

    }

    /**
     * Metodo che filtra i file dei referti in base a centro e data ed imposta
     * la riga di configurazione corrente in base alla selezione del centro
     * @throws java.lang.Exception
     * @param ctx
     */
    public void selectCentro(ValueChangeEvent valueChangeEvent) {
        try {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
            ViewObject vo = voIter.getViewObject();
            selectCentro(vo,(Integer)valueChangeEvent.getNewValue());
            filterFiles();
        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
        }
    }

    public void selectOnlyCentro(ValueChangeEvent valueChangeEvent) {
        try {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
            ViewObject vo = voIter.getViewObject();
            selectCentro(vo,(Integer)valueChangeEvent.getNewValue());
        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
        }
    }
    
    public void selectTpdip(ValueChangeEvent valueChangeEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        try {

            ImpexpBean bean =
                (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();
            //potrei avere degli export verso diversi dipartimentali, imposto il primo che trovo
            //MOD 20090406
            //String wh="IMPEXP='"+ConfigurationConstants.IMPEXP_IMP+
            String wh = "IMPEXP='" + this.getImpExp() + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'";
            //in caso di utenti con restrizioni di centro filtro ulteriormente
            String in = (String) sess.get("elenco_centri");
            if (in != null) {
                wh +=
                    " AND IDCENTROREF IN (" + "SELECT R.idcentroref FROM SO_CENTRO_PREL_REF R " + "WHERE idcentro in " +
                    in + ")";
            }

            //14082012 gaion: se e' un export puntuale on demand non serve filtrare i centri perche' e' gia' settato
            DCIteratorBinding voIter = bindings.findIteratorBinding("Impexp_SoCnfCentriImpexpView1Iterator");
            ViewObject vo = voIter.getViewObject();         
            vo.setWhereClause(wh + " AND TPDIP='" + valueChangeEvent.getNewValue() + "'");
            vo.executeQuery();
            System.out.println("RIGHE>>>"+vo.getEstimatedRowCount());
            //ed imposto i dati del bean sul primo centro ch etrovo
            Row r2 = vo.first();
            if (r2 != null) {
                bean.setIdcentro(r2.getAttribute("Idcentroref") == null ? null :
                                 (Integer) r2.getAttribute("Idcentroref"));
                bean.setCentro((String) r2.getAttribute("Suffisso"));
            } else {
                bean.setIdcentro(null);
                bean.setCentro(null);
            }
            //}
            // fine 14082012

            voIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
            vo = voIter.getViewObject();
            vo.setWhereClause(wh + " AND TPDIP='" + valueChangeEvent.getNewValue() + "'");
            vo.executeQuery();

            Impexp_dataForwardAction.selectCentro((ImpExp_AppModule) vo.getApplicationModule(), bean);

            Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();

            if (ConfigurationConstants.MODALITA_FTP.equals(r.getModalita())) {
                File dir = new File(r.getPosizfilearr());
                if (!dir.exists())
                    throw new Exception("Directory " + dir.getAbsolutePath() + " non trovata");

                sess.put("impexp_localdir", dir.getAbsolutePath());
                sess.put("impexp_localfile", r.getNomefilearr());
            }

            //memorizzo la modalita' nella sessione
            sess.put("impexp_modalita", r.getModalita());


        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
        }
    }

    public abstract String getImpExp();
    
    public void renameFile(File f) {

        Map s = ADFContext.getCurrent().getSessionScope();
        String path = (String) s.get("impexp_file");

        if (path != null)
            f = new File(path);

        if (path.indexOf("_IMPORTED") < 0) {
            if (path.indexOf("_OLD") > 0) {
                path = path.substring(0, path.indexOf("_OLD"));
            }

            path = path + "_IMPORTED";
            File old = new File(path);
            f.renameTo(old);
        }

    }

    
}
