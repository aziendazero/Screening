package view.referto;

import insiel.utilities.dataformats.DateUtils;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import model.common.RefMa_AppModule;
import model.common.Ref_SoInterventomammoViewRow;

import model.commons.ConfigurationConstants;

import model.commons.ViewHelper;

import model.filters.ViewObjectFilters;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichForm;

import oracle.adf.view.rich.render.ClientEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;

import view.util.Utility;

public class RefMa_istologiaAction extends RefMa_InterventiDataForwardAction {

    private RichForm istologiaForm;

    public void setIstologiaForm(RichForm istologiaForm) {
        this.istologiaForm = istologiaForm;
    }

    public RichForm getIstologiaForm() {
        if (istologiaForm == null){
            //filtro i dati
               this.filter();
        }
        return istologiaForm;
    }
    
    private void filter()
      {
        //mantengo coerenza tra il centro di refertazione del referto ed i medici visualizzati,
        //in base anche alla data del referto (per default quella odierna)
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding refVoIter = bindings.findIteratorBinding("Ref_SoRefertomammo2livView1Iterator");
        Row r=refVoIter.getCurrentRow();
        if(r==null)
          return;
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        oracle.jbo.domain.Date d = (oracle.jbo.domain.Date)r.getAttribute("Dtinserimento");
        String data=null;
        if(d!=null)
           data=DateUtils.dateToString(d.getValue());
        
        //medico patologo 
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoOpmedicoView1Iterator");
        ViewObjectFilters.filterOpMedici(voIter.getViewObject(),data,null,ConfigurationConstants.CODICE_PATOLOGO,ulss, tpscr);
        
        //diagnosi istologica
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livISTDIAView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
        
        //metastasi
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livISTOLMView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
        
        //stadio N
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livISTOPNView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
        
        //stadio T
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livISTOPTView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
        
        //Diametro
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livISTODMView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
        
        //stadiazione
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livSTADIOView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
        
        //residui
        voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livISTORXView1Iterator");
        ViewObjectFilters.filterCnfReferti(voIter.getViewObject(),ulss,tpscr,data);
             
      }

    public void updateStadiazione(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        Ref_SoInterventomammoViewRow r = (Ref_SoInterventomammoViewRow) voIter.getCurrentRow();
        if (r == null)
            return;
        //aggiornato il 02/02/2009 per inserimento N1mic
        //imposta i dati di stadiazione e diametro in base alla selezione di pt, pn e pm
        try {
            /* pM     pN          pT          Stadio
             * M1     --          --            IV
             * M0     N0          T0            0
             *                    T1(micr,a,b,c)I
             *                    T2            IIA
             *                    T3            IIB
             *                    T4(a,b,c,d)   IIIB
             *        N1(mic,a,b,c)   T0,T1(micr,a,b,c) IIA
             *                    T2            IIB
             *                    T3            IIIA
             *        N2(a,b)     T0,..,T3      IIIA
             *                    T4(a,b,c,d)   IIIB
             *        N3(a,b,c)   --            IIIC
             *
             * */


            //se M1-->Stadio IV
            if (ConfigurationConstants.CODICE_CO_ISTOM_M1.equals(r.getStadioM()))
                r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IV_MA);
            //se M0:
            else if (ConfigurationConstants.CODICE_CO_ISTOM_M0.equals(r.getStadioM())) {
                //se pM0 e pN0
                if (ConfigurationConstants.CODICE_CO_ISTOPN_N0.equals(r.getStadioPn())) {
                    //se pT0 --> stadiazione 0
                    if (ConfigurationConstants.CODICE_CO_ISTOPT_T0.equals(r.getStadioPt()))
                        r.setStadiazione(ConfigurationConstants.DB_FALSE.intValue());

                    //se pT1, pT1micr,pT1a,pT1b ,pT1c --> stadiazione I
                    else if (r.getStadioPt() != null &&
                             r.getStadioPt().intValue() >= ConfigurationConstants.CODICE_CO_ISTOPT_T1.intValue() &&
                             r.getStadioPt().intValue() <= ConfigurationConstants.CODICE_MA_ISTOPT_T1c.intValue())
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_I_MA);

                    //se pT2 --> stadiazione IIA
                    else if (ConfigurationConstants.CODICE_CO_ISTOPT_T2.equals(r.getStadioPt()))
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIA_MA);

                    //se pT3 --> stadiazione IIB
                    else if (ConfigurationConstants.CODICE_CO_ISTOPT_T3.equals(r.getStadioPt()))
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIB_MA);

                    //se pT4,pT4a, pT4b, pT4c, pT4d --> stadiazione IIIB
                    else if (r.getStadioPt() != null &&
                             ConfigurationConstants.CODICE_CO_ISTOPT_T4.intValue() <= r.getStadioPt().intValue())
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIIB_MA);
                }
                //se pM0 e pN1 (anche mic, a, b, c)
                else if (r.getStadioPn() != null &&
                         ((ConfigurationConstants.CODICE_CO_ISTOPN_N1.intValue() <= r.getStadioPn().intValue() &&
                           ConfigurationConstants.CODICE_MA_ISTOPN_N1c.intValue() >= r.getStadioPn().intValue()) ||
                          ConfigurationConstants.CODICE_MA_ISTOPN_N1mic.intValue() == r.getStadioPn().intValue())) {
                    //se pT0, pT1 o pT1micr,pT1a,pT1b,pT1c --> stadiazione IIA
                    if (ConfigurationConstants.CODICE_CO_ISTOPT_T0.equals(r.getStadioPt()) ||
                        (r.getStadioPt() != null &&
                         r.getStadioPt().intValue() >= ConfigurationConstants.CODICE_CO_ISTOPT_T1.intValue() &&
                         r.getStadioPt().intValue() <= ConfigurationConstants.CODICE_MA_ISTOPT_T1c.intValue()))
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIA_MA);


                    //se pT2 --> stadiazione IIB
                    else if (ConfigurationConstants.CODICE_CO_ISTOPT_T2.equals(r.getStadioPt()))
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIB_MA);

                    //se pT3 --> stadiazione IIIA
                    else if (ConfigurationConstants.CODICE_CO_ISTOPT_T3.equals(r.getStadioPt()))
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIIA_MA);

                    //se pT4(a,b,c,d) --> stadiazione IIIB
                    else if (r.getStadioPt() != null &&
                             ConfigurationConstants.CODICE_CO_ISTOPT_T4.intValue() <= r.getStadioPt().intValue())
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIIB_MA);
                }

                //se pM0 e pN2,pN2a,pN2b
                else if (r.getStadioPn() != null &&
                         ConfigurationConstants.CODICE_MA_ISTOPN_N2.intValue() <= r.getStadioPn().intValue() &&
                         ConfigurationConstants.CODICE_MA_ISTOPN_N2b.intValue() >=
                         r.getStadioPn().intValue()) {
                    //da pT0 a pT3 --> stadiazione IIIA
                    if (r.getStadioPt() != null &&
                        ConfigurationConstants.CODICE_CO_ISTOPT_T0.intValue() <= r.getStadioPt().intValue() &&
                        ConfigurationConstants.CODICE_CO_ISTOPT_T3.intValue() >= r.getStadioPt().intValue())
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIIA_MA);

                    //se pT4(a,b,c,d) --> stadiazione IIIB
                    else if (r.getStadioPt() != null &&
                             ConfigurationConstants.CODICE_CO_ISTOPT_T4.intValue() <= r.getStadioPt().intValue())
                        r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIIB_MA);

                }
                //se pM0 e pN3,pN3a,pN3b,pN3c-->stadiazione IIIC
                else if (r.getStadioPn() != null &&
                         ConfigurationConstants.CODICE_CO_ISTOPN_N3.intValue() <= r.getStadioPn().intValue()) {
                    r.setStadiazione(ConfigurationConstants.CODICE_STADIO_IIIC_MA);
                }

            } //se M0

            //impostazione automatica del diametro (tra pTis e pT3 ha lo stesso valore del pT)
            if (r.getStadioPt() != null &&
                ConfigurationConstants.CODICE_CO_ISTOPT_TIS.intValue() <= r.getStadioPt().intValue() &&
                ConfigurationConstants.CODICE_CO_ISTOPT_T3.intValue() >= r.getStadioPt().intValue())
                r.setDiametro(r.getStadioPt());

            else if (r.getStadioPt() != null &&
                     ConfigurationConstants.CODICE_CO_ISTOPT_T3.intValue() < r.getStadioPt().intValue())
                r.setDiametro(ConfigurationConstants.CODICE_CO_ISTOPT_T3);

        } catch (Exception ex) {
            this.handleException("Impossibile impostare automaticamente diametro e/o stadio: " + ex.getMessage(),
                                 null);
        }
    }
    
    protected void beforeSavingReferto() throws Exception {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoInterventomammoView1Iterator");
        //eseguo il controllo per tutti gli interventi di quel referto
        RowSetIterator iter = ViewHelper.getRowSetIterator(voIter.getViewObject(), "iter");
        Ref_SoInterventomammoViewRow r = null;
        try {

            while (iter.hasNext()) {
                r = (Ref_SoInterventomammoViewRow) iter.next();
                if (r == null)
                    continue;

                if (r != null && r.getAttribute("Completo") != null &&
                    ConfigurationConstants.DB_TRUE.equals((Integer) r.getAttribute("Completo")))
                    throw new Exception("Intervento/i gia' chiuso/i, le modifiche sono state annullate");

                if (ConfigurationConstants.DB_FALSE.equals(r.getIstologia())) {
                    if (r.getDtistologia() != null)
                        r.setAttribute("Dtistologia", null);

                    if (r.getNoteIstologia() != null && r.getNoteIstologia().length() > 0)
                        r.setAttribute("NoteIstologia", null);

                    if (r.getIdpatologo1() != null)
                        r.setAttribute("Idpatologo1", null);

                    if (r.getIdpatologo2() != null)
                        r.setAttribute("Idpatologo2", null);

                    if (!ConfigurationConstants.DB_FALSE.equals(r.getDiagnosi()))
                        r.setAttribute("Diagnosi", ConfigurationConstants.DB_FALSE);

                    if (!ConfigurationConstants.DB_FALSE.equals(r.getStadioM()))
                        r.setAttribute("StadioM", ConfigurationConstants.DB_FALSE);

                    if (!ConfigurationConstants.DB_FALSE.equals(r.getStadioPn()))
                        r.setAttribute("StadioPn", ConfigurationConstants.DB_FALSE);

                    if (!ConfigurationConstants.DB_FALSE.equals(r.getStadioPt()))
                        r.setAttribute("StadioPt", ConfigurationConstants.DB_FALSE);

                    if (r.getGrading() != null)
                        r.setAttribute("Grading", null);

                    if (!ConfigurationConstants.DB_FALSE.equals(r.getDiametro()))
                        r.setAttribute("Diametro", ConfigurationConstants.DB_FALSE);

                    if (!ConfigurationConstants.DB_FALSE.equals(r.getStadiazione()))
                        r.setAttribute("Stadiazione", ConfigurationConstants.DB_FALSE);

                    if (!ConfigurationConstants.DB_FALSE.equals(r.getResidui()))
                        r.setAttribute("Residui", ConfigurationConstants.DB_FALSE);

                    if (r.getNLinfonodi() != null)
                        r.setAttribute("NLinfonodi", null);

                    if (r.getNPositivi() != null)
                        r.setAttribute("NPositivi", null);
                }

                //se la diagnosi istologica non e' altro, il testo associato
                //non puo' essere valorizato
                if (!ConfigurationConstants.CODICE_DIAGNOSI_ALTRO.equals(r.getDiagnosi()) &&
                    r.getAltraDiagnosi() != null && r.getAltraDiagnosi().trim().length() > 0)
                    r.setAltraDiagnosi(null);

            }
            
            iter.closeRowSetIterator();

        } catch (Exception ex) {
            throw ex;
        } finally {
            iter.closeRowSetIterator();
        }

    }

    public void onChgIstologia(ValueChangeEvent valueChangeEvent) {
        valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        Utility.addScriptOnPartialRequest("initOptionsFromServer(['pt1:soc8', 'pt1:soc9'])");
    }
}
