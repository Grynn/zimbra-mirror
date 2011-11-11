/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.jsp;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.client.ZMailbox;
import com.zimbra.client.ZFolder;
import com.zimbra.soap.type.DataSource.ConnectionType;

public class MailBean extends FormBean {
    public MailBean() {}
    
    protected String accountId;
    protected String accountName = "";
    protected String accountFlavor = "";
    protected String type = "";

    protected String email = "";
    protected String password = "";
    protected String username = "";

    protected String host = "";
    protected String port = "";

    protected ConnectionType connectionType = ConnectionType.cleartext;
    
    protected long syncFreqSecs = OfflineConstants.DEFAULT_SYNC_FREQ / 1000;

    protected String syncEmailDate = "";
    protected String syncFixedDate =  "";
    protected String syncRelativeDate = "";
    protected String syncFieldName= "";

    protected boolean isDebugTraceEnabled;
    protected boolean isExpireOldEmailsEnabled;
    
    public String getAccountId() {
	return accountId;
    }
    
    public void setAccountId(String accountId) {
	this.accountId = accountId;
    }
    
    public String getAccountName() {
	return accountName;
    }
    
    public void setAccountName(String accountName) {
	this.accountName = require(accountName);
    }

    public String getAccountFlavor() {
	return accountFlavor;
    }
    
    public void setAccountFlavor(String accountFlavor) {
	this.accountFlavor = require(accountFlavor);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getPassword() {
	return password;
    }
    
    public void setPassword(String password) {
	this.password = require(password);
    }
    
    public String getEmail() {
	return email;
    }
    
    public void setEmail(String email) {
	this.email = require(email);
    }
    
    public String getHost() {
	return host;
    }
    
    public void setHost(String host) {
	this.host = require(host);
    }
    
    public String getPort() {
	return port;
    }
    
    public void setPort(String port) {
	this.port = require(port);
    }

    public void setSecurity(String security) {
        connectionType = ConnectionType.valueOf(security);
    }

    public String getSecurity() {
        return connectionType.toString();
    }
    
    public boolean isSsl() {
        return connectionType == ConnectionType.ssl;
    }
    
    public boolean isDebugTraceEnabled() {
        return isDebugTraceEnabled;
    }

    public void setDebugTraceEnabled(boolean isDebugTraceEnabled) {
        this.isDebugTraceEnabled = isDebugTraceEnabled;
    }

    public boolean isExpireOldEmailsEnabled() {
        return isExpireOldEmailsEnabled;
    }

    public void setExpireOldEmailsEnabled(boolean isExpireOldEmailsEnabled) {
        this.isExpireOldEmailsEnabled = isExpireOldEmailsEnabled;
    }

    public String getsyncEmailDate() {
        return syncEmailDate;
    }

    public void setsyncEmailDate(String syncEmailDate) {
        this.syncEmailDate = syncEmailDate;
    }

    public String getsyncFixedDate() {
        return syncFixedDate;
    }

    public void setsyncFixedDate(String syncFixedDate) {
        this.syncFixedDate = syncFixedDate;
    }

    public String getsyncRelativeDate() {
        return syncRelativeDate;
    }

    public void setsyncRelativeDate(String syncRelativeDate) {
        this.syncRelativeDate = syncRelativeDate;
    }

    public String getsyncFieldName() {
        return syncFieldName;
    }

    public void setsyncFieldName(String syncFieldName) {
        this.syncFieldName = syncFieldName;
    }

    public long getSyncFreqSecs() {
        return syncFreqSecs;
    }

    public void setSyncFreqSecs(long syncFreqSecs) {
        this.syncFreqSecs = syncFreqSecs;
    }

    public boolean getZmail() {
	try {
	    JspProvStub stub = JspProvStub.getInstance();
	    Account account = stub.getOfflineAccount(accountId);

	    return account.getAttr(OfflineConstants.A_offlineRemoteServerUri,
		null) != null;
	} catch (Exception e) {
	}
	return false;
    }
    
    public String[] getExportList() throws ServiceException {
	return getFolderList(true);
    }

    public String[] getImportList() throws ServiceException {
	return getFolderList(false);
    }

    String[] getFolderList(boolean export) throws ServiceException {
	List<ZFolder> fldrs;
	ArrayList<String> list = new ArrayList<String>();
	ZMailbox.Options options = new ZMailbox.Options();

	options.setAccountBy(AccountBy.id);
	options.setAccount(accountId);
	options.setPassword(password);
	options.setUri(ConfigServlet.LOCALHOST_SOAP_URL);
	ZMailbox mbox = ZMailbox.getMailbox(options);
	fldrs = mbox.getAllFolders();
	for (ZFolder f : fldrs) {
	    if (f.getPath().equals("/") || f.getParentId() == null ||
		f.getId().equals(ZFolder.ID_AUTO_CONTACTS))
		continue;
	    else if (!export && f.getClass() != ZFolder.class)
		continue;
	    list.add(f.getRootRelativePath());
	}
	return list.toArray(new String[0]);
    }

    @Override protected void reload() {}

    @Override protected void doRequest() {
	if (verb == null || !isAllOK())
	    return;
	if (verb.isReset()) {
	    try {
		JspProvStub stub = JspProvStub.getInstance();

		stub.resetOfflineDataSource(accountId);
	    } catch (Exception t) {
		setError(t.getMessage());
	    }
	}
    }

    public boolean isCalendarSyncSupported() {
        return false;
    }

    public boolean isContactSyncSupported() {
        return false;
    }

    public boolean isFolderSyncSupported() {
        return false;
    }

    public boolean isServerConfigSupported() {
        return true;
    }

    public boolean isSmtpConfigSupported() {
        return true;
    }

    public boolean isUsernameRequired() {
        return true;
    }
}
