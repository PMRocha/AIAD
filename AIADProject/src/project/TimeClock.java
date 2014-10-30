package project;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



	public class TimeClock {
		
		static int timeEpooch=1420070400; 
		
		/*
		import java.util.concurrent.Executors;
		import java.util.concurrent.ScheduledExecutorService;
		import java.util.concurrent.TimeUnit;
		
		ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
		exec.scheduleAtFixedRate(new Runnable() {
		  @Override
		  public void run() {
			  TimeClock.timeEpooch+=3600;
			  System.out.println(TimeClock.timeEpooch);
			  if(TimeClock.timeEpooch == 1420102800)
				  exec.shutdown();
		  }
		}, 0, 1, TimeUnit.SECONDS);
		 */
	  
}
