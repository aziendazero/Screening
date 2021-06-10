package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.Ref_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Ref_2livBean;

import model.filters.ViewObjectFilters;

import model.referto.Ref_SoSoggScrViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.backing.ConstantsBean;

import view.providers.Ref_supportForProviders;

public class Ref_referto1livAction extends Ref_DataForwardAction {
    private RichForm refertoForm;

    public Ref_referto1livAction() {
    }

    public void setRefertoForm(RichForm refertoForm) {
        this.refertoForm = refertoForm;
    }

    public RichForm getRefertoForm() {
        if (refertoForm == null) {
            //filtro i dati
            this.filter();
            GestoreRefertiUI.loadFromDB1liv();
        }
        return refertoForm;
    }

    protected void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoRefertocito1livView3Iterator");
        Ref_AppModule am = (Ref_AppModule) voIter.getViewObject().getApplicationModule();
        Row r = voIter.getCurrentRow();

        if (r == null)
            return;

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        Date d = (Date) r.getAttribute("Dtinserimento");
        Date dataReferto = (Date) r.getAttribute("Dtreferto");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //supervisore
        ViewObject vo = am.findViewObject("Ref_SoOpmedicoSupervisoreView1");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_PATOLOGO, ulss, tpscr);

        //citoscreener
        vo = am.findViewObject("Ref_SoOpmedicoCitoscreenerView1");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_CITOSCREENER, ulss, tpscr);

        //adeguatezza del preparato
        vo = am.findViewObject("Ref_SoCnfRef1livADEPREView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //giudizio diagnostico
        vo = am.findViewObject("Ref_SoCnfRef1livGIUDIAView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //limitazione
        vo = am.findViewObject("Ref_SoCnfRef1livINALIMView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //positivi
        vo = am.findViewObject("Ref_SoCnfRef1livPOSITIView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //modificazioni reattive
        /*   vo=am.findViewObject("Ref_SoCnfRef1livMODREAView1");
                    ViewObjectFilters.filterCnfReferti(vo,ulss,tpscr,data);*/

        //esito HPV
        vo = am.findViewObject("Ref_SoCnfRef1livHPVES1View1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //tipi di infezioni per HPV
        vo = am.findViewObject("Ref_SoCnfRef1livHPVTP1View1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //20081210 MOD versione 1.2: prelevatore
        vo = am.findViewObject("Ref_SoOpmedicoView1");
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_OSTETRICA, ulss, tpscr);

        // Suggerimenti
        vo = am.findViewObject("Ref_SoCnfSugg1livView1");
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, dataReferto);

        //dati trial
        vo = am.findViewObject("Ref_SoSoggScrView1");
        vo.setWhereClause("Ref_SoSoggScr.ulss = '" + ulss + "' AND Ref_SoSoggScr.tpscr = '" + tpscr +
                          "' AND Ref_SoSoggScr.codts = '" + r.getAttribute("Codts") + "'");
        vo.executeQuery();
        vo.first();

        //centri di refertazioni
        vo = am.findViewObject("Ref_SoCentroPrelRefView1");
        vo.setWhereClause("ULSS = '" + ulss + "' AND TPSCR = '" + tpscr + "' AND TIPOCENTRO = 3");
        vo.executeQuery();
        
        this.filterSuggerimenti();
    }

    private void filterSuggerimenti() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoCnfSuggCrit1livCitoView1Iterator");
        ViewObject sugg = voIter.getViewObject();  //uso la nuova tabella dei criteri
        Ref_AppModule am = (Ref_AppModule) sugg.getApplicationModule();
        
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        Boolean hpv = (Boolean) session.get("HPV");

        ViewObject ref = am.findViewObject("Ref_SoRefertocito1livView3");
        Row rr = ref.getCurrentRow();

        String where = "Ref_SoCnfSugg1liv.TPSCR = '" + tpscr + "' AND Ref_SoCnfSugg1liv.ULSS = '" + ulss + "' AND ";

        boolean use_hpv = false;
        ViewObject acc = am.findViewObject("Ref_SoAccCito1livView2");
        Row r_acc = acc.first();

        if (r_acc != null &&
            ConfigurationConstants.CODICE_PRELIEVO_HPV_ESEGUITO.equals(r_acc.getAttribute("PrelievoHpv")))
            use_hpv = true;

        if (hpv.booleanValue() || use_hpv) {
            //uso la tabella dei criteri

            ViewObject adepre = am.findViewObject("Ref_SoCodref1livcADEPREView1");
            //filtro in base all'adeguatezza

            Row[] result1 = adepre.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_ADEPRE);
            Integer ad = null;
            if (result1.length == 0) {
                where +=
                    "(Ref_SoCnfCriteriSugg1liv.GRADEPRE = '" + ConfigurationConstants.NOME_GRUPPO_ADEPRE +
                    "' AND Ref_SoCnfCriteriSugg1liv.ADEPRE IS NULL) AND ";
            } else {
                Row r1 = result1[0];
                ad = (Integer) r1.getAttribute("Idcnfref");
                where +=
                    "(Ref_SoCnfCriteriSugg1liv.GRADEPRE = '" + ConfigurationConstants.NOME_GRUPPO_ADEPRE +
                    "' AND Ref_SoCnfCriteriSugg1liv.ADEPRE = " + ad +
                    " OR Ref_SoCnfCriteriSugg1liv.ADEPRE IS NULL) AND ";
            }

            if (ConfigurationConstants.CODICE_ADEPRE_LIMITATO.equals(ad) ||
                ConfigurationConstants.CODICE_ADEPRE_ADEGUATO.equals(ad)) {
                //devo distinguere tra positivo, negativo e modificazioni reattive
                ViewObject giudiaVo = am.findViewObject("Ref_SoCodref1livcGIUDIAView1");

                //filtro in base al giudizio diagnostico
                Row[] result = giudiaVo.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_GIUDIA);

                Integer giudia = null;
                if (result.length == 0) {
                    where +=
                        "(Ref_SoCnfCriteriSugg1liv.GR_GIUDIA = '" + ConfigurationConstants.NOME_GRUPPO_GIUDIA +
                        "' AND Ref_SoCnfCriteriSugg1liv.GIUDIA IS NULL) AND ";
                } else {
                    Row r = result[0];
                    giudia = (Integer) r.getAttribute("Idcnfref");
                    where +=
                        "(Ref_SoCnfCriteriSugg1liv.GR_GIUDIA = '" + ConfigurationConstants.NOME_GRUPPO_GIUDIA +
                        "' AND Ref_SoCnfCriteriSugg1liv.GIUDIA =" + giudia +
                        " OR Ref_SoCnfCriteriSugg1liv.GIUDIA IS NULL) AND ";

                }

                //filtro in base alla positivita
                ViewObject positiviVo = am.findViewObject("Ref_SoCodref1livcView4");
                Row[] result2 = positiviVo.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_POSITI);
                String positivi = "";
                if (result2.length == 0) {
                    where +=
                        "(Ref_SoCnfCriteriSugg1liv.GR_POSITI = '" + ConfigurationConstants.NOME_GRUPPO_POSITI +
                        "' AND Ref_SoCnfCriteriSugg1liv.POSITI IS NULL) AND ";
                } else {
                    for (int i = 0; i < result2.length; i++) {
                        Row r = result2[i];
                        positivi += r.getAttribute("Idcnfref") + ",";
                    }
                    positivi = positivi.substring(0, positivi.length() - 1);
                    where +=
                        "(Ref_SoCnfCriteriSugg1liv.GR_POSITI = '" + ConfigurationConstants.NOME_GRUPPO_POSITI +
                        "' AND Ref_SoCnfCriteriSugg1liv.POSITI IN (" + positivi +
                        ") OR Ref_SoCnfCriteriSugg1liv.POSITI IS NULL) AND ";
                }

            } //inadeguato
            else if (ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.equals(ad)) {
                //devo distinguere tra inadeguato tecnico e flogistico
                Ref_2livBean bean =
                    (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();

                if (bean.getLimitatezza_1l() == null) {
                    where +=
                        "(Ref_SoCnfCriteriSugg1liv.GR_INALIM = '" + ConfigurationConstants.NOME_GRUPPO_INALIM +
                        "' AND Ref_SoCnfCriteriSugg1liv.INALIM IS NULL ) AND ";
                } else {
                    where +=
                        "(Ref_SoCnfCriteriSugg1liv.GR_INALIM = '" + ConfigurationConstants.NOME_GRUPPO_INALIM +
                        "' AND Ref_SoCnfCriteriSugg1liv.INALIM = " + bean.getLimitatezza_1l() +
                        " OR Ref_SoCnfCriteriSugg1liv.INALIM IS NULL ) AND ";
                }
            }

            //parte hpv: filtro anche sul giudizio diagnostico corrispondente

            //il referto hpv va considerato anche se l'azienda non ha il progetto attivo, ma per il paziente e' stato registrato il prelievo hpv
            if (use_hpv) {
                if (rr != null && rr.getAttribute("EsitoHpv") != null) {
                    int n = ((Integer) rr.getAttribute("EsitoHpv")).intValue();
                    where +=
                        "(Ref_SoCnfCriteriSugg1liv.GR_ESITO_HPV = '" + ConfigurationConstants.NOME_GRUPPO_ESITO_HPV +
                        "' AND Ref_SoCnfCriteriSugg1liv.ESITO_HPV =" + rr.getAttribute("EsitoHpv") +
                        " OR Ref_SoCnfCriteriSugg1liv.ESITO_HPV IS NULL) AND ";
                } else {
                    where +=
                        "(Ref_SoCnfCriteriSugg1liv.GR_ESITO_HPV = '" + ConfigurationConstants.NOME_GRUPPO_ESITO_HPV +
                        "' AND Ref_SoCnfCriteriSugg1liv.ESITO_HPV IS NULL) AND ";
                }
            }

            //filtro in base al tipo di invito
            ViewObject invitiVo = am.findViewObject("Ref_SoInvitiPerRefertiView1");
            Row invito = invitiVo.getCurrentRow();
            String tipoInvito = (String) invito.getAttribute("Idtpinvito");
            where +=
                "(Ref_SoCnfCriteriSugg1liv.IDTPINVITO = '" + tipoInvito +
                "' OR Ref_SoCnfCriteriSugg1liv.IDTPINVITO IS NULL) AND ";

            //filtro in base all'eta del soggetto alla data di invito
            Date dataNascita = (Date) invito.getAttribute("DataNascita");
            Date dataInvito = (Date) invito.getAttribute("Dtapp");
            int eta = ViewHelper.etaCompiuta(dataInvito.getValue(), dataNascita.getValue());
            where +=
                "(Ref_SoCnfCriteriSugg1liv.ETAMIN <= " + eta +
                " OR Ref_SoCnfCriteriSugg1liv.ETAMIN IS NULL) AND (Ref_SoCnfCriteriSugg1liv.ETAMAX >= " + eta +
                " OR Ref_SoCnfCriteriSugg1liv.ETAMAX IS NULL ) AND ";

        } else {

            //logica preselezione suggerimenti vecchia cablata qui

            ViewObject adepre = am.findViewObject("Ref_SoCodref1livcADEPREView1");
            //filtro in base all'adeguatezza
            //  Row r1=adepre.first();
            Row[] result1 = adepre.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_ADEPRE);

            if (result1.length == 0) {
                where += "Ref_SoCnfSugg1liv.CODREGIONALE=" + ConfigurationConstants.CODICE_SUGG1L_DEFAULT;
            } else {
                Row r1 = result1[0];
                Integer ad = (Integer) r1.getAttribute("Idcnfref");
                //dato non disponibile
                if (ConfigurationConstants.CODICE_ADEPRE_DEFAULT.equals(ad))
                    where += "Ref_SoCnfSugg1liv.CODREGIONALE=" + ConfigurationConstants.CODICE_SUGG1L_DEFAULT;

                //vetrino danneggiato
                else if (ConfigurationConstants.CODICE_ADEPRE_DANNEGGIATO.equals(ad))
                    where += "Ref_SoCnfSugg1liv.CODREGIONALE=" + ConfigurationConstants.CODICE_SUGG1L_DANNEGGIATO;

                //vetrino non pervenuto
                else if (ConfigurationConstants.CODICE_ADEPRE_NON_PERVENUTO.equals(ad))
                    where += "Ref_SoCnfSugg1liv.CODREGIONALE=" + ConfigurationConstants.CODICE_SUGG1L_NON_PERVENUTO;

                //inadeguato
                else if (ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.equals(ad)) {
                    //devo distinguere tra inadeguato tecnico e flogistico
                    Ref_2livBean bean =
                        (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();

                    if (bean.getLimitatezza_1l() == null)
                        where += "Ref_SoCnfSugg1liv.CODREGIONALE=" + ConfigurationConstants.CODICE_SUGG1L_DEFAULT;
                    else { //inadeguato tecnico
                        if (ConfigurationConstants.CODICE_INALIM_TECNICO.equals(bean.getLimitatezza_1l()))
                            where +=
                                "Ref_SoCnfSugg1liv.CODREGIONALE=" +
                                ConfigurationConstants.CODICE_SUGG1L_INADEGUATO_TECNICO;
                        else
                            //inadeguato flogistico
                            where +=
                                "Ref_SoCnfSugg1liv.CODREGIONALE=" +
                                ConfigurationConstants.CODICE_SUGG1L_INADEGUATO_FLOGISTICO;
                    }
                } else if (ConfigurationConstants.CODICE_ADEPRE_LIMITATO.equals(ad) ||
                           ConfigurationConstants.CODICE_ADEPRE_ADEGUATO.equals(ad)) {
                    //devo distinguere tra positivo, negativo e modificazioni reattive
                    ViewObject giudia = am.findViewObject("Ref_SoCodref1livcGIUDIAView1");

                    //filtro in base al giudizio diagnostico
                    Row[] result = giudia.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_GIUDIA);

                    // Row r=giudia.first();
                    if (result.length == 0)
                        where += "Ref_SoCnfSugg1liv.CODREGIONALE=" + ConfigurationConstants.CODICE_SUGG1L_DEFAULT;
                    else {
                        Row r = result[0];
                        //se e' non e' selezionato un giudizio diagnostico, non ci deve essree un suggeriemnto da scegliere
                        if (ConfigurationConstants.CODICE_GIUDIA_DEFAULT.equals(r.getAttribute("Idcnfref")))
                            where += "Ref_SoCnfSugg1liv.CODREGIONALE=" + ConfigurationConstants.CODICE_SUGG1L_DEFAULT;
                        else
                            where += "Ref_SoCnfSugg1liv.GIUDDIAGNOSTICO=" + r.getAttribute("Idcnfref");
                    }
                }
            }

            //parte hpv: filtro anche sul giudizio diagnostico corrispondente

            //il referto hpv va considerato anche se l'azienda non ha il progetto attivo, ma per il paziente e' stato registrato il prelievo hpv
            if (hpv.booleanValue() || use_hpv) {
                if (rr != null && rr.getAttribute("EsitoHpv") != null) {
                    int n = ((Integer) rr.getAttribute("EsitoHpv")).intValue();
                    if (n > 0)
                        where += " OR Ref_SoCnfSugg1liv.GIUDDIAGNOSTICO=" + rr.getAttribute("EsitoHpv");
                    else
                        where += " OR Ref_SoCnfSugg1liv.CODREGIONALE=" + ConfigurationConstants.CODICE_SUGG1L_DEFAULT;
                }
            }

            where += " AND ";

        }

        // Filtro per data fine validita
        Date dataRefertoHPV = null;
        Date dataRefertoPAP = null;
        Date dataReferto;
        if (rr != null) {
            dataRefertoHPV = (Date) rr.getAttribute("DataRefertoHpv");
            dataRefertoPAP = (Date) rr.getAttribute("DataRefertoPap");
        }

        // Scelgo la data maggiore tra data referto HPV e data referto PAP
        if (dataRefertoHPV == null) {
            dataReferto = dataRefertoPAP;
        } else if (dataRefertoPAP == null) {
            dataReferto = dataRefertoHPV;
        } else {
            if (dataRefertoHPV.compareTo(dataRefertoPAP) > 0) {
                dataReferto = dataRefertoHPV;
            } else {
                dataReferto = dataRefertoPAP;
            }
        }

        if (dataReferto == null) {
            where += "(Ref_SoCnfSugg1liv.DTFINEVALIDITA IS NULL OR Ref_SoCnfSugg1liv.DTFINEVALIDITA > SYSDATE)";
        } else {
            where +=
                "(Ref_SoCnfSugg1liv.DTFINEVALIDITA IS NULL OR Ref_SoCnfSugg1liv.DTFINEVALIDITA > TO_DATE('" +
                dataReferto.dateValue() + "', 'YYYY-MM-DD'))";
        }

        // Non devo permettere all'utente di scegliere il suggerimento 90
        // riservato alla randomizzazione per trial
        boolean completo = false;
        if (rr != null) {
            completo = ConfigurationConstants.DB_TRUE.equals(rr.getAttribute("Completo"));
        }
        if (!completo) {
            where += " AND Ref_SoCnfSugg1liv.IDSUGG <> 90";
        }

        //il sugg REFERTO nON COMPILATO deve essere sempre presente
        where +=
            " ) UNION (SELECT Ref_SoCnfSugg1liv.IDSUGG, " + 
            "Ref_SoCnfSugg1liv.ULSS, " + "Ref_SoCnfSugg1liv.TPSCR, " + "Ref_SoCnfSugg1liv.IDTPINVITO, " +
            "Ref_SoCnfSugg1liv.ESITOSUGG, " + "Ref_SoCnfSugg1liv.DESCRIZIONE, " + "Ref_SoCnfSugg1liv.GGRICHIAMO, " +
            "Ref_SoCnfSugg1liv.INVIAINTERVENTO, " + "Ref_SoCnfSugg1liv.TIPOESAME, " + "Ref_SoCnfSugg1liv.INVIA2LIV, " +
            "Ref_SoCnfSugg1liv.GIUDDIAGNOSTICO, " + "Ref_SoCnfSugg1liv.CODDIP, " + "Ref_SoCnfSugg1liv.CODREGIONALE, " +
            "Ref_SoCnfSugg1liv.ORDINE, " +
            "Ref_SoCnfSugg1liv.DTFINEVALIDITA FROM SO_CNF_SUGG_1LIV Ref_SoCnfSugg1liv " +
            "where  Ref_SoCnfSugg1liv.TPSCR             = '" + tpscr + "' " +
            "AND Ref_SoCnfSugg1liv.ULSS               = '" + ulss + "' " + "AND (Ref_SoCnfSugg1liv.IDSUGG = 0 ";

        //deve essere sempre presente il suggerimento gia' compilato
        if (rr != null) {
            Integer suggOld = (Integer) rr.getAttribute("Idsugg");
            if (suggOld != null && suggOld.intValue() != 0) {
                where += " OR Ref_SoCnfSugg1liv.IDSUGG = " + suggOld;
            }
        }
        where += " )";

        sugg.setWhereClause(where);
        sugg.executeQuery();
        //System.out.println(sugg.getQuery());
    }

    protected void updateDB() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoCodref1livcGIUDIAView1Iterator");
        Row[] result = voIter.getViewObject().getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_GIUDIA);
        if (result.length > 0 && !ConfigurationConstants.CODICE_GIUDIA_POSITIVO.equals(result[0].getAttribute("Idcnfref"))){
            DCIteratorBinding voIterPositi = bindings.findIteratorBinding("Ref_SoCnfRef1livPOSITIView1Iterator");
            Ref_supportForProviders.resetRefSelection(voIterPositi.getViewObject());
        }
        //aggiornamento dati di configurazione positivi
        GestoreRefertiUI.updateDB1liv();
        //filtro l'elenco dei suggerimenti a disposizione
        this.filterSuggerimenti();
    }

    /**
     * Metodo che si occupa di chiudere un referto di 1 livello e sovrascrive
     * il metodo della classe madre:
     * 1. aggiorna il db
     * 2. crea la lettera di referto, se ncessaria
     * 3. imposta il campo completo a true
     * 4. aggiorna i dati dell'invito con il tipo e data richiamo corretti
     * 5. salva il tutto
     * @param ctx
     */
    public void onClose(ClientEvent clientEvent) {
        Ref_AppModule am =
            (Ref_AppModule) BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertocito1livView3");
        ViewObject cnf_adepre = am.findViewObject("Ref_SoCodref1livcADEPREView1");
        ViewObject cnf_giudia = am.findViewObject("Ref_SoCodref1livcGIUDIAView1");

        String to_do =(String)clientEvent.getParameters().get("ref_lettera");
        try {
            Row r=ref.getCurrentRow();
            if (r == null)
                throw new Exception("Nessun referto da chiudere");

            if (r.getAttribute("Idsugg") == null ||
                ConfigurationConstants.CODICE_SUGG1L_DEFAULT.equals(r.getAttribute("Idsugg")))
                throw new Exception("Non e' stata segnalata nessuna azione conseguente");

            /* non faccio questo controllo perche' in referti importati e' un dato che non c'e'!
                    if (r.getAttribute("Idmedref") == null)
                            throw new Exception("Supervisore non inserito");*/

            //aggiorno il db in conseguenza alle selezioni di interfaccia
            this.beforeSavingReferto();

            ViewObject invitiVo = am.findViewObject("Ref_SoInvitiPerRefertiView1");
            Row refertiRow = invitiVo.getCurrentRow();
            String prelievoHpv = null;
            //controllo la congruenza tra accettazione e referto
            Boolean hpvAttivo = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
            if (hpvAttivo != null && hpvAttivo.booleanValue()) {
                prelievoHpv = (String) refertiRow.getAttribute("PrelievoHpv");
                Date dataRefertoHPV = (Date) r.getAttribute("DataRefertoHpv");
                if ("Si".equals(prelievoHpv)) {
                    //obbligatorio inserire un referto HPV
                    if (r.getAttribute("DataRefertoHpv") == null || r.getAttribute("EsitoHpv") == null ||
                        ((Integer) r.getAttribute("EsitoHpv")).intValue() == 0) {
                        throw new Exception("Il prelievo HPV risulta eseguito ma non ne e' registrato l’esito");
                    }
                } else if ("No".equals(prelievoHpv)) {
                    //il referto HPV non deve essere compilato
                    if (r.getAttribute("DataRefertoHpv") != null ||
                        (r.getAttribute("EsitoHpv") != null && ((Integer) r.getAttribute("EsitoHpv")).intValue() > 0)) {
                        throw new Exception("Il prelievo HPV risulta non eseguito ma ne e' registrato l’esito");
                    }
                }
            }

            //MOD 20081008
            //se ho un pap test devo avere un supervisore
            if (r.getAttribute("DataRefertoPap") != null && r.getAttribute("Idmedref") == null)
                throw new Exception("Supervisore non inserito");
            if (r.getAttribute("DataRefertoPap") != null && r.getAttribute("Idcentroref") == null)
                throw new Exception("Centro di refertazione non inserito");
            //se ho un hpv devo avere un esito
            if (r.getAttribute("DataRefertoHpv") != null &&
                (r.getAttribute("EsitoHpv") == null || ((Integer) r.getAttribute("EsitoHpv")).intValue() == 0))
                throw new Exception("Esito test HPV non inserito");
            if (r.getAttribute("DataRefertoHpv") != null && r.getAttribute("LabHpv") == null)
                throw new Exception("Laboratorio refertazione non inserito");

            //Se il giudizio diagnostico e' positivo ci deve essere almeno una causa
            //di positivita'
            this.checkPositivi(cnf_giudia, r, am.findViewObject("Ref_SoCodref1livcView4"));

            // Recupero il trial attualmente configurato
            Integer idTrialAttivoInt = (Integer) ADFContext.getCurrent().getSessionScope().get("trial");
            int idTrialAttivo = idTrialAttivoInt != null ? idTrialAttivoInt.intValue() : 0;

            // Recupero il trial del soggetto
            Integer idTrialNumber = (Integer) refertiRow.getAttribute("Idtrial");
            int idTrialSogg = idTrialNumber != null ? idTrialNumber.intValue() : 0;

            // Trial mRNA
            if (idTrialAttivo == ConfigurationConstants.TRIAL_MRNA && idTrialSogg == idTrialAttivo) {
                boolean isInvitoAttivo = ((Integer) refertiRow.getAttribute("Attivo")).intValue() == 1;

                // Il trial si applica solo su inviti attivi
                if (isInvitoAttivo) {
                    String tipoInvito = (String) refertiRow.getAttribute("Idtpinvito");
                    Integer statoTrialAttr = (Integer) refertiRow.getAttribute("IdstatoTrial");
                    int statoTrial = statoTrialAttr == null ? 0 : statoTrialAttr.intValue();

                    ViewObject referticitoVo = am.findViewObject("Ref_SoRefertocito1livView3");
                    Row referticitoRow = referticitoVo.getCurrentRow();
                    int idSugg = ((Integer) referticitoRow.getAttribute("Idsugg")).intValue();

                    if (("1 ".equals(tipoInvito) || "2 ".equals(tipoInvito) || "10".equals(tipoInvito)) &&
                        statoTrial == 2 && idSugg == 22) {
                        // Determino il braccio tramite randomizzazione (50/50) 1=Braccio A, 2=Braccio B
                        int braccio = (int) (Math.random() + .5) + 1;

                        ViewObject soggettoScrView = am.findViewObject("Ref_SoSoggScrView1");
                        if (soggettoScrView != null) {
                            soggettoScrView.setWhereClause("Ref_SoSoggScr.ulss = '" + r.getAttribute("Ulss") +
                                                           "' AND Ref_SoSoggScr.tpscr = '" + r.getAttribute("Tpscr") +
                                                           "' AND Ref_SoSoggScr.codts = '" + r.getAttribute("Codts") +
                                                           "'");
                            soggettoScrView.executeQuery();
                            Ref_SoSoggScrViewRowImpl soggettoScrRow =
                                (Ref_SoSoggScrViewRowImpl) soggettoScrView.first();
                            if (soggettoScrRow != null) {
                                soggettoScrRow.refresh(Row.REFRESH_CONTAINEES);
                                soggettoScrRow.setAttribute("IdbraccioTrial", new Integer(braccio));
                            }
                        }

                        // Per il braccio A forzo il suggerimento "Invio al secondo livello per randomizzazione"
                        if (braccio == 1) {
                            referticitoRow.setAttribute("Idsugg", new Integer(90));
                        }
                    }
                }
            }

            String test_proposto = (String) refertiRow.getAttribute("TestProposto");

            //creo la lettera di referto
            GestoreReferti gr = new GestoreReferti(am);

            //se devo ricreare la lettera la cancello e ricreo
            if ("ricrea".equals(to_do)) {
                gr.deleteLettera(r);
                gr.creaLetteraDiRefertoCI(r, cnf_adepre, cnf_giudia, 1, null, test_proposto);
            }

            //se la lettera non c'e' la creo semplicemente
            else if ("crea".equals(to_do)) {
                gr.creaLetteraDiRefertoCI(r, cnf_adepre, cnf_giudia, 1, null, test_proposto);
            }

            //il referto e' completo
            r.setAttribute("Completo", ConfigurationConstants.DB_TRUE);

            //imposto il richiamo
            gr.updateInvito(r, am.findViewObject("Ref_SoInvitoView2"), 1,
                            (String) ADFContext.getCurrent().getSessionScope().get("user"));

            if (hpvAttivo != null && hpvAttivo.booleanValue()) {
                //aggiorno il round individuale HPV
                gr.updateRoundIndivHPV(r, am.findViewObject("Ref_SoInvitoView2"));
            }
            
            //aggiorno il centro di prelievo dell'invito
            Integer idCentroRef = (Integer) r.getAttribute("Idcentroref");
            if (idCentroRef != null)
                refertiRow.setAttribute("Idcentroref1liv",idCentroRef);

            Ref_2livBean bean =
                (Ref_2livBean) BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
            bean.setDirty(false);

            //journaling
           Map sess = ADFContext.getCurrent().getSessionScope();
            Boolean flag = (Boolean) sess.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) sess.get("user");
                String tpscr = (String) sess.get("scr");
                String ulss = (String) sess.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }

            //salvo il tutto
            am.getTransaction().commit();
            
            cnf_adepre.executeQuery();
            cnf_giudia.executeQuery();
            
            //dopo la chiusura la lista dei suggerimenti disponibili puo' variare (es si aggiunge il suggerimento 90),
            //quindi rieseguo il filtro
            filterSuggerimenti();
            
            FacesContext fc = FacesContext.getCurrentInstance();
            String refreshpage = fc.getViewRoot().getViewId();
            ViewHandler ViewH = fc.getApplication().getViewHandler();
            UIViewRoot UIV = ViewH.createView(fc,refreshpage);
            UIV.setViewId(refreshpage);
            fc.setViewRoot(UIV);

        } catch (Exception ex) {     
            ViewObject vo = am.findViewObject("Ref_SoCnfRef1livPOSITIView1");
            vo.executeQuery();
            
            ex.printStackTrace();
            this.handleException("Impossibile chiudere il referto: " + ex.getMessage(), null);      
            
        }
        
        
    }

    protected void loadFromDB() {
        GestoreRefertiUI.loadFromDB1liv();
    }

    protected void checkMandatoryData(Row ref) throws Exception {
        Date d_hpv = (Date) ref.getAttribute("DataRefertoHpv");
        Date d_pap = (Date) ref.getAttribute("DataRefertoPap");
        if (d_hpv == null && d_pap == null)
            throw new Exception("e' necessario inserire la data del referto");

        //imposto la data del referto come la data maggiore tra le due date di referto
        if (d_hpv == null)
            ref.setAttribute("Dtreferto", d_pap);
        else if (d_pap == null)
            ref.setAttribute("Dtreferto", d_hpv);
        else {
            if (d_hpv.compareTo(d_pap) > 0)
                ref.setAttribute("Dtreferto", d_hpv);
            else
                ref.setAttribute("Dtreferto", d_pap);
        }
    }

    /**
     * Metodo che controlla se il referto ha giudizio diagnostico positivo ed,
     * in tal caso, se ha almeno un morivo di positivita'
     * @throws java.lang.Exception
     * @param cnf viewobject delle configurzioni dipendente dal referto
     * @param referto record con il referto
     * @param giudia viewobject del giudizio diagnostico dipendente dal referto
     */
    private void checkPositivi(ViewObject giudia, Row referto, ViewObject cnf) throws Exception {
        //cerco il giudizio diagnostico
        Row[] result = giudia.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_GIUDIA);
        //se non c'e' non ho controlli da fare
        if (result.length == 0)
            return;

        //c'e':se non e' positivo non ho controlli da fare
        if (!ConfigurationConstants.CODICE_GIUDIA_POSITIVO.equals(result[0].getAttribute("Idcnfref")))
            return;

        //altrimenti controllo che esista almeno un motivo di positivita'
        result = cnf.getFilteredRows("Gruppo", ConfigurationConstants.NOME_GRUPPO_POSITI);
        if (result.length == 0)
            throw new Exception("Il referto ha giudizio diagnostico positivo ma non e' stato indicato nessun tipo di positivita'");

        /*cnf.setWhereClause("GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_POSITI+
            "' AND IDREFERTO="+referto.getAttribute("Idreferto"));
            cnf.executeQuery();

            if(cnf.getRowCount()==0)
            throw new Exception("Il referto ha giudizio diagnostico positivo ma non e' stato indicato nessun tipo di positivita'");
            */
    }

    public void onReopen() {
        super.onReopen();
        this.filter();
        GestoreRefertiUI.loadFromDB1liv();
    }

    public void onChangeDataReferto(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance()); 
        
        filterSuggerimenti();
    }
}
