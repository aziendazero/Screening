package model.commons;

import java.util.Map;

import oracle.adf.share.ADFContext;

public class AccessManager {
    public static final String CODREGIONALE = ConfigurationConstants.CODICE_ULSS_REGIONALE;

    public AccessManager() {
    }

    /**
     * Metodo che decide se l'ulss di riferimento ha diritto ad
     * accesso a livello regionale (solo la ulss fittizia
     * 050999, corrisposndente al registro tumori, ha diritto
     * a tale tipo di accesso)
     * @return true se l'accesso è regionale, false altrimenti
     * @param ulss codice della ulss
     */
    public static boolean isRegionale(String ulss) {
        if (CODREGIONALE.equals(ulss))
            return true;
        else
            return false;
    }

    /**
     * Metodo che crea la where clause di base per i view object che devono essere filtrati in
     * base al tipo di screening ed alla ulss, a meno di accesso regionale
     * @return
     * @param scr sigla che codifica il tipo di screening
     * @param ulss codice dell'azienda sanitaria
     */
    public static String getInitialFilter(String ulss, String scr) {
        String where = "TPSCR='" + scr + "'";
        if (!isRegionale(ulss))
            where += " AND ULSS='" + ulss + "' ";
        return where;
    }

    /**
     * Metodo che genara la condizione di filtering
     * sulla ulss
     * @return ULSS=codiceulss oppure string avuota se la ulss
     * è di livello regionale
     * @param ulss
     */
    public static String getUlssFilter(String ulss) {
        if (!isRegionale(ulss))
            return "ULSS='" + ulss + "' ";
        else
            return "";
    }

    /**
     * Metodo che genara la condizione di filtering
     * sull tipo di screening
     * @return TPSCR='CI' per il citologico etc.
     * @param scr sigla del tipo si dcreening
     */
    public static String getTpscrFilter(String scr) {
        return "TPSCR='" + scr + "'";
    }

    /**
     * Metoche che genera un'eccezione se l'utente non ha l'abilitazione specificata
     * @throws java.lang.IllegalAccessException
     * @param session sessione in cui deve essere memorizzata l'abilitazione
     * @param perm nome della risorsa, dell'abilitazione
     */
    public static void checkPermission(String perm) throws IllegalAccessException {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        Boolean can_do = (Boolean) session.get(perm);
        if (can_do == null || !can_do.booleanValue())
            throw new IllegalAccessException("Operazione non concessa o non abilitata");
    }
}
