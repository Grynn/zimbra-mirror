package projects.html.tests.folders.zmmailbox;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import framework.core.*;
import framework.util.Stafzmmailbox;
import framework.util.ZimbraSeleniumProperties;
import projects.html.tests.CommonTest;
import projects.html.ui.MailApp;


public class GetFolder extends CommonTest {

	public GetFolder() {
	}
	
	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
	}

	@Test(
			description = "Create a mail folder using zmmailbox and verify it in html",
			groups = { "smoke", "test" }
	)
	public void BasicGetFolder() throws Exception {
		
		String myMailbox = ClientSessionFactory.session().currentUserName();
		String folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();
		
		Stafzmmailbox zmmailbox = new Stafzmmailbox();
		
		// Use zmmailbox to create a new folder in USER_ROOT
		zmmailbox.execute("zmmailbox -z -m "+ myMailbox +" cf /"+ folderName);
		
		// For debugging, use zmmailbox to list the folders
		zmmailbox.execute("zmmailbox -z -m "+ myMailbox +" gaf");
		
		
		// TODO: Do we need to click on "get mail" to sync the change?
		obj.zButton.zClick(MailApp.zRefreshBtn);

		
		// Use ZWC to verify the folder exists
		obj.zFolder.zExists(folderName);

	}



}
