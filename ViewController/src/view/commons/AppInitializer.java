package view.commons;

import java.util.Map;

import model.commons.ConfigurationReader;

import model.commons.SchedUtils;

import oracle.adf.share.ADFContext;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.cronSchedule;

// classe singleton per operazioni di inizializzazione app
public class AppInitializer {
    private static AppInitializer INSTANCE = null;
    private static boolean initState = false;
    private static Scheduler sched = null;

    // Private constructor prevents instantiation from other classes
    private AppInitializer() {
        try {
            scheduleQuartzDaemon();
            initState = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void scheduleQuartzDaemon() throws Exception {

        String demoneQuartz = ConfigurationReader.readProperty("demoneQuartz");

        if (demoneQuartz == null || demoneQuartz.trim().isEmpty() || demoneQuartz.equals("1")) {

            String impAnagManuale = ConfigurationReader.readProperty("impAnagManuale");
            boolean isAnag = impAnagManuale == null || impAnagManuale.trim().isEmpty() || impAnagManuale.equals("0");

            if (isAnag) {

                SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
                sched = schedFact.getScheduler();

                JobDetail jobDetail = newJob(DaemonJob.class).withIdentity("quartz_job", "quartz_job").build();

                String periodo = ConfigurationReader.readProperty("impAnagPeriodo");
                String cronExp = "0 0/5 * * * ?"; // default

                if (periodo != null) {
                    if (periodo.equals("1") || periodo.equals("2") || periodo.equals("3") || periodo.equals("10")) {
                        cronExp = "0 0/" + periodo + " * * * ?";
                    }
                }

                CronTrigger trigger =
                    newTrigger().
                        withIdentity("quartz_trigger", "quartz_trigger").
                        withSchedule(cronSchedule(cronExp)).
                        build();

                sched.scheduleJob(jobDetail, trigger);
            }
        }
        
        if (sched != null) {
            sched.start();
            System.out.println("schedulato demone quartz - " + SchedUtils.getServerTime());
        }

    }

    public static AppInitializer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppInitializer();
        }
        return INSTANCE;
    }

    public static boolean isInitState() {
        return initState;
    }

    public Scheduler getScheduler() {
        return sched;
    }
}
