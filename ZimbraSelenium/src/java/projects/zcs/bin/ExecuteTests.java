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
	public static String WorkingDirectory = ".";

	/**
	 * Use this to debug a particular testMethod
	 */
	private static void debugSuite() {
		includedGrps = "always,smoke";// run 'enabled'(and 'always') tests
		suiteName = "debugSuite";
		// ---------------------------------------------------
		testName = "Message Action";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.mail.messageactions.MessageRightClickMenuTests");
		cls
				.add("projects.zcs.tests.mail.messageactions.MultipleMessageActionTests");
		addTests(testName, cls);
	}

	/**
	 * Add any new class with test methods here..
	 */
	private static void fullSuite() {
		includedGrps = "always,smoke";
		suiteName = "fullSuite";
		// ------------- MAIL --------------------------------
		// ---------------------------------------------------
		testName = "Mail Compose";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.compose.ComposeReplyFwdInHTMLTests");
		cls
				.add("projects.zcs.tests.mail.compose.ComposeReplyFwdInPlainTextTests");
		cls.add("projects.zcs.tests.mail.compose.SendBtnNegativeTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Drafts";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.compose.DraftTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Attachments";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.compose.AttachmentTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Tag Message";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.tags.TagMessageTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Message Actions";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.mail.messageactions.MessageRightClickMenuTests");
		cls
				.add("projects.zcs.tests.mail.messageactions.MultipleMessageActionTests");
		cls
				.add("projects.zcs.tests.mail.messageactions.AddRemoveAttachmentAndAddToBriefcaseTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Read Receipt";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.readreceipt.ReadReceiptTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Reading Pane";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.readingpane.ReadingPaneTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Spell Check";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.spellcheck.SpellCheckTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "New Window";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.newwindow.NewWindowTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Mail Folders";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.folders.MailFolderTests");
		cls.add("projects.zcs.tests.mail.folders.RssFeedFolderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Mail Folder Sharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.sharing.MailSharingTests");
		cls.add("projects.zcs.tests.mail.sharing.ModifySharedFolderToDiffRole");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "AutoComplete In Mail Compose";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.mail.autocomplete.MailAutoCompleteAddressTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Mail Saved Searches";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.savedsearches.MailSavedSearchTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Mail Print";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.print.PrintMessageTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Mail Tab Order";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.taborder.MailTabOrderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Mail Bugs";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.mail.MailBugTests");
		addTests(testName, cls);

		// ------------ ADDRESS BOOK -------------------------
		// ---------------------------------------------------
		testName = "Address Book";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.addressbook.newcontact.BasicContactTests");
		cls.add("projects.zcs.tests.addressbook.newcontact.CreateContactTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Address Book Actions";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.addressbook.contactactions.ContactActionTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Address Book Folders";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.addressbook.folders.AddressBookFolderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Contact Groups";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.addressbook.contactgroups.ContactGroupTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Tag Contact";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.addressbook.tags.TagContactTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Address Book Folder Sharing";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.addressbook.sharing.AddressBookSharingTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Address Book Saved Searches";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.addressbook.savedsearches.AddressBookSavedSearchTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Address Book Print";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.addressbook.print.PrintAddressBookTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Address Book Tab Order";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.addressbook.taborder.AddressBookTabOrderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Address Book Bugs";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.addressbook.AddressBookBugTests");
		addTests(testName, cls);

		// ------------ CALENDAR -----------------------------
		// ---------------------------------------------------
		testName = "New Appointment";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.calendar.newappt.CreateApptTests");
		cls.add("projects.zcs.tests.calendar.newappt.ApptInviteTests");
		cls
				.add("projects.zcs.tests.calendar.newappt.ApptRSRPAndNotificationTests");
		cls
				.add("projects.zcs.tests.calendar.newappt.CalendarQuickAddAndFishEyeViewTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Attendees Resources Autocomplete & Search";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.calendar.attendeesresource.AttendeesResourcesAutocompleteAndSearchTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Calendar Appointment Actions";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.calendar.apptactions.AppointmentActionTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Calendar Folders";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.calendar.folders.CalendarFolderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Tag Appointment";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.calendar.tags.TagAppointmentTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Calendar Folder Sharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.calendar.sharing.CalendarSharingTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Calendar Saved Searches";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.calendar.savedsearches.CalendarSavedSearchTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Calendar Print";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.calendar.print.PrintCalendarTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Calendar Tab Order";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.calendar.taborder.CalendarTabOrderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Calendar Bugs";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.calendar.CalendarBugTests");
		addTests(testName, cls);

		// ---------------- TASKS ----------------------------
		// ---------------------------------------------------
		testName = "Tasks";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.newtask.BasicTaskTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Task Actions";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.taskactions.TaskActionTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Task Folders";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.folders.TaskFolderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Tag Task";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.tags.TagTaskTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Task Folder Sharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.sharing.TaskSharingTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Task Saved Searches";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.savedsearches.TaskSavedSearchTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Task Print";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.print.PrintTaskTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Task Tab Order";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.taborder.TaskTabOrderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Tasks Bugs";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.tasks.TaskBugTests");
		addTests(testName, cls);

		// ---------------- DOCUMENTS ------------------------
		// ---------------------------------------------------
		testName = "Documents";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.documents.newpage.BasicDocumentTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Document Page Actions";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.documents.pageactions.PageActionTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Document Folders";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.documents.folders.DocumentFolderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Tag Document Page";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.documents.tags.TagDocumentPageTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Document Folder Sharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.documents.sharing.DocumentSharingTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Document Saved Searches";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.documents.savedsearches.DocumentSavedSearchTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Document Tab Order";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.documents.taborder.DocumentTabOrderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Document Print";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.documents.print.PrintDocumentTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Document Bugs";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.documents.DocumentBugTests");
		addTests(testName, cls);

		// ---------------- BRIEFCASE ------------------------
		// ---------------------------------------------------
		testName = "Briefcase";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.briefcase.newfile.BasicBriefcaseTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Briefcase File Actions";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.briefcase.fileactions.FileActionTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Briefcase Folders";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.briefcase.folders.BriefcaseFolderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Tag Briefcase File";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.briefcase.tags.TagBriefcaseFileTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Briefcase Folder Sharing";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.briefcase.sharing.BriefcaseSharingTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Briefcase Saved Searches";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.briefcase.savedsearches.BriefcaseSavedSearchTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Briefcase Tab Order";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.briefcase.taborder.BriefcaseTabOrderTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Briefcase Bugs";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.briefcase.BriefcaseBugTests");
		addTests(testName, cls);

		// --------------- PREFERENCES -----------------------
		// ---------------------------------------------------
		testName = "General Preferences";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.preferences.general.GeneralPreferencesSetTrueTests");
		cls
				.add("projects.zcs.tests.preferences.general.GeneralPreferencesSetFalseTests");
		cls.add("projects.zcs.tests.preferences.general.ChangePasswordTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Skin Preferences";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.general.VerifyAllSkinUI");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Mail Preferences";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.mail.MailPreferencesTestsUI");
		cls
				.add("projects.zcs.tests.preferences.mail.MailPreferencesSetTrueTest");
		cls
				.add("projects.zcs.tests.preferences.mail.MailPreferencesSetFalseTest");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Composing Preferences";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.preferences.composing.ComposePreferencesTestsUI");
		cls
				.add("projects.zcs.tests.preferences.composing.ComposePreferencesSetTrueTest");
		cls
				.add("projects.zcs.tests.preferences.composing.ComposePreferencesSetFalseTest");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Signatures";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.preferences.signatures.BasicSignatureTests");
		cls
				.add("projects.zcs.tests.preferences.signatures.ChangeMailFormatSignatureTests");
		cls
				.add("projects.zcs.tests.preferences.signatures.AboveBelowSignatureTests");
		cls.add("projects.zcs.tests.preferences.signatures.SignatureBugTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Accounts Preferences";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.accounts.AccountsPref");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Filters";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.filters.FilterTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "AddressBook Preferences";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.addressbook.ABPreferences");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Calendar Preferences";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.preferences.calendar.CalendarPreferencesSetTrueTest");
		cls
				.add("projects.zcs.tests.preferences.calendar.CalendarPreferencesSetFalseTest");
		cls
				.add("projects.zcs.tests.preferences.calendar.CalendarPreferencesTestsUI");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Sharing Preferences";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.preferences.sharing.SharingPreferencesTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Import Export";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.preferences.importexport.AccountImportExportTest");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Shortcuts";
		cls = new ArrayList<String>();
		// cls.add("projects.zcs.tests.preferences.shortcuts.ShortcutsCustom");
		cls.add("projects.zcs.tests.preferences.shortcuts.ShortcutsGeneral");
		cls.add("projects.zcs.tests.preferences.shortcuts.ShortcutsMail");
		cls.add("projects.zcs.tests.preferences.shortcuts.ShortcutBugTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Zimlets Preferences";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.zimlets.ZimletPreferenceTests");
		addTests(testName, cls);

		// ---------------------------------------------------
		testName = "Preferences Bugs";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.preferences.PreferencesBugTests");
		addTests(testName, cls);

		// ---------------- SEARCH ---------------------------
		// ---------------------------------------------------
		testName = "Search";
		cls = new ArrayList<String>();
		cls
				.add("projects.zcs.tests.search.SearchBarIsInColonAutoCompleteAndSearchTests");
		cls.add("projects.zcs.tests.search.SearchBugTests");
		addTests(testName, cls);

		// ---------------- FEATURES -------------------------
		// ---------------------------------------------------
		testName = "Features";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.features.ClientURLTests");
		cls.add("projects.zcs.tests.features.FamilyMailboxTests");
		cls.add("projects.zcs.tests.features.DeepLinkTests");
		addTests(testName, cls);

		// ---------------- ZCSCOMMON -------------------------
		// ---------------------------------------------------
		testName = "Common Features Acorss All Application Tab";
		cls = new ArrayList<String>();
		cls.add("projects.zcs.tests.zcscommon.NewDropdownMenuTests");
		addTests(testName, cls);
	}

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
		conf = new PropertiesConfiguration(WorkingDirectory + "/conf/config.properties");
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
		File file = new File(WorkingDirectory + "/conf/skipTests.txt");

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
