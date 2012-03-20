package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.dom4j.DocumentException;

import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;

public class TestCaseData {
	protected static Logger logger = LogManager.getLogger(TestCaseData.class);
	
	
	/**
	 * Return the current list of "Bug ID" to "Bug Status"
	 * @param root
	 * @return
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public static Map<String, Boolean> getStatusData(File root) throws IOException, DocumentException {

		TestCaseData engine = new TestCaseData();
		return (engine.getData(root));
		
		
	}
	
	private static String LastResult = "Test Case Results: Parsed 0 Total Tests, 0 Passed Tests, 0 Failed Tests";

	public static String getResultString() {
		return (LastResult);
	}
	
	
	/**
	 * The TestNG XML resutls file that contains the pass/fail information for all executed test cases
	 */
	protected static final String TestNGResultsXMLFilename = "testng-results.xml";
	
	private int CountPass = 0;
	private int CountFail = 0;
	
	protected TestCaseData() {
		logger.info("new "+ TestCaseData.class.getCanonicalName());
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

	/**
	 * Return a map of test case names and pass/fail results based on test testng-results.xml found in root
	 * @param root
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws DocumentException
	 */
	private Map<String, Boolean> getData(File root) throws UnsupportedEncodingException, IOException, DocumentException {
		if ( root == null )
			throw new NullPointerException("root folder was null");
		
		if ( !root.exists() )
			throw new FileNotFoundException(root.getCanonicalPath() +" does not exist");
			

		Map<String, Boolean> map = new HashMap<String, Boolean>();
		
		// Find the folder containing the "testng-results.xml" file
		File results = findFile(TestNGResultsXMLFilename, root);
		if ( results == null )
			throw new FileNotFoundException("Unable to find "+ TestNGResultsXMLFilename +" in "+ root.getAbsolutePath());
		
		// Open the testng-results.xml file and convert to Element
        String docStr = new String(ByteUtil.getContent(results), "utf-8");    	
        Element docElement = Element.parseXML(docStr);
        
        for (Element eClass : getElementsFromPath(docElement, "//class")) {
        	String clazz = eClass.getAttribute("name", "undefined");
        	
        	for (Element eTestmethod : getElementsFromPath(eClass, "//test-method")) {
        		if ( eTestmethod.getAttribute("is-config", "false").equals("true") )
        			continue; // skip common* methods

        		String method = eTestmethod.getAttribute("name", "undefined");
        		Boolean status = eTestmethod.getAttribute("status", "FAIL").equals("PASS");
        		map.put(clazz + "." + method, status);
        		
        		if ( status ) {
        			CountPass++;
        		} else {
        			CountFail++;
        		}
        		
        	}
        }
        
        LastResult = String.format("Test Case Results: Parsed\n\t%d Total Tests\n\t%d Passed Tests\n\t%d Failed Tests", 
        		CountPass + CountFail, 
        		CountPass, 
        		CountFail);
        
        return (map);
	}
	
	
    /**
     * Runs an XPath query on the specified element context and returns the results.
     */
    @SuppressWarnings("unchecked")
	protected Element[] getElementsFromPath(Element context, String path) {
		org.dom4j.Element d4context = context.toXML();
		org.dom4j.XPath xpath = d4context.createXPath(path);
		xpath.setNamespaceURIs(getURIs());
		org.dom4j.Node node;
		List dom4jElements = xpath.selectNodes(d4context);

		List<Element> zimbraElements = new ArrayList<Element>();
		Iterator iter = dom4jElements.iterator();
		while (iter.hasNext()) {
			node = (org.dom4j.Node)iter.next();
			if (node instanceof org.dom4j.Element) {
				Element zimbraElement = Element.convertDOM((org.dom4j.Element) node);
				zimbraElements.add(zimbraElement);
			}
		}

		Element[] retVal = new Element[zimbraElements.size()];
		zimbraElements.toArray(retVal);
		return retVal;
    }
    
	private static Map<String, String> mURIs = null;
    static {
    	mURIs = new HashMap<String, String>();

    	// Add any URI's for xpath processing here
    	// mURIs.put("zimbra", "urn:zimbra");
    }
	private static Map<String, String> getURIs() {
		return mURIs;
	}



}
