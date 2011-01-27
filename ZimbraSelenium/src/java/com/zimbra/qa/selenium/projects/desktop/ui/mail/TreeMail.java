package com.zimbra.qa.selenium.projects.desktop.ui.mail;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.items.SavedSearchFolderItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTree;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.projects.desktop.ui.ContextMenu;

public class TreeMail extends AbsTree {

   public final static String stringToReplace = "<TREE_ITEM_NAME>";
   public static class Locators {
      public final static String zTreeItems = new StringBuffer("//td[text()='").
            append(stringToReplace).append("']").toString();
   }

   public TreeMail(AbsApplication application) {
      super(application);
      logger.info("new " + TreeMail.class.getCanonicalName());
   }

   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public AbsPage zPressButton(Button button) throws HarnessException {
      if ( button == null )
         throw new HarnessException("Button cannot be null");

      AbsPage page = null;
      String locator = null;

      if (button == Button.B_TREE_NEWFOLDER) {
         locator = "id=overviewHeader-Text FakeAnchor";
         page = new DialogCreateFolder(MyApplication);

         if ( !this.sIsElementPresent(locator) ) {
            throw new HarnessException("Unable to locator folder in tree "+ locator);
         }

         this.zClick(locator);
         
         // Wait for the page to load
         SleepUtil.sleepSmall();

         // No result page is returned in this case ... use app.zPageMail
         page = null;

         // FALL THROUGH

      } else {
         throw new HarnessException("no logic defined for button "+ button);
      }

      if ( locator == null ) {
         throw new HarnessException("locator was null for button "+ button);
      }

      // Default behavior, process the locator by clicking on it
      //

      // Make sure the button exists
      if ( !this.sIsElementPresent(locator) )
         throw new HarnessException("Button is not present locator="+ locator +" button="+ button);

      // Click it
      this.zClick(locator);

      // If page was specified, make sure it is active
      if ( page != null ) {

         // This function (default) throws an exception if never active
         page.zWaitForActive();
      }
      return (page);
   }

   public AbsPage zTreeItem(Action action, String locator) throws HarnessException {
      AbsPage page = null;

      if ( locator == null )
         throw new HarnessException("locator is null for action "+ action);

      if ( !this.sIsElementPresent(locator) )
         throw new HarnessException("Unable to locator folder in tree "+ locator);

      if ( action == Action.A_LEFTCLICK ) {

         // FALL THROUGH
      } else if ( action == Action.A_RIGHTCLICK ) {

         // Select the folder
         this.zRightClick(locator);

         // return a context menu
         return (new ContextMenu(MyApplication));

      } else {
         throw new HarnessException("Action "+ action +" not yet implemented");
      }

      // Default behavior.  Click the locator
      zClick(locator);

      return (page);
   }

   protected AbsPage zTreeItem(Action action, FolderItem folderItem) throws HarnessException {
      AbsPage page = null;
      String locator = null;
      int delayMillis = 0;
      
      if ( action == Action.A_LEFTCLICK ) {
         
         locator = "id=zti__main_Mail__"+ folderItem.getId() +"_textCell";
         
         // FALL THROUGH

      } else if ( action == Action.A_RIGHTCLICK ) {
         
         // Currently, the harness must left-click + context shortcut key
         // to activate the shortcut
         
         // Select the folder
         this.zTreeItem(Action.A_LEFTCLICK, folderItem);
         
         this.zRightClick(locator);                                           

         // return a context menu
         return (new ContextMenu(MyApplication));

      } else {
         throw new HarnessException("Action "+ action +" not yet implemented");
      }

      
      if ( locator == null )
         throw new HarnessException("locator is null for action "+ action);
      
      if ( !this.sIsElementPresent(locator) )
         throw new HarnessException("Unable to locator folder in tree "+ locator);
      
      
      // Default behavior.  Click the locator
      zClick(locator);

      if ( page != null ) {
         
         // Wait for the page to become active, if it was specified
         page.zWaitForActive();
      }

      this.zClick(locator);
      
      if ( delayMillis > 0 ) {
         
         // Sleep for a bit, if it was specified
         SleepUtil.sleep(delayMillis);
      }

      return (page);

   }

   @Override
   public AbsPage zTreeItem(Action action, IItem folder) throws HarnessException {
   // Validate the arguments
      if ( (action == null) || (folder == null) ) {
         throw new HarnessException("Must define an action and addressbook");
      }
      
      if ( folder instanceof FolderItem ) {
         return (zTreeItem(action, (FolderItem)folder));
      } else if ( folder instanceof SavedSearchFolderItem ) {
         return (zTreeItem(action, (SavedSearchFolderItem)folder));
      }
      
      throw new HarnessException("Must use FolderItem or SavedSearchFolderItem as argument, but was "+ folder.getClass());
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      // TODO Auto-generated method stub
      return false;
   }

}
