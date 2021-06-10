package view.round;

import insiel.dataHandling.DateUtils;

import java.sql.SQLException;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import model.util.MessageBundle;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.invito.GestoreRound;

public class Round_invitiRSSAction extends Round_DataForwardAction {
    private static ADFLogger logger = ADFLogger.createADFLogger(Round_invitiRSSAction.class);

    private Integer centro_conta;

    protected String getElenco_voName() {
        return "Round_SoSoggettiFilteredView4";
    }

    /**
     *Se cambio centro devo resettare comune e zona
     * @param ctx
     */
    public void onSelectCentro(ValueChangeEvent valueChangeEvent) {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        if(valueChangeEvent!=null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        bean.setComune(null);
        bean.setZona(null);
        //this.selectComune();
        processUpdateModel();
        
        this.setGenerationDate();
    }

    /**
     * Se cambio comune devo rigenerare le zone e resettare la scelta
     * @param ctx
     */
    public void onSelectComune(ValueChangeEvent valueChangeEvent) {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        bean.setZona(null);
        
        if(valueChangeEvent!=null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        processUpdateModel();
    }

    /**
     *Ad ogni refresh devo mantenere la consistenza dei dati tra centri, comuni e zone
     * @param ctx
     */
    protected void processUpdateModel() {
        this.selectCentro();
        this.selectComune();
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

            // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
            if(bean.getRc_comune()!=null){
                Key _k = new Key(new Object[]{bean.getRc_comune()});
                Row[] _r = iter.findByKey(_k, 1);
                
                if(_r==null || _r.length==0){
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

        ViewObject vo = am.findViewObject("Round_SoZoneByComuniCentri1");
        RowSetIterator iter = null;

        try {

            Map sess = ADFContext.getCurrent().getSessionScope();
            String scr = (String) sess.get("scr");
            String ulss = (String) sess.get("ulss");

            String where = "ULSS = '" + ulss + "' AND TPSCR = '" + scr + "'";

            where += " AND CODCOM='" + bean.getComune() + "' AND RELEASE_CODE_COM=" + bean.getRc_comune();

            if (bean.getCentro() != null) {
                int tipoCt = ViewHelper.getTipoCentro(am, bean.getCentro());

                if (tipoCt == 1) {
                    where += " AND (IDCENTRO1LIV IS NULL OR IDCENTRO1LIV=" + bean.getCentro() + ")";
                } else {
                    where += " AND (IDCENTRO2LIV IS NULL OR IDCENTRO2LIV=" + bean.getCentro() + ")";
                }
            }
            vo.setWhereClause(where);

            vo.executeQuery();

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

            // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
            if(bean.getZona()!=null){
                Key _k = new Key(new Object[]{bean.getZona()});
                Row[] _r = iter.findByKey(_k, 1);
                
                if(_r==null || _r.length==0){
                    bean.setZona(null);
                }
            }
            // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }
    }

    /**
     * Impostazione degli anni di nascita.
     * @throws java.lang.Exception
     * @param ctx
     */
    protected void prepareModel() {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        //per la RSS l'eta' e' fissa a 60
        if (bean.getEta_da() == 0) {
            bean.setEta_da(ConfigurationConstants.ETA_PRIMO_INV_RSS);
        }
        if (bean.getEta_a() == 0) {
            bean.setEta_a(ConfigurationConstants.ETA_PRIMO_INV_RSS);
        }

    }

    /**
     * Selezione e conteggio della popolazione target in base ai criteri inseriti
     * @param ctx
     */
    public void onFilterPopulation(ActionEvent actionEvent) {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        try {
            this.checkFilters();

            ViewObject target = this.filterSoggettiNEW();
            this.countTarget(target, ConfigurationConstants.CODICE_PRIMO_INVITO_RSS);
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException(ex.getMessage(), am.getTransaction());
        }
    }

    /**
     * Metodo che controlla che i filtri per la selezione della popolazione target
     * siano corretti
     * @throws java.lang.Exception se un filtro non e' corretto
     * @param ctx
     */
    private void checkFilters() throws Exception {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        ViewObject ulss = am.findViewObject("Round_SoAziendaView1");
        String as = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        //controllo validita' filtri
        if (bean.getCentro() == null || bean.getCentro().intValue() < 0)
            throw new Exception("Selezionare il centro di prelievo prima di eseguire la selezione");

        if (bean.getMax_data_richiamo() == null)
            throw new Exception("Impostare la data di esclusione prima di eseguire la selezione");
        /*try {
			DateUtils.DATE_FORMATTER.parse(bean.getMax_data_richiamo());
		} catch(ParseException pex) {
			throw new Exception("La data di esclusione non ha il formato corretto (" + DateUtils.DATE_PATTERN + ")");
		}*/

        Date maxDtRichiamo = bean.getMax_data_richiamo();
        Date oggi = DateUtils.DATE_FORMATTER.parse(DateUtils.getToday());

        if (maxDtRichiamo.compareTo(oggi) < 0) {
            throw new Exception("La data 'Soggetti non esclusi al' non puo' essere anteriore alla data odierna");
        }

        if (bean.getData_riferimento_eta() == null)
            throw new Exception("Impostare la data di riferimento per l'eta' prima di eseguire la selezione");
        /*try {
			DateUtils.DATE_FORMATTER.parse(bean.getData_riferimento_eta());
		} catch (ParseException pex) {
			throw new Exception("La data di riferimento per l'eta' non ha il formato corretto (" + DateUtils.DATE_PATTERN + ")");
		}*/

        Row[] result = ulss.getFilteredRows("Codaz", as);
        if (result.length == 0)
            throw new Exception("Configurazione di screening non trovata per la ulss " + as);

        //eseguo il controllo a seconda del tipo di screening
        if (((Integer) result[0].getAttribute("Etacolonmin")).intValue() > bean.getEta_da())
            throw new Exception("L'eta' minima non puo' essere inferiore a " + result[0].getAttribute("Etacolonmin"));
        if (((Integer) result[0].getAttribute("Etacolonmax")).intValue() < bean.getEta_a())
            throw new Exception("L'eta' massima non puo' essere superiore a " + result[0].getAttribute("Etacolonmax"));
    }

    /**
     * Versione che cerca la popolazione target considerando anche i richiami
     * post-esclusione
     * @throws java.lang.Exception
     * @return
     * @param ctx
     */
    private ViewObject filterSoggettiNEW() throws Exception {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoSoggettiFilteredView4");
        String as = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        //leggo se l'azienda lavora con centri di richiamo preconfigurati sul singolo soggetto
        Boolean cnf_rich = (Boolean) ADFContext.getCurrent().getSessionScope().get("centri_rich_cnf");

        String where =
            GestoreRound.buildPrimiInvitiQueryNEW(tpscr, am.findViewObject("Round_SoCnfTpscrView1"),
                                                  bean.getData_riferimento_eta(),
                                                  bean.getEta_da(), bean.getEta_a(),
                                                  bean.getData_nascita_da(), 
                                                  bean.getData_nascita_a());

        //se c'e' un sesso selezionato dall'utente
        if ("maschi".equals(bean.getSesso()))
            where += "trim(both ' ' from SESSO) ='M' AND ";
        else if ("femmine".equals(bean.getSesso()))
            where += "trim(both ' ' from SESSO) ='F' AND ";

        //se ho selezionato un comune
        if (bean.getComune() != null && bean.getComune().length() > 0 && !bean.getComune().equals("-1")) {
            where +=
                "(Round_SoRoundorg.CODCOM='" + bean.getComune() + "' AND Round_SoRoundorg.RELEASE_CODE_COM=" +
                bean.getRc_comune() + " ) AND ";

            //se ho selezionato anche una zona
            if (bean.getZona() != null && bean.getZona().intValue() > 0)
                //filtro sulla zona
                where += "CODDISTRZONA=" + bean.getZona() + " AND ";
        }

        //se l'azienda non lavora sui centri mi baso su comuni e zone:
        //devo lavorare su tutti i comuni che fanno riferimento al centro (o sulle zone)
        else if (!cnf_rich.booleanValue()) {

            //selezione di tutti i comuni che fanno riferimento al centro di 1 livello
            String comune_select =
                "SELECT C.RELEASE_CODE_COM  FROM SO_CNF_COMUNE C WHERE " + "C.IDCENTRO2LIV=" + bean.getCentro() +
                " AND C.TPSCR='" + tpscr + "'"; // and C.ULSS='" + as + "'";

            //Selezione di tutte le zone che fanno riferimento al centro di 1 livello
            String zona_select =
                "SELECT CODDISTRZONA FROM SO_CNF_DISTRZONA C WHERE " + "C.IDCENTRO2LIV=" + bean.getCentro() +
                " AND C.TPSCR='" + tpscr + "' and C.ULSS='" + as + "'";

            where +=
                "(" + "Round_SoRoundorg.RELEASE_CODE_COM IN (" + comune_select + ") OR " + "CODDISTRZONA IN (" +
                zona_select + ")" + ") AND ";
        }

        //se l'azienda lavora con centri di richiamo configurati per singolo soggetto,
        //filtro sui soggetti che devono essere invitati in quel centro di secondo livello
        if (cnf_rich.booleanValue()) {
            where += " Round_SoSoggScr.IDCENTRO2LIV=" + bean.getCentro() + " AND ";
        }

        if (bean.getMedico() != null && bean.getMedico().intValue() >= 0) {
            where += "Round_SoSoggetto.CODICEREGMEDICO=" + bean.getMedico() + " AND ";
        }

        //filtro sull'indirizzo
        if (bean.getVia() != null && bean.getVia().trim().length() > 0)
            where +=
                "((Round_SoSoggetto.CODANAGREG=" + ConfigurationConstants.CODICE_DOMICILIATO +
                " AND UPPER(INDIRIZZO_DOM) LIKE '%" + bean.getVia().toUpperCase() + "%' " +
                ") OR (Round_SoSoggetto.CODANAGREG<>" + ConfigurationConstants.CODICE_DOMICILIATO +
                " AND UPPER(INDIRIZZO_RES) LIKE '%" + bean.getVia().toUpperCase() + "%')) AND ";

        //se voglio i soggetti mai invitati non ci deve essere nessun appuntamento
        where += "Round_SoInvito.IDINVITO IS NULL AND ";

        if (where.endsWith(" AND "))
            where = where.substring(0, where.length() - 5);

        vo.setWhereClause(where);

        //gestione esclusioni	
        String dtRich = new SimpleDateFormat("dd/MM/yyyy").format(bean.getMax_data_richiamo());
        vo.setWhereClauseParam(6, dtRich);
        vo.setWhereClauseParam(7, dtRich);

        //mi occupo dell'ordinamneto
        Integer ind = new Integer(bean.getPer_indirizzo());

        String s =
            "DECODE(round_sosoggetto.codanagreg,3,round_sosoggetto.codcomdom,round_sosoggetto.codcomres)," +
            "DECODE(round_sosoggetto.codanagreg,3,round_sosoggetto.indirizzo_dom,round_sosoggetto.indirizzo_res)," +
            "DATA_NASCITA,COGNOME,NOME";

        //se non invito per indirizzo
        if (ind == null || ind.intValue() == 0) { //cancello l'ordering imposto, tornando al default
            vo.setOrderByClause(null);
        } else { //sto invitando per indirizzo
            vo.setOrderByClause(s);
        }

        //prima di eseguire la query faccio la tracciatura
        String userParams =
            "ulss=" + as + "; tpscr=" + tpscr + "; centro_prelievo=" + bean.getCentro().toString() + "; comune=" +
            bean.getComune() + "; ord_per_indirizzo=" + bean.getPer_indirizzo() + "; zona_distretto=" + bean.getZona() +
            "; medico=" + bean.getMedico() + "; eta_da=" + bean.getEta_da() + "; eta_a=" + bean.getEta_a() +
            "; compiuti_entro=" + bean.getData_riferimento_eta() + "; non_esclusi_al=" + bean.getMax_data_richiamo();

        String qry = vo.getQuery();
        Object[] params = vo.getWhereClauseParams();

        for (int i = 0; i < params.length; i++) {
            String toReplace = ":" + String.valueOf(i + 1);
            String replaceWith = "'" + params[i] + "'";
            qry = qry.replaceFirst(toReplace, replaceWith);
        }

        int lenQ = qry.length();

        if (lenQ > 8000) {
            qry = qry.substring(0, 7995) + " ..";
            lenQ = 7998;
        }

        String sel1 = "";
        String sel2 = "";

        if (lenQ > 4000) {
            sel1 = qry.substring(0, 4000);
            sel2 = qry.substring(4000);
        } else {
            sel1 = qry;
        }

        if (userParams.length() > 4000) {
            userParams = userParams.substring(0, 4000);
        }

        sel1 = sel1.replaceAll("'", "''");
        sel2 = sel2.replaceAll("'", "''");
        userParams = userParams.replaceAll("'", "''");

        try {
            String insert =
                "INSERT INTO so_messaggi_applicazione (IDMESSAPP,IDMESS,DATA_MESS,ORIGINE,DETTAGLI,DETTAGLI_2,DETTAGLI_3,ULSS) VALUES " +
                "(so_messapp_seq.NEXTVAL,5,sysdate,'Round_invitiAction.java','" + userParams + "','" + sel1 + "','" +
                sel2 + "','" + as + "')";

            am.getTransaction().executeCommand(insert);
            am.getTransaction().commit();

        } catch (Exception ex) {
            logger.severe("Errore durante INSERT INTO so_messaggi_applicazione", ex);
        }
        vo.getEstimatedRowCount();
        vo.executeEmptyRowSet();
        vo.executeQuery();

        //registro dove e' stata fatta ultima query
        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.put("conteggioInviti", "R");

        //imposto il centro per cui e' stato fatto il conteggio
        this.centro_conta = new Integer(bean.getCentro());

        return vo;
    }

    /**
     * Metodo che inserisce nell'apposito viewobject i tipi di inviti da generare con
     * le relative numerosita'
     * @param target viewobject con la popolazione gia' filtrata
     * @param ctx
     * @param defaultType se not null, tutti gli inviti si sintendono del tipo fornito
     */
    private void countTarget(ViewObject target, String defaultType) throws Exception {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();

        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView4");
        ViewObject tpinviti = am.findViewObject("Round_SoCnfTpinvitoView1");

        //cancello dati vecchi
        ViewHelper.clearVO(vo);

        if (defaultType != null) {
            Row[] result = tpinviti.getFilteredRows("Idtpinvito", defaultType);
            if (result.length == 0)
                throw new Exception("Tipo di invito " + defaultType + " non definito per questa azienda sanitaria");
            Row r = vo.createRow();
            vo.insertRow(r);
            r.setAttribute("Idtpinvito", result[0].getAttribute("Idtpinvito"));
            r.setAttribute("Descrizione", result[0].getAttribute("Descrizione"));

            //numero di inviti di quel tipo da generare
            //Integer n = new Integer(target.getEstimatedRowCount());
            Integer n = new Integer(target.getRowCount());
            target.clearCache();
            tpinviti.clearCache();
            r.setAttribute("Numero", n);
            r.setAttribute("DaGenerare", new Integer(0));
        }
    }

    /**
     * Metodo che si occupa della generazione degli inviti
     * @param ctx
     */
    public void onGenerate() {
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
        ViewObject soggetti = am.findViewObject("Round_SoSoggettiFilteredView4");
        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView4");

        //slot, gia' filtrati
        ViewObject slots = am.findViewObject("Round_SoAppuntamentoView1");
        int generati = 0;

        //preparo la classe che generera' gli inviti (con inizializzazione ridotta=false)
        GeneratoreInviti gi = new GeneratoreInviti(am2, false);

        // gestione allegato virtuale
        Integer virt = null;
       // try {
            virt = new Integer(bean.getVirtuale());
        //} catch (SQLException e) {
        //}
        boolean virtuale = (virt != null && virt.intValue() == 1);

        //indica se l'errore e' avvenuto prima di aver generato qualche invito
        boolean before = true;
        try {
            AccessManager.checkPermission("SOGenerazioneInviti");

            //controllo dove e' stata fatta ultima query
            Map sess = ADFContext.getCurrent().getSessionScope();
            String conteggioInviti = (String) sess.get("conteggioInviti");

            if (conteggioInviti == null) {
                throw new Exception("Eseguire il conteggio degli inviti prima di generarli.");
            }

            if (!conteggioInviti.equals("R")) {
                throw new Exception("L'ultimo conteggio degli inviti non e' stato eseguito su 'Primi inviti RSS'. Rieseguire il conteggio.");
            }

            //controllo la correttezza dei dati inseriti e filtro gli slot
            this.checkParams();

            //verifico che il centro impostato sia coerente con quello del conteggio
            if (!this.centro_conta.equals(bean.getCentro())) {
                throw new Exception("Il centro e' stato modificato dopo l'esecuzione del conteggio. Ripristinare il centro originario");
            }

            //per aggiornare i dati dei round
            GestoreRound gr = new GestoreRound(am2);

            //ciclo sulla generazione degli inviti
            Row r = vo.first();

            int n_inviti = ((Integer) r.getAttribute("DaGenerare")).intValue();
            Row slot = slots.first();
            Row sogg = soggetti.first();

            //ciclo sugli slot
            while (slot != null && sogg != null && generati < n_inviti) {
                int x =
                    ((Integer) slot.getAttribute("Dispslot")).intValue() -
                    ((Integer) slot.getAttribute("Slotoccup")).intValue();
                for (int i = 0; i < x; i++) {
                    if (generati >= n_inviti || sogg == null)
                        break;

                    //leggo un soggetto e determino il comune di riferimento
                    Integer comune;
                    if (ConfigurationConstants.CODICE_DOMICILIATO.equals((Integer) sogg.getAttribute("Codanagreg")))
                        //il comune di riferimento e' il domicilio
                        comune = (Integer) sogg.getAttribute("ReleaseCodeComDom");
                    else
                        comune = (Integer) sogg.getAttribute("ReleaseCodeComRes");

                    if (comune == null)
                        throw new Exception("Impossibile trovare il comune di riferimento per il soggetto con tessera " +
                                            (String) sogg.getAttribute("Codts"));

                    //genero l'invito
                    //se true, incremento generati
                    if (gi.generaInvito(ulss, tpscr, user, (String) r.getAttribute("Idtpinvito"),
                                        (Integer) slot.getAttribute("Idapp"), (String) sogg.getAttribute("Codts"),
                                        (Integer) sogg.getAttribute("Idinvito"), //se non c'e' un invito attivo e' null
                                        bean.getData_riferimento_eta(), (Integer) slot.getAttribute("Idcentro"),
                                        virtuale)) {
                        generati++;
                        before = false;

                        //aggiorno la data del primo e ultimo invito per il comune di riferimento
                        gr.updateRoundData(tpscr, comune, (oracle.jbo.domain.Date) slot.getAttribute("Dtapp"),
                                           gr.DTPRIMOINVITO);
                        gr.updateRoundData(tpscr, comune, (oracle.jbo.domain.Date) slot.getAttribute("Dtapp"),
                                           gr.DTULTINVITO);
                    }

                    //comunque
                    sogg = soggetti.next();
                }
                slot = slots.next();
            }

            //aggiorno lo stato dell'ultimo invito per il comune
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException("Errori nella generazione degli inviti: " + ex.getMessage(), null);
        } finally {

            //rieseguo il filtro perche' la visualizzazione sia consistente, ma solo se ho generato qualcosa
            if (!before)
                this.onFilterPopulation(null);

            slots.clearCache();
            soggetti.clearCache();
            gi.releaseLogo();

            ADFContext adfCtx = ADFContext.getCurrent();
            String m =
                MessageBundle.getMessageFromApplicationMessageBundle(adfCtx.getLocale(), "view.round.message_Bundle",
                                                                     "ROUND_INVITI_GEN_MSG", true);

            if(FacesContext.getCurrentInstance().getMaximumSeverity()==null || FacesContext.getCurrentInstance().getMaximumSeverity().equals(FacesMessage.SEVERITY_INFO))
                this.handleMessages(FacesMessage.SEVERITY_INFO, MessageFormat.format(m, generati));
        }
    }

    private void checkParams() throws Exception {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        //la data di inizio generazione
        if (bean.getGenerate_dal() == null)
            throw new Exception("La data di inizio del periodo da generare e' obbligatoria");
        try {
            //formato + l'inizio deve essere non prima di domani
            if (bean.getGenerate_dal().before(new Date()))
                throw new Exception("La generazione degli inviti fino alla data corrente non e' un'operazione consentita.");
        } catch (ParseException pex) {
            throw new Exception("La data di inizio del periodo non corrisponde al formato dd/MM/yyyy");
        }

        try {
            //formato, se non null (non e' obbligatoria)
            if (bean.getGenerate_al() != null && bean.getGenerate_al().before(new Date()))
                throw new Exception("La generazione degli inviti fino alla data corrente non e' un'operazione consentita.");
        } catch (ParseException pex) {
            throw new Exception("La data di fine del periodo non corrisponde al formato dd/MM/yyyy");
        }

        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView4");

        Row r = vo.first();
        if (r == null)
            throw new Exception("Non e' stato eseguito nessun calcolo in base la quale generare gli inviti");

        //non possono esserci inviti diversi dai primi
        if (!((String) r.getAttribute("Idtpinvito")).equals(ConfigurationConstants.CODICE_PRIMO_INVITO_RSS))
            throw new Exception("Non e' stato eseguito nessun calcolo in base la quale generare gli inviti");

        //controllo che l'agenda fisica del centro presenti degli slot
        ViewObject slot = am.findViewObject("Round_SoAppuntamentoView1");
        String where =
            "TPSRC='" + tpscr + "' AND IDCENTRO=" + bean.getCentro() + " AND DTAPP>=TO_DATE('" +
            new SimpleDateFormat("dd/MM/yyyy").format(bean.getGenerate_dal()) + "','" + DateUtils.DATE_PATTERN + "')";
        if (bean.getGenerate_al() != null)
            where +=
                " AND DTAPP<TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getGenerate_al()) + "','" +
                DateUtils.DATE_PATTERN + "')";

        // controllo filtro per orario e lo metto in query
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

            where += " AND ( ( ORAAPP > " + hh_da + ") OR " + "( ORAAPP = " + hh_da + " AND MINAPP >= " + mi_da + ") )";
        }

        if ((ora_a != null) && !ora_a.equals("")) {
            String hh_a = ora_a.substring(0, 2);
            String mi_a = ora_a.substring(3, 5);

            where += " AND ( ( ORAAPP < " + hh_a + ") OR " + "( ORAAPP = " + hh_a + " AND MINAPP <= " + mi_a + ") )";
        }

        slot.setWhereClause(where);
        slot.setForwardOnly(true);
        slot.executeQuery();
        if (slot.first() == null)
            throw new Exception("L'agenda fisica per il periodo richiesto non e' stata generata");
    }
}
