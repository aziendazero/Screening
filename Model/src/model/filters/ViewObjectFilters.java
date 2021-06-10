package model.filters;
import insiel.dataHandling.DateUtils;

import model.commons.AccessManager;

import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

public final class ViewObjectFilters 
{
  public ViewObjectFilters()
  {
  }
  
  /**
   * Filtra i centri del view object di input in base a ulss, tpscr e 
   * data di chisura (se not null)
   * @param data
   * @param tpscr
   * @param ulss
   * @param vo
   */
  public static void filterCentri(ViewObject vo, 
                                  String ulss, 
                                  String tpscr,
                                  String data)
  {
    ViewObjectFilters.filterCentri(vo, ulss, tpscr, data, null);
    
  }
  
   public static void filterCentri(ViewObject vo, 
                                  String ulss, 
                                  String tpscr,
                                  String data,
                                  String elenco_centri)
  {
      ViewObjectFilters.filterCentri(vo, ulss, tpscr, data, elenco_centri, null);
    
  }
   
    public static void filterCentri(ViewObject vo, 
                                      String ulss, 
                                      String tpscr,
                                      String data,
                                      String elenco_centri,
                                      String livello)
      {
        String where="TPSCR='"+tpscr+"' AND ULSS='"+ulss+"' ";
        if(data!=null)
          where+=" AND (DTCHIUSURACENTRO IS NULL OR  DTCHIUSURACENTRO>TO_DATE('"+data+"','"+DateUtils.DATE_PATTERN+"'))";
        if(elenco_centri != null)
          where+=" AND IDCENTRO IN "+elenco_centri;
        if (livello != null)
          where+=" AND TIPOCENTRO = "+livello+" ";
        vo.setWhereClause(where);
        vo.executeQuery();
        
      }
  
 /**
   *Filtra i medici del view object di input in base a ulss e data di 
   * fine validit� (se not null)
   * @param data
   * @param ulss
   * @param vo
   */
  public static void filterMedici(ViewObject vo, 
                                  String ulss, 
                                  String data)
  {
    String where="ULSS='"+ulss+"' ";
    if(data!=null)
      where+=" AND (DTFINEVALOP IS NULL OR  DTFINEVALOP>TO_DATE('"+data+"','"+DateUtils.DATE_PATTERN+"'))";
    vo.setWhereClause(where);
    vo.executeQuery();
    
  }
  
  /**
   * Filtra i template del view object di input in base a ulss (pi� quella
   * regionale), tpscr e data di fina validit� (se not null)
   * @param data
   * @param tpscr
   * @param ulss
   * @param vo
   */
  public static void filterTemplates(ViewObject vo, 
                                  String ulss, 
                                  String tpscr,
                                  String data)
  {
    filterTemplates(vo,ulss,tpscr,data,null);
    
  }
  /**
   * Filtra i template del view object di input in base a ulss (pi� quella
   * regionale), tpscr e data di fina validit� (se not null). Aggiunge alla fine 
   * AND more, dove more sar� una stringa del tipo FIELD1=VALUE1
   * @param more
   * @param data
   * @param tpscr
   * @param ulss
   * @param vo
   */
  public static void filterTemplates(ViewObject vo, 
                                  String ulss, 
                                  String tpscr,
                                  String data,
                                  String more)
  {
   String where="TPSCR='"+tpscr+"'";
   //se � un utente regionale pu� vederli tutti
    if(!AccessManager.CODREGIONALE.equals(ulss))
      where+="  AND (ULSS='"+ulss+"' OR ULSS='"+AccessManager.CODREGIONALE+"') ";
    if(data!=null)
      where+="AND (DTFINEVALTEMPL IS NULL OR  DTFINEVALTEMPL>TO_DATE('"+data+"','"+DateUtils.DATE_PATTERN+"'))";
   if(more!=null && more.length()>0)
     where+=" AND " + more;
    
    vo.setWhereClause(where);
    vo.executeQuery();
    
  }
  
  /**
   * Filtro gli operatori medici in base al centro (se presente) e 
   * alla data di validit� (se presente)
   * @param centro
   * @param data
   * @param vo
   */
  public static void filterOpMedici(ViewObject vo, 
                                  String data,
                                  Integer centro,
                                  Integer tpop,
                                  String ulss, String tpscr)
  {
    String where="";
    if(ulss!=null)
      where+="ULSS='"+ulss+"' AND ";
    if(tpscr!=null)
      where+="TPSCR='"+tpscr+"' AND ";
    if(data!=null)
      where+="(DTFINEVALOPMEDICO IS NULL OR DTFINEVALOPMEDICO>TO_DATE('"+data+"','"+DateUtils.DATE_PATTERN+"')) AND ";
    if(centro!=null && centro.intValue()>=0)
      where+="IDCENTRO="+centro+" AND ";
    if(tpop!=null && tpop.intValue()>=0)
      where+="CODOP="+tpop;
    where=where.trim();
    if(where.endsWith("AND"))
      where=where.substring(0,where.lastIndexOf("AND"));
    vo.setWhereClause(where);
    vo.executeQuery();
    
  }
  
  /**
   * Filtro gli operatori medici in base al centro (se presente) e 
   * alla data di validit� (se presente)
   * @param centro
   * @param data
   * @param vo
   */
  public static void filterOpMedici(ViewObject vo, 
                                  String data,
                                  Integer centro,
                                  Integer[] tpop,
                                  String ulss, String tpscr)
  {
    String where="";
    if(ulss!=null)
      where+="ULSS='"+ulss+"' AND ";
    if(tpscr!=null)
      where+="TPSCR='"+tpscr+"' AND ";
    if(data!=null)
      where+="(DTFINEVALOPMEDICO IS NULL OR DTFINEVALOPMEDICO>TO_DATE('"+data+"','"+DateUtils.DATE_PATTERN+"')) AND ";
    if(centro!=null && centro.intValue()>=0)
      where+="IDCENTRO="+centro+" AND ";
    if(tpop!=null && tpop.length>0)
    {
      where+="( CODOP="+tpop[0];
      for(int i=1;i<tpop.length;i++)
        where+=" OR CODOP="+tpop[i];
      
      where+=")";
    }
      
    where=where.trim();
    if(where.endsWith("AND"))
      where=where.substring(0,where.lastIndexOf("AND"));
    vo.setWhereClause(where);
    vo.executeQuery();
    
  }
  
  /**
   * Filtro dei dati di configurazione in base a ulss, tpscr e 
   * alla data di validit� (se presente)
   * @param data
   * @param tpscr
   * @param ulss
   * @param vo
   */
  public static void filterCnfReferti(ViewObject vo, 
                                  String ulss, 
                                  String tpscr,
                                  String data)
  {
    String where="TPSCR='"+tpscr+"' AND ULSS='"+ulss+"' ";
    if(data!=null)

      where+=" AND (DTFINEVALIDITA IS NULL OR  DTFINEVALIDITA>TO_DATE('"+data+"','"+DateUtils.DATE_PATTERN+"'))";
    vo.setWhereClause(where);
    vo.executeQuery();
    
  }
  
    /**
     * Filtro dei dati di configurazione in base a ulss, tpscr e
     * alla data di validit� (se presente)
     * @param data
     * @param tpscr
     * @param ulss
     * @param vo
     */
    public static void filterCnfReferti(ViewObject vo, String ulss, String tpscr, Date data) {
        String where = "TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "'";
        if (data != null) {
            where +=
                " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA > TO_DATE('" + data.dateValue() + "', 'YYYY-MM-DD'))";
        } else {
            where += " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA > SYSDATE)";
        }
        vo.setWhereClause(where);
        vo.executeQuery();
    }
}
