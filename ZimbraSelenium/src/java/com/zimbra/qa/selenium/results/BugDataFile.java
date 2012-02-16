package com.zimbra.qa.selenium.results;

import java.io.*;
import java.security.*;
import java.util.*;

import org.apache.log4j.*;

public abstract class BugDataFile {
	protected static Logger logger = LogManager.getLogger(BugStatus.class);

	/**
	 * A list of directory paths that can contain the bug report information
	 */
	protected static List<File> paths = null;
	
	/**
	 * A map of file names to file size.  Used to determine whether to reload the data
	 */
	protected static Map<String, String> hashes = new HashMap<String, String>();
	

	protected BugDataFile() {
		logger.info("new " + BugDataFile.class.getCanonicalName());
		
		if (paths == null) {
			paths = new ArrayList<File>();
			paths.add(new File("/opt/qa/testlogs/BugReports"));
			paths.add(new File("T:\\BugReports"));
			paths.add(new File("C:\\BugReports"));
		}
		
	}


	/**
	 * Return the database file, if it exists, in the normal data paths 
	 * @param filename
	 * @return
	 * @throws IOException 
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	protected File getDatafile(String filename) throws FileNotFoundException {
		
		// Find where the database files are located
		for (File directory : paths) {
			File file = new File(directory, filename);
			if ( file.exists() ) {
				return (file);
			}
		}
		
		throw new FileNotFoundException("Unable to locate "+ filename +" in "+ Arrays.toString(paths.toArray()));
	}
	
}
