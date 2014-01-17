package com.clipsal.predator.core;

import java.util.ArrayList;

//import com.clipsal.predator.core.log.Log;

public class CoreObject {
	private String name = "";

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public String getNameOrClassName() {
		if ( name != null && name.trim().length()>1) {
			return name;
		} else {
			return getClassNameOnly();
		}
	}
	
	private String getClassNameOnly() {
		String FQClassName = getClass().getName();
		int firstChar;
		firstChar = FQClassName.lastIndexOf ('.') + 1;
		if ( firstChar > 0 ) {
			return FQClassName.substring ( firstChar );
		}
		return FQClassName;
	}
	

	public void logError(String message) {
//		Log.logError(message);
	}		

	public void logInfo(String message) {
//		Log.logInfo( " " + message);
	}		
	
	static String lastDebugClassString = "";
	public void logDebug(String message) {
		String classString = "ClassName=" + getClassNameOnly() + ".  toString=" + toString();
		if (!lastDebugClassString.equals(classString)) {
			lastDebugClassString = classString;
//			Log.logDebug( "DEBUG: ---- New class context. " + classString);
		}
		System.out.println("DEBUG: " + message);
//		Log.logDebug("DEBUG: " + message);
	}		

	public static void logDebugStatic(String message) {
//		Log.logDebug("DEBUG: " + message);
	}		
	
	protected void setLogLevelDebug() {
//		Log.setLevelDebug();
	}		

	protected void setLogLevelNormal() {
//		Log.setLevelNormal();
	}		

	protected void setLogLevelWarning() {
//		Log.setLevelWarning();
	}		
	
	/** Implementation of wait() that obscures exceptions
	 * @param milliseconds
	 */
	public synchronized void waitSafely( long milliseconds ) {
		try {
			wait(milliseconds);
		} catch( InterruptedException ie ) {
			logError("wait encountered InterruptedException: " + ie.getMessage() );
		} catch( IllegalMonitorStateException imse ) {
			logError("wait encountered IllegalMonitorStateException: " + imse.getMessage() );
		}
	}

	@Override
	public String toString() {
		return "Name=" + name;
	}
}
