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

public class HistoryItem extends AItem {

	private String historyLocator = null;
	private String historyText = null;
	private String historyUser = null;
	private String historyAction = null;
	private String historyItem = null;
	private String historyTime = null;
	private String ParentId = null;
	
	public HistoryItem() {
		
	}
	
	/**
	 * @param historyLocator 
	 */
	public void setLocator(String locator) {
		historyLocator = locator;
	}

	/**
	 * @return the theLocator
	 */
	public String getLocator() {
		return historyLocator;
	}

	public void setParentId(String parentId) {
		ParentId = parentId;
	}

	public String getParentId() {
		return ParentId;
	}

	public void setHistoryText(String historyText) {
		this.historyText = historyText;
	}

	public String getHistoryText() {
		return historyText;
	}

	public void setHistoryTime(String historyTime) {
		this.historyTime = historyTime;
	}

	public String getHistoryTime() {
		return historyTime;
	}

	public void setHistoryAction(String historyAction) {
		this.historyAction = historyAction;
	}

	public String getHistoryAction() {
		return historyAction;
	}
	
	public void setHistoryItem(String historyItem) {
		this.historyItem = historyItem;
	}

	public String getHistoryItem() {
		return historyItem;
	}
	
	public void setHistoryUser(String historyUser) {
		this.historyUser = historyUser;
	}

	public String getHistoryUser() {
		return historyUser;
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(HistoryItem.class.getSimpleName()).append('\n');
		sb.append("GUI Data:\n");
		sb.append("HistoryText: ").append(getHistoryText()).append('\n');
		sb.append("HistoryUser: ").append(getHistoryUser()).append('\n');
		sb.append("HistoryTime: ").append(getHistoryTime()).append('\n');		
		return (sb.toString());
	}

}
