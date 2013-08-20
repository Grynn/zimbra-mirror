/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
/**
 * 
 */
package com.zimbra.qa.selenium.projects.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.qa.selenium.framework.items.ConversationItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;


/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsTab {

	public static class Locators {
		
		// TODO: Need better locator that doesn't have content text
		public static final String zMailIsActive = "css=a:contains('Folders')";

		public static final String zDList_View = "css=div#dlist-view";

	}
	
	public PageMail(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageMail.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {
		
		// Make sure the main page is active
		if ( !((AppMobileClient)MyApplication).zPageMain.zIsActive() ) {
			((AppMobileClient)MyApplication).zPageMain.zNavigateTo();
		}

		boolean active = this.sIsElementPresent(Locators.zMailIsActive);
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
		if ( !((AppMobileClient)MyApplication).zPageMain.zIsActive() ) {
			((AppMobileClient)MyApplication).zPageMain.zNavigateTo();
		}
		
		// Click on Mail icon
		sClick(PageMain.Locators.zAppbarMail);
		
		zWaitForActive();

	}

	/**
	 * Return a list of all messages in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<MailItem> zListGetMessages() throws HarnessException {
		
		throw new HarnessException("implement me!");

	}

	/**
	 * Return a list of all conversations in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<ConversationItem> zListGetConversations() throws HarnessException {
		List<ConversationItem> items = new ArrayList<ConversationItem>();
		
		if (!sIsElementPresent(Locators.zDList_View))
			throw new HarnessException("Unable to find the message list!");
		
		int count = sGetXpathCount("//div[contains(@id, 'conv')]");
		logger.info(count + " conversations found");

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			
			final String convLocator = "//div[contains(@id, 'conv')]["+ i +"]";
			
			if ( !this.sIsElementPresent(convLocator) ) {
				throw new HarnessException("Can't find conversation row from locator "+ convLocator);
			}

			String locator;
			
			ConversationItem item = new ConversationItem();

			// TODO: Is it checked?

			// TODO: Converstation icon
			
			// From:
			locator = convLocator + "//div[@class='from-span']";
			if ( this.sIsElementPresent(locator) ) {
				item.gFrom = this.sGetText(locator);
			} else {
				item.gFrom = "";
			}

			// Subject:
			locator = convLocator + "//div[@class='sub-span']";
			if ( this.sIsElementPresent(locator) ) {
				item.gSubject = this.sGetText(locator);
			} else {
				item.gSubject = "";
			}

			// From:
			locator = convLocator + "//div[@class='fragment-span']";
			if ( this.sIsElementPresent(locator) ) {
				item.gFragment = this.sGetText(locator);
			} else {
				item.gFragment = "";
			}


			
			// Add the new item to the list
			items.add(item);
			logger.info(item.prettyPrint());
		}
		
		return (items);
	}


	/**
	 * Refresh the inbox list by clicking "Get Mail"
	 * @throws HarnessException 
	 */
	public void zRefresh() throws HarnessException {
		this.sClick(PageMain.Locators.zAppbarContact);
		SleepUtil.sleepMedium();

		this.sClick(PageMain.Locators.zAppbarMail);
		SleepUtil.sleepMedium();

	}

	@Override
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
			throws HarnessException {
		throw new HarnessException("Mobile page does not have context menu");
	}	
	
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}


}
