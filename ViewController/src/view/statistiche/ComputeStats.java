package view.statistiche;

import insiel.dataHandling.DateUtils;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import model.common.Stats_AppModule;

import model.common.Stats_ClassiDiagnostiche1livViewRow;
import model.common.Stats_SoCnfCategTpinvitoViewRow;
import model.common.Stats_SoCnfEsclusioneViewRow;

import model.common.Stats_SoCnfEsitoinvitoViewRow;
import model.common.Stats_SoCnfTpinvitoViewRow;
import model.common.Stats_SoOpmedicoViewRow;

import model.common.Stats_SoStatsAggregazViewRow;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;

import model.datacontrol.Stats_dynamicFilter;

import model.datacontrol.Stats_paramBean;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class ComputeStats {
    public ComputeStats() {
    }


    public static void esclusioni(int row, int col,String tpscr, String ulss, String tpescl,
                                  Date dal, Date al_escluso, BigDecimal zona, String comune,
                                  BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                  BigDecimal eta_da, BigDecimal eta_a, Date data_escl,
                                  String sesso) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject esclusioni = am.findViewObject("Stats_SoCnfEsclusioneView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        try {

            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);

            String where = "TPSCR='" + tpscr + "' AND ULSS='" + AccessManager.CODREGIONALE + "'";
            if (tpescl != null) {
                where += " AND TPESCL='" + tpescl + "'";
            }
            esclusioni.setWhereClause(where);
            esclusioni.executeQuery();

            iter = ViewHelper.getRowSetIterator(esclusioni, "escl_iter");


            //LEGGO LE COLONNE
            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);

            //UNA COLONNA PER classe, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = "Esclusione";
            //   headers[2]="Numero";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            //   render[2]=true;
            totali = new int[headers.length];
            //   totali[2]=0;
            //per ogni tipo di invito (una riga dell amatrice)
            int j = iter.getRowCount();
            while (iter.hasNext()) {
                int rowTotal = 0;
                Stats_SoCnfEsclusioneViewRow rr = (Stats_SoCnfEsclusioneViewRow) iter.next();
                j--;
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, rr.getIdcnfescl());
                    r.setAttribute(1, rr.getDescrizione());
                }

                int x = 0;
                HashMap result = null;

                //contatore per le colonne
                int i = 2;

                for (i = 2; i < ages.size() + 2; i++) {
                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    //ottengo i confini della classe d'eta' (cioe' le colonne)
                    BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);


                    //l'intestazione dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        headers[i] = "<" + (borders[1].intValue() + 1);
                    //ultima riga
                    else if (borders[1] == null)
                        headers[i] = borders[0].intValue() + "+";
                    else //righe intermedie
                        headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                    //questa colonna va mostrata
                    render[i] = true;
                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callEsclusioniNewFunction(sesso, data_escl==null?null:new SimpleDateFormat("dd/MM/yyyy").format(data_escl), tpescl, new BigDecimal(rr.getIdcnfescl()),
                                                     ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                     al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                     round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                     round_ind_a, //round ind a
                                                     round_org, //round org
                                                     //centro
                                                     zona, //zona
                                                     borders[0], //eta da
                                                     borders[1], //eta a
                                                     comune, //comune
                                                     /*20080725 MOD*/
                                                     (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                     /*20080725 MOD*/
                                                     );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 MOD
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
           if(row<0 || col<0)
               r.setAttribute(i,new Integer(x));
               */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }


                    /*20080725 fine MOD*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Classe d'et\u00e0: " + headers[col + 2]);

                        return;

                    }
                    //incremento il totale
                    /* totali[i]+=x;
              rowTotal+=x;*/

                    //    System.out.println(rr.getIdcnfescl()+" "+ rr.getDescrizione()+" eta: "+borders[0]+"-"+borders[1]+" = "+x);

                }
                //totale di riga

                //memorizzo la label
                headers[i] = "Tutti";
                //questa colonna va mostrata
                render[i] = true;


                //ho chiesto di vedere questi soggetti, li calcolo

                result =
                    am.callEsclusioniNewFunction(sesso, data_escl==null?null:new SimpleDateFormat("dd/MM/yyyy").format(data_escl), tpescl, new BigDecimal(rr.getIdcnfescl()), ulss,
                                                 tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                 al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                 round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                 round_ind_a, //round ind a
                                                 round_org, //round org
                                                 //centro
                                                 zona, //zona
                                                 null, //eta da
                                                 null, //eta a
                                                 comune, //comune
                                                 /*20080725 MOD*/
                                                 (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                 /*20080725 MOD*/
                                                 );
                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 MOD
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
           if(row<0 || col<0){
                 r.setAttribute(i,new Integer(x));
                 totali[i]+=x;
               }
               */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(i, new Integer(x));
                    totali[i] += x;
                }


                /*20080725 fine MOD*/

                else if (row >= 0 && col >= 0 && row == j && col == i - 2) {
                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                          "Classe d'et\u00e0: " + headers[col + 2]);

                    return;


                }

            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }
            /*  if(ages!=null)
        ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(ages.size()));
        */
        }
    }


    public static void corteScreenati(int row, int col,String tpscr, String ulss, Date dal,
                                      Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                      BigDecimal round_org, BigDecimal eta_da, BigDecimal eta_a, BigDecimal volontari,
                                      boolean coorti, String sesso,
                                      /*20080707 MOD: aggiunta round inviti ai filtri*/
                                      BigDecimal round_inviti
                                      /*20080707 FINE MOD*/
                                      ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        // ViewObject cat_inviti=am.findViewObject("Stats_SoCnfCatTpinvitoView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        //  RowSetIterator iter=null;
        //  RowSetIterator iter1=null;
        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        //  String esito=ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO;


        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            //due colonne di dati, UN APER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[2 + 3];
            headers[0] = "Codice";
            headers[1] = "Classe d'et\u00e0";
            headers[2] = "Primi esami";
            headers[3] = "Esami successivi";
            headers[4] = "Totale";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            render[2] = true;
            render[3] = true;
            render[4] = true;
            totali = new int[headers.length];
            totali[2] = 0;
            totali[3] = 0;
            totali[4] = 0;


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");


            Vector ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);
            //i e' anche il contatore per le righe
            for (int i = ages.size() - 1; i >= 0; i--) {
                //controllo se sto lavorando per riempire o per ottenere le persone
                //se devo solo ottenere le persone e non sono nella riga giusta, meglio che vada avanti
                if (row >= 0 && col >= 0 && i != row)
                    continue;


                //ottengo i confini della classe d'eta'
                BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i);
                int rowTotal = 0;
                Row r = null;
                //controllo che tipo di lavoro sto facendo
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date().toString());

                    //la label dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        r.setAttribute(1, "<" + (borders[1].intValue() + 1));
                    //ultima riga
                    else if (borders[1] == null)
                        r.setAttribute(1, borders[0].intValue() + "+");
                    else //righe intermedie
                        r.setAttribute(1, borders[0].intValue() + "-" + borders[1].intValue());
                }


                int inviti = 0;

                HashMap result = null;
                //colonna primo round (l acalcolo in due casi:
                //1. sto calcolando l'indicatore
                if ((row < 0 || col < 0) ||
                    //2. sto estraendo i dati per quella colonna
                    (row >= 0 && col == 0)) {

                    result =
                        am.callCoorteScreenatiNewFunction(sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso),

                                                               new BigDecimal(1), new BigDecimal(1), round_org, centro,
                                                          zona, borders[0], borders[1], comune, volontari,
                                                          coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                          /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                          round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                          /*20082507 FINE MOD*/
                                                          );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20082507 MOD
               inviti=((Integer)result.get("howmany")).intValue();


               if(row<0 || col<0)
                 r.setAttribute(2,new Integer(inviti));

               else
               {
         */


                    if (row < 0 || col < 0) {
                        //valorizzo il totale solo se ho anche eseguito il conto
                        inviti = ((Integer) result.get("howmany")).intValue();
                        r.setAttribute(2, new Integer(inviti));
                        totali[2] += inviti;
                        rowTotal += inviti;

                    }

                    else {
                        /*20080725 fine mod*/

                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                    /*20080725 mod
               totali[2]+=inviti;
               rowTotal+=inviti;*/
                }


                inviti = 0;

                //colonna round succesivi (la calcolo in due casi:
                //1. sto calcolando l'indicatore
                if ((row < 0 || col < 0) ||
                    //2. sto estraendo i dati per quella colonna
                    (row >= 0 && col == 1)) {

                    //conto dei primi inviti
                    result =
                        am.callCoorteScreenatiNewFunction(sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), new BigDecimal(2), null,
                                                          round_org, centro, zona, borders[0], borders[1], comune,
                                                          volontari, coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                          /*20080707 MOD: aggiunta round inviti ai filtri*/
                                                          round_inviti,
                                                          (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1)
                                                          /*20080707 FINE MOD*/
                                                          );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20082507 MOD
               inviti=((Integer)result.get("howmany")).intValue();


               if(row<0 || col<0)
                 r.setAttribute(3,new Integer(inviti));

               else
               {
         */


                    if (row < 0 || col < 0) {
                        //valorizzo il totale solo se ho anche eseguito il conto
                        inviti = ((Integer) result.get("howmany")).intValue();
                        r.setAttribute(3, new Integer(inviti));
                        totali[3] += inviti;
                        rowTotal += inviti;

                    }

                    else {
                        /*20080725 fine mod*/


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    /*20082507 MOD
         totali[3]+=inviti;
         rowTotal+=inviti;*/

                }

                //totale


                //----------------------

                //conto dei primi inviti
                result =
                    am.callCoorteScreenatiNewFunction(sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), null, null, round_org,
                                                      centro, zona, borders[0], borders[1], comune, volontari,
                                                      coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                      /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                      round_inviti,
                                                      (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1)
                                                      /*20082507 FINE MOD*/
                                                      );
                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));

                /*20082507 MOD
               int x=((Integer)result.get("howmany")).intValue();
               if(x<0)
                 throw new Exception("Errore di database");
               if(row<0 || col<0){
                 r.setAttribute(4,new Integer(x));
                 totali[4]+=x;
               }

               */
                if (row < 0 || col < 0) {
                    int x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(4, new Integer(x));
                    totali[4] += x;
                }

                /*20080725 fine mod*/
                else if (row >= 0 && col >= 0 && row == i && col == 2) {

                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                    return;


                    //---------
                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }
            // ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(2));
        }
    }


    public static void diagnosi1liv(int row, int col,String tpscr, String ulss, Date dal,
                                    Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                    BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                    BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                    /*20080707 MOD: aggiunta round inviti ai filtri*/
                                    BigDecimal round_inviti
                                    /*20080707 FINE MOD*/
                                    ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject positi = am.findViewObject("Stats_SoCnfRef1livPOSITIView1");
        ViewObject inalim = am.findViewObject("Stats_SoCnfRef1livINALIMView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");
        ViewObject classi = am.findViewObject("Stats_ClassiDiagnostiche1livView1");

        RowSetIterator iter = null;
        RowSetIterator iter1 = null;
        RowSetIterator iter2 = null;

        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);


            //UNA COLONNA PER ESITO, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = "Classe di diagnosi";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];
            //per ogni classe di diagnosi (una riga dell amatrice)
            iter = ViewHelper.getRowSetIterator(classi, "classi_iter");
            iter1 = ViewHelper.getRowSetIterator(positi, "positi_iter");
            iter2 = ViewHelper.getRowSetIterator(inalim, "inalim_iter");
            //ottengo il numero totale di righe (2 e' per i negativi)
            int pos = iter1.getRowCount();
            int inal = iter2.getRowCount();
            int tot_rows = pos + inal + 2;

            while (iter.hasNext()) {
                int x = 0;
                HashMap result = null;

                Stats_ClassiDiagnostiche1livViewRow inv = (Stats_ClassiDiagnostiche1livViewRow) iter.next();
                //se si tratta di classi da suddividere ciclo su ognuna di esse
                String group = null;


                if ("Positivo".equals(inv.getTipoesame())) {
                    //i positivi sono gli ultimi, quindi si parte dal fondo
                    int j = tot_rows;
                    //se non sono in una riga compresa nei positivi proseguo
                    if (col >= 0 && !(tot_rows - pos <= row && row < tot_rows))
                        continue;

                    group = ConfigurationConstants.NOME_GRUPPO_POSITI;

                    while (iter1.hasNext()) {

                        int rowTotal = 0;
                        Row inv1 = iter1.next();

                        //se non sono nella riga desiderata e sto cercando le persone vado avanti
                        j--;
                        if (col >= 0 && row != j)
                            continue;


                        Row r = null;
                        if (col < 0 && row < 0) {
                            r = to_fill.createRow();
                            to_fill.insertRow(r);
                            //id e label della riga
                            r.setAttribute(0, inv1.getAttribute("Idcnfref1l"));
                            r.setAttribute(1, "Positivo: " + inv1.getAttribute("Descrizione"));
                        }
                        //contatore per le colonne
                        int i = 2;

                        for (i = 2; i < ages.size() + 2; i++) {
                            //se non ho trovato la colonna
                            if (row == j && col != i - 2)
                                continue;

                            //ottengo i confini della classe d'eta' (cioe' le colonne)
                            BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);


                            //l'intestazione dipende dalla situazione
                            //prima riga
                            if (borders[0] == null)
                                headers[i] = "<" + (borders[1].intValue() + 1);
                            //ultima riga
                            else if (borders[1] == null)
                                headers[i] = borders[0].intValue() + "+";
                            else //righe intermedie
                                headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                            //questa colonna va mostrata
                            render[i] = true;

                            //calcolo del numero di record che soddisfano i criteri
                            result =
                                am.callDiagnosi1livNewFunction(null, //lettore
                                                                    null, //prelevatore
                                                                    null, //supervisore
                                                                    null, //adeguatezza
                                                                    null, //giudizio diagnostico
                                                                    group,
                                                               //gruppo
                                                                    new BigDecimal(((Number) inv1.getAttribute("Idcnfref1l")).doubleValue()), //id configurzione
                                                               ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                               round_ind_a, //round ind a
                                                               round_org, //round org
                                                               centro, //centro
                                                               zona, //zona
                                                               borders[0], //eta da
                                                               borders[1], //eta a
                                                               comune, //comune
                                                               volontari, //volontaria
                                                               (String) inv.getTipoesame(),
                                                               coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                                               /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                               round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                               /*20082507 FINE MOD*/
                                                               );
                            if (result.get("errore") != null)
                                throw new Exception((String) result.get("errore"));


                            /*20080725 mod
                         x=((Integer)result.get("howmany")).intValue();
                         if(x<0)
                           throw new Exception("Errore di database");
                        if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                            if (row < 0 || col < 0) {
                                x = ((Integer) result.get("howmany")).intValue();
                                if (x < 0)
                                    throw new Exception("Errore di database");
                                r.setAttribute(i, new Integer(x));
                                totali[i] += x;
                                rowTotal += x;
                            } /*20080725 fine mod*/
                            else {


                                String inClause = (String) result.get("query");
                                Cnf_selectionBean c_bean =
                                    (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                                c_bean.setInClause(inClause);

                                //imposto i dati
                                ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                                      headers[1] + ": " +
                                                                                      ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                                ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                                      "Classe d'et\u00e0: " +
                                                                                      headers[col + 2]);

                                return;

                            }

                            //incremento il totale
                            /*  totali[i]+=x;

                         rowTotal+=x;*/


                        }

                        //totale di riga

                        //memorizzo la label
                        headers[i] = "Tutti";
                        //questa colonna va mostrata
                        render[i] = true;


                        //TOTALI

                        //calcolo del numero di record che soddisfano i criteri
                        result =
                            am.callDiagnosi1livNewFunction(null, //lettore
                                                                null, //prelevatore
                                                                null, //supervisore
                                                                null, //adeguatezza
                                                                null, //giudizio diagnostico
                                                                group,
                                                           //gruppo
                                                                new BigDecimal(((Number) inv1.getAttribute("Idcnfref1l")).doubleValue()), //id configurzione
                                                           ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                           round_ind_a, //round ind a
                                                           round_org, //round org
                                                           centro, //centro
                                                           zona, //zona
                                                           null, //eta da
                                                           null, //eta a
                                                           comune, //comune
                                                           volontari, //volontaria
                                                           (String) inv.getTipoesame(),
                                                           coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                                           /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                           round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                           /*20082507 FINE MOD*/
                                                           );
                        if (result.get("errore") != null)
                            throw new Exception((String) result.get("errore"));


                        /*20080725 mod
                         x=((Integer)result.get("howmany")).intValue();
                         if(x<0)
                           throw new Exception("Errore di database");
                         if(row<0 || col<0){
                 r.setAttribute(i,new Integer(x));
                 totali[i]+=x;
               }

                             */
                        if (row < 0 || col < 0) {
                            x = ((Integer) result.get("howmany")).intValue();
                            if (x < 0)
                                throw new Exception("Errore di database");
                            r.setAttribute(i, new Integer(x));
                            totali[i] += x;

                        } /*20080725 fine mod*/
                        else if (row >= 0 && col >= 0 && row == j && col == i - 2) {


                            String inClause = (String) result.get("query");
                            Cnf_selectionBean c_bean =
                                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                            c_bean.setInClause(inClause);

                            //imposto i dati
                            ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                                  headers[1] + ": " +
                                                                                  ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                            ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                                  "Classe d'et\u00e0: " + headers[col + 2]);

                            return;


                        }
                    }

                    iter1.closeRowSetIterator();
                    iter1 = null;

                } //if positivi
                //caso dei negativi: negativi e modreattive
                else if ("Negativo".equals(inv.getTipoesame())) {

                    //i negativi sono i primi

                    //se non sono in una riga compresa nei negativi proseguo
                    if (col >= 0 && !(row >= 0 && row < 2))
                        continue;

                    ///////////////////////////
                    //MODIFICAZIONI REATTIVE
                    //////////////////////////
                    int rowTotal = 0;
                    Row r = null;
                    if (col < 0 && row < 0) {
                        r = to_fill.createRow();
                        to_fill.insertRow(r);
                        //id e label della riga
                        r.setAttribute(0, ConfigurationConstants.CODICE_GIUDIA_MODREATTIVE);
                        r.setAttribute(1, "Negativo: modificazioni reattive");
                    }
                    //contatore per le colonne
                    int i = 2;

                    for (i = 2; i < ages.size() + 2; i++) {
                        //se sto cercando le persone ma non sono nella riga 1 (neg: mod reattive)
                        //e nella colonna corretta non faccio nulla
                        if (col >= 0 && row >= 0 && (col != i - 2 || row != 1))
                            continue;

                        //ottengo i confini della classe d'eta' (cioe' le colonne)
                        BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);


                        //l'intestazione dipende dalla situazione
                        //prima riga
                        if (borders[0] == null)
                            headers[i] = "<" + (borders[1].intValue() + 1);
                        //ultima riga
                        else if (borders[1] == null)
                            headers[i] = borders[0].intValue() + "+";
                        else //righe intermedie
                            headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                        //questa colonna va mostrata
                        render[i] = true;


                        result =
                            am.callDiagnosi1livNewFunction(null, //lettore
                                                                null, //prelevatore
                                                                null, //supervisore
                                                                null,
                                                           //adeguatezza
                                                                new BigDecimal(ConfigurationConstants.CODICE_GIUDIA_MODREATTIVE.doubleValue()), //giudizio diagnostico
                                            null, //gruppo
                                            null, //id configurzione
                                            ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                            round_ind_a, //round ind a
                                            round_org, //round org
                                            centro, //centro
                                            zona, //zona
                                            borders[0], //eta da
                                            borders[1], //eta a
                                            comune, //comune
                                            volontari, //volontaria
                                            inv.getTipoesame(), //NEGATIVO
                                            coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                            /*20082507 FINE MOD*/
                                            );
                        if (result.get("errore") != null)
                            throw new Exception((String) result.get("errore"));

                        /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                        if (row < 0 || col < 0) {
                            x = ((Integer) result.get("howmany")).intValue();
                            if (x < 0)
                                throw new Exception("Errore di database");
                            r.setAttribute(i, new Integer(x));
                            totali[i] += x;
                            rowTotal += x;
                        }

                        /*20080725 fine mod*/
                        else {


                            String inClause = (String) result.get("query");
                            Cnf_selectionBean c_bean =
                                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                            c_bean.setInClause(inClause);

                            //imposto i dati
                            ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                                  headers[1] + ": " +
                                                                                  ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                            ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                                  "Classe d'et\u00e0: " + headers[col + 2]);

                            return;

                        }
                        //incremento il totale
                        /* totali[i]+=x;
           //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
             rowTotal+=x;*/


                    }


                    //TOTALE Di RIGA

                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callDiagnosi1livNewFunction(null, //lettore
                                                            null, //prelevatore
                                                            null, //supervisore
                                                            null,
                                                       //adeguatezza
                                                            new BigDecimal(ConfigurationConstants.CODICE_GIUDIA_MODREATTIVE.doubleValue()), //giudizio diagnostico
                                        null, //gruppo
                                        null, //id configurzione
                                        ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                        round_ind_a, //round ind a
                                        round_org, //round org
                                        centro, //centro
                                        zona, //zona
                                        null, //eta da
                                        null, //eta a
                                        comune, //comune
                                        volontari, //volontaria
                                        inv.getTipoesame(), //NEGATIVO
                                        coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                        /*20082507 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                        /*20082507 FINE MOD*/
                                        );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0){
                 r.setAttribute(i,new Integer(x));
                 totali[i]+=x;
               }
                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else if (row >= 0 && col >= 0 && row == 1 && col == i - 2) {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Classe d'et\u00e0: " + headers[col + 2]);

                        return;


                    }


                    ///////////////////////////
                    //NEGATIVO
                    //////////////////////////
                    rowTotal = 0;
                    if (row < 0 || col < 0) {
                        r = to_fill.createRow();
                        to_fill.insertRow(r);
                        //id e label della riga
                        r.setAttribute(0, ConfigurationConstants.CODICE_GIUDIA_NEGATIVO);
                        r.setAttribute(1, "Negativo: negativo");
                    }

                    //contatore per le colonne
                    i = 2;

                    for (i = 2; i < ages.size() + 2; i++) {

                        //se sto cercando le persone ma non sono nella riga 0 (neg: negativo)
                        //e nella colonna corretta non faccio nulla
                        if (col >= 0 && row >= 0 && (col != i - 2 || row != 0))
                            continue;

                        //ottengo i confini della classe d'eta' (cioe' le colonne)
                        BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);


                        //l'intestazione dipende dalla situazione
                        //prima riga
                        if (borders[0] == null)
                            headers[i] = "<" + (borders[1].intValue() + 1);
                        //ultima riga
                        else if (borders[1] == null)
                            headers[i] = borders[0].intValue() + "+";
                        else //righe intermedie
                            headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                        //questa colonna va mostrata
                        render[i] = true;

                        result =
                            am.callDiagnosi1livNewFunction(null, //lettore
                                                                null, //prelevatore
                                                                null, //supervisore
                                                                null,
                                                           //adeguatezza
                                                                new BigDecimal(ConfigurationConstants.CODICE_GIUDIA_NEGATIVO.doubleValue()), //giudizio diagnostico
                                            null, //gruppo
                                            null, //id configurzione
                                            ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                            round_ind_a, //round ind a
                                            round_org, //round org
                                            centro, //centro
                                            zona, //zona
                                            borders[0], //eta da
                                            borders[1], //eta a
                                            comune, //comune
                                            volontari, //volontaria
                                            inv.getTipoesame(), //NEGATIVO
                                            coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                            /*20082507 FINE MOD*/
                                            );
                        if (result.get("errore") != null)
                            throw new Exception((String) result.get("errore"));

                        /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                        if (row < 0 || col < 0) {
                            x = ((Integer) result.get("howmany")).intValue();
                            if (x < 0)
                                throw new Exception("Errore di database");
                            r.setAttribute(i, new Integer(x));
                            totali[i] += x;
                            rowTotal += x;
                        }

                        /*20080725 fine mod*/

                        else {


                            String inClause = (String) result.get("query");
                            Cnf_selectionBean c_bean =
                                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                            c_bean.setInClause(inClause);

                            //imposto i dati
                            ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                                  headers[1] + ": " +
                                                                                  ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                            ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                                  "Classe d'et\u00e0: " + headers[col + 2]);

                            return;

                        }
                        //incremento il totale
                        /*  totali[i]+=x;

             rowTotal+=x;*/

                    }


                    headers[i] = "Tutti";
                    //questa colonna va mostrata
                    render[i] = true;


                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callDiagnosi1livNewFunction(null, //lettore
                                                            null, //prelevatore
                                                            null, //supervisore
                                                            null,
                                                       //adeguatezza
                                                            new BigDecimal(ConfigurationConstants.CODICE_GIUDIA_NEGATIVO.doubleValue()), //giudizio diagnostico
                                        null, //gruppo
                                        null, //id configurzione
                                        ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                        round_ind_a, //round ind a
                                        round_org, //round org
                                        centro, //centro
                                        zona, //zona
                                        null, //eta da
                                        null, //eta a
                                        comune, //comune
                                        volontari, //volontaria
                                        inv.getTipoesame(), //NEGATIVO
                                        coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                        /*20082507 MOD: aggiunta round inviti ai filtri*/
                                        round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                        /*20082507 FINE MOD*/
                                        );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
              if(row<0 || col<0){
                 r.setAttribute(i,new Integer(x));
                 totali[i]+=x;
               }
                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                    }

                    /*20080725 fine mod*/


                    else if (row >= 0 && col >= 0 && row == 0 && col == i - 2) {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Classe d'et\u00e0: " + headers[col + 2]);

                        return;


                    }


                } // NEGATIVO
                else {

                    //INADEGUATI

                    //gli inadeguati sono tra i negativi ed i positivi
                    int j = tot_rows - pos;
                    //se non sono in una riga compresa negli inadeguati proseguo
                    if (col >= 0 && !(tot_rows - pos - inal <= row && row < tot_rows))
                        continue;

                    while (iter2.hasNext()) {

                        int rowTotal = 0;
                        Row inv1 = iter2.next();
                        j--;
                        //se sono nella riga sbagliata proseguo
                        if (col >= 0 && row >= 0 && row != j)
                            continue;


                        Row r = null;
                        if (col < 0 || row < 0) {
                            r = to_fill.createRow();
                            to_fill.insertRow(r);
                            //id e label della riga
                            r.setAttribute(0, inv1.getAttribute("Idcnfref1l"));
                            r.setAttribute(1, "Inadeguato " + inv1.getAttribute("Descrizione"));
                        }
                        //contatore per le colonne
                        int i = 2;

                        for (i = 2; i < ages.size() + 2; i++) {
                            if (col >= 0 && row == j && col != i - 2)
                                continue;

                            //ottengo i confini della classe d'eta' (cioe' le colonne)
                            BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);


                            //l'intestazione dipende dalla situazione
                            //prima riga
                            if (borders[0] == null)
                                headers[i] = "<" + (borders[1].intValue() + 1);
                            //ultima riga
                            else if (borders[1] == null)
                                headers[i] = borders[0].intValue() + "+";
                            else //righe intermedie
                                headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                            //questa colonna va mostrata
                            render[i] = true;

                            //calcolo del numero di record che soddisfano i criteri
                            result =
                                am.callDiagnosi1livNewFunction(null, //lettore
                                                                    null, //prelevatore
                                                                    null,
                                                               //supervisore
                                                                    new BigDecimal(ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.doubleValue()), //adeguatezza
                                                null, //giudizio diagnostico
                                                ConfigurationConstants.NOME_GRUPPO_INALIM,
                                                //gruppo
                                                new BigDecimal(((Number) inv1.getAttribute("Idcnfref1l")).doubleValue()), //id configurzione
                                                ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                round_ind_a, //round ind a
                                                round_org, //round org
                                                centro, //centro
                                                zona, //zona
                                                borders[0], //eta da
                                                borders[1], //eta a
                                                comune, //comune
                                                volontari, //volontaria
                                                inv.getTipoesame(), //inadeguato
                                                coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                                /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                /*20082507 FINE MOD*/
                                                );
                            if (result.get("errore") != null)
                                throw new Exception((String) result.get("errore"));

                            /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                            if (row < 0 || col < 0) {
                                x = ((Integer) result.get("howmany")).intValue();
                                if (x < 0)
                                    throw new Exception("Errore di database");
                                r.setAttribute(i, new Integer(x));
                                totali[i] += x;
                                rowTotal += x;
                            }

                            /*20080725 fine mod*/

                            else {


                                String inClause = (String) result.get("query");
                                Cnf_selectionBean c_bean =
                                    (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                                c_bean.setInClause(inClause);

                                //imposto i dati
                                ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                                      headers[1] + ": " +
                                                                                      ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                                ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                                      "Classe d'et\u00e0: " +
                                                                                      headers[col + 2]);

                                return;

                            }
                            //incremento il totale
                            /*   totali[i]+=x;
                       //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                         rowTotal+=x;*/


                        } //end for

                        //totale di riga


                        //memorizzo la label
                        headers[i] = "Tutti";
                        //questa colonna va mostrata
                        render[i] = true;


                        //TOTALI

                        //calcolo del numero di record che soddisfano i criteri
                        result =
                            am.callDiagnosi1livNewFunction(null, //lettore
                                                                null, //prelevatore
                                                                null,
                                                           //supervisore
                                                                new BigDecimal(ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.doubleValue()), //adeguatezza
                                            null, //giudizio diagnostico
                                            ConfigurationConstants.NOME_GRUPPO_INALIM, //gruppo
                                            new BigDecimal(((Number) inv1.getAttribute("Idcnfref1l")).doubleValue()), //id configurzione
                                            ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                            round_ind_a, //round ind a
                                            round_org, //round org
                                            centro, //centro
                                            zona, //zona
                                            null, //eta da
                                            null, //eta a
                                            comune, //comune
                                            volontari, //volontaria
                                            inv.getTipoesame(), //inadeguato
                                            coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                            /*20082507 FINE MOD*/
                                            );
                        if (result.get("errore") != null)
                            throw new Exception((String) result.get("errore"));

                        /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
              if(row<0 || col<0){
                 r.setAttribute(i,new Integer(x));
                 totali[i]+=x;
               }
                             */
                        if (row < 0 || col < 0) {
                            x = ((Integer) result.get("howmany")).intValue();
                            if (x < 0)
                                throw new Exception("Errore di database");
                            r.setAttribute(i, new Integer(x));
                            totali[i] += x;
                        }

                        /*20080725 fine mod*/


                        else if (row >= 0 && col >= 0 && row == j && col == i - 2) {


                            String inClause = (String) result.get("query");
                            Cnf_selectionBean c_bean =
                                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                            c_bean.setInClause(inClause);

                            //imposto i dati
                            ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                                  headers[1] + ": " +
                                                                                  ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                            ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                                  "Classe d'et\u00e0: " + headers[col + 2]);

                            return;


                        }


                    } //end while

                    iter2.closeRowSetIterator();
                    iter2 = null;

                } //end else
            } //end while (sulle classi di diagnosi)
        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter1 != null)
                iter1.closeRowSetIterator();
            if (iter2 != null)
                iter2.closeRowSetIterator();


            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }
            /*   if(ages!=null)
      ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(ages.size()));
    */
        }
    }


    public static void catDiagnosi1liv(int row, int col,String tpscr, String ulss, Date dal,
                                       Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                       BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                       BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                       /*20080707 MOD: aggiunta round inviti ai filtri*/
                                       BigDecimal round_inviti
                                       /*20080707 FINE MOD*/
                                       ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");
        ViewObject classi = am.findViewObject("Stats_ClassiDiagnostiche1livView1");

        RowSetIterator iter = null;

        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;
        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            //LEGGO LE COLONNE
            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);


            //UNA COLONNA PER ESITO, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = "Classe di diagnosi";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];


            //leggo le righe dei giudizi diagnostici
            //  iter=ViewHelper.getRowSetIterator(diagnosi,"diagnosi_iter");
            iter = ViewHelper.getRowSetIterator(classi, "classi_iter");
            //per ogni classe di diagnosi (una riga dell amatrice)
            int j = iter.getRowCount();
            while (iter.hasNext()) {
                j--;
                Row inv = iter.next();
                //controllo se sto lavorando per riempire o per ottenere le persone
                //se devo solo ottenere le persone e non sono nella riga giusta, meglio che vada avanti
                //le righe sono inserite a rovescio!!!!
                if (row >= 0 && col >= 0 && j != row)
                    continue;

                int rowTotal = 0;

                Row r = null;
                //se sto facendo il lavoro completo
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date().toString());
                    r.setAttribute(1, inv.getAttribute("Tipoesame"));
                }

                BigDecimal adeguatezza;
                if (((String) inv.getAttribute("Tipoesame")).equals("Inadeguato"))
                    //per gli inadeguati considero l'adeguatezza, perche' il tipo esame non basta
                    //(conterei anche vetrini non pervenuti o danneggiati)
                    adeguatezza = new BigDecimal(ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.doubleValue());
                else
                    //positivi e negativi, invece, potrebbero avere sia adeguato ch elimitato, dunque lascio null
                    adeguatezza = null;

                int x;
                HashMap result;

                //contatore per le colonne
                int i = 2;

                for (i = 2; i < ages.size() + 2; i++) {
                    //se sto cercando le persone e sono nella colonna sbagliata vado avanti
                    if (row >= 0 && col >= 0 && col != i - 2)
                        continue;
                    //ottengo i confini della classe d'eta' (cioe' le colonne)
                    BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);
                    x = 0;
                    result = null;

                    //l'intestazione dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        headers[i] = "<" + (borders[1].intValue() + 1);
                    //ultima riga
                    else if (borders[1] == null)
                        headers[i] = borders[0].intValue() + "+";
                    else //righe intermedie
                        headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                    //questa colonna va mostrata
                    render[i] = true;


                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callDiagnosi1livNewFunction(null, //lettore
                                                            null, //prelevatore
                                                            null, //supervisore
                                                            adeguatezza, //adeguatezza
                                                            //   new BigDecimal(((Number)inv.getAttribute("Idcnfref1l")).doubleValue()),//giudizio diagnostico
                                                            null, //giudizio diagnostico
                                                            null, //gruppo
                                                            null, //id configurzione
                                                            ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                            round_ind_a, //round ind a
                                                            round_org, //round org
                                                            centro, //centro
                                                            zona, //zona
                                                            borders[0], //eta da
                                                            borders[1], //eta a
                                                            comune, //comune
                                                            volontari, //volontaria
                                                            (String) inv.getAttribute("Tipoesame"),
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Classe d'et\u00e0: " + headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    /* totali[i]+=x;
         //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
           rowTotal+=x;*/


                } //end for


                //totale di riga

                //memorizzo la label
                headers[i] = "Tutti";
                //questa colonna va mostrata
                render[i] = true;


                result =
                    am.callDiagnosi1livNewFunction(null, //lettore
                                                        null, //prelevatore
                                                        null, //supervisore
                                                        adeguatezza, //adeguatezza
                                                        //   new BigDecimal(((Number)inv.getAttribute("Idcnfref1l")).doubleValue()),//giudizio diagnostico
                                                        null, //giudizio diagnostico
                                                        null, //gruppo
                                                        null, //id configurzione
                                                        ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                        round_ind_a, //round ind a
                                                        round_org, //round org
                                                        centro, //centro
                                                        zona, //zona
                                                        null, //eta da
                                                        null, //eta a
                                                        comune, //comune
                                                        volontari, //volontaria
                                                        (String) inv.getAttribute("Tipoesame"),
                                                   coorti ? new BigDecimal(1) : new BigDecimal(0), //positivo
                                                   /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                   round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                   /*20082507 FINE MOD*/
                                                   );
                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
                    if(row<0 || col<0){
               r.setAttribute(i,new Integer(x));
               totali[i]+=x;
           }
                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(i, new Integer(x));
                    totali[i] += x;
                }

                /*20080725 fine mod*/

                else if (row >= 0 && col >= 0 && col == i - 2 && row == j) {


                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                          "Classe d'et\u00e0: " + headers[col + 2]);

                    return;

                }

            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }
            /* if(ages!=null)
      ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(ages.size()));
      */
        }


    }


    public static void cnfref2liv(int row, int col, String nome_gruppo, String header_desc, 
                                  String tpscr, String ulss, Date dal, Date al_escluso, BigDecimal centro,
                                  BigDecimal zona, String comune, BigDecimal round_ind_da, BigDecimal round_ind_a,
                                  BigDecimal round_org, BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a,
                                  boolean coorti, String sesso,
                                  /*20080707 MOD: aggiunta round inviti ai filtri*/
                                  BigDecimal round_inviti
                                  /*20080707 FINE MOD*/
                                  ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject colpes = am.findViewObject("Stats_SoCnfRef2liv" + nome_gruppo + "View1");

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);
            iter = ViewHelper.getRowSetIterator(colpes, "colpes_iter");

            //UNA COLONNA PER ESITO, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = header_desc;
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];
            int j = iter.getRowCount();
            //per ogni classe di diagnosi (una riga dell amatrice)
            while (iter.hasNext()) {

                int rowTotal = 0;
                Row inv = iter.next();
                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getAttribute("Idcnfref2l"));
                    r.setAttribute(1, inv.getAttribute("Descrizione"));
                }
                //contatore per le colonne
                int i = 2;
                // System.out.println(inv.getAttribute("Idcnfref2l")+": "+inv.getAttribute("Descrizione"));


                int x = 0;
                HashMap result = null;

                for (i = 2; i < ages.size() + 2; i++) {


                    //ottengo i confini della classe d'eta' (cioe' le colonne)
                    BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);

                    //se sto cercando le persone e non sono nell ariga desiderata continuo

                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    //l'intestazione dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        headers[i] = "<" + (borders[1].intValue() + 1);
                    //ultima riga
                    else if (borders[1] == null)
                        headers[i] = borders[0].intValue() + "+";
                    else //righe intermedie
                        headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                    //questa colonna va mostrata
                    render[i] = true;

                    //calcolo del numero di record che soddisfano i criteri
                    if ("CI".equals(tpscr)) {
                        result =
                            (HashMap) am.callCount2livNewFunction(null, nome_gruppo,
                                                                  new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()), //id configurzione
                                                                  null, //colpo val
                                                                  ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                                  round_ind_a, //round ind a
                                                                  round_org, //round org
                                                                  centro, //centro
                                                                  zona, //zona
                                                                  borders[0], //eta da
                                                                  borders[1], //eta a
                                                                  comune, //comune
                                                                  volontari,
                                                                  coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                                  /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                                  round_inviti,
                                                                  (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                                  /*20082507 FINE MOD*/
                                                                  );

                    } else if ("CO".equals(tpscr)) {

                        result =
                            (HashMap) am.callCount2livCOFunction(sesso, null,
                                                                 //idsugg
                                                                      ConfigurationConstants.NOME_GRUPPO_DIAGNOSI.equals(nome_gruppo) ?
                                                                      new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()) :
                                                                      null,
                                                    //diagnosi peggiore
                                                    ConfigurationConstants.NOME_GRUPPO_CONCL_CO.equals(nome_gruppo) ?
                                                    new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()) :
                                                    null, //null
                                                    ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                    round_ind_a, //round ind a
                                                    round_org, //round org
                                                    centro, //centro
                                                    zona, //zona
                                                    borders[0], //eta da
                                                    borders[1], //eta a
                                                    comune, //comune
                                                    volontari, coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                    /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                    round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                    /*20082507 FINE MOD*/
                                                    );
                    } else if ("MA".equals(tpscr)) {

                        result =
                            (HashMap) am.callCount2livMammoFunction(null, //idsugg
                                                                         null, //mammo
                                                                         null, //eco
                                                                         null, //clinico
                                                                         null, //esito cito
                                                                         null,
                                                                    //esito agobiopsia
                                                                         new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()), //diagnosi
                                                                    ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                                    round_ind_a, //round ind a
                                                                    round_org, //round org
                                                                    centro, //centro
                                                                    zona, //zona
                                                                    borders[0], //eta da
                                                                    borders[1], //eta a
                                                                    comune, //comune
                                                                    volontari,
                                                                    coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                                    /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                                    round_inviti,
                                                                    (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                                    /*20082507 FINE MOD*/
                                                                    );

                    } else
                        result = null;

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Classe d'et\u00e0: " + headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    /*     totali[i]+=x;
                       //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                         rowTotal+=x;*/


                }

                //totale di riga

                //memorizzo la label
                headers[i] = "Tutti";
                //questa colonna va mostrata
                render[i] = true;

                if ("CI".equals(tpscr)) {
                    result =
                        (HashMap) am.callCount2livNewFunction(null, nome_gruppo,
                                                              new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()), //id configurzione
                                                              null, //colpo val
                                                              ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                              round_ind_a, //round ind a
                                                              round_org, //round org
                                                              centro, //centro
                                                              zona, //zona
                                                              null, //eta da
                                                              null, //eta a
                                                              comune, //comune
                                                              volontari, coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                              /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                              round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                              /*20082507 FINE MOD*/
                                                              );
                } else if ("CO".equals(tpscr)) {
                    result =
                        am.callCount2livCOFunction(sesso, null,
                                                   //idsugg
                                                        ConfigurationConstants.NOME_GRUPPO_DIAGNOSI.equals(nome_gruppo) ?
                                                        new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()) :
                                                        null,
                                      //diagnosi peggiore
                                      ConfigurationConstants.NOME_GRUPPO_CONCL_CO.equals(nome_gruppo) ?
                                      new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()) : null, //null
                                      ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                      round_ind_a, //round ind a
                                      round_org, //round org
                                      centro, //centro
                                      zona, //zona
                                      null, //eta da
                                      null, //eta a
                                      comune, //comune
                                      volontari, coorti ? new BigDecimal(1) : new BigDecimal(0),
                                      /*20082507 MOD: aggiunta round inviti ai filtri*/
                                      round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                      /*20082507 FINE MOD*/
                                      );
                } else if ("MA".equals(tpscr)) {

                    result =
                        (HashMap) am.callCount2livMammoFunction(null, //idsugg
                                                                     null, //mammo
                                                                     null, //eco
                                                                     null, //clinico
                                                                     null, //esito cito
                                                                     null,
                                                                //esito agobiopsia
                                                                     new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()), //diagnosi
                                                                ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                                round_ind_a, //round ind a
                                                                round_org, //round org
                                                                centro, //centro
                                                                zona, //zona
                                                                null, //eta da
                                                                null, //eta a
                                                                comune, //comune
                                                                volontari,
                                                                coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                                /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                                round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                                /*20082507 FINE MOD*/
                                                                );

                } else
                    result = null;


                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
              if(row<0 || col<0){
                 r.setAttribute(i,new Integer(x));
                 totali[i]+=x;
             }
                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(i, new Integer(x));
                    totali[i] += x;

                }

                /*20080725 fine mod*/


                else {


                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                          "Classe d'et\u00e0: " + headers[col + 2]);

                    return;

                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }
            /*  if(ages!=null)
        ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(ages.size()));
        */
        }
    }


    public static void operatoriMedici(int row, int col,int tipoop, String tpscr, String ulss,
                                       Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                       BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                       BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                       /*20080709 MOD: aggiunta round inviti ai filtri*/
                                       BigDecimal round_inviti
                                       /*20080709 FINE MOD*/
                                       ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();


        ViewObject operatori = am.findViewObject("Stats_SoOpmedicoView1");

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;

        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            String where = "ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND ";

            if (1 == tipoop) {
                where += "CODOP=" + ConfigurationConstants.CODICE_OSTETRICA;
            } else if (2 == tipoop) {
                where += "CODOP=" + ConfigurationConstants.CODICE_CITOSCREENER;
            } else if (3 == tipoop) {
                where += "CODOP=" + ConfigurationConstants.CODICE_PATOLOGO;
            } else
                throw new Exception("Tipo di operatore '" + tipoop + "' non supportato");
            operatori.setWhereClause(where);
            operatori.executeQuery();


            //id | nome | totale esami | %inadeguati | %inadeg. tecnici | %indeg. flogictici | % positivi
            headers = new String[7];
            headers[0] = "Codice";
            // headers[1]=tipoop;
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];


            //imposto l'header del tipo di operatore
            switch (tipoop) {
            case 1:
                {
                    headers[1] = "Prelevatore";
                    break;
                }
            case 2:
                {
                    headers[1] = "Citoscreener";
                    break;
                }
            case 3:
                {
                    headers[1] = "Supervisore";
                    break;
                }
            default:
                {
                    break;
                }

            }

            //leggo le righe dei giudizi diagnostici
            iter = ViewHelper.getRowSetIterator(operatori, "operatori_iter");

            int j = iter.getRowCount();
            //per ogni classe di diagnosi (una riga dell amatrice)
            while (iter.hasNext()) {

                //   int rowTotal=0;
                Stats_SoOpmedicoViewRow inv = (Stats_SoOpmedicoViewRow) iter.next();
                j--;
                if (row >= 0 && col >= 0 && row != j)
                    continue;


                Row r = null;

                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getIdop());
                    r.setAttribute(1, inv.getCognome() + " " + inv.getNome());
                }

                BigDecimal lettore = null;
                BigDecimal prelevatore = null;
                BigDecimal supervisore = null;
                if (tipoop == 2) {
                    lettore = new BigDecimal(inv.getIdop().doubleValue());
                } else if (tipoop == 1) {
                    prelevatore = new BigDecimal(inv.getIdop().doubleValue());
                } else {
                    supervisore = new BigDecimal(inv.getIdop().doubleValue());
                }

                int x = 0;
                HashMap result = null;

                //  COLONNA COL NUMERO TOTALE DI ESAMI
                int i = 2;
                headers[i] = "N. test";
                render[i] = true;

                if ((row < 0 && col < 0) || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callDiagnosi1livNewFunction(lettore, //lettore
                                                            prelevatore, //prelevatore
                                                            supervisore, //supervisore
                                                            null, //adeguatezza
                                                            null, //giudizio diagnostico
                                                            null, //gruppo
                                                            null, //id configurzione
                                                            ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                            round_ind_a, //round ind a
                                                            round_org, //round org
                                                            centro, //centro
                                                            zona, //zona
                                                            eta_da, //eta da
                                                            eta_a, //eta a
                                                            comune, //comune
                                                            volontari, //volontaria
                                                            null, //tipo esame
                                                            coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                            /*20082507 FINE MOD*/
                                                            );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                }

                int totale = x;
                //totali[i]+=x;

                //  COLONNA CON LA PERCENTUALE DI POSITIVI
                i = 3;
                headers[i] = "Positivi";
                render[i] = true;

                if ((row < 0 && col < 0) ||
                    (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result =
am.callDiagnosi1livNewFunction(lettore, //lettore
                                                            prelevatore, //prelevatore
                                                            supervisore, //supervisore
                                                            null,
                               //adeguatezza
                                                            new BigDecimal(ConfigurationConstants.CODICE_GIUDIA_POSITIVO.doubleValue()), //giudizio diagnostico
                               null, //gruppo
                               null, //id configurzione
                               ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                               round_ind_a, //round ind a
                               round_org, //round org
                               centro, //centro
                               zona, //zona
                               eta_da, //eta da
                               eta_a, //eta a
                               comune, //comune
                               volontari, //volontaria
                               null, //tipo esame
                               coorti ? new BigDecimal(1) : new BigDecimal(0),
                               /*20082507 MOD: aggiunta round inviti ai filtri*/
                               round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                               /*20082507 FINE MOD*/
                               );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                    r.setAttribute(i,x+" ("+Double.toString(Stats_helper.percentage(totale,x))+"%)");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Test: " + headers[col + 2]);

                        return;

                    }

                    // totali[i]+=x;
                }


                //  COLONNA CON LA PERCENTUALE DI INADEGUATI
                i = 4;
                headers[i] = "Inadeguati";
                render[i] = true;

                if ((row < 0 && col < 0) ||
                    (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result =
am.callDiagnosi1livNewFunction(lettore, //lettore
                                                            prelevatore, //prelevatore
                                                            supervisore,
                               //supervisore
                                                            new BigDecimal(ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.doubleValue()), //adeguatezza
             null, //giudizio diagnostico
             null, //gruppo
             null, //id configurzione
             ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
             round_ind_a, //round ind a
             round_org, //round org
             centro, //centro
             zona, //zona
             eta_da, //eta da
             eta_a, //eta a
             comune, //comune
             volontari, //volontaria
             null, //tipo esame
             coorti ? new BigDecimal(1) : new BigDecimal(0),
             /*20082507 MOD: aggiunta round inviti ai filtri*/
             round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
             /*20082507 FINE MOD*/
             );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                    r.setAttribute(i,x+" ("+Double.toString(Stats_helper.percentage(totale,x))+"%)");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;
                    }

                    /*20080725 fine mod*/


                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Test: " + headers[col + 2]);

                        return;

                    }

                    //   totali[i]+=x;
                }


                //  COLONNA CON LA PERCENTUALE DI INADEGUATI TECNICI
                i = 5;
                headers[i] = "Inadeguati tecnici";
                render[i] = true;

                if ((row < 0 && col < 0) ||
                    (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result =
am.callDiagnosi1livNewFunction(lettore, //lettore
                                                            prelevatore, //prelevatore
                                                            supervisore,
                               //supervisore
                                                            new BigDecimal(ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.doubleValue()), //adeguatezza
             null, //giudizio diagnostico
             ConfigurationConstants.NOME_GRUPPO_INALIM, //gruppo
             new BigDecimal(ConfigurationConstants.CODICE_INALIM_TECNICO.doubleValue()), //id configurzione
             ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
             round_ind_a, //round ind a
             round_org, //round org
             centro, //centro
             zona, //zona
             eta_da, //eta da
             eta_a, //eta a
             comune, //comune
             volontari, //volontaria
             null, //tipo esame
             coorti ? new BigDecimal(1) : new BigDecimal(0),
             /*20082507 MOD: aggiunta round inviti ai filtri*/
             round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
             /*20082507 FINE MOD*/
             );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                      r.setAttribute(i,x+" ("+Double.toString(Stats_helper.percentage(totale,x))+"%)");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Test: " + headers[col + 2]);

                        return;

                    }

                    //  totali[i]+=x;

                }


                //  COLONNA CON LA PERCENTUALE DI INADEGUATI FLOGISTICI
                i = 6;
                headers[i] = "Inadeguati flogistici";
                render[i] = true;

                if ((row < 0 && col < 0) ||
                    (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result =
am.callDiagnosi1livNewFunction(lettore, //lettore
                                                            prelevatore, //prelevatore
                                                            supervisore,
                               //supervisore
                                                            new BigDecimal(ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.doubleValue()), //adeguatezza
             null, //giudizio diagnostico
             ConfigurationConstants.NOME_GRUPPO_INALIM, //gruppo
             new BigDecimal(ConfigurationConstants.CODICE_INALIM_FLOGISTICO.doubleValue()), //id configurzione
             ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
             round_ind_a, //round ind a
             round_org, //round org
             centro, //centro
             zona, //zona
             eta_da, //eta da
             eta_a, //eta a
             comune, //comune
             volontari, //volontaria
             null, //tipo esame
             coorti ? new BigDecimal(1) : new BigDecimal(0),
             /*20082507 MOD: aggiunta round inviti ai filtri*/
             round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
             /*20082507 FINE MOD*/
             );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                        r.setAttribute(i,x+" ("+Double.toString(Stats_helper.percentage(totale,x))+"%)");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/


                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);


                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Test: " + headers[col + 2]);

                        return;

                    }

                    // totali[i]+=x;
                }

            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);

                //calcolo anche l eprcentuali sui totali
                String[] tot = new String[totali.length];
                tot[2] = new Integer(totali[2]).toString();
                for (int h = 3; h < totali.length; h++) {
                    tot[h] = totali[h] + " (" + Double.toString(Stats_helper.percentage(totali[2], totali[h])) + "%)";
                }

                ADFContext.getCurrent().getSessionScope().remove("stats_inviti_esiti_totali");
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali_perc", tot);

            }


        }
    }


    public static void esitiColpo(int row, int col,String tpscr, String ulss, Date dal,
                                  Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                  BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                  BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                  /*20080707 MOD: aggiunta round inviti ai filtri*/
                                  BigDecimal round_inviti
                                  /*20080707 FINE MOD*/
                                  ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject colpvl = am.findViewObject("Stats_SoCnfRef2livView1");
        ViewObject colpes = am.findViewObject("Stats_SoCnfRef2livCOLPESView1");

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;
        RowSetIterator iter2 = null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);


            //UNA COLONNA PER ESITO, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = "Diagnosi colposcopica";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];

            //FASE 1: esiti della colposcopia (eseguita)
            //iteratore sugli esiti
            iter = ViewHelper.getRowSetIterator(colpes, "colpes_iter");

            //iteratore sulla validita'
            colpvl.setWhereClause("ULSS='" + AccessManager.CODREGIONALE + "' AND TPSCR='" + tpscr + "' AND GRUPPO='" +
                                  ConfigurationConstants.NOME_GRUPPO_COLPVL_2L +
                                  "' AND UPPER(DESCRIZIONE) LIKE 'NON ESEG%'");
            colpvl.setOrderByClause("ORDINE DESC");
            colpvl.executeQuery();

            iter2 = ViewHelper.getRowSetIterator(colpvl, "colpvl_iter");

            //numero totale di righe
            int j = iter.getRowCount() + iter2.getRowCount();
            //per ogni classe di diagnosi (una riga dell amatrice)
            while (iter.hasNext()) {

                int rowTotal = 0;
                Row inv = iter.next();
                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getAttribute("Idcnfref2l"));
                    r.setAttribute(1, inv.getAttribute("Descrizione"));
                }
                //contatore per le colonne
                int i = 2;


                int x = 0;
                HashMap result = null;

                for (i = 2; i < ages.size() + 2; i++) {


                    //ottengo i confini della classe d'eta' (cioe' le colonne)
                    BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);

                    //se sto cercando le persone e non sono nell ariga desiderata continuo

                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    //l'intestazione dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        headers[i] = "<" + (borders[1].intValue() + 1);
                    //ultima riga
                    else if (borders[1] == null)
                        headers[i] = borders[0].intValue() + "+";
                    else //righe intermedie
                        headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                    //questa colonna va mostrata
                    render[i] = true;

                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callCount2livNewFunction(null, ConfigurationConstants.NOME_GRUPPO_COLPES_2L,
                                                    new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()), //id configurzione
                                                    null, //colpo val
                                                    ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                    round_ind_a, //round ind a
                                                    round_org, //round org
                                                    centro, //centro
                                                    zona, //zona
                                                    borders[0], //eta da
                                                    borders[1], //eta a
                                                    comune, //comune
                                                    volontari, coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                    /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                    round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                    /*20082507 FINE MOD*/
                                                    );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Classe d'et\u00e0: " + headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    /*    totali[i]+=x;
                       //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                         rowTotal+=x;*/


                }

                //totale di riga

                //memorizzo la label
                headers[i] = "Tutti";
                //questa colonna va mostrata
                render[i] = true;


                result =
                    am.callCount2livNewFunction(null, ConfigurationConstants.NOME_GRUPPO_COLPES_2L,
                                                new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()), //id configurzione
                                                null, //colpo val
                                                ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                round_ind_a, //round ind a
                                                round_org, //round org
                                                centro, //centro
                                                zona, //zona
                                                null, //eta da
                                                null, //eta a
                                                comune, //comune
                                                volontari, coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                /*20082507 FINE MOD*/
                                                );
                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
              if(row<0 || col<0){
                   r.setAttribute(i,new Integer(x));
                   totali[i]+=x;
               }
                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(i, new Integer(x));
                    totali[i] += x;

                }

                /*20080725 fine mod*/


                else if (row >= 0 && col >= 0 && row == j && col == i - 2) {


                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                          "Classe d'et\u00e0: " + headers[col + 2]);

                    return;

                }


            }


            //FASE 2: ESITI DI COLPOSCOPIE ESEGUITE
            //memorizzo il numero dell eprime righe


            //per ogni classe di diagnosi (una riga dell amatrice)
            while (iter2.hasNext()) {

                int rowTotal = 0;
                Row inv = iter2.next();
                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getAttribute("Idcnfref2l"));
                    r.setAttribute(1, inv.getAttribute("Descrizione"));
                }
                //contatore per le colonne
                int i = 2;


                int x = 0;
                HashMap result = null;

                for (i = 2; i < ages.size() + 2; i++) {


                    //ottengo i confini della classe d'eta' (cioe' le colonne)
                    BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);

                    //se sto cercando le persone e non sono nell ariga desiderata continuo

                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    //l'intestazione dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        headers[i] = "<" + (borders[1].intValue() + 1);
                    //ultima riga
                    else if (borders[1] == null)
                        headers[i] = borders[0].intValue() + "+";
                    else //righe intermedie
                        headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                    //questa colonna va mostrata
                    render[i] = true;

                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callCount2livNewFunction(null, null, //GRUPPO CNF
                                                         null,
                                                    //id configurzione
                                                         new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()), //colpo val
                            ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                            round_ind_a, //round ind a
                            round_org, //round org
                            centro, //centro
                            zona, //zona
                            borders[0], //eta da
                            borders[1], //eta a
                            comune, //comune
                            volontari, coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                            /*20082507 FINE MOD*/
                            );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Classe d'et\u00e0: " + headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    /*     totali[i]+=x;
                       //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                         rowTotal+=x;*/


                }

                //totale di riga

                //memorizzo la label
                headers[i] = "Tutti";
                //questa colonna va mostrata
                render[i] = true;

                result =
                    am.callCount2livNewFunction(null, null, //GRUPPO CNF
                                                     null,
                                                //id configurzione
                                                     new BigDecimal(((Number) inv.getAttribute("Idcnfref2l")).doubleValue()), //colpo val
                        ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                        round_ind_a, //round ind a
                        round_org, //round org
                        centro, //centro
                        zona, //zona
                        null, //eta da
                        null, //eta a
                        comune, //comune
                        volontari, coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                        /*20082507 MOD: aggiunta round inviti ai filtri*/
                        round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                        /*20082507 FINE MOD*/
                        );

                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));

                /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
              if(row<0 || col<0){
                   r.setAttribute(i,new Integer(x));
                   totali[i]+=x;
               }

                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(i, new Integer(x));
                    totali[i] += x;

                }

                /*20080725 fine mod*/


                else if (row >= 0 && col >= 0 && row == j && col == i - 2) {


                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                          "Classe d'et\u00e0: " + headers[col + 2]);

                    return;

                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter2 != null)
                iter2.closeRowSetIterator();
            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }
            /*    if(ages!=null)
        ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(ages.size()));
        */
        }
    }


    public static void getInvitiEsiti(int row, int col,String tpscr, String ulss, int livello,
                                      Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                      BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                      BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                      boolean esteso, String sesso,
                                      /*20080704 MOD: aggiunta round inviti ai filtri*/
                                      BigDecimal round_inviti
                                      /*20080704 FINE MOD*/
                                      )

    throws Exception {
        if (coorti) {
            invitiEsitiCoorti(row, col, tpscr, ulss, dal,al_escluso, centro, zona, comune, round_ind_da,
                              round_ind_a, round_org, volontari, eta_da, eta_a, sesso,
                              /*20080704 MOD: aggiunta round inviti ai filtri*/
                              round_inviti /*20080704 FINE MOD*/
                              );
        } else {
            //volume attivita'
            if (esteso) {
                //forma estesa
                invitiEsiti(row, col, tpscr, ulss, livello, dal,al_escluso, centro, zona, comune, round_ind_da,
                            round_ind_a, round_org, volontari, eta_da, eta_a, sesso,
                            /*20080704 MOD: aggiunta round inviti ai filtri*/
                            round_inviti
                            /*20080704 FINE MOD*/
                            );
            } else {
                //forma aggreagata
                catInvitiEsiti(row, col, tpscr, ulss, livello, dal,al_escluso, centro, zona, comune, round_ind_da,
                               round_ind_a, round_org, volontari, eta_da, eta_a, sesso,
                               /*20080704 MOD: aggiunta round inviti ai filtri*/
                               round_inviti
                               /*20080704 FINE MOD*/
                               );
            }

        }
    }

    private static void invitiEsiti(int row, int col,String tpscr, String ulss, int livello,
                                    Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                    BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                    BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, String sesso,
                                    /*20080707 MOD: aggiunta round inviti ai filtri*/
                                    BigDecimal round_inviti
                                    /*20080707 FINE MOD*/
                                    ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject inviti = am.findViewObject("Stats_SoCnfTpinvitoView1");
        ViewObject esiti = am.findViewObject("Stats_SoCnfEsitoinvitoView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;
        RowSetIterator iter1 = null;
        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;


        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);

            String where =
                "A_SoCnfTpinvito.TPSCR='" + tpscr + "' AND A_SoCnfTpinvito.ULSS='" + AccessManager.CODREGIONALE +
                "' AND A_SoCnfTpinvito.LIVELLO=" + livello;
            //se l'accesso e' tutti va bene così
            //se e' invitati, quindi non volontari, escluso la categoria volontari
            if (volontari != null && volontari.intValue() == 0)
                where += " AND A_SoCnfTpinvito.IDCATEG<>" + ConfigurationConstants.CODICE_CAT_VOLONTARIO;
            //se e' spontanei prendo solo la cetgoria volontari
            else if (volontari != null && volontari.intValue() == 1)
                where += " AND A_SoCnfTpinvito.IDCATEG=" + ConfigurationConstants.CODICE_CAT_VOLONTARIO;
            inviti.setWhereClause(where);
            inviti.executeQuery();
            esiti.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + AccessManager.CODREGIONALE + "' AND LIVESITO=" +
                                 livello);
            esiti.executeQuery();
            iter = ViewHelper.getRowSetIterator(inviti, "inviti_iter");

            //UNA COLONNA PER ESITO, UN APER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[esiti.getRowCount() + 3];
            headers[0] = "Codice tipo di invito";
            headers[1] = "Tipo di invito";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];

            int j = iter.getRowCount();

            //per ogni tipo di invito (una riga dell amatrice)
            while (iter.hasNext()) {

                int rowTotal = 0;
                Stats_SoCnfTpinvitoViewRow inv = (Stats_SoCnfTpinvitoViewRow) iter.next();

                j--;
                int x = -1;
                //non e' la riga che sto ceracndo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                //inserisco una riga solo se non sto cercando persone
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getIdtpinvito());
                    r.setAttribute(1, inv.getDescrizione());
                }

                //contatore per le colonne
                int i = 3;


                iter1 = ViewHelper.getRowSetIterator(esiti, "esiti_iter");
                //per ogni tipo di esito valorizzo una colonna
                while (iter1.hasNext()) {

                    Stats_SoCnfEsitoinvitoViewRow e = (Stats_SoCnfEsitoinvitoViewRow) iter1.next();

                    //non e' la colonna che sto cercando
                    if (row >= 0 && col >= 0 && (row != j || col != i - 2)) {
                        i++;
                        continue;
                    }

                    //memorizzo la label
                    headers[i] = e.getEsitoinvito();
                    //questa colonna va mostrata
                    render[i] = true;


                    //calcolo del numero di record che soddisfano i criteri
                    HashMap result =
                        am.callInvitiEsitiNewFunction(null, //not tipo invito
                                                                   sesso, new BigDecimal(0), //solleciti
                                                                   livello, inv.getIdtpinvito(), null, //categoria di tipo di invito
                                                                   e.getCodesitoinvito(), ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                                   al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                                   round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                                   round_ind_a, //round ind a
                                                                   round_org, //round org
                                                                   centro, //centro
                                                                   zona, //zona
                                                                   eta_da, //eta da
                                                                   eta_a, //eta a
                                                                   comune, //comune
                                                                   volontari, //volontaria
                                                                   new BigDecimal(0), //coorti false
                                                                   /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                                   round_inviti,
                                                      (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                      /*20082507 FINE MOD*/
                                                      );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    /*    totali[i]+=x;
         //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
           rowTotal+=x;*/
                    i++;

                }


                iter1.closeRowSetIterator();


                //totale di riga

                //memorizzo la label
                headers[2] = "Totale inviti";
                //questa colonna va mostrata
                render[2] = true;

                HashMap result = am.callInvitiEsitiNewFunction(null, //not tipo invito
                                                               sesso, new BigDecimal(0), //solleciti
                                                               livello, inv.getIdtpinvito(), null, //categoria di tipo di invito
                                                               null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                               al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                               round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                               round_ind_a, //round ind a
                                                               round_org, //round org
                                                               centro, //centro
                                                               zona, //zona
                                                               eta_da, //eta da
                                                               eta_a, //eta a
                                                               comune, //comune
                                                               volontari, //volontaria
                                                               new BigDecimal(0), //coorti false
                                                               /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                               round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                               /*20082507 FINE MOD*/
                                                               );
                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
              if(row<0 || col<0){
               r.setAttribute(2,new Integer(x));
               totali[2]+=x;
         }

                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(2, new Integer(x));
                    totali[2] += x;
                    rowTotal += x;
                }

                /*20080725 fine mod*/


                else if (row >= 0 && col >= 0 && row == j && col == 0) {


                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                    return;

                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter1 != null)
                iter1.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);


            }
            //  ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(0));
        }
    }


    private static void catInvitiEsiti(int row, int col,String tpscr, String ulss, int livello,
                                       Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                       BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                       BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, String sesso,
                                       /*20080707 MOD: aggiunta round inviti ai filtri*/
                                       BigDecimal round_inviti
                                       /*20080707 FINE MOD*/
                                       ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject inviti = am.findViewObject("Stats_SoCnfCategTpinvitoView1");
        ViewObject esiti = am.findViewObject("Stats_SoCnfEsitoinvitoView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;
        RowSetIterator iter1 = null;
        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;


        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            String where = "TPSCR='" + tpscr + "' AND ULSS='" + AccessManager.CODREGIONALE + "' AND LIVELLO=" + livello;
            //se l'accesso e' tutti va bene così
            //se e' invitati, quindi non volontari, escluso la categoria volontari
            if (volontari != null && volontari.intValue() == 0)
                where += " AND IDCATEG<>" + ConfigurationConstants.CODICE_CAT_VOLONTARIO;
            //se e' spontanei prendo solo la cetgoria volontari
            else if (volontari != null && volontari.intValue() == 1)
                where += " AND IDCATEG=" + ConfigurationConstants.CODICE_CAT_VOLONTARIO;
            inviti.setWhereClause(where);
            inviti.executeQuery();
            esiti.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + AccessManager.CODREGIONALE + "' AND LIVESITO=" +
                                 livello);
            esiti.executeQuery();
            iter = ViewHelper.getRowSetIterator(inviti, "inviti_iter");

            //UNA COLONNA PER ESITO, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[esiti.getRowCount() + 3];
            headers[0] = "Codice";
            headers[1] = "Categoria di invito";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];

            int j = iter.getRowCount();
            //per ogni tipo di invito (una riga dell amatrice)
            while (iter.hasNext()) {

                int rowTotal = 0;
                Stats_SoCnfCategTpinvitoViewRow inv = (Stats_SoCnfCategTpinvitoViewRow) iter.next();

                j--;
                //non e' la riga che sto ceracndo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                //inserisco una riga solo se non sto cercando persone
                if (row < 0 || col < 0) {

                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getIdcateg());
                    r.setAttribute(1, inv.getDescrizione());
                }
                //contatore per le colonne
                int i = 3;


                iter1 = ViewHelper.getRowSetIterator(esiti, "esiti_iter");
                //per ogni tipo di esito valorizzo una colonna
                while (iter1.hasNext()) {

                    Stats_SoCnfEsitoinvitoViewRow e = (Stats_SoCnfEsitoinvitoViewRow) iter1.next();

                    //non e' la colonna che sto cercando
                    if (row >= 0 && col >= 0 && (row != j || col != i - 2)) {
                        i++;
                        continue;
                    }

                    //memorizzo la label
                    headers[i] = e.getEsitoinvito();
                    //questa colonna va mostrata
                    render[i] = true;


                    //calcolo del numero di record che soddisfano i criteri
                    HashMap result =
                        am.callInvitiEsitiNewFunction(null, //not tipo invito
                                                                   sesso, new BigDecimal(0), //solleciti
                                                                   livello, null, //tipo di invito
                                                                   new BigDecimal(inv.getIdcateg().doubleValue()), //categoria di tipo di invito
                                                                   e.getCodesitoinvito(), ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                                   al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                                   round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                                   round_ind_a, //round ind a
                                                                   round_org, //round org
                                                                   centro, //centro
                                                                   zona, //zona
                                                                   eta_da, //eta da
                                                                   eta_a, //eta a
                                                                   comune, //comune
                                                                   volontari, //volontaria
                                                                   new BigDecimal(0), //coorti false
                                                                   /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                                   round_inviti,
                                                      (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                      /*20082507 FINE MOD*/
                                                      );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 mod
            int x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        int x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                        return;

                    }

                    //incremento il totale
                    /*  totali[i]+=x;
         //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
           rowTotal+=x;*/
                    i++;

                }


                iter1.closeRowSetIterator();


                //totale di riga

                //memorizzo la label
                headers[2] = "Totale inviti";
                //questa colonna va mostrata
                render[2] = true;

                HashMap result = am.callInvitiEsitiNewFunction(null, //not tipo invito
                                                               sesso, new BigDecimal(0), //solleciti
                                                               livello, null, //tipo di invito
                                                               new BigDecimal(inv.getIdcateg().doubleValue()), //categoria di tipo di invito
                                                               null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                               al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                               round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                               round_ind_a, //round ind a
                                                               round_org, //round org
                                                               centro, //centro
                                                               zona, //zona
                                                               eta_da, //eta da
                                                               eta_a, //eta a
                                                               comune, //comune
                                                               volontari, //volontaria
                                                               new BigDecimal(0), //coorti false
                                                               /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                               round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                               /*20082507 FINE MOD*/
                                                               );
                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 mod
           int  x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0){
                r.setAttribute(2,new Integer(x));
               totali[2]+=x;
         }

                             */
                if (row < 0 || col < 0) {
                    int x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(2, new Integer(x));
                    totali[2] += x;

                }

                /*20080725 fine mod*/


                else if (row >= 0 && col >= 0 && row == j && col == 0) {


                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                    return;

                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter1 != null)
                iter1.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);


            }
            // ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(0));
        }
    }


    private static void invitiEsitiCoorti(int row, int col,String tpscr, String ulss,
                                          Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona,
                                          String comune, BigDecimal round_ind_da, BigDecimal round_ind_a,
                                          BigDecimal round_org, BigDecimal volontari, BigDecimal eta_da,
                                          BigDecimal eta_a, String sesso,
                                          /*20080704 MOD: aggiunta round inviti ai filtri*/
                                          BigDecimal round_inviti
                                          /*20080704 FINE MOD*/
                                          )

    throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject esiti = am.findViewObject("Stats_SoCnfEsitoinvitoView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter1 = null;
        //  RowSetIterator iter2=null;
        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        //per il calcolo dell'adesione:
        double aderenti = 0;
        int invitati = 0;
        int inesitati = 0;
        int correzione = 0;


        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone

            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);

            //filtro gli esiti
            esiti.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + AccessManager.CODREGIONALE + "'  AND LIVESITO=1");
            esiti.executeQuery();

            //CREO L'ARRAY CON LE DEFINIZIONE DELLE RIGHE
            String[][] inviti = null;

            if (!"CO".equals(tpscr)) {
                //0= tipo invito
                //1=descrizione
                //2=categoria
                //3= not tipo invito
                inviti = new String[2][4];
                //richiamo
                inviti[0][0] = ConfigurationConstants.CODICE_INVITO_ABOH;
                inviti[0][1] =
                    (String) ViewHelper.decodeByTpscr(tpscr, "Richiamo dopo esito A-B-O-H", null,
                                                      "Richiamo dopo esito A - O - H/escl temporanea", null, null);
                inviti[0][2] = null;
                inviti[0][3] = null;
                //primo invito
                inviti[1][0] = ConfigurationConstants.CODICE_PRIMO_INVITO;
                inviti[1][1] = "Primo invito";
                inviti[1][2] = null;
                inviti[1][3] = null;
            } else //if("CO".equals(tpscr))
            {
                //0= tipo invito
                //1=descrizione
                //2=categoria
                //3= not tipo invito
                inviti = new String[3][4];
                //richiamo
                inviti[0][0] = ConfigurationConstants.CODICE_INVITO_ABOH;
                inviti[0][1] = "Richiamo dopo esclusione temporanea";
                inviti[0][2] = null;
                inviti[0][3] = null;
                //FOBT a 5 anni
                inviti[1][0] = null;
                inviti[1][1] = "FOBT a 5 anni";
                inviti[1][2] = ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.toString();
                inviti[1][3] = ConfigurationConstants.CODICE_PRIMO_INVITO;
                //primo invito
                inviti[2][0] = ConfigurationConstants.CODICE_PRIMO_INVITO;
                inviti[2][1] = "Primo invito";
                inviti[2][2] = null;
                inviti[2][3] = null;
                /*  ViewObject inviti_vo=am.findViewObject("Stats_SoCnfTpinvitoView1");
        String where="A_SoCnfTpinvito.TPSCR='"+tpscr+
       "' AND A_SoCnfTpinvito.ULSS='"+AccessManager.CODREGIONALE+
       "' AND A_SoCnfTpinvito.IDCATEG="+ConfigurationConstants.CODICE_CAT_PRIMO_INVITO;
        inviti_vo.setWhereClause(where);
        inviti_vo.executeQuery();
        iter2=ViewHelper.getRowSetIterator(inviti_vo,"tipi_invito_co");
        inviti=new String[iter2.getRowCount()+1][2];
        inviti[0][0]=ConfigurationConstants.CODICE_INVITO_ABOH;
        inviti[0][1]="Richiamo dopo esclusione temporanea";
        int g=1;
        while(iter2.hasNext())
        {
          Stats_SoCnfTpinvitoViewRow r2=(Stats_SoCnfTpinvitoViewRow)iter2.next();
          inviti[g][0]=r2.getIdtpinvito();
          inviti[g][1]=r2.getDescrizione();
          g++;
        }*/

            }
            //


            //UNA COLONNA PER ESITO, UN APER I TOTALI, DUE COME LABL DI RIGA e due per i soleeciit
            headers = new String[esiti.getRowCount() + 3 + 2];
            headers[0] = "Codice tipo di invito";
            headers[1] = "Tipo di invito";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];

            int j = inviti.length;
            //ciclo sulle righe
            for (int k = 0; k < inviti.length; k++) {
                int rowTotal = 0;

                j--;
                //non e' la riga che sto cercando
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                //inserisco una riga solo se non sto cercando persone

                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    if (inviti[k][0] == null)
                        r.setAttribute(0, "-1");
                    else
                        r.setAttribute(0, inviti[k][0]);

                    r.setAttribute(1, inviti[k][1]);
                }

                //contatore per le colonne
                int i = 3;


                iter1 = ViewHelper.getRowSetIterator(esiti, "esiti_iter");
                //per ogni tipo di esito valorizzo una colonna
                while (iter1.hasNext()) {

                    Stats_SoCnfEsitoinvitoViewRow e = (Stats_SoCnfEsitoinvitoViewRow) iter1.next();

                    //non e' la colonna che sto cercando
                    if (row >= 0 && col >= 0 && (row != j || col != i - 2)) {
                        i++;
                        continue;
                    }

                    //memorizzo la label
                    headers[i] = e.getEsitoinvito();
                    //questa colonna va mostrata
                    render[i] = true;


                    //calcolo del numero di record che soddisfano i criteri
                    HashMap result =
                        am.callInvitiEsitiNewFunction(inviti[k][3], //not tpinvito
                                                                   sesso, new BigDecimal(0), //solleciti
                                                                   1, //livello
                                                                   inviti[k][0],
                                                      //tipo di invito
                                                                   inviti[k][2] == null ? null :
                                                                   new BigDecimal(inviti[k][2]), //categoria di tipo di invito
                                                      e.getCodesitoinvito(), ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                      al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                      round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                      round_ind_a, //round ind a
                                                      round_org, //round org
                                                      centro, //centro
                                                      zona, //zona
                                                      eta_da, //eta da
                                                      eta_a, //eta a
                                                      comune, //comune
                                                      volontari, //volontaria
                                                      new BigDecimal(1), //coorti true
                                                      /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                      round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                      /*20082507 FINE MOD*/
                                                      );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
            int  x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
                   //se si tratta di adsioni, le registro
           if(ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO.equals(e.getCodesitoinvito()))
              aderenti+=x;
           //se si tratta di esami documentati li registro
           if(
           (!"CO".equals(tpscr) &&(
           ConfigurationConstants.CODICE_ESITO_ESAME_RECENTE_DOC.equals(e.getCodesitoinvito())
             || ConfigurationConstants.CODICE_ESITO_ESAME_RECENTE_NON_DOC.equals(e.getCodesitoinvito()))
             )
             ||
             ("CO".equals(tpscr) &&(
           ConfigurationConstants.CODICE_ESITO_COLONSCOPIA_RECENTE_DOC.equals(e.getCodesitoinvito())
             || ConfigurationConstants.CODICE_ESITO_FOBT_DOC_CO.equals(e.getCodesitoinvito()))
             )

             )
              correzione+=x;

           //se si tratta di esami inesitati li registro:
           //ESITO W (indirizzo sbagliato) E LIVELLO 1
           if(ConfigurationConstants.CODICE_ESITO_IND_SBAGLIATO_1.equals(e.getCodesitoinvito()))
              inesitati+=x;
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        int x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        //se si tratta di adsioni, le registro
                        if (ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO.equals(e.getCodesitoinvito()))
                            aderenti += x;
                        //se si tratta di esami documentati li registro
                        if ((!"CO".equals(tpscr) &&
                             (ConfigurationConstants.CODICE_ESITO_ESAME_RECENTE_DOC.equals(e.getCodesitoinvito()) ||
                              ConfigurationConstants.CODICE_ESITO_ESAME_RECENTE_NON_DOC.equals(e.getCodesitoinvito()))) ||
                            ("CO".equals(tpscr) &&
                             (ConfigurationConstants.CODICE_ESITO_COLONSCOPIA_RECENTE_DOC.equals(e.getCodesitoinvito()) ||
                              ConfigurationConstants.CODICE_ESITO_FOBT_DOC_CO.equals(e.getCodesitoinvito()))))


                            correzione += x;

                        //se si tratta di esami inesitati li registro:
                        //ESITO W (indirizzo sbagliato) E LIVELLO 1
                        if (ConfigurationConstants.CODICE_ESITO_IND_SBAGLIATO_1.equals(e.getCodesitoinvito()))
                            inesitati += x;

                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    /*  totali[i]+=x;
          // System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
           rowTotal+=x;*/
                    i++;


                }


                iter1.closeRowSetIterator();


                //totale di riga

                //memorizzo la label
                headers[2] = "Totale invitati";
                //questa colonna va mostrata
                render[2] = true;
                int x = -1;

                //calcolo del numero di record che soddisfano i criteri
                HashMap result =
                    am.callInvitiEsitiNewFunction(inviti[k][3], //not tpinvito
                                                               sesso, new BigDecimal(0), //solleciti
                                                               1, //livello
                                                               inviti[k][0],
                                                  //tipo di invito
                                                               inviti[k][2] == null ? null :
                                                               new BigDecimal(inviti[k][2]), //categoria di tipo di invito
                                                  null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                  al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                  round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                  round_ind_a, //round ind a
                                                  round_org, //round org
                                                  centro, //centro
                                                  zona, //zona
                                                  eta_da, //eta da
                                                  eta_a, //eta a
                                                  comune, //comune
                                                  volontari, //volontaria
                                                  new BigDecimal(1), //coorti true
                                                  /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                  round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                  /*20082507 FINE MOD*/
                                                  );
                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 mod
            int x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
              if(row<0 || col<0)
       {
               r.setAttribute(2,new Integer(x));
               totali[2]+=x;
           }

                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(2, new Integer(x));
                    totali[2] += x;
                }

                /*20080725 fine mod*/

                else if (row >= 0 && col >= 0 && col == 0 && row == j) {


                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                    return;

                }


                ///////////////////////////////////////////
                //colonna totale dei solleciti
                //se e' la colonna che sto cercando
                if (row < 0 || col < 0 || (row == j && col == i - 2)) {
                    //memorizzo la label
                    headers[i] = "Totale solleciti*";
                    //questa colonna va mostrata
                    render[i] = true;


                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callInvitiEsitiNewFunction(inviti[k][3], //not tpinvito
                                                           sesso, new BigDecimal(1), //solleciti
                                                           1, //livello
                                                           inviti[k][0], //tipo di invito
                                                           inviti[k][2] == null ? null : new BigDecimal(inviti[k][2]), //categoria di tipo di invito
                                                           null, //esito
                                                           ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                           al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                           round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                           round_ind_a, //round ind a
                                                           round_org, //round org
                                                           centro, //centro
                                                           zona, //zona
                                                           eta_da, //eta da
                                                           eta_a, //eta a
                                                           comune, //comune
                                                           volontari, //volontaria
                                                           new BigDecimal(1), //coorti true
                                                           /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                           round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                           /*20082507 FINE MOD*/
                                                           );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }


                }
                //    totali[i]+=x;
                i++;


                ////////
                //colonna adsioni ai solleciti
                //se e' la colonna che sto cercando
                if (row < 0 || col < 0 || (row == j && col == i - 2)) {
                    //memorizzo la label
                    headers[i] = "Adesioni ai solleciti**";
                    //questa colonna va mostrata
                    render[i] = true;


                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callInvitiEsitiNewFunction(inviti[k][3], //not tpinvito
                                                           sesso, new BigDecimal(1), //solleciti
                                                           1, //livello
                                                           inviti[k][0], //tipo di invito
                                                           inviti[k][2] == null ? null : new BigDecimal(inviti[k][2]), //categoria di tipo di invito
                                                           ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO, //esito
                                                           ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal), //dal
                                                           al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), //al_escluso
                                                           round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                           round_ind_a, //round ind a
                                                           round_org, //round org
                                                           centro, //centro
                                                           zona, //zona
                                                           eta_da, //eta da
                                                           eta_a, //eta a
                                                           comune, //comune
                                                           volontari, //volontaria
                                                           new BigDecimal(1), //coorti true
                                                           /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                           round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                           /*20082507 FINE MOD*/
                                                           );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                }
                // totali[i]+=x;
                i++;


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {

            if (iter1 != null)
                iter1.closeRowSetIterator();
            /*  if(iter2!=null)
         iter2.closeRowSetIterator();*/

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

                //calcolo dell'adesione e dell'adesione corretta
                invitati = totali[2];
                double ad_grezza = Stats_helper.round((aderenti * 100) / (invitati - inesitati), 1);
                double ad_corretta = Stats_helper.round((aderenti * 100) / (invitati - inesitati - correzione), 1);
                String msg =
                    "<ul><li> Adesione grezza=" + ad_grezza + "%</li>" + "<li>Adesione corretta=" + ad_corretta +
                    "%</li></ul>";


                ADFContext.getCurrent().getSessionScope().put("stats_notes", msg);

            }
            //   ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(0));
        }
    }


    public static void suggerimenti(int row, int col,String tpscr, String ulss, int livello,
                                    Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                    BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                    BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean esteso,
                                    boolean coorti, String sesso,
                                    /*20080707 MOD: aggiunta round inviti ai filtri*/
                                    BigDecimal round_inviti
                                    /*20080707 FINE MOD*/
                                    )

    throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Stats_SoStatsAggregazView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");


        RowSetIterator iter = null;
        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        try {
            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);

            //leggo i nomi delle firme da cercare
            String[] gruppi = Stats_helper.STATS_GRUPPI[livello - 1];
            //forma estesa o raggruppata
            String gruppo = esteso ? gruppi[1] : gruppi[0];


            //ottengo igli item del raggruppamento scelto
            vo.setWhereClause("TPSCR='" + tpscr + "' AND TIPO='" + gruppo + "'");
            vo.executeQuery();
            //leggo le righe
            iter = ViewHelper.getRowSetIterator(vo, "groups_iter");
            //LEGGO LE COLONNE
            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);

            //UNA COLONNA PER classe, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = "Raccomandazione di " + livello + " livello";
            //   headers[2]="Numero";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            //   render[2]=true;
            totali = new int[headers.length];
            //   totali[2]=0;
            //per ogni tipo di invito (una riga dell amatrice)
            int j = iter.getRowCount();
            while (iter.hasNext()) {
                int rowTotal = 0;
                Stats_SoStatsAggregazViewRow rr = (Stats_SoStatsAggregazViewRow) iter.next();
                j--;
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, rr.getAggregaz());
                    r.setAttribute(1, rr.getDescrizione());
                }

                int x = 0;
                HashMap result = null;

                //imposto i parametri in base a forma estsa/aggregata
                String forma_agg = null;
                String forma_est = null;
                String agg = null;
                String est = null;
                if (esteso) {
                    forma_est = gruppo;
                    est = rr.getAggregaz();
                } else {
                    forma_agg = gruppo;
                    agg = rr.getAggregaz();
                }

                //contatore per le colonne
                int i = 2;

                for (i = 2; i < ages.size() + 2; i++) {
                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    //ottengo i confini della classe d'eta' (cioe' le colonne)
                    BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);


                    //l'intestazione dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        headers[i] = "<" + (borders[1].intValue() + 1);
                    //ultima riga
                    else if (borders[1] == null)
                        headers[i] = borders[0].intValue() + "+";
                    else //righe intermedie
                        headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                    //questa colonna va mostrata
                    render[i] = true;


                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callSuggerimentiFunction(sesso, livello, null, //sugg
                                                         forma_agg, forma_est, agg, est, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso),
                                                    round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                    round_ind_a, //round ind a
                                                    round_org, //round org
                                                    centro, //centro
                                                    zona, //zona
                                                    borders[0], //eta da
                                                    borders[1], //eta a
                                                    comune, //comune
                                                    volontari, //volontaria
                                                    coorti ? new BigDecimal(1) : new BigDecimal(0), //COORTI
                                                    /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                    round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                    /*20082507 FINE MOD*/
                                                    );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        //  System.out.println(inClause);
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Classe d'et\u00e0: " + headers[col + 2]);

                        return;

                    }
                    //incremento il totale
                    /* totali[i]+=x;
              rowTotal+=x;*/


                }
                //totale di riga

                //memorizzo la label
                headers[i] = "Tutti";
                //questa colonna va mostrata
                render[i] = true;

                result =
                    am.callSuggerimentiFunction(sesso, livello, null, //sugg
                                                     forma_agg, forma_est, agg, est, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso),
                                                round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                round_ind_a, //round ind a
                                                round_org, //round org
                                                centro, //centro
                                                zona, //zona
                                                null, //eta da
                                                null, //eta a
                                                comune, //comune
                                                volontari, //volontaria
                                                coorti ? new BigDecimal(1) : new BigDecimal(0), //COORTI
                                                /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                /*20082507 FINE MOD*/
                                                );
                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
            if(row<0 || col<0){
               r.setAttribute(i,new Integer(x));
               totali[i]+=x;
           }
                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(i, new Integer(x));
                    totali[i] += x;

                }

                /*20080725 fine mod*/


                else if (row >= 0 && col >= 0 && row == j && col == i - 2) {


                    String inClause = (String) result.get("query");
                    //  System.out.println(inClause);
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                          "Classe d'et\u00e0: " + headers[col + 2]);

                    return;

                }

            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }
            /*  if(ages!=null)
        ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(ages.size()));
        */
        }
    }


    public static void tempi(int row, int col,String tpscr, String ulss, Date dal,
                             Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                             BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                             BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti, int durata_int,
                             int inizio_int, int fine_int,
                             /*20080707 MOD: aggiunta round inviti ai filtri*/
                             BigDecimal round_inviti
                             /*20080707 FINE MOD*/
                             ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 && col < 0)
                ViewHelper.clearVO(to_fill);


            Vector ages = Stats_helper.getAgeClasses(inizio_int, fine_int, durata_int);


            //UNA COLONNA PER ESITO, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = "Tempo tra";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            //  totali=new int[headers.length];

            Object[][] righe =
                (Object[][]) ViewHelper.decodeByTpscr(tpscr,
                                                      new Object[][] {
                                                                     //codice,label,positivi
                                                                     { "1", "test positivo e 2° livello",
                                                                       new BigDecimal(1) },
                                                                     { "0", "test negativo e comunicazione esito",
                                                                       new BigDecimal(0) } },
                                               new Object[][] {
                                               //codice,label,positivi
                                               { "1", "test positivo e 2° livello", new BigDecimal(1) },
                                               { "0", "test negativo e comunicazione esito", new BigDecimal(0) } },
                                               new Object[][] {
                                               //codice,label,positivi
                                               { "2", "test positivo ed intervento", new BigDecimal(2) },
                                               { "1", "test positivo e 2° livello", new BigDecimal(1) },
                                               { "0", "test negativo e comunicazione esito", new BigDecimal(0) } },
                                               null, null);

            //per ogni riga
            int k = righe.length;
            for (int j = 0; j < righe.length; j++) {
                k--;
                if (row >= 0 && col >= 0 && row != k)
                    continue;

                HashMap result = null;
                //per prima cosa eseguo il conteggio degli assoluti
                int totale = 0;
                if (row < 0 && col < 0) {
                    result =
                        am.callTempiFunction(null, null, (BigDecimal) righe[j][2], ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso),
                                             round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                             round_ind_a, //round ind a
                                             round_org, //round org
                                             centro, //centro
                                             zona, //zona
                                             eta_da, //eta da
                                             eta_a, //eta ata a
                                             comune, //comune
                                             volontari, //volontaria
                                             coorti ? new BigDecimal(1) : new BigDecimal(0), //COORTI
                                             /*20082507 MOD: aggiunta round inviti ai filtri*/
                                             round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                             /*20082507 FINE MOD*/
                                             );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    totale = ((Integer) result.get("howmany")).intValue();
                }


                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, righe[j][0]);
                    r.setAttribute(1, righe[j][1]);
                }

                int x = 0;


                //contatore per le colonne
                int i = 2;

                for (i = 2; i < ages.size() + 2; i++) {
                    if (row >= 0 && col >= 0 && (row != k || col != i - 2))
                        continue;

                    //ottengo i confini della classe d'eta' (cioe' le colonne)
                    BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);


                    //l'intestazione dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        headers[i] = "<" + (borders[1].intValue() + 1) + " gg";
                    //ultima riga
                    else if (borders[1] == null)
                        headers[i] = borders[0].intValue() + "+ gg";
                    else //righe intermedie
                        headers[i] = borders[0].intValue() + "-" + borders[1].intValue() + " gg";

                    //questa colonna va mostrata
                    render[i] = true;

                    result =
                        am.callTempiFunction(borders[0], borders[1], (BigDecimal) righe[j][2], ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),
                                             al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                             round_ind_a, //round ind a
                                             round_org, //round org
                                             centro, //centro
                                             zona, //zona
                                             eta_da, //eta da
                                             eta_a, //eta ata a
                                             comune, //comune
                                             volontari, //volontaria
                                             coorti ? new BigDecimal(1) : new BigDecimal(0), //COORTI
                                             /*20082507 MOD: aggiunta round inviti ai filtri*/
                                             round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                             /*20082507 FINE MOD*/
                                             );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                              r.setAttribute(i,Double.toString(Stats_helper.percentage(totale,x))+"% ("+x+"/"+totale+")");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i,
                                       Double.toString(Stats_helper.percentage(totale, x)) + "% (" + x + "/" + totale +
                                       ")");

                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        //  System.out.println(inClause);
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Distanza temporale: " +
                                                                              headers[col + 2]);

                        return;

                    }


                } //ciclio sulle colonne

            } //ciclo sulle righe


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);
                ADFContext.getCurrent().getSessionScope().remove("stats_inviti_esiti_totali_perc");
            }
        }
    }


    public static void periodismo(int row, int col,String tpscr, String ulss, Date dal,
                                  Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                  BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                  BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, int durata_int,
                                  int inizio_int, int fine_int, String sesso,
                                  /*20080708 MOD: aggiunta round inviti ai filtri*/
                                  BigDecimal round_inviti
                                  /*20080708 FINE MOD*/
                                  ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 && col < 0)
                ViewHelper.clearVO(to_fill);


            Vector ages = Stats_helper.getAgeClasses(inizio_int, fine_int, durata_int);


            //UNA COLONNA PER ESITO, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = "Periodismo inviti";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;


            HashMap result = null;
            //per prima cosa eseguo il conteggio degli assoluti
            int totale = 0;
            if (row < 0 && col < 0) {
                result =
                    am.callPeriodismoFunction(sesso, new BigDecimal(0), null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso),
                                              round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                              round_ind_a, //round ind a
                                              round_org, //round org
                                              centro, //centro
                                              zona, //zona
                                              eta_da, //eta da
                                              eta_a, //eta ata a
                                              comune, //comune
                                              volontari, //volontaria
                                              /*20082507 MOD: aggiunta round inviti ai filtri*/
                                              round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                              /*20082507 FINE MOD*/
                                              );

                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                totale = ((Integer) result.get("howmany")).intValue();
            }


            Row r = null;
            if (row < 0 || col < 0) {
                r = to_fill.createRow();
                to_fill.insertRow(r);
                //id e label della riga
                r.setAttribute(0, "1");
                r.setAttribute(1, "Soggetti invitati ai passaggi successivi entro");
            }

            int x = 0;


            //contatore per le colonne
            int i = 2;

            for (i = 2; i < ages.size() + 2; i++) {
                if (row >= 0 && col >= 0 && (row != 0 || col != i - 2))
                    continue;

                //ottengo i confini della classe d'eta' (cioe' le colonne)
                BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);


                //l'intestazione dipende dalla situazione
                //prima riga
                if (borders[0] == null)
                    headers[i] = "<" + (borders[1].intValue() + 1) + " mesi";
                //ultima riga
                else if (borders[1] == null)
                    headers[i] = borders[0].intValue() + "+ mesi";
                else //righe intermedie
                    headers[i] = borders[0].intValue() + "-" + borders[1].intValue() + " mesi";

                //questa colonna va mostrata
                render[i] = true;

                result =
                    am.callPeriodismoFunction(sesso, borders[0], borders[1], ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                              round_ind_a, //round ind a
                                              round_org, //round org
                                              centro, //centro
                                              zona, //zona
                                              eta_da, //eta da
                                              eta_a, //eta ata a
                                              comune, //comune
                                              volontari, //volontaria
                                              /*20082507 MOD: aggiunta round inviti ai filtri*/
                                              round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                              /*20082507 FINE MOD*/
                                              );

                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));
                /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                              r.setAttribute(i,Double.toString(Stats_helper.percentage(totale,x))+"% ("+x+"/"+totale+")");

                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(i,
                                   Double.toString(Stats_helper.percentage(totale, x)) + "% (" + x + "/" + totale +
                                   ")");

                }

                /*20080725 fine mod*/


                else {


                    String inClause = (String) result.get("query");
                    //  System.out.println(inClause);
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                          "Distanza temporale " + headers[col + 2]);

                    return;

                }


            } //ciclio sulle colonne
            
            //totali=new int[headers.length];

        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().remove("stats_inviti_esiti_totali");
                ADFContext.getCurrent().getSessionScope().remove("stats_inviti_esiti_totali_perc");
            }
        }
    }


    public static String[] createDynamicQuery() throws Exception {
        //  Stats_AppModule am=(Stats_AppModule)BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        Stats_dynamicFilter bean =
            (Stats_dynamicFilter) BindingContext.getCurrent().findDataControl("Stats_dynamicFilterDataControl").getDataProvider();
        Stats_paramBean bean2 =
            (Stats_paramBean) BindingContext.getCurrent().findDataControl("Stats_paramBeanDataControl").getDataProvider();

        //controllo subito che i filtri siano almneo su uno tra invitati ed esclusi
        if (!bean.isInvitati() && !bean.isEsclusi())
            throw new Exception("Selezionare almeno una delle opzioni invitati o esclusi");


        String note = "";
        //comincio a costruire la query
        String original_select = "select distinct c.codts, c.ulss from ";
        if (bean.isInvitati() && bean.isEsclusi()) {
            original_select += "so_stats_inviti_escl c ";
            note += "SOGGETTI INVITATI ED ESCLUSI";
        } else if (bean.isInvitati()) {
            original_select += "so_stats_coorti c ";
            note += "SOGGETTI INVITATI";
        } else if (bean.isEsclusi()) {
            original_select += "so_stats_esclusioni c ";
            note += "SOGGETTI ESCLUSI";
        }
        String select = original_select;
        String where = null;
        String original_where = null;
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");

        Boolean regionale = (Boolean) ADFContext.getCurrent().getSessionScope().get("regionale");

        //azienda sanitaria
        String ulss = null;
        //se si tratta di un utente regionale puo' scegliere la ulss o lavorare su tutte
        if (regionale.booleanValue()) {

            if (bean2.getUlss() == null || "-1".equals(bean2.getUlss()))
                throw new Exception("E' necessario selezionare un'azienda sanitaria");

            ulss = bean2.getUlss();
        } //altrimenti la ulss e' quella dell'utente
        else {
            ulss = (String) ADFContext.getCurrent().getSessionScope().get("ulss");
        }

        where = "where c.ULSS='" + ulss + "' and c.TPSCR='" + tpscr + "'";

        //SESSO
        if (bean2.getSesso() != null && !"-1".equals(bean2.getSesso()))
            where += "\nAND C.SESSO='" + bean2.getSesso() + "' ";
        /*MOD 20090623 */
        if ("CI".equals(tpscr) || "MA".equals(tpscr))
            where += "\nAND C.SESSO='F' ";
        /*20090623 fine mod*/

        //periodo temporale, controllo i formati
        try {
            if (bean.getPeriodo_dal() != null) {
                //gli inviti devono cadere dopo l'inizio del periodo
                if (bean.isInvitati())
                    where +=
                        "\n AND C.DTAPP>=TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getPeriodo_dal()) + "','" + DateUtils.DATE_PATTERN + "')";
                //le esclusioni si concludono dopo l'inizio del periodo
                if (bean.isEsclusi())
                    where +=
                        "\n AND (C.DTFINE IS NULL OR C.DTFINE>=TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getPeriodo_dal()) + "','" +
                        DateUtils.DATE_PATTERN + "'))";

                note += "\nPeriodo dal " + bean.getPeriodo_dal();
            }
        } catch (Throwable pex) {
            throw new Exception("La data di inizio del periodo temporale non rispetta il formato richiesto (" +
                                DateUtils.DATE_PATTERN + ")");
        }

        try {
            //l'invito deve cadere prima della fine del periodo
            if (bean.getPeriodo_al() != null ) {
               
                if (bean.isInvitati())
                    where += "\n AND C.DTAPP<TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getPeriodo_al()) + "','" + DateUtils.DATE_PATTERN + "')";
                //le esclusioni devono concludersi prima del periodo
                if (bean.isEsclusi())
                    where +=
                        "\n AND (C.DTFINE IS NULL OR C.DTFINE<=TO_DATE('" +new SimpleDateFormat("dd/MM/yyyy").format( bean.getPeriodo_al()) + "','" +
                        DateUtils.DATE_PATTERN + "'))";


                if (note.length() > 0)
                    note += " al (escluso) " + bean.getPeriodo_al();
                else
                    note += "Periodo fino al (escluso) " + bean.getPeriodo_al();
            }
        } catch (Throwable pex) {
            throw new Exception("La data di fine del periodo temporale non rispetta il formato richiesto (" +
                                DateUtils.DATE_PATTERN + ")");
        }

        //SE CONSIDERO SIA INVITATI CHE ESCLUSI, L'ESCLUSIONE DEVE CONCLUDERSI PRIMA DELL'INVITO
        if (bean.isEsclusi() && bean.isInvitati())
            where += "\n AND C.DTFINE<=C.DTAPP ";

        if (bean.getComune() != null && bean.getComune().trim().length() > 0) {
            where +=
                "\n AND ( (C.statoanag=" + ConfigurationConstants.CODICE_DOMICILIATO + " AND C.coddomicilio='" +
                bean.getComune() + "') OR (C.statoanag<>" + ConfigurationConstants.CODICE_DOMICILIATO +
                "AND C.codresidenza='" + bean.getComune() + "') )";
            note += "\nComune: " + bean.getComune_desc();
        }

        if (bean.getZona() != null) {
            where += "\n and c.zona=" + bean.getZona();
            note += "\nZona: " + bean.getZona_desc();
        }

        if (bean.getRound_comune() != null && bean.getRound_comune().trim().length() > 0) {
            where += "\n and c.roundcomune=" + bean.getRound_comune();
            note += "\nRound organizzativo: " + bean.getRound_comune();
        }

        if (bean2.getMedico() != null && bean2.getMedico().intValue() > 0) {
            where += "\n AND C.CODICEREGMEDICO=" + bean2.getMedico();
            note += "\nMedico di medicina generale: " + bean2.getMedico_desc() + " " + bean2.getMedico_desc_2();
        }


        if (bean.getEta_da() > 0) {
            where += "\n AND c.eta>=" + bean.getEta_da();
            note += "\nEta': da " + bean.getEta_da();
        }

        if (bean.getEta_a() > 0) {
            where += "\n AND c.eta<=" + bean.getEta_a();
            note += " a " + bean.getEta_a();
        }

        if (bean.isEsclusi()) {
            //tipo di esclusione

            if (bean.getTpescl() != null && !"-1".equals(bean.getTpescl())) {
                where += "\n AND c.tpescl='" + bean.getTpescl() + "'";
                if (bean.getTpescl().startsWith("D"))
                    note += "\nEsclusione: definitiva";
                else
                    note += "\nEsclusione: temporanea";
            }

        }


        if (bean.isInvitati()) {


            if (bean.getCentro_invito() != null) {
                where += "\n AND c.idcentroprelievo=" + bean.getCentro_invito();
                note += "\nCentro: " + bean.getCentro_invito_desc();
            }

            if (bean2.getCentro_ref() != null && bean2.getCentro_ref().intValue() > 0) {
                where += "\n AND c.idcentroref1liv=" + bean2.getCentro_ref();
                note += "\nCentro refertante: " + bean2.getCentro_ref_desc();
            }

            if (bean.getRound_indiv() != null && bean.getRound_indiv().trim().length() > 0) {
                if ("Primi esami".equals(bean.getRound_indiv()))
                    //per i primi esami il round al momento dell'invito era ancora 0
                    where += "\n and (c.roundindiv is null or c.roundindiv=0)";
                else if ("Esami successivi".equals(bean.getRound_indiv()))
                    where += "\n and c.roundindiv>0";

                note += "\nRound individuale: " + bean.getRound_indiv();

            }
            /*20080709 MOD: aggiunta round inviti ai filtri*/
            if (bean.getRound_inviti() != null && bean.getRound_inviti().trim().length() > 0) {
                where += "\n and c.roundinviti=" + bean.getRound_inviti();
                note += "\nRound inviti: " + bean.getRound_inviti();
            }
            /*20080709 FINE MOD*/

            /*20080730 MOD: aggiungi filtri CCR*/
            if ("CO".equals(tpscr) && bean.getAlto_rischio() != null && bean.getAlto_rischio().intValue() >= 0) {
                select += ", so_sogg_scr ss";
                //join con so_sogg_scr per l'alto rischio
                where +=
                    "\n and ss.codts=c.codts and ss.ulss=c.ulss and ss.tpscr=c.tpscr" + "\n and ss.altorischio=" +
                    bean.getAlto_rischio().intValue();
                note += "\nSolo soggetti ad alto rischio";
            }
            /*20080730 FINE MOD*/


            if (bean.getAccesso() != null && bean.getAccesso().trim().length() > 0) {
                if ("Spontanei".equals(bean.getAccesso()))
                    where += "\n and c.volontaria=1";
                //solo gli inviti non volontari
                else if ("Invitati".equals(bean.getAccesso()))
                    where += "\n and (c.volontaria is null or c.volontaria=0)";

                note += "\nAccesso: " + bean.getAccesso();

            }

            original_where = where;
            // note+="\nINVITO:";


            /*******  filtro 1 ****************/
            /*  if(bean.getFiltro_1()==1)
       {*/

            /*20080710 MOD: aggiunta categoria invito*/
            if (bean.getCat_invito() != null) {
                where += "\n and c.idcateg=" + bean.getCat_invito() + "";
                note += "\nCategoria di invito: " + bean.getCat_invito_desc();
            }
            /*20080710 FINE MOD*/

            if (bean.getTipo_invito() != null && bean.getTipo_invito().trim().length() > 0) {
                where += "\n and c.idtpinvito='" + bean.getTipo_invito() + "'";
                note += "\nTipo di invito: " + bean.getTipo_invito_desc();
            }


            if (bean.getEsito() != null && bean.getEsito().trim().length() > 0) {
                where += "\n and c.codesitoinvito='" + bean.getEsito() + "'";
                note += "\nEsito: " + bean.getEsito_desc();
            }

            if (bean.getTipo_richiamo() != null && bean.getTipo_richiamo().trim().length() > 0) {
                where += "\n and c.tprichiamo='" + bean.getTipo_richiamo() + "'";
                note += "\nTipo di richiamo: " + bean.getTipo_richiamo_desc();
            }


            if (bean.getLivello() > 0) {

                where += " and c.livello=" + bean.getLivello() + " and c.livesito=" + bean.getLivello();
                note += "\nLivello: " + bean.getLivello();

            }

            if (bean.getLivello() > 0 && bean.isReferto()) {

                if ("0".equals(bean.getCompleto())) {
                    //referti inseriti ma non completi
                    where += "\n and r.idreferto is not null and (r.completo=0 or r.completo is null)";
                    note += "\nReferto inserito ma NON completo";
                } else if ("1".equals(bean.getCompleto())) {
                    where += "\n and r.idreferto is not null and r.completo=1";
                    note += "\nReferto completo";
                } else {
                    where += "\n and r.idreferto is not null ";
                    note += "\nReferto presente";
                }


                //primo livello
                if (bean.getLivello() ==
                    1) {
                    //join esterno col ref di 1 livello
                    select +=
   ViewHelper.decodeByTpscr(tpscr, ", so_ref1liv_completo r", ", so_ref1livco_completo r", ", so_ref1livma_completo r",
                            null, null);
                    where += "\n and c.idinvito=r.idinvito";

                    //solo per il citologico
                    if ("CI".equals(tpscr)) {
                        if (bean.getAdepre() != null) {
                            where +=
                                "\n and r.adepre_id=" + bean.getAdepre() + " and r.adepre_gruppo='" +
                                ConfigurationConstants.NOME_GRUPPO_ADEPRE + "'";
                            note += "\nAdeguatezza: " + bean.getAdepre_desc();
                        }

                        if (bean.getGiudia() != null) {
                            where +=
                                "\n and r.giudia_id=" + bean.getGiudia() + " and r.giudia_gruppo='" +
                                ConfigurationConstants.NOME_GRUPPO_GIUDIA + "'";
                            note += "\nGiudizio diagnostico: " + bean.getGiudia_desc();
                        }

                        if (bean.getPositi() != null) {
                            where +=
                                "\n and r.cnf_id=" + bean.getPositi() + " and r.cnf_gruppo='" +
                                ConfigurationConstants.NOME_GRUPPO_POSITI + "'";
                            note += "\nTipo di positivita': " + bean.getPositi_desc();
                        }
                        /* mod 20081210 versione 1.2*/
                        if (bean.getInalim() != null) {
                            where +=
                                "\n and r.cnf_id=" + bean.getInalim() + " and r.cnf_gruppo='" +
                                ConfigurationConstants.NOME_GRUPPO_INALIM + "'";
                            note += "\nTipo di inadeguatezza: " + bean.getInalim_desc();
                        }

                        /* fine mod 20081210*/
                    }
                    //solo per il mammografico
                    else if ("MA".equals(tpscr)) {

                        if (bean.getMx_esito() != null) {
                            where += "\n and r.esito=" + bean.getMx_esito();
                            note += "\nEsito radiologico: " + bean.getMx_esito_desc();
                        }
                    }
                    /*20080710 MOD: aggiungi filtri CCR*/
                    else if ("CO".equals(tpscr)) {
                        if (bean.getQuantita() != null && bean.getQuantita().intValue() > 0) {
                            switch (bean.getQuantita().intValue()) {
                            case 1:
                                {
                                    where += "\n and r.quantita<100";
                                    note += "\nQuantita' <100 mg";
                                    break;
                                }
                            case 2:
                                {
                                    where += "\n and r.quantita>=100";
                                    note += "\nQuantita' >=100 mg";
                                    break;
                                }
                            case 3:
                                {
                                    where += "\n and r.quantita>=90 and r.quantita<=100";
                                    note += "\nQuantita' tra 90 e 100 mg";
                                    break;
                                }
                            case 4:
                                {
                                    where += "\n and r.quantita>=100 and r.quantita<=110";
                                    note += "\nQuantita' tra 100 e 110 mg";
                                    break;
                                }
                            case 5:
                                {
                                    where += "\n and r.quantita>=110";
                                    note += "\nQuantita' >=110 mg";
                                    break;
                                }
                            default:
                                break;
                            }

                        }
                    }
                    /*20080710 FINE MOD*/


                    if (bean.getIdsugg1l() != null) {
                        where += "\n and r.idsugg=" + bean.getIdsugg1l();
                        note += "\nSuggerimento conclusivo: " + bean.getIdsugg1l_desc();
                    }


                } //fine 1 livello
                //seocndo livello
                else if (bean.getLivello() ==
                         2) {
                    //join esterno col ref di 2 livello
                    select +=
   ViewHelper.decodeByTpscr(tpscr, ", so_ref2liv_completo r", ", so_ref2livco_completo r", ", so_ref2livma_completo r",
                            null, null);

                    where += "\n and c.idinvito=r.idinvito";

                    //solo per il citlogico
                    if ("CI".equals(tpscr)) {
                        if (bean.getColpvl() != null) {
                            where += "\n and r.colpovl=" + bean.getColpvl();
                            note += "\nColposcopia: " + bean.getColpvl_desc();
                        }

                        if (bean.getColpes() != null) {
                            where +=
                                "\n and r.codreg=" + bean.getColpes() + " and r.gruppo='" +
                                ConfigurationConstants.NOME_GRUPPO_COLPES_2L + "'";
                            note += "\nEsito della colposcopia: " + bean.getColpes_desc();
                        }

                        if (bean.getRacdia() !=
                            null) {
                            //aggiungo un join per la diagnosi peggiore
                            select +=
            ", \n" + "(SELECT SO_CODREF2LIVC2.ID, " + "SO_CODREF2LIVC2.gruppo, " + "SO_CODREF2LIVC2.idreferto, " +
            " SO_CNF_REF2LIV.CODREG, " + " SO_CNF_REF2LIV.DESCRIZIONE, " + "SO_CNF_REF2LIV.ORDINE " +
            "FROM SO_CODREF2LIVC2, " + "SO_CNF_REF2LIV " + "WHERE SO_CODREF2LIVC2.IDCNFREF=SO_CNF_REF2LIV.IDCNFREF2L " +
            "AND SO_CODREF2LIVC2.GRUPPO=SO_CNF_REF2LIV.GRUPPO " + "AND SO_CODREF2LIVC2.ULSS=SO_CNF_REF2LIV.ULSS " +
            "AND SO_CODREF2LIVC2.TPSCR=SO_CNF_REF2LIV.TPSCR) racdia";
                            where +=
                                "\n and (r.idreferto=racdia.idreferto(+) and racdia.gruppo(+)='" +
                                ConfigurationConstants.NOME_GRUPPO_RACDIA_2L + "') and racdia.codreg=" +
                                bean.getRacdia();
                            note += "\nDiagnosi istologica peggiore: " + bean.getRacdia_desc();

                        }
                        /* 20081210 MOD versione 1.2 */
                        if (bean.getLesione_hpv() != null && bean.getLesione_hpv().intValue() >= 0) {
                            where += "\n and r.biohpv=" + bean.getLesione_hpv();
                            note +=
                                "\nLesione da HPV: " +
                                (bean.getLesione_hpv().intValue() == 1 ? "rilevata" : "dato non disponibile");
                        }
                        if (bean.getIst_bio() != null && bean.getIst_bio().intValue() >= 0) {
                            where += "\n and r.istbioptica=" + bean.getIst_bio();
                            note +=
                                "\nIstologia bioptica: " +
                                (bean.getIst_bio().intValue() == 1 ? "eseguita" : "non eseguita");
                        }
                        if (bean.getIst_bio_diagnosi() !=
                            null) {
                            //aggiungo un join per la diagnosi della biopsia
                            select +=
  ", \n" + "(SELECT SO_CODREF2LIVC2.ID, " + "SO_CODREF2LIVC2.gruppo, " + "SO_CODREF2LIVC2.idreferto, " +
  " SO_CNF_REF2LIV.CODREG, " + " SO_CNF_REF2LIV.DESCRIZIONE, " + "SO_CNF_REF2LIV.ORDINE " + "FROM SO_CODREF2LIVC2, " +
  "SO_CNF_REF2LIV " + "WHERE SO_CODREF2LIVC2.IDCNFREF=SO_CNF_REF2LIV.IDCNFREF2L " +
  "AND SO_CODREF2LIVC2.GRUPPO=SO_CNF_REF2LIV.GRUPPO " + "AND SO_CODREF2LIVC2.ULSS=SO_CNF_REF2LIV.ULSS " +
  "AND SO_CODREF2LIVC2.TPSCR=SO_CNF_REF2LIV.TPSCR) biodia";
                            where +=
                                "\n and (r.idreferto=biodia.idreferto(+) and biodia.gruppo(+)='" +
                                ConfigurationConstants.NOME_GRUPPO_BIODIA_2L + "') and biodia.codreg=" +
                                bean.getIst_bio_diagnosi();
                            note += "\nDiagnosi istologica: " + bean.getIst_bio_diagnosi_desc();

                        }
                        /* 20081210 fine mod */
                    }

                    //solo per il colon-retto
                    if ("CO".equals(tpscr)) {
                        if (bean.getRxcolon() != null && Integer.parseInt(bean.getRxcolon()) >= 0) {

                            where += "\n and r.rx_colon=" + bean.getRxcolon();
                            note +=
                                "\nRx colon: " +
                                (Integer.parseInt(bean.getRxcolon()) == 0 ? "non eseguito" : "eseguito");
                        }
                        if (bean.getRx_conclusioni() != null) {
                            where += "\n and r.rx_concl=" + bean.getRx_conclusioni();
                            note += "\nConclusioni rx colon: " + bean.getRx_conclusioni_desc();
                        }
                        if ((bean.getEndo_estensione() != null && Integer.parseInt(bean.getEndo_estensione()) >= 0) ||
                            (bean.getEndo_complicanze() != null) || (bean.getMotivo() != null) ||
                            (bean.getProcedura() != null && bean.getProcedura().intValue() >= 0)) {
                            //aggiungo il join alla tabella delle endoscopie
                            select += ", \nSO_ENDOSCOPIA E";
                            where += "\n and r.IDREFERTO=E.IDREFERTO";
                        }

                        if (bean.getEndo_estensione() != null && Integer.parseInt(bean.getEndo_estensione()) >= 0) {
                            where += "\n AND E.estenSIONE=" + bean.getEndo_estensione();
                            note +=
                                "\nEstensione endoscopia: " +
                                (Integer.parseInt(bean.getEndo_estensione()) == 0 ? "incompleta" : "completa");

                        }
                        if (bean.getMotivo() != null) {
                            where += "\n AND E.motivo=" + bean.getMotivo();
                            note += "\nMotivo di incompletezza: " + bean.getMotivo_desc();
                        }
                        if (bean.getProcedura() != null && bean.getProcedura().intValue() >= 0) {
                            where += "\n AND E.procedura=" + bean.getProcedura();
                            note +=
                                "\nProcedura operativa: " +
                                (bean.getProcedura().intValue() >= 1 ? "eseguita" : "non eseguita");
                        }

                        if (bean.getEndo_complicanze() != null) {
                            where += "\n AND E.complicanze=" + bean.getEndo_complicanze();
                            note += "\nComplicanze endoscopia: " + bean.getEndo_complicanze_desc();

                        }
                        if (bean.getConclusioni2l() != null) {
                            where += "\n AND r.conclusioni=" + bean.getConclusioni2l();
                            note += "\nConclusioni di 2° livello: " + bean.getConclusioni2l_desc();

                        }
                        if (bean.getDiagnosi_co() != null) {
                            where += "\n AND r.diagnosi_peggiore=" + bean.getDiagnosi_co();
                            note += "\nDiagnosi istologica peggiore: " + bean.getDiagnosi_co_desc();

                        }
                    } else if ("MA".equals(tpscr)) {
                        ///////////////////////////////////////////
                        if (bean.getMammo() != null && bean.getMammo().intValue() >= 0) {
                            where += "\n AND r.mammo=" + bean.getMammo();
                            note += "\nMammografia: " + (bean.getMammo().intValue() == 0 ? "non eseguita" : "eseguita");
                        }
                        if (bean.getEco() != null && bean.getEco().intValue() >= 0) {
                            where += "\n AND r.eco=" + bean.getEco();
                            note += "\nEcografia: " + (bean.getEco().intValue() == 0 ? "non eseguita" : "eseguita");
                        }
                        if (bean.getClinico() != null && bean.getClinico().intValue() >= 0) {
                            where += "\n AND r.clinico=" + bean.getClinico();
                            note +=
                                "\nEsame clinico: " + (bean.getClinico().intValue() == 0 ? "non eseguito" : "eseguito");
                        }
                        if (bean.getCitologia() != null && bean.getCitologia().intValue() >= 0) {
                            where +=
                                "\n AND (r.citologia_dx=" + bean.getCitologia() + " OR r.citologia_sx=" +
                                bean.getCitologia() + ")";
                            note += "\nEsame citologico: " + bean.getCitologia_desc();
                        }
                        if (bean.getEsito_cito() != null) {
                            where +=
                                "\n AND (r.c_dx_esito=" + bean.getEsito_cito() + " OR r.c_sx_esito=" +
                                bean.getEsito_cito() + ")";
                            note += "\nEsito citologia: " + bean.getEsito_cito_desc();
                        }

                        if (bean.getAgobiopsia() != null && bean.getAgobiopsia().intValue() >= 0) {
                            where +=
                                "\n AND (r.agob_dx=" + bean.getAgobiopsia() + " OR r.agob_sx=" + bean.getAgobiopsia() +
                                ")";
                            note += "\nAgobiopsia, esame: " + bean.getAgobiopsia_desc();
                        }
                        if (bean.getEsito_agob() != null) {
                            where +=
                                "\n AND (r.a_dx_esito=" + bean.getEsito_agob() + " OR r.a_sx_esito=" +
                                bean.getEsito_agob() + ")";
                            note += "\nEsito agobiopsia: " + bean.getEsito_agob_desc();
                        }

                        if (bean.getRacdia() != null) {
                            where += "\n AND r.diagnosi=" + bean.getRacdia();
                            note += "\nDiagnosi istologica peggiore: " + bean.getRacdia_desc();

                        }
                    }


                    if (bean.getIdsugg2l() != null) {
                        where +=
                            "\n and r." + ViewHelper.decodeByTpscr(tpscr, "sugg2l", "idsugg2l", "sugg2l", null, null) +
                            "=" + bean.getIdsugg2l();
                        note += "\nSuggerimento conclusivo 2° livello: " + bean.getIdsugg2l_desc();
                    }

                    //casi in cui si ricerca la presenza di un intervento
                    if ((bean.getIntchiusi() != null && bean.getIntchiusi().trim().length() > 0 &&
                         Integer.parseInt(bean.getIntchiusi()) > 0) || bean.getIntval() != null ||
                        bean.getTipo_int() != null || bean.getIdsugg3l() != null || bean.getPm() != null ||
                        bean.getPn() != null || bean.getPt() != null ||
                        ("CI".equals(tpscr) && (bean.getIst_chir() != null || bean.getIst_chir_diagnosi() != null)) ||
                        ("MA".equals(tpscr) && bean.getAscella() != null)) {

                        //join esterno con l'intervento
                        select +=
                            ViewHelper.decodeByTpscr(tpscr, ", so_interventocito i", ", so_interventocolon i",
                                                     ", so_interventomammo i", null, null);
                        where += "\n and i.idreferto(+)=r.idreferto "; /*and i.idint in ("+
                  "SELECT MAX(I2.IDINT) FROM SO_INTERVENTOCITO I2 "+
                  "WHERE I2.IDREFERTO=r.idreferto)";*/
                    }

                    /* 20081210 MOD versione 1.2 */
                    //solo pe rlo screening citologico
                    if ("CI".equals(tpscr)) {
                        if (bean.getIst_chir() != null && bean.getIst_chir().intValue() > 0) {
                            where += "\n and i.istchirurgica=" + bean.getIst_chir();
                            note +=
                                "\nIstologia chirurgica: " +
                                (bean.getIst_chir().intValue() == 1 ? "eseguita" : "non eseguita");

                        }
                        if (bean.getIst_chir_diagnosi() != null && bean.getIst_chir_diagnosi().intValue() > 0) {
                            where += "\n and i.istdia=" + bean.getIst_chir_diagnosi();
                            note += "\nDiagnosi: " + bean.getIst_chir_diagnosi_desc();

                        }
                    }
                    /* 20081210 fine mod */
                    if (bean.getPn() != null) {
                        where +=
                            "\n and i." +
                            ViewHelper.decodeByTpscr(tpscr, "istopn", "stadio_pn", "stadio_pn", null, null) + "=" +
                            bean.getPn();
                        note += "\nStadio pN: " + bean.getPn_desc();
                    }
                    if (bean.getPt() != null) {
                        where +=
                            "\n and i." +
                            ViewHelper.decodeByTpscr(tpscr, "istopt", "stadio_pt", "stadio_pt", null, null) + "=" +
                            bean.getPt();
                        note += "\nStadio pT: " + bean.getPt_desc();
                    }
                    if (bean.getPm() != null) {
                        where +=
                            "\n and i." +
                            ViewHelper.decodeByTpscr(tpscr, "istolm", "stadio_m", "stadio_m", null, null) + "=" +
                            bean.getPm();
                        note += "\nStadio M: " + bean.getPm_desc();
                    }

                    //07/02/2013 Gaion: correzione -1
                    if (bean.getIntchiusi() != null && bean.getIntchiusi().trim().length() > 0 &&
                        Integer.parseInt(bean.getIntchiusi()) > -1) {
                        if ("0".equals(bean.getIntchiusi())) {
                            where += "\n and i.idint is not null and (r.intchiusi is null or r.intchiusi=0)";
                            note += "\nInterventi inseriti ma NON chiusi";
                        } else if ("1".equals(bean.getIntchiusi())) {
                            where += "\n and i.idint is not null and r.intchiusi=1";
                            note += "\nInterventi chiusi";
                        }


                    }

                    if (bean.getIntval() != null) {
                        where +=
                            "\n and i.opzioneesec=" + bean.getIntval() + " and i." +
                            ViewHelper.decodeByTpscr(tpscr, "gropzione", "gr_opzione", "gr_opzione", null, null) +
                            "='" + ConfigurationConstants.NOME_GRUPPO_INTVAL_2L + "'";
                        note += "\nIntervento: " + bean.getIntval_desc();
                    }

                    if (bean.getTipo_int() != null) {
                        where +=
                            "\n and i.tpintervento=" + bean.getTipo_int() + " and i." +
                            ViewHelper.decodeByTpscr(tpscr, "grtpintervento", "gr_tpintervento", "gr_tpintervento",
                                                     null, null) + "='" + ConfigurationConstants.NOME_GRUPPO_INTTIP_2L +
                            "'";
                        note += "\nTipo di intervento: " + bean.getTipo_int_desc();
                    }


                    if ("MA".equals(tpscr) && bean.getAscella() != null) {
                        //aggiungo il join alla tabella delle endoscopie
                        select += ", \nSO_CODREF3LIVMA C3";
                        where +=
                            "\n and I.IDINT=C3.IDINT AND I.ULSS=C3.ULSS AND I.TPSCR=C3.TPSCR " + " AND C3.GRUPPO='" +
                            ConfigurationConstants.NOME_GRUPPO_ASCELLA + "' " + "\n and c3.idcnfref=" +
                            bean.getAscella();

                        note += "\nTipo intervento ascella: " + bean.getAscella_desc();
                    }

                    if (bean.getIdsugg3l() != null) {
                        //join per prendere il codice regionale (in tuti gli altri casi e' così)
                        select += ", so_cnf_sugg_3liv s";
                        where +=
                            "\n and (i." +
                            ViewHelper.decodeByTpscr(tpscr, "idsugg", "idsugg3l", "idsugg3l", null, null) +
                            "=s.idsugg3l and i.ulss=s.ulss and i.tpscr=s.tpscr) " + " and s.codregionale=" +
                            bean.getIdsugg3l();
                        note += "\nSuggerimento post-intervento: " + bean.getIdsugg3l_desc();

                    }


                } //fine 2 livello

            } //livello>0


            // }//fine filtro 1

            // String[] out=new String[2];
            //prima parte di query
            // out[0]=select+"\n"+where;

            //reset
            /* select=original_select;
     where=original_where;*/
            /*******  filtro 2 ****************/

            if (bean.getFiltro_2() == 1 && !bean.isEsclusi()) {
                note += "\nINVITO SUCCESSIVO: ";
                select = select.replaceAll("so_stats_coorti", "so_stats_seq_inviti");


                /*20080710 MOD: aggiunta categoria invito*/
                if (bean.getCat_invito_2() != null) {
                    where += "\n and c.idcateg_2liv=" + bean.getCat_invito_2();
                    note += "\n\t\t\tCategoria di invito: " + bean.getCat_invito_desc_2();
                }
                /*20080710 FINE MOD*/

                if (bean.getTipo_invito_2() != null && bean.getTipo_invito_2().trim().length() > 0) {
                    where += "\n and c.idtpinvito_2liv='" + bean.getTipo_invito_2() + "'";
                    note += "\n\t\t\tTipo di invito: " + bean.getTipo_invito_desc_2();
                }


                if (bean.getEsito_2() != null && bean.getEsito_2().trim().length() > 0) {
                    where += "\n and c.codesitoinvito_2liv='" + bean.getEsito_2() + "'";
                    note += "\n\t\t\tEsito: " + bean.getEsito_desc_2();
                }

                if (bean.getTipo_richiamo_2() != null && bean.getTipo_richiamo_2().trim().length() > 0) {
                    where += "\n and c.tprichiamo_2liv='" + bean.getTipo_richiamo_2() + "'";
                    note += "\n\t\t\tTipo di richiamo: " + bean.getTipo_richiamo_desc_2();
                }


                if (bean.getLivello_2() > 0) {

                    where +=
                        " and c.livello_2liv=" + bean.getLivello_2() + " and c.livesito_2liv=" + bean.getLivello_2();
                    note += "\n\t\t\tLivello: " + bean.getLivello_2();

                }

                if (bean.getLivello_2() > 0 && bean.isReferto2()) {

                    if ("0".equals(bean.getCompleto_2())) {
                        //referti inseriti ma non completi
                        where += "\n and r1.idreferto is not null and (r1.completo=0 or r1.completo is null)";
                        note += "\n\t\t\tReferto inserito ma NON completo";
                    } else if ("1".equals(bean.getCompleto_2())) {
                        where += "\n and r1.idreferto is not null and r1.completo=1";
                        note += "\n\t\t\tReferto completo";
                    } else {
                        where += "\n and r1.idreferto is not null ";
                        note += "\nReferto presente";
                    }


                    //primo livello
                    if (bean.getLivello_2() ==
                        1) {
                        //join esterno col ref di 1 livello
                        //join esterno col ref di 1 livello
                        select +=
     ViewHelper.decodeByTpscr(tpscr, ", so_ref1liv_completo r1", ", so_ref1livco_completo r1",
                              ", so_ref1livma_completo r1", null, null);
                        where += "\n and c.idinvito_2liv=r1.idinvito";


                        //solo per il citologico
                        if ("CI".equals(tpscr)) {
                            if (bean.getAdepre_2() != null) {
                                where +=
                                    "\n and r1.adepre_id=" + bean.getAdepre_2() + " and r1.adepre_gruppo='" +
                                    ConfigurationConstants.NOME_GRUPPO_ADEPRE + "'";
                                note += "\n\t\t\tAdeguatezza: " + bean.getAdepre_desc_2();
                            }

                            if (bean.getGiudia_2() != null) {
                                where +=
                                    "\n and r1.giudia_id=" + bean.getGiudia_2() + " and r1.giudia_gruppo='" +
                                    ConfigurationConstants.NOME_GRUPPO_GIUDIA + "'";
                                note += "\n\t\t\tGiudizio diagnostico: " + bean.getGiudia_desc_2();
                            }

                            if (bean.getPositi_2() != null) {
                                where +=
                                    "\n and r1.cnf_id=" + bean.getPositi_2() + " and r1.cnf_gruppo='" +
                                    ConfigurationConstants.NOME_GRUPPO_POSITI + "'";
                                note += "\n\t\t\tTipo di positivita': " + bean.getPositi_desc_2();
                            }

                            /* mod 20081210 versione 1.2*/
                            if (bean.getInalim_2() != null) {
                                where +=
                                    "\n and r1.cnf_id=" + bean.getInalim_2() + " and r1.cnf_gruppo='" +
                                    ConfigurationConstants.NOME_GRUPPO_INALIM + "'";
                                note += "\nTipo di inadeguatezza: " + bean.getInalim_desc_2();
                            }

                            /* fine mod 20081210*/
                        }
                        //solo per il mammografico
                        else if ("MA".equals(tpscr)) {

                            if (bean.getMx_esito_2() != null) {
                                where += "\n and r1.esito=" + bean.getMx_esito_2();
                                note += "\n\t\t\tEsito radiologico: " + bean.getMx_esito_desc_2();
                            }
                        }
                        /*20080710 MOD: aggiungi filtri CCR*/
                        else if ("CO".equals(tpscr)) {
                            if (bean.getQuantita_2() != null && bean.getQuantita_2().intValue() > 0) {
                                switch (bean.getQuantita_2().intValue()) {
                                case 1:
                                    {
                                        where += "\n and r1.quantita<100";
                                        note += "\nQuantita' <100 mg";
                                        break;
                                    }
                                case 2:
                                    {
                                        where += "\n and r1.quantita>=100";
                                        note += "\nQuantita' >=100 mg";
                                        break;
                                    }
                                case 3:
                                    {
                                        where += "\n and r1.quantita>=90 and r1.quantita<=100";
                                        note += "\nQuantita' tra 90 e 100 mg";
                                        break;
                                    }
                                case 4:
                                    {
                                        where += "\n and r1.quantita>=100 and r1.quantita<=110";
                                        note += "\nQuantita' tra 100 e 110 mg";
                                        break;
                                    }
                                case 5:
                                    {
                                        where += "\n and r1.quantita>=110";
                                        note += "\nQuantita' >=110 mg";
                                        break;
                                    }
                                default:
                                    break;
                                }

                            }
                        }
                        /*20080710 FINE MOD*/


                        if (bean.getIdsugg1l_2() != null) {
                            where += "\n and r1.idsugg=" + bean.getIdsugg1l_2();
                            note += "\n\t\t\tSuggerimento conclusivo: " + bean.getIdsugg1l_desc_2();
                        }


                    } //fine 1 livello
                    //seocndo livello
                    else if (bean.getLivello_2() == 2) {

                        //join esterno col ref di 2 livello
                        select +=
                            ViewHelper.decodeByTpscr(tpscr, ", so_ref2liv_completo r1", ", so_ref2livco_completo r1",
                                                     ", so_ref2livma_completo r1", null, null);
                        where += "\n and c.idinvito_2liv=r1.idinvito";


                        //solo per il citologico
                        if ("CI".equals(tpscr)) {
                            if (bean.getColpvl_2() != null) {
                                where += "\n and r1.colpovl=" + bean.getColpvl_2();
                                note += "\n\t\t\tColposcopia: " + bean.getColpvl_desc_2();
                            }

                            if (bean.getColpes_2() != null) {
                                where +=
                                    "\n and r1.codreg=" + bean.getColpes_2() + " and r1.gruppo='" +
                                    ConfigurationConstants.NOME_GRUPPO_COLPES_2L + "'";
                                note += "\n\t\t\tEsito della colposcopia: " + bean.getColpes_desc_2();
                            }

                            if (bean.getRacdia_2() !=
                                null) {
                                //aggiungo un join per la diagnosi peggiore
                                select +=
              ", \n" + "(SELECT SO_CODREF2LIVC2.ID, " + "SO_CODREF2LIVC2.gruppo, " + "SO_CODREF2LIVC2.idreferto, " +
              " SO_CNF_REF2LIV.CODREG, " + " SO_CNF_REF2LIV.DESCRIZIONE, " + "SO_CNF_REF2LIV.ORDINE " +
              "FROM SO_CODREF2LIVC2, " + "SO_CNF_REF2LIV " +
              "WHERE SO_CODREF2LIVC2.IDCNFREF=SO_CNF_REF2LIV.IDCNFREF2L " +
              "AND SO_CODREF2LIVC2.GRUPPO=SO_CNF_REF2LIV.GRUPPO " + "AND SO_CODREF2LIVC2.ULSS=SO_CNF_REF2LIV.ULSS " +
              "AND SO_CODREF2LIVC2.TPSCR=SO_CNF_REF2LIV.TPSCR) racdia1";
                                where +=
                                    "\n and (r1.idreferto=racdia1.idreferto(+) and racdia1.gruppo(+)='" +
                                    ConfigurationConstants.NOME_GRUPPO_RACDIA_2L + "') and racdia1.codreg=" +
                                    bean.getRacdia_2();
                                note += "\n\t\t\tDiagnosi istologica peggiore: " + bean.getRacdia_desc_2();

                            }


                            /* 20081210 MOD versione 1.2 */
                            if (bean.getLesione_hpv_2() != null && bean.getLesione_hpv_2().intValue() >= 0) {
                                where += "\n and r1.biohpv=" + bean.getLesione_hpv_2();
                                note +=
                                    "\nLesione da HPV: " +
                                    (bean.getLesione_hpv_2().intValue() == 1 ? "rilevata" : "dato non disponibile");
                            }
                            if (bean.getIst_bio_2() != null && bean.getIst_bio_2().intValue() >= 0) {
                                where += "\n and r1.istbioptica=" + bean.getIst_bio_2();
                                note +=
                                    "\nIstologia bioptica: " +
                                    (bean.getIst_bio_2().intValue() == 1 ? "eseguita" : "non eseguita");
                            }
                            if (bean.getIst_bio_diagnosi_2() !=
                                null) {
                                //aggiungo un join per la diagnosi della biopsia
                                select +=
    ", \n" + "(SELECT SO_CODREF2LIVC2.ID, " + "SO_CODREF2LIVC2.gruppo, " + "SO_CODREF2LIVC2.idreferto, " +
    " SO_CNF_REF2LIV.CODREG, " + " SO_CNF_REF2LIV.DESCRIZIONE, " + "SO_CNF_REF2LIV.ORDINE " + "FROM SO_CODREF2LIVC2, " +
    "SO_CNF_REF2LIV " + "WHERE SO_CODREF2LIVC2.IDCNFREF=SO_CNF_REF2LIV.IDCNFREF2L " +
    "AND SO_CODREF2LIVC2.GRUPPO=SO_CNF_REF2LIV.GRUPPO " + "AND SO_CODREF2LIVC2.ULSS=SO_CNF_REF2LIV.ULSS " +
    "AND SO_CODREF2LIVC2.TPSCR=SO_CNF_REF2LIV.TPSCR) biodia1";
                                where +=
                                    "\n and (r1.idreferto=biodia1.idreferto(+) and biodia1.gruppo(+)='" +
                                    ConfigurationConstants.NOME_GRUPPO_BIODIA_2L + "') and biodia1.codreg=" +
                                    bean.getIst_bio_diagnosi_2();
                                note += "\nDiagnosi istologica: " + bean.getIst_bio_diagnosi_desc_2();

                            }
                            /* 20081210 fine mod */
                        }

                        //solo per il colon-retto
                        if ("CO".equals(tpscr)) {
                            if (bean.getRxcolon_2() != null && Integer.parseInt(bean.getRxcolon_2()) >= 0) {

                                where += "\n and r1.rx_colon=" + bean.getRxcolon_2();
                                note +=
                                    "\n\t\t\tRx colon: " +
                                    (Integer.parseInt(bean.getRxcolon_2()) == 0 ? "non eseguito" : "eseguito");
                            }
                            if (bean.getRx_conclusioni_2() != null) {
                                where += "\n and r1.rx_concl=" + bean.getRx_conclusioni_2();
                                note += "\n\t\t\tConclusioni rx colon: " + bean.getRx_conclusioni_desc_2();
                            }
                            if ((bean.getEndo_estensione_2() != null &&
                                 Integer.parseInt(bean.getEndo_estensione_2()) >= 0) ||
                                (bean.getEndo_complicanze_2() != null) || (bean.getMotivo_2() != null) ||
                                (bean.getProcedura_2() != null && bean.getProcedura_2().intValue() >= 0)) {
                                //aggiungo il join alla tabella delle endoscopie
                                select += ", \nSO_ENDOSCOPIA E1";
                                where += "\n and r1.IDREFERTO=E1.IDREFERTO";
                            }

                            if (bean.getEndo_estensione_2() != null &&
                                Integer.parseInt(bean.getEndo_estensione_2()) >= 0) {
                                where += "\n AND E1.estenSIONE=" + bean.getEndo_estensione_2();
                                note +=
                                    "\n\t\t\tEstensione endoscopia: " +
                                    (Integer.parseInt(bean.getEndo_estensione_2()) == 0 ? "incompleta" : "completa");

                            }
                            if (bean.getMotivo_2() != null) {
                                where += "\n AND E1.motivo=" + bean.getMotivo_2();
                                note += "\n\t\t\tMotivo di incompletezza: " + bean.getMotivo_desc_2();
                            }
                            if (bean.getProcedura_2() != null && bean.getProcedura_2().intValue() >= 0) {
                                where += "\n AND E1.procedura=" + bean.getProcedura_2();
                                note +=
                                    "\n\t\t\tProcedura operativa: " +
                                    (bean.getProcedura_2().intValue() >= 1 ? "eseguita" : "non eseguita");
                            }
                            if (bean.getEndo_complicanze_2() != null) {
                                where += "\n AND E1.complicanze=" + bean.getEndo_complicanze_2();
                                note += "\n\t\t\tComplicanze endoscopia: " + bean.getEndo_complicanze_desc_2();

                            }
                            if (bean.getConclusioni2l_2() != null) {
                                where += "\n AND r1.conclusioni=" + bean.getConclusioni2l_2();
                                note += "\n\t\t\tConclusioni di 2° livello: " + bean.getConclusioni2l_desc_2();

                            }
                            if (bean.getDiagnosi_co_2() != null) {
                                where += "\n AND r1.diagnosi_peggiore=" + bean.getDiagnosi_co_2();
                                note += "\n\t\t\tDiagnosi istologica peggiore: " + bean.getDiagnosi_co_desc_2();

                            }
                        } else if ("MA".equals(tpscr)) {
                            ///////////////////////////////////////////
                            if (bean.getMammo_2() != null && bean.getMammo_2().intValue() >= 0) {
                                where += "\n AND r1.mammo=" + bean.getMammo_2();
                                note +=
                                    "\n\t\t\tMammografia: " +
                                    (bean.getMammo_2().intValue() == 0 ? "non eseguita" : "eseguita");
                            }
                            if (bean.getEco_2() != null && bean.getEco_2().intValue() >= 0) {
                                where += "\n AND r1.eco=" + bean.getEco_2();
                                note +=
                                    "\n\t\t\tEcografia: " +
                                    (bean.getEco_2().intValue() == 0 ? "non eseguita" : "eseguita");
                            }
                            if (bean.getClinico_2() != null && bean.getClinico_2().intValue() >= 0) {
                                where += "\n AND r1.clinico=" + bean.getClinico_2();
                                note +=
                                    "\n\t\t\tEsame clinico: " +
                                    (bean.getClinico_2().intValue() == 0 ? "non eseguito" : "eseguito");
                            }
                            if (bean.getCitologia_2() != null && bean.getCitologia_2().intValue() >= 0) {
                                where +=
                                    "\n AND (r1.citologia_dx=" + bean.getCitologia_2() + " OR r.citologia_sx=" +
                                    bean.getCitologia_2() + ")";
                                note += "\n\t\t\tEsame citologico: " + bean.getCitologia_desc_2();
                            }
                            if (bean.getEsito_cito_2() != null) {
                                where +=
                                    "\n AND (r1.c_dx_esito=" + bean.getEsito_cito_2() + " OR r1.c_sx_esito=" +
                                    bean.getEsito_cito_2() + ")";
                                note += "\n\t\t\tEsito citologia: " + bean.getEsito_cito_desc_2();
                            }

                            if (bean.getAgobiopsia_2() != null && bean.getAgobiopsia_2().intValue() >= 0) {
                                where +=
                                    "\n AND (r1.agob_dx=" + bean.getAgobiopsia_2() + " OR r1.agob_sx=" +
                                    bean.getAgobiopsia_2() + ")";
                                note += "\n\t\t\tAgobiopsia, esame: " + bean.getAgobiopsia_desc_2();
                            }
                            if (bean.getEsito_agob_2() != null) {
                                where +=
                                    "\n AND (r1.a_dx_esito=" + bean.getEsito_agob_2() + " OR r1.a_sx_esito=" +
                                    bean.getEsito_agob_2() + ")";
                                note += "\n\t\t\tEsito agobiopsia: " + bean.getEsito_agob_desc_2();
                            }

                            if (bean.getRacdia_2() != null) {
                                where += "\n AND r1.diagnosi=" + bean.getRacdia_2();
                                note += "\n\t\t\tDiagnosi istologica peggiore: " + bean.getRacdia_desc_2();

                            }
                        }


                        if (bean.getIdsugg2l_2() != null) {
                            where +=
                                "\n and r1." +
                                ViewHelper.decodeByTpscr(tpscr, "sugg2l", "idsugg2l", "sugg2l", null, null) + "=" +
                                bean.getIdsugg2l_2();
                            note += "\n\t\t\tSuggerimento conclusivo 2° livello: " + bean.getIdsugg2l_desc_2();
                        }

                        //casi in cui si ricerca la presenza di un intervento
                        if ((bean.getIntchiusi_2() != null && bean.getIntchiusi_2().trim().length() > 0 &&
                             Integer.parseInt(bean.getIntchiusi_2()) > 0) || bean.getIntval_2() != null ||
                            bean.getTipo_int_2() != null || bean.getIdsugg3l_2() != null || bean.getPm_2() != null ||
                            bean.getPn_2() != null || bean.getPt_2() != null ||
                            ("CI".equals(tpscr) &&
                             (bean.getIst_chir_2() != null || bean.getIst_chir_diagnosi_2() != null)) ||
                            ("MA".equals(tpscr) && bean.getAscella_2() != null)) {

                            //join esterno con l'intervento
                            select +=
                                ViewHelper.decodeByTpscr(tpscr, ", so_interventocito i1", ", so_interventocolon i1",
                                                         ", so_interventomammo i1", null, null);
                            where += "\n and i1.idreferto(+)=r1.idreferto "; /*and not exists ("+
                "SELECT 1 FROM SO_INTERVENTOCITO I2 "+
                "WHERE I2.IDREFERTO=r1.idreferto and i1.idint>i2.idint)";
                   */
                        }

                        if (bean.getPn_2() != null) {
                            where +=
                                "\n and i1." +
                                ViewHelper.decodeByTpscr(tpscr, "istopn", "stadio_pn", "stadio_pn", null, null) + "=" +
                                bean.getPn_2();
                            note += "\n\t\t\tStadio pN: " + bean.getPn_desc_2();
                        }
                        if (bean.getPt_2() != null) {
                            where +=
                                "\n and i1." +
                                ViewHelper.decodeByTpscr(tpscr, "istopt", "stadio_pt", "stadio_pt", null, null) + "=" +
                                bean.getPt_2();
                            note += "\n\t\t\tStadio pT: " + bean.getPt_desc_2();
                        }
                        if (bean.getPm_2() != null) {
                            where +=
                                "\n and i1." +
                                ViewHelper.decodeByTpscr(tpscr, "istolm", "stadio_m", "stadio_m", null, null) + "=" +
                                bean.getPm_2();
                            note += "\n\t\t\tStadio M: " + bean.getPm_desc_2();
                        }

                        /* 20081210 MOD versione 1.2 */
                        //solo pe rlo screening citologico
                        if ("CI".equals(tpscr)) {
                            if (bean.getIst_chir_2() != null && bean.getIst_chir_2().intValue() > 0) {
                                where += "\n and i1.istchirurgica=" + bean.getIst_chir_2();
                                note +=
                                    "\nIstologia chirurgica: " +
                                    (bean.getIst_chir_2().intValue() == 1 ? "eseguita" : "non eseguita");

                            }
                            if (bean.getIst_chir_diagnosi_2() != null && bean.getIst_chir_diagnosi_2().intValue() > 0) {
                                where += "\n and i1.istdia=" + bean.getIst_chir_diagnosi_2();
                                note += "\nDiagnosi: " + bean.getIst_chir_diagnosi_desc_2();

                            }
                        }
                        /* 20081210 fine mod */

                        if (bean.getIntchiusi_2() != null && bean.getIntchiusi_2().trim().length() > 0 &&
                            Integer.parseInt(bean.getIntchiusi_2()) > 0) {
                            if ("0".equals(bean.getIntchiusi_2())) {
                                where += "\n and i1.idint is not null and (r1.intchiusi is null or r1.intchiusi=0)";
                                note += "\n\t\t\tInterventi inseriti ma NON chiusi";
                            } else if ("1".equals(bean.getIntchiusi_2())) {
                                where += "\n and i1.idint is not null and r1.intchiusi=1";
                                note += "\n\t\t\tInterventi chiusi";
                            }


                        }

                        if (bean.getIntval_2() != null) {
                            where +=
                                "\n and i1.opzioneesec=" + bean.getIntval_2() +
                                //20110404 Serra: correzione
                                " and i1." +
                           ViewHelper.decodeByTpscr(tpscr, "gropzione", "gr_opzione", "gr_opzione", null, null) + "='" +
                           ConfigurationConstants.NOME_GRUPPO_INTVAL_2L + "'";
                            note += "\n\t\t\tIntervento: " + bean.getIntval_desc_2();
                        }

                        if (bean.getTipo_int_2() != null) {
                            where +=
                                "\n and i1.tpintervento=" + bean.getTipo_int_2() +
                                //20110404 Serra: correzione
                                " and i1." +
                           ViewHelper.decodeByTpscr(tpscr, "grtpintervento", "gr_tpintervento", "gr_tpintervento", null,
                                                    null) + "='" + ConfigurationConstants.NOME_GRUPPO_INTTIP_2L + "'";
                            note += "\n\t\t\tTipo di intervento: " + bean.getTipo_int_desc_2();
                        }


                        if ("MA".equals(tpscr) && bean.getAscella_2() != null) {
                            //aggiungo il join alla tabella delle endoscopie
                            select += ", \nSO_CODREF3LIVMA C4";
                            where +=
                                "\n and I1.IDINT=C4.IDINT AND I1.ULSS=C4.ULSS AND I1.TPSCR=C4.TPSCR " +
                                " AND C4.GRUPPO='" + ConfigurationConstants.NOME_GRUPPO_ASCELLA + "' " +
                                "\n and c4.idcnfref3l=" + bean.getAscella();

                            note += "\n\t\t\tTipo intervento ascella: " + bean.getAscella_desc_2();
                        }

                        if (bean.getIdsugg3l_2() != null) {
                            //join per prendere il codice regionale (in tuti gli altri casi e' così)
                            select += ", so_cnf_sugg_3liv s1";
                            where +=
                                "\n and (i1." +
                                ViewHelper.decodeByTpscr(tpscr, "idsugg", "idsugg3l", null, null, null) +
                                "=s1.idsugg3l and i1.ulss=s1.ulss and i1.tpscr=s1.tpscr) " + " and s1.codregionale=" +
                                bean.getIdsugg3l_2();
                            note += "\n\t\t\tSuggerimento post-intervento: " + bean.getIdsugg3l_desc_2();

                        }


                    } //fine 2 livello

                } //livello>0


            } //fine filtro 2

        } //fine della query se si lavora sugli invitati

        /**************** CONCLUSIONE ********************/
        String[] out = new String[2];
        out[0] = select + "\n" + where;
        //     System.out.println(out[0]);
        out[1] = note;
        return out;


    }


    public static void indicazioni(int row, int col,String tpscr, String ulss, Date dal,
                                   Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                   BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                   BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                   int livello_esito, int tipo_referto,
                                   /*20080708 MOD: aggiunta round inviti ai filtri*/
                                   BigDecimal round_inviti
                                   /*20080708 FINE MOD*/
                                   ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        String voName, header_0, groupName;
        BigDecimal adepre;
        //prendo come righe l epositivita' o gli inadeguati a seconda del livello
        if (tipo_referto == 1) {
            //inadeguatezza ed invio al primo livello
            voName = "Stats_SoCnfRef1livINALIMView1";
            header_0 = "Inadeguato";
            groupName = ConfigurationConstants.NOME_GRUPPO_INALIM;
            //  giudia=null;
            adepre = new BigDecimal(ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.intValue());

        } else {
            //giudizio diagnostico positivo ed invio al secondo livello
            voName = "Stats_SoCnfRef1livPOSITIView1";
            header_0 = "Positivo per";
            groupName = ConfigurationConstants.NOME_GRUPPO_POSITI;
            // giudia=new BigDecimal(ConfigurationConstants.CODICE_GIUDIA_POSITIVO.intValue());
            adepre = null;

        }

        ViewObject positi = am.findViewObject(voName);
        ViewObject esiti = am.findViewObject("Stats_SoCnfEsitoinvitoView1");

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");


        RowSetIterator iter = null;
        RowSetIterator iter1 = null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;


        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            //filtro gli esiti del livello richiesto
            esiti.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + AccessManager.CODREGIONALE + "' AND LIVESITO=" +
                                 livello_esito);
            esiti.executeQuery();

            //UNA COLONNA PER ESITO, UNA PER I TOTALI ed una per gli inviti totali E DUE COME LABL DI RIGA
            headers = new String[esiti.getRowCount() + 4];
            headers[0] = "Codice";
            headers[1] = header_0;
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];
            //per ogni classe di diagnosi (una riga dell amatrice)
            iter = ViewHelper.getRowSetIterator(positi, "positi_iter");
            iter1 = ViewHelper.getRowSetIterator(esiti, "esiti_stats_iter");


            int j = iter.getRowCount();
            while (iter.hasNext()) {

                Row cnf = iter.next();
                j--;
                if (row >= 0 && col >= 0 && row != j)
                    continue;


                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, cnf.getAttribute("Idcnfref1l"));
                    r.setAttribute(1, cnf.getAttribute("Descrizione"));

                }


                //colonna 0: totale test con tale esito
                int i = 2;
                HashMap result = null;
                int x = 0;
                //memorizzo la label
                headers[i] = "Totale test";
                //questa colonna va mostrata
                render[i] = true;
                if (row < 0 || col < 0 || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    result =
                        am.callIndicazioni2livFunction(null, //sesso
                                                            new BigDecimal(0), //tipo di calcolo
                                                            null, //esito
                                                            new BigDecimal(livello_esito), //livello esito

                                                            null, //giudia
                                                            adepre, //adepre,
                                                            groupName,
                                                       new BigDecimal(((Number) cnf.getAttribute("Idcnfref1l")).intValue()),
                                                       ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              cnf.getAttribute("Descrizione"));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    // totali[i]+=x;
                } //fine colonna 0


                //colonna 1: totale inviti conseguenti
                i = 3;
                result = null;
                x = 0;
                //memorizzo la label
                headers[i] = "Totale inviti conseguenti";
                //questa colonna va mostrata
                render[i] = true;
                if (row < 0 || col < 0 || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    result =
                        am.callIndicazioni2livFunction(null, //sesso
                                                            new BigDecimal(1), //tipo di calcolo
                                                            null, //esito
                                                            new BigDecimal(livello_esito), //livello esito

                                                            null, //giudia
                                                            adepre, //adepre,
                                                            groupName,
                                                       new BigDecimal(((Number) cnf.getAttribute("Idcnfref1l")).intValue()),
                                                       ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                    // totali[i]+=x;
                } //fine colonna 1


                //una colonna per ogni esito di secondo livello
                iter1.first();
                iter1.previous();
                //
                while (iter1.hasNext()) {
                    Row e = iter1.next();
                    i++;
                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    result = null;
                    x = 0;
                    //memorizzo la label
                    headers[i] = (String) e.getAttribute("Esitoinvito");
                    //questa colonna va mostrata
                    render[i] = true;
                    result =
                        am.callIndicazioni2livFunction(null, //sesso
                                                            new BigDecimal(1), //tipo di calcolo
                                                            (String) e.getAttribute("Codesitoinvito"),
                                                       //esito
                                                            new BigDecimal(((Number) e.getAttribute("Livesito")).intValue()), //livello esito

                                                       null, //giudia
                                                       adepre, //adepre,
                                                       groupName,
                                                       new BigDecimal(((Number) cnf.getAttribute("Idcnfref1l")).intValue()),
                                                       ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                        return;

                    }
                    // totali[i]+=x;

                }


            } //end while (sulle diagnosi positive


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter1 != null)
                iter1.closeRowSetIterator();


            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }

        }
    }

    public static void indicazioniCO(int row, int col,String tpscr, String ulss, Date dal,
                                     Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                     BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                     BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                     int livello_esito, String sesso,
                                     /*20080708 MOD: aggiunta round inviti ai filtri*/
                                     BigDecimal round_inviti
                                     /*20080708 FINE MOD*/
                                     ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();

        ViewObject positi = am.findViewObject("Stats_SoCnfRef1livGIUDIAView2");
        ViewObject esiti = am.findViewObject("Stats_SoCnfEsitoinvitoView1");

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");


        RowSetIterator iter = null;
        RowSetIterator iter1 = null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;


        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            //filtro gli esiti del livello richiesto
            esiti.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + AccessManager.CODREGIONALE + "' AND LIVESITO=" +
                                 livello_esito);
            esiti.executeQuery();

            //UNA COLONNA PER ESITO, UNA PER I TOTALI ed una per gli inviti totali E DUE COME LABL DI RIGA
            headers = new String[esiti.getRowCount() + 4];
            headers[0] = "Codice";
            headers[1] = "Referto";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];
            //per ogni classe di diagnosi (una riga dell amatrice)
            iter = ViewHelper.getRowSetIterator(positi, "positi_iter");
            iter1 = ViewHelper.getRowSetIterator(esiti, "esiti_stats_iter");


            int j = iter.getRowCount();
            while (iter.hasNext()) {

                Row cnf = iter.next();


                j--;
                if (row >= 0 && col >= 0 && row != j)
                    continue;


                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, cnf.getAttribute("Idcnfref1l"));
                    r.setAttribute(1, cnf.getAttribute("Descrizione"));

                }


                //colonna 0: totale test con tale esito
                int i = 2;
                HashMap result = null;
                int x = 0;
                //memorizzo la label
                headers[i] = "Totale test";
                //questa colonna va mostrata
                render[i] = true;
                if (row < 0 || col < 0 || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    result =
                        am.callIndicazioni2livFunction(sesso, new BigDecimal(0), //tipo di calcolo
                                                            null, //esito
                                                            new BigDecimal(livello_esito),
                                                       //livello esito
                                                            new BigDecimal(((Number) cnf.getAttribute("Idcnfref1l")).doubleValue()), //giudia
                                                       null, //adepre,
                                                       null, null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              cnf.getAttribute("Descrizione"));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    //   totali[i]+=x;
                } //fine colonna 0


                //colonna 1: totale inviti conseguenti
                i = 3;
                result = null;
                x = 0;
                //memorizzo la label
                headers[i] = "Totale inviti conseguenti";
                //questa colonna va mostrata
                render[i] = true;
                if (row < 0 || col < 0 || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    result =
                        am.callIndicazioni2livFunction(sesso, new BigDecimal(1), //tipo di calcolo
                                                            null, //esito
                                                            new BigDecimal(livello_esito),
                                                       //livello esito

                                                            new BigDecimal(((Number) cnf.getAttribute("Idcnfref1l")).doubleValue()), //giudia
            null, //adepre,
            null, null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
            round_ind_a, //round ind a
            round_org, //round org
            centro, //centro
            zona, //zona
            eta_da, //eta da
            eta_a, //eta a
            comune, //comune
            volontari, //volontaria
            coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
            /*20082507 MOD: aggiunta round inviti ai filtri*/
            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
            /*20082507 FINE MOD*/
            );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                    //   totali[i]+=x;
                } //fine colonna 1


                //una colonna per ogni esito di secondo livello
                iter1.first();
                iter1.previous();
                //
                while (iter1.hasNext()) {
                    Row e = iter1.next();
                    i++;
                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    result = null;
                    x = 0;
                    //memorizzo la label
                    headers[i] = (String) e.getAttribute("Esitoinvito");
                    //questa colonna va mostrata
                    render[i] = true;
                    result =
                        am.callIndicazioni2livFunction(sesso, new BigDecimal(1), //tipo di calcolo
                                                            (String) e.getAttribute("Codesitoinvito"), //esito
                                                            new BigDecimal(livello_esito),
                                                       //livello esito
                                                            new BigDecimal(((Number) cnf.getAttribute("Idcnfref1l")).doubleValue()), //giudia
                                                       null, //adepre,
                                                       null, null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                        return;

                    }
                    //  totali[i]+=x;

                }


            } //end while (sulle diagnosi positive


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter1 != null)
                iter1.closeRowSetIterator();


            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }

        }
    }


    public static void operatoriEndoscopie(int row, int col,int tipoop, String tpscr,
                                           String ulss, Date dal, Date al_escluso, BigDecimal centro,
                                           BigDecimal zona, String comune, BigDecimal round_ind_da,
                                           BigDecimal round_ind_a, BigDecimal round_org, BigDecimal volontari,
                                           BigDecimal eta_da, BigDecimal eta_a, boolean coorti, String sesso,
                                           /*20080709 MOD: aggiunta round inviti ai filtri*/
                                           BigDecimal round_inviti
                                           /*20080709 FINE MOD*/
                                           ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();


        ViewObject operatori = am.findViewObject("Stats_SoOpmedicoView1");

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;

        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            String where =
                "ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND " + "CODOP=" +
                ConfigurationConstants.CODICE_GASTROENTEROLOGO;
            operatori.setWhereClause(where);
            operatori.executeQuery();


            //id | nome | totale endoscopie | %incomplete | %con complicanze | totale soggetti | dr pe ca. | dr. per aar | dr per abr
            headers = new String[9];
            headers[0] = "Codice";
            headers[1] = "Endoscopista";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];


            //leggo le righe degli operatori
            iter = ViewHelper.getRowSetIterator(operatori, "operatori_iter");

            int j = iter.getRowCount();
            //per ogni operatore (una riga dell amatrice)
            while (iter.hasNext()) {

                //   int rowTotal=0;
                Stats_SoOpmedicoViewRow inv = (Stats_SoOpmedicoViewRow) iter.next();
                j--;
                if (row >= 0 && col >= 0 && row != j)
                    continue;


                Row r = null;

                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getIdop());
                    r.setAttribute(1, inv.getCognome() + " " + inv.getNome());
                }

                int x = 0;
                HashMap result = null;

                //  COLONNA COL NUMERO TOTALE DI ESAMI
                int i = 2;
                headers[i] = "N° endoscopie";
                render[i] = true;

                if ((row < 0 && col < 0) || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callEndoscopieFunction(sesso, new BigDecimal(0), //tipo calcolo
                                                       new BigDecimal(inv.getIdop().doubleValue()), //endoscopista
                                                       null, //endo number
                                                       null, //estensione
                                                       null, //complicanze
                                                       null, //conclusioni
                                                       ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                }

                int totale = x;
                //totali[i]+=x;

                //  COLONNA CON LA PERCENTUALE DI incomplete
                i = 3;
                headers[i] = "Endo. incomplete";
                render[i] = true;

                if ((row < 0 && col < 0) || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callEndoscopieFunction(sesso, new BigDecimal(0), //tipo calcolo
                                                       new BigDecimal(inv.getIdop().doubleValue()), //endoscopista
                                                       null, //endo number
                                                       new BigDecimal(0), //estensione
                                                       null, //complicanze
                                                       null, //conclusioni
                                                       ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,x+" ("+Double.toString(Stats_helper.percentage(totale,x))+"%)");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/


                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    //    totali[i]+=x;
                }


                //  COLONNA CON LA PERCENTUALE DI complicanze
                i = 4;
                headers[i] = "Endo. con complicanze";
                render[i] = true;

                if ((row < 0 && col < 0) || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callEndoscopieFunction(sesso, new BigDecimal(0), //tipo calcolo
                                                       new BigDecimal(inv.getIdop().doubleValue()), //endoscopista
                                                       null, //endo number
                                                       null, //estensione
                                                       new BigDecimal(1), //complicanze
                                                       null, //conclusioni
                                                       ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                    r.setAttribute(i,x+" ("+Double.toString(Stats_helper.percentage(totale,x))+"%)");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/


                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    //  totali[i]+=x;
                }


                //  COLONNA CON LA DETECTION RATE PER CANCRO:

                //Prima conto quanti soggetti hanno la 1° endo con l'endoscopista
                i = 5;
                headers[i] = "N° soggetti";
                render[i] = true;

                if ((row < 0 && col < 0) || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callEndoscopieFunction(sesso, new BigDecimal(1), //tipo calcolo
                                                       new BigDecimal(inv.getIdop().doubleValue()), //endoscopista
                                                       new BigDecimal(1), //endo number
                                                       null, //estensione
                                                       null, //complicanze
                                                       null, //conclusioni
                                                       ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                       round_ind_a, //round ind a
                                                       round_org, //round org
                                                       centro, //centro
                                                       zona, //zona
                                                       eta_da, //eta da
                                                       eta_a, //eta a
                                                       comune, //comune
                                                       volontari, //volontaria
                                                       coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                       /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                       round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                       /*20082507 FINE MOD*/
                                                       );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             totale=((Integer)result.get("howmany")).intValue();
             if(totale<0)
               throw new Exception("Errore di database");
           if(row<0 || col<0)
               r.setAttribute(i,Integer.toString(totale));

           */
                    if (row < 0 || col < 0) {
                        totale = ((Integer) result.get("howmany")).intValue();
                        if (totale < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, Integer.toString(totale));
                        totali[i] += totale;
                    }

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    // totali[i]+=totale;
                }

                //adeso conto i soggetti che hanno la 1° endo con quell'endoscopista e conclusione carcinoma
                i = 6;
                headers[i] = "DR per carcinoma";
                render[i] = true;

                if ((row < 0 && col < 0) || (row >= 0 && col >= 0 && row == j && col == i - 2)) {

                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callEndoscopieFunction(sesso, new BigDecimal(1), //tipo calcolo
                                                       new BigDecimal(inv.getIdop().doubleValue()), //endoscopista
                                                       new BigDecimal(1), //endo number
                                                       null, //estensione
                                                       null,
                                                  //complicanze
                                                       new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_CARCINOMA.doubleValue()), //conclusioni
                                                  ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                  round_ind_a, //round ind a
                                                  round_org, //round org
                                                  centro, //centro
                                                  zona, //zona
                                                  eta_da, //eta da
                                                  eta_a, //eta a
                                                  comune, //comune
                                                  volontari, //volontaria
                                                  coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                  /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                  round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                  /*20082507 FINE MOD*/
                                                  );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                     r.setAttribute(i,x+" ("+Double.toString(Stats_helper.percentage(totale,x))+"%)");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    //  totali[i]+=x;

                }


                //  COLONNA CON LA DETECTION RATE PER ADENOMI ALTO RISCHIO
                i = 7;
                headers[i] = "DR per adenomi ad alto rischio";
                render[i] = true;

                if ((row < 0 && col < 0) ||
                    (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri

                    //calcolo del numero di record che soddisfano i criteri
                    result =
am.callEndoscopieFunction(sesso, new BigDecimal(1), //tipo calcolo
                                                       new BigDecimal(inv.getIdop().doubleValue()), //endoscopista
                                                       new BigDecimal(1), //endo number
                                                       null, //estensione
                                                       null,
                          //complicanze
                                                       new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_HR.doubleValue()), //conclusioni
                          ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                          round_ind_a, //round ind a
                          round_org, //round org
                          centro, //centro
                          zona, //zona
                          eta_da, //eta da
                          eta_a, //eta a
                          comune, //comune
                          volontari, //volontaria
                          coorti ? new BigDecimal(1) : new BigDecimal(0),
                          /*20082507 MOD: aggiunta round inviti ai filtri*/
                          round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                          /*20082507 FINE MOD*/
                          );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,x+" ("+Double.toString(Stats_helper.percentage(totale,x))+"%)");

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);


                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    //   totali[i]+=x;
                }


                //  COLONNA CON LA DETECTION RATE PER ADENOMI BASSO RISCHIO
                i = 8;
                headers[i] = "DR per adenomi a basso rischio";
                render[i] = true;

                if ((row < 0 && col < 0) ||
                    (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri

                    //calcolo del numero di record che soddisfano i criteri
                    result =
am.callEndoscopieFunction(sesso, new BigDecimal(1), //tipo calcolo
                                                       new BigDecimal(inv.getIdop().doubleValue()), //endoscopista
                                                       new BigDecimal(1), //endo number
                                                       null, //estensione
                                                       null,
                          //complicanze
                                                       new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_LR.doubleValue()), //conclusioni
                          ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                          round_ind_a, //round ind a
                          round_org, //round org
                          centro, //centro
                          zona, //zona
                          eta_da, //eta da
                          eta_a, //eta a
                          comune, //comune
                          volontari, //volontaria
                          coorti ? new BigDecimal(1) : new BigDecimal(0),
                          /*20082507 MOD: aggiunta round inviti ai filtri*/
                          round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                          /*20082507 FINE MOD*/
                          );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, x + " (" + Double.toString(Stats_helper.percentage(totale, x)) + "%)");
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);


                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    // totali[i]+=x;
                }

            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);

                //calcolo anche l eprcentuali sui totali
                String[] tot = new String[totali.length];
                tot[2] = new Integer(totali[2]).toString();
                for (int h = 3; h < 5; h++) {
                    tot[h] = totali[h] + " (" + Double.toString(Stats_helper.percentage(totali[2], totali[h])) + "%)";
                }
                tot[5] = new Integer(totali[5]).toString();
                for (int h = 6; h < totali.length; h++) {
                    tot[h] = totali[h] + " (" + Double.toString(Stats_helper.percentage(totali[5], totali[h])) + "%)";
                }

                ADFContext.getCurrent().getSessionScope().remove("stats_inviti_esiti_totali");
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali_perc", tot);

            }
        }


    }


    public static void dati_app_colon(int row, int col,String tpscr, String ulss, Date dal,
                                      Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                      BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                      BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                      String sesso,
                                      /*20080708 MOD: aggiunta round inviti ai filtri*/
                                      BigDecimal round_inviti
        /*20080708 FINE MOD*/
    ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        // RowSetIterator iter=null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);


            //colonne:
            //id, label, soggetti,colonscopie complete,rxcolon,complicanze,cancro,AAR,ABR
            headers = new String[10];
            headers[0] = "Codice";
            headers[1] = "Eta'";
            headers[2] = "Soggetti aderenti al 2° liv.";
            headers[3] = "Colonscopie complete";
            headers[4] = "Rx colon";
            headers[5] = "Complicanze";
            headers[6] = "Carcinoma";
            headers[7] = "Adenoma alto rischio";
            headers[8] = "Adenoma basso rischio";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];


            //creo a priori il set di parametri per ogni colonna
            BigDecimal[][] col_params = { //solo screenati,colonscopia,rxcolon,complicanze,conclusioni

                //colonna 0: soggetti screenati
                { new BigDecimal(1), null, null, null, null },
                //colonna 1: soggetti con una colonscopia completa
                { new BigDecimal(0), new BigDecimal(1), null, null, null },
                //colonna 2: soggetti con una rx colon eseguito
                { new BigDecimal(0), null, new BigDecimal(1), null, null },
                //colonna 3: soggetti con una colonscopia con complicanze
                { new BigDecimal(0), null, null, new BigDecimal(1), null },
                //colonna 4: soggetti con carcinoma
                { new BigDecimal(0), null, null, null,
                  new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_CARCINOMA.doubleValue()) },
                //colonna 5: soggetti con adenoma lato rischio
                { new BigDecimal(0), null, null, null,
                  new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_HR.doubleValue()) },
                //colonna 6: soggetti con adenoma basso rischio
                { new BigDecimal(0), null, null, null,
                  new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_LR.doubleValue()) },

            };


            int j = ages.size();
            //per ogni classe d'eta' (una riga dell amatrice)
            for (int k = ages.size() - 1; k >= 0; k--) {

                //ottengo i confini della classe d'eta'
                BigDecimal[] borders = (BigDecimal[]) ages.elementAt(k);
                String label = null;
                //l'intestazione dipende dalla situazione
                //prima riga
                if (borders[0] == null)
                    label = "<" + (borders[1].intValue() + 1);
                //ultima riga
                else if (borders[1] == null)
                    label = borders[0].intValue() + "+";
                else //righe intermedie
                    label = borders[0].intValue() + "-" + borders[1].intValue();


                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date());
                    r.setAttribute(1, label);
                }

                int x = 0;
                HashMap result = null;
                //CICLO SULLE COLONNE
                for (int i = 2; i < col_params.length + 2; i++) {

                    if (row == j && col >= 0 && col != i - 2)
                        continue;

                    render[i] = true;
                    //  System.out.println("i="+i+", conclusioni="+col_params[i-2][4]);
                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callDatiApprofondimentiCOFunction(col_params[i - 2][0], //solo screenati
                                                                  col_params[i - 2][1], //colonscopia
                                                                  col_params[i - 2][2], //rxcolon
                                                                  col_params[i - 2][3], //complicanze
                                                                  col_params[i - 2][4], //conclusioni
                                                                  sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                                  round_ind_a, //round ind a
                                                                  round_org, //round org
                                                                  centro, //centro
                                                                  zona, //zona
                                                                  borders[0], //eta da
                                                                  borders[1], //eta a
                                                                  comune, //comune
                                                                  volontari, //volontaria
                                                                  coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                                  /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                                  round_inviti,
                                                             (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                             /*20082507 FINE MOD*/
                                                             );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    //  totali[i]+=x;
                    //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }

        }
    }


    public static void detection_rate_CO(int row, int col,String tpscr, String ulss, Date dal,
                                         Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                         BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                         BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                         String sesso,
                                         /*20080708 MOD: aggiunta round inviti ai filtri*/
                                         BigDecimal round_inviti
        /*20080708 FINE MOD*/
    )

        throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        // RowSetIterator iter=null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        //elenco delle dipendenze. Per ogni colonna i:
        //se dep[i]=0, calcolo l'indicatore e basta
        //se dep[i]>0, calcolo l'indicatore e uso tale dato anche per calcolar
        //          la detection rate e  impostare la colonn dep[i]
        //se dep[i]>0 non calcolo niente (si tartta di una detection rate, e' gia' impostata)
        int[] dep = { 0, 0, 0, 5, 0, -1, 7, -1, 9, -1 };

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);


            //colonne:

            headers = new String[10];
            headers[0] = "Codice";
            headers[1] = "Eta'";
            headers[2] = "Screenati";
            headers[3] = "Soggetti con cancro";
            headers[4] = "Soggetti con adenoma cancerizzato";
            headers[5] = "Detection rate per cancro (x1000)";
            headers[6] = "Soggetti con adenoma AR";
            headers[7] = "Detection rate per adenoma AR (x1000)";
            headers[8] = "Soggetti con adenoma BR";
            headers[9] = "Detection rate per adenoma BR (x1000)";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];


            //creo a priori il set di parametri per ogni colonna
            BigDecimal[][] col_params = { //conclusioni, istologia del cancro

                //colonna 0: gli screenati
                null,
                //colonna 1: cancro
                { new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_CARCINOMA.doubleValue()), null },
                //colonna 2: adenoma cancerizzato
                { new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_CARCINOMA.doubleValue()),
                  new BigDecimal(ConfigurationConstants.CODICE_ISTO_ADENOMA_C.doubleValue()) },
                //colonna 3: dr per cancro
                null,
                //colonna 4: adenoma alto rischio
                { new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_HR.doubleValue()), null },
                //colonn 5: dr per adenoma alto rischio
                null,
                //colonna 6: soggetti con adenoma basso rischio
                { new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_LR.doubleValue()), null },
                //colonna 7: dr per adenoma basso rischio
                null
            };


            int j = ages.size();
            //per ogni classe d'eta' (una riga dell amatrice)
            for (int k = ages.size() - 1; k >= 0; k--) {

                //ottengo i confini della classe d'eta'
                BigDecimal[] borders = (BigDecimal[]) ages.elementAt(k);
                String label = null;
                //l'intestazione dipende dalla situazione
                //prima riga
                if (borders[0] == null)
                    label = "<" + (borders[1].intValue() + 1);
                //ultima riga
                else if (borders[1] == null)
                    label = borders[0].intValue() + "+";
                else //righe intermedie
                    label = borders[0].intValue() + "-" + borders[1].intValue();


                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date());
                    r.setAttribute(1, label);
                }

                int x = 0;
                HashMap result = null;
                int screenati = 0;
                //prima di tutto calcolo gli screenati e memorizzo il dato
                int i = 2;

                render[i] = true;
                if ((row < 0 && col < 0) || (row == j && col == i - 2)) {
                    result =
                        am.callCoorteScreenatiNewFunction(sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da,
                                                          round_ind_a, round_org, centro, zona, borders[0], borders[1],
                                                          comune, volontari,
                                                          coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                          /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                          round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                          /*20082507 FINE MOD*/
                                                          );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 MOD
                    screenati=((Integer)result.get("howmany")).intValue();
                    //icremento gli screenati totali
                    totali[i]+=screenati;


                   if(x<0)
                     throw new Exception("Errore di database");
                     if(row<0 || col<0){
                         r.setAttribute(i,new Integer(screenati));
                    }

                    */
                    if (row < 0 || col < 0) {
                        screenati = ((Integer) result.get("howmany")).intValue();

                        if (screenati < 0)
                            throw new Exception("Errore di database");

                        //icremento gli screenati totali
                        totali[i] += screenati;
                        r.setAttribute(i, new Integer(screenati));
                    }

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                }


                //CICLO SULLE COLONNE
                for (i = 3; i < col_params.length + 2; i++) {

                    //devo calcolare anche se l'utente ha selzionato la dipendenza
                    if (row == j && col >= 0 && col != i - 2 && col != dep[i] - 2)
                        continue;

                    render[i] = true;
                    //se si tratta di una detection rate nondevo fare nulla
                    if (dep[i] < 0)
                        continue;


                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callDetectionRateCOFunction(col_params[i - 2][0], //conclusioni
                                                            col_params[i - 2][1], //istologia
                                                            sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                            round_ind_a, //round ind a
                                                            round_org, //round org
                                                            centro, //centro
                                                            zona, //zona
                                                            borders[0], //eta da
                                                            borders[1], //eta a
                                                            comune, //comune
                                                            volontari, //volontaria
                                                            new BigDecimal(1), //coorti,
                                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                            /*20082507 FINE MOD*/
                                                            );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 MOD
             x=((Integer)result.get("howmany")).intValue();
             //incremento il totale
             totali[i]+=x;
          //   System.out.println(headers[i]+":"+x);


             if(x<0)
               throw new Exception("Errore di database");
               if(row<0 || col<0){
                   r.setAttribute(i,new Integer(x));
                   //se c'e' una detection rate che dipende da questo dato
                   //la calcolo e la imposto
                   if(dep[i]>0)
                     r.setAttribute(dep[i],Double.toString(Stats_helper.x_tage(1000,screenati,x))+"‰");

               }*/
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        //incremento il totale
                        totali[i] += x;
                        r.setAttribute(i, new Integer(x));
                        //se c'e' una detection rate che dipende da questo dato
                        //la calcolo e la imposto
                        if (dep[i] > 0)
                            r.setAttribute(dep[i], Double.toString(Stats_helper.x_tage(1000, screenati, x)) + "‰");

                    } else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }


                }


            }

            //calcolo anche l eprcentuali sui totali
            if (row < 0 || col < 0) {

                String[] tot = new String[totali.length];
                for (int h = 2; h < totali.length; h++) {
                    //se e' una detection rate vado avanti
                    if (dep[h] < 0)
                        continue;

                    //altrimenti imposto il totale
                    tot[h] = Integer.toString(totali[h]);

                    //se c'e' anche un adipendenza imposto anche la detection rate di questa
                    if (dep[h] > 0)
                        tot[dep[h]] = Double.toString(Stats_helper.x_tage(1000, totali[2], totali[h])) + "‰";

                }

                ADFContext.getCurrent().getSessionScope().remove("stats_inviti_esiti_totali");
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali_perc", tot);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);


            }

        }
    }


    public static void detection_rate_MA(int row, int col,String tpscr, String ulss, Date dal,
                                         Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                         BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                         BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a,
                                         /*20080708 MOD: aggiunta round inviti ai filtri*/
                                         BigDecimal round_inviti
                                         /*20080708 FINE MOD*/
                                         ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;
        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);


            //colonne:

            headers = new String[10];
            headers[0] = "Codice";
            headers[1] = "Eta'";
            headers[2] = "Screenati";
            headers[3] = "Soggetti con cancro";
            headers[4] = "Detection rate per cancro (x1000)";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            render[2] = true;
            render[3] = true;
            render[4] = true;
            totali = new int[headers.length];

            int j = ages.size();
            //per ogni classe d'eta' (una riga dell amatrice)
            for (int k = ages.size() - 1; k >= 0; k--) {
                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;


                //ottengo i confini della classe d'eta'
                BigDecimal[] borders = (BigDecimal[]) ages.elementAt(k);
                String label = null;
                //l'intestazione dipende dalla situazione
                //prima riga
                if (borders[0] == null)
                    label = "<" + (borders[1].intValue() + 1);
                //ultima riga
                else if (borders[1] == null)
                    label = borders[0].intValue() + "+";
                else //righe intermedie
                    label = borders[0].intValue() + "-" + borders[1].intValue();


                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date());
                    r.setAttribute(1, label);
                }

                int x = 0;
                HashMap result = null;
                int screenati = 0;
                //prima di tutto calcolo gli screenati e memorizzo il dato
                int i = 2;


                if ((row < 0 && col < 0) || (row == j && col == i - 2)) {
                    result =
                        am.callCoorteScreenatiNewFunction("F", ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, round_ind_a,
                                                          round_org, centro, zona, borders[0], borders[1], comune,
                                                          volontari, new BigDecimal(1),
                                                          /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                          round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                          /*20082507 FINE MOD*/
                                                          )

                    ;
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
            screenati=((Integer)result.get("howmany")).intValue();
             //icremento gli screenati totali
                    totali[i]+=screenati;
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(screenati));

                             */
                    if (row < 0 || col < 0) {
                        screenati = ((Integer) result.get("howmany")).intValue();
                        if (screenati < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(screenati));
                        //icremento gli screenati totali
                        totali[i] += screenati;
                    }

                    /*20080725 fine mod*/

                    else {
                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                }


                i = 3;
                if ((row < 0 && col < 0) || (row == j && col >= i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callDetectionRateMAFunction(null, //lettore
                                                            ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                            round_ind_a, //round ind a
                                                            round_org, //round org
                                                            centro, //centro
                                                            zona, //zona
                                                            borders[0], //eta da
                                                            borders[1], //eta a
                                                            comune, //comune
                                                            volontari, //volontaria
                                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                            /*20082507 FINE MOD*/
                                                            ); //coorti
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 MOD
             x=((Integer)result.get("howmany")).intValue();
             //incremento il totale
             totali[i]+=x;

             if(x<0)
               throw new Exception("Errore di database");
               if(row<0 || col<0){
                   r.setAttribute(i,new Integer(x));
                   //calcolo ed imposto la detection rate
                   r.setAttribute(i+1,Double.toString(Stats_helper.x_tage(1000,screenati,x))+"‰");
               }
               */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        //incremento il totale
                        totali[i] += x;
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        //calcolo ed imposto la detection rate
                        r.setAttribute(i + 1, Double.toString(Stats_helper.x_tage(1000, screenati, x)) + "‰");


                    }

                    /*20080725 fine mod*/
                    else {
                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);
                        return;
                    }
                }
            }

            //calcolo anche l eprcentuali sui totali
            if (row < 0 || col < 0) {
                String[] tot = new String[totali.length];
                tot[2] = Integer.toString(totali[2]);
                tot[3] = Integer.toString(totali[3]);
                tot[4] = Double.toString(Stats_helper.x_tage(1000, totali[2], totali[3])) + "‰";

                ADFContext.getCurrent().getSessionScope().remove("stats_inviti_esiti_totali");
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali_perc", tot);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);

            }

        }
    }

    public static void trattamenti_chirurgici(int row, int col,String tpscr, String ulss,
                                              Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona,
                                              String comune, BigDecimal round_ind_da, BigDecimal round_ind_a,
                                              BigDecimal round_org, BigDecimal volontari, BigDecimal eta_da,
                                              BigDecimal eta_a, boolean coorti, String sesso, String lesione_,
                                              /*20080709 MOD: aggiunta round inviti ai filtri*/
                                              BigDecimal round_inviti
                                              /*20080709 FINE MOD*/
                                              ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Stats_SoCnfRef2livView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        // RowSetIterator iter=null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;
        RowSetIterator iter = null;
        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);

            //trovo tutti motivi di inesecuzione
            vo.setWhereClause("ULSS='" + ulss + "' and TPSCR='" + tpscr + "' and GRUPPO='" +
                              ConfigurationConstants.NOME_GRUPPO_INTMIE_2L + "' and idcnfref2l>0");
            vo.setOrderByClause("ORDINE");
            vo.executeQuery();
            iter = ViewHelper.getRowSetIterator(vo, "intmie");
            //colonne:
            //id, label, soggetti,interventi eseguiti, interventi non eseguiti+
            // 1 coonna per ogni motivo di inesecuzione
            headers = new String[5 + vo.getRowCount()];
            headers[0] = "Codice";
            headers[1] = "Eta'";
            headers[2] = "Soggetti inviati ad intervento";
            headers[3] = "Intervento eseguito";
            headers[4] = "Intervento non eseguito";

            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];


            //creo a priori il set di parametri per le prim 3 colonne
            Object[][] col_params = { //soggetti inviati al 3 liv,esecuzione int

                //colonna 0:soggetti invitai al 3 livello
                { new BigDecimal(1), null },
                //colonna 1: intervento eseguito
                { new BigDecimal(0),
                  ConfigurationConstants.CODICE_INTVAL_EX_QUI + " " + ConfigurationConstants.CODICE_INTVAL_EX_ALTROVE },
                //colonna 2: interveto non eseguito
                { new BigDecimal(0), ConfigurationConstants.CODICE_INTVAL_NON_EX.toString() },

            };

            BigDecimal lesione = null;
            //casi del colon
            if ("carcinoma".equals(lesione_))
                lesione = new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_CARCINOMA.doubleValue());
            else if ("adenoma alto rischio".equals(lesione_))
                lesione = new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_HR.doubleValue());
            else if ("adenoma basso rischio".equals(lesione_))
                lesione = new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_LR.doubleValue());
            //casi del mammografico (0,1,2)
            else {
                try {
                    lesione = new BigDecimal(lesione_);
                } catch (Exception e) {
                    //lesione =new BigDecimal(0);
                    lesione = null;
                }
            }


            int j = ages.size();
            //per ogni classe d'eta' (una riga dell amatrice)
            for (int k = ages.size() - 1; k >= 0; k--) {

                //ottengo i confini della classe d'eta'
                BigDecimal[] borders = (BigDecimal[]) ages.elementAt(k);
                String label = null;
                //l'intestazione dipende dalla situazione
                //prima riga
                if (borders[0] == null)
                    label = "<" + (borders[1].intValue() + 1);
                //ultima riga
                else if (borders[1] == null)
                    label = borders[0].intValue() + "+";
                else //righe intermedie
                    label = borders[0].intValue() + "-" + borders[1].intValue();


                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date());
                    r.setAttribute(1, label);
                }

                int x = 0;
                HashMap result = null;

                //CICLO SULLE COLONNE: l eprime 3
                int i;
                for (i = 2; i < col_params.length + 2; i++) {

                    if (row == j && col >= 0 && col != i - 2)
                        continue;

                    render[i] = true;

                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callChirurgieCO((BigDecimal) col_params[i - 2][0], //solo screenati
                                                (String) col_params[i - 2][1], //esecuzione
                                                null, //motivo
                                                new BigDecimal(1), //invio 3 liv
                                                lesione, //lesione
                                                sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                round_ind_a, //round ind a
                                                round_org, //round org
                                                centro, //centro
                                                zona, //zona
                                                borders[0], //eta da
                                                borders[1], //eta a
                                                comune, //comune
                                                volontari, //volontaria
                                                coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                /*20082507 FINE MOD*/
                                                );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    //  totali[i]+=x;
                    //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                } //fine del cilco sulle prim 3 colonne


                //CICLO SULLE COLONNE: i motivi di inesecuzione

                iter.first();
                iter.previous();
                Row r2 = null;
                while ((r2 = iter.next()) != null) {

                    if (row == j && col >= 0 && col != i - 2) {
                        i++;
                        continue;
                    }

                    render[i] = true;
                    headers[i] = (String) r2.getAttribute("Descrizione");
                    //  System.out.println("i="+i+", conclusioni="+col_params[i-2][4]);
                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callChirurgieCO(new BigDecimal(0), //solo screenati
                                                ConfigurationConstants.CODICE_INTVAL_NON_EX.toString(), //esecuzione
                                                new BigDecimal(((Number) r2.getAttribute("Idcnfref2l")).doubleValue()), //motivo
                                                new BigDecimal(1), //invio 3 liv
                                                lesione, //lesione
                                                sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                round_ind_a, //round ind a
                                                round_org, //round org
                                                centro, //centro
                                                zona, //zona
                                                borders[0], //eta da
                                                borders[1], //eta a
                                                comune, //comune
                                                volontari, //volontaria
                                                coorti ? new BigDecimal(1) : new BigDecimal(0),
                                                /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                /*20082507 FINE MOD*/
                                                );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i++] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Intervento non eseguito per " +
                                                                              headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    //    totali[i++]+=x;
                    //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }

        }
    }

    public static void dimensioni_linfonodi(int row, int col,String tpscr, String ulss,
                                            Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona,
                                            String comune, BigDecimal round_ind_da, BigDecimal round_ind_a,
                                            BigDecimal round_org, BigDecimal volontari, BigDecimal eta_da,
                                            BigDecimal eta_a, boolean coorti, String sesso, String lesione_,
                                            /*20080708 MOD: aggiunta round inviti ai filtri*/
                                            BigDecimal round_inviti
                                            /*20080708 FINE MOD*/
                                            ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject pt = am.findViewObject("Stats_SoStatsAggregazView1");
        ViewObject pn = am.findViewObject("Stats_SoStatsAggregazView2");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;
        RowSetIterator iter = null;
        RowSetIterator iter2 = null;


        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            //trovo i gruppi dei pt per le righe
            pt.setWhereClause("TPSCR='" + tpscr + "' and TIPO='" + Stats_helper.GRUPPI_PT[0] + "'");
            //vo.setOrderByClause("ORDINE");
            pt.executeQuery();
            iter = ViewHelper.getRowSetIterator(pt, "pt_aggregaz");

            //trovo i gruppi dei pn per le colonne
            pn.setWhereClause("TPSCR='" + tpscr + "' and TIPO='" + Stats_helper.GRUPPI_PN[0] + "'");
            //vo.setOrderByClause("ORDINE");
            pn.executeQuery();
            iter2 = ViewHelper.getRowSetIterator(pn, "pn_aggregaz");

            //colonne:
            //id, label, una per ogni pn, totale
            headers = new String[3 + pn.getRowCount()];
            headers[0] = "Codice";
            headers[1] = "pTN";

            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];


            BigDecimal lesione = null;
            if ("carcinoma".equals(lesione_))
                lesione = new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_CARCINOMA.doubleValue());
            else if ("adenoma alto rischio".equals(lesione_))
                lesione = new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_HR.doubleValue());
            else if ("adenoma basso rischio".equals(lesione_))
                lesione = new BigDecimal(ConfigurationConstants.CODICE_CO2LIV_ADENOMA_LR.doubleValue());


            int j = iter.getRowCount();
            Row pt_r = null;
            //per ogni classe d'eta' (una riga dell amatrice)
            while ((pt_r = iter.next()) != null) {

                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date());
                    r.setAttribute(1, pt_r.getAttribute("Descrizione"));
                }

                int x = 0;
                HashMap result = null;

                //CICLO SULLE COLONNE:
                int i = 2;
                Row pn_r = null;
                iter2.first();
                iter2.previous();
                while ((pn_r = iter2.next()) != null) {

                    if (row == j && col >= 0 && col != i - 2) {
                        i++;
                        continue;
                    }

                    render[i] = true;
                    headers[i] = (String) pn_r.getAttribute("Descrizione");


                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callPnPt((String) pn_r.getAttribute("Aggregaz"), //pn
                                         (String) pt_r.getAttribute("Aggregaz"), //pt
                                         Stats_helper.GRUPPI_PN[0], Stats_helper.GRUPPI_PT[0], new BigDecimal(0), //aggregato
                                         lesione, //lesione
                                         sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                         round_ind_a, //round ind a
                                         round_org, //round org
                                         centro, //centro
                                         zona, //zona
                                         eta_da, //eta da
                                         eta_a, //eta a
                                         comune, //comune
                                         volontari, //volontaria
                                         coorti ? new BigDecimal(1) : new BigDecimal(0),
                                         /*20082507 MOD: aggiunta round inviti ai filtri*/
                                         round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                         /*20082507 FINE MOD*/
                                         );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i++] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[i]);

                        return;

                    }


                    //incremento il totale
                    // totali[i++]+=x;
                    //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");

                } //fine del cilco sulle  colonne


                //colonna col totale
                if ((row < 0 || col < 0) || (row == j && col == i - 2)) {

                    render[i] = true;
                    headers[i] = "Totali";

                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callPnPt(null, //pn
                                         (String) pt_r.getAttribute("Aggregaz"), //pt
                                         null, Stats_helper.GRUPPI_PT[0], new BigDecimal(0), //aggregato
                                         lesione, //lesione
                                         sesso, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                         round_ind_a, //round ind a
                                         round_org, //round org
                                         centro, //centro
                                         zona, //zona
                                         eta_da, //eta da
                                         eta_a, //eta a
                                         comune, //comune
                                         volontari, //volontaria
                                         coorti ? new BigDecimal(1) : new BigDecimal(0),
                                         /*20082507 MOD: aggiunta round inviti ai filtri*/
                                         round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                         /*20082507 FINE MOD*/
                                         );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i++] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Totali (tutti gli N)");

                        return;

                    }


                    //incremento il totale
                    //  totali[i++]+=x;
                }

            } //fine iterazione sulle righe


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter2 != null)
                iter2.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }

        }
    }


    public static void diagnosi1livMA(int row, int col,String tpscr, String ulss, Date dal,
                                      Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                      BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                      BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                      /*20080707 MOD: aggiunta round inviti ai filtri*/
                                      BigDecimal round_inviti
                                      /*20080707 FINE MOD*/
                                      ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject colpes = am.findViewObject("Stats_SoCnfRef1livMXEST1View1");

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);
            iter = ViewHelper.getRowSetIterator(colpes, "colpes_iter");

            //UNA COLONNA PER ESITO, UNA PER I TOTALI E DUE COME LABL DI RIGA
            headers = new String[ages.size() + 3];
            headers[0] = "Codice";
            headers[1] = "Diagnosi di 1° livello";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];
            int j = iter.getRowCount();
            //per ogni classe di diagnosi (una riga dell amatrice)
            while (iter.hasNext()) {

                int rowTotal = 0;
                Row inv = iter.next();
                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getAttribute("Idcnfref1l"));
                    r.setAttribute(1, inv.getAttribute("Descrizione"));
                }
                //contatore per le colonne
                int i = 2;
                // System.out.println(inv.getAttribute("Idcnfref2l")+": "+inv.getAttribute("Descrizione"));


                int x = 0;
                HashMap result = null;

                for (i = 2; i < ages.size() + 2; i++) {


                    //ottengo i confini della classe d'eta' (cioe' le colonne)
                    BigDecimal[] borders = (BigDecimal[]) ages.elementAt(i - 2);

                    //se sto cercando le persone e non sono nell ariga desiderata continuo

                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    //l'intestazione dipende dalla situazione
                    //prima riga
                    if (borders[0] == null)
                        headers[i] = "<" + (borders[1].intValue() + 1);
                    //ultima riga
                    else if (borders[1] == null)
                        headers[i] = borders[0].intValue() + "+";
                    else //righe intermedie
                        headers[i] = borders[0].intValue() + "-" + borders[1].intValue();

                    //questa colonna va mostrata
                    render[i] = true;


                    result =
                        (HashMap) am.callDiagnosi1livMAFunction(new BigDecimal(0), //letture
                                                                     null,
                                                                //lettore
                                                                     new BigDecimal(((Number) inv.getAttribute("Idcnfref1l")).doubleValue()), //esito
                                    null, //sugg)
                                    ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                    round_ind_a, //round ind a
                                    round_org, //round org
                                    centro, //centro
                                    zona, //zona
                                    borders[0], //eta da
                                    borders[1], //eta a
                                    comune, //comune
                                    volontari, coorti ? new BigDecimal(1) : new BigDecimal(0),
                                    /*20082507 MOD: aggiunta round inviti ai filtri*/
                                    round_inviti,

                                    (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                    );


                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;
                        rowTotal += x;
                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                              "Diagnosi: " + headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    /*   totali[i]+=x;
                       //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                         rowTotal+=x;*/


                }

                //totale di riga

                //memorizzo la label
                headers[i] = "Tutti";
                //questa colonna va mostrata
                render[i] = true;

                result =
                    (HashMap) am.callDiagnosi1livMAFunction(new BigDecimal(0), //letture
                                                                 null,
                                                            //lettore
                                                                 new BigDecimal(((Number) inv.getAttribute("Idcnfref1l")).doubleValue()), //esito
                                null, //sugg)
                                ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                round_ind_a, //round ind a
                                round_org, //round org
                                centro, //centro
                                zona, //zona
                                null, //eta da
                                null, //eta a
                                comune, //comune
                                volontari, coorti ? new BigDecimal(1) : new BigDecimal(0),
                                /*20082507 MOD: aggiunta round inviti ai filtri*/
                                round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                /*20082507 FINE MOD*/
                                );


                if (result.get("errore") != null)
                    throw new Exception((String) result.get("errore"));

                /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0){
                             r.setAttribute(i,new Integer(x));
                             totali[i]+=x;
                             }
                             */
                if (row < 0 || col < 0) {
                    x = ((Integer) result.get("howmany")).intValue();
                    if (x < 0)
                        throw new Exception("Errore di database");
                    r.setAttribute(i, new Integer(x));
                    totali[i] += x;

                }

                /*20080725 fine mod*/
                else {


                    String inClause = (String) result.get("query");
                    Cnf_selectionBean c_bean =
                        (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                    c_bean.setInClause(inClause);

                    //imposto i dati
                    ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                          headers[1] + ": " +
                                                                          ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                    ADFContext.getCurrent().getSessionScope().put("colLabel", "Diagnosi: " + headers[col + 2]);

                    return;

                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }
            /*  if(ages!=null)
        ADFContext.getCurrent().getSessionScope().put("stats_total_col_index",new Integer(ages.size()));
        */
        }
    }


    public static void dati_app_mammo(int row, int col,String tpscr, String ulss, Date dal,
                                      Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                      BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                      BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                      /*20080708 MOD: aggiunta round inviti ai filtri*/
                                      BigDecimal round_inviti
                                      /*20080708 FINE MOD*/
                                      ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();

        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        // RowSetIterator iter=null;


        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);


            //colonne:
            //id, label, soggetti,esami non invasivi,solo cito, micoristologia, operate
            headers = new String[7];
            headers[0] = "Codice";
            headers[1] = "Eta'";
            headers[2] = "Soggetti aderenti al 2° liv.";
            headers[3] = "Esami non invasivi";
            headers[4] = "Solo citologia";
            headers[5] = "Microistologia";
            headers[6] = "Donne operate";

            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            render[2] = true;
            render[3] = true;
            render[4] = true;
            render[5] = true;
            render[6] = true;
            totali = new int[headers.length];


            int j = ages.size();
            //per ogni classe d'eta' (una riga dell amatrice)
            for (int k = ages.size() - 1; k >= 0; k--) {
                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                //ottengo i confini della classe d'eta'
                BigDecimal[] borders = (BigDecimal[]) ages.elementAt(k);
                String label = null;
                //l'intestazione dipende dalla situazione
                //prima riga
                if (borders[0] == null)
                    label = "<" + (borders[1].intValue() + 1);
                //ultima riga
                else if (borders[1] == null)
                    label = borders[0].intValue() + "+";
                else //righe intermedie
                    label = borders[0].intValue() + "-" + borders[1].intValue();


                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date());
                    r.setAttribute(1, label);
                }

                int x = 0;
                HashMap result = null;
                //CICLO SULLE COLONNE
                for (int i = 2; i < headers.length; i++) {

                    if (row == j && col >= 0 && col != i - 2)
                        continue;


                    //calcolo del numero di record che soddisfano i criteri
                    result =
                        am.callDatiApprofondimentiMAFunction(new BigDecimal(i - 2),
                                                             //tipo
                                                                  new BigDecimal(ConfigurationConstants.CODICE_NON_EXEC_ALTRO.doubleValue()), //soglia exec
                                      new BigDecimal(ConfigurationConstants.CODICE_INTVAL_NON_EX.doubleValue()), //soglia_exec_int
                                      ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                      round_ind_a, //round ind a
                                      round_org, //round org
                                      centro, //centro
                                      zona, //zona
                                      borders[0], //eta da
                                      borders[1], //eta a
                                      comune, //comune
                                      volontari, //volontaria
                                      coorti ? new BigDecimal(1) : new BigDecimal(0),
                                      /*20082507 MOD: aggiunta round inviti ai filtri*/
                                      round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                      /*20082507 FINE MOD*/
                                      );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }


                    //incremento il totale
                    //  totali[i]+=x;
                    //  System.out.println("Colonna "+i+" parziale "+totali[i]+" (aggiunto "+x+")");
                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }

        }
    }


    public static void indicazioniMA(int row, int col,String tpscr, String ulss, Date dal,
                                     Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                     BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                     BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                     int livello_esito, BigDecimal giudia,
                                     /*20080708 MOD: aggiunta round inviti ai filtri*/
                                     BigDecimal round_inviti
                                     /*20080708 FINE MOD*/
                                     ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject esiti = am.findViewObject("Stats_SoCnfEsitoinvitoView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter1 = null;

        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        Vector ages = null;


        try {
            //ripulisco i dati di conti precdenti, solo se non sto selezionando persone
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);

            if (eta_da == null || eta_a == null)
                throw new Exception("Per il calcolo di questo indicatore e' necessario definire i limite d'eta'");

            ages = Stats_helper.getAgeClasses(eta_da.intValue(), eta_a.intValue(), 5);


            //filtro gli esiti del livello richiesto
            esiti.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + AccessManager.CODREGIONALE + "' AND LIVESITO=" +
                                 livello_esito);
            esiti.executeQuery();

            //UNA COLONNA PER ESITO, UNA PER I TOTALI ed una per gli inviti totali E DUE COME LABL DI RIGA
            headers = new String[esiti.getRowCount() + 4];
            headers[0] = "Codice";
            headers[1] = "Classe d'et\u00e0";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];

            iter1 = ViewHelper.getRowSetIterator(esiti, "esiti_stats_iter");


            int j = ages.size();
            //per ogni classe d'eta' (una riga dell amatrice)
            for (int k = ages.size() - 1; k >= 0; k--) {
                j--;
                //se sto cercando le persone e non sono nell ariga desiderata continuo
                if (row >= 0 && col >= 0 && row != j)
                    continue;

                //ottengo i confini della classe d'eta'
                BigDecimal[] borders = (BigDecimal[]) ages.elementAt(k);
                String label = null;
                //l'intestazione dipende dalla situazione
                //prima riga
                if (borders[0] == null)
                    label = "<" + (borders[1].intValue() + 1);
                //ultima riga
                else if (borders[1] == null)
                    label = borders[0].intValue() + "+";
                else //righe intermedie
                    label = borders[0].intValue() + "-" + borders[1].intValue();


                Row r = null;
                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, new Date());
                    r.setAttribute(1, label);

                }


                //colonna 0: totale test con tale esito
                int i = 2;
                HashMap result = null;
                int x = 0;
                //memorizzo la label
                headers[i] = "Totale test";
                //questa colonna va mostrata
                render[i] = true;
                if (row < 0 || col < 0 || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    result = am.callIndicazioni2livFunction(null, new BigDecimal(0), //tipo di calcolo
                                                            null, //esito
                                                            new BigDecimal(livello_esito), //livello esito
                                                            giudia, //giudia
                                                            null, //adepre,
                                                            null, null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                            round_ind_a, //round ind a
                                                            round_org, //round org
                                                            centro, //centro
                                                            zona, //zona
                                                            borders[0], //eta da
                                                            borders[1], //eta a
                                                            comune, //comune
                                                            volontari, //volontaria
                                                            coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                            /*20082507 FINE MOD*/
                                                            );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel", headers[1] + ": " + label);
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }

                    //  totali[i]+=x;
                } //fine colonna 0


                //colonna 1: totale inviti conseguenti
                i = 3;
                result = null;
                x = 0;
                //memorizzo la label
                headers[i] = "Totale inviti conseguenti";
                //questa colonna va mostrata
                render[i] = true;
                if (row < 0 || col < 0 || (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    result = am.callIndicazioni2livFunction(null, new BigDecimal(1), //tipo di calcolo
                                                            null, //esito
                                                            new BigDecimal(livello_esito), //livello esito

                                                            giudia, //giudia
                                                            null, //adepre,
                                                            null, null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                            round_ind_a, //round ind a
                                                            round_org, //round org
                                                            centro, //centro
                                                            zona, //zona
                                                            borders[0], //eta da
                                                            borders[1], //eta a
                                                            comune, //comune
                                                            volontari, //volontaria
                                                            coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                            /*20082507 FINE MOD*/
                                                            );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel", headers[1] + ": " + label);
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                    //totali[i]+=x;
                } //fine colonna 1


                //una colonna per ogni esito di secondo livello
                iter1.first();
                iter1.previous();
                //
                while (iter1.hasNext()) {
                    Row e = iter1.next();
                    i++;
                    if (row >= 0 && col >= 0 && (row != j || col != i - 2))
                        continue;

                    result = null;
                    x = 0;
                    //memorizzo la label
                    headers[i] = (String) e.getAttribute("Esitoinvito");
                    //questa colonna va mostrata
                    render[i] = true;
                    result = am.callIndicazioni2livFunction(null, new BigDecimal(1), //tipo di calcolo
                                                            (String) e.getAttribute("Codesitoinvito"), //esito
                                                            new BigDecimal(livello_esito), //livello esito
                                                            giudia, //giudia
                                                            null, //adepre,
                                                            null, null, ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                            round_ind_a, //round ind a
                                                            round_org, //round org
                                                            centro, //centro
                                                            zona, //zona
                                                            borders[0], //eta da
                                                            borders[1], //eta a
                                                            comune, //comune
                                                            volontari, //volontaria
                                                            coorti ? new BigDecimal(1) : new BigDecimal(0), //coorti
                                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                            /*20082507 FINE MOD*/
                                                            );

                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel", headers[1] + ": " + label);
                        ADFContext.getCurrent().getSessionScope().put("colLabel", "Esito: " + headers[col + 2]);

                        return;

                    }
                    //  totali[i]+=x;

                }


            } //end while (sulle diagnosi positive


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter1 != null)
                iter1.closeRowSetIterator();


            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }

        }
    }

    public static void operatoriMammo(int row, int col,int tipoop, String tpscr, String ulss,
                                      Date dal, Date al_escluso, BigDecimal centro, BigDecimal zona, String comune,
                                      BigDecimal round_ind_da, BigDecimal round_ind_a, BigDecimal round_org,
                                      BigDecimal volontari, BigDecimal eta_da, BigDecimal eta_a, boolean coorti,
                                      /*20080709 MOD: aggiunta round inviti ai filtri*/
                                      BigDecimal round_inviti
                                      /*20080709 FINE MOD*/
                                      ) throws Exception {
        if (coorti)
            operatoriMammoCoorti(row, col, tipoop, tpscr, ulss, dal,al_escluso, centro, zona, comune,
                                 round_ind_da, round_ind_a, round_org, volontari, eta_da, eta_a,
                                 /*20080709 MOD: aggiunta round inviti ai filtri*/
                                 round_inviti
                                 /*20080709 FINE MOD*/
                                 );


        else
            operatoriMammoVolume(row, col, tipoop, tpscr, ulss, dal,al_escluso, centro, zona, comune,
                                 round_ind_da, round_ind_a, round_org, volontari, eta_da, eta_a,
                                 /*20080709 MOD: aggiunta round inviti ai filtri*/
                                 round_inviti
                                 /*20080709 FINE MOD*/
                                 );

    }

    private static void operatoriMammoVolume(int row, int col,int tipoop, String tpscr,
                                             String ulss, Date dal, Date al_escluso, BigDecimal centro,
                                             BigDecimal zona, String comune, BigDecimal round_ind_da,
                                             BigDecimal round_ind_a, BigDecimal round_org, BigDecimal volontari,
                                             BigDecimal eta_da, BigDecimal eta_a,
                                             /*20080709 MOD: aggiunta round inviti ai filtri*/
                                             BigDecimal round_inviti
                                             /*20080709 FINE MOD*/
                                             ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject operatori = am.findViewObject("Stats_SoOpmedicoView1");
        ViewObject mxesiti = am.findViewObject("Stats_SoCnfRef1livMXEST1View1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;
        RowSetIterator iter1 = null;

        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            String where =
                "ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND CODOP=" + ConfigurationConstants.CODICE_RADIOLOGO;
            operatori.setWhereClause(where);
            operatori.executeQuery();


            //leggo le righe degli operatori
            iter = ViewHelper.getRowSetIterator(operatori, "operatori_iter");
            //e le colonne degli esiti
            iter1 = ViewHelper.getRowSetIterator(mxesiti, "esiti_mammo");

            //id | nome | mx refertate | una colonna per ogni referto|SOGGETTI CON CANCRO|DETECTION RATE
            headers = new String[iter1.getRowCount() + 3];
            headers[0] = "Codice";
            headers[1] = "Lettore";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            totali = new int[headers.length];


            int j = iter.getRowCount();
            //per ogni operatore (una riga)
            while (iter.hasNext()) {

                //   int rowTotal=0;
                Stats_SoOpmedicoViewRow inv = (Stats_SoOpmedicoViewRow) iter.next();
                j--;
                if (row >= 0 && col >= 0 && row != j)
                    continue;


                Row r = null;

                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getIdop());
                    r.setAttribute(1, inv.getCognome() + " " + inv.getNome());
                }

                int x = 0;
                HashMap result = null;

                //  COLONNA COL NUMERO TOTALE DI ESAMI
                int i = 2;
                headers[i] = "N° letture";
                render[i] = true;

                if ((row < 0 && col < 0) ||
                    (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result =
am.callDiagnosi1livMAFunction(new BigDecimal(1), new BigDecimal(inv.getIdop().doubleValue()), //lettore
                              null, //esito
                              null, //sugg
                              ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                              round_ind_a, //round ind a
                              round_org, //round org
                              centro, //centro
                              zona, //zona
                              eta_da, //eta da
                              eta_a, //eta a
                              comune, //comune
                              volontari, //volontaria
                              new BigDecimal(0),
                              /*20082507 MOD: aggiunta round inviti ai filtri*/
                              round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                              /*20082507 FINE MOD*/
                              );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));

                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                }

                // int totale=x;
                //  totali[i]+=x;

                //un acolonna per ogni esito di mammografia
                iter1.first();
                iter1.previous();
                while (iter1.hasNext()) {

                    i++;
                    Row r1 = iter1.next();
                    headers[i] = (String) r1.getAttribute("Descrizione");
                    render[i] = true;

                    if ((row < 0 && col < 0) ||
                        (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                        //calcolo del numero di record che soddisfano i criteri
                        result =
    am.callDiagnosi1livMAFunction(new BigDecimal(1), new BigDecimal(inv.getIdop().doubleValue()), //lettore
                                  new BigDecimal(((Number) r1.getAttribute("Idcnfref1l")).doubleValue()), //esito
                                  null, //sugg
                                  ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                  round_ind_a, //round ind a
                                  round_org, //round org
                                  centro, //centro
                                  zona, //zona
                                  eta_da, //eta da
                                  eta_a, //eta a
                                  comune, //comune
                                  volontari, //volontaria
                                  new BigDecimal(0),
                                  /*20082507 MOD: aggiunta round inviti ai filtri*/
                                  round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                  /*20082507 FINE MOD*/
                                  );
                        if (result.get("errore") != null)
                            throw new Exception((String) result.get("errore"));

                        /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                        if (row < 0 || col < 0) {
                            x = ((Integer) result.get("howmany")).intValue();
                            if (x < 0)
                                throw new Exception("Errore di database");
                            r.setAttribute(i, new Integer(x));
                            totali[i] += x;

                        }

                        /*20080725 fine mod*/
                        else {


                            String inClause = (String) result.get("query");
                            Cnf_selectionBean c_bean =
                                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                            c_bean.setInClause(inClause);

                            //imposto i dati
                            ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                                  headers[1] + ": " +
                                                                                  ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                            ADFContext.getCurrent().getSessionScope().put("colLabel",
                                                                                  "Test: " + headers[col + 2]);

                            return;

                        }

                        //    totali[i]+=x;
                    }
                }

            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();

            if (iter1 != null)
                iter1.closeRowSetIterator();

            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali", totali);

            }


        }
    }


    private static void operatoriMammoCoorti(int row, int col,int tipoop, String tpscr,
                                             String ulss, Date dal, Date al_escluso, BigDecimal centro,
                                             BigDecimal zona, String comune, BigDecimal round_ind_da,
                                             BigDecimal round_ind_a, BigDecimal round_org, BigDecimal volontari,
                                             BigDecimal eta_da, BigDecimal eta_a,
                                             /*20080709 MOD: aggiunta round inviti ai filtri*/
                                             BigDecimal round_inviti
                                             /*20080709 FINE MOD*/
                                             ) throws Exception {

        Stats_AppModule am =
            (Stats_AppModule) BindingContext.getCurrent().findDataControl("Stats_AppModuleDataControl").getDataProvider();
        ViewObject operatori = am.findViewObject("Stats_SoOpmedicoView1");
        ViewObject to_fill = am.findViewObject("Stats_InvitiEsitiMatrix");

        RowSetIterator iter = null;

        String[] headers = null;
        boolean[] render = null;
        int[] totali = null;

        try {
            //ripulisco i dati di conti precdenti
            if (row < 0 || col < 0)
                ViewHelper.clearVO(to_fill);


            String where =
                "ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND CODOP=" + ConfigurationConstants.CODICE_RADIOLOGO;
            operatori.setWhereClause(where);
            operatori.executeQuery();


            //leggo le righe degli operatori
            iter = ViewHelper.getRowSetIterator(operatori, "operatori_iter");

            //id | nome | soggetti screenati|SOGGETTI CON CANCRO|DETECTION RATE
            headers = new String[5];
            headers[0] = "Codice";
            headers[1] = "Lettore";
            render = new boolean[headers.length];
            render[0] = false;
            render[1] = true;
            render[2] = true;
            render[3] = true;
            render[4] = true;
            totali = new int[headers.length];


            int j = iter.getRowCount();
            //per ogni operatore (una riga)
            while (iter.hasNext()) {

                //   int rowTotal=0;
                Stats_SoOpmedicoViewRow inv = (Stats_SoOpmedicoViewRow) iter.next();
                j--;
                if (row >= 0 && col >= 0 && row != j)
                    continue;


                Row r = null;

                if (row < 0 || col < 0) {
                    r = to_fill.createRow();
                    to_fill.insertRow(r);
                    //id e label della riga
                    r.setAttribute(0, inv.getIdop());
                    r.setAttribute(1, inv.getCognome() + " " + inv.getNome());
                }

                int x = 0;
                HashMap result = null;


                //1. NUMERO DI SCREENATI DELL'OPERATORE
                int i = 2;
                headers[i] = "Soggetti screenati";
                if ((row < 0 && col < 0) ||
                    (row >= 0 && col >= 0 && row == j && col == i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result =
am.callDiagnosi1livMAFunction(new BigDecimal(1), new BigDecimal(inv.getIdop().doubleValue()), //lettore
                              null, //esito
                              null, //sugg
                              ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                              round_ind_a, //round ind a
                              round_org, //round org
                              centro, //centro
                              zona, //zona
                              eta_da, //eta da
                              eta_a, //eta a
                              comune, //comune
                              volontari, //volontaria
                              new BigDecimal(1),
                              /*20082507 MOD: aggiunta round inviti ai filtri*/
                              round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                              /*20082507 FINE MOD*/
                              );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
             if(row<0 || col<0)
                             r.setAttribute(i,new Integer(x));

                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        totali[i] += x;

                    }

                    /*20080725 fine mod*/
                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                }

                int screenati = x;
                //totali[i]+=x;

                //2. NUMERO SCREENATI CON CANCRO
                i++;
                headers[i] = "Soggetti con carcinoma";
                headers[i + 1] = "Detection rate per carcinoma (x1000)";
                if ((row < 0 && col < 0) || (row >= 0 && col >= 0 && row == j && col >= i - 2)) {
                    //calcolo del numero di record che soddisfano i criteri
                    result = am.callDetectionRateMAFunction(new BigDecimal(inv.getIdop().doubleValue()), //lettore
                                                            ulss, tpscr, dal==null?null:new SimpleDateFormat("dd/MM/yyyy").format(dal),al_escluso==null?null:new SimpleDateFormat("dd/MM/yyyy").format(al_escluso), round_ind_da, //round ind da (cioe' quelle che sono partite con lo zero)
                                                            round_ind_a, //round ind a
                                                            round_org, //round org
                                                            centro, //centro
                                                            zona, //zona
                                                            eta_da, //eta da
                                                            eta_a, //eta a
                                                            comune, //comune
                                                            volontari,
                                                            /*20082507 MOD: aggiunta round inviti ai filtri*/
                                                            round_inviti, (row < 0 || col < 0) ? new BigDecimal(0) : new BigDecimal(1) //solo_query
                                                            /*20082507 FINE MOD*/
                                                            );
                    if (result.get("errore") != null)
                        throw new Exception((String) result.get("errore"));
                    /*20080725 mod
             x=((Integer)result.get("howmany")).intValue();
             if(x<0)
               throw new Exception("Errore di database");
            if(row<0 || col<0){
                 r.setAttribute(i,new Integer(x));
                 //calcolo ed imposto la detection rate
                 //3. DETECTION RATE
                 r.setAttribute(i+1,Double.toString(Stats_helper.x_tage(1000,screenati,x))+"‰");

             }
                             */
                    if (row < 0 || col < 0) {
                        x = ((Integer) result.get("howmany")).intValue();
                        if (x < 0)
                            throw new Exception("Errore di database");
                        r.setAttribute(i, new Integer(x));
                        //calcolo ed imposto la detection rate
                        //3. DETECTION RATE
                        r.setAttribute(i + 1, Double.toString(Stats_helper.x_tage(1000, screenati, x)) + "‰");

                        totali[i] += x;

                    }

                    /*20080725 fine mod*/

                    else {


                        String inClause = (String) result.get("query");
                        Cnf_selectionBean c_bean =
                            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
                        c_bean.setInClause(inClause);

                        //imposto i dati
                        ADFContext.getCurrent().getSessionScope().put("rowLabel",
                                                                              headers[1] + ": " +
                                                                              ((String) ADFContext.getCurrent().getSessionScope().get("rowLabel")));
                        ADFContext.getCurrent().getSessionScope().put("colLabel", headers[col + 2]);

                        return;

                    }
                }

                // int totale=x;
                // totali[i]+=x;


            }


            if (row < 0 || col < 0) {
                String[] tot = new String[totali.length];
                tot[2] = Integer.toString(totali[2]);
                tot[3] = Integer.toString(totali[3]);
                tot[4] = Double.toString(Stats_helper.x_tage(1000, totali[2], totali[3])) + "‰";

                ADFContext.getCurrent().getSessionScope().remove("stats_inviti_esiti_totali");
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_totali_perc", tot);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            ViewHelper.clearVO(to_fill);
            throw ex;
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();


            if (row < 0 || col < 0) {
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_headers", headers);
                ADFContext.getCurrent().getSessionScope().put("stats_inviti_esiti_render", render);

            }
        }
    }

}
