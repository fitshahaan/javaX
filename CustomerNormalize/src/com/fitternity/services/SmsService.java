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
import com.google.gson.JsonObject;
/**
 * @author Shahaan
 *
 */
public class SmsService extends FitternityApiService implements AppConstants {
	
	
	public String sendSingleSms(JsonObject body)
	{
		try 
		{
			System.out.println(" [smsService ]:: "+PropertiesUtil.getAppProperty(SMS_API));
		    return sendPost(PropertiesUtil.getAppProperty(SMS_API),body);
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
