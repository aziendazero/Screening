package view.statistiche;

import insiel.dataHandling.DateUtils;
import insiel.dataHandling.ObjectTransformationUtils;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import model.common.Stats_AppModule;

import model.commons.AccessManager;

import model.commons.ConfigurationConstants;

import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.Stats_dynamicFilter;
import model.datacontrol.Stats_paramBean;

import model.datasources.Stats_datasource;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;


public class Stats_helper {
    /** COSTANTI **/
    public static final String[][] STATS_GRUPPI = {
        // FORMA AGGREGATA, FORMA ESTESA
        { "SUGG1_AGGR", "SUGG1_ESTE" }, //LIVELLO 1
        { "SUGG2_AGGR", "SUGG2_ESTE" }, //LIVELLO 2
        { "SUGG3_AGGR", "SUGG3_ESTE" } //LIVELLO 3



    };

    //PER ORA C'e' SOLO LA FORMA AGGREGATA
    public static final String[] GRUPPI_PN = { "ISTOPN_AGG", "ISTOPN_AGG" };
    public static final String[] GRUPPI_PT = { "ISTOPT_AGG", "ISTOPT_AGG" };


    /**
     * Dato un intervallo ed una ampiezza lo suddivide in sottointervali di tale
     * ampiezza (piu' gli estremi non delimitati)
     * Esempio: da=25, a=64, width=5
     * Si ottiene: (null,24],[25,29],[30,34]....[60,64],[65,null)
     * @return vettore di array costituiti da due BigDecimal che delimitano l'intervallo
     * @param width ampiezza dei sottointervalli
     * @param a fine dell'intervallo da suddividere
     * @param da primo intero dell'intervallo da suddividere
     */
    public static Vector getAgeClasses(int da, int a, int width) {
        if (da > a || width <= 0)
            return null;

        Vector v = new Vector();
        int init, end;
        init = 0;
        end = da - 1;
        boolean again = true;
        while (again) {
            BigDecimal[] classBorders = new BigDecimal[2];
            //primo intervallo
            if (init == 0) {
                classBorders[0] = null;
                classBorders[1] = new BigDecimal(end);

            }
            //ultimo intervallo
            else if (end == 250) {
                classBorders[0] = new BigDecimal(init);
                classBorders[1] = null;

                //fine del ciclo
                again = false;

            }
            //intervalli intermedi
            else {
                classBorders[0] = new BigDecimal(init);
                classBorders[1] = new BigDecimal(end);
            }


            //incremento la classe d'eta'
            init = end + 1;
            end = end + width;
            if (init >= a)
                end = 250;

            v.addElement(classBorders);


        }

        return v;
    }

    /**
     * Calcola il rapporto percentuale di una parte sul totale
     * @return
     * @param part
     * @param tot
     */
    public static double percentage(double tot, double part) {
        return x_tage(100, tot, part);

    }

    public static double x_tage(int x, double tot, double part) {
        return x_tage(x, tot, part, 1);

    }

    public static double x_tage(int x, double tot, double part, int scale) {
        if (tot == 0 || part == 0)
            return 0;
        double output = x * part / tot;
        output = new BigDecimal(output).setScale(scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        return output;

    }

    /**
     *
     * @param args
     */
    /* public static void main(String[] args)
    {
      Stats_helper stats_helper = new Stats_helper();
      Vector v=stats_helper.getAgeClasses(25,64,5);
      PrintUtils.printArray(v);
    }*/

    /**
     * Metodo che arrotonda un double al numero di cifre dcimali indicate
     * @return
     * @param decimalPlace
     * @param value
     */
    public static double round(double value, int decimalPlace) {
        double power_of_ten = 1;
        while (decimalPlace-- > 0)
            power_of_ten *= 10.0;
        return Math.round(value * power_of_ten) / power_of_ten;
    }


    public static void onCall_proc() throws Exception {

        ADFContext.getCurrent().getSessionScope().remove("stats_total_col_index");

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");
        int col = -1;
        int row = -1;


        String c = (String) ADFContext.getCurrent().getPageFlowScope().get("col_n");
        if (c != null)
            try {
                col = Integer.parseInt(c);
                row = to_fill.getCurrentRowIndex();
                if (to_fill.getCurrentRow() != null)
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          to_fill.getCurrentRow().getAttribute(1));

            } catch (NumberFormatException nex) {
            }


        //se non sto cercando un elenco, allora resetto i messaggi
        if (row < 0 && col < 0)
            ADFContext.getCurrent().getSessionScope().remove("stats_notes");


        AccessManager.checkPermission("SOStatULSS");
        //tipo di screening
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        Stats_paramBean bean =
            (Stats_paramBean) BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
        Boolean regionale = (Boolean) ADFContext.getCurrent().getSessionScope().get("regionale");

        //azienda sanitaria
        String ulss = null;
        //se si tratta di un utente regionale puo' scegliere la ulss
        if (regionale.booleanValue()) {

            if (bean.getUlss() == null || "-1".equals(bean.getUlss()))
                throw new Exception("E' necessario selezionare un'azienda sanitaria");

            ulss = bean.getUlss();
        } //altrimenti la ulss e' quella dell'utente
        else {
            ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        }

        //periodo temporale
        java.sql.Date dal = null;
           if (bean.getDal() != null) {
                Date d = bean.getDal();
                dal = new java.sql.Date(d.getTime());
            }

        java.sql.Date al = null;
            if (bean.getAl_escluso() != null) {
                Date d = bean.getAl_escluso();
                al = new java.sql.Date(d.getTime());
            }

        if (dal != null && al != null && dal.after(al))
            throw new Exception("Il periodo temporale non e' ben definito (la data di inizio non precede quella di fine)");

        //centro di prelievo
        BigDecimal centro = null;
        if (bean.getCentro_prel() != null)
            centro = new BigDecimal(bean.getCentro_prel());

        //comune (e' una stringa, non serve fare niente)

        //zona
        BigDecimal zona = null;
        if (bean.getZona() != null)
            zona = new BigDecimal(bean.getZona());

        //round individuale
        BigDecimal round_da = null;
        BigDecimal round_a = null;
        if (bean.getRound_ind() != null) {
            if ("Primi esami".equals(bean.getRound_ind())) {
                round_da = new BigDecimal(1);
                round_a = new BigDecimal(1);
            } else if ("Esami successivi".equals(bean.getRound_ind())) {
                round_da = new BigDecimal(2);
                round_a = new BigDecimal(new Integer(Integer.MAX_VALUE).doubleValue());
            }
            /*  if(!"tutti".equals(bean.getRound_ind()))
      {
        try
        { //se il parsing funziona, allora ho un unico round individuale..
          round_da=new BigDecimal(bean.getRound_ind());
          round_a=round_da;
        }
        catch(NumberFormatException ex)
        { //altrimento ho da 2 in su
          round_da=new BigDecimal(2);
          round_a=new BigDecimal(new Integer(Integer.MAX_VALUE).doubleValue());
        }
      }*/
        }
        //round organizzativo
        BigDecimal round_org = null;
        if (bean.getRound_org() != null && bean.getRound_org().length() > 0)
            round_org = new BigDecimal(bean.getRound_org());

        /*20080704 MOD: aggiunta round inviti ai filtri*/
        //round organizzativo
        BigDecimal round_inviti = null;
        if (bean.getRound_inviti() != null && bean.getRound_inviti().length() > 0)
            round_inviti = new BigDecimal(bean.getRound_inviti());
        /*20080704 FINE MOD*/

        //volontarie
        BigDecimal volontarie = null;
        if (bean.getAccesso() != null) {
            //solo gli inviti volontarie
            if ("Spontanei".equals(bean.getAccesso()))
                volontarie = new BigDecimal(1);
            //solo gli inviti non volontari
            else if ("Invitati".equals(bean.getAccesso()))
                volontarie = new BigDecimal(0);
        }

        //eta'
        BigDecimal eta_da = new BigDecimal(bean.getEta_da());
        BigDecimal eta_a = new BigDecimal(bean.getEta_a());

        //livello (e' gia' un intero, non serve fare niente)

        //tipo di esclusione
        String tpescl = null;
        if ("definitive".equals(bean.getTpescl()))
            tpescl = "D";
        else if ("temporanee".equals(bean.getTpescl()))
            tpescl = "T";


        //coorti/volume attivita'
        boolean coorti = false;
        if (bean.getStats_tab() == 1)
            coorti = true;


        if (bean.getDurata_int() <= 0) {
            if (Stats_paramBean.PERIODISMO == bean.getStats_type())
                bean.setDurata_int(6);
            else if (Stats_paramBean.TEMPI == bean.getStats_type())
                bean.setDurata_int(15);
        }

           //sesso
        String sesso = null;
        if (bean.getSesso() != null && !"-1".equals(bean.getSesso()))
            sesso = bean.getSesso();


        if ("CI".equals(tpscr))
            computeIndicator_CI(bean.getStats_type(), row, col, tpscr, ulss, bean.getDal(), bean.getAl_escluso(),
                                centro, zona, bean.getComune(), round_da, round_a, round_org, volontarie, eta_da, eta_a,
                                coorti, bean.getLivello().intValue(), bean.getLiv_successivo(),
                                ConfigurationConstants.NOME_GRUPPO_RACDIA_2L, "Diagnosi istologica peggiore", tpescl,
                                bean.getData_esclusione(), "esteso".equals(bean.getFormato()), bean.getTipo_operatore(),
                                bean.getDurata_int(), bean.getInizio_int(), bean.getFine_int(),
                                /*20080704 MOD: aggiunta round inviti ai filtri*/
                                round_inviti,
                                /*20080704 FINE MOD*/
                                /*20090623: filtro esplicito sul sesso*/
                                "F"
                                /*20090623 fine mod*/
                                );
        else if ("CO".equals(tpscr))
            computeIndicator_CO(bean.getStats_type(), row, col, tpscr, ulss, bean.getDal(), bean.getAl_escluso(),
                                centro, zona, bean.getComune(), round_da, round_a, round_org, volontarie, eta_da, eta_a,
                                coorti, bean.getLivello().intValue(), bean.getLiv_successivo(),
                                ConfigurationConstants.NOME_GRUPPO_RACDIA_2L, "Diagnosi istologica peggiore", tpescl,
                                bean.getData_esclusione(), "esteso".equals(bean.getFormato()), bean.getTipo_operatore(),
                                bean.getDurata_int(), bean.getInizio_int(), bean.getFine_int(), sesso,
                                bean.getLesione(),
                                /*20080704 MOD: aggiunta round inviti ai filtri*/
                                round_inviti
                /*20080704 FINE MOD*/
            );
        else if ("MA".equals(tpscr))
            computeIndicator_MA(bean.getStats_type(), row, col, tpscr, ulss, bean.getDal(), bean.getAl_escluso(),
                                centro, zona, bean.getComune(), round_da, round_a, round_org, volontarie, eta_da, eta_a,
                                coorti, bean.getLivello().intValue(), bean.getGiudia(),
                                ConfigurationConstants.NOME_GRUPPO_RACDIA_2L, "Diagnosi istologica peggiore", tpescl,
                                bean.getData_esclusione(), "esteso".equals(bean.getFormato()), bean.getTipo_operatore(),
                                bean.getDurata_int(), bean.getInizio_int(), bean.getFine_int(), bean.getLesione(),
                                /*20080704 MOD: aggiunta round inviti ai filtri*/
                                round_inviti,
                                /*20080704 FINE MOD*/
                                /*20090623: filtro esplicito sul sesso*/
                                "F"
                                /*20090623 fine mod*/
                                );
        else
            throw new Exception("Nessun indicatore predisposto per questo tipo di screening");

        //tipo di indicatore da calcolare:
        /* switch(bean.getStats_type())
    {
     case Stats_paramBean.INVITI_ESITI:
     {
         ComputeStats.getInvitiEsiti(row,col,ctx,tpscr,
                        ulss,
                        bean.getLivello().intValue(),
                        dal,
                        al,
                        centro,
                        zona,
                        bean.getComune(),
                        round_da,
                        round_a,
                        round_org,
                        volontarie,
                        eta_da,
                        eta_a,
                        coorti,
                        "esteso".equals(bean.getFormato()));


       break;
     }
     case Stats_paramBean.ESCLUSIONI:
     {

      ComputeStats.esclusioni(
                        row,col,ctx,tpscr,
                        ulss,
                        tpescl,
                        dal,
                        al,
                        zona,
                        bean.getComune(),
                        round_da,
                        round_a,
                        null,
                        eta_da,
                        eta_a,
                        bean.getData_esclusione());
       break;
     }
     case Stats_paramBean.SCREENATI:
     {

          ComputeStats.corteScreenati(row, col,ctx,
                           tpscr,
                           ulss,
                           dal,
                           al,
                           centro,
                           zona,
                           bean.getComune(),
                           round_org,
                           eta_da,
                           eta_a,
                           bean.getAccesso(),
                           coorti);
       break;
     }
     case Stats_paramBean.DIAGNOSI1LIV:
     {
       //forma estesa: considero tutti i singoli tipi di diagnosi (positivi e inadeguati)
       if("esteso".equals(bean.getFormato())){

              ComputeStats.diagnosi1liv(row,col,ctx,tpscr,
                        ulss,
                        dal,
                        al,
                        centro,
                        zona,
                        bean.getComune(),
                        round_da,
                        round_a,
                        round_org,
                        volontarie,
                        eta_da,
                        eta_a,
                        coorti);
       }
       else
       { //forma aggregata: considero le classi di diagnosi

               ComputeStats.catDiagnosi1liv(row,col,ctx,tpscr,
                        ulss,
                        dal,
                        al,
                        centro,
                        zona,
                        bean.getComune(),
                        round_da,
                        round_a,
                        round_org,
                        volontarie,
                        eta_da,
                        eta_a,
                        coorti);

       }
       break;
     }
     case Stats_paramBean.ESITI_COLPO:
     {

        ComputeStats.esitiColpo(row,col,ctx,
                      tpscr,
                      ulss,dal,
                        al,
                        centro,
                        zona,
                        bean.getComune(),
                        round_da,
                        round_a,
                        round_org,
                        volontarie,
                        eta_da,
                        eta_a,
                        coorti);

        break;
     }
     case Stats_paramBean.DIAGNOSI_PEGGIORE:
     {

        ComputeStats.cnfref2liv(row,col,
                       ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,
                        "Diagnosi istologica peggiore",
                        ctx,tpscr,
                      ulss,dal,
                        al,
                        centro,
                        zona,
                        bean.getComune(),
                        round_da,
                        round_a,
                        round_org,
                        volontarie,
                        eta_da,
                        eta_a,
                        coorti);
        break;
     }
     case Stats_paramBean.RAC2LIV:
     {

        ComputeStats.suggerimenti(row,col,ctx,
                                  tpscr,
                                  ulss,
                                  bean.getLivello().intValue(),
                                  dal,
                                  al,
                                  centro,
                                  zona,
                                  bean.getComune(),
                                  round_da,
                                  round_a,
                                  round_org,
                                  volontarie,
                                  eta_da,
                                  eta_a,
                                  "esteso".equals(bean.getFormato()),
                                  coorti);
        break;
     }
     case Stats_paramBean.OPMEDICI:
     {

        ComputeStats.operatoriMedici(row,col,ctx,
                            bean.getTipo_operatore(),
                             tpscr,
                      ulss,dal,
                        al,
                        centro,
                        zona,
                        bean.getComune(),
                        round_da,
                        round_a,
                        null,
                        volontarie,
                        eta_da,
                        eta_a,
                        coorti);
        break;
     }
     case Stats_paramBean.TEMPI:
     {

           ComputeStats.tempi(row,col,ctx,
                                  tpscr,
                                  ulss,
                                  dal,
                                  al,
                                  centro,
                                  zona,
                                  bean.getComune(),
                                  round_da,
                                  round_a,
                                  round_org,
                                  volontarie,
                                  eta_da,
                                  eta_a,
                                  coorti,
                                  bean.getDurata_int(),
                                  bean.getInizio_int(),
                                  bean.getFine_int());
          break;
     }
     case Stats_paramBean.PERIODISMO:
     {
        ComputeStats.periodismo(row,col,ctx,
                                  tpscr,
                                  ulss,
                                  dal,
                                  al,
                                  centro,
                                  zona,
                                  bean.getComune(),
                                  round_da,
                                  round_a,
                                  round_org,
                                  volontarie,
                                  eta_da,
                                  eta_a,
                                  bean.getDurata_int(),
                                  bean.getInizio_int(),
                                  bean.getFine_int());

        break;
     }
     case Stats_paramBean.INDICAZIONI2LIV:
     {
       ComputeStats.indicazioni(row,col,ctx,
                                  tpscr,
                                  ulss,
                                  dal,
                                  al,
                                  centro,
                                  zona,
                                  bean.getComune(),
                                  round_da,
                                  round_a,
                                  round_org,
                                  volontarie,
                                  eta_da,
                                  eta_a,
                                 // coorti,
                                  bean.getLivello().intValue(),
                                  bean.getLiv_successivo());
     }
     default:
       break;
    }
       */

        //   Stats_AppModule am=(Stats_AppModule)BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        //   ViewObject to_fill=am.findViewObject("Stats_InvitiEsitiMatrix");
        Map sess = ADFContext.getCurrent().getSessionScope();
        Stats_datasource ds =
            initializePrintBean(bean, tpscr, ulss, centro, zona, to_fill,
                                (String[]) sess.get("stats_inviti_esiti_headers"),
                                (boolean[]) sess.get("stats_inviti_esiti_render"),
                                (int[]) sess.get("stats_inviti_esiti_totali"));
        Vector v = new Vector();
        v.addElement(ds);
        am.storePrintBean(v);
        Cnf_selectionBean c_bean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        String note = createNote(ds);
        note += (String) sess.get("rowLabel") + "\n" + (String) sess.get("colLabel");
        c_bean.setNote(note);


        /*  if(row>=0 && col>=0)
         ctx.setActionForward("goElenco");*/


    }

    /**
     * Metodo che crea le note da inserire nell'elenco soggetti in modo da
     * dare informazioni sui filtri utilizzati
     */
    private static String createNote(Stats_datasource st) {
        String s = "Statistiche: " + st.getIndicatore() + " (" + st.getCoorte() + ")\n";
        //livello
        /* if(st.getLivello()!=null)
       s+="Livello: "+st.getLivello()+"\n";
      //formato
      if(st.getFormato()!=null && st.getFormato().length()>0)
        s+="Formato: "+st.getFormato()+"\n";
           //tipo esclusioni
       if(st.getTipo_escl()!=null && st.getTipo_escl().length()>0)
        s+="Esclusioni: "+st.getTipo_escl()+"\n";*/
        //tipo operatore
        if (st.getTipo_operatore() != null && st.getTipo_operatore().length() > 0)
            s += "Tipo operatore: " + st.getTipo_operatore() + "\n";


        //periodo
        if ((st.getPeriodo_dal() != null ) || (st.getPeriodo_al() != null)){
            s +=
                "Periodo: " + (st.getPeriodo_dal() == null ? "" : "dal " + DateUtils.dateToString(DateUtils.DATE_PATTERN,st.getPeriodo_dal())) +
                (st.getPeriodo_al() == null ? "" : " fino al " + DateUtils.dateToString(DateUtils.DATE_PATTERN,st.getPeriodo_al()) + " escluso ") + "\n";
        }
        //centro
        if (st.getCod_centro() != null)
            s += "Centro: " + st.getNome_centro() + "\n";
        //comune
        if (st.getCod_comune() != null && st.getCod_comune().length() > 0)
            s += "Comune: " + st.getNome_comune() + "\n";
        //zona
        if (st.getCod_distretto() != null)
            s += "Zona: " + st.getNome_distretto() + "\n";

        //range d'eta'
        if (st.getEta_da() != null || st.getEta_a() != null)
            s +=
                "Eta': " + (st.getEta_da() == null ? "" : " da " + st.getEta_da() + " anni ") +
                (st.getEta_a() == null ? "" : " fino a " + st.getEta_a() + " anni ") + "\n";
        //sesso
        if (st.getSesso() != null)
            s += "Sesso: " + st.getSesso() + "\n";
        //volontari o invitati
        if (st.getAccesso() != null && st.getAccesso().length() > 0)
            s += "Accesso: " + st.getAccesso() + "\n";
        //round individuale
        if (st.getRound_indiv() != null && st.getRound_indiv().length() > 0)
            s += "Round individuale: " + st.getRound_indiv() + "\n";
        //round comune
        if (st.getRound_org() != null && st.getRound_org().length() > 0)
            s += "Round organizzativo: " + st.getRound_org() + "\n";

        /*20080704 MOD: aggiunta round inviti ai filtri*/
        //round inviti
        if (st.getRound_inviti() != null && st.getRound_inviti().length() > 0)
            s += "Round inviti: " + st.getRound_inviti() + "\n";
        /*20080704 FINE MOD*/


        return s;

    }


    private static Stats_datasource initializePrintBean(Stats_paramBean params, String tpscr, String ulss,
                                                        BigDecimal centro, BigDecimal zona, ViewObject vo,
                                                        String[] headers, boolean[] render, int[] totali) {
        Stats_datasource bean = new Stats_datasource();
        bean.setCoorte(params.getStats_tab() == 0 ? "VOLUME ATTIVITA'" : "COORTE " + params.getCoorte());
        bean.setAccesso(params.getAccesso());
        bean.setCod_centro(centro);
        bean.setCod_comune(params.getComune());
        bean.setCod_distretto(zona);
        bean.setEta_a(params.getEta_a());
        bean.setEta_da(params.getEta_da());
        bean.setNome_centro(params.getNome_centro());
        bean.setNome_comune(params.getNome_comune());
        bean.setNome_distretto(params.getNome_zona());
        bean.setPeriodo_al(params.getAl_escluso());
        bean.setPeriodo_dal(params.getDal());
        bean.setRound_indiv(params.getRound_ind());
        bean.setRound_org(params.getRound_org());
        /*20080704 MOD: aggiunta round inviti ai filtri*/
        bean.setRound_inviti(params.getRound_inviti());
        /*20080704 FINE MOD*/
        bean.setTpscr(tpscr);
        bean.setUlss(ulss);
        bean.setSesso(params.getSesso() == null ? null :
                      params.getSesso().equals("M") ? "Maschi" : params.getSesso().equals("F") ? "Femmine" : null);

        String indicatore = null;
        switch (params.getStats_type()) {
        case Stats_paramBean.DIAGNOSI1LIV:
            {
                indicatore = "Diagnosi test di 1° livello";
                //imposto anche il formato
                bean.setFormato(params.getFormato());
                break;
            }
        case Stats_paramBean.DIAGNOSI_PEGGIORE:
            {
                indicatore =
                    (String) ViewHelper.decodeByTpscr(tpscr, "Diagnosi istologica peggiore",
                                                      "Diagnosi istologica peggiore", "Diagnosi patologica peggiore",
                                                      null, null);
                break;
            }
        case Stats_paramBean.CONCLUSIONI2L:
            {
                indicatore = "Conclusioni di 2° livello";
                break;
            }
        case Stats_paramBean.ESCLUSIONI:
            {
                indicatore = "Esclusioni";
                //annullo il tipo di accesso
                bean.setAccesso(null);
                //annullo il centro
                bean.setCod_centro(null);
                bean.setNome_centro(null);
                //imposto il tipo di esclusione
                bean.setTipo_escl(params.getTpescl());
                /*20080704 MOD: aggiunta round inviti ai filtri*/
                //annullo i round inviti
                bean.setRound_inviti(null);
                /*20080704 FINE MOD*/
                break;
            }
        case Stats_paramBean.ESITI_COLPO:
            {
                indicatore = "Diagnosi colposcopiche";
                break;
            }
        case Stats_paramBean.INVITI_ESITI:
            {
                indicatore = "Inviti ed esiti";
                //imposto anche il formato
                bean.setFormato(params.getFormato());
                //imposto il livello
                bean.setLivello(params.getLivello());
                break;
            }
        case Stats_paramBean.OPMEDICI:
            {
                indicatore = "Statistiche per operatore";
                /*20080708 MOD: aggiunta round inviti ai filtri*/
                //annullo i round inviti
                //  bean.setRound_inviti(null);
                /*20080708 FINE MOD*/
                //imposto anche il tipo di operatore
                switch (params.getTipo_operatore()) {
                case 1:
                    {
                        bean.setTipo_operatore((String) ViewHelper.decodeByTpscr(tpscr, "Prelevatore", "Endoscopista",
                                                                                 null, null, null));
                        break;
                    }
                case 2:
                    {
                        bean.setTipo_operatore("Citoscreener");
                        break;
                    }
                case 3:
                    {
                        bean.setTipo_operatore("Supervisore");
                        break;
                    }
                default:
                    {
                        break;
                    }


                }

                break;
            }
        case Stats_paramBean.SCREENATI:
            {
                indicatore = params.getStats_type() == 1 ? "Coorte screenati" : "Soggetti esaminati";
                //annullo l'accesso
                bean.setAccesso(null);
                //annullo il round individuale
                bean.setRound_indiv(null);
                break;
            }
        case Stats_paramBean.TEMPI:
            {
                indicatore = "Tempi di risposta";
                break;
            }
        case Stats_paramBean.RAC2LIV:
            {
                indicatore = "Raccomandazioni di " + params.getLivello() + " livello";
                //imposto anche il formato
                bean.setFormato(params.getFormato());
                //imposto il livello
                bean.setLivello(params.getLivello());
                break;
            }
        case Stats_paramBean.PERIODISMO:
            {
                indicatore = "Periodismo inviti";
                break;
            }
        case Stats_paramBean.INDICAZIONI2LIV:
            {
                indicatore = "Indicazioni e conseguenze";
                break;
            }
        case Stats_paramBean.APPROFONDIMENTO_CO:
            {
                indicatore = "Dati approfondimenti";
                break;
            }
        case Stats_paramBean.DETECTION_RATE:
            {
                indicatore = "Detection rate";
                break;
            }
        case Stats_paramBean.CHIRURGIE_CO:
            {
                indicatore = "Trattamenti chirurgici per ";
                if ("carcinoma".equals(params.getLesione()))
                    indicatore += "carcinoma";
                else if ("adenoma alto rischio".equals(params.getLesione()))
                    indicatore += "adenoma alto rischio";
                else if ("adenoma basso rischio".equals(params.getLesione()))
                    indicatore += "adenoma basso rischio";
                else
                    indicatore += "tutte le lesioni";
                break;
            }
        case Stats_paramBean.PN_PT:
            {
                indicatore = "Dimensioni e linfonodi dei carcinomi";
                break;
            }

        }
        bean.setIndicatore(indicatore);

        //dati calcolati
        RowSetIterator iter = ViewHelper.getRowSetIterator(vo, "stats_iter");
        //creo la sorgente dati per il report
        Row[] matrix = new Row[iter.getRowCount()];
        int i = 0;
        while (iter.hasNext()) {
            matrix[i] = iter.next();
            i++;
        }
        iter.closeRowSetIterator();

        bean.setRisultati(matrix);

        //headers
        bean.setHeaders(ObjectTransformationUtils.objectArrayToMap(headers, true));

        //max columns used
        bean.setMax_column_used(headers.length);

        //render
        Boolean[] rendersW = new Boolean[render.length];
        for (i = 0; i < render.length; i++) {
            rendersW[i] = ObjectTransformationUtils.getWrapper(render[i]);
        }
        bean.setRender(ObjectTransformationUtils.objectArrayToMap(rendersW, true));

        //computeTotals
        if (totali == null)
            bean.setComputeTotals(Boolean.FALSE);
        else
            bean.setComputeTotals(Boolean.TRUE);


        return bean;
    }


    private static void computeIndicator_CI(int indicator, int row, int col, String tpscr,
                                            String ulss, Date dal, Date al, BigDecimal centro, BigDecimal zona,
                                            String comune, BigDecimal round_da, BigDecimal round_a,
                                            BigDecimal round_org, BigDecimal volontari, BigDecimal eta_da,
                                            BigDecimal eta_a, boolean coorti, int livello, int tipo_referto,
                                            String nome_gruppo, String header_desc, String tpescl, Date data_escl,
                                            boolean esteso, int tipoop, int durata_int, int inizio_int, int fine_int,
                                            /*20080704 MOD: aggiunta round inviti ai filtri*/
                                            BigDecimal round_inviti,
                                            /*20080704 FINE MOD*/
                                            /*20090623: filtro esplicito sul sesso*/
                                            String sesso
                                            /*20090623 fine mod*/
                                            ) throws Exception {
        //tipo di indicatore da calcolare:
        switch (indicator) {
        case Stats_paramBean.INVITI_ESITI:
            {
                ComputeStats.getInvitiEsiti(row, col, tpscr, ulss, livello, dal,al, centro, zona, comune,
                                            round_da, round_a, round_org, volontari, eta_da, eta_a, coorti, esteso,
                                            sesso, //sesso (uso il null)
                                            /*20080704 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080704 FINE MOD*/
                                            );


                break;
            }
        case Stats_paramBean.ESCLUSIONI:
            {

                ComputeStats.esclusioni(row, col, tpscr, ulss, tpescl,dal, al, zona, comune, round_da, round_a,
                                        null, eta_da, eta_a, data_escl, sesso); //sesso
                break;
            }
        case Stats_paramBean.SCREENATI:
            {

                ComputeStats.corteScreenati(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_org,
                                            eta_da, eta_a, volontari, coorti, sesso, //sesso
                                            /*20080704 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080704 FINE MOD*/
                                            );
                break;
            }
        case Stats_paramBean.DIAGNOSI1LIV:
            {
                //forma estesa: considero tutti i singoli tipi di diagnosi (positivi e inadeguati)
                if (esteso) {

                    ComputeStats.diagnosi1liv(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                              round_a, round_org, volontari, eta_da, eta_a, coorti,
                                              /*20080707 MOD: aggiunta round inviti ai filtri*/
                                              round_inviti
                                              /*20080707 FINE MOD*/
                                              );
                } else { //forma aggregata: considero le classi di diagnosi

                    ComputeStats.catDiagnosi1liv(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                                 round_a, round_org, volontari, eta_da, eta_a, coorti,
                                                 /*20080707 MOD: aggiunta round inviti ai filtri*/
                                                 round_inviti
                                                 /*20080707 FINE MOD*/
                                                 );

                }
                break;
            }
        case Stats_paramBean.ESITI_COLPO:
            {

                ComputeStats.esitiColpo(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                        round_org, volontari, eta_da, eta_a, coorti,
                                        /*20080707 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti
                                        /*20080707 FINE MOD*/
                                        );

                break;
            }
        case Stats_paramBean.DIAGNOSI_PEGGIORE:
            {

                ComputeStats.cnfref2liv(row, col, ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,
                                        "Diagnosi istologica peggiore", tpscr, ulss,dal, al, centro, zona, comune,
                                        round_da, round_a, round_org, volontari, eta_da, eta_a, coorti, sesso, //sesso
                                        /*20080707 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti
                                        /*20080707 FINE MOD*/
                                        );
                break;
            }
        case Stats_paramBean.RAC2LIV:
            {

                ComputeStats.suggerimenti(row, col, tpscr, ulss, livello,dal, al, centro, zona, comune, round_da,
                                          round_a, round_org, volontari, eta_da, eta_a, esteso, coorti, sesso, //sesso
                                          /*20080707 MOD: aggiunta round inviti ai filtri*/
                                          round_inviti
                                          /*20080707 FINE MOD*/
                                          );
                break;
            }
        case Stats_paramBean.OPMEDICI:
            {

                ComputeStats.operatoriMedici(row, col, tipoop, tpscr, ulss,dal, al, centro, zona, comune,
                                             round_da, round_a, null, volontari, eta_da, eta_a, coorti,
                                             /*20080709 MOD: aggiunta round inviti ai filtri*/
                                             round_inviti
                                             /*20080709 FINE MOD*/
                                             );
                break;
            }
        case Stats_paramBean.TEMPI:
            {

                ComputeStats.tempi(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                   round_org, volontari, eta_da, eta_a, coorti, durata_int, inizio_int, fine_int,
                                   /*20080707 MOD: aggiunta round inviti ai filtri*/
                                   round_inviti
                                   /*20080707 FINE MOD*/
                                   );
                break;
            }
        case Stats_paramBean.PERIODISMO:
            {
                ComputeStats.periodismo(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                        round_org, volontari, eta_da, eta_a, durata_int, inizio_int, fine_int, sesso, //sesso,
                                        /*20080707 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti
                                        /*20080707 FINE MOD*/
                                        );

                break;
            }
        case Stats_paramBean.INDICAZIONI2LIV:
            {
                ComputeStats.indicazioni(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                         round_org, volontari, eta_da, eta_a, coorti, livello, tipo_referto,
                                         /*20080708 MOD: aggiunta round inviti ai filtri*/
                                         round_inviti
                                         /*20080708 FINE MOD*/
                                         );
                break;
            }
        default:
            {
                throw new Exception("Indicatore sconosciuto per questo tipo di screening");
                //  break;
            }
        }
    }


    private static void computeIndicator_CO(int indicator, int row, int col, String tpscr,
                                            String ulss, Date dal, Date al, BigDecimal centro, BigDecimal zona,
                                            String comune, BigDecimal round_da, BigDecimal round_a,
                                            BigDecimal round_org, BigDecimal volontari, BigDecimal eta_da,
                                            BigDecimal eta_a, boolean coorti, int livello, int tipo_referto,
                                            String nome_gruppo, String header_desc, String tpescl, Date data_escl,
                                            boolean esteso, int tipoop, int durata_int, int inizio_int, int fine_int,
                                            String sesso, String lesione,
                                            /*20080704 MOD: aggiunta round inviti ai filtri*/
                                            BigDecimal round_inviti
                                            /*20080704 FINE MOD*/
                                            ) throws Exception {
        //tipo di indicatore da calcolare:
        switch (indicator) {
        case Stats_paramBean.INVITI_ESITI:
            {
                ComputeStats.getInvitiEsiti(row, col, tpscr, ulss, livello,dal, al, centro, zona, comune,
                                            round_da, round_a, round_org, volontari, eta_da, eta_a, coorti, esteso,
                                            sesso,
                                            /*20080704 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                    /*20080704 FINE MOD*/
                );


                break;
            }
        case Stats_paramBean.SCREENATI:
            {
                ComputeStats.corteScreenati(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_org,
                                            eta_da, eta_a, volontari, coorti, sesso,
                                            /*20080707 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080707 FINE MOD*/
                                            );
                break;
            }
        case Stats_paramBean.ESCLUSIONI:
            {

                ComputeStats.esclusioni(row, col, tpscr, ulss, tpescl,dal, al, zona, comune, round_da, round_a,
                                        null, eta_da, eta_a, data_escl, sesso);
                break;
            }
        case Stats_paramBean.TEMPI:
            {

                ComputeStats.tempi(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                   round_org, volontari, eta_da, eta_a, coorti, durata_int, inizio_int, fine_int,
                                   /*20080707 MOD: aggiunta round inviti ai filtri*/
                                   round_inviti
                                   /*20080707 FINE MOD*/
                                   );
                break;
            }
        case Stats_paramBean.DIAGNOSI_PEGGIORE:
            {

                ComputeStats.cnfref2liv(row, col, ConfigurationConstants.NOME_GRUPPO_DIAGNOSI,
                                        "Diagnosi istologica peggiore", tpscr, ulss,dal, al, centro, zona, comune,
                                        round_da, round_a, round_org, volontari, eta_da, eta_a, coorti, sesso, //sesso
                                        /*20080707 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti
                                        /*20080707 FINE MOD*/
                                        );
                break;
            }
        case Stats_paramBean.CONCLUSIONI2L:
            {

                ComputeStats.cnfref2liv(row, col, ConfigurationConstants.NOME_GRUPPO_CONCL_CO, "Conclusioni di 2° liv.",
                                        tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a, round_org,
                                        volontari, eta_da, eta_a, coorti, sesso, //sesso
                                        /*20080707 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti
                                        /*20080707 FINE MOD*/
                                        );
                break;
            }
        case Stats_paramBean.OPMEDICI:
            {

                ComputeStats.operatoriEndoscopie(row, col, tipoop, tpscr, ulss,dal, al, centro, zona, comune,
                                                 round_da, round_a, null, volontari, eta_da, eta_a, coorti, sesso,
                                                 /*20080709 MOD: aggiunta round inviti ai filtri*/
                                                 round_inviti
                                                 /*20080709 FINE MOD*/
                                                 );
                break;
            }
        case Stats_paramBean.INDICAZIONI2LIV:
            {
                ComputeStats.indicazioniCO(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                           round_org, volontari, eta_da, eta_a, coorti, livello, sesso,
                                           /*20080708 MOD: aggiunta round inviti ai filtri*/
                                           round_inviti
                                           /*20080708 FINE MOD*/
                                           );
                break;
            }
        case Stats_paramBean.PERIODISMO:
            {
                ComputeStats.periodismo(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                        round_org, volontari, eta_da, eta_a, durata_int, inizio_int, fine_int, sesso, //sesso
                                        /*20080708 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti
                                        /*20080708 FINE MOD*/
                                        );

                break;
            }
        case Stats_paramBean.RAC2LIV:
            {

                ComputeStats.suggerimenti(row, col, tpscr, ulss, livello,dal, al, centro, zona, comune, round_da,
                                          round_a, round_org, volontari, eta_da, eta_a, esteso, coorti, sesso,
                                          /*20080707 MOD: aggiunta round inviti ai filtri*/
                                          round_inviti
                                          /*20080707 FINE MOD*/
                                          );
                break;
            }
        case Stats_paramBean.APPROFONDIMENTO_CO:
            {
                ComputeStats.dati_app_colon(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                            round_a, round_org, volontari, eta_da, eta_a, coorti, sesso,
                                            /*20080708 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080708 FINE MOD*/
                                            );
                break;
            }
        case Stats_paramBean.DETECTION_RATE:
            {
                ComputeStats.detection_rate_CO(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                               round_a, round_org, volontari, eta_da, eta_a, coorti, sesso,
                                               /*20080708 MOD: aggiunta round inviti ai filtri*/
                                               round_inviti
                                               /*20080708 FINE MOD*/
                                               );
                break;
            }
        case Stats_paramBean.CHIRURGIE_CO:
            {
                ComputeStats.trattamenti_chirurgici(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                                    round_a, round_org, volontari, eta_da, eta_a, coorti, sesso,
                                                    lesione,
                                                    /*20080709 MOD: aggiunta round inviti ai filtri*/
                                                    round_inviti
                    /*20080709 FINE MOD*/
                );
                break;
            }
        case Stats_paramBean.PN_PT:
            {
                ComputeStats.dimensioni_linfonodi(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                                  round_a, round_org, volontari, eta_da, eta_a, coorti, sesso, lesione,
                                                  /*20080708 MOD: aggiunta round inviti ai filtri*/
                                                  round_inviti
                                                  /*20080708 FINE MOD*/
                                                  );
                break;
            }

        default:
            {
                throw new Exception("Indicatore sconosciuto per questo tipo di screening");
                //  break;
            }
        }
    }


    private static void computeIndicator_MA(int indicator, int row, int col, String tpscr,
                                            String ulss, Date dal, Date al, BigDecimal centro, BigDecimal zona,
                                            String comune, BigDecimal round_da, BigDecimal round_a,
                                            BigDecimal round_org, BigDecimal volontari, BigDecimal eta_da,
                                            BigDecimal eta_a, boolean coorti, int livello, Integer tipo_referto,
                                            String nome_gruppo, String header_desc, String tpescl, Date data_escl,
                                            boolean esteso, int tipoop, int durata_int, int inizio_int, int fine_int,
                                            String lesione,
                                            /*20080704 MOD: aggiunta round inviti ai filtri*/
                                            BigDecimal round_inviti,
        /*20080704 FINE MOD*/
        /*20090623: filtro esplicito sul sesso*/
        String sesso
        /*20090623 fine mod*/
    ) throws Exception {
        //tipo di indicatore da calcolare:
        switch (indicator) {
        case Stats_paramBean.INVITI_ESITI:
            {
                ComputeStats.getInvitiEsiti(row, col, tpscr, ulss, livello,dal, al, centro, zona, comune,
                                            round_da, round_a, round_org, volontari, eta_da, eta_a, coorti, esteso,
                                            sesso, //sesso (uso il null)
                                            /*20080704 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080704 FINE MOD*/
                                            );


                break;
            }
        case Stats_paramBean.ESCLUSIONI:
            {

                ComputeStats.esclusioni(row, col, tpscr, ulss, tpescl,dal, al, zona, comune, round_da, round_a,
                                        null, eta_da, eta_a, data_escl, sesso); //sesso
                break;
            }
        case Stats_paramBean.SCREENATI:
            {

                ComputeStats.corteScreenati(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_org,
                                            eta_da, eta_a, volontari, coorti, sesso, //sesso
                                            /*20080707 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080707 FINE MOD*/
                                            );
                break;
            }
        case Stats_paramBean.DIAGNOSI1LIV:
            {


                ComputeStats.diagnosi1livMA(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                            round_a, round_org, volontari, eta_da, eta_a, coorti,
                                            /*20080707 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080707 FINE MOD*/
                                            );

                break;
            }
        case Stats_paramBean.DETECTION_RATE:
            {
                ComputeStats.detection_rate_MA(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                               round_a, round_org, volontari, eta_da, eta_a,
                                               /*20080708 MOD: aggiunta round inviti ai filtri*/
                                               round_inviti
                                               /*20080708 FINE MOD*/
                                               );
                break;
            }
        case Stats_paramBean.DIAGNOSI_PEGGIORE:
            {

                ComputeStats.cnfref2liv(row, col, ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,
                                        "Diagnosi patologica peggiore", tpscr, ulss,dal, al, centro, zona, comune,
                                        round_da, round_a, round_org, volontari, eta_da, eta_a, coorti, sesso, //sesso
                                        /*20080707 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti
                                        /*20080707 FINE MOD*/
                                        );
                break;
            }
        case Stats_paramBean.RAC2LIV:
            {

                ComputeStats.suggerimenti(row, col, tpscr, ulss, livello,dal, al, centro, zona, comune, round_da,
                                          round_a, round_org, volontari, eta_da, eta_a, esteso, coorti, sesso, //sesso
                                          /*20080707 MOD: aggiunta round inviti ai filtri*/
                                          round_inviti
                                          /*20080707 FINE MOD*/
                                          );
                break;
            }
        case Stats_paramBean.OPMEDICI:
            {

                ComputeStats.operatoriMammo(row, col, tipoop, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                            round_a, null, volontari, eta_da, eta_a, coorti,
                                            /*20080709 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080709 FINE MOD*/
                                            );
                break;
            }
        case Stats_paramBean.TEMPI:
            {

                ComputeStats.tempi(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                   round_org, volontari, eta_da, eta_a, coorti, durata_int, inizio_int, fine_int,
                                   /*20080707 MOD: aggiunta round inviti ai filtri*/
                                   round_inviti
                                   /*20080707 FINE MOD*/
                                   );
                break;
            }
        case Stats_paramBean.PERIODISMO:
            {
                ComputeStats.periodismo(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                        round_org, volontari, eta_da, eta_a, durata_int, inizio_int, fine_int, sesso, //sesso
                                        /*20080708 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti
                                        /*20080708 FINE MOD*/
                                        );

                break;
            }
        case Stats_paramBean.APPROFONDIMENTO_CO:
            {
                ComputeStats.dati_app_mammo(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                            round_a, round_org, volontari, eta_da, eta_a, coorti,
                                            /*20080708 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti
                                            /*20080708 FINE MOD*/
                                            );
                break;
            }
        case Stats_paramBean.INDICAZIONI2LIV:
            {
                ComputeStats.indicazioniMA(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da, round_a,
                                           round_org, volontari, eta_da, eta_a, coorti, livello,
                                           tipo_referto == null ? null : new BigDecimal(tipo_referto.doubleValue()),
                                           /*20080708 MOD: aggiunta round inviti ai filtri*/
                                           round_inviti
                                           /*20080708 FINE MOD*/
                                           );
                break;
            }
        case Stats_paramBean.CHIRURGIE_CO:
            {
                ComputeStats.trattamenti_chirurgici(row, col, tpscr, ulss,dal, al, centro, zona, comune, round_da,
                                                    round_a, round_org, volontari, eta_da, eta_a, coorti, sesso,
                                                    lesione,
                                                    /*20080709 MOD: aggiunta round inviti ai filtri*/
                                                    round_inviti
                    /*20080709 FINE MOD*/
                );
                break;
            }
        default:
            {
                throw new Exception("Indicatore sconosciuto per questo tipo di screening");
                //  break;
            }
        }
    }


    public static void onChangeUlss() throws Exception {
        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        Stats_paramBean bean =
            (Stats_paramBean) BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
        Boolean regionale = (Boolean) ADFContext.getCurrent().getSessionScope().get("regionale");


        //se si tratta di un utente regionale puo' scegliere la ulss
        if (!regionale.booleanValue())
            return;

        if (bean.getUlss() == null || "-1".equals(bean.getUlss()))
            return;
        // throw new Exception("E' necessario selezionare un'azienda sanitaria");

        String ulss = bean.getUlss();
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        //filtro i comuni in base alla ulss
        ViewObject vo = am.findViewObject("Cnf_SoComuneView1");
        vo.setWhereClause("ULSS='" + ulss + "'");
        vo.executeQuery();

        //filtro le zone in base alla ulss
        vo = am.findViewObject("Cnf_SoDistrettoZonaView1");
        vo.setWhereClause("ULSS='" + ulss + "'");
        vo.executeQuery();

        //filtro i centri di prelievo in base alla ulss ed al tipo di screening
        vo = am.findViewObject("Cnf_SoCnfCentroPrelView1");
        vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'");
        vo.executeQuery();

        //filtro i centri di refertazione in base alla ulss ed al tipo di screening
        vo = am.findViewObject("Cnf_SoCnfCentroRefView1");
        vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'");
        vo.executeQuery();

        //filtro i medici in base alla ulss
        vo = am.findViewObject("Stats_SoMedicoView1");
        vo.setWhereClause("ULSS='" + ulss + "'");
        vo.executeQuery();

    }


    public static void setAges() throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject azienda = am.findViewObject("Cnf_SoAziendaView1");
        ViewObject scr = am.findViewObject("Cnf_SoCnfTpscrView1");

        String ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        Stats_paramBean bean =
            (Stats_paramBean) BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();
        Boolean regionale = (Boolean) ADFContext.getCurrent().getSessionScope().get("regionale");

        //utente regionale, utilizzo le eta' di default dello screening
        if (regionale.booleanValue()) {
            Row[] result = scr.getFilteredRows("Codscr", tpscr);
            if (result.length == 0)
                throw new Exception("Tipo di screening con codice " + tpscr + " non trovato");
            //do per scontato che lo screening ci sia
            Integer n = (Integer) result[0].getAttribute("Etada");
            bean.setEta_da(n.intValue());
            n = (Integer) result[0].getAttribute("Etaa");
            bean.setEta_a(n.intValue());
        }
        //utente comune, utilizzo le eta' di default della sua ulss
        else {
            Row[] result = azienda.getFilteredRows("Codaz", ulss);
            if (result.length == 0)
                throw new Exception("Azienda sanitaria con codice " + tpscr + " non rtrovata");
            Integer n1, n2;
            if ("CI".equals(tpscr)) {
                n1 = (Integer) result[0].getAttribute("Etacitomin");
                n2 = (Integer) result[0].getAttribute("Etacitomax");
            } else if ("MA".equals(tpscr)) {
                n1 = (Integer) result[0].getAttribute("Etamammomin");
                n2 = (Integer) result[0].getAttribute("Etamammomax");
            } else if ("CO".equals(tpscr)) {
                n1 = (Integer) result[0].getAttribute("Etacolonmin");
                n2 = (Integer) result[0].getAttribute("Etacolonmax");
            } else
                throw new Exception("Tipo di screening con codice " + tpscr + " non riconosciuto");

            bean.setEta_da(n1.intValue());
            bean.setEta_a(n2.intValue());


        }

        Stats_dynamicFilter bean2 =
            (Stats_dynamicFilter) BindingContext.getCurrent().findDataControl("Stats_dynamicFilterDataControl").getDataProvider();
        bean2.setEta_da(bean.getEta_da());
        bean2.setEta_a(bean.getEta_a());
    }
}
