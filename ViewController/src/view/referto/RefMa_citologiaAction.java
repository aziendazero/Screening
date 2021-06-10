package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefMa_AppModule;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.util.Utility;

public class RefMa_citologiaAction extends RefMa_DataForwardAction {
    private RichForm citologiaForm;

    protected void afterUpdateModel()
      {


      }
      
       protected void updateDB() 
      {     
          
      }
      
      protected void loadFromDB()
      {
      
      }

      protected void beforeSavingReferto() throws Exception {

        super.beforeSavingReferto();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        ViewObject ref = refVoIter.getViewObject();
        Row r = ref.getCurrentRow();

        //se un esame non e' eseguito non puo' avere i dati
        String[] lato = { "Dx", "Sx" };
        for (int i = 0; i < 2; i++) {
            //se non c'e' altro motivo, cancello l'elevtuale testo
            if (!ConfigurationConstants.CODICE_NON_EXEC_ALTRO.equals(r.getAttribute("Citologia" + lato[i])))
                if (r.getAttribute("C" + lato[i] + "Altro") != null &&
                    ((String) r.getAttribute("C" + lato[i] + "Altro")).trim().length() > 0)
                    r.setAttribute("C" + lato[i] + "Altro", null);

            if (r.getAttribute("Citologia" + lato[i]) != null &&
                ((Integer) r.getAttribute("Citologia" + lato[i])).intValue() <=
                ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue()) {
                if (r.getAttribute("C" + lato[i] + "Data") != null)
                    r.setAttribute("C" + lato[i] + "Data", null);

                if (r.getAttribute("C" + lato[i] + "Medico") != null)
                    r.setAttribute("C" + lato[i] + "Medico", null);

                if (r.getAttribute("C" + lato[i] + "Localizz") != null)
                    r.setAttribute("C" + lato[i] + "Localizz", null);

                if (r.getAttribute("C" + lato[i] + "NoteEs") != null &&
                    ((String) r.getAttribute("C" + lato[i] + "NoteEs")).trim().length() > 0)
                    r.setAttribute("C" + lato[i] + "NoteEs", null);

                if (r.getAttribute("C" + lato[i] + "Esito") != null)
                    r.setAttribute("C" + lato[i] + "Esito", null);

                if (r.getAttribute("C" + lato[i] + "Dtref") != null)
                    r.setAttribute("C" + lato[i] + "Dtref", null);

                if (r.getAttribute("C" + lato[i] + "Calcificazioni") != null &&
                    !ConfigurationConstants.DB_FALSE.equals(r.getAttribute("C" + lato[i] + "Calcificazioni")))
                    r.setAttribute("C" + lato[i] + "Calcificazioni", ConfigurationConstants.DB_FALSE);

                if (r.getAttribute("C" + lato[i] + "Patologo") != null)
                    r.setAttribute("C" + lato[i] + "Patologo", null);

                /*20080714 MOD: aggiunta secondo patologo*/
                if (r.getAttribute("C" + lato[i] + "Patologo2") != null)
                    r.setAttribute("C" + lato[i] + "Patologo2", null);
                /*20080714 FINE MOD*/

                if (r.getAttribute("C" + lato[i] + "Note") != null &&
                    ((String) r.getAttribute("C" + lato[i] + "Note")).trim().length() > 0)
                    r.setAttribute("C" + lato[i] + "NoteEs", null);
            }
        }
    }

    public void setCitologiaForm(RichForm citologiaForm) {
        this.citologiaForm = citologiaForm;
    }

    public RichForm getCitologiaForm() {
        if (citologiaForm==null){
            //filtro i dati
            this.filter();
        }
        return citologiaForm;
    }
    
    private void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        ViewObject ref = refVoIter.getViewObject();
        Row r = ref.getCurrentRow();
        if (r == null)
            return;
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String ulss = (String) session.get("ulss");
        String tpscr = (String) session.get("scr");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date) r.getAttribute("Dtinserimento");
        String data = null;
        if (d != null)
            data = DateUtils.dateToString(d.getValue());

        //medico PATOLOGO
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView1Iterator");
        ViewObject vo = voIter.getViewObject();
        ViewObjectFilters.filterOpMedici(vo, data, null, ConfigurationConstants.CODICE_PATOLOGO, ulss, tpscr);

        //medico prelevatore
        voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView4Iterator");
        vo = voIter.getViewObject();
        ViewObjectFilters.filterOpMedici(vo, data, null,
                                         new Integer[] { ConfigurationConstants.CODICE_RADIOLOGO,
                                                        ConfigurationConstants.CODICE_SENOLOGO,
                                                        ConfigurationConstants.CODICE_CHIRURGO,
                                                        ConfigurationConstants.CODICE_PATOLOGO }, ulss, tpscr);

        //esecuzione dell'esame
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livMXEXECView1Iterator");
        vo = voIter.getViewObject();
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //tecnica di localizzzione
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livMXLOCLView1Iterator");
        vo = voIter.getViewObject();
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //esito
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livMXCESTView1Iterator");
        vo = voIter.getViewObject();
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

    }

    public void onChangeCitologia(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        if(valueChangeEvent.getComponent().getId().contains("Sx"))
            Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:CSxLocalizz', 'pt1:CSxEsito'])");
        else
            Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:CDxLocalizz', 'pt1:CDxEsito'])");
    }
}
