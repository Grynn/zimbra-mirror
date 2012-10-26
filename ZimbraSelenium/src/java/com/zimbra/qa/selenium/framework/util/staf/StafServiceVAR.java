package com.zimbra.qa.selenium.framework.util.staf;

import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * Use the STAF FS service to execute a command
 * 
 * @author Matt Rhoades
 *
 */
public class StafServiceVAR extends StafAbstract {
	
	public StafServiceVAR() {
		logger.info("new "+ StafServiceVAR.class.getCanonicalName());
		
		// Command:
		// STAF local VAR GET SYSTEM VAR STAF/Config/Machine

		this.StafServer = "local";
		this.StafService = "VAR";
		this.StafParms = "GET SYSTEM VAR STAF/Config/Machine";
		
	}
	
	/**
	 * Execute the STAF request<p>
	 * For example, if "command = 'QUERY ENTRY /tmp'", then execute 'STAF server FS QUERY ENTRY /tmp'
	 * @return 
	 * @throws HarnessException 
	 */
	public boolean execute(String command) throws HarnessException {
		StafParms = command;
		return (super.execute());
	}
}
