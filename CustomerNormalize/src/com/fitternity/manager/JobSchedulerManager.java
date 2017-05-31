package com.fitternity.manager;

import java.util.Timer;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.constants.Jobs;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.jobs.NormalizeCustomerId;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class JobSchedulerManager 
{
	public JobSchedulerManager() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static Timer scheduleJob(Jobs job,long initDelay,long schduleTime)
	{
		Timer timer = new Timer();
		switch(job)
		{
		case NormalizeCustomerId:
								{	/*if(initDelay<0)
//										throw new */
									NormalizeCustomerId a = new NormalizeCustomerId(timer);
									timer.schedule(a, initDelay);
									return timer;
								}
		case OzonetelCustomerMapper:
									break;
		default:
									break;
		
		}

		return timer;
	}
	
}
