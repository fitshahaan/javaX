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
			BufferedReader br=new BufferedReader(new FileReader(new File(System.getProperty("user.dir")+"/input/Unsubscribe Customers/Unsubsrcibe List - Sheet1.csv")));
			String line="";
			HashSet<String> emails=new HashSet<>();
			int counter=0;
			System.out.println(" TOTAL LINES "+br.lines());
			/*while((line=br.readLine())!=null)
			{
				String currentEmail=line.split(",")[0];
				if(currentEmail!=null&&"".equalsIgnoreCase(currentEmail))
				{
					emails.add(currentEmail);	
					counter++;
				}
				if(counter==5000)
				{
					updateInDB(emails,bulkWriteOperation);
					emails.clear();
					counter=0;
				}
			}
			if (counter<5000&&counter>0) 
			{
				updateInDB(emails,bulkWriteOperation);
				emails.clear();
				counter=0;
			}*/
		} 
		catch (IOException e) 
		{

			e.printStackTrace();
		}
	}
	
	
	private void updateInDB(HashSet<String> emails,BulkWriteOperation bulkWriteOperation)
	{
			bulkWriteOperation.find(new BasicDBObject("email", new BasicDBObject("$in",emails.toArray(new String[emails.size()])))).update(new BasicDBObject("$set",new BasicDBObject("unsubscribed customer", true)));
			System.out.println(" 5000 DONE." + bulkWriteOperation.execute());
			bulkWriteOperation = custDao.getBulkWriteOp();
	}
	
		

}
