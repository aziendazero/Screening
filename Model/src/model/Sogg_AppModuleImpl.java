package model;


import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.accettazione.A_SoAccCito1livView1Impl;
import model.accettazione.A_SoAccCito2livViewImpl;
import model.accettazione.A_SoAccColon1livViewImpl;
import model.accettazione.A_SoAccMammo1livViewImpl;
import model.accettazione.A_SoAccMammo2livViewImpl;

import model.common.Sogg_AppModule;

import model.commons.ConfigurationReader;
import model.commons.Parent_AppModuleImpl;

import model.conf.Cnf_EsclProposteViewImpl;
import model.conf.Cnf_SoCnfAnagRegViewImpl;
import model.conf.Cnf_SoCnfCentro1livViewImpl;
import model.conf.Cnf_SoCnfCentro2livViewImpl;
import model.conf.Cnf_SoCnfComuneViewImpl;

import model.conf.Cnf_SoCnfInvitiFastViewImpl;

import model.global.A_SoAllegatoViewImpl;
import model.global.A_SoAppuntamentoViewImpl;
import model.global.A_SoAziendaViewImpl;
import model.global.A_SoCentroPrelRefViewImpl;
import model.global.A_SoCnfComuniZoneParamViewImpl;
import model.global.A_SoCnfLetteracentroViewImpl;
import model.global.A_SoCnfParametriSistemaViewImpl;
import model.global.A_SoCnfPartemplateViewImpl;
import model.global.A_SoCnfTpinvitoViewImpl;
import model.global.A_SoCnfTpscrViewImpl;
import model.global.A_SoInvitoViewImpl;
import model.global.A_SoMedicoViewImpl;
import model.global.A_SoRoundorgViewImpl;
import model.global.A_SoSoggScrViewImpl;
import model.global.A_SoSoggettoViewImpl;
import model.global.A_SoTemplateViewImpl;

import model.soggetto.Sogg_CRichInvitoImpl;
import model.soggetto.Sogg_CnfEsitoImpl;
import model.soggetto.Sogg_ComuneAURViewImpl;
import model.soggetto.Sogg_ComuneViewImpl;
import model.soggetto.Sogg_CprelInvitoViewImpl;
import model.soggetto.Sogg_DistrettiViewImpl;
import model.soggetto.Sogg_EsclusioniCollegateViewImpl;
import model.soggetto.Sogg_EsitoViewImpl;
import model.soggetto.Sogg_MedicoViewImpl;
import model.soggetto.Sogg_MotEsclViewImpl;
import model.soggetto.Sogg_NuovoInvitoAttivoViewImpl;
import model.soggetto.Sogg_OrariDispViewImpl;
import model.soggetto.Sogg_ProvinciaViewImpl;
import model.soggetto.Sogg_QueryAgendaViewImpl;
import model.soggetto.Sogg_RicercaViewImpl;
import model.soggetto.Sogg_SelCprelViewImpl;
import model.soggetto.Sogg_SessoViewImpl;
import model.soggetto.Sogg_SinondViewImpl;
import model.soggetto.Sogg_SoChiaviViewImpl;
import model.soggetto.Sogg_SoCittadinanzaViewImpl;
import model.soggetto.Sogg_SoCnfTipiDocViewImpl;
import model.soggetto.Sogg_SoDocumentiSoggStoricoViewImpl;
import model.soggetto.Sogg_SoDocumentiSoggViewImpl;
import model.soggetto.Sogg_SoEndoscopiaStoricoViewImpl;
import model.soggetto.Sogg_SoEsclusioneViewImpl;
import model.soggetto.Sogg_SoInterventocolonStoricoViewImpl;
import model.soggetto.Sogg_SoInterventomammoStoricoViewImpl;
import model.soggetto.Sogg_SoInvitoViewImpl;
import model.soggetto.Sogg_SoRefertocolon2livStoricoViewImpl;
import model.soggetto.Sogg_SoRefertomammo1livStoricoViewImpl;
import model.soggetto.Sogg_SoRefertomammo2livStoricoViewImpl;
import model.soggetto.Sogg_SoSoggScrViewImpl;
import model.soggetto.Sogg_SoSoggettoViewImpl;
import model.soggetto.Sogg_SoStoricoConsensoViewImpl;
import model.soggetto.Sogg_StCaRef1ViewImpl;
import model.soggetto.Sogg_StCoRef1ViewImpl;
import model.soggetto.Sogg_StConsensiViewImpl;
import model.soggetto.Sogg_StDocumentiViewImpl;
import model.soggetto.Sogg_StEsclViewImpl;
import model.soggetto.Sogg_StInvitiAllegBckViewImpl;
import model.soggetto.Sogg_StInvitiAllegViewImpl;
import model.soggetto.Sogg_StInvitiBckViewImpl;
import model.soggetto.Sogg_StInvitiViewImpl;
import model.soggetto.Sogg_StPfRef1ViewImpl;
import model.soggetto.Sogg_StRef1ViewImpl;
import model.soggetto.Sogg_StRef2IntViewImpl;
import model.soggetto.Sogg_StRef2ViewImpl;
import model.soggetto.Sogg_StatoViewImpl;
import model.soggetto.Sogg_TipiEsclViewImpl;
import model.soggetto.Sogg_TipoInvitoViewImpl;
import model.soggetto.Sogg_TpscrViewImpl;
import model.soggetto.Sogg_UpdEsclusioneViewImpl;

import model.trial.A_SelTrialBraccioViewImpl;
import model.trial.A_SelTrialStatoViewImpl;

import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.DBTransaction;
import oracle.jbo.server.ViewLinkImpl;
import oracle.jbo.server.ViewObjectImpl;
//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class Sogg_AppModuleImpl extends Parent_AppModuleImpl implements Sogg_AppModule {
  /**
   * 
   *  This is the default constructor (do not remove)
   */
  public Sogg_AppModuleImpl()
  {
  }

  /**
     *
     *  Container's getter for Sogg_RicercaView1
     */
    public Sogg_RicercaViewImpl getSogg_RicercaView1()
  {
    return (Sogg_RicercaViewImpl)findViewObject("Sogg_RicercaView1");
  }

  /**
   * 
   *  Sample main for debugging Business Components code using the tester.
   */
  public static void main(String[] args)
  {
    launchTester("model", "Sogg_AppModuleLocal");
  }
  
  public void dummy()
  {
    
  }

  /**
     *
     *  Container's getter for Sogg_SelCprelView1
     */
    public Sogg_SelCprelViewImpl getSogg_SelCprelView1()
  {
    return (Sogg_SelCprelViewImpl)findViewObject("Sogg_SelCprelView1");
  }

  /**
     *
     *  Container's getter for Sogg_ComuneView1
     */
    public Sogg_ComuneViewImpl getSogg_ComuneView1()
  {
    return (Sogg_ComuneViewImpl)findViewObject("Sogg_ComuneView1");
  }

  /**
     *
     *  Container's getter for Sogg_MedicoView1
     */
    public Sogg_MedicoViewImpl getSogg_MedicoView1()
  {
    return (Sogg_MedicoViewImpl)findViewObject("Sogg_MedicoView1");
  }

  /**
     *
     *  Container's getter for Sogg_DistrettiView1
     */
    public Sogg_DistrettiViewImpl getSogg_DistrettiView1()
  {
    return (Sogg_DistrettiViewImpl)findViewObject("Sogg_DistrettiView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoSoggettoView1
     */
    public Sogg_SoSoggettoViewImpl getSogg_SoSoggettoView1()
  {
    return (Sogg_SoSoggettoViewImpl)findViewObject("Sogg_SoSoggettoView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoInvitoView1
     */
    public Sogg_SoInvitoViewImpl getSogg_SoInvitoView1()
  {
    return (Sogg_SoInvitoViewImpl)findViewObject("Sogg_SoInvitoView1");
  }

  /**
     *
     *  Container's getter for Sogg_CprelInvitoView1
     */
    public Sogg_CprelInvitoViewImpl getSogg_CprelInvitoView1()
  {
    return (Sogg_CprelInvitoViewImpl)findViewObject("Sogg_CprelInvitoView1");
  }

  /**
     *
     *  Container's getter for Sogg_OrariDispView1
     */
    public Sogg_OrariDispViewImpl getSogg_OrariDispView1()
  {
    return (Sogg_OrariDispViewImpl)findViewObject("Sogg_OrariDispView1");
  }

  /**
     *
     *  Container's getter for Sogg_QueryAgendaView1
     */
    public Sogg_QueryAgendaViewImpl getSogg_QueryAgendaView1()
  {
    return (Sogg_QueryAgendaViewImpl)findViewObject("Sogg_QueryAgendaView1");
  }

  /**
     *
     *  Container's getter for Sogg_EsitoView1
     */
    public Sogg_EsitoViewImpl getSogg_EsitoView1()
  {
    return (Sogg_EsitoViewImpl)findViewObject("Sogg_EsitoView1");
  }

  /**
     *
     *  Container's getter for Sogg_TipoInvitoView1
     */
    public Sogg_TipoInvitoViewImpl getSogg_TipoInvitoView1()
  {
    return (Sogg_TipoInvitoViewImpl)findViewObject("Sogg_TipoInvitoView1");
  }

  /**
     *
     *  Container's getter for Sogg_TpscrView1
     */
    public Sogg_TpscrViewImpl getSogg_TpscrView1()
  {
    return (Sogg_TpscrViewImpl)findViewObject("Sogg_TpscrView1");
  }

  /**
     *
     *  Container's getter for A_SoAllegatoView1
     */
    public A_SoAllegatoViewImpl getA_SoAllegatoView1()
  {
    return (A_SoAllegatoViewImpl)findViewObject("A_SoAllegatoView1");
  }

  /**
     *
     *  Container's getter for A_SoAppuntamentoView1
     */
    public A_SoAppuntamentoViewImpl getA_SoAppuntamentoView1()
  {
    return (A_SoAppuntamentoViewImpl)findViewObject("A_SoAppuntamentoView1");
  }

  /**
     *
     *  Container's getter for A_SoAziendaView1
     */
    public A_SoAziendaViewImpl getA_SoAziendaView1()
  {
    return (A_SoAziendaViewImpl)findViewObject("A_SoAziendaView1");
  }

  /**
     *
     *  Container's getter for A_SoCentroPrelRefView1
     */
    public A_SoCentroPrelRefViewImpl getA_SoCentroPrelRefView1()
  {
    return (A_SoCentroPrelRefViewImpl)findViewObject("A_SoCentroPrelRefView1");
  }

  /**
     *
     *  Container's getter for A_SoCnfLetteracentroView1
     */
    public A_SoCnfLetteracentroViewImpl getA_SoCnfLetteracentroView1()
  {
    return (A_SoCnfLetteracentroViewImpl)findViewObject("A_SoCnfLetteracentroView1");
  }

  /**
     *
     *  Container's getter for A_SoCnfTpscrView1
     */
    public A_SoCnfTpscrViewImpl getA_SoCnfTpscrView1()
  {
    return (A_SoCnfTpscrViewImpl)findViewObject("A_SoCnfTpscrView1");
  }

  /**
     *
     *  Container's getter for A_SoInvitoView1
     */
    public A_SoInvitoViewImpl getA_SoInvitoView1()
  {
    return (A_SoInvitoViewImpl)findViewObject("A_SoInvitoView1");
  }

  /**
     *
     *  Container's getter for A_SoMedicoView1
     */
    public A_SoMedicoViewImpl getA_SoMedicoView1()
  {
    return (A_SoMedicoViewImpl)findViewObject("A_SoMedicoView1");
  }

  /**
     *
     *  Container's getter for A_SoRoundorgView1
     */
    public A_SoRoundorgViewImpl getA_SoRoundorgView1()
  {
    return (A_SoRoundorgViewImpl)findViewObject("A_SoRoundorgView1");
  }

  /**
     *
     *  Container's getter for A_SoSoggettoView1
     */
    public A_SoSoggettoViewImpl getA_SoSoggettoView1()
  {
    return (A_SoSoggettoViewImpl)findViewObject("A_SoSoggettoView1");
  }

  /**
     *
     *  Container's getter for A_SoSoggScrView1
     */
    public A_SoSoggScrViewImpl getA_SoSoggScrView1()
  {
    return (A_SoSoggScrViewImpl)findViewObject("A_SoSoggScrView1");
  }

  /**
     *
     *  Container's getter for A_SoTemplateView1
     */
    public A_SoTemplateViewImpl getA_SoTemplateView1()
  {
    return (A_SoTemplateViewImpl)findViewObject("A_SoTemplateView1");
  }

  /**
     *
     *  Container's getter for A_SoCnfPartemplateView1
     */
    public A_SoCnfPartemplateViewImpl getA_SoCnfPartemplateView1()
  {
    return (A_SoCnfPartemplateViewImpl)findViewObject("A_SoCnfPartemplateView1");
  }

  /**
     *
     *  Container's getter for A_SoCnfTpinvitoView1
     */
    public A_SoCnfTpinvitoViewImpl getA_SoCnfTpinvitoView1()
  {
    return (A_SoCnfTpinvitoViewImpl)findViewObject("A_SoCnfTpinvitoView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoEsclusioneView1
     */
    public Sogg_SoEsclusioneViewImpl getSogg_SoEsclusioneView1()
  {
    return (Sogg_SoEsclusioneViewImpl)findViewObject("Sogg_SoEsclusioneView1");
  }

  /**
     *
     *  Container's getter for Sogg_MotEsclView1
     */
    public Sogg_MotEsclViewImpl getSogg_MotEsclView1()
  {
    return (Sogg_MotEsclViewImpl)findViewObject("Sogg_MotEsclView1");
  }

  /**
     *
     *  Container's getter for Sogg_TipiEsclView1
     */
    public Sogg_TipiEsclViewImpl getSogg_TipiEsclView1()
  {
    return (Sogg_TipiEsclViewImpl)findViewObject("Sogg_TipiEsclView1");
  }

  /**
     *
     *  Container's getter for Sogg_UpdEsclusioneView1
     */
    public Sogg_UpdEsclusioneViewImpl getSogg_UpdEsclusioneView1()
  {
    return (Sogg_UpdEsclusioneViewImpl)findViewObject("Sogg_UpdEsclusioneView1");
  }

  /**
     *
     *  Container's getter for Sogg_SessoView1
     */
    public Sogg_SessoViewImpl getSogg_SessoView1()
  {
    return (Sogg_SessoViewImpl)findViewObject("Sogg_SessoView1");
  }

  /**
     *
     *  Container's getter for Sogg_ProvinciaView1
     */
    public Sogg_ProvinciaViewImpl getSogg_ProvinciaView1()
  {
    return (Sogg_ProvinciaViewImpl)findViewObject("Sogg_ProvinciaView1");
  }

  /**
     *
     *  Container's getter for Sogg_DistrettiView2
     */
    public Sogg_DistrettiViewImpl getSogg_DistrettiView2()
  {
    return (Sogg_DistrettiViewImpl)findViewObject("Sogg_DistrettiView2");
  }

  /**
     *
     *  Container's getter for Sogg_StInvitiView1
     */
    public Sogg_StInvitiViewImpl getSogg_StInvitiView1()
  {
    return (Sogg_StInvitiViewImpl)findViewObject("Sogg_StInvitiView1");
  }

  /**
     *
     *  Container's getter for Sogg_StInvitiAllegView1
     */
    public Sogg_StInvitiAllegViewImpl getSogg_StInvitiAllegView1()
  {
    return (Sogg_StInvitiAllegViewImpl)findViewObject("Sogg_StInvitiAllegView1");
  }

  /**
   *
   *  Container's getter for Sogg_StInvitiAllegViewLink1
   */
    public ViewLinkImpl getSogg_StInvitiAllegViewLink1()
  {
        return (ViewLinkImpl)findViewLink("Sogg_StInvitiAllegViewLink1");
    }

  /**
     *
     *  Container's getter for Sogg_StEsclView1
     */
    public Sogg_StEsclViewImpl getSogg_StEsclView1()
  {
    return (Sogg_StEsclViewImpl)findViewObject("Sogg_StEsclView1");
  }

  /**
     *
     *  Container's getter for Sogg_StRef1View1
     */
    public Sogg_StRef1ViewImpl getSogg_StRef1View1()
  {
    return (Sogg_StRef1ViewImpl)findViewObject("Sogg_StRef1View1");
  }

  /**
     *
     *  Container's getter for Sogg_StRef2View1
     */
    public Sogg_StRef2ViewImpl getSogg_StRef2View1()
  {
    return (Sogg_StRef2ViewImpl)findViewObject("Sogg_StRef2View1");
  }

  /**
     *
     *  Container's getter for Sogg_MotEsclView2
     */
    public Sogg_MotEsclViewImpl getSogg_MotEsclView2()
  {
    return (Sogg_MotEsclViewImpl)findViewObject("Sogg_MotEsclView2");
  }



  /**
     *
     *  Container's getter for Sogg_CnfEsito1
     */
    public Sogg_CnfEsitoImpl getSogg_CnfEsito1()
  {
    return (Sogg_CnfEsitoImpl)findViewObject("Sogg_CnfEsito1");
  }

  /**
     *
     *  Container's getter for Sogg_StInvitiAllegView2
     */
    public Sogg_StInvitiAllegViewImpl getSogg_StInvitiAllegView2()
  {
    return (Sogg_StInvitiAllegViewImpl)findViewObject("Sogg_StInvitiAllegView2");
  }

  /**
   *
   *  Container's getter for Sogg_SoInvitoAllegatoLink1
   */
    public ViewLinkImpl getSogg_SoInvitoAllegatoLink1()
  {
        return (ViewLinkImpl)findViewLink("Sogg_SoInvitoAllegatoLink1");
    }

  /**
     *
     *  Container's getter for Sogg_StatoView1
     */
    public Sogg_StatoViewImpl getSogg_StatoView1()
  {
    return (Sogg_StatoViewImpl)findViewObject("Sogg_StatoView1");
  }

  /**
     *
     *  Container's getter for Sogg_StRef2IntView1
     */
    public Sogg_StRef2IntViewImpl getSogg_StRef2IntView1()
  {
    return (Sogg_StRef2IntViewImpl)findViewObject("Sogg_StRef2IntView1");
  }

  /**
   *
   *  Container's getter for Sogg_StRef2IntViewLink1
   */
    public ViewLinkImpl getSogg_StRef2IntViewLink1()
  {
        return (ViewLinkImpl)findViewLink("Sogg_StRef2IntViewLink1");
    }

  protected void afterConnect()
  {
    // TODO:  Override this oracle.jbo.server.ApplicationModuleImpl method
    
    super.afterConnect();
    //getDBTransaction().executeCommand("ALTER SESSION SET SQL_TRACE TRUE");
        
  }

  /**
     *
     *  Container's getter for Cnf_SoCnfComuneView1
     */
    public Cnf_SoCnfComuneViewImpl getCnf_SoCnfComuneView1()
  {
    return (Cnf_SoCnfComuneViewImpl)findViewObject("Cnf_SoCnfComuneView1");
  }

  /**
     *
     *  Container's getter for Sogg_NuovoInvitoAttivoView1
     */
    public Sogg_NuovoInvitoAttivoViewImpl getSogg_NuovoInvitoAttivoView1()
  {
    return (Sogg_NuovoInvitoAttivoViewImpl)findViewObject("Sogg_NuovoInvitoAttivoView1");
  }

  /**
     *
     *  Container's getter for Sogg_StCoRef1View1
     */
    public Sogg_StCoRef1ViewImpl getSogg_StCoRef1View1()
  {
    return (Sogg_StCoRef1ViewImpl)findViewObject("Sogg_StCoRef1View1");
  }

  /**
     *
     *  Container's getter for Sogg_SoRefertocolon2livStoricoView1
     */
    public Sogg_SoRefertocolon2livStoricoViewImpl getSogg_SoRefertocolon2livStoricoView1()
  {
    return (Sogg_SoRefertocolon2livStoricoViewImpl)findViewObject("Sogg_SoRefertocolon2livStoricoView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoEndoscopiaStoricoView1
     */
    public Sogg_SoEndoscopiaStoricoViewImpl getSogg_SoEndoscopiaStoricoView1()
  {
    return (Sogg_SoEndoscopiaStoricoViewImpl)findViewObject("Sogg_SoEndoscopiaStoricoView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoInterventocolonStoricoView1
     */
    public Sogg_SoInterventocolonStoricoViewImpl getSogg_SoInterventocolonStoricoView1()
  {
    return (Sogg_SoInterventocolonStoricoViewImpl)findViewObject("Sogg_SoInterventocolonStoricoView1");
  }

  /**
   *
   *  Container's getter for Sogg_FKRefertoEndoStoricoLink1
   */
    public ViewLinkImpl getSogg_FKRefertoEndoStoricoLink1()
  {
        return (ViewLinkImpl)findViewLink("Sogg_FKRefertoEndoStoricoLink1");
    }

  /**
   *
   *  Container's getter for Sogg_FkRefertoIntStoricoLink1
   */
    public ViewLinkImpl getSogg_FkRefertoIntStoricoLink1()
  {
        return (ViewLinkImpl)findViewLink("Sogg_FkRefertoIntStoricoLink1");
    }

  /**
   * 
   *  Container's getter for A_SoAccColon1livView1
   */
  public A_SoAccColon1livViewImpl getA_SoAccColon1livView1()
  {
    return (A_SoAccColon1livViewImpl)findViewObject("A_SoAccColon1livView1");
  }

  /**
     *
     *  Container's getter for A_SoCnfComuniZoneParamView1
     */
    public A_SoCnfComuniZoneParamViewImpl getA_SoCnfComuniZoneParamView1()
  {
    return (A_SoCnfComuniZoneParamViewImpl)findViewObject("A_SoCnfComuniZoneParamView1");
  }

  /**
   * 
   *  Container's getter for A_SoAccMammo1livView1
   */
  public A_SoAccMammo1livViewImpl getA_SoAccMammo1livView1()
  {
    return (A_SoAccMammo1livViewImpl)findViewObject("A_SoAccMammo1livView1");
  }

  /**
   * 
   *  Container's getter for A_SoAccMammo2livView1
   */
  public A_SoAccMammo2livViewImpl getA_SoAccMammo2livView1()
  {
    return (A_SoAccMammo2livViewImpl)findViewObject("A_SoAccMammo2livView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoSoggScrView1
     */
    public Sogg_SoSoggScrViewImpl getSogg_SoSoggScrView1()
  {
    return (Sogg_SoSoggScrViewImpl)findViewObject("Sogg_SoSoggScrView1");
  }

  /**
     *
     *  Container's getter for A_SoCnfParametriSistemaView1
     */
    public A_SoCnfParametriSistemaViewImpl getA_SoCnfParametriSistemaView1()
  {
    return (A_SoCnfParametriSistemaViewImpl)findViewObject("A_SoCnfParametriSistemaView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoRefertomammo1livStoricoView1
     */
    public Sogg_SoRefertomammo1livStoricoViewImpl getSogg_SoRefertomammo1livStoricoView1()
  {
    return (Sogg_SoRefertomammo1livStoricoViewImpl)findViewObject("Sogg_SoRefertomammo1livStoricoView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoRefertomammo2livStoricoView1
     */
    public Sogg_SoRefertomammo2livStoricoViewImpl getSogg_SoRefertomammo2livStoricoView1()
  {
    return (Sogg_SoRefertomammo2livStoricoViewImpl)findViewObject("Sogg_SoRefertomammo2livStoricoView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoInterventomammoStoricoView1
     */
    public Sogg_SoInterventomammoStoricoViewImpl getSogg_SoInterventomammoStoricoView1()
  {
    return (Sogg_SoInterventomammoStoricoViewImpl)findViewObject("Sogg_SoInterventomammoStoricoView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoInterventomammoStoricoView2
     */
    public Sogg_SoInterventomammoStoricoViewImpl getSogg_SoInterventomammoStoricoView2()
  {
    return (Sogg_SoInterventomammoStoricoViewImpl)findViewObject("Sogg_SoInterventomammoStoricoView2");
  }

  /**
   *
   *  Container's getter for Sogg_StRefMa2IntViewLink1
   */
    public ViewLinkImpl getSogg_StRefMa2IntViewLink1()
  {
        return (ViewLinkImpl)findViewLink("Sogg_StRefMa2IntViewLink1");
    }

  /**
   * 
   *  Container's getter for A_SoAccCito1livView1_1
   */
  public A_SoAccCito1livView1Impl getA_SoAccCito1livView1_1()
  {
    return (A_SoAccCito1livView1Impl)findViewObject("A_SoAccCito1livView1_1");
  }

  /**
     *
     *  Container's getter for Sogg_SoCnfCentro2livView1
     */
    public Cnf_SoCnfCentro2livViewImpl getSogg_SoCnfCentro2livView1()
  {
    return (Cnf_SoCnfCentro2livViewImpl)findViewObject("Sogg_SoCnfCentro2livView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoCnfCentro1livView1
     */
    public Cnf_SoCnfCentro1livViewImpl getSogg_SoCnfCentro1livView1()
  {
    return (Cnf_SoCnfCentro1livViewImpl)findViewObject("Sogg_SoCnfCentro1livView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoSoggScrView2
     */
    public Sogg_SoSoggScrViewImpl getSogg_SoSoggScrView2()
  {
    return (Sogg_SoSoggScrViewImpl)findViewObject("Sogg_SoSoggScrView2");
  }

  /**
     *
     *  Container's getter for Sogg_CRichInvito1
     */
    public Sogg_CRichInvitoImpl getSogg_CRichInvito1()
  {
    return (Sogg_CRichInvitoImpl)findViewObject("Sogg_CRichInvito1");
  }

  /**
     *
     *  Container's getter for Cnf_EsclProposteViewCI
     */
    public Cnf_EsclProposteViewImpl getCnf_EsclProposteViewCI()
  {
    return (Cnf_EsclProposteViewImpl)findViewObject("Cnf_EsclProposteViewCI");
  }

  /**
     *
     *  Container's getter for Cnf_EsclProposteViewMA
     */
    public Cnf_EsclProposteViewImpl getCnf_EsclProposteViewMA()
  {
    return (Cnf_EsclProposteViewImpl)findViewObject("Cnf_EsclProposteViewMA");
  }

  /**
     *
     *  Container's getter for Cnf_EsclProposteViewCO
     */
    public Cnf_EsclProposteViewImpl getCnf_EsclProposteViewCO()
  {
    return (Cnf_EsclProposteViewImpl)findViewObject("Cnf_EsclProposteViewCO");
  }

  /**
     *
     *  Container's getter for Sogg_EsclusioniCollegateView1
     */
    public Sogg_EsclusioniCollegateViewImpl getSogg_EsclusioniCollegateView1()
  {
    return (Sogg_EsclusioniCollegateViewImpl)findViewObject("Sogg_EsclusioniCollegateView1");
  }

  /**
     *
     *  Container's getter for Sogg_SinondView1
     */
    public Sogg_SinondViewImpl getSogg_SinondView1()
  {
    return (Sogg_SinondViewImpl)findViewObject("Sogg_SinondView1");
  }

  /**
     *
     *  Container's getter for Sogg_SoStoricoConsensoView1
     */
    public Sogg_SoStoricoConsensoViewImpl getSogg_SoStoricoConsensoView1()
  {
    return (Sogg_SoStoricoConsensoViewImpl)findViewObject("Sogg_SoStoricoConsensoView1");
  }

  /**
     *
     *  Container's getter for Cnf_EsclProposteView1
     */
    public Cnf_EsclProposteViewImpl getCnf_EsclProposteView1()
  {
    return (Cnf_EsclProposteViewImpl)findViewObject("Cnf_EsclProposteView1");
  }


    /**
     * Container's getter for A_SelTrialBraccioView1.
     * @return A_SelTrialBraccioView1
     */
    public A_SelTrialBraccioViewImpl getA_SelTrialBraccioView1() {
        return (A_SelTrialBraccioViewImpl) findViewObject("A_SelTrialBraccioView1");
    }

    /**
     * Container's getter for A_SelTrialStatoView1.
     * @return A_SelTrialStatoView1
     */
    public A_SelTrialStatoViewImpl getA_SelTrialStatoView1() {
        return (A_SelTrialStatoViewImpl) findViewObject("A_SelTrialStatoView1");
    }

    /**
     * Container's getter for Sogg_SoCnfTipiDocView1.
     * @return Sogg_SoCnfTipiDocView1
     */
    public Sogg_SoCnfTipiDocViewImpl getSogg_SoCnfTipiDocView1() {
        return (Sogg_SoCnfTipiDocViewImpl) findViewObject("Sogg_SoCnfTipiDocView1");
    }

    public String callFunDocCongruenza(String tipoDoc, String docIdent)
    {
      CallableStatement st = null;
     
      try 
      {  
        String stmt = "SELECT FUN_DOC_CONGRUENZA('"+tipoDoc+"','"+docIdent+"',null,null) FROM dual";
    
        DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmt,1);
    
        ResultSet res = st.executeQuery();   
        res.next();
        
        // se si vuole mandare un messaggio
        String mess=res.getString(1);

        return mess;
          
        } catch(Exception e) {
            e.printStackTrace();
            return e.getMessage();
          }
      
      finally 
      {
        try 
        {
          if (st != null) st.close();
        }
        catch (SQLException s) { /* ignore */}
        
      }
    }
    
    public String callMergeAnagrafico(String ulss, String codtsVal, String codtsDepr, String utente)

    {
      //lettura parametro di abilitazione per il journaling
      String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
      if (flagAbilJournal != null && flagAbilJournal.equals("1"))
      {
        this.preapareJournaling("MERGE_ANAGRAFICO",ulss,null);
      }
    
      CallableStatement st = null;
     
      try 
      {  //PROCEDURE merge_soggetti (valido_codts IN varchar2, deprecato_codts IN varchar2, P_ULSS in char, p_utente in varchar2, tipo_operazione IN varchar2, p_id_merge IN OUT number, message IN OUT varchar2);
        String stmt = "BEGIN  MERGE_ANAGRAFICO.merge_soggetti(?,?,?,?,?,?,?,?); END;";
    
        DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmt,1);
        if(ulss==null)
          throw new Exception("Nessuna azienda sanitaria indicata");
        st.setString(1,codtsVal);
        st.setString(2,codtsDepr);
        st.setString(3,ulss);             // passaggio del parametro: controllare prima che sia diverso da null
        st.setString(4,utente);
        // I00095789
        st.setString(5,"A");  // M = manuale
        // st.setString(5,"M");  // M = manuale
        // I00095789
        st.registerOutParameter(6, java.sql.Types.NUMERIC);
        st.registerOutParameter(7, java.sql.Types.VARCHAR); 
        st.registerOutParameter(8, java.sql.Types.VARCHAR); 
    
        st.executeUpdate();   

        // se si vuole mandare un messaggio
        String mess=st.getString(7);
        int num = (st.getBigDecimal(6)).intValue();            
        if(num > 0)
          return null;
        else 
          return mess;
          
        } catch(Exception e) {
            e.printStackTrace();
            return e.getMessage();
          }
      
      finally 
      {
        try 
        {
          if (st != null) st.close();
        }
        catch (SQLException s) { /* ignore */}
        
      }
    }
    
    public String callUnmergeAnagrafico(String ulss, String codtsVal, String codtsDepr, String utente, Integer idMerge)

    {
      //lettura parametro di abilitazione per il journaling
      String flagAbilJournal = ConfigurationReader.readProperty("flagAbilJournal");
      if (flagAbilJournal != null && flagAbilJournal.equals("1"))
      {
        this.preapareJournaling("MERGE_ANAGRAFICO",ulss,null);
      }
    
      CallableStatement st = null;
     
      try 
      {  //PROCEDURE unmerge_soggetti (valido_codts IN varchar2, ripristinato_codts IN varchar2, P_ULSS in char, p_utente in varchar2, tipo_operazione IN varchar2, p_id_merge IN OUT number, message IN OUT varchar2);
        String stmt = "BEGIN  MERGE_ANAGRAFICO.unmerge_soggetti(?,?,?,?,?,?,?,?); END;";
    
        DBTransaction tr = this.getDBTransaction();
        st = tr.createCallableStatement(stmt,1);
        if(ulss==null)
          throw new Exception("Nessuna azienda sanitaria indicata");
        st.setString(1,codtsVal);
        st.setString(2,codtsDepr);
        st.setString(3,ulss);             // passaggio del parametro: controllare prima che sia diverso da null
        st.setString(4,utente);
        st.setString(5,"M");  // M = manuale
        st.setInt(6,idMerge.intValue());
        st.registerOutParameter(7, java.sql.Types.VARCHAR);
        st.registerOutParameter(8, java.sql.Types.VARCHAR); 
    
        st.executeUpdate();   

        // se si vuole mandare un messaggio
        String mess=st.getString(7);

        return mess;
          
        } catch(Exception e) {
            e.printStackTrace();
            return e.getMessage();
          }
      
      finally 
      {
        try 
        {
          if (st != null) st.close();
        }
        catch (SQLException s) { /* ignore */}
        
      }
    }    

    public void impostaViewPerSoggetto(String codts, String ulss, String tpscr) {
    
            // SoSoggettoView1
            Sogg_SoSoggettoViewImpl soggettoView = getSogg_SoSoggettoView1();
            String whcls = "Sogg_SoSoggetto.CODTS = '" + codts +
                    "' AND Sogg_SoSoggetto.ULSS = '" + ulss + "'";
            soggettoView.setWhereClause(whcls);
            soggettoView.executeQuery();
            soggettoView.first();

            // SoSoggScrView1
            Sogg_SoSoggScrViewImpl soggscrView = getSogg_SoSoggScrView1();
            String whcl = "Sogg_SoSoggScr.CODTS = '" + codts +
                    "' AND Sogg_SoSoggScr.ULSS = '" + ulss +
                    "' AND Sogg_SoSoggScr.TPSCR = '" + tpscr + "'";
            soggscrView.setWhereClause(whcl);
            soggscrView.executeQuery();
            soggscrView.first();
    }

    /**
     * Container's getter for Sogg_SoDocumentiSoggStoricoView1.
     * @return Sogg_SoDocumentiSoggStoricoView1
     */
    public Sogg_SoDocumentiSoggStoricoViewImpl getSogg_SoDocumentiSoggStoricoView1() {
        return (Sogg_SoDocumentiSoggStoricoViewImpl) findViewObject("Sogg_SoDocumentiSoggStoricoView1");
    }

    /**
     * Container's getter for Sogg_StPfRef1View1.
     * @return Sogg_StPfRef1View1
     */
    public Sogg_StPfRef1ViewImpl getSogg_StPfRef1View1() {
        return (Sogg_StPfRef1ViewImpl) findViewObject("Sogg_StPfRef1View1");
    }

    /**
     * Container's getter for Cnf_SoCnfAnagRegView1.
     * @return Cnf_SoCnfAnagRegView1
     */
    public Cnf_SoCnfAnagRegViewImpl getCnf_SoCnfAnagRegView1() {
        return (Cnf_SoCnfAnagRegViewImpl) findViewObject("Cnf_SoCnfAnagRegView1");
    }

    /**
     * Container's getter for Sogg_StCaRef1View1.
     * @return Sogg_StCaRef1View1
     */
    public Sogg_StCaRef1ViewImpl getSogg_StCaRef1View1() {
        return (Sogg_StCaRef1ViewImpl) findViewObject("Sogg_StCaRef1View1");
    }

    /**
     * Container's getter for Sogg_StConsensiView1.
     * @return Sogg_StConsensiView1
     */
    public Sogg_StConsensiViewImpl getSogg_StConsensiView1() {
        return (Sogg_StConsensiViewImpl) findViewObject("Sogg_StConsensiView1");
    }

    /**
     * Container's getter for Sogg_StDocumentiView1.
     * @return Sogg_StDocumentiView1
     */
    public Sogg_StDocumentiViewImpl getSogg_StDocumentiView1() {
        return (Sogg_StDocumentiViewImpl) findViewObject("Sogg_StDocumentiView1");
    }

    /**
     * Container's getter for Sogg_ProvinciaView2.
     * @return Sogg_ProvinciaView2
     */
    public Sogg_ProvinciaViewImpl getSogg_ProvinciaView2() {
        return (Sogg_ProvinciaViewImpl) findViewObject("Sogg_ProvinciaView2");
    }

    /**
     * Container's getter for Sogg_SoCittadinanzaView1.
     * @return Sogg_SoCittadinanzaView1
     */
    public Sogg_SoCittadinanzaViewImpl getSogg_SoCittadinanzaView1() {
        return (Sogg_SoCittadinanzaViewImpl) findViewObject("Sogg_SoCittadinanzaView1");
    }

    /**
     * Container's getter for Sogg_SoDocumentiSoggView1.
     * @return Sogg_SoDocumentiSoggView1
     */
    public Sogg_SoDocumentiSoggViewImpl getSogg_SoDocumentiSoggView1() {
        return (Sogg_SoDocumentiSoggViewImpl) findViewObject("Sogg_SoDocumentiSoggView1");
    }

    /**
     * Container's getter for Sogg_SoggDocLink1.
     * @return Sogg_SoggDocLink1
     */
    public ViewLinkImpl getSogg_SoggDocLink1() {
        return (ViewLinkImpl) findViewLink("Sogg_SoggDocLink1");
    }

    /**
     * Container's getter for Sogg_StInvitiAllegView3.
     * @return Sogg_StInvitiAllegView3
     */
    public Sogg_StInvitiAllegViewImpl getSogg_StInvitiAllegView3() {
        return (Sogg_StInvitiAllegViewImpl) findViewObject("Sogg_StInvitiAllegView3");
    }

    /**
     * Container's getter for Sogg_InvConfermatoLOV1.
     * @return Sogg_InvConfermatoLOV1
     */
    public ViewObjectImpl getSogg_InvConfermatoLOV1() {
        return (ViewObjectImpl) findViewObject("Sogg_InvConfermatoLOV1");
    }

    /**
     * Container's getter for Mrg_SoSoggettoView1.
     * @return Mrg_SoSoggettoView1
     */
    public ViewObjectImpl getMrg_SoggValidoView() {
        return (ViewObjectImpl) findViewObject("Mrg_SoggValidoView");
    }

    /**
     * Container's getter for Mrg_SoSoggettoView1.
     * @return Mrg_SoSoggettoView1
     */
    public ViewObjectImpl getMrg_SoggDeprecatoView() {
        return (ViewObjectImpl) findViewObject("Mrg_SoggDeprecatoView");
    }

    /**
     * Container's getter for Mrg_SoMergeView1.
     * @return Mrg_SoMergeView1
     */
    public ViewObjectImpl getMrg_SoMergeView1() {
        return (ViewObjectImpl) findViewObject("Mrg_SoMergeView1");
    }

    /**
     * Container's getter for Mrg_SoMergeOperazioniView1.
     * @return Mrg_SoMergeOperazioniView1
     */
    public ViewObjectImpl getMrg_SoMergeOperazioniView1() {
        return (ViewObjectImpl) findViewObject("Mrg_SoMergeOperazioniView1");
    }

    /**
     * Container's getter for Mrg_MrgOperazVL1.
     * @return Mrg_MrgOperazVL1
     */
    public ViewLinkImpl getMrg_MrgOperazVL1() {
        return (ViewLinkImpl) findViewLink("Mrg_MrgOperazVL1");
    }

    /**
     * Container's getter for Mrg_SoMergeProposteView1.
     * @return Mrg_SoMergeProposteView1
     */
    public ViewObjectImpl getMrg_SoMergeProposteView1() {
        return (ViewObjectImpl) findViewObject("Mrg_SoMergeProposteView1");
    }

    /**
     * Container's getter for A_SoAccCito2livView1.
     * @return A_SoAccCito2livView1
     */
    public A_SoAccCito2livViewImpl getA_SoAccCito2livView1() {
        return (A_SoAccCito2livViewImpl) findViewObject("A_SoAccCito2livView1");
    }

    /**
     * Container's getter for Sogg_SoChiaviView1.
     * @return Sogg_SoChiaviView1
     */
    public Sogg_SoChiaviViewImpl getSogg_SoChiaviView1() {
        return (Sogg_SoChiaviViewImpl) findViewObject("Sogg_SoChiaviView1");
    }
    /**
     * Container's getter for Sogg_RicercaView2.
     * @return Sogg_RicercaView2
     */
    public Sogg_RicercaViewImpl getSogg_RicercaViewAURCheck() {
        return (Sogg_RicercaViewImpl) findViewObject("Sogg_RicercaViewAURCheck");
    }
    /**
     * Container's getter for Sogg_SoCnfClassePopView1.
     * @return Sogg_SoCnfClassePopView1
     */
    public ViewObjectImpl getSogg_SoCnfClassePopView1() {
        return (ViewObjectImpl) findViewObject("Sogg_SoCnfClassePopView1");
    }

    /**
     * Container's getter for Sogg_ComuneAURView1.
     * @return Sogg_ComuneAURView1
     */
    public Sogg_ComuneAURViewImpl getSogg_ComuneAURView1() {
        return (Sogg_ComuneAURViewImpl) findViewObject("Sogg_ComuneAURView1");
}

    /**
     * Container's getter for Sogg_SoStatoAURView1.
     * @return Sogg_SoStatoAURView1
     */
    public ViewObjectImpl getSogg_SoStatoAURView1() {
        return (ViewObjectImpl) findViewObject("Sogg_SoStatoAURView1");
    }
    
    /**
     * Container's getter for Sogg_SoLogImpexpHl7View1.
     * @return Sogg_SoLogImpexpHl7View1
     */
    public ViewObjectImpl getSogg_SoLogImpexpHl7View1() {
        return (ViewObjectImpl) findViewObject("Sogg_SoLogImpexpHl7View1");
    }
    /**
     * Container's getter for Sogg_CategoriaView1.
     * @return Sogg_CategoriaView1
     */
    public ViewObjectImpl getSogg_CategoriaView1() {
        return (ViewObjectImpl) findViewObject("Sogg_CategoriaView1");
    }

    /**
     * Container's getter for Sogg_SoMedicoAURView1.
     * @return Sogg_SoMedicoAURView1
     */
    public ViewObjectImpl getSogg_SoMedicoAURView1() {
        return (ViewObjectImpl) findViewObject("Sogg_SoMedicoAURView1");
    }

    /**
     * Container's getter for SoInvMotivoView1.
     * @return SoInvMotivoView1
     */
    public ViewObjectImpl getSoInvMotivoView1() {
        return (ViewObjectImpl) findViewObject("SoInvMotivoView1");
    }

    /**
     * Container's getter for FK_SO_INV_MOTIVO_INVITOViewLink1.
     * @return FK_SO_INV_MOTIVO_INVITOViewLink1
     */
    public ViewLinkImpl getFK_SO_INV_MOTIVO_INVITOViewLink1() {
        return (ViewLinkImpl) findViewLink("FK_SO_INV_MOTIVO_INVITOViewLink1");
    }

    /**
     * Container's getter for SoCnfRef1LivMTEXECView1.
     * @return SoCnfRef1LivMTEXECView1
     */
    public ViewObjectImpl getSoCnfRef1LivMTEXECView1() {
        return (ViewObjectImpl) findViewObject("SoCnfRef1LivMTEXECView1");
    }


    /**
     * Container's getter for Sogg_StInvitiBckView1.
     * @return Sogg_StInvitiBckView1
     */
    public Sogg_StInvitiBckViewImpl getSogg_StInvitiBckView1() {
        return (Sogg_StInvitiBckViewImpl) findViewObject("Sogg_StInvitiBckView1");
    }

    /**
     * Container's getter for Sogg_StInvitiAllegBckView1.
     * @return Sogg_StInvitiAllegBckView1
     */
    public Sogg_StInvitiAllegBckViewImpl getSogg_StInvitiAllegBckView1() {
        return (Sogg_StInvitiAllegBckViewImpl) findViewObject("Sogg_StInvitiAllegBckView1");
    }

    /**
     * Container's getter for Sogg_StInvitiAllegBckViewLink1.
     * @return Sogg_StInvitiAllegBckViewLink1
     */
    public ViewLinkImpl getSogg_StInvitiAllegBckViewLink1() {
        return (ViewLinkImpl) findViewLink("Sogg_StInvitiAllegBckViewLink1");
    }

    /**
     * Container's getter for Sogg_StInvitiAllegBckView2.
     * @return Sogg_StInvitiAllegBckView2
     */
    public Sogg_StInvitiAllegBckViewImpl getSogg_StInvitiAllegBckView2() {
        return (Sogg_StInvitiAllegBckViewImpl) findViewObject("Sogg_StInvitiAllegBckView2");
    }

    /**
     * Container's getter for Sogg_SoInvitoBckView1.
     * @return Sogg_SoInvitoBckView1
     */
    public ViewObjectImpl getSogg_SoInvitoBckView1() {
        return (ViewObjectImpl) findViewObject("Sogg_SoInvitoBckView1");
    }

    /**
     * Container's getter for Cnf_SoCnfInvitiFastView1.
     * @return Cnf_SoCnfInvitiFastView1
     */
    public Cnf_SoCnfInvitiFastViewImpl getCnf_SoCnfInvitiFastView1() {
        return (Cnf_SoCnfInvitiFastViewImpl) findViewObject("Cnf_SoCnfInvitiFastView1");
    }
}
