package com.zimbra.cs.offline;

import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.util.ZimbraApplication;

public class OfflineApplication extends ZimbraApplication {

	@Override
	public String getId() {
		return OfflineLC.zdesktop_app_id.value();
	}
	
	@Override
	public boolean supports(String className) {
		return false;
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
		try {
			DbPool.shutdown();
		} catch (Exception x) {
			OfflineLog.offline.warn("Exception during shutdown", x);
		}
	}
}
