package projects.zcs.bin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.*;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import framework.core.SelNGBase;
import framework.util.MakeResultUTF8;
import framework.util.SendEmail;
import framework.util.SkipTestClass;
import framework.util.SummaryReporter;
import framework.util.TestStatusReporter;

/**
 * Programatically runs full or debug testng suite. Reads information to
 * automatically configure things to be ignored based on language, bug etc Read
 * the skipTests.text for more info
 * 
 * @author Raja Rao
 */
public class ExecuteTests {
	private static String appType = "AJAX";// note this should match
	// selngbase.appType
	private static String suiteName = "debugSuite";
	private static XmlSuite suite = new XmlSuite();
	private static ArrayList<String> cls = new ArrayList<String>();
	private static ArrayList<SkipTestClass> skipClasses = new ArrayList<SkipTestClass>();
	private static String testName = "";
	private static String includedGrps = "";
	private static String testoutputfolder = "test-output";
	private static String locale = "en_US";
	private static String browser = "";
	private static String skippedTableRows = "";
	private static Configuration conf;

	/**
	 * Use this to debug a particular testMethod
	 */
	private static void debugSuite() {
		includedGrps = "always,test";// run 'enabled'(and 'always') tests
		suiteName = "debugSuite";
		// ---------------------------------------------------
		testName = "ABSharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.familymailbox.FamilyMailboxTest");
		// cls.add("projects.zcs.tests.addressbook.AddressBookTest");

		addTests(testName, cls);
		// ---------------------------------------------------
	}

	/**
	 * Add any new class with test methods here..
	 */
	private static void fullSuite() {
		includedGrps = "always,smoke";
		suiteName = "fullSuite";
		// ---------------------------------------------------
		testName = "General(Mail)";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.compose.ComposeReplyFwdInHTMLTest");
		cls.add("projects.zcs.tests.compose.ComposeReplyFwdInPlainText");
		cls.add("projects.zcs.tests.compose.SendBtnNegativeTest");
		cls.add("projects.zcs.tests.mail.NewDropdownMenuTests");
		cls.add("projects.zcs.tests.mail.MailFolderTests");
		cls.add("projects.zcs.tests.mail.MailRightClickMenuTests");
		cls.add("projects.zcs.tests.mail.MultipleMailActionTests");
		cls.add("projects.zcs.tests.mail.ReadReceiptTests");
		cls.add("projects.zcs.tests.mail.NewWindowTests");
		cls.add("projects.zcs.tests.mail.ReadingPaneTests");
		cls.add("projects.zcs.tests.mail.MiscMailTests");
		cls.add("projects.zcs.tests.mail.SpellCheckTests");

		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "DraftsTests";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.compose.DraftsTest");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "AttachmentsTests";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.compose.AttachmentsTest");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "Calendar";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.calendar.CalendarFolderTests");
		cls.add("projects.zcs.tests.calendar.CreateApptTests");
		cls.add("projects.zcs.tests.calendar.ApptInviteTests");
		cls.add("projects.zcs.tests.calendar.ApptRSRPAndNotificationTests");
		cls
				.add("projects.zcs.tests.calendar.CalendarQuickAddAndFishEyeViewTests");
		cls.add("projects.zcs.tests.calendar.CalendarMiscTests");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "Document";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.document.DocumentTest");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "AddressBook";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.addressbook.AddressBookTest");
		cls.add("projects.zcs.tests.addressbook.ABMiscTest");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "Briefcase";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.briefcase.BriefcaseTests");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "Tasks";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.Tasks");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ----------------------- SEARCH & AUTOCOMPLETE --------------
		testName = "AutoComplete Tests";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.searchandautocomplete.AutoCompleteAddressTests");
		cls
				.add("projects.zcs.tests.searchandautocomplete.IsInColonAutoCompleteAndSearchTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ------------------------SHARING---------------------
		// ---------------------------------------------------
		testName = "MailSharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.sharing.MailSharingTests");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "FolderSharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.sharing.ModifySharedFolderToDiffRole");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "ABSharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.sharing.ABSharingTest");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "CalendarSharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.sharing.CalendarSharingTests");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "TasksSharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.sharing.TasksSharingTests");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "DocumentsSharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.sharing.DocumentSharing");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "BriefcaseSharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.sharing.BriefcaseSharingTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		//--------------------------------------------------PREFERENCES----------
		// -----------------------------------------

		// ---------------------------------------------------
		testName = "GeneralPref";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.preferences.GeneralPreferencesSetTrueTests");
		cls
				.add("projects.zcs.tests.preferences.GeneralPreferencesSetFalseTests");
		cls.add("projects.zcs.tests.preferences.GeneralPreferencesUI");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "MailPref";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.MailPreferencesTestsUI");
		cls.add("projects.zcs.tests.preferences.MailPreferencesSetTrueTest");
		cls.add("projects.zcs.tests.preferences.MailPreferencesSetFalseTest");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "ComposePref";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.ComposePreferencesTestsUI");
		cls.add("projects.zcs.tests.preferences.ComposePreferencesSetTrueTest");
		cls
				.add("projects.zcs.tests.preferences.ComposePreferencesSetFalseTest");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "CalendarPref";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.preferences.CalendarPreferencesSetTrueTest");
		cls
				.add("projects.zcs.tests.preferences.CalendarPreferencesSetFalseTest");
		cls.add("projects.zcs.tests.preferences.CalendarPreferencesTestsUI");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "AccountsPref";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.AccountsPref");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "Signatures";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.SignatureTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "ABPref";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.ABPreferences");
		addTests(testName, cls);
		// ---------------------------------------------------
		// ---------------------------------------------------
		testName = "FilterPref";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.FilterTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "SharingPref";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.SharingPreferencesTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "ShortCutsPref";
		cls = new ArrayList<String>();
		// cls.add("projects.zcs.tests.preferences.ShortcutsCustom");
		cls.add("projects.zcs.tests.preferences.ShortcutsGeneral");
		cls.add("projects.zcs.tests.preferences.ShortcutsMail");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "ZimletsPref";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.ZimletPreferenceTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "All Skins";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.VerifyAllSkinUI");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "Others";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.others.CustomLoginTest");
		cls.add("projects.zcs.tests.others.ClientURLTests");
		cls.add("projects.zcs.tests.others.RssFeedFolderTests");
		cls.add("projects.zcs.tests.others.lmtpInjectTests");
		cls.add("projects.zcs.tests.others.MiscBugTests");
		addTests(testName, cls);
		// ---------------------------------------------------

		// ---------------------------------------------------
		testName = "Family Mailboxes";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.familymailbox.FamilyMailboxTest");
		addTests(testName, cls);
		// ---------------------------------------------------
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	public static void main(String[] args) throws Exception {

		loadConfig();
		loadSkipTestsDetails();

		// first argument can be suite name fullSuite|debugSuite
		if (args.length == 1) {
			suiteName = args[0];
		} else {
			throw new Exception("Pass suiteName: <[fullSuite|debugSuite]>");
		}

		suite.setName(suiteName);
		suite.setVerbose(10);
		// store suitename. used to ignore posting to report server and also to
		// avoid trying to get zimbraVersion during startserver
		SelNGBase.suiteName = suiteName;

		// load appropriate suite
		if (suiteName.equals("fullSuite"))
			fullSuite();
		else if (suiteName.equals("debugSuite"))
			debugSuite();

		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		suites.add(suite);
		System.out.println(suite.toXml());
		SummaryReporter createSummary = new SummaryReporter(conf, appType);
		TestStatusReporter testReporter = new TestStatusReporter();
		TestNG tng = new TestNG();
		tng.setXmlSuites(suites);
		tng.addListener(createSummary);
		tng.addListener(testReporter);
		tng.setOutputDirectory(testoutputfolder);
		tng.run();

		// convert result to utf8
		MakeResultUTF8.makeUTF8(testoutputfolder, getSkippedTestsTable());

		// send email if fullsuite...
		if (suiteName.equals("fullSuite")) {
			copyCommandLineOutputFile();
			sendEmail();
		}
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
			FileChannel srcChannel = new FileInputStream(conf
					.getString("ZimbraLogRoot")
					+ "/testresult.txt").getChannel();

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

	public static void loadConfig() throws ConfigurationException {
		conf = new PropertiesConfiguration("conf/config.properties");
		locale = conf.getString("locale");
		testoutputfolder = conf.getString("ZimbraLogRoot") + "/" + appType;
		browser = conf.getString("browser");
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
			c = addExcludeMethodsInfo(c);// check if any methods needs to be
			// excluded
			classes.add(c);
		}
		test.setXmlClasses(classes);

	}

	public static void loadSkipTestsDetails() throws Exception {
		File file = new File("conf/skipTests.txt");

		BufferedReader br = new BufferedReader(new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF8")));

		String str = null;
		while ((str = br.readLine()) != null) {
			if (str.indexOf("#") >= 0 || str.equals(""))
				continue;
			// #className;methodName;locale;browser;bugnumber;remark
			skipClasses.add(new SkipTestClass(str));

		}

		br.close();

	}

	public static ArrayList<SkipTestClass> getSkipTestClassByName(
			String reqClass) {
		ArrayList<SkipTestClass> matchedSkipClasses = new ArrayList<SkipTestClass>();
		for (SkipTestClass el : skipClasses) {
			if (el.className.equals(reqClass))
				matchedSkipClasses.add(el);
		}
		return matchedSkipClasses;
	}

	public static XmlClass addExcludeMethodsInfo(XmlClass c) {
		String reqClass = c.getName();

		ArrayList<SkipTestClass> matchedSkipClassesList = getSkipTestClassByName(reqClass);
		ArrayList<String> matchedMethods = new ArrayList<String>();
		for (SkipTestClass s : matchedSkipClassesList) {
			boolean dontSkipBecauseOfAndCombination = false;
			if ((s.locales.indexOf(locale) >= 0)
					|| (s.browsers.indexOf(browser) >= 0)
					|| (s.locales.indexOf("all") >= 0)
					|| (s.browsers.indexOf("all") >= 0)) {

				// perform Browser AND locale test to skip
				if (s.browsers.indexOf("na") < 0
						&& s.browsers.indexOf("all") < 0
						&& s.locales.indexOf("na") < 0
						&& s.locales.indexOf("all") < 0) {
					if (s.browsers.indexOf(browser) < 0
							|| s.locales.indexOf(locale) < 0) {
						dontSkipBecauseOfAndCombination = true;// either browser
						// or locale
						// didnt match
					}
				}
				if (!dontSkipBecauseOfAndCombination) {
					matchedMethods.add(s.methodToSkip);// add the method to be
					addToSkippedTableRowsHTML(s);// add to html-email row
					logSkippedMethodsToFile(s);// add to log file
				}
			}
		}
		c.setExcludedMethods(matchedMethods);

		return c;

	}

	public static void addToSkippedTableRowsHTML(SkipTestClass s) {
		String bugLinks = createLinksFromBugNumbers(s.bugs);
		skippedTableRows = skippedTableRows + "<tr><td>" + s.className
				+ "</td><td class=\"numi\">" + s.methodToSkip.toString()
				+ "</td>" + "<td>" + s.locales + "</td><td class=\"numi\">"
				+ s.browsers + "</td>" + "<td>" + bugLinks
				+ "</td><td class=\"numi\">" + s.remark + "</td>" + "</tr>";
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

	public static String getSkippedTestsTable() {
		String str = "<h1>Dynamically Skipped TestMethods</h1><table cellspacing=0 cellpadding=0 class=\"param\">"
				+ "<tr><th>Class</th><th class=\"numi\">Methods</th><th>locales</th><th class=\"numi\">browsers</th><th>Bugs</th><th class=\"numi\">Remarks</th></tr>"
				+ skippedTableRows + "</table>";
		return str;
	}

	private static void logSkippedMethodsToFile(SkipTestClass s) {
		File retriedTestFile = new File(testoutputfolder
				+ "\\skippedMethodsDueToConfig.txt");
		String str = "Class: " + s.className + " Methods:"
				+ s.methodToSkip.toString() + " Locales:" + s.locales
				+ " Browsers:" + s.browsers + " Bugs:" + s.bugs + " Remarks:"
				+ s.remark;
		try {
			Writer output = new BufferedWriter(new FileWriter(retriedTestFile,
					true));
			output.write(str + "\n");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
