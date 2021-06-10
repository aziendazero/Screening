package view.accettazione.cardio;

import insiel.dataHandling.DateUtils;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.AccCa_AppModule;

import model.accettazione.AccCa_RicInvitiViewRow;
import model.accettazione.AccCa_RicInvitiViewRowImpl;

import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.AccCa_RicParam;
import model.datacontrol.Ref_SearchBean;
import model.datacontrol.Sogg_RicParam;

import model.inviti.DatiRichiamo;
import model.inviti.GeneratoreInviti;
import model.inviti.InvitoUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.UIXTable;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import view.commons.AccUtils;
import view.commons.AppConstants;
import view.commons.PageDescriptor;
import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class AccCa_ricercaAction extends Parent_DataForwardAction {

    private RichPopup dateWarning;
    private RichPopup confirm;

    protected void setAppModule() {
        this.amName = "AccCa_AppModule";
    }

    private RichTable resultTable;

    private RichForm formSearch;

    public void setFormSearch(RichForm formSearch) {
        this.formSearch = formSearch;
    }

    public RichForm getFormSearch() {
        if (formSearch == null)
            findForward();

        return formSearch;
    }

    public void setResultTable(RichTable resultTable) {
        this.resultTable = resultTable;
    }

    public RichTable getResultTable() {
        return resultTable;
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    protected void findForward() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        AccCa_RicParam bean =
            (AccCa_RicParam) BindingContext.getCurrent().findDataControl("AccCa_RicParamDataControl").getDataProvider();
        AccCa_AppModule am =
            (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Boolean fromDett = (Boolean) session.get("fromDett");
        if (fromDett != null && fromDett) {
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCa_RicInvitiView1Iterator");
            ((AccCa_AppModule) voIter.getViewObject().getApplicationModule()).doRollback("AccCa_RicInvitiView1");
            ViewObject vo = voIter.getViewObject();
            ViewHelper.queryAndRestoreCurrentRow(vo);

            RowKeySet selected = (RowKeySet) ADFContext.getCurrent().getSessionScope().get("selectedRows");
            if (selected == null)
                selected = new RowKeySetImpl();

            if (this.resultTable == null)
                this.resultTable = new RichTable();

            AccCa_RicInvitiViewRowImpl _row = (AccCa_RicInvitiViewRowImpl) vo.getCurrentRow();
            _row.setSelezionato(Boolean.TRUE);
            selected.clear();
            selected.add(Arrays.asList(_row.getKey()));
            this.resultTable.setSelectedRowKeys(selected);

            Utility.gotoTablePageOfSelectedRow(voIter, this.resultTable);
            ADFContext.getCurrent().getViewScope().put("numSelected", 1);
        } else {
            boolean nav = bean.getNavigazione().booleanValue();
            if (nav) {
                bean.queryInviti();
                //bean.setNavigazione(Boolean.FALSE);
            } else {
                bean.resetCampi();

                ViewObject vo = am.findViewObject("AccCa_RicInvitiView1");
                vo.setWhereClause("1=2");
                vo.executeQuery();
            }
        }
        session.put("chooseEsito", Boolean.FALSE);

        ViewObject voCt = am.findViewObject("AccCa_SelCentriView1");
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");

        voCt.setWhereClauseParams(new Object[] { ulss, tpscr });

        voCt.executeQuery();
    }

    // Override
    protected void beforeNavigate(String dest) throws Exception {
        Map session = ADFContext.getCurrent().getSessionScope();
        session.remove("fromDett");
        session.remove("selectedRows");

        AccCa_AppModule am =
            (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("AccCa_RicInvitiView1");
        Row r = vo.getCurrentRow();
        if (r != null) {

            // Navigazione verso ricerca soggetto
            if (dest.equals("iniSogg")) {
                Sogg_RicParam beanSogg =
                    (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                beanSogg.resetCampi();
                beanSogg.setTessSan((String) r.getAttribute("Codts"));
                beanSogg.setCognome((String) r.getAttribute("Cognome"));
                beanSogg.setNome((String) r.getAttribute("Nome"));
                
                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanSogg.setChiave(chiave);
                    }
                }
                
                beanSogg.setInEta(0);
                beanSogg.setNavigazione(Boolean.TRUE);
            }

            // Navigazione verso la refertazione
            else if (dest.startsWith("to_ref")) {
                Integer livello = (Integer) r.getAttribute("Livello");
                Ref_SearchBean beanRef =
                    (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
                beanRef.reset();
                beanRef.setCognome((String) r.getAttribute("Cognome"));
                beanRef.setNome((String) r.getAttribute("Nome"));
                
                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanRef.setChiave(chiave);
                    }
                }
                
                beanRef.setCodts((String) r.getAttribute("Codts"));
                beanRef.setCentro_prel(((Integer) r.getAttribute("Idcentroprelievo")).intValue());
                beanRef.setLivello(livello.toString());
                beanRef.setEmptyQuery(false);
            }
        }

        // Se sto uscendo dall'accettazione resetto il bean
        if (!dest.startsWith("acc_to")) {
            AccCa_RicParam bean =
                (AccCa_RicParam) BindingContext.getCurrent().findDataControl("AccCa_RicParamDataControl").getDataProvider();
            bean.resetCampi();
        }
    }

    public void onCerca() {
        AccCa_RicParam bean =
            (AccCa_RicParam) BindingContext.getCurrent().findDataControl("AccCa_RicParamDataControl").getDataProvider();

        //parametri di ricerca minimi
        boolean paramOk = false;
        Map session = ADFContext.getCurrent().getSessionScope();
        if ((bean.getCognome() != null) && (bean.getCognome().length() > 1)) {
            paramOk = true;
        } else if ((bean.getNome() != null) && (bean.getNome().length() > 1)) {
            paramOk = true;
        } else if (session.get("dtInvito") != null) {
            paramOk = true;
        } else if (bean.getCodIdSogg() != null && bean.getCodIdSogg().length() > 0) {
            paramOk = true;
        } else if (bean.getTessSan() != null && bean.getTessSan().length() > 0) {
            paramOk = true;
        } else if (bean.getCodiceDocumento() != null && bean.getCodiceDocumento().length() > 0) {
            paramOk = true;
        }

        if (!paramOk) {
            this.handleException("Parametri di ricerca insufficienti.Impostare almeno uno di questi campi: Cognome (con almeno due caratteri), Nome (con almeno due caratteri)," +
                                 "Tessera sanitaria, Data invito, Codice richiesta, Codice identificativo", null);

        } else {
            bean.queryInviti();

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCa_RicInvitiView1Iterator");
            ViewObject vo = voIter.getViewObject();
            if (vo.getEstimatedRowCount() > 0) {
                AccCa_RicInvitiViewRowImpl _row = (AccCa_RicInvitiViewRowImpl) vo.first();
                _row.setSelezionato(Boolean.TRUE);
                RowKeySet selected = new RowKeySetImpl();
                selected.add(Arrays.asList(_row.getKey()));
                this.resultTable.setSelectedRowKeys(selected);
                ADFContext.getCurrent().getViewScope().put("numSelected", 1);
                Utility.gotoTablePageOfSelectedRow(voIter, this.resultTable);
            }
        }
    }

    public void onReimp(ActionEvent actionEvent) {
        AccCa_RicParam bean =
            (AccCa_RicParam) BindingContext.getCurrent().findDataControl("AccCa_RicParamDataControl").getDataProvider();
        bean.resetCampi();
    }

    protected void doRollback() {
        AccCa_AppModule am =
            (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
        String[] views = { "AccCa_RicInvitiView1" };
        am.doRollback(views);
    }

    public String onAnagrafica() {
        boolean navOK = this.navDett();
        if (navOK) {
            AccCa_AppModule am =
                (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
            ViewObject voRic = am.findViewObject("AccCa_RicInvitiView1");
            AccCa_RicInvitiViewRow cInv = (AccCa_RicInvitiViewRow) voRic.getCurrentRow();
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            java.util.Date dtInvito = (java.util.Date) session.get("dtInvito");
            Sogg_RicParam beanSogg =
                (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
            beanSogg.resetCampi();
            beanSogg.setTessSan((String) cInv.getCodts());
            beanSogg.setCognome((String) cInv.getCognome());
            beanSogg.setNome((String) cInv.getNome());
            beanSogg.setInEta(0);
            session.put("dtInvito", dtInvito);
            beanSogg.querySogg();

            Sogg_AppModule amSogg =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            ViewObject vo = amSogg.findViewObject("Sogg_RicercaView1");
            vo.setCurrentRow(vo.first());
            
            session.put("showTabs", Boolean.TRUE);
            session.put("anagEsiste", Boolean.TRUE);

            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");

            // setto anagrafica corrente
            ViewObject voAnag = amSogg.findViewObject("Sogg_SoSoggettoView1");
            voAnag.setWhereClause("CODTS = '" + cInv.getCodts() + "' AND ULSS='"+ulss+"'");
            //voAnag.setWhereClauseParams(new Object[] { (String) cInv.getCodts(), ulss });
            voAnag.executeQuery();

            Row fRow = voAnag.first();
            voAnag.setCurrentRow(fRow);

            ViewObject voSoggScr = amSogg.findViewObject("Sogg_SoSoggScrView1");
            String whScr =
                "Sogg_SoSoggScr.CODTS = '" + (String) cInv.getCodts() + "' and Sogg_SoSoggScr.ULSS = '" + ulss +
                "' and Sogg_SoSoggScr.TPSCR = '" + tpscr + "'";
            voSoggScr.setWhereClause(whScr);
            voSoggScr.executeQuery();

            // filtro distretti
            String comRes = (String) fRow.getAttribute("Codcomres");

            session.put("LINK_ACC", Boolean.TRUE);
            session.put("invitoPresente", Boolean.TRUE);

            // Imposto le informazioni per il ritorno a questa pagina
            PageDescriptor page = new PageDescriptor("AccCa_ricerca");
            page.setBackTitle("Torna all'accettazione");
            page.setAction("acc_toCardio");
            ADFContext.getCurrent().getSessionScope().put(AppConstants.FROM_PAGE, page);


            return "goAnag";
        }

        return "error";
    }

    public String onSegnaAss(List rowIndexes) {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String user = (String) sess.get("user");
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");

        if (rowIndexes.size() > 0) {
            AccCa_AppModule am =
                (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
            ViewObject voRic = am.findViewObject("AccCa_RicInvitiView1");
            ViewObject voInvito = am.findViewObject("AccCa_SoInvitoView1");

            // Journaling
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                am.preapareJournaling(user, ulss, tpscr);
            }

            boolean errore = false;

            for (int i = 0; i < rowIndexes.size(); i++) {
                int selectedRowIndex = (Integer) rowIndexes.get(i);
                AccCa_RicInvitiViewRow cInv = (AccCa_RicInvitiViewRow) voRic.getRowAtRangeIndex(selectedRowIndex);
                voRic.setCurrentRow(cInv);

                String esito = cInv.getCodesitoinvito();
                if (!esito.equals(ConfigurationConstants.CODICE_ESITO_NON_DISPONIBILE))
                    continue;

                Integer idInv = cInv.getIdinvito().intValue();
                voInvito.setWhereClauseParam(0, idInv);
                voInvito.executeQuery();
                Row fInv = voInvito.first();
                fInv.refresh(Row.REFRESH_CONTAINEES);

                fInv.setAttribute("Codesitoinvito", ConfigurationConstants.CODICE_ESITO_MANCATA_PRESENZA);
                fInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                fInv.setAttribute("Opmodifica", ADFContext.getCurrent().getSessionScope().get("user"));

                GeneratoreInviti gen = new GeneratoreInviti(am);
                try {
                    gen.updateRoundIndiv(fInv);
                    AccUtils.insRichiamoCA(am, fInv);
                } catch (Exception ex) {
                    String msg = ex.getMessage();
                    this.handleException(msg, null);
                    errore = true;
                    break;
                }
            }

            if (errore) {
                this.doRollback();
            } else {
                am.getTransaction().commit();
                AccUtils.requeryElencoCardio();
            }
        }


        return "assignedass";
    }

    public String onSegnapres(List rowIndexes) {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String user = (String) sess.get("user");
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");

        if (rowIndexes.size() > 0) {
            AccCa_AppModule am =
                (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
            ViewObject voRic = am.findViewObject("AccCa_RicInvitiView1");
            ViewObject voInvito = am.findViewObject("AccCa_SoInvitoView1");

            // Journaling
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                am.preapareJournaling(user, ulss, tpscr);
            }

            boolean errore = false;

            for (int i = 0; i < rowIndexes.size(); i++) {
                int selectedRowIndex = (Integer) rowIndexes.get(i);

                AccCa_RicInvitiViewRow cInv = (AccCa_RicInvitiViewRow) voRic.getRowAtRangeIndex(selectedRowIndex);
                voRic.setCurrentRow(cInv);
                Integer idInv = (Integer) cInv.getIdinvito();

                voInvito.setWhereClauseParam(0, idInv);

                voInvito.executeQuery();
                Row fInv = voInvito.first();
                fInv.refresh(Row.REFRESH_CONTAINEES);
                fInv.setAttribute("Codesitoinvito", ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO);
                fInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                fInv.setAttribute("Opmodifica", ADFContext.getCurrent().getSessionScope().get("user"));

                GeneratoreInviti gen = new GeneratoreInviti(am);
                try {
                    gen.updateRoundIndiv(fInv);
                    AccUtils.insRichiamoCA(am, fInv);
                } catch (Exception ex) {
                    String msg = ex.getMessage();
                    this.handleException(msg, null);
                    errore = true;
                    break;
                }
            }

            if (errore) {
                this.doRollback();
            } else {
                am.getTransaction().commit();
                AccUtils.requeryElencoCardio();
            }
        }

        return "assignedpres";
    }

    public void onChiudig(ActionEvent actionEvent) {
        closeConfirm();
        AccCa_AppModule am =
            (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
        AccCa_RicParam bean =
            (AccCa_RicParam) BindingContext.getCurrent().findDataControl("AccCa_RicParamDataControl").getDataProvider();
        Integer idCt = bean.getIdCprel();
        Map req = ADFContext.getCurrent().getRequestScope();
        Map session = ADFContext.getCurrent().getSessionScope();
        java.util.Date dtInvito = (java.util.Date) session.get("closeToDate");
        if (dtInvito == null) {
            dtInvito = (java.util.Date) session.get("dtInvito");
        }

        String livello = bean.getLivello();
        String esito = "N";
        Integer ctInvito = idCt.intValue();
        String user = (String) session.get("user");
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // Journaling
        Boolean flag = (Boolean) session.get("flagAbilJournal");
        if (flag != null && flag.booleanValue()) {
            am.preapareJournaling(user, ulss, tpscr);
        }

        DatiRichiamo datiRich = InvitoUtils.getDatiRichiamo(am, livello, esito, ctInvito, ulss, tpscr);

        //SE IL COGNOME HA DENTRO UN APOSTROFO LA STRINGA VA ALTERATA
        String userSql = user.replaceAll("'", "''");

        String istrUpd = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        if (datiRich != null) {
            Integer detCtRich = datiRich.getDetCtRich();
            Integer ctRich = datiRich.getIdCentroRichiamo();
            Integer ggRich = datiRich.getGgRichiamo();
            String tpRich = datiRich.getTpRichiamo();

            if (ggRich == null) {
                ggRich = new Integer(0);
            }

            if (detCtRich.intValue() == 1) {
                istrUpd =
                    "UPDATE SO_INVITO SET " + "CODESITOINVITO = 'N', " + "IDCENTRORICHIAMO = " + ctRich.toString() +
                    ", " + "TPRICHIAMO = '" + tpRich + "', " + "DTRICHIAMO = DTAPP + " + ggRich.toString() + ", " +
                    "OPMODIFICA = '" + userSql + "', " + "DTULTIMAMOD = sysdate " +
                    "where CODESITOINVITO = '?' and IDCENTROPRELIEVO = " + idCt.intValue() + " and DTAPP < to_date('" +
                    sdf.format(dtInvito) + "', 'YYYYMMDD') + 1";
            } else {
                istrUpd =
                    "UPDATE SO_INVITO SET " + "CODESITOINVITO = 'N', " +
                    "IDCENTRORICHIAMO = FUN_ULTCPREL1LIV(CODTS,ULSS,TPSCR), " + "TPRICHIAMO = '" + tpRich + "', " +
                    "DTRICHIAMO = DTAPP + " + ggRich.toString() + ", " + "OPMODIFICA = '" + userSql + "', " +
                    "DTULTIMAMOD = sysdate " + "where CODESITOINVITO = '?' and IDCENTROPRELIEVO = " + idCt.intValue() +
                    " and DTAPP < to_date('" + new SimpleDateFormat("dd/MM/yyyy").format(dtInvito) +
                    "','dd/MM/yyyy') + 1";
            }
        } else {
            // l'esito non prevede richiamo
            istrUpd =
                "UPDATE SO_INVITO SET " + "CODESITOINVITO = 'N', " + "IDCENTRORICHIAMO = null, " +
                "TPRICHIAMO = null, " + "DTRICHIAMO = null, " + "OPMODIFICA = '" + userSql + "', " +
                "DTULTIMAMOD = sysdate " + "where CODESITOINVITO = '?' and IDCENTROPRELIEVO = " + idCt.intValue() +
                " and DTAPP < to_date('" + sdf.format(dtInvito) + "', 'YYYYMMDD') + 1";
        }

        ViewObject voSelCt = am.findViewObject("AccCa_SelCentriView1");
        Row[] rows = voSelCt.getFilteredRows("Idcentro", idCt);
        Date dtChiusi = (Date) rows[0].getAttribute("Dtchperiodo");
        if (dtChiusi != null) {
            istrUpd += " and DTAPP > to_date('" + DateUtils.dateToString(dtChiusi.dateValue()) + "','dd/MM/yyyy')";
        }

        try {
            am.getTransaction().executeCommand(istrUpd);

            String updCt =
                "UPDATE SO_CENTRO_PREL_REF " + "SET DTCHPERIODO = to_date('" +
                sdf.format(dtInvito) + "', 'YYYYMMDD') " + "WHERE IDCENTRO = " +
                idCt.intValue();

            am.getTransaction().executeCommand(updCt);

            am.getTransaction().commit();

            ViewObject voRic1 = am.findViewObject("AccCa_RicInvitiView1");
            voRic1.executeQuery();
            voSelCt.executeQuery();
        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
            this.doRollback();
        }
    }

    private List getSelected() {
        //getting table reference from binding
        UIXTable table = getResultTable();
        //getting iterator to iterate over selected row keys
        Iterator selection = table.getSelectedRowKeys().iterator();
        List rowIndexes = new ArrayList();
        while (selection.hasNext()) {
            Object rowKey = selection.next();
            //setting the rowKey to table one by one
            table.setRowKey(rowKey);
            int index = table.getRowIndex();
            //adding selected row indexes to a List
            rowIndexes.add(index);
            System.out.println(".... Row row=" + table.getRowData() + "\t" + index);
        }
        return rowIndexes;
    }

    public void onSegnapres(ActionEvent actionEvent) {
        List rowIndexes = getSelected();

        if (((UIComponent) actionEvent.getSource()).getId().equals("btpres"))
            onSegnapres(rowIndexes);
        else if (((UIComponent) actionEvent.getSource()).getId().equals("btass"))
            onSegnaAss(rowIndexes);
    }

    public boolean setCurrIfOneSelected() {
        List rowIndexes = getSelected();

        if (rowIndexes.size() == 1) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCa_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            AccCa_RicInvitiViewRow cInv =
                (AccCa_RicInvitiViewRow) voRic.getRowAtRangeIndex((Integer) rowIndexes.get(0));
            voRic.setCurrentRow(cInv);

            return true;
        } else {
            return false;
        }
    }

    public boolean navDett() {
        boolean oneSel = this.setCurrIfOneSelected();
        if (oneSel) {
            AccUtils.procInvCorrenteCardio();
            return true;
        } else {
            String msg =
                "Per poter accedere alle informazioni di dettaglio e' necessario " +
                "che risulti selezionato uno e un solo invito";
            this.handleException(msg, null);
            return false;
        }
    }

    public void selectionListener(SelectionEvent selectionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCa_RicInvitiView1Iterator");
        ViewObject vo = voIter.getViewObject();

        RowKeySet selectedRows = getResultTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        System.out.println("----------------------> " + numSelectedRows);

        vo.reset();
        RowSetIterator rs = vo.createRowSetIterator(null);
        rs.reset();
        AccCa_RicInvitiViewRowImpl row;
        while (rs.hasNext()) {
            row = (AccCa_RicInvitiViewRowImpl) rs.next();
            row.setSelezionato(Boolean.FALSE);
        }
        rs.closeRowSetIterator();

        Iterator selectedIter = selectedRows.iterator();
        while (selectedIter.hasNext()) {
            Key key = (Key) ((List) selectedIter.next()).get(0);
            Row[] _rs = vo.findByKey(key, 1);
            if (_rs != null && _rs.length > 0) {
                AccCa_RicInvitiViewRowImpl _row = (AccCa_RicInvitiViewRowImpl) _rs[0];
                _row.setSelezionato(Boolean.TRUE);

                if (numSelectedRows == 1)
                    vo.setCurrentRow(_row);
            }
        }

        ADFContext.getCurrent().getViewScope().put("numSelected", numSelectedRows);
    }
    
    public void checkDate(ValueChangeEvent valueChangeEvent) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        java.util.Date dtInvito = (java.util.Date) valueChangeEvent.getNewValue();
        boolean isAfterToday = dtInvito != null && dtInvito.after(today.getTime());
        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("todayCheckDate", isAfterToday);
        if (isAfterToday) {
            dtInvito = today.getTime();
        }
        session.put("closeToDate", dtInvito);
    }

    public void setDateWarning(RichPopup dateWarning) {
        this.dateWarning = dateWarning;
    }

    public RichPopup getDateWarning() {
        return dateWarning;
    }
    
    public void closeDateWarning() {
        if (dateWarning != null) {
            dateWarning.hide();
        }
    }

    public void openConfirm() {
        closeDateWarning();
        if (confirm != null) {
            confirm.show(new RichPopup.PopupHints());
        }
    }

    public String closeConfirm() {
        if (confirm != null) {
            confirm.hide();
        }
        return "closed";
    }
    
    public void setConfirm(RichPopup confirm) {
        this.confirm = confirm;
    }

    public RichPopup getConfirm() {
        return confirm;
    }

}
