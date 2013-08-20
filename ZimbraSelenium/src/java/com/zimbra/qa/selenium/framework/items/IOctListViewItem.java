/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.items;

import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * This Interface is used by the Octopus application for any
 * item that can be represented in the Octopus file list
 * view, such as folders, files, shares
 * 
 * @author Matt Rhoades
 *
 */
public interface IOctListViewItem {

	/**
	 * Get the List View icon
	 * @return
	 * @throws HarnessException
	 */
	public String getListViewIcon() throws HarnessException;

	
	/**
	 * Set the List View icon
	 * @throws HarnessException
	 */
	public void setListViewIcon(String icon) throws HarnessException;
	
	/**
	 * Get the List View name
	 * @return
	 * @throws HarnessException
	 */
	public String getListViewName() throws HarnessException;

	
	/**
	 * Set the List View icon
	 * @throws HarnessException
	 */
	public void setListViewName(String name) throws HarnessException;
	
}
