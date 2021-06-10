package view.agenda;

import insiel.dataHandling.DateUtils;

import java.util.Calendar;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.AccPf_AppModule;
import model.common.Acc_AppModule;
import model.common.AccCo_AppModule;
import model.common.AccMa_AppModule;
import model.common.Agenda_AppModule;
import model.common.Parent_AppModule;
import model.common.Sogg_AppModule;
import model.commons.AgendaUtils;
import model.commons.ConfigurationConstants;
import model.commons.ParametriSistema;
import model.commons.SoggUtils;
import model.commons.ViewHelper;
import model.datacontrol.Agenda_Servizio;
import model.inviti.InvitoUtils;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.output.RichPanelCollection;
import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import java.util.Date;
import java.util.Locale;

import model.Cnf_AppModuleImpl;
import model.agenda.Agenda_SoAppuntamentoViewRowImpl;
import model.agenda.common.Agenda_SoAppuntamentoViewRow;
import model.datacontrol.Cnf_selectionBean;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.jbo.ApplicationModule;
import oracle.jbo.Transaction;
import oracle.jbo.domain.Timestamp;
import oracle.jbo.server.EntityImpl;

import view.commons.action.Parent_DataForwardAction;

@SuppressWarnings({ "deprecation", "unchecked" })
public class Ag_giornAction extends Parent_DataForwardAction {
    private RichPanelCollection panelOrari;

    public Ag_giornAction() {
    }

    public void onCerca(ValueChangeEvent valueChangeEvent) {
        if(valueChangeEvent!=null)
            valueChangeEvent.getComponent().processUpdates(FacesContext.getCurrentInstance());
        
        boolean saveOK = this.save();
        Map sess = ADFContext.getCurrent().getSessionScope();
        if (saveOK) {
            AgendaUtils.querySlot();
        } else {
            // ripristino centro precedentemente impostato
            Integer formerCt = (Integer) sess.get("formerCt");
            Agenda_Servizio bean =
                (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
            bean.setIdCentro(formerCt);
        }
        Agenda_AppModule am = (Agenda_AppModule) BindingContext.getCurrent().
          findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        ViewObject voSlot = am.findViewObject("Agenda_SoAppuntamentoView1");
        
        if(voSlot.getEstimatedRowCount()>0)
            voSlot.setCurrentRow(voSlot.first());
    }

    public void onPrev(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            Map session = ADFContext.getCurrent().getSessionScope();
            Date dtAgenda = session.get("dtAgenda")!=null ? (Date) session.get("dtAgenda") : null;
            
            if(dtAgenda!=null){
                java.util.Date dtAg = dtAgenda;
                java.util.Date newDate = AgendaUtils.addDaysToDate(dtAg, -1);
                session.put("dtAgenda", newDate);
                AgendaUtils.querySlot();
            }
        }
    }

    public void onNext(ActionEvent actionEvent) {
        boolean saveOK = this.save();
        if (saveOK) {
            Map session = ADFContext.getCurrent().getSessionScope();
            Date dtAgenda = (Date) session.get("dtAgenda")!=null ? (Date) session.get("dtAgenda") : null;
            
            if(dtAgenda!=null){
                java.util.Date dtAg = dtAgenda;
                java.util.Date newDate = AgendaUtils.addDaysToDate(dtAg, 1);
    
                session.put("dtAgenda", newDate);
                AgendaUtils.querySlot();
            }
        }
    }

    public void onPgl(ActionEvent actionEvent) {
        Map session = ADFContext.getCurrent().getSessionScope();
        Date dtAgenda = (Date) session.get("dtAgenda")!=null ? (Date) session.get("dtAgenda") : null;
            
        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
        Integer idCt = bean.getIdCentro();

        if (idCt == null) {
            String msg = "Per poter calcolare il primo giorno libero e' necessario impostare il centro.";
            this.handleException(msg, null);
            return;
        }

        boolean saveOK = this.save();
        if (saveOK) {
            oracle.jbo.domain.Date dtAge = null;
            if (dtAgenda == null || dtAgenda.equals("")) {
                dtAge = DateUtils.getOracleDateNow();
            } else {
                java.util.Date cleanDate = AgendaUtils.getDateWithoutTime(dtAgenda);
                dtAge = DateUtils.getOracleDate(cleanDate);
            }

            Integer idCentro = new Integer(idCt.intValue());
            Agenda_AppModule am =
                (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();

            String scr = (String) session.get("scr");
            String ulss = (String) session.get("ulss");
            oracle.jbo.domain.Date dtMin = AgendaUtils.primoGLib(am, idCentro, dtAge, scr, ulss);

            if (dtMin == null) {
                String msg = "Non sono stati trovati giorni liberi per i parametri impostati.";
                this.handleException(msg, null);
                return;
            }

            Date primoGG = AgendaUtils.getDateWithoutTime(dtMin.dateValue());
            session.put("dtAgenda", primoGG);
            AgendaUtils.querySlot();
        }
    }


    public String onCanc() {
        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        ViewObject voSlot = am.findViewObject("Agenda_SoAppuntamentoView1");
        Agenda_SoAppuntamentoViewRow cSlot = (Agenda_SoAppuntamentoViewRow) voSlot.getCurrentRow();

        // Tolgo il riferimento all'appuntamento da cancellare negli inviti
        Integer idApp = cSlot.getIdapp();
        String istrUpd = "update SO_INVITO set IDAPP = null where IDAPP = " + idApp.intValue();
        am.getTransaction().executeCommand(istrUpd);

        // Elimino lo slot
        // Devo rieseguire la query perche', avendo cancellato dei figli, l'appuntamento risulta modificato
        boolean isNew = ((Agenda_SoAppuntamentoViewRowImpl)cSlot).getEntity(0).getEntityState() == EntityImpl.STATUS_NEW;
        
        if(isNew){
            cSlot.remove();
            ADFContext.getCurrent().getSessionScope().put("creatoNuovo", Boolean.FALSE);
        }else {
            Key cSlotKey = cSlot.getKey();
            voSlot.executeQuery();
            cSlot = (Agenda_SoAppuntamentoViewRow) voSlot.getRow(cSlotKey);
            
            if(cSlot!=null)
                cSlot.remove();
        }
        
        //02052013 GAION: aggiunto commit per evitare problemi con le fk
        am.getTransaction().commit();
        //fine 02052013
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelOrari);
        
        return null;
    }

    public void onNuovo(ActionEvent actionEvent) {
        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();

        Integer idCt = bean.getIdCentro();
        if (idCt == null) {
            String msgct = "Per poter inserire un nuovo orario e' necessario impostare il centro.";
            this.handleException(msgct, null);
            return;
        }
        int idCentro = idCt.intValue();

        Map session = ADFContext.getCurrent().getSessionScope();
        Date dtAgenda = (Date) session.get("dtAgenda");
        if (dtAgenda == null) {
            String msgdt = "Per poter inserire un nuovo orario e' necessario impostare la data.";
            this.handleException(msgdt, null);
            return;
        }
        java.util.Date jDate = AgendaUtils.getDateWithoutTime(dtAgenda);
        oracle.jbo.domain.Date oDate = DateUtils.getOracleDate(jDate);

        String tpscr = (String) session.get("scr");

        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        ViewObject voSlot = am.findViewObject("Agenda_SoAppuntamentoView1");
        Agenda_SoAppuntamentoViewRow nSlot = (Agenda_SoAppuntamentoViewRow) voSlot.createRow();

        Integer idApp = am.getNextIdAppuntamento();

        nSlot.setIdapp(idApp);
        nSlot.setIdcentro(idCentro);
        nSlot.setDtapp(oDate);
        nSlot.setTpsrc(tpscr);
        nSlot.setDispslot(new Integer(1));
        nSlot.setRigaSalvata("N");
        nSlot.setNascosto(Boolean.FALSE);

        voSlot.insertRow(nSlot);
        voSlot.setCurrentRow(nSlot);
        session.put("creatoNuovo", Boolean.TRUE);
    }
    
    public String onConfigurazione() {
        Agenda_Servizio bean =
            (Agenda_Servizio) BindingContext.getCurrent().findDataControl("Agenda_ServizioDataControl").getDataProvider();
        Integer idCt = bean.getIdCentro();
        
        if (idCt != null){
            Cnf_selectionBean beanCFG = (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
            beanCFG.setCentro(idCt); 
            Map session = ADFContext.getCurrent().getSessionScope();
            session.put("cnf_setCentro",Boolean.valueOf(true));
            
            Cnf_AppModuleImpl cnfAm =
                (Cnf_AppModuleImpl) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
            
            Key k = new Key(new Object[]{idCt});
            ViewObject voIter = cnfAm.findViewObject("Cnf_SoCentroPrelRefView1");
            ViewHelper.setCurrent(voIter, k);
        }
        
        return "agenda_centro";
    }


    protected boolean beforeSave() {
        // TODO:  Override this view.commons.Parent_DataForwardAction method
        // veriifco se ci sono nuovo righe, se s� devo fare elab. per orario
        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        ViewObject voSlot = am.findViewObject("Agenda_SoAppuntamentoView1");
        //27022013 Gaion: gestione dell'eccezione nel caso getFilteredRows fallisca
        try {
            // controllo orario nuova riga
            Row[] nSlots = voSlot.getFilteredRows("RigaSalvata", "N");
            if (nSlots.length > 0) {
                Agenda_SoAppuntamentoViewRow slot = (Agenda_SoAppuntamentoViewRow) nSlots[0];
                Map req = ADFContext.getCurrent().getRequestScope();
                String ora = slot.getOrario() != null ? (String) slot.getOrario() : null;
                Integer hhr = null;
                Integer min = null;
                String msgformato = "L'orario impostato non ha un formato corretto. Impossibile aggiornare.";
                try {
                    String sep = ora.substring(2, 3);
                    if (!sep.equals(":")) {
                        this.handleException(msgformato, null);
                        return false;
                    }
                    String hh = ora.substring(0, 2);
                    String mi = ora.substring(3, 5);
                    hhr = new Integer(hh);
                    min = new Integer(mi);
                } catch (Exception ex) {
                    this.handleException(msgformato, null);
                    return false;
                }
                if (hhr == null || hhr.intValue() < 0 || hhr.intValue() > 23) {
                    this.handleException(msgformato, null);
                    return false;
                }
                if (min == null || min.intValue() < 0 || min.intValue() > 59) {
                    this.handleException(msgformato, null);
                    return false;
                }
                slot.setOraapp(hhr);
                slot.setMinapp(min);

            }
        } catch (Exception e) {
            this.handleException(e.toString(), null);
            return false;
        }

        // controllo che tutte le disp siano > 0
        //  RowSetIterator rsi = voSlot.createRowSetIterator("iter_orari");
        RowSetIterator rsi = ViewHelper.getRowSetIterator(voSlot, "iter_orari");
        while (rsi.hasNext()) {
            Agenda_SoAppuntamentoViewRow slot = (Agenda_SoAppuntamentoViewRow) rsi.next();
            int disp = slot.getDispslot().intValue();
            if (disp < 0) {
                this.handleException("Esistono orari con disponibilita' < 0. Impossibile aggiornare.", null);
                rsi.closeRowSetIterator();
                return false;
            }
        }
        rsi.closeRowSetIterator();

        return true;
    }

    protected String afterSave() {
        // TODO:  Override this view.commons.Parent_DataForwardAction method
        Map session = ADFContext.getCurrent().getSessionScope();
        boolean creato = ((Boolean) session.get("creatoNuovo")).booleanValue();
        if (creato) {
            session.put("creatoNuovo", Boolean.FALSE);
            return "goCerca";
        }
        AgendaUtils.loadSint("G");
        return null;
    }


    public String onConf() {
        boolean saveOK = this.save();
        if (saveOK) {
            Map session = ADFContext.getCurrent().getSessionScope();
            String chiamante = (String) session.get("chAgenda");

            if (chiamante.equals("menuApp.do")) {
                return "adfMenu_home";

            } else if (chiamante.equals("sogg_preInvCorr.do")) {
                return "goInvCorr";

            } else if (chiamante.equals("sogg_preNuovo.do")) {
                return "goNuovoInv";

            } else if (chiamante.equals("acc_preEsito.do")) {
                return "goEsitoCI";

            } else if (chiamante.equals("accCo_preEsito.do")) {
                return "to_accCo_preEsito";

            } else if (chiamante.equals("accMa_preEsito.do")) {
                return "to_accMa_preEsito";

            } else if (chiamante.equals("cnf_agendaCentro2.do")) {
                return "to_cnf_agendaCentro2_p";

            }
        }

        return null;
    }

    public String onAppl() {
         onCerca(null);
        return null;
    }

    public String onSpostaqui() {
        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        ViewObject voSlot = am.findViewObject("Agenda_SoAppuntamentoView1");

        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String) session.get("user");
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        //journaling
        //100120123 sara
        Boolean flag = (Boolean) session.get("flagAbilJournal");
        if (flag != null && flag.booleanValue()) {
            ((Parent_AppModule) am).preapareJournaling(user, ulss, tpscr);
        }
        
        //fine 10012013
        boolean creato = ((Boolean) session.get("creatoNuovo")).booleanValue();
        if (creato) {

            // verifico se ci sono nuovo righe, se s� devo fare elab. per orario
            // controllo orario nuova riga
            Row[] nSlots = voSlot.getFilteredRows("RigaSalvata", "N");
            if (nSlots.length > 0) {
                Agenda_SoAppuntamentoViewRow slot = (Agenda_SoAppuntamentoViewRow) nSlots[0];
                Map req = ADFContext.getCurrent().getRequestScope();
                String ora = slot.getOrario() != null ? (String) slot.getOrario() : null;
                Integer hhr = null;
                Integer min = null;
                String msgformato = "L'orario impostato non ha un formato corretto. Impossibile aggiornare.";

                try {
                    String sep = ora.substring(2, 3);
                    if (!sep.equals(":")) {
                        this.handleException(msgformato, null);
                        return null;
                    }
                    String hh = ora.substring(0, 2);
                    String mi = ora.substring(3, 5);
                    hhr = new Integer(hh);
                    min = new Integer(mi);
                } catch (Exception ex) {
                    this.handleException(msgformato, null);
                    return null;
                }

                if (hhr == null || hhr.intValue() < 0 || hhr.intValue() > 23) {
                    this.handleException(msgformato, null);
                    return null;
                }
                if (min == null || min.intValue() < 0 || min.intValue() > 59) {
                    this.handleException(msgformato, null);
                    return null;
                }
                slot.setOraapp(hhr);
                slot.setMinapp(min);
            }

            // controllo che tutte le disp siano > 0
            //  RowSetIterator rsi = voSlot.createRowSetIterator("iter_orari");
            RowSetIterator rsi = ViewHelper.getRowSetIterator(voSlot, "iter_orari");
            while (rsi.hasNext()) {
                Agenda_SoAppuntamentoViewRow slot = (Agenda_SoAppuntamentoViewRow) rsi.next();
                int disp = slot.getDispslot().intValue();
                if (disp < 0) {
                    this.handleException("Esistono orari con disponibilita' < 0. Impossibile aggiornare.", null);
                    rsi.closeRowSetIterator();
                    return null;
                }
            }
            rsi.closeRowSetIterator();
            // committo slot nuovo
            am.getTransaction().commit();

            session.put("creatoNuovo", Boolean.FALSE);
            return "goCerca";
        }

        Row cSlot = voSlot.getCurrentRow();
        Integer idApp = (Integer) cSlot.getAttribute("Idapp");
        Integer idCt = (Integer) cSlot.getAttribute("Idcentro");
        oracle.jbo.domain.Date dtApp = (oracle.jbo.domain.Date) cSlot.getAttribute("Dtapp");
        Integer oraApp = (Integer) cSlot.getAttribute("Oraapp");
        Integer minApp = (Integer) cSlot.getAttribute("Minapp");

        String chAg = ((String) session.get("chAgenda")).substring(0, 4);

        // differenzio gestione nel caso di chiamata da accettazione
        // acc. citologico
        if (chAg.equals("acc_")) {
            Acc_AppModule amAcc =
                (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("Acc_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();

            String idTipo = (String) cInv.getAttribute("Idtpinvito");
            String ulssTipo = (String) cInv.getAttribute("Ulss");
            String tpscrTipo = (String) cInv.getAttribute("Tpscr");
            boolean compatibili = SoggUtils.ctVStipo(idCt, idTipo, ulssTipo, tpscrTipo, am);
            Integer idInvCorr = null;
           
            if (!compatibili) {
                String msg =
                    "Il centro prelievo e il tipo di invito impostati " +
                    "hanno livello diverso. Impossibile aggiornare.";
                this.handleException(msg, null);
                return null;
            }

            try {
                cInv.setAttribute("Idapp", idApp);
                cInv.setAttribute("Idcentroprelievo", idCt);
                cInv.setAttribute("Dtapp", dtApp);
                cInv.setAttribute("Oraapp", oraApp);
                cInv.setAttribute("Minapp", minApp);
                cInv.setAttribute("DaConfermare", new Integer(0));
                cInv.setAttribute("Opmodifica", user);
                cInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());

                Integer ctRef = SoggUtils.getCtRef(am, idCt);
                cInv.setAttribute("Idcentroref1liv", ctRef);

                Timestamp dtAppIni = (Timestamp) cInv.getAttribute("Dtappiniziale");
                if (dtAppIni == null) {
                    Calendar c = Calendar.getInstance(Locale.ITALY);
                    c.clear();
                    c.setTime(dtApp.dateValue());
                    c.set(Calendar.HOUR_OF_DAY, oraApp.intValue());
                    c.set(Calendar.MINUTE, minApp.intValue());

                    oracle.jbo.domain.Date dtIni = new oracle.jbo.domain.Date(new java.sql.Timestamp(c.getTime().getTime()));

                    cInv.setAttribute("Dtappiniziale", dtIni);
                }

                idInvCorr = (Integer) cInv.getAttribute("Idinvito");
                Integer livello = (Integer) cInv.getAttribute("Livello");
                String testProposto = (String) cInv.getAttribute("TestProposto");

                // 27122013 Gaion: cod_richiesta 2 liv
                if (tpscr.equals("CI")) {
                    //gaion 07/06/2011
                    Boolean hpv = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
                    InvitoUtils.gesAccCito(amAcc, idInvCorr, dtApp, livello.intValue(), ctRef, user, ulss, tpscr, hpv,
                                           false, testProposto);
                }

                // 05/11/2009 rigenerazione lettera
                String codTs = (String) cInv.getAttribute("Codts");
                InvitoUtils.gestioneRigLettera(amAcc, ulssTipo, tpscrTipo, idTipo, codTs, idApp, idInvCorr, idCt, dtApp,
                                               testProposto);
                // 05/11/2009, fine
            } catch (Exception ex) {
                this.handleException(ex.getMessage(), amAcc.getTransaction());
                return null;
            }

            amAcc.getTransaction().commit();

            voInvito.executeQuery();

            if(idInvCorr!=null)
                ViewHelper.restoreCurrentRow(voInvito, "Idinvito", idInvCorr);
        }

        // accettazione colon
        else if (chAg.equals("accC")) {
            AccCo_AppModule amAcc =
                (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("AccCo_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();
            Integer idInvCorr = null;

            String idTipo = (String) cInv.getAttribute("Idtpinvito");
            String ulssTipo = (String) cInv.getAttribute("Ulss");
            String tpscrTipo = (String) cInv.getAttribute("Tpscr");
            
            boolean compatibili = SoggUtils.ctVStipo(idCt, idTipo, ulssTipo, tpscrTipo, am);

            if (!compatibili) {
                String msg =
                    "Il centro prelievo e il tipo di invito impostati " +
                    "hanno livello diverso. Impossibile aggiornare.";
                this.handleException(msg, null);
                return null;
            }
            
            //I00101171 - CONGRUENZA TRA CENTRO DI REFERTAZIONE E TIPO DI INVITO
            if (tpscr.equals("CO")){
                boolean found = centroRefTipoInvito(idCt, idTipo, am);
                    
                if (!found){
                    String msg = "Impossibile salvare: tipo invito e centro refertazione sono incompatibili.";
                    this.handleException(msg, null);
                    return null;
                }
            }

            try {
                cInv.setAttribute("Idapp", idApp);
                cInv.setAttribute("Idcentroprelievo", idCt);
                cInv.setAttribute("Dtapp", dtApp);
                cInv.setAttribute("Oraapp", oraApp);
                cInv.setAttribute("Minapp", minApp);
                cInv.setAttribute("DaConfermare", new Integer(0));
                cInv.setAttribute("Opmodifica", user);
                cInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                Integer ctRef = SoggUtils.getCtRef(am, idCt);
                cInv.setAttribute("Idcentroref1liv", ctRef);

                Timestamp dtAppIni = (Timestamp) cInv.getAttribute("Dtappiniziale");
                if (dtAppIni == null) {
                    Calendar c = Calendar.getInstance(Locale.ITALY);
                    c.clear();
                    c.setTime(dtApp.dateValue());
                    c.set(11, oraApp.intValue());
                    c.set(12, minApp.intValue());
                    
                    oracle.jbo.domain.Date dtIni = new oracle.jbo.domain.Date(new java.sql.Timestamp(c.getTime().getTime()));

                    cInv.setAttribute("Dtappiniziale", dtIni);
                }

                idInvCorr = (Integer) cInv.getAttribute("Idinvito");
                Integer livello = (Integer) cInv.getAttribute("Livello");
                String otp = (String) cInv.getAttribute("Otp");

                if (tpscr.equals("CO")) {                               
                    InvitoUtils.gesAccColon(amAcc, idInvCorr, dtApp, livello.intValue(), ctRef, user, ulss, tpscr);                                         
                }

                // 05/11/2009 rigenerazione lettera
                String codTs = (String) cInv.getAttribute("Codts");
                InvitoUtils.gestioneRigLettera(amAcc, ulssTipo, tpscrTipo, idTipo, codTs, idApp, idInvCorr, idCt, dtApp,
                                               otp);
                // 05/11/2009, fine
            } catch (Exception ex) {
                this.handleException(ex.getMessage(), amAcc.getTransaction());
                return null;
            }

            amAcc.getTransaction().commit();

            voInvito.executeQuery();
            
            if(idInvCorr!=null)
                ViewHelper.restoreCurrentRow(voInvito, "Idinvito", idInvCorr);
            
            ViewObject voAcc = amAcc.findViewObject("AccCo_SoAccColon1livView1");
            voAcc.executeQuery();
        }

        // acc. mammo
        else if (chAg.equals("accM")) {
            AccMa_AppModule amAcc =
                (AccMa_AppModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("AccMa_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();
            Integer idInvCorr = (Integer) cInv.getAttribute("Idinvito");

            String idTipo = (String) cInv.getAttribute("Idtpinvito");
            String ulssTipo = (String) cInv.getAttribute("Ulss");
            String tpscrTipo = (String) cInv.getAttribute("Tpscr");
            boolean compatibili = SoggUtils.ctVStipo(idCt, idTipo, ulssTipo, tpscrTipo, am);

            if (!compatibili) {
                String msg =
                    "Il centro prelievo e il tipo di invito impostati " +
                    "hanno livello diverso. Impossibile aggiornare.";
                this.handleException(msg, null);
                return null;
            }

            try {
                cInv.setAttribute("Idapp", idApp);
                cInv.setAttribute("Idcentroprelievo", idCt);
                cInv.setAttribute("Dtapp", dtApp);
                cInv.setAttribute("Oraapp", oraApp);
                cInv.setAttribute("Minapp", minApp);
                cInv.setAttribute("DaConfermare", new Integer(0));
                cInv.setAttribute("Opmodifica", user);
                cInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                Integer ctRef = SoggUtils.getCtRef(am, idCt);
                cInv.setAttribute("Idcentroref1liv", ctRef);

                Timestamp dtAppIni = (Timestamp) cInv.getAttribute("Dtappiniziale");
                if (dtAppIni == null) {
                    Calendar c = Calendar.getInstance(Locale.ITALY);
                    c.clear();
                    c.setTime(dtApp.dateValue());
                    c.set(11, oraApp.intValue());
                    c.set(12, minApp.intValue());
                    
                    oracle.jbo.domain.Date dtIni = new oracle.jbo.domain.Date(new java.sql.Timestamp(c.getTime().getTime()));

                    cInv.setAttribute("Dtappiniziale", dtIni);
                }

                if (tpscr.equals("MA")) {
                    Integer livello = (Integer) cInv.getAttribute("Livello");
                    int liv = livello.intValue();
                    String prodNrich = "";
                    String prodNrich2 = "";
                    ViewObject voParam = amAcc.findViewObject("A_SoCnfParametriSistemaView1");
                    if (liv == 1)
                        prodNrich =
                            ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                           ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
                    if (liv == 2)
                        prodNrich2 =
                            ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                           ConfigurationConstants.PARAMETRO_PROD_RICHIESTA2);

                    if ((liv == 1 && "S".equals(prodNrich)) || (liv == 2 && "S".equals(prodNrich2))) {
                        oracle.jbo.domain.Date dtInvito = (oracle.jbo.domain.Date) cInv.getAttribute("Dtapp");
                        InvitoUtils.gesAccMammo(amAcc, idInvCorr, dtInvito, liv, ulss, idCt, user, tpscr);
                    }
                }

                // 05/11/2009 rigenerazione lettera
                String codTs = (String) cInv.getAttribute("Codts");
                String otp = (String) cInv.getAttribute("Otp");
                InvitoUtils.gestioneRigLettera(amAcc, ulssTipo, tpscrTipo, idTipo, codTs, idApp, idInvCorr, idCt, dtApp,
                                               otp);
                // 05/11/2009, fine
            } catch (Exception ex) {
                this.handleException(ex.getMessage(), amAcc.getTransaction());
                return null;
            }

            amAcc.getTransaction().commit();

            voInvito.executeQuery();
            //impossibile sostituirlo con queryAndRestoreCurrentRow
            ViewHelper.restoreCurrentRow(voInvito, "Idinvito", idInvCorr);
        }

        // acc. pfas
        else if (chAg.equals("accP")) {
            AccPf_AppModule amAcc =
                (AccPf_AppModule) BindingContext.getCurrent().findDataControl("AccPf_AppModuleDataControl").getDataProvider();
            ViewObject voInvito = amAcc.findViewObject("AccPf_SoInvitoView1");
            Row cInv = voInvito.getCurrentRow();

            String idTipo = (String) cInv.getAttribute("Idtpinvito");
            String ulssTipo = (String) cInv.getAttribute("Ulss");
            String tpscrTipo = (String) cInv.getAttribute("Tpscr");
            boolean compatibili = SoggUtils.ctVStipo(idCt, idTipo, ulssTipo, tpscrTipo, am);

            if (!compatibili) {
                String msg =
                    "Il centro prelievo e il tipo di invito impostati " +
                    "hanno livello diverso. Impossibile aggiornare.";
                this.handleException(msg, null);
                return null;
            }

            try {
                cInv.setAttribute("Idapp", idApp);
                cInv.setAttribute("Idcentroprelievo", idCt);
                cInv.setAttribute("Dtapp", dtApp);
                cInv.setAttribute("Oraapp", oraApp);
                cInv.setAttribute("Minapp", minApp);
                cInv.setAttribute("DaConfermare", new Integer(0));
                cInv.setAttribute("Opmodifica", user);
                cInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
                Integer ctRef = SoggUtils.getCtRef(am, idCt);
                cInv.setAttribute("Idcentroref1liv", ctRef);

                Timestamp dtAppIni = (Timestamp) cInv.getAttribute("Dtappiniziale");
                if (dtAppIni == null) {
                    Calendar c = Calendar.getInstance(Locale.ITALY);
                    c.clear();
                    c.setTime(dtApp.dateValue());
                    c.set(11, oraApp.intValue());
                    c.set(12, minApp.intValue());
                    
                    oracle.jbo.domain.Date dtIni = new oracle.jbo.domain.Date(new java.sql.Timestamp(c.getTime().getTime()));

                    cInv.setAttribute("Dtappiniziale", dtIni);
                }

                Integer idInvCorr = (Integer) cInv.getAttribute("Idinvito");
                Integer livello = (Integer) cInv.getAttribute("Livello");

                if (tpscr.equals("PF")) {
                    if (livello.intValue() == 1)
                        InvitoUtils.gesAccPfas(amAcc, idInvCorr, dtApp, livello.intValue(), ctRef, user, ulss, tpscr);
                }

                String codTs = (String) cInv.getAttribute("Codts");
                String otp = (String) cInv.getAttribute("Otp");
                InvitoUtils.gestioneRigLettera(amAcc, ulssTipo, tpscrTipo, idTipo, codTs, idApp, idInvCorr, idCt, dtApp,
                                               otp);

            } catch (Exception ex) {
                this.handleException(ex.getMessage(), amAcc.getTransaction());
                return null;
            }

            amAcc.getTransaction().commit();

            voInvito.executeQuery();
            ViewObject voAcc = amAcc.findViewObject("AccPf_SoAccPfas1livView1");
            voAcc.executeQuery();
        }

        // da soggetto, o da menu, o da configurazioni
        else {
            String msg = spostaSogg(am, idApp, idCt, dtApp, oraApp, minApp, tpscr, ulss);
            if (msg != null) {
                handleException(msg, null);
                return null;
            }
            
        }

        session.put("appSel", Boolean.valueOf(false));
        boolean daMenu = ((Boolean) session.get("agDaMenu")).booleanValue();
        if (daMenu) {
            /* MOD 20/06/2007
                          * voSlot.executeQuery();
                          Row[] slots = voSlot.getFilteredRows("Idapp",idApp);
                          voSlot.setCurrentRow(slots[0]);

                          int index=voSlot.getCurrentRowIndex();
                          int range=voSlot.getRangeSize();
                          if(range>0)
                          voSlot.scrollRangeTo(slots[0],index % range);
                          else
                          voSlot.scrollRangeTo(slots[0],index);
                          */
            ViewHelper.queryAndRestoreCurrentRow(voSlot);
            AgendaUtils.loadSint("G");
        } else {
            try {
                /*
                String dest = (String) session.get("chAgenda");
                //ADFContext.getCurrent().getRequestScope().getRequestDispatcher(dest).forward(ADFContext.getCurrent().getRequestScope(), ctx.getHttpServletResponse());
                if (dest == null)
                    throw new Exception("destinazione non specificata");

                String suffix = "";
                if (dest.indexOf("?") >= 0)
                    suffix = "_p";
                int index = dest.indexOf(".");
                if (index >= 0)
                    dest = dest.substring(0, index);
                return "to_" + dest + suffix;*/
                return "rb_daAgenda";
            } catch (Exception ex) {
                this.handleException("Impossibile navigare verso la destinazione sogg_invCorr.do", null);
            }
        }

        return null;
    }

    public void onSposta(ActionEvent actionEvent) {
        Agenda_AppModule am =
            (Agenda_AppModule) BindingContext.getCurrent().findDataControl("Agenda_AppModuleDataControl").getDataProvider();
        Map session = ADFContext.getCurrent().getSessionScope();
        ViewObject voApp;
        String tpscr = (String) session.get("scr");
        if (tpscr.equals("CO")) {
            voApp = am.findViewObject("Agenda_DettAppColonView1");
        } else {
            voApp = am.findViewObject("Agenda_DettAppView1");
        }
        Row cApp = voApp.getCurrentRow();
        String cogn = (String) cApp.getAttribute("Cognome");
        String nome = (String) cApp.getAttribute("Nome");
        oracle.jbo.domain.Date dtApp = (oracle.jbo.domain.Date) cApp.getAttribute("Dtapp");
        String dataApp = DateUtils.dateToString(dtApp.dateValue());
        session.put("pazSel",
                    "L'appuntamento relativo al soggetto " + cogn + " " + nome + " in data " + dataApp +
                    " e' stato selezionato per lo spostamento.");
        session.put("appSel", Boolean.valueOf(true));


        Sogg_AppModule amSogg =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = amSogg.findViewObject("Sogg_SoInvitoView1");
        Integer idInv = (Integer) cApp.getAttribute("Idinvito");
        voInvito.setWhereClause("IDINVITO = " + idInv.toString());
        voInvito.executeQuery();
        Row fInv = voInvito.first();
        voInvito.setCurrentRow(fInv);

    }


    // To override a method of the lifecycle, go to
    // the main menu "Tools/Override Methods...".
    protected void setAppModule() {
        this.amName = "Agenda_AppModule";
    }

    protected void beforeNavigate(String dest) throws Exception {
        // TODO:  Override this view.commons.Parent_DataForwardAction method
        AgendaUtils.beforeNav(dest);
    }


    public void setPanelOrari(RichPanelCollection panelOrari) {
        this.panelOrari = panelOrari;
    }

    public RichPanelCollection getPanelOrari() {
        return panelOrari;
    }
    
    public static String spostaSogg(ApplicationModule am, Integer idApp, Integer idCPrel,
                      oracle.jbo.domain.Date dtApp, Integer oraApp, Integer minApp, String tpscr, String ulss) {

        Map session = ADFContext.getCurrent().getSessionScope();
        String user = (String) session.get("user");
        String chAg = ((String) session.get("chAgenda")).substring(0, 4);

        Sogg_AppModule amSogg =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        ViewObject voInvito = amSogg.findViewObject("Sogg_SoInvitoView1");
        Row cInv = voInvito.getCurrentRow();

        String idTipo = (String) cInv.getAttribute("Idtpinvito");
        String ulssTipo = (String) cInv.getAttribute("Ulss");
        String tpscrTipo = (String) cInv.getAttribute("Tpscr");

        // Controllo la coerenza tra data appuntamento e tipo invito
        // Recupero la configurazione del tipo invito
        ViewObject voTipoInvito = amSogg.findViewObject("A_SoCnfTpinvitoView1");
        voTipoInvito.setWhereClause("TPSCR = '" + tpscr + "' AND ULSS = '" + ulss + "' AND IDTPINVITO = '" +
                                    idTipo + "'");
        voTipoInvito.executeQuery();
        voTipoInvito.next();
        Row rowTipoInvito = voTipoInvito.getCurrentRow();
        oracle.jbo.domain.Date dtFineValidita = (oracle.jbo.domain.Date) rowTipoInvito.getAttribute("Dtfinevalidita");
        if (dtFineValidita != null && dtApp.compareTo(dtFineValidita) > 0) {
            return "Il tipo invito non e' valido alla data dell'appuntamento";
        }

        boolean compatibili = SoggUtils.ctVStipo(idCPrel, idTipo, ulssTipo, tpscrTipo, am);

        if (!compatibili) {
            return
                "Il centro prelievo e il tipo di invito impostati " +
                "hanno livello diverso. Impossibile aggiornare.";
        }

        //I00101171 - CONGRUENZA TRA CENTRO DI REFERTAZIONE E TIPO DI INVITO
        if (tpscr.equals("CO")){
            boolean found = centroRefTipoInvito(idCPrel, idTipo, am);
                
            if (!found)
                return "Impossibile salvare: tipo invito e centro refertazione sono incompatibili.";
        }
        
        try {
            cInv.refresh(Row.REFRESH_CONTAINEES);
            cInv.setAttribute("Idcentroprelievo", idCPrel);

            cInv.setAttribute("Idapp", idApp);
            cInv.setAttribute("Dtapp", dtApp);
            cInv.setAttribute("Oraapp", oraApp);
            cInv.setAttribute("Minapp", minApp);
            cInv.setAttribute("DaConfermare", new Integer(0));
            cInv.setAttribute("Opmodifica", user);
            cInv.setAttribute("Dtultimamod", DateUtils.getOracleDateNow());
            Integer ctRef = SoggUtils.getCtRef(am, idCPrel);
            cInv.setAttribute("Idcentroref1liv", ctRef);

            oracle.jbo.domain.Date dtAppIni = (oracle.jbo.domain.Date) cInv.getAttribute("Dtappiniziale");
            if (dtAppIni == null) {
                Calendar c = Calendar.getInstance(Locale.ITALY);
                c.clear();
                c.setTime(dtApp.dateValue());
                c.set(Calendar.HOUR_OF_DAY, oraApp.intValue());
                c.set(Calendar.MINUTE, minApp.intValue());
                
                oracle.jbo.domain.Date dtIni = new oracle.jbo.domain.Date(new java.sql.Timestamp(c.getTime().getTime()));
                cInv.setAttribute("Dtappiniziale", dtIni);
            }

            Integer idInvCorr = (Integer) cInv.getAttribute("Idinvito");
            Integer livello = (Integer) cInv.getAttribute("Livello");
            String testProposto = (String) cInv.getAttribute("TestProposto");

            if (tpscr.equals("CO")) {                               
                    InvitoUtils.gesAccColon(amSogg, idInvCorr, dtApp, livello.intValue(), ctRef, user, ulss, tpscr);                                
            }

            if (tpscr.equals("MA")) {
                int liv = livello.intValue();
                String prodNrich = "";
                String prodNrich2 = "";
                ViewObject voParam = amSogg.findViewObject("A_SoCnfParametriSistemaView1");
                if (liv == 1)
                    prodNrich =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
                if (liv == 2)
                    prodNrich2 =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA2);

                if ((liv == 1 && "S".equals(prodNrich)) || (liv == 2 && "S".equals(prodNrich2))) {
                    InvitoUtils.gesAccMammo(amSogg, idInvCorr, dtApp, liv, ulss, idCPrel, user, tpscr);
                }
            }

            // 27122013 Gaion : cod_richiesta per 2 livelli
            if (tpscr.equals("CI")) {
                //gaion 07/06/2011
                Boolean hpv = (Boolean) ADFContext.getCurrent().getSessionScope().get("HPV");
                InvitoUtils.gesAccCito(amSogg, idInvCorr, dtApp, livello.intValue(), ctRef, user, ulss, tpscr, hpv,
                                       false, testProposto);
            }

            //20170126 Serra: cod richiesta e campione per il PFAS
            if (tpscr.equals("PF")) {

                InvitoUtils.gesAccPfas(amSogg, idInvCorr, dtApp, livello.intValue(), ctRef, user, ulss, tpscr);
            }

            // 05/11/2009 rigenerazione lettera
            String codTs = (String) cInv.getAttribute("Codts");
            InvitoUtils.gestioneRigLettera(amSogg, ulssTipo, tpscrTipo, idTipo, codTs, idApp, idInvCorr, idCPrel,
                                           dtApp, testProposto);
            // 05/11/2009, fine
        } catch (Exception ex) {
            Transaction t = amSogg.getTransaction();
            if (t != null) {
                t.rollback();
            }
            return ex.getMessage();
        }

        amSogg.getTransaction().commit();
        //voInvito.executeQuery();

        if (chAg.equals("sogg")) {
            session.put("sceltaOrario", Boolean.TRUE);
            SoggUtils.filtraOrari(true);
            Integer livello = (Integer) cInv.getAttribute("Livello");
            SoggUtils.filtraEsiti(dtApp, livello);
        }
        return null;
    }
    
    //I00101171 - CONGRUENZA TRA CENTRO DI REFERTAZIONE E TIPO DI INVITO
    private static boolean centroRefTipoInvito(Integer idCt, String idTipo, ApplicationModule am) {
        boolean found = true;
        
        String sql = "SELECT ct.idtpinvito FROM SO_CENTRO_PREL_REF cpr " +
            "JOIN SO_CNF_CENTROREF_TPINVITO ct ON cpr.idcentroref = ct.idcentroref " +
            "where cpr.idcentro = " + idCt + " AND cpr.tipocentro = 1";
        
        ViewObject vo = am.createViewObjectFromQueryStmt("", sql);
        vo.setRangeSize(-1);
        vo.executeQuery();
        
        if (vo==null || vo.getEstimatedRowCount()==0)
            return found;
            
        else if (vo.getEstimatedRowCount()>0){
            found = false;
            
            RowSetIterator it = vo.createRowSetIterator(null);
            while (it.hasNext()) {
                Row r = it.next();
                String idTipoOk = (String) r.getAttribute(0);
                
                if (idTipoOk!=null && idTipoOk.equals(idTipo))
                    found = true; 
            }
        }
        
        return found;
    }

}
