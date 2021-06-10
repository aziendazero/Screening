package view.round;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

import javax.faces.event.ValueChangeEvent;

import model.common.Round_AppModule;

import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.Round_invitiBean;

import model.filters.ViewObjectFilters;

import model.round.common.Round_pianificaInvitiCountView;

import model.round.common.Round_pianificaInvitiListaSoggettiView;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.input.RichSelectManyListbox;

import oracle.jbo.domain.Number;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class Round_pianificaInvitiAction extends Round_DataForwardAction {
    
    private static ADFLogger logger = ADFLogger.createADFLogger(Round_pianificaInvitiAction.class);

    private RichForm frmPianificaInviti;
    private RichForm frmDettagliPianificaInviti;

    private RichSelectManyListbox selectCategoriaPop;
    private RichSelectManyListbox selectCentroPrelievo;

    private List<Integer> centroPrelievo;
    private List<String> categoriaPop;
    private String soggetti;

    public Round_pianificaInvitiAction() {
        super();
    }

    protected void findForward() {
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoCentro1livView1");
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        String elenco_centri = (String) sess.get("elenco_centri");
        ViewObjectFilters.filterCentri(vo, ulss, tpscr, null, elenco_centri, "1");

        categoriaPop = (List<String>)sess.get("roundCategoriaPop");
        //sess.remove("roundCategoriaPop");
        centroPrelievo = (List<Integer>)sess.get("roundCentroPrelievo");
        //sess.remove("roundCentroPrelievo");
        soggetti = (String)sess.get("roundScreenati");
        //sess.remove("roundScreenati");
        if (soggetti == null) {
            soggetti = "tutti";
        }

        //I00101237 Filtro le classi di popolazione per data attuale      
        vo = am.findViewObject("Sogg_SoCnfClassePopView1");
        vo.setWhereClause("TPSCR = :1 AND scadenza IS NULL OR TRUNC(scadenza) >= TRUNC(SYSDATE)");    
        vo.setWhereClauseParams(new Object[]{tpscr});
        vo.executeQuery();

        vo = am.findViewObject("Round_SoCnfTpinvitoView1");
        //vo.setWhereClause("Round_SoCnfTpinvito.TPSCR = '" + tpscr + "' AND Round_SoCnfTpinvito.ULSS = '" + ulss + "' AND Round_SoCnfTpinvito.IDCATEG = 1");
        vo.setWhereClause("Round_SoCnfTpinvito.TPSCR = :tpscr AND Round_SoCnfTpinvito.ULSS = :ulss AND Round_SoCnfTpinvito.IDCATEG = :idcateg");
        vo.setWhereClauseParams(new Object[]{tpscr, ulss, 1});
        vo.executeQuery();
    }

    @Override
    protected void setAppModule() {
        this.amName = "Round_AppModule";
    }
    
    public void setFrmPianificaInviti(RichForm frmPianificaInviti) {
        this.frmPianificaInviti = frmPianificaInviti;
    }

    public RichForm getFrmPianificaInviti() {
        if (frmPianificaInviti == null) {
            findForward();
        }
       return frmPianificaInviti;
    }


    public void setFrmDettagliPianificaInviti(RichForm frmDettagliPianificaInviti) {
        this.frmDettagliPianificaInviti = frmDettagliPianificaInviti;
    }

    public RichForm getFrmDettagliPianificaInviti() {
        if (frmDettagliPianificaInviti == null) {
            //H00292525
            //onSetInClause();
            
            Cnf_selectionBean c_bean =
                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            c_bean.setInClause(QUERY);
            c_bean.setNote(null);
        }

        return frmDettagliPianificaInviti;
    }

    public void setSelectCategoriaPop(RichSelectManyListbox selectCategoriaPop) {
        this.selectCategoriaPop = selectCategoriaPop;
    }

    public RichSelectManyListbox getSelectCategoriaPop() {
        if (selectCategoriaPop == null) {
            selectCategoriaPop = new RichSelectManyListbox();
        }
        return selectCategoriaPop;
    }

    public void setSelectCentroPrelievo(RichSelectManyListbox selectCentroPrelievo) {
        this.selectCentroPrelievo = selectCentroPrelievo;
    }

    public RichSelectManyListbox getSelectCentroPrelievo() {
        if (selectCentroPrelievo == null) {
            selectCentroPrelievo = new RichSelectManyListbox();
        }
        return selectCentroPrelievo;
    }
    
    private static final String QUERY_SELECT = "SELECT S.CODTS, S.COGNOME, S.NOME, S.DATA_NASCITA, S.SESSO," +
        " S.CF,SC.CODCLASSEPOP, I.IDINVITO, I.IDTPINVITO, I.DTAPP, I.IDCENTROPRELIEVO, I.DTAPP + P.GG_PERIODISMO RICHIAMO";
    private static final String QUERY_FROM = " FROM SO_SOGGETTO S, SO_SOGG_SCR SC, SO_CNF_ANAG_SCR A, SO_INVITO I, SO_CNF_PERIODISMO P";
    private static final String QUERY_WHERE_MAIN = //" WHERE S.CODTS=SC.CODTS AND S.ULSS=SC.ULSS AND " + <-- � IMPLICITO NELLA QUERY DEL VO
        " SC.TPSCR = :tpScr AND SC.CODCLASSEPOP IS NOT NULL AND P.ULSS = SC.ULSS AND P.TPSCR = SC.TPSCR" +
        " AND P.CODCLASSEPOP = SC.CODCLASSEPOP AND P.IDTPINVITO = :tpInvito AND I.CODTS(+) = SC.CODTS AND I.ULSS(+) = SC.ULSS" +
        " AND I.TPSCR(+) = SC.TPSCR" +
        // IL SOGGETTO NON DEVE ESSERE ESCLUSO ALLA DATA ATTUALE
        " AND S.CODTS NOT IN (" + 
        " SELECT RSE.CODTS FROM SO_ESCLUSIONE RSE, SO_CNF_ESCLUSIONE RSCE" +
        " WHERE ((RSE.IDCNFESCL = RSCE.IDCNFESCL)" +
        " AND ( RSE.ULSS = RSCE.ULSS )" +
        " AND ( RSE.TPSCR = RSCE.TPSCR )" +
        " AND ( RSE.TPSCR = :tpScr )" +
        " AND ( RSE.ULSS = :ulss )" +
        " AND DTANNULL IS NULL AND TRUNC(DTESCL) <= TRUNC(SYSDATE) AND (" +
        " TPESCL = 'D' OR (TPESCL = 'T' AND TRUNC(DTESCL + NUMGIORNI) >= TRUNC(SYSDATE))" + // VERIFICARE
        " ) ) )" +
        //IL SOGGETTO DEVE ESSERE PARTE DELLA POPOLAZIONE TARGET
        " AND ( S.CODANAGREG = A.CODANAGREG AND S.ULSS = A.ULSS AND A.INCLUSO = 1 AND A.TPSCR=SC.TPSCR )" +
        // CONSIDERO L'INVITO PI� RECENTE DELLA TIPOLOGIA INDICATA, SE NE ESISTE UNO 
        " AND I.IDTPINVITO(+) = :tpInvito" +
        " AND ( I.IDINVITO IS NULL OR (I.CODTS, I.DTAPP ) IN (" +
        " SELECT II.CODTS, MAX(II.DTAPP) DTINVITO FROM SO_INVITO II, SO_SOGG_SCR D" +
        " WHERE II.ULSS = :ulss AND II.TPSCR = :tpScr AND II.IDTPINVITO = :tpInvito" +
        " AND D.CODTS = II.CODTS AND D.ULSS = II.ULSS AND D.TPSCR = II.TPSCR AND D.CODCLASSEPOP IS NOT NULL" +
        " GROUP BY II.CODTS ) )" +
        // � TRASCORSO ALMENO IL PERIODISMO INDICATO DALL'INVITO CONSIDERATO
        " AND NVL(I.DTAPP, TO_DATE('01/01/1900','DD/MM/YYYY'))+P.GG_PERIODISMO <= TRUNC(SYSDATE)";
    // SE ALMENO UNA CATEGORIA � SELEIZONATA COME FILTRO
    private static final String QUERY_WHERE_POP_CATEG = " AND SC.CODCLASSEPOP IN (:listaCategoriaPop)";
    private static final String QUERY_WHERE_NOT_POP_CATEG = " AND SC.CODCLASSEPOP IS NOT NULL";
    // SE ALMENO UN CENTRO � SELEIZONATO COME FILTRO
    private static final String QUERY_WHERE_CENTER = " AND I.IDCENTROPRELIEVO IN (:listaCentroPrelievo)";
    // CASO SOGGETTI SCREENATI: SOSTITUIRE :maiScreenati CON "NOT", SE SI VUOLE RICERCARE I CASI MAI SCREENATI
    // OPPURE CON "" PER RICERCARE I CASI GI� SCREENATI
    private static final String QUERY_WHERE_SCREENED = " AND :maiScreenati EXISTS (SELECT * FROM SO_INVITO J" +
        " WHERE J.CODTS = SC.CODTS AND J.TPSCR = SC.TPSCR AND J.ULSS = SC.ULSS" +
        " AND J.IDTPINVITO = :tpInvito AND J.CODESITOINVITO = 'S')";
    // ORDINAMENTO PER LA STAMPA
    //private static final String QUERY_ORDER = " ORDER BY NVL(I.DTAPP, TO_DATE('01/01/1900','DD/MM/YYYY'))+P.GG_PERIODISMO";

    public void onFilterPopulation() {
        Map sess = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpScr = (String) sess.get("scr");

        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        String tpInvito = bean.getTpInvito();

        String where = QUERY_WHERE_MAIN;

        if (categoriaPop != null && !categoriaPop.isEmpty()) {
            String listaCategoriaPop = "";
            for (String c : categoriaPop) {
                listaCategoriaPop += (listaCategoriaPop.isEmpty() ? "" : ",") + "'" + c + "'";
            }
            where += QUERY_WHERE_POP_CATEG.replace(":listaCategoriaPop", listaCategoriaPop);
            sess.put("roundCategoriaPop", categoriaPop);
        }

        if (centroPrelievo != null && !centroPrelievo.isEmpty()) {
            String listaCentroPrelievo = "";
            for (Integer c : centroPrelievo) {
                listaCentroPrelievo += (listaCentroPrelievo.isEmpty() ? "" : ",") + c;
            }
            where += QUERY_WHERE_CENTER.replace(":listaCentroPrelievo", listaCentroPrelievo);
            sess.put("roundCentroPrelievo", centroPrelievo);
        }
        
        if ("screenati".equalsIgnoreCase(soggetti) || "maiScreenati".equalsIgnoreCase(soggetti)) {
            if ("screenati".equalsIgnoreCase(soggetti)) {
                where += QUERY_WHERE_SCREENED.replace(":maiScreenati", "");
            } else {
                where += QUERY_WHERE_SCREENED.replace(":maiScreenati", "NOT");
            }
            sess.put("roundScreenati", soggetti);
        }

        where = where.replace(":ulss", "'" + ulss + "'")
            .replace(":tpScr", "'" + tpScr + "'")
            .replace(":tpInvito", "'" + tpInvito + "'");
        
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();

        /*
        ViewObject vo = am.findViewObject("Round_pianificaInvitiCount");
        if (vo != null) {
            vo.remove();
        }
        String selectFrom = QUERY_SELECT + QUERY_FROM + where + QUERY_ORDER;
        vo = am.createViewObjectFromQueryStmt("Round_pianificaInvitiCount", selectFrom);
        */
        
        ViewObject vo = am.findViewObject("Round_pianificaInvitiCountView1");
        vo.setWhereClause(where);
        //Object qp[] = queryParameters.toArray(new Object[queryParameters.size()]);
        //vo.setWhereClauseParams(qp);
        vo.setRangeSize(-1);
        vo.executeQuery();

        ViewObject voConteggio = am.findViewObject("Round_SoPianificazioneInvitiView1");
        ViewHelper.clearVO(voConteggio);
        
        if (vo.hasNext()) {
            Row r = voConteggio.createRow();
            voConteggio.insertRow(r);
            r.setAttribute("Idtpinvito", tpInvito);
            ViewObject tpinviti = am.findViewObject("Round_SoCnfTpinvitoView1");
            Row[] tipi = tpinviti.getFilteredRows("Idtpinvito", tpInvito);
            if (tipi != null && tipi.length > 0) {
                r.setAttribute("TipoTest", tipi[0].getAttribute("Descrizione"));
            }
            //long c = vo.getEstimatedRowCount();
            //r.setAttribute("Conteggio", c);
            
            long conteggioSoggetti = 0;

            Round_pianificaInvitiListaSoggettiView voSoggetti = (Round_pianificaInvitiListaSoggettiView)am.findViewObject("Round_pianificaInvitiListaSoggettiView1");
            ViewHelper.clearVO(voSoggetti);
            
            ViewObject voClassePop = am.findViewObject("Sogg_SoCnfClassePopView1");
            ViewObject voCentroPrelievo = am.findViewObject("Round_SoCentro1livView1");
            
            List<String> codsTs = new ArrayList<String>();
            
            while (vo.hasNext()) {
                Row rSogg = vo.next();
                Row rNew = voSoggetti.createRow();
                voSoggetti.insertRow(rNew);
                
                String codTs = (String)rSogg.getAttribute("Codts");
                if (codsTs.contains(codTs)) {
                    continue;
                } else {
                    codsTs.add(codTs);
                    conteggioSoggetti++;
                    rNew.setAttribute("Codts", codTs);
                    rNew.setAttribute("Cognome", rSogg.getAttribute("Cognome"));
                    rNew.setAttribute("Nome", rSogg.getAttribute("Nome"));
                    rNew.setAttribute("DataNascita", rSogg.getAttribute("DataNascita"));
                    rNew.setAttribute("Sesso", rSogg.getAttribute("Sesso"));
                    rNew.setAttribute("CodiceFiscale", rSogg.getAttribute("Cf"));
                    String codClassePop = (String) rSogg.getAttribute("Codclassepop");
                    rNew.setAttribute("CodClassePop", codClassePop);
                    Row[] classi = voClassePop.getFilteredRows("Codclasse", codClassePop);
                    if (classi != null && classi.length > 0) {
                        rNew.setAttribute("DescClassePop", classi[0].getAttribute("Descrizione"));
                    }
                    //rNew.setAttribute("TipoTest", xxx);
                    rNew.setAttribute("DataApp", rSogg.getAttribute("Dtapp"));
                    Integer idCentroPrelievo = (Integer)rSogg.getAttribute("Idcentroprelievo");
                    if (idCentroPrelievo != null) {
                        Row[] centriPrelievo = voCentroPrelievo.getFilteredRows("Idcentro", idCentroPrelievo);
                        if (centriPrelievo != null && centriPrelievo.length > 0) {
                            rNew.setAttribute("CentroPrelievo", centriPrelievo[0].getAttribute("Descrbreve"));
                        }
                    }
                    rNew.setAttribute("Richiamo", rSogg.getAttribute("Richiamo"));
                }
            }

            // AGGIUNGO IL VERO CONTEGGIO DEI SOGGETTI ALLA FINE
            r.setAttribute("Conteggio", conteggioSoggetti);
            System.out.println("Eseguita query con risultati - " + conteggioSoggetti);
        }
        
    }
    
    //H00292525
    private static final String QUERY_NEW = "SELECT S.COGNOME, S.NOME, S.DATA_NASCITA, S.SESSO, S.CODTS, S.ULSS, S.CF, " +
        "I.IDINVITO, I.IDTPINVITO, I.DTAPP, I.IDCENTROPRELIEVO, (I.DTAPP + P.GG_PERIODISMO) AS Richiamo, SC.CODCLASSEPOP, " +
        "SC.CODTS AS CODTS1, SC.ULSS AS ULSS1, SC.TPSCR " + 
        
        "FROM SO_SOGG_SCR SC " + 
        "join SO_SOGGETTO S on sc.ulss=s.ulss and S.CODTS = SC.CODTS " + 
        //"join SO_CNF_ANAG_SCR A  on S.CODANAGREG = A.CODANAGREG  and S.ULSS = A.ULSS AND A.INCLUSO = 1 AND A.TPSCR=SC.TPSCR " + 
        "join SO_CNF_PERIODISMO P on P.ULSS = SC.ULSS and P.TPSCR = SC.TPSCR AND P.CODCLASSEPOP = SC.CODCLASSEPOP AND P.IDTPINVITO = :tpInvito " + 
        "left join (" + 
            "SELECT II.CODTS, MAX(II.DTAPP) DTINVITO,II.ulss,II.tpscr" + 
            " FROM SO_INVITO II" + 
            " join SO_SOGG_SCR D  on D.CODTS = II.CODTS  AND D.ULSS = II.ULSS  AND D.TPSCR = II.TPSCR AND D.CODCLASSEPOP IS NOT NULL " + 
            " join SO_CNF_PERIODISMO P on P.ULSS = D.ULSS and P.TPSCR = D.TPSCR AND P.CODCLASSEPOP = D.CODCLASSEPOP AND II.IDTPINVITO = p.IDTPINVITO" + 
            " WHERE II.ULSS = :ulss " + 
            " AND II.TPSCR = :tpScr " + 
            " AND II.IDTPINVITO = :tpInvito " + 
            " GROUP BY II.CODTS ,II.ulss,II.tpscr " + 
        ") screenati on sc.codts=screenati.codts " + 
        "left join SO_INVITO I on " +
        "I.CODTS = screenati.CODTS AND I.ULSS = screenati.ULSS AND I.TPSCR = screenati.TPSCR AND I.IDTPINVITO = :tpInvito " + 
        "AND I.DTAPP=screenati.dtinvito " + 
        
        "WHERE sc.ulss = :ulss " + 
        "AND SC.TPSCR = :tpScr " + 
        ":categoriaPop" +
        ":centroPrelievo" +
        ":screenati" +
        
        //ESCLUSIONI 
        "AND S.codanagreg != 8" +
        "AND SC.CODTS NOT IN (" +
            "SELECT RSE.CODTS " + 
            "FROM SO_ESCLUSIONE RSE, SO_CNF_ESCLUSIONE RSCE " + 
            "WHERE ((RSE.IDCNFESCL = RSCE.IDCNFESCL) " + 
            "AND (RSE.ULSS = RSCE.ULSS) " + 
            "AND (RSE.TPSCR = RSCE.TPSCR) " + 
            "AND (RSE.TPSCR = :tpScr) AND " + 
            "(RSE.ULSS = :ulss) " + 
            "AND DTANNULL IS NULL AND TRUNC(DTESCL) <= TRUNC(SYSDATE) " + 
            "AND (TPESCL = 'D' OR (TPESCL = 'T' AND TRUNC(DTESCL + NUMGIORNI) >= TRUNC(SYSDATE)))) " +
        ") " +
                                        
        //PERIODISMO
        "AND NVL(I.DTAPP, TO_DATE('01/01/1900','DD/MM/YYYY'))+P.GG_PERIODISMO <= TRUNC(SYSDATE) ";
    
    private static final String QUERY_ORDER = "ORDER BY (I.DTAPP + P.GG_PERIODISMO) ASC NULLS FIRST, S.COGNOME, S.NOME"; // <-- AGGIUNTA ALLA QUERY DEL VO
    
    private static String QUERY = "";

    public void onFilterPopulationNew() {

        try {
            checkFilters();
            
            Map sess = ADFContext.getCurrent().getSessionScope();
            String ulss = (String) sess.get("ulss");
            String tpScr = (String) sess.get("scr");

            Round_invitiBean bean =
                (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
            String tpInvito = bean.getTpInvito();

            QUERY = QUERY_NEW + QUERY_ORDER;

            String listaCategoriaPop = "";
            if (categoriaPop != null && !categoriaPop.isEmpty()) {
                for (String c : categoriaPop) {
                    listaCategoriaPop += (listaCategoriaPop.isEmpty() ? "" : ",") + "'" + c + "'";
                }
            }

            putValueInSession("roundCategoriaPop", categoriaPop);

            if ("".equals(listaCategoriaPop))
                QUERY = QUERY.replace(":categoriaPop", "AND SC.CODCLASSEPOP IS NOT NULL ");
            else
                QUERY = QUERY.replace(":categoriaPop", "AND SC.CODCLASSEPOP IN (" + listaCategoriaPop + ") ");

            String listaCentroPrelievo = "";
            if (centroPrelievo != null && !centroPrelievo.isEmpty()) {
                for (Integer c : centroPrelievo) {
                    listaCentroPrelievo += (listaCentroPrelievo.isEmpty() ? "" : ",") + c;
                }
            }

            if ("".equals(listaCentroPrelievo))
                QUERY = QUERY.replace(":centroPrelievo", "");
            else
                QUERY = QUERY.replace(":centroPrelievo", "AND I.IDCENTROPRELIEVO IN (" + listaCentroPrelievo + ") ");

            putValueInSession("roundCentroPrelievo", centroPrelievo);

            if ("screenati".equalsIgnoreCase(soggetti) || "maiScreenati".equalsIgnoreCase(soggetti)) {
                if ("screenati".equalsIgnoreCase(soggetti))
                    QUERY = QUERY.replace(":screenati", QUERY_WHERE_SCREENED.replace(":maiScreenati", ""));
                else
                    QUERY = QUERY.replace(":screenati", QUERY_WHERE_SCREENED.replace(":maiScreenati", "NOT"));

            } else
                QUERY = QUERY.replace(":screenati", "");

            QUERY =
                QUERY.replace(":ulss", "'" + ulss + "'")
                    .replace(":tpScr", "'" + tpScr + "'")
                    .replace(":tpInvito", "'" + tpInvito + "'");

            putValueInSession("roundScreenati", soggetti);

            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();

            ViewObject voNew = am.createViewObjectFromQueryStmt("", QUERY);
            voNew.setRangeSize(-1);
            voNew.executeQuery();

            ViewObject voConteggio = am.findViewObject("Round_SoPianificazioneInvitiView1");
            ViewHelper.clearVO(voConteggio);

            if (voNew.hasNext()) {
                Row r = voConteggio.createRow();
                voConteggio.insertRow(r);
                r.setAttribute("Idtpinvito", tpInvito);
                ViewObject tpinviti = am.findViewObject("Round_SoCnfTpinvitoView1");
                Row[] tipi = tpinviti.getFilteredRows("Idtpinvito", tpInvito);
                if (tipi != null && tipi.length > 0) {
                    r.setAttribute("TipoTest", tipi[0].getAttribute("Descrizione"));
                }

                long conteggioSoggetti = 0;

                Round_pianificaInvitiListaSoggettiView voSoggetti =
                    (Round_pianificaInvitiListaSoggettiView) am.findViewObject("Round_pianificaInvitiListaSoggettiView1");
                ViewHelper.clearVO(voSoggetti);

                ViewObject voClassePop = am.findViewObject("Sogg_SoCnfClassePopView1");
                ViewObject voCentroPrelievo = am.findViewObject("Round_SoCentro1livView1");

                List<String> codsTs = new ArrayList<String>();

                while (voNew.hasNext()) {
                    Row rSogg = voNew.next();
                    Row rNew = voSoggetti.createRow();
                    voSoggetti.insertRow(rNew);

                    String codTs = (String) rSogg.getAttribute("CODTS");
                    if (codsTs.contains(codTs)) {
                        continue;
                    } else {
                        codsTs.add(codTs);
                        conteggioSoggetti++;

                        rNew.setAttribute("Codts", codTs);
                        rNew.setAttribute("Cognome", rSogg.getAttribute("COGNOME"));
                        rNew.setAttribute("Nome", rSogg.getAttribute("NOME"));
                        rNew.setAttribute("DataNascita", rSogg.getAttribute("DATA_NASCITA"));
                        rNew.setAttribute("Sesso", rSogg.getAttribute("SESSO"));
                        rNew.setAttribute("CodiceFiscale", rSogg.getAttribute("CF"));

                        String codClassePop = (String) rSogg.getAttribute("CODCLASSEPOP");
                        rNew.setAttribute("CodClassePop", codClassePop);

                        Row[] classi = voClassePop.getFilteredRows("Codclasse", codClassePop);
                        if (classi != null && classi.length > 0) {
                            rNew.setAttribute("DescClassePop", classi[0].getAttribute("Descrizione"));
                        }

                        rNew.setAttribute("DataApp", rSogg.getAttribute("DTAPP"));

                        Integer idCentroPrelievo = null;
                        if (rSogg.getAttribute("IDCENTROPRELIEVO") != null)
                            idCentroPrelievo = ((Number) rSogg.getAttribute("IDCENTROPRELIEVO")).intValue();

                        if (idCentroPrelievo != null) {
                            Row[] centriPrelievo = voCentroPrelievo.getFilteredRows("Idcentro", idCentroPrelievo);
                            if (centriPrelievo != null && centriPrelievo.length > 0) {
                                rNew.setAttribute("CentroPrelievo", centriPrelievo[0].getAttribute("Descrbreve"));
                            }
                        }

                        rNew.setAttribute("Richiamo", rSogg.getAttribute("RICHIAMO"));
                    }
                }

                // AGGIUNGO IL VERO CONTEGGIO DEI SOGGETTI ALLA FINE
                r.setAttribute("Conteggio", conteggioSoggetti);
                System.out.println("Eseguita query con risultati - " + conteggioSoggetti);
            }
        } catch (Exception e) {
            handleException(e.getMessage(), null);
        }
    }

    public void setCentroPrelievo(List<Integer> centroPrelievo) {
        this.centroPrelievo = centroPrelievo;
    }

    public List<Integer> getCentroPrelievo() {
        return centroPrelievo;
    }

    public void setCategoriaPop(List<String> categoriaPop) {
        this.categoriaPop = categoriaPop;
    }

    public List<String> getCategoriaPop() {
        return categoriaPop;
    }

    public void setSoggetti(String soggetti) {
        this.soggetti = soggetti;
    }

    public String getSoggetti() {
        return soggetti;
    }
    
    public String onBack() {
        //poiche' ho ripopolato la cache, se esco dall apagina devo ripulirla
        /*Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_SoInvitoSollecitiView2");
        vo.clearCache();
        vo = am.findViewObject("Round_SoInvitoSollecitiView3");
        vo.clearCache();*/
        return "back";
    }

    public Row[] getPianificaInvitiResults(){
        Row[] rs = null;
        Round_AppModule am =
            (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Round_pianificaInvitiListaSoggettiView1");
        if (vo != null) {
            rs = vo.getAllRowsInRange();
        }
        return rs;
    }

    @Override
    protected String getElenco_voName() {
        return "Round_pianificaInvitiCountView1";
    }

    private void checkFilters() throws Exception {
        Round_invitiBean bean =
            (Round_invitiBean) BindingContext.getCurrent().findDataControl("Round_invitiBeanDataControl").getDataProvider();
        String tpInvito = bean.getTpInvito();

        //controllo validita' filtri
        if (tpInvito == null || tpInvito.trim().isEmpty() || "-1".equals(tpInvito)) {
            throw new Exception("Selezionare il TIPO TEST prima di eseguire la ricerca");
        }

    }
    
    public void categoriaPopChangeListener(ValueChangeEvent valueChangeEvent) {
        putValueInSession("roundCategoriaPop", valueChangeEvent.getNewValue());
    }

    public void centroPrelievoChangeListener(ValueChangeEvent valueChangeEvent) {
        putValueInSession("roundCentroPrelievo", valueChangeEvent.getNewValue());
    }

    public void soggettiChangeListener(ValueChangeEvent valueChangeEvent) {
        putValueInSession("roundScreenati", valueChangeEvent.getNewValue());
    }
    
    private void putValueInSession(String key, Object value) {
        Map sess = ADFContext.getCurrent().getSessionScope();
        if (value == null) {
            sess.remove(key);
        } else {
            sess.put(key, value);
        }
    }
}
