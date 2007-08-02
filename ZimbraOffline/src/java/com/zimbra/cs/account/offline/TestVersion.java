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
