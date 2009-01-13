package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.soap.DocumentHandler;

public class OfflineClientEventNotify extends DocumentHandler {
	
	public static final String A_Event = "e";
	public static final String EVENT_UI_LOAD_BEGIN = "ui_load_begin";
	public static final String EVENT_UI_LOAD_END = "ui_load_end";

	@Override
	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {
		
		String event = request.getAttribute(A_Event);
		if (event.equals(EVENT_UI_LOAD_BEGIN))
			OfflineSyncManager.getInstance().setUiLoadingInProgress(true);
		else if (event.equals(EVENT_UI_LOAD_END))
			OfflineSyncManager.getInstance().setUiLoadingInProgress(false);
		else
			throw OfflineServiceException.UNKNOWN_CLIENT_EVENT(event); 
		
		return getZimbraSoapContext(context).createElement(OfflineService.CLIENT_EVENT_NOTIFY_RESPONSE);
	}
}
