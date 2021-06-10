package view.referto;

import insiel.dataHandling.DateUtils;
import insiel.dataHandling.ObjectTransformationUtils;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.faces.event.ActionEvent;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.Ref_SearchBean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.util.Utility;

public class RefCa_ricercaAction extends RefCa_DataForwardAction {
    private RichForm frmSearch;
    private RichTable tableSearchResult;

    public RefCa_ricercaAction() {
    }

    public void setFrmSearch(RichForm frmSearch) {
        this.frmSearch = frmSearch;
    }

    public RichForm getFrmSearch() {
        if (frmSearch == null)
            findForward();

        return frmSearch;
    }

    public String onCerca() {
        try {
            search();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException("Impossibile eseguire la ricerca: " + ex.getMessage());
        }
        return "searched";
    }

    public static void search() throws Exception {
        ApplicationModule am =
            (ApplicationModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        Ref_SearchBean bean =
            (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String where = "(DA_CONFERMARE IS NULL OR DA_CONFERMARE = 0) AND ";

        //se si tratta di una query vuota, resetto tutto
        if (bean.isEmptyQuery()) {
            System.out.println("#####################a'a' RESET");
            bean.reset();
            bean.setEmptyQuery(false);
            vo.setWhereClause("1 = 2");
            vo.executeQuery();

            String newQry = "select null codts, null ulss from dual where 1 = 2";
            Cnf_selectionBean clauseBean =
                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            clauseBean.setInClause(newQry);
            clauseBean.setNote(null);
            return;
        }

        boolean paramOk = false;

        if ((bean.getCognome() != null) && (bean.getCognome().length() > 1)) {
            paramOk = true;
        } else if ((bean.getNome() != null) && (bean.getNome().length() > 1)) {
            paramOk = true;
        } else if (bean.getAnno_nascita() != null && bean.getMese_nascita() != null) {
            paramOk = true;
        } else if (bean.getCodts() != null && bean.getCodts().length() > 0) {
            paramOk = true;
        } else if ((bean.getPrelievo_al().equals("il")) &&
                   (bean.getPrelievo_dal() != null)) {
            paramOk = true;
        } else if (bean.getPrelievo_dal() != null) {
            if (bean.getPrelievo_al().equals("dopo il")){
                //try {
                    //testo il formato
                    Date inputDate = bean.getPrelievo_dal();

                    //la data non deve essere antecedente a un mese fa
                    Calendar today = Calendar.getInstance();
                    today.add(Calendar.MONTH, -1);
                    if (inputDate.after(today.getTime())) {
                        paramOk = true;
                    } else { //oppure ci deve essere almeno un secondo parametro impostato
                        if (bean.getParamNumber() >= 2) {
                            paramOk = true;
                        }
                    }
                //} catch (ParseException pex) {
                //}
            }else if (bean.getPrelievo_al().equals("prima del")) { //e' possibile solo se e' impostato almeno un altro parametro
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
                                "Tessera sanitaria, Data di nascita, Data invito il, oppure data invito prima del/dopo il assieme ad un altro filtro");
        }

        AccessManager.checkPermission("SORefertazione");

        //seleziono solo gli inviti che possono avere un referto
        where +=
            "Ref_SoInvito.CODESITOINVITO IN (" +
            ObjectTransformationUtils.arrayToSQLString(ConfigurationConstants.CODICI_ESITI_REFERTABILI) + ") AND ";

        //centro di referto
        if (bean.getCentro_prel() != null && bean.getCentro_prel().intValue() > 0)
            where += "Ref_SoInvito.IDCENTROPRELIEVO=" + bean.getCentro_prel() + " AND ";

        //data test
       // try {
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
                    "Ref_SoInvito.DTAPP" + op + "TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getPrelievo_dal()) + "','" + DateUtils.DATE_PATTERN +
                    "') AND ";
            }
        //} catch (ParseException pex) {
         //   throw new Exception("La data dell'invito non e' espressa nel formato corretto (" + DateUtils.DATE_PATTERN +
         //                       ")");
        //}

        //nome e cognome
        if (bean.getCognome() != null && bean.getCognome().length() > 0)
            where +=
                "Ref_SoSoggetto.COGNOME LIKE '" + ViewHelper.replaceApostrophe(bean.getCognome().toUpperCase()) +
                "%' AND ";

        if (bean.getNome() != null && bean.getNome().length() > 0)
            where +=
                "Ref_SoSoggetto.NOME LIKE '" + ViewHelper.replaceApostrophe(bean.getNome().toUpperCase()) + "%' AND ";

        //data di nascita
        if (bean.getGiorno_nascita() != null && bean.getGiorno_nascita().intValue() > 0)
            where += "TO_CHAR(Ref_SoSoggetto.DATA_NASCITA,'dd')=LPAD('" + bean.getGiorno_nascita() + "',2,'0') AND ";

        if (bean.getMese_nascita() != null && bean.getMese_nascita().intValue() > 0)
            where += "TO_CHAR(Ref_SoSoggetto.DATA_NASCITA,'MM')=LPAD('" + bean.getMese_nascita() + "',2,'0') AND ";

        if (bean.getAnno_nascita() != null && bean.getAnno_nascita().intValue() > 0)
            where += "TO_CHAR(Ref_SoSoggetto.DATA_NASCITA,'yyyy')='" + bean.getAnno_nascita() + "' AND ";

        //tessera sanitaria
        if (bean.getCodts() != null && bean.getCodts().length() > 0)
            where += "Ref_SoInvito.CODTS='" + bean.getCodts() + "' AND ";

        //vedo se devo leggere tutti i referti o solo quelli attuali
        if (!bean.isInviti_attivi()) {
            //carico solo quelli attivi
            where += "Ref_SoInvito.ATTIVO=1 AND ";
        }

        //referti pendenti da piu' di x giorni
        if (bean.getPendente() && bean.getGiorni_pendente() != null && bean.getGiorni_pendente().intValue() >= 0) {
            where +=
                "(Ref_SoInvito.DTAPP + " + bean.getGiorni_pendente() + ") < SYSDATE AND " +
                "(RefCa_SoRefertocardio.COMPLETO IS NULL OR RefCa_SoRefertocardio.COMPLETO <> 1) AND ";
        }

        //referti aperti e/o chiusi
        if (bean.getReferti_chiusi() != null) {
            //solo referti ancora aperti
            if ("aperti".equals(bean.getReferti_chiusi()))
                where += "RefCa_SoRefertocardio.COMPLETO = 0 AND ";

            //solo referti gia' chiusi
            if ("chiusi".equals(bean.getReferti_chiusi()))
                where += "RefCa_SoRefertocardio.COMPLETO = 1 AND ";
        }

        //suggerimento conclusivo
        if (bean.getSugg_esito() != null && bean.getSugg_descrizione() != null) {
            where +=
                "FUN_GETSUGG_CA(Ref_SoInvito.IDINVITO) LIKE '%" + bean.getSugg_esito() +
                "%' AND FUN_GETSUGG_CA(Ref_SoInvito.IDINVITO) LIKE '%" + bean.getSugg_descrizione() + "%' AND ";
        }

        //filtro sui centri dell'utente
        Number c1 = (Number) ADFContext.getCurrent().getSessionScope().get("centro1liv");
        String in = (String) ADFContext.getCurrent().getSessionScope().get("elenco_centri");
        if (c1 != null) {
            //filtro i soggetti che sono associati ad un centro dell'utente oppure hanno
            //l'ultimo invito in tale centro
            where += " (idcentroprelievo in " + in;
            if (c1 != null)
                where += " or idcentro1liv = " + c1;

            where += ") AND ";
        }

        // gestione presenza senza referto
        if (bean.isPres_no_referto()) {
            where += "Ref_SoInvito.CODESITOINVITO = 'S' and RefCa_SoRefertocardio.IDREFERTO is null";
        }

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

        // Salvo la query per eventuale stampa della lista soggetti
        String qry = vo.getQuery();
        qry = qry.replaceFirst("ULSS = :1", "ULSS = '" + ulss + "'");
        qry = qry.replaceFirst("TPSCR = :2", "TPSCR = '" + tpscr + "'");
        String newQry = "select codts, ulss from (" + qry + ")";
        Cnf_selectionBean clauseBean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        clauseBean.setInClause(newQry);
        clauseBean.setNote("Elenco soggetti risultato della ricerca");

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
        if ("cerca".equals(event)){
            System.out.println("----------------------------------------------> PARAM EVENT CERCA");
            this.onCerca();
        }
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInvitiPerRefertiCAView1Iterator");
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
    
    public void reset(ActionEvent actionEvent){
        Ref_SearchBean bean =
            (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
    
        bean.reset();
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc3'])");
    }

    public void setTableSearchResult(RichTable tableSearchResult) {
        this.tableSearchResult = tableSearchResult;
    }

    public RichTable getTableSearchResult() {
        return tableSearchResult;
    }
}
