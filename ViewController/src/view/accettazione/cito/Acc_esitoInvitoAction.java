package view.accettazione.cito;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;
import java.util.Map;

import javax.faces.event.ActionEvent;

import model.accettazione.Acc_SoInvitoViewRowImpl;
import model.accettazione.common.Acc_RicInvitiViewRow;
import model.accettazione.common.Acc_SoInvitoViewRow;

import model.common.Acc_AppModule;
import model.common.Acc_SoAnamnesiCitoViewRow;
import model.common.Cnf_AppModule;
import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.Sogg_ConsensoParam;

import model.filters.ViewObjectFilters;

import model.inviti.GeneratoreInviti;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.commons.AccUtils;
import view.commons.action.Parent_DataForwardAction;

public class Acc_esitoInvitoAction extends Parent_DataForwardAction {
    private RichForm frm;

    @Override
    protected void setAppModule() {
        this.amName = "Acc_AppModule";
    }

    public void setFrm(RichForm frm) {
        this.frm = frm;
    }

    public RichForm getFrm() {
        if (frm == null)
            findForward();

        return frm;
    }

    public Acc_esitoInvitoAction() {
        super();
    }

    protected void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_SoInvitoView1Iterator");
        ViewObject voInvito = voInvIter.getViewObject();

        Acc_AppModule am = (Acc_AppModule) voInvito.getApplicationModule();
        Row rowInv = voInvito.getCurrentRow();

        Date dataApp = (Date) rowInv.getAttribute("Dtapp");
        String strDataApp = null;
        if (dataApp != null) {
            strDataApp = DateUtils.dateToString(dataApp.dateValue());
        }

        //prelevatore - ostetrica
        ViewObject vo = am.findViewObject("Acc_OstetricaView1");
        ViewObjectFilters.filterOpMedici(vo, strDataApp, null, ConfigurationConstants.CODICE_OSTETRICA, ulss, tpscr);

        // filtro esiti invito
        Integer livello = (Integer) rowInv.getAttribute("Livello");

        // Se non c'e' la data appuntamento uso la data di sistema
        dataApp = (Date) rowInv.getAttribute("Dtapp");
        if (dataApp == null) {
            strDataApp = new java.sql.Date(new java.util.Date().getTime()).toString();
        } else {
            strDataApp = dataApp.dateValue().toString();
        }
        
        /* I00102494 - Nella scheda dell'esito invito di tutte le accettazioni la possibilità di accedere 
         * all'agenda per spostare l'invito va permessa con gli stessi cirteri descritti per l'invito corrente */        
        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
        try {
            
            String sposta_invito =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, "sposta_invito");
            
            if(sposta_invito!=null && sposta_invito.matches("-?\\d+")){
                int n = Integer.parseInt(sposta_invito);
                
                if (dataApp!=null){
                    java.util.Date dataAppuntamento = dataApp.getValue();
                    
                    Calendar c = Calendar.getInstance();
                    c.setTime(dataAppuntamento);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    c.add(Calendar.DATE, n);
                    dataAppuntamento = c.getTime();
                    
                    c.setTime(new java.util.Date());
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    
                    java.util.Date oggi = c.getTime();
                    
                    if (dataAppuntamento.after(oggi))
                        session.put("sposta_invito", "true");
                    else 
                        session.put("sposta_invito", "false");
                }
            
            } else
                session.put("sposta_invito", "true");
            
        } catch (Exception e) {
        }

        ViewObject voEsiti = am.findViewObject("Acc_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'YYYY-MM-DD'))");
        voEsiti.executeQuery();
    }

    public void prevInvito(@SuppressWarnings("unused") ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.previous();
            AccUtils.processaInvitoCorrente(voRic);
        }
    }

    public void nextInvito(@SuppressWarnings("unused") ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.next();
            AccUtils.processaInvitoCorrente(voRic);
        }
    }

    @SuppressWarnings("unchecked")
    public String onConf() {
        boolean saveOK = this.save();
        if (saveOK) {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            session.put("fromDett", Boolean.TRUE);
            return "acc_to";
        }
        return null;
    }

    public void onAppl(@SuppressWarnings("unused") ActionEvent actionEvent) {
        @SuppressWarnings("unused")
        boolean saveOK = this.save();
    }

    @SuppressWarnings("unchecked")
    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        if (dest.equals("ag_preGiorn")) {
            session.put("chAgenda", "acc_preEsito.do");
        } else if (dest.equals("acc_to")) {
            session.put("fromDett", Boolean.TRUE);
        } else {
            AccUtils.beforeNavAcc(dest, true);
        }
    }

    protected boolean beforeSave() {
        // TODO:  Override this view.commons.DetAcc_DataForwardAction method
        boolean esitoSuper = super.beforeSave();
        if (!esitoSuper)
            return false;

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        boolean escl = ((Boolean) session.get("soggEscluso")).booleanValue();
        if (escl) {
            String msg = "La persona risulta esclusa. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_SoInvitoView1Iterator");
        ViewObject voInv = voInvIter.getViewObject();
        Acc_AppModule am = (Acc_AppModule) voInv.getApplicationModule();

        ViewObject voAnam = am.findViewObject("Acc_SoAnamnesiCitoView1");
        Acc_SoAnamnesiCitoViewRow cAnam = (Acc_SoAnamnesiCitoViewRow) voAnam.getCurrentRow();

        if (cAnam != null) {

            ViewObject voRic = am.findViewObject("Acc_RicInvitiView1");
            Acc_RicInvitiViewRow cInvRic = (Acc_RicInvitiViewRow) voRic.getCurrentRow();
            Number idInv = (Number) cInvRic.getIdinvito();
            ViewObject voInvito = am.findViewObject("Acc_SoInvitoView1");
            voInvito.setWhereClauseParams(new Object[] { idInv });
            voInvito.executeQuery();

            Acc_SoInvitoViewRow fInv = (Acc_SoInvitoViewRow) voInvito.first();
            voInvito.setCurrentRow(fInv);
            Date dataApp = fInv.getDtapp();

            cAnam.setDtanamnesi(dataApp);
        }

        Row cInv = voInv.getCurrentRow();

        String codEsit = (String) cInv.getAttribute("Codesitoinvito");

        String metodo = (String) cInv.getAttribute("MetodoCalcolo");
        Integer dtObblig = (Integer) cInv.getAttribute("DataObbligatoria");
        if ("D".equals(metodo)) {
            Date dtEsame = (Date) cInv.getAttribute("Dtesamerecente");
            if (dtEsame == null && dtObblig != null && dtObblig.intValue() == 1) {
                String msg =
                    "Dato l'esito inserito e' necessario impostare la data dell'esame. Impossibile aggiornare.";
                this.handleException(msg, null);
                return false;
            }

        } else {
            cInv.setAttribute("Dtesamerecente", null);
        }

        // aggiornamento round e richiamo
        GeneratoreInviti gen = new GeneratoreInviti(am);
        try {
            gen.updateRoundIndiv(cInv);

            Integer idInvito = (Integer) cInv.getAttribute("Idinvito");
            String codEsInvNew = (String) cInv.getAttribute("Codesitoinvito");
            Integer idCtPrelNew = (Integer) cInv.getAttribute("Idcentroprelievo");
            Date dtAppNew = (Date) cInv.getAttribute("Dtapp");
            //20110404 Serra
            Date dtEsameRecNew = (Date) cInv.getAttribute("Dtesamerecente");

            boolean modifiche =
                SoggUtils.datiRichiamoModificati(am, idInvito, codEsInvNew, idCtPrelNew, dtAppNew, dtEsameRecNew);
            //fine

            if (modifiche)
                AccUtils.insRichiamo(am, cInv);

        } catch (Exception ex) {
            String msg = ex.getMessage();
            this.handleException(msg, null);
            this.doRollback();
            return false;
        }
        //

        // dati aggiornamento
        Date dtoraCorr = DateUtils.getOracleDateNow();
        String user = (String) session.get("user");

        cInv.setAttribute("Dtultimamod", dtoraCorr);
        cInv.setAttribute("Opmodifica", user);

        return true;
    }

    protected String afterSave() {
        /*DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject voInv = voInvIter.getViewObject();
        ViewHelper.queryAndRestoreCurrentRow(voInv);
        */
        return null;
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    public String onGoConsenso() {
        this.save();

        Map sess = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_SoInvitoView1Iterator");
        ViewObject voInv = voInvIter.getViewObject();

        Acc_AppModule am = (Acc_AppModule) voInv.getApplicationModule();
        Acc_SoInvitoViewRowImpl cInv = (Acc_SoInvitoViewRowImpl) voInv.getCurrentRow();

        Sogg_AppModule soggAm =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        // setto anagrafica corrente
        ViewObject voAnag = soggAm.findViewObject("Sogg_SoSoggettoView1");
        voAnag.setWhereClause("CODTS = '" + cInv.getCodts() + "' AND ULSS='"+ulss+"'");
        //voAnag.setWhereClauseParams(new Object[] { cInv.getCodts(), ulss });
        voAnag.executeQuery();
        Row fRow = voAnag.first();
        voAnag.setCurrentRow(fRow);

        ViewObject vo = soggAm.findViewObject("Sogg_SoSoggScrView1");
        String whScr =
            "Sogg_SoSoggScr.CODTS = '" + (String) cInv.getCodts() + "' and Sogg_SoSoggScr.ULSS = '" + ulss +
            "' and Sogg_SoSoggScr.TPSCR = '" + tpscr + "'";
        vo.setWhereClause(whScr);
        vo.executeQuery();
        Row r = vo.first();

        //se non esiste il record lo inserisco
        if (r == null) {
            r = vo.createRow();
            vo.insertRow(r);
            r.setAttribute("Codts", cInv.getCodts());
            r.setAttribute("Roundindiv", 0);
            r.setAttribute("Roundinviti", 0);
            r.setAttribute("Altorischio", ViewHelper.decodeByTpscr(tpscr, null, ConfigurationConstants.DB_FALSE, //altorischio e' valorizzato solo pe il colon
                                                                   null, null, null));
            r.setAttribute("Tpscr", tpscr);
            r.setAttribute("Ulss", ulss);
        }

        Cnf_AppModule amCnf =
            (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        ViewObject voFunz = amCnf.findViewObject("Cnf_SoCnfFunzView1");
        voFunz.setWhereClause("ULSS='" + ulss + "'");
        voFunz.executeQuery();

        Sogg_ConsensoParam bean =
            (Sogg_ConsensoParam) BindingContext.getCurrent().findDataControl("Sogg_ConsensoParamDataControl").getDataProvider();
        bean.resetCampi();

        String propagaConsenso = "S";
        try {
            ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
            propagaConsenso = ParametriSistema.getParamValue(voParam, ulss, tpscr, "propaga_consenso");
        } catch (Exception ex) {
            //per default il consenso va propagato
        }

        //siamo nel citologico
        if (tpscr.equals("CI")) {
            sess.put("consensoCI", Boolean.FALSE);

            if (voFunz.getFilteredRows("Tpscr", "MA").length > 0 && "S".equals(propagaConsenso)) {
                sess.put("consensoMA", Boolean.TRUE);
            } else {
                sess.put("consensoMA", Boolean.FALSE);
                bean.setPropagaMA(false);
            }

            if (voFunz.getFilteredRows("Tpscr", "CO").length > 0 && "S".equals(propagaConsenso)) {
                sess.put("consensoCO", Boolean.TRUE);
            } else {
                sess.put("consensoCO", Boolean.FALSE);
                bean.setPropagaCO(false);
            }
        }

        sess.put("showTabs", Boolean.TRUE);
        sess.put("LINK_ACC_INV", Boolean.TRUE);

        sess.remove("backConsensoOK");
        return "goConsenso";
    }

    protected boolean pendingUpdatesOnSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_SoInvitoView1Iterator");
        ViewObject voAnam = voIter.getViewObject();
        Acc_AppModule am = (Acc_AppModule) voAnam.getApplicationModule();

        boolean dirty = am.getTransaction().isDirty();
        return dirty;
    }
}
