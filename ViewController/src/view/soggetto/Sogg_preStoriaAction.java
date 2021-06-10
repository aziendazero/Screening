package view.soggetto;

import java.util.List;
import java.util.Map;

import model.common.Sogg_AppModule;

import model.commons.AccessManager;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class Sogg_preStoriaAction {
    private RichForm frmstory;

    public Sogg_preStoriaAction() {
    }

    public void setFrmstory(RichForm frmstory) {
        this.frmstory = frmstory;
    }

    public RichForm getFrmstory() {
        if (frmstory == null)
            findForward();

        return frmstory;
    }

    public void findForward() {
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();

        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_RicercaView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row cAnag = vo.getCurrentRow();
        String codts = (String) cAnag.getAttribute("Codts");

        Object[] pars = { ulss, tpscr, codts };

        Sogg_AppModule am = (Sogg_AppModule) vo.getApplicationModule();
        // storico inviti
        vo = am.findViewObject("Sogg_StInvitiView1");
        vo.setWhereClauseParams(pars);
        vo.executeQuery();
        boolean hasInv = vo.getEstimatedRowCount() > 0;

        // storico esclusioni
        vo = am.findViewObject("Sogg_StEsclView1");
        vo.setWhereClauseParams(pars);
        vo.executeQuery();

        // inviti cancellati
        vo = am.findViewObject("Sogg_StInvitiBckView1");
        vo.setWhereClauseParams(null);
        vo.setWhereClauseParams(pars);
        /*
        vo.setWhereClauseParam(0, ulss);
        vo.setWhereClauseParam(1, tpscr);
        vo.setWhereClauseParam(2, codts);
        */
        vo.executeQuery();
        //long nInvBck = vo.getEstimatedRowCount();
        session.put("forceActive", !hasInv /*&& nInvBck == 1*/);
        
        try{
            // devo filtrare i referti per centri
            AccessManager.checkPermission("SOLimiteCentri");
            List<Integer> centriAutorizzati = (List<Integer>) session.get("centriautorizzati");
            String centri = "";
            if (centriAutorizzati != null && !centriAutorizzati.isEmpty()){
                for (int i = 0; i < centriAutorizzati.size(); i++) {
                    centri += "" + centriAutorizzati.get(i) + ",";
                }
                centri = centri.substring(0, centri.length() - 1);
            } else {
                centri = "-1";
            }
            
            vo = am.findViewObject("Sogg_StRef1View1");
            vo.setWhereClauseParams(pars);
            vo.setWhereClause("IDCENTROPRELIEVO IN ("+centri+") ");
            vo.executeQuery();

            vo = am.findViewObject("Sogg_StRef2View1");
            vo.setWhereClauseParams(pars);
            vo.setWhereClause("IDCENTROPRELIEVO IN ("+centri+") ");
            vo.executeQuery();

            vo = am.findViewObject("Sogg_StCoRef1View1");
            vo.setWhereClauseParams(pars);
            vo.setWhereClause("IDCENTROPRELIEVO IN ("+centri+") ");
            vo.executeQuery();

            vo = am.findViewObject("Sogg_StPfRef1View1");
            vo.setWhereClauseParams(pars);
            vo.setWhereClause("IDCENTROPRELIEVO IN ("+centri+") ");
            vo.executeQuery();

            vo = am.findViewObject("Sogg_SoRefertocolon2livStoricoView1");
            vo.setWhereClauseParams(pars);
            vo.setWhereClause("IDCENTROPRELIEVO IN ("+centri+") ");
            vo.executeQuery();

            vo = am.findViewObject("Sogg_SoRefertomammo1livStoricoView1");
            vo.setWhereClauseParams(pars);
            vo.setWhereClause("IDCENTROPRELIEVO IN ("+centri+") ");
            vo.executeQuery();

            vo = am.findViewObject("Sogg_SoRefertomammo2livStoricoView1");
            vo.setWhereClauseParams(pars);
            vo.setWhereClause("IDCENTROPRELIEVO IN ("+centri+") ");
            vo.executeQuery();

            vo = am.findViewObject("Sogg_StCaRef1View1");
            vo.setWhereClauseParams(pars);
            vo.setWhereClause("IDCENTROPRELIEVO IN ("+centri+") ");
            vo.executeQuery();
            
        } catch (IllegalAccessException e){                              
            //non deve fare niente
            vo = am.findViewObject("Sogg_StRef1View1");
            vo.setWhereClauseParams(pars);
            vo.executeQuery();

            vo = am.findViewObject("Sogg_StRef2View1");
            vo.setWhereClauseParams(pars);
            vo.executeQuery();

            vo = am.findViewObject("Sogg_StCoRef1View1");
            vo.setWhereClauseParams(pars);
            vo.executeQuery();

            vo = am.findViewObject("Sogg_StPfRef1View1");
            vo.setWhereClauseParams(pars);
            vo.executeQuery();

            vo = am.findViewObject("Sogg_SoRefertocolon2livStoricoView1");
            vo.setWhereClauseParams(pars);
            vo.executeQuery();

            vo = am.findViewObject("Sogg_SoRefertomammo1livStoricoView1");
            vo.setWhereClauseParams(pars);
            vo.executeQuery();

            vo = am.findViewObject("Sogg_SoRefertomammo2livStoricoView1");
            vo.setWhereClauseParams(pars);
            vo.executeQuery();

            vo = am.findViewObject("Sogg_StCaRef1View1");
            vo.setWhereClauseParams(pars);
            vo.executeQuery();
        }
        
        vo = am.findViewObject("Sogg_StConsensiView1");
        vo.setWhereClauseParams(pars);
        vo.executeQuery();

        Object[] pars2 = { codts, ulss, codts, ulss };
        vo = am.findViewObject("Sogg_StDocumentiView1");
        vo.setWhereClauseParams(pars2);
        vo.executeQuery();
    }

}
