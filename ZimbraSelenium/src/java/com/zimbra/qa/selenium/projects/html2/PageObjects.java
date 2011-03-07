package com.zimbra.qa.selenium.projects.html2;

import com.zimbra.qa.selenium.projects.html2.ui.ABComposeHtml;
import com.zimbra.qa.selenium.projects.html2.ui.AccPref;
import com.zimbra.qa.selenium.projects.html2.ui.CalFolderApp;
import com.zimbra.qa.selenium.projects.html2.ui.CalendarApp;
import com.zimbra.qa.selenium.projects.html2.ui.ComposePrefUI;
import com.zimbra.qa.selenium.projects.html2.ui.ComposeView;
import com.zimbra.qa.selenium.projects.html2.ui.GeneralPrefUI;
import com.zimbra.qa.selenium.projects.html2.ui.LoginPage;
import com.zimbra.qa.selenium.projects.html2.ui.MailApp;
import com.zimbra.qa.selenium.projects.html2.ui.MailPrefUI;
import com.zimbra.qa.selenium.projects.html2.ui.TaskApp;


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
