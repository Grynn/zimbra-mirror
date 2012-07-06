package com.zimbra.qa.selenium.results;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * This class creates the result processing engine objects.
 * 
 * Based on the given result root folder (and contents) determine
 * which test harness generated the results.  Then, create an engine
 * that can process those results.
 * 
 * @author Matt Rhoades
 *
 */
public class TestCaseEngineFactory {

	/**
	 * A mapping of result XML file to Engine classes
	 */
	private static Map<String, String> resultToEngineMap = new HashMap<String, String>() {
		private static final long serialVersionUID = 5313831959348813667L;
	{
		put("testng-results.xml", TestCaseEngineTestNG.class.getCanonicalName());
		put("Results.xml", TestCaseEngineNUnit.class.getCanonicalName());
	}};

	
	/**
	 * Get the TestCaseEngine object based on the given results folder
	 * 
	 * 
	 * @param root
	 * @return
	 * @throws ResultsException
	 */
	public static TestCaseEngine getEngine(File root) throws ResultsException {

		// Based on the files inside root, create a new engine
		for ( Entry<String,String> e : resultToEngineMap.entrySet() ) {
			
			// Look for "results.xml"
			File found = findFile(e.getKey(), root);
			if ( found != null ) {
				return (createEngine(e.getValue(), found));
			}
		}
		
		// No applicable result files!
		throw new ResultsException("Unable to find applicable results file: "+ resultToEngineMap.keySet());
		
	}

	/**
	 * Create a TestCaseEngine class based on the results file that was found
	 * @param className
	 * @param results
	 * @return
	 * @throws ResultsException
	 */
	private static TestCaseEngine createEngine(String className, File results) throws ResultsException
	{
		TestCaseEngine t = null;
		
		try {
						
			Class<?> c = Class.forName(className);
			Class<?> argTypes[] = new Class[1];
			argTypes[0] = File.class;
			Constructor<?> constructor = c.getConstructor(argTypes);
			Object argList[] = new Object[1];
			argList[0] = results;
			t = (TestCaseEngine)constructor.newInstance(argList);

		} catch (Exception e) {
			throw new ResultsException("Unable to create "+ className, e);
		}
			
		return (t);		
	}
	

	/**
	 * Find filename, recursively, in the specified directory
	 * @param filename
	 * @param directory
	 * @return
	 */
	protected static File findFile(String filename, File directory) {
		
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



}
