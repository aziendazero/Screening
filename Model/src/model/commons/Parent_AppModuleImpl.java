package model.commons;

import java.math.BigDecimal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

import model.common.Parent_AppModule;

import oracle.adf.share.ADFContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.DBTransaction;
import oracle.jbo.server.SequenceImpl;
import oracle.jbo.server.TransactionEvent;

public class Parent_AppModuleImpl extends ApplicationModuleImpl implements Parent_AppModule {
    
    
    
    public Parent_AppModuleImpl() {
    }
    
    @Override
    protected void afterValidate(TransactionEvent transactionEvent) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sessionMap = adfCtx.getSessionScope();
    
        String user = (String)sessionMap.get("user");
        String tpscr = (String)sessionMap.get("scr");
        String ulss = (String)sessionMap.get("ulss");
        
        preapareJournaling(user,ulss,tpscr);
        super.afterValidate(transactionEvent);
    }
    
    
    /** Esegue il rollback ripristinando le righe correnti
    * di tutti i viewObject. Non abusare nell'utilizzo per evitare 
    * problemi di performance
    * */
    public void doRollback()
    {
    
    HashMap keys=this.prepareForRollback(this.getViewObjectNames());
    
    this.getDBTransaction().rollback();
    
    this.restoreAfterRollback(keys,this.getViewObjectNames());
    keys.clear();
    }
  
    /**
    * Esegue il rollback ripristinando la riga corrente
    * del viewobject indicato
    * @param voName nome del viewObject da ripristinare
    */
    public void doRollback(String voName)
    {
    HashMap keys=this.prepareForRollback(new String[]{voName});
    this.getDBTransaction().rollback();
    
    this.restoreAfterRollback(keys,new String[]{voName});
    keys.clear();
    
    
    }
    
    /**
    * Esegue il rollback ripristinando la riga corrente
    * dei viewobjects indicati
    * @param voName2 nome di un view object
    * @param voName1 nome di un view object
    */
    public void doRollback(String voName1,String voName2)
    {
    HashMap keys=this.prepareForRollback(new String[]{voName1,voName2});
    this.getDBTransaction().rollback();
    
    this.restoreAfterRollback(keys,new String[]{voName1,voName2});
    keys.clear();
    }
    
    /**
    * Esegue il rollback ripristinando la riga corrente
    * dei viewobjects indicati
    * @param voNames vettore con i nomi dei viewobject da ripristinare
    */
    public void doRollback(String[] voNames)
    {
    HashMap keys=this.prepareForRollback(voNames);
    this.getDBTransaction().rollback();
    try{
      this.restoreAfterRollback(keys, voNames);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    keys.clear();
    }
    
    /**
    * Salva le chiavi delle righe correnti dei viewObject indicati, 
    * in  modo da potrle ripristinare dopo il rollback
    * @return hashmap in cui le chiavi sono i nomi dei viewobject
    * ed i valori sono le chiavi primarie della riga corrente
    * @param vo_names array con i nomi dei view object da gestire
    */
    private HashMap prepareForRollback(String[] vo_names)
    {
    HashMap keys=new HashMap();
    //per ogni viewObject
    for(int i=0;i<vo_names.length;i++)
    {
        //leggo il viewObject
        ViewObject vo=this.findViewObject(vo_names[i]);
        if(vo==null) continue;
        //se c'è una riga corrente...
        Row r=vo.getCurrentRow();
        if(r!=null)
        {
        //...salvo i dati relativi alla riga corrente
            Key k=r.getKey();
            String s = k.toString();
            
            keys.put(vo_names[i],r.getKey());
        }
    }
    return keys;
    }
    
    
    /**
    * Ristabilisce le righe correnti precedenti al rollabck
    * @param keys hashmap in cui le chiavi sono i nomi dei viewobject
    * ed i valori sono le chiavi primarie della riga corrente da ripristinare
    */
    private void restoreAfterRollback(HashMap keys, String[] voNames)
    {
    // Object[] keySet= keys.keySet().toArray();
    //  for(int i=0;i<keySet.length;i++)
    //devo rispettare l'ordine
    for(int i=0;i<voNames.length;i++)
    {
      String vo_name=voNames[i];
    //   String vo_name=(String)keySet[i];
      ViewObject vo=this.findViewObject(vo_name);
      if(vo==null) continue;
      vo.executeQuery();
    
     //se la riga non è stata cancellata riporto il focus sulla stessa
     
     Row current=vo.getRow((Key)keys.get(vo_name));
      if(current!=null){
        vo.setCurrentRow(current);
        //navigate table to correct range set
       int index=vo.getCurrentRowIndex();
       int range=vo.getRangeSize();
      if(range>0)
        vo.scrollRangeTo(vo.getCurrentRow(),index % range);
      else
       vo.scrollRangeTo(vo.getCurrentRow(),index);
      
      }
    }
    
    //  iter.remove();
    }
    
    public Integer getNextIdInvito()
    {
    SequenceImpl s = new SequenceImpl("SO_INVITI_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAllegato()
    {
    SequenceImpl s = new SequenceImpl("SO_ALLEGATI_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAppuntamento()
    {
    SequenceImpl s = new SequenceImpl("SO_APPUNTAMENTO_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdReferto1liv()
    {
    SequenceImpl s = new SequenceImpl("SO_REFERTI1LIV_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdReferto2liv()
    {
    SequenceImpl s = new SequenceImpl("SO_REFERTI2LIV_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdConfReferto1liv()
    {
    SequenceImpl s = new SequenceImpl("SO_CNFREFERTI1LIV_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdConfReferto2liv()
    {
    SequenceImpl s = new SequenceImpl("SO_CNFREFERTI2LIV_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdEsclusione()
    {
    SequenceImpl s = new SequenceImpl("SO_ESCLUSIONE_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdIntervento()
    {
    SequenceImpl s = new SequenceImpl("SO_INTERVENTICITO_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAccettazione()
    {
    SequenceImpl s = new SequenceImpl("so_accettazioni_seq", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAccettazione2()
    {
    SequenceImpl s = new SequenceImpl("so_accettazioni2_seq", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAccPFAS1()
    {
    SequenceImpl s = new SequenceImpl("SO_ACCPFAS1LIV_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAccPFAS2()
    {
    SequenceImpl s = new SequenceImpl("SO_ACCPFAS2LIV_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAccColon1()
    {
    SequenceImpl s = new SequenceImpl("SO_ACCCOLON1LIV_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAccColon2()
    {
    SequenceImpl s = new SequenceImpl("SO_ACCCOLON2LIV_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAnamco()
    {
    SequenceImpl s = new SequenceImpl("SO_ANAMNESICOLON_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAccMammo1()
    {
    SequenceImpl s = new SequenceImpl("SO_ACCMAMMO1LIV_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAccMammo2()
    {
    SequenceImpl s = new SequenceImpl("SO_ACCMAMMO2LIV_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAnamMx()
    {
    SequenceImpl s = new SequenceImpl("SO_ANAMNESIMX_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    
    public Integer getNextIdAnamMxSint()
    {
    SequenceImpl s = new SequenceImpl("SO_ANAMNESIMX_SINT_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdInserto()
    {
    SequenceImpl s = new SequenceImpl("SO_INSERTI_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextPID()
    {
    SequenceImpl s = new SequenceImpl("SO_CODA_PROCESSI_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    
    }
    
    public Integer getNextIdUtenteOperatore()
    {
    SequenceImpl s = new SequenceImpl("SO_CNF_UTENTI_OPERATORI_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdStoricoConsenso()
    {
    SequenceImpl s = new SequenceImpl("SO_STORICO_CONSENSO_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdQuestionario()
    {
    SequenceImpl s = new SequenceImpl("SO_CNF_QUESTIONARIO_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdDomanda()
    {
    SequenceImpl s = new SequenceImpl("SO_CNF_DOMANDE_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Connection getDBConnection()
    {
      Connection conn;
      try
      {
        conn = this.getDBTransaction().
        createCallableStatement("select 1 from dual",1).
        getConnection();
      }
      catch (Exception ex)
      {
        conn = null;        
      }
      return conn;
    }
    
    public Integer getNextIdReferto1livCO()
    {
    SequenceImpl s = new SequenceImpl("SO_REFCOLON_1LIV", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdReferto2livCO()
    {
    SequenceImpl s = new SequenceImpl("so_refertocolon_2liv_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextEndoscopia()
    {
    SequenceImpl s = new SequenceImpl("SO_ENDO_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdInterventoCO()
    {
    SequenceImpl s = new SequenceImpl("SO_INTERVENTOCOLON_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    /* public Integer getNextIdCnfRef2lCO()
    {
    SequenceImpl s = new SequenceImpl("SO_CONFENDO2L_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }*/
    
    public Integer getNextIdCentriRacc() {
    SequenceImpl s = new SequenceImpl("so_com_zone_param_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAmbIstric()
    {
    SequenceImpl s = new SequenceImpl("so_amb_istric_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdCnfFestivita()
    {
    SequenceImpl s = new SequenceImpl("SO_CNF_FESTIVITA_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdRefPF()
    {
    SequenceImpl s = new SequenceImpl("SO_REFERTOPFAS_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdRefPfasDati()
    {
    SequenceImpl s = new SequenceImpl("SO_REFPFAS_DATI_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdRef1livMA()
    {
    SequenceImpl s = new SequenceImpl("so_refmammo1liv_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    public Integer getNextIdCnfRef1livMA()
    {
    SequenceImpl s = new SequenceImpl("so_CODREFMA1liv_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdRef2livMA()
    {
    SequenceImpl s = new SequenceImpl("so_refmammo2liv_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    public Integer getNextIdCnfRef2livMA()
    {
    SequenceImpl s = new SequenceImpl("so_CODREFMA2liv_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    public Integer getNextIdCnfRefPF()
    {
    SequenceImpl s = new SequenceImpl("SO_CODREFPF1LIV_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    public Integer getNextIdCnfRef2livPF()
    {
    SequenceImpl s = new SequenceImpl("SO_CODREFPF2LIV_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    public Integer getNextIdProceduraPF()
    {
    SequenceImpl s = new SequenceImpl("SO_PROCEDURAPFAS_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    public Integer getNextIdRefPF2liv()
    {
    SequenceImpl s = new SequenceImpl("SO_REFPFAS2LIV_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    public Integer getNextIdInterventoMA()
    {
    SequenceImpl s = new SequenceImpl("so_INTmammo_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    public Integer getNextIdCnfRef3livMA()
    {
    SequenceImpl s = new SequenceImpl("so_CNFINTMA_seq", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdAnamCito()
    {
    SequenceImpl s = new SequenceImpl("SO_ANAMNESI_CITO_SEQ", getDBTransaction());     
    return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdRefertocardio() {
            SequenceImpl s = new SequenceImpl("SO_REFERTOCARDIO_SEQ", getDBTransaction());     
            return s.getSequenceNumber().intValue();
    }
    
    public Integer getNextIdRefcardioDati() {
            SequenceImpl s = new SequenceImpl("SO_REFCARDIO_DATI_SEQ", getDBTransaction());     
            return s.getSequenceNumber().intValue();
    }
        
    public Integer getNextIdImpExpLog()
    {
    SequenceImpl s = new SequenceImpl("IMPEXP_LOG_SEQ", getDBTransaction());         
    return s.getSequenceNumber().intValue();
    }
  
  
    /**
   * Chiama la procedura PL/SQL per la memorizzazione degli appuntamneti/accettazioni 
   * da cancellare nel database di frontiera
   * @return il primo elemento è l'eventuale messaggio d'errore,
   * il secondo è il BigDecimal con il numero di inviti da esportare
   * @param al
   * @param dal
   * @param centro
   * @param tpscr
   * @param ulss
   */
   public HashMap callPrepareCancToDB(String ulss,
                                        String tpscr,
                                        BigDecimal centro,
                                        String dal,
                                        String al,
                                        BigDecimal centro_prel,
                                        BigDecimal livello) 

  {
    CallableStatement st = null;
    HashMap out=new HashMap();
   
    try 
    {

      String stmt = "BEGIN  EXPORT_COLON.prepara_cancellazioni(?,?,?,?,?,?,?,?,?,?); END;";

      DBTransaction tr = this.getDBTransaction();
      st = tr.createCallableStatement(stmt,1);

      if(ulss==null)
        throw new Exception("Nessuna azienda sanitaria indicata");
      st.setString("p_data_da",dal);
      st.setString("p_data_a",al);
      st.setBigDecimal("p_id_centro_ref",centro);
      st.setBigDecimal("p_id_centro_prel",centro_prel);
      st.setBigDecimal("p_livello",livello);
      st.setString("p_ulss",ulss);
      st.setString("p_tpscr",tpscr);
      
     
      st.registerOutParameter("message", java.sql.Types.VARCHAR); 
      st.registerOutParameter("rval", java.sql.Types.NUMERIC); 
      st.registerOutParameter("nrighe", java.sql.Types.NUMERIC); 
      st.executeUpdate();   


// se si vuole mandare un messaggio

      int num = (st.getBigDecimal("rval")).intValue();    
      //se rval=-1 c'è un errore
      if(num < 0){
        out.put("error",st.getString("message"));
      }
      else{
        out.put("result",st.getBigDecimal("nrighe")); 
      }
        
      } 
      catch(Exception e) 
      {
        e.printStackTrace();
        out.put("error",e.getMessage());
      }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }
      
      
      
    }
    return out;
    
  }
  
  /**
   * Invoca la procedura PL/SQL che restituisce il corretto centro di richiamo
   * @return 
   * @param livellorichiamo livello del tipo di invito con cui il soggetto 
   * deve essere richiamato
   * @param idinvito identificativo dell'invito attivo
   * @param tpscr tipo di screening
   * @param ulss azienda sanitaria
   * @param tessera tessera sanitaria del soggetto
   */
  public HashMap callGetCentroRichiamo(String tessera,
                                        String ulss,
                                        String tpscr,
                                        BigDecimal idinvito,
                                        BigDecimal livellorichiamo) 

  {/*GET_CENTRO_RICHIAMO
   (tessera IN varchar,
   azienda in varchar,
   scr in varchar,
   invitoattivo in Integer,--id dell'eventuale invito prcednete al richiamo da calcolare
   livrichiamo in Integer,
   msg in out varchar,
   centro IN OUT Integer)*/
   
    CallableStatement st = null;
    HashMap out=new HashMap();
   
    try 
    {

      String stmt = "BEGIN GET_CENTRO_RICHIAMO(?,?,?,?,?,?,?); END;";

      DBTransaction tr = this.getDBTransaction();
      st = tr.createCallableStatement(stmt,1);

      st.setString("tessera",tessera);
      st.setString("azienda",ulss);
      st.setString("scr",tpscr);
      st.setBigDecimal("invitoattivo",idinvito);
      st.setBigDecimal("livrichiamo",livellorichiamo);
      
      st.registerOutParameter("msg", java.sql.Types.VARCHAR); 
      st.registerOutParameter("centro", java.sql.Types.NUMERIC);  
      st.executeUpdate();   


// se si vuole mandare un messaggio

      String s=  st.getString("msg");
      //se rval=-1 c'è un errore
      if(s!=null && s.length()>0){
        out.put("error",st.getString("msg"));
      }
      else{
        out.put("centro",st.getBigDecimal("centro")); 
      }
        
      } 
      catch(Exception e) 
      {
        e.printStackTrace();
        out.put("error",e.getMessage());
      }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }
      
      
      
    }
    return out;
    
  }
  
  /**
   * Invoca la procedura PL/SQL che restituisce il corretto documento
   * @return codice documento
   * @param chiave del soggetto
   * @param ulss azienda sanitaria

   */
  public String callFunCodtsTrovaDoc(String codts,
                                        String ulss,
                                        String tipoDoc) 
  {
    CallableStatement st = null;
    String out=null;
   
    try 
    {

      String stmt = "{ ? = call FUN_CODTS_TROVA_DOC(?,?,?)}";

      DBTransaction tr = this.getDBTransaction();
      st = tr.createCallableStatement(stmt,1);

      st.registerOutParameter(1, java.sql.Types.VARCHAR);
      st.setString(2,codts);
      st.setString(3,ulss);
      st.setString(4,tipoDoc);
    
      st.executeQuery();

      out = st.getString(1);
        
      } 
      catch(Exception e) 
      {
        e.printStackTrace();
        out = e.getMessage();
      }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }
      
      
      
    }
    return out;
    
  }
  
  public void preapareJournaling(String user, String ulss, String tpscr)
  {
    CallableStatement st = null;
    try //UTENTE
    {
      String stmtUtente = "BEGIN  a00_journal.SET_UTENTE(?); END;";
      DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmtUtente,1);
        st.setString("P_UTEN", user);
        st.executeUpdate();  
    }
    catch (SQLException e1)
    {
      
    }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }    
    }
    
    try //FLAG_ABIL  
    {
      String stmtFlag = "BEGIN  a00_journal.SET_FLAG_ABIL2(?); END;";
      DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmtFlag,1);
        st.setString("P_ABIL", "Y");
        st.executeUpdate();  
    }
    catch (SQLException e2)
    {
      
    }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }    
    }
    
    try //APPLICAZIONE
    {
      String stmtApplicazione = "BEGIN  a00_journal.SET_APPLICAZIONE(?); END;";
      DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmtApplicazione,1);
        st.setString("P_APPL", "SCREENING");
        st.executeUpdate();  
    }
    catch (SQLException e3)
    {
      
    }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }    
    }
    
    try //AMBIENTE
    {
      String stmtAmbiente = "BEGIN  a00_journal.SET_AMBIENTE(?); END;";
      DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmtAmbiente,1);
        st.setString("P_AMBI", "SCREENING");
        st.executeUpdate();  
    }
    catch (SQLException e4)
    {
      
    }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }    
    }
    
    try //ENTE
    {
      String stmtEnte = "BEGIN  a00_journal.SET_ENTE(?); END;";
      DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmtEnte,1);
        st.setString("P_ENTE", ulss);
        st.executeUpdate();  
    }
    catch (SQLException e5)
    {
      
    }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }    
    }
    
    try //RIFERIMENTI
    {
      String stmtRiferimenti = "BEGIN  a00_journal.SET_RIFERIMENTI(?); END;";
      DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmtRiferimenti,1);
        st.setString("P_RIFE", tpscr);
        st.executeUpdate();  
    }
    catch (SQLException e7)
    {
      
    }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }    
    }
    
    try //TRANSAZIONE
    {
      SequenceImpl s = new SequenceImpl("A_JOURNAL_TR", getDBTransaction());     
      Integer numTransazione= s.getSequenceNumber().intValue();
      String stmtTransazione = "BEGIN  a00_journal.SET_TRANSAZIONE(?); END;";
      DBTransaction tr = this.getDBTransaction();
      st = tr.createCallableStatement(stmtTransazione,1);
      st.setInt("P_TRAN", numTransazione.intValue());
      st.executeUpdate();  
    }
    catch (SQLException e6)
    {
      
    }
    finally 
    {
      try 
      {
        if (st != null) st.close();
      }
      catch (SQLException s) { }    
    }
    
    
  }

}