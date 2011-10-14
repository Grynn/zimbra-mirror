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
	
	public Tooltip(AbsTab tab) {	
		super(tab);
		
		logger.info("new " + this.getClass().getCanonicalName());
	}
	
	public String zGetContents() 
	throws HarnessException 
	{
		logger.info(myPageName() + " zGetContents()");

		return (MyTab.sGetText(Locators.TooltipContentsCSS));
	}
	
	
	public boolean zIsActive() 
	throws HarnessException
	{
		logger.info(myPageName() + " zIsVisible()");
		
		boolean present = MyTab.sIsElementPresent(Locators.DwtToolTipCSS);
		if ( !present )
			return (false);
		
		boolean visible = MyTab.zIsVisiblePerPosition(Locators.DwtToolTipCSS, 0, 0);
		if ( !visible )
			return (false);
		
		return (true);
		
	}

	@Override
	public String myPageName() {
		return (this.getClass().getCanonicalName());
	}
	
}
