package com.zimbra.cs.offline.jsp;

import com.zimbra.common.localconfig.LC;

public class PageBean {

	public String getAppVersion() {
		return LC.get("zdesktop_version");
	}
}
