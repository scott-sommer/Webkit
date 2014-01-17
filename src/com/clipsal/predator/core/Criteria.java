package com.clipsal.predator.core;

public interface Criteria {

	/**
	 * Returns true if the given response matches this criteria.
	 */
	public boolean match( String text );
	public String toString();
	
}
