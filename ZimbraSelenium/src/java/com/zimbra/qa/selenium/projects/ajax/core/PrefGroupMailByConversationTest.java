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
public class PrefGroupMailByConversationTest extends AjaxCommonTest {

	public PrefGroupMailByConversationTest() {
		
		super.startingPage = app.zPageMail;

		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -8033517997031535379L;
		{
			put("zimbraPrefGroupMailBy", "conversation");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};

	}
	
}
