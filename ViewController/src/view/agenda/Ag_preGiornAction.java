package view.agenda;

import java.util.Arrays;
import java.util.Map;

import model.common.AccPf_AppModule;

import model.common.AccCo_AppModule;
import model.common.AccMa_AppModule;
import model.common.Acc_AppModule;
import model.common.Agenda_AppModule;
import model.common.Sogg_AppModule;

import model.commons.AgendaUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.event.RangeChangeEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

@SuppressWarnings({ "unchecked", "deprecation" })
public class Ag_preGiornAction<T extends Agenda> {
    T agendaObject;

    private RichForm frmagenda;
    private RichTable tabResult;
    private RichTable tabResultCo;

    public void setFrmagenda(RichForm frmagenda) {
        this.frmagenda = frmagenda;
    }

    public RichForm getFrmagenda() {
        if (frmagenda == null) {
            findForward();
        }

        return frmagenda;
    }

    public void findForward() {
        if (agendaObject != null)
            agendaObject.findForward();
        this.init();
    }

    private void init() {
        // TODO:  Override this oracle.adf.controller.struts.actions.DataAction method
        AgendaUtils.querySlot();
        Map session = ADFContext.getCurrent().getSessionScope();
        
        boolean oraDaInvito = false;
        if (session.get("oraDaInvito")!=null && (session.get("oraDaInvito") instanceof Boolean))
            oraDaInvito = ((Boolean) session.get("oraDaInvito")).booleanValue();
        
        Integer idCprel = null;

        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();

        ViewObject voDett = am.findViewObject("Agenda_DettAppView1");

        if (oraDaInvito) {
            session.put("oraDaInvito", Boolean.valueOf(false));
            String chAg = ((String) session.get("chAgenda")).substring(0, 4);

            Integer idApp = null;
            Integer idInvito = null;
            if (chAg.equals("sogg")) {
                Sogg_AppModule amSogg =
                    (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
                ViewObject voInvito = amSogg.findViewObject("Sogg_SoInvitoView1");
                Row cInv = voInvito.getCurrentRow();
                idApp = (Integer) cInv.getAttribute("Idapp");
                idInvito = (Integer) cInv.getAttribute("Idinvito");
            } else if (chAg.equals("acc_")) // accettazione cito
            {
                Acc_AppModule amAcc =
                    (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
                ViewObject voInvito = amAcc.findViewObject("Acc_SoInvitoView1");
                Row cInv = voInvito.getCurrentRow();
                idApp = (Integer) cInv.getAttribute("Idapp");
                idInvito = (Integer) cInv.getAttribute("Idinvito");
            } else if (chAg.equals("accC")) // accettazione colon
            {
                AccCo_AppModule amAcc =
                    (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
                ViewObject voInvito = amAcc.findViewObject("AccCo_SoInvitoView1");
                Row cInv = voInvito.getCurrentRow();
                idApp = (Integer) cInv.getAttribute("Idapp");
                idInvito = (Integer) cInv.getAttribute("Idinvito");
                voDett = am.findViewObject("Agenda_DettAppColonView1");
            } else if (chAg.equals("accM")) // acc. mammo
            {
                AccMa_AppModule amAcc =
                    (AccMa_AppModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
                ViewObject voInvito = amAcc.findViewObject("AccMa_SoInvitoView1");
                Row cInv = voInvito.getCurrentRow();
                idApp = (Integer) cInv.getAttribute("Idapp");
                idInvito = (Integer) cInv.getAttribute("Idinvito");
            } else // acc. pfas
            {
                AccPf_AppModule amAcc =
                    (AccPf_AppModule) BindingContext.getCurrent().findDataControl("AccPf_AppModuleDataControl").getDataProvider();
                ViewObject voInvito = amAcc.findViewObject("AccPf_SoInvitoView1");
                Row cInv = voInvito.getCurrentRow();
                idApp = (Integer) cInv.getAttribute("Idapp");
                idInvito = (Integer) cInv.getAttribute("Idinvito");
            }

            if (idApp != null) {

                ViewObject voSlot = am.findViewObject("Agenda_SoAppuntamentoView1");
                Row[] slots = voSlot.getFilteredRows("Idapp", idApp);
                if (slots.length > 0) {
                    voSlot.setCurrentRow(slots[0]);
                    int index = voSlot.getCurrentRowIndex();
                    int range = voSlot.getRangeSize();
                    if (range > 0)
                        voSlot.scrollRangeTo(slots[0], index % range);
                    else
                        voSlot.scrollRangeTo(slots[0], index);

                    idCprel = (Integer) slots[0].getAttribute("Idcentro");
                }

            }

            if (voDett != null && idInvito != null) {
                Row[] slots = voDett.getFilteredRows("Idinvito", idInvito);
                if (slots.length > 0) {
                    voDett.setCurrentRow(slots[0]);
                    int index = voDett.getCurrentRowIndex();
                    int range = voDett.getRangeSize();
                    if (range > 0)
                        voDett.scrollRangeTo(slots[0], index % range);
                    else
                        voDett.scrollRangeTo(slots[0], index);

                    int oldStart = voDett.getRangeStart();
                    int oldEnd = oldStart + range;
                    int newPage = index / range;
                    int newStart = newPage * range;
                    int newEnd = newStart + range;

                    if (chAg.equals("accC")) {
                        if (this.tabResultCo == null)
                            this.tabResultCo = new RichTable();

                        RowKeySet selected = new RowKeySetImpl();
                        selected.add(Arrays.asList(voDett.getCurrentRow().getKey()));
                        this.tabResultCo.setSelectedRowKeys(selected);
                        tabResultCo.broadcast(new RangeChangeEvent(tabResultCo, oldStart, oldEnd, newStart, newEnd));
                        AdfFacesContext.getCurrentInstance().addPartialTarget(tabResultCo);
                    } else {
                        if (this.tabResult == null)
                            this.tabResult = new RichTable();

                        RowKeySet selected = new RowKeySetImpl();
                        selected.add(Arrays.asList(voDett.getCurrentRow().getKey()));
                        this.tabResult.setSelectedRowKeys(selected);
                        tabResult.broadcast(new RangeChangeEvent(tabResult, oldStart, oldEnd, newStart, newEnd));
                        AdfFacesContext.getCurrentInstance().addPartialTarget(tabResult);
                    }
                }
            }
        }

        session.put("creatoNuovo", Boolean.FALSE);


        //filtro sui centri
        ViewObject voCt = am.findViewObject("Agenda_SelCprelView1");
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        /* voCt.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'");
            voCt.executeQuery();*/
        //filtro i centri in base a:
        //standard
        String ww = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
        //se l'utente e' legato ad un centro
        String in = (String) session.get("elenco_centri");

        if (in != null && idCprel == null) {
            ww += " AND IDCENTRO IN " + in;
        } //se sto navigando da un invito, devo preservare anche il centro dello stesso
        else if (in != null && idCprel != null) {
            ww += " AND (IDCENTRO IN " + in + " OR IDCENTRO=" + idCprel + ")";
        }

        voCt.setWhereClause(ww);
        voCt.executeQuery();

    }

    public void setAgendaObject(T agendaObject) {
        this.agendaObject = agendaObject;
    }

    public T getAgendaObject() {
        return agendaObject;
    }

    public void setTabResult(RichTable tabResult) {
        this.tabResult = tabResult;
    }

    public RichTable getTabResult() {
        return tabResult;
    }

    public void setTabResultCo(RichTable tabResultCo) {
        this.tabResultCo = tabResultCo;
    }

    public RichTable getTabResultCo() {
        return tabResultCo;
    }

}
