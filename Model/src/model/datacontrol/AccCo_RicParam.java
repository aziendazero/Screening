package model.datacontrol;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import model.common.AccCo_AppModule;

import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class AccCo_RicParam {
    public AccCo_RicParam() {
        this.setNavigazione(new Boolean(false));
    }

    String cognome, nome;
    String codFisc, tessSan;
    Integer idCprel;
    Boolean navigazione;
    String livello;
    BigDecimal codCampione;
    BigDecimal codRichiesta;
    /*20080714 MOD: codice archiviazione*/
    String codIdSogg;
    /*20080714 FINE MOD*/
    //15112013 Gaion: esito invito per tendina
    String esito;
    //15112013 fine
    String tipoDocumento;
    String codiceDocumento;
    Integer soloStorico;
    String chiave;

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
        this.setCodCampione(null);
        this.setCodRichiesta(null);
        /*20080714 MOD: codice archiviazione*/
        this.setCodIdSogg(null);
        /*20080714 FINE MOD*/
        this.setEsito("N");
        this.setCodiceDocumento(null);
        this.setSoloStorico(null);
        this.setTipoDocumento(null);
        this.setChiave(null);
    }

    public void queryInviti() {
        ADFContext adfCtx = ADFContext.getCurrent();
        Map session = adfCtx.getSessionScope();
        Map req = adfCtx.getRequestScope();
        String tpscr = (String) session.get("scr");
        String ulss = (String) session.get("ulss");

        String whcl = "TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";

        AccCo_AppModule am =
            (AccCo_AppModule) BindingContext.getCurrent().findDataControl("AccCo_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("AccCo_RicInvitiView1");

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

        String tess = this.getTessSan();
        if (tess != null && !(tess.equals(""))) {
            whcl += " and CODTS = '" + tess + "'";
        }

        String lev = this.getLivello();
        BigDecimal codCamp = this.getCodCampione();
        if (lev.equals("1") && codCamp != null) {
            whcl += " and COD_CAMPIONE = " + codCamp.toString();
        }

        BigDecimal codRich = this.getCodRichiesta();
        if (codRich != null) {
            whcl += " and COD_RICH = " + codRich.toString();
        }
        /*20080714 MOD: codice archiviazione*/
        String idmx = this.getCodIdSogg();
        if (idmx != null && !idmx.equals("")) {
            whcl += " and codidsogg_mx = '" + idmx + "'";
        }
        /*20080714 FINE MOD*/
        
        String chiave = this.getChiave();
        if (chiave != null && !(chiave.equals(""))) {
            whcl += " and CHIAVE = '" + chiave + "'";
        }

        //filtro sui centri dell'utente
        Integer c1 = (Integer) session.get("centro1liv");
        Integer c2 = (Integer) session.get("centro2liv");
        String in = (String) session.get("elenco_centri");
        if (c1 != null || c2 != null) { //filtro i soggetti che sono associati ad un centro dell'utente oppure hanno
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

        vo.setWhereClause(whcl);
        vo.setOrderByClause("DTORAAPP,COGNOME,NOME");
        vo.executeQuery();
        // System.out.println(vo.getQuery());

        //20110401 Serra: il fatto che un invito sia di 1 o 2 livello non va collegato al centro, che può essere null,
        // ma alla scelta del livello fatta dall'utente
        /* if (idCprel != null)
    {
      int liv = SoggUtils.getLivelloCt(am,new Number(idCprel.intValue()));
      session.setAttribute("accPrimo",new Boolean(liv == 1));
    }
    */
        if (this.livello != null) {
            int liv = new Integer(this.livello).intValue();
            session.put("accPrimo", new Boolean(liv == 1));
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
                //System.out.println(qry);
            }

        }

        String newQry = "select codts, ulss from (" + qry + ")";
        Cnf_selectionBean clauseBean =
            (Cnf_selectionBean) BindingContext.getCurrent().findDataControl("Cnf_selectionBean").getDataProvider();
        clauseBean.setInClause(newQry);
        clauseBean.setNote(null);
        // System.out.println(newQry);
        //fine 20110524

        //filtro gli esisti
        ViewObject voEsiti = am.findViewObject("AccCo_EsitoView1");
        voEsiti.setWhereClause("TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "' AND LIVESITO = " + this.getLivello());
        voEsiti.executeQuery();

    }

    /**
     *
     * @webmethod
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }


    /**
     *
     * @webmethod
     */
    public String getCognome() {
        return cognome;
    }


    /**
     *
     * @webmethod
     */
    public void setNome(String nome) {
        this.nome = nome;
    }


    /**
     *
     * @webmethod
     */
    public String getNome() {
        return nome;
    }


    /**
     *
     * @webmethod
     */
    public void setCodFisc(String codFisc) {
        this.codFisc = codFisc;
    }


    /**
     *
     * @webmethod
     */
    public String getCodFisc() {
        return codFisc;
    }


    /**
     *
     * @webmethod
     */
    public void setTessSan(String tessSan) {
        this.tessSan = tessSan;
    }


    /**
     *
     * @webmethod
     */
    public String getTessSan() {
        return tessSan;
    }


    /**
     *
     * @webmethod
     */
    public void setIdCprel(Integer idCprel) {
        this.idCprel = idCprel;
    }


    /**
     *
     * @webmethod
     */
    public Integer getIdCprel() {
        return idCprel;
    }


    /**
     *
     * @webmethod
     */
    public void setNavigazione(Boolean navigazione) {
        this.navigazione = navigazione;
    }


    /**
     *
     * @webmethod
     */
    public Boolean getNavigazione() {
        return navigazione;
    }


    /**
     *
     * @webmethod
     */
    public void setLivello(String livello) {
        this.livello = livello;
    }


    /**
     *
     * @webmethod
     */
    public String getLivello() {
        return livello;
    }


    public void setCodCampione(BigDecimal codCampione) {
        this.codCampione = codCampione;
    }


    public BigDecimal getCodCampione() {
        return codCampione;
    }


    public void setCodRichiesta(BigDecimal codRichiesta) {
        this.codRichiesta = codRichiesta;
    }


    public BigDecimal getCodRichiesta() {
        return codRichiesta;
    }

    /*20080714 MOD: codice archiviazione*/

    public String getCodIdSogg() {
        return codIdSogg;
    }

    public void setCodIdSogg(String codIdSogg) {
        this.codIdSogg = codIdSogg;
    }
    /*20080714 FINE MOD*/

    public void setEsito(String esito) {
        this.esito = esito;
    }


    public String getEsito() {
        return esito;
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

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }


    public String getChiave() {
        return chiave;
    }


    public void setSoloStorico(Integer soloStorico) {
        this.soloStorico = soloStorico;
    }


    public Integer getSoloStorico() {
        return soloStorico;
    }

}
