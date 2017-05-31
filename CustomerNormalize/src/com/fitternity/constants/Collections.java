package com.fitternity.constants;

public enum Collections 
{
	TRANSACTIONS,CUSTOMERS,VENDORS,OZONETELCAPTURES;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name().toLowerCase();
	}
}
