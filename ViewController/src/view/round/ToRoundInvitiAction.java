package view.round;

import insiel.dataHandling.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import model.common.Round_AppModule;

import model.commons.AccessManager;

import model.commons.ViewHelper;

import model.datacontrol.Round_invitiBean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

public class ToRoundInvitiAction extends Parent_DataForwardAction {
    private Round_invitiAction round_invitiAction;
    private RichForm frmInvito;

    public void setRound_invitiAction(Round_invitiAction round_invitiAction) {
        this.round_invitiAction = round_invitiAction;
    }

    public Round_invitiAction getRound_invitiAction() {
        return round_invitiAction;
    }

    public ToRoundInvitiAction() {
    }

    public void setFrmInvito(RichForm frmInvito) {
        this.frmInvito = frmInvito;
    }

    public RichForm getFrmInvito() {
        if (frmInvito == null)
            findForward();

        return frmInvito;
    }

    protected void findForward() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoCentro1livView1");
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String elenco_centri = (String) ADFContext.getCurrent().getSessionScope().get("elenco_centri");
        ViewObjectFilters.filterCentri(vo, ulss, tpscr, DateUtils.getToday(), elenco_centri);

        Round_invitiBean invitiBean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
        if (invitiBean.getCentro() != null) {
            RowSetIterator _iter = ViewHelper.getRowSetIterator(vo, "centro_iter");
            Row[] _r = _iter.getFilteredRows("Idcentro", invitiBean.getCentro());

            if (_r == null || _r.length == 0) {
                invitiBean.setCentro(null);
                invitiBean.setLivello(null);
            }

            _iter.closeRowSetIterator();
        }
        // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE

        vo = am.findViewObject("Round_SoMediciByCentriView1");
        String where =
            "Round_SoMedico.ULSS='" + ulss + "' " + " AND (Round_SoMedico.DTFINEVALOP IS NULL OR  " +
            " Round_SoMedico.DTFINEVALOP>TO_DATE('" + DateUtils.getToday() + "','" + DateUtils.DATE_PATTERN + "'))";
        vo.setWhereClause(where);
        vo.executeQuery();
        //ViewObjectFilters.filterMedici(vo, ulss, DateUtils.getToday());

        // Imposto i valori di default nei campi di ricerca
        if (invitiBean.getData_riferimento_eta() == null) {

            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            if ("CA".equals(tpscr)) {
                Date date = null;
                try {
                    date = format.parse("31/12/2014");
                } catch (ParseException e) {
                }
                invitiBean.setData_riferimento_eta(date);
            } else if ("PF".equals(tpscr)) {
                Date date = null;
                try {
                    date = format.parse("31/12/2016");
                } catch (ParseException e) {
                }
                invitiBean.setData_riferimento_eta(date);
            } else {
                Date date = null;
                //try {
                date = invitiBean.getMax_data_richiamo();
                //} catch (ParseException e) {
                //}
                invitiBean.setData_riferimento_eta(date);
            }
        }
        //Impostazione degli anni di nascita.
        //se la selezione e' nulla non faccio nulla
        vo = am.findViewObject("Round_SoAziendaView1");
        Row[] result = vo.getFilteredRows("Codaz", ulss);
        try {
            if (result.length == 0)
                throw new Exception("Configurazione di screening non trovata per la ulss " + ulss);

            String nome1 = "Etacitomin";
            String nome2 = "Etacitomax";

            if ("CO".equals(tpscr)) {
                nome1 = "Etacolonmin";
                nome2 = "Etacolonmax";
            } else if ("MA".equals(tpscr)) {
                nome1 = "Etamammomin";
                nome2 = "Etamammomax";
            } else if ("CA".equals(tpscr)) {
                nome1 = "Etacardiomin";
                nome2 = "Etacardiomax";
            } else if ("PF".equals(tpscr)) {
                nome1 = "Etapfasmin";
                nome2 = "Etapfasmax";
            }

            if ("CI".equals(tpscr) || "CO".equals(tpscr) || "MA".equals(tpscr)) {
                if (invitiBean.getEta_da() == 0) {
                    invitiBean.setEta_da(((Integer) result[0].getAttribute(nome1)).intValue());
                }
                if (invitiBean.getEta_a() == 0) {
                    invitiBean.setEta_a(((Integer) result[0].getAttribute(nome2)).intValue());
                }
            } else {
                if (invitiBean.getData_nascita_da() == null) {
                    invitiBean.setData_nascita_da(((oracle.jbo.domain.Date) result[0].getAttribute(nome1)).getValue());
                }
                if (invitiBean.getData_nascita_a() == null) {
                    invitiBean.setData_nascita_a(((oracle.jbo.domain.Date) result[0].getAttribute(nome2)).getValue());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            handleException(ex.getMessage());
        }

        try {
            AccessManager.checkPermission("SOGenerazioneInviti");
        } catch (Exception ex) {
            ex.printStackTrace();
            handleException(ex.getMessage());
        }

        round_invitiAction.processUpdateModel();
    }

    @Override
    protected void setAppModule() {
    }
}
