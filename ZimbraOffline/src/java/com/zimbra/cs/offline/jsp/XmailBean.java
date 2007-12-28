package com.zimbra.cs.offline.jsp;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.DataSource.ConnectionType;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.jsp.JspConstants.JspVerb;

public class XmailBean extends FormBean {
	
	public XmailBean() {}
	
	private JspVerb verb;
	
	private String accountId;
	private String dsName;
	
	private String username = "";
	private String password = "";
	private String email = "";
	private String fromDisplay = "";
	private String replyTo = "";
	private String replyToDisplay = "";

	private String protocol = "";
	private String host = "";
	private String port = "";
	private boolean isSsl;
	
	private String smtpHost = "";
	private String smtpPort = "";
	private boolean isSmtpSsl;
	private boolean isSmtpAuth;
	private String smtpUsername = "";
	private String smtpPassword = "";
	
	private long syncFreqSecs = OfflineConstants.DEFAULT_SYNC_FREQ / 1000;
	
	
	@Override
	protected void reload() {
		DataSource ds = null;
		try {
			ds = JspProvStub.getInstance().getOfflineDataSource(accountId);
		} catch (ServiceException x) {
			setError(x.getMessage());
			return;
		}
		
		dsName = ds.getName();
		username = ds.getUsername();
		password = JspConstants.MASKED_PASSWORD;
		email = ds.getEmailAddress();
		fromDisplay = ds.getFromDisplay();
		replyTo = ds.getReplyToAddress();
		replyToDisplay = ds.getReplyToDisplay();
		protocol = ds.getType().toString();
		host = ds.getHost();
		port = ds.getPort().toString();
		isSsl = ds.getConnectionType() == DataSource.ConnectionType.ssl;
		
		smtpHost = ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpHost, null);
		smtpPort = ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpPort, null);
		isSmtpSsl = "ssl".equals(ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpConnectionType));
		isSmtpAuth = ds.getBooleanAttr(OfflineConstants.A_zimbraDataSourceSmtpAuthRequired, false);
		if (isSmtpAuth) {
			smtpUsername = ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpAuthUsername, null);
			smtpPassword = isEmpty(ds.getAttr(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword, null)) ? null : JspConstants.MASKED_PASSWORD;
		} else {
			smtpUsername = "";
			smtpPassword = "";
		}

		syncFreqSecs = ds.getTimeIntervalSecs(OfflineConstants.A_zimbraDataSourceSyncFreq, OfflineConstants.DEFAULT_SYNC_FREQ / 1000);
	}
	
	@Override
	protected void doRequest() {
		if (verb == null || !isAllOK())
			return;
		
	    try {
			Map<String, Object> dsAttrs = new HashMap<String, Object>();
			DataSource.Type dsType = isEmpty(protocol) ? null : DataSource.Type.fromString(protocol);
			
			if (verb.isAdd() || verb.isModify()) {
				if (dsType == null)
					addInvalid("protocol");
				if (isEmpty(dsName))
					addInvalid("dataSourceName");
				if (isEmpty(username))
			    	addInvalid("username");
				if (isEmpty(password))
			    	addInvalid("password");
				if (isEmpty(host))
			    	addInvalid("host");
				if (!isValidPortNumber(port))
			    	addInvalid("port");
				if (!isValidEmail(email))
			    	addInvalid("email");
				if (isEmpty(smtpHost))
			    	addInvalid("smtpHost");
				if (!isValidPortNumber(smtpPort))
			    	addInvalid("smtpPort");
				if (isSmtpAuth) {
			    	if (isEmpty(smtpUsername))
			    		addInvalid("smtpUsername");
			    	if (isEmpty(smtpPassword))
			    		addInvalid("smtpPassword");
			    }
                    
			    if (isAllOK()) {
			        dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, JspConstants.TRUE);
			        dsAttrs.put(Provisioning.A_zimbraDataSourceUsername, username);
			        if (!password.equals(JspConstants.MASKED_PASSWORD)) {
			            dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, password);
			        }
			        
			        dsAttrs.put(Provisioning.A_zimbraDataSourceEmailAddress, email);
			        dsAttrs.put(Provisioning.A_zimbraPrefFromDisplay, fromDisplay);
			        dsAttrs.put(Provisioning.A_zimbraPrefReplyToAddress, replyTo);
			        dsAttrs.put(Provisioning.A_zimbraPrefReplyToDisplay, replyToDisplay);
			
			        dsAttrs.put(Provisioning.A_zimbraDataSourceHost, host);
			        dsAttrs.put(Provisioning.A_zimbraDataSourcePort, port);
			        dsAttrs.put(Provisioning.A_zimbraDataSourceConnectionType, (isSsl ? ConnectionType.ssl : ConnectionType.cleartext).toString());
			
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpHost, smtpHost);
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpPort, smtpPort);
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpConnectionType, (isSmtpSsl ? ConnectionType.ssl : ConnectionType.cleartext).toString());
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthRequired, isSmtpAuth ? JspConstants.TRUE : JspConstants.FALSE);
			        
			        if (isSmtpAuth) {
			        	dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthUsername, smtpUsername);
				        if (!smtpPassword.equals(JspConstants.MASKED_PASSWORD)) {
				            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword, smtpPassword);
				        }
			        }
			        
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSyncFreq, Long.toString(syncFreqSecs));
			        if (dsType == DataSource.Type.pop3) {
			            dsAttrs.put(Provisioning.A_zimbraDataSourceLeaveOnServer, JspConstants.TRUE);
		                dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, Integer.toString(Mailbox.ID_FOLDER_INBOX));
			        }
			    }
			}
			
			JspProvStub stub = JspProvStub.getInstance();
			if (isAllOK()) {                
			    if (verb.isAdd()) {
			        stub.createOfflineDataSource(dsName, email, dsType, dsAttrs);
			    } else {
			        if (isEmpty(accountId)) {
			            setError("Account ID missing");
			        } else if (verb.isModify()) {
			            stub.modifyOfflineDataSource(accountId, dsName, dsAttrs);
			        } else if (verb.isReset()) {
					    stub.resetOfflineDataSource(accountId);
					} else if (verb.isDelete()) {
					    stub.deleteOfflineDataSource(accountId);
					} else {
					    setError("Unknown action");
	                }
	            }
			}
        } catch (Throwable t) {
            setError(t.getMessage());
        }
	}
	
	public void setVerb(String strVerb) {
		verb = strVerb != null ? JspVerb.fromString(strVerb) : null;
	}
	
	public boolean isNoVerb() {
		return verb == null;
	}
	
	public boolean isAdd() {
		return verb != null && verb.isAdd();
	}
	
	public boolean isModify() {
		return verb != null && verb.isModify();
	}
	
	public boolean isReset() {
		return verb != null && verb.isReset();
	}
	
	public boolean isDelete() {
		return verb != null && verb.isDelete();
	}

	public void setAccountId(String accountId) {
		this.accountId =  accountId;
	}
	
	public String getAccountId() {
		return accountId;
	}
	
	public void setDataSourceName(String dsName) {
		this.dsName = require(dsName);
	}
	
	public String getDataSourceName() {
		return dsName;
	}
	
	public void setUsername(String username) {
		this.username = require(username);
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPassword(String password) {
		this.password = require(password);
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setEmail(String email) {
		this.email = require(email);
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setFromDisplay(String fromDisplay) {
		this.fromDisplay = optional(fromDisplay);
	}
	
	public String getFromDisplay() {
		return fromDisplay;
	}
	
	public void setReplyTo(String replyTo) {
		this.replyTo = optional(replyTo);
	}
	
	public String getReplyTo() {
		return replyTo;
	}
	
	public void setReplyToDisplay(String replyToDisplay) {
		this.replyToDisplay = optional(replyToDisplay);
	}
	
	public String getReplyToDisplay() {
		return replyToDisplay;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public boolean isImap() {
		return "imap".equals(protocol);
	}
	
	public boolean isPop() {
		return "pop3".equals(protocol);
	}
	
	public void setHost(String host) {
		this.host = require(host);
	}
	
	public String getHost() {
		return host;
	}
	
	public void setPort(String port) {
		this.port = require(port);
	}
	
	public String getPort() {
		return port;
	}
	
	public void setSsl(boolean isSsl) {
		this.isSsl = isSsl;
	}
	
	public boolean isSsl() {
		return isSsl;
	}
	
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = require(smtpHost);
	}
	
	public String getSmtpHost() {
		return smtpHost;
	}
	
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = require(smtpPort);
	}
	
	public String getSmtpPort() {
		return smtpPort;
	}
	
	public void setSmtpSsl(boolean isSmtpSsl) {
		this.isSmtpSsl = isSmtpSsl;
	}
	
	public boolean isSmtpSsl() {
		return isSmtpSsl;
	}

	public void setSmtpAuth(boolean isSmtpAuth) {
		this.isSmtpAuth = isSmtpAuth;
	}
	
	public boolean isSmtpAuth() {
		return isSmtpAuth;
	}
	
	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = optional(smtpUsername);
	}
	
	public String getSmtpUsername() {
		return smtpUsername;
	}
	
	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = optional(smtpPassword);
	}
	
	public String getSmtpPassword() {
		return smtpPassword;
	}
	
	public void setSyncFreqSecs(long syncFreqSecs) {
		this.syncFreqSecs = syncFreqSecs;
	}
	
	public long getSyncFreqSecs() {
		return syncFreqSecs;
	}
}
