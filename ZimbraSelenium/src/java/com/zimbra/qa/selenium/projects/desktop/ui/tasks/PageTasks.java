/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui.tasks;

import java.util.*;

import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.projects.desktop.ui.*;


/**
 * @author Matt Rhoades
 * 
 */
public class PageTasks extends AbsTab {

	public static class Locators {

		public static final String zv__TKL = "zv__TKL-main";
		public static final String zl__TKL__rows = "zl__TKL__rows";
		public static final String zl__TKL__headers = "zl__TKL__headers";
		public static final String _newTaskBannerId = "_newTaskBannerId";
		public static final String _upComingTaskListHdr = "_upComingTaskListHdr";
		public static final String zli__TKL__ = "zli__TKL__"; // Each task item:
		// <div
		// id='zli__TKL__<item
		// id>' .../>
		public static final String zb__TKE1__SAVE_left_icon = "zb__TKE1__SAVE_left_icon";
		public static final String taskListView = "css=div[id='zl__TKL__rows'][class='DwtListView-Rows']";
		//public static final String taskbodyView = "css=div[id='zl__TKL__rows'][class='DwtListView-Rows']";
		public static final String zTasksTab = "zb__App__Tasks";
		public static final String zNewTask = "zb__TKL__NEW_MENU_left_icon";
		public static final String zNewTaskDropDown = "css=td[id$='__NEW_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";
		public static final String zNewTagMenuItem= "//td[contains(@id,'_left_icon')]/div[contains(@class,'ImgNewTag')]";
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

		String locator = "zb__App__Tasks";
		String rowLocator = "//div[@id='" + Locators.zl__TKL__rows + "']/div";

		boolean loaded = this.sIsElementPresent(rowLocator);
		if (!loaded)
			return (false);

		String selected = this.sGetAttribute("xpath=(//div[@id='" + locator
				+ "'])@class");
		return (selected.contains("ZSelected"));

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

		this.zClick(PageMain.Locators.zAppbarTasks);

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
		String itemSubject = null;

		// How many items are in the table?
		findTask(subject);
		String rowLocator = "//div[@id='" + Locators.zl__TKL__rows + "']/div";
		int count = this.sGetXpathCount(rowLocator);
		logger.debug(myPageName() + " zListItem: number of rows: " + count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			itemLocator = rowLocator + "[" + i + "]";
			String id = this.sGetAttribute("xpath=(" + itemLocator + ")@id");
			String locator = null;

			// Skip any invalid IDs
			if ((id == null) || (id.trim().length() == 0))
				continue;

			// Look for zli__TKL__258
			if (id.contains(Locators.zli__TKL__)) {
				// Found a task

				// What is the subject?
				locator = itemLocator + "//td[5]";
				itemSubject = this.sGetText(locator).trim();

				if ((itemSubject == null) || (itemSubject.trim().length() == 0)) {
					logger.debug("found empty task subject");
					continue;
				}

				if (itemSubject.equals(subject)) {
					// Found it
					break;
				}

			}
		}

		if (itemLocator == null) {
			throw new HarnessException("Unable to locate item with subject("
					+ subject + ")");
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

			String selectlocator = itemLocator + "//div[contains(@id, '__se')]";
			if (!this.sIsElementPresent(selectlocator))
				throw new HarnessException("Checkbox locator is not present "
						+ selectlocator);

			String image = this.sGetAttribute("xpath=" + selectlocator
					+ "@class");
			if (image.equals("ImgCheckboxChecked"))
				throw new HarnessException(
				"Trying to check box, but it was already enabled");

			// Left-Click on the flag field

			this.zClick(selectlocator);
			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else if (action == Action.A_MAIL_UNCHECKBOX) {

			String selectlocator = itemLocator + "//div[contains(@id, '__se')]";
			if (!this.sIsElementPresent(selectlocator))
				throw new HarnessException("Checkbox locator is not present "
						+ selectlocator);

			String image = this.sGetAttribute("xpath=" + selectlocator
					+ "@class");
			if (image.equals("ImgCheckboxUnchecked"))
				throw new HarnessException(
				"Trying to uncheck box, but it was already disabled");

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

		String rowLocator = null;
		String itemLocator = null;
		AbsPage page = null;

		// How many items are in the table?
		rowLocator = "//div[@id='" + Locators.zl__TKL__rows + "']/div";
		int count = this.sGetXpathCount(rowLocator);
		logger.debug(myPageName() + " zListItem: number of rows: " + count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			itemLocator = rowLocator + "[" + i + "]";
			String id = this.sGetAttribute("xpath=(" + itemLocator + ")@id");
			String locator = null;

			// Skip any invalid IDs
			if ((id == null) || (id.trim().length() == 0))
				continue;

			// Look for zli__TKL__258
			if (id.contains(Locators.zli__TKL__)) {
				// Found a task

				// What is the subject?
				locator = itemLocator + "//td[5]";
				String itemSubject = this.sGetText(locator).trim();

				if ((itemSubject == null) || (itemSubject.trim().length() == 0)) {
					logger.debug("found empty task subject");
					continue;
				}

				if (itemSubject.equals(subject)) {
					// Found it
					break;
				}

			}
		}

		if (itemLocator == null) {
			throw new HarnessException("Unable to locate item with subject("
					+ subject + ")");
		}

		if (action == Action.A_RIGHTCLICK) {

			// Right-Click on the item
			this.zRightClick(itemLocator);

			// Now the ContextMenu is opened
			// Click on the specified option

			String optionLocator = null;

			if (option == Button.B_DELETE) {

				// <div id="zmi__Tasks__DELETE" ...
				optionLocator = "zmi__Tasks__DELETE";
				page = null;

			} else {
				throw new HarnessException("implement action:" + action
						+ " option:" + option);
			}

			// click on the option
			this.zClick(optionLocator);

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
		if (button == Button.B_NEW) {

			// New button
			locator = Locators.zNewTask;

			page = new FormTaskNew(this.MyApplication);


		} else if (button == Button.B_EDIT) {

			locator = "zb__TKL__EDIT_left_icon";

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='" + locator
					+ "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}
			page = new FormTaskNew(this.MyApplication);

		} else if (button == Button.B_DELETE) {

			locator = "zb__TKL__DELETE_left_icon";

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='" + locator
					+ "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}

		} else if (button == Button.B_MOVE) {

			locator = "zb__TKL__MOVE_left_icon";

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='" + locator
					+ "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}

			page = new DialogMove(this.MyApplication,this);

			// FALL THROUGH

		} else if (button == Button.B_PRINT) {

			locator = "zb__TKL__PRINT_left_icon";

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='" + locator
					+ "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}

			page = null; // TODO
			throw new HarnessException("implement Print dialog");

		} else if (button == Button.B_SAVE) {
			locator = "zb__TKE1__SAVE_left_icon";
			page = new FormTaskNew(this.MyApplication);

		} else if (button == Button.B_TAG) {

			// For "TAG" without a specified pulldown option, just click on the
			// pulldown
			// To use "TAG" with a pulldown option, see
			// zToolbarPressPulldown(Button, Button)
			//

			locator = "zb__TKL__TAG_MENU_dropdown";

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='" + locator
					+ "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}

		} else if (button == Button.B_TASK_FILTERBY) {
			throw new HarnessException("implement me");
		} else if (button == Button.B_TASK_MARKCOMPLETED) {
			throw new HarnessException("implement me");
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClick(locator);

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

				optionLocator = "css=td[id$='__TAG_MENU|MENU|NEWTAG_title']";

				page = new DialogTag(this.MyApplication, this);

				// FALL THROUGH
			} else if (option == Button.O_TAG_REMOVETAG) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[id$='__TAG_MENU|MENU|REMOVETAG_title']";

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
			}else{
				throw new HarnessException(	"no logic defined for pulldown/option " + pulldown+ "/" + option);
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

			this.zClick(pulldownLocator);

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown
							+ " option " + option + " optionLocator "
							+ optionLocator + " not present!");
				}

				this.zClick(optionLocator);

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
	 * Get all tasks in the specified section
	 * 
	 * @param status
	 * @return
	 * @throws HarnessException
	 */
	public List<TaskItem> zGetTasks(TaskStatus status) throws HarnessException {

		List<TaskItem> items = null;

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
		String rowLocator = "//div[@id='" + Locators.zl__TKL__rows + "']";
		int count = this.sGetXpathCount(rowLocator);
		logger.debug(myPageName() + " zGetTasks: number of rows: " + count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			String tasklocator = rowLocator + "/div[" + i + "]";

			String id = this.sGetAttribute("xpath=(" + tasklocator + ")@id");
			if (Locators._newTaskBannerId.equals(id)) {
				// Skip the "Add New Task" row
				continue;
			} else if (Locators._upComingTaskListHdr.equals(id)) {
				// Found a status separator

				String text = this.sGetText(tasklocator);
				if (("Past Due".equals(text)) && (status == TaskStatus.PastDue)) {

					items = new ArrayList<TaskItem>();
					continue;

				} else if (("Upcoming".equals(text))
						&& (status == TaskStatus.Upcoming)) {

					items = new ArrayList<TaskItem>();
					continue;

				} else if (("No Due Date".equals(text))
						&& (status == TaskStatus.NoDueDate)) {

					items = new ArrayList<TaskItem>();
					continue;

				}

				// If a list already exists, then we've just completed it
				if (items != null)
					return (items);

			} else if (id.contains(Locators.zli__TKL__)) {
				// Found a task

				// If the list is initialized, then we are in the correct list
				// section
				if (items == null)
					continue;

				TaskItem item = new TaskItem();

				// TODO: extract the info from the GUI

				items.add(item);
				logger.info(item.prettyPrint());

			} else {
				logger.warn("Unknown task row ID: " + id);
				continue;
			}
		}

		// If items is still null, then we didn't find any matching tasks
		// Just return an empty list
		if (items == null)
			items = new ArrayList<TaskItem>();

		// Return the list of items
		return (items);

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
		String rowLocator = "//div[@id='" + Locators.zl__TKL__rows + "']/div";
		int count = this.sGetXpathCount(rowLocator);
		logger.debug(myPageName() + " zGetTasks: number of rows: " + count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			String itemLocator = rowLocator + "[" + i + "]";

			String id;
			try {
				id = this.sGetAttribute("xpath=(" + itemLocator + ")@id");
			} catch (SeleniumException e) {
				// Make sure there is an ID
				logger.warn("Task row didn't have ID.  Probably normal if message is 'Could not find element attribute' => "+ e.getMessage());
				continue;
			}

			String locator = null;
			String attr = null;

			// Skip any invalid IDs
			if ((id == null) || (id.trim().length() == 0))
				continue;

			// Look for zli__TKL__258
			if (id.contains(Locators.zli__TKL__)) {
				// Found a task

				TaskItem item = new TaskItem();

				logger.info("TASK: " + id);

				// Is it checked?
				// <div id="zlif__TKL__258__se" style=""
				// class="ImgCheckboxUnchecked"></div>
				locator = itemLocator
				+ "//div[contains(@class, 'ImgCheckboxUnchecked')]";
				item.gIsChecked = this.sIsElementPresent(locator);

				// Is it tagged?
				// <div id="zlif__TKL__258__tg" style=""
				// class="ImgBlank_16"></div>
				locator = itemLocator + "//div[contains(@id, '__tg')]";
				// TODO: handle tags

				// What's the priority?
				// <td width="19" id="zlif__TKL__258__pr"><center><div style=""
				// class="ImgTaskHigh"></div></center></td>
				locator = itemLocator
				+ "//td[contains(@id, '__pr')]/center/div";
				if (!this.sIsElementPresent(locator)) {
					item.gPriority = "normal";
				} else {
					locator = "xpath=(" + itemLocator
					+ "//td[contains(@id, '__pr')]/center/div)@class";
					attr = this.sGetAttribute(locator);
					if (attr.equals("ImgTaskHigh")) {
						item.gPriority = "high";
					} else if (attr.equals("ImgTaskLow")) {
						item.gPriority = "low";
					}
				}

				// Is there an attachment?
				// <td width="19" class="Attach"><div id="zlif__TKL__258__at"
				// style="" class="ImgBlank_16"></div></td>
				locator = "xpath=(" + itemLocator
				+ "//div[contains(@id, '__at')])@class";
				attr = this.sGetAttribute(locator);
				if (attr.equals("ImgBlank_16")) {
					item.gHasAttachments = false;
				} else {
					// TODO - handle other attachment types
				}

				// See http://bugzilla.zimbra.com/show_bug.cgi?id=56452

				// Get the subject
				locator = itemLocator + "//td[5]";
				item.gSubject = this.sGetText(locator).trim();

				// Get the status
				locator = itemLocator + "//td[6]";
				item.gStatus = this.sGetText(locator).trim();

				// Get the % complete
				locator = itemLocator + "//td[7]";
				item.gPercentComplete = this.sGetText(locator).trim();

				// Get the due date
				locator = itemLocator + "//td[8]";
				item.gDueDate = this.sGetText(locator).trim();

				items.add(item);
				logger.info(item.prettyPrint());

			}

		}

		// Return the list of items
		return (items);

	}
	@Override
	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {

		if (shortcut == null)
			throw new HarnessException("Shortcut cannot be null");
		
		tracer.trace("Using the keyboard, press the "+ shortcut.getKeys() +" keyboard shortcut");

		AbsPage page = null;
		
		if ( (shortcut == Shortcut.S_NEWTAG) ){
			
			// "New Message" shortcuts result in a compose form opening
			//page = new FormMailNew(this.MyApplication);
			page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);
		}
		
		zKeyboard.zTypeCharacters(shortcut.getKeys());
		
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

	/**
	 * Dynamically wait (for 30 secs) until the task with the specified subject is found
	 * @param subject Subject of the task to be searched for
	 * @return TaskItem with the specified subject
	 * @throws HarnessException
	 */
	public TaskItem findTask(String subject) throws HarnessException {
	   Object[] params = {subject};
	   return (TaskItem)GeneralUtility.waitFor(null, this, false, "browseTask", params, WAIT_FOR_OPERAND.NEQ, null, 30000, 1000);
	}
	
	public String  GetShowOrigBodyText(String EmailAddress, String calItemId) throws HarnessException{

		try{
		    String port = ZimbraDesktopProperties.getInstance().getConnectionPort();
		    String host = ZimbraSeleniumProperties.getStringProperty("desktop.server.host", "localhost");
			String ShowOriURL = "http://" + host + ":" + port + "/home/" + EmailAddress + "/Tasks/?id=" + calItemId + "&mime=text/plain&noAttach=1";
			sOpenWindow(ShowOriURL, "ShowOrignal");
			sWaitForPopUp("ShowOrignal", "3000");
			sSelectWindow("ShowOrignal");
			String showOrigBody = sGetBodyText().replaceAll("\\n", "").trim().replaceAll(" ", "");
			sClose();
			return showOrigBody;

		}finally{
			ClientSessionFactory.session().selenium().selectWindow("null");	
		}
	}
}
