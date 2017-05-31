package com.fitternity.util;

import java.util.ResourceBundle;
public class PropertiesUtil {
	
	private static ResourceBundle application;
	private static ResourceBundle dbQueries;
	private static ResourceBundle dbConnection;
	private static ResourceBundle cron;
	static 
	{
		 application =ResourceBundle.getBundle("com.fitternity.properties.application");
		 dbQueries =ResourceBundle.getBundle("com.fitternity.properties.dbQueries");
		 dbConnection =ResourceBundle.getBundle("com.fitternity.properties.dbConnection");
		 cron =ResourceBundle.getBundle("com.fitternity.properties.cron");
	}
	public static String getAppProperty(String prop)
	{
		return application.getString(prop);
	}
	public static String getDBQueryProperty(String prop)
	{
		return dbQueries.getString(prop);
	}
	public static String getDBConnectionProperty(String prop)
	{
		return dbConnection.getString(prop);
	}
	public static String getCronProperty(String prop)
	{
		return cron.getString(prop);
	}
	
}
