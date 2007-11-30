package com.zimbra.cs.offline.jsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.DataSourceBy;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.offline.common.OfflineConstants;

public class JspProvStub {
    
    private SoapProvisioning prov;

    private JspProvStub() throws ServiceException {
    	prov = new SoapProvisioning();
        prov.soapSetURI(JspConstants.LOCALHOST_ADMIN_URL);
        prov.soapZimbraAdminAuthenticate();
    }
    
    public static JspProvStub getInstance() throws ServiceException {
    	return new JspProvStub();
    }

    public List<Account> getOfflineAccounts() throws ServiceException {
        List<Account> accounts = prov.getAllAccounts(null);
        for (Iterator<Account> i = accounts.iterator(); i.hasNext();) {
            if (i.next().getAttr(OfflineConstants.A_offlineRemoteServerUri, null) == null)
                i.remove();
        }
        return accounts;  
    }
    
    public List<DataSource> getOfflineDataSources() throws ServiceException {
        List<Account> accounts = prov.getAllAccounts(null);
        List<DataSource> dataSources = new ArrayList<DataSource>();
	    for (Account account : accounts) {
	    	String dsName = account.getAttr(OfflineConstants.A_offlineDataSourceName, null);
	        if (dsName != null) {
	            DataSource ds = prov.get(account, Provisioning.DataSourceBy.name, dsName);
	            if (ds != null)
	               dataSources.add(ds);
	        }
	    }
        return dataSources;    
    }
    
    public String getLoginAccountName() throws ServiceException {
        List<Account> accounts = prov.getAllAccounts(null);
        if (accounts.size() == 1)
        	return accounts.get(0).getName();
        return JspConstants.LOCAL_ACCOUNT;
    }
    
    public DataSource createOfflineDataSource(String dsName, String email, DataSource.Type dsType, Map<String, Object> dsAttrs)
    		throws ServiceException {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(OfflineConstants.A_offlineDataSourceName, dsName);
        Account account = prov.createAccount(email, JspConstants.DUMMY_PASSWORD, attrs);
        try {
        	return prov.createDataSource(account, dsType, dsName, dsAttrs);
        } catch (ServiceException e) {
        	prov.deleteAccount(account.getId());
        	throw e;
        }
    }
    
    public void modifyOfflineDataSource(String accountId, String dsName, Map<String, Object> dsAttrs) throws ServiceException {
    	Account account = prov.get(AccountBy.id, accountId);
    	DataSource ds = prov.get(account, DataSourceBy.name, dsName);
    	prov.modifyDataSource(account, ds.getId(), dsAttrs);
    }
    
    public void resetOfflineDataSource(String accountId) throws ServiceException {
        prov.deleteMailbox(accountId);
    }
    
    public void deleteOfflineDataSource(String accountId) throws ServiceException {
        prov.deleteMailbox(accountId);
        prov.deleteAccount(accountId);
    }
}
