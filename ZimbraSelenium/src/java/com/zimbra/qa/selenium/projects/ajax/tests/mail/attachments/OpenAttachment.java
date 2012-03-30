package com.zimbra.qa.selenium.projects.ajax.tests.mail.attachments;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AttachmentItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.SeparateWindowOpenAttachment;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class OpenAttachment extends PrefGroupMailByMessageTest {

	
	public OpenAttachment() throws HarnessException {
		logger.info("New "+ OpenAttachment.class.getCanonicalName());
		
	}
	
	
	@Test(	description = "Open a text attachment",
			groups = { "functional", "matt" })
	public void OpenTextAttachment_01() throws HarnessException {
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime01.txt";
		final String subject = "subject151615738";
		final String attachmentname = "file.txt";
		final String attachmentcontent = "Text Attachment Content";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		AttachmentItem item = null;
		List<AttachmentItem> items = display.zListGetAttachments();
		for (AttachmentItem i : items) {
			if ( i.getAttachmentName().equals(attachmentname)) {
				item = i;
				break;
			}
		}
		ZAssert.assertNotNull(item, "Verify one attachment is in the message");
			
		SeparateWindowOpenAttachment window = null;
		
		try {
			
			// Left click on the attachment
			window = (SeparateWindowOpenAttachment)display.zListAttachmentItem(Action.A_LEFTCLICK, item);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the attachment is open");
			
			//Verify show original window with proper content.
			String content = window.sGetBodyText();
			ZAssert.assertStringContains(content, attachmentcontent, "Verify the content in the attachment");
			
			window.zCloseWindow();
			window = null;

		} finally {
			
			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}
			
		}

	}


}
