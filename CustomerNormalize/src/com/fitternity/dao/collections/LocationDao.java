package com.fitternity.dao.collections;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.dao.beans.Customer;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * @author shahaan
 *
 */
public class LocationDao extends BaseCollection
{
	DBCollection collection;

	public LocationDao(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public LocationDao() {
		// TODO Auto-generated constructor stub
	}

	
	public DBObject getLocation(Number id) {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		DBObject cursor = collection.findOne(query);
		System.out.println("getLocation "+query);
		return cursor;	
	}
	
	
	public Number getTopLocationID(int topElement) {
		if(topElement<1)
			throw new IllegalArgumentException(" Invalid top Element value");
//		db.collection.find().sort({_id:-1}).limit(1).pretty()
		DBObject query =  new BasicDBObject();
		DBCursor dbCursor = collection.find(query).sort(new BasicDBObject("_id",-1)).limit(topElement);
		System.out.println( "   dbCursor :: "+dbCursor.count());
		System.out.println(" topElement :: "+topElement);
		while (dbCursor.hasNext())
		{
			DBObject topCustObject=dbCursor.next();
			if(topCustObject!=null)
			{
				if(topElement!=1)
					topElement--;
				else
						{
						if(topCustObject.get("_id")!=null&&!"".equals(topCustObject.get("_id")))
							return (Number)topCustObject.get("_id");
						else return -10000;
						}	
			}
			else return -10000;
		}
		return -10000;
	}
	
	
	public Number getTopLocationID() {
//		db.collection.find().sort({_id:-1}).limit(1).pretty()
		DBObject query =  new BasicDBObject();
		DBCursor dbCursor = collection.find(query).sort(new BasicDBObject("_id",-1)).limit(1);
//		System.out.println( "   dbCursor :: "+dbCursor);
		while (dbCursor.hasNext())
		{
			DBObject topCustObject=dbCursor.next();
//			System.out.println(topCustObject);
			if(topCustObject!=null)
				if(topCustObject.get("_id")!=null&&!"".equals(topCustObject.get("_id")))
					return (Number)topCustObject.get("_id");
				else return -10000;
			else return -10000;
		}
		return -10000;
	}
	public DBObject getCustomerBasedOnEmail(String emailId) {
		DBObject query = BasicDBObjectBuilder.start().add("email", emailId).get();
		DBObject cursor = collection.findOne(query);
		System.out.println("getCustomerBasedOnEmail size  "+cursor);
		return cursor;
	}
	
	public DBObject getCustomerBasedOnPhone(String contactNo) {
		DBObject query = BasicDBObjectBuilder.start().add("contact_no", contactNo).get();
		System.out.println("query "+query);
		DBObject cursor = collection.findOne(query);
//		System.out.println("getCustomerBasedOnPhone size  "+cursor.keySet().size());
//		System.out.println("getCustomerBasedOnPhone id  "+cursor.get("_id"));
		return cursor;
	}
	
	public DBObject getCustomerOnOzoneTel(String contactNo) {
		if(contactNo==null||"".equals(contactNo))
			return null;
		else if(contactNo.length()>10)
		{
			contactNo=contactNo.substring(contactNo.length()-11, contactNo.length());
		}
		
		 BasicDBObject regexQuery = new BasicDBObject();
		    regexQuery.put("contact_no",new BasicDBObject("$regex", contactNo).append("$options", "i"));

		System.out.println("[ getCustomerOnOzoneTel ] query "+regexQuery);
		
		DBObject cursor = collection.findOne(regexQuery);
		System.out.println("[ getCustomerOnOzoneTel]  size  "+cursor);
//		System.out.println("getCustomerBasedOnPhone id  "+cursor.get("_id"));
		return cursor;
	}
	
	
	public void formatToFields(DBCursor cursor) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		
		while(cursor.hasNext()){
			Customer customer=new Customer();
			DBObject dd=cursor.next();
			Set keys=dd.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {

					String key=iterator.next();
					
					System.out.println(dd.get(key).toString());
					System.out.println(key);
					Field field=customer.getClass().getField(key);
					System.out.println(field.toGenericString());
					if(field!=null)
						field.set("_id", dd.get(key).toString());
					else
						System.out.println(field.toGenericString());
			}
			System.out.println(customer.toString());
			System.out.println(cursor.next());
		}
		
	}
	public boolean addNewCustomer(DBObject dbObject) {
//		System.out.println(" NEW CUSTOMER INFO :: "+dbObject);
		return ((collection.insert(dbObject)!=null)?true:false);
	}
	
	public BulkWriteOperation getBulkWriteOp()
	{
		BulkWriteOperation  bulkWriteOperation= collection.initializeUnorderedBulkOperation();
		return bulkWriteOperation;
	}
	
	public void updateSubscribedCustomers() 
	{
		DBObject query = BasicDBObjectBuilder.start().add("unsubscribed_customer", new BasicDBObject("$exists", false)).get();
		System.out.println("query "+query);
		DBObject updateSet=BasicDBObjectBuilder.start().add("$set", new BasicDBObject("unsubscribed_customer",false)).get();
		WriteResult cursor = collection.updateMulti(query,updateSet);
//		System.out.println("getCustomerBasedOnPhone size  "+cursor.keySet().size());
//		System.out.println("getCustomerBasedOnPhone id  "+cursor.get("_id"));
		System.out.println(cursor.getUpsertedId());
		System.out.println(cursor.getN());
		System.out.println(cursor.isUpdateOfExisting());
	}
	public DBCursor getAllIncorrectLocations() {
		// TODO Auto-generated method stub
		
			DBObject query = BasicDBObjectBuilder.start().add("geometry.coordinates.0", new BasicDBObject("$exists",false)).get();
			DBCursor cursor = collection.find(query);
//			while(cursor.hasNext()){	
			return cursor;
		}
	public DBCursor getAllLocations() {
		// TODO Auto-generated method stub
		
			DBObject query = BasicDBObjectBuilder.start().add("geometry.coordinates.0", new BasicDBObject("$exists",true)).get();
			DBCursor cursor = collection.find(query);
//			while(cursor.hasNext()){	
			return cursor;
		}	
	
}
