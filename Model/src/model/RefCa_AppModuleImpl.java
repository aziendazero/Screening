package model;

import com.lowagie.text.Font;
import com.lowagie.text.Image;

import java.sql.Timestamp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import model.commons.DomandaQuestionario;
import model.commons.Parent_AppModuleImpl;
import model.commons.Scelta;

import model.conf.Cnf_SoCnfLetteracentroViewImpl;
import model.conf.Cnf_SoTemplateViewImpl;

import model.global.A_SoAziendaViewImpl;
import model.global.A_SoCnfParametriSistemaViewImpl;
import model.global.A_SoSoggettoViewImpl;

import model.referto.RefCa_SoCnfQuestDomandeViewImpl;
import model.referto.RefCa_SoCnfQuestionarioViewImpl;
import model.referto.RefCa_SoDomandeQuestionarioViewImpl;
import model.referto.RefCa_SoDomandeQuestionarioViewRowImpl;
import model.referto.RefCa_SoOpmedicoRilevatoreViewImpl;
import model.referto.RefCa_SoRefcardioDatiViewImpl;
import model.referto.RefCa_SoRefertocardioBckViewImpl;
import model.referto.RefCa_SoRefertocardioViewImpl;
import model.referto.RefCa_SoRefertocardioViewRowImpl;
import model.referto.Ref_SelTipiDocViewImpl;
import model.referto.Ref_SoAllegatoViewImpl;
import model.referto.Ref_SoCentroPrelievoViewImpl;
import model.referto.Ref_SoCnfRef1livViewImpl;
import model.referto.Ref_SoCnfRef1livViewRowImpl;
import model.referto.Ref_SoCnfSugg1livViewImpl;
import model.referto.Ref_SoCnfTpinvitoViewImpl;
import model.referto.Ref_SoInvitiPerRefertiCAViewImpl;
import model.referto.Ref_SoInvitiPerRefertiCAViewRowImpl;
import model.referto.Ref_SoInvitoViewImpl;
import model.referto.Ref_SoOpmedicoViewImpl;
import model.referto.Ref_SoRefertopfasDatiViewImpl;
import model.referto.Ref_SoSuggerimentiViewImpl;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.server.ApplicationModuleImpl;
import oracle.jbo.server.ViewLinkImpl;
import oracle.jbo.server.ViewObjectImpl;

//  ---------------------------------------------------------------------
//  ---    File generated by Oracle ADF Business Components Design Time.
//  ---    Custom code may be added to this class.
//  ---    Warning: Do not modify method signatures of generated methods.
//  ---------------------------------------------------------------------

public class RefCa_AppModuleImpl extends Parent_AppModuleImpl implements model.RefCa_AppModule  {
	private static final Logger logger = Logger.getLogger(RefCa_AppModuleImpl.class.getName());
	
	private static final String ALGORITMO_CA1 = "A01";
	
	private static final Font FONT_INTESTAZIONE = new Font(Font.HELVETICA, 16, Font.BOLD);
	private static final Font FONT_TITOLO_QUESTIONARIO = new Font(Font.HELVETICA, 12, Font.BOLD);
	private static final Font FONT_TITOLO_SEZIONE = new Font(Font.HELVETICA, 12, Font.BOLD);
	private static final Font FONT_TITOLO_SCELTA = new Font(Font.HELVETICA, 10, Font.BOLD);
	private static final Font FONT_DOMANDA = new Font(Font.HELVETICA, 8, Font.NORMAL);
	private static final Font FONT_SPAZIO = new Font(Font.HELVETICA, 8, Font.NORMAL);
	private static final Font FONT_RISPOSTA = new Font(Font.HELVETICA, 10, Font.BOLD);
	private static final Font FONT_NOTE = new Font(Font.HELVETICA, 8, Font.ITALIC);
	private static final Font FONT_FOOTER = new Font(Font.HELVETICA, 8, Font.NORMAL);
	private static final float TABLE_WIDTH = 530;
	private static Image imgUnchecked = null;
	private static Image imgChecked = null;
	private static final NumberFormat numberFormat = new DecimalFormat("####.###", new DecimalFormatSymbols(Locale.ITALY));


	
	/**
	 * 
	 *  This is the default constructor (do not remove)
	 */
	public RefCa_AppModuleImpl() {
	}

	/**
	 * 
	 *  Container's getter for Ref_SoCentroPrelievoView1
	 */
	public Ref_SoCentroPrelievoViewImpl getRef_SoCentroPrelievoView1() {
		return (Ref_SoCentroPrelievoViewImpl)findViewObject("Ref_SoCentroPrelievoView1");
	}

	/**
	 * 
	 *  Sample main for debugging Business Components code using the tester.
	 */
	public static void main(String[] args) {
		launchTester("model", "RefCa_AppModuleLocal");
	}


	
	public void doRollback() {
		//questionario = null;
		super.doRollback();
	}
	
	public void doRollback(String voName) {
		//questionario = null;
		super.doRollback(voName);
	}

	public void doRollback(String voName1,String voName2) {
		//questionario = null;
		super.doRollback(voName1, voName2);
	}

	public void doRollback(String[] voNames) {
		//questionario = null;
		super.doRollback(voNames);
	}
	
	public void filterConfiguration(String ulss, String scr, String codregionale, boolean regionale) {
		Object[] pars = {ulss, scr};
		ViewObject vo;
		String whcl;
		
		whcl = "ULSS = '" + ulss + "'";
		if (!regionale) {
			whcl += " AND TPSCR = '" + scr + "'";
		}

		// Centri
		vo = getRef_SoCentroPrelievoView1();
		vo.setWhereClause(whcl);
		vo.executeQuery();
		
		// Suggerimenti
		vo = getRef_SoCnfSugg1livView1();
		vo.setWhereClause(whcl);
		vo.executeQuery();
		vo = getRef_SoSuggerimentiView1();
		vo.setWhereClause(whcl);
		vo.executeQuery();
    
    //tipi documenti
    vo = this.getRef_SelTipiDocView1();
    vo.setWhereClauseParams(new Object[]{ulss});
    vo.executeQuery();
		
		// Inviti per referti
		vo = getRef_SoInvitiPerRefertiCAView1();
		vo.setWhereClauseParams(pars);
		vo.setWhereClause("1=2");
		vo.executeQuery();
		
		// Questionario
		vo = getRefCa_SoCnfQuestionarioView1();
		vo.setWhereClause("1=2");
		vo.executeQuery();
		
		// Medico operatore
		vo = getRefCa_SoOpmedicoRilevatoreView1();
		vo.setWhereClause("1=2");
		vo.executeQuery();
		
		// Backup referto
		vo = getRefCa_SoRefertocardioBckView1();
		vo.setWhereClause("1=2");
		vo.executeQuery();
		
		// Domande referto
		vo = getRefCa_SoDomandeQuestionarioView1();
		vo.setWhereClause("1=2");
		vo.executeQuery();
		
		// Dati referto
		vo = getRefCa_SoRefcardioDatiView1();
		vo.setWhereClause("1=2");
		vo.executeQuery();
		
		// Questionario attivo
		vo = getRefCa_SoCnfQuestionarioAttivoView();
		vo.setWhereClause("RefCa_SoCnfQuestionario.DT_FINE_VAL IS NULL OR RefCa_SoCnfQuestionario.DT_FINE_VAL > :1");
		vo.setWhereClauseParam(0, new Date(new java.sql.Date(new java.util.Date().getTime())));
		vo.executeQuery();
	}


	/**
	 * 
	 *  Container's getter for RefCa_SoDomandeQuestionarioView1
	 */
	public RefCa_SoDomandeQuestionarioViewImpl getRefCa_SoDomandeQuestionarioView1() {
		return (RefCa_SoDomandeQuestionarioViewImpl)findViewObject("RefCa_SoDomandeQuestionarioView1");
	}



	/**
	 * 
	 *  Container's getter for RefCa_SoCnfQuestionarioAttivoView
	 */
	public RefCa_SoCnfQuestionarioViewImpl getRefCa_SoCnfQuestionarioAttivoView() {
		return (RefCa_SoCnfQuestionarioViewImpl)findViewObject("RefCa_SoCnfQuestionarioAttivoView");
	}
	
	public List getScelte(String gruppo) {
		List result = new ArrayList();
		Ref_SoInvitiPerRefertiCAViewImpl invitiView = getRef_SoInvitiPerRefertiCAView1();
		Ref_SoInvitiPerRefertiCAViewRowImpl invitiRow = (Ref_SoInvitiPerRefertiCAViewRowImpl)invitiView.getCurrentRow();
		Ref_SoCnfRef1livViewImpl cnfRefView = (Ref_SoCnfRef1livViewImpl)getRef_SoCnfRef1livView1();
		cnfRefView.setWhereClause("ULSS = :1 AND TPSCR = :2 AND GRUPPO = :3");
		cnfRefView.setWhereClauseParams(null);
		cnfRefView.setWhereClauseParam(0, "050000"); // TODO Parametrizzare ulss regionale
		cnfRefView.setWhereClauseParam(1, invitiRow.getTpscr());
		cnfRefView.setWhereClauseParam(2, gruppo);
		cnfRefView.executeQuery();
		while (cnfRefView.hasNext()) {
			Ref_SoCnfRef1livViewRowImpl cnfRefRow = (Ref_SoCnfRef1livViewRowImpl)cnfRefView.next();
			Scelta scelta = new Scelta();
			scelta.setNome(cnfRefRow.getDescrizione());
			scelta.setValore(cnfRefRow.getIdcnfref1l().toString());
			result.add(scelta);
		}
		return result;
	}
	
	// domandeView deve avere la riga corrente che punta alla prima domanda
	private List getDomande(ViewObject domandeView) {
		List domande = new ArrayList();
		RefCa_SoDomandeQuestionarioViewRowImpl domandeRow = (RefCa_SoDomandeQuestionarioViewRowImpl)domandeView.getCurrentRow();
		
		// Identifico assenza di padre con -1
		long idPadre = domandeRow.getCapostipite() == null ? -1 : domandeRow.getCapostipite().longValue();
		String sezione = domandeRow.getSezione();
		do {
			DomandaQuestionario domanda = new DomandaQuestionario();

			domanda.setTipo(domandeRow.getTipo());
			domanda.setId(domandeRow.getIdcnfdom().longValue());
			domanda.setSezione(domandeRow.getSezione());
			domanda.setTesto(domandeRow.getDomanda());
			domanda.setValore(domandeRow.getValore());
			domanda.setDimensione(domandeRow.getLunghezza() != null ? domandeRow.getLunghezza().intValue() : 0);
			domanda.setGruppo(domandeRow.getGruppo());
			domanda.setModificabile(Boolean.valueOf(domandeRow.getModificabile().toString()).booleanValue());
			domanda.setObbligatorio(Boolean.valueOf(domandeRow.getObbligatorio().toString()).booleanValue());
			domanda.setNote(domandeRow.getNote());
			domanda.setCapostipite(domandeRow.getCapostipite() != null ? domandeRow.getCapostipite().longValue() : 0);
			domanda.setCodice(domandeRow.getCodiceDom());
			
			// Leggo la domanda successiva
			domandeRow = (RefCa_SoDomandeQuestionarioViewRowImpl)domandeView.next();
			
			// Gestisco i valori multipli della stessa domanda
			while (domandeRow != null && domandeRow.getIdcnfdom().longValue() == domanda.getId()
				&& domandeRow.getSezione().equals(domanda.getSezione()))
			{
				domanda.addValore(domandeRow.getValore());
				domandeRow = (RefCa_SoDomandeQuestionarioViewRowImpl)domandeView.next();
			}
			
			// Se la domanda � figlia, carico le domande in modo ricorsivo
			if (domandeRow != null &&
				domandeRow.getCapostipite() != null &&
				domandeRow.getCapostipite().longValue() == domanda.getId() &&
				domandeRow.getSezione().equals(domanda.getSezione()))
			{
				domanda.setDomande1liv(getDomande(domandeView));
				domandeRow = (RefCa_SoDomandeQuestionarioViewRowImpl)domandeView.getCurrentRow();
			}
			
			domande.add(domanda);
		} while (domandeRow != null &&
			((domandeRow.getCapostipite() == null && idPadre == -1) ||
			 (domandeRow.getCapostipite() != null && domandeRow.getCapostipite().longValue() == idPadre &&
			 domandeRow.getSezione().equals(sezione))));

		return domande;
	}

	/**
	 * 
	 *  Container's getter for Ref_SoCnfRef1livView1
	 */
	public Ref_SoCnfRef1livViewImpl getRef_SoCnfRef1livView1() {
		return (Ref_SoCnfRef1livViewImpl)findViewObject("Ref_SoCnfRef1livView1");
	}

	

	/**
	 * 
	 *  Container's getter for RefCa_SoRefcardioDatiView1
	 */
	public RefCa_SoRefcardioDatiViewImpl getRefCa_SoRefcardioDatiView1() {
		return (RefCa_SoRefcardioDatiViewImpl)findViewObject("RefCa_SoRefcardioDatiView1");
	}




	/**
	 * 
	 *  Container's getter for Sogg_AppModule1
	 */
	public ApplicationModuleImpl getSogg_AppModule1() {
    return (ApplicationModuleImpl)findApplicationModule("Sogg_AppModule1");
	}

	/**
	 * 
	 *  Container's getter for Cnf_AppModule1
	 */
	public ApplicationModuleImpl getCnf_AppModule1() {
    return (ApplicationModuleImpl)findApplicationModule("Cnf_AppModule1");
	}

	
	
	public void filtraMedici() {
		Ref_SoInvitiPerRefertiCAViewRowImpl invitoRow = (Ref_SoInvitiPerRefertiCAViewRowImpl)getRef_SoInvitiPerRefertiCAView1().getCurrentRow();
		RefCa_SoRefertocardioViewRowImpl refertoRow = (RefCa_SoRefertocardioViewRowImpl)getRefCa_SoRefertocardioView1().first();

		ViewObject mediciView = getRefCa_SoOpmedicoRilevatoreView1();
		mediciView.setWhereClause("ULSS = :1 AND TPSCR = :2 AND (DTFINEVALOPMEDICO IS NULL OR DTFINEVALOPMEDICO > :3)");
		mediciView.setWhereClauseParams(null);
		mediciView.setWhereClauseParam(0, invitoRow.getUlss());
		mediciView.setWhereClauseParam(1, invitoRow.getTpscr());
		
		if (refertoRow != null && refertoRow.getDataRilevazione() != null) {
			mediciView.setWhereClauseParam(2, refertoRow.getDataRilevazione());
		} else {
			mediciView.setWhereClauseParam(2, getOracleNow());
		}

		mediciView.executeQuery();
	}
	
	/**
	 * 
	 *  Container's getter for RefCa_SoRefertocardioBckView1
	 */
	public RefCa_SoRefertocardioBckViewImpl getRefCa_SoRefertocardioBckView1() {
		return (RefCa_SoRefertocardioBckViewImpl)findViewObject("RefCa_SoRefertocardioBckView1");
	}

	/**
	 * 
	 *  Container's getter for Cnf_SoCnfLetteracentroView1
	 */
	public Cnf_SoCnfLetteracentroViewImpl getCnf_SoCnfLetteracentroView1() {
		return (Cnf_SoCnfLetteracentroViewImpl)findViewObject("Cnf_SoCnfLetteracentroView1");
	}

	/**
	 * 
	 *  Container's getter for Cnf_SoTemplateView1
	 */
	public Cnf_SoTemplateViewImpl getCnf_SoTemplateView1() {
		return (Cnf_SoTemplateViewImpl)findViewObject("Cnf_SoTemplateView1");
	}

	/**
	 * 
	 *  Container's getter for Ref_SoAllegatoView1
	 */
	public Ref_SoAllegatoViewImpl getRef_SoAllegatoView1() {
		return (Ref_SoAllegatoViewImpl)findViewObject("Ref_SoAllegatoView1");
	}

	/**
	 * 
	 *  Container's getter for A_SoSoggettoView1
	 */
	public A_SoSoggettoViewImpl getA_SoSoggettoView1() {
		return (A_SoSoggettoViewImpl)findViewObject("A_SoSoggettoView1");
	}

	private Date getOracleNow() {
		return new Date(new Timestamp(new java.util.Date().getTime()));
	}

	/**
	 * 
	 *  Container's getter for Ref_SoCnfSugg1livView1
	 */
	public Ref_SoCnfSugg1livViewImpl getRef_SoCnfSugg1livView1() {
		return (Ref_SoCnfSugg1livViewImpl)findViewObject("Ref_SoCnfSugg1livView1");
	}

	/**
	 * 
	 *  Container's getter for Ref_SoCnfTpinvitoView1
	 */
	public Ref_SoCnfTpinvitoViewImpl getRef_SoCnfTpinvitoView1() {
		return (Ref_SoCnfTpinvitoViewImpl)findViewObject("Ref_SoCnfTpinvitoView1");
	}


	/**
	 * 
	 *  Container's getter for LetteraAppModule1
	 */
	public ApplicationModuleImpl getLetteraAppModule1() {
    return (ApplicationModuleImpl)findApplicationModule("LetteraAppModule1");
	}

	/**
	 * 
	 *  Container's getter for Ref_SoOpmedicoView1
	 */
	public Ref_SoOpmedicoViewImpl getRef_SoOpmedicoView1() {
		return (Ref_SoOpmedicoViewImpl)findViewObject("Ref_SoOpmedicoView1");
	}

	/**
	 * 
	 *  Container's getter for RefCa_SoOpmedicoRilevatoreView1
	 */
	public RefCa_SoOpmedicoRilevatoreViewImpl getRefCa_SoOpmedicoRilevatoreView1() {
		return (RefCa_SoOpmedicoRilevatoreViewImpl)findViewObject("RefCa_SoOpmedicoRilevatoreView1");
	}

	/**
	 * 
	 *  Container's getter for Ref_SoInvitiPerRefertiCAView1
	 */
	public Ref_SoInvitiPerRefertiCAViewImpl getRef_SoInvitiPerRefertiCAView1() {
		return (Ref_SoInvitiPerRefertiCAViewImpl)findViewObject("Ref_SoInvitiPerRefertiCAView1");
	}

	/**
	 * 
	 *  Container's getter for A_SoCnfParametriSistemaView1
	 */
	public A_SoCnfParametriSistemaViewImpl getA_SoCnfParametriSistemaView1() {
		return (A_SoCnfParametriSistemaViewImpl)findViewObject("A_SoCnfParametriSistemaView1");
	}

	

	public void refreshInviti() {
		ViewObject vo = getRef_SoInvitiPerRefertiCAView1();
		Row row = vo.getCurrentRow();
		Key key = row.getKey();
		vo.executeQuery();
		row = vo.getRow(key);
		vo.setCurrentRow(row);
	}

	/**
	 * 
	 *  Container's getter for A_SoAziendaView1
	 */
	public A_SoAziendaViewImpl getA_SoAziendaView1() {
		return (A_SoAziendaViewImpl)findViewObject("A_SoAziendaView1");
	}

	/**
	 * 
	 *  Container's getter for Ref_SoSuggerimentiView1
	 */
	public Ref_SoSuggerimentiViewImpl getRef_SoSuggerimentiView1() {
		return (Ref_SoSuggerimentiViewImpl)findViewObject("Ref_SoSuggerimentiView1");
	}

	/**
	 * 
	 *  Container's getter for RefCa_SoCnfQuestionarioView1
	 */
	public RefCa_SoCnfQuestionarioViewImpl getRefCa_SoCnfQuestionarioView1() {
		return (RefCa_SoCnfQuestionarioViewImpl)findViewObject("RefCa_SoCnfQuestionarioView1");
	}

	/**
	 * 
	 *  Container's getter for RefCa_SoCnfQuestDomandeView1
	 */
	public RefCa_SoCnfQuestDomandeViewImpl getRefCa_SoCnfQuestDomandeView1() {
		return (RefCa_SoCnfQuestDomandeViewImpl)findViewObject("RefCa_SoCnfQuestDomandeView1");
	}

	/**
	 * 
	 *  Container's getter for RefCa_SoCnfQuestionarioDomandeLink1
	 */
	public ViewLinkImpl getRefCa_SoCnfQuestionarioDomandeLink1() {
    return (ViewLinkImpl)findViewLink("RefCa_SoCnfQuestionarioDomandeLink1");
	}

	/**
	 * 
	 *  Container's getter for RefCa_SoOpmedicoRilevatoreView2
	 */
	public RefCa_SoOpmedicoRilevatoreViewImpl getRefCa_SoOpmedicoRilevatoreView2() {
		return (RefCa_SoOpmedicoRilevatoreViewImpl)findViewObject("RefCa_SoOpmedicoRilevatoreView2");
	}

  /**
   * 
   *  Container's getter for Ref_SelTipiDocView1
   */
  public Ref_SelTipiDocViewImpl getRef_SelTipiDocView1()
  {
    return (Ref_SelTipiDocViewImpl)findViewObject("Ref_SelTipiDocView1");
  }

  /**
   * 
   *  Container's getter for RefCa_SoRefertocardioView1
   */
  public RefCa_SoRefertocardioViewImpl getRefCa_SoRefertocardioView1()
  {
    return (RefCa_SoRefertocardioViewImpl)findViewObject("RefCa_SoRefertocardioView1");
  }

  /**
   * 
   *  Container's getter for Ref_FK_InvitiPerRefertoCA_VL1
   */
  public ViewLinkImpl getRef_FK_InvitiPerRefertoCA_VL1()
  {
    return (ViewLinkImpl)findViewLink("Ref_FK_InvitiPerRefertoCA_VL1");
  }

  /**
   * 
   *  Container's getter for Ref_SoInvitoView1
   */
  public Ref_SoInvitoViewImpl getRef_SoInvitoView1()
  {
    return (Ref_SoInvitoViewImpl)findViewObject("Ref_SoInvitoView1");
  }

  /**
   * 
   *  Container's getter for Ref_FKRefInvitiCA_VL1
   */
  public ViewLinkImpl getRef_FKRefInvitiCA_VL1()
  {
    return (ViewLinkImpl)findViewLink("Ref_FKRefInvitiCA_VL1");
  }

  /**
   * 
   *  Container's getter for Ref_SoRefertopfasDatiView1
   */
  public Ref_SoRefertopfasDatiViewImpl getRef_SoRefertopfasDatiView1()
  {
    return (Ref_SoRefertopfasDatiViewImpl)findViewObject("Ref_SoRefertopfasDatiView1");
  }


    /**
     * Container's getter for RefCa_SoRefertocardioView2.
     * @return RefCa_SoRefertocardioView2
     */
    public RefCa_SoRefertocardioViewImpl getRefCa_SoRefertocardioView2() {
        return (RefCa_SoRefertocardioViewImpl) findViewObject("RefCa_SoRefertocardioView2");
    }
}
