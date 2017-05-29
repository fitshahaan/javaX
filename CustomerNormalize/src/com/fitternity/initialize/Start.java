/**
 * 
 */
package com.fitternity.initialize;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.fitternity.dao.beans.Customer;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.manager.TransactionManager;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkUpdateRequestBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteRequestBuilder;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sun.xml.internal.fastinfoset.sax.Properties;

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
		Properties properties=new Properties();

	}
	public static void main(String[] args) 
	{
		/*Timer timer = new Timer();
		OneTimeNormalizeCustomerId a =new OneTimeNormalizeCustomerId(timer);
		timer.schedule(a, 5000);*/
		
		Timer MapOzonetelToCustomerTimer = new Timer();
		MapOzonetelToCustomer a1 =new MapOzonetelToCustomer(MapOzonetelToCustomerTimer);
		MapOzonetelToCustomerTimer.schedule(a1, 1000,24*60*60);
	}
}
	
	class OneTimeNormalizeCustomerId extends TimerTask
	{
		Timer timer;
		public OneTimeNormalizeCustomerId(Timer timer) {
			// TODO Auto-generated constructor stub
			this.timer=timer;
		}

		public void run() {
			System.out.println("Timer task started at:"+new Date());

			TransactionManager transactionManager=new TransactionManager();
			
			transactionManager.startTransaction("local");
			CustomerDao custDao=(CustomerDao)transactionManager.getDatabaseManager("fitadmin").getCollection("customers");
			
			TransactionDao transDao=(TransactionDao)transactionManager.getDatabaseManager("fitapi").getCollection("transactions");
			/*Customer customer=*/
			DBObject customerData=custDao.getCustomer(1);
//			System.out.println(transDao.getUncertainTransactions());
//			daoManager.getDB("fitadmin").getCollection(");
			DBCursor transCursor=transDao.getUncertainTransactions();
			while(transCursor.hasNext())
			{
				DBObject currentTransObject=transCursor.next();
				System.out.println(" currentTransObject "+currentTransObject);
				String custPhone=(String) currentTransObject.get("customer_phone");
				System.out.println(" cust phone "+custPhone);
				if(custPhone==null||custPhone.equals(""))
				{
					System.out.println("NO PHONE EXISTS");
					String custEmail=(String) currentTransObject.get("customer_email");	
					DBObject customerDataEmail=custDao.getCustomerBasedOnEmail(custEmail);
					int cid= (int) customerDataEmail.get("customer_id");
					System.out.println(cid);
					System.out.println("OBJECT ID  "+currentTransObject.get("_id"));
					transDao.updateTransOnCustomerId(cid,currentTransObject);
				}
				else
				{
					DBObject customerDataPhone=custDao.getCustomerBasedOnPhone(custPhone);
					long cid= (long) customerDataPhone.get("_id");
					System.out.println("CID "+cid);
					System.out.println(currentTransObject.get("_id").toString());
					transDao.updateTransOnCustomerId(cid,currentTransObject);
				}
				
			}
			
		
			 System.out.println("Timer task finished at:"+new Date());
			 timer.cancel();
		}
		
		
	}
	
	class MapOzonetelToCustomer extends TimerTask
	{
		Timer timer;
		public MapOzonetelToCustomer(Timer timer) {
			// TODO Auto-generated constructor stub
			this.timer=timer;
		}

		public void run() {
			System.out.println("Timer task started at:"+new Date());
			TransactionManager transactionManager=new TransactionManager();
			
			transactionManager.startTransaction("local");
			CustomerDao custDao=(CustomerDao)transactionManager.getDatabaseManager("fitadmin").getCollection("customers");
			OzoneTelDao ozoneTelDao=(OzoneTelDao)transactionManager.getDatabaseManager("fitadmin").getCollection("ozonetelcaptures");
			
			
			DBCursor ozoneTelCursor=ozoneTelDao.getOzoneTelCaps();
			BulkWriteOperation bulkWriteOperation =ozoneTelDao.getBulkWriteOp();
			int i=0;
			while(ozoneTelCursor.hasNext())
			{	
				DBObject currentOzoneTelObject=ozoneTelCursor.next();;
				String custPhone=(String) currentOzoneTelObject.get("customer_cid");
				if(custPhone!=null&&!custPhone.equals(""))
				{
						DBObject customerData=custDao.getCustomerOnOzoneTel(custPhone);
				        System.out.println(customerData);
				        bulkWriteOperation.find(new BasicDBObject("_id",(Number)currentOzoneTelObject.get("_id"))).update((new BasicDBObject("$set", new BasicDBObject("customer exists", (customerData!=null)?true:false))));
						i++;
						System.out.println(i);
						if(i%1000==0)
							System.out.println(" 1000 Target Reached");
						if(i==5000)
						{
							System.out.println(" WITHIN 5000" +bulkWriteOperation.execute());
							i=0;
							bulkWriteOperation =ozoneTelDao.getBulkWriteOp();	
						}
					}	
						// old single update
//						ozoneTelDao.updateOzoneTelCaptOnCustomerId((customerData!=null)?true:false, currentOzoneTelObject);
				
				}
				if(i<5000)
				{
					System.out.println(" AFTER 5000" +bulkWriteOperation.execute());
					bulkWriteOperation =ozoneTelDao.getBulkWriteOp();
				}
			 System.out.println("Timer task finished at:"+new Date());
//			 timer.cancel();
		}
		
		
	}

