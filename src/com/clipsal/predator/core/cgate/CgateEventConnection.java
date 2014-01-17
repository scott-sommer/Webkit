package com.clipsal.predator.core.cgate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Vector;
import com.clipsal.predator.core.*;


/**
 * Establish a connection to a C-Gate event port
 * @author carrd
 */
public class CgateEventConnection extends CgateConnection {

    protected BufferedReader in = null;     // reader for reading from C-Gate
    protected Vector<CgateEvent> events = new Vector<CgateEvent>(); // list of events
 	
	public CgateEventConnection(String name, String address, int port) throws ConnectionException {
		super(name,address,port);
	}

	@Override
	public void connect() throws ConnectionException {
		try {
			// make the connection
	        socket = new Socket(address, port);
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
	        		sendReceivedTextToListeners(response);
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

	
}
