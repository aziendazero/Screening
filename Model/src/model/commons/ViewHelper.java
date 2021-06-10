package model.commons;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Key;
import oracle.jbo.NameClashException;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;

public class ViewHelper {

    public static Object getQueryResult(ApplicationModule am, String query) throws Exception {
        ViewObject vo = am.createViewObjectFromQueryStmt("", query);
        vo.setRangeSize(-1);
        vo.executeQuery();

        Row res = vo.first();

        if (res == null)
            return null;
        else
            return res.getAttribute(0);

    }

    public static int getTipoCentro(ApplicationModule am, Integer codCt) throws Exception {

        String qry = "SELECT a.tipocentro FROM so_centro_prel_ref a where a.idcentro = " + codCt.toString();
        ViewObject vo = am.createViewObjectFromQueryStmt("", qry);
        vo.setRangeSize(-1);
        vo.executeQuery();

        Row ct = vo.first();

        Integer tipoCt = ct.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) ct.getAttribute(0)).intValue() : null;

        return tipoCt.intValue();

    }


    public static boolean checkStringaOrario(String orario) {
        if (orario == null)
            return true;
        if (orario.equals(""))
            return true;

        if (orario.length() != 5)
            return false;

        String sep = orario.substring(2, 3);

        if (!sep.equals(":"))
            return false;

        String ora = orario.substring(0, 2);

        int hh, mi;

        try {
            hh = Integer.parseInt(ora);
        } catch (Exception ex) {
            return false;
        }

        String min = orario.substring(3, 5);

        try {
            mi = Integer.parseInt(min);
        } catch (Exception ex) {
            return false;
        }

        if (hh < 0 || hh > 23)
            return false;

        if (mi < 0 || mi > 59)
            return false;

        return true;

    }

    /**
     * Metodo che esegue la query di un viewobject e ripristina la current row
     * (versione che limita il consumo di memoria)
     * @param vo viewobject
     */

    public static void queryAndRestoreCurrentRow(ViewObject vo) {

        // mauro, 08-06-2007, nuovo metodo per query e ripristino
        // riga corrente
        /*MOD 20071106
    long count = vo.getEstimatedRowCount();


    Key k = null;

    if (count > 0)
    {
      Row cRow = vo.getCurrentRow();
      k = cRow.getKey();
    }
    */


        Key k = null;
        Row cRow = vo.getCurrentRow();

        if (cRow != null) {

            k = cRow.getKey();
        }

        vo.executeQuery();

        //fcnt = vo.getFetchedRowCount();

        if (k != null) {

            setCurrent(vo, k);
        }

        //fcnt = vo.getFetchedRowCount();

    }

    /**Metodo che imposta come current row la riga con la chiave data
     * */
    public static void setCurrent(ViewObject vo, Key k) {
        Row row = vo.getRow(k);
        //fcnt = vo.getFetchedRowCount();

        if (row != null) {
            vo.setCurrentRow(row);

            int index = vo.getCurrentRowIndex();
            int range = vo.getRangeSize();
            if (range > 0)
                vo.scrollRangeTo(vo.getCurrentRow(), index % range);
            else
                vo.scrollRangeTo(vo.getCurrentRow(), index);
        }
    }

    public static void restoreCurRow(ViewObject vo, String attName, Object attValue) {
        // senza esecuzione query
        //   int fCount = vo.getFetchedRowCount();
        //System.out.println("before "+fCount);
        Row[] result = vo.getFilteredRows(attName, attValue);
        //  fCount = vo.getFetchedRowCount();
        // System.out.println("after "+fCount);
        if (result.length != 0) {
            vo.setCurrentRow(result[0]);

            int index = vo.getCurrentRowIndex();
            int range = vo.getRangeSize();
            if (range > 0)
                vo.scrollRangeTo(vo.getCurrentRow(), index % range);
            else
                vo.scrollRangeTo(vo.getCurrentRow(), index);
        }
        result = null;
    }

    /**
     * Metodo cheripristina la current row in un viewobject, in
     * base alla chiave della stessa
     * @param attValue valore dell'attributo discriminante
     * @param attName nome dell'attributo discriminante
     * @param vo viewobject
     */
    public static void restoreCurRow(ViewObject vo, Key k) {
        // senza esecuzione query
        Row[] result = vo.findByKey(k, 1);
        if (result.length != 0) {
            vo.setCurrentRow(result[0]);

            int index = vo.getCurrentRowIndex();
            int range = vo.getRangeSize();
            if (range > 0)
                vo.scrollRangeTo(vo.getCurrentRow(), index % range);
            else
                vo.scrollRangeTo(vo.getCurrentRow(), index);
        }
        result = null;
    }

    /**
     * Metodo che esegue la query sul view object e imposta come current row
     * la riga che ha l'attributo attName=attValue
     * @param attValue valore dell'attributo discriminante
     * @param attName nome dell'attributo discriminante
     * @param vo viewobject su cui lavorare
     */
    public static void restoreCurrentRow(ViewObject vo, String attName, Object attValue) {

        vo.executeQuery();

        restoreCurRow(vo, attName, attValue);
        /* Row[] result=vo.getFilteredRows(attName,attValue);
    if(result.length!=0)
    {
      vo.setCurrentRow(result[0]);
      int index=vo.getCurrentRowIndex();
      vo.scrollRangeTo(vo.getCurrentRow(),index);
    }*/
    }

    /**
     * Medodo che cancella tutte le righe di un viewObject
     * (utile per il reset dei vo populated programmatically)
     * @param vo
     */
    public static void clearVO(ViewObject vo) {
        if (vo == null)
            return;
        while (vo.last() != null) {
            vo.last().remove();
        }
    }     

    /**
     * Dato un array di record, li inserisce in una hashmap in cui le
     * chiavi sono i valori dell'attributo passato come input
     * @return
     * @param keyAttName
     * @param rows
     */
    public static HashMap createHashMapFromRows(Row[] rows, String keyAttName) {
        if (rows == null)
            return null;
        HashMap hash = new HashMap();
        for (int i = 0; i < rows.length; i++) {
            hash.put(rows[i].getAttribute(keyAttName), rows[i]);
        }
        return hash;

    }

    public static String replaceApostrophe(String s) {
        if (s == null)
            return null;
        return s.replaceAll("'", "''");
    }

    /**
     * Formatta ora e minuti in una string adi 5 caratteri
     * avente il formato hh:mm
     * @return
     * @param mm
     * @param hh
     */
    public static String formatTime(String hh, String mm) {
        String time;
        if (hh == null || hh.length() == 0)
            time = "00";
        else if (hh.length() == 1)
            time = "0" + hh;
        else
            time = hh;

        time += ":";

        if (mm == null || mm.length() == 0)
            time += "00";
        else if (mm.length() == 1)
            time += "0" + mm;
        else
            time += mm;

        return time;
    }


    public static String formatTime(Integer hh, Integer mm) {
        String h, m;
        if (hh == null || hh.intValue() < 0)
            h = null;
        else
            h = hh.toString();

        if (mm == null || mm.intValue() < 0)
            m = null;
        else
            m = mm.toString();

        return formatTime(h, m);
    }
    
    public static String formatTime(Number hh, Number mm) {
        String h, m;
        if (hh == null || hh.intValue() < 0)
            h = null;
        else
            h = hh.toString();

        if (mm == null || mm.intValue() < 0)
            m = null;
        else
            m = mm.toString();

        return formatTime(h, m);
    }

    /**
     * Prova a creare un iteratore. Se risulta gi� esistente lo cancella e lo ricrea
     * @return
     * @param name nome dell'iteratore
     * @param vo viewobject
     */
    public static RowSetIterator getRowSetIterator(ViewObject vo, String name) {
        RowSetIterator iter;
        try {
            iter = vo.createRowSetIterator(name);
        } catch (NameClashException ex) {
            iter = vo.findRowSetIterator(name);
            if (iter != null)
                iter.closeRowSetIterator();
            iter = vo.createRowSetIterator(name);
        }

        return iter;
    }

    /**
     * Metodo che restituisce una delle tre possibili opzioni a
     * seconda del tipo di screening
     * @return
     * @param ma opzione per il mammografico
     * @param co opzione per il colonretto
     * @param ci opzione per il citologico
     * @param tpscr tipo di screening
     */
    public static Object decodeByTpscr(String tpscr, Object ci, Object co, Object ma, Object ca, Object pf) {
        if ("CI".equals(tpscr))
            return ci;

        if ("CO".equals(tpscr))
            return co;

        if ("MA".equals(tpscr))
            return ma;

        if ("CA".equals(tpscr))
            return ca;

        if ("PF".equals(tpscr))
            return pf;

        return null;
    }

    /*20080721 MOD: lettere per eta e centro*/

    /**
     * Metodo che restituisce l'et� in anni compiuti
     * @return anni compiuti
     * @param nascita data di nascita
     * @param verifica data in rapporto alla quale calcolare l'et�
     */
    public static int etaCompiuta(Date verifica, Date nascita) {
        Calendar cal = Calendar.getInstance(); // default will be Gregorian in US Locales		
        cal.setTime(verifica);
        int ADay = cal.get(Calendar.DATE);
        int AMonth = cal.get(Calendar.MONTH);
        int AYear = cal.get(Calendar.YEAR);
        cal.setTime(nascita);
        int BDay = cal.get(Calendar.DATE);
        int BMonth = cal.get(Calendar.MONTH);
        int BYear = cal.get(Calendar.YEAR);

        int eta = AYear - BYear;
        if (AMonth > BMonth)
            return eta;

        //passo a verificare sui mesi
        //mesi diversi, non ha ncora compito gli anni
        if (AMonth < BMonth) {
            return eta - 1;
        } else //stesso mese, verifico le date
        {
            if (ADay < BDay)
                return eta - 1;
            else
                return eta;
        }


    }
}
