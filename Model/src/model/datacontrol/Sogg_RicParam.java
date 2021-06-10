package model.datacontrol;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import model.common.Sogg_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.SoggUtils;
import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ApplicationModule;
import oracle.jbo.ViewObject;

public class Sogg_RicParam {
    public Sogg_RicParam() {
        this.setNavigazione(false);
    }

    String cognome, nome, idComRes, desComRes, desMedico;
    String codFisc, tessSan;
    Integer idCprel, idMedico, idDistr;
    Integer inEta;
    Boolean navigazione;
    Integer etaMin, etaMax;
    Integer altoRischio;
    Integer restituire;
    Integer idStatoTrial;
    Integer idBraccioTrial;
    String testProposto;
    String prelievoHpv;
    String tipoDocumento;
    String codiceDocumento;
    Integer soloStorico;
    Integer codComRes;
    String mpiRegionale;
    String codClassePop;
    String chiave;
    
    Integer annoNascita;
    String codRichiesta;
    String codCampione;

    @SuppressWarnings("unchecked")
    public void setEtaScreening() {
        @SuppressWarnings("deprecation")
        Sogg_AppModule am =
            (Sogg_AppModule) BindingContext.getCurrent().findDataControl("Sogg_AppModuleDataControl").getDataProvider();
        Integer[] etaScr = SoggUtils.getEtaScreening(am);
        this.setEtaMin(etaScr[0]);
        this.setEtaMax(etaScr[1]);
    }

    @SuppressWarnings("unchecked")
    public void querySogg() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        Map req = adfCtx.getRequestScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");
        boolean debug_mode = session.get("debug_mode") != null;

        if (debug_mode)
            System.out.println("Creazione query di ricerca: " + new java.util.Date().toString());

        String whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' and SIGLA <> 'ME'";

        BindingContext ctx = BindingContext.getCurrent();
        @SuppressWarnings("deprecation")
        Sogg_AppModule am = (Sogg_AppModule) ctx.findDataControl("Sogg_AppModuleDataControl").getDataProvider();

        ViewObject vo = am.findViewObject("Sogg_RicercaView1");
        //TODO: patch per la perdita delle bind variables VERIFICARE!!!!
        vo.setWhereClauseParams(new Object[] { ulss, tpscr, ulss, tpscr, tpscr });

        Integer inEta = this.getInEta();
        if (inEta.intValue() == 1) {
            Integer etaMin = this.getEtaMin();
            Integer etaMax = this.getEtaMax();
            whcl += " and ETA >= " + etaMin.toString();
            whcl += " and ETA <= " + etaMax.toString();
        }

        Integer idCprel = this.getIdCprel();
        if (idCprel != null) {
            whcl += " and IDCENTROPRELIEVO = " + idCprel.toString();
        }

        Date dtInvito = (Date) session.get("dtInvito");
        if (dtInvito != null) {
            whcl += " and DTAPP = to_date('" +  new SimpleDateFormat("dd/MM/yyyy").format(dtInvito) + "','dd/mm/yyyy')";
        }

        Date dtNasc = session.get("dtNasc") != null ? (Date) session.get("dtNasc") : null;
        if(dtNasc==null)
            session.remove("dtNasc");
        else
            session.put("dtNasc",dtNasc);
        if (dtNasc != null) {
            whcl += " and DATA_NASCITA = to_date('" + new SimpleDateFormat("dd/MM/yyyy").format(dtNasc) + "','dd/mm/yyyy')";
        }
        
        //I00102494
        Integer annoNascita = this.getAnnoNascita();
        if (annoNascita != null) {
            whcl += " and EXTRACT(YEAR FROM data_nascita) = " + annoNascita.toString();
        }

        String cogn = this.getCognome();
        if (cogn != null && !(cogn.equals(""))) {
            cogn = ViewHelper.replaceApostrophe(cogn);
            whcl += " and COGNOME LIKE '" + cogn.trim() + "%'";
        }

        String nome = this.getNome();
        if (nome != null && !(nome.equals(""))) {
            nome = ViewHelper.replaceApostrophe(nome);
            whcl += " and NOME LIKE '" + nome.trim() + "%'";
        }

        String codFisc = this.getCodFisc();
        if (codFisc != null && !(codFisc.equals(""))) {
            whcl += " and CF = '" + codFisc + "'";
        }

        String tess = this.getTessSan();
        if (tess != null && !(tess.equals(""))) {
            whcl += " and CODTS = '" + tess + "'";
        }

        String mpi = this.getMpiRegionale();
        if (mpi != null && !(mpi.equals(""))) {
            whcl += " and IDINTERNO = '" + mpi + "'";
        }

        String comRes = this.getIdComRes();
        if (comRes != null && !(comRes.equals(""))) {
            whcl += " and CODCOMRES = '" + comRes + "'";
        }

        Integer distr = this.getIdDistr();
        if (distr != null) {
            whcl += " and CODDISTRZONA = " + distr.toString();
        }

        Integer med = this.getIdMedico();
        if (med != null) {
            whcl += " and CODICEREGMEDICO = " + med.toString();
        }

        Integer altoR = this.getAltoRischio();
        if (altoR.intValue() == 1) {
            whcl += " and ALTORISCHIO = 1";
        }

        Integer rest = this.getRestituire();
        if (rest.intValue() == 1) {
            whcl += " and RESTITUIRE = 1";
        }

        //se l'utente � vincolato ad un centro fisico si vanno a filtrare solo i soggetti
        //che devono, per configurazione, aferire a tale centro
        Integer c1 = (Integer) session.get("centro1liv");
        Integer c2 = (Integer) session.get("centro2liv");
        String in = (String) session.get("elenco_centri");
        if (c1 != null || c2 != null) {
            //filtro i soggetti che sono associati ad un centro dell'utente oppure hanno
            //l'ultimo invito in tale centro

            whcl += " and (idcentroprelievo in " + in;
            if (c1 != null)
                whcl += " or idcentro1liv=" + c1;
            if (c2 != null)
                whcl += " or idcentro2liv=" + c2;

            whcl += ")";
        }

        // Trial
        if (getIdStatoTrial() != null) {
            switch (getIdStatoTrial().intValue()) {
            case -1:
                whcl += " AND idstato_trial IS NULL";
                break;
            case -2:
                whcl += " AND idstato_trial IS NOT NULL";
                break;
            default:
                whcl += " AND idstato_trial = " + getIdStatoTrial().toString();
            }
        }
        
        if (getIdBraccioTrial() != null) {
            switch (getIdBraccioTrial().intValue()) {
            case -1:
                whcl += " AND idbraccio_trial IS NULL";
                break;
            case -2:
                whcl += " AND idbraccio_trial IS NOT NULL";
                break;
            default:
                whcl += " AND idbraccio_trial = " + getIdBraccioTrial().toString();
            }
        }

        String test = this.getTestProposto();
        if (test != null) {
            whcl += " and TEST_PROPOSTO = '" + test + "'";
        }

        String prelievo = this.getPrelievoHpv();
        if (prelievo != null) {
            if (prelievo.equals("Si")) {
                whcl += " and (PRELIEVO_HPV =" + ConfigurationConstants.DB_SINOND_SI + " and CODESITOINVITO = 'S')";
            } else if (prelievo.equals("No")) {
                whcl += " and (PRELIEVO_HPV =" + ConfigurationConstants.DB_SINOND_NO + " and CODESITOINVITO = 'S')";
            }
        }

        String numDoc = this.getCodiceDocumento();
        if (numDoc != null && numDoc.length() > 0) {
            if (this.getSoloStorico() != null && this.getSoloStorico().intValue() == 1) {
                whcl +=
                    " and CODTS IN (select CODTS AS CODTS_DOC " + "from so_soggetto where codts in " +
                    "(SELECT COLUMN_VALUE " + "FROM TABLE(CAST(FUN_DOC_TROVA_CODTS_ARRAY('" + this.getTipoDocumento() +
                    "','" + numDoc + "','" + ulss + "','S') AS VAR_ARRAY)))  " + "and ULSS ='" + ulss + "') ";

            } else {
                whcl +=
                    " and CODTS IN (FUN_DOC_TROVA_CODTS('" + this.getTipoDocumento() + "','" + numDoc + "','" + ulss +
                    "')) ";
            }
        }
        
        String codClasse = this.getCodClassePop();
        if (codClasse != null)
        {
          whcl += " and CODCLASSEPOP = '"+codClasse+"' ";
        }
        
        String chiave = this.getChiave();
        if (chiave != null && !"".equals(chiave))
        {
          whcl += " and CHIAVE = '"+chiave+"' ";
        }
        
        //I00102494
        String codRichiesta = this.getCodRichiesta();
        if (codRichiesta != null && !"".equals(codRichiesta))
        {
          whcl += " and CODRICHIESTA = "+codRichiesta+" ";
        }
        
        String codCampione = this.getCodCampione();
        if (codCampione != null && !"".equals(codCampione))
        {
          whcl += " and CODCAMPIONE = "+codCampione+" ";
        }

        vo.setWhereClause(whcl);
        vo.setOrderByClause("COGNOME, NOME, DATA_NASCITA");

        if (debug_mode)
            System.out.println("Inizio ricerca: " + new java.util.Date().toString());

        vo.executeQuery();

        if (debug_mode)
            System.out.println("Fine ricerca: " + new java.util.Date().toString());

        String qry = vo.getQuery();

        /*20080717 correzione*/
        Object[] pars = vo.getWhereClauseParams();
        if (pars != null) {
            for (int i = 0; i < pars.length; i++) {
                qry = qry.replaceFirst(":" + (i + 1), "'" + (String) pars[i] + "'");
            }
        }
        /*20080717 FINE correzione*/

        String newQry = "select codts, ulss from (" + qry + ")";

        @SuppressWarnings("deprecation")
        Cnf_selectionBean clauseBean = (Cnf_selectionBean) ctx.findDataControl("Cnf_selectionBean").getDataProvider();
        clauseBean.setInClause(newQry);
        clauseBean.setNote(null);

        if (debug_mode)
            System.out.println("Fine elaborazione: " + new java.util.Date().toString());
    }

    @SuppressWarnings("unchecked")
    public void resetCampi() {
        this.setCodFisc(null);
        this.setCognome(null);
        this.setDesComRes(null);
        this.setDesMedico(null);
        this.setIdComRes(null);
        this.setIdCprel(null);
        this.setIdDistr(null);
        this.setIdMedico(null);
        this.setNome(null);
        this.setTessSan(null);
        this.setMpiRegionale(null);
        this.setAnnoNascita(null);
        this.setCodRichiesta(null);
        this.setCodCampione(null);

        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.remove("dtInvito");
        session.remove("dtNasc");
        session.put("codCom", "");
        session.put("codMed", "");
        String tpscr = (String) session.get("scr");

        // Per il cardiovascolare il default di "in et� per lo screening" � false
        this.setInEta("CA".equals(tpscr) ? new Integer(0) : new Integer(1));

        this.setNavigazione(false);
        this.setEtaScreening();
        session.put("inEta", getInEta().intValue() == 0 ? Boolean.FALSE : Boolean.TRUE);
        this.setAltoRischio(new Integer(0));
        this.setRestituire(new Integer(0));
        this.setIdStatoTrial(null);
        this.setIdBraccioTrial(null);
        this.setTestProposto(null);
        this.setPrelievoHpv(null);
        this.setCodiceDocumento(null);
        this.setSoloStorico(null);
        this.setCodClassePop(null);
        this.setChiave(null);
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setIdComRes(String idComRes) {
        this.idComRes = idComRes;
    }

    public String getIdComRes() {
        return idComRes;
    }

    public void setDesComRes(String desComRes) {
        this.desComRes = desComRes;
    }

    public String getDesComRes() {
        return desComRes;
    }

    public void setDesMedico(String desMedico) {
        this.desMedico = desMedico;
    }

    public String getDesMedico() {
        return desMedico;
    }

    public void setCodFisc(String codFisc) {
        this.codFisc = codFisc;
    }

    public String getCodFisc() {
        return codFisc;
    }

    public void setTessSan(String tessSan) {
        this.tessSan = tessSan;
    }

    public String getTessSan() {
        return tessSan;
    }

    public void setIdCprel(Integer idCprel) {
        this.idCprel = idCprel;
    }

    public Integer getIdCprel() {
        return idCprel;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdDistr(Integer idDistr) {
        this.idDistr = idDistr;
    }

    public Integer getIdDistr() {
        return idDistr;
    }

    public void setInEta(Integer inEta) {
        this.inEta = inEta;
    }

    public Integer getInEta() {
        return inEta;
    }

    public void setNavigazione(Boolean navigazione) {
        this.navigazione = navigazione;
    }

    public Boolean getNavigazione() {
        return navigazione;
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

    public void setAltoRischio(Integer altoRischio) {
        this.altoRischio = altoRischio;
    }

    public Integer getAltoRischio() {
        return altoRischio;
    }

    public void setRestituire(Integer restituire) {
        this.restituire = restituire;
    }

    public Integer getRestituire() {
        return restituire;
    }

    public Integer getIdStatoTrial() {
        return idStatoTrial;
    }

    public void setIdStatoTrial(Integer idStatoTrial) {
        this.idStatoTrial = idStatoTrial;
    }

    public Integer getIdBraccioTrial() {
        return idBraccioTrial;
    }

    public void setIdBraccioTrial(Integer idBraccioTrial) {
        this.idBraccioTrial = idBraccioTrial;
    }


    public void setTestProposto(String testProposto) {
        this.testProposto = testProposto;
    }


    public String getTestProposto() {
        return testProposto;
    }


    public void setPrelievoHpv(String prelievoHpv) {
        this.prelievoHpv = prelievoHpv;
    }


    public String getPrelievoHpv() {
        return prelievoHpv;
    }


    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }


    public String getTipoDocumento() {
        return tipoDocumento;
    }


    public void setCodiceDocumento(String codiceDocumento) {
        this.codiceDocumento = codiceDocumento;
    }


    public String getCodiceDocumento() {
        return codiceDocumento;
    }


    public void setSoloStorico(Integer soloStorico) {
        this.soloStorico = soloStorico;
    }


    public Integer getSoloStorico() {
        return soloStorico;
    }


    public void setCodComRes(Integer codComRes) {
        this.codComRes = codComRes;
    }


    public Integer getCodComRes() {
        return codComRes;
    }


    public void setMpiRegionale(String mpiRegionale) {
        this.mpiRegionale = mpiRegionale;
    }


    public String getMpiRegionale() {
        return mpiRegionale;
    }
    
    public void setCodClassePop(String codClassePop) {
        this.codClassePop = codClassePop;
    }

    public String getCodClassePop() {
        return codClassePop;
    }
    
    public void setChiave(String chiave) {
        this.chiave = chiave;
    }

    public String getChiave() {
        return chiave;
    }

    public void setAnnoNascita(Integer annoNascita) {
        this.annoNascita = annoNascita;
    }

    public Integer getAnnoNascita() {
        return annoNascita;
    }
    
    public void setCodRichiesta(String codRichiesta) {
        this.codRichiesta = codRichiesta;
    }

    public String getCodRichiesta() {
        return codRichiesta;
    }

    public void setCodCampione(String codCampione) {
        this.codCampione = codCampione;
    }

    public String getCodCampione() {
        return codCampione;
    }
}
