package view.commons;

import java.util.Calendar;

import java.util.HashMap;

import model.common.ImpExp_AppModule;

import model.common.Parent_AppModule;
import model.common.Sched_AppModule;

import model.commons.ConfigurationConstants;
import model.commons.SchedUtils;

import model.impexp.common.Impexp_SoCnfImpexpViewRow;

import model.sched.common.Sched_SoCodaProcessiViewRow;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.domain.Date;
import oracle.jbo.client.Configuration;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import view.thread.ImportAnagrafeSched;
import view.thread.ImportPresenzeSched;


public class DaemonJob implements Job {
	public void execute(JobExecutionContext cntxt) throws JobExecutionException {
		
		System.out.println("attivazione job quartz - " + SchedUtils.getServerTime());
		
		Sched_AppModule am = null;
		
		try {
			boolean epsAbilitata = SchedUtils.epsAbilitata();
			if (!epsAbilitata) return;
			
			am = (Sched_AppModule)Configuration.createRootApplicationModule("model.Sched_AppModule", "Sched_AppModuleLocal");
			
			ViewObject voRun = am.findViewObject("Sched_RunningProcessesView");
			int numRunning = voRun.getRowCount();
			
			//20110412 Serra: se un task e' in esecuzione da 1 ora ne lancio un altro comunque
			if (numRunning > 0) {
				
				Date d = (Date)voRun.first().getAttribute("InizioEsecuzione");
	
				//leggo l'ora di inizio e ci aggiungo un'ora
				Calendar c_inizio = Calendar.getInstance();
				c_inizio.setTime(d.getValue());
				c_inizio.add(Calendar.HOUR_OF_DAY, 1);
				
				//se non e' ancora trascorsa un'ora, allora non faccio niente
				if (c_inizio.getTime().after(new java.util.Date())) {
					//if (voRun != null) voRun.remove();
					return;
				}
	
				//in caso contrario lancio comunque un secondo thread
				System.out.println("E' trascorsa piu' di un'ora e il processo " + voRun.getCurrentRow().getAttribute(0) + " e' ancora in esecuzione");
			}
			//fine 20110412
			
			ViewObject voCoda = am.findViewObject("Sched_ReadCodaProcessiView1");
			
			//20120120 S.Gaion: filtro su data schedulazione fino ad oggi
			voCoda.setWhereClause("DATA_SCHEDULAZIONE <= sysdate ");
			voCoda.executeQuery();
			
			int numWaiting = voCoda.getRowCount();
			
			if (numWaiting == 0) {
				//if (voCoda != null) voCoda.remove();
				return;
			}
			
			// metto in esecuzione il primo processo
			System.out.println("in esecuzione");
			
			Sched_SoCodaProcessiViewRow fProcess = (Sched_SoCodaProcessiViewRow)voCoda.first();
			
			String tipoProcesso = fProcess.getTipoProcesso();
			String ulss = fProcess.getUlss();
			String tpscr = fProcess.getTpscr();
			int pid = fProcess.getPid().intValue(); 
			
			if (tipoProcesso.equals("ANA")) {
				//20120120 S.Gaion: solo un import per giorno
				//controllo che non siano gia' stati eseguiti (conclusi o che stanno girando) altri processi per oggi
				ViewObject voCompletati = am.findViewObject("Sched_TodayRunProcessesView1");
				
				voCompletati.setWhereClause("ULSS = '" + ulss + "'");
				voCompletati.executeQuery();
				
				int numCompleted = voCompletati.getRowCount();
				if (numCompleted > 0) {
					//lo rischedulo per il giorno dopo a mezzanotte
					Calendar c_ora = Calendar.getInstance();
					c_ora.add(Calendar.DATE, 1);    
					c_ora.set(Calendar.HOUR_OF_DAY, 0);
					
					SchedUtils.rescheduleProcess(pid, am, c_ora.getTime());
					//if (voCompletati != null) voCompletati.remove();
					return;
				}
				// fine 20120120
				
				// posso rilasciare schedAM
				if (am != null) Configuration.releaseRootApplicationModule(am, true);
				am = null;
				
				// import anagrafe
				importAnagrafe(pid, ulss, tpscr);
			} else {
				//02072013 Gaion : import presenze automatico
				//import presenze
				if (tipoProcesso.equals("ACC")) {
					importPresenze(pid, ulss, tpscr);
				}
				
				// Verifico se ci sono processi ricorrenti da schedulare (solo per l'import presenze)
				ViewObject processiDaSchedulare = am.findViewObject("Sched_SoCnfImpexpDaSchedulareView1");
				int numProc = processiDaSchedulare.getRowCount();
				if (numProc > 0) {
					while (processiDaSchedulare.hasNext()) {
						Row processo = processiDaSchedulare.next();

						// Recupero ore e minuti di schedulazione
						int orario = ((Integer)processo.getAttribute("Orasched")).intValue();
						int ore = orario / 100;
						int minuti = orario - (ore * 100);
						
						// Calcolo la data di esecuzione aggiungendo un giorno ad oggi ed impostando ore e minuti
						// Lascio la frazione di minuto attuale, per una partenza quasi casuale nell'arco di un minuto
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DATE, 1);
						cal.set(Calendar.HOUR_OF_DAY, ore);
						cal.set(Calendar.MINUTE, minuti);
						
						// Creo una nuova schedulazione
						SchedUtils.scheduleTimeProcess((Parent_AppModule)am, "ACC", ulss, tpscr, "DAEMON", cal.getTime());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			if (am != null) Configuration.releaseRootApplicationModule(am, true);
		}
	}

	private void importAnagrafe(int pid, String ulss, String tpscr) {
		
		System.out.println("importAnagrafe");
		
		ImpExp_AppModule am = (ImpExp_AppModule)Configuration.createRootApplicationModule("model.ImpExp_AppModule", "ImpExp_AppModuleLocal");
		
		try {
			SchedUtils.setProcessRunning(pid, am);
			
			ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
			
			String whcl = "TPDIP = 'AN' and TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
			vo.setWhereClause(whcl);
			vo.executeQuery();
			
			Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow)vo.first();
			
			if (r == null) {
				String msg = "Non e' stata selezionata nessuna ulss oppure la ulss selezionata non presenta i dati di configurazione per eseguire tale operazione.";        
				am.log(ulss, r.getTpdip(), ConfigurationConstants.IMPEXP_LOC,msg, tpscr);
				am.log(ulss, r.getTpdip(), ConfigurationConstants.IMPEXP_LOC,
					"IMPORT DATI ANAGRAFICI INTERROTTO PER ERRORI. Operazione chiusa.", tpscr);
	
				SchedUtils.setProcessAborted(pid, am, msg);
			
				return;
			}
	
			HashMap errors = new HashMap();
			ImportAnagrafeSched t = new ImportAnagrafeSched(r.getKey(), errors, pid);
			
			if (!errors.isEmpty()) {
				t = null;
				
				String msg1 = (String)errors.get("error");
				am.log(ulss, r.getTpdip(), ConfigurationConstants.IMPEXP_LOC, msg1, tpscr);
				am.log(ulss, r.getTpdip(), ConfigurationConstants.IMPEXP_LOC,
					"IMPORT DATI ANAGRAFICI INTERROTTO PER ERRORI. Operazione chiusa.", tpscr);
				
				//String msg2 = "Impossibile attivare l'aggiornamento dei dati anagrafici: "+ msg1;
				
				SchedUtils.setProcessAborted(pid, am, msg1);
				
				return;
			}
			
			//altrimenti faccio partire il thread
			t.start();
		} catch (Exception e) {
			SchedUtils.setProcessAborted(pid, am, "Eccezione in schedulazione import anagrafe: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (am != null) Configuration.releaseRootApplicationModule(am, true);
		}
	}

	/**
	 * Metodo che fa partire il thread per l'import presenze
	 */
	private void importPresenze(int pid, String ulss, String tpscr) {
		System.out.println("importPresenze");
		
		ImpExp_AppModule am = (ImpExp_AppModule)Configuration.createRootApplicationModule("model.ImpExp_AppModule", "ImpExp_AppModuleLocal");
		
		try {
			SchedUtils.setProcessRunning(pid, am);
			
			// cerco la configurazione adatta
			ViewObject vo = am.findViewObject("Impexp_SoCnfImpexpView1");
			String whcl = "TPDIP = 'AC' and TPSCR = '" + tpscr + "' and ULSS = '" + ulss + "'";
			vo.setWhereClause(whcl);
			vo.executeQuery();
			
			Impexp_SoCnfImpexpViewRow r = (Impexp_SoCnfImpexpViewRow)vo.first();
			
			if (r == null) {
				String msg = "Non e' stata selezionata nessuna ulss oppure la ulss selezionata non presenta i dati di configurazione per eseguire tale operazione.";              
				SchedUtils.setProcessAborted(pid, am, msg);
				
				return;
			}
			
			HashMap errors = new HashMap();
			ImportPresenzeSched t = new ImportPresenzeSched(r.getKey(), errors, pid);
			
			if (!errors.isEmpty()) {
				t = null;
				
				String msg1 = (String)errors.get("error");       
				
				SchedUtils.setProcessAborted(pid, am, msg1);
				
				return;
			}
			
			//altrimenti faccio partire il thread
			t.start();
		} catch (Exception e) {
			SchedUtils.setProcessAborted(pid, am, "Eccezione in schedulazione import presenze: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (am != null) Configuration.releaseRootApplicationModule(am, true);
		}
	}
}

