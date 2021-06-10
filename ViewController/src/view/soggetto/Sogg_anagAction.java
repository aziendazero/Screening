package view.soggetto;

import insiel.dataHandling.DateUtils;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.AccCa_AppModule;

import model.common.AccCo_AppModule;
import model.common.AccMa_AppModule;
import model.common.Acc_AppModule;
import model.common.Cnf_AppModule;
import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.AccCa_RicParam;
import model.datacontrol.Sogg_ConsensoParam;

import model.soggetto.common.Sogg_SoSoggettoViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.ReturnEvent;

import view.cittadinanza.LovCittadinanzaAction;

import view.commons.AccUtils;
import view.commons.AppConstants;
import view.commons.PageDescriptor;
import view.commons.action.Parent_DataForwardAction;

import view.comune.LovComuneAction;

import view.medico.LovMedicoAction;

import view.stato.LovStatoEsteroAction;

import view.util.Utility;

@SuppressWarnings("deprecation")
public class Sogg_anagAction extends Parent_DataForwardAction {
    private RichInputText cittadinanzaLOV;
    private RichInputText descrizioneLOV;
    private RichInputText codcomnasc;
    private RichInputText desstatoLOV;
    private RichInputText codst;
    private RichInputText codiceregmedico;
    private RichInputText cognmedLOV;
    private RichInputText nomemed;
    private RichInputText descomresLOV;
    private RichInputText codcomres;
    private RichInputText codcomdom;
    private RichInputText descomdomLOV;
    private RichInputText codcomscr;
    private RichInputText descomscrLOV;
    private RichPopup cfWarnPopup;
    private RichPopup classePopWarnPopup;

    public Sogg_anagAction() {
    }

    @Override
    protected void setAppModule() {
        this.amName = "Sogg_AppModule";
    }

    protected void findForward() {
        onChDataNascita();

        // Filtro le province per data attuale
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        
        ViewObject provinciaView = am.findViewObject("Sogg_ProvinciaView2");
        provinciaView.setWhereClause("(dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE)");
        provinciaView.executeQuery();
        
        //I00101237 Filtro le classi di popolazione per data attuale
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        
        String tpscr = (String) session.get("scr");
        ViewObject classePopView = am.findViewObject("Sogg_SoCnfClassePopView1");
        classePopView.setWhereClause("tpscr ='" + tpscr + "' AND scadenza IS NULL OR TRUNC(scadenza) >= TRUNC(SYSDATE)");
        classePopView.executeQuery();
    }

    public String onChCittadinanza() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        String cittadinanza = soggettoRow.getCittadinanza();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        if (cittadinanza == null || cittadinanza.length() == 0) {
            soggettoRow.setReleaseCodeCit(null);
            
            boolean onClick = true;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : true;
                }catch(Throwable th){
                    onClick = true;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                Utility.addScriptOnPartialRequest("setFocus('" + cittadinanzaLOV.getClientId() + "', false)");
                return null;
            }
        }
        
        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                LovCittadinanzaAction.checkOnLovFilter();
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                Utility.addScriptOnPartialRequest("setFocus('" + cittadinanzaLOV.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovCittadinanza";
    }

    @SuppressWarnings("unchecked")
    public void setParameterAndCallback(ClientEvent ce){
        ADFContext adfCtx = ADFContext.getCurrent();
        Map _m = ce.getParameters();
        System.out.println("---------------------> " + _m);
        adfCtx.getViewScope().putAll(_m);

        String _cb = null;
        if(_m.containsKey("callbackFunction")){
            try{
                _cb = _m.get("callbackFunction") != null ? _m.get("callbackFunction").toString() : null;
            }catch(Throwable th){
                _cb = null;
            }
        }
        
        if(_cb!=null)
            Utility.addScriptOnPartialRequest(_cb);
    }

    public void onChDataNascita() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        int cittadinanza = soggettoRow.getReleaseCodeCit() != null ? soggettoRow.getReleaseCodeCit().intValue() : 0;

        // Filtro le province per data di nascita
        ViewObject voProv = am.findViewObject("Sogg_ProvinciaView1");
        voProv.setWhereClause(null);
        voProv.setWhereClauseParams(null);

        // Se cittadinana sconosciuta o italiana
        if (cittadinanza == 0 || cittadinanza == 1) {
            voProv.setWhereClause("(dtiniziovalidita IS NULL OR dtiniziovalidita <= :1) AND (dtfinevalidita IS NULL OR dtfinevalidita > :2)");
            voProv.setWhereClauseParam(0, soggettoRow.getDataNascita());
            voProv.setWhereClauseParam(1, soggettoRow.getDataNascita());
        } else {
            voProv.setWhereClause("(dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE)");
        }

        // TODO: Aggiungere la provincia attuale, se non gia' presente nella select

        voProv.executeQuery();
    }

    public void onChProv(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        soggettoRow.setDescrizione(null);
        soggettoRow.setCodcomnascita(null);
        soggettoRow.setReleaseCodeComNas(null);
    }

    public void onChProvScr(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Row cAnag = voAnag.getCurrentRow();
        cAnag.setAttribute("Descomscr", null);
        cAnag.setAttribute("Codcomscr", null);
    }

    public void onChProvDom(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Row cAnag = voAnag.getCurrentRow();
        cAnag.setAttribute("Descomdom", null);
        cAnag.setAttribute("Codcomdom", null);
    }

    public void onChProvres(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Row cAnag = voAnag.getCurrentRow();
        cAnag.setAttribute("Descomres", null);
        cAnag.setAttribute("Codcomres", null);
        //20120111 Serra: disbailito filtro su zona
        // SoggUtils.filtraDistretti(ctx,null,"Sogg_DistrettiView2");
        //20110111 fine
    }

    @SuppressWarnings("unchecked")
    public String onChComNasc() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cAnag = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        String desCom = cAnag.getDescrizione();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        if (desCom == null || desCom.equals("")) {
            cAnag.setCodcomnascita(null);
            cAnag.setReleaseCodeComNas(null);
            
            boolean onClick = false;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : false;
                }catch(Throwable th){
                    onClick = false;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                Utility.addScriptOnPartialRequest("setFocus('" + descrizioneLOV.getClientId() + "', false)");
                return null;
            }
        }
        
        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                Map page = adfCtx.getPageFlowScope();
                page.put("iteratorBinding", req.get("iteratorBinding"));
                page.put("source", req.get("source"));
                
                LovComuneAction.checkOnLovFilter();
                
                page.remove("iteratorBinding");
                page.remove("source");                
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                Utility.addScriptOnPartialRequest("setFocus('" + descrizioneLOV.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovComune";
    }

    @SuppressWarnings("unchecked")
    public String onChComRes() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cAnag = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        String desCom = cAnag.getDescomres();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        if (desCom == null || desCom.equals("")) {
            cAnag.setCodcomres(null);
            cAnag.setReleaseCodeComRes(null);
            
            boolean onClick = false;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : false;
                }catch(Throwable th){
                    onClick = false;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                Utility.addScriptOnPartialRequest("setFocus('" + descomresLOV.getClientId() + "', false)");
                return null;
            }
        }
        
        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                Map page = adfCtx.getPageFlowScope();
                page.put("iteratorBinding", req.get("iteratorBinding"));
                page.put("source", req.get("source"));
                
                LovComuneAction.checkOnLovFilter();
                
                page.remove("iteratorBinding");
                page.remove("source");                
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                Utility.addScriptOnPartialRequest("setFocus('" + descomresLOV.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovComune";
    }

    @SuppressWarnings("unchecked")
    public String onChComDom() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cAnag = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        String desCom = cAnag.getDescomdom();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        if (desCom == null || desCom.equals("")) {
            cAnag.setCodcomdom(null);
            cAnag.setReleaseCodeComDom(null);
            
            boolean onClick = false;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : false;
                }catch(Throwable th){
                    onClick = false;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                Utility.addScriptOnPartialRequest("setFocus('" + descomdomLOV.getClientId() + "', false)");
                return null;
            }
        }
        
        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                Map page = adfCtx.getPageFlowScope();
                page.put("iteratorBinding", req.get("iteratorBinding"));
                page.put("source", req.get("source"));
                
                LovComuneAction.checkOnLovFilter();
                
                page.remove("iteratorBinding");
                page.remove("source");                
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                Utility.addScriptOnPartialRequest("setFocus('" + descomdomLOV.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovComune";
    }

    public String onChComScr() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cAnag = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        String desCom = cAnag.getDescomscr();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        if (desCom == null || desCom.equals("")) {
            cAnag.setCodcomscr(null);
            cAnag.setReleaseCodeComScr(null);
            
            boolean onClick = false;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : false;
                }catch(Throwable th){
                    onClick = false;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                Utility.addScriptOnPartialRequest("setFocus('" + descomscrLOV.getClientId() + "', false)");
                return null;
            }
        }

        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                Map page = adfCtx.getPageFlowScope();
                page.put("iteratorBinding", req.get("iteratorBinding"));
                page.put("source", req.get("source"));
                
                LovComuneAction.checkOnLovFilter();
                
                page.remove("iteratorBinding");
                page.remove("source");                
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                Utility.addScriptOnPartialRequest("setFocus('" + descomscrLOV.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovComune";
    }

    public String onChStato() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cAnag = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        String desStato = cAnag.getDesstato();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        if (desStato == null || desStato.equals("")) {
            cAnag.setCodst(null);
            cAnag.setReleaseCodeStNas(null);
            
            boolean onClick = false;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : false;
                }catch(Throwable th){
                    onClick = false;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                Utility.addScriptOnPartialRequest("setFocus('" + desstatoLOV.getClientId() + "', false)");
                return null;
            }
        }
        
        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                LovStatoEsteroAction.checkOnLovFilter();
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                Utility.addScriptOnPartialRequest("setFocus('" + desstatoLOV.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovStato";
    }

    public String onChMed() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cAnag = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        String cognMed = cAnag.getCognmed();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        if (cognMed == null || cognMed.equals("")) {
            cAnag.setCodiceregmedico(null);
            
            boolean onClick = false;
            if(req.containsKey("onClick")){
                try{
                    onClick = req.get("onClick") != null ? (Boolean)req.get("onClick") : false;
                }catch(Throwable th){
                    onClick = false;
                }
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(!onClick){
                Utility.addScriptOnPartialRequest("setFocus('" + cognmedLOV.getClientId() + "', false)");
                return null;
            }
        }
        
        if(req.containsKey("onOneReturn")){
            boolean onOneReturn = false;
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            if(onOneReturn){
                Map page = adfCtx.getPageFlowScope();
                page.put("source", req.get("source"));

                LovMedicoAction.checkOnLovFilter();
                                
                page.remove("source");                
            }
            
            try{
                onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
            }catch(Throwable th){
                onOneReturn = false;
            }
            
            adfCtx.getViewScope().remove("onClick");
            adfCtx.getViewScope().remove("onOneReturn");
            if(onOneReturn){
                Utility.addScriptOnPartialRequest("setFocus('" + cognmedLOV.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovMedico";
    }

    public String onConf() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String tpscr = (String) sess.get("scr");
        
        boolean checkClasseOk = true;
        
        //I00101237 - SCADENZA CATEGORIA POPOLAZIONE
        if ("CO".equals(tpscr)){
            boolean bypassCheck = adfCtx.getViewScope().containsKey("bypassCheckPop");
            
            if (!bypassCheck)
                checkClasseOk = this.checkClassePop(am, adfCtx);
            
            adfCtx.getViewScope().remove("bypassCheckPop");
            
        }
        
        if (checkClasseOk && this.save()) {
            ADFContext.getCurrent().getSessionScope().remove("FROM_AUR");

            PageDescriptor page =
                (PageDescriptor) ADFContext.getCurrent().getSessionScope().get(AppConstants.FROM_PAGE);
            if (page != null) {
                ADFContext.getCurrent().getSessionScope().remove(AppConstants.FROM_PAGE);
                ADFContext.getCurrent().getSessionScope().put("fromDett", Boolean.TRUE);
                return page.getAction();
            } else {
                return "goRic";
            }
        }
        
        return null;
    }
    
    public String onAppl() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String tpscr = (String) sess.get("scr");
        
        boolean checkClasseOk = true;
        
        //I00101237 - SCADENZA CATEGORIA POPOLAZIONE
        if ("CO".equals(tpscr)){
            boolean bypassCheck = adfCtx.getViewScope().containsKey("bypassCheckPop");
            
            if (!bypassCheck)
                checkClasseOk = this.checkClassePop(am, adfCtx);
            
            adfCtx.getViewScope().remove("bypassCheckPop");
        }
        
        if (checkClasseOk && this.save()) {
            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            voAnag.executeQuery();
            AdfFacesContext.getCurrentInstance().addPartialTarget(cittadinanzaLOV); 
            AdfFacesContext.getCurrentInstance().addPartialTarget(descomresLOV); 
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private boolean checkClassePop(Sogg_AppModule am, ADFContext adfCtx) {

        ViewObject soggscrView = am.findViewObject("Sogg_SoSoggScrView1");
        Row soggScr = soggscrView.getCurrentRow();
        
        if (soggScr!=null){
            String tpscr = (String) soggScr.getAttribute("Tpscr");
            String codClassePop = (String) soggScr.getAttribute("Codclassepop");
            
            if (codClassePop!=null && tpscr!=null){
                String query = "SELECT scadenza, descrizione FROM so_cnf_classe_pop " +
                    "WHERE tpscr='" + tpscr + "' AND codclasse = '" + codClassePop + "'";
                
                ViewObject vo = am.createViewObjectFromQueryStmt("", query);
                vo.setRangeSize(-1);
                vo.executeQuery();
                
                Row res = vo.first();
                
                if (res != null){
                    Date scad = (Date)res.getAttribute(0);
                    String descClassePop = res.getAttribute(1).toString();
                    
                    if(scad!=null){
                        java.util.Date scadenza = new java.util.Date(scad.dateValue().getTime());

                        if (scadenza!=null && scadenza.before(new java.util.Date())){
                            
                            adfCtx.getViewScope().put("descClassePop", descClassePop);
                            
                            if(this.classePopWarnPopup==null)
                                this.classePopWarnPopup = new RichPopup();
                            
                            this.classePopWarnPopup.show(new RichPopup.PopupHints());
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }

    @SuppressWarnings("unchecked")
    protected void beforeNavigate(String dest) throws Exception {
        //19092011 Gaion setto la variabile LINK_ACC a false prima di navigare con qualsiasi pulsante
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.put("LINK_ACC", Boolean.FALSE);
        session.put("LINK_REF", Boolean.FALSE);
        session.remove("FROM_AUR");

        SoggUtils.beforeNavSogg(dest, false);
    }

    @SuppressWarnings("unchecked")
    protected boolean beforeSave() {
        // mauro 29/11/2010 controllo consistenza codice tessera
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        Boolean erroreTessera = (Boolean) session.get("erroreTessera");
        if (erroreTessera != null) {
            String msg =
                "Attenzione: il programma ha rilevato un tentativo di sovrascrittura di dati anagrafici. " +
                "Per tale ragione in questa sessione non e' piu' possibile apportare modifiche su dati anagrafici. " +
                "Si invita a chiudere tutte le finestre del browser ed a riavviare l'applicazione " +
                "utilizzando una sola finestra del browser.";
            this.handleException(msg, null);
            return false;
        }
        //

        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cAnag = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();

        // mauro 29/11/2010 controllo consistenza codice tessera
        String codts = cAnag.getCodts();
        String ulss = (String) session.get("ulss");
        Map req = adfCtx.getRequestScope();

        String ts_page = (String) req.get("ts_page");
        // 14052012 gaion: con il pop-up aperto il controllo sulla tessera non puo' funzionare
        if (ts_page != null && !codts.equals(ts_page)) {
            session.put("erroreTessera", Boolean.TRUE);
            String msg =
                "Attenzione: il programma ha rilevato un tentativo di sovrascrittura di dati anagrafici. " +
                "La modifica non verra' salvata e, in questa sessione, non sara' piu' possibile apportare modifiche su dati anagrafici. " +
                "Si invita a chiudere tutte le finestre del browser ed a riavviare l'applicazione " +
                "utilizzando una sola finestra del browser.";
            this.handleException(msg, null);
            return false;
        }
        //

        // mauro 2-10-2009, controllo che il cap sia numerico
        String capRes = cAnag.getCapComres();
        String capDom = cAnag.getCapComdom();
        String capScr = cAnag.getCapComsrc();

        if (capRes != null && !capRes.equals("")) {
            try {
                Integer.parseInt(capRes);
            } catch (Exception ex) {
                String msg = "Nel CAP di residenza e' necessario impostare un valore numerico. Aggiornamento fallito.";
                this.handleException(msg, null);
                return false;
            }
        }

        if (capDom != null && !capDom.equals("")) {
            try {
                Integer.parseInt(capDom);
            } catch (Exception ex) {
                String msg = "Nel CAP di domicilio e' necessario impostare un valore numerico. Aggiornamento fallito.";
                this.handleException(msg, null);
                return false;
            }
        }

        if (capScr != null && !capScr.equals("")) {
            try {
                Integer.parseInt(capScr);
            } catch (Exception ex) {
                String msg = "Nel CAP di screening e' necessario impostare un valore numerico. Aggiornamento fallito.";
                this.handleException(msg, null);
                return false;
            }
        }

        String comNasc = cAnag.getCodcomnascita();
        String codSt = cAnag.getCodst();

        if (comNasc != null && codSt != null) {
            String msg =
                "E' necessario specificare un solo dato fra il comune e lo stato estero " +
                "di nascita. Aggiornamento fallito.";
            this.handleException(msg, null);
            return false;
        } else if (comNasc == null && codSt == null) {
            String msg =
                "E' necessario specificare o il comune o lo stato estero " +
                "di nascita. Aggiornamento fallito.";
            this.handleException(msg, null);
            return false;
        }

        // controllo CF
        String evaluator = "^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$";
        if(!adfCtx.getViewScope().containsKey("bypassCheckCF") && (cAnag.getCf()==null || !cAnag.getCf().matches(evaluator))){
            if(this.cfWarnPopup==null)
                this.cfWarnPopup = new RichPopup();
            
            this.cfWarnPopup.show(new RichPopup.PopupHints());
            return false;
        }
        
        // controllo conferma cellulare
        if(!verificaConfermaCellulare())
            return false;

        // controlli su recapiti
        Integer sitAnag = cAnag.getCodanagreg();
        if (sitAnag.equals(ConfigurationConstants.CODICE_DOMICILIATO)) {
            String codDom = cAnag.getCodcomdom();
            String indDom = cAnag.getIndirizzoDom();

            if (codDom == null || codDom.equals("") || indDom == null || indDom.equals("")) {
                String msg =
                    "Per i pazienti domiciliati e' necessario specificare il" +
                    " comune e l'indirizzo di domicilio. Aggiornamento fallito";
                this.handleException(msg, null);
                return false;
            }
        } else {
            String indRes = cAnag.getIndirizzoRes();
            if (indRes == null || indRes.equals("")) {
                String msg =
                    "Per i pazienti non domiciliati e' necessario specificare " +
                    "l'indirizzo di residenza. Aggiornamento fallito";
                this.handleException(msg, null);
                return false;
            }
        }

        String comScr = cAnag.getCodcomscr();
        String indScr = cAnag.getIndirizzoScr();

        if (comScr == null || comScr.equals("")) {
            if (indScr != null && !indScr.equals("")) {
                String msg =
                    "I campi relativi a comune di screening e indirizzo " +
                    "di screening devono essere entrambi valorizzati o entrambi " +
                    "non valorizzati. Aggiornamento fallito.";
                this.handleException(msg, null);
                return false;
            }
        } else {
            if (indScr == null || indScr.equals("")) {
                String msg =
                    "I campi relativi a comune di screening e indirizzo " +
                    "di screening devono essere entrambi valorizzati o entrambi " +
                    "non valorizzati. Aggiornamento fallito.";
                this.handleException(msg, null);
                return false;
            }
        }
        //

        Date dtoraCorr = DateUtils.getOracleDateNow();
        String user = (String) session.get("user");
        cAnag.setDtultmodifica(dtoraCorr);
        cAnag.setOpultmodifica(user);

        //se l'azienda lavora con i centri preconfigurati
        if (((Boolean) session.get("modalita_centri")).booleanValue() ||
            ((Boolean) session.get("centri_rich_cnf")).booleanValue()) {
            String tpscr = (String) session.get("scr");
            

            //quando inserisco un nuovo soggetto vado anche ad inserire un record in so_sogg_scr
            try {
                ViewObject vo = am.findViewObject("Sogg_SoSoggScrView1");
                //vedo se ho gia' inserito il record in un precedente tentaivo di salvatggio
                Row r = vo.getCurrentRow();
                if (r == null) {

                    vo = am.findViewObject("Sogg_SoSoggScrView2");

                    //vedo se ho gia' inserito il record in un precedente tentativo di salvatggio
                    r = vo.createRow();
                    vo.insertRow(r);

                    r.setAttribute("Codts", cAnag.getCodts());
                    r.setAttribute("Roundindiv", new Integer(0));
                    r.setAttribute("Roundinviti", new Integer(0));
                    r.setAttribute("Altorischio", ViewHelper.decodeByTpscr(tpscr, null, ConfigurationConstants.DB_FALSE, //altorischio e' valorizzato solo per il colon
                                                                           null, null, null));
                    r.setAttribute("VaccinatoHpv", new Integer(0));
                    r.setAttribute("Consenso", new Integer(0));
                    r.setAttribute("ConsensoCond", new Integer(0));
                    r.setAttribute("Tpscr", tpscr);
                    r.setAttribute("Ulss", ulss);

                }
            } catch (Exception e) {
                this.handleException(e, null);
                return false;
            }
        }

        //se il soggetto e' nuovo
        if (!((Boolean) session.get("anagEsiste")).booleanValue()) {
            voAnag.setWhereClause("CODTS = '" + codts + "' AND ULSS='"+ulss+"'");
            session.put("anagEsiste", Boolean.TRUE);
        }
               
        voAnag.executeQuery();

        return true;
    }

    protected boolean pendingUpdatesOnSave() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        boolean mod = am.getTransaction().isDirty();
        return mod;
    }

    public String onUndoAur() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        am.doRollback("Sogg_RicercaView1");

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        //session.remove("FROM_AUR");
        
        return "aurSearch";
    }

    public String onUndoAcc() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        am.doRollback("Sogg_RicercaView1");

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        
        String  scr = (String) session.get("scr");
        switch (scr)
          {
          case "CI":
            return to_acc_query();
          case "MA":
            return to_accMa_query();
          case "CO":
            return to_accCo_query();
          case "CA":
            return to_accCa_query();
          default:
            return "nothing";
          }
    }
    
    private String to_acc_query(){
        //200911 Gaion mantiene la selezione nella selezione multipla
        Acc_AppModule am = (Acc_AppModule) BindingContext.getCurrent().
          findDataControl("Acc_AppModuleDataControl").getDataProvider();

        AccUtils.requeryElenco(am);

        ViewObject voRic = am.findViewObject("Acc_RicInvitiView1");
        Row cInv = voRic.getCurrentRow();
        if (cInv != null)
          cInv.setAttribute("Selezionato",Boolean.TRUE);
        // fine Gaion
        
        //filtro gli esisti
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");  
        ViewObject voEsiti = am.findViewObject("Acc_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND LIVESITO = " + cInv.getAttribute("Livello"));
        voEsiti.executeQuery();
        
        return onBack();
    }

    private String to_accMa_query(){
        //200911 Gaion mantiene la selezione nella selezione multipla
        AccMa_AppModule am = (AccMa_AppModule) BindingContext.getCurrent().
          findDataControl("AccMa_AppModuleDataControl").getDataProvider();

        AccUtils.requeryElencoMammo(am);

        ViewObject voRic = am.findViewObject("AccMa_RicInvitiView1");
        Row cInv = voRic.getCurrentRow();
        if (cInv != null)
          cInv.setAttribute("Selezionato",Boolean.TRUE);
        // fine Gaion
        
        //filtro gli esisti
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");  
        ViewObject voEsiti = am.findViewObject("AccMa_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND LIVESITO = " + cInv.getAttribute("Livello"));
        voEsiti.executeQuery();
        
        return onBack();
    }

    private String to_accCo_query(){
        //200911 Gaion mantiene la selezione nella selezione multipla
        AccCo_AppModule am = (AccCo_AppModule) BindingContext.getCurrent().
          findDataControl("AccCo_AppModuleDataControl").getDataProvider();

        AccUtils.requeryElencoColon(am);

        ViewObject voRic = am.findViewObject("AccCo_RicInvitiView1");
        Row cInv = voRic.getCurrentRow();
        if (cInv != null)
          cInv.setAttribute("Selezionato",Boolean.TRUE);
        // fine Gaion
        
        //filtro gli esisti
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");  
        ViewObject voEsiti = am.findViewObject("AccCo_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND LIVESITO = " + cInv.getAttribute("Livello"));
        voEsiti.executeQuery();
        
        return onBack();
    }

    private String to_accCa_query(){
        //200911 Gaion mantiene la selezione nella selezione multipla
        AccCa_AppModule am = (AccCa_AppModule) BindingContext.getCurrent().
          findDataControl("AccCa_AppModuleDataControl").getDataProvider();

        AccUtils.requeryElencoCardio();

        ViewObject voRic = am.findViewObject("AccCa_RicInvitiView1");
        Row cInv = voRic.getCurrentRow();
        if (cInv != null)
          cInv.setAttribute("Selezionato",Boolean.TRUE);
        // fine Gaion
        
        //filtro gli esisti
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");  
        ViewObject voEsiti = am.findViewObject("AccCa_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND LIVESITO = " + cInv.getAttribute("Livello"));
        voEsiti.executeQuery();

        return onBack();
    }

    @SuppressWarnings({ "unchecked", "oracle.jdeveloper.java.insufficient-catch-block" })
    public String onGoConsenso() {

        this.save();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cAnag = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
        ViewObject vo = am.findViewObject("Sogg_SoSoggScrView1");
        String whScr =
            "Sogg_SoSoggScr.CODTS = '" + cAnag.getCodts() + "' and Sogg_SoSoggScr.ULSS = '" + ulss +
            "' and Sogg_SoSoggScr.TPSCR = '" + tpscr + "'";
        vo.setWhereClause(whScr);
        vo.executeQuery();
        Row r = vo.getCurrentRow();
        //se non esiste il record lo inserisco
        if (r == null) {
            r = vo.createRow();
            vo.insertRow(r);
            r.setAttribute("Codts", cAnag.getCodts());
            r.setAttribute("Roundindiv", new Integer(0));
            r.setAttribute("Roundinviti", new Integer(0));
            r.setAttribute("Altorischio", ViewHelper.decodeByTpscr(tpscr, null, ConfigurationConstants.DB_FALSE, //altorischio e' valorizzato solo pe il colon
                                                                   null, null, null));
            r.setAttribute("Consenso", new Integer(0));
            r.setAttribute("ConsensoCond", new Integer(0));
            r.setAttribute("VaccinatoHpv", new Integer(0));
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

        if (tpscr.equals("MA")) {
            if (voFunz.getFilteredRows("Tpscr", "CI").length > 0 && "S".equals(propagaConsenso)) {
                sess.put("consensoCI", Boolean.TRUE);
            } else {
                sess.put("consensoCI", Boolean.FALSE);
                bean.setPropagaCI(false);
            }

            sess.put("consensoMA", Boolean.FALSE);
            if (voFunz.getFilteredRows("Tpscr", "CO").length > 0 && "S".equals(propagaConsenso)) {
                sess.put("consensoCO", Boolean.TRUE);
            } else {
                sess.put("consensoCO", Boolean.FALSE);
                bean.setPropagaCO(false);
            }
        }

        if (tpscr.equals("CO")) {
            if (cAnag.getSesso().equals("F")) {
                if (voFunz.getFilteredRows("Tpscr", "CI").length > 0 && "S".equals(propagaConsenso)) {
                    sess.put("consensoCI", Boolean.TRUE);
                } else {
                    sess.put("consensoCI", Boolean.FALSE);
                    bean.setPropagaCI(false);
                }

                if (voFunz.getFilteredRows("Tpscr", "MA").length > 0 && "S".equals(propagaConsenso))
                    sess.put("consensoMA", Boolean.TRUE);
                else {
                    sess.put("consensoMA", Boolean.FALSE);
                    bean.setPropagaMA(false);
                }
            } else { //maschio
                sess.put("consensoCI", Boolean.FALSE);
                sess.put("consensoMA", Boolean.FALSE);
                bean.setPropagaMA(false);
                bean.setPropagaCI(false);
            }
            sess.put("consensoCO", Boolean.FALSE);
        }
        sess.remove("backConsensoOK");
        return "goConsenso";

    }

    public String onBack() {
        Map sess =  ADFContext.getCurrent().getSessionScope();
        Map req = ADFContext.getCurrent().getRequestScope();
        PageDescriptor page = (PageDescriptor)sess.get(AppConstants.FROM_PAGE);
        String destination=null;
        if (page != null) {
            ADFContext.getCurrent().getSessionScope().put("fromDett", Boolean.TRUE);
            destination = page.getAction();
            if (pendingUpdatesOnSave()) {
                sess.remove(AppConstants.FROM_PAGE);
                return this.onRichnav(destination, new HashMap());
            } else {
                try {
                    AccCa_RicParam beanAccCa =
                        (AccCa_RicParam) BindingContext.getCurrent().findDataControl("AccCa_RicParamDataControl").getDataProvider();
                    beanAccCa.setNavigazione(Boolean.TRUE);
                    beforeNavigate(destination);
                    return destination;
                } catch (Exception ex) {
                    handleException("Impossibile navigare verso la destinazione " + destination + ": " +
                                    ex.getMessage(), null);
                }
            }
        }
        return destination;
    }

    public void chDataNascita(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        onChDataNascita();
    }

    public void setCittadinanzaLOV(RichInputText cittadinanzaLOV) {
        this.cittadinanzaLOV = cittadinanzaLOV;
    }

    public RichInputText getCittadinanzaLOV() {
        if(cittadinanzaLOV==null)
            cittadinanzaLOV = new RichInputText();
        return cittadinanzaLOV;
    }

    @SuppressWarnings("unused")
    public void lovCittadinanzaReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(cittadinanzaLOV);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + cittadinanzaLOV.getClientId() + "', false)");
    }

    @SuppressWarnings("unused")
    public void lovDescrizioneReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(descrizioneLOV);
        RequestContext.getCurrentInstance().addPartialTarget(codcomnasc);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + descrizioneLOV.getClientId() + "', false)");        
    }
    
    @SuppressWarnings("unused")
    public void lovDesstatoReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(desstatoLOV);
        RequestContext.getCurrentInstance().addPartialTarget(codst);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + desstatoLOV.getClientId() + "', false)");
    }
    
    @SuppressWarnings("unused")
    public void lovCognmedReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(cognmedLOV);
        RequestContext.getCurrentInstance().addPartialTarget(codiceregmedico);
        RequestContext.getCurrentInstance().addPartialTarget(nomemed);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + cognmedLOV.getClientId() + "', false)");        
    }

    public void setDescrizioneLOV(RichInputText descrizioneLOV) {
        this.descrizioneLOV = descrizioneLOV;
    }

    public RichInputText getDescrizioneLOV() {
        return descrizioneLOV;
    }

    public void setCodcomnasc(RichInputText codcomnasc) {
        this.codcomnasc = codcomnasc;
    }

    public RichInputText getCodcomnasc() {
        return codcomnasc;
    }

    public void setDesstatoLOV(RichInputText desstatoLOV) {
        this.desstatoLOV = desstatoLOV;
    }

    public RichInputText getDesstatoLOV() {
        return desstatoLOV;
    }

    public void setCodst(RichInputText codst) {
        this.codst = codst;
    }

    public RichInputText getCodst() {
        return codst;
    }

    public void setCodiceregmedico(RichInputText codiceregmedico) {
        this.codiceregmedico = codiceregmedico;
    }

    public RichInputText getCodiceregmedico() {
        return codiceregmedico;
    }

    public void setCognmedLOV(RichInputText cognmedLOV) {
        this.cognmedLOV = cognmedLOV;
    }

    public RichInputText getCognmedLOV() {
        return cognmedLOV;
    }

    public void setNomemed(RichInputText nomemed) {
        this.nomemed = nomemed;
    }

    public RichInputText getNomemed() {
        return nomemed;
    }

    public void setDescomresLOV(RichInputText descomresLOV) {
        this.descomresLOV = descomresLOV;
    }

    public RichInputText getDescomresLOV() {
        return descomresLOV;
    }

    public void lovDescomresReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(descomresLOV);
        RequestContext.getCurrentInstance().addPartialTarget(codcomres);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + descomresLOV.getClientId() + "', false)");        
    }

    public void setCodcomres(RichInputText codcomres) {
        this.codcomres = codcomres;
    }

    public RichInputText getCodcomres() {
        return codcomres;
    }

    public void setCodcomdom(RichInputText codcomdom) {
        this.codcomdom = codcomdom;
    }

    public RichInputText getCodcomdom() {
        return codcomdom;
    }

    public void setDescomdomLOV(RichInputText descomdomLOV) {
        this.descomdomLOV = descomdomLOV;
    }

    public RichInputText getDescomdomLOV() {
        return descomdomLOV;
    }

    public void lovDescomdomReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(descomdomLOV);
        RequestContext.getCurrentInstance().addPartialTarget(codcomdom);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + descomdomLOV.getClientId() + "', false)");        
    }

    public void setCodcomscr(RichInputText codcomscr) {
        this.codcomscr = codcomscr;
    }

    public RichInputText getCodcomscr() {
        return codcomscr;
    }

    public void setDescomscrLOV(RichInputText descomscrLOV) {
        this.descomscrLOV = descomscrLOV;
    }

    public RichInputText getDescomscrLOV() {
        return descomscrLOV;
    }

    public void lovDescomscrReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(descomscrLOV);
        RequestContext.getCurrentInstance().addPartialTarget(codcomscr);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + descomscrLOV.getClientId() + "', false)");        
    }

    public void setCfWarnPopup(RichPopup cfWarnPopup) {
        this.cfWarnPopup = cfWarnPopup;
    }

    public RichPopup getCfWarnPopup() {
        return cfWarnPopup;
    }

    public void setClassePopWarnPopup(RichPopup classePopWarnPopup) {
        this.classePopWarnPopup = classePopWarnPopup;
    }

    public RichPopup getClassePopWarnPopup() {
        return classePopWarnPopup;
    }
}
