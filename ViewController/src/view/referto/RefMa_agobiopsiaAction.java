package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.commons.ConfigurationConstants;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.util.Utility;

public class RefMa_agobiopsiaAction extends RefMa_DataForwardAction {
    private RichForm agobiopsiaForm;

    public RefMa_agobiopsiaAction() {
        super();
    }

    @Override
    protected void afterUpdateModel() {
        // TODO Implement this method
    }

    @Override
    protected void updateDB() throws Exception {
        // TODO Implement this method
    }

    @Override
    protected void loadFromDB() {
        // TODO Implement this method
    }

    public void setAgobiopsiaForm(RichForm agobiopsiaForm) {
        this.agobiopsiaForm = agobiopsiaForm;
    }

    public RichForm getAgobiopsiaForm() {
        if (agobiopsiaForm == null){
            this.filter();
        }
        return agobiopsiaForm;
    }
    
    private void filter() {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        Row r = refVoIter.getCurrentRow();
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
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livMXAESTView1Iterator");
        vo = voIter.getViewObject();
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

        //tecnica di esecuzione
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livMXTEXEView1Iterator");
        vo = voIter.getViewObject();
        ViewObjectFilters.filterCnfReferti(vo, ulss, tpscr, data);

    }
    
    protected void beforeSavingReferto() throws Exception
      {
        
        super.beforeSavingReferto();

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        Row r = refVoIter.getCurrentRow();

        //se un esame non e' eseguito non puo' avere i dati
        String[] lato = { "Dx", "Sx" };
        for (int i = 0; i < 2; i++) {
            //se non c'e' altro motivo, cancello l'elevtuale testo
            if (!ConfigurationConstants.CODICE_NON_EXEC_ALTRO.equals(r.getAttribute("Agob" + lato[i])))
                if (r.getAttribute("A" + lato[i] + "Altro") != null &&
                    ((String) r.getAttribute("A" + lato[i] + "Altro")).trim().length() > 0)
                    r.setAttribute("A" + lato[i] + "Altro", null);

            if (r.getAttribute("Agob" + lato[i]) != null &&
                ((Integer) r.getAttribute("Agob" + lato[i])).intValue() <=
                ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue()) {
                if (r.getAttribute("A" + lato[i] + "Data") != null)
                    r.setAttribute("A" + lato[i] + "Data", null);

                if (r.getAttribute("A" + lato[i] + "Prelevatore") != null)
                    r.setAttribute("A" + lato[i] + "Prelevatore", null);

                if (r.getAttribute("A" + lato[i] + "Localizz") != null)
                    r.setAttribute("A" + lato[i] + "Localizz", null);

                if (r.getAttribute("A" + lato[i] + "Esecuzione") != null)
                    r.setAttribute("A" + lato[i] + "Esecuzione", null);

                if (r.getAttribute("A" + lato[i] + "ExecAltro") != null)
                    r.setAttribute("A" + lato[i] + "ExecAltro", null);

                if (r.getAttribute("A" + lato[i] + "NoteEs") != null &&
                    ((String) r.getAttribute("A" + lato[i] + "NoteEs")).trim().length() > 0)
                    r.setAttribute("A" + lato[i] + "NoteEs", null);

                if (r.getAttribute("A" + lato[i] + "Esito") != null)
                    r.setAttribute("A" + lato[i] + "Esito", null);

                if (r.getAttribute("A" + lato[i] + "Dtref") != null)
                    r.setAttribute("A" + lato[i] + "Dtref", null);

                if (r.getAttribute("A" + lato[i] + "Calcificazioni") != null &&
                    !ConfigurationConstants.DB_FALSE.equals(r.getAttribute("A" + lato[i] + "Calcificazioni")))
                    r.setAttribute("A" + lato[i] + "Calcificazioni", ConfigurationConstants.DB_FALSE);

                if (r.getAttribute("A" + lato[i] + "Lettore") != null)
                    r.setAttribute("A" + lato[i] + "Lettore", null);

                /*20080714 MOD: aggiunta secondo patologo*/
                if (r.getAttribute("A" + lato[i] + "Lettore2") != null)
                    r.setAttribute("A" + lato[i] + "Lettore2", null);
                /*20080714 FINE MOD*/

                if (r.getAttribute("A" + lato[i] + "Note") != null &&
                    ((String) r.getAttribute("A" + lato[i] + "Note")).trim().length() > 0)
                    r.setAttribute("A" + lato[i] + "NoteEs", null);
            }

            else if (!ConfigurationConstants.CODICE_ALTRA_TECNICA.equals(r.getAttribute("A" + lato[i] + "Esecuzione")))
                if (r.getAttribute("A" + lato[i] + "ExecAltro") != null &&
                    ((String) r.getAttribute("A" + lato[i] + "ExecAltro")).trim().length() > 0)
                    r.setAttribute("A" + lato[i] + "ExecAltro", null);
        }
      }

    public void onChangeAgob(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        if(valueChangeEvent.getComponent().getId().contains("Sx"))
            Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:ASxLocalizz', 'pt1:ASxEsecuzione', 'pt1:ASxEsito'])");
        else
            Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:ADxLocalizz', 'pt1:ADxEsecuzione', 'pt1:ADxEsito'])");
    }
}
