/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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

import java.util.List;

public abstract class AFolderItem extends AItem {

	private String _ParentId = null;
	private String _Name = null;
	private String _Color = null;
	private List<AFolderItem> _Subfolders = null;
	
	public void setParentId(String id) {
		_ParentId = id;
	}

	public String getParentId() {
		return (_ParentId);
	}
	

	public void setName(String name) {
		_Name = name;
	}
	
	public String getName() {
		return (_Name);
	}
	
	public void setColor(String color) {
		_Color = color;
	}
	
	public String getColor() {
		return (_Color);
	}

	public List<AFolderItem> getSubfolders() {
		return (_Subfolders);
	}
	
	public void setSubfolders(List<AFolderItem> subfolders) {
		_Subfolders = subfolders;
	}

	public String getView() {
		// TODO Auto-generated method stub
		return null;
	}


}
