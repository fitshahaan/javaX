package com.fitternity.dao.collections;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.bson.types.ObjectId;

import com.fitternity.abstracthelpers.BaseCollection;
import com.mongodb.BasicDBList;
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
	
	public HashMap[] getAggregatedCustomersGAT() {

		ArrayList<DBObject> pipelines=new ArrayList<>();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -6);
		Date sixOld = cal.getTime();
		
		pipelines.add(BasicDBObjectBuilder.start().add("$match", new BasicDBObject("created_at", new BasicDBObject("$gte", sixOld)).append("finder_id", new BasicDBObject("$exists", true)).append("customer_id", new BasicDBObject("$exists", true))).get());
		
		
		BasicDBList types=new BasicDBList();
		types.add("memberships");
		types.add("healthytiffinmembership");
		BasicDBList failedCond=new BasicDBList();
		
		failedCond.add(new BasicDBObject("$match", new BasicDBObject("transaction_type", "Order").append("status", "0").append("created_at", new BasicDBObject("$exists", true)).append("type", new BasicDBObject("$in",types ))));
		failedCond.add(BasicDBObjectBuilder.start().add("$group", 
				new BasicDBObject("_id", "$customer_id").
				append("details", new BasicDBObject("$push",
				new BasicDBObject("name", "$customer_name")
				.append("updated_at", "$created_at")
				.append("vendorId", "$finder_id")))).get());
		failedCond.add(new BasicDBObject("$project", new BasicDBObject("details", 1)));
		
		BasicDBList SuccessCond=new BasicDBList();
		SuccessCond.add(new BasicDBObject("$match", new BasicDBObject("transaction_type", "Order").append("status", "1").append("created_at", new BasicDBObject("$exists", true)).append("type", new BasicDBObject("$in",types ))));
		SuccessCond.add(BasicDBObjectBuilder.start().add("$group", 
				new BasicDBObject("_id", "$customer_id").
				append("details", new BasicDBObject("$push",
				new BasicDBObject("name", "$customer_name")
				.append("updated_at", "$created_at")
				.append("vendorId", "$finder_id")))).get());
		SuccessCond.add(new BasicDBObject("$project", new BasicDBObject("details", 1)));
		
		pipelines.add(BasicDBObjectBuilder.start().add("$facet", new BasicDBObject("sc",SuccessCond).append("fc", failedCond)).get());
		
		System.out.println(pipelines.toString());
		
		
		Iterator<DBObject> output =this.collection.aggregate(pipelines).results().iterator();
		HashMap<Number, Object[]> sMap=new HashMap<>();
		HashMap<Number, Object[]> fMap=new HashMap<>();
		while (output.hasNext()) 
		{
			DBObject dbObject = (DBObject) output.next();
			System.out.println(dbObject.get("sc"));
			BasicDBList scMain=(BasicDBList)dbObject.get("sc");
			BasicDBList fcMain=(BasicDBList)dbObject.get("fc");
			for (Iterator iterator = scMain.iterator(); iterator.hasNext();) {
				DBObject scSingle = (DBObject) iterator.next();
				System.out.println(scSingle);
				BasicDBList details=(BasicDBList)scSingle.get("details");
			    sMap.put((Number)scSingle.get("_id"), getTopDate(details));
			}
			
			for (Iterator iterator = fcMain.iterator(); iterator.hasNext();) {
				DBObject fcSingle = (DBObject) iterator.next();
				System.out.println(fcSingle);
				BasicDBList details=(BasicDBList)fcSingle.get("details");
				fMap.put((Number)fcSingle.get("_id"), getTopDate(details));
			}
			
			System.out.println("SMAP :: "+sMap.size());
			System.out.println("FMAP :: "+fMap.size());
			
			System.out.println("SC :: "+scMain.size());
			System.out.println("FC:" +fcMain.size());
			
		}
		return new HashMap[]{sMap,fMap};
		
		/*BasicDBObject orConds[] = {new BasicDBObject("customer_id", new BasicDBObject("$type", 2)),new BasicDBObject("customer_id", new BasicDBObject("$eq", -1)),new BasicDBObject("customer_id", new BasicDBObject("$exists", false)),new BasicDBObject("$where", "this.customer_id.length>5")};
		
		DBObject query = BasicDBObjectBuilder.start().add("$or", orConds).get();
		System.out.println(" QUERY :: "+query);
		System.out.println(collection.getName());
		DBCursor cursor = collection.find(query);
		cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		System.out.println(" MAIN UNCETAIN CURSOR SIZE" +cursor.count());
		*/
//		return  cursor;

}
	
	
private Object[] getTopDate(BasicDBList details) {
		// TODO Auto-generated method stub
	
	Date topDate=null;
	Number vendorId=null;
	for (Iterator iterator = details.iterator(); iterator.hasNext();) {
		DBObject detail= (DBObject) iterator.next();
//			DBObject ud=(DBObject)detail.get("updated_at");
//			Date d1=(Date)ud.get("$date");
			Date d1=(Date)detail.get("updated_at");
			vendorId=(Number)detail.get("vendorId");
			if(topDate==null)
				topDate=d1;
			else if(d1.after(topDate))
					topDate=d1;
	}
		return new Object[]{topDate,vendorId};
	}
	public DBCursor getSchdeuledDateData() {
		
		BasicDBObject orConds[] = {new BasicDBObject("transaction_type", "Order"),new BasicDBObject("schedule_date", new BasicDBObject("$exists", true))};
		DBObject query = BasicDBObjectBuilder.start().add("$and", orConds).get();
		
		System.out.println(" QUERY :: "+query);

		DBCursor cursor = collection.find(query);
		cursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		
		System.out.println(" MAIN UNCETAIN CURSOR SIZE" +cursor.count());
		
		return  cursor;

}
	
	
	
	
	
	
	public BulkWriteOperation getBulkWriteOp()
{
	BulkWriteOperation  bulkWriteOperation= collection.initializeUnorderedBulkOperation();
	return bulkWriteOperation;
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
	}
	
}
