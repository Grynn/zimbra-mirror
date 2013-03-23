/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.desktop.ui;

import java.awt.event.KeyEvent;

import org.apache.log4j.*;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;


public class PagePrint extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsTab.class);
	



	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public PagePrint(AbsApplication application) {
		super(application);
		
		logger.info("new " + PagePrint.class.getCanonicalName());
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
	    return true;
		//throw new HarnessException("Implement me");
	} 
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	
	public void cancelPrintDialog() throws HarnessException {
		//wait for Print Dialog displayed
		SleepUtil.sleepMedium();
		
		// close Print dialog 
		zKeyboard.zTypeKeyEvent(KeyEvent.VK_ESCAPE);
	
		SleepUtil.sleepSmall();
		
		//switch to Print View
		ClientSessionFactory.session().selenium().selectWindow("title=Zimbra");
	}
	
	
	public boolean isContained(String locator, String message) throws HarnessException {		
		return this.sGetText(locator).contains(message);
	}
}
