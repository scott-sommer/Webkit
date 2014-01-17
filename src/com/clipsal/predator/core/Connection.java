package com.clipsal.predator.core;

/*
 * Abstract class defining a connection that we make to something
 */
public abstract class Connection extends CoreObject implements Runnable {

	/**
	 * State of the thread.
	 *   NONE = no thread.
	 *   RUNNING = thread is running.
	 *   STOPPING = thread is stopping.
	 */
	public enum ThreadState {
		UNTHREADED { public String toString() { return "UNTHREADED"; } },
		RUNNING { public String toString() { return "RUNNING"; } },
		STOPPING { public String toString() { return "STOPPING"; } }
	};
	
	/**
	 * (When running as a thread) state of the thread.
	 */
    private ThreadState threadState = ThreadState.UNTHREADED;

	/**
	 * (When running as a thread) time in milliseconds to sleep after processing.
	 */
    private int postProcessSleepTime = 5;
    
	/**
	 * Set to true when a connection is made, and false when a disconnection occurs.
	 */
    protected boolean connected = false;
    
    /**
     * Constructor
     */
	public Connection(String name) throws ConnectionException {
		if (name == null) {
			throw new ConnectionException("No name provided for Connection constructor.");
		}
		this.setName(name);		
	}
	
    /**
     * Establish the connection.  
     * Must set the connected property to true.
     */
	public abstract void connect() throws ConnectionException;
	
    /**
     * Pull down the connection.  
     * Must set the connected property to false.
     */
	public abstract void disconnect() throws ConnectionException;

    /**
     * Perform processing of the connection.
     * Should contain code to process all sends (outgoing) and receives (incoming) 
     * traffic for a short duration, so that this method may be called repeatedly.
     */
	public abstract void process() throws ConnectionException;
	
    /**
     * Make the connection threadable.
     * When the thread is started, establish a connection and wait 
     * for either a disconnection or an thread stop
     */
	public void run() {
		if (threadState == ThreadState.UNTHREADED) {
			threadState = ThreadState.RUNNING;
			boolean wasConnected = isConnected();
			if ( !wasConnected ) {
				try {
					connect();
				} catch ( ConnectionException ce ) {
					logError( "Error while connecting: " + ce.getMessage() );
				}
			}
			while ( ( connected == true ) && ( threadState == ThreadState.RUNNING ) ) {
				try {
					process();
					try{
						Thread.sleep(postProcessSleepTime);
					} catch ( InterruptedException ie ) {
					}
				} catch ( ConnectionException ce ) {
					if ( connected ) {
						logError( "Error while processing: " + ce.getMessage() );
					} else {
						logError( "Clean disconnection of threaded session." );
					}
					if ( !wasConnected ) {
						try {
							disconnect();
						} catch ( ConnectionException dce ) {
							logError( "Error while disconnecting: " + ce.getMessage() );
						}
					}
				}
			}
			if ( connected == true ) {
				// we stopped the thread while still connected
				if ( !wasConnected ) {
					try {
						disconnect();
					} catch ( ConnectionException ce ) {
						logError( "Error while disconnecting stopped thread: " + ce.getMessage() );
					}
				}
			}
		}
	}	

    /**
     * Command to stop the thread
     */
	public void stop() {
		threadState = ThreadState.STOPPING;
	}
	
	/**
	 * @return true if currently connected.
	 */
	public boolean isConnected() {
		return connected;
	}
	
	@Override
	public String toString() {
		return super.toString()+";ThreadState=" + threadState.toString();
	}

}
