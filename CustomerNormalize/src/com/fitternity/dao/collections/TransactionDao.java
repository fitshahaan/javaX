package com.fitternity.dao.collections;

import java.util.ArrayList;

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

/**
 * @author shahaan
 *
 */
public class TransactionDao extends BaseCollection
{
	DBCollection collection;

	public TransactionDao(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public TransactionDao() {
		// TODO Auto-generated constructor stub
	}
	public DBCursor getTransactions(BasicDBObject regExObject) {
		DBObject query = BasicDBObjectBuilder.start().add("12", new BasicDBObject()).get();
		DBCursor cursor = collection.find(query);
		return cursor;
	}
	public DBCursor getUncertainTransactions() {
//		DBObject query = BasicDBObjectBuilder.start()
//		.add("customer_id", new BasicDBObject("$type", 2)).get();

		ArrayList<BasicDBObject> basicDBObjects=new ArrayList<>();
		
//		basicDBObjects.add(new BasicDBObject("customer_id",new BasicDBObject("$type", 2)));
		//basicDBObjects.add(new BasicDBObject("$type", 2));
		
		BasicDBObject orConds[] = {new BasicDBObject("customer_id", new BasicDBObject("$type", 2)),new BasicDBObject("customer_id", new BasicDBObject("$eq", -1)),new BasicDBObject("customer_id", new BasicDBObject("$exists", false))/*,new BasicDBObject("$where", "this.customer_id.length>5")*/};
//		BasicDBObject orConds[] = {new BasicDBObject("customer_id", 89609)};
		//query = new BasicDBObject("$or", or_conditions);
		DBObject query = BasicDBObjectBuilder.start().add("$or", orConds).get();
		System.out.println(" QUERY :: "+query);
		System.out.println(collection.getName());
		DBCursor cursor = collection.find(query);
		cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		
		System.out.println(" MAIN UNCETAIN CURSOR SIZE" +cursor.count());
		//while(cursor.hasNext()){
		//System.out.println(cursor.next());
		//}
		return  cursor;

}
public void updateTransOnCustomerId(Number cid, DBObject currentTransObject) {
		
		ObjectId objId=new ObjectId(currentTransObject.get("_id").toString());
		System.out.println(" OBJID "+objId);
		DBObject query = BasicDBObjectBuilder.start().add("_id",objId).get();
				DBObject updateSet=BasicDBObjectBuilder.start().add("$set", new BasicDBObject("customer_id",cid)).get();
		WriteResult cursor = collection.update(query,updateSet);
			
		System.out.println(cursor.getUpsertedId());
		System.out.println(cursor.getN());
		System.out.println(cursor.isUpdateOfExisting());
		System.out.println(cursor.getLastConcern());
	}
	
}
