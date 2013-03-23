/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;


public class FormEditResource extends AbsForm {

	public static class TreeItem {
		public static final String PROPERTIES="Properties";
	}
	
	public static class Locators {
		public static final String NAME_TEXT_BOX="css=input#ztabv__RES_EDIT_";
		public static final String SAVE_BUTTON="css=td[id^='zb__ZaCurrentAppBar__SAVE']";
		public static final String CLOSE_BUTTON="css=td[id^='zb__ZaCurrentAppBar__CLOSE']";
	}

	public FormEditResource(AbsApplication application) {
		super(application);
		
		logger.info("new " + myPageName());

	}

	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the Admin Console is loaded in the browser
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Admin Console application is not active!");

		
		boolean present = sIsElementPresent("");
		if ( !present ) {
			return (false);
		}
		
		String attrs = sGetAttribute("");
		if ( !attrs.contains("ZSelected") ) {
			return (false);
		}

		return (true);
		
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zSubmit() throws HarnessException {
		sClickAt(Locators.SAVE_BUTTON,"");
		sClickAt(Locators.CLOSE_BUTTON,"");
	}
	
	public void zClickTreeItem(String treeItem) throws HarnessException {
		sClickAt("css=td:contains('" + treeItem + "')", "");
	}
	
	public void setName(String name) throws HarnessException {
		for(int i=10;i>=2;i--) {
			if(sIsElementPresent(Locators.NAME_TEXT_BOX+i+"_name_3")) {
				sType(Locators.NAME_TEXT_BOX+i+"_name_3", name);
				return;
			}
		}
		sType(Locators.NAME_TEXT_BOX+"name_3", name);
		}
	}

