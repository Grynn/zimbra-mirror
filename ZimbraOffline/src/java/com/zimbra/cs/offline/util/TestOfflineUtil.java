package com.zimbra.cs.offline.util;

import junit.framework.TestCase;

public class TestOfflineUtil extends TestCase {
	public void testFixItemOrder() {
		String[] oldOrder = {"abc", "foo", "bar"};
		String[] newOrder = {"def", "abc", "bar", "xyz", "123"};
		
		OfflineUtil.fixItemOrder(oldOrder, newOrder);
		
		assertEquals(newOrder[0], "abc");
		assertEquals(newOrder[1], "bar");
	}
}
