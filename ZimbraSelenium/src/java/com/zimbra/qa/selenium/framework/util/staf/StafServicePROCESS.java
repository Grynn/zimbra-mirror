/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
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
