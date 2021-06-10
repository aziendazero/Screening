package view.commons.action;

import insiel.dataHandling.DateUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import model.common.Parent_AppModule;

import model.commons.AccessManager;

import model.soggetto.common.Sogg_SoSoggettoViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.layout.RichDecorativeBox;

import oracle.binding.BindingContainer;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Key;
import oracle.jbo.Transaction;
import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.CollectionModel;

import view.util.Utility;

public abstract class Parent_DataForwardAction {
    protected String amName;

    /**
     * Questo metodo servira' ad impostare il nome dell'application module
     * da usare
     * Esempio di implementazione:
     * this.amName='QuestoAppModule";
     */
    protected abstract void setAppModule();
    
    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }
    
    public BindingContainer findBindingContainer(String bindingsContainerId) {
        return BindingContext.getCurrent().findBindingContainer(bindingsContainerId);
    }

    /**
     * Gestisce una eccezione facendo un rollback dell'eventuale transazione
     * e segnalando l'errore all'utente
     * @param t transazione da annullare (se null non succede niente)
     * @param msg messaggio d'errore da comunicare all'utente
     * @param ctx contesto in cui pubblicare il messaggio
     */
    public void handleException(String msg, Transaction t) {
        if (t != null)
            t.rollback();
        if (msg != null) {
            ADFContext adfCtx = ADFContext.getCurrent();
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
            ctx.addMessage(null, fm);
        }

    }

    public void handleException(Throwable msg, Transaction t) {
        if (t != null)
            t.rollback();
        if (msg != null) {
            ADFContext adfCtx = ADFContext.getCurrent();
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, printStackTrace(msg), "");
            ctx.addMessage(null, fm);
        }

    }

    public static String printStackTrace(Throwable exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter, true));
        return stringWriter.toString();
    }


    /**
     * Gestisce una eccezione facendo un rollback (con ripristino current rows) dell'eventuale transazione
     * e segnalando l'errore all'utente
     * @param voNames nomi dei viewobject di cui ripristinare la riga corrente
     * @param am application module
     * @param msg messaggio d'errore da comunicare all'utente
     * @param ctx contesto in cui pubblicare il messaggio
     */
    public void handleException(String msg, Parent_AppModule am, String[] voNames) {
        am.doRollback(voNames);
        if (msg != null) {
            ADFContext adfCtx = ADFContext.getCurrent();
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
            ctx.addMessage(null, fm);
        }
    }

    public void handleException(String msg) {
        if (msg != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            handleException(ctx, msg);
        }
    }

    public void handleException(FacesContext facesContext, String msg) {
        if (msg != null) {
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, "");
            facesContext.addMessage(null, fm);
        }
    }

    /**
     * Gestisce la comunicazione di un messaggio informativo all'utente
     * @param msg messaggio informativo
     * @param ctx contesto in cui pubblicare il messaggio
     */
    public void handleMessages(FacesMessage.Severity severity, String msg) {
        if (msg != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(severity, msg, "");
            ctx.addMessage(null, fm);
        }
    }


    /**
     * Gestione della richiesta di navigazione: se la transazione e' "sporca", cioe'
     * se ci sono modifiche non salvate, allora si imposta un attributo della request
     * con il valore della destinazione. Nel metodo onLoad della pagina si controlla tale attributo
     * (o meglio, la formvalue che lo assume): se e' diverso da null, allora si chiede all'utente
     * se salvare prima di proseguire, latrimenti non succede nulla. Se la transzione non e' sporca,
     * la navigazione procede senza intoppi
     * @param ctx
     */
    public String onRichnav(String dest, HashMap params) {
        //prima si deve impostare il nome dell'application module
        this.setAppModule();
        Map req = ADFContext.getCurrent().getRequestScope();
        req.putAll(params);

        boolean modP = this.pendingUpdatesOnRichnav();
        //se ci sono modifiche non salvate...
        if (modP) { //imposto l'attributo destinazione e lascio al metodo onLoad la gestione
            req.put("destNav", dest);
            ADFContext.getCurrent().getViewScope().put("destNav", dest);
            RequestContext.getCurrentInstance().addPartialTarget(getFvDest());
            Utility.addScriptOnPartialRequest("wantToSaveOnLoad()");
            return "error";
        } else { //altriemnti navigo verso la destinzione

            try {
                this.beforeNavigate(dest);
                //e navigo verso la destinazione
                //  ctx.getHttpServletResponse().sendRedirect(destination);
                //ctx.getHttpServletRequest().getRequestDispatcher(destination).forward(ctx.getHttpServletRequest(), ctx.getHttpServletResponse());

                if (dest == null)
                    throw new Exception("destinazione non specificata");
                /*String suffix = "";
                if (destination.indexOf("?") >= 0)
                    suffix = "_p";
                int index = destination.indexOf(".");
                if (index >= 0)
                    destination = destination.substring(0, index);*/
            } catch (Exception ex)

            {
                ex.printStackTrace();
                this.handleException("Impossibile navigare verso la destinazione " + dest + ": " + ex.getMessage(),
                                     null);
                return null;
            }
        }
        return dest;
    }

    /**
     * Gestione dell'evento di navigazione: esegue l'azione richiesta dalla request
     * (commit o rollback, serve ch enella pagina uix ci sia il binding relativo)
     * e poi naviga verso la destinzione
     * @param ctx contesto
     */
    protected boolean save() {
        this.setAppModule();
        boolean pUpd = this.pendingUpdatesOnSave();
        if (!pUpd)
            return true;
        ApplicationModule am =
            BindingContext.getCurrent().findDataControl(amName + "DataControl").getApplicationModule();
        boolean opOK = this.beforeSave();
        if (opOK) {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map sess = adfCtx.getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) sess.get("user");
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                ((Parent_AppModule) am).preapareJournaling(user, ulss, tpscr);
            }
            boolean commitOK = true;
            try {
                am.getTransaction().commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                commitOK = false;
                this.handleException(ex.getMessage(), null);
                this.doRollback();
            }
            if (commitOK) {
                this.afterSave();
            }
            return commitOK;
        } else {
            return false;
        }
    }

    public String onNavigate() {

        //@codecoach:disable nextline
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getRequestScope();
        String destination = (String) sess.get("destination");

        boolean saveOK = this.save();
        if (saveOK) {

            try {
                this.beforeNavigate(destination);
                //  ctx.getHttpServletResponse().sendRedirect(destination);
                //ctx.getHttpServletRequest().getRequestDispatcher(destination).forward(ctx.getHttpServletRequest(), ctx.getHttpServletResponse());
                if (destination == null)
                    throw new Exception("destinazione non specificata");
                String suffix = "";
                if (destination.indexOf("?") >= 0)
                    suffix = "_p";
                int index = destination.indexOf(".");
                if (index >= 0)
                    destination = destination.substring(0, index);
            } catch (Exception ex) {
                ex.printStackTrace();
                this.handleException("Impossibile navigare verso la destinazione " + destination + ": " +
                                     ex.getMessage(), null);
            }
        } else
            return "errore";

        return destination;
    }

    protected boolean beforeSave() {
        return true;

    }

    protected String afterSave() {
        return null;
    }


    protected void beforeNavigate(String dest) throws Exception {

    }


    protected void doRollback() {
    }

    protected boolean pendingUpdatesOnSave() {
        return true;
    }

    protected boolean pendingUpdatesOnRichnav() {
        ApplicationModule am =
            BindingContext.getCurrent().findDataControl(amName + "DataControl").getApplicationModule();
        boolean modP = am.getTransaction().isDirty();
        return modP;
    }

    protected void saveMessages(String messages) {
        System.out.println("----------------------------------> saveMessages: " + messages);
        Map session = ADFContext.getCurrent().getSessionScope();
        // Remove any messages attribute if none are required
        if ((messages == null) || messages.isEmpty()) {
            session.remove("Globals_MESSAGE_KEY");
            return;
        }

        // Save the messages we need
        session.put("Globals_MESSAGE_KEY", messages);
        
        System.out.println("----------------------------------> saveMessages: " + session.get("Globals_MESSAGE_KEY"));

    }

    private RichInputText fvDest;
    public void setFvDest(RichInputText fvDest) {
        this.fvDest = fvDest;
    }

    public RichInputText getFvDest() {
        return fvDest;
    }
    
    private RichDecorativeBox  tabsMenu;
    public void setTabsMenu(RichDecorativeBox  tabsMenu) {
        this.tabsMenu = tabsMenu;
    }

    public RichDecorativeBox  getTabsMenu() {
        return tabsMenu;
    }
    
    /**      * Synchronizes the table UI component row selection with the
     * * selection in the ADF binding layer
     * * @param selectionEvent event object created by the table
     * * component upon row selection
     * */
    public static void makeCurrent(SelectionEvent selectionEvent) {
        RichTable _table = (RichTable) selectionEvent.getSource();
        //the Collection Model is the object that provides the
        ////structured data
        ////for the table to render
        CollectionModel _tableModel = (CollectionModel) _table.getValue();
        ////the ADF object that implements the CollectionModel is
        ////JUCtrlHierBinding. It is wrapped by the CollectionModel API
        JUCtrlHierBinding _adfTableBinding = (JUCtrlHierBinding) _tableModel.getWrappedData();
        //Acess the ADF iterator binding that is used with
        ////ADF table binding
        DCIteratorBinding _tableIteratorBinding = _adfTableBinding.getDCIteratorBinding();
        //the role of this method is to synchronize the table component
        //selection with the selection in the ADF model
        Object _selectedRowData = _table.getSelectedRowData();
        //cast to JUCtrlHierNodeBinding, which is the ADF object
        ////that represents a row
        JUCtrlHierNodeBinding _nodeBinding = (JUCtrlHierNodeBinding) _selectedRowData;
        //get the row key from the node binding and set it
        ////as the current row in the iterator
        Key _rwKey = _nodeBinding.getRowKey();
        _tableIteratorBinding.setCurrentRowWithKey(_rwKey.toStringFormat(true));
    }
    
    /**
     * Metodo che controlla se il dato centro è autorizzato per l'utente di sessione
     *
     * @param idCentro
     * @return
     */
    public String isAuthorizedCenter (Integer idCentro){
        try{
            AccessManager.checkPermission("SOLimiteCentri");
            ADFContext adfCtx = ADFContext.getCurrent();
            Map sess = adfCtx.getSessionScope();
            List<Integer> centriAutorizzati = (List<Integer>) sess.get("centriautorizzati");
            if (centriAutorizzati == null){
                return "false";
            } else if (!centriAutorizzati.contains(idCentro)){
                return "false";
            }
        } catch (IllegalAccessException e){                              
            //non deve fare niente           
        }
        return "true";
    }

    private RichPopup confCellWarning;
    public void setConfCellWarning(RichPopup confCellWarning) {
        this.confCellWarning = confCellWarning;
        
    }

    public RichPopup getConfCellWarning() {
        return confCellWarning;
    }    

    public boolean verificaConfermaCellulare(){
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoSoggettoView1Iterator");
        
        Map sess = ADFContext.getCurrent().getSessionScope();
        if(voIter!=null){
            Sogg_SoSoggettoViewRow cSogg = (Sogg_SoSoggettoViewRow) voIter.getCurrentRow();
    
            if(cSogg!=null){
                // controllo conferma cellulare
                ADFContext adfCtx = ADFContext.getCurrent();
                // controllo conferma cellulare
                // se il cellulare è null non faccio controlli
                // il controllo va bypassato anche se ho già mostrato l'alert adfCtx.getRequestScope().containsKey("checkCellulare")
                boolean showArlert =  cSogg.getCellulare()!=null && !adfCtx.getRequestScope().containsKey("checkCellulare");
                
                // se adfCtx.getRequestScope().containsKey("checkCellulare") allora significa che ho già mostrato l'alert
                String confirmCheckCellulare = adfCtx.getRequestScope().containsKey("checkCellulare") ? (String)adfCtx.getRequestScope().get("checkCellulare") : 
                    (cSogg.getCellConfermato()!=null && cSogg.getCellConfermato()==1) ? "SI" : "NO";

                if(cSogg.getCellulare()==null){
                    cSogg.setCellConfermato(0);
                    confirmCheckCellulare = "NO";
                } else {
                    if(showArlert && (!confirmCheckCellulare.equalsIgnoreCase("SI") || (cSogg.getCellOld()!=null && !cSogg.getCellulare().equalsIgnoreCase(cSogg.getCellOld())))){
                        if(this.confCellWarning==null)
                            this.confCellWarning = new RichPopup();
                        
                        this.confCellWarning.show(new RichPopup.PopupHints());
                        return false;
                    }
                }
                
                if(confirmCheckCellulare.equalsIgnoreCase("SI"))
                    cSogg.setCellConfermato(1);
                else
                    cSogg.setCellConfermato(0);
                    
                if(cSogg.getCellConfermato()==1){
                    cSogg.setOpconferma((String) sess.get("user"));
                    cSogg.setDtconferma(DateUtils.getOracleDateNow());
                }else{
                    cSogg.setOpconferma(null);
                    cSogg.setDtconferma(null);
                }
            }
        }   
        
        return true;
    }
    
    public String onAppl() {
        return null;
    }
    
    public String onConf() {
        return null;
    }
    
    public String confirmCell(){
        ADFContext adfCtx = ADFContext.getCurrent();
        String dest = (String)adfCtx.getRequestScope().get("destination");
        String op = (String)adfCtx.getRequestScope().get("operation");
        
        if(op!=null){
            switch (op){
            case "NAVIGATE":
                return onNavigate();
            case "CONF":
                return onConf();
            case "APPL":
                return onAppl();
            }
            
            return dest;
        }
        
        
        return "azz";
    }
}
