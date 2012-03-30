package com.zimbra.qa.selenium.projects.ajax.tests.mail.attachments;

import java.io.File;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.AttachmentItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class AttachmentIcons extends PrefGroupMailByMessageTest {

	
	public AttachmentIcons() throws HarnessException {
		logger.info("New "+ AttachmentIcons.class.getCanonicalName());
		
	}
	
	
	@Test(	description = "Verify icon: ImgGenericDoc",
			groups = { "functional" })
	public void ImgGenericDoc_01() throws HarnessException {
		
		// This mime contains an attachment that should map to ImgGenericDoc
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime01.txt";
		final String subject = "subject151615738";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		
		// Verify the icon appears
		boolean found = false;
		for ( AttachmentItem i : display.zListGetAttachments() ) {
			if ( i.getAttachmentIcon().equals(AttachmentItem.AttachmentIcon.ImgGenericDoc) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the attachment icon appears");
		
		
	}


	@Test(	description = "Verify icon: ImgImageDoc",
			groups = { "functional" })
	public void ImgImageDoc_01() throws HarnessException {
		
		// This mime contains an attachment that should map to ImgGenericDoc
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime03.txt";
		final String subject = "subject13330659993903";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));


		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		
		// Verify the icon appears
		boolean found = false;
		for ( AttachmentItem i : display.zListGetAttachments() ) {
			if ( i.getAttachmentIcon().equals(AttachmentItem.AttachmentIcon.ImgImageDoc) ) {
				found = true;
				break;
			}
		}
		ZAssert.assertTrue(found, "Verify the attachment icon appears");
		
		
	}



}
