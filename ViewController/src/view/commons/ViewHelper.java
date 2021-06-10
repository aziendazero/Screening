package view.commons;

import insiel.dataHandling.BlobUtils;

import java.io.File;
import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.BlobDomain;

public class ViewHelper {
    /**
     * Estrae il logo in un file e ne memorizza il path in un cookie reperibile
     * anche poi
     * @throws java.io.IOException
     * @return
     * @param ctx
     * @param r
     */
    private static File prepareLogo(Row r) throws IOException {
        File f = File.createTempFile("logoUlss", ".gif");
        BlobDomain blob = (BlobDomain) r.getAttribute("Logo");
        if (blob != null) {
            ADFContext ctx = ADFContext.getCurrent();
            f = BlobUtils.getFileFromBlob(blob, f.getAbsolutePath());
            if (f != null) {
                ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
                HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
                HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
                CookiesManager.updateFileCookie("logoUlss" + ((String) r.getAttribute("Codaz")).trim(),
                                                f.getAbsolutePath(), response, request);
            }
            ctx.getSessionScope().put("cookie_logoUlss", f.getAbsolutePath());
        }
        return f;
    }

    /**
     * Prova a recuperare il logo da un file, se e' presente l'opportuno cookie
     * e se il file non e' stato cancellato. Altrimenti lo riestrae e memorizza nel cookie
     * @throws java.io.IOException
     * @return il file con il logo dell'azienda sanitaria
     * @param ctx dataactioncontext
     * @param r record con l'azienda sanitaria in questione
     */
    public static File getLogoFile(Row r) throws IOException {
        ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
        HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
        //se ho gia' il cookie per il logo non serve estrarlo nuovamente
        Cookie logoC = CookiesManager.getCookie("logoUlss" + ((String) r.getAttribute("Codaz")).trim(), request);
        File x = null;
        if (logoC != null) {
            //il cookie c'e' gia', controllo ch eil file ci sia ancora
            x = new File(logoC.getValue());
            if (x.exists())
                //mi limito a memorizzarne il valore nella session
                ADFContext.getCurrent().getSessionScope().put("cookie_logoUlss", logoC.getValue());
            else
                x = prepareLogo(r);
        } else { //il cookie va creato ed il file va estratto dal DB
            x = prepareLogo(r);
        }

        return x;
    }
    
    /**
     * Metodo che esegue la query di un viewobject e ripristina la current row
     * (versione che limita il consumo di memoria)
     * @param vo viewobject
     */

    public static void queryAndRestoreCurrentRow(ViewObject vo) {

        Key k = null;
        Row cRow = vo.getCurrentRow();

        if (cRow != null) {
            k = cRow.getKey();
        }

        vo.executeQuery();

        if (k != null) {
            setCurrent(vo, k);
        }

    }
    
    /**Metodo che imposta come current row la riga con la chiave data
     * */
    public static void setCurrent(ViewObject vo, Key k) {
        Row row = vo.getRow(k);

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
    
    public static String replaceApostrophe(String s) {
        if (s == null)
            return null;
        return s.replaceAll("'", "''");
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
}
