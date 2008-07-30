package com.zimbra.cs.offline.jsp;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.offline.common.OfflineConstants;

public class ZmailBean extends MailBean {
	
	public ZmailBean() {}
	
	@Override
	protected void reload() {
		Account account = null;
		try {
			account = JspProvStub.getInstance().getOfflineAccount(accountId);
		} catch (ServiceException x) {
			setError(x.getMessage());
			return;
		}
		
		accountName = account.getAttr(Provisioning.A_zimbraPrefLabel);
		accountName = accountName != null ? accountName : account.getAttr(OfflineConstants.A_offlineAccountName);
		email = account.getName();
		password = JspConstants.MASKED_PASSWORD;
		host = account.getAttr(JspConstants.OFFLINE_REMOTE_HOST);
		port = account.getAttr(JspConstants.OFFLINE_REMOTE_PORT);
		isSsl = account.getBooleanAttr(JspConstants.OFFLINE_REMOTE_SSL, false);
		syncFreqSecs = account.getTimeIntervalSecs(OfflineConstants.A_offlineSyncFreq, OfflineConstants.DEFAULT_SYNC_FREQ / 1000);
		isDebugTraceEnabled = account.getBooleanAttr(OfflineConstants.A_offlineEnableTrace, false);
	}
	
	@Override
	protected void doRequest() {
		if (verb == null || !isAllOK())
			return;
		
	    try {
			Map<String, Object> attrs = new HashMap<String, Object>();
			
			if (verb.isAdd() || verb.isModify()) {
				if (isEmpty(accountName))
					addInvalid("accountName");
				if (!isValidEmail(email))
			    	addInvalid("email");
				if (isEmpty(password))
			    	addInvalid("password");
				if (!isValidHost(host))
			    	addInvalid("host");
				if (!isEmpty(port) && !isValidPort(port))
			    	addInvalid("port");
                    
			    if (isAllOK()) {
			    	attrs.put(Provisioning.A_zimbraPrefLabel, accountName);
                    attrs.put(OfflineConstants.A_offlineRemoteServerUri, getRemoteServerUri());
                    attrs.put(OfflineConstants.A_offlineSyncFreq, Long.toString(syncFreqSecs));
                    attrs.put(OfflineConstants.A_offlineEnableTrace, isDebugTraceEnabled ? Provisioning.TRUE : Provisioning.FALSE);
			        if (!password.equals(JspConstants.MASKED_PASSWORD)) {
			            attrs.put(OfflineConstants.A_offlineRemotePassword, password);
			        }
			        
                    attrs.put(JspConstants.OFFLINE_REMOTE_HOST, host);
                    attrs.put(JspConstants.OFFLINE_REMOTE_PORT, port);
                    attrs.put(JspConstants.OFFLINE_REMOTE_SSL, isSsl ? Provisioning.TRUE : Provisioning.FALSE);
			    }
			}
			
			JspProvStub stub = JspProvStub.getInstance();
			if (isAllOK()) {                
			    if (verb.isAdd()) {
			        stub.createOfflineAccount(accountName, email, attrs);
			    } else {
			        if (isEmpty(accountId)) {
			            setError(getMessage("AccountIdMissing"));
					} else if (verb.isExport()) {
					} else if (verb.isImport()) {
			        } else if (verb.isModify()) {
			            stub.modifyOfflineAccount(accountId, attrs);
			        } else if (verb.isReset()) {
					    stub.resetOfflineAccount(accountId);
					} else if (verb.isDelete()) {
					    stub.deleteOfflineAccount(accountId);
					} else {
					    setError(getMessage("UnknownAct"));
	                }
	            }
			}
	    } catch (SoapFaultException x) {
	    	if (!(verb != null && verb.isDelete() && x.getCode().equals("account.NO_SUCH_ACCOUNT")))
	    		setExceptionError(x);
        } catch (Throwable t) {
            setError(t.getMessage());
        }
	}

	public boolean isDefaultPort() {
		if (isEmpty(port))
			return true;
		int iPort = Integer.parseInt(port);
		return (isSsl && iPort == 443) || (!isSsl && iPort == 80);
	}
	
	private String getRemoteServerUri() {
		return (isSsl ? "https://" : "http://") + host + (isDefaultPort() ? "" : ":" + port);
	}
}

