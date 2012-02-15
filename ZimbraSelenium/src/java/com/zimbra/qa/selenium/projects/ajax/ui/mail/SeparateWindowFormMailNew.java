/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.util.List;

import com.zimbra.qa.selenium.framework.core.SeleniumService;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsSeparateWindow;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.SeparateWindowDialog;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;



/**
 * Represents a "Compose in New Window" form
 * <p>
 * @author Matt Rhoades
 *
 */
public class SeparateWindowFormMailNew extends AbsSeparateWindow {

	public static class Locators {

	}
	


	public SeparateWindowFormMailNew(AbsApplication application) {
		super(application);
		
		this.DialogWindowTitle = "Compose";
		
	}
	
	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	public void zFill(MailItem mail) throws HarnessException {
		logger.info(myPageName() + ".zFill(MailItem)");
		logger.info(mail.prettyPrint());

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

	public void zFillField(Field field, String value) throws HarnessException {
		logger.info(myPageName() + "zFillField("+ field +", "+ value +")");

		tracer.trace("Set "+ field +" to "+ value);

		String container = "css=div[id^='zv__COMPOSE']";
		String locator = null;
		
		if ( field == Field.To ) {
			
			locator = container + " tr[id$='_to_row'] input[id$='_to_control']";
			
			// FALL THROUGH
			
		} else if ( field == Field.Cc ) {
			
			locator = container + " tr[id$='_cc_row'] input[id$='_cc_control']";
			
			// FALL THROUGH
			
		} else if ( field == Field.Bcc ) {
			
			locator = container + " tr[id$='_bcc_row'] input[id$='_bcc_control']";
			
			// Make sure the BCC field is showing
			if ( !zBccIsActive() ) {
				this.zToolbarPressButton(Button.B_SHOWBCC);
			}
			
			// FALL THROUGH
			
		} else if ( field == Field.Subject ) {
			
			locator = container + " tr[id$='_subject_row'] input[id$='_subject_control']";

			// FALL THROUGH
			
		} else if (field == Field.Body) {

			// For some reason, the client expects a bit of a delay here.
			// A cancel compose will not register unless this delay is here
			// projects.ajax.tests.mail.compose.CancelComposeHtml.CancelComposeHtml_01
			// http://zqa-004.eng.vmware.com/testlogs/UBUNTU10_64/HELIX/20110621210101_FOSS/SelNG-projects-ajax-tests/130872172760061/zqa-442.eng.vmware.com/AJAX/firefox_3.6.12/en_US/debug/projects/ajax/tests/mail/compose/CancelComposeHtml/CancelComposeHtml_01.txt
			//
			SleepUtil.sleepLong();

			int frames = sGetCssCount("css=iframe");
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
					this.sType(locator, value);

					return;

				} else if (frames == 2) {

					//locator = "css=iframe[id^='iframe_DWT']";
					locator ="css=iframe[id$='_content_ifr']";
					if (!this.sIsElementPresent(locator))
						throw new HarnessException(
								"Unable to locate compose body");

					zTypeFormattedText(locator, value);

					return;

				}

			} else {
				if (frames == 0) {
					
					// //
					// Text compose
					// //

					sType("css=textarea[class='DwtHtmlEditorTextArea']", value);

					return;

				} else if (frames == 1) {
					
					// //
					// HTML compose
					// //
					try {

						sSelectFrame("index=0"); // iframe index is 0 based

						locator = "css=body[id='tinymce']";

						if (!sIsElementPresent(locator))
							throw new HarnessException("Unable to locate compose body");

						sFocus(locator);
						zClick(locator);

						/*
						 * Oct 25, 2011: The new TinyMCE editor broke sType().  Use zKeyboard instead,
						 * however, it is preferred to use sType() if possible, but I can't find a
						 * solution right now. 
						 */
						// sType("css=iframe[id$='_content_ifr']", "css=html body", value);

						// this.sType(locator, value);
						zTypeCharacters(value);
						
					} finally {
						// Make sure to go back to the original iframe
						this.sSelectFrame("relative=top");

					}

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
		
		sType(locator, value);
		
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


	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		tracer.trace("Press the "+ button +" button");

		if ( button == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String container = "css=div[id^='ztb__COMPOSE']";
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_SEND ) {

			locator = container + " div[id$='__SEND'] td[id$='_title']";
			page = null;
			
			this.zClickAt(locator,"0,0");
			
			Stafpostqueue postqueue = new Stafpostqueue();
			postqueue.waitForPostqueue();
			
			return (page);

		} else if ( button == Button.B_CANCEL ) {

			locator = container + " div[id$='__CANCEL'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_SAVE_DRAFT ) {

			locator = container + " div[id$='__SAVE_DRAFT'] td[id$='_title']";
			page = null;

			this.zClickAt(locator,"0,0");

			this.zWaitForBusyOverlay();

			return (page);

		} else if ( button == Button.B_ADD_ATTACHMENT ) {

			locator = container + " div[id$='__ATTACHMENT'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_SPELL_CHECK ) {

			locator = container + " div[id$='__SPELL_CHECK'] td[id$='_title']";
			page = null;

			// FALL THROUGH

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
			
			pulldownLocator = "css=td[id$='___priority_dropdown']>div";

			if ( option == Button.O_PRIORITY_HIGH ) {
				
				optionLocator = "css=td[id$='_left_icon']>div[class='ImgPriorityHigh_list']";
				page = null;

			} else if ( option == Button.O_PRIORITY_NORMAL ) {
				
				optionLocator = "css=td[id$='_left_icon']>div[class='ImgPriorityNormal_list']";
				page = null;

			} else if ( option == Button.O_PRIORITY_LOW ) {
				
				optionLocator = "css=td[id$='_left_icon']>div[class='ImgPriorityLow_list']";
				page = null;

			} else {
				throw new HarnessException("unsupported priority option "+ option);
			}
		
		} else {
			throw new HarnessException("no logic defined for pulldown "+ pulldown);
		}

		// Default behavior
		if ( pulldownLocator != null ) {
						
			zClickAt(pulldownLocator, "");

			if ( optionLocator != null ) {

				zClickAt(optionLocator, "");

			}
			
		}
		
		// Return the specified page, or null if not set
		return (page);
	}

	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {
		logger.info(myPageName() + " zKeyboardShortcut("+ shortcut +")");
		
		if (shortcut == null)
			throw new HarnessException("Shortcut cannot be null");

		tracer.trace("Using the keyboard, press the "+ shortcut.getKeys() +" keyboard shortcut");

		AbsPage page = null;

		if (shortcut== Shortcut.S_ESCAPE) {

			// This dialog may or may not appear, depending on the message content
			page = new SeparateWindowDialog(
					DialogWarning.DialogWarningID.SaveCurrentMessageAsDraft,
					this.MyApplication,
					this);
			((AbsSeparateWindow)page).zSetWindowTitle(DialogWindowTitle);
			((AbsSeparateWindow)page).zSetWindowID(DialogWindowTitle);

			zKeyDown("27");
			return page;

		}


		zTypeCharacters(shortcut.getKeys());

		return (page);	
		
	}

}
