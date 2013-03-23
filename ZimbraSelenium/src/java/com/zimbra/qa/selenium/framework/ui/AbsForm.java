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
package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * A <code>AbsForm</code> object represents a "compose page", 
 * such as a new message, new contact, new appointment, new document, etc.
 * <p>
 * Form objects are usually returned after clicking NEW from the toolbar.
 * <p>
 * As a shortcut, form objects take a {@link ZimbraItem} object in the 
 * {@link AbsForm#zFill(ZimbraItem)} and attempts to fill in the form
 * automatically based on the item's previously set properties.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsForm extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsForm.class);


	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsForm(AbsApplication application) {
		super(application);
		
		logger.info("new AbsForm");
	}
	
	/**
	 * Fill out the form (but don't submit)
	 * @throws HarnessException on error
	 */
	public abstract void zFill(IItem item) throws HarnessException;
	
	
	/**
	 * Click on "submit" button
	 * @throws HarnessException on error
	 */
	public abstract void zSubmit() throws HarnessException;
	
	
	/**
	 * Fill and submit the form
	 * @throws HarnessException on error
	 */
	public void zComplete(IItem item) throws HarnessException {
		zFill(item);
		zSubmit();
	}
	
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	
}
