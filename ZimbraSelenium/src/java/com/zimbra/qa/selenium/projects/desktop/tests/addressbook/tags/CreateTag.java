package com.zimbra.qa.selenium.projects.desktop.tests.addressbook.tags;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogTag;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;

public class CreateTag extends AjaxCommonTest {
   public CreateTag() {
      logger.info("New "+ CreateTag.class.getCanonicalName());

      // All tests start at the login page
      super.startingPage = app.zPageAddressbook;
      super.startingAccountPreferences = null;

   }

   private void _verifyTagCreated(DialogTag dialog) throws HarnessException {
      String name = "tag" + ZimbraSeleniumProperties.getUniqueString();
      
      // Fill out the form with the basic details
      dialog.zSubmit(name);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageAddressbook.zWaitForDesktopLoadingSpinner(5000);

      // Make sure the tag was created on the server
      TagItem tag = app.zPageAddressbook.zGetTagItem(app.zGetActiveAccount(), name);
      ZAssert.assertNotNull(tag, "Verify the new tag was created");
      
      ZAssert.assertEquals(tag.getName(), name, "Verify the server and client tag names match");
            
   }

   @Test(   description = "Create a new tag from New drop down option on Address Book page",
         groups = { "sanity" })
   public void ClickTagsOnFolderTree() throws HarnessException {

      DialogTag dialog = (DialogTag)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_TAG);

      _verifyTagCreated(dialog);
   }

}
