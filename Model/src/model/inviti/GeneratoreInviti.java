package model.inviti;


import insiel.dataHandling.BlobUtils;
import insiel.dataHandling.DateUtils;

import java.io.File;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import model.accettazione.A_SoAccPfas1livViewRowImpl;
import model.accettazione.common.A_SoAccColon1livViewRow;

import model.common.A_SoAccCito1livView1Row;
import model.common.A_SoAllegatoViewRow;
import model.common.A_SoAppuntamentoViewRow;
import model.common.A_SoCnfComuniZoneParamViewRow;
import model.common.A_SoCnfLetteracentroViewRow;
import model.common.A_SoCnfTpinvitoViewRow;
import model.common.A_SoCnfTpscrViewRow;
import model.common.A_SoInvitoViewRow;
import model.common.A_SoRoundorgViewRow;
import model.common.A_SoTemplateViewRow;
import model.common.Parent_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.Lettera;
import model.commons.ParametriSistema;
import model.commons.Progressivi;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import model.global.common.A_SoSoggScrViewRow;
import model.global.common.A_SoSoggettoViewRow;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Number;

public class GeneratoreInviti {
    /**
     * Tabella hash che memorizza la mappa dei parametri dell'ulss
     * da passare al template
     */
    private HashMap ulssParams;

    /**
     * Tabella hash che memorizza, per ogni tipo di invito,
     * l'oggetto "lettera" da usare
     */
    private HashMap reports = new HashMap();

    private ApplicationModule am;
    private ViewObject allegato_vo;
    private ViewObject slot_vo;
    private ViewObject ulss_vo;
    //@codecoach:disable nextline
    private ViewObject centro_vo;
    private ViewObject lettera_vo;
    private ViewObject tpscr_vo;
    private ViewObject invito_vo;
    //@codecoach:disable nextline
    // private ViewObject medico_vo;
    private ViewObject round_vo;
    private ViewObject soggetto_vo;
    private ViewObject sogg_scr_vo;
    private ViewObject template_vo;
    private ViewObject params_vo;
    private ViewObject tpinvito_vo;
    private ViewObject accColon1l_vo;
    private ViewObject accCito1l_vo;
    private ViewObject accPfas1l_vo;
    private ViewObject comzonepar_vo;
    //201104011 Serra
    private ViewObject esclusioni_vo;
    //fine


    //@codecoach:disable nextline
    //  private ViewObject old;
    //@codecoach:disable nextline
    //  private ViewObject curr;
    private boolean am_new_instance = false;

    // Chiave di lunghezza arbitraria, i caratteri non devono essere ripetuti
    // Tolti 0, O, I, 1, che potrebbero essere confusi
    private static final String key = "ABCDEFGHLMNPQRSTUVZ23456789";
    private static final int RESULT_LENGTH = 8;

    /* public GeneratoreInviti()
  {
    am = (A_AppModule)Configuration.createRootApplicationModule("model.A_AppModule","A_AppModuleLocal");
    this.am_new_instance=true;
    this.instantiateVOs();
  }*/

    public GeneratoreInviti(ApplicationModule _am) {
        this(_am, false);
    }

    public GeneratoreInviti(ApplicationModule _am, boolean reduced) {
        this.am = _am;
        if (reduced)
            this.reducedInstantiateVOs();
        else
            this.instantiateVOs();
    }

    public GeneratoreInviti(ApplicationModule _am, String codts, String ulss, String tpscr) {
        this.am = _am;
        this.instantiateVOs();
        this.filter(codts, ulss, tpscr);
    }


    private void instantiateVOs() {
        this.allegato_vo = am.findViewObject("A_SoAllegatoView1");
        this.slot_vo = am.findViewObject("A_SoAppuntamentoView1");
        this.ulss_vo = am.findViewObject("A_SoAziendaView1");
        this.centro_vo = am.findViewObject("A_SoCentroPrelRefView1");
        this.lettera_vo = am.findViewObject("A_SoCnfLetteracentroView1");
        this.tpscr_vo = am.findViewObject("A_SoCnfTpscrView1");
        this.invito_vo = am.findViewObject("A_SoInvitoView1");
        //   this.medico_vo=am.findViewObject("A_SoMedicoView1");
        this.round_vo = am.findViewObject("A_SoRoundorgView1");
        this.soggetto_vo = am.findViewObject("A_SoSoggettoView1");
        this.template_vo = am.findViewObject("A_SoTemplateView1");
        this.params_vo = am.findViewObject("A_SoCnfPartemplateView1");
        this.accColon1l_vo = am.findViewObject("A_SoAccColon1livView1");
        this.accCito1l_vo = am.findViewObject("A_SoAccCito1livView1_1");
        this.accPfas1l_vo = am.findViewObject("A_SoAccPfas1livView1");
        this.comzonepar_vo = am.findViewObject("A_SoCnfComuniZoneParamView1");
        //201104011 Serra
        this.esclusioni_vo = am.findViewObject("A_SoEsclusioneView1");
        //fine


        this.reducedInstantiateVOs();
    }

    private void reducedInstantiateVOs() {
        this.sogg_scr_vo = am.findViewObject("A_SoSoggScrView1");
        //   this.old=am.findViewObject("A_SoOldRoundindivView1");
        //   this.curr=am.findViewObject("A_SoCurrentRoundindivView1");
        this.tpinvito_vo = am.findViewObject("A_SoCnfTpinvitoView1");

    }

    /**
   * Rilascia la connessione all'app module prima di eliminare l'oggetto
   * @throws java.lang.Throwable
   */
    /* protected void finalize() throws Throwable
  {
     if(am_new_instance)
        Configuration.releaseRootApplicationModule(am,true);
     //per evitare sovraccarico di connessioni al db (7/12/2006)
     //Configuration.releaseRootApplicationModule(am,false);
    super.finalize();
  }*/

    /**
     * Filtro i viewobject a priori per accelerare i tempi di esecuzione
     * @param tpscr
     * @param ulss
     * @param codts
     */
    public void filter(String codts, String ulss, String tpscr) {
        //soggetto
        this.soggetto_vo.setWhereClause("CODTS='" + codts + "' AND A_SoSoggetto.ULSS='" + ulss + "'");
        this.soggetto_vo.executeQuery();

        //inviti del soggetto
        this.invito_vo.setWhereClause("CODTS='" + codts + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'");
        this.invito_vo.executeQuery();


    }

    /**
     * Metodo che esegue la generazione di un nuovo invito in tutti i suoi passi:
     * 1- creazione ed inserimento dell'invito
     * 2- creazione e memorizzaizone della lettera d'invito
     * 3- aggiornamento dati soggetto
     * 4- annullamnerto dell'eventuale esclusione temporanea attiva (20110411 Serra)
     * @return
     * @param riferimento data di riferiemnto per i confronti sulle eta'

     * @param invito_attivo id dell'invito attivo o null se e' un primo invito
     * @param soggetto id del soggetto da invitare
     * @param slot id dello slot in cui sistemare l'appuntamento
     * @param tpinvito tipo di invito
     * @param comune comune di riferimento per il round
     * @param user utente connesso
     * @param tpscr tipo di screening
     * @param ulss azienda sanitaria
     */
    public boolean generaInvito(String ulss, String tpscr, String user, String tpinvito, Integer slotId,
                                String soggettoId, Integer invito_attivo, Date riferimento, Integer centro,
                                boolean virtuale) throws Exception {
        return this.generaInvito(ulss, tpscr, user, tpinvito, slotId, soggettoId, invito_attivo, riferimento, centro,
                                 virtuale, null);
    }


    /**
     * Metodo che esegue la generazione di un nuovo invito in tutti i suoi passi:
     * 1- creazione ed inserimento dell'invito
     * 2- creazione e memorizzaizone della lettera d'invito
     * 3- aggiornamento dati soggetto
     * 4- annullamnerto dell'eventuale esclusione temporanea attiva (20110411 Serra)
     * @return
     * @param riferimento data di riferiemnto per i confronti sulle eta'

     * @param invito_attivo id dell'invito attivo o null se e' un primo invito
     * @param soggetto id del soggetto da invitare
     * @param slot id dello slot in cui sistemare l'appuntamento
     * @param tpinvito tipo di invito
     * @param comune comune di riferimento per il round
     * @param user utente connesso
     * @param tpscr tipo di screening
     * @param ulss azienda sanitaria
     * @param tipo test citologico proposto
     */
    public boolean generaInvito(String ulss, String tpscr, String user, String tpinvito, Integer slotId,
                                String soggettoId, Integer invito_attivo, Date riferimento, Integer centro,
                                boolean virtuale, String tipo_test) throws Exception {

        //leggo e alcuni dati di utilita' comune
        this.filter(soggettoId, ulss, tpscr);
        //il soggetto

        //20110411 Serra
        this.esclusioni_vo.setWhereClause("CODTS='" + soggettoId + "' AND Sogg_SoEsclusione.ULSS='" + ulss +
                                          "' AND Sogg_SoEsclusione.TPSCR='" + tpscr + "' and ULTIMA=1");
        this.esclusioni_vo.executeQuery();
        //fine 20110411

        /*MOD20071107
   * if(this.soggetto_vo.getRowCount()==0)
       throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda anitaria "+ulss);
    A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)this.soggetto_vo.first();
   */
        A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) this.soggetto_vo.first();
        if (soggetto == null)
            throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId +
                                " non e' stato trovato nell'azienda anitaria " + ulss);

        //l'invito attivo
        A_SoInvitoViewRow invito;
        if (invito_attivo != null) {
            Row[] result = this.invito_vo.getFilteredRows("Idinvito", invito_attivo);
            if (result.length == 0)
                throw new Exception("Invito precedentemente attivo (id=" + invito_attivo + ") non trovato");
            invito = (A_SoInvitoViewRow) result[0];
        } else
            invito = null;

        //controllo se il tipo di invito richiede un appuntamento

        this.tpinvito_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND IDTPINVITO='" + tpinvito +
                                        "'");
        this.tpinvito_vo.executeQuery();
        /*MOD20071107
   * if(this.tpinvito_vo.getRowCount()==0)
      throw new Exception("Tipo di invito con codice "+tpinvito+" non trovato per la ulss "+ulss);
    A_SoCnfTpinvitoViewRow tpInvito=(A_SoCnfTpinvitoViewRow)this.tpinvito_vo.first();
    */
        A_SoCnfTpinvitoViewRow tpInvito = (A_SoCnfTpinvitoViewRow) this.tpinvito_vo.first();
        if (tpInvito == null)
            throw new Exception("Tipo di invito con codice " + tpinvito + " non trovato per la ulss " + ulss);

        boolean sollecito = false;
        if (tpInvito.getAppuntamento() == null || tpInvito.getAppuntamento().intValue() != 1)
            sollecito = true;


        //se si, controllo che l'invito attivo sia null
        if (sollecito && invito_attivo == null)
            throw new Exception("Impossibile generare un sollecito privo di appuntamento senza un invito attivo valido");

        A_SoAppuntamentoViewRow slot;
        if (slotId != null) {
            //lo slot
            this.slot_vo.setWhereClause("IDAPP=" + slotId);
            this.slot_vo.executeQuery();
            /*MOD20071107
     * if(this.slot_vo.getRowCount()==0)
       throw new Exception("Slot con id="+slotId+" non trovato nella tabella SO_APPUNTAMENTO");
     slot=(A_SoAppuntamentoViewRow)this.slot_vo.first();
     */
            slot = (A_SoAppuntamentoViewRow) this.slot_vo.first();
            if (slot == null)
                throw new Exception("Slot con id=" + slotId + " non trovato nella tabella SO_APPUNTAMENTO");

        } else
            slot = null;

        if (!sollecito && slot == null)
            throw new Exception("Per generare un invito con appuntamento e' necessario fornire i dati dell'appuntamento");

        try {

            //step 1: aggiornamento dati invito attivo e posizione di screening
            //il funzionamento deve dipendere dal fatto che sia un sollecito oppure no
            if (invito_attivo != null)
                this.updateInvitoAttivo(invito, user);

            Integer idinvito, idallegato;

            //step 3: creazione e salvataggio dell'invito, se non e' un sollecito senza app
            /*   if(!sollecito)
     idinvito=this.creaInvito(ulss,tpscr,user,tpInvito,slot,soggetto,riferimento,invito);
    else
    //altrimeti l'invito cui fare riferimento e' quello attivo
     idinvito=invito.getIdinvito();*/
            //modifica per inviti senza app: genero comunque l'invito, ma senza slot
            //controllo se si deve generare l'otp
            ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
            String generazOtp = null;
            try {
                generazOtp = ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_OTP);
            } catch (Exception ex) {
                generazOtp = "N"; //se il parametro non e' confiogurato si lavora in modo standard
            }

            A_SoInvitoViewRow new_invito =
                this.creaInvito(ulss, tpscr, user, tpInvito, slot, soggetto, riferimento, invito, centro,
                                "S".equals(generazOtp));
            idinvito = new_invito.getIdinvito();

            //modifica per il colon e per il mammo: in questo caso bisogna generare un record di
            //accettazione con il codice corretto
            BigDecimal campione = null;
            BigDecimal codiceRichiesta = null;
            if ("CO".equals(tpscr)) { //18122013 Gaion: modifiche per gestire la generazione codice richiesta per 2 livelli
                int livello = new_invito.getLivello().intValue();
                if (livello == 1) {
                    campione = this.insertAccColon(user, new_invito);
                }
                if (livello == 2) {
                    String prodNrich2 =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA2);
                    if ("S".equals(prodNrich2)) {
                        InvitoUtils.gesAccColon(am, idinvito, new_invito.getDtapp(), livello,
                                                new_invito.getIdcentroref1liv(), user, new_invito.getUlss(), tpscr);
                    }
                }
                //fine 18122013
            }


            if ("MA".equals(tpscr)) {

                int livello = new_invito.getLivello().intValue();
                String prodNrich = "";
                String prodNrich2 = "";

                if (livello == 1)
                    prodNrich =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
                if (livello == 2)
                    prodNrich2 =
                        ParametriSistema.getParamValue(voParam, ulss, tpscr,
                                                       ConfigurationConstants.PARAMETRO_PROD_RICHIESTA2);

                if ((livello == 1 && "S".equals(prodNrich)) || (livello == 2 && "S".equals(prodNrich2))) {
                    InvitoUtils.gesAccMammo(am, idinvito, new_invito.getDtapp(), livello, new_invito.getUlss(),
                                            new_invito.getIdcentroref1liv(), user, tpscr);
                }
            }

            // mauro 7-10-2008: modifica per hpv: generazione codice richiesta e campione
            if ("CI".equals(tpscr)) {
                Integer idCt = new_invito.getIdcentroprelievo();
                Integer ctRef = SoggUtils.getCtRef(am, idCt);

                // 02012012 gaion recupero del parametro per passarlo a invitoutils
                Boolean hpvParam = null;
                try {
                    String hpv = ParametriSistema.getParamValue(voParam, ulss, tpscr, "hpv");
                    if (hpv.equals("S")) {
                        hpvParam = Boolean.valueOf(true);
                    } else
                        hpvParam = Boolean.valueOf(false);
                } catch (Exception e) {
                    hpvParam = Boolean.valueOf(false);
                }
                // fine 02012012

                //09122014 HPV-DNA
                if (tipo_test != null && !"".equals(tipo_test)) {
                    new_invito.setTestProposto(tipo_test);
                    if ("HPV".equals(tipo_test)) {
                        hpvParam = Boolean.valueOf(true);
                    } else {
                        hpvParam = Boolean.valueOf(false);
                    }
                }

                // mauro 29/03/2010: recupero campione per metterlo su lettera
                A_SoAccCito1livView1Row r =
                    InvitoUtils.gesAccCito(am, idinvito, new_invito.getDtapp(), new_invito.getLivello().intValue(),
                                           ctRef, user, new_invito.getUlss(), tpscr, hpvParam, true,
                                           new_invito.getTestProposto());

                if (r != null)
                    campione = (BigDecimal) r.getAttribute("Numvetrino");
                // mauro 29/03/2010, fine

            }

            if ("PF".equals(tpscr)) {
                int livello = new_invito.getLivello().intValue();
                if (livello == 1) {

                    A_SoAccPfas1livViewRowImpl accRow = this.insertAccPFAS(user, new_invito);
                    if (accRow != null) {
                        codiceRichiesta = (BigDecimal) accRow.getAttribute("CodRichiesta");
                        campione = (BigDecimal) accRow.getAttribute("CodCampione");
                    }
                }
            }

            //step 4: creazione e salvataggio della lettera (qui non imposto il centro)
            String tesseraSanitaria = SoggUtils.trovaTessera(am, soggetto.getCodts(), soggetto.getUlss());
            idallegato =
                this.creaLettera(ulss, tpscr, tpinvito, soggetto, slot, idinvito, campione, virtuale, tipo_test,
                                 tesseraSanitaria, codiceRichiesta, new_invito.getOtp());

            //20110411 Serra
            //step 5: se esiste un'esclusione attiva temporanea e non annullata, allora la annullo
            Row r = this.esclusioni_vo.first();
            if (r != null) {
                if ("T".equals((String) r.getAttribute("Tpescl")) && r.getAttribute("Dtannull") == null &&
                    ((oracle.jbo.domain.Date) r.getAttribute("FineEscl")).compareTo(DateUtils.getOracleDateNow()) > 0) {
                    r.setAttribute("Dtannull", DateUtils.getOracleDateNow());
                    r.setAttribute("Dtmodescl", DateUtils.getOracleDateNow());
                    r.setAttribute("Opmodifica", user);
                    r.setAttribute("Noteescl", "Annullata automaticamente dalla generazione inviti");
                }

            }
            //fine 20110411
            am.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            //    ex.printStackTrace();
            am.getTransaction().rollback();
            throw ex;
            //   return false;
        }

    }

    /**
     * Inserisce un soggetto tra quelli che hanno a che fare con lo screening
     * @throws java.lang.Exception
     * @return
     * @param invito_attivo
     * @param soggettoId
     * @param tpscr
     * @param ulss
     */
    private A_SoSoggScrViewRow insertIntoScr(String ulss, String tpscr, String soggettoId,
                                             Integer invito_attivo) throws Exception {
        //non e' possibile avere un invito attivo e non esser stati inseriti nello screening
        if (invito_attivo != null)
            throw new Exception("Posizione di screening per il soggetto con tessera sanitaria " + soggettoId +
                                " non trovata");
        A_SoSoggScrViewRow ss = (A_SoSoggScrViewRow) this.sogg_scr_vo.createRow();
        this.sogg_scr_vo.insertRow(ss);
        ss.setCodts(soggettoId);
        ss.setRoundindiv(new Integer(0));
        ss.setRoundinviti(new Integer(0));
        ss.setAltorischio(ConfigurationConstants.DB_FALSE);
        ss.setTpscr(tpscr);
        ss.setUlss(ulss);

        /*20080714 MOD: codice archiviazione

    //se si tratta del mammografico c'e' il codice univoco da generare
    if ("MA".equals(tpscr)){  */
        /*20080714 FINE MOD*/
        //se la ulss adotta tale coice lo genero e memorizzzo

        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");
        String prodNrich = ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_ID_MX);
        if ("S".equals(prodNrich)) {
            this.soggetto_vo.setWhereClause("CODTS='" + soggettoId + "' AND A_SoSoggetto.ULSS='" + ulss + "'");
            this.soggetto_vo.executeQuery();
            /*MOD20071107
             * if(this.soggetto_vo.getRowCount()==0)
                throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda sanitaria "+ulss);
            A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)this.soggetto_vo.first();
            */
            A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) this.soggetto_vo.first();
            if (soggetto == null)
                throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId +
                                    " non e' stato trovato nell'azienda sanitaria " + ulss);

            String s =
                Progressivi.getCodiceIdMx(am, soggetto.getDataNascita(), soggetto.getCognome(), soggetto.getNome());
            ss.setCodidsoggMx(s);
        }
        /*20080714 MOD: codice archiviazione
    }*/
        /*20080714 FINE MOD*/

        return ss;

    }

    /**
     * Crea e inserisce un nuovo invito con i dati di input
     * @param soggettoId identificativo del soggetto cui rivolgere l'invito
     * @param slotId identificativo dello slot in cui inserire l'invito
     * @param tpinvito tipo di invito da creare
     * @param comune comune cui si fa riferimento per il round
     * @param tpscr tipo di screening
     * @param ulss azienda sanitaria di riferimento
     * @param volontaria true se si tratta di un arruolamento volontario,
     * false altrimenti
     * @param riferimento data di riferimento per i confronti di eta'
     */
    private A_SoInvitoViewRow creaInvito(String ulss, String tpscr, String user, A_SoCnfTpinvitoViewRow tpInvito,
                                         A_SoAppuntamentoViewRow slot, A_SoSoggettoViewRow soggetto, Date riferimento,
                                         A_SoInvitoViewRow invito_attivo, Integer centro, boolean tokenOtp) throws Exception

    {
        A_SoInvitoViewRow invito = (A_SoInvitoViewRow) this.invito_vo.createRow();
        this.invito_vo.insertRow(invito);
        //ottengo un nuovo id
        Integer id = ((Parent_AppModule) am).getNextIdInvito();
        invito.setIdinvito(id);
        //il nuovo invito diventera' quello attivo
        invito.setAttivo(ConfigurationConstants.DB_TRUE);
        //imposto il soggetto
        invito.setCodts(soggetto.getCodts());
        if (tokenOtp) {
            String token = getToken(id.longValue());
            invito.setOtp(token);
        }

        //06/06/2006 modifica per inviti senza appuntamento
        //se imposto uno slot, allora e' un invito normale
        if (slot != null) {
            invito.setDaConfermare(ConfigurationConstants.DB_FALSE);
            //dati slot
            invito.setIdcentroprelievo(slot.getIdcentro());
            invito.setIdcentroref1liv(slot.getIdcentroref());
            invito.setDtapp(slot.getDtapp());
            invito.setIdapp(slot.getIdapp());
            invito.setMinapp(slot.getMinapp());
            invito.setOraapp(slot.getOraapp());
            //memorizzo in un campo unico data e ora iniziali dell'appunatmento
            
            Calendar c = Calendar.getInstance(Locale.ITALY);
                c.clear();
                c.setTime(slot.getDtapp().dateValue());
                c.set(11, slot.getOraapp().intValue());
                c.set(12, slot.getMinapp().intValue());
            invito.setDtappiniziale(new oracle.jbo.domain.Date(new java.sql.Timestamp(c.getTime().getTime())));
            //invito.setDtappiniziale(DateUtils.getOracleDateTime(slot.getDtapp(), slot.getOraapp().intValue(),
            //                                                    slot.getMinapp().intValue()));
        } else { //si tratta di un invito senza appuntamento
            invito.setDaConfermare(ConfigurationConstants.DB_TRUE);
            //la data teorica e' quella della craezione
            invito.setDtapp(DateUtils.getOracleDateNow());
            //il centro di prelievo e' quello previsto dal richiamo, tranne per i solleciti
            invito.setIdcentroprelievo(centro);
            //cerco anche il centro di refertazione associato
            this.centro_vo.setWhereClause("IDCENTRO=" + centro);
            this.centro_vo.executeQuery();
            Row c = this.centro_vo.first();
            if (c == null)
                throw new Exception("Nessun centro trovato con codice " + centro);
            invito.setIdcentroref1liv((Integer) c.getAttribute("Idcentroref"));
        }
        //fine modifica

        invito.setConfermato(new Number(0));
        invito.setConsenso(null);
        invito.setCodesitoinvito(ConfigurationConstants.CODICE_ESITO_NON_DISPONIBILE);
        invito.setLivesito(tpInvito.getLivello());
        invito.setLivello(tpInvito.getLivello());


        //imposto il tipo di invito
        invito.setIdtpinvito(tpInvito.getIdtpinvito());
        //leggo ed imposto il round in cui viene chiamata:
        Integer comuneId = this.getComuneId(soggetto);

        /////////////////////////////
        //dati di riferimento per le statistiche
        //comune o stato di residenza
        invito.setCodresidenza(soggetto.getCodResidenza());

        //comune o stato di domicilio
        invito.setCoddomicilio(soggetto.getCodDomicilio());

        //indirizo di residenza
        invito.setViaresidenza(soggetto.getIndirizzoRes());

        //indirizzo di domicilio
        invito.setViadomicilio(soggetto.getIndirizzoDom());

        //comuni
        invito.setReleaseCodeComDom(soggetto.getReleaseCodeComDom());
        invito.setReleaseCodeComRes(soggetto.getReleaseCodeComRes());

        //stato anagrafico
        invito.setStatoanag(soggetto.getCodanagreg());

        //ZONA
        invito.setZona(soggetto.getCoddistrzona());

        //////////////////////////

        //se si tratta di un primo invito uso il round del comune:
        Integer id_att = invito_attivo != null ? invito_attivo.getIdinvito() : null;
        invito.setRoundcomune(this.getRoundComune(ulss, tpscr, comuneId, tpInvito, invito_attivo));
        //imposto se e' volontaria
        invito.setVolontaria(this.getVolontaria(tpInvito, invito_attivo));

        //fuori fascia
        invito.setFuorifascia(this.getFuorifascia(tpscr, soggetto, ulss, riferimento, invito_attivo));

        //info varie
        invito.setDtcreazione(DateUtils.getOracleDateNow());
        invito.setDtultimamod(invito.getDtcreazione());
        invito.setOpinserimento(user);
        invito.setOpmodifica(user);
        invito.setTpscr(tpscr);
        invito.setUlss(ulss);

        //vedo se la donna e' inserita nello screening
        A_SoSoggScrViewRow soggscr = this.insertIntoScreeningIfNecessary(tpscr, ulss, soggetto.getCodts(), id_att);

        //posizione di screening (aggiornamento)
        invito.setRoundinviti(this.updateAndGetRoundInviti(soggscr,
            //invito.getRoundcomune(),
            tpInvito, invito.getIdinvito()));
        this.updateRoundIndiv(soggscr, invito);
        invito.setRoundindiv(this.getRoundIndividuale(soggscr));

        invito.setAttribute("Codiceregmedico", soggetto.getCodiceregmedico());

        String classePop = soggscr.getCodclassepop();
        invito.setAttribute("Codclassepop", classePop);

        return invito; //.getIdinvito();
    }

    /*
   * crea il pdf e lo memorizza nel record 'recAlleg' passato
   */
    private void creaLetteraPDF(Row recAlleg, String ulss, String tpscr, String tpinvito, A_SoSoggettoViewRow soggetto,
                                A_SoAppuntamentoViewRow slot, BigDecimal campione, String testProposto,
                                String tesseraSanitaria, String otp, BigDecimal codiceRichiesta) throws Exception {
        //se i parametri della ulss non sono ancora stati inseriti, inseriscili
        if (this.ulssParams == null) {
            this.ulssParams = ParametriSistema.getParamTemplate(ulss, tpscr, this.ulss_vo, this.params_vo);
        }

        File pdf = null;

        Date d = slot == null ? new Date() : slot.getDtapp().getValue();
        int eta = ViewHelper.etaCompiuta(d, soggetto.getDataNascita().getValue());
        Integer cc = slot == null ? null : slot.getIdcentro();

        Lettera l = this.createLetteraObject(ulss, tpscr, tpinvito, eta, cc, testProposto);

        //crea il bean data source
        LetteraInvitoBean bean =
            this.createBean(ulss, tpscr, soggetto, slot, campione, tesseraSanitaria, otp, codiceRichiesta);

        //ottieni il pdf
        pdf = l.createLetter(bean);

        recAlleg.setAttribute("Lettera", BlobUtils.getBlobFromFile(pdf));

        pdf.delete();

    }

    /* metodo pubblico
   * crea il pdf e lo memorizza nel record 'recAlleg' passato
   */

    public void creaLetteraPDF(Row recAlleg, String ulss, String tpscr, String tpinvito, String soggettoId,
                               Integer slotId, Integer invito, String testProposto) throws Exception {


        /* MOD20071107
    * if(this.soggetto_vo.getRowCount()==0)
       throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda anitaria "+ulss);
    A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)this.soggetto_vo.first();
    */
        A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) this.soggetto_vo.first();
        if (soggetto == null)
            throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId +
                                " non e' stato trovato nell'azienda anitaria " + ulss);

        A_SoAppuntamentoViewRow slot = null;
        if (slotId != null) {
            this.slot_vo.setWhereClause("IDAPP=" + slotId);
            this.slot_vo.executeQuery();
            /*MOD20071107
         * if(this.slot_vo.getRowCount()==0)
           throw new Exception("Slot con id="+slotId+" non trovato nella tabella SO_APPUNTAMENTO");
         slot=(A_SoAppuntamentoViewRow)this.slot_vo.first();
         */
            slot = (A_SoAppuntamentoViewRow) this.slot_vo.first();
            if (slot == null)
                throw new Exception("Slot con id=" + slotId + " non trovato nella tabella SO_APPUNTAMENTO");
        }

        //cerco il numero di campione e il codice richiesta
        BigDecimal campione = null;
        BigDecimal codiceRichiesta = null;
        if ("CO".equals(tpscr)) {
            this.accColon1l_vo.setWhereClause("IDINVITO=" + invito);
            this.accColon1l_vo.executeQuery();
            A_SoAccColon1livViewRow r = (A_SoAccColon1livViewRow) this.accColon1l_vo.first();
            if (r != null)
                campione = r.getCodCampione();
        } else if ("CI".equals(tpscr)) {
            this.accCito1l_vo.setWhereClause("IDINVITO=" + invito);
            this.accCito1l_vo.executeQuery();
            Row r = this.accCito1l_vo.first();
            if (r != null)
                campione = (BigDecimal) r.getAttribute("Numvetrino");
        } else if ("PF".equals(tpscr)) {
            this.accPfas1l_vo.setWhereClause("IDINVITO=" + invito);
            this.accPfas1l_vo.executeQuery();
            Row r = this.accPfas1l_vo.first();
            if (r != null) {
                campione = (BigDecimal) r.getAttribute("CodCampione");
                codiceRichiesta = (BigDecimal) r.getAttribute("CodRichiesta");
            }
        }
        //cerco l'otp
        String otp = null;
        Row[] result = this.invito_vo.getFilteredRows("Idinvito", invito);
        if (result[0] != null) {
            otp = (String) result[0].getAttribute("Otp");
        }

        this.creaLetteraPDF(recAlleg, ulss, tpscr, tpinvito, soggetto, slot, campione, testProposto, soggettoId, otp,
                            codiceRichiesta);
    }

    /**
     * Crea e memorizza la laettera di invito
     * @throws java.lang.Exception
     * @return
     * @param invito id invito cui e' relativa la lettera
     * @param slotId slot imn cui e' stato fissato l'appuntamento
     * @param soggettoId soggetto invitato
     * @param tpinvito tipo di invito
     * @param tpscr
     * @param ulss
     */
    private Integer creaLettera(String ulss, String tpscr, String tpinvito, A_SoSoggettoViewRow soggetto,
                               A_SoAppuntamentoViewRow slot, Integer invito, BigDecimal campione, boolean virtuale,
                               String tipo_test, String tesseraSanitaria, BigDecimal codiceRichiesta,
                               String otp) throws Exception {
        //se i parametri della ulss non sono ancora stati inseriti, inseriscili
        if (this.ulssParams == null) {
            this.ulssParams = ParametriSistema.getParamTemplate(ulss, tpscr, this.ulss_vo, this.params_vo);
        }

        //se non e' ancora stato creato l'oggetto lettera per quel tipo di invito, crealo
        /*20080721 MOD: lettere per eta e centro
   Lettera l=(Lettera)this.reports.get(tpinvito);
   if(l==null)
   {
     //crea il report e lo memorizza

        l=this.createLetteraObject(ulss,tpscr,tpinvito);
       * */
        //calcolo l'eta' del soggetto alla data dell'invito o al momento attuale

        // mauro 25/11/2010, se virtuale = true non devo generare la lettera
        File pdf = null;

        if (!virtuale) {

            Date d = slot == null ? new Date() : slot.getDtapp().getValue();
            int eta = ViewHelper.etaCompiuta(d, soggetto.getDataNascita().getValue());
            Integer cc = slot == null ? null : slot.getIdcentro();

            Lettera l = this.createLetteraObject(ulss, tpscr, tpinvito, eta, cc, tipo_test);

            /* this.reports.put(tpinvito,l);
        }
        20080721 FINE MOD*/

            //crea il bean data source
            LetteraInvitoBean bean =
                this.createBean(ulss, tpscr, soggetto, slot, campione, tesseraSanitaria, otp, codiceRichiesta);

            //ottieni il pdf
            pdf = l.createLetter(bean);

        }

        //crea l'allegato
        A_SoAllegatoViewRow allegato = (A_SoAllegatoViewRow) this.allegato_vo.createRow();
        this.allegato_vo.insertRow(allegato);

        //leggo l'id dell'allegato
        Integer id = ((Parent_AppModule) am).getNextIdAllegato();
        allegato.setIdallegato(id);
        allegato.setCodts(soggetto.getCodts());
        allegato.setDtcreazione(DateUtils.getOracleDateNow());
        allegato.setIdinvito(invito);
        allegato.setTpscr(tpscr);
        allegato.setUlss(ulss);

        if (!virtuale) {
            allegato.setLettera(BlobUtils.getBlobFromFile(pdf));
            pdf.delete();
        }

        return allegato.getIdallegato();

    }

    public Integer creaLettera(String ulss, String tpscr, String tpinvito, String soggettoId, Integer slotId,
                              Integer invito, String testProposto, String tesseraSanitaria, String otp,
                              BigDecimal codiceRichiesta, BigDecimal campione) throws Exception {

        A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) this.soggetto_vo.first();
        if (soggetto == null)
            throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId +
                                " non e' stato trovato nell'azienda anitaria " + ulss);

        A_SoAppuntamentoViewRow slot = null;
        if (slotId != null) {
            this.slot_vo.setWhereClause("IDAPP=" + slotId);
            this.slot_vo.executeQuery();

            slot = (A_SoAppuntamentoViewRow) this.slot_vo.first();
            if (slot == null)
                throw new Exception("Slot con id=" + slotId + " non trovato nella tabella SO_APPUNTAMENTO");
        }

        Integer esito =
            this.creaLettera(ulss, tpscr, tpinvito, soggetto, slot, invito, campione, false, testProposto,
                             tesseraSanitaria, codiceRichiesta, otp);
        this.releaseLogo();
        return esito;

    }

    public Integer creaLettera(String ulss, String tpscr, String tpinvito, String soggettoId, Integer slotId,
                              Integer invito, String testProposto, String tesseraSanitaria,
                              String otp) throws Exception {


        /* MOD20071107
    * if(this.soggetto_vo.getRowCount()==0)
       throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda anitaria "+ulss);
    A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)this.soggetto_vo.first();
    */
        A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) this.soggetto_vo.first();
        if (soggetto == null)
            throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId +
                                " non e' stato trovato nell'azienda anitaria " + ulss);

        A_SoAppuntamentoViewRow slot = null;
        if (slotId != null) {
            this.slot_vo.setWhereClause("IDAPP=" + slotId);
            this.slot_vo.executeQuery();
            /*MOD20071107
         * if(this.slot_vo.getRowCount()==0)
           throw new Exception("Slot con id="+slotId+" non trovato nella tabella SO_APPUNTAMENTO");
         slot=(A_SoAppuntamentoViewRow)this.slot_vo.first();
         */
            slot = (A_SoAppuntamentoViewRow) this.slot_vo.first();
            if (slot == null)
                throw new Exception("Slot con id=" + slotId + " non trovato nella tabella SO_APPUNTAMENTO");
        }

        //cerco il numero di campione e il codice richiesta
        BigDecimal campione = null;
        BigDecimal codiceRichiesta = null;
        if ("CO".equals(tpscr)) {
            this.accColon1l_vo.setWhereClause("IDINVITO=" + invito);
            this.accColon1l_vo.executeQuery();
            A_SoAccColon1livViewRow r = (A_SoAccColon1livViewRow) this.accColon1l_vo.first();
            if (r != null)
                campione = r.getCodCampione();
        } else if ("CI".equals(tpscr)) {
            this.accCito1l_vo.setWhereClause("IDINVITO=" + invito);
            this.accCito1l_vo.executeQuery();
            Row r = this.accCito1l_vo.first();
            if (r != null)
                campione = (BigDecimal) r.getAttribute("Numvetrino");
        } else if ("PF".equals(tpscr)) {
            this.accPfas1l_vo.setWhereClause("IDINVITO=" + invito);
            this.accPfas1l_vo.executeQuery();
            Row r = this.accPfas1l_vo.first();
            if (r != null) {
                campione = (BigDecimal) r.getAttribute("CodCampione");
                codiceRichiesta = (BigDecimal) r.getAttribute("CodRichiesta");
            }
        }

        // mauro 10/02/2010 rimozione file logo
        Integer esito =
            this.creaLettera(ulss, tpscr, tpinvito, soggetto, slot, invito, campione, false, testProposto,
                             tesseraSanitaria, codiceRichiesta, otp);
        this.releaseLogo();
        return esito;
        // mauro 10/02/2010, fine
    }

    /**
     * Creo l'oggetto lettera (con l amappa parametri ed il template compilato)
     * per la ulss ed il tipo di invito di input
     * @throws java.lang.Exception
     * @return
     * @param tpinvito
     * @param tpscr
     * @param ulss
     */
    private Lettera createLetteraObject(String ulss, String tpscr, String tpinvito,
                                        /*20080721 MOD: lettere per eta e centro*/
                                        int eta, Integer centro,
                                        /*20080721 FINE MOD*/
                                        String tipo_test) throws Exception {
        //cerco qual e' il template da usare in questo caso
        /*20080721 MOD: lettere per eta e centro
      this.lettera_vo.setWhereClause("IDTPINVITO='"+tpinvito+"' AND ULSS='"+ulss+"' AND TPSCR='"+tpscr+"'");
      */String wh =
            "IDTPINVITO='" + tpinvito + "' AND ULSS='" + ulss + "' AND TPSCR='" + tpscr + "'" +
            " and   nvl(eta_da,0)<=" + eta + " and   nvl(eta_a,200)>=" + eta;
        if (centro != null)
            wh += " and nvl(centro," + centro + ")=" + centro;
        //09122014 HPV-DNA
        if (tipo_test != null)
            wh += " and (TEST_PROPOSTO ='" + tipo_test + "' OR TEST_PROPOSTO IS null) ";
        //fine 09122014
        this.lettera_vo.setWhereClause(wh);
        //ordino in base al criterio dell'eta' 8prendo il range piu' piccolo che la contiene)
        //e poi, eventualmente, per centro
        this.lettera_vo.setOrderByClause("(" + eta + "-nvl(eta_da,0))+(nvl(eta_a,200)-" + eta + "),centro");
        // System.out.println(this.lettera_vo.getQuery());
        /*20080721 FINE MOD*/
        this.lettera_vo.executeQuery();
        /*MOD20071107
       * if(this.lettera_vo.getRowCount()==0)
        throw new Exception("Non e' stato selezionato il template da usare per l'azienda sanitaria "+ulss+" ed il tipo di invito con codice "+tpinvito);
      A_SoCnfLetteracentroViewRow conf=(A_SoCnfLetteracentroViewRow)this.lettera_vo.first();
      */
        A_SoCnfLetteracentroViewRow conf = (A_SoCnfLetteracentroViewRow) this.lettera_vo.first();
        if (conf == null)
            throw new Exception("Non e' stato selezionato il template da usare per l'azienda sanitaria " + ulss +
                                " ed il tipo di invito con codice " + tpinvito);

        Integer t = conf.getCodtempl();
        /*20080721 MOD: lettere per eta e centro*/
        //verifico se il template desiderato l'ho gia' memorizzato
        Lettera l = (Lettera) this.reports.get(t);
        if (l != null)
            return l;

        /*20080721 FINE MOD*/


        //vado a recuperare il template
        this.template_vo.setWhereClause("CODTEMPL=" + t);
        this.template_vo.executeQuery();
        /*MOD20071107
       * if(this.template_vo.getRowCount()==0)
        throw new Exception("Il template con codice "+t+" non e' stato trovato nella tabella SO_TEMPLATE");
      A_SoTemplateViewRow template=(A_SoTemplateViewRow)this.template_vo.first();
      *******/
        A_SoTemplateViewRow template = (A_SoTemplateViewRow) this.template_vo.first();
        if (template == null)
            throw new Exception("Il template con codice " + t + " non e' stato trovato nella tabella SO_TEMPLATE");

        //creo l'oggetto lettera corrispondente
        //  Lettera l=new Lettera(template.getFilexml(),"Invito"+tpinvito);
        l = new Lettera(template, "A_SoTemplateView1", "Filexml", "Compiled", "Invito" + tpinvito,
                        ConfigurationConstants.FORMATO_PDF);
        l.setParametersMap(this.ulssParams);

        /*20080721 MOD: lettere per eta e centro*/
        //memorizzo il tenmplate utilizzato
        this.reports.put(t, l);
        /*20080721 FINE MOD*/

        return l;
    }

    /**
     * Crea ed inizializza un bean che serve da sorgente dati per una
     * lettera di invito
     * @throws java.lang.Exception
     * @return
     * @param slotId
     * @param soggettoId
     * @param comuneId
     * @param tpscr
     * @param ulss
     */
    private LetteraInvitoBean createBean(String ulss, String tpscr, A_SoSoggettoViewRow soggetto,
                                         A_SoAppuntamentoViewRow slot, BigDecimal campione, String tesseraSanitaria,
                                         String otp, BigDecimal codiceRichiesta) throws Exception {

        //medico di base (se aderisce)
        String med = null;
        String att =
            (String) ViewHelper.decodeByTpscr(tpscr, "Dtadesione", "DtadesCo", "DtadesMa", "DtadesCa", "DtadesPf");
        if (soggetto.getAttribute(att) != null)
            med = soggetto.getNome1() + " " + soggetto.getCognome1();


        //se lo slot e' null si tratta di una lettera senza appuntamento
        String centro, ind_centro;
        /*20080718 MOD: dati centro nelle stampe*/
        String sede, orari, tel_centro;
        /*20080718 FINE MOD*/
        if (slot != null) {
            centro = slot.getDescrizione();
            ind_centro = slot.getIndirizzoRes();
            /*20080718 MOD: dati centro nelle stampe*/
            sede = slot.getSede();
            orari = slot.getOraritel();
            tel_centro = slot.getTel();
            /*20080718 FINE MOD*/
        } else {
            centro = null;
            ind_centro = null;
            /*20080718 MOD: dati centro nelle stampe*/
            sede = null;
            orari = null;
            tel_centro = null;
            /*20080718 FINE MOD*/
        }
        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");

        //24112011 GAION: aggiunto parametro per il calcolo dell'indirizzo
        String mod_spedizione = null;
        try {

            mod_spedizione = ParametriSistema.getParamValue(voParam, ulss, tpscr, "mod_spedizione");
        } catch (Exception ex) {
            mod_spedizione = "standard"; //se il parametro non e' confiogurato si lavora in modo standard
        }
        //24112011 fine

        String indirizzo, comune, cap, provincia;
        //c'e' un indirizzo di screening
        if (soggetto.getIndirizzoScr() != null && soggetto.getIndirizzoScr().trim().length() > 0) {
            indirizzo = soggetto.getIndirizzoScr();
            comune = soggetto.getDescrizione();
            cap = soggetto.getCap();
            provincia = soggetto.getCodpr();
        }
        //c'e' un indirizzo di domicilio
        //24112011 GAION: aggiunto parametro per il calcolo dell'indirizzo
        else if (mod_spedizione.equalsIgnoreCase("standard") && soggetto.getIndirizzoDom() != null &&
                 soggetto.getIndirizzoDom().trim().length() > 0) {
            indirizzo = soggetto.getIndirizzoDom();
            comune = soggetto.getDescrizione1();
            cap = soggetto.getCap1();
            provincia = soggetto.getCodpr1();
        }
        //c'e' un indirizzo di residenza
        else if (soggetto.getIndirizzoRes() != null && soggetto.getIndirizzoRes().trim().length() > 0) {
            indirizzo = soggetto.getIndirizzoRes();
            comune = soggetto.getDescrizione2();
            cap = soggetto.getCap2();
            provincia = soggetto.getCodpr2();
        } else {
            indirizzo = null;
            comune = null;
            cap = null;
            provincia = null;
        }

        String altro = null;
        if (ConfigurationConstants.CODICE_DOMICILIATO.equals(soggetto.getCodanagreg())) {
            this.comzonepar_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND RELEASE_CODE_COM='" +
                                              soggetto.getReleaseCodeComDom() + "'");
        } else {
            this.comzonepar_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND RELEASE_CODE_COM='" +
                                              soggetto.getReleaseCodeComRes() + "'");
        }
        this.comzonepar_vo.executeQuery();
        if (this.comzonepar_vo.getRowCount() > 1)
            throw new Exception("Troppi dati di configurazione associati al comune di riferimento del soggetto");
        A_SoCnfComuniZoneParamViewRow cr = (A_SoCnfComuniZoneParamViewRow) this.comzonepar_vo.first();
        if (cr != null)
            altro = cr.getAltro();
        else { //provo con la zona
            this.comzonepar_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND CODDISTRZONA=" +
                                              soggetto.getCoddistrzona() + "");
            this.comzonepar_vo.executeQuery();
            if (this.comzonepar_vo.getRowCount() > 1)
                throw new Exception("Troppi dati di configurazione associati alla zona di riferimento del soggetto");
            cr = (A_SoCnfComuniZoneParamViewRow) this.comzonepar_vo.first();
            if (cr != null)
                altro = cr.getAltro();

        }

        /*02052013 GAION:progetto 45 mx*/
        String braccio45mx = null;
        String codice45mx = null;
        if ("MA".equals(tpscr)) {
            try {
                String studio45mx =
                    ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_STUDIO_45_MX);
                if (studio45mx != null && "S".equals(studio45mx)) {
                    this.sogg_scr_vo.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' AND CODTS='" +
                                                    soggetto.getCodts() + "'");
                    this.sogg_scr_vo.executeQuery();
                    A_SoSoggScrViewRow soggscr = (A_SoSoggScrViewRow) this.sogg_scr_vo.first();
                    if (soggscr != null) {
                        braccio45mx = soggscr.getMx45Braccio();
                        codice45mx = soggscr.getMx45Codice();
                    }
                }
            } catch (Exception ex) {
            }
        }
        /* FINE 02052013*/


        LetteraInvitoBean lb =
            new LetteraInvitoBean(soggetto.getCognome(), soggetto.getNome(), indirizzo, cap, comune, //comune
                                                     provincia, DateUtils.getToday(), centro, //centro
                                                     ind_centro,
                                  slot == null ? null : DateUtils.dateToString(slot.getDtapp().dateValue()),
                                  slot == null ? null : ViewHelper.formatTime(slot.getOraapp(), slot.getMinapp()), med,
                                  null, campione == null ? null : campione.toString(), //codice campione
                                  altro,
                                  soggetto.getDataNascita() != null ?
                                  DateUtils.dateToString(soggetto.getDataNascita().dateValue()) : null, tesseraSanitaria,
                                  soggetto.getCf(),
                                  /*20080718 MOD: dati centro nelle stampe*/
                                  sede, orari, tel_centro,
                                  /*20080718 FINE MOD*/
                                  braccio45mx, codice45mx, soggetto.getEmail(), soggetto.getCellulare(), otp,
                                  codiceRichiesta == null ? null : codiceRichiesta.toString());
        return lb;

    }

    /**
     * Aggiorna i dati dell'ex-invito attivo per rispecchiare l'attuale situaizone
     * @throws java.lang.Exception
     * @param user
     * @param idinvito
     */
    private void updateInvitoAttivo(A_SoInvitoViewRow invito, String user) throws Exception {
        /* if(sollecito)
      //se e' un sollecito l'invito rimane attivo, viene solo aggiornato
      invito.setDtinviosollecito(DateUtils.getOracleDate(new Date()));
    else{//altrimenti...*/


        //06/06/2006 modifica per inviti senza appuntamento:
        //l'ultimo invito non e' piu' quello attivo SEMPRE
        //non e' piu' quello attivo
        invito.setAttivo(ConfigurationConstants.DB_FALSE);
        //ho generato il richiamo
        invito.setDtinviorichiamo(DateUtils.getOracleDate(new Date()));
        // }
        //comunque registro che ho operato delle modifiche
        invito.setDtultimamod(DateUtils.getOracleDate(new Date()));
        invito.setOpmodifica(user);


    }


    /**
     * Aggiorna i dati dell'ex-invito attivo per rispecchiare l'attuale situaizone
     * @throws java.lang.Exception
     * @param tpscr
     * @param ulss
     * @param tpinvito
     * @param user
     * @param invito_attivo
     */
    public void updateInvitoAttivo(Integer invito_attivo, String user) throws Exception {

        /*   this.tpinvito_vo.setWhereClause("ULSS='"+ulss+"' AND TPSCR='"+tpscr+"' AND IDTPINVITO='"+tpinvito+"'");
    this.tpinvito_vo.executeQuery();
    if(this.tpinvito_vo.getRowCount()==0)
      throw new Exception("Tipo di invito con codice "+tpinvito+" non trovato per la ulss "+ulss);
    A_SoCnfTpinvitoViewRow tpInvito=(A_SoCnfTpinvitoViewRow)this.tpinvito_vo.first();
    boolean sollecito=false;
    if(tpInvito.getAppuntamento().intValue()!=1)
       sollecito=true;*/

        A_SoInvitoViewRow invito_att;

        Row[] result = this.invito_vo.getFilteredRows("Idinvito", invito_attivo);
        if (result.length == 0)
            throw new Exception("Invito precedentemente attivo (id=" + invito_attivo + ") non trovato");
        invito_att = (A_SoInvitoViewRow) result[0];


        this.updateInvitoAttivo(invito_att, user);


    }


    /**
   * Aggiorna il numero di primi inviti (lo incrementa solo se sto inserendo un primo invito o
   * un accesso spontaneo)
   * @param tpinvito
   * @param soggscr
   */
    /* private void updateScreeningPos(A_SoSoggScrViewRow soggscr,
 // Integer roundcomune,
  A_SoCnfTpinvitoViewRow tpinvito
  )
  {
   if(ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.equals(tpinvito.getIdcateg())
   || ConfigurationConstants.CODICE_CAT_VOLONTARIO.equals(tpinvito.getIdcateg()))
    {//solo se si tratta di un primo invito ne incremento il numero
      if(soggscr.getRoundinviti()==null)
        soggscr.setRoundinviti(new Integer(1));
      else
      soggscr.setRoundinviti(soggscr.getRoundinviti().add(1));

    }
 /*   String where=invito_vo.getWhereClause();
    this.invito_vo.setWhereClause("ULSS='"+soggscr.getUlss()+"' AND TPSCR='"+soggscr.getTpscr()+
    "' AND CODTS='"+soggscr.getCodts()+"' AND ROUNDCOMUNE="+roundcomune);
    invito_vo.executeQuery();
    if(invito_vo.getRowCount()==0)
    {
      //primo invito che riceve in quetso round, round inviti va incrementato
      if(soggscr.getRoundinviti()==null)
        soggscr.setRoundinviti(new Integer(1));
      else
       soggscr.setRoundinviti(soggscr.getRoundinviti().add(1));
    }
    //altrimenti non faccio nulla

    invito_vo.setWhereClause(where);
    invito_vo.executeQuery();
    */
    // }

    /**
     * Calcolo il numero di roundinviti (0numero primi inviti+ numero accessi spontanei)
     * e lo aggoirno in soggscr
     * @param idinvito identificativo del'invito su cui sto lavorando,
     * serve per escluderlo dai calcoli su database
     * @param tpinvito record con la tipologia di invito che sto creando/modificando
     * @param soggscr posizione di screnig del soggetto
     */
    private void updateScreeningPos(A_SoSoggScrViewRow soggscr, A_SoCnfTpinvitoViewRow tpinvito,
                                    Integer idinvito) {
        //il round inviti e' il mumero di primi inviti e accessi spontanei
        //calcolo quelli del mio soggetto, tranne quello su cui sto lavorando
        String query =
            "SELECT COUNT(*) " + "FROM SO_INVITO JOIN SO_CNF_TPINVITO " +
            "ON SO_INVITO.IDTPINVITO=SO_CNF_TPINVITO.IDTPINVITO  " + "AND SO_INVITO.LIVELLO=SO_CNF_TPINVITO.LIVELLO " +
            "AND SO_INVITO.ULSS=SO_CNF_TPINVITO.ULSS " + "AND SO_INVITO.TPSCR=SO_CNF_TPINVITO.TPSCR " +
            "WHERE (IDCATEG=" + ConfigurationConstants.CODICE_CAT_PRIMO_INVITO + " OR IDCATEG=" +
            ConfigurationConstants.CODICE_CAT_VOLONTARIO + ") " + "AND SO_INVITO.TPSCR='" + soggscr.getTpscr() +
            "' AND CODTS='" + soggscr.getCodts() + "' AND IDINVITO<>" + idinvito + "AND SO_INVITO.ULSS='" +
            soggscr.getUlss() + "'";
        //System.out.println(query);
        ViewObject inviti = null;

        try {
            inviti = this.am.createViewObjectFromQueryStmt("invit_tmp", query);
            // inviti.executeQuery();
            Integer roundinviti = new Integer(0);
            /*MOD20071107
     * if(inviti.getRowCount()>0)
         roundinviti=(Integer)inviti.first().getAttribute(0);
         */
            Row xxx = inviti.first();
            if (xxx != null)
                roundinviti = xxx.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) xxx.getAttribute(0)).intValue() : null;

            if (ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.equals(tpinvito.getIdcateg()) ||
                ConfigurationConstants.CODICE_CAT_VOLONTARIO.equals(tpinvito.getIdcateg()))
                //solo se si tratta di un primo invito ne incremento il numero
                roundinviti = roundinviti + 1;

            soggscr.setRoundinviti(roundinviti);
        } finally {
            if (inviti != null)
                inviti.remove();
        }


    }

    /**
     * metodo che ricava il round comune in cui deve ricadere un invito
     * @throws java.lang.Exception se non viene trovato un round attivo per il
     * comune di riferimento o se non c'e'  l'invito attivo precedente
     * @return roundcomune
     * @param invito_attivo id dell'eventuale invito attivo precedente o null,
     * se l'invito e' il primo in assoluto
     * @param tpinvito tipo di invito
     * @param comuneId id del comune di riferimento
     * @param tpscr tipo di screening
     * @param ulss ulss
     */
    public Integer getRoundComune(String ulss, String tpscr, Integer comuneId, String tpinvito,
                                 Integer invito_attivo) throws Exception {
        this.tpinvito_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND IDTPINVITO='" + tpinvito +
                                        "'");
        this.tpinvito_vo.executeQuery();
        /*MOD20071107
    * if(this.tpinvito_vo.getRowCount()==0)
      throw new Exception("Tipo di invito con codice "+tpinvito+" non trovato per la ulss "+ulss);
    A_SoCnfTpinvitoViewRow tpInvito=(A_SoCnfTpinvitoViewRow)this.tpinvito_vo.first();
   */
        A_SoCnfTpinvitoViewRow tpInvito = (A_SoCnfTpinvitoViewRow) this.tpinvito_vo.first();
        if (tpInvito == null)
            throw new Exception("Tipo di invito con codice " + tpinvito + " non trovato per la ulss " + ulss);

        //se si tratta di un primo invito cerco il round del comune
        if (tpInvito.getIdcateg().equals(ConfigurationConstants.CODICE_CAT_PRIMO_INVITO) ||
            tpInvito.getIdcateg().equals(ConfigurationConstants.CODICE_CAT_VOLONTARIO)) {
            this.round_vo.setWhereClause("CODSCR='" + tpscr + "' AND RELEASE_CODE_COM='" + comuneId +
                                         "' AND NOT(DTINIZIO IS NULL) AND DTFINE IS NULL");
            this.round_vo.executeQuery();
            if (this.round_vo.getRowCount() == 0) {
                ViewObject comune = null;
                try {
                    comune =
                        am.createViewObjectFromQueryStmt("nome_comune",
                                                         "SELECT C.descrizione FROM SO_COMUNE C WHERE C.RELEASE_CODE='" +
                                                         comuneId + "'");
                    if (comune.first() == null)
                        throw new Exception("Non e' stato trovato nessun round attivo per il comune con codice=" +
                                            comuneId);
                    else
                        throw new Exception("Non e' stato trovato nessun round attivo per il comune di " +
                                            comune.first().getAttribute(0));
                } finally {
                    if (comune != null)
                        comune.remove();
                }
            }

            A_SoRoundorgViewRow comune = (A_SoRoundorgViewRow) this.round_vo.first();
            return comune.getNumround();
        } else {
            //cerco l'invito attivo
            //  A_SoInvitoViewRow invito;
            if (invito_attivo != null) {
                Row[] result = this.invito_vo.getFilteredRows("Idinvito", invito_attivo);
                if (result.length == 0)
                    throw new Exception("Invito precedentemente attivo (id=" + invito_attivo + ") non trovato");
                return ((A_SoInvitoViewRow) result[0]).getRoundcomune();
            } else
                throw new Exception("Il soggetto non ha nessun invito attivo e non e' stato possibile trovare il round attivo per il comune con codice=" +
                                    comuneId);
        }

    }


    private Integer getRoundComune(String ulss, String tpscr, Integer comuneId, A_SoCnfTpinvitoViewRow tpinvito,
                                  A_SoInvitoViewRow invito_attivo) throws Exception {

        //se si tratta di un primo invito cerco il round del comune
        if (tpinvito.getIdcateg().equals(ConfigurationConstants.CODICE_CAT_PRIMO_INVITO)) {
            this.round_vo.setWhereClause("CODSCR='" + tpscr + "' AND RELEASE_CODE_COM=" + comuneId +
                                         " AND NOT(DTINIZIO IS NULL) AND DTFINE IS NULL");
            this.round_vo.executeQuery();
            if (this.round_vo.getRowCount() == 0) {
                ViewObject comune = null;
                try {
                    comune =
                        am.createViewObjectFromQueryStmt("nome_comune",
                                                         "SELECT C.descrizione FROM SO_COMUNE C WHERE C.RELEASE_CODE=" +
                                                         comuneId);
                    if (comune.first() == null)
                        throw new Exception("Non e' stato trovato nessun round attivo per il comune con codice=" +
                                            comuneId);
                    else
                        throw new Exception("Non e' stato trovato nessun round attivo per il comune di " +
                                            comune.first().getAttribute(0));
                } finally {
                    if (comune != null)
                        comune.remove();
                }
            }

            A_SoRoundorgViewRow comune = (A_SoRoundorgViewRow) this.round_vo.first();
            return comune.getNumround();
        } else {
            //cerco l'invito attivo
            if (invito_attivo == null)
                throw new Exception("Nessun invito attivo precedente ad un invito non di tipo primo invito");
            return invito_attivo.getRoundcomune();

        }

    }

    /**
     * Ottiene il comune di riferimento per un dato soggetto
     * (quello di domicilio se e' un domiciliato, quello di residenza altrimenti)
     * @throws java.lang.Exception
     * @return id codice del comune
     * @param ulss ulss
     * @param soggettoId identificativo del soggetto
     */
    public Integer getComuneId(String soggettoId, String ulss) throws Exception {
        this.soggetto_vo.setWhereClause("CODTS='" + soggettoId + "' AND A_SoSoggetto.ULSS='" + ulss + "'");
        this.soggetto_vo.executeQuery();
        /*MOD20071107
        * if(this.soggetto_vo.getRowCount()==0)
           throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda sanitaria "+ulss);
        A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)this.soggetto_vo.first();
        */
        A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) this.soggetto_vo.first();
        if (soggetto == null)
            throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId +
                                " non e' stato trovato nell'azienda sanitaria " + ulss);

        //domiciliata
        if (soggetto.getCodanagreg().equals(ConfigurationConstants.CODICE_DOMICILIATO))
            return soggetto.getReleaseCodeComDom();
        else //altro
            return soggetto.getReleaseCodeComRes();
    }

    /**
     * Ottiene il comune di riferimento per un dato soggetto
     * (quello di domicilio se e' un domiciliato, quello di residenza altrimenti)
     *
     * @return
     * @param soggetto
     */
    private Integer getComuneId(A_SoSoggettoViewRow soggetto) {
        //domiciliata
        if (soggetto.getCodanagreg().equals(ConfigurationConstants.CODICE_DOMICILIATO))
            return soggetto.getReleaseCodeComDom();
        else //altro
            return soggetto.getReleaseCodeComRes();
    }

    /**
     * Metodo che calcola se il soggetto e' fuori fascia
     * @throws java.lang.Exception
     * @return
     * @param invito_attivo
     * @param dataRiferimento
     * @param ulss
     * @param soggettoId
     * @param tpscr
     * @param tpinvito
     */
    public Integer getFuorifascia(String tpscr, String soggettoId, String ulss, Date dataRiferimento,
                                 Integer invito_attivo) throws Exception {
        if (dataRiferimento == null)
            dataRiferimento = new Date();

        //lavoro solo sui primi inviti,
        //      if(tpinvito.equals(ConfigurationConstants.CODICE_PRIMO_INVITO)){

        this.soggetto_vo.setWhereClause("CODTS='" + soggettoId + "' AND A_SoSoggetto.ULSS='" + ulss + "'");
        this.soggetto_vo.executeQuery();
        /*MOD20071107
       * if(this.soggetto_vo.getRowCount()==0)
           throw new Exception("Il soggetto con tessera sanitaria="+soggettoId+" non e' stato trovato nell'azienda sanitaria "+ulss);
        A_SoSoggettoViewRow soggetto=(A_SoSoggettoViewRow)this.soggetto_vo.first();
       */
        A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) this.soggetto_vo.first();
        if (soggetto == null)
            throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId +
                                " non e' stato trovato nell'azienda sanitaria " + ulss);


        //leggo le fasce d'eta' standard
        this.tpscr_vo.setWhereClause("CODSCR='" + tpscr + "'");
        this.tpscr_vo.executeQuery();
        /* MOD20071107
         * if(this.tpscr_vo.getRowCount()==0)
            throw new Exception("Non e' stata trovata nessuna informazione relativa al tipo di screening con sigla="+tpscr);
          A_SoCnfTpscrViewRow scr=(A_SoCnfTpscrViewRow)this.tpscr_vo.first();
          */
        A_SoCnfTpscrViewRow scr = (A_SoCnfTpscrViewRow) this.tpscr_vo.first();
        if (scr == null)
            throw new Exception("Non e' stata trovata nessuna informazione relativa al tipo di screening con sigla=" +
                                tpscr);

        //dati del soggetto
        Calendar c = DateUtils.getCalendar(soggetto.getDataNascita().dateValue());
        c.add(Calendar.YEAR, scr.getEtada().intValue());
        //se alla data di riferimento non ha ancora compiuto l'eta' minima,
        //allora e' fuori fascia
        if (c.getTime().after(dataRiferimento))
            return ConfigurationConstants.DB_TRUE;
        else {
            c = DateUtils.getCalendar(soggetto.getDataNascita().dateValue());
            c.add(Calendar.YEAR, scr.getEtaa().intValue() + 1);
            //se alla data di riferimento ha gia' compiuto un anno piu' dello
            //standard, allora e' fuori fascia
            if (c.getTime().before(dataRiferimento))
                return ConfigurationConstants.DB_TRUE;
            else
                return ConfigurationConstants.DB_FALSE;
        }
        /*   }
    else {
    //per gli inviti successivi eredito dall'invito attivo

            if(invito_attivo!=null){
              Row[] result=this.invito_vo.getFilteredRows("Idinvito",invito_attivo);
              if(result.length==0)
                throw new Exception("Invito precedentemente attivo (id="+invito_attivo+") non trovato");

              return ((A_SoInvitoViewRow)result[0]).getFuorifascia();
            }
    }

   return null;*/
    }

    /**
     *  Metodo che calcola se il soggetto e' fuori fascia per i primi inviti,
     *  mentre copia il valore di fuori fascia dall'invito precedente se si tratta
     *  di un invito diverso
     * @throws java.lang.Exception
     * @return
     * @param invito_attivo
     * @param dataRiferimento
     * @param ulss
     * @param soggetto
     * @param tpscr
     * @param tpinvito
     */
    private Integer getFuorifascia(String tpscr, A_SoSoggettoViewRow soggetto, String ulss, Date dataRiferimento,
                                  A_SoInvitoViewRow invito_attivo) throws Exception {
        if (dataRiferimento == null)
            dataRiferimento = new Date();

        //lavoro solo sui primi inviti,
        //    if(tpinvito.equals("1 ")){

        //leggo le fasce d'eta' standard
        this.tpscr_vo.setWhereClause("CODSCR='" + tpscr + "'");
        this.tpscr_vo.executeQuery();
        /*MOD20071107
          if(this.tpscr_vo.getRowCount()==0)
            throw new Exception("Non e' stata trovata nessuna informazione relativa al tipo di screening con sigla="+tpscr);
          A_SoCnfTpscrViewRow scr=(A_SoCnfTpscrViewRow)this.tpscr_vo.first();
          */
        A_SoCnfTpscrViewRow scr = (A_SoCnfTpscrViewRow) this.tpscr_vo.first();
        if (scr == null)
            throw new Exception("Non e' stata trovata nessuna informazione relativa al tipo di screening con sigla=" +
                                tpscr);

        //dati del soggetto
        Calendar c = DateUtils.getCalendar(soggetto.getDataNascita().dateValue());
        c.add(Calendar.YEAR, scr.getEtada().intValue());
        //se alla data di riferimento non ha ancora compiuto l'eta' minima,
        //allora e' fuori fascia
        if (c.getTime().after(dataRiferimento))
            return ConfigurationConstants.DB_TRUE;
        else {
            c = DateUtils.getCalendar(soggetto.getDataNascita().dateValue());
            c.add(Calendar.YEAR, scr.getEtaa().intValue() + 1);
            //se alla data di riferimento ha gia' compiuto un anno piu' dello
            //standard, allora e' fuori fascia
            if (c.getTime().before(dataRiferimento))
                return ConfigurationConstants.DB_TRUE;
            else
                return ConfigurationConstants.DB_FALSE;
        }
        /*    }
    else {
    //per gli inviti successivi eredito dall'invito attivo

            if(invito_attivo!=null){

              return invito_attivo.getFuorifascia();
            }
    }

   return null;*/
    }

    public A_SoSoggScrViewRow insertIntoScreeningIfNecessary(String tpscr, String ulss, String soggettoId,
                                                             Integer invito_attivo) throws Exception {
        this.sogg_scr_vo.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' AND CODTS='" + soggettoId + "'");
        this.sogg_scr_vo.executeQuery();
        /*MOD20071107
       * A_SoSoggScrViewRow soggscr;
         //se non e' gia' nello screening lo aggiungo
         if(this.sogg_scr_vo.getRowCount()==0)
         {
           soggscr=this.insertIntoScr(ulss,tpscr,soggettoId,invito_attivo);
         }
         else
          soggscr=(A_SoSoggScrViewRow)this.sogg_scr_vo.first();
          */

        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");

        A_SoSoggScrViewRow soggscr = (A_SoSoggScrViewRow) this.sogg_scr_vo.first();
        //se non e' gia' nello screening lo aggiungo
        if (soggscr == null)
            soggscr = this.insertIntoScr(ulss, tpscr, soggettoId, invito_attivo);
        /*20080714 MOD: codice archiviazione*/
        //se c'e' gia' vado a vedere se devo generare un codice di archiviazione
        else {
            //prima controllo se esiste gia' un codice di archiviazione
            if (soggscr.getCodidsoggMx() != null && soggscr.getCodidsoggMx().length() > 0) {
                //in tal caso non devo fare nulla
                return soggscr;
            }
            //in caso contrario vado a vedere se devo generarlo
            String prodNrich =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_ID_MX);
            if ("S".equals(prodNrich)) {
                this.soggetto_vo.setWhereClause("CODTS='" + soggettoId + "' AND A_SoSoggetto.ULSS='" + ulss + "'");
                this.soggetto_vo.executeQuery();

                A_SoSoggettoViewRow soggetto = (A_SoSoggettoViewRow) this.soggetto_vo.first();
                if (soggetto == null)
                    throw new Exception("Il soggetto con tessera sanitaria=" + soggettoId +
                                        " non e' stato trovato nell'azienda sanitaria " + ulss);

                String s =
                    Progressivi.getCodiceIdMx(am, soggetto.getDataNascita(), soggetto.getCognome(), soggetto.getNome());
                soggscr.setCodidsoggMx(s);
            }
        }
        /*20080714 FINE MOD*/


        return soggscr;
    }

    /**
     * Metodo che aggiorna, se serve, la posizione di screening e restituisce
     * il round inviti
     * @throws java.lang.Exception
     * @return
     * @param tpinvito
     * @param soggscr record ch eidentifica la donna nello screening
     */

    public Integer updateAndGetRoundInviti(Row invito) throws Exception {
        this.sogg_scr_vo.setWhereClause("TPSCR='" + invito.getAttribute("Tpscr") + "' AND ULSS='" +
                                        invito.getAttribute("Ulss") + "' AND CODTS='" + invito.getAttribute("Codts") +
                                        "'");
        this.sogg_scr_vo.executeQuery();
        /*MOD20071107
    * A_SoSoggScrViewRow soggscr;
     //se non e' gia' nello screening lo aggiungo
     if(this.sogg_scr_vo.getRowCount()==0)
     {
       throw new Exception("Posizione di screening del soggetto con tessera sanitaria "+invito.getAttribute("Codts")+" non trovata nella ulss "+invito.getAttribute("Ulss"));
     }
     else
      soggscr=(A_SoSoggScrViewRow)this.sogg_scr_vo.first();
      */
        A_SoSoggScrViewRow soggscr = (A_SoSoggScrViewRow) this.sogg_scr_vo.first();
        if (soggscr == null)
            throw new Exception("Posizione di screening del soggetto con tessera sanitaria " +
                                invito.getAttribute("Codts") + " non trovata nella ulss " +
                                invito.getAttribute("Ulss"));

        String tpinvito = (String) invito.getAttribute("Idtpinvito");
        this.tpinvito_vo.setWhereClause("ULSS='" + soggscr.getUlss() + "' AND TPSCR='" + soggscr.getTpscr() +
                                        "' AND IDTPINVITO='" + tpinvito + "'");
        this.tpinvito_vo.executeQuery();
        /*MOD20071107
     * if(this.tpinvito_vo.getRowCount()==0)
      throw new Exception("Tipo di invito con codice "+tpinvito+" non trovato per la ulss "+soggscr.getUlss());
    A_SoCnfTpinvitoViewRow tpInvito=(A_SoCnfTpinvitoViewRow)this.tpinvito_vo.first();
    */
        A_SoCnfTpinvitoViewRow tpInvito = (A_SoCnfTpinvitoViewRow) this.tpinvito_vo.first();
        if (tpInvito == null)
            throw new Exception("Tipo di invito con codice " + tpinvito + " non trovato per la ulss " +
                                soggscr.getUlss());

        //aggiorno il round invito
        this.updateScreeningPos(soggscr, tpInvito, (Integer) invito.getAttribute("Idinvito"));
        return soggscr.getRoundinviti();
    }

    public Integer updateAndGetRoundInviti(A_SoSoggScrViewRow soggscr, A_SoCnfTpinvitoViewRow tpinvito,
                                          Integer idinvito) throws Exception {

        //aggiorno il round invito
        this.updateScreeningPos(soggscr,
            //roundcomune,
            tpinvito, idinvito);
        return soggscr.getRoundinviti();
    }

    /**
     * Metodo che aggiorna, se serve, la posizione di screening e restituisce
     * il round inviti
     * @throws java.lang.Exception
     * @return
     * @param tpinvito
     * @param invito_attivo
     * @param soggettoId
     * @param ulss
     * @param tpscr
     */
    public Integer updateAndGetRoundInviti(A_SoSoggScrViewRow soggscr, String tpinvito,
                                          Integer idinvito) throws Exception {
        this.tpinvito_vo.setWhereClause("ULSS='" + soggscr.getUlss() + "' AND TPSCR='" + soggscr.getTpscr() +
                                        "' AND IDTPINVITO='" + tpinvito + "'");
        this.tpinvito_vo.executeQuery();
        /*MOD20071107
    * if(this.tpinvito_vo.getRowCount()==0)
      throw new Exception("Tipo di invito con codice "+tpinvito+" non trovato per la ulss "+soggscr.getUlss());
    A_SoCnfTpinvitoViewRow tpInvito=(A_SoCnfTpinvitoViewRow)this.tpinvito_vo.first();
    */
        A_SoCnfTpinvitoViewRow tpInvito = (A_SoCnfTpinvitoViewRow) this.tpinvito_vo.first();
        if (tpInvito == null)
            throw new Exception("Tipo di invito con codice " + tpinvito + " non trovato per la ulss " +
                                soggscr.getUlss());

        return this.updateAndGetRoundInviti(soggscr, tpInvito, idinvito);

    }

    /**
     * Metodo che restituisce il round individuale, leggendolo dalla tabella so_sogg_scr
     * @return
     * @param soggscr
     */
    public Integer getRoundIndividuale(A_SoSoggScrViewRow soggscr) {
        if (soggscr == null)
            return null;
        else
            return soggscr.getRoundindiv();
    }

    /**
     * Metodo che restituisce il round individuale, leggendolo dalla tabella so_sogg_scr
     * @return
     * @param soggettoId
     * @param ulss
     * @param tpscr
     */
    public Integer getRoundIndividuale(String tpscr, String ulss, String soggettoId) {
        this.sogg_scr_vo.setWhereClause("TPSCR='" + tpscr + "' AND ULSS='" + ulss + "' AND CODTS='" + soggettoId + "'");
        this.sogg_scr_vo.executeQuery();
        A_SoSoggScrViewRow soggscr = (A_SoSoggScrViewRow) this.sogg_scr_vo.first();
        return this.getRoundIndividuale(soggscr);
    }

    /**
     * Permette di ottenere il livello di un invito
     * @throws java.lang.Exception
     * @return
     * @param tpscr
     * @param ulss
     * @param tpinvito
     */
    public Integer getLivello(String tpinvito, String ulss, String tpscr) throws Exception {
        this.tpinvito_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND IDTPINVITO='" + tpinvito +
                                        "'");
        this.tpinvito_vo.executeQuery();
        /* MOD20071107
    * if(this.tpinvito_vo.getRowCount()==0)
      throw new Exception("Tipo di invito con codice "+tpinvito+" non trovato per la ulss "+ulss);
    A_SoCnfTpinvitoViewRow tpInvito=(A_SoCnfTpinvitoViewRow)this.tpinvito_vo.first();
    */
        A_SoCnfTpinvitoViewRow tpInvito = (A_SoCnfTpinvitoViewRow) this.tpinvito_vo.first();
        if (tpInvito == null)
            throw new Exception("Tipo di invito con codice " + tpinvito + " non trovato per la ulss " + ulss);
        return tpInvito.getLivello();

    }

    /**
     * Permette di ottenere il livello di un invito
     * @throws java.lang.Exception
     * @return
     * @param tpscr
     * @param ulss
     * @param tpinvito
     */
    public Integer getLivello(A_SoCnfTpinvitoViewRow tpInvito) throws Exception {
        return tpInvito.getLivello();

    }

    /**
     * Stabilisce il valore del flag volontaria:
     * se e' un primo invito si usa il dato di input,
     * altrimenti il valore viene ereditato dall'invito precedente
     * @throws java.lang.Exception
     * @return
     * @param volontaria
     * @param invito_attivo
     * @param tpinvito
     */
    public Integer getVolontaria(String ulss, String tpscr, String tpinvito, Integer invito_attivo) throws Exception {
        this.tpinvito_vo.setWhereClause("ULSS='" + ulss + "' AND TPSCR='" + tpscr + "' AND IDTPINVITO='" + tpinvito +
                                        "'");
        this.tpinvito_vo.executeQuery();
        /*MOD20071107
    * if(this.tpinvito_vo.getRowCount()==0)
      throw new Exception("Tipo di invito con codice "+tpinvito+" non trovato per la ulss "+ulss);
    A_SoCnfTpinvitoViewRow tpInvito=(A_SoCnfTpinvitoViewRow)this.tpinvito_vo.first();
    */
        A_SoCnfTpinvitoViewRow tpInvito = (A_SoCnfTpinvitoViewRow) this.tpinvito_vo.first();
        if (tpInvito == null)
            throw new Exception("Tipo di invito con codice " + tpinvito + " non trovato per la ulss " + ulss);


        if (ConfigurationConstants.CODICE_CAT_VOLONTARIO.equals(tpInvito.getIdcateg())) {
            return new Integer(1);

        }
        //se si tratta di un primo invito non e' volontaria
        else if (ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.equals(tpInvito.getIdcateg()))
            return ConfigurationConstants.DB_FALSE;
        else //se si tratta di un followup fa testo l'invito precedente
        {
            if (invito_attivo == null)
                throw new Exception("Nessun invito attivo precedente ad un invito non di tipo primo invito");
            Row[] result = this.invito_vo.getFilteredRows("Idinvito", invito_attivo);
            if (result.length == 0)
                throw new Exception("Invito precedentemente attivo con codice " + invito_attivo + " non trovato");
            return ((A_SoInvitoViewRow) result[0]).getVolontaria();
        }
    }


    /**
     * Stabilisce il valore del flag volontaria:
     * se e' un primo invito si usa il dato di input,
     * altrimenti il valore viene ereditato dall'invito precedente
     * @throws java.lang.Exception
     * @return
     * @param invito_attivo
     * @param tpinvito
     */
    private Integer getVolontaria(A_SoCnfTpinvitoViewRow tpinvito, A_SoInvitoViewRow invito_attivo) throws Exception {
        //se e' un accesso di tipo volontaria la posso registrare coome volontaria
        if (ConfigurationConstants.CODICE_CAT_VOLONTARIO.equals(tpinvito.getIdcateg())) {
            return new Integer(1);

        }
        //se si tratta di un primo invito non e' volontaria
        else if (ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.equals(tpinvito.getIdcateg()))
            return ConfigurationConstants.DB_FALSE;
        else //se si tratta di un followup fa testo l'invito precedente
        {
            if (invito_attivo == null)
                throw new Exception("Nessun invito attivo precedente ad un invito non di tipo primo invito");
            return invito_attivo.getVolontaria();
        }
    }

    /**
     * Calcola il nuovo valore del round individuale in seguito alla modifica
     * dell'esito di un invito
     * @return
     * @param invito record con l'invito modificato
     */
    public Integer computeNewRoundIndiv(Row invito) throws Exception {
        //view object che mi permette di ottenere tutti gli inviti del soggetto
        //in ordine cronologico
        String query =
            "SELECT SO_INVITO.IDINVITO, " + "SO_INVITO.CODESITOINVITO, " + "SO_CNF_TPINVITO.IDCATEG " +
            "FROM SO_INVITO JOIN SO_CNF_TPINVITO " + "ON SO_INVITO.IDTPINVITO=SO_CNF_TPINVITO.IDTPINVITO " +
            "AND SO_INVITO.ULSS=SO_CNF_TPINVITO.ULSS " + "AND SO_INVITO.TPSCR=SO_CNF_TPINVITO.TPSCR " +
            "WHERE CODTS='" + invito.getAttribute("Codts") + "' AND SO_INVITO.ULSS='" + invito.getAttribute("Ulss") +
            "' AND SO_INVITO.TPSCR='" + invito.getAttribute("Tpscr") + "' ORDER BY DTAPP";

        ViewObject inviti = am.createViewObjectFromQueryStmt("inv_tmp", query);
        //contatore adesioni
        int counter = 0;
        try {

            inviti.first();
            inviti.previous();
            //ancora nessuna adesione
            boolean adesione = false;

            while (inviti.hasNext()) {
                Row r = inviti.next();
                String esito;
                Integer categoria;
                //se l'invito e' quello su cui sto lavorando, meglio prendere la versione runtime
                //che e' aggiornata alle modifiche
                Integer a = r.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) r.getAttribute(0)).intValue() : null;
                Integer b = (Integer)invito.getAttribute("Idinvito");
                if (a.compareTo(b)==0) {
                    esito = (String) invito.getAttribute("Codesitoinvito");
                    try {
                        categoria = (Integer) invito.getAttribute("Idcateg");
                    } catch (Exception ex) {
                        //se il record con l'invito non ha la actegoria, la cerco
                        this.tpinvito_vo.setWhereClause("ULSS='" + invito.getAttribute("Ulss") + "' AND TPSCR='" +
                                                        invito.getAttribute("Tpscr") + "' AND IDTPINVITO='" +
                                                        invito.getAttribute("Idtpinvito") + "'");
                        this.tpinvito_vo.executeQuery();
                        if (this.tpinvito_vo.getRowCount() == 0)
                            throw new Exception("Tipo di invito con codice " + invito.getAttribute("Idtpinvito") +
                                                " non trovato per la ulss " + invito.getAttribute("Ulss"));
                        categoria = (Integer) this.tpinvito_vo.first().getAttribute("Idcateg");
                    }

                } else {
                    esito = (String) r.getAttribute(1);
                    categoria = r.getAttribute(2) !=null ? ((oracle.jbo.domain.Number) r.getAttribute(2)).intValue() : null; 
                }


                //se si tratta di un invito di categoria primo invito o volotario
                //reset dell'adesione, perche' inizia un nuovo espisodio
                if (ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.equals(categoria) ||
                    ConfigurationConstants.CODICE_CAT_VOLONTARIO.equals(categoria))
                    adesione = false;

                //se l'esito risulta presentata e non ho ancora registrato nessuna adesione nell'episodio:
                if (ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO.equals(esito) && !adesione) {
                    //incremento il numero di adesioni
                    counter++;
                    //registro che per questo episodio c'e' gia' un'adesione
                    adesione = true;
                }
            }

        }

        finally {
            if (inviti != null)
                inviti.remove();
        }
        return new Integer(counter);

    }

    /* public Integer computeNewRoundIndiv(Row invito)
  {
    //view object che mi permette di ottenere il valore del round individuale
    //escludendo il round organizzativo dell'invito su cui sto lavorando

    //where codesitoinvito=:1 and codts=:2 and roundcomune<>:3
    old.setWhereClauseParam(0,ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO);
    old.setWhereClauseParam(1,invito.getAttribute("Codts"));
    old.setWhereClauseParam(2,invito.getAttribute("Roundcomune"));
    old.executeQuery();

    Integer old_roundindiv=new Integer(0);
    //se ho trovato qualcosa, memorizzo il round individuale
    if(old.getRowCount()>0)
    {
      A_SoOldRoundindivViewRow r=(A_SoOldRoundindivViewRow)old.first();
      old_roundindiv=r.getOldRoundindiv();
      if(old_roundindiv==null)
       old_roundindiv=new Integer(0);
    }


    //view object che mi permette di ottenere il valore del round individuale (0 o 1)
    //solo nel round organizzativo dell'invito su cui sto lavorando,
    //escludendo l'invito stesso

    //where codesitoinvito=:1 and codts=:2 and roundcomune=:3 and idinvito<>:4
    curr.setWhereClauseParam(0,ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO);
    curr.setWhereClauseParam(1,invito.getAttribute("Codts"));
    curr.setWhereClauseParam(2,invito.getAttribute("Roundcomune"));
    curr.setWhereClauseParam(3,invito.getAttribute("Idinvito"));
    curr.executeQuery();

    boolean current=false;
    //se ho trovato qualcosa, allora esiste gia' un invito con presenza
    //in questo round
    if(curr.getRowCount()>0)
    {
      current=true;
    }

    //se ho gia' un invito con presenza, qualunque sia l'esito dell'invito
    //su cui sto lavorando, il round individuale e' quello vecchio piu' la presenza
    if(current)
      return old_roundindiv.add(1);


    //se invece non ho ancora presenze nel round corrente, allora dipende dal
    //nuovo esito dell'invito:
    //se l'esito e' presentata il round individuale va incrementato
    else if(ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO.equals(invito.getAttribute("Codesitoinvito")))
       return old_roundindiv.add(1);

    //altrimenti il round individuale e' invariato
    else
      return old_roundindiv;
  }*/

    public void updateRoundIndiv(Row invito) throws Exception {
        this.sogg_scr_vo.setWhereClause("TPSCR='" + invito.getAttribute("Tpscr") + "' AND ULSS='" +
                                        invito.getAttribute("Ulss") + "' AND CODTS='" + invito.getAttribute("Codts") +
                                        "'");
        this.sogg_scr_vo.executeQuery();
        /*MOD20071107
    * A_SoSoggScrViewRow soggscr;
     //se non e' gia' nello screening lo aggiungo
     if(this.sogg_scr_vo.getRowCount()==0)
     {
       throw new Exception("Posizione di screening del soggetto con tessera sanitaria "+invito.getAttribute("Codts")+" non trovata nella ulss "+invito.getAttribute("Ulss"));
     }
     else
      soggscr=(A_SoSoggScrViewRow)this.sogg_scr_vo.first();
      */

        A_SoSoggScrViewRow soggscr = (A_SoSoggScrViewRow) this.sogg_scr_vo.first();
        if (soggscr == null)
            throw new Exception("Posizione di screening del soggetto con tessera sanitaria " +
                                invito.getAttribute("Codts") + " non trovata nella ulss " +
                                invito.getAttribute("Ulss"));


        this.updateRoundIndiv(soggscr, invito);
    }

    public void updateRoundIndiv(A_SoSoggScrViewRow soggscr, Row invito) throws Exception {

        Integer roundindiv = this.computeNewRoundIndiv(invito);
        soggscr.setRoundindiv(roundindiv);
    }

    public void updateRoundIndivHPV(Row invito) throws Exception {
        this.sogg_scr_vo.setWhereClause("TPSCR='" + invito.getAttribute("Tpscr") + "' AND ULSS='" +
                                        invito.getAttribute("Ulss") + "' AND CODTS='" + invito.getAttribute("Codts") +
                                        "'");
        this.sogg_scr_vo.executeQuery();

        A_SoSoggScrViewRow soggscr = (A_SoSoggScrViewRow) this.sogg_scr_vo.first();
        if (soggscr == null)
            throw new Exception("Posizione di screening del soggetto con tessera sanitaria " +
                                invito.getAttribute("Codts") + " non trovata nella ulss " +
                                invito.getAttribute("Ulss"));


        /* -- CALCOLO DEL VALORE DEL ROUND -- */

        //view object che mi permette di ottenere tutti gli inviti del soggetto
        //in ordine cronologico
        String query =
            "SELECT SO_INVITO.IDINVITO, " + "SO_INVITO.CODESITOINVITO, " + "SO_CNF_TPINVITO.IDCATEG, " +
            "so_acc_cito1liv.prelievo_hpv " + "FROM SO_INVITO JOIN SO_CNF_TPINVITO " +
            "ON SO_INVITO.IDTPINVITO=SO_CNF_TPINVITO.IDTPINVITO " + "AND SO_INVITO.ULSS=SO_CNF_TPINVITO.ULSS " +
            "AND SO_INVITO.TPSCR=SO_CNF_TPINVITO.TPSCR " +
            "LEFT JOIN so_acc_cito1liv ON SO_INVITO.IDINVITO=so_acc_cito1liv.idinvito AND so_acc_cito1liv.tpscr=SO_INVITO.TPSCR " +
            "WHERE CODTS='" + invito.getAttribute("Codts") + "' AND SO_INVITO.ULSS='" + invito.getAttribute("Ulss") +
            "' AND SO_INVITO.TPSCR='" + invito.getAttribute("Tpscr") + "' ORDER BY DTAPP";

        ViewObject inviti = am.createViewObjectFromQueryStmt("inv_hpv_tmp", query);
        //contatore adesioni
        int counter = 0;
        try {

            inviti.first();
            inviti.previous();
            //ancora nessuna adesione
            boolean adesione = false;

            while (inviti.hasNext()) {
                Row r = inviti.next();
                String esito;
                Integer categoria;
                //se l'invito e' quello su cui sto lavorando, meglio prendere la versione runtime
                //che e' aggiornata alle modifiche
                Integer a = r.getAttribute(0) !=null ? ((oracle.jbo.domain.Number) r.getAttribute(0)).intValue() : null;
                Integer b = (Integer)invito.getAttribute("Idinvito");
                if (a.compareTo(b)==0) {
                    esito = (String) invito.getAttribute("Codesitoinvito");
                    try {
                        categoria = (Integer) invito.getAttribute("Idcateg");
                    } catch (Exception ex) {
                        //se il record con l'invito non ha la actegoria, la cerco
                        this.tpinvito_vo.setWhereClause("ULSS='" + invito.getAttribute("Ulss") + "' AND TPSCR='" +
                                                        invito.getAttribute("Tpscr") + "' AND IDTPINVITO='" +
                                                        invito.getAttribute("Idtpinvito") + "'");
                        this.tpinvito_vo.executeQuery();
                        if (this.tpinvito_vo.getRowCount() == 0)
                            throw new Exception("Tipo di invito con codice " + invito.getAttribute("Idtpinvito") +
                                                " non trovato per la ulss " + invito.getAttribute("Ulss"));
                        categoria = (Integer) this.tpinvito_vo.first().getAttribute("Idcateg");
                    }

                } else {
                    esito = (String) r.getAttribute(1);
                    categoria = r.getAttribute(2)!=null ? ((oracle.jbo.domain.Number) r.getAttribute(2)).intValue() : null;
                }
                Integer prelievoHPV = (r.getAttribute(3)!=null) ? ((oracle.jbo.domain.Number) r.getAttribute(3)).intValue() : null;

                //se si tratta di un invito di categoria primo invito o volotario
                //reset dell'adesione, perche' inizia un nuovo espisodio
                if (ConfigurationConstants.CODICE_CAT_PRIMO_INVITO.equals(categoria) ||
                    ConfigurationConstants.CODICE_CAT_VOLONTARIO.equals(categoria))
                    adesione = false;

                //se l'esito risulta presentata e non ho ancora registrato nessuna adesione nell'episodio:
                if (ConfigurationConstants.CODICE_ESITO_ESAME_ESEGUITO.equals(esito) &&
                    !adesione) {
                    //se ha eseguito il prelievo HPV
                    if (prelievoHPV != null &&
                        ConfigurationConstants.CODICE_PRELIEVO_HPV_ESEGUITO.equals(prelievoHPV)) {
                        //incremento il numero di adesioni
                        counter++;
                        //registro che per questo episodio c'e' gia' un'adesione
                        adesione = true;
                    }
                }
            }
        }

        finally {
            if (inviti != null)
                inviti.remove();
        }

        /*-- fine calcolo --*/

        //aggiorno l'invito corrente
        invito.setAttribute("RoundindivHpv", new Integer(counter));

        //Se tale invito e' quello attivo, aggiorna anche SO_SOGG_SCR
        Integer invitoAttivo = (Integer) invito.getAttribute("Attivo");
        if (invitoAttivo != null && invitoAttivo.intValue() == 1) {
            soggscr.setRoundindivHpv(new Integer(counter));
        }

    }

    /**
     * Cancella la lettera di invito
     * @param allegato
     * @param tpscr
     * @param ulss
     */
    public void deleteLettera(Integer allegato) {       
        if(allegato==null)
             return;
            this.allegato_vo.setWhereClause("IDALLEGATO="+allegato);
            this.allegato_vo.executeQuery();
            A_SoAllegatoViewRow r=(A_SoAllegatoViewRow)this.allegato_vo.first();
            if(r!=null){
              if (r.getAttribute("Dtstampa") != null)
              {
                // BACK UP della vecchia lettera
                String sql = "insert into so_allegato_bck " + 
                  "select * from so_allegato where idallegato = " + allegato;     
                am.getTransaction().executeCommand(sql);    
              }
              r.remove();
            }
    }

    /**
     * Inserisce un record id accettazione di primo livello per il colon,
     * creando anche il codice campione relativo
     * @return codice del campione
     * @param invito id dell'invito cui e' relativa l'accettazione
     * @param user utente connesso
     */
    private BigDecimal insertAccColon(String user, A_SoInvitoViewRow invito) throws Exception {
        A_SoAccColon1livViewRow a = (A_SoAccColon1livViewRow) this.accColon1l_vo.createRow();
        this.accColon1l_vo.insertRow(a);
        a.setIdaccco1liv(((Parent_AppModule) am).getNextIdAccColon1());
        a.setIdinvito(invito.getIdinvito());
        a.setDtcreazione(DateUtils.getOracleDateNow());
        a.setOpcreazione(user);
        //generazione del numero di campione
        // if(invito.getIdcentroref1liv()!=null){
        //da testare
        // mauro 22-8 modificato chiamate per nuova versione serv.

        //controllo se devo generare il codice campione
        String ulss = invito.getUlss();
        String tpscr = invito.getTpscr();
        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");

        // I00093449
        boolean gestioneUnicaCodici = false;
        try{
            String prodNrich =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_UNQ_RIC_CAM);
            
            gestioneUnicaCodici = "S".equals(prodNrich);
        }catch(Throwable th){
            gestioneUnicaCodici = false;
        }
        
        if(gestioneUnicaCodici){
            BigDecimal prog = Progressivi.getProgressivo(this.am, ConfigurationConstants.PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE, ulss,
                                                        tpscr, invito.getIdcentroref1liv(), invito.getDtapp());
            a.setCodCampione(prog);
            a.setCodRichiesta(prog);
        }else{
        // I00093449
            String prodNrich =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_CAMPIONE);
            if ("S".equals(prodNrich))
                a.setCodCampione(Progressivi.getProgressivo(this.am, ConfigurationConstants.PROGRESSIVO_CAMPIONE, ulss,
                                                            tpscr, invito.getIdcentroref1liv(), invito.getDtapp()));
    
            //controllo se devo generare il codice richiesta
            prodNrich =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
            if ("S".equals(prodNrich))
                a.setCodRichiesta(Progressivi.getProgressivo(this.am, ConfigurationConstants.PROGRESSIVO_RICHIESTA, ulss,
                                                             tpscr, invito.getIdcentroref1liv(), invito.getDtapp()));
        }
        //
        /*  }
    else{
       a.setCodCampione(null);
       a.setCodRichiesta(null);
    }*/

        a.setOpultmod(user);
        a.setDtultmod(DateUtils.getOracleDateNow());

        return a.getCodCampione();
    }

    /**
     * Inserisce un record id accettazione di primo livello per il PFAS,
     * creando anche il codice campione relativo
     * @return codice del campione
     * @param invito id dell'invito cui e' relativa l'accettazione
     * @param user utente connesso
     */
    private A_SoAccPfas1livViewRowImpl insertAccPFAS(String user, A_SoInvitoViewRow invito) throws Exception {
        A_SoAccPfas1livViewRowImpl a = (A_SoAccPfas1livViewRowImpl) this.accPfas1l_vo.createRow();
        this.accPfas1l_vo.insertRow(a);
        a.setIdaccpf1liv(((Parent_AppModule) am).getNextIdAccPFAS1());

        a.setIdinvito(invito.getIdinvito());
        a.setDtcreazione(DateUtils.getOracleDateNow());
        a.setOpcreazione(user);
        //generazione del numero di campione

        //controllo se devo generare il codice campione
        String ulss = invito.getUlss();
        String tpscr = invito.getTpscr();
        ViewObject voParam = am.findViewObject("A_SoCnfParametriSistemaView1");

        // I00093449
        boolean gestioneUnicaCodici = false;
        try{
            String prodNrich =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_UNQ_RIC_CAM);
            
            gestioneUnicaCodici = "S".equals(prodNrich);
        }catch(Throwable th){
            gestioneUnicaCodici = false;
        }
        
        if(gestioneUnicaCodici){
            BigDecimal prog = Progressivi.getProgressivo(this.am, ConfigurationConstants.PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE, ulss,
                                                        tpscr, invito.getIdcentroref1liv(), invito.getDtapp());
            a.setCodCampione(prog);
            a.setCodRichiesta(prog);
        }else{
        // I00093449
            String prodNrich =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_CAMPIONE);
            if ("S".equals(prodNrich))
                a.setCodCampione(Progressivi.getProgressivo(this.am, ConfigurationConstants.PROGRESSIVO_CAMPIONE, ulss,
                                                            tpscr, invito.getIdcentroref1liv(), invito.getDtapp()));
    
            //controllo se devo generare il codice richiesta
            prodNrich =
                ParametriSistema.getParamValue(voParam, ulss, tpscr, ConfigurationConstants.PARAMETRO_PROD_RICHIESTA);
            if ("S".equals(prodNrich))
                a.setCodRichiesta(Progressivi.getProgressivo(this.am, ConfigurationConstants.PROGRESSIVO_RICHIESTA, ulss,
                                                             tpscr, invito.getIdcentroref1liv(), invito.getDtapp()));
        }
        
        a.setMiteni(new Integer(0));
        a.setDonatore(new Integer(0));
        a.setOpultmod(user);
        a.setDtultmod(DateUtils.getOracleDateNow());

        return a;
    }

    protected void finalize() throws Throwable {
        ParametriSistema.releaseLogo(this.ulssParams);
        super.finalize();
    }

    // mauro 10/02/2010 creata funzioen da chiamare esplicitamente per il rilascio
    // del logo
    public void releaseLogo() {
        ParametriSistema.releaseLogo(this.ulssParams);
    }

    /**
     * Crea un token corrispondente ad un id.
     *
     * @param id
     * @return
     */
    public static String getToken(long id) {
        char[] chrKey = key.toCharArray();
        char[] result = new char[RESULT_LENGTH];
        int base = key.length();

        long value = id;
        for (int i = 0; i < RESULT_LENGTH; i++) {
            result[i] = chrKey[(int) (value % base)];
            value = value / base;
        }
        return String.valueOf(result);
    }

}
