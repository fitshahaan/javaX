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
public class VendorServiceCategoryDao extends BaseCollection
{
	DBCollection collection;

	public VendorServiceCategoryDao(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public VendorServiceCategoryDao() {
		// TODO Auto-generated constructor stub
	}
	
	public DBCursor getSingularSecondaryLocations() {
		DBObject query = BasicDBObjectBuilder.start().add("location.secondary.0", new BasicDBObject("$exists",true)).append("location.secondary.1",new BasicDBObject("$exists",false)).get();
		DBCursor cursor = collection.find(query);
//		while(cursor.hasNext()){	
		return cursor;
//		}
	}
	
	public Number getCategoryId(String name) {
		DBObject query = BasicDBObjectBuilder.start().add("name", name).get();
		BasicDBObject b=new BasicDBObject("name",name );
		System.out.println(" QUERY :: "+b);
		System.out.println(collection.getName());
		DBObject cursor = collection.findOne(b);
		System.out.println("category.primary" +cursor);
		return (Number)cursor.get("_id");	
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
	public DBObject getCategory(Number id) {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		DBObject cursor = collection.findOne(query);
		System.out.println("getCategory "+query);
		return cursor;	
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
	
}
