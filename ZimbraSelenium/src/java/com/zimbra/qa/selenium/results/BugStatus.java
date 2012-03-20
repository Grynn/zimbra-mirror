package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;


public class BugStatus extends BugDataFile {

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
	
	private static String LastResult = "Bug Status: Processed 0 bugs";
	
	public static String getResultString() {
		return (LastResult);
	}
	
	
	protected static final String DataFilename = "bugStatus.txt";

	
	protected BugStatus() {
		logger.info("new " + BugStatus.class.getCanonicalName());
	}


	protected Map<String, BugState> getData() throws IOException {
		
		// New datafile was found.  Clear the map
		Map<String, BugState> bugStatusMap = new HashMap<String, BugState>();
		
		// Read the file and build the map
		BufferedReader reader = null;
		String line;
		
		try {
			
			reader = new BufferedReader(new FileReader(getDatafile(DataFilename)));
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

		updateResultString(bugStatusMap);

		return (bugStatusMap);
	}
	
	private void updateResultString(Map<String, BugState> map) {
		StringBuilder sb = new StringBuilder();
		sb.append("Bug Status: Processed");
		
		for (BugState state : BugState.values()) {
			int count = 0;
			for (Map.Entry<String, BugState> entry : map.entrySet()) {
				if (entry.getValue().equals(state)) {
					count++;
				}
			}
			sb.append("\n\t").append(count).append(" ").append(state.toString());
		}
		
		LastResult = sb.toString();
	}


}
