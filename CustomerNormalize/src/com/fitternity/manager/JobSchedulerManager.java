package com.fitternity.manager;

import java.util.Timer;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.enums.Jobs;
import com.fitternity.jobs.NormalizeCustomerId;
import com.fitternity.jobs.OzonetelCustomerMapper;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class JobSchedulerManager 
{
	public JobSchedulerManager() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static Timer scheduleJob(Jobs job,Long initDelay,Long scheduleTime)
	{
		Timer timer = new Timer();
				if(initDelay==null||initDelay<0)
				{
						if(initDelay==null)
								throw new NullPointerException("Init Delay Can't be Null");
						if(initDelay<0)
							throw new IllegalArgumentException(" Init Delay Can't be less than zero and is of type long");
				}
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
	
}
