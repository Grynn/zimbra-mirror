package com.zimbra.qa.selenium.projects.html.clients;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;




/**
 * @author raodv
 * Essentially same as Folder in html-client but with some extra apis like zCheck zunCheck etc.
 */
public class CalendarFolder extends Folder {

	public CalendarFolder() {
		super();
	} 
	/**
	 * Check Calendar folder
	 * @param folder
	 */
	public  void zCheck(String folder) throws HarnessException   {
		ClientSessionFactory.session().selenium().call("folderCore_html",  folder+"_check", "click", true, "", "");
	}	
	/**
	 * unchecks Calendar folder
	 * @param folder
	 */
	public  void zUnCheck(String folder) throws HarnessException   {
		ClientSessionFactory.session().selenium().call("folderCore_html",  folder+"_uncheck", "click", true, "", "");
	}		

	/**
	 * Checks if Calendar folder's checkbox is unchecked
	 * @param folder
	 */
	public  String zIsUnChecked(String folder)  throws HarnessException  {
		return ClientSessionFactory.session().selenium().call("folderCore_html",  folder+"_uncheck", "exists", true, "", "");
	}

	/**
	 * Checks if Calendar folder's checkbox is checked
	 * @param folder
	 */
	public  String zIsChecked(String folder)  throws HarnessException  {
		return ClientSessionFactory.session().selenium().call("folderCore_html",  folder+"_check", "exists", true, "", "");
	}	
}