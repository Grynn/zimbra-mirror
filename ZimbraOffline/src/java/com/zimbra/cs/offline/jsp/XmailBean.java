package com.zimbra.cs.offline.jsp;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.DataSource.ConnectionType;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.zclient.ZFolder;

public class XmailBean extends MailBean {

    public XmailBean() {}
	
	protected String domain;
	
	protected String username = "";
	protected String fromDisplay = "";
	protected String replyTo = "";
	protected String replyToDisplay = "";

	protected String protocol = "";
	
	protected String smtpHost = "";
	protected String smtpPort = "";
	protected boolean isSmtpSsl;
	protected boolean isSmtpAuth;
	protected String smtpUsername = "";
	protected String smtpPassword = "";
	
	protected boolean syncAllServerFolders;

	protected boolean leaveOnServer;

	protected boolean contactSyncEnabled;
    protected boolean calendarSyncEnabled;
    protected boolean calLoginError;

	private static final String adomain = "aol.com";
	private static final String gdomain = "gmail.com";
	private static final String hdomain = "hotmail.com";
	private static final String mdomain = "msn.com";
	private static final String ydomain = "yahoo.com";
	private static final String ymdomain = "ymail.com";
	private static final String yrmdomain = "rocketmail.com";

	@Override
	protected void reload() {
		DataSource ds;
		try {
			ds = JspProvStub.getInstance().getOfflineDataSource(accountId);
		} catch (ServiceException x) {
			setError(x.getMessage());
			return;
		}

		accountName = ds.getName();
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
		isDebugTraceEnabled = ds.isDebugTraceEnabled();
		contactSyncEnabled = ds.getBooleanAttr(OfflineConstants.A_zimbraDataSourceContactSyncEnabled, false);
        calendarSyncEnabled = ds.getBooleanAttr(OfflineConstants.A_zimbraDataSourceCalendarSyncEnabled, false);

		domain = ds.getAttr(OfflineConstants.A_zimbraDataSourceDomain, null);
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
		syncAllServerFolders = ds.getBooleanAttr(OfflineConstants.A_zimbraDataSourceSyncAllServerFolders, false);
		leaveOnServer = ds.getBooleanAttr(Provisioning.A_zimbraDataSourceLeaveOnServer, false);
	}

	@Override
	protected void doRequest() {
		if (verb == null || !isAllOK())
			return;

		calLoginError = false;
	    try {
			Map<String, Object> dsAttrs = new HashMap<String, Object>();
			DataSource.Type dsType = isEmpty(protocol) ? null : DataSource.Type.fromString(protocol);

			if (verb.isAdd() || verb.isModify()) {
				if (dsType == null)
					addInvalid("protocol");
				if (isEmpty(accountName))
					addInvalid("accountName");
				if (isEmpty(username))
			    	addInvalid("username");
				if (isEmpty(password))
			    	addInvalid("password");
				if (!isValidHost(host))
			    	addInvalid("host");
				if (!isValidPort(port))
			    	addInvalid("port");
				if (!isValidEmail(email))
			    	addInvalid("email");

                if (domain == null) {
                    domain = email.substring(email.indexOf('@') + 1);
                }
				if (!isLive() && !isYmail()) {
					if (!isValidHost(smtpHost))
				    	addInvalid("smtpHost");
					if (!isValidPort(smtpPort))
				    	addInvalid("smtpPort");
					if (isSmtpAuth) {
				    	if (isEmpty(smtpUsername))
				    		addInvalid("smtpUsername");
				    	if (isEmpty(smtpPassword))
				    		addInvalid("smtpPassword");
				    }
				}

			    if (isAllOK()) {
			        dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, Provisioning.TRUE);
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
			        dsAttrs.put(Provisioning.A_zimbraDataSourceEnableTrace, isDebugTraceEnabled ? Provisioning.TRUE : Provisioning.FALSE);

			        if (isContactSyncSupported()) {
						dsAttrs.put(OfflineConstants.A_zimbraDataSourceContactSyncEnabled,
						contactSyncEnabled ? Provisioning.TRUE : Provisioning.FALSE);
					}

			        if (isCalendarSyncSupported()) {
	                     dsAttrs.put(OfflineConstants.A_zimbraDataSourceCalendarSyncEnabled,
	                     calendarSyncEnabled ? Provisioning.TRUE : Provisioning.FALSE);
			        }
			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceDomain, domain);

			        if (!isLive() && !isYmail()) {
				        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpHost, smtpHost);
				        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpPort, smtpPort);
				        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpConnectionType, (isSmtpSsl ? ConnectionType.ssl : ConnectionType.cleartext).toString());
				        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthRequired, isSmtpAuth ? Provisioning.TRUE : Provisioning.FALSE);

				        if (isSmtpAuth) {
				        	dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthUsername, smtpUsername);
					        if (!smtpPassword.equals(JspConstants.MASKED_PASSWORD)) {
					            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword, smtpPassword);
					        }
				        }
			        }

			        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSyncFreq, Long.toString(syncFreqSecs));

					if (dsType == DataSource.Type.imap || dsType == DataSource.Type.live)
						dsAttrs.put(OfflineConstants.A_zimbraDataSourceSyncAllServerFolders, syncAllServerFolders ? Provisioning.TRUE : Provisioning.FALSE);

			        if (dsType == DataSource.Type.pop3) {
			            dsAttrs.put(Provisioning.A_zimbraDataSourceLeaveOnServer, Boolean.toString(leaveOnServer).toUpperCase());
		                    dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, ZFolder.ID_INBOX);
			        } else {
			        	assert dsType == DataSource.Type.imap || dsType == DataSource.Type.live;
			        	dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, ZFolder.ID_USER_ROOT);
			        }
			    }
			}

			if (verb.isAdd()) {
				if (email.endsWith('@' + ydomain) || email.endsWith('@' + ymdomain) || email.endsWith('@' + yrmdomain)) {
					if (dsType == DataSource.Type.imap) {
						dsAttrs.put(OfflineConstants.A_zimbraDataSourceDomain, ydomain);
					} else {
						addInvalid("protocol");
						setError(getMessage("YMPMustUseImap"));
					}
				} else if (email.endsWith('@' + gdomain)) {
					if (dsType == DataSource.Type.imap) {
						dsAttrs.put(OfflineConstants.A_zimbraDataSourceDomain, gdomain);
					} else {
						addInvalid("protocol");
						setError(getMessage("GmailMustUseImap"));
					}
				} else if (email.endsWith('@' + adomain)) {
					if (dsType == DataSource.Type.imap) {
						dsAttrs.put(OfflineConstants.A_zimbraDataSourceDomain, adomain);
					} else {
						addInvalid("protocol");
						setError(getMessage("AOLMustUseImap"));
					}
				} else if (email.endsWith('@' + hdomain) || email.endsWith('@' + mdomain)) {
					if (dsType == DataSource.Type.live) {
						dsAttrs.put(OfflineConstants.A_zimbraDataSourceDomain, hdomain);
					} else {
						addInvalid("protocol");
						setError(getMessage("LiveMustUseLive"));
					}
				}
			}

			if (isAllOK()) {
				JspProvStub stub = JspProvStub.getInstance();
				if (verb.isAdd()) {
					stub.createOfflineDataSource(accountName, email, dsType, dsAttrs);
				} else if (isEmpty(accountId)) {
					setError(getMessage("AccountIdMissing"));
				} else if (verb.isDelete()) {
					stub.deleteOfflineDataSource(accountId);
				} else if (verb.isExport()) {
				} else if (verb.isImport()) {
				} else if (verb.isModify()) {
					stub.modifyOfflineDataSource(accountId, accountName, dsAttrs);
				} else if (verb.isReset()) {
					stub.resetOfflineDataSource(accountId);
				} else {
					setError(getMessage("UnknownAct"));
				}
			}
	    } catch (SoapFaultException x) {
	    	if (x.getCode().equals("account.AUTH_FAILED")) {
	    		setError(getMessage("InvalidUserOrPass"));
	    	} else if (x.getCode().equals("account.ACCOUNT_INACTIVE")) {
	    		setError(getMessage("YMPPlusRequired"));
	    	} else if (x.getCode().equals("offline.CALDAV_LOGIN_FAILED")) {
	    	    setCalDavLoginError("CalAccessErr");
	    	} else if (x.getCode().equals("offline.YCALDAV_NEED_UPGRADE")) {
	    	    setCalDavLoginError("YMPSyncCalUpgradeNote");
	    	} else if (x.getCode().equals("offline.GCALDAV_NEED_ENABLE")) {
	    	    setCalDavLoginError("GmailCalDisabled");
	    	} else if (!(verb != null && verb.isDelete() && x.getCode().equals("account.NO_SUCH_ACCOUNT"))) {
	    		setExceptionError(x);
	    	}
        } catch (Throwable t) {
            setError(t.getMessage());
        }
	}

	protected void setCalDavLoginError(String key) {
	    setError(getMessage(key));
	    calLoginError = true;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

	public void setUsername(String username) {
		this.username = require(username);
	}

	public String getUsername() {
		return username;
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

	public void setSyncAllServerFolders(boolean syncAllServerFolders) {
		this.syncAllServerFolders = syncAllServerFolders;
	}

	public boolean isSyncAllServerFolders() {
		return syncAllServerFolders;
	}

    public boolean isLeaveOnServer() {
        return leaveOnServer;
    }

    public void setLeaveOnServer(boolean leaveOnServer) {
        this.leaveOnServer = leaveOnServer;
    }

    public boolean isLive() {
        return "live".equals(protocol);
    }

	public boolean isYmail() {
		return domain != null && (domain.equals(ydomain) ||
			domain.equals(ymdomain) ||
			domain.equals(yrmdomain));
	}

	public boolean isGmail() {
	    return domain != null && domain.equals(gdomain);
	}

	public void setContactSyncEnabled(boolean enabled) {
		contactSyncEnabled = enabled;
	}

	public boolean isContactSyncEnabled() {
		return contactSyncEnabled;
	}

	public void setCalendarSyncEnabled(boolean enabled) {
	    calendarSyncEnabled = enabled;
	}

	public boolean isCalendarSyncEnabled() {
	    return calendarSyncEnabled;
	}

	public void setCalLoginError(boolean err) {
	    calLoginError = err;
	}

    public boolean isCalLoginError() {
        return calLoginError;
    }

    public boolean isCalendarSyncSupported() {
        return isYmail() || isGmail();
    }

    public boolean isContactSyncSupported() {
        return isLive() || isYmail() || isGmail();
    }
}