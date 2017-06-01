package com.fitternity.jobs;

import static com.fitternity.enums.Collections.CUSTOMERS;
import static com.fitternity.enums.Collections.OZONETELCAPTURES;
import static com.fitternity.enums.Databases.FITADMIN;
import static com.fitternity.enums.Environments.STAGING;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.manager.TransactionManager;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
/**
 * @author Shahaan
 *
 */
public class OzonetelCustomerMapper extends TimerTask {
	Timer timer;

	public OzonetelCustomerMapper(Timer timer) {
		// TODO Auto-generated constructor stub
		this.timer = timer;
	}

	public void run() {
		System.out.println("Timer task started at:" + new Date());
		TransactionManager transactionManager = new TransactionManager();

		transactionManager.startTransaction();
		CustomerDao custDao = (CustomerDao) transactionManager.getDatabaseManager(FITADMIN).getCollection(CUSTOMERS);
		OzoneTelDao ozoneTelDao = (OzoneTelDao) transactionManager.getDatabaseManager(FITADMIN).getCollection(OZONETELCAPTURES);

		DBCursor ozoneTelCursor = ozoneTelDao.getOzoneTelCaps();
		BulkWriteOperation bulkWriteOperation = ozoneTelDao.getBulkWriteOp();
		int i = 0;
		while (ozoneTelCursor.hasNext()) {
			DBObject currentOzoneTelObject = ozoneTelCursor.next();
			String custPhone = (String) currentOzoneTelObject.get("customer_contact_no");
			// if(custPhone!=null&&!custPhone.equals(""))
			// {
			DBObject customerData = custDao.getCustomerOnOzoneTel(custPhone);
			System.out.println(customerData);
			bulkWriteOperation.find(new BasicDBObject("_id", (Number) currentOzoneTelObject.get("_id")))
					.update((new BasicDBObject("$set",
							new BasicDBObject("customer_id", (customerData != null) ? (Number)customerData.get("_id") : -1))));
			i++;
			System.out.println(i);
			if (i % 1000 == 0)
				System.out.println(" 1000 Target Reached");
			if (i == 5000) {
				System.out.println(" WITHIN 5000" + bulkWriteOperation.execute());
				i = 0;
				bulkWriteOperation = ozoneTelDao.getBulkWriteOp();
			}
		}
		// old single update
		// ozoneTelDao.updateOzoneTelCaptOnCustomerId((customerData!=null)?true:false,
		// currentOzoneTelObject);

		// }
		if (i<5000&&i>0) {
			System.out.println(" AFTER 5000" + bulkWriteOperation.execute());
			bulkWriteOperation = ozoneTelDao.getBulkWriteOp();
		}
		System.out.println("Timer task finished at:" + new Date());
		// timer.cancel();
	}

}
