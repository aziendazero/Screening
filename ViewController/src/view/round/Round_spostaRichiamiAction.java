package view.round;

import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.Round_AppModule;

import model.commons.ConfigurationReader;
import model.commons.SoggUtils;

import model.datacontrol.Round_ParamSpostaRich;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.ReturnEvent;

import view.commons.action.Parent_DataForwardAction;

import view.comune.LovComuneAction;

import view.util.Utility;

public class Round_spostaRichiamiAction extends Parent_DataForwardAction {
    // To handle an event named "yourname" add a method:
    // public void onYourname(DataActionContext ctx)
    private RichInputText codComune;
    private RichInputText descComune;

    public String onReimp() {
        Round_ParamSpostaRich bean =
            (Round_ParamSpostaRich) BindingContext.getCurrent().findDataControl("Round_ParamSpostaRichDataControl").getDataProvider();
        bean.reset();
        
        return "reimp";
    }


    public void onSposta(ActionEvent actionEvent) {
        Round_ParamSpostaRich bean =
            (Round_ParamSpostaRich) BindingContext.getCurrent().findDataControl("Round_ParamSpostaRichDataControl").getDataProvider();
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Integer ctPart = bean.getCtPart();
        Integer ctArr = bean.getCtArr();
        Date richDa = bean.getDtRichDa();
        Date richA = bean.getDtRichA();
        String codCom = bean.getCodCom();
        Map sess = ADFContext.getCurrent().getSessionScope();
        //SE IL COGNOME HA DENTRO UN APOSTROFO LA STRINGA VA ALTERATA
        String user = ((String) sess.get("user")).replaceAll("'", "''");
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");

        //Gaion 06/10/2015
        //lettura parametro di abilitazione per il journaling
        String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
        if (flagAbilJournal != null && flagAbilJournal.equals("1")) {
            am.preapareJournaling("SPOSTA_RICHIAMI", ulss, tpscr);
        }

        String istrUpd =
            "update SO_INVITO set " + "IDCENTRORICHIAMO = " + ctArr.toString() + "," + "DTULTIMAMOD = sysdate," +
            "OPMODIFICA = '" + user + "'" + " where IDCENTRORICHIAMO = " + ctPart.toString() +
            " and ATTIVO = 1 and TPSCR = '" + tpscr + "'";

        if (richDa != null) {
            istrUpd += " and DTRICHIAMO >= to_date('" + new SimpleDateFormat("dd/MM/yyyy").format(richDa) + "','dd/mm/yyyy')";
        }
        if (richA != null) {
            istrUpd += " and DTRICHIAMO < to_date('" + new SimpleDateFormat("dd/MM/yyyy").format(richA)+ "','dd/mm/yyyy') + 1";
        }
        if (codCom != null && !codCom.equals("")) {
            istrUpd +=
                " and '" + codCom + "' = " + "(SELECT decode(codanagreg,3,codcomdom,codcomres) FROM so_soggetto " +
                "where so_soggetto.codts = SO_INVITO.CODTS AND so_soggetto.ULSS = SO_INVITO.ULSS)";
        }

        /* 15052013 GAION : aggiunto anche lo spostamento di so_sogg_scr */
        String soggScrUpd =
            "update SO_SOGG_SCR set " + "IDCENTRORICHIAMO = " + ctArr.toString() + " where IDCENTRORICHIAMO = " +
            ctPart.toString() + " and TPSCR = '" + tpscr + "'";
        if (richDa != null) {
            soggScrUpd += " and DTRICHIAMO >= to_date('" + new SimpleDateFormat("dd/MM/yyyy").format(richDa) + "','dd/mm/yyyy')";
        }
        if (richA != null) {
            soggScrUpd += " and DTRICHIAMO < to_date('" + new SimpleDateFormat("dd/MM/yyyy").format(richA) + "','dd/mm/yyyy') + 1";
        }
        if (codCom != null && !codCom.equals("")) {
            soggScrUpd +=
                " and '" + codCom + "' = " + "(SELECT decode(codanagreg,3,codcomdom,codcomres) FROM so_soggetto " +
                "where so_soggetto.codts = SO_SOGG_SCR.CODTS AND so_soggetto.ULSS = SO_SOGG_SCR.ULSS)";
        }
        /* fine 15052013 */

        try {
            int n = am.getTransaction().executeCommand(istrUpd);
            int n2 = am.getTransaction().executeCommand(soggScrUpd);
            am.getTransaction().commit();
            String msg =
                "Operazione eseguita con successo. Aggiornati i richiami per " + n + " inviti e " + n2 + " soggetti.";
            this.handleMessages(FacesMessage.SEVERITY_INFO, msg);
        } catch (Exception ex) {
            this.handleException("Impossibile effettuare lo spostamento: " + ex.getMessage(), am.getTransaction());
        }

    }


    public void onChCentro(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        Round_ParamSpostaRich bean =
            (Round_ParamSpostaRich) BindingContext.getCurrent().findDataControl("Round_ParamSpostaRichDataControl").getDataProvider();
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Integer idCtPart = bean.getCtPart();
        Integer livCtPart = -1;
        //try {
            livCtPart = SoggUtils.getLivelloCt(am, idCtPart);
        //} catch (SQLException e) {
        //}
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        String whArr = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
        whArr += " and TIPOCENTRO = " + livCtPart;

        String elenco_centri = (String) session.get("elenco_centri");
        if (elenco_centri != null)
            whArr += " AND IDCENTRO IN " + elenco_centri;


        ViewObject voCtArr = am.findViewObject("Round_RichSelCprelView2");
        voCtArr.setWhereClause(whArr);
        voCtArr.executeQuery();

    }

    // To override a method of the lifecycle, go to
    // the main menu "Tools/Override Methods...".
    protected void setAppModule() {
        this.amName = "Round_AppModule";
    }

    public void lovComuneReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(codComune);
        RequestContext.getCurrentInstance().addPartialTarget(descComune);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', false)");
    }

    public void setCodComune(RichInputText codComune) {
        this.codComune = codComune;
    }

    public RichInputText getCodComune() {
        return codComune;
    }

    public void setDescComune(RichInputText descComune) {
        this.descComune = descComune;
    }

    public RichInputText getDescComune() {
        return descComune;
    }

    public void onReimpListener(ActionEvent actionEvent) {
        onReimp();
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc1', 'pt1:soc2'])");
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
    
    @SuppressWarnings({"unchecked", "deprecation"})
    public String onChComuneLOV() {
        Round_ParamSpostaRich am =
            (Round_ParamSpostaRich) BindingContext.getCurrent().findDataControl("Round_ParamSpostaRichDataControl").getDataProvider();
        String desCom = am.getDesCom();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        if (desCom == null || desCom.equals("")) {
            am.setCodCom(null);
            
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
                Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', false)");
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
                Utility.addScriptOnPartialRequest("setFocus('" + descComune.getClientId() + "', true)");
                return null;
            }
        }
        
        return "lovComune";
    }
}
