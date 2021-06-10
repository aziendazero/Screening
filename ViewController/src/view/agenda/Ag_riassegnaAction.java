package view.agenda;

import insiel.dataHandling.DateUtils;

import java.util.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;

import model.Cnf_AppModuleImpl;

import model.common.Agenda_AppModule;
import model.common.Agenda_AppRiassViewRow;
import model.common.Sogg_AppModule;

import model.commons.AgendaUtils;
import model.commons.ViewHelper;

import model.datacontrol.Agenda_Servizio;
import model.datacontrol.Cnf_selectionBean;

import model.inviti.InvitoUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.adf.share.logging.ADFLogger;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

@SuppressWarnings({ "deprecation", "unused" })
public class Ag_riassegnaAction extends Parent_DataForwardAction {

    private ADFLogger logger = ADFLogger.createADFLogger(this.getClass().getName());
    
    private RichPopup endMovePopup; 

    public Ag_riassegnaAction() {
    }

    protected void setAppModule() {
        this.amName = "Agenda_AppModule";
    }

    public void onCerca(ActionEvent actionEvent) {
        Map sess = ADFContext.getCurrent().getSessionScope();
        Date _dtIni = (Date) sess.get("dtRiassIni");
        Date _dtFin = (Date) sess.get("dtRiassFin");
        Date _dtCreIni = (Date) sess.get("dtCreIni");
        Date _dtCreFin = (Date) sess.get("dtCreFin");

        String dtIni = _dtIni == null ? null : DateUtils.dateToString(_dtIni);
        String dtFin = _dtFin == null ? null : DateUtils.dateToString(_dtFin);
        String dtCreIni = _dtCreIni == null ? null : DateUtils.dateToString(_dtCreIni);
        String dtCreFin = _dtCreFin == null ? null : DateUtils.dateToString(_dtCreFin);

        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
        Integer idCt = bean.getIdCtRiass();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");

        String whcl = "ULSS = '" + ulss + "' and TPSCR = '" + tpscr + "'";

        if (idCt != null) {
            whcl += " and IDCENTROPRELIEVO = " + idCt.toString();
        }

        if (dtIni != null && !dtIni.equals("")) {
            whcl += " and DTAPP >= to_date('" + dtIni + "','dd/mm/yyyy')";
        }

        if (dtFin != null && !dtFin.equals("")) {
            whcl += " and DTAPP < to_date('" + dtFin + "','dd/mm/yyyy') + 1";
        }

        if (dtCreIni != null && !dtCreIni.equals("")) {
            whcl += " and DTCREAZIONE >= to_date('" + dtCreIni + "','dd/mm/yyyy')";
        }

        if (dtCreFin != null && !dtCreFin.equals("")) {
            whcl += " and DTCREAZIONE < to_date('" + dtCreFin + "','dd/mm/yyyy') + 1";
        }

        // Se l'utente e' vincolato ad un centro fisico si vanno a filtrare gli inviti
        // in uno dei centri logici associati, l'utente non ha diritto a spostare altri inviti

        String in = (String) sess.get("elenco_centri");
        if (in != null) {
            // Filtro i soggetti che sono associati ad un centro dell'utente oppure hanno
            // l'ultimo invito in tale centro
            whcl += " AND idcentroprelievo in " + in + "";
        }

        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        ViewObject voElenco = am.findViewObject("Agenda_AppRiassView1");
        voElenco.setWhereClause(whcl);
        voElenco.executeQuery();

        //20110524 SARA
        String qry = voElenco.getQuery();

        Object[] pars = voElenco.getWhereClauseParams();
        if (pars != null) {
            for (int i = 0; i < pars.length; i++) {
                qry = qry.replaceFirst(":" + (i + 1), "'" + (String) pars[i] + "'");
            }
        }

        String newQry = "select codts, ulss from (" + qry + ")";
        Cnf_selectionBean clauseBean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        clauseBean.setInClause(newQry);
        clauseBean.setNote(null);
        //fine 20110524
    }

    @SuppressWarnings("unchecked")
    public void onSposta(ActionEvent actionEvent) {
        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        ViewObject voApp = am.findViewObject("Agenda_AppRiassView1");
        Row rApp = voApp.getCurrentRow();
        String cogn = (String) rApp.getAttribute("Cognome");
        String nome = (String) rApp.getAttribute("Nome");
        oracle.jbo.domain.Date dtApp = (oracle.jbo.domain.Date) rApp.getAttribute("Dtapp");
        String dataApp = "non specificata";

        if (dtApp != null) {
            dataApp = DateUtils.dateToString(dtApp.dateValue());
        }

        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("pazSel",
                    "L'appuntamento relativo al soggetto " + cogn + " " + nome + " in data " + dataApp +
                    " e' stato selezionato per lo spostamento.");
        session.put("appSel", Boolean.valueOf(true));

        cercaSoggInvito(rApp);
    }

    public void onCanc(ActionEvent actionEvent) {
        Sogg_AppModule amSogg =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        ViewObject voElenco = am.findViewObject("Agenda_AppRiassView1");

        RowSetIterator rsi = ViewHelper.getRowSetIterator(voElenco, "iter_app");

        int deleted = 0;

        while (rsi.hasNext()) {
            Agenda_AppRiassViewRow cApp = (Agenda_AppRiassViewRow) rsi.next();
            String codEsito = cApp.getCodesitoinvito();

            if (codEsito.equals("?")) {
                Integer idInvito = cApp.getIdinvito();
                String user = (String) session.get("user");
                String codTs = cApp.getCodts();
                String ulss = cApp.getUlss();
                String tpscr = cApp.getTpscr();

                try {
                    InvitoUtils.cancellaInvito(amSogg, idInvito.intValue(), codTs, ulss, tpscr, user);
                    deleted++;
                } catch (Exception ex) {
                    amSogg.getTransaction().rollback();
                    rsi.closeRowSetIterator();
                    voElenco.executeQuery();
                    ex.printStackTrace();
                    this.handleException(ex, null);
                    return;
                }
            }
        }

        rsi.closeRowSetIterator();
        voElenco.executeQuery();
        String msg = "";

        if (deleted == 0) {
            msg = "Non e' stato possibile cancellare alcun invito.";
        } else {
            msg = "Cancellati con successo " + deleted + " inviti.";

            // Annullo l'eventuale invito selezionato per lo spostamento
            session.remove("pazSel");
            session.remove("appSel");
        }
        this.handleMessages(FacesMessage.SEVERITY_INFO, msg);
    }
    
    public String onConfigurazione() {
        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
        Integer idCt = bean.getIdCentro();
        
        if (idCt != null){
            Cnf_selectionBean beanCFG = (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            beanCFG.setCentro(idCt); 
            Map session = ADFContext.getCurrent().getSessionScope();
            session.put("cnf_setCentro",Boolean.valueOf(true));
            
            Cnf_AppModuleImpl cnfAm =
                (Cnf_AppModuleImpl) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
            
            Key k = new Key(new Object[]{idCt});
            ViewObject voIter = cnfAm.findViewObject("Cnf_SoCentroPrelRefView1");
            ViewHelper.setCurrent(voIter, k);
        }
        
        return "agenda_centro";
    }
    
    public void onFullSposta() {
        // recupero appuntamenti pendendi da lista
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIterApps = bindings.findIteratorBinding("Agenda_AppRiassView1Iterator");
        ViewObject voApps = voIterApps.getViewObject(); // Agenda_AppRiassView
        ApplicationModule am = voApps.getApplicationModule(); // Agenda_AppModule
        // ciclo su ognuno
        RowSetIterator iterApps = voApps.createRowSetIterator(null);
        try {
            long appRiposizionati = 0;
            String errors = "";
            Map sess = ADFContext.getCurrent().getSessionScope();
            while (iterApps.hasNext()) {
                Row rApp = iterApps.next();
                Integer idCPrel = (Integer) rApp.getAttribute("Idcentroprelievo");
                String descCPrel = (String) rApp.getAttribute("Ctapp");
                //  > ricerca primo giorno libero
                oracle.jbo.domain.Date dtAge = DateUtils.getOracleDateNow();

                String ulss = (String) sess.get("ulss");
                String tpScr = (String) sess.get("scr");
                
                oracle.jbo.domain.Date dtApp = (oracle.jbo.domain.Date) rApp.getAttribute("Dtapp");
                Integer oraApp = (Integer) rApp.getAttribute("Oraapp");
                Integer minApp = (Integer) rApp.getAttribute("Minapp");
                Row rSlot = AgendaUtils.checkSlot(am, idCPrel, dtApp, oraApp, minApp, tpScr);
                if (rSlot != null) {
                    // CERCA E FISSA INVITO
                    cercaSoggInvito(rApp);

                    // 
                    Integer idApp = (Integer) rSlot.getAttribute("Idapp");
                    String msg = Ag_giornAction.spostaSogg(am, idApp, idCPrel, dtApp, oraApp, minApp, tpScr, ulss);
                    if (msg == null) {
                        appRiposizionati++;
                    } else {
                        errors += (errors.isEmpty() ? "" : "\n") + msg;
                    }
                } else {
                    String msg = "Impossibile riposizionare gli appuntamenti in agenda per il centro " + descCPrel + ". Slot originale non trovato.";
                    if (!errors.contains(msg)) {
                        errors += (errors.isEmpty() ? "" : "\n") + msg;
                    }
                    continue;
                }

                /*oracle.jbo.domain.Date dtMin = AgendaUtils.primoGLib(am, idCPrel, dtAge, tpScr, ulss);

                if (dtMin == null) {
                    String msg = "Non sono stati trovati slot liberi in agenda per il centro " + descCPrel;
                    if (!errors.contains(msg)) {
                        errors += (errors.isEmpty() ? "" : "\n") + msg;
                    }
                    continue;
                }

                Date primoGG = AgendaUtils.getDateWithoutTime(dtMin.dateValue());
                ViewObject voSlot = AgendaUtils.querySlotOnly(am, idCPrel, primoGG, tpScr);
                if (voSlot != null) {
                    //  > ricerca slot libero
                    voSlot.reset();
                    Row rSlot = null;
                    while (voSlot.hasNext()) {
                        rSlot = voSlot.next();
                        Integer dispSlot = (Integer) rSlot.getAttribute("Dispslot");
                        Integer occSlot = (Integer) rSlot.getAttribute("Slotoccup");
                        if (dispSlot > occSlot) {
                            logger.info("Trovato slot libero");
                            break;
                        }
                        rSlot = null;
                    }
                    if (rSlot != null) {
                        Integer idApp = (Integer) rSlot.getAttribute("Idapp");
                        oracle.jbo.domain.Date dtApp = (oracle.jbo.domain.Date) rSlot.getAttribute("Dtapp");
                        Integer oraApp = (Integer) rSlot.getAttribute("Oraapp");
                        Integer minApp = (Integer) rSlot.getAttribute("Minapp");
                        
                        // fissa 
                        cercaSoggInvito(rApp);

                        //      > spostamento appuntamento o creazione nuovo
                        String msg = Ag_giornAction.spostaSogg(am, idApp, idCPrel, dtApp, oraApp, minApp, tpScr, ulss);
                        if (msg == null) {
                            appRiposizionati++;
                        } else {
                            errors += (errors.isEmpty() ? "" : "\n") + msg;
                        }
                    }
                }*/
            }
            sess.put("appRiposizionati", appRiposizionati);
            sess.put("riposizErrors", errors);
        } catch (Exception e) {
            Sogg_AppModule amSogg =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            logger.severe("Errore spostando gli appuntamenti pendenti", e);
            handleException("Errore spostando gli appuntamenti pendenti", amSogg.getTransaction());
            return;
        } finally {
            iterApps.closeRowSetIterator();
        }
        // apertura popup resoconto a fine ciclo
        if (endMovePopup != null) {
            endMovePopup.show(new RichPopup.PopupHints());
        }
    }


    public void setEndMovePopup(RichPopup endMovePopup) {
        this.endMovePopup = endMovePopup;
    }

    public RichPopup getEndMovePopup() {
        return endMovePopup;
    }

    private void cercaSoggInvito(Row rApp) {
        Sogg_AppModule amSogg =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = amSogg.findViewObject("Sogg_SoInvitoView1");
        Integer idInv = (Integer) rApp.getAttribute("Idinvito");
        voInvito.setWhereClause("IDINVITO = " + idInv.toString());
        voInvito.executeQuery();
        Row fInv = voInvito.first();
        voInvito.setCurrentRow(fInv);
    }
    
    public void onClosePopup() {
        onCerca(null);
        if (endMovePopup != null) {
            endMovePopup.hide();
        }
    }
}
