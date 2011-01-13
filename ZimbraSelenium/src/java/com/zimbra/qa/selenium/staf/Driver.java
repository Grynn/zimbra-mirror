package com.zimbra.qa.selenium.staf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain;
import com.zimbra.qa.selenium.framework.util.HarnessException;


public class Driver {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws HarnessException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, HarnessException {

        // Create the execution object
        ExecuteHarnessMain harness = new ExecuteHarnessMain();
        
        harness.jarfilename = "jarfile.jar";
        harness.classfilter = "projects.mobile.tests";
        harness.groups = Arrays.asList("always", "sanity");
        
        // Execute!
		String response = harness.execute();
		
		System.out.println(response);
		
	}

}
