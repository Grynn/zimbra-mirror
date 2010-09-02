package framework.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Stafzmmailbox extends StafAbstract {
	private static Logger logger = LogManager.getLogger(Stafzmmailbox.class);
		
	public Stafzmmailbox() {
		super();
		
		logger.info("new Stafzmmailbox");
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
