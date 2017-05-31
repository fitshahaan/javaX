/**
 * 
 */
package com.fitternity.initialize;

import static com.fitternity.constants.Collections.VENDORS;

import java.util.Properties;
import java.util.Timer;
import static com.fitternity.constants.Jobs.*;
import com.fitternity.manager.JobSchedulerManager;

/**
 * @author Shahaan
 *
 */
public class Start {

	/**
	 * @param args
	 */
	// initialize properties
	static {
		Properties properties = new Properties();

	}

	public static void main(String[] args) {
		/*Timer timer = new Timer();
		NormalizeCustomerId a = new NormalizeCustomerId(timer);
		timer.schedule(a, 2000);*/

		System.out.println(VENDORS);
		Timer timer =JobSchedulerManager.scheduleJob(NormalizeCustomerId, 2000,-1);

		/* Timer MapOzonetelToCustomerTimer = new Timer();
		 OzonetelCustomerMapper a1 =new	 OzonetelCustomerMapper(MapOzonetelToCustomerTimer);
		 MapOzonetelToCustomerTimer.schedule(a1, 1000,24*60*60);*/
	}
}


