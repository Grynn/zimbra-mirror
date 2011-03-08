/**
 * 
 */
package com.zimbra.qa.selenium.projects.html.ui.mail;

import java.util.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.html.ui.*;


/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsTab {


	public static class Locators {
		public static final String LocatorGetMail = "css=img[alt='Refresh']";
	}





	public PageMail(AbsApplication application) {
		super(application);

		logger.info("new " + PageMail.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if ( !((AppHtmlClient)MyApplication).zPageMain.zIsActive() ) {
			((AppHtmlClient)MyApplication).zPageMain.zNavigateTo();
		}

		// If the "folders" tree is visible, then mail is active
		String locator = Locators.LocatorGetMail;

		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (false);

		return (true);

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
		if ( !((AppHtmlClient)MyApplication).zPageMain.zIsActive() ) {
			((AppHtmlClient)MyApplication).zPageMain.zNavigateTo();
		}

		tracer.trace("Navigate to "+ this.myPageName());

		this.zClick(PageMain.Locators.zAppbarMail);

		this.zWaitForBusyOverlayHTML();

		zWaitForActive();

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		tracer.trace("Press the "+ button +" button");

		if ( button == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_NEW ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_GETMAIL ) {

			locator = Locators.LocatorGetMail;
			page = null;

		} else if ( button == Button.B_DELETE ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH


		} else if ( button == Button.B_MOVE ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_PRINT ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_REPLY ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_REPLYALL ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_FORWARD ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( (button == Button.B_RESPORTSPAM) || (button == Button.B_RESPORTNOTSPAM) ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_TAG ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_NEWWINDOW ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH			

		} else if ( button == Button.B_LISTVIEW ) {

			locator = "implement me";
			page = null; // new FormMailNew(this.MyApplication);

			// FALL THROUGH

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
		this.zWaitForBusyOverlayHTML();
		
		// If page was specified, make sure it is active
		if ( page != null ) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}


		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");

		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		if ( pulldown == null )
			throw new HarnessException("Pulldown cannot be null!");

		if ( option == null )
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( pulldown == Button.B_NEW ) {

			if ( option == Button.O_NEW_ADDRESSBOOK ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_APPOINTMENT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_BRIEFCASE ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_CALENDAR ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_CONTACT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_CONTACTGROUP ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_DOCUMENT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_FOLDER ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_MESSAGE ) {

				// TODO: should this actually click New followed by Message?

				pulldownLocator = null;
				optionLocator = null;
				page = zToolbarPressButton(pulldown);

				// FALL THROUGH

			} else if ( option == Button.O_NEW_TAG ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_TASK ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_NEW_TASKFOLDER ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}

		} else if ( pulldown == Button.B_LISTVIEW ) { 

			if ( option == Button.O_LISTVIEW_BYCONVERSATION ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_LISTVIEW_BYMESSAGE ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_LISTVIEW_READINGPANEBOTTOM ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_LISTVIEW_READINGPANEOFF ) {
				throw new HarnessException("implement me!");
			} else if ( option == Button.O_LISTVIEW_READINGPANERIGHT ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}

		} else if ( pulldown == Button.B_TAG ) {

			if ( option == Button.O_TAG_NEWTAG ) {

				pulldownLocator = "implement me";
				optionLocator = "implement me";
				page = null; // new DialogTag(this.MyApplication, this);

				// FALL THROUGH

			} else if ( option == Button.O_TAG_REMOVETAG ) {

				pulldownLocator = "implement me";
				optionLocator = "implement me";
				page = null; 

				// FALL THROUGH

			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}

		} else {
			throw new HarnessException("no logic defined for pulldown "+ pulldown);
		}

		// Default behavior
		if ( pulldownLocator != null ) {

			// Make sure the locator exists
			if ( !this.sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
			}

			this.zClick(pulldownLocator);

			// If the app is busy, wait for it to become active
			this.zWaitForBusyOverlayHTML();


			if ( optionLocator != null ) {

				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}

				this.zClick(optionLocator);

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayHTML();

			}


			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if ( page != null ) {
				page.zWaitForActive();
			}

		}

		// Return the specified page, or null if not set
		return (page);
	}




	public enum PageMailView {
		BY_MESSAGE, BY_CONVERSATION
	}

	/**
	 * Return a list of all messages in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<MailItem> zListGetMessages() throws HarnessException {

		List<MailItem> items = new ArrayList<MailItem>();


		// Return the list of items
		return (items);
	}

	/**
	 * Return a list of all conversations in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<ConversationItem> zListGetConversations() throws HarnessException {
		logger.info(myPageName() + " getConversationList");

		List<ConversationItem> items = new ArrayList<ConversationItem>();


		// Return the list of items
		return (items);
	}




	@Override
	public AbsPage zListItem(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");

		tracer.trace(action +" on subject = "+ subject);

		AbsPage page = null;

		// Find the item locator
		//



		if ( page != null ) {
			page.zWaitForActive();
		}

		// default return command
		return (page);

	}

	public AbsPage zListItem(Action action, Button option, FolderItem folderItem) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ option +")");
		tracer.trace(action +" then "+ option +" on Folder Item = "+ folderItem);

		AbsPage page = null;
		return page;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
	throws HarnessException {
		tracer.trace(action +" then "+ option + "," + subOption + " on item = "+ item);

		throw new HarnessException("implement me!");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ option +", "+ subject +")");

		tracer.trace(action +" then "+ option +" on subject = "+ subject);


		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("button cannot be null");
		if ( subject == null || subject.trim().length() == 0)
			throw new HarnessException("subject cannot be null or blank");

		AbsPage page = null;




		if ( page != null ) {
			page.zWaitForActive();
		}


		// Default behavior
		return (page);

	}

	@Override
	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {

		if (shortcut == null)
			throw new HarnessException("Shortcut cannot be null");

		tracer.trace("Using the keyboard, press the "+ shortcut.getKeys() +" keyboard shortcut");

		AbsPage page = null;

		if ( (shortcut == Shortcut.S_NEWITEM) ||
				(shortcut == Shortcut.S_NEWMESSAGE) ||
				(shortcut == Shortcut.S_NEWMESSAGE2) )
		{
			// "New Message" shortcuts result in a compose form opening
			page = null; // new FormMailNew(this.MyApplication);
		}

		zKeyboard.zTypeCharacters(shortcut.getKeys());

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlayHTML();

		// If a page is specified, wait for it to become active
		if ( page != null ) {
			page.zWaitForActive();	// This method throws a HarnessException if never active
		}
		return (page);
	}




}
