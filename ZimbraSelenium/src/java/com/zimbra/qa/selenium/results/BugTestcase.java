package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;


public class BugTestcase extends BugDataFile {

	/**
	 * Return the current list of "Test Case" to "List of BugIDs"
	 * @return
	 * @throws IOException
	 */
	public static Map<String, List<String>> getTestcaseData() throws IOException {
		BugTestcase engine = new BugTestcase();
		return (engine.getData());
	}
	
	private static String LastResult = "Bug Test Case #: Processed 0 bugs";

	public static String getResultString() {
		return (LastResult);
	}

	
	
	protected static final String DataFilename = "bugTestcase.txt";
	
	private int CountTestcases = 0;
	
	protected BugTestcase() {
		logger.info("new " + BugTestcase.class.getCanonicalName());
	}


	protected Map<String, List<String>> getData() throws IOException {
		
		
		Map<String, List<String>> bugTestcaseMap = new HashMap<String, List<String>>();
		
		// Read the file and build the map
		BufferedReader reader = null;
		String line;
		
		try {
			
			reader = new BufferedReader(new FileReader(getDatafile(DataFilename)));
			while ( (line=reader.readLine()) != null ) {
	
				// Example: genesis/data/zmstatctl/basic.rb	29149 40782
				String[] values = line.split("\\s");
				if ( values.length <= 1 ) {
					logger.warn("bugTestcase: invalid line: "+ line);
					continue;
				}
				
				String bugtestcase = values[0];
				values = line.replace(bugtestcase, "").split("\\s");
				
				bugTestcaseMap.put(bugtestcase, Arrays.asList(values));
				logger.debug("bugTestcase: put "+ line);

				CountTestcases++;
			}
			
		} finally {
			if ( reader != null )
				reader.close();
			reader = null;
		}

		LastResult = String.format("Bug Test Case #: Processed\n\t%d bugs with test case IDs", CountTestcases);
		
		return (bugTestcaseMap);
	}
	


}
