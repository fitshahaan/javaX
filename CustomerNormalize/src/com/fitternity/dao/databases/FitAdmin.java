package com.fitternity.dao.databases;

import com.fitternity.abstracthelpers.BaseDatabase;
import com.mongodb.DB;

public class FitAdmin extends BaseDatabase
{

	public FitAdmin(DB db)
	{
//		super(db);
		this.db=db;
	}
	
	
}
