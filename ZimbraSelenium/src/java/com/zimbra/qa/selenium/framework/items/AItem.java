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

public abstract class AItem {

	private String id = "0";
	
	protected AItem() {
	}

	/**
	 * Get the Zimbra ID of this item
	 * @return
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Set the Zimbra ID of this item
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Get the name of this item, such as subject, fileas, folder name, etc.
	 * @return
	 */
	public String getName() {
		return (getId());
	}
	
	/**
	 * Create a string version of this object suitable for using with a logger
	 */
	public abstract String prettyPrint();
}
