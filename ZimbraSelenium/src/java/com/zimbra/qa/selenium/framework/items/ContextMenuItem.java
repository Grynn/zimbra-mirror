package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.util.*;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.util.*;
import java.lang.reflect.*;
public class ContextMenuItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	 
	//contact's context menu	
	public static final ContextMenuItem C_CONTACT_SEARCH = new ContextMenuItem("zmi__Contacts__SEARCH","Find Emails From Contact","div[class='ImgSearch']","");	
	public static final ContextMenuItem C_CONTACT_ADVANCED_SEARCH = new ContextMenuItem("zmi__Contacts__BROWSE","Advanced Search","div[class='ImgSearchBuilder']","");	
	public static final ContextMenuItem C_CONTACT_NEW_EMAIL = new ContextMenuItem("zmi__Contacts__NEW_MESSAGE","New Email","div[class='ImgNewMessage']","[nm]");	
	public static final ContextMenuItem C_CONTACT_EDIT = new ContextMenuItem("zmi__Contacts__CONTACT","Edit Contact","div[class='ImgEdit']","");	
	public static final ContextMenuItem C_CONTACT_FORWARD = new ContextMenuItem("zmi__Contacts__SEND_CONTACTS_IN_EMAIL","Forward Contact","div[class='ImgMsgStatusSent']","");	
	public static final ContextMenuItem C_CONTACT_TAG = new ContextMenuItem("zmi__Contacts__TAG_MENU","Tag Contact","div[class='ImgTag']","css=div[class='ImgCascade']");	
	public static final ContextMenuItem C_CONTACT_DELETE = new ContextMenuItem("zmi__Contacts__DELETE","Delete","div[class='ImgDelete']","");
	public static final ContextMenuItem C_CONTACT_MOVE = new ContextMenuItem("zmi__Contacts__MOVE","Move","div[class='ImgMoveToFolder']","");
	public static final ContextMenuItem C_CONTACT_PRINT = new ContextMenuItem("zmi__Contacts__PRINT_CONTACT","Print","div[class='ImgPrint']","");
   
	
	
		
	//FIXME
	public static final ContextMenuItem C_SEPARATOR = new ContextMenuItem("css=div[id='DWT']","","","");
    
    
    private static HashMap<String,ContextMenuItem> hm = new HashMap();

    static
    {
    	//use reflection to put pre-defined ContextMenuItem objects into the Hashmap 
    	Field[] fields= ContextMenuItem.class.getFields();
       
    	 for (Field f:fields) {
    		try {   			    		 
    		 ContextMenuItem cmi = (ContextMenuItem) f.get(null); 
    		 hm.put(cmi.locator, cmi);
    		}  	
    		//exception occurs for non-ContextMenuItem fields
    		catch (Exception e) {}    		        		
    	}    	
    }
    
	public final String locator;
	public final String image;
	public final String text;
	public final String shortcut;
	public boolean enable;
	
	private ContextMenuItem (String locator,String text,String image,String shortcut) {
		this.locator=locator;
		this.image=image;
		this.text=text;
		this.shortcut=shortcut;	
	}
	
	
	public static ContextMenuItem getContextMenuItem  (String locator)throws HarnessException {
	   ContextMenuItem cmi=null;
	   //?????
	   //if (hm.containsKey(locator))
		   
	   for (String key : hm.keySet()) {
		  if (key.equals(locator)) {
		     cmi= hm.get(key);
		    
		     //TODO verify text, image, shortcut 
			 /*   
		     String cssLocator= "css=div[id='" + locator + "']";
		     String xpathLocator = "xpath=//div[@id='" + locator + "']"; 
		     
		 	 if (! (ClientSessionFactory.session().selenium().isElementPresent(cssLocator + " " +cmi.image) ||
		    		ClientSessionFactory.session().selenium().isElementPresent(cssLocator + " " +cmi.text) ||
		    		ClientSessionFactory.session().selenium().isElementPresent(cssLocator + " " +cmi.shortcut) 
		         )) {
		    	 
		     }
		    	 
		     //_left_icon ._title _dropdown
		     //String contextItemMain      = typeLocator + "/table/tbody/tr["+ i +"]";
		     //String contextItemText      = contextItemMain + "/td/div/ table/tbody/tr/td[3]";
			 //String contextItemLocator   = contextItemMain +"/td/div";
			 //get attribute
			 //String contextItemImage     = contextItemMain + "/td/div/table/tbody/tr/td[2]";
			 //String contextItemShortcut  = contextItemMain + "/td/div/table/tbody/tr/td[4]";
              */
		     break;
		  }
		  else if (locator.startsWith("DWT")){
			  //TODO probably separator 
			  cmi = C_SEPARATOR;
			  break;
		  }
	   }
		
	   if (cmi == null) {
		   throw new HarnessException("cannot find context menu " + locator);
	   }
	   
	   return cmi;
	}
	
	public boolean isEnable() {
		//TODO get visible attribute from Selenium 
		return enable;
	}
		
}
