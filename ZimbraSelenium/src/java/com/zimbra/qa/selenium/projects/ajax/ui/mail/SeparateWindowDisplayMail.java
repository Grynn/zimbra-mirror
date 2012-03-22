/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.util.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.*;



/**
 * Represents a "Launch in New Window" display of a message
 * <p>
 * @author Matt Rhoades
 *
 */
public class SeparateWindowDisplayMail extends AbsSeparateWindow {

	public static class Locators {

	}
	
	public String ContainerLocator = "css=div[id='zv__MSG-1__MSG']";


	public SeparateWindowDisplayMail(AbsApplication application) {
		super(application);
		
		// Set the title to null to start.
		// Set the title with zSetSubject()
		this.DialogWindowTitle = null;
		
	}
	
	public String zGetMailProperty(Field field) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedValue(" + field + ")");

		String container = "css=div[id='zv__MSG-1__MSG']";
		String locator = null;
		
		if ( field == Field.From ) {
			
			locator = container + " tr[id$='_from'] span[id$='_com_zimbra_email']";
			if ( !this.sIsElementPresent(locator) ) {
				locator = container + " tr[id$='_from']"; // No bubbles
			}
			
		} else if ( field == Field.To ) {
			
			locator = container + " tr[id$='_to'] span[id$='_com_zimbra_email']";
			if ( !this.sIsElementPresent(locator) ) {
				locator = container + " tr[id$='_to']"; // No bubbles
			}
			
		} else if ( field == Field.Cc ) {
			
			locator = container + " tr[id$='_cc'] span[id$='_com_zimbra_email']";
			if ( !this.sIsElementPresent(locator) ) {
				locator = container + " tr[id$='_cc']"; // No bubbles
			}
			
		} else if ( field == Field.OnBehalfOf ) {
			
			locator = container + " td[id$='_obo'] span[id$='_com_zimbra_email']";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = container + " td[id$='_obo']";
			}

		} else if ( field == Field.ResentFrom ) {
			
			locator = container + " td[id$='_bwo'] span[id$='_com_zimbra_email']";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = container + " tr[id$='_bwo']";
			}

		} else if ( field == Field.OnBehalfOfLabel ) {
			
			locator = container + " td[id$='_obo_label']";

		} else if ( field == Field.ReplyTo ) {
			
			locator = container + " tr[id$='_reply to'] span[id$='_com_zimbra_email']";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = container + " tr[id$='_reply to']";
			}

		} else if ( field == Field.Subject ) {
			
			locator = container + " tr[id='zv__MSG__MSG-1_hdrTableTopRow'] td[class~='SubjectCol']";
			
		} else if ( field == Field.ReceivedDate ) {
			
			locator = container + " tr[id$='_hdrTableTopRow'] td[class~='DateCol'] span[id$='_com_zimbra_date']";

		} else if ( field == Field.ReceivedTime ) {
			
			String timeAndDateLocator = container + " tr[id$='_hdrTableTopRow'] td[class~='DateCol'] span[id$='_com_zimbra_date']";

			// Make sure the subject is present
			if ( !sIsElementPresent(timeAndDateLocator) )
				throw new HarnessException("Unable to find the time and date field!");
			
			// Get the subject value
			String timeAndDate = this.sGetText(timeAndDateLocator).trim();
			String date = this.zGetMailProperty(Field.ReceivedDate);
			
			// Strip the date so that only the time remains
			String time = timeAndDate.replace(date, "").trim();
			
			logger.info("zGetDisplayedValue(" + field + ") = " + time);
			return(time);

		} else if ( field == Field.Body ) {
			
			/*
			 * To get the body contents, need to switch iframes
			 */
			String text = sGetText("css=iframe[id$='_body__iframe']", "css=body");
			logger.info("zGetDisplayedValue(" + field + ") = " + text);
			return(text);

		} else {
			
			throw new HarnessException("No logic defined for Field: "+ field);
			
		}


		// Make sure something was set
		if ( locator == null )
			throw new HarnessException("locator was null for field = "+ field);
		
		// Default behavior
		//
		String value = sGetText(locator);
		
		logger.info("zGetDisplayedValue(" + field + ") = " + value);
		return(value);

	}


	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsPage#zWaitForActive()
	 */
	public void zWaitForActive() throws HarnessException {
		super.zWaitForActive(PageLoadDelay);
		
		// Sometimes it takes a while for the separate window to load
		// Look for the subject before returning
		String locator = "css=div[id='zv__MSG-1__MSG'] tr[id='zv__MSG__MSG-1_hdrTableTopRow'] td[class*='SubjectCol']";
		for(int i = 0; i < 30; i++) {

			boolean present = sIsElementPresent(locator);
			if ( present ) {
				return;
			}
			
			SleepUtil.sleep(1000);
			
		}

		throw new HarnessException("Page never became active!");

	}

	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		tracer.trace("Press the "+ button +" button");

		if ( button == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String container = "css=div[id^='ztb__MSG']";
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_CLOSE ) {

			locator = container + " div[id$='__CLOSE'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_DELETE ) {

			locator = container + " div[id$='__DELETE'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_REPLY ) {

			locator = container + " div[id$='__REPLY'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_REPLYALL ) {

			locator = container + " div[id$='__REPLY_ALL'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_FORWARD ) {

			locator = container + " div[id$='__FORWARD'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_RESPORTSPAM ) {

			locator = container + " div[id$='__SPAM'] td[id$='_title']";
			page = null;

		} else if ( button == Button.B_RESPORTNOTSPAM ) {

			locator = container + " div[id$='__SPAM'] td[id$='_title']";
			page = null;

		} else {
			
			throw new HarnessException("no logic defined for button "+ button);
			
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClickAt(locator,"0,0");

		return (page);
	}

	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");
		
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);
		
		
		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");
		
		
		// Default behavior variables
		String containerToolbar = "css=div[id^='ztb__MSG']";
		String containerActionMenu = "css=div[id^='zm__MSG']";
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if ( pulldown == Button.B_ACTIONS ) {
		
			pulldownLocator = containerToolbar + " div[id$='__ACTIONS_MENU'] td[id$='_dropdown']>div";

			if ( option == Button.B_PRINT ) {
				
				optionLocator = containerActionMenu + " div[id='PRINT'] td[id$='_title']";
				page = null;
				throw new HarnessException("implement me"); // Need to implement the print dialog

				// FALL THROUGH
				
			} else if (option == Button.B_RESPORTSPAM) {
				
				optionLocator = containerActionMenu + " div[id='SPAM'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH
				
			} else if (option == Button.B_RESPORTNOTSPAM) {
				
				optionLocator = containerActionMenu + " div[id='SPAM'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH
				
			} else if (option == Button.O_MARK_AS_READ) {
				
				optionLocator = containerActionMenu + " div[id='MARK_READ'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH
				
			} else if (option == Button.O_MARK_AS_UNREAD) {
				
				optionLocator = containerActionMenu + " div[id='MARK_UNREAD'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH
				
			} else if (option == Button.O_SHOW_ORIGINAL) {
				
				optionLocator = containerActionMenu + " div[id='SHOW_ORIG'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else if (option == Button.B_REDIRECT) {
				
				optionLocator = containerActionMenu + " div[id='REDIRECT'] td[id$='_title']";
				page = new SeparateWindowDialogRedirect(this.MyApplication, this);

				// FALL THROUGH

			} else if (option == Button.O_EDIT_AS_NEW) {
				
				optionLocator = containerActionMenu + " div[id='EDIT_AS_NEW'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else {
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
			}

		} else if (pulldown == Button.B_TAG) {
			
			pulldownLocator = containerToolbar + " div[id$='__TAG_MENU'] td[id$='_dropdown']>div";

			if (option == Button.O_TAG_NEWTAG) {

				optionLocator = "css=td[id$='__TAG_MENU|MENU|NEWTAG_title']";
				page = null; // new DialogTag(this.MyApplication, this);
				throw new HarnessException("implement me"); // Need to implement the 'new tag' dialog

				// FALL THROUGH
				
			} else if (option == Button.O_TAG_REMOVETAG) {

				optionLocator = "css=div[id$='__TAG_MENU|MENU'] div[id='message_removetag'] td[id$='_title']";
				page = null;

				// FALL THROUGH
				
			} else {
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
			}
			
		} else {
			
			throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
			
		}
		
		
		if (pulldownLocator != null) {

			this.zClickAt(pulldownLocator,"");

			if (optionLocator != null) {

				this.zClickAt(optionLocator,"");

			}
			
		}
		
		if ( page != null ) {
			page.zWaitForActive();
		}
			
		return (page);

	}
			


	
	public AbsPage zToolbarPressPulldown(Button button, Object dynamic) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +", "+ dynamic +")");

		tracer.trace("Click pulldown "+ button +" then "+ dynamic);

		if ( button == null )
			throw new HarnessException("Button cannot be null!");

		if ( dynamic == null )
			throw new HarnessException("Dynamic cannot be null!");


		// Default behavior variables
		//
		String container = "css=div[id^='ztb__MSG']";
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_TAG ) {

			if ( !(dynamic instanceof String) ) 
				throw new HarnessException("if button = B_TAG, then dynamic should be a tag name");
			String tagname = (String)dynamic;
			
			pulldownLocator = container + " div[id$='__TAG_MENU'] td[id$='_dropdown']>div";
			optionLocator = "css=div[id$='__TAG_MENU|MENU'] td[id$='_title']:contains("+ tagname +")";
			page = null;

			// FALL THROUGH

		} else {
			
			throw new HarnessException("no logic defined for button "+ button);
			
		}

		if (pulldownLocator != null) {

			this.zClickAt(pulldownLocator,"");

			if (optionLocator != null) {

				this.zClickAt(optionLocator,"");

			}
			
		}
			
		return (page);

	}

	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {
		logger.info(myPageName() + " zKeyboardShortcut("+ shortcut +")");
		
		if (shortcut == null)
			throw new HarnessException("Shortcut cannot be null");

		tracer.trace("Using the keyboard, press the "+ shortcut.getKeys() +" keyboard shortcut");

		AbsPage page = null;

		if (shortcut== Shortcut.S_ESCAPE) {

			// Close the window
			zKeyDown("27");
			return page;

		}


		zTypeCharacters(shortcut.getKeys());

		return (page);	
		
	}

	public boolean zHasShareADButtons() throws HarnessException {
		// Haven't fully baked this method.  
		// Maybe it works.  
		// Maybe it needs to check "visible" and/or x/y/z coordinates

		List<String> locators = Arrays.asList(
				this.ContainerLocator + " td[id$='__Shr__SHARE_ACCEPT_title']",
				this.ContainerLocator + " td[id$='__Shr__SHARE_DECLINE_title']");

		for (String locator : locators) {
			if ( !this.sIsElementPresent(locator) )
				return (false);
		}
		
		return (true);
	}

	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zDisplayPressButton("+ button +")");
		
		tracer.trace("Click "+ button);

		AbsPage page = this;
		String locator = null;
		boolean doPostfixCheck = false;

		if ( button == Button.B_VIEW_ENTIRE_MESSAGE ) {
			
			locator = this.ContainerLocator + " span[id$='_msgTruncation_link']";

			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("locator is not present for button "+ button +" : "+ locator);
			
			this.sClick(locator); // sClick() is required for this element
			
			this.zWaitForBusyOverlay();

			return (page);

		} else if ( button == Button.B_HIGHLIGHT_OBJECTS ) {

			locator = this.ContainerLocator + " span[id$='_highlightObjects_link']";


			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("locator is not present for button "+ button +" : "+ locator);
			
			this.sClick(locator); // sClick() is required for this element
			
			this.zWaitForBusyOverlay();

			return (page);

		} else if ( button == Button.B_ACCEPT ) {
			
			locator = DisplayMail.Locators.AcceptButton;
			page = null;
			doPostfixCheck = true;
		
		} else if ( button == Button.O_ACCEPT_NOTIFY_ORGANIZER ) {
			
			locator = DisplayMail.Locators.AcceptNotifyOrganizerMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.O_ACCEPT_EDIT_REPLY ) {
			
			locator = DisplayMail.Locators.AcceptEditReplyMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.O_ACCEPT_DONT_NOTIFY_ORGANIZER ) {
			
			locator = DisplayMail.Locators.AcceptDontNotifyOrganizerMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.B_TENTATIVE ) {
			
			locator = DisplayMail.Locators.TentativeButton;
			page = null;
			doPostfixCheck = true;
		
		} else if ( button == Button.O_TENTATIVE_NOTIFY_ORGANIZER ) {
			
			locator = DisplayMail.Locators.TentativeNotifyOrganizerMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.O_TENTATIVE_EDIT_REPLY ) {
			
			locator = DisplayMail.Locators.TentativeEditReplyMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.O_TENTATIVE_DONT_NOTIFY_ORGANIZER ) {
			
			locator = DisplayMail.Locators.TentativeDontNotifyOrganizerMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.B_DECLINE ) {
			
			locator = DisplayMail.Locators.DeclineButton;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.O_DECLINE_NOTIFY_ORGANIZER ) {
			
			locator = DisplayMail.Locators.DeclineNotifyOrganizerMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.O_DECLINE_EDIT_REPLY ) {
			
			locator = DisplayMail.Locators.DeclineEditReplyMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.O_DECLINE_DONT_NOTIFY_ORGANIZER ) {
			
			locator = DisplayMail.Locators.DeclineDontNotifyOrganizerMenu;
			page = null;
			doPostfixCheck = true;
			
		} else if ( button == Button.B_PROPOSE_NEW_TIME ) {
			
			locator = DisplayMail.Locators.ProposeNewTimeButton;
			page = null;

		} else if ( button == Button.B_ACCEPT_SHARE ) {

			locator = this.ContainerLocator + " td[id$='__Shr__SHARE_ACCEPT_title']";
			page = new DialogShareAccept(MyApplication, ((AppAjaxClient) MyApplication).zPageMail);
			doPostfixCheck = true;

		} else if ( button == Button.B_DECLINE_SHARE ) {

			locator = this.ContainerLocator + " td[id$='__Shr__SHARE_DECLINE_title']";
			page = new DialogShareDecline(MyApplication, ((AppAjaxClient) MyApplication).zPageMail);
			doPostfixCheck = true;

		} else  {
			
			throw new HarnessException("no implementation for button: "+ button);

		}
		
		if ( locator == null )
			throw new HarnessException("no locator defined for button "+ button);
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("locator is not present for button "+ button +" : "+ locator);
		
		this.zClick(locator);
		
		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}
		
		if ( doPostfixCheck ) {
			// Make sure the response is delivered before proceeding
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
		}

		return (page);
	}
}
