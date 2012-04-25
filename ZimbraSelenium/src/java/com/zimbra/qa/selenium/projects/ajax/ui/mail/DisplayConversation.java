/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;

/**
 * @author zimbra
 *
 */
public class DisplayConversation extends DisplayMail {

	protected DisplayConversation(AbsApplication application) {
		super(application);

		logger.info("new " + DisplayConversation.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zDisplayPressButton("+ button +")");
				
		tracer.trace("Click "+ button);

		AbsPage page = this;
		String locator = null;
		boolean doPostfixCheck = false;

		if ( button == Button.B_QUICK_REPLY_REPLY ) {
			
			locator = "css=div.footer a[id$='__footer_reply']";
			this.sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (null);

		} else if ( button == Button.B_QUICK_REPLY_REPLY_ALL ) {
			
			locator = "css=div.footer a[id$='__footer_replyAll']";
			this.sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (null);

		} else if ( button == Button.B_QUICK_REPLY_FORWARD ) {
			
			locator = "css=div.footer a[id$='__footer_forward']";
			page = new FormMailNew(this.MyApplication);

			this.sClick(locator);
			this.zWaitForBusyOverlay();

			page.zWaitForActive();
			
			return (page);

		} else if ( button == Button.B_QUICK_REPLY_MORE_ACTIONS ) {
			
			locator = "css=div.footer a[id$='__footer_moreActions']";
			this.sClick(locator);
			this.zWaitForBusyOverlay();
			
			return (null);

		} else if ( button == Button.B_QUICK_REPLY_SEND ) {

			locator = "css=div[id='zb__CV2__Rep__SEND'] td[id$='_title']";
			page = null;
			doPostfixCheck = true;

		} else if ( button == Button.B_QUICK_REPLY_CANCEL ) {

			locator = "css=div[id='zb__CV2__Rep__CANCEL'] td[id$='_title']";
			page = null;
			doPostfixCheck = false;

		} else if ( button == Button.B_QUICK_REPLY_MORE ) {

			locator = "css=div[id='zb__CV2__Rep__REPLY_ALL'] td[id$='_title']";
			page = new FormMailNew(this.MyApplication);
			doPostfixCheck = false;

		} else  {

			// Pass all other actions to DisplayMail
			return (super.zPressButton(button));

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

	/**
	 * Set the "Quick Reply" content
	 * @param reply The text to set the content area as
	 * @throws HarnessException 
	 */
	public void zFillField(Field field, String value) throws HarnessException {
		
		tracer.trace("Set "+ field +" to "+ value);

		String locator = null;
		
		if ( field == Field.Body ) {

			locator = "css=div@zv__CLV-main textarea[id$='_replyInput']";
				
		} else {
			
			throw new HarnessException("not implemented for field " + field);
//			super.zFillField(field, value);
//			return;
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
	
	/**
	 * Get the Quick Reply Placeholder Hepler Text (e.g. "click here to reply to user1, user2, and user3")
	 * @return the text in the placeholder area
	 * @throws HarnessException
	 */
	public String zGetQuickReplyPlaceholder() throws HarnessException {

		// Make sure the client is not busy
		this.zWaitForBusyOverlay();
		SleepUtil.sleepSmall();
		
		
		String locator = "css=textarea[id='zv__CLV-main__CV_replyInput']";
		
		
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Placeholder not visible!");
		}
		
		// The placeholder tests are ok when running manually, but
		// fail in TMS.  Get the HTML to inspect what the TMS
		// execution is failing on.
		this.zGetHtml("css=div#zv__CLV-main__CV");
		
		String placeholder = this.sGetAttribute(locator + "@placeholder");
		logger.debug("Found placeholder text: "+ placeholder);
		
		return (placeholder);
		
	}


}
