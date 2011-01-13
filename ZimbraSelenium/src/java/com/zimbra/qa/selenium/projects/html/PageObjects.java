package com.zimbra.qa.selenium.projects.html;

import com.zimbra.qa.selenium.projects.html.ui.ABComposeHtml;
import com.zimbra.qa.selenium.projects.html.ui.AccPref;
import com.zimbra.qa.selenium.projects.html.ui.CalFolderApp;
import com.zimbra.qa.selenium.projects.html.ui.CalendarApp;
import com.zimbra.qa.selenium.projects.html.ui.ComposePrefUI;
import com.zimbra.qa.selenium.projects.html.ui.ComposeView;
import com.zimbra.qa.selenium.projects.html.ui.GeneralPrefUI;
import com.zimbra.qa.selenium.projects.html.ui.LoginPage;
import com.zimbra.qa.selenium.projects.html.ui.MailApp;
import com.zimbra.qa.selenium.projects.html.ui.MailPrefUI;
import com.zimbra.qa.selenium.projects.html.ui.TaskApp;


public class PageObjects {
	public static LoginPage zLoginpage = new LoginPage();
	public static ComposeView zComposeView = new ComposeView();
	public static MailApp zMailApp = new MailApp();
	public static ABComposeHtml zABComposeHTML = new ABComposeHtml();
	public static TaskApp zTaskApp = new TaskApp();
	public static CalendarApp zCalendarApp = new CalendarApp();
	public static CalFolderApp zCalFolderApp = new CalFolderApp();
	public static AccPref zAccPref = new AccPref();
	public static GeneralPrefUI zGeneralPrefUI = new GeneralPrefUI();
	public static MailPrefUI zMailPrefUI = new MailPrefUI();
	public static ComposePrefUI zComposePrefUI = new ComposePrefUI();
}
