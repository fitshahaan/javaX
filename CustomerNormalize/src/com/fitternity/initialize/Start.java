package com.fitternity.initialize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.json.simple.JSONObject;

import com.fitternity.dao.collections.CityDao;
import com.fitternity.dao.collections.LocationClusterDao;
import com.fitternity.dao.collections.LocationDao;
import com.fitternity.enums.Collections;
import com.fitternity.enums.Databases;
import com.fitternity.manager.JobSchedulerManager;
import com.fitternity.manager.TransactionManager;
import com.fitternity.services.GoogleApiServices;
import com.fitternity.services.ReverseMigrateApiServices;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBObject;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * @author Shahaan
 *
 */
public class Start
{
	public static void main(String[] args) 
	{	
			System.out.println(JobSchedulerManager.processJobs()+" JOBS STARTED AND SCHEDULED.");
			
//			System.out.println(new GoogleApiServices().googleGeoCodeOutput("jamia nagar")+" JOBS STARTED AND SCHEDULED.");
			addNewLocations();
//		System.out.println(Boolean.parseBoolean(PropertiesUtil.getAppProperty(NORM_CUST_JOB)));
	}
	
	
	public static void addNewLocations()
	{
		/*TransactionManager transactionManager = new TransactionManager();
		transactionManager.startTransaction();
		LocationDao locationDao = (LocationDao) transactionManager.getDatabaseManager(Databases.FITAPI).getCollection(Collections.LOCATIONS);
		System.out.println(locationDao.getTopLocationID(3));*/
		
		
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
	
	
}