package com.zimbra.qa.selenium.projects.zcs;

import com.zimbra.qa.selenium.projects.zcs.ui.ABApp;
import com.zimbra.qa.selenium.projects.zcs.ui.ABCompose;
import com.zimbra.qa.selenium.projects.zcs.ui.AccPref;
import com.zimbra.qa.selenium.projects.zcs.ui.BriefcaseApp;
import com.zimbra.qa.selenium.projects.zcs.ui.CalApp;
import com.zimbra.qa.selenium.projects.zcs.ui.CalCompose;
import com.zimbra.qa.selenium.projects.zcs.ui.ComposeView;
import com.zimbra.qa.selenium.projects.zcs.ui.DocumentApp;
import com.zimbra.qa.selenium.projects.zcs.ui.DocumentCompose;
import com.zimbra.qa.selenium.projects.zcs.ui.FilterPref;
import com.zimbra.qa.selenium.projects.zcs.ui.GeneralPrefUI;
import com.zimbra.qa.selenium.projects.zcs.ui.LoginPage;
import com.zimbra.qa.selenium.projects.zcs.ui.MailApp;
import com.zimbra.qa.selenium.projects.zcs.ui.Sharing;
import com.zimbra.qa.selenium.projects.zcs.ui.SignaturePref;
import com.zimbra.qa.selenium.projects.zcs.ui.TaskApp;

public class PageObjects {
	public static LoginPage zLoginpage = new LoginPage();
	public static ComposeView zComposeView = new ComposeView();
	public static MailApp zMailApp = new MailApp();
	public static BriefcaseApp zBriefcaseApp = new BriefcaseApp();
	public static CalApp zCalApp = new CalApp();
	public static CalCompose zCalCompose = new CalCompose();
	public static TaskApp zTaskApp = new TaskApp();
	public static ABApp zABApp = new ABApp();
	public static ABCompose zABCompose = new ABCompose();
	public static DocumentCompose zDocumentCompose = new DocumentCompose();
	public static DocumentApp zDocumentApp = new DocumentApp();
	public static Sharing zSharing = new Sharing();
	public static FilterPref zFilterPreferences = new FilterPref();
	public static AccPref zAccPref = new AccPref();
	public static GeneralPrefUI zGenPrefUI = new GeneralPrefUI();
	public static SignaturePref zSignaturePref = new SignaturePref();
}
