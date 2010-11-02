package projects.ajax.ui;

import framework.items.MailItem;
import framework.items.RecipientItem;
import framework.items.ZimbraItem;
import framework.items.RecipientItem.RecipientType;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.Stafpostqueue;


public class FormMailNew extends AbsForm {
	
	public static final String zSendIconBtn = "css=[id^=zb__COMPOSE][id$=__SEND_left_icon]";

	public static final String zToField = "css=[id^=zv__COMPOSE][id$=_to_control]";
	public static final String zSubjectField = "css=[id^=zv__COMPOSE][id$=_subject_control]";
	public static final String zBodyField = "TODO";

	
	public FormMailNew(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormMailNew.class.getCanonicalName());

	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void submit() throws HarnessException {
		logger.info("FormMailNew.submit()");
		
		// Look for "Send"
		boolean visible = this.sIsElementPresent(zSendIconBtn);
		if ( !visible )
			throw new HarnessException("Send button is not visible "+ zSendIconBtn);
		
		// Click on it
		this.sMouseDown(zSendIconBtn);
		this.sMouseUp(zSendIconBtn);
		
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
	public void fill(ZimbraItem item) throws HarnessException {
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
		if ( mail.subject != null ) {
			this.sType(zSubjectField, mail.subject);
			SleepUtil.sleepMedium();
		}
		
		// Handle the Recipient list, which can be a combination
		// of To, Cc, Bcc, and From
		StringBuilder to = null;
		StringBuilder cc = null;
		StringBuilder bcc = null;
		StringBuilder from = null;
		
		// Convert the list of recipients to a semicolon separated string
		if ( mail.recipients != null ) {
			if ( !mail.recipients.isEmpty() ) {
				
				for (RecipientItem r : mail.recipients) {
					if ( r.type == RecipientType.To ) {
						if ( to == null ) {
							to = new StringBuilder();
							to.append(r.emailAddress);
						} else {
							to.append(";").append(r.emailAddress);
						}
					}
					if ( r.type == RecipientType.Cc ) {
						if ( cc == null ) {
							cc = new StringBuilder();
							cc.append(r.emailAddress);
						} else {
							cc.append(";").append(r.emailAddress);
						}
					}
					if ( r.type == RecipientType.Bcc ) {
						if ( bcc == null ) {
							bcc = new StringBuilder();
							bcc.append(r.emailAddress);
						} else {
							bcc.append(";").append(r.emailAddress);
						}
					}
					if ( r.type == RecipientType.From ) {
						if ( from == null ) {
							from = new StringBuilder();
							from.append(r.emailAddress);
						} else {
							from.append(";").append(r.emailAddress);
						}
					}
				}
				
			}
		}
		
		if ( to != null ) {
			// Add the recipient string to the To field
			this.sType(zToField, to.toString());
		}
		
		// TODO: handle cc, bcc, and from

		// TODO: handle bodyText
		
	}

}
