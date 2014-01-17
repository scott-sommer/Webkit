package com.clipsal.predator.core.cgate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import com.clipsal.predator.core.*;


/**
 * Establish a connection to the C-Gate command port
 * @author carrd
 */
public class CgateCommandConnection extends CgateConnection {

    protected PrintWriter out = null;     // writer for sending to C-Gate
    protected BufferedReader in = null;     // reader for reading from C-Gate
    protected Vector<CgateCommand> commands = new Vector<CgateCommand>(); // list of commands
    protected Vector<CgateResponse> connectionResponses = new Vector<CgateResponse>();  // initial connection responses
    protected Responses unclaimedResponses = new Responses();  // unclaimed responses
	
	public CgateCommandConnection(String name, String address, int port) throws ConnectionException {
		super(name,address,port);
	}

	@Override
	public void connect() throws ConnectionException {
		try {
			// make the connection
	        socket = new Socket(address, port);
	        out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
        catch (UnknownHostException e) {
			throw new ConnectionException("No host found at address: " + address );
		}
        catch (IOException e) {
            // will get here if c-gate isn't running
			throw new ConnectionException("Cannot connect to address: " + address + " port: " + Integer.toString(port) );
        }

        logDebug("initial");
        
        // capture the initial C-Gate output and move it to another array
		processInitialConnection();
		logDebug("processed");

		logDebug("connected");

        // all done
        connected = true;
    }

	@Override
	public void disconnect() throws ConnectionException {
		if ( connected ) {
			connected = false;
			logInfo("disconnecting...");
			removeAllListeners();
	        try {
	        	if ( out != null ) {
	                out.close();
	        	}
	        	if ( in != null ) {
	        		in.close();
	        	}
	        	if ( socket != null ) {
	        		socket.close();
	        	}
	        }
	        catch (Exception e) {
	        	throw new ConnectionException( e.getMessage() );
	        }
			logInfo("disconnected.");
		}
	}
	
	protected void processInitialConnection() throws ConnectionException {
		String response = "";
		int milliseconds = 2000;
		Date startTime = new Date();
        while ( ( response != null ) && ( startTime.getTime() + milliseconds > new Date().getTime() ) ) {
			try {
	        	if ( isInputStreamReady() ) {
    				response = in.readLine();
	        		//log("rcvd:" + response);
        			connectionResponses.add(new CgateResponse(response));
        			if ( response.contains("Service ready")) {
        				return;
        			}
	        	}
			} catch (IOException e) {
				throw new ConnectionException("Cannot read from connection." , e);
			}
			try{
				Thread.sleep(5);
			} catch ( InterruptedException ie ) {
			}
		}
        logError("No connection response seen.");
	}
	
	protected boolean isInputStreamReady() throws IOException {
		return in.ready();
	}
	
	public void process( int milliseconds ) throws ConnectionException {
		String response = "";
		Date startTime = new Date();
        while ( ( response != null ) && ( startTime.getTime() + milliseconds > new Date().getTime() ) ) {
			try {
	        	if ( isInputStreamReady() ) {
    				response = in.readLine();
	        		logInfo("Rx:" + response);
        			processResponse(response);
	        	}
			} catch (IOException e) {
				throw new ConnectionException("Cannot read from connection." );
			}
			try{
				Thread.sleep(5);
			} catch ( InterruptedException ie ) {
			}
		}		
	}
	
	@Override
	public void process() throws ConnectionException {
		process(100);
	}

	public synchronized void send( CgateCommand command ) throws ConnectionException {
		if (out == null) {
			throw new ConnectionException("Not connected, cannot send a command." );
		}
		// add it to the list of commands
		commands.add( command );
		command.sentAfter();
		out.println(command.getSendString());		
	}

	public synchronized ArrayList<String> dump(String prefix ) {
		return dump(prefix,false);
	}
	
	private String truncateLine( String line ) {
		int linefeedpos = line.indexOf("\n");
		if ( linefeedpos > 0 ) {
			return line.substring(0,linefeedpos) + "(...)";
		}
		if ( line.length() > 70 ) {
			return line.substring(0,70) + "(...)";
		}
		return line;
	}
	
	public synchronized ArrayList<String> dump(String prefix, boolean verbose ) {
		ArrayList<String> result = new ArrayList<String>();
		result.add(prefix + "************************************************************" );
		if ( verbose ) {
			result.add(prefix + "Initial Connection Messages: " + connectionResponses.size() );
			for ( Enumeration<CgateResponse> respenum  = connectionResponses.elements() ; respenum.hasMoreElements(); ) {
				CgateResponse resp = respenum.nextElement();
				result.add(prefix + "  " + resp.getText() );
			}
		}
		result.add(prefix+"Commands: " + commands.size() );
		for ( Enumeration<CgateCommand> cmdenum = commands.elements() ; cmdenum.hasMoreElements(); ) {
			CgateCommand cmd = cmdenum.nextElement();
			String cmdString = verbose ? cmd.text : truncateLine( cmd.text );
			result.add(prefix + cmd.getOID() + ":" + cmdString + " (" + cmd.getStateAsString() + "}" );
			if ( verbose ) {
				for ( Enumeration<Response> respenum  = cmd.getResponsesVector().elements() ; respenum.hasMoreElements(); ) {
					Response resp = respenum.nextElement();
					result.add(prefix + "  R:" + resp.getText() );
	
				}
			}
		}
		if ( unclaimedResponses.size() > 0 ) {
			result.add(prefix + "Unclaimed Responses: " + unclaimedResponses.size() );
			for ( Enumeration<Response> respenum  = unclaimedResponses.elements() ; respenum.hasMoreElements(); ) {
				Response resp = respenum.nextElement();
				result.add(prefix + "  R:" + resp.getText() );
			}
		}
		result.add(prefix + "************************************************************" );
		return result;
	}
	
	protected void processResponse( String response ) {
		// put it in an object
		CgateResponse r = new CgateResponse(response);
		// look for a matching command
		boolean found = false;
		for ( Enumeration<CgateCommand> e = commands.elements() ; e.hasMoreElements(); ) {
			CgateCommand cmd = e.nextElement();
			if ( cmd.tryClaimResponse(r) ) {
				found = true;
				logDebug("claimed by cmd: " + cmd.text );
				break;  // exit for loop
			}
		}
		// if no matching command, add it to a list of unclaimed responses
		if ( !found ) {
			unclaimedResponses.add(r);
		}
		
		// let the listeners have it
		sendReceivedTextToListeners(response);
	}

	public Vector<CgateCommand> getCommands() {
		return commands;
	}

	
	public Responses getUnclaimedResponses() {
		return unclaimedResponses;
	}
	
	/**
	 * Assert that the substring does not exist in the unclaimed responses.
	 * (Useful for ensuring no errors or unusual output was produced).
	 * @param substring
	 */
	public void assertUnclaimedResponsesSubStringEx(String substring) {
		Vector<Criteria> exCriterias = new Vector<Criteria>();
		exCriterias.add(new SubstringCriteria( substring ));
		boolean matchNotFound = true;
		for ( Criteria crit : exCriterias ) {
			int matchIndex = unclaimedResponses.indexOfResponseMatch(crit);
			if ( matchIndex >= 0 ) {
				matchNotFound = false;
				logError("Match found for substring '" + crit.toString() + "' in unclaimed responses, line " + matchIndex + ": " + unclaimedResponses.getResponses().get(matchIndex) );
			}
		}
	}

	
}
