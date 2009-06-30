package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;

public class ExchangeMailbox extends ChangeTrackingMailbox {
	
	
	
	
	public ExchangeMailbox(MailboxData data) throws ServiceException {
		super(data);
	}
	
	@Override
	void trackChangeNew(MailItem item) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	void trackChangeModified(MailItem item, int changeMask)
			throws ServiceException {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public boolean isAutoSyncDisabled() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void syncOnTimer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sync(boolean isOnRequest, boolean isDebugTraceOn) {
		try {
			syncFolders();
		} catch (ServiceException x) {
			
		}
		
	}


	private void syncFolders() throws ServiceException {
		
		
		
		
		
		
	}






}
