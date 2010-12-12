package projects.ajax.ui;

import java.util.List;

import framework.items.MailItem;
import framework.items.RecipientItem;
import framework.items.IItem;
import framework.items.RecipientItem.RecipientType;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.Stafpostqueue;


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
		
		public static final String zSendIconBtn = "css=[id^=zb__COMPOSE][id$=__SEND_left_icon]";

		public static final String zToField = "css=[id^=zv__COMPOSE][id$=_to_control]";
		public static final String zSubjectField = "css=[id^=zv__COMPOSE][id$=_subject_control]";
		public static final String zBodyField = "TODO";
		
	}

	
	/**
	 * Protected constuctor for this object.  Only classes within
	 * this package should create DisplayMail objects.
	 * 
	 * @param application
	 */
	protected FormMailNew(AbsApplication application) {
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
		
		// Look for "Send"
		boolean visible = this.sIsElementPresent(Locators.zSendIconBtn);
		if ( !visible )
			throw new HarnessException("Send button is not visible "+ Locators.zSendIconBtn);
		
		// Click on it
		this.sMouseDown(Locators.zSendIconBtn);
		this.sMouseUp(Locators.zSendIconBtn);
		
		// Need to wait for the client request to be sent
		SleepUtil.sleepSmall();
		
		// Wait for the message to be delivered
		try {
		
			// Check the message queue
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
		
		} catch (Exception e) {
			throw new HarnessException("Unable to wait for message queue", e);
		}

	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("FormMailNew.fill(ZimbraItem)");
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
			if ( !this.sIsElementPresent(Locators.zSubjectField) )
				throw new HarnessException("Unable to find locator "+ Locators.zSubjectField);
			this.sType(Locators.zSubjectField, mail.dSubject);
			SleepUtil.sleepMedium();
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
		
		if ( to != null ) {
			// Add the recipient string to the To field
			if ( !this.sIsElementPresent(Locators.zToField) )
				throw new HarnessException("Unable to find locator "+ Locators.zSubjectField);
			this.sType(Locators.zToField, to.toString());
		}
		
		// TODO: handle cc, bcc, and from

		// TODO: handle bodyText
		
	}

}
