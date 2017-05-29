package com.fitternity.manager;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.dao.collections.VendorDao;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class DatabaseManager 
{
	private DB db; 
	private MongoClient conn; 
	
	public DatabaseManager (MongoClient client,String db)
	{
			this.conn=client;
			setDB(db);
	}
	private void setDB(String db) {
			this.db=this.conn.getDB(db);
	}
	
	public BaseCollection getCollection(String name) 
	{
		switch (name) {
		case "vendors": 	return new VendorDao(db.getCollection(name));
		case "customers": 	System.out.println(db);return new CustomerDao(db.getCollection(name));
		case "ozonetelcaptures": 	return new OzoneTelDao(db.getCollection(name));
		case "transactions": 	return new TransactionDao(db.getCollection(name));
		default:return null;
		}
	}	
	
}
