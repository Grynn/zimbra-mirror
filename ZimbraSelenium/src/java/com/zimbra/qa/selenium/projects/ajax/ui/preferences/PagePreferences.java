/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.preferences;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning.DialogWarningID;


/**
 * @author Matt Rhoades
 *
 */
public class PagePreferences extends AbsTab {

	
	public static class Locators {
		
		public static final String zPreferencesMainID = "zov__main_Options";
		// Preferences Toolbar: Save, Cancel
		public static final String zToolbarSaveID = "zb__PREF__SAVE_title";
		public static final String zToolbarCancelID = "zb__PREF__CANCEL_title";

		public static final String zSaveChangesYes = "id=DWT241_title";
		public static final String zSaveChangesNo = "id=DWT242_title";
		public static final String zSaveChangesCancel = "id=DWT243_title";
		

	}
	
	



	public PagePreferences(AbsApplication application) {
		super(application);
		
		logger.info("new " + PagePreferences.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}
		
		/*
		 * Active:
		 * <div id="zov__main_Options" style="position: absolute; overflow: auto; z-index: 300; left: 0px; top: 76px; width: 169px; height: 537px;" class="ZmOverview" parentid="z_shell">
		 * 
		 * Not active:
		 * <div id="zov__main_Options" style="position: absolute; overflow: auto; z-index: 300; left: -10000px; top: -10000px; width: 169px; height: 537px;" class="ZmOverview" parentid="z_shell">
		 */
		
		
		// If the "folders" tree is visible, then mail is active
		String locator = null;
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
		   locator = "css=div[id='zov__local@host.local:main_Options']";
		} else {
			locator = "css=div#"+ Locators.zPreferencesMainID;
		}
		
		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (loaded);
		
		boolean active = this.zIsVisiblePerPosition(locator, -1, 74);
		return (active);

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {

		// Check if this page is already active.
		if ( zIsActive() ) {
			return;
		}
		
		// Make sure we are logged into the Mobile app
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}
		
		tracer.trace("Navigate to "+ this.myPageName());

		// Click on Preferences icon
		if ( !sIsElementPresent(PageMain.Locators.zAppbarPreferences) ) {
			throw new HarnessException("Can't locate preferences icon");
		}

		zClick(PageMain.Locators.zAppbarPreferences);
		
		zWaitForActive();

	}

	/**
	 * Click "Cancel" to navigate away from preferences
	 * @throws HarnessException 
	 */
	public void zNavigateAway(Button savechanges) throws HarnessException {
		logger.info("zNavigateAway(" + savechanges +")");

		// See also bug 53203

		// Click Cancel
		zToolbarPressButton(Button.B_CANCEL);

		// Check if the "Would you like to save your changes?" appears
		//
		
		// Wait for the dialog to appear
		SleepUtil.sleep(5000);
		
		// Check for the dialog
		if ( zIsVisiblePerPosition("id=DWT240", 420, 200) ) {
			logger.debug("zNavigateAway(" + savechanges +") - dialog is showing");

			String locator = null;
			
			// "Would you like to save your changes?" is displayed.  
			if ( savechanges == Button.B_YES ) {
				locator = Locators.zSaveChangesYes;
			} else if ( savechanges == Button.B_NO ) {
				locator = Locators.zSaveChangesNo;
			} else if ( savechanges == Button.B_CANCEL ) {
				locator = Locators.zSaveChangesCancel;
			} else {
				throw new HarnessException("zNavigateAway() not defined for button "+ savechanges);
			}
			
			if ( locator == null ) {
				throw new HarnessException("zNavigateAway() no locator for button "+ savechanges);
			}
			
			if ( !sIsElementPresent(locator) ) {
				throw new HarnessException("zNavigateAway() locator is not present "+ locator);
			}
			
			zClick(locator);
			
		} else {
			logger.debug("zNavigateAway(" + savechanges +") - dialog did not show");
		}
		
		
	}
	

	/**
	 * Determine if a checkbox is checked or not
	 * @param preference the Account preference to check
	 * @return true if checked, false if not checked
	 * @throws HarnessException
	 */
	public boolean zGetCheckboxStatus(String preference) throws HarnessException {
		logger.info("zGetCheckboxStatus(" + preference +")");

		String locator = null;
		
		if ( preference.equals("zimbraPrefIncludeSpamInSearch")) {
			
			locator = "css=input[id$=_SEARCH_INCLUDES_SPAM]";

		} else if (preference.equals("zimbraPrefIncludeTrashInSearch")) {
			
			locator = "css=input[id$=_SEARCH_INCLUDES_TRASH]";

		} else if (preference.equals("zimbraPrefShowSearchString")) {

			locator = "css=input[id$=_SHOW_SEARCH_STRING]";

		} else if (preference.equals("zimbraPrefAutoAddAddressEnabled")) {

			locator = "css=input[id$=_AUTO_ADD_ADDRESS]";
			
		}  else if (preference.equals("zimbraPrefAutocompleteAddressBubblesEnabled")) {

			locator = "css=input[id$=_USE_ADDR_BUBBLES]";
			
		}else {
			throw new HarnessException("zGetCheckboxStatus() not defined for preference "+ preference);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator not defined for preference "+ preference);
		}
		
		if ( !sIsElementPresent(locator) ) {
			throw new HarnessException("locator not present "+ locator);
		}
		
		boolean checked = sIsChecked(locator);
		logger.info("zGetCheckboxStatus(" + preference +") = "+ checked);
		
		return (checked);

	}
	
	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a Toolbar");
	}
	
	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
			throws HarnessException {

		throw new HarnessException("Not applicaple for Preference");
	}
	
	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not have a Toolbar");
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		tracer.trace("Click button "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
				
		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( button == Button.B_SAVE ) {
			
			locator = "id="+ Locators.zToolbarSaveID;
			page = null;
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator = "id="+ Locators.zToolbarCancelID;
			page = null;
						
		} else if ( button == Button.B_CHANGE_PASSWORD ) {
			
			locator = "css=td[id='CHANGE_PASSWORD_title']";
			page = new SeparateWindowChangePassword(MyApplication);
	
		} else if ( button == Button.B_NEW_FILTER ) {
			
			locator = "css=div[id='zb__FRV__ADD_FILTER_RULE'] td[id$='_title']";
			page = new DialogEditFilter(MyApplication,((AppAjaxClient) MyApplication).zPagePreferences);
	
		} else if ( button == Button.B_NEW_QUICK_COMMAND ) {
			
			locator = "css=div[id='zb__QCV__ADD_QUICK_COMMAND'] td[id$='_title']";
			page = new DialogEditQuickCommand(MyApplication,((AppAjaxClient) MyApplication).zPagePreferences);

		} else if ( button == Button.B_EDIT_QUICK_COMMAND ) {
			
			locator = "css=div[id='zb__QCV__EDIT_QUICK_COMMAND'] td[id$='_title']";
			page = new DialogEditQuickCommand(MyApplication,((AppAjaxClient) MyApplication).zPagePreferences);

		} else if ( button == Button.B_DELETE_QUICK_COMMAND ) {
			
			locator = "css=div[id='zb__QCV__REMOVE_QUICK_COMMAND'] td[id$='_title']";
			page = new DialogWarning(DialogWarningID.QuickCommandConfirmDelete, MyApplication,((AppAjaxClient) MyApplication).zPagePreferences);

		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}
		
		// Default behavior, process the locator by clicking on it
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);
		
		// Click it
		this.zClick(locator);
		
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			page.zWaitForActive();
			page.zWaitForBusyOverlay();
		}

		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		throw new HarnessException("implement me!");
	}

	/**
	 * Check/Uncheck a checkbox (just returns if checkbox already checked)
	 * @param locator The locator for the checkbox
	 * @param status The desired status of the checkbox (true=checked, false=unchecked)
	 * @throws HarnessException 
	 */
	public void zCheckboxSet(String locator, boolean status) throws HarnessException {
		
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException(locator + " no present!");
		}
		
		if ( this.sIsChecked(locator) == status ) {
			logger.debug("checkbox status matched.  not doing anything");
			return;
		}
		
		if ( status == true ) {
			this.sCheck(locator);
		} else {
			this.sUncheck(locator);
		}
		
		this.zWaitForBusyOverlay();
		
	}

	public static class ShareItem {
		public String name = null;
		public String item = null;
		public String type = null;
		public String role = null;
		public String folder = null;
		public String email = null;
		public String with = null;
		
		public ShareItem() {	
		}
		
		public String toString() {
			return (String.format("name:%s with:%s item:%s type:%s role:%s folder:%s email:%s", name, with, item, type, role, folder, email));
		}
		
	}
	
	// See https://bugzilla.zimbra.com/show_bug.cgi?id=65919
	// parseUnacceptedShareItem and parseAcceptedShareItem can
	// likely be combined once unique ID's are added to the DOM
	//
	
	protected ShareItem parseUnacceptedShareItem(String itemLocator) throws HarnessException {
		logger.info(myPageName() + " parseUnacceptedShareItem("+ itemLocator +")");
		
		
		if ( !this.sIsElementPresent(itemLocator) ) {
			throw new HarnessException("item is not present! "+ itemLocator);
		}

		/**

			<!-- Unaccepted -->
			<tr id="DWT398">
				<td width="180">enus13186341202964</td>
				<td id="DWT397_it" width="auto">/Inbox/ownerfolder13186341358436</td>
				<td width="60">Folder</td>
				<td id="DWT397_ro" width="50">Viewer</td>
				<td width="180">
					<a href="javascript:;" onclick='ZmSharingView._handleAcceptLink("DWT397");'>Accept</a>
				</td>
				<td width="180">enus13186341276255@testdomain.com</td>
			</tr>
			
		 */
		
		String locator = null;
		
		ShareItem item = new ShareItem();
		
		locator = itemLocator + " td";
		if ( this.sIsElementPresent(locator) ) {
			item.name = this.sGetText(locator);
		}
		
		locator = itemLocator + " td[id$='_it']";
		if ( this.sIsElementPresent(locator) ) {
			item.item = this.sGetText(locator);
		}
		
		locator = itemLocator + " td + td + td";
		if ( this.sIsElementPresent(locator) ) {
			item.type = this.sGetText(locator);
		}
		
		locator = itemLocator + " td[id$='_ro']";
		if ( this.sIsElementPresent(locator) ) {
			item.role = this.sGetText(locator);
		}
		
		locator = itemLocator + " td + td + td + td + td + td";
		if ( this.sIsElementPresent(locator) ) {
			item.email = this.sGetText(locator);
		}
		
		
		return (item);
	}
	
	protected ShareItem parseAcceptedShareItem(String itemLocator) throws HarnessException {
		logger.info(myPageName() + " parseAcceptedShareItem("+ itemLocator +")");
		
		
		if ( !this.sIsElementPresent(itemLocator) ) {
			throw new HarnessException("item is not present! "+ itemLocator);
		}

		/**

			<!-- Accepted -->
			<tr id="DWT334">
				<td width="180">enus13186341202964</td>
				<td id="DWT333_it" width="auto">/Inbox/ownerfolder13186341358436</td>
				<td width="60">Folder</td>
				<td id="DWT333_ro" width="50">Viewer</td>
				<td id="DWT333_fo" width="150">mountpoint13186341362347</td>
				<td width="180">enus13186341276255@testdomain.com</td>
			</tr>

		 */
		
		String locator = null;
		
		ShareItem item = new ShareItem();
		
		locator = itemLocator + " td";
		if ( this.sIsElementPresent(locator) ) {
			item.name = this.sGetText(locator);
		}
		
		locator = itemLocator + " td[id$='_it']";
		if ( this.sIsElementPresent(locator) ) {
			item.item = this.sGetText(locator);
		}
		
		locator = itemLocator + " td + td + td";
		if ( this.sIsElementPresent(locator) ) {
			item.type = this.sGetText(locator);
		}
		
		locator = itemLocator + " td[id$='_ro']";
		if ( this.sIsElementPresent(locator) ) {
			item.role = this.sGetText(locator);
		}
		
		locator = itemLocator + " td[id$='_fo']";
		if ( this.sIsElementPresent(locator) ) {
			item.folder = this.sGetText(locator);
		}
		
		locator = itemLocator + " td + td + td + td + td + td";
		if ( this.sIsElementPresent(locator) ) {
			item.email = this.sGetText(locator);
		}
		
		
		return (item);
	}
	
	
	protected ShareItem parseSharedByMeShareItem(String itemLocator) throws HarnessException {
		logger.info(myPageName() + " parseAcceptedShareItem("+ itemLocator +")");
		
		
		if ( !this.sIsElementPresent(itemLocator) ) {
			throw new HarnessException("item is not present! "+ itemLocator);
		}

		/**

			<!-- Shared By Me -->
			<tr id="DWT435">
				<td width="180">admin@zqa-069.eng.vmware.com</td>
				<td id="DWT434_it" width="auto">Inbox</td>
				<td width="60">Mail Folder</td>
				<td id="DWT434_ro" width="50">Viewer</td>
				<td width="180">
					<a href="javascript:;" onclick='ZmSharingView._handleShareAction("DWT434", "_handleEditShare");'>Edit</a>
					<a href="javascript:;" onclick='ZmSharingView._handleShareAction("DWT434", "_handleRevokeShare");'>Revoke</a>
					<a href="javascript:;" onclick='ZmSharingView._handleShareAction("DWT434", "_handleResendShare");'>Resend</a>
				</td>
			</tr>

		 */
		
		String locator = null;
		
		ShareItem item = new ShareItem();
		
		locator = itemLocator + " td";
		if ( this.sIsElementPresent(locator) ) {
			item.with = this.sGetText(locator);
		}
		
		locator = itemLocator + " td[id$='_it']";
		if ( this.sIsElementPresent(locator) ) {
			item.item = this.sGetText(locator);
		}
		
		locator = itemLocator + " td + td + td";
		if ( this.sIsElementPresent(locator) ) {
			item.type = this.sGetText(locator);
		}
		
		locator = itemLocator + " td[id$='_ro']";
		if ( this.sIsElementPresent(locator) ) {
			item.role = this.sGetText(locator);
		}		
		
		return (item);
	}
	
	/**
	 * Get a list of share rows from the Preferences->Sharing page
	 * @throws HarnessException 
	 */
	public List<ShareItem> zSharesGetUnaccepted() throws HarnessException {
		logger.info(myPageName() + " zSharesGetUnaccepted()");
		
		List<ShareItem> items = new ArrayList<ShareItem>();
		
		
		String rowsLocator = "css=div[id='zl__SVP__rows'] tr";
		if ( !this.sIsElementPresent(rowsLocator) ) {
			logger.info("No rows - return empty list");
			return (items);
		}
		
		int count = this.sGetCssCount(rowsLocator);
		for (int i = 0; i < count; i++) {
			String itemLocator = "css=div[id='zl__SVP__rows'] tr"+ StringUtils.repeat(" + tr", i);
			ShareItem item = parseUnacceptedShareItem(itemLocator);
			items.add(item);
		}
		
		return (items);
		
	}

	/**
	 * Get a list of share rows from the Preferences->Sharing page
	 * @throws HarnessException 
	 */
	public List<ShareItem> zSharesGetAccepted() throws HarnessException {
		logger.info(myPageName() + " zSharesGetUnaccepted()");
		
		List<ShareItem> items = new ArrayList<ShareItem>();
		
		
		String rowsLocator = "css=div[id='zl__SVM__rows'] tr";
		if ( !this.sIsElementPresent(rowsLocator) ) {
			logger.info("No rows - return empty list");
			return (items);
		}
		
		int count = this.sGetCssCount(rowsLocator);
		for (int i = 0; i < count; i++) {
			String itemLocator = "css=div[id='zl__SVM__rows'] tr"+ StringUtils.repeat(" + tr", i);
			ShareItem item = parseAcceptedShareItem(itemLocator);
			items.add(item);
		}
		
		return (items);
		
	}

	/**
	 * Get a list of share rows from the Preferences->Sharing page
	 * @throws HarnessException 
	 */
	public List<ShareItem> zSharesGetSharedByMe() throws HarnessException {
		logger.info(myPageName() + " zSharesGetUnaccepted()");
		
		List<ShareItem> items = new ArrayList<ShareItem>();
		
		
		String rowsLocator = "css=div[id='zl__SVP__rows'] tr";
		if ( !this.sIsElementPresent(rowsLocator) ) {
			logger.info("No rows - return empty list");
			return (items);
		}
		
		int count = this.sGetCssCount(rowsLocator);
		for (int i = 0; i < count; i++) {
			String itemLocator = "css=div[id='zl__SVG__rows'] tr"+ StringUtils.repeat(" + tr", i);
			ShareItem item = parseSharedByMeShareItem(itemLocator);
			items.add(item);
		}
		
		return (items);
		
	}







}
