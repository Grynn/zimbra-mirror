package com.zimbra.qa.selenium.results;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public abstract class TestCaseEngine {
	
	protected static Logger logger = LogManager.getLogger(TestCaseEngine.class);

	protected int CountPass = 0;
	protected int CountFail = 0;
	
	
	/**
	 * A pointer to the corresponding results file.  
	 * 
	 * This value could be a file or folder.
	 * 
	 * Folder: SOAP results
	 * File: Nunit Results.xml file
	 * File: TestNG testng-results.xml file
	 * etc.
	 * 
	 */
	private File ResultsFile = null;

	
	
	/**
	 * Create a new Engine to process the results
	 * @param results A pointer to the corresponding results file or folder
	 */
	protected TestCaseEngine(File results) {
		logger.info("new "+ TestCaseEngine.class.getCanonicalName());

		setResultsFile(results);
	}
	
	
	/**
	 * Return a map of test case names and pass/fail results based on test results
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws DocumentException
	 */
	public abstract Map<String, Boolean> getData() throws UnsupportedEncodingException, IOException, DocumentException;
	
	
	


	public File getResultsFile() {
		return ResultsFile;
	}


	public void setResultsFile(File resultsFile) {
		ResultsFile = resultsFile;
	}


	/**
	 * Return a Document format of the results file (if XML formatted)
	 * @return
	 * @throws DocumentException 
	 */
	public Document getResultsDocument() throws DocumentException {
		
		SAXReader reader = new SAXReader();
		return (reader.read(getResultsFile()));

	}
	
	
}
