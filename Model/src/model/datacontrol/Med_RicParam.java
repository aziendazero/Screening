package model.datacontrol;


import java.util.Date;

import model.common.Med_AppModule;

import model.commons.SoggUtils;

import oracle.adf.model.BindingContext;

public class Med_RicParam {
    String nome, cognome;
    Integer codMedico;
    String codCom, desCom;
    Integer rcCom;
   
    //parametri stampa 
    String codComu,desComu;
    Integer roundCom;
    Date dtIni,dtFin;
    Integer etaMin, etaMax;
    Date dtRif;
    String sesso;
    
    public void reset() {
        this.setCodMedico(null);
        this.setCognome(null);
        this.setNome(null);
        this.setCodCom(null);
        this.setDesCom(null);
    }
    
    public void resetCampiStampa()
      {
          Med_AppModule am =
              (Med_AppModule) BindingContext.getCurrent().findDataControl("Med_AppModuleDataControl").getDataProvider();
        
        this.setCodComu(null);
        this.setDesComu(null);
        this.setDtFin(null);
        this.setDtIni(null);
        this.setRoundCom(null);
        this.setEtaMin(null);
        this.setEtaMax(null);   
        this.setDtRif(new Date());
        String sessoIni = SoggUtils.getSessoScreening(am);
        this.setSesso(sessoIni);
        Integer[] etaScr = SoggUtils.getEtaScreening(am);
        this.setEtaMin(etaScr[0]);
        this.setEtaMax(etaScr[1]);
      }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCodMedico(Integer codMedico) {
        this.codMedico = codMedico;
    }

    public Integer getCodMedico() {
        return codMedico;
    }

    public void setCodCom(String codCom) {
        this.codCom = codCom;
    }

    public String getCodCom() {
        return codCom;
    }

    public void setDesCom(String desCom) {
        this.desCom = desCom;
    }

    public String getDesCom() {
        return desCom;
    }

    public void setRcCom(Integer rcCom) {
        this.rcCom = rcCom;
    }

    public Integer getRcCom() {
        return rcCom;
    }

    public void setCodComu(String codComu) {
        this.codComu = codComu;
    }

    public String getCodComu() {
        return codComu;
    }

    public void setDesComu(String desComu) {
        this.desComu = desComu;
    }

    public String getDesComu() {
        return desComu;
    }

    public void setRoundCom(Integer roundCom) {
        this.roundCom = roundCom;
    }

    public Integer getRoundCom() {
        return roundCom;
    }

    public void setDtIni(Date dtIni) {
        this.dtIni = dtIni;
    }

    public Date getDtIni() {
        return dtIni;
    }

    public void setDtFin(Date dtFin) {
        this.dtFin = dtFin;
    }

    public Date getDtFin() {
        return dtFin;
    }

    public void setEtaMin(Integer etaMin) {
        this.etaMin = etaMin;
    }

    public Integer getEtaMin() {
        return etaMin;
    }

    public void setEtaMax(Integer etaMax) {
        this.etaMax = etaMax;
    }

    public Integer getEtaMax() {
        return etaMax;
    }

    public void setDtRif(Date dtRif) {
        this.dtRif = dtRif;
    }

    public Date getDtRif() {
        return dtRif;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getSesso() {
        return sesso;
    }
}
