package com.clipsal.predator.core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexCriteria extends CoreObject implements Criteria {

	private String regex;
	
	/**
	 * Takes a string literal and turns it into a regex pattern.
	 * @param literal
	 * @return
	 */
	public static String stringToRegex( String literal ) {
		return Pattern.quote(literal);
	}
	
	public RegexCriteria( String regex ) {
		this.regex = regex;
	}  

	public String toString() {
		return regex;
	}
	
	public boolean match(String source) {
	/*
		// log("trying regex=" + regex + " source=" + response.getText() );
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(text);
		if ( matcher.lookingAt() ) {
			//log("MATCHED");
			return true;
        }
		//log("UNMATCHED");
		return false;
	*/

		ArrayList<String> groups = findGroups(source);
		return ( ( groups != null ) && ( groups.size() >= 1 ) );
	}

	public String findFirstCapturedGroup(Response response) {
		return findFirstCapturedGroup(response.getText());
	}
	
	public String findFirstCapturedGroup(String source) {
		ArrayList<String> groups = findGroups(source);
		// NB: group 0 is the whole source, group 1 is the first captured group
		if ( ( groups != null ) && ( groups.size() >= 1 ) ) {
			//logDebug("first-capture=" + groups.get(1) + " regex=" + regex + " source=" + source );
			return groups.get(1);
        }
		return null;
	}
	
	/**
	 * Return all matched groups in the source String
	 * Note that group 0 is always the full source string (if it matched)
	 * Followed by groups 1..n which are the captured groups.
	 * @param source
	 * @return
	 */
	public ArrayList<String> findGroups(String source) {
		Pattern pat = Pattern.compile(regex);
		Matcher matcher = pat.matcher(source);
		logDebug("Matching regex "+ regex + " to text: " + source);
		
		/*
		 * Out of find(), matches() and lookingAt(), only find() seems to correctly match 
		 * with capture groups.
		 */
		if ( matcher.find() ) {
			ArrayList<String> groups = new ArrayList<String>();
			for( int i=0; i<=matcher.groupCount();i++) {
				logDebug("Found match.  Group="+matcher.group(i));
				groups.add(matcher.group(i));
			}
			return groups;
        }
		logDebug("FAILED to find a match.");
		return null;
	}
}
