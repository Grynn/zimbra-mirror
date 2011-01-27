package com.zimbra.qa.selenium.projects.desktop.ui;

import java.util.List;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DialogCreateFolder;

/*
 * Right-click menu
 * 
 */

public class ContextMenu extends AbsDisplay {
   
   public static class Locators {
   }
   
   
   public ContextMenu(AbsApplication application) {
      super(application);
   }

   public AbsPage zSelect(ContextMenuItem.CONTEXT_MENU_ITEM_NAME cmiName) throws HarnessException {
      ContextMenuItem cmi = ContextMenuItem.getDesktopContextMenuItem(cmiName);
      logger.info(myPageName() + " zSelect("+ cmi.text +")");
      this.zClick(cmi.locator);
      switch (cmiName) {
      case NEW_FOLDER:
         return new DialogCreateFolder(MyApplication);
      }
      return null;
   }

   
   public List<ContextMenuItem> zListGetContextMenuItems(String typeLocator) throws HarnessException {

      return null;     
   }
   
   /* (non-Javadoc)
    * @see framework.ui.AbsDialog#myPageName()
    */
   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      //TODO return true for now    
      return true; 
      //return ( this.sIsElementPresent(Locators.zTagDialogId) );
   }



}