package com.zimbra.cs.offline.jsp;

import java.util.Arrays;

public interface JspConstants {
	
	public enum JspVerb {
		add, del, exp, imp, mod, rst;
		
		public boolean isAdd() { return this == add; }
                public boolean isDelete() { return this == del; }
                public boolean isExport() { return this == exp; }
                public boolean isImport() { return this == imp; }
		public boolean isModify() { return this == mod; }
                public boolean isReset() { return this == rst; }
		
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
}
