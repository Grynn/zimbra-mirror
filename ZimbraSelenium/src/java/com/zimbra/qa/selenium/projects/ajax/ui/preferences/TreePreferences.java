/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.preferences;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;


/**
 * @author zimbra
 *
 */
public class TreePreferences extends AbsTree {

	public static class Locators {
		public final static String zGeneralTextID = "zti__main_Options__PREF_PAGE_GENERAL_textCell";
		public final static String zGeneralTextID_Desktop = "zti__local@host.local:main_Options__PREF_PAGE_GENERAL_textCell";
		public final static String zGeneralImageID = "zti__main_Options__PREF_PAGE_GENERAL_imageCell";
		public final static String zGeneralImageID_Desktop = "zti__local@host.local:main_Options__PREF_PAGE_GENERAL_imageCell";
		public final static String zsignatureTextID = "zti__main_Options__PREF_PAGE_SIGNATURES_textCell";
		public final static String zSignatureTextID_Desktop = "zti__<EMAIL_ADDRESS>:main_Options__PREF_PAGE_SIGNATURES_textCell";
		public final static String zAddressBookTextID = "zti__main_Options__PREF_PAGE_CONTACTS_textCell";
	}
	
	public enum TreeItem {
		General,
		Mail, MailComposing, MailSignatures, MailAccounts, MailFilters, MailOutOfOffice, MailTrustedAddresses,
		AddressBook,
		Calendar,
		Sharing,
		Notifications,
		MobileDevices,
		ImportExport,
		Shortcuts,
		QuickCommands,
		Zimlets 
	}
		
	
	public TreePreferences(AbsApplication application) {
		super(application);
		logger.info("new " + TreePreferences.class.getCanonicalName());
	}
	
	/**
	 * Click on an item in the preferences tree
	 * @param action
	 * @param item
	 * @throws HarnessException
	 */
	public void zTreeItem(Action action, TreeItem item) throws HarnessException {
		logger.info("zTreeItem(" + action +", "+ item +")");
		String locator = null;
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
		   if ( !itemToLocator_desktop.containsKey(item) ) {
            throw new HarnessException("locator not defined in itemToLocator_desktop for "+ item);
         }
         
         if ( itemToLocator_desktop.get(item) == null ) {
            throw new HarnessException("locator is null in itemToLocator_desktop for "+ item);
         }
   
         locator = itemToLocator_desktop.get(item).replace("<EMAIL_ADDRESS>",
               MyApplication.zGetActiveAccount().EmailAddress);

		} else {
   		if ( !itemToLocator.containsKey(item) ) {
   			throw new HarnessException("locator not defined in itemToLocator for "+ item);
   		}
   		
   		if ( itemToLocator.get(item) == null ) {
   			throw new HarnessException("locator is null in itemToLocator for "+ item);
   		}
   
   	   locator = itemToLocator.get(item);

		}
		
		if ( !sIsElementPresent(locator) ) {
			throw new HarnessException("locator is not present "+ locator);
		}
		
		zClick(locator);
		
		zWaitForBusyOverlay();
		
	}

	/**
	 * Not implemented.  Use zTreeItem(Action action, TreeItem item) instead
	 */
	@Override
	public AbsPage zTreeItem(Action action, IItem preference) throws HarnessException {
		throw new HarnessException("Not implemented.  Use zTreeItem(Action action, TreeItem item) instead");
	}
	
	/**
	 * Not implemented.  Use zTreeItem(Action action, TreeItem item) instead
	 */
	@Override
	public AbsPage zTreeItem(Action action, Button option, IItem item) throws HarnessException {
		throw new HarnessException("Not applicable.");
	}


	/**
	 * Not implemented.  There are no buttons in the preferences tree.
	 */
	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		throw new HarnessException("Not implemented.  There are no buttons in the preferences tree.");
	}

	private static final Map<TreeItem, String> itemToLocator = createItemToLocator();
	private static Map<TreeItem, String> createItemToLocator() {
		
		Map<TreeItem, String> map = new HashMap<TreeItem, String>();
		
		map.put(TreeItem.General, "id=" + Locators.zGeneralTextID);
		map.put(TreeItem.Mail, "css=td[id='zti__main_Options__PREF_PAGE_MAIL_textCell']");
		map.put(TreeItem.MailComposing, "css=td[id='zti__main_Options__PREF_PAGE_COMPOSING_textCell']");
		map.put(TreeItem.MailSignatures, "id="+Locators.zsignatureTextID);
		map.put(TreeItem.MailAccounts, "css=td[id='zti__main_Options__PREF_PAGE_ACCOUNTS_textCell']");
		map.put(TreeItem.MailFilters, "css=td[id='zti__main_Options__PREF_PAGE_FILTERS_textCell']");
		map.put(TreeItem.MailOutOfOffice, "css=td[id='zti__main_Options__PREF_PAGE_OUTOFOFFICE_textCell']");
		map.put(TreeItem.MailTrustedAddresses, "css=td[id='zti__main_Options__PREF_PAGE_TRUSTED_ADDR_textCell']");
		map.put(TreeItem.AddressBook, "id=" + Locators.zAddressBookTextID);
		map.put(TreeItem.Calendar, "css=td[id='zti__main_Options__PREF_PAGE_CALENDAR_textCell']");
		map.put(TreeItem.Sharing, "css=td[id='zti__main_Options__PREF_PAGE_SHARING_textCell']");
		map.put(TreeItem.Notifications, "css=td[id='zti__main_Options__PREF_PAGE_NOTIFICATIONS_textCell']");
		map.put(TreeItem.MobileDevices, "css=td[id='zti__main_Options__PREF_PAGE_MOBILE_textCell']");
		map.put(TreeItem.ImportExport, "css=td[id='zti__main_Options__PREF_PAGE_IMPORT_EXPORT_textCell']");
		map.put(TreeItem.Shortcuts, "css=td[id='zti__main_Options__PREF_PAGE_SHORTCUTS_textCell']");
		map.put(TreeItem.QuickCommands, "css=td[id='zti__main_Options__PREF_PAGE_QUICKCOMMANDS_textCell']");
		map.put(TreeItem.Zimlets, "css=td[id='zti__main_Options__PREF_PAGE_PREF_ZIMLETS_textCell']");
		
		return (Collections.unmodifiableMap(map));
	}

	private static final Map<TreeItem, String> itemToLocator_desktop = createItemToLocator_desktop();
   private static Map<TreeItem, String> createItemToLocator_desktop() {
      
      Map<TreeItem, String> map = new HashMap<TreeItem, String>();
      
      map.put(TreeItem.General, "id=" + Locators.zGeneralTextID_Desktop);
      map.put(TreeItem.Mail, null);
      map.put(TreeItem.MailComposing, null);
      map.put(TreeItem.MailSignatures, "id=" + Locators.zSignatureTextID_Desktop);
      map.put(TreeItem.MailAccounts, null);
      map.put(TreeItem.MailFilters, null);
      map.put(TreeItem.MailTrustedAddresses, null);
      map.put(TreeItem.AddressBook, null);
      map.put(TreeItem.Calendar, null);
      map.put(TreeItem.Sharing, null);
      map.put(TreeItem.Notifications, null);
      map.put(TreeItem.ImportExport, null);
      map.put(TreeItem.Shortcuts, null);
      map.put(TreeItem.Zimlets, null);
      
      return (Collections.unmodifiableMap(map));
   }

	/* (non-Javadoc)
	 * @see framework.ui.AbsTree#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		throw new HarnessException("implement me");
	}


}
