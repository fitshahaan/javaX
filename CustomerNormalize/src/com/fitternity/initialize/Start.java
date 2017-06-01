package com.fitternity.initialize;

import com.fitternity.manager.JobSchedulerManager;

/**
 * @author Shahaan
 *
 */
public class Start
{
	public static void main(String[] args) 
	{	
			System.out.println(JobSchedulerManager.processJobs()+" JOBS STARTED AND SCHEDULED.");
//		System.out.println(Boolean.parseBoolean(PropertiesUtil.getAppProperty(NORM_CUST_JOB)));
	}
}