package com.zimbra.cs.offline.jsp;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.account.Provisioning;

public class PageBean {

	private static final String APP_VERSION = LC.get("zdesktop_relabel") + LC.get("zdesktop_version") + " (build " + LC.get("zdesktop_buildid") + ")";
	
	private Locale clientLocale = new Locale("en");
	private static Locale serverLocale = null; 
	
	protected String getMessage(String key) {
		ResourceBundle bundle = ResourceBundle.getBundle("/desktop/ZdMsg", clientLocale);
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
	
	public void setLocale(Locale locale) throws ServiceException {
		clientLocale = locale;
		updateServerLocale(locale);
	}
	
	public static synchronized void updateServerLocale(Locale locale) throws ServiceException {
	    if (serverLocale == null || !serverLocale.equals(locale)) {	        
	        Map<String, Object> attrs = new HashMap<String, Object>();
	        attrs.put(Provisioning.A_zimbraPrefLocale, locale.toString());
	        JspProvStub stub = JspProvStub.getInstance();
	        stub.modifyOfflineAccount(OfflineConstants.LOCAL_ACCOUNT_ID, attrs);
	        
	        serverLocale = (Locale)locale.clone();						
	    }
	}
}
