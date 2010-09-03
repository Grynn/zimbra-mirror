package projects.html.bin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import framework.core.SelNGBase;
import framework.util.SkippedTestListener;
import framework.util.SummaryReporter;
import framework.util.TestStatusReporter;
import framework.util.ZimbraSeleniumLogger;
import framework.util.ZimbraSeleniumProperties;

/**
 * Programatically runs testng suite
 */
public class ExecuteTests {
	private static String appType = "HTML";// note this should match
	private static String suiteName = "debugSuite";
	private static XmlSuite suite = new XmlSuite();
	private static ArrayList<String> cls = new ArrayList<String>();
	private static String testName = "";
	private static String includedGrps = "";
	private static String testoutputfolder = "test-output";
	public static String WorkingDirectory = ".";
	public static final String PACKAGE_OPT = "-p";
	public static final String CLASS_OPT = "-c";
	public static final String METHOD_OPT = "-m";
	public static final String GROUP_OPT = "-g";
	public static final String SUITE_OPT = "fullSuiteORdebugSuite";
	public static final String CONF_PROP_OPT = "-cfg";
	private static Map<String, Object> margs = new HashMap<String, Object>();

	/**
	 * Use this to debug a particular testMethod
	 */
	private static void debugSuite() {
		if (null == includedGrps)
			includedGrps = "always,parallel";
		
		suiteName = "debugSuite";

		// ---------------------------------------------------
		testName = "tasks";
		cls = new ArrayList<String>();
		//cls.add("projects.html.tests.mail.MailTagTests");
		//cls.add("projects.html.tests.compose.ComposeReplyFwdInHTMLTests");
		//cls.add("projects.html.tests.compose.DraftsTests");
		//cls.add("projects.html.tests.mail.MailFolderTests");
		//cls.add("projects.html.tests.addressbook.AddressBookTestHtml");
		//cls.add("projects.html.tests.compose.ComposeBtmToolBarTests");
		cls.add("projects.html.tests.tasks.Tasks");
		//cls.add("projects.html.tests.preferences.ComposePref");
		//cls.add("projects.html.tests.preferences.GeneralPref");
		//cls.add("projects.html.tests.preferences.ABPreference");			
		//cls.add("projects.html.tests.calendar.CalendarMiscTests");
		addTests(testName, cls);
		
        testName = "calendar";
	    cls = new ArrayList<String>();
		cls.add("projects.html.tests.calendar.CalendarMiscTests");
	    //cls.add("projects.html.tests.preferences.SignatureAndAccPref");
	    //cls.add("projects.html.tests.preferences.ComposePref");
	    //cls.add("projects.html.tests.preferences.GeneralPref");
		//cls.add("projects.html.tests.preferences.ABPreference");
	    //cls.add("projects.html.tests.tasks.Tasks");
	  	addTests(testName, cls);
	  			
	    // ---------------------------------------------------
	}

	/**
	 * Add any new class with test methods here..
	 */
	private static void fullSuite() {
		if (null == includedGrps)
			includedGrps = "always,smoke";
		suiteName = "fullSuite";

		// ---------------------------------------------------
		testName = "Compose";
		cls = new ArrayList<String>();
		cls.add("projects.html.tests.compose.ComposeReplyFwdInHTMLTests");
		cls.add("projects.html.tests.compose.ComposeReplyFwdInPlainText");
		cls.add("projects.html.tests.compose.AttachmentTests");
		cls.add("projects.html.tests.compose.DraftsTests");
		cls.add("projects.html.tests.compose.ComposeToCcBccBtnTests");
		cls.add("projects.html.tests.compose.ComposeBtmToolBarTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "Mail";
		cls = new ArrayList<String>();
		cls.add("projects.html.tests.mail.MailFolderTests");
		cls.add("projects.html.tests.mail.MailTopBtmToolBarTests");
		cls.add("projects.html.tests.mail.MailTagTests");
		cls.add("projects.html.tests.mail.MiscMailTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "AddressBook";
		cls = new ArrayList<String>();
		cls.add("projects.html.tests.addressbook.AddressBookTestHtml");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "Calendar";
		cls = new ArrayList<String>();
		cls.add("projects.html.tests.calendar.CalendarFolderTests");
		cls.add("projects.html.tests.calendar.CalendarMiscTests");
		cls.add("projects.html.tests.calendar.CreateApptTests");
		cls.add("projects.html.tests.calendar.CreateApptWithAttendeeTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "Tasks";
		cls = new ArrayList<String>();
		cls.add("projects.html.tests.tasks.Tasks");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "Preferences";
		cls = new ArrayList<String>();
		cls.add("projects.html.tests.preferences.GeneralPref");
		cls.add("projects.html.tests.preferences.MailPreferencesTests");
		cls.add("projects.html.tests.preferences.ComposePref");
		cls.add("projects.html.tests.preferences.SignatureAndAccPref");
		cls.add("projects.html.tests.preferences.ABPreference");

		addTests(testName, cls);
		// ---------------------------------------------------
	}

	/**
	 * This is used with arguments provided from command line
	 */
	private static void customSuite() throws Exception{
		suiteName = "customSuite";
		XmlTest test = new XmlTest(suite);
		List<XmlClass> lxc = new ArrayList<XmlClass>();
		if (null != margs.get(CLASS_OPT)) {
			testName = CLASS_OPT;
			test.setName(suiteName + " " + testName);
			//cls = new ArrayList<String>();
			//cls.addAll(Arrays.asList(((String) margs.get(CLASS_OPT)).split(",")));
			String [] testClassNames = ((String)margs.get(CLASS_OPT)).split(",");
			if (null == margs.get(METHOD_OPT)) {
				if (null != includedGrps)
					test.setIncludedGroups(Arrays.asList(includedGrps.split(",")));				
			}
			for (String cl : testClassNames) {
				lxc.add(new XmlClass(cl));
				}
				test.setXmlClasses(lxc);			
		}

		if (null != margs.get(METHOD_OPT)) {
			if(null != margs.get(CLASS_OPT)){
				testName = METHOD_OPT;
				test.setName(test.getName() + " " + testName);
				String[] methodnames = ((String) margs.get(METHOD_OPT)).split(",");
				List<String> str_methods = Arrays.asList(methodnames);
				List<XmlInclude> xml_methods = new ArrayList<XmlInclude>();;
				for (String s : str_methods) {
					XmlInclude inc = new XmlInclude(s);
					xml_methods.add(inc);		
				}									
				for (XmlClass xc : lxc) {
					xc.setIncludedMethods(xml_methods);				
				}	
			}else{
				usage();
				throw new Exception(METHOD_OPT + " must be used with " + CLASS_OPT);
			} 
		}
	}

	private static void usage() {
		ZimbraSeleniumLogger.setmLog(ExecuteTests.class);
		ZimbraSeleniumLogger.mLog.debug("Usage:");
		ZimbraSeleniumLogger.mLog
				.debug("[fullSuite|debugSuite]");
		/*
		ZimbraSeleniumLogger.mLog.debug("[" + PACKAGE_OPT
				+ " <full package name>] \n runs all classes in the package");
		*/
		ZimbraSeleniumLogger.mLog.debug("[" + CLASS_OPT
				+ " <comma separated fully qualified class names>]");
		ZimbraSeleniumLogger.mLog.debug("[" + METHOD_OPT
				+ " <comma separated method names>],must be used with -c option");
		ZimbraSeleniumLogger.mLog.debug("[" + GROUP_OPT
				+ " <comma separated group names>]\n");
		
		ZimbraSeleniumLogger.mLog.debug("Example 1: \n" +
				"debugSuite\n" + 
				"-g always,smoke\n" +
				"runs debugSuite with specified group\n");

		ZimbraSeleniumLogger.mLog.debug("Example 2: \n" +
			"-c projects.html.tests.calendar.CalendarMiscTests,projects.html.tests.mail.MailFolderTests\n" +
			 "runs all methods in the specified classes\n");
		
		ZimbraSeleniumLogger.mLog.debug("Example 3: \n" +
		"-c projects.html.tests.calendar.CalendarMiscTests,projects.html.tests.mail.MailFolderTests\n" +
		"-g always,debug\n" +
		"runs specified groups in the classes\n");
		
		ZimbraSeleniumLogger.mLog.debug("Example 4: \n" +
				"-c projects.html.tests.calendar.CalendarMiscTests\n" +
				"-m verifyFreeBusyView,verifyRepeatApptExceptionChangeTimezon\n" +
				"runs specified methods\n");
			}

	private static void parseCommandLine(final String[] args) throws Exception {
		for (int i = 0; i < args.length; ++i) {
			ZimbraSeleniumLogger.mLog.debug("args[" + i + "] = \"" + args[i]
					+ "\"");
		}

		for (int i = 0; i < args.length; i++) {
			if ("fullSuite".equals(args[i]) || "debugSuite".equals(args[i])) {
				margs.put(SUITE_OPT, args[i]);
			} else if (GROUP_OPT.equalsIgnoreCase(args[i])) {
				if ((i + 1) < args.length) {
					margs.put(GROUP_OPT, args[i + 1]);
					i++;
				}
			} else if (args[i].startsWith("-")) {
				if (PACKAGE_OPT.equalsIgnoreCase(args[i])) {
					if ((i + 1) < args.length) {
						margs.put(PACKAGE_OPT, args[i + 1]);
						i++;
					}
				} else if (CLASS_OPT.equalsIgnoreCase(args[i])) {
					if ((i + 1) < args.length) {
						margs.put(CLASS_OPT, args[i + 1]);
						i++;
					}
				} else if (METHOD_OPT.equalsIgnoreCase(args[i])) {
					if ((i + 1) < args.length) {
						margs.put(METHOD_OPT, args[i + 1]);
						i++;
					}
				}
			}
		}
		if (margs.isEmpty()) {
			usage();
			throw new Exception("invalid option");
		} else {
			includedGrps = (String) margs.get(GROUP_OPT);					
			if (null == margs.get(SUITE_OPT)
					&& (null != margs.get(PACKAGE_OPT)
							|| null != margs.get(CLASS_OPT) || null != margs
							.get(METHOD_OPT))) {
				customSuite();
			} else {
				if ("fullSuite".equals(margs.get(SUITE_OPT))) {
					fullSuite();
				} else if ("debugSuite".equals(margs.get(SUITE_OPT))) {
					debugSuite();
				}
			}
		}
	}

	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------

	public static void main(String[] args) throws Exception {

		loadConfig();

		if (args.length == 0) {
			usage();
			throw new Exception("Empty program arguments");
		} else {
			parseCommandLine(args);
		}

		suite.setName(suiteName);
		suite.setVerbose(2);
		//"classes","tests","methods"
		//threadPoolSize = 3
		suite.setThreadCount(4);
		suite.setParallel("none");		

		// store suitename. used to ignore posting to report server and also to
		// avoid trying to get zimbraVersion during startserver
		SelNGBase.suiteName = suiteName;

		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(suite);
		System.out.println(suite.toXml());
		SummaryReporter createSummary = new SummaryReporter(appType);
		TestStatusReporter testReporter = new TestStatusReporter(appType);
		TestNG tng = new TestNG();
		tng.setXmlSuites(suites);
		tng.addListener(createSummary);
		tng.addListener(testReporter);
		tng.addListener(new SkippedTestListener(new File(testoutputfolder)));
		tng.setOutputDirectory(testoutputfolder);
		//tng.setParallel("tests");
		//tng.setThreadCount(4);
		tng.run();

		// send email if fullsuite...
		if (suiteName.equals("fullSuite")) {
			copyCommandLineOutputFile();
			sendEmail();
		}
	}

	public static void loadConfig() {
		testoutputfolder = ZimbraSeleniumProperties.getStringProperty(
				"ZimbraLogRoot", "test-output")
				+ "/" + appType;
		createResultFolders();

	}

	public static void sendEmail() throws Exception {
		String subject = getFileContents(testoutputfolder + "/subject.txt");
		String body = getFileContents(testoutputfolder + "/body.txt");
		SendEmail se = new SendEmail(subject, body);
		se.send();
	}

	public static String getFileContents(String filePath) {
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

	public static void copyCommandLineOutputFile() {
		File f = new File(testoutputfolder + "/testresult.txt");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		try {

			// Create channel on the source
			FileChannel srcChannel = new FileInputStream(
					ZimbraSeleniumProperties.getStringProperty("ZimbraLogRoot") + "/testresult.txt"
					).getChannel();

			// Create channel on the destination
			FileChannel dstChannel = new FileOutputStream(testoutputfolder
					+ "/testresult.txt").getChannel();

			// Copy file contents from source to destination
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
			// Close the channels
			srcChannel.close();
			dstChannel.close();
		} catch (IOException e) {
		}
	}

	public static void addTests(String testName,
			ArrayList<String> testClassNames) {
		XmlTest test = new XmlTest(suite);
		List<String> includedGroups = new ArrayList<String>();
		String[] igrps = includedGrps.split(",");
		for (int j = 0; j < igrps.length; j++) {
			includedGroups.add(igrps[j]);
		}
		File f = new File(testoutputfolder + "/skippedMethodsDueToConfig.txt");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
			}

		test.setIncludedGroups(includedGroups);
		test.setName(testName);
		List<XmlClass> classes = new ArrayList<XmlClass>();
		for (String el : testClassNames) {
			XmlClass c = new XmlClass(el);
			classes.add(c);
		}
		test.setXmlClasses(classes);
	}




	public static String createLinksFromBugNumbers(String b) {
		String retStr = "";
		if (b.equals("") || b.equals("na"))
			return retStr;

		String[] temp = b.split(",");
		for (String el : temp) {
			if (retStr.equals(""))
				retStr = retStr
						+ "<a href=\"http://bugzilla.zimbra.com/show_bug.cgi?id="
						+ el + "\">" + el + "</a>";
			else
				retStr = retStr
						+ ", "
						+ "<a href=\"http://bugzilla.zimbra.com/show_bug.cgi?id="
						+ el + "\">" + el + "</a>";
		}
		return retStr;
	}



	private static void createResultFolders() {
		try {

			boolean success = (new File(testoutputfolder)).mkdirs();
			if (success) {
				System.out.println("Directories: " + testoutputfolder
						+ " created");
			}

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
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
	

}
