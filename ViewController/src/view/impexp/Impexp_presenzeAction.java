package view.impexp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.PrintWriter;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.Scanner;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.servlet.ServletContext;

import model.bean.ImpexpFile;

import model.common.ImpExp_AppModule;

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

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.AttributeDef;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.event.ReturnEvent;
import org.apache.myfaces.trinidad.model.UploadedFile;

public class Impexp_presenzeAction extends Impexp_dataForwardAction {
    private RichTable _tableOld;
    private RichTable _table;
    private UploadedFile _file;

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
        return "IMPORT PRESENZE";
    }

    /**
     * Esegue l'operazione di import corretta
     * @return
     */
    protected String doImport(HashMap parameters, Integer[] rc) throws Exception {
        ImpExp_AppModule am = (ImpExp_AppModule) parameters.get("appmodule");
        if (am == null)
            throw new Exception("Import delle presenze impossibile: nessuna istanza dell'application module fornita");
        String ulss = (String) parameters.get("ulss");
        String tpscr = (String) parameters.get("tpscr");
        Integer n = (Integer) parameters.get("centro");
        BigDecimal centro = null;
        if (n != null)
            centro = new BigDecimal(n.doubleValue());

        //la chiamata e' unica, l'am cambia a seconda dello screening e del dipartimentale
        String msg = am.callImportPresenze(ulss, tpscr, centro, (String) parameters.get("tpdip"), rc);

        return msg;
    }

    public static String getServerTime() {
        SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return DATE_TIME_FORMATTER.format(new java.util.Date());
    }

    /**
     * Restituisce il nome della permission da controllare prima di eseguire
     * qualsiasi operazione
     * @return
     */
    protected String getRelatedPermission() {
        return "SOImportPresenze";
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
        }    
        else {
            treeData = (JUCtrlHierBinding)bindings.get("ImpexpFile");
            tab = _table;
        }

        DCIteratorBinding _tableIteratorBinding = treeData.getDCIteratorBinding();
        Object _selectedRowData = tab.getSelectedRowData();
        JUCtrlHierNodeBinding _nodeBinding = (JUCtrlHierNodeBinding) _selectedRowData;
        Key _rwKey = _nodeBinding.getRowKey();
        _tableIteratorBinding.setCurrentRowWithKey(_rwKey.toStringFormat(true));

        DCDataRow currentRow = (DCDataRow) treeData.getCurrentRow();
        String viewName = (String) currentRow.getAttribute("viewName");
        ImpexpFile fo = (ImpexpFile) currentRow.getDataProvider();
        selection = fo.getTxtFile();
        try {
            AccessManager.checkPermission(this.getRelatedPermission());

            if (selection == null)
                throw new Exception("Nessun file selezionato per l'operazione");

            session.put("impexp_file", selection.getAbsolutePath());
            session.put("impexp_file_name", selection.getName());//AG
            Impexp_SoCnfImpexpViewRow r;
            String name = selection.getName();
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
            session.put("impexp_presenze_mode", "dati");

            //copio il file in modo che sia accessibile tramite db
            ImpexpUtils.copyToDirVirtuale(r, selection, (ImpExp_AppModule)vo.getApplicationModule());

            //ripulisco l'eventuale simbolo di ordinamento riumasto dall'accesso precedente
            String vo_name = "Impexp_SoImportPresenzeView1";
            DCIteratorBinding voiter = bindings.findIteratorBinding(vo_name + "Iterator");
            voiter.applySortCriteria(null);
            this.fillTable(null, (ImpExp_AppModule)vo.getApplicationModule());

            String query =
                "SELECT trim(codts) CODTS, ulss ULSS FROM so_imp_presenze WHERE ULSS='" + ulss + "' AND TPSCR = '" +
                tpscr + "'";

            Cnf_selectionBean clauseBean =
                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            clauseBean.setInClause(query);
            clauseBean.setNote(null);
            
            //I00095792: filtro i record dei log di import per ulss, tpscr, impexp, tpdip e name (nome del file selezionato)
            String impExp = r.getImpexp();
            String tpdip = r.getTpdip();
            
            DCIteratorBinding cnfLogIter = bindings.findIteratorBinding("Impexp_SoCnfImpExpLogView1Iterator");
            ViewObject voLog = cnfLogIter.getViewObject();
            
            if (name.endsWith("_IMPORTED"))
                name = name.substring(0, name.indexOf("_IMPORTED"));
            
            String where = "ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND " +
                "IMPEXP='" + impExp + "' AND TPDIP='" + tpdip + "' AND " +
                "NOMEFILEIMPORT='" + name + "'";

            voLog.setWhereClause(where);
            voLog.setOrderByClause("DTFINEELAB DESC");
            
            voLog.executeQuery();
            
        } catch (Exception ex) {
            super.handleException(ex.getMessage(), null);
        }
    }


    public void filter(ActionEvent actionEvent) {
        try {
            this.filterFiles();
        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
        }
    }

    /**
     * qUESTO METODO RIEMPIE LA TABELLA DELLE PRESENZE.
     * @param orderBy
     * @param
     */
    private void fillTable(String orderBy, ImpExp_AppModule am) {
  
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");


        //copio i dati dalla tabella del db ad un viewobject ad_hoc
        String query = "SELECT * FROM so_imp_presenze WHERE ULSS='" + ulss + "' AND TPSCR = '" + tpscr + "'";
        String vo_name = "Impexp_SoImportPresenzeView1";

        if (orderBy != null)
            query += " ORDER BY " + orderBy;


        ViewObject presenze = am.createViewObjectFromQueryStmt("PresenzeImportateView", query);
        ViewObject display = am.findViewObject(vo_name);
        try {
            ViewHelper.clearVO(display);

            presenze.last();
            Row record;
            while ((record = presenze.getCurrentRow()) != null) {
                Row ref = display.createRow();
                display.insertRow(ref);
                //impostazione di tutti i campi
                AttributeDef[] atts = display.getAttributeDefs();
                for (int i = 0; i < atts.length; i++) {
                    ref.setAttribute(atts[i].getName(), record.getAttribute(atts[i].getColumnName()));
                    ;
                }
                presenze.previous();
            }
        } finally {
            if (presenze != null)
                presenze.remove();
        }
    }

    public String getImpExp() {
        return ConfigurationConstants.IMPEXP_IMP;
    }

    public void findForward() {
        
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ImpexpBean bean =
            (ImpexpBean) BindingContext.getCurrent().findDataControl("ImpexpBeanDataControl").getDataProvider();
        
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        try {

            session.put("renderChiudiOp", Boolean.TRUE);

            ViewObject tp = am.findViewObject("Impexp_SoCnfImpexpTpdipView1");
            String wh =
                "IMPEXP='" + ConfigurationConstants.IMPEXP_IMP + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr +
                "' AND MODALITA=1";

            //in caso di utenti con restrizioni di centro filtro ulteriormente
            String in = (String) session.get("elenco_centri");
            if (in != null) {
                wh +=
                    " AND IDCENTROREF IN (" + "SELECT R.idcentroref FROM SO_CENTRO_PREL_REF R " + "WHERE idcentro in " +
                    in + ")";
            }

            wh += " AND TPDIP='" + ConfigurationConstants.TPDIP_ACCETTAZIONE + "' ";

            tp.setWhereClause(wh);
            tp.executeQuery();
            
            //System.out.println(tp.getQuery());

            Row x = tp.first();
            if (x == null)
                throw new Exception("Nessuna configurazione trovata");

            bean.setTpdip((String) x.getAttribute("Tpdip"));

            //filtro anche i centri utilizzati nelle configurazioni
            ViewObject vo = am.findViewObject("Impexp_SoCnfCentriImpexpView1");

            vo.setWhereClause(wh + " AND TPDIP='" + bean.getTpdip() + "'");
            vo.setWhereClause(wh);
            vo.executeQuery();


            //ed imposto i dati del bean sul primo centro ch etrovo
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
            vo.setWhereClause(wh + " AND TPDIP='" + bean.getTpdip() + "'");
            vo.executeQuery();

            Impexp_dataForwardAction.selectCentro(am,bean);

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
            ex.printStackTrace();
            this.handleException(ex.getMessage());
        } finally {
            //super.findForward(ctx);
        }
    }

    public void importPresenze(ActionEvent actionEvent) {
        super.onImport();
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
        
        
    public void handlePopupReturn(ReturnEvent event)
    {  
        AdfFacesContext.getCurrentInstance().addPartialTarget(_table); 
        AdfFacesContext.getCurrentInstance().addPartialTarget(_tableOld); 
      if (event.getReturnValue() != null)
      {
          this.handleMessages(FacesMessage.SEVERITY_INFO,
                              "Upload eseguito con successo del file "+(String)event.getReturnValue());
      }
    }
    
    public void setFile(UploadedFile _file) {
        this._file = _file;
    }

    public UploadedFile getFile() {
        return _file;
    }
    
    public String insertFile() {
        UploadedFile fileUploaded = this.getFile();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String user = (String) session.get("user");      
        AdfFacesContext afc = AdfFacesContext.getCurrentInstance();
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
        Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();
        String dirPath = r.getPosizfilearr();
        String nomeFile = r.getNomefilearr();
        DateFormat dateFormat = new SimpleDateFormat(ConfigurationConstants.IMPEXP_DATE_PATTERN);
        String strDate = dateFormat.format(new Date());
        nomeFile += strDate;
        
        String suffissoCentro = r.getSuffisso();
        if (suffissoCentro != null && suffissoCentro.length() > 0) {
           nomeFile +=  suffissoCentro;
        }

        //save on server     
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileUploaded.getInputStream()));
            PrintWriter writer = new PrintWriter(
                  new BufferedOutputStream(new FileOutputStream(dirPath + "/" +nomeFile)));
            String str;
            while ((str = reader.readLine()) != null) {
                str = str.replace("\r\n", ""); // replace string sequence
                writer.println(str);
            }
            writer.close();
            reader.close();
            
            //ricarico i file
            this.filterFiles();
            
            //registro il log      
            ViewObject logVo = am.findViewObject("Impexp_SoLogTransferView1");
            Row row = logVo.createRow();
            row.setAttribute("Gruppo", "AC");
            row.setAttribute("Verso", "IMP");
            row.setAttribute("Ulss", ulss);
            row.setAttribute("Tpscr", tpscr);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            row.setAttribute("Data", timestamp);
            row.setAttribute("Descrizione", "L'utente "+user+ " ha caricato il file "+fileUploaded.getFilename()+" rinominato in "+nomeFile);
            logVo.insertRow(row);
        }catch (Exception e){
            e.printStackTrace(); 
            
            //registro il log      
            ViewObject logVo = am.findViewObject("Impexp_SoLogTransferView1");
            Row row = logVo.createRow();
            row.setAttribute("Gruppo", "AC");
            row.setAttribute("Verso", "IMP");
            row.setAttribute("Ulss", ulss);
            row.setAttribute("Tpscr", tpscr);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            row.setAttribute("Data", timestamp);
            row.setAttribute("Descrizione", "Caricamento del file "+nomeFile+" da parte dell'utente "+user+" fallito per "+e.getMessage());
            logVo.insertRow(row);
        }
        am.getTransaction().commit();    
        afc.returnFromDialog(nomeFile, null);
        return null;
    }
}
