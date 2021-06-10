package model.datacontrol.aur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "result", "persons", "AURServiceErrors", "internalId" })
public class AURResponse {
    public AURResponse() {
        super();
        //persons = new ArrayList<Person>();
        //persons.add(new Person());
    }
    @JsonProperty("result")
    private String result;
    @JsonProperty("persons")
    private List<Person> persons = new ArrayList<Person>();
    @JsonProperty("internalId")
    private String internalId;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("persons")
    public List<Person> getPersons() {
        return persons;
    }

    @JsonProperty("persons")
    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     *
     * @return
     *     The internalId
     */
    @JsonProperty("internalId")
    public String getInternalId() {
        return internalId;
    }
}
