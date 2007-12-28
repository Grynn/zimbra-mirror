package com.zimbra.cs.offline.jsp;

import java.util.Arrays;

public interface JspConstants {
	
	public enum JspVerb {
		add, mod, rst, del;
		
		public boolean isAdd() { return this == add; }
		public boolean isModify() { return this == mod; }
		public boolean isReset() { return this == rst; }
		public boolean isDelete() { return this == del; }
		
        public static JspVerb fromString(String s) {
            try {
                return s == null ? null : JspVerb.valueOf(s);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("invalid type: " + s + ", valid values: " + Arrays.asList(JspVerb.values()), e); 
            }
        }
	}
	

	public static final String LOCAL_ACCOUNT = "local@host.local";
	public static final String MASKED_PASSWORD = "********";
	public static final String DUMMY_PASSWORD = "topsecret";
	
	public static final String OFFLINE_REMOTE_HOST = "offlineRemoteHost";
	public static final String OFFLINE_REMOTE_PORT = "offlineRemotePort";
	public static final String OFFLINE_REMOTE_SSL = "offlineRemoteSsl";
	
    public static final String LOCALHOST_URL = "http://localhost:7633";
    public static final String LOCALHOST_SOAP_URL = LOCALHOST_URL + "/service/soap/";
    public static final String LOCALHOST_ADMIN_URL = "http://localhost:7634" + "/service/admin/soap/";
    public static final String LOCALHOST_RESOURCE_URL = LOCALHOST_URL + "/zimbra/";
    public static final String LOCALHOST_MAIL_URL = LOCALHOST_URL + "/zimbra/mail";
}
