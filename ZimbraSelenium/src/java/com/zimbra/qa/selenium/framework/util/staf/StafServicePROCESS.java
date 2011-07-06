package com.zimbra.qa.selenium.framework.util.staf;

import com.zimbra.qa.selenium.framework.util.HarnessException;

public class StafServicePROCESS extends StafAbstract {
	
	public static final int StafTimeoutMillisDefault = 30000;
	private int StafTimeoutMillis = StafTimeoutMillisDefault;

	public StafServicePROCESS() {
		logger.info("new "+ StafServicePROCESS.class.getCanonicalName());
		
		StafService = "PROCESS";
		StafParms = "START SHELL COMMAND \"ls\" RETURNSTDOUT RETURNSTDERR WAIT "+ StafTimeoutMillis;
		
	}
	
	public void setTimeout(int timeout) {
		StafTimeoutMillis = timeout;
	}
	
	public int getTimeout() {
		return (StafTimeoutMillis);
	}
	
	public int resetTimeout() {
		StafTimeoutMillis = StafTimeoutMillisDefault;
		return (StafTimeoutMillis);
	}
	
	/**
	 * Execute the STAF request
	 * @return 
	 * @throws HarnessException 
	 */
	public boolean execute(String command) throws HarnessException {
		if ( command.trim().startsWith("zm") ) {
			// For zm commands, run as zimbra user, and prepend the full path
			StafParms = String.format("START SHELL COMMAND \"su - zimbra -c '/opt/zimbra/bin/%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, StafTimeoutMillis);
		} else {
			StafParms = String.format("START SHELL COMMAND \"%s\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, StafTimeoutMillis);
		}
		return (super.execute());

	}
}
