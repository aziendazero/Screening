package view.referto;

import insiel.dataHandling.ComparisonUtils;
import insiel.dataHandling.DateUtils;

import java.sql.SQLException;

import java.util.Date;
import java.util.Map;

import oracle.jbo.domain.Number;

import model.common.Cnf_AppModule;
import model.common.RefCo_AppModule;
import model.common.RefMa_AppModule;
import model.common.RefPf_AppModule;
import model.common.Ref_AppModule;


import model.commons.ConfigurationConstants;


import model.commons.ViewHelper;

import model.datacontrol.Ref_2livBean;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

import view.providers.Ref_supportForProviders;

public class GestoreRefertiUI {

    /************************************************************/
    /**             1 LIVELLO                                  **/
    /************************************************************/
    
    /*******  UPDATE DELL'INTERFACCIA A PARTIRE DAL DATABASE  *********/
       /**
       * Carica i dati dal database per un referto di primo livello
       * @param ctx
       */
      public static void loadFromDB1liv()
      {
        getDBPositiviSelection();
        getDBInalimSelection();
        getDBHPVTP1Selection();
      }
    
       private static void getDBPositiviSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef1livPOSITIView1");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref1livcView4");
       Ref_supportForProviders.getDBRefSelection(positi,"Idcnfref1l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_POSITI);
      }
    
      /**
       * Impostazione del vettore dei tipi di virus HPV solo da DB
       * @param ctx
       */
       private static void getDBHPVTP1Selection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef1livHPVTP1View1");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref1livcView4");
       Ref_supportForProviders.getDBRefSelection(positi,"Idcnfref1l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_TIPI_VIRUS);
      }
    
      /**
       * Legge dal database la selezione del motivo di inadeguatezza/limitazione
       * ed aggiorna la bean property relativa
       * @param ctx
       */
      private static void getDBInalimSelection()
      {
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject cnf=am.findViewObject("Ref_SoCodref1livcView4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        Integer n=(Integer)Ref_supportForProviders.getDBSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_INALIM);
        if(!ComparisonUtils.compare(bean.getLimitatezza_1l(),n)){
          bean.setLimitatezza_1l(n);
          bean.setDirty(false);
        }
      }
    
    /*******  UPDATE DELL'INTERFACCIA A PARTIRE DALL'INTERFACCIA  *********/
     /**
       * Metodo che reimposta l'interfaccia in base alla selezine precednete
       * @param ctx
       */
      public static void loadFromUI1liv()
      {
        //positivi
        //getUIPositiviSelection();
        //getUITipiVirusSelection();
        //controllo che la limitatezza non vada resettata
       resetInalimIfNecessary();
       //controllo che il gidizio diagnostico non vada resettato
       resetGiudiaIfNecessary();
      }
    
     // /**
     //  * Impostazione del vettore dei positivi  da interfaccia,
     //  * con aggiornamento dei dati sul db
     //  * @return
     //  * @param ctx
     //  */
     // private static Vector getUIPositiviSelection()
     // {
    
     //   Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
     //   ViewObject positi=am.findViewObject("Ref_SoCnfRef1livPOSITIView1");
     //  ViewObject cnf=am.findViewObject("Ref_SoCodref1livcView4");
     //    return Ref_supportForProviders.getUIRefSelection(positi,"Idcnfref1l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_POSITI);
     // }
    
    //  /**
    //   * Impostazione del vettore dei tipi di virus hpv  da interfaccia,
    //   * con aggiornamento dei dati sul db
    //   * @return
    //   * @param ctx
    //   */
    //  private static Vector getUITipiVirusSelection()
    //  {
    //
    //    Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
    //    ViewObject positi=am.findViewObject("Ref_SoCnfRef1livHPVTP1View1");
    //   ViewObject cnf=am.findViewObject("Ref_SoCodref1livcView4");
    //    ServletRequestDataSet ds = new ServletRequestDataSet(ctx.getHttpServletRequest(),"cnf_tipivirus");
    //     int[] indices = SelectionUtils.getSelectedIndices(ds);
    //
    //     return Ref_supportForProviders.getUIRefSelection(positi,"Idcnfref1l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_TIPI_VIRUS,indices);
    //  }
    //

      private static void resetInalimIfNecessary()
      {
         Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
         ViewObject cnf_adepre=am.findViewObject("Ref_SoCodref1livcADEPREView1");
         Row[] result1=cnf_adepre.getFilteredRows("Gruppo",ConfigurationConstants.NOME_GRUPPO_ADEPRE);
         Row adepre;
         if(result1.length==0)
           adepre=null;
          else
           adepre=result1[0];
    
         Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
         if(adepre==null)
         {
    
           if(bean.getLimitatezza_1l()!=null)
              bean.setLimitatezza_1l(null);
           return;
         }
    
    
         //se il dato non e' inadeguato o limitato, allora non ci dve essere motivo di limitazione
         if(!ConfigurationConstants.CODICE_ADEPRE_INADEGUATO.equals(adepre.getAttribute("Idcnfref"))
         && !ConfigurationConstants.CODICE_ADEPRE_LIMITATO.equals(adepre.getAttribute("Idcnfref")))
          {
            if(bean.getLimitatezza_1l()!=null)
              bean.setLimitatezza_1l(null);
          }
    
      }
    
      private static void resetGiudiaIfNecessary()
      {
         Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
         //vo dipendenti dl referto selezionato (una riga per uno)
         ViewObject cnf_adepre=am.findViewObject("Ref_SoCodref1livcADEPREView1");
         ViewObject cnf_giudia=am.findViewObject("Ref_SoCodref1livcGIUDIAView1");
         Row[] result1=cnf_adepre.getFilteredRows("Gruppo",ConfigurationConstants.NOME_GRUPPO_ADEPRE);
         Row[] result=cnf_giudia.getFilteredRows("Gruppo",ConfigurationConstants.NOME_GRUPPO_GIUDIA);
         Row adepre,giudia;
         if(result1.length==0)
           adepre=null;
          else
           adepre=result1[0];
        if(result.length==0)
           giudia=null;
          else
         giudia=result[0];
    
    
         if(adepre==null)
         {
           if(giudia!=null && !ConfigurationConstants.CODICE_GIUDIA_DEFAULT.equals(giudia.getAttribute("Idcnfref")))
             giudia.setAttribute("Idcnfref",ConfigurationConstants.CODICE_GIUDIA_DEFAULT);
    
           return;
         }
    
    
         //se il dato non e' adeguato o limitato, allora IL GIUDIZIO DIAGNOSTICO NON DEVE ESSERE DISPONIBILE
         if(!ConfigurationConstants.CODICE_ADEPRE_ADEGUATO.equals(adepre.getAttribute("Idcnfref"))
         && !ConfigurationConstants.CODICE_ADEPRE_LIMITATO.equals(adepre.getAttribute("Idcnfref")))
          {
            if(!ConfigurationConstants.CODICE_GIUDIA_DEFAULT.equals(giudia.getAttribute("Idcnfref")))
              giudia.setAttribute("Idcnfref",ConfigurationConstants.CODICE_GIUDIA_DEFAULT);
          }
      }
    
    /*******  UPDATE DEL DATABASE  *********/
      /**
       * Metodo che riporta sul database le scelte dell'utente
       * @param ctx
       */
      public static void updateDB1liv()
      {
         updatePositiviSelection();
       //  updateModreaSelection(ctx);
         updateInalimSelection();
         updateTipiVirusSelection();
      }
    
      private static void updatePositiviSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef1livPOSITIView1");
        ViewObject ref=am.findViewObject("Ref_SoRefertocito1livView3");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref1livcView4");
    
        //faccio l'update su db
        Ref_supportForProviders.updateRefSelection(positi,"Idcnfref1l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_POSITI,am,ref,1);
      }
    
      private static void updateTipiVirusSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef1livHPVTP1View1");
        ViewObject ref=am.findViewObject("Ref_SoRefertocito1livView3");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref1livcView4");
    
        //faccio l'update su db
        Ref_supportForProviders.updateRefSelection(positi,"Idcnfref1l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_TIPI_VIRUS,am,ref,1);
      }
    
      private static void updateInalimSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref=am.findViewObject("Ref_SoRefertocito1livView3");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        //configurazione dipendente dai referti su cui sto lavorando
        ViewObject cnf=am.findViewObject("Ref_SoCodref1livcView4");
        Ref_supportForProviders.updateSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_INALIM,bean.getLimitatezza_1l(),am,ref,1);
      }
    
    ///************************************************************/
    ///**             2 LIVELLO - CITOLOGIA                    **/
    ///************************************************************/
    //
    //
    //
    //
    /************************************************************/
    /**             2 LIVELLO - COLPOSCOPIA                    **/
    /************************************************************/
    
    /*******  UPDATE DELL'INTERFACCIA A PARTIRE DAL DATABASE  *********/
      /**
       * Carica i dati dal database per un referto di secondo livello colposcopia
       * @param ctx
       */
      public static void loadFromDB2livColpo()
      {
        //esito (singolo)
         getDBColpo2lEsitoSelection();
        //reperti colposcopici miscellanei (multiplo)
        getDBColpo2lColpreSelection();
        //prelievo citologico (multipli)
        getDBColpo2lPrecitoSelection();
        //prelievo bioptico (multipli)
        getDBColpo2lPrebioSelection();
        //esito HPV
        getDBHPV2lEsitoSelection();
      }
    
      /**
       * Imposta l'esito della colposcopia (selezione singola) nel referto citologico di secondo livello
       * @param ctx
       */
      private static void getDBColpo2lEsitoSelection()
      {
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
    
        Integer n=(Integer)Ref_supportForProviders.getDBSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPES_2L);
        if(!ComparisonUtils.compare(bean.getColpo_esito(),n)){
          bean.setColpo_esito(n);
          bean.setDirty(false);
        }
      }
    
      /**
       * Impostazione del vettore dei referti colpsocopici miscellanei solo da DB
       * @return
       * @param ctx
       */
      private static void getDBColpo2lColpreSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPREView1");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
       Ref_supportForProviders.getDBRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPRE_2L);
      }
    
      /**
       * Impostazione del vettore dei dati prelievo citologico solo da DB
       * @return
       * @param ctx
       */
      private static void getDBColpo2lPrecitoSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPPCView1");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
       Ref_supportForProviders.getDBRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPPC_2L);
      }
    
      /**
       * Impostazione del vettore dei dati prelievo bioptico solo da DB
       * @return
       * @param ctx
       */
      private static void getDBColpo2lPrebioSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPPBView1");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
       Ref_supportForProviders.getDBRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPPB_2L);
      }
    
      /**
       * Imposta l'esito dell'HPV (selezione singola) nel referto citologico di secondo livello
       * @param ctx
       */
      private static void getDBHPV2lEsitoSelection()
      {
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
    
        Integer n=(Integer)Ref_supportForProviders.getDBSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_HPVES2_2L);
        if(!ComparisonUtils.compare(bean.getHpv_esito(),n)){
          bean.setDirty(false);
       }
        if (n != null){
          bean.setHpv_esito(n);
        } else
        {
          bean.setHpv_esito(new Integer(0));
        }
      }
    
      /*******  UPDATE DELL'INTERFACCIA A PARTIRE DALL'INTERFACCIA  *********/
      public static void loadFromUI2livColpo()
      {
        //ogni selezione singola va resettata se serve
        resetEsitoColpo2lIfNecessary();
    //
    //    //impostazione selzioni multiple a partire dall'interfaccia
    //   Boolean just_set=(Boolean)ctx.getHttpServletRequest().getAttribute("ref_colpre2l_just_set");
    //    if(just_set==null || !just_set.booleanValue()){
    //      //impostazione da sola interfaccia
    //      Vector v=getUIColpo2lRepertiSelection(ctx);
    //      ctx.getHttpServletRequest().getSession().setAttribute("ref_confcolpre2l",v);
    //    }
    //
    //    just_set=(Boolean)ctx.getHttpServletRequest().getAttribute("ref_colppc2l_just_set");
    //    if(just_set==null || !just_set.booleanValue()){
    //      //impostazione da sola interfaccia
    //      Vector v=getUIColpo2lPrecitoSelection(ctx);
    //      ctx.getHttpServletRequest().getSession().setAttribute("ref_confcolppc2l",v);
    //    }
    //
    //    just_set=(Boolean)ctx.getHttpServletRequest().getAttribute("ref_colppb2l_just_set");
    //    if(just_set==null || !just_set.booleanValue()){
    //      //impostazione da sola interfaccia
    //      Vector v=getUIColpo2lPrebioSelection(ctx);
    //      ctx.getHttpServletRequest().getSession().setAttribute("ref_confcolppb2l",v);
    //    }
    //
    }
    //
       private static void resetEsitoColpo2lIfNecessary()
      {
         Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
    
         //va resettato solo se l'esame risulta non eseguito
         ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
         Row r=ref.getCurrentRow();
         if(r==null)
           return;
    
         Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
    
         //se l'esame non risulta esgeuito non ci puo' esser giudizio diagnostico
         //DA CONTROLLARE L ELATRE SITUAZIONI POSSIBILI!!!!
         if(ConfigurationConstants.DB_FALSE.equals(r.getAttribute("Colposcopia"))){
            if(bean.getColpo_esito()!=null)
              bean.setColpo_esito(null);
          }
      }
    
    //
    //  /**
    //   * Impostazione del vettore dei reperti colposcopici miscellanei di secondo livello
    //   *
    //   * @return
    //   * @param ctx
    //   */
    //  private static Vector getUIColpo2lRepertiSelection()
    //  {
    //
    //    Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
    //    ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPREView1");
    //   ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
    //    ServletRequestDataSet ds = new ServletRequestDataSet(ctx.getHttpServletRequest(),"cnf_colpre_2l");
    //     int[] indices = SelectionUtils.getSelectedIndices(ds);
    //
    //     return Ref_supportForProviders.getUIRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPRE_2L,indices);
    //  }
    //  /**
    //   * Impostazione del vettore dei dati di prelievo citologico di secondo livello
    //   *
    //   * @return
    //   * @param ctx
    //   */
    //  private static Vector getUIColpo2lPrecitoSelection()
    //  {
    //
    //    Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
    //    ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPPCView1");
    //   ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
    //    ServletRequestDataSet ds = new ServletRequestDataSet(ctx.getHttpServletRequest(),"cnf_colppc_2l");
    //     int[] indices = SelectionUtils.getSelectedIndices(ds);
    //
    //     return Ref_supportForProviders.getUIRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPPC_2L,indices);
    //  }
    //
    //  /**
    //   * Impostazione del vettore dei dati di prelivo bioptico di secondo livello
    //   *
    //   * @return
    //   * @param ctx
    //   */
    //  private static Vector getUIColpo2lPrebioSelection()
    //  {
    //
    //    Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
    //    ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPPBView1");
    //   ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
    //    ServletRequestDataSet ds = new ServletRequestDataSet(ctx.getHttpServletRequest(),"cnf_colppb_2l");
    //     int[] indices = SelectionUtils.getSelectedIndices(ds);
    //
    //     return Ref_supportForProviders.getUIRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPPB_2L,indices);
    //  }
    
       /*******  UPDATE DEL DATABASE  *********/
      /**
       * Metodo che riporta sul database le scelte dell'utente
       * @param ctx
       */
      public static void updateDB2livColpo()
      {
         updateEsitoColpo2lSelection();
         updateColpoReperti2lSelection();
         updateColpoPrecito2lSelection();
         updateColpoPrebio2lSelection();
         updateEsitoHpv2lSelection();
      }
    
      private static void updateEsitoColpo2lSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        //configurazione dipendente dai referti su cui sto lavorando
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_supportForProviders.updateSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPES_2L,bean.getColpo_esito(),am,ref,2);
      }

      private static void updateColpoReperti2lSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPREView1");
        ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
    
        //faccio l'update su db
        Ref_supportForProviders.updateRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPRE_2L,am,ref,2);
      }
    
      private static void updateColpoPrecito2lSelection()
    {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPPCView1");
        ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");

        //faccio l'update su db
        Ref_supportForProviders.updateRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPPC_2L,am,ref,2);
      }
    
     private static void updateColpoPrebio2lSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject positi=am.findViewObject("Ref_SoCnfRef2livCOLPPBView1");
        ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
        //configurazione dipendente dai referti su cui sto lavorando
       ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");

        //faccio l'update su db
        Ref_supportForProviders.updateRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_COLPPB_2L,am,ref,2);
      }
    
      private static void updateEsitoHpv2lSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        //configurazione dipendente dai referti su cui sto lavorando
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_supportForProviders.updateSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_HPVES2_2L,bean.getHpv_esito(),am,ref,2);
      }

    /************************************************************/
    /**             2 LIVELLO - ISTOLOGIA BIOPTICA                    **/
    /************************************************************/
    
    /*******  UPDATE DELL'INTERFACCIA A PARTIRE DAL DATABASE  *********/
      /**
       * Carica i dati dal database per un referto di secondo livello colposcopia
       * @param ctx
       */
      public static void loadFromDB2livIstobio()
      {
        //grading (singolo)
        getDBIstobio2lGradingSelection();
        //diagnosi (singolo)
        getDBIstobio2lDiagnosiSelection();
      }
    
      /**
       * Imposta il grading dell'istologia bioptica (selezione singola) nel referto citologico di secondo livello
       * @param ctx
       */
      private static void getDBIstobio2lGradingSelection()
      {
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
    
        Integer n=(Integer)Ref_supportForProviders.getDBSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_ISTGRA_2L);
        if(n== null)
          n=ConfigurationConstants.DB_FALSE;
        if(!ComparisonUtils.compare(bean.getIstobio_grading(),n)){
          bean.setIstobio_grading(n);
          bean.setDirty(false);
        }
      }
    
      /**
       * Imposta la diagnosi dell'istologia bioptica (selezione singola) nel referto citologico di secondo livello
       * @param ctx
       */
      private static void getDBIstobio2lDiagnosiSelection()
      {
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
    
        Integer n=(Integer)Ref_supportForProviders.getDBSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_BIODIA_2L);
        if(n== null)
          n=ConfigurationConstants.DB_FALSE;
         if(!ComparisonUtils.compare(bean.getIstobio_diagnosi(),n)){
          bean.setIstobio_diagnosi(n);
          bean.setDirty(false);
        }
      }

      /*******  UPDATE DELL'INTERFACCIA A PARTIRE DALL'INTERFACCIA  *********/
      public static void loadFromUI2livIstobio()
      {
        resetGradingIstobio2lIfNecessary();
        resetDiagnosiIstobio2lIfNecessary();
      }
    
       private static void resetGradingIstobio2lIfNecessary()
      {
         Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
    
         //va resettato solo se l'esame risulta non eseguito
         ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
         Row r=ref.getCurrentRow();
         if(r==null)
           return;
    
         Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
    
         //se l'esame non risulta esgeuito non ci puo' esser giudizio diagnostico
         //DA CONTROLLARE L ELATRE SITUAZIONI POSSIBILI!!!!
         if(ConfigurationConstants.DB_FALSE.equals(r.getAttribute("Istbioptica"))){
            if(bean.getIstobio_grading()!=ConfigurationConstants.DB_FALSE)
              bean.setIstobio_grading(ConfigurationConstants.DB_FALSE);
          }
      }
    
       private static void resetDiagnosiIstobio2lIfNecessary()
      {
         Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
    
         //va resettato solo se l'esame risulta non eseguito
         ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
         Row r=ref.getCurrentRow();
         if(r==null)
           return;
    
         Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
    
         //se l'esame non risulta esgeuito non ci puo' esser giudizio diagnostico
         //DA CONTROLLARE L ELATRE SITUAZIONI POSSIBILI!!!!
         if(ConfigurationConstants.DB_FALSE.equals(r.getAttribute("Istbioptica"))){
            if(bean.getIstobio_diagnosi()!=ConfigurationConstants.DB_FALSE)
              bean.setIstobio_diagnosi(ConfigurationConstants.DB_FALSE);
          }
      }

       /*******  UPDATE DEL DATABASE  *********/
      /**
       * Metodo che riporta sul database le scelte dell'utente
       * @param ctx
       */
      public static void updateDB2livIstobio()
      {
         updateGradingIstobio2lSelection();
         updateDiagnosiIstobio2lSelection();
      }
    
      private static void updateGradingIstobio2lSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        //configurazione dipendente dai referti su cui sto lavorando
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_supportForProviders.updateSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_ISTGRA_2L,bean.getIstobio_grading(),am,ref,2);
      }
    
      private static void updateDiagnosiIstobio2lSelection()
      {
    
        Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        //configurazione dipendente dai referti su cui sto lavorando
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_supportForProviders.updateSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_BIODIA_2L,bean.getIstobio_diagnosi(),am,ref,2);
      }

      /************************************************************/
    /**             3 LIVELLO -   DIAGNOSI PEGGIORE              **/
    /************************************************************/
    
    /*******  UPDATE DELL'INTERFACCIA A PARTIRE DAL DATABASE  *********/
      /**
       * Carica i dati dal database per un referto
       * @param ctx
       */
      public static void loadFromDB2livDiagnosiPeggiore()
      {
          Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
    
        Integer n=(Integer)Ref_supportForProviders.getDBSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_RACDIA_2L);
        if(!ComparisonUtils.compare(bean.getDiagnosi_peggiore(),n)){
           bean.setDiagnosi_peggiore(n);
           bean.setDirty(false);
        }

       //diagnosi istologia bioptica in lettura
        getDBIstobio2lDiagnosiSelection();
      }
    
        /*******  UPDATE DEL DATABASE  *********/
      /**
       * Metodo che riporta sul database le scelte dell'utente
       * @param ctx
       */
      public static void updateDB2livDiagnosiPeggiore()
      {
         Ref_AppModule am=(Ref_AppModule)BindingContext.getCurrent().findDataControl("Ref_AppModuleDataControl").getDataProvider();
        ViewObject ref=am.findViewObject("Ref_SoRefertocito2livView4");
        Ref_2livBean bean=(Ref_2livBean)BindingContext.getCurrent().findDataControl("Ref_2livBeanDataControl").getDataProvider();
        //configurazione dipendente dai referti su cui sto lavorando
        ViewObject cnf=am.findViewObject("Ref_SoCodref2livc2View4");
        Ref_supportForProviders.updateSingleRefSelection(cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_RACDIA_2L,bean.getDiagnosi_peggiore(),am,ref,2);
      }
      
    // /***********************************************************************/
    // /***********************************************************************/
    // /************************   COLON  *************************************/
    // /***********************************************************************/
    // /***********************************************************************/
    //
    //
    //
    /****************************************************************/
    /****************  MAMMOGRAFICO         *************************/
    /****************************************************************/


    /************************************************************/
    /**             1 LIVELLO                                  **/
    /************************************************************/

    /*******  UPDATE DELL'INTERFACCIA A PARTIRE DAL DATABASE  *********/

    /**
     * Carica i dati dal database per un referto di primo livello
     * @param ctx
     */
    public static void loadFromDB1livMA(int lettura) {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertomammo1livView1");
        //il primo e' quello dipendente, cioe' quello corrente
        Row refRow = ref.first();
        Integer idreferto = null;
        if (refRow != null)
            idreferto = (Integer) refRow.getAttribute("Idreferto");
        ViewObject positi = am.findViewObject("Ref_SoCnfRef1livMXINDIView1");
        //configurazione NON dipendente dai referti su cui sto lavorando
        ViewObject cnf = am.findViewObject("Ref_SoCodref1livmaView5");

        RowSetIterator iter = null;
        try {
            iter = ViewHelper.getRowSetIterator(positi, "indicazioni_iter");
            //  int i=1;
            //per ogni dato di configurazione...
            while (iter.hasNext()) {
                Row r = iter.next();
                r.setAttribute("Dx", false);
                r.setAttribute("Sx", false);

                //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
                if (idreferto != null) {
                    String where =
                        "TPSCR = 'MA' AND IDCNFREF=" + r.getAttribute("Idcnfref1l") + " AND GRUPPO='" +
                        ConfigurationConstants.NOME_GRUPPO_INDICAZIONI_MA + "' AND IDREFERTO=" + idreferto;
                    //se uso anche la lettura
                    if (lettura > 0)
                        where += " AND LETTURA=" + lettura + "";
                    cnf.setWhereClause(where);

                    cnf.executeQuery();
                    Row r1 = cnf.first();
                    //se ci sono piu' righe prendo i check di tutte
                    while (r1 != null) {

                        if (!ConfigurationConstants.DB_FALSE.equals(r1.getAttribute("Dx")))
                            r.setAttribute("Dx", true);


                        if (!ConfigurationConstants.DB_FALSE.equals(r1.getAttribute("Sx")))
                            r.setAttribute("Sx", true);

                        r1 = cnf.next();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

        //06052013 GAIOn : progetto studio 45 mx
        //DENSITA
        //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
        if (idreferto != null) {
            ViewObject codDens = am.findViewObject("Ref_SoCodref1livmaDENSView1");
            String where = " IDREFERTO=" + idreferto;
            codDens.setWhereClause(where);
            codDens.executeQuery();
            //se uso anche la lettura
            if (lettura > 0) {
                Row[] rows = codDens.getFilteredRows("Lettura", new Integer(lettura));
                ViewObject densVo = am.findViewObject("Ref_SoCnfRef1livMXDENSView1");
                if (rows.length > 0) {
                    Row r1 = rows[0];
                    Integer idcnf = (Integer) r1.getAttribute("Idcnfref");
                    RowSetIterator iterDens = densVo.createRowSetIterator(null);
                    while (iterDens.hasNext()) {
                        Row r3 = iterDens.next();
                        Integer idcndDens = (Integer) r3.getAttribute("Idcnfref1l");
                        if (idcnf.intValue() == idcndDens) {
                            refRow.setAttribute("DensitaTemp", idcndDens);
                        } 
                    }
                    iterDens.closeRowSetIterator();
                } else {
                    Row r3 = densVo.first();
                    if (r3 != null) {
                        refRow.setAttribute("DensitaTemp", r3.getAttribute("Idcnfref1l"));
                    }
                }
            }
        }
    }

    /**
     * Carica i dati dal database per un referto di primo livello in modalita doppio cieco
     * @return
     * @param ctx
     */
    public static int loadFromDB1livMA() {
        Cnf_AppModule amCnf =
            (Cnf_AppModule) BindingContext.getCurrent().findDataControl("Cnf_AppModuleDataControl").getDataProvider();
        ViewObject cnfUtenti = amCnf.findViewObject("Cnf_SoCnfUtentiOperatoriView1");
        Map sess = ADFContext.getCurrent().getSessionScope();
        String username = (String) sess.get("user");
        String ulss = (String) sess.get("ulss");
        String tpscr = (String) sess.get("scr");
        String data = DateUtils.dateToString(new Date());
        String whereCnf =
            "Cnf_SoCnfUtentiOperatori.ULSS='" + ulss + "' AND Cnf_SoCnfUtentiOperatori.TPSCR='" + tpscr +
            "' AND Cnf_SoCnfUtentiOperatori.USERNAME = '" + username + "' " +
            " AND (Cnf_SoOpmedico.DTFINEVALOPMEDICO IS NULL OR Cnf_SoOpmedico.DTFINEVALOPMEDICO>TO_DATE('" + data +
            "','" + DateUtils.DATE_PATTERN + "'))";
        cnfUtenti.setWhereClause(whereCnf);
        cnfUtenti.executeQuery();
        Row user = cnfUtenti.first();
        //esiste un radiologo associato all'utente
        if (user != null) {
            Integer radiologo = (Integer) user.getAttribute("Operatore");
            RefMa_AppModule am =
                (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
            ViewObject ref = am.findViewObject("Ref_SoRefertomammo1livView1");
            //il primo e' quello dipendente, cioe' quello corrente
            Row r = ref.first();
            Integer idreferto = null;
            if (r != null)
                idreferto = (Integer) r.getAttribute("Idreferto");

            ViewObject positi = am.findViewObject("Ref_SoCnfRef1livMXINDIView1");
            //configurazione NON dipendente dai referti su cui sto lavorando
            ViewObject cnf = am.findViewObject("Ref_SoCodref1livmaView5");
            RowSetIterator iter = null;
            int lettura = 1;
            if (idreferto != null && ((Integer) r.getAttribute("Completo")).intValue() != 1) {

                if (r.getAttribute("L1Radiologo") != null &&
                    radiologo.compareTo((Integer) r.getAttribute("L1Radiologo")) == 0) {
                    lettura = 1;
                } else if (r.getAttribute("L2Radiologo") != null &&
                           radiologo.compareTo((Integer) r.getAttribute("L2Radiologo")) == 0) {
                    lettura = 2;
                } else if (r.getAttribute("RRadiologo") != null &&
                           radiologo.compareTo((Integer) r.getAttribute("RRadiologo")) == 0) {
                    lettura = 3;
                    sess.put("revisore", Boolean.TRUE);
                } else {
                    if (r.getAttribute("L2Radiologo") == null) {
                        lettura = 2;
                        r.setAttribute("L2Radiologo", radiologo);
                    } else if (r.getAttribute("RRadiologo") == null) {
                        lettura = 3;
                        r.setAttribute("RRadiologo", radiologo);
                        sess.put("revisore", Boolean.TRUE);
                    } else {
                        //tutte le letture sono gia' compilate
                        return -1;
                    }
                }
            }
            try {
                iter = ViewHelper.getRowSetIterator(positi, "indicazioni_iter");
                //  int i=1;
                //per ogni dato di configurazione...
                while (iter.hasNext()) {
                    //... inserisco un record nel vo
                    Row r2 = iter.next();
                    r2.setAttribute("Dx", false);
                    r2.setAttribute("Sx", false);

                    //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
                    if (idreferto != null) {

                        String where =
                            "IDCNFREF=" + r2.getAttribute("Idcnfref1l") + " AND GRUPPO='" +
                            ConfigurationConstants.NOME_GRUPPO_INDICAZIONI_MA + "' AND IDREFERTO=" + idreferto;
                        //se uso anche la lettura
                        if (lettura > 0)
                            where += " AND LETTURA=" + lettura + "";
                        cnf.setWhereClause(where);
                        cnf.executeQuery();
                        Row r1 = cnf.first();
                        //se ci sono piu' righe prendo i check di tutte
                        while (r1 != null) {

                            if (!ConfigurationConstants.DB_FALSE.equals(r1.getAttribute("Dx")))
                                r2.setAttribute("Dx", true);


                            if (!ConfigurationConstants.DB_FALSE.equals(r1.getAttribute("Sx")))
                                r2.setAttribute("Sx", true);

                            r1 = cnf.next();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            } finally {
                if (iter != null)
                    iter.closeRowSetIterator();
            }

            //06052013 GAIOn : progetto studio 45 mx
            //DENSITA
            //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
            if (idreferto != null) {
                ViewObject codDens = am.findViewObject("Ref_SoCodref1livmaDENSView1");
                String where = " IDREFERTO=" + idreferto;
                codDens.setWhereClause(where);
                codDens.executeQuery();
                //se uso anche la lettura
                if (lettura > 0) {
                    Row[] rows = codDens.getFilteredRows("Lettura", new Integer(lettura));
                    ViewObject densVo = am.findViewObject("Ref_SoCnfRef1livMXDENSView1");
                    if (rows.length > 0) {
                        Row r1 = rows[0];
                        Integer idcnf = (Integer) r1.getAttribute("Idcnfref");
                        RowSetIterator iterDens = densVo.createRowSetIterator(null);
                        while (iterDens.hasNext()) {
                            Row r3 = iterDens.next();
                            Integer idcndDens = (Integer) r3.getAttribute("Idcnfref1l");
                            if (idcnf.intValue() == idcndDens) {
                                r.setAttribute("DensitaTemp", idcndDens);
                            } 
                        }
                        iterDens.closeRowSetIterator();
                    } else {
                        Row r3 = densVo.first();
                        if (r3 != null) {
                            r.setAttribute("DensitaTemp", r3.getAttribute("Idcnfref1l"));
                        }
                    }
                }
            }

            return lettura;
        } else {
            // non trovato radiologo associato all'utente
            return -1;
        }
    }


    /**
     * Carica i dati dal database per un referto di secondo livello
     * (tipologia di esame mammografico)
     * @param ctx
     */
    public static void loadFromDB2livMA() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertomammo2livView1");
        //il primo e' quello dipendente, cioe' quello corrente
        Row r = ref.first();
        Integer idreferto = null;
        if (r != null)
            idreferto = (Integer) r.getAttribute("Idreferto");
        ViewObject positi = am.findViewObject("Ref_SoCnfRef2livMXESTPView1");
        //configurazione NON dipendente dai referti su cui sto lavorando
        ViewObject cnf = am.findViewObject("Ref_SoCodref2livmaView2");
        ViewObject vo = am.findViewObject("Ref_SoCnfIndicazioniView1");
        //ripuliamo il vo
        ViewHelper.clearVO(vo);
        RowSetIterator iter = null;
        try {
            iter = ViewHelper.getRowSetIterator(positi, "indicazioni_iter");
            //  int i=1;
            //per ogni dato di configurazione...
            while (iter.hasNext()) {
                //... inserisco un record nel vo
                r = iter.next();
                Row newR = vo.createRow();
                vo.insertRow(newR);
                newR.setAttribute("Idcnfref", (Integer) r.getAttribute("Idcnfref2l"));
                newR.setAttribute("Descrizione", (String) r.getAttribute("Descrizione"));
                newR.setAttribute("Gruppo", (String) r.getAttribute("Gruppo"));
                newR.setAttribute("Dx", new  Number (ConfigurationConstants.DB_FALSE));
                newR.setAttribute("Sx", new Number (ConfigurationConstants.DB_FALSE));

                //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
                if (idreferto != null) {
                    String where =
                        "IDCNFREF=" + r.getAttribute("Idcnfref2l") + " AND GRUPPO='" +
                        ConfigurationConstants.NOME_GRUPPO_TIPO_ESAME + "' AND IDREFERTO=" + idreferto;

                    cnf.setWhereClause(where);

                    cnf.executeQuery();
                    Row r1 = cnf.first();
                    //se ci sono piu' righe prendo i check di tutte
                    while (r1 != null) {

                        if (!ConfigurationConstants.DB_FALSE.equals(r1.getAttribute("Dx")))
                            newR.setAttribute("Dx", new Number(r1.getAttribute("Dx")));


                        if (!ConfigurationConstants.DB_FALSE.equals(r1.getAttribute("Sx")))
                            newR.setAttribute("Sx", new Number(r1.getAttribute("Sx")));

                        r1 = cnf.next();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

    }


    public static void loadFromDB3livMA() {
        getDBIntascSelection();

        getDBSedeIntSelection();
    }

    /**
     * Carica i dati dal database per un referto di secondo livello
     * (tipologia di esame mammografico)
     * @param ctx
     */
    public static void getDBIntascSelection() {
        DCBindingContainer bindings = (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding voIter = bindings.findIteratorBinding("Ref_SoCnfRef2livINTASCView1Iterator");
        //configurazione dipendente dall'intervento su cui sto lavorando
        DCIteratorBinding cnfVoIter = bindings.findIteratorBinding("Ref_SoCodref3livmaView1Iterator");

        Ref_supportForProviders.getDBRefSelection(voIter.getViewObject(), "Idcnfref2l", cnfVoIter.getViewObject(),
                                                  "Idcnfref", ConfigurationConstants.NOME_GRUPPO_ASCELLA);
    }

    /**
     * Carica i dati dal database per la sede dell'intervento
     * (screening mammografico)
     * @param ctx
     */
    private static void getDBSedeIntSelection() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoInterventomammoView1");

        Row r = ref.getCurrentRow();
        Integer idint = null;
        if (r != null)
            idint = (Integer) r.getAttribute("Idint");
        ViewObject positi = am.findViewObject("Ref_SoCnfRef2livINTSEDView1");
        //configurazione NON dipendente dai referti su cui sto lavorando
        ViewObject cnf = am.findViewObject("Ref_SoCodref3livmaView2");

        RowSetIterator iter = null;
        RowSetIterator iter2 = null;
        try {
            iter = ViewHelper.getRowSetIterator(positi, "indicazioni_iter");

            //per ogni dato di configurazione...
            while (iter.hasNext()) {

                r = iter.next();
                r.setAttribute("Dx", false);
                r.setAttribute("Sx", false);
                System.out.println("configuraz " + r.getAttribute("Descrizione"));
                //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
                if (idint != null) {
                    String where =
                        "IDCNFREF=" + r.getAttribute("Idcnfref2l") + " AND GRUPPO='" +
                        ConfigurationConstants.NOME_GRUPPO_SEDE_INT + "' AND IDINT=" + idint;
                    cnf.setWhereClause(where);
                    cnf.executeQuery();
                    System.out.println("righe " + cnf.getEstimatedRowCount());

                    iter2 = ViewHelper.getRowSetIterator(cnf, "codref_iter");
                    //se ci sono piu' righe prendo i check di tutte
                    while (iter2.hasNext()) {
                        Row r1 = iter2.next();
                        if (!ConfigurationConstants.DB_FALSE.equals(r1.getAttribute("Dx")))
                            r.setAttribute("Dx", true);

                        if (!ConfigurationConstants.DB_FALSE.equals(r1.getAttribute("Sx")))
                            r.setAttribute("Sx", true);

                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
            if (iter2 != null)
                iter2.closeRowSetIterator();
        }

    }

    //
    //  /**
    //   * Carica i dati dal database per un referto PFAS di primo livello
    //   * @param ctx
    //   */
    //  public static void loadFromDB1livPF()
    //  {
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    ViewObject ref=am.findViewObject("Ref_SoRefertopfasView1");
    //    //il primo e' quello dipendente, cioe' quello corrente
    //    Row r=ref.first();
    //    Integer idreferto=null;
    //    if(r!=null)
    //      idreferto=(Integer)r.getAttribute("Idreferto");
    //    ViewObject positi=am.findViewObject("Ref_SoCnfRef1livESA1LIView1");
    //    //configurazione NON dipendente dai referti su cui sto lavorando
    //    ViewObject cnf=am.findViewObject("Ref_SoCodref1livpfView1");
    //    ViewObject vo=am.findViewObject("Ref_SoCnfEsamiView1");
    //    //ripuliamo il vo
    //    ViewHelper.clearVO(vo);
    //    RowSetIterator iter=null;
    //    try
    //    {
    //      iter=ViewHelper.getRowSetIterator(positi, "esami_iter");
    //    //  int i=1;
    //      //per ogni dato di configurazione...
    //      while(iter.hasNext())
    //      {
    //      //... inserisco un record nel vo
    //        r=iter.next();
    //        Row newR=vo.createRow();
    //        vo.insertRow(newR);
    //        newR.setAttribute("Idcnfref",(Integer)r.getAttribute("Idcnfref1l"));
    //        newR.setAttribute("Descrizione",(String)r.getAttribute("Descrizione"));
    //        newR.setAttribute("Gruppo",(String)r.getAttribute("Gruppo"));
    //        newR.setAttribute("UnitaMisura",(String)r.getAttribute("UnitaMisura"));
    //        /*newR.setAttribute("Min1",(Integer)r.getAttribute("Min1"));
    //        newR.setAttribute("Max1",(Integer)r.getAttribute("Max1"));*/
    //
    //        //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
    //        if(idreferto!=null){
    //          String where="TPSCR = 'PF' AND IDCNFREF="+r.getAttribute("Idcnfref1l")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI+
    //          "' AND IDREFERTO="+idreferto;
    //
    //          cnf.setWhereClause(where);
    //      //   System.out.println(cnf.getQuery());
    //          cnf.executeQuery();
    //          Row r1=cnf.first();
    //
    //          while(r1!=null)
    //          {
    //            if (r1.getAttribute("Valore") != null){
    //              newR.setAttribute("Valore",r1.getAttribute("Valore"));
    //              newR.setAttribute("Anormalita",r1.getAttribute("Anormalita"));
    //            }
    //            r1=cnf.next();
    //          }
    //        }
    //      }
    //    }
    //    catch(Exception ex)
    //    {
    //      ex.printStackTrace();
    //
    //    }
    //    finally
    //    {
    //      if(iter!=null)
    //        iter.closeRowSetIterator();
    //    }
    //
    //    ctx.getHttpServletRequest().setAttribute("ref_esami_just_set",Boolean.TRUE);
    //
    //  }
    //
    //  /**
    //   * Carica i dati dal database per un referto PFAS di primo livello
    //   * @param ctx
    //   */
    //  public static void loadFromDB1livEsamiPfas()
    //  {
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    ViewObject ref=am.findViewObject("Ref_SoRefertopfasView1");
    //    //il primo e' quello dipendente, cioe' quello corrente
    //    Row r=ref.first();
    //    Integer idreferto=null;
    //    if(r!=null)
    //      idreferto=(Integer)r.getAttribute("Idreferto");
    //    ViewObject positi=am.findViewObject("Ref_SoCnfRef1livPFA1LIView1");
    //    //configurazione NON dipendente dai referti su cui sto lavorando
    //    ViewObject cnf=am.findViewObject("Ref_SoCodref1livpfView2");
    //    ViewObject vo=am.findViewObject("Ref_SoCnfEsamiView2");
    //    //ripuliamo il vo
    //    ViewHelper.clearVO(vo);
    //    RowSetIterator iter=null;
    //    try
    //    {
    //      iter=ViewHelper.getRowSetIterator(positi, "esami_iter");
    //    //  int i=1;
    //      //per ogni dato di configurazione...
    //      while(iter.hasNext())
    //      {
    //      //... inserisco un record nel vo
    //        r=iter.next();
    //        Row newR=vo.createRow();
    //        vo.insertRow(newR);
    //        newR.setAttribute("Idcnfref",(Integer)r.getAttribute("Idcnfref1l"));
    //        newR.setAttribute("Descrizione",(String)r.getAttribute("Descrizione"));
    //        newR.setAttribute("Gruppo",(String)r.getAttribute("Gruppo"));
    //        newR.setAttribute("UnitaMisura",(String)r.getAttribute("UnitaMisura"));
    //        /*newR.setAttribute("Min1",(Integer)r.getAttribute("Min1"));
    //        newR.setAttribute("Max1",(Integer)r.getAttribute("Max1"));*/
    //
    //        //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
    //        if(idreferto!=null){
    //          String where="TPSCR = 'PF' AND IDCNFREF="+r.getAttribute("Idcnfref1l")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS+
    //          "' AND IDREFERTO="+idreferto;
    //
    //          cnf.setWhereClause(where);
    //      //   System.out.println(cnf.getQuery());
    //          cnf.executeQuery();
    //          Row r1=cnf.first();
    //
    //          while(r1!=null)
    //          {
    //            if (r1.getAttribute("Valore") != null){
    //              newR.setAttribute("Valore",r1.getAttribute("Valore"));
    //              newR.setAttribute("Anormalita",r1.getAttribute("Anormalita"));
    //            }
    //            r1=cnf.next();
    //          }
    //        }
    //      }
    //    }
    //    catch(Exception ex)
    //    {
    //      ex.printStackTrace();
    //
    //    }
    //    finally
    //    {
    //      if(iter!=null)
    //        iter.closeRowSetIterator();
    //    }
    //
    //    ctx.getHttpServletRequest().setAttribute("ref_esami_just_set",Boolean.TRUE);
    //
    //  }
    //
    //  /**
    //   * Carica i dati dal database per un referto PFAS di secondo livello
    //   * @param ctx
    //   */
    //  public static void loadFromDB2livEsamiPfas()
    //  {
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    ViewObject ref=am.findViewObject("Ref_SoProcedurapfasView1");
    //    //il primo e' quello dipendente, cioe' quello corrente
    //    Row r=ref.getCurrentRow();
    //    Integer idProc=null;
    //    if(r!=null)
    //      idProc=(Integer)r.getAttribute("Idproc");
    //    ViewObject positi=am.findViewObject("Ref_SoCnfRef2livPFA2LIView1");
    //    //configurazione NON dipendente dai referti su cui sto lavorando
    //    ViewObject cnf=am.findViewObject("Ref_SoCodref2livpfView1");
    //    ViewObject vo=am.findViewObject("Ref_SoCnfEsamiView3");
    //    //ripuliamo il vo
    //    ViewHelper.clearVO(vo);
    //    RowSetIterator iter=null;
    //    try
    //    {
    //      iter=ViewHelper.getRowSetIterator(positi, "esami_iter");
    //    //  int i=1;
    //      //per ogni dato di configurazione...
    //      while(iter.hasNext())
    //      {
    //      //... inserisco un record nel vo
    //        r=iter.next();
    //        Row newR=vo.createRow();
    //        vo.insertRow(newR);
    //        newR.setAttribute("Idcnfref",(Integer)r.getAttribute("Idcnfref2l"));
    //        newR.setAttribute("Descrizione",(String)r.getAttribute("Descrizione"));
    //        newR.setAttribute("Gruppo",(String)r.getAttribute("Gruppo"));
    //        newR.setAttribute("UnitaMisura",(String)r.getAttribute("UnitaMisura"));
    //        newR.setAttribute("Anormalita",new Integer(0));
    //
    //        //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
    //        if(idProc!=null){
    //          String where="TPSCR = 'PF' AND IDCNFREF="+r.getAttribute("Idcnfref2l")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L+
    //          "' AND IDPROC="+idProc;
    //
    //          cnf.setWhereClause(where);
    //      //   System.out.println(cnf.getQuery());
    //          cnf.executeQuery();
    //          Row r1=cnf.first();
    //
    //          while(r1!=null)
    //          {
    //            if (r1.getAttribute("Valore") != null){
    //              newR.setAttribute("Valore",r1.getAttribute("Valore"));
    //              newR.setAttribute("Anormalita",r1.getAttribute("Anormalita"));
    //            }
    //            r1=cnf.next();
    //          }
    //        }
    //      }
    //    }
    //    catch(Exception ex)
    //    {
    //      ex.printStackTrace();
    //
    //    }
    //    finally
    //    {
    //      if(iter!=null)
    //        iter.closeRowSetIterator();
    //    }
    //
    //    ctx.getHttpServletRequest().setAttribute("ref_esami_just_set",Boolean.TRUE);
    //
    //  }
    //
    //  /**
    //   * Carica i dati dal database per un referto PFAS di secondo livello
    //   * @param ctx
    //   */
    //  public static void loadFromDB2livScambioPf()
    //  {
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    ViewObject ref=am.findViewObject("Ref_SoProcedurapfasView1");
    //    //il primo e' quello dipendente, cioe' quello corrente
    //    Row r=ref.getCurrentRow();
    //    Integer idProc=null;
    //    Integer tipoProc=null;
    //    if(r!=null){
    //      idProc=(Integer)r.getAttribute("Idproc");
    //      tipoProc=(Integer)r.getAttribute("Tpprocedura");
    //    }
    //    ViewObject positi=am.findViewObject("Ref_SoCnfRef2livPFA2LIView1");
    //    //configurazione NON dipendente dai referti su cui sto lavorando
    //    ViewObject cnf=am.findViewObject("Ref_SoCodref2livpfView1");
    //
    //    //****analisi PRE scambio****//
    //    ViewObject vo=am.findViewObject("Ref_SoCnfEsamiView4");  //analisi pre scambio
    //    //ripuliamo il vo
    //    ViewHelper.clearVO(vo);
    //    RowSetIterator iter=null;
    //    try
    //    {
    //      iter=ViewHelper.getRowSetIterator(positi, "esami_pre_iter");
    //    //  int i=1;
    //      //per ogni dato di configurazione...
    //      while(iter.hasNext())
    //      {
    //      //... inserisco un record nel vo
    //        r=iter.next();
    //        Row newR=vo.createRow();
    //        vo.insertRow(newR);
    //        newR.setAttribute("Idcnfref",(Integer)r.getAttribute("Idcnfref2l"));
    //        newR.setAttribute("Descrizione",(String)r.getAttribute("Descrizione"));
    //        newR.setAttribute("Gruppo",(String)r.getAttribute("Gruppo"));
    //        newR.setAttribute("UnitaMisura",(String)r.getAttribute("UnitaMisura"));
    //        newR.setAttribute("Anormalita",new Integer(0));
    //
    //        //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
    //        if(idProc!=null){
    //          String where="TPSCR = 'PF' AND IDCNFREF="+r.getAttribute("Idcnfref2l")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L+
    //          "' AND IDPROC="+idProc +
    //          " AND TIPO_PROCEDURA="+tipoProc;
    //
    //          cnf.setWhereClause(where);
    //          cnf.executeQuery();
    //          Row r1=cnf.first();
    //
    //          while(r1!=null)
    //          {
    //            if (r1.getAttribute("Valore") != null){
    //              newR.setAttribute("Valore",r1.getAttribute("Valore"));
    //              newR.setAttribute("Anormalita",r1.getAttribute("Anormalita"));
    //            }
    //            r1=cnf.next();
    //          }
    //        }
    //      }
    //
    //      //*****analisi POST scambio******//
    //      vo=am.findViewObject("Ref_SoCnfEsamiView5");  //analisi pre scambio
    //      //ripuliamo il vo
    //      ViewHelper.clearVO(vo);
    //
    //      //per ogni dato di configurazione...
    //      iter=ViewHelper.getRowSetIterator(positi, "esami_post_iter");
    //      if (tipoProc != null)
    //        tipoProc = tipoProc.add(3);
    //      while(iter.hasNext())
    //      {
    //      //... inserisco un record nel vo
    //        r=iter.next();
    //        Row newR=vo.createRow();
    //        vo.insertRow(newR);
    //        newR.setAttribute("Idcnfref",(Integer)r.getAttribute("Idcnfref2l"));
    //        newR.setAttribute("Descrizione",(String)r.getAttribute("Descrizione"));
    //        newR.setAttribute("Gruppo",(String)r.getAttribute("Gruppo"));
    //        newR.setAttribute("UnitaMisura",(String)r.getAttribute("UnitaMisura"));
    //        newR.setAttribute("Anormalita",new Integer(0));
    //
    //        //cerco se ci sono dati per quel referto ed eventualmnet eli imposto nel vo
    //        if(idProc!=null){
    //
    //          String where="TPSCR = 'PF' AND IDCNFREF="+r.getAttribute("Idcnfref2l")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L+
    //          "' AND IDPROC="+idProc +
    //          " AND TIPO_PROCEDURA="+tipoProc;
    //          cnf.setWhereClause(where);
    //          cnf.executeQuery();
    //          Row r1=cnf.first();
    //
    //          while(r1!=null)
    //          {
    //            if (r1.getAttribute("Valore") != null){
    //              newR.setAttribute("Valore",r1.getAttribute("Valore"));
    //              newR.setAttribute("Anormalita",r1.getAttribute("Anormalita"));
    //            }
    //            r1=cnf.next();
    //          }
    //        }
    //      }
    //    }
    //    catch(Exception ex)
    //    {
    //      ex.printStackTrace();
    //
    //    }
    //    finally
    //    {
    //      if(iter!=null)
    //        iter.closeRowSetIterator();
    //    }
    //
    //    ctx.getHttpServletRequest().setAttribute("ref_esami_just_set",Boolean.TRUE);
    //
    //  }
    //
    //
    /*******  UPDATE DELL'INTERFACCIA A PARTIRE DALL'INTERFACCIA  *********/


    //
    //  /**
    //   * Metodo che reimposta l'interfaccia in base alla selezine precednete
    //   * @param ctx
    //   */
    //  public static void loadFromUI2livPF()
    //  {
    //    Boolean just_set=(Boolean)ctx.getHttpServletRequest().getAttribute("ref_pfas_just_set");
    //    if(just_set==null || !just_set.booleanValue()){
    //      //impostazione da sola interfaccia
    //      Vector v=getUIProceduraSelection(ctx);
    //      ctx.getHttpServletRequest().getSession().setAttribute("ref_confascella",v);
    //    }
    //    //altrimenti non faccio nulla
    //  }
    //
    //  private static Vector getUIProceduraSelection()
    //  {
    //
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    //ViewObject positi=am.findViewObject("Ref_SoCnfRef2livINTASCView1");
    //   //ViewObject cnf=am.findViewObject("Ref_SoCodref3livmaView1");
    //    ServletRequestDataSet ds = new ServletRequestDataSet(ctx.getHttpServletRequest(),"cnf_ascella");
    //     int[] indices = SelectionUtils.getSelectedIndices(ds);
    //    return null;
    //     //return Ref_supportForProviders.getUIRefSelection(positi,"Idcnfref2l",cnf,"Idcnfref",ConfigurationConstants.NOME_GRUPPO_ASCELLA,indices);
    //  }
    //
    //
    /*******  UPDATE DEL DATABASE  *********/

    /**
     * Metodo che riporta sul database le scelte dell'utente
     * @param ctx
     */
    public static void updateDB1livMA(int lettura) {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertomammo1livView1");
        //il primo e' quello dipendente, cioe' quello corrente
        Row refRow = ref.first();
        Integer idreferto = (Integer) refRow.getAttribute("Idreferto");
        String ulss = (String) refRow.getAttribute("Ulss");
        String tpscr = (String) refRow.getAttribute("Tpscr");
        ViewObject positi = am.findViewObject("Ref_SoCnfRef1livMXINDIView1");
        //configurazione NON dipendente dai referti su cui sto lavorando
        ViewObject cnf = am.findViewObject("Ref_SoCodref1livmaView5");

        RowSetIterator iter = null;
        try {
            iter = ViewHelper.getRowSetIterator(positi, "indicazioni_iter");
            //per ogni dato del vo
            while (iter.hasNext()) {
                Row r = iter.next();
                //cerco se c'e' un record corrispondente nelle configurazioni
                String where =
                    "IDCNFREF=" + r.getAttribute("Idcnfref1l") + " AND GRUPPO='" +
                    ConfigurationConstants.NOME_GRUPPO_INDICAZIONI_MA + "' AND IDREFERTO=" + idreferto +
                    " AND LETTURA=" + lettura + "";
                cnf.setWhereClause(where);
                cnf.executeQuery();

                Row r1 = cnf.first();

                //se c'e' almeno un check...
                if ((r.getAttribute("Dx") != null && (Boolean) r.getAttribute("Dx")) ||
                    (r.getAttribute("Sx") != null && (Boolean) r.getAttribute("Sx"))) {
                    //se non c'e' la riga la creo
                    if (r1 == null) {
                        r1 = cnf.createRow();
                        cnf.insertRow(r1);
                        r1.setAttribute("Id", am.getNextIdCnfRef1livMA());
                        r1.setAttribute("Idreferto", idreferto);
                        r1.setAttribute("Gruppo", ConfigurationConstants.NOME_GRUPPO_INDICAZIONI_MA);
                        r1.setAttribute("Lettura", new Integer(lettura));
                        r1.setAttribute("Idcnfref", r.getAttribute("Idcnfref1l"));
                        r1.setAttribute("Ulss", ulss);
                        r1.setAttribute("Tpscr", tpscr);
                        r1.setAttribute("Dx", ConfigurationConstants.DB_FALSE);
                        r1.setAttribute("Sx", ConfigurationConstants.DB_FALSE);
                    }
                    //aggiorno, se serve, la selezione

                    if (r.getAttribute("Dx") != null && (Boolean) r.getAttribute("Dx"))
                        r1.setAttribute("Dx", ConfigurationConstants.DB_TRUE);
                    else
                        r1.setAttribute("Dx", ConfigurationConstants.DB_FALSE);

                    if (r.getAttribute("Sx") != null && (Boolean) r.getAttribute("Sx"))
                        r1.setAttribute("Sx", ConfigurationConstants.DB_TRUE);
                    else
                        r1.setAttribute("Sx", ConfigurationConstants.DB_FALSE);

                } else //non c'e' nessun check, la riga va cancellata
                {
                    if (r1 != null)
                        r1.remove();
                }
            }
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

        //densita'
        Integer densita = (Integer)refRow.getAttribute("DensitaTemp");
        ViewObject codDens = am.findViewObject("Ref_SoCodref1livmaDENSView1");
        //cerco se c'e' un record corrispondente nelle configurazioni
        Row[] rDens = codDens.getFilteredRows("Lettura", new Integer(lettura));
        if (densita != null && densita > 0) {
            //se non c'e' la riga la creo
            if (rDens.length == 0) {
                Row r1 = codDens.createRow();
                codDens.insertRow(r1);
                r1.setAttribute("Id", am.getNextIdCnfRef1livMA());
                r1.setAttribute("Idreferto", idreferto);
                r1.setAttribute("Gruppo", ConfigurationConstants.NOME_GRUPPO_DENSITA);
                r1.setAttribute("Lettura", new Integer(lettura));
                r1.setAttribute("Idcnfref", densita);
                r1.setAttribute("Ulss", ulss);
                r1.setAttribute("Tpscr", tpscr);
            } else //altrimenti aggiorno
            {
                Row r1 = rDens[0];
                r1.setAttribute("Idcnfref", densita);
            }
        } else //se c'e' qualcosa devo eliminarlo
        {
            if (rDens.length > 0) {
                rDens[0].remove();
            }
        }
    }


    /**
     * Metodo che riporta sul database le scelte dell'utente
     * @param ctx
     */
    public static void updateDB2livMA() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoRefertomammo2livView1");
        //il primo e' quello dipendente, cioe' quello corrente
        Row r = ref.first();
        Integer idreferto = (Integer) r.getAttribute("Idreferto");
        String ulss = (String) r.getAttribute("Ulss");
        String tpscr = (String) r.getAttribute("Tpscr");
        //  ViewObject positi=am.findViewObject("Ref_SoCnfRef1livMXINDIView1");
        //configurazione NON dipendente dai referti su cui sto lavorando
        ViewObject cnf = am.findViewObject("Ref_SoCodref2livmaView2");
        ViewObject vo = am.findViewObject("Ref_SoCnfIndicazioniView1");

        RowSetIterator iter = null;
        try {
            iter = ViewHelper.getRowSetIterator(vo, "indicazioni_iter");
            //per ogni dato del vo
            while (iter.hasNext()) {
                r = iter.next();
                //cerco se c'e' un record corrispondente nelle configurazioni
                String where =
                    "IDCNFREF=" + r.getAttribute("Idcnfref") + " AND GRUPPO='" +
                    ConfigurationConstants.NOME_GRUPPO_TIPO_ESAME + "' AND IDREFERTO=" + idreferto;
                cnf.setWhereClause(where);
                cnf.executeQuery();

                Row r1 = cnf.first();

                System.out.println(idreferto);
                System.out.println(r.getAttribute("Dx"));
                System.out.println(r.getAttribute("Sx"));

                //se c'e' almeno un check...
                if ((r.getAttribute("Dx") != null && ((Number) r.getAttribute("Dx")).intValue() > 0) ||
                    (r.getAttribute("Sx") != null && ((Number) r.getAttribute("Sx")).intValue() > 0)) {
                    //se non c'e' la riga la creo
                    if (r1 == null) {
                        r1 = cnf.createRow();
                        cnf.insertRow(r1);
                        r1.setAttribute("Id", am.getNextIdCnfRef2livMA());
                        r1.setAttribute("Idreferto", idreferto);
                        r1.setAttribute("Gruppo", ConfigurationConstants.NOME_GRUPPO_TIPO_ESAME);
                        //r1.setAttribute("Lettura",new Integer(lettura));
                        r1.setAttribute("Idcnfref", r.getAttribute("Idcnfref"));
                        r1.setAttribute("Ulss", ulss);
                        r1.setAttribute("Tpscr", tpscr);
                        r1.setAttribute("Dx", new Number(ConfigurationConstants.DB_FALSE));
                        r1.setAttribute("Sx", new Number (ConfigurationConstants.DB_FALSE));
                    }
                    //aggiorno, se serve, la selezione
                    if (r.getAttribute("Dx") != null && !r.getAttribute("Dx").equals(r1.getAttribute("Dx")) &&
                        ((Number) r.getAttribute("Dx")).intValue() > 0) {
                        r1.setAttribute("Dx", (Number)r.getAttribute("Dx"));
                    }

                    if (r.getAttribute("Sx") != null && !r.getAttribute("Sx").equals(r1.getAttribute("Sx")) &&
                        ((Number) r.getAttribute("Sx")).intValue() > 0) {
                        r1.setAttribute("Sx", (Number) r.getAttribute("Sx"));
                    }

                } else //non c'e' nessun check, la riga va cancellata
                {
                    if (r1 != null)
                        r1.remove();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

    }


    /**
     * Metodo che riporta sul database le scelte dell'utente
     * @param ctx
     */
    public static void updateDB3livMA() {
        updateAscellaSelection();

        updateSedeIntSelection();
    }

    private static void updateAscellaSelection() {

        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject positi = am.findViewObject("Ref_SoCnfRef2livINTASCView1");
        ViewObject ref = am.findViewObject("Ref_SoInterventomammoView1");
        //configurazione dipendente dai referti su cui sto lavorando
        ViewObject cnf = am.findViewObject("Ref_SoCodref3livmaView1");

        //faccio l'update su db
        Ref_supportForProviders.updateRefSelectionMA(positi, "Idcnfref2l", cnf, "Idcnfref",
                                                     ConfigurationConstants.NOME_GRUPPO_ASCELLA, am, ref);
    }


    private static void updateSedeIntSelection() {
        RefMa_AppModule am =
            (RefMa_AppModule) BindingContext.getCurrent().findDataControl("RefMa_AppModuleDataControl").getDataProvider();
        ViewObject ref = am.findViewObject("Ref_SoInterventomammoView1");

        Row r = ref.getCurrentRow();
        Integer idint = (Integer) r.getAttribute("Idint");
        String ulss = (String) r.getAttribute("Ulss");
        String tpscr = (String) r.getAttribute("Tpscr");

        //configurazione NON dipendente dai referti su cui sto lavorando
        ViewObject cnf = am.findViewObject("Ref_SoCodref3livmaView2");

        ViewObject vo = am.findViewObject("Ref_SoCnfRef2livINTSEDView1");
        RowSetIterator iter = null;
        try {
            iter = ViewHelper.getRowSetIterator(vo, "indicazioni_iter");
            //per ogni dato del vo
            while (iter.hasNext()) {
                r = iter.next();
                //cerco se c'e' un record corrispondente nelle configurazioni
                String where =
                    "IDCNFREF=" + r.getAttribute("Idcnfref2l") + " AND GRUPPO='" +
                    ConfigurationConstants.NOME_GRUPPO_SEDE_INT + "' AND IDINT=" + idint;
                cnf.setWhereClause(where);
                cnf.executeQuery();

                Row r1 = cnf.first();

                //se c'e' almeno un check...
                if ((r.getAttribute("Dx") != null && (Boolean) r.getAttribute("Dx")) ||
                    (r.getAttribute("Sx") != null && (Boolean) r.getAttribute("Sx"))) {
                    //se non c'e' la riga la creo
                    if (r1 == null) {
                        r1 = cnf.createRow();
                        cnf.insertRow(r1);
                        r1.setAttribute("Id", am.getNextIdCnfRef3livMA());
                        r1.setAttribute("Idint", idint);
                        r1.setAttribute("Gruppo", ConfigurationConstants.NOME_GRUPPO_SEDE_INT);
                        r1.setAttribute("Idcnfref", r.getAttribute("Idcnfref2l"));
                        r1.setAttribute("Ulss", ulss);
                        r1.setAttribute("Tpscr", tpscr);
                        r1.setAttribute("Dx", ConfigurationConstants.DB_FALSE);
                        r1.setAttribute("Sx", ConfigurationConstants.DB_FALSE);
                    }
                    //aggiorno, se serve, la selezione
                    if (r.getAttribute("Dx") != null && (Boolean) r.getAttribute("Dx"))
                        r1.setAttribute("Dx", ConfigurationConstants.DB_TRUE);
                    else
                        r1.setAttribute("Dx", ConfigurationConstants.DB_FALSE);

                    if (r.getAttribute("Sx") != null && (Boolean) r.getAttribute("Sx"))
                        r1.setAttribute("Sx", ConfigurationConstants.DB_TRUE);
                    else
                        r1.setAttribute("Sx", ConfigurationConstants.DB_FALSE);
                } else //non c'e' nessun check, la riga va cancellata
                {
                    if (r1 != null)
                        r1.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (iter != null)
                iter.closeRowSetIterator();
        }

    }


    //  /**
    //   * Metodo che riporta sul database le scelte dell'utente
    //   * @param ctx
    //   */
    //  public static void updateDB1livPF()
    //  {
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    ViewObject ref=am.findViewObject("Ref_SoRefertopfasView1");
    //    //il primo e' quello dipendente, cioe' quello corrente
    //    Row r=ref.first();
    //    Integer idreferto=(Integer)r.getAttribute("Idreferto");
    //    String ulss=(String)r.getAttribute("Ulss");
    //    String tpscr=(String)r.getAttribute("Tpscr");
    //
    //    //configurazione NON dipendente dai referti su cui sto lavorando
    //    ViewObject cnf=am.findViewObject("Ref_SoCodref1livpfView1");
    //    ViewObject vo=am.findViewObject("Ref_SoCnfEsamiView1");
    //
    //    RowSetIterator iter=null;
    //    try
    //    {
    //      iter=ViewHelper.getRowSetIterator(vo, "esami_iter");
    //      //per ogni dato del vo
    //      while(iter.hasNext())
    //      {
    //        r=iter.next();
    //        //cerco se c'e' un record corrispondente nelle configurazioni
    //        String where="IDCNFREF="+r.getAttribute("Idcnfref")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI+
    //          "' AND IDREFERTO="+idreferto;
    //          cnf.setWhereClause(where);
    //
    //          cnf.executeQuery();
    //
    //          Row r1=cnf.first();
    //
    //
    //        //se c'e' almeno un valore
    //        if(r.getAttribute("Valore") != null)
    //        {
    //          //se non c'e' la riga la creo
    //          if(r1==null)
    //          {
    //            r1=cnf.createRow();
    //            cnf.insertRow(r1);
    //            r1.setAttribute("Id",am.getNextIdCnfRefPF());
    //            r1.setAttribute("Idreferto",idreferto);
    //            r1.setAttribute("Gruppo",ConfigurationConstants.NOME_GRUPPO_ESAMI);
    //            r1.setAttribute("Idcnfref",r.getAttribute("Idcnfref"));
    //            r1.setAttribute("Ulss",ulss);
    //            r1.setAttribute("Tpscr",tpscr);
    //          }
    //          //aggiorno
    //          r1.setAttribute("Valore",r.getAttribute("Valore"));
    //          r1.setAttribute("Anormalita",r.getAttribute("Anormalita"));
    //        }
    //        else //non c'e' nessun valore, la riga va cancellata
    //        {
    //          if(r1!=null)
    //            r1.remove();
    //        }
    //      }
    //    }
    //    catch(Throwable ex)
    //    {
    //      ex.printStackTrace();
    //
    //    }
    //    finally
    //    {
    //      if(iter!=null)
    //        iter.closeRowSetIterator();
    //    }
    //
    //  }
    //
    //  /**
    //   * Metodo che riporta sul database le scelte dell'utente
    //   * @param ctx
    //   */
    //  public static void updateDB1livEsamiPfas()
    //  {
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    ViewObject ref=am.findViewObject("Ref_SoRefertopfasView1");
    //    //il primo e' quello dipendente, cioe' quello corrente
    //    Row r=ref.first();
    //    Integer idreferto=(Integer)r.getAttribute("Idreferto");
    //    String ulss=(String)r.getAttribute("Ulss");
    //    String tpscr=(String)r.getAttribute("Tpscr");
    //
    //    //configurazione NON dipendente dai referti su cui sto lavorando
    //    ViewObject cnf=am.findViewObject("Ref_SoCodref1livpfView2");
    //    ViewObject vo=am.findViewObject("Ref_SoCnfEsamiView2");
    //
    //    RowSetIterator iter=null;
    //    try
    //    {
    //      iter=ViewHelper.getRowSetIterator(vo, "esami_iter");
    //      //per ogni dato del vo
    //      while(iter.hasNext())
    //      {
    //        r=iter.next();
    //        //cerco se c'e' un record corrispondente nelle configurazioni
    //        String where="IDCNFREF="+r.getAttribute("Idcnfref")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS+
    //          "' AND IDREFERTO="+idreferto;
    //          cnf.setWhereClause(where);
    //
    //          cnf.executeQuery();
    //
    //          Row r1=cnf.first();
    //
    //
    //        //se c'e' almeno un valore
    //        if(r.getAttribute("Valore") != null)
    //        {
    //          //se non c'e' la riga la creo
    //          if(r1==null)
    //          {
    //            r1=cnf.createRow();
    //            cnf.insertRow(r1);
    //            r1.setAttribute("Id",am.getNextIdCnfRefPF());
    //            r1.setAttribute("Idreferto",idreferto);
    //            r1.setAttribute("Gruppo",ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS);
    //            r1.setAttribute("Idcnfref",r.getAttribute("Idcnfref"));
    //            r1.setAttribute("Ulss",ulss);
    //            r1.setAttribute("Tpscr",tpscr);
    //          }
    //          //aggiorno
    //          r1.setAttribute("Valore",r.getAttribute("Valore"));
    //          r1.setAttribute("Anormalita",r.getAttribute("Anormalita"));
    //        }
    //        else //non c'e' nessun valore, la riga va cancellata
    //        {
    //          if(r1!=null)
    //            r1.remove();
    //        }
    //      }
    //    }
    //    catch(Throwable ex)
    //    {
    //      ex.printStackTrace();
    //
    //    }
    //    finally
    //    {
    //      if(iter!=null)
    //        iter.closeRowSetIterator();
    //    }
    //
    //  }
    //
    //  /**
    //   * Metodo che riporta sul database le scelte dell'utente
    //   * @param ctx
    //   */
    //  public static void updateDB2livEsamiPfas()
    //  {
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    ViewObject ref=am.findViewObject("Ref_SoProcedurapfasView1");
    //    //il primo e' quello dipendente, cioe' quello corrente
    //    Row r=ref.getCurrentRow();
    //    Integer idreferto=(Integer)r.getAttribute("Idproc");
    //    Integer tipoProcedura=(Integer)r.getAttribute("Tpprocedura");
    //    String ulss=(String)r.getAttribute("Ulss");
    //    String tpscr=(String)r.getAttribute("Tpscr");
    //
    //    //configurazione NON dipendente dai referti su cui sto lavorando
    //    ViewObject cnf=am.findViewObject("Ref_SoCodref2livpfView1");
    //    ViewObject vo=am.findViewObject("Ref_SoCnfEsamiView3");
    //
    //    RowSetIterator iter=null;
    //    try
    //    {
    //      iter=ViewHelper.getRowSetIterator(vo, "esami_iter");
    //      //per ogni dato del vo
    //      while(iter.hasNext())
    //      {
    //        r=iter.next();
    //        //cerco se c'e' un record corrispondente nelle configurazioni
    //        String where="IDCNFREF="+r.getAttribute("Idcnfref")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L+
    //          "' AND IDPROC="+idreferto;
    //          cnf.setWhereClause(where);
    //
    //          cnf.executeQuery();
    //
    //          Row r1=cnf.first();
    //
    //
    //        //se c'e' almeno un valore
    //        if(r.getAttribute("Valore") != null)
    //        {
    //          //se non c'e' la riga la creo
    //          if(r1==null)
    //          {
    //            r1=cnf.createRow();
    //            cnf.insertRow(r1);
    //            r1.setAttribute("Id",am.getNextIdCnfRef2livPF());
    //            r1.setAttribute("Idproc",idreferto);
    //            r1.setAttribute("Gruppo",ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L);
    //            r1.setAttribute("Idcnfref",r.getAttribute("Idcnfref"));
    //            r1.setAttribute("Ulss",ulss);
    //            r1.setAttribute("Tpscr",tpscr);
    //            r1.setAttribute("TipoProcedura",tipoProcedura);
    //            r1.setAttribute("GrTipo",ConfigurationConstants.NOME_GRUPPO_TIPO_PROCEDURA);
    //          }
    //          //aggiorno
    //          r1.setAttribute("Valore",r.getAttribute("Valore"));
    //          r1.setAttribute("Anormalita",r.getAttribute("Anormalita"));
    //        }
    //        else //non c'e' nessun valore, la riga va cancellata
    //        {
    //          if(r1!=null)
    //            r1.remove();
    //        }
    //      }
    //    }
    //    catch(Throwable ex)
    //    {
    //      ex.printStackTrace();
    //
    //    }
    //    finally
    //    {
    //      if(iter!=null)
    //        iter.closeRowSetIterator();
    //    }
    //
    //  }
    //
    //  /**
    //   * Metodo che riporta sul database le scelte dell'utente
    //   * @param ctx
    //   */
    //  public static void updateDB2livScambioPf()
    //  {
    //    RefPf_AppModule am=(RefPf_AppModule)BindingContext.getCurrent().findDataControl("RefPf_AppModuleDataControl").getDataProvider();
    //    ViewObject ref=am.findViewObject("Ref_SoProcedurapfasView1");
    //    //il primo e' quello dipendente, cioe' quello corrente
    //    Row r=ref.getCurrentRow();
    //    Integer idreferto=(Integer)r.getAttribute("Idproc");
    //    Integer tipoProcedura=(Integer)r.getAttribute("Tpprocedura");
    //    String ulss=(String)r.getAttribute("Ulss");
    //    String tpscr=(String)r.getAttribute("Tpscr");
    //
    //    //configurazione NON dipendente dai referti su cui sto lavorando
    //    ViewObject cnf=am.findViewObject("Ref_SoCodref2livpfView1");
    //    ViewObject vo=am.findViewObject("Ref_SoCnfEsamiView4"); //esami pre procedura
    //
    //    RowSetIterator iter=null;
    //    try
    //    {
    //      iter=ViewHelper.getRowSetIterator(vo, "esami_pre_iter");
    //      //per ogni dato del vo
    //      while(iter.hasNext())
    //      {
    //        r=iter.next();
    //        //cerco se c'e' un record corrispondente nelle configurazioni
    //        String where="IDCNFREF="+r.getAttribute("Idcnfref")+
    //          " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L+
    //          "' AND IDPROC="+idreferto+" AND TIPO_PROCEDURA ="+tipoProcedura;
    //          cnf.setWhereClause(where);
    //
    //          cnf.executeQuery();
    //
    //          Row r1=cnf.first();
    //
    //
    //        //se c'e' almeno un valore
    //        if(r.getAttribute("Valore") != null)
    //        {
    //          //se non c'e' la riga la creo
    //          if(r1==null)
    //          {
    //            r1=cnf.createRow();
    //            cnf.insertRow(r1);
    //            r1.setAttribute("Id",am.getNextIdCnfRef2livPF());
    //            r1.setAttribute("Idproc",idreferto);
    //            r1.setAttribute("Gruppo",ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L);
    //            r1.setAttribute("Idcnfref",r.getAttribute("Idcnfref"));
    //            r1.setAttribute("Ulss",ulss);
    //            r1.setAttribute("Tpscr",tpscr);
    //            r1.setAttribute("TipoProcedura",tipoProcedura);
    //            r1.setAttribute("GrTipo",ConfigurationConstants.NOME_GRUPPO_TIPO_SCAMBIO);
    //          }
    //          //aggiorno
    //          r1.setAttribute("Valore",r.getAttribute("Valore"));
    //          r1.setAttribute("Anormalita",r.getAttribute("Anormalita"));
    //        }
    //        else //non c'e' nessun valore, la riga va cancellata
    //        {
    //          if(r1!=null)
    //            r1.remove();
    //        }
    //      }
    //
    //      if (tipoProcedura.intValue() != 99){
    //        vo=am.findViewObject("Ref_SoCnfEsamiView5"); //esami POST procedura
    //        tipoProcedura = tipoProcedura.add(3);
    //        iter=ViewHelper.getRowSetIterator(vo, "esami_post_iter");
    //        //per ogni dato del vo
    //        while(iter.hasNext())
    //        {
    //          r=iter.next();
    //          //cerco se c'e' un record corrispondente nelle configurazioni
    //          String where="IDCNFREF="+r.getAttribute("Idcnfref")+
    //            " AND GRUPPO='"+ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L+
    //            "' AND IDPROC="+idreferto+" AND TIPO_PROCEDURA ="+tipoProcedura;
    //            cnf.setWhereClause(where);
    //
    //            cnf.executeQuery();
    //
    //            Row r1=cnf.first();
    //
    //            //se c'e' almeno un valore
    //            if(r.getAttribute("Valore") != null)
    //            {
    //              //se non c'e' la riga la creo
    //              if(r1==null)
    //              {
    //                r1=cnf.createRow();
    //                cnf.insertRow(r1);
    //                r1.setAttribute("Id",am.getNextIdCnfRef2livPF());
    //                r1.setAttribute("Idproc",idreferto);
    //                r1.setAttribute("Gruppo",ConfigurationConstants.NOME_GRUPPO_ESAMI_PFAS_2L);
    //                r1.setAttribute("Idcnfref",r.getAttribute("Idcnfref"));
    //                r1.setAttribute("Ulss",ulss);
    //                r1.setAttribute("Tpscr",tpscr);
    //                r1.setAttribute("TipoProcedura",tipoProcedura);
    //                r1.setAttribute("GrTipo",ConfigurationConstants.NOME_GRUPPO_TIPO_SCAMBIO);
    //              }
    //              //aggiorno
    //              r1.setAttribute("Valore",r.getAttribute("Valore"));
    //              r1.setAttribute("Anormalita",r.getAttribute("Anormalita"));
    //            }
    //            else //non c'e' nessun valore, la riga va cancellata
    //            {
    //              if(r1!=null)
    //                r1.remove();
    //            }
    //        }
    //      }
    //    }
    //    catch(Throwable ex)
    //    {
    //      ex.printStackTrace();
    //
    //    }
    //    finally
    //    {
    //      if(iter!=null)
    //        iter.closeRowSetIterator();
    //    }
    //
    //  }
}
