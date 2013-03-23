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


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;

/**
 * Used to define a Zimbra All Item Types Search Results
 * 
 * @author Matt Rhoades
 *
 */
public class AllItemTypesItem implements IItem {
	protected static Logger logger = LogManager.getLogger(IItem.class);
	
	public String tag = null;	
	public String imageType = null;
	public String from = null;
	public String attachment = null; //boolean??
	public String subject = null;
	public String date = null;
	
			
	public AllItemTypesItem() {
		super();
	}

	
	public AllItemTypesItem(String tag, String imageType, String from,String attachment, String subject, String date) {
		this.tag       =tag;
		this.imageType =imageType;
		this.from      =from;
		this.attachment=attachment;
		this.subject   =subject;
		this.date      =date;
		
	}

	@Override
    public String getName() {
    	return "AllItemTypes: " + subject;
    }
	
	@Override
	public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
		// TODO Auto-generated method stub

	}

	private String myId;
	public String getId() {
		return (myId);
	}
	public void setId(String id) {
		myId=id;
	}
	

	@Override
	public String prettyPrint() {
		StringBuilder sb = new StringBuilder();
		sb.append(AllItemTypesItem.class.getSimpleName()).append('\n');
		
		sb.append(String.format("Tag:(%s)\n Image Type:(%s)\n From:(%s)\n Attachment:(%s)\n Subject:(%s)\n Date:(%s)\n",
				                 tag, imageType, from, attachment, subject,date));
		
		return (sb.toString());
	}



}


