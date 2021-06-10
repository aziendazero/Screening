package view.referto;

import java.io.OutputStream;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import javax.servlet.http.HttpServletRequest;

import model.LetteraAppModuleImpl;
import model.RefCa_AppModule;

import model.common.Cnf_AppModule;
import model.common.Sogg_AppModule;

import model.commons.AlgoritmoQuestionario;
import model.commons.AlgoritmoQuestionarioCA1;
import model.commons.ConfigurationConstants;
import model.commons.ConfigurationReader;
import model.commons.DomandaQuestionario;
import model.commons.Lettera;
import model.commons.LetteraRefertoCABean;
import model.commons.Questionario;
import model.commons.RispostaQuestionario;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.conf.Cnf_SoCnfLetteracentroViewRowImpl;

import model.global.common.A_SoSoggettoViewRow;

import model.referto.RefCa_SoCnfQuestionarioViewImpl;
import model.referto.RefCa_SoCnfQuestionarioViewRowImpl;
import model.referto.RefCa_SoDomandeQuestionarioViewImpl;
import model.referto.RefCa_SoDomandeQuestionarioViewRowImpl;
import model.referto.RefCa_SoOpmedicoRilevatoreViewRowImpl;
import model.referto.RefCa_SoRefcardioDatiViewImpl;
import model.referto.RefCa_SoRefcardioDatiViewRowImpl;
import model.referto.RefCa_SoRefertocardioBckViewImpl;
import model.referto.RefCa_SoRefertocardioViewImpl;
import model.referto.RefCa_SoRefertocardioViewRowImpl;
import model.referto.Ref_SoAllegatoViewRowImpl;
import model.referto.Ref_SoCnfSugg1livViewImpl;
import model.referto.Ref_SoCnfSugg1livViewRowImpl;
import model.referto.Ref_SoCnfTpinvitoViewRowImpl;
import model.referto.Ref_SoInvitiPerRefertiCAViewImpl;
import model.referto.Ref_SoInvitiPerRefertiCAViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;
import oracle.jbo.domain.Date;
import oracle.jbo.uicli.binding.JUCtrlAttrsBinding;

import org.apache.myfaces.trinidad.context.RequestContext;

import view.commons.AppConstants;
import view.commons.PageDescriptor;

import view.providers.QuestionarioProvider;

public class RefCa_questionarioAction extends RefCa_DataForwardAction {
    private static final NumberFormat numberFormat =
        new DecimalFormat("####.###", new DecimalFormatSymbols(Locale.ITALY));
    private static final String ALGORITMO_CA1 = "A01";
    
    private QuestionarioProvider QuestionarioProvider;
    
    public RefCa_questionarioAction() {
    }

    /**
     * Legge le risposte dalla request e le impacchetta in un List.
     *
     * @return Lista di RispostaQuestionario.
     * @param request
     */
    private List getRisposte(HttpServletRequest request) {
        List risposte = new ArrayList();
        for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {

            // Il nome del parametro deve essere nella forma <sezione>_<id>
            String paramName = (String) en.nextElement();
            try {
                int separatorPos = paramName.lastIndexOf("_");
                String sezione = paramName.substring(paramName.lastIndexOf(":") + 1, separatorPos);
                String idStr = paramName.substring(separatorPos + 1);
                int id = Integer.parseInt(idStr);
                for (int i = 0; i < request.getParameterValues(paramName).length; i++) {
                    String valore = request.getParameterValues(paramName)[i];
                    if (valore != null && valore.length() > 0) {
                        RispostaQuestionario risposta = new RispostaQuestionario();
                        risposta.setSezione(sezione);
                        risposta.setId(id);
                        risposta.setValore(valore);
                        risposte.add(risposta);
                    }
                }
            } catch (Throwable e) {
                // Il parametro non e' nel formato previsto o non ha valore, quindi lo ignoro
            }
        }
        return risposte;
    }

    /**
     * Override per aggiornare il modello del questionario con i campi della pagina.
     */
    private void handleLifecycle() {

        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        // Leggo le risposte
        List risposte = getRisposte(request);

        // Aggiorno il questionario
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        //am.aggiornaQuestionario(risposte);

        Questionario questionario =
            (Questionario) BindingContext.getCurrent().findDataControl("QuestionarioDataControl").getDataProvider();
        // Se il questionario e' read-only non lo aggiorno
        // Questo metodo viene chiamato anche quando a video i campi sono read-only
        // e resituiscono sempre un valore nullo
        if (questionario == null || questionario.isReadOnly()) {
            //niente



        } else {

            // Ciclo per ogni domanda del questionario.
            // Quando imposto il valore della domanda, se il nuovo valore e' diverso da quello precedente,
            // il questionario viene marcato come sporco.
            // Questo complica la valorizzazione delle selezioni multiple per le quali il valore
            // complessivo viene costruito a mano a mano che vengono recuperate le risposte.
            Collection domandeMul = new ArrayList();
            boolean rispostaTrovata;
            for (Iterator iDomanda = questionario.getDomandeTuttiLiv().iterator(); iDomanda.hasNext();) {
                DomandaQuestionario domanda = (DomandaQuestionario) iDomanda.next();

                // Cerco la risposta corrispondente alla domanda
                rispostaTrovata = false;
                for (Iterator iRisposta = risposte.iterator(); iRisposta.hasNext();) {
                    RispostaQuestionario risposta = (RispostaQuestionario) iRisposta.next();

                    // Se trovo la domanda corrispondente ne aggiorno il valore e passo alla risposta successiva
                    if (risposta.getId() == domanda.getId() && risposta.getSezione().equals(domanda.getSezione())) {
                        rispostaTrovata = true;

                        // Nel caso di domande con selezione multipla, devo aggiungere il valore a quelli gia' presenti
                        // e devo ciclare su tutte le risposte per vedere se ce ne sono altre
                        if (DomandaQuestionario.SELEZIONE_MULTIPLA.equals(domanda.getTipo())) {

                            // Cerco la domanda tra quelle gia' salvate
                            boolean domandaCloneTrovata = false;
                            DomandaQuestionario domandaClone = null;
                            for (Iterator i = domandeMul.iterator(); i.hasNext();) {
                                domandaClone = (DomandaQuestionario) i.next();
                                if (domandaClone.getId() == domanda.getId()) {
                                    domandaCloneTrovata = true;
                                    domandaClone.addValore(risposta.getValore());
                                    break;
                                }
                            }
                            if (!domandaCloneTrovata) {
                                try {
                                    domandaClone = (DomandaQuestionario) domanda.clone();
                                    domandeMul.add(domandaClone);
                                    domandaClone.setValore(risposta.getValore());
                                } catch (CloneNotSupportedException e) {
                                    // Non succede mai
                                    //logger.log(Level.SEVERE, "Eccezione durante l'aggiornamento delle riposte del questionario", e);
                                }
                            }
                        } else {
                            domanda.setValore(risposta.getValore());
                            // In caso di risposta singola non cerco ulteriori risposte
                            break;
                        }
                    }
                }
                if (!rispostaTrovata) {
                    domanda.setValore(null);
                }
            }

            // Valorizzo le selezioni multiple
            for (Iterator iDomanda = questionario.getDomandeTuttiLiv().iterator(); iDomanda.hasNext();) {
                DomandaQuestionario domanda = (DomandaQuestionario) iDomanda.next();
                if (DomandaQuestionario.SELEZIONE_MULTIPLA.equals(domanda.getTipo())) {

                    // Cerco la domanda clone di questa
                    for (Iterator iClone = domandeMul.iterator(); iClone.hasNext();) {
                        DomandaQuestionario domandaClone = (DomandaQuestionario) iClone.next();
                        if (domandaClone.getId() ==
                            domanda.getId()) {
                            // L'attributo String valore e' la concatenazione dei valori, quindi puo' essere usato
                            // per confrontare le liste di valori
                            if ((domanda.getValore() != null &&
                                 !domanda.getValore().equals(domandaClone.getValore())) ||
                                (domandaClone.getValore() != null &&
                                 !domandaClone.getValore().equals(domanda.getValore()))) {
                                domanda.setValori(domandaClone.getValori());
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String onApply() {
        handleLifecycle();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String user = (String) sess.get("user");
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");

        // Salvo il questionario
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();

        // Journaling
        String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
        if (flagAbilJournal != null && flagAbilJournal.equals("1")) {
            am.preapareJournaling(user, ulss, tpscr);
        }

        salvaQuestionario(user);
        am.getTransaction().commit();

        // Aggiorno l'esito dell'invito
        ViewObject invitoView = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
        Ref_SoInvitiPerRefertiCAViewRowImpl invitoRow =
            (Ref_SoInvitiPerRefertiCAViewRowImpl) invitoView.getCurrentRow();
        if (!"S".equals(invitoRow.getCodesitoinvito())) {
            GestoreReferti.updateEsito(am, invitoRow.getIdinvito());
        }
        return "applied";
    }

    public void onCreateReferto(ActionEvent actionEvent) {
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String user = (String) sess.get("user");
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");

        // Journaling
        String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
        if (flagAbilJournal != null && flagAbilJournal.equals("1")) {
            am.preapareJournaling(user, ulss, tpscr);
        }

        try {
            //am.creaReferto(user);
            ViewObject invitoView = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
            Ref_SoInvitiPerRefertiCAViewRowImpl invitoRow =
                (Ref_SoInvitiPerRefertiCAViewRowImpl) invitoView.getCurrentRow();

            Cnf_AppModule cnfAM =
                (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();

            // Recupero il questionario attivo
            ViewObject questionarioAttivo = am.findViewObject("RefCa_SoCnfQuestionarioAttivoView");
            RefCa_SoCnfQuestionarioViewRowImpl questionarioRow =
                (RefCa_SoCnfQuestionarioViewRowImpl) questionarioAttivo.first();
            Integer idQuestionario = questionarioRow.getIdcnfquest();

            // Recupero la configurazione del questionario, ad uso di getQuestionario()
            ViewObject cnfQuestionarioView = am.findViewObject("RefCa_SoCnfQuestionarioView1");
            cnfQuestionarioView.setWhereClause("IDCNFQUEST = :1");
            cnfQuestionarioView.setWhereClauseParam(0, idQuestionario);
            cnfQuestionarioView.executeQuery();

            // Creo il referto
            RefCa_SoRefertocardioViewImpl refertoView =
                (RefCa_SoRefertocardioViewImpl) am.findViewObject("RefCa_SoRefertocardioView1");
            RefCa_SoRefertocardioViewRowImpl refertoRow = (RefCa_SoRefertocardioViewRowImpl) refertoView.createRow();
            Date adesso = new Date(new java.sql.Timestamp(new java.util.Date().getTime()));
            refertoRow.setIdreferto(am.getNextIdRefertocardio());
            refertoRow.setDtinserimento(adesso);
            refertoRow.setOpinserimento(user);
            refertoRow.setDtultimamodifica(adesso);
            refertoRow.setOpultmodifica(user);
            refertoRow.setUlss(ulss);
            refertoRow.setTpscr(tpscr);
            //refertoRow.setIdinvito(invitoRow.getIdinvito());
            refertoRow.setCodts(invitoRow.getCodts());
            refertoRow.setDataRilevazione(adesso);
            refertoRow.setOprilevazione(cnfAM.getOperatore(ulss, tpscr, user));
            refertoRow.setIdsugg(new Integer(0));
            refertoRow.setIdcnfquest(idQuestionario);
            refertoRow.setCompleto(new Integer(0));
            refertoView.insertRow(refertoRow);

            // Recupero le domande del questionario usando id questionario e un referto inesistente
            // per avere le risposte vuote, ad uso di getQuestionario()
            RefCa_SoDomandeQuestionarioViewImpl domandeView =
                (RefCa_SoDomandeQuestionarioViewImpl) am.findViewObject("RefCa_SoDomandeQuestionarioView1");
            domandeView.setWhereClause("RefCa_SoCnfQuestDomande.IDCNFQUEST = :1 AND RefCa_SoRefcardioDati.IDREFERTO (+) = :2");
            domandeView.setWhereClauseParams(null);
            domandeView.setWhereClauseParam(0, idQuestionario);
            domandeView.setWhereClauseParam(1, new Integer(0)); // Referto inesistente
            domandeView.executeQuery();

        } catch (Exception ex) {
            handleException("Impossibile inserire un nuovo referto: " + ex.getMessage(), am.getTransaction());
        }
    }

    public String onDeleteReferto() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        String user = (String) sess.get("user");
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");

        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();

        // Journaling
        String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
        if (flagAbilJournal != null && flagAbilJournal.equals("1")) {
            am.preapareJournaling(user, ulss, tpscr);
        }

        //am.deleteReferto(user);
        try {
            // Controllo che sia possibile cancellare il referto
            ViewObject referti = am.findViewObject("RefCa_SoRefertocardioView1");
            RefCa_SoRefertocardioViewRowImpl refertoRow = (RefCa_SoRefertocardioViewRowImpl) referti.getCurrentRow();

            // Memorizzo l'id allegato da cancellare
            Integer idAllegato = refertoRow.getIdallegato();

            // Se il referto e' gia' chiuso non si puo' cancellare
            if (ConfigurationConstants.DB_TRUE.equals(refertoRow.getCompleto()))
                return "closed";

            Integer idReferto = refertoRow.getIdreferto();
            Integer idInvito = refertoRow.getIdinvito();

            // Cancello il backup precedente
            RefCa_SoRefertocardioBckViewImpl refertoBckView =
                (RefCa_SoRefertocardioBckViewImpl) am.findViewObject("RefCa_SoRefertocardioBckView1");
            String whcl = "idinvito = " + idInvito;
            refertoBckView.setWhereClause(whcl);
            refertoBckView.executeQuery();
            while (refertoBckView.hasNext()) {
                Row refertoBckRow = refertoBckView.next();
                refertoBckRow.remove();
            }

            // Salvo il referto su tabella backup
            String sql =
                "INSERT INTO so_refertocardio_bck" + " SELECT so_refertocardio.*, sysdate, '" + user + "'" +
                " FROM so_refertocardio WHERE idreferto = " + idReferto;
            am.getTransaction().executeCommand(sql);

            sql =
                "INSERT INTO so_refcardio_dati_bck" + " SELECT so_refcardio_dati.*, sysdate, '" + user + "'" +
                " FROM so_refcardio_dati WHERE idreferto = " + idReferto;
            am.getTransaction().executeCommand(sql);

            // Rimuovo le risposte al questionario
            sql = "DELETE FROM so_refcardio_dati WHERE idreferto = " + idReferto;
            am.getTransaction().executeCommand(sql);

            

            // Rimuovo l'eventuale allegato
            if (idAllegato != null) {
                refertoRow.setAttribute("Idallegato", null);
                        
                sql = "insert into so_allegato_bck " + 
                          "select * from so_allegato where idallegato = " + idAllegato;     
                am.getTransaction().executeCommand(sql);    
                am.getTransaction().postChanges();
                      
                sql = "DELETE FROM so_allegato WHERE idallegato = " + idAllegato;
                am.getTransaction().executeCommand(sql); 
            }
            
            // Rimuovo il referto
            refertoRow.remove();
        } catch (Exception e) {
            e.printStackTrace();
            am.getTransaction().rollback();
        }

        am.getTransaction().commit();

        ViewObject vo = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
        ViewHelper.queryAndRestoreCurrentRow(vo);

        //handleMessages(FacesMessage.SEVERITY_INFO,
         //              "Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");
         this.saveMessages("Referto cancellato con successo. Il soggetto risulta comunque presentato all'invito");
 
        return "to_refCa_ricerca";
    }

    public String onRiapriReferto() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        String user = (String) sess.get("user");
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");

        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();

        // Journaling
        String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
        if (flagAbilJournal != null && flagAbilJournal.equals("1")) {
            am.preapareJournaling(user, ulss, tpscr);
        }

        //am.riapriReferto(user);
        // Annullo il richiamo
        ViewObject inviti = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
        Ref_SoInvitiPerRefertiCAViewRowImpl invitoRow = (Ref_SoInvitiPerRefertiCAViewRowImpl) inviti.getCurrentRow();
        invitoRow.setTprichiamo(null);
        invitoRow.setDtrichiamo(null);
        invitoRow.setIdcentrorichiamo(null);

        // Recupero il referto
        ViewObject referti = am.findViewObject("RefCa_SoRefertocardioView1");
        RefCa_SoRefertocardioViewRowImpl refertoRow = (RefCa_SoRefertocardioViewRowImpl) referti.getCurrentRow();

        // Se la lettera non risulta spedita la cancello
        if (refertoRow.getDtspedizione() == null) {
            Integer idAllegato = refertoRow.getIdallegato();

            refertoRow.setIdallegato(null);
            refertoRow.setDtcreazione(null);
            refertoRow.setDtspedizione(null);

            ViewObject allegatoView = am.findViewObject("Ref_SoAllegatoView1");
            allegatoView.setWhereClause("IDALLEGATO=" + idAllegato);
            allegatoView.executeQuery();
            Row allegatoRow = allegatoView.first();
            if (allegatoRow != null) {
                allegatoRow.remove();
            }
        }

        // Marco il referto come non completo
        refertoRow.setCompleto(new Integer(0));

        // Imposto il questionario come modificabile
        Questionario questionario =
            (Questionario) BindingContext.getCurrent().findDataControl("QuestionarioDataControl").getDataProvider();
        questionario.setReadOnly(false);

        // TODO: Preparare journaling
        am.getTransaction().commit();
        
        refresh();
        return "to_refCa_questionario";
    }

    // Override
    protected boolean pendingUpdatesOnRichnav() {
        if (super.pendingUpdatesOnRichnav()) {
            return true;
        }

        // Verifico se sono state fatte modifiche al questionario
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        Questionario questionario =
            (Questionario) BindingContext.getCurrent().findDataControl("QuestionarioDataControl").getDataProvider();
        return questionario.isDirty();
    }

    // Override
    protected boolean pendingUpdatesOnSave() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        String user = (String) sess.get("user");

        // Salvo il questionario (senza commit)
        //RefCa_AppModule am = (RefCa_AppModule)BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        salvaQuestionario(user);
        return true;
    }

    protected void beforeNavigate(String dest) throws Exception {
        super.beforeNavigate(dest);

        if (dest.equals("goAnag")) {
            String codts =
                (String) ((JUCtrlAttrsBinding) BindingContext.getCurrent().getCurrentBindingsEntry().getControlBinding("Codts")).getValueAt(0);
            String ulss =
                (String) ((JUCtrlAttrsBinding) BindingContext.getCurrent().getCurrentBindingsEntry().getControlBinding("Ulss")).getValueAt(0);
            String tpscr =
                (String) ((JUCtrlAttrsBinding) BindingContext.getCurrent().getCurrentBindingsEntry().getControlBinding("Tpscr")).getValueAt(0);

            // Imposto le informazioni per il ritorno a questa pagina
            PageDescriptor page = new PageDescriptor("Questionario");
            page.setBackTitle("Torna al questionario");
            page.setAction("to_refCa_questionario");
            ADFContext.getCurrent().getSessionScope().put(AppConstants.FROM_PAGE, page);

            // Valorizzo le view necessarie alla visualizzazione anagrafica
            Sogg_AppModule soggAm =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            soggAm.impostaViewPerSoggetto(codts, ulss, tpscr);

            // Imposto l'attributo showTabs necessario alla visualizzazione anagrafica
            ADFContext.getCurrent().getSessionScope().put("showTabs", Boolean.TRUE);
        }
    }

    public void onFiltraMedici(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        am.filtraMedici();
    }

    /**
     * Aggiorna su db le risposte al questionario.
     */
    private void salvaQuestionario(String user) {

        // Recupero il referto corrente
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        RefCa_SoRefertocardioViewImpl refertiView =
            (RefCa_SoRefertocardioViewImpl) am.findViewObject("RefCa_SoRefertocardioView1");
        RefCa_SoRefertocardioViewRowImpl refertiRow = (RefCa_SoRefertocardioViewRowImpl) refertiView.getCurrentRow();
        Integer idReferto = refertiRow.getIdreferto();
        Integer idQuestionario = refertiRow.getIdcnfquest();

        // Elimino le eventuali vecchie risposte
        RefCa_SoRefcardioDatiViewImpl refcardioView =
            (RefCa_SoRefcardioDatiViewImpl) am.findViewObject("RefCa_SoRefcardioDatiView1");
        refcardioView.setWhereClause("IDREFERTO = :1");
        refcardioView.setWhereClauseParams(null);
        refcardioView.setWhereClauseParam(0, idReferto);
        refcardioView.executeQuery();
        while (refcardioView.hasNext()) {
            refcardioView.next().remove();
        }

        Date adesso = new Date(new java.sql.Timestamp(new java.util.Date().getTime()));

        // Ciclo per ogni domanda del questionario
        Questionario questionario =
            (Questionario) BindingContext.getCurrent().findDataControl("QuestionarioDataControl").getDataProvider();
        for (Iterator iDomanda = questionario.getDomandeTuttiLiv().iterator(); iDomanda.hasNext();) {
            DomandaQuestionario domanda = (DomandaQuestionario) iDomanda.next();

            // Nel caso di selezione multipla, scrivo una riga per ogni risposta
            for (Iterator i = domanda.getValori().iterator(); i.hasNext();) {
                String valore = (String) i.next();

                RefCa_SoRefcardioDatiViewRowImpl refcardioRow =
                    (RefCa_SoRefcardioDatiViewRowImpl) refcardioView.createRow();
                refcardioView.insertRow(refcardioRow);

                refcardioRow.setIdrefdati(am.getNextIdRefcardioDati());
                refcardioRow.setIdreferto(idReferto);
                refcardioRow.setIdcnfquest(idQuestionario);
                refcardioRow.setIdcnfdom(Long.valueOf(domanda.getId()).intValue());
                refcardioRow.setSezione(domanda.getSezione());
                refcardioRow.setOpinserimento(user);
                refcardioRow.setDtinserimento(adesso);
                refcardioRow.setOpultmodifica(user);
                refcardioRow.setDtultimamodifica(adesso);
                refcardioRow.setValore(valore);
            }
        }

        // Dichiaro il questionario non modificato
        questionario.setDirty(false);

    }

    /**
     * Effettua la chiusura del referto. Se la chiusura fallisce viene restituita
     * la lista degli errori, altrimenti restituisce null.
     * @return Lista di errori. null se chiusura effettuata con successo.
     * @param username
     */
    private List chiudiReferto(String username) {
        List messaggi = new ArrayList();
        try {
            RefCa_AppModule am =
                (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
            ViewObject inviti = am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
            Ref_SoInvitiPerRefertiCAViewRowImpl invitoRow =
                (Ref_SoInvitiPerRefertiCAViewRowImpl) inviti.getCurrentRow();
            ViewObject questionari = am.findViewObject("RefCa_SoCnfQuestionarioView1");
            RefCa_SoCnfQuestionarioViewRowImpl cnfQuestRow =
                (RefCa_SoCnfQuestionarioViewRowImpl) questionari.getCurrentRow();
            ViewObject referti = am.findViewObject("RefCa_SoRefertocardioView1");
            RefCa_SoRefertocardioViewRowImpl refertoRow = (RefCa_SoRefertocardioViewRowImpl) referti.getCurrentRow();
            Long idSuggObj = null;

            // Controlli formali del questionario
            Questionario questionario =
                (Questionario) BindingContext.getCurrent().findDataControl("QuestionarioDataControl").getDataProvider();
            List domande = questionario.getDomandeTuttiLiv();
            for (Iterator i = domande.iterator(); i.hasNext();) {
                DomandaQuestionario domanda = (DomandaQuestionario) i.next();
                String messaggio = null;

                // Obbligatorieta'
                if (domanda.getValore() == null) {
                    if (domanda.isObbligatorio()) {
                        messaggio = "Manca " + getNomeCampo(domanda);
                    }
                } else {
                    if (DomandaQuestionario.NUMERO.equals(domanda.getTipo())) {
                        try {
                            numberFormat.parse(domanda.getValore());
                        } catch (ParseException e) {
                            messaggio = "Formato di " + getNomeCampo(domanda) + " non valido";
                        }
                    }
                    if (DomandaQuestionario.INTERO.equals(domanda.getTipo())) {
                        try {
                            Integer.parseInt(domanda.getValore());
                        } catch (NumberFormatException e) {
                            messaggio =
                                "Formato di " + getNomeCampo(domanda) + " non valido. Deve essere un numero intero.";
                        }
                    }
                }

                if (messaggio != null) {
                    //logger.fine(messaggio);
                    messaggi.add(messaggio);
                }
            }
            if (messaggi.size() > 0) {
                return messaggi;
            }

            // Salvo il questionario e committo prima del calcolo dell'esito
            salvaQuestionario(username);
            am.getTransaction().commit();

            // Determino l'algoritmo per il calcolo del questionario
            AlgoritmoQuestionario algoritmo = null;
            String idAlgoritmo = cnfQuestRow.getAlgoritmo();

            // Preimposto idSugg col valore attuale
            long idSugg = refertoRow.getIdsugg().longValue();

            // Se non c'e' l'algoritmo non effettuo i calcoli
            if (idAlgoritmo != null) {
                if (ALGORITMO_CA1.equals(idAlgoritmo)) {
                    algoritmo = new AlgoritmoQuestionarioCA1();
                } else {
                    //logger.log(Level.FINE, "Nessun algoritmo di calcolo trovato");
                    messaggi.add("Non e' stato trovato l'algoritmo di calcolo " + algoritmo);
                    return messaggi;
                }

                // Calcolo i campi protetti del questionario.
                idSuggObj = (Long) algoritmo.calcola(questionario);
                if (idSuggObj != null) {
                    idSugg = idSuggObj.longValue();
                }
            }

            // Salvo il questionario con eventuali campi calcolati dall'algoritmo
            salvaQuestionario(username);
            am.getTransaction().commit();

            // Se ho eseguito l'algoritmo ed ha restituito null, ci sono stati errori nel calcolo
            if (idAlgoritmo != null && idSuggObj == null) {
                return algoritmo.getMessaggi();
            }

            // Controllo che la data e il medico siano presenti
            String msg = null;
            if (refertoRow.getDataRilevazione() == null) {
                msg = "La data rilevazione e' obbligatoria";
            }
            if (refertoRow.getOprilevazione() == null) {
                msg = "Il medico di rilevazione e' obbligatorio";
            }
            if (msg != null) {
                messaggi.add(msg);
                return messaggi;
            }

            // Aggiorno l'esito dell'invito
            if (!"S".equals(invitoRow.getCodesitoinvito())) {
                GestoreReferti.updateEsito(am, invitoRow.getIdinvito());
            }

            impostaRichiamo(username, idSugg);

            // Aggiorno il referto
            refertoRow.setCompleto(new Integer(1));
            refertoRow.setOpultmodifica(username);
            refertoRow.setDtultimamodifica(getOracleNow());
            if (idSugg >= 0) {
                refertoRow.setIdsugg(Long.valueOf(idSugg).intValue());
            }

            // Finalizzo il salvataggio del referto.
            // (Significa che in fase di commit verra' fatta la insert o l'update del referto
            // con i campi fin qui valorizzati.
            // Importante nel caso venga fatta anche la stampa del referto, che implica
            // un update del referto; senza postChanges() farebbe una insert con gia' valorizzato
            // l'id allegato, che pero' non sarebbe ancora salvato, e darebbe errore su FK constraint).
            am.getTransaction().commit();
            am.getTransaction().postChanges();

            // Imposto il questionario read-only
            questionario.setReadOnly(true);
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Errore in chiusura referto", e);
            messaggi.add("Errore imprevisto nella chiusura del referto");
            return messaggi;
        }
        return null;
    }

    private String getNomeCampo(DomandaQuestionario domanda) {
        String nomeCampo;
        if (domanda.getTesto() != null && domanda.getTesto().trim().length() > 0) {
            nomeCampo = domanda.getTesto();
        } else if (domanda.getCodice() != null) {
            nomeCampo = domanda.getCodice();
        } else {
            if (domanda.isObbligatorio()) {
                nomeCampo = "campo obbligatorio";
            } else {
                nomeCampo = "campo";
            }
        }
        return nomeCampo;
    }

    private void impostaRichiamo(String username, long idSugg) throws Exception {
        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
        ViewObject inviti = am.findViewObject("Ref_SoInvitoView1");
        Row invitoRow = inviti.first();
        invitoRow.setAttribute("Tprichiamo", null);
        invitoRow.setAttribute("Dtrichiamo", null);
        invitoRow.setAttribute("Idcentrorichiamo", null);
        if (idSugg > 0) {

            // Recupero il tipo invito corrispondente al suggerimento
            ViewObject referti = am.findViewObject("RefCa_SoRefertocardioView1");
            RefCa_SoRefertocardioViewRowImpl refertoRow = (RefCa_SoRefertocardioViewRowImpl) referti.getCurrentRow();
            Ref_SoCnfSugg1livViewImpl suggView =
                (Ref_SoCnfSugg1livViewImpl) am.findViewObject("Ref_SoCnfSugg1livView1");
            Row[] suggRows =
                suggView.findByKey(new Key(new Object[] { new Integer(new Long(idSugg).intValue()), refertoRow.getUlss(),
                                                          refertoRow.getTpscr() }), 1);
            Ref_SoCnfSugg1livViewRowImpl suggRow = (Ref_SoCnfSugg1livViewRowImpl) suggRows[0];
            String tipoInvito = suggRow.getIdtpinvito();
            if (tipoInvito != null) {
                invitoRow.setAttribute("Tprichiamo", tipoInvito);
                Calendar cal = Calendar.getInstance();

                // Data richiamo = Data appuntamento + giorni richiamo
                int ggRichiamo = suggRow.getGgrichiamo() != null ? suggRow.getGgrichiamo().intValue() : 0;
                Date dateFrom = (Date) invitoRow.getAttribute("Dtapp");
                cal.setTime(dateFrom.dateValue());
                cal.add(Calendar.DATE, ggRichiamo);
                invitoRow.setAttribute("Dtrichiamo", new Date(new java.sql.Date(cal.getTimeInMillis())));

                // Recupero la categoria del richiamo
                ViewObject tipoInvitoView = am.findViewObject("Ref_SoCnfTpinvitoView1");
                tipoInvitoView.setWhereClause("ULSS = :1 AND TPSCR = :2 AND IDTPINVITO = :3");
                tipoInvitoView.setWhereClauseParam(0, suggRow.getUlss());
                tipoInvitoView.setWhereClauseParam(1, suggRow.getTpscr());
                tipoInvitoView.setWhereClauseParam(2, tipoInvito);
                tipoInvitoView.executeQuery();
                Ref_SoCnfTpinvitoViewRowImpl tipoInvitoRow = (Ref_SoCnfTpinvitoViewRowImpl) tipoInvitoView.first();
                int categoriaRichiamo = 0;
                if (tipoInvitoRow != null && tipoInvitoRow.getIdcateg() != null) {
                    categoriaRichiamo = tipoInvitoRow.getIdcateg().intValue();
                }

                if (categoriaRichiamo == 1) {
                    invitoRow.setAttribute("Chiusuraroundindiv", new Integer(1));
                } else {
                    invitoRow.setAttribute("Chiusuraroundindiv", new Integer(0));
                }

                // Imposto il centro del richiamo
                Map result =
                    am.callGetCentroRichiamo((String) invitoRow.getAttribute("Codts"),
                                             (String) invitoRow.getAttribute("Ulss"),
                                             (String) invitoRow.getAttribute("Tpscr"),
                                             new BigDecimal(((Integer) invitoRow.getAttribute("Idinvito")).doubleValue()),
                                             new BigDecimal(tipoInvitoRow.getLivello().intValue()));
                if (result.get("error") != null) {
                    throw new Exception((String) result.get("error"));
                } else {
                    invitoRow.setAttribute("Idcentrorichiamo",
                                           new Integer(((BigDecimal) result.get("centro")).intValue()));
                }
            }
        }
    }

    private Date getOracleNow() {
        return new Date(new Date());
    }

    public List creaLettera(String username) {
        Integer idAllegato = null;
        try {
            RefCa_AppModule am =
                (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();
            Ref_SoInvitiPerRefertiCAViewImpl invitoView =
                (Ref_SoInvitiPerRefertiCAViewImpl) am.findViewObject("Ref_SoInvitiPerRefertiCAView1");
            Ref_SoInvitiPerRefertiCAViewRowImpl invitoRow =
                (Ref_SoInvitiPerRefertiCAViewRowImpl) invitoView.getCurrentRow();
            RefCa_SoRefertocardioViewImpl refertoView =
                (RefCa_SoRefertocardioViewImpl) am.findViewObject("RefCa_SoRefertocardioView1");
            RefCa_SoRefertocardioViewRowImpl refertoRow =
                (RefCa_SoRefertocardioViewRowImpl) refertoView.getCurrentRow();
            Integer idRilevatore = refertoRow.getOprilevazione();
            ViewObject medicoRilevatoreView = am.findViewObject("RefCa_SoOpmedicoRilevatoreView2");
            medicoRilevatoreView.setWhereClause("IDOP = " + idRilevatore);
            medicoRilevatoreView.executeQuery();
            RefCa_SoOpmedicoRilevatoreViewRowImpl medicoRilevatoreRow =
                (RefCa_SoOpmedicoRilevatoreViewRowImpl) medicoRilevatoreView.first();
            ViewObject allegatoView = am.findViewObject("Ref_SoAllegatoView1");

            long idSugg = refertoRow.getIdsugg().longValue();

            // Memorizzo l'id dell'allegato vecchio, perche' dovro' cancellarlo alla fine
            Integer idAllegatoOld = refertoRow.getIdallegato();

            // Recupero il soggetto
            ViewObject soggetti = am.findViewObject("A_SoSoggettoView1");
            soggetti.setWhereClause("CODTS='" + invitoRow.getCodts() + "' AND A_SoSoggetto.ULSS='" +
                                    invitoRow.getUlss() + "'");
            soggetti.executeQuery();
            A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) soggetti.first();

            java.util.Date dataNascita = soggetto.getDataNascita().dateValue();

            // Calcolo l'eta' del soggetto
            Calendar calNascita = Calendar.getInstance();
            calNascita.setTime(dataNascita);
            Calendar calAdesso = Calendar.getInstance();
            int eta = calAdesso.get(Calendar.YEAR) - calNascita.get(Calendar.YEAR);
            if (calNascita.get(Calendar.MONTH) > calAdesso.get(Calendar.MONTH) ||
                (calNascita.get(Calendar.MONTH) == calAdesso.get(Calendar.MONTH) &&
                 calNascita.get(Calendar.DATE) > calAdesso.get(Calendar.DATE))) {
                eta--;
            }

            // Recupero il template
            ViewObject letteraCentroView = am.findViewObject("Cnf_SoCnfLetteracentroView1");
            String whcl =
                "ULSS = :1 AND TPSCR = :2 AND IDSUGG = :3 AND (ETA_DA IS NULL OR ETA_DA <= :4) AND (ETA_A IS NULL OR ETA_A >= :5)";
            letteraCentroView.setWhereClause(whcl);
            letteraCentroView.setWhereClauseParams(null);
            letteraCentroView.setWhereClauseParam(0, refertoRow.getUlss());
            letteraCentroView.setWhereClauseParam(1, refertoRow.getTpscr());
            letteraCentroView.setWhereClauseParam(2, new Integer(new Long(idSugg).intValue()));
            letteraCentroView.setWhereClauseParam(3, new Integer(eta));
            letteraCentroView.setWhereClauseParam(4, new Integer(eta));
            letteraCentroView.setOrderByClause("nvl(eta_a, 200) - nvl(eta_da, 0)");
            letteraCentroView.executeQuery();
            Cnf_SoCnfLetteracentroViewRowImpl letteraCentroRow =
                (Cnf_SoCnfLetteracentroViewRowImpl) letteraCentroView.first();
            if (letteraCentroRow != null && letteraCentroRow.getCodtempl() != null) {
                long idTemplate = letteraCentroRow.getCodtempl().longValue();
                LetteraAppModuleImpl letteraAm =
                    (LetteraAppModuleImpl) BindingContext.getCurrent().findDataControl("LetteraAppModuleDataControl").getDataProvider();
                Lettera lettera = new Lettera(refertoRow.getUlss(), refertoRow.getTpscr(), idTemplate, letteraAm);

                // Recupero la modalita' di spedizione
                boolean spedizioneStandard = true;
                ViewObject paramView = am.findViewObject("A_SoCnfParametriSistemaView1");
                paramView.setWhereClause("ULSS = '" + refertoRow.getUlss() + "' AND TPSCR = '" + refertoRow.getTpscr() +
                                         "' AND UPPER(NOME_PARAM) = 'MOD_SPEDIZIONE'");
                paramView.executeQuery();
                Row paramRow = paramView.first();
                if (paramRow != null) {
                    spedizioneStandard = "standard".equals((String) paramRow.getAttribute("ValoreParam"));
                }

                // Valorizzo il bean per la stampa
                LetteraRefertoCABean letteraBean = new LetteraRefertoCABean();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                // Determino l'indirizzo del soggetto
                String indirizzoSoggetto;
                String capSoggetto;
                String comuneSoggetto;
                String provinciaSoggetto;
                if (soggetto.getIndirizzoScr() != null) {
                    indirizzoSoggetto = soggetto.getIndirizzoScr();
                    capSoggetto = soggetto.getCap();
                    comuneSoggetto = soggetto.getDescrizione();
                    provinciaSoggetto = soggetto.getCodpr();
                } else if (soggetto.getIndirizzoDom() != null && spedizioneStandard) {
                    indirizzoSoggetto = soggetto.getIndirizzoDom();
                    capSoggetto = soggetto.getCap1();
                    comuneSoggetto = soggetto.getDescrizione1();
                    provinciaSoggetto = soggetto.getCodpr1();
                } else {
                    indirizzoSoggetto = soggetto.getIndirizzoRes();
                    capSoggetto = soggetto.getCap2();
                    comuneSoggetto = soggetto.getDescrizione2();
                    provinciaSoggetto = soggetto.getCodpr2();
                }

                //trovo la tessera sanitaria
                String tesseraSanitaria = SoggUtils.trovaTessera(am, soggetto.getCodts(), soggetto.getUlss());

                // Recupero la descrizione del suggerimento
                Ref_SoCnfSugg1livViewImpl suggView =
                    (Ref_SoCnfSugg1livViewImpl) am.findViewObject("Ref_SoCnfSugg1livView1");
                Row[] suggRows =
                    suggView.findByKey(new Key(new Object[] { new Integer(new Long(idSugg).intValue()), refertoRow.getUlss(),
                                                              refertoRow.getTpscr() }), 1);
                Ref_SoCnfSugg1livViewRowImpl suggRow = (Ref_SoCnfSugg1livViewRowImpl) suggRows[0];

                letteraBean.setData_stampa(dateFormat.format(new java.util.Date()));
                letteraBean.setData_esame(dateFormat.format(refertoRow.getDataRilevazione().dateValue()));
                letteraBean.setCognome(soggetto.getCognome());
                letteraBean.setNome(soggetto.getNome());
                letteraBean.setIndirizzo(indirizzoSoggetto);
                letteraBean.setCap(capSoggetto);
                letteraBean.setComune(comuneSoggetto);
                letteraBean.setProvincia(provinciaSoggetto);
                letteraBean.setData_nascita(dateFormat.format(soggetto.getDataNascita().dateValue()));
                letteraBean.setTessera_sanitaria(tesseraSanitaria);
                letteraBean.setCodice_fiscale(soggetto.getCf());
                letteraBean.setSesso(soggetto.getSesso());
                letteraBean.setEmail(soggetto.getEmail());
                letteraBean.setCellulare(soggetto.getCellulare());
                letteraBean.setTelefono1(soggetto.getTel1());
                letteraBean.setTelefono2(soggetto.getTel2());
                letteraBean.setCentro_prelievo(invitoRow.getDescrCentroPrel());
                letteraBean.setConsiglio(suggRow.getDescrizione());
                letteraBean.setNote(refertoRow.getNote() == null ? "" : refertoRow.getNote());
                letteraBean.setIndirizzo_centro(invitoRow.getIndirizzoCentro());
                letteraBean.setSede_centro(invitoRow.getSedeCentro());
                letteraBean.setOrari_centro(invitoRow.getOrariCentro());
                letteraBean.setTel_centro(invitoRow.getTelCentro());
                letteraBean.setData_app(dateFormat.format(invitoRow.getDtapp().dateValue()));
                letteraBean.setData_creazione(dateFormat.format(new java.util.Date()));
                letteraBean.setZona(invitoRow.getZona());
                letteraBean.setSupervisore(medicoRilevatoreRow.getTitolo() + " " + medicoRilevatoreRow.getNome() + " " +
                                           medicoRilevatoreRow.getCognome());

                // Medico, solo se aderisce allo screening
                if (soggetto.getDtadesCa() != null) {
                    letteraBean.setMedico(soggetto.getNome1() + " " + soggetto.getCognome1());
                }

                Questionario questionario =
                    (Questionario) BindingContext.getCurrent().findDataControl("QuestionarioDataControl").getDataProvider();
                for (Iterator i = questionario.getDomandeTuttiLiv().iterator(); i.hasNext();) {
                    DomandaQuestionario domanda = (DomandaQuestionario) i.next();
                    if ("SYS_USED".equals(domanda.getCodice())) {
                        letteraBean.setSys(domanda.getValore());
                    } else if ("DIA_USED".equals(domanda.getCodice())) {
                        letteraBean.setDia(domanda.getValore());
                    } else if ("GLICEM_NUM".equals(domanda.getCodice())) {
                        letteraBean.setGlicemia(domanda.getValore());
                    } else if ("COLESTEROL".equals(domanda.getCodice())) {
                        letteraBean.setColesterolo_tot(domanda.getValore());
                    } else if ("COL_HDL".equals(domanda.getCodice())) {
                        letteraBean.setColesterolo_hdl(domanda.getValore());
                    } else if ("CIRCONF".equals(domanda.getCodice())) {
                        letteraBean.setCirconf_vita(domanda.getValore());
                    } else if ("BMI".equals(domanda.getCodice())) {
                        letteraBean.setBmi(domanda.getValore());
                    } else if ("AF_TOT".equals(domanda.getCodice())) {
                        letteraBean.setAttivita_fisica(domanda.getValore());
                    }
                }

                // Preparo il blob di output
                BlobDomain pdfBlob = new BlobDomain();
                OutputStream pdfOutputStream = pdfBlob.getOutputStream();

                // Scrivo il pdf nel blob
                lettera.writePdf(letteraBean, pdfOutputStream);

                // Salvo il PDF come allegato
                Ref_SoAllegatoViewRowImpl allegatoRow = (Ref_SoAllegatoViewRowImpl) allegatoView.createRow();
                allegatoView.insertRow(allegatoRow);

                idAllegato = am.getNextIdAllegato();
                allegatoRow.setIdallegato(idAllegato);
                allegatoRow.setCodts(soggetto.getCodts());
                allegatoRow.setDtcreazione(getOracleNow());
                allegatoRow.setTpscr(refertoRow.getTpscr());
                allegatoRow.setUlss(refertoRow.getUlss());
                allegatoRow.setLettera(pdfBlob);

                am.getTransaction().postChanges();

                refertoRow.setIdallegato(idAllegato);
                refertoRow.setDtcreazione(allegatoRow.getDtcreazione());
                refertoRow.setDtspedizione(null);
            } else {
                refertoRow.setIdallegato(null);
                refertoRow.setDtcreazione(null);
                refertoRow.setDtspedizione(null);
            }

            // Elimino l'eventuale lettera precedente
            if (idAllegatoOld != null) {
                String sql = "insert into so_allegato_bck " + 
                          "select * from so_allegato where idallegato = " + idAllegatoOld;     
                am.getTransaction().executeCommand(sql);    
                
                allegatoView.setWhereClause("IDALLEGATO = " + idAllegatoOld);
                allegatoView.executeQuery();
                allegatoView.setWhereClauseParams(null);
                Ref_SoAllegatoViewRowImpl allegatoRow = (Ref_SoAllegatoViewRowImpl) allegatoView.first();
                if (allegatoRow != null) {
                    allegatoRow.remove();
                }
            }
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Eccezione nella creazione del report", e);
            List result = new ArrayList();
            result.add(e.getMessage());
            return result;
        }
        return null;
    }

     public String onChiudi() {
        handleLifecycle();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        String user = (String) sess.get("user");
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        String stampaLettera = (String) adfCtx.getRequestScope().get("ref_lettera");  //(String)clientEvent.getParameters().get("ref_lettera");//

        RefCa_AppModule am =
            (RefCa_AppModule) BindingContext.getCurrent().findDataControl("RefCa_AppModuleDataControl").getDataProvider();

        // Journaling
        String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
        if (flagAbilJournal != null && flagAbilJournal.equals("1")) {
            am.preapareJournaling(user, ulss, tpscr);
        }

        try {
            List errori = chiudiReferto(user);
            if (errori != null) {
                StringBuffer msg = new StringBuffer();
                for (int i = 0; i < errori.size(); i++) {
                    if (i > 0) {
                        msg.append("; ");
                    }
                    msg.append(errori.get(i));
                }
                //am.doRollback(new String[]{"Ref_SoInvitiPerRefertiCAView1", "RefCa_SoCnfQuestionarioView1", "RefCa_SoRefertocardioView1"});
                handleException(msg.toString(),
                                null); // Non deve fare rollback, altrimenti resetta le current row nei view object
            } else {
                if (!"lascia".equals(stampaLettera)) {
                    errori = creaLettera(user);
                    if (errori != null && errori.size() > 0) {
                        //am.doRollback(new String[]{"Ref_SoInvitiPerRefertiCAView1", "RefCa_SoCnfQuestionarioView1", "RefCa_SoRefertocardioView1"});
                        handleException((String) errori.get(0), null);
                        return null;
                    } else {
                        am.getTransaction().commit();
                    }
                } else {
                    am.getTransaction().commit();
                }
                
                refresh();
            }
        } catch (Exception e) {
            //logger.log(Level.SEVERE, "Eccezione in chiusura referto", e);
            handleException(e.getMessage(), am.getTransaction());
            return null;
        }

        return "to_refCa_questionario";
    }

    public void setQuestionarioProvider(QuestionarioProvider QuestionarioProvider) {
        this.QuestionarioProvider = QuestionarioProvider;
    }

    public QuestionarioProvider getQuestionarioProvider() {
        return QuestionarioProvider;
    }
    
    private RichPanelGroupLayout node;

    public void setNode(RichPanelGroupLayout node) {
        this.node = node;
    }

    public RichPanelGroupLayout getNode() {
        if(node==null)
            node = QuestionarioProvider.createNode(new RichPanelGroupLayout());
        return node;
    }
    
    public void refresh(){
        node = QuestionarioProvider.createNode(new RichPanelGroupLayout());
        //RequestContext.getCurrentInstance().addPartialTarget(node);
        FacesContext fc = FacesContext.getCurrentInstance();
        String refreshpage = fc.getViewRoot().getViewId();
        ViewHandler ViewH = fc.getApplication().getViewHandler();
        UIViewRoot UIV = ViewH.createView(fc,refreshpage);
        UIV.setViewId(refreshpage);
        fc.setViewRoot(UIV);
    }
    
    private RichForm frmRef;

    public void findForward() {

        Questionario questionario =
            (Questionario) BindingContext.getCurrent().findDataControl("QuestionarioDataControl").getDataProvider();

        // Cerco il referto
        ADFContext.getCurrent().getSessionScope().remove(AppConstants.FROM_PAGE);
       
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding invitiIter = bindings.findIteratorBinding("Ref_SoInvitiPerRefertiCAView1Iterator");

        // Recupero l'id referto dall'invito
        ViewObject inviti = invitiIter.getViewObject();
        Ref_SoInvitiPerRefertiCAViewRowImpl invitoRow = (Ref_SoInvitiPerRefertiCAViewRowImpl) inviti.getCurrentRow();
        Integer idReferto = invitoRow.getIdreferto();

        DCIteratorBinding refertiIter = bindings.findIteratorBinding("RefCa_SoRefertocardioView1Iterator");
        ViewObject refertoView = refertiIter.getViewObject();

        // Recupero l'id questionario dal referto, se esiste, altrimenti uso quello del questionario attivo
        RefCa_AppModule am = (RefCa_AppModule)refertoView.getApplicationModule();
        Integer idQuestionario;
        if (refertoView.first() != null) {
            RefCa_SoRefertocardioViewRowImpl refertoRow = (RefCa_SoRefertocardioViewRowImpl) refertoView.first();
            idQuestionario = refertoRow.getIdcnfquest();
        } else {
            RefCa_SoCnfQuestionarioViewImpl questionarioView =
                (RefCa_SoCnfQuestionarioViewImpl) am.findViewObject("RefCa_SoCnfQuestionarioAttivoView");
            RefCa_SoCnfQuestionarioViewRowImpl questionarioRow =
                (RefCa_SoCnfQuestionarioViewRowImpl) questionarioView.first();
            idQuestionario = questionarioRow.getIdcnfquest();
        }

        // Ad uso di getQuestionario()
        ViewObject cnfQuestionarioView = am.findViewObject("RefCa_SoCnfQuestionarioView1");
        cnfQuestionarioView.setWhereClauseParams(null);
        cnfQuestionarioView.setWhereClause("IDCNFQUEST = :1");
        cnfQuestionarioView.setWhereClauseParam(0, idQuestionario);
        cnfQuestionarioView.executeQuery();

        // Recupero le domande del questionario usando id referto, id questionario
        RefCa_SoDomandeQuestionarioViewImpl domandeView =
            (RefCa_SoDomandeQuestionarioViewImpl) am.findViewObject("RefCa_SoDomandeQuestionarioView1");
        domandeView.setWhereClause("(RefCa_SoCnfDomande.DT_FINE_VAL IS NULL OR RefCa_SoCnfDomande.DT_FINE_VAL > SYSDATE) AND RefCa_SoCnfQuestDomande.IDCNFQUEST = :1 AND RefCa_SoRefcardioDati.IDREFERTO (+) = :2");
        domandeView.setWhereClauseParams(null);
        domandeView.setWhereClauseParam(0, idQuestionario);
        domandeView.setWhereClauseParam(1, idReferto);
        domandeView.executeQuery();


        String sesso = invitoRow.getSesso();
        questionario.setSesso(sesso);

        RefCa_SoCnfQuestionarioViewRowImpl questionarioRow =
            (RefCa_SoCnfQuestionarioViewRowImpl) cnfQuestionarioView.first();
        questionario.setNome(questionarioRow.getDescrBreve());
        questionario.setDescrizione(questionarioRow.getDescrizione());

        // Popolo le domande    
        RefCa_SoDomandeQuestionarioViewRowImpl domandeRow =
            (RefCa_SoDomandeQuestionarioViewRowImpl) domandeView.first();
        if (domandeRow != null) {
            questionario.setDomande1liv(getDomande(domandeView));
        }

        // Determino se deve essere read-only
        if (invitoRow.getCompleto() != null && invitoRow.getCompleto().intValue() == 1) {
            questionario.setReadOnly(true);
        } else {
            questionario.setReadOnly(false);
        }

        // Dichiaro il questionario non modificato
        questionario.setDirty(false);

        // Filtro i medici rilevatori
        am.filtraMedici();

    }

    private List getDomande(ViewObject domandeView) {
        List domande = new ArrayList();
        RefCa_SoDomandeQuestionarioViewRowImpl domandeRow =
            (RefCa_SoDomandeQuestionarioViewRowImpl) domandeView.getCurrentRow();

        // Identifico assenza di padre con -1
        long idPadre = domandeRow.getCapostipite() == null ? -1 : domandeRow.getCapostipite().longValue();
        String sezione = domandeRow.getSezione();
        do {
            DomandaQuestionario domanda = new DomandaQuestionario();

            domanda.setTipo(domandeRow.getTipo());
            domanda.setId(domandeRow.getIdcnfdom().longValue());
            domanda.setSezione(domandeRow.getSezione());
            domanda.setTesto(domandeRow.getDomanda());
            domanda.setValore(domandeRow.getValore());
            domanda.setDimensione(domandeRow.getLunghezza() != null ? domandeRow.getLunghezza().intValue() : 0);
            domanda.setGruppo(domandeRow.getGruppo());
            domanda.setModificabile(domandeRow.getModificabile().intValue()==1);
            domanda.setObbligatorio(domandeRow.getObbligatorio().intValue()==1);
            domanda.setNote(domandeRow.getNote());
            domanda.setCapostipite(domandeRow.getCapostipite() != null ? domandeRow.getCapostipite().longValue() : 0);
            domanda.setCodice(domandeRow.getCodiceDom());
            domanda.setMaxValue(domandeRow.getMaxval());
            domanda.setMinValue(domandeRow.getMinval());

            // Leggo la domanda successiva
            domandeRow = (RefCa_SoDomandeQuestionarioViewRowImpl) domandeView.next();

            // Gestisco i valori multipli della stessa domanda
            while (domandeRow != null && domandeRow.getIdcnfdom().longValue() == domanda.getId() &&
                   domandeRow.getSezione().equals(domanda.getSezione())) {
                domanda.addValore(domandeRow.getValore());
                domandeRow = (RefCa_SoDomandeQuestionarioViewRowImpl) domandeView.next();
            }

            // Se la domanda e' figlia, carico le domande in modo ricorsivo
            if (domandeRow != null && domandeRow.getCapostipite() != null &&
                domandeRow.getCapostipite().longValue() == domanda.getId() &&
                domandeRow.getSezione().equals(domanda.getSezione())) {
                domanda.setDomande1liv(getDomande(domandeView));
                domandeRow = (RefCa_SoDomandeQuestionarioViewRowImpl) domandeView.getCurrentRow();
            }

            domande.add(domanda);
        } while (domandeRow != null &&
                 ((domandeRow.getCapostipite() == null && idPadre == -1) ||
                  (domandeRow.getCapostipite() != null && domandeRow.getCapostipite().longValue() == idPadre &&
                   domandeRow.getSezione().equals(sezione))));

        return domande;
    }

    public void setFrmRef(RichForm frmRef) {
        this.frmRef = frmRef;
    }

    public RichForm getFrmRef() {
        if (frmRef == null)
            findForward();

        return frmRef;
    }

    public void onChiudi(ClientEvent clientEvent) {
        onChiudi();
    }
}
