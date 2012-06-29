package com.zimbra.qa.selenium.projects.ajax.core;

import java.util.HashMap;

public class FeatureBriefcaseTest extends AjaxCommonTest {

	public FeatureBriefcaseTest() {
		
	    super.startingPage = app.zPageMail;

	    super.startingAccountPreferences = new HashMap<String, String>() {
		
		private static final long serialVersionUID = -4746013883105449655L;
		
		{
			put("zimbraFeatureBriefcasesEnabled", "TRUE");
		}};
	}
}
