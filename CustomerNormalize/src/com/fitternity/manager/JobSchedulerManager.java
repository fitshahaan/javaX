package com.fitternity.manager;

import static com.fitternity.enums.Jobs.NORMALIZECUSTOMERID;
import static com.fitternity.enums.Jobs.OZONETELCUSTOMERMAPPER;
import static com.fitternity.enums.Jobs.UNSUBSCRIBECUSTOMERS;

import java.util.HashMap;
import java.util.Timer;

import com.fitternity.constants.AppConstants;
import com.fitternity.constants.CronConstants;
import com.fitternity.enums.Jobs;
import com.fitternity.jobs.NormalizeCustomerId;
import com.fitternity.jobs.OzonetelCustomerMapper;
import com.fitternity.jobs.UnsubscribeCustomers;
import com.fitternity.util.PropertiesUtil;
/**
 * @author shahaan
 *
 */
public class JobSchedulerManager  implements CronConstants,AppConstants
{
	public JobSchedulerManager() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @param job
	 * @param initDelay
	 * @param scheduleTime
	 * @return
	 */
	private static Timer scheduleJob(final Jobs job,final Long initDelay,final Long scheduleTime)
	{
		Timer timer = new Timer();
				if(initDelay==null||initDelay<0)
				{
						if(initDelay==null)
								throw new NullPointerException("Init Delay Can't be Null");
						if(initDelay<0)
							throw new IllegalArgumentException(" Init Delay Can't be less than zero and is of type long");
				}
				System.out.println(job);
		switch(job)
		{
		case NORMALIZECUSTOMERID:
								{	
									NormalizeCustomerId normalizeCustomerId = new NormalizeCustomerId(timer);
									if(scheduleTime==null||scheduleTime<0)
									{
										timer.schedule(normalizeCustomerId, initDelay);
										return timer;	
									}
									else
									{
										timer.schedule(normalizeCustomerId, initDelay,scheduleTime);
										return timer;
									}
								}
		case OZONETELCUSTOMERMAPPER:
									 OzonetelCustomerMapper ozonetelCustomerMapper =new	 OzonetelCustomerMapper(timer);
									 if(scheduleTime==null||scheduleTime<0)
										{
										 	timer.schedule(ozonetelCustomerMapper, initDelay);
											return timer;	
										}
										else
										{
											timer.schedule(ozonetelCustomerMapper, initDelay,scheduleTime);
											return timer;
										}
									 
		case UNSUBSCRIBECUSTOMERS:
			 UnsubscribeCustomers unsubscribeCustomers =new UnsubscribeCustomers(timer);
			 if(scheduleTime==null||scheduleTime<0)
				{
				 	timer.schedule(unsubscribeCustomers, initDelay);
					return timer;	
				}
				else
				{
					timer.schedule(unsubscribeCustomers, initDelay,scheduleTime);
					return timer;
				}
		default:
									break;
		
		}

		return timer;
	}


	/**
	 * @return
	 */
	public static HashMap<Jobs, Timer> processJobs() 
	{
		HashMap<Jobs, Timer> timersScheduled=new HashMap<>();
		if(Boolean.parseBoolean(PropertiesUtil.getAppProperty(NORM_CUST_JOB)))
		{
							timersScheduled.put(NORMALIZECUSTOMERID,scheduleJob(NORMALIZECUSTOMERID, 
							Long.parseLong(PropertiesUtil.getCronProperty(NORM_CUST_ID_INIT_DELAY)), 
							Long.parseLong(PropertiesUtil.getCronProperty(NORM_CUST_ID_PERIOD))));
		}
		if(Boolean.parseBoolean(PropertiesUtil.getAppProperty(OZONE_MAP_JOB)))
		{
							timersScheduled.put(Jobs.OZONETELCUSTOMERMAPPER,scheduleJob(OZONETELCUSTOMERMAPPER, 
							Long.parseLong(PropertiesUtil.getCronProperty(OZO_CUST_MAPID_INIT_DELAY)), 
							Long.parseLong(PropertiesUtil.getCronProperty(OZO_CUST_MAPID_PERIOD))));
		}
		if(Boolean.parseBoolean(PropertiesUtil.getAppProperty(UNSUBSCRIBE_CUST)))
		{
					        timersScheduled.put(UNSUBSCRIBECUSTOMERS,scheduleJob(UNSUBSCRIBECUSTOMERS, 
							Long.parseLong(PropertiesUtil.getCronProperty(UNSUB_CUST_INIT_DELAY)), 
							Long.parseLong(PropertiesUtil.getCronProperty(UNSUB_CUST_PERIOD))));
		}
		return timersScheduled;
	}
	
}
