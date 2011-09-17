package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints.findshares;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShareFind;


public class Cancel extends PrefGroupMailByMessageTest {

	
	public Cancel() {
		logger.info("New "+ Cancel.class.getCanonicalName());
		
		
		
		
		
	}
	
	@Test(	description = "Open the find shares dialog.  Cancel the dialog.",
			groups = { "functional" })
	public void CancelFindShares_01() throws HarnessException {
		

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Click the inbox
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
				
		// Click Find Shares
		DialogShareFind dialog = (DialogShareFind)app.zTreeMail.zPressButton(Button.B_TREE_FIND_SHARES);
		
		// Close the dialog box
		dialog.zClickButton(Button.B_CANCEL);

	}

	
	

	

}
