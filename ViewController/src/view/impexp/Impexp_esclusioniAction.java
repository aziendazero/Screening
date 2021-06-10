package view.impexp;

import insiel.dataHandling.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import javax.xml.rpc.ParameterMode;

import model.common.ImpExp_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationReader;

import model.datacontrol.Cnf_selectionBean;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import noNamespace.InfoFlusso;
import noNamespace.RichiestaFlussoDocument;
import noNamespace.RispostaFlussoDocument;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.RowSet;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.xmlbeans.XmlOptions;

import view.thread.ImportEsclusioni;

public class Impexp_esclusioniAction extends Impexp_dataForwardAction {
    
    String tipo_flusso;
      String elaborato;
      Integer anno;
      Integer fase;
    private RichTable ulssTable;

    public Impexp_esclusioniAction() {
        super();
    }

    @Override
    protected String getRelatedPermission() {
        return "SOImportEsclusioni";
    }

    @Override
    protected boolean getAcrossTpscr() {
        return true;
    }

    @Override
    protected String doImport(HashMap parameters, Integer[] rc) throws Exception {
        // TODO Implement this method
        return null;
    }

    @Override
    protected String getLogTitle() {
        // TODO Implement this method
        return null;
    }

    @Override
    protected File searchForFile(Impexp_SoCnfImpexpViewRow r) throws Exception {
        // TODO Implement this method
        return null;
    }

    @Override
    public String getImpExp() {
        // TODO Implement this method
        return null;
    }
    
    public void findForward() throws Exception
      {
        ImpExp_AppModule am=(ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("impexp_modalita", new Integer(1));
        session.put("viewlog_disabled",Boolean.TRUE);
        session.put("eleb_escl_view",Boolean.FALSE);
        ImpexpUtils.initializeLockState(am);
   
        resetForm(am);
        
        // filtro su elaborazioni completate  
        String ulss=(String)session.get("ulss");
        ViewObject voCoda=am.findViewObject("Impexp_ElabEsclusioniView1");
        voCoda.setWhereClause("ULSS='"+ulss+"'"); 
        voCoda.executeQuery();
        
      }
    
    public void resetForm(ImpExp_AppModule am)
    {
      tipo_flusso = "";
      elaborato = "N";
        
      Calendar iCal = Calendar.getInstance();
      iCal.setTime(new java.util.Date()); 
      int y = iCal.get(Calendar.YEAR);
        
      anno = new Integer(y);
      fase = null;
      
      ViewObject vo = am.findViewObject("Impexp_Sdosps_ElencoFlussiView1");
      vo.setWhereClause("1=2");
      vo.executeQuery();
        
    }

    public void setTipo_flusso(String tipo_flusso) {
        this.tipo_flusso = tipo_flusso;
    }

    public String getTipo_flusso() {
        return tipo_flusso;
    }

    public void setElaborato(String elaborato) {
        this.elaborato = elaborato;
    }

    public String getElaborato() {
        return elaborato;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public Integer getAnno() {
        return anno;
    }

    public void setFase(Integer fase) {
        this.fase = fase;
    }

    public Integer getFase() {
        return fase;
    }

    public void cerca(ActionEvent actionEvent) {
        // per prima cosa verifico che l'operazione sia chiusa
        try {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();

            Map session = ADFContext.getCurrent().getSessionScope();
            String tpDip = (String) session.get("impexp_lock_tpdip");

            if (tpDip != null) {
                if (ImpexpUtils.isLockActive()) {
                    throw new Exception("Chiudere l'operazione prima di effettuare una nuova ricerca");
                }
            }       

            String tipo_flusso = this.getTipo_flusso();
            if (tipo_flusso == null || tipo_flusso.equals("")) {
                throw new Exception("E' necessario impostare tipo flusso, stato e anno");
            }

            String elaborato = this.getElaborato();
            if (elaborato == null || elaborato.equals("")) {
                throw new Exception("E' necessario impostare tipo flusso, stato e anno");
            }

            Integer anno = this.getAnno();

            if (anno == null) {
                throw new Exception("E' necessario impostare tipo flusso, stato e anno");
            }

            String ulss = (String) session.get("ulss");
            String user = (String) session.get("user");

            //commentare per eseguire in locale
            //callWSUpdate(((ImpExp_AppModule)vo.getApplicationModule()), ulss, this.getTipo_flusso(), this.getAnno(), this.getFase(), user);

            int esitoQuery = queryElenco();

            DCIteratorBinding voiter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
            ViewObject cnfVo = voiter.getViewObject();
            Impexp_SoCnfImpexpViewRow r = ImpexpUtils.setCurrentCNF(cnfVo, ulss, tipo_flusso);

            session.put("viewlog_disabled", Boolean.FALSE);
            session.put("impexp_lock_tpdip", r.getTpdip());

        } catch (Exception ex) {
            ex.printStackTrace();
           
            svuotaElenco();
            this.handleException(ex.getMessage(), null);
        }
    }

    public void reimposta(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding flussiIter = bindings.findIteratorBinding("Impexp_Sdosps_ElencoFlussiView1Iterator");
        ViewObject vo = flussiIter.getViewObject();
        
        resetForm((ImpExp_AppModule)vo.getApplicationModule());
    }
    
    public void callWSUpdate(ImpExp_AppModule am, String ulssFlusso, String tipoFlusso, Integer annoFlusso, Integer faseFlusso,
                             String user) throws Exception {
        RispostaFlussoDocument.RispostaFlusso risposta = getFlusso(ulssFlusso, tipoFlusso, annoFlusso, faseFlusso);

        String esito = risposta.getEsito();

        if (esito.equals("OK")) {
            

            String whcl =
                "ULSS = '" + ulssFlusso + "' and TIPO_FLUSSO = '" + tipoFlusso + "' and ANNO = " +
                annoFlusso.toString() + " and STATO = 'D'";

            if (faseFlusso != null)
                whcl += " and FASE = " + faseFlusso.toString();

            String delete = "delete from SO_SDOSPS_FLUSSO where " + whcl;

            am.getTransaction().executeCommand(delete);
            am.getTransaction().commit();

            InfoFlusso[] infoArray = risposta.getInfoflArray();

            int len = infoArray.length;

            for (int i = 0; i < len; i++) {
                InfoFlusso info = infoArray[i];
                String ulss = info.getUlssre();
                String tipoFl = info.getTipfls();
                String anno = info.getAnno();
                String fase = info.getIdfase();
                String dataPr = info.getDatapr();

                // controllare che non sia gia' in tabella
                String whQuery =
                    "ULSS = '" + ulss + "' and TIPO_FLUSSO = '" + tipoFl + "' and ANNO = " + anno + " and FASE = " +
                    fase;

                String query = "select count(*) from SO_SDOSPS_FLUSSO where " + whQuery;

                ViewObject voCheck = am.createViewObjectFromQueryStmt("", query);
                voCheck.setRangeSize(-1);
                voCheck.executeQuery();

                Number cnt = (Number) voCheck.first().getAttribute(0);

                if (cnt.intValue() > 0) {
                    // la riga c'e' gia' in tabella (scaricata, elaborato o annullata) quindi non
                    // ha senso che la inserisco
                    // valutare se dare un errore
                    continue;

                }

                String insert =
                    "insert into SO_SDOSPS_FLUSSO values " + "(SO_SDOSPS_FLUSSO_SEQ.nextval,'" + ulss + "','" + tipoFl +
                    "'," + anno + "," + fase + ",'D',to_date('" + dataPr + "','yyyymmdd'),to_date(null)," +
                    "to_date(null),sysdate,'" + user + "',sysdate,'" + user + "')";

                am.getTransaction().executeCommand(insert);

            }

            am.getTransaction().commit();

        }

        else {
            String coderr = risposta.getErrore().getCodice();

            String codeNotFound = ConfigurationReader.readProperty("sdoNotFound");

            // se l'errore e' 'non ho trovato dati' non devo dare eccezioni
            if (coderr.equals(codeNotFound))
                return;

            String deserr = risposta.getErrore().getDescrizione();

            String msg = "errore nel reperimento dei flussi disponibili: " + coderr + " - " + deserr;

            throw new Exception(msg);

        }

    }

    private int queryElenco() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding flussiIter = bindings.findIteratorBinding("Impexp_Sdosps_ElencoFlussiView1Iterator");
        ViewObject vo = flussiIter.getViewObject();

        String whcl = "ULSS = '" + ulss + "'";
        whcl += " AND TIPO_FLUSSO = '" + tipo_flusso + "'";
        whcl += " AND ANNO = " + anno;

        if (elaborato.equals("N"))
            whcl += " AND STATO in ('D','S')";
        else
            whcl += " AND STATO in ('E','A')";

        if (fase != null)
            whcl += " AND FASE = " + fase;

        vo.setWhereClause(whcl);

        if (elaborato.equals("N"))
            vo.setOrderByClause("STATO DESC,ANNO,FASE");
        else
            vo.setOrderByClause("ANNO DESC,FASE DESC");

        vo.executeQuery();

        return 1;

    }

    private void svuotaElenco() {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding flussiIter = bindings.findIteratorBinding("Impexp_Sdosps_ElencoFlussiView1Iterator");

        ViewObject vo = flussiIter.getViewObject();

        vo.setWhereClause("1=2");

        vo.executeQuery();

    }

    private RispostaFlussoDocument.RispostaFlusso getFlusso(String ulssFlusso, String tipoFlusso, Integer annoFlusso,
                                                            Integer faseFlusso) throws Exception {
        RichiestaFlussoDocument richiestaDoc = RichiestaFlussoDocument.Factory.newInstance();
        RichiestaFlussoDocument.RichiestaFlusso richiesta =
            RichiestaFlussoDocument.RichiestaFlusso.Factory.newInstance();

        richiesta.setUlssre(ulssFlusso);
        richiesta.setTipfls(tipoFlusso);
        richiesta.setAnno(annoFlusso.toString());

        if (faseFlusso == null)
            richiesta.setIdfase("");
        else
            richiesta.setIdfase(faseFlusso.toString());

        richiesta.setModo("RL");

        richiestaDoc.setRichiestaFlusso(richiesta);

        String req = getRichiestaXML(richiestaDoc);

        //SSLUtilities.trustAllHostnames();
        //SSLUtilities.trustAllHttpsCertificates();

        //String endpoint = "https://192.168.47.130/pa/services/Screening";
        String endpoint = ConfigurationReader.readProperty("sdoEndpoint");

        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(new java.net.URL(endpoint));
        call.setOperationName("getFlusso");
        call.addParameter("mes", XMLType.XSD_STRING, ParameterMode.IN);
        call.setReturnType(XMLType.XSD_STRING);

        String ret = (String) call.invoke(new Object[] { req });

        RispostaFlussoDocument rispostaDoc = RispostaFlussoDocument.Factory.parse(ret);

        RispostaFlussoDocument.RispostaFlusso risposta = rispostaDoc.getRispostaFlusso();

        return risposta;

    }
    
    private String getRichiestaXML(RichiestaFlussoDocument richiestaDoc) throws Exception
      {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        xmlOptions.setSavePrettyPrintIndent(4);
        xmlOptions.setSaveAggressiveNamespaces();
        xmlOptions.setSaveNamespacesFirst();
        xmlOptions.setUseDefaultNamespace();

        richiestaDoc.save(bo,xmlOptions);
        
        String output = bo.toString();
        
        bo.close();
        
        return output;
      }

    public void scaricaFlusso(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding flussiIter = bindings.findIteratorBinding("Impexp_Sdosps_ElencoFlussiView1Iterator");
        ViewObject flussiVo = flussiIter.getViewObject();
        Row flusso = flussiVo.getCurrentRow();

        String ulssFlusso = (String)flusso.getAttribute("Ulss");
        String tipoFlusso = (String)flusso.getAttribute("TipoFlusso");
        Integer annoFlusso = (Integer)flusso.getAttribute("Anno");
        Integer faseFlusso = (Integer)flusso.getAttribute("Fase");

        try {
            //commentare per testare senza ws
            //ImpexpUtils.setLock(ulssFlusso, tipoFlusso);
            
            // //commentare per eseguire in locale
            //scaricaFlusso(ulssFlusso, tipoFlusso, annoFlusso, faseFlusso);
            visualizzaFlusso((ImpExp_AppModule)flussiVo.getApplicationModule(), ulssFlusso, tipoFlusso, annoFlusso, faseFlusso);

        } catch (Exception ex) {
            String msg = ex.getMessage();
            //ex.printStackTrace();
            this.handleException(ex.getMessage(), null);
        }
    }
    
    private void visualizzaFlusso(ImpExp_AppModule am, String ulssFlusso,String tipoFlusso, Integer annoFlusso, Integer faseFlusso) throws Exception
      {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding cnfIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject cnfVo = cnfIter.getViewObject();
        Impexp_SoCnfImpexpViewRow cfg = (Impexp_SoCnfImpexpViewRow) cnfVo.getCurrentRow();

        String dwnDir = ImpexpUtils.getDwnDir(cfg);
        String fName = cfg.getNomefilearr() + ".dat";
        File dwnFile = new File(dwnDir, fName);

        String virtDir = ImpexpUtils.getVirtDir(cfg);
        String dvFName = cfg.getFilevirtuale();    
        File dvFile = new File(virtDir, dvFName);
            
        StreamUtils.copyInputToOutput(new FileInputStream(dwnFile),new FileOutputStream(dvFile));
        
        String carattFlusso = tipoFlusso + " / " + annoFlusso + " / " + faseFlusso;
        
        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("sdosps_caratt_flusso",carattFlusso);
        
        ViewObject voElab = am.findViewObject("Impexp_SdospsDaextView1");
        ViewObject voErog = am.findViewObject("Impexp_SdospsUlssErogView1");
        
        String whcl = "TIPO_FLUSSO = '" + tipoFlusso + "' AND ULSS = '" + ulssFlusso + "'";    
        voElab.setWhereClause(whcl);    
        voElab.executeQuery();
        voErog.setWhereClause(whcl);
        voErog.executeQuery();
        
        // filtro log
        ViewObject vo=am.findViewObject("Impexp_SoLogTransferView1");    
        String wh = "GRUPPO='"+cfg.getTpdip()+"' AND VERSO='IMP' AND ULSS='"+
              cfg.getUlss()+"' AND TPSCR='"+cfg.getTpscr()+"'";    
        vo.setWhereClause(wh);
        vo.setOrderByClause("IDLOG DESC");
        vo.executeQuery();        
        //
        
        // gaion 08/06/2011
        String query="SELECT trim(codts) CODTS, ulss ULSS FROM so_sdosps_daext WHERE ULSS='"+cfg.getUlss()+"'";
          Cnf_selectionBean clauseBean =
              (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();    
            clauseBean.setInClause(query);
            clauseBean.setNote(null); 
        // fine gaion 

        session.put("sdosps_lanciato_import",Boolean.FALSE);
        session.put("eleb_escl_view",Boolean.TRUE);
            
      }

    public void visualizzaFlusso(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding flussiIter = bindings.findIteratorBinding("Impexp_Sdosps_ElencoFlussiView1Iterator");
        ViewObject flussiVo = flussiIter.getViewObject();
        Row flusso = flussiVo.getCurrentRow();

        String ulssFlusso = (String) flusso.getAttribute("Ulss");
        String tipoFlusso = (String) flusso.getAttribute("TipoFlusso");
        Integer annoFlusso = (Integer) flusso.getAttribute("Anno");
        Integer faseFlusso = (Integer) flusso.getAttribute("Fase");

        try {
            // Spostata la gestione del lock nel thread
            DCIteratorBinding voiter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
            ViewObject cnfVo = voiter.getViewObject();
            ImpexpUtils.setCurrentCNF(cnfVo, ulssFlusso, tipoFlusso);
            if (ImpexpUtils.isLockActive()) {
                throw new Exception("Un altro operatore sta effettuando questa operazione. Si consiglia di riprovare piu' tardi.");
            }

            //ImpexpUtils.setFlussoCorrente(ulssFlusso, tipoFlusso, annoFlusso, faseFlusso);
            visualizzaFlusso((ImpExp_AppModule) flussiVo.getApplicationModule(), ulssFlusso, tipoFlusso, annoFlusso,
                             faseFlusso);

        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
        }
    }
    
    public void chiudiOperazione(ActionEvent actionEvent) {
        try{
              AccessManager.checkPermission(this.getRelatedPermission());
              ImpexpUtils.releaseLock(); 
                 
            }catch(Exception ex)
            {
            //  ex.printStackTrace();
              this.handleException(ex.getMessage(),null);
            }
    }

    public void cleanLog(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding logIter = bindings.findIteratorBinding("Impexp_SoLogTransferView1Iterator");
        ViewObject logVo = logIter.getViewObject();

        RowSetIterator rsI = logVo.createRowSetIterator(null);
        while (rsI.hasNext()) {
            rsI.next().remove();
        }
        rsI.closeRowSetIterator();
        
        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        commitBinding.execute();

    }

    public void importEscl(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding cnfIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject cnfVo = cnfIter.getViewObject();
        Impexp_SoCnfImpexpViewRow cfg = (Impexp_SoCnfImpexpViewRow) cnfVo.getCurrentRow();
        String ulss = cfg.getUlss();
        String tpscr = cfg.getTpscr();
        String tpDip = cfg.getTpdip();
        String suffisso = cfg.getSuffisso();
        String tipoFlusso = tpDip.equals("SD") ? "SDO" : "SPS";
        String eroganti = this.getUlssEroganti();
        String dwnDir = ImpexpUtils.getDwnDir(cfg);
        String fName = cfg.getNomefilearr();

        if (eroganti.equals("")) {
            String msg = "Per eseguire l'import e' necessario selezionare almeno una ulss erogante";
            this.handleException(msg, null);
            return;
        }
        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String) session.get("user");

        DCIteratorBinding flussiIter = bindings.findIteratorBinding("Impexp_Sdosps_ElencoFlussiView1Iterator");
        ViewObject flussiVo = flussiIter.getViewObject();
        Row flussoCorrente = flussiVo.getCurrentRow();

        int annoFlusso = ((Integer) flussoCorrente.getAttribute("Anno")).intValue();
        int faseFlusso = ((Integer) flussoCorrente.getAttribute("Fase")).intValue();

        // lancio procedura in modalita' di elaborazione
        ImportEsclusioni t =
            new ImportEsclusioni(ulss, tpscr, tipoFlusso, annoFlusso, faseFlusso, eroganti, user, suffisso, "E", dwnDir,
                                 fName);

        //t.run();
        t.start();

        session.put("sdosps_lanciato_import", Boolean.TRUE);

        this.handleMessages(FacesMessage.SEVERITY_INFO, "Il processo di import per la ulss " + ulss + " e' stato avviato");
    }
    
    private String getUlssEroganti() {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding empIter = bindings.findIteratorBinding("Impexp_SdospsUlssErogView1Iterator");
        RowSet duplicateRowSet = empIter.getViewObject().createRowSet("duplicateRowSet");
        Row[] rowsSelected =  duplicateRowSet.getFilteredRows("Selected", true);
        
        String eroganti = "";
        if (rowsSelected.length > 0) {
            for (Row rw : rowsSelected) {

                String ulss = (String) rw.getAttribute("UlssInviante");

                if (eroganti.length() < 1)
                    eroganti = ulss;
                else
                    eroganti += "," + ulss;

            }
        }
        duplicateRowSet.closeRowSet();

        return eroganti;
    }

    public void setUlssTable(RichTable ulssTable) {
        this.ulssTable = ulssTable;
    }

    public RichTable getUlssTable() {
        return ulssTable;
    }

    public void verifica(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding cnfIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject cnfVo = cnfIter.getViewObject();
        Impexp_SoCnfImpexpViewRow cfg = (Impexp_SoCnfImpexpViewRow) cnfVo.getCurrentRow();
        String ulss = cfg.getUlss();
        String tpscr = cfg.getTpscr();
        String tpDip = cfg.getTpdip();
        String suffisso = cfg.getSuffisso();
        String tipoFlusso = tpDip.equals("SD") ? "SDO" : "SPS";
        String eroganti = this.getUlssEroganti();
        String dwnDir = ImpexpUtils.getDwnDir(cfg);
        String fName = cfg.getNomefilearr() + ".dat";

        if (eroganti.equals("")) {
            String msg = "Per eseguire la verifica import e' necessario selezionare almeno una ulss erogante";
            this.handleException(msg, null);
            return;
        }
        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String) session.get("user");

        DCIteratorBinding flussiIter = bindings.findIteratorBinding("Impexp_Sdosps_ElencoFlussiView1Iterator");
        ViewObject flussiVo = flussiIter.getViewObject();
        Row flussoCorrente = flussiVo.getCurrentRow();

        int annoFlusso = ((Integer) flussoCorrente.getAttribute("Anno")).intValue();
        int faseFlusso = ((Integer) flussoCorrente.getAttribute("Fase")).intValue();

        // lancio procedura in modalita' di controllo
        ImportEsclusioni t =
            new ImportEsclusioni(ulss, tpscr, tipoFlusso, annoFlusso, faseFlusso, eroganti, user, suffisso, "C", dwnDir,
                                 fName);

        //t.run();
        t.start();

       session.put("sdosps_lanciato_import", Boolean.TRUE);

        this.handleMessages(FacesMessage.SEVERITY_INFO, "Il processo di verifica import per la ulss " + ulss + " e' stato avviato");
    }

    public void annullaImport(ActionEvent actionEvent) {
        try

        {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding cnfIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
            ViewObject cnfVo = cnfIter.getViewObject();
            Impexp_SoCnfImpexpViewRow cfg = (Impexp_SoCnfImpexpViewRow) cnfVo.getCurrentRow();
            String ulss = cfg.getUlss();
            String tpscr = cfg.getTpscr();
            String tpDip = cfg.getTpdip();
            String tipoFlusso = tpDip.equals("SD") ? "SDO" : "SPS";
            Map session = ADFContext.getCurrent().getSessionScope();
            String user = (String)session.get("user");
            String dwnDir = ImpexpUtils.getDwnDir(cfg);
            String fName = cfg.getNomefilearr();

            DCIteratorBinding flussiIter = bindings.findIteratorBinding("Impexp_Sdosps_ElencoFlussiView1Iterator");
            ViewObject flussiVo = flussiIter.getViewObject();
            Row flussoCorrente = flussiVo.getCurrentRow();

            int annoFlusso = ((Integer) flussoCorrente.getAttribute("Anno")).intValue();
            int faseFlusso = ((Integer) flussoCorrente.getAttribute("Fase")).intValue();
            
            ImpExp_AppModule am=(ImpExp_AppModule)flussiVo.getApplicationModule();

            String update =
                "UPDATE SO_CNF_IMPEXP SET DTIMPORT=sysdate " + "WHERE TPDIP='" + tpDip + "' AND " +
                "IMPEXP='IMP' AND " + "ULSS='" + ulss + "' ";

            am.getTransaction().executeCommand(update);

            String updateFlusso =
                "update SO_SDOSPS_FLUSSO set STATO = 'A', DATA_ELABORAZIONE = sysdate, " + "USER_MODIFICA = '" + user +
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

            am.log(ulss, tpDip, "IMP",
                   "IMPORT FLUSSO " + tipoFlusso + " per l'azienda sanitaria " + ulss + " annullato.", tpscr);

            ImpexpUtils.releaseLock();
            flussiVo.executeQuery();
            
            session.put("eleb_escl_view",Boolean.FALSE);

        } catch (Exception ex) {
            String msg = ex.getMessage();
            ex.printStackTrace();
            this.handleException(ex.getMessage(), null);

        }
    }
}
