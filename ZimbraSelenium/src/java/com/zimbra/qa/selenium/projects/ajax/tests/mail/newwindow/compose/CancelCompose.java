package com.zimbra.qa.selenium.projects.ajax.tests.mail.newwindow.compose;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowFormMailNew;


public class CancelCompose extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public CancelCompose() {
		logger.info("New "+ CancelCompose.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String , String>() {{
				    put("zimbraPrefComposeFormat", "text");
				    put("zimbraPrefComposeInNewWindow", "TRUE");
				}};
		
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
			
			ZAssert.assertFalse(window.zIsActive(), "Verify the window is closed");
			
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
