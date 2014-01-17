package com.clipsal.predator.core;

/**
 * Exception class to indicates a problem with a Connection instance
 */
public class ConnectionException extends Exception {
	private static final long serialVersionUID = -2293225057110833977L;

	public ConnectionException(String message) {
		super(message);
	}
	public ConnectionException(String message, Throwable cause ) {
		super(message, cause);
	}
	
}
