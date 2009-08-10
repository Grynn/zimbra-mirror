package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.offline.OfflineLC;

public class MobileMeBean extends ImapBean {
    public static final String Domain = OfflineLC.zdesktop_mobileme_domain.value();

    public MobileMeBean() {}

    @Override
    protected void doRequest() {
	domain = Domain;
        if (verb != null && (verb.isAdd() || verb.isModify())) {
            if (!isEmpty(email) && email.indexOf('@') < 0)
                email += '@' + domain;
	    username = email;
        }
	host = OfflineLC.zdesktop_mobileme_imap_host.value();
        try {
            connectionType = DataSource.ConnectionType.valueOf(
                OfflineLC.zdesktop_mobileme_imap_connection_type.value());
        } catch (IllegalArgumentException e) {
            connectionType = DataSource.ConnectionType.cleartext;
        }
	port = OfflineLC.zdesktop_mobileme_imap_port.value();

	smtpHost = OfflineLC.zdesktop_mobileme_smtp_host.value();
	smtpPort = OfflineLC.zdesktop_mobileme_smtp_port.value();
	isSmtpSsl = OfflineLC.zdesktop_mobileme_smtp_ssl.booleanValue();
	isSmtpAuth = OfflineLC.zdesktop_mobileme_smtp_auth.booleanValue();
	smtpUsername = email;
	smtpPassword = password;
	super.doRequest();
    }

    public boolean isCalendarSyncSupported() {
	return false;
    }

    public boolean isContactSyncSupported() {
	return false;
    }

    public boolean isServerConfigSupported() {
	return false;
    }

    public boolean isSmtpConfigSupported() {
	return false;
    }

    public boolean isUsernameRequired() {
	return false;
    }
}
