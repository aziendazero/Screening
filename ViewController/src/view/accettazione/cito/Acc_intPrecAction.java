package view.accettazione.cito;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.accettazione.Acc_SoAccCito1livViewRowImpl;
import model.accettazione.Acc_SoAccCito2livViewRowImpl;
import model.accettazione.common.Acc_RicInvitiViewRow;
import model.accettazione.common.Acc_SoInvitoViewRow;

import model.common.Acc_AppModule;
import model.common.Acc_IntPrec2ElencoFullRow;
import model.common.Acc_IntervPrecElencoFullRow;
import model.common.Acc_SoAnamnesiCitoViewRow;
import model.common.Acc_SoInterventoPrec2livViewRow;
import model.common.Acc_SoInterventoPrecViewRow;

import model.commons.ViewHelper;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.commons.AccUtils;
import view.commons.action.Parent_DataForwardAction;

public class Acc_intPrecAction extends Parent_DataForwardAction {
    private RichTable tabInterventi;

    @Override
    protected void setAppModule() {
        this.amName = "Acc_AppModule";
    }

    public Acc_intPrecAction() {
        super();
    }

    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        if (dest.equals("acc_to")) {
            session.put("fromDett", Boolean.TRUE);
        } else {
            AccUtils.beforeNavAcc(dest, true);
        }
    } 

    protected boolean beforeSave() {
        boolean esitoSuper = super.beforeSave();
        if (!esitoSuper)
            return false;

        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        boolean escl = ((Boolean) sess.get("soggEscluso")).booleanValue();
        if (escl) {
            String msg = "La persona risulta esclusa. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject voRic = voInvIter.getViewObject();

        Acc_AppModule am = (Acc_AppModule) voRic.getApplicationModule();

        ViewObject voAnam = am.findViewObject("Acc_SoAnamnesiCitoView1");
        Acc_SoAnamnesiCitoViewRow cAnam = (Acc_SoAnamnesiCitoViewRow) voAnam.getCurrentRow();

        if (cAnam != null) {
            Acc_RicInvitiViewRow cInvRic = (Acc_RicInvitiViewRow) voRic.getCurrentRow();
            Integer idInv = cInvRic.getIdinvito();
            ViewObject voInvito = am.findViewObject("Acc_SoInvitoView1");
            voInvito.setWhereClauseParams(new Object[] { idInv });
            voInvito.executeQuery();

            Acc_SoInvitoViewRow fInv = (Acc_SoInvitoViewRow) voInvito.first();
            voInvito.setCurrentRow(fInv);
            Date dataApp = fInv.getDtapp();

            cAnam.setDtanamnesi(dataApp);
        }

        ViewObject voInvito = am.findViewObject("Acc_SoInvitoView1");
        Acc_SoInvitoViewRow cInv = (Acc_SoInvitoViewRow) voInvito.getCurrentRow();
        Integer livello = cInv.getLivello();

        if (livello.intValue() == 1) {

            ViewObject voAcc = am.findViewObject("Acc_SoAccCito1livView1");
            Acc_SoAccCito1livViewRowImpl cAcc = (Acc_SoAccCito1livViewRowImpl) voAcc.getCurrentRow();
            Integer idAcc = cAcc.getIdacc1liv();

            ViewObject voIntAcc = am.findViewObject("Acc_SoInterventoPrecView1");
            voIntAcc.setWhereClause("IDACC1LIV = " + idAcc.toString());
            voIntAcc.executeQuery();

            ViewHelper.clearVO(voIntAcc);

            ViewObject voSelInt = am.findViewObject("Acc_IntervPrecElencoFull1");
            int countInt = voSelInt.getRowCount();

            for (int i = 0; i < countInt; i++) {
                Acc_IntervPrecElencoFullRow rowSelInt = (Acc_IntervPrecElencoFullRow) voSelInt.getRowAtRangeIndex(i);
                Integer eff = rowSelInt.getEffettuato();
                if (eff.intValue() == 1) {
                    Acc_SoInterventoPrecViewRow nInt = (Acc_SoInterventoPrecViewRow) voIntAcc.createRow();
                    nInt.setIdintervento(rowSelInt.getIdintervento());
                    nInt.setIdacc1liv(idAcc);
                    nInt.setAnnointervento(rowSelInt.getAnnointervento());
                    nInt.setTpscr(rowSelInt.getTpscr());
                    voIntAcc.insertRow(nInt);
                }
            }

        } else {

            ViewObject voAcc2 = am.findViewObject("Acc_SoAccCito2livView1");
            Acc_SoAccCito2livViewRowImpl cAcc2 = (Acc_SoAccCito2livViewRowImpl) voAcc2.getCurrentRow();
            Integer idAcc2 = cAcc2.getIdacc2liv();

            ViewObject voIntAcc = am.findViewObject("Acc_SoInterventoPrec2livView1");
            voIntAcc.setWhereClause("IDACC2LIV = " + idAcc2.toString());
            voIntAcc.executeQuery();

            ViewHelper.clearVO(voIntAcc);

            ViewObject voSelInt = am.findViewObject("Acc_IntPrec2ElencoFull1");
            int countInt = voSelInt.getRowCount();

            for (int i = 0; i < countInt; i++) {
                Acc_IntPrec2ElencoFullRow rowSelInt = (Acc_IntPrec2ElencoFullRow) voSelInt.getRowAtRangeIndex(i);
                Integer eff = rowSelInt.getEffettuato();
                if (eff.intValue() == 1) {
                    Acc_SoInterventoPrec2livViewRow nInt = (Acc_SoInterventoPrec2livViewRow) voIntAcc.createRow();
                    nInt.setIdintervento(rowSelInt.getIdintervento());
                    nInt.setIdacc2liv(idAcc2);
                    nInt.setAnnointervento(rowSelInt.getAnnointervento());
                    nInt.setTpscr(rowSelInt.getTpscr());
                    voIntAcc.insertRow(nInt);
                }
            }

        }
        return true;
    }

    protected boolean pendingUpdatesOnRichnav() {
        // TODO:  Override this view.commons.Parent_DataForwardAction method
        boolean checkSuper = super.pendingUpdatesOnRichnav();
        if (checkSuper)
            return true;

        boolean checkInterventi = this.modificheSuInterventi();
        return checkInterventi;
    }

    protected boolean pendingUpdatesOnSave() {
        // TODO:  Override this view.commons.DetAcc_DataForwardAction method
        boolean checkSuper = super.pendingUpdatesOnSave();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject voRic = voInvIter.getViewObject();

        Acc_AppModule am = (Acc_AppModule) voRic.getApplicationModule();
        checkSuper = am.getTransaction().isDirty();

        if (checkSuper)
            return true;

        boolean checkInterventi = this.modificheSuInterventi();
        return checkInterventi;

    }

    public boolean modificheSuInterventi() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        // String ulss = (String) session.getAttribute("ulss");
        String tpscr = (String) session.get("scr");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_SoInvitoView1Iterator");
        ViewObject voInvito = voInvIter.getViewObject();

        Acc_AppModule am = (Acc_AppModule) voInvito.getApplicationModule();
        Acc_SoInvitoViewRow cInv = (Acc_SoInvitoViewRow) voInvito.getCurrentRow();
        int liv = cInv.getLivello().intValue();

        if (liv == 1) {
            ViewObject voIntAttuale = am.findViewObject("Acc_IntervPrecElencoFull1");
            ViewObject voIntOrig = am.findViewObject("Acc_IntervPrecElencoFull2");
            ViewObject voAcc = am.findViewObject("Acc_SoAccCito1livView1");
            Acc_SoAccCito1livViewRowImpl currAcc = (Acc_SoAccCito1livViewRowImpl) voAcc.getCurrentRow();
            Integer idAcc = currAcc.getIdacc1liv();
            /*MOD20071121
          String whInt = "IDACC1LIV = " + idAcc.toString() + " and TPSCR = '" + tpscr + "'";
          voIntOrig.setWhereClause(whInt);
          */
            voIntOrig.setWhereClauseParam(0, tpscr);
            voIntOrig.setWhereClauseParam(1, idAcc);

            voIntOrig.executeQuery();

            //  RowSetIterator rsiAttuale = voIntAttuale.createRowSetIterator("iter_att");
            //  RowSetIterator rsiOrig = voIntOrig.createRowSetIterator("iter_orig");
            RowSetIterator rsiAttuale = ViewHelper.getRowSetIterator(voIntAttuale, "iter_att");
            RowSetIterator rsiOrig = ViewHelper.getRowSetIterator(voIntOrig, "iter_orig");


            while (rsiAttuale.hasNext()) {
                Acc_IntervPrecElencoFullRow rowAtt = (Acc_IntervPrecElencoFullRow) rsiAttuale.next();
                Acc_IntervPrecElencoFullRow rowOrig = (Acc_IntervPrecElencoFullRow) rsiOrig.next();
                Integer effAtt = rowAtt.getEffettuato();
                Integer effOrig = rowOrig.getEffettuato();
                Integer annoAtt = rowAtt.getAnnointervento();
                Integer annoOrig = rowOrig.getAnnointervento();

                if (effAtt != null || effOrig != null) {
                    if (effAtt == null && effOrig != null) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                    if (effAtt != null && effOrig == null) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                    if (effAtt.intValue() != effOrig.intValue()) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                }

                if (annoAtt != null || annoOrig != null) {
                    if (annoAtt == null && annoOrig != null) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                    if (annoAtt != null && annoOrig == null) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                    if (annoAtt.intValue() != annoOrig.intValue()) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                }

            } // while
            rsiAttuale.closeRowSetIterator();
            rsiOrig.closeRowSetIterator();

        } else {
            ViewObject voIntAttuale = am.findViewObject("Acc_IntPrec2ElencoFull1");
            ViewObject voIntOrig = am.findViewObject("Acc_IntPrec2ElencoFull2");
            ViewObject voAcc = am.findViewObject("Acc_SoAccCito2livView1");
            Acc_SoAccCito2livViewRowImpl currAcc = (Acc_SoAccCito2livViewRowImpl) voAcc.getCurrentRow();
            Integer idAcc = currAcc.getIdacc2liv();
            /*MOD20071121
          String whInt = "IDACC2LIV = " + idAcc.toString() + " and TPSCR = '" + tpscr + "'";
          voIntOrig.setWhereClause(whInt);
          */
            voIntOrig.setWhereClauseParam(0, tpscr);
            voIntOrig.setWhereClauseParam(1, idAcc);
            voIntOrig.executeQuery();


            //  RowSetIterator rsiAttuale = voIntAttuale.createRowSetIterator("iter_att");
            //  RowSetIterator rsiOrig = voIntOrig.createRowSetIterator("iter_orig");
            RowSetIterator rsiAttuale = ViewHelper.getRowSetIterator(voIntAttuale, "iter_att");
            RowSetIterator rsiOrig = ViewHelper.getRowSetIterator(voIntOrig, "iter_orig");

            while (rsiAttuale.hasNext()) {
                Acc_IntPrec2ElencoFullRow rowAtt = (Acc_IntPrec2ElencoFullRow) rsiAttuale.next();
                Acc_IntPrec2ElencoFullRow rowOrig = (Acc_IntPrec2ElencoFullRow) rsiOrig.next();
                Integer effAtt = rowAtt.getEffettuato();
                Integer effOrig = rowOrig.getEffettuato();
                Integer annoAtt = rowAtt.getAnnointervento();
                Integer annoOrig = rowOrig.getAnnointervento();

                if (effAtt != null || effOrig != null) {
                    if (effAtt == null && effOrig != null) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                    if (effAtt != null && effOrig == null) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                    if (effAtt.intValue() != effOrig.intValue()) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                }

                if (annoAtt != null || annoOrig != null) {
                    if (annoAtt == null && annoOrig != null) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                    if (annoAtt != null && annoOrig == null) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                    if (annoAtt.intValue() != annoOrig.intValue()) {
                        rsiAttuale.closeRowSetIterator();
                        rsiOrig.closeRowSetIterator();
                        return true;
                    }
                }

            } // while
            rsiAttuale.closeRowSetIterator();
            rsiOrig.closeRowSetIterator();

        }

        return false;
    }

    protected void doRollback() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        ViewObject vo = voInvIter.getViewObject();

        Acc_AppModule am = (Acc_AppModule) vo.getApplicationModule();
        String[] views = { "Acc_RicInvitiView1" };
        am.doRollback(views);
    }

    @SuppressWarnings("unchecked")
    public String onConf() {
        boolean saveOK = this.save();
        if (saveOK) {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            session.put("fromDett", Boolean.TRUE);
            return "acc_to";
        }
        return null;
    }

    public String onRollback() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
        Acc_AppModule am = (Acc_AppModule) voIter.getViewObject().getApplicationModule();
        am.doRollback("Acc_RicInvitiView1");

        ViewObject voRic = voIter.getViewObject();
        Row cInv = voRic.getCurrentRow();
        if (cInv != null)
            cInv.setAttribute("Selezionato", Boolean.TRUE);

        return "acc_to";
    }

    public void onAppl(@SuppressWarnings("unused") ActionEvent actionEvent) {
        @SuppressWarnings("unused")
        boolean saveOK = this.save();
    }

    public void onModEff(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_IntervPrecElencoFull1Iterator");
        ViewObject voInt = voIter.getViewObject();
        Acc_IntervPrecElencoFullRow cInt = (Acc_IntervPrecElencoFullRow) voInt.getCurrentRow();

        Number eff = cInt.getEffettuato();
        if (eff.intValue() == 0) {
            cInt.setAnnointervento(null);
        }
    }

    public void onPrev(@SuppressWarnings("unused") ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.previous();
            AccUtils.processaInvitoCorrente(voRic);
        }
    }

    public void onNext(@SuppressWarnings("unused") ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Acc_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.next();
            AccUtils.processaInvitoCorrente(voRic);
        }
    }

    public void setTabInterventi(RichTable tabInterventi) {
        this.tabInterventi = tabInterventi;
    }

    public RichTable getTabInterventi() {
        return tabInterventi;
    }
}
