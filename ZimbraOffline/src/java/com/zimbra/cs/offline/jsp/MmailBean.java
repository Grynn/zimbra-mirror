package com.zimbra.cs.offline.jsp;

public class MmailBean extends ImapBean {
    public static final String Domain = ".msexchange";

    public MmailBean() {}

    @Override
    protected void doRequest() {
        domain = Domain;
        super.doRequest();
    }
}

