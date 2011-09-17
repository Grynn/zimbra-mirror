package com.zimbra.qa.selenium.projects.ajax.core;

import java.util.HashMap;

/**
 * A base class that sets
 *  1) the starting page to be the mail app,
 *  2) sets zimbraPrefGroupMailBy=message, and
 *  3) sets zimbraPrefMessageViewHtmlPreferred=TRUE
 *  
 * @author Matt Rhoades
 *
 */
public class PrefGroupMailByMessageTest extends AjaxCommonTest {

	public PrefGroupMailByMessageTest() {
		
		super.startingPage = app.zPageMail;

		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 3370780885378699878L;
		{
			put("zimbraPrefGroupMailBy", "message");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};

	}
	
}
