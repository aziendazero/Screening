package view.referto;

import insiel.dataHandling.DateUtils;
import insiel.dataHandling.ObjectTransformationUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.faces.event.ActionEvent;

import model.common.RefMa_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.Ref_SearchBean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.util.Utility;

public class RefMa_ricercaAction extends RefMa_DataForwardAction {

    private RichForm searchForm;
    private RichTable tableSearchResult;

    public String onCerca() {

        try {
            this.search();

        } catch (Exception ex) {
            this.handleException("Impossibile eseguire la ricerca: " + ex.getMessage(), null);
        }

        return "";
    }
    
    public void reset(ActionEvent actionEvent) {
        Ref_SearchBean bean =
            (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();

        bean.reset();
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc3'])");
    }

    public static void search() throws Exception {

        Ref_SearchBean bean =
            (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
        DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInvitiPerRefertiMAView1Iterator");       
        ViewObject vo = voIter.getViewObject();
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String where = "(DA_CONFERMARE IS NULL OR DA_CONFERMARE=0) AND ";
        Cnf_selectionBean clauseBean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();

        //se si tratta di una query vuota, resetto tutto
        if (bean.isEmptyQuery()) {
            bean.reset();
            bean.setEmptyQuery(false);
            vo.setWhereClause("1=2");
            vo.executeQuery();

            String newQry = "select null codts, null ulss from dual where 1=2";
            
            clauseBean.setInClause(newQry);
            clauseBean.setNote(null);

            return;

        }

        //03102011 Gaion parametri di ricerca minimi
        boolean paramOk = false;

        if ((bean.getCognome() != null) && (bean.getCognome().length() > 1)) {
            paramOk = true;
        } else if ((bean.getNome() != null) && (bean.getNome().length() > 1)) {
            paramOk = true;
        } else if (bean.getAnno_nascita() != null && bean.getMese_nascita() != null) {
            paramOk = true;
        } else if (bean.getCodts() != null && bean.getCodts().length() > 0) {
            paramOk = true;
        } else if ((bean.getPrelievo_al().equals("il")) && (bean.getPrelievo_dal() != null)) {
            paramOk = true;
        } else if (bean.getPrelievo_dal() != null) {
            if (bean.getPrelievo_al().equals("dopo il")) {
                //try{
                //testo il formato
                Date inputDate = bean.getPrelievo_dal();
                Calendar today = Calendar.getInstance();
                today.roll(Calendar.MONTH, false);
                //la data non deve essere antecedente a un mese fa
                if (inputDate.after(today.getTime())) {
                    paramOk = true;
                } else { //oppure ci deve essere almeno un secondo parametro impostato
                    if (bean.getParamNumber() >= 2) {
                        paramOk = true;
                    }

                }

            }
            //fine "dopo il"
            else if (bean.getPrelievo_al().equals("prima del")) { //e' possibile solo se e' impostato almeno un altro parametro
                if (bean.getPrelievo_dal() != null) {
                    if (bean.getParamNumber() >= 2) {
                        paramOk = true;
                    }
                }
            }
        } else if (bean.getCodiceDocumento() != null && bean.getCodiceDocumento().length() > 0) {
            paramOk = true;
        } else if (bean.getChiave() != null && bean.getChiave().length() > 0) {
            paramOk = true;
        }

        if (!paramOk) {
            throw new Exception("Parametri di ricerca insufficienti. Impostare almeno uno di questi campi: Cognome (con almeno due caratteri), Nome (con almeno due caratteri)," +
                                "Tessera sanitaria, Data di nascita, Chiave anonima, Data invito il, oppure data invito prima del/dopo il assieme ad un altro filtro");

        }
        //03102011 fine

        AccessManager.checkPermission("SORefertazione");

        //il centro di referto non e' obbligatorio
        if (bean.getCentro_ref() != null && bean.getCentro_ref().intValue() > 0)
            where += "Ref_SoInvito.IDCENTROREF1LIV=" + bean.getCentro_ref() + " AND ";


        //seleziono solo gli inviti che possono avere un referto
        where +=
            "Ref_SoInvito.CODESITOINVITO IN (" +
            ObjectTransformationUtils.arrayToSQLString(ConfigurationConstants.CODICI_ESITI_REFERTABILI) + ") AND ";

        //centro di referto
        if (bean.getCentro_prel() != null && bean.getCentro_prel().intValue() > 0)
            where += "Ref_SoInvito.IDCENTROPRELIEVO=" + bean.getCentro_prel() + " AND ";

        //data test
        //try{
        if (bean.getPrelievo_dal() != null) {
            //vedo l'operatore di confronto
            String op;
            if ("prima del".equals(bean.getPrelievo_al()))
                op = "<";
            else if ("dopo il".equals(bean.getPrelievo_al()))
                op = ">";
            else
                op = "=";
            
            Date inputDate = bean.getPrelievo_dal();

            where +=
                "Ref_SoInvito.DTAPP" + op + "TO_DATE('" + DateUtils.dateToString(inputDate) + "','" + DateUtils.DATE_PATTERN +
                "') AND ";

        }

        //nome e cognome
        if (bean.getCognome() != null && bean.getCognome().length() > 0)
            //MOD20071114 performance

            // where+="UPPER(COGNOME) LIKE '"+ViewHelper.replaceApostrophe(bean.getCognome().toUpperCase())+"%' AND ";
            where += "COGNOME LIKE '" + ViewHelper.replaceApostrophe(bean.getCognome().toUpperCase()) + "%' AND ";
        if (bean.getNome() != null && bean.getNome().length() > 0)
            //MOD20071114 performance

            //where+="UPPER(NOME) LIKE '"+ViewHelper.replaceApostrophe(bean.getNome().toUpperCase())+"%' AND ";
            where += "NOME LIKE '" + ViewHelper.replaceApostrophe(bean.getNome().toUpperCase()) + "%' AND ";
        //data di nascita
        if (bean.getGiorno_nascita() != null && bean.getGiorno_nascita().intValue() > 0)
            where += "TO_CHAR(DATA_NASCITA,'dd')=LPAD('" + bean.getGiorno_nascita() + "',2,'0') AND ";
        if (bean.getMese_nascita() != null && bean.getMese_nascita().intValue() > 0)
            where += "TO_CHAR(DATA_NASCITA,'MM')=LPAD('" + bean.getMese_nascita() + "',2,'0') AND ";
        if (bean.getAnno_nascita() != null && bean.getAnno_nascita().intValue() > 0)
            where += "TO_CHAR(DATA_NASCITA,'yyyy')='" + bean.getAnno_nascita() + "' AND ";

        //tessera sanitaria
        if (bean.getCodts() != null && bean.getCodts().length() > 0)
            where += "Ref_SoInvito.CODTS='" + bean.getCodts() + "' AND ";

        //chiave anonima
        if (bean.getChiave() != null && bean.getChiave().length() > 0)
            where += "Sogg_SoChiavi.CHIAVE='" + bean.getChiave() + "' AND ";

        //filtro su un livello?
        //prima controllo che l'utente posa vedere tutti i livelli
        boolean livello_free = true;
        try {
            AccessManager.checkPermission("SORef2Liv");
        } catch (Exception ex) { //se l'utente non puo' vedere i 2 livelli...
            livello_free = false;
        }
        //se non ci sono restrizioni e l'utente ha seleizonato un livello specifico
        if (livello_free && !"tutti".equals(bean.getLivello()))
            where += "Ref_SoInvito.LIVELLO=" + bean.getLivello() + " AND ";
        //se ci sono restrizioni impongo il primo livello
        else if (!livello_free)
            where += "Ref_SoInvito.LIVELLO=1 AND ";


        //vedo se devo leggere tutti i referti o solo quelli attuali
        if (!bean.isInviti_attivi()) {
            //carico solo quelli attivi
            where += "Ref_SoInvito.ATTIVO=1 AND ";
        }

        //referti pendenti da piu' di x giorni
        if (bean.getPendente() && bean.getGiorni_pendente() != null && bean.getGiorni_pendente().intValue() >= 0) {
            where +=
                "(Ref_SoInvito.DTAPP+" + bean.getGiorni_pendente() + ")<SYSDATE AND " +
                "(Ref_SoRefertomammo1liv.COMPLETO IS NULL OR Ref_SoRefertomammo1liv.COMPLETO<>1) " +
                "AND (Ref_SoRefertomammo2liv.COMPLETO IS NULL OR Ref_SoRefertomammo2liv.COMPLETO<>1) AND ";
        }

        //referti aperti e/o chiusi
        if (bean.getReferti_chiusi() != null) {
            //solo referti ancora aperti
            if ("aperti".equals(bean.getReferti_chiusi()))
                where += "(Ref_SoRefertomammo1liv.COMPLETO=0 OR Ref_SoRefertomammo2liv.COMPLETO=0) AND ";
            //solo referti gia' chiusi
            if ("chiusi".equals(bean.getReferti_chiusi()))
                where += "(Ref_SoRefertomammo1liv.COMPLETO=1 OR Ref_SoRefertomammo2liv.COMPLETO=1) AND ";
            if ("con 3 livello aperto".equals(bean.getReferti_chiusi()))
                where +=
                    "EXISTS (SELECT * FROM SO_INTERVENTOMAMMO I " +
                    "WHERE I.IDREFERTO=Ref_SoRefertomammo2liv.IDREFERTO AND I.COMPLETO=0) AND ";
            if ("con 3 livello chiuso".equals(bean.getReferti_chiusi()))
                where +=
                    "EXISTS (SELECT * FROM SO_INTERVENTOMAMMO I " +
                    "WHERE I.IDREFERTO=Ref_SoRefertomammo2liv.IDREFERTO AND I.COMPLETO=1) AND ";

        }


        // categoria di tipo di invito
        if (bean.getFollow_up() != null && bean.getFollow_up().intValue() >= 0) {
            // il tipo di richiamo deve esser di tipo followup di secondo livello
            where += "Ref_SoCnfTpinvito.IDCATEG=" + bean.getFollow_up() + " AND ";

        }

        // categoria di richiamo
        if (bean.getCat_richiamo() != null && bean.getCat_richiamo().intValue() >= 0) {
            //categoria normale
            if (bean.getCat_richiamo().intValue() != 0)
                where +=
                    "Ref_SoInvito.TPRICHIAMO IN (SELECT IDTPINVITO FROM SO_CNF_TPINVITO " + "WHERE ULSS='" + ulss +
                    "' AND TPSCR='" + tpscr + "' AND IDCATEG=" + bean.getCat_richiamo() + ") AND ";
            else
                //nessun richiamo, inviti senza richiamo
                where += "Ref_SoInvito.TPRICHIAMO IS NULL AND ";
        }

        //suggerimento conclusivo
        if (bean.getSugg_esito() != null && bean.getSugg_descrizione() != null) {
            where +=
                "FUN_GETSUGG_MA(Ref_SoInvito.IDINVITO) LIKE '%" + bean.getSugg_esito() +
                "%' AND FUN_GETSUGG_MA(Ref_SoInvito.IDINVITO) LIKE '%" + bean.getSugg_descrizione() + "%' AND ";
        }

        //referti da revisionare
        if (bean.isDa_revisionare())
            where +=
                "Ref_SoRefertomammo1liv.IDREFERTO=ANY (SELECT R.IDREFERTO FROM SO_REFERTOMAMMO1LIV R " +
                "WHERE R.l1_sugg!=R.l2_sugg AND (R.revisione IS NULL OR R.revisione=0)) AND ";

        /* */
        //filtro sui centri dell'utente
        Integer c1 = (Integer) ADFContext.getCurrent().getSessionScope().get("centro1liv");
        Integer c2 = (Integer) ADFContext.getCurrent().getSessionScope().get("centro2liv");
        String in = (String) ADFContext.getCurrent().getSessionScope().get("elenco_centri");
        if (c1 != null || c2 != null) { //filtro i soggetti che sono associati ad un centro dell'utente oppure hanno
            //l'ultimo invito in tale centro
            where += " (Ref_SoInvito.idcentroprelievo in " + in;
            if (c1 != null)
                where += " or Ref_SoSoggScr.idcentro1liv=" + c1;
            if (c2 != null)
                where += " or Ref_SoSoggScr.idcentro2liv=" + c2;

            where += ") AND ";
        }
        /* */

        // 22/12/2009, gestione presenza senza referto
        if (bean.isPres_no_referto()) {
            where +=
                "Ref_SoInvito.CODESITOINVITO = 'S' and Ref_SoRefertomammo1liv.IDREFERTO is null AND Ref_SoRefertomammo2liv.IDREFERTO is null AND ";
        }
        // 22/12/2009, fine modifica

        if (bean.getCodiceDocumento() != null && bean.getCodiceDocumento().length() > 0) {
            if (bean.getSoloStorico()) {
                where +=
                    " Ref_SoInvito.CODTS IN (select CODTS AS CODTS_DOC " + "from so_soggetto where codts in " +
                    "(SELECT COLUMN_VALUE " + "FROM TABLE(CAST(FUN_DOC_TROVA_CODTS_ARRAY('" + bean.getTipoDocumento() +
                    "','" + bean.getCodiceDocumento() + "','" + ulss + "','S') AS VAR_ARRAY)))  " + "and ULSS ='" +
                    ulss + "') AND ";
            } else {
                where +=
                    " Ref_SoInvito.CODTS IN (FUN_DOC_TROVA_CODTS('" + bean.getTipoDocumento() + "','" +
                    bean.getCodiceDocumento() + "','" + ulss + "')) AND ";
            }
        }

        where = where.trim();

        if (where.endsWith("AND"))
            where = where.substring(0, where.length() - 3);

        vo.setWhereClause(where);
        vo.executeQuery();

        /*20080701 MOD:stampa elenco soggetti da refertazione*/
        String qry = vo.getQuery();
        qry = qry.replaceAll(":1\\b", "'" + ulss + "'");
        qry = qry.replaceAll(":2\\b", "'" + tpscr + "'");
        String newQry = "select codts, ulss from (" + qry + ")";
        clauseBean.setInClause(newQry);
        clauseBean.setNote("Elenco soggetti risultato della ricerca");
        /*20080701 FINE MOD*/

        //cancello l'eventuale impostazione dell'id_invito
        bean.setId_invito(null);
    }

    protected void afterUpdateModel() {
    }

    protected void updateDB() {
    }

    protected void loadFromDB() {

    }

    protected void findForward() {
        String event = (String) ADFContext.getCurrent().getRequestScope().get("event_call");

        // La pagina e' richiamata da menu'
        if ("cerca".equals(event)) {

            // Recupero il bean con i parametri di ricerca
            Ref_SearchBean bean =
                (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();

            // Tolgo il centro di refertazione dai parametri di ricerca
            if (!bean.isEmptyQuery()) {
                bean.setCentro_ref(null);
            }
            this.onCerca();
        }
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInvitiPerRefertiMAView1Iterator");
        ViewObject vo = voIter.getViewObject();
        if (vo.getEstimatedRowCount() > 0 && vo.getCurrentRow()!=null) {
            if (this.tableSearchResult == null)
                this.tableSearchResult = new RichTable();
    
            Row _row = vo.getCurrentRow();
            RowKeySet selected = new RowKeySetImpl();
            selected.add(Arrays.asList(_row.getKey()));
            this.tableSearchResult.setSelectedRowKeys(selected);
            Utility.gotoTablePageOfSelectedRow(voIter, this.tableSearchResult);
        }
    }

    public void setSearchForm(RichForm searchForm) {
        this.searchForm = searchForm;
    }

    public RichForm getSearchForm() {
        if (searchForm == null)
            findForward();

        return searchForm;
    }

    public String onReferto() {
        String tsk = (String) ADFContext.getCurrent().getRequestScope().get("tsk");
        
        DCBindingContainer bindings;
        if(tsk!=null && !tsk.isEmpty())
            bindings = (DCBindingContainer)findBindingContainer(tsk);
        else
            bindings = (DCBindingContainer) getBindings();
        
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInvitiPerRefertiMAView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row r = vo.getCurrentRow();
        RefMa_AppModule am = (RefMa_AppModule)vo.getApplicationModule();

        try {
            AccessManager.checkPermission("SORefertazione");

            if (r == null)
                throw new Exception("Nessun invito selezionato per il referto");

            //controllo il livello e se esiste gia' un referto del livello corretto
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            Integer livello = (Integer) session.get("ref_livello");
            if (livello == null)
                throw new Exception("Nessun invito selezionato per il referto");
            session.put("showTabs",true);
            
            //secondo livello
            if (livello.intValue() == 2) {
                AccessManager.checkPermission("SORef2Liv");

                //viewObject dipendente in base al valore di idinvito (che viene impostato in automatico)         
                ViewObject refVo = am.findViewObject("Ref_SoRefertomammo2livView1");

                //se il referto c'e' gia' non devo fare niente, solo visualizzarlo
                if (refVo.getRowCount() == 0) {

                    //se l'invito non ha un esito adeguato, allora non posso creare
                    //un referto
                    //se non ha esito va bene
                    if (r.getAttribute("Codesitoinvito") != null) {
                        boolean found = false;
                        for (int i = 0; i < ConfigurationConstants.CODICI_ESITI_REFERTABILI.length; i++) {
                            if (ConfigurationConstants.CODICI_ESITI_REFERTABILI[i].equals(r.getAttribute("Codesitoinvito"))) {
                                found = true;
                                break;
                            }
                        }

                        if (!found)
                            throw new Exception("L'invito selezionato non ha referto (esito=" +
                                                r.getAttribute("CodesitoInvito") + ")");
                    }
                }

                //vado a cercare l'invito di primo livello correlato (L'ULTIMO CON DATA
                //PRECEDENTE A QUELLO DI SECONDO LIV CHE STO REFERTANDO)
                ViewObject primol = am.findViewObject("Ref_SoInvito1livMAView1");
                try {
                    primol.setWhereClauseParams(new Object[] { r.getAttribute("Ulss"), r.getAttribute("Tpscr"),
                                                               r.getAttribute("Codts") });
                    String where =
                        "DTAPP < TO_DATE('" + DateUtils.dateToString(((oracle.jbo.domain.Date) r.getAttribute("Dtapp")).getValue()) +
                        "','" + DateUtils.DATE_PATTERN + "')";
                    primol.setWhereClause(where);
                    primol.executeQuery();
                } catch (Exception ex) {
                    //se non riesco a fare la query corretta, allora meglio non trovare niente
                    String where = "IDINVITO IS NULL";
                    primol.setWhereClause(where);
                    primol.executeQuery();
                }
                primol.first();
            }

            //primo livello
            else {

                //viewObject dipendente in base al valore di idinvito (che viene impostato in automatico)
                ViewObject refVo = am.findViewObject("Ref_SoRefertomammo1livView1");

                //se il referto c'e' gia' non devo fare niente, solo visualizzarlo
                if (refVo.getRowCount() == 0) {

                    //se l'invito non ha un esito adeguato, allora non posso creare un referto
                    //se non ha esito va bene
                    if (r.getAttribute("Codesitoinvito") != null) {
                        boolean found = false;
                        for (int i = 0; i < ConfigurationConstants.CODICI_ESITI_REFERTABILI.length; i++) {
                            if (ConfigurationConstants.CODICI_ESITI_REFERTABILI[i].equals(r.getAttribute("Codesitoinvito"))) {
                                found = true;
                                break;
                            }
                        }

                        if (!found)
                            throw new Exception("L'invito selezionato non ha referto (esito=" +
                                                r.getAttribute("CodesitoInvito") + ")");
                    }
                }

                //09032012 gaion : rimuovo dalla session il flag revisore per il doppio cieco
                session.remove("revisore");
                
                this.filter();
                Map sess = ADFContext.getCurrent().getSessionScope();
                Boolean authDoppioCieco = (Boolean) sess.get("SODoppioCieco");
                Boolean revisione = (Boolean) sess.get("revisore");
                try {
                    if ((authDoppioCieco != null && authDoppioCieco.booleanValue()) && (revisione == null)) {
                        int lettura = GestoreRefertiUI.loadFromDB1livMA();
                        if (lettura < 0)
                            throw new Exception("Non sono presenti o previste letture per l'utente connesso");
                        else {
                            switch (lettura) {
                            case 1:
                                return("livello" + livello+"Ma");
                            case 2:
                                return "to_refMa_lettura2";
                            case 3:
                                return "to_refMa_revisione";
                            }
                        }
                    } else {
                        //leggo i dati per l aprima lettura
                        GestoreRefertiUI.loadFromDB1livMA(1);
                    }
                } catch (Exception ex) {
                    this.handleException("Impossibile visualizzare il referto: " + ex.getMessage());
                    return null;
                }
            }
            
            return("livello" + livello+"Ma");
            
        } catch (Exception ex) {
            am.doRollback("Ref_SoInvitiPerRefertiMAView1");
            this.handleException("Impossibile visualizzare il referto: " + ex.getMessage(), null);
        }

        return null;
    }
    
    /**
     * Filtra i dati per i referti di 1 livello
     */
    private void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertomammo1livView1");
        Row r = ref.getCurrentRow();
        if (r == null)
            return;
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");

        // Determino la data referto (oggi se null)
        oracle.jbo.domain.Date dataReferto = (oracle.jbo.domain.Date) r.getAttribute("Dtreferto");
        if (dataReferto == null) {
            dataReferto = new oracle.jbo.domain.Date(new java.sql.Date(new java.util.Date().getTime()));
        }

        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = insiel.utilities.dataformats.DateUtils.dateToString(d.getValue());

        //LETTORE
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoView1");

        // Gaion 17062013 : se e' completo non filtro per ulss: per la rete mammografica
        if (((Integer) r.getAttribute("Completo")).intValue() == 1) {
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_RADIOLOGO, null, tpscr);
        } else {
            ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_RADIOLOGO, ulss, tpscr);
        }

        //indicazioni
        vo = am.findViewObject("Ref_SoCnfRef1livMXINDIView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //esiti
        vo = am.findViewObject("Ref_SoCnfRef1livMXEST1View1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //DENSITA
        vo = am.findViewObject("Ref_SoCnfRef1livMXDENSView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //suggerimenti
        vo = am.findViewObject("Ref_SoCnfSugg1livView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, dataReferto);
    }

    public void setTableSearchResult(RichTable tableSearchResult) {
        this.tableSearchResult = tableSearchResult;
    }

    public RichTable getTableSearchResult() {
        return tableSearchResult;
    }
}
