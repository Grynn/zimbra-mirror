package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;


public class BugStatus extends BugData {

	public enum BugState {
		UNCONFIRMED,
		NEW,
		ASSIGNED,
		REOPENED,
		RESOLVED,
		VERIFIED,
		CLOSED
	}
	
	/**
	 * Return the current list of "Bug ID" to "Bug Status"
	 * @return
	 * @throws IOException
	 */
	public static Map<String, BugState> getStatusData() throws IOException {
		BugStatus engine = new BugStatus();
		return (engine.getData());
	}
	

	
	
	protected static final String DataFilename = "bugStatus.txt";

	protected static Map<String, BugState> bugStatusMap = new HashMap<String, BugState>();
	
	protected BugStatus() {
		logger.info("new " + BugStatus.class.getCanonicalName());
	}


	protected Map<String, BugState> getData() throws IOException {
		
		// Open the data file
		File datafile = getDatafile(DataFilename);

		if ( datafile == null )
			return (bugStatusMap);			// No file OR file didn't change
		
		// New datafile was found.  Clear the map
		bugStatusMap = new HashMap<String, BugState>();
		
		// Read the file and build the map
		BufferedReader reader = null;
		String line;
		
		try {
			
			reader = new BufferedReader(new FileReader(datafile));
			while ( (line=reader.readLine()) != null ) {

				// Example: 50208	RESOLVED
				String[] values = line.split("\\s");
				if ( values.length != 2 ) {
					logger.warn("bugStatus: invalid line: "+ line);
					continue;
				}
				
				String bugid = values[0];
				BugState bugState = BugState.valueOf(values[1]);
							
				bugStatusMap.put(bugid, bugState);
				logger.debug("bugStatus: put "+ line);
				
			}
			
		} finally {
			if ( reader != null )
				reader.close();
			reader = null;
		}


		return (bugStatusMap);
	}
	


}
