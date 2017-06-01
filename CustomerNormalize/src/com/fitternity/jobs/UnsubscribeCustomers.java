package com.fitternity.jobs;

import static com.fitternity.enums.Collections.CUSTOMERS;
import static com.fitternity.enums.Collections.OZONETELCAPTURES;
import static com.fitternity.enums.Databases.FITADMIN;
import static com.fitternity.enums.Environments.STAGING;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
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
public class UnsubscribeCustomers extends TimerTask {
	Timer timer;
	CustomerDao custDao ;
	public UnsubscribeCustomers(Timer timer) {
		// TODO Auto-generated constructor stub
		this.timer = timer;
	}

	public void run() 
	{
		System.out.println("Timer task started at:" + new Date());
		
		
		TransactionManager transactionManager = new TransactionManager();
		transactionManager.startTransaction();
		custDao = (CustomerDao) transactionManager.getDatabaseManager(FITADMIN).getCollection(CUSTOMERS);
		
		readUnSubData();
		
		System.out.println("Timer task finished at:" + new Date());
		// timer.cancel();
	}

	private void readUnSubData()
	{
		BulkWriteOperation bulkWriteOperation = custDao.getBulkWriteOp();
		try 
		{
//			System.out.println("HI "+new File(System.getProperty("user.dir")+"/input/Unsubscribe Customers/Unsubsrcibe List - Sheet1.csv").getName());
			BufferedReader br=new BufferedReader(new FileReader(new File(System.getProperty("user.dir")+"/input/Unsubscribe Customers/Unsubsrcibe List - Sheet1.csv")));
			String line="";
			HashSet<String> emails=new HashSet<>();
			int counter=0;
//			System.out.println(" TOTAL LINES "+br.lines());
			while((line=br.readLine())!=null)
			{
				String currentEmail=line.split(",")[0];
//				System.out.println(" currentEmail " +currentEmail);
				if(currentEmail!=null&&!"".equalsIgnoreCase(currentEmail))
				{
					System.out.println(currentEmail);
					emails.add(currentEmail);	
					counter++;
					System.out.println(" currentEmail :: "+currentEmail +" _ counter :: "+counter);
				}
				if(counter==5000)
				{
					updateInDB(emails,bulkWriteOperation);
					counter=0;
				}
			}
			if (counter<5000&&counter>0) 
			{
				updateInDB(emails,bulkWriteOperation);
				custDao.updateSubscribedCustomers();
//				counter=0;
			}
		} 
		catch (IOException e) 
		{

			e.printStackTrace();
		}
	}
	

	private void updateInDB(HashSet<String> emails,BulkWriteOperation bulkWriteOperation)
	{
		bulkWriteOperation = custDao.getBulkWriteOp();
			System.out.println(emails.size());
			bulkWriteOperation.find(new BasicDBObject("email", new BasicDBObject("$in",emails.toArray(new String[emails.size()])))).update(new BasicDBObject("$set",new BasicDBObject("unsubscribed_customer", true)));
			/*try {
				Thread.sleep(12000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			System.out.println(" 5000 DONE." + bulkWriteOperation.execute());
			emails.clear();
		
	}
	
		

}
