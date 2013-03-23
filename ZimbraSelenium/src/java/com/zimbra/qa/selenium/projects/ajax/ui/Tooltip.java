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
package com.zimbra.qa.selenium.projects.ajax.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class Tooltip extends AbsTooltip {
	protected static Logger logger = LogManager.getLogger(Tooltip.class);

	public static class Locators {
	
		public static final String DwtToolTipId = "DwtToolTip";
		public static final String DwtToolTipCSS = "css=div[class='DwtToolTip']";
		
		public static final String TooltipContentsId = "tooltipContents";
		public static final String TooltipContentsCSS = "css=div[class='DwtToolTip'] div[id='tooltipContents']";
		
		
	}
	
	public Tooltip(AbsApplication application) {	
		super(application);
		
		logger.info("new " + this.getClass().getCanonicalName());
	}
	
	public String zGetContents() 
	throws HarnessException 
	{
		logger.info(myPageName() + " zGetContents()");

		return (this.sGetText(Locators.TooltipContentsCSS));
	}
	
	
	public boolean zIsActive() 
	throws HarnessException
	{
		logger.info(myPageName() + " zIsVisible()");
		
		boolean present = this.sIsElementPresent(Locators.DwtToolTipCSS);
		if ( !present )
			return (false);
		
		boolean visible = this.zIsVisiblePerPosition(Locators.DwtToolTipCSS, 0, 0);
		if ( !visible )
			return (false);
		
		return (true);
		
	}

	@Override
	public String myPageName() {
		return (this.getClass().getCanonicalName());
	}
	
}
