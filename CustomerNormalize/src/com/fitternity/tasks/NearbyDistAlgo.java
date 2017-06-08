package com.fitternity.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.fitternity.constants.AppConstants;
import com.fitternity.dao.collections.CityDao;
import com.fitternity.dao.collections.LocationDao;
import com.fitternity.dao.collections.VendorDao;
import com.fitternity.enums.Collections;
import com.fitternity.enums.Databases;
import com.fitternity.manager.TransactionManager;
import com.fitternity.services.GoogleApiServices;
import com.fitternity.util.CoreDistAlgo;
import com.fitternity.util.PropertiesUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
/**
 * @author Shahaan
 *
 */
public class NearbyDistAlgo {
	
	public  void fetchIncorrectLocations()
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

}
