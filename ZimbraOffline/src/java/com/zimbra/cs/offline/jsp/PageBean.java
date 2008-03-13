package com.zimbra.cs.offline.jsp;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;

public class PageBean {

	private static final String APP_VERSION = LC.get("zdesktop_relabel") + LC.get("zdesktop_version") + " (build " + LC.get("zdesktop_buildid") + ")";
	
	public PageBean() {}
	
	public String getAppVersion() {
		return APP_VERSION;
	}
	
	public String getLoginUsername() {
		try {
			return JspProvStub.getInstance().getLoginAccountName();
		} catch (ServiceException x) {
			return JspConstants.LOCAL_ACCOUNT;
		}
	}
}
