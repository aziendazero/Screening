package view.round;


import insiel.dataHandling.DateUtils;

import java.math.BigDecimal;

import java.sql.SQLException;

import java.text.MessageFormat;
import java.text.ParseException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
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

import model.util.MessageBundle;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.invito.DistributoreInviti;
import view.invito.GestoreRound;

public class Round_invitiAction extends Round_DataForwardAction {
    private static ADFLogger logger = ADFLogger.createADFLogger(Round_invitiAction.class);

    //20110407 Serra:variabile che memorizza il centro per cui e' stato fatto un conteggio
    private Integer centro_conta;
    //fine 20110407

    protected String getElenco_voName() {
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        if (hpvAttivo.booleanValue()) {
            return "Round_SoConteggiInvitiView1";
        }
        return "Round_SoSoggettiFilteredView1";
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
     * Se cambio modalita di chiamata devo resettare/filtrare i medici
     * @param ctx
     */
    public void onChangeChiamata(ValueChangeEvent valueChangeEvent) {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

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
     * una zona associata a quel centro.
     * @param ctx
     */
    private static void selectCentro() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        ViewObject vo = am.findViewObject("Round_SoComuniByCentri1");
        RowSetIterator iter = null;
        try {
            String where = vo.getWhereClause();
            if (where != null && where.length() > 0 && where.indexOf("(IDCENTRO1LIV") >= 0) {
                //elimino la query precedente, se e' stata fatta (ma devo matenere la
                //query iniziale per scremare per ulss e tpscr)
                where = where.substring(0, where.indexOf("(IDCENTRO1LIV"));
            }

            // Tolgo eventuale AND finale
            if (where != null && where.trim().endsWith("AND"))
                where = where.substring(0, where.lastIndexOf(" AND"));

            // Se modalita' chiamata per medico, non devo filtrare i comuni
            if (!"Per medico".equals(bean.getMod_chiamata())) {

                //se centro=-1 e' corretto che nulla venga visualizzato
                if (bean.getCentro() == null) {
                    where += " AND (IDCENTRO1LIV=-1)";
                } else {
                    where += " AND (IDCENTRO1LIV=" + bean.getCentro() + ")";
                }
            }

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
            if (iter != null){
                iter.closeRowSetIterator();
            }
        }
    }

    /**
     * In seguito alla selezione di un centro e un comune scremo le zone per vedere
     * quelle associate al comune che sono prive di centro di 1 livello
     * o che hanno il centro di 1 livello selezionato.
     * @param ctx
     */
    private static void selectComune() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        //MOD 20090511 (eliminazione duplicati zone)
        RowSetIterator iter = null;
        //FINE MOD

        try {
            ViewObject vo = am.findViewObject("Round_SoZoneByComuniCentri1");

            String where = vo.getWhereClause();
            if (where != null && where.length() > 0 && where.indexOf("CODCOM=") >= 0) {
                //elimino la query precedente, se e' stata fatta (ma devo mantenere la
                //query iniziale per scremare per ulss e tpscr)
                where = where.substring(0, where.indexOf("CODCOM="));
            }
            if (where != null && where.trim().endsWith("AND"))
                where = where.substring(0, where.lastIndexOf(" AND"));

            if (where != null && where.length() > 0)
                where += " AND ";
            else
                where = "";

            //se comune = -1 e' corretto che nulla venga visualizzato
            if (bean.getComune() == null) {
                where += "CODCOM='-1'";
            } else {
                where += "CODCOM='" + bean.getComune() + "' AND RELEASE_CODE_COM=" + bean.getRc_comune();
            }
            
            if (!bean.getMod_chiamata().equals("Per medico")) {
                where += " AND (IDCENTRO1LIV IS NULL OR IDCENTRO1LIV=";
    
                //se centro = -1 e' corretto che nulla venga visualizzato
                if (bean.getCentro() == null) {
                    where += "-1)";
                } else {
                    where += bean.getCentro() + ")";
                }
            }
            
            vo.setWhereClause(where);
            vo.executeQuery();
            
            //MOD 20090511
            //filtro di doppioni (zone che rientrano sia perche' associati al centro,
            //sia perche' associate a uno o piu' comuni associati a quel centro)
            iter = ViewHelper.getRowSetIterator(vo, "zone_iter");
            HashMap hash = new HashMap();
            while (iter.hasNext()) {
                Row r = iter.next();
                Object o = hash.get(r.getAttribute("Coddistrzona"));
                //se questo comune non e' gia' presente lo aggiungo
                if (o == null)
                    hash.put(r.getAttribute("Coddistrzona"), Boolean.TRUE);
                //se c'e' gia' lo elimino solo dal view object
                else
                    r.removeFromCollection();
            }
            //FINE MOD

            // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
            if(bean.getZona()!=null){
                Key _k = new Key(new Object[]{bean.getZona()});
                Row[] _r = iter.findByKey(_k, 1);
                
                if(_r==null || _r.length==0){
                    bean.setZona(null);
                }
            }
            // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE

            // Se selezione per medico, devo
            // - filtrare i medici per centro (se e' selezionato un centro), altrimenti la lista deve essere vuota
            // - togliere il filtro sui comuni
            if (bean.getMod_chiamata().equals("Per medico")) {
                vo = am.findViewObject("Round_SoMediciByCentriView1");
                where = vo.getWhereClause();
                if (where != null && where.length() > 0 && where.indexOf("(IDCENTRO") >= 0) {
                    //elimino la query precedente, se e' stata fatta (ma devo matenere la
                    //query iniziale per scremare per ulss )
                    where = where.substring(0, where.indexOf("(IDCENTRO"));
                }
                if (where != null && where.trim().endsWith("AND"))
                    where = where.substring(0, where.lastIndexOf(" AND"));

                if (where != null && where.length() > 0)
                    where += " AND ";
                else
                    where = "";

                //se centro = -1 e' corretto che nulla venga visualizzato
                if (bean.getCentro() == null) {
                    where += "(IDCENTRO=-1)";
                } else {
                    where += "(IDCENTRO=" + bean.getCentro() + ")";
                }
                vo.setWhereClause(where);
                vo.executeQuery();

                // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
                /*if(bean.getMedico()!=null){
                    Key _k = new Key(new Object[]{bean.getMedico(), bean.});
                    RowSetIterator _iter = ViewHelper.getRowSetIterator(vo, "medici_iter");
                    Row[] _r = _iter.findByKey(_k, 1);
                    
                    if(_r==null || _r.length==0){
                        bean.setMedico(null);
                    }
                    
                    _iter.closeRowSetIterator();
                }*/
                // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE


                // Tolgo il filtro sul comune
                /*
                vo = am.findViewObject("Round_SoComuniByCentri1");

                where = vo.getWhereClause();
                if (where != null && where.length() > 0 && where.indexOf("(IDCENTRO1LIV") >= 0) {
                    //elimino la query precedente, se e' stata fatta (ma devo matenere la
                    //query iniziale per scremare per ulss e tpscr)
                    where = where.substring(0, where.indexOf("(IDCENTRO1LIV"));
                }

                // Tolgo eventuale AND finale
                if (where != null && where.trim().endsWith(" AND"))
                    where = where.substring(0, where.lastIndexOf(" AND"));
                */
            } else {
                //resetto il filtro per centro
                vo = am.findViewObject("Round_SoMediciByCentriView1");
                where = vo.getWhereClause();
                if (where != null && where.length() > 0 && where.indexOf("(IDCENTRO") >= 0) {
                    //elimino la query precedente, se e' stata fatta (ma devo matenere la
                    //query iniziale per scremare per ulss )
                    where = where.substring(0, where.indexOf("(IDCENTRO"));
                }
                if (where != null && where.trim().endsWith("AND"))
                    where = where.substring(0, where.lastIndexOf(" AND"));

                vo.setWhereClause(where);
                vo.executeQuery();

                // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO
                /*if(bean.getMedico()!=null){
                    Key _k = new Key(new Object[]{bean.getMedico()});
                    RowSetIterator _iter = ViewHelper.getRowSetIterator(vo, "medici_iter");
                    Row[] _r = _iter.findByKey(_k, 1);
                    
                    if(_r==null || _r.length==0){
                        bean.setMedico(null);
                    }
                    
                    _iter.closeRowSetIterator();
                }*/
                // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE
            }
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

    /**
     * Selezione e conteggio della popolazione target in base ai criteri inseriti
     * @param ctx
     */
    public void onFilterPopulation(ActionEvent actionEvent) {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        // Round_invitiBean bean = (Round_invitiBean)BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        try {
            this.checkFilters();
            //ViewObject target = this.filterSoggetti();
            ViewObject target = this.filterSoggettiNEW();
            this.countTarget(target, ConfigurationConstants.CODICE_PRIMO_INVITO);
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
            throw new Exception("Impostare la data di invito previsto prima di eseguire la selezione");
        /*try {
            DateUtils.DATE_FORMATTER.parse(bean.getMax_data_richiamo());
        } catch (ParseException pex) {
            throw new Exception("La data di invito previsto non ha il formato corretto (" + DateUtils.DATE_PATTERN +
                                ")");
        }*/

        Date maxDtRichiamo = bean.getMax_data_richiamo();
        Date oggi = DateUtils.DATE_FORMATTER.parse(DateUtils.getToday());

        if (maxDtRichiamo.compareTo(oggi) < 0) {
            throw new Exception("La data 'invito previsto prima del' non puo' essere anteriore alla data odierna");
        }

        if (bean.getData_riferimento_eta() == null)
            throw new Exception("Impostare la data di riferimento per l'eta' prima di eseguire la selezione");
        /*try {
            DateUtils.DATE_FORMATTER.parse(bean.getData_riferimento_eta());
        } catch (ParseException pex) {
            throw new Exception("La data di riferimento per l'eta' non ha il formato corretto (" +
                                DateUtils.DATE_PATTERN + ")");
        }*/

        Row[] result = ulss.getFilteredRows("Codaz", as);
        if (result.length == 0)
            throw new Exception("Configurazione di screening non trovata per la ulss " + as);

        //eseguo il controllo a seconda del tipo di screening
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

        if ("CI".equals(tpscr) || "CO".equals(tpscr) || "MA".equals(tpscr)){
              if (((Integer)result[0].getAttribute(nome1)).intValue() > bean.getEta_da())
                throw new Exception("L'eta' minima non puo' essere inferiore a " + result[0].getAttribute(nome1));
              if (((Integer)result[0].getAttribute(nome2)).intValue() < bean.getEta_a())
                throw new Exception("L'eta' massima non puo' essere superiore a " + result[0].getAttribute(nome2));
                
              if (bean.getData_riferimento_eta() == null)
                throw new Exception("Impostare la data di riferimento per l'eta' prima di eseguire la selezione");
            } else 
            {
              //la popolazione si basa sulla data di nascita per CA e PF.
              if (((oracle.jbo.domain.Date)result[0].getAttribute(nome1)).getValue().compareTo(bean.getData_nascita_da()) > 0)
                throw new Exception("La data di nascita non puo' essere inferiore a " + DateUtils.dateToString((oracle.jbo.domain.Date)result[0].getAttribute(nome1)));
              if (((oracle.jbo.domain.Date)result[0].getAttribute(nome2)).getValue().compareTo(bean.getData_nascita_a()) < 0)
                throw new Exception("La data di nascita non puo' essere superiore a " + DateUtils.dateToString((oracle.jbo.domain.Date)result[0].getAttribute(nome2)));
            }
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
        ViewObject vo = am.findViewObject("Round_SoSoggettiFilteredView1");
        String as = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        // Recupero parametro per sapere se consentire piu' inviti nello stesso round
        boolean ignoraRoundComune = false;
        //03122014 modifiche per gestire hpv-dna
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        Integer etaHpvMin = new Integer(0);
        Integer etaHpvMax = new Integer(0);

        //recupero la configurazione dei primi inviti per capire se prevede test hpv
        ViewObject tpinviti = am.findViewObject("Round_SoCnfTpinvitoView1");
        Row[] tipoInvito = tpinviti.getFilteredRows("Idtpinvito", ConfigurationConstants.CODICE_PRIMO_INVITO);
        Integer hpvTipoInvito = null;
        if (tipoInvito.length > 0) {
            hpvTipoInvito = (Integer) tipoInvito[0].getAttribute("Hpv");
        }

        ViewObject parametriView = am.findViewObject("A_SoCnfParametriSistemaView1");
        String where = "ulss = '" + as + "' AND tpscr = '" + tpscr + "' AND nome_param = 'ignora_round_comune'";
        parametriView.setWhereClause(where);
        parametriView.executeQuery();
        Row parametriRow = parametriView.next();
        if (parametriRow != null) {
            ignoraRoundComune = "S".equals(parametriRow.getAttribute("ValoreParam"));
        }

        if ("CI".equals(tpscr) && hpvAttivo.booleanValue()) {
            vo = am.findViewObject("Round_SoSoggettiFilteredHPVView1");
            //ricavo fascia di eta' per l’eleggibilita' allo screening HPV definita in SO_AZIENDA
            ViewObject voConf = am.findViewObject("Round_SoAziendaView1");
            Row[] result = voConf.getFilteredRows("Codaz", as);
            etaHpvMin = (Integer) result[0].getAttribute("Etahpvmin");
            etaHpvMax = ((Integer) result[0].getAttribute("Etahpvmax")) + 1;
            if (etaHpvMin == null || etaHpvMax == null) {
                throw new Exception("Fascia di eta' per l’eleggibilita' allo screening HPV non definita");
            }
        }

        if ("aderenti al round precedente".equals(bean.getChiInvitare())) {
            ignoraRoundComune = false;
        }
        if ("NON aderenti al round precedente".equals(bean.getChiInvitare())) {
            ignoraRoundComune = false;
        }
        if ("esclusi al round precedente".equals(bean.getChiInvitare())) {
            ignoraRoundComune = false;
        }

        //leggo se l'azienda lavora con centri di richiamo preconfigurati sul singolo soggetto
        Boolean cnf_rich = (Boolean) ADFContext.getCurrent().getSessionScope().get("centri_rich_cnf");

        where =
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
                bean.getRc_comune() + ") AND ";

            //se ho selezionato anche una zona
            if (bean.getZona() != null && bean.getZona().intValue() > 0)
                //filtro sulla zona
                where += "CODDISTRZONA=" + bean.getZona() + " AND ";
        }

        //se l'azienda non lavora sui centri mi baso su comuni e zone:
        //devo lavorare su tutti i comuni che fanno riferimento al centro (o sulle zone)
        else if (!cnf_rich.booleanValue() && !"Per medico".equals(bean.getMod_chiamata())) {

            //selezione di tutti i comuni che fanno riferimento al centro di 1 livello
            String comune_select =
                "SELECT C.RELEASE_CODE_COM  FROM SO_CNF_COMUNE C WHERE " + "C.IDCENTRO1LIV=" + bean.getCentro() +
                " AND C.TPSCR='" + tpscr + "'"; // and C.ULSS='" + as + "'";

            //Selezione di tutte le zone che fanno riferimento al centro di 1 livello
            String zona_select =
                "SELECT CODDISTRZONA FROM SO_CNF_DISTRZONA C WHERE " + "C.IDCENTRO1LIV=" + bean.getCentro() +
                " AND C.TPSCR='" + tpscr + "' and C.ULSS='" + as + "'";

            where +=
                "(" + "Round_SoRoundorg.RELEASE_CODE_COM IN (" + comune_select + ") OR " + "CODDISTRZONA IN (" +
                zona_select + ")" + ") AND ";
        }

        //se l'azienda lavora con centri di richiamo configurati per singolo soggetto,
        //filtro sui soggetti che devono essere invitati in quel centro di primo livello
        if (cnf_rich.booleanValue() && !"Per medico".equals(bean.getMod_chiamata())) { //sono primi inviti, il centro di riferimento e' sempre quello di primo livello
            where += " Round_SoSoggScr.IDCENTRO1LIV=" + bean.getCentro() + " AND ";
        }

        if (bean.getMedico() != null && bean.getMedico().intValue() >= 0) {
            where += "Round_SoSoggetto.CODICEREGMEDICO=" + bean.getMedico() + " AND ";
        }

        //cardiovascolare
        else if (bean.getMod_chiamata().equals("Per medico")) {
            //si filtra sui medici associati al centro
            ViewObject mecidiView = am.findViewObject("Round_SoMediciByCentriView1");

            RowSetIterator iter = ViewHelper.getRowSetIterator(mecidiView, "medici_iter");
            String filtriMedici = "";
            while (iter.hasNext()) {
                Row r = iter.next();
                filtriMedici += ((Integer)r.getAttribute("Codiceregmedico")).toString() + ",";
            }
            if (!"".equals(filtriMedici)) {
                filtriMedici = filtriMedici.substring(0, filtriMedici.lastIndexOf(","));
                where += "Round_SoSoggetto.CODICEREGMEDICO IN (" + filtriMedici + ") AND ";
            }
        }

        //filtro sull'indirizzo
        if (bean.getVia() != null && bean.getVia().trim().length() > 0)
            where += "((Round_SoSoggetto.CODANAGREG=" + ConfigurationConstants.CODICE_DOMICILIATO +
                                            " AND UPPER(Round_SoSoggetto.INDIRIZZO_DOM) LIKE '%" + bean.getVia().toUpperCase() + "%' " +
                                            ") OR (Round_SoSoggetto.CODANAGREG<>" + ConfigurationConstants.CODICE_DOMICILIATO +
                                            " AND UPPER(Round_SoSoggetto.INDIRIZZO_RES) LIKE '%" + bean.getVia().toUpperCase() + "%')) AND ";
        
        //se non c'e' l'invito e nemmeno la data di richiamo e' un soggetto mai invitato e mai escluso,
        //altrimenti ho almeno un invito o un'esclusione e il richiamo deve rispettare i filtri
        where +=
            "((Round_SoInvito.IDINVITO IS NULL and Round_SoSoggScr.DTRICHIAMO is null) OR " + "( " +
            //data di richiamo inferiore a quella impostata
            "Round_SoSoggScr.DTRICHIAMO<TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getMax_data_richiamo()) + "','" + DateUtils.DATE_PATTERN +
       "') AND " +
       //il richiamo deve essere un primo invito
       "Round_SoSoggScr.TPRICHIAMO='" + ConfigurationConstants.CODICE_PRIMO_INVITO + "')) AND ";

        //se voglio quelli saltati al round precedente,
        //allora l'ultimo round deve essere minore del precedente
        String between;
        if ("non invitati al round precedente".equals(bean.getChiInvitare())) {
            between = "-1";
            ignoraRoundComune = false;
        } else {
            between = "";
        }

        if (!ignoraRoundComune) {
            // mauro 29-10-2009, modifica
            where +=
                "( Round_SoRoundorg.RELEASE_CODE_COM <> DECODE(Round_SoInvito.statoanag, 3, Round_SoInvito.RELEASE_CODE_COM_DOM, Round_SoInvito.RELEASE_CODE_COM_RES) OR " +
                "Round_SoInvito.ROUNDCOMUNE IS NULL OR " + "Round_SoInvito.ROUNDCOMUNE<Round_SoRoundorg.NUMROUND" +
                between + ") AND ";
            // mauro 29-10-2009, fine modifica
        }

        //se voglio i soggetti mai invitati non ci deve essere nessun appuntamento
        if ("mai invitati".equals(bean.getChiInvitare()))
            where += "Round_SoInvito.IDINVITO IS NULL AND ";

        //se voglio i soggetti da richiamare ci deve essere almeno un appuntamento
        if ("da richiamare".equals(bean.getChiInvitare()))
            where += "Round_SoInvito.IDINVITO IS NOT NULL AND ";

        //se voglio i soggetti che hanno aderito al round precedente allora
        //deve esistere un invito del round di quello attivo che ha esito=S
        if ("aderenti al round precedente".equals(bean.getChiInvitare()))
            where +=
                "(Round_SoInvito.IDINVITO IS NOT NULL AND EXISTS (SELECT * FROM SO_INVITO S " +
                "WHERE S.CODTS=Round_SoSoggetto.CODTS AND S.ULSS=Round_SoSoggetto.ULSS AND S.TPSCR=Round_SoCnfAnagScr.TPSCR " +
                "AND S.ROUNDCOMUNE=Round_SoInvito.ROUNDCOMUNE AND S.CODESITOINVITO='" +
                ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO + "')) AND ";

        //se voglio i soggetti che NON hanno aderito al round precedente allora
        //deve esistere un invito del round di quello attivo che ha esito!=S
        if ("NON aderenti al round precedente".equals(bean.getChiInvitare()))
            where +=
                "(Round_SoInvito.IDINVITO IS NOT NULL and Round_SoInvito.ROUNDCOMUNE=Round_SoRoundorg.NUMROUND-1 " +
                " AND NOT EXISTS (SELECT * FROM SO_INVITO S " +
                "WHERE S.CODTS=Round_SoSoggetto.CODTS AND S.ULSS=Round_SoSoggetto.ULSS AND S.TPSCR=Round_SoCnfAnagScr.TPSCR " +
                "AND S.ROUNDCOMUNE=Round_SoInvito.ROUNDCOMUNE AND S.CODESITOINVITO='" +
                ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO + "')) AND ";

        if ("esclusi al round precedente".equals(bean.getChiInvitare())) {
            ViewObject help_vo = am.findViewObject("Round_SoEsclusioneView1");
            String lista_ex_escluse = "SELECT CODTS FROM (" + help_vo.getQuery() +
                //l'esclusione e' stata registrata in un round precedente
                ") ESCL WHERE ESCL.ROUNDORGESCL<Round_SoRoundorg.NUMROUND AND " +
                //caso 1. si tratta di un'esclusion definitiva, annullata nel round in corso
                "((TPESCL='D' AND DTANNULL>=Round_SoRoundorg.DTINIZIO) OR " +
                //caso 2. esclusione temporanea che scade in questo round (se non e' gia' scaduta
                //la signora e' comunque eliminata dal filtro sulle esclusioni)
                "(TPESCL='T' AND DTESCL+NUMGIORNI>=Round_SoRoundorg.DTINIZIO AND DTANNULL IS NULL) OR " +
                //caso 3. esclusione temporanea annullata in questo round
                "(TPESCL='T' AND DTANNULL>=Round_SoRoundorg.DTINIZIO))";
            where += "Round_SoSoggetto.CODTS IN (" + lista_ex_escluse + ") AND ";
        }

        if (where.endsWith(" AND "))
            where = where.substring(0, where.length() - 5);

        vo.setWhereClause(where);

        // mauro 16/10/2009 modifiche per gestione esclusione
        String dtRich = new SimpleDateFormat("dd/MM/yyyy").format(bean.getMax_data_richiamo());

        if (hpvAttivo.booleanValue()) {
            vo.setWhereClauseParam(13, dtRich);
            vo.setWhereClauseParam(14, dtRich);

            vo.setWhereClauseParam(3, bean.getData_riferimento_eta());
            vo.setWhereClauseParam(4, etaHpvMax);
            vo.setWhereClauseParam(5, bean.getData_riferimento_eta());
            vo.setWhereClauseParam(6, etaHpvMin);

            vo.setWhereClauseParam(0, hpvTipoInvito);
            vo.setWhereClauseParam(1, hpvTipoInvito);
            vo.setWhereClauseParam(2, hpvTipoInvito);

            //svuoto il vo non usato
            ViewObject vo2 = am.findViewObject("Round_SoSoggettiFilteredView1");
            vo2.setWhereClause("1=2");
            vo2.executeQuery();

        } else {
            
            vo.setWhereClauseParam(0, tpscr);
            vo.setWhereClauseParam(1, tpscr);
            vo.setWhereClauseParam(2, as);
            vo.setWhereClauseParam(3, tpscr);
            vo.setWhereClauseParam(4, tpscr);
            vo.setWhereClauseParam(5, as);
            vo.setWhereClauseParam(6, dtRich);
            vo.setWhereClauseParam(7, dtRich);
            vo.setWhereClauseParam(8, tpscr);

            //svuoto il vo non usato
            ViewObject vo2 = am.findViewObject("Round_SoConteggiInvitiView1");
            vo2.setWhereClause("1=2");
            vo2.executeQuery();
        }

        //mi occupo dell'ordinamneto
        Integer ind = bean.getPer_indirizzo();

        String s =
            "DECODE(round_sosoggetto.codanagreg,3,round_sosoggetto.codcomdom,round_sosoggetto.codcomres)," +
            "DECODE(round_sosoggetto.codanagreg,3,round_sosoggetto.indirizzo_dom,round_sosoggetto.indirizzo_res)," +
            "NVL(Round_SoSoggScr.dtrichiamo, TO_DATE('01/01/1900','dd/MM/yyyy')),DATA_NASCITA,COGNOME,NOME";

        //se non invito per indirizzo
        if (ind == null || ind.intValue() == 0) {
            if (tpscr.equals("PF")) {
                //odinamento per data nascita decrescente
                s = "DATA_NASCITA DESC,NVL(Round_SoSoggScr.dtrichiamo, TO_DATE('01/01/1900','dd/MM/yyyy')),COGNOME,NOME";
                vo.setOrderByClause(s);
            } else {
                //cancello l'ordering imposto, tornando al default
                vo.setOrderByClause(null);
            }
        } else { //sto invitando per indirizzo
            vo.setOrderByClause(s);
        }

        // mauro 08/02/2010, prima di eseguire la query faccio la tracciatura
        String userParams =
            "ulss=" + as + "; tpscr=" + tpscr + "; centro_prelievo=" + bean.getCentro().toString() + "; comune=" +
            bean.getComune() + "; ord_per_indirizzo=" + bean.getPer_indirizzo() + "; zona_distretto=" + bean.getZona() +
            "; medico=" + bean.getMedico() + "; eta_da=" + bean.getEta_da() + "; eta_a=" + bean.getEta_a() +
            "; compiuti_entro=" + bean.getData_riferimento_eta() + "; invito_prima_del=" + bean.getMax_data_richiamo() +
            "; soggetti=" + bean.getChiInvitare();

        String qry = vo.getQuery();
        Object[] params = vo.getWhereClauseParams();

        for (int i = 0; i < params.length; i++) {
            String toReplace = ":" + String.valueOf(i + 1);
            Object _o = params[i]!=null ? ((Object[])params[i])[1] : null;
            String replaceWith = "'" + (_o instanceof Date ? new SimpleDateFormat("dd/MM/yyyy").format((Date)_o) : _o )  + "'";
            qry = qry.replaceFirst(toReplace, replaceWith);
        }

        // mauro 24/08/2010, modifica algoritmo per sudd. query
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

        // mauro 24/08/2010, fine modifica

        // Massimo Matassoni 09/10/2013
        // Spostata la replace dopo il troncamento a 4000 caratteri,
        // perche' il doppio apice nel db occupa un solo carattere.
        // Inoltre risolve il problema del caso in cui il troncamento
        // avviene tra i due apici di un doppio apice.
        sel1 = sel1.replaceAll("'", "''");
        sel2 = sel2.replaceAll("'", "''");
        userParams = userParams.replaceAll("'", "''");

        try {
            // mauro 10/03/2010, aggiunta ulss
            String insert =
                "INSERT INTO so_messaggi_applicazione (IDMESSAPP,IDMESS,DATA_MESS,ORIGINE,DETTAGLI,DETTAGLI_2,DETTAGLI_3,ULSS) VALUES " +
                "(so_messapp_seq.NEXTVAL,5,sysdate,'Round_invitiAction.java','" + userParams + "','" + sel1 + "','" +
                sel2 + "','" + as + "')";
            // mauro 10/03/2010, fine

            am.getTransaction().executeCommand(insert);
            am.getTransaction().commit();
            // mauro 08/02/2010, fine
        } catch (Exception ex) {
            logger.severe("Errore durante INSERT INTO so_messaggi_applicazione", ex);
        }

        if (hpvAttivo.booleanValue()) {
            if (lenQ > 4000) {
                sel1 = qry.substring(0, 4000);
                sel2 = qry.substring(4000);
            } else {
                sel1 = qry;
            }
            //chiamo il package CONTEGGI_INVITI
            Integer id_query_old = (Integer) ADFContext.getCurrent().getSessionScope().get("id_query_old");
            if (id_query_old == null)
                id_query_old = new Integer(0);
            Integer id_query =
                am.callContaInviti(as, tpscr, sel1, sel2, new BigDecimal(id_query_old.intValue()),
                                   ConfigurationConstants.CODICE_PRIMO_INVITO);
            if (id_query.intValue() < 0)
                throw new Exception("Errore nella procedura di conteggio inviti");
            vo = am.findViewObject("Round_CountSoConteggioInvitiView1");
            vo.setWhereClauseParam(0, id_query);
            vo.executeQuery();

            ADFContext.getCurrent().getSessionScope().put("id_query_old", id_query);
        } else {           
            vo.executeQuery();
        }

        // mauro 27/05/2010, registro dove e' stata fatta ultima query
        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.put("conteggioInviti", "P");
        // mauro 27/05/2010, fine

        //20110407 Serra: imposto il centro per cui e' stato fatto il conteggio
        this.centro_conta = bean.getCentro();
        //fine 20110407

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
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");

        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView1");
        ViewObject tpinviti = am.findViewObject("Round_SoCnfTpinvitoView1");

        //cancello dati vecchi
        ViewHelper.clearVO(vo);

        if (defaultType != null && target.getEstimatedRowCount() > 0) {
            if (hpvAttivo.booleanValue()) {

                Row[] tipi = tpinviti.getFilteredRows("Idtpinvito", defaultType);
                if (tipi.length == 0)
                    throw new Exception("Tipo di invito " + defaultType + " non definito per questa azienda sanitaria");
                //conto i pap test proposti
                Row[] result = target.getFilteredRows("TestProposto", "PAP");
                Row r = vo.createRow();
                vo.insertRow(r);
                r.setAttribute("Idtpinvito", tipi[0].getAttribute("Idtpinvito"));
                r.setAttribute("Descrizione", tipi[0].getAttribute("Descrizione"));
                r.setAttribute("TipoTest", "PAP");
                r.setAttribute("DaGenerare", 0);
                if (result.length > 0) {
                    //numero di inviti di quel tipo da generare
                    r.setAttribute("Numero", result[0].getAttribute("Numero"));
                } else {
                    r.setAttribute("Numero", 0);
                }

                // conto hpv proposti
                vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView5");
                ViewHelper.clearVO(vo);
                Row[] resultHPV = target.getFilteredRows("TestProposto", "HPV");
                Row rHPV = vo.createRow();
                vo.insertRow(rHPV);
                rHPV.setAttribute("Idtpinvito", tipi[0].getAttribute("Idtpinvito"));
                rHPV.setAttribute("Descrizione", tipi[0].getAttribute("Descrizione"));
                rHPV.setAttribute("TipoTest", "HPV");
                rHPV.setAttribute("DaGenerare", 0);
                if (resultHPV.length > 0) {
                    //numero di inviti di quel tipo da generare
                    rHPV.setAttribute("Numero", resultHPV[0].getAttribute("Numero"));
                } else {
                    rHPV.setAttribute("Numero", 0);
                }

                target.clearCache();
                tpinviti.clearCache();

            } else {
                Row[] result = tpinviti.getFilteredRows("Idtpinvito", defaultType);
                if (result.length == 0)
                    throw new Exception("Tipo di invito " + defaultType + " non definito per questa azienda sanitaria");
                Row r = vo.createRow();
                vo.insertRow(r);
                r.setAttribute("Idtpinvito", result[0].getAttribute("Idtpinvito"));
                r.setAttribute("Descrizione", result[0].getAttribute("Descrizione"));

                //numero di inviti di quel tipo da generare
                Integer n = target.getRowCount();
                target.clearCache();
                tpinviti.clearCache();
                r.setAttribute("Numero", n);
                r.setAttribute("DaGenerare", 0);
            }
        } else {
            if (hpvAttivo.booleanValue()) {
                vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView5");
                //cancello dati vecchi
                ViewHelper.clearVO(vo);
            }
        }
    }

    /**
     * Metodo che si occupa della generazione degli inviti
     * @param ctx
     */
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
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");

        //soggetti, gia' filtrati
        ViewObject soggetti = am.findViewObject("Round_SoSoggettiFilteredView1");
        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView1");

        //slot, gia' filtrati
        ViewObject slots = am.findViewObject("Round_SoAppuntamentoView1");
        int generati = 0;
        //ViewObject cnf_comuni = am.findViewObject("Round_SoCnfComuneView1");

        // mauro 09/02/2010, spostato fuory da try x rimozione logo
        //preparo la classe che generera' gli inviti (con inizializzazione ridotta=false)
        GeneratoreInviti gi = new GeneratoreInviti(am2, false);

        // mauro 26/11/2010, gestione allegato virtuale
        Integer virt = bean.getVirtuale();

        boolean virtuale = (virt != null && virt.intValue() == 1);

        //indica se l'errore e' avvenuto prima di aver generato qualche invito
        boolean before = true;
        try {
            AccessManager.checkPermission("SOGenerazioneInviti");

            // mauro 27/05/2010, registro dove e' stata fatta ultima query
            Map sess = ADFContext.getCurrent().getSessionScope();
            String conteggioInviti = (String) sess.get("conteggioInviti");

            if (conteggioInviti == null) {
                throw new Exception("Eseguire il conteggio degli inviti prima di generarli.");
            }

            if (!conteggioInviti.equals("P")) {
                throw new Exception("L'ultimo conteggio degli inviti non e' stato eseguito su 'Primi inviti'. Rieseguire il conteggio.");
            }
            // mauro 27/05/2010, fine

            //controllo la correttezza dei dati inseriti e filtro gli slot
            this.checkParams();

            //20110407 Serra: verifico che il centro impostato sia coerente con quello del conteggio
            if (bean.getCentro() == null) { //per evitare problemi di passivazione
                bean.setCentro(this.centro_conta.intValue());
            }
            if (!this.centro_conta.equals(bean.getCentro())) {
                throw new Exception("Il centro e' stato modificato dopo l'esecuzione del conteggio. Ripristinare il centro originario");
            }
            //fine 20110407

            //per aggiornare i dati dei round
            GestoreRound gr = new GestoreRound(am2);

            //ciclo sulla generazione degli inviti
            Row r = vo.first();

            if (hpvAttivo.booleanValue()) {
                Hashtable daInvitare = new Hashtable();
                soggetti = am.findViewObject("Round_SoConteggiInvitiView1");
                Integer idQuery = (Integer) ADFContext.getCurrent().getSessionScope().get("id_query_old");
                String tipo_test = (String) r.getAttribute("TipoTest");

                //se non ne devo generare non li inserisco
                int n_inviti = ((Integer) r.getAttribute("DaGenerare")).intValue();

                if (n_inviti > 0) {
                    soggetti.setWhereClause("ID_QUERY = " + idQuery + " AND TEST_PROPOSTO = '" + tipo_test + "' ");
                    soggetti.executeQuery();

                    int num_soggetti = (int) soggetti.getEstimatedRowCount();
                    //tengo solo il numero di inviti che voglio generare
                    int l_pap = Math.min(num_soggetti, n_inviti);

                    Row[] elenco_pap = new Row[l_pap];

                    RowSetIterator iter = ViewHelper.getRowSetIterator(soggetti, "pap_iter");

                    int pap_copiati = 0;

                    while (iter.hasNext() && pap_copiati < l_pap) {
                        elenco_pap[pap_copiati] = iter.next();
                        pap_copiati++;
                    }

                    daInvitare.put(tipo_test, elenco_pap);
                }

                /********************** HPV *********************************/

                vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView5");
                r = vo.first();
                tipo_test = (String) r.getAttribute("TipoTest");

                //se non ne devo generare non li inserisco
                n_inviti = ((Integer) r.getAttribute("DaGenerare")).intValue();

                if (n_inviti > 0) {
                    soggetti.setWhereClause("ID_QUERY = " + idQuery + " AND TEST_PROPOSTO = '" + tipo_test + "' ");
                    soggetti.executeQuery();
                    Row sogg = soggetti.first();
                    int num_soggetti = (int) soggetti.getEstimatedRowCount();
                    //tengo solo il numero di inviti che voglio generare
                    int l_hpv = Math.min(num_soggetti, n_inviti);

                    Row[] elenco_hpv = new Row[l_hpv];

                    RowSetIterator iter = ViewHelper.getRowSetIterator(soggetti, "hpv_iter");

                    int hpv_copiati = 0;

                    while (iter.hasNext() && hpv_copiati < l_hpv) {
                        elenco_hpv[hpv_copiati] = iter.next();
                        hpv_copiati++;
                    }

                    daInvitare.put(tipo_test, elenco_hpv);

                }

                if (!daInvitare.isEmpty()) {
                    Vector listaInviti = DistributoreInviti.distribuisciInviti(daInvitare, 5);
                    Iterator iterInviti = listaInviti.iterator();
                    Row slot = slots.first();

                    //ciclo sugli slot
                    while (slot != null && iterInviti.hasNext() && generati < listaInviti.size()) {
                        int x =
                            ((Integer) slot.getAttribute("Dispslot")).intValue() -
                            ((Integer) slot.getAttribute("Slotoccup")).intValue();
                        for (int i = 0; i < x; i++) {
                            if (generati >= listaInviti.size() || !iterInviti.hasNext())
                                break;

                            //leggo un soggetto e determino il comune di riferimento
                            Row sogg = (Row) iterInviti.next();
                            Integer comune;
                            if (ConfigurationConstants.CODICE_DOMICILIATO.equals((Integer) sogg.getAttribute("Codanagreg")))
                                //il comune di riferimento e' il domicilio
                                comune = (Integer) sogg.getAttribute("ReleaseCodeComDom");
                            else
                                comune = (Integer) sogg.getAttribute("ReleaseCodeComRes");

                            if (comune == null) {
                                throw new Exception("Impossibile trovare il comune di riferimento per il soggetto con tessera " +
                                                    (String) sogg.getAttribute("Codts"));
                            }

                            //genero l'invito
                            //se true, incremento generati
                            if (gi.generaInvito(ulss, tpscr, user, (String) r.getAttribute("Idtpinvito"),
                                                (Integer) slot.getAttribute("Idapp"),
                                                (String) sogg.getAttribute("Codts"),
                                                (Integer) sogg.getAttribute("Idinvito"), //se non c'e' un invito attivo e' null
                                                bean.getData_riferimento_eta(),
                                                (Integer) slot.getAttribute("Idcentro"), virtuale,
                                                (String) sogg.getAttribute("TestProposto"))) {
                                generati++;
                                before = false;

                                //aggiorno la data del primo e ultimo invito per il comune di riferimento
                                gr.updateRoundData(tpscr, comune, (oracle.jbo.domain.Date) slot.getAttribute("Dtapp"),
                                                   gr.DTPRIMOINVITO);
                                gr.updateRoundData(tpscr, comune, (oracle.jbo.domain.Date) slot.getAttribute("Dtapp"),
                                                   gr.DTULTINVITO);
                            }

                        }
                        slot = slots.next();
                    }


                }

            } else {

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

                        if (comune == null) {
                            throw new Exception("Impossibile trovare il comune di riferimento per il soggetto con tessera " +
                                                (String) sogg.getAttribute("Codts"));
                        }

                        //genero l'invito
                        //se true, incremento generati
                        if (gi.generaInvito(ulss, tpscr, user, (String) r.getAttribute("Idtpinvito"),
                                            (Integer) slot.getAttribute("Idapp"), (String) sogg.getAttribute("Codts"),
                                            (Integer) sogg.getAttribute("Idinvito"), //se non c'e' un invito attivo e' null
                                            bean.getData_riferimento_eta(),
                                            (Integer) slot.getAttribute("Idcentro"), virtuale)) {
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
            gi.releaseLogo(); // mauro 09/02/2010, rimozione logo

            ADFContext adfCtx = ADFContext.getCurrent();
            String m = MessageBundle.getMessageFromApplicationMessageBundle(adfCtx.getLocale(), "view.round.message_Bundle", "ROUND_INVITI_GEN_MSG", true);
            
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
        if (bean.getGenerate_dal() == null)// || bean.getGenerate_dal().length() == 0)
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
            if (bean.getGenerate_al() != null // && bean.getGenerate_al().length() > 0 
                &&  bean.getGenerate_al().before(new Date()))
                throw new Exception("La generazione degli inviti fino alla data corrente non e' un'operazione consentita.");
        } catch (ParseException pex) {
            throw new Exception("La data di fine del periodo non corrisponde al formato dd/MM/yyyy");
        }

        //controllo che il centro non sia cambiato
        /*  if(bean.getCentro()!=null && !bean.getCentro().equals(this.centroSel))
          throw new Exception("Il centro e' stato cambiato, rieseguire il conto prima di generare gli inviti");
        */
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView1");

        Row r = vo.first();

        if (hpvAttivo.booleanValue()) {
            if (r == null) {
                vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView5");
                r = vo.first();
                if (r == null)
                    throw new Exception("Non e' stato eseguito nessun calcolo in base la quale generare gli inviti");
            }
        } else {
            if (r == null)
                throw new Exception("Non e' stato eseguito nessun calcolo in base la quale generare gli inviti");
        }

        //non possono esserci inviti diversi dai primi
        if (!((String) r.getAttribute("Idtpinvito")).equals(ConfigurationConstants.CODICE_PRIMO_INVITO))
            throw new Exception("Non e' stato eseguito nessun calcolo in base la quale generare gli inviti");

        //controllo che l'agenda fisica del centro presenti degli slot
        ViewObject slot = am.findViewObject("Round_SoAppuntamentoView1");
        String where =
            "TPSRC='" + tpscr + "' AND IDCENTRO=" + bean.getCentro() + " AND DTAPP>=TO_DATE('" +
             new SimpleDateFormat("dd/MM/yyyy").format(bean.getGenerate_dal()) + "','" + DateUtils.DATE_PATTERN + "')";
        if (bean.getGenerate_al() != null)// && bean.getGenerate_al().length() > 0)
            where += " AND DTAPP<TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getGenerate_al()) + "','" + DateUtils.DATE_PATTERN + "')";

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

            where += " AND ( ( ORAAPP > " + hh_da + ") OR " + "( ORAAPP = " + hh_da + " AND MINAPP >= " + mi_da + ") )";
        }

        if ((ora_a != null) && !ora_a.equals("")) {
            String hh_a = ora_a.substring(0, 2);
            String mi_a = ora_a.substring(3, 5);

            where += " AND ( ( ORAAPP < " + hh_a + ") OR " + "( ORAAPP = " + hh_a + " AND MINAPP <= " + mi_a + ") )";
        }
        // 14-10-2009, fine modifica

        slot.setWhereClause(where);
        slot.setForwardOnly(true);
        slot.executeQuery();
        if (slot.first() == null)
            throw new Exception("L'agenda fisica per il periodo richiesto non e' stata generata");
    }

    public String onGoDetails() {
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        if (hpvAttivo.booleanValue()) {
            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            Round_invitiBean bean =
                (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

            //tipo test da visualizzare
            ViewObject daGenerare = am.findViewObject("Round_SoTipiInvitiDaGenerareView1");
            Row selection = daGenerare.getCurrentRow();
            String tipoTest = (String) selection.getAttribute("TipoTest");

            Integer idQuery = (Integer) ADFContext.getCurrent().getSessionScope().get("id_query_old");
            String where = "ID_QUERY = " + idQuery + " AND TEST_PROPOSTO = '" + tipoTest + "' ";

            //mi occupo dell'ordinamneto
            Integer ind = bean.getPer_indirizzo();

            String s =
                "DECODE(CODANAGREG,3,CODCOMDOM,CODCOMRES)," +
                "DECODE(CODANAGREG,3,INDIRIZZO_DOM,INDIRIZZO_RES)," +
                "NVL(DTRICHIAMO, TO_DATE('01/01/1900','dd/MM/yyyy')),DATA_NASCITA,COGNOME,NOME";

            ViewObject voDetail = am.findViewObject("Round_SoConteggiInvitiView1");
            voDetail.setWhereClause(where);

            //se non invito per indirizzo
            if (ind == null || ind.intValue() == 0) { //cancello l'ordering imposto, tornando al default
                voDetail.setOrderByClause(null);
            } else { //sto invitando per indirizzo
                voDetail.setOrderByClause(s);
            }

            voDetail.executeQuery();

        }

        return "goDetails";
    }

    public String onGoDetailsHPV() {
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        if (hpvAttivo.booleanValue()) {
            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            Round_invitiBean bean =
                (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

            //tipo test da visualizzare
            ViewObject daGenerare = am.findViewObject("Round_SoTipiInvitiDaGenerareView5");
            Row selection = daGenerare.getCurrentRow();
            String tipoTest = (String) selection.getAttribute("TipoTest");

            Integer idQuery = (Integer) ADFContext.getCurrent().getSessionScope().get("id_query_old");
            String where = "ID_QUERY = " + idQuery + " AND TEST_PROPOSTO = '" + tipoTest + "' ";

            //mi occupo dell'ordinamneto
            Integer ind = bean.getPer_indirizzo();

            String s =
                "DECODE(CODANAGREG,3,CODCOMDOM,CODCOMRES)," +
                "DECODE(CODANAGREG,3,INDIRIZZO_DOM,INDIRIZZO_RES)," +
                "NVL(DTRICHIAMO, TO_DATE('01/01/1900','dd/MM/yyyy')),DATA_NASCITA,COGNOME,NOME";

            ViewObject voDetail = am.findViewObject("Round_SoConteggiInvitiView1");
            voDetail.setWhereClause(where);

            //se non invito per indirizzo
            if (ind == null || ind.intValue() == 0) { //cancello l'ordering imposto, tornando al default
                voDetail.setOrderByClause(null);
            } else { //sto invitando per indirizzo
                voDetail.setOrderByClause(s);
            }

            voDetail.executeQuery();

        }

        return "goDetails";
    }
}
