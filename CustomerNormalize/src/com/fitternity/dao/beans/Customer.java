package com.fitternity.dao.beans;

import com.fitternity.abstracthelpers.BaseBean;
import com.fitternity.abstracthelpers.BaseCollection;
import com.mongodb.DBCollection;

/**
 * @author shahaan
 *
 */
public class Customer extends BaseBean
{
	DBCollection collection;
	public String _id;

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Customer(DBCollection collection) {
		// TODO Auto-generated constructor stub
		this.collection=collection;
	}
	public Customer() {
		// TODO Auto-generated constructor stub
	}
	
}
