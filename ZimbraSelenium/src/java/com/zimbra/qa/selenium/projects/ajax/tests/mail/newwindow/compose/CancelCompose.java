package com.zimbra.qa.selenium.projects.ajax.tests.mail.newwindow.compose;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowFormMailNew;


public class CancelCompose extends PrefGroupMailByMessageTest {

	public CancelCompose() {
		logger.info("New "+ CancelCompose.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefComposeInNewWindow", "TRUE");
		
	}
	
	@Test(	description = "Compose a message in a separate window - click Cancel",
			groups = { "smoke" })
	public void CancelCompose_01() throws HarnessException {
		
		
		
		
		SeparateWindowFormMailNew window = null;
		
		try {
			
			// Open the new mail form
			window = (SeparateWindowFormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW_IN_NEW_WINDOW);
			
			window.zSetWindowTitle("Compose");
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			window.zToolbarPressButton(Button.B_CANCEL);
			
			//ZAssert.assertFalse(window.zIsActive(), "Verify the window is closed");
			// get title by calling getTitle() method once it's implemented, 
			// for now just hardcoding window title name
			boolean status = window.zIsClosed("Zimbra: Compose");
			ZAssert.assertTrue(status, "Verify the window is closed");
			
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
