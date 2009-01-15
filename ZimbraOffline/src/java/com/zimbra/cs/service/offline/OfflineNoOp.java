package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.service.mail.NoOp;
import com.zimbra.soap.SoapServlet;

public class OfflineNoOp extends NoOp {

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		if (!context.containsKey(SoapServlet.IS_RESUMED_REQUEST))
			OfflineSyncManager.getInstance().clientPing();
		
		return super.handle(request, context);
	}
}
