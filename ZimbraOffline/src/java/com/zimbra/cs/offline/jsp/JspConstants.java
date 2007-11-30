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
	

	public static final String TRUE = "TRUE";
	public static final String FALSE = "FALSE";
	public static final String CHECKED = "checked";
	public static final String SELECTED = "selected";

	public static final String LOCAL_ACCOUNT = "local@host.local";
	public static final String MASKED_PASSWORD = "********";
	public static final String DUMMY_PASSWORD = "topsecret";
	
	public static final String PARAM_VERB = "verb";
	
	public static final String PARAM_ACCOUNT_ID = "account_id";
	public static final String PARAM_DATASOURCE_NAME = "ds_name";
	
	public static final String PARAM_SERVER_HOST = "host";
	public static final String PARAM_SERVER_PORT = "port";
	public static final String PARAM_SERVER_SSL = "ssl";
	public static final String PARAM_SERVER_PROTOCOL = "protocol";
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_PASSWORD = "password";
	
	public static final String PARAM_SMTP_HOST = "smtp_host";
	public static final String PARAM_SMTP_PORT = "smtp_port";
	public static final String PARAM_SMTP_SSL = "smtp_ssl";
	public static final String PARAM_SMTP_AUTH = "smtp_auth";
	public static final String PARAM_SMTP_USER = "smtp_user";
	public static final String PARAM_SMTP_PASS = "smtp_pass";

	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_FROM_DISPLAY = "from_display";
	public static final String PARAM_REPLYTO = "replyto";
	public static final String PARAM_REPLYTO_DISPLAY = "replyto_display";
	public static final String PARAM_POP_LEAVE_ON_SERVER = "pop_los";
	
    public static final String LOCALHOST_URL = "http://localhost:7633";
    public static final String LOCALHOST_SOAP_URL = LOCALHOST_URL + "/service/soap/";
    public static final String LOCALHOST_ADMIN_URL = "http://localhost:7634" + "/service/admin/soap/";
    public static final String LOCALHOST_RESOURCE_URL = LOCALHOST_URL + "/zimbra/";
    public static final String LOCALHOST_MAIL_URL = LOCALHOST_URL + "/zimbra/mail";
}
