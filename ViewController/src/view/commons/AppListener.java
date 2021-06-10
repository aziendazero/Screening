package view.commons;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.Scheduler;

public class AppListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        AppInitializer.getInstance();
    }

    public void contextDestroyed(ServletContextEvent event) {
        AppInitializer ai = AppInitializer.getInstance();
        if (ai != null) {
            try {
                Scheduler scheduler = ai.getScheduler();
                // Attendo la fine dei processi prima di chiudere lo scheduler
                if (scheduler != null) {
                    scheduler.shutdown(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

