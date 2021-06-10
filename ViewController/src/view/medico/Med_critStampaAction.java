package view.medico;

import insiel.utilities.dataformats.DateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import model.Print_AppModuleImpl;

import model.commons.ConfigurationConstants;

import model.commons.Lettera;

import model.datacontrol.Med_RicParam;

import model.datacontrol.PL_Bean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.context.RequestContext;
import org.apache.myfaces.trinidad.event.ReturnEvent;

import view.commons.action.Parent_DataForwardAction;

import view.util.Utility;

public class Med_critStampaAction extends Parent_DataForwardAction {

    private RichInputText codComu;
    private RichInputText desComu;

    @Override
    protected void setAppModule() {
        this.amName="Med_AppModule";
    }


    public String chDesComu() {
        Med_RicParam bean =
            (Med_RicParam) BindingContext.getCurrent().findDataControl("Med_RicParamDataControl").getDataProvider();
        String desCom = bean.getDesComu();

        if (desCom == null || desCom.equals("")) {
            bean.setCodComu(null);
        }
        return "lovComune";
    }

    public void lovComuneReturnListener(ReturnEvent returnEvent) {
        RequestContext.getCurrentInstance().addPartialTarget(codComu);
        RequestContext.getCurrentInstance().addPartialTarget(desComu);
        ADFContext adfCtx = ADFContext.getCurrent();
        adfCtx.getViewScope().remove("onClick");
        adfCtx.getViewScope().remove("onOneReturn");
        Utility.addScriptOnPartialRequest("setFocus('" + desComu.getClientId() + "', false)");
    }

    public void setCodComu(RichInputText codComu) {
        this.codComu = codComu;
    }

    public RichInputText getCodComu() {
        return codComu;
    }

    public void setDesComu(RichInputText desComu) {
        this.desComu = desComu;
    }

    public RichInputText getDesComu() {
        return desComu;
    }

    public void reimposta(ActionEvent actionEvent) {
        Med_RicParam bean =
            (Med_RicParam) BindingContext.getCurrent().findDataControl("Med_RicParamDataControl").getDataProvider();
        bean.resetCampiStampa();

        PL_Bean templateBean =
            (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        templateBean.setTemplate(null);
        
        codComu.resetValue();
        desComu.resetValue();
    }


    public void stampa(FacesContext facesContext, OutputStream outputStream) throws Exception {
        String selClause = "select so_soggetto.codts, so_soggetto.ulss";
        String fromClause = " from so_soggetto, SO_CNF_ANAG_SCR";
        String whereClause = "";

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Med_ElencoMediciView1Iterator");
        ViewObject voElenco = voIter.getViewObject();

        Map session = ADFContext.getCurrent().getSessionScope();
        String singolo = (String) session.get("medStsing");

        if ("true".equals(singolo)) {
            Row cMed = voElenco.getCurrentRow();
            Integer codMed = (Integer) cMed.getAttribute("Codiceregmedico");

            whereClause =
                " where so_soggetto.CODICEREGMEDICO = " + codMed.toString() + " AND so_soggetto.ULSS='" +
                session.get("ulss") + "'" + " and SO_SOGGETTO.CODANAGREG = SO_CNF_ANAG_SCR.CODANAGREG" +
                " and SO_SOGGETTO.ulss = SO_CNF_ANAG_SCR.ULSS" + " and SO_CNF_ANAG_SCR.TPSCR ='" + session.get("scr") +
                "'" + " and SO_CNF_ANAG_SCR.INCLUSO = 1";
        } else {
            String whElenco = voElenco.getWhereClause();

            whereClause =
                " where (so_soggetto.CODICEREGMEDICO,so_soggetto.ulss) in (select CODICEREGMEDICO,ulss from SO_MEDICO," +
                "so_cnf_tpscr where " + whElenco + ")" + " and SO_SOGGETTO.CODANAGREG = SO_CNF_ANAG_SCR.CODANAGREG" +
                " and SO_SOGGETTO.ulss = SO_CNF_ANAG_SCR.ULSS" + " and SO_CNF_ANAG_SCR.TPSCR ='" + session.get("scr") +
                "'" + " and SO_CNF_ANAG_SCR.INCLUSO = 1";
        }

        // se una delle date e' valorizzata devo aggiungere so_invito nella from e
        // lecondizione nella where
        Med_RicParam bean =
            (Med_RicParam) BindingContext.getCurrent().findDataControl("Med_RicParamDataControl").getDataProvider();
        Date dtIni = bean.getDtIni();
        Date dtFin = bean.getDtFin();

        if ((dtIni != null) || (dtFin != null)) {
            fromClause += ",so_invito";
            whereClause += " and so_invito.codts = so_soggetto.codts";
            whereClause += " and so_invito.ulss = so_soggetto.ulss";
            if (dtIni != null) {
                whereClause += " and so_invito.dtapp >= to_date('" + DateUtils.dateToString(dtIni) + "','dd/MM/yyyy')";
            }

            if (dtFin != null) {
                whereClause += " and so_invito.dtapp < to_date('" + DateUtils.dateToString(dtFin) + "','dd/MM/yyyy') + 1";
            }
        }

        Integer etaMin = bean.getEtaMin();
        Integer etaMax = bean.getEtaMax();
        Date dtRif = bean.getDtRif();

        if (etaMin != null) {
            whereClause +=
                " and floor(months_between(to_date('" + DateUtils.dateToString(dtRif) + "','dd/mm/yyyy'),so_soggetto.data_nascita)/12) >= " +
                etaMin.toString();
        }

        if (etaMax != null) {
            whereClause +=
                " and floor(months_between(to_date('" + DateUtils.dateToString(dtRif) + "','dd/mm/yyyy'),so_soggetto.data_nascita)/12) <= " +
                etaMax.toString();
        }

        String sesso = bean.getSesso();
        if (sesso != null) {
            whereClause += " and so_soggetto.sesso = '" + sesso + "'";
        }

        String queryReport = "";

        String codComune = bean.getCodCom();
        Integer round = bean.getRoundCom();

        boolean filtroCom = ((codComune != null && !codComune.equals("")) || (round != null));

        if (filtroCom) {
            String whDom = whereClause + " and so_soggetto.codanagreg = " + ConfigurationConstants.CODICE_DOMICILIATO;
            String whRes = whereClause + " and so_soggetto.codanagreg <> " + ConfigurationConstants.CODICE_DOMICILIATO;

            if (codComune != null && !codComune.equals("")) {
                whDom += " and codcomdom = '" + codComune + "'";
                whRes += " and codcomres = '" + codComune + "'";
            }

            if (round != null) {
                String codScr = (String) session.get("scr");
                whDom +=
                    " and codcomdom in " + "(SELECT CODCOM FROM SO_ROUNDORG where codscr='" + codScr +
                    "' and NUMROUND=" + round.toString() + " AND DTFINE IS NULL)";
                whRes +=
                    " and codcomres in " + "(SELECT CODCOM FROM SO_ROUNDORG where codscr='" + codScr +
                    "' and NUMROUND=" + round.toString() + " AND DTFINE IS NULL)";
            }

            queryReport = selClause + fromClause + whDom + " union all " + selClause + fromClause + whRes;

        } else {
            queryReport = selClause + fromClause + whereClause;
        }

        PL_Bean plBean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();

        File f = null;
        try {

            if (plBean.getTemplate() == null || plBean.getTemplate().intValue() < 0)
                throw new Exception("Nessun template selezionato per la stampa dell'elenco soggetti");

            Print_AppModuleImpl am =
                (Print_AppModuleImpl) BindingContext.getCurrent().findDataControl("Print_AppModuleDataControl").getDataProvider();
            ViewObject templates = am.findViewObject("Print_SoTemplateElenchiSoggView1");
            Row[] result = templates.getFilteredRows("Codtempl", plBean.getTemplate());
            if (result.length == 0)
                throw new Exception("Template selezionato non trovato");
            String fileName = (String) result[0].getAttribute("Nomefile");
            if (fileName.indexOf(".") >= 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

            Lettera l =
                new Lettera(result[0], "Print_SoTemplateElenchiSoggView1", "Filexml", "Compiled", fileName,
                            plBean.getExport_type());
            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");
            String inClause = queryReport;

            if (inClause == null || inClause.trim().length() == 0)
                inClause = "''";

            l.setParametersMap(ulss, tpscr, am.findViewObject("A_SoAziendaView1"),
                               am.findViewObject("A_SoCnfPartemplateView1"));
            l.addParameter("inParams", inClause);
            l.addParameter("note", null);

            f = l.createLetter(am.getDBConnection());

            //download del file
            FileInputStream fis;
            byte[] b;
            try {
                fis = new FileInputStream(f);

                int n;
                while ((n = fis.available()) > 0) {
                    b = new byte[n];
                    int res = fis.read(b);
                    outputStream.write(b, 0, b.length);
                    if (res == -1)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream.flush();
            outputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Impossibile eseguire la stampa richiesta: " + ex.getMessage());
            //this.handleException("Impossibile eseguire la stampa richiesta: " + ex.getMessage());
        } finally {
            if (f != null)
                f.delete();
        }

    }
}
