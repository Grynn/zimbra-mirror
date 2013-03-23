/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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

import com.zimbra.qa.selenium.framework.util.*;

/**
 * A <code>AbsTooltip</code> object represents a popup tooltip
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsTooltip extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsTooltip.class);


	/**
	 * Create this Tooltip object that exists in the specified page
	 * @param application
	 */
	protected AbsTooltip(AbsApplication application) {		
		super(application);

		logger.info("new " + this.getClass().getCanonicalName());
		
	}
	
	/**
	 * Get the text contents of the tooltip
	 * @return
	 * @throws HarnessException
	 */
	public abstract String zGetContents() throws HarnessException;
	
	/**
	 * Determine if the tooltip is currently visible
	 * @return
	 * @throws HarnessException
	 */
	public abstract boolean zIsActive() throws HarnessException;
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();

}
