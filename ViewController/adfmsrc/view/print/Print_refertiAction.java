package view.print;

import insiel.dataHandling.DateUtils;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import model.common.Print_AppModule;

import model.commons.AccessManager;
import model.commons.ViewHelper;

import model.datacontrol.Cnf_selectionBean;
import model.datacontrol.PL_Bean;
import model.datacontrol.PrintBean;

import model.datacontrol.Sogg_ConsensoParam;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import oracle.jbo.uicli.binding.JUCtrlListBinding;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Print_refertiAction extends Parent_DataForwardAction {
    private RichForm frm;

    public Print_refertiAction() {
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
        String mode = (String) ADFContext.getCurrent().getRequestScope().get("print_mode");
        System.out.println("--------------------------------> PRINT MODE:" + mode);
        if ("empty".equals(mode)) {
            Print_AppModule am =
                (Print_AppModule) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
            PrintBean bean =
                (PrintBean) BindingContext.getCurrent().findDataControl("PrintBeanDataControl").getDataProvider();
            bean.reset();

            String tpscr = (String) ADFContext.getCurrent().getSessionScope().get("scr");
            //nome del viewobject
            String voName =
                (String) ViewHelper.decodeByTpscr(tpscr, "Print_SoAllegatoRefView1", "Print_SoAllegatoRefCOView1",
                                                  "Print_SoAllegatoRefMAView1", "Print_SoAllegatoRefCAView1",
                                                  "Print_SoAllegatoRefPFView1");

            ViewObject vo = am.findViewObject(voName);
            vo.setWhereClause("1=2");
            vo.executeQuery();
            
            vo = am.findViewObject("Print_SoCnfClassePopView1");
            vo.setWhereClauseParams(new Object[]{tpscr});
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
        String where = "ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND ";

        try {

            //nome del viewobject
            String voName =
                (String) ViewHelper.decodeByTpscr(tpscr, "Print_SoAllegatoRefView1", "Print_SoAllegatoRefCOView1",
                                                  "Print_SoAllegatoRefMAView1", "Print_SoAllegatoRefCAView1",
                                                  "Print_SoAllegatoRefPFView1");

            ViewObject vo = am.findViewObject(voName);

            if (bean.getNuovi() != null && !"tutti".equals(bean.getNuovi())) {
                if ("da stampare".equals(bean.getNuovi()))
                    where += "DTSTAMPA IS NULL AND ";
                else if ("gia stampati".equals(bean.getNuovi()))
                    where += "NOT(DTSTAMPA IS NULL) AND ";
            }

            if (bean.getTpsugg() != null && !bean.getInitialSugg().equals(bean.getTpsugg())) {
                if (bean.getLivello_ref() != null && bean.getLivello_ref().startsWith("LIV")) {
                    where +=
                        "IDSUGG=" + bean.getTpsugg() + " AND " + "LIVELLO_REF='" + bean.getLivello_ref().substring(3) +
                        "' AND ";
                }
            }

            if (bean.getLivello() != null && !"tutti".equals(bean.getLivello()))
                where += "LIVELLO='" + bean.getLivello() + "' AND ";


            if (bean.getCognome() != null)
                //MOD20071114 performance


                // where+="UPPER(COGNOME) LIKE'"+ViewHelper.replaceApostrophe(bean.getCognome().toUpperCase())+"%' AND ";
                where += "COGNOME LIKE'" + ViewHelper.replaceApostrophe(bean.getCognome().toUpperCase()) + "%' AND ";

            if (bean.getNome() != null)
                //MOD20071114 performance


                // where+="UPPER(NOME) LIKE'"+ViewHelper.replaceApostrophe(bean.getNome().toUpperCase())+"%' AND ";
                where += "NOME LIKE'" + ViewHelper.replaceApostrophe(bean.getNome().toUpperCase()) + "%' AND ";

            if (bean.getCreato() != null)
                where +=
                    "DTCREAZIONE>=TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getCreato()) +
                    "','dd/MM/yyyy') AND ";
            if (bean.getCreato_al() != null)
                where +=
                    "DTCREAZIONE<TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getCreato_al()) +
                    "','dd/MM/yyyy') AND ";


            if (bean.getStampate_dal() != null)
                where +=
                    "DTSTAMPA>=TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getStampate_dal()) + "','" +
                    DateUtils.DATE_PATTERN + "') AND ";
            if (bean.getStampate_al() != null)
                where +=
                    "DTSTAMPA<TO_DATE('" + new SimpleDateFormat("dd/MM/yyyy").format(bean.getStampate_al()) + "','" +
                    DateUtils.DATE_PATTERN + "') AND ";

            if (bean.getCentro_ref() != null && bean.getCentro_ref().intValue() >= 0) {
                where += "IDCENTROREF=" + bean.getCentro_ref() + " AND ";
            }
            if (bean.getCentro() != null && bean.getCentro().intValue() >= 0) {
                where += "ID_CENTRO_PREL=" + bean.getCentro() + " AND ";
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
            //gestione delle raccomandate e dei referti da non spedire, solo per il mammografico
            if ("MA".equals(tpscr)) {

                //referti da spedire o da non spedire
                if (bean.getDa_spedire() != null) {
                    //se contiene il NON (NON spedire referto)
                    if (bean.getDa_spedire().indexOf("NON") >= 0)
                        where += "NON_SPEDIRE_REFERTO=1 AND ";
                    else
                        where += "(NON_SPEDIRE_REFERTO is null or NON_SPEDIRE_REFERTO=0) AND ";

                }

                if (bean.getRaccomandata() == 0)
                    where += "(RACC_LETT_REFERTO IS NULL OR RACC_LETT_INVITO=0) AND ";
                else if (bean.getRaccomandata() > 0)
                    where += "RACC_LETT_REFERTO=" + bean.getRaccomandata() + " AND ";
            }

            // HPV-DNA
            if ("CI".equals(tpscr)) {
                if (bean.getPrelievoHpv() != null) {
                    if ("Si".equals(bean.getPrelievoHpv())) {
                        where += "PRELIEVO_HPV = 2 AND ";
                    } else if ("No".equals(bean.getPrelievoHpv())) {
                        where += "PRELIEVO_HPV = 1 AND ";
                    }
                }
            }

            // Trial
            if (bean.getIdStatoTrial() != null) {
                switch (bean.getIdStatoTrial().intValue()) {
                case -1:
                    where += "idstato_trial IS NULL AND ";
                    break;
                case -2:
                    where += "idstato_trial IS NOT NULL AND ";
                    break;
                default:
                    where += "idstato_trial = " + bean.getIdStatoTrial().toString() + " AND ";
                }
            }
            if (bean.getIdBraccioTrial() != null) {
                switch (bean.getIdBraccioTrial().intValue()) {
                case -1:
                    where += "idbraccio_trial IS NULL AND ";
                    break;
                case -2:
                    where += "idbraccio_trial IS NOT NULL AND ";
                    break;
                default:
                    where += "idbraccio_trial = " + bean.getIdBraccioTrial().toString() + " AND ";
                }
            }
            
            //sottogruppi
            if (this.selectedItems != null && this.selectedItems.size() > 0){
                String classi = "";
                for (int i = 0; i < selectedItems.size(); i++) {
                    classi += "'" + selectedItems.get(i) + "',";
                }
                classi = classi.substring(0, classi.length() - 1);
                
                where += "CODCLASSEPOP IN ("+classi+") AND ";
            }

            if (this.selectedCentri != null && this.selectedCentri.size() > 0){
                String centri = "";
                for (int i = 0; i < selectedCentri.size(); i++) {
                    centri += "" + selectedCentri.get(i) + ",";
                }
                centri = centri.substring(0, centri.length() - 1);
                
                where += "ID_CENTRO_PREL IN ("+centri+") AND ";
            } 
            
            //controllo se l'utente ha delle limitazioni sui centri
            try{
                AccessManager.checkPermission("SOLimiteCentri");
                ADFContext adfCtx = ADFContext.getCurrent();
                Map sessionMap = adfCtx.getSessionScope();
                List<Integer> centriAutorizzati = (List<Integer>) sessionMap.get("centriautorizzati");
                String centri = "";
                if (centriAutorizzati != null && !centriAutorizzati.isEmpty()){
                    for (int i = 0; i < centriAutorizzati.size(); i++) {
                        centri += "" + centriAutorizzati.get(i) + ",";
                    }
                    centri = centri.substring(0, centri.length() - 1);                           
                    where += "ID_CENTRO_PREL IN (" + centri + ") AND ";
                } else {
                    where += "1=2 AND ";
                }           
            } catch (IllegalAccessException e){                                      
               // non faccio niente
            }

            if (where.endsWith(" AND "))
                where = where.substring(0, where.length() - 5);

            vo.setWhereClause(where);
            //System.out.println(vo.getQuery());

            /*MOD 19/06/2007
              //se possibile vorrei mantenere la riga corrente
              Number id=null;
              if(vo.getCurrentRow()!=null)
                id=(Number)vo.getCurrentRow().getAttribute("Idallegato");

            //  System.out.println(vo.getQuery());
              ViewHelper.restoreCurrentRow(vo,"Idallegato",id);
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
        
        this.selectedItems = null;
        this.selectedCentri = null;

        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc1', 'pt1:soc2', 'pt1:soc3', 'pt1:soc44', 'pt1:soc4', 'pt1:soc6', 'pt1:soc10', 'pt1:soc11', 'pt1:soc12', 'pt1:soc14'])");
    }

    private List<String> selectedItems;
    public void setSelectedItems(List<String> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }
    
    private List<Integer> selectedCentri;

    public void setSelectedCentri(List<Integer> selectedCentri) {
        this.selectedCentri = selectedCentri;
    }

    public List<Integer> getSelectedCentri() {
        return selectedCentri;
    }

    public void onChangeCodClassePop(ValueChangeEvent valueChangeEvent) {
        List<String> selectedItems = new ArrayList<>();
        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        
        if (valueChangeEvent==null || bean==null)
            return;
        
        selectedItems = (List<String>) valueChangeEvent.getNewValue(); 
        
        if (selectedItems==null || selectedItems.size()>1)
            bean.setCodClassePop(null);
        else 
            bean.setCodClassePop(selectedItems.get(0));
    }

}
