package view.soggetto;

import insiel.dataHandling.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.Cnf_EsclProposteViewRow;
import model.common.Sogg_AppModule;

import model.commons.SoggUtils;
import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.jbo.AttributeDef;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.StructureDef;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

import view.commons.action.Parent_DataForwardAction;

public class Sogg_esclAction extends Parent_DataForwardAction {

    protected void setAppModule() {
        this.amName = "Sogg_AppModule";
    }

    private String esclDaPropagare(String istVOProposte) {
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voSelectIter = bindings.findIteratorBinding(istVOProposte);
        Row[] rows = voSelectIter.getAllRowsInRange();
        String desEscl = "";
        for (Row row : rows) {
            if (row.getAttribute("Selected") != null && (Boolean)row.getAttribute("Selected")){
                desEscl = row.getAttribute("Idcnfescl") + " - " + row.getAttribute("Descrizione");
            }
        }
        
      return desEscl;
    }


    protected boolean pendingUpdatesOnRichnav() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        boolean modAnnullo = ((Boolean) sess.get("esclModAnnullo")).booleanValue();

        if (modAnnullo) {
            @SuppressWarnings("deprecation")
            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            am.getTransaction().rollback();

            return false;
        } else
            return super.pendingUpdatesOnRichnav();
    }

    /*
       * mauro, 16/06/2010, evento creato per permettere selezione delle esclusioni collegate
       * da annullare
       */
    public String onConfAnnullo() {

        try {
            Map sess = ADFContext.getCurrent().getSessionScope();
            sess.put("esclModAnnullo", Boolean.FALSE);

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
            ViewObject voEscl = voIter.getViewObject();
            Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
            Row cEscl = voEscl.getCurrentRow();
            Date dtCorr = DateUtils.getOracleDateNow();
            cEscl.setAttribute("Dtannull", dtCorr);

            String user = (String) sess.get("user");
            DCIteratorBinding voSelectIter = bindings.findIteratorBinding("Sogg_EsclusioniCollegateView1Iterator");
            Row[] rows = voSelectIter.getAllRowsInRange();
            for (Row row : rows) {
                if (row.getAttribute("Selected") != null && (Boolean)row.getAttribute("Selected")){
                    Integer idEscl = (Integer) row.getAttribute("Idescl");
                    String updateEscl =
                        "update so_esclusione set DTANNULL = sysdate, " + "DTMODESCL = sysdate, OPMODIFICA = '" + user +
                        "' " + "where IDESCL = " + idEscl.intValue();

                    am.getTransaction().executeCommand(updateEscl);
                }
            }

            boolean esito = this.save();
            if (esito) {
                return "goRic";
            }
            // altrimennti il rollback e' eseguito dal parent

        } catch (Exception ex) {
            this.doRollback();
            this.handleException(ex.getMessage(), null);
        }
    
        return null;
    }

    public String onRinAnnullo() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.put("esclModAnnullo", Boolean.FALSE);
        
        return null;

    }

    public String onAnnulla() {

        Map sess = ADFContext.getCurrent().getSessionScope();
        sess.put("esclModAnnullo", Boolean.TRUE);

        /* mauro 15/06/2010
         * per prima cosa devo determinare il padre, se l'esclusione ha escl. figlie
         * allora e' il padre, altrimenti potrebbe avere un padre e allora e' quella
         * l'escl padre. Se non ha neanche un padre non ci sono escl collegate.
         * Una volte determinato il padre faccio query per trovare le escl collegate:
         * padre + figlie esclusa esclusione corrente.
         */
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
        ViewObject voEscl = voIter.getViewObject();
        Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
        Row cEscl = voEscl.getCurrentRow();
        Integer idEscl = (Integer) cEscl.getAttribute("Idescl");

        Integer idEsclPadre = null;

        String figlie = (String) cEscl.getAttribute("EsclFiglie");

        if (figlie != null && !figlie.equals("")) {
            idEsclPadre = (Integer) cEscl.getAttribute("Idescl");
        } else {
            idEsclPadre = (Integer) cEscl.getAttribute("IdesclPadre");
        }

        ViewObject voEsclColleg = am.findViewObject("Sogg_EsclusioniCollegateView1");

        String whcl = "";

        if (idEsclPadre == null) {
            // nessuna escl collegata
            whcl = "1=2";
        } else {
            //07022013 Gaion: aggiunta la ulss tra le where clause. Non e' possibile aggiungere il tpscr perche'
            //la query deve trovare le esclusioni collegate
            String ulss = (String) sess.get("ulss");
            whcl =
                "(IDESCL = " + idEsclPadre.toString() + " or IDESCL_PADRE = " + idEsclPadre.toString() + ") and " +
                "IDESCL <> " + idEscl.toString() + " and ULSS = '" + ulss + "' ";
            //fine 07022013
        }

        voEsclColleg.setWhereClause(whcl);
        voEsclColleg.executeQuery();

        int count = voEsclColleg.getRowCount();

        if (count == 0)
            sess.put("exEsclColleg", Boolean.FALSE);
        else
            sess.put("exEsclColleg", Boolean.TRUE);

        return null;

    }

    public String onConf() {
        boolean esito = this.save();
        if (esito) {
            return "goRic";
        }
        
        return null;
    }


    public String onAppl() {
        this.save();
        
        return null;
    }

    public void onCrea(ActionEvent actionEvent) {
        try {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
            ViewObject voEscl = voIter.getViewObject();
            Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
            Row nEscl = voEscl.createRow();

            Integer idEscl = am.getNextIdEsclusione();
            nEscl.setAttribute("Idescl", idEscl);

            Date dtCorr = DateUtils.getOracleDateNow();
            nEscl.setAttribute("Dtescl", dtCorr);

            ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
            Row cAnag = voAnag.getCurrentRow();
            String codTs = (String) cAnag.getAttribute("Codts");
            nEscl.setAttribute("Codts", codTs);

            nEscl.setAttribute("Ultima", 1);

            Map session = ADFContext.getCurrent().getSessionScope();
            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");
            nEscl.setAttribute("Ulss", ulss);
            nEscl.setAttribute("Tpscr", tpscr);
            nEscl.setAttribute("Tpescl", "D");

            // salvo invito attivo e round
            Integer idInvito = SoggUtils.getInvitoAttivo(am, codTs, ulss, tpscr);
            nEscl.setAttribute("Idinvito", idInvito);

            Integer round = SoggUtils.getRoundComuneSogg(am, codTs, ulss, tpscr);
            nEscl.setAttribute("Roundorgescl", round);

            Integer rIndiv = SoggUtils.getRoundIndiv(am, codTs, ulss, tpscr);
            nEscl.setAttribute("Roundindiv", rIndiv);
            //

            Date dtCrea = DateUtils.getOracleDateNow();
            String user = (String) session.get("user");
            nEscl.setAttribute("Dtcreazione", dtCrea);
            nEscl.setAttribute("Opcreazione", user);

            voEscl.insertRow(nEscl);
            session.put("esisteEscl", Boolean.valueOf(true));
            session.put("esclAppesa", Boolean.valueOf(true));
            SoggUtils.filtraMotEscl("D", false);

            SoggUtils.setupInterfacciaPropagazione(true, am);

            nEscl.setAttribute("Idcnfescl", null);

        } catch (Exception ex) {
            this.handleException(ex.getMessage(), null);
        }


    }

    public void onChTipo(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
        ViewObject voEscl = voIter.getViewObject();
        Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
        Row cEscl = voEscl.getCurrentRow();
        String tipo = (String)valueChangeEvent.getNewValue();
        cEscl.setAttribute("Tpescl",tipo);
        SoggUtils.filtraMotEscl(tipo, true);

        Map session = ADFContext.getCurrent().getSessionScope();
        Boolean esclSalvata = (Boolean) session.get("esclSalvata");
        SoggUtils.setupInterfacciaPropagazione(!esclSalvata.booleanValue(), am);

        cEscl.setAttribute("Idcnfescl", null);

    }

    public void onChMot(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent != null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
        ViewObject voEscl = voIter.getViewObject();
        Row currEscl = voEscl.getCurrentRow();
        Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
        ViewObject voMot = am.findViewObject("Sogg_MotEsclView1");
        RowSetIterator iter = ViewHelper.getRowSetIterator(voMot, "mot_iter");
        Row[] rows = iter.getFilteredRows("Idcnfescl", valueChangeEvent.getNewValue());
        if (rows != null && rows.length==1){
            currEscl.setAttribute("Numgg", rows[0].getAttribute("Numgiorni"));
            currEscl.setAttribute("Idcnfescl", rows[0].getAttribute("Idcnfescl"));
        }
        iter.closeRowSetIterator();
        SoggUtils.setDtfinEscl(currEscl);
        SoggUtils.setIdTpRichiamoEscl(am);

        Map session = ADFContext.getCurrent().getSessionScope();
        Boolean esclSalvata = (Boolean) session.get("esclSalvata");
        SoggUtils.setupInterfacciaPropagazione(!esclSalvata.booleanValue(), am);

    }

    protected boolean beforeSave() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
        ViewObject voEscl = voIter.getViewObject();
        Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
        Row cEscl = voEscl.getCurrentRow();

        Date dtMod = DateUtils.getOracleDateNow();
        Map sess = ADFContext.getCurrent().getSessionScope();
        String user = (String) sess.get("user");

        cEscl.setAttribute("Dtmodescl", dtMod);
        cEscl.setAttribute("Opmodifica", user);

        Integer idEscl = (Integer) cEscl.getAttribute("Idescl");
        String codTs = (String) cEscl.getAttribute("Codts");
        String ulss = (String) cEscl.getAttribute("Ulss");
        String tpscr = (String) cEscl.getAttribute("Tpscr");

        ViewObject voUpdEscl = am.findViewObject("Sogg_UpdEsclusioneView1");
        String whcl =
            "CODTS = '" + codTs + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' and ULTIMA = 1 and IDESCL <> " +
            idEscl.toString();
        voUpdEscl.setWhereClause(whcl);
        voUpdEscl.setRangeSize(-1);
        voUpdEscl.executeQuery();

        int cntEscl = voUpdEscl.getRowCount();
        for (int i = 0; i < cntEscl; i++) {
            Row rEscl = voUpdEscl.getRowAtRangeIndex(i);
            rEscl.setAttribute("Ultima", 0);
        }

        boolean esclSalvata = ((Boolean) sess.get("esclSalvata")).booleanValue();

        if (!esclSalvata) {
            // controllo impostazione per propagazione
            boolean propagaCI = ((Boolean) sess.get("propagaCI")).booleanValue();

            if (!tpscr.equals("CI") && propagaCI) {

                DCIteratorBinding voSelectIter = bindings.findIteratorBinding("Cnf_EsclProposteViewCIIterator");
                Row[] rows = voSelectIter.getAllRowsInRange();
                int marked = 0;
                for (Row row : rows) {
                    if (row.getAttribute("Selected") != null && (Boolean) row.getAttribute("Selected")) {
                        marked++;
                    }
                }
                if (marked > 1) {
                    String msg =
                        "Per la propagazione sullo screening cervicale e' possibile selezionare al massimo un'esclusione. Aggiornamento fallito.";
                    this.handleException(msg, null);
                    return false;
                }

            }

            boolean propagaMA = ((Boolean) sess.get("propagaMA")).booleanValue();

            if (!tpscr.equals("MA") && propagaMA) {
                DCIteratorBinding voSelectIter = bindings.findIteratorBinding("Cnf_EsclProposteViewMAIterator");
                Row[] rows = voSelectIter.getAllRowsInRange();
                int marked = 0;
                for (Row row : rows) {
                    if (row.getAttribute("Selected") != null && (Boolean) row.getAttribute("Selected")) {
                        marked++;
                    }
                }
                if (marked > 1) {
                    String msg =
                        "Per la propagazione sullo screening mammografico e' possibile selezionare al massimo un'esclusione. Aggiornamento fallito.";
                    this.handleException(msg, null);
                    return false;
                }

            }

            boolean propagaCO = ((Boolean) sess.get("propagaCO")).booleanValue();

            if (!tpscr.equals("CO") && propagaCO) {
                DCIteratorBinding voSelectIter = bindings.findIteratorBinding("Cnf_EsclProposteViewCOIterator");
                Row[] rows = voSelectIter.getAllRowsInRange();
                int marked = 0;
                for (Row row : rows) {
                    if (row.getAttribute("Selected") != null && (Boolean) row.getAttribute("Selected")) {
                        marked++;
                    }
                }
                if (marked > 1) {
                    String msg =
                        "Per la propagazione sullo screening del colon e' possibile selezionare al massimo un'esclusione. Aggiornamento fallito.";
                    this.handleException(msg, null);
                    return false;
                }

            }

        }

        return true;
    }

    protected void beforeNavigate(String dest) throws Exception {
        SoggUtils.beforeNavSogg(dest, false);
    }


    private boolean propagaSuScreening(String tpscr, String istVOProposte) {
        Map session = ADFContext.getCurrent().getSessionScope();

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
        ViewObject voEscl = voIter.getViewObject();
        Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
        Row cEscl = voEscl.getCurrentRow();
        String ulss = (String) cEscl.getAttribute("Ulss");
        String codTs = (String) cEscl.getAttribute("Codts");
        Integer idEscl = (Integer) cEscl.getAttribute("Idescl");

        String user = (String) session.get("user");
      
        DCIteratorBinding voSelectIter = bindings.findIteratorBinding(istVOProposte);
        Row[] rows = voSelectIter.getAllRowsInRange();
        Cnf_EsclProposteViewRow esclProp = null;
        for (Row row : rows) {

            if (row.getAttribute("Selected") != null && (Boolean) row.getAttribute("Selected")) {
                esclProp = (Cnf_EsclProposteViewRow) row;
            }
        }

        if (esclProp != null) {

            // 06062011 modificata gaion: aggiunto calcolo data fine esclusione
            String qryEsclPres = "SELECT a.idescl, a.idinvito, c.tpescl, c.predominante, a.dtescl+c.numgiorni, ";
            qryEsclPres +=
                "case when a.dtannull is null and (c.tpescl = 'D' or (c.tpescl = 'T' and a.dtescl + c.numgiorni >= trunc(SYSDATE))) then 1  else 0 end as attiva";
            qryEsclPres += " FROM so_esclusione a, so_cnf_esclusione c";
            qryEsclPres += " where c.idcnfescl = a.idcnfescl";
            qryEsclPres += " and c.ulss = a.ulss";
            qryEsclPres += " and c.tpscr = a.tpscr";
            qryEsclPres += " and a.ultima = 1";
            qryEsclPres += " and a.tpscr = '" + tpscr + "'";
            qryEsclPres += " and a.codts = '" + codTs + "'";
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
                Number esclAttiva = (Number) esclPres.getAttribute(5);

                //06062011 gaion
                //data fine validita' eclusione presente
                Date fineescl_pres = (Date) esclPres.getAttribute(4);
                //20110112 SErra: se e' definitiva la data deve essere sempr emaggiore
                if (fineescl_pres == null)
                    try {
                        fineescl_pres = DateUtils.getOracleDate(new SimpleDateFormat("dd/mm/yyyy").parse("31/12/2999"));
                    } catch (ParseException ex) {
                        //non puo' verificarsi
                    }

                //fine Serra
                //data fine validita' eclusione inserita
                Date fineescl_ins = null;
                if (esclProp.getNumgiorni() != null) {
                    fineescl_ins =
                        (Date) ((Date) cEscl.getAttribute("Dtescl")).addJulianDays(esclProp.getNumgiorni(), 0);
                }
                //fine gaion
                //20110112 SErra: se e' definitiva la data deve essere sempr emaggiore
                else {
                    try {
                        fineescl_ins = DateUtils.getOracleDate(new SimpleDateFormat("dd/mm/yyyy").parse("31/12/2999"));
                    } catch (ParseException ex) {
                        //non puo' verificarsi
                    }
                }
                //fine Serra


                if (esclAttiva.intValue() == 0) {
                    //se la fine validita' della nuova esclusione e' piu' vecchia di quella gia' inserita non faccio niente
                    //07052012 gaion: se l'esclusione precedente era definitiva ed e' stata annullata non confronto le date di fine esclusione
                    if (((String) esclPres.getAttribute(2)).equals("D") || fineescl_pres.compareTo(fineescl_ins) < 0) {
                        // posso inserire ma prima devo dire che l'esclusione non e' 'ultima'
                        inserisci = true;

                        Number esclPresID = (Number) esclPres.getAttribute(0);

                        String updateEsclPres =
                            "update so_esclusione set ULTIMA = 0, " + "DTMODESCL = sysdate, OPMODIFICA = '" + user +
                            "' " + "where IDESCL = " + esclPresID.intValue();

                        am.getTransaction().executeCommand(updateEsclPres);
                    }
                } else {

                    String esclPresTipo = (String) esclPres.getAttribute(2);
                    Number esclPresPred = (Number) esclPres.getAttribute(3);

                    String esclPropTipo = (String) esclProp.getAttribute("TipoEscl");
                    Integer esclPropPred = (Integer) esclProp.getAttribute("Predominante");

                    int esitoConfronto =
                        SoggUtils.confrontaEsclusioni(esclPresTipo, esclPresPred.intValue(), esclPropTipo, esclPropPred,
                                                      fineescl_pres, fineescl_ins);

                    //20110627 M.Serra correzione
                    //  if (esitoConfronto <= 0)
                    if (esitoConfronto < 0)
                    //fine
                    {
                        // posso propagare
                        inserisci = true;

                        Number esclPresID = (Number) esclPres.getAttribute(0);

                        String updateEsclPres =
                            "update so_esclusione set DTANNULL = sysdate, ULTIMA = 0, " +
                            "DTMODESCL = sysdate, OPMODIFICA = '" + user + "' " + "where IDESCL = " +
                            esclPresID.intValue();

                        am.getTransaction().executeCommand(updateEsclPres);

                    }

                }

            }

            if (inserisci) {

                Integer esclPropID = esclProp.getIdcnfescl();
                Date dtEscl = (Date) cEscl.getAttribute("Dtescl");
                String dataEscl = DateUtils.dateToString(dtEscl.getValue());

                Integer idInvito = SoggUtils.getInvitoAttivo(am, codTs, ulss, tpscr);
                String valInvito = (idInvito == null ? "to_number(null)" : idInvito.toString());

                Integer round = SoggUtils.getRoundComuneSogg(am, codTs, ulss, tpscr);
                String valRound = (round == null ? "to_number(null)" : round.toString());

                Integer rIndiv = SoggUtils.getRoundIndiv(am, codTs, ulss, tpscr);
                String valRInd = (rIndiv == null ? "to_number(null)" : rIndiv.toString());

                String noteEscl = (String) cEscl.getAttribute("Noteescl");
                if (noteEscl != null) {
                    noteEscl = noteEscl.replaceAll("'", "''");
                } else {
                    noteEscl = "";
                }

                String insEsclProp =
                    "insert into SO_ESCLUSIONE (IDESCL," + "IDCNFESCL," + "IDINVITO," + "CODTS," + "DTESCL," +
                    "ULTIMA," + "DTRICHIAMO," + "LETTRICHIAMO," + "ROUNDORGESCL," + "ROUNDINDIV," + "DTANNULL," +
                    "DTMODESCL," + "NOTEESCL," + "ULSS," + "TPSCR," + "DTCREAZIONE," + "OPCREAZIONE," + "OPMODIFICA," +
                    "IDESCL_PADRE) values " + "(SO_ESCLUSIONE_SEQ.NEXTVAL, " + esclPropID.intValue() + ", " +
                    valInvito + ", " + "'" + codTs + "', " + "to_date('" + dataEscl + "','dd/mm/yyyy'), " + "1, " +
                    "to_date(null), " + "to_number(null), " + valRound + ", " + valRInd + ", " + "to_date(null), " +
                    "sysdate, " + "'" + noteEscl + "', " + "'" + ulss + "', " + "'" + tpscr + "', " + "sysdate, " +
                    "'" + user + "', " + "'" + user + "', " + idEscl.intValue() + ")";

                am.getTransaction().executeCommand(insEsclProp);

                return true;

            }

        }


        return false;

    }


    protected String afterSave() {
        Map session = ADFContext.getCurrent().getSessionScope();

        boolean esclSalvata = ((Boolean) session.get("esclSalvata")).booleanValue();

        if (!esclSalvata) {
            // gestione propagazione

            DCBindingContainer bindings = (DCBindingContainer) getBindings();
            DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
            ViewObject voEscl = voIter.getViewObject();
            Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
            Row cEscl = voEscl.getCurrentRow();
            String tpscr = (String) cEscl.getAttribute("Tpscr");

            boolean changesToCommit = false;

            boolean propagaCI = ((Boolean) session.get("propagaCI")).booleanValue();

            boolean utilizzoPrecedenza = false;
            String msgPrecedenza = "Esclusioni non inserite per regola di precedenza: ";

            if (!tpscr.equals("CI") && propagaCI) {
                // propagazione su citologico
                String desEscl = this.esclDaPropagare("Cnf_EsclProposteViewCIIterator");

                boolean commitNeeded = propagaSuScreening("CI", "Cnf_EsclProposteViewCIIterator");
                if (commitNeeded)
                    changesToCommit = true;
                else {
                    utilizzoPrecedenza = true;
                    msgPrecedenza += "Screening cervicale: " + desEscl + "; ";
                }
            }

            boolean propagaMA = ((Boolean) session.get("propagaMA")).booleanValue();

            if (!tpscr.equals("MA") && propagaMA) {

                String desEscl = this.esclDaPropagare("Cnf_EsclProposteViewMAIterator");

                // propagazione su mammo
                boolean commitNeeded = propagaSuScreening("MA", "Cnf_EsclProposteViewMAIterator");
                if (commitNeeded)
                    changesToCommit = true;
                else {
                    utilizzoPrecedenza = true;
                    msgPrecedenza += "Screening mammografico: " + desEscl + "; ";
                }
            }

            boolean propagaCO = ((Boolean) session.get("propagaCO")).booleanValue();

            if (!tpscr.equals("CO") && propagaCO) {

                String desEscl = this.esclDaPropagare("Cnf_EsclProposteViewCOIterator");

                // propagazione su colon
                boolean commitNeeded = propagaSuScreening("CO", "Cnf_EsclProposteViewCOIterator");
                if (commitNeeded)
                    changesToCommit = true;
                else {
                    utilizzoPrecedenza = true;
                    msgPrecedenza += "Screening colon retto: " + desEscl + "; ";

                }

            }


            if (changesToCommit)
                am.getTransaction().commit();


            if (utilizzoPrecedenza) {

                int len = msgPrecedenza.length();
                msgPrecedenza = msgPrecedenza.substring(0, len - 2);

                this.handleMessages(FacesMessage.SEVERITY_INFO,msgPrecedenza);
            }


        }

        session.put("esclSalvata", Boolean.valueOf(true));
        session.put("esclAppesa", Boolean.valueOf(false));

        SoggUtils.svuotaInterfacciaPropagazione();
        
        return "";

    }

    protected void doRollback() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
        ViewObject voEscl = voIter.getViewObject();
        Sogg_AppModule am = (Sogg_AppModule) voEscl.getApplicationModule();
        Row cEscl = voEscl.getCurrentRow();
        Integer idEscl = (Integer) cEscl.getAttribute("Idescl");

        String[] views = { "Sogg_RicercaView1" };
        am.doRollback(views);
        SoggUtils.loadVoDet();

        Map sess = ADFContext.getCurrent().getSessionScope();
        boolean esclAppesa = ((Boolean) sess.get("esclAppesa")).booleanValue();

        if (esclAppesa) {
            voEscl.setWhereClause("1=2");
            voEscl.executeQuery();
            Row nEscl = voEscl.createRow();
            StructureDef def = nEscl.getStructureDef();
            AttributeDef[] attrs = def.getAttributeDefs();
            for (int j = 0, numAttrs = attrs.length; j < numAttrs; j++) {
                nEscl.setAttribute(j, cEscl.getAttribute(j));
            }
            voEscl.insertRow(nEscl);
            voEscl.setCurrentRow(nEscl);
        } else {
            voEscl.setWhereClause("IDESCL = " + idEscl.toString());
            voEscl.executeQuery();

        }

    }

    public void onChData(ValueChangeEvent valueChangeEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Sogg_SoEsclusioneView1Iterator");
        Row currEscl = voIter.getCurrentRow();
        SoggUtils.setDtfinEscl(currEscl);
    }

}
