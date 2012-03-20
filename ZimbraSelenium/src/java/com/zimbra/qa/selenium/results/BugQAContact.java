package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;


public class BugQAContact extends BugDataFile {

	/**
	 * Return the current list of "Bug ID" to "QA Contact"
	 * @return
	 * @throws IOException
	 */
	public static Map<String, String> getQAContactData() throws IOException {
		BugQAContact engine = new BugQAContact();
		return (engine.getData());
	}
	
	private static String LastResult = "Bug Test Case #: Processed 0 bugs";

	public static String getResultString() {
		return (LastResult);
	}

	
	
	protected static final String DataFilename = "bugQaContact.txt";
	
	protected static Map<String, String> bugQAContactMap = new HashMap<String, String>();
	
	private int CountQACount = 0;
	
	protected BugQAContact() {
		logger.info("new " + BugQAContact.class.getCanonicalName());
	}


	protected Map<String, String> getData() throws IOException {
				
		// New datafile was found.  Clear the map
		Map<String, String> bugQAContactMap = new HashMap<String, String>();
		
		// Read the file and build the map
		BufferedReader reader = null;
		String line;
		
		try {
			
			reader = new BufferedReader(new FileReader(getDatafile(DataFilename)));
			while ( (line=reader.readLine()) != null ) {
				
				// Example: 42337	sarang@zimbra.com

				String[] values = line.split("\\s");
				if ( values.length != 2 ) {
					logger.warn("bugQAContact: invalid line: "+ line);
					continue;
				}
				
				String bugid = values[0];
				String bugcontact = values[1];
				
				bugQAContactMap.put(bugid, bugcontact);
				logger.debug("bugQAContact: put "+ line);

				CountQACount++;
				
			}
			
		} finally {
			if ( reader != null )
				reader.close();
			reader = null;
		}

		LastResult = String.format("QA Contact: Processed\n\t%d bugs with QA Contact", CountQACount);

		return (bugQAContactMap);
	}
	


}
