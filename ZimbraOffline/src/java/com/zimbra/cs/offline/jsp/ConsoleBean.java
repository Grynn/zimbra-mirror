package com.zimbra.cs.offline.jsp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.common.OfflineConstants.SyncStatus;

public class ConsoleBean extends PageBean {
	
	public static class AccountSummary {
		
		private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy 'at' h:mma");
		
		private boolean isZmail;
		private String id;
		private String name;
		private String email;
		private long lastSync;
		private SyncStatus status = SyncStatus.unknown;
		//private boolean isAutoSyncDisabled;

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
		
		public String getLastSync() {
			return lastSync == 0 ? "Never" : sdf.format(new Date(lastSync));
		}
		
		public SyncStatus getSyncStatus() {
			return status;
		}
		
		public boolean isStatusUnknown() {
			return status == SyncStatus.unknown;
		}
		
		public boolean isStatusOffline() {
			return status == SyncStatus.offline;
		}
		
		public boolean isStatusOnline() {
			return status == SyncStatus.online;
		}
		
		public boolean isStatusRunning() {
			return status == SyncStatus.running;
		}
		
		public boolean isStatusAuthFailed() {
			return status == SyncStatus.authfail;
		}
		
		public boolean isStatusError() {
			return status == SyncStatus.error;
		}
		
//		private boolean isAutoSyncDisabled() {
//			return isAutoSyncDisabled;
//		}
	}
	
	
	public ConsoleBean() {}
	
	public Collection<AccountSummary> getAccounts() throws ServiceException {
		List<AccountSummary> sums = new ArrayList<AccountSummary>();		
		List<Account> accounts = JspProvStub.getInstance().getOfflineAccounts();
		for (Account account : accounts) {
			AccountSummary sum = new AccountSummary();
			sum.isZmail = true;
			sum.id = account.getId();
			sum.name = account.getAttr(Provisioning.A_zimbraPrefLabel);
			sum.name = sum.name != null ? sum.name : account.getAttr(OfflineConstants.A_offlineAccountName); //TODO: remove this line
			sum.email = account.getName();
			
			sum.lastSync = account.getLongAttr(OfflineConstants.A_offlineLastSync, 0);
			String status = account.getAttr(OfflineConstants.A_offlineSyncStatus);
			sum.status = status == null ? SyncStatus.unknown : SyncStatus.valueOf(status);
			
			//sum.isAutoSyncDisabled = DateUtil.getTimeIntervalSecs(account.getAttr(OfflineConstants.A_offlineSyncFreq), OfflineConstants.DEFAULT_SYNC_FREQ / 1000) < 0;
			sums.add(sum);
		}
		List<DataSource> dataSources = JspProvStub.getInstance().getOfflineDataSources();
		for (DataSource ds : dataSources) {
			AccountSummary sum = new AccountSummary();
			sum.id = ds.getAccountId();
			sum.name = ds.getName();
			sum.email = ds.getEmailAddress();
			
			sum.lastSync = ds.getLongAttr(OfflineConstants.A_zimbraDataSourceLastSync, 0);
			String status = ds.getAttr(OfflineConstants.A_zimbraDataSourceSyncStatus);
			sum.status = status == null ? SyncStatus.unknown : SyncStatus.valueOf(status);
			
			//sum.isAutoSyncDisabled = DateUtil.getTimeIntervalSecs(ds.getAttr(OfflineConstants.A_zimbraDataSourceSyncFreq), OfflineConstants.DEFAULT_SYNC_FREQ / 1000) < 0;
			sums.add(sum);
		}
		return sums;
	}
	
}
