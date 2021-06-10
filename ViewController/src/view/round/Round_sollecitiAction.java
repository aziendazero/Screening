package view.round;

import insiel.dataHandling.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.A_AppModule;
import model.common.Round_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Round_invitiBean;

import model.inviti.GeneratoreInviti;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.invito.DistributoreInviti;
import view.invito.GestoreRound;


public class Round_sollecitiAction extends Round_DataForwardAction {
    public Round_sollecitiAction() {
    }

    protected String getElenco_voName() {
        return "Round_SoInvitoSollecitiView1";
    }

    public void onSelectCentro(ValueChangeEvent valueChangeEvent) {
        // mauro 30/10/2009, aggiunta per gestione comune/zona
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        bean.setComune(null);
        bean.setZona(null);

        processUpdateModel();
        // mauro 30/10/2009, fine

        this.setGenerationDate();
    }

    /**
     * mauro aggiunto il 30/10/2009 per gestione comune/zona
     * Se cambio comune devo rigenerare le zone e resettare la scelta
     * @param ctx
     */
    public void onSelectComune(ValueChangeEvent valueChangeEvent) {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        //  System.out.println("zona="+bean.getZona());
        bean.setZona(null);
        // System.out.println("zona="+bean.getZona());

        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        processUpdateModel();
    }

    public void onSelectCentroInvito(ValueChangeEvent valueChangeEvent) {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        if (bean.getCentro_2() == null) {
            bean.setCentro(null);
            // mauro aggiunte 04/11/2009
            //this.selectCentro();
            bean.setComune(null);
            //this.selectComune();
            bean.setZona(null);

            if (valueChangeEvent != null)
                valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

            processUpdateModel();

            this.setGenerationDate();
            // mauro 04/11/2009, fine
            return;
        }
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoCentriPrelView1");
        Row[] result = vo.getFilteredRows("Idcentro", bean.getCentro_2());
        //il centro dell'invito e' stato chiuso
        if (result.length == 0)
            bean.setCentro(null);
        else
            //imposto lo stesso centro
            bean.setCentro(bean.getCentro_2());

        // mauro aggiunte 04/11/2009
        //this.selectCentro();
        bean.setComune(null);
        //this.selectComune();
        bean.setZona(null);
        this.setGenerationDate();
        // mauro 04/11/2009, fine

        processUpdateModel();
    }

    public void onSelectTpInvito(ValueChangeEvent valueChangeEvent) {
        //se scelgo il tipo di invito che scatena il sollecito, devo impostare automaticamente il tipo di sollecito
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        String tipoInvito = bean.getTpInvito();

        if (tipoInvito != null) {
            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();

            ViewObject vo = am.findViewObject("Round_SoCnfTpinvitoSollView1");
            Row[] result = vo.getFilteredRows("Idtpinvito", tipoInvito);

            if (result.length > 0) {
                String tipoSolecito = (String) result[0].getAttribute("Idtpinvito1");
                Integer livello = (Integer) result[0].getAttribute("Livello");

                //vo per tipo invito
                vo = am.findViewObject("Round_SoCnfTpinvitoSollecitiView1");
                String where = " LIVELLO = " + bean.getLivello() + " AND IDTPINVITO = '" + tipoSolecito + "' ";
                vo.setWhereClause(where);
                vo.executeQuery();

                if(vo.getEstimatedRowCount()>0){
                    bean.setTpSollecito(tipoSolecito);
                    bean.setLivelloInvito(livello);
                } else {
                    bean.setTpSollecito(null);
                    bean.setLivelloInvito(null);
                }
            } else {
                //svuoto il vo per i solleciti
                vo = am.findViewObject("Round_SoCnfTpinvitoSollecitiView1");
                String where = " 1=2 ";
                vo.setWhereClause(where);
                vo.executeQuery();
            }
        } else 
            processUpdateModel();
    }

    /**
     * Selezione e conteggio della popolazione target in base ai criteri inseriti
     * @param ctx
     */
    public void onFilterPopulation(ActionEvent actionEvent) {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        //  Round_invitiBean bean=(Round_invitiBean)BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        try {

            this.checkFilters();
            ViewObject target = this.filterSoggetti();
            this.countTarget(target);


        } catch (Exception ex) {
            ex.printStackTrace();
            if(ex.getMessage()!=null && !ex.getMessage().isEmpty())
                this.handleException(ex.getMessage(), am.getTransaction());
            else
                this.handleException(ex, am.getTransaction());
        }
    }

    /**
     * Metodo che controlla che i filtri per la selzione delle popolazione target
     * siano corretti
     * @throws java.lang.Exception se un filtro non e' corretto
     * @param ctx
     */
    private void checkFilters() throws Exception {
        //   Round_AppModule am=(Round_AppModule)BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        //CONTROLLO VALIDITa' FILTRI
        /*   if(bean.getCentro()==null || bean.getCentro().intValue()<0)
         throw new Exception("Selezionare il centro di prelievo prima di eseguire la selezione");
    */if (bean.getEsito() == null || bean.getEsito().length() == 0)
            throw new Exception("Selezionare l'esito in seguito al quale deve scattare il sollecito");

        //controllo solo il formato, trattandosi non di primi inviti posso invitare anche
        //chi e' ormai fuori fascia
        if (bean.getEta_a() > 0 || bean.getEta_a() > 0) {
            if (bean.getData_riferimento_eta() == null)
                throw new Exception("Impostare la data di riferimento per l'eta' prima di eseguire la selezione");
        }
        
        if (bean.getOrdine() == null)
            throw new Exception("Selezionare il tipo di ordinamento da applicare");

        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        if (hpvAttivo.booleanValue() && bean.getLivello().intValue() == 1) {
            if (("-1".equals(bean.getTpSollecito()) || bean.getTpSollecito() == null) &&
                ("tutti".equals(bean.getTestProposto()) || bean.getTestProposto()==null)) {
                throw new Exception("Impostare il tipo di sollecito o il test proposto");
            }
        }

    }

    /**
     * Metodo che filtra i soggetti per trovare quelli target della ricerca.
     * Qui si cercano tutte l edonne che devono proseguire l'iter di screening
     * nel centro selezionato
     * @throws java.lang.Exception
     * @return
     * @param ctx
     */
    private ViewObject filterSoggetti() throws Exception {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoInvitoSollecitiView1");
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String as = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        Boolean hpvAttivo = (Boolean) session.get("HPV");
        session.put("doppiaTabella", Boolean.FALSE);

        String where = "ULSS='" + as + "' AND TPSCR='" + tpscr + "' AND ";

        ViewObject cnfTpscr = am.findViewObject("Round_SoCnfTpscrView1");
        Row[] cnf_scr = cnfTpscr.getFilteredRows("Codscr", tpscr);
        if (cnf_scr.length == 0)
            throw new Exception("Configurazione di screening non trovata per lo screening in uso");
        if (cnf_scr[0].getAttribute("Sesso") != null)
            where += "SESSO='" + cnf_scr[0].getAttribute("Sesso") + "' AND ";


        String lista_escluse =
            "SELECT Round_SoEsclusione.CODTS \n" +
            "FROM SO_ESCLUSIONE Round_SoEsclusione, SO_CNF_ESCLUSIONE Round_SoCnfEsclusione  \n" +
            "WHERE ((Round_SoEsclusione.IDCNFESCL = Round_SoCnfEsclusione.IDCNFESCL) AND  \n" +
            "(Round_SoEsclusione.ULSS = Round_SoCnfEsclusione.ULSS)) AND  \n" +
            "(Round_SoEsclusione.TPSCR = Round_SoCnfEsclusione.TPSCR) \n" + "AND Round_SoEsclusione.tpscr='" + tpscr +
            "' \n" +
            //mod20090724
            "AND Round_SoEsclusione.ULSS='" + as + "' \n" +
            //fine mod
            "AND DTANNULL IS NULL AND DTESCL<=TO_DATE('" + DateUtils.getToday() + "','" + DateUtils.DATE_PATTERN +
            "')  \n" + "AND (TPESCL='D' OR (TPESCL='T' AND DTESCL+NUMGIORNI>=TO_DATE('" + DateUtils.getToday() + "','" +
            DateUtils.DATE_PATTERN + "'))) ";

        where += "CODTS NOT IN (" + lista_escluse + ") AND ";

        //considero solo l'invito attivo
        where += "ATTIVO=1 AND " +

            //15112013 Gaion, aggiunto esito per mancata presenza RSS
            //esito cablato=Non presentata
            "(CODESITOINVITO='" + ConfigurationConstants.CODICE_ESITO_MANCATA_PRESENZA;
        if (tpscr.equals("CO")) {
            where += "' OR CODESITOINVITO='" + ConfigurationConstants.CODICE_ESITO_MANCATA_PRESENZA_RSS + "' ) AND ";
        } else {
            where += "' ) AND ";
        }

        //filtro anagrafico corretto
        where += "ANAG_TPSCR='" + tpscr + "' AND INCLUSO=1 AND ";

        /*   if(bean.getCentro()==null || bean.getCentro().intValue()<0)
         where+="IDCENTROPRELIEVO IS NULL AND ";
       else
         where+="IDCENTROPRELIEVO="+bean.getCentro()+" AND ";*/
        where += "IDCENTROPRELIEVO=" + bean.getCentro_2() + " AND ";

        //se ho scelto anche un preciso tipo di invito
        if (bean.getTpInvito() != null && bean.getTpInvito().length() > 0 && !"-1".equals(bean.getTpInvito()))
            where += "IDTPINVITO='" + bean.getTpInvito() + "' AND ";

        //deve avere un sollecito definito dopo tot giorni
        where += "GGSOLLECITO>=0 AND " +
            //devono essere trascorsi piu' dei giorni richiesti
            "(SCADENZA<=SYSDATE) AND ";
        //NESSUN SOLLECITO e' ANCORA STATO INVIATO (non serve, adesso i solleciti sono comunque inviti
        // "DTINVIOSOLLECITO IS NULL AND ";

        //filtro in base all'eta'
        if (bean.getEta_a() > 0)
            //seleziono chi alla data di riferimento non ha ancora compiuto l'eta' massima +1
            where +=
                "DATA_NASCITA>add_months(TO_DATE('" +
                new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_riferimento_eta()) + "','" +
                DateUtils.DATE_PATTERN + "'),-" + (bean.getEta_a() + 1) + "*12) AND ";

        if (bean.getEta_da() > 0)
            //seleziono chi alla data di riferimento ha gia' compiuto l'eta' minima
            where +=
                "DATA_NASCITA<add_months(TO_DATE('" +
                new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_riferimento_eta()) + "','" +
                DateUtils.DATE_PATTERN + "'),-" + bean.getEta_da() + "*12) AND ";

        // mauro 30/10/2009, filtro per comune o zona
        //se ho selezionato un comune
        if (bean.getComune() != null && bean.getComune().length() > 0 && !bean.getComune().equals("-1")) {

            where += "(COMUNE_SOGG='" + bean.getComune() + "') AND ";
            //se ho selezionato anche una zona
            if (bean.getZona() != null && bean.getZona().intValue() > 0)
                //filtro sulla zona
                where += "CODDISTRZONA=" + bean.getZona() + " AND ";

        }
        // mauro 30/10/2009, fine modifica


        //filtro in base alla data dell'invito
        if (bean.getMax_data_invito() != null)
            where +=
                "DTAPP>=TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getMax_data_invito()) + "','" +
                DateUtils.DATE_PATTERN + "') AND ";

        if (bean.getTpSollecito() != null && !bean.getTpSollecito().equals("-1")) {
            where += "IDTPINV_SOLL ='" + bean.getTpSollecito() + "' AND ";

            if (hpvAttivo.booleanValue() && bean.getLivello().intValue() == 1) {
                session.put("doppiaTabella", Boolean.TRUE);
            }
        }

        if (hpvAttivo.booleanValue() && bean.getLivello().intValue() == 1 && bean.getTestProposto()!=null && !"tutti".equals(bean.getTestProposto())) {
            where += "TEST_PROPOSTO = '" + bean.getTestProposto() + "' AND ";
        }

        if (where.endsWith(" AND "))
            where = where.substring(0, where.length() - 5);

        vo.setWhereClause(where);

        // mauro 22/01/2010, nuova gestione ordinamento
        String ordine = bean.getOrdine();

        if (ordine.equals("Default")) {
            String s = "DATA_NASCITA,DTAPP,COGNOME,NOME";
            if (tpscr.equals("PF")) {
                s = "DATA_NASCITA DESC,DTAPP,COGNOME,NOME";
            }
            vo.setOrderByClause(s);
        } else if (ordine.equals("Indirizzo")) {
            String s =
                "DECODE(codanagreg,3,codcomdom,codcomres)," + "DECODE(codanagreg,3,indirizzo_dom,indirizzo_res)," +
                "DATA_NASCITA,COGNOME,NOME";
            vo.setOrderByClause(s);
        } else if (ordine.equals("Dtultapp")) {
            String s = "DTAPP,DATA_NASCITA,COGNOME,NOME";
            if (tpscr.equals("PF")) {
                s = "DTAPP,DATA_NASCITA DESC,COGNOME,NOME";
            }
            vo.setOrderByClause(s);
        }

        vo.getEstimatedRowCount();
        vo.executeEmptyRowSet();
        vo.executeQuery();

        // mauro 27/05/2010, registro dove e' stata fatta ultima query

        session.put("conteggioInviti", "S");
        // mauro 27/05/2010, fine

        return vo;

    }


    /**
     * Metodo che inserisce nell'apposito viewobject i tipi diinviti da generare con
     * le relative numerosita'
     * @param target viewobject con la popolazione gia' filtrata
     * @param ctx
     * @param defaultType se not null, tutti gli inviti si sintendono del tipo fornito
     */
    private void countTarget(ViewObject target) throws Exception {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();

        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView3");

        // mauro 28/05/2010, modifica query per non avere righe doppie
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        Boolean hpvAttivo = (Boolean) sess.get("HPV");
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        Boolean doppiaTabella = (Boolean) sess.get("doppiaTabella");

        String querySoll =
            "SELECT distinct Round_SoCnfTpinvito1.IDTPINVITO AS IDTPINVITO1, " +
            "Round_SoCnfTpinvito1.DESCRIZIONE AS DESCRIZIONE1 " +
            "FROM SO_CNF_TPINVITO Round_SoCnfTpinvito, SO_CNF_TPINVITO Round_SoCnfTpinvito1 " +
            "WHERE ((Round_SoCnfTpinvito.IDTPINV_SOLL = Round_SoCnfTpinvito1.IDTPINVITO) AND " +
            "(Round_SoCnfTpinvito.ULSS = Round_SoCnfTpinvito1.ULSS)) AND " +
            "(Round_SoCnfTpinvito.TPSCR = Round_SoCnfTpinvito1.TPSCR) AND " + "Round_SoCnfTpinvito.ULSS = '" + ulss +
            "' AND Round_SoCnfTpinvito.TPSCR = '" + tpscr + "'";

        ViewObject tpinviti = am.createViewObjectFromQueryStmt("", querySoll);
        tpinviti.setRangeSize(-1);
        tpinviti.executeQuery();

        //cancello dati vecchi
        ViewHelper.clearVO(vo);

        ViewObject voHPV = am.findViewObject("Round_SoTipiInvitiDaGenerareView7");
        ViewHelper.clearVO(voHPV);

        //ciclo sui tipi di inviti
        Row tp = tpinviti.first();
        while (tp != null) {
            Row[] result = target.getFilteredRows("IdtpinvSoll", tp.getAttribute(0));

            if (result.length > 0) {
                if (hpvAttivo.booleanValue()) {
                    Integer livello = (Integer) result[0].getAttribute("Livello");
                    if (livello.intValue() == 1) {
                        if (doppiaTabella.booleanValue()) {

                            //conto i pap
                            Row[] resultPap = target.getFilteredRows("TestProposto", "PAP");
                            Row r = vo.createRow();
                            vo.insertRow(r);
                            r.setAttribute("Idtpinvito", tp.getAttribute(0));
                            r.setAttribute("Descrizione", tp.getAttribute(1));
                            r.setAttribute("TipoTest", "PAP");
                            r.setAttribute("DaGenerare", 0);
                            if (resultPap.length > 0) {
                                //numero di inviti di quel tipo da generare
                                r.setAttribute("Numero", resultPap.length);
                            } else {
                                r.setAttribute("Numero", 0);
                            }

                            // conto hpv proposti
                            Row[] resultHPV = target.getFilteredRows("TestProposto", "HPV");
                            Row rHPV = voHPV.createRow();
                            voHPV.insertRow(rHPV);
                            rHPV.setAttribute("Idtpinvito", tp.getAttribute(0));
                            rHPV.setAttribute("Descrizione", tp.getAttribute(1));
                            rHPV.setAttribute("TipoTest", "HPV");
                            rHPV.setAttribute("DaGenerare", 0);
                            if (resultHPV.length > 0) {
                                //numero di inviti di quel tipo da generare
                                rHPV.setAttribute("Numero", resultHPV.length);
                            } else {
                                rHPV.setAttribute("Numero", 0);
                            }
                        } else {

                            //devo visualizzare il test proposto
                            int counterHPV = 0;
                            int counterPAP = 0;
                            int counterVoid = 0;
                            for (int i = 0; i < result.length; i++) {
                                if (result[i].getAttribute("TestProposto") != null) {
                                    if (result[i].getAttribute("TestProposto").equals("HPV")) {
                                        counterHPV++;
                                    } else if (result[i].getAttribute("TestProposto").equals("PAP")) {
                                        counterPAP++;
                                    }
                                } else {
                                    counterVoid++;
                                }
                            }
                            if (counterHPV > 0) {
                                Row r = vo.createRow();
                                vo.insertRow(r);
                                r.setAttribute("Idtpinvito", tp.getAttribute(0));
                                r.setAttribute("Descrizione", tp.getAttribute(1));
                                r.setAttribute("TipoTest", "HPV");
                                //numero di inviti di quel tipo da generare
                                r.setAttribute("Numero", counterHPV);
                                r.setAttribute("DaGenerare", 0);
                            }
                            if (counterPAP > 0) {
                                Row r = vo.createRow();
                                vo.insertRow(r);
                                r.setAttribute("Idtpinvito", tp.getAttribute(0));
                                r.setAttribute("Descrizione", tp.getAttribute(1));
                                r.setAttribute("TipoTest", "PAP");
                                //numero di inviti di quel tipo da generare
                                r.setAttribute("Numero", counterPAP);
                                r.setAttribute("DaGenerare", 0);
                            }
                            if (counterVoid > 0) {
                                Row r = vo.createRow();
                                vo.insertRow(r);
                                r.setAttribute("Idtpinvito", tp.getAttribute(0));
                                r.setAttribute("Descrizione", tp.getAttribute(1));
                                r.setAttribute("TipoTest", null);
                                //numero di inviti di quel tipo da generare
                                r.setAttribute("Numero", counterVoid);
                                r.setAttribute("DaGenerare", 0);
                            }
                        }
                    } else {
                        Row r = vo.createRow();
                        vo.insertRow(r);
                        r.setAttribute("Idtpinvito", tp.getAttribute(0));
                        r.setAttribute("Descrizione", tp.getAttribute(1));
                        r.setAttribute("TipoTest", null);
                        //numero di inviti di quel tipo da generare
                        r.setAttribute("Numero", result.length);
                        r.setAttribute("DaGenerare", 0);
                    }
                } else {
                    Row r = vo.createRow();
                    vo.insertRow(r);
                    r.setAttribute("Idtpinvito", tp.getAttribute(0));
                    r.setAttribute("Descrizione", tp.getAttribute(1));
                    r.setAttribute("TipoTest", null);
                    //numero di inviti di quel tipo da generare
                    r.setAttribute("Numero", result.length);
                    r.setAttribute("DaGenerare", 0);
                }
            }

            tp = tpinviti.next();
        }

        // mauro 28/05/2010, fine modifiche

        target.clearCache();

    }


    public void onGenerate(ActionEvent actionEvent) {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        A_AppModule am2 =
            (A_AppModule) BindingContext.getCurrent().findDataControl("A_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String user = (String) ADFContext.getCurrent().getSessionScope().get("user");
        //soggetti, gia' filtrati
        ViewObject soggetti = am.findViewObject("Round_SoInvitoSollecitiView1");
        ViewObject inviti = am.findViewObject("Round_SoInvitoView4");
        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView3");
        //slot, DA FILTRARE SE SERVE USARLI
        ViewObject slots = am.findViewObject("Round_SoAppuntamentoView1");
        ViewObject tpinviti = am.findViewObject("Round_SoCnfTpinvitoView1");
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");

        //conterra' vettori di inviti tutti dello stesso tipo per i quali bisogna generare un appuntamento
        Hashtable daInvitare = new Hashtable();
        //conterra' vettori di inviti tutti dello stesso tipo per i quali non e' necessario un appuntamento
        Hashtable daSpedire = new Hashtable();
        //conterra' il numero di inviti da fare per ogni tipo
        Hashtable quantiPerTipo = new Hashtable();
        //conterra' le descrizioni testuali dei tipi di invito
        Hashtable descrizioni = new Hashtable();
        //quanti ne ho generati per tipo, inizializzo a zero
        Hashtable generati = new Hashtable();

        // mauro 09/02/2010, spostato fuory da try x rimozione logo
        //preparo la classe che generera' gli inviti(con inizializzazione ridotta=false)
        GeneratoreInviti gi = new GeneratoreInviti(am2, false);
        //

        // mauro 15/12/2010, gestione allegato virtuale
        Integer virt = bean.getVirtuale();
        boolean virtuale = (virt != null && virt.intValue() == 1);
        //

        boolean before = true;
        try {
            AccessManager.checkPermission("SOGenerazioneInviti");

            // mauro 27/05/2010, registro dove e' stata fatta ultima query
            Map sess = ADFContext.getCurrent().getSessionScope();
            String conteggioInviti = (String) sess.get("conteggioInviti");

            if (!conteggioInviti.equals("S")) {
                throw new Exception("L'ultimo conteggio degli inviti non e' stato eseguito su 'Solleciti'. Rieseguire il conteggio.");
            }
            // mauro 27/05/2010, fine

            //controllo la correttezza dei dati inseriti
            this.checkParams();

            Date rif_date;
            if (bean.getData_riferimento_eta() != null)
                rif_date = bean.getData_riferimento_eta();
            else
                rif_date = new Date();

            //1: preparo tutto per la distribuzione degli inviti
            //ciclo sulla generzione degli inviti
            //per ogni tipo di invito da generare
            Row r = vo.first();
            //isolo gli inviti relativi ed il conto di quanti generarne
            while (r != null) {
                String tipo = (String) r.getAttribute("Idtpinvito");
                int n_inviti = ((Integer) r.getAttribute("DaGenerare")).intValue();
                int risultato = ((Integer) r.getAttribute("Numero")).intValue();
                if (n_inviti == 0 || risultato == 0) {
                    r = vo.next();
                    continue;
                }

                Row[] tipi = tpinviti.getFilteredRows("Idtpinvito", tipo);
                if (tipi.length == 0)
                    throw new Exception("Tipo di invito con codice " + tipo + " non trovato");

                //memorizzo l'info testuale sul tipo di invito
                descrizioni.put(tipo, (String) r.getAttribute("Descrizione"));
                //inserisco l'array dei soggetti da invitare con quell'invito
                Row[] result = soggetti.getFilteredRows("IdtpinvSoll", tipo);
                //tengo solo il numero di inviti che voglio generare
                int elle = Math.min(result.length, n_inviti);
                Row[] elenco = new Row[elle];


                if (hpvAttivo.booleanValue()) {
                    //devo copiare nel nuovo array solo gli inviti con il test proposto selezionato
                    String test_proposto = (String) r.getAttribute("TipoTest");
                    int counter = 0;
                    int copiati = 0;
                    while (copiati < elle) {
                        if (test_proposto != null && result[counter].getAttribute("TestProposto") != null &&
                            result[counter].getAttribute("TestProposto").equals(test_proposto)) {
                            elenco[copiati] = result[counter];
                            copiati++;
                        } else if (test_proposto == null && result[counter].getAttribute("TestProposto") == null) {
                            elenco[copiati] = result[counter];
                            copiati++;
                        }
                        counter++;
                    }

                    //controllo se si tratta di invito con o senza appuntamento
                    if (((Integer) tipi[0].getAttribute("Appuntamento")) == null ||
                        ((Integer) tipi[0].getAttribute("Appuntamento")).intValue() != 1) {
                        //senza appuntamento
                        //devo unire gli inviti con stesso tipo invito ma diverso tipo test proposto
                        if (daSpedire.get(tipo) != null) {
                            Row[] old = (Row[]) daSpedire.get(tipo);
                            List listMerged = new ArrayList(Arrays.asList(old));
                            listMerged.addAll(Arrays.asList(elenco));
                            Row[] newElenco = (Row[]) listMerged.toArray(new Row[0]);

                            daSpedire.put(tipo, newElenco);
                            quantiPerTipo.put(tipo, newElenco.length);
                        } else {
                            daSpedire.put(tipo, elenco);
                            quantiPerTipo.put(tipo, elle);
                        }
                    } else {
                        //con appuntameto
                        //devo unire gli inviti con stesso tipo invito ma diverso tipo test proposto
                        if (daInvitare.get(tipo) != null) {
                            Row[] old = (Row[]) daInvitare.get(tipo);
                            List listMerged = new ArrayList(Arrays.asList(old));
                            listMerged.addAll(Arrays.asList(elenco));
                            Row[] newElenco = (Row[]) listMerged.toArray(new Row[0]);

                            daInvitare.put(tipo, newElenco);
                            quantiPerTipo.put(tipo, newElenco.length);
                        } else {
                            daInvitare.put(tipo, elenco);
                            quantiPerTipo.put(tipo, elle);
                        }
                    }
                } else {

                    System.arraycopy(result, 0, elenco, 0, elle);

                    //controllo se si tratta di invito con o senza appuntamento


                    if (((Integer) tipi[0].getAttribute("Appuntamento")) == null ||
                        ((Integer) tipi[0].getAttribute("Appuntamento")).intValue() != 1)
                        //senza appuntamento
                        daSpedire.put(tipo, elenco);
                    else
                        //con appuntameto
                        daInvitare.put(tipo, elenco);

                    //inserisco il numero di inviti di quel tipo che devo fare
                    quantiPerTipo.put(tipo, elle);

                }
                generati.put(tipo, 0);
                r = vo.next();

            }

            Boolean doppiaTabella = (Boolean) ADFContext.getCurrent().getSessionScope().get("doppiaTabella");
            if (doppiaTabella.booleanValue()) {
                vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView7");

                r = vo.first();
                while (r != null) {
                    String tipo = (String) r.getAttribute("Idtpinvito");
                    int n_inviti = ((Integer) r.getAttribute("DaGenerare")).intValue();
                    int risultato = ((Integer) r.getAttribute("Numero")).intValue();
                    if (n_inviti == 0 || risultato == 0) {
                        r = vo.next();
                        continue;
                    }

                    Row[] tipi = tpinviti.getFilteredRows("Idtpinvito", tipo);
                    if (tipi.length == 0)
                        throw new Exception("Tipo di invito con codice " + tipo + " non trovato");

                    //memorizzo l'info testuale sul tipo di invito
                    descrizioni.put(tipo, (String) r.getAttribute("Descrizione"));
                    //inserisco l'array dei soggetti da invitare con quell'invito
                    Row[] result = soggetti.getFilteredRows("IdtpinvSoll", tipo);
                    //tengo solo il numero di inviti che voglio generare
                    int elle = Math.min(result.length, n_inviti);
                    Row[] elenco = new Row[elle];

                    if (hpvAttivo.booleanValue()) {
                        //devo copiare nel nuovo array solo gli inviti con il test proposto selezionato
                        String test_proposto = (String) r.getAttribute("TipoTest");
                        int counter = 0;
                        int copiati = 0;
                        while (copiati < elle) {
                            if (test_proposto != null && result[counter].getAttribute("TestProposto") != null &&
                                result[counter].getAttribute("TestProposto").equals(test_proposto)) {
                                elenco[copiati] = result[counter];
                                copiati++;
                            } else if (test_proposto == null && result[counter].getAttribute("TestProposto") == null) {
                                elenco[copiati] = result[counter];
                                copiati++;
                            }
                            counter++;
                        }

                        //controllo se si tratta di invito con o senza appuntamento
                        if (((Integer) tipi[0].getAttribute("Appuntamento")) == null ||
                            ((Integer) tipi[0].getAttribute("Appuntamento")).intValue() != 1) {
                            //senza appuntamento
                            //devo unire gli inviti con stesso tipo invito ma diverso tipo test proposto
                            if (daSpedire.get(tipo) != null) {
                                Row[] old = (Row[]) daSpedire.get(tipo);
                                List listMerged = new ArrayList(Arrays.asList(old));
                                listMerged.addAll(Arrays.asList(elenco));
                                Row[] newElenco = (Row[]) listMerged.toArray(new Row[0]);

                                daSpedire.put(tipo, newElenco);
                                quantiPerTipo.put(tipo, newElenco.length);
                            } else {
                                daSpedire.put(tipo, elenco);
                                quantiPerTipo.put(tipo, elle);
                            }
                        } else {
                            //con appuntameto
                            //devo unire gli inviti con stesso tipo invito ma diverso tipo test proposto
                            if (daInvitare.get(tipo) != null) {
                                Row[] old = (Row[]) daInvitare.get(tipo);
                                List listMerged = new ArrayList(Arrays.asList(old));
                                listMerged.addAll(Arrays.asList(elenco));
                                Row[] newElenco = (Row[]) listMerged.toArray(new Row[0]);

                                daInvitare.put(tipo, newElenco);
                                quantiPerTipo.put(tipo, newElenco.length);
                            } else {
                                daInvitare.put(tipo, elenco);
                                quantiPerTipo.put(tipo, elle);
                            }
                        }
                    }
                    generati.put(tipo, 0);
                    r = vo.next();
                }
            }

            //SE CI SONO SOLLECITI CON APPUNTAMENTO DEVE ESERCI ANCHE
            //UNA DISTRIBUZIONE LOGISTICO-TEMPORALE
            if (!daInvitare.isEmpty()) {
                //controllo ch eci sia un adata di inizio periodo
                if (bean.getGenerate_dal() == null) // || bean.getGenerate_dal().length() == 0)
                    throw new Exception("E' stata richiesta la generazione di solleciti che prevedono un appuntamento, di conseguenza la data di inizio periodo e' un parametro necessario");
                //controllo che l'agenda fisica del centro presenti degli slot
                String where =
                    "TPSRC='" + tpscr + "' AND IDCENTRO=" + bean.getCentro() + " AND DTAPP>=TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getGenerate_dal()) + "','" + DateUtils.DATE_PATTERN +
                    "')";
                if (bean.getGenerate_al() != null) // && bean.getGenerate_al().length() > 0)
                    where +=
                        " AND DTAPP<TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getGenerate_al()) +
                        "','" + DateUtils.DATE_PATTERN + "')";

                // mauro 14-10-2009, controllo filtro per orario e lo metto in query
                String ora_da = bean.getGenerate_daore();
                String ora_a = bean.getGenerate_aore();

                String msgErrFormato = "L'orario impostato non ha il formato corretto hh:mm .";

                if (!ViewHelper.checkStringaOrario(ora_da)) {
                    throw new Exception(msgErrFormato);
                }

                if (!ViewHelper.checkStringaOrario(ora_a)) {
                    throw new Exception(msgErrFormato);
                }

                if ((ora_da != null) && !ora_da.equals("")) {

                    String hh_da = ora_da.substring(0, 2);
                    String mi_da = ora_da.substring(3, 5);

                    where +=
                        " AND ( ( ORAAPP > " + hh_da + ") OR " + "( ORAAPP = " + hh_da + " AND MINAPP >= " + mi_da +
                        ") )";

                }

                if ((ora_a != null) && !ora_a.equals("")) {

                    String hh_a = ora_a.substring(0, 2);
                    String mi_a = ora_a.substring(3, 5);

                    where +=
                        " AND ( ( ORAAPP < " + hh_a + ") OR " + "( ORAAPP = " + hh_a + " AND MINAPP <= " + mi_a + ") )";

                }
                // 14-10-2009, fine modifica


                slots.setWhereClause(where);
                //  System.out.println(where);
                slots.executeQuery();
                slots.setForwardOnly(true);
                //MOD 29/06/2007
                Row slot = slots.first();
                if (slot == null)
                    throw new Exception("L'agenda fisica per il periodo richiesto non e' stata generata");


                //2. ottengo la distribuzione degli inviti
                Vector listaInviti = DistributoreInviti.distribuisciInviti(daInvitare, 5);

                //3. genero effettivamente gli inviti tenendo conto dei vincoli


                //posizione nella lista degli inviti da effettuare
                int contatoreLista = 0;
                GestoreRound gr = new GestoreRound(am2);


                //ciclo sugli slot
                while (slot != null) {
                    if (contatoreLista >= listaInviti.size())
                        break;
                    //disponibilita' dello slot
                    int x =
                        ((Integer) slot.getAttribute("Dispslot")).intValue() -
                        ((Integer) slot.getAttribute("Slotoccup")).intValue();
                    // dalla posizione attuale fino ad esaurire la disponibilita'
                    //27/06/2006: assegno il valore del contatore, perche' poi viene incrementato e non va sempre bene
                    int y = contatoreLista;
                    for (int i = y; i < y + x; i++)
                    // for(int i=contatoreLista;i<contatoreLista+x;i++)
                    {
                        if (i >= listaInviti.size())
                            break;
                        Row sogg = (Row) listaInviti.elementAt(i);
                        int gia_generati = ((Integer) generati.get(sogg.getAttribute("IdtpinvSoll"))).intValue();

                        if (gia_generati >=
                            ((Integer) quantiPerTipo.get(sogg.getAttribute("IdtpinvSoll"))).intValue()) {
                            //ne ho gia' generati a sufficienza di quel tipo, quindi vado avanti
                            contatoreLista++;
                            //26/07/2006 :sposto anche il limite su questo slot
                            y++;
                            continue;
                        }

                        //altrimenti e' un invito da generare
                        try {
                            //leggo un soggetto e determino il comune di riferimento
                            Integer comune;
                            if (((Integer) sogg.getAttribute("Codanagreg")).intValue() == 3)
                                //il comune di riferimento e' il domicilio
                                comune = (Integer) sogg.getAttribute("ReleaseCodeComDom");
                            else
                                comune = (Integer) sogg.getAttribute("ReleaseCodeComRes");

                            //genero l'invito
                            Integer idapp;
                            if (slot == null)
                                idapp = null;
                            else
                                idapp = (Integer) slot.getAttribute("Idapp");

                            //HPV-DNA test proposto
                            String tipo_test = null;
                            if (hpvAttivo.booleanValue()) {
                                tipo_test = (String) sogg.getAttribute("TestProposto");
                            }

                            //se true, incremento generati
                            if (gi.generaInvito(ulss, tpscr, user, (String) sogg.getAttribute("IdtpinvSoll"),
                                                idapp, (String) sogg.getAttribute("Codts"),
                                                (Integer) sogg.getAttribute("Idinvito"), //se non c'e' uninvito attivo e' null
                                                rif_date, (Integer) slot.getAttribute("Idcentro"), virtuale,
                                                tipo_test)) {
                                //aggiorno il tipo di richiamo con un sollecito
                                before = false;
                                soggetti.setCurrentRow(sogg);
                                Row inv = inviti.first();
                                if (inv == null)
                                    throw new Exception("Nessun invito trovato");

                                inv.refresh(Row.REFRESH_CONTAINEES);
                                inv.setAttribute("Tprichiamo", (String) sogg.getAttribute("IdtpinvSoll"));
                                inv.setAttribute("Idcentrorichiamo", bean.getCentro());
                                //data di richiamo
                                Calendar c = DateUtils.getCalendar(((oracle.jbo.domain.Date) inv.getAttribute("Dtapp")).dateValue());
                                Integer gg = (Integer) sogg.getAttribute("Ggsollecito");
                                if (gg != null)
                                    c.add(Calendar.DAY_OF_MONTH, gg.intValue());
                                inv.setAttribute("Dtrichiamo", DateUtils.getOracleDate(c));

                                //trattandosi di un solleicto non dovrebbe esserci chiusura del round individuale
                                inv.setAttribute("Chiusuraroundindiv", ConfigurationConstants.DB_FALSE);

                                am.getTransaction().commit();

                                //incremento il numero di inviti generati di quel tipo
                                gia_generati++;
                                generati.put(sogg.getAttribute("IdtpinvSoll"), gia_generati);

                                //aggiorno l'ultimo sollecito generato
                                gr.updateRoundData(tpscr, comune,
                                                   (oracle.jbo.domain.Date) slot.getAttribute("Dtapp"),
                                                   gr.DTULTSOLLECITO);
                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                            this.handleException("Problemi nella generazione di un invito: " + ex.getMessage(), null);
                        } finally {
                            contatoreLista++;
                        }
                    }


                    slot = slots.next();
                }
            } //fine generazione solleciti con appuntamenti

            //generazione solleciti senza appuntamento
            if (!daSpedire.isEmpty()) {
                Enumeration keys = daSpedire.keys();
                while (keys.hasMoreElements()) {
                    String tpsoll = (String) keys.nextElement();
                    Row[] solleciti = (Row[]) daSpedire.get(tpsoll);
                    for (int i = 0; i < solleciti.length; i++) {
                        int gia_generati = ((Integer) generati.get(tpsoll)).intValue();
                        if (gia_generati >= ((Integer) quantiPerTipo.get(tpsoll)).intValue())
                            //ne ho gia' generati a sufficienza di questo tipo, quindi vado avanti
                            break;

                        try {

                            //HPV-DNA test proposto
                            String tipo_test = null;
                            if (hpvAttivo.booleanValue()) {
                                tipo_test = (String) solleciti[i].getAttribute("TestProposto");
                            }
                            //genero l'invito
                            //se true, incremento generati
                            if (gi.generaInvito(ulss, tpscr, user, tpsoll, null, //nessuno slot perche' non c'e' nessun appuntamento
                                                (String) solleciti[i].getAttribute("Codts"),
                                                (Integer) solleciti[i].getAttribute("Idinvito"), //se non c'e' uninvito attivo e' null
                                                rif_date, bean.getCentro(), virtuale, tipo_test)) {
                                //incremento il numero di inviti generati di quel tipo
                                gia_generati++;
                                before = false;
                                generati.put(tpsoll, gia_generati);
                            }
                        } catch (Exception ex) {
                            this.handleException("Problemi nella generazione di un invito: " + ex.getMessage(), null);
                        }


                    }
                }

            } //fine generazione solleciti senza appuntamento
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException("Impossibile generare gli inviti: " + ex.getMessage(), null);
        } finally {
            // msg+="<li> n° "+gen_tp+" inviti di tipo "+r.getAttribute("Descrizione")+"</li>";
            Enumeration en = descrizioni.keys();
            StringBuilder msg = new StringBuilder("<html><body>");
            if (descrizioni.size() > 0) {
                msg.append("<p><b>Sono stati generati:</b></p>");
                msg.append("<ul>");

                while (en.hasMoreElements()) {
                    String key = (String) en.nextElement();
                    msg.append("<p><li><i>n&#176; " + generati.get(key) + " inviti di tipo " + descrizioni.get(key) +
                               "</i></li></p>");
                }
                msg.append("</ul>");
            } else {
                msg.append("Non e' stato generato nessun invito perche' non e' stato trovato nulla da generare.");
            }

            msg.append("</body></html>");
            Map req = ADFContext.getCurrent().getRequestScope();
            req.put("round_generate_results", msg + "</ul>");
            if (!before)
                this.onFilterPopulation(null);

            soggetti.clearCache();
            slots.clearCache();
            gi.releaseLogo(); // mauro 09/02/2010, rimozione logo

            if(FacesContext.getCurrentInstance().getMaximumSeverity()==null || FacesContext.getCurrentInstance().getMaximumSeverity().equals(FacesMessage.SEVERITY_INFO))
                this.handleMessages(FacesMessage.SEVERITY_INFO, msg.toString());

        }

    }

    private void checkParams() throws Exception {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        // String tpscr=(String)ADFContext.getCurrent().getSessionScope().get("scr");

        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView3");

        Row r = vo.first();
        if (r == null)
            throw new Exception("Non e' stato eseguito nessun calcolo in base la quale generare gli inviti");

        if (bean.getCentro_2() == null || bean.getCentro_2().intValue() < 0)
            throw new Exception("Selezionare il centro degli inviti su cui eseguire i solleciti");
        //il centro in cui posizionare gli inviti
        if (bean.getCentro() == null || bean.getCentro().intValue() < 0)
            throw new Exception("Selezionare il centro in cui fissare gli appuntamenti");


        try {
            //formato + l'inizio deve essere non prima di domani
            if (bean.getGenerate_dal() != null //&& bean.getGenerate_dal().length() > 0
                && bean.getGenerate_dal().before(new Date()))
                throw new Exception("La generazione degli inviti fino alla data corrente non e' un'operazione consentita.");
        } catch (ParseException pex) {
            throw new Exception("La data di inizio del periodo non corrisponde al formato dd/MM/yyyy");
        }
        try {
            //formato, se non null (non e' obbligatoria)
            if (bean.getGenerate_al() != null //&& bean.getGenerate_al().length() > 0
                && bean.getGenerate_al().before(new Date()))
                throw new Exception("La generazione degli inviti fino alla data corrente non e' un'operazione consentita.");
        } catch (ParseException pex) {
            throw new Exception("La data di fine del periodo non corrisponde al formato dd/MM/yyyy");
        }


    }

    public String onGoDetails() {
        //riporto la query dal vo indipendente a quello dipendente e navigo
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoInvitoSollecitiView1");
        String where = vo.getWhereClause();
        String order = vo.getOrderByClause();
        vo = am.findViewObject("Round_SoInvitoSollecitiView2");
        vo.setWhereClause(where);
        vo.setOrderByClause(order);
        vo.executeQuery();

        vo = am.findViewObject("Round_SoInvitoSollecitiView3");
        vo.setWhereClause("1=2");
        vo.executeQuery();

        ADFContext.getCurrent().getSessionScope().put("listaHPV", Boolean.FALSE);

        return "goDetails";
    }

    public String onGoDetailsHPV() {
        //riporto la query dal vo indipendente a quello dipendente e navigo
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoInvitoSollecitiView1");
        String where = vo.getWhereClause();
        String order = vo.getOrderByClause();
        vo = am.findViewObject("Round_SoInvitoSollecitiView3");
        vo.setWhereClause(where);
        vo.setOrderByClause(order);
        vo.executeQuery();

        vo = am.findViewObject("Round_SoInvitoSollecitiView2");
        vo.setWhereClause("1=2");
        vo.executeQuery();

        ADFContext.getCurrent().getSessionScope().put("listaHPV", Boolean.TRUE);

        return "goDetails";
    }

    /**
     * mauro aggiunto il 30/10/2009 per gestione comune/zona
     *Ad ogni refresh devo mantenere la consistenza dei dati tra centri, comuni e zone
     * @param ctx
     */
    protected void processUpdateModel() {
        this.selectCentro();
        this.selectComune();

    }


    /**
     * In seguito alla selzione di un centro e un comune scremo le zone per vedere
     * quelle associate al comune che sono prive di centro di 1 livello
     * o che hanno il centro di 1 livello selezionato
     * @param ctx
     */
    private static void selectComune() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        //se la selezione e' nulla non faccio nulla
        if (bean.getCentro() != null) { //24092013 gaion: aggiunto controllo per evitare nullpointer
            ViewObject vo = am.findViewObject("Round_SoZoneByComuniCentri1");
            //MOD 20090511 (eliminazione duplicati zone)
            RowSetIterator iter = null;
            //FINE MOD
            try {

                Map sess = ADFContext.getCurrent().getSessionScope();
                String scr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");

                String where = "ULSS = '" + ulss + "' AND TPSCR = '" + scr + "'";

                where += " AND CODCOM='" + bean.getComune() + "' AND RELEASE_CODE_COM=" + bean.getRc_comune();

                int tipoCt = ViewHelper.getTipoCentro(am, bean.getCentro());

                if (tipoCt == 1) {
                    where += " AND (IDCENTRO1LIV IS NULL OR IDCENTRO1LIV=" + bean.getCentro() + ")";
                } else {
                    where += " AND (IDCENTRO2LIV IS NULL OR IDCENTRO2LIV=" + bean.getCentro() + ")";
                }

                vo.setWhereClause(where);

                vo.executeQuery();

                //MOD 20090511
                //filtro di doppioni (zone che rientrano sia perche' associati al centro,
                //sia perche' associate a uno o piu'a' comuni associati a quel centro)
                iter = ViewHelper.getRowSetIterator(vo, "zone_iter");
                HashMap hash = new HashMap();
                while (iter.hasNext()) {
                    Row r = iter.next();
                    Object o = hash.get(r.getAttribute("Coddistrzona"));
                    //se questo comune non e' gia' presente lo aggiungo
                    if (o == null)
                        hash.put(r.getAttribute("Coddistrzona"), Boolean.TRUE);
                    //se c'e' gia' lo elimino solo dal viewobject
                    else
                        r.removeFromCollection();
                }

                //FINE MOD

                // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
                if (bean.getZona() != null) {
                    Key _k = new Key(new Object[] { bean.getZona() });
                    Row[] _r = iter.findByKey(_k, 1);

                    if (_r == null || _r.length == 0) {
                        bean.setZona(null);
                    }
                }
                // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE
            } catch (Exception ex) {

                ex.printStackTrace();
            }
            //MOD 20090511
            finally {
                if (iter != null)
                    iter.closeRowSetIterator();
            }
            //FINE MOD
        }
    }


    /**
     * In seguito alla selezione di un centro di prelievo scremo i comuni che sono
     * associati a quel centro, che non sono associati a nessun centro o che hanno almeno
     * una zona associata a quel centro
     * @param ctx
     */

    private static void selectCentro() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        //se la selezione e' nulla non faccio nulla

        RowSetIterator iter = null;
        ViewObject vo = am.findViewObject("Round_SoComuniByCentri1");


        try {

            Map sess = ADFContext.getCurrent().getSessionScope();
            String scr = (String) sess.get("scr");
            String ulss = (String) sess.get("ulss");

            String where = "ULSS = '" + ulss + "' AND TPSCR = '" + scr + "'";

            //se centro=-1 e' corretto che nulla venga visualizzato
            where += " AND (IDCENTRO1LIV=" + bean.getCentro() + " OR IDCENTRO2LIV=" + bean.getCentro() + ")";
            vo.setWhereClause(where);

            vo.executeQuery();

            //filtro di doppioni (comuni che rientrano sia perche' associati al centro,
            //sia perche' in zone associate al centro)
            iter = ViewHelper.getRowSetIterator(vo, "comuni_iter");
            HashMap hash = new HashMap();
            while (iter.hasNext()) {
                Row r = iter.next();
                Object o = hash.get(r.getAttribute("Codcom"));
                //se questo comune non e' gia' presente lo aggiungo
                if (o == null)
                    hash.put(r.getAttribute("Codcom"), Boolean.TRUE);
                //se c'e' gia' lo elimino solo dal viewobject
                else
                    r.removeFromCollection();
            }

            //vo per tipo invito
            vo = am.findViewObject("Round_SoCnfTpinvitoSollecitiView1");
            where = " LIVELLO = " + bean.getLivello();
            vo.setWhereClause(where);
            vo.executeQuery();

            if (bean.getLivello() != null && bean.getLivelloInvito() != null &&
                bean.getLivello().intValue() != bean.getLivelloInvito().intValue()) {
                bean.setTpSollecito("-1");
            }

            // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
            if (bean.getRc_comune() != null) {
                Key _k = new Key(new Object[] { bean.getRc_comune() });
                Row[] _r = iter.findByKey(_k, 1);

                if (_r == null || _r.length == 0) {
                    bean.setRc_comune(null);
                    bean.setComune(null);
                }
            }
            // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }
    }
}
