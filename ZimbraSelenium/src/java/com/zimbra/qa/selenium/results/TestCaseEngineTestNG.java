package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.dom4j.DocumentException;

import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;

public class TestCaseEngineTestNG extends TestCaseEngine {
	protected static Logger logger = LogManager.getLogger(TestCaseEngineTestNG.class);
	
	

	
	public TestCaseEngineTestNG(File results) {
		super(results);
		logger.info("new "+ TestCaseEngineTestNG.class.getCanonicalName());
		
	}
	
	
	@Override
	public Map<String, Boolean> getData() throws UnsupportedEncodingException, IOException, DocumentException 
	{
			
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		
		
		// Open the testng-results.xml file and convert to Element
        String docStr = new String(ByteUtil.getContent(this.getResultsFile()), "utf-8");    	
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
        
        TestCaseData.setResultString(
        		String.format("Test Case Results: Parsed\n\t%d Total Tests\n\t%d Passed Tests\n\t%d Failed Tests", 
        		CountPass + CountFail, 
        		CountPass, 
        		CountFail) );
        
        return (map);
	}
	
    /**
     * Runs an XPath query on the specified element context and returns the results.
     */
    @SuppressWarnings("rawtypes")
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
