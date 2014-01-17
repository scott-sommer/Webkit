package com.clipsal.predator.core;

/*
 * Stores information about Criteria that identify a Response
 * ie. associates a Criteria with a finalMatch property
 */
public class ResponseCriteria {

	public Criteria criteria;	
	public boolean finalResponse;	
	
	public ResponseCriteria( Criteria criteria, boolean finalResponse ) {
		this.criteria = criteria;
		this.finalResponse = finalResponse;
	}
}
