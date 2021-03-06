package model.client;

import java.math.BigDecimal;

import java.sql.Connection;

import java.util.HashMap;

import model.common.ImpExp_AppModule;
import model.common.RefCo_AppModule;
import model.common.RefMa_AppModule;
import model.common.RefPf_AppModule;
import model.common.Ref_AppModule;

import oracle.jbo.client.remote.ApplicationModuleImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Thu Feb 02 16:21:06 CET 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class ImpExp_AppModuleClient extends ApplicationModuleImpl implements ImpExp_AppModule {
    /**
     * This is the default constructor (do not remove).
     */
    public ImpExp_AppModuleClient() {
    }


    public String callImportEsclusioni(String ulss, String tpscr, String tipoFlusso, String eroganti, String user,
                                       String modalita) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callImportEsclusioni",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String" },
                                        new Object[] { ulss, tpscr, tipoFlusso, eroganti, user, modalita });
        return (String) _ret;
    }


    public Ref_AppModule getRef_AppModule() {
        return (Ref_AppModule) findApplicationModule("Ref_AppModule1");
    }

    public RefCo_AppModule getRefCo_AppModule() {
        return (RefCo_AppModule) findApplicationModule("RefCo_AppModule1");
    }

    public RefMa_AppModule getRefMa_AppModule() {
        return (RefMa_AppModule) findApplicationModule("RefMa_AppModule1");
    }

    public RefPf_AppModule getRefPf_AppModule() {
        return (RefPf_AppModule) findApplicationModule("RefPf_AppModule1");
    }


    public HashMap callSPSwrite(String ulss, String tpscr, String dal, String al, BigDecimal centro_prel,
                                BigDecimal centro_ref, String prestazioni) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callSPSwrite",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.math.BigDecimal",
                                                       "java.math.BigDecimal", "java.lang.String" },
                                        new Object[] { ulss, tpscr, dal, al, centro_prel, centro_ref, prestazioni });
        return (HashMap) _ret;
    }


    public HashMap callDWHwrite(String ulss, String tpscr, String anno, String periodo, String test) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callDWHwrite",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String" },
                                        new Object[] { ulss, tpscr, anno, periodo, test });
        return (HashMap) _ret;
    }

    public HashMap callExport(String ulss, String tpscr, BigDecimal centro, String dal, String al,
                              BigDecimal centro_prel, BigDecimal livello, BigDecimal exptype, String tpdip,
                              BigDecimal hpv, BigDecimal id_invito, String user) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callExport",
                                        new String[] { "java.lang.String", "java.lang.String", "java.math.BigDecimal",
                                                       "java.lang.String", "java.lang.String", "java.math.BigDecimal",
                                                       "java.math.BigDecimal", "java.math.BigDecimal",
                                                       "java.lang.String", "java.math.BigDecimal",
                                                       "java.math.BigDecimal", "java.lang.String" },
                                        new Object[] { ulss, tpscr, centro, dal, al, centro_prel, livello, exptype,
                                                       tpdip, hpv, id_invito, user });
        return (HashMap) _ret;
    }

    public String callFunCodtsTrovaDoc(String codts, String ulss, String tipoDoc) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callFunCodtsTrovaDoc",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String" },
                                        new Object[] { codts, ulss, tipoDoc });
        return (String) _ret;
    }

    public HashMap callGetCentroRichiamo(String tessera, String ulss, String tpscr, BigDecimal idinvito,
                                         BigDecimal livellorichiamo) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callGetCentroRichiamo",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.math.BigDecimal", "java.math.BigDecimal" },
                                        new Object[] { tessera, ulss, tpscr, idinvito, livellorichiamo });
        return (HashMap) _ret;
    }

    public String callImportAnagrafe(String ulss, Integer modalita) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callImportAnagrafe",
                                        new String[] { "java.lang.String", "java.lang.Integer" },
                                        new Object[] { ulss, modalita });
        return (String) _ret;
    }

    public String callImportAnagrafeSingola(String ulss, BigDecimal chiaveMsg) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callImportAnagrafeSingola",
                                        new String[] { "java.lang.String", "java.math.BigDecimal" },
                                        new Object[] { ulss, chiaveMsg });
        return (String) _ret;
    }

    public String callImportEsclusioni(String ulss, String tpscr, String tipoFlusso, String eroganti, String user,
                                       String modalita, int anno, int fase) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callImportEsclusioni",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "int", "int" },
                                        new Object[] { ulss, tpscr, tipoFlusso, eroganti, user, modalita,
                                                       new Integer(anno), new Integer(fase) });
        return (String) _ret;
    }

    public String callImportPresenze(String ulss, String tpscr, BigDecimal centro, String tpdip, Integer[] rc) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callImportPresenze",
                                        new String[] { "java.lang.String", "java.lang.String", "java.math.BigDecimal",
                                                       "java.lang.String", "[Ljava.lang.Integer;" },
                                        new Object[] { ulss, tpscr, centro, tpdip, rc });
        return (String) _ret;
    }

    public String callImportReferti(String ulss, String tpscr, BigDecimal centro, String tpdip, Integer[] rc) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callImportReferti",
                                        new String[] { "java.lang.String", "java.lang.String", "java.math.BigDecimal",
                                                       "java.lang.String", "[Ljava.lang.Integer;" },
                                        new Object[] { ulss, tpscr, centro, tpdip, rc });
        return (String) _ret;
    }

    public String callImportRefertiFromDB(String ulss, String tpscr, BigDecimal centro, String dal, String al) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callImportRefertiFromDB",
                                        new String[] { "java.lang.String", "java.lang.String", "java.math.BigDecimal",
                                                       "java.lang.String", "java.lang.String" },
                                        new Object[] { ulss, tpscr, centro, dal, al });
        return (String) _ret;
    }

    public HashMap callPrepareCancToDB(String ulss, String tpscr, BigDecimal centro, String dal, String al,
                                       BigDecimal centro_prel, BigDecimal livello) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callPrepareCancToDB",
                                        new String[] { "java.lang.String", "java.lang.String", "java.math.BigDecimal",
                                                       "java.lang.String", "java.lang.String", "java.math.BigDecimal",
                                                       "java.math.BigDecimal" },
                                        new Object[] { ulss, tpscr, centro, dal, al, centro_prel, livello });
        return (HashMap) _ret;
    }

    public HashMap callSPSwrite(String ulss, String tpscr, String dal, String al, BigDecimal centro_prel,
                                String centro_ref, String prestazioni, String tipi_invito, String labor_hpv) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callSPSwrite",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.math.BigDecimal", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String" },
                                        new Object[] { ulss, tpscr, dal, al, centro_prel, centro_ref, prestazioni,
                                                       tipi_invito, labor_hpv });
        return (HashMap) _ret;
    }

    public HashMap callTUwriteAnagrafeInFascia(String ulss, String tpscr, String data, String filename,
                                               String delimiter, String format) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callTUwriteAnagrafeInFascia",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String" },
                                        new Object[] { ulss, tpscr, data, filename, delimiter, format });
        return (HashMap) _ret;
    }

    public HashMap callTUwriteAnagrafeTU(String ulss, String tpscr, String data_dal, String data_al, String filename,
                                         String delimiter, String format) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callTUwriteAnagrafeTU",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String" },
                                        new Object[] { ulss, tpscr, data_dal, data_al, filename, delimiter, format });
        return (HashMap) _ret;
    }

    public HashMap callTUwriteEsclusioni(String ulss, String tpscr, String data_dal, String data_al, String filename,
                                         String delimiter, String format) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callTUwriteEsclusioni",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String" },
                                        new Object[] { ulss, tpscr, data_dal, data_al, filename, delimiter, format });
        return (HashMap) _ret;
    }

    public HashMap callTUwriteInviti(String ulss, String tpscr, String data_dal, String data_al, String inviti_filename,
                                     String anam_filename, String anam_extra_filename, String ref1_filename,
                                     String ref1_extra_filename, String ref2_filename, String ref2_extra_filename,
                                     String int_filename, String int_extra_filename, String endo_filename,
                                     String delimiter, String format) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callTUwriteInviti",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String" },
                                        new Object[] { ulss, tpscr, data_dal, data_al, inviti_filename, anam_filename,
                                                       anam_extra_filename, ref1_filename, ref1_extra_filename,
                                                       ref2_filename, ref2_extra_filename, int_filename,
                                                       int_extra_filename, endo_filename, delimiter, format });
        return (HashMap) _ret;
    }

    public String callUnpackReferti(String ulss, String tpscr) {
        Object _ret =
            this.riInvokeExportedMethod(this, "callUnpackReferti",
                                        new String[] { "java.lang.String", "java.lang.String" },
                                        new Object[] { ulss, tpscr });
        return (String) _ret;
    }

    public void doRollback() {
        Object _ret = this.riInvokeExportedMethod(this, "doRollback", null, null);
        return;
    }

    public void doRollback(String voName) {
        Object _ret =
            this.riInvokeExportedMethod(this, "doRollback", new String[] { "java.lang.String" },
                                        new Object[] { voName });
        return;
    }

    public void doRollback(String voName1, String voName2) {
        Object _ret =
            this.riInvokeExportedMethod(this, "doRollback", new String[] { "java.lang.String", "java.lang.String" },
                                        new Object[] { voName1, voName2 });
        return;
    }

    public void doRollback(String[] voNames) {
        Object _ret =
            this.riInvokeExportedMethod(this, "doRollback", new String[] { "[Ljava.lang.String;" },
                                        new Object[] { voNames });
        return;
    }

    public void filterConfiguration(String ulss, String scr, boolean regionale) {
        Object _ret =
            this.riInvokeExportedMethod(this, "filterConfiguration",
                                        new String[] { "java.lang.String", "java.lang.String", "boolean" },
                                        new Object[] { ulss, scr, new Boolean(regionale) });
        return;
    }

    public Connection getDBConnection() {
        Object _ret = this.riInvokeExportedMethod(this, "getDBConnection", null, null);
        return (Connection) _ret;
    }

    public Integer getNextEndoscopia() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextEndoscopia", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAccColon1() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAccColon1", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAccColon2() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAccColon2", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAccMammo1() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAccMammo1", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAccMammo2() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAccMammo2", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAccPFAS1() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAccPFAS1", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAccPFAS2() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAccPFAS2", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAccettazione() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAccettazione", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAccettazione2() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAccettazione2", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAllegato() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAllegato", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAmbIstric() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAmbIstric", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAnamCito() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAnamCito", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAnamMx() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAnamMx", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAnamMxSint() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAnamMxSint", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAnamco() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAnamco", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdAppuntamento() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdAppuntamento", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdCentriRacc() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdCentriRacc", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdCnfFestivita() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdCnfFestivita", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdCnfRef1livMA() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdCnfRef1livMA", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdCnfRef2livMA() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdCnfRef2livMA", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdCnfRef2livPF() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdCnfRef2livPF", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdCnfRef3livMA() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdCnfRef3livMA", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdCnfRefPF() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdCnfRefPF", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdConfReferto1liv() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdConfReferto1liv", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdConfReferto2liv() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdConfReferto2liv", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdDomanda() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdDomanda", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdEsclusione() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdEsclusione", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdInserto() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdInserto", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdIntervento() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdIntervento", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdInterventoCO() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdInterventoCO", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdInterventoMA() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdInterventoMA", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdInvito() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdInvito", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdProceduraPF() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdProceduraPF", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdQuestionario() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdQuestionario", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdRef1livMA() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdRef1livMA", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdRef2livMA() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdRef2livMA", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdRefPF() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdRefPF", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdRefPF2liv() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdRefPF2liv", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdRefPfasDati() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdRefPfasDati", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdRefcardioDati() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdRefcardioDati", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdReferto1liv() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdReferto1liv", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdReferto1livCO() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdReferto1livCO", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdReferto2liv() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdReferto2liv", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdReferto2livCO() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdReferto2livCO", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdRefertocardio() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdRefertocardio", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdStoricoConsenso() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdStoricoConsenso", null, null);
        return (Integer) _ret;
    }

    public Integer getNextIdUtenteOperatore() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdUtenteOperatore", null, null);
        return (Integer) _ret;
    }

    public Integer getNextPID() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextPID", null, null);
        return (Integer) _ret;
    }

    public void log(String azienda, String tipoDip, String verso, String msg, String tpscr) {
        Object _ret =
            this.riInvokeExportedMethod(this, "log",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String" },
                                        new Object[] { azienda, tipoDip, verso, msg, tpscr });
        return;
    }

    public void logDWH(String azienda, String tipoDip, String verso, String msg, String tpscr) {
        Object _ret =
            this.riInvokeExportedMethod(this, "logDWH",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String" },
                                        new Object[] { azienda, tipoDip, verso, msg, tpscr });
        return;
    }

    public void logMsg(String codts, String group, String coderrore, String impexp, String ulss, String tpscr) {
        Object _ret =
            this.riInvokeExportedMethod(this, "logMsg",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String", "java.lang.String" },
                                        new Object[] { codts, group, coderrore, impexp, ulss, tpscr });
        return;
    }

    public void logSPS(String azienda, String tipoDip, String verso, String msg, String tpscr) {
        Object _ret =
            this.riInvokeExportedMethod(this, "logSPS",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String",
                                                       "java.lang.String", "java.lang.String" },
                                        new Object[] { azienda, tipoDip, verso, msg, tpscr });
        return;
    }

    public void preapareJournaling(String user, String ulss, String tpscr) {
        Object _ret =
            this.riInvokeExportedMethod(this, "preapareJournaling",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String" },
                                        new Object[] { user, ulss, tpscr });
        return;
    }

    public Integer getNextIdImpExpLog() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdImpExpLog", null, null);
        return (Integer) _ret;

    }

}
