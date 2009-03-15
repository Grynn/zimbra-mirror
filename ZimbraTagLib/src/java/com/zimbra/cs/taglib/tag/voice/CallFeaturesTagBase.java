/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;

import java.util.List;
import java.util.ArrayList;

abstract public class CallFeaturesTagBase extends ZimbraSimpleTag {
	protected String mVar;
	protected String mPhone;
	protected boolean mEmailNotificationActive;
	protected String mEmailNotificationAddress;
	protected boolean mCallForwardingActive;
	protected String mCallForwardingForwardTo;
	protected boolean mSelectiveCallForwardingActive;
	protected String mSelectiveCallForwardingForwardTo;
	protected List<String> mSelectiveCallForwardingForwardFrom;
	protected long mNumberPerPage;

	public void setVar(String var) { mVar = var; }
	public void setPhone(String phone) { mPhone = phone; }
	public void setEmailnotificationactive(String active) { mEmailNotificationActive = booleanValue(active); }
	public void setEmailnotificationaddress(String address) { mEmailNotificationAddress = address.trim(); }
	public void setCallforwardingactive(String active) { mCallForwardingActive = booleanValue(active); }
	public void setCallforwardingforwardto(String number) { mCallForwardingForwardTo = number.trim(); }
	public void setSelectivecallforwardingactive(String active) { mSelectiveCallForwardingActive = booleanValue(active); }
	public void setSelectivecallforwardingforwardto(String number) { mSelectiveCallForwardingForwardTo = number.trim(); }
	public void setSelectivecallforwardingforwardfrom(String[] numbers) {
		mSelectiveCallForwardingForwardFrom = new ArrayList<String>(numbers.length);
		for (String number : numbers) {
			mSelectiveCallForwardingForwardFrom.add( number.trim());
		}
	}
	public void setNumberPerPage(String number) { mNumberPerPage = Long.parseLong(number); }

	private boolean booleanValue(String value) {
		return "TRUE".equals(value);
	}
}
