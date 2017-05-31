package com.fitternity.manager;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.constants.Collections;
import com.fitternity.constants.Databases;
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
	
	public DatabaseManager (MongoClient client,Databases db)
	{
			this.conn=client;
			setDB(db.toString());
	}
	private void setDB(String db) {
			this.db=this.conn.getDB(db);
	}
	
	public BaseCollection getCollection(Collections name) 
	{
		switch (name) {
		case VENDORS: 	return new VendorDao(db.getCollection(name.toString()));
		case CUSTOMERS: 	System.out.println(db);return new CustomerDao(db.getCollection(name.toString()));
		case OZONETELCAPTURES: 	return new OzoneTelDao(db.getCollection(name.toString()));
		case TRANSACTIONS: 	return new TransactionDao(db.getCollection(name.toString()));
		default:return null;
		}
	}
	public DB getDb() {
		return db;
	}
	public void setDb(DB db) {
		this.db = db;
	}	
	
}
