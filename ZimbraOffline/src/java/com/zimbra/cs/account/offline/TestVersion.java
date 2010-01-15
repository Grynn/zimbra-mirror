/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import junit.framework.TestCase;

public class TestVersion extends TestCase {
	
	public void testVersion() {
		
		OfflineAccount.Version v = new OfflineAccount.Version("4.5.6_GA_763.FC5_20070323134056");
		
		System.out.println("major=" + v.getMajor());
		System.out.println("minor=" + v.getMinor());
		System.out.println("maint=" + v.getMaintenance());
		
		v = new OfflineAccount.Version("5.a");
		
		System.out.println("major=" + v.getMajor());
		System.out.println("minor=" + v.getMinor());
		System.out.println("maint=" + v.getMaintenance());
	}

}
