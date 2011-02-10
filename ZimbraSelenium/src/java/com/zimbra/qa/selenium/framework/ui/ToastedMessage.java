package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class ToastedMessage extends AjaxCommonTest  {
	private static String locator= "css=div[id='z_toast_text']";
	protected static Logger logger = LogManager.getLogger(ToastedMessage.class);

    
    public static boolean isContainedText(String text) {
    	String seleniumText = ClientSessionFactory.session().selenium().getText(locator);
		logger.info("DefaultSelenium.getText(" + locator + ") = " + seleniumText);        
    	return seleniumText.contains(text);    
    }
    
    public static boolean isContainedLink(String innerText) {
    	//TODO fill in later
    	return true;
    }
	
    public static boolean isVisible() {
    	//TODO fill in later
    	return true;
    }

    public static boolean clickLink(String innerText) {
    	return true;
    }
}
