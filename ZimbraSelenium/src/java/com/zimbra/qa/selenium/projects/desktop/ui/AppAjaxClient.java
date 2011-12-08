/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraDesktopProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.accounts.PageAddNewAccount;
import com.zimbra.qa.selenium.projects.desktop.ui.addressbook.PageAddressbook;
import com.zimbra.qa.selenium.projects.desktop.ui.addressbook.TreeContacts;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.PageBriefcase;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.TreeBriefcase;
import com.zimbra.qa.selenium.projects.desktop.ui.calendar.PageCalendar;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.*;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.PagePreferences;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.TreePreferences;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.signature.PageSignature;
import com.zimbra.qa.selenium.projects.desktop.ui.search.PageAdvancedSearch;
import com.zimbra.qa.selenium.projects.desktop.ui.search.PageSearch;
import com.zimbra.qa.selenium.projects.desktop.ui.tasks.*;


/**
 * The <code>AppAjaxClient</code> class defines the Zimbra Ajax client.
 * <p>
 * The <code>AppAjaxClient</code> contains all pages, folder trees,
 * dialog boxes, forms, menus for the Ajax client.
 * <p>
 * In {@link AjaxCommonTest}, there is one
 * AppAjaxClient object created per test case class (ensuring 
 * class-level concurrency).  The test case methods can access
 * different application pages and trees, using the object
 * properties.
 * <p>
 * <pre>
 * {@code
 * 
 * // Navigate to the addresbook
 * app.zPageAddressbook.navigateTo();
 * 
 * // Click "New" button to create a new contact
 * app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
 * 
 * }
 * </pre>
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public class AppAjaxClient extends AbsApplication {
	
	public PageLogin					zPageLogin = null;
	public PageMain						zPageMain = null;
	public PageSearch					zPageSearch = null;
	public PageAdvancedSearch			zPageAdvancedSearch = null;
	public PageMail						zPageMail = null;
	public PageBriefcase                zPageBriefcase = null;
	public PageAddressbook              zPageAddressbook = null;
	public PageCalendar					zPageCalendar = null;
	public PageTasks					zPageTasks = null;
	public PagePreferences				zPagePreferences = null;
	public PageSignature				zPageSignature = null;
	public PageAddNewAccount            zPageAddNewAccount = null;
	
	public TreeMail						zTreeMail = null;
	public TreeContacts					zTreeContacts = null;
	public TreeTasks					zTreeTasks = null;
	public TreeBriefcase		        zTreeBriefcase = null;
	public TreePreferences				zTreePreferences = null;
	
	public AppAjaxClient() {
		super();
		
		logger.info("new " + AppAjaxClient.class.getCanonicalName());
		
		
		// Login page
		
		zPageLogin = new PageLogin(this);
		pages.put(zPageLogin.myPageName(), zPageLogin);
		
		// Main page
		zPageMain = new PageMain(this);
		pages.put(zPageMain.myPageName(), zPageMain);
		
		zPageSearch = new PageSearch(this);
		pages.put(zPageSearch.myPageName(), zPageSearch);
		
		zPageAdvancedSearch = new PageAdvancedSearch(this);
		pages.put(zPageAdvancedSearch.myPageName(), zPageAdvancedSearch);
		
		// Mail page
		zPageMail = new PageMail(this);
		pages.put(zPageMail.myPageName(), zPageMail);
		
		zTreeMail = new TreeMail(this);
		trees.put(zTreeMail.myPageName(), zTreeMail);
		
		//Addressbook page    
		zPageAddressbook = new PageAddressbook(this);
		pages.put(zPageAddressbook.myPageName(), zPageAddressbook);

		zTreeContacts = new TreeContacts(this);
		trees.put(zTreeContacts.myPageName(), zTreeContacts);
		
		// Calendar page
		zPageCalendar = new PageCalendar(this);
		pages.put(zPageCalendar.myPageName(), zPageCalendar);
		
		// PageBriefcase page
		zPageBriefcase = new PageBriefcase(this);
		pages.put(zPageBriefcase.myPageName(), zPageBriefcase);
		
		zTreeBriefcase = new TreeBriefcase(this);
		trees.put(zTreeBriefcase.myPageName(), zTreeBriefcase);
				
		// PageTasks page
		zPageTasks = new PageTasks(this);
		pages.put(zPageTasks.myPageName(), zPageTasks);
		
		zTreeTasks = new TreeTasks(this);
		trees.put(zTreeTasks.myPageName(), zTreeTasks);
		
		// Preferences page
		zPagePreferences = new PagePreferences(this);
		pages.put(zPagePreferences.myPageName(), zPagePreferences);

		zTreePreferences = new TreePreferences(this);
		trees.put(zTreePreferences.myPageName(), zTreePreferences);

		// signature Preferences page
		zPageSignature = new PageSignature(this);
		pages.put(zPageSignature.myPageName(),zPageSignature);

		// Add New Account page
		zPageAddNewAccount = new PageAddNewAccount(this);
		pages.put(zPageAddNewAccount.myPageName(), zPageAddNewAccount);

		// Configure the localization strings
		getL10N().zAddBundlename(I18N.Catalog.I18nMsg);
		getL10N().zAddBundlename(I18N.Catalog.AjxMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZsMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZmMsg);
		
	}
	
	
	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsApplication#isLoaded()
	 */
	@Override
	public boolean zIsLoaded() throws HarnessException {
	   if (this.zPageMain.zIsActive() ||
            this.zPageLogin.zIsActive() ||
            this.zPageAddNewAccount.zIsActive()) {
         return true;
      } else {
         return false;
      }
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsApplication#myApplicationName()
	 */
	@Override
	public String myApplicationName() {
		return ("Ajax Client");
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsApplication#myApplicationName()
	 * Set to public instead of protected only for desktop project to allow multiple account switching
	 * in the middle of the tests
	 */
	@Override
	public ZimbraAccount zSetActiveAcount(ZimbraAccount account) throws HarnessException {
		return (super.zSetActiveAcount(account));
	}

	/**
    * Delete Desktop account through HTTP Post with last account variable defaulted to false
    * @param accountName Account Name to be deleted
    * @param accountId Account ID to be deleted
    * @param accountType Account Type (usually: zimbra)
    * @param accountFlavor Account Flavor (usually: Zimbra)
    * @throws HarnessException
    */
	public void zDeleteDesktopAccount(String accountName, String accountId,
         String accountType, String accountFlavor) throws HarnessException {
	   zDeleteDesktopAccount(accountName, accountId, accountType, accountFlavor, false);
   }

	/**
    * Delete Desktop account through HTTP Post
    * @param accountName Account Name to be deleted
    * @param accountId Account ID to be deleted
    * @param accountType Account Type (usually: zimbra)
    * @param accountFlavor Account Flavor (usually: Zimbra)
    * @param lastAccount Is this last account (for wait purpose)
    * @throws HarnessException
    */
   public void zDeleteDesktopAccount(String accountName, String accountId,
         String accountType, String accountFlavor, boolean lastAccount) throws HarnessException {
      String serverScheme = ZimbraSeleniumProperties.getStringProperty("server.scheme", "http");
      String serverName = ZimbraSeleniumProperties.getStringProperty("desktop.server.host", "localhost");
      ZimbraDesktopProperties zdp = ZimbraDesktopProperties.getInstance();
      String connectionPort = zdp.getConnectionPort();
      String accountDeleteUrl = new StringBuilder(serverScheme).append("://")
            .append(serverName). append(":")
            .append(connectionPort).append("/")
            .append("zimbra/desktop/accsetup.jsp?at=")
            .append(zdp.getSerialNumber()).append("&accountId=")
            .append(accountId).append("&verb=del&accountFlavor=")
            .append(accountFlavor).append("&accountName=")
            .append(accountName).append("&accountType=")
            .append(accountType).toString();//append("&dev=1&scripterrors=1").toString();

      logger.info("accountDeleteUrl: " + accountDeleteUrl);
      GeneralUtility.doHttpPost(accountDeleteUrl);

      zPageLogin.sRefresh();
      GeneralUtility.waitForElementPresent(zPageLogin,
            PageLogin.Locators.zAddNewAccountButton);
      if (lastAccount || !zPageLogin.sIsElementPresent(PageLogin.Locators.zDeleteButton)) {
         ZimbraAccount.ResetAccountZDC();
      }
   }
}
