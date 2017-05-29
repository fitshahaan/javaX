package com.fitternity.dao.collections;

import com.fitternity.abstracthelpers.BaseCollection;
import com.mongodb.DBCollection;

public class VendorDao extends BaseCollection
{
	DBCollection collection;

	public VendorDao(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public VendorDao() {
		// TODO Auto-generated constructor stub
	}
	
}
