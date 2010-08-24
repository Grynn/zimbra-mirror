/**
 * 
 */
package framework.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import framework.util.SkipTestClass;
import framework.util.SummaryReporter;
import framework.util.TestStatusReporter;
import framework.util.ZimbraSeleniumProperties;



/**
 * @author Matt Rhoades
 *
 */
public class ExecuteHarnessMain {
	private static Logger logger = LogManager.getLogger(ExecuteHarnessMain.class);
	
	/**
	 * This token must appear in the class package name.
	 * The next subpackage after the token is the TestNG test name
	 * All subsequent packages are assumed to be tests
	 * Ex: com.zimbra.qa.seleneium.projects.zcs.tests.addressbook.CreateContact.java
	 * addressbook will be the test name
	 * CreateContact.java should contain TestNG test methods
	 */
	public static String TEST_TOKEN = ".tests.";
	
	/**
	 * The jarfile containing the TestNG classes
	 */
	public String jarfilename;
	
	/**
	 * The regex pattern used to search for tests, i.e. projects.zcs.tests
	 */
	public String classfilter = null;

	/**
	 * The list of groups to execute
	 */
	public List<String> groups = Arrays.asList("always", "sanity");


	/**
	 * The suite verbosity
	 */
	public int verbosity = 10;
	
	/**
	 * Where output is logged
	 */
	public String testoutputfoldername = null;
	
	/**
	 * Where conf folder is located
	 */
	public String workingfoldername = ".";
	
	
	// A list of classes to execute using TestNG from the jarfile
	protected List<String> classes = null;
	
	// A list of test methods to skip
	protected List<SkipTestClass> skipClasses = null;

	
	/**
	 * Determine all the classes in the specified jarfile filtered by a regex
	 * @param jarfile The jarfile to inspect
	 * @param pattern A regex Pattern to match.  Use null for all classes
	 */
	private static List<String> getClassesFromJar(File jarfile, Pattern pattern) throws FileNotFoundException, IOException {
		logger.debug("getClassesFromJar "+ jarfile.getAbsolutePath());
		
		List<String> classes = new ArrayList<String>();
		
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
	
	private static List<SkipTestClass> loadSkipTestsDetails(File skipFile) throws IOException {

		List<SkipTestClass> skips = new ArrayList<SkipTestClass>();
		BufferedReader br = null;
		
		try {
			
			br = new BufferedReader(new FileReader(skipFile));

			String str = null;
			while ((str = br.readLine()) != null) {
				
				if (str.indexOf("#") >= 0 || str.equals(""))
					continue;
				
				// #className;methodName;locale;browser;bugnumber;remark
				skips.add(new SkipTestClass(str));

			}

		} finally {
			if ( br != null )
				br.close();
		}

		return (skips);

	}




	protected List<String> getXmlTestNames() throws HarnessException {
		
		List<String> testnames = new ArrayList<String>();
		
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
		suite.setVerbose(verbosity);
		
		// Add all the names per the list of classes
		for (String testname : getXmlTestNames()) {
			XmlTest test = new XmlTest(suite);
			test.setName(testname);
			test.setIncludedGroups(groups);
		}
		
		// Add all the classes per the appropriate test name
		for (String c : classes) {
			String testname = getTestName(c);
			for (XmlTest test : suite.getTests()) {
				if ( test.getName().equals(testname)) {
					// TODO: process skipped methods

					XmlClass x = new XmlClass(c);
					test.getXmlClasses().add(x);
					
					break; // back to the classes list
				}
			}
		}
				
		LogManager.getLogger("xmlsuite").info(suite.toXml());
		
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
			
			// Determine which tests to skip
			skipClasses = loadSkipTestsDetails(new File(workingfoldername + "/conf/skipTests.txt"));
			
			// Create the TestNG test runner
			TestNG ng = new TestNG();
			
			// Configure the runner
			ng.setXmlSuites(suites);
			ng.addListener(new SummaryReporter("AJAX"));
			ng.addListener(new TestStatusReporter("AJAX")); // TODO: This shouldn't throw Exception
			ng.setOutputDirectory(testoutputfoldername);

			// Run!
			ng.run();
					
		} catch (HarnessException e) {
			logger.error("Unable to execute tests", e);
		} catch (Exception e) {
			logger.error("Fix TestStatusReporter constructor to not throw raw Exception", e);
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
        options.addOption(new Option("p", "pattern", true, "class filter regex, i.e. projects.zcs.tests."));
        options.addOption(new Option("g", "groups", true, "comma separated list of groups to execute (always, sanity, smoke, full)"));
        options.addOption(new Option("v", "verbose", true, "set suite verbosity"));
        options.addOption(new Option("o", "output", true, "output foldername"));
        options.addOption(new Option("w", "working", true, "current working foldername"));

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
	        
	        if ( cmd.hasOption('o') ) {
	        	this.testoutputfoldername = cmd.getOptionValue('o');
	        } else {
	        	this.testoutputfoldername = ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot") + "/AJAX";
	        }
	        
	        // Make sure the test output folder exists, create it if not
	        File outputfolder = new File(testoutputfoldername);
	        if ( !outputfolder.exists() ) {
	        	outputfolder.mkdirs();
	        }
	        try {
				this.testoutputfoldername = outputfolder.getCanonicalPath();
			} catch (IOException e) {
				logger.warn("Unable to get canonical path of the test output folder ("+ e.getMessage() +").  Using absolute path.");
				this.testoutputfoldername = outputfolder.getAbsolutePath();
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
	        
	        if ( cmd.hasOption('g') ) {
	        	// Remove spaces and split on commas
	        	String[] values = cmd.getOptionValue('g').replaceAll("\\s+", "").split(",");
	        	this.groups = Arrays.asList(values);
	        }
	        
	        if ( cmd.hasOption('v') ) {
	        	this.verbosity = Integer.parseInt(cmd.getOptionValue('v'));
	        }
	        
	        if ( cmd.hasOption('w') ) {
	        	workingfoldername = cmd.getOptionValue('w');
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
	public static void main(String[] args) {
    	BasicConfigurator.configure();

		try {
			ExecuteHarnessMain harness = new ExecuteHarnessMain();
			if ( harness.parseArgs(args) ) {
				harness.execute();
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
		
	}

}
