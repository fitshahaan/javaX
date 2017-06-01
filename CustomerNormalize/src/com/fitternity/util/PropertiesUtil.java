package com.fitternity.util;

import java.util.ResourceBundle;
/**
 * @author shahaan
 *
 */
public class PropertiesUtil {
	
	private static ResourceBundle application;
	private static ResourceBundle dbQueries;
	private static ResourceBundle dbConnection;
	private static ResourceBundle cron;
	static 
	{
		 application =ResourceBundle.getBundle("com.fitternity.settings.application");
		 dbQueries =ResourceBundle.getBundle("com.fitternity.settings.dbQueries");
		 dbConnection =ResourceBundle.getBundle("com.fitternity.settings.dbConnection");
		 cron =ResourceBundle.getBundle("com.fitternity.settings.cron");
	}
	/**
	 * @param prop to get in application settings.
	 * @return key present in application settings.
	 */
	public static String getAppProperty(String prop)
	{
		return application.getString(prop).trim();
	}
	public static String getDBQueryProperty(String prop)
	{
		return dbQueries.getString(prop).trim();
	}
	public static String getDBConnectionProperty(String prop)
	{
		return dbConnection.getString(prop).trim();
	}
	public static String getCronProperty(String prop)
	{
		return cron.getString(prop).trim();
	}
	
}
