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

public class XsyncBean extends MailBean {

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

                if (isAllOK()) {
                	dsAttrs.put(OfflineConstants.A_zimbraDataSourceAccountSetup, Provisioning.TRUE);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceEnabled, Provisioning.TRUE);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceUsername, username);
                    if (!password.equals(JspConstants.MASKED_PASSWORD))
                    	dsAttrs.put(Provisioning.A_zimbraDataSourcePassword, password);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceEmailAddress, email);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceHost, host);
                    dsAttrs.put(Provisioning.A_zimbraDataSourcePort, port);
                    dsAttrs.put(Provisioning.A_zimbraDataSourceConnectionType, connectionType.toString());
                    dsAttrs.put(Provisioning.A_zimbraDataSourceEnableTrace, isDebugTraceEnabled ? Provisioning.TRUE : Provisioning.FALSE);
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
                    stub.createOfflineDataSource(accountName, email, dsType, dsAttrs);
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
	
    public boolean isSmtpConfigSupported() {
        return false;
    }

    public boolean isUsernameRequired() {
        return false;
    }
}
