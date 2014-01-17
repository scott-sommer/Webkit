package com.clipsal.predator.core.cgate;

import com.clipsal.predator.core.*;

public class CgateCommand extends Command {

	public CgateCommand(String text) {
		super(text);
	}	

	public CgateCommand(String text, int timeoutDuration) {
		super(text,timeoutDuration);
	}	

	/**
	 * Get any prefix that has been manually included in the text string.
	 * This is used to ensure we don't send two prefixes.
	 * Returns empty string "" if no prefix is found.
	 * Prefix syntax:
	 *   [ ['&' [ priority-digit ] ] '[' id-string ']' ] [ SP ] command
	 *   (priority-digit 0=low, 4=default, 9=highest)
	 * eg: &4[55foo]
	 * @return
	 */

	/*
	 * We can use this regex to pick up entire prefixes:   ^((?:\&\d?)?(?:\[.*\])?).*
	 *    ^     = from start of line
	 *    (     = a capturing group - this defines the final "name" result
	 *    (?:   = a non-capturing group 
	 *    \&    = & character
	 *    \d?   = optional digit (0-9)
	 *    )?    = zero or one occurences of the nested group
	 *    (?:   = a non-capturing group 
	 *    \[.*\] = an identifier in brackets
	 *    )?    = zero or one occurences of the nested group (identifier)
	 *    .*    = remainder of string
	 *    
	 *  NB: In Java string literals we have to double up the backslashes.
	 */   
	
	public boolean getSentBackgrounded() {
		/*
		 * Returns true if the "background" prefix exists
		 */   
		RegexCriteria crit = new RegexCriteria("^(\\&\\d?).*");
		return ( crit.findFirstCapturedGroup(text) != null );
	}	
	public int getSentPriority() {
		/*
		 * Returns the "thread" priority 
		 */   
		RegexCriteria crit = new RegexCriteria("^\\&(\\d?).*");
		return Integer.parseInt( crit.findFirstCapturedGroup(text) );
	}	
	public String getSentIdentifier() {
		/*
		 * Returns the "identifier" prefix, including brackets
		 */   
		RegexCriteria crit = new RegexCriteria("^(?:\\&\\d?)?(\\[.*\\]).*");
		return crit.findFirstCapturedGroup(text);
	}	
	
	/**
	 * Get the prefix expected for the incoming responses.
	 * This usually includes the command identifier, but not backgrounding or priority.
	 * @return
	 */
	public String getExpectedResponsePrefix() {
		return getSentIdentifier();
	}
	
	/**
	 * Get the full string to be sent, including any prefix.
	 * @return
	 */
	@Override
	public String getSendString() {
		return text;
		// TODO: incorporate prefix and handle it in response
	}
	
	
	/**
	 * Add the default set of Criteria commonly used to accept responses to this command.
	 * In other words check for the unique identifier prefix, the 
	 * 3 digit response code, and also flag the final line (space instead of -)
	 */
	public void addDefaultResponseCriteria() {
		String expectedPrefix = getExpectedResponsePrefix();
		String regexPrefix = "";
		if ( ( expectedPrefix != null ) && ( expectedPrefix != "" ) ) {
			/* Regardless of whether the command is sent as "[55]command" or "[55] command", 
			 * the response always has a space, ie. "[55] 666-reply"
			 */
			regexPrefix = RegexCriteria.stringToRegex(expectedPrefix) + "\\s";
		}
		// add the regex to detect all intermediate lines
		addResponseCriteria( new RegexCriteria( "^" + regexPrefix + "[0-9][0-9][0-9]\\-.*") , false );
		// add the regex that detects the last line (4th char is space)
		addResponseCriteria( new RegexCriteria( "^" + regexPrefix + "[0-9][0-9][0-9]\\s.*"), true );
	}

	/**
	 * Add criteria to accept all event messages that occur during the command.
	 */
	public void addEventMessageCriteria() {
		// add the regex to detect the event messages on the command port
		addResponseCriteria( new RegexCriteria( "^#e#.*") , false );
	}
	
}
