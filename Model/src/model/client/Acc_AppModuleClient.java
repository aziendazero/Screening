package model.client;

import java.math.BigDecimal;

import java.sql.Connection;

import java.util.HashMap;

import model.common.Acc_AppModule;

import oracle.jbo.client.remote.ApplicationModuleImpl;

public class Acc_AppModuleClient extends ApplicationModuleImpl implements Acc_AppModule {
    /**
     * This is the default constructor (do not remove).
     */
    public Acc_AppModuleClient() {
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

    public void dummy() {
        Object _ret = this.riInvokeExportedMethod(this, "dummy", null, null);
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

    public Integer getNextIdImpExpLog() {
        Object _ret = this.riInvokeExportedMethod(this, "getNextIdImpExpLog", null, null);
        return (Integer) _ret;

    }


    public void preapareJournaling(String user, String ulss, String tpscr) {
        Object _ret =
            this.riInvokeExportedMethod(this, "preapareJournaling",
                                        new String[] { "java.lang.String", "java.lang.String", "java.lang.String" },
                                        new Object[] { user, ulss, tpscr });
        return;
    }
}

