package model.sched;

import model.Sched_AppModuleImpl;

import oracle.adf.share.logging.ADFLogger;

import oracle.jbo.Key;
import oracle.jbo.client.Configuration;
import oracle.jbo.server.ViewObjectImpl;


public abstract class AbstractProcess extends Thread {
	private static ADFLogger logger = ADFLogger.createADFLogger(AbstractProcess.class);
	private int pid;

	public AbstractProcess(int pid) {
		this.pid = pid;
	}

	protected abstract void execute() throws Exception;

	@Override
	public void run() {
		Sched_AppModuleImpl am = (Sched_AppModuleImpl)Configuration.createRootApplicationModule("model.Sched_AppModule", "Sched_AppModuleLocal");
		Sched_SoCodaProcessiViewRowImpl processo = null;
		try {
			// Leggo la riga di questo processo
			ViewObjectImpl processoView = (ViewObjectImpl)am.findViewObject("Sched_ScheduledNowProcessesView");
			processo = (Sched_SoCodaProcessiViewRowImpl)processoView.getRow(new Key(new Object[] { new Long(pid) }));
			if (processo == null) {
				String msg = "Il processo " + pid + " non è schedulato";
				logger.severe(msg);
				throw new Exception(msg);
			}

			// Imposto lo stato a running
			processo.setStato(Sched_SoCodaProcessiImpl.STATO_RUNNING);
			am.getTransaction().commit();
			
			// Eseguo il processo
			logger.fine("Lanciato processo " + pid);
			execute();
			logger.fine("Processo " + pid + " terminato");
			
			// Imposto lo stato a completed
			processo.setStato(Sched_SoCodaProcessiImpl.STATO_COMPLETED);
		} catch (Exception e) {
                        e.printStackTrace();
			am.getTransaction().rollback();
			if (processo != null) {
				processo.setStato(Sched_SoCodaProcessiImpl.STATO_ABORTED);
				processo.setErrorMessage(e.getMessage());
			}
		} finally {
			if (am != null) {
			    am.getTransaction().commit();
				Configuration.releaseRootApplicationModule(am, true);
			}
		}
	}
	
	public int getPid() {
		return pid;
	}
}
