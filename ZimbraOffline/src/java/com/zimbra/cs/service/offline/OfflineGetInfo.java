package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.account.GetInfo;

public class OfflineGetInfo extends GetInfo {
	@Override
	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		OfflineSyncManager.getInstance().setUiLoadingInProgress(true);
		
		return super.handle(request, context);
	}
	
	@Override
	protected Element encodeChildAccount(Element parent, Account child,
			boolean isVisible) {
		Element elem = super.encodeChildAccount(parent, child, isVisible);
        String accountName = child.getAttr(Provisioning.A_zimbraPrefLabel);
        accountName = accountName != null ? accountName : child.getAttr(OfflineConstants.A_offlineAccountName);
        if (elem != null && accountName != null) {
            Element attrsElem = elem.addUniqueElement(AccountConstants.E_ATTRS);
            attrsElem.addKeyValuePair(Provisioning.A_zimbraPrefLabel, accountName, AccountConstants.E_ATTR, AccountConstants.A_NAME);
        }
        return elem;
	}
}
