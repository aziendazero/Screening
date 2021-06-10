package view.conf;

import insiel.dataHandling.DateUtils;

import java.util.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;

import javax.servlet.http.HttpSession;

import model.common.A_AppModule;
import model.common.Round_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import view.backing.ParentBackingBean;

import view.commons.ViewHelper;

import view.invito.GestoreRound;

public class Cnf_comuneBean extends ParentBackingBean {
    public Cnf_comuneBean() {
    }

    public void setSecondoLivello(ValueChangeEvent valueChangeEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject vo = bindings.findIteratorBinding("Cnf_SoCnfComuneView2Iterator").getViewObject();
        ViewObject vo2 = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator").getViewObject();

        try {
            Row r = vo.getCurrentRow();

            if (r == null || valueChangeEvent.getNewValue() == null)
                return;

            //lavoro su un set di dati che e' gia' tutto in memoria perche' in menu', quindi il getFilteredRows va bene
            Integer idCentro = (Integer)valueChangeEvent.getNewValue();
            Row[] result = vo2.getFilteredRows("Idcentro", idCentro);
            if (result.length <= 0)
                return;
            
            r.setAttribute("Idcentro2liv", result[0].getAttribute("Idcentro2liv"));

        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }

    public void activateRound(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Map session = ADFContext.getCurrent().getSessionScope();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Round_SoRoundAttiviView1Iterator");
        ViewObject vo = voIter.getViewObject();   
        Row r = vo.getCurrentRow();
        try {

            if (r == null)
                throw new Exception("nessun comune selezionato per l'attivazione");
            //controllo che il round precedente sia chiuso o non esista
            if (r.getAttribute("Dtinizio") != null && r.getAttribute("Dtfine") == null)
                throw new Exception("il comune ha un round attivo non ancora chiuso");
            //leggo il numero di round
            Integer round = (Integer) r.getAttribute("Numround");
            if (round == null)
                round = new Integer(0);

            DCIteratorBinding orgVoIter = bindings.findIteratorBinding("Round_SoRoundorgView3Iterator");
            ViewObject org = orgVoIter.getViewObject();
            Row newR = org.createRow();
            org.insertRow(newR);
            //impostare questi attributi
            newR.setAttribute("Codcom", r.getAttribute("Codcom"));
            newR.setAttribute("Codscr", session.get("scr"));
            newR.setAttribute("Dtinizio", DateUtils.getOracleDate(new Date()));
            newR.setAttribute("Numround", round + 1);
            newR.setAttribute("ReleaseCodeCom", r.getAttribute("ReleaseCode"));

            //journaling
            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            Boolean flag = (Boolean) session.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) session.get("user");
                String tpscr = (String) session.get("scr");
                String ulss = (String) session.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }
            
            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            Object commitResult = commitBinding.execute();


        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile attivare il round: " + ex.getMessage() );
        } finally {

            ViewHelper.queryAndRestoreCurrentRow(vo);
        }
    }

    public void closeRound(DialogEvent dialogEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        Map session = ADFContext.getCurrent().getSessionScope();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Round_SoRoundAttiviView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row r = vo.getCurrentRow();
        try {

            if (r == null)
                throw new Exception("nessun comune selezionato per la chiusura");
            //controllo che il round precedente sia chiuso
            if (r.getAttribute("Dtfine") != null || r.getAttribute("Dtinizio") == null)
                throw new Exception("questo comune non presenta round attivi da poter chiudere");


            //superati tutti i controlli, si puo' chiudere il round
            //leggo la riga editabile
            DCIteratorBinding orgVoIter = bindings.findIteratorBinding("Round_SoRoundorgView3Iterator");
            ViewObject vo1 = orgVoIter.getViewObject();
            //l'ordinamento decrescente per round rende il primo il round attivo
            Row r1 = vo1.first();
            if (r1 == null)
                throw new Exception("nessun comune selezionato per la chiusura");

            r1.setAttribute("Dtfine", DateUtils.getOracleDate(new Date()));

            //journaling
            Round_AppModule am =
                (Round_AppModule) BindingContext.getCurrent().findDataControl("Round_AppModuleDataControl").getDataProvider();
            Boolean flag = (Boolean) session.get("flagAbilJournal");
            if (flag != null && flag.booleanValue()) {
                String user = (String) session.get("user");
                String tpscr = (String) session.get("scr");
                String ulss = (String) session.get("ulss");
                am.preapareJournaling(user, ulss, tpscr);
            }
            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            Object commitResult = commitBinding.execute();

        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile chiudere il round: " + ex.getMessage() );
        } finally {
            //faccio un refresh
            ViewHelper.queryAndRestoreCurrentRow(vo);


        }
    }
    
    public boolean getCheckRoundPopulation() {
        Map session = ADFContext.getCurrent().getSessionScope();
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Round_SoRoundAttiviView1Iterator");
        ViewObject vo = voIter.getViewObject();
        Row r = vo.getCurrentRow();
        if (r == null)
            return Boolean.FALSE;
        A_AppModule am2=(A_AppModule)BindingContext.getCurrent().findDataControl("A_AppModuleDataControl").getDataProvider();
        GestoreRound gr = new GestoreRound(am2);
        try {

            if (gr.checkPopulationNEW((String) r.getAttribute("Ulss"),
                                      (String) session.get("scr"),
                                      (String) r.getAttribute("Codcom"), (Integer) r.getAttribute("Numround")))
                //ci sono ancora persone da invitare
                return Boolean.TRUE;

        } catch (Exception e) {
            e.printStackTrace();
            this.handleMessage(FacesMessage.SEVERITY_ERROR, e.getMessage());
            return Boolean.FALSE;
        }

        return Boolean.FALSE;
    }

    public void setSecondoLivelloZona(ValueChangeEvent valueChangeEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject vo = bindings.findIteratorBinding("Cnf_SoCnfDistrzonaView2Iterator").getViewObject();
        ViewObject vo2 = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator").getViewObject();

        try {
            Row r = vo.getCurrentRow();

            if (r == null || valueChangeEvent.getNewValue() == null)
                return;

            //lavoro su un set di dati che e' gia' tutto in memoria perche' in menu', quindi il getFilteredRows va bene
            Integer idCentro = (Integer)valueChangeEvent.getNewValue();
            Row[] result = vo2.getFilteredRows("Idcentro", idCentro);
            if (result.length <= 0)
                return;
            
            r.setAttribute("Idcentro2liv", result[0].getAttribute("Idcentro2liv"));

        } catch (Exception ex) {
            this.handleMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }
}
