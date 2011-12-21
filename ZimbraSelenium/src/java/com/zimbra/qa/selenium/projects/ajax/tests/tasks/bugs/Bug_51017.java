package com.zimbra.qa.selenium.projects.ajax.tests.tasks.bugs;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class Bug_51017 extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public Bug_51017() {
		logger.info("New "+ Bug_51017.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageTasks;

		super.startingAccountPreferences = new HashMap<String , String>() {{
			put("zimbraPrefTasksReadingPaneLocation", "bottom");
		}};

	}


	@Bugs(	ids = "51017")
	@Test(	description = "Show Original Pop Up should Get Open With Proper Content",
			groups = { "functional" }
	)
	public void Bug__51017() throws HarnessException {

		FolderItem taskFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Tasks);

		// Create a basic task 
		String subject = "task"+ ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
				"<CreateTaskRequest xmlns='urn:zimbraMail'>" +
					"<m >" +
						"<inv>" +
							"<comp name='"+ subject +"'>" +
								"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"</comp>" +
						"</inv>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</CreateTaskRequest>");



		// Refresh the tasks view
		app.zPageTasks.zToolbarPressButton(Button.B_REFRESH);
		app.zTreeTasks.zTreeItem(Action.A_LEFTCLICK, taskFolder);

		// Select the item
		app.zPageTasks.zListItem(Action.A_MAIL_CHECKBOX, subject);


		SeparateWindowShowOriginal window = null;
		
		try {
			
			// Right click the item, select Show Original
			window = (SeparateWindowShowOriginal)app.zPageTasks.zListItem(Action.A_RIGHTCLICK, Button.O_SHOW_ORIGINAL, subject);
			window.zWaitForActive();		// Make sure the window is there
			
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");
			
			//Verify show original window with proper content.
			String ShowOrigBody = window.sGetBodyText();
			ZAssert.assertStringContains(ShowOrigBody, subject, "Verify subject in showorig window");
			
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
