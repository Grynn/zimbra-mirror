package staf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import framework.core.ExecuteHarnessMain;

public class Driver {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {

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
