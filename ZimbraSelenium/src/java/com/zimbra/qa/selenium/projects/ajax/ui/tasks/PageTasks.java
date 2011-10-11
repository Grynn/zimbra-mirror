/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.tasks;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAssistant;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.PageMain;


/**
 * @author Matt Rhoades
 * 
 */
public class PageTasks extends AbsTab {

	public static class Locators {

		public static final String zl__TKL__rowsID = "zl__TKL__rows";
		public static final String _newTaskBannerID = "_newTaskBannerId";
		public static final String _upComingTaskListHdrID = "_upComingTaskListHdr";
		public static final String zli__TKL__ = "zli__TKL__"; // Each task item:
		// <div
		// id='zli__TKL__<item
		// id>' .../>
		public static final String zb__TKE1__SAVE_left_icon = "zb__TKE1__SAVE_left_icon";
		public static final String taskListView = "css=div[id='zl__TKL__rows'][class='DwtListView-Rows']";
		public static final String zTasksTab = "zb__App__Tasks";
		public static final String zNewTask = "zb__TKL__NEW_MENU_left_icon";
		public static final String zNewTaskDropDown = "css=td[id='zb__TKL__NEW_MENU_dropdown']>div";
		public static final String zNewTagMenuItem= "css=td[id$='__TKL__NEW_MENU_NEW_TAG_title']";
		public static final String zMarkAsCompleted = "css=div#zb__TKL__MARK_AS_COMPLETED";
		public static final String zNewTaskMenuItem ="css=div#zb__TKL__NEW_MENU_NEW_TASK";
		public static final String zNewTaskFolderMenuItem ="css=div#zb__TKL__NEW_MENU_NEW_TASK_FOLDER";
		public static final String zDeleteTaskMenuItem ="css=div[id='zm__Tasks'] tr[id='POPUP_DELETE']";
		public static final String zMoveTaskMenuItem ="css=div[id='zm__Tasks'] tr[id='POPUP_MOVE']";
		public static final String zNewTaskListMenuItem="css=div[id$='NEWFOLDER']";
		public static final String zMoveTaskDropDown="css=td#zb__TKL__MOVE_MENU_dropdown>div";
		public static final String zEditTaskMenuItem ="css=div[id='zm__Tasks'] tr[id='POPUP_EDIT']";
		public static final String zFilterByTaskDropDown="css=tr[id='ztb__TKL_items'] div[id='zb__TKL__SORTBY_MENU'] td[id='zb__TKL__SORTBY_MENU_dropdown']>div";
		public static final String zToDoListTaskMenuItem ="css=div[id^='POPUP_DWT'] div[id^='DWT'] tr[id='POPUP_TKVT']";
	}

	public PageTasks(AbsApplication application) {
		super(application);

		logger.info("new " + PageTasks.class.getCanonicalName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();
		}

		String id = "zb__App__Tasks";
		String rowLocator = "css=div[id='" + Locators.zl__TKL__rowsID + "']>div";

		boolean loaded = this.sIsElementPresent(rowLocator);
		if (!loaded)
			return (false);

		//	String selected = this.sGetAttribute("xpath=(//div[@id='" + locator + "'])@class");
		//	return (selected.contains("ZSelected"));

		return(this.sIsElementPresent("css=div[id='"+ id +"'][class*=ZSelected]"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {

		// Check if this page is already active.
		if (zIsActive()) {
			return;
		}

		// Make sure we are logged into the Mobile app
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();
		}

		tracer.trace("Navigate to "+ this.myPageName());

		this.zClickAt(PageMain.Locators.zAppbarTasks, "");

		this.zWaitForBusyOverlay();

		zWaitForActive();

	}
	public boolean isPresent(String itemName) throws HarnessException {
		String itemLocator = Locators.taskListView
		+ " td[width*='auto']:contains(" + itemName + ")";

		zWaitForElementPresent(itemLocator);
		return true;
	}


	@Override
	public AbsPage zListItem(Action action, String subject)
	throws HarnessException {
		logger.info(myPageName() + " zListItem(" + action + ", " + subject
				+ ")");

		tracer.trace(action +" on subject = "+ subject);

		if (action == null)
			throw new HarnessException("action cannot be null");

		if ((subject == null) || (subject.trim().length() == 0))
			throw new HarnessException("subject cannot be null or empty");

		AbsPage page = null;
		String itemLocator = null;

		// How many items are in the table?
		// findTask(subject);
		String rowLocator = "css=div[id='" + Locators.zl__TKL__rowsID + "']>div";
		int count = this.sGetCssCount(rowLocator);
		logger.debug(myPageName() + " zListItem: number of rows: " + count);

		if ( count < 1 ) 
			throw new HarnessException("No tasks in the list!");

		// Get each conversation's data from the table list
		itemLocator = rowLocator + ":first-child";
		for (int i = 1; i < count; i++) {
			
			itemLocator = itemLocator + " + div ";
			if ( !this.sIsElementPresent(itemLocator) )
				throw new HarnessException("Item Locator not present: "+ itemLocator);

			String id;
			try {
				id = this.sGetAttribute(itemLocator + "@id");
				if ( id == null )
					throw new HarnessException("id was null: "+ itemLocator + "@id");
				if ( !id.startsWith(Locators.zli__TKL__) )
					continue; // _newTaskBannerId, etc.

			} catch (SeleniumException e) {
				logger.warn("No ID on item: "+ itemLocator);
				continue;
			}

			String subjectLocator = "css=div[id='"+ id +"'] td[id$='_su']";
			if ( !this.sIsElementPresent(subjectLocator) )
				throw new HarnessException("Subject Locator not present: "+ subjectLocator);
			
			String itemSubject = this.sGetText(subjectLocator);
			if ((itemSubject == null) || (itemSubject.trim().length() == 0)) {
				logger.debug("found empty task subject");
				continue;
			}
			if(!itemSubject.equals(subject)){
				continue;
			}
			if (itemSubject.equals(subject)) {
				// Found it
				break;
			}
			
			itemLocator = null;
		}

		if (itemLocator == null) {
			throw new HarnessException("Unable to locate item with subject(" + subject + ")");
		}

		
		if (action == Action.A_LEFTCLICK) {

			// Left-Click on the item
			this.zClick(itemLocator);

			this.zWaitForBusyOverlay();

			// Return the displayed mail page object
			page = new DisplayTask(MyApplication);

			// FALL THROUGH

		} else if (action == Action.A_CTRLSELECT) {

			throw new HarnessException("implement me!  action = " + action);

		} else if (action == Action.A_SHIFTSELECT) {

			throw new HarnessException("implement me!  action = " + action);

		} else if (action == Action.A_RIGHTCLICK) {

			// Right-Click on the item
			this.zRightClick(itemLocator);

			// Return the displayed mail page object
			page = new ContextMenu(MyApplication);

			// FALL THROUGH

		} else if (action == Action.A_MAIL_CHECKBOX) {

			String selectlocator = itemLocator + " div[id$='__se']";
			if (!this.sIsElementPresent(selectlocator))
				throw new HarnessException("Checkbox locator is not present "
						+ selectlocator);

			if (this.sIsElementPresent(selectlocator +"[class*=ImgCheckboxChecked]"))
				throw new HarnessException("Trying to check box, but it was already enabled");

			// Left-Click on the flag field

			this.zClick(selectlocator);
			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else if (action == Action.A_MAIL_UNCHECKBOX) {

			String selectlocator = itemLocator + " div[id$='__se']";
			if (!this.sIsElementPresent(selectlocator))
				throw new HarnessException("Checkbox locator is not present "
						+ selectlocator);

			if (this.sIsElementPresent(selectlocator +"[class*=ImgCheckboxChecked]"))
				throw new HarnessException("Trying to uncheck box, but it was already disabled");

			// Left-Click on the flag field

			this.zClick(selectlocator);
			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else {
			throw new HarnessException("implement me!  action = " + action);
		}

		if (page != null) {
			page.zWaitForActive();
		}

		// default return command
		return (page);

	}
	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
	throws HarnessException {
		tracer.trace(action +" then "+ option + "," + subOption + " on item = "+ item);

		throw new HarnessException("implement me!");
	}
	
	@Override
	public AbsPage zListItem(Action action, Button option, String subject)
	throws HarnessException {
		logger.info(myPageName() + " zListItem(" + action + ", " + option
				+ ", " + subject + ")");

		tracer.trace(action +" then "+ option +" on subject = "+ subject);

		if (action == null)
			throw new HarnessException("action cannot be null");
		if (option == null)
			throw new HarnessException("button cannot be null");
		if (subject == null || subject.trim().length() == 0)
			throw new HarnessException("subject cannot be null or blank");

		String itemLocator = null;
		AbsPage page = null;

		
		// How many items are in the table?
		// findTask(subject);
		String rowLocator = "css=div[id='" + Locators.zl__TKL__rowsID + "']>div";
		int count = this.sGetCssCount(rowLocator);
		logger.debug(myPageName() + " zListItem: number of rows: " + count);

		if ( count < 1 ) 
			throw new HarnessException("No tasks in the list!");

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			
			itemLocator = rowLocator + ":nth-of-type("+ i +")";
			if ( !this.sIsElementPresent(itemLocator) )
				throw new HarnessException("Item Locator not present: "+ itemLocator);

			String id;
			try {
				id = this.sGetAttribute(itemLocator + "@id");
				if ( id == null )
					throw new HarnessException("id was null: "+ itemLocator + "@id");
				if ( !id.startsWith(Locators.zli__TKL__) )
					continue; // _newTaskBannerId, etc.

			} catch (SeleniumException e) {
				logger.warn("No ID on item: "+ itemLocator);
				continue;
			}

			String subjectLocator = "css=div[id='"+ id +"'] td[id$='_su']";
			if ( !this.sIsElementPresent(subjectLocator) )
				throw new HarnessException("Subject Locator not present: "+ subjectLocator);
			
			String itemSubject = this.sGetText(subjectLocator);
			if ((itemSubject == null) || (itemSubject.trim().length() == 0)) {
				logger.debug("found empty task subject");
				continue;
			}
			
			if (itemSubject.equals(subject)) {
				// Found it
				break;
			}
			
			itemLocator = null;
		}

		if (itemLocator == null) {
			throw new HarnessException("Unable to locate item with subject(" + subject + ")");
		}


		if (action == Action.A_RIGHTCLICK) {

			// Right-Click on the item
			this.zRightClickAt(itemLocator,"");

			// Now the ContextMenu is opened
			// Click on the specified option

			String optionLocator = null;

			if (option == Button.B_DELETE) {

				// <div id="zmi__Tasks__DELETE" ...
				//optionLocator = "zmi__Tasks__DELETE";
				optionLocator= Locators.zDeleteTaskMenuItem;
				page = null;

			}else if(option == Button.O_MOVE_MENU){
				optionLocator= Locators.zMoveTaskMenuItem;
				page = new DialogMove(MyApplication, this);
			
			}else if(option == Button.O_EDIT){
				optionLocator= Locators.zEditTaskMenuItem;
				page = new FormTaskNew(this.MyApplication);
			
			}else {
				throw new HarnessException("implement action:" + action
						+ " option:" + option);
			}

			// click on the option
			this.zClickAt(optionLocator,"");

			this.zWaitForBusyOverlay();

			// FALL THROUGH

		} else {
			throw new HarnessException("implement me!  action = " + action);
		}

		if (page != null) {
			page.zWaitForActive();
		}

		// Default behavior
		return (page);

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the "+ button +" button");

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//
		if (button == Button.B_REFRESH) {

			return (((AppAjaxClient)this.MyApplication).zPageMain.zToolbarPressButton(Button.B_REFRESH));

		} else if (button == Button.B_NEW) {

			// New button
			locator = Locators.zNewTask;

			page = new FormTaskNew(this.MyApplication);


		} else if (button == Button.B_EDIT) {

			locator = "zb__TKL__EDIT_left_icon";

			// Check if the button is enabled
			if (this.sIsElementPresent("css=td#" + locator + " div[class*=ZDisabledImage]")){ 
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " );}

			page = new FormTaskNew(this.MyApplication);

		} else if (button == Button.B_DELETE) {

			locator = "zb__TKL__DELETE_left_icon";

			// Check if the button is enabled
			if (this.sIsElementPresent("css=td#" + locator + " div[class*=ZDisabledImage]")){ 
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " );}


		} else if (button == Button.B_MOVE) {

			locator = "zb__TKL__MOVE_left_icon";

			// Check if the button is enabled
			if (this.sIsElementPresent("css=td#" + locator + " div[class*=ZDisabledImage]")){ 
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " );}

			page = new DialogMove(this.MyApplication,this);

			// FALL THROUGH

		} else if (button == Button.B_PRINT) {

			locator = "zb__TKL__PRINT_left_icon";

			// Check if the button is enabled
			if (this.sIsElementPresent("css=td#" + locator + " div[class*=ZDisabledImage]")){ 
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " );}


			page = null; // TODO
			throw new HarnessException("implement Print dialog");

		} else if (button == Button.B_SAVE) {
			locator = "css=div[id^='ztb__TKE'] div[id$='__SAVE']";
			page = null;
			//page = new FormTaskNew(this.MyApplication);

		} else if (button == Button.B_TAG) {

			// For "TAG" without a specified pulldown option, just click on the
			// pulldown
			// To use "TAG" with a pulldown option, see
			// zToolbarPressPulldown(Button, Button)
			//

			locator = "zb__TKL__TAG_MENU_dropdown";

			// Check if the button is enabled
			if (this.sIsElementPresent("css=td#" + locator + " div[class*=ZDisabledImage]")){ 
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " );}


		} else if (button == Button.B_TASK_FILTERBY) {
			throw new HarnessException("implement me");
		} else if (button == Button.B_TASK_MARKCOMPLETED) {
			locator= Locators.zMarkAsCompleted;		
			page = null;
			
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClickAt(locator,"");

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if (page != null) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}

		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
	throws HarnessException {

		tracer.trace("Click pulldown "+ pulldown +" then "+ option);
		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");
		// Default behavior variables

		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_TAG) {
			if (option == Button.O_TAG_NEWTAG) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				//optionLocator = "css=td[id$='__TAG_MENU|MENU|NEWTAG_title']";
				optionLocator="css=div[id='zb__TKL__TAG_MENU|MENU'] div[id='tasks_newtag']";

				page = new DialogTag(this.MyApplication, this);

				// FALL THROUGH
			} else if (option == Button.O_TAG_REMOVETAG) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				//optionLocator = "css=td[id$='__TAG_MENU|MENU|REMOVETAG_title']";
				optionLocator="css=div[id='zb__TKL__TAG_MENU|MENU'] div[id='tasks_removetag']";
				

				page = null;

				// FALL THROUGH
			} else {
				throw new HarnessException(	"no logic defined for pulldown/option " + pulldown+ "/" + option);
			}
		} else if (pulldown== Button.B_NEW) {

			if(option == Button.O_NEW_TAG){

				pulldownLocator = Locators.zNewTaskDropDown;
				optionLocator= Locators.zNewTagMenuItem;

				page = new DialogTag(this.MyApplication, this);
			}else if(option==Button.O_NEW_TASK){
				
				pulldownLocator = Locators.zNewTaskDropDown;
				optionLocator= Locators.zNewTaskMenuItem;
				
				page = new FormTaskNew(this.MyApplication);
				
			}else if(option==Button.O_NEW_TASKFOLDER){
				
				pulldownLocator = Locators.zNewTaskDropDown;
				optionLocator= Locators.zNewTaskFolderMenuItem;
				
				page = new DialogCreateTaskFolder(this.MyApplication, this);
				
			}
			else{
				throw new HarnessException(	"no logic defined for pulldown/option " + pulldown+ "/" + option);
			}

		}else if (pulldown == Button.B_MOVE) {
		
			pulldownLocator = Locators.zMoveTaskDropDown;
			optionLocator = Locators.zNewTaskListMenuItem;

			page = new DialogCreateTaskFolder(this.MyApplication, this);

		}else if (pulldown == Button.B_TASK_FILTERBY) {
			
			if(option==Button.O_TASK_TODOLIST){
				
				pulldownLocator= Locators.zFilterByTaskDropDown;
				optionLocator=Locators.zToDoListTaskMenuItem;
				
				page=null;
			}			

		}
		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option "
						+ option + " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			this.zClickAt(pulldownLocator,"");
			SleepUtil.sleepMedium();
			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown
							+ " option " + option + " optionLocator "
							+ optionLocator + " not present!");
				}

				this.zClickAt(optionLocator,"");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();
			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				page.zWaitForActive();
			}
		}
		// Return the specified page, or null if not set
		return (page);


	}
	/**
	 * Activate a pulldown with dynamic values, such as "Move to folder" and "Add a tag".
	 * 
	 * @param pulldown the toolbar button to press
	 * @param dynamic the toolbar item to click such as FolderItem or TagItem
	 * @throws HarnessException 
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Object dynamic)
			throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("
				+ pulldown + ", " + dynamic + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + dynamic);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (dynamic == null)
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_MOVE) {

			if (!(dynamic instanceof FolderItem))
				throw new HarnessException("if pulldown = " + Button.B_MOVE
						+ ", then dynamic must be FolderItem");

			FolderItem folder = (FolderItem) dynamic;
			pulldownLocator = Locators.zMoveTaskDropDown;
			optionLocator = "css=td#zti__DwtFolderChooser_TasksTKL__"+ folder.getId() + "_textCell";

			page = null;

		} else {

			throw new HarnessException("no logic defined for pulldown/dynamic "
					+ pulldown + "/" + dynamic);

		}

		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown
						+ " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			this.zClickAt(pulldownLocator, "");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			SleepUtil.sleepSmall();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException(" dynamic " + dynamic
							+ " optionLocator " + optionLocator
							+ " not present!");
				}

				this.zClickAt(optionLocator, "");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();
			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				page.zWaitForActive();
			}

		}

		// Return the specified page, or null if not set
		return (page);

	}

	public enum TaskStatus {
		PastDue, Upcoming, NoDueDate
	}

	/**
	 * Given a task ID, look for the GUI display of the item
	 * @param css Locator to the task row (i.e. css=div[id='zli__TKL__261'])
	 * @return
	 * @throws HarnessException
	 */
	private TaskItem parseTaskRow(String css) throws HarnessException {
		logger.info("TASK: " + css);		
		// See http://bugzilla.zimbra.com/show_bug.cgi?id=56452

		if ( !this.sIsElementPresent(css) )
			throw new HarnessException("Unable to locate task: "+ css);
		
		
		TaskItem item = new TaskItem();


		// Is it checked?
		// <div id="zlif__TKL__258__se" style="" class="ImgCheckboxUnchecked"></div>
		item.gIsChecked = this.sIsElementPresent(css + " div[id$='__se'][class='ImgCheckboxChecked']");

		// TODO: handle tags
		// Is it tagged?
		// <div id="zlif__TKL__258__tg" style="" class="ImgBlank_16"></div>
		this.sIsElementPresent(css + " div[id$='__tg'][class='ImgBlank_16']");

		// What's the priority?
		item.gPriority = "normal";
		if ( this.sIsElementPresent(css + " td[id$='__pr'] div[class*='ImgPriorityHigh_list']") )  {
			item.gPriority = "high";
		} else if ( this.sIsElementPresent(css + "td[id$='__pr'] div[class*='ImgPriorityLow_list']") )  {
			
		} else {
			item.gPriority = "normal";
		}

		// Is there an attachment?
		item.gHasAttachments = this.sIsElementPresent(css + " div[id$='__at'][class*='ImgAttachment']");		
		
		// Get the subject
		item.gSubject = this.sGetText(css + " td[id$='__su']").trim();

		// Get the status
		item.gStatus = this.sGetText(css + " td[id$='__st']").trim();

		// Get the % complete
		item.gPercentComplete = this.sGetText(css + " td[id$='__pc']").trim();

		// Get the due date
		item.gDueDate = this.sGetText(css + " td[id$='__dt']").trim();

		return (item);
	}
	
	/**
	 * Get all tasks from the current view
	 * 
	 * @return
	 * @throws HarnessException
	 */
	public List<TaskItem> zGetTasks() throws HarnessException {

		List<TaskItem> items = new ArrayList<TaskItem>();

		// The task page has the following under the zl__TKL__rows div:
		// <div id='_newTaskBannerId' .../> -- enter a new task
		// <div id='_upComingTaskListHdr' .../> -- Past due
		// <div id='zli__TKL__267' .../> -- Task item
		// <div id='zli__TKL__299' .../> -- Task item
		// <div id='_upComingTaskListHdr' .../> -- Upcoming
		// <div id='zli__TKL__271' .../> -- Task item
		// <div id='zli__TKL__278' .../> -- Task item
		// <div id='zli__TKL__275' .../> -- Task item
		// <div id='_upComingTaskListHdr' .../> -- No due date
		// <div id='zli__TKL__284' .../> -- Task item
		// <div id='zli__TKL__290' .../> -- Task item

		// How many items are in the table?
		// findTask(subject);
		String rowLocator = "css=div[id='" + Locators.zl__TKL__rowsID + "']>div";
		int count = this.sGetCssCount(rowLocator);
		logger.debug(myPageName() + " zGetTasks: number of rows: " + count);

		if ( count < 1 ) 
			throw new HarnessException("No tasks in the list!");

		
		String itemLocator = rowLocator + ":first-child";			
			
		// Get each conversation's data from the table list
		for (int i = 1; i <count; i++) {
			itemLocator = itemLocator + " + div ";
			//String itemLocator = rowLocator + ":nth-of-type("+ i +")";
			if ( !this.sIsElementPresent(itemLocator) )
				throw new HarnessException("Item Locator not present: "+ itemLocator);

			String id;
			try {
				id = this.sGetAttribute(itemLocator + "@id");
				if ( id == null )
					throw new HarnessException("id was null: "+ itemLocator + "@id");
				if ( !id.startsWith(Locators.zli__TKL__) )
					continue; // _newTaskBannerId, etc.

			} catch (SeleniumException e) {
				logger.warn("No ID on item: "+ itemLocator);
				continue;
			}


			// Found a task

			TaskItem item = parseTaskRow("css=div[id='"+ id +"']");
			items.add(item);
			logger.info(item.prettyPrint());

		}

		// Return the list of items
		return (items);

	}
	@Override
	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {
		String keyCode = "";
		if (shortcut == null)
			throw new HarnessException("Shortcut cannot be null");

		tracer.trace("Using the keyboard, press the "+ shortcut.getKeys() +" keyboard shortcut");

		AbsPage page = null;

		if ( (shortcut == Shortcut.S_NEWTAG) ){

			// "New Message" shortcuts result in a compose form opening
			//page = new FormMailNew(this.MyApplication);
			page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);
			keyCode = "78,84";
			
		}else if(shortcut== Shortcut.S_ESCAPE){
			page = new DialogWarning(
					DialogWarning.DialogWarningID.SaveTaskChangeMessage,
					this.MyApplication,
					((AppAjaxClient)this.MyApplication).zPageTasks);	
			
			keyCode = "27";
			
		}else if ( shortcut == Shortcut.S_ASSISTANT ) {			
			page = new DialogAssistant(MyApplication, ((AppAjaxClient) MyApplication).zPageTasks);
			keyCode= "192";
			
		}else if ( shortcut == Shortcut.S_NEWTASK ) {			
			//page = new DialogAssistant(MyApplication, ((AppAjaxClient) MyApplication).zPageTasks);
			page= new FormTaskNew(this.MyApplication);
			keyCode= "78,75";
			
		}else if ( shortcut == Shortcut.S_TASK_HARDELETE ) {			
			page= null;
			keyCode= "16,46";
			
		}else if (shortcut == Shortcut.S_MOVE) {

			// "Move" shortcut opens "Choose Folder" dialog
			page = new DialogMove(MyApplication, this);

			keyCode = "77";
		}else if ( shortcut == Shortcut.S_BACKSPACE ) {			
			page= null;
			keyCode= "8";
			
		}else{
			throw new HarnessException("implement shortcut: " + shortcut);
		}
		
		zKeyDown(keyCode);
		//zKeyboard.zTypeCharacters(shortcut.getKeys());

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If a page is specified, wait for it to become active
		if ( page != null ) {
			page.zWaitForActive();	// This method throws a HarnessException if never active
		}
		return (page);
	}

	/**
	 * Browse through the current listed tasks to find task with the specified subject
	 * @param subject Subject of the task to be searched for
	 * @return TaskItem with the specified subject
	 * @throws HarnessException
	 */
	public TaskItem browseTask(String subject) throws HarnessException {
		// Get the list of tasks in the view
		List<TaskItem> tasks = zGetTasks();
		ZAssert.assertNotNull(tasks, "Verify the list of tasks exists");

		// Iterate over the task list, looking for the new task
		TaskItem found = null;
		for (TaskItem t : tasks ) {
			logger.info("Task: looking for "+ subject +" found: "+ t.gSubject);
			if ( subject.equals(t.gSubject) ) {
				// Found it!
				found = t;
			}
		}
		return found;
	}

//	/**
//	 * Dynamically wait (for 30 secs) until the task with the specified subject is found
//	 * @param subject Subject of the task to be searched for
//	 * @return TaskItem with the specified subject
//	 * @throws HarnessException
//	 */
//	public TaskItem findTask(String subject) throws HarnessException {
//		Object[] params = {subject};
//		return (TaskItem)GeneralUtility.waitFor(null, this, false, "browseTask", params,
//				WAIT_FOR_OPERAND.NEQ, null, 30000, 1000);
//	}

	public String  GetShowOrigBodyText(String EmailAddress, String calItemId) throws HarnessException{

		try{
			sOpenWindow(ZimbraSeleniumProperties.getBaseURL() + "/home/" + EmailAddress + "/Tasks/?id=" + calItemId + "&mime=text/plain&noAttach=1","ShowOrignal");
			sWaitForPopUp("ShowOrignal", "3000");
			sSelectWindow("ShowOrignal");
			String showOrigBody=sGetBodyText().replaceAll("\\n", "").trim().replaceAll(" ", "");
			sClose();
			return showOrigBody;

		}finally{
			ClientSessionFactory.session().selenium().selectWindow("null");	
		}
	}
	
	public String zGetHtmlBodyText() throws HarnessException {
		try {
			sSelectFrame("css=iframe[id='zv__TKL_body__iframe']");
			String bodyhtml = this.sGetHtmlSource();
			return bodyhtml;
		} finally {
			this.sSelectFrame("relative=top");
		}

	}

}
