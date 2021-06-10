package view.soggetto;

import insiel.dataHandling.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;

import model.datacontrol.Sogg_NuovoParam;

import model.global.common.A_SoSoggScrViewRow;

import model.inviti.GeneratoreInviti;
import model.inviti.InvitoUtils;

import model.soggetto.common.Sogg_SoSoggettoViewRow;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichPopup;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.ApplicationModule;
import oracle.jbo.AttributeDef;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.StructureDef;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

@SuppressWarnings({ "deprecation", "unchecked" })
public class Sogg_NuovoAction extends Parent_DataForwardAction {
    
    private RichPopup classePopWarnPopup;

    public Sogg_NuovoAction() {
    }

    protected void setAppModule() {
        this.amName = "Sogg_AppModule";
    }

    public String onCrea() {
        try {
            String msg = InvitoUtils.creaInvito(false);
            if (msg != null)
                this.handleMessages(FacesMessage.SEVERITY_INFO, msg);
        } catch (Exception ex) {
            this.handleException("Impossibile controllare l'eventuale presenza di primi inviti/" +
                                 "accessi spontanei nello stesso round: " + ex.getMessage(), null);
        }

        return null;
    }

    public void onAggOrari(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        SoggUtils.filtraOrari(false);

        // Filtro i tipi invito in base alla data appuntamento
        Map sess = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvitoIter = bindings.findIteratorBinding("Sogg_SoInvitoView1Iterator");
        ViewObject invitoView = voInvitoIter.getViewObject();
        Row invitoRow = invitoView.getCurrentRow();
        Integer attivoCorrente = (Integer) invitoRow.getAttribute("Attivo");

        // Determino se esiste gia' un invito attivo
        Sogg_AppModule am = (Sogg_AppModule)invitoView.getApplicationModule();
        ViewObject soggView = am.findViewObject("Sogg_SoSoggettoView1");
        Sogg_SoSoggettoViewRow cSogg = (Sogg_SoSoggettoViewRow) soggView.getCurrentRow();
        String codts = cSogg.getCodts();
        boolean esisteAttivo = InvitoUtils.getInvitoAttivo(am, codts, 0, ulss, tpscr) != null;

        // filtro tipi invito
        ViewObject voTI = am.findViewObject("Sogg_TipoInvitoView1");
        String whcl;
        if (esisteAttivo || (attivoCorrente != null && attivoCorrente.intValue() == 1)) {
            whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
        } else {
            /* mauro 30-11-06: modifiche per includere anche accesso spontaneo */
            Integer codCatI = ConfigurationConstants.CODICE_CAT_PRIMO_INVITO;
            Integer codCatV = ConfigurationConstants.CODICE_CAT_VOLONTARIO;
            whcl =
                "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'" + " and IDCATEG in (" + codCatI.toString() + "," +
                codCatV.toString() + ")";
        }

        // Filtro per data fine validita
        Date dataApp = (Date) invitoRow.getAttribute("Dtapp");
        String strData;
        if (dataApp != null) {
            strData = dataApp.dateValue().toString();
        } else {
            strData = new java.sql.Date(new java.util.Date().getTime()).toString();
        }
        whcl += " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" + strData + "', 'YYYY-MM-DD'))";

        voTI.setWhereClause(whcl);
        voTI.executeQuery();
        
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:Idapp'])");
    }

    public void onTipoInvito(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voSoggettoIter = bindings.findIteratorBinding("Sogg_SoSoggettoView1Iterator");
        Row soggettoRow = voSoggettoIter.getCurrentRow();
        DCIteratorBinding voInvitoIter = bindings.findIteratorBinding("Sogg_SoInvitoView1Iterator");
        Row invitoRow = voInvitoIter.getCurrentRow();
        String ulss = (String) invitoRow.getAttribute("Ulss");
        String tpscr = (String) invitoRow.getAttribute("Tpscr");
        String codts = (String) invitoRow.getAttribute("Codts");
        String idtpinvito = (String) invitoRow.getAttribute("Idtpinvito");

        // Se non ho scelto nessun tipo invito non faccio nulla
        if (idtpinvito == null) {
            return;
        }

        boolean gestioneHPV = Boolean.TRUE.equals(ADFContext.getCurrent().getSessionScope().get("HPV"));

        // Recupero la configurazione del tipo invito
        Sogg_AppModule am = (Sogg_AppModule)voSoggettoIter.getViewObject().getApplicationModule();
        ViewObject tipoInvitoView = am.findViewObject("Sogg_TipoInvitoView1");
        Row tipoInvitoRow = null;
        for (tipoInvitoRow = tipoInvitoView.first(); tipoInvitoRow != null; tipoInvitoRow = tipoInvitoView.next()) {
            if (idtpinvito.equals(tipoInvitoRow.getAttribute("Idtpinvito"))) {
                break;
            }
        }
        Integer livello = (Integer) tipoInvitoRow.getAttribute("Livello");
        int hpv =
            tipoInvitoRow.getAttribute("Hpv") != null ? ((Integer) tipoInvitoRow.getAttribute("Hpv")).intValue() : -1;

        // Recupero l'azienda dell'invito
        ViewObject aziendaView = am.findViewObject("A_SoAziendaView1");
        Row aziendaRow = aziendaView.getCurrentRow();
        if (aziendaRow == null) {
            aziendaView.setWhereClause("CODAZ = '" + ulss + "'");
            aziendaView.executeQuery();
            aziendaRow = aziendaView.first();
        }

        int etaMin =
            aziendaRow.getAttribute("Etahpvmin") != null ? ((Integer) aziendaRow.getAttribute("Etahpvmin")).intValue() :
            0;
        int etaMax =
            aziendaRow.getAttribute("Etahpvmax") != null ? ((Integer) aziendaRow.getAttribute("Etahpvmax")).intValue() :
            999;

        java.util.Date dataNascita = ((Date) soggettoRow.getAttribute("DataNascita")).dateValue();

        // Recupero il round individuale HPV
        ViewObject soggScrView = am.findViewObject("Sogg_SoSoggScrView1");      
        Row soggScrRow = soggScrView.getCurrentRow();
        int roundIndivHpv = 0;
        if (soggScrRow != null) {
            Integer n = (Integer) soggScrRow.getAttribute("RoundindivHpv");
            if (n != null) {
                roundIndivHpv = n.intValue();
            }
        }

        // Valorizzo test proposto
        String testProposto =
            InvitoUtils.calcolaTestProposto(gestioneHPV, livello.intValue(), hpv, roundIndivHpv, dataNascita, etaMin,
                                            etaMax);
        invitoRow.setAttribute("TestProposto", testProposto);

        if (gestioneHPV) {
            invitoRow.setAttribute("Livello", livello);
        }
    }

    public void onTestProposto(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        // Sincronizzo il db con il valore selezionato dall'utente
        String testProposto =
            ADFContext.getCurrent().getRequestScope().get("testProposto") != null ?
            ADFContext.getCurrent().getRequestScope().get("testProposto").toString() : null;
        if (testProposto != null) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voInvitoIter = bindings.findIteratorBinding("Sogg_SoInvitoView1Iterator");
            Row invitoRow = voInvitoIter.getCurrentRow();
            if (testProposto.equals("")) {
                invitoRow.setAttribute("TestProposto", null);
            } else {
                invitoRow.setAttribute("TestProposto", testProposto);
            }
        }
    }

    public String onStampa(Date dtstampa) {
        if (dtstampa != null)
            return "print_invito";
        else
            return null;
    }

    public String onConf() {
        
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();

        Map session = adfCtx.getSessionScope();
        String tpscr = (String) session.get("scr");
        
        boolean checkClasseOk = true;
        
        //I00101237 - SCADENZA CATEGORIA POPOLAZIONE
        if ("CO".equals(tpscr)){
            boolean bypassCheck = adfCtx.getViewScope().containsKey("bypassCheckPop");
            
            if (!bypassCheck)
                checkClasseOk = this.checkClassePop(am, adfCtx);
            
            adfCtx.getViewScope().remove("bypassCheckPop");
        }
        
        if (checkClasseOk) {
            session.put("nuovoInvitoCheckDate", Boolean.TRUE);
            boolean opOK = this.save();
            session.put("nuovoInvitoCheckDate", Boolean.FALSE);
            if (opOK) {
                return "goRic";
            }
        }
        
        return null;
    }

    public String onAppl() {
        
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();

        Map session = adfCtx.getSessionScope();
        String tpscr = (String) session.get("scr");
        
        boolean checkClasseOk = true;
        
        //I00101237 - SCADENZA CATEGORIA POPOLAZIONE
        if ("CO".equals(tpscr)){
            boolean bypassCheck = adfCtx.getViewScope().containsKey("bypassCheckPop");
            
            if (!bypassCheck)
                checkClasseOk = this.checkClassePop(am, adfCtx);
            
            adfCtx.getViewScope().remove("bypassCheckPop");
        }
        
        if (checkClasseOk) {
            session.put("nuovoInvitoCheckDate", Boolean.TRUE);
            this.save();
            session.put("nuovoInvitoCheckDate", Boolean.FALSE);
            //20111012 serra
            SoggUtils.filtraOrari(false);
            //fine 20111012
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private boolean checkClassePop(Sogg_AppModule am, ADFContext adfCtx) {

        ViewObject soggscrView = am.findViewObject("Sogg_SoSoggScrView1");
        Row soggScr = soggscrView.getCurrentRow();
        
        if (soggScr!=null){
            String tpscr = (String) soggScr.getAttribute("Tpscr");
            
            Sogg_NuovoParam bean =
                (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
            String classePop = bean.getCodClassePop();
            
            if (classePop!=null && tpscr!=null){
                String query = "SELECT sccp.scadenza, sccp.descrizione FROM so_cnf_classe_pop sccp " + 
                "WHERE sccp.codclasse = '" + classePop + "' AND sccp.tpscr='" + tpscr + "'";     
                
                ViewObject vo = am.createViewObjectFromQueryStmt("", query);
                vo.setRangeSize(-1);
                vo.executeQuery();
                
                Row res = vo.first();
                
                if (res != null){
                    Date scad = (Date)res.getAttribute(0);
                    String descClassePop = res.getAttribute(1).toString();
                    
                    if(scad!=null){
                        java.util.Date scadenza = new java.util.Date(scad.dateValue().getTime());

                        if (scadenza!=null && scadenza.before(new java.util.Date())){
                            
                            adfCtx.getViewScope().put("descClassePop", descClassePop);
                            
                            if(this.classePopWarnPopup==null)
                                this.classePopWarnPopup = new RichPopup();
                            
                            this.classePopWarnPopup.show(new RichPopup.PopupHints());
                            return false;
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    public void doRollback() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voInvitoIter = bindings.findIteratorBinding("Sogg_SoInvitoView1Iterator");
        ViewObject voInvito = voInvitoIter.getViewObject();
        Row cInv = voInvito.getCurrentRow();
        Integer idInv = (Integer) cInv.getAttribute("Idinvito");
        String[] views = { "Sogg_RicercaView1" };
        Sogg_AppModule am = (Sogg_AppModule)voInvito.getApplicationModule();
        am.doRollback(views);
        SoggUtils.loadVoDet();

        Map sess = ADFContext.getCurrent().getSessionScope();
        boolean invAppeso = ((Boolean) sess.get("invitoAppeso")).booleanValue();

        if (invAppeso) {

            // mauro 10/02/2010
            voInvito.setWhereClause("1=2");
            voInvito.executeQuery();
            // mauro 10/02/2010, fine

            Row nInv = voInvito.createRow();
            StructureDef def = nInv.getStructureDef();
            AttributeDef[] attrs = def.getAttributeDefs();
            for (int j = 0, numAttrs = attrs.length - 1; j < numAttrs; j++) {
                if(attrs[j].getUpdateableFlag()==AttributeDef.UPDATEABLE)
                    nInv.setAttribute(j, cInv.getAttribute(j));
            }
            voInvito.insertRow(nInv);

            // mauro 10/02/2010
            voInvito.setCurrentRow(nInv);
            // mauro 10/02/2010, fine
        } else {
            voInvito.setWhereClause("IDINVITO = " + idInv.toString());
            voInvito.executeQuery();
        }
    }

    public void onReg() {
        Sogg_NuovoParam bean =
            (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
        int reg = bean.getRegLett().intValue();
        boolean disStp = (reg == 0);
        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.put("disStp", Boolean.valueOf(disStp));
        if (reg == 0) {
            bean.setStpLett(0);
        }
    }
    
    protected boolean beforeSave() {
        // controllo conferma cellulare
        if(!verificaConfermaCellulare())
            return false;
        
        Map sess = ADFContext.getCurrent().getSessionScope();

        // controllo che il soggetto non sia escluso
        boolean escl = ((Boolean) sess.get("esclSalvata")).booleanValue();
        if (escl) {
            String msg = "La persona risulta esclusa. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }
        
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        Row cRow = voInvito.getCurrentRow();

        // mauro 11/11/2010, verifica che la data sia impostata se l'aggioramento e' richiesto con
        // i pulsanti conferma o applica (deve essere comunque escluso il caso di navigazione
        // verso l'agenda)
        boolean checkDate = ((Boolean) sess.get("nuovoInvitoCheckDate")).booleanValue();

        Date dtAppu = (Date) cRow.getAttribute("Dtapp");
        if (checkDate) {
            Integer idAppu = (Integer) cRow.getAttribute("Idapp");
            if (dtAppu == null && idAppu == null) {
                String msg = "E' necessario impostare la data appuntamento. Impossibile aggiornare.";
                this.handleException(msg, null);
                return false;
            }
        }
        //

        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");
        GeneratoreInviti gen = new GeneratoreInviti(am, (String) cRow.getAttribute("Codts"), ulss, tpscr);

        Integer idCt = (Integer) cRow.getAttribute("Idcentroprelievo");
        // mauro 27-06: il centro prelievo deve essere valorizzato, per forza
        if (idCt == null) {
            String msg = "Il centro prelievo e' un dato obbligatorio. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }
        //

        String idTipo = (String) cRow.getAttribute("Idtpinvito");
        if (idTipo == null) {
            String msg = "Il tipo invito e' un dato obbligatorio. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }
        String ulssTipo = (String) cRow.getAttribute("Ulss");
        String tpscrTipo = (String) cRow.getAttribute("Tpscr");

        //I00101171 - CONGRUENZA TRA CENTRO DI REFERTAZIONE E TIPO DI INVITO
        if (tpscr.equals("CO")){
            String sql = "SELECT ct.idtpinvito FROM SO_CENTRO_PREL_REF cpr " +
                "JOIN SO_CNF_CENTROREF_TPINVITO ct ON cpr.idcentroref = ct.idcentroref " +
                "where cpr.idcentro = " + idCt + " AND cpr.tipocentro = 1";
            
            ViewObject vo = am.createViewObjectFromQueryStmt("", sql);
            vo.setRangeSize(-1);
            vo.executeQuery();
            
            if (vo.getEstimatedRowCount()>0){
                boolean found = false;
                
                RowSetIterator it = vo.createRowSetIterator(null);
                while (it.hasNext()) {
                    Row r = it.next();
                    String idTipoOk = (String) r.getAttribute(0);
                    
                    if (idTipoOk!=null && idTipoOk.equals(idTipo))
                        found = true; 
                }
                
                if (!found){
                    String msg = "Impossibile salvare: tipo invito e centro refertazione sono incompatibili.";
                    this.handleException(msg, null);
                    return false;
                }
            }
        }
        
        // Controllo la coerenza tra data appuntamento e tipo invito
        if (dtAppu != null) {
            // Recupero la configurazione del tipo invito
            ViewObject voTipoInvito = am.findViewObject("A_SoCnfTpinvitoView1");
            voTipoInvito.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND IDTPINVITO = '" +
                                        idTipo + "'");
            voTipoInvito.executeQuery();
            voTipoInvito.next();
            Row rowTipoInvito = voTipoInvito.getCurrentRow();
            Date dtFineValidita = (Date) rowTipoInvito.getAttribute("Dtfinevalidita");
            if (dtFineValidita != null && dtAppu.compareTo(dtFineValidita) > 0) {
                String msg = "Il tipo invito non e' valido alla data dell'appuntamento";
                this.handleException(msg, null);
                return false;
            }
        }

        boolean compatibili = SoggUtils.ctVStipo(idCt.intValue(), idTipo, ulssTipo, tpscrTipo, am);

        if (!compatibili) {
            String msg =
                "Il centro prelievo e il tipo di invito impostati " + "hanno livello diverso. Impossibile aggiornare.";
            this.handleException(msg, null);
            return false;
        }
        
        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
        if (tpscr.equals("CO") && (Boolean)sess.get("covid19")){
            try {
                Integer IdMotivo = (Integer) cRow.getAttribute("MotivoIdcnfref");
                String motivo_obbl =
                    ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                   ConfigurationConstants.motivo_obbl);
                
                if(motivo_obbl!=null && "S".equalsIgnoreCase(motivo_obbl) && IdMotivo==null){
                    String msg =
                        "Il motivo di esecuzione � obbligatorio.";
                    this.handleException(msg, null);
                    return false;
                }
                
            } catch (Exception e) {
            }
        }
        
        try {
            cRow.setAttribute("Attivo", new Integer(1));
            
            //I00102504 - Nel caso di Inviti Fast l'esito viene presettato
            Object codesitoinvito = cRow.getAttribute("Codesitoinvito");
            if(codesitoinvito==null || "".equals(codesitoinvito.toString()))
                cRow.setAttribute("Codesitoinvito", "?");

            String tipo = (String) cRow.getAttribute("Idtpinvito");
            Integer livello = gen.getLivello(tipo, ulss, tpscr);
            cRow.setAttribute("Livesito", livello);

            Integer cPrel = (Integer) cRow.getAttribute("Idcentroprelievo");
            Integer idCtRef = SoggUtils.getCtRef(am, cPrel.intValue());
            cRow.setAttribute("Idcentroref1liv", idCtRef);

            Date dtApp = (Date) cRow.getAttribute("Dtapp");
            Integer oraApp = (Integer) cRow.getAttribute("Oraapp");
            Integer minApp = (Integer) cRow.getAttribute("Minapp");

            //20110401 Serra: dtappiniziale va sempre compilato a partire dalla data appuntamento, anche in assenza di orario
            /* if (dtApp != null && oraApp != null && minApp != null)
                            {
                                    Date dtIni = DateUtils.getOracleDateTime(dtApp,oraApp.intValue(),minApp.intValue());
                                    cRow.setAttribute("Dtappiniziale",dtIni);
                            }
                            */
            if (dtApp != null) {
                Date dtIni = null;
                if (oraApp != null && minApp != null) {
                    Calendar c = Calendar.getInstance(Locale.ITALY);
                        c.clear();
                        c.setTime(dtApp.dateValue());
                        c.set(11, oraApp.intValue());
                        c.set(12, minApp.intValue());
                    dtIni = (new oracle.jbo.domain.Date(new java.sql.Timestamp(c.getTime().getTime())));                    
                } else
                    dtIni = dtApp;
                cRow.setAttribute("Dtappiniziale", dtIni);
            }
            //fine 20110401

            Integer idInvCorr = (Integer) cRow.getAttribute("Idinvito");
            String codTs = (String) cRow.getAttribute("Codts");
            Integer idInvAtt;
            boolean invSalvato = ((Boolean) sess.get("invitoSalvato")).booleanValue();
            if (invSalvato) {
                idInvAtt = (Integer) sess.get("idInvAtt");
            } else {
                idInvAtt = InvitoUtils.getInvitoAttivo(am, codTs, idInvCorr.intValue(), ulss, tpscr);
                sess.put("idInvAtt", idInvAtt);
            }

            Integer comId = gen.getComuneId(codTs, ulss);
            Integer roundC = gen.getRoundComune(ulss, tpscr, comId, tipo, idInvAtt);
            cRow.setAttribute("Roundcomune", roundC);

            ViewObject soggView = am.findViewObject("Sogg_SoSoggettoView1");
            Sogg_SoSoggettoViewRow cSogg = (Sogg_SoSoggettoViewRow) soggView.getCurrentRow();

            cRow.setAttribute("Codiceregmedico", cSogg.getCodiceregmedico());

            Sogg_NuovoParam beanScr =
                (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
            String classePop = beanScr.getCodClassePop();
            cRow.setAttribute("Codclassepop", classePop);

            String codRes = cSogg.getCodcomres();
            String codDom = cSogg.getCodcomdom();
            String viaRes = cSogg.getIndirizzoRes();
            String viaDom = cSogg.getIndirizzoDom();
            Integer statoAna = cSogg.getCodanagreg();
            Integer zona = cSogg.getCoddistrzona();
            Integer rcRes = cSogg.getReleaseCodeComRes();
            Integer rcDom = cSogg.getReleaseCodeComDom();

            cRow.setAttribute("Codresidenza", codRes);
            cRow.setAttribute("Coddomicilio", codDom);
            cRow.setAttribute("Viaresidenza", viaRes);
            cRow.setAttribute("Viadomicilio", viaDom);
            cRow.setAttribute("Statoanag", statoAna);
            if (zona != null) {
                cRow.setAttribute("Zona", new Integer(zona.intValue()));
            }
            cRow.setAttribute("ReleaseCodeComRes", rcRes);
            cRow.setAttribute("ReleaseCodeComDom", rcDom);
            
            Integer vol = gen.getVolontaria(ulss, tpscr, tipo, idInvAtt);
            cRow.setAttribute("Volontaria", vol);

            Date dtoraCorr = DateUtils.getOracleDateNow();
            String user = (String) sess.get("user");

            cRow.setAttribute("Dtultimamod", dtoraCorr);
            cRow.setAttribute("Dtcreazione", dtoraCorr);
            cRow.setAttribute("Opinserimento", user);
            cRow.setAttribute("Opmodifica", user);
            
            Integer ff = gen.getFuorifascia(tpscr, codTs, ulss, new java.util.Date(), idInvAtt);
            cRow.setAttribute("Fuorifascia", ff);

            cRow.setAttribute("Livello", livello);
            
            if (idInvAtt != null) {
                gen.updateInvitoAttivo(idInvAtt, user);
            }

            // gestione dei codici progressivi
            Date dtInvito = (Date) cRow.getAttribute("Dtapp");
            Row accRow = null;
            if (tpscr.equals("CO")) {
                accRow =
                    InvitoUtils.gesAccColon(am, idInvCorr.intValue(), dtInvito, livello.intValue(), idCtRef, user, ulss, tpscr);
            }

            if (tpscr.equals("MA")) {
                int liv = livello.intValue();
                String prodNrich = "";
                String prodNrich2 = "";

                //ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
                if (liv == 1)
                    prodNrich =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
                if (liv == 2)
                    prodNrich2 =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA2);
                if ((liv == 1 && "S".equals(prodNrich)) || (liv == 2 && "S".equals(prodNrich2))) {
                    accRow = InvitoUtils.gesAccMammo(am, idInvCorr.intValue(), dtInvito, liv, ulss, idCtRef, user, tpscr);
                }
            }
            String testProposto = null;
            // mauro 6-10-2008, trattato anche cito per hvp
            // 27122013 Gaion: cod_richiesta per 2 liv
            if (tpscr.equals("CI")) {
                //gaion 07/06/2011
                Boolean hpv = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
                if (hpv != null && hpv.booleanValue()) {
                    testProposto = (String) cRow.getAttribute("TestProposto");
                    // Se il test proposto non e' HPV, imposto a false il flag
                    if (!"HPV".equals(testProposto)) {
                        hpv = Boolean.FALSE;
                    }
                }
                accRow =
                    InvitoUtils.gesAccCito(am, idInvCorr.intValue(), dtInvito, livello.intValue(), idCtRef, user, ulss, tpscr, hpv,
                                           false, testProposto);
            }
            //
            //20170126 Serra: cod richiesta e campione per il PFAS
            if (tpscr.equals("PF")) {

                accRow =
                    InvitoUtils.gesAccPfas(am, idInvCorr.intValue(), dtInvito, livello.intValue(), idCtRef, user, ulss, tpscr);
            }

            Sogg_NuovoParam bean =
                (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
            int reg = bean.getRegLett().intValue();
            if (reg == 1) {
                Integer idApp = (Integer) cRow.getAttribute("Idapp");
                if (idApp == null) {
                    this.doRollback();
                    String msg =
                        "Non e' possibile registrare la lettera " + "perche' non esiste un appuntamento. " +
                        "L'aggiornamento e' fallito.";
                    this.handleMessages(FacesMessage.SEVERITY_ERROR, msg);
                    return false;
                } else {

                    //controllo se c'e' gia' una lettera
                    ViewObject allegati = am.findViewObject("Sogg_StInvitiAllegView2");
                    Row all = allegati.first();
                    if (all != null)
                        gen.deleteLettera(((Integer) all.getAttribute("Idallegato")).intValue());

                    //trovo la tessera sanitaria
                    String tesseraSanitaria = SoggUtils.trovaTessera(am, codTs, ulss);

                    String otp = (String) cRow.getAttribute("Otp");

                    if (accRow != null) {
                        BigDecimal codCampione = null;
                        try {
                            if (tpscr.equals("CI")) {
                                codCampione = (BigDecimal) accRow.getAttribute("Numvetrino");
                            } else {
                                codCampione = (BigDecimal) accRow.getAttribute("CodCampione");
                            }
                        } catch (Exception e) {
                            //non faccio niente
                            //vuol dire che non c'e' codice campione
                        }

                        gen.creaLettera(ulss, tpscr, tipo, codTs, idApp.intValue(), idInvCorr.intValue(), testProposto,
                                        tesseraSanitaria, otp, ((BigDecimal) accRow.getAttribute("CodRichiesta")),
                                        codCampione);
                    } else {
                        gen.creaLettera(ulss, tpscr, tipo, codTs, idApp.intValue(), idInvCorr.intValue(), testProposto, tesseraSanitaria,
                                        otp);
                    }

                    bean.setRegLett(0);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = ex.getMessage();
            this.handleMessages(FacesMessage.SEVERITY_ERROR, msg);
            this.doRollback();
            return false;
        } finally {
            //it.closeRowSetIterator();
        }
        
        return true;
    }

    protected String afterSave() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.put("invitoAppeso", Boolean.valueOf(false));
        sess.put("invitoSalvato", Boolean.valueOf(true));

        sess.put("invitoPresente", Boolean.valueOf(true));

        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();    
        ViewObject voAllegato = am.findViewObject("Sogg_StInvitiAllegView2");      
        voAllegato.executeQuery();
        //aggiorno il numero round
        try{
            String tpscr = (String) sess.get("scr");
            String ulss = (String) sess.get("ulss");
            Integer idInvAtt = (Integer) sess.get("idInvAtt");
            ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
            Row cRow = voInvito.getCurrentRow();
            String codTs = (String) cRow.getAttribute("Codts");
            String tipo = (String) cRow.getAttribute("Idtpinvito");
            GeneratoreInviti gen = new GeneratoreInviti(am, (String) cRow.getAttribute("Codts"), ulss, tpscr);
            A_SoSoggScrViewRow soggscr = gen.insertIntoScreeningIfNecessary(tpscr, ulss, codTs, idInvAtt);
            gen.updateRoundIndiv(soggscr, cRow);
            Integer roundInd = gen.getRoundIndividuale(soggscr);
            Integer roundInv = gen.updateAndGetRoundInviti( //tpscr, ulss,
                                                           soggscr, tipo, ((Integer) cRow.getAttribute("Idinvito")).intValue());
            cRow.setAttribute("Roundinviti", roundInv);
            cRow.setAttribute("Roundindiv", roundInd);
            
            am.getTransaction().postChanges();
            
            Sogg_NuovoParam bean =
                (Sogg_NuovoParam) BindingContext.getCurrent().findDataControl("Sogg_NuovoParamDataControl").getDataProvider();
            String classePop = bean.getCodClassePop();
            if (classePop != null){
                String upStmt = " UPDATE SO_SOGG_SCR SET CODCLASSEPOP = '"+classePop+ "' "+
                    " WHERE CODTS = '"+codTs+"' AND ULSS = '"+ulss+"' AND TPSCR = '"+tpscr+"' ";
    
                am.getTransaction().executeCommand(upStmt);
            }
            
            if (tpscr.equals("CO") && (Boolean)sess.get("covid19")){
                DCBindingContainer bindings = (DCBindingContainer) getBindings();
                DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoInvitoView1Iterator");
                
                if(voIter!=null){
                    Row _r = voIter.getCurrentRow();
                    if(_r!=null){
                        Integer MotivoidCnfRef = (Integer) _r.getAttribute("MotivoIdcnfref");
                        String MotivoGruppo = (String) _r.getAttribute("MotivoGruppo");
                        Integer IdMotivo = (Integer) _r.getAttribute("MotivoId");
                        
                        // se IdMotivo == null --> che non � presente il dato sulla tabella SO_INV_MOTIVO (INSERT SE MotivoidCnfRef <> null)
                        // se IdMotivo <> null --> che � presente il dato sulla tabella SO_INV_MOTIVO (DELETE SE MotivoidCnfRef == null UPDATE MotivoidCnfRef <> null)
                        String stm = null;
                        if(IdMotivo==null && MotivoidCnfRef!=null)
                            stm = " INSERT INTO SO_INV_MOTIVO (ID , IDCNFREF , IDINVITO , DTCREAZIONE, DTULTIMAMOD , GRUPPO , NOTE , OPINSERIMENTO , OPMODIFICA , TPSCR , ULSS) VALUES" +
                                  "(so_inv_motivo_seq.nextVal, " + MotivoidCnfRef + ", " + cRow.getAttribute("Idinvito") + ", sysdate, sysdate, '" + MotivoGruppo + "', null, '" 
                                  + sess.get("user") + "', '" + sess.get("user") + "', '" + tpscr + "', '" + ulss + "')";
                        else if(IdMotivo!=null && MotivoidCnfRef!=null && IdMotivo.intValue()!=MotivoidCnfRef.intValue())
                            stm = " UPDATE SO_INV_MOTIVO SET IDCNFREF = " + MotivoidCnfRef +
                                  ", GRUPPO = '" + MotivoGruppo + "' " +
                                  ", OPMODIFICA = '" + sess.get("user") + "' " +
                                  ", DTULTIMAMOD = sysdate" +
                                  " WHERE ID = " + IdMotivo;
                        else if(IdMotivo!=null && MotivoidCnfRef==null)
                            stm = " DELETE FROM SO_INV_MOTIVO " +
                                  " WHERE ID = " + IdMotivo;
                        
                        if(stm!=null)
                            am.getTransaction().executeCommand(stm);
                    }
                }
            }

            
            am.getTransaction().commit();
            
            if (tpscr.equals("CO") && (Boolean)sess.get("covid19")){
                String sql = "select id from so_inv_motivo i where i.idinvito = " + ((Integer) cRow.getAttribute("Idinvito")).intValue();
                ViewObject voCount = am.createViewObjectFromQueryStmt("", sql);
                voCount.setRangeSize(-1);
                voCount.executeQuery();

                if(voCount.getDeferredEstimatedRowCount()>0){
                    Row fRow = voCount.first();
                    Number cnt = (Number) fRow.getAttribute(0);
    
                    DCBindingContainer bindings = (DCBindingContainer) getBindings();
                    DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoInvitoView1Iterator");
                    
                    if(voIter!=null){
                        Row _r = voIter.getCurrentRow();
                        if(_r!=null){
                             _r.setAttribute("MotivoId", cnt.intValue());
                        } 
                    }
                }
            }
            
            ViewObject voSoggScr = am.findViewObject("Sogg_SoSoggScrView1");
            voSoggScr.executeQuery();
            voSoggScr.setCurrentRow(voSoggScr.first());
        } catch (Exception ex){
            ex.printStackTrace();
            String msg = ex.getMessage();
            this.handleMessages(FacesMessage.SEVERITY_ERROR, msg);
        }
            
        return null;
    }

    protected void beforeNavigate(String dest) throws Exception {
        SoggUtils.beforeNavSogg(dest, false);
    }
    
    public void setClassePopWarnPopup(RichPopup classePopWarnPopup) {
        this.classePopWarnPopup = classePopWarnPopup;
    }

    public RichPopup getClassePopWarnPopup() {
        return classePopWarnPopup;
    }
}
