package view.backing;

import model.commons.AccessManager;
import model.commons.ConfigurationConstants;

public class ConstantsBean {
    private String ulss_regionale=AccessManager.CODREGIONALE;

      private static Integer cod_adepre_adeguato=ConfigurationConstants.CODICE_ADEPRE_ADEGUATO;
      private static Integer cod_adepre_inadeguato=ConfigurationConstants.CODICE_ADEPRE_INADEGUATO;;
      private static Integer cod_adepre_limitato=ConfigurationConstants.CODICE_ADEPRE_LIMITATO;
      private static Integer cod_giudia_positivo=ConfigurationConstants.CODICE_GIUDIA_POSITIVO;
      private static Integer cod_giudia_modrea=ConfigurationConstants.CODICE_GIUDIA_MODREATTIVE;
      private static Integer cod_giudia_positivo_2l=ConfigurationConstants.CODICE_GIUDIA2L_POSITIVO;
    //  private static Integer cod_giudia_modrea_2l=ConfigurationConstants.CODICE_GIUDIA2L_MODREA;
      private static Integer cod_colpvl_cervice=ConfigurationConstants.CODICE_COLPVL_CERVICE;
      private static Integer cod_colpvl_non_soddisf=ConfigurationConstants.CODICE_COLPVL_NON_SODDISF;
      private static Integer cod_colpvl_non_es_tecnico=ConfigurationConstants.CODICE_COLPVL_NON_ES_TECNICO;
      private static Integer cod_colpvl_non_es_flogistico=ConfigurationConstants.CODICE_COLPVL_NON_ES_FLOGISTICO;
      private static Integer codice_intval_non_ex=ConfigurationConstants.CODICE_INTVAL_NON_EX;
      private static Integer cod_motivo_altro=ConfigurationConstants.CODICE_MOTIVO_ALTRO.intValue();
      private static Integer cod_compl_altro=ConfigurationConstants.CODICE_COMPLIC_ALTRO;
      private static Integer cod_compl_int_altro=ConfigurationConstants.CODICE_COMPLIC_INT_ALTRO;
      private static Integer cod_intmie_altro=ConfigurationConstants.CODICE_INTMIE_ALTRO;
      private static Integer cod_lesioni_altro=ConfigurationConstants.CODICE_LESIONI_ALTRO;
      private static Integer cod_esitoma_sospetto=new Integer(ConfigurationConstants.CODICE_ESITO_SOSPETTO.intValue());
      private static Integer cod_non_exe_altro=new Integer(ConfigurationConstants.CODICE_NON_EXEC_ALTRO.intValue());
      private static Integer cod_altra_tecnica=ConfigurationConstants.CODICE_ALTRA_TECNICA;
      private static Integer cod_altra_diagnosi=ConfigurationConstants.CODICE_DIAGNOSI_ALTRO;
      

      public ConstantsBean()
      {
      }

      

      public Integer getCod_adepre_adeguato()
      {
        return cod_adepre_adeguato;
      }

      public void setCod_adepre_adeguato(Integer cod_adepre_adeguato)
      {
        this.cod_adepre_adeguato = cod_adepre_adeguato;
      }

      public Integer getCod_adepre_inadeguato()
      {
        return cod_adepre_inadeguato;
      }

      public void setCod_adepre_inadeguato(Integer cod_adepre_inadeguato)
      {
        this.cod_adepre_inadeguato = cod_adepre_inadeguato;
      }

      public Integer getCod_adepre_limitato()
      {
        return cod_adepre_limitato;
      }

      public void setCod_adepre_limitato(Integer cod_adepre_limitato)
      {
        this.cod_adepre_limitato = cod_adepre_limitato;
      }

      public Integer getCod_giudia_positivo()
      {
        return cod_giudia_positivo;
      }

      public void setCod_giudia_positivo(Integer cod_giudia_positivo)
      {
        this.cod_giudia_positivo = cod_giudia_positivo;
      }

      public Integer getCod_giudia_modrea()
      {
        return cod_giudia_modrea;
      }

      public void setCod_giudia_modrea(Integer cod_giudia_modrea)
      {
        this.cod_giudia_modrea = cod_giudia_modrea;
      }

    /*  public Integer getCod_giudia_modrea_2l()
      {
        return cod_giudia_modrea_2l;
      }

      public void setCod_giudia_modrea_2l(Integer cod_giudia_modrea_2l)
      {
        this.cod_giudia_modrea_2l = cod_giudia_modrea_2l;
      }*/

      public Integer getCod_giudia_positivo_2l()
      {
        return cod_giudia_positivo_2l;
      }

      public void setCod_giudia_positivo_2l(Integer cod_giudia_positivo_2l)
      {
        this.cod_giudia_positivo_2l = cod_giudia_positivo_2l;
      }

      public Integer getCod_colpvl_cervice()
      {
        return cod_colpvl_cervice;
      }

      public void setCod_colpvl_cervice(Integer cod_colpvl_cervice)
      {
        this.cod_colpvl_cervice = cod_colpvl_cervice;
      }

      public Integer getCod_colpvl_non_soddisf()
      {
        return cod_colpvl_non_soddisf;
      }

      public void setCod_colpvl_non_soddisf(Integer cod_colpvl_non_soddisf)
      {
        this.cod_colpvl_non_soddisf = cod_colpvl_non_soddisf;
      }

      public Integer getCod_colpvl_non_es_tecnico()
      {
        return cod_colpvl_non_es_tecnico;
      }

      public void setCod_colpvl_non_es_tecnico(Integer cod_colpvl_non_es_tecnico)
      {
        this.cod_colpvl_non_es_tecnico = cod_colpvl_non_es_tecnico;
      }

      public Integer getCod_colpvl_non_es_flogistico()
      {
        return cod_colpvl_non_es_flogistico;
      }

      public void setCod_colpvl_non_es_flogistico(Integer cod_colpvl_non_es_flogistico)
      {
        this.cod_colpvl_non_es_flogistico = cod_colpvl_non_es_flogistico;
      }

      public Integer getCodice_intval_non_ex()
      {
        return codice_intval_non_ex;
      }

      public void setCodice_intval_non_ex(Integer codice_intval_non_ex)
      {
        this.codice_intval_non_ex = codice_intval_non_ex;
      }

      public String getUlss_regionale()
      {
        return ulss_regionale;
      }

      public void setUlss_regionale(String ulss_regionale)
      {
        this.ulss_regionale = ulss_regionale;
      }

      public Integer getCod_motivo_altro()
      {
        return cod_motivo_altro;
      }

      public void setCod_motivo_altro(Integer cod_motivo_altro)
      {
        this.cod_motivo_altro = cod_motivo_altro;
      }

      public Integer getCod_compl_altro()
      {
        return cod_compl_altro;
      }

      public void setCod_compl_altro(Integer cod_compl_altro)
      {
        this.cod_compl_altro = cod_compl_altro;
      }

      public Integer getCod_compl_int_altro()
      {
        return cod_compl_int_altro;
      }

      public void setCod_compl_int_altro(Integer cod_compl_int_altro)
      {
        this.cod_compl_int_altro = cod_compl_int_altro;
      }

      public Integer getCod_intmie_altro()
      {
        return cod_intmie_altro;
      }

      public void setCod_intmie_altro(Integer cod_intmie_altro)
      {
        this.cod_intmie_altro = cod_intmie_altro;
      }

      public Integer getCod_lesioni_altro()
      {
        return cod_lesioni_altro;
      }

      public void setCod_lesioni_altro(Integer cod_lesioni_altro)
      {
        this.cod_lesioni_altro = cod_lesioni_altro;
      }

      public Integer getCod_esitoma_sospetto()
      {
        return cod_esitoma_sospetto;
      }

      public void setCod_esitoma_sospetto(Integer cod_esitoma_sospetto)
      {
        this.cod_esitoma_sospetto = cod_esitoma_sospetto;
      }

      public Integer getCod_non_exe_altro()
      {
        return cod_non_exe_altro;
      }

      public void setCod_non_exe_altro(Integer cod_non_exe_altro)
      {
        this.cod_non_exe_altro = cod_non_exe_altro;
      }

      public Integer getCod_altra_tecnica()
      {
        return cod_altra_tecnica;
      }

      public void setCod_altra_tecnica(Integer cod_altra_tecnica)
      {
        this.cod_altra_tecnica = cod_altra_tecnica;
      }

      public Integer getCod_altra_diagnosi()
      {
        return cod_altra_diagnosi;
      }

      public void setCod_altra_diagnosi(Integer cod_altra_diagnosi)
      {
        this.cod_altra_diagnosi = cod_altra_diagnosi;
      }
}
