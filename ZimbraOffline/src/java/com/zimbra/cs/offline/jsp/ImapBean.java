package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class ImapBean extends XmailBean {
    public ImapBean() {
        port = "143";
        type = DataSource.Type.imap.toString();
    }

    public boolean isFolderSyncSupported() {
	return true;
    }
}
