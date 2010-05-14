package com.zimbra.bp;
/**
 * @author Greg Solovyev
 */
public class BulkProvisioningThread extends Thread {
	private static BulkProvisioningThread mInstance = null;
	
	public static BulkProvisioningThread getInstance() {
		if(mInstance == null) {
			mInstance = new BulkProvisioningThread();
		}
		return mInstance;
	}
}
