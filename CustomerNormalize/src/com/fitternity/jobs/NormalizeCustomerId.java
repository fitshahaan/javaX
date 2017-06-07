package com.fitternity.jobs;

import static com.fitternity.enums.Collections.CUSTOMERS;
import static com.fitternity.enums.Collections.TRANSACTIONS;
import static com.fitternity.enums.Databases.FITADMIN;
import static com.fitternity.enums.Databases.FITAPI;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.fitternity.constants.AppConstants;
import com.fitternity.constants.FitConstants;
import com.fitternity.dao.beans.Customer;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.manager.TransactionManager;
import com.fitternity.services.ReverseMigrateApiServices;
import com.fitternity.util.PropertiesUtil;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Shahaan
 *
 */
public class NormalizeCustomerId extends TimerTask {
	private Timer timer;
	
	private String job;
	private ReverseMigrateApiServices reverseMigrateApiServices=new ReverseMigrateApiServices();

	public NormalizeCustomerId(Timer timer) {
		// TODO Auto-generated constructor stub
		this.timer = timer;
	}

	public void run() {
		System.out.println("Timer task started at:" + new Date());
		TransactionManager transactionManager = new TransactionManager();
		transactionManager.startTransaction();
		
//		transactionManager.endTransaction();
		CustomerDao custDao = (CustomerDao) transactionManager.getDatabaseManager(FITADMIN).getCollection(CUSTOMERS);
		System.out.println("custDao :: " + custDao);
		TransactionDao transDao = (TransactionDao) transactionManager.getDatabaseManager(FITAPI).getCollection(TRANSACTIONS);
		System.out.println("transDao :: " + transDao);
		// DBObject customerData=custDao.getCustomer(1);

		// System.out.println(topCustID);

		// System.out.println(transDao.getUncertainTransactions());
		// daoManager.getDB("fitadmin").getCollection(");
		
		DBCursor transCursor = transDao.getUncertainTransactions();
		while (transCursor.hasNext()) 
		{
			DBObject currentTransObject = transCursor.next();
			System.out.println(" currentTransObject " + currentTransObject);
			String custPhone = currentTransObject.get("customer_phone") == null?null:""+currentTransObject.get("customer_phone");

			System.out.println(" cust phone " + custPhone);
			System.out.println(" cust phone " + custPhone instanceof String);
			// System.out.println(" cust phone "+String.valueOf(custPhone));
			if (custPhone == null|| custPhone.equals("") ||custPhone.equals("null") ) {
				System.out.println("NO PHONE EXISTS");
				String custEmail = currentTransObject.get("customer_email") == null?null:""+currentTransObject.get("customer_email");;

				DBObject customerDataEmail = null;
				if (custEmail != null && !"".equals(custEmail))
					customerDataEmail = custDao.getCustomerBasedOnEmail(custEmail);
				Number cid = -1;
				if (customerDataEmail != null)
					cid = (Number) customerDataEmail.get("_id");

				System.out.println(cid);
				System.out.println("OBJECT ID  " + currentTransObject.get("_id"));
				transDao.updateTransOnCustomerId(cid, currentTransObject);
			} 
			else 
			{
				DBObject customerDataPhone = custDao.getCustomerBasedOnPhone(custPhone);
				Number cid = -1;
				if (customerDataPhone != null)
					cid = (Number) customerDataPhone.get("_id");
				else 
				{
					String custEmail = (String) currentTransObject.get("customer_email");
					DBObject customerDataEmail = null;
					if (custEmail != null && !"".equals(custEmail))
						customerDataEmail = custDao.getCustomerBasedOnEmail(custEmail);
					if (customerDataEmail != null)
						cid = (Number) customerDataEmail.get("_id");
					else 
					{
						if (cid.intValue() == -1) 
						{
							cid = custDao.getTopCustomerID().intValue() + 1;
							BasicDBObjectBuilder documentBuilder = BasicDBObjectBuilder.start().add("_id", cid);
							if (currentTransObject.get("customer_name") != null&& !"".equals(currentTransObject.get("customer_name")))
								documentBuilder.add("name", currentTransObject.get("customer_name"));
							if (currentTransObject.get("customer_phone") != null&& !"".equals(currentTransObject.get("customer_phone")))
								documentBuilder.add("contact_no", currentTransObject.get("customer_phone"));
							 if(currentTransObject.get("customer_email")!=null&&!"".equals(currentTransObject.get("customer_email")))
							documentBuilder.add("email", currentTransObject.get("customer_email"));
							documentBuilder.add("created_at", new Date());
							documentBuilder.add("updated_at", new Date());
							if (!custDao.addNewCustomer(documentBuilder.get()))
								cid = -1;
						}
					}
				}
				System.out.println("CID " + cid);
				System.out.println(currentTransObject.get("_id").toString());
				transDao.updateTransOnCustomerId(cid, currentTransObject);
			}

		}

		System.out.println("Timer task finished at:" + new Date());
	
		try {
			System.out.println("CRON JOB STARTED AT :" + new Date());
			System.out.println(" PropertiesUtil.getAppProperty(cronJob) "+PropertiesUtil.getAppProperty(AppConstants.CRON_JOB));
			System.out.println(" job "+job);
			if(FitConstants.CUSTOMER_JOB.equalsIgnoreCase(PropertiesUtil.getAppProperty(AppConstants.CRON_JOB))&&job==null)
				{
					reverseMigrateApiServices.startCustomerCron(PropertiesUtil.getAppProperty(AppConstants.CRON_JOB));			
					job=PropertiesUtil.getAppProperty(AppConstants.CRON_JOB);
				}
			else
			{
				reverseMigrateApiServices.startCustomerCron(PropertiesUtil.getAppProperty(AppConstants.CRON_JOB));
			}
			System.out.println("CRON JOB RESPONSE AT  :" + new Date());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR :: "+e);
			e.printStackTrace();
		}
		
	}


}