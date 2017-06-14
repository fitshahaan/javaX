package com.fitternity.initialize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.fitternity.constants.AppConstants;
import com.fitternity.dao.collections.CityDao;
import com.fitternity.dao.collections.LocationClusterDao;
import com.fitternity.dao.collections.LocationDao;
import com.fitternity.dao.collections.ReviewDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.enums.Collections;
import com.fitternity.enums.Databases;
import com.fitternity.manager.JobSchedulerManager;
import com.fitternity.manager.TaskManager;
import com.fitternity.manager.TransactionManager;
import com.fitternity.services.GoogleApiServices;
import com.fitternity.services.ReverseMigrateApiServices;
import com.fitternity.util.CoreDistAlgo;
import com.fitternity.util.PropertiesUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Shahaan
 *
 */
public class Start
{
	public static void main(String[] args) 
	{	
//			System.out.println(JobSchedulerManager.processJobs()+" JOBS STARTED AND SCHEDULED.");
		
//			System.out.println(Haversine.distance(19.1363246, 72.82765999999999, 19.1229326, 72.83013059999999));
//			fetchIncorrectLocations();
//			System.out.println(PropertiesUtil.getCronProperty("normalizeCustomerIdPeriod"));
//			System.out.println(TaskManager.processTasks());
			migrationTask();
//			System.out.println(new GoogleApiServices().googleGeoCodeOutput("jamia nagar")+" JOBS STARTED AND SCHEDULED.");

			
			
			
			//			ReverseMigrateApiServices reverseMigrateApiServices=new ReverseMigrateApiServices();
//			reverseMigrateApiServices.startCustomerCron();
//			addNewLocations();
//		System.out.println(Boolean.parseBoolean(PropertiesUtil.getAppProperty(NORM_CUST_JOB)));
	}
	
	
	public static void addNewLocations()
	{	
		try {
			ReverseMigrateApiServices reverseMigrateApiServices=new ReverseMigrateApiServices();
			GoogleApiServices googleApiServices=new GoogleApiServices();
			TransactionManager transactionManager = new TransactionManager();
			transactionManager.startTransaction();
			LocationDao locationDao = (LocationDao) transactionManager.getDatabaseManager(Databases.FITAPI).getCollection(Collections.LOCATIONS);
			LocationClusterDao locationClusterDao = (LocationClusterDao) transactionManager.getDatabaseManager(Databases.FITAPI).getCollection(Collections.LOCATIONCLUSTERS);
			CityDao cityDao= (CityDao)transactionManager.getDatabaseManager(Databases.FITAPI).getCollection(Collections.CITIES);
			
			
			BulkWriteOperation bulkWriteOperation = locationDao.getBulkWriteOp();

			BufferedReader br=new BufferedReader(new FileReader(new File(System.getProperty("user.dir")+"/input/LocationsPopulate/Locations - Delhi NCR - Location.csv")));
			String line="";
			int counter=0;
			boolean firstLine=true;
			boolean maxTaken=false;
			boolean idPresent=false;
			ArrayList<Integer> ids=new ArrayList<>();
//		System.out.println(" TOTAL LINES "+br.lines());
			int id=-1;
			while((line=br.readLine())!=null)
			{
				String currentData[]=line.split(",");
				JSONObject geometry;
				Number lat=-1;
				Number lng=-1;
				Number locationcluster_id = -1;
				Number city_id=-1;
				String vendorSlug="";
				if(currentData!=null&&currentData.length>0)
				{
					if(firstLine)
					{
						if(currentData[0]!=null&&!"".equalsIgnoreCase(currentData[0])&&"ID".equalsIgnoreCase(currentData[0].trim()))
						{
							idPresent=true;
						}
						firstLine=false;
						continue;
					}
					
					if(currentData[0]!=null&&!"".equalsIgnoreCase(currentData[0])&&!idPresent)
					{
						if(maxTaken)
							id=id+1;
						else
						{
							id=locationDao.getTopLocationID(2).intValue()+1;
							maxTaken=true;
						}
					}
					
					
					if(currentData[1]!=null&&!"".equalsIgnoreCase(currentData[1]))
					{
						
						vendorSlug=currentData[1].trim().replaceAll(" ", "-").toLowerCase();
						geometry=googleApiServices.googleGeoCodeOutput(currentData[1]);
						if(geometry!=null)
						{
							System.out.println("gEO AAAYA");
							lat=(Number)geometry.get("lat");
							lng=(Number)geometry.get("lng");						
						}
						else continue;
					}
					if(currentData[2]!=null&&!"".equalsIgnoreCase(currentData[2]))
					{
						DBObject lc=locationClusterDao.getCluster(currentData[2]);
						System.out.println("LOC AAYA "+lc+" CLUSTER :: "+currentData[2]);
						if(lc!=null)
							locationcluster_id=(Number)lc.get("_id");
						else
							continue;
					}
					if(currentData[3]!=null&&!"".equalsIgnoreCase(currentData[3]))
					{
						DBObject city=cityDao.getCity(currentData[3]);
						System.out.println("city AAYA "+city);
						if(city!=null)
							city_id=(Number)city.get("_id");
						else
							continue;
					}
					
					ids.add(id);
					if(idPresent)
					{				
							System.out.println(" UPDATE ");
							double geo[]=new double[2];
							geo[0]=lat.doubleValue();
							geo[1]=lng.doubleValue();
							DBObject mainQuery=new BasicDBObject("$set",new BasicDBObject("geometry",new BasicDBObject("coordinates",geo)).
									append("city_id", city_id.intValue()).
									append("locationcluster_id", locationcluster_id.intValue()).
									append("name",currentData[1] ).
									append("updated_at", new Date()).
									append("hidden", false).
									append("vendors", new int[0]).
									append("location_group", (currentData[4]!=null&&!"".equalsIgnoreCase(currentData[4]))?currentData[4].trim().toLowerCase():"general").
									append("slug",vendorSlug)
									);
							System.out.println(" MAIN QUERY :: "+mainQuery);
							bulkWriteOperation.find(new BasicDBObject("_id",Integer.parseInt(currentData[0]))).update(mainQuery);		
					}
					else
					{
						System.out.println(" INSERT ");
//						".coordinates.0"
						double geo[]=new double[2];
						geo[0]=lat.doubleValue();
						geo[1]=lng.doubleValue();
						DBObject mainQuery=new BasicDBObject("geometry",new BasicDBObject("coordinates",geo)).
								append("city_id", city_id.intValue()).
								append("locationcluster_id", locationcluster_id.intValue()).
								append("name",currentData[1] ).
								append("created_at", new Date()).
								append("updated_at", new Date()).
								append("hidden", false).
								append("_id",id).
								append("vendors", new int[0]).
								append("location_group", (currentData[4]!=null&&!"".equalsIgnoreCase(currentData[4]))?currentData[4].toLowerCase():"general").
								append("slug",vendorSlug);
						System.out.println(" MAIN QUERY :: "+mainQuery);
						bulkWriteOperation.insert(mainQuery);
					}
					
					counter++;
				System.out.println(counter);
						if (counter == 5000) 
						{
							System.out.println(" WITHIN 5000" + bulkWriteOperation.execute());
							counter= 0;
							bulkWriteOperation = locationDao.getBulkWriteOp();
							for (Integer it : ids) 
							{
									System.out.println(reverseMigrateApiServices.reverseMigrateLocation(it));	
							}
							ids.clear();
						}
				}
			}
				
							if (counter<5000&&counter>0) 
							{
								System.out.println(" AFTER 5000" + bulkWriteOperation.execute());
								bulkWriteOperation = locationDao.getBulkWriteOp();
								for (Integer it : ids) 
								{
									System.out.println(reverseMigrateApiServices.reverseMigrateLocation(it));	
								}
								ids.clear();
							}
		}
		catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public static void fetchIncorrectLocations()
	{
		try
		{
			File a=new File(System.getProperty("user.dir")+"/vendors.txt");
			FileWriter fw=new FileWriter(a);
			fw.write("ID-NAME-CITY-LOCATION"+System.lineSeparator());
			GoogleApiServices googleApiServices=new GoogleApiServices();
			TransactionManager transactionManager = new TransactionManager();
			transactionManager.startTransaction();
			LocationDao locationDao = (LocationDao) transactionManager.getDatabaseManager(Databases.FITAPI).getCollection(Collections.LOCATIONS);
			VendorDao vendorDao = (VendorDao) transactionManager.getDatabaseManager(Databases.FITAPI).getCollection(Collections.VENDORS);
			CityDao cityDao = (CityDao) transactionManager.getDatabaseManager(Databases.FITAPI).getCollection(Collections.CITIES);
			
			DBCursor incorrectLocationCursor=locationDao.getAllIncorrectLocations();
			Number lat=-1;
			Number lng=-1;
			BulkWriteOperation bulkWriteOperation = locationDao.getBulkWriteOp();
			int locationInsertCounter=0;
			while(incorrectLocationCursor.hasNext())
			{
				DBObject currentLocation=incorrectLocationCursor.next();
				JSONObject geometry=googleApiServices.googleGeoCodeOutput((String)currentLocation.get("name"));
				if(geometry!=null)
				{
					System.out.println("gEO AAAYA");
					lat=(Number)geometry.get("lat");
					lng=(Number)geometry.get("lng");						
				
				double geo[]={lat.doubleValue(),lng.doubleValue()};
				DBObject mainQuery=new BasicDBObject("$set",new BasicDBObject("geometry",new BasicDBObject("coordinates",geo)));
//				System.out.println(" MAIN QUERY :: "+mainQuery);
				bulkWriteOperation.find(new BasicDBObject("_id",Integer.parseInt(currentLocation.get("_id").toString()))).update(mainQuery);
				locationInsertCounter++;
				System.out.println("locationInsertCounter :: "+locationInsertCounter);
				}
			}
//			System.out.println("locationCoordinates Insert  "+bulkWriteOperation.execute());
//			bulkWriteOperation=locationDao.getBulkWriteOp();
			
			DBCursor incorrectSecondaryLocationsCursor=vendorDao.getSingularSecondaryLocations();
			DBCursor incorrectAllLocationsCursor=locationDao.getAllLocations();
			
			HashMap<Number, BasicDBList> idToLocs=new HashMap<>();
			HashMap<Number, String> idToName=new HashMap<>();
			while(incorrectAllLocationsCursor.hasNext())
			{
				DBObject currentLocation=incorrectAllLocationsCursor.next();			
				if(currentLocation!=null)
				{
					DBObject coordinates=(DBObject)currentLocation.get("geometry");
					if(coordinates!=null)
					{
						
						Number idNumber=(Number)currentLocation.get("_id");
						int id=idNumber.intValue();
						idToLocs.put(id, (BasicDBList)coordinates.get("coordinates"));
						idToName.put(id, (String)currentLocation.get("name"));
					}
				}
					
				System.out.println(currentLocation);
				DBObject coordinates=(DBObject)currentLocation.get("geometry");
				System.out.println(coordinates.get("coordinates"));
				System.out.println((DBObject)currentLocation.get("geometry.coordinates"));
			}
			System.out.println(idToLocs.toString());
			System.out.println(idToLocs.get(1).get(0));
			
			
			
			
			int vendorSecfailedCounter=0;
			
			while(incorrectSecondaryLocationsCursor.hasNext())
			{
				vendorSecfailedCounter++;
				DBObject currentVendor=incorrectSecondaryLocationsCursor.next();
				
				DBObject secondary=(DBObject)currentVendor.get("location");
				BasicDBList locId=(BasicDBList)secondary.get("secondary");
				
				DBObject coordinatesVendor=(DBObject)currentVendor.get("geometry");
				if(coordinatesVendor!=null)
				{
					BasicDBList coordinatesval=(BasicDBList)coordinatesVendor.get("coordinates");
					System.out.println(currentVendor);
					Number vendorLat=(coordinatesval.get(0) instanceof String)?Double.parseDouble((String)coordinatesval.get(0)):(Number)coordinatesval.get(0);
					Number vendorLng=(coordinatesval.get(1) instanceof String)?Double.parseDouble((String)coordinatesval.get(1)):(Number)coordinatesval.get(1);
					
					Number numLoc=(Number)locId.get(0);
					BasicDBList rootLocationCoordinates=idToLocs.get(numLoc.intValue());
					if(rootLocationCoordinates!=null)
					{						
						double startLat =(double)rootLocationCoordinates.get(0);
						double startLong =(double)rootLocationCoordinates.get(1);
						
						double endLat =vendorLat.doubleValue();
						double endLong =vendorLng.doubleValue();
						
						
						if(CoreDistAlgo.distance(startLat, startLong, endLat, endLong)>=Double.parseDouble(PropertiesUtil.getAppProperty(AppConstants.APPROX_DIST)))
						{
							fw.write(currentVendor.get("_id")+"-"+currentVendor.get("name")+"-"+cityDao.getCity((Number)currentVendor.get("city_id")).get("name")+"-"+idToName.get(numLoc.intValue())+System.lineSeparator());
							System.out.println("IHAR AAYA");
						}
						
					}
				}
				
				
				System.out.println("locationInsertCounter :: "+locationInsertCounter);
			}
			fw.close();
			
			

		}
		catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	public static void migrationTask()
	{
		TransactionManager transactionManager = new TransactionManager();
		transactionManager.startTransaction();
		ReviewDao reviewFitAdminDao = (ReviewDao) transactionManager.getDatabaseManager(Databases.FITADMIN).getCollection(Collections.REVIEWS);
		ReviewDao reviewFitApiDao = (ReviewDao) transactionManager.getDatabaseManager(Databases.FITAPI).getCollection(Collections.REVIEWS);
		
			BulkWriteOperation bulkWriteOperation = reviewFitApiDao.getBulkWriteOp();
		
			DBCursor reviewFitadminCursor= reviewFitAdminDao.getAllData();
			while(reviewFitadminCursor.hasNext())
			{
				DBObject dbObject = reviewFitadminCursor.next();
				
				BasicDBList customerReviews=new BasicDBList();
				customerReviews.add(BasicDBObjectBuilder.start().add("description",(String)dbObject.get("description")).append("createdAt",dbObject.get("created_at")).append("rating",(dbObject.get("rating") instanceof String)? Double.parseDouble((String)dbObject.get("rating")):(Number)dbObject.get("rating")).append("detailRating",(BasicDBList)dbObject.get("detail_rating")).get());				
				DBObject outputObject =BasicDBObjectBuilder.start().
				add("_id",(Number)dbObject.get("_id")).
				append("customerId", (Number)dbObject.get("customer_id")).
				append("vendorId", (Number)dbObject.get("finder_id")).
				append("status",Integer.parseInt((String) dbObject.get("status"))).
				append("customerReviews", customerReviews).
				append("vendorReviews", null).append("hull_id", (String)dbObject.get("hull_id")).get();
				
				System.out.println(dbObject);
				System.out.println(outputObject);
				
				
				bulkWriteOperation.insert(outputObject);		
			}
			System.out.println(bulkWriteOperation.execute());
		
	}
	
}