/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.tasks;

import java.util.*;

import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.items.TaskItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogMove;


/**
 * @author Matt Rhoades
 *
 */
public class PageTasks extends AbsTab {

	
	public static class Locators {

		public static final String zv__TKL 					= "zv__TKL";
		public static final String zl__TKL__rows 			= "zl__TKL__rows";
		public static final String zl__TKL__headers 		= "zl__TKL__headers";
		public static final String _newTaskBannerId 		= "_newTaskBannerId";
		public static final String _upComingTaskListHdr 	= "_upComingTaskListHdr";
		public static final String zli__TKL__ 				= "zli__TKL__";			// Each task item: <div id='zli__TKL__<item id>' .../>

	}
	
	



	public PageTasks(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageTasks.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}
		
		// If the "folders" tree is visible, then mail is active
		String locator = "zb__App__Tasks";
		
		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (false);
		
		String selected = this.sGetAttribute("xpath=(//div[@id='"+ locator +"'])@class");
		return (selected.contains("ZSelected"));

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {

		// Check if this page is already active.
		if ( zIsActive() ) {
			return;
		}
		
		// Make sure we are logged into the Mobile app
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}
		
		this.zClick(PageMain.Locators.zAppbarTasks);
		
		this.zWaitForBusyOverlay();
		
		zWaitForActive();

	}

	@Override
	public AbsPage zListItem(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");
		
		if ( (subject == null) || (subject.trim().length() == 0) )
			throw new HarnessException("subject cannot be null or empty");
		
		AbsPage page = null;
		String itemLocator = null;
		String itemSubject = null;
		
		// How many items are in the table?
		String rowLocator = "//div[@id='"+ Locators.zl__TKL__rows  +"']/div";
		int count = this.sGetXpathCount(rowLocator);
		logger.debug(myPageName() + " zListItem: number of rows: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			
			itemLocator = rowLocator + "["+ i +"]";
			String id = this.sGetAttribute("xpath=("+ itemLocator +")@id");
			String locator = null;
		
			// Skip any invalid IDs
			if ( (id == null) || (id.trim().length() == 0) )
				continue;
			
			// Look for zli__TKL__258
			if ( id.contains(Locators.zli__TKL__) ) {
				// Found a task

				// What is the subject?
				locator = itemLocator + "//td[5]";
				itemSubject = this.sGetText(locator).trim();
				
				if ((itemSubject == null) || (itemSubject.trim().length() == 0) ) {
					logger.debug("found empty task subject");
					continue;
				}
				
				if (itemSubject.equals(subject)) {
					// Found it
					break;
				}
				
			}
		}

		
		if ( itemLocator == null ) {
			throw new HarnessException("Unable to locate item with subject("+ subject +")");
		}

		if ( action == Action.A_LEFTCLICK ) {
			
			// Left-Click on the item
			this.zClick(itemLocator);
			
			this.zWaitForBusyOverlay();

			// Return the displayed mail page object
			page = new DisplayTask(MyApplication);
			
			// FALL THROUGH

		} else if ( action == Action.A_CTRLSELECT ) {
			
			throw new HarnessException("implement me!  action = "+ action);
			
		} else if ( action == Action.A_SHIFTSELECT ) {
			
			throw new HarnessException("implement me!  action = "+ action);
			
		} else if ( action == Action.A_RIGHTCLICK ) {
			
			// Right-Click on the item
			this.zRightClick(itemLocator);
			
			// Return the displayed mail page object
			page = new ContextMenu(MyApplication);
			
			// FALL THROUGH
			
		} else if ( action == Action.A_MAIL_CHECKBOX ) {
			
			String selectlocator = itemLocator + "//div[contains(@id, '__se')]";
			if ( !this.sIsElementPresent(selectlocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectlocator);
			
			String image = this.sGetAttribute("xpath="+ selectlocator +"@class");
			if ( image.equals("ImgCheckboxChecked") )
				throw new HarnessException("Trying to check box, but it was already enabled");
				
			// Left-Click on the flag field
			this.zClick(selectlocator);
			
			this.zWaitForBusyOverlay();
			
			// No page to return
			page = null;

			// FALL THROUGH
			
		} else if ( action == Action.A_MAIL_UNCHECKBOX ) {
			
			String selectlocator = itemLocator + "//div[contains(@id, '__se')]";
			if ( !this.sIsElementPresent(selectlocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectlocator);
			
			String image = this.sGetAttribute("xpath="+ selectlocator +"@class");
			if ( image.equals("ImgCheckboxUnchecked") )
				throw new HarnessException("Trying to uncheck box, but it was already disabled");
				
			// Left-Click on the flag field
			this.zClick(selectlocator);
			
			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH
			
		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}
		

		if ( page != null ) {
			page.zWaitForActive();
		}
		
		// default return command
		return (page);
		
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
				
		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( button == Button.B_NEW ) {
			
			// New button
			locator = "zb__TKL__NEW_MENU_left_icon";
			
			// Create the page
			page = new FormTaskNew(this.MyApplication);
			
			// FALL THROUGH
			
		} else if ( button == Button.B_EDIT ) {
			
			locator = "zb__TKL__EDIT_left_icon";
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ locator +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}				

		} else if ( button == Button.B_DELETE ) {
			
			locator = "zb__TKL__DELETE_left_icon";
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ locator +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}				
			
		} else if ( button == Button.B_MOVE ) {
			
			locator = "zb__TKL__MOVE_left_icon";
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ locator +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			page = new DialogMove(MyApplication);

			// FALL THROUGH
			
		} else if ( button == Button.B_PRINT ) {
			
			locator = "zb__TKL__PRINT_left_icon";
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ locator +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			page = null;	// TODO
			throw new HarnessException("implement Print dialog");
			
		} else if ( button == Button.B_TAG ) {
			
			// For "TAG" without a specified pulldown option, just click on the pulldown
			// To use "TAG" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
			//
			
			locator = "zb__TKL__TAG_MENU_dropdown";
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ locator +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}
			
		} else if ( button == Button.B_TASK_FILTERBY ) {
			throw new HarnessException("implement me");
		} else if ( button == Button.B_TASK_MARKCOMPLETED ) {
			throw new HarnessException("implement me");
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}
		
		// Default behavior, process the locator by clicking on it
		//
		this.zClick(locator);
		
		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();
		

		// If page was specified, make sure it is active
		if ( page != null ) {
			
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}

		
		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public enum TaskStatus {
		PastDue,
		Upcoming,
		NoDueDate
	}
	
	/**
	 * Get all tasks in the specified section
	 * @param status
	 * @return
	 */
	public List<TaskItem> zGetTasks(TaskStatus status) {
		
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
		String rowLocator = "//div[@id='"+ Locators.zl__TKL__rows  +"']";
		int count = this.sGetXpathCount(rowLocator);
		logger.debug(myPageName() + " zGetTasks: number of rows: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			String tasklocator = rowLocator +"/div["+ i +"]";
			
			String id = this.sGetAttribute("xpath=("+ tasklocator +")@id");
			if ( Locators._newTaskBannerId.equals(id) ) {
				// Skip the "Add New Task" row
				continue;
			} else if ( Locators._upComingTaskListHdr.equals(id) ) {
				// Found a status separator

				
				String text = this.sGetText(tasklocator);
				if ( ("Past Due".equals(text)) && (status == TaskStatus.PastDue) ) {
					
					items = new ArrayList<TaskItem>();
					continue;
					
				} else if ( ("Upcoming".equals(text)) && (status == TaskStatus.Upcoming) ) {
					
					items = new ArrayList<TaskItem>();
					continue;
					
				} else if ( ("No Due Date".equals(text)) && (status == TaskStatus.NoDueDate) ) {
					
					items = new ArrayList<TaskItem>();
					continue;
					
				}
				
				// If a list already exists, then we've just completed it
				if ( items != null )
					return (items);
				
			} else if ( id.contains(Locators.zli__TKL__ ) ) {
				// Found a task
				
				// If the list is initialized, then we are in the correct list section
				if ( items == null )
					continue;
				
				TaskItem item = new TaskItem();
				
				// TODO: extract the info from the GUI

				items.add(item);
				logger.info(item.prettyPrint());

				
			} else {
				logger.warn("Unknown task row ID: "+ id);
				continue;
			}
		}
		
		// If items is still null, then we didn't find any matching tasks
		// Just return an empty list
		if ( items == null )
			items = new ArrayList<TaskItem>();
		
		// Return the list of items
		return (items);

	}
	
	/**
	 * Get all tasks from the current view
	 * @return
	 */
	public List<TaskItem> zGetTasks() {
		
		
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
		String rowLocator = "//div[@id='"+ Locators.zl__TKL__rows  +"']/div";
		int count = this.sGetXpathCount(rowLocator);
		logger.debug(myPageName() + " zGetTasks: number of rows: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			
			String itemLocator = rowLocator + "["+ i +"]";
			
			String id;
			try {
				id = this.sGetAttribute("xpath=("+ itemLocator +")@id");
			} catch (SeleniumException e) {
				// Make sure there is an ID
				logger.warn("Task row didn't have ID", e);
				continue;
			}
			
			String locator = null;
			String attr = null;
			
			// Skip any invalid IDs
			if ( (id == null) || (id.trim().length() == 0) )
				continue;
			
			// Look for zli__TKL__258
			if ( id.contains(Locators.zli__TKL__) ) {
				// Found a task
				
				TaskItem item = new TaskItem();

				logger.info("TASK: "+ id);
				
				// Is it checked?
				// <div id="zlif__TKL__258__se" style="" class="ImgCheckboxUnchecked"></div>
				locator = itemLocator +"//div[contains(@class, 'ImgCheckboxUnchecked')]";
				item.gIsChecked = this.sIsElementPresent(locator);
							
				// Is it tagged?
				// <div id="zlif__TKL__258__tg" style="" class="ImgBlank_16"></div>
				locator = itemLocator + "//div[contains(@id, '__tg')]";
				// TODO: handle tags
				
				// What's the priority?
				// <td width="19" id="zlif__TKL__258__pr"><center><div style="" class="ImgTaskHigh"></div></center></td>
				locator = itemLocator + "//td[contains(@id, '__pr')]/center/div";
				if ( !this.sIsElementPresent(locator) ) {
					item.gPriority = "normal";
				} else {
					locator = "xpath=("+ itemLocator +"//td[contains(@id, '__pr')]/center/div)@class";
					attr = this.sGetAttribute(locator);
					if ( attr.equals("ImgTaskHigh") ) {
						item.gPriority = "high";
					} else if ( attr.equals("ImgTaskLow") ) {
						item.gPriority = "low";
					}
				}
				
				// Is there an attachment?
				// <td width="19" class="Attach"><div id="zlif__TKL__258__at" style="" class="ImgBlank_16"></div></td>
				locator = "xpath=("+ itemLocator +"//div[contains(@id, '__at')])@class";
				attr = this.sGetAttribute(locator);
				if ( attr.equals("ImgBlank_16") ) {
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


}
