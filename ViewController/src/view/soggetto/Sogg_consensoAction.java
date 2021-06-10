package view.soggetto;

import insiel.dataHandling.DateUtils;

import java.text.SimpleDateFormat;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.Acc_AppModule;
import model.Sogg_AppModuleImpl;

import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.Sogg_ConsensoParam;

import model.soggetto.Sogg_SoStoricoConsensoViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.commons.AccUtils;
import view.commons.action.Parent_DataForwardAction;

@SuppressWarnings({ "deprecation", "unchecked", "unused" })
public class Sogg_consensoAction extends Parent_DataForwardAction {
    String msgEsclusioni;
    String msgPrecedenze;

    protected void setAppModule() {
        this.amName = "Sogg_AppModule";
    }

    protected void beforeNavigate(String dest) throws Exception {

        SoggUtils.beforeNavSogg(dest, false);
    }

    public Sogg_consensoAction() {
    }

    public void onUpdateConsenso(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        //Se il consenso generale ha valore NO, anche il consenso alla condivisione prendera' valore No
        //solo per il citologico
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voConsenso = am.findViewObject("Sogg_SoSoggScrView1");
        Row cSogg = voConsenso.getCurrentRow();

        Map sess = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) sess.get("scr");

        Integer newConsenso = (Integer) cSogg.getAttribute("Consenso");

        if ((tpscr.equals("CI") || tpscr.equals("PF")) && newConsenso.intValue() == 1) //NO
        {
            cSogg.setAttribute("ConsensoCond", new Integer(1));
        } else if ((tpscr.equals("CI") || tpscr.equals("PF")) && newConsenso.intValue() == 0) { //dato non disponibile
            cSogg.setAttribute("ConsensoCond", new Integer(0));
        }
    }

    public String onSave() {
        msgEsclusioni = "";
        msgPrecedenze = "";
        Map sess = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");
        Boolean esclSalvata = (Boolean) sess.get("esclSalvata");

        Sogg_AppModuleImpl am =
            (Sogg_AppModuleImpl) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voConsenso = am.findViewObject("Sogg_SoSoggScrView1");
        Row cSogg = voConsenso.getCurrentRow();
        String codts = (String) cSogg.getAttribute("Codts");
        Integer newConsenso = (Integer) cSogg.getAttribute("Consenso");

        this.updateStorico(newConsenso, tpscr, codts, "GEN");

        ViewObject voParam = am.getA_SoCnfParametriSistemaView1();
        String codEscl = null;
        try {
            codEscl = ParametriSistema.getParamValue(voParam, ulss, tpscr, "consenso_escl");
            if (newConsenso.intValue() == 1) //NO
            {
                if (this.inserisciEsclusione(tpscr, codts, codEscl)) {
                    esclSalvata = Boolean.TRUE;
                    // per accettazione
                    sess.put("soggEscluso", Boolean.valueOf(true));
                }
            } else //si o nd
            {
                if (this.annullaEsclusione(tpscr, codts, codEscl)) {
                    // Se ho annullato l'esclusione, deduco che adesso non c'e' piu' nessuna esclusione
                    esclSalvata = Boolean.FALSE;
                    // per accettazione
                    sess.put("soggEscluso", Boolean.valueOf(false));
                } else {
                    // Se non ho annullato l'esclusione e' perche' non l'ho trovata in quanto ne esiste gia' un'altra
                    esclSalvata = Boolean.TRUE;
                }
            }

        } catch (Exception e) {
            this.handleMessages(FacesMessage.SEVERITY_ERROR, "Esclusioni non inserite perche' mancano configurazioni");
        }

        Sogg_ConsensoParam bean =
            (Sogg_ConsensoParam) BindingContext.getCurrent().findDataControl("Sogg_ConsensoParamDataControl").getDataProvider();

        if (tpscr.equals("CI")) {
            //propago su mammo e colon
            if (bean.getPropagaCO()) {
                Boolean colon = this.propagaConsenso("CO", codts, newConsenso);
            }
            if (bean.getPropagaMA()) {
                Boolean mammo = this.propagaConsenso("MA", codts, newConsenso);
            }
        }

        if (tpscr.equals("MA")) {
            //propago su cito e colon
            if (bean.getPropagaCI()) {
                Boolean cito = this.propagaConsenso("CI", codts, newConsenso);
            }
            if (bean.getPropagaCO()) {
                Boolean colon = this.propagaConsenso("CO", codts, newConsenso);
            }
        }

        if (tpscr.equals("CO")) {
            //propago su mammo e cito
            if (bean.getPropagaCI()) {
                Boolean cito = this.propagaConsenso("CI", codts, newConsenso);
            }
            if (bean.getPropagaMA()) {
                Boolean mammo = this.propagaConsenso("MA", codts, newConsenso);
            }
        }

        if (tpscr.equals("CI") || tpscr.equals("PF")) {
            //aggiorno il consenso di condivisione
            Integer newConsensoCond = (Integer) cSogg.getAttribute("ConsensoCond");
            this.updateStorico(newConsensoCond, tpscr, codts, "COND");
        }

        this.handleMessages(FacesMessage.SEVERITY_INFO,
                            "Consenso correttamente salvato. " + msgEsclusioni + msgPrecedenze);

        this.save();

        //refresh del vo
        voConsenso.executeQuery();

        //refresh del vo per esclusioni
        ViewObject voEscl = am.findViewObject("Sogg_SoEsclusioneView1");
        voEscl.executeQuery();
        Row cEscl = voEscl.first();
        boolean eEscl = (cEscl != null);
        ViewObject voEsclColleg = am.findViewObject("Sogg_EsclusioniCollegateView1");
        voEsclColleg.setWhereClause("1=2");
        voEsclColleg.executeQuery();

        sess.put("esisteEscl", Boolean.valueOf(eEscl));
        sess.put("esclSalvata", Boolean.valueOf(eEscl));

        if (eEscl) {

            Integer idEscl = (Integer) cEscl.getAttribute("Idescl");
            String tipo = (String) cEscl.getAttribute("Tpescl");

            SoggUtils.filtraMotEscl(tipo, false);

            sess.put("esclAppesa", Boolean.valueOf(false));

            SoggUtils.svuotaInterfacciaPropagazione();
        }

        //sess.setAttribute("backConsensoOK",Boolean.TRUE);
        //sess.removeAttribute("backConsensoOK");
        Boolean flag = (Boolean) sess.get("LINK_ACC_INV");
        if (flag != null && flag.booleanValue() &&
            tpscr.equals("CI")) {
            //torno all'accettazione

            //refresh del vo
            Acc_AppModule amAcc =
            (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("Acc_SoInvitoView1");
            voInvito.executeQuery();
            voInvito.setCurrentRow(voInvito.first());

            to_acc_query();
            
            sess.remove("LINK_ACC_INV");
            //sess.remove("showTabs");

            return "goEsitoCI";
        } else {
            //torno all'anagrafica
            return "back";
        }
    }

    public String onCancel() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ViewObject voTest = am.findViewObject("Sogg_SoInvitoView1");
        if (voTest.getWhereClause() == null) // se non e' stato inizializzato
        {
            voTest.setWhereClause("1=2");
        }

        String[] views = { "Sogg_RicercaView1", "Sogg_SoSoggettoView1", "Sogg_SoInvitoView1", "Sogg_SoSoggScrView1" };
        am.doRollback(views);

        Map sess = ADFContext.getCurrent().getSessionScope();
        Boolean flag = (Boolean) sess.get("LINK_ACC_INV");
        if (flag != null &&
            flag.booleanValue()) {
            //torno all'accettazione

            //refresh del vo
            Acc_AppModule amAcc =
(Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("Acc_SoInvitoView1");
            voInvito.executeQuery();
            voInvito.setCurrentRow(voInvito.first());
            
            to_acc_query();
                
            sess.remove("LINK_ACC_INV");
            //sess.remove("showTabs");

            return "goEsitoCI";
        } else {
            //torno all'anagrafica
            return "back";
        }
    }

    public String onClose() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.remove("backConsensoOK");
        Boolean flag = (Boolean) sess.get("LINK_ACC_INV");
        if (flag != null &&
            flag.booleanValue()) {
            //torno all'accettazione

            //refresh del vo
            Acc_AppModule amAcc =
            (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("Acc_SoInvitoView1");
            voInvito.executeQuery();
            voInvito.setCurrentRow(voInvito.first());

            to_acc_query();

            sess.remove("LINK_ACC_INV");
            //sess.remove("showTabs");

            return "goEsitoCI";
        } else {
            //torno all'anagrafica
            return "back";
        }
    }
    
    private void to_acc_query(){
        //200911 Gaion mantiene la selezione nella selezione multipla
        Acc_AppModule am = (Acc_AppModule) BindingContext.getCurrent().
          findDataControl("Acc_AppModuleDataControl").getDataProvider();

        AccUtils.requeryElenco(am);

        ViewObject voRic = am.findViewObject("Acc_RicInvitiView1");
        Row cInv = voRic.getCurrentRow();
        if (cInv != null)
          cInv.setAttribute("Selezionato",Boolean.TRUE);
        // fine Gaion
        
        //filtro gli esisti
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String tpscr = (String) sess.get("scr");
        String ulss = (String) sess.get("ulss");  
        ViewObject voEsiti = am.findViewObject("Acc_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND LIVESITO = " + cInv.getAttribute("Livello"));
        voEsiti.executeQuery();
    }

    private boolean updateStorico(Integer newConsenso, String tpscr, String codts, String tipo) {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");

        Date dtoraCorr = DateUtils.getOracleDateNow();
        String user = (String) sess.get("user");
        Sogg_AppModuleImpl am =
            (Sogg_AppModuleImpl) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voStorico = am.findViewObject("Sogg_SoStoricoConsensoView1");
        voStorico.setWhereClause("ulss = '" + ulss + "' AND tpscr = '" + tpscr + "' AND codts = '" + codts +
                                 "' AND tipo = '" + tipo + "' AND fine_val is null ");
        voStorico.executeQuery();
        if (voStorico.getRowCount() > 0) {
            Sogg_SoStoricoConsensoViewRowImpl lastRecord = (Sogg_SoStoricoConsensoViewRowImpl) voStorico.first();
            Integer lastConsenso = (Integer) lastRecord.getAttribute("Valore");
            //se i valore del consenso prima e dopo sono diversi proseguo altrimenti non faccio niente
            if (newConsenso.intValue() != lastConsenso.intValue()) {
                //metto la data di fine valore al record vecchio
                lastRecord.setFineVal(dtoraCorr);
                lastRecord.setOpMod(user);
            } else {
                return false;
            }
        }
        //inserisco un record per il nuovo valore
        Sogg_SoStoricoConsensoViewRowImpl nRecord = (Sogg_SoStoricoConsensoViewRowImpl) voStorico.createRow();
        nRecord.setIdstorico(am.getNextIdStoricoConsenso());
        nRecord.setUlss(ulss);
        nRecord.setTpscr(tpscr);
        nRecord.setCodts(codts);
        nRecord.setTipo(tipo);
        nRecord.setInizioVal(dtoraCorr);
        nRecord.setOpIns(user);
        nRecord.setValore(newConsenso);
        voStorico.insertRow(nRecord);

        return true;

    }

    private boolean inserisciEsclusione(String tpscr, String codts, String codEscl) throws Exception {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String user = (String) sess.get("user");
        Date dtoraCorr = DateUtils.getOracleDateNow();
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        //trova la configurazione per l'esclusione da inserire
        ViewObject voEsclProp = voEsclProp = am.findViewObject("Cnf_EsclProposteView1");
        String wh = "idcnfescl = " + codEscl + " and ulss = '" + ulss + "' and tpscr = '" + tpscr + "' ";
        voEsclProp.setWhereClause(wh);
        voEsclProp.executeQuery();

        if (voEsclProp.getRowCount() > 0) {
            Row esclProp = voEsclProp.first();
            //cerco esclusioni gia' presenti
            String qryEsclPres = "SELECT a.idescl, a.idinvito, c.tpescl, c.predominante, a.dtescl+c.numgiorni, ";
            qryEsclPres +=
                "case when a.dtannull is null and (c.tpescl = 'D' or (c.tpescl = 'T' and a.dtescl + c.numgiorni >= trunc(SYSDATE))) then 1  else 0 end as attiva";
            qryEsclPres += " FROM so_esclusione a, so_cnf_esclusione c";
            qryEsclPres += " where c.idcnfescl = a.idcnfescl";
            qryEsclPres += " and c.ulss = a.ulss";
            qryEsclPres += " and c.tpscr = a.tpscr";
            qryEsclPres += " and a.ultima = 1";
            qryEsclPres += " and a.tpscr = '" + tpscr + "'";
            qryEsclPres += " and a.codts = '" + codts + "'";
            qryEsclPres += " and a.ulss = '" + ulss + "'";

            ViewObject voEsclPres = am.createViewObjectFromQueryStmt("", qryEsclPres);
            voEsclPres.setRangeSize(-1);
            voEsclPres.executeQuery();

            int count = voEsclPres.getRowCount();

            boolean inserisci = false;

            if (count == 0) {
                inserisci = true;
            } else {
                Row esclPres = voEsclPres.first();
                // vedere se esclusione e' attiva o meno
                Integer esclAttiva =  esclPres.getAttribute(5) !=null ? ((oracle.jbo.domain.Number)  esclPres.getAttribute(5)).intValue() : null;
                //data fine validita' eclusione presente
                Date fineescl_pres = (Date) esclPres.getAttribute(4);
                // se e' definitiva la data deve essere sempr emaggiore
                if (fineescl_pres == null)
                    fineescl_pres = DateUtils.getOracleDate(new SimpleDateFormat("dd/mm/yyyy").parse("31/12/2999"));
                //data fine validita' eclusione inserita
                Date fineescl_ins = null;
                if (esclProp.getAttribute("Numgiorni") != null) {
                    fineescl_ins =
                        (Date) dtoraCorr.addJulianDays(((Integer) esclProp.getAttribute("Numgiorni")).intValue(), 0);
                }
                //se e' definitiva la data deve essere sempr emaggiore
                else {
                    fineescl_ins = DateUtils.getOracleDate(new SimpleDateFormat("dd/mm/yyyy").parse("31/12/2999"));
                }

                if (esclAttiva.intValue() == 0) {
                    //se la fine validita' della nuova esclusione e' piu' vecchia di quella gia' inserita non faccio niente
                    //se l'esclusione precedente era definitiva ed e' stata annullata non confronto le date di fine esclusione
                    if ((esclPres.getAttribute(2)).equals("D") || fineescl_pres.compareTo(fineescl_ins) < 0) {
                        // posso inserire ma prima devo dire che l'esclusione non e' 'ultima'
                        inserisci = true;
                        Integer esclPresID = esclPres.getAttribute(0) !=null ? ((oracle.jbo.domain.Number)  esclPres.getAttribute(0)).intValue() : null; 
                        String updateEsclPres =
                            "update so_esclusione set ULTIMA = 0, " + "DTMODESCL = sysdate, OPMODIFICA = '" + user +
                            "' " + "where IDESCL = " + esclPresID.intValue();
                        am.getTransaction().executeCommand(updateEsclPres);
                    }
                } else {

                    String esclPresTipo = (String) esclPres.getAttribute(2);
                    Integer esclPresPred =   esclPres.getAttribute(3) !=null ? ((oracle.jbo.domain.Number)  esclPres.getAttribute(3)).intValue() : null; 

                    String esclPropTipo = (String) esclProp.getAttribute("TipoEscl");
                    Integer esclPropPred = esclProp.getAttribute("Predominante") !=null ? ((oracle.jbo.domain.Number) esclProp.getAttribute("Predominante")).intValue() : null; 

                    int esitoConfronto =
                        SoggUtils.confrontaEsclusioni(esclPresTipo, esclPresPred, esclPropTipo, esclPropPred,
                                                      fineescl_pres, fineescl_ins);

                    if (esitoConfronto < 0) {
                        // posso inserire
                        inserisci = true;
                        Integer esclPresID = esclPres.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) esclPres.getAttribute(0)).intValue() : null; 

                        String updateEsclPres =
                            "update so_esclusione set DTANNULL = sysdate, ULTIMA = 0, " +
                            "DTMODESCL = sysdate, OPMODIFICA = '" + user + "' " + "where IDESCL = " +
                            esclPresID.intValue();
                        am.getTransaction().executeCommand(updateEsclPres);
                    }

                }

            }

            if (inserisci) {

                Integer esclPropID = new Integer(Integer.parseInt(codEscl));
                String dataEscl = DateUtils.dateToString(dtoraCorr.dateValue());

                Integer idInvito = SoggUtils.getInvitoAttivo(am, codts, ulss, tpscr);
                String valInvito = (idInvito == null ? "to_number(null)" : idInvito.toString());

                Integer round = SoggUtils.getRoundComuneSogg(am, codts, ulss, tpscr);
                String valRound = (round == null ? "to_number(null)" : round.toString());

                Integer rIndiv = SoggUtils.getRoundIndiv(am, codts, ulss, tpscr);
                String valRInd = (rIndiv == null ? "to_number(null)" : rIndiv.toString());

                String insEsclProp =
                    "insert into SO_ESCLUSIONE values " + "(SO_ESCLUSIONE_SEQ.NEXTVAL, " + esclPropID.intValue() +
                    ", " + valInvito + ", " + "'" + codts + "', " + "to_date('" + dataEscl + "','dd/mm/yyyy'), " +
                    "1, " + "to_date(null), " + "to_number(null), " + valRound + ", " + valRInd + ", " +
                    "to_date(null), " + "sysdate, " + "null, " + "'" + ulss + "', " + "'" + tpscr + "', " +
                    "sysdate, " + "'" + user + "', " + "'" + user + "', null )";

                am.getTransaction().executeCommand(insEsclProp);

                msgEsclusioni = msgEsclusioni + " Esclusione inserita per: " + tpscr;
                return true;

            } else {
                msgPrecedenze = msgPrecedenze + " Esclusione non inserita per regola di precedenza: " + tpscr;
                return false;
            }

        } else {
            throw new Exception("Non e' stato trovata nessuna configurazione per l'esclusione " + codEscl);
        }

    }

    private boolean annullaEsclusione(String tpscr, String codts, String codEscl) {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String user = (String) sess.get("user");
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        String qryEsclPres = "SELECT a.idescl, a.idinvito, c.tpescl, c.predominante, a.dtescl+c.numgiorni ";
        qryEsclPres += " FROM so_esclusione a, so_cnf_esclusione c";
        qryEsclPres += " where c.idcnfescl = a.idcnfescl";
        qryEsclPres += " and c.ulss = a.ulss";
        qryEsclPres += " and c.tpscr = a.tpscr";
        qryEsclPres += " and a.ultima = 1";
        qryEsclPres += " and a.dtannull is null";
        qryEsclPres += " and a.tpscr = '" + tpscr + "'";
        qryEsclPres += " and a.codts = '" + codts + "'";
        qryEsclPres += " and a.ulss = '" + ulss + "'";
        qryEsclPres += " and a.idcnfescl = '" + codEscl + "'";

        ViewObject voEsclPres = am.createViewObjectFromQueryStmt("", qryEsclPres);
        voEsclPres.setRangeSize(-1);
        voEsclPres.executeQuery();

        Row esclPres = voEsclPres.first();
        if (esclPres != null) {
            try {
                //annullo l'esclusione corrente
                Integer esclPresID = esclPres.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) esclPres.getAttribute(0)).intValue() : null;

                String updateEsclPres =
                    "update so_esclusione set DTANNULL = sysdate, " + "DTMODESCL = sysdate, OPMODIFICA = '" + user +
                    "' " + "where IDESCL = " + esclPresID.intValue();
                am.getTransaction().executeCommand(updateEsclPres);

                boolean esito = this.save();

            } catch (Exception ex) {
                this.doRollback();
                this.handleException(ex.getMessage(), null);
            }

            msgEsclusioni = msgEsclusioni + " Esclusione annullata per: " + tpscr;
            return true;

        }

        else {
            msgEsclusioni = msgEsclusioni + " Nessuna esclusione da annullare per: " + tpscr;
            return false;
        }
    }

    private Boolean propagaConsenso(String tpscr, String codts, Integer newConsenso) {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        Sogg_AppModuleImpl am =
            (Sogg_AppModuleImpl) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Sogg_SoSoggScrView2");
        vo.setWhereClause("Sogg_SoSoggScr.ULSS='" + ulss + "' AND Sogg_SoSoggScr.CODTS='" + codts +
                          "' AND Sogg_SoSoggScr.TPSCR = '" + tpscr + "'");
        vo.executeQuery();
        Row r = vo.first();
        //se non esiste il record lo inserisco
        if (r == null) {
            r = vo.createRow();
            vo.insertRow(r);
            r.setAttribute("Codts", codts);
            r.setAttribute("Roundindiv", new Integer(0));
            r.setAttribute("Roundinviti", new Integer(0));
            r.setAttribute("Altorischio", ViewHelper.decodeByTpscr(tpscr, null, ConfigurationConstants.DB_FALSE, //altorischio e' valorizzato solo pe il colon
                                                                   null, null, null));
            r.setAttribute("Consenso", new Integer(0));
            r.setAttribute("ConsensoCond", new Integer(0));
            r.setAttribute("VaccinatoHpv", new Integer(0));
            r.setAttribute("Tpscr", tpscr);
            r.setAttribute("Ulss", ulss);
        }
        r.setAttribute("Consenso", newConsenso);
        this.updateStorico(newConsenso, tpscr, codts, "GEN");

        ViewObject voParam = am.getA_SoCnfParametriSistemaView1();
        try {
            String codEscl = ParametriSistema.getParamValue(voParam, ulss, tpscr, "consenso_escl");
            if (newConsenso.intValue() == 1) //NO
            {
                boolean esclPrima = this.inserisciEsclusione(tpscr, codts, codEscl);
                return Boolean.valueOf(esclPrima);
            } else //si o nd
            {
                this.annullaEsclusione(tpscr, codts, codEscl);
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
