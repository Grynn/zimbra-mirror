package com.zimbra.cs.offline.jsp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.DateUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.offline.common.OfflineConstants;

public class ConsoleBean extends PageBean {
	
	public static class AccountSummary {
		
		private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy 'at' h:mma");
		
		private boolean isZmail;
		private String id;
		private String name;
		private String email;
		private long lastAccess;
		private String error;
		private boolean isAutoSyncDisabled;

		public boolean isZmail() {
			return isZmail;
		}
		
		public String getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public String getEmail() {
			return email;
		}
		
		public String getLastAccess() {
			return sdf.format(new Date(lastAccess));
		}
		
		public String getError() {
			return error;
		}
		
		public boolean isAutoSyncDisabled() {
			return isAutoSyncDisabled;
		}
	}
	
	
	public ConsoleBean() {}
	
	public Collection<AccountSummary> getAccounts() throws ServiceException {
		List<AccountSummary> sums = new ArrayList<AccountSummary>();		
		List<Account> accounts = JspProvStub.getInstance().getOfflineAccounts();
		for (Account account : accounts) {
			AccountSummary sum = new AccountSummary();
			sum.isZmail = true;
			sum.id = account.getId();
			sum.name = account.getName();
			sum.email = account.getName();
			sum.isAutoSyncDisabled = DateUtil.getTimeIntervalSecs(account.getAttr(OfflineConstants.A_offlineSyncFreq), OfflineConstants.DEFAULT_SYNC_FREQ / 1000) == -1;
			sums.add(sum);
		}
		List<DataSource> dataSources = JspProvStub.getInstance().getOfflineDataSources();
		for (DataSource ds : dataSources) {
			AccountSummary sum = new AccountSummary();
			sum.id = ds.getAccountId();
			sum.name = ds.getName();
			sum.email = ds.getEmailAddress();
			sum.isAutoSyncDisabled = DateUtil.getTimeIntervalSecs(ds.getAttr(OfflineConstants.A_zimbraDataSourceSyncFreq), OfflineConstants.DEFAULT_SYNC_FREQ / 1000) == -1;
			sums.add(sum);
		}
		return sums;
	}
	
}
