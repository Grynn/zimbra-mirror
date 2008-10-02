package com.zimbra.cs.offline.jsp;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZFolder;


public class CalDavBean extends FormBean {
	
	public CalDavBean() {}
	
	protected String accountId;
	protected String accountName;
	protected String principalPath;
	protected String principalUrl;
	protected String defaultCalDavUrl;
	
	protected String displayName = "";
	protected String email = "";
	protected String password = "";
	
	protected String mailUsername;
	protected String mailPassword;

	protected String host = "";
	protected String port = "";
	protected boolean isSsl;
	protected boolean useLoginFromEmail = true;
	
	protected long syncFreqSecs = OfflineConstants.DEFAULT_SYNC_FREQ / 1000;
	
	protected boolean isDebugTraceEnabled;
	
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
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getPrincipalPath() {
		return principalPath;
	}
	
	public void setPrincipalPath(String principalPath) {
		this.principalPath = principalPath;
	}
	
	public String getPrincipalUrl() {
		if (principalUrl == null || principalUrl.length() == 0)
			return defaultCalDavUrl;
		return principalUrl;
	}
	
	public void setPrincipalUrl(String principalUrl) {
		this.principalUrl = principalUrl;
	}
	
	public String getDefaultCalDavUrl() {
		return defaultCalDavUrl;
	}
	
	public void setDefaultCalDavUrl(String caldavUrl) {
		this.defaultCalDavUrl = caldavUrl;
	}
	
	public String getPassword() {
		return JspConstants.MASKED_PASSWORD;
	}
	
	public void setPassword(String password) {
		this.password = require(password);
	}
	
	public String getEmail() {
		if (useLoginFromEmail)
			return mailUsername;
		return email;
	}
	
	public void setEmail(String email) {
		this.email = require(email);
	}

	public String getMailUsername() {
		return mailUsername;
	}
	
	public void setMailUsername(String email) {
		mailUsername = email;
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
	
	public boolean isSsl() {
		return isSsl;
	}
	
	public void setSsl(boolean isSsl) {
		this.isSsl = isSsl;
	}
	
	public boolean isUseLoginFromEmail() {
		return useLoginFromEmail;
	}
	
	public void setUseLoginFromEmail(boolean val) {
		useLoginFromEmail = val;
	}
	
	public boolean isDebugTraceEnabled() {
		return isDebugTraceEnabled;
	}
	
	public void setDebugTraceEnabled(boolean isDebugTraceEnabled) {
		this.isDebugTraceEnabled = isDebugTraceEnabled;
	}
	
	public long getSyncFreqSecs() {
		return syncFreqSecs;
	}
	
	public void setSyncFreqSecs(long syncFreqSecs) {
		this.syncFreqSecs = syncFreqSecs;
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

	@Override
	protected void reload() {
		DataSource ds, mailDs;
		try {
			mailDs = JspProvStub.getInstance().getOfflineDataSource(accountId);
			mailUsername = mailDs.getUsername();
			mailPassword = mailDs.getDecryptedPassword();
			ds = JspProvStub.getInstance().getOfflineCalendarDataSource(accountId);
		} catch (ServiceException x) {
			setError(x.getMessage());
			return;
		}
		
		accountName = ds.getName();
		email = ds.getUsername();
		password = JspConstants.MASKED_PASSWORD;
		displayName = ds.getFromDisplay();
		host = ds.getHost();
		port = ds.getPort().toString();
		isSsl = ds.getConnectionType() == DataSource.ConnectionType.ssl;
		isDebugTraceEnabled = ds.isDebugTraceEnabled();
		syncFreqSecs = ds.getTimeIntervalSecs(OfflineConstants.A_zimbraDataSourceSyncFreq, OfflineConstants.DEFAULT_SYNC_FREQ / 1000);
	}

	@Override protected void doRequest() {
		if (verb == null || !isAllOK())
			return;
		if (verb.isReset()) {
			try {
				JspProvStub stub = JspProvStub.getInstance();

				stub.resetOfflineDataSource(accountId);
			} catch (Throwable t) {
				setError(t.getMessage());
			}
	    }
	}
}
