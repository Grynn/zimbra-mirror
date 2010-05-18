package projects.zcs;

import projects.zcs.ui.ABApp;
import projects.zcs.ui.ABCompose;
import projects.zcs.ui.AccPref;
import projects.zcs.ui.BriefcaseApp;
import projects.zcs.ui.CalCompose;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.DocumentApp;
import projects.zcs.ui.DocumentCompose;
import projects.zcs.ui.FilterPref;
import projects.zcs.ui.GeneralPrefUI;
import projects.zcs.ui.LoginPage;
import projects.zcs.ui.MailApp;
import projects.zcs.ui.CalApp;
import projects.zcs.ui.Sharing;
import projects.zcs.ui.TaskApp;

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
	public static GeneralPrefUI zGenPrefUI=new GeneralPrefUI();
}
