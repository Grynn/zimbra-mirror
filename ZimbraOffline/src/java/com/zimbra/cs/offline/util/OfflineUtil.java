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
