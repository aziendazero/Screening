package view.accettazione.mammo;

import java.util.Map;

import model.datacontrol.AccMa_RicParam;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

public class AccMa_iniAction extends Parent_DataForwardAction {

    private RichForm formSearch;

    public void setFormSearch(RichForm formSearch) {
        this.formSearch = formSearch;
    }

    public RichForm getFormSearch() {
        if (formSearch == null)
            findForward();
        
        return formSearch;
    }
    
    protected void findForward() {
        AccMa_RicParam bean =
            (AccMa_RicParam) BindingContext.getCurrent().findDataControl("AccMa_RicParamDataControl").getDataProvider();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        
        boolean nav = bean.getNavigazione().booleanValue();
        if (nav) {
            bean.queryInviti();
           // bean.setNavigazione(Boolean.FALSE);
        } else {
            bean.resetCampi();
            
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_RicInvitiView1Iterator");
           
            ViewObject vo = voIter.getViewObject();
            vo.setWhereClause("1=2");
            vo.executeQuery();
        }

        // 12122013 gaion: scelta esito
        session.put("chooseEsito", Boolean.FALSE);

        String livello = bean.getLivello();
        DCIteratorBinding voCtIter = bindings.findIteratorBinding("AccMa_SelCprelView1Iterator");
        ViewObject voCt = voCtIter.getViewObject();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");

        voCt.setWhereClauseParams(new Object[] { ulss, tpscr });
        String whct = "tipocentro = " + livello.toString();

        voCt.setWhereClause(whct);
        voCt.executeQuery();

    }

    @Override
    protected void setAppModule() {
        this.amName = "AccMa_AppModule";
    }
}
