package com.clipsal.predator.core.cgate;

import java.util.ArrayList;
import java.util.Date;

import com.clipsal.predator.core.RegexCriteria;

/**
 * Listens to messages and compares them against the given Criteria
 * @author carrd
 *
 */
public class CgateListener {

	private RegexCriteria criteria = null;
	private ArrayList<String> allMatchingStrings = new ArrayList<String>();
	private ArrayList<String> allMatchingGroups = new ArrayList<String>();
	
	/**
	 * Accept all event messages and save them to allMatchingStrings
	 */
	public CgateListener() {
		this.criteria = null;
	}
	/**
	 * Accept only specific event messages
	 */
	public CgateListener( RegexCriteria crit ) {
		this.criteria = crit;
	}
	
	public void process( String text ) {
		if ( criteria != null ) {
			ArrayList<String> groups = criteria.findGroups( text );
			if ( groups != null && groups.size() > 0 ) {
				allMatchingStrings.add( text );
				allMatchingGroups.addAll( groups );
			}
		} else {
			// add everything
			allMatchingStrings.add( text );
		}
	}

	public boolean hasMatch() {
		return ( allMatchingStrings.size() > 0 );
	}

	public boolean waitForMatch( int milliseconds ) {
		Date start = new Date();
		while ( ( new Date().getTime() < ( start.getTime() + milliseconds ) ) ) {
			if ( allMatchingStrings.size() > 0 ) {
				return true;
			}
			try{
				Thread.sleep(100);
			} catch ( InterruptedException ie ) {
			}
		}
		return false;
	}

	public ArrayList<String> getCopyOfAllMatchingStrings() {
		return new ArrayList<String>(allMatchingStrings);
	}

	/*
	public ArrayList<String> getCopyOfAllMatchingGroups() {
		return new ArrayList<String>(allMatchingGroups);
	}
	*/
}
