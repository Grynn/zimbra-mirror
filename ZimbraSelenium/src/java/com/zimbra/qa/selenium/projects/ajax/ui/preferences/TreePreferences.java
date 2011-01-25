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


/**
 * @author zimbra
 *
 */
public class TreePreferences extends AbsTree {

	public static class Locators {
		public static String zGeneralTextID = "zti__main_Options__PREF_PAGE_GENERAL_textCell";
		public static String zGeneralImageID = "zti__main_Options__PREF_PAGE_GENERAL_imageCell";
		
	}
	
	public enum TreeItem {
		General,
		Mail, MailComposing, MailSignatures, MailAccounts, MailFilters, MailTrustedAddresses,
		AddressBook,
		Calendar,
		Sharing,
		Notifications,
		ImportExport,
		Shortcuts,
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
		
		if ( !itemToLocator.containsKey(item) ) {
			throw new HarnessException("locator not defined in itemToLocator for "+ item);
		}
		
		if ( itemToLocator.get(item) == null ) {
			throw new HarnessException("locator is null in itemToLocator for "+ item);
		}

		String locator = itemToLocator.get(item);
		
		if ( !sIsElementPresent(locator) ) {
			throw new HarnessException("locator is not present "+ locator);
		}
		
		zClick(locator);
		
	}

	/**
	 * Not implemented.  Use zTreeItem(Action action, TreeItem item) instead
	 */
	public AbsPage zTreeItem(Action action, IItem preference) throws HarnessException {
		throw new HarnessException("Not implemented.  Use zTreeItem(Action action, TreeItem item) instead");
	}
	
	/**
	 * Not implemented.  There are no buttons in the preferences tree.
	 */
	public AbsPage zPressButton(Button button) throws HarnessException {
		throw new HarnessException("Not implemented.  There are no buttons in the preferences tree.");
	}

	private static final Map<TreeItem, String> itemToLocator = createItemToLocator();
	private static Map<TreeItem, String> createItemToLocator() {
		
		Map<TreeItem, String> map = new HashMap<TreeItem, String>();
		
		map.put(TreeItem.General, "id=" + Locators.zGeneralTextID);
		map.put(TreeItem.Mail, null);
		map.put(TreeItem.MailComposing, null);
		map.put(TreeItem.MailSignatures, null);
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
