package model.stats;
import java.math.BigDecimal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

public class Indicatore 
{
  protected String errore;
  protected int count;
//  protected String[] list;
  protected String query;
  
  protected HashMap inParamTypes;
  protected HashMap inParamValues;
  
  protected Connection conn;
  protected CallableStatement stmt;
  protected String statement;
  
  public Indicatore(Connection _conn, String _statement)
  {
    this.conn=_conn;
    this.statement=_statement;
  }
  
  public void initializeStandard() throws Exception
  {
      this.addParamType("this_ulss","java.lang.String");
      this.addParamType("ulss_reg","java.lang.String");
      this.addParamType("this_tpscr","java.lang.String");
      this.addParamType("dal","java.lang.String");
      this.addParamType("al_escluso","java.lang.String");
      this.addParamType("round_ind_da","java.math.BigDecimal");
      this.addParamType("round_ind_a","java.math.BigDecimal");
      this.addParamType("round_org","java.math.BigDecimal");
      this.addParamType("centro_prel","java.math.BigDecimal");
      this.addParamType("cod_zona","java.math.BigDecimal");
      this.addParamType("eta_da","java.math.BigDecimal");
      this.addParamType("eta_a","java.math.BigDecimal");
      this.addParamType("comune","java.lang.String");
      this.addParamType("is_volontaria","java.math.BigDecimal");
  }
  
  /**
   * Metodo che imposta un parametro nello statement SQl col setter corretto
   * @throws java.lang.Exception se non sono stati definiti il tipo o il valore del parametro
   * @param name nome del parametro
   */
  private void bindParam(String name) throws Exception
  {
    
    Class type=(Class)inParamTypes.get(name);
    if(type==null)
      throw new Exception("Tipo del parametro di input "+name+" non definito");
    
    Object value=inParamValues.get(name);
    if(type.equals(Class.forName("java.math.BigDecimal")))
      stmt.setBigDecimal(name,(BigDecimal)value);
    else if(type.equals(Class.forName("java.lang.String")))
      stmt.setString(name,(String)value);
    else if(type.equals(Class.forName("java.sql.Date")))
      stmt.setDate(name,(Date)value);
    else if(type.equals(Class.forName("java.lang.Integer")))
      stmt.setInt(name,((Integer)value)==null?-1:((Integer)value).intValue());  
    else
      throw new Exception("Tipo "+type.getName()+" non previsto");
  }
  
  /**
   * Metodo che imposta tutti i parametri dello statement SQl col setter corretto
   * @throws java.lang.Exception
   */
  private void bindParams() throws Exception
  {
    if(stmt==null)
      this.prepareStatement();
     if(inParamTypes==null)
      throw new Exception("Tipi dei parametri di input non definiti");
    if(inParamValues==null)
      throw new Exception("Valori dei parametri di input non definiti");
      
    Object[] keys=inParamValues.keySet().toArray();
    for(int i=0;i<keys.length;i++)
    {
      try{
  
        this.bindParam((String)keys[i]);
      }catch(Exception ex)
      {
        ex.printStackTrace();
        throw ex;
      }
    }
  }
  
  /**
   * Crea lo statement a partire da stringa e connessione
   * @throws java.lang.Exception
   */
  private void prepareStatement() throws Exception
  {
    if(this.conn==null)
       throw new Exception("Connessione non valida");
    if(statement==null)
       throw new Exception("Statement SQL non valido");
    this.stmt=(CallableStatement)conn.prepareCall(statement);
  }
  
  /**
   * Aggiunge la definizione di un tipo di un parametro
   * @throws java.lang.Exception
   * @param classname
   * @param name
   */
  public void addParamType(String name, String classname) throws Exception
  {
    if(inParamTypes==null)
      inParamTypes=new HashMap();
    try{
      Class c=Class.forName(classname);
      inParamTypes.put(name,c);
    }
    catch(ClassNotFoundException ex)
    {
      throw new Exception("Impossibile definire il tipo del parametro "+name+": classe "+classname+" non trovata");
    }
  }
  
  /**
   * Aggiunge il valore di un parametro
   * @throws java.lang.Exception
   * @param value
   * @param name
   */
  public void addParamValue(String name,Object value) throws Exception
  {
     if(inParamTypes==null)
      throw new Exception("Tipi dei parametri di input non definiti");
    Class type=(Class)inParamTypes.get(name);
    if(type==null)
      throw new Exception("Impossibile assegnare un valore: tipo del parametro di input "+name+" non definito");
      
    if(this.inParamValues==null)
      this.inParamValues=new HashMap();
      
    if(value==null)
      this.inParamValues.put(name,null);
    else
    {
      if(type.equals(value.getClass()))
        this.inParamValues.put(name,value);
      else
       throw new Exception("Impossibile assegnare un valore al parametro di input "+name+":\n"+
       " il parametro è di tipo "+value.getClass().getName()+" mentre dovrebbe essere di tipo "+type.getName());
    }
  }
  
  /**
   * Esegue lo statement sql
   */
  public void execute()
  {
  //reset dei dati
     this.errore=null;
     this.count=-1;
//     this.list=null;
    try
    {
     this.bindParams();
     
  /*    stmt.registerOutParameter("codts_list",
                               OracleTypes.ARRAY,
                               "STRINGARRAY");*/
      stmt.registerOutParameter("counter",Types.NUMERIC);
      stmt.registerOutParameter("msg",Types.VARCHAR);
      stmt.registerOutParameter("query",Types.VARCHAR);
      stmt.executeUpdate();
 
       this.count=stmt.getBigDecimal("counter")==null?-1:stmt.getBigDecimal("counter").intValue();
       this.errore=stmt.getString("msg");
       this.query=stmt.getString("query");
     //  Array simpleArray = (Array)stmt.getArray("codts_list");
     //  this.list=(String[])simpleArray.getArray();
     //  this.count=list.length;
    }
    catch(Exception ex)
    {
    
      this.setErrore(ex.getMessage());
    }
     finally 
    {
      try 
      {
        if (stmt != null) stmt.close();
      }
      catch (SQLException s) { }
      
    }
  }

  public int getCount()
  {
    return count;
  }

  public String getErrore()
  {
    return errore;
  }

/*  public String[] getList()
  {
    return list;
  }*/

  public void setErrore(String errore)
  {
   if( this.errore==null)
     this.errore= errore;
   else
    this.errore+=" - "+errore;
  }

  public String getQuery()
  {
    return query;
  }


}