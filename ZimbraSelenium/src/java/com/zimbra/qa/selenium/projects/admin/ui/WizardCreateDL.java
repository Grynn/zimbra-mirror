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
package com.zimbra.qa.selenium.projects.admin.ui;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.AbsWizard;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;

public class WizardCreateDL extends AbsWizard {
	public static class Locators {
		public static final String zdlg_DL_NAME = "zdlgv__NEW_DL_name";
		public static final String zdlg_DOMAIN_NAME="zdlgv__NEW_DL_name_2_display";
	}

	public WizardCreateDL(AbsTab page) {
		super(page);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {

		if ( !(item instanceof DistributionListItem) )
			throw new HarnessException("item must be an DistributionListItem, was "+ item.getClass().getCanonicalName());

		DistributionListItem dl = (DistributionListItem)item;

		String CN = dl.getLocalName();
		String domain = dl.getDomainName();


		zType(Locators.zdlg_DL_NAME, CN);
		if(ZimbraSeleniumProperties.isWebDriver()) {
			SleepUtil.sleepSmall();
			this.clearField(Locators.zdlg_DOMAIN_NAME);
		}
		//this.clearField(Locators.zdlg_DOMAIN_NAME);
		zType(Locators.zdlg_DOMAIN_NAME, "");
		zType(Locators.zdlg_DOMAIN_NAME, domain);
		
		this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
		clickFinish(AbsWizard.Locators.DL_DIALOG);

		return (dl);
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

	public void zComplete(DistributionListItem dl) {
		// TODO Auto-generated method stub
		
	}

}
