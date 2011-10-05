package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.util.*;

import com.zimbra.qa.selenium.framework.core.SeleniumService;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.AutocompleteEntry.Icon;




/**
 * The <code>FormMailNew<code> object defines a compose new message view
 * in the Zimbra Ajax client.
 * <p>
 * This class can be used to compose a new message.
 * <p>
 * 
 * @author Matt Rhoades
 * @see http://wiki.zimbra.com/wiki/Testing:_Selenium:_ZimbraSelenium_Overview#Mail_Page
 */
public class FormMailNew extends AbsForm {
	
	/**
	 * Defines Selenium locators for various objects in {@link FormMailNew}
	 */
	public static class Locators {
		
		public static final String zSendIconBtn			= "css=[id^=zb__COMPOSE][id$=__SEND_title]";
		public static final String zCancelIconBtn		= "css=[id^=zb__COMPOSE][id$=__CANCEL_title]";
		public static final String zSaveDraftIconBtn	= "css=[id^=zb__COMPOSE][id$=__SAVE_DRAFT_title]";
		public static final String zSpellCheckIconBtn	= "css=[id^=zb__COMPOSE][id$=__SPELL_CHECK_title]";

		public static final String zToField				= "css=[id^=zv__COMPOSE][id$=_to_control]";
		public static final String zCcField				= "css=[id^=zv__COMPOSE][id$=_cc_control]";
		public static final String zBccField			= "css=[id^=zv__COMPOSE][id$=_bcc_control]";
		public static final String zSubjectField		= "css=div[id^=zv__COMPOSE] input[id$=_subject_control]";
		public static final String zAttachmentField     = "css=div[id$=_attachments_div]";
		public static final String zAttachmentImage     = "css=div[id$=_attachments_div] div[class='ImgAttachment']";
		public static final String zAttachmentCheckbox  = "css=div[id$=_attachments_div] input[name='ZmComposeView_forAttName1']";			
		public static final String zAttachmentText      = "css=div[id$=_attachments_div] a[class='AttLink']:contains(";
		public static final String zLinkText 			= "css=iframe[id*='DWT'][class*='Editor']";
		
		public static final String zBodyFrameHTML		= "//div[contains(id,'zv__COMPOSE')]//iframe";
		
		public static final String zPriorityPulldown	= "css=[id*='__COMPOSE'][id$='___priority_dropdown']";
		public static final String zPriorityOptionHigh	= "css=[id*='__COMPOSE'][id$='___priority_dropdown']";
		public static final String zPriorityOptionNormal	= "css=[id*='__COMPOSE'][id$='___priority_dropdown']";
		public static final String zPriorityOptionLow	= "css=[id*='__COMPOSE'][id$='___priority_dropdown']";
		
		public static final String zBubbleToField		= "css=[id^=zv__COMPOSE][id$=_to_cell]";
		public static final String zBubbleCcField		= "css=[id^=zv__COMPOSE][id$=_cc_cell]";
		public static final String zBubbleBccField		= "css=[id^=zv__COMPOSE][id$=_bcc_cell]";
		
	}

	public static class Field {
	
		public static final Field To = new Field("To");
		public static final Field Cc = new Field("Cc");
		public static final Field Bcc = new Field("Bcc");
		public static final Field Subject = new Field("Subject");
		public static final Field Body = new Field("Body");
		
		
		private String field;
		private Field(String name) {
			field = name;
		}
		
		@Override
		public String toString() {
			return (field);
		}

	}
	
	
	/**
	 * Protected constuctor for this object.  Only classes within
	 * this package should create DisplayMail objects.
	 * 
	 * @param application
	 */
	public FormMailNew(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormMailNew.class.getCanonicalName());

	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	

	@Override
	public void zSubmit() throws HarnessException {
		logger.info("FormMailNew.submit()");
		
		zToolbarPressButton(Button.B_SEND);

		this.zWaitForBusyOverlay();

	}

	/**
	 * Press the toolbar button
	 * @param button
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		tracer.trace("Click button "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
		// Fallthrough objects
		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_SEND ) {
			
			locator = Locators.zSendIconBtn;
			
			// Click on send
			this.zClick(locator);
			
			this.zWaitForBusyOverlay();
			
			// Wait for the message to be delivered
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
		
			
			return (page);
		
		} else if ( button == Button.B_CANCEL ) {

			locator = Locators.zCancelIconBtn;
			page = new DialogWarning(DialogWarning.DialogWarningID.SaveCurrentMessageAsDraft, this.MyApplication, ((AppAjaxClient)this.MyApplication).zPageMail);
			
			// If the compose view is not dirty (i.e. no pending changes)
			// then the dialog will not appear.  So, click the button
			// and return the page, without waiting for it to be active
						
			this.zClick(locator);

			this.zWaitForBusyOverlay();

			// Return the page, if specified
			return (page);
			
		} else if ( button == Button.B_SAVE_DRAFT ) {

			locator = Locators.zSaveDraftIconBtn;
			page = this;
			
			// FALL THROUGH
			
		} else if ( button == Button.B_ADD_ATTACHMENT ) {

			throw new HarnessException("implement me (?)");
			
			// FALL THROUGH
			
		} else if ( button == Button.B_SPELL_CHECK ) {

			locator = Locators.zSpellCheckIconBtn;
			page = this;
			
			// FALL THROUGH
			
		} else if ( button == Button.B_SIGNATURE ) {

			throw new HarnessException("use zToolbarPressPulldown to attach signature");
			
		} else if ( button == Button.B_OPTIONS ) {

			throw new HarnessException("use zToolbarPressPulldown to attach signature");
			
		} else if ( button == Button.B_TO ) {
			
			locator = "css=div[id$='__TO'] td[id$='__TO_title']";
			page = new FormAddressPicker(this.MyApplication);

		} else if ( button == Button.B_CC ) {
			
			locator = "css=div[id$='__CC'] td[id$='__CC_title']";
			page = new FormAddressPicker(this.MyApplication);

		} else if ( button == Button.B_BCC ) {
			
			// In the test case, make sure B_SHOWBCC was activated first
			// (i.e. make sure BCC button is showing
			
			throw new HarnessException("implement me");

		} else if ( button == Button.B_SHOWBCC) {

			page = this;
			locator = "xpath=//div[contains(@id,'zv__COMPOSE')]//a[contains(@id,'_toggle_bcc')]";

			if ( zBccIsActive() )
				return (this);
			
			////
			// For some reason, zClick doesn't work for "Show BCC", but sClick does
			////
			
			// Click it
			this.sClick(locator);
			
			this.zWaitForBusyOverlay();

			return (page);
		}
		else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		// Make sure a locator was set
		if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		
		// Default behavior, process the locator by clicking on it
		//
		
		// Click it
		this.zClick(locator);

		// if the app is busy, wait for it to become active again
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			
			// Make sure the page becomes active
			page.zWaitForActive();
			
		}
		
		// Return the page, if specified
		return (page);

	}
	
	/**
	 * Press the toolbar pulldown and the menu option
	 * @param pulldown
	 * @param option
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown("+ pulldown +", "+ option +")");
		
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
		
		if ( pulldown == Button.B_PRIORITY ) {
			
			if ( option == Button.O_PRIORITY_HIGH ) {
				
				// TODO
				pulldownLocator = Locators.zPriorityPulldown;

            // Have to use xpath because there is no unique identifier to select the text "High" and by using xpath, it selects the text "high" through the sibling relationship.
            // When using the css to point to the icon, it clicks on the outside of the drop down menu
            // , therefore it ends up closing and selecting nothing
            optionLocator = "//div[@class='ImgPriorityHigh_list']/../../td[@class='ZWidgetTitle']";
				page = this;

			} else if ( option == Button.O_PRIORITY_NORMAL ) {
				
				// TODO
				pulldownLocator = Locators.zPriorityPulldown;
				optionLocator = "css=[class='ImgPriorityNormal_list']";
				page = this;

			} else if ( option == Button.O_PRIORITY_LOW ) {
				
				// TODO
				pulldownLocator = Locators.zPriorityPulldown;
				optionLocator = "css=[class='ImgPriorityLow_list']";
				page = this;

			} else {
				throw new HarnessException("unsupported priority option "+ option);
			}
		
		} else if ( pulldown == Button.B_SEND ) {
			
			pulldownLocator = "css=div[id$='__SEND_MENU'] td[id$='__SEND_MENU_dropdown']>div";

			if ( option == Button.O_SEND_SEND ) {

				// This action requires a 'wait for postfix queue'
				optionLocator = "css=tr#POPUP_SEND td#SEND_title";
				page = null;
				
				// Make sure the locator exists
				if ( !this.sIsElementPresent(pulldownLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
				}
				
				// For some reason, zClick() activates the entire button, not the pulldown
				// As a work around use right click
				// this.zClick(pulldownLocator);
				this.zRightClick(pulldownLocator);

				this.zWaitForBusyOverlay();
				
				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}
					
				this.zClick(optionLocator);

				this.zWaitForBusyOverlay();

				// Wait for the message to be delivered
				Stafpostqueue sp = new Stafpostqueue();
				sp.waitForPostqueue();
			
				
				return (page);
				


			} else if ( option == Button.O_SEND_SEND_LATER ) {
				
				optionLocator = "css=tr#POPUP_SEND_LATER td#SEND_LATER_title";
				page = new DialogSendLater(this.MyApplication, ((AppAjaxClient)MyApplication).zPageMail);

				// Make sure the locator exists
				if ( !this.sIsElementPresent(pulldownLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
				}
				
				// For some reason, zClick() activates the entire button, not the pulldown
				// As a work around use right click
				// this.zClick(pulldownLocator);
				this.zRightClick(pulldownLocator);

				this.zWaitForBusyOverlay();
				
				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}
					
				this.zClick(optionLocator);

				this.zWaitForBusyOverlay();


				page.zWaitForActive();
				
				return (page);

			} else {
				throw new HarnessException("unsupported pulldown/option "+ pulldown +"/"+ option);
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

			this.zWaitForBusyOverlay();
			
			if ( optionLocator != null ) {

				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}
				
				this.zClick(optionLocator);

				this.zWaitForBusyOverlay();

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
	
	/**
	 * Fill in the form field with the specified text
	 * @param field
	 * @param value
	 * @throws HarnessException
	 */
	public void zFillField(Field field, String value) throws HarnessException {
	
		tracer.trace("Set "+ field +" to "+ value);

		String locator = null;
		
		if ( field == Field.To ) {
			
			locator = Locators.zToField;
			
			// FALL THROUGH
			
		} else if ( field == Field.Cc ) {
			
			locator = Locators.zCcField;
			
			// FALL THROUGH
			
		} else if ( field == Field.Bcc ) {
			
			locator = Locators.zBccField;
			
			// Make sure the BCC field is showing
			if ( !zBccIsActive() ) {
				this.zToolbarPressButton(Button.B_SHOWBCC);
			}
			
			// FALL THROUGH
			
		} else if ( field == Field.Subject ) {
			
			locator = Locators.zSubjectField;
			
			// FALL THROUGH
			
		} else if (field == Field.Body) {

			// For some reason, the client expects a bit of a delay here.
			// A cancel compose will not register unless this delay is here
			// projects.ajax.tests.mail.compose.CancelComposeHtml.CancelComposeHtml_01
			// http://zqa-004.eng.vmware.com/testlogs/UBUNTU10_64/HELIX/20110621210101_FOSS/SelNG-projects-ajax-tests/130872172760061/zqa-442.eng.vmware.com/AJAX/firefox_3.6.12/en_US/debug/projects/ajax/tests/mail/compose/CancelComposeHtml/CancelComposeHtml_01.txt
			//
			SleepUtil.sleepLong();

			int frames = this.sGetCssCount("css=iframe");
			logger.debug("Body: # of frames: " + frames);
			String browser = SeleniumService.getInstance().getSeleniumBrowser();
			/*
			 * Added IE specific condition because IE recognized frame=1 for text compose and frame=2 for html compose
			 */
			if (browser.equalsIgnoreCase("iexplore")) {
				if (frames == 1) {
					// //
					// Text compose
					// //

					locator = "css=textarea[id*='textarea_']";

					if (!this.sIsElementPresent(locator))
						throw new HarnessException(
								"Unable to locate compose body");

					this.sFocus(locator);
					this.zClick(locator);
					this.zWaitForBusyOverlay();
					this.sType(locator, value);

					return;

				} else if (frames == 2) {

					//locator = "css=iframe[id^='iframe_DWT']";
					locator ="css=iframe[id$='_content_ifr']";
					if (!this.sIsElementPresent(locator))
						throw new HarnessException(
								"Unable to locate compose body");

					zTypeFormattedText(locator, value);

					// Is this requried?
					this.zWaitForBusyOverlay();

					return;

				}

			} else {
				if (frames == 0) {
					// //
					// Text compose
					// //

					locator = "css=textarea[class='DwtHtmlEditorTextArea']";

					if (!this.sIsElementPresent(locator))
						throw new HarnessException("Unable to locate compose body");

					this.sFocus(locator);
					this.zClick(locator);
					this.zWaitForBusyOverlay();
					this.sType(locator, value);

					return;

				} else if (frames == 1) {
					// //
					// HTML compose
					// //

					try {

						this.sSelectFrame("index=0"); // iframe index is 0 based

						locator = "css=html body";

						if (!this.sIsElementPresent(locator))
							throw new HarnessException(
									"Unable to locate compose body");

						this.sFocus(locator);
						this.zClick(locator);
						this.sType(locator, value);

					} finally {
						// Make sure to go back to the original iframe
						this.sSelectFrame("relative=top");

					}

					// Is this requried?
					this.zWaitForBusyOverlay();

					return;
					
				} else if (frames == 2) {
					// //
					// HTML compose
					// //

					try {

						this.sSelectFrame("css=iframe[id$='_content_ifr']"); // iframe index is 0 based

						locator = "css=html body";

						if (!this.sIsElementPresent(locator))
							throw new HarnessException(
									"Unable to locate compose body");

						this.sFocus(locator);
						this.zClick(locator);
						this.sType(locator, value);

					} finally {
						// Make sure to go back to the original iframe
						this.sSelectFrame("relative=top");

					}

					// Is this requried?
					this.zWaitForBusyOverlay();

					return;
					
				} else {
					throw new HarnessException("Compose //iframe count was " + frames);
				}
			}

		} else {
			throw new HarnessException("not implemented for field " + field);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator was null for field "+ field);
		}
		
		// Default behavior, enter value into locator field
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		// Seems that the client can't handle filling out the new mail form too quickly
		// Click in the "To" fields, etc, to make sure the client is ready
		this.sFocus(locator);
		this.zClick(locator);
		this.zWaitForBusyOverlay();

		// Enter text
		this.sType(locator, value);
		
		this.zWaitForBusyOverlay();

	}
	
	
	private boolean zBccIsActive() throws HarnessException {
		logger.info(myPageName() + ".zBccIsActive()");

		// <tr id='zv__COMPOSEX_bcc_row' style='display: table_row' x-display='table-row' ...
		// <tr id='zv__COMPOSEX_bcc_row' style='display: none'  x-display='table-row' ...
		
		String locator;
		
		locator = "css=div[id^='zv__COMPOSE'] tr[id$='_bcc_row']";
		if ( !sIsElementPresent(locator) )
			throw new HarnessException("Unable to locate the BCC field "+ locator);
		
		locator = locator + "[style*=none]";
		return (!sIsElementPresent(locator));
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info(myPageName() + ".zFill(ZimbraItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a MailItem
		if ( !(item instanceof MailItem) ) {
			throw new HarnessException("Invalid item type - must be MailItem");
		}
		
		// Convert object to MailItem
		MailItem mail = (MailItem) item;
		
		// Fill out the form
		//
		
		// Handle the subject
		if ( mail.dSubject != null ) {
			
			zFillField(Field.Subject, mail.dSubject);

		}
		
		if ( mail.dBodyText != null ) {
			
			zFillField(Field.Body, mail.dBodyText);
			
		}
		if ( mail.dBodyHtml != null ) {
			
			zFillField(Field.Body, mail.dBodyHtml);
			
		}
		
		
		// Handle the Recipient list, which can be a combination
		// of To, Cc, Bcc, and From
		StringBuilder to = null;
		StringBuilder cc = null;
		StringBuilder bcc = null;
		StringBuilder from = null;
		
		// Convert the list of recipients to a semicolon separated string
		List<RecipientItem> recipients = mail.dAllRecipients();
		if ( recipients != null ) {
			if ( !recipients.isEmpty() ) {
				
				for (RecipientItem r : recipients) {
					if ( r.dType == RecipientType.To ) {
						if ( to == null ) {
							to = new StringBuilder();
							to.append(r.dEmailAddress);
						} else {
							to.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.Cc ) {
						if ( cc == null ) {
							cc = new StringBuilder();
							cc.append(r.dEmailAddress);
						} else {
							cc.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.Bcc ) {
						if ( bcc == null ) {
							bcc = new StringBuilder();
							bcc.append(r.dEmailAddress);
						} else {
							bcc.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.From ) {
						if ( from == null ) {
							from = new StringBuilder();
							from.append(r.dEmailAddress);
						} else {
							from.append(";").append(r.dEmailAddress);
						}
					}
				}
				
			}
		}
		
		// Fill out the To field
		if ( to != null ) {
			this.zFillField(Field.To, to.toString());
		}
		
		if ( cc != null ) {
			this.zFillField(Field.Cc, cc.toString());
		}
		
		if ( bcc != null ) {
			this.zFillField(Field.Bcc, bcc.toString());
		}

		
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
		
		// Look for the div
		String locator = "css=div[id^='ztb__COMPOSE']";
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false);	
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);
		}
		
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}
	
	public boolean zHasAttachment(String name)  throws HarnessException {
	    
	    //verify clipper image existed, checkbox is checked, and  attachment file name
	    
	    return  sIsElementPresent(Locators.zAttachmentImage) &&
	            sIsChecked(Locators.zAttachmentCheckbox) &&
	            sIsElementPresent(Locators.zAttachmentText + "'" + name + "'" + ")");	    		   
	}

	/**
	 * Autocompleting is more complicated than zFillField().  Use this
	 * method when filling out a field that will autocomplete.
	 * @param field The form field to use (to, cc, bcc, etc.)
	 * @param value The partial string to use to autocomplete
	 * @throws HarnessException 
	 */
	public List<AutocompleteEntry> zAutocompleteFillField(Field field, String value) throws HarnessException {
		logger.info(myPageName() + " zAutocompleteFillField("+ field +", "+ value +")");

		tracer.trace("Set "+ field +" to "+ value);

		String locator = null;
		
		if ( field == Field.To ) {
			
			locator = Locators.zToField;
			
			// FALL THROUGH
			
		} else if ( field == Field.Cc ) {
			
			locator = Locators.zCcField;
			
			// FALL THROUGH
			
		} else if ( field == Field.Bcc ) {
			
			locator = Locators.zBccField;
			
			// Make sure the BCC field is showing
			if ( !zBccIsActive() ) {
				this.zToolbarPressButton(Button.B_SHOWBCC);
			}
			
			// FALL THROUGH
			
		} else {
			throw new HarnessException("Unsupported field: "+ field);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator was null for field "+ field);
		}
		
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		// Seems that the client can't handle filling out the new mail form too quickly
		// Click in the "To" fields, etc, to make sure the client is ready
		this.sFocus(locator);
		this.zClick(locator);
		this.zWaitForBusyOverlay();

		// Instead of sType() use zKeyboard
		this.zKeyboard.zTypeCharacters(value);
		
		this.zWaitForBusyOverlay();

		waitForAutocomplete();
		
// logger.info(this.sGetHtmlSource());		// For debugging

		return (zAutocompleteListGetEntries());
		
	}

	
	/**

  <div x-display="block" parentid="z_shell" class="ZmAutocompleteListView" style="position: absolute; overflow: auto; display: block; left: 263px; top: 132px; z-index: 750;" id="zac__COMPOSE-1">
    <table id="DWT117" border="0" cellpadding="0" cellspacing="0">
      <tbody>
        <tr id="zac__COMPOSE-1_acRow_0" class="acRow-selected">
          <td class="Icon">
            <div class="ImgContact" style=""></div>
          </td>

          <td>"&Atilde;&lsquo;&Atilde;&copy;&Atilde;&iexcl;l Wilson"
          &lt;enus13173367893124@testdomain.com&gt;</td>

          <td class="Link"></td>

          <td class="Link"></td>
        </tr>
      </tbody>
    </table>
  </div>
  <span style= "position: absolute; left: -10000px; top: -10000px; font-size: 13.3333px;">&Atilde;&lsquo;&Atilde;&copy;&Atilde;&iexcl;l</span>


  <div x-display="block" style="position: absolute; left: 263px; top: 132px; z-index: 100; display: none;" class="acWaiting">
    <table border="0" cellpadding="0" cellspacing="0">
      <tbody>
        <tr>
          <td>
            <div class="ImgSpinner"></div>
          </td>

          <td>Autocompleting...</td>
        </tr>
      </tbody>
    </table>
  </div>

		 */

	/**
	 * Wait for the autocomplete spinner to go away
	 */
	protected void waitForAutocomplete() throws HarnessException {
		String locator = "css=div[class='acWaiting'][style*='display: none;']";
		for (int i = 0; i < 30; i++) {
			if ( this.sIsElementPresent(locator) )
				return; // Found it!
			SleepUtil.sleep(1000);
		}
		throw new HarnessException("autocomplete never completed");
	}
	
	protected AutocompleteEntry parseAutocompleteEntry(String itemLocator) throws HarnessException {
		logger.info(myPageName() + " parseAutocompleteEntry()");

		String locator = null;
		
		// Get the icon
		locator = itemLocator + " td.Icon div@class";
		String image = this.sGetAttribute(locator);
		
		// Get the address
		locator = itemLocator + " td + td";
		String address = this.sGetText(locator);
		
		AutocompleteEntry entry = new AutocompleteEntry(
									Icon.getIconFromImage(image), 
									address, 
									false,
									itemLocator);

		return (entry);
	}
	
	public List<AutocompleteEntry> zAutocompleteListGetEntries() throws HarnessException {
		logger.info(myPageName() + " zAutocompleteListGetEntries()");
		
		List<AutocompleteEntry> items = new ArrayList<AutocompleteEntry>();
		
		String containerLocator = "css=div[id^='zac__COMPOSE-'][style*='display: block;']";

		if ( !this.sIsElementPresent(containerLocator) ) {
			// Autocomplete is not visible, return an empty list.
			return (items);
		}

		
		String rowsLocator = containerLocator + " tr[id*='_acRow_']";
		int count = this.sGetCssCount(rowsLocator);
		for (int i = 0; i < count; i++) {
			
			items.add(parseAutocompleteEntry(containerLocator + " tr[id$='_acRow_"+ i +"']"));
			
		}
		
		return (items);
	}

	public void zAutocompleteSelectItem(AutocompleteEntry entry) throws HarnessException {
		logger.info(myPageName() + " zAutocompleteSelectItem("+ entry +")");
		
		// Click on the address
		this.sMouseDown(entry.getLocator() + " td + td");
		this.zWaitForBusyOverlay();
		
	}
	
	public void zAutocompleteForgetItem(AutocompleteEntry entry) throws HarnessException {
		logger.info(myPageName() + " zAutocompleteForgetItem("+ entry +")");
		
		// Mouse over the entry
		zAutocompleteMouseOverItem(entry);
		
		// Click on the address
		this.sMouseDown(entry.getLocator() + " div[id*='_acForgetText_']");
		this.zWaitForBusyOverlay();
		
	}

	public void zAutocompleteMouseOverItem(AutocompleteEntry entry) throws HarnessException {
		logger.info(myPageName() + " zAutocompleteMouseOverItem("+ entry +")");
		
		// Click on the address
		this.sMouseOver(entry.getLocator() + " td + td");
		this.zWaitForBusyOverlay();
		
	}

	
}
