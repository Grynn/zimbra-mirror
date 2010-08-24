/**
 * 
 */
package framework.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import framework.util.HarnessException;



/**
 * @author Matt Rhoades
 *
 */
public class ExecuteHarnessMain {
	private static Logger logger = LogManager.getLogger(ExecuteHarnessMain.class);
	
	public static String TEST_TOKEN = ".tests.";
	
	/**
	 * The jarfile containing the TestNG classes
	 */
	public String jarfilename;
	
	/**
	 * The regex pattern used to search for tests, i.e. projects.zcs.tests
	 */
	public String classfilter = null;

	// A list of classes to execute using TestNG from the jarfile
	protected ArrayList<String> classes = null;
	
	/**
	 * Determine all the classes in the specified jarfile filtered by a regex
	 * @param jarfile The jarfile to inspect
	 * @param pattern A regex Pattern to match.  Use null for all classes
	 */
	private static ArrayList<String> getClassesFromJar(File jarfile, Pattern pattern) throws FileNotFoundException, IOException {
		logger.debug("getClassesFromJar "+ jarfile.getAbsolutePath());
		
		ArrayList<String> classes = new ArrayList<String>();
		
		JarInputStream jarFile = new JarInputStream(new FileInputStream(jarfile));
		while (true) {
			JarEntry jarEntry = jarFile.getNextJarEntry();
			
			if ( jarEntry == null )
				break; // All Done!


			if ( !jarEntry.getName().endsWith(".class") )
				continue; // Only process classes
			
			if ( jarEntry.getName().contains("CommonTest.class") )
				continue; // Skip CommonTest, since those aren't tests
			
			String name = jarEntry.getName().replace('/', '.').replaceAll(".class$", "");
			logger.debug("Class: "+ name);

			if ( pattern != null ) {
				
				Matcher matcher = pattern.matcher(name);
				if (matcher.find()) {
					
					// Class name matched the filter.  add it.
					classes.add(name);
				}
				
			} else {
				
				// No filter.  add all.
				classes.add(name);
				
			}
			
		}

		return (classes);
	}
	
	/**
	 * Get the testname for a given class, per Zimbra standard formatting
	 * The test name is the package part after .tests.
	 */
	private static String getTestName(String classname) throws HarnessException {
		String token = TEST_TOKEN;
		
		int indexOfTests = classname.indexOf(token);
		if ( indexOfTests < 0 )
			throw new HarnessException("class names must contain " + token + " ("+ classname +")");
		
		int indexOfDot = classname.indexOf('.', indexOfTests + token.length());
		if ( indexOfDot < 0 )
			throw new HarnessException("class name doesn't contain ending dot (" + classname + ")");

		String testname = classname.substring(indexOfTests + token.length(), indexOfDot);
		logger.debug("testname: "+ testname);

		return (testname);
	}

	protected ArrayList<String> getXmlTestNames() throws HarnessException {
		
		ArrayList<String> testnames = new ArrayList<String>();
		
		// Split the test list into tests based on the 'next' package
		// i.e. projects.zcs.tests.addressbook goes into the "addressbook" test
		// i.e. projects.zcs.tests.briefcase goes into the "briefcase" test
		//
		// Use the next package after ".tests."
		//
		for (String c : classes) {
			
			String testname = getTestName(c);
			
			// Check if the test name already exists
			//
			if ( !testnames.contains(testname) ){
				logger.debug("Add new testiname "+ testname);
				testnames.add(testname);
			}
						
		}

		return (testnames);
	}

	protected List<XmlSuite> getXmlSuiteList() throws HarnessException {
		
		// Only one suite per run in the zimbra process (subject to change)
		XmlSuite suite = new XmlSuite();
		suite.setName("zimbra");
		
		// Add all the names per the list of classes
		for (String testname : getXmlTestNames()) {
			XmlTest test = new XmlTest(suite);
			test.setName(testname);
		}
		
		// Add all the classes per the appropriate test name
		for (String c : classes) {
			String testname = getTestName(c);
			for (XmlTest test : suite.getTests()) {
				if ( test.getName().equals(testname)) {
					test.getXmlClasses().add(new XmlClass(c));
					break; // back to the classes list
				}
			}
		}
				
		logger.debug("Suite:");
		logger.debug(suite.toXml());
		
		return (Arrays.asList(suite));
	}
	
	/**
	 * Execute all TestNG tests based on configuration
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void execute() throws FileNotFoundException, IOException {
		logger.info("Execute tests ...");
		
		try
		{

			// Build the class list
			classes = getClassesFromJar(new File(jarfilename), (classfilter == null ? null : Pattern.compile(classfilter)));
			
			// Build the list of XmlSuites
			List<XmlSuite> suites = getXmlSuiteList();
			
			// Run!
			TestNG ng = new TestNG();
			ng.setXmlSuites(suites);
			ng.run();
		
		} catch (HarnessException e) {
			logger.error("Unable to execute tests", e);
		}
		
		logger.info("Execute tests ... completed");
	}
	
	/**
	 * Parse command line arguments
	 * @param arguments
	 * @return
	 * @throws ParseException
	 */
    public boolean parseArgs(String arguments[]) throws ParseException {

    	// Build option list
    	Options options = new Options();
        options.addOption(new Option("h", "help", false, "print usage"));
        options.addOption(new Option("l", "log4j", true, "log4j file containing log4j configuration"));
        options.addOption(new Option("j", "jarfile", true, "jarfile containing test cases"));
        options.addOption(new Option("p", "pattern", true, "class filter, i.e. projects.project.tests.*"));

        // Set required options
        options.getOption("j").setRequired(true);
        
        try
        {
	        CommandLineParser parser = new GnuParser();
	        CommandLine cmd = parser.parse(options, arguments);
	        
	        if ( cmd.hasOption('h') ) {
	    		HelpFormatter formatter = new HelpFormatter();
	    		formatter.printHelp("ExecuteTests", options);
	        	return false;
	        }
	        
	        if ( cmd.hasOption('l') ) {
	        	PropertyConfigurator.configure(cmd.getOptionValue('l'));
	        } else {
	        	BasicConfigurator.configure();
	        }
	        
	        if ( cmd.hasOption('j') ) {
	        	this.jarfilename = cmd.getOptionValue('j'); 
	        }
	        
	        if ( cmd.hasOption('p') ) {
	        	this.classfilter = cmd.getOptionValue('p'); 
	        }
	        
        } catch (ParseException e) {
    		HelpFormatter formatter = new HelpFormatter();
    		formatter.printHelp("ExecuteTests", options);
        	throw e;
        }
        
        return (true);
    }
    
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		
		ExecuteHarnessMain harness = new ExecuteHarnessMain();
		if ( harness.parseArgs(args) ) {
			harness.execute();
		}
		
	}

}
