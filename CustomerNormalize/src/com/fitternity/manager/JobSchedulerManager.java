package com.fitternity.manager;

import static com.fitternity.enums.Jobs.NORMALIZECUSTOMERID;
import static com.fitternity.enums.Jobs.OZONETELCUSTOMERMAPPER;

import java.util.HashMap;
import java.util.Timer;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.constants.AppConstants;
import com.fitternity.constants.CronConstants;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.enums.Jobs;
import com.fitternity.jobs.NormalizeCustomerId;
import com.fitternity.jobs.OzonetelCustomerMapper;
import com.fitternity.util.PropertiesUtil;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.util.Hash;

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
			Timer NormalizeCustomerIdtimer =scheduleJob(NORMALIZECUSTOMERID, 
					Long.parseLong(PropertiesUtil.getCronProperty(NORM_CUST_ID_INIT_DELAY)), 
					Long.parseLong(PropertiesUtil.getCronProperty(NORM_CUST_ID_PERIOD)));
			timersScheduled.put(NORMALIZECUSTOMERID,NormalizeCustomerIdtimer);
		}
		if(Boolean.parseBoolean(PropertiesUtil.getAppProperty(OZONE_MAP_JOB)))
		{
		Timer ozoneTelCustMaptimer =scheduleJob(OZONETELCUSTOMERMAPPER, 
									Long.parseLong(PropertiesUtil.getCronProperty(OZO_CUST_MAPID_INIT_DELAY)), 
									Long.parseLong(PropertiesUtil.getCronProperty(OZO_CUST_MAPID_PERIOD)));
								timersScheduled.put(Jobs.OZONETELCUSTOMERMAPPER,ozoneTelCustMaptimer);
		}
		return timersScheduled;
	}
	
}
