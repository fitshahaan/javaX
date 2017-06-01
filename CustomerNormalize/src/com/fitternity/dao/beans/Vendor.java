package com.fitternity.dao.beans;

import com.fitternity.abstracthelpers.BaseCollection;
import com.mongodb.DBCollection;

/**
 * @author shahaan
 *
 */
public class Vendor extends BaseCollection
{
	DBCollection collection;

	public Vendor(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public Vendor() {
		// TODO Auto-generated constructor stub
	}
	
}
