package framework.util;

import java.util.ArrayList;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import framework.core.ClientSessionFactory;

public class Stafpostqueue extends StafAbstract {
	private Logger logger = LogManager.getLogger(Stafzmprov.class);

	public boolean waitForPostqueue() throws Exception {
		boolean success = false;
		String command = "postqueue -p";

		for (int i = 0; i < 15; i++) {
			SleepUtil.sleep(1000);
			if (execute(command)) {
				if (!this.StafResponse.contains(ClientSessionFactory.session()
						.currentUserName().toLowerCase())) {
					success = true;
					return success;
				}
			}
		}

		if (!success) {
			logger.info("Item never delievered, deleting from the queue");
			ArrayList<String> qid = new ArrayList<String>();
			String[] tokens1 = this.StafResponse.split(ClientSessionFactory
					.session().currentUserName().toLowerCase());
			for (int i = 0; i < tokens1.length - 1; i++) {
				String[] tokens2 = tokens1[i].trim().split("\n");
				qid.add(tokens2[tokens2.length - 1].split(" ")[0]);
			}
			deletePostqueueItem(qid);
		}
		return success;
	}

	public void deletePostqueueItem(ArrayList<String> qid)
			throws HarnessException {
		// STAF <SERVER> PROCESS START COMMAND
		// /opt/zimbra/postfix/sbin/postsuper PARMS -d <queue_id> RETURNSTDOUT
		// RETURNSTDERR WAIT 60000
		for (String id : qid) {
			execute("/opt/zimbra/postfix/sbin/postsuper -d " + id);
		}
	}

	public Stafpostqueue() {
		super();
		logger.info("new Stafpostqueue");
		StafService = "PROCESS";
	}

	public boolean execute(String command) throws HarnessException {
		setCommand(command);
		return (super.execute());
	}

	protected String setCommand(String command) {
		// We must convert the command to a special format
		if (command.trim().contains("postsuper"))
			// Running a command as super user.
			StafParms = String
					.format(
							"START SHELL COMMAND \"su - -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d",
							command, StafTimeoutMillis);
		else
			// Running a command as 'zimbra' user.
			StafParms = String
					.format(
							"START SHELL COMMAND \"su - zimbra -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d",
							command, StafTimeoutMillis);

		return (getStafCommand());
	}
}
