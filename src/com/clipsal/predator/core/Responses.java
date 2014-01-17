package com.clipsal.predator.core;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Manages a collection of responses.
 */
public class Responses extends CoreObject {

	protected Vector<Response> responses = new Vector<Response>();

	public Responses() {
	}	
	
	public int indexOfResponseMatch( Criteria criteria ) {
		int matchIndex = -1;
		// NB: we only need to find one match
		for ( Response r : responses ) {
			if( criteria.match(r.getText())) {
				matchIndex = responses.indexOf(r);
				break;
			}
		}
		return matchIndex;
	}
	
	public boolean hasResponseMatch( Criteria criteria ) {
		boolean matchFound = false;
		// NB: we only need to find one match
		for ( Response r : responses ) {
			if( criteria.match(r.getText())) {
				matchFound = true;
				break;
			}
		}
		return matchFound;
	}

	public String textOfResponseMatch( Criteria criteria ) {
		String matchText = "";
		// NB: we only need to find one match
		for ( Response r : responses ) {
			if( criteria.match(r.getText())) {
				matchText = r.getText();
				break;
			}
		}
		return matchText;
	}

	public String findFirstResponseRegexMatch( RegexCriteria criteria ) {
		for ( Response r : responses ) {
			String match = criteria.findFirstCapturedGroup(r);
			if ( match != null ) {
				return match;
			}
		}
		return null;
	}

	public ArrayList<String> findAllResponseRegexMatches( RegexCriteria criteria ) {
		ArrayList<String> results = new ArrayList<String>();
		for ( Response r : responses ) {
			String match = criteria.findFirstCapturedGroup(r);
			if ( match != null ) {
				results.add(match);
			}
		}
		return results;
	}
	
	public Vector<Response> getResponses() {
		return responses;
	}
	public void add( Response response ) {
		responses.add(response);
	}
	public int size() {
		return responses.size();
	}
	public Enumeration<Response> elements() {
		return responses.elements();
	}
	public Response getResponse(int index) {
		return responses.get(index);
	}
}
