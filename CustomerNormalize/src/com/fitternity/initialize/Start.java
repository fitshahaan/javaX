package com.fitternity.initialize;

import static com.fitternity.enums.Collections.VENDORS;
import static com.fitternity.enums.Jobs.NORMALIZECUSTOMERID;

import java.util.Timer;

import com.fitternity.constants.CronConstants;
import com.fitternity.manager.JobSchedulerManager;
import com.fitternity.util.PropertiesUtil;

/**
 * @author Shahaan
 *
 */
public class Start {

	
	public static void main(String[] args) {
		/*Timer timer = new Timer();
		NormalizeCustomerId a = new NormalizeCustomerId(timer);
		timer.schedule(a, 2000);*/
		
		System.out.println(VENDORS);
		Timer timer =JobSchedulerManager.scheduleJob(NORMALIZECUSTOMERID, 
													Long.parseLong(PropertiesUtil.getCronProperty(CronConstants.NORM_CUST_ID_INIT_DELAY)), 
													Long.parseLong(PropertiesUtil.getCronProperty(CronConstants.NORM_CUST_ID_PERIOD)));

		/* Timer MapOzonetelToCustomerTimer = new Timer();
		 OzonetelCustomerMapper a1 =new	 OzonetelCustomerMapper(MapOzonetelToCustomerTimer);
		 MapOzonetelToCustomerTimer.schedule(a1, 1000,24*60*60);*/
	}
}


