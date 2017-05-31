package com.fitternity.enums;
/**
 * @author Shahaan
 *
 */
public enum Collections 
{
	TRANSACTIONS,CUSTOMERS,VENDORS,OZONETELCAPTURES;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name().toLowerCase();
	}
}
