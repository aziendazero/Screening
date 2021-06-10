package view.print;

import insiel.utilities.dataformats.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.List;

import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;

import model.common.PL_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.Lettera;
import model.commons.LetteraBean;
import model.commons.LetteraEtichetta;
import model.commons.PianoLavoroBean;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.datacontrol.PL_Bean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

import view.commons.action.Parent_DataForwardAction;

public class Print_PLAction extends Parent_DataForwardAction {

    private RichForm pianoForm;
    private RichPopup pdfPopUp;

    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }
    
    public String stampaUrl() throws Exception {
        PL_AppModule am =
            (PL_AppModule) BindingContext.getCurrent().findDataControl("PL_AppModuleDataControl").getDataProvider();
        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        ViewObject vo = null;
        File f = null;
        Lettera l = null;

        try {
            if (bean.getCentro() == null || bean.getCentro().intValue() < 0)
                throw new Exception("E' necessario selezionare un centro di prelievo");

            if (bean.getData() == null)
                throw new Exception("E' necessario inserire la data");

            if (bean.getTemplate() == null || bean.getTemplate().intValue() < 0)
                throw new Exception("E' necessario selezionare un modello di piano di lavoro");

            if ("single".equals(bean.getType())) {
                if (bean.getCodts() == null || bean.getCodts().length() == 0)
                    throw new Exception("E' necessario inserire la chiave del soggetto");
            }

            // mauro 11/06/2010: modificata query per aggiunta dati medico
            String query =
                "SELECT PL_SoInvito.idinvito,\n" + "PL_SoInvito.dtapp,\n" + "PL_SoInvito.oraapp,\n" +
                "PL_SoInvito.minapp,\n" + "PL_SoSoggetto.COGNOME,\n" + "PL_SoSoggetto.NOME,\n" +
                "PL_SoSoggetto.DATA_NASCITA,\n" +
                "NVL(TRIM(PL_SoSoggetto.INDIRIZZO_SCR), (NVL(TRIM(PL_SoSoggetto.INDIRIZZO_DOM),PL_SoSoggetto.INDIRIZZO_RES))) INDIRIZZO,\n" +
                "TRIM(NVL(PL_SoSoggetto.TEL1, '') || ' ' || NVL(PL_SoSoggetto.TEL2, '')) TEL,\n" +
                "PL_SoSoggetto.CODTS,\n" + "PL_SoSoggetto.ULSS,\n" + "PL_SoSoggetto.CF,\n" +
                "PL_SoComune.DESCRIZIONE AS COMUNE,\n" + "PL_SoComune.CODPR,\n" +

                // CAP
                "DECODE(TRIM(PL_SoSoggetto.indirizzo_scr), null, " +
                "DECODE(TRIM(PL_SoSoggetto.indirizzo_dom), null, NVL(PL_SoSoggetto.cap_comres, PL_SoComune.cap), " +
                "NVL(PL_SoSoggetto.cap_comdom, PL_SoComune.cap)), " + "NVL(PL_SoSoggetto.cap_comsrc,PL_SoComune.cap)) AS CAP,\n" +

                "PL_SoCentroPrelRef.DESCRIZIONE AS CENTRO,\n" + "PL_SoCentroPrelRef.INDIRIZZO_RES,\n" +
                "PL_SoCnfTpinvito.DESCRIZIONE AS TIPO_INVITO,\n" + "PL_SoCnfEsitoinvito.CODESITOINVITO,\n" +
                "PL_SoCnfEsitoinvito.ESITOINVITO,\n" +

                // Cod campione
                (String) ViewHelper.decodeByTpscr(tpscr, "PL_SoAccCito1liv.NUMVETRINO AS COD_CAMPIONE,\n",
                                                  "A_SoAccColon1liv.COD_CAMPIONE,\n", "null AS COD_CAMPIONE,\n",
                                                  "null AS COD_CAMPIONE,\n", "DECODE(PL_SoInvito.livello, 1,A_SoAccPfas1liv.COD_CAMPIONE, 2, A_SoAccPfas2liv.COD_CAMPIONE) AS COD_CAMPIONE,\n") +

                // Cod richiesta



                (String) ViewHelper.decodeByTpscr(tpscr,
                                                  "DECODE(PL_SoInvito.livello, 1, PL_SoAccCito1liv.COD_RICHIESTA, 2, A_SoAccCito2liv.COD_RICHIESTA) AS COD_RICHIESTA",
                                                  //mauro 8-10-2008, aggiunto cod. richiesta per cito
                                                  "DECODE(PL_SoInvito.livello, 1, A_SoAccColon1liv.COD_RICHIESTA, 2, A_SoAccColon2liv.COD_RICHIESTA) AS COD_RICHIESTA",
                                                  "DECODE(PL_SoInvito.livello, 1, A_SoAccMammo1liv.COD_RICHIESTA, 2, A_SoAccMammo2liv.COD_RICHIESTA) AS COD_RICHIESTA",
                                                  "null AS COD_RICHIESTA",
                                                  "DECODE(PL_SoInvito.livello, 1, A_SoAccPfas1liv.COD_RICHIESTA, 2, A_SoAccPfas2liv.COD_RICHIESTA) AS COD_RICHIESTA") + ",\n" +

                "to_char(PL_SoSoggScr.ROUNDINDIV) ROUNDINDIV,\n" + "PL_SoSoggScr.CODIDSOGG_MX,\n" + //mauro 8-10-2008, aggiunto cod. archiviazione
                "A_SoMedico.NOME AS NOME1,\n" + "A_SoMedico.CODICEREGMEDICO,\n" + "A_SoMedico.COGNOME AS COGNOME1,\n" +
                "A_SoMedico.INDIRIZZO_RES AS INDIRIZZO_RES2,\n" + "A_SoComune3.DESCRIZIONE AS DESCRIZIONE4,\n" +
                "A_SoComune3.CODCOM AS CODCOM3,\n" + "A_SoComune3.CODPR AS CODPR3,\n" + "A_SoComune3.CAP AS CAP3,\n" +
                "PL_SoSoggScr.MX45_BRACCIO,\n" + //03052013 GAION, aggiunto per progetto studio 45 mx
                "PL_SoSoggScr.MX45_CODICE,\n" + //03052013 GAION, aggiunto per progetto studio 45 mx
                "PL_SoSoggScr.CODIDSOGG_MX,\n" + //30092013 Gaion
                "Cnf_SoCnfTrialStato.DESCR TRIAL_STATO_DESCR,\n" +
                "Cnf_SoCnfTrialBraccio.DESCR TRIAL_BRACCIO_DESCR,\n" + "PL_SoSoggetto.EMAIL,\n" + //05112014 GAION
                "PL_SoSoggetto.CELLULARE,\n" + //05112014 GAION
                "PL_SoInvito.TEST_PROPOSTO,\n" +
                //Consenso
                "PL_SoSoggScr.CONSENSO_COND,\n" + "PL_SoSoggScr.CONSENSO,\n" +
                "PL_SoInvito.CONFERMATO,\n" +
                "Print_SoCentroPrelRef1.DESCRIZIONE AS CENTRO_REF,\n" + 
                // Prelievo HPV
                (String) ViewHelper.decodeByTpscr(tpscr,
                                                  "DECODE(PL_SoInvito.CODESITOINVITO || PL_SoInvito.LIVELLO || PL_SoAccCito1liv.PRELIEVO_HPV, 'S11', 'No', 'S12', 'Si', null)",
                                                  "null", "null", "null", "null") + " AS PRELIEVO_HPV,\n" +
                "PL_SoInvito.OTP AS OTP\n" + // I00098760


                // FROM
                "FROM SO_INVITO PL_SoInvito,\n" + "SO_CNF_TPINVITO PL_SoCnfTpinvito,\n" +
                "SO_CNF_ESITOINVITO PL_SoCnfEsitoinvito,\n" + "SO_CENTRO_PREL_REF PL_SoCentroPrelRef,\n" +
                "SO_SOGGETTO PL_SoSoggetto,\n" + "SO_COMUNE PL_SoComune,\n" + "SO_SOGG_SCR PL_SoSoggScr,\n" +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_ACC_CITO1LIV PL_SoAccCito1liv,\n",
                                                  "SO_ACC_COLON1LIV A_SoAccColon1liv,\n",
                                                  "SO_ACC_MAMMO1LIV A_SoAccMammo1liv,\n", "",
                                                  "SO_ACC_PFAS1LIV A_SoAccPfas1liv,\n") +
                "SO_ACC_MAMMO2LIV A_SoAccMammo2liv,\n" + "SO_ACC_COLON2LIV A_SoAccColon2liv,\n" +
                "SO_ACC_CITO2LIV A_SoAccCito2liv,\n" + "SO_ACC_PFAS2LIV A_SoAccPfas2liv,\n" +
                "SO_MEDICO A_SoMedico,\n" + "SO_COMUNE A_SoComune3,\n" + "SO_CNF_TRIAL_STATO Cnf_SoCnfTrialStato,\n" +
                "SO_CNF_TRIAL_BRACCIO Cnf_SoCnfTrialBraccio,\n" + "SO_CENTRO_PREL_REF Print_SoCentroPrelRef1\n" +

                // WHERE
                "WHERE\n" +

                //filtro gli inviti da stampare in base a centro e periodo temporale
                "PL_SoInvito.IDCENTROPRELIEVO = " + bean.getCentro() + " AND ";

            //data unica
            if (bean.getData_al() == null || "single".equals(bean.getType())) {
                query +=
                    "PL_SoInvito.DTAPP = TO_DATE('" + DateUtils.dateToString(bean.getData()) + "','" +
                    DateUtils.DATE_PATTERN + "') AND " + "PL_SoInvito.ULSS='" + ulss + "' AND PL_SoInvito.TPSCR='" +
                    tpscr + "' AND ";
            }

            //periodo temporale
            else {
                query +=
                    "PL_SoInvito.DTAPP >= TO_DATE('" + DateUtils.dateToString(bean.getData()) + "','" +
                    DateUtils.DATE_PATTERN + "') AND " + "PL_SoInvito.DTAPP < TO_DATE('" +
                    DateUtils.dateToString(bean.getData_al()) + "','" + DateUtils.DATE_PATTERN + "') AND " +
                    "PL_SoInvito.ULSS='" + ulss + "' AND PL_SoInvito.TPSCR='" + tpscr + "' AND ";
            }

            if ("single".equals(bean.getType())) {

                query += "PL_SoSoggetto.CODTS = '" + bean.getCodts() + "'\nAND ";
            }

            query +=
                "(PL_SoInvito.CODTS = PL_SoSoggetto.CODTS AND PL_SoInvito.ULSS=PL_SoSoggetto.ULSS)\n" +
                "AND DECODE(TRIM(PL_SoSoggetto.indirizzo_scr), null, " +
                "DECODE(TRIM(PL_SoSoggetto.indirizzo_dom), null, PL_SoSoggetto.RELEASE_CODE_COM_RES, PL_SoSoggetto.RELEASE_CODE_COM_DOM), PL_SoSoggetto.RELEASE_CODE_COM_SCR) = PL_SoComune.RELEASE_CODE\n" + "AND PL_SoInvito.idcentroprelievo = PL_SoCentroPrelRef.IDCENTRO\n" +

                "AND PL_SoInvito.IDTPINVITO = PL_SoCnfTpinvito.IDTPINVITO\n" +
                "AND PL_SoInvito.ULSS = PL_SoCnfTpinvito.ULSS\n" + "AND PL_SoInvito.TPSCR = PL_SoCnfTpinvito.TPSCR\n" +

                "AND (PL_SoInvito.CODESITOINVITO = PL_SoCnfEsitoinvito.CODESITOINVITO\n" +
                "AND PL_SoInvito.LIVESITO = PL_SoCnfEsitoinvito.LIVESITO\n" +
                "AND PL_SoInvito.ULSS = PL_SoCnfEsitoinvito.ULSS\n" +
                "AND PL_SoInvito.TPSCR = PL_SoCnfEsitoinvito.TPSCR)\n" +
                "AND PL_SoSoggScr.CODTS = PL_SoInvito.CODTS\n" + "AND PL_SoSoggScr.ULSS = PL_SoInvito.ULSS\n" +
                "AND PL_SoSoggScr.TPSCR = PL_SoInvito.TPSCR\n" +
                (String) ViewHelper.decodeByTpscr(tpscr, "AND (PL_SoAccCito1liv.IDINVITO (+)= PL_SoInvito.IDINVITO)",
                                                  "AND (A_SoAccColon1liv.IDINVITO (+)= PL_SoInvito.IDINVITO)",
                                                  "AND (A_SoAccMammo1liv.IDINVITO (+)= PL_SoInvito.IDINVITO)", "",
                                                  "AND (A_SoAccPfas1liv.IDINVITO (+)= PL_SoInvito.IDINVITO)") + "\n" +
                "AND A_SoAccMammo2liv.IDINVITO (+)= PL_SoInvito.IDINVITO\n" +
                "AND A_SoAccColon2liv.IDINVITO (+)= PL_SoInvito.IDINVITO\n" +
                "AND A_SoAccCito2liv.IDINVITO (+)= PL_SoInvito.IDINVITO\n" +
                "AND A_SoAccPfas2liv.IDINVITO (+)= PL_SoInvito.IDINVITO\n" +
                "AND A_SoMedico.CODICEREGMEDICO (+)= PL_SoSoggetto.CODICEREGMEDICO\n" +
                "AND A_SoMedico.ulss (+)= PL_SoSoggetto.ulss\n" +
                "AND A_SoComune3.RELEASE_CODE (+)= A_SoMedico.RELEASE_CODE_COM\n" +
                "AND PL_SoInvito.IDCENTROREF1LIV (+)  = Print_SoCentroPrelRef1.IDCENTRO\n" +
                "AND Cnf_SoCnfTrialStato.IDSTATO_TRIAL (+) = PL_SoSoggScr.IDSTATO_TRIAL\n" +
                "AND Cnf_SoCnfTrialStato.IDTRIAL (+) = PL_SoSoggScr.IDTRIAL\n" +
                "AND Cnf_SoCnfTrialStato.TPSCR (+) = PL_SoSoggScr.TPSCR\n" +
                "AND Cnf_SoCnfTrialBraccio.IDBRACCIO_TRIAL (+) = PL_SoSoggScr.IDBRACCIO_TRIAL\n" +
                "AND Cnf_SoCnfTrialBraccio.IDTRIAL (+) = PL_SoSoggScr.IDTRIAL\n" +
                "AND Cnf_SoCnfTrialBraccio.TPSCR (+) = PL_SoSoggScr.TPSCR\n";

            //impongo l'eventuale ordinamento
            if ("temporale".equals(bean.getOrdine())) {
                //ordinamento temporale
                query += " order by Pl_SoInvito.DTAPP, Pl_SoInvito.ORAAPP, Pl_SoInvito.MINAPP";
            } else if ("alfabetico".equals(bean.getOrdine())) {
                //ordinamento alfabetico
                query += " ORDER BY Pl_SoSoggetto.COGNOME, Pl_SoSoggetto.NOME";
            } else {
                //numero del campione, l'attributo dipende dal tipo di screening
                if ("CI".equals(tpscr))
                    //default per il citologico
                    query += " order by PL_SoAccCito1liv.NUMVETRINO";
                else if ("CO".equals(tpscr))
                    query +=  "order by A_SoAccColon1liv.COD_CAMPIONE";
            }

            vo = am.createViewObjectFromQueryStmt("appuntamenti", query);

            //evita il caching e risparmia memoria
            vo.setForwardOnly(true);

            //recupero il template
            ViewObject temp = am.findViewObject("PL_SoTemplateView1");
            Row[] result = temp.getFilteredRows("Codtempl", bean.getTemplate());
            if (result.length == 0)
                throw new Exception("Template selezionato non trovato");

            String fileName = (String) result[0].getAttribute("Nomefile");
            if (fileName.indexOf(".") >= 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

            l = new Lettera(result[0], "PL_SoTemplateView1", "Filexml", "Compiled", fileName,
                            ConfigurationConstants.FORMATO_PDF);

            l.setParametersMap(ulss, tpscr, am.findViewObject("A_SoAziendaView1"),
                               am.findViewObject("PL_SoCnfPartemplateView1"));
            Integer tipo_lettera = (Integer) result[0].getAttribute("Idtplettera");

            //elenco esiti refertabili
            String[] esiti_array =
                (String[]) ViewHelper.decodeByTpscr(tpscr, ConfigurationConstants.CODICI_ESITI_REFERTABILI,
                                                    ConfigurationConstants.CODICI_ESITI_REFERTABILI_CO,
                                                    ConfigurationConstants.CODICI_ESITI_REFERTABILI,
                                                    ConfigurationConstants.CODICI_ESITI_REFERTABILI,
                                                    ConfigurationConstants.CODICI_ESITI_REFERTABILI);

            HashMap esiti = new HashMap();
            //lo trasformo in hashmap
            for (int h = 0; h < esiti_array.length; h++)
                esiti.put(esiti_array[h], Boolean.TRUE);

            Vector array = new Vector();
            //per ogni soggetto trovato con la ricerca

            while (vo.hasNext()) {
                Row r = vo.next();

                //controllo se il soggetto non abbia gia' disdetto
                if (esiti.get(r.getAttribute("CODESITOINVITO")) == null)
                    continue;

                //trovo la tessera sanitaria
                String tesseraSanitaria =
                    SoggUtils.trovaTessera(am, (String) r.getAttribute("CODTS"), (String) r.getAttribute("ULSS"));

                LetteraBean pl = null;
                if (ConfigurationConstants.CODICE_PIANO_LAVORO.equals(tipo_lettera)) {

                    //creo un record da passare al report
                    pl =
                        new PianoLavoroBean(r.getAttribute("CENTRO") + ", " + r.getAttribute("INDIRIZZO_RES"),
                                            r.getAttribute("DTAPP") == null ? null :
                                            DateUtils.dateToString(((Date) r.getAttribute("DTAPP")).getValue()),
                                            (String) r.getAttribute("COGNOME"), (String) r.getAttribute("NOME"),
                                            (String) r.getAttribute("TIPO_INVITO"),
                                            DateUtils.dateToString(((Date) r.getAttribute("DATA_NASCITA")).getValue()),
                                            (String) r.getAttribute("INDIRIZZO"),
                                            tesseraSanitaria,
                                            //(String)r.getAttribute("CODTS"),
                                            ViewHelper.formatTime((Number) r.getAttribute("ORAAPP"),
                                                                  (Number) r.getAttribute("MINAPP")),
                                            (String) r.getAttribute("ESITOINVITO"), null,
                                            //escl def
                                            null,
                                            //escl temp
                                            r.getAttribute("COD_CAMPIONE") != null ?
                                            r.getAttribute("COD_CAMPIONE").toString() : null,
                                            (String) r.getAttribute("COMUNE"), (String) r.getAttribute("CODPR"),
                                            (String) r.getAttribute("TEL"), (String) r.getAttribute("CAP"),
                                            DateUtils.getToday(), (String) r.getAttribute("CF"),
                                            (String) r.getAttribute("ROUNDINDIV"),
                                            r.getAttribute("COD_RICHIESTA") != null ?
                                            r.getAttribute("COD_RICHIESTA").toString() : null,
                                            (String) r.getAttribute("CODIDSOGG_MX"),
                                            (String) r.getAttribute("MX45_BRACCIO"),
                                            (String) r.getAttribute("MX45_CODICE"),
                                            (String) r.getAttribute("CODIDSOGG_MX"),
                                            (String) r.getAttribute("TRIAL_STATO_DESCR"),
                                            (String) r.getAttribute("TRIAL_BRACCIO_DESCR"),
                                            (String) r.getAttribute("EMAIL"), (String) r.getAttribute("CELLULARE"),
                                            (String) r.getAttribute("COGNOME1"),
                                            (String) r.getAttribute("NOME1"),

                                            (r.getAttribute("CONSENSO") != null ? (r.getAttribute("CONSENSO")).toString() : null),
                                            (r.getAttribute("CONSENSO_COND") != null ? (r.getAttribute("CONSENSO_COND")).toString() : null),
                                            (r.getAttribute("CONFERMATO") != null ? (r.getAttribute("CONFERMATO")).toString() : null),
                                            (String) r.getAttribute("OTP"));
                    ((PianoLavoroBean) pl).setTest_proposto((String) r.getAttribute("TEST_PROPOSTO"));
                    ((PianoLavoroBean) pl).setPrelievo_hpv((String) r.getAttribute("PRELIEVO_HPV"));
                } else if (ConfigurationConstants.CODICE_ETICHETTE.equals(tipo_lettera)) {
                    // mauro 11/06/2010, utilizzo nuovo costruttore per stampa dati del medico
                    // 3092013 gaion, nuovi parametri per mammografico
                    pl =
                        new LetteraEtichetta((String) r.getAttribute("COGNOME"), (String) r.getAttribute("NOME"),
                                             (String) r.getAttribute("INDIRIZZO"), (String) r.getAttribute("CAP"),
                                             (String) r.getAttribute("COMUNE"), (String) r.getAttribute("CODPR"),
                                             DateUtils.getToday(), tesseraSanitaria, //(String)r.getAttribute("CODTS"),
                                             (String) r.getAttribute("CF"),
                                             DateUtils.dateToString(((Date) r.getAttribute("DATA_NASCITA")).getValue()),
                                             (String) r.getAttribute("TEL"), (String) r.getAttribute("CENTRO"),
                                             r.getAttribute("COD_CAMPIONE") != null ?
                                             r.getAttribute("COD_CAMPIONE").toString() : null,
                                             r.getAttribute("COD_RICHIESTA") != null ?
                                             r.getAttribute("COD_RICHIESTA").toString() : null,
                                             r.getAttribute("DTAPP") == null ? null :
                                             DateUtils.dateToString(((Date) r.getAttribute("DTAPP")).getValue()),
                                             (String) r.getAttribute("CODIDSOGG_MX"), (String) r.getAttribute("NOME1"),
                                             (String) r.getAttribute("COGNOME1"),
                                             (String) r.getAttribute("INDIRIZZO_RES2"), (String) r.getAttribute("CAP3"),
                                             (String) r.getAttribute("DESCRIZIONE4"), (String) r.getAttribute("CODPR3"),
                                             (String) r.getAttribute("MX45_BRACCIO"),
                                             (String) r.getAttribute("MX45_CODICE"),
                                             (String) r.getAttribute("CODIDSOGG_MX"),
                                             (String) r.getAttribute("TRIAL_STATO_DESCR"),
                                             (String) r.getAttribute("TRIAL_BRACCIO_DESCR"),
                                             (String) r.getAttribute("EMAIL"), (String) r.getAttribute("CELLULARE"),
                                             (r.getAttribute("CONFERMATO") != null ? r.getAttribute("CONFERMATO").toString() : null),
                                             (String) r.getAttribute("TIPO_INVITO"),
                                             (String) r.getAttribute("CENTRO_REF"),
                                             (String) r.getAttribute("OTP"));
                    ((LetteraEtichetta) pl).setTest_proposto((String) r.getAttribute("TEST_PROPOSTO"));
                    ((LetteraEtichetta) pl).setPrelievo_hpv((String) r.getAttribute("PRELIEVO_HPV"));
                    if (bean.getIdproc() != null) //codice procedura pfas 2 liv
                        ((LetteraEtichetta) pl).setId_procedura(bean.getIdproc().toString());
                    if (bean.getData_proc() != null) //data procedura pfas 2 liv
                        ((LetteraEtichetta) pl).setData_procedura(DateUtils.dateToString(bean.getData_proc()));
                } else
                    throw new Exception("Template con codice tipo lettera " + tipo_lettera + " non previsto");

                //e lo inserisco nell'array
                array.addElement(pl);
            }

            LetteraBean[] bean_array = new LetteraBean[array.size()];
            for (int h = 0; h < bean_array.length; h++)
                bean_array[h] = (LetteraBean) array.elementAt(h);

            f = l.createLetter(bean_array);

            return f.getAbsolutePath();

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new Exception("Impossibile creare il piano di lavoro: " + ex.getMessage());

        } finally {

            if (vo != null) {
                vo.remove();
                vo = null;
            }
        }
    }

    public void stampa(FacesContext facesContext, OutputStream outputStream) throws Exception {
        PL_AppModule am =
            (PL_AppModule) BindingContext.getCurrent().findDataControl("PL_AppModuleDataControl").getDataProvider();
        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        ViewObject vo = null;
        File f = null;
        Lettera l = null;

        try {
            if (bean.getCentro() == null || bean.getCentro().intValue() < 0)
                throw new Exception("E' necessario selezionare un centro di prelievo");

            if (bean.getData() == null)
                throw new Exception("E' necessario inserire la data");

            if (bean.getTemplate() == null || bean.getTemplate().intValue() < 0)
                throw new Exception("E' necessario selezionare un modello di piano di lavoro");

            if ("single".equals(bean.getType())) {
                if (bean.getCodts() == null || bean.getCodts().length() == 0)
                    throw new Exception("E' necessario inserire la chiave del soggetto");
            }

            // mauro 11/06/2010: modificata query per aggiunta dati medico
            String query =
                "SELECT PL_SoInvito.idinvito,\n" + "PL_SoInvito.dtapp,\n" + "PL_SoInvito.oraapp,\n" +
                "PL_SoInvito.minapp,\n" + "PL_SoSoggetto.COGNOME,\n" + "PL_SoSoggetto.NOME,\n" +
                "PL_SoSoggetto.DATA_NASCITA,\n" +
                "NVL(TRIM(PL_SoSoggetto.INDIRIZZO_SCR), (NVL(TRIM(PL_SoSoggetto.INDIRIZZO_DOM),PL_SoSoggetto.INDIRIZZO_RES))) INDIRIZZO,\n" +
                "TRIM(NVL(PL_SoSoggetto.TEL1, '') || ' ' || NVL(PL_SoSoggetto.TEL2, '')) TEL,\n" +
                "PL_SoSoggetto.CODTS,\n" + "PL_SoSoggetto.ULSS,\n" + "PL_SoSoggetto.CF,\n" +
                "PL_SoComune.DESCRIZIONE AS COMUNE,\n" + "PL_SoComune.CODPR,\n" +

                // CAP
                "DECODE(TRIM(PL_SoSoggetto.indirizzo_scr), null, " +
                "DECODE(TRIM(PL_SoSoggetto.indirizzo_dom), null, NVL(PL_SoSoggetto.cap_comres, PL_SoComune.cap), " +
                "NVL(PL_SoSoggetto.cap_comdom, PL_SoComune.cap)), " + "NVL(PL_SoSoggetto.cap_comsrc,PL_SoComune.cap)) AS CAP,\n" +

                "PL_SoCentroPrelRef.DESCRIZIONE AS CENTRO,\n" + "PL_SoCentroPrelRef.INDIRIZZO_RES,\n" +
                "PL_SoCnfTpinvito.DESCRIZIONE AS TIPO_INVITO,\n" + "PL_SoCnfEsitoinvito.CODESITOINVITO,\n" +
                "PL_SoCnfEsitoinvito.ESITOINVITO,\n" +

                // Cod campione
                (String) ViewHelper.decodeByTpscr(tpscr, "PL_SoAccCito1liv.NUMVETRINO AS COD_CAMPIONE,\n",
                                                  "A_SoAccColon1liv.COD_CAMPIONE,\n", "null AS COD_CAMPIONE,\n",
                                                  "null AS COD_CAMPIONE,\n", "DECODE(PL_SoInvito.livello, 1,A_SoAccPfas1liv.COD_CAMPIONE, 2, A_SoAccPfas2liv.COD_CAMPIONE) AS COD_CAMPIONE,\n") +

                // Cod richiesta



                (String) ViewHelper.decodeByTpscr(tpscr,
                                                  "DECODE(PL_SoInvito.livello, 1, PL_SoAccCito1liv.COD_RICHIESTA, 2, A_SoAccCito2liv.COD_RICHIESTA) AS COD_RICHIESTA",
                                                  //mauro 8-10-2008, aggiunto cod. richiesta per cito
                                                  "DECODE(PL_SoInvito.livello, 1, A_SoAccColon1liv.COD_RICHIESTA, 2, A_SoAccColon2liv.COD_RICHIESTA) AS COD_RICHIESTA",
                                                  "DECODE(PL_SoInvito.livello, 1, A_SoAccMammo1liv.COD_RICHIESTA, 2, A_SoAccMammo2liv.COD_RICHIESTA) AS COD_RICHIESTA",
                                                  "null AS COD_RICHIESTA",
                                                  "DECODE(PL_SoInvito.livello, 1, A_SoAccPfas1liv.COD_RICHIESTA, 2, A_SoAccPfas2liv.COD_RICHIESTA) AS COD_RICHIESTA") + ",\n" +

                "to_char(PL_SoSoggScr.ROUNDINDIV) ROUNDINDIV,\n" + "PL_SoSoggScr.CODIDSOGG_MX,\n" + //mauro 8-10-2008, aggiunto cod. archiviazione
                "A_SoMedico.NOME AS NOME1,\n" + "A_SoMedico.CODICEREGMEDICO,\n" + "A_SoMedico.COGNOME AS COGNOME1,\n" +
                "A_SoMedico.INDIRIZZO_RES AS INDIRIZZO_RES2,\n" + "A_SoComune3.DESCRIZIONE AS DESCRIZIONE4,\n" +
                "A_SoComune3.CODCOM AS CODCOM3,\n" + "A_SoComune3.CODPR AS CODPR3,\n" + "A_SoComune3.CAP AS CAP3,\n" +
                "PL_SoSoggScr.MX45_BRACCIO,\n" + //03052013 GAION, aggiunto per progetto studio 45 mx
                "PL_SoSoggScr.MX45_CODICE,\n" + //03052013 GAION, aggiunto per progetto studio 45 mx
                "PL_SoSoggScr.CODIDSOGG_MX,\n" + //30092013 Gaion
                "Cnf_SoCnfTrialStato.DESCR TRIAL_STATO_DESCR,\n" +
                "Cnf_SoCnfTrialBraccio.DESCR TRIAL_BRACCIO_DESCR,\n" + "PL_SoSoggetto.EMAIL,\n" + //05112014 GAION
                "PL_SoSoggetto.CELLULARE,\n" + //05112014 GAION
                "PL_SoInvito.TEST_PROPOSTO,\n" +
                //Consenso
                "PL_SoSoggScr.CONSENSO_COND,\n" + "PL_SoSoggScr.CONSENSO,\n" +
                "PL_SoInvito.CONFERMATO,\n" +
                " Print_SoCentroPrelRef1.DESCRIZIONE AS CENTRO_REF,\n" +
                // Prelievo HPV
                (String) ViewHelper.decodeByTpscr(tpscr,
                                                  "DECODE(PL_SoInvito.CODESITOINVITO || PL_SoInvito.LIVELLO || PL_SoAccCito1liv.PRELIEVO_HPV, 'S11', 'No', 'S12', 'Si', null)",
                                                  "null", "null", "null", "null") + " AS PRELIEVO_HPV,\n" +
                // conferma cellulare
                "PL_SoSoggetto.CELL_CONFERMATO AS CELL_CONFERMATO,\n" +
                "PL_SoInvito.OTP AS OTP\n" + // I00098760
                
                
                // FROM
                "FROM SO_INVITO PL_SoInvito,\n" + "SO_CNF_TPINVITO PL_SoCnfTpinvito,\n" +
                "SO_CNF_ESITOINVITO PL_SoCnfEsitoinvito,\n" + "SO_CENTRO_PREL_REF PL_SoCentroPrelRef,\n" +
                "SO_SOGGETTO PL_SoSoggetto,\n" + "SO_COMUNE PL_SoComune,\n" + "SO_SOGG_SCR PL_SoSoggScr,\n" +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_ACC_CITO1LIV PL_SoAccCito1liv,\n",
                                                  "SO_ACC_COLON1LIV A_SoAccColon1liv,\n",
                                                  "SO_ACC_MAMMO1LIV A_SoAccMammo1liv,\n", "",
                                                  "SO_ACC_PFAS1LIV A_SoAccPfas1liv,\n") +
                "SO_ACC_MAMMO2LIV A_SoAccMammo2liv,\n" + "SO_ACC_COLON2LIV A_SoAccColon2liv,\n" +
                "SO_ACC_CITO2LIV A_SoAccCito2liv,\n" + "SO_ACC_PFAS2LIV A_SoAccPfas2liv,\n" +
                "SO_MEDICO A_SoMedico,\n" + "SO_COMUNE A_SoComune3,\n" + "SO_CNF_TRIAL_STATO Cnf_SoCnfTrialStato,\n" +
                "SO_CNF_TRIAL_BRACCIO Cnf_SoCnfTrialBraccio,\n" + "SO_CENTRO_PREL_REF Print_SoCentroPrelRef1\n" +

                // WHERE
                "WHERE\n" +

                //filtro gli inviti da stampare in base a centro e periodo temporale
                "PL_SoInvito.IDCENTROPRELIEVO = " + bean.getCentro() + " AND ";

            //data unica
            if (bean.getData_al() == null || "single".equals(bean.getType()))
                query +="PL_SoInvito.DTAPP = TO_DATE('" + DateUtils.dateToString(bean.getData()) + "','" +
                            DateUtils.DATE_PATTERN + "') AND PL_SoInvito.ULSS='" + ulss + "' AND PL_SoInvito.TPSCR='" +
                            tpscr + "' AND ";

            //periodo temporale
            else
                query +="PL_SoInvito.DTAPP >= TO_DATE('" + DateUtils.dateToString(bean.getData()) + "','" +
                            DateUtils.DATE_PATTERN + "') AND PL_SoInvito.DTAPP < TO_DATE('" +
                            DateUtils.dateToString(bean.getData_al()) + "','" + DateUtils.DATE_PATTERN + "') AND " +
                            "PL_SoInvito.ULSS='" + ulss + "' AND PL_SoInvito.TPSCR='" + tpscr + "' AND ";
            
            //I00093086
            if ("CO".equals(tpscr)){
                if (bean.getDalle_ore()!=null){
                    String[] dalle = bean.getDalle_ore().split(":");
                    query += "PL_SoInvito.oraapp >= '" + dalle[0] + "' AND PL_SoInvito.minapp >= '" + dalle[1] + "' AND ";
                }
                
                if (bean.getAlle_ore()!=null){
                    String[] alle = bean.getAlle_ore().split(":");
                    query += "PL_SoInvito.oraapp <= '" + alle[0] + "' AND PL_SoInvito.minapp <= '" + alle[1] + "' AND ";
                }
                
                String codClassePop = bean.getCodClassePop();;
                if (codClassePop!=null && !"".equals(codClassePop))
                    query += "pl_sosoggscr.codclassepop='" + codClassePop + "' AND ";
            }
            
            String idtpinvito = bean.getIdtpinvito();
            if (idtpinvito!=null && !"".equals(idtpinvito))
                query += "pl_soinvito.idtpinvito='" + idtpinvito + "' AND ";
                    
            if ("single".equals(bean.getType())) {

                query += "PL_SoSoggetto.CODTS = '" + bean.getCodts() + "'\nAND ";
            }

            query +=
                "(PL_SoInvito.CODTS = PL_SoSoggetto.CODTS AND PL_SoInvito.ULSS=PL_SoSoggetto.ULSS)\n" +
                "AND DECODE(TRIM(PL_SoSoggetto.indirizzo_scr), null, " +
                "DECODE(TRIM(PL_SoSoggetto.indirizzo_dom), null, PL_SoSoggetto.RELEASE_CODE_COM_RES, PL_SoSoggetto.RELEASE_CODE_COM_DOM), PL_SoSoggetto.RELEASE_CODE_COM_SCR) = PL_SoComune.RELEASE_CODE\n" + "AND PL_SoInvito.idcentroprelievo = PL_SoCentroPrelRef.IDCENTRO\n" +

                "AND PL_SoInvito.IDTPINVITO = PL_SoCnfTpinvito.IDTPINVITO\n" +
                "AND PL_SoInvito.ULSS = PL_SoCnfTpinvito.ULSS\n" + "AND PL_SoInvito.TPSCR = PL_SoCnfTpinvito.TPSCR\n" +

                "AND (PL_SoInvito.CODESITOINVITO = PL_SoCnfEsitoinvito.CODESITOINVITO\n" +
                "AND PL_SoInvito.LIVESITO = PL_SoCnfEsitoinvito.LIVESITO\n" +
                "AND PL_SoInvito.ULSS = PL_SoCnfEsitoinvito.ULSS\n" +
                "AND PL_SoInvito.TPSCR = PL_SoCnfEsitoinvito.TPSCR)\n" +
                "AND PL_SoSoggScr.CODTS = PL_SoInvito.CODTS\n" + "AND PL_SoSoggScr.ULSS = PL_SoInvito.ULSS\n" +
                "AND PL_SoSoggScr.TPSCR = PL_SoInvito.TPSCR\n" +
                (String) ViewHelper.decodeByTpscr(tpscr, "AND (PL_SoAccCito1liv.IDINVITO (+)= PL_SoInvito.IDINVITO)",
                                                  "AND (A_SoAccColon1liv.IDINVITO (+)= PL_SoInvito.IDINVITO)",
                                                  "AND (A_SoAccMammo1liv.IDINVITO (+)= PL_SoInvito.IDINVITO)", "",
                                                  "AND (A_SoAccPfas1liv.IDINVITO (+)= PL_SoInvito.IDINVITO)") + "\n" +
                "AND A_SoAccMammo2liv.IDINVITO (+)= PL_SoInvito.IDINVITO\n" +
                "AND A_SoAccColon2liv.IDINVITO (+)= PL_SoInvito.IDINVITO\n" +
                "AND A_SoAccCito2liv.IDINVITO (+)= PL_SoInvito.IDINVITO\n" +
                "AND A_SoAccPfas2liv.IDINVITO (+)= PL_SoInvito.IDINVITO\n" +
                "AND A_SoMedico.CODICEREGMEDICO (+)= PL_SoSoggetto.CODICEREGMEDICO\n" +
                "AND A_SoMedico.ulss (+)= PL_SoSoggetto.ulss\n" +
                "AND A_SoComune3.RELEASE_CODE (+)= A_SoMedico.RELEASE_CODE_COM\n" +
                "AND PL_SoInvito.IDCENTROREF1LIV (+)  = Print_SoCentroPrelRef1.IDCENTRO\n" +
                "AND Cnf_SoCnfTrialStato.IDSTATO_TRIAL (+) = PL_SoSoggScr.IDSTATO_TRIAL\n" +
                "AND Cnf_SoCnfTrialStato.IDTRIAL (+) = PL_SoSoggScr.IDTRIAL\n" +
                "AND Cnf_SoCnfTrialStato.TPSCR (+) = PL_SoSoggScr.TPSCR\n" +
                "AND Cnf_SoCnfTrialBraccio.IDBRACCIO_TRIAL (+) = PL_SoSoggScr.IDBRACCIO_TRIAL\n" +
                "AND Cnf_SoCnfTrialBraccio.IDTRIAL (+) = PL_SoSoggScr.IDTRIAL\n" +
                "AND Cnf_SoCnfTrialBraccio.TPSCR (+) = PL_SoSoggScr.TPSCR\n";

            //impongo l'eventuale ordinamento
            if ("temporale".equals(bean.getOrdine())) {
                //ordinamento temporale
                query += " order by Pl_SoInvito.DTAPP, Pl_SoInvito.ORAAPP, Pl_SoInvito.MINAPP";
            } else if ("alfabetico".equals(bean.getOrdine())) {
                //ordinamento alfabetico
                query += " ORDER BY Pl_SoSoggetto.COGNOME, Pl_SoSoggetto.NOME";
            } else {
                //numero del campione, l'attributo dipende dal tipo di screening
                if ("CI".equals(tpscr))
                    //default per il citologico
                    query += " order by PL_SoAccCito1liv.NUMVETRINO";
                else if ("CO".equals(tpscr))
                    query += "order by A_SoAccColon1liv.COD_CAMPIONE";
            }

            vo = am.createViewObjectFromQueryStmt("appuntamenti", query);

            //evita il caching e risparmia memoria
            vo.setForwardOnly(true);

            //recupero il template
            ViewObject temp = am.findViewObject("PL_SoTemplateView1");
            Row[] result = temp.getFilteredRows("Codtempl", bean.getTemplate());
            if (result.length == 0)
                throw new Exception("Template selezionato non trovato");

            String fileName = (String) result[0].getAttribute("Nomefile");
            if (fileName.indexOf(".") >= 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

            l = new Lettera(result[0], "PL_SoTemplateView1", "Filexml", "Compiled", fileName,
                            ConfigurationConstants.FORMATO_PDF);

            l.setParametersMap(ulss, tpscr, am.findViewObject("A_SoAziendaView1"),
                               am.findViewObject("PL_SoCnfPartemplateView1"));
            Integer tipo_lettera = (Integer) result[0].getAttribute("Idtplettera");

            //elenco esiti refertabili
            String[] esiti_array =
                (String[]) ViewHelper.decodeByTpscr(tpscr, ConfigurationConstants.CODICI_ESITI_REFERTABILI,
                                                    ConfigurationConstants.CODICI_ESITI_REFERTABILI_CO,
                                                    ConfigurationConstants.CODICI_ESITI_REFERTABILI,
                                                    ConfigurationConstants.CODICI_ESITI_REFERTABILI,
                                                    ConfigurationConstants.CODICI_ESITI_REFERTABILI);

            HashMap esiti = new HashMap();
            //lo trasformo in hashmap
            for (int h = 0; h < esiti_array.length; h++)
                esiti.put(esiti_array[h], Boolean.TRUE);

            Vector array = new Vector();
            //per ogni soggetto trovato con la ricerca

            while (vo.hasNext()) {
                Row r = vo.next();

                //controllo se il soggetto non abbia gia' disdetto
                if (esiti.get(r.getAttribute("CODESITOINVITO")) == null)
                    continue;

                //trovo la tessera sanitaria
                String tesseraSanitaria =
                    SoggUtils.trovaTessera(am, (String) r.getAttribute("CODTS"), (String) r.getAttribute("ULSS"));

                LetteraBean pl = null;
                if (ConfigurationConstants.CODICE_PIANO_LAVORO.equals(tipo_lettera)) {

                    //creo un record da passare al report
                    pl =
                        new PianoLavoroBean(r.getAttribute("CENTRO") + ", " + r.getAttribute("INDIRIZZO_RES"),
                                            r.getAttribute("DTAPP") == null ? null :
                                            DateUtils.dateToString(((Date) r.getAttribute("DTAPP")).getValue()),
                                            (String) r.getAttribute("COGNOME"), (String) r.getAttribute("NOME"),
                                            (String) r.getAttribute("TIPO_INVITO"),
                                            DateUtils.dateToString(((Date) r.getAttribute("DATA_NASCITA")).getValue()),
                                            (String) r.getAttribute("INDIRIZZO"),
                                            tesseraSanitaria,
                                            //(String)r.getAttribute("CODTS"),
                                            ViewHelper.formatTime((Number) r.getAttribute("ORAAPP"),
                                                                  (Number) r.getAttribute("MINAPP")),
                                            (String) r.getAttribute("ESITOINVITO"), null,
                                            //escl def
                                            null,
                                            //escl temp
                                            r.getAttribute("COD_CAMPIONE") != null ?
                                            r.getAttribute("COD_CAMPIONE").toString() : null,
                                            (String) r.getAttribute("COMUNE"), (String) r.getAttribute("CODPR"),
                                            (String) r.getAttribute("TEL"), (String) r.getAttribute("CAP"),
                                            DateUtils.getToday(), (String) r.getAttribute("CF"),
                                            (String) r.getAttribute("ROUNDINDIV"),
                                            r.getAttribute("COD_RICHIESTA") != null ?
                                            r.getAttribute("COD_RICHIESTA").toString() : null,
                                            (String) r.getAttribute("CODIDSOGG_MX"),
                                            (String) r.getAttribute("MX45_BRACCIO"),
                                            (String) r.getAttribute("MX45_CODICE"),
                                            (String) r.getAttribute("CODIDSOGG_MX"),
                                            (String) r.getAttribute("TRIAL_STATO_DESCR"),
                                            (String) r.getAttribute("TRIAL_BRACCIO_DESCR"),
                                            (String) r.getAttribute("EMAIL"), (String) r.getAttribute("CELLULARE"),
                                            (String) r.getAttribute("COGNOME1"),
                                            (String) r.getAttribute("NOME1"),

                                            (r.getAttribute("CONSENSO") != null ? (r.getAttribute("CONSENSO")).toString() : null),
                                            (r.getAttribute("CONSENSO_COND") != null ? (r.getAttribute("CONSENSO_COND")).toString() : null),
                                            (r.getAttribute("CONFERMATO") != null ? (r.getAttribute("CONFERMATO")).toString() : null),
                                            (String) r.getAttribute("OTP"));
                    ((PianoLavoroBean) pl).setTest_proposto((String) r.getAttribute("TEST_PROPOSTO"));
                    ((PianoLavoroBean) pl).setPrelievo_hpv((String) r.getAttribute("PRELIEVO_HPV"));
                    
                    // conferma cellulare
                    ((PianoLavoroBean) pl).setCellConfermato((r.getAttribute("CELL_CONFERMATO"))!=null ? ((Number) r.getAttribute("CELL_CONFERMATO")).stringValue() : null);
                    
                } else if (ConfigurationConstants.CODICE_ETICHETTE.equals(tipo_lettera)) {
                    // mauro 11/06/2010, utilizzo nuovo costruttore per stampa dati del medico
                    // 3092013 gaion, nuovi parametri per mammografico
                    pl =
                        new LetteraEtichetta((String) r.getAttribute("COGNOME"), (String) r.getAttribute("NOME"),
                                             (String) r.getAttribute("INDIRIZZO"), (String) r.getAttribute("CAP"),
                                             (String) r.getAttribute("COMUNE"), (String) r.getAttribute("CODPR"),
                                             DateUtils.getToday(), tesseraSanitaria, //(String)r.getAttribute("CODTS"),
                                             (String) r.getAttribute("CF"),
                                             DateUtils.dateToString(((Date) r.getAttribute("DATA_NASCITA")).getValue()),
                                             (String) r.getAttribute("TEL"), (String) r.getAttribute("CENTRO"),
                                             r.getAttribute("COD_CAMPIONE") != null ?
                                             r.getAttribute("COD_CAMPIONE").toString() : null,
                                             r.getAttribute("COD_RICHIESTA") != null ?
                                             r.getAttribute("COD_RICHIESTA").toString() : null,
                                             r.getAttribute("DTAPP") == null ? null :
                                             DateUtils.dateToString(((Date) r.getAttribute("DTAPP")).getValue()),
                                             (String) r.getAttribute("CODIDSOGG_MX"), (String) r.getAttribute("NOME1"),
                                             (String) r.getAttribute("COGNOME1"),
                                             (String) r.getAttribute("INDIRIZZO_RES2"), (String) r.getAttribute("CAP3"),
                                             (String) r.getAttribute("DESCRIZIONE4"), (String) r.getAttribute("CODPR3"),
                                             (String) r.getAttribute("MX45_BRACCIO"),
                                             (String) r.getAttribute("MX45_CODICE"),
                                             (String) r.getAttribute("CODIDSOGG_MX"),
                                             (String) r.getAttribute("TRIAL_STATO_DESCR"),
                                             (String) r.getAttribute("TRIAL_BRACCIO_DESCR"),
                                             (String) r.getAttribute("EMAIL"), (String) r.getAttribute("CELLULARE"),
                                             (r.getAttribute("CONFERMATO") != null ? r.getAttribute("CONFERMATO").toString() : null),
                                             (String) r.getAttribute("TIPO_INVITO"),
                                             (String) r.getAttribute("CENTRO_REF"),
                                             (String) r.getAttribute("OTP"));
                    ((LetteraEtichetta) pl).setTest_proposto((String) r.getAttribute("TEST_PROPOSTO"));
                    ((LetteraEtichetta) pl).setPrelievo_hpv((String) r.getAttribute("PRELIEVO_HPV"));
                    
                    // conferma cellulare
                    ((LetteraEtichetta) pl).setCellConfermato((r.getAttribute("CELL_CONFERMATO"))!=null ? ((Number) r.getAttribute("CELL_CONFERMATO")).stringValue() : "0");

                    if (bean.getIdproc() != null) //codice procedura pfas 2 liv
                        ((LetteraEtichetta) pl).setId_procedura(bean.getIdproc().toString());
                    if (bean.getData_proc() != null) //data procedura pfas 2 liv
                        ((LetteraEtichetta) pl).setData_procedura(DateUtils.dateToString(bean.getData_proc()));
                } else
                    throw new Exception("Template con codice tipo lettera " + tipo_lettera + " non previsto");

                //e lo inserisco nell'array
                array.addElement(pl);
            }

            LetteraBean[] bean_array = new LetteraBean[array.size()];
            for (int h = 0; h < bean_array.length; h++)
                bean_array[h] = (LetteraBean) array.elementAt(h);

            f = l.createLetter(bean_array);

            //download del file
            FileInputStream fis;
            byte[] b;
            try {
                fis = new FileInputStream(f);

                int n;
                while ((n = fis.available()) > 0) {
                    b = new byte[n];
                    int res = fis.read(b);
                    outputStream.write(b, 0, b.length);
                    if (res == -1)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream.flush();
            outputStream.close();

        } catch (Exception ex) {

            ex.printStackTrace();
            throw new Exception("Impossibile creare il piano di lavoro: " + ex.getMessage());

        } finally {

            if (f != null)
                f.delete();
            if (vo != null) {
                vo.remove();
                vo = null;
            }
        }
    }

    public void setPianoForm(RichForm pianoForm) {
        this.pianoForm = pianoForm;
    }

    public RichForm getPianoForm() {
        if (pianoForm == null) {
            filter();
        }
        return pianoForm;
    }

    private void filter() {
        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
                
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String user = (String) session.get("user");
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        AdfFacesContext afc = AdfFacesContext.getCurrentInstance();
        Map pageFlowScope = afc.getPageFlowScope();
        
        DCIteratorBinding voIter = bindings.findIteratorBinding("PL_SoCentroPrelRefView1Iterator");
        ViewObject vo = voIter.getViewObject();
        
        PL_AppModule am =
            (PL_AppModule) BindingContext.getCurrent().findDataControl("PL_AppModuleDataControl").getDataProvider();

        //se l'utente e' legato ad un centro puo' stampare il piano di lavoro solo per i centri associati
        String in = (String) session.get("elenco_centri");
        ViewObjectFilters.filterCentri(vo, ulss, tpscr, DateUtils.getToday(), in);

        /* I00101370 - Se l'utente è autorizzato a vedere solo i dati di alcuni centri (autorizzazione SOLimiteCentri)
         * potrà richiedere la stampa di etichette e piani di lavoro solo per i centri di sua competenza. In questo caso, 
         * la lista valori va filtrata in base ai centri associati all'utente (dato configurato in so_cnf_users_centri).
         * Se l'utente non ha vincoli nell'accesso ai dati, la lista valori sarà completa. */
        Boolean limiteCentri = (Boolean)session.get("SOLimiteCentri");
        limiteCentri=limiteCentri==null?false:limiteCentri;
        
        List<Integer> centriList = new ArrayList<Integer>(); 
        
        if (limiteCentri){    
            String centriStr = "";
                        
            //Accedo ai centri autorizzati
            String query = "SELECT centri.idcentro FROM so_cnf_users_centri centri " + 
            "LEFT JOIN sec_cnf_users us ON centri.so_user_id = us.so_user_id " + 
            "WHERE centri.ulss='" + ulss + "' AND centri.tpscr='" + tpscr + 
            "' AND UPPER(us.ldap_user_id) = UPPER('" + user + "')";
            
            ViewObject voCentri = am.createViewObjectFromQueryStmt("", query);
            voCentri.setRangeSize(-1);
            voCentri.executeQuery();
            
            RowSetIterator iter = voCentri.createRowSetIterator(null);
            while (iter.hasNext()) {
                Row row = iter.next();

                Integer idCentro = row.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) row.getAttribute(0)).intValue() : null;
                if (idCentro!=null){
                    centriList.add(idCentro);
                    
                    if (centriStr!="")
                        centriStr += ", " + idCentro;
                    else 
                        centriStr += idCentro;
                }
            }
            iter.closeRowSetIterator();
            
            String where = "";
            
            if (centriList.size()==0)
                //Non ci sono centri da mostrare
                where = "1=2";
            else
                where = "TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' AND (DTCHIUSURACENTRO IS NULL OR DTCHIUSURACENTRO > TO_DATE('" + DateUtils.getToday() + 
                        "', '" + insiel.dataHandling.DateUtils.DATE_PATTERN + "')) AND IDCENTRO IN (" + centriStr + ")";
           
            vo.setWhereClause(where);
            vo.executeQuery();
        }

        Integer centro = (Integer) pageFlowScope.get("pl_centro");

        Object _d = pageFlowScope.get("pl_data");

        String data =
            _d != null ?
            _d instanceof java.util.Date ? DateUtils.dateToString(((java.util.Date) _d)) :
            _d instanceof Date ? DateUtils.dateToString(((Date) _d).dateValue()) : (String) _d : null;
        String codts = (String) pageFlowScope.get("pl_codts");
        String idproc = (String) pageFlowScope.get("pl_idproc");
        String dtproc =
            pageFlowScope.get("pl_dtproc") != null ? DateUtils.dateToString(((Date) pageFlowScope.get("pl_dtproc"))) :
            null;
        
        /* I00101370 - Se l'utente è autorizzato a vedere solo i dati di alcuni centri 
         * (autorizzazione SOLimiteCentri) presetto il centro solo se rientra tra quelli autorizzati */  
        if (centro != null && (!limiteCentri || centriList.contains(centro)))
            bean.setCentro(centro.intValue());
        else
            bean.setCentro(null);
        
        if (data != null && data.length() > 0) {
            try {
                bean.setData(DateUtils.DATE_FORMATTER.parse(data));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else
            bean.setData(null);

        if (codts != null && codts.length() > 0) {
            //String tesseraDoc = SoggUtils.trovaTessera(vo.getApplicationModule(), codts, ulss);

            bean.setCodts(codts);
        } else
            bean.setCodts(null);

        if (idproc != null && idproc.length() > 0) {
            bean.setIdproc(new Integer(idproc));
        } else
            bean.setIdproc(null);

        if (dtproc != null && dtproc.length() > 0) {
            try {
                bean.setData_proc(DateUtils.DATE_FORMATTER.parse(dtproc));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            bean.setData_proc(null);
        }
        
        /* Se stampaEtichetta = true l'apertura del pop-up è invocata automaticamente
         * dal bottone "Conferma e stampa etichetta" in creazione nuovo invito */
        if (session.get("stampaEtichetta")!=null && ((Boolean) session.get("stampaEtichetta")).booleanValue()){
            pageFlowScope.put("pl_type", "single");
            session.remove("stampaEtichetta");
        }
        
        // mauro 27/07/2010 solo etichette se ho chiesto etichetta
        String type = (String) pageFlowScope.get("pl_type");
        
        if (type != null && type.length() > 0) {
            bean.setType(type);
            session.put("typeDisabled", Boolean.TRUE);
        } else {
            session.put("typeDisabled", Boolean.FALSE);
        }
        
        //filtro tipo invito
        DCIteratorBinding voIterTi = bindings.findIteratorBinding("Sogg_TipoInvitoView1Iterator");
        ViewObject voTI = voIterTi.getViewObject();
        
        voTI.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'");
        voTI.executeQuery();
        
        /* I00102766 - per agevolare meglio la preselezione dell'etichetta, ove la pop-up viene lanciata in 
         * relazione ad un singolo soggetto (dentro l'accettazione, ad esempio) anche il filtro sul 
         * tipo di invito va precompilato con i dati dell'invito in esame */
        String idTpInvito = null;
        if (type != null && type.equals("single")) {
            idTpInvito = (String) pageFlowScope.get("pl_idtpinv");
            
            if (idTpInvito!=null && !"".equals(idTpInvito))
                bean.setIdtpinvito(idTpInvito);
        } else 
            bean.setIdtpinvito(null);
        
        /* I00102766 - Per le etichette:
         * - se il centro di refertazione associato al centro di prelievo selezionato ha configurazioni in 
         *   SO_CNF_TEMPLATE_FILTRI, vanno mostrate solo quelle etichette
         *   
         * - se il centro di refertazione associato al centro di prelievo selezionato non ha configurazione in
         *   SO_CNF_TEMPLATE_FILTRI, non va previsto alcun filtro aggiuntivo 
         *   
         * - se l'utente imposta anche un tipo di invito, le etichette vanno filtrate anche in base a tale dato. 
         *   Questo significa che per un centro o si configurano etichette per tutti i tipi di invito, oppure si 
         *   configurano tutte SENZA specificare il tipo di invito */
        String idTemplates = "";
        List<Integer> idTemplatesList = new ArrayList<Integer>(); 

        if (centro != null){
            String query = "SELECT SCTF.codtempl " +
                "FROM SO_CENTRO_PREL_REF SCPR " +
                "JOIN SO_CNF_TEMPLATE_FILTRI SCTF ON SCTF.idcentro=SCPR.idcentroref " +
                "WHERE SCPR.ulss='" + ulss +"' AND SCPR.tpscr='" + tpscr + "' AND ";
            
            if (idTpInvito!=null && !"".equals(idTpInvito) && !idTpInvito.equals("Tutti"))
                query += "(SCTF.idtpinvito IS NULL OR SCTF.idtpinvito='" + idTpInvito + "') AND ";
            
            query += "SCPR.idcentro = " + centro;
            
            ViewObject voIdTemplates = am.createViewObjectFromQueryStmt("", query);
            voIdTemplates.setRangeSize(-1);
            voIdTemplates.executeQuery();
            
            RowSetIterator iter = voIdTemplates.createRowSetIterator(null);
            while (iter.hasNext()) {
                Row row = iter.next();
    
                Integer idTemplate = row.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) row.getAttribute(0)).intValue() : null;
                if (idTemplate!=null){
                    idTemplatesList.add(idTemplate);
                    
                    if (idTemplates!="")
                        idTemplates += ", " + idTemplate;
                    else 
                        idTemplates += idTemplate;
                }
            }
            iter.closeRowSetIterator();
        }
        /* FINE I00102766 */
        
        voIter = bindings.findIteratorBinding("PL_SoTemplateView1Iterator");
        vo = voIter.getViewObject();
        
        String more = "";
        if (type != null && type.equals("single")) {
            if (idTemplatesList.size()>0)
                more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + " AND CODTEMPL IN (" + idTemplates + ")) ";
            else 
                more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + ") ";
            
        } else {

            if (idTemplatesList.size()>0) 
                more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_PIANO_LAVORO +
                       " OR (IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + " AND CODTEMPL IN (" + idTemplates + ")) ) ";
            else 
                more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_PIANO_LAVORO +
                       " OR IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + ") ";
        }
        
        ViewObjectFilters.filterTemplates(vo, ulss, tpscr, DateUtils.getToday(), more);

        if (vo.first() != null) {
            bean.setTemplate((Integer) vo.first().getAttribute("Codtempl"));
        }

    }
    
    //I00102766
    public void onChangeCentro(ValueChangeEvent valueChangeEvent) {
        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        
        Integer centro = (Integer) valueChangeEvent.getNewValue();
        String idTpInvito = bean.getIdtpinvito();
        
        this.preFilterTemplates(centro, idTpInvito);
    }
    
    //I00102766
    public void onChangeTpinvito(ValueChangeEvent valueChangeEvent) {
        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();

        String idTpInvito = (String) valueChangeEvent.getNewValue();        
        Integer centro = bean.getCentro();
        
        this.preFilterTemplates(centro, idTpInvito);
    }
    
    //I00102766
    private void preFilterTemplates(Integer centro, String idTpInvito) {
        PL_AppModule am =
            (PL_AppModule) BindingContext.getCurrent().findDataControl("PL_AppModuleDataControl").getDataProvider();
       
        DCBindingContainer bindings = (DCBindingContainer) getBindings();

        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        
        String idTemplates = "";
        List<Integer> idTemplatesList = new ArrayList<Integer>(); 

        if (centro != null){
            String query = "SELECT SCTF.codtempl " +
                "FROM SO_CENTRO_PREL_REF SCPR " +
                "JOIN SO_CNF_TEMPLATE_FILTRI SCTF ON SCTF.idcentro=SCPR.idcentroref " +
                "WHERE SCPR.ulss='" + ulss +"' AND SCPR.tpscr='" + tpscr + "' AND ";
            
            if (idTpInvito!=null && !"".equals(idTpInvito) && !idTpInvito.equals("Tutti"))
                query += "(SCTF.idtpinvito IS NULL OR SCTF.idtpinvito='" + idTpInvito + "') AND ";
            
            query += "SCPR.idcentro = " + centro;
            
            ViewObject voIdTemplates = am.createViewObjectFromQueryStmt("", query);
            voIdTemplates.setRangeSize(-1);
            voIdTemplates.executeQuery();
            
            RowSetIterator iter = voIdTemplates.createRowSetIterator(null);
            while (iter.hasNext()) {
                Row row = iter.next();
        
                Integer idTemplate = row.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) row.getAttribute(0)).intValue() : null;
                if (idTemplate!=null){
                    idTemplatesList.add(idTemplate);
                    
                    if (idTemplates!="")
                        idTemplates += ", " + idTemplate;
                    else 
                        idTemplates += idTemplate;
                }
            }
            iter.closeRowSetIterator();
        }
        
        DCIteratorBinding voIter = bindings.findIteratorBinding("PL_SoTemplateView1Iterator");
        ViewObject vo = voIter.getViewObject();
        
        String more = "";
        String type = (String) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("pl_type");
        
        /*if (idTemplatesList.size()>0) 
            more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_PIANO_LAVORO +
                   " OR (IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + " AND CODTEMPL IN (" + idTemplates + ")) ) ";
        else 
            more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_PIANO_LAVORO +
                   " OR IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + ") ";*/
        
        if (type != null && type.equals("single")) {
            if (idTemplatesList.size()>0)
                more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + " AND CODTEMPL IN (" + idTemplates + ")) ";
            else 
                more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + ") ";
            
        } else {

            if (idTemplatesList.size()>0) 
                more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_PIANO_LAVORO +
                       " OR (IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + " AND CODTEMPL IN (" + idTemplates + ")) ) ";
            else 
                more = " (IDTPLETTERA=" + ConfigurationConstants.CODICE_PIANO_LAVORO +
                       " OR IDTPLETTERA=" + ConfigurationConstants.CODICE_ETICHETTE + ") ";
        }
        
        ViewObjectFilters.filterTemplates(vo, ulss, tpscr, DateUtils.getToday(), more);

        if (vo.first() != null) {
            bean.setTemplate((Integer) vo.first().getAttribute("Codtempl"));
        }
    }
    
    public String openPopUpPdf() {
        String url;
        try {
            url = this.stampaUrl();
            Map session = ADFContext.getCurrent().getSessionScope();
            session.put("pdfToPrint",url);
            RichPopup.PopupHints hints = new RichPopup.PopupHints();
            this.getPdfPopUp().show(hints);
        } catch (Exception e) {
        }
        
        return null;
    }

    public void setPdfPopUp(RichPopup pdfPopUp) {
        this.pdfPopUp = pdfPopUp;
    }

    public RichPopup getPdfPopUp() {
        return pdfPopUp;
    }

    public void resetPdfPopup(PopupCanceledEvent popupCanceledEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String urlFile = (String)session.get("pdfToPrint");
        if (urlFile != null){
            File f = new File(urlFile);
            f.delete();
        }
    }

}
