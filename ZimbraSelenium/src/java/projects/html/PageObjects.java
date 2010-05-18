package projects.html;

import projects.html.ui.ComposeView;

import projects.html.ui.ABComposeHtml;
import projects.html.ui.AccPref;
import projects.html.ui.CalFolderApp;
import projects.html.ui.ComposePrefUI;
import projects.html.ui.GeneralPrefUI;
import projects.html.ui.LoginPage;
import projects.html.ui.MailApp;
import projects.html.ui.MailPrefUI;
import projects.html.ui.TaskApp;
import projects.html.ui.CalendarApp;

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
