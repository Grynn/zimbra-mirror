package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.dom4j.DocumentException;

public class TestCaseData {
	protected static Logger logger = LogManager.getLogger(TestCaseData.class);
	
	
	/**
	 * Return the current list of "Bug ID" to "Bug Status"
	 * @param root
	 * @return
	 * @throws IOException
	 * @throws DocumentException 
	 * @throws ResultsException 
	 */
	public static Map<String, Boolean> getStatusData(File root) throws IOException, DocumentException, ResultsException {

		// Validate root
		if ( root == null )
			throw new NullPointerException("root folder was null");
		
		// Validate root
		if ( !root.exists() )
			throw new FileNotFoundException(root.getCanonicalPath() +" does not exist");

		
		
		TestCaseEngine engine = TestCaseEngineFactory.getEngine(root);
		if ( engine == null ) {
			throw new ResultsException("Unable to create engine");
		}
		
		return (engine.getData());
		
		
	}
	
	private static String LastResult = "Test Case Results: Parsed 0 Total Tests, 0 Passed Tests, 0 Failed Tests";

	public static String getResultString() {
		return (LastResult);
	}
	
	public static void setResultString(String result) {
		LastResult = result;
	}
	
	


}
