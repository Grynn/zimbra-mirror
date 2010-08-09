package projects.zcs.tests;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.clapper.util.text.HTMLUtil;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import projects.zcs.CoreObjects;
import projects.zcs.Locators;
import projects.zcs.PageObjects;
import projects.zcs.clients.ProvZCS;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.HarnessException;
import framework.util.ZimbraAdminAccount;
import framework.util.ZimbraUtil;

/**
 * @author Raja Rao DV
 */
@SuppressWarnings( { "static-access", "deprecation" })
public class CommonTest extends SelNGBase {

	/*
	 * protected static Button button = new Button(); protected static Folder
	 * folder = new Folder(); protected static ButtonMenu buttonMenu = new
	 * ButtonMenu(); protected static MenuItem menuItem = new MenuItem();
	 * protected static Dialog dialog = new Dialog(); protected static
	 * MessageItem messageItem = new MessageItem(); protected static Tab tab =
	 * new Tab(); protected static Editfield editField = new Editfield();
	 * protected static TextArea textAreaField = new TextArea(); protected
	 * static Editor editor = new Editor(); protected static PwdField pwdField =
	 * new PwdField(); protected static CheckBox checkbox = new CheckBox();
	 * protected static RadioBtn radioBtn = new RadioBtn();
	 * 
	 * protected static Login login = new Login(); protected static ComposeView
	 * composeView = new ComposeView(); protected static MailApp mailApp = new
	 * MailApp();
	 */
	public static ResourceBundle zmMsg;
	public static ResourceBundle zsMsg;
	public static ResourceBundle ajxMsg;
	public static ResourceBundle i18Msg;
	private Configuration conf;
	public static CoreObjects obj;
	public static PageObjects page;
	public static Locators locator;
	public static String COVERAGE_SCRIPT =
		"if (! window.jscoverage_report) {\n" +
		"  window.jscoverage_report = function jscoverage_report(dir) {\n" +
		"    if(window._$jscoverage == undefined) return \"\";\n" +
		"    var pad = function (s) {   \n" +
		"          return '0000'.substr(s.length) + s; \n" +
		"   };\n" +
		"  var quote = function (s) {   \n" +
		"   return '\"' + s.replace(/[\\u0000-\\u001f\"\\\\\\u007f-\\uffff]/g, function (c) {  \n" +
		"      switch (c) {\n" +
		"        case '\\b':\n" +
		"          return '\\\\b';\n" +
		"        case '\\f':    \n" +
		"         return '\\\\f';\n" +
		"        case '\\n': \n" +
		"         return '\\\\n'; \n" +
		"       case '\\r':\n" +
		"          return '\\\\r'; \n" +
		"       case '\\t':\n" +
		"          return '\\\\t'; \n" +
		"       case '\"':     \n" +
		"         return '\\\\\"'; \n" +
		"       case '\\\\':\n" +
		"          return '\\\\\\\\';\n" +
		"       default:   \n" +
		"              return '\\\\u' + pad(c.charCodeAt(0).toString(16));\n" +
		"        }\n" +
		"      }) + '\"';\n" +
		"    };\n" +
		"\n" +
		"    var json = [];\n" +
		"    for (var file in window._$jscoverage) { \n" +
		"     var coverage = window._$jscoverage[file];\n" +
		"      var array = []; \n" +
		"     var length = coverage.length;\n" +
		"      for (var line = 0; line < length; line++) {\n" +
		"        var value = coverage[line];       \n" +
		"    if (value === undefined || value === null) {\n" +
		"          value = 'null';    \n" +
		"    }else{\n" +
		"          coverage[line] = 0; //stops double counting\n" +
		"        }\n" +
		"        array.push(value);}\n" +			
		"      json.push(quote(file) + ':{\"coverage\":[' + array.join(',') + ']}');    } \n" +
		"   json = '{' + json.join(',') + '}';\n" +
		"    return json;\n" +
		"  };\n" +
		"}; \n" +
		"window.jscoverage_report()\n";

	protected static Map<String, Object> selfAccountAttrs = new HashMap<String, Object>();
	public static String ZimbraVersion = "";

	public CommonTest() {
		try {
			this.conf = new PropertiesConfiguration("conf/config.properties");
			zmMsg = ResourceBundle.getBundle("framework.locale.ZmMsg",
					new Locale(this.conf.getString("locale")));
			ajxMsg = ResourceBundle.getBundle("framework.locale.AjxMsg",
					new Locale(this.conf.getString("locale")));
			i18Msg = ResourceBundle.getBundle("framework.locale.I18nMsg",
					new Locale(this.conf.getString("locale")));
			zsMsg = ResourceBundle.getBundle("framework.locale.ZsMsg",
					new Locale(this.conf.getString("locale")));

			obj = new CoreObjects();
			page = new PageObjects();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void resetSession() throws Exception {
		// reset all the selngbase settings, since they might have been set to
		// true by the failing test
		SelNGBase.labelStartsWith = false;
		SelNGBase.fieldLabelIsAnObject = false;
		SelNGBase.actOnLabel = false;
		SelNGBase.stopSeleniumSession();
	}

	public static void zLoginIfRequired() throws Exception {
		// set retry to false so that newtests and dependsOn methods would work
		// like fresh-test
		// isExecutionARetry = false;
		Map<String, Object> accntAttrs = new HashMap<String, Object>();
		zLoginIfRequired(accntAttrs);
	}

	public static void zLoginIfRequired(Map<String, Object> accntAttrs)
			throws Exception {
		if (needsReLogin(accntAttrs) || needReset) {

			resetSession();
			selfAccountAttrs = accntAttrs;
			selfAccountName = page.zLoginpage.zLoginToZimbraAjax(accntAttrs);
		}
	}

	private static boolean needsReLogin(Map<String, Object> accntAttrs) {
		int currentAccntAttrsSize = selfAccountAttrs.size() - 4;// -4 is to
		// remove default settings
		// none has logged in yet
		if (selfAccountName.equals(""))
			return true;
		// a user has already logged in with default settings
		// and test needs to use default-settings as well.
		if (currentAccntAttrsSize == 0 && accntAttrs.size() == 0)
			return false;
		// we have a default user, but need user with some prefs(s)
		// or, we have user with some pref, but need default user
		if ((currentAccntAttrsSize == 0 && accntAttrs.size() > 0)
				|| (selfAccountAttrs.size() > 0) && (accntAttrs.size() == 0))
			return true;

		if ((currentAccntAttrsSize > 0) && (accntAttrs.size() > 0)) {
			Iterator<String> keys = accntAttrs.keySet().iterator();
			while (keys.hasNext()) {
				String reqkey = keys.next();
				// if the key doesnt exist return true
				if (!selfAccountAttrs.containsKey(reqkey)) {
					return true;
				}
				// if the value doesnt match return true
				String key1 = selfAccountAttrs.get(reqkey).toString();
				String key2 = accntAttrs.get(reqkey).toString();
				if (!key1.equals(key2)) {
					return true;
				}
			}

		}

		return false;
	}

	@BeforeSuite(groups = { "always" })
	public void initTests() throws Exception {
		initFramework();
		ProvZCS.setupZCSTestBed();
		super.startSeleniumServer();
		ProvZCS.createAccount("ccuser@testdomain.com");
		ProvZCS.createAccount("bccuser@testdomain.com");
		if (!SelNGBase.suiteName.equals("debugSuite")) {
			ZimbraVersion = zGetZimbraVersionFromAjax();
			SelNGBase.ZimbraVersion = ZimbraVersion;
			CmdExec("taskkill /f /t /im iexplore.exe");
			CmdExec("taskkill /f /t /im firefox.exe");
			CmdExec("taskkill /f /t /im Safari.exe");
			CmdExec("taskkill /f /t /im chrome.exe");
		}
	BufferedWriter out = new BufferedWriter(new FileWriter("test-output\\CODECOVERAGE\\coveredClasses.txt"));
    out.close();
	}

	public static void writeCoverage() throws Exception {
		System.out.println("<=======><=======><=== Writing Coverage to json file ===><=======><=======>");
		BufferedWriter out = new BufferedWriter(new FileWriter("test-output\\CODECOVERAGE\\jscoverage.json"));
		Set<String> keys = FILENAME_TO_COVERAGE.keySet();
		Iterator itr = keys.iterator();
		String jsonString = "";
		while(itr.hasNext()) {
			String key = (String) itr.next();
			jsonString = jsonString + "\"" + key + "\"" + ":{\"coverage\":" + FILENAME_TO_COVERAGE.get(key) + ",\"source\":" + FILENAME_TO_SOURCE.get(key) + "},";
		}
		out.write("{" + jsonString + "}");
	    out.close();
	}
	
	
	@AfterSuite(groups = { "always" })
	public void cleanup() throws Exception {
		super.stopSeleniumServer();
	}
	
	@AfterClass(groups = { "always" })
	public void stopSession() throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter("test-output\\CODECOVERAGE\\coveredClasses.txt", true));
		out.write(this.getClass().toString() + "\n");
	    out.close();
		System.out.println("Executing AfterClass For " + this.getClass().toString());
		if(config.containsKey("runCodeCoverage") && config.getString("runCodeCoverage").equalsIgnoreCase("yes")) {
			writeCoverage();
		}
		selenium.stop();
	}
	
	@AfterMethod(groups = { "always" })
	public void calculateCoverageIfRequired() throws Exception {
		if(config.containsKey("runCodeCoverage") && config.getString("runCodeCoverage").equalsIgnoreCase("yes")) {
			calculateCoverage();
		}
	}	


	/**
	 * Gets the zimbra version string from GetVersionInfoRequest (e.g. 6.0.7_GA_2470.UBUNTU8.NETWORK)
	 * 
	 * @return Zimbra Version
	 * @throws HarnessException
	 */
	private String zGetZimbraVersionFromAjax() throws HarnessException {
		
		ZimbraAdminAccount.GlobalAdmin().soapSend("<GetVersionInfoRequest xmlns='urn:zimbraAdmin'/>");
		String version = ZimbraAdminAccount.GlobalAdmin().soapSelectValue("//admin:info", "version");
		if ( version == null )
			throw new HarnessException("Unable to determine version from GetVersionInfoResponse "+ ZimbraAdminAccount.GlobalAdmin().soapLastResponse());
		
		// The version string looks like 6.0.7_GA_2470.UBUNTU8.NETWORK
		return (version);
	}


	public void initFramework() {
		super.initFramework(this.conf);
		zmMsg = ResourceBundle.getBundle("framework.locale.ZmMsg", new Locale(
				conf.getString("locale")));

	}

	public static String localize(String locatorKey) {
		String key = locatorKey.split("::")[0];
		String prependthis = "";
		if (key.indexOf("link=") >= 0) {
			key = key.replace("link=", "");
			prependthis = "link=";
		}
		// dont localize if the locatorKey
		if (key.indexOf("=") > 0)
			return key;

		// else.. it must be a label, so localize...

		if (key.equals("ok") || key.equals("cancel"))// bug(zmmsg is different
			// from ajxmsg)
			return prependthis + HTMLUtil.stripHTMLTags(ajxMsg.getString(key));
		try {
			return prependthis + HTMLUtil.stripHTMLTags(zmMsg.getString(key));
		} catch (MissingResourceException e) {
			try {
				return prependthis
						+ HTMLUtil.stripHTMLTags(ajxMsg.getString(key));
			} catch (MissingResourceException e1) {
				try {
					return prependthis
							+ HTMLUtil.stripHTMLTags(i18Msg.getString(key));
				} catch (MissingResourceException e2) {
					return prependthis
							+ HTMLUtil.stripHTMLTags(zsMsg.getString(key));
				}
			}
		}

	}

	public static String localize(String key, String zeroValue, String oneValue) {
		String loc = localize(key);
		if (zeroValue != "")
			loc = loc.replace("{0}", zeroValue);
		if (oneValue != "")
			loc.replace("{1}", oneValue);
		return loc;
	}

	public static String localize(String key, String zeroValue,
			String oneValue, String twoValue) {
		String loc = localize(key);
		if (zeroValue != "" && oneValue != "" && twoValue != "")
			loc = loc.replace("{0}", zeroValue).replace("{1}", oneValue)
					.replace("{2}", twoValue);
		return loc;
	}

	//   
	/**
	 * Returns localized version of import toast message
	 * 
	 * @param key
	 *            localize key
	 * @param itemType
	 *            CONTACTS or CALENDAR(yet to implement)
	 * @param numberOfItemsImported
	 *            number of items value Usage: String str =
	 *            CommonTest.localizeChoiceMsgs("contactsImportedResult",
	 *            "CONTACTS", 10);
	 * @return returns localized version of "10 Contacts imported" string in
	 *         English
	 */
	public static String localizeChoiceMsgs(String key, String itemType,
			int numberOfItemsImported) {
		String loc = localize(key);
		String tmp[] = loc.split("\\{*}");
		tmp[2] = tmp[2].trim();
		int numLoc = 0;
		int typLoc = 0;
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].indexOf("number") > 0)
				numLoc = i;
			else if (tmp[i].indexOf("choice") > 0)
				typLoc = i;
		}

		if (numberOfItemsImported != 1 && itemType.equals("CONTACTS")) {
			tmp[typLoc] = localize("contacts");
			tmp[numLoc] = "" + numberOfItemsImported;
		} else if (numberOfItemsImported == 1 && itemType.equals("CONTACTS")) {
			tmp[typLoc] = localize("contact");
			tmp[numLoc] = "" + numberOfItemsImported;
		}
		String val = "";
		for (int i = 0; i < tmp.length; i++) {
			val = val + " " + tmp[i];

		}
		return val;
	}

	public static String getLocalizedData(int numberofkeys) {
		String[] keysArray = { "saveDraft", "saveDraftTooltip", "savedSearch",
				"savedSearches", "saveIn", "savePrefs", "saveSearch",
				"saveSearchTooltip", "saveToSent", "saveToSentNOT", "schedule",
				"search", "searchAll", "searchAppts", "searchBuilder",
				"searchByAttachment", "searchByBasic", "searchByCustom",
				"searchByDate", "searchByDomain", "searchByFlag",
				"searchByFolder", "searchBySavedSearch", "searchBySize",
				"searchByTag", "searchByTime", "searchByZimlet",
				"searchCalendar", "searchContacts", "whenSentToError",
				"whenSentToHint", "whenInFolderError", "whenInFolderHint",
				"whenReplyingToAddress", "whenReplyingToFolder",
				"sendNoMailAboutShare", "sendUpdateTitle", "sendUpdatesNew",
				"sendUpdatesAll", "sendStandardMailAboutShare",
				"sendStandardMailAboutSharePlusNote", "sendPageTT",
				"sendTooltip" };
		Random r = new Random();
		String output = "";
		for (int i = 0; i < numberofkeys; i++) {
			int randint = r.nextInt(keysArray.length);
			output = output + localize(keysArray[randint]).replace("\"", "");

		}
		return output;
	}

	/**
	 * Returns a 8-char length word with random-characters that are localized.
	 * Also, the returned word is special-char or space free.
	 * 
	 * @return "tesxsdfe"
	 */
	public static String getLocalizedData_NoSpecialChar() {
		String str = localize("whenReplyingToAddress");
		str = str + localize("editNotebookIndex");
		str = str + localize("invitees");
		str = str + localize("subject");
		str = str + localize("searchCalendar");
		str = str + localize("goToMail");
		str = str + localize("tagItem");
		str = str + localize("uploadImage");
		str = str + localize("createNewAppt");
		str = str + localize("tasksFolder");
		str = str + localize("imSortListByName");
		str = str + localize("imPrefInstantNotify");
		str = str + localize("signatureAttachLabel");
		str = str + localize("wikletFragment");
		str = str + localize("imPrefAutoLogin");
		str = str + localize("wikletModifyTimeTT");
		str = str + localize("searchForMessages");
		str = str + localize("percent");
		str = str + localize("noMisspellingsFound");
		str = str + localize("sendNoMailAboutShare");
		str = str + localize("splitCell");
		str = str + localize("chooseAddrBookToImport");
		str = str + localize("insertImage");
		str = str + localize("declineShare");
		str = str + localize("participantStatus");
		str = str + localize("addressBookLabel");
		str = str + localize("multipartMixed");
		str = str + localize("previousWeek");
		str = str + localize("byMessage");
		str = str + localize("company");
		str = str + localize("hideDetails");
		str = str + localize("applicationDocument");
		str = str + localize("conversation");
		str = str + localize("ownerLabel");
		str = str + localize("attendeesLabel");
		str = str + localize("running");
		str = str + localize("preferences");
		str = str + localize("chooseFolder");
		str = str + localize("mountNotebook");
		str = str + localize("matches");
		str = str + localize("quota");
		str = str + localize("active");
		str = str + localize("convertCamelCase");
		str = str + localize("sharing");
		str = str.replace(" ", "");
		str = str.replace(".", "");
		str = str.replace(":", "");
		Random r = new Random();
		int max = str.length() - 8;
		int randInt = r.nextInt(max);
		return str.substring(randInt, randInt + 8);

	}

	public static String getOnlyEnglishAlphabetCharAndNumber() {
		String str = "0WhenReplyingToAddresS1";
		str = str + "2EditNotebookIndeX3";
		str = str + "4InviteeS5";
		str = str + "6SubjecT7";
		str = str + "8SearchCalendaR9";
		str = str + "0GoToMaiL1";
		str = str + "2TagIteM3";
		str = str.replace(" ", "");
		str = str.replace(".", "");
		str = str.replace(":", "");
		Random r = new Random();
		int max = str.length() - 1;
		int randInt = r.nextInt(max);
		return str.substring(randInt, randInt + 1);

	}

	public static String getTodaysDateZimbraFormat() {

		String DATE_FORMAT = "yyyyMMdd";

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		Calendar testcal = Calendar.getInstance();

		String todayDate = sdf.format(testcal.getTime());

		return todayDate;
	}

	public static void assertReport(String expectedValue, String actualValue,
			String reportSummary) throws Exception {
		if (expectedValue.equals("_selfAccountName_"))
			expectedValue = selfAccountName;
		if (actualValue.equals("_selfAccountName_"))
			actualValue = selfAccountName;
		Assert.assertTrue(expectedValue.indexOf(actualValue) >= 0,
				"Expected value(" + expectedValue + "), Actual Value("
						+ actualValue + ")");
	}

	public static String replaceUserNameInStaticId(String staticID) {
		String actualID = null;
		actualID = staticID.replace("USERNAME", SelNGBase.selfAccountName
				.toLowerCase());
		return actualID;
	}

	public void zPressBtnIfDlgExists(String dlgName, String dlgBtn,
			String folderToBeClicked) throws Exception {
		for (int i = 0; i <= 5; i++) {
			String dlgExists = obj.zDialog.zExistsDontWait(dlgName);
			if (dlgExists.equals("true")) {
				obj.zFolder.zClickInDlgByName(folderToBeClicked, dlgName);
				Thread.sleep(1000);
				obj.zButton.zClickInDlgByName(dlgBtn, dlgName);
			} else {
				Thread.sleep(500);
			}
		}
	}

	public String zNavigateAgainIfRequired(String navURL) throws Exception {
		Thread.sleep(5000);
		String btnExists = "false";
		for (int i = 0; i <= 2; i++) {
			btnExists = obj.zButton
					.zExistsDontWait(page.zLoginpage.zSearchFldr);
			if (btnExists.equals("true")) {
				break;
			} else {
				zReloginToAjax();
				selenium.open(navURL);
				Thread.sleep(5000);
			}
		}
		return btnExists;
	}

	public static void zWaitTillObjectExist(String objectType, String objectName)
			throws Exception {
		int i = 0;
		boolean found = false;
		for (i = 0; i <= 30; i++) {
			String retVal = null;
			objectType = objectType.toLowerCase();

			if (objectType.equals("button")) {
				retVal = obj.zButton.zExistsDontWait(objectName);
			} else if (objectType.equals("id")) {
				retVal = obj.zButton.zExistsDontWait("id=" + objectName);
			} else if (objectType.equals("checkbox")) {
				retVal = obj.zCheckbox.zExistsDontWait(objectName);
			} else if (objectType.equals("radiobutton")) {
				retVal = obj.zRadioBtn.zExistsDontWait(objectName);
			} else if (objectType.equals("message")) {
				retVal = obj.zMessageItem.zExistsDontWait(objectName);
			} else if (objectType.equals("menuitem")) {
				retVal = obj.zMenuItem.zExistsDontWait(objectName);
			} else if (objectType.equals("folder")) {
				retVal = obj.zFolder.zExistsDontWait(objectName);
			} else if (objectType.equals("tab")) {
				retVal = obj.zTab.zExistsDontWait(objectName);
			} else if (objectType.equals("editfield")) {
				retVal = obj.zEditField.zExistsDontWait(objectName);
			} else if (objectType.equals("textarea")) {
				retVal = obj.zTextAreaField.zExistsDontWait(objectName);
			} else if (objectType.equals("dialog")) {
				retVal = obj.zDialog.zExistsDontWait(objectName);
			} else if (objectType.equals("link")) {
				if (selenium.isElementPresent("link=" + objectName))
					retVal = "true";
				else
					retVal = "false";
			} else if (objectType.equals("element")
					|| objectType.equals("xpath")) {
				if (selenium.isElementPresent(objectName))
					retVal = "true";
				else
					retVal = "false";
			} else if (objectType.equals("class")) {
				if (selenium.isElementPresent("xpath=//td[contains(@class,'"
						+ objectName + "')]")) {
					retVal = "true";
				} else {
					retVal = "false";
				}
			} else if (objectType.equals("text")) {
				if (selenium.isTextPresent(objectName))
					retVal = "true";
				else
					retVal = "false";
			}

			if (retVal.equals("false")) {
				Thread.sleep(1000);
			} else {
				Thread.sleep(1000);
				found = true;
				break;
			}
		}
		if (!found)
			Assert.fail("Object(" + objectName
					+ ") didn't appear even after 30 seconds");
	}

	/**
	 * @param applicationtab
	 *            Either specify exact (any case either lower OR upper)
	 *            application tab name in english (for e.g. "Mail",
	 *            "Address Book", "Calendar", "Tasks", "Documents", "Briefcase",
	 *            "Preferences") OR pass corresponding localize string to click
	 *            on application tab
	 */
	public static void zGoToApplication(String applicationtab) throws Exception {
		String lCaseapplicationtab = applicationtab.toLowerCase();
		if ((lCaseapplicationtab.equals("mail"))
				|| (applicationtab.equals(localize(locator.mail)))) {
			obj.zButton.zClick(page.zMailApp.zMailTabIconBtn);
			String compose1Exists = obj.zTab
					.zExistsDontWait("id=zb__App__tab_COMPOSE1_left_icon");
			if (compose1Exists.equals("true")) {
				obj.zTab.zClick("id=zb__App__tab_COMPOSE1_left_icon");
				obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
				String warningDlgExists = obj.zDialog
						.zExistsDontWait(localize(locator.warningMsg));
				if (warningDlgExists.equals("true")) {
					obj.zButton.zClickInDlgByName(localize(locator.no),
							localize(locator.warningMsg));
				}
			}
		} else if ((lCaseapplicationtab.equals("address book"))
				|| (applicationtab.equals(localize(locator.addressBook)))) {
			obj.zButton.zClick(localize(locator.addressBook));
			zWaitTillObjectExist("folder", page.zABCompose.zContactsFolder);
		} else if ((lCaseapplicationtab.equals("calendar"))
				|| (applicationtab.equals(localize(locator.calendar)))) {
			obj.zButton.zClick(localize(locator.calendar));
			zWaitTillObjectExist("folder", page.zCalApp.zCalendarFolder);
		} else if ((lCaseapplicationtab.equals("tasks"))
				|| (applicationtab.equals(localize(locator.tasks)))) {
			obj.zButton.zClick(localize(locator.tasks));
			zWaitTillObjectExist("folder", page.zTaskApp.zTasksFolder);
		} else if ((lCaseapplicationtab.equals("im"))
				|| (applicationtab.equals(localize(locator.imAppTitle)))) {
			obj.zButton.zClick(localize(locator.imAppTitle));
		} else if ((lCaseapplicationtab.equals("documents"))
				|| (applicationtab.equals(localize(locator.documents)))) {
			obj.zButton.zClick(localize(locator.documents));
			zWaitTillObjectExist("folder", page.zDocumentApp.zNotebookFolder);
		} else if ((lCaseapplicationtab.equals("briefcase"))
				|| (applicationtab.equals(localize(locator.briefcase)))) {
			obj.zButton.zClick(localize(locator.briefcase));
			zWaitTillObjectExist("folder", page.zBriefcaseApp.zBriefcaseFolder);
		} else if ((lCaseapplicationtab.equals("preferences"))
				|| (applicationtab.equals(localize(locator.preferences)))) {
			obj.zButton.zClick(localize(locator.preferences));
			zWaitTillObjectExist("button", localize(locator.changePassword));
		}
	}

	public static void zGoToPreferences(String overviewPaneFolderName)
			throws Exception {
		overviewPaneFolderName = overviewPaneFolderName.toLowerCase();
		if (overviewPaneFolderName.equals("general")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zGeneralPrefFolder));
		} else if (overviewPaneFolderName.equals("mail")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zMailPrefFolder));
		} else if (overviewPaneFolderName.equals("signatures")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zSignaturesPrefFolder));
		} else if (overviewPaneFolderName.equals("accounts")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zAccountsPrefFolder));
		} else if (overviewPaneFolderName.equals("filters")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zFiltersPrefFolder));
		} else if (overviewPaneFolderName.equals("composing")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zComposingPrefFolder));
		} else if (overviewPaneFolderName.equals("address book")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zAddressBookPrefFolder));
		} else if (overviewPaneFolderName.equals("calendar")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zCalendarPrefFolder));
		} else if (overviewPaneFolderName.equals("sharing")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zSharingPrefFolder));
		} else if (overviewPaneFolderName.equals("import/export")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zImportExportPrefFolder));
		} else if (overviewPaneFolderName.equals("shortcuts")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zShortcutsPrefFolder));
		} else if (overviewPaneFolderName.equals("zimlets")) {
			obj.zButton
					.zClick(replaceUserNameInStaticId(page.zMailApp.zZimletsPrefFolder));
		}
	}

	public static String getNameWithoutSpace(String key) {
		if (config.getString("browser").equals("IE"))
			return key.replace("\u00a0:", "");
		else
			return key;
	}

	public static void zReloginToAjax() throws Exception {
		String accountName = selfAccountName;
		resetSession();
		Thread.sleep(1000);
		SelNGBase.selfAccountName = accountName;
		page.zLoginpage.zLoginToZimbraAjax(accountName);
	}

	public static void verifyShowOriginalMsgBody(String bodyValue, String from,
			String to, String cc, String bcc, String subject, String body)
			throws Exception {
		if (config.getString("locale").equals("en_US")) {
			assertReport(bodyValue, localize(locator.dateLabel),
					"Date: text mismatched in show original body");
			assertReport(bodyValue, localize(locator.fromLabel),
					"From: text mismatched in show original body");
			assertReport(bodyValue, localize(locator.toLabel),
					"To: text mismatched in show original body");
			assertReport(bodyValue, localize(locator.ccLabel),
					"Cc: text mismatched in show original body");
			assertReport(bodyValue, localize(locator.subjectLabel),
					"Subject: text mismatched in show original body");
		}
		assertReport(bodyValue, from,
				"From user value mismatched in show original body");
		assertReport(bodyValue, to,
				"To user value mismatched in show original body");
		assertReport(bodyValue, cc,
				"Cc user value is mismatched in show original body");
		assertReport(bodyValue, subject,
				"Subject value mismatched in show original body");
		assertReport(bodyValue, body,
				"Body value mismatched in show original body");
	}

	public void zCreateTag(String tagName) throws Exception {
		obj.zFolder.zRtClick(page.zMailApp.zTagOverViewHeader);
		Thread.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlgByName(localize(locator.tagName), tagName,
				localize(locator.createNewTag));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewTag));
		Thread.sleep(500);
	}

	public void zRenameTag(String oldTagName, String newTagName)
			throws Exception {
		obj.zFolder.zRtClick(oldTagName);
		obj.zMenuItem.zClick(localize(locator.renameTag));
		obj.zEditField.zTypeInDlg(localize(locator.newTagName), newTagName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(500);
	}

	public void zDeleteTag(String tagName) throws Exception {
		obj.zFolder.zRtClick(tagName);
		obj.zMenuItem.zClick(localize(locator.del));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		Thread.sleep(1000);
		obj.zFolder.zNotExists(tagName);
	}

	public void zDuplicateTag(String tagName) throws Exception {
		obj.zFolder.zRtClick(tagName);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlgByName(localize(locator.tagName), tagName,
				localize(locator.createNewTag));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewTag));
		assertReport(localize(locator.tagNameExists), obj.zDialog
				.zGetMessage(localize(locator.criticalMsg)),
				"Verifying dialog message");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.criticalMsg));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewTag));
		Thread.sleep(1000);
		obj.zFolder.zNotExists(tagName);
	}

	public static void zCreateSavedSearchFolder(String savedSearchFolderName,
			String searchString) throws Exception {
		selenium.type("xpath=//input[@class='search_input']", searchString);
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClick("id=zb__Search__SAVE_left_icon");
		obj.zEditField.zTypeInDlgByName(localize(locator.name),
				savedSearchFolderName, localize(locator.saveSearch));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.saveSearch));
	}

	public void zRenameSavedSearchFolder(String oldSavedSearchFolderName,
			String newSavedSearchFolderName) throws Exception {
		obj.zFolder.zRtClick(oldSavedSearchFolderName);
		obj.zMenuItem.zClick(localize(locator.renameSearch));
		obj.zEditField.zTypeInDlg(localize(locator.newName),
				newSavedSearchFolderName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(500);
	}

	public void zMoveFolderToTrash(String savedSearchFolderName)
			throws Exception {
		obj.zFolder.zRtClick(savedSearchFolderName);
		obj.zMenuItem.zClick(localize(locator.del));
		Thread.sleep(1000);
	}

	public void zPermanentlyDeleteFolder(String savedSearchFolderName)
			throws Exception {
		obj.zFolder.zRtClick(savedSearchFolderName);
		obj.zMenuItem.zClick(localize(locator.del));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.warningMsg));
		Thread.sleep(1000);
		obj.zFolder.zNotExists(savedSearchFolderName);
	}

	public void zRenameFolder(String folderName, String newFolderName)
			throws Exception {
		obj.zFolder.zRtClick(folderName);
		obj.zMenuItem.zClick(localize(locator.renameFolder));
		obj.zEditField.zTypeInDlg(localize(locator.newName), newFolderName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zFolder.zExists(newFolderName);
		obj.zFolder.zNotExists(folderName);
	}

	public static void pressKeys(String keys) throws Exception {
		String individualKey;
		Robot zRobot = new Robot();
		String[] key = keys.split(",");
		for (int i = 0; i <= key.length - 1; i++) {
			individualKey = key[i].toLowerCase().trim();
			Thread.sleep(1000);
			if (individualKey.equals("_")) {
				zRobot.keyPress(KeyEvent.VK_UNDERSCORE);
				zRobot.keyRelease(KeyEvent.VK_UNDERSCORE);
			} else if (individualKey.equals("'")) {
				zRobot.keyPress(KeyEvent.VK_QUOTE);
				zRobot.keyRelease(KeyEvent.VK_QUOTE);
			} else if (individualKey.equals("@")) {
				zRobot.keyPress(KeyEvent.VK_SHIFT);
				zRobot.keyPress(KeyEvent.VK_2);
				zRobot.keyRelease(KeyEvent.VK_2);
				zRobot.keyRelease(KeyEvent.VK_SHIFT);
			} else if (individualKey.equals(";")) {
				zRobot.keyPress(KeyEvent.VK_SEMICOLON);
				zRobot.keyRelease(KeyEvent.VK_SEMICOLON);
			} else if (individualKey.equals("$")) {
				zRobot.keyPress(KeyEvent.VK_DOLLAR);
				zRobot.keyRelease(KeyEvent.VK_DOLLAR);
			} else if (individualKey.equals("-")) {
				zRobot.keyPress(KeyEvent.VK_SUBTRACT);
				zRobot.keyRelease(KeyEvent.VK_SUBTRACT);
			} else if (individualKey.equals("!")) {
				zRobot.keyPress(KeyEvent.VK_EXCLAMATION_MARK);
				zRobot.keyRelease(KeyEvent.VK_EXCLAMATION_MARK);
			} else if (individualKey.equals("(")) {
				zRobot.keyPress(KeyEvent.VK_LEFT_PARENTHESIS);
				zRobot.keyRelease(KeyEvent.VK_LEFT_PARENTHESIS);
			} else if (individualKey.equals(")")) {
				zRobot.keyPress(KeyEvent.VK_RIGHT_PARENTHESIS);
				zRobot.keyRelease(KeyEvent.VK_RIGHT_PARENTHESIS);
			} else if (individualKey.equals("&")) {
				zRobot.keyPress(KeyEvent.VK_AMPERSAND);
				zRobot.keyRelease(KeyEvent.VK_AMPERSAND);
			} else if (individualKey.equals(".")) {
				zRobot.keyPress(KeyEvent.VK_PERIOD);
				zRobot.keyRelease(KeyEvent.VK_PERIOD);
			} else if (individualKey.equals(",")) {
				zRobot.keyPress(KeyEvent.VK_COMMA);
				zRobot.keyRelease(KeyEvent.VK_COMMA);
			} else if (individualKey.equals("backspace")) {
				zRobot.keyPress(KeyEvent.VK_BACK_SPACE);
				zRobot.keyRelease(KeyEvent.VK_BACK_SPACE);
			} else if (individualKey.equals("space")) {
				zRobot.keyPress(KeyEvent.VK_SPACE);
				zRobot.keyRelease(KeyEvent.VK_SPACE);
			} else if (individualKey.equals("delete")) {
				zRobot.keyPress(KeyEvent.VK_DELETE);
				zRobot.keyRelease(KeyEvent.VK_DELETE);
			} else if (individualKey.equals("enter")) {
				zRobot.keyPress(KeyEvent.VK_ENTER);
				zRobot.keyRelease(KeyEvent.VK_ENTER);
			} else if (individualKey.equals("right")) {
				zRobot.keyPress(KeyEvent.VK_RIGHT);
				zRobot.keyRelease(KeyEvent.VK_RIGHT);
			} else if (individualKey.equals("-")) {
				zRobot.keyPress(KeyEvent.VK_MINUS);
				zRobot.keyRelease(KeyEvent.VK_MINUS);
			} else if (individualKey.equals("home")) {
				zRobot.keyPress(KeyEvent.VK_HOME);
				zRobot.keyRelease(KeyEvent.VK_HOME);
			} else if (individualKey.equals("end")) {
				zRobot.keyPress(KeyEvent.VK_END);
				zRobot.keyRelease(KeyEvent.VK_END);
			} else if (individualKey.equals("down")) {
				zRobot.keyPress(KeyEvent.VK_DOWN);
				zRobot.keyRelease(KeyEvent.VK_DOWN);
			} else if (individualKey.equals("up")) {
				zRobot.keyPress(KeyEvent.VK_UP);
				zRobot.keyRelease(KeyEvent.VK_UP);
			} else if (individualKey.equals("tab")) {
				zRobot.keyPress(KeyEvent.VK_TAB);
				zRobot.keyRelease(KeyEvent.VK_TAB);
			} else if (individualKey.equals("*")) {
				zRobot.keyPress(KeyEvent.VK_SHIFT);
				zRobot.keyPress(KeyEvent.VK_8);
				zRobot.keyRelease(KeyEvent.VK_8);
				zRobot.keyRelease(KeyEvent.VK_SHIFT);
			} else if (individualKey.equals(":")) {
				zRobot.keyPress(KeyEvent.VK_SHIFT);
				zRobot.keyPress(KeyEvent.VK_SEMICOLON);
				zRobot.keyRelease(KeyEvent.VK_SEMICOLON);
				zRobot.keyRelease(KeyEvent.VK_SHIFT);
			} else if (individualKey.equals("ctrl+a")) {
				zRobot.keyPress(KeyEvent.VK_CONTROL);
				zRobot.keyPress(KeyEvent.VK_A);
				zRobot.keyRelease(KeyEvent.VK_CONTROL);
				zRobot.keyRelease(KeyEvent.VK_A);
			} else if (individualKey.equals("0")) {
				zRobot.keyPress(KeyEvent.VK_0);
				zRobot.keyRelease(KeyEvent.VK_0);
			} else if (individualKey.equals("1")) {
				zRobot.keyPress(KeyEvent.VK_1);
				zRobot.keyRelease(KeyEvent.VK_1);
			} else if (individualKey.equals("2")) {
				zRobot.keyPress(KeyEvent.VK_2);
				zRobot.keyRelease(KeyEvent.VK_2);
			} else if (individualKey.equals("3")) {
				zRobot.keyPress(KeyEvent.VK_3);
				zRobot.keyRelease(KeyEvent.VK_3);
			} else if (individualKey.equals("4")) {
				zRobot.keyPress(KeyEvent.VK_4);
				zRobot.keyRelease(KeyEvent.VK_4);
			} else if (individualKey.equals("5")) {
				zRobot.keyPress(KeyEvent.VK_5);
				zRobot.keyRelease(KeyEvent.VK_5);
			} else if (individualKey.equals("6")) {
				zRobot.keyPress(KeyEvent.VK_6);
				zRobot.keyRelease(KeyEvent.VK_6);
			} else if (individualKey.equals("7")) {
				zRobot.keyPress(KeyEvent.VK_7);
				zRobot.keyRelease(KeyEvent.VK_7);
			} else if (individualKey.equals("8")) {
				zRobot.keyPress(KeyEvent.VK_8);
				zRobot.keyRelease(KeyEvent.VK_8);
			} else if (individualKey.equals("9")) {
				zRobot.keyPress(KeyEvent.VK_9);
				zRobot.keyRelease(KeyEvent.VK_9);
			} else if (individualKey.equals("a")) {
				zRobot.keyPress(KeyEvent.VK_A);
				zRobot.keyRelease(KeyEvent.VK_A);
			} else if (individualKey.equals("b")) {
				zRobot.keyPress(KeyEvent.VK_B);
				zRobot.keyRelease(KeyEvent.VK_B);
			} else if (individualKey.equals("c")) {
				zRobot.keyPress(KeyEvent.VK_C);
				zRobot.keyRelease(KeyEvent.VK_C);
			} else if (individualKey.equals("d")) {
				zRobot.keyPress(KeyEvent.VK_D);
				zRobot.keyRelease(KeyEvent.VK_D);
			} else if (individualKey.equals("e")) {
				zRobot.keyPress(KeyEvent.VK_E);
				zRobot.keyRelease(KeyEvent.VK_E);
			} else if (individualKey.equals("f")) {
				zRobot.keyPress(KeyEvent.VK_F);
				zRobot.keyRelease(KeyEvent.VK_F);
			} else if (individualKey.equals("g")) {
				zRobot.keyPress(KeyEvent.VK_G);
				zRobot.keyRelease(KeyEvent.VK_G);
			} else if (individualKey.equals("h")) {
				zRobot.keyPress(KeyEvent.VK_H);
				zRobot.keyRelease(KeyEvent.VK_H);
			} else if (individualKey.equals("i")) {
				zRobot.keyPress(KeyEvent.VK_I);
				zRobot.keyRelease(KeyEvent.VK_I);
			} else if (individualKey.equals("j")) {
				zRobot.keyPress(KeyEvent.VK_J);
				zRobot.keyRelease(KeyEvent.VK_J);
			} else if (individualKey.equals("k")) {
				zRobot.keyPress(KeyEvent.VK_K);
				zRobot.keyRelease(KeyEvent.VK_K);
			} else if (individualKey.equals("l")) {
				zRobot.keyPress(KeyEvent.VK_L);
				zRobot.keyRelease(KeyEvent.VK_L);
			} else if (individualKey.equals("m")) {
				zRobot.keyPress(KeyEvent.VK_M);
				zRobot.keyRelease(KeyEvent.VK_M);
			} else if (individualKey.equals("n")) {
				zRobot.keyPress(KeyEvent.VK_N);
				zRobot.keyRelease(KeyEvent.VK_N);
			} else if (individualKey.equals("o")) {
				zRobot.keyPress(KeyEvent.VK_O);
				zRobot.keyRelease(KeyEvent.VK_O);
			} else if (individualKey.equals("p")) {
				zRobot.keyPress(KeyEvent.VK_P);
				zRobot.keyRelease(KeyEvent.VK_P);
			} else if (individualKey.equals("q")) {
				zRobot.keyPress(KeyEvent.VK_Q);
				zRobot.keyRelease(KeyEvent.VK_Q);
			} else if (individualKey.equals("r")) {
				zRobot.keyPress(KeyEvent.VK_R);
				zRobot.keyRelease(KeyEvent.VK_R);
			} else if (individualKey.equals("s")) {
				zRobot.keyPress(KeyEvent.VK_S);
				zRobot.keyRelease(KeyEvent.VK_S);
			} else if (individualKey.equals("t")) {
				zRobot.keyPress(KeyEvent.VK_T);
				zRobot.keyRelease(KeyEvent.VK_T);
			} else if (individualKey.equals("u")) {
				zRobot.keyPress(KeyEvent.VK_U);
				zRobot.keyRelease(KeyEvent.VK_U);
			} else if (individualKey.equals("v")) {
				zRobot.keyPress(KeyEvent.VK_V);
				zRobot.keyRelease(KeyEvent.VK_V);
			} else if (individualKey.equals("w")) {
				zRobot.keyPress(KeyEvent.VK_W);
				zRobot.keyRelease(KeyEvent.VK_W);
			} else if (individualKey.equals("x")) {
				zRobot.keyPress(KeyEvent.VK_X);
				zRobot.keyRelease(KeyEvent.VK_X);
			} else if (individualKey.equals("y")) {
				zRobot.keyPress(KeyEvent.VK_Y);
				zRobot.keyRelease(KeyEvent.VK_Y);
			} else if (individualKey.equals("z")) {
				zRobot.keyPress(KeyEvent.VK_Z);
				zRobot.keyRelease(KeyEvent.VK_Z);
			}
			System.out.println("Typed keyboard key " + individualKey
					+ " using Robot class");
		}
	}

	public static void zVerifyAutocompleteExists(String value, int rank,
			int wait) throws Exception {
		if (wait == 1) {
			Thread.sleep(1000);
		}
		Assert
				.assertTrue(
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//tr[contains(@id, 'acRow_"
										+ (rank - 1)
										+ "')]//td[contains(text(), '"
										+ value
										+ "')]"),
						"Verifying autocomplete list rank " + rank + " for "
								+ value);
	}

	public static void zVerifyAutocompleteNotExists(String value, int rank,
			int wait) throws Exception {
		if (wait == 1) {
			Thread.sleep(1000);
		}
		Assert
				.assertFalse(
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//tr[contains(@id, 'acRow_"
										+ (rank - 1)
										+ "')]//td[contains(text(), '"
										+ value
										+ "')]"),
						"Verifying autocomplete list rank " + rank + " for "
								+ value);
	}

	/**
	 * customized method for DragAndDrop, in case the default Selenium
	 * DragAndDrop doesn't work.
	 * 
	 * @author Girish
	 */
	public static void zDragAndDrop(String source, String destination)
			throws Exception {
		Thread.sleep(2000);
		Number x_coord1 = selenium.getElementPositionLeft(destination);
		Number y_coord1 = selenium.getElementPositionTop(destination);
		Number x_coord2 = selenium.getElementPositionLeft(source);
		Number y_coord2 = selenium.getElementPositionTop(source);
		Number x_coord = (x_coord1.intValue() - x_coord2.intValue());
		Number y_coord = (y_coord1.intValue() - y_coord2.intValue());

		String xy_coord = x_coord.toString() + "," + y_coord.toString();
		System.out.println("x,y coordinate of the objectToBeDroppedInto="
				+ x_coord1 + "," + y_coord1);
		System.out.println("x,y coordinate of the objectToBeDragged="
				+ x_coord2 + "," + y_coord2);
		System.out
				.println("x,y coordinate of the objectToBeDroppedInto relative to objectToBeDragged = "
						+ xy_coord);

		selenium.mouseDown(source);
		Thread.sleep(1000);
		selenium.mouseMoveAt(source, xy_coord);
		Thread.sleep(1000 * 2);
		selenium.mouseMove(destination);
		selenium.mouseOver(destination);
		Thread.sleep(1000);
		selenium.mouseUp(destination);
		Thread.sleep(2000);
	}

	public static void calculateCoverage() throws Exception {
		String coverage_string = selenium.getEval(COVERAGE_SCRIPT);
		JSONObject jsonCoverage = (JSONObject)JSONSerializer.toJSON(coverage_string);
		String individualFileInfo[] = coverage_string.split("},");
		for(int i=0; i<individualFileInfo.length;i++) {
			String jsonElements[] = individualFileInfo[i].split(":");
			if(jsonElements[0].startsWith("{")) jsonElements[0]=jsonElements[0].replace("{", "");
			if(jsonElements[0].startsWith("\"") && jsonElements[0].endsWith(".js\"")) 
			{
				String jsFileName = jsonElements[0].replace("\"", "");
				parseCoverage(jsFileName, jsonCoverage);
				updateSource(jsFileName);
			}	
		}		
	}

	public static void parseCoverage(String file, JSONObject jsonCoverage) 	{
		JSONObject fileName = jsonCoverage.getJSONObject(file);
		JSONArray jsonCoverageArray = fileName.getJSONArray("coverage");
		ArrayList<Integer> coverage = new ArrayList<Integer>();
		for(int j=0;j<jsonCoverageArray.size();j++) {
			if(jsonCoverageArray.getString(j).equalsIgnoreCase("null")) {
				coverage.add(null);
			} else {
				coverage.add(Integer.parseInt(jsonCoverageArray.getString(j))) ;
			}
		}
		updateCoverage(file, coverage);
	}

	public static void updateSource(String file) throws Exception
	{
		if(!FILENAME_TO_SOURCE.containsKey(file)) {
			URL  url;
			url = new URL("http://"+config.getString("coverageServer")+"/zimbra/"+file);
			URLConnection uc = url.openConnection();
			DataInputStream dis;
			dis = new DataInputStream(uc.getInputStream());
			JSONArray jsonSourceArray = new JSONArray();

			String line;

			while ((line = dis.readLine()) != null)
			{
				jsonSourceArray.add(line);
			} 
			dis.close();
			FILENAME_TO_SOURCE.put(file, jsonSourceArray);
		}
	}

	public static void updateCoverage(String file, ArrayList<Integer> data)
	{
		if (FILENAME_TO_COVERAGE.containsKey(file))
		{
			ArrayList<Integer> coverage = FILENAME_TO_COVERAGE.get(file);
			int i = 0;
			for (; i < coverage.size(); i++)
			{
				Integer oldValue = coverage.get(i);
				Integer newValue = data.get(i);
				if (oldValue == null && newValue == null)
				{
					continue;
				}
				if (newValue == null)
				{
					continue;
				}
				if (oldValue == null)
				{
					oldValue = 0;
				}
				coverage.set(i, oldValue + newValue);
			}

			for (; i < data.size(); i++)
			{
				coverage.add(data.get(i));
			}
			FILENAME_TO_COVERAGE.put(file, coverage);
		}
		else
		{
			FILENAME_TO_COVERAGE.put(file, data);
		}
	}

}
