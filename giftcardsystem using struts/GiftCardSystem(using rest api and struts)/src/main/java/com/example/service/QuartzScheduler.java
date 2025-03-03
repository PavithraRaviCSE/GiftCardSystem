package com.example.service;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import com.example.jobs.job1;

public class QuartzScheduler {

	public static void jobexecution() {
		try {
			JobKey jobKey = new JobKey("defaultScheduler", "group");
			JobDetail jobDetail = JobBuilder.newJob(job1.class).usingJobData("message", "jobdatail1.....")
					.withIdentity(jobKey).build();

			JobDetail jobDetail2 = JobBuilder.newJob(job1.class).withIdentity("customJob", "group1")
					.usingJobData("message", "Jobdetail2....").build();

			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger", "group")
					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever())
					.build();

			Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("trigger2", "group1").startNow().build();
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(jobDetail, trigger);

			scheduler.scheduleJob(jobDetail2, trigger2);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
