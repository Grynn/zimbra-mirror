/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.jsp;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.DataSource.ConnectionType;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.jsp.JspConstants.JspVerb;
import com.zimbra.cs.zclient.ZFolder;

public class XsyncBean extends MailBean {
    protected String fromDisplay = "";
    protected String domain = "";
    
	public XsyncBean() {
		type = "xsync";
		connectionType = ConnectionType.ssl;
		port = "443";
	}
	
    @Override
	protected void reload() {
        DataSource ds;
        try {
            ds = JspProvStub.getInstance().getOfflineDataSource(accountId);
        } catch (ServiceException x) {
            setError(x.getMessage());
            return;
        }
        accountFlavor = ds.getAttr(OfflineConstants.A_offlineAccountFlavor);
        accountName = ds.getName();
        password = JspConstants.MASKED_PASSWORD;
        email = ds.getEmailAddress();
        fromDisplay = ds.getFromDisplay();
        username = ds.getUsername();
        domain = ds.getDomain() == null ? "" : ds.getDomain();

        type = ds.getType().toString();
        host = ds.getHost();
        port = ds.getPort().toString();
        connectionType = ds.getConnectionType();
        isDebugTraceEnabled = ds.isDebugTraceEnabled();
        syncFreqSecs = ds.getTimeIntervalSecs(
            OfflineConstants.A_zimbraDataSourceSyncFreq,
            OfflineConstants.DEFAULT_SYNC_FREQ / 1000);
	}

	@Override
	protected void doRequest() {
        if (verb == null || !isAllOK())
            return;
        try {
            Map<String, Object> dsAttrs = new HashMap<String, Object>();
            DataSource.Type dsType = isEmpty(type) ? null : DataSource.Type.fromString(type);

            if (verb.isAdd() || verb.isModify()) {
                if (dsType == null)
                    addInvalid("type");
                if (isEmpty(accountFlavor))
                    addInvalid("flavor");
                if (isEmpty(accountName))
                    addInvalid("accountName");
                if (isEmpty(password))
                    addInvalid("password");
                if (!isValidHost(host))
                    addInvalid("host");
                if (!isEmpty(port) && !isValidPort(port))
                    addInvalid("port");
                if (!isValidEmail(email))
                    addInvalid("email");
                if (isEmpty(username))
                    addInvalid("username");
                
                if (username == null || username.length() == 0)
                    username = email.substring(0, email.indexOf('@'));

                if (isAllOK()) {
                	dsAttrs.put(OfflineConstants.A_zimbraDataSourceAccountSetup, Provisioning.TRUE);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, Provisioning.TRUE);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceUsername, username);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceDomain, domain);
                    if (!password.equals(JspConstants.MASKED_PASSWORD))
                    	dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, password);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceEmailAddress, email);
                    dsAttrs.put(Provisioning.A_zimbraPrefFromDisplay, fromDisplay);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceHost, host);
                    dsAttrs.put(Provisioning.A_zimbraDataSourcePort, port);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceConnectionType, connectionType.toString());
                    dsAttrs.put(Provisioning.A_zimbraDataSourceEnableTrace, isDebugTraceEnabled ? Provisioning.TRUE : Provisioning.FALSE);
                    dsAttrs.put(OfflineConstants.A_zimbraDataSourceContactSyncEnabled, Provisioning.TRUE);
                    dsAttrs.put(OfflineConstants.A_zimbraDataSourceCalendarSyncEnabled, Provisioning.TRUE);
                    dsAttrs.put(OfflineConstants.A_zimbraDataSourceTaskSyncEnabled, Provisioning.TRUE);
                    dsAttrs.put(OfflineConstants.A_zimbraDataSourceSyncFreq, Long.toString(syncFreqSecs));
                    dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, ZFolder.ID_USER_ROOT);
                    if (sslCertAlias != null && sslCertAlias.length() > 0)
                    	dsAttrs.put(OfflineConstants.A_zimbraDataSourceSslCertAlias, sslCertAlias);
                    if (verb.isAdd())
                        dsAttrs.put(OfflineConstants.A_offlineAccountFlavor, accountFlavor);
                }
            }

            if (isAllOK()) {
                JspProvStub stub = JspProvStub.getInstance();
                if (verb.isAdd()) {
                    accountId = stub.createOfflineDataSource(accountName, email, dsType, dsAttrs).getId();
                } else if (isEmpty(accountId)) {
                    setError(getMessage("AccountIdMissing"));
                } else if (verb.isDelete()) {
                    stub.deleteOfflineDataSource(accountId);
                } else if (verb.isModify()) {
                    stub.modifyOfflineDataSource(accountId, accountName, dsAttrs);
                } else if (verb.isReset()) {
                    stub.resetOfflineDataSource(accountId);
    		    } else if (verb.isReindex()) {
    		    	stub.reIndex(accountId);
                } else {
                    setError(getMessage("UnknownAct"));
                }
            }
        } catch (SoapFaultException x) {
            if (x.getCode().equals("account.AUTH_FAILED")) {
                setError(getMessage("InvalidUserOrPass"));
            } else if (!(verb != null && verb.isDelete() && x.getCode().equals("account.NO_SUCH_ACCOUNT"))) {
                setExceptionError(x);
            }
        } catch (Exception t) {
            setError(t.getLocalizedMessage() == null ? t.toString() : t.getLocalizedMessage());
        }
	}
	
    public String getFromDisplay() {
        return fromDisplay;
    }

    public void setFromDisplay(String fromDisplay) {
        this.fromDisplay = optional(fromDisplay);
    }
	
	public String getDomain() {
	    return domain;
	}
	
	public void setDomain(String domain) {
	    this.domain = optional(domain);
	}
	
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
	
    public boolean isSmtpConfigSupported() {
        return false;
    }

    public boolean isUsernameRequired() {
        return true;
    }
    
    public static String createAccount(String accountName, String username, String password, String email, String host, int port, boolean isSSL) throws Exception {
        XsyncBean xb = new XsyncBean();
        xb.verb = JspVerb.add;
        xb.type = "xsync";
        xb.accountFlavor = "Xsync";
        xb.accountName = accountName;
        xb.username = username;
        xb.password = password;
        xb.email = email;
        xb.host = host;
        xb.port = "" + port;
        xb.connectionType = isSSL ? ConnectionType.ssl : ConnectionType.cleartext;
        xb.isDebugTraceEnabled = true;
        xb.syncFreqSecs=-1;
        xb.doRequest();
        if (xb.getError() != null)
            throw new RuntimeException(xb.getError());
        return xb.accountId;
    }
    
    public static void deleteAccount(String accountId) throws Exception {
        XsyncBean xb = new XsyncBean();
        xb.verb = JspVerb.del;
        xb.accountId = accountId;
        xb.doRequest();
    }
}
