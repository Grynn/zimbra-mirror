/**
 * 
 */
package framework.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import framework.util.HarnessException;
import framework.util.SkippedTestListener;
import framework.util.SleepUtil;
import framework.util.SummaryReporter;
import framework.util.TestStatusReporter;
import framework.util.ZimbraSeleniumProperties;
import framework.util.ZimbraSeleniumProperties.AppType;



/**
 * @author Matt Rhoades
 *
 */
public class ExecuteHarnessMain {
	private static Logger logger = LogManager.getLogger(ExecuteHarnessMain.class);
	private static PrintStream ps=null;
	private static ByteArrayOutputStream baos; 
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
	 * The regex pattern used to search for tests, i.e. projects.zcs.tests
	 */
	public String classfilter = null;

	
	/**
	 * The regex pattern used to exclude search for tests, i.e. projects.zcs.tests
	 */
	public String excludefilter = null;

	/**
	 * The list of groups to include execute
	 */
	public List<String> groups = Arrays.asList("always", "sanity");

	
	public List<String> excludeGroups = Arrays.asList("skip");
		
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
	
	
	/**
	 * Determine all the classes in the specified jarfile filtered by a regex
	 * @param jarfile The jarfile to inspect
	 * @param pattern A regex Pattern to match.  Use null for all classes
	 * @throws HarnessException 
	 */ 
	private static List<String> getClassesFromJar(File jarfile, Pattern pattern, String excludeStr) throws FileNotFoundException, IOException, HarnessException {
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
	 * Start the selenium server (if configured) and run tests
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
			
			SeleniumService.getInstance().startSeleniumServer();
			String response = executeTests();
			result.append(response).append('\n');
			
		} finally {
			SeleniumService.getInstance().stopSeleniumServer();
			finish = new Date();
		}
		
		// calculate how long the tests took
		long duration = finish.getTime() - start.getTime();
		result.append("Duration: ").append(duration / 1000).append(" seconds\n");
		
		return (result.toString());
	}
	
	/**
	 * Execute all TestNG tests based on configuration
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws HarnessException 
	 */
	public String executeTests() throws FileNotFoundException, IOException, HarnessException {
		logger.info("Execute tests ...");
		
		ResultListener listener = null;
		
		baos= new ByteArrayOutputStream();
		ps=new PrintStream(baos,true); //autoflush
		
		System.setOut(ps); 
		System.setErr(ps);	
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
		
		String subject = "ZimbraSelenium-" +ZimbraSeleniumProperties.getAppType() +
				" browser:" +ZimbraSeleniumProperties.getStringProperty("browser") + 			
				" locale:" + ZimbraSeleniumProperties.getStringProperty("locale") ;			
		SendEmail se = new SendEmail(subject);
		try {
			se.sendFirstEmail();
		} catch (Exception e) {
			throw new HarnessException(e);
		}


		// Configure the runner
		ng.setXmlSuites(suites);
		
		ng.addListener(new SummaryReporter(ZimbraSeleniumProperties.getAppType().toString()));
		
		//ng.addListener(new SummaryReporter(ZimbraSeleniumProperties.getAppType().toString()));
		try {
			ng.addListener(new TestStatusReporter(ZimbraSeleniumProperties.getAppType().toString(),baos,ps)); // TODO: This shouldn't throw Exception
			ng.addListener(new SkippedTestListener(new File(this.testoutputfoldername)));
			ng.addListener(new MethodListener(this.testoutputfoldername));
			ng.addListener(listener = new ResultListener());
			ng.setOutputDirectory(this.testoutputfoldername);
		} catch (Exception e) {
			throw new HarnessException(e);
		}

		// Run!
		ng.run();
		
		// finish inProgress - overwrite inProgress/index.html		
		TestStatusReporter.copyFile(testoutputfoldername + "\\inProgress\\result.txt" , testoutputfoldername + "\\inProgress\\index.html");
		
		// TODO: remove the email logic.  just let tms send the email.
		// email results
		copyCommandLineOutputFile();
		try {
			se.send(getFileContents(testoutputfoldername + "\\ebody.txt"));
		} catch (Exception e) {
			throw new HarnessException(e);
		}

		
		logger.info("Execute tests ... completed");
		
		return ( listener == null ? "Done" : listener.getResults() );

	}
	

	public String getFileContents(String filePath) {
		String str = "";
		String tmpStr = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));

			while ((tmpStr = in.readLine()) != null) {
				str = str + "\n" + tmpStr;
			}
			in.close();
		} catch (IOException e) {
		}
		return str;
	}


	public void copyCommandLineOutputFile() {
		File f = new File(testoutputfoldername + "/testresult.txt");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		try {

			// Create channel on the source
			FileChannel srcChannel = new FileInputStream(ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot")
					+ "/testresult.txt").getChannel();

			// Create channel on the destination
			FileChannel dstChannel = new FileOutputStream(testoutputfoldername + "/testresult.txt").getChannel();

			// Copy file contents from source to destination
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
			// Close the channels
			srcChannel.close();
			dstChannel.close();
		} catch (IOException e) {
		}

	}

	private static class SendEmail {
		private String subject="SelNG: Couldnt get subject";
		private String body="SelNG: Couldnt get body";
		
		public SendEmail(String sub, String bd){
			subject = sub;
			body = bd;
		}
		
		public SendEmail(String sub){
			subject = sub;			
		}


		public void sendFirstEmail() throws Exception {
				
			String bodyfileXpPath =  ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot")+"\\"  +
			ZimbraSeleniumProperties.zimbraGetVersionString()  + "\\"  + 
            ZimbraSeleniumProperties.getAppType().toString() +"\\" + 
            ZimbraSeleniumProperties.getStringProperty("browser") + "\\" +  
            ZimbraSeleniumProperties.getStringProperty("locale") ;
			
			String lines = "\n--------------------------------------------\n";
			String uri = (bodyfileXpPath.replace("T:/",
					"http://tms.lab.zimbra.com/testlogs/")).replace("\\", "/");
		    uri = ZimbraSeleniumProperties.getStringProperty("TestURL") + uri;
			
			StringBuffer body = new StringBuffer( "");
		
			body.append(ZimbraSeleniumProperties.zimbraGetVersionString());			
		    body.append("\n\nserver: " +ZimbraSeleniumProperties.getStringProperty("server"));
		    body.append("\nclient: " + System.getenv("COMPUTERNAME")) ;
		    	   
			
			body.append("\n\n" + lines + "IN PROGRESS: " + lines + uri+ "/" + TestStatusReporter.inProgressDir );
			
			body.append("\n\n" + lines + "LOGS: " + lines );
			body.append("\n" + "Initialization: \n" + uri+ "/start.txt");
			body.append("\n" + "All Results: \n" + uri+ "/finish.txt");

			body.append("\n\n" + lines + "CATEGORIES: " + lines + uri+ "/" + TestStatusReporter.categoryDir );	
			
			body.append("\n\n" + lines + "Configuration FAIL: " + lines + uri + "/" + TestStatusReporter.confFailDir );
			body.append("\n\n" + lines + "Configuration SKIP: " + lines   + uri + "/" +  TestStatusReporter.confSkipDir);
			body.append("\n\n" + lines + "FAIL: " + lines  + uri + "/" +  TestStatusReporter.failDir );
			
			body.append("\n\n" + lines + "SKIP: "  + lines  + uri + "/" +  TestStatusReporter.skipDir);
			body.append("\n\n" + lines + "PASS: "  + lines  + uri + "/" +  TestStatusReporter.passDir);

			
			body.append("\n\n" + lines + "TestNG REPORTS: " + lines);		
		    body.append("\n" + uri );
			body.append("\n" + uri + "/emailable-report.html");
			
			body.append("\n\n" + lines + "SKIP TESTS INFO: " + lines+ "\n" + uri + "/SkippedTests.txt");

			
            send(body.toString());

		}

	   public void send(String body) throws Exception{
	      Properties props = new Properties();
	      props.setProperty("mail.transport.protocol", "smtp");
	      props.setProperty("mail.host", "mail.zimbra.com");

	      Session mailSession = Session.getDefaultInstance(props, null);
	      Transport transport = mailSession.getTransport();

	      MimeMessage message = new MimeMessage(mailSession);
	      subject= subject.replace("\n", "");
	      subject.replace("\r", "");
	      message.setSubject(subject);
	      message.setContent(body, "text/plain");
	      
	      String emailFrom = ZimbraSeleniumProperties.getStringProperty("emailFrom");
	      emailFrom = ((emailFrom == null) || (emailFrom.length() == 0))?"qa-tms@zimbra.com":emailFrom;
          
          String emailTo = ZimbraSeleniumProperties.getStringProperty("emailTo");
	      emailTo = ((emailTo == null) || (emailTo.length() == 0))?"qa-group@zimbra.com":emailTo;

	      message.setFrom(new InternetAddress(emailFrom));
	      message.addRecipient(Message.RecipientType.TO,
	    		 new InternetAddress(emailTo));

	      transport.connect();
	      transport.sendMessage(message,
	          message.getRecipients(Message.RecipientType.TO));
	      transport.close();
	    }
	}
	public class mySummaryReporter implements IReporter {

	    
	    public mySummaryReporter(String atype){
	    	System.out.println("\nmySummaryReporter" );
	    }	
		public void generateReport(java.util.List<XmlSuite> xmlSuites,
				java.util.List<ISuite> suites, java.lang.String outputDirectory) {
			//long duration = (new Date()).getTime() - startDate.getTime();
			
			
			System.out.println("+++++++++++++++All test ran generateReport" + ZimbraSeleniumProperties.getStringProperty("locale"));
				}
	}
	
	public static class MethodListener implements IInvokedMethodListener {
		private static Logger logger = LogManager.getLogger(MethodListener.class);
		
		private static Logger openqaLogger = LogManager.getLogger("org.openqa");
		private static Logger frameworkLogger = LogManager.getLogger("framework");
		private static Logger projectsLogger = LogManager.getLogger("projects");
		
		public Map<String, Appender> appenders = null;
		private static Layout layout = null;

		private String outputFolder = null;
		
		/**
		 * A MethodListener that creates a log file for each test class
		 * @param folder
		 */
		public MethodListener(String folder) {
			layout = new PatternLayout("%-4r [%t] %-5p %c %x - %m%n");
			appenders = new HashMap<String, Appender>();
			outputFolder = (folder == null ? "logs" : folder);
		}
		
		protected String getKey(Method method) {
			return (method.getDeclaringClass().getCanonicalName());
		}
		
		protected String getFilename(Method method) {
			// String c = method.getDeclaringClass().getCanonicalName().replace('.', '/');
			String c = method.getDeclaringClass().getCanonicalName();
			String m = method.getName();
			return (String.format("%s/debug/%s%s.txt", outputFolder, c, m));
		}
		
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
						frameworkLogger.addAppender(a);
						projectsLogger.addAppender(a);
					}
				} catch (IOException e) {
					logger.warn("Unable to add test class appender", e);
				}

			}
		}
		
		@Override
		public void afterInvocation(IInvokedMethod method, ITestResult result) {
			if ( method.isTestMethod() ) {
				Appender a = null;
				String key = getKey(method.getTestMethod().getMethod());
				if ( appenders.containsKey(key) ) {
					a = appenders.get(key);
					appenders.remove(key);
				}
				if ( a != null ) {
					openqaLogger.removeAppender(a);
					frameworkLogger.removeAppender(a);
					projectsLogger.removeAppender(a);
					a.close();
					a = null;
				}
			}
		}

	}
	
	public static class ResultListener implements ITestListener {

		private int testsTotal = 0;
		private int testsPass = 0;
		private int testsFailed = 0;
		private int testsSkipped = 0;
		private List<String> failedTests = new ArrayList<String>();
		private List<String> skippedTests = new ArrayList<String>();
		
		public ResultListener() {
		}
		
		public String getResults() {
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
		
		@Override
		public void onFinish(ITestContext context) {
		}

		@Override
		public void onStart(ITestContext context) {
		}

		@Override
		public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		}

		@Override
		public void onTestFailure(ITestResult result) {
			testsFailed++;
			failedTests.add(result.getName());
		}

		@Override
		public void onTestSkipped(ITestResult result) {
			testsSkipped++;	
			skippedTests.add(result.getName());
		}

		@Override
		public void onTestStart(ITestResult result) {
			testsTotal++;
		}

		@Override
		public void onTestSuccess(ITestResult result) {
			testsPass++;
		}
		
	}
	
	
	/**
	 * Parse command line arguments
	 * @param arguments
	 * @return
	 * @throws ParseException
	 * @throws HarnessException 
	 */
    public boolean parseArgs(String arguments[]) throws ParseException, HarnessException {

    	// Build option list
    	Options options = new Options();
        options.addOption(new Option("h", "help", false, "print usage"));
        options.addOption(new Option("l", "log4j", true, "log4j file containing log4j configuration"));
        options.addOption(new Option("j", "jarfile", true, "jarfile containing test cases"));
        options.addOption(new Option("p", "pattern", true, "class filter regex, i.e. projects.zcs.tests."));
        options.addOption(new Option("g", "groups", true, "comma separated list of groups to execute (always, sanity, smoke, full)"));
        options.addOption(new Option("v", "verbose", true, "set suite verbosity (default: "+ verbosity +")"));
        options.addOption(new Option("o", "output", true, "output foldername"));
        options.addOption(new Option("w", "working", true, "current working foldername"));
        options.addOption(new Option("c", "config", true, "dynamic setting config properties i.e browser, server, locale... ( -c 'locale=en_US,browser=firefox' "));

        options.addOption(new Option("e", "exclude", true, "exclude pattern  "));
        options.addOption(new Option("eg", "exclude groups", true, "comma separated list of groups to exclude when execute (skip)"));
        
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
	        	Matcher m = Pattern.compile("projects.(.*).tests.*").matcher(filter);
	        	if (m.find()){
	        		if(m.group(1).equalsIgnoreCase("zcs"))
	        			ZimbraSeleniumProperties.setAppType(AppType.AJAX);
	        		else
	        			ZimbraSeleniumProperties.setAppType(Enum.valueOf(AppType.class, m.group(1).toUpperCase()));	        			 	
	        	}
	        }

	        if ( cmd.hasOption('e') ) {	        	
	        	if (cmd.getOptionValue('e').length() >0) { 
	        	   this.excludefilter = cmd.getOptionValue('e');	
	        	}
	        }

	        //'o' check should be after 'p' check to avoid code redundancy
	        if ( cmd.hasOption('o') ) {
	        	 this.testoutputfoldername = cmd.getOptionValue('o');
	        } else {
	             this.testoutputfoldername = 
	       		 ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot")+"\\"  +
			     ZimbraSeleniumProperties.zimbraGetVersionString() + "\\"  + 
	             ZimbraSeleniumProperties.getAppType() +"\\" + 
	             ZimbraSeleniumProperties.getStringProperty("browser") + "\\" +  
	             ZimbraSeleniumProperties.getStringProperty("locale") ;  		
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
	       	        
	        if ( cmd.hasOption('g') ) {
	        	// Remove spaces and split on commas
	        	String[] values = cmd.getOptionValue('g').replaceAll("\\s+", "").split(",");
	        	this.groups = Arrays.asList(values);
	        }
	      
	        if ( cmd.hasOption("eg") ) {
	        	// Remove spaces and split on commas
	        	String[] values = cmd.getOptionValue("eg").replaceAll("\\s+", "").split(",");
	        	this.excludeGroups = Arrays.asList(values);
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
			SeleniumService.getInstance().setUserExtensions(new File(ZimbraSeleniumProperties.getBaseDirectory() + "/src/java/framework/lib/user-extensions.js"));

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
        ps.close();
	}

}
