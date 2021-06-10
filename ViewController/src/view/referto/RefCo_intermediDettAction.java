package view.referto;

import insiel.dataHandling.DateUtils;
import insiel.dataHandling.ObjectTransformationUtils;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.RefCo_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.AccCo_RicParam;
import model.datacontrol.Ref_2livBean;
import model.datacontrol.Ref_SearchBean;
import model.datacontrol.Sogg_RicParam;

import model.referto.Ref_SoEndoscopiaIntermViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.binding.OperationBinding;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.commons.action.Parent_DataForwardAction;

public class RefCo_intermediDettAction extends Parent_DataForwardAction {
    public RefCo_intermediDettAction() {
        super();
    }

    @Override
    protected void setAppModule() {
        this.amName = "RefCo_AppModule";
    }

    public void onSetEstensione(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        Ref_SoEndoscopiaIntermViewRowImpl r = (Ref_SoEndoscopiaIntermViewRowImpl) voIter.getCurrentRow();
        if (r == null)
            return;
        // l'estensione e' completa anche per ANASTOMOSI
        //se ho raggiunto il cieco l'estensione e' completa, altrimenti incompleta
        if (ConfigurationConstants.CODICE_REGIONE_CIECO.equals(r.getRegione()) ||
            ConfigurationConstants.CODICE_REGIONE_ANASTOMOSI.equals(r.getRegione())) {
            r.setEstensione(ConfigurationConstants.DB_TRUE);
            //non ci sono motivi di incompletezza
            r.setMotivo(ConfigurationConstants.DB_FALSE);
            r.setAltroMotivo(null);
        }

        else
            r.setEstensione(ConfigurationConstants.DB_FALSE);

    }

    public String onAppl() {
        boolean saveOK = this.save();
        //ricarico la tabella dei polipi
        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding polipiIter = bindings.findIteratorBinding("Ref_SoPolipiIntermView1Iterator");
            polipiIter.executeQuery();
        }
        
        return null;
    }


    protected void doRollback() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        RefCo_AppModule am =
            (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
        String[] views = { "Ref_SoEndoscopiaIntermView1" };
        am.doRollback(views);
    }

    protected boolean beforeSave() {
        boolean okToSave = super.beforeSave();

        if (okToSave) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
            RefCo_AppModule am =
                (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
            boolean mod = am.getTransaction().isDirty();

            if (mod) {
                Ref_SoEndoscopiaIntermViewRowImpl r = (Ref_SoEndoscopiaIntermViewRowImpl) voIter.getCurrentRow();
                Date dtCorr = DateUtils.getOracleDateNow();
                r.setDtultimamod(dtCorr);
                // imposto modalita scelta polipi e utente che fa la scelta
                Map session = ADFContext.getCurrent().getSessionScope();
                String user = (String) session.get("user");
                r.setOpscelta(user);
                r.setTipoSceltaPolipi("M"); //manuale
                return true;
            }
        }

        return false;
    }

    protected void beforeNavigate(String dest) throws Exception {
        Map session = ADFContext.getCurrent().getSessionScope();
        session.put("LINK_DETT", Boolean.FALSE);
        session.remove("CODTS");

        //le modifiche sono gia' state salvate o annullate
        Ref_2livBean bean2 =
            (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        bean2.setDirty(false);
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        Row r = voIter.getCurrentRow();
        String dst = dest.substring(0, 10);
        if (r != null) {

            //impostare i parametri di ricerca della gestione
            //soggetto e accettazione
            if (dst.equals("iniSogg.do")) {
                //reset dei parametri di ricerca
                Sogg_RicParam beanSogg =
                    (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                beanSogg.resetCampi();
                beanSogg.setTessSan((String) r.getAttribute("Codts"));
                beanSogg.setCognome((String) r.getAttribute("Cognome"));
                beanSogg.setNome((String) r.getAttribute("Nome"));
                
                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanSogg.setChiave(chiave);
                    }
                }
                
                beanSogg.setInEta(0);
                beanSogg.setNavigazione(Boolean.TRUE);
            } else if (dst.equals("acc_gen.do")) {
                //si suppone che il livello e' 2
                try {
                    AccessManager.checkPermission("SOAcc2Liv");
                } catch (IllegalAccessException ex) {
                    // non ha il permesso, permetto di navigare ma non
                    // imposto nulla
                    return;
                }

                AccCo_RicParam beanAcc =
                    (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();
                beanAcc.resetCampi();
                beanAcc.setCognome((String) r.getAttribute("Cognome"));
                beanAcc.setNome((String) r.getAttribute("Nome"));

                String chiave = (String) r.getAttribute("Chiave");
                
                if (chiave!=null && !"".equals(chiave)){
                    if (session.get("SOAccessoAnonimo")!=null){
                        Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                        if (sOAccessoAnonimo)
                            beanAcc.setChiave(chiave);
                    }
                }
                
                beanAcc.setTessSan((String) r.getAttribute("Codts"));
                beanAcc.setLivello("2");
                beanAcc.setNavigazione(Boolean.TRUE);
            }
        }

        if (!dst.startsWith("refCo_")) {
            Ref_SearchBean bean =
                (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
            bean.reset();
        } else {
            Ref_SearchBean bean =
                (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
            bean.reset();
            bean.setCognome((String) r.getAttribute("Cognome"));
            bean.setNome((String) r.getAttribute("Nome"));

            String chiave = (String) r.getAttribute("Chiave");
            
            if (chiave!=null && !"".equals(chiave)){
                if (session.get("SOAccessoAnonimo")!=null){
                    Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                    if (sOAccessoAnonimo)
                        bean.setChiave(chiave);
                }
            }
            
            bean.setCodts((String) r.getAttribute("Codts"));
            bean.setLivello("2");
            bean.setEmptyQuery(false);
        }

    }


    protected boolean pendingUpdatesOnSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        RefCo_AppModule am =
            (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
        boolean dirty = am.getTransaction().isDirty();
        return dirty;
    }

    public String onRollback() {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        OperationBinding rollbackBinding = bindings.getOperationBinding("Rollback");
        rollbackBinding.execute();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        if (session.get("LINK_DETT") == Boolean.TRUE) {
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
            RefCo_AppModule am =
                (RefCo_AppModule) voIter.getViewObject().getApplicationModule();
            ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
            Row r = vo.getCurrentRow();
            if (r == null)
                requeryReferti();
            
            session.put("LINK_DETT", Boolean.FALSE);
            session.remove("CODTS");
            return "livello2Co";
            
        } else {
            //requeryIntermedi();
            return "intermediCo";
        }
    }

    private void requeryIntermedi() {

        Ref_SearchBean bean =
            (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row currentEndo = vo.getCurrentRow();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        String where =
            "IMPORTATA = 0 AND " + "ULSS = '" + ulss + "' AND " +
            "TPSCR = '" + tpscr + "' AND ";

        if (bean.getInterm_dal() != null) {

            where +=
                "(DTENDO >= TO_DATE('" + bean.getInterm_dal() + "','" + DateUtils.DATE_PATTERN +
                "')" + "OR DTREFISTO >= TO_DATE('" + bean.getInterm_dal() + "','" +
                DateUtils.DATE_PATTERN + "')" +
                "OR (DTENDO IS NULL AND DTREFISTO IS NULL)) AND ";
        }


        if (bean.isInterm_completi()) {
            //carico solo quelli completi
            where += "COMPLETA=1 AND ";
        }

        if (bean.isIterm_errore()) {
            //carico solo quelli non importati per errore
            where += "(DTIMPORT IS NOT NULL AND ERRORE IS NOT NULL) AND ";
        }

        where = where.trim();

        if (where.endsWith("AND"))
            where = where.substring(0, where.length() - 3);

        vo.setWhereClause(where);

        vo.executeQuery();

        //reimposto la riga corrente
        Key k = null;
        if (currentEndo != null)
            k = currentEndo.getKey();

        //vedo se riesco a ripristinare la riga corrente
        if (k != null)
            ViewHelper.restoreCurRow(vo, k);
    }

    private void requeryReferti() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        ViewObject voIntermedi = voIter.getViewObject();
        RefCo_AppModule am = (RefCo_AppModule) voIntermedi.getApplicationModule();

        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCOView1");
        
        Row r = voIntermedi.getCurrentRow();
        
        String codts = (String)ADFContext.getCurrent().getSessionScope().get("CODTS");
        
        String where = "(DA_CONFERMARE IS NULL OR DA_CONFERMARE=0) AND ";
        //tessera sanitaria
        where += "Ref_SoInvito.CODTS='" + ( (codts!=null && ! codts.isEmpty()) ? codts :  (String) r.getAttribute("Codts")) + "' AND ";
        where += "Ref_SoInvito.LIVELLO=2 AND ";

        //seleziono solo gli inviti che possono avere un referto
        where +=
            "Ref_SoInvito.CODESITOINVITO IN (" +
            ObjectTransformationUtils.arrayToSQLString(ConfigurationConstants.CODICI_ESITI_REFERTABILI_CO) + ") ";

        vo.setWhereClause(where);
        vo.executeQuery();


    }

    public String onConfirm() {
        this.onAppl();
        Map session = ADFContext.getCurrent().getSessionScope();
        if (session.get("LINK_DETT") == Boolean.TRUE) {
            requeryReferti();
            session.put("LINK_DETT", Boolean.FALSE);
            session.remove("CODTS");
            return "to_refCo_toReferto";
            
        } else {
            requeryIntermedi();
            return "back_ricerca";
        }


    }

    public void onImport() {
        //prima salvo
        boolean saveOK = this.save();

        if (saveOK) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
            ViewObject endo = voIter.getViewObject();
            Ref_SoEndoscopiaIntermViewRowImpl r = (Ref_SoEndoscopiaIntermViewRowImpl) endo.getCurrentRow();

            //controllo che sia stato scelto almeno un polipo da importare
            RowIterator ri = r.getRef_SoPolipiIntermView();
            if (ri.getRowCount() > 0) {
                boolean polipi_selezionati = false;
                while (ri.hasNext()) {
                    Row polipo = ri.next();
                    String selezione = (String) polipo.getAttribute("Selezione");
                    if (!selezione.equals("0")) {
                        polipi_selezionati = true;
                    }
                }

                if (!polipi_selezionati) {
                    this.handleException("Impossibile avviare  l'import. Non e' stato selezionato nessun polipo da importare. ",
                                         null);
                    return;
                }
            }

            //esecuzione della procedura di import
            Integer[] rc = new Integer[] { new Integer(0) };
            Map session = ADFContext.getCurrent().getSessionScope();
            String user = (String) session.get("user");
            BigDecimal idIntermedio = null;
            if (r.getIdInterm() != null)
                idIntermedio = new BigDecimal(r.getIdInterm().doubleValue());
            RefCo_AppModule am = (RefCo_AppModule) endo.getApplicationModule();
            HashMap result = am.callImportIntermedi(r.getUlss(), r.getTpscr(), idIntermedio, user, rc);

            //ricarico l'endoscopia perche' se ok dovrebbe essere stata messa in stato importato
            endo.executeQuery();

            String msg = (String) result.get("error");


            //se il risultato e' null non ci sono stati errori
            if (msg == null || msg.length() == 0) {

                this.handleMessages(FacesMessage.SEVERITY_INFO,"Import eseguito con successo.");
            } else {
                this.handleMessages(FacesMessage.SEVERITY_ERROR,"Sono stati riscontrati problemi nell'esecuzione dell'import: " + msg);
            }
        }

    }

    public void onInsertEndo(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        ViewObject endo = voIter.getViewObject();
        Ref_SoEndoscopiaIntermViewRowImpl rCurr = (Ref_SoEndoscopiaIntermViewRowImpl) endo.getCurrentRow();
        if (rCurr != null) {
            //copio gli attributi dalla riga corrente
            Ref_SoEndoscopiaIntermViewRowImpl rNew = (Ref_SoEndoscopiaIntermViewRowImpl) endo.createRow();
            endo.insertRow(rNew);

            Map session = ADFContext.getCurrent().getSessionScope();
            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");
            RefCo_AppModule am = (RefCo_AppModule) endo.getApplicationModule();
            rNew.setIdInterm(am.getNextIdEndoscopiaIntermedia());
            rNew.setUlss(ulss);
            rNew.setTpscr(tpscr);
            rNew.setIdinvito(rCurr.getIdinvito());
            rNew.setCodts(rCurr.getCodts());
            rNew.setDtultimamod(DateUtils.getOracleDateNow());
            rNew.setCompleta("0");
            rNew.setImportata("0");

        }
    }

    public void onInsertPolipo(ActionEvent actionEvent) {
       
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding endo = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        DCIteratorBinding polipiIter = bindings.findIteratorBinding("Ref_SoPolipiIntermView1Iterator");
        ViewObject polipiVo = polipiIter.getViewObject();
        Ref_SoEndoscopiaIntermViewRowImpl rCurr = (Ref_SoEndoscopiaIntermViewRowImpl) endo.getCurrentRow();
        if (rCurr != null) {
            //RowIterator polipiIter = rCurr.getRef_SoPolipiIntermView();
            Row polipo = polipiVo.createRow();
            polipiVo.insertRow(polipo);
            RefCo_AppModule am = (RefCo_AppModule) polipiVo.getApplicationModule();
            polipo.setAttribute("IdIntermP", am.getNextIdPolipoIntermedio());
            polipo.setAttribute("Selezione", "0");
            polipo.setAttribute("IdPolipo", "0");
            if (rCurr.getDtendo() != null)
                polipo.setAttribute("DataPrelievo", rCurr.getDtendo());
            else
                polipo.setAttribute("DataPrelievo", DateUtils.getOracleDateNow());
            polipo.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
            Map session = ADFContext.getCurrent().getSessionScope();
            String ulss = (String) session.get("ulss");
            polipo.setAttribute("Ulss", ulss);


        }

    }

    public void onCreateEndo(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        ViewObject endo = voIter.getViewObject();
        //prendo i dati dall'invito
        RefCo_AppModule am = (RefCo_AppModule) endo.getApplicationModule();
        ViewObject invito = am.findViewObject("Ref_SoInvitoView2");
        Row inv = invito.first();

        if (inv != null) {
            Ref_SoEndoscopiaIntermViewRowImpl rNew = (Ref_SoEndoscopiaIntermViewRowImpl) endo.createRow();
            endo.insertRow(rNew);

            Map session = ADFContext.getCurrent().getSessionScope();
            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");

            rNew.setIdInterm(am.getNextIdEndoscopiaIntermedia());
            rNew.setUlss(ulss);
            rNew.setTpscr(tpscr);
            rNew.setIdinvito((Integer) inv.getAttribute("Idinvito"));
            rNew.setCodts((String) inv.getAttribute("Codts"));
            rNew.setDtultimamod(DateUtils.getOracleDateNow());
            rNew.setCompleta("0");
            rNew.setImportata("0");

        }
    }

    public void onComplete(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoEndoscopiaIntermView1Iterator");
        Ref_SoEndoscopiaIntermViewRowImpl rCurr = (Ref_SoEndoscopiaIntermViewRowImpl) voIter.getCurrentRow();

        rCurr.setCompleta("1");
    }

}
