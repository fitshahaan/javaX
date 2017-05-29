package com.fitternity.manager;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.abstracthelpers.BaseDatabase;
import com.fitternity.dao.collections.CustomerDao;
import com.fitternity.dao.collections.OzoneTelDao;
import com.fitternity.dao.collections.TransactionDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.dao.databases.FitAdmin;
import com.fitternity.dao.databases.FitApi;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class TransactionManager 

{
	MongoClient mongoClient ; 
	DB db; 
	
	public MongoClient getConnection(String type) 
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
				MongoClient mongo = new MongoClient(new ServerAddress("localhost", 27017), auths);
				return 	this.mongoClient;
			}
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	public boolean startTransaction(String type) 
	{
			// TODO Auto-generated method stub
		try 
		{
			if(type.equals("local"))
			{
				this.mongoClient= new MongoClient( "localhost" , 27017 );
				return true;		
			}
			else
			{
				MongoCredential journaldevAuth = MongoCredential.createPlainCredential("pankaj", "journaldev", "pankaj123".toCharArray());
//				MongoCredential testAuth = MongoCredential.createPlainCredential("pankaj", "test", "pankaj123".toCharArray());
				ArrayList<MongoCredential> auths=new ArrayList<>();
				auths.add(journaldevAuth);
				MongoClient mongo = new MongoClient(new ServerAddress("localhost", 27017), auths);
				return 	true;
			}
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			return false;
		}
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
	public DatabaseManager getDatabaseManager(String db) 
	{
		return new DatabaseManager(this.mongoClient,db);
		
	}	
	
}
