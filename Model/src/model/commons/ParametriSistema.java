package model.commons;

import insiel.dataHandling.BlobUtils;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import model.A_AppModuleImpl;

import model.common.A_AppModule;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;
import oracle.jbo.domain.BlobDomain;


public class ParametriSistema {
    
    public ParametriSistema()
      {
      }
      

      
      public static void init()
      {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map sess = adfCtx.getSessionScope();
        
        String ulss = (String) sess.get("ulss");
        String scr = (String) sess.get("scr");
        
          BindingContext ctx = BindingContext.getCurrent();
          A_AppModuleImpl am = (A_AppModuleImpl)ctx.findDataControl("A_AppModuleDataControl").getDataProvider();
          ViewObject voParam = am.getA_SoCnfParametriSistemaView1();
        
          try
          {
            String idmxauto = getParamValue(voParam,ulss,scr,"idmx_auto");
            boolean idMxAuto = ("S".equals(idmxauto));
            sess.put("idmxAuto",Boolean.valueOf(idMxAuto));
          }
          catch (Exception ex)
          {
            sess.put("idmxAuto",Boolean.valueOf(false));
          }
          
          //verifico se l'azienda lavora con centri fisici oppure no
         try
          {
            String cnf_rich = getParamValue(voParam,ulss,scr,"modalita_centri");
            boolean b = ("S".equals(cnf_rich));
            sess.put("modalita_centri",Boolean.valueOf(b));
          }
          catch (Exception ex)
          {
            sess.put("modalita_centri",Boolean.valueOf(false));
          }
          
          //verifico se l'azienda lavora con centri preconfigurati per i richiami oppure no
         try
          {
            String cnf_rich = getParamValue(voParam,ulss,scr,"centri_richiamo_cnf");
            boolean b = ("S".equals(cnf_rich));
            sess.put("centri_rich_cnf",Boolean.valueOf(b));
          }
          catch (Exception ex)
          {
            sess.put("centri_rich_cnf",Boolean.valueOf(false));
          }
          
          //verifico se l'azienda lavora con test HPV oppure no
         try
          {
            String hpv = getParamValue(voParam,ulss,scr,"hpv");
            boolean b = ("S".equals(hpv));
            sess.put("HPV",Boolean.valueOf(b));
          }
          catch (Exception ex)
          {
            sess.put("HPV",Boolean.valueOf(false));
          }
          
          //memorizzo se genero io il codice campione oppure no
          
         try
          {
            String hpv = getParamValue(voParam,ulss,scr,"prod_codice_campione");
            boolean b = ("S".equals(hpv));
            sess.put("campione",Boolean.valueOf(b));
          }
          catch (Exception ex)
          {
            sess.put("campione",Boolean.valueOf(false));
          }
           //memorizzo se genero io il codice richiesta oppure no
           try
          {
            String hpv = getParamValue(voParam,ulss,scr,"prod_codice_richiesta");
            boolean b = ("S".equals(hpv));
            sess.put("richiesta",Boolean.valueOf(b));
          }
          catch (Exception ex)
          {
            sess.put("richiesta",Boolean.valueOf(false));
          }
           
          //parametro che abilita lo spostamento di un invito da un centro all'altro
        try {
            String sposta_centro = getParamValue(voParam, ulss, scr, "sposta_centro_invito");
            boolean b = ("N".equals(sposta_centro)); //di default lo spostamento è permesso
            sess.put("sposta_centro_invito", Boolean.valueOf(!b));
        } catch (Exception ex) {
            sess.put("sposta_centro_invito", Boolean.valueOf(true));
        }
          
           //verifico se il calcolo dell'indirizzo ï¿½ standard o semplificato
         try
          {
            String calcolo_indirizzo = getParamValue(voParam,ulss,scr,"mod_spedizione");
            if (calcolo_indirizzo != null)
              sess.put("mod_spedizione",calcolo_indirizzo);
          }
          catch (Exception ex)
          {
            sess.put("mod_spedizione","standard");
          }
          //verifico se l'azienda utilizza la modalita doppio cieco
          try
          {
            String doppio_cieco = getParamValue(voParam,ulss,scr,"doppio_cieco");
            boolean b = ("S".equals(doppio_cieco));
            sess.put("doppio_cieco",Boolean.valueOf(b));
          }
          catch (Exception ex)
          {
            sess.put("doppio_cieco",Boolean.valueOf(false));
          }
          //verifico se l'azienda aderisce al progetto 45MX
          try {
            String mx45 = getParamValue(voParam, ulss, scr, "mx45");
            boolean b = ("S".equals(mx45));
            sess.put("MX45", Boolean.valueOf(b));
          } catch (Exception ex) {
            sess.put("MX45", Boolean.valueOf(false));
          }

          //verifico se l'azienda aderisce al trial
          try {
            String trial = getParamValue(voParam, ulss, scr, "trial");
            sess.put("trial", Integer.valueOf(trial));
          } catch (Exception ex) {
          }

          //verifico se l'azienda utilizza la modalita rettosigmoidoscopia
          try {
            String rss = getParamValue(voParam, ulss, scr, "RSS");
            boolean b = ("S".equals(rss));
            sess.put("RSS", Boolean.valueOf(b));
          } catch (Exception ex) {
            sess.put("RSS", Boolean.valueOf(false));
          }

          //verifico se l'azienda ha la funzionalità portale
          try {
            String otpMode = getParamValue(voParam, ulss, scr, "otp");
            boolean b = ("S".equals(otpMode));
            sess.put("otp", Boolean.valueOf(b));
          } catch (Exception ex) {
            sess.put("otp", Boolean.valueOf(false));
          }
          
          //verifico se la categoria popolazione è obbligatoria
          try {
            String classeObbl = getParamValue(voParam, ulss, scr, "classepop");
            boolean b = ("S".equals(classeObbl));
            sess.put("classepop", Boolean.valueOf(b));
          } catch (Exception ex) {
            sess.put("classepop", Boolean.valueOf(false));
          }
          
          //verifica se ha altri inviti nello stesso round
          try {
            String ignoraRound = getParamValue(voParam, ulss, scr, "ignora_round_comune");
            boolean b = ("S".equals(ignoraRound));
            sess.put("ignora_round", Boolean.valueOf(b));
          } catch (Exception ex) {
            sess.put("ignora_round", Boolean.valueOf(false));
          }
      }
      
       public static String getParamValue(ViewObject voParam,
                                            String ulss, 
                                          String tpscr,
                                          String paramName)
        throws Exception
      {
        
        voParam.setWhereClause("ULSS='"+ulss+"' AND TPSCR='"+tpscr+
        "' AND UPPER(NOME_PARAM)=UPPER('"+paramName+"')");
        voParam.executeQuery();
        Row r=voParam.first();
        if(r==null)
          throw new Exception("Non ï¿½ stato trovato nessun parametro con il nome "+paramName);
        return (String)r.getAttribute("ValoreParam");
      
      }
      
      /**
       * legge i parametri da passare ai template per una ulss ed un tipo di screening
       * @throws java.lang.Exception
       * @param params_vo
       * @param ulss_vo
       * @param tpscr
       * @param ulss
       */
      public static HashMap readUlssParams(String ulss,String tpscr, 
                      ViewObject ulss_vo, ViewObject params_vo) 
      {
        HashMap ulssParams=new HashMap();
        if(ulss==null)
          return ulssParams;
        //prima di tutto il logo: 
        ulss_vo.setWhereClause("CODAZ='"+ulss+"'");
        ulss_vo.executeQuery();
        BlobDomain blob=(BlobDomain)ulss_vo.first().getAttribute("Logo");
        if(blob!=null){
          File logo=null;
          try{
          logo=BlobUtils.getFileFromBlob(blob,"logo");
          if(logo!=null){
            ulssParams.put("logo",logo);
          }
          }catch(IOException iex)
          {
            iex.printStackTrace();
          }
          
        }
          
        
        //poi gli altri parametri
        params_vo.setWhereClause("CODAZ='"+ulss+"' AND TPSCR='"+tpscr+"'");
        params_vo.executeQuery();
        Row par=params_vo.first();
        while(par!=null)
        {
          
          ulssParams.put(par.getAttribute("Nomepar"),par.getAttribute("Descrpar"));
          par=params_vo.next();
        }
        
        return ulssParams;
        
      }
      
      /*public static HashMap readUlssParams()
      {
       

       
    
        return readUlssParams((String)ctx.getHttpServletRequest().getSession().getAttribute("ulss"),
                                  (String)ctx.getHttpServletRequest().getSession().getAttribute("scr"),
                                  am.findViewObject("A_SoAziendaView1"),
                                  am.findViewObject("A_SoCnfPartemplateView1"));
      
      }*/
      
      public static HashMap readUlssParams(String ulss, String tpscr)
      {
        A_AppModule am = (A_AppModule)Configuration.createRootApplicationModule("model.A_AppModule","A_AppModuleLocal");
       try{
         return readUlssParams(ulss,
                                  tpscr,
                                  am.findViewObject("A_SoAziendaView1"),
                                  am.findViewObject("A_SoCnfPartemplateView1"));
       }
       
       finally
       {
         Configuration.releaseRootApplicationModule(am,true);
       }
      }


      public static HashMap getParamTemplate(String ulss,String tpscr, ViewObject ulss_vo, ViewObject params_vo)
      {
        
        return readUlssParams(ulss,tpscr,ulss_vo,params_vo);
      }
      
      /**
       * Serve a cancellare il file col logo dopo il suo utilizzo
       * @param parameters
       */
      public static void releaseLogo(HashMap parameters)
      {
        if(parameters==null) return;
        File f=(File)parameters.get("logo");
        if(f!=null)
          f.delete();
      }
    
}
