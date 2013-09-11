/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.ui.search;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;

public class DisplayMail extends com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail {

	public DisplayMail(AbsApplication application) {
		super(application);
		
		logger.info("new " + DisplayMail.class.getCanonicalName());
	}

	
	@Override
	public boolean zIsActive() throws HarnessException {
		//logger.warn("implement me", new Throwable());
		zWaitForZimlets();
		
		// Determine which <div/> contains this preview
		// Use this 'top' css for all subsequent parsing
		// zv__TV-SR-Mail-1__MSG
		
		if ( this.zIsVisiblePerPosition("css=div[id^='zv__TV-SR-Mail-'][id$='__MSG']", 0, 0) ) {
			
			int count = this.sGetCssCount("css=div[id^='zv__TV-SR-Mail-'][id$='__MSG']");
			if ( count > 1 ) {
				throw new HarnessException("Too many message views open: "+ count);
			}
			ContainerLocator = "css=div#" + this.sGetAttribute("css=div[id^='zv__TV-SR-Mail-'][id$='__MSG']" + "@id");
			
//		} else if ( this.zIsVisiblePerPosition("css=div[id^='zv__TV-SR-Mail']", 0, 0)) {
//			
//			zv__TV-SR-Mail-1__MSG
//			
//			if ( this.zIsVisiblePerPosition(Locators.MessageViewPreviewAtBottomCSS, 0, 0) ) {
//				ContainerLocator = Locators.MessageViewPreviewAtBottomCSS;
//			} else if ( this.zIsVisiblePerPosition(Locators.MessageViewPreviewAtRightCSS, 0, 0) ) {
//				ContainerLocator = Locators.MessageViewPreviewAtRightCSS;
//			} else {
//				throw new HarnessException("Unable to determine the current open view");				
//			}
//			
//		} else if ( this.zIsVisiblePerPosition("css=div[id^='zv__CLV-SR-Mail']", 0, 0) ) {
//			
//			if ( this.zIsVisiblePerPosition(Locators.ConversationViewPreviewAtBottomCSS, 0, 0) ) {
//				ContainerLocator = Locators.ConversationViewPreviewAtBottomCSS;
//			} else if ( this.zIsVisiblePerPosition(Locators.ConversationViewPreviewAtRightCSS, 0, 0) ){
//				ContainerLocator = Locators.ConversationViewPreviewAtRightCSS;
//			} else {
//				throw new HarnessException("Unable to determine the current open view");
//			}
			
		} else {
			
			throw new HarnessException("Unable to determine the current open view");
			
		}
		

		return (sIsElementPresent(this.ContainerLocator) );
				
	}

	
	public HtmlElement zGetMailPropertyAsHtml(Field field) throws HarnessException {

		String source = null;

		if ( field == Field.Body) {

			try {

				this.sSelectFrame("css=iframe[id^='zv__TV-SR-Mail'][id$='__MSG__body__iframe']");

				source = this.sGetHtmlSource();

				// For some reason, we don't get the <html/> tag.  Add it
				source = "<html>" + source + "</html>";

			} finally {
				// Make sure to go back to the original iframe
				this.sSelectFrame("relative=top");
			}

		} else {
			throw new HarnessException("not implemented for field "+ field);
		}

		// Make sure source was found
		if ( source == null )
			throw new HarnessException("source was null for "+ field);

		logger.info("DisplayMail.zGetMailPropertyAsHtml() = "+ HtmlElement.clean(source).prettyPrint());

		// Clean up the HTML code to be valid
		return (HtmlElement.clean(source));


	}

}
