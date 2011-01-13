package com.zimbra.qa.selenium.results;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ResultsMain {
	private static Logger logger = LogManager.getLogger(ResultsMain.class);
	
	public static final String xmlfilename = "testng-results.xml";
	public static final String resultfilename = "BugReports/BugReport.txt";
	public static final File root = new File(".");
	

	public static File findFile(String filename, File directory) {
		
		// If directory is a file, check if it matches
		if ( directory.isFile() ) {
			if ( filename.equals(directory.getName()) ) {
				// Found it!
				return (directory);
			}
			// Not it
			return (null);
		}
		
		// Check all the directory contents
		for (File f : directory.listFiles()) {
			
			File found = findFile(filename, f);
			if ( found != null ) {
				// Found it!
				return (found);
			}
			
		}
		
		// Not found
		return (null);
	}
	
	public static void main(String[] args) throws Exception {
		
		// Configure logging
		BasicConfigurator.configure();
		logger.info("Starting ...");

		// Find the testng-failed.xml file
		File f = findFile(xmlfilename, root);
		if ( f == null ) {
			logger.error("Unable to file "+ xmlfilename +" in "+ root.getAbsolutePath() );
			return;
		}
		logger.info("Using file: "+ f.getCanonicalPath());

		
		// Create the new core
		ResultsCore core = new ResultsCore();
		
		// Configure it
		core.setResultsXmlFile(f.getAbsolutePath());
		core.setResultsOutputFile(resultfilename);
		
		// Execute it
		core.execute();
		
		logger.info("Done.");
		
	}

}
