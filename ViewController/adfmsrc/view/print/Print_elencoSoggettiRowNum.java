package view.print;

import insiel.dataHandling.DateUtils;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import javax.servlet.http.HttpSession;

import model.Print_AppModuleImpl;

import model.common.Print_AppModule;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;
import model.commons.Lettera;

import model.datacontrol.Cnf_selectionBean;

import model.datacontrol.PL_Bean;

import model.filters.ViewObjectFilters;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;

import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.commons.action.Parent_DataForwardAction;

import view.statistiche.ComputeStats;
import view.statistiche.Stats_helper;

public class Print_elencoSoggettiRowNum extends Parent_DataForwardAction {
    private RichForm onLoad;
    private RichSelectOneChoice template;

    public Print_elencoSoggettiRowNum() {
    }

    Boolean filterVo;

    @Override
    protected void setAppModule() {
        // TODO Implement this method
    }

    public void stampa(FacesContext facesContext, OutputStream outputStream) throws Exception {   
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Print_SoTemplateElenchiSoggView1Iterator");

        ViewObject templates = voIter.getViewObject();

        Cnf_selectionBean c_bean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();

        File f = null;
        try {

            if (bean.getTemplate() == null || bean.getTemplate().intValue() < 0)
                throw new Exception("Nessun template selezionato per la stampa dell'elenco soggetti");

            Row[] result = templates.getFilteredRows("Codtempl", bean.getTemplate());
            if (result.length == 0)
                throw new Exception("Template selezionato non trovato");
            String fileName = (String) result[0].getAttribute("Nomefile");
            if (fileName.indexOf(".") >= 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

            Lettera l =
                new Lettera(result[0], "Print_SoTemplateElenchiSoggView1", "Filexml", "Compiled", fileName,
                            bean.getExport_type());
            
            Map session = ADFContext.getCurrent().getSessionScope();
            String ulss = (String) session.get("ulss");
            String tpscr = (String) session.get("scr");
            String inClause = c_bean.getInClause();
            
            if (inClause == null || inClause.trim().length() == 0)
                inClause = "''";
            
            Boolean stampaRowNum = ADFContext.getCurrent().getViewScope().get("stampaRowNum")!=null?(Boolean)ADFContext.getCurrent().getViewScope().get("stampaRowNum"):false;
            Integer rowNum = c_bean.getRowNum();

            if (stampaRowNum && rowNum!=null && rowNum>0)  
                inClause = "SELECT * FROM (" + inClause + ") WHERE rownum <= " + rowNum;
            
            Print_AppModuleImpl am = (Print_AppModuleImpl) templates.getApplicationModule();
            l.setParametersMap(ulss, tpscr, am.findViewObject("A_SoAziendaView1"),
                               am.findViewObject("A_SoCnfPartemplateView1"));
            l.addParameter("inParams", inClause);
            l.addParameter("note", c_bean.getNote());


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
            //this.handleException("Impossibile eseguire la stampa richiesta: " + ex.getMessage());
            //throw new Exception("Impossibile eseguire la stampa richiesta: " + ex.getMessage());
            this.saveMessages("Impossibile eseguire la stampa richiesta: " + ex.getMessage());

        } finally {

            if (f != null)
                f.delete();
        }

    }

    public void setFilterVo(Boolean filterVo) {
        this.filterVo = filterVo;
    }

    public Boolean getFilterVo() {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Print_SoTemplateElenchiSoggView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Map session = ADFContext.getCurrent().getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        String data = DateUtils.dateToString(new Date());
        ViewObjectFilters.filterTemplates(vo, ulss, tpscr, data,
                                          "IDTPLETTERA=" + ConfigurationConstants.CODICE_ELENCHI_SOGG_ORD);


        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        if (vo.first() != null){
            if (bean.getTemplate() == null) 
                bean.setTemplate((Integer) vo.first().getAttribute("Codtempl"));
            
            if (bean.getNome_file() == null)
                bean.setNome_file(vo.first().getAttribute("Nomefile").toString());
        }

        prepareInClause();

        return true;
    }

    private void prepareInClause() {
        String s = (String) ADFContext.getCurrent().getPageFlowScope().get("todo");
        if (s == null)
            return;

        //situazione in cui devo impostare la in clause
        Cnf_selectionBean c_bean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        try {


            //controllo l'autorizzazione
            AccessManager.checkPermission("SOStatULSS");

            //proveniendo dai filtri dinamici
            if ("dynamic".equals(s)) {
                String[] out = ComputeStats.createDynamicQuery();
                c_bean.setInClause(out[0]);
                // System.out.println(c_bean.getInClause());
                c_bean.setNote(out[1]);
            } else //stamp aelenco dai filtri standard
            {
                Stats_helper.onCall_proc();
                // System.out.println(c_bean.getInClause());
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.handleException("Impossibile procedere con la stampa richiesta: " + e.getMessage());
        } finally {
            ADFContext.getCurrent().getPageFlowScope().remove("todo");
            ADFContext.getCurrent().getPageFlowScope().remove("col_n");
        }

    }

    public void onCreaFileInviti(ActionEvent actionEvent) {
        Long conteggioSoggetti = 0L;
        
        Cnf_selectionBean c_bean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        
        PL_Bean bean = (PL_Bean) BindingContext.getCurrent().findDataControl("PL_BeanDataControl").getDataProvider();
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Round_SoPianificazioneInvitiView1Iterator");
        DCIteratorBinding voIterTemplates = bindings.findIteratorBinding("Print_SoTemplateElenchiSoggView1Iterator");
        
        if (voIter!=null){
            ViewObject voConteggio = voIter.getViewObject();
            
            if (voConteggio.getEstimatedRowCount()==1){
                Row r = voConteggio.first();
                conteggioSoggetti = (Long)r.getAttribute("Conteggio");
            }
        }
        
        bean.setExport_type(1);
        
        if (conteggioSoggetti>0)
            c_bean.setRowNum(conteggioSoggetti.intValue());
    }

    public void setOnLoad(RichForm onLoad) {
        this.onLoad = onLoad;
        ADFContext.getCurrent().getViewScope().put("stampaRowNum", true);
    }

    public RichForm getOnLoad() {
        return onLoad;
    }

}

