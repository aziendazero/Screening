package view.accettazione.mammo;

import insiel.dataHandling.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.Connection;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.accettazione.common.AccMa_SoAnamnesimxSintomiViewRow;
import model.accettazione.common.AccMa_SoAnamnesimxViewRow;
import model.accettazione.common.AccMa_SoInvitoViewRow;

import model.common.AccMa_AppModule;

import model.commons.ParametriSistema;
import model.commons.ViewHelper;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.commons.AccUtils;
import view.commons.action.Parent_DataForwardAction;

import view.reports.ImageLoader;
import view.reports.SystemReport;

import view.util.Utility;

public class AccMa_anamAction extends Parent_DataForwardAction {
    private RichForm frm;
    private AccMa_ricercaAction accMa_ricercaAction;

    public void setAccMa_ricercaAction(AccMa_ricercaAction accMa_ricercaAction) {
        this.accMa_ricercaAction = accMa_ricercaAction;
    }

    public AccMa_ricercaAction getAccMa_ricercaAction() {
        return accMa_ricercaAction;
    }

    @Override
    protected void setAppModule() {
        this.amName = "AccMa_AppModule";
    }

    public void onAppl(ActionEvent actionEvent) {
        boolean saveOK = this.save();
    }

    public String onConf() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
        AccMa_SoAnamnesimxViewRow cAnam = (AccMa_SoAnamnesimxViewRow) voIter.getCurrentRow();
        System.out.println(cAnam.getIdopanamnesi().intValue());
        boolean saveOK = this.save();
        if (saveOK) {
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            session.put("fromDett", Boolean.TRUE);
            return "acc_toMammo";
        }
        return null;
    }

    public void prevInvitoMammo(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.previous();
            AccUtils.procInvCorrenteMammo(voRic);
            procCurrentAnam((AccMa_AppModule) voRic.getApplicationModule());
        }
    }

    public void nextInvitoMammo(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_RicInvitiView1Iterator");
            ViewObject voRic = voIter.getViewObject();
            voRic.next();
            AccUtils.procInvCorrenteMammo(voRic);
            procCurrentAnam((AccMa_AppModule) voRic.getApplicationModule());
        }
    }

    protected void beforeNavigate(String dest) throws Exception {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        if (dest.equals("acc_toMammo")) {
            session.put("fromDett", Boolean.TRUE);
        } else {
            AccUtils.beforeNavAccMa(dest, true);
        }
    }

    public void onChfscr(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
            AccMa_SoAnamnesimxViewRow cAnam = (AccMa_SoAnamnesimxViewRow) voIter.getCurrentRow();

            cAnam.setUmData(null);
            cAnam.setUmCons(new Integer(0));
            cAnam.setUmDtcons(null);
            cAnam.setUmRest(new Integer(0));
            cAnam.setUmDtrest(null);
        }
    }

    public void onChlc(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
            AccMa_SoAnamnesimxViewRow cAnam = (AccMa_SoAnamnesimxViewRow) voIter.getCurrentRow();

            cAnam.setUmDtcons(null);
            cAnam.setUmRest(new Integer(0));
            cAnam.setUmDtrest(null);
        }
    }

    public void onChlr(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
            AccMa_SoAnamnesimxViewRow cAnam = (AccMa_SoAnamnesimxViewRow) voIter.getCurrentRow();

            cAnam.setUmDtrest(null);
        }
    }

    private void procCurrentAnam(AccMa_AppModule am) {

        ViewObject voInvito = am.findViewObject("AccMa_SoInvitoView1");
        AccMa_SoInvitoViewRow cInv = (AccMa_SoInvitoViewRow) voInvito.getCurrentRow();
        Integer livello = cInv.getLivello();
        String codTs = cInv.getCodts();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String user = (String) session.get("user");
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        ViewObject voAnam = am.findViewObject("AccMa_SoAnamnesimxView1");
        ViewObject voAnamSint = am.findViewObject("AccMa_SoAnamnesimxSintomiView1");
        ViewObject voSintomi = am.findViewObject("AccMa_SintomiView1");
        Integer idAcc = null;

        if (livello.intValue() == 1) {
            ViewObject voAcc = am.findViewObject("AccMa_SoAccMammo1livView1");
            Row currAcc = voAcc.getCurrentRow();
            Integer idAcc1 = (Integer) currAcc.getAttribute("Idaccma1liv");
            idAcc = idAcc1;

            String wh = "IDACCMA_1LIV = " + idAcc1.toString();
            voAnam.setWhereClause(wh);
        } else {
            ViewObject voAcc = am.findViewObject("AccMa_SoAccMammo2livView1");
            Row currAcc = voAcc.getCurrentRow();
            Integer idAcc2 = (Integer) currAcc.getAttribute("Idaccma2liv");
            idAcc = idAcc2;

            String wh = "IDACCMA_2LIV = " + idAcc2.toString();
            voAnam.setWhereClause(wh);
        }

        voAnam.executeQuery();
        int countAnam = voAnam.getRowCount();

        // Se l'anamnesi non esiste, ne copio una eventualmente gia' esistente
        if (countAnam == 0) {
            boolean riportata = AccUtils.riportaAnamMA(am, codTs, ulss, idAcc, livello.intValue(), user);
            if (riportata) {
                voAnam.executeQuery();
                countAnam = voAnam.getRowCount();
            }
        }

        if (countAnam > 0) {
            AccMa_SoAnamnesimxViewRow fAnam = (AccMa_SoAnamnesimxViewRow) voAnam.first();
            voAnam.setCurrentRow(fAnam);

            Integer idAnam = fAnam.getIdAnamma();
            voAnamSint.setWhereClauseParam(0, idAnam);

            voAnamSint.setOrderByClause("ORDINE");
            voAnamSint.executeQuery();
        }

        // L'anamnesi non esiste e non e' stata riportata da una precedente
        else {
            voAnam.setWhereClause("1=2");
            voAnam.executeQuery();
            voAnamSint.setWhereClauseParam(0, null);

            voAnamSint.executeQuery();

            AccMa_SoAnamnesimxViewRow nAnam = (AccMa_SoAnamnesimxViewRow) voAnam.createRow();
            Integer idAnam = am.getNextIdAnamMx();
            Date dtoraCorr = DateUtils.getOracleDateNow();
            Date dtInvito = cInv.getDtapp();

            nAnam.setIdAnamma(idAnam);

            if (livello.intValue() == 1) {
                nAnam.setIdaccma1liv(idAcc);
            } else {
                nAnam.setIdaccma2liv(idAcc);
            }

            // Valorizzo l'operatore anamnesi in funzione dell'utente collegato
            ViewObject cnfUtenti = am.findViewObject("Cnf_SoCnfUtentiOperatoriView1");
            String whereCnf =
                "Cnf_SoCnfUtentiOperatori.ULSS = '" + ulss + "' AND Cnf_SoCnfUtentiOperatori.TPSCR = '" + tpscr +
                "' AND Cnf_SoCnfUtentiOperatori.USERNAME = '" + user + "' " +
                " AND (Cnf_SoOpmedico.DTFINEVALOPMEDICO IS NULL OR Cnf_SoOpmedico.DTFINEVALOPMEDICO > SYSDATE)" +
                " AND Cnf_SoOpmedico.CODOP = 1"; // Operatore anamnesi
            cnfUtenti.setWhereClause(whereCnf);
            cnfUtenti.executeQuery();
            Row userRow = cnfUtenti.first();
            if (userRow != null) {
               nAnam.setIdopanamnesi((Integer) userRow.getAttribute("Operatore"));
            }

            nAnam.setCodts(codTs);
            nAnam.setDtcreazione(dtoraCorr);
            nAnam.setOpcreazione(user);
            nAnam.setDtultmod(dtoraCorr);
            nAnam.setOpultmod(user);
            nAnam.setDtanamnesi(dtInvito);
            nAnam.setRipetuto(new Integer(0));
            nAnam.setUmFuoriscr(new Integer(0));
            nAnam.setUmCons(new Integer(0));
            nAnam.setUmRest(new Integer(0));
            nAnam.setToPresente(new Integer(0));
            nAnam.setToIncorso(new Integer(0));
            nAnam.setIcPresente(new Integer(0));
            nAnam.setUlss(ulss);
            nAnam.setTpscr(tpscr);

            nAnam.setIcIdintervento(new Integer(0));
            nAnam.setIcSedeint(new Integer(0));
            nAnam.setIcIdmot(new Integer(0));

            nAnam.setAfEsito(new Integer(0));
            nAnam.setAfMadre(new Integer(0));
            nAnam.setAfSorella(new Integer(0));
            nAnam.setAfAltro(new Integer(0));

            voAnam.insertRow(nAnam);

            String whCodSint = "ULSS = '" + ulss + "'";
            voSintomi.setWhereClause(whCodSint);
            voSintomi.executeQuery();

            RowSetIterator rsiSint = ViewHelper.getRowSetIterator(voSintomi, "iter_sint");

            while (rsiSint.hasNext()) {
                Row sintRow = rsiSint.next();
                Integer idSint = (Integer) sintRow.getAttribute("Idsintomo");
                String desSint = (String) sintRow.getAttribute("Descr");

                AccMa_SoAnamnesimxSintomiViewRow nAnSint = (AccMa_SoAnamnesimxSintomiViewRow) voAnamSint.createRow();

                Integer idAnamSint = am.getNextIdAnamMxSint();
                nAnSint.setIdAnammxSint(idAnamSint);
                nAnSint.setIdAnamma(idAnam);
                nAnSint.setIdsintomo(idSint);
                nAnSint.setUlss(ulss);
                nAnSint.setTpscr(tpscr);
                nAnSint.setDxIdsede(new Integer(0));
                nAnSint.setSxIdsede(new Integer(0));
                nAnSint.setDescr(desSint);

                voAnamSint.insertRow(nAnSint);
            }

            rsiSint.closeRowSetIterator();
        }

        // filtro operatori
        ViewObject voOper = am.findViewObject("AccMa_OpAnamView1");
        String whcl = "ULSS = '" + ulss + "'";
        //20110401 Serra: aggiunto il filtro sulla data di validita'
        String data_rif =
            cInv.getDtapp() == null ? DateUtils.getNow() :
            DateUtils.dateToString(DateUtils.DATE_PATTERN, cInv.getDtapp().dateValue());
        whcl +=
            " AND nvl(dtfinevalopmedico, to_date('31/12/2999','dd/mm/yyyy'))>=to_date('" + data_rif + "','" +
            DateUtils.DATE_PATTERN + "')";
        voOper.setWhereClause(whcl);
        voOper.executeQuery();
        
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:Idopanamnesi'])");
    }

    public void onChfam(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (((Integer) valueChangeEvent.getNewValue()).intValue() != 2) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
            AccMa_SoAnamnesimxViewRow cAnam = (AccMa_SoAnamnesimxViewRow) voIter.getCurrentRow();

            cAnam.setAfMadre(new Integer(0));
            cAnam.setAfSorella(new Integer(0));
            cAnam.setAfAltro(new Integer(0));
            cAnam.setAfAltroTesto(null);
        }
    }

    public void onChfamaltro(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (!(Boolean) valueChangeEvent.getNewValue()) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
            AccMa_SoAnamnesimxViewRow cAnam = (AccMa_SoAnamnesimxViewRow) voIter.getCurrentRow();

            cAnam.setAfAltroTesto(null);
        }
    }

    public void onChtop(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (((Integer) valueChangeEvent.getNewValue()).intValue() != 2) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
            AccMa_SoAnamnesimxViewRow cAnam = (AccMa_SoAnamnesimxViewRow) voIter.getCurrentRow();

            cAnam.setToDal(null);
            cAnam.setToAl(null);
            cAnam.setToIncorso(new Integer(0));
        }
    }

    public void stampaScheda(FacesContext facesContext, OutputStream outputStream) throws IOException, Exception,
                                                                                          FileNotFoundException {

        boolean _empty = true;

        try {
            _empty = (Boolean) ADFContext.getCurrent().getRequestScope().get("schedaEmpty");
        } catch (Throwable th) {
            th.printStackTrace();
        }

        if (!_empty)
            //18112011 gaion: salvo prima di stampare
            this.save();
        //fine 18112011

        // TODO:  Override this oracle.adf.controller.struts.actions.DataAction method
        Integer id = null;

        if (!_empty)
            id = (Integer) ADFContext.getCurrent().getRequestScope().get("idAnamma");
        else
            id = (Integer) ADFContext.getCurrent().getRequestScope().get("Idinvito");


        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_RicInvitiView1Iterator");
        AccMa_AppModule am = (AccMa_AppModule) voIter.getViewObject().getApplicationModule();
        ViewObject ulss_vo = am.findViewObject("AccMa_SoAziendaView1");
        ViewObject param_vo = am.findViewObject("AccMa_SoCnfPartemplateView1");
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        File pdf = null;
        HashMap h = null;
        try {
            SystemReport report = null;
            if (!_empty)
                report = new SystemReport(SystemReport.SCHEDA_ANAM_MX);
            else
                report = new SystemReport(SystemReport.SCHEDA_ANAM_MX_VUOTA);

            h = ParametriSistema.getParamTemplate(ulss, tpscr, ulss_vo, param_vo);
            h.put(((!_empty) ? "idanamma" : "idinvito"), Double.valueOf(String.valueOf(id)));

            File f1 = ImageLoader.getImage("flagsi.jpg");
            h.put("flagsi", f1);
            File f2 = ImageLoader.getImage("flagno.jpg");
            h.put("flagno", f2);

            File fQ = ImageLoader.getImage("quadrantiseno.GIF");
            h.put("qseno", fQ);

            Connection conn = am.getDBConnection();
            pdf = report.getPDFReport(conn, h);

            FileInputStream is = new FileInputStream(pdf);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            is.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (pdf != null)
                pdf.delete();
            ParametriSistema.releaseLogo(h);
        }
    }

    public String onReferto() {
        String dset = this.onRichnav("to_refMa_Anam", new HashMap());
        ADFContext.getCurrent().getRequestScope().put("tsk", "view_refMa_ricercaPageDef");
        return dset;
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
        DCIteratorBinding voInvIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
        ViewObject voInv = voInvIter.getViewObject();
        AccMa_AppModule am = (AccMa_AppModule) voInv.getApplicationModule();

        boolean mod = am.getTransaction().isDirty();

        if (mod) {
            ViewObject voAnam = am.findViewObject("AccMa_SoAnamnesimxView1");
            AccMa_SoAnamnesimxViewRow cAnam = (AccMa_SoAnamnesimxViewRow) voAnam.getCurrentRow();
            Date dtCorr = DateUtils.getOracleDateNow();

            cAnam.setDtultmod(dtCorr);
            String user = (String) sess.get("user");
            cAnam.setOpultmod(user);
        }

        return true;

    }

    public void setFrm(RichForm frm) {
        this.frm = frm;
    }

    public RichForm getFrm() {
        if (frm == null)
            findForward();
        return frm;
    }
    
    protected void findForward() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        Boolean initParam = (Boolean) session.get("initOK");
        if (initParam == null || !initParam.booleanValue()){
            DCBindingContainer bindings = (DCBindingContainer) getBindings();;
            this.initAnam(bindings);
        }
    }    

    protected boolean pendingUpdatesOnSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_SoAnamnesimxView1Iterator");
        ViewObject voAnam = voIter.getViewObject();
        AccMa_AppModule am = (AccMa_AppModule) voAnam.getApplicationModule();

        boolean dirty = am.getTransaction().isDirty();
        return dirty;
    }
    
    public void initAnam(DCBindingContainer bindings) {
        DCIteratorBinding voIter = bindings.findIteratorBinding("AccMa_RicInvitiView1Iterator");
        ViewObject voRic = voIter.getViewObject();
        AccMa_AppModule am = (AccMa_AppModule) voRic.getApplicationModule();
        ViewObject voInvito = am.findViewObject("AccMa_SoInvitoView1");
        AccMa_SoInvitoViewRow cInv = (AccMa_SoInvitoViewRow) voInvito.getCurrentRow();
        Integer livello = cInv.getLivello();
        String codTs = cInv.getCodts();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String user = (String) session.get("user");
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        ViewObject voAnam = am.findViewObject("AccMa_SoAnamnesimxView1");
        ViewObject voAnamSint = am.findViewObject("AccMa_SoAnamnesimxSintomiView1");
        ViewObject voSintomi = am.findViewObject("AccMa_SintomiView1");
        Integer idAcc = null;

        if (livello.intValue() == 1) {
            ViewObject voAcc = am.findViewObject("AccMa_SoAccMammo1livView1");
            Row currAcc = voAcc.getCurrentRow();
            Integer idAcc1 = (Integer) currAcc.getAttribute("Idaccma1liv");
            idAcc = idAcc1;

            String wh = "IDACCMA_1LIV = " + idAcc1.toString();
            voAnam.setWhereClause(wh);
        } else {
            ViewObject voAcc = am.findViewObject("AccMa_SoAccMammo2livView1");
            Row currAcc = voAcc.getCurrentRow();
            Integer idAcc2 = (Integer) currAcc.getAttribute("Idaccma2liv");
            idAcc = idAcc2;

            String wh = "IDACCMA_2LIV = " + idAcc2.toString();
            voAnam.setWhereClause(wh);
        }

        voAnam.executeQuery();
        int countAnam = voAnam.getRowCount();

        // Se l'anamnesi non esiste, ne copio una eventualmente gia' esistente
        if (countAnam == 0) {
            boolean riportata = AccUtils.riportaAnamMA(am, codTs, ulss, idAcc.intValue(), livello.intValue(), user);
            if (riportata) {
                voAnam.executeQuery();
                countAnam = voAnam.getRowCount();
            }
        }

        if (countAnam > 0) {
            AccMa_SoAnamnesimxViewRow fAnam = (AccMa_SoAnamnesimxViewRow) voAnam.first();
            voAnam.setCurrentRow(fAnam);

            Integer idAnam = fAnam.getIdAnamma();
            voAnamSint.setWhereClauseParam(0, idAnam);

            voAnamSint.setOrderByClause("ORDINE");
            voAnamSint.executeQuery();
        }

        // L'anamnesi non esiste e non e' stata riportata da una precedente
        else {
            voAnam.setWhereClause("1=2");
            voAnam.executeQuery();
            voAnamSint.setWhereClauseParam(0, null);

            voAnamSint.executeQuery();

            AccMa_SoAnamnesimxViewRow nAnam = (AccMa_SoAnamnesimxViewRow) voAnam.createRow();
            Integer idAnam = am.getNextIdAnamMx();
            Date dtoraCorr = DateUtils.getOracleDateNow();
            Date dtInvito = cInv.getDtapp();

            nAnam.setIdAnamma(idAnam);

            if (livello.intValue() == 1) {
                nAnam.setIdaccma1liv(idAcc);
            } else {
                nAnam.setIdaccma2liv(idAcc);
            }

            // Valorizzo l'operatore anamnesi in funzione dell'utente collegato
            ViewObject cnfUtenti = am.findViewObject("Cnf_SoCnfUtentiOperatoriView1");
            String whereCnf =
                "Cnf_SoCnfUtentiOperatori.ULSS = '" + ulss + "' AND Cnf_SoCnfUtentiOperatori.TPSCR = '" + tpscr +
                "' AND Cnf_SoCnfUtentiOperatori.USERNAME = '" + user + "' " +
                " AND (Cnf_SoOpmedico.DTFINEVALOPMEDICO IS NULL OR Cnf_SoOpmedico.DTFINEVALOPMEDICO > SYSDATE)" +
                " AND Cnf_SoOpmedico.CODOP = 1"; // Operatore anamnesi
            cnfUtenti.setWhereClause(whereCnf);
            cnfUtenti.executeQuery();
            Row userRow = cnfUtenti.first();
            if (userRow != null) {
                nAnam.setIdopanamnesi((Integer)userRow.getAttribute("Operatore"));
            }     

            nAnam.setCodts(codTs);
            nAnam.setDtcreazione(dtoraCorr);
            nAnam.setOpcreazione(user);
            nAnam.setDtultmod(dtoraCorr);
            nAnam.setOpultmod(user);
            nAnam.setDtanamnesi(dtInvito);
            nAnam.setRipetuto(new Integer(0));
            nAnam.setUmFuoriscr(new Integer(0));
            nAnam.setUmCons(new Integer(0));
            nAnam.setUmRest(new Integer(0));
            nAnam.setToPresente(new Integer(0));
            nAnam.setToIncorso(new Integer(0));
            nAnam.setIcPresente(new Integer(0));
            nAnam.setUlss(ulss);
            nAnam.setTpscr(tpscr);

            nAnam.setIcIdintervento(new Integer(0));
            nAnam.setIcSedeint(new Integer(0));
            nAnam.setIcIdmot(new Integer(0));

            nAnam.setAfEsito(new Integer(0));
            nAnam.setAfMadre(new Integer(0));
            nAnam.setAfSorella(new Integer(0));
            nAnam.setAfAltro(new Integer(0));

            voAnam.insertRow(nAnam);

            String whCodSint = "ULSS = '" + ulss + "'";
            voSintomi.setWhereClause(whCodSint);
            voSintomi.executeQuery();

            RowSetIterator rsiSint = ViewHelper.getRowSetIterator(voSintomi, "iter_sint");

            while (rsiSint.hasNext()) {
                Row sintRow = rsiSint.next();
                Integer idSint = (Integer) sintRow.getAttribute("Idsintomo");
                String desSint = (String) sintRow.getAttribute("Descr");

                AccMa_SoAnamnesimxSintomiViewRow nAnSint = (AccMa_SoAnamnesimxSintomiViewRow) voAnamSint.createRow();

                Integer idAnamSint = am.getNextIdAnamMxSint();
                nAnSint.setIdAnammxSint(idAnamSint);
                nAnSint.setIdAnamma(idAnam);
                nAnSint.setIdsintomo(idSint.intValue());
                nAnSint.setUlss(ulss);
                nAnSint.setTpscr(tpscr);
                nAnSint.setDxIdsede(new Integer(0));
                nAnSint.setSxIdsede(new Integer(0));
                nAnSint.setDescr(desSint);

                voAnamSint.insertRow(nAnSint);
            }

            rsiSint.closeRowSetIterator();
        }

        // filtro operatori
        ViewObject voOper = am.findViewObject("AccMa_OpAnamView1");
        String whcl = "ULSS = '" + ulss + "'";
        //20110401 Serra: aggiunto il filtro sulla data di validita'
        String data_rif =
            cInv.getDtapp() == null ? DateUtils.getNow() :
            DateUtils.dateToString(DateUtils.DATE_PATTERN, cInv.getDtapp().dateValue());
        whcl +=
            " AND nvl(dtfinevalopmedico, to_date('31/12/2999','dd/mm/yyyy'))>=to_date('" + data_rif + "','" +
            DateUtils.DATE_PATTERN + "')";
        voOper.setWhereClause(whcl);
        voOper.executeQuery();
        
        session.put("initOK", Boolean.TRUE);
    }
}
