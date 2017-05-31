package com.fitternity.manager;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.abstracthelpers.BaseDatabase;
import com.fitternity.constants.Databases;
import com.fitternity.constants.Environments;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.constants.Environments.*;
import com.fitternity.dao.databases.FitAdmin;
import com.fitternity.dao.databases.FitApi;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class TransactionManager 

{
	MongoClient mongoClient ; 
	Environments type;
	DB db;
	boolean isAuthenticated;
	
	/*public MongoClient getConnection(String type) 
	{
			// TODO Auto-generated method stub
		try 
		{
			if(type.equals("local"))
			{
				this.mongoClient= new MongoClient( "localhost" , 27017 );
				return 	this.mongoClient;			
			}
			else
			{
				MongoCredential journaldevAuth = MongoCredential.createPlainCredential("pankaj", "journaldev", "pankaj123".toCharArray());
//				MongoCredential testAuth = MongoCredential.createPlainCredential("pankaj", "test", "pankaj123".toCharArray());
				ArrayList<MongoCredential> auths=new ArrayList<>();
				auths.add(journaldevAuth);
				MongoClient mongo = new MongoClient(new ServerAddress("35.154.147.1", 27017), auths);
				return 	this.mongoClient;
			}
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			return null;
		}
	}*/
	public void startTransaction(Environments type)
	{
	
		try {
			switch (type)
			{
			case LOCAL:  this.mongoClient= new MongoClient( "localhost" , 27017 );
						 this.type=type;
						 break;
			case STAGING:
						this.mongoClient= new MongoClient( "35.154.147.1" , 27017 );
						this.type=type;
						break;
			case PRODUCTION:
						break;
			default: this.mongoClient= new MongoClient( "localhost" , 27017 );
					 this.type=type;
					 break;
			}
		} catch (UnknownHostException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		/*try 
		{
			if(type.equals("local"))
			{
				this.mongoClient= new MongoClient( "localhost" , 27017 );
				this.type=type;
				return true;		
			}
			else if(type.equals("staging"))
			{
//				this.mongoClient= new MongoClient( "localhost" , 27017 );
				this.mongoClient= new MongoClient( "35.154.147.1" , 27017 );
				this.type=type;
				return 	true;
			}
			else
				return false;
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			return false;
		}*/
	}
	
	public BaseDatabase getDB(String dbName)
	{
		switch (dbName) {
		case "fitadmin": 	return new FitAdmin(this.mongoClient.getDB(dbName));
		case "fitapi": 	return new FitApi(this.mongoClient.getDB(dbName));
		default:return null;
		}
	}
	private void viewAllDbs() {
		// TODO Auto-generated method stub
		List<String> dbs = this.mongoClient.getDatabaseNames();
		System.out.println(dbs);
	}
	public BaseCollection getCollection(String name) 
	{
		switch (name) {
		case "vendors": 	return new VendorDao(db.getCollection(name));
		case "customers": 	return new CustomerDao(db.getCollection(name));
		case "ozoneTel": 	return new OzoneTelDao(db.getCollection(name));
		case "transactions": 	return new TransactionDao(db.getCollection(name));
		default:return null;
		}
	}
	public DatabaseManager getDatabaseManager(Databases db) 
	{
		System.out.println(" this.mongoClient ::  "+this.mongoClient);
		DatabaseManager databaseManager=new DatabaseManager(this.mongoClient,db);
		System.out.println(type);
		System.out.println(" databaseManager.getDb().getName() ::  "+databaseManager.getDb().getName());
		System.out.println(" databaseManager.getDb() ::  "+databaseManager.getDb());
		System.out.println(" Environments.STAGING.equals(type) "+Environments.STAGING.equals(type));
		System.out.println(" isAuthenticated "+isAuthenticated);
		if(Environments.STAGING.equals(type)/*&&!isAuthenticated*/)
		{
			databaseManager.getDb().authenticate("fitadmin", "fit1234".toCharArray());
			isAuthenticated=true;
		}
			
		return databaseManager;
	}

	public boolean endTransaction() {
		if(mongoClient !=null)
		{
			mongoClient.close();
			return true;
		}
		else return false;
		
	}		
}