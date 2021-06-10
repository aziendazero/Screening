package view.soggetto;

import java.util.Map;
import javax.faces.context.FacesContext;

import model.common.Sogg_AppModule;
import model.datacontrol.Sogg_RicParam;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.nav.RichButton;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.render.ExtendedRenderKitService;

public class IniSoggAction {
    private RichForm formSearch;
    private RichButton btnPianoLavoroEtichette;

    public IniSoggAction() {
        super();
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    protected void findForward() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        
        String tpscr = (String) session.get("scr");

        Map req = adfCtx.getPageFlowScope();
        boolean init = true;
        System.out.print("---------------> init: " + req.get("initSearch"));
        try{
            init = req.get("initSearch") != null ? (Boolean)req.get("initSearch") : true;
        }catch(Throwable th){
            init = true;
        }
        
        req.put("initSearch", init);
        
        //I00101237 resetto le classi di popolazione
        if ("CO".equals(tpscr)){
            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            
            ViewObject classePopView = am.findViewObject("Sogg_SoCnfClassePopView1");
            classePopView.setWhereClause("tpscr ='" + tpscr + "'");
            classePopView.executeQuery();  
        }
        
        if(init){
            Sogg_RicParam bean =
                (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
    
            boolean nav = bean.getNavigazione().booleanValue();
            if (nav) {
                bean.setEtaScreening();
                bean.querySogg();
                //bean.setNavigazione(Boolean.valueOf(false));
                session.put("inEta", Boolean.valueOf(false));
            } else {
                bean.resetCampi();
    
                ViewObject vo = am.findViewObject("Sogg_RicercaView1");
                vo.setWhereClause("1=2");
                //TODO: patch per la perdita delle bind variables VERIFICARE!!!!
                String ulss = (String) session.get("ulss");
                vo.setWhereClauseParams(new Object[] { ulss, tpscr, ulss, tpscr, tpscr });
                vo.executeQuery();
            }
        } else
            Sogg_ricercaAction.checkSelected();
        
        //I00102504 - Inviti Fast
        String ulss  = (String) session.get("ulss");
        String user  = (String) session.get("user");
        
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ViewObject vo = am.findViewObject("Cnf_SoCnfInvitiFastView1");
        
        if (vo!=null){            
            String where = "ULSS = '" + ulss + "' AND TPSCR = '" + tpscr + "' AND OPINS = '" + user + "' AND UTILIZZA = 1";
        
            vo.setWhereClause(where);
            vo.executeQuery();
            
            long rowCoun = vo.getRowCount();
            
            /* Render del bottone appropriato:
             * Se ha trovato una configurazione unica, la creazione dell'invito fast sarà diretta
             * altrimenti si dovrà passare dal pop-up di conferme(rowCoun=0)/selezione(rowCoun>1) */
            if (rowCoun==1)
                session.put("invitoFastDirect", Boolean.valueOf(true));
            else 
                session.put("invitoFastDirect", Boolean.valueOf(false));
        }
        
        /* Se si ritorna sulla form di ricerca dopo acer creato un invito fast con stampaEtichetta = true
         * apro la stampa cliccando via JS sul bottone "Stampa piani lavoro/Etichette" */
        if (session.get("stampaEtichetta")!=null && ((Boolean) session.get("stampaEtichetta")).booleanValue())
            executeClientJavascript("callBtnClick('pt1:b10')");
        }

    public static void executeClientJavascript(String script) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExtendedRenderKitService service = org.apache.myfaces.trinidad.util.Service.getRenderKitService(facesContext, ExtendedRenderKitService.class);
        service.addScript(facesContext, script);
    }
    
    public void setFormSearch(RichForm formSearch) {
        this.formSearch = formSearch;
    }

    public RichForm getFormSearch() {
        if(formSearch==null)
            findForward();
        
        return formSearch;
    }

    public void setBtnPianoLavoroEtichette(RichButton btnPianoLavoroEtichette) {
        this.btnPianoLavoroEtichette = btnPianoLavoroEtichette;
    }

    public RichButton getBtnPianoLavoroEtichette() {
        if (btnPianoLavoroEtichette==null)
            btnPianoLavoroEtichette =  new RichButton();
            
        return btnPianoLavoroEtichette;
    }
}
