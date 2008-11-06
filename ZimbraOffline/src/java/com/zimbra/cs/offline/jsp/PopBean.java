package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class PopBean extends XmailBean {
    public PopBean() {
        port = "110";
        type = DataSource.Type.pop3.toString();
    }
}
