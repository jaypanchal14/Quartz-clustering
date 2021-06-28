package com.example.demo;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class DemoApplication {

	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	public MyDao dao;
	
	public static MyDao myDaoStaticRef;
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@GetMapping("/startScheduler")
	public String start() throws Exception{
		
		scheduler.start();
		
		return "started";
	}
	
	
	@PostMapping("/scheduleJob")
	public String schedule(@RequestBody ScheduleBean bean) throws Exception{
		ScheduleDTO dto = dao.getScheduleDTO(bean.getName());
		scheduleMyJob(dto.getName(), dto.getCron());
		
		return "starteded successfully.";
	}
	
	
	@PostMapping("/addScheduleDetail")
	public String addScheduleDetail(@RequestBody ScheduleBean bean) throws Exception{
		
		boolean flag = dao.save(bean.getName(), bean.getCron());
		
		if(!flag){
			return "error while adding";
		}
		
		return "added";
	}
	
	void scheduleMyJob(String name, String cron) throws Exception{ 
		JobKey key = new JobKey(name);
		
		
		
		JobDetail job1 = JobBuilder.newJob(MyJob.class)
				.withIdentity(key).build();
		//job1.getJobDataMap().put("dao", dao);
		scheduler.getContext().put("dao", dao);
		
        Trigger trigger1 = TriggerBuilder.newTrigger()
                .withIdentity(name, "dummygroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        scheduler.scheduleJob(job1, trigger1);
        
	}
	
	
	@PostConstruct
	public void setDAO(){
		myDaoStaticRef = dao; 
	}
	
	public static MyDao getMyDaoStaticRef(){
		return myDaoStaticRef;
	}
}
