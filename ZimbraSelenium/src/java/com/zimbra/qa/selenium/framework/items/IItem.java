package com.zimbra.qa.selenium.framework.items;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;

public interface IItem {

	
	/**
	 * Get the name of this item, such as subject, fileas, folder name, etc.
	 */
	public String getName();
	
	/**
	 * Create an object on the Zimbra server based on the object values
	 * @param account - the account used to create the object
	 * @throws HarnessException
	 */
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException;
	
	/**
	 * Create a string version of this object suitable for using with a logger
	 */
	public String prettyPrint();

}
