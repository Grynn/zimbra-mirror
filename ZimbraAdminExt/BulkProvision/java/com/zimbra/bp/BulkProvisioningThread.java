package com.zimbra.bp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
/**
 * @author Greg Solovyev
 */
public class BulkProvisioningThread extends Thread {
	private static Map<String,BulkProvisioningThread> mThreadCache = new HashMap<String,BulkProvisioningThread>();
	private static int MAX_PROVISIONING_THREADS = 2;
	public static int iSTATUS_IDLE = 0;
	public static int iSTATUS_STARTED = 1;
	public static int iSTATUS_CREATING_ACCOUNTS = 3;
	public static int iSTATUS_FINISHED = 4;
	public static int iSTATUS_ABORT = 5;
	public static int iSTATUS_ABORTED = 6;
	public static int iSTATUS_ERROR = 100;
	
	private List<Map<String, Object>> sourceAccounts;
	private int mStatus = 0;
	
	public int getStatus() {
		return mStatus;
	}

	public List<Map<String, Object>> getSourceAccounts() {
		return sourceAccounts;
	}

	public void abort() {
		mStatus = iSTATUS_ABORT;
	}
	
	@Override
	public void run() {
		mStatus = iSTATUS_STARTED;
		if(sourceAccounts == null) {
			mStatus = iSTATUS_ERROR;
			ZimbraLog.extensions.error("sourceAccounts map is empty", BulkProvisionException.BP_IMPORT_THREAD_NOT_INITIALIZED());
			return;
		}
		Provisioning prov = Provisioning.getInstance();
		for (Map<String, Object> entry : sourceAccounts) {
			if(mStatus == iSTATUS_ABORT) {
				ZimbraLog.extensions.warn("Warning! Aborting bulk provisioning impor thread.");
				mStatus = iSTATUS_ABORTED;
				return;
			}
			try {
				String accName = String.valueOf(entry.get(Provisioning.A_mail));
				String accPwd = String.valueOf(entry.get(Provisioning.A_userPassword));
				entry.remove(Provisioning.A_mail);
				entry.remove(Provisioning.A_userPassword);
				prov.createAccount(accName, accPwd, entry);
			} catch (Exception e) {
				mStatus = iSTATUS_ERROR;
				ZimbraLog.extensions.error("Failed to import account", e);
				return;				
			}
        }
	}

	public void setSourceAccounts(List<Map<String, Object>> sourceAccounts) {
		this.sourceAccounts = sourceAccounts;
	}

	public static BulkProvisioningThread getThreadInstance(String threadId, boolean createNew) throws BulkProvisionException {
		synchronized(mThreadCache) {
			if(mThreadCache.get(threadId) != null) {
				return mThreadCache.get(threadId);
			} else if(createNew) {
				if(mThreadCache.size()>=MAX_PROVISIONING_THREADS) {
					throw BulkProvisionException.BP_TOO_MANY_THREADS (MAX_PROVISIONING_THREADS);
				}
				BulkProvisioningThread thread =  new BulkProvisioningThread(threadId);
				mThreadCache.put(threadId, thread);
				return thread;
			} else {
				return null;
			}
		}
	}
	
	public static void deleteThreadInstance(String threadId) {
		synchronized(mThreadCache) {
			if(mThreadCache.get(threadId) != null) {
				if(mThreadCache.get(threadId).isAlive()) {
					mThreadCache.get(threadId).interrupt();
					mThreadCache.remove(threadId);
				}
			}
		}
	}
	
	private BulkProvisioningThread(String threadId) {
		sourceAccounts = null;
		mStatus = iSTATUS_IDLE;
	}
}
