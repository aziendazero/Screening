package view.soggetto;

import java.util.Map;

import model.common.Sogg_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class Sogg_preanagAction {
    private RichForm frmanag;
    
    private Sogg_anagAction sogg_anagAction;

    public Sogg_preanagAction() {
    }

    public void setFrmanag(RichForm frmanag) {
        this.frmanag = frmanag;
    }

    public RichForm getFrmanag() {
        if (frmanag == null)
            findForward();

        return frmanag;
    }

    public void findForward() {

        Map session = ADFContext.getCurrent().getSessionScope();
        @SuppressWarnings("deprecation")
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Sogg_RicercaView1");
        Row cAnag = vo.getCurrentRow();

        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        //filtro sui centri dell'utente
        Integer c1 = (Integer) session.get("centro1liv");
        Integer c2 = (Integer) session.get("centro2liv");

        //filtro i centri di 1 livello che possono essere associati al soggetto
        //in base a ulss, tpscr e, se l'utente e' vincolto ad un centro, a quel centro
        //ed eventualmente quello gia' associato all'anagrafica

        String wh = "TPSCR='" + tpscr + "' and ulss='" + ulss + "' ";

        vo = am.findViewObject("Sogg_SoCnfCentro1livView1");
        //se l'utente e' vincolato a qualche centro...
        if (c1 != null) {

            String in = "(";
            //lavoro sul centro di primo livello
            if (c1 != null)
                in += c1;
            if (cAnag != null) {
                Integer centro = (Integer) cAnag.getAttribute("Idcentro1liv");

                if (c1 != null && centro != null)
                    in += ",";
                if (centro != null)
                    in += centro;
            }
            in += ")";
            wh += " and idcentro in " + in;
        }
        vo.setWhereClause(wh);
        vo.executeQuery();


        vo = am.findViewObject("Sogg_SoCnfCentro2livView1");
        wh = "TPSCR='" + tpscr + "' and ulss='" + ulss + "' ";
        if (c2 != null) {
            String in = "(";
            //lavoro sul centro di secodno livello
            if (c2 != null)
                in += c2;
            if (cAnag != null) {
                Integer centro = (Integer) cAnag.getAttribute("Idcentro2liv");

                if (c2 != null && centro != null)
                    in += ",";
                if (centro != null)
                    in += centro;
            }

            in += ")";
            wh += " and idcentro in " + in;

        }
        vo.setWhereClause(wh);
        vo.executeQuery();

        vo = am.findViewObject("Cnf_SoCnfAnagRegView1");
        vo.setWhereClause("SIGLA NOT IN ('ME','NP') ");
        vo.executeQuery();
        
        sogg_anagAction.findForward();
    }

    public void setSogg_anagAction(Sogg_anagAction sogg_anagAction) {
        this.sogg_anagAction = sogg_anagAction;
    }

    public Sogg_anagAction getSogg_anagAction() {
        return sogg_anagAction;
    }
}
