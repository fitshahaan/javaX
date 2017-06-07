package com.fitternity.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fitternity.abstracthelpers.FitternityApiService;
import com.fitternity.constants.AppConstants;
import com.fitternity.enums.GoogleApis;
import com.fitternity.util.PropertiesUtil;
/**
 * @author Shahaan
 *
 */
public class GoogleApiServices extends FitternityApiService implements AppConstants {
	
	
	public JSONObject googleGeoCodeOutput(final String address)
	{
		try 
		{
		   return processData(sendGet(PropertiesUtil.getAppProperty(GOOGLE_GEO_API)+address+"&key="+PropertiesUtil.getAppProperty(GOOGLE_GEO_API_KEY)),GoogleApis.GEOCODE);
		} 
		 catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected JSONObject processData(final String data,GoogleApis api)
	{
		System.out.println(" INPUT DATA :: " +data);
		switch (api)
			{
				case GEOCODE:	try {
										JSONObject d=(JSONObject)new JSONParser().parse(data);
										System.out.println( " d.containsKey(lng) :: "+d.containsKey("lng"));										JSONArray results=(JSONArray)d.get("results");
										JSONObject results1=(JSONObject)results.get(0);
										JSONObject geometry=(JSONObject)results1.get("geometry");
										JSONObject location=(JSONObject)geometry.get("location");
										return location;
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
			}
						
				default:
							break;
		}	
		return null;
	}
	
	
}
