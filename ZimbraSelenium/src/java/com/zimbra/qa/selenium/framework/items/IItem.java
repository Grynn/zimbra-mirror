/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
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
