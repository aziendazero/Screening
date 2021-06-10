package model.datacontrol.aur;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "fiscalCode", "nameFam", "nameGiv", "cs", "mpi", "birthTime" })
public class AURFilter {

    public AURFilter() {
        super();
    }

    @JsonProperty("fiscalCode")
    private String fiscalCode;
    @JsonProperty("nameFam")
    private String nameFam;
    @JsonProperty("nameGiv")
    private String nameGiv;
    @JsonProperty("cs")
    private String cs;
    @JsonProperty("mpi")
    private String mpi;
    @JsonProperty("birthTime")
    private String birthTime;
    private Date birthTimeDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    private String dtfTSsend = "yyyy-MM-dd";

    @JsonProperty("fiscalCode")
    public String getFiscalCode() {
        return fiscalCode!=null ? fiscalCode.trim() : fiscalCode;
    }

    @JsonProperty("fiscalCode")
    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode!=null ? fiscalCode.trim() : fiscalCode;
    }

    public String getNameFam() {
        return nameFam!=null ? nameFam.trim() : nameFam;
    }

    public void setNameFam(String nameFam) {
        this.nameFam = nameFam!=null ? nameFam.trim() : nameFam;
    }

    public String getNameGiv() {
        return nameGiv!=null ? nameGiv.trim() : nameGiv;
    }

    public void setNameGiv(String nameGiv) {
        this.nameGiv = nameGiv!=null ? nameGiv.trim() : nameGiv;
    }

    @JsonProperty("nameFam")
    public String getNameFamJson() {
        return nameFam == null || nameFam.isEmpty() ? null : nameFam + "%";
    }

    @JsonProperty("nameGiv")
    public String getNameGivJson() {
        return nameGiv  == null || nameGiv.isEmpty() ? null : nameGiv + "%";
    }

    @JsonProperty("cs")
    public String getCs() {
        return cs!=null ? cs.trim() : cs;
    }

    @JsonProperty("cs")
    public void setCs(String cs) {
        this.cs = cs!=null ? cs.trim() : cs;
    }

    @JsonProperty("mpi")
    public String getMpi() {
        return mpi!=null ? mpi.trim() : mpi;
    }

    @JsonProperty("mpi")
    public void setMpi(String mpi) {
        this.mpi = mpi!=null ? mpi.trim() : mpi;
    }

    @JsonProperty("birthTime")
    public String getBirthTime() {
        if(birthTimeDate!=null)
           return new SimpleDateFormat(dtfTSsend).format(birthTimeDate);
        else
            return null;
    }

    @JsonProperty("birthTime")
    public void setBirthTime(String birthTime) {
        this.birthTime = birthTime;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void setBirthTimeDate(Date birthTimeDate) {
        this.birthTimeDate = birthTimeDate;
    }

    public Date getBirthTimeDate() {
        return birthTimeDate;
    }

    public void reset(){
        fiscalCode = null;
        nameFam = null;
        nameGiv = null;
        cs = null;
        mpi = null;
        birthTime = null;
        birthTimeDate = null;
    }
}
