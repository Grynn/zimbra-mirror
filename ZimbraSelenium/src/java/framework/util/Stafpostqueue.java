package framework.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import framework.core.SelNGBase;

public class Stafpostqueue extends StafAbstract {
	private Logger logger = LogManager.getLogger(Stafzmprov.class);
	
	public boolean waitForPostqueue() throws Exception {
		boolean success = false;
		String response = "";
		String command = "postqueue -p";
		
		for (int i = 0 ; i < 15; i ++) {
			if(execute(command)){
				response = this.StafResponse;
				if (!response.contains(SelNGBase.selfAccountName.get())){
					success = true;	
					return success;
				}
			}
			SleepUtil.sleep(1000);
		}
		
		if(!success){
			deletePostqueueItem("");
			logger.info("Item never delievered, deleting from the queue");
		}
		return success;
	}
	
	public void deletePostqueueItem(String queue_id) throws HarnessException{
		// STAF <SERVER> PROCESS START COMMAND /opt/zimbra/postfix/sbin/postsuper PARMS -d <queue_id> RETURNSTDOUT RETURNSTDERR WAIT 60000
		String command = "/opt/zimbra/postfix/sbin/postsuper -d ALL";
		execute(command);
	}
	
	public Stafpostqueue() {
		super();
		logger.info("new Stafpostqueue");
		StafService = "PROCESS";
	}
		
	public boolean execute(String command) throws HarnessException {
		setCommand(command);
		return(super.execute());
	}	
	
	protected String setCommand(String command){
		//We must convert the command to a special format
		if (command.trim().contains("postsuper"))
		//Running a command as super user.
			StafParms = String.format("START SHELL COMMAND \"su - -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, StafTimeoutMillis);
		else		
		//Running a command as 'zimbra' user.
			StafParms = String.format("START SHELL COMMAND \"su - zimbra -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, StafTimeoutMillis);
		
		return (getStafCommand());		
	}
}
