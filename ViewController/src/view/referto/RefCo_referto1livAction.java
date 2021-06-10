package view.referto;

import insiel.dataHandling.DateUtils;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.Parent_AppModule;
import model.common.RefCo_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Ref_2livBean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

public class RefCo_referto1livAction extends RefCo_DataForwardAction {
    private RichForm refertoForm;

    public RefCo_referto1livAction() {
        super();
    }

    @Override
    protected void afterUpdateModel() {
        try {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
            Row r = voIter.getCurrentRow();
            String changeRecord = (String) ADFContext.getCurrent().getRequestScope().get("changeRecord");
            //se carico un referto completo oppure ho cambiato record, devo leggere i dati da DB
            if (r != null &&
                (ConfigurationConstants.DB_TRUE.equals(r.getAttribute("Completo")) ||
                 (changeRecord != null && changeRecord.length() > 0))) {
                this.loadFromDB();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void loadFromDB() {
        changeEsito();  
    }

    public void changeEsito() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
        Row current = voIter.getCurrentRow();

        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        if (current == null)
            bean.setCo_esito1liv(ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
        else
            bean.setCo_esito1liv((Integer) current.getAttribute("Idsugg"));
        
    }

    @Override
    protected void updateDB() {
        // TODO Implement this method
    }

    public void setRefertoForm(RichForm refertoForm) {
        this.refertoForm = refertoForm;
    }

    public RichForm getRefertoForm() {
        if (refertoForm == null) {
            //filtro i dati
            this.filter();

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
            Row current = voIter.getCurrentRow();

            if (current != null) {
                if (current.getAttribute("Idsugg") != null) {
                    current.setAttribute("Esito", ConfigurationConstants.CODICE_SUGG1L_DEFAULT);
                } else {
                    current.setAttribute("Esito", ((Integer) current.getAttribute("Idsugg")).intValue());
                }
            }
        }
        return refertoForm;
    }

    protected void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
        RefCo_AppModule am = (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
        Row r = voIter.getCurrentRow();
        if (r == null)
            return;
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        Date d = (Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        d = (Date) r.getAttribute("Dtreferto");
        String dataReferto = null;
        if (d != null)
            dataReferto = DateUtils.dateToString(d.getValue());

        //LETTORE
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_LETTORE_CO, ulss, tpscr);

        //ESITO
        vo = am.findViewObject("Ref_SoCnfRef1livCoES1LIVView1_1");
        
        /* I00101166: Referto Colon/Covid - Accedendo alla pagina del referto di 1 livello, 
         * la lista valori degli esiti deve essere ulteriormente filtrata con le eventuali 
         * configurazioni (legate al Tipo Invito) presenti in SO_CNF_TPINVITO_REF */
        this.filterCnfRefertiCo(vo, ulss, tpscr, dataReferto);

        //centro di refertazione
        vo = am.findViewObject("Ref_SoCentroRefertoView2");
        ViewObjectFilters.filterCentri(vo, ulss, tpscr, data);
        
        changeEsito();
    }
    
    /**
     * I00101166 Check configurazioni SO_CNF_TPINVITO_REF.
     * Se in SO_CNF_TPINVITO_REF non vi sono configurazioni, si procede con il filtro standard
     * @param data
     * @param tpscr
     * @param ulss
     * @param vo */
    protected void filterCnfRefertiCo(ViewObject vo, String ulss, String tpscr, String data) {
        RefCo_AppModule am = (RefCo_AppModule) vo.getApplicationModule();
        List<Integer> idRef1lList = new ArrayList<Integer>(); 
        
        //Preparo la where standard
        String where = "TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' ";
        if (data != null)
            where += " AND (DTFINEVALIDITA IS NULL OR  DTFINEVALIDITA > TO_DATE('" + data + "','" + DateUtils.DATE_PATTERN + "'))";
        
        //Recupero il tipo invito
        ViewObject invitoVo = am.findViewObject("Ref_SoInvitoView2");
        Row inv = invitoVo.first();
        String idTpinvito = null;
        
        if (inv!=null && inv.getAttribute("Idtpinvito")!=null) {
            idTpinvito = (String)inv.getAttribute("Idtpinvito");
            
            //Cerco eventuali configurazioni presenti in SO_CNF_TPINVITO_REF
            String qry = "SELECT IDCNFREF1L FROM SO_CNF_TPINVITO_REF " +
                "WHERE IDTPINVITO = '" + idTpinvito + "' AND GRUPPO = 'ES1LIV' " +
                "AND TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "'";
            
            ViewObject voIdRef1l = am.createViewObjectFromQueryStmt("", qry);
            voIdRef1l.setRangeSize(-1);
            voIdRef1l.executeQuery();
            
            RowSetIterator iter = voIdRef1l.createRowSetIterator(null);
            while (iter.hasNext()) {
                Row row = iter.next();

                Integer idRefl1l = row.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) row.getAttribute(0)).intValue() : null;
                if (idRefl1l!=null)
                    idRef1lList.add(idRefl1l);
            }
            iter.closeRowSetIterator();
            
            //Aggiungo la condizione per filtrare ulteriormente gli esiti
            if (idRef1lList.size()>0)
                where += " AND IDCNFREF1L IN (" + idRef1lList.toString().replace("[", "").replace("]", "") + ")";
        } 
        
        vo.setWhereClause(where);
        vo.executeQuery();
        
    }

    public void onSetEsito(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
        ViewObject ref = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) ref.getApplicationModule();
        Row current = ref.getCurrentRow();
        if (current == null)
            return;

        Ref_2livBean bean =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();

        // Cerco il suggerimento corrispondente all'esito (stessa chiave)
        ViewObject suggVo = am.findViewObject("Ref_SoCnfSugg1livView1");
        Row[] suggRows = suggVo.getFilteredRows("Idsugg", bean.getCo_esito1liv());
        Integer idsugg;
        if (suggRows.length > 0) {
            idsugg = bean.getCo_esito1liv();
        } else {
            idsugg = 0;
        }
        current.setAttribute("Idsugg", idsugg);
    }

    protected void beforeSavingReferto() throws Exception {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
        ViewObject ref = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) ref.getApplicationModule();
        //  Row r=ref.getCurrentRow();
        RowSetIterator iter = ViewHelper.getRowSetIterator(ref, "provette_iter");

        try {
            ViewObject invito = am.findViewObject("Ref_SoInvitoView2");
            Row inv = invito.first();
            if (inv == null)
                throw new Exception("Invito relativo non trovato");

            if (inv.getAttribute("Idcentroref1liv") == null)
                throw new Exception("Il centro di lettura non risulta valido");

            Row r = null;
            String user = (String) ADFContext.getCurrent().getSessionScope().get("user");
            while ((r = iter.next()) != null) {
                //impostazione centro di refertzione
                r.setAttribute("Idcentroref", inv.getAttribute("Idcentroref1liv"));

                //impostazione dati ultima modifica
                r.setAttribute("Dtultmodifica", DateUtils.getOracleDateNow());
                r.setAttribute("Opultmodifica", user);
            }

            r = iter.last();
            //allineamento numero vetrino con accettazione (considero un solo numero)
            GestoreReferti gr = new GestoreReferti(am);
            gr.updateVetrinoInAcc((Integer) r.getAttribute("Idinvito"), (BigDecimal) r.getAttribute("CodProvetta"),
                                  (String) r.getAttribute("Tpscr"), true, user, false);

            this.updateDB();
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }
    }

    public void onChangeDataLettura(ValueChangeEvent valueChangeEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
        ViewObject refVo = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) refVo.getApplicationModule();
        Row refRow = refVo.getCurrentRow();

        String ulss = (String) refRow.getAttribute("Ulss");
        String tpscr = (String) refRow.getAttribute("Tpscr");

        String dataReferto = (String) valueChangeEvent.getNewValue();

        // Filtro gli esiti in funzione della data referto
        ViewObject suggVo = am.findViewObject("Ref_SoCnfRef1livCoES1LIVView1_1");
        ViewObjectFilters.filterCnfReferti(suggVo, ulss, tpscr, dataReferto);

        // Valorizzo il suggerimento in funzione dell'esito
        onSetEsito(null);
    }

    public void onClose(ClientEvent clientEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
        ViewObject ref = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) ref.getApplicationModule();
        Row r = ref.first();
        String to_do = (String) clientEvent.getParameters().get("ref_lettera");
        try {

            //controllo di conformita'
            if (r.getAttribute("Idsugg") == null ||
                ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getAttribute("Idsugg")))
                throw new Exception("Non e' stata segnalata nessuna raccomandazione conclusiva");


            this.checkMandatoryData(r);
            //aggiorno il db in conseguenza alle selezioni di interfaccia
            this.beforeSavingReferto();

            ViewObject invitoVo = am.findViewObject("Ref_SoInvitoView2");
            Row inv = invitoVo.first();

            //creo la lettera di refero
            GestoreReferti gr = new GestoreReferti(am);
            //se devo ricreare la lettera la cancello e ricreo
            if ("ricrea".equals(to_do)) {
                gr.deleteLettera(r);
                gr.creaLetteraDiReferto(r, 1, null, inv);
            }
            //se la lettera non c'e' la creo semplicemente
            else if ("crea".equals(to_do)) {
                gr.creaLetteraDiReferto(r, 1, null, inv);
            }

            //il referto e' completo
            r.setAttribute("Completo", ConfigurationConstants.DB_TRUE);

            //imposto il richiamo
            Map session = ADFContext.getCurrent().getSessionScope();
            gr.updateInvito(r, invitoVo, 1, (String) session.get("user"));
            //salvo il tutto
            am.getTransaction().commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException("Impossibile chiudere il referto: " + ex.getMessage(), null);
        } 
    }

    protected void checkMandatoryData(Row ref) throws Exception {
        if (ref.getAttribute("Dtreferto") == null)
            throw new Exception("Inserire la data di lettura della provetta");


    }

    public String onDeleteReferto() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
        ViewObject ref = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) ref.getApplicationModule();

        try {

            Row r = voIter.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto selezionato per la cancellazione");

            String ulss = (String) r.getAttribute("Ulss");
            String tpscr = (String) r.getAttribute("Tpscr");
            Integer idinvito = (Integer) r.getAttribute("Idinvito");

            ViewObject allegati = am.findViewObject("Ref_SoAllegatoView1");
            ViewObject inviti = am.findViewObject("Ref_SoInvitoView2");
            Row[] result1 = inviti.getFilteredRows("Idinvito", r.getAttribute("Idinvito"));
            //cancellazione referto e annessi
            GestoreReferti gr = new GestoreReferti(am);
            Map session = ADFContext.getCurrent().getSessionScope();
            String user = (String) session.get("user");
            r = ref.last();
            while (r != null) {
                gr.deleteRefertoCO(r, null, null, allegati, 1, am, user);
                r = ref.last();
            }
            //update invito relativo
            if (result1.length == 0)
                throw new Exception("Invito cui si riferisce il referto non trovato");

            gr.updateInvito(ulss, tpscr, idinvito, null, //sugg=null
                            inviti, 1, user);

            //6. salvo
            am.getTransaction().commit();

            //7. comunico all'utente che l'operazione e' avvenuta con successo e che il soggetto risulta
            //counque presentato
            //this.handleMessages(FacesMessage.SEVERITY_INFO,
            //                    "Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");

            this.saveMessages("Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");

            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
            ViewHelper.queryAndRestoreCurrentRow(vo);

            return "to_refCo_ricerca";
        } catch (Exception ex) {
            //  ex.printStackTrace();
            this.whenException("Impossibile cancellare il referto: " + ex.getMessage(), am,
                               new String[] { "Ref_SoInvitiPerRefertiCOView1", "Ref_SoRefertocolon1livView1" });
        }
        return null;
    }

    /**
     * Creazione di un nuovo referto
     * @param ctx
     */
    public void onCreateReferto(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocolon1livView1Iterator");
        ViewObject ref = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) ref.getApplicationModule();

        try {

            int provette = ref.getRowCount();

            //altrimenti lo devo creare
            ViewObject invito = am.findViewObject("Ref_SoInvitoView2");
            Row inv = invito.first();
            if (inv == null)
                throw new Exception("Invito non trovato");

            GestoreReferti gr = new GestoreReferti(am);

            if (inv.getAttribute("Dtapp") == null)
                throw new Exception("La data dell'invito non risulta valorizzata");
            if (inv.getAttribute("Idcentroprelievo") == null) //|| inv.getAttribute("Idcentroref1liv")==null)
                throw new Exception("Il centro di prelievo non risulta valorizzato"); //e/o il centro di refertazione dell'invito non risultano valorizzati");

            Map session = ADFContext.getCurrent().getSessionScope();
            Row nuovoRef = gr.nuovoRefertoCO(invito, ref, (String) session.get("user"), new Integer(provette + 1));

            nuovoRef.setAttribute("Esito", ((Integer) nuovoRef.getAttribute("Idsugg")).intValue());

            //filtro i dati
            this.filter();

        } catch (Exception ex) {
            //  ex.printStackTrace();
            this.whenException("Impossibile inserire un nuovo referto: " + ex.getMessage(), (Parent_AppModule) am,
                               new String[] { "Ref_SoInvitiPerRefertiCOView1" });
        }
    }
}
