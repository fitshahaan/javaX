package com.fitternity.dao.databases;

import com.fitternity.abstracthelpers.BaseDatabase;
import com.mongodb.DB;
/**
 * @author Shahaan
 *
 */
public class FitApi extends BaseDatabase
{

	public FitApi(DB db) {
		super(db);
		System.out.println(this.getClass().getName());
	}
	
	
}
