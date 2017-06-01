package com.fitternity.dao.beans;

import com.fitternity.abstracthelpers.BaseCollection;
import com.mongodb.DBCollection;

/**
 * @author shahaan
 *
 */
public class Transaction extends BaseCollection
{
	DBCollection collection;

	public Transaction(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public Transaction() {
		// TODO Auto-generated constructor stub
	}
	
}
