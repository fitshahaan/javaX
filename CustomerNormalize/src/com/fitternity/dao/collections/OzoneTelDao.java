package com.fitternity.dao.collections;

import java.util.ArrayList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteResult;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBObject;
import com.mongodb.BulkUpdateRequestBuilder;
import com.mongodb.BulkWriteException;

import org.bson.types.ObjectId;

import com.fitternity.abstracthelpers.BaseCollection;
import com.fitternity.dao.beans.Customer;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class OzoneTelDao extends BaseCollection
{
	DBCollection collection;

	public OzoneTelDao(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public OzoneTelDao() {
		// TODO Auto-generated constructor stub
	}
/*	public DBCursor getCaptures(BasicDBObject regExObject) {
		DBObject query = BasicDBObjectBuilder.start().add("12", new BasicDBObject()).get();
		DBCursor cursor = collection.find(query);
		return cursor;
	}*/
	public DBCursor getOzoneTelCaps() 
	{
		BasicDBObject and_conditions[] = {/*new BasicDBObject("customer_contact_no", new BasicDBObject("$exists",true)),new BasicDBObject("customer_contact_no",new BasicDBObject("$ne","")),*/new BasicDBObject("customer exists",new BasicDBObject("$exists",false))};
		DBObject query = BasicDBObjectBuilder.start()
				.add("$and", and_conditions).get();
		DBCursor cursor = collection.find(query);
		cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		System.out.println(" MAIN UNCETAIN CURSOR SIZE" +cursor.count());
		return  cursor;
	}
	public void updateOzoneTelCaptOnCustomerId(boolean exists, DBObject currentOzoneTelCapObject) {
		
		DBObject query = BasicDBObjectBuilder.start().add("_id",(Number)currentOzoneTelCapObject.get("_id")).get();
				DBObject updateSet=BasicDBObjectBuilder.start().add("$set", new BasicDBObject("customer exists",exists)).get();
		WriteResult cursor = collection.update(query,updateSet);
			
		System.out.println(cursor.getUpsertedId());
		System.out.println(cursor.getN());
		System.out.println(cursor.isUpdateOfExisting());
		System.out.println(cursor.getLastConcern());
	}
	

	// Get  BulkWriteOperation by accessing the mongodb com.mongodb.DBCollection class on mycol //Collection
public BulkWriteOperation getBulkWriteOp()
{
	BulkWriteOperation  bulkWriteOperation= collection.initializeUnorderedBulkOperation();
	return bulkWriteOperation;
}

	


}
