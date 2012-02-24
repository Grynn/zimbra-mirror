/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;


import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DialogCreateFolder;

/*
 * Right-click menu
 * 
 */

public class ContextMenu extends AbsDisplay {

	
	public ContextMenu(AbsApplication application) {
		super(application);
	}
	
	

	public void  zSelect(ContextMenuItem cmi) throws HarnessException {
		logger.info(myPageName() + " zSelect("+ cmi.text +")");
				
		this.zClick(cmi.locator);
        zWaitForBusyOverlay();
	}

	public AbsPage zSelect(ContextMenuItem.CONTEXT_MENU_ITEM_NAME cmiName) throws HarnessException {
      ContextMenuItem cmi = ContextMenuItem.getDesktopContextMenuItem(cmiName);
      logger.info(myPageName() + " zSelect("+ cmi.text +")");
      this.zClick(cmi.locator);
      AbsPage page = null;
      switch (cmiName) {
      case NEW_FOLDER:
         page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageMail);
      }

      // If page was specified, make sure it is active
      if ( page != null ) {
         // This function (default) throws an exception if never active
         page.zWaitForActive();
         
      }

      return page;
   }

	public ContextMenuItem getContextMenuItem  (String locator, Class contextMenuItemObject)throws HarnessException {
		   ContextMenuItem cmi=null;
		   
		   Field[] fields= contextMenuItemObject.getFields();
	       
	       for (Field f:fields) {
	    	   try {   			    		 
	    		 cmi = (ContextMenuItem) f.get(null); 
	    		 
	    	    	
		         if (cmi.locator.equals(locator)) {
			     
		        	 String cssLocator= "css=td[id='" + locator ;
			 
			        //verify image, text, and shortcut 		   
		        	 if (! this.sIsElementPresent(cssLocator + "_left_icon" + "'] " +cmi.image))
		        	 {
		        		 throw new HarnessException("cannot find " +  cssLocator + "_left_icon" + "'] " +cmi.image);
		        	 }
				
		        	/* TODO locale
		        	 * if (! this.sIsElementPresent(cssLocator + "_title" + "']:contains('" +cmi.text + "')")) 
		        	 {
		        		 throw new HarnessException("cannot find " +  cssLocator + "_title" + "']:contains('" +cmi.text + "')");
		        	 }
				    */   
		        	 if (! this.sIsElementPresent(cssLocator + "_dropdown" +"']" +cmi.shortcut))
		        	 {
		        		 throw new HarnessException("cannot find " + cssLocator + "_dropdown" +"']" +cmi.shortcut);
		        	 }
		              	 
		        	 break;
		          }
	    	   }   	
	    	   //exception occurs for non-ContextMenuItem fields
	    	   catch (Exception e) {}    		        	
	    
	       }	   
		   
	       if (locator.startsWith("DWT")){
				  //most likely separator 
			   cmi = ContextMenuItem.C_SEPARATOR;			  
		   }
		  
			
		   if (cmi == null) {
			   throw new HarnessException("cannot find context menu " + locator);
		   }
		   
		   return cmi;
		}
		
	public ArrayList<ContextMenuItem> zListGetContextMenuItems(Class contextMenuItemObjects) throws HarnessException {

		ArrayList <ContextMenuItem> list= new ArrayList<ContextMenuItem>();
		String typeLocator = null;
		
		//get LOCATOR 
		try {			
		  typeLocator = (String) contextMenuItemObjects.getField("LOCATOR").get(null);
		} catch (Exception e) {
			throw new HarnessException("Context Menu LOCATOR not defined", e);
		}
		
		//TODO: check for visible		
		if ( !this.sIsElementPresent("xpath=//div[@" +typeLocator + "]"))
			throw new HarnessException("Context Menu List is not present(visible) "+ "//div[@id='zm__Contacts']");

		//Get the number of context menu item including separator  
		int count = this.sGetXpathCount("//div[@" + typeLocator + "]/table/tbody/tr");
		
		logger.debug(myPageName() + " zListGetContextMenuItems: number of context menu item including separators: "+ count);
		System.out.println(myPageName() + " zListGetContextMenuItems: number of context menu item including separators: "+ count);

		// Get each context item data's data from the table list
		for (int i = 1; i <= count; i++) {
			//get id attribute
			String id = sGetAttribute("xpath=(//div[@" + typeLocator + "]/table/tbody/tr["+ i +"]/td/div)@id");
						
			ContextMenuItem ci  = getContextMenuItem(id, contextMenuItemObjects);		    						
			list.add(ci);	    	      
		}


		return list;		
	}
	
	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zDisplayPressButton("+ button +")");
		
		tracer.trace("Click "+ button);

		throw new HarnessException("no logic defined for button: "+ button);
		
	}
	

	@Override
	public boolean zIsActive() throws HarnessException {
		//TODO return true for now		
		return true; 
		//return ( this.sIsElementPresent(Locators.zTagDialogId) );
	}

	//check if a context menu item is enable
	public boolean isEnable(ContextMenuItem cmi) throws HarnessException  {
		String attrs = sGetAttribute("xpath=(//div[@id='" +cmi.locator + "'])@class");       
		return !attrs.contains("ZDisabled");
	}

}
