package com.zimbra.cs.offline;

import com.zimbra.cs.util.ZimbraApplication;

public class OfflineApplication extends ZimbraApplication {

	@Override
	public boolean supports(String className) {
		return false;
	}
}
