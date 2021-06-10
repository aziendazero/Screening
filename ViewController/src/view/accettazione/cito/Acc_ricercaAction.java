package view.accettazione.cito;

import insiel.dataHandling.DateUtils;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.accettazione.Acc_RicInvitiViewRowImpl;
import model.accettazione.common.Acc_RicInvitiViewRow;

import model.common.Acc_AppModule;
import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.Acc_RicParam;
import model.datacontrol.Sogg_RicParam;

import model.inviti.DatiRichiamo;
import model.inviti.GeneratoreInviti;
import model.inviti.InvitoUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
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

public class Acc_ricercaAction extends Parent_DataForwardAction {

    private RichPopup dateWarning;
    private RichPopup confirm;

    public Acc_ricercaAction() {
        super();
    }

    protected void setAppModule() {
        // TODO:  Implement this view.commons.Parent_DataForwardAction abstract method
        this.amName = "Acc_AppModule";
    }

    private RichForm formSearch;
    private RichPopup choosePrel;
    private RichTable accTable;

    public void setChoosePrel(RichPopup choosePrel) {
        this.choosePrel = choosePrel;
    }

    public RichPopup getChoosePrel() {
        return choosePrel;
    }

    public void setAccTable(RichTable accTable) {
        this.accTable = accTable;
    }

    public RichTable getAccTable() {
        return accTable;
    }

    public void setFormSearch(RichForm formSearch) {
        this.formSearch = formSearch;
    }

    public RichForm getFormSearch() {
        if (formSearch == null)
            findForward();
        return formSearch;
    }

    protected void findForward() {
        Acc_RicParam bean =
            (Acc_RicParam) BindingContext.getCurrent().findDataControl("Acc_RicParamDataControl").getDataProvider();
        
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        ADFContext.getCurrent().getSessionScope().remove(AppConstants.FROM_PAGE);

        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        Acc_AppModule am = (Acc_AppModule) voIter.getViewObject().getApplicationModule();

        //torno dal dettaglio?
        Boolean fromDett = (Boolean) session.get("fromDett");
        if (fromDett != null && fromDett) {
            am.doRollback("Acc_RicInvitiView1");
            ViewObject vo = voIter.getViewObject();
            ViewHelper.queryAndRestoreCurrentRow(vo);

            RowKeySet selected = (RowKeySet) ADFContext.getCurrent().getSessionScope().get("selectedRows");
            if (selected == null)
                selected = new RowKeySetImpl();

            if (this.accTable == null)
                this.accTable = new RichTable();

            Acc_RicInvitiViewRowImpl _row = (Acc_RicInvitiViewRowImpl) vo.getCurrentRow();
            _row.setSelezionato(Boolean.TRUE);
            selected.clear();
            selected.add(Arrays.asList(_row.getKey()));
            this.accTable.setSelectedRowKeys(selected);

            Utility.gotoTablePageOfSelectedRow(voIter, this.accTable);
            ADFContext.getCurrent().getViewScope().put("numSelected", 1);
        } else {

            boolean nav = bean.getNavigazione().booleanValue();
            if (nav) {
                bean.queryInviti();
               // bean.setNavigazione(Boolean.FALSE);
            } else {
                bean.resetCampi();

                ViewObject vo = voIter.getViewObject();
                vo.setWhereClause("1=2");
                vo.executeQuery();
            }

            String livello = bean.getLivello();
            ViewObject voCt = am.findViewObject("Acc_SelCprelView1");
            String tpscr = (String) session.get("scr");
            String ulss = (String) session.get("ulss");
            /*MOD20071121
            String whct = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" +
              " and tipocentro = " + livello.toString();
              */
            voCt.setWhereClauseParams(new Object[] { ulss, tpscr });
            String whct = "tipocentro = " + livello.toString();

            if (((Boolean) session.get("modalita_centri")).booleanValue()) {

                Number c1 = (Number) session.get("centro1liv");
                Number c2 = (Number) session.get("centro2liv");
                String in = (String) session.get("elenco_centri");

                if (c1 != null || c2 != null) {
                    whct += "and idcentro in " + in;
                    if (c1 != null)
                        whct += " or idcentro=" + c1;
                    if (c2 != null)
                        whct += " or idcentro=" + c2;
                }

            }

            voCt.setWhereClause(whct);
            voCt.executeQuery();

            // mauro 20/09/2010, scelta prelevatore
            session.put("choosePrel", Boolean.FALSE);
            // 12122013 gaion: scelta esito
            session.put("chooseEsito", Boolean.FALSE);
            String today = DateUtils.getToday();

            ViewObject voPrel = am.findViewObject("Acc_OstetricaView2");
            String whcl = "ULSS = '" + ulss + "' and TPSCR = '" + tpscr + "'";
            whcl += " and (DTFINEVALOPMEDICO is null or ";
            whcl += " DTFINEVALOPMEDICO >= to_date('" + today + "','dd/MM/yyyy'))";
            whcl += " and IDOP is not null";
            voPrel.setWhereClause(whcl);
            voPrel.executeQuery();
            // mauro 20/09/2010, fine modifica

            this.onChliv(null);
        }
    }

    public void onChliv(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        Acc_AppModule am = (Acc_AppModule) voIter.getViewObject().getApplicationModule();

        ViewObject voCt = am.findViewObject("Acc_SelCprelView1");
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        Acc_RicParam bean =
            (Acc_RicParam) BindingContext.getCurrent().findDataControl("Acc_RicParamDataControl").getDataProvider();
        String livello = bean.getLivello();
        /*MOD20071121
            String whct = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" +
              " and tipocentro = " + livello;
              */
        voCt.setWhereClauseParams(new Object[] { ulss, tpscr });
        String whct = "tipocentro = " + livello.toString();

        voCt.setWhereClause(whct);
        voCt.executeQuery();
        if (!"1".equals(livello)) {
            bean.setTestProposto(null);
            bean.setPrelievoHpv(null);
        }
    }

    public void onCerca() {
        Acc_RicParam bean =
            (Acc_RicParam) BindingContext.getCurrent().findDataControl("Acc_RicParamDataControl").getDataProvider();

        //03102011 Gaion parametri di ricerca minimi
        boolean paramOk = false;
        Map session = ADFContext.getCurrent().getSessionScope();
        if ((bean.getCognome() != null) && (bean.getCognome().length() > 1)) {
            paramOk = true;
        } else if ((bean.getNome() != null) && (bean.getNome().length() > 1)) {
            paramOk = true;
        } else if (session.get("dtInvito") != null) {
            paramOk = true;
        } else if (bean.getCodRichiesta() != null) {
            paramOk = true;
        } else if (bean.getCodIdSogg() != null && bean.getCodIdSogg().length() > 0) {
            paramOk = true;
        } else if (bean.getTessSan() != null && bean.getTessSan().length() > 0) {
            paramOk = true;
        } else if (bean.getCodiceDoc() != null && bean.getCodiceDoc().length() > 0) {
            paramOk = true;
        } else if (bean.getChiave() != null && bean.getChiave().length() > 0) {
            paramOk = true;
        }

        if (!paramOk) {
            this.handleException("Parametri di ricerca insufficienti.Impostare almeno uno di questi campi: Cognome (con almeno due caratteri), Nome (con almeno due caratteri)," +
                                 "Tessera sanitaria, Data invito, Codice richiesta, Codice identificativo, Chiave anonima", null);

        } else {

            bean.queryInviti();
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject vo = voIter.getViewObject();
            if (vo.getEstimatedRowCount() > 0) {
                Acc_RicInvitiViewRowImpl _row = (Acc_RicInvitiViewRowImpl) vo.first();
                _row.setSelezionato(Boolean.TRUE);
                RowKeySet selected = new RowKeySetImpl();
                selected.add(Arrays.asList(_row.getKey()));
                this.accTable.setSelectedRowKeys(selected);
                ADFContext.getCurrent().getViewScope().put("numSelected", 1);
                ADFContext.getCurrent().getViewScope().put("selectedRows", selected);
                Utility.gotoTablePageOfSelectedRow(voIter, this.accTable);
            }
        }
    }

    public void onReimp(ActionEvent actionEvent) {
        Acc_RicParam bean =
            (Acc_RicParam) BindingContext.getCurrent().findDataControl("Acc_RicParamDataControl").getDataProvider();
        bean.resetCampi();
        this.onChliv(null);
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc2'])");
    }

    public void selectionListener(SelectionEvent selectionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject vo = voIter.getViewObject();

        RowKeySet selectedRows = getAccTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        System.out.println("----------------------> " + numSelectedRows);

        vo.reset();
        RowSetIterator rs = vo.createRowSetIterator(null);
        rs.reset();
        Acc_RicInvitiViewRowImpl row;
        while (rs.hasNext()) {
            row = (Acc_RicInvitiViewRowImpl) rs.next();
            row.setSelezionato(Boolean.FALSE);
        }
        rs.closeRowSetIterator();

        Iterator selectedIter = selectedRows.iterator();
        while (selectedIter.hasNext()) {
            Key key = (Key) ((List) selectedIter.next()).get(0);
            Row[] _rs = vo.findByKey(key, 1);
            if (_rs != null && _rs.length > 0) {
                Acc_RicInvitiViewRowImpl _row = (Acc_RicInvitiViewRowImpl) _rs[0];
                _row.setSelezionato(Boolean.TRUE);

                if (numSelectedRows == 1)
                    vo.setCurrentRow(_row);
            }
        }

        ADFContext.getCurrent().getViewScope().put("numSelected", numSelectedRows);
    }

    public void onChiudig(ActionEvent actionEvent) {
        closeConfirm();
        Acc_AppModule am =
            (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
        Acc_RicParam bean =
            (Acc_RicParam) BindingContext.getCurrent().findDataControl("Acc_RicParamDataControl").getDataProvider();
        Integer idCt = bean.getIdCprel();
        Map session = ADFContext.getCurrent().getSessionScope();
        java.util.Date dtInvito = (java.util.Date) session.get("closeToDate");
        if (dtInvito == null) {
            dtInvito = (java.util.Date) session.get("dtInvito");
        }

        String livello = bean.getLivello();
        String esito = "N";
        Integer ctInvito = idCt;
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        //20170126 serra: getsire il caso senza richiamo
        Integer detCtRich = null;
        Integer ctRich = null;
        Integer ggRich = null;
        String tpRich = null;

        DatiRichiamo datiRich = InvitoUtils.getDatiRichiamo(am, livello, esito, ctInvito, ulss, tpscr);

        if (datiRich != null) {
            detCtRich = datiRich.getDetCtRich();
            ctRich = datiRich.getIdCentroRichiamo();
            ggRich = datiRich.getGgRichiamo();
            tpRich = datiRich.getTpRichiamo();
        }
        if (ggRich == null) {
            ggRich = 0;
        }

        //SE IL COGNOME HA DENTRO UN APOSTROFO LA STRINGA VA ALTERATA
        String user = ((String) session.get("user")).replaceAll("'", "''");

        String istrUpd = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        if (datiRich == null) {
            istrUpd =
                "UPDATE SO_INVITO SET " + "CODESITOINVITO = 'N', " + "IDCENTRORICHIAMO = null, " +
                "TPRICHIAMO = null, " + "DTRICHIAMO = null, " + "OPMODIFICA = '" + user + "', " +
                "DTULTIMAMOD = sysdate " + "where CODESITOINVITO = '?' and IDCENTROPRELIEVO = " + idCt.intValue() +
                " and DTAPP < to_date('" + sdf.format(dtInvito) + "', 'YYYYMMDD') + 1";
        }

        else //20170126 serra: fine mod
        if (detCtRich.intValue() == 1) {
            istrUpd =
                "UPDATE SO_INVITO SET " + "CODESITOINVITO = 'N', " + "IDCENTRORICHIAMO = " + ctRich.toString() + ", " +
                "TPRICHIAMO = '" + tpRich + "', " + "DTRICHIAMO = DTAPP + " + ggRich.toString() + ", " +
                "OPMODIFICA = '" + user + "', " + "DTULTIMAMOD = sysdate " +
                "where CODESITOINVITO = '?' and IDCENTROPRELIEVO = " + idCt.intValue() + " and DTAPP < to_date('" +
                sdf.format(dtInvito) + "', 'YYYYMMDD') + 1";
        } else {
            istrUpd =
                "UPDATE SO_INVITO SET " + "CODESITOINVITO = 'N', " +
                "IDCENTRORICHIAMO = FUN_ULTCPREL1LIV(CODTS,ULSS,TPSCR), " + "TPRICHIAMO = '" + tpRich + "', " +
                "DTRICHIAMO = DTAPP + " + ggRich.toString() + ", " + "OPMODIFICA = '" + user + "', " +
                "DTULTIMAMOD = sysdate " + "where CODESITOINVITO = '?' and IDCENTROPRELIEVO = " + idCt.intValue() +
                " and DTAPP < to_date('" + sdf.format(dtInvito) + "', 'YYYYMMDD') + 1";

        }

        ViewObject voSelCt = am.findViewObject("Acc_SelCprelView1");
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

            ViewObject voRic1 = am.findViewObject("Acc_RicInvitiView1");
            voRic1.executeQuery();
            voSelCt.executeQuery();
        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
            this.doRollback();
        }
    }

    public String onEsito() {
        if (this.navDett())
            return "goEsitoCI";
        else
            return "noexec";
    }

    public boolean navDett() {
        RowKeySet selectedRows = getAccTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        boolean oneSel = numSelectedRows == 1;
        if (oneSel) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            RowSetIterator accRSIter = voIter.getRowSetIterator();
            Iterator selectedAccIter = selectedRows.iterator();
            Key key = (Key) ((List) selectedAccIter.next()).get(0);
            Row currentRow = accRSIter.getRow(key);
            ViewObject voAcc = voIter.getViewObject();
            voAcc.setCurrentRow(currentRow);
            AccUtils.processaInvitoCorrente(voAcc);
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            session.put("showTabs", Boolean.TRUE);
            return true;
        } else {
            String msg =
                "Per poter accedere alle informazioni di dettaglio e' necessario " +
                "che risulti selezionato uno e un solo invito";
            this.handleException(msg, null);
            return false;
        }
    }

    public String onPrel() {
        boolean navOK = this.navDett();
        if (navOK) {
            return "goPrel";
        }

        return "noexec";
    }

    public String onInterv() {
        boolean navOK = this.navDett();
        if (navOK) {
            return "goInt";
        }

        return "noexec";
    }

    public String onAnam() {
        String tsk = (String) ADFContext.getCurrent().getRequestScope().get("tsk");

        DCBindingContainer bindings;
        if (tsk != null && !tsk.isEmpty())
            bindings = (DCBindingContainer) findBindingContainer(tsk);
        else
            bindings = (DCBindingContainer) getBindings();

        if ((tsk != null && !tsk.isEmpty()) || this.navDett()) {
            return "goAnamCI";
        } else
            return "noexec";
    }

    public String onAnagrafica() {
        boolean navOK = this.navDett();
        if (navOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");

            ViewObject voRic = voIter.getViewObject();
            Acc_RicInvitiViewRow cInv = (Acc_RicInvitiViewRow) voRic.getCurrentRow();

            Sogg_RicParam beanSogg =
                (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
            beanSogg.resetCampi();
            beanSogg.setTessSan((String) cInv.getCodts());
            beanSogg.setCognome((String) cInv.getCognome());
            beanSogg.setNome((String) cInv.getNome());
            
            String chiave = (String) cInv.getAttribute("Chiave");
            
            if (chiave!=null && !"".equals(chiave)){
                Map session = ADFContext.getCurrent().getSessionScope();
                if (session.get("SOAccessoAnonimo")!=null){
                    Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                    if (sOAccessoAnonimo)
                        beanSogg.setChiave(chiave);
                }
            }
            
            beanSogg.setInEta(0);
            beanSogg.querySogg();

            Sogg_AppModule amSogg =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            ViewObject vo = amSogg.findViewObject("Sogg_RicercaView1");
            vo.setCurrentRow(vo.first());

            Map session = ADFContext.getCurrent().getSessionScope();
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

            // 31072013 Gaion: fix su column ambiguously defined
            ViewObject voSoggScr = amSogg.findViewObject("Sogg_SoSoggScrView1");
            String whScr =
                "Sogg_SoSoggScr.CODTS = '" + (String) cInv.getCodts() + "' and Sogg_SoSoggScr.ULSS = '" + ulss +
                "' and Sogg_SoSoggScr.TPSCR = '" + tpscr + "'";
            voSoggScr.setWhereClause(whScr);
            voSoggScr.executeQuery();

            // filtro distretti
            String comRes = (String) fRow.getAttribute("Codcomres");
            //20120111 Serra: disbailito filtro su zona
            // SoggUtils.filtraDistretti(ctx,comRes,"Sogg_DistrettiView2");
            //20110111 fine

            session.put("LINK_ACC", Boolean.TRUE);
            session.put("invitoPresente", Boolean.TRUE);

            // Imposto le informazioni per il ritorno a questa pagina
            PageDescriptor page = new PageDescriptor("Acc_ricerca");
            page.setBackTitle("Torna all'accettazione");
            page.setAction("acc_to");
            ADFContext.getCurrent().getSessionScope().put(AppConstants.FROM_PAGE, page);


            return "goAnag";
        }

        return "unselected";
    }

    public void onSegnaAss(ActionEvent actionEvent) {
        RowKeySet selectedRows = getAccTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        if (numSelectedRows > 0) {

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            Acc_AppModule am = (Acc_AppModule) voRic.getApplicationModule();
            ViewObject voInvito = am.findViewObject("Acc_SoInvitoView1");
            boolean errore = false;

            Iterator selectedIter = selectedRows.iterator();
            while (selectedIter.hasNext()) {
                Key key = (Key) ((List) selectedIter.next()).get(0);
                Row[] _rs = voRic.findByKey(key, 1);
                if (_rs != null && _rs.length > 0) {
                    Acc_RicInvitiViewRow cInv = (Acc_RicInvitiViewRow) _rs[0];
                    voRic.setCurrentRow(cInv);

                    String esito = cInv.getCodesitoinvito();
                    if (!esito.equals(ConfigurationConstants.CODICE_ESITO_NON_DISPONIBILE))
                        continue;

                    Integer idInv = cInv.getIdinvito();
                    /*MOD20071121
                voInvito.setWhereClause("IDINVITO = " + idInv.toString());
                */
                    voInvito.setWhereClauseParam(0, idInv);

                    voInvito.executeQuery();
                    Row fInv = voInvito.first();

                    fInv.setAttribute("Codesitoinvito", ConfigurationConstants.CODICE_ESITO_MANCATA_PRESENZA);
                    //20111018 Serra: mancano data e opetratore di modifica
                    fInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                    fInv.setAttribute("Opmodifica", (String) ADFContext.getCurrent().getSessionScope().get("user"));
                    //fine mod

                    GeneratoreInviti gen = new GeneratoreInviti(am);
                    try {
                        gen.updateRoundIndiv(fInv);
                        AccUtils.insRichiamo(am, fInv);

                    } catch (Exception ex) {
                        String msg = ex.getMessage();
                        this.handleException(msg, null);
                        errore = true;
                        break;
                    }
                }
            }

            if (errore) {
                this.doRollback();
            } else {
                am.getTransaction().commit();
                AccUtils.requeryElenco(am);
            }
        }
    }

    public void onSegnapres(ActionEvent actionEvent) {
        RowKeySet selectedRows = getAccTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        if (numSelectedRows > 0) {

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            Acc_AppModule am = (Acc_AppModule) voRic.getApplicationModule();
            ViewObject voInvito = am.findViewObject("Acc_SoInvitoView1");
            boolean errore = false;

            Iterator selectedIter = selectedRows.iterator();
            while (selectedIter.hasNext()) {
                Key key = (Key) ((List) selectedIter.next()).get(0);
                Row[] _rs = voRic.findByKey(key, 1);
                if (_rs != null && _rs.length > 0) {
                    Acc_RicInvitiViewRow cInv = (Acc_RicInvitiViewRow) _rs[0];

                    voRic.setCurrentRow(cInv);
                    Integer idInv = cInv.getIdinvito();
                    /*MOD20071121
                    voInvito.setWhereClause("IDINVITO = " + idInv.toString());
                    */
                    voInvito.setWhereClauseParam(0, idInv);

                    voInvito.executeQuery();
                    Row fInv = voInvito.first();

                    fInv.setAttribute("Codesitoinvito", ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO);
                    //20111018 Serra: mancano data e opetratore di modifica
                    fInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                    fInv.setAttribute("Opmodifica", (String) ADFContext.getCurrent().getSessionScope().get("user"));
                    //fine mod

                    GeneratoreInviti gen = new GeneratoreInviti(am);
                    try {
                        gen.updateRoundIndiv(fInv);
                        AccUtils.insRichiamo(am, fInv);

                    } catch (Exception ex) {
                        String msg = ex.getMessage();
                        this.handleException(msg, null);
                        errore = true;
                        break;
                    }
                }
            }

            if (errore) {
                this.doRollback();
            } else {
                am.getTransaction().commit();
                AccUtils.requeryElenco(am);
            }
        }
    }

    public void choosePrel(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject voRic = voIter.getViewObject();

        Map sess = ADFContext.getCurrent().getSessionScope();
        String user = (String) sess.get("user");

        RowKeySet selectedRows = getAccTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        if (numSelectedRows > 0) {
            Acc_AppModule am = (Acc_AppModule) voRic.getApplicationModule();
            ViewObject voInvito = am.findViewObject("Acc_SoInvitoView1");
            @SuppressWarnings("deprecation")
            Acc_RicParam bean =
                (Acc_RicParam) BindingContext.getCurrent().findDataControl("Acc_RicParamDataControl").getDataProvider();
            String selectedEsito = bean.getEsito();

            boolean errore = false;

            Iterator selectedIter = selectedRows.iterator();
            while (selectedIter.hasNext()) {
                Key key = (Key) ((List) selectedIter.next()).get(0);
                Row[] _rs = voRic.findByKey(key, 1);
                if (_rs != null && _rs.length > 0) {
                    Acc_RicInvitiViewRow cInv = (Acc_RicInvitiViewRow) _rs[0];
                    voRic.setCurrentRow(cInv);
                    Integer idInv = cInv.getIdinvito();

                    voInvito.setWhereClauseParams(new Object[] { idInv.toString() });

                    voInvito.executeQuery();
                    Row fInv = voInvito.first();

                    Integer idPrel = bean.getIdPrelevatore();
                    fInv.setAttribute("Idostetrica", idPrel);
                    fInv.setAttribute("Codesitoinvito", ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO);

                    fInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                    fInv.setAttribute("Opmodifica", user);


                    GeneratoreInviti gen = new GeneratoreInviti(am);
                    try {
                        gen.updateRoundIndiv(fInv);

                        Integer idInvito = (Integer) fInv.getAttribute("Idinvito");
                        String codEsInvNew = (String) fInv.getAttribute("Codesitoinvito");
                        Integer idCtPrelNew = (Integer) fInv.getAttribute("Idcentroprelievo");
                        Date dtAppNew = (Date) fInv.getAttribute("Dtapp");

                        Date dtEsameRecNew = (Date) fInv.getAttribute("Dtesamerecente");

                        boolean modifiche =
                            SoggUtils.datiRichiamoModificati(am, idInvito, codEsInvNew, idCtPrelNew, dtAppNew,
                                                             dtEsameRecNew);

                        if (modifiche)
                            AccUtils.insRichiamo(am, fInv);

                    } catch (Exception ex) {
                        String msg = ex.getMessage();
                        this.handleException(msg, null);
                        errore = true;
                        break;
                    }
                }
            }

            if (errore) {
                this.doRollback();
            } else {
                getChoosePrel().hide();
            }
            am.getTransaction().commit();
            AccUtils.requeryElenco(am);
        }
    }

    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.remove("fromDett");
        session.remove("selectedRows");
        Acc_AppModule am =
            (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Acc_RicInvitiView1");
        Row r = vo.getCurrentRow();

        Integer nr = (Integer) ADFContext.getCurrent().getViewScope().get("numSelected");
        boolean oneSel = r != null && nr != null && nr == 1;
        AccUtils.beforeNavAcc(dest, oneSel);
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
