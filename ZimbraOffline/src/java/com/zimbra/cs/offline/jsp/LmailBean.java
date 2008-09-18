package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class LmailBean extends XmailBean {
    public LmailBean() {}

    @Override
    protected void doRequest() {
        host = "www.hotmail.com";
        isSsl = false;
        port = "80";
        protocol = DataSource.Type.live.toString();
        if (verb != null && (verb.isAdd() || verb.isModify())) {
            domain = "hotmail.com";
            if (!isEmpty(email) && email.indexOf('@') < 0)
                email += '@' + domain;
            username = email;
        }
        super.doRequest();
    }
}
