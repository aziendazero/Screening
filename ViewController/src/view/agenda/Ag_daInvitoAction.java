package view.agenda;

import insiel.dataHandling.DateUtils;

import java.util.Map;

import model.common.AccPf_AppModule;
import model.common.Acc_AppModule;

import model.common.AccCo_AppModule;
import model.common.AccMa_AppModule;
import model.common.Agenda_AppModule;
import model.common.Sogg_AppModule;

import model.commons.AgendaUtils;

import model.datacontrol.Agenda_Servizio;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

@SuppressWarnings({ "unchecked", "deprecation" })
public class Ag_daInvitoAction extends Agenda {
    @Override
    public void findForward() {
        // TODO:  Override this oracle.adf.controller.struts.actions.DataAction method
        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        //  ViewObject voCt = am.findViewObject("Agenda_SelCprelView1");
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        /* spostato piu' avanti
            *
            * voCt.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'");
            voCt.executeQuery();*/


        Date dtApp;
        Integer idCprel = null;
        String nome = null;
        String cogn = null;

        String chAg = ((String) session.get("chAgenda")).substring(0, 4);

        if (chAg.equals("sogg")) {
            Sogg_AppModule amSogg =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amSogg.findViewObject("Sogg_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();
            idCprel = (Integer) cInv.getAttribute("Idcentroprelievo");
            dtApp = (Date) cInv.getAttribute("Dtapp");
            ViewObject voAnag = amSogg.findViewObject("Sogg_SoSoggettoView1");
            Row cAnag = voAnag.getCurrentRow();
            nome = (String) cAnag.getAttribute("Nome");
            cogn = (String) cAnag.getAttribute("Cognome");
        } else if (chAg.equals("acc_")) // provengo da accettazione cito
        {
            Acc_AppModule amAcc =
                (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("Acc_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();
            idCprel = (Integer) cInv.getAttribute("Idcentroprelievo");
            dtApp = (Date) cInv.getAttribute("Dtapp");
            ViewObject voRic = amAcc.findViewObject("Acc_RicInvitiView1");
            Row cInvv = voRic.getCurrentRow();
            nome = (String) cInvv.getAttribute("Nome");
            cogn = (String) cInvv.getAttribute("Cognome");

        } else if (chAg.equals("accC")) // provengo da accettazione colon
        {
            AccCo_AppModule amAcc =
                (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("AccCo_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();
            idCprel = (Integer) cInv.getAttribute("Idcentroprelievo");
            dtApp = (Date) cInv.getAttribute("Dtapp");
            ViewObject voRic = amAcc.findViewObject("AccCo_RicInvitiView1");
            Row cInvv = voRic.getCurrentRow();
            nome = (String) cInvv.getAttribute("Nome");
            cogn = (String) cInvv.getAttribute("Cognome");

        } else if (chAg.equals("accM")) // provengo da acc. mammo
        {
            AccMa_AppModule amAcc =
                (AccMa_AppModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("AccMa_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();
            idCprel = (Integer) cInv.getAttribute("Idcentroprelievo");
            dtApp = (Date) cInv.getAttribute("Dtapp");
            ViewObject voRic = amAcc.findViewObject("AccMa_RicInvitiView1");
            Row cInvv = voRic.getCurrentRow();
            nome = (String) cInvv.getAttribute("Nome");
            cogn = (String) cInvv.getAttribute("Cognome");

        } else // provengo da acc. pfas
        {
            AccPf_AppModule amAcc =
                (AccPf_AppModule) BindingContext.getCurrent().findDataControl("AccPf_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("AccPf_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();
            idCprel = (Integer) cInv.getAttribute("Idcentroprelievo");
            dtApp = (Date) cInv.getAttribute("Dtapp");
            ViewObject voRic = amAcc.findViewObject("AccPf_RicInvitiView1");
            Row cInvv = voRic.getCurrentRow();
            nome = (String) cInvv.getAttribute("Nome");
            cogn = (String) cInvv.getAttribute("Cognome");
        }

        //filtro i centri in base a:
        //standard
        /*  String ww="TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
            //se l'utente e' legato ad un centro
            String in=(String)session.get("elenco_centri");

            if(in!=null &&  idCprel==null)
            {
              ww+=" AND IDCENTRO IN "+in;
            } //se sto navigando da un invito, devo preservare anche il centro dello stesso
            else if (in!=null && idCprel!=null)
            {
              ww+=" AND (IDCENTRO IN "+in+" OR IDCENTRO="+idCprel+")";
            }

            voCt.setWhereClause(ww);
            voCt.executeQuery();*/

        Date dataApp;
        String dtMess;
        if (dtApp != null) {
            dataApp = dtApp;
            dtMess = DateUtils.dateToString(dataApp.dateValue());
        } else {
            dataApp = DateUtils.getOracleDateNow();
            dtMess = "non specificata";
        }

        session.put("dtAgenda", AgendaUtils.getDateWithoutTime(dataApp.dateValue()));

        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();

        if (idCprel == null) {
            bean.setIdCentro(null);
        } else {
            bean.setIdCentro(new Integer(idCprel.intValue()));
        }


        session.put("pazSel",
                    "L'appuntamento relativo al soggetto " + cogn + " " + nome + " in data " + dtMess +
                    " e' stato selezionato per lo spostamento.");
        session.put("appSel", Boolean.valueOf(true));
        session.put("agDaMenu", Boolean.valueOf(false));
        session.put("oraDaInvito", Boolean.valueOf(true));
    }
}
