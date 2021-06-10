package model.common;

import java.math.BigDecimal;

import java.sql.Connection;

import java.util.HashMap;

public interface Parent_AppModule  
{
    public void doRollback();

    public void doRollback(String voName);

    public void doRollback(String voName1, String voName2);

    public void doRollback(String[] voNames);
    
    public Integer getNextIdInvito();

    public Integer getNextIdAllegato();

    public Integer getNextIdAppuntamento();
    
    public Integer getNextIdReferto1liv();
    
    public Integer getNextIdReferto2liv();
    
    public Integer getNextIdConfReferto1liv();
    
    public Integer getNextIdConfReferto2liv();
    
    public Integer getNextIdEsclusione();
    
    public Integer getNextIdIntervento();

    public Integer getNextIdAccettazione();

    public Integer getNextIdAccettazione2();
    
    public Connection getDBConnection();
    
    public Integer getNextIdAccPFAS1();
    
    public Integer getNextIdAccColon1();

    public Integer getNextIdAccColon2();
    
    public Integer getNextIdAnamco();
    
    public Integer getNextIdReferto1livCO();
    
    public Integer getNextIdReferto2livCO();
    
     public Integer getNextEndoscopia();
    
     public Integer getNextIdInterventoCO();
    
    //   public Integer getNextIdCnfRef2lCO();

    public Integer getNextIdCentriRacc();
    
    public Integer getNextIdAmbIstric();

    public Integer getNextIdAccMammo1();

    public Integer getNextIdAccMammo2();
    
     public Integer getNextIdRef1livMA();

      public Integer getNextIdCnfRef1livMA();

    public Integer getNextIdRef2livMA();

    public Integer getNextIdCnfRef2livMA();

    public Integer getNextIdInterventoMA();
    
    public Integer getNextIdCnfRef3livMA();

    public Integer getNextIdAnamMx();
    
    public Integer getNextIdAnamMxSint();
    
    public Integer getNextIdInserto();
    
    public Integer getNextIdAnamCito();
    
    public Integer getNextPID();
    
    public Integer getNextIdRefPF();
    
    public Integer getNextIdRefPfasDati();
    
    public Integer getNextIdCnfRefPF();
    
    public Integer getNextIdCnfFestivita();
    
    public Integer getNextIdImpExpLog();    
    
    public HashMap callPrepareCancToDB(String ulss,
                                          String tpscr,
                                          BigDecimal centro,
                                          String dal,
                                          String al,
                                          BigDecimal centro_prel,
                                          BigDecimal livello);
    
    public HashMap callGetCentroRichiamo(String tessera,
                                          String ulss,
                                          String tpscr,
                                          BigDecimal idinvito,
                                          BigDecimal livellorichiamo);
                                          
    public void preapareJournaling(String user, String ulss, String tpscr);
    
    public String callFunCodtsTrovaDoc(String codts,
                                          String ulss,
                                          String tipoDoc);

}
