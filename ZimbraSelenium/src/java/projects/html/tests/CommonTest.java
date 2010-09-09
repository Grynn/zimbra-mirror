package projects.html.tests;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;

import org.clapper.util.text.HTMLUtil;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import projects.html.CoreObjects;
import projects.html.Locators;
import projects.html.PageObjects;

import com.zimbra.common.service.ServiceException;

import framework.core.*;
import framework.core.SeleniumService;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

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
	public static ResourceBundle zhMsg;
	public static ResourceBundle zsMsg;
	public static ResourceBundle ajxMsg;
	public static ResourceBundle i18Msg;
	public static CoreObjects obj;
	public static PageObjects page;
	public static Locators locator;

	protected static Map<String, Object> selfAccountAttrs = new HashMap<String, Object>();

	public CommonTest() {
		zmMsg = ZimbraSeleniumProperties.getResourceBundleProperty("zmMsg");
		zhMsg = ZimbraSeleniumProperties.getResourceBundleProperty("zhMsg");
		ajxMsg = ZimbraSeleniumProperties.getResourceBundleProperty("ajxMsg");
		i18Msg = ZimbraSeleniumProperties.getResourceBundleProperty("i18Msg");
		zsMsg = ZimbraSeleniumProperties.getResourceBundleProperty("zsMsg");
		obj = new CoreObjects();
		page = new PageObjects();		
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
		if (needsReLogin(accntAttrs) || SelNGBase.needReset.get()) {
			resetSession();
			selfAccountAttrs = accntAttrs;
			page.zLoginpage.zLoginToZimbraHTML(accntAttrs);
		}
	}

	private static boolean needsReLogin(Map<String, Object> accntAttrs) {
		int currentAccntAttrsSize = selfAccountAttrs.size() - 4;// -4 is to
		// remove default settings
		// none has logged in yet
		if (ClientSessionFactory.session().currentUserName().equals(""))
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
	public void initTests() throws ServiceException, HarnessException {

		initFramework();
		
		// Create the test domain
		Stafzmprov.createDomain(ZimbraSeleniumProperties.getStringProperty("testdomain"));
		
		SeleniumService.getInstance().startSeleniumServer();
		
		// Provision the default users
		@SuppressWarnings("unused")
		ZimbraAccount ccuser = new ZimbraAccount("ccuser@testdomain.com", "test123").provision().authenticate();
		@SuppressWarnings("unused")
		ZimbraAccount bccuser = new ZimbraAccount("bccuser@testdomain.com", "test123").provision().authenticate();

	}

	/**
	 * Check whether the current test method should be skipped due to locale/browser combination being tested<p>
	 * <p>
	 * all: when used for locales or browsers, skip for all locales or browsers<p>
	 * <p>
	 * na: when used for locales, then the locale being tested does not matter in determining if
	 * the test should be skipped.  When used for browsers, then the browser being tested does not
	 * matter in determining whether the test should be skipped. <p>
	 * <p>
	 * <p>
	 * Example: <p>
	 * <p>
	 * public void TestMethod() { <p>
	 * <p>
	 *     // Check for current client config against skipped configs <p>
	 *     checkForSkipException("ru,en_GB", "na", "3452,15232", "TestMethod feature not implemented for russian or britsh locales"); <p>
	 * <p>
	 *     // ... continue with test <p>
	 * } <p>
	 * <p>
	 * @param locales a comma separated list of locales, or na, or all
	 * @param browsers a comma separated list of browsers, or na, or all
	 * @param bugs a comma separated list of bug numbers for reference
	 * @param remark a short description why the method is skipped
	 * @throws SkipException
	 */
	public void checkForSkipException(String locales, String browsers, String bugs, String remark) throws SkipException {
		
		// Build a string with the remark and bug list
		// to be used at the end of any SkipExceptions
		String data = " remark(" + remark +") bugs("+ bugs +")";
		
		// Check for null
		if (locales == null)
			locales = "";
		if (browsers == null)
			browsers = "";

		// Convert the comma separated lists to List<String> objects
		List<String> localeList = Arrays.asList(locales.trim().toLowerCase().split(","));
		List<String> browserList = Arrays.asList(browsers.toLowerCase().split(","));
		
		// If either locales or browsers contains "all", then skip
		// TODO: confirm this is what is meant by "all"
		if ( localeList.contains("all") )
			throw new SkipException(locales + " contains all" + data);

		if ( browserList.contains("all") )
			throw new SkipException(browsers + " contains all" + data);
		
		// Determine which browser is being used during this test
		String myLocale = ZimbraSeleniumProperties.getStringProperty("locale").trim().toLowerCase();
		String myBrowser = ZimbraSeleniumProperties.getStringProperty("browser").trim().toLowerCase();
		
		// If locales contains "na", then just check the browser
		if ( localeList.contains("na") ) {
			// Locale does no matter, just check the browser
			if (browserList.contains(myBrowser))
				throw new SkipException(browsers + " contains "+ myBrowser + data);
		}
		
		// If browsers contains "na", then just check the locale
		if ( browserList.contains("na") ) {
			// Browser does not matter, just check the locale
			if ( localeList.contains(myLocale) )
				throw new SkipException(locales + " contains " + myLocale + data);	
		}
		
		// Check the locale and browser combination.  Skip if both match.
		if ( localeList.contains(myLocale) && browserList.contains(myBrowser) )
			throw new SkipException(locales + " contains " + myLocale + " and " + browsers + " contains " + myBrowser + data);
	
		// Done.  Test should not be skipped.
		
	}
	

	@AfterSuite(groups = { "always" })
	public void cleanup() throws HarnessException {
		SeleniumService.getInstance().stopSeleniumServer();
	}

	public void initFramework() {
		zhMsg = ResourceBundle.getBundle("framework.locale.ZhMsg", new Locale(ZimbraSeleniumProperties.getConfigProperties().getString("locale")));

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

		//some keys have . in them(represented by _dot_ in java).
		if(key.indexOf("_dot_")>0)
			key = key.replace("_dot_", ".");

		// else.. it must be a label, so localize...


		if (key.equals("ok") || key.equals("cancel"))// bug(zhmsg is different
			// from ajxmsg)
			return prependthis + HTMLUtil.stripHTMLTags(ajxMsg.getString(key));
		try {
			return prependthis + HTMLUtil.stripHTMLTags(zhMsg.getString(key));
		} catch (MissingResourceException e) {
			try {
				return prependthis
						+ HTMLUtil.stripHTMLTags(zmMsg.getString(key));
			} catch (MissingResourceException e1) {
				try {
					return prependthis
							+ HTMLUtil.stripHTMLTags(ajxMsg.getString(key));
				} catch (MissingResourceException e2) {
					try {
						return prependthis
								+ HTMLUtil.stripHTMLTags(i18Msg.getString(key));
					} catch (MissingResourceException e3) {
						return prependthis
								+ HTMLUtil.stripHTMLTags(zsMsg.getString(key));
					}
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
	 * Returns a 5-char length word with random-characters that are localized.
	 * Also, the returned word is special-char or space free.
	 *
	 * @return "tesxe"
	 */
	public static String getLocalizedData_NoSpecialChar() {
		String str = localize("whenReplyingToAddress");
		str = str + localize("editNotebookIndex");
		str = str + localize("invitees");
		str = str + localize("subject");
		str = str + localize("searchCalendar");
		str = str + localize("goToMail");
		str = str + localize("tagItem");
		//str = str + localize("imPrefFlashIcon");
		str = str.replace(" ", "");
		str = str.replace(".", "");
		str = str.replace(":", "");
		Random r = new Random();
		int max = str.length() - 5;
		int randInt = r.nextInt(max);
		return str.substring(randInt, randInt + 5);

	}

	public static String getTodaysDateZimbraFormat() {

		String DATE_FORMAT = "yyyyMMdd";

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

		Calendar testcal = Calendar.getInstance();

		String todayDate = sdf.format(testcal.getTime());

		return todayDate;
	}

	public static void assertReport(String expectedFullBody,
			String dataToVerify, String reportSummary) throws Exception {
		if (expectedFullBody.equals("_selfAccountName_"))
			expectedFullBody = ClientSessionFactory.session().currentUserName();
		if (dataToVerify.equals("_selfAccountName_"))
			dataToVerify = ClientSessionFactory.session().currentUserName();
		Assert.assertTrue(expectedFullBody.indexOf(dataToVerify) >= 0,
				"Expected value(" + expectedFullBody + "), Actual Value("
						+ dataToVerify + ")");
	}

	public void zPressBtnIfDlgExists(String dlgName, String dlgBtn,
			String folderToBeClicked) throws Exception {
		for (int i = 0; i <= 5; i++) {
			String dlgExists = obj.zDialog.zExistsDontWait(dlgName);
			if (dlgExists.equals("true")) {
				obj.zFolder.zClickInDlgByName(folderToBeClicked, dlgName);
				SleepUtil.sleep(1000);
				obj.zButton.zClickInDlgByName(dlgBtn, dlgName);
			} else {
				SleepUtil.sleep(500);
			}
		}
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
				|| (applicationtab.equals("id=TAB_MAIL"))) {
			obj.zButton.zClick("id=TAB_MAIL");
			SleepUtil.sleep(2500);
			zWaitTillObjectExist("button", page.zMailApp.zRefreshBtn);
		} else if ((lCaseapplicationtab.equals("address book"))
				|| (applicationtab.equals("id=TAB_ADDRESSBOOK"))) {
			obj.zButton.zClick("id=TAB_ADDRESSBOOK");
		} else if ((lCaseapplicationtab.equals("calendar"))
				|| (applicationtab.equals("id=TAB_CALENDAR"))) {
			obj.zButton.zClick("id=TAB_CALENDAR");
		} else if ((lCaseapplicationtab.equals("tasks"))
				|| (applicationtab.equals("id=TAB_TASKS"))) {
			obj.zButton.zClick("id=TAB_TASKS");
		} else if ((lCaseapplicationtab.equals("preferences"))
				|| (applicationtab.equals("id=TAB_OPTIONS"))) {
			obj.zButton.zClick("id=TAB_OPTIONS");
			SleepUtil.sleep(2500);
			zWaitTillObjectExist("radiobutton", "name=zimbraPrefClientType");
		}
	}

	public static void zWaitTillObjectExist(String objectType, String objectName)
			throws Exception {
		int i = 0;
		boolean found = false;
		for (i = 0; i <= 15; i++) {
			String retVal = null;
			objectType = objectType.toLowerCase();
			if (objectType.equals("button")) {
				retVal = obj.zButton.zExistsDontWait(objectName);
			} else if (objectType.equals("checkbox")) {
				retVal = obj.zCheckbox.zExistsDontWait(objectName);
			} else if (objectType.equals("radiobutton")) {
				retVal = obj.zRadioBtn.zExistsDontWait(objectName);
			} else if (objectType.equals("message")) {
				retVal = obj.zMessageItem.zExistsDontWait(objectName);
			} else if (objectType.equals("menuitem")) {
				retVal = obj.zMenuItem.zExistsDontWait(objectName);
			} else if (objectType.equals("htmlmenu")) {
				retVal = obj.zMenuItem.zExistsDontWait(objectName);
			} else if (objectType.equals("folder")) {
				retVal = obj.zFolder.zExistsDontWait(objectName);
			} else if (objectType.equals("tab")) {
				retVal = obj.zTab.zExistsDontWait(objectName);
			} else if (objectType.equals("editfield")) {
				retVal = obj.zEditField.zExistsDontWait(objectName);
			} else if (objectType.equals("textarea")) {
				retVal = obj.zTextAreaField.zExistsDontWait(objectName);
			} else if (objectType.equals("link")) {
				if (ClientSessionFactory.session().selenium().isElementPresent("link=" + objectName))
					retVal = "true";
				else
					retVal = "false";
			} else if (objectType.equals("text")) {
				if (ClientSessionFactory.session().selenium().isTextPresent(objectName))
					retVal = "true";
				else
					retVal = "false";
			}

			if (retVal.equals("false")) {
				SleepUtil.sleep(2000);
			} else {
				SleepUtil.sleep(1000);
				found = true;
				break;
			}
		}
		if (!found)
			Assert.fail("Object(" + objectName
					+ ") didn't appear even after 60 seconds");
	}

	public static String getNameWithoutSpace(String key) {
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE"))
			return key.replace("�:", "");
		else
			return key;
	}

	public static void zReloginToAjax() throws Exception {

		String accountName = ClientSessionFactory.session().currentUserName();

		resetSession();
		SleepUtil.sleep(2000);

		
		page.zLoginpage.zLoginToZimbraHTML(accountName);

	}


}
