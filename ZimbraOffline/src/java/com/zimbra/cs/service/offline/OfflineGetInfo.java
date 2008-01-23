package com.zimbra.cs.service.offline;

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.account.GetInfo;

public class OfflineGetInfo extends GetInfo {

	@Override
	protected Element encodeChildAccount(Element parent, Account child,
			boolean isVisible) {
		Element elem = super.encodeChildAccount(parent, child, isVisible);
        String offlineAccountName = child.getAttr(OfflineConstants.A_offlineAccountName);
        if (elem != null && offlineAccountName != null) {
            Element attrsElem = elem.addUniqueElement(AccountConstants.E_ATTRS);
            attrsElem.addKeyValuePair(OfflineConstants.A_offlineAccountName, offlineAccountName, AccountConstants.E_ATTR, AccountConstants.A_NAME);
        }
        return elem;
	}
}
