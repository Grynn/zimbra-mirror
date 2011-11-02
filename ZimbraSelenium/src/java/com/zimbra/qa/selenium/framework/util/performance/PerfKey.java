package com.zimbra.qa.selenium.framework.util.performance;


/**
 * A list of perf metrics that can be measured.
 * See http://bugzilla.zimbra.com/show_bug.cgi?id=61972
 * See http://bugzilla.zimbra.com/attachment.cgi?id=33941
 * @author Matt Rhoades
 *
 */
public class PerfKey {

	public static final PerfKey ZmMailApp 			= new PerfKey("ZmMailApp", "ZmMailApp_launched", "ZmMailApp_loaded");
	public static final PerfKey ZmMailItem 			= new PerfKey("ZmMailItem", "ZmMailItem_loading", "ZmMailItem_loaded");
	public static final PerfKey ZmContactsApp 		= new PerfKey("ZmContactsApp", "ZmContactsApp_launched", "ZmContactsApp_loaded");
	public static final PerfKey ZmContactsItem 		= new PerfKey("ZmContactsItem", "ZmContactItem_loading", "ZmContactItem_loaded");
	public static final PerfKey ZmCalendarApp 		= new PerfKey("ZmCalendarApp", "ZmCalendarApp_launched", "ZmCalendarApp_loaded");
	public static final PerfKey ZmCalWorkWeekView 	= new PerfKey("ZmCalWorkWeekView", "ZmCalWorkWeekView_loading", "ZmCalWorkWeekView_loaded");
	public static final PerfKey ZmCalItemView 		= new PerfKey("ZmCalItemView", "ZmCalItemView_loading", "ZmCalItemView_loaded");
	public static final PerfKey ZmCalViewItem 		= new PerfKey("ZmCalViewItem", "ZmCalViewItem_loading", "ZmCalViewItem_loaded");
	public static final PerfKey ZmTasksApp 			= new PerfKey("ZmTasksApp", "ZmTasksApp_launched", "ZmTasksApp_loaded");
	public static final PerfKey ZmTaskItem 			= new PerfKey("ZmTaskItem", "ZmTaskItem_loading", "ZmTaskItem_loaded");
	public static final PerfKey ZmBriefcaseApp 		= new PerfKey("ZmBriefcaseApp", "ZmBriefcaseApp_launched", "ZmBriefcaseApp_loaded");
	public static final PerfKey ZmBriefcaseItem 	= new PerfKey("ZmBriefcaseItem", "ZmBriefcaseItem_loading", "ZmBriefcaseItem_loaded");
	
	protected String Key;
	protected String LaunchKey;
	protected String FinishKey;
	
	public PerfKey(String key, String launch, String finish) {
		Key = key;
		LaunchKey = launch;
		FinishKey = finish; 
	}
	
	public String toString() {
		return (Key);
	}

}
