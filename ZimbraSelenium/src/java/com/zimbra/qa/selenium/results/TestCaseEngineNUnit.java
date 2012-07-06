package com.zimbra.qa.selenium.results;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

public class TestCaseEngineNUnit extends TestCaseEngine {
	protected static Logger logger = LogManager.getLogger(TestCaseEngineNUnit.class);
	
	

	
	public TestCaseEngineNUnit(File results) {
		super(results);
		logger.info("new "+ TestCaseEngineNUnit.class.getCanonicalName());
		
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Boolean> getData() throws UnsupportedEncodingException, IOException, DocumentException
	{
		
		Map<String, Boolean> map = new HashMap<String, Boolean>();

		
		Document document = this.getResultsDocument();
		List nodes = document.getRootElement().selectNodes("//test-case");
		for ( Object n : nodes ) {
			if ( n instanceof Element ) {
				Element e = (Element)n;
				
	        	String name = e.attributeValue("name", "undefined");
	        	String executed = e.attributeValue("executed", "False");
	        	String success = e.attributeValue("success", "False");
	        	String time = e.attributeValue("time", "-1");
	        	String asserts = e.attributeValue("asserts", "-1");
	        	
	        	logger.info(String.format("%s: executed %s, success %s, time %s, asserts %s",
	        			name, executed, success, time, asserts));

	        	String testcase = name;
	        	Boolean result = success.equals("True");
	        	
        		map.put(testcase, result);
        		
        		if ( result ) {
        			CountPass++;
        		} else {
        			CountFail++;
        		}

			}
		}
		
		return (map);

	}

}
