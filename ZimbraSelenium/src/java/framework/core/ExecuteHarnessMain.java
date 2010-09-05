/**
 * 
 */
package framework.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import framework.util.HarnessException;
import framework.util.SkippedTestListener;
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
	
	/**
	 * App type
	 */
	private String apptype = "AJAX";
	
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
	public String execute() throws FileNotFoundException, IOException {
		logger.info("Execute tests ...");
		ResultListener listener = null;
		
		try
		{

			// Build the class list
			classes = getClassesFromJar(new File(jarfilename), (classfilter == null ? null : Pattern.compile(classfilter)));
			
			// Build the list of XmlSuites
			List<XmlSuite> suites = getXmlSuiteList();
						
			// Create the TestNG test runner
			TestNG ng = new TestNG();
			
			// Configure the runner
			ng.setXmlSuites(suites);
			ng.addListener(new SummaryReporter(this.apptype));
			ng.addListener(new TestStatusReporter(this.apptype)); // TODO: This shouldn't throw Exception
			ng.addListener(new SkippedTestListener(new File(testoutputfoldername)));
			ng.addListener(listener = new ResultListener());
			ng.setOutputDirectory(testoutputfoldername);

			// Run!
			ng.run();
				
			// TODO: remove the email logic.  just let tms send the email.
			// email results
			copyCommandLineOutputFile();
			sendEmail();

		} catch (HarnessException e) {
			logger.error("Unable to execute tests", e);
		} catch (Exception e) {
			logger.error("Fix TestStatusReporter constructor to not throw raw Exception", e);
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

	public void sendEmail() throws Exception {
		String subject = getFileContents(testoutputfoldername + "/subject.txt");
		String body = getFileContents(testoutputfoldername + "/body.txt");
		SendEmail se = new SendEmail(subject, body);
		se.send();
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

	   public void send() throws Exception{
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
	      message.setFrom(new InternetAddress("qa-tms@zimbra.com"));
	      message.addRecipient(Message.RecipientType.TO,
	           new InternetAddress("qa-automation@zimbra.com"));

	      transport.connect();
	      transport.sendMessage(message,
	          message.getRecipients(Message.RecipientType.TO));
	      transport.close();
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
	 */
    public boolean parseArgs(String arguments[]) throws ParseException {

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
	        	if ( cmd.hasOption('p') && cmd.getOptionValue('p').contains("projects.html.tests"))
	        		this.apptype = "HTML";		        		
				
	        	this.testoutputfoldername = ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot") 
		        + "/" + this.apptype;
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
	        	String filter = cmd.getOptionValue('p');
	        	this.classfilter = filter;
	        	if ( filter.contains("projects.html.tests"))
	        		this.apptype = "HTML";		        		
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
