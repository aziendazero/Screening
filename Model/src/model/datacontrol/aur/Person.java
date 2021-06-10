package model.datacontrol.aur;

import insiel.dataHandling.DateUtils;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.soggetto.Sogg_SoSoggettoViewRowImpl;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.Row;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaManager;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;
import oracle.jbo.server.SequenceImpl;
import oracle.jbo.server.ViewRowImpl;
import oracle.jbo.uicli.binding.JUIteratorBinding;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
                   "teamInst", "telecomHp", "mmgDateEnd", "reliability", "telecomMail", "birthTime", "domAddrStr",
                   "countryOfDom", "telecomPg", "countryOfBirth", "fiscalCode", "addrStr", "domAddrCode", "teamDateEnd",
                   "deathDate", "mmgDateBegin", "teamIdent", "telecomTmp", "mmgNameFam", "stpDateEnd", "mmgNameGiv",
                   "domAddrBnr", "csDateEnd", "stpDateBegin", "eni", "addrBnr", "nameFam", "mpi", "csDateBegin",
                   "teamCode", "mmgRegionalCode", "csRegion", "nameGiv", "eniDateBegin", "stp", "telecomEc", "cs",
                   "eniDateEnd", "genderCode", "addrCode", "countryOfAddr", "telecomH", "birthplaceCode", "telecomBad",
                   "telecomMc", "teamPers", "addrZip", "domAddrZip", "istatUlssAss", "citizenship", "category"
    })
public class Person {
    private static ADFLogger log = ADFLogger.createADFLogger(Person.class);
    private Object birthplaceCodeRow = null;
    private Object countryOfDomRow = null;
    private Object countryOfBirthRow = null;
    private Object domAddrCodeRow = null;
    private Object addrCodeRow = null;
    private Object countryOfAddrRow = null;
    private Object mmgRow = null;

    public Person() {
    }

    @JsonProperty("teamInst")
    private String teamInst;
    @JsonProperty("telecomHp")
    private String telecomHp;
    @JsonProperty("mmgDateEnd")
    private String mmgDateEnd;
    @JsonProperty("reliability")
    private String reliability;
    @JsonProperty("telecomMail")
    private String telecomMail;
    @JsonProperty("birthTime")
    private String birthTime;
    @JsonProperty("domAddrStr")
    private String domAddrStr;
    @JsonProperty("countryOfDom")
    private String countryOfDom;
    @JsonProperty("telecomPg")
    private String telecomPg;
    @JsonProperty("countryOfBirth")
    private String countryOfBirth;
    @JsonProperty("fiscalCode")
    private String fiscalCode;
    @JsonProperty("addrStr")
    private String addrStr;
    @JsonProperty("domAddrCode")
    private String domAddrCode;
    @JsonProperty("teamDateEnd")
    private String teamDateEnd;
    @JsonProperty("deathDate")
    private String deathDate;
    @JsonProperty("mmgDateBegin")
    private String mmgDateBegin;
    @JsonProperty("teamIdent")
    private String teamIdent;
    @JsonProperty("telecomTmp")
    private String telecomTmp;
    @JsonProperty("mmgNameFam")
    private String mmgNameFam;
    @JsonProperty("stpDateEnd")
    private String stpDateEnd;
    @JsonProperty("mmgNameGiv")
    private String mmgNameGiv;
    @JsonProperty("domAddrBnr")
    private String domAddrBnr;
    @JsonProperty("csDateEnd")
    private String csDateEnd;
    @JsonProperty("stpDateBegin")
    private String stpDateBegin;
    @JsonProperty("eni")
    private String eni;
    @JsonProperty("addrBnr")
    private String addrBnr;
    @JsonProperty("nameFam")
    private String nameFam;
    @JsonProperty("mpi")
    private String mpi;
    @JsonProperty("csDateBegin")
    private String csDateBegin;
    @JsonProperty("teamCode")
    private String teamCode;
    @JsonProperty("mmgRegionalCode")
    private String mmgRegionalCode;
    @JsonProperty("csRegion")
    private String csRegion;
    @JsonProperty("nameGiv")
    private String nameGiv;
    @JsonProperty("eniDateBegin")
    private String eniDateBegin;
    @JsonProperty("stp")
    private String stp;
    @JsonProperty("telecomEc")
    private String telecomEc;
    @JsonProperty("cs")
    private String cs;
    @JsonProperty("eniDateEnd")
    private String eniDateEnd;
    @JsonProperty("genderCode")
    private String genderCode;
    @JsonProperty("addrCode")
    private String addrCode;
    @JsonProperty("countryOfAddr")
    private String countryOfAddr;
    @JsonProperty("telecomH")
    private String telecomH;
    @JsonProperty("birthplaceCode")
    private String birthplaceCode;
    @JsonProperty("telecomBad")
    private String telecomBad;
    @JsonProperty("telecomMc")
    private String telecomMc;
    @JsonProperty("teamPers")
    private String teamPers;
    @JsonProperty("addrZip")
    private String addrZip;
    @JsonProperty("domAddrZip")
    private String domAddrZip;
    @JsonProperty("istatUlssAss")
    private String istatUlssAss;
    @JsonProperty("citizenship")
    private String citizenship;
    @JsonProperty("category")
    private String category;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("addrZip")
    public String getAddrZip() {
        return addrZip;
    }

    @JsonProperty("addrZip")
    public void setAddrZip(String addrZip) {
        this.addrZip = addrZip;
    }

    @JsonProperty("domAddrZip")
    public void setDomAddrZip(String domAddrZip) {
        this.domAddrZip = domAddrZip;
    }

    @JsonProperty("domAddrZip")
    public String getDomAddrZip() {
        return domAddrZip;
    }

    @JsonProperty("istatUlssAss")
    public void setIstatUlssAss(String istatUlssAss) {
        this.istatUlssAss = istatUlssAss;
    }

    @JsonProperty("istatUlssAss")
    public String getIstatUlssAss() {
        return istatUlssAss;
    }

    @JsonProperty("citizenship")
    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    @JsonProperty("citizenship")
    public String getCitizenship() {
        return citizenship;
    }

    @JsonProperty("category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("teamInst")
    public String getTeamInst() {
        return teamInst;
    }

    @JsonProperty("teamInst")
    public void setTeamInst(String teamInst) {
        this.teamInst = teamInst;
    }

    @JsonProperty("telecomHp")
    public String getTelecomHp() {
        return telecomHp;
    }

    @JsonProperty("telecomHp")
    public void setTelecomHp(String telecomHp) {
        this.telecomHp = telecomHp;
    }

    @JsonProperty("mmgDateEnd")
    public String getMmgDateEnd() {
        return mmgDateEnd;
    }

    @JsonProperty("mmgDateEnd")
    public void setMmgDateEnd(String mmgDateEnd) {
        this.mmgDateEnd = mmgDateEnd;
        this.mmgDateEndDate = null;

        if (mmgDateEnd != null && !mmgDateEnd.isEmpty()) {
            try {
                this.mmgDateEndDate = new SimpleDateFormat(dtfReturn).parse(mmgDateEnd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Date mmgDateEndDate;

    public void setMmgDateEndDate(Date mmgDateEndDate) {
        this.mmgDateEndDate = mmgDateEndDate;

    }

    public Date getMmgDateEndDate() {
        return mmgDateEndDate;
    }

    @JsonProperty("reliability")
    public String getReliability() {
        return reliability;
    }

    @JsonProperty("reliability")
    public void setReliability(String reliability) {
        this.reliability = reliability;
    }

    @JsonProperty("telecomMail")
    public String getTelecomMail() {
        return telecomMail;
    }

    @JsonProperty("telecomMail")
    public void setTelecomMail(String telecomMail) {
        this.telecomMail = telecomMail;
    }

    @JsonProperty("birthTime")
    public String getBirthTime() {
        return birthTime;
    }

    @JsonProperty("birthTime")
    public void setBirthTime(String birthTime) {
        this.birthTime = birthTime;
        this.birthTimeDate = null;

        if (birthTime != null && !birthTime.isEmpty()) {
            try {
                this.birthTimeDate = new SimpleDateFormat(dtfReturn).parse(birthTime);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    @JsonProperty("domAddrStr")
    public String getDomAddrStr() {
        return domAddrStr;
    }

    @JsonProperty("domAddrStr")
    public void setDomAddrStr(String domAddrStr) {
        this.domAddrStr = domAddrStr;
    }

    @JsonProperty("countryOfDom")
    public String getCountryOfDom() {
        return countryOfDom;
    }

    @JsonProperty("countryOfDom")
    public void setCountryOfDom(String countryOfDom) {
        this.countryOfDom = countryOfDom;

        countryOfDomRow = findRow("Sogg_StatoView1Iterator", "Codst", this.countryOfDom);
    }

    @JsonProperty("telecomPg")
    public String getTelecomPg() {
        return telecomPg;
    }

    @JsonProperty("telecomPg")
    public void setTelecomPg(String telecomPg) {
        this.telecomPg = telecomPg;
    }

    @JsonProperty("countryOfBirth")
    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    @JsonProperty("countryOfBirth")
    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;

        countryOfBirthRow = findRow("Sogg_StatoView1Iterator", "Codst", this.countryOfBirth);
    }

    @JsonProperty("fiscalCode")
    public String getFiscalCode() {
        return fiscalCode;
    }

    @JsonProperty("fiscalCode")
    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    @JsonProperty("addrStr")
    public String getAddrStr() {
        return addrStr;
    }

    @JsonProperty("addrStr")
    public void setAddrStr(String addrStr) {
        this.addrStr = addrStr;
    }

    @JsonProperty("domAddrCode")
    public String getDomAddrCode() {
        return domAddrCode;
    }

    @JsonProperty("domAddrCode")
    public void setDomAddrCode(String domAddrCode) {
        this.domAddrCode = domAddrCode;

        domAddrCodeRow = findRow("Sogg_ComuneAURView1Iterator", "Codcom", this.domAddrCode);
    }

    @JsonProperty("teamDateEnd")
    public String getTeamDateEnd() {
        return teamDateEnd;
    }

    @JsonProperty("teamDateEnd")
    public void setTeamDateEnd(String teamDateEnd) {
        this.teamDateEnd = teamDateEnd;
        this.teamDateEndDate = null;

        if (teamDateEnd != null && !teamDateEnd.isEmpty()) {
            try {
                this.teamDateEndDate = new SimpleDateFormat(dtfReturn).parse(teamDateEnd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Date teamDateEndDate;

    public void setTeamDateEndDate(Date teamDateEndDate) {
        this.teamDateEndDate = teamDateEndDate;
    }

    public Date getTeamDateEndDate() {
        return teamDateEndDate;
    }

    @JsonProperty("deathDate")
    public String getDeathDate() {
        return deathDate;
    }

    @JsonProperty("deathDate")
    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
        this.deathDateDate = null;

        if (deathDate != null && !deathDate.isEmpty()) {
            try {
                this.deathDateDate = new SimpleDateFormat(dtfReturn).parse(deathDate);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Date deathDateDate;

    public void setDeathDateDate(Date deathDateDate) {
        this.deathDateDate = deathDateDate;
    }

    public Date getDeathDateDate() {
        return deathDateDate;
    }

    @JsonProperty("mmgDateBegin")
    public String getMmgDateBegin() {
        return mmgDateBegin;
    }

    @JsonProperty("mmgDateBegin")
    public void setMmgDateBegin(String mmgDateBegin) {
        this.mmgDateBegin = mmgDateBegin;
        this.mmgDateBeginDate = null;

        if (mmgDateBegin != null && !mmgDateBegin.isEmpty()) {
            try {
                this.mmgDateBeginDate = new SimpleDateFormat(dtfReturn).parse(mmgDateBegin);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Date mmgDateBeginDate;

    public void setMmgDateBeginDate(Date mmgDateBeginDate) {
        this.mmgDateBeginDate = mmgDateBeginDate;

    }

    public Date getMmgDateBeginDate() {
        return mmgDateBeginDate;
    }

    @JsonProperty("teamIdent")
    public String getTeamIdent() {
        return teamIdent;
    }

    @JsonProperty("teamIdent")
    public void setTeamIdent(String teamIdent) {
        this.teamIdent = teamIdent;
    }

    @JsonProperty("telecomTmp")
    public String getTelecomTmp() {
        return telecomTmp;
    }

    @JsonProperty("telecomTmp")
    public void setTelecomTmp(String telecomTmp) {
        this.telecomTmp = telecomTmp;
    }

    @JsonProperty("mmgNameFam")
    public String getMmgNameFam() {
        return mmgNameFam;
    }

    @JsonProperty("mmgNameFam")
    public void setMmgNameFam(String mmgNameFam) {
        this.mmgNameFam = mmgNameFam;
    }

    @JsonProperty("stpDateEnd")
    public String getStpDateEnd() {
        return stpDateEnd;
    }

    @JsonProperty("stpDateEnd")
    public void setStpDateEnd(String stpDateEnd) {
        this.stpDateEnd = stpDateEnd;
        this.stpDateEndDate = null;

        if (stpDateEnd != null && !stpDateEnd.isEmpty()) {
            try {
                this.stpDateEndDate = new SimpleDateFormat(dtfReturn).parse(stpDateEnd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

    }

    private Date stpDateEndDate;

    public void setStpDateEndDate(Date stpDateEndDate) {
        this.stpDateEndDate = stpDateEndDate;

    }

    public Date getStpDateEndDate() {
        return stpDateEndDate;
    }

    @JsonProperty("mmgNameGiv")
    public String getMmgNameGiv() {
        return mmgNameGiv;
    }

    @JsonProperty("mmgNameGiv")
    public void setMmgNameGiv(String mmgNameGiv) {
        this.mmgNameGiv = mmgNameGiv;
    }

    @JsonProperty("domAddrBnr")
    public String getDomAddrBnr() {
        return domAddrBnr;
    }

    @JsonProperty("domAddrBnr")
    public void setDomAddrBnr(String domAddrBnr) {
        this.domAddrBnr = domAddrBnr;
    }

    @JsonProperty("csDateEnd")
    public String getCsDateEnd() {
        return csDateEnd;
    }

    @JsonProperty("csDateEnd")
    public void setCsDateEnd(String csDateEnd) {
        this.csDateEnd = csDateEnd;
        this.csDateEndDate = null;

        if (csDateEnd != null && !csDateEnd.isEmpty()) {
            try {
                this.csDateEndDate = new SimpleDateFormat(dtfReturn).parse(csDateEnd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

    }

    private Date csDateEndDate;

    public void setCsDateEndDate(Date csDateEndDate) {
        this.csDateEndDate = csDateEndDate;

    }

    public Date getCsDateEndDate() {
        return csDateEndDate;
    }

    @JsonProperty("stpDateBegin")
    public String getStpDateBegin() {
        return stpDateBegin;
    }

    @JsonProperty("stpDateBegin")
    public void setStpDateBegin(String stpDateBegin) {
        this.stpDateBegin = stpDateBegin;
        this.stpDateBeginDate = null;

        if (stpDateBegin != null && !stpDateBegin.isEmpty()) {
            try {
                this.stpDateBeginDate = new SimpleDateFormat(dtfReturn).parse(stpDateBegin);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Date stpDateBeginDate;

    public void setStpDateBeginDate(Date stpDateBeginDate) {
        this.stpDateBeginDate = stpDateBeginDate;
    }

    public Date getStpDateBeginDate() {
        return stpDateBeginDate;
    }

    @JsonProperty("eni")
    public String getEni() {
        return eni;
    }

    @JsonProperty("eni")
    public void setEni(String eni) {
        this.eni = eni;
    }

    @JsonProperty("addrBnr")
    public String getAddrBnr() {
        return addrBnr;
    }

    @JsonProperty("addrBnr")
    public void setAddrBnr(String addrBnr) {
        this.addrBnr = addrBnr;
    }

    @JsonProperty("nameFam")
    public String getNameFam() {
        return nameFam;
    }

    @JsonProperty("nameFam")
    public void setNameFam(String nameFam) {
        this.nameFam = nameFam;
    }

    @JsonProperty("mpi")
    public String getMpi() {
        return mpi;
    }

    @JsonProperty("mpi")
    public void setMpi(String mpi) {
        this.mpi = mpi;
    }

    @JsonProperty("csDateBegin")
    public String getCsDateBegin() {
        return csDateBegin;
    }

    @JsonProperty("csDateBegin")
    public void setCsDateBegin(String csDateBegin) {
        this.csDateBegin = csDateBegin;
        this.csDateBeginDate = null;

        if (csDateBegin != null && !csDateBegin.isEmpty()) {
            try {
                this.csDateBeginDate = new SimpleDateFormat(dtfReturn).parse(csDateBegin);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Date csDateBeginDate;

    public void setCsDateBeginDate(Date csDateBeginDate) {
        this.csDateBeginDate = csDateBeginDate;

    }

    public Date getCsDateBeginDate() {
        return csDateBeginDate;
    }

    @JsonProperty("teamCode")
    public String getTeamCode() {
        return teamCode;
    }

    @JsonProperty("teamCode")
    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    @JsonProperty("mmgRegionalCode")
    public String getMmgRegionalCode() {
        return mmgRegionalCode;
    }

    @JsonProperty("mmgRegionalCode")
    public void setMmgRegionalCode(String mmgRegionalCode) {
        this.mmgRegionalCode = mmgRegionalCode;

        if (mmgRegionalCode != null && !mmgRegionalCode.isEmpty())
            mmgRow = findRowMmg();
    }

    @JsonProperty("csRegion")
    public String getCsRegion() {
        return csRegion;
    }

    @JsonProperty("csRegion")
    public void setCsRegion(String csRegion) {
        this.csRegion = csRegion;
    }

    @JsonProperty("nameGiv")
    public String getNameGiv() {
        return nameGiv;
    }

    @JsonProperty("nameGiv")
    public void setNameGiv(String nameGiv) {
        this.nameGiv = nameGiv;
    }

    @JsonProperty("eniDateBegin")
    public String getEniDateBegin() {
        return eniDateBegin;
    }

    @JsonProperty("eniDateBegin")
    public void setEniDateBegin(String eniDateBegin) {
        this.eniDateBegin = eniDateBegin;
        this.eniDateBeginDate = null;

        if (eniDateBegin != null && !eniDateBegin.isEmpty()) {
            try {
                this.eniDateBeginDate = new SimpleDateFormat(dtfReturn).parse(eniDateBegin);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Date eniDateBeginDate;

    public void setEniDateBeginDate(Date eniDateBeginDate) {
        this.eniDateBeginDate = eniDateBeginDate;

    }

    public Date getEniDateBeginDate() {
        return eniDateBeginDate;
    }

    @JsonProperty("stp")
    public String getStp() {
        return stp;
    }

    @JsonProperty("stp")
    public void setStp(String stp) {
        this.stp = stp;
    }

    @JsonProperty("telecomEc")
    public String getTelecomEc() {
        return telecomEc;
    }

    @JsonProperty("telecomEc")
    public void setTelecomEc(String telecomEc) {
        this.telecomEc = telecomEc;
    }

    @JsonProperty("cs")
    public String getCs() {
        return cs;
    }

    @JsonProperty("cs")
    public void setCs(String cs) {
        this.cs = cs;
    }

    @JsonProperty("eniDateEnd")
    public String getEniDateEnd() {
        return eniDateEnd;
    }

    @JsonProperty("eniDateEnd")
    public void setEniDateEnd(String eniDateEnd) {
        this.eniDateEnd = eniDateEnd;
        this.eniDateEndDate = null;

        if (eniDateEnd != null && !eniDateEnd.isEmpty()) {
            try {
                this.eniDateEndDate = new SimpleDateFormat(dtfReturn).parse(eniDateEnd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private Date eniDateEndDate;

    public void setEniDateEndDate(Date eniDateEndDate) {
        this.eniDateEndDate = eniDateEndDate;

    }

    public Date getEniDateEndDate() {
        return eniDateEndDate;
    }

    @JsonProperty("genderCode")
    public String getGenderCode() {
        return genderCode;
    }

    @JsonProperty("genderCode")
    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    @JsonProperty("addrCode")
    public String getAddrCode() {
        return addrCode;
    }

    @JsonProperty("addrCode")
    public void setAddrCode(String addrCode) {
        this.addrCode = addrCode;

        addrCodeRow = findRow("Sogg_ComuneAURView1Iterator", "Codcom", this.addrCode);
    }

    @JsonProperty("countryOfAddr")
    public String getCountryOfAddr() {
        return countryOfAddr;
    }

    @JsonProperty("countryOfAddr")
    public void setCountryOfAddr(String countryOfAddr) {
        this.countryOfAddr = countryOfAddr;

        countryOfAddrRow = findRow("Sogg_StatoView1Iterator", "Codst", this.countryOfAddr);
    }

    @JsonProperty("telecomH")
    public String getTelecomH() {
        return telecomH;
    }

    @JsonProperty("telecomH")
    public void setTelecomH(String telecomH) {
        this.telecomH = telecomH;
    }

    @JsonProperty("birthplaceCode")
    public String getBirthplaceCode() {
        return birthplaceCode;
    }

    @JsonProperty("birthplaceCode")
    public void setBirthplaceCode(String birthplaceCode) {
        this.birthplaceCode = birthplaceCode;

        birthplaceCodeRow = findRow("Sogg_ComuneAURView1Iterator", "Codcom", this.birthplaceCode);
    }

    @JsonProperty("telecomBad")
    public String getTelecomBad() {
        return telecomBad;
    }

    @JsonProperty("telecomBad")
    public void setTelecomBad(String telecomBad) {
        this.telecomBad = telecomBad;
    }

    @JsonProperty("telecomMc")
    public String getTelecomMc() {
        return telecomMc;
    }

    @JsonProperty("telecomMc")
    public void setTelecomMc(String telecomMc) {
        this.telecomMc = telecomMc;
    }

    @JsonProperty("teamPers")
    public String getTeamPers() {
        return teamPers;
    }

    @JsonProperty("teamPers")
    public void setTeamPers(String teamPers) {
        this.teamPers = teamPers;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    private Date birthTimeDate;
    private String dtfReturn = "yyyy-MM-dd";

    public void setBirthTimeDate(Date birthTimeDate) {
        this.birthTimeDate = birthTimeDate;

    }

    public Date getBirthTimeDate() {
        return birthTimeDate;
    }

    private Object findRow(String iteratorTableName, String viewObjectAttributeFinder, String valueFind) {
        if (valueFind == null || valueFind.isEmpty())
            return null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application app = facesContext.getApplication();
        ExpressionFactory elFactory = app.getExpressionFactory();
        ELContext elContext = facesContext.getELContext();

        Object _oi =
            elFactory.createValueExpression(elContext, "#{bindings." + iteratorTableName + "}",
                                            Object.class).getValue(elContext);

        if (_oi != null) {
            ViewObject vo = ((JUIteratorBinding) _oi).getViewObject();

            findRow(vo, viewObjectAttributeFinder, valueFind, true);
            if (vo.getEstimatedRowCount() > 1)
                log.warning("findRow iterator: " + iteratorTableName + " : retrieve more rows: " +
                            vo.getEstimatedRowCount() + " with key value: " + viewObjectAttributeFinder + " = " +
                            valueFind + ". Returned only first row.");


            return vo.first();

        } else
            log.severe("findRow iterator: " + iteratorTableName + " : NOT FOUND");

        return null;
    }

    public Object getCountryOfDomRow() {
        return countryOfDomRow;
    }

    public Object getCountryOfBirthRow() {
        return countryOfBirthRow;
    }

    public Object getDomAddrCodeRow() {
        return domAddrCodeRow;
    }

    public Object getAddrCodeRow() {
        return addrCodeRow;
    }

    public Object getCountryOfAddrRow() {
        return countryOfAddrRow;
    }

    public Object getBirthplaceCodeRow() {
        return birthplaceCodeRow;
    }

    public Object getMmgRow() {
        return mmgRow;
    }

    private static void findRow(ViewObject vo, String viewObjectAttributeFinder, String valueFind, boolean append) {
        vo.setRangeSize(-1);
        ViewCriteriaManager vcm = vo.getViewCriteriaManager();

        vcm.removeApplyViewCriteriaName("clauseCriteria");
        ViewCriteria clauseCriteria = vo.createViewCriteria();
        clauseCriteria.setName("clauseCriteria");

        String clause = "=" + valueFind;

        ViewCriteriaRow criteriaRow = clauseCriteria.createViewCriteriaRow();
        criteriaRow.setAttribute(vo.getAttributeIndexOf(viewObjectAttributeFinder), clause);

        clauseCriteria.addElement(criteriaRow);

        //Apply the criteria (appended to the existing one => true)
        vcm.applyViewCriteria(clauseCriteria, append);

        vo.executeQuery();
    }

    private Object findRowMmg() {
        Integer cod = null;
        try{
            cod = new Integer(mmgRegionalCode).intValue();
        }catch(Throwable th){
            return null;
        }
        
        if(cod==null || cod==0)
            return null;
        
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();

        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding voIterMmg = bindings.findIteratorBinding("Sogg_SoMedicoAURView1Iterator");

        ViewObject vo = voIterMmg.getViewObject();
        vo.setRangeSize(-1);

        String whcl = "ULSS = '" + ((String) sess.get("ulss")) + "'";
        whcl += " and Codiceregmedico = " + cod;

        vo.setWhereClause(whcl);
        vo.executeQuery();

        if (vo.getEstimatedRowCount() > 1)
            log.warning("findRow iterator: Sogg_SoMedicoAURView1Iterator: retrieve more rows: " +
                        vo.getEstimatedRowCount() + " with key value: Codiceregmedico = " + mmgRegionalCode +
                        ". Returned only first row.");

        return vo.first();
    }

    @SuppressWarnings({ "unchecked", "oracle.jdeveloper.java.insufficient-catch-block" })
    private void importAurAnag() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.put("showTabs", Boolean.FALSE);
        session.put("LINK_ACC", Boolean.FALSE);
        session.put("LINK_REF", Boolean.FALSE);
        session.put("anagEsiste", Boolean.FALSE);
        session.put("invitoPresente", Boolean.FALSE);

        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding voIterAUR = bindings.findIteratorBinding("Sogg_SoSoggettoView1Iterator");

        Sogg_AppModule am = (Sogg_AppModule) voIterAUR.getViewObject().getApplicationModule();

        // creo nuova anag
        ViewObject voAnag = voIterAUR.getViewObject();
        voAnag.setWhereClause("1=2");
        voAnag.executeQuery();

        Sogg_SoSoggettoViewRowImpl nAnag = (Sogg_SoSoggettoViewRowImpl) voAnag.createRow();

        String ulss = (String) session.get("ulss");
        String user = (String) session.get("user");
        String tpscr = (String) session.get("scr");
        oracle.jbo.domain.Date dtoraCorr = DateUtils.getOracleDateNow();
        nAnag.setUlss(ulss);
        nAnag.setDtcreazione(dtoraCorr);
        nAnag.setOpcreazione(user);
        nAnag.setDtultmodifica(dtoraCorr);
        nAnag.setOpultmodifica(user);
        nAnag.setCodanagreg(0);

        DCIteratorBinding _catIterator = bindings.findIteratorBinding("Sogg_CategoriaView1Iterator");
        ViewObject voCat = _catIterator.getViewObject();

        voCat.setNamedWhereClauseParam("category", category);
        voCat.setNamedWhereClauseParam("istatUlssAss", istatUlssAss);
        voCat.setNamedWhereClauseParam("addrZip", addrZip);
        voCat.setNamedWhereClauseParam("domAddrZip", domAddrZip);
        voCat.setNamedWhereClauseParam("ulss", ulss);

        voCat.executeQuery();

        String _v = null;
        if (voCat.getEstimatedRowCount() > 0) {
            Row _r = voCat.first();

            if (_r != null)
                _v = (String) _r.getAttribute("Codreganag");
        }

        if (_v != null && !_v.isEmpty() && !_v.equalsIgnoreCase("x"))
            try {
                // effettuo la transcodifica
                String qryTransc = "SELECT codanagreg FROM so_cnf_anag_ulss ";
                qryTransc += " where ulss = '" + ulss + "'";
                qryTransc += " and codanagulss = '" + _v + "'";

                ViewObject voEsclPres = am.createViewObjectFromQueryStmt("", qryTransc);
                voEsclPres.setRangeSize(-1);
                voEsclPres.executeQuery();

                int count = voEsclPres.getRowCount();
                
                if(count>0){
                    Row transcRow = voEsclPres.first();
                    if(transcRow!=null && transcRow.getAttribute("CODANAGREG")!=null)
                        nAnag.setCodanagreg(((oracle.jbo.domain.Number)transcRow.getAttribute("CODANAGREG")).intValue());
                }
            } catch (Throwable th) {
                log.warning("Codreganag", th);
            }

        //creo un codts
        SequenceImpl s = new SequenceImpl("SO_CODTS_SEQ", am);
        Integer idCodts = s.getSequenceNumber().intValue();
        String codtsString = idCodts.toString();
        codtsString = "S" + "0000000000".substring(codtsString.length()) + codtsString;
        nAnag.setCodts(codtsString);

        nAnag.setIdinterno(mpi);
        nAnag.setCf(fiscalCode);
        nAnag.setCognome(nameFam);
        nAnag.setNome(nameGiv);

        if (birthTimeDate != null)
            nAnag.setDataNascita(DateUtils.getOracleDate(birthTimeDate));

        nAnag.setSesso(genderCode);

        if (birthplaceCodeRow != null) {
            nAnag.setReleaseCodeComNas(((Integer) (((ViewRowImpl) birthplaceCodeRow).getAttribute("ReleaseCode"))));
            nAnag.setCodcomnascita(((String) (((ViewRowImpl) birthplaceCodeRow).getAttribute("Codcom"))));
            nAnag.setDescrizione(((String) (((ViewRowImpl) birthplaceCodeRow).getAttribute("Descrizione"))));
            nAnag.setReleaseCodePr(((Integer) (((ViewRowImpl) birthplaceCodeRow).getAttribute("ReleaseCodePr"))));
        }

        if (countryOfBirthRow != null && birthplaceCodeRow == null) {
            nAnag.setReleaseCodeStNas(((oracle.jbo.domain.Number) (((ViewRowImpl) countryOfBirthRow).getAttribute("ReleaseCode"))).intValue());
            nAnag.setCodst((String) (((ViewRowImpl) countryOfBirthRow).getAttribute("Codst")));
            nAnag.setDesstato((String) (((ViewRowImpl) countryOfBirthRow).getAttribute("Descrizione")));
        }

        if (addrStr != null && !addrStr.trim().isEmpty()){
            nAnag.setIndirizzoRes(addrStr + " " + addrBnr);
        }

        if (addrCodeRow != null) {
            nAnag.setReleaseCodeComRes((Integer) (((ViewRowImpl) addrCodeRow).getAttribute("ReleaseCode")));
            nAnag.setCodcomres((String) (((ViewRowImpl) addrCodeRow).getAttribute("Codcom")));
            nAnag.setDescomres((String) (((ViewRowImpl) addrCodeRow).getAttribute("Descrizione")));
            nAnag.setReleaseCodePrRes(((Integer) (((ViewRowImpl) addrCodeRow).getAttribute("ReleaseCodePr"))));
        }

        if (countryOfAddrRow != null) {
            nAnag.setReleaseCodeStRes(((oracle.jbo.domain.Number) (((ViewRowImpl) countryOfAddrRow).getAttribute("ReleaseCode"))).intValue());
            nAnag.setCodstres((String) (((ViewRowImpl) countryOfAddrRow).getAttribute("Codst")));
        }

        if (domAddrStr != null && !domAddrStr.trim().isEmpty()){
            nAnag.setIndirizzoDom(domAddrStr + " " + domAddrBnr);
        }

        if (domAddrCodeRow != null) {
            nAnag.setReleaseCodeComDom(((Integer) (((ViewRowImpl) domAddrCodeRow).getAttribute("ReleaseCode"))));
            nAnag.setCodcomdom((String) (((ViewRowImpl) domAddrCodeRow).getAttribute("Codcom")));
            nAnag.setDescomdom((String) (((ViewRowImpl) domAddrCodeRow).getAttribute("Descrizione")));
            nAnag.setReleaseCodePrDom(((Integer) (((ViewRowImpl) domAddrCodeRow).getAttribute("ReleaseCodePr"))));
        }

        if (countryOfDomRow != null) {
            nAnag.setReleaseCodeStDom(((oracle.jbo.domain.Number) (((ViewRowImpl) countryOfDomRow).getAttribute("ReleaseCode"))).intValue());
            nAnag.setCodstdom((String) (((ViewRowImpl) countryOfDomRow).getAttribute("Codst")));
        }

        nAnag.setTel1(telecomH);
        nAnag.setTel2(telecomHp + " " + telecomBad);

        if(telecomMc!=null && !telecomMc.trim().isEmpty()){
            try{
                String str = ( telecomMc.substring(0, 1) + telecomMc.substring(1).replaceAll("[^a-zA-Z0-9]",  "")).replaceAll("[^a-zA-Z0-9 +]",  "");
                nAnag.setCellulare(str);
            }catch(Throwable th){}
        }

        if(telecomMail!=null && !telecomMail.trim().isEmpty())
            nAnag.setEmail(telecomMail);

        if (deathDateDate != null)
            nAnag.setDtdecesso(DateUtils.getOracleDate(deathDateDate));

        // VERIFICO IL MEDICO
        if (mmgRegionalCode != null && !mmgRegionalCode.isEmpty()) {
            Integer cod = 0;
            try{
                cod = new Integer(mmgRegionalCode).intValue();
            }catch(Throwable th){
            }
            
            if (mmgRow == null && cod>0) {
                DCIteratorBinding voIterMmg = bindings.findIteratorBinding("Sogg_SoMedicoAURView1Iterator");
                ViewObject vo = voIterMmg.getViewObject();

                Row _mmgRow = vo.createRow();
                _mmgRow.setAttribute("Codiceregmedico", Integer.valueOf(mmgRegionalCode));
                _mmgRow.setAttribute("Cognome", mmgNameFam);
                _mmgRow.setAttribute("Nome", mmgNameGiv);
                _mmgRow.setAttribute("Ulss", ulss);

                if (getMmgDateBeginDate() != null)
                    _mmgRow.setAttribute("Dtadesione", DateUtils.getOracleDate(getMmgDateBeginDate()));

                if (getMmgDateEndDate() != null)
                    _mmgRow.setAttribute("Dtfinevalop", DateUtils.getOracleDate(getMmgDateEndDate()));

                vo.insertRow(_mmgRow);
                vo.getApplicationModule().getTransaction().commit();

                mmgRow = _mmgRow;
            }

            if(mmgRow!=null){
                nAnag.setCodiceregmedico(((oracle.jbo.domain.Number) (((ViewRowImpl) mmgRow).getAttribute("Codiceregmedico"))).intValue());
                nAnag.setCognmed(((String) (((ViewRowImpl) mmgRow).getAttribute("Cognome"))));
                nAnag.setNomemed(((String) (((ViewRowImpl) mmgRow).getAttribute("Nome"))));
            }
        }
        
       importDocument(nAnag);
        
        voAnag.insertRow(nAnag);
        voAnag.setCurrentRow(nAnag);

        // inserisco record anche su SO_SOGG_SCR
        ViewObject voSoggScr = am.findViewObject("Sogg_SoSoggScrView1");
        Row r = voSoggScr.createRow();
        voSoggScr.insertRow(r);

        r.setAttribute("Codts", codtsString);
        r.setAttribute("Roundindiv", new Integer(0));
        r.setAttribute("Roundinviti", new Integer(0));
        r.setAttribute("Altorischio", ViewHelper.decodeByTpscr(tpscr, null, ConfigurationConstants.DB_FALSE, //altorischio e' valorizzato solo per il colon
                                                               null, null, null));
        r.setAttribute("VaccinatoHpv", new Integer(0));
        r.setAttribute("Consenso", new Integer(0));
        r.setAttribute("ConsensoCond", new Integer(0));
        r.setAttribute("Tpscr", tpscr);
        r.setAttribute("Ulss", ulss);

        voSoggScr.setCurrentRow(r);
    }

    public String importAnagrafe() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding _soggImport = bindings.findIteratorBinding("Sogg_RicercaViewAURCheckIterator");
        ViewObject vo = _soggImport.getViewObject();

        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");

        vo.setWhereClauseParams(new Object[] { ulss, tpscr, ulss, tpscr, tpscr });

        String whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' and SIGLA <> 'ME'";

        whcl += " and (CF = '" + fiscalCode + "' OR IDINTERNO = '" + mpi + "')";

        vo.setWhereClause(whcl);
        vo.executeQuery();

        if (vo.getEstimatedRowCount() == 0) {
            log.info("Soggetto non trovato --> creo nuovo");
            importAurAnag();
            return "goAnag";
        } else {
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesMessage fm =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 "Impossibile inserire il soggetto: già presente nalla base dati", "");
            ctx.addMessage(null, fm);
        }

        return null;
    }


    public void importDocument(Object anagOnj) {
        Sogg_SoSoggettoViewRowImpl anag = (Sogg_SoSoggettoViewRowImpl) anagOnj;
        // carico le voci di dizionario
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();

        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding _soggImport = bindings.findIteratorBinding("Sogg_SoCnfTipiDocView1Iterator");
        ViewObject vo = _soggImport.getViewObject();

        String ulss = (String) session.get("ulss");

        vo.setWhereClause("ULSS ='"+ulss+"'");
        //vo.setWhereClauseParams(new Object[] { ulss });
        vo.setRangeSize(-1);
        
        vo.executeQuery();
        // Tessera sanitaria
        String tesseraSanitaria = cs;
        try {
            Row[] _td =  vo.getFilteredRows("IdTipoDoc", "TS");
            if (_td == null)
                log.warning("document 'CS' is not defined in SO_CNF_TIPI_DOC TABLE");
            else if (tesseraSanitaria != null && !tesseraSanitaria.isEmpty())
                createDocument(anag, (String)_td[0].getAttribute("IdTipoDoc"), "CS", (String)_td[0].getAttribute("Descrizione"));
        } catch (Throwable th) {
            log.warning("Document is not created", th);
        }

        // STP
        String STP = stp;
        try {
            Row[] _td = vo.getFilteredRows("IdTipoDoc", "STP");
            if (_td == null)
                log.warning("document 'STP' is not defined in SO_CNF_TIPI_DOC TABLE");
            else if (STP != null && !STP.isEmpty())
                createDocument(anag, (String)_td[0].getAttribute("IdTipoDoc"), "STP", (String)_td[0].getAttribute("Descrizione"));
        } catch (Throwable th) {
            log.warning("Document is not created", th);
        }

        // ENI
        String ENI = eni;
        try {
            Row[] _td = vo.getFilteredRows("IdTipoDoc", "ENI");
            if (_td == null)
                log.warning("document 'ENI' is not defined in SO_CNF_TIPI_DOC TABLE");
            else if (ENI != null && !ENI.isEmpty())
                createDocument(anag, (String)_td[0].getAttribute("IdTipoDoc"), "ENI", (String)_td[0].getAttribute("Descrizione"));
        } catch (Throwable th) {
            log.warning("Document is not created", th);
        }

        // TEAM
        String TEAM = teamCode;
        try {
            Row[] _td =vo.getFilteredRows("IdTipoDoc", "TEAM");
            if (_td == null)
                log.warning("document 'TEAM' is not defined in SO_CNF_TIPI_DOC TABLE");
            else if (TEAM != null && !TEAM.isEmpty())
                createDocument(anag, (String)_td[0].getAttribute("IdTipoDoc"), "TEAM", (String)_td[0].getAttribute("Descrizione"));
        } catch (Throwable th) {
            log.warning("Document is not created", th);
        }
    }
 
 
    private void createDocument(Object anagOnj, String value, String tipoDoc, String DescrizioneTipoDoc) {
        Sogg_SoSoggettoViewRowImpl so = (Sogg_SoSoggettoViewRowImpl) anagOnj;       
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        String user = (String) session.get("user");
        oracle.jbo.domain.Date dtoraCorr = DateUtils.getOracleDateNow();

        Row _doc = so.getSogg_SoDocumentiSoggView().createRow();
        _doc.setAttribute("IdTipoDoc", value);
        _doc.setAttribute("DescrizioneTipoDoc", DescrizioneTipoDoc);
        
        _doc.setAttribute("Dtultmodifica", dtoraCorr);
        _doc.setAttribute("Opmodifica", user);
        
        _doc.setAttribute("Dtinserimento", dtoraCorr);
        _doc.setAttribute("Opinserimento", user);

        switch (tipoDoc) {
        case "CS":
            _doc.setAttribute("DocIdent", cs);
            _doc.setAttribute("Dtrilascio", csDateBeginDate);
            _doc.setAttribute("Dtfinevalidita", csDateEndDate);
            break;
        case "STP":
            _doc.setAttribute("DocIdent", stp);
            _doc.setAttribute("Dtrilascio", stpDateBeginDate);
            _doc.setAttribute("Dtfinevalidita", stpDateEndDate);

            _doc.setAttribute("IdIstComp", csRegion);
            break;
        case "ENI":
            _doc.setAttribute("DocIdent", eni);
            _doc.setAttribute("Dtrilascio", eniDateBeginDate);
            _doc.setAttribute("Dtfinevalidita", eniDateEndDate);

            _doc.setAttribute("IdIstComp", csRegion);
            break;
        case "TEAM":
            _doc.setAttribute("DocIdent", teamCode);
            _doc.setAttribute("Dtfinevalidita", teamDateEndDate);

            _doc.setAttribute("DocIdentAnag", teamPers);
            _doc.setAttribute("IdIstComp", teamInst);
            break;
        }
        
        so.getSogg_SoDocumentiSoggView().insertRow(_doc);
    }    
}
