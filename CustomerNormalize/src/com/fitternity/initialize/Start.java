/**
 * 
 */
package com.fitternity.initialize;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.manager.TransactionManager;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.sun.xml.internal.fastinfoset.sax.Properties;

import sun.security.jca.GetInstance.Instance;

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
//		Timer timer = new Timer();
//		OneTimeNormalizeCustomerId a = new OneTimeNormalizeCustomerId(timer);
//		timer.schedule(a, 2000);

		 Timer MapOzonetelToCustomerTimer = new Timer();
		 MapOzonetelToCustomer a1 =new
		 MapOzonetelToCustomer(MapOzonetelToCustomerTimer);
		 MapOzonetelToCustomerTimer.schedule(a1, 1000,24*60*60);
	}
}

class OneTimeNormalizeCustomerId extends TimerTask {
	Timer timer;

	public OneTimeNormalizeCustomerId(Timer timer) {
		// TODO Auto-generated constructor stub
		this.timer = timer;
	}

	public void run() {
		System.out.println("Timer task started at:" + new Date());
		TransactionManager transactionManager = new TransactionManager();
		transactionManager.startTransaction("staging");
		CustomerDao custDao = (CustomerDao) transactionManager.getDatabaseManager("fitadmin")
				.getCollection("customers");
		System.out.println("custDao :: " + custDao);
		TransactionDao transDao = (TransactionDao) transactionManager.getDatabaseManager("fitapi")
				.getCollection("transactions");

		System.out.println("transDao :: " + transDao);
		/* Customer customer= */
		// DBObject customerData=custDao.getCustomer(1);

		// System.out.println(topCustID);

		// System.out.println(transDao.getUncertainTransactions());
		// daoManager.getDB("fitadmin").getCollection(");
		DBCursor transCursor = transDao.getUncertainTransactions();
		while (transCursor.hasNext()) {
			DBObject currentTransObject = transCursor.next();
			System.out.println(" currentTransObject " + currentTransObject);
			String custPhone = currentTransObject.get("customer_phone") == null?null:""+currentTransObject.get("customer_phone");

			System.out.println(" cust phone " + custPhone);
			System.out.println(" cust phone " + custPhone instanceof String);
			// System.out.println(" cust phone "+String.valueOf(custPhone));
			if (custPhone == null|| custPhone.equals("")/* ||custPhone.equals("null") */) {
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
			} else {
				DBObject customerDataPhone = custDao.getCustomerBasedOnPhone(custPhone);
				Number cid = -1;
				if (customerDataPhone != null)
					cid = (Number) customerDataPhone.get("_id");
				else {
					String custEmail = (String) currentTransObject.get("customer_email");
					DBObject customerDataEmail = null;
					if (custEmail != null && !"".equals(custEmail))
						customerDataEmail = custDao.getCustomerBasedOnEmail(custEmail);
					if (customerDataEmail != null)
						cid = (Number) customerDataEmail.get("_id");
					else {
						if (cid.intValue() == -1) {
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
		timer.cancel();
	}

}

class MapOzonetelToCustomer extends TimerTask {
	Timer timer;

	public MapOzonetelToCustomer(Timer timer) {
		// TODO Auto-generated constructor stub
		this.timer = timer;
	}

	public void run() {
		System.out.println("Timer task started at:" + new Date());
		TransactionManager transactionManager = new TransactionManager();

		transactionManager.startTransaction("staging");
		CustomerDao custDao = (CustomerDao) transactionManager.getDatabaseManager("fitadmin")
				.getCollection("customers");
		OzoneTelDao ozoneTelDao = (OzoneTelDao) transactionManager.getDatabaseManager("fitadmin")
				.getCollection("ozonetelcaptures");

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
