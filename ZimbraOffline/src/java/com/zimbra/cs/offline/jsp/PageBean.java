package com.zimbra.cs.offline.jsp;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;

public class PageBean {

	public PageBean() {}
	
	public String getAppVersion() {
		return LC.get("zdesktop_version");
	}
	
	public String getLoginUsername() {
		try {
			return JspProvStub.getInstance().getLoginAccountName();
		} catch (ServiceException x) {
			return JspConstants.LOCAL_ACCOUNT;
		}
	}
}
