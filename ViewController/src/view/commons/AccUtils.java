package view.commons;

import insiel.dataHandling.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Map;

import model.AccCa_AppModule;
import model.common.AccPf_AppModule;
import model.common.Acc_AppModule;


import model.accettazione.AccCa_RicInvitiViewRow;
import model.accettazione.AccCa_SoInvitoViewRow;
import model.accettazione.AccMa_RicInvitiViewRowImpl;
import model.accettazione.AccPf_RicInvitiViewRow;
import model.accettazione.AccPf_SoAccPfas1livViewRow;
import model.accettazione.AccPf_SoAccPfas2livViewRow;
import model.accettazione.AccPf_SoInvitoViewRow;
import model.accettazione.Acc_RicInvitiViewRowImpl;

import model.accettazione.common.AccMa_SoAccMammo1livViewRow;
import model.accettazione.common.AccMa_SoAccMammo2livViewRow;
import model.accettazione.common.AccMa_SoInvitoViewRow;

import model.common.AccCo_AppModule;
import model.common.AccCo_RicInvitiViewRow;
import model.common.AccCo_SoAccColon1livViewRow;
import model.common.AccCo_SoAccColon2livViewRow;
import model.common.AccCo_SoInvitoViewRow;
import model.common.AccMa_AppModule;
import model.accettazione.Acc_SoAccCito1livViewRowImpl;
import model.accettazione.Acc_SoInvitoViewRowImpl;

import model.common.Acc_SoAccCito2livViewRow;
import model.common.Acc_SoAnamnesiCitoViewRow;
import model.common.RefCo_AppModule;
import model.common.RefMa_AppModule;
import model.common.RefPf_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.datacontrol.Ref_SearchBean;
import model.datacontrol.Sogg_RicParam;

import model.inviti.InvitoUtils;

import view.referto.GestoreReferti;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.referto.RefCo_ricercaAction;
import view.referto.RefMa_ricercaAction;

public class AccUtils {
    /**
     * Crea la riga di anamnesi copiandola da quella precedente, se esiste.
     *
     * @return Se ha inserito la riga oppure no.
     * @param user Operatore
     * @param livello Livello screening
     * @param idNacc Chiave della tabella SO_ACC_CITO1LIV o SO_ACC_CITO2LIV
     * @param ulss ULSS
     * @param codTs Codice tessera sanitaria
     * @param am Application Module
     */
    public static boolean riportaAnam(Acc_AppModule am, String codTs, String ulss, Integer idNacc, int livello,
                                      String user, Date dataInvito) {
        ViewObject voAnamSogg = am.findViewObject("Acc_SoAnamnesiCitoView1");
        String whcl = "CODTS = '" + codTs + "' AND ULSS = '" + ulss + "'";
        voAnamSogg.setWhereClause(whcl);
        voAnamSogg.executeQuery();

        // se il cognome ha dentro un apostrofo non deve causare errore
        String user2 = user.replaceAll("'", "''");

        int countAnam = voAnamSogg.getRowCount();

        if (countAnam > 0) {
            Row fAnam = voAnamSogg.first();
            Integer idAnaOld = (Integer) fAnam.getAttribute("IdAnamci");

            String strDataApp = DateUtils.dateToString(dataInvito.dateValue());

            if (livello == 1) {
                Integer idAnam = am.getNextIdAnamCito();
                String ins =
                    "INSERT INTO so_anamnesi_cito (" + "id_anamci, " + "idacc1liv, " + "idacc2liv, " + "codts, " +
                    "dtcreazione, " + "opcreazione, " + "dtultmod, " + "opultmod, " + "ulss, " + "tpscr, " + "note, " +
                    "idopanamnesi, " + "dtanamnesi, " + "gravidanza, " + "mese_gravidanza, " + "allattamento, " +
                    "presenza_iud, " + "contracc_torm, " + "dt_ult_mestr, " + "menopausa, " + "chemio_ult1, " +
                    "radio_ult3, " + "grav_termine, " + "aborti, " + "parti_prematuri, " + "figli_viventi" +
                    ") SELECT " + idAnam.toString() + ", " + idNacc.toString() + ", " + "NULL, " + "a.codts, " +
                    "SYSDATE, '" + user2 + "', SYSDATE, '" + user2 + "', " + "a.ulss, " + "a.tpscr, " + "a.note, " +
                    "a.idopanamnesi, " + "TO_DATE('" + strDataApp + "', 'DD/MM/YYYY'), " + "a.gravidanza, " +
                    "a.mese_gravidanza, " + "a.allattamento, " + "a.presenza_iud, " + "a.contracc_torm, " +
                    "a.dt_ult_mestr, " + "a.menopausa, " + "a.chemio_ult1, " + "a.radio_ult3, " + "a.grav_termine, " +
                    "a.aborti, " + "a.parti_prematuri, " + "a.figli_viventi " + "FROM so_anamnesi_cito a " +
                    "WHERE a.id_anamci = " + idAnaOld.toString();

                am.getTransaction().executeCommand(ins);
            } else {
                Integer idAnam = am.getNextIdAnamCito();
                String ins =
                    "INSERT INTO so_anamnesi_cito (" + "id_anamci, " + "idacc1liv, " + "idacc2liv, " + "codts, " +
                    "dtcreazione, " + "opcreazione, " + "dtultmod, " + "opultmod, " + "ulss, " + "tpscr, " + "note, " +
                    "idopanamnesi, " + "dtanamnesi, " + "gravidanza, " + "mese_gravidanza, " + "allattamento, " +
                    "presenza_iud, " + "contracc_torm, " + "dt_ult_mestr, " + "menopausa, " + "chemio_ult1, " +
                    "radio_ult3, " + "grav_termine, " + "aborti, " + "parti_prematuri, " + "figli_viventi" +
                    ") SELECT " + idAnam.toString() + ", " + "NULL, " + idNacc.toString() + ", " + "a.codts, " +
                    "SYSDATE, '" + user2 + "', SYSDATE, '" + user2 + "', " + "a.ulss, " + "a.tpscr, " + "a.note, " +
                    "a.idopanamnesi, " + "TO_DATE('" + strDataApp + "', 'DD/MM/YYYY'), " + "a.gravidanza, " +
                    "a.mese_gravidanza, " + "a.allattamento, " + "a.presenza_iud, " + "a.contracc_torm, " +
                    "a.dt_ult_mestr, " + "a.menopausa, " + "a.chemio_ult1, " + "a.radio_ult3, " + "a.grav_termine, " +
                    "a.aborti, " + "a.parti_prematuri, " + "a.figli_viventi " + "FROM so_anamnesi_cito a " +
                    "WHERE a.id_anamci = " + idAnaOld.toString();
                am.getTransaction().executeCommand(ins);
            }
            am.getTransaction().commit();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Inserisce una riga di anamnesi, copiando i dati da un'altra anamnesi
     * dello stesso soggetto e stessa ULSS.
     *
     * @param user Utente connesso all'applicativo.
     * @param livello Livello di screening.
     * @param idNacc Identificativo della riga dell'accettazione
     * (SO_ACC_MAMMO1LIV / SO_ACC_MAMMO2LIV a seconda del livello)
     * per cui si vuole inserire l'anamnesi.
     * @param ulss ULSS.
     * @param codTs Soggetto.
     * @param am Application Module.
     *
     * @return Se e' stata riportata l'anamnesi.
     */
    public static boolean riportaAnamMA(AccMa_AppModule am, String codTs, String ulss, Integer idNacc, int livello,
                                        String user) {
        // Recupero l'anamnesi da cui copiare i dati
        ViewObject voAnamSogg = am.findViewObject("AccMa_AnamnesiSoggView1");
        String whcl = "CODTS = '" + codTs + "' AND ULSS = '" + ulss + "'";
        voAnamSogg.setWhereClause(whcl);
        voAnamSogg.executeQuery();

        // se il cognome ha dentro un apostrofo non deve causare errore
        String user2 = user.replaceAll("'", "''");

        int countAnam = voAnamSogg.getRowCount();

        // Se ho trovato un'anamnesi da copiare
        if (countAnam > 0) {

            // Valorizzo l'operatore anamnesi in funzione dell'utente collegato
            ViewObject cnfUtenti = am.findViewObject("Cnf_SoCnfUtentiOperatoriView1");
            String whereCnf =
                "Cnf_SoCnfUtentiOperatori.ULSS = '" + ulss +
                "' AND Cnf_SoCnfUtentiOperatori.TPSCR = 'MA' AND Cnf_SoCnfUtentiOperatori.USERNAME = '" + user2 + "' " +
                " AND (Cnf_SoOpmedico.DTFINEVALOPMEDICO IS NULL OR Cnf_SoOpmedico.DTFINEVALOPMEDICO > SYSDATE)" +
                " AND Cnf_SoOpmedico.CODOP = 1"; // Operatore anamnesi
            cnfUtenti.setWhereClause(whereCnf);
            cnfUtenti.executeQuery();
            Row userRow = cnfUtenti.first();
            String opAnamnesiStr = null;
            if (userRow != null) {
                opAnamnesiStr = userRow.getAttribute("Operatore").toString();
            }

            Row fAnam = voAnamSogg.first();
            Integer idAnaOld = (Integer) fAnam.getAttribute("IdAnamma");

            Integer idAnam = am.getNextIdAnamMx();
            String ins =
                "insert into SO_ANAMNESIMX (" + "ID_ANAMMA, IDACCMA_" + livello + "LIV, " + // Valorizzo la colonna corrispondente al livello
                "CODTS, DTCREAZIONE, OPCREAZIONE, " + "DTULTMOD, OPULTMOD, " + "IDOPANAMNESI, " +
       "DTANAMNESI, RIPETUTO, UM_FUORISCR, " + "UM_DATA, UM_CONS, UM_DTCONS, UM_REST, UM_DTREST, " +
       "TO_PRESENTE, TO_INCORSO, TO_DAL, TO_AL, " + "IC_PRESENTE, IC_DATAINT, IC_IDINTERVENTO, IC_ALTROINT, " +
       "IC_SEDEINT, IC_IDMOT, IC_ALTROMOT, NOTE, ULSS, TPSCR, " +
       "AF_ESITO, AF_MADRE, AF_SORELLA, AF_ALTRO, AF_ALTRO_TESTO" + ") select " + idAnam.toString() + ", " +
       idNacc.toString() + ", a.codts, sysdate, '" + user2 + "', sysdate, '" + user2 + "', " +

       // Se non ho trovato l'operatore anamnesi corrispondente all'utente
       // utilizzo l'operatore dell'anamnesi che sto copiando
       (opAnamnesiStr != null ? opAnamnesiStr : "a.idopanamnesi") +

       ", a.dtanamnesi, a.ripetuto, a.um_fuoriscr, " + "a.um_data, a.um_cons, a.um_dtcons, a.um_rest, a.um_dtrest, " +
       "a.to_presente, a.to_incorso, a.to_dal, a.to_al, " +
       "a.ic_presente, a.ic_dataint, a.ic_idintervento, a.ic_altroint, " +
       "a.ic_sedeint, a.ic_idmot, a.ic_altromot, a.note, a.ulss, a.tpscr, " +
       "a.af_esito, a.af_madre, a.af_sorella, a.af_altro, a.af_altro_testo " + "from SO_ANAMNESIMX a " +
       "where a.ID_ANAMMA = " + idAnaOld.toString();

            am.getTransaction().executeCommand(ins);

            String insSint =
                "insert into SO_ANAMNESIMX_SINTOMI (ID_ANAMMX_SINT, ID_ANAMMA, IDSINTOMO, ULSS, TPSCR, DX_IDSEDE, SX_IDSEDE) " +
                "SELECT SO_ANAMNESIMX_SINT_SEQ.NEXTVAL, " + idAnam.toString() +
                ", a.idsintomo, a.ulss, a.tpscr, a.dx_idsede, a.sx_idsede " + "FROM so_anamnesimx_sintomi a " +
                "WHERE a.id_anamma = " + idAnaOld.toString();
            am.getTransaction().executeCommand(insSint);

            am.getTransaction().commit();
            return true;
        }

        // Non ho trovato nessuna anamnesi da copiare
        else {
            return false;
        }
    }

    public static void insRichiamoCA(AccCa_AppModule am, Row cInv) throws Exception {
        //TODO



    }

    public static void insRichiamoPF(AccPf_AppModule am, Row cInv) throws Exception {

        String ulss = (String) cInv.getAttribute("Ulss");
        String tpscr = (String) cInv.getAttribute("Tpscr");

        boolean up_richiamo = true;
        String s = "select * from ";
        if (((Integer) cInv.getAttribute("Livello")).intValue() == 1) {
            s = s +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO1LIV", "SO_REFERTOCOLON1LIV",
                                                  "SO_REFERTOMAMMO1LIV", null, "SO_REFERTOPFAS");
        }

        s = s + " R WHERE R.IDINVITO=" + cInv.getAttribute("Idinvito");
        ViewObject ref_vo = null;
        try {
            ref_vo = am.createViewObjectFromQueryStmt("ref", s);
            ref_vo.executeQuery();
            up_richiamo = (ref_vo.first() == null);
        } catch (Exception rex) {
            up_richiamo = true;
        } finally {
            if (ref_vo != null)
                ref_vo.remove();
        }

        if (up_richiamo) {
            ViewObject voCnfEsito = am.findViewObject("Acc_CnfEsitoView1");
            String codEsito = (String) cInv.getAttribute("Codesitoinvito");
            Integer livEsito = (Integer) cInv.getAttribute("Livesito");

            String whcl =
                "CODESITOINVITO = '" + codEsito + "' and LIVESITO = " + livEsito.toString() + " and ULSS = '" + ulss +
                "' and TPSCR = '" + tpscr + "'";
            voCnfEsito.setWhereClause(whcl);
            voCnfEsito.executeQuery();

            Row cnfEsito = voCnfEsito.first();

            String tpRich = (String) cnfEsito.getAttribute(2);
            Integer categ = (Integer) cnfEsito.getAttribute(6);
            Integer gg = (Integer) cnfEsito.getAttribute(3);
            Integer livRich = (Integer) cnfEsito.getAttribute(7);

            ViewObject voCt = am.findViewObject("A_SoCentroPrelRefView1");

            GestoreReferti.insertRichiamo(cInv, tpRich, categ, livRich == null ? 0 : livRich.intValue(), gg, voCt, am);
        }
    }

    public static void insRichiamoMA(AccMa_AppModule am, Row cInv) throws Exception {
        String ulss = (String) cInv.getAttribute("Ulss");
        String tpscr = (String) cInv.getAttribute("Tpscr");
        /*MOD 20071212 il richiamo va ricalcolato solo se non c'e' gia' un referto*/
        boolean up_richiamo = true;
        String s = "select * from ";
        if (((Integer) cInv.getAttribute("Livello")).intValue() == 1) {
            s = s +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO1LIV", "SO_REFERTOCOLON1LIV",
                                                  "SO_REFERTOMAMMO1LIV", null, "SO_REFERTOPFAS");
        } else {
            s = s +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO2LIV", "SO_REFERTOCOLON2LIV",
                                                  "SO_REFERTOMAMMO2LIV", null, null);
        }

        s = s + " R WHERE R.IDINVITO=" + cInv.getAttribute("Idinvito");
        ViewObject ref_vo = null;
        try {
            ref_vo = am.createViewObjectFromQueryStmt("ref", s);
            ref_vo.executeQuery();
            up_richiamo = (ref_vo.first() == null);
        } catch (Exception rex) {
            up_richiamo = true;
        } finally {
            if (ref_vo != null)
                ref_vo.remove();
        }

        if (up_richiamo) {


            ViewObject voCnfEsito = am.findViewObject("AccMa_CnfEsitoView1");
            String codEsito = (String) cInv.getAttribute("Codesitoinvito");
            Integer livEsito = (Integer) cInv.getAttribute("Livesito");
            // String ulss = (String) cInv.getAttribute("Ulss");
            //String tpscr = (String) cInv.getAttribute("Tpscr");
            String whcl =
                "CODESITOINVITO = '" + codEsito + "' and LIVESITO = " + livEsito.toString() + " and ULSS = '" + ulss +
                "' and TPSCR = '" + tpscr + "'";
            voCnfEsito.setWhereClause(whcl);
            voCnfEsito.executeQuery();

            Row cnfEsito = voCnfEsito.first();

            String tpRich = (String) cnfEsito.getAttribute(2);
            Integer categ = (Integer) cnfEsito.getAttribute(6);
            Integer gg = (Integer) cnfEsito.getAttribute(3);
            Integer livRich = (Integer) cnfEsito.getAttribute(7);

            ViewObject voCt = am.findViewObject("A_SoCentroPrelRefView1");

            GestoreReferti.insertRichiamo(cInv, tpRich, categ, livRich == null ? 0 : livRich.intValue(), gg, voCt, am);
        }

    }


    public static void insRichiamoCO(AccCo_AppModule am, Row cInv) throws Exception {

        String ulss = (String) cInv.getAttribute("Ulss");
        String tpscr = (String) cInv.getAttribute("Tpscr");
        /*MOD 20071212 il richiamo va ricalcolato solo se non c'e' gia' un referto*/
        boolean up_richiamo = true;
        String s = "select * from ";
        if (((Integer) cInv.getAttribute("Livello")).intValue() == 1) {
            s = s +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO1LIV", "SO_REFERTOCOLON1LIV",
                                                  "SO_REFERTOMAMMO1LIV", null, "SO_REFERTOPFAS");
        } else {
            s = s +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO2LIV", "SO_REFERTOCOLON2LIV",
                                                  "SO_REFERTOMAMMO2LIV", null, null);
        }

        s = s + " R WHERE R.IDINVITO=" + cInv.getAttribute("Idinvito");
        ViewObject ref_vo = null;
        try {
            ref_vo = am.createViewObjectFromQueryStmt("ref", s);
            ref_vo.executeQuery();
            up_richiamo = (ref_vo.first() == null);
        } catch (Exception rex) {
            up_richiamo = true;
        } finally {
            if (ref_vo != null)
                ref_vo.remove();
        }

        if (up_richiamo) {
            ViewObject voCnfEsito = am.findViewObject("AccCo_CnfEsitoView1");
            String codEsito = (String) cInv.getAttribute("Codesitoinvito");
            Integer livEsito = (Integer) cInv.getAttribute("Livesito");
            // String ulss = (String) cInv.getAttribute("Ulss");
            // String tpscr = (String) cInv.getAttribute("Tpscr");
            String whcl =
                "CODESITOINVITO = '" + codEsito + "' and LIVESITO = " + livEsito.toString() + " and ULSS = '" + ulss +
                "' and TPSCR = '" + tpscr + "'";
            voCnfEsito.setWhereClause(whcl);
            voCnfEsito.executeQuery();

            Row cnfEsito = voCnfEsito.first();

            String tpRich = (String) cnfEsito.getAttribute(2);
            Integer categ = (Integer) cnfEsito.getAttribute(6);
            Integer gg = (Integer) cnfEsito.getAttribute(3);
            Integer livRich = (Integer) cnfEsito.getAttribute(7);

            ViewObject voCt = am.findViewObject("A_SoCentroPrelRefView1");

            GestoreReferti.insertRichiamo(cInv, tpRich, categ, livRich == null ? 0 : livRich.intValue(), gg, voCt, am);
        }
    }


    public static void insRichiamo(Acc_AppModule am, Row cInv) throws Exception {
        String ulss = (String) cInv.getAttribute("Ulss");
        String tpscr = (String) cInv.getAttribute("Tpscr");
        /*MOD 20071212 il richiamo va ricalcolato solo se non c'e' gia' un referto*/
        boolean up_richiamo = true;
        String s = "select * from ";
        if (((Integer) cInv.getAttribute("Livello")).intValue() == 1) {
            s = s +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO1LIV", "SO_REFERTOCOLON1LIV",
                                                  "SO_REFERTOMAMMO1LIV", null, "SO_REFERTOPFAS");
        } else {
            s = s +
                (String) ViewHelper.decodeByTpscr(tpscr, "SO_REFERTOCITO2LIV", "SO_REFERTOCOLON2LIV",
                                                  "SO_REFERTOMAMMO2LIV", null, null);
        }

        s = s + " R WHERE R.IDINVITO=" + cInv.getAttribute("Idinvito");
        ViewObject ref_vo = null;
        try {
            ref_vo = am.createViewObjectFromQueryStmt("ref", s);
            ref_vo.executeQuery();
            up_richiamo = (ref_vo.first() == null);
        } catch (Exception rex) {
            up_richiamo = true;
        } finally {
            if (ref_vo != null)
                ref_vo.remove();
        }

        if (up_richiamo) {
            ViewObject voCnfEsito = am.findViewObject("Acc_CnfEsitoView1");
            String codEsito = (String) cInv.getAttribute("Codesitoinvito");
            Integer livEsito = (Integer) cInv.getAttribute("Livesito");
            //  String ulss = (String) cInv.getAttribute("Ulss");
            //  String tpscr = (String) cInv.getAttribute("Tpscr");
            String whcl =
                "CODESITOINVITO = '" + codEsito + "' and LIVESITO = " + livEsito.toString() + " and ULSS = '" + ulss +
                "' and TPSCR = '" + tpscr + "'";
            voCnfEsito.setWhereClause(whcl);
            voCnfEsito.executeQuery();

            Row cnfEsito = voCnfEsito.first();

            String tpRich = (String) cnfEsito.getAttribute(2);
            Integer categ = (Integer) cnfEsito.getAttribute(6);
            Integer gg = (Integer) cnfEsito.getAttribute(3);
            Integer livRich = (Integer) cnfEsito.getAttribute(7);

            ViewObject voCt = am.findViewObject("A_SoCentroPrelRefView1");

            GestoreReferti.insertRichiamo(cInv, tpRich, categ, livRich == null ? 0 : livRich.intValue(), gg, voCt, am);
        }
    }

    public static void requeryElencoCardio() {
        AccCa_AppModule am =
            (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("AccCa_RicInvitiView1");
        ViewHelper.queryAndRestoreCurrentRow(vo);

    }

    public static void beforeNavAccPf(String dest, boolean selected) throws Exception {
        if (selected) {
            if (dest.equals("iniSogg")) {
                AccPf_AppModule am =
                    (AccPf_AppModule) BindingContext.getCurrent().findDataControl("AccPf_AppModuleDataControl").getDataProvider();
                ViewObject voRic = am.findViewObject("AccPf_RicInvitiView1");
                if (voRic.getRowCount() > 0) {
                    AccPf_RicInvitiViewRow cInv = (AccPf_RicInvitiViewRow) voRic.getCurrentRow();

                    Sogg_RicParam beanSogg =
                        (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                    beanSogg.resetCampi();
                    beanSogg.setTessSan((String) cInv.getCodts());
                    beanSogg.setCognome((String) cInv.getCognome());
                    beanSogg.setNome((String) cInv.getNome());
                    beanSogg.setInEta(0);
                    beanSogg.setNavigazione(Boolean.TRUE);

                }

            } else if (dest.startsWith("refPf_")) {
                AccPf_AppModule am =
                    (AccPf_AppModule) BindingContext.getCurrent().findDataControl("AccPf_AppModuleDataControl").getDataProvider();
                ViewObject voRic = am.findViewObject("AccPf_RicInvitiView1");
                Ref_SearchBean bean =
                    (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
                //reimposto comunque i criteri di ricerca
                bean.reset();
                if (voRic.getRowCount() > 0) {
                    //se ho una riga corrente, allora uso tali ati come parametri di ricerca...
                    AccPf_RicInvitiViewRow cInv = (AccPf_RicInvitiViewRow) voRic.getCurrentRow();
                    bean.setCodts(cInv.getCodts());
                    bean.setCognome(cInv.getCognome());
                    bean.setNome(cInv.getNome());
                    bean.setCentro_ref(cInv.getIdcref() == null ? null : cInv.getIdcref().intValue());
                    //... ed evito che venga seguita un auqery priva di risultati
                    bean.setEmptyQuery(false);
                }

                //se sto andando verso i referti
                if (dest.equals("refPf_toQu")) {

                    //devo eseguire la query
                    //RefPf_ricercaAction.search();

                    //impostare il record trovato come record corrente
                    RefPf_AppModule am2 =
                        (RefPf_AppModule) BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
                    ViewObject vo = am2.findViewObject("Ref_SoInvitiPerRefertiPFView1");
                    Row r = vo.first();
                    if (r == null)
                        throw new Exception("Referto non previsto per questo invito");
                    //ed impostare il livello del referto
                    ADFContext.getCurrent().getSessionScope().put("ref_livello", r.getAttribute("Livello"));

                }
            }

        }

    }

    public static void beforeNavAccCo(String dest, boolean selected) throws Exception {
        if (selected) {
            if (dest.equals("iniSogg")) {
                AccCo_AppModule am =
                    (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
                ViewObject voRic = am.findViewObject("AccCo_RicInvitiView1");
                if (voRic.getRowCount() > 0) {
                    AccCo_RicInvitiViewRow cInv = (AccCo_RicInvitiViewRow) voRic.getCurrentRow();

                    Sogg_RicParam beanSogg =
                        (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                    beanSogg.resetCampi();
                    beanSogg.setTessSan((String) cInv.getCodts());
                    beanSogg.setCognome((String) cInv.getCognome());
                    beanSogg.setNome((String) cInv.getNome());
                    beanSogg.setInEta(0);
                    beanSogg.setNavigazione(Boolean.TRUE);
                    
                    String chiave = (String) cInv.getChiave();
                    
                    if (chiave!=null && !"".equals(chiave)){
                        Map session = ADFContext.getCurrent().getSessionScope();
                        if (session.get("SOAccessoAnonimo")!=null){
                            Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                            if (sOAccessoAnonimo)
                                beanSogg.setChiave(chiave);
                        }
                    } 
                }

            } else if (dest.startsWith("to_refCo")) {
                AccCo_AppModule am =
                    (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
                ViewObject voRic = am.findViewObject("AccCo_RicInvitiView1");
                Ref_SearchBean bean =
                    (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
                //reimposto comunque i criteri di ricerca
                bean.reset();
                if (voRic.getRowCount() > 0) {
                    //se ho una riga corrente, allora uso tali ati come parametri di ricerca...
                    AccCo_RicInvitiViewRow cInv = (AccCo_RicInvitiViewRow) voRic.getCurrentRow();
                    bean.setCodts(cInv.getCodts());
                    bean.setCognome(cInv.getCognome());
                    bean.setNome(cInv.getNome());
                    bean.setCentro_ref(cInv.getIdcref() == null ? null : cInv.getIdcref().intValue());
                    //... ed evito che venga seguita un auqery priva di risultati
                    bean.setEmptyQuery(false);
                }

                //se sto andando verso i referti(dall'anamnesi)
                if (dest.equals("to_refCo_Anam")) {

                    //devo eseguire la query
                    RefCo_ricercaAction.search();

                    //impostare il record trovato come record corrente
                    RefCo_AppModule am2 =
                        (RefCo_AppModule) BindingContext.getCurrent().findDataControl("RefCo_AppModuleDataControl").getDataProvider();
                    ViewObject vo = am2.findViewObject("Ref_SoInvitiPerRefertiCoView1");
                    Row r = vo.first();
                    if (r == null)
                        throw new Exception("Referto non previsto per questo invito");
                    //ed impostare il livello del referto
                    ADFContext.getCurrent().getSessionScope().put("ref_livello", r.getAttribute("Livello"));

                }
            }

        }

    }

    public static void beforeNavAccMa(String dest, boolean selected) throws Exception {
        if (selected) {
            if (dest.equals("iniSogg")) {
                AccMa_AppModule am =
                    (AccMa_AppModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
                ViewObject voRic = am.findViewObject("AccMa_RicInvitiView1");
                if (voRic.getRowCount() > 0) {
                    AccMa_RicInvitiViewRowImpl cInv = (AccMa_RicInvitiViewRowImpl) voRic.getCurrentRow();

                    Sogg_RicParam beanSogg =
                        (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                    beanSogg.resetCampi();
                    beanSogg.setTessSan((String) cInv.getCodts());
                    beanSogg.setCognome((String) cInv.getCognome());
                    beanSogg.setNome((String) cInv.getNome());
                    beanSogg.setInEta(0);
                    
                    String chiave = (String) cInv.getChiave();
                    
                    if (chiave!=null && !"".equals(chiave)){
                        Map session = ADFContext.getCurrent().getSessionScope();
                        if (session.get("SOAccessoAnonimo")!=null){
                            Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                            if (sOAccessoAnonimo)
                                beanSogg.setChiave(chiave);
                        }
                    } 
                                        
                    beanSogg.setNavigazione(Boolean.TRUE);

                }

            } else if (dest.startsWith("to_refMa")) {
                AccMa_AppModule am =
                    (AccMa_AppModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
                ViewObject voRic = am.findViewObject("AccMa_RicInvitiView1");
                Ref_SearchBean bean =
                    (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
                //reimposto comunque i criteri di ricerca
                bean.reset();
                if (voRic.getRowCount() > 0) {
                    //se ho una riga corrente, allora uso tali ati come parametri di ricerca...
                    AccMa_RicInvitiViewRowImpl cInv = (AccMa_RicInvitiViewRowImpl) voRic.getCurrentRow();
                    bean.setCodts(cInv.getCodts());
                    bean.setCognome(cInv.getCognome());
                    bean.setNome(cInv.getNome());
                    bean.setCentro_ref(cInv.getIdcref() == null ? null : cInv.getIdcref().intValue());

                    String chiave = (String) cInv.getChiave();
                    
                    if (chiave!=null && !"".equals(chiave)){
                        Map session = ADFContext.getCurrent().getSessionScope();
                        if (session.get("SOAccessoAnonimo")!=null){
                            Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                            if (sOAccessoAnonimo)
                                bean.setChiave(chiave);
                        }
                    } 

                    //... ed evito che venga seguita un auqery priva di risultati
                    bean.setEmptyQuery(false);
                }

                //se sto andando verso i referti(dall'anamnesi)
                if (dest.equals("to_refMa_Anam")) {

                    //devo eseguire la query
                    RefMa_ricercaAction.search();

                    //impostare il record trovato come record corrente
                    RefMa_AppModule am2 =
                        (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
                    ViewObject vo = am2.findViewObject("Ref_SoInvitiPerRefertiMAView1");
                    Row r = vo.first();
                    if (r == null)
                        throw new Exception("Referto non previsto per questo invito");
                    //ed impostare il livello del referto
                    ADFContext.getCurrent().getSessionScope().put("ref_livello", r.getAttribute("Livello"));

                }

            }

        }

    }


    public static void beforeNavAcc(String dest, boolean selected) {
        if (selected) {
            if (dest.equals("iniSogg")) {
                Acc_AppModule am =
                    (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
                ViewObject voRic = am.findViewObject("Acc_RicInvitiView1");
                if (voRic.getRowCount() > 0) {
                    Row rowInv = voRic.getCurrentRow();
                    Acc_RicInvitiViewRowImpl cInv = (Acc_RicInvitiViewRowImpl) rowInv;

                    Sogg_RicParam beanSogg =
                        (Sogg_RicParam) BindingContext.getCurrent().findDataControl("Sogg_RicParamDataControl").getDataProvider();
                    beanSogg.resetCampi();
                    beanSogg.setTessSan((String) cInv.getCodts());
                    beanSogg.setCognome((String) cInv.getCognome());
                    beanSogg.setNome((String) cInv.getNome());
                    beanSogg.setInEta(0);

                    String chiave = (String) cInv.getChiave();
                    
                    if (chiave!=null && !"".equals(chiave)){
                        Map session = ADFContext.getCurrent().getSessionScope();
                        if (session.get("SOAccessoAnonimo")!=null){
                            Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                            if (sOAccessoAnonimo)
                                beanSogg.setChiave(chiave);
                        }
                    } 

                    beanSogg.setNavigazione(Boolean.TRUE);
                }

            } else if (dest.startsWith("to_ref")) {
                Acc_AppModule am =
                    (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
                ViewObject voRic = am.findViewObject("Acc_RicInvitiView1");
                Ref_SearchBean bean =
                    (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
                //reimposto comunque i criteri di ricerca
                bean.reset();
                if (voRic.getRowCount() > 0) {
                    //se ho una riga corrente, allora uso tali ati come parametri di ricerca...
                    Acc_RicInvitiViewRowImpl cInv = (Acc_RicInvitiViewRowImpl) voRic.getCurrentRow();
                    bean.setCodts(cInv.getCodts());
                    bean.setCognome(cInv.getCognome());
                    bean.setNome(cInv.getNome());
                    bean.setCentro_ref(cInv.getIdcref() == null ? null : cInv.getIdcref().intValue());
                    
                    String chiave = (String) cInv.getChiave();
                    
                    if (chiave!=null && !"".equals(chiave)){
                        Map session = ADFContext.getCurrent().getSessionScope();
                        if (session.get("SOAccessoAnonimo")!=null){
                            Boolean sOAccessoAnonimo = (Boolean) session.get("SOAccessoAnonimo");
                            if (sOAccessoAnonimo)
                                bean.setChiave(chiave);
                        }
                    } 

                    //... ed evito che venga seguita un auqery priva di risultati
                    bean.setEmptyQuery(false);
                }

            }

        }

    }

    public static void procInvCorrenteMammo(ViewObject voRic) {

        AccMa_AppModule am = (AccMa_AppModule) voRic.getApplicationModule();

        AccMa_RicInvitiViewRowImpl cInvRic = (AccMa_RicInvitiViewRowImpl) voRic.getCurrentRow();
        Integer idInv = (Integer) cInvRic.getIdinvito();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // determino se invito ha referto
        boolean refPresente = InvitoUtils.invitoHaReferto(am, idInv.intValue(), tpscr);
        session.put("refPresente", Boolean.valueOf(refPresente));

        ViewObject voInvito = am.findViewObject("AccMa_SoInvitoView1");
        voInvito.setWhereClauseParam(0, idInv);
        voInvito.executeQuery();
        
        // determino se sogg. e' escluso
        String escl = cInvRic.getEscl();
        boolean nonEscl = (escl == null || escl.equals(""));
        session.put("soggEscluso", Boolean.valueOf(!nonEscl));       

        AccMa_SoInvitoViewRow fInv = (AccMa_SoInvitoViewRow) voInvito.first();
        voInvito.setCurrentRow(fInv);

        // filtro esiti invito
        Integer livello = (Integer) fInv.getLivello();
        String strDataApp = DateUtils.dateToString(fInv.getDtapp().dateValue());
        ViewObject voEsiti = am.findViewObject("AccMa_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'DD/MM/YYYY'))");
        voEsiti.executeQuery();

        // differenzio gestione secondo livello
        Integer idInvito = (Integer) fInv.getIdinvito();

        if (livello.intValue() == 1) {

            // inserimento o reperimento accettazione
            ViewObject voAcc = am.findViewObject("AccMa_SoAccMammo1livView1");
            String whcls = "IDINVITO = " + idInvito.toString();
            voAcc.setWhereClause(whcls);
            voAcc.executeQuery();

            int countAcc = voAcc.getRowCount();
            if (countAcc > 0) {
                AccMa_SoAccMammo1livViewRow fAcc = (AccMa_SoAccMammo1livViewRow) voAcc.first();
                voAcc.setCurrentRow(fAcc);
            } else {
                AccMa_SoAccMammo1livViewRow nAcc = (AccMa_SoAccMammo1livViewRow) voAcc.createRow();
                Integer idAcc = am.getNextIdAccMammo1();
                nAcc.setIdaccma1liv(idAcc);
                nAcc.setIdinvito(idInvito.intValue());

                Date dtoraCorr = DateUtils.getOracleDateNow();
                String user = (String) session.get("user");

                nAcc.setDtcreazione(dtoraCorr);
                nAcc.setOpcreazione(user);
                nAcc.setDtultmod(dtoraCorr);
                nAcc.setOpultmod(user);

                voAcc.insertRow(nAcc);

                am.getTransaction().commit();
            }

        } else // livello = 2
        {
            // inserimento o reperimento accettazione
            ViewObject voAcc = am.findViewObject("AccMa_SoAccMammo2livView1");
            String whcls = "IDINVITO = " + idInvito.toString();
            voAcc.setWhereClause(whcls);
            voAcc.executeQuery();

            int countAcc = voAcc.getRowCount();
            if (countAcc > 0) {
                AccMa_SoAccMammo2livViewRow fAcc = (AccMa_SoAccMammo2livViewRow) voAcc.first();
                voAcc.setCurrentRow(fAcc);
            } else {
                AccMa_SoAccMammo2livViewRow nAcc = (AccMa_SoAccMammo2livViewRow) voAcc.createRow();
                Integer idAcc = am.getNextIdAccMammo2();
                nAcc.setIdaccma2liv(idAcc);
                nAcc.setIdinvito(idInvito.intValue());

                Date dtoraCorr = DateUtils.getOracleDateNow();
                String user = (String) session.get("user");

                nAcc.setDtcreazione(dtoraCorr);
                nAcc.setOpcreazione(user);
                nAcc.setDtultmod(dtoraCorr);
                nAcc.setOpultmod(user);

                voAcc.insertRow(nAcc);

                am.getTransaction().commit();
            }

        }

    }


    public static void procInvCorrenteColon(ViewObject voRic) {
        AccCo_AppModule am =
            (AccCo_AppModule) voRic.getApplicationModule();

        AccCo_RicInvitiViewRow cInvRic = (AccCo_RicInvitiViewRow) voRic.getCurrentRow();

        Integer idInv = (Integer) cInvRic.getIdinvito();
        Map req = ADFContext.getCurrent().getRequestScope();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // setto variabili per capire se posso andare avanti e indietro
        boolean hasNext = voRic.hasNext();
        session.put("hasNext", Boolean.valueOf(hasNext));
        boolean hasPrev = voRic.hasPrevious();
        session.put("hasPrev", Boolean.valueOf(hasPrev));
        //

        // determino se invito ha referto
        boolean refPresente = InvitoUtils.invitoHaReferto(am, idInv.intValue(), tpscr);
        session.put("refPresente", Boolean.valueOf(refPresente));
        //

        // determino se sogg. e' escluso
        String escl = cInvRic.getEscl();
        boolean nonEscl = (escl == null || escl.equals(""));
        session.put("soggEscluso", Boolean.valueOf(!nonEscl));
        //

        ViewObject voInvito = am.findViewObject("AccCo_SoInvitoView1");
        /*MOD20071121
      voInvito.setWhereClause("IDINVITO = " + idInv.intValue());
        */
        voInvito.setWhereClauseParams(new Object[] { idInv });

        voInvito.executeQuery();

        AccCo_SoInvitoViewRow fInv = (AccCo_SoInvitoViewRow) voInvito.first();
        voInvito.setCurrentRow(fInv);

        // filtro esiti invito
        Integer livello = (Integer) fInv.getLivello();
        String strDataApp = DateUtils.dateToString(fInv.getDtapp().dateValue());
        ViewObject voEsiti = am.findViewObject("AccCo_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'DD/MM/YYYY'))");
        voEsiti.executeQuery();

        // differenzio gestione secondo livello
        Integer idInvito = (Integer) fInv.getIdinvito();

        if (livello.intValue() == 1) {
            // intestazione
            session.put("testoLivello", "Accettazione 1� livello");

            // inserimento o reperimento accettazione
            ViewObject voAcc = am.findViewObject("AccCo_SoAccColon1livView1");
            String whcls = "IDINVITO = " + idInvito.toString();
            voAcc.setWhereClause(whcls);
            voAcc.executeQuery();


            int countAcc = voAcc.getRowCount();
            if (countAcc > 0) {
                AccCo_SoAccColon1livViewRow fAcc = (AccCo_SoAccColon1livViewRow) voAcc.first();
                voAcc.setCurrentRow(fAcc);
            } else {
                AccCo_SoAccColon1livViewRow nAcc = (AccCo_SoAccColon1livViewRow) voAcc.createRow();
                Integer idAcc = am.getNextIdAccColon1();
                nAcc.setIdaccco1liv(idAcc);
                nAcc.setIdinvito(idInvito.intValue());

                Date dtoraCorr = DateUtils.getOracleDateNow();
                String user = (String) session.get("user");

                nAcc.setDtcreazione(dtoraCorr);
                nAcc.setOpcreazione(user);
                nAcc.setDtultmod(dtoraCorr);
                nAcc.setOpultmod(user);

                voAcc.insertRow(nAcc);

                am.getTransaction().commit();
            }


        } else // livello = 2
        {

            // intestazione
            session.put("testoLivello", "Accettazione 2� livello");

            // inserimento o reperimento accettazione
            ViewObject voAcc = am.findViewObject("AccCo_SoAccColon2livView1");
            String whcls = "IDINVITO = " + idInvito.toString();
            voAcc.setWhereClause(whcls);
            voAcc.executeQuery();

            int countAcc = voAcc.getRowCount();
            if (countAcc > 0) {
                AccCo_SoAccColon2livViewRow fAcc = (AccCo_SoAccColon2livViewRow) voAcc.first();
                voAcc.setCurrentRow(fAcc);
            } else {
                AccCo_SoAccColon2livViewRow nAcc = (AccCo_SoAccColon2livViewRow) voAcc.createRow();
                Integer idAcc = am.getNextIdAccColon2();
                nAcc.setIdaccco2liv(idAcc);
                nAcc.setIdinvito(idInvito.intValue());

                Date dtoraCorr = DateUtils.getOracleDateNow();
                String user = (String) session.get("user");

                nAcc.setDtcreazione(dtoraCorr);
                nAcc.setOpcreazione(user);
                nAcc.setDtultmod(dtoraCorr);
                nAcc.setOpultmod(user);

                voAcc.insertRow(nAcc);

                am.getTransaction().commit();
            }

            // query vuota su vo primo livello
            ViewObject voAcc1 = am.findViewObject("AccCo_SoAccColon1livView1");
            voAcc1.setWhereClause("1=2");
            voAcc1.executeQuery();

            // filtro view object per codifiche anamnesi
            ViewObject voOper = am.findViewObject("AccCo_OpAnamView1");
            String whcl = "REALE = 0 OR (REALE = 1 AND ULSS = '" + ulss + "' AND TPSCR = '" + tpscr + "')";
            //20110401 Serra: aggiunto il filtro sulla data di validita'
            String data_rif =
                fInv.getDtapp() == null ? DateUtils.getNow() :
                DateUtils.dateToString(DateUtils.DATE_PATTERN, fInv.getDtapp().dateValue());
            whcl +=
                " AND nvl(dtfinevalopmedico, to_date('31/12/2999','dd/mm/yyyy'))>=to_date('" + data_rif + "','" +
                DateUtils.DATE_PATTERN + "')";
            // fine 20110401
            voOper.setWhereClause(whcl);
            voOper.executeQuery();

            ViewObject voEsitiAnam = am.findViewObject("AccCo_EsitoAnamFamView1");
            String wh = "ULSS = '" + ulss + "' AND TPSCR = '" + tpscr + "'";
            voEsitiAnam.setWhereClause(wh);
            voEsitiAnam.executeQuery();

            // filtro sogg. scr.
            String codTs = fInv.getCodts();
            ViewObject voSoggScr = am.findViewObject("AccCo_SoSoggScrView1");
            String whscr = "CODTS = '" + codTs + "'" + " AND ULSS = '" + ulss + "' AND TPSCR = '" + tpscr + "'";
            voSoggScr.setWhereClause(whscr);
            voSoggScr.executeQuery();

            // referto II livello
            //
        }

    }


    public static void procInvCorrenteCardio() {
        AccCa_AppModule am =
            (AccCa_AppModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
        ViewObject voRic = am.findViewObject("AccCa_RicInvitiView1");
        AccCa_RicInvitiViewRow cInvRic = (AccCa_RicInvitiViewRow) voRic.getCurrentRow();
        Integer idInv = (Integer) cInvRic.getIdinvito();
        Map req = ADFContext.getCurrent().getRequestScope();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // setto variabili per capire se posso andare avanti e indietro
        boolean hasNext = voRic.hasNext();
        session.put("hasNext", Boolean.valueOf(hasNext));
        boolean hasPrev = voRic.hasPrevious();
        session.put("hasPrev", Boolean.valueOf(hasPrev));
        //

        // determino se invito ha referto
        boolean refPresente = InvitoUtils.invitoHaReferto(am, idInv.intValue(), tpscr);
        session.put("refPresente", Boolean.valueOf(refPresente));
        //

        // determino se sogg. e' escluso
        String escl = cInvRic.getEscl();
        boolean nonEscl = (escl == null || escl.equals(""));
        session.put("soggEscluso", Boolean.valueOf(!nonEscl));
        //

        ViewObject voInvito = am.findViewObject("AccCa_SoInvitoView1");
        voInvito.setWhereClauseParams(new Object[] { idInv });

        voInvito.executeQuery();

        AccCa_SoInvitoViewRow fInv = (AccCa_SoInvitoViewRow) voInvito.first();
        voInvito.setCurrentRow(fInv);

        // filtro esiti invito
        Integer livello = (Integer) fInv.getLivello();
        String strDataApp = DateUtils.dateToString(fInv.getDtapp().dateValue());
        ViewObject voEsiti = am.findViewObject("AccCa_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'DD/MM/YYYY'))");
        voEsiti.executeQuery();

    }

    public static void procInvCorrentePfas() {
        AccPf_AppModule am =
            (AccPf_AppModule) BindingContext.getCurrent().findDataControl("AccPf_AppModuleDataControl").getDataProvider();
        ViewObject voRic = am.findViewObject("AccPf_RicInvitiView1");
        AccPf_RicInvitiViewRow cInvRic = (AccPf_RicInvitiViewRow) voRic.getCurrentRow();
        Integer idInv = (Integer) cInvRic.getIdinvito();
        Map req = ADFContext.getCurrent().getRequestScope();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");

        // setto variabili per capire se posso andare avanti e indietro
        boolean hasNext = voRic.hasNext();
        session.put("hasNext", Boolean.valueOf(hasNext));
        boolean hasPrev = voRic.hasPrevious();
        session.put("hasPrev", Boolean.valueOf(hasPrev));
        //

        // determino se invito ha referto
        boolean refPresente = InvitoUtils.invitoHaReferto(am, idInv.intValue(), tpscr);
        session.put("refPresente", Boolean.valueOf(refPresente));
        //

        // determino se sogg. e' escluso
        String escl = cInvRic.getEscl();
        boolean nonEscl = (escl == null || escl.equals(""));
        session.put("soggEscluso", Boolean.valueOf(!nonEscl));
        //

        ViewObject voInvito = am.findViewObject("AccPf_SoInvitoView1");
        voInvito.setWhereClauseParams(new Object[] { idInv });
        voInvito.executeQuery();

        AccPf_SoInvitoViewRow fInv = (AccPf_SoInvitoViewRow) voInvito.first();
        voInvito.setCurrentRow(fInv);

        // filtro esiti invito
        Integer livello = (Integer) fInv.getLivello();
        String strDataApp = DateUtils.dateToString(fInv.getDtapp().dateValue());
        ViewObject voEsiti = am.findViewObject("AccPf_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'DD/MM/YYYY'))");
        voEsiti.executeQuery();

        // differenzio gestione secondo livello
        Integer idInvito = (Integer) fInv.getIdinvito();

        if (livello.intValue() == 1) {
            // intestazione
            session.put("testoLivello", "Accettazione 1� livello");

            // inserimento o reperimento accettazione
            ViewObject voAcc = am.findViewObject("AccPf_SoAccPfas1livView1");
            String whcls = "IDINVITO = " + idInvito.toString();
            voAcc.setWhereClause(whcls);
            voAcc.executeQuery();


            int countAcc = voAcc.getRowCount();
            if (countAcc > 0) {
                AccPf_SoAccPfas1livViewRow fAcc = (AccPf_SoAccPfas1livViewRow) voAcc.first();
                voAcc.setCurrentRow(fAcc);
            } else {
                AccPf_SoAccPfas1livViewRow nAcc = (AccPf_SoAccPfas1livViewRow) voAcc.createRow();
                Integer idAcc = am.getNextIdAccPFAS1();
                nAcc.setIdaccpf1liv(idAcc);
                nAcc.setIdinvito(idInvito.intValue());

                Date dtoraCorr = DateUtils.getOracleDateNow();
                String user = (String) session.get("user");

                nAcc.setDtcreazione(dtoraCorr);
                nAcc.setOpcreazione(user);
                nAcc.setDtultmod(dtoraCorr);
                nAcc.setOpultmod(user);

                voAcc.insertRow(nAcc);

                am.getTransaction().commit();
            }

        } else {
            // SECONDI LIVELLI
            // intestazione
            session.put("testoLivello", "Accettazione 2� livello");

            // inserimento o reperimento accettazione
            ViewObject voAcc = am.findViewObject("AccPf_SoAccPfas2livView1");
            String whcls = "IDINVITO = " + idInvito.toString();
            voAcc.setWhereClause(whcls);
            voAcc.executeQuery();

            int countAcc = voAcc.getRowCount();
            if (countAcc > 0) {
                AccPf_SoAccPfas2livViewRow fAcc = (AccPf_SoAccPfas2livViewRow) voAcc.first();
                voAcc.setCurrentRow(fAcc);
            } else {
                AccPf_SoAccPfas2livViewRow nAcc = (AccPf_SoAccPfas2livViewRow) voAcc.createRow();
                Integer idAcc = am.getNextIdAccPFAS2();
                nAcc.setIdaccpf2liv(idAcc);
                nAcc.setIdinvito(idInvito.intValue());

                Date dtoraCorr = DateUtils.getOracleDateNow();
                String user = (String) session.get("user");

                nAcc.setDtcreazione(dtoraCorr);
                nAcc.setOpcreazione(user);
                nAcc.setDtultmod(dtoraCorr);
                nAcc.setOpultmod(user);

                voAcc.insertRow(nAcc);

                am.getTransaction().commit();
            }

        }

    }


    public static void processaInvitoCorrente(ViewObject voRic) {
        Acc_AppModule am =
            (Acc_AppModule) voRic.getApplicationModule();

        Acc_RicInvitiViewRowImpl cInvRic = (Acc_RicInvitiViewRowImpl) voRic.getCurrentRow();
        Integer idInv = cInvRic.getIdinvito();

        String codTS = cInvRic.getCodts();
        Date dataCorr = DateUtils.getOracleDateNow();

        Map req = ADFContext.getCurrent().getRequestScope();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String user = (String) session.get("user");

        // setto variabili per capire se posso andare avanti e indietro
        boolean hasNext = voRic.hasNext();
        session.put("hasNext", Boolean.valueOf(hasNext));
        boolean hasPrev = voRic.hasPrevious();
        session.put("hasPrev", Boolean.valueOf(hasPrev));

        // determino se invito ha referto
        boolean refPresente = InvitoUtils.invitoHaReferto(am, idInv.intValue(), tpscr);
        session.put("refPresente", Boolean.valueOf(refPresente));

        // determino se sogg. e' escluso
        String escl = cInvRic.getEscl();
        boolean nonEscl = (escl == null || escl.equals(""));
        session.put("soggEscluso", Boolean.valueOf(!nonEscl));

        ViewObject voInvito = am.findViewObject("Acc_SoInvitoView1");
        /*MOD20071121
		voInvito.setWhereClause("IDINVITO = " + idInv.intValue());*/
        voInvito.setWhereClauseParams(new Object[] { idInv });

        voInvito.executeQuery();

        Acc_SoInvitoViewRowImpl fInv = (Acc_SoInvitoViewRowImpl) voInvito.first();
        voInvito.setCurrentRow(fInv);

        // filtro esiti invito
        Integer livello = (Integer) fInv.getLivello();
        Date dataApp = fInv.getDtapp();
        String strDataApp;

        // Se non c'e' la data appuntamento uso la data di sistema
        if (dataApp == null) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            strDataApp = df.format(new Date());
        } else {
            strDataApp = DateUtils.dateToString(dataApp.dateValue());
        }

        ViewObject voEsiti = am.findViewObject("Acc_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'DD/MM/YYYY'))");
        voEsiti.executeQuery();

        // Preimposto il prelevatore se non valorizzato
        if (fInv.getIdostetrica() == null && nonEscl) {
            String user2 = user.replaceAll("'", "''");
            ViewObject cnfUtenti = am.findViewObject("Cnf_SoCnfUtentiOperatoriView1");
            String whereCnf =
                "Cnf_SoCnfUtentiOperatori.ULSS = '" + ulss +
                "' AND Cnf_SoCnfUtentiOperatori.TPSCR = 'CI' AND Cnf_SoCnfUtentiOperatori.USERNAME = '" + user2 + "' " +
                " AND (Cnf_SoOpmedico.DTFINEVALOPMEDICO IS NULL OR Cnf_SoOpmedico.DTFINEVALOPMEDICO > SYSDATE)" +
                " AND Cnf_SoOpmedico.CODOP = 5"; // Prelevatore
            cnfUtenti.setWhereClause(whereCnf);
            cnfUtenti.executeQuery();
            Row userRow = cnfUtenti.first();
            String opAnamnesiStr = null;
            if (userRow != null) {
                fInv.setIdostetrica((Integer) userRow.getAttribute("Operatore"));
            }
        }

        // filtro ostetriche
        // Integer idCprel = (Integer) fInv.getIdcentroprelievo();
        Date dtCreaz = (Date) fInv.getDtcreazione();
        String strDate = DateUtils.dateToString(dtCreaz.dateValue());

        ViewObject voOst = am.findViewObject("Acc_OstetricaView1");
        String whcl = "( ULSS = '" + ulss + "' and TPSCR = '" + tpscr + "'";
        whcl += " and (DTFINEVALOPMEDICO is null or DTFINEVALOPMEDICO >= to_date('" + strDate + "','dd/MM/yyyy')) )";
        whcl += " or (IDOP is null)";
        voOst.setWhereClause(whcl);
        voOst.executeQuery();

        // filtro mot. intervento
        ViewObject voMotUlt = am.findViewObject("Acc_MotUltIntView1");
        voMotUlt.setWhereClause("TPSCR = '" + tpscr + "'");
        voMotUlt.executeQuery();
        //

        Date dtApp = fInv.getDtapp();
        Integer idOper = fInv.getIdostetrica();

        Integer idInvito = (Integer) fInv.getIdinvito();

        ViewObject voSoggScr = am.findViewObject("Acc_SoSoggScrView1");
        String whScr =
            "Acc_SoSoggScr.CODTS = '" + codTS + "' and Acc_SoSoggScr.ULSS = '" + ulss + "'" +
            " and Acc_SoSoggScr.TPSCR = '" + tpscr + "'";
        voSoggScr.setWhereClause(whScr);
        voSoggScr.executeQuery();

        // differenzio gestione secondo livello
        if (livello.intValue() == 1) {
            // intestazione
            session.put("testoLivello", "Accettazione 1� livello");
            Boolean hpv = (Boolean) session.get("HPV");

            // inserimento o reperimento accettazione
            ViewObject voAcc = am.findViewObject("Acc_SoAccCito1livView1");
            String whcls = "IDINVITO = " + idInvito.toString();
            voAcc.setWhereClause(whcls);
            voAcc.executeQuery();

            Integer idAcc;

            int countAcc = voAcc.getRowCount();
            if (countAcc > 0) {
                Acc_SoAccCito1livViewRowImpl fAcc = (Acc_SoAccCito1livViewRowImpl) voAcc.first();
                voAcc.setCurrentRow(fAcc);
                idAcc = fAcc.getIdacc1liv();
            } else {
                Acc_SoAccCito1livViewRowImpl nAcc = (Acc_SoAccCito1livViewRowImpl) voAcc.createRow();
                idAcc = (Integer) am.getNextIdAccettazione();
                nAcc.setIdacc1liv(idAcc);
                nAcc.setIdmot(new Integer(0));
                nAcc.setIdtpprelievo(new Integer(0));
                nAcc.setTpscr(tpscr);
                nAcc.setIdinvito(idInvito.intValue());

                // sara 26/05/2011
                if (hpv.booleanValue())
                    nAcc.setPrelievoHpv(ConfigurationConstants.CODICE_PRELIEVO_HPV_ESEGUITO);
                else
                    nAcc.setPrelievoHpv(ConfigurationConstants.CODICE_PRELIEVO_HPV_NON_ESEGUITO);

                voAcc.insertRow(nAcc);

                am.getTransaction().commit();
                voAcc.setCurrentRow(nAcc);
                // fine sara 26/05/2011
            }

            // filtro interv. precedenti
            ViewObject voSelInt = am.findViewObject("Acc_IntervPrecElencoFull1");
            /*MOD20071121
			String whInt = "IDACC1LIV = " + idAcc.toString() + " and TPSCR = '" + tpscr + "'";
			
			voSelInt.setWhereClause(whInt);
			*/
            voSelInt.setWhereClauseParam(0, tpscr);
            voSelInt.setWhereClauseParam(1, idAcc);

            voSelInt.executeQuery();
            //
            // interv di II livello: filtro per evitare query
            ViewObject voSelInt2 = am.findViewObject("Acc_IntPrec2ElencoFull1");
            /*MOD20071121
			voSelInt2.setWhereClause("1=2");
			*/
            voSelInt2.setWhereClauseParam(1, null);

            voSelInt2.executeQuery();
            //

            // filtro tipo prelievo
            ViewObject voTipo = am.findViewObject("Acc_TipoPrelView1");
            voTipo.setWhereClause("TPSCR = '" + tpscr + "'");
            voTipo.executeQuery();
            //

            // 09-12-2009, gestione anamnesi
            ViewObject voAnam = am.findViewObject("Acc_SoAnamnesiCitoView1");
            String wh = "IDACC1LIV = " + idAcc.toString();
            voAnam.setWhereClause(wh);
            voAnam.executeQuery();

            int countAnam = voAnam.getRowCount();

            // Se non c'e' l'anamnesi, copio quella precedente (se esiste)
            if (countAnam == 0) {
                riportaAnam(am, codTS, ulss, idAcc, livello.intValue(), user, dtApp);
                voAnam.setWhereClause(wh);
                voAnam.executeQuery();
                countAnam = voAnam.getRowCount();
            }

            if (countAnam == 0) {
                Acc_SoAnamnesiCitoViewRow nAnam = (Acc_SoAnamnesiCitoViewRow) voAnam.createRow();

                Integer idAnam = (Integer) am.getNextIdAnamCito();

                nAnam.setIdAnamci(idAnam);
                nAnam.setIdacc1liv(idAcc);
                nAnam.setCodts(codTS);
                nAnam.setDtcreazione(dataCorr);
                nAnam.setOpcreazione(user);
                nAnam.setDtultmod(dataCorr);
                nAnam.setOpultmod(user);
                nAnam.setUlss(ulss);
                nAnam.setTpscr(tpscr);
                nAnam.setIdopanamnesi(idOper);
                nAnam.setDtanamnesi(dtApp);

                nAnam.setGravidanza(new Integer(0));
                nAnam.setAllattamento(new Integer(0));
                nAnam.setPresenzaIud(new Integer(0));
                nAnam.setContraccTorm(new Integer(0));
                nAnam.setMenopausa(new Integer(0));
                nAnam.setChemioUlt1(new Integer(0));
                nAnam.setRadioUlt3(new Integer(0));

                voAnam.insertRow(nAnam);

                am.getTransaction().commit();
            }
        }

        // livello = 2
        else {
            // intestazione
            session.put("testoLivello", "Accettazione 2� livello");

            ViewObject voAcc2 = am.findViewObject("Acc_SoAccCito2livView1");
            /*MOD20071121
			String whcls = "IDINVITO = " + idInvito.toString();
			voAcc2.setWhereClause(whcls);
			*/
            voAcc2.setWhereClauseParams(new Object[] { idInvito });

            voAcc2.executeQuery();

            String ulss2 = fInv.getUlss();

            Integer inv1liv = (Integer) getIdInv1liv(dtApp, codTS, ulss2);

            Integer idAcc2;

            int countAcc2 = voAcc2.getRowCount();
            if (countAcc2 > 0) {
                Acc_SoAccCito2livViewRow fAcc2 = (Acc_SoAccCito2livViewRow) voAcc2.first();
                voAcc2.setCurrentRow(fAcc2);
                idAcc2 = fAcc2.getIdacc2liv();
            } else {
                Acc_SoAccCito2livViewRow nAcc2 = (Acc_SoAccCito2livViewRow) voAcc2.createRow();
                idAcc2 = am.getNextIdAccettazione2();
                nAcc2.setIdacc2liv(idAcc2);
                nAcc2.setIdmot(new Integer(0));
                nAcc2.setTpscr(tpscr);
                nAcc2.setIdinvito(idInvito.intValue());
                voAcc2.insertRow(nAcc2);

                am.getTransaction().postChanges();

                String insStmt =
                    "insert into SO_INTERVENTO_PREC2LIV select " + "SO_INTERVENTO_PREC.IDINTERVENTO," +
                    idAcc2.toString() + "," + "SO_INTERVENTO_PREC.TPSCR," + "SO_INTERVENTO_PREC.ANNOINTERVENTO " +
                    "FROM SO_INTERVENTO_PREC, SO_ACC_CITO1LIV " + "where SO_ACC_CITO1LIV.idinvito = " +
                    inv1liv.toString() + " and SO_INTERVENTO_PREC.IDACC1LIV = SO_ACC_CITO1LIV.IDACC1LIV";

                am.getTransaction().executeCommand(insStmt);

                am.getTransaction().commit();
            }

            // filtro interv. precedenti
            ViewObject voSelInt2 = am.findViewObject("Acc_IntPrec2ElencoFull1");
            /*MOD20071121
			String whInt = "IDACC2LIV = " + idAcc.toString() + " and TPSCR = '" + tpscr + "'";
			voSelInt2.setWhereClause(whInt);
			*/
            voSelInt2.setWhereClauseParam(0, tpscr);
            voSelInt2.setWhereClauseParam(1, idAcc2);

            voSelInt2.executeQuery();

            // interventi di primo livello: filtro per evitare query
            ViewObject voSelInt1 = am.findViewObject("Acc_IntervPrecElencoFull1");
            /*MOD20071121
			voSelInt1.setWhereClause("1=2");
			*/
            voSelInt1.setWhereClauseParam(1, null);

            voSelInt1.executeQuery();

            // filtro referto I livello
            ViewObject voRef = am.findViewObject("Acc_Referto1livView1");
            /*MOD20071121
			if (inv1liv == null) {
				voRef.setWhereClause("1=2");
			} else {
				voRef.setWhereClause("IDINVITO = " + inv1liv.toString());
			}*/

            voRef.setWhereClauseParam(0, inv1liv);

            voRef.executeQuery();

            // 09-12-2009, gestione anamnesi
            ViewObject voAnam = am.findViewObject("Acc_SoAnamnesiCitoView1");
            String wh = "IDACC2LIV = " + idAcc2.toString();
            voAnam.setWhereClause(wh);
            voAnam.executeQuery();

            int countAnam = voAnam.getRowCount();

            // Se non c'e' l'anamnesi, copio quella precedente (se esiste)
            if (countAnam == 0) {
                riportaAnam(am, codTS, ulss, idAcc2, livello.intValue(), user, dtApp);
                voAnam.setWhereClause(wh);
                voAnam.executeQuery();
                countAnam = voAnam.getRowCount();
            }

            if (countAnam == 0) {
                Acc_SoAnamnesiCitoViewRow nAnam = (Acc_SoAnamnesiCitoViewRow) voAnam.createRow();

                Integer idAnam = (Integer) am.getNextIdAnamCito();

                nAnam.setIdAnamci(idAnam);
                nAnam.setIdacc2liv(idAcc2);
                nAnam.setCodts(codTS);
                nAnam.setDtcreazione(dataCorr);
                nAnam.setOpcreazione(user);
                nAnam.setDtultmod(dataCorr);
                nAnam.setOpultmod(user);
                nAnam.setUlss(ulss);
                nAnam.setTpscr(tpscr);
                nAnam.setIdopanamnesi(idOper.intValue());
                nAnam.setDtanamnesi(dtApp);

                // se c'e' anamnesi di primo livello devo riportarla sul secondo liv.
                String strDtApp = DateUtils.dateToString(dtApp.dateValue());

                String qry =
                    "SELECT a.id_anamci, i.dtapp FROM so_anamnesi_cito a, SO_ACC_CITO1LIV c, so_invito i " +
                    "where a.codts = '" + codTS + "' " + "and c.idacc1liv = a.idacc1liv and i.idinvito = c.idinvito " +
                    "and i.dtapp <= to_date('" + strDtApp + "','dd/mm/yyyy') " + "order by 2 desc";

                ViewObject voRicAnam = am.createViewObjectFromQueryStmt("", qry);
                voRicAnam.setRangeSize(-1);
                voRicAnam.executeQuery();

                int countRic = voRicAnam.getRowCount();

                if (countRic > 0) {
                    Row rowAnam1liv = voRicAnam.first();

                    Integer idAnam1liv =
                        rowAnam1liv.getAttribute(0) != null ?
                        ((oracle.jbo.domain.Number) rowAnam1liv.getAttribute(0)).intValue() : null;

                    ViewObject voAnam1liv = am.findViewObject("Acc_SoAnamnesiCitoView1liv");
                    String wh1liv = "ID_ANAMCI = " + idAnam1liv.toString();
                    voAnam1liv.setWhereClause(wh1liv);
                    voAnam1liv.executeQuery();

                    Acc_SoAnamnesiCitoViewRow anam1liv = (Acc_SoAnamnesiCitoViewRow) voAnam1liv.first();

                    nAnam.setGravidanza(anam1liv.getGravidanza());
                    nAnam.setMeseGravidanza(anam1liv.getMeseGravidanza());
                    nAnam.setAllattamento(anam1liv.getAllattamento());
                    nAnam.setPresenzaIud(anam1liv.getPresenzaIud());
                    nAnam.setContraccTorm(anam1liv.getContraccTorm());
                    nAnam.setMenopausa(anam1liv.getMenopausa());
                    nAnam.setDtUltMestr(anam1liv.getDtUltMestr());
                    nAnam.setChemioUlt1(anam1liv.getChemioUlt1());
                    nAnam.setRadioUlt3(anam1liv.getRadioUlt3());
                    nAnam.setGravTermine(anam1liv.getGravTermine());
                    nAnam.setAborti(anam1liv.getAborti());
                    nAnam.setPartiPrematuri(anam1liv.getPartiPrematuri());
                    nAnam.setFigliViventi(anam1liv.getFigliViventi());
                } else {
                    nAnam.setGravidanza(new Integer(0));
                    nAnam.setAllattamento(new Integer(0));
                    nAnam.setPresenzaIud(new Integer(0));
                    nAnam.setContraccTorm(new Integer(0));
                    nAnam.setMenopausa(new Integer(0));
                    nAnam.setChemioUlt1(new Integer(0));
                    nAnam.setRadioUlt3(new Integer(0));
                }

                voAnam.insertRow(nAnam);
                am.getTransaction().commit();
            }
        }
    }

    public static Integer getIdInv1liv(Date dtApp, String codTs, String ulss) {
        Acc_AppModule am =
            (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
        String dtAppS = DateUtils.dateToString(dtApp.dateValue());
        String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
        String wh =
            "LIVELLO = 1 AND CODTS = '" + codTs + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr +
            "' AND DTAPP < to_date('" + dtAppS + "','dd/MM/yyyy')";

        String qry = " SELECT IDINVITO,DTAPP FROM SO_INVITO WHERE " + wh + " ORDER BY DTAPP DESC";
        ViewObject vo = am.createViewObjectFromQueryStmt("", qry);
        vo.setRangeSize(-1);
        vo.executeQuery();

        int count = vo.getRowCount();
        if (count > 0) {
            Row fInv = vo.first();
            Integer idInv =
                fInv.getAttribute(0) != null ? ((oracle.jbo.domain.Number) fInv.getAttribute(0)).intValue() : null;
            return idInv;

        } else {
            return null;
        }

    }

    /**
     * 20081117
     * Metodo che inizializza le query di ricerca in modo che abbiano risultato vuoto,
     * per ottimizzare
     * @param tpscr
     * @param ulss
     * @param ctx
     */
    public static void filterAcc(String ulss, String tpscr) {
        ApplicationModule am =
            (ApplicationModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Acc_RicInvitiView1");
        vo.setWhereClause("1=2");

        // Trial per citologico
        Integer idTrialAttr = (Integer) ADFContext.getCurrent().getSessionScope().get("trial");
        Integer idTrial = null;
        try {
            idTrial = new Integer(idTrialAttr);
        } catch (Exception e) {
            idTrial = new Integer(0);
        }
        Object[] trialParams = new Object[] { idTrial, tpscr };
        vo = am.findViewObject("A_SelTrialStatoView1");
        vo.setWhereClauseParams(trialParams);
        vo = am.findViewObject("A_SelTrialBraccioView1");
        vo.setWhereClauseParams(trialParams);

        ViewObject voTipiDoc = am.findViewObject("Acc_SelTipiDocView1");
        voTipiDoc.setWhereClauseParams(new Object[] { ulss });
        voTipiDoc.executeQuery();

        am =
            (ApplicationModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
        vo = am.findViewObject("AccCo_RicInvitiView1");
        vo.setWhereClause("1=2");
        am =
            (ApplicationModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
        vo = am.findViewObject("AccMa_RicInvitiView1");
        vo.setWhereClause("1=2");
        am =
            (ApplicationModule) BindingContext.getCurrent().findDataControl("AccCa_AppModuleDataControl").getDataProvider();
        vo = am.findViewObject("AccCa_RicInvitiView1");
        vo.setWhereClause("1=2");
        am =
            (ApplicationModule) BindingContext.getCurrent().findDataControl("AccPf_AppModuleDataControl").getDataProvider();
        vo = am.findViewObject("AccPf_RicInvitiView1");
        vo.setWhereClause("1=2");
    }

    public static void requeryElencoMammo(AccMa_AppModule am) {
        ViewObject vo = am.findViewObject("AccMa_RicInvitiView1");
        ViewHelper.queryAndRestoreCurrentRow(vo);
    }
    
    public static void requeryElenco(Acc_AppModule am)
      {
        ViewObject vo = am.findViewObject("Acc_RicInvitiView1");
        ViewHelper.queryAndRestoreCurrentRow(vo);
      }
    
    public static void requeryElencoColon(AccCo_AppModule am)
      {
        ViewObject vo = am.findViewObject("AccCo_RicInvitiView1");
        ViewHelper.queryAndRestoreCurrentRow(vo);
      }
}
