package model.common;

import oracle.jbo.ApplicationModule;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public interface Agenda_AppModule extends ApplicationModule {
  java.util.HashMap callPrepareCancToDB(String ulss, String tpscr, java.math.BigDecimal centro, String dal, String al, java.math.BigDecimal centro_prel, java.math.BigDecimal livello);

  void doRollback();

  void doRollback(String voName);

  void doRollback(String voName1, String voName2);


    java.sql.Connection getDBConnection();

  Integer getNextEndoscopia();

  Integer getNextIdAccColon1();

  Integer getNextIdAccColon2();

  Integer getNextIdAccMammo1();

  Integer getNextIdAccMammo2();

  Integer getNextIdAccettazione();

  Integer getNextIdAccettazione2();

  Integer getNextIdAllegato();

  Integer getNextIdAmbIstric();

  Integer getNextIdAnamMx();

  Integer getNextIdAnamMxSint();

  Integer getNextIdAnamco();

  Integer getNextIdAppuntamento();

  Integer getNextIdCentriRacc();

  Integer getNextIdCnfRef1livMA();

  Integer getNextIdCnfRef2livMA();

  Integer getNextIdCnfRef3livMA();

  Integer getNextIdConfReferto1liv();

  Integer getNextIdConfReferto2liv();

  Integer getNextIdEsclusione();

  Integer getNextIdInserto();

  Integer getNextIdIntervento();

  Integer getNextIdInterventoCO();

  Integer getNextIdInterventoMA();

  Integer getNextIdInvito();

  Integer getNextIdRef1livMA();

  Integer getNextIdRef2livMA();

  Integer getNextIdReferto1liv();

  Integer getNextIdReferto1livCO();

  Integer getNextIdReferto2liv();

  Integer getNextIdReferto2livCO();

  java.util.HashMap callGetCentroRichiamo(String tessera, String ulss, String tpscr, java.math.BigDecimal idinvito, java.math.BigDecimal livellorichiamo);

  Integer getNextIdAnamCito();

  Integer getNextPID();

    String callFunCodtsTrovaDoc(String codts, String ulss, String tipoDoc);

    void doRollback(String[] voNames);

    Integer getNextIdAccPFAS1();

    Integer getNextIdAccPFAS2();

    Integer getNextIdCnfFestivita();

    Integer getNextIdCnfRef2livPF();

    Integer getNextIdCnfRefPF();

    Integer getNextIdDomanda();

    Integer getNextIdProceduraPF();

    Integer getNextIdQuestionario();

    Integer getNextIdRefPF();

    Integer getNextIdRefPF2liv();

    Integer getNextIdRefPfasDati();

    Integer getNextIdRefcardioDati();

    Integer getNextIdRefertocardio();

    Integer getNextIdStoricoConsenso();

    Integer getNextIdUtenteOperatore();

    void preapareJournaling(String user, String ulss, String tpscr);
}