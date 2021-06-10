package model.commons;

import java.util.Calendar;
import java.util.Date;

import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

public class ConfigurationConstants 
{

/****************************************************/
/**       DATI REPERIBILI ANCHE DA DB              **/
/****************************************************/
 /*TABELLA: SO_CNF_TPINVITO, codice del primo invito
  */
  public static final String CODICE_PRIMO_INVITO="1 ";
  /*TABELLA: SO_CNF_TPINVITO, codice del primo invito RSS
  */
  public static final String CODICE_PRIMO_INVITO_RSS="1S";
  /*TABELLA: SO_CNF_TPINVITO, codice del richimao dopo esito aboh
  */
  public static final String CODICE_INVITO_ABOH="10";
  /*TABELLA: SO_CNF_TPINVITO, codice dell'accesso spontaneo, 
  * che identifica le volontarie
  */
//  public static final String CODICE_ACCESSO_SPONTANEO="9 ";
  /* TABELLA: SO_CNF_ANAG_REG, codice dei domiciliati
   */
  public static final Integer CODICE_DOMICILIATO=new Integer(3);
  /* CODICE REGIONALE, ULSS FITTIZIA
   */
//@codecoach:disable nextline
  public static final String CODICE_ULSS_REGIONALE="050000";
  /* TABELLA SO_CNF_CATEG_TPINVITO, codice della categoria "Follow-up di 2 livello"
   */
  public static final Integer CODICE_FOLLOWUP2LIV=new Integer(5);
  
  
  /* TABELLA SO_CNF_CATEG_TPINVITO, codice della categoria "Primo invito"
   */
  public static final Integer CODICE_CAT_PRIMO_INVITO=new Integer(1);
   /* TABELLA SO_CNF_CATEG_TPINVITO, codice della categoria "sollecito"
   */
  public static final Integer CODICE_CAT_SOLLECITO=new Integer(2);
  /* TABELLA SO_CNF_CATEG_TPINVITO, codice della categoria "sollecito"
   */
  public static final Integer CODICE_CAT_VOLONTARIO=new Integer(6);
   /* TABELLA SO_CNF_CATEG_TPINVITO, codice della categoria "VOLONTARIA"
   */
  public static final Integer CODICE_CAT_RICHIAMO1LIV=new Integer(3);
  
  
  /*tabella: so_cnf_esito, codice dell'esito di invito "Dato non disponibile"
   */

  public static final String CODICE_ESITO_NON_DISPONIBILE="?";
  /*tabella: so_cnf_esito, codice dell'esito di invito "Mancata presenza"
   */

  public static final String CODICE_ESITO_MANCATA_PRESENZA="N";
  /*tabella: so_cnf_esito, codice dell'esito di invito "Mancata presenza RSS"
   */

  public static final String CODICE_ESITO_MANCATA_PRESENZA_RSS="G";
  /*tabella: so_cnf_esito, codice dell'esito di invito "Esame eseguito"
   */

  public static final String CODICE_ESITO_ESAME_ESEGUITO="S";
  /*tabella: so_cnf_esito, codice dell'esito di invito "pap test recente documentato"
   */
  public static final String CODICE_ESITO_ESAME_RECENTE_DOC="D";
   public static final String CODICE_ESITO_ESAME_RECENTE_NON_DOC="E";
   
   /* 20110404 Serra: esame recente di 2 livello
    * */
  public static final String CODICE_ESITO_ESAME_RECENTE_2LIV="A";
  
  /*tabella: so_cnf_esito, codice dell'esito di invito "indirizzo sbagliato"(1LIV)
   */
  public static final String CODICE_ESITO_IND_SBAGLIATO_1="W";
   /*tabella: so_cnf_esito, codice dell'esito di invito "NESSUN CONTATTO" (2LIV)
   */
  public static final String CODICE_ESITO_NESSUN_CONT_2="D";
   
  /*TIPI DI ESITO PER CUI POSSO INSERIRE UN REFERTO
   */
  public static final String[] CODICI_ESITI_REFERTABILI=
  {CODICE_ESITO_NON_DISPONIBILE,
  CODICE_ESITO_ESAME_ESEGUITO};
  
  public static final int ETA_PRIMO_INV_RSS = 60;
  
  /* ESITI DEL COLON RETTO */
  //FOBT negativo documentato
  public static final String CODICE_ESITO_KIT_RITIRATO="Q";
  public static final String CODICE_ESITO_FOBT_DOC_CO="D";
  public static final String CODICE_ESITO_COLPO_DOC_CO="C";
  public static final String[] CODICI_ESITI_REFERTABILI_CO=
  {CODICE_ESITO_NON_DISPONIBILE,
  CODICE_ESITO_ESAME_ESEGUITO,
  CODICE_ESITO_KIT_RITIRATO};
  
   public static final String CODICE_ESITO_COLONSCOPIA_RECENTE_DOC="C";

   public static final String CODICE_ESITO_SANGUEOCC_RECENTE_DOC="D";

   public static final String CODICE_ESITO_CONTENITORE_RITIRATO="Q";
   
   public static final Integer CODICE_PRELIEVO_HPV_NON_ESEGUITO=new Integer(1);
   public static final Integer CODICE_PRELIEVO_HPV_ESEGUITO=new Integer(2);
   public static final Integer CODICE_PRELIEVO_HPV_ND=new Integer(0);
  
// OPERATORI MEDICI CITOLOGICO
  /*tabella: so_cnf_tpopmedico, codice del CITOSCREENER
   */
  public static final Integer CODICE_CITOSCREENER=new Integer(2);
  /*tabella: so_cnf_tpopmedico, codice del PRELEVATORE
   */
  public static final Integer CODICE_OSTETRICA=new Integer(5);
   /*tabella: so_cnf_tpopmedico, codice del MEDICO
   */
   /*tabella: so_cnf_tpopmedico, codice del PATOLOGO
   */
  public static final Integer CODICE_PATOLOGO=new Integer(6);
  
  /*tabella: so_cnf_tpopmedico, codice del ginecologo
   */
  public static final Integer CODICE_GINECOLOGO=new Integer(4);
  
  
  // OPERATORI MEDICI COLON-RETTO
  /*tabella: so_cnf_tpopmedico, codice del LETTORE DEL COLON
   */
  public static final Integer CODICE_LETTORE_CO=new Integer(8);
  /* tabella: so_cnf_tpopmedico, codice del GASTROENTEROLOGO
   **/ 
  public static final Integer CODICE_GASTROENTEROLOGO=new Integer(9);
  /* tabella: so_cnf_tpopmedico, codice del RADIOLOGO
   **/ 
  public static final Integer CODICE_RADIOLOGO=new Integer(7);
  /*tabella: so_cnf_tpopmedico, codice del CHIRURGO
   **/ 
    public static final Integer CODICE_CHIRURGO=new Integer(3);
  
  /*MAMMOGRAFICO*/
  /* SENOLOGO*/
  public static final Integer CODICE_SENOLOGO=new Integer(10);
  
  /* tutti gli screening */
  /* oncologo */
  public static final Integer CODICE_ONCOLOGO=new Integer(12);
  
  /* RILEVATORE QUESTIONARIO */
  public static final Integer CODICE_RILEVATORE=new Integer(13);
  
  /*tabella: so_cnf_REF1LIV, nomi dei gruppi
   */
//@codecoach:disable nextline
  public static final String NOME_GRUPPO_ADEPRE="ADEPRE";
//@codecoach:disable nextline
  public static final String NOME_GRUPPO_POSITI="POSITI";
//@codecoach:disable nextline
  public static final String NOME_GRUPPO_INALIM="INALIM";
//@codecoach:disable nextline
  public static final String NOME_GRUPPO_GIUDIA="GIUDIA";
//@codecoach:disable nextline
  public static final String NOME_GRUPPO_MODREA="MODREA";
  public static final String NOME_GRUPPO_ESITO_HPV="HPVES1";
  public static final String NOME_GRUPPO_TIPI_VIRUS="HPVTP1";
  public static final String NOME_GRUPPO_ESAMI="ESA1LI";
  
  /*tabella: so_cnf_REF1LIV, valori di default
   */
  public static final Integer CODICE_ADEPRE_DEFAULT=new Integer(0);
  public static final Integer CODICE_ADEPRE_ADEGUATO=new Integer(1);
  public static final Integer CODICE_ADEPRE_INADEGUATO=new Integer(2);
  public static final Integer CODICE_ADEPRE_LIMITATO=new Integer(3);
  public static final Integer CODICE_ADEPRE_DANNEGGIATO=new Integer(5);
  public static final Integer CODICE_ADEPRE_NON_PERVENUTO=new Integer(4);
  public static final Integer CODICE_GIUDIA_DEFAULT=new Integer(0);
  public static final Integer CODICE_GIUDIA_POSITIVO=new Integer(2);
  public static final Integer CODICE_GIUDIA_MODREATTIVE=new Integer(3);
  public static final Integer CODICE_GIUDIA_NEGATIVO=new Integer(1);
  public static final Integer CODICE_INALIM_TECNICO=new Integer(1);
  public static final Integer CODICE_INALIM_FLOGISTICO=new Integer(2);
  
  
  /*tabella: so_cnf_REF2LIV, nomi dei gruppi
   */
//@codecoach:disable nextline
  public static final String NOME_GRUPPO_BIODIA_2L="BIODIA";
//@codecoach:disable nextline
  public static final String NOME_GRUPPO_COLPES_2L="COLPES";
  public static final String NOME_GRUPPO_CITPOS_2L="CITPOS";
  public static final String NOME_GRUPPO_INTMIE_2L="INTMIE";
  public static final String NOME_GRUPPO_INTTIP_2L="INTTIP";
  public static final String NOME_GRUPPO_ISTDIA_2L="ISTDIA";
  public static final String NOME_GRUPPO_ISTOLM_2L="ISTOLM";
  public static final String NOME_GRUPPO_ISTOPN_2L="ISTOPN";
  public static final String NOME_GRUPPO_ISTOPT_2L="ISTOPT";
  public static final String NOME_GRUPPO_RACDIA_2L="RACDIA";
  public static final String NOME_GRUPPO_COLPVL_2L="COLPVL";
  public static final String NOME_GRUPPO_COLPRE_2L="COLPRE";
  public static final String NOME_GRUPPO_COLPPC_2L="COLPPC";
  public static final String NOME_GRUPPO_COLPPB_2L="COLPPB";
  public static final String NOME_GRUPPO_GIUDIA_2L="GIUDIA";
  public static final String NOME_GRUPPO_MODREA_2L="MODREA";
  public static final String NOME_GRUPPO_ISTGRA_2L="ISTGRA";
  public static final String NOME_GRUPPO_INTVAL_2L="INTVAL";
  public static final String NOME_GRUPPO_HPVES2_2L="HPVES2";
  
  /* colon */
  public static final String NOME_GRUPPO_QUALITA="ENDQLT";
  public static final String NOME_GRUPPO_REGIONE="ENDREG";
  public static final String NOME_GRUPPO_MOTIVO="ENDMOT";
  public static final String NOME_GRUPPO_COMPLICANZE="ENDCOM";
  public static final String NOME_GRUPPO_INDICAZIONI="ENDIND";
  public static final String NOME_GRUPPO_DIAMETRO="ENDDIM";
  public static final String NOME_GRUPPO_DIAGNOSI="ISTOCO";
  public static final String NOME_GRUPPO_ASPETTO="POLASP";
  public static final String NOME_GRUPPO_ISTO_POLIPI="POLIST";
  public static final String NOME_GRUPPO_DISPLASIA="DISPCO";
  public static final String NOME_GRUPPO_ISTO_CANCRO="CARIST";
  public static final String NOME_GRUPPO_LESIONI="ENDLES";
  public static final String NOME_GRUPPO_CONCL_CO="CO2LIV";
  public static final String NOME_GRUPPO_COMPLICANZE_INT="INTCOM";
  public static final String NOME_GRUPPO_ASTLER_COLLER="ISTOAC";
  public static final String NOME_GRUPPO_DUKES="ISTODK";
  public static final String NOME_GRUPPO_STADIO="STADIO";
  public static final String NOME_GRUPPO_GENERICO="GENRCO";
  public static final String NOME_GRUPPO_RXCONC="RXCONC";
  /* mammo*/
  public static final String NOME_GRUPPO_ESITO_RADIO="MXCONS";
  public static final String NOME_GRUPPO_EXEC2L="MXEXEC";
  public static final String NOME_GRUPPO_LOCALIZZ="MXLOCL";
  public static final String NOME_GRUPPO_ESITO_CITO="MXCEST";
  public static final String NOME_GRUPPO_TECNICA_EXEC="MXTEXE";
  public static final String NOME_GRUPPO_ESITO_AGOB="MXAEST";
  public static final String NOME_GRUPPO_INDICAZIONI_MA="MXINDI";
  public static final String NOME_GRUPPO_ESITO_MA="MXEST1";
  public static final String NOME_GRUPPO_TIPO_ESAME="MXESTP";
  public static final String NOME_GRUPPO_ASCELLA="INTASC";
  public static final String NOME_GRUPPO_SEDE_INT="INTSED";
  public static final String NOME_GRUPPO_DENSITA="MXDENS";
  
  /*pfas*/
  public static final String NOME_GRUPPO_CLASSE_ESAMI="CLESAM";
  public static final String NOME_GRUPPO_STILE_VITA="CLQUES";
  public static final String NOME_GRUPPO_ALCOOL="CLALCO"; // range alcool
  public static final String NOME_GRUPPO_BMI="CLABMI"; //range bmi
  public static final String NOME_GRUPPO_SISTOLICA="CLSIST"; // range pressione sistolica
  public static final String NOME_GRUPPO_DIASTOLICA="CLDIAS"; //range pressione diastolica
  public static final String NOME_GRUPPO_ESAMI_PFAS="PFA1LI";
  
  /*tabella: so_cnf_REF2LIV, valori di default
   */
   public static final Integer CODICE_GIUDIA2L_POSITIVO=new Integer(4);
  // public static final Integer CODICE_GIUDIA2L_MODREA=new Integer(5);
   public static final Integer CODICE_RACDIA_DEFAULT=new Integer(0);
    public static final Integer CODICE_COLPES_DEFAULT=new Integer(0);
  
 /*tabella: so_cnf_sugg_1liv, suggeriemnti più usati
   */
  public static final Integer CODICE_SUGG1L_DEFAULT= 0;
  public static final Integer CODICE_SUGG1L_DANNEGGIATO= new Integer(-4);
  public static final Integer CODICE_SUGG1L_NON_PERVENUTO= new Integer(-3);
  public static final Integer CODICE_SUGG1L_INADEGUATO_TECNICO= new Integer(-1);
  public static final Integer CODICE_SUGG1L_INADEGUATO_FLOGISTICO= new Integer(-2);
  
  
  /*tabella: so_cnf_REF2LIV, validità della colposcopia (0=NON ESEGUITA, 1=ESEGUITA)
   */
  public static final Integer CODICE_COLPVL_CERVICE= new Integer(2);
  public static final Integer CODICE_COLPVL_NON_SODDISF= new Integer(3);
  public static final Integer CODICE_COLPVL_NON_ES_TECNICO= new Integer(4);
  public static final Integer CODICE_COLPVL_NON_ES_FLOGISTICO= new Integer(5);
  
  /*tabella: so_cnf_REF2LIV, validità dell'intervento (0=Dato non disponibile)
   */
  public static final Integer CODICE_INTVAL_NON_EX= 1;
  public static final Integer CODICE_INTVAL_EX_QUI= 2;
  public static final Integer CODICE_INTVAL_EX_ALTROVE= 3;
   /*20080712 MOD: aggiunta stadiazione*/
    /*STADIAZIONE*/
  public static final Integer CODICE_STADIO_0_CI=new Integer(0);
  public static final Integer CODICE_STADIO_I_CI=new Integer(1);
  public static final Integer CODICE_STADIO_II_CI=new Integer(2);
  public static final Integer CODICE_STADIO_III_CI=new Integer(3);
  public static final Integer CODICE_STADIO_IVa_CI=new Integer(4);
   public static final Integer CODICE_STADIO_IVb_CI=new Integer(5);
    public static final Integer CODICE_STADIO__CI=new Integer(6);
    /*CITO: ISTOLOGIA CHIRURGICA*/
  /*STADIO M*/
  public static final Integer CODICE_CI_ISTOM_M1=new Integer(2);
  /*STADIO PT*/
  public static final Integer CODICE_CI_ISTOPT_TX=new Integer(1);
  public static final Integer CODICE_CI_ISTOPT_T0=new Integer(2);
  public static final Integer CODICE_CI_ISTOPT_TIS=new Integer(3);
  public static final Integer CODICE_CI_ISTOPT_T1=new Integer(20);
  public static final Integer CODICE_CI_ISTOPT_T2=new Integer(8);
  public static final Integer CODICE_CI_ISTOPT_T3=new Integer(9);
  public static final Integer CODICE_CI_ISTOPT_T4=new Integer(30);
    /*20080712 FINE MOD*/
  /***********************COLON*******************************/
  /*regione raggiunta*/
  public static final Integer CODICE_REGIONE_CIECO= 8;
  public static final Integer CODICE_REGIONE_MULTIPLE=9;
  public static final Integer CODICE_REGIONE_ANASTOMOSI=10;
  /*Motivo: altro*/
  public static final Integer CODICE_MOTIVO_ALTRO= new Integer(5);
  /*cOMPLICANZE: LATRO*/
  public static final Integer CODICE_COMPLIC_ALTRO=5;
  /*cOMPLICANZE interventi: AlTRO*/
  public static final Integer CODICE_COMPLIC_INT_ALTRO=4;
  /*DIMENSIONI: DA 1 A 2CM*/
  public static final Integer CODICE_DIM_1_2=new Integer(2);
  /*DIMENSIONI: > 2CM*/
  public static final Integer CODICE_DIM_2=new Integer(3);
  /*DISPLASIA GRAVE*/
  public static final Integer CODICE_DISP_GRAVE=new Integer(2);
  /*DISPLASIA ALTO GRADO*/
  public static final Integer CODICE_DISP_HG=new Integer(4);
  /*ISTOLOGIA: ADENOMA TUBULARE+DISPLASIA GRAVE*/
   public static final Integer CODICE_ISTO_AT_DG=new Integer(10);
   /*ISTOLOGIA: ADENOMA TUBULARE+DISPLASIA MODERATA*/
   public static final Integer CODICE_ISTO_AT_DM=new Integer(9);
   /*ISTOLOGIA: ADENOMA TUBULARE*/
   public static final Integer CODICE_ISTO_AT=new Integer(8);
   /*ISTOLOGIA: LEIOMIOMA*/
   public static final Integer CODICE_ISTO_LEIOMIOMA=new Integer(7);
   /*ISTOLOGIA: ALTRO*/
   public static final Integer CODICE_ISTO_ALTRO=new Integer(3);
   /*ISTOLOGIA: adenoma carncerizzato*/
   public static final Integer CODICE_ISTO_ADENOMA_C=new Integer(17);
   /*altre lesioni: poliposi*/
   public static final Integer CODICE_LESIONI_POLIPOSI=new Integer(1);
   /*altre lesioni: IBD*/
   public static final Integer CODICE_LESIONI_IBD=new Integer(2);
   /*altre lesioni: altro*/
   public static final Integer CODICE_LESIONI_ALTRO=3;
   /*ISTOLOGIA: adenoma tubolo-villoso*/
   public static final Integer CODICE_ISTO_TUBVIL=new Integer(11);
   /*ISTOLOGIA: adenoma villoso + displasia grave*/
   public static final Integer CODICE_ISTO_VILL_DG=new Integer(16);
  
  /* CONCLUSIONI DI 2 LIVELLO*/
  public static final Integer CODICE_CO2LIV_NEGATIVO=1;
  public static final Integer CODICE_CO2LIV_ALTRO=2;
  public static final Integer CODICE_CO2LIV_IBD=3;
  public static final Integer CODICE_CO2LIV_POLIPOSI=4;
  public static final Integer CODICE_CO2LIV_ADENOMA_LR=5;
  public static final Integer CODICE_CO2LIV_ADENOMA_HR=6;
  public static final Integer CODICE_CO2LIV_CARCINOMA=7;
  public static final Integer CODICE_CO2LIV_ADENOMA_MR=10;
  
  /*COLON: ISTOLOGIA CHIRURGICA*/
  /*STADIO M*/
  public static final Integer CODICE_CO_ISTOM_M0= 1;
  public static final Integer CODICE_CO_ISTOM_M1= 2;
  /*STADIO PN*/
  public static final Integer CODICE_CO_ISTOPN_N0= 2;
  public static final Integer CODICE_CO_ISTOPN_N1= 3;
  public static final Integer CODICE_CO_ISTOPN_N1b= 5;
  public static final Integer CODICE_MA_ISTOPN_N1c= 6;
  public static final Integer CODICE_CO_ISTOPN_N2= 10;
  public static final Integer CODICE_MA_ISTOPN_N2= 7;
  public static final Integer CODICE_MA_ISTOPN_N2b= 9;
  public static final Integer CODICE_CO_ISTOPN_N3= 11;
  public static final Integer CODICE_MA_ISTOPN_N3= 10;
  //20090202 aggiunto N1mic
   public static final Integer CODICE_MA_ISTOPN_N1mic=new Integer(14);
  /*STADIO PT*/
  public static final Integer CODICE_CO_ISTOPT_T0= 2;
  public static final Integer CODICE_CO_ISTOPT_TIS= 3;
  public static final Integer CODICE_CO_ISTOPT_T1= 4;
  public static final Integer CODICE_CO_ISTOPT_T1MICR= 5;
  public static final Integer CODICE_MA_ISTOPT_T1c= 8;
  public static final Integer CODICE_CO_ISTOPT_T2= 9;
  public static final Integer CODICE_CO_ISTOPT_T3= 10;
  public static final Integer CODICE_CO_ISTOPT_T4= 11;
  
  /*ASTLER-COLLER*/
  public static final Integer CODICE_ASTLERC_A= 1;
  public static final Integer CODICE_ASTLERC_B1= 2;
  public static final Integer CODICE_ASTLERC_B2= 3;
  public static final Integer CODICE_ASTLERC_C1= 4;
  public static final Integer CODICE_ASTLERC_C2= 5;
  public static final Integer CODICE_ASTLERC_D= 6;
  /*DUKES*/
  public static final Integer CODICE_DUKES_A= 1;
  public static final Integer CODICE_DUKES_B= 2;
  public static final Integer CODICE_DUKES_C= 3;
  public static final Integer CODICE_DUKES_D= 4;
  /*STADIAZIONE*/
  public static final Integer CODICE_STADIO_I= 1;
  public static final Integer CODICE_STADIO_II= 2;
  public static final Integer CODICE_STADIO_III= 3;
  public static final Integer CODICE_STADIO_IV= 4;
  /*INESECUZIONE DELL'INTERVENTO*/
  public static final Integer CODICE_INTMIE_ALTRO=4;
           

  
  /******************* MAMMOGRAFICO *****************/
  /*ESITD I PRIMO LIVELLO*/
  public static final Integer CODICE_ESITO_SOSPETTO=new Integer(4);
  /*citologia/agonbiopsia non eseguita per altro motivo*/
  public static final Integer CODICE_NON_EXEC_ALTRO=new Integer(2);
  /*altra tecnica di esecuzione dell'agobiopsia*/
  public static final Integer CODICE_ALTRA_TECNICA=new Integer(3);
  
  /*DIAGNOSI PEGGIORE*/
  /*BENIGNO*/
  public static final Integer CODICE_DIAGNOSI_BENIGNA= 1;
  /*SOSPETTO*/
  public static final Integer CODICE_DIAGNOSI_SOSPETTA=10;
  /*BENIGNO*/
  public static final Integer CODICE_DIAGNOSI_MALIGNA=11;
  /*altro*/
  public static final Integer CODICE_DIAGNOSI_ALTRO=2;
  /*normale*/
  public static final Integer CODICE_DIAGNOSI_NORMALE=-1;
  
  /*STADIAZIONE MAMMO*/
  public static final Integer CODICE_STADIO_ND=-1;
  public static final Integer CODICE_STADIO_I_MA=1;
  public static final Integer CODICE_STADIO_IIA_MA=2;
  public static final Integer CODICE_STADIO_IIB_MA=3;
  public static final Integer CODICE_STADIO_IIIA_MA=4;
  public static final Integer CODICE_STADIO_IIIB_MA=5;
  public static final Integer CODICE_STADIO_IIIC_MA=6;
  public static final Integer CODICE_STADIO_IV_MA=7;
     
  
  
  /*TIPI DI TEMPLATE*/
  public static final Integer CODICE_PIANO_LAVORO= new Integer(2);
  public static final Integer CODICE_ETICHETTE= new Integer(3);
  public static final Integer CODICE_ELENCHI_SOGG= new Integer(4);
  public static final Integer CODICE_ELENCHI_SOGG_ORD= new Integer(10);
    
    
  /* CODICI STANDARD PER IMPORT/EXPORT*/
  public static final String IMPEXP_IMP="IMP";
  public static final String IMPEXP_EXP="EXP";
  public static final String IMPEXP_LOC="LOC";
  public static final String TPDIP_ANAGRAFE="AN";
  public static final String TPDIP_ANAPAT="AP";
  public static final String TPDIP_RADIO="RA";
  public static final String TPDIP_LAB="LA";
  public static final String TPDIP_SDO="SD";
  public static final String TPDIP_SPS="SP";
  public static final String TPDIP_REGTUM="RT";
  public static final String TPDIP_ACCETTAZIONE="AC";
  public static final String TPDIP_RADIO2="R2";
  public static final String TPDIP_ANAPAT2="A2";
  public static final String TPDIP_DWH="DW";
  public static final String TPDIP_FLUSSOSPA = "SS";
  public static final String TPDIP_ENDO2 = "E2";
  public static final Integer MODALITA_FTP=new Integer(1);
  public static final Integer MODALITA_DB=new Integer(2);
  public static final Integer MODALITA_PDD=new Integer(3);
  
  
  /* CODICI DI PROGRESSIVI*/
  public static final String PROGRESSIVO_CAMPIONE="CAMPIONE";
  public static final String PROGRESSIVO_RICHIESTA="RICHIESTA";
  public static final String PROGRESSIVO_RICHIESTA2="RICHIESTA2";
  public static final String PROGRESSIVO_RICHMX="RICHMX";
  public static final String PARAMETRO_PROD_RICHIESTA="prod_codice_richiesta";
  public static final String PARAMETRO_PROD_RICHIESTA2="prod_codice_richiesta2";
  public static final String PARAMETRO_PROD_CAMPIONE="prod_codice_campione";
  public static final String PARAMETRO_ID_MX="idmx_auto";
  public static final String PARAMETRO_DATA_RICH_REF="data_rich_ref";
  public static final String PARAMETRO_STUDIO_45_MX="mx45";
  public static final String PARAMETRO_OTP="otp";
  
  // I00093449
  // Progressivo unico Cod Campione e richiesta
  public static final String PARAMETRO_PROD_UNQ_RIC_CAM="PROD_UNQ_COD_RICH_CAMP";
  public static final String PROGRESSIVO_UNQ_RICHIESTA_CAMPIONE="UNQ_COD_RIC_CAM";    
  
  /* TIPI DOCUMENTI*/
  public static final String TIPO_DOC_TESSERA_SANITARIA="TS";
  
/****************************************************/
/**                DATI STANDARD                   **/
/****************************************************/
   /*Rappresentazione numerica die valori booleani si DB
   */
  public static final Integer DB_TRUE=new Integer(1);
  public static final Integer DB_FALSE=new Integer(0);
  public static final int FORMATO_PDF=0;
  public static final int FORMATO_CSV=1;
  /* tabella SINOND */
  public static final int DB_SINOND_ND = 0;
  public static final int DB_SINOND_NO = 1;
  public static final int DB_SINOND_SI = 2;
  /* VERSIONE DI PDF ACETTATA DA POSTEL */
  public static final Character PDF_VERSION=JRPdfExporterParameter.PDF_VERSION_1_3;


/****************************************************/
/**       DATI CONFIGURABILI A PIACIMENTO          **/
/****************************************************/
/*Per quanti giorni predisporre automaticamente la 
 * generazione dell'agenda fisica
 */
 public static final int AGENDA_FISICA_N_GIORNI=90;
 /* Da che giorno cominciare a generare l'agenda fisica
  * (non prima di)
  */
 public static final Date AGENDA_FISICA_INIZIO=getTomorrow();
 /*Per quanti giorni predisporre automaticamente la 
 * generazione degli inviti
 */
 public static final int AGENDA_INVITI_N_GIORNI=90;
 /* Da che giorno cominciare a generare GLI INVITI
  * (non prima di)
  */
 public static final int AGENDA_INVITI_INIZIO_POSTICIPATO=30;
 
 public static final String NOME_CODA_STAMPA="screening";
 
 public static final String IMPEXP_DATE_PATTERN="yyyyMMddHHmm";//"ddMMyyHHmm";
 
 //se le stampe per Postel sono di test oppure reali
 public static final boolean POSTEL_TEST=true;

	// Identificativo del Trial mRNA, corrisponde alla colonna SO_CNF_TRIAL.IDTRIAL
	public static final int TRIAL_MRNA = 1;
        
 public static final String motivo_obbl="motivo_obbl";
	
  public ConfigurationConstants()
  {
  }
  public static Date getTomorrow()
  {
    Calendar c=Calendar.getInstance();
    c.setTime(new Date());
    c.add(Calendar.DAY_OF_MONTH,1);
    return c.getTime();
  }
  
}