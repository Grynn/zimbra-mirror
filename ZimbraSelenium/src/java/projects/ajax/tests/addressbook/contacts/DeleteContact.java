package projects.ajax.tests.addressbook.contacts;
import org.testng.annotations.Test;


import framework.items.*;
import framework.ui.Buttons;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Addressbook.*;

public class DeleteContact extends AjaxCommonTest  {
	public DeleteContact() {
		logger.info("New "+ DeleteContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
			
		super.startingAccount = account;		
		
	}
	
	@Test(	description = "Delete a contact item",
			groups = { "sanity"})
	public void DeleteContact_01() throws HarnessException {
		
		// Create a contact 
		ContactItem contactItem=CreateContact.createBasicContact(app);
		
		//delete contact
		app.zPageAddressbook.zClick(PageAddressbook.Toolbar.DELETE);
		SleepUtil.sleepSmall();
		
		//verify deleted contact not displayed
		ZAssert.assertTrue(PageAddressbook.LeftPanel.isEmpty(),"cannot delete contact " + contactItem.firstName + contactItem.lastName );
		ZAssert.assertTrue(PageAddressbook.RightPanel.isEmpty(),"cannot delete contact" + contactItem.firstName + contactItem.lastName );
		
	}

}
