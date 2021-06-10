package view.impexp;

import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.DateUtils;

import insiel.dataHandling.StreamUtils;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileOutputStream;

import java.math.BigDecimal;

import java.text.ParseException;

import java.util.Date;
import java.util.HashMap;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.ImpExp_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;

import model.datacontrol.ImpexpBean;

import model.filters.ViewObjectFilters;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import model.print.ReportHandler;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class Impexp_appuntamentiAction extends Impexp_dataForwardAction {
    public Impexp_appuntamentiAction() {
        super();
    }

    @Override
    protected String getRelatedPermission() {
        return "SOExportAppuntamenti";
    }

    @Override
    protected boolean getAcrossTpscr() {
        //lavoro solo sul mio screening
        return false;
    }

    @Override
    protected String doImport(HashMap parameters, Integer[] rc) throws Exception {

        return null;
    }

    @Override
    protected String getLogTitle() {
        return "EXPORT APPUNTAMENTI";
    }

    @Override
    protected File searchForFile(Impexp_SoCnfImpexpViewRow r) throws Exception {
        return null;
    }

    @Override
    public String getImpExp() {
        return ConfigurationConstants.IMPEXP_EXP;
    }
    
    public void beforeNavigate(String dest) throws Exception {
        Map session = ADFContext.getCurrent().getSessionScope();
        
        //controllo che l'utente abbia chiuso l'operazione
        Boolean myLock = (Boolean) session.get("impexp_lock");
        if (myLock != null && myLock.booleanValue())
            throw new Exception("Impossible navigare: l'operazione non risulta chiusa.");
        
        session.put("LINK_INV", Boolean.FALSE);
    }

    public void findForward() throws Exception {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        try {

            session.put("renderChiudiOp", Boolean.TRUE);

            this.filter(am, session);
            ImpexpBean bean =
                (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();
            
            //potrei avere degli export verso diversi dipartimentali, imposto il primo che trovo
            // 11092016 Gaion: cambiato il view object per i dipartimentali
            ViewObject tp = am.findViewObject("Impexp_SoCnfTpdipartimentaleView1");
            String wh =
                "IMPEXP='" + ConfigurationConstants.IMPEXP_EXP + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'";

            tp.setWhereClause(wh);
            tp.executeQuery();
            System.out.println(tp.getQuery());

            //imposto il primo che trovo anche se ce ne sono di piu'
            Row x = tp.first();
            if (x != null) {
                bean.setTpdip((String) x.getAttribute("Tpdip"));
                //filtro anche i centri utilizzati nelle configurazioni PER QUEL TIPO DIP
                ViewObject vo = am.findViewObject("Impexp_SoCnfCentriImpexpView1");
                vo.setWhereClause(wh + " AND TPDIP='" + bean.getTpdip() + "'");
                vo.executeQuery();

                //ed imposto i dati del bean sul primo centro ch etrovo
                Row r2 = vo.first();
                if (r2 != null) {
                    bean.setIdcentro(((Integer) r2.getAttribute("Idcentroref")).intValue());
                    bean.setCentro((String) r2.getAttribute("Suffisso"));
                } else {
                    bean.setIdcentro(null);
                    bean.setCentro(null);
                }

                //filtro sulla configurazione dell'import che mi interessa
                vo = am.findViewObject("Impexp_SoCnfImpexpView1");
                vo.setWhereClause(wh + " AND TPDIP='" + bean.getTpdip() + "'");
                vo.executeQuery();

                //OTTENGO TUTTE LE RIGHE, UNA PER OGNI CENTRO DI REFERTAZIONE

                Impexp_dataForwardAction.selectCentro(vo,bean.getIdcentro());

                Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();
                if (r != null) {
                    if (r.getTest() != null) {
                        if ("HPV".equals(r.getTest()))
                            bean.setInviti_hpv(1);
                        else if ("PAP".equals(r.getTest()))
                            bean.setInviti_hpv(0);
                    } else  {
                        bean.setInviti_hpv(-1);
                    }

                    if (r.getTipo() != null) {
                        if ("appuntamenti".equals(r.getTipo()))
                            bean.setOp_type(0);
                        else if ("accettazioni".equals(r.getTipo()))
                            bean.setOp_type(1);
                        else if ("cancellazioni".equals(r.getTipo()))
                            bean.setOp_type(2);
                        else if ("HPV positivi".equals(r.getTipo()))
                            bean.setOp_type(3);
                    }
                }

                //memorizzo la modalita' nella sessione
                session.put("impexp_modalita", r.getModalita());

                session.put("LINK_INV", Boolean.FALSE);

                //livello di default
                bean.setLivello("1");

            }

        } catch (Exception ex) {
            //  ex.printStackTrace();
            this.handleException("Funzionalita' non utilizzabile: " + ex.getMessage());
        } 
    }


    private void filter(ImpExp_AppModule am, Map session) {
        
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String in = (String) session.get("elenco_centri");

        String data = DateUtils.getToday();

        //centri di prelievo
        ViewObject vo = am.findViewObject("Impexp_SoCentroPrelView1");
        ViewObjectFilters.filterCentri(vo, ulss, tpscr, data, in);

    }
    
    public void selectTpdip(ValueChangeEvent valueChangeEvent) {
        
        super.selectTpdip(valueChangeEvent);
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject vo = voIter.getViewObject();

        Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();
        ImpexpBean bean =
            (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();

        if (r != null) {
            if (r.getTest() != null) {
                if ("HPV".equals(r.getTest()))
                    bean.setInviti_hpv(1);
                else if ("PAP".equals(r.getTest()))
                    bean.setInviti_hpv(0);
            }

            if (r.getTipo() != null) {
                if ("appuntamenti".equals(r.getTipo()))
                    bean.setOp_type(0);
                else if ("accettazioni".equals(r.getTipo()))
                    bean.setOp_type(1);
                else if ("cancellazioni".equals(r.getTipo()))
                    bean.setOp_type(2);
                else if ("HPV positivi".equals(r.getTipo()))
                    bean.setOp_type(3);
            }
        }

    }

    public void selectOnlyCentro(ValueChangeEvent valueChangeEvent) {
        super.selectOnlyCentro(valueChangeEvent);
           
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject vo = voIter.getViewObject();

        Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();
        ImpexpBean bean =
            (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();
            
            if (r != null){
              if (r.getTest() != null)
              {
                if ("HPV".equals(r.getTest()))
                  bean.setInviti_hpv(1);
                else if ("PAP".equals(r.getTest()))
                  bean.setInviti_hpv(0);
              }
              
              if (r.getTipo() != null) 
              {
                if ("appuntamenti".equals(r.getTipo()))
                  bean.setOp_type(0);
                else if ("accettazioni".equals(r.getTipo()))
                  bean.setOp_type(1);
                else if ("cancellazioni".equals(r.getTipo()))
                  bean.setOp_type(2);
                else if ("HPV positivi".equals(r.getTipo()))
                  bean.setOp_type(3);
              }
            }
    }

    public void export(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        ImpexpBean bean =
            (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();

        File pdf = null;

        try {

            // tipo di export
            BigDecimal exptype = new BigDecimal(bean.getOp_type());

            // controllo il permesso all'export di accettazioni o HPV positivi
            if (bean.getOp_type() == 1 || bean.getOp_type() == 3)
                AccessManager.checkPermission("SOExportAccettazioni");
            else
            // controllo il permesso alla cancellazione
            if (bean.getOp_type() == 2)
                AccessManager.checkPermission("SOExportCancellazioni");
            else
                // controllo il permesso all'export degli appuntamenti (che vale sia per gli appuntamenti sia per le cancellazioni)
                AccessManager.checkPermission("SOExportAppuntamenti");

            // imposto la riga corrente in base al centro selezionato e controllo i dati
            if (bean.getData() == null || bean.getData_max() == null)
                throw new Exception("e' necessario impostare il periodo temporale degli inviti da esportare");

            //reimposto la riga di configurazione giusta
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
            ViewObject vo = voIter.getViewObject();
            Impexp_dataForwardAction.selectCentro(vo, bean.getIdcentro());

            // lock dell'operazione
            Impexp_SoCnfImpexpViewRow r = ImpexpUtils.getRowAndLock(true, this.getAcrossTpscr(), vo);

            if (!ConfigurationConstants.MODALITA_FTP.equals(r.getModalita()) &&
                !ConfigurationConstants.MODALITA_PDD.equals(r.getModalita()))
                throw new Exception("Modalita' di scambio dati non supportata");

            if (r.getTemplate() == null)
                throw new Exception("E' necessario inserire il template per il report con il log degli errori");
            
            
            String dataStr =  DateUtils.DATE_FORMATTER.format(bean.getData());
            String dataMaxStr = DateUtils.DATE_FORMATTER.format(bean.getData_max());
                 

            //centro di refertazione (o comunque di destinazione)
            BigDecimal centro = null;
            if (bean.getIdcentro() != null) {
                Integer centroref = r.getIdcentroref();
                if (centroref != null)
                    centro = new BigDecimal(r.getIdcentroref().doubleValue());
                else
                    centro = null;
            }
            //centro di prelievo facoltativo
            BigDecimal centro_prel = null;
            if (bean.getCentro_prel() != null)
                centro_prel = new BigDecimal(bean.getCentro_prel().doubleValue());
            //livello (1 e' il default se non viene selezionato alcun centro di preliveo)
            BigDecimal livello = new BigDecimal(1);
            if (bean.getLivello() != null)
                livello = new BigDecimal(bean.getLivello());

            BigDecimal hpv = null;
            if (bean.getInviti_hpv() != null && bean.getInviti_hpv().intValue() >= 0)
                hpv = new BigDecimal(bean.getInviti_hpv().doubleValue());

            BigDecimal id_invito = null;
            if (bean.getId_invito() != null) {
                id_invito = new BigDecimal(bean.getId_invito().doubleValue());

            }

            //esecuzione della procedura di export
            String user = (String) session.get("user");
            ImpExp_AppModule am = (ImpExp_AppModule) vo.getApplicationModule();
            HashMap result =
                am.callExport(ulss, tpscr, centro, dataStr, dataMaxStr, centro_prel, livello, exptype,
                              bean.getTpdip(), hpv, id_invito, user);

            String msg = (String) result.get("error");

            //se il risultato e' null non ci sono stati errori
            if (msg == null || msg.length() == 0) {
                if (ConfigurationConstants.MODALITA_FTP.equals(r.getModalita())) {
                    this.spostaFileExport(r);
                }
                this.handleMessages(FacesMessage.SEVERITY_INFO, "Export eseguito con successo. E' ora possibile chiudere l'operazione.");
            } else {
                if (ConfigurationConstants.MODALITA_FTP.equals(r.getModalita())) {
                    this.deleteWrongFiles(r);
                }
                throw new Exception("Sono stati riscontrati problemi nell'esecuzione dell'export: " + msg);
            }

            try {
                //creazione e memorizzazione del log
                HashMap map = new HashMap();
                map.put("ulss", ulss);
                map.put("tpscr", tpscr);
                map.put("gruppo", r.getTpdip());
                map.put("impexp", r.getImpexp());
                map.put("data_log", DateUtils.getNow());
                map.put("luogo", r.getSuffisso());
                map.put("quanti", result.get("result"));
                if (bean.getOp_type() == 0)
                    map.put("titolo", "EXPORT APPUNTAMENTI");
                else if (bean.getOp_type() == 1)
                    map.put("titolo", "EXPORT ACCETTAZIONI");
                else if (bean.getOp_type() == 3)
                    map.put("titolo", "EXPORT HPV POSITIVI");
                else
                    map.put("titolo", "EXPORT CANCELLAZIONI");


                ReportHandler rh = new ReportHandler();
                pdf =
                    rh.createReport(r, "Impexp_SoCnfImpexpView1", "Template", "TemplateCompilato", am.getDBConnection(),
                                    map);
                rh.cleanup();

                r.setLogFile(BlobUtils.getBlobFromFile(pdf));

            } catch (Exception e) {
                throw new Exception("Impossibile creare il report di log " + r.getNomefilepart() + ": " +
                                    e.getMessage());
            }
            //aggiornamento data import
            r.setDtimport(DateUtils.getTimestampNow());
            am.getTransaction().commit();

        } catch (Exception ex) {
            this.handleException(ex.getMessage());
            System.out.println(ex.getMessage());
        } finally {
            //cancello il pdf, tanto e' stato salvato nel DB
            if (pdf != null)
                pdf.delete();
        }
    }
    
    /**
     * Metodo che cerca il file generato dalla procedura di export nella dir virtuale
     * e lo copia nella cartella da cui verra' trasferito DA TESTARE
     * @throws java.lang.Exception
     * @param r
     */
    private static void spostaFileExport(Impexp_SoCnfImpexpViewRow r) throws Exception {
        File f = null;
        File vir = null;
        File dir = null;
        try {
            String filevirt = r.getFilevirtuale();

            if (filevirt != null) {

                String virtual = r.getPosizdirvirtuale();
                //  System.out.println(virtual);
                vir = new File(virtual);
                if (!vir.exists())
                    throw new Exception("Directory " + virtual + " non trovata");

                //directocry in cui dovro' creare lo zip
                dir = new File(r.getPosizfilepart());

                if (!dir.exists())
                    throw new Exception("Directory " + r.getPosizfilepart() + " non trovata");
                f = new File(vir, filevirt);
                //se non trovo il file
                if (!f.exists())
                    // Se il file non esiste, e' stato creato in sua vece un flusso HL7 (SM1-1), quindi non e' errore
                    //throw new Exception("Non e' stato creato nessun file da trasferire");
                    return;

                //se si tratta di un file parziale vi appiccico il timestamp
                String s = r.getNomefilepart();
                if (r.getFileparziale() != null && r.getFileparziale().intValue() > 0)
                    s += DateUtils.dateToString(ConfigurationConstants.IMPEXP_DATE_PATTERN, new Date());
                //creo il file in cui trasferire quello della dir virtuale
                File out = new File(dir, s);
                StreamUtils.copyInputToOutput(new FileInputStream(f), new FileOutputStream(out));

            }
        } catch (Exception e1) {
            throw new Exception("Sono stati riscontrati problemi nell'esecuzione dell'export. Impossibile creare il file " +
                                r.getNomefilepart() + ": " + e1.getMessage());
        } finally {
            if (f != null)
                f.delete();
        }
    }

    /**
     * cancella i file eventualmete generati nella directory virtuale
     * (Viene invocato in caso di eccezione precedente alla generazione del file zip)
     * @param r
     */
    private static void deleteWrongFiles(Impexp_SoCnfImpexpViewRow r) {
        String virtual = r.getPosizdirvirtuale();
        if (virtual == null)
            return;
        File vir = new File(virtual);
        if (vir == null || !vir.exists())
            return;
        File f = new File(virtual, r.getFilevirtuale());
        if (f.exists())
            f.delete();
        
    }
    
    /**
             * Chiude l'operazione facendo due cose:
             * 1. sblocca il record dell'import impostando attivo a false
             * 2. cancella o rinomina il file sulla dir locale a seconda della scelta di configurazione
             * @param ctx
             */
    public void chiudiOperazione(ActionEvent actionEvent) {
        Map sess = ADFContext.getCurrent().getSessionScope();
        ImpexpBean bean = (ImpexpBean)BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();

        try {
            // controllo il permesso all'export di accettazioni
            if (bean.getOp_type() == 1 || bean.getOp_type()==3)
                    AccessManager.checkPermission("SOExportAccettazioni");
            else
            // controllo il permesso alla cancellazione
            if (bean.getOp_type() == 2)
              AccessManager.checkPermission("SOExportCancellazioni");
            else 
              // controllo il permesso all'export degli appuntamenti (che vale sia per gli appuntamenti sia per le cancellazioni)   
              AccessManager.checkPermission("SOExportAppuntamenti");

            Impexp_SoCnfImpexpViewRow r = ImpexpUtils.getRowAndUNLock( true, this.getAcrossTpscr());

            String path = (String)sess.get("impexp_file");
            File f = null;
            if (path != null)
                f = new File(path);

            if (f != null) {
                //cancellazione file sulla dir fisica locale oppure rinomina del file
                if(r.getCancellazione()!=null && r.getCancellazione().intValue() == 1) {
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

        } catch (Exception ex) {
                this.handleException(ex.getMessage(), null);
        }
    }
}
