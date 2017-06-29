package com.fitternity.dao.collections;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.dao.beans.Customer;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * @author shahaan
 *
 */
public class DeviceDao extends BaseCollection
{
	DBCollection collection;

	public DeviceDao(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public DeviceDao() {
		// TODO Auto-generated constructor stub
	}
	
	public DBCursor getAllData() {
		 BasicDBObject regexQuery = new BasicDBObject();
		DBCursor dbCursor= collection.find(regexQuery);
		dbCursor.setOptions(Bytes.QUERYOPTION_NOTIMEOUT);
		return dbCursor;
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
	
	public BulkWriteOperation getBulkWriteOp()
	{
		BulkWriteOperation  bulkWriteOperation= collection.initializeUnorderedBulkOperation();
		return bulkWriteOperation;
	}
	
	
}
