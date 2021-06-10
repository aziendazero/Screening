package model.commons;

import insiel.dataHandling.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Map;

import model.AccCa_AppModule;
import model.common.Acc_AppModule;

import model.common.AccCo_AppModule;
import model.common.AccMa_AppModule;
import model.common.AccPf_AppModule;
import model.common.Parent_AppModule;
import model.common.Sogg_AppModule;

import model.datacontrol.AccCa_RicParam;
import model.datacontrol.AccCo_RicParam;
import model.datacontrol.AccMa_RicParam;
import model.datacontrol.AccPf_RicParam;
import model.datacontrol.Acc_RicParam;
import model.datacontrol.Ref_SearchBean;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;

@SuppressWarnings("deprecation")
public class SoggUtils {
    public static Integer[] getEtaScreening(ApplicationModule am) {
        Integer[] ret = new Integer[2];

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");

        String qry =
            "SELECT CODAZ, ETACITOMIN, ETACITOMAX, ETAMAMMOMIN, " +
            "ETAMAMMOMAX, ETACOLONMIN, ETACOLONMAX, ETACARDIOMIN, ETACARDIOMAX, " + "ETAPFASMIN, ETAPFASMAX " +
            "FROM SO_AZIENDA WHERE CODAZ = '" + ulss + "'";
        ViewObject voAz = am.createViewObjectFromQueryStmt("", qry);
        voAz.setRangeSize(-1);
        voAz.executeQuery();

        Row fAz = voAz.first();
        if (fAz != null) {
            if (tpscr.equals("CI")) {
                ret[0] =
                    fAz.getAttribute(1) != null ? ((oracle.jbo.domain.Number) fAz.getAttribute(1)).intValue() : null;
                ret[1] =
                    fAz.getAttribute(2) != null ? ((oracle.jbo.domain.Number) fAz.getAttribute(2)).intValue() : null;
            } else if (tpscr.equals("MA")) {
                ret[0] =
                    fAz.getAttribute(3) != null ? ((oracle.jbo.domain.Number) fAz.getAttribute(3)).intValue() : null;
                ret[1] =
                    fAz.getAttribute(4) != null ? ((oracle.jbo.domain.Number) fAz.getAttribute(4)).intValue() : null;
            } else if (tpscr.equals("CO")) {
                ret[0] =
                    fAz.getAttribute(5) != null ? ((oracle.jbo.domain.Number) fAz.getAttribute(5)).intValue() : null;
                ret[1] =
                    fAz.getAttribute(6) != null ? ((oracle.jbo.domain.Number) fAz.getAttribute(6)).intValue() : null;
            } else if (tpscr.equals("CA")) {
                Integer eta_min = calcolaEta((Date) fAz.getAttribute(8));
                Integer eta_max = calcolaEta((Date) fAz.getAttribute(7));
                ret[0] = eta_min;
                ret[1] = eta_max;
            } else if (tpscr.equals("PF")) {
                Integer eta_min = calcolaEta((Date) fAz.getAttribute(10));
                Integer eta_max = calcolaEta((Date) fAz.getAttribute(9));
                ret[0] = eta_min;
                ret[1] = eta_max;
            }
        }

        return ret;
    }

    public static int confrontaEsclusioni(String esclPresTipo, Integer esclPresPred, String esclPropTipo,
                                          Integer esclPropPred, Date fineesclPres, Date fineesclProp) {
        // restituisce:
        // 1 se esclPresente è più importante di esclProposta
        // 0 se hanno la stessa importanza
        // -1 se esclProposta è più importante di esclPresente
        int ePresPred = (esclPresPred == null ? 0 : esclPresPred);
        int ePropPred = (esclPropPred == null ? 0 : esclPropPred);

        if (esclPresTipo.equals("D")) {

            if (esclPropTipo.equals("Temporanea"))
                return 1;


            if (ePresPred == 1) {
                if (ePropPred == 1)
                    return 0;
                else
                    return 1;
            } else {
                if (ePropPred == 1)
                    return -1;
                else
                    return 0;

            }

        } else {
            //06062011 gaion: aggiunto confronto date fine esclusione
            if (esclPropTipo.equals("Temporanea") && (fineesclPres.compareTo(fineesclProp) >= 0))
                return 1;
            else
                return -1;


        }

    }
    
    public static void setIdTpRichiamoEscl(Sogg_AppModule am)
      {
        ViewObject voEscl = am.findViewObject("Sogg_SoEsclusioneView1");
        Row cEscl = voEscl.getCurrentRow();
        String tEscl = (String) cEscl.getAttribute("Tpescl");
        if (tEscl.equals("T"))
        {
          ViewObject voMot = am.findViewObject("Sogg_MotEsclView1");
          try{
            Row[] result=voMot.getFilteredRows("Idcnfescl",cEscl.getAttribute("Idcnfescl"));
            if(result.length==0)
               return;
            
            cEscl.setAttribute("Tprich",result[0].getAttribute("Idtpinvito"));
          
          }
          catch(Exception ex){}
        }
      }
    
    public static void setupInterfacciaPropagazione(boolean inserimento, Sogg_AppModule am)
      {
      
        if (!inserimento) return;
      
        ViewObject voEscl = am.findViewObject("Sogg_SoEsclusioneView1");
        Row cEscl = voEscl.getCurrentRow();
        Integer motEscl = (Integer) cEscl.getAttribute("Idcnfescl");
        String ulss = (String) cEscl.getAttribute("Ulss");
        String tpscr = (String) cEscl.getAttribute("Tpscr");
        
        int propagare = 0;
        Row result = null;
        
        if (motEscl != null){
        
          String qryPropaga = "SELECT a.propagabile, a.codregionale FROM so_cnf_esclusione a where " +
            "a.idcnfescl = " + motEscl.intValue() + " and a.ulss = '" + ulss + "' and a.tpscr = '" +
            tpscr + "'";
      
          ViewObject voPropaga = am.createViewObjectFromQueryStmt("",qryPropaga);
          voPropaga.setRangeSize(-1);
          voPropaga.executeQuery();
          
          result = voPropaga.first();
          
          if (result != null) {
                Number propag = (Number) result.getAttribute(0);
                propagare = (propag == null ? 0 : propag.intValue());
            }
       
        }
        
        if (propagare == 1 && tpscr.equals("CO"))
          {    
            // se sono in screening del colon e il soggetto è maschio devo comunque non propagare
            String codTS = (String) cEscl.getAttribute("Codts") ;
            String sesso = getSessoSoggetto(am, codTS, ulss);
            
            if (sesso.equals("M")) propagare = 0;      
            //
          }
          
        ViewObject voEsclPropCI = am.findViewObject("Cnf_EsclProposteViewCI");
        ViewObject voEsclPropMA = am.findViewObject("Cnf_EsclProposteViewMA");
        ViewObject voEsclPropCO = am.findViewObject("Cnf_EsclProposteViewCO");
        
        Map session = ADFContext.getCurrent().getSessionScope();    

        if (propagare == 1)
        {

          int codreg = ((Number) result.getAttribute(1)).intValue();

          if (tpscr.equals("CI"))
          {
            session.put("propagaCI",Boolean.FALSE);
            voEsclPropCI.setWhereClause("1=2");
            voEsclPropCI.executeQuery();
            
            session.put("propagaMA",Boolean.TRUE);
            String whMA = "codregionale = " + codreg + " and ulss = '" + ulss + "' and tpscr = 'MA'";        
            voEsclPropMA.setWhereClause(whMA);
            voEsclPropMA.executeQuery();
          
            session.put("propagaCO",Boolean.TRUE);
            String whCO = "codregionale = " + codreg + " and ulss = '" + ulss + "' and tpscr = 'CO'";        
            voEsclPropCO.setWhereClause(whCO);
            voEsclPropCO.executeQuery();
          }

          if (tpscr.equals("MA"))
          {
            session.put("propagaCI",Boolean.TRUE);
            String whCI = "codregionale = " + codreg + " and ulss = '" + ulss + "' and tpscr = 'CI'";        
            voEsclPropCI.setWhereClause(whCI);
            voEsclPropCI.executeQuery();
            
            session.put("propagaMA",Boolean.FALSE);
            voEsclPropMA.setWhereClause("1=2");
            voEsclPropMA.executeQuery();
          
            session.put("propagaCO",Boolean.TRUE);
            String whCO = "codregionale = " + codreg + " and ulss = '" + ulss + "' and tpscr = 'CO'";        
            voEsclPropCO.setWhereClause(whCO);
            voEsclPropCO.executeQuery();
          }
          
          if (tpscr.equals("CO"))
          {
            session.put("propagaCI",Boolean.TRUE);
            String whCI = "codregionale = " + codreg + " and ulss = '" + ulss + "' and tpscr = 'CI'";        
            voEsclPropCI.setWhereClause(whCI);
            voEsclPropCI.executeQuery();
            
            session.put("propagaMA",Boolean.TRUE);
            String whMA = "codregionale = " + codreg + " and ulss = '" + ulss + "' and tpscr = 'MA'";        
            voEsclPropMA.setWhereClause(whMA);
            voEsclPropMA.executeQuery();
          
            session.put("propagaCO",Boolean.FALSE);
            voEsclPropCO.setWhereClause("1=2");
            voEsclPropCO.executeQuery();
          }

        }
        else // propagare = 0
        {
            session.put("propagaCI",Boolean.FALSE);
            voEsclPropCI.setWhereClause("1=2");
            voEsclPropCI.executeQuery();

            session.put("propagaMA",Boolean.FALSE);
            voEsclPropMA.setWhereClause("1=2");
            voEsclPropMA.executeQuery();
          
            session.put("propagaCO",Boolean.FALSE);
            voEsclPropCO.setWhereClause("1=2");
            voEsclPropCO.executeQuery();
        }
        
        
      }


    public static boolean stringsEqual(String str1, String str2) {
        if (str1 == null && str2 != null)
            return false;
        if (str1 != null && str2 == null)
            return false;
        if (str1 == null && str2 == null)
            return true;

        return (str1.equals(str2));
    }

    public static boolean IntegersEqual(Integer num1, Integer num2) {
        if (num1 == null && num2 != null)
            return false;
        if (num1 != null && num2 == null)
            return false;
        if (num1 == null && num2 == null)
            return true;

        return (num1.intValue() == num2.intValue());
    }

    public static boolean datesEqual(Date date1, Date date2) {
        if (date1 == null && date2 != null)
            return false;
        if (date1 != null && date2 == null)
            return false;
        if (date1 == null && date2 == null)
            return true;

        java.util.Date jDate1 = date1.dateValue();
        java.util.Date jDate2 = date2.dateValue();

        return (jDate1.compareTo(jDate2) == 0);
    }

    public static boolean appModificato(ApplicationModule am, Integer idInvito, Integer idAppNew) throws Exception {
        String qryInvito = "SELECT a.idapp " + "FROM so_invito a where a.idinvito = " + idInvito.toString();
        ViewObject voInvito = am.createViewObjectFromQueryStmt("", qryInvito);
        voInvito.setRangeSize(-1);
        voInvito.executeQuery();

        Row invito = voInvito.first();

        Integer idAppOld =
            invito.getAttribute(0) != null ? ((oracle.jbo.domain.Number) invito.getAttribute(0)).intValue() : null;

        if (!IntegersEqual(idAppNew, idAppOld))
            return true;

        return false;

    }


    //20110404 Serra


    public static boolean datiRichiamoModificati(ApplicationModule am, Integer idInvito, String codEsInvNew,
                                                 Integer idCtPrelNew, Date dtAppNew,
                                                 //20110404 Serra
                                                 Date dtEsameRecNew)

        throws Exception {

        String qryInvito =
            "SELECT a.codesitoinvito, a.idcentroprelievo, a.dtapp, a.dtesamerecente " +
            "FROM so_invito a where a.idinvito = " + idInvito.toString();
        ViewObject voInvito = am.createViewObjectFromQueryStmt("", qryInvito);
        voInvito.setRangeSize(-1);
        voInvito.executeQuery();

        Row invito = voInvito.first();

        String codEsInvOld = (String) invito.getAttribute(0);
        Integer idCtPrelOld =
            invito.getAttribute(1) != null ? ((oracle.jbo.domain.Number) invito.getAttribute(1)).intValue() : null;
        Date dtAppOld = (Date) invito.getAttribute(2);
        Date dtesameRecOld = (Date) invito.getAttribute(3);

        if (!stringsEqual(codEsInvNew, codEsInvOld))
            return true;
        if (!IntegersEqual(idCtPrelNew, idCtPrelOld))
            return true;
        if (!datesEqual(dtAppNew, dtAppOld))
            return true;
        //20110404 Serra
        if (!datesEqual(dtEsameRecNew, dtesameRecOld))
            return true;

        return false;

    }
    //fine 20110404


    public static boolean consensiPositivi(ApplicationModule am, String codts, String tpscr, String ulss) {
        String qry =
            "SELECT a.valore, b.valore FROM SO_STORICO_CONSENSO a, SO_STORICO_CONSENSO b " +
            " WHERE a.TPSCR = b.TPSCR " + " AND a.ULSS = b.ULSS " + " AND a.CODTS = b.CODTS " +
            " and a.FINE_VAL is null " + " and a.tipo = 'GEN' " + " and b.TIPO = 'COND' " + " and b.FINE_VAL is null " +
            " AND a.TPSCR = '" + tpscr + "' AND a.ulss = '" + ulss + "' " + "  and a.CODTS = '" + codts + "' ";
        ViewObject vo = am.createViewObjectFromQueryStmt("", qry);
        vo.setRangeSize(-1);
        vo.executeQuery();

        Row sRow = vo.first();
        if (sRow != null) {
            Integer consensoGen =
                sRow.getAttribute(0) != null ? ((oracle.jbo.domain.Number) sRow.getAttribute(0)).intValue() : null;
            Integer consensoCond =
                sRow.getAttribute(1) != null ? ((oracle.jbo.domain.Number) sRow.getAttribute(1)).intValue() : null;
            if (consensoGen.intValue() == 2 && consensoCond.intValue() == 2) {
                return true;
            }
        }
        return false;

    }

    public static Integer getCtRef(ApplicationModule am, Integer ctPrel) throws Exception {
        if (ctPrel == null) {
            return null;
        } else {
            String qryCt = "select IDCENTROREF from SO_CENTRO_PREL_REF " + "where IDCENTRO = " + ctPrel.toString();
            ViewObject voCtRef = am.createViewObjectFromQueryStmt("", qryCt);
            voCtRef.setRangeSize(-1);
            voCtRef.executeQuery();

            Row sRow = voCtRef.first();
            Integer idCtRef =
                sRow.getAttribute(0) != null ? ((oracle.jbo.domain.Number) sRow.getAttribute(0)).intValue() : null;
            return idCtRef;

        }
    }

    public static Integer getRoundIndiv(Sogg_AppModule am, String codTs, String ulss, String tpscr) {
        String qry =
            "select ROUNDINDIV from SO_SOGG_SCR where CODTS = '" + codTs + "' and ULSS = '" + ulss + "' and TPSCR = '" +
            tpscr + "'";
        ViewObject vo = am.createViewObjectFromQueryStmt("", qry);
        vo.setRangeSize(-1);
        vo.executeQuery();

        Row fRec = vo.first();
        if (fRec != null) {
            Integer round =
                fRec.getAttribute(0) != null ? ((oracle.jbo.domain.Number) fRec.getAttribute(0)).intValue() : null;
            return round;
        } else
            return null;

    }

    public static Integer getRoundComuneSogg(Sogg_AppModule am, String codTs, String ulss, String tpscr) {
        String qry =
            "SELECT CODTS,CODANAGREG,RELEASE_CODE_COM_RES,RELEASE_CODE_COM_DOM FROM SO_SOGGETTO" + " where CODTS = '" +
            codTs + "' AND ULSS='" + ulss + "'";
        ViewObject voSogg = am.createViewObjectFromQueryStmt("", qry);
        voSogg.setRangeSize(-1);
        voSogg.executeQuery();
        Row fSogg = voSogg.first();
        Integer codAna =
            fSogg.getAttribute(1) != null ? ((oracle.jbo.domain.Number) fSogg.getAttribute(1)).intValue() : null;
        Integer codCom = null;
        if (codAna.equals(ConfigurationConstants.CODICE_DOMICILIATO)) {
            codCom =
                fSogg.getAttribute(3) != null ? ((oracle.jbo.domain.Number) fSogg.getAttribute(3)).intValue() : null;
        } else {
            codCom =
                fSogg.getAttribute(2) != null ? ((oracle.jbo.domain.Number) fSogg.getAttribute(2)).intValue() : null;
        }

        String qRound =
            "SELECT NUMROUND FROM SO_ROUNDORG WHERE RELEASE_CODE_COM = " + codCom + " AND CODSCR = '" + tpscr +
            "' AND DTFINE IS NULL";
        ViewObject voRound = am.createViewObjectFromQueryStmt("", qRound);
        voRound.setRangeSize(-1);
        voRound.executeQuery();

        Row fRound = voRound.first();
        Integer round = null;
        if (fRound != null) {
            round =
                fRound.getAttribute(0) != null ? ((oracle.jbo.domain.Number) fRound.getAttribute(0)).intValue() : null;
        }

        return round;
    }

    public static Integer getInvitoAttivo(Sogg_AppModule am, String codTs, String ulss, String tpscr) {
        String qry =
            "select IDINVITO from SO_INVITO where CODTS = '" + codTs + "' and attivo = 1" + " and ulss = '" + ulss +
            "' and tpscr = '" + tpscr + "'";
        ViewObject voInviti = am.createViewObjectFromQueryStmt("", qry);
        voInviti.setRangeSize(-1);
        voInviti.executeQuery();

        Integer invAtt = null;
        Row fInv = voInviti.first();
        if (fInv != null)
            invAtt = fInv.getAttribute(0) != null ? ((oracle.jbo.domain.Number) fInv.getAttribute(0)).intValue() : null;

        return invAtt;

    }

    public static int getLivelloCt(ApplicationModule am, Integer idCt) {
        String selCt = "SELECT TIPOCENTRO FROM SO_CENTRO_PREL_REF where IDCENTRO = " + idCt.toString();
        ViewObject voCt = am.createViewObjectFromQueryStmt("", selCt);
        voCt.setRangeSize(-1);
        voCt.executeQuery();
        Integer livCt =
            (voCt.first()).getAttribute(0) != null ?
            ((oracle.jbo.domain.Number) (voCt.first()).getAttribute(0)).intValue() : null;
        return livCt.intValue();

    }

    public static int getLivelloTipoInvito(ApplicationModule am, String tipoInv, String ulss, String tpscr) {
        String queryTipoInvito =
            "SELECT a.livello FROM so_cnf_tpinvito a where " + "a.idtpinvito = '" + tipoInv + "' " + "and a.ulss = '" +
            ulss + "' " + "and a.tpscr = '" + tpscr + "'";
        ViewObject voTipoInv = am.createViewObjectFromQueryStmt("", queryTipoInvito);
        voTipoInv.setRangeSize(-1);
        voTipoInv.executeQuery();

        Integer livello =
            (voTipoInv.first()).getAttribute(0) != null ?
            ((oracle.jbo.domain.Number) (voTipoInv.first()).getAttribute(0)).intValue() : null;
        return livello.intValue();
    }


    public static boolean ctVStipo(Integer idCt, String idTpInv, String ulss, String tpscr, ApplicationModule am) {
        if (idCt == null)
            return true;
        String selTipo =
            "select livello from SO_CNF_TPINVITO " + "where IDTPINVITO = '" + idTpInv + "' and ULSS = '" + ulss +
            "' and TPSCR = '" + tpscr + "'";
        ViewObject voTipo = am.createViewObjectFromQueryStmt("", selTipo);
        voTipo.setRangeSize(-1);
        voTipo.executeQuery();
        Integer livTipo =
            (voTipo.first()).getAttribute(0) != null ?
            ((oracle.jbo.domain.Number) (voTipo.first()).getAttribute(0)).intValue() : null;

        String selCt = "SELECT TIPOCENTRO FROM SO_CENTRO_PREL_REF where IDCENTRO = " + idCt.toString();
        ViewObject voCt = am.createViewObjectFromQueryStmt("", selCt);
        voCt.setRangeSize(-1);
        voCt.executeQuery();
        Integer livCt =
            (voCt.first()).getAttribute(0) != null ?
            ((oracle.jbo.domain.Number) (voCt.first()).getAttribute(0)).intValue() : null;

        return (livTipo.intValue() == livCt.intValue());
    }

    public static String getSessoScreening(ApplicationModule am) {
        Map session = ADFContext.getCurrent().getSessionScope();
        String tpscr = (String) session.get("scr");
        String qry = "SELECT a.sesso FROM so_cnf_tpscr a where a.codscr = '" + tpscr + "'";
        ViewObject voScr = am.createViewObjectFromQueryStmt("", qry);
        voScr.setRangeSize(-1);
        voScr.executeQuery();

        Row fScr = voScr.first();
        if (fScr != null)
            return (String) fScr.getAttribute(0);
        else
            return null;
    }


    public static String getSessoSoggetto(ApplicationModule am, String codTS, String ulss) {
        String qrySogg =
            "SELECT a.sesso FROM so_soggetto a where a.codts = '" + codTS + "' and a.ulss = '" + ulss + "'";

        ViewObject voSogg = am.createViewObjectFromQueryStmt("", qrySogg);
        voSogg.setRangeSize(-1);
        voSogg.executeQuery();

        Row result = voSogg.first();

        return (String) result.getAttribute(0);

    }

    public static void filtraDistretti(String codCom, String voDistr) {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject(voDistr);
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String ulss = (String) session.get("ulss");
        if (codCom != null && !codCom.equals("")) {

            // Recupero il comune attivo alla data attuale
            ViewObject comuneView = am.findViewObject("Sogg_ComuneView1");
            comuneView.setWhereClause("codcom = :1 AND (dtiniziovalidita IS NULL OR dtiniziovalidita <= SYSDATE) AND (dtfinevalidita IS NULL OR dtfinevalidita > SYSDATE)");
            comuneView.setWhereClauseParams(null);
            comuneView.setWhereClauseParam(0, codCom);
            comuneView.executeQuery();
            Row comuneRow = comuneView.first();

            if (comuneRow != null) {
                Integer releaseCodeCom = (Integer) comuneRow.getAttribute("ReleaseCode");
                vo.setWhereClause("ulss ='"+ulss+"' and release_code_com = " + releaseCodeCom + " OR release_code_com IS NULL");
            } else {
                vo.setWhereClause("ulss ='"+ulss+"' and release_code_com IS NULL");
            }
        } else {
            vo.setWhereClause("ulss ='"+ulss+"' and release_code_com IS NULL");
        }
        vo.executeQuery();
    }

    public static void filtraOrari(boolean ingresso) {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
        Row cRow = voInvito.getCurrentRow();

        //MOD20071115
        //String whcl = getFiltroOrari(cRow,ctx);
        if (cRow != null) {
            ViewObject voOrari = am.findViewObject("Sogg_OrariDispView1");

            //ho inglobato il codice di getFiltroOrari
            Integer idCprel = (Integer) cRow.getAttribute("Idcentroprelievo");
            Date dtApp = (Date) cRow.getAttribute("Dtapp");
            Integer idApp = (Integer) cRow.getAttribute("Idapp");
            ADFContext adfCtx = ADFContext.getCurrent();
            Map session = adfCtx.getSessionScope();
            boolean sceltaOrario = ((Boolean) session.get("sceltaOrario")).booleanValue();
            String whcl = "1=2";
            String scr = (String) session.get("scr");

            if (sceltaOrario) {
                if (idCprel != null && dtApp != null) {
                    String dataApp = DateUtils.dateToString(dtApp.dateValue());
                    if (idApp != null) {
                        whcl =
                            "DTAPP = to_date('" + dataApp + "','dd/mm/yyyy') and " + "(IDAPP = " + idApp.toString() +
                            " or SLOTOCCUP < DISPSLOT)";
                    } else {
                        whcl = "DTAPP = to_date('" + dataApp + "','dd/mm/yyyy') and " + "SLOTOCCUP < DISPSLOT";
                    }
                }
            }
            voOrari.setWhereClauseParams(new Object[] { scr, idCprel });

            //


            voOrari.setWhereClause(whcl);
            voOrari.setOrderByClause("ORAAPP,MINAPP"); // mauro 30-11-06
            //System.out.println(voOrari.getQuery());
            voOrari.executeQuery();
            /* MOD20071106
                * int count = voOrari.getRowCount();

               if (!ingresso && count == 0)
               */
            if (!ingresso && voOrari.first() == null) {
                cRow.setAttribute("Idapp", null);
            }
        }
    }

    public static void filtraEsiti(Date dataApp, Integer livello) {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String strDataApp;

        // Uso la data appuntamento per filtrare gli esiti disponibili
        // oppure la data di sistema se non c'è la data appuntamento
        if (dataApp == null) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            strDataApp = df.format(new java.util.Date());
        } else {
            strDataApp = DateUtils.dateToString(dataApp.dateValue());
        }
        ViewObject voEsiti = am.findViewObject("Sogg_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND LIVESITO = " +
                               livello.toString() + " AND (DTFINEVALIDITA IS NULL OR DTFINEVALIDITA >= TO_DATE('" +
                               strDataApp + "', 'DD/MM/YYYY'))");
        voEsiti.executeQuery();
    }

    public static void beforeNavSogg(String dest, boolean elenco) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        /*if (dest.startsWith("to_ref_ricerc") || dest.startsWith("to_refCo_rice") || dest.startsWith("to_refMa_rice") ||
            dest.startsWith("to_refCa_rice") || dest.startsWith("to_refPf_rice"))*/
        if (dest.startsWith("to_ref")) {
            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

            Ref_SearchBean bean =
                (Ref_SearchBean) BindingContext.getCurrent().findDataControl("Ref_SearchBeanDataControl").getDataProvider();
            bean.reset();

            Integer idInvito = null;
            Integer idcref = null;
            String cognome = null;
            String nome = null;
            String codTs = null;
            String chiave = null;

            if (elenco) {

                ViewObject voRic = am.findViewObject("Sogg_RicercaView1");
                /*MOD20071107
         * int cnt = voRic.getRowCount();
          if (cnt > 0)
          {*/
                Row cRow = voRic.getCurrentRow();
                if (cRow != null) {
                    idInvito = (Integer) cRow.getAttribute("Idinvito");
                    idcref = (Integer) cRow.getAttribute("Idcref");
                    cognome = (String) cRow.getAttribute("Cognome");
                    nome = (String) cRow.getAttribute("Nome");
                    codTs = (String) cRow.getAttribute("Codts");
                    chiave = (String) cRow.getAttribute("Chiave");

                    bean.setCodts(codTs);
                    bean.setCognome(cognome);
                    bean.setNome(nome);
                    bean.setCentro_ref(idcref == null ? null : idcref.intValue());
                    bean.setEmptyQuery(false);
                    
                    if (session.get("SOAccessoAnonimo")!=null){
                        boolean sOAccessoAnonimo = ((Boolean) session.get("SOAccessoAnonimo")).booleanValue();
                        
                        if (sOAccessoAnonimo && chiave!=null && !"".equals(chiave))
                            bean.setChiave(chiave);
                    }
                }

            } else {
                boolean invPresente =
                    (session.get("invitoPresente") != null) ? ((Boolean) session.get("invitoPresente")).booleanValue() :
                    false;
                if (invPresente) {
                    ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
                    Row cInv = voInvito.getCurrentRow();
                    if (cInv != null) {
                        idInvito = (Integer) cInv.getAttribute("Idinvito");
                        idcref = (Integer) cInv.getAttribute("Idcentroref1liv");
                    }
                }

                ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
                Row cAnag = voAnag.getCurrentRow();
                if (cAnag != null) {
                    cognome = (String) cAnag.getAttribute("Cognome");
                    nome = (String) cAnag.getAttribute("Nome");
                    codTs = (String) cAnag.getAttribute("Codts");
                    chiave = (String) cAnag.getAttribute("Chiave");
                }

                bean.setCodts(codTs);
                bean.setCognome(cognome);
                bean.setNome(nome);
                bean.setCentro_ref(idcref == null ? null : idcref.intValue());
                bean.setEmptyQuery(false);
                
                if (session.get("SOAccessoAnonimo")!=null){
                    boolean sOAccessoAnonimo = ((Boolean) session.get("SOAccessoAnonimo")).booleanValue();
                    
                    if (sOAccessoAnonimo && chiave!=null && !"".equals(chiave))
                        bean.setChiave(chiave);
                }

            }
        }

        else if (dest.startsWith("acc_to")) {
            Sogg_AppModule am =
                (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
            Integer idInvito = null;

            if (elenco) {
                ViewObject voRic = am.findViewObject("Sogg_RicercaView1");
                //MOD20071107
                /*  int cnt = voRic.getRowCount();
          if (cnt > 0)
          {*/
                Row cRow = voRic.getCurrentRow();
                if (cRow != null) {
                    idInvito = (Integer) cRow.getAttribute("Idinvito");
                }
                //}
            } else {
                boolean invPresente = ((Boolean) session.get("invitoPresente")).booleanValue();
                if (invPresente) {
                    ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
                    Row cInv = voInvito.getCurrentRow();
                    if (cInv != null) {
                        idInvito = (Integer) cInv.getAttribute("Idinvito");
                    }
                }

            }

            if (idInvito != null) {

                Integer livello = null;
                String cognome = null;
                String nome = null;
                String codTs = null;
                Integer idCprel = null;
                String chiave = null;

                if (elenco) {
                    ViewObject voRic = am.findViewObject("Sogg_RicercaView1");

                    /*mOD20071107
           * int cnt = voRic.getRowCount();
            if (cnt > 0)
            {*/
                    Row cRow = voRic.getCurrentRow();
                    if (cRow != null) {
                        livello = (Integer) cRow.getAttribute("Livello");
                        cognome = (String) cRow.getAttribute("Cognome");
                        nome = (String) cRow.getAttribute("Nome");
                        chiave = (String) cRow.getAttribute("Chiave");
                        codTs = (String) cRow.getAttribute("Codts");
                        idCprel = (Integer) cRow.getAttribute("Idcentroprelievo");

                    }
                    // }
                } else {
                    ViewObject voAnag = am.findViewObject("Sogg_SoSoggettoView1");
                    Row cAnag = voAnag.getCurrentRow();
                    if (cAnag != null) {
                        cognome = (String) cAnag.getAttribute("Cognome");
                        nome = (String) cAnag.getAttribute("Nome");
                        chiave = (String) cAnag.getAttribute("Chiave");
                        codTs = (String) cAnag.getAttribute("Codts");
                    }

                    boolean invPresente = ((Boolean) session.get("invitoPresente")).booleanValue();
                    if (invPresente) {
                        ViewObject voInvito = am.findViewObject("Sogg_SoInvitoView1");
                        Row cInv = voInvito.getCurrentRow();
                        if (cInv != null) {
                            livello = (Integer) cInv.getAttribute("Livello");
                            idCprel = (Integer) cInv.getAttribute("Idcentroprelievo");
                        }
                    }

                }


                if (livello.intValue() == 2) {
                    try {
                        AccessManager.checkPermission("SOAcc2Liv");
                    } catch (IllegalAccessException ex) {
                        // non ha il permesso, permetto di navigare ma non
                        // imposto nulla
                        return;
                    }
                }

                String tpscr = (String) session.get("scr");

                if (tpscr.equals("CI")) {

                    Acc_RicParam beanAcc =
                        (Acc_RicParam) BindingContext.getCurrent().findDataControl("Acc_RicParamDataControl").getDataProvider();
                    beanAcc.resetCampi();
                    beanAcc.setCognome(cognome);
                    beanAcc.setNome(nome);
                    beanAcc.setTessSan(codTs);
                    
                    if (session.get("SOAccessoAnonimo")!=null){
                        boolean sOAccessoAnonimo = ((Boolean) session.get("SOAccessoAnonimo")).booleanValue();
                        
                        if (sOAccessoAnonimo && chiave!=null && !"".equals(chiave))
                            beanAcc.setChiave(chiave);
                    }
      
                    if (idCprel == null)
                        beanAcc.setIdCprel(null);
                    else
                        beanAcc.setIdCprel(new Integer(idCprel.intValue()));
                    beanAcc.setLivello(livello.toString());
                    beanAcc.setNavigazione(Boolean.TRUE);

                } else if (tpscr.equals("CO")) {

                    AccCo_RicParam beanAccCo =
                        (AccCo_RicParam) BindingContext.getCurrent().findDataControl("AccCo_RicParamDataControl").getDataProvider();
                    beanAccCo.resetCampi();
                    beanAccCo.setCognome(cognome);
                    beanAccCo.setNome(nome);
                    beanAccCo.setTessSan(codTs);
                    
                    if (session.get("SOAccessoAnonimo")!=null){
                        boolean sOAccessoAnonimo = ((Boolean) session.get("SOAccessoAnonimo")).booleanValue();
                        
                        if (sOAccessoAnonimo && chiave!=null && !"".equals(chiave))
                            beanAccCo.setChiave(chiave);
                    }
                    
                    if (idCprel == null)
                        beanAccCo.setIdCprel(null);
                    else
                        beanAccCo.setIdCprel(new Integer(idCprel.intValue()));
                    beanAccCo.setLivello(livello.toString());
                    beanAccCo.setNavigazione(Boolean.TRUE);

                } else if (tpscr.equals("MA")) {

                    AccMa_RicParam beanAccMa =
                        (AccMa_RicParam) BindingContext.getCurrent().findDataControl("AccMa_RicParamDataControl").getDataProvider();
                    beanAccMa.resetCampi();
                    beanAccMa.setCognome(cognome);
                    beanAccMa.setNome(nome);
                    
                    if (session.get("SOAccessoAnonimo")!=null){
                        boolean sOAccessoAnonimo = ((Boolean) session.get("SOAccessoAnonimo")).booleanValue();
                        
                        if (sOAccessoAnonimo && chiave!=null && !"".equals(chiave))
                            beanAccMa.setChiave(chiave);
                    }
                    
                    beanAccMa.setTessSan(codTs);
                    if (idCprel == null)
                        beanAccMa.setIdCprel(null);
                    else
                        beanAccMa.setIdCprel(new Integer(idCprel.intValue()));
                    beanAccMa.setLivello(livello.toString());
                    beanAccMa.setNavigazione(Boolean.TRUE);

                } else if (tpscr.equals("CA")) {

                    AccCa_RicParam beanAccCa =
                        (AccCa_RicParam) BindingContext.getCurrent().findDataControl("AccCa_RicParamDataControl").getDataProvider();
                    beanAccCa.resetCampi();
                    beanAccCa.setCognome(cognome);
                    beanAccCa.setNome(nome);
                    beanAccCa.setTessSan(codTs);
                    
                    if (session.get("SOAccessoAnonimo")!=null){
                        boolean sOAccessoAnonimo = ((Boolean) session.get("SOAccessoAnonimo")).booleanValue();
                        
                        if (sOAccessoAnonimo && chiave!=null && !"".equals(chiave))
                            beanAccCa.setChiave(chiave);
                    }
                    
                    if (idCprel == null)
                        beanAccCa.setIdCprel(null);
                    else
                        beanAccCa.setIdCprel(new Integer(idCprel.intValue()));
                    beanAccCa.setLivello(livello.toString());
                    beanAccCa.setNavigazione(Boolean.TRUE);

                } else if (tpscr.equals("PF")) {
                    AccPf_RicParam beanAccPf =
                        (AccPf_RicParam) BindingContext.getCurrent().findDataControl("AccPf_RicParamDataControl").getDataProvider();
                    beanAccPf.resetCampi();
                    beanAccPf.setCognome(cognome);
                    beanAccPf.setNome(nome);
                    beanAccPf.setTessSan(codTs);
                    if (idCprel == null)
                        beanAccPf.setIdCprel(null);
                    else
                        beanAccPf.setIdCprel(new Integer(idCprel.intValue()));
                    beanAccPf.setLivello(livello.toString());
                    beanAccPf.setNavigazione(Boolean.TRUE);
                }
            }


        } else if (dest.equals("med_ini")) {

            session.put("navDaSogg", Boolean.TRUE);
        }
    }

    public static String trovaTessera(String codts, String ulss) {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        return am.callFunCodtsTrovaDoc(codts, ulss, ConfigurationConstants.TIPO_DOC_TESSERA_SANITARIA);
    }

    public static String trovaTessera(ApplicationModule am, String codts, String ulss) {
        return ((Parent_AppModule) am).callFunCodtsTrovaDoc(codts, ulss,
                                                            ConfigurationConstants.TIPO_DOC_TESSERA_SANITARIA);
    }

    public static void filtraMotEscl(String tEscl, boolean setNumero) {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voMot = am.findViewObject("Sogg_MotEsclView1");
        ViewObject voEscl = am.findViewObject("Sogg_SoEsclusioneView1");
        Row cEscl = voEscl.getCurrentRow();
        voMot.setWhereClauseParams(new Object[] { ulss, tpscr });
        voMot.setWhereClause("TPESCL = '" + tEscl + "'"); 
        voMot.executeQuery();

        if (setNumero) {
            Row fMot = voMot.first();
            if (fMot != null) {
                Integer ngg = (Integer) fMot.getAttribute("Numgiorni");
                cEscl.setAttribute("Numgg", ngg);
                // mauro 01-12 setto anche la causale
                Integer causale = (Integer) fMot.getAttribute("Idcnfescl");
                cEscl.setAttribute("Idcnfescl", causale);
            }

        }

        setDtfinEscl(cEscl);

    }

    public static void setDtfinEscl(Row cEscl) {
        
        String tEscl = (String) cEscl.getAttribute("Tpescl");
        if (tEscl.equals("T")) {
            Date dtEscl = (Date) cEscl.getAttribute("Dtescl");
            Integer numgg = (Integer) cEscl.getAttribute("Numgg");
            if (dtEscl != null && numgg != null) {
                java.util.Date jEscl = dtEscl.dateValue();
                java.util.Date jFin = AgendaUtils.addDaysToDate(jEscl, numgg.intValue());
                Date dtFin = DateUtils.getOracleDate(jFin);
                cEscl.setAttribute("Dtfine", dtFin);
            }
        }

    }

    @SuppressWarnings("unchecked")
    public static void svuotaInterfacciaPropagazione() {
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        ViewObject voEsclPropCI = am.findViewObject("Cnf_EsclProposteViewCI");
        ViewObject voEsclPropMA = am.findViewObject("Cnf_EsclProposteViewMA");
        ViewObject voEsclPropCO = am.findViewObject("Cnf_EsclProposteViewCO");


        session.put("propagaCI", Boolean.FALSE);
        voEsclPropCI.setWhereClause("1=2");
        voEsclPropCI.executeQuery();

        session.put("propagaMA", Boolean.FALSE);
        voEsclPropMA.setWhereClause("1=2");
        voEsclPropMA.executeQuery();

        session.put("propagaCO", Boolean.FALSE);
        voEsclPropCO.setWhereClause("1=2");
        voEsclPropCO.executeQuery();
    }

    /**
     * Metodo invocato in fase di inizializzazione del sistema per filtrare a priori certi tipi di dati
     * ed evitare un sovraccarico di memoria (quando si carica un UIModel viene
     * eseguita la query, quindi è meglio che la where clause sia già impostata
     * @param tpscr
     * @param ulss
     * @param ctx
     */
    public static void filterSogg(String ulss, String tpscr) {
        BindingContext ctx = BindingContext.getCurrent();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        Sogg_AppModule am = (Sogg_AppModule) ctx.findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Sogg_RicercaView1");
        vo.setWhereClauseParams(new Object[] { ulss, tpscr, ulss, tpscr, tpscr });
        vo.setWhereClause("1=2");

        vo = am.findViewObject("Sogg_SoDocumentiSoggStoricoView1");
        vo.setWhereClause("1=2");

        vo = am.findViewObject("Sogg_SelCprelView1");
        vo.setWhereClauseParams(new Object[] { ulss, tpscr, ulss, tpscr });
        vo.executeQuery();

        if (((Boolean) session.get("modalita_centri")).booleanValue()) {

            Integer c1 = (Integer) session.get("centro1liv");
            Integer c2 = (Integer) session.get("centro2liv");
            String in = (String) session.get("elenco_centri");
            String whcl = "";
            if (c1 != null || c2 != null) {
                whcl += " idcentro in " + in;
                if (c1 != null)
                    whcl += " or idcentro=" + c1;
                if (c2 != null)
                    whcl += " or idcentro=" + c2;
            }
            vo.setWhereClause(whcl);
            vo.executeQuery();
        }

        vo = am.findViewObject("Sogg_OrariDispView1");
        vo.setWhereClauseParams(new Object[] { tpscr, null });
        vo.setWhereClause("1=2");

        vo = am.findViewObject("Sogg_SoSoggettoView1");
        vo.setWhereClause("1=2");
        //vo.setWhereClauseParams(new Object[] { null, ulss });

        vo = am.findViewObject("Sogg_DistrettiView1");
        vo.setWhereClause("ULSS ='"+ulss+"'");
        vo = am.findViewObject("Sogg_DistrettiView2");
        vo.setWhereClause("ULSS ='"+ulss+"'");

        vo = am.findViewObject("Sogg_MotEsclView1");
        vo.setWhereClauseParams(new Object[] { ulss, tpscr });

        vo = am.findViewObject("Sogg_StInvitiView1");
        vo.setWhereClauseParams(new Object[] { ulss, tpscr, null });

        //tipi documenti
        vo = am.findViewObject("Sogg_SoCnfTipiDocView1");
        vo.setWhereClause("ULSS ='"+ulss+"'");
        
        //sottogruppi popolazione
        vo=am.findViewObject( "Sogg_SoCnfClassePopView1");
        vo.setWhereClause("TPSCR ='"+tpscr+"'");

        //per lo storico
        Object[] pars = { ulss, tpscr, null };
        vo = am.findViewObject("Sogg_StInvitiView1");
        vo.setWhereClauseParams(pars);

        vo = am.findViewObject("Sogg_StEsclView1");
        vo.setWhereClauseParams(pars);

        vo = am.findViewObject("Sogg_StRef1View1");
        vo.setWhereClauseParams(pars);

        vo = am.findViewObject("Sogg_StRef2View1");
        vo.setWhereClauseParams(pars);

        vo = am.findViewObject("Sogg_StCoRef1View1");
        vo.setWhereClauseParams(pars);

        vo = am.findViewObject("Sogg_StPfRef1View1");
        vo.setWhereClauseParams(pars);

        vo = am.findViewObject("Sogg_SoRefertocolon2livStoricoView1");
        vo.setWhereClauseParams(pars);

        vo = am.findViewObject("Sogg_SoRefertomammo1livStoricoView1");
        vo.setWhereClauseParams(pars);

        vo = am.findViewObject("Sogg_SoRefertomammo2livStoricoView1");
        vo.setWhereClauseParams(pars);

        // Trial
        Integer idTrialAttr = (Integer) session.get("trial");
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

        //accettazione
        if ("CO".equals(tpscr)) {
            AccCo_AppModule aam = (AccCo_AppModule) ctx.findDataControl("AccCo_AppModuleDataControl").getDataProvider();

            pars = new Object[] { ulss, tpscr };
            vo = aam.findViewObject("AccCo_SelCprelView1");
            vo.setWhereClauseParams(pars);

            vo = aam.findViewObject("AccCo_SoInvitoView1");
            vo.setWhereClauseParams(new Object[] { null });

            vo = aam.findViewObject("AccCo_SoAnamnesicolonView1");
            vo.setWhereClauseParams(new Object[] { null });

        }
        //accettazione
        else if ("CI".equals(tpscr)) {
            Acc_AppModule aam = (Acc_AppModule) ctx.findDataControl("Acc_AppModuleDataControl").getDataProvider();


            pars = new Object[] { ulss, tpscr };
            vo = aam.findViewObject("Acc_SelCprelView1");
            vo.setWhereClauseParams(pars);

            vo = aam.findViewObject("Acc_SoInvitoView1");
            vo.setWhereClauseParams(new Object[] { null });

            vo = aam.findViewObject("Acc_SoAccCito2livView1");
            vo.setWhereClauseParams(new Object[] { null });

            vo = aam.findViewObject("Acc_IntervPrecElencoFull1");
            vo.setWhereClauseParams(new Object[] { tpscr, null });

            vo = aam.findViewObject("Acc_IntervPrecElencoFull2");
            vo.setWhereClauseParams(new Object[] { tpscr, null });

            vo = aam.findViewObject("Acc_IntPrec2ElencoFull1");
            vo.setWhereClauseParams(new Object[] { tpscr, null });

            vo = aam.findViewObject("Acc_IntPrec2ElencoFull2");
            vo.setWhereClauseParams(new Object[] { tpscr, null });

            vo = aam.findViewObject("Acc_Referto1livView1");
            vo.setWhereClauseParams(new Object[] { null });
        } else if ("MA".equals(tpscr)) {
            AccMa_AppModule aam = (AccMa_AppModule) ctx.findDataControl("AccMa_AppModuleDataControl").getDataProvider();


            pars = new Object[] { ulss, tpscr };
            vo = aam.findViewObject("AccMa_SelCprelView1");
            vo.setWhereClauseParams(pars);

            vo = aam.findViewObject("AccMa_SoInvitoView1");
            vo.setWhereClauseParams(new Object[] { null });

            vo = aam.findViewObject("AccMa_SoAnamnesimxSintomiView1");
            vo.setWhereClauseParams(new Object[] { null });
        } else if ("CA".equals(tpscr)) {
            AccCa_AppModule aam = (AccCa_AppModule) ctx.findDataControl("AccCa_AppModuleDataControl").getDataProvider();


            pars = new Object[] { ulss, tpscr };
            vo = aam.findViewObject("AccCa_SelCentriView1");
            vo.setWhereClauseParams(pars);

            vo = aam.findViewObject("AccCa_SoInvitoView1");
            vo.setWhereClauseParams(new Object[] { null });

        } else if ("PF".equals(tpscr)) {
            AccPf_AppModule aam = (AccPf_AppModule) ctx.findDataControl("AccPf_AppModuleDataControl").getDataProvider();

            pars = new Object[] { ulss, tpscr };
            vo = aam.findViewObject("AccPf_SelCprelView1");
            vo.setWhereClauseParams(pars);

            vo = aam.findViewObject("AccPf_SoInvitoView1");
            vo.setWhereClauseParams(new Object[] { null });
        }

    }

    public static void loadVoDet() {
        /*  Sogg_AppModule am = (Sogg_AppModule) ctx.getBindingContext().
          findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        // carico vo per sesso
        ViewObject voSesso = am.findViewObject("Sogg_SessoView1");
        ViewHelper.clearVO(voSesso);
        Row nRow = voSesso.createRow();
        nRow.setAttribute(0,"M");
        nRow.setAttribute(1,"Maschio");
        voSesso.insertRow(nRow);
        nRow = voSesso.createRow();
        nRow.setAttribute(0,"F");
        nRow.setAttribute(1,"Femmina");
        voSesso.insertRow(nRow);
        //
        ViewObject voTEscl = am.findViewObject("Sogg_TipiEsclView1");
        ViewHelper.clearVO(voTEscl);
        nRow = voTEscl.createRow();
        nRow.setAttribute(0,"T");
        nRow.setAttribute(1,"Temporanea");
        voTEscl.insertRow(nRow);
        nRow = voTEscl.createRow();
        nRow.setAttribute(0,"D");
        nRow.setAttribute(1,"Definitiva");
        voTEscl.insertRow(nRow);
        //
        */
    }

    public static Integer calcolaEta(Date dataNascita) {
        Calendar calNascita = Calendar.getInstance();
        calNascita.setTime(dataNascita.getValue());
        Calendar calAdesso = Calendar.getInstance();
        int eta = calAdesso.get(Calendar.YEAR) - calNascita.get(Calendar.YEAR);
        if (calNascita.get(Calendar.MONTH) > calAdesso.get(Calendar.MONTH) ||
            (calNascita.get(Calendar.MONTH) == calAdesso.get(Calendar.MONTH) &&
             calNascita.get(Calendar.DATE) > calAdesso.get(Calendar.DATE))) {
            eta--;
        }
        return eta;
    }
}
