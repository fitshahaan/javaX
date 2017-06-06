package com.fitternity.manager;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.dao.collections.CityDao;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.LocationClusterDao;
import com.fitternity.dao.collections.LocationDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.enums.Collections;
import com.fitternity.enums.Databases;
import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * @author shahaan
 *
 */
public class DatabaseManager 
{
	private DB db; 
	@Override
	public String toString() {
		return "DatabaseManager [db=" + db + ", conn=" + conn + "]";
	}
	private MongoClient conn; 
	
	/**
	 * @param client
	 * @param db
	 */
	public DatabaseManager (MongoClient client,Databases db)
	{
			this.conn=client;
			setDB(db.toString());
	}
	private void setDB(String db) {
			this.db=this.conn.getDB(db);
	}
	
	public MongoClient getConn() {
		return conn;
	}
	public void setConn(MongoClient conn) {
		this.conn = conn;
	}
	/**
	 * @param name
	 * @return
	 */
	public BaseCollection getCollection(Collections name) 
	{
		switch (name) {
		case VENDORS: 	return new VendorDao(db.getCollection(name.toString()));
		case CUSTOMERS: 	System.out.println(db);return new CustomerDao(db.getCollection(name.toString()));
		case OZONETELCAPTURES: 	return new OzoneTelDao(db.getCollection(name.toString()));
		case TRANSACTIONS: 	return new TransactionDao(db.getCollection(name.toString()));
		case LOCATIONS: 	return new LocationDao(db.getCollection(name.toString()));
		case LOCATIONCLUSTERS: 	return new LocationClusterDao(db.getCollection(name.toString()));
		case CITIES: 	return new CityDao(db.getCollection(name.toString()));
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
