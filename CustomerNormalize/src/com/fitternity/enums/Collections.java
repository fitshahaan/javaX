package com.fitternity.enums;
/**
 * @author Shahaan
 *
 */
public enum Collections 
{
	TRANSACTIONS,CUSTOMERS,VENDORS,OZONETELCAPTURES,CITIES,LOCATIONS,LOCATIONCLUSTERS,REVIEWS;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name().toLowerCase();
	}
}
