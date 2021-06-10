package view.accettazione.colon;

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

import model.accettazione.AccCo_RicInvitiViewRowImpl;

import model.common.AccCo_AppModule;
import model.common.AccCo_RicInvitiViewRow;
import model.common.AccCo_SoAnamnesicolonViewRow;
import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.AccCo_RicParam;
import model.datacontrol.Ref_SearchBean;
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

public class AccCo_ricercaAction extends Parent_DataForwardAction {
    private RichPopup chooseEsito;
    private RichPopup dateWarning;
    private RichPopup confirm;

    protected void setAppModule() {
        // TODO:  Implement this view.commons.Parent_DataForwardAction abstract method
        this.amName = "AccCo_AppModule";
    }

    private RichForm formSearch;

    public AccCo_ricercaAction() {
    }

    public void setFormSearch(RichForm formSearch) {
        this.formSearch = formSearch;
    }

    public RichForm getFormSearch() {
        if (formSearch == null)
            findForward();
        return formSearch;
    }

    private RichTable accCoTable;

    public void setAccCoTable(RichTable accCoTable) {
        this.accCoTable = accCoTable;
    }

    public RichTable getAccCoTable() {
        return accCoTable;
    }

    protected void findForward() {
        AccCo_RicParam bean =
            (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        ADFContext.getCurrent().getSessionScope().remove(AppConstants.FROM_PAGE);

        //torno dal dettaglio?
        Boolean fromDett = (Boolean) session.get("fromDett");
        if (fromDett != null && fromDett) {
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
            ((AccCo_AppModule) voIter.getViewObject().getApplicationModule()).doRollback("AccCo_RicInvitiView1");
            ViewObject vo = voIter.getViewObject();
            ViewHelper.queryAndRestoreCurrentRow(vo);

            RowKeySet selected = (RowKeySet) ADFContext.getCurrent().getSessionScope().get("selectedRows");
            if (selected == null)
                selected = new RowKeySetImpl();

            if (this.accCoTable == null)
                this.accCoTable = new RichTable();

            AccCo_RicInvitiViewRowImpl _row = (AccCo_RicInvitiViewRowImpl) vo.getCurrentRow();
            _row.setSelezionato(Boolean.TRUE);
            selected.clear();
            selected.add(Arrays.asList(_row.getKey()));
            this.accCoTable.setSelectedRowKeys(selected);

            Utility.gotoTablePageOfSelectedRow(voIter, this.accCoTable);
            ADFContext.getCurrent().getViewScope().put("numSelected", 1);
        } else {

            boolean nav = bean.getNavigazione().booleanValue();
            if (nav) {
                bean.queryInviti();
                //bean.setNavigazione(Boolean.FALSE);
            } else {
                bean.resetCampi();

                DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");

                ViewObject vo = voIter.getViewObject();
                vo.setWhereClause("1=2");
                vo.executeQuery();
            }

            this.onChliv(null);
        }
    }

    public void onChliv(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        AccCo_AppModule am =
            (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
        ViewObject voCt = am.findViewObject("AccCo_SelCprelView1");
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        AccCo_RicParam bean =
            (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();
        String livello = bean.getLivello();
        /*MOD20071121
            String whct = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" +
              " and tipocentro = " + livello;
              */
        voCt.setWhereClauseParams(new Object[] { ulss, tpscr });
        String whct = "tipocentro = " + livello.toString();

        voCt.setWhereClause(whct);
        voCt.executeQuery();
        if (livello.equals("2")) {
            bean.setCodCampione(null);
        }
    }

    public void onCerca() {
        AccCo_RicParam bean =
            (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();

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
        } else if (bean.getCodCampione() != null) {
            paramOk = true;
        } else if (bean.getCodIdSogg() != null && bean.getCodIdSogg().length() > 0) {
            paramOk = true;
        } else if (bean.getTessSan() != null && bean.getTessSan().length() > 0) {
            paramOk = true;
        } else if (bean.getCodiceDocumento() != null && bean.getCodiceDocumento().length() > 0) {
            paramOk = true;
        } else if (bean.getChiave() != null) {
            paramOk = true;
        }

        if (!paramOk) {
            this.handleException("Parametri di ricerca insufficienti.Impostare almeno uno di questi campi: Cognome (con almeno due caratteri), Nome (con almeno due caratteri)," +
                                 "Tessera sanitaria, Data invito, Codice richiesta, Codice identificativo, Codice campione, Chiave anonima",
                                 null);

        } else {

            bean.queryInviti();
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
            ViewObject vo = voIter.getViewObject();
            if (vo.getEstimatedRowCount() > 0) {
                AccCo_RicInvitiViewRowImpl _row = (AccCo_RicInvitiViewRowImpl) vo.first();
                _row.setSelezionato(Boolean.TRUE);
                RowKeySet selected = new RowKeySetImpl();
                selected.add(Arrays.asList(_row.getKey()));
                this.accCoTable.setSelectedRowKeys(selected);
                ADFContext.getCurrent().getViewScope().put("numSelected", 1);
                ADFContext.getCurrent().getViewScope().put("selectedRows", selected);
                Utility.gotoTablePageOfSelectedRow(voIter, this.accCoTable);
            }
        }
    }


    public void onReimp(ActionEvent actionEvent) {
        AccCo_RicParam bean =
            (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();
        bean.resetCampi();
        this.onChliv(null);
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc2'])");
    }

    public void selectionListener(SelectionEvent selectionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
        ViewObject vo = voIter.getViewObject();

        RowKeySet selectedRows = getAccCoTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        System.out.println("----------------------> " + numSelectedRows);

        vo.reset();
        RowSetIterator rs = vo.createRowSetIterator(null);
        rs.reset();
        AccCo_RicInvitiViewRowImpl row;
        while (rs.hasNext()) {
            row = (AccCo_RicInvitiViewRowImpl) rs.next();
            row.setSelezionato(Boolean.FALSE);
        }
        rs.closeRowSetIterator();

        Iterator selectedIter = selectedRows.iterator();
        while (selectedIter.hasNext()) {
            Key key = (Key) ((List) selectedIter.next()).get(0);
            Row[] _rs = vo.findByKey(key, 1);
            if (_rs != null && _rs.length > 0) {
                AccCo_RicInvitiViewRowImpl _row = (AccCo_RicInvitiViewRowImpl) _rs[0];
                _row.setSelezionato(Boolean.TRUE);

                if (numSelectedRows == 1)
                    vo.setCurrentRow(_row);
            }
        }

        ADFContext.getCurrent().getViewScope().put("numSelected", numSelectedRows);
    }

    public String onEsito() {
        if (this.navDett())
            return "goEsitoCo";
        else
            return "noexec";
    }

    public boolean navDett() {
        RowKeySet selectedRows = getAccCoTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        boolean oneSel = numSelectedRows == 1;
        if (oneSel) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
            RowSetIterator accRSIter = voIter.getRowSetIterator();
            Iterator selectedAccIter = selectedRows.iterator();
            Key key = (Key) ((List) selectedAccIter.next()).get(0);
            Row currentRow = accRSIter.getRow(key);
            ViewObject voAcc = voIter.getViewObject();
            voAcc.setCurrentRow(currentRow);
            AccUtils.procInvCorrenteColon(voAcc);
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

    public void initAnam(DCBindingContainer bindings) {
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
        ViewObject voRic = voIter.getViewObject();
        AccCo_AppModule am = (AccCo_AppModule) voRic.getApplicationModule();

        ViewObject voAcc = am.findViewObject("AccCo_SoAccColon2livView1");
        Row currAcc = voAcc.getCurrentRow();
        Integer idAcc = (Integer) currAcc.getAttribute("Idaccco2liv");

        ViewObject voAnam = am.findViewObject("AccCo_SoAnamnesicolonView1");
        /*MOD20071121
            String wh = "IDACCCO_2LIV = " + idAcc.toString();
            voAnam.setWhereClause(wh);
            */
        voAnam.setWhereClauseParams(new Object[] { idAcc });

        voAnam.executeQuery();

        /*MOD20071121
            int countAnam = voAnam.getRowCount();

            if (countAnam > 0)
            {
              Row fAnam = voAnam.first();
              */

        Row fAnam = voAnam.first();
        if (fAnam != null) {

            voAnam.setCurrentRow(fAnam);
        } else {
            AccCo_SoAnamnesicolonViewRow nAnam = (AccCo_SoAnamnesicolonViewRow) voAnam.createRow();
            Integer idAnam = am.getNextIdAnamco();
            Date dtoraCorr = DateUtils.getOracleDateNow();

            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            String user = (String) session.get("user");
            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");

            nAnam.setIdAnamco(idAnam);
            nAnam.setIdaccco2liv(idAcc);
            nAnam.setDtcreazione(dtoraCorr);
            nAnam.setOpcreazione(user);
            nAnam.setDtultmod(dtoraCorr);
            nAnam.setOpultmod(user);
            nAnam.setDtanamnesi(dtoraCorr);
            nAnam.setIdesitoaf(0);
            nAnam.setIbd(0);
            nAnam.setPcPresente(0);
            nAnam.setAcPresenti(0);
            nAnam.setPaPresente(0);
            nAnam.setRcPresente(0);
            nAnam.setAlPresente(0);
            nAnam.setUlss(ulss);
            nAnam.setTpscr(tpscr);

            voAnam.insertRow(nAnam);
        }
    }

    public String onAnam() {
        String tsk = (String) ADFContext.getCurrent().getRequestScope().get("tsk");

        DCBindingContainer bindings;
        if (tsk != null && !tsk.isEmpty())
            bindings = (DCBindingContainer) findBindingContainer(tsk);
        else
            bindings = (DCBindingContainer) getBindings();

        if ((tsk != null && !tsk.isEmpty()) || this.navDett()) {
            initAnam(bindings);

            return "goAnamCo";
        } else
            return "noexec";
    }

    public String onAnagrafica() {
        boolean navOK = this.navDett();
        if (navOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");

            ViewObject voRic = voIter.getViewObject();
            AccCo_RicInvitiViewRow cInv = (AccCo_RicInvitiViewRow) voRic.getCurrentRow();

            Sogg_RicParam beanSogg =
                (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
            beanSogg.resetCampi();
            beanSogg.setTessSan((String) cInv.getCodts());
            beanSogg.setCognome((String) cInv.getCognome());
            beanSogg.setNome((String) cInv.getNome());
            beanSogg.setInEta(0);
            beanSogg.querySogg();
            
            String chiave = (String) cInv.getChiave();
            
            if (chiave!=null && !"".equals(chiave)){
                Map session = ADFContext.getCurrent().getSessionScope();
                if (session.get("SOAccessoAnonimo")!=null){
                    Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                    if (sOAccessoAnonimo)
                        beanSogg.setChiave(chiave);
                }
            }

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
            PageDescriptor page = new PageDescriptor("AccCo_ricerca");
            page.setBackTitle("Torna all'accettazione");
            page.setAction("acc_toColon");
            ADFContext.getCurrent().getSessionScope().put(AppConstants.FROM_PAGE, page);


            return "goAnag";
        }

        return "unselected";
    }

    public void chooseEsito(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
        ViewObject voRic = voIter.getViewObject();

        Map sess = ADFContext.getCurrent().getSessionScope();
        String user = (String) sess.get("user");

        RowKeySet selectedRows = getAccCoTable().getSelectedRowKeys();
        Integer numSelectedRows = selectedRows.getSize();

        if (numSelectedRows > 0) {
            AccCo_AppModule am = (AccCo_AppModule) voRic.getApplicationModule();
            ViewObject voInvito = am.findViewObject("AccCo_SoInvitoView1");
            @SuppressWarnings("deprecation")
            AccCo_RicParam bean =
                (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();
            String selectedEsito = bean.getEsito();
            //verifico che sia stato selezionato un esito
            if (selectedEsito.equals(ConfigurationConstants.CODICE_ESITO_NON_DISPONIBILE)) {
                String msg = "Selezionare un esito diverso da ?";
                this.handleException(msg, null);
                return;
            }
            boolean errore = false;

            Iterator selectedIter = selectedRows.iterator();
            while (selectedIter.hasNext()) {
                Key key = (Key) ((List) selectedIter.next()).get(0);
                Row[] _rs = voRic.findByKey(key, 1);
                if (_rs != null && _rs.length > 0) {
                    AccCo_RicInvitiViewRow cInv = (AccCo_RicInvitiViewRow) _rs[0];
                    voRic.setCurrentRow(cInv);

                    String esito = cInv.getCodesitoinvito();
                    if (!esito.equals(ConfigurationConstants.CODICE_ESITO_NON_DISPONIBILE))
                        continue;

                    Integer idInv = cInv.getIdinvito();

                    voInvito.setWhereClauseParams(new Object[] { idInv.toString() });

                    voInvito.executeQuery();
                    Row fInv = voInvito.first();

                    fInv.setAttribute("Codesitoinvito", selectedEsito);
                    //se e' stato selezionato esito ritiro imposto la data ritiro
                    if (ConfigurationConstants.CODICE_ESITO_CONTENITORE_RITIRATO.equals(selectedEsito)) {
                        Date dtInvito = (Date) fInv.getAttribute("Dtapp");
                        fInv.setAttribute("Dtritcont", dtInvito);
                    }
                    fInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                    fInv.setAttribute("Opmodifica", user);

                    GeneratoreInviti gen = new GeneratoreInviti(am);
                    try {
                        gen.updateRoundIndiv(fInv);
                        AccUtils.insRichiamoCO(am, fInv);

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
                getChooseEsito().hide();
            }
            am.getTransaction().commit();
            AccUtils.requeryElencoColon(am);
        }
    }

    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.remove("fromDett");
        session.remove("selectedRows");
        AccCo_AppModule am =
            (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("AccCo_RicInvitiView1");
        Row r = vo.getCurrentRow();

        Integer nr = (Integer) ADFContext.getCurrent().getViewScope().get("numSelected");
        if (r != null && nr != null && nr == 1) {

            // Navigazione verso ricerca soggetto
            if (dest.equals("iniSogg")) {
                Sogg_RicParam beanSogg =
                    (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                beanSogg.resetCampi();
                beanSogg.setTessSan((String) r.getAttribute("Codts"));
                beanSogg.setCognome((String) r.getAttribute("Cognome"));
                beanSogg.setNome((String) r.getAttribute("Nome"));
                beanSogg.setInEta(0);
                beanSogg.setNavigazione(Boolean.TRUE);
                
                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanSogg.setChiave(chiave);
                    }
                }
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
    }

    public void setChooseEsito(RichPopup chooseEsito) {
        this.chooseEsito = chooseEsito;
    }

    public RichPopup getChooseEsito() {
        return chooseEsito;
    }
    
    public void onChiudig(ActionEvent actionEvent) {
        closeConfirm();
        AccCo_AppModule am =
            (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
        AccCo_RicParam bean =
            (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();
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

        /* DatiRichiamo datiRich =
              InvitoUtils.getDatiRichiamo(am,livello,esito,ctInvito,ulss,tpscr);

            Number detCtRich = datiRich.getDetCtRich();
            Number ctRich = datiRich.getIdCentroRichiamo();
            Number ggRich = datiRich.getGgRichiamo();
            String tpRich = datiRich.getTpRichiamo();
            */
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
                "where CODESITOINVITO = '?' and IDCENTROPRELIEVO = " + idCt.intValue() +
                " and DTAPP < to_date('" + sdf.format(dtInvito) + "', 'YYYYMMDD') + 1";
        } else {
            istrUpd =
                "UPDATE SO_INVITO SET " + "CODESITOINVITO = 'N', " +
                "IDCENTRORICHIAMO = FUN_ULTCPREL1LIV(CODTS,ULSS,TPSCR), " + "TPRICHIAMO = '" + tpRich + "', " +
                "DTRICHIAMO = DTAPP + " + ggRich.toString() + ", " + "OPMODIFICA = '" + user + "', " +
                "DTULTIMAMOD = sysdate " + "where CODESITOINVITO = '?' and IDCENTROPRELIEVO = " + idCt.intValue() +
                " and DTAPP < to_date('" + sdf.format(dtInvito) + "', 'YYYYMMDD') + 1";

        }

        ViewObject voSelCt = am.findViewObject("AccCo_SelCprelView1");
        Row[] rows = voSelCt.getFilteredRows("Idcentro", idCt);
        Date dtChiusi = (Date) rows[0].getAttribute("Dtchperiodo");
        if (dtChiusi != null) {
            istrUpd += " and DTAPP > to_date('" + DateUtils.dateToString(dtChiusi.dateValue()) + "','dd/MM/yyyy')";
        }

        try {

            am.getTransaction().executeCommand(istrUpd);

            String updCt =
                "UPDATE SO_CENTRO_PREL_REF SET " +
                "DTCHPERIODO = to_date('" + sdf.format(dtInvito) + "', 'YYYYMMDD') " +
                "WHERE IDCENTRO = " + idCt.intValue();

            am.getTransaction().executeCommand(updCt);

            am.getTransaction().commit();

            ViewObject voRic1 = am.findViewObject("AccCo_RicInvitiView1");
            voRic1.executeQuery();
            voSelCt.executeQuery();
        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
            this.doRollback();
        }
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
