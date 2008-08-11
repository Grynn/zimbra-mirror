package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class MmailBean extends XmailBean {
    public MmailBean() {}

    private void fixup() {
        if (verb == null) return;
        domain = ".msexchange";
        protocol = DataSource.Type.imap.toString();
    }

    @Override
    protected void doRequest() {
        fixup();
        super.doRequest();
    }
}
