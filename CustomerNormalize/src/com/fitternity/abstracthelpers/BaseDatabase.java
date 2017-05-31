package com.fitternity.abstracthelpers;

import com.mongodb.DB;

/**
 * @author shahaan
 *
 */
public class BaseDatabase 
{
	protected DB db ;
	public BaseDatabase() {
		// TODO Auto-generated constructor stub
	}
	public BaseDatabase(DB db) {
		this.db=db;
		System.out.println(this.getClass().getName());
		// TODO Auto-generated constructor stub
	}
}
