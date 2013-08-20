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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AttachmentItem {
	protected static Logger logger = LogManager.getLogger(AttachmentItem.class);

	public enum AttachmentIcon {
		ImgGenericDoc,
		ImgImageDoc,
		ImgMSWordDoc,
		ImgMSExcelDoc,
	}
	
	private String locator = null;
	private String name = null;
	private AttachmentIcon icon = null;
	
	
	public AttachmentItem() {
		logger.info("new "+ AttachmentItem.class.getCanonicalName());
	}


	public void setLocator(String locator) {
		this.locator = locator;
	}


	public String getLocator() {
		return this.locator;
	}


	public void setAttachmentName(String attachmentName) {
		name = attachmentName;
	}


	public String getAttachmentName() {
		return name;
	}

	public void setAttachmentIcon(AttachmentIcon attachmentIcon) {
		icon = attachmentIcon;
	}


	public AttachmentIcon getAttachmentIcon() {
		return icon;
	}


	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(AttachmentItem.class.getSimpleName()).append('\n');
		sb.append("Name: ").append(name).append('\n');
		sb.append("Icon: ").append(icon).append('\n');
		sb.append("Locator: ").append(locator).append('\n');
		return (sb.toString());
	}
	
}
