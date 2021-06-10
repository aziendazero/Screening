package view.referto;

import insiel.dataHandling.DateUtils;
import insiel.dataHandling.ObjectTransformationUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.common.RefCo_AppModule;

import model.common.Sogg_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.PL_Bean;
import model.datacontrol.Ref_SearchBean;

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

public class RefCo_ricercaAction extends RefCo_DataForwardAction {
    private RichForm frmSearch;
    private RichTable tableSearchResult;

    public RefCo_ricercaAction() {
    }

    public void setFrmSearch(RichForm frmSearch) {
        this.frmSearch = frmSearch;
    }

    public RichForm getFrmSearch() {
        if (frmSearch == null)
            findForward();

        return frmSearch;
    }

    public void reset(ActionEvent actionEvent) {
        Ref_SearchBean bean =
            (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();

        bean.reset();
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc3'])");
    }

    public String onCerca() {

        try {
            this.search();

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInvitiPerRefertiCOView1Iterator");
            ViewObject vo = voIter.getViewObject();
            if (vo.getEstimatedRowCount() > 0 && vo.getCurrentRow() != null) {
                if (this.tableSearchResult == null)
                    this.tableSearchResult = new RichTable();

                Row _row = vo.getCurrentRow();
                RowKeySet selected = new RowKeySetImpl();
                selected.add(Arrays.asList(_row.getKey()));
                this.tableSearchResult.setSelectedRowKeys(selected);
                Utility.gotoTablePageOfSelectedRow(voIter, this.tableSearchResult);
            }

        } catch (Exception ex) {
            this.handleException("Impossibile eseguire la ricerca: " + ex.getMessage(), null);
        }

        return "";
    }

    public static void search() throws Exception {
        RefCo_AppModule am =
            (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
        Ref_SearchBean bean =
            (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String where = "Ref_SoInvito.ULSS ='"+ulss+"' AND Ref_SoInvito.TPSCR ='"+tpscr+"' AND (DA_CONFERMARE IS NULL OR DA_CONFERMARE=0) AND ";

        //se si tratta di una query vuota, resetto tutto
        if (bean.isEmptyQuery()) {
            bean.reset();
            bean.setEmptyQuery(false);
            vo.setWhereClause("1=2");
            vo.executeQuery();

            /*20080701 MOD:stampa elenco soggetti da refertazione*/
            String newQry = "select null codts, null ulss from dual where 1=2";
            Cnf_selectionBean clauseBean =
                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            clauseBean.setInClause(newQry);
            // System.out.println(newQry);
            clauseBean.setNote(null);
            /*20080701 FINE MOD*/

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
        } else if (bean.getChiave() != null && bean.getChiave().length() > 0) {
            paramOk = true;          
        } else if ((bean.getPrelievo_al().equals("il")) && (bean.getPrelievo_dal() != null)) {
            paramOk = true;
        } else if (bean.getPrelievo_dal() != null) {
            if (bean.getPrelievo_al().equals("dopo il")) {
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
        }

        if (!paramOk) {
            throw new Exception("Parametri di ricerca insufficienti. Impostare almeno uno di questi campi: Cognome (con almeno due caratteri), Nome (con almeno due caratteri)," +
                                "Tessera sanitaria, Data di nascita, Chiave anonima, Data invito il, oppure data invito prima del/dopo il assieme ad un altro filtro",
                                null);
        }
        //03102011 fine

        AccessManager.checkPermission("SORefertazione");

        //il centro di referto non e' obbligatorio
        if (bean.getCentro_ref() != null && bean.getCentro_ref().intValue() > 0)
            where += "Ref_SoInvito.IDCENTROREF1LIV=" + bean.getCentro_ref() + " AND ";


        //seleziono solo gli inviti che possono avere un referto
        where +=
            "Ref_SoInvito.CODESITOINVITO IN (" +
            ObjectTransformationUtils.arrayToSQLString(ConfigurationConstants.CODICI_ESITI_REFERTABILI_CO) + ") AND ";

        //centro di referto
        if (bean.getCentro_prel() != null && bean.getCentro_prel().intValue() > 0)
            where += "Ref_SoInvito.IDCENTROPRELIEVO=" + bean.getCentro_prel() + " AND ";

        //data test
        if (bean.getPrelievo_dal() != null) {
            //vedo l'operatore di confronto
            String op;
            if ("prima del".equals(bean.getPrelievo_al()))
                op = "<";
            else if ("dopo il".equals(bean.getPrelievo_al()))
                op = ">";
            else
                op = "=";

            where +=
                "Ref_SoInvito.DTAPP" + op + "TO_DATE('" + DateUtils.dateToString(bean.getPrelievo_dal()) + "','" +
                DateUtils.DATE_PATTERN + "') AND ";

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
        
        //Chiave anonima
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


        //se cerco quelli di primo livello posso usare anche il CODICE PROVETTA
        if ("1".equals(bean.getLivello())) {
            if (bean.getVetrino_dal() != null)
                where +=
                    "(Ref_SoRefertocolon1liv.COD_PROVETTA>=" + bean.getVetrino_dal() +
                    " OR Ref_SoRefertocolon1liv1.COD_PROVETTA>=" + bean.getVetrino_dal() + ") AND ";
            if (bean.getVetrino_al() != null)
                where +=
                    "(Ref_SoRefertocolon1liv.COD_PROVETTA<=" + bean.getVetrino_al() +
                    " OR Ref_SoRefertocolon1liv1.COD_PROVETTA<=" + bean.getVetrino_al() + ") AND ";

        }

        //numero di provette di un referto
        if (bean.getAnno_vetrino() != null && bean.getAnno_vetrino().intValue() > 0)
            //  where+="NUM_PROVETTE="+bean.getAnno_vetrino()+" AND ";
            where +=
                "      DECODE(Ref_SoRefertocolon1liv1.N_PROVETTA,NULL," +
                "DECODE(Ref_SoRefertocolon1liv.N_PROVETTA,NULL,0,Ref_SoRefertocolon1liv.N_PROVETTA)," +
                "Ref_SoRefertocolon1liv1.n_PROVETTA)=" + bean.getAnno_vetrino() + " AND ";

        //vedo se devo leggere tutti i referti o solo quelli attuali
        if (!bean.isInviti_attivi()) {
            //carico solo quelli attivi
            where += "Ref_SoInvito.ATTIVO=1 AND ";
        }

        //referti pendenti da piu' di x giorni
        if (bean.getPendente() && bean.getGiorni_pendente() != null && bean.getGiorni_pendente().intValue() >= 0) {
            where +=
                "(Ref_SoInvito.DTAPP+" + bean.getGiorni_pendente() +
           ")<SYSDATE AND " +
                //"(COMPLETO IS NULL OR COMPLETO<>1) AND (COMPLETO2 IS NULL OR COMPLETO2<>1) AND ";
                "(DECODE(Ref_SoRefertocolon1liv1.COMPLETO,1,1,DECODE(Ref_SoRefertocolon1liv.COMPLETO,1,1,0)) IS NULL OR " +
           "DECODE(Ref_SoRefertocolon1liv1.COMPLETO,1,1,DECODE(Ref_SoRefertocolon1liv.COMPLETO,1,1,0))<>1) " +
           "AND (Ref_SoRefertocolon2liv.COMPLETO IS NULL OR Ref_SoRefertocolon2liv.COMPLETO<>1) AND ";

        }

        //referti aperti e/o chiusi
        if (bean.getReferti_chiusi() != null) {
            //solo referti ancora aperti
            if ("aperti".equals(bean.getReferti_chiusi()))
                where +=
                    "((Ref_SoRefertocolon1liv.IDREFERTO IS NOT NULL AND " +
                    "DECODE(Ref_SoRefertocolon1liv1.COMPLETO,1,1,DECODE(Ref_SoRefertocolon1liv.COMPLETO,1,1,0))=0) OR " +
                    "(Ref_SoRefertocolon2liv.IDREFERTO IS NOT NULL AND Ref_SoRefertocolon2liv.COMPLETO=0)) AND ";
            //solo referti gia' chiusi
            if ("chiusi".equals(bean.getReferti_chiusi()))
                where +=
                    "(DECODE(Ref_SoRefertocolon1liv1.COMPLETO,1,1,DECODE(Ref_SoRefertocolon1liv.COMPLETO,1,1,0))=1 OR " +
                    "Ref_SoRefertocolon2liv.COMPLETO=1) AND ";
            if ("con 3 livello aperto".equals(bean.getReferti_chiusi()))
                where +=
                    "EXISTS (SELECT * FROM SO_INTERVENTOCOLON I " +
                    "WHERE I.IDREFERTO=Ref_SoRefertocolon2liv.IDREFERTO AND I.COMPLETO=0) AND ";
            if ("con 3 livello chiuso".equals(bean.getReferti_chiusi()))
                where +=
                    "EXISTS (SELECT * FROM SO_INTERVENTOCOLON I " +
                    "WHERE I.IDREFERTO=Ref_SoRefertocolon2liv.IDREFERTO AND I.COMPLETO=1) AND ";

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
                "FUN_GETSUGG_CO(Ref_SoInvito.IDINVITO) LIKE '%" + bean.getSugg_esito() +
                "%' AND FUN_GETSUGG_CO(Ref_SoInvito.IDINVITO) LIKE '%" + bean.getSugg_descrizione() + "%' AND ";
        }


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
        
        //controllo se l'utente ha delle limitazioni sui centri
        try{
            AccessManager.checkPermission("SOLimiteCentri");
            ADFContext adfCtx = ADFContext.getCurrent();
            Map sessionMap = adfCtx.getSessionScope();
            List<Integer> centriAutorizzati = (List<Integer>) sessionMap.get("centriautorizzati");
            String centri = "";
            if (centriAutorizzati != null && !centriAutorizzati.isEmpty()){
                for (int i = 0; i < centriAutorizzati.size(); i++) {
                    centri += "" + centriAutorizzati.get(i) + ",";
                }
                centri = centri.substring(0, centri.length() - 1);
                where += " Ref_SoInvito.idcentroprelievo in (" + centri + ") AND ";
            } else {
                where += " 1=2 AND ";
            }           
        } catch (IllegalAccessException e){                                      
           // non faccio niente
        }

        // 22/12/2009, gestione presenza senza referto
        if (bean.isPres_no_referto()) {
            where +=
                "Ref_SoInvito.CODESITOINVITO = 'S' and Ref_SoRefertocolon1liv.IDREFERTO is null AND Ref_SoRefertocolon2liv.IDREFERTO is null AND ";
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

        if (bean.getCodClassePop() != null && bean.getCodClassePop().length() > 0) {
            where += " Ref_SoSoggScr.CODCLASSEPOP = '"+bean.getCodClassePop()+"' AND ";
        }
        where = where.trim();

        if (where.endsWith("AND"))
            where = where.substring(0, where.length() - 3);

        vo.setWhereClause(where);
        // System.out.println(vo.getQuery());
        vo.executeQuery();

        /*20080701 MOD:stampa elenco soggetti da refertazione*/
        String qry = vo.getQuery();
        qry = qry.replaceAll(":1\\b", "'" + ulss + "'");
        qry = qry.replaceAll(":2\\b", "'" + tpscr + "'");
        String newQry = "select codts, ulss from (" + qry + ")";
        Cnf_selectionBean clauseBean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
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
        
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        
        String tpscr = (String) session.get("scr");

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
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInvitiPerRefertiCOView1Iterator");
        ViewObject vo = voIter.getViewObject();
        if (vo.getEstimatedRowCount() > 0 && vo.getCurrentRow() != null) {
            if (this.tableSearchResult == null)
                this.tableSearchResult = new RichTable();

            Row _row = vo.getCurrentRow();
            RowKeySet selected = new RowKeySetImpl();
            selected.add(Arrays.asList(_row.getKey()));
            this.tableSearchResult.setSelectedRowKeys(selected);
            Utility.gotoTablePageOfSelectedRow(voIter, this.tableSearchResult);
        }
        
        //I00101237 resetto le classi di popolazione
        if ("CO".equals(tpscr)){
            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            
            ViewObject classePopView = am.findViewObject("Sogg_SoCnfClassePopView1");
            classePopView.setWhereClause("tpscr ='" + tpscr + "'");
            classePopView.executeQuery();  
        }
        
    }

    public String onReferto() {
        String tsk = (String) ADFContext.getCurrent().getRequestScope().get("tsk");

        DCBindingContainer bindings;
        if (tsk != null && !tsk.isEmpty())
            bindings = (DCBindingContainer) findBindingContainer(tsk);
        else
            bindings = (DCBindingContainer) getBindings();

        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInvitiPerRefertiCOView1Iterator");
        ViewObject vo = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) vo.getApplicationModule();
        Row r = vo.getCurrentRow();

        try {
            AccessManager.checkPermission("SORefertazione");

            if (r == null)
                throw new Exception("Nessun invito selezionato per il referto");
            //controllo il livello e se esiste gia' un referto del livello corretto
            Map session = ADFContext.getCurrent().getSessionScope();
            Integer livello = (Integer) session.get("ref_livello");
            if (livello == null)
                throw new Exception("Nessun invito selezionato per il referto");

            //secondo livello
            if (livello.intValue() == 2) {
                AccessManager.checkPermission("SORef2Liv");

                //viewObject dipendente in base al valore di idinvito (che viene impostato in automatico)
                ViewObject ref = am.findViewObject("Ref_SoRefertocolon2livView1");
                //se il referto c'e' gia' non devo fare niente, solo visualizzarlo
                if (ref.getRowCount() == 0) {

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
                ViewObject primol = am.findViewObject("Ref_SoInvito1LivCOView1");
                String where;
                try {

                    primol.setWhereClauseParams(new Object[] { r.getAttribute("Ulss"), r.getAttribute("Tpscr"),
                                                               r.getAttribute("Codts") });
                    where =
                        "DTAPP<TO_DATE('" +
                        DateUtils.dateToString(((oracle.jbo.domain.Date) r.getAttribute("Dtapp")).getValue()) + "','" +
                        DateUtils.DATE_PATTERN + "')";
                    primol.setWhereClause(where);
                    primol.executeQuery();
                } catch (Exception ex) {
                    //se non riesco a fare la quesry corretta, allora meglio non trovare niente
                    where = "IDINVITO IS NULL";
                    primol.setWhereClause(where);
                    primol.executeQuery();
                }
                primol.first();


            } else //primo livello
            {
                //viewObject dipendente in base al valore di idinvito (che viene impostato in automatico)
                ViewObject ref = am.findViewObject("Ref_SoRefertocolon1livView1");
                //se il referto c'e' gia' non devo fare niente, solo visualizzarlo
                if (ref.getRowCount() == 0) {

                    //se l'invito non ha un esito adeguato, allora non posso creare
                    //un referto
                    //se non ha esito va bene
                    if (r.getAttribute("Codesitoinvito") != null) {
                        boolean found = false;
                        for (int i = 0; i < ConfigurationConstants.CODICI_ESITI_REFERTABILI_CO.length; i++) {
                            if (ConfigurationConstants.CODICI_ESITI_REFERTABILI_CO[i].equals(r.getAttribute("Codesitoinvito"))) {
                                found = true;
                                break;
                            }
                        }

                        if (!found)
                            throw new Exception("L'invito selezionato non ha referto (esito=" +
                                                r.getAttribute("Codesitoinvito") + ")");
                    }
                } //fine if getrowcount==0

                // filtro le accettzioni per motivi di performance
                vo = am.findViewObject("Ref_SoAccColon1livView1");
                vo.setWhereClause("CODTS='" + r.getAttribute("Codts") + "' AND ULSS='" + r.getAttribute("Ulss") + "'");
                vo.executeQuery();

            }

            return ("livello" + livello + "Co");

        } catch (Exception ex) {
            //   ex.printStackTrace();
            am.doRollback("Ref_SoInvitiPerRefertiCOView1");
            this.handleException("Impossibile visualizzare il referto: " + ex.getMessage());

        }
        return null;
    }

    public void setTableSearchResult(RichTable tableSearchResult) {
        this.tableSearchResult = tableSearchResult;
    }

    public RichTable getTableSearchResult() {
        return tableSearchResult;
    }

    public void onChangeCodClassePop(ValueChangeEvent valueChangeEvent) {
        String codClassePop = "";
        
        if (valueChangeEvent!=null)
            codClassePop = (String) valueChangeEvent.getNewValue(); 
        
        if (codClassePop!=null && !"".equals(codClassePop)){
            PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
            bean.setCodClassePop(codClassePop);
        }
    }
}
