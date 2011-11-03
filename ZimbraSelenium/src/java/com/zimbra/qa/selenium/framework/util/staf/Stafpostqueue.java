package com.zimbra.qa.selenium.framework.util.staf;

import java.util.*;
import java.util.regex.*;

import com.ibm.staf.STAFMarshallingContext;
import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.*;

public class Stafpostqueue extends StafServicePROCESS {

	private static final String MailQueueIsEmpty = "Mail queue is empty";
	private static final String MailQueueIsUnavailable = "mail system is down"; // 	postqueue: fatal: Queue report unavailable - mail system is down

	/**
	 * How long (msec) to wait for a message to be delivered.  After this time, fail if the message isn't delivered.
	 */
	private static final int MailQueueWaitMsec = 30000;
	
	/**
	 * How long (msec) to wait between checking the queue
	 */
	private static final int MailQueueWaitIntervalMsec = 1000;
	
	/**
	 * Wait for messages for the current test account to be delivered
	 * @throws HarnessException
	 */
	@SuppressWarnings("unchecked")
	public void waitForPostqueue() throws HarnessException {
		
		// Start: Dev env hack
		if ( DevEnvironment.isUsingDevEnvironment() ) {
			logger.info("In dev environment, waiting for message to be delivered ...");
			SleepUtil.sleep(5000);
			return;
		}
		// End: Dev env hack
		
		String command = "postqueue -p";
		String emailaddress = ClientSessionFactory.session().currentUserName().toLowerCase().trim();
		
		// emailaddress could be null or blank
		// if so, set it to @domain.com
		if ( (emailaddress == null) || (emailaddress.equals("")) ) {
			logger.warn("Unable to determien current user account.  Use @testdomain.com instead");
			emailaddress = "@" + ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com");
		}

		for (int i = 0; i < MailQueueWaitMsec; i += MailQueueWaitIntervalMsec) {
			
			// Check the server queue if it is empty
			if (execute(command)) {
				
				logger.debug("looking for "+ emailaddress +" in "+ this.StafResponse);

				if ( this.StafResponse.contains(MailQueueIsEmpty)) {
					// Queue is empty
					return;
				}
				
				if (!this.StafResponse.contains(emailaddress)) {
					// Queue does not contain any messages for the test account
					return;
				}
				
				if ( this.StafResponse.contains(MailQueueIsUnavailable)) {
					throw new HarnessException("Unable to check message queue.  MTA is down.  "+ this.StafResponse);
				}
				
			}

			// Wait a bit for the message to be delivered
			SleepUtil.sleep(MailQueueWaitIntervalMsec);			

		}

			
		logger.warn("Item never delivered, deleting from the queue");

		// Get only the postqueue output from the STAF result object
     	STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(this.StafResult.result);        	
     	Map resultMap = (Map)mc.getRootObject();
    	List returnedFileList = (List)resultMap.get("fileList");
    	Map stdoutMap = (Map)returnedFileList.get(0);
    	String output = (String)stdoutMap.get("data");

		/* Example "output":

    	-Queue ID- --Size-- ----Arrival Time---- -Sender/Recipient-------
    	A391E167584     1164 Thu Jun 30 11:23:15  zimbra@zqa-429.eng.vmware.com
    	(delivery temporarily suspended: connect to zqa-429.eng.vmware.com[10.137.245.174]:7025: Connection refused)
    	                                         admin@zqa-429.eng.vmware.com

    	03E3A16757E     2012 Thu Jun 30 11:25:52  enus130945819697629@testdomain.com
    	(delivery temporarily suspended: connect to zqa-429.eng.vmware.com[10.137.245.174]:7025: Connection refused)
    	                                         enus130945833860146@testdomain.com

    	48E6016757B     1164 Thu Jun 30 11:21:46  zimbra@zqa-429.eng.vmware.com
    	  (connect to zqa-429.eng.vmware.com[10.137.245.174]:7025: Connection refused)
    	                                         admin@zqa-429.eng.vmware.com

    	9A588167581     1164 Thu Jun 30 11:23:15  zimbra@zqa-429.eng.vmware.com
    	(delivery temporarily suspended: connect to zqa-429.eng.vmware.com[10.137.245.174]:7025: Connection refused)
    	                                         admin@zqa-429.eng.vmware.com


    	-- 17 Kbytes in 4 Requests.

    				*/

    	//
    	// Determine each queue ID
    	// ID's are 10 hex digits
    	//
    	
    	// Keep a table of strings to entries, i.e.
    	// key = 48E6016757B
    	// value = 48E6016757B     1164 Thu Jun 30 11:21:46  enus1231@zqa-429.eng.vmware.com\n(connect to ... 
    	//
    	Hashtable<String, String> idTable = new Hashtable<String, String>();

    	// Look for all instances of a 10 digit hex number (The queue ID)
    	// Pattern patter = Pattern.compile("\\b[0-9A-F]{10}\\b");
    	Pattern pattern = Pattern.compile("^[0-9A-F]+\\b", Pattern.MULTILINE);
    	Matcher matcher = pattern.matcher(output);
    	
    	while (matcher.find()) {
    		logger.debug("matched: "+ matcher.group() +" at "+ matcher.start() + " to "+ matcher.end());

    		String id = matcher.group();
    		int start = matcher.start();
    		String value = output.substring(start);
    		logger.debug("matched: "+ id + " value "+ value);

    		// Determine if there is another message in the queue
    		// And, if yes, then delete the remainder from "value"
    		Matcher next = pattern.matcher(value);
    		if ( matcher.find() ) {
    			int finish = next.start();
    			value = value.substring(0, finish);
    		}
    		
    		// Add this ID and element to the table
    		idTable.put(id, value);
    		logger.debug("matched: "+ id + " value "+ value);
    	}

		// Separate all the current queue IDS associated with the test account
		ArrayList<String> qid = new ArrayList<String>(); 
		
		for (Map.Entry<String, String> entry : idTable.entrySet()) {
			if ( entry.getValue().contains(emailaddress) )
				qid.add(entry.getKey());
		}

		// Delete each ID one by one
		deletePostqueueItems(qid);
		
		throw new HarnessException("Message(s) never delivered from queue.  IDs: "+ qid.toString());
			
	}

	public void deletePostqueueItems(ArrayList<String> ids)
			throws HarnessException {
		
		// STAF <SERVER> PROCESS START COMMAND
		// /opt/zimbra/postfix/sbin/postsuper PARMS -d <queue_id> RETURNSTDOUT
		// RETURNSTDERR WAIT 60000
		
		for (String id : ids)
			this.deletePostqueueItem(id);
		
	}
	
	private void deletePostqueueItem(String id) throws HarnessException {

		// STAF <SERVER> PROCESS START COMMAND
		// /opt/zimbra/postfix/sbin/postsuper PARMS -d <queue_id> RETURNSTDOUT
		// RETURNSTDERR WAIT 60000

		execute("/opt/zimbra/postfix/sbin/postsuper -d " + id);

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
							command, this.getTimeout());
		else
			// Running a command as 'zimbra' user.
			StafParms = String
					.format(
							"START SHELL COMMAND \"su - zimbra -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d",
							command, this.getTimeout());

		return (getStafCommand());
	}
}
