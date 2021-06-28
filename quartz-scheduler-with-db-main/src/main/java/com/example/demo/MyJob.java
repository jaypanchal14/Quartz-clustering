package com.example.demo;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class MyJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		
		String jobKeyName = arg0.getJobDetail().getKey().getName();
		MyDao dao = null;
		try {
			dao = (MyDao)arg0.getScheduler().getContext().get("dao");
			
			if(dao == null){
				dao = DemoApplication.getMyDaoStaticRef();
			}
			
			//dao = (MyDao)arg0.getMergedJobDataMap().get(jobKeyName);
			String fireTime = arg0.getFireTime().toString();
			String nextFireTime = arg0.getNextFireTime().toString();
			
			boolean done = dao.update(jobKeyName, fireTime, nextFireTime);
			if(done){
				System.out.println(jobKeyName+"  : fire time : "+fireTime+"    nextFireTime : "+nextFireTime+" updated successfully.");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
