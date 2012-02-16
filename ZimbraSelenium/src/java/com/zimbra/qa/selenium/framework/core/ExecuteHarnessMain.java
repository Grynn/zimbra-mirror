/**
 * 
 */
package com.zimbra.qa.selenium.framework.core;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.jar.*;
import java.util.regex.*;

import org.apache.commons.cli.*;
import org.apache.commons.cli.CommandLine;
import org.apache.log4j.*;
import org.testng.*;
import org.testng.xml.*;

import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;




/**
 * The <code>ExecuteHarnessMain</code> class is the main execution class for the
 * Zimbra Selenium Harness.
 * <p>
 * Typical usage:<p>
 * <pre>
 * {@code
 * 
 * ExecuteHarnessMain harness = new ExecuteHarnessMain();
 * harness.jarfilename = "foo.jar";
 * harness.classfilter = "projects.tests.ajax";
 * harness.groups = "always,sanity".split(',');
 * harness.testoutputfoldername = "logs";
 * harness.execute();
 * 
 * }
 * </pre>
 * 
 * @author Matt Rhoades
 *
 */
public class ExecuteHarnessMain {
	private static Logger logger = LogManager.getLogger(ExecuteHarnessMain.class);

	/**
	 * A Log4j logger for tracing test case steps
	 */
	
	public static final String TraceLoggerName = "testcase.trace";
	public static Logger tracer = LogManager.getLogger(TraceLoggerName);

	private static HashMap<String, String> configMap= new HashMap<String,String>();
	
	
	public ExecuteHarnessMain() {
		
	}
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
	 * The regex pattern used to search for tests<p>For example, projects.zcs.tests
	 */
	public String classfilter = null;

	
	/**
	 * The regex pattern used to exclude search for tests<p>For example, projects.zcs.tests
	 */
	public String excludefilter = null;

	/**
	 * The list of groups to execute
	 */
	public ArrayList<String> groups = new ArrayList<String>(Arrays.asList("always", "sanity"));

	/**
	 * The list of groups to exclude
	 */
	public ArrayList<String> excludeGroups = new ArrayList<String>(Arrays.asList("skip"));
		
	/**
	 * The suite verbosity
	 */
	public int verbosity = 10;
	
	/**
	 * Where output is logged
	 */
	protected String testoutputfoldername = null;
	public void setTestOutputFolderName(String path) {
		
		System.setProperty("zimbraSelenium.output", path);
		
		// The Code Coverage report should exist at the root
		File coverage = new File(path + "/coverage");
		if ( !coverage.exists() )	coverage.mkdirs();
		CodeCoverage.getInstance().setOutputFolder(coverage.getAbsolutePath());

		String browser = ZimbraSeleniumProperties.getStringProperty(
				ZimbraSeleniumProperties.getLocalHost() + ".browser",
				ZimbraSeleniumProperties.getStringProperty("browser"));
		
		if (browser.charAt(0) == '*') {
			browser = browser.substring(1);
			if ((browser.indexOf(" ")) > 0) {
				String str = browser.split(" ")[0];
				int i;
				if ((i = browser.lastIndexOf("\\")) > 0) {
					str += "_" + browser.substring(i+1);
				}
				browser = str;
			}
		}
		
		// Save the browser value (for logging)
		ZimbraSeleniumProperties.setStringProperty("CalculatedBrowser", browser);
		

		// Append the app, browser, locale
		path += "/"
			+ ZimbraSeleniumProperties.getAppType()
			+ "/"
			+	browser						
			+ "/" + ZimbraSeleniumProperties.getStringProperty("locale");
		
		// Make sure the path exists
		File output = new File(path);
		if ( !output.exists() )		output.mkdirs();
		
		// Set the property to the absolute path
		try {
			testoutputfoldername = output.getCanonicalPath();
		} catch (IOException e) {
			logger.warn("Unable to get canonical path of the test output folder ("+ e.getMessage() +").  Using absolute path.");
			testoutputfoldername = output.getAbsolutePath();
		}
		
		// Make sure any other dependent folders exist
		File debug = new File(testoutputfoldername + "/debug");
		if ( !debug.exists() )		debug.mkdirs();
		
		File testng = new File(testoutputfoldername + "/TestNG");
		if ( !testng.exists() )		testng.mkdirs();
		
		
	}
	
	/**
	 * Where conf folder is located
	 */
	public String workingfoldername = ".";
	 	
	 	
	// A list of classes to execute using TestNG from the jarfile
	protected List<String> classes = null;



	/**
	 * Determine all the classes in the specified jarfile filtered by a regex
	 * @param jarfile The jarfile to inspect
	 * @param pattern A regex Pattern to match.  Use null for all classes
	 * @throws HarnessException 
	 */ 
	private static List<String> getClassesFromJar(File jarfile, Pattern pattern, String excludeStr) throws FileNotFoundException, IOException, HarnessException {
		logger.debug("getClassesFromJar "+ jarfile.getAbsolutePath());
		
		List<String> classes = new ArrayList<String>();
		
		JarInputStream jarFile = null;
		try {
			
			jarFile = new JarInputStream(new FileInputStream(jarfile));
			
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
						if (!isExcluded(name,excludeStr)) {
						  classes.add(name);
						}
					}
					
				} else {
					
					// No filter.  add all.
					if (!isExcluded(name,excludeStr)) {
					  classes.add(name);
					}
					
				}
				
			}

			
		} finally {
			if ( jarFile != null ) {
				jarFile.close();
				jarFile = null;
			}
		}

		if (classes.size() < 1) {
			throw new HarnessException("no classes matched pattern filter "+ pattern.pattern());
		}
		
		return (classes);
	}

	private static boolean isExcluded(String name, String excludeStr) {
		boolean result=false;
		
		if (excludeStr == null) {
			return result;
		}
		
		if (excludeStr.indexOf(";") == -1) {
			result = (name.indexOf(excludeStr) != -1);
		}
		else {
			String[] splitStr= excludeStr.split(";");
			for (int j=0; j< splitStr.length; j++){
			   if (result = (name.indexOf(splitStr[j]) != -1)) {
				   break;
			   }
			}			
		}
		return result;
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
	


	/**
	 * Based on the contents of the jarfile, build the list of TestNG test names
	 * @return
	 * @throws HarnessException
	 */
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

	/**
	 * Based on the contents of the jarfile, build the list of XmlSuite to execute
	 * @return
	 * @throws HarnessException
	 */
	protected List<XmlSuite> getXmlSuiteList() throws HarnessException {

		// Add network or foss based on the server version
		if ( ZimbraSeleniumProperties.zimbraGetVersionString().toLowerCase().contains("network") ) {
			excludeGroups.add("foss");
		} else {
			excludeGroups.add("network");
		}
		
		// If groups contains "performance", then enable performance metrics gathering
		PerfMetrics.getInstance().Enabled = groups.contains("performance");

		// Only one suite per run in the zimbra process (subject to change)
		XmlSuite suite = new XmlSuite();
		suite.setName("zimbra");
		suite.setVerbose(verbosity);
		suite.setThreadCount(4);
		suite.setParallel(XmlSuite.PARALLEL_NONE);
		
		// Add all the names per the list of classes
		for (String testname : getXmlTestNames()) {
			XmlTest test = new XmlTest(suite);
			test.setName(testname);
			test.setIncludedGroups(groups);
	        test.setExcludedGroups(excludeGroups);
		}
		
		// Add all the classes per the appropriate test name
		for (String c : classes) {
			String testname = getTestName(c);
			for (XmlTest test : suite.getTests()) {
				if ( test.getName().equals(testname)) {

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
	 * Execute tests
	 * @throws HarnessException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public String execute() throws HarnessException, FileNotFoundException, IOException {
		logger.info("Execute ...");
		
		Date start = new Date();
		Date finish;

		StringBuilder result = new StringBuilder();

		try {

			// Each method handles different subsystem ...
			// executeCodeCoverage() ... if configured, instrument/uninstrument server code
			// executeSelenium() ... if configured, start/stop seleneium
			// executeTests() ... execute TestNG tests
			String response = executeCodeCoverage();
			result.append(response).append('\n');
			
		} finally {
			finish = new Date();
		}
		
		// calculate how long the tests took
		long duration = finish.getTime() - start.getTime();
		result.append("Duration: ").append(duration / 1000).append(" seconds\n");
		result.append("Browser: ").append(ZimbraSeleniumProperties.getStringProperty("CalculatedBrowser", "unknown")).append('\n');
		
		return (result.toString());

	}
	
	/**
	 * Instrument/Uninstrument server JS code for code coverage (if configured), and then run tests
	 * @return
	 * @throws FileNotFoundException
	 * @throws HarnessException
	 * @throws IOException
	 */
	protected String executeCodeCoverage() throws FileNotFoundException, HarnessException, IOException {
		
		try {

			try {
				
				CodeCoverage.getInstance().instrumentServer();
				return (executeSelenium());
				
			} finally {
				
				CodeCoverage.getInstance().writeXml();
				CodeCoverage.getInstance().instrumentServerUndo();
				
			}
		} finally {
			
			// Since writing the report may require the (uninstrumented) source code, then
			// write the coverage report last (after uninstrumenting)
			CodeCoverage.getInstance().writeCoverage();
			
		}

	}
	
	
	/**
	 * Start/Stop the selenium server (if configured), and then run tests
	 * @throws HarnessException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	protected String executeSelenium() throws HarnessException, FileNotFoundException, IOException {
		
		try {
			
			SeleniumService.getInstance().startSeleniumServer();
			return (executeTests());
			
		} finally {
			SeleniumService.getInstance().stopSeleniumServer();
		}
		
	}
	
	/**
	 * Execute all TestNG tests based on configuration
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws HarnessException 
	 */
	protected String executeTests() throws FileNotFoundException, IOException, HarnessException {
		logger.info("Execute tests ...");
		
		ResultListener listener = null;
		

		// Build the class list
		classes = getClassesFromJar(new File(jarfilename), (classfilter == null ? null : Pattern.compile(classfilter)),excludefilter);
		
		// Build the list of XmlSuites
		List<XmlSuite> suites = getXmlSuiteList();
					
		// Create the TestNG test runner
		TestNG ng = new TestNG();
		
		for (String st : configMap.keySet()) {
			ZimbraSeleniumProperties.setStringProperty(st,configMap.get(st));
		}
		
		
		// keep checking for server down
		while (ZimbraSeleniumProperties.zimbraGetVersionString().indexOf("unknown") != -1) {
			SleepUtil.sleep(100000);
		}
		
		// Configure the runner
		ng.setXmlSuites(suites);
		ng.addListener(new MethodListener(this.testoutputfoldername));
		ng.addListener(listener = new ResultListener(this.testoutputfoldername));
		
		try {
			ng.setOutputDirectory(this.testoutputfoldername + "/TestNG");
		} catch (Exception e) {
			throw new HarnessException(e);
		}

		// Run!
		ng.run();
		
		// finish inProgress - overwrite inProgress/index.html		
		TestStatusReporter.copyFile(testoutputfoldername + "\\inProgress\\result.txt" , testoutputfoldername + "\\inProgress\\index.html");

		// Write the results to testsummary.txt so that TMS picks them up
    	Logger summary = LogManager.getLogger("testsummary");
    	summary.info("#" + (new Date()));
		if ( listener != null ) {
			summary.info("Passed=" + listener.testsPass);
			summary.info("Failed=" + listener.testsFailed);
			summary.info("Errors=" + listener.testsSkipped);
		}

		logger.info("Execute tests ... completed");
		
		return ( listener == null ? "Done" : listener.getResults() );

	}
	



	
	/**
	 * A TestNG MethodListener that creates a log file for each test class
	 * <p>
	 * @author Matt Rhoades
	 */
	protected static class MethodListener implements IInvokedMethodListener {
		private static Logger logger = LogManager.getLogger(MethodListener.class);
		
		
		private static final String OpenQABasePackage = "org.openqa";
		private static final String ZimbraQABasePackage = "com.zimbra.qa.selenium";
		private static final Logger openqaLogger = LogManager.getLogger(OpenQABasePackage);
		private static final Logger zimbraqaLogger = LogManager.getLogger(ZimbraQABasePackage);
		
		private final Map<String, Appender> appenders = new HashMap<String, Appender>();
		private static final Layout layout = new PatternLayout("%-4r [%t] %-5p %c %x - %m%n");

		private String outputFolder = null;
		
		protected MethodListener(String folder) {
			outputFolder = (folder == null ? "logs" : folder);
		}
		
		protected String getKey(Method method) {
			return (method.getDeclaringClass().getCanonicalName());
		}
		
		protected String getFilename(Method method) {
			// Change the class name in two ways to build the file path:
			// 1. Remove com.zimbra.qa.selenium (for brevity)
			// 2. Change package names to directory names, by changing "." to "/"
			//
			String c = method.getDeclaringClass().getCanonicalName().replace(ZimbraQABasePackage, "").replace('.', '/');
			String m = method.getName();
			return (String.format("%s/debug/%s/%s.txt", outputFolder, c, m));
		}
		
		protected String getTestCaseID(Method method) {
			// The TestCaseId is the fully qualified name of the method
			String c = method.getDeclaringClass().getCanonicalName();
			String m = method.getName();
			return (c + "." + m);
		}
		
		/**
		 * Add a new FileAppender for each class before invocation
		 */
		@Override
		public void beforeInvocation(IInvokedMethod method, ITestResult result) {
			if ( method.isTestMethod() ) {
				
				try {
					String key = getKey(method.getTestMethod().getMethod());
					if ( !appenders.containsKey(key) ) {
						String filename = getFilename(method.getTestMethod().getMethod());
						Appender a = new FileAppender(layout, filename, false);
						appenders.put(key, a);
						openqaLogger.addAppender(a);
						zimbraqaLogger.addAppender(a);
					}
					logger.info("MethodListener: START: " + getTestCaseID(method.getTestMethod().getMethod()));
					
					// Log the associated bugs
					Bugs b = method.getTestMethod().getMethod().getAnnotation(Bugs.class);
					if ( b != null ) {
						logger.info("Associated bugs: "+ b.ids());
					}
					
					// Log the test case trace
					tracer.trace("/***");
					tracer.trace("## ID: " + getTestCaseID(method.getTestMethod().getMethod()));
					tracer.trace("# Objective: " + method.getTestMethod().getDescription());
					tracer.trace("# Group(s): " + Arrays.toString(method.getTestMethod().getGroups()));
					tracer.trace("");
					
				} catch (IOException e) {
					logger.warn("Unable to add test class appender", e);
				}

			}
		}
		
		/**
		 * Remove any FileAppenders after invocation
		 */
		@Override
		public void afterInvocation(IInvokedMethod method, ITestResult result) {
			if ( method.isTestMethod() ) {
				
				try {
					CodeCoverage.getInstance().calculateCoverage(getTestCaseID(method.getTestMethod().getMethod()));
				} catch (HarnessException e) {
					logger.error("Skip logging calculation", e);
				}

				logger.info("MethodListener: FINISH: "+ getTestCaseID(method.getTestMethod().getMethod()));
				
				tracer.trace("");
				tracer.trace("# Pass: " + result.isSuccess());
				tracer.trace("# End ID: " + getTestCaseID(method.getTestMethod().getMethod()));
				tracer.trace("***/");
				tracer.trace("");
				tracer.trace("");

				Appender a = null;
				String key = getKey(method.getTestMethod().getMethod());
				if ( appenders.containsKey(key) ) {
					a = appenders.get(key);
					appenders.remove(key);
				}
				if ( a != null ) {
					openqaLogger.removeAppender(a);
					zimbraqaLogger.removeAppender(a);
					a.close();
					a = null;
				}
			}
		}

	}
	
	/**
	 * A TestNG TestListener that tracks the pass/fail/skip counts
	 * <p>
	 * @author Matt Rhoades
	 */
	public static class ResultListener extends TestListenerAdapter {

		private static final String ZimbraQABasePackage = "com.zimbra.qa.selenium";
		private static ITestResult runningTestCase = null;
		
		private int testsTotal = 0;
		private int testsPass = 0;
		private int testsFailed = 0;
		private int testsSkipped = 0;
		private List<String> failedTests = new ArrayList<String>();
		private List<String> skippedTests = new ArrayList<String>();
		
		public static String outputFolder = null;

		protected ResultListener(String folder) {
			outputFolder = (folder == null ? "logs" : folder);
		}
		
		protected String getResults() {
			StringBuilder sb = new StringBuilder();
			sb.append("Total Tests:   ").append(testsTotal).append('\n');
			sb.append("Total Passed:  ").append(testsPass).append('\n');
			sb.append("Total Failed:  ").append(testsFailed).append('\n');
			sb.append("Total Skipped: ").append(testsSkipped).append('\n');
			if ( !failedTests.isEmpty() ) {
				sb.append("\n\nFailed tests:\n");
				for (String s : failedTests ) {
					sb.append(s).append('\n');
				}
			}
			if ( !skippedTests.isEmpty() ) {
				sb.append("\n\nSkipped tests:\n");
				for (String s : skippedTests ) {
					sb.append(s).append('\n');
				}
			}
			return (sb.toString());
		}
		
		private static int screenshotcount = 0; 
		/**
		 * Generate the screenshot filename name
		 * @param method
		 * @return
		 */
		public static String getScreenCaptureFilename(Method method) {
			String c = method.getDeclaringClass().getCanonicalName().replace(ZimbraQABasePackage, "").replace('.', '/');
			String m = method.getName();
			return (String.format("%s/debug/%s/%sss%d.png", outputFolder, c, m, ++screenshotcount));
		}

		/**
		 * Save a screenshot
		 * @param result
		 * @return
		 */
		public static void getScreenCapture(ITestResult result) {
			String filename = getScreenCaptureFilename(result.getMethod().getMethod());
			logger.warn("Creating screenshot: "+ filename);
			ClientSessionFactory.session().selenium().captureScreenshot(filename);
		}
		
		@Override
		public void onFinish(ITestContext context) {
		}

		@Override
		public void onStart(ITestContext context) {
			
		}

		@Override
		public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
			getScreenCapture(result);
		}

		/**
		 * Add 1 to the failed tests
		 */
		@Override
		public void onTestFailure(ITestResult result) {
			testsFailed++;
			String fullname = result.getMethod().getMethod().getDeclaringClass().getName() +"."+ result.getMethod().getMethod().getName();
			failedTests.add(fullname.replace("com.zimbra.qa.selenium.projects.", "main.projects."));
			getScreenCapture(result);
		}

		/**
		 * Add 1 to the skipped tests
		 */
		@Override
		public void onTestSkipped(ITestResult result) {
			testsSkipped++;	
			String fullname = result.getMethod().getMethod().getDeclaringClass().getName() +"."+ result.getMethod().getMethod().getName();
			skippedTests.add(fullname.replace("com.zimbra.qa.selenium.projects.", "main.projects."));
		}

		/**
		 * Add 1 to the total tests
		 */
		@Override
		public void onTestStart(ITestResult result) {
			runningTestCase = result;
			testsTotal++;
		}

		public static void captureScreen() {
			getScreenCapture(runningTestCase);
		}
		/**
		 * Add 1 to the passed tests
		 */
		@Override
		public void onTestSuccess(ITestResult result) {
			testsPass++;
		}

		@Override
		public void onConfigurationFailure(ITestResult result) {
			getScreenCapture(result);
		}

		@Override
		public void onConfigurationSkip(ITestResult result) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConfigurationSuccess(ITestResult result) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	/**
	 * Parse command line arguments
	 * @param arguments
	 * @return
	 * @throws ParseException
	 * @throws HarnessException 
	 */
    private boolean parseArgs(String arguments[]) throws ParseException, HarnessException {

    	// Build option list
    	Options options = new Options();
        options.addOption(new Option("h", "help", false, "print usage"));
        options.addOption(new Option("l", "log4j", true, "log4j file containing log4j configuration"));
        options.addOption(new Option("d", "dev", false, "use development settings"));
        options.addOption(new Option("j", "jarfile", true, "jarfile containing test cases"));
        options.addOption(new Option("p", "pattern", true, "class filter regex, i.e. projects.zcs.tests."));
        options.addOption(new Option("g", "groups", true, "comma separated list of groups to execute (always, sanity, smoke, full)"));
        options.addOption(new Option("v", "verbose", true, "set suite verbosity (default: "+ verbosity +")"));
        options.addOption(new Option("o", "output", true, "output foldername"));
        options.addOption(new Option("w", "working", true, "current working foldername"));
        options.addOption(new Option("c", "config", true, "dynamic setting config properties i.e browser, server, locale... ( -c 'locale=en_US,browser=firefox' "));

        options.addOption(new Option("e", "exclude", true, "exclude pattern  "));
        options.addOption(new Option("eg", "exclude_groups", true, "comma separated list of groups to exclude when execute (skip)"));
        
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

	     
	        // In the dev environment, server name defaults to localhost
	        // STAF isn't invoked
	        // etc., etc.
	        if ( cmd.hasOption('d') ) {
	        	DevEnvironment.setDevEnvironment(true);
	        }

	        
	        if ( cmd.hasOption('c') ) {
	        	
	           String[] confArray= cmd.getOptionValues('c');
	           
	           for (int i=0; i <confArray.length; i++) {
	        	   //could have form: 'browser=firefox;locale=en_US'
	        	   String[] confItems= confArray[i].split(",");
	        	   
	        	   for (int j=0; j < confItems.length; j++) {
	        		 String[] confItem= confItems[j].split("=");
		        	   
	        		 //check  form config=value and if a valid config name
	        	     if ((confItem.length >1) && (ZimbraSeleniumProperties.getStringProperty(confItem[0]) != null)) {
	        			configMap.put(confItem[0], confItem[1]);
	        		   
	        	     }
	        	   }
	           }
	        }
	        
	        if ( cmd.hasOption('p') ) {
	        	String filter = cmd.getOptionValue('p');
	        	this.classfilter = filter;
	        	
	            // Set the app type on the properties
	            for (AppType t : AppType.values()) {
	            	// Look for ".type." (e.g. ".ajax.") in the pattern
	            	if ( this.classfilter.contains(t.toString().toLowerCase()) ) {
	            		ZimbraSeleniumProperties.setAppType(t);
	                	break;
	            	}
	            }
	        }

	        if ( cmd.hasOption('e') ) {	        	
	        	if (cmd.getOptionValue('e').length() >0) { 
	        	   this.excludefilter = cmd.getOptionValue('e');	
	        	}
	        }

	        //'o' check should be after 'p' check to avoid code redundancy
	        if ( cmd.hasOption('o') ) {
	        	this.setTestOutputFolderName(cmd.getOptionValue('o'));
	        } else {
	        	this.setTestOutputFolderName( 
	        			ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot") +"/"+
	        			ZimbraSeleniumProperties.zimbraGetVersionString()
				    ); 
	        }
	        
	        	
	        // Processing log4j must come first so debugging can happen
	        if ( cmd.hasOption('l') ) {
	        	PropertyConfigurator.configure(cmd.getOptionValue('l'));
	        } else {
	        	BasicConfigurator.configure();
	        }
	        	        
	        if ( cmd.hasOption('j') ) {
	        	this.jarfilename = cmd.getOptionValue('j'); 
	        }
	       	        
	        if ( cmd.hasOption('g') ) {
	        	// Remove spaces and split on commas
	        	String[] values = cmd.getOptionValue('g').replaceAll("\\s+", "").split(",");
	        	this.groups = new ArrayList<String>(Arrays.asList(values));
	        }
	      
	        if ( cmd.hasOption("eg") ) {
	        	// Remove spaces and split on commas
	        	String[] values = cmd.getOptionValue("eg").replaceAll("\\s+", "").split(",");
	        	this.excludeGroups = new ArrayList<String>(Arrays.asList(values));
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
	 * Main execution method
	 * 
	 * Use -h for help
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		BasicConfigurator.configure();

    	String result = "No results";
		try {
			
			// Set the working conditions
			ZimbraSeleniumProperties.setBaseDirectory(".");
			ZimbraSeleniumProperties.setConfigProperties("conf/config.properties");

			// Create the harness object and execute it
			ExecuteHarnessMain harness = new ExecuteHarnessMain();
			if ( harness.parseArgs(args) ) { 
				result = harness.execute();
			}
			
		} catch (Exception e) {
			logger.error(e, e);
		}
		
		logger.info(result);
		System.out.println("*****\n"+ result);

	}

}
