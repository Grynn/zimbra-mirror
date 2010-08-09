package framework.util;

import framework.core.SelNGBase;

public class ZimbraUtil extends SelNGBase {


	public static String printUnMatchedTextWithIndex(String actual,
			String expected) {
		if (actual.length() != expected.length()) {
			return "actual(" + actual + ") expected(" + expected + ")";
		}

		char[] arr = new char[actual.length()];
		actual.getChars(0, actual.length(), arr, 0);
		char[] arr2 = new char[expected.length()];
		expected.getChars(0, expected.length(), arr2, 0);
		String retval = "";
		for (int i = 0; i < arr.length; i++) {
			int t = (int) arr[i];
			int t2 = (int) arr2[i];
			if (t != t2) {
				retval = retval + " index=" + i + " ('" + arr[i] + "' != '"
						+ arr2[i] + "'    '" + t + "' != '" + t2 + "')\n";
			}
		}
		return retval;
	}
}
