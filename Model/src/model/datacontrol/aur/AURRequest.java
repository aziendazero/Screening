package model.datacontrol.aur;

import java.io.InputStream;

import java.math.BigDecimal;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import model.common.ImpExp_AppModule;
import model.common.Sogg_AppModule;

import model.commons.ConfigurationReader;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.server.SequenceImpl;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "AURFilter" })
public class AURRequest {
    public AURRequest() {
        super();
        this.AURFilter = new AURFilter();
    }
    private static ADFLogger log = ADFLogger.createADFLogger(AURRequest.class);

    @JsonProperty("filter")
    private AURFilter AURFilter = new AURFilter();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("filter")
    public AURFilter getAURFilter() {
        if (AURFilter == null)
            AURFilter = new AURFilter();

        return AURFilter;
    }

    @JsonProperty("filter")
    public void setAURFilter(AURFilter AURFilter) {
        this.AURFilter = AURFilter;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void resetFilter() {
        this.AURFilter.reset();
    }

    public String rollback(String actionToGo) {
        this.resetFilter();

        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding voIterAUR = bindings.findIteratorBinding("personsIterator");

        voIterAUR.getViewObject().executeEmptyRowSet();

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.remove("FROM_AUR");

        return actionToGo;
    }

    public AURResponse findAnag() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map r = adfCtx.getRequestScope();

        boolean executeSearch = r.containsKey("executeSearch") && (Boolean) r.get("executeSearch");

        if (!executeSearch)
            return new AURResponse();

        //il sistema verifica la presenza di almeno uno dei filtri in elenco (ove nome, cognome e data di nasciat sono un filtro unico);
        // in caso contrario segnala l'impossibilità di effettuare la ricerca
        if ((AURFilter.getMpi() == null || AURFilter.getMpi().trim().isEmpty()) &&
            (AURFilter.getFiscalCode() == null || AURFilter.getFiscalCode().trim().isEmpty()) &&
            (AURFilter.getCs() == null || AURFilter.getCs().trim().isEmpty()) &&
            (AURFilter.getNameFam() == null || AURFilter.getNameFam().trim().isEmpty() || AURFilter.getNameGiv() == null ||
             AURFilter.getNameGiv().trim().isEmpty() || AURFilter.getBirthTimeDate() == null)) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 "E' obbligatorio definire il Codice fiscale oppure la Tessera Sanitaria oppure l' MPI oppure la terna Cognome/Nome/Data di nascita",
                                 "");
            ctx.addMessage(null, fm);
            return new AURResponse();
        }
        adfCtx.getViewScope().put("enableNewButton", Boolean.TRUE);
        return executeAURSearch();
    }

    @SuppressWarnings({"oracle.jdeveloper.java.insufficient-catch-block", "unchecked"})
    private AURResponse executeAURSearch(){
        String mirthUrl = ConfigurationReader.readProperty("AUR_SERVICE_URL");
        String timeoutStr = ConfigurationReader.readProperty("AUR_SERVICE_TIMEOUT");
        String enabledStr = ConfigurationReader.readProperty("AUR_SERVICE_ENABLED");
        Integer timeout = null;
        Boolean enabled = Boolean.FALSE;
        if (enabledStr != null && enabledStr.equals("1")) {
            enabled = Boolean.TRUE;
        }

        try {
            timeout = Integer.parseInt(timeoutStr);
        } catch (Throwable th) {
            log.severe(th);
        }

        AURResponse anagrafe = new AURResponse();
        if (!enabled || mirthUrl == null || mirthUrl.isEmpty()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Servizio non diponibile", "");
            ctx.addMessage(null, fm);
            return anagrafe;
        }

        HttpParams httpParams = new BasicHttpParams();

        if (timeout != null) {
            HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
            HttpConnectionParams.setSoTimeout(httpParams, timeout);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        
        try{
            System.out.println("AURSEARCH with parameter: ");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.AURFilter));
        }catch(Throwable th){}

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        try {
            HttpPost request = new HttpPost(mirthUrl);
            request.setParams(httpParams);

            StringEntity se = new StringEntity(mapper.writeValueAsString(this));

            request.addHeader("content-type", "application/json");
            request.addHeader("Accept", "application/json");
            request.setEntity(se);

            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (response != null && statusCode == HttpStatus.SC_OK) {
                InputStream in = response.getEntity().getContent();

                try{
                    Map<String, Object> jsonMap = mapper.readValue(in, Map.class);
                    System.out.println("----------- RISPOSTA AUR --------------");
                    System.out.println(jsonMap);                    
                    System.out.println("----------- FINE RISPOSTA AUR --------------");
                    anagrafe = mapper.convertValue(jsonMap, AURResponse.class);
                } catch (Throwable th) {
                    log.severe("CODE: SERVICE_ERROR_RESPONSE", th);
                    anagrafe = new AURResponse();
                }
            } else {
                String _msg = MessageFormat.format("Problema di connessione con il servizio esterno: [ http status code {0} or null response]", statusCode); 

                log.severe("CODE: SERVICE_ERROR_RESPONSE - " + _msg);
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, _msg, "");
                ctx.addMessage(null, fm);
            }
        } catch (Exception ex) {
            String _msg =
                MessageFormat.format("Problema di connessione con il servizio esterno: [{0}]", ex.getMessage()); 
            log.severe("SERVICE_ERROR: ", ex);

            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, _msg, "");
            ctx.addMessage(null, fm);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        
        return anagrafe;
    }
        
    @SuppressWarnings("unchecked")
    public String updateAURAnag(String mpi, String cf){
        String numTessera = null;
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding _soggDocs = bindings.findIteratorBinding("Sogg_SoDocumentiSoggView1Iterator");
        ViewObject vo = _soggDocs.getViewObject();
        if((mpi==null || mpi.isEmpty()) && (cf==null || cf.isEmpty()) ){           
            // se non ho l'mpi e il cf uso la tessera sanitaria
            Row[] tessera = vo.getFilteredRows("IdTipoDoc", "TS");
            if (tessera.length > 0){
                numTessera = (String)tessera[0].getAttribute("DocIdent");               
            } else {
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossibile aggiornare il soggetto con mpi nullo e tessera nullas", "");
                ctx.addMessage(null, fm);
            }
        }
        
        ADFContext adfCtx = ADFContext.getCurrent();
        Map req = adfCtx.getRequestScope();

        req.put("executeSearch", Boolean.TRUE);
    
        this.resetFilter();
        this.getAURFilter().setMpi(mpi);
        this.getAURFilter().setFiscalCode(cf);
        this.getAURFilter().setCs(numTessera);

        AURResponse result = this.findAnag();
        
        if(result!=null && result.getPersons()!=null && !result.getPersons().isEmpty()){
            // aggiorna
            Person persona = result.getPersons().get(0);
            SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMdd");  //formato date HL7
            Sogg_AppModule am = (Sogg_AppModule) vo.getApplicationModule();
            Map session = adfCtx.getSessionScope();
            String ulss = (String)session.get("ulss");
            String tpscr = (String)session.get("scr");
            
            try{
                SequenceImpl s = new SequenceImpl("ID_MSG_HL7_SIASI", am);
                BigDecimal idMsg = s.getSequenceNumber().bigDecimalValue();
                
                String insertStr = "INSERT INTO KIANR0A (ID_MSG_FK , " + 
                    "		EVN_TYPE_CODE," +
                    "           EVN_OCCURED," + 
                    "		PID3_CF ," + 
                    "		PID3_CS," + 
                    "		PID3_ID_MPI ," + 
                    "		PID3_STP," + 
                    "		PID7_BIRTH_DATE," + 
                    "		PID11_COM_NASC_ISTAT," + 
                    "		PID5_SURNAME ," + 
                    "		PID5_NAME ," + 
                    "		PID8_SEX ," + 
                    "		PV1_CAT_CITTADINO," + 
                    "		PD1_3_ULSS_COD_ISTAT," + 
                    "		PID11_COM_RES_ISTAT," + 
                    "		PID11_COM_DOM_ISTAT," + 
                    "		PID11_RES_STREET," + 
                    "		PID11_RES_NUMBER, " + 
                    "		PID11_DOM_STREET," + 
                    "		PID11_DOM_NUMBER," +
                    "           Pid11_Cap_Dom_Com," +
                    "           Pid11_Cap_Res_Com," +
                    "		PID13_NUMBER_PHONE_RES," + 
                    "		PID13_NUMBER_PHONE_DOM," + 
                    "		PV1_MED_DATE_END, " + 
                    "		PV1_CREG_MED," + 
                    "		PV1_NOM_MED," + 
                    "		PV1_COGN_MED," + 
                    "           Pid26_Cod_Istat_Citt," +
                    "		PID29_DEATH_DATE_TIME ," + 
                    "		PID3_ENI ," + 
                    "		PID3_TEAM_IDENT ) VALUES (" + 
                    idMsg + ", " +
                    "	        'A08', " +  //aggiornamento anagraficoo
                    "   TO_CHAR(SYSDATE,'yyyymmddHH24miss'), '"+  
                    persona.getFiscalCode().replaceAll("'", "''") + "', '"+
                    persona.getCs().replaceAll("'", "''") + "', '"+
                    persona.getMpi().replaceAll("'", "''")+ "', '"+
                    persona.getStp().replaceAll("'", "''") + "', '"+
                    (persona.getBirthTimeDate() != null ? outFormat.format(persona.getBirthTimeDate()) : "") + "', '"+
                    persona.getBirthplaceCode().replaceAll("'", "''")+ "', '"+
                    persona.getNameFam().replaceAll("'", "''")+ "', '"+
                    persona.getNameGiv().replaceAll("'", "''")+ "', '"+
                    persona.getGenderCode().replaceAll("'", "''")+ "', '"+
                    persona.getCategory().replaceAll("'", "''") + "', '"+
                    persona.getIstatUlssAss().replaceAll("'", "''") + "', '"+
                    persona.getAddrCode().replaceAll("'", "''")+ "', '"+
                    persona.getDomAddrCode().replaceAll("'", "''")+ "', '"+
                    persona.getAddrStr().replaceAll("'", "''")+ "', '"+
                    persona.getAddrBnr().replaceAll("'", "''")+ "', '"+
                    persona.getDomAddrStr().replaceAll("'", "''")+ "', '"+
                    persona.getDomAddrBnr().replaceAll("'", "''")+ "', '"+
                    persona.getDomAddrZip().replaceAll("'", "''") + "', '"+
                    persona.getAddrZip().replaceAll("'", "''") + "', '"+
                    persona.getTelecomHp().replaceAll("'", "''")+ "', '"+
                    persona.getTelecomMc().replaceAll("'", "''")+ "', '"+
                    (persona.getMmgDateEndDate() != null ? outFormat.format(persona.getMmgDateEndDate()) : "") + "', '"+
                    persona.getMmgRegionalCode().replaceAll("'", "''")+ "', '"+
                    persona.getMmgNameGiv().replaceAll("'", "''")+ "', '"+
                    persona.getMmgNameFam().replaceAll("'", "''")+ "', '"+
                    persona.getCitizenship().replaceAll("'", "''")+ "', '"+
                    (persona.getDeathDateDate() != null ? outFormat.format(persona.getDeathDateDate()) : "") + "', '"+
                    persona.getEni().replaceAll("'", "''")+ "', '"+
                    persona.getStp().replaceAll("'", "''") + "' )";
                
                
                am.getTransaction().executeCommand(insertStr);
                
                SequenceImpl sElab = new SequenceImpl("SO_ELABORAZIONE_ANAG_SEQ", am);
                BigDecimal idElab = sElab.getSequenceNumber().bigDecimalValue();
                String elabIns  = "INSERT INTO SO_ELABORAZIONE_ANAG VALUES " +
                    "("+idElab+", " + idMsg +
                    ", '"+ulss+"', 'N', null, null, sysdate, null, null, null, null)";
                
                am.getTransaction().executeCommand(elabIns);
                am.getTransaction().commit();

                @SuppressWarnings("deprecation")
                ImpExp_AppModule impexpAm =
                    (ImpExp_AppModule) BindingContext.getCurrent().findDataControl("ImpExp_AppModuleDataControl").getDataProvider();
                String msgReturn = impexpAm.callImportAnagrafeSingola(ulss, idMsg);
                
                if (msgReturn == null) {
                    DCIteratorBinding _soggetti = bindings.findIteratorBinding("Sogg_SoSoggettoView1Iterator");
                    _soggetti.executeQuery();
                    ViewObject logImpexp = am.findViewObject("Sogg_SoLogImpexpHl7View1");
                    logImpexp.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND IDELAB = " + idElab);
                    logImpexp.executeQuery();
                    Row rLog = logImpexp.first();
                    if (rLog != null){
                        if(rLog.getAttribute("Coderrore")!=null && !"NOERROR".equalsIgnoreCase((String)rLog.getAttribute("Coderrore")))
                            throw new Exception ((String)rLog.getAttribute("Coderrore") + " - " + (String)rLog.getAttribute("Msg"));

                        FacesContext ctx = FacesContext.getCurrentInstance();
                        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aggionramento completato correttamente.", "");
                        ctx.addMessage(null, fm);
                    }
                } else {
                    throw new Exception (msgReturn);
                }
                
            } catch (Exception e){
                FacesContext ctx = FacesContext.getCurrentInstance();
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossibile aggiornare il soggetto: " + e.getMessage(), "");
                ctx.addMessage(null, fm);
            }
            
        } else {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Il soggetto non risulta presente nell'Anagrafe Unica Regionale", "");
            ctx.addMessage(null, fm);
        }
               
        return null;
    }
}
