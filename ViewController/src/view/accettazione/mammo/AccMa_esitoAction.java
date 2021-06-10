package view.accettazione.mammo;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;
import java.util.Map;

import javax.faces.event.ActionEvent;

import model.accettazione.common.AccMa_SoAccMammo1livViewRow;

import model.accettazione.common.AccMa_SoAccMammo2livViewRow;

import model.common.AccMa_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;

import model.inviti.GeneratoreInviti;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.commons.AccUtils;
import view.commons.action.Parent_DataForwardAction;

public class AccMa_esitoAction extends Parent_DataForwardAction {

    private RichForm frm;

    @Override
    protected void setAppModule() {
        this.amName = "AccMa_AppModule";
    }

    public void onAppl(ActionEvent actionEvent) {
        boolean saveOK = this.save();
    }

    public String onConf() {
        boolean saveOK = this.save();
        if (saveOK) {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            session.put("fromDett", Boolean.TRUE);
            return "acc_toMammo";
        }
        return null;
    }

    public void prevInvitoMammo(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.previous();
            AccUtils.procInvCorrenteMammo(voRic);
        }
    }

    public void nextInvitoMammo(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.next();
            AccUtils.procInvCorrenteMammo(voRic);
        }
    }

    protected boolean beforeSave() {
        boolean esitoSuper = super.beforeSave();
        if (!esitoSuper)
            return false;

        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        boolean escl = ((Boolean) sess.get("soggEscluso")).booleanValue();
        if (escl) {
            String msg = "La persona risulta esclusa. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("AccMa_SoInvitoView1Iterator");
        ViewObject voInv = voInvIter.getViewObject();
        Row cInv = voInv.getCurrentRow();

        Integer liv = (Integer) cInv.getAttribute("Livello");
        String codEsit = (String) cInv.getAttribute("Codesitoinvito");
        String metodo = (String) cInv.getAttribute("MetodoCalcolo");
        Integer dtObblig = (Integer) cInv.getAttribute("DataObbligatoria");
        if ("D".equals(metodo)) {
            Date dtEsame = (Date) cInv.getAttribute("Dtesamerecente");
            if (dtEsame == null && dtObblig != null && dtObblig.intValue() == 1) {
                String msg = "Dato l'esito inserito e' necessario impostare la data dell'esame. Impossibile aggiornare.";
                this.handleException(msg, null);
                return false;
            }

        } else {
            cInv.setAttribute("Dtesamerecente", null);
        }

        // aggiornamento round e richiamo
        AccMa_AppModule am = (AccMa_AppModule) voInv.getApplicationModule();
        GeneratoreInviti gen = new GeneratoreInviti(am);
        try {
            gen.updateRoundIndiv(cInv);

            Integer idInvito = (Integer) cInv.getAttribute("Idinvito");
            String codEsInvNew = (String) cInv.getAttribute("Codesitoinvito");
            Integer idCtPrelNew = (Integer) cInv.getAttribute("Idcentroprelievo");
            Date dtAppNew = (Date) cInv.getAttribute("Dtapp");
            Date dtEsameRecNew = (Date) cInv.getAttribute("Dtesamerecente");

            boolean modifiche =
                SoggUtils.datiRichiamoModificati(am, idInvito, codEsInvNew, idCtPrelNew, dtAppNew, dtEsameRecNew);

            if (modifiche)
                AccUtils.insRichiamoMA(am, cInv);

        } catch (Exception ex) {
            String msg = ex.getMessage();
            this.handleException(msg, null);
            this.doRollback();
            return false;
        }

        // dati aggiornamento
        Date dtoraCorr = DateUtils.getOracleDateNow();
        String user = (String) sess.get("user");

        cInv.setAttribute("Dtultimamod", dtoraCorr);
        cInv.setAttribute("Opmodifica", user);

        // riporto anamnesi se ci sono le condizioni
        if (codEsit.equals(ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO)) {
            String codTs = (String) cInv.getAttribute("Codts");
            String ulss = (String) cInv.getAttribute("Ulss");
            Integer idAcc = null;

            ViewObject voAnam = am.findViewObject("AccMa_SoAnamnesimxView1");

            if (liv.intValue() == 1) {
                ViewObject voAcc = am.findViewObject("AccMa_SoAccMammo1livView1");
                AccMa_SoAccMammo1livViewRow currAcc = (AccMa_SoAccMammo1livViewRow) voAcc.getCurrentRow();
                idAcc = currAcc.getIdaccma1liv();

                String wh = "IDACCMA_1LIV = " + idAcc.toString();
                voAnam.setWhereClause(wh);

            } else {
                ViewObject voAcc = am.findViewObject("AccMa_SoAccMammo2livView1");
                AccMa_SoAccMammo2livViewRow currAcc = (AccMa_SoAccMammo2livViewRow) voAcc.getCurrentRow();
                idAcc = currAcc.getIdaccma2liv();

                String wh = "IDACCMA_1LIV = " + idAcc.toString();
                voAnam.setWhereClause(wh);
            }

            voAnam.executeQuery();
            int countAnam = voAnam.getRowCount();

            if (countAnam == 0) {
                boolean riportata = AccUtils.riportaAnamMA(am, codTs, ulss, idAcc, liv.intValue(), user);
            }

        }

        return true;
    }

    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        if (dest.equals("ag_preGiorn")) {
            session.put("chAgenda", "accMa_preEsito.do");
        } else if (dest.equals("acc_toMammo")) {

            session.put("fromDett", Boolean.TRUE);
        } else {
            AccUtils.beforeNavAccMa(dest,true);
        }
        session.remove("initOK");
    }

    public void setFrm(RichForm frm) {
        this.frm = frm;
    }

    public RichForm getFrm() {
        if (frm == null)
            findForward();

        return frm;
    }

    protected void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("AccMa_SoInvitoView1Iterator");
        ViewObject voInvito = voInvIter.getViewObject();

        AccMa_AppModule am = (AccMa_AppModule) voInvito.getApplicationModule();
        Row rowInv = voInvito.getCurrentRow();

        // filtro esiti invito
        Integer livello = (Integer) rowInv.getAttribute("Livello");
        Date dataApp = (Date) rowInv.getAttribute("Dtapp");
        String strDataApp;

        // Se non c'e' la data appuntamento uso la data di sistema
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
        
        ViewObject voEsiti = am.findViewObject("AccMa_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'YYYY-MM-DD'))");
        voEsiti.executeQuery();
    }

    protected boolean pendingUpdatesOnSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoInvitoView1Iterator");
        ViewObject voAnam = voIter.getViewObject();
        AccMa_AppModule am = (AccMa_AppModule) voAnam.getApplicationModule();

        boolean dirty = am.getTransaction().isDirty();
        return dirty;
    }
}
