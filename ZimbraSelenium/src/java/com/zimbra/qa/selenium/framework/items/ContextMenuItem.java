package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.ui.I18N;

public class ContextMenuItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);

	public static String stringToReplace = "<ITEM_NAME>";
	public static final String zDesktopContextMenuItems = new StringBuffer("css=td[id$='_title']:contains(")
	      .append(stringToReplace).append(")").toString();

	//FIXME
	public static final ContextMenuItem C_SEPARATOR = new ContextMenuItem("css=div[id='DWT']","","","");
    
	public final String locator;
	public final String image;
	public final String text;
	public final String shortcut;
	
	public ContextMenuItem (String locator, String text, String image, String shortcut) {
		this.locator=locator;
		this.image=image;
		this.text=text;
		this.shortcut=shortcut;	
	}

	public enum CONTEXT_MENU_ITEM_NAME {
	   NEW_FOLDER
	}

	public static ContextMenuItem getDesktopContextMenuItem(CONTEXT_MENU_ITEM_NAME cmiName) {
	   String locator = null;
	   String text = null;
	   String image = null;
	   String shortcut = null;
	   switch (cmiName) {
	   case NEW_FOLDER:
	      locator = zDesktopContextMenuItems.replace(stringToReplace, I18N.CONTEXT_MENU_ITEM_NEW_FOLDER);
	      text = I18N.CONTEXT_MENU_ITEM_NEW_FOLDER;
	      break;
	   }

	   return new ContextMenuItem(locator, text, image, shortcut);
	}
}
