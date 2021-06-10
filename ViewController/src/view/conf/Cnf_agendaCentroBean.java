package view.conf;

import insiel.dataHandling.DateUtils;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import model.common.Cnf_AppModule;

import model.commons.AccessManager;
import model.commons.ViewHelper;

import model.conf.common.Cnf_SoAgendacentroViewRow;
import model.conf.common.Cnf_SoAppuntamentoViewRow;

import model.datacontrol.Cnf_selectionBean;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.binding.OperationBinding;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowIterator;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.backing.ParentBackingBean;

import view.commons.Tree;


public class Cnf_agendaCentroBean extends ParentBackingBean
{
/** GENERAZIONE AUTOMATICA DELL'AGENDA TEORICA **/
  /**
   * Numero di fasce orarie da generare
   */
  private int n_fasce=1;
  /**
   * Durata di ogni fascia oraria in minuti
   */
  private int durata=30;
  /**
   * Ora di inizio espressa in ore e minuti
   */
  private int ore=8;
  private int minuti=0;
  /**
   * Giorno della settimana (0=dal lunedì al venerdì)
   */
  private int giorno=0;
  /**
   * Disponibilita' di ogni fascia oraria
   */
  private int disponibilita=0;
  
  /** GENERAZIONE AUTOMATICA DELL'AGENDA TEORICA **/
  /**
   * Parametro per filtrare la visualizzazione delle fasce orarie
   * in base al giorno della settimana
   */
  private int filtro=0;
  
  /** GENERAZIONE AUTOMATICA DELL'AGENDA FISICA **/
  /**
   * Data da cui generare la disponibilita'
   */
  private String gen_dal;
  /**
   * Data fino alla quale (esclusa) generare la disponibilita'
   */
  private String gen_al;
  /**
   * Comportamento da tenere in caso ci siano gia' degli slot nel peridoo richiesto:
   * stop= non generare nulla
   * override_slot= se ci sono solo slot e non appuntamenti, allora cancella i vecchi 
   * slot e genera i nuovi, altrimenti non generare nulla
   * override_all=genera i nuovi slot in ogni caso (se ci sono appuntamenti tenta il
   * riposizionamento o li segnala come pendenti)
   */
  private String option="stop";
  /**
   * Generazione automatica
   * Fascia nascosta al cittadino che accede da portale
   */
   private Boolean nascosto = false;

  public Cnf_agendaCentroBean()
  {
  }

  public int getN_fasce()
  {
    return n_fasce;
  }

  public void setN_fasce(int n_fasce)
  {
    this.n_fasce = n_fasce;
  }

  public int getDurata()
  {
    return durata;
  }

  public void setDurata(int durata)
  {
    this.durata = durata;
  }

  public int getOre()
  {
    return ore;
  }

  public void setOre(int ore)
  {
    this.ore = ore;
  }

  public int getMinuti()
  {
    return minuti;
  }

  public void setMinuti(int minuti)
  {
    this.minuti = minuti;
  }

  public int getGiorno()
  {
    return giorno;
  }

  public void setGiorno(int giorno)
  {
    this.giorno = giorno;
  }

  public int getDisponibilita()
  {
    return disponibilita;
  }

  public void setDisponibilita(int disponibilita)
  {
    this.disponibilita = disponibilita;
  }

  public int getFiltro()
  {
    return filtro;
  }

  public void setFiltro(int filtro)
  {
    this.filtro = filtro;
  }

  public String getGen_dal()
  {
    return gen_dal;
  }

  public void setGen_dal(String gen_dal)
  {
    this.gen_dal = gen_dal;
  }

  public String getGen_al()
  {
    return gen_al;
  }

  public void setGen_al(String gen_al)
  {
    this.gen_al = gen_al;
  }

  public String getOption()
  {
    return option;
  }

  public void setOption(String option)
  {
    this.option = option;
  }


  public void setNascosto(Boolean nascosto)
  {
    this.nascosto = nascosto;
  }


  public Boolean getNascosto()
  {
    return nascosto;
  }
  

      /**
     * Event handler ch efiltra il contenuto dell'agenda teorica in
     * base al giorno della settimana selezionato
     * @param ctx
     */
    public void filter(ValueChangeEvent valueChangeEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject vo = bindings.findIteratorBinding("Cnf_SoAgendacentroView2Iterator").getViewObject();
        Integer newFiltro = (Integer) valueChangeEvent.getNewValue();
        try {
            if (newFiltro < 0 || newFiltro > 7)
                throw new Exception("Impossibile filtrare la visualizzazione: il valore del filtro non e' corretto");

            String where;
            if (newFiltro == 0 )
                where = null;
            else {
                where = "GGSETTIMANA=" + newFiltro;
            }
            vo.setWhereClause(where);
            vo.executeQuery();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage());
        }
    }
      
      /**
       * Event handler che si occupa di generare le fasce orarie richieste dall'utente
       * @param ctx
       */
      public void generate(ActionEvent actionEvent)
      {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoAgendacentroView2Iterator");
        ViewObject vo=voIter.getViewObject();
        
        try {
            AccessManager.checkPermission("SOGestioneConfigurazioneCentri");

            //re-inserisco il valore del centro, perche' potrebbe essere stato selezionato
            //un centro di refertazione, che ha un valore valido, ma l'utente non lo vede
            DCIteratorBinding centroIter = bindings.findIteratorBinding("Cnf_selectionBeanIterator");
            Cnf_selectionBean bean = (Cnf_selectionBean)centroIter.getDataControl().getDataProvider();
            Integer centro = bean.getCentro();
            DCIteratorBinding voCentroIter = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator");
            Row centroRow = voCentroIter.getCurrentRow();

            //controllo la corretteza dei dati inseriti
            if (centroRow == null || centro == null)
                throw new Exception("Nessun centro selezionato");

            if (this.getDisponibilita() < 0)
                throw new Exception("Impossibile generare fasce orarie con disponibilita' negativa");
            if (this.getGiorno() < 0 || this.getGiorno() > 7)
                throw new Exception("Il giorno selezionato non e' valido");
            if (this.getOre() < 0 || this.getOre() > 23 || this.getMinuti() < 0 || this.getMinuti() > 59)
                throw new Exception("L'orario di inizio impostato non e' valido");


            //determino il/i giorno/i su cui lavorare
            int inizio, fine;
            if (this.getGiorno() == 0) { //ciclo su tutti i giorni dal lunedì al venerdì
                inizio = 2; //lun
                fine = 6; //ven

            } else { //lavoro su un giorno unico
                inizio = this.getGiorno();
                fine = inizio;
            }

            //ciclo sui giorni
            for (int day = inizio; day <= fine; day++) {
                //imposto l'orario di partenza (valido per ogni giorno)
                Calendar c = Calendar.getInstance(Locale.ITALY);
                c.clear();
                c.set(Calendar.HOUR_OF_DAY, this.getOre());
                c.set(Calendar.MINUTE, this.getMinuti());
                //ciclo sul numero di fasce
                for (int slot = 0; slot < this.getN_fasce(); slot++) {
                    //creo la fascia oraria
                    Row fascia = vo.createRow();
                    vo.insertRow(fascia);  
                    fascia.setAttribute("Ggsettimana", new Integer(day));
                    fascia.setAttribute("Oraapp", new Integer(c.get(Calendar.HOUR_OF_DAY)));
                    fascia.setAttribute("Minapp", new Integer(c.get(Calendar.MINUTE)));
                    fascia.setAttribute("Disporaria", new Integer(this.getDisponibilita()));
                    if (this.getNascosto())
                        fascia.setAttribute("Nascosto", new Integer(1));
                    else 
                        fascia.setAttribute("Nascosto", new Integer(0));
                    //incremento l'ora
                    c.add(Calendar.MINUTE, this.getDurata());
                }
            }

            //salvo il tutto
            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            Object commitResult = commitBinding.execute();
            //mantengo l'ordinamento
            vo.executeQuery();
            
            DCIteratorBinding settimanaIter = bindings.findIteratorBinding("Cnf_SoAgendaCentroSettimanaleView2Iterator");
            settimanaIter.executeQuery();


        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleMessage(FacesMessage.SEVERITY_ERROR, "Impossibile generare le fasce orarie richieste: " + ex.getMessage());
        }
      }

    public void deleteAll(ActionEvent actionEvent) {
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject myVo = bindings.findIteratorBinding("Cnf_SoAgendacentroView2Iterator").getViewObject();
        RowSetIterator rsI = myVo.createRowSetIterator(null);
        while (rsI.hasNext()) {
            rsI.next().remove();
        }
        rsI.closeRowSetIterator();

        try {
            OperationBinding commitBinding = bindings.getOperationBinding("Commit");
            Object commitResult = commitBinding.execute();
            
            DCIteratorBinding settimanaIter = bindings.findIteratorBinding("Cnf_SoAgendaCentroSettimanaleView2Iterator");
            settimanaIter.executeQuery();

        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleMessage(FacesMessage.SEVERITY_ERROR,
                               "Impossibile effettuare la cancellazione: " + ex.getMessage());
        }

    }

    /**
     * Event handler che cancella definitivamente tutte le fasce orarie presenti nella tabella
     * @param ctx
     */
    public void selectCentro(ValueChangeEvent valueChangeEvent) {
        Integer idCentro = null;
        
        if (valueChangeEvent.getNewValue() instanceof Integer)
            idCentro = (Integer) valueChangeEvent.getNewValue();
        
        if (idCentro != null) {
            DCBindingContainer bindings = (DCBindingContainer) getBindings();
         
            Key k = new Key(new Object[]{idCentro});
            DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator");
            ViewHelper.setCurrent(voIter.getViewObject(), k);
            
            ADFContext.getCurrent().getSessionScope().put("centro", k);            
        }
    }

    public void generateAgenda(ActionEvent actionEvent) {

        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        OperationBinding commitBinding = bindings.getOperationBinding("Commit");
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sessionMap = adfCtx.getSessionScope();
        String tpscr = (String)sessionMap.get("scr");
        //prendo un'istanza diversa dell'agenda perche' altrimenti puo' essere filtrata
        //per giorno della settimana
        DCIteratorBinding voIter = bindings.findIteratorBinding("Cnf_SoAgendacentroView3Iterator");
        ViewObject vo = voIter.getViewObject();

        String result_msg = "";

        DCIteratorBinding voCentroIter = bindings.findIteratorBinding("Cnf_SoCentroPrelRefView1Iterator");
        Row cc = voCentroIter.getCurrentRow();
        
        DCIteratorBinding centroIter = bindings.findIteratorBinding("Cnf_selectionBeanIterator");
        Cnf_selectionBean bean = (Cnf_selectionBean) centroIter.getDataControl().getDataProvider();
        Integer centro = bean.getCentro();

        try {
            AccessManager.checkPermission("SOGestioneConfigurazioneCentri");

            if (cc != null && cc.getAttribute("Dtchiusuracentro") != null &&
                ((oracle.jbo.domain.Date) cc.getAttribute("Dtchiusuracentro")).getValue().before(new Date()))
                throw new Exception("Non e' possibile generare delle disponibilita' per un centro chiuso");

            //controllo la correttezza dei dati inseriti
            if (centro == null || centro <= 0)
                throw new Exception("Nessun centro selezionato");

            if (this.getGen_dal() == null || this.getGen_dal().length() == 0)
                throw new Exception("La data di inizio del periodo da generare e' obbligatoria");
            if (this.getGen_al() == null || this.getGen_al().length() == 0)
                throw new Exception("La data di fine del periodo da generare e' obbligatoria");

            //controllo che che il centro abbia un'agenda teorica
            if (vo.first() == null)
                throw new Exception("Non e' stata definita l'agenda teorica per il centro selezionato");

            //controllo sulle date
            //il formato
            try {
                //formato + l'inizio deve essere non prima di domani
                if (DateUtils.DATE_FORMATTER.parse(this.getGen_dal()).before(new Date()))
                    throw new Exception("La generazione dell'agenda fisica fino alla data corrente non e' un'operazione consentita.");

            } catch (ParseException pex) {
                throw new Exception("La data di inizio del periodo non corrisponde al formato dd/MM/yyyy");
            }
            try {
                DateUtils.DATE_FORMATTER.parse(this.getGen_al());
            } catch (ParseException pex) {
                throw new Exception("La data di fine del periodo non corrisponde al formato dd/MM/yyyy");
            }

            //controllo se ci sono gia' degli slot in quel periodo
            DCIteratorBinding appsIter = bindings.findIteratorBinding("Cnf_SoAppuntamentoView1Iterator");
            ViewObject apps = appsIter.getViewObject();
            String where =
                "Idcentro=" + centro + " AND DTAPP>=TO_DATE('" + this.getGen_dal() + "','" + DateUtils.DATE_PATTERN +
                "') AND DTAPP<TO_DATE('" + this.getGen_al() + "','" + DateUtils.DATE_PATTERN + "')";

            apps.setWhereClause(where);
            apps.executeQuery();

            //se ci sono degli slot
            if (apps.first() !=
                null) {
                //controllo il comportamneto da tenere
                if ("stop".equals(this.getOption())) { //l'utente ha chiesto di non sovrascrivere nulla
                    this.handleMessage(FacesMessage.SEVERITY_ERROR,
                                       "Sono gia' presenti delle disponibilita' per il periodo richiesto. Nessuna nuova disponibilita' e' stata generata.");
                    return;
                }


                //controllo se ci sono gia' degli appuntamenti
                DCIteratorBinding slotIter = bindings.findIteratorBinding("Cnf_SoSlotInvitiView1Iterator");
                ViewObject slot_inviti = slotIter.getViewObject();
                slot_inviti.setWhereClause(where);
                slot_inviti.executeQuery();

                //se ci sono anche degli appuntamenti
                if (slot_inviti.first() !=
                    null) {
                    //controllo il comportamneto da tenere
                    if ("override_slot".equals(this.getOption())) { //l'utente ha chiesto di non sovrascrivere gli appuntamenti
                        this.handleMessage(FacesMessage.SEVERITY_ERROR,
                                       "Sono gia' presenti delle disponibilita' e degli appuntamenti per il periodo richiesto. Nessuna nuova disponibilita' e' stata generata.");
                        return;
                    }
                    /*--------- CON OVERRIDING DEGLI SLOT E RIPOSIZIONAMENTO APPUNTAMENTI ---------*/
                    //posso sovrascrivere tutto, quindi devo:
                    //1. rendere pendenti tutti quegli appuntamenti
                    //2. cancellare i vecchi slot
                    Tree pendenti = this.deleteOldSlots(apps, true, commitBinding);
                    int pend = pendenti.getLeaf_count();
                    
                    //3. generare i nuovi slot
                    Vector new_slots =
                        this.generateSlots(vo, apps, centro, DateUtils.DATE_FORMATTER.parse(this.getGen_dal()),
                                           DateUtils.DATE_FORMATTER.parse(this.getGen_al()),tpscr);

                    //4.riposizionare gli appuntamenti se possibile
                    int repos = this.repositionApps(new_slots, pendenti);
                    Object commitResult =commitBinding.execute();
                    if (!commitBinding.getErrors().isEmpty()) {
                            //add error handling here
                            throw new Exception(commitBinding.getErrors().toString());
                    } else {
                        result_msg +=
                            "Generazione dell'agenda fisica dal " + this.getGen_dal() + " al " + this.getGen_al() +
                            " (escluso) completata con successo";
                        result_msg += " | n. " + repos + " appuntamenti sono stati riposizionati ";
                        result_msg += " | n. " + (pend - repos) + " appuntamenti sono rimasti pendenti ";
                    }
                } else
                /*--------- CON OVERRIDING DEGLI SLOT  ---------*/
                { //non ci sono appuntamenti, posso sovrascrivere gli slot
                    //1. cancello i vecchi slot
                    this.deleteOldSlots(apps, false, commitBinding);
                    //2. genero i nuovi slot
                    this.generateSlots(vo, apps, centro, DateUtils.DATE_FORMATTER.parse(this.getGen_dal()),
                                       DateUtils.DATE_FORMATTER.parse(this.getGen_al()),
                                       tpscr);
                    Object commitResult =commitBinding.execute();
                    if (!commitBinding.getErrors().isEmpty()) {
                            //add error handling here
                            throw new Exception(commitBinding.getErrors().toString());
                    } else {
                    result_msg +=
                        "Generazione dell'agenda fisica dal " + this.getGen_dal() + " al " + this.getGen_al() +
                        " (escluso) completata con successo";
                    }
                }

            } else //se non ci sono gia' degli slot
            { /*--------- SEMPLICE GENERAZIONE NUOVI SLOT ---------*/
                this.generateSlots(vo, apps, centro, DateUtils.DATE_FORMATTER.parse(this.getGen_dal()),
                                   DateUtils.DATE_FORMATTER.parse(this.getGen_al()),
                                   tpscr);
                Object commitResult =commitBinding.execute();
                if (!commitBinding.getErrors().isEmpty()) {
                        //add error handling here
                        throw new Exception(commitBinding.getErrors().toString());
                } else {
                result_msg +=
                    "Generazione dell'agenda fisica dal " + this.getGen_dal() + " al " + this.getGen_al() +
                    " (escluso) completata con successo";
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            this.handleMessage(FacesMessage.SEVERITY_ERROR,
                               "Impossibile generare la disponibilita' o riposizionare gli appuntamenti: " +
                               ex.getMessage());
        }

        finally {
            this.handleMessage(FacesMessage.SEVERITY_INFO, result_msg );
        }

        // m.avon, 11/01/2010 controllo duplicazione slot
        String qryCheck =
            "SELECT a.tpsrc, a.idcentro, a.dtapp, a.oraapp, a.minapp, count(*) " + "FROM so_appuntamento a " +
            "WHERE a.tpsrc = '" + tpscr + "' and a.idcentro = " + centro + " and a.dtapp >= to_date('" +
            this.getGen_dal() + "','dd/mm/yyyy')" + " and a.dtapp < to_date('" + this.getGen_al() + "','dd/mm/yyyy')" +
            "group by a.tpsrc, a.idcentro, a.dtapp, a.oraapp, a.minapp " + "having count(*) > 1";
        
        BindingContext ctx = BindingContext.getCurrent();
        Cnf_AppModule am = (Cnf_AppModule)ctx.findDataControl("Cnf_AppModuleDataControl").getDataProvider();

        ViewObject voCheck = am.createViewObjectFromQueryStmt("", qryCheck);
        voCheck.setRangeSize(-1);
        voCheck.executeQuery();

        int cntErr = voCheck.getRowCount();

        if (cntErr > 0) {
            String desErrore =
                "scr=" + tpscr + ";centro=" + centro + ";" + "dal=" + this.getGen_dal() + ";al=" +
                this.getGen_al();

            // mauro 10/03/2010, aggiunta ulss
            String ulss = (String) sessionMap.get("ulss");

            String insert =
                "INSERT INTO so_messaggi_applicazione (IDMESSAPP,IDMESS,DATA_MESS,ORIGINE,DETTAGLI,ULSS) VALUES " +
                "(so_messapp_seq.NEXTVAL,3,sysdate,'Cnf_generazAgendaAction.java','" + desErrore + "','" + ulss + "')";
            // mauro 10/03/2010, fine

            am.getTransaction().executeCommand(insert);

            commitBinding.execute();
        }
    }
    
    /**
     * Metodo che cancella l'elenco di slot fornito ed eventualmnete rende pendenti
     * gli inviti correlati (e salva il tutto)
     * @return un vettore contenente gli inviti resi pendenti (un albero con la struttura
     * "data"-->"ora"-->"min"-->"key"
     * @param app_pendenti se true, per ogni slot gli inviti correlati vengono
     * resi pendenti, se false non viene fatto nulla sugli inviti correlati (si assume che non
     * ce ne siano)
     * @param inviti viewobject detail dipendente da slots, contiene gli inviti che ountano
     * allo slot selezionato
     * @param slots viewobject contenete solo gli slot da cancellare
     * @param am application module
     */
    private Tree deleteOldSlots(ViewObject slots, boolean app_pendenti, OperationBinding commitBinding) throws Exception {

        Tree t = new Tree();

        RowSetIterator rsI = slots.createRowSetIterator(null);
        while (rsI.hasNext()) {
            Cnf_SoAppuntamentoViewRow slot = (Cnf_SoAppuntamentoViewRow) rsI.next();
            //se devo lavorare sugli inviti
            if (app_pendenti) {

                //ciclo sugli inviti correlati allo slot
                RowIterator inviti = slot.getCnf_SoInvitoView();
                Row r = inviti.first();
                while (r !=
                       null) {
                    //rendo l'appuntamneto pendente
                    r.setAttribute("Idapp",
                                   null);
                    //memorizzo i dati dellinvito per il riposizionamento
                    t.insertBranch(new Object[] { r.getAttribute("Dtapp"), r.getAttribute("Oraapp"),
                                                  r.getAttribute("Minapp"), r.getKey(), r });

                    r = inviti.next();

                }
            }
        }
        rsI.closeRowSetIterator();
        
        Object commitResult =commitBinding.execute();
        if (!commitBinding.getErrors().isEmpty()) {
                throw new Exception(commitBinding.getErrors().toString());
        }

        slots.executeQuery();
        
        RowSetIterator rsI2 = slots.createRowSetIterator(null);
        while (rsI2.hasNext()) {
            rsI2.next().remove();
        }
        rsI2.closeRowSetIterator();

        return t;
    }
    
    /**
     *  Metodo che genera gli slot per un centro per il periodo
     * definito rispettandone le chiusure e salva il tutto
     * @return il numero di giorni per i quali sono stati generati degli slot
     * @param tpscr sigla del tipo di screening su cui sto lavorando
     * @param al data di fine del periodo da generare (esclusa)
     * @param dal data di inizio del periodo da generare
     * @param centro identificativo del centro su cui si sta lavorando
     * @param agenda viewobject contenente solo l'agenda teorica per il centro
     * @param am application module (serve per ottere i viewobject degli appuntamenti
     * e delle chiusure e per effettuare la commit
     */
    private Vector generateSlots(ViewObject agenda, ViewObject apps, Integer centro, Date dal, Date al, String tpscr) {

        Vector new_slots = new Vector();
        
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject chiusure = bindings.findIteratorBinding("Cnf_SoChiusuracentroView1Iterator").getViewObject();
        //imposto la data di inizio
        Calendar c = Calendar.getInstance(Locale.ITALY);
        c.clear();
        c.setTime(dal);

        //ciclo fino a coprire tutti i giorni
        while (c.getTime().before(al)) {
            //controllo che la giornata non ricada in una chiusura
            String where =
                "IDCENTRO=" + centro + " AND DTDA<=TO_DATE('" + DateUtils.dateToString(c) + "','" +
                DateUtils.DATE_PATTERN + "') AND DTA>TO_DATE('" + DateUtils.dateToString(c) + "','" +
                DateUtils.DATE_PATTERN + "')";
            chiusure.setWhereClause(where);
            chiusure.executeQuery();
            //se c'e' una chiusura che contiene la data, allora non genero nulla per quel giorno
            if (chiusure.first() != null) {
                c.add(Calendar.DAY_OF_MONTH, 1);
                continue;
            }


            //leggo l'agenda teorica per quel giorno
            Row[] at = agenda.getFilteredRows("Ggsettimana", new Integer(c.get(Calendar.DAY_OF_WEEK)));
            //vado a generare slot per la giornata, quindi la conto come giornata generata
            //per ogni slot teorico...
            for (int i = 0; i < at.length; i++) {
                //...genero uno slot fisico
                Cnf_SoAgendacentroViewRow r = (Cnf_SoAgendacentroViewRow) at[i];
                Cnf_SoAppuntamentoViewRow slot = (Cnf_SoAppuntamentoViewRow) apps.createRow();
                apps.insertRow(slot);
                slot.setIdcentro(r.getIdcentro());
                slot.setDtapp(DateUtils.getOracleDate(c));
                slot.setOraapp(r.getOraapp());
                slot.setMinapp(r.getMinapp());
                slot.setDispslot(r.getDisporaria());
                slot.setTpsrc(tpscr);
                slot.setSlotoccup(new Integer(0));
                slot.setNascosto(r.getNascosto());

                //aggiungo lo slot generato al vettore
                new_slots.addElement(slot);
                //      System.out.println("Generato slot n° "+slot.getIdapp()+" il "+slot.getDtapp()+" alle "+slot.getOraapp()+":"+slot.getMinapp());
            }

            //passo al giorno successivo
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        return new_slots;
    }
    
    /**
     * Metodod che cerca, se possibile, di riposizionare gli appunatmenti
     * @return numero di appuntamneti riposizionati
     * @param apps albero degli appuntamenti pendenti
     * @param slots elenco dei nuovislot in cui riposizionare gli app
     * @param am application module
     */
    private int repositionApps(Vector slots, Tree apps) throws Exception {
        int repos = 0;
        DCBindingContainer bindings = (DCBindingContainer) getBindings();
        ViewObject vo = bindings.findIteratorBinding("Cnf_SoInvitoView1Iterator").getViewObject();

        //ciclo su ogni nuovo slot
        for (int i = 0; i < slots.size(); i++) {
            Cnf_SoAppuntamentoViewRow slot = (Cnf_SoAppuntamentoViewRow) slots.elementAt(i);

            HashMap map = (HashMap) apps.getNode(new Object[] { slot.getDtapp(), slot.getOraapp(), slot.getMinapp() });
            //se nessun invito puo' esere sistemato nello slot, procedo
            if (map == null)
                continue;

            //altrimenti riposiziono ognuno di questi inviti nello slot
            //leggo i singoli app
            Object[] inviti = map.values().toArray();
            for (int j = 0; j < inviti.length; j++) {
                Row r = (Row) inviti[j];
                vo.setWhereClause("IDINVITO=" + r.getAttribute("Idinvito"));
                vo.executeQuery();
                if (vo.first() == null)
                    throw new Exception("Invito con chiave " + r.getKey() + " non trovato per il riposizionamento");

                //imposto il puntatore allo slot
                vo.first().setAttribute("Idapp", slot.getIdapp());
                repos++;
            }
        }

        return repos;
    }
}
