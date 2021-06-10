package view.round;

import java.sql.SQLException;

import java.util.Map;

import model.common.Round_AppModule;

import model.commons.SoggUtils;

import model.datacontrol.Round_ParamSpostaRich;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

public class ToRoundSpostaAction extends Parent_DataForwardAction {
    private RichForm frmsposta;

    public ToRoundSpostaAction() {
    }

    public void setFrmsposta(RichForm frmsposta) {
        this.frmsposta = frmsposta;
    }

    public RichForm getFrmsposta() {
        if (frmsposta == null)
            findForward();
        
        return frmsposta;
    }
    
    protected void findForward() {
        Round_ParamSpostaRich bean = (Round_ParamSpostaRich) BindingContext.getCurrent().
          findDataControl("Round_ParamSpostaRichDataControl").getDataProvider();
        bean.reset();
        
        Round_AppModule am = (Round_AppModule) BindingContext.getCurrent().
          findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject voCtPart = am.findViewObject("Round_RichSelCprelView1");
        ViewObject voCtArr = am.findViewObject("Round_RichSelCprelView2");
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        String elenco_centri=(String)session.get("elenco_centri");
        String whPart = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
         if(elenco_centri != null)
          whPart+=" AND IDCENTRO IN "+elenco_centri;
        voCtPart.setWhereClause(whPart);
        voCtPart.executeQuery();
        
        String whArr = whPart;
        
        Row fCtPart = voCtPart.first();
        Integer ctPart = (Integer) fCtPart.getAttribute("Idcentro");
        if (fCtPart != null)
        {
          int livello;
           // try {
                livello = SoggUtils.getLivelloCt(am, ctPart);
                whArr += " and tipocentro = " + livello;
           // } catch (SQLException e) {
            //} 
        }
        
        voCtArr.setWhereClause(whArr);
        voCtArr.executeQuery();
    }

    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }
}
