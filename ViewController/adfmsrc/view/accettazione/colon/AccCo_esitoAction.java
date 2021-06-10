package view.accettazione.colon;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;
import java.util.Map;

import javax.faces.event.ActionEvent;

import model.common.AccCo_AppModule;

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

public class AccCo_esitoAction extends Parent_DataForwardAction {
    private RichForm frm;

    @Override
    protected void setAppModule() {
        this.amName = "AccCo_AppModule";
    }

    public AccCo_esitoAction() {
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
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("AccCo_SoInvitoView1Iterator");
        ViewObject voInvito = voInvIter.getViewObject();

        AccCo_AppModule am = (AccCo_AppModule) voInvito.getApplicationModule();
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
        
        ViewObject voEsiti = am.findViewObject("AccCo_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'YYYY-MM-DD'))");
        voEsiti.executeQuery();
    }

    public void prevInvitoColon(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.previous();
            AccUtils.procInvCorrenteColon(voRic);
        }
    }

    public void nextInvitoColon(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.next();
            AccUtils.procInvCorrenteColon(voRic);
        }
    }

    public String onConf() {
        boolean saveOK = this.save();
        if (saveOK) {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            session.put("fromDett", Boolean.TRUE);
            return "acc_toColon";
        }
        return null;
    }

    public void onAppl(ActionEvent actionEvent) {
        boolean saveOK = this.save();
    }

    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        if (dest.equals("ag_preGiorn")) {
            session.put("chAgenda", "accCo_preEsito.do");
        } else if (dest.equals("acc_toColon")) {
            session.put("fromDett", Boolean.TRUE);
        } else {
            AccUtils.beforeNavAccCo(dest, true);
        }
    }

    protected boolean beforeSave() {
        // TODO:  Override this view.commons.DetAccCo_DataForwardAction method
        boolean esitoSuper = super.beforeSave();
        if (!esitoSuper)
            return false;

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("AccCo_SoInvitoView1Iterator");
        ViewObject voInv = voInvIter.getViewObject();
        Row cInv = voInv.getCurrentRow();


        Integer liv = (Integer) cInv.getAttribute("Livello");
        String codEsit = (String) cInv.getAttribute("Codesitoinvito");
        // gaion 07/06/2011
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

        /* if (liv.intValue() == 1 && (codEsit.equals(ConfigurationConstants.CODICE_ESITO_COLONSCOPIA_RECENTE_DOC)
        || codEsit.equals(ConfigurationConstants.CODICE_ESITO_SANGUEOCC_RECENTE_DOC)))
        {
          Date dtEsame = (Date) cInv.getAttribute("Dtesamerecente");
          if (dtEsame == null)
          {
            String msg = "Dato l'esito inserito e' necessario impostare la data dell'esame. Impossibile aggiornare.";
            this.handleException(ctx,msg,null);
            return false;
          }

        }//20110404 Serra:
        else if (liv.intValue()==2 && codEsit.equals(ConfigurationConstants.CODICE_ESITO_ESAME_RECENTE_2LIV))
        {
          //non si f anulla perche' non e' obbligatorio
        }
        // fine
        else
        {
          cInv.setAttribute("Dtesamerecente",null);
        }*/
        // fine gaion 07/06/2011

        if (!codEsit.equals(ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO)) {
            cInv.setAttribute("Dtconscont", null);
            if (!codEsit.equals(ConfigurationConstants.CODICE_ESITO_CONTENITORE_RITIRATO)) {
                cInv.setAttribute("Dtritcont", null);
            }
        }


        // aggiornamento round e richiamo
        AccCo_AppModule am = (AccCo_AppModule) voInv.getApplicationModule();
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
                AccUtils.insRichiamoCO(am, cInv);

        } catch (Exception ex) {
            String msg = ex.getMessage();
            this.handleException(msg, null);
            this.doRollback();
            return false;
        }
        //

        // dati aggiornamento
        Date dtoraCorr = DateUtils.getOracleDateNow();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String user = (String) session.get("user");

        cInv.setAttribute("Dtultimamod", dtoraCorr);
        cInv.setAttribute("Opmodifica", user);

        return true;

    }

    protected boolean pendingUpdatesOnSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccCo_SoAccColon2livView1Iterator");
        ViewObject voAnam = voIter.getViewObject();
        AccCo_AppModule am = (AccCo_AppModule) voAnam.getApplicationModule();

        boolean dirty = am.getTransaction().isDirty();
        return dirty;
    }
}
