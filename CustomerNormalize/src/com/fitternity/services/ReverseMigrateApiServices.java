package com.fitternity.services;

import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fitternity.abstracthelpers.FitternityApiService;
import com.fitternity.constants.AppConstants;
import com.fitternity.constants.FitConstants;
import com.fitternity.enums.GoogleApis;
import com.fitternity.util.PropertiesUtil;
/**
 * @author Shahaan
 *
 */
public class ReverseMigrateApiServices extends FitternityApiService implements AppConstants {
	
	
	public String reverseMigrateLocation(final int id)
	{
		try 
		{
			System.out.println(" [reverseMigrateLocation ]:: "+PropertiesUtil.getAppProperty(LOC_REV_MIG_API)+"/"+id);
		   return sendGet(PropertiesUtil.getAppProperty(LOC_REV_MIG_API)+id);
		} 
		 catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}

	public String startCustomerCron(final String job)
	{
		try 
		{
					return sendGet(PropertiesUtil.getAppProperty(AppConstants.CRON_START_API)+"job="+job,"cron");					
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
		return null;
	}
}
