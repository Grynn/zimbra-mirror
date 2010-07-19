package framework.util;

import framework.core.SelNGBase;

public class ZimbraUtil extends SelNGBase {
	private static String zimbraVersion = "";

	public static String getZimbraVersion() {
		// ps: getZimbraVersion is being passed just to trick selenium.call to
		// return string(because of get*)
		if (zimbraVersion.equals(""))
			zimbraVersion = selenium.call("zGetZimbraVersion", "", "getZimbraVersion", false);

		return zimbraVersion;
	}

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
