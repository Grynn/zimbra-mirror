package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;

public class OfflineClientEventNotify extends DocumentHandler {

	@Override
	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {
		
		String event = request.getAttribute(OfflineConstants.A_Event);
		if (event.equals(OfflineConstants.EVENT_UI_LOAD_BEGIN))
			OfflineSyncManager.getInstance().setUiLoadingInProgress(true);
		else if (event.equals(OfflineConstants.EVENT_UI_LOAD_END))
			OfflineSyncManager.getInstance().setUiLoadingInProgress(false);
		else
			throw OfflineServiceException.UNKNOWN_CLIENT_EVENT(event); 
		
		return getZimbraSoapContext(context).createElement(OfflineConstants.CLIENT_EVENT_NOTIFY_RESPONSE);
	}
	
	@Override
    public boolean needsAuth(Map<String, Object> context) {
        return false;
    }
	
	@Override
    public boolean needsAdminAuth(Map<String, Object> context) {
        return false;
    }
}
