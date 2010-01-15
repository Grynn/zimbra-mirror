/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.util;

import java.util.Arrays;
import java.util.Comparator;

public class OfflineUtil {

	public static <A> void fixItemOrder(final A[] oldOrder, final A[] newOrder) {
        Arrays.sort(newOrder, new Comparator<A>() {
			public int compare(A o1, A o2) {
				int index1 = newOrder.length, index2 = newOrder.length;
				for (int i = 0; i < oldOrder.length; ++i) {
					if (o1.equals(oldOrder[i]))
						index1 = i;
					else if (o2.equals(oldOrder[i]))
						index2 = i;
				}
				return index1 - index2;
			}
        });
	}
}
