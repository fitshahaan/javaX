package com.fitternity.constants;

public enum Environments 
{
	STAGING,LOCAL,PRODUCTION;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name().toLowerCase();
	}
}
