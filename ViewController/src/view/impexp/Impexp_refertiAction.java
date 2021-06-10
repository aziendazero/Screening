package view.impexp;

import java.io.File;
import java.io.FileNotFoundException;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.bean.ImpexpFile;

import model.common.ImpExp_AppModule;
import model.common.Impexp_SoImportRefertiViewRow;
import model.common.RefCo_AppModule;
import model.common.RefMa_AppModule;
import model.common.Ref_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.ImpexpBean;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.bean.DCDataRow;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.output.RichOutputText;

import oracle.jbo.ApplicationModule;
import oracle.jbo.AttributeDef;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import view.referto.GestoreReferti;

public class Impexp_refertiAction extends Impexp_dataForwardAction {

    private RichOutputText fileName;
    private RichTable _tableOld;
    private RichTable _table;
    private RichOutputText filenameOld;

    /**
     * Sovrascrive il metodo della superclasse per impedire la navigazione
     * in caso di operazione non chiusa
     * @param
     */
    public void beforeNavigate(String dest) throws Exception {

        Map session = ADFContext.getCurrent().getSessionScope();
        //controllo che l'utente abbia chiuso l'operazione
        Boolean myLock = (Boolean) session.get("impexp_lock");
        if (myLock != null && myLock.booleanValue())
            throw new Exception("Impossible navigare: l'operazione non risulta chiusa.");

    }

    protected File searchForFile(Impexp_SoCnfImpexpViewRow r) throws FileNotFoundException {
        //non voglio copiare nuovamete il file perche' e' gia' stato copiato
        //da un altro metodo su richiesta dell'utente
        return null;
    }

    protected boolean getAcrossTpscr() {
        //l'import dei referti vale solo all'interno di uno screening
        return false;
    }

    /**
     * @return titolo del report di log
     */
    protected String getLogTitle() {
        return "IMPORT REFERTI";
    }

    /**
     * Esegue l'operazione di import corretta
     * @return
     */
    protected String doImport(HashMap parameters, Integer[] rc) throws Exception {
        ImpExp_AppModule am = (ImpExp_AppModule) parameters.get("appmodule");
        if (am == null)
            throw new Exception("Import dei referti impossibile: nessuna istanza dell'application module fornita");
        String ulss = (String) parameters.get("ulss");
        String tpscr = (String) parameters.get("tpscr");
        String tpdip = (String) parameters.get("tpdip");
        Integer n = (Integer) parameters.get("centro");
        BigDecimal centro = null;
        if (n != null)
            centro = new BigDecimal(n.doubleValue());
        //la chiamata e' unica, l'am cambia a seconda dello screening e del dipartimentale

        String msg = am.callImportReferti(ulss, tpscr, centro, tpdip, rc);

        if (rc[0].intValue() == -1)
            return msg;

        //19012012 S.Gaion: i referti di 2 livello vengono importati aperti e non va creata la lettera
        if (!ConfigurationConstants.TPDIP_RADIO2.equals(tpdip) && !ConfigurationConstants.TPDIP_ANAPAT2.equals(tpdip)) {
            if (msg == null)
                msg = this.createLetters(ulss, tpscr, tpdip, am);
            else
                msg += this.createLetters(ulss, tpscr, tpdip, am);
        }

        return msg;
    }

    public static String getServerTime() {
        SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return DATE_TIME_FORMATTER.format(new java.util.Date());
    }

    /**
     * Metodo che legge i referti che l'import ha importato/aggiornato e va a
     * creare le relative lettere
     * @param am
     * @param tpscr
     * @param ulss
     */
    public static String createLetters(String ulss, String tpscr, String tpdip, ImpExp_AppModule am) {
        /*
     * mauro 09/09/2010, inseriti messaggi di logging per problema mancata produzione
     * allegati
     */

        System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr + "/" + getServerTime() +
                           "/inizio metodo");

        ViewObject vo = am.findViewObject("Impexp_SoImpexpRefertiView1");
        vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND TPDIP='" + tpdip + "'");
        vo.executeQuery();

        ApplicationModule r_am =
            am.findApplicationModule((String) ViewHelper.decodeByTpscr(tpscr, "Ref_AppModule1", "RefCo_AppModule1",
                                                                       "RefMa_AppModule1", null, null));

        GestoreReferti gr = null;
        if ("CI".equals(tpscr))
            gr = new GestoreReferti((Ref_AppModule) r_am);
        if ("CO".equals(tpscr))
            gr = new GestoreReferti((RefCo_AppModule) r_am);
        if ("MA".equals(tpscr))
            gr = new GestoreReferti((RefMa_AppModule) r_am);


        ViewObject referti =
            r_am.findViewObject((String) ViewHelper.decodeByTpscr(tpscr, "Ref_SoRefertocito1livView1",
                                                                  "Ref_SoRefertocolon1livView2",
                                                                  "Ref_SoRefertomammo1livView2", null, null));
        String out = null;
        Row[] result = vo.getFilteredRows("Ulss", ulss);
        for (int i = 0; i < result.length; i++) {
            System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr + "/" + getServerTime() +
                               "/inizio iterazione");

            Row r = result[i];

            try {
                referti.setWhereClause("IDREFERTO=" + r.getAttribute("Idreferto"));
                referti.executeQuery();
                Row ref = referti.first();
                if (ref == null)
                    throw new Exception("Nessun referto con id=" + r.getAttribute("Idreferto"));

                //MODIFICA 20081015
                //PRIMA DI VEDERE SE DEVO GENERARE LA LETTERA, CONTROLLO CHE SIA COMPLETO
                if (ConfigurationConstants.DB_FALSE.equals(ref.getAttribute("Completo")))
                    continue;

                // altrimenti, se c'e' un allegato devo cancellarlo
                if (ref.getAttribute("Idallegato") != null)
                    gr.deleteLettera(ref);

                //infine creo la nuova lettera
                if ("CI".equals(tpscr))
                    gr.creaLetteraDiRefertoCI(ref, null, null, 1, null);
                else
                    gr.creaLetteraDiReferto(ref, 1, null);


                //e salvo il tutto
                r_am.getTransaction().commit();
                System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr + "/" + getServerTime() +
                                   "/fine iterazione");

            } catch (Exception ex) {
                System.out.println("Import referti/creazione lettere/" + ulss + "/" + tpscr + "/" + getServerTime() +
                                   "/eccezione");
                ex.printStackTrace();
                r_am.getTransaction().rollback();
                //  ex.printStackTrace();
                String s =
                    "Impossibile generare la lettera per il referto " + r.getAttribute("Idreferto") + ": " +
                    ex.getMessage() + "\n";
                if (out == null)
                    out = s;
                else
                    out += s;
            }


        }

        return out;
    }

    /**
     * Restituisce il nome dell apermission da controllare prima di eseguire
     * qualsiasi operazione
     * @return
     */
    protected String getRelatedPermission() {
        return "SOImportReferti";
    }

    public void filter(ActionEvent actionEvent) {
        try {
            this.filterFiles();
        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
        }
    }


    /**
     * qUESTO METODO RIEMPIE LA TABELLA DEI REFERTI PER IL CITO.
     * @param orderBy
     * @param
     */
    private void fillTable(String orderBy, String tpdip, ImpExp_AppModule am) {
     
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");


        //copio i dati dalla tabella del db ad un viewobject ad_hoc
        String query = null;
        String vo_name = null;
        if ("CI".equals(tpscr)) {
            if (ConfigurationConstants.TPDIP_ANAPAT.equals(tpdip)) {
                query = "SELECT * FROM so_referticito WHERE ULSS='" + ulss + "'";
                vo_name = "Impexp_SoImportRefertiView1";
            } else if (ConfigurationConstants.TPDIP_LAB.equals(tpdip)) {
                query = "SELECT * FROM so_referticito_hpv WHERE ULSS='" + ulss + "'";
                vo_name = "Impexp_SoImportRefertiHPVView1";
            }


        } else if ("CO".equals(tpscr)) {
            if (ConfigurationConstants.TPDIP_LAB.equals(tpdip)) {
                query =
                    "select * from (select a1.progr,\n" + "a1.idrichiesta, \n" + "a3.idcampione,\n" + "a1.cognome, \n" +
                    "a1.nome, \n" + "TO_CHAR(to_date(a2.datanasc,'yyyy-MM-dd'),'dd/MM/yyyy') datanasc,\n" +
                    "a1.sesso, \n" + "a2.codts, \n" + "a2.codanag, \n" + "a3.codfisc, \n" + "a3.codcomnasc,\n" +
                    "TO_CHAR(to_date(a1.dataacc,'yyyy-MM-dd'),'dd/MM/yyyy') dataacc, \n" + "a2.codric, \n" +
                    "a1.richap,\n" + "a3.codregtar,  \n" + "a3.codprov,\n" + "a3.codservdia, \n" + "a3.codmotivo, \n" +
                    "r1.nrecord n_esame,\n" + "r1.codanalisi codice_prestazione1, \n" +
                    "r1.codesito codice_esito1, \n" + "r1.valore esito_qualitativo,\n" +
                    "r12.codanalisi codice_prestazione2,\n" + "r12.codesito codice_esito2, \n" +
                    "r12.valore quantita\n" + "from so_refcolon_met_a1 a1, so_refcolon_met_a2 a2,\n" +
                    "so_refcolon_met_a3 a3, so_refcolon_met_r1 r1,so_refcolon_met_r1 r12\n" +
                    "where a1.idrichiesta=a2.idrichiesta\n" + "and a1.progr=a3.progr\n" +
                    "and (r1.progr(+)=a1.progr and r1.tiporis(+)='T')\n" +
                    "and (r12.progr(+)=a1.progr and r12.tiporis(+)='N')\n" +
                    "and (r1.nrecord is null or r12.nrecord is null or r1.nrecord=r12.nrecord)\n" + "and a1.ulss='" +
                    ulss + "' and a2.ulss='" + ulss + "' and a3.ulss='" + ulss + "'\n" + "and r1.ulss(+)='" + ulss +
                    "' and r12.ulss(+)='" + ulss + "')";

                vo_name = "Impexp_SoImportRefertiCOView1";

            }

        } else if ("MA".equals(tpscr)) {
            if (ConfigurationConstants.TPDIP_RADIO.equals(tpdip)) {
                query = "SELECT * FROM so_refertimammo WHERE ULSS='" + ulss + "'";
                vo_name = "Impexp_SoImportRefertiMAView1";
            }
            // 19012012 S.Gaion: referti 2 livello
            else if (ConfigurationConstants.TPDIP_RADIO2.equals(tpdip)) {
                query = "SELECT * FROM so_refertimammo_r2 WHERE ULSS='" + ulss + "'";
                vo_name = "Impexp_SoImportRefertiR2View1";
            } else if (ConfigurationConstants.TPDIP_ANAPAT2.equals(tpdip)) {
                query = "SELECT * FROM so_refertimammo_a2 WHERE ULSS='" + ulss + "'";
                vo_name = "Impexp_SoImportRefertiA2View1";
            }
        }


        if (orderBy != null)
            query += " ORDER BY " + orderBy;


        ViewObject referti = am.createViewObjectFromQueryStmt("RefertiImportatiView", query);
        ViewObject display = am.findViewObject(vo_name);
        try {
            ViewHelper.clearVO(display);

            // referti.first();
            referti.last();
            Row record;
            while ((record = referti.getCurrentRow()) != null) {
                //riempio la tabella per il citologico
                if ("CI".equals(tpscr) && ConfigurationConstants.TPDIP_ANAPAT.equals(tpdip)) {
                    Impexp_SoImportRefertiViewRow ref = (Impexp_SoImportRefertiViewRow) display.createRow();
                    display.insertRow(ref);


                    ref.setAnnoAcc((String) record.getAttribute("ANNO"));
                    ref.setCodts((String) record.getAttribute("NUMLIB"));
                    ref.setCognomeNome((String) record.getAttribute("CONOME"));

                    ref.setLettore((String) record.getAttribute("LETTO1"));
                    ref.setFirma((String) record.getAttribute("FIRMA"));
                    // ref.setSnomo1((String)record.getAttribute("SNOMO1"));
                    ref.setSnomo2((String) record.getAttribute("SNOMO2"));
                    ref.setSnotop((String) record.getAttribute("SNOTOP"));
                    ref.setTipoAcc((String) record.getAttribute("TIPO"));
                    //formattazione date
                    String d = (String) record.getAttribute("DATNAS");
                    if (d == null || d.length() == 0)
                        ref.setDatanas(null);
                    else {
                        d = d.substring(0, 2) + "/" + d.substring(2, 4) + "/" + d.substring(4);
                        ref.setDatanas(d);
                    }

                    d = (String) record.getAttribute("DATREF");
                    if (d == null || d.length() == 0)
                        ref.setDataReferto(null);
                    else {
                        d = d.substring(0, 2) + "/" + d.substring(2, 4) + "/" + d.substring(4);
                        ref.setDataReferto(d);
                    }

                }
 
                else //METODO GENERICO
                {
                    Row ref = display.createRow();
                    display.insertRow(ref);
                    //impostazione di tutti i campi
                    AttributeDef[] atts = display.getAttributeDefs();
                    for (int i = 0; i < atts.length; i++) {
                        // System.out.println(atts[i].getName()+"-"+atts[i].getColumnName());
                        ref.setAttribute(atts[i].getName(), record.getAttribute(atts[i].getColumnName()));
                        ;
                    }


                }


                //  referti.next();
                referti.previous();
            }
        } finally {
            if (referti != null)
                referti.remove();
        }
    }

    public String getImpExp() {
        // TODO:  Implement this view.impexp.Impexp_dataForwardAction abstract method
        return ConfigurationConstants.IMPEXP_IMP;
    } 

    public void findForward() {

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        try {

            session.put("renderChiudiOp", Boolean.TRUE);
            ImpExp_AppModule am =
                (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
            ImpexpBean bean =
                (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();
            ViewObject tp = am.findViewObject("Impexp_SoCnfImpexpTpdipView1");
            String wh = "IMPEXP='" + ConfigurationConstants.IMPEXP_IMP +
                //il tipo di dipartimentale dipende dallo screening
                "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND MODALITA=1";

            //in caso di utenti con restrizioni di centro filtro ulteriormente
            String in = (String) session.get("elenco_centri");
            if (in != null) {
                wh +=
                    " AND IDCENTROREF IN (" + "SELECT R.idcentroref FROM SO_CENTRO_PREL_REF R " + "WHERE idcentro in " +
                    in + ")";
            }

            wh += " AND TPDIP<>'" + ConfigurationConstants.TPDIP_ANAGRAFE + "' ";
            wh += " AND TPDIP<>'" + ConfigurationConstants.TPDIP_SDO + "' ";
            wh += " AND TPDIP<>'" + ConfigurationConstants.TPDIP_SPS + "' ";
            wh += " AND TPDIP<>'" + ConfigurationConstants.TPDIP_ACCETTAZIONE + "' ";

            tp.setWhereClause(wh);

            tp.executeQuery();

            Row x = tp.first();
            if (x == null)
                throw new Exception("Nessuna configurazione trovata");

            bean.setTpdip((String) x.getAttribute("Tpdip"));

            //filtro anche i centri utilizzati nelle configurazioni
            ViewObject vo = am.findViewObject("Impexp_SoCnfCentriImpexpView1");

            vo.setWhereClause(wh + " AND TPDIP='" + bean.getTpdip() + "'");
            vo.executeQuery();

            //ed imposto i dati del bean sul primo centro che trovo
            Row r2 = vo.first();
            if (r2 != null) {

                bean.setIdcentro((Integer) r2.getAttribute("Idcentroref"));
                bean.setCentro((String) r2.getAttribute("Suffisso"));
                Impexp_dataForwardAction.filterFiles();
            } else {
                bean.setIdcentro(null);
                bean.setCentro(null);
            }

            //filtro sulla configurazione dell'import che mi interessa
            vo = am.findViewObject("Impexp_SoCnfImpexpView1");
            vo.setWhereClause(wh + " AND TPDIP='" + bean.getTpdip() + "' ");
            vo.executeQuery();

            Impexp_dataForwardAction.selectCentro(am, bean);

            Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();

            if (ConfigurationConstants.MODALITA_FTP.equals(r.getModalita())) {
                File dir = new File(r.getPosizfilearr());
                if (!dir.exists())
                    throw new Exception("Directory " + dir.getAbsolutePath() + " non trovata");

                session.put("impexp_localdir", dir.getAbsolutePath());
                session.put("impexp_localfile", r.getNomefilearr());
            }

            //memorizzo la modalita' nella sessione
            session.put("impexp_modalita", r.getModalita());


        } catch (Exception ex) {
            this.handleException(ex.getMessage());
        } finally {

        }
    }

    public void copyToDB(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding cnfIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject vo = cnfIter.getViewObject();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        //il view object contiene tutte le righe per l'import dei referti,
        //devo trovare la riga del centro col suffisso corretto
        File selection = null;
        String type = (String) ADFContext.getCurrent().getRequestScope().get("type");
        JUCtrlHierBinding treeData = null;
        
        RichTable tab = null;
        RichOutputText _filenam = null;
        if ("old".equals(type)){
            treeData = (JUCtrlHierBinding)bindings.get("ImpexpFile1");  
            tab = _tableOld;
            _filenam = filenameOld;
        }    
        else {
            treeData = (JUCtrlHierBinding)bindings.get("ImpexpFile");
            tab = _table;
            _filenam = fileName;
        }
        
        try {
            DCIteratorBinding _tableIteratorBinding = treeData.getDCIteratorBinding();
            Object _selectedRowData = tab.getSelectedRowData();
            JUCtrlHierNodeBinding _nodeBinding = (JUCtrlHierNodeBinding) _selectedRowData;
            Key _rwKey = _nodeBinding.getRowKey();
            _tableIteratorBinding.setCurrentRowWithKey(_rwKey.toStringFormat(true));
            
            DCDataRow currentRow = (DCDataRow) treeData.getCurrentRow();
            ImpexpFile fo = (ImpexpFile) currentRow.getDataProvider();

            if (fo == null)
                throw new Exception("Nessun file selezionato per l'operazione");
            selection = fo.getTxtFile();

            AccessManager.checkPermission(this.getRelatedPermission());

            if (_filenam == null)
                throw new Exception("Nessun file selezionato per l'operazione");

            session.put("impexp_file", selection.getAbsolutePath());
            session.put("impexp_file_name", selection.getName());
            Impexp_SoCnfImpexpViewRow r;
            String name = _filenam.getValue().toString();
            if (name.endsWith("_OLD"))
                name = name.substring(0, name.indexOf("_OLD"));

            ImpexpBean bean =
                (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();

            //cerco la riga di configurazione corretta per il file selezionato
            //e la imposto come riga corrente
            if (bean.getIdcentro() != null) {
                try {

                    Row[] result = vo.getFilteredRows("Idcentroref", bean.getIdcentro());
                    if (result.length == 0)
                        throw new Exception("Nessuna configurazione trovata per il centro selezionato");
                    if (result.length > 1)
                        throw new Exception("Troppe configurazioni trovate per il centro selezionato");
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

            //tento di iniziare l'operazione
            r = ImpexpUtils.getRowAndLock(true, this.getAcrossTpscr(), vo);

            //se il lock e' avvenuto faccio in modo che venga visualizzata solo la tabella
            if (tpscr.equals("MA") || tpscr.equals("CI")) {
                session.put("impexp_referti_mode", "dati");
            }

            //copio il file in modo che sia accessibile tramite db
            ImpexpUtils.copyToDirVirtuale(r, selection, (ImpExp_AppModule) vo.getApplicationModule());

            if (tpscr.equals("CO")) {
                session.put("impexp_referti_mode", "dati");
            }

            //ripulisco l'eventuale simbolo di ordinamento riumasto dall'accesso precedente
            String vo_name = null;
            if ("CI".equals(tpscr) && ConfigurationConstants.TPDIP_ANAPAT.equals(bean.getTpdip()))
                vo_name = "Impexp_SoImportRefertiView1";
            else if ("CI".equals(tpscr) && ConfigurationConstants.TPDIP_LAB.equals(bean.getTpdip()))
                vo_name = "Impexp_SoImportRefertiHPVView1";
            else if ("CO".equals(tpscr) && ConfigurationConstants.TPDIP_LAB.equals(bean.getTpdip()))
                vo_name = "Impexp_SoImportRefertiCOView1";
            else if ("MA".equals(tpscr) && ConfigurationConstants.TPDIP_RADIO.equals(bean.getTpdip()))
                vo_name = "Impexp_SoImportRefertiMAView1";
            else if ("MA".equals(tpscr) && ConfigurationConstants.TPDIP_RADIO2.equals(bean.getTpdip()))
                vo_name = "Impexp_SoImportRefertiR2View1";
            else if ("MA".equals(tpscr) && ConfigurationConstants.TPDIP_ANAPAT2.equals(bean.getTpdip()))
                vo_name = "Impexp_SoImportRefertiA2View1";
            else
                throw new Exception("nessun import previsto per tipo screening=" + tpscr + " e dipartimentale=" +
                                    bean.getTpdip());

            DCIteratorBinding voiter = bindings.findIteratorBinding(vo_name + "Iterator");
            voiter.applySortCriteria(null);
            this.fillTable(null, r.getTpdip(), (ImpExp_AppModule) vo.getApplicationModule());


            String tpdip = r.getTpdip();
            String query = "";

            if ("CI".equals(tpscr)) {
                if (ConfigurationConstants.TPDIP_ANAPAT.equals(tpdip)) {
                    query = "SELECT trim(numlib) CODTS, ulss ULSS FROM so_referticito WHERE ULSS='" + ulss + "'";

                } else if (ConfigurationConstants.TPDIP_LAB.equals(tpdip)) {
                    query = "SELECT trim(tessera) CODTS, ulss ULSS FROM so_referticito_hpv WHERE ULSS='" + ulss + "'";

                }

            } else if ("CO".equals(tpscr)) {
                if (ConfigurationConstants.TPDIP_LAB.equals(tpdip)) {
                    query = "SELECT trim(codts) CODTS, ulss from so_refcolon_met_a2 WHERE ulss='" + ulss + "'";
                }
            } else if ("MA".equals(tpscr)) {
                if (ConfigurationConstants.TPDIP_RADIO.equals(tpdip)) {
                    query = "SELECT trim(tessera) codts, ulss ULSS FROM so_refertimammo WHERE ULSS='" + ulss + "'";
                } else if (ConfigurationConstants.TPDIP_RADIO2.equals(tpdip)) {
                    query = "SELECT trim(tessera) codts, ulss ULSS FROM so_refertimammo_r2 WHERE ULSS='" + ulss + "'";
                } else if (ConfigurationConstants.TPDIP_ANAPAT2.equals(tpdip)) {
                    query = "SELECT trim(tessera) codts, ulss ULSS FROM so_refertimammo_a2 WHERE ULSS='" + ulss + "'";
                }
            }

            Cnf_selectionBean clauseBean =
                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            clauseBean.setInClause(query);
            clauseBean.setNote(null);

        } catch (Exception ex) {
            super.handleException(ex.getMessage(), null);
        }
    }

    public void importReferti(ActionEvent actionEvent) {
        super.onImport();
    }

    public void nuovoCentro(ValueChangeEvent valueChangeEvent) {
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

    public void setFileName(RichOutputText fileName) {
        this.fileName = fileName;
    }

    public RichOutputText getFileName() {
        return fileName;
    }

    public void set_tableOld(RichTable _tableOld) {
        this._tableOld = _tableOld;
    }

    public RichTable get_tableOld() {
        return _tableOld;
    }

    public void set_table(RichTable _table) {
        this._table = _table;
    }

    public RichTable get_table() {
        return _table;
    }

    public void setFilenameOld(RichOutputText filenameOld) {
        this.filenameOld = filenameOld;
    }

    public RichOutputText getFilenameOld() {
        return filenameOld;
    }
}
