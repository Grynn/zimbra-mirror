package com.zimbra.cs.offline.jsp;

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;

public class PageBean {

	private static final String APP_VERSION = LC.get("zdesktop_relabel") + LC.get("zdesktop_version") + " (build " + LC.get("zdesktop_buildid") + ")";
	
	private Locale locale = new Locale("en");
	
	protected String getMessage(String key) {
		ResourceBundle bundle = ResourceBundle.getBundle("/desktop/ZdMsg", locale);
		String msg =  bundle.getString(key);
		return msg == null ? "??" + key + "??" : msg; 
	}
	
	protected String getMessage(String key, Object[] params) {
		return MessageFormat.format(getMessage(key), params);
	}
	
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
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
