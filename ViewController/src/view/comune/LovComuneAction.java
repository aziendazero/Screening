package view.comune;

import java.util.Map;


import model.common.Sogg_AppModule;

import model.commons.SoggUtils;

import model.conf.Cnf_SoComuneViewImpl;

import model.datacontrol.Round_ParamSpostaRich;
import model.datacontrol.Sogg_RicParam;

import model.medico.Med_ComuneViewImpl;

import model.round.Round_LovComuneViewImpl;

import model.soggetto.Sogg_ComuneViewImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

import model.common.Med_AppModule;

import model.datacontrol.Med_RicParam;

import model.soggetto.common.Sogg_SoSoggettoViewRow;

import oracle.adf.view.rich.context.AdfFacesContext;

public class LovComuneAction {
    public LovComuneAction() {
        super();
    }

    public static String checkOnLovFilter() {
        LovComuneAction l = new LovComuneAction();
        return l.onLovFilter();
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    public String onLovFilter() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();
        Map page = adfCtx.getPageFlowScope();
        
        String st = req.get("searchText") != null ? req.get("searchText").toString() : "";

        DCIteratorBinding iteratorBinding;

        if (page.containsKey("itName"))
            iteratorBinding =
                (DCIteratorBinding) BindingContext.getCurrent().getCurrentBindingsEntry().get(page.get("itName").toString());
        else{
            iteratorBinding = (DCIteratorBinding) page.get("iteratorBinding");
            page.put("itName", "ComuneIterator");
            page.put("voName", iteratorBinding.getSourceName());
            page.put("dcName", iteratorBinding.getDataControl().getName());
        }

        String whcl = "DESCRIZIONE like '" + st.replace("'", "''") + "%'";
        ViewObject voCom = iteratorBinding.getViewObject(); //am.findViewObject("Sogg_ComuneView1");

        if (voCom instanceof Sogg_ComuneViewImpl)
            soggettoLovCom(voCom, whcl);
        else if (voCom instanceof Round_LovComuneViewImpl)
            roundLovCom(voCom, whcl);
        else if (voCom instanceof Cnf_SoComuneViewImpl)
            cnfZoneLovCom(voCom, whcl);
        else if (voCom instanceof Med_ComuneViewImpl)
            mediciLovCom(voCom, whcl);

        voCom.executeQuery();

        if (voCom.getEstimatedRowCount() > 0)
            voCom.setCurrentRow(voCom.first());
        
        boolean onOneReturn = false;
        
        try{
            onOneReturn = req.get("onOneReturn") != null ? (Boolean)req.get("onOneReturn") : false;
        }catch(Throwable th){
            onOneReturn = false;
        }
        
        if(onOneReturn && voCom.getEstimatedRowCount()==1){
            onLovSelect();
        } else 
            req.put("onOneReturn", false);

        return "searchcompleted";
    }
    
    private void roundLovCom(ViewObject voCom, String whcl) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        
        String ulss = (String) sess.get("ulss");
        whcl += " and ULSS = '" + ulss + "'";
        
        voCom.setWhereClause(whcl);
    }
    
    private void mediciLovCom(ViewObject voCom, String whcl) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        Map page = adfCtx.getPageFlowScope();
        
        String src = page.get("source").toString();
        sess.put("lovSource", src);

        String ulss = (String) sess.get("ulss");
        whcl += " and ULSS = '" + ulss + "'";

        if (src.equals("VB_Descom")) {
            whcl += " and (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA > SYSDATE) ";
            Med_AppModule am = (Med_AppModule) voCom.getApplicationModule();
            ViewObject voMed = am.findViewObject("Med_SoMedicoView1");
            Row cMed = voMed.getCurrentRow();
            String codPr = (String) cMed.getAttribute("Codpr");
            if (codPr != null && !codPr.equals("")) {
                whcl += " and CODPR = '" + codPr + "'";
                ;
            }

        }

        voCom.setWhereClause(whcl);
    }

    private void soggettoLovCom(ViewObject voCom, String whcl) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        Map page = adfCtx.getPageFlowScope();
        
        String src = page.get("source").toString();
        sess.put("lovSource", src);

        voCom.setWhereClause(null);
        voCom.setWhereClauseParams(null);

        Sogg_AppModule am = (Sogg_AppModule) voCom.getApplicationModule();

        ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();

        // Comune residenza (pagina ricerca anagrafica)
        if (src.equals("VB_desComRes")) {
            String ulss = (String) sess.get("ulss");
            whcl += " and ULSS = '" + ulss + "'";
            whcl +=
                " AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE)";
        }

        // Comune nascita
        else if (src.equals("VB_Descrizione")) {
            Integer provNasc = soggettoRow.getReleaseCodePr();
            if (provNasc != null) {
                whcl += " AND release_code_pr = " + provNasc.toString();
            }

            // Il comune di nascita deve essere coerente con la cittadinanza e la data di nascita
            int cittadinanza = soggettoRow.getReleaseCodeCit() != null ? soggettoRow.getReleaseCodeCit().intValue() : 0;

            // Se cittadinanza sconosciuta o italiana
            if (cittadinanza == 0 || cittadinanza == 1) {
                Date dataNascita = soggettoRow.getDataNascita();
                whcl +=
                    " AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= :1) AND (dtfinevalidita IS NULL OR dtfinevalidita > :2)";
                voCom.setWhereClauseParam(0, dataNascita);
                voCom.setWhereClauseParam(1, dataNascita);
            } else {
                whcl +=
                    " AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE)";
            }

            // TODO: Aggiungere il comune attuale, se non gia' presente nella select
        }

        // Comune residenza
        else if (src.equals("VB_Descomres")) {
            Integer provRes = soggettoRow.getReleaseCodePrRes();
            whcl +=
                " AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE) AND release_code_pr = " +
                provRes;
        }

        // Comune domicilio
        else if (src.equals("VB_Descomdom")) {
            Integer provDom = soggettoRow.getReleaseCodePrDom();
            whcl +=
                " AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE) AND release_code_pr = " +
                provDom;
        }

        // Comune screening
        else if (src.equals("VB_Descomscr")) {
            Integer provScr = soggettoRow.getReleaseCodePrScr();
            whcl +=
                " AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE) AND release_code_pr = " +
                provScr;
        }
        voCom.setWhereClause(whcl);
    }
    
    private void roundSelectLovCom(ViewObject voCom, Row cCom) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        
        BindingContext ctx = BindingContext.getCurrent();
        
        String desCom = (String) cCom.getAttribute("Descrizione");
        String codCom = (String) cCom.getAttribute("Codcom");

        Round_ParamSpostaRich bean = (Round_ParamSpostaRich) ctx.findDataControl("Round_ParamSpostaRichDataControl").getDataProvider();
        
        bean.setCodCom(codCom);
        bean.setDesCom(desCom);
    }
    
    private void mediciSelectLovCom(ViewObject voCom, Row cCom) {
        ADFContext adfCtx = ADFContext.getCurrent();
        AdfFacesContext afContext = AdfFacesContext.getCurrentInstance();
        Map sess = adfCtx.getSessionScope();

        String desCom = (String) cCom.getAttribute("Descrizione");
        String codCom = (String) cCom.getAttribute("Codcom");
        String codPr = (String) cCom.getAttribute("Codpr");
        Integer rcComune = (Integer) cCom.getAttribute("ReleaseCode");
        String lovSource = (String) sess.get("lovSource");

        if (lovSource.equals("VB_Descom")) {
            Med_AppModule am = (Med_AppModule) voCom.getApplicationModule();
            ViewObject voMed = am.findViewObject("Med_SoMedicoView1");
            Row cMed = voMed.getCurrentRow();
            cMed.setAttribute("Codcom", codCom);
            cMed.setAttribute("Descom", desCom);
            cMed.setAttribute("Codpr", codPr);
            cMed.setAttribute("ReleaseCodeCom", rcComune);
        } else if (lovSource.equals("VB_desCom")) {
            Med_RicParam bean =
                (Med_RicParam) BindingContext.getCurrent().findDataControl("Med_RicParamDataControl").getDataProvider();
            bean.setCodCom(codCom);
            bean.setDesCom(desCom);
            bean.setRcCom(rcComune.intValue());

        } else if (lovSource.equals("VB_desComu")) { //stampe per i medici
            Med_RicParam bean =
                (Med_RicParam) BindingContext.getCurrent().findDataControl("Med_RicParamDataControl").getDataProvider();
            bean.setCodComu(codCom);
            bean.setDesComu(desCom);
        }
    }

    private void soggettoSelectLovCom(ViewObject voCom, Row cCom) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        
        String src = (String) sess.get("lovSource");

        Sogg_AppModule am = (Sogg_AppModule) voCom.getApplicationModule();

        String desCom = (String) cCom.getAttribute("Descrizione");
        String codCom = (String) cCom.getAttribute("Codcom");
        Integer releaseCode = (Integer) cCom.getAttribute("ReleaseCode");

        BindingContext ctx = BindingContext.getCurrent();


        // Comune di residenza (pagina ricerca anagrafica)
        if (src.equals("VB_desComRes")) {
            Sogg_RicParam bean = (Sogg_RicParam) ctx.findDataControl("Sogg_RicParamDataControl").getDataProvider();
            bean.setDesComRes(desCom);
            bean.setIdComRes(codCom);
            bean.setCodComRes(Integer.valueOf(releaseCode.toString()));
            sess.put("codCom", codCom);
            SoggUtils.filtraDistretti(codCom, "Sogg_DistrettiView1");
        }

        // Comune di nascita
        else if (src.equals("VB_Descrizione")) {
            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
            soggettoRow.setCodcomnascita(codCom);
            soggettoRow.setDescrizione(desCom);
            soggettoRow.setReleaseCodeComNas(releaseCode);
        }

        // Comune residenza
        else if (src.equals("VB_Descomres")) {
            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
            soggettoRow.setCodcomres(codCom);
            soggettoRow.setDescomres(desCom);
            soggettoRow.setReleaseCodeComRes(releaseCode);
            //20120111 Serra: disabilito filtro su zona
            // SoggUtils.filtraDistretti(ctx, null, "Sogg_DistrettiView2");
            //20110111 fine
        }

        // Comune domicilio
        else if (src.equals("VB_Descomdom")) {
            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
            soggettoRow.setCodcomdom(codCom);
            soggettoRow.setDescomdom(desCom);
            soggettoRow.setReleaseCodeComDom(releaseCode);
        }

        // Comune screening
        else if (src.equals("VB_Descomscr")) {
            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            Sogg_SoSoggettoViewRow soggettoRow = (Sogg_SoSoggettoViewRow) voAnag.getCurrentRow();
            soggettoRow.setCodcomscr(codCom);
            soggettoRow.setDescomscr(desCom);
            soggettoRow.setReleaseCodeComScr(releaseCode);
        }
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    public String onLovSelect() {
        ADFContext adfCtx = ADFContext.getCurrent();

        //Sogg_AppModule am = (Sogg_AppModule) ctx.findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        Map page = adfCtx.getPageFlowScope();
        DCIteratorBinding iteratorBinding =
                (DCIteratorBinding) BindingContext.getCurrent().getCurrentBindingsEntry().get(page.get("itName").toString());

        ViewObject voCom = iteratorBinding.getViewObject(); //am.findViewObject("Sogg_ComuneView1");

        Row cCom = voCom.getCurrentRow();

        if (voCom instanceof Sogg_ComuneViewImpl)
            soggettoSelectLovCom(voCom, cCom);
        else if (voCom instanceof Round_LovComuneViewImpl)
            roundSelectLovCom(voCom, cCom);
        else if (voCom instanceof Cnf_SoComuneViewImpl)
            cnfZoneSelectLovCom(voCom, cCom);
        else if (voCom instanceof Med_ComuneViewImpl)
            mediciSelectLovCom(voCom, cCom);

        return "selected";
    }

    private void cnfZoneSelectLovCom(ViewObject voCom, Row cCom) {

        Row comune = voCom.getCurrentRow();
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        ViewObject comzone = bindings.findIteratorBinding("Cnf_SoComDistrettiZoneView3Iterator").getViewObject();

        try {

            if (comune == null)
                throw new Exception("Nessun comune selezionato");

            Row r = comzone.createRow();
            comzone.insertRow(r);
            r.setAttribute("Codcom", comune.getAttribute("Codcom"));
            r.setAttribute("ReleaseCodeCom", comune.getAttribute("ReleaseCode"));

            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            Object commitResult = commitBinding.execute();
            
            comzone.executeQuery();
            
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    private void cnfZoneLovCom(ViewObject voCom, String whcl) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        
        String ulss = (String) sess.get("ulss");
        whcl += " and ULSS = '" + ulss + "'";
        
        voCom.setWhereClause(whcl);
    }
}
