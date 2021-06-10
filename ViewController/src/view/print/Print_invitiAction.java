package view.print;

import insiel.dataHandling.DateUtils;

import java.text.SimpleDateFormat;

import java.util.Map;

import javax.faces.event.ActionEvent;

import model.common.Print_AppModule;

import model.commons.AccessManager;

import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.PrintBean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

import view.referto.GestoreRefertiUI;

import view.util.Utility;

public class Print_invitiAction extends Parent_DataForwardAction {
    private RichForm frm;

    public Print_invitiAction() {
    }

    public void setFrm(RichForm frm) {
        this.frm = frm;
    }

    public RichForm getFrm() {
        if (frm == null) {
            findForward();
        }

        return frm;
    }

    private void findForward() {
        Print_AppModule am =
            (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Print_SoTemplateEtichetteView1");
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String where =
            "Print_SoTemplate.TPSCR='" + tpscr + "' and " + "(Print_SoTemplate.ULSS='" + ulss +
            "' OR Print_SoTemplate.ULSS='" + AccessManager.CODREGIONALE +
            "') AND (Print_SoTemplate.DTFINEVALTEMPL is null or " + "Print_SoTemplate.DTFINEVALTEMPL>sysdate)";
        vo.setWhereClause(where);
        vo.executeQuery();

        vo = am.findViewObject("Cnf_SoInsertoView1");
        where = "ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'";
        vo.setWhereClause(where);
        vo.executeQuery();

        String pmode = (String) ADFContext.getCurrent().getRequestScope().get("print_mode");
        System.out.println("--------------------------------> PRINT MODE:" + pmode);
        if ("empty".equals(pmode)) {
            PrintBean bean =
                (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();
            bean.reset();
            vo = am.findViewObject("Print_SoAllegatoInvitiView1");
            vo.setWhereClause("1=2");
            vo.executeQuery();
        }
    }

    @Override
    protected void setAppModule() {
        this.amName = "Print_AppModule";
    }

    public String onFiltra() {
        Print_AppModule am =
            (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
        PrintBean bean =
            (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Print_SoAllegatoInvitiView1");

        //03102011 Gaion parametri di ricerca minimi
        boolean paramOk = false;

        if ((bean.getCognome() != null) && (bean.getCognome().length() > 1)) {
            paramOk = true;
        } else if ((bean.getNome() != null) && (bean.getNome().length() > 1)) {
            paramOk = true;
        } else if ((bean.getData_app() != null) && (bean.getData_app_al() != null)) {

            paramOk = true;
        } else if ((bean.getCreato() != null) && (bean.getCreato_al() != null)) {
            paramOk = true;
        } else if ((bean.getStampate_dal() != null) && (bean.getStampate_al() != null)) {
            paramOk = true;
        }

        if (!paramOk) {
            this.handleException("Parametri di ricerca insufficienti. Impostare almeno uno di questi campi: Cognome (con almeno due caratteri), Nome (con almeno due caratteri)," +
                                 "Appuntamento dal - al, Creati dal - al, Stampati dal - al", null);
            return "error";

        }
        //03102011 fine

        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String where = "Print_SoAllegato.ULSS='" + ulss + "' AND Print_SoAllegato.TPSCR='" + tpscr + "' AND ";

        try {
            if (bean.getNuovi() != null && !"tutti".equals(bean.getNuovi())) {
                if ("da stampare".equals(bean.getNuovi()))
                    where += "DTSTAMPA IS NULL AND ";
                else if ("gia stampati".matches(bean.getNuovi()))
                    where += "NOT(DTSTAMPA IS NULL) AND ";
            }


            if (bean.getTpinvito() != null && !"tutti".equals(bean.getTpinvito())) {
                where += "IDTPINVITO='" + bean.getTpinvito() + "' AND ";
            }
            if (bean.getCentro() != null && bean.getCentro().intValue() >= 0) {
                where += "IDCENTROPRELIEVO=" + bean.getCentro() + " AND ";
            }


            /*20080704 MOD: filtro su intervallo data appuntamento
             if(bean.getData_app()!=null && bean.getData_app().length()>0)
                where+="DTAPP=TO_DATE('"+bean.getData_app()+"','dd/MM/yyyy') AND ";
                */
            if (bean.getData_app() != null)
                where +=
                    "DTAPP>=TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_app()) +
                    "','dd/MM/yyyy') AND ";

            if (bean.getData_app_al() != null)
                where +=
                    "DTAPP<TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getData_app_al()) +
                    "','dd/MM/yyyy') AND ";
            /*20080704 FINE MOD*/

            if (bean.getCognome() != null)
                //MOD20071114 performance


                //where+="UPPER(A_SoSoggetto.COGNOME) LIKE'"+ViewHelper.replaceApostrophe(bean.getCognome().toUpperCase())+"%' AND ";
                where +=
                    "A_SoSoggetto.COGNOME LIKE'" + ViewHelper.replaceApostrophe(bean.getCognome().toUpperCase()) +
                    "%' AND ";

            if (bean.getNome() != null)
                //MOD20071114 performance


                //where+="UPPER(A_SoSoggetto.NOME) LIKE'"+ViewHelper.replaceApostrophe(bean.getNome().toUpperCase())+"%' AND ";
                where +=
                    "A_SoSoggetto.NOME LIKE'" + ViewHelper.replaceApostrophe(bean.getNome().toUpperCase()) + "%' AND ";

            if (bean.getCreato() != null)
                where +=
                    "Print_SoAllegato.DTCREAZIONE>=TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getCreato()) + "','dd/MM/yyyy') AND ";
            if (bean.getCreato_al() != null)
                where +=
                    "Print_SoAllegato.DTCREAZIONE<TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getCreato_al()) + "','dd/MM/yyyy') AND ";


            if (bean.getStampate_dal() != null)
                where +=
                    "Print_SoAllegato.DTSTAMPA>=TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getStampate_dal()) + "','" + DateUtils.DATE_PATTERN +
                    "') AND ";
            if (bean.getStampate_al() != null)
                where +=
                    "Print_SoAllegato.DTSTAMPA<TO_DATE('" +
                    new SimpleDateFormat("dd/MM/yyyy").format(bean.getStampate_al()) + "','" + DateUtils.DATE_PATTERN +
                    "') AND ";

            //gestione delle raccomandate, solo per il mammografico
            if ("MA".equals(tpscr)) {
                if (bean.getRaccomandata() == 0)
                    where += "(RACC_LETT_INVITO IS NULL OR RACC_LETT_INVITO=0) AND ";
                else if (bean.getRaccomandata() > 0)
                    where += "RACC_LETT_INVITO=" + bean.getRaccomandata() + " AND ";
            }

            // HPV-DNA
            if ("CI".equals(tpscr)) {
                if ("PAP test".equals(bean.getTestProposto()))
                    where += "A_SoInvito.TEST_PROPOSTO = 'PAP' AND ";
                else if ("HPV".equals(bean.getTestProposto()))
                    where += "A_SoInvito.TEST_PROPOSTO = 'HPV' AND ";
            }

            // Trial
            if (bean.getIdStatoTrial() != null) {
                switch (bean.getIdStatoTrial().intValue()) {
                case -1:
                    where += "A_SoSoggScr.idstato_trial IS NULL AND ";
                    break;
                case -2:
                    where += "A_SoSoggScr.idstato_trial IS NOT NULL AND ";
                    break;
                default:
                    where += "A_SoSoggScr.idstato_trial = " + bean.getIdStatoTrial().toString() + " AND ";
                }
            }
            if (bean.getIdBraccioTrial() != null) {
                switch (bean.getIdBraccioTrial().intValue()) {
                case -1:
                    where += "A_SoSoggScr.idbraccio_trial IS NULL AND ";
                    break;
                case -2:
                    where += "A_SoSoggScr.idbraccio_trial IS NOT NULL AND ";
                    break;
                default:
                    where += "A_SoSoggScr.idbraccio_trial = " + bean.getIdBraccioTrial().toString() + " AND ";
                }
            }

            if (bean.getConsensi() != null && !"qualsiasi valore".equals(bean.getConsensi())) {
                if ("consensi non dati".equals(bean.getConsensi())) {
                    where += "A_SoSoggScr.CONSENSO IN (0,1) AND A_SoSoggScr.CONSENSO_COND IN (0,1) AND ";
                } else if ("dato il primo".equals(bean.getConsensi())) {
                    where += "A_SoSoggScr.CONSENSO = 2 AND ";
                } else if ("dati entrambi".equals(bean.getConsensi())) {
                    where += "A_SoSoggScr.CONSENSO = 2 AND A_SoSoggScr.CONSENSO_COND = 2 AND ";
                }
            }

            if (where.endsWith(" AND "))
                where = where.substring(0, where.length() - 5);

            vo.setWhereClause(where);
            //System.out.println(vo.getQuery());
            //se possibile vorrei mantenere la riga corrente

            /* MOD 19/06/2007
              // mauro correzione 01-06-2007
              Number id=null;
              if(vo.getCurrentRow()!=null)
              {
                id=(Number)vo.getCurrentRow().getAttribute("Idallegato");
                ViewHelper.restoreCurrentRow(vo,"Idallegato",id);
              }
              else
              {
                vo.executeQuery();
              }
              // mauro 01-06-2007 fine correzione
              */
            ViewHelper.queryAndRestoreCurrentRow(vo);

            //20110524 SARA
            String qry = vo.getQuery();

            Object[] pars = vo.getWhereClauseParams();
            if (pars != null) {
                for (int i = 0; i < pars.length; i++) {
                    qry = qry.replaceFirst(":" + (i + 1), "'" + (String) pars[i] + "'");
                    //System.out.println(qry);
                }

            }

            String newQry = "select codts, ulss from (" + qry + ")";
            Cnf_selectionBean clauseBean =
                (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            clauseBean.setInClause(newQry);
            clauseBean.setNote(null);

            //fine 20110524

        } catch (Exception ex) {
            //  ex.printStackTrace();
            this.handleException(ex.getMessage(), null);
        }
        
        return "filtered";
    }

    public void onReset(ActionEvent actionEvent) {
        PrintBean bean =
            (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();
        
        bean.reset();

        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc1', 'pt1:soc2', 'pt1:soc4', 'pt1:soc6', 'pt1:soc10', 'pt1:soc11', 'pt1:soc12'])");
    }
}
