package com.clipsal.predator.core;

//import java.util.Date;

public class Response extends CoreObject {

	private String text;
	// private Date timeReceived;
	
	public Response(String text) {
		this.text = text;
		// this.timeReceived = new Date();  // set it to now
	}
	
	public String getText() {
		return text;
	}
	
	public String getRegexMatch( RegexCriteria criteria ) {
		String match = criteria.findFirstCapturedGroup(this);
		if ( match != null ) {
			return match;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Name=" + getName() + " Text=" + getText();
	}	
}
