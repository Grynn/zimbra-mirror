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

public class UploadProgressItem extends AItem{

	public String uploadProgressBarLocator=null;
	public String uploadFileName=null;
	public String uploadFileSize=null;
	public String uploadProgresSize=null;
	public String uploadStop=null;
	public String uploadBarValue=null;


	public UploadProgressItem()
	{

	}

	public void setLocator(String locator)
	{
		uploadProgressBarLocator=locator;
	}
	public String getLocator()
	{
		return uploadProgressBarLocator;
	}
	public void setFileName(String filename)
	{
		this.uploadFileName=filename;
	}
	public String getFileName()
	{
		return this.uploadFileName;
	}
	public void setUploadFileSize(String fileSize)
	{
		int index = fileSize.indexOf("of");
		uploadFileSize=fileSize.substring(index+2, fileSize.length()).trim();
		uploadProgresSize=fileSize.substring(0, index).trim();
	}
	public String getUploadFileSize()
	{
		return this.uploadFileSize;
	}
	public void setUploadBarValue(String value)
	{
		uploadBarValue=value;
	}
	public String getUploadBarValue()
	{
		return this.uploadBarValue;
	}
	public String getUploadProgressSize()
	{
		return this.uploadProgresSize;
	}

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(UploadProgressItem.class.getSimpleName()).append('\n');
		sb.append("GUI Data:\n");
		sb.append("UploadFileName: ").append(getFileName()).append('\n');
		sb.append("UploadFileSize: ").append(getUploadFileSize()).append('\n');
		sb.append("UploadProgressBarValue: ").append(getUploadFileSize()).append('\n');
		return (sb.toString());
	}
}
