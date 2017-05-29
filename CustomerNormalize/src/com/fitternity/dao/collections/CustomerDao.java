package com.fitternity.dao.collections;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.dao.beans.Customer;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class CustomerDao extends BaseCollection
{
	DBCollection collection;

	public CustomerDao(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public CustomerDao() {
		// TODO Auto-generated constructor stub
	}
	public DBObject getCustomer(int id) {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		DBCursor cursor = collection.find(query);
		while(cursor.hasNext()){
		
		return cursor.next();
		}
		/*try {
			formatToFields(cursor);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;*/
		return query;
	}
	public DBObject getCustomerBasedOnEmail(String emailId) {
		DBObject query = BasicDBObjectBuilder.start().add("email", emailId).get();
		DBObject cursor = collection.findOne(query);
		System.out.println("getCustomerBasedOnEmail size  "+cursor.keySet().size());
		return cursor;
	}
	
	public DBObject getCustomerBasedOnPhone(String contactNo) {
		DBObject query = BasicDBObjectBuilder.start().add("contact_no", contactNo).get();
		System.out.println("query "+query);
		DBObject cursor = collection.findOne(query);
		System.out.println("getCustomerBasedOnPhone size  "+cursor.keySet().size());
		System.out.println("getCustomerBasedOnPhone id  "+cursor.get("_id"));
		return cursor;
	}
	
	public DBObject getCustomerOnOzoneTel(String contactNo) {
		if(contactNo!=null&&!contactNo.equals("")&&contactNo.length()>10)
		{
			contactNo=contactNo.substring(contactNo.length()-11, contactNo.length());
		}
		 BasicDBObject regexQuery = new BasicDBObject();
		    regexQuery.put("contact_no",new BasicDBObject("$regex", contactNo).append("$options", "i"));

//		System.out.println("query "+regexQuery);
		
		DBObject cursor = collection.findOne(regexQuery);
//		System.out.println("getCustomerBasedOnPhone size  "+cursor);
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
	
	
	
}
