package com.zimbra.qa.selenium.projects.ajax.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.ui.AbsTab;

public class TooltipContact extends Tooltip {
	protected static Logger logger = LogManager.getLogger(TooltipContact.class);

	public static class Locators {
	
	}
	
	public TooltipContact(AbsTab tab) {	
		super(tab);
		
		logger.info("new " + this.getClass().getCanonicalName());
	}
	

	@Override
	public String myPageName() {
		return (this.getClass().getCanonicalName());
	}
	
}
