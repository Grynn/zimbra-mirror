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
/**
 * 
 */
package com.zimbra.qa.selenium.framework.ui;

import com.zimbra.qa.selenium.framework.util.HarnessException;



/**
 * A <code>AbsDialogSeparateWindow</code> object represents a "popup dialog", 
 * such as a new folder, new tag, error message, etc. - in a separate window
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsDialogSeparateWindow extends AbsSeparateWindow {
	
	protected AbsSeparateWindow MyWindow = null;
	
	public AbsDialogSeparateWindow(AbsApplication application, AbsSeparateWindow window) {
		super(application);
		
		MyWindow = window;
		
		logger.info("new "+ AbsDialogSeparateWindow.class.getCanonicalName());

	}
	
	public AbsPage zClickButton(Button button) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Check if this dialog is active.
	 * First, check that the separate window is active.
	 * Second, check if the dialog is visible.
	 * 
	 */
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		if ( !MyWindow.zIsActive() ) {
			logger.debug("separate window is not active");
			return (false);
		}
		
		// Define whether this dialog is active in the extending class
		
		return (true);
	}
	
}
