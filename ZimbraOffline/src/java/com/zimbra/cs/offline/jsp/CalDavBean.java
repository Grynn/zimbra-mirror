package com.zimbra.cs.offline.jsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.DataSource.ConnectionType;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZFolder;


public class CalDavBean extends FormBean {
	
	public CalDavBean() {}
	
	protected String accountId;
	protected String name;
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
	protected boolean isLoaded;
	protected DataSource.Type type;
	
	public String getAccountId() {
		return accountId;
	}
	
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String n) {
		this.name = require(n);
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
		if (verb == null)
			return "";
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
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	public void setLoaded(boolean loaded) {
		isLoaded = loaded;
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
		isLoaded = false;
		try {
			mailDs = JspProvStub.getInstance().getOfflineDataSource(accountId);
			mailUsername = mailDs.getUsername();
			mailPassword = mailDs.getDecryptedPassword();
			ds = JspProvStub.getInstance().getOfflineCalendarDataSource(accountId);
		} catch (ServiceException x) {
			setError(x.getMessage());
			return;
		}
		
    	type = DataSource.Type.caldav;
		if (ds == null)
			return;
		name = ds.getName();
		email = ds.getUsername();
		password = JspConstants.MASKED_PASSWORD;
		displayName = ds.getFromDisplay();
		host = ds.getHost();
		port = ds.getPort().toString();
		isSsl = ds.getConnectionType() == DataSource.ConnectionType.ssl;
    	String attrs[] = ds.getMultiAttr(Provisioning.A_zimbraDataSourceAttribute);
    	for (String a : attrs) {
    		if (a.startsWith("p:")) {
    			principalPath = a.substring(2);
    			break;
    		}
    	}
		isDebugTraceEnabled = ds.isDebugTraceEnabled();
		syncFreqSecs = ds.getTimeIntervalSecs(OfflineConstants.A_zimbraDataSourceSyncFreq, OfflineConstants.DEFAULT_SYNC_FREQ / 1000);
		isLoaded = true;
	}

	@Override protected void doRequest() {
		if (verb == null) {
			reload();
			return;
		}
		if (!isAllOK())
			return;

		if (verb.isReset()) {
			// delete calendar ds
			return;
		}

		if (useLoginFromEmail) {
			try {
				DataSource mailDs = JspProvStub.getInstance().getOfflineDataSource(accountId);
				mailUsername = mailDs.getUsername();
				// we can't get the password via SoapProvisioning
				mailPassword = mailDs.getDecryptedPassword();
			} catch (ServiceException x) {
				setError(x.getMessage());
				return;
			}
			email = mailUsername;
			//password = mailPassword;
		}
		
		Map<String, Object> dsAttrs = new HashMap<String, Object>();
		
		if (isEmpty(email))
	    	addInvalid("email");
		if (isEmpty(password))
	    	addInvalid("password");
		if (isEmpty(principalUrl))
	    	addInvalid("url");

		String url = principalUrl;
		if (url.startsWith("http://")) {
			url = url.substring(7);
			port = "80";
			isSsl = false;
		} else if (url.startsWith("https://")) {
			url = url.substring(8);
			port = "443";
			isSsl = true;
		}
		int slash = url.indexOf('/');
		if (slash > 0) {
			principalPath = url.substring(slash);
			url = url.substring(0, slash);
		}
		int colon = url.indexOf(':');
		if (colon > 0) {
			port = url.substring(colon);
			url = url.substring(0, colon);
		}
		host = url;
		
		if (isEmpty(principalPath))
			addInvalid("url");
		
	    if (isAllOK()) {
	        dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, Provisioning.TRUE);
	        dsAttrs.put(Provisioning.A_zimbraDataSourceName, name);
	        dsAttrs.put(Provisioning.A_zimbraDataSourceUsername, email);
	        dsAttrs.put(Provisioning.A_zimbraPrefFromDisplay, displayName);
	        if (!password.equals(JspConstants.MASKED_PASSWORD)) {
	            dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, password);
	        }
	        
	        dsAttrs.put(Provisioning.A_zimbraDataSourceHost, host);
	        dsAttrs.put(Provisioning.A_zimbraDataSourcePort, port);
	        dsAttrs.put(Provisioning.A_zimbraDataSourceAttribute, "p:"+principalPath);
	        dsAttrs.put(Provisioning.A_zimbraDataSourceConnectionType, (isSsl ? ConnectionType.ssl : ConnectionType.cleartext).toString());
	        dsAttrs.put(Provisioning.A_zimbraDataSourceEnableTrace, isDebugTraceEnabled ? Provisioning.TRUE : Provisioning.FALSE);
	        
	        dsAttrs.put(OfflineConstants.A_zimbraDataSourceSyncFreq, Long.toString(syncFreqSecs));
            dsAttrs.put(Provisioning.A_zimbraDataSourceFolderId, ZFolder.ID_CALENDAR);

			try {
				JspProvStub stub = JspProvStub.getInstance();
				if (verb.isAdd()) {
					stub.createOfflineCalendarDataSource(accountId, dsAttrs);
				} else if (verb.isModify()) {
					stub.modifyOfflineDataSource(accountId, name, dsAttrs);
				} else {
					setError(getMessage("UnknownAct"));
				}
		    } catch (SoapFaultException x) {
		    	if (x.getCode().equals("account.AUTH_FAILED")) {
		    		setError(getMessage("InvalidUserOrPass"));
		    	} else if (!(verb != null && verb.isDelete() && x.getCode().equals("account.NO_SUCH_ACCOUNT"))) {
		    		setExceptionError(x);
		    	}
	        } catch (Throwable t) {
	            setError(t.getMessage());
			}
		}
	}
}
