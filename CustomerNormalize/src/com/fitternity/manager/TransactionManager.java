package com.fitternity.manager;

import static com.fitternity.enums.Environments.LOCAL;
import static com.fitternity.enums.Environments.PRODUCTION;
import static com.fitternity.enums.Environments.STAGING;

import java.net.UnknownHostException;
import java.util.List;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.abstracthelpers.BaseDatabase;
import com.fitternity.constants.AppConstants;
import com.fitternity.constants.DBConstants;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.dao.databases.FitAdmin;
import com.fitternity.dao.databases.FitApi;
import com.fitternity.enums.Databases;
import com.fitternity.enums.Environments;
import com.fitternity.util.PropertiesUtil;
import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * @author shahaan
 *
 */
public class TransactionManager implements AppConstants,DBConstants

{
	private MongoClient mongoClient ; 
	private Environments env;
	private DB db;
	private boolean isAuthenticated;
	
	
	private void setEnvironment()
	{
		if(PropertiesUtil.getAppProperty(ENVIRONMENT).equalsIgnoreCase(LOCAL.toString()))
			this.env=LOCAL;
		if(PropertiesUtil.getAppProperty(ENVIRONMENT).equalsIgnoreCase(STAGING.toString()))
			this.env=STAGING;
		if(PropertiesUtil.getAppProperty(ENVIRONMENT).equalsIgnoreCase(PRODUCTION.toString()))
			this.env=PRODUCTION;
	}
	
	
	@Override
	public String toString() {
		return "TransactionManager [mongoClient=" + mongoClient + ", env=" + env + ", db=" + db + ", isAuthenticated="
				+ isAuthenticated + "]";
	}


	public MongoClient getMongoClient() {
		return mongoClient;
	}


	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}


	public Environments getEnv() {
		return env;
	}


	public void setEnv(Environments env) {
		this.env = env;
	}


	public DB getDb() {
		return db;
	}


	public void setDb(DB db) {
		this.db = db;
	}


	public boolean isAuthenticated() {
		return isAuthenticated;
	}


	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}


	public void startTransaction()
	{
						setEnvironment();
		try {
			switch (this.env)
			{
			case LOCAL:  this.mongoClient= new MongoClient(PropertiesUtil.getDBConnectionProperty(API_HOST), Integer.parseInt(PropertiesUtil.getDBConnectionProperty(API_PORT)));
						 break;
			case STAGING:
						this.mongoClient= new MongoClient( PropertiesUtil.getDBConnectionProperty(API_HOST) ,Integer.parseInt(PropertiesUtil.getDBConnectionProperty(API_PORT)));
						break;
			case PRODUCTION:
							this.mongoClient= new MongoClient( PropertiesUtil.getDBConnectionProperty(API_HOST) , Integer.parseInt(PropertiesUtil.getDBConnectionProperty(API_PORT)));
							 break;
			default: this.mongoClient= new MongoClient( PropertiesUtil.getDBConnectionProperty(API_HOST), Integer.parseInt(PropertiesUtil.getDBConnectionProperty(API_PORT)));
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
		System.out.println(env);
		System.out.println(" databaseManager.getDb().getName() ::  "+databaseManager.getDb().getName());
		System.out.println(" databaseManager.getDb() ::  "+databaseManager.getDb());
		System.out.println(" Environments.STAGING.equals(type) "+Environments.STAGING.equals(env));
		System.out.println(" isAuthenticated "+isAuthenticated);
		if(Environments.STAGING.equals(env)/*&&!isAuthenticated*/)
		{
			databaseManager.getDb().authenticate(PropertiesUtil.getDBConnectionProperty(API_USERNAME), PropertiesUtil.getDBConnectionProperty(API_PASSWORD).toCharArray());
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