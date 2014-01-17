package com.clipsal.predator.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

public class Command extends CoreObject {

	/**
	 * An internal private unique identifier for commands
	 */
	private static int globalCommandOIDCounter = 0;
	
	/**
	 * Generates an unique OID for a command.  
	 * Note necessarily the same as the identifier inside a command.
	 */
	private static int getUniqueCommandOID() {
		globalCommandOIDCounter++;
		return globalCommandOIDCounter;
	}
	
	/**
	 * State of the command.
	 *   UNSENT = command has not been sent yet
	 *   PENDING = command is awaiting responses
	 *   CLOSED = command has received all responses
	 */
	public enum CommandState {
		UNSENT,
		PENDING,
		CLOSED
	};
	
	private int oid;
	public String text;
	public Date sentTime;	
	
	/**
	 * Duration in milliseconds that this command is allowed to run
	 */
	private int timeoutDuration = 10000;
	
	public CommandState state;
	protected Responses responses = new Responses();
	protected Vector<ResponseCriteria> responseCriterias = new Vector<ResponseCriteria>();

	public Command(String text) {
		this.oid = getUniqueCommandOID();
		this.text = text;
		this.sentTime = null; 
		this.state = CommandState.UNSENT;
	}	

	public Command(String text, int timeoutDuration) {
		this(text);
		this.timeoutDuration = timeoutDuration;
	}	

	/**
	 * A method to be called when the command has actually been sent.
	 * This initialises the internal times and states etc.
	 */
	public void sentAfter() {
		this.sentTime = new Date();  // set it to now
		this.state = CommandState.PENDING;
	}
	
	/**
	 * Try to claim the given response.  
	 * If the response matches any of the command's criteria, it will be 
	 * added to the responses for that command and the method result will
	 * be true. Additionally if the response matches any criteria that 
	 * specify a lastMatch, the command's state will be set to CLOSED.
	 * 
	 * @param response
	 * @return
	 */
	public boolean tryClaimResponse( Response response ) {
		boolean claimIt = false;
		boolean lastResponse = false;
		
		//log("trying cmd=" + text + " response=" + response.getText() + " criterias=" + responseCriterias.size());
		
		// if the command is closed, it is not allowed to claim any more responses!
		if ( isClosed() ) {
			//logdebug("cmd closed");
			return false;
		}
		
		// NB: we need to check ALL criteria, as a later one may have finalMatch set
		for ( Enumeration<ResponseCriteria> critenum  = responseCriterias.elements() ; critenum.hasMoreElements(); ) {
			ResponseCriteria respcrit = critenum.nextElement();
			if( respcrit.criteria.match(response.getText())) {
				claimIt = true;
				if ( respcrit.finalResponse) {
					lastResponse = true;
					logDebug("last response detected");
				}
			}
		}
		if ( claimIt ) {
			logDebug("claimed! response=" + response.getText());
			responses.add(response);
			if ( lastResponse ) {
				logDebug("last! response=" + response.getText());
				state = CommandState.CLOSED;
			}
			return true;
		}
		return false;
	}

	public int getOID() {
		return oid;
	}

	public String getStateAsString() {
		return isClosed() ? "CLOSED" : "PENDING";
	}
	
	public int getTimeoutDuration() {
		return timeoutDuration;
	}
	
	/**
	 * Get the full string to be sent
	 * @return
	 */
	public String getSendString() {
		return text;
	}
	
	public boolean isClosed() {
		return ( state == CommandState.CLOSED );
	}
	
	public void setClosed() {
		state = CommandState.CLOSED;
	}

	/**
	 * Returns true if the command was sent more than the given
	 * number of milliseconds ago.
	 * @param milliseconds
	 * @return
	 */
	public boolean isTimedOut() {
		return ( timeoutDuration > 0 ) && ( sentTime.getTime() + timeoutDuration < ( new Date().getTime() ) );
	}
	
	/**
	 * Adds a criteria used to accept a response to this command.  Use this in situations
	 * where addDefaultResponseCriteria does not apply.
	 * @param criteria The Criteria to add
	 * @param finalResponse true if matching this Criteria will indicate a final response
	 */
	public void addResponseCriteria( Criteria criteria, boolean finalResponse ) {
		ResponseCriteria rc = new ResponseCriteria( criteria, finalResponse );
		responseCriterias.add(rc);
	}
	
	public String findFirstResponseRegexMatch( RegexCriteria criteria ) {
		return responses.findFirstResponseRegexMatch(criteria);
	}

	public ArrayList<String> findAllResponseRegexMatches( RegexCriteria criteria ) {
		return responses.findAllResponseRegexMatches(criteria);
	}
	
	public Vector<Response> getResponsesVector() {
		return responses.getResponses();
	}
	public Responses getResponses() {
		return responses;
	}
}
