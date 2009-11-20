package moten.david.util.tv.servlet;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

public class SchedulerServletContextListener implements ServletContextListener {

	private static Logger log = Logger
			.getLogger(SchedulerServletContextListener.class.getName());
	private Scheduler scheduler;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
			scheduler = schedulerFactory.getScheduler();
			scheduler.start();
			JobDetail jobDetail = new JobDetail("updaterJob", null,
					UpdaterJob.class);
			// run at 04:43 every day
			Trigger trigger = TriggerUtils.makeDailyTrigger("updaterTrigger",
					4, 43);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (RuntimeException e) {
			log.severe(e.getMessage());
			throw e;
		} catch (SchedulerException e) {
			log.severe(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			if (scheduler != null)
				scheduler.shutdown();
		} catch (SchedulerException e) {
			log.severe(e.getMessage());
			e.printStackTrace();
		}
	}

}
