package com.example.jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class job1 implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap(); // Get merged data

        String message = dataMap.getString("message"); // Now includes Trigger's JobDataMap
        System.out.println("Message: " + message);
    }
}
