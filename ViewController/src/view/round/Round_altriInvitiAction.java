package view.round;

import insiel.dataHandling.DateUtils;

import java.math.BigDecimal;

import java.sql.SQLException;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.invito.DistributoreInviti;
import view.invito.GestoreRound;

public class Round_altriInvitiAction extends Round_DataForwardAction {
    ///20110407 Serra:variabile che memorizza il centro per cui e' stato fatto un conteggio
    private Integer centro_conta;
    //fine 20110407

    protected String getElenco_voName() {
        return "Round_SoSoggettiFilteredView1";
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
            if (bean.getRc_comune() != null) {
                Key _k = new Key(new Object[] { bean.getRc_comune() });
                Row[] _r = iter.findByKey(_k, 1);

                if (_r == null || _r.length == 0) {
                    bean.setRc_comune(null);
                    bean.setComune(null);
                }
            }
            // BUG IN CASO DI SWITCH TRA LE TIPOLOGIE DI INVITO - FINE

            //vo per tipo invito
            vo = am.findViewObject("Round_SoCnfTpinvitoAltriView1");
            where = "ULSS = '" + ulss + "' AND TPSCR = '" + scr + "' AND LIVELLO = " + bean.getLivello();
            vo.setWhereClause(where);
            vo.executeQuery();

            if (bean.getLivello() != null && bean.getLivelloInvito() != null &&
                bean.getLivello().intValue() != bean.getLivelloInvito().intValue()) {
                bean.setTpInvito("-1");
            }

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
        //se la selezione e' nulla non faccio nulla

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


    public void onSelectCentro(ValueChangeEvent valueChangeEvent) {
        // mauro 30/10/2009, aggiunta per gestione comune/zona
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        bean.setComune(null);
        bean.setZona(null);
        //this.selectComune(ctx);
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


    /**
     * mauro aggiunto il 30/10/2009 per gestione comune/zona
     *Ad ogni refresh devo mantenere la consistenza dei dati tra centri, comuni e zone
     * @param ctx
     */
    protected void processUpdateModel() {
        // TODO:  Override this oracle.adf.controller.struts.actions.DataAction method
        this.selectCentro();
        this.selectComune();

    }


    /**
     * Selezione e conteggio della popolazione target in base ai criteri inseriti
     * @param ctx
     */
    public void onFilterPopulation(ActionEvent actionEvent) {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        // Round_invitiBean bean=(Round_invitiBean)BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        try {

            this.checkFilters();
            ViewObject target = this.filterSoggettiNEW();
            this.countTarget(target);


        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleException(ex.getMessage(), am.getTransaction());
        }
    }

    /**
     * Metodo che controlla che i filtri per la selzione delle popolazione target
     * siano corretti
     * @throws java.lang.Exception se un filtro non e' corretto
     * @param ctx
     */
    private void checkFilters() throws Exception {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

        // Controllo validita' filtri
        // if(bean.getCentro()==null || bean.getCentro().intValue()<0)
        // throw new Exception("Selezionare il centro di prelievo prima di eseguire la selezione");
        if (bean.getMax_data_richiamo() == null)
            throw new Exception("Impostare la data di richiamo limite prima di eseguire la selezione");

        /*20080703 MOD: aggiunta filtri opzionali sull'eta' */
        /*if (bean.getData_riferimento_eta() != null && bean.getData_riferimento_eta().length() > 0)
		try {
			DateUtils.DATE_FORMATTER.parse(bean.getData_riferimento_eta());
		} catch (ParseException pex) {
			throw new Exception("La data di riferimento per l'eta' non ha il formato corretto (" + DateUtils.DATE_PATTERN + ")");
		}*/

        if (bean.getEta_a() > 0 || bean.getEta_da() > 0) {
            if (bean.getData_riferimento_eta() == null)
                throw new Exception("Impostare la data di riferimento per l'eta' prima di eseguire la selezione in base all'eta'");
        }
        /*20080703 FINE MOD */

        Date maxDtRichiamo = bean.getMax_data_richiamo();
        Date oggi = DateUtils.DATE_FORMATTER.parse(DateUtils.getToday());
        if (maxDtRichiamo.compareTo(oggi) < 0) {
            throw new Exception("La data 'Richiamo prima del' non puo' essere anteriore alla data odierna");
        }

        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        if (hpvAttivo.booleanValue() && bean.getLivello().intValue() == 1) {
            if (("-1".equals(bean.getTpInvito()) || bean.getTpInvito() == null) && (bean.getTestProposto()==null ||
                "tutti".equals(bean.getTestProposto()))) {
                throw new Exception("Impostare il tipo di invito o il test proposto");
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
    /*  private ViewObject filterSoggetti()  throws Exception
  {
     Round_AppModule am=(Round_AppModule)BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
    Round_invitiBean bean=(Round_invitiBean)BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
    ViewObject vo=am.findViewObject("Round_SoSoggettiFilteredView1");
    String as=(String)ADFContext.getCurrent().getSessionScope().get("ulss");
    String tpscr=(String)ADFContext.getCurrent().getSessionScope().get("scr");

    String where="ULSS='"+as+"' AND (TPSCR IS NULL OR TPSCR='"+tpscr+
    "') AND (Scrcomunerif is null OR Scrcomunerif='"+tpscr+"') AND ";

      //considero solo l'invito attivo
      where+="ATTIVO=1 AND "+
      //data di richiamo inferiore a quella impostata
      "DTRICHIAMO<TO_DATE('"+bean.getMax_data_richiamo()+"','"+DateUtils.DATE_PATTERN+"') AND "+
      //l richimao NON deve essere un primo invito
      "TPRICHIAMO<>'"+ConfigurationConstants.CODICE_PRIMO_INVITO+"' AND ";
      if(bean.getCentro()==null || bean.getCentro().intValue()<0)
        where+="IDCENTRORICHIAMO IS NULL AND ";
      else
      //il prossimo invito va effettuato nel centro selezionato
       where+="IDCENTRORICHIAMO="+bean.getCentro()+" AND ";


      //i codici anagrafici dei soggetti devono ricadere in quelli selezionati dalle
      //ulss per lo screening
      where+="ANAG_TPSCR='"+tpscr+"' AND INCLUSO=1 AND ";

      //i soggetti non devono essere esclusi  al momento


      String lista_escluse="SELECT Round_SoEsclusione.CODTS \n"+
"FROM SO_ESCLUSIONE Round_SoEsclusione, SO_CNF_ESCLUSIONE Round_SoCnfEsclusione  \n"+
"WHERE ((Round_SoEsclusione.IDCNFESCL = Round_SoCnfEsclusione.IDCNFESCL) AND  \n"+
"(Round_SoEsclusione.ULSS = Round_SoCnfEsclusione.ULSS)) AND  \n"+
"(Round_SoEsclusione.TPSCR = Round_SoCnfEsclusione.TPSCR) \n"+
"AND Round_SoEsclusione.tpscr='"+tpscr+"' \n"+
"AND DTANNULL IS NULL AND DTESCL<=TO_DATE('"+DateUtils.getToday()+"','"+DateUtils.DATE_PATTERN+"')  \n"+
"AND (TPESCL='D' OR (TPESCL='T' AND DTESCL+NUMGIORNI>=TO_DATE('"+DateUtils.getToday()+"','"+DateUtils.DATE_PATTERN+"'))) ";

      where+="CODTS NOT IN ("+lista_escluse+") AND ";

      if(where.endsWith(" AND "))
        where=where.substring(0,where.length()-5);

      vo.setWhereClause(where);
//     System.out.println(vo.getQuery());
      vo.executeQuery();

    return vo;

  }
  */

    /**
     * Metodo che filtra i soggetti per trovare quelli target della ricerca.
     * Qui si cercano tutte l edonne che devono proseguire l'iter di screening
     * nel centro selezionato 8considerando anche i richiami pst-esclusione)
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
        ViewObject cnfTpscr = am.findViewObject("Round_SoCnfTpscrView1");
        String as = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        //leggo se l'azienda lavora con centri dir ichamo preconfifgurati sul singolo soggetto
        Map session = ADFContext.getCurrent().getSessionScope();
        Boolean cnf_rich = (Boolean) session.get("centri_rich_cnf");
        session.put("doppiaTabella", Boolean.FALSE);

        //03122014 modifiche per gestire hpv-dna
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        Integer etaHpvMin = new Integer(0);
        Integer etaHpvMax = new Integer(0);

        if ("CI".equals(tpscr) && hpvAttivo.booleanValue()) {
            vo = am.findViewObject("Round_SoSoggettiFilteredHPVView3");
            //ricavo fascia di eta' per l’eleggibilita' allo screening HPV definita in SO_AZIENDA
            ViewObject voConf = am.findViewObject("Round_SoAziendaView1");
            Row[] result = voConf.getFilteredRows("Codaz", as);
            etaHpvMin = (Integer) result[0].getAttribute("Etahpvmin");
            etaHpvMax = ((Integer) result[0].getAttribute("Etahpvmax")) + 1;
            if (etaHpvMin == null || etaHpvMax == null) {
                throw new Exception("Fascia di eta' per l’eleggibilita' allo screening HPV non definita");
            }
        }

        String where = "";

        Row[] cnf_scr = cnfTpscr.getFilteredRows("Codscr", tpscr);
        if (cnf_scr.length == 0)
            throw new Exception("Configurazione di screening non trovata per lo screening in uso");
        if (cnf_scr[0].getAttribute("Sesso") != null)
            where += "SESSO='" + cnf_scr[0].getAttribute("Sesso") + "' AND ";

        if (tpscr.equals("CI") || tpscr.equals("MA") || tpscr.equals("CO")) {
            //seleziono chi alla data di riferimento non ha ancora compiuto l'eta' massima +1
            if (bean.getEta_a() > 0)
                where +=
                    "trunc(DATA_NASCITA)>add_months(TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_riferimento_eta()) + "','" +
                    DateUtils.DATE_PATTERN + "'),-" + (bean.getEta_a() + 1) + "*12) AND ";
            //seleziono chi alla data di riferimento ha gia' compiuto l'eta' minima
            if (bean.getEta_da() > 0)
                where +=
                    "trunc(DATA_NASCITA)<add_months(TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_riferimento_eta()) + "','" +
                    DateUtils.DATE_PATTERN + "'),-" + bean.getEta_da() + "*12) AND ";
        } else {
            //la chiamata di basa sulla data di nascita e non sull'eta'
            where +=
                "trunc(DATA_NASCITA) >= TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_nascita_da()) + "', '" + DateUtils.DATE_PATTERN +
                "') AND ";
            where +=
                "trunc(DATA_NASCITA) <= TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_nascita_a()) + "', '" + DateUtils.DATE_PATTERN +
                "') AND ";
        }

        //data di richiamo inferiore a quella impostata
        //e presenza di un invito (le mai invitat evengono sempre chiamate con primo invito, non qui)
        where +=
            "idinvito is not null and Round_SoSoggscr.DTRICHIAMO<TO_DATE('" +
            new SimpleDateFormat("dd/MM/yyyy").format(bean.getMax_data_richiamo()) + "','" + DateUtils.DATE_PATTERN +
            "') AND " +
            //l richimao NON deve essere un primo invito
            "Round_SoSoggscr.TPRICHIAMO<>'" + ConfigurationConstants.CODICE_PRIMO_INVITO + "' AND ";

        if (bean.getTpInvito() != null && !bean.getTpInvito().equals("-1")) {
            where += "Round_SoSoggscr.TPRICHIAMO='" + bean.getTpInvito() + "' AND ";
            if (hpvAttivo.booleanValue() && bean.getLivello().intValue() == 1) {
                session.put("doppiaTabella", Boolean.TRUE);
            }
        }

        if (hpvAttivo.booleanValue() && bean.getLivello().intValue() == 1 && !"tutti".equals(bean.getTestProposto())) {
            if ("HPV".equals(bean.getTestProposto())) {
                where +=
                    "(Round_SoCnfTpinvito.HPV = 1 OR " +
                    "(Round_SoCnfTpinvito.HPV = 2 AND Round_SoSoggScr.ROUNDINDIV_HPV > 0) OR " +
                    "(Round_SoCnfTpinvito.HPV = 2 AND DATA_NASCITA >= add_months(TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_riferimento_eta()) + "','dd/MM/yyyy'), -" +
                    etaHpvMax + " * 12) " + "AND DATA_NASCITA <= add_months(TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_riferimento_eta()) + "','dd/MM/yyyy'),-" +
                    etaHpvMin + " * 12))) AND ";
            } else //PAP
            {
                where +=
                    "(Round_SoCnfTpinvito.HPV = 0 OR " +
                    "(Round_SoCnfTpinvito.HPV = 2 AND Round_SoSoggScr.ROUNDINDIV_HPV = 0 AND " +
                    " DATA_NASCITA < add_months(TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_riferimento_eta()) + "','dd/MM/yyyy'), -" +
                    etaHpvMax + " * 12) " + "OR DATA_NASCITA > add_months(TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_riferimento_eta()) + "','dd/MM/yyyy'),-" +
                    etaHpvMin + " * 12))) AND ";
            }
        }

        // mauro 30/10/2009, filtro per comune o zona
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
        // mauro 30/10/2009, fine modifica


        //se l'azienda lavora con centri di richiamo confiogurati per songolo soggetto,
        //filtro sui soggetti che devono essere invitati in quel centro di primo livello
        if (cnf_rich.booleanValue()) {
            if (bean.getCentro() == null || bean.getCentro().intValue() < 0)
                where += "Round_SoSoggscr.IDCENTRO1LIV IS NULL AND Round_SoSoggscr.IDCENTRO2LIV IS NULL AND ";
            else //filro sul centro configurato del livello corrispondente
            {
                where +=
                    " Round_SoSoggScr.IDCENTRO" + bean.getLivello() + "LIV=" + bean.getCentro() + " AND " +
                    "Round_SoSoggscr.TPRICHIAMO in (\n" + "select cc.idtpinvito from so_cnf_tpinvito cc " +
                    "where cc.ulss='" + as + "' and cc.tpscr='" + tpscr + "' and cc.livello=" + bean.getLivello() +
                    ") AND ";
            }

        } else {
            //l'azienda lavora con i centro di richiamo normali
            if (bean.getCentro() == null || bean.getCentro().intValue() < 0)
                where += "Round_SoSoggscr.IDCENTRORICHIAMO IS NULL AND ";
            else
                //il prossimo invito va effettuato nel centro selezionato
                where += "Round_SoSoggscr.IDCENTRORICHIAMO=" + bean.getCentro() + " AND ";

        } //centro di richiamo normali

        if ("CA".equals(tpscr)) {
            where +=
                "Round_SoInvito.IDTPINVITO IN (select idtpinvito " + "from so_cnf_tpinvito " + "where tpscr = 'CA' " +
                "and ulss = '" + as + "' " +
                "and idcateg in (1,2,9)) AND "; //genera i richiami solo su primi inviti e solleciti standard
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

            vo.setWhereClauseParam(0, null);
            vo.setWhereClauseParam(1, null);
            vo.setWhereClauseParam(2, null);

            //svuoto l'altro vo non usato
            ViewObject vo2 = am.findViewObject("Round_SoSoggettiFilteredView2");
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
            
            // mauro 08/07/2010 necessario allineare Round_SoSoggettiFilteredView2
            ViewObject vo2 = am.findViewObject("Round_SoSoggettiFilteredView2");
            vo2.setWhereClauseParam(6, dtRich);
            vo2.setWhereClauseParam(7, dtRich);
            // mauro 08/07/2010 fine
            // 16/10/2009 fine modifiche

            //svuoto l'altro vo non usato
            vo2 = am.findViewObject("Round_SoConteggiInvitiView1");
            vo2.setWhereClause("1=2");
            vo2.executeQuery();
        }

        //mi occupo dell'ordinamneto
        Integer ind = new Integer(bean.getPer_indirizzo());

        String s =
            "DECODE(round_sosoggetto.codanagreg,3,round_sosoggetto.codcomdom,round_sosoggetto.codcomres)," +
            "DECODE(round_sosoggetto.codanagreg,3,round_sosoggetto.indirizzo_dom,round_sosoggetto.indirizzo_res)," +
            "NVL(Round_SoSoggScr.dtrichiamo, TO_DATE('01/01/1900','dd/MM/yyyy')),DATA_NASCITA,COGNOME,NOME";
        //se non invito pe rindirizzo
        if (ind == null || ind.intValue() == 0) {
            if (tpscr.equals("PF")) {
                //odinamento per data nascita decrescente
                s = "DATA_NASCITA DESC,NVL(Round_SoSoggScr.dtrichiamo, TO_DATE('01/01/1900','dd/MM/yyyy')),COGNOME,NOME";
                vo.setOrderByClause(s);
            } else {
                //cancello l'ordering imposto, tornando al default
                vo.setOrderByClause(null);
            }
        } else //sto invitando pe rindirizzo
        {
            vo.setOrderByClause(s);
        }

        if (hpvAttivo.booleanValue()) {
            String qry = vo.getQuery();
            Object[] params = vo.getWhereClauseParams();

            for (int i = 0; i < params.length; i++) {
                String toReplace = ":" + String.valueOf(i + 1);
                Object _o = ((Object[])params[i])[1];
                String replaceWith = _o!=null? "'" + (_o instanceof Date ? new SimpleDateFormat("dd/MM/yyyy").format((Date)_o) : _o )  + "'": "''";
                qry = qry.replaceFirst(toReplace, replaceWith);
                /*String replaceWith = "";
                if (params[i] != null)
                    replaceWith = "'" + params[i] + "'";
                else
                    replaceWith = "null";
                qry = qry.replaceFirst(toReplace, replaceWith);*/
            }
            String sel1 = "";
            String sel2 = "";
            int lenQ = qry.length();
            if (lenQ > 4000) {
                sel1 = qry.substring(0, 4000);
                sel2 = qry.substring(4000);
            } else {
                sel1 = qry;
            }
            //chiamo il package CONTEGGI_INVITI
            Integer id_query_old = (Integer) ADFContext.getCurrent().getSessionScope().get("id_query_altri");
            if (id_query_old == null)
                id_query_old = new Integer(0);
            Integer id_query =
                am.callContaInviti(as, tpscr, sel1, sel2, new BigDecimal(id_query_old.intValue()), null);
            if (id_query.intValue() < 0)
                throw new Exception("Errore nella procedura di conteggio inviti");
            vo = am.findViewObject("Round_CountSoContegAltriInvitiView1"); //cambiare view object
            vo.setWhereClauseParam(0, id_query);

            ADFContext.getCurrent().getSessionScope().put("id_query_altri", id_query);
        } else {
            vo.executeQuery();
        }

        // mauro 27/05/2010, registro dove e' stata fatta ultima query
        session.put("conteggioInviti", "A");
        // mauro 27/05/2010, fine

        //20110407 Serra: imposto il centro per cui e' stato fatto il conteggio
        this.centro_conta = (bean.getCentro()!=null)?new Integer(bean.getCentro()):null;
        //fine 20110407

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
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        Boolean doppiaTabella = (Boolean) ADFContext.getCurrent().getSessionScope().get("doppiaTabella");
        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView2");
        ViewObject tpinviti = am.findViewObject("Round_SoCnfTpinvitoView1");
        //cancello dati vecchi
        ViewHelper.clearVO(vo);

        ViewObject voHPV = am.findViewObject("Round_SoTipiInvitiDaGenerareView6");
        ViewHelper.clearVO(voHPV);

        //ciclo sui tipi di inviti
        Row tp = tpinviti.first();
        while (tp != null) {
            Row[] result = target.getFilteredRows("Tprichiamo", tp.getAttribute("Idtpinvito"));

            if (result.length > 0) {
                if (hpvAttivo.booleanValue()) {
                    Integer livello = (Integer) result[0].getAttribute("Livello");
                    if (livello.intValue() == 1) {
                        if (doppiaTabella.booleanValue()) {
                            //conto i pap
                            Row[] resultPap = target.getFilteredRows("TestProposto", "PAP");
                            Row r = vo.createRow();
                            vo.insertRow(r);
                            r.setAttribute("Idtpinvito", tp.getAttribute("Idtpinvito"));
                            r.setAttribute("Descrizione", tp.getAttribute("Descrizione"));
                            r.setAttribute("TipoTest", "PAP");
                            r.setAttribute("DaGenerare", new Integer(0));
                            if (resultPap.length > 0) {
                                //numero di inviti di quel tipo da generare
                                r.setAttribute("Numero", resultPap[0].getAttribute("Numero"));
                            } else {
                                r.setAttribute("Numero", new Integer(0));
                            }

                            // conto hpv proposti
                            Row[] resultHPV = target.getFilteredRows("TestProposto", "HPV");
                            Row rHPV = voHPV.createRow();
                            voHPV.insertRow(rHPV);
                            rHPV.setAttribute("Idtpinvito", tp.getAttribute("Idtpinvito"));
                            rHPV.setAttribute("Descrizione", tp.getAttribute("Descrizione"));
                            rHPV.setAttribute("TipoTest", "HPV");
                            rHPV.setAttribute("DaGenerare", new Integer(0));
                            if (resultHPV.length > 0) {
                                //numero di inviti di quel tipo da generare
                                rHPV.setAttribute("Numero", resultHPV[0].getAttribute("Numero"));
                            } else {
                                rHPV.setAttribute("Numero", new Integer(0));
                            }

                        } else {
                            //devo distinguere il test proposto
                            Integer counterHPV = new Integer(0);
                            Integer counterPAP = new Integer(0);
                            for (int i = 0; i < result.length; i++) {
                                if (result[i].getAttribute("TestProposto").equals("HPV")) {
                                    counterHPV = (Integer) result[i].getAttribute("Numero");
                                } else {
                                    counterPAP = (Integer) result[i].getAttribute("Numero");
                                }
                            }
                            if (counterHPV.intValue() > 0) {
                                Row r = vo.createRow();
                                vo.insertRow(r);
                                r.setAttribute("Idtpinvito", tp.getAttribute("Idtpinvito"));
                                r.setAttribute("Descrizione", tp.getAttribute("Descrizione"));
                                r.setAttribute("TipoTest", "HPV");
                                //numero di inviti di quel tipo da generare
                                r.setAttribute("Numero", counterHPV);
                                r.setAttribute("DaGenerare", new Integer(0));
                            }
                            if (counterPAP.intValue() > 0) {
                                Row r = vo.createRow();
                                vo.insertRow(r);
                                r.setAttribute("Idtpinvito", tp.getAttribute("Idtpinvito"));
                                r.setAttribute("Descrizione", tp.getAttribute("Descrizione"));
                                r.setAttribute("TipoTest", "PAP");
                                //numero di inviti di quel tipo da generare
                                r.setAttribute("Numero", counterPAP);
                                r.setAttribute("DaGenerare", new Integer(0));
                            }
                        }
                    } else {
                        //senza distinzione
                        Row r = vo.createRow();
                        vo.insertRow(r);
                        r.setAttribute("Idtpinvito", tp.getAttribute("Idtpinvito"));
                        r.setAttribute("Descrizione", tp.getAttribute("Descrizione"));
                        //numero di inviti di quel tipo da generare
                        r.setAttribute("Numero", result[0].getAttribute("Numero"));
                        r.setAttribute("DaGenerare", new Integer(0));
                    }

                } else {
                    Row r = vo.createRow();
                    vo.insertRow(r);
                    r.setAttribute("Idtpinvito", tp.getAttribute("Idtpinvito"));
                    r.setAttribute("Descrizione", tp.getAttribute("Descrizione"));
                    //numero di inviti di quel tipo da generare
                    r.setAttribute("Numero", new Integer(result.length));
                    r.setAttribute("DaGenerare", new Integer(0));
                }
            }

            tp = tpinviti.next();
        }

        target.clearCache();
        tpinviti.clearCache();

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
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        //soggetti, gia' filtrati
        ViewObject soggetti = am.findViewObject("Round_SoSoggettiFilteredView1");
        if (hpvAttivo.booleanValue()) {
            soggetti = am.findViewObject("Round_SoConteggiInvitiView1");
            Integer idQuery = (Integer) ADFContext.getCurrent().getSessionScope().get("id_query_altri");
            soggetti.setWhereClause("ID_QUERY = " + idQuery);
            soggetti.executeQuery();
        }
        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView2");
        //slot, gia' filtrati
        ViewObject slots = am.findViewObject("Round_SoAppuntamentoView1");
        ViewObject tpinviti = am.findViewObject("Round_SoCnfTpinvitoView1");
        //conterra' vettori di inviti tutti dello stesso tipo
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
        Integer virt = null;
        //try {
            virt = new Integer(bean.getVirtuale());
        //} catch (SQLException e) {
        //}
        boolean virtuale = (virt != null && virt.intValue() == 1);
        //

        boolean before = true;


        try {
            AccessManager.checkPermission("SOGenerazioneInviti");

            // mauro 27/05/2010, registro dove e' stata fatta ultima query
            Map sess = ADFContext.getCurrent().getSessionScope();
            String conteggioInviti = (String) sess.get("conteggioInviti");

            if (!conteggioInviti.equals("A")) {
                throw new Exception("L'ultimo conteggio degli inviti non e' stato eseguito su 'Altri inviti'. Rieseguire il conteggio.");
            }
            // mauro 27/05/2010, fine

            //controllo la correttezza dei dati inseriti e filtro gli slot
            this.checkParams();

            //20110407 Serra: verifico che il centro imostato sia coerente con quello del conteggio
            if (!this.centro_conta.equals(bean.getCentro())) {
                throw new Exception("Il centro e' stato modificato dopo l'esecuzione del conteggio. Ripristinare il centro originario");
            }
            //fine 20110407

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
                //se non ne devo generare non li inserisco
                int n_inviti = ((Integer) r.getAttribute("DaGenerare")).intValue();
                int risultato = ((Integer) r.getAttribute("Numero")).intValue();

                if (n_inviti == 0 || risultato == 0) {
                    r = vo.next();
                    continue;
                }

                //controllo che il tipo invito esista nella configurazione
                Row[] tipi = tpinviti.getFilteredRows("Idtpinvito", tipo);
                if (tipi.length == 0)
                    throw new Exception("Tipo di invito con codice " + tipo + " non trovato");

                //memorizzo l'info testuale sul tipo di invito
                descrizioni.put(tipo, (String) r.getAttribute("Descrizione"));

                //inserisco l'array dei soggetti da invitare on quell'invito
                Row[] result = null;
                try {
                    result = soggetti.getFilteredRows("Tprichiamo", tipo);
                } catch (Exception e) {
                    result = new Row[0];
                }
                //tengo solo il numero di inviti che voglio generare
                int l = Math.min(result.length, n_inviti);
                Row[] elenco = new Row[l];

                if (hpvAttivo.booleanValue()) {
                    //devo copiare nel nuovo array solo gli inviti con il test proposto selezionato se di primo livello
                    String test_proposto = (String) r.getAttribute("TipoTest");
                    int counter = 0;
                    int copiati = 0;
                    while (copiati < l && counter < result.length) {
                        if (test_proposto == null ||
                            result[counter].getAttribute("TestProposto").equals(test_proposto)) {
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
                            quantiPerTipo.put(tipo, new Integer(newElenco.length));
                        } else {
                            daSpedire.put(tipo, elenco);
                            quantiPerTipo.put(tipo, new Integer(l));
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
                            quantiPerTipo.put(tipo, new Integer(newElenco.length));
                        } else {
                            daInvitare.put(tipo, elenco);
                            quantiPerTipo.put(tipo, new Integer(l));
                        }
                    }
                } else {
                    System.arraycopy(result, 0, elenco, 0, l);

                    //controllo se si tratta di invito con o senza appuntamento
                    if (((Integer) tipi[0].getAttribute("Appuntamento")) == null ||
                        ((Integer) tipi[0].getAttribute("Appuntamento")).intValue() != 1)
                        //senza appuntamento
                        daSpedire.put(tipo, elenco);
                    else
                        //con appuntameto
                        daInvitare.put(tipo, elenco);

                    //inserisco il numero di inviti di quel tipo che devo fare
                    quantiPerTipo.put(tipo, new Integer(l));

                }

                generati.put(tipo, new Integer(0));

                r = vo.next();

            }

            Boolean doppiaTabella = (Boolean) ADFContext.getCurrent().getSessionScope().get("doppiaTabella");
            if (doppiaTabella.booleanValue()) {
                vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView6");

                r = vo.first();
                while (r != null) {

                    String tipo = (String) r.getAttribute("Idtpinvito");
                    //se non ne devo generare non li inserisco
                    int n_inviti = ((Integer) r.getAttribute("DaGenerare")).intValue();
                    int risultato = ((Integer) r.getAttribute("Numero")).intValue();

                    if (n_inviti == 0 || risultato == 0) {
                        r = vo.next();
                        continue;
                    }

                    //controllo che il tipo invito esista nella configurazione
                    Row[] tipi = tpinviti.getFilteredRows("Idtpinvito", tipo);
                    if (tipi.length == 0)
                        throw new Exception("Tipo di invito con codice " + tipo + " non trovato");

                    //memorizzo l'info testuale sul tipo di invito
                    descrizioni.put(tipo, (String) r.getAttribute("Descrizione"));

                    //inserisco l'array dei soggetti da invitare on quell'invito
                    Row[] result = null;
                    try {
                        result = soggetti.getFilteredRows("Tprichiamo", tipo);
                    } catch (Exception e) {
                        result = new Row[0];
                    }

                    //tengo solo il numero di inviti che voglio generare
                    int l = Math.min(result.length, n_inviti);
                    Row[] elenco = new Row[l];


                    //devo copiare nel nuovo array solo gli inviti con il test proposto selezionato se di primo livello
                    String test_proposto = (String) r.getAttribute("TipoTest");
                    int counter = 0;
                    int copiati = 0;
                    while (copiati < l) {
                        if (test_proposto == null ||
                            result[counter].getAttribute("TestProposto").equals(test_proposto)) {
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
                            quantiPerTipo.put(tipo, new Integer(newElenco.length));
                        } else {
                            daSpedire.put(tipo, elenco);
                            quantiPerTipo.put(tipo, new Integer(l));
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
                            quantiPerTipo.put(tipo, new Integer(newElenco.length));
                        } else {
                            daInvitare.put(tipo, elenco);
                            quantiPerTipo.put(tipo, new Integer(l));
                        }
                    }

                    generati.put(tipo, new Integer(0));

                    r = vo.next();

                }
            }

            //SE CI SONO SOLLECITI CON APPUNTAMENTO DEVE ESERCI ANCHE
            //UNA DISTRIBUZIONE LOGISTICO-TEMPORALE
            if (!daInvitare.isEmpty()) {
                //controllo ch eci sia un adata di inizio periodo
                if (bean.getGenerate_dal() == null)
                    throw new Exception("E' stata richiesta la generazione di richiami che prevedono un appuntamento, di conseguenza la data di inizio periodo e' un parametro necessario");
                //controllo che l'agenda fisica del centro presenti degli slot
                String where =
                    "TPSRC='" + tpscr + "' AND IDCENTRO=" + bean.getCentro() + " AND DTAPP>=TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getGenerate_dal()) + "','" + DateUtils.DATE_PATTERN +
                    "')";
                if (bean.getGenerate_al() != null)
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
                //mod 29/06/2007
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
                        int gia_generati = ((Integer) generati.get(sogg.getAttribute("Tprichiamo"))).intValue();

                        if (gia_generati >= ((Integer) quantiPerTipo.get(sogg.getAttribute("Tprichiamo"))).intValue()) {
                            //ne ho gia' generati a sufficienza di quel tipo, quindi vado avanti
                            contatoreLista++;
                            //26/07/2006 :sposto anche il limite su questo slot
                            y++;
                            continue;
                        }
                        //HPV-DNA test proposto
                        String tipo_test = null;
                        if (hpvAttivo.booleanValue()) {
                            Integer livello = (Integer) sogg.getAttribute("Livello");
                            if (livello != null && livello.intValue() == 1) {
                                tipo_test = (String) sogg.getAttribute("TestProposto");
                            }
                        }

                        //altrimenti e' un invito da generare

                        try {
                            //genero l'invito
                            //se true, incremento generati
                            if (gi.generaInvito(ulss, tpscr, user, (String) sogg.getAttribute("Tprichiamo"),
                                                (Integer) slot.getAttribute("Idapp"),
                                                (String) sogg.getAttribute("Codts"),
                                                (Integer) sogg.getAttribute("Idinvito"), //se non c'e' uninvito attivo e' null
                                                rif_date, (Integer) slot.getAttribute("Idcentro"), virtuale,
                                                tipo_test)) {
                                //incremento il numero di inviti generati di quel tipo
                                gia_generati++;
                                before = false;
                                generati.put(sogg.getAttribute("Tprichiamo"), new Integer(gia_generati));

                                //leggo un soggetto e determino il comune di riferimento
                                Integer comune;
                                if (((Integer) sogg.getAttribute("Codanagreg")).intValue() ==
                                    ConfigurationConstants.CODICE_DOMICILIATO.intValue())
                                    //il comune di riferimento e' il domicilio
                                    comune = (Integer) sogg.getAttribute("ReleaseCodeComDom");
                                else
                                    comune = (Integer) sogg.getAttribute("ReleaseCodeComRes");
                                //aggiorno la data dell'ultimo invito ed eventualmente anche del primo
                                gr.updateRoundData(tpscr, comune, (oracle.jbo.domain.Date) slot.getAttribute("Dtapp"),
                                                   gr.DTULTINVITO);
                                gr.updateRoundData(tpscr, comune, (oracle.jbo.domain.Date) slot.getAttribute("Dtapp"),
                                                   gr.DTPRIMOINVITO);
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
            } //fine generazione richiami con appuntamento

            //generazione richiami senza appuntamento
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
                                                rif_date, new Integer(bean.getCentro()), virtuale, tipo_test)) {
                                //incremento il numero di inviti generati di quel tipo
                                gia_generati++;
                                before = false;
                                generati.put(tpsoll, new Integer(gia_generati));

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            this.handleException("Problemi nella generazione di un invito: " + ex.getMessage(), null);
                        }


                    }
                }

            } //fine generazione richiami senza appuntamento

        } catch (Exception ex) {
            //  ex.printStackTrace();
            this.handleException("Impossibile generare gli inviti: " + ex.getMessage(), null);
        } finally {           
            Enumeration en = descrizioni.keys();
            StringBuilder msg = new StringBuilder("<html><body>");
            if (descrizioni.size() > 0) {
                msg.append("<p><b>Sono stati generati:</b></p>"); 
                msg.append("<ul>");

                while (en.hasMoreElements()) {
                    String key = (String) en.nextElement();
                    msg.append("<p><li><i>n&#176; " + generati.get(key) + " inviti di tipo " + descrizioni.get(key) + "</i></li></p>");
                }
                msg.append("</ul>");
            } else {
               msg.append("Non e' stato generato nessun invito perche' non e' stato trovato nulla da generare.");
            }

            msg.append("</body></html>");

            if (!before)
                this.onFilterPopulation(null);

            slots.clearCache();
            soggetti.clearCache();

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


        //il centro in cui posizionare gli inviti
        if (bean.getCentro() == null || bean.getCentro().intValue() < 0)
            throw new Exception("Selezionare il centro in cui fissare gli appuntamenti");

        //la data di inizio generazione
        /*   if(bean.getGenerate_dal()==null || bean.getGenerate_dal().length()==0)
            throw new Exception("La data di inizio del periodo da generare e' obbligatoria");
     */try {
            //formato + l'inizio deve essere non prima di domani
            if (bean.getGenerate_dal() != null && bean.getGenerate_dal().before(new Date()))
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

        ViewObject vo = am.findViewObject("Round_SoTipiInvitiDaGenerareView2");

        Row r = vo.first();
        if (r == null)
            throw new Exception("Non e' stato eseguito nessun calcolo in base la quale generare gli inviti");

    }

    public String onGoDetails() {
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        if (hpvAttivo.booleanValue()) {
            //riporto la query dal vo indipendente a quello dipendente e navigo
            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            Round_invitiBean bean =
                (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

            //tipo test da visualizzare
            ViewObject daGenerare = am.findViewObject("Round_SoTipiInvitiDaGenerareView2");
            Row selection = daGenerare.getCurrentRow();
            String tipoTest = (String) selection.getAttribute("TipoTest");
            String idTipoInvito = (String) selection.getAttribute("Idtpinvito");

            Integer idQuery = (Integer) ADFContext.getCurrent().getSessionScope().get("id_query_altri");
            String where = "ID_QUERY = " + idQuery;
            if (tipoTest !=
                null) {
                //primi livelli
                where +=
        " AND (TEST_PROPOSTO = '" + tipoTest + "' OR TEST_PROPOSTO IS NULL) AND TPRICHIAMO = '" + idTipoInvito + "' ";
            } else {
                //secondi livelli senza distinzione di test proposto
                where += " AND TPRICHIAMO = '" + idTipoInvito + "' ";
            }

            ViewObject voDetail = am.findViewObject("Round_SoConteggiInvitiView1");
            voDetail.setWhereClause(where);
            voDetail.executeQuery();

        } else {
            //riporto la query dal vo indipendente a quello dipendente e navigo
            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            ViewObject vo = am.findViewObject("Round_SoSoggettiFilteredView1");
            String where = vo.getWhereClause();
            String order = vo.getOrderByClause();
            vo = am.findViewObject("Round_SoSoggettiFilteredView2");
            vo.setWhereClause(where);
            vo.setOrderByClause(order);
            vo.executeQuery();
        }

        return "goDetails";
    }

    public String onGoDetailsHPV() {
        Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
        if (hpvAttivo.booleanValue()) {
            //riporto la query dal vo indipendente a quello dipendente e navigo
            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            Round_invitiBean bean =
                (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();

            //tipo test da visualizzare
            ViewObject daGenerare = am.findViewObject("Round_SoTipiInvitiDaGenerareView6");
            Row selection = daGenerare.getCurrentRow();
            String tipoTest = (String) selection.getAttribute("TipoTest");
            String idTipoInvito = (String) selection.getAttribute("Idtpinvito");

            Integer idQuery = (Integer) ADFContext.getCurrent().getSessionScope().get("id_query_altri");
            String where = "ID_QUERY = " + idQuery;
            if (tipoTest !=
                null) {
                //primi livelli
                where +=
        " AND (TEST_PROPOSTO = '" + tipoTest + "' OR TEST_PROPOSTO IS NULL) AND TPRICHIAMO = '" + idTipoInvito + "' ";
            } else {
                //secondi livelli senza distinzione di test proposto
                where += " AND TPRICHIAMO = '" + idTipoInvito + "' ";
            }

            ViewObject voDetail = am.findViewObject("Round_SoConteggiInvitiView1");
            voDetail.setWhereClause(where);
            voDetail.executeQuery();

        }

        return "goDetails";
    }

}
