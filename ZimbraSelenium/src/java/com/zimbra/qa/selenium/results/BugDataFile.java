package com.zimbra.qa.selenium.results;

import java.io.*;
import java.security.*;
import java.util.*;

import org.apache.log4j.*;

public abstract class BugDataFile {
	protected static final Logger logger = LogManager.getLogger(BugStatus.class);

	
	private static class FilePaths {
		public static final String UNIX_PATH = "/opt/qa/testlogs/BugReports";
		public static final String WINDOWS_TMS_PATH = "T:\\BugReports";
		public static final String WINDOWS_DEV_PATH = "C:\\BugReports";
	}
	
	/**
	 * A list of directory paths that can contain the bug report information
	 */
	private static List<File> paths = null;
	
	

	protected BugDataFile() {
		logger.info("new " + BugDataFile.class.getCanonicalName());
		
		getPaths();
	}


	private synchronized List<File> getPaths() {
		if (paths == null) {
			paths = new ArrayList<File>();
			paths.add(new File(FilePaths.UNIX_PATH));
			paths.add(new File(FilePaths.WINDOWS_TMS_PATH));
			paths.add(new File(FilePaths.WINDOWS_DEV_PATH));
		}
		return (paths);
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
