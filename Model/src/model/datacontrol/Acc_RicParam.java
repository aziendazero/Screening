package model.datacontrol;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import model.common.Acc_AppModule;

import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;

import oracle.jbo.ViewObject;
import oracle.jbo.Row;

public class Acc_RicParam {
    String cognome, nome;
    String codFisc, tessSan;
    Integer idCprel;
    Boolean navigazione;
    String livello;
    /*20080714 MOD: codice archiviazione*/
    String codIdSogg;
    /*20080714 FINE MOD*/
    BigDecimal codRichiesta; // mauro 7-10-2008
    Integer idPrelevatore; // mauro 20/09/2010
    String esito; //18112013 gaion
    Integer idStatoTrial;
    Integer idBraccioTrial;
    String testProposto;
    String prelievoHpv;
    String tipoDoc;
    String codiceDoc;
    Integer soloStorico;
    String chiave;

    public Acc_RicParam() {
        this.setNavigazione(false);
    }

    public void resetCampi() {
        this.setIdCprel(null);
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        session.remove("dtInvito");
        this.setCognome(null);
        this.setNome(null);
        this.setTessSan(null);
        this.setCodFisc(null);
        this.setLivello("1");
        /*20080714 MOD: codice archiviazione*/
        this.setCodIdSogg(null);
        /*20080714 FINE MOD*/
        this.setEsito("N");
        this.setIdStatoTrial(null);
        this.setIdBraccioTrial(null);
        this.setTestProposto(null);
        this.setPrelievoHpv(null);
        this.setCodiceDoc(null);
        this.setSoloStorico(null);
        setCodRichiesta(null);
        setTipoDoc(null);
        setChiave(null);
    }

    public void queryInviti() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        Map req = adfCtx.getRequestScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");

        String whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";

        Acc_AppModule am =
            (Acc_AppModule) BindingContext.getCurrent().findDataControl("Acc_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("Acc_RicInvitiView1");

        Integer idCprel = this.getIdCprel();
        if (idCprel != null) {
            whcl += " and IDCENTROPRELIEVO = " + idCprel.toString();
        }

        Date dtInvito = session.get("dtInvito") != null ? (Date) session.get("dtInvito") : null;
        if (dtInvito == null)
            session.remove("dtInvito");
        else
            session.put("dtInvito", dtInvito);
        if (dtInvito != null) {
            whcl += " and DTAPP = to_date('" + new SimpleDateFormat("dd/MM/yyyy").format(dtInvito) + "','dd/mm/yyyy')";
        }

        String cogn = this.getCognome();
        cogn = ViewHelper.replaceApostrophe(cogn);
        if (cogn != null && !(cogn.equals(""))) {
            whcl += " and COGNOME LIKE '" + cogn + "%'";
        }

        String nome = this.getNome();
        nome = ViewHelper.replaceApostrophe(nome);
        if (nome != null && !(nome.equals(""))) {
            whcl += " and NOME LIKE '" + nome + "%'";
        }

        String codFisc = this.getCodFisc();
        if (codFisc != null && !(codFisc.equals(""))) {
            whcl += " and CF = '" + codFisc + "'";
        }
        
        String chiave = this.getChiave();
        if (chiave != null && !(chiave.equals(""))) {
            whcl += " and CHIAVE = '" + chiave + "'";
        }

        String tess = this.getTessSan();
        if (tess != null && !(tess.equals(""))) {
            whcl += " and CODTS = '" + tess + "'";
        }

        // mauro 7-10-2008 gestione codice richiesta
        if (codRichiesta != null) {
            whcl += " and COD_RICHIESTA = " + codRichiesta;
        }
        //

        /*20080714 MOD: codice archiviazione*/
        String idmx = this.getCodIdSogg();
        if (idmx != null && !idmx.equals("")) {
            whcl += " and codidsoggmx = '" + idmx + "'";
        }
        /*20080714 FINE MOD*/

        //filtro sui centri dell'utente
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

        //20110401 filtro sul livello
        if (this.livello != null) {
            whcl += " and livello=" + this.livello;
        }
        //fine 20110401

        // HPV
        if ("HPV".equals(getTestProposto())) {
            whcl += " AND test_proposto = 'HPV'";
        } else if ("PAP test".equals(getTestProposto())) {
            whcl += " AND test_proposto = 'PAP'";
        }
        if ("Si".equals(getPrelievoHpv())) {
            whcl += " AND prelievo_hpv = 2 AND codesitoinvito = 'S'";
        } else if ("No".equals(getPrelievoHpv())) {
            whcl += " AND prelievo_hpv = 1 AND codesitoinvito = 'S'";
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

        String numDoc = this.getCodiceDoc();
        if (numDoc != null && numDoc.length() > 0) {
            if (this.getSoloStorico() != null && this.getSoloStorico().intValue() == 1) {
                whcl +=
                    " and CODTS IN (select CODTS AS CODTS_DOC " + "from so_soggetto where codts in " +
                    "(SELECT COLUMN_VALUE " + "FROM TABLE(CAST(FUN_DOC_TROVA_CODTS_ARRAY('" + this.getTipoDoc() +
                    "','" + numDoc + "','" + ulss + "','S') AS VAR_ARRAY)))  " + "and ULSS ='" + ulss + "') ";
            } else {
                whcl +=
                    " and CODTS IN (FUN_DOC_TROVA_CODTS('" + this.getTipoDoc() + "','" + numDoc + "','" + ulss + "')) ";
            }
        }

        vo.setWhereClause(whcl);
        //  System.out.println(vo.getQuery());
        vo.setOrderByClause("DTORAAPP,COGNOME,NOME");
        vo.executeQuery();

        //20110401 Serra: il fatto che un invito sia di 1 o 2 livello non va collegato al centro, che può essere null,
        // ma alla scelta del livello fatta dall'utente
        /* if (idCprel != null)
		{
		int liv = SoggUtils.getLivelloCt(am,new Integer(idCprel.intValue()));
		session.setAttribute("accPrimo",new Boolean(liv == 1));
		}
		*/
        if (this.livello != null) {
            int liv = new Integer(this.livello).intValue();
            session.put("accPrimo", liv == 1);
        }
        //fine 20110401

        //20110922 SARA se il risultato è uno, lo seleziono
        long count = vo.getEstimatedRowCount();
        if (count == 1) {
            Row first = vo.first();
            first.setAttribute("Selezionato", Boolean.TRUE);
            vo.setCurrentRow(first);
        }
        //fine 20110922

        //20110524 SARA
        String qry = vo.getQuery();

        Object[] pars = vo.getWhereClauseParams();
        if (pars != null) {
            for (int i = 0; i < pars.length; i++) {
                qry = qry.replaceFirst(":" + (i + 1), "'" + (String) pars[i] + "'");
            }
        }

        String newQry = "select codts, ulss from (" + qry + ")";
        Cnf_selectionBean clauseBean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        clauseBean.setInClause(newQry);
        clauseBean.setNote(null);
        //fine 20110524

        //filtro gli esiti
        ViewObject voEsiti = am.findViewObject("Acc_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND LIVESITO = " + this.getLivello());
        voEsiti.executeQuery();
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

    public void setNavigazione(Boolean navigazione) {
        this.navigazione = navigazione;
    }

    public Boolean getNavigazione() {
        return navigazione;
    }

    public void setLivello(String livello) {
        this.livello = livello;
    }

    public String getLivello() {
        return livello;
    }
    /*20080714 MOD: codice archiviazione*/

    public String getCodIdSogg() {
        return codIdSogg;
    }

    public void setCodIdSogg(String codIdSogg) {
        this.codIdSogg = codIdSogg;
    }

    public void setCodRichiesta(BigDecimal codRichiesta) {
        this.codRichiesta = codRichiesta;
    }

    public BigDecimal getCodRichiesta() {
        return codRichiesta;
    }

    public void setIdPrelevatore(Integer idPrelevatore) {
        this.idPrelevatore = idPrelevatore;
    }

    public Integer getIdPrelevatore() {
        return idPrelevatore;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    public String getEsito() {
        return esito;
    }
    /*20080714 FINE MOD*/

    public void setIdStatoTrial(Integer value) {
        idStatoTrial = value;
    }

    public Integer getIdStatoTrial() {
        return idStatoTrial;
    }

    public void setIdBraccioTrial(Integer value) {
        idBraccioTrial = value;
    }

    public Integer getIdBraccioTrial() {
        return idBraccioTrial;
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


    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }


    public String getTipoDoc() {
        return tipoDoc;
    }


    public void setCodiceDoc(String codiceDoc) {
        this.codiceDoc = codiceDoc;
    }


    public String getCodiceDoc() {
        return codiceDoc;
    }


    public void setSoloStorico(Integer soloStorico) {
        this.soloStorico = soloStorico;
    }


    public Integer getSoloStorico() {
        return soloStorico;
    }

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }

    public String getChiave() {
        return chiave;
    }
}

