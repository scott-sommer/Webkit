package com.clipsal.predator.core.cgate;

import com.clipsal.predator.core.RegexCriteria;

/**
 * Listens to messages and compares them against the given Criteria
 * @author carrd
 *
 */
public class CgateEventListener extends CgateListener {

	/**
	 * Accept all event messages and save them to allMatchingStrings
	 */
	public CgateEventListener() {
		super();
	}
	/**
	 * Accept only specific event messages
	 */
	public CgateEventListener( RegexCriteria crit ) {
		super(crit);
	}

}
