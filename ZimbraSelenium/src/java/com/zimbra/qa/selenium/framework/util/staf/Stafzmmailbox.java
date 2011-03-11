package com.zimbra.qa.selenium.framework.util.staf;

import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * @deprecated As of version 7.0
 * @author zimbra
 *
 */
public class Stafzmmailbox extends StafServicePROCESS {
		
	public Stafzmmailbox() {
		super();
		
		logger.info("new "+ Stafzmmailbox.class.getCanonicalName());
		StafService = "PROCESS";
		
	}
	
	public boolean execute(String command) throws HarnessException {
		setCommand(command);
		return (super.execute());
	}
	
	protected String setCommand(String command) {
		
		// Make sure the full path is specified
		if ( command.trim().startsWith("zmmailbox") ) {
			command = "/opt/zimbra/bin/" + command;
		}
		// Running a command as 'zimbra' user.
		// We must convert the command to a special format
		// START SHELL COMMAND "su - zimbra -c \'<cmd>\'" RETURNSTDOUT RETURNSTDERR WAIT 30000</params>

		StafParms = String.format("START SHELL COMMAND \"su - zimbra -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, StafTimeoutMillis);
		return (getStafCommand());
	}
	
}
