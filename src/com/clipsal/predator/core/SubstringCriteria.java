package com.clipsal.predator.core;

public class SubstringCriteria implements Criteria {

	private String substring;	
	private boolean matchFull = false;
	
	public SubstringCriteria( String substring ) {
		this.substring = substring;
	}
	public SubstringCriteria( String substring, boolean matchFull ) {
		this.substring = substring;
		this.matchFull = matchFull;
	}
	
	public String toString() {
		return substring;
	}

	public boolean match(String text) {
		if ( matchFull ) {
			return text.equals(substring);
		} else {
			return text.contains(substring);
		}
	}

}
