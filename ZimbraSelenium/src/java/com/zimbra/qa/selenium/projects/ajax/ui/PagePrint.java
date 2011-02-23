package com.zimbra.qa.selenium.projects.ajax.ui;

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
