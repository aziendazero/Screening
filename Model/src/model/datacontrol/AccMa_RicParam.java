package model.datacontrol;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;

import model.common.AccMa_AppModule;

import model.commons.ViewHelper;

import oracle.adf.model.BindingContext;

import oracle.adf.share.ADFContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class AccMa_RicParam {
    public AccMa_RicParam() {
        this.setNavigazione(Boolean.FALSE);
    }

    String cognome, nome;
    String codFisc, tessSan;
    Integer idCprel;
    Boolean navigazione;
    String livello;
    String codIdSogg;
    BigDecimal codRichiesta;
    Integer annoUltAcc;
    String esito; //18112013 gaion
    String tipoDocumento;
    String codiceDocumento;
    Boolean soloStorico;
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
        this.setEsito("N");
        this.setCodiceDocumento(null);
        this.setCodRichiesta(null);
        this.setCodIdSogg(null);
        this.setAnnoUltAcc(null);
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

        AccMa_AppModule am =
            (AccMa_AppModule) BindingContext.getCurrent().findDataControl("AccMa_AppModuleDataControl").getDataProvider();
        ViewObject vo = am.findViewObject("AccMa_RicInvitiView1");

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

        BigDecimal cRich = this.getCodRichiesta();
        if (cRich != null) {
            whcl += " and cod_rich = " + cRich.toString();
        }

        String idmx = this.getCodIdSogg();
        if (idmx != null && !idmx.equals("")) {
            whcl += " and codidsogg_mx = '" + idmx + "'";
        }

        Integer anno = this.getAnnoUltAcc();
        if (anno != null) {
            whcl += " and anno = " + anno.toString();
        }


        //filtro sui centri dell'utente
        Number c1 = (Number) session.get("centro1liv");
        Number c2 = (Number) session.get("centro2liv");
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
            if (this.getSoloStorico() != null && this.getSoloStorico()) {
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

        //fine 20110524

        //filtro gli esisti
        ViewObject voEsiti = am.findViewObject("AccMa_EsitoView1");
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


    public void setCodIdSogg(String codIdSogg) {
        this.codIdSogg = codIdSogg;
    }


    public String getCodIdSogg() {
        return codIdSogg;
    }


    public void setCodRichiesta(BigDecimal codRichiesta) {
        this.codRichiesta = codRichiesta;
    }


    public BigDecimal getCodRichiesta() {
        return codRichiesta;
    }


    public void setAnnoUltAcc(Integer annoUltAcc) {
        this.annoUltAcc = annoUltAcc;
    }


    public Integer getAnnoUltAcc() {
        return annoUltAcc;
    }


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


    public void setSoloStorico(Boolean soloStorico) {
        this.soloStorico = soloStorico;
    }


    public Boolean getSoloStorico() {
        return soloStorico;
    }

    public void setChiave(String chiave) {
        this.chiave = chiave;
    }

    public String getChiave() {
        return chiave;
    }
}
