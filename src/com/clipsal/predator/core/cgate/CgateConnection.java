package com.clipsal.predator.core.cgate;

import java.net.Socket;
import java.util.Vector;

import com.clipsal.predator.core.*;


/**
 * Establish a C-Gate connection
 * @author carrd
 */
public abstract class CgateConnection extends Connection {

    protected Socket socket = null;   // connection socket to C-Gate
	public String address = "";
	public int port = -1;
    protected Vector<CgateListener> listeners = new Vector<CgateListener>(); // list of listeners
	
	public CgateConnection(String name, String address, int port) throws ConnectionException {
		super(name);
		if (address == null) {
			throw new ConnectionException("No address provided for CgateConnection constructor.");
		}
		if (port < 0) {
			throw new ConnectionException("No port provided for CgateConnection constructor.");
		}
		this.address = address;		
		this.port = port;		
	}

	protected void sendReceivedTextToListeners( String text ) {
		for ( CgateListener listener : listeners ) {
			listener.process( text );
		}
	}

	public void addListener( CgateListener listener ) {
		listeners.add( listener );
	}

	public void removeListener( CgateListener listener ) {
		listeners.remove( listener );
	}
	public void removeAllListeners() {
		listeners.clear();
	}
	
}
