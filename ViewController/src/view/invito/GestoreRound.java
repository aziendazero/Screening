package view.invito;
import insiel.dataHandling.DateUtils;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import model.common.A_SoAziendaViewRow;
import model.common.A_SoCnfTpscrViewRow;
import model.common.A_SoRoundorgViewRow;
import model.common.Round_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.ViewHelper;

import model.global.A_SoAziendaViewRowImpl;

import oracle.jbo.ApplicationModule;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.client.Configuration;

public class GestoreRound {
	public static final int DTPRIMOINVITO = 0;
	public static final int DTULTINVITO = 1;
	public static final int DTULTSOLLECITO = 2;
	
	private ApplicationModule am;
	private ViewObject ulss_vo;
	private ViewObject tpscr_vo;
	private ViewObject round_vo;
	
	public GestoreRound(ApplicationModule _am) {
		am = _am;
		
		this.round_vo = am.findViewObject("A_SoRoundorgView1");
		this.tpscr_vo = am.findViewObject("A_SoCnfTpscrView1");
		this.ulss_vo = am.findViewObject("A_SoAziendaView1");
	}
  
	/**
	 * Controlla se ci sono ancora primi inviti da effettuare per un comune
	 * @return 
	 * @param round
	 * @param codcom
	 * @param tpscr
	 * @param ulss
	 */
	public boolean checkPopulationNEW(String ulss, String tpscr, String codcom, Integer round) throws Exception {
	
		// La data di riferimento e' oggi + 1 mese
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		Date dataRif = cal.getTime();
		String dataRifString = DateUtils.dateToString(dataRif);
		
		this.ulss_vo.setWhereClause("CODAZ='" + ulss + "'");
		this.ulss_vo.executeQuery();
		A_SoAziendaViewRowImpl a = (A_SoAziendaViewRowImpl)this.ulss_vo.first();
		
		//se non trovo l'azienda allora e' un comune extra (lo vedo solo come regionale) 
		if (a == null) {
			return false;
		}
		
		//modifica per velocizzare il tutto
		this.tpscr_vo.setWhereClause("CODSCR = '" + tpscr + "'");
		this.tpscr_vo.executeQuery();
		A_SoCnfTpscrViewRow t = (A_SoCnfTpscrViewRow)this.tpscr_vo.first();

		String where = "SELECT count(*) quanti\n" +
			"FROM SO_SOGGETTO Round_SoSoggetto,\n" +
			"SO_INVITO Round_SoInvito,\n" +
			"SO_ROUNDORG Round_SoRoundorg,\n" +
			"SO_CNF_ANAG_SCR Round_SoCnfAnagScr,\n" +
			"SO_SOGG_SCR Round_SoSoggScr\n" +
			"WHERE\n" +
			// Soggetto
			"Round_SoSoggetto.ULSS = '" + ulss + "'\n" +
			"AND DATA_NASCITA > add_months(TO_DATE('" + dataRifString + "', '" + DateUtils.DATE_PATTERN + "'), -(" +
			ViewHelper.decodeByTpscr(tpscr, a.getEtacitomax(), a.getEtacolonmax(), a.getEtamammomax(), a.getEtacardiomax(), a.getEtapfasmax()) + " + 1) * 12)\n" +
			"AND DATA_NASCITA < add_months(TO_DATE('" + dataRifString + "', '" + DateUtils.DATE_PATTERN + "'), -" +
			ViewHelper.decodeByTpscr(tpscr, a.getEtacitomin(), a.getEtacolonmin(), a.getEtamammomin(), a.getEtacardiomin(), a.getEtapfasmin()) + " * 12)\n" +
			"AND Round_SoSoggetto.CODTS NOT IN (\n" +
			"SELECT ROUND_SOESCLUSIONE.CODTS\n" +
			"FROM SO_ESCLUSIONE ROUND_SOESCLUSIONE,\n" +
			"SO_CNF_ESCLUSIONE ROUND_SOCNFESCLUSIONE\n" +
			"WHERE ROUND_SOESCLUSIONE.IDCNFESCL = ROUND_SOCNFESCLUSIONE.IDCNFESCL\n" +
			"AND ROUND_SOESCLUSIONE.ULSS = ROUND_SOCNfESCLUSIONE.ULSS\n" +
			"AND ROUND_SOESCLUSIONE.TPSCR = ROUND_SOCNFESCLUSIONE.TPSCR\n" +
			"AND ROUND_SOESCLUSIONE.TPSCR = '" + tpscr + "'\n" +
			"AND ROUND_SOESCLUSIONE.ULSS = '" + ulss + "'\n" +
			"AND DTANNULL IS NULL\n" +
			"AND DTESCL <= to_date('" + dataRifString + "', '" + DateUtils.DATE_PATTERN + "')\n" +
			"AND (TPESCL = 'D' OR (TPESCL = 'T' AND DTESCL + NUMGIORNI >= to_date('" + dataRifString + "', '" + DateUtils.DATE_PATTERN + "'))))\n" +
			// Invito
			"AND Round_SoInvito.CODTS(+) = Round_SoSoggetto.CODTS\n" +
			"AND Round_SoInvito.ULSS(+) = Round_SoSoggetto.ULSS\n" +
			"AND Round_SoInvito.tpscr(+) = '" + tpscr + "'\n" +
			"AND Round_SoInvito.attivo(+) = 1\n" +
			// Roundorg
			"AND Round_SoRoundorg.RELEASE_CODE_COM(+) = DECODE(Round_SoSoggetto.CODANAGREG, " + ConfigurationConstants.CODICE_DOMICILIATO + ", Round_SoSoggetto.RELEASE_CODE_COM_DOM, Round_SoSoggetto.RELEASE_CODE_COM_RES)\n" +
			"AND Round_SoRoundorg.dtfine IS NULL\n" +
			"AND Round_SoRoundorg.CODSCR = '" + tpscr + "'\n" +
			"AND Round_SoRoundorg.CODCOM = '" + codcom + "'\n" +
			// Cnf Anagr Scr
			"AND Round_SoCnfAnagScr.CODANAGREG = Round_SoSoggetto.CODANAGREG\n" +
			"AND Round_SoCnfAnagScr.ULSS = Round_SoSoggetto.ULSS\n" +
			"AND Round_SoCnfAnagScr.incluso = 1\n" +
			"AND Round_SoCnfAnagScr.tpscr = '" + tpscr + "'\n" +
			// Soggetto Scr
			"AND Round_SoSoggScr.CODTS(+) = Round_SoSoggetto.CODTS\n" +
			"AND Round_SoSoggScr.ULSS(+) = Round_SoSoggetto.ULSS\n" +
			"AND Round_SoSoggScr.TPSCR(+) = '" + tpscr + "'\n" +
			"AND ((Round_SoInvito.IDINVITO IS NULL\n" +
			"AND Round_SoSoggScr.DTRICHIAMO IS NULL)\n" +
			"OR ( Round_SoSoggScr.DTRICHIAMO < TO_DATE('" + dataRifString + "', '" + DateUtils.DATE_PATTERN + "')\n" +
			"AND Round_SoSoggScr.TPRICHIAMO ='" + ConfigurationConstants.CODICE_PRIMO_INVITO + "'))\n" +
			"AND ( Round_SoRoundorg.RELEASE_CODE_COM <> DECODE(Round_SoInvito.statoanag, " + ConfigurationConstants.CODICE_DOMICILIATO + ", Round_SoInvito.RELEASE_CODE_COM_DOM, Round_SoInvito.RELEASE_CODE_COM_RES)\n" +
			"OR Round_SoInvito.ROUNDCOMUNE IS NULL\n" +
			"OR Round_SoInvito.ROUNDCOMUNE < Round_SoRoundorg.NUMROUND)\n" +
			"AND ROWNUM <= 1\n";

		String s = t.getSesso();
		if (s != null && s.trim().length() > 0) {
			where += "AND Round_SoSoggetto.SESSO = '" + s + "'";
		}
		
		//System.out.println("checkPopulation sql=" + where);
		
		ViewObject vo = am.createViewObjectFromQueryStmt("da_invitare", where);
		boolean b = "1".equals(vo.first().getAttribute(0).toString());
		vo.remove();
		
		return b;
}

	/**
	 * Modifica un dato del round attivo per il comune e lo screening considerati.
	 * @throws java.lang.Exception
	 * @param attribute attributo da modificare (puo' essere DTPRIMOINVITO, DTULTINVITO
	 * oppure DTULTSOLLECITO)
	 * @param data data oracle da attribuire all'attributo
	 * @param comuneId codice comune
	 * @param tpscr tipo di screning
	 */
	public void updateRoundData( String tpscr,
		Integer comuneId,
		oracle.jbo.domain.Date data,
		int attribute) throws Exception
	{
		//seleziono IL ROUND ATTIVO DEL COMUNE RICHIESTO
		String where = "CODSCR = '" + tpscr + "' AND RELEASE_CODE_COM = " + comuneId + " AND NOT(DTINIZIO IS NULL) AND DTFINE IS NULL";
		this.round_vo.setWhereClause(where);
		this.round_vo.executeQuery();
		A_SoRoundorgViewRow r = (A_SoRoundorgViewRow)this.round_vo.first();
		if (r == null) {
			throw new Exception("Nessun round attivo trovato per il comune con codice "+comuneId);
		}
		
		switch (attribute) {
			//il primo invito si imposta solo se non c'e' gia'
			case DTPRIMOINVITO:
			{
				if (r.getDtprimoinvito() == null) {
					r.setDtprimoinvito(data);
					am.getTransaction().commit();
				}
				break;
			}
			//l'ultimo invito si imposta se la data e' maggiore di quella presente
			case DTULTINVITO:
			{
				if (r.getDtultinvito() == null || r.getDtultinvito().dateValue().before(data.dateValue())) {
					r.setDtultinvito(data);
					am.getTransaction().commit();
				}
				break;
			}
			//l'ultimo invito si imposta se la data e' maggiore di quella presente
			case DTULTSOLLECITO:
			{
				if (r.getDtultsollecito() == null || r.getDtultsollecito().dateValue().before(data.dateValue())) {
					r.setDtultsollecito(data);
					am.getTransaction().commit();
				}
				break;
			}
		}
	}
	
	/**
	 * Costruisce la query di base considerando anche il join con le esclusioni
	 * @throws java.lang.Exception
	 * @return 
	 * @param eta_max
	 * @param eta_min
	 * @param data_eta
	 * @param data
	 * @param anagScr
	 * @param esclusioni
	 * @param cnfTpscr
	 * @param tpscr
	 * @param ulss
	 */
	public static String buildPrimiInvitiQueryNEW(String tpscr,
		ViewObject cnfTpscr,
		Date data_eta,
		int eta_min, int eta_max,
                Date data_nascita_da, Date data_nascita_a) throws Exception
	{
		String where = "";

		Row[] cnf_scr = cnfTpscr.getFilteredRows("Codscr", tpscr);
		if (cnf_scr.length == 0) {
			throw new Exception("Configurazione di screening non trovata per lo screening in uso");
		}
		if (cnf_scr[0].getAttribute("Sesso") != null) {
			where += "SESSO = '" + cnf_scr[0].getAttribute("Sesso") + "' AND ";
		}
                
                if (tpscr.equals("CI")||tpscr.equals("MA")||tpscr.equals("CO")){
                    String dateStr = new SimpleDateFormat(DateUtils.DATE_PATTERN).format(data_eta);
                      // seleziono chi alla data di riferimento non ha ancora compiuto l'eta' massima + 1 anno
                      where += "trunc(DATA_NASCITA) > add_months(TO_DATE('" + dateStr + "', '" + DateUtils.DATE_PATTERN + "'), -" + (eta_max + 1) + " * 12) AND ";
                      
                      // seleziono chi alla data di riferimento ha gia' compiuto l'eta' minima
                      where += "trunc(DATA_NASCITA) <= add_months(TO_DATE('" + dateStr + "', '" + DateUtils.DATE_PATTERN + "'), -" + eta_min + " * 12) AND ";
	        } else 
	        {
                    
                      //la chiamata di basa sulla data di nascita e non sull'eta'
                      where += "trunc(DATA_NASCITA) >= TO_DATE('" + new SimpleDateFormat(DateUtils.DATE_PATTERN).format(data_nascita_da) + "', '" + DateUtils.DATE_PATTERN + "') AND ";
                      where += "trunc(DATA_NASCITA) <= TO_DATE('" + new SimpleDateFormat(DateUtils.DATE_PATTERN).format(data_nascita_a) + "', '" + DateUtils.DATE_PATTERN + "') AND ";
	        }
	
		return where;
	}
}