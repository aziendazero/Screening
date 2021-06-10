package view.impexp;

import java.io.File;

import java.util.HashMap;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;


import model.common.ImpExp_AppModule;

import model.common.Parent_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;

import model.commons.ConfigurationReader;
import model.commons.SchedUtils;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class Impexp_anagrafeAction extends Impexp_dataForwardAction {
    public Impexp_anagrafeAction() {
        super();
    }

    @Override
    protected String getRelatedPermission() {
        return "SOImportAnagrafe";
    }

    @Override
    protected boolean getAcrossTpscr() {
        //lavoro su tutti gli screening
        //perche' l'anagrafe e' condivisa
        return true;
    }

    @Override
    protected String doImport(HashMap parameters, Integer[] rc) throws Exception {
        ImpExp_AppModule am = (ImpExp_AppModule) parameters.get("appmodule");
        if (am == null)
            throw new Exception("Import dell'anagrafe impossibile: nessuna istanza dell'application module fornita");
        String ulss = (String) parameters.get("ulss");
        Integer modalita = (Integer) parameters.get("modalita");
        return am.callImportAnagrafe(ulss, modalita.intValue());
    }

    @Override
    protected String getLogTitle() {
        return "IMPORT DATI ANAGRAFICI";
    }

    @Override
    protected File searchForFile(Impexp_SoCnfImpexpViewRow r) throws Exception {
        // TODO Implement this method
        return null;
    }

    @Override
    public String getImpExp() {
        return ConfigurationConstants.IMPEXP_IMP;
    }

    public void findForward() {
        ImpExp_AppModule am =
            (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        try {

            Boolean impAnagManuale = (Boolean) session.get("impAnagManuale");
            session.put("renderChiudiOp", impAnagManuale);

            //filtro sulla configurazione dell'import che mi interessa
            ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
            vo.setWhereClause("IMPEXP='" + ConfigurationConstants.IMPEXP_IMP + "' AND TPDIP='" +
                              ConfigurationConstants.TPDIP_ANAGRAFE + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr +
                              "'");
            vo.executeQuery();
            Impexp_dataForwardAction.selectCentro(vo, null);

            Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();
            //memorizzo la modalita' nella sessione
            session.put("impexp_modalita", r.getModalita());

            //filtro sul log
            vo = am.findViewObject("Impexp_SoLogTransferView1");

            if (r != null) {
                vo.setWhereClause("GRUPPO='" + r.getTpdip() + "' AND VERSO='" + ConfigurationConstants.IMPEXP_LOC +
                                  "' AND ULSS='" + r.getUlss() + "' AND TPSCR='" + r.getTpscr() + "'");
                vo.setOrderByClause("IDLOG DESC");
                vo.executeQuery();
            }

            // filtro su processi schedulati (28/07/2010)
            ViewObject voCoda = am.findViewObject("Impexp_CodaProcessiView1");
            voCoda.setWhereClause("ULSS='" + ulss + "'");
            voCoda.executeQuery(); // mauro 26/10/2010

        } catch (Exception ex) {
            this.handleException(ex.getMessage());
        } finally {

        }

    }

    public void schedule(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String) session.get("user");
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding cnfIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject vo = cnfIter.getViewObject();
        Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow) vo.getCurrentRow();
        try {
            AccessManager.checkPermission(this.getRelatedPermission());

            if (r == null)
                throw new Exception("Non e' stata selezionata nessuna ulss oppure la ulss selezionata non presenta i dati di configurazione per eseguire tale operazione.");

        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException(ex.getMessage(), null);
            return;
        }

        int PID = SchedUtils.scheduleProcess((Parent_AppModule) vo.getApplicationModule(), "ANA", ulss, tpscr, user);
        DCIteratorBinding processiIter = bindings.findIteratorBinding("Impexp_CodaProcessiView1Iterator");
        ViewObject voCoda = processiIter.getViewObject();
        voCoda.executeQuery();

        String msg = "IMPORT DATI ANAGRAFICI per l'azienda sanitaria " + ulss + " schedulato. PID=" + PID;

        ((ImpExp_AppModule)vo.getApplicationModule()).log(ulss, r.getTpdip(), ConfigurationConstants.IMPEXP_LOC, msg, tpscr);

        // comunico il fatto all'utente
        this.handleMessages(FacesMessage.SEVERITY_INFO,"Il processo di aggiornamento dei dati anagrafici per la ulss " + r.getUlss() +
                            " e' stato schedulato");
    }

    public void cleanLog(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Impexp_SoLogTransferView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row r;
        while ((r = vo.last()) != null) {
            r.remove();
        }
        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        commitBinding.execute();
    }

    public void annulla(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
            
            String user = (String) session.get("user");
            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");


        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding cnfIter = bindings.findIteratorBinding("Impexp_SoCnfImpexpView1Iterator");
        ViewObject vo = cnfIter.getViewObject();
            Impexp_SoCnfImpexpViewRow r=(Impexp_SoCnfImpexpViewRow)vo.getCurrentRow();
            try
            {
              AccessManager.checkPermission(this.getRelatedPermission());
              
               if(r==null)
                 throw new Exception("Non e' stata selezionata nessuna ulss oppure la ulss selezionata non presenta i dati di configurazione per eseguire tale operazione.");

            }
            catch(Exception ex)
            {
             ex.printStackTrace();
              this.handleException("Sono stati riscontrati problemi nell'esecuzione dell'operazione: "+ex.getMessage());
              return;
            }

        DCIteratorBinding processiIter = bindings.findIteratorBinding("Impexp_CodaProcessiView1Iterator");
        ViewObject voCoda = processiIter.getViewObject();
             
             Row proc = voCoda.getCurrentRow();
             
             if (proc == null)
             {
               this.handleException("Non e' stato selezionato il processo da annullare");
               return;
             }
             
             String stato = (String) proc.getAttribute("Stato");
             
             if (!stato.equals("S"))
             {
               this.handleException("Lo stato del processo selezionato e' diverso da 'Schedulato', impossibile annullare la schedulazione");       
               return;
             }
             
             int pid = ((Integer) proc.getAttribute("Pid")).intValue();
             
             SchedUtils.cancelProcess(pid, vo.getApplicationModule());
             
             // refresh
             
             voCoda.executeQuery();   
                  
            String msg = "Il processo con PID = '" + pid + "' (IMPORT DATI ANAGRAFICI per l'azienda sanitaria " + ulss + 
              ") e' stato annullato su richiesta utente.";
            
            ((ImpExp_AppModule)vo.getApplicationModule()).log(ulss,
                  r.getTpdip(),
                  ConfigurationConstants.IMPEXP_LOC,
                  msg,
                  tpscr);    

              // comunico il fatto all'utente
              this.handleMessages(FacesMessage.SEVERITY_INFO,"Il processo con PID = " + pid + " e' stato annullato");
    }

    public void cancella(ActionEvent actionEvent) {

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding processiIter = bindings.findIteratorBinding("Impexp_CodaProcessiView1Iterator");
        ViewObject vo = processiIter.getViewObject();
        ImpExp_AppModule am = (ImpExp_AppModule) vo.getApplicationModule();

        vo.setWhereClause("ULSS='" + ulss + "' and stato in ('A','C')");
        vo.executeQuery();
        
        RowSetIterator rsI = vo.createRowSetIterator(null);
        while(rsI.hasNext()){
            Row r = rsI.next();
            if (r.getAttribute("LinkLog") != null) {
                String filename =
                    ConfigurationReader.readProperty("scheduledLogPath") + "/" + (String) r.getAttribute("LinkLog");

                File f = new File(filename);
                if (f != null)
                    f.delete();
            }
            String sql = "DELETE FROM SO_CODA_PROCESSI WHERE PID = " + r.getAttribute("Pid");
            am.getTransaction().executeCommand(sql);
            r.remove();
        }
        rsI.closeRowSetIterator();  

        am.getTransaction().commit();
        
        //refresh vo
        vo.setWhereClause("ULSS='" + ulss + "' ");
        vo.executeQuery();
    }

    
}
