package com.fitternity.manager;

import java.util.HashMap;

import com.fitternity.constants.AppConstants;
import com.fitternity.constants.CronConstants;
import com.fitternity.enums.Tasks;
import com.fitternity.tasks.AddNewLoc;
import com.fitternity.tasks.NearbyDistAlgo;
import com.fitternity.util.PropertiesUtil;
/**
 * @author shahaan
 *
 */
public class TaskManager  implements CronConstants,AppConstants
{
	public TaskManager() {
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * @return
	 */
	public static HashMap<Tasks, Boolean> processTasks() 
	{
		HashMap<Tasks, Boolean> tasksScheduled=new HashMap<>();
		if(Boolean.parseBoolean(PropertiesUtil.getAppProperty(NEW_LOC_TASK)))
		{
			new AddNewLoc().addNewLocations();
			tasksScheduled.put(Tasks.ADDNEWLOC, true);
			
		}
		
		if(Boolean.parseBoolean(PropertiesUtil.getAppProperty(NEARBY_DIST_ALGO_TASK)))
		{
			new NearbyDistAlgo().fetchIncorrectLocations();
			tasksScheduled.put(Tasks.NEARBYDISTALGO, true);
		}
//		if(Boolean.parseBoolean(PropertiesUtil.getAppProperty(NEARBY_DIST_ALGO_TASK)))
//		{
//			new NearbyDistAlgo().fetchIncorrectLocations();
//			tasksScheduled.put(Tasks.NEARBYDISTALGO, true);
//		}
		return tasksScheduled;
	}
	
}
