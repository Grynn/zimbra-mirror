/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AttributeManager;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.DataSourceBy;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.offline.common.OfflineConstants;

public class JspProvStub {
    
    private SoapProvisioning prov;

    private JspProvStub() throws ServiceException {
    	prov = new SoapProvisioning();
        prov.soapSetURI(ConfigServlet.LOCALHOST_ADMIN_URL);
        prov.soapAdminAuthenticate(LC.zimbra_ldap_user.value(), LC.get("zdesktop_installation_key"));
    }
    
    public static JspProvStub getInstance() throws ServiceException {
        AttributeManager.setMinimize(true);
    	return new JspProvStub();
    }

    @SuppressWarnings("unchecked")
    public List<Account> getOfflineAccounts() throws ServiceException {
        List<Account> accounts = prov.getAllAccounts(null);
        for (Iterator<Account> i = accounts.iterator(); i.hasNext();) {
            if (i.next().getAttr(OfflineConstants.A_offlineRemoteServerUri, null) == null)
                i.remove();
        }
        return accounts;  
    }
    
    @SuppressWarnings("unchecked")
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
    
    @SuppressWarnings("unchecked")
    public String getLoginAccountName() throws ServiceException {
        List<Account> accounts = prov.getAllAccounts(null);
        if (accounts.size() > 0)
        	return JspConstants.LOCAL_ACCOUNT;
        return null;
    }
    
    public Account getOfflineAccount(String accountId) throws ServiceException {
    	return prov.get(AccountBy.id, accountId);
    }
    
    public Account getOfflineAccountByName(String name) throws ServiceException {
        return prov.get(AccountBy.name, name);
    }
    
    public Account createOfflineAccount(String accountName, String email, Map<String, Object> attrs)
    		throws ServiceException {
        attrs.put(Provisioning.A_zimbraPrefLabel, accountName);
        return prov.createAccount(email, JspConstants.DUMMY_PASSWORD, attrs);
    }
    
    public void modifyOfflineAccount(String accountId, Map<String, Object> attrs) throws ServiceException {
        Account account = prov.get(Provisioning.AccountBy.id, accountId);
        prov.modifyAttrs(account, attrs, true);
    }
    
    public void resetOfflineAccount(String accountId) throws ServiceException {
        prov.deleteMailbox(accountId);
    }
    
    public void deleteOfflineAccount(String accountId) throws ServiceException {
    	prov.deleteAccount(accountId);
    }
    
    public DataSource getOfflineDataSource(String accountId) throws ServiceException {
    	Account account = prov.get(AccountBy.id, accountId);
    	return prov.get(account, DataSourceBy.name, account.getAttr(OfflineConstants.A_offlineDataSourceName));
    }
    
    public DataSource getOfflineCalendarDataSource(String accountId) throws ServiceException {
    	Account account = prov.get(AccountBy.id, accountId);
    	String dsName = account.getAttr(OfflineConstants.A_offlineDataSourceName)+OfflineConstants.CALDAV_DS;
    	return prov.get(account, DataSourceBy.name, dsName);
    }
    
    public Account createOfflineDataSource(String dsName, String email, DataSource.Type dsType, Map<String, Object> dsAttrs)
    		throws ServiceException {
        dsAttrs.put(OfflineConstants.A_offlineDataSourceName, dsName);
        dsAttrs.put(OfflineConstants.A_offlineDataSourceType, dsType.toString());
        dsAttrs.put(Provisioning.A_zimbraPrefLabel, dsName);
        return prov.createAccount(email, JspConstants.DUMMY_PASSWORD, dsAttrs);
    }
    
    public void createOfflineCalendarDataSource(String accountId, Map<String, Object> dsAttrs)
    		throws ServiceException {
    	Account account = prov.get(AccountBy.id, accountId);
    	DataSource.Type dsType = DataSource.Type.caldav;
    	String name = account.getAttr(OfflineConstants.A_offlineDataSourceName) + OfflineConstants.CALDAV_DS;
    	dsAttrs.put(Provisioning.A_zimbraDataSourceName, name);
    	dsAttrs.put(OfflineConstants.A_offlineDataSourceName, name);
    	dsAttrs.put(OfflineConstants.A_offlineDataSourceType, dsType.toString());
    	dsAttrs.put(Provisioning.A_zimbraDataSourceImportClassName, DataSourceManager.getDefaultImportClass(dsType));
    	prov.createDataSource(account, dsType, name, dsAttrs);
	}
    
    public void modifyOfflineDataSource(String accountId, String acctName, Map<String, Object> dsAttrs) throws ServiceException {
    	Account account = prov.get(AccountBy.id, accountId);
    	String dsName = account.getAttr(OfflineConstants.A_offlineDataSourceName);
    	DataSource ds = prov.get(account, DataSourceBy.name, dsName);
    	Map<String, Object> attrs = new HashMap<String, Object>(1);
    	attrs.put(Provisioning.A_zimbraPrefLabel, acctName);
    	prov.modifyAttrs(account, attrs);
    	prov.modifyDataSource(account, ds.getId(), dsAttrs);
    }
    
    public void resetOfflineDataSource(String accountId) throws ServiceException {
        prov.deleteMailbox(accountId);
    }
    
    public void deleteOfflineDataSource(String accountId) throws ServiceException {
    	prov.deleteAccount(accountId);
    }
    
    public String[] promoteAccount(String accountId) throws ServiceException {
    	Map<String, Object> attrs = new HashMap<String, Object>(1);
    	attrs.put(OfflineConstants.A_offlineAccountsOrder, accountId); //server side will fill in the rest
    	Account account = prov.get(AccountBy.id, OfflineConstants.LOCAL_ACCOUNT_ID);
    	prov.modifyAttrs(account, attrs);
    	return getAccountsOrder();
    }
    
    public String[] getAccountsOrder() throws ServiceException {
    	Account account = prov.get(AccountBy.id, OfflineConstants.LOCAL_ACCOUNT_ID);
    	String orderStr = account.getAttr(OfflineConstants.A_offlineAccountsOrder, "");
    	return orderStr.length() > 0 ? orderStr.split(",") : new String[0];
    }
    
    public void reIndex(String accountId) throws ServiceException {
    	Account account = prov.get(AccountBy.id, accountId);
    	prov.reIndex(account, "start", null, null);
    }
}
